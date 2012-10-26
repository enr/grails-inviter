package grails.plugin.inviter.module

import org.scribe.model.Verb
import org.scribe.model.OAuthRequest

class InviterOAuthService {
    
	String sendRequest(authService, accessToken, method, url ){
		OAuthRequest request = new OAuthRequest( method, url )
		authService.signRequest( accessToken, request )
		def response = request.send();
		return response?.body ?: ''
	}

}
