<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="com.ericsson.eniq.alarmcfg.reports.AlarmReport" %>

<%
  // cache control to prevent the pages from showing out of date data.
  
  response.setHeader("Pragma", "no-cache"); //HTTP 1.0
  response.setDateHeader("Expires", 0); //prevents caching at the proxy server
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<%@ include file="/fragments/PageHeader.jspf" %>

<body>
<div id="main">

<%@ include file="/fragments/Header.jspf" %>
<%@ include file="/fragments/Interfaces.jspf" %>
<%@ include file="/fragments/LocationButton.jspf"%>

<div id="left_column">
<%
  AlarmInterfaceContainer interfaces = (AlarmInterfaceContainer) session.getAttribute("interfaces");
  out.println(getInterfaces(interfaces, true, true));
%>
</div>

<div id="main_column">
<%
  if (request.getAttribute("message") != null) {
    out.println("<div class='main_message'>" + request.getAttribute("message") + "</div>");
  }
%>
<div id="action_title">Add new alarm report (1/2): Available reports</div>
	<table id="report_table">
	<tr>
		<th>Add</th>
		<th>Name</th>
	</tr>
	<%
	  Vector<AlarmReport> reports = (Vector<AlarmReport>) session.getAttribute("reportlist");

	  // if we have some available reports, print them. If no reports are available, print such a message instead.
	  if (reports != null && reports.size() > 0) {
	    for (int i = 0; i < reports.size(); i++) {
	      AlarmReport report = reports.get(i);
	      out.println("<tr><td>");
	      if (request.getAttribute("disableSelection") == null) {
	      	out.println("<a href='AddReport?add=" + i + "'><img src='img/new.png' alt='Add' />&nbsp;</a>");
	      } else {
	        out.println("N/A");
	      }
	      out.println("</td><td>" + report.getName() + "</td></tr>");
	    }
	  } else {
	    out.println("<tr><td colspan='2'>No reports available!</td></tr>");
	  }
	%>
</table>

<%
	out.println(getLocationButton("Cancel", "ExistingAlarms"));
%>
</div>
</div>

</body>
	<script>
		window.addEventListener('load',window.history.replaceState(null, null, window.location.pathname))
	</script>
</html>