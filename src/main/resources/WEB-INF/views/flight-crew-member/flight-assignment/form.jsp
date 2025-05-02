<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<jstl:choose>

    <%-- ====================================================
         BLOQUE: Comando "show"
         ==================================================== --%>
    <jstl:when test="${_command == 'show'}">
        <acme:form readonly="false">

            <%-- Campo Role: Se muestra de solo lectura --%>
            <acme:input-textbox readonly="true"
                code="flight-crew-member.flight-assignment.list.label.role"
                path="duty" />

            <%-- Campos de solo lectura para lastUpdate y status --%>
            <acme:input-textbox readonly="true"
                code="flight-crew-member.flight-assignment.list.label.lastUpdate"
                path="momentOfLastUpdate" />
            <acme:input-textbox readonly="true"
                code="flight-crew-member.flight-assignment.list.label.status"
                path="currentStatus" />

            <%-- Campo Remarks y Leg, según la comparación de Fid e id --%>
            <jstl:if test="${Fid != id}">
                <acme:input-textbox readonly="true"
                    code="flight-crew-member.flight-assignment.list.label.remarks"
                    path="remarks" />

                <%-- Campo Leg: Se muestra como de solo lectura si Fid no es igual a id --%>
                <acme:input-select readonly="${Fid != id}"
                    code="flight-crew-member.flight-assignment.list.label.leg"
                    path="leg" choices="${possibleLegs}" />
            </jstl:if>

            <jstl:if test="${Fid == id}">
                <acme:input-textbox readonly="${!currentStatus == 'PENDING'}"
                    code="flight-crew-member.flight-assignment.list.label.remarks"
                    path="remarks" />
                <%-- Campo Leg con condición sobre isLegChangeable e isSupLA --%>
                <acme:input-select readonly="${!isLegChangeable and !isSupLA}"
                    code="flight-crew-member.flight-assignment.list.label.leg"
                    path="leg" choices="${possibleLegs}" />
            </jstl:if>

            <%-- Campo Lead Attendant solo se muestra si duty no es 'LEAD ATTENDANT' y isSupla es false --%>
            <jstl:if test="${duty != 'LEAD ATTENDANT' and isSupla == false}">
                <acme:input-textbox readonly="true"
                    code="flight-crew-member.flight-assignment.list.label.leadAttendant"
                    path="lead_attendant" />
            </jstl:if>

            <%-- Botones de acción: Solo se muestran si currentStatus es PENDING --%>
            <jstl:if test="${currentStatus eq 'PENDING'}">
                <jstl:if test="${leg.published and leg.status != 'LANDED'}">
                    <jstl:if test="${Fid == id}">
                        <acme:submit code="flight-crew-member.flight-assignment.update"
                            action="/flight-crew-member/flight-assignment/update" />
                        <acme:submit code="flight-crew-member.flight-assignment.delete"
                            action="/flight-crew-member/flight-assignment/delete" />
                        <acme:submit code="flight-crew-member.flight-assignment.publish"
                            action="/flight-crew-member/flight-assignment/publish" />
                    </jstl:if>
                    <jstl:if test="${Fid != id and isSupLA}">
                        <acme:submit code="flight-crew-member.flight-assignment.delete"
                            action="/flight-crew-member/flight-assignment/delete" />                  
                    </jstl:if>
                </jstl:if>
            </jstl:if>

            <%-- Botón para listar crews dependiendo de currentStatus y condiciones adicionales --%>
            <jstl:if test="${Fid == id and currentStatus eq 'CONFIRMED'}">
                <jstl:if test="${!isSupLA}">
                    <acme:button code="flight-crew-member.flight-assignment.listCrews"
                        action="/flight-crew-member/flight-assignment/list-crews?Fid=${Fid}" />
                </jstl:if>
                <jstl:if test="${isSupLA}">
                    <acme:button code="flight-crew-member.flight-assignment.listCrews"
                        action="/flight-crew-member/flight-assignment/list-LeadAttendantCrews?Fid=${Fid}" />
                </jstl:if>
            </jstl:if>
            
            <jstl:if test="${ leg.status == 'LANDED'}">
            	<acme:button code="flight-crew-member.flight-assignment.activityLog"
                        action="/flight-crew-member/activity-log/list?id=${Fid}" />
            </jstl:if>

        </acme:form>
    </jstl:when>


    <%-- ====================================================
         BLOQUE: Comando "addFlightAssignment"
         ==================================================== --%>
    <jstl:when test="${_command == 'addFlightAssignment'}">
        <acme:form readonly="false">

            <%-- Campo Role editable: Selector con opciones de duty --%>
            <acme:input-select readonly="false"
                code="flight-crew-member.flight-assignment.list.label.role"
                path="duty" choices="${duty}" />

            <%-- Campo de selección de Crews específico para addFlightAssignment --%>
            <acme:input-select readonly="false"
                code="flight-crew-member.flight-assignment.list.label.crews"
                path="memberId" choices="${possibleCrews}" />

            <%-- Campo Leg: Se muestra en modo solo lectura --%>
            <acme:input-select readonly="true"
                code="flight-crew-member.flight-assignment.list.label.leg"
                path="leg" choices="${possibleLegs}"/>

            <%-- Botón para crear asignación de vuelo en addFlightAssignment --%>
            <acme:submit code="flight-crew-member.flight-assignment.create"
                action="/flight-crew-member/flight-assignment/addFlightAssignment?Fid=${param.Fid}" />

        </acme:form>
    </jstl:when>


    <%-- ====================================================
         BLOQUE: Comando "create"
         ==================================================== --%>
    <jstl:when test="${_command == 'create'}">
        <acme:form readonly="false">

            <%-- Campo Role: Selector de duty en modo de solo lectura --%>
            <acme:input-select readonly="true"
                code="flight-crew-member.flight-assignment.list.label.role"
                path="duty" choices="${duty}" />

            <%-- Campo Leg: Selector de leg --%>
            <acme:input-select
                code="flight-crew-member.flight-assignment.list.label.leg"
                path="leg" choices="${possibleLegs}" />

            <%-- Campo Remarks: Editable --%>
            <acme:input-textbox readonly="false"
                code="flight-crew-member.flight-assignment.list.label.remarks"
                path="remarks" />

            <%-- Botón para crear asignación --%>
            <acme:submit code="flight-crew-member.flight-assignment.create"
                action="/flight-crew-member/flight-assignment/create" />

        </acme:form>
    </jstl:when>


    <%-- ====================================================
         BLOQUE: Otros Comandos (p.ej., update)
         ==================================================== --%>
    <jstl:otherwise>
        <acme:form readonly="false">

            <%-- Campo Role: Se muestra de solo lectura --%>
            <acme:input-textbox readonly="true"
                code="flight-crew-member.flight-assignment.list.label.role"
                path="duty" />

            <%-- Campos de solo lectura para lastUpdate y status --%>
            <acme:input-textbox readonly="true"
                code="flight-crew-member.flight-assignment.list.label.lastUpdate"
                path="momentOfLastUpdate" />
            <acme:input-textbox readonly="true"
                code="flight-crew-member.flight-assignment.list.label.status"
                path="currentStatus" />

            <%-- Campo Remarks y Leg, según la comparación de Fid e id --%>
            <jstl:if test="${Fid != id}">
                <acme:input-textbox readonly="true"
                    code="flight-crew-member.flight-assignment.list.label.remarks"
                    path="remarks" />

                <%-- Campo Leg: Se muestra como de solo lectura si Fid no es igual a id --%>
                <acme:input-select readonly="${Fid != id}"
                    code="flight-crew-member.flight-assignment.list.label.leg"
                    path="leg" choices="${possibleLegs}" />
            </jstl:if>

            <jstl:if test="${Fid == id}">
                <acme:input-textbox readonly="${!currentStatus == 'PENDING'}"
                    code="flight-crew-member.flight-assignment.list.label.remarks"
                    path="remarks" />
                <%-- Campo Leg con condición sobre isLegChangeable e isSupLA --%>
                <acme:input-select readonly="${!isLegChangeable and !isSupLA}"
                    code="flight-crew-member.flight-assignment.list.label.leg"
                    path="leg" choices="${possibleLegs}" />
            </jstl:if>

            <%-- Campo Lead Attendant solo se muestra si duty no es 'LEAD ATTENDANT' y isSupla es false --%>
            <jstl:if test="${duty != 'LEAD ATTENDANT' and isSupla == false}">
                <acme:input-textbox readonly="true"
                    code="flight-crew-member.flight-assignment.list.label.leadAttendant"
                    path="lead_attendant" />
            </jstl:if>

            <%-- Botones de acción: Solo se muestran si currentStatus es PENDING --%>
            <jstl:if test="${currentStatus eq 'PENDING'}">
                <jstl:if test="${leg.published and leg.status != 'LANDED'}">
                    <jstl:if test="${Fid == id}">
                        <acme:submit code="flight-crew-member.flight-assignment.update"
                            action="/flight-crew-member/flight-assignment/update" />
                        <acme:submit code="flight-crew-member.flight-assignment.delete"
                            action="/flight-crew-member/flight-assignment/delete" />
                        <acme:submit code="flight-crew-member.flight-assignment.publish"
                            action="/flight-crew-member/flight-assignment/publish" />
                    </jstl:if>
                    <jstl:if test="${Fid != id and isSupLA}">
                        <acme:submit code="flight-crew-member.flight-assignment.delete"
                            action="/flight-crew-member/flight-assignment/delete" />                  
                    </jstl:if>
                </jstl:if>
            </jstl:if>

            <%-- Botón para listar crews dependiendo de currentStatus y condiciones adicionales --%>
            <jstl:if test="${Fid == id and currentStatus eq 'CONFIRMED'}">
                <jstl:if test="${!isSupLA}">
                    <acme:button code="flight-crew-member.flight-assignment.listCrews"
                        action="/flight-crew-member/flight-assignment/list-crews?Fid=${Fid}" />
                </jstl:if>
                <jstl:if test="${isSupLA}">
                    <acme:button code="flight-crew-member.flight-assignment.listCrews"
                        action="/flight-crew-member/flight-assignment/list-LeadAttendantCrews?Fid=${Fid}" />
                </jstl:if>
            </jstl:if>

        </acme:form>
    </jstl:otherwise>

</jstl:choose>
