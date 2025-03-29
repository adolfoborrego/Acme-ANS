
<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
    <acme:list-column code="administrator.airport.list.label.name" path="name"/>
    <acme:list-column code="administrator.airport.list.label.iataCode" path="iataCode"/>
    <acme:list-column code="administrator.airport.list.label.city" path="city"/>
    <acme:list-column code="administrator.airport.list.label.country" path="country"/>
    <acme:list-column code="administrator.airport.list.label.operationalScope" path="operationalScope"/>
</acme:list>
<jstl:if test="${showCreate}">
    <acme:button code="administrator.airport.list.button.create" action="/administrator/airport/create"/>
</jstl:if> 