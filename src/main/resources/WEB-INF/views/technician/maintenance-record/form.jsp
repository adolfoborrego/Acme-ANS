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
	   
	   
	   <jstl:if test="${acme:anyOf(_command, 'show|publish')&& numberOfTasks != 0}">
			<acme:button code="technician.maintenance-record.list-tasks" action="/technician/task/list?maintenanceRecordId=${id}"/>
		</jstl:if>
	   
	   
	   <jstl:choose>
	   		<jstl:when test="${acme:anyOf(_command, 'show|update|publish') && !published}">
				<acme:submit code="technician.maintenanceRecord.update.submit" action="/airline-manager/flight/update"/>
				<acme:submit code="technician.maintenanceRecord.publish" action="/airline-manager/flight/publish" />
		</jstl:when>
	   		<jstl:when test="${_command == 'create'}">
				<acme:submit code="technician.maintenanceRecord.create.submit" action="/technician/maintenance-record/create"/>
			</jstl:when>
		</jstl:choose>
</acme:form>