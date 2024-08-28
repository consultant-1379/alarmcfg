<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.Enumeration"%>
<%@ page import="com.ericsson.eniq.alarmcfg.reports.AlarmReport"%>
<%@ page import="com.ericsson.eniq.alarmcfg.prompts.AlarmReportPrompt"%>
<%@ page import="com.ericsson.eniq.alarmcfg.common.Constants"%>

<%
  // cache control to prevent the pages from showing out of date data.
  
  response.setHeader("Pragma", "no-cache"); //HTTP 1.0
  response.setDateHeader("Expires", 0); //prevents caching at the proxy server
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@ include file="/fragments/PageHeader.jspf"%>

<body>
<div id="main"><%@ include file="/fragments/Header.jspf"%>
<%@ include file="/fragments/Interfaces.jspf"%>

<div id="left_column">
<%
  AlarmInterfaceContainer interfaces = (AlarmInterfaceContainer) session.getAttribute("interfaces");

  //print the interfaces, but disable selection.
  out.println(getInterfaces(interfaces, true, true));
%>
</div>

<div id="main_column">
<%
  if (request.getAttribute("message") != null) {
    out.println("<div class='main_message'>" + request.getAttribute("message") + "</div>");
  }
%>
<div id="action_title">Add new alarm report (2/2): Report settings</div>
<form action="AddAlarm" method="POST" name="addalarmform" onSubmit="return filterFields();"><input type="hidden"
	name="reportid" value="<%= request.getParameter("add") %>">
<table id="parameter_table">
    <tr><td class='table_spacer' colspan='2'>&nbsp;</td></tr>
	<tr>
		<td colspan='2' class='parameter_title'>Alarm report:</td>
	</tr>
    <%
    final AlarmReport addReport = (AlarmReport) session.getAttribute("addreport");
    if (addReport != null) {
      out.println("<tr><td class='parameter_value'>" + addReport.getName() + "</td></tr>");
    }
    out.println("<tr><td class='table_spacer' colspan='2'>&nbsp;</td></tr>");
    %>
</table>
<table id="parameter_table">
	<tr>
		<td colspan='2' class='parameter_title'>Base table selection:</td>
	</tr>
	<tr>
		<td class="parameter_desc">Tech pack:</td>
		<td class="parameter_value"><select onchange="populate('type');"
			name="select_techpacks" id="select_techpacks">
		</select></td>
	</tr>
	<tr>
		<td class="parameter_desc">Type name:</td>
		<td class="parameter_value"><select onchange="populate('level');"
			name="select_types" id="select_types">
		</select></td>
	</tr>
	<tr>
		<td class="parameter_desc">Table level:</td>
		<td class="parameter_value"><select
			onchange="populate('basetable');" name="select_levels"
			id="select_levels">
		</select></td>
	</tr>
	<tr>
		<td class="parameter_desc">Base table name:</td>
		<td class="parameter_value"><select id="select_basetables"
			name="select_basetables" onchange="checkAddStatus();">
		</select></td>
	</tr>

	<%
		// print all the different parameters for the alarm report and also fix these.
		if (addReport != null) {
			String promptValueLimit = null;
		  out.println("<tr><td class='table_spacer' colspan='2'>&nbsp;</td></tr>");
		  out.println("<tr><td class='parameter_title' colspan='2'>Prompts:</td></tr>");
		  Enumeration<AlarmReportPrompt> prompts = addReport.getPrompts();
		  StringBuffer sb = new StringBuffer();
		  int promptsCount = 0;
		  int promptsNum = 0;
		  while (prompts.hasMoreElements()) {
		    AlarmReportPrompt prompt = prompts.nextElement();
		    if (prompt.isSettable()) {
		        String previousValue = "";
		        if (prompt.getPreviousValues() != null) {
		          for (String value : prompt.getPreviousValues()) {
		            if (previousValue.isEmpty()) {
		              previousValue = value; 
		            } else {
		              previousValue += Constants.PROMPT_SEPARATOR + value; 
		            }
		          }
		           if (previousValue.length()>=255){
		        	  previousValue = previousValue.substring(0, 255);
		        	  promptValueLimit = previousValue;
		        	  sb.append(prompt.getName());
		        	  sb.append(", ");
 
		          }
		          
		        }
			    out.println("<tr>");
			    out.println("  <td class='parameter_desc'>" + prompt.getName() + "</td>");
			    out.println("  <td class='parameter_value'><input name='prompt_" + prompt.getName() + "'" + "value='" + previousValue + "'"    
			        + " maxlength='255' id='prompt_" + promptsNum+"'"+" onchange=showLimitMsgAlert('prompt_" + promptsNum+"');></td>");
			    out.println("</tr>");
			    promptsNum++;
		    }
		    
		    // note! it is important that the counter is incremented each 
		    // time so that the correct prompt id is given in the name!
		    promptsCount++;
		  }
		  if(promptValueLimit!=null && promptValueLimit.length()>=255){
			out.println("<tr>");
			out.println(" <td colspan=2 class='promptvalue_warning'><IMG src='img/warning.png' border=0 />&nbsp;For prompts:&nbsp;" + sb.toString()+"&nbsp;"+
			"values were more than allowed length.&nbsp; Now Values are truncated to maximum limit." +
				  "&nbsp;Please check Alarm User Guide, section: Adding and Activating Alarm Reports, for more information.</td>");
		 	out.println("</tr>");
		  }
		}
	%>

	<tr>
		<td class='table_spacer' colspan='2'>&nbsp;</td>
	</tr>
	<tr>
		<td colspan="2">
		<center><input type="submit" value="Add report"
			id="add_alarm_button" >&nbsp; <input type="button"
			value="Cancel" onclick="parent.location='ExistingAlarms'"></center>
		</td>
	</tr>
</table>
<p><b>Note:</b> please select the specific Techpack for which alarms need to be generated.</p>
</form>
</div>
</div>

<script type="text/javascript">
	populate('techpack');
	checkAddStatus();
</script>

</body>
	<script>
		window.addEventListener('load',window.history.replaceState(null, null, window.location.pathname))
	</script>
</html>