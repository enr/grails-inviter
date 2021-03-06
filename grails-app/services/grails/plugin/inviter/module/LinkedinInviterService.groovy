package grails.plugin.inviter.module

import grails.converters.deep.XML
import org.scribe.builder.ServiceBuilder
import org.scribe.builder.api.LinkedInApi
import org.scribe.model.OAuthRequest
import org.scribe.model.Token
import org.scribe.model.Verb
import org.scribe.model.Verifier

class LinkedinInviterService {

	static def authService

	static def messageAttrs = [ 'accessToken', 'contact', 'subject', 'message' ]
    
	static def useEmail = false

    def inviterOAuthService
    
    def grailsApplication

	def getAuthDetails(callbackUrl) {
		if (!authService) {

			authService = new ServiceBuilder()
							   .provider(LinkedInApi.class)
							   .apiKey( grailsApplication.config.grails.plugin.inviter.linkedin.key as String )
							   .apiSecret( grailsApplication.config.grails.plugin.inviter.linkedin.secret as String )
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
        def response = inviterOAuthService.sendRequest(authService, accessToken, Verb.GET, "http://api.linkedin.com/v1/people/~/connections")
		def connections = XML.parse( response )

		def contacts = [ ]

		connections.person.each{

			def contact = [:]
			contact.name = "${it.'first-name'} ${it.'last-name'}"
			contact.address = it.id
			if( it.'picture-url' != '')
				contact.photo = it.'picture-url'
			contacts << contact

		}

		contacts.sort { it.name.toLowerCase() }

	}

	def sendMessage = { attrs->

		OAuthRequest request = new OAuthRequest( Verb.POST, 'http://api.linkedin.com/v1/people/~/mailbox' )
		String jsonLoad = """{
		  "recipients": {
			"values": [
			{
			  "person": {
				"_path": "/people/${attrs.contact}",
			   }
			}]
		  },
		  "subject": "${ attrs.subject }",
		  "body": "${ attrs.message + ' ' + attrs.link }"
		}"""

		request.addHeader("Content-Length", Integer.toString( jsonLoad.length()));
    	request.addHeader("Content-Type", "text/json");
    	request.addPayload(jsonLoad);

		authService.signRequest( attrs.accessToken, request )

		def response = request.send()
		return response.code

	}

    /*
	private def sendRequest( accessToken, method, url ){
		OAuthRequest request = new OAuthRequest( method, url )
		authService.signRequest( accessToken, request )
		def response = request.send();
		return XML.parse( response.body )
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