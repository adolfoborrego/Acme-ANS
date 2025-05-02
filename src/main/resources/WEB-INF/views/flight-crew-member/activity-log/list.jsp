<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
    <!-- Columnas para ActivityLog -->
    <acme:list-column 
        code="activity-log.list.label.registrationMoment" 
        path="registrationMoment" 
        width="25%"/>
    <acme:list-column 
        code="activity-log.list.label.typeOfIncident" 
        path="typeOfIncident" 
        width="25%"/>
    <acme:list-column 
        code="activity-log.list.label.severityLevel" 
        path="severityLevel" 
        width="15%"/>

</acme:list>

<!-- Botón único para crear -->
<acme:button 
    code="activity-log.create" 
    action="/flight-crew-member/activity-log/create-activityLog?id=${param.id}" />

