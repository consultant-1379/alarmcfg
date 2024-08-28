package com.ericsson.eniq.alarmcfg.servlets;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ericsson.eniq.alarmcfg.exceptions.DatabaseException;
import com.ericsson.eniq.alarmcfg.interfaces.AlarmInterface;
import com.ericsson.eniq.alarmcfg.interfaces.AlarmInterfaceContainer;
import com.ericsson.eniq.alarmcfg.reports.AlarmReport;

/**
 * Servlet implementation class DeleteReport
 */
public class DeleteReport extends AlarmServlet {

  private static final long serialVersionUID = -903615335616263896L;

  private static final String EXISTING_ALARMS = "/ExistingAlarms";

  private static final String ERROR_PAGE = "/ErrorPages/ErrorPage.jsp";

  private static Logger log = Logger.getLogger(DeleteReport.class.getName());

  /**
   * @see HttpServlet#HttpServlet()
   */
  public DeleteReport() {
    super();

    log.finest("Instantiated the DeleteReport servlet");
  }

  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
      IOException {
    // initialize the forward screen
    String forward = "";

    if (isRequestValid(request, response)) {
      if (request.getParameter("id") == null) {
        // the id parameter is missing. Show an error message to the user.
        request.setAttribute("message", "The report id was not specified. Please check your URL.");
        forward = ERROR_PAGE;
      } else {
        final HttpSession session = request.getSession(false);

        // we can only disable/enable stuff from the currently selected
        // interface, so refresh the current interface and then delete the
        // report.
        refreshSelectedInterface(session);

        final AlarmInterfaceContainer interfaces = (AlarmInterfaceContainer) session.getAttribute("interfaces");
        final AlarmInterface alarmInterface = interfaces.getCurrentInterface();
        if (alarmInterface != null) {
          // do we want to enable or disable the report?
          final String reportId = request.getParameter("id");

          // iterate over all reports
          final List<AlarmReport> reports = alarmInterface.getReports();
          for (AlarmReport report : reports) {
            // we found the right one
            if (report.getId().equals(reportId)) {
              // enable/disable the report and break the loop.
              try {
                report.remove();
                request.setAttribute("infomessage", "The report " + report.getName() + " successfully removed.");
              } catch (DatabaseException e) {
                request.setAttribute("message",
                    "An error occurred while accessing the database. The action could not be completed.");
                request.setAttribute("error", e.getMessage());
                forward = ERROR_PAGE;
              }
              break;
            }
          }

          // set the message to an error message since the we didn't find a
          // matching report in the while loop above.
          if ((request.getAttribute("message") == null) && (request.getAttribute("infomessage") == null)) {
            request.setAttribute("message", "There is no report with the id " + reportId + " attached to the "
                + alarmInterface.getName() + " interface.");
          }
        }
        forward = EXISTING_ALARMS;
      }

      request.getRequestDispatcher(forward).forward(request, response);
    }
  }

}
