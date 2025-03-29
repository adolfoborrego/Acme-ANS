<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>

		<acme:input-textbox code="technician.maintenance-record.list.label.currentStatus" path="currentStatus" />
	    <acme:input-money code="technician.maintenance-record.list.label.estimatedCost" path="estimatedCost" />
	    <acme:input-textarea code="technician.maintenance-record.list.label.notes" path="notes" /> 
	    <acme:input-moment code="technician.maintenance-record.list.label.moment" path="moment" readonly="true"/>
	    <acme:input-moment code="technician.maintenance-record.list.label.inspectionDueDate" path="inspectionDueDate"/>
	    <acme:input-textbox code="technician.maintenance-record.list.label.aircraft" path="aircraft" />
	    <acme:input-checkbox code="technician.maintenance-record.list.label.published" path="published" />
	   
	   <acme:button code="technician.maintenance-record.list-tasks" action="/technician/task/list?maintenanceRecordId=${id}"/>
</acme:form>