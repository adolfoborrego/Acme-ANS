<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="administrator.aircraft.list.label.registrationNumber" path="registrationNumber"/>
    <acme:list-column code="administrator.aircraft.list.label.model" path="model"/>
    <acme:list-column code="administrator.aircraft.list.label.capacity" path="capacity"/>
    <acme:list-column code="administrator.aircraft.list.label.airline.iata" path="airline.iata"/>
    <acme:list-column code="administrator.aircraft.list.label.status" path="status"/>
</acme:list>

<jstl:if test="${showCreate}">
    <acme:button code="administrator.aircraft.list.button.create" action="/administrator/aircraft/create"/>
</jstl:if> 
