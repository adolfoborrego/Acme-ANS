
<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>



	<acme:form>
	    <acme:input-textbox code="administrator.airline.form.label.name" path="name" placeholder = "administrator.airline.placeholder.name"/>
	    <acme:input-textbox code="administrator.airline.form.label.iataCode" path="iataCode" placeholder = "administrator.airline.placeholder.iataCode"/>
	    <acme:input-url code="administrator.airline.form.label.website" path="website"/>
	    <acme:input-select code="administrator.airline.form.label.type" path="type" choices="${typeChoices}"/>
	    <acme:input-moment code="administrator.airline.form.label.foundationTime" path="foundationTime"/>
	    <acme:input-email code="administrator.airline.form.label.email" path="email" />
	    <acme:input-textbox code="administrator.airline.form.label.phoneNumber" path="phoneNumber" placeholder = "administrator.airline.placeholder.phoneNumber"/>
			    
		
		<jstl:choose>
	   		<jstl:when test="${_command == 'create'}">
	   			<acme:input-checkbox code="administrator.airline.form.label.confirmation" path="confirmation"/>
				<acme:submit code="administrator.airline.create.submit" action="/administrator/airline/create"/>
			</jstl:when>
			<jstl:when test="${_command != 'create'}">
	   			<acme:input-checkbox code="administrator.airline.form.label.confirmation.update" path="confirmation"/>
	   			<acme:submit code="administrator.airline.update.submit" action="/administrator/airline/update"/>
			</jstl:when>
		</jstl:choose>
	</acme:form>

