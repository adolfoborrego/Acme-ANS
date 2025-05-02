<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<jstl:choose>

    <%-- ====================================================
         BLOQUE: Comando "create"
         ==================================================== --%>
    <jstl:when test="${_command == 'create-activityLog'}">
        <acme:form readonly="false">
            <!-- FlightAssignment info -->
            <acme:input-textbox readonly="true"
                code="activity-log.flight-assignment.info"
                path="flightAssignmentIdentificator" />
            <!-- Registration Moment: readonly, set in backend -->
            <acme:input-textbox readonly="true"
                code="activity-log.list.label.registrationMoment"
                path="registrationMoment" />

            <!-- Incident Type -->
            <acme:input-textbox readonly="false"
                code="activity-log.list.label.typeOfIncident"
                path="typeOfIncident" />

            <!-- Description -->
            <acme:input-textarea readonly="false"
                code="activity-log.list.label.description"
                path="description" />

            <!-- Severity Level -->
            <acme:input-textbox readonly="false"
                code="activity-log.list.label.severityLevel"
                path="severityLevel" />

            <!-- Create button -->
            <acme:submit code="activity-log.create"
                action="/flight-crew-member/activity-log/create-activityLog?id=${fid}" />
        </acme:form>
    </jstl:when>

    <%-- ====================================================
         BLOQUE: Comando "show"
         ==================================================== --%>
    <jstl:when test="${_command == 'show'}">
        <acme:form readonly="false">
            <!-- FlightAssignment info -->
            <acme:input-textbox readonly="true"
                code="activity-log.flight-assignment.info"
                path="flightAssignmentIdentificator" />

            <acme:input-textbox readonly="true"
                code="activity-log.list.label.registrationMoment"
                path="registrationMoment" />
            <jstl:if test="${!isPublished}">
			    <acme:input-textbox readonly="false"
			        code="activity-log.list.label.typeOfIncident"
			        path="typeOfIncident" />
			    <acme:input-textarea readonly="false"
			        code="activity-log.list.label.description"
			        path="description" />
			    <acme:input-textbox readonly="false"
			        code="activity-log.list.label.severityLevel"
			        path="severityLevel" />
			</jstl:if>
			
			<jstl:if test="${isPublished}">
			    <acme:input-textbox readonly="true"
			        code="activity-log.list.label.typeOfIncident"
			        path="typeOfIncident" />
			    <acme:input-textarea readonly="true"
			        code="activity-log.list.label.description"
			        path="description" />
			    <acme:input-textbox readonly="true"
			        code="activity-log.list.label.severityLevel"
			        path="severityLevel" />
			</jstl:if>


            <%-- Botones de acción: Update y Publish si no está publicado --%>
            <jstl:if test="${!isPublished}">
                <acme:submit code="activity-log.update"
                    action="/flight-crew-member/activity-log/update" />
                <acme:submit code="activity-log.publish"
                    action="/flight-crew-member/activity-log/publish" />
                <acme:submit code="activity-log.delete"
                action="/flight-crew-member/activity-log/delete" />
            </jstl:if>
        </acme:form>
    </jstl:when>

    <%-- ====================================================
         BLOQUE: Comando "update"
         ==================================================== --%>
    <jstl:when test="${_command == 'update'}">
        <acme:form readonly="false">
            <!-- FlightAssignment info -->
            <acme:input-textbox readonly="true"
                code="activity-log.flight-assignment.info"
                path="flightAssignment.identificator" />
            <acme:input-textbox readonly="true"
                code="activity-log.list.label.registrationMoment"
                path="registrationMoment" />
            <acme:input-textbox readonly="false"
                code="activity-log.list.label.typeOfIncident"
                path="typeOfIncident" />
            <acme:input-textarea readonly="false"
                code="activity-log.list.label.description"
                path="description" />
            <acme:input-textbox readonly="false"
                code="activity-log.list.label.severityLevel"
                path="severityLevel" />
            <!-- Update button -->
             <jstl:if test="${!isPublished}">
                <acme:submit code="activity-log.update"
                    action="/flight-crew-member/activity-log/update" />
                <acme:submit code="activity-log.publish"
                    action="/flight-crew-member/activity-log/publish" />
                <acme:submit code="activity-log.delete"
                action="/flight-crew-member/activity-log/delete" />
            </jstl:if>
        </acme:form>
    </jstl:when>

    <%-- ====================================================
         BLOQUE: Comando "publish"
         ==================================================== --%>
    <jstl:when test="${_command == 'publish'}">
        <acme:form readonly="false">
            <!-- FlightAssignment info -->
            <acme:input-textbox readonly="true"
                code="activity-log.flight-assignment.info"
                path="flightAssignment.identificator" />
            <acme:input-textbox readonly="true"
                code="activity-log.list.label.registrationMoment"
                path="registrationMoment" />
            <acme:input-textbox readonly="true"
                code="activity-log.list.label.typeOfIncident"
                path="typeOfIncident" />
            <acme:input-textarea readonly="true"
                code="activity-log.list.label.description"
                path="description" />
            <acme:input-textbox readonly="true"
                code="activity-log.list.label.severityLevel"
                path="severityLevel" />
            <!-- Publish button -->
             <jstl:if test="${!isPublished}">
                <acme:submit code="activity-log.update"
                    action="/flight-crew-member/activity-log/update" />
                <acme:submit code="activity-log.publish"
                    action="/flight-crew-member/activity-log/publish" />
                <acme:submit code="activity-log.delete"
                action="/flight-crew-member/activity-log/delete" />
            </jstl:if>
        </acme:form>
    </jstl:when>

</jstl:choose>

