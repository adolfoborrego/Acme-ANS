<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>

		<jstl:if test="${acme:anyOf(_command, 'show|update')}">
			<acme:input-textbox code="customer.booking.list.label.flight" path="flight" />	
		</jstl:if>
		<acme:input-textbox code="customer.booking.list.label.lastNibble" path="lastNibble" />
		<acme:input-textbox code="customer.booking.list.label.locatorCode" path="locatorCode" />
	    <acme:input-money code="customer.booking.list.label.price" path="price" />
	    <acme:input-moment code="customer.booking.list.label.purchaseMoment" path="purchaseMoment"/>
	    <acme:input-textbox code="customer.booking.list.label.travelClass" path="travelClass" />
	    <acme:input-checkbox code="customer.booking.list.label.published" path="published" readonly="true"/>
	    	    

	<!-- Botón para ver los legs de este vuelo -->
    <jstl:if test="${_command == 'show' && numberOfLayovers != 0}">
   		 <acme:button code="customer.passenger.list" action="/customer/passenger/list?bookingId=${id}" />
   	</jstl:if>
   	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|publish') && !published}">
			<acme:input-textbox code="customer.booking.list.label.flight" path="flight" />
			<acme:submit code="customer.booking.update.submit" action="/customer/booking/update"/>
			<acme:submit code="customer.booking.publish" action="/customer/booking/publish" />	
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:input-select code="customer.booking.list.label.flight" path="flight" choices="${flights}" />
			<acme:submit code="customer.booking.create.submit" action="/customer/booking/create"/>
		</jstl:when>	
	</jstl:choose>	

</acme:form>>