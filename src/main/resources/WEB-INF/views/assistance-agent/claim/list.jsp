<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="assistance-agent.claim.list.label.registration-moment" path="registrationMoment" width="12%"/>
	<acme:list-column code="assistance-agent.claim.list.label.passenger-email" path="passengerEmail" width="18%"/>
	<acme:list-column code="assistance-agent.claim.list.label.description" path="description" width="25%"/>
	<acme:list-column code="assistance-agent.claim.list.label.type" path="type" width="10%"/>
	<acme:list-column code="assistance-agent.claim.list.label.leg" path="leg" width="10%"/>
	<acme:list-column code="assistance-agent.claim.list.label.published" path="published" width="10%"/>
	<acme:list-column code="assistance-agent.claim.list.label.indicator" path="indicator" width="10%"/>
	<acme:list-payload path="payload"/>	
</acme:list>
<acme:button code="assistance-agent.claim.create" action="/assistance-agent/claim/create"/>
