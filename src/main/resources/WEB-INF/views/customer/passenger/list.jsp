<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="customer.passenger.list.label.fullName" path="fullName" />
	<acme:list-column code="customer.passenger.list.label.passportNumber" path="passportNumber" />
	<acme:list-column code="customer.passenger.list.label.specialNeeds" path="specialNeeds" />
	<acme:list-column code="customer.passenger.list.label.email" path="email" />
    <acme:list-payload path="payload"/>	
</acme:list>

