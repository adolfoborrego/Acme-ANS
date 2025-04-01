
<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>
 
<acme:form>

	    	<acme:input-textbox code="administrator.aircraft.list.label.model" path="model" placeholder = "administrator.aircraft.placeholder.model"/>
    		<acme:input-textbox code="administrator.aircraft.list.label.registrationNumber" path="registrationNumber" placeholder = "administrator.aircraft.placeholder.registrationNumber"/>
		    <acme:input-textbox code="administrator.aircraft.list.label.capacity" path="capacity" placeholder = "administrator.aircraft.placeholder.capacity"/>
		    <acme:input-textbox code="administrator.aircraft.form.label.cargoWeight" path="cargoWeight" placeholder = "administrator.aircraft.placeholder.cargoWeight"/>
		    <acme:input-select code="administrator.aircraft.list.label.status" path="status" choices = "${statusChoices}"/>
		    <acme:input-textbox code="administrator.aircraft.form.label.details" path="details" placeholder = "administrator.aircraft.placeholder.details"/>
		    <acme:input-select code="administrator.aircraft.list.label.airline.iata" path="airline" choices = "${choicesAirline}"/>
		    
		
		<jstl:choose>
	   		<jstl:when test="${_command == 'create'}">
	   			<acme:input-checkbox code="administrator.aircraft.form.label.confirmation" path="confirmation"/>
				<acme:submit code="administrator.aircraft.create.submit" action="/administrator/aircraft/create"/>
			</jstl:when>
			<jstl:when test="${_command != 'create'}">
	   			<acme:input-checkbox code="administrator.aircraft.form.label.confirmation.update" path="confirmation"/>
	   			<acme:submit code="administrator.aircraft.update.submit" action="/administrator/aircraft/update"/>
			</jstl:when>
		</jstl:choose>
</acme:form>

