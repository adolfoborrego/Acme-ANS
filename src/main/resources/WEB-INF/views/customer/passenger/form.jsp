<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>

		<acme:input-textbox code="customer.passenger.list.label.fullName" path="fullName" placeholder="max 256 chars. Ej: Alberto González"/>
		<acme:input-textbox code="customer.passenger.list.label.passportNumber" path="passportNumber" placeholder="between 6-9 chars. Ej: ABC6789"/>
		<acme:input-textarea code="customer.passenger.list.label.specialNeeds" path="specialNeeds" />
	    <acme:input-email code="customer.passenger.list.label.email" path="email" placeholder="Ej: albgonz@gmail.com"/>
	    <acme:input-moment code="customer.passenger.list.label.dateOfBirth" path="dateOfBirth"/>

	<jstl:choose>
	
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="customer.passenger.create.submit" action="/customer/passenger/create?bookingId=${id}"/>
		</jstl:when>
		<jstl:when test="${acme:anyOf(_command, 'show|update|publish') && !published}">
			<acme:submit code="customer.passenger.update" action="/customer/passenger/update?id=${id}" />	
			<acme:submit code="customer.passenger.publish" action="/customer/passenger/publish?id=${id}" />	
		</jstl:when>
	</jstl:choose>

</acme:form>>