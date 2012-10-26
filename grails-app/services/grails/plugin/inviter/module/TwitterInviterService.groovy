package grails.plugin.inviter.module

import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH

import grails.converters.deep.JSON
import org.scribe.builder.ServiceBuilder
import org.scribe.builder.api.TwitterApi
import org.scribe.model.OAuthRequest
import org.scribe.model.Token
import org.scribe.model.Verb
import org.scribe.model.Verifier

class TwitterInviterService {

	static def authService
	static def useEmail = false
    def inviterOAuthService

	static def messageAttrs = [ 'message', 'contact', 'accessToken' ]

	def getAuthDetails(callbackUrl) {
		if (!authService) {

			authService = new ServiceBuilder()
							   .provider(TwitterApi.class)
							   .apiKey( CH.config.grails.plugin.inviter.twitter.key as String )
							   .apiSecret( CH.config.grails.plugin.inviter.twitter.secret as String )
							   .callback( callbackUrl as String )
							   .build();
			}

		Token requestToken = authService.getRequestToken();

		[ authUrl : authService.getAuthorizationUrl(requestToken), requestToken : requestToken ]

	}

	def getAccessToken( params, requestToken ){
		requestToken = requestToken as Token
		Verifier verifier = new Verifier( params.oauth_verifier )
		authService.getAccessToken(requestToken, verifier);
	}

	def getContacts(accessToken) {
		//def currentUser = sendRequest( accessToken, Verb.GET, "http://api.twitter.com/1/account/verify_credentials.json" )
        def currentUserResponse = inviterOAuthService.sendRequest(authService, accessToken, Verb.GET, "http://api.twitter.com/1/account/verify_credentials.json")
        def currentUser = JSON.parse( currentUserResponse )
        def currentUserId = currentUser.id
        def friendsResponse = inviterOAuthService.sendRequest(authService, accessToken, Verb.GET, "http://api.twitter.com/1/followers/ids.json?user_id=${ currentUserId }")
		//def friendsJson = sendRequest( accessToken, Verb.GET, "http://api.twitter.com/1/followers/ids.json?user_id=${ currentUserId }" )
        def friendsJson = JSON.parse( friendsResponse )
		def contacts = []
        def friends = friendsJson.ids
        
		// get friend details. 100 at a time due to twitter api limits
		partition( friends, 100 ).each{ friendList ->
            if (friendList) {
                def friendDetailsResponse = inviterOAuthService.sendRequest(authService, accessToken, Verb.GET, "http://api.twitter.com/1/users/lookup.json?user_id=${ friendList.join(',') }" )
                def friendDetails = JSON.parse( friendDetailsResponse )
                friendDetails.each{
                    def contact = [:]
                    contact.name = "${ it.name } (@${ it.screen_name })"
                    contact.photo = it.profile_image_url
                    contact.address = it.id
                    contacts << contact
                }
            }
		}
		contacts.sort { it.name.toLowerCase() }
	}

	def sendMessage = { attrs ->
		OAuthRequest request = new OAuthRequest( Verb.POST, 'http://api.twitter.com/1/direct_messages/new.json' )
		request.addBodyParameter( 'user_id', attrs.contact )
		request.addBodyParameter( 'text', ( attrs.message + " " + attrs.link?:'' ).trim() )
		authService.signRequest( attrs.accessToken, request )
		def response = JSON.parse( response )
		return response
	}
    /*
	private def sendRequest( accessToken, method, url ){
		OAuthRequest request = new OAuthRequest( method, url )
		authService.signRequest( accessToken, request )
		def response = request.send();
		return JSON.parse( response.body )
	}
    */

	private def partition(array, size) {
		def partitions = []
		int partitionCount = array.size() / size
		partitionCount.times { partitionNumber ->
			def start = partitionNumber * size
			def end = start + size - 1
			partitions << array[start..end]
		}

		if (array.size() % size) partitions << array[partitionCount * size..-1]
		return partitions
	}
}