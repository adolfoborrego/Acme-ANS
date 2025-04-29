<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="customer.passenger-booking.list.label.passenger" path="passenger" />
    <acme:list-payload path="payload"/>	
</acme:list>