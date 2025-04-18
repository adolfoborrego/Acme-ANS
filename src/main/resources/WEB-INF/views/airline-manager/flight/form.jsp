<%--
- Pagina para los manager
--%>
<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
		<acme:input-textbox code="airline-manager.flight.list.label.tag" path="tag" />
	    <acme:input-money code="airline-manager.flight.list.label.cost" path="cost" />
	    <acme:input-textarea code="airline-manager.flight.list.label.description" path="description" />
	    <acme:input-checkbox code="airline-manager.flight.list.label.indicator" path="indicator" />

    <!-- Atributos derivados (solo aparecen en el show) -->
        <jstl:if test="${acme:anyOf(_command, 'show|update|delete|publish|create')}">
	        <acme:input-checkbox code="airline-manager.flight.list.label.published" path="published" readonly="true"/>
		    <acme:input-moment code="airline-manager.flight.list.label.sheduledDeparture" path="sheduledDeparture" readonly="true"/>
		    <acme:input-moment code="airline-manager.flight.list.label.sheduledArrival" path="sheduledArrival" readonly="true"/>
		    <acme:input-textbox code="airline-manager.flight.list.label.departureCity" path="departureCity" readonly="true"/>
		    <acme:input-textbox code="airline-manager.flight.list.label.arrivalCity" path="arrivalCity" readonly="true"/>
		    <acme:input-double code="airline-manager.flight.list.label.numberOfLayovers" path="numberOfLayovers" readonly="true"/>
       	</jstl:if>
    
    <!-- Bot�n para ver o crear escalas del vuelo -->
	<jstl:if test="${acme:anyOf(_command, 'show|publish|update')}">
		<jstl:choose>
			<jstl:when test="${published || numberOfLayovers > 0}">
				<acme:button code="airline-manager.leg.list" action="/airline-manager/leg/list?flightId=${id}" />
			</jstl:when>
			<jstl:when test="${!published && numberOfLayovers == 0}">
				<acme:button code="manager.leg.create.submit" action="/airline-manager/leg/create?flightId=${id}" />
			</jstl:when>
		</jstl:choose>
	</jstl:if>

	<!-- Botones de acci�n -->
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish') && !published}">
			<acme:submit code="manager.flight.update.submit" action="/airline-manager/flight/update"/>
			<acme:submit code="manager.flight.delete.submit" action="/airline-manager/flight/delete"/>
			<jstl:if test="${canPublish}">
				<acme:submit code="manager.flight.publish" action="/airline-manager/flight/publish" />
			</jstl:if>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="manager.flight.create.submit" action="/airline-manager/flight/create"/>
		</jstl:when>
	</jstl:choose>
    
</acme:form>
