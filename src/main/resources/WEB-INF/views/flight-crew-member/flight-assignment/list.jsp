<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
    <acme:list-column code="flight-crew-member.flight-assignment.list.label.role" path="duty" width="20%"/>
    <acme:list-column code="flight-crew-member.flight-assignment.list.label.lastUpdate" path="momentOfLastUpdate" width="20%"/>
    <acme:list-column code="flight-crew-member.flight-assignment.list.label.status" path="currentStatus" width="15%"/>

	<%-- Inicializamos la variable de control --%>
	<c:set var="showAddAssignment" value="true" />
	
	<%-- Iteramos sobre la colección 'duty' y comprobamos si existe "LEAD ATTENDANT" --%>
	<c:forEach var="role" items="${duty}">
	    <c:if test="${role eq 'LEAD ATTENDANT'}">
	        <c:set var="showAddAssignment" value="false" />
	    </c:if>
	</c:forEach>

</acme:list>

	<%-- Evaluamos la condición combinada para mostrar el botón según _command y showAddAssignment --%>
	<c:if test="${_command eq 'list-planned'}">
	    <acme:button code="flight-crew-member.flight-assignment.create" action="/flight-crew-member/flight-assignment/create"/>
	</c:if>

	<c:if test="${_command eq 'list-crews' and showAddAssignment}">
	    <acme:button code="flight-crew-member.flight-assignment.create" action="/flight-crew-member/flight-assignment/addFlightAssignment?Fid=${param.Fid}"/>
	</c:if>
