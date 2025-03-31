<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
		
		
		<jstl:choose>
		
			<jstl:when test="${acme:anyOf(_command, 'create|publish') || !published}">
				<acme:input-select code="technician.maintenance-record.list.label.aircraft" path="aircraft" choices ="${aircrafts}"/>
	    		<acme:input-money code="technician.maintenance-record.list.label.estimatedCost" path="estimatedCost" />
	    		<acme:input-textarea code="technician.maintenance-record.list.label.notes" path="notes" /> 
	    		<acme:input-moment code="technician.maintenance-record.list.label.moment" path="moment"/>
	    		<acme:input-moment code="technician.maintenance-record.list.label.inspectionDueDate" path="inspectionDueDate"/>
	    		<acme:input-select code="technician.maintenance-record.list.label.currentStatus" path="currentStatus" choices = "${statusChoices}"/>
	    		<acme:input-checkbox code="technician.maintenance-record.list.label.published" path="published" readonly="true"/>
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
		<jstl:if test="${acme:anyOf(_command, 'show|publish')&& numberOfTasks == 0}">
			<acme:button code="technician.maintenance-record.create-first-task" action="/technician/task/create"/>
		</jstl:if>
	   <jstl:if test="${acme:anyOf(_command, 'show|publish')&& numberOfTasks != 0}">
			<acme:button code="technician.maintenance-record.list-tasks" action="/technician/task/list?maintenanceRecordId=${id}"/>
		</jstl:if>
	   <jstl:choose>
	   		<jstl:when test="${acme:anyOf(_command, 'show|update|publish') && !published}">
				<acme:submit code="technician.maintenanceRecord.update.submit" action="/technician/maintenance-record/update"/>
				<acme:submit code="technician.maintenanceRecord.publish" action="/technician/maintenance-record/publish" />
		</jstl:when>
	   		<jstl:when test="${_command == 'create'}">
				<acme:submit code="technician.maintenanceRecord.create.submit" action="/technician/maintenance-record/create"/>
			</jstl:when>
		</jstl:choose>
</acme:form>

	   