<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="com.ericsson.eniq.alarmcfg.authenticate.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<%
	AuthSession auth = (AuthSession) session.getAttribute("AuthSession");
	if (auth != null) {
	  out.println("<div class='message'>Logged in... Redirecting</div>");
	} else {
	  out.println("<div class='message'>Not logged in!</div>");
	  %>
	  	<jsp:forward page="/LoginPage" />
	  <%
	}
%>
<br><br>However: Move along, nothing to see here... just a test!
</body>
</html>