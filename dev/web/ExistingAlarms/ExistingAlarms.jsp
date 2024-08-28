<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="com.ericsson.eniq.alarmcfg.authenticate.AuthSession"%>
<%@ page import="com.ericsson.eniq.alarmcfg.common.Constants"%>
<%@ page import="com.ericsson.eniq.alarmcfg.reports.AlarmReport"%>
<%@ page import="com.ericsson.eniq.alarmcfg.prompts.AlarmReportPrompt"%>
<%@ page import="java.util.*"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<%@ include file="/fragments/PageHeader.jspf"%>

<body>
<div id="main"><%@ include file="/fragments/Header.jspf"%>
<%@ include file="/fragments/Interfaces.jspf"%>
<%@ include file="/fragments/LocationButton.jspf"%>

<div id="left_column">
<%
  AlarmInterfaceContainer interfaces = (AlarmInterfaceContainer) session.getAttribute("interfaces");
  out.println(getInterfaces(interfaces, false));
%>
</div>
<div id="main_column">
<%
  if (request.getAttribute("message") != null) {
    out.println("<div class='main_message'>" + request.getAttribute("message") + "</div>");
  }

  if (request.getAttribute("infomessage") != null) {
    out.println("<div class='info_message'>" + request.getAttribute("infomessage") + "</div>");
  }

  if (interfaces != null) {

    Vector<AlarmReport> boreps;

    AlarmInterface currentInterface = interfaces.getCurrentInterface();

    if (currentInterface != null) {
      List<AlarmReport> reports = currentInterface.getReports();

      if (reports.isEmpty()) {
        out.println("<div id='action_title'>No&nbsp;reports&nbsp;for&nbsp;" + currentInterface.getName() + "&nbsp;interface</div>");
      } else {

        if (currentInterface.isRealInterface()) {
          out.println(getLocationButton("Add Report", "AddReport"));
        }

        String prevInterface = null;

        for (AlarmReport currentReport : reports) {

          if (!currentReport.getInterface().getName().equals(prevInterface)) {
            if (prevInterface != null) {
              out.println("</table>");
            }
            out.println("<div id='action_title'>Interface&nbsp;" + currentReport.getInterface().getName() + "&nbsp;reports:</div>");
            out.println("<table id='report_table'>");
            out.println("  <tr>");
            out.println("    <th>Status</th>");
            out.println("    <th>Name</th>");
            out.println("    <th>Prompts</th>");
            out.println("    <th>Base table Name</th>");
            out.println("    <th>Actions</th>");
            out.println("  </tr>");
            prevInterface = currentReport.getInterface().getName();
          }
          out.println("<tr>");

          out.println("  <td>");
          if (currentReport.isValid()) {
            if (currentReport.isEnabled()) {
              out.println("<img src='img/Enabled_state.png' alt='Enabled' />");
            } else {
              out.println("<img src='img/Disabled_state.png' alt='Disabled' />");
            }
          } else {
            out.println("<img src='img/warning.png' alt='Invalid' />");
          }
          out.println("  </td>");

          out.println("  <td>" + currentReport.getName() + "</td>");
          out.println("  <td>");

          // now print all prompt values as a list.
          String basetableName = "";
          Enumeration<AlarmReportPrompt> prompts = currentReport.getPrompts();
          if (prompts.hasMoreElements()) {
            out.println("<table class='prompt_table'>");
            while (prompts.hasMoreElements()) {
              AlarmReportPrompt prompt = prompts.nextElement();
              // BasetableName is shown in different column
              if (prompt.getName().equals(Constants.BASETABLE_NAME)) {
                basetableName = prompt.getValue();
              } else if (prompt.isSettable()) {
	              // just replace all , with a ", ". Even if we have duplicate spaces because of this, it won't matter since the browsers truncate multiple whitespace.
	              String value = prompt.getValue().replaceAll(",",", ");
	              out.println("<tr><td>" + prompt.getName() + "</td><td>" + value + "</td></tr>");
              }
            }
            out.println("</table>");
          }

          out.println("  <td>" + basetableName + "</td>");

          out.println("  </td>");
          out.println("  <td>");
          if (currentReport.isEnabled()) {
            out.println("<a href='ChangeStatus?disable=" + currentReport.getId()
                + "'><img src='img/stop_task.png' alt='Disable' />&nbsp;</a>");
          } else if (currentReport.isValid()) {
            out.println("<a href='ChangeStatus?enable=" + currentReport.getId()
                + "'><img src='img/start_task.png' alt='Enable' />&nbsp;</a>");
          } else {
            out.println("<img src='img/start_disabled_task.png' alt='Fix errors' />&nbsp;");
          }
          out.println(" <a href='DeleteReport?id=" + currentReport.getId()
              + "'><img src='img/delete.png' onclick='return confirmAlarmRemove();' alt='Remove' />"
              + "&nbsp;</a>");
          out.println("  </td>");

          out.println("</tr>");

        }
        if (prevInterface != null) {
          out.println("</table>");
        }
      }

      if (currentInterface.isRealInterface()) {
    	  out.println(getLocationButton("Add Report", "AddReport"));
      }
      
    }
  }
%>
</div>
</div>

</body>
	<script>
		window.addEventListener('load',window.history.replaceState(null, null, window.location.pathname))
	</script>
</html>