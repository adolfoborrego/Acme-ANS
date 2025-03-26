<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
    <acme:input-moment path="scheduledDeparture" code="leg.scheduledDeparture"  />
    <acme:input-moment path="scheduledArrival" code="leg.scheduledArrival" />
    <acme:input-select path="status" code="leg.status" choices="${statuses}" />
    
    <acme:input-textbox path="departureAirport" code="leg.departureAirport"  />
    <acme:input-textbox path="arrivalAirport" code="leg.arrivalAirport" />
    
    <acme:input-textbox path="aircraft" code="leg.aircraft" />
    

    <!-- Atributos derivados (solo aparecen en el show) -->
    <jstl:if test="${acme:anyOf(_command, 'show|update|delete')}">
        <acme:input-checkbox path="published" code="leg.published" readonly="true"/>
    	<acme:input-double path="duration" code="leg.duration" readonly="true" />
    	<acme:input-textbox path="flightNumber" code="leg.flightNumber" readonly="true" />
    </jstl:if>
    
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete') && !published}">
			<acme:submit code="manager.leg.update.submit" action="/airline-manager/leg/update"/>
			<acme:submit code="manager.leg.delete.submit" action="/airline-manager/leg/delete"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="manager.leg.create.submit" action="/airline-manager/leg/create?flightId=${flightId}"/>
		</jstl:when>		
	</jstl:choose>	
    
</acme:form>
