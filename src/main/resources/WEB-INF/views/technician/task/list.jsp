<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list navigable = "false">
<acme:list-column code="technician.task.list.label.id" path="id" />
	<acme:list-column code="technician.task.list.label.type" path="type" />
	<acme:list-column code="technician.task.list.label.estimatedDuration" path="estimatedDuration" />
	<acme:list-column code="technician.task.list.label.priority" path="priority" />
	<acme:list-column code="technician.task.list.label.description" path="description" />
    <acme:list-payload path="payload"/>	
</acme:list>