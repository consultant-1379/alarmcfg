/**
 * 
 */
package com.ericsson.eniq.alarmcfg.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ericsson.eniq.alarmcfg.authenticate.AuthSession;
import com.ericsson.eniq.alarmcfg.authenticate.Authenticator;
import com.ericsson.eniq.alarmcfg.authenticate.AuthenticatorFactory;
import com.ericsson.eniq.alarmcfg.common.Constants;
import com.ericsson.eniq.alarmcfg.common.PromptNameValuePair;
import com.ericsson.eniq.alarmcfg.config.AlarmConfigurationFactory;
import com.ericsson.eniq.alarmcfg.config.AlarmProperties;
import com.ericsson.eniq.alarmcfg.database.DatabaseConnector;
import com.ericsson.eniq.alarmcfg.database.DatabaseConnectorFactory;
import com.ericsson.eniq.alarmcfg.database.DatabaseSession;
import com.ericsson.eniq.alarmcfg.exceptions.AlarmConfigurationException;
import com.ericsson.eniq.alarmcfg.exceptions.AuthenticationException;
import com.ericsson.eniq.alarmcfg.exceptions.DatabaseException;
import com.ericsson.eniq.alarmcfg.interfaces.AlarmInterface;
import com.ericsson.eniq.alarmcfg.interfaces.AlarmInterfaceContainer;
import com.ericsson.eniq.alarmcfg.interfaces.AllAlarmInterface;
import com.ericsson.eniq.alarmcfg.reports.AlarmReport;
import com.ericsson.eniq.alarmcfg.reports.AlarmReportLister;
import com.ericsson.eniq.repository.ETLCServerProperties;

/**
 * @author eheijun
 * 
 */
public class AlarmReportRunner extends AlarmServlet {

  private static Logger log = Logger.getLogger(AlarmReportRunner.class.getName());
 
  /**
   * 
   */
  private static final long serialVersionUID = 3923109488394800188L;

  /**
   * This servlet runs BO Document and returns it as XML format in response
   */
  protected void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
      IOException {
	  String threadName = Thread.currentThread().getName();
	  log.fine(threadName+" Inside service");
    if (!isRequestValid(request, response, false)) {
      if (!authenticate(request)) {
        return;
      }
    }
    
    final HttpSession session = request.getSession();
 
    // just for debugging purposes, real alarm module should never send this parameter
    // debug_time parameter can be used to set minimum time before servlet responses
    try {
    
    final long start_time = System.currentTimeMillis();
    long debug_time = 0;
    if (request.getParameter("debug_time") != null) {
      final String debug_time_str = (String) request.getParameter("debug_time");
      if (debug_time_str != null && !debug_time_str.equals("")) {
        debug_time = Long.parseLong(debug_time_str);
      }
    }

    log.fine(threadName+" service starts at " + start_time);

    // We have valid session and go on running report
    String password = "password";
    String reportName = null;
    String reportId = "1"; // old reports don't have reportID so use default 1

    final List<PromptNameValuePair> nameValuePairs = new ArrayList<PromptNameValuePair>();

    // Causes problems with corbetura?
    // @SuppressWarnings("unchecked")
    final Enumeration<?> paramNames = request.getParameterNames();

    while (paramNames.hasMoreElements()) {
      String paramName = ((String) paramNames.nextElement()).trim();
      final String paramValue = ((String) request.getParameter(paramName)).trim();
      if (paramName.equalsIgnoreCase(Constants.REPORT_NAME)) {
        reportName = paramValue;
        log.fine(threadName+" session " + session.getId() + " report " + reportName + " requested");
      } else if (paramName.equalsIgnoreCase(Constants.REPORT_ID)) {
        reportId = paramValue;
        log.fine(threadName+" session " + session.getId() + " report id " + reportId + " requested");
      } else if (paramName.startsWith("promptValue_")) {
        paramName = paramName.substring(12);
        // prompt needs values as String table
        final String[] paramValues = paramValue.split(Constants.PROMPT_SEPARATOR);
        final PromptNameValuePair nameValuePair = new PromptNameValuePair(paramName, paramValues);
        nameValuePairs.add(nameValuePair);
         for (String tmp : paramValues) {
        	 if(!paramName.equalsIgnoreCase(password)) {
          log.fine(threadName+" session " + session.getId() + " prompt " + paramName + " has value " + tmp);
        	 }
        }
      } else if(!paramName.equalsIgnoreCase(password)){
        log.fine(threadName+" session " + session.getId() + " param " + paramName + " has value " + paramValue);
      }
    }

    if ((reportName == null) || (reportName.trim().equals(""))) {
      log.severe(threadName+" session " + session.getId() + " Invalid alarm runner servlet call, reportname is missing.");
      return;
    }

    long current_time = System.currentTimeMillis();
    log.fine(threadName+" session " + session.getId() + " parameters are resolved at " + (current_time - start_time) + " ms");

    // fetch the alarm category.
    final DatabaseSession dbSession = (DatabaseSession) session.getAttribute("databasesession");
    final AlarmProperties alarmProperties = (AlarmProperties) session.getAttribute("alarmproperties");
    final AlarmInterfaceContainer interfaces = getInterfaces(session);
    final AlarmInterface allinterface = interfaces.getInterfaceById(AllAlarmInterface.ALL_NAME);
    final String alarmCategory = alarmProperties.getProperty(AlarmProperties.BOALARMCATEGORY);
    final String alarmCategoryRD = alarmProperties.getProperty(AlarmProperties.BOALARMCATEGORYRD);
    current_time = System.currentTimeMillis();
    log.finest(threadName+" session " + session.getId() + " alarm category " + alarmCategory + " resolved at "+ (current_time - start_time) + " ms");

    log.fine(threadName+" session " + session.getId() + " running report " + reportName + " from " + alarmCategory);
    // get the report lister and retrieve a list of all reports that are available.
    final AlarmReportLister reportLister = getAuthSession(session).getAlarmReportLister(dbSession, allinterface,
        new String[] { alarmCategory, alarmCategoryRD }, new Boolean[] { false, true });
    final AlarmReport boAlarmReport = reportLister.getReport(reportName);
 	    current_time = System.currentTimeMillis();
	    log.finest(threadName+" session " + session.getId() + " alarm report instance resolved at " + (current_time - start_time)
	        + " ms");
    String xmlView = boAlarmReport.refresh(nameValuePairs);
    log.finest(threadName+" xmlView refresh Done " + session.getId() );
    // adding the report id to the report xml
    final String reportTag = "<report rid=\"";
    final String reportEndTag = "\">";

    final String temp = xmlView.substring(xmlView.lastIndexOf(reportTag) + reportTag.length());
    final String titleNo = temp.substring(0, temp.indexOf("\""));

    final String newReportId = reportTag + reportId + reportEndTag;
    final String oldReportID = reportTag + titleNo + reportEndTag;
    xmlView = xmlView.replaceFirst(oldReportID, newReportId);
    current_time = System.currentTimeMillis();
    log.finest(threadName+ " session " + session.getId() + " alarm report (contains " + xmlView.length() + " chars) refreshed at "
        + (current_time - start_time) + " ms");

    log.fine(threadName+" session " + session.getId() + " report successfully refreshed");

    response.reset();
    response.setContentType("text/xml");

    final PrintWriter out = response.getWriter();

    out.println(xmlView);

    current_time = System.currentTimeMillis();
    log.finest(threadName+" session " + session.getId() + " alarm report printed for response at " + (current_time - start_time)
        + " ms");

    // if debug_time is set, wait until time has gone
    long end_time = System.currentTimeMillis();
    while (end_time < start_time + debug_time) {
      end_time = System.currentTimeMillis();
    }
    log.finest(threadName+" session " + session.getId() + " 	 " + (end_time - start_time)
        + " ms");
    }
	    catch(Exception e){
	 	 	 	
	 	log.severe(threadName+" alarmReportRunner failed to download the report"+ " session " + session.getId()+"  " +e);
	 	  
	    }
    finally{ 
    // finally clean session
        	doLogout(session);
            }
  }

  private boolean authenticate(final HttpServletRequest request) {
	  String threadName = Thread.currentThread().getName();
    // get the authenticator from session data if it has already been
    // instantiated. This will probably only be used for unit testing, but it
    // could be reused in other cases such as logouts as well.
    final HttpSession session = request.getSession();

    Authenticator auth;
    if (session.getAttribute("authenticator") == null) {
      auth = AuthenticatorFactory.getAuthenticator();
    } else {
      auth = (Authenticator) session.getAttribute("authenticator");
    }

    // get the properties for the alarm configuration.
    ETLCServerProperties etlcServerProperties = null;
    AlarmProperties alarmProperties = null;
    if (session.getAttribute("alarmproperties") == null) {
      try {
        etlcServerProperties = AlarmConfigurationFactory.getAlarmConfiguration().getETLCServerProperties(
            getServletContext());
        alarmProperties = AlarmConfigurationFactory.getAlarmConfiguration().getAlarmProperties(getServletContext());
        log.fine(threadName+" session " + session.getId() + " Alarm configurations read successfully");
      } catch (AlarmConfigurationException e) {
        log.severe(threadName+" session " + session.getId() + " " + e.getMessage());
      }
      session.setAttribute("etlcserverproperties", etlcServerProperties);
      session.setAttribute("alarmproperties", alarmProperties);
    } else {
      etlcServerProperties = (ETLCServerProperties) session.getAttribute("ETLCServerProperties");
      alarmProperties = (AlarmProperties) session.getAttribute("alarmproperties");
    }

    // start a database session.
    if (session.getAttribute("databasesession") == null) {
      DatabaseSession databaseSession = null;
      try {
        if (alarmProperties != null) {
          final DatabaseConnector databaseConnector = DatabaseConnectorFactory.createDatabaseConnector();
          databaseSession = databaseConnector.createDatabaseSession(etlcServerProperties, alarmProperties);
          log.fine(threadName+" session " + session.getId() + " Database session created successfully");
        }
      } catch (DatabaseException e) {
        log.severe(threadName+" session " + session.getId() + " " + e.getMessage());
      }
      session.setAttribute("databasesession", databaseSession);
    }

    // get the user input from the form, and process it via the doLogin method
    // (in AlarmServlet).
    if (request.getParameter("username") != null) {
      final String username = (String) request.getParameter("username");
      final String password = (String) request.getParameter("password");
      final String server = (String) request.getParameter("cms");
      final String authType = (String) request.getParameter("authtype");

      log.fine(threadName+" session " + session.getId() + " Trying to log in " + server + " with username " + username
          + " using authmethod " + authType);
      // try to log in.
      final LoginResponse login = doLogin(auth, username, password, server, authType, session);

      // now set the forward page and/or errors accordingly.
      if (login == LoginResponse.SUCCESS) {
        log.fine(threadName+" session " + session.getId() + " Login succesfull");
        return true;
      }
    }
    log.fine(threadName+" session " + session.getId() + " Login unsuccesfull");
    return false;
  }

  /**
   * @param username
   *          the user login.
   * @param password
   *          the user password.
   * @param CMS
   *          the server to check the credentials against.
   * @param authtype
   *          the type of authentication to use (enterprise?)
   * @return A LoggingResponse with the relevant value.
   */
  protected LoginResponse doLogin(final Authenticator auth, final String username, final String password,
      final String CMS, final String authtype, final HttpSession session) {
	  String threadName = Thread.currentThread().getName();
    try {
    	 
      // authenticate against the given authenticator.
    	log.fine(threadName+" Inside LoginResponse doLogin " + session.getId());
      final AuthSession authSession = auth.getAuthSession(username, password, CMS, authtype);
      log.fine(threadName+" Inside LoginResponse doLogin.Got the session " + session.getId() );
      // if we got a session, the login was valid. Save the session and return
      // success.
      if (authSession == null) {
        log.fine(threadName+" session " + session.getId() + " AuthSession not ready");
        session.removeAttribute("authsession");
        // return a failed login.
        return LoginResponse.FAILED_LOGIN;
      } else {
        log.fine(threadName+" session " + session.getId() + " AuthSession ready");
        session.setAttribute("authsession", authSession);
        // return a success login.
        return LoginResponse.SUCCESS;
      }
    } catch (AuthenticationException e) {
      log.severe(threadName+" session " + session.getId() + " failed " + e.getMessage());
      // we got an authentication exception. A number of things could cause
      // this, return a generic failure.
      return LoginResponse.FAILED_GENERIC;
    } catch (Exception e) {
      log.severe(threadName+" session " + session.getId() + " failed " + e.getMessage());
      // we got an fatal exception. This can happen for example if session has already expired
      return LoginResponse.FAILED_GENERIC;
    }
  }

  /**
   * Clean BO and database session and invalidate http session
   * 
   * @param session
   */
  protected void doLogout(final HttpSession session) {
	  String threadName = Thread.currentThread().getName();
    final AuthSession authSession = (AuthSession) session.getAttribute("authsession");
    final DatabaseSession databaseSession = (DatabaseSession) session.getAttribute("databasesession");
    log.fine(threadName+" Logout in AlarmReportRunner "+ session.getId() );
    if (authSession != null) {
      // do final cleanup for the session.
      authSession.logout();
      session.removeAttribute("authsession");
      log.fine(threadName+" session " + session.getId() + " authSession cleaned");
    }

    if (databaseSession != null) {
      // clear database session
      databaseSession.clear();
      session.removeAttribute("databasesession");
      log.fine(threadName+" session " + session.getId() + " databaseSession cleaned");
    }
    session.invalidate();
    log.fine(threadName+" session " + session.getId() + " invalidate method called");
  }

}
