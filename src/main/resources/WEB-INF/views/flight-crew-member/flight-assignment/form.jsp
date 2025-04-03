<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>


<jstl:choose>
    <%-- Bloque para el comando "show" --%>
    <jstl:when test="${_command == 'show'}">
        <acme:form readonly="false">
            <%-- Campo Role (en show, se muestra como de solo lectura) --%>
            <acme:input-select readonly="true"
                code="flight-crew-member.flight-assignment.list.label.role"
                path="duty" choices="${duty}" />

            <%-- Campos solo en show --%>
            <acme:input-textbox readonly="true"
                code="flight-crew-member.flight-assignment.list.label.lastUpdate"
                path="momentOfLastUpdate" />
            <acme:input-textbox readonly="true"
                code="flight-crew-member.flight-assignment.list.label.status"
                path="currentStatus" />


            <%-- Campo Remarks, diferenciando según Fid e id --%>
            <jstl:if test="${Fid != id}">
                <acme:input-textbox readonly="true"
                    code="flight-crew-member.flight-assignment.list.label.remarks"
                    path="remarks" />
                    <%-- Campo Leg --%>
	            <acme:input-select readonly="true"
	                code="flight-crew-member.flight-assignment.list.label.leg"
	                path="leg" choices="${possibleLegs}" />
            </jstl:if>
            <jstl:if test="${Fid == id}">
                <acme:input-textbox readonly="false"
                    code="flight-crew-member.flight-assignment.list.label.remarks"
                    path="remarks" />
                <%-- Campo Leg --%>
	            <acme:input-select readonly="true"
	                code="flight-crew-member.flight-assignment.list.label.leg"
	                path="leg" choices="${possibleLegs}" />
            </jstl:if>

            <%-- Campo Lead Attendant si duty no es 'LEAD ATTENDANT' --%>
            <jstl:if test="${duty != 'LEAD ATTENDANT' and isSupla == false}">
                <acme:input-textbox readonly="true"
                    code="flight-crew-member.flight-assignment.list.label.leadAttendant"
                    path="lead_attendant" />
            </jstl:if>

            <%-- Botones de acción (solo si el vuelo aún no está publicado/aterrizado y Fid coincide) --%>
            <jstl:if test="${currentStatus eq 'PENDING'}">
            	<jstl:if test="${!leg.published and leg.status != 'LANDED'}">
            		<jstl:if test="${Fid == id}">
	            		<acme:submit code="flight-crew-member.flight-assignment.update"
	                    			action="/flight-crew-member/flight-assignment/update"/>
	            		<acme:submit code="flight-crew-member.flight-assignment.delete"
	                    			action="/flight-crew-member/flight-assignment/delete"/>
	                	<acme:submit code="flight-crew-member.flight-assignment.publish"
	                   				 action="/flight-crew-member/flight-assignment/publish"/>
                   	</jstl:if>
                   	<jstl:if test="${Fid != id and isSupLA}">
	            		<acme:submit code="flight-crew-member.flight-assignment.delete"
	                    			action="/flight-crew-member/flight-assignment/delete"/>
                   	</jstl:if>
            	</jstl:if>
            </jstl:if>
            <jstl:if test="${Fid == id}">
            <acme:button code="flight-crew-member.flight-assignment.listCrews"
                   			 action="/flight-crew-member/flight-assignment/list-crews?Fid=${Fid}"/>
        	</jstl:if>
        </acme:form>
    </jstl:when>

    <%-- Bloque para el comando "addFlightAssignment" --%>
    <jstl:when test="${_command == 'addFlightAssignment'}">
        <acme:form readonly="false">
            <%-- Campo Role (editable en este comando) --%>
            <acme:input-select readonly="false"
                code="flight-crew-member.flight-assignment.list.label.role"
                path="duty" choices="${duty}" />

            <%-- Campo de selección de Crews, específico de addFlightAssignment --%>
            <acme:input-select readonly="false"
                code="flight-crew-member.flight-assignment.list.label.crews"
                path="memberId" choices="${possibleCrews}" />

            <%-- Campo Leg --%>
            <acme:input-select readonly="true"
                code="flight-crew-member.flight-assignment.list.label.leg"
                path="leg" choices="${possibleLegs}" />

            <%-- Botón para crear asignación de vuelo en addFlightAssignment --%>
            <acme:submit code="flight-crew-member.flight-assignment.create"
                action="/flight-crew-member/flight-assignment/addFlightAssignment?Fid=${param.Fid}"/>
        </acme:form>
    </jstl:when>

    <%-- Bloque para el comando "create" --%>
    <jstl:when test="${_command == 'create'}">
        <acme:form readonly="false">
            <%-- Campo Role (en create, se muestra como de solo lectura) --%>
            <acme:input-select readonly="true"
                code="flight-crew-member.flight-assignment.list.label.role"
                path="duty" choices="${duty}" />

            <%-- Campo Leg --%>
            <acme:input-select
                code="flight-crew-member.flight-assignment.list.label.leg"
                path="leg" choices="${possibleLegs}" />

            <%-- Campo Remarks, diferenciando según Fid e id --%>
            <acme:input-textbox readonly="false"
                code="flight-crew-member.flight-assignment.list.label.remarks"
                path="remarks" />
                 

            <%-- Botón para crear asignación --%>
            <acme:submit code="flight-crew-member.flight-assignment.create"
                action="/flight-crew-member/flight-assignment/create" />
        </acme:form>
    </jstl:when>

    <%-- Bloque para los demás comandos (por ejemplo, update) --%>
    <jstl:otherwise>
        <acme:form readonly="false">
            <%-- Campo Role (en comandos distintos a addFlightAssignment, se muestra como de solo lectura) --%>
            <acme:input-select readonly="true"
                code="flight-crew-member.flight-assignment.list.label.role"
                path="duty" choices="${duty}" />

            <%-- Campo Leg --%>
            <acme:input-select readonly = "false"
                code="flight-crew-member.flight-assignment.list.label.leg"
                path="leg" choices="${possibleLegs}" />

            <%-- Campo Remarks, diferenciando según Fid e id --%>
            <jstl:if test="${Fid != id}">
                <acme:input-textbox readonly="true"
                    code="flight-crew-member.flight-assignment.list.label.remarks"
                    path="remarks" />
            </jstl:if>
            <jstl:if test="${Fid == id}">
                <acme:input-textbox readonly="false"
                    code="flight-crew-member.flight-assignment.list.label.remarks"
                    path="remarks" />
            </jstl:if>

            <%-- Campo Lead Attendant si duty no es 'LEAD ATTENDANT' --%>
            <jstl:if test="${duty != 'LEAD ATTENDANT'}">
                <acme:input-textbox readonly="true"
                    code="flight-crew-member.flight-assignment.list.label.leadAttendant"
                    path="lead_attendant" />
            </jstl:if>

            <%-- Botones de acción si se cumple la condición de vuelo no publicado/aterrizado y Fid coincide --%>
            <jstl:if test="${!leg.published and leg.status != 'LANDED' and Fid == id}">
                <acme:submit code="flight-crew-member.flight-assignment.update"
                    action="/flight-crew-member/flight-assignment/update"/>
                <acme:button code="flight-crew-member.flight-assignment.listCrews"
                    action="/flight-crew-member/flight-assignment/list-crews?Fid=${Fid}"/>
                <acme:submit code="flight-crew-member.flight-assignment.delete"
                    action="/flight-crew-member/flight-assignment/delete"/>
                <acme:submit code="flight-crew-member.flight-assignment.publish"
                    action="/flight-crew-member/flight-assignment/publish"/>
            </jstl:if>
        </acme:form>
    </jstl:otherwise>
</jstl:choose>
