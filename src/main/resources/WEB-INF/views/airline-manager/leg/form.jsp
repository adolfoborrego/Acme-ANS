<%@page contentType="text/html;charset=UTF-8" %>
<%@taglib prefix="acme" uri="http://acme-framework.org/" %>

<acme:form>
    <acme:input-moment path="scheduledDeparture" code="leg.scheduledDeparture" readonly="true" />
    <acme:input-moment path="scheduledArrival" code="leg.scheduledArrival" readonly="true" />
    <acme:input-textbox path="status" code="leg.status" readonly="true" />
    
    <acme:input-textbox path="departureAirport" code="leg.departureAirport" readonly="true" />
    <acme:input-textbox path="arrivalAirport" code="leg.arrivalAirport" readonly="true" />
    
    <acme:input-textbox path="aircraft" code="leg.aircraft" readonly="true" />

    <acme:input-double path="duration" code="leg.duration" readonly="true" />
    <acme:input-textbox path="flightNumber" code="leg.flightNumber" readonly="true" />
</acme:form>
