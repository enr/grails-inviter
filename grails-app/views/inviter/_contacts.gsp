<div id="filter">
    <span class="filtertext"><g:message code="grails.plugin.inviter.contacts.search"/>&nbsp;</span><g:textField name="filterField" id="filterField" value=""></g:textField>
    <div id="selectionHelpers">
      <a id="showSelected" href="javascript:void(0);"><g:message code="grails.plugin.inviter.contacts.showSelected"/> (<span id="inv-s-num"></span>)</a> | 
      <a id="selectAll" href="javascript:void(0);"><g:message code="grails.plugin.inviter.contacts.selectAll"/></a> | 
      <a id="clearSelection" href="javascript:void(0);"><g:message code="grails.plugin.inviter.contacts.clearSelection"/></a>
    </div>
</div>

<div id="inviter-friends" class="friends">
    <g:each in="${ contacts }" var="contact">
        <div class="friend" data-email="${contact.address}">
            <g:if test="${contact.photo}">
                <img src="${contact.photo}"/>
            </g:if><g:else>
                <img src="${resource(dir: 'images', file: 'empty.png')}"/>
            </g:else>
            ${contact.name ? contact.name : contact.address}
        </div>
    </g:each>
</div>