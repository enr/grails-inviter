package grails.plugin.inviter

import grails.plugin.spock.UnitSpec

@TestFor(InviterTagLib)
class InviterTagLibSpec extends UnitSpec {
    def 'invitationLink tag'() {
        expect:
            applyTemplate(/<iv:invitationLink pick="${pick}" provider="${provider}">${body}<\/iv:invitationLink>/) == "<a href=\"/inviter/${action}?provider=${provider}\">${body}</a>"
        where:
            pick     | body              | provider     | action
            true     | 'The Face Book'   | 'facebook'   | 'pick'
            false    | 'The Face Book'   | 'facebook'   | 'invite'
            true     | 'The Big G'       | 'google'     | 'pick'
            false    | 'The Big G'       | 'google'     | 'invite'
            true     | 'The Whale'       | 'twitter'    | 'pick'
            false    | 'The Whale'       | 'twitter'    | 'invite'
            true     | 'Linked In'       | 'linkedin'   | 'pick'
            false    | 'Linked In'       | 'linkedin'   | 'invite'
    }

}