<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>

		<acme:input-textbox code="technician.task.list.label.type" path="type" />
	    <acme:input-textbox code="technician.task.list.label.estimatedDuration" path="estimatedDuration" /> 
	    <acme:input-textbox code="technician.task.list.label.priority" path="priority" /> 
	    <acme:input-textarea code="technician.task.list.label.description" path="description" />
	    <acme:input-checkbox code="technician.task.form.label.published" path="published" />
</acme:form>