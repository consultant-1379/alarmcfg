<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<%@ include file="/fragments/PageHeader.jspf"%>

<body>
<div id="main">

<%@ include file="/fragments/Header.jspf"%>
<%@ include file="/fragments/Interfaces.jspf"%>

<div id="left_column">
</div>

<div id="main_column">
<%
if (request.getAttribute("message") != null) {
  out.print("<div class='main_message'>" + request.getAttribute("message") + "</div>");
} else {
  out.print("<div class='main_message'>An error occurred which prevented the action from completing correctly.</div>");
}

if (request.getAttribute("error") != null) {
  out.println("<div class='error'>"+ request.getAttribute("error") +"</div>");
}

%>
</div>
</div>
</body>
</html>