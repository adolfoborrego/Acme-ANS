
<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
    <acme:input-textbox code="administrator.airline.form.label.name" path="name" readonly="true"/>
    <acme:input-textbox code="administrator.airline.form.label.iataCode" path="iataCode" readonly="true"/>
    <acme:input-url code="administrator.airline.form.label.website" path="website" readonly="true"/>
    <acme:input-textbox code="administrator.airline.form.label.type" path="type" readonly="true"/>
    <acme:input-moment code="administrator.airline.form.label.foundationTime" path="foundationTime" readonly="true"/>
    <acme:input-email code="administrator.airline.form.label.email" path="email" readonly="true"/>
    <acme:input-textbox code="administrator.airline.form.label.phoneNumber" path="phoneNumber" readonly="true"/>
</acme:form>