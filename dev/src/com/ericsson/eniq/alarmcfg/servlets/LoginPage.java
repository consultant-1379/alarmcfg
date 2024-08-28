package com.ericsson.eniq.alarmcfg.servlets;

import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ericsson.eniq.alarmcfg.authenticate.AuthSession;
import com.ericsson.eniq.alarmcfg.authenticate.Authenticator;
import com.ericsson.eniq.alarmcfg.authenticate.AuthenticatorFactory;
import com.ericsson.eniq.alarmcfg.config.AlarmConfigurationFactory;
import com.ericsson.eniq.alarmcfg.config.AlarmProperties;
import com.ericsson.eniq.alarmcfg.database.DatabaseConnector;
import com.ericsson.eniq.alarmcfg.database.DatabaseSession;
import com.ericsson.eniq.alarmcfg.database.DatabaseConnectorFactory;
import com.ericsson.eniq.alarmcfg.exceptions.AlarmConfigurationException;
import com.ericsson.eniq.alarmcfg.exceptions.AuthenticationException;
import com.ericsson.eniq.alarmcfg.exceptions.DatabaseException;
import com.ericsson.eniq.alarmcfg.licensing.DefaultLicenseChecker;
import com.ericsson.eniq.alarmcfg.licensing.LicenseChecker;
import com.ericsson.eniq.repository.ETLCServerProperties;

/**
 * Servlet implementation class LoginPage
 * 
 * @author ecarbjo
 * @author eheijun
 * @copyright Ericsson (c) 2009
 */
public class LoginPage extends AlarmServlet {

  private static final long serialVersionUID = 1L;

  private static final String LOGIN_FORM_JSP = "/LoginPage/Form.jsp";

  private static final String EXISTING_ALARMS = "ExistingAlarms";
  
  private String defaultMessage;
  private static final String MESSAGE_PROPERTIES_FILE = "/eniq/sw/runtime/tomcat/webapps/adminui/conf/message.properties";
  private final Properties props = new Properties();
  private static Logger log = Logger.getLogger(LoginPage.class.getName());

  /**
   * @see HttpServlet#HttpServlet()
   */
  public LoginPage() {
    super();

    log.finest("Instantiated the LoginPage servlet");
  }

  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
      IOException {
    if (isRequestValid(request, response, false)) {
      // we already have a valid session, just forward to the ExistingAlarms
      // page.
      response.sendRedirect(EXISTING_ALARMS);
    } else {
      // kill any old session, since it's either not existing or not valid.
      logout(request.getSession(false));

      final RequestDispatcher view = request.getRequestDispatcher(LOGIN_FORM_JSP);
      view.forward(request, response);
    }
  }

  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
      IOException {
    // Get a map of the request parameters

    String forward = LOGIN_FORM_JSP;

    // get the authenticator from session data if it has already been
    // instantiated. This will probably only be used for unit testing, but it
    // could be reused in other cases such as logouts as well.
    final HttpSession session = request.getSession();

    // if the request ISN'T valid, then do the login check. If we already have a
    // valid session, then forward to the existing alarms page
    if (isRequestValid(request, response, false)) {
      response.sendRedirect(EXISTING_ALARMS);
    }
    // TR:HK96425: Started
    // get message from JSP and check for browser
    // Display error message if browser does not support
    else if (request.getParameter("browserstatusmessage") != null && !request.getParameter("browserstatusmessage").equals("")) { //NOPMD
      final String status = (String) request.getParameter("browserstatusmessage");
      if (status.equals("Unsupported browser version")) {

        log.warning("Invalid Browser Version");

        // show a message on the screen for the user.
        request.setAttribute("login_error",
            "Invalid Browser version! Please refer to Alarm module user guide to see all supported Internet browsers.");

        // now forward this request to the view.
        request.getRequestDispatcher(forward).forward(request, response);
        // invalidate session
        session.invalidate();
      } else if (status.equals("Browser is not supported")) {

        log.warning("Invalid Browser");

        // show a message on the screen for the user.
        request.setAttribute("login_error",
            "Invalid Browser for the alarm module! Please refer Alarm Module User Guide.");

        // now forward this request to the view.
        request.getRequestDispatcher(forward).forward(request, response);
        // invalidate session
        session.invalidate();
      }
      // TR:HK96425: End
    } else {
      if (request.getParameter("browserversionmessage") != null && !request.getParameter("browserversionmessage").equals("")) {
        final String browserVersion = (String) request.getParameter("browserversionmessage");
        log.fine(browserVersion);
      }

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
          log.fine("Alarm configurations read successfully");
        } catch (AlarmConfigurationException e) {
          log.severe(e.getMessage());
        }
        session.setAttribute("etlcserverproperties", etlcServerProperties);
        session.setAttribute("alarmproperties", alarmProperties);
      } else {
        etlcServerProperties = (ETLCServerProperties) session.getAttribute("etlcserverproperties");
        alarmProperties = (AlarmProperties) session.getAttribute("alarmproperties");
      }

      // first check the license.
      final LicenseChecker checker = new DefaultLicenseChecker(etlcServerProperties);
      if (checker.isLicenseValid()) {
        // start a database session.
        DatabaseSession databaseSession = null;
        if (session.getAttribute("databasesession") == null) {
          try {
            if (alarmProperties != null) {
              final DatabaseConnector databaseConnector = DatabaseConnectorFactory.createDatabaseConnector();
              databaseSession = databaseConnector.createDatabaseSession(etlcServerProperties, alarmProperties);
              log.fine("Database session created successfully");
            }
          } catch (DatabaseException e) {
            log.severe(e.getMessage());
          }
          session.setAttribute("databasesession", databaseSession);
        } else {
          databaseSession = (DatabaseSession) session.getAttribute("databasesession");
        }

        // get the user input from the form, and process it via the doLogin
        // method
        // (in AlarmServlet).
        if (request.getParameter("username") == null) {
          request.setAttribute("login_error", "Username not given");
          // now forward this request to the view.
          request.getRequestDispatcher(forward).forward(request, response);
          // invalidate session
          session.invalidate();
        } else {
          final String username = (String) request.getParameter("username");
          final String password = (String) request.getParameter("password");
          final String server = (String) request.getParameter("cms");
          final String authType = (String) request.getParameter("authtype");

          // try to log in.
          final LoginResponse login = doLogin(auth, username, password, server, authType, session);

          // now set the forward page and/or errors accordingly.
          if (login == LoginResponse.SUCCESS) {
            log.finest("Login succeeded for user " + username + ". Forwarding to existing alarms");
            response.sendRedirect(EXISTING_ALARMS);

          } else {
            forward = LOGIN_FORM_JSP;

            log.info("Login failure for user " + username + ". Error " + login);

            // set a sensible, human readable, error.
            if (login == LoginResponse.FAILED_CONNECTION) {
              request
                  .setAttribute("login_error", "Connection to the server " + request.getParameter("cms") + " failed");
            } else if (login == LoginResponse.FAILED_LOGIN) {
              request.setAttribute("login_error", "Login failed");
            } else if (login == LoginResponse.FAILED_PASSWORD) {
              request.setAttribute("login_error", "The password was invalid");
            } else {
              request.setAttribute("login_error", "Could not log in");
            }

            // now forward this request to the view.
            request.getRequestDispatcher(forward).forward(request, response);
            // invalidate session
            session.invalidate();
          }
        }      
      } else {
        // do not allow the user to log in since the license is invalid!
        forward = LOGIN_FORM_JSP;

        log.warning("License validation failed!");

        // show a message on the screen for the user.
        request.setAttribute("login_error",
            "Could not find a valid license for the alarm module! Please contact the system administrator.");

        // now forward this request to the view.
        request.getRequestDispatcher(forward).forward(request, response);
        // invalidate session
        session.invalidate();
      }
    }
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
    try {
      // authenticate against the given authenticator.
      final AuthSession authSession = auth.getAuthSession(username, password, CMS, authtype);

      // if we got a session, the login was valid. Save the session and return
      // success.
      if (authSession == null) {
        // return a failed login.
        return LoginResponse.FAILED_LOGIN;
      } else {
        session.setAttribute("authsession", authSession);

        // make sure that the interfaces have been read.
        getInterfaces(session);

        return LoginResponse.SUCCESS;
      }
    } catch (AuthenticationException e) {
      // we got an authentication exception. A number of things could cause
      // this, return a generic failure.
      return LoginResponse.FAILED_GENERIC;
    }
  }
  public String getFooterMessage() {
		File messageFile = new File(MESSAGE_PROPERTIES_FILE);
		defaultMessage ="IF YOU ARE NOT AN AUTHORIZED USER STOP ANY ACTIVITY YOU ARE PERFORMING ON THIS SYSTEM AND EXIT IMMEDIATELY.\n\n"+
				"This system is provided for authorized and official use only.\n"+
				"The usage of this system is monitored and audited.\n"+
				"Unauthorized or improper usage may result in disciplinary actions, civil or criminal penalties.\n";
		try {
			props.load(new FileInputStream(messageFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return props.getProperty("CUSTOM_MESSAGE",defaultMessage).replaceAll("(\r\n|\n)", "<br>");
	}

}
