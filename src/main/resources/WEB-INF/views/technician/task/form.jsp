<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
		<jstl:choose>
		
			<jstl:when test="${acme:anyOf(_command, 'create|publish') || !published}">
	    		<acme:input-textbox code="technician.task.list.label.priority" path="priority" placeholder="technician.task.placeholder.priority"/>
	    		<acme:input-textbox code="technician.task.list.label.estimatedDuration" path="estimatedDuration" placeholder="technician.task.placeholder.estimatedDuration"/>
	    		<acme:input-select code="technician.task.list.label.type" path="type" choices = "${types}"/>  
	    		<acme:input-textarea code="technician.task.list.label.description" path="description" />
	    		<acme:input-checkbox code="technician.task.form.label.published" path="published" readonly="true"/>
			</jstl:when>
			<jstl:when test="${_command != 'create' && published}">	
				<acme:input-textbox code="technician.maintenance-record.list.label.aircraft" path="aircraft" readonly="true"/>
	    		<acme:input-money code="technician.maintenance-record.list.label.estimatedCost" path="estimatedCost" readonly="true"/>
	    		<acme:input-textarea code="technician.maintenance-record.list.label.notes" path="notes" readonly="true"/> 
	    		<acme:input-moment code="technician.maintenance-record.list.label.moment" path="moment" readonly="true"/>
	    		<acme:input-moment code="technician.maintenance-record.list.label.inspectionDueDate" path="inspectionDueDate" readonly="true"/>
	    		<acme:input-textbox code="technician.maintenance-record.list.label.currentStatus" path="currentStatus" readonly="true"/>
	    		<acme:input-checkbox code="technician.maintenance-record.list.label.published" path="published" readonly="true"/>
			</jstl:when>
		</jstl:choose>
	   <jstl:choose>
	   		<jstl:when test="${acme:anyOf(_command, 'show|update|publish|delete') && !published}">
				<acme:submit code="technician.task.update.submit" action="/technician/task/update"/>
				<acme:submit code = "technician.task.delete.submit" action ="/technician/task/delete"/>
				<acme:submit code="technician.task.publish" action="/technician/task/publish" />
		</jstl:when>
	   		<jstl:when test="${_command == 'create'}">
				<acme:submit code="technician.maintenanceRecord.create.submit" action="/technician/task/create?maintenanceRecordId=${maintenanceRecordId}"/>
			</jstl:when>
		</jstl:choose>
</acme:form>