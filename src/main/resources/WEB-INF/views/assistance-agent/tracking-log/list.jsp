<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="assistance-agent.tracking-log.list.label.last-update-moment" path="lastUpdateMoment"/>
	<acme:list-column code="assistance-agent.tracking-log.list.label.step" path="step"/>
	<acme:list-column code="assistance-agent.tracking-log.list.label.resolution-percentage" path="resolutionPercentage"/>
	<acme:list-column code="assistance-agent.tracking-log.list.label.indicator" path="indicator"/>
	<acme:list-column code="assistance-agent.tracking-log.list.label.resolution" path="resolution"/>
	<acme:list-column code="assistance-agent.tracking-log.list.label.published" path="published"/>
	<acme:list-payload path="payload"/>	
</acme:list>
<acme:button code="assistance-agent.tracking-log.create" action="/assistance-agent/tracking-log/create?claimId=${claimId}"/>
