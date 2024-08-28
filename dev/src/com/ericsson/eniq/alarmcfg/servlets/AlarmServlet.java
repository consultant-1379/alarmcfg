package com.ericsson.eniq.alarmcfg.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ericsson.eniq.alarmcfg.authenticate.AuthSession;
import com.ericsson.eniq.alarmcfg.config.AlarmProperties;
import com.ericsson.eniq.alarmcfg.database.DatabaseSession;
import com.ericsson.eniq.alarmcfg.interfaces.AlarmInterface;
import com.ericsson.eniq.alarmcfg.interfaces.AlarmInterfaceContainer;
import com.ericsson.eniq.alarmcfg.interfaces.AlarmInterfaceFactory;

/**
 * This is a master class for all servlets in the alarm configuration
 * application. Include common methods and attributes in this class.
 */
public class AlarmServlet extends HttpServlet {

  private static final long serialVersionUID = -6652146839637582518L;

  private static Logger log = Logger.getLogger(AlarmServlet.class.getName());

  private static final String LOGIN_PAGE = "LoginPage";

  private static final String ERROR_PAGE = "/ErrorPages/ErrorPage.jsp";

  /**
   * Check if the request is valid. To have a valid request we need to make sure
   * that the session exists and contains the necessary data, and that the
   * session is properly authenticated. If this is not the case, we send a
   * redirect to the login page. If all data is properly initialized the method
   * returns true.
   * 
   * @param request
   *          the request to check
   * @param response
   *          the response to redirect if necessary
   * @param redirect
   *          true if messages and redirects should be issued.
   * @return true if all session data is valid, false (and redirect) otherwise.
   */
  protected boolean isRequestValid(final HttpServletRequest request, final HttpServletResponse response,
      final boolean redirect) throws ServletException, IOException {
    // get the session, but make sure not to create one if none is already
    // available.

    final HttpSession session = request.getSession(false);
    if (session == null) {
      if (redirect) {
        redirectToLogin(request, response, "The session is not set or has expired. Please log in again.");
      }
      return false;
    } else {
      // now check the auth session. This should be set by the login page.
      final AuthSession authSession = getAuthSession(session);
      if (authSession == null) {
        if (redirect) {
          redirectToLogin(request, response, "AuthSession does not exist. Please log in before accessing this page.");
        }
        return false;
      } else if (!authSession.isLoggedIn()) {
        // the user isn't properly logged in.
        if (redirect) {
          redirectToLogin(request, response,
              "User is not authenticated or the session has expired. Please log in again.");
        }
        return false;
      } else if (session.getAttribute("databasesession") == null) {
        // the database session is not set.
        if (redirect) {
          redirectToLogin(request, response, "Database session does not exist. Please log in again.");
        }
        return false;
      } else if (session.getAttribute("interfaces") == null) {
        if (redirect) {
          redirectToLogin(request, response, "No interfaces exist in the session. Please log in again.");
        }
        return false;
      } else {
        // everything checks out. Return true.
        return true;
      }
    }
  }

  /**
   * Helper method for the isRequestValid(HttpServletRequest,
   * HttpServletResponse, boolean) method.
   * 
   * @param request
   * @param response
   * @return
   * @throws ServletException
   * @throws IOException
   */
  protected boolean isRequestValid(final HttpServletRequest request, final HttpServletResponse response)
      throws ServletException, IOException {
    return isRequestValid(request, response, true);
  }

  /**
   * Redirects to the login page via the error page showing a message of choice.
   * 
   * @param request
   * @param response
   * @param message
   * @throws ServletException
   * @throws IOException
   */
  private void redirectToLogin(final HttpServletRequest request, final HttpServletResponse response,
      final String message) throws ServletException, IOException {
    log.fine("Redirecting to LoginPage with the message: " + message);
    request.setAttribute("message", message);
    request.setAttribute("redirect", LOGIN_PAGE);
    request.getRequestDispatcher(ERROR_PAGE).forward(request, response);
  }

  /**
   * Gets an auth session from the httpsession. Null is returned if no
   * authenticated session exists.
   * 
   * @param session
   *          the session to check for an authsession.
   * @return null or the current auth session if one exists.
   */
  protected AuthSession getAuthSession(final HttpSession session) {
    final AuthSession authSession = (AuthSession) session.getAttribute("authsession");
    return authSession;
  }

  /**
   * Returns an alarminterface container if such a container exists in the
   * current session. If no container exists a new one will be created.
   * 
   * @param session
   *          the session to check for interfaces
   * @return the AlarmInterfaceContainer of this session (old or new).
   */
  protected AlarmInterfaceContainer getInterfaces(final HttpSession session) {
    AlarmInterfaceContainer interfaces;
    if (session.getAttribute("interfaces") == null) {
      log.finest("Session does not contain interfaces, creating a new interface/report model");

      final DatabaseSession databaseSession = (DatabaseSession) session.getAttribute("databasesession");
      final AuthSession authSession = getAuthSession(session);
      final AlarmProperties alarmProperties = (AlarmProperties) session.getAttribute("alarmproperties");

      // instantiate a factory and create the current alarm interfaces.
      final AlarmInterfaceFactory factory = new AlarmInterfaceFactory(databaseSession, authSession, alarmProperties);
      interfaces = factory.getAlarmInterfaces();

      session.setAttribute("interfaces", interfaces);
    } else {
      interfaces = (AlarmInterfaceContainer) session.getAttribute("interfaces");
    }

    return interfaces;
  }

  /**
   * refresh the current interface to make sure that the right stuff is shown.
   */
  protected void refreshSelectedInterface(final HttpSession session) {
    final AlarmInterfaceContainer interfaces = getInterfaces(session);
    final AlarmInterface current = interfaces.getCurrentInterface();
    if (current != null) {
      current.update();
    }
  }

  /**
   * If the currentInterface variable is set, change the selected alarm
   * interface to this id.
   * 
   * @param request
   *          the current http request that would contain the parameters
   */
  protected void selectAlarmInterface(final HttpServletRequest request) {
    // select the wanted interface.
    // NOTE: getParameter is for GET parameters. Attributes are not set by
    // tomcat! (took me a while to figure this out last time...)
    if (request.getParameter("currentInterface") != null) {
      final String currentInterface = request.getParameter("currentInterface");
      final AlarmInterfaceContainer interfaces = getInterfaces(request.getSession());

      if (!interfaces.select((String) request.getParameter("currentInterface"))) {
        request.setAttribute("message", "There is no interface with the name " + currentInterface
            + ". None was selected.");
        log.finest("Could not select " + currentInterface + " since it doesn't exist.");
      }
    }
  }

  /**
   * Cleans up the needed data and invalidates the session.
   * 
   * @param session
   *          the session to log out from.
   */
  protected void logout(final HttpSession session) {
    if (session != null) {
      session.invalidate();
    }
  }
}
