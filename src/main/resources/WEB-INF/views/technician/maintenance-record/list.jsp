<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="technician.maintenance-record.list.label.aircraft" path="aircraft" />
	<acme:list-column code="technician.maintenance-record.list.label.currentStatus" path="currentStatus" />
	<acme:list-column code="technician.maintenance-record.list.label.estimatedCost" path="estimatedCost" />
	<acme:list-column code="technician.maintenance-record.list.label.inspectionDueDate" path="inspectionDueDate" />
	<acme:list-column code="technician.maintenance-record.list.label.notes" path="notes" />
    <acme:list-payload path="payload"/>
 
</acme:list>
<acme:button code="technician.maintenanceRecord.create" action="/technician/maintenance-record/create" />