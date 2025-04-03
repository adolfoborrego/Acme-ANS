<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>

		<acme:input-select code="customer.passenger-booking.list.label.passenger" path="passenger" choices="${passengers}"/>

	<jstl:choose>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="customer.passenger-booking.create.submit" action="/customer/passenger-booking/create?bookingId=${bookingId}"/>
		</jstl:when>	
	</jstl:choose>

</acme:form>>