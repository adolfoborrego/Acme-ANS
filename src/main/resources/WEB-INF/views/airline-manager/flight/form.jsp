<%--
- Pagina para los manager
--%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form readonly="true">
    <acme:input-textbox code="airline-manager.flight.list.label.tag" path="tag"/>
    <acme:input-textbox code="airline-manager.flight.list.label.indicator" path="indicator"/>
    <acme:input-money code="airline-manager.flight.list.label.cost" path="cost"/>
    <acme:input-textarea code="airline-manager.flight.list.label.description" path="description"/>
    <acme:input-textbox code="airline-manager.flight.list.label.published" path="published"/>

    <!-- Atributos derivados -->
    <acme:input-moment code="airline-manager.flight.list.label.sheduledDeparture" path="sheduledDeparture"/>
    <acme:input-moment code="airline-manager.flight.list.label.sheduledArrival" path="sheduledArrival"/>
    <acme:input-textbox code="airline-manager.flight.list.label.departureCity" path="departureCity"/>
    <acme:input-textbox code="airline-manager.flight.list.label.arrivalCity" path="arrivalCity"/>
    <acme:input-double code="airline-manager.flight.list.label.numberOfLayovers" path="numberOfLayovers"/>
    
    <!-- Bot�n para ver los legs de este vuelo -->
    <acme:button code="airline-manager.leg.list" action="/airline-manager/leg/list?flightId=${id}" />
</acme:form>
