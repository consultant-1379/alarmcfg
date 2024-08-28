package com.ericsson.eniq.alarmcfg.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ExistingAlarms
 */
public class ExistingAlarms extends AlarmServlet {

  private static final long serialVersionUID = -9117731761488370733L;

  private static final String EXISTING_ALARMS_JSP = "/ExistingAlarms/ExistingAlarms.jsp";

  private static Logger log = Logger.getLogger(ExistingAlarms.class.getName());

  /**
   * @see HttpServlet#HttpServlet()
   */
  public ExistingAlarms() {
    super();

    log.finest("Instantiated the ExistingAlarms servlet");
  }

  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
      IOException {
    generateContent(request, response);
  }

  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
      IOException {
    generateContent(request, response);
  }

  protected void generateContent(final HttpServletRequest request, final HttpServletResponse response)
      throws ServletException, IOException {
    final String forward = EXISTING_ALARMS_JSP;

    if (isRequestValid(request, response)) {
      // make sure that the interfaces have been read and are stored in the
      // session.
      getInterfaces(request.getSession());

      // if the current interface parameter is set, select this interface.
      selectAlarmInterface(request);

      // refresh the selected interface.
      refreshSelectedInterface(request.getSession());

      request.getRequestDispatcher(forward).forward(request, response);
    }
  }
}
