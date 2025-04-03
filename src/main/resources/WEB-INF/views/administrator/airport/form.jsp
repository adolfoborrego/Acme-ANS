
<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
    <acme:input-textbox code="administrator.airport.form.label.name" path="name"/>
    <acme:input-textbox code="administrator.airport.form.label.iataCode" path="iataCode" placeholder = "administrator.airport.form.label.iataCode.placeholder"/>
    <acme:input-select code="administrator.airport.form.label.operationalScope" path="operationalScope" choices="${operationalScopes}"/>
    <acme:input-textbox code="administrator.airport.form.label.city" path="city" />
    <acme:input-textbox code="administrator.airport.form.label.country" path="country" />
    <acme:input-url code="administrator.airport.form.label.website" path="website" />
    <acme:input-email code="administrator.airport.form.label.emailAddress" path="emailAddress" />
    <acme:input-textbox code="administrator.airport.form.label.contactPhoneNumber" path="contactPhoneNumber" placeholder="+34 912 345 678"/>
    <acme:input-checkbox code="administrator.airport.form.label.confirmation" path="confirmation"/>
    
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update') && !published}">
			<acme:submit code="administrator.airport.form.button.update" action="/administrator/airport/update"/>
		</jstl:when>
		
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="administrator.airport.form.button.create" action="/administrator/airport/create"/>
		</jstl:when>		
	</jstl:choose>

</acme:form>
