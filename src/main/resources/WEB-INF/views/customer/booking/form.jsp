<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>

		<acme:input-textbox code="customer.booking.list.label.flight" path="flight" />
		<acme:input-textbox code="customer.booking.list.label.lastNibble" path="lastNibble" />
		<acme:input-textbox code="customer.booking.list.label.locatorCode" path="locatorCode" />
	    <acme:input-money code="customer.booking.list.label.price" path="price" />
	    <acme:input-moment code="customer.booking.list.label.purchaseMoment" path="purchaseMoment" readonly="true"/>
	    <acme:input-textbox code="customer.booking.list.label.travelClass" path="travelClass" />	    

	<!-- Botón para ver los legs de este vuelo -->
    <jstl:if test="${_command == 'show' && numberOfLayovers != 0}">
   		 <acme:button code="customer.passenger.list" action="/customer/passenger/list?bookingId=${id}" />
   	</jstl:if>

</acme:form>>