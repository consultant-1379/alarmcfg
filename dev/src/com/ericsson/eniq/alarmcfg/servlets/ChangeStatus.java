package com.ericsson.eniq.alarmcfg.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.logging.Logger;

import com.ericsson.eniq.alarmcfg.exceptions.DatabaseException;
import com.ericsson.eniq.alarmcfg.interfaces.AlarmInterface;
import com.ericsson.eniq.alarmcfg.interfaces.AlarmInterfaceContainer;
import com.ericsson.eniq.alarmcfg.reports.AlarmReport;

/**
 * Servlet implementation class ChangeStatus
 */
public class ChangeStatus extends AlarmServlet {

  private static final long serialVersionUID = 1544923380526953013L;

  private static Logger log = Logger.getLogger(ChangeStatus.class.getName());

  private static final String EXISTING_ALARMS = "/ExistingAlarms";

  private static final String ERROR_PAGE = "/ErrorPages/ErrorPage.jsp";

  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
      IOException {
    // initialize the forward screen
    String forward = "";

    // if there is no interfaces container in this session, create one.
    if (isRequestValid(request, response)) {
      final HttpSession session = request.getSession(false);

      if (request.getParameter("enable") == null && request.getParameter("disable") == null) {
        // should this forward to an error page?
        forward = EXISTING_ALARMS;
      } else {
        final AlarmInterfaceContainer interfaces = getInterfaces(session);

        // we can only disable/enable stuff from the currently selected
        // interface
        final AlarmInterface alarmInterface = interfaces.getCurrentInterface();

        if (alarmInterface != null) {
          // refresh the selected interface
          refreshSelectedInterface(session);

          // do we want to enable or disable the report? We can do this check as
          // we already checked that at least one of enable/disable is not null.
          final boolean enable = (request.getParameter("enable") != null);

          final String reportId = enable ? request.getParameter("enable") : request.getParameter("disable");

          // iterate over all reports
          final List<AlarmReport> reports = alarmInterface.getReports();
          for (AlarmReport report : reports) {
            // we found the right one
            if (report.getId().equals(reportId)) {
              // enable/disable the report and break the loop.
              try {
                log.fine((enable ? "Enabling" : "Disabling") + " the report " + reportId);
                if (report.setEnabled(enable)) {
                  request.setAttribute("infomessage", "The report " + report.getName() + " successfully "
                      + (enable ? "enabled." : "disabled."));
                } else {
                  request.setAttribute("message", "The report " + report.getName() + " is already "
                      + (enable ? "enabled." : "disabled."));
                }
              } catch (DatabaseException e) {
                request.setAttribute("message",
                    "An error occurred while accessing the database. The action could not be completed.");
                request.setAttribute("error", e.getMessage());
                forward = ERROR_PAGE;

                log.warning("Caught a database exception when trying to " + (enable ? "enable" : "disable")
                    + " the report " + reportId + " (" + e.getMessage() + ")");
              }

              continue;
            }
          }
        }

        forward = EXISTING_ALARMS;
      }

      request.getRequestDispatcher(forward).forward(request, response);
    }
  }

}
