<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-moment code="assistance-agent.form.label.registration-moment" path="registrationMoment"/>
	<acme:input-textbox code="assistance-agent.form.label.passenger-email" path="passengerEmail"/>
	<acme:input-textarea code="assistance-agent.form.label.description" path="description"/>
	<acme:input-textbox code="assistance-agent.form.label.type" path="type"/>
	<acme:input-checkbox code="assistance-agent.form.label.published" path="published"/>
</acme:form>
	