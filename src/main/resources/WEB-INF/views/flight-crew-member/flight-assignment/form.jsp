<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>


<acme:form readonly=${readOnly}>
    <acme:input-textbox code="flight-crew-member.flight-assignment.list.label.role" path="duty"/>
    <acme:input-textbox code="flight-crew-member.flight-assignment.list.label.lastUpdate" path="momentOfLastUpdate"/>
    <acme:input-money code="flight-crew-member.flight-assignment.list.label.status" path="currentStatus"/>
    <acme:input-textarea code="flight-crew-member.flight-assignment.list.label.leg" path="leg"/>
    <acme:input-textbox code="flight-crew-member.flight-assignment.list.label.remarks" path="remarks"/>
    <acme:input-textbox code="flight-crew-member.flight-assignment.list.label.flightCrewMembers" path="flightCrewMembers"/>



    <jstl:choose>
        <jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish') && Leg.status != 'LANDED'}">
            <acme:submit code="flight-crew-member.flight-assignment.update.submit" action="/flight-crew-member/flight-assignment/update"/>
            <acme:submit code="flight-crew-member.flight-assignment.submit" action="/flight-crew-member/flight-assignment/delete"/>
            <acme:submit code="flight-crew-member.flight-assignment.publish" action="/flight-crew-member/flight-assignment/publish" />
        </jstl:when>
        <jstl:when test="${_command == 'create'}">
            <acme:submit code="flight-crew-member.flight-assignment.create.submit" action="/flight-crew-member/flight-assignment/create"/>
        </jstl:when>		
    </jstl:choose>	
</acme:form>