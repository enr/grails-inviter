package grails.plugin.inviter.module

import grails.plugin.spock.IntegrationSpec

import spock.lang.*

class FacebookInviterServiceIntegrationSpec extends IntegrationSpec {
  
    FacebookInviterService service
    
    def inviterOAuthService
    
    def setup() {
        service = new FacebookInviterService()
        inviterOAuthService = [:]
    }

    def "should not break for unexpected response"() {
        given:
            inviterOAuthService.sendRequest = { authService, accessToken, method, url ->
                return '{"o"="3"}'
            }
            service.inviterOAuthService = inviterOAuthService
        when:
            def contacts = service.getContacts( 'accessToken' )
        then:
            contacts.size() == 0
    }
    
    def "should return ordered contacts list"() {
        given:
            inviterOAuthService.sendRequest = { authService, accessToken, method, url ->
                return new File('./test/data/fb_getContacts_01.json').text
            }
            service.inviterOAuthService = inviterOAuthService
        when:
            def contacts = service.getContacts( 'accessToken' )
        then:
            contacts.size() == 5
            contacts[0].name == 'Alex Another'
            contacts[0].address == '449000089'
            contacts[0].photo == 'http://graph.facebook.com/449000089/picture'
            contacts[4].name == 'Steven Hawks'
            contacts[4].address == '665568925'
            contacts[4].photo == 'http://graph.facebook.com/665568925/picture'
    }
}