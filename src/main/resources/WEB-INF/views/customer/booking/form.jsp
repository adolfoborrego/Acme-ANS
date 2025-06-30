<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>

		<jstl:if test="${acme:anyOf(_command, 'show|update|publish')}">
			<acme:input-select code="customer.booking.list.label.flight" path="flight" choices="${flights}"/>	
		</jstl:if>
		<acme:input-textbox code="customer.booking.list.label.lastNibble" path="lastNibble" placeholder="max 4 numbers. Ej: 1234" />
		<acme:input-textbox code="customer.booking.list.label.locatorCode" path="locatorCode" placeholder="between 6-8 chars. Ej: ABC6789"/>
	    <acme:input-money code="customer.booking.list.label.price" path="price" />
	    <acme:input-select code="customer.booking.list.label.travelClass" path="travelClass" choices= "${travelClasses}"/>
	    <jstl:if test="${numberOfLayovers > 0}">
				<acme:button code="customer.passenger.list" action="/customer/passenger/list?id=${id}" />
		</jstl:if>

    <jstl:if test="${acme:anyOf(_command, 'show|publish|update')}">
			<jstl:if test="${!published}">
				<acme:button code="customer.passenger-booking.create" action="/customer/passenger-booking/create?bookingId=${id}" />
			</jstl:if>
   	</jstl:if>
   	<jstl:choose>	
		<jstl:when test="${acme:anyOf(_command, 'show|update|publish') && !published}">
			<acme:submit code="customer.booking.update.submit" action="/customer/booking/update?id=${id}"/>
			<acme:submit code="customer.booking.publish" action="/customer/booking/publish?id=${id}" />	
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:input-select code="customer.booking.list.label.flight" path="flight" choices="${flights}" />
			<acme:submit code="customer.booking.create.submit" action="/customer/booking/create"/>
		</jstl:when>	
	</jstl:choose>	

</acme:form>>