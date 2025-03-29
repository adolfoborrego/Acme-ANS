
<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
    <acme:input-textbox code="administrator.aircraft.list.label.model" path="model" readonly="true"/>
    <acme:input-textbox code="administrator.aircraft.list.label.registrationNumber" path="registrationNumber" readonly="true"/>
    <acme:input-textbox code="administrator.aircraft.list.label.capacity" path="capacity" readonly="true"/>
    <acme:input-textbox code="administrator.aircraft.form.label.cargoWeight" path="cargoWeight" readonly="true"/>
    <acme:input-textbox code="administrator.aircraft.list.label.status" path="status" readonly="true"/>
    <acme:input-textbox code="administrator.aircraft.form.label.details" path="details" readonly="true"/>
    <acme:input-textbox code="administrator.aircraft.list.label.airlane.iata" path="airlane.iata" readonly="true"/>
</acme:form>