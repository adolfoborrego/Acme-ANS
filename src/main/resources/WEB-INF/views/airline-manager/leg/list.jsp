<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
    <acme:list-column path="scheduledDeparture" code="leg.scheduledDeparture" />
    <acme:list-column path="scheduledArrival" code="leg.scheduledArrival" />
    <acme:list-column path="departureAirport" code="leg.departureAirport" />
    <acme:list-column path="arrivalAirport" code="leg.arrivalAirport" />
    <acme:list-column path="status" code="leg.status" />
</acme:list>
<jstl:if test="${showCreate}">
	<acme:button code="manager.leg.create.submit" action="/airline-manager/leg/create?id=${id}" />
</jstl:if>