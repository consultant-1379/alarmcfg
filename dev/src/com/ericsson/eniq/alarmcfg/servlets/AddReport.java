package com.ericsson.eniq.alarmcfg.servlets;

import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ericsson.eniq.alarmcfg.config.AlarmProperties;
import com.ericsson.eniq.alarmcfg.database.DatabaseSession;
import com.ericsson.eniq.alarmcfg.interfaces.AlarmInterfaceContainer;
import com.ericsson.eniq.alarmcfg.reports.AlarmReport;
import com.ericsson.eniq.alarmcfg.reports.AlarmReportLister;
import com.ericsson.eniq.alarmcfg.techpacks.DWHInfoFactory;
import com.ericsson.eniq.alarmcfg.techpacks.DefaultDWHInfoFactory;

/**
 * Servlet implementation class AddReport
 */
public class AddReport extends AlarmServlet {

  private static final String CHOOSE_ALARMS_JSP = "/AddReport/ChooseAlarm.jsp";

  private static final String CHOOSE_PARAMETERS_JSP = "/AddReport/SetAlarmParameters.jsp";

  /**
   * 
   */
  private static final long serialVersionUID = 3517280733456241792L;

  private static Logger log = Logger.getLogger(AddReport.class.getName());

  /**
   * @see HttpServlet#HttpServlet()
   */
  public AddReport() {
    super();

    log.finest("Instantiated the AddReport servlet");
  }

  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
      IOException {

    if (isRequestValid(request, response)) {
      String forward = CHOOSE_ALARMS_JSP;

      // if there is no interfaces container in this session, create one.
      final HttpSession session = request.getSession();
      final AlarmInterfaceContainer interfaces = getInterfaces(session);

      log.finest("Processing GET request.");

      // make sure that the alarm interface is selected if requested.
      selectAlarmInterface(request);

      if (!interfaces.getCurrentInterface().isRealInterface()) {
        request.setAttribute("message", "Please select a real interface before adding a new report.");

        // tell the view that it should not allow selection before the user has
        // selected a real interface
        request.setAttribute("disableSelection", true);

        log.finest("Disabling adding of reports until a real interface has been selected.");

        request.getRequestDispatcher(forward).forward(request, response);
      } else {
        final DatabaseSession dbSession = (DatabaseSession) session.getAttribute("databasesession");

        if (request.getParameter("add") == null) {
          selectAlarms(session, dbSession, interfaces);

          // and set the forward page to step two of the addition process.
          forward = CHOOSE_ALARMS_JSP;

          log.finest("Forwarding to the alarm report selection, the real interface "
              + interfaces.getCurrentInterface().getName() + " has been selected.");
        } else {
          int reportIndex;
          try {
            reportIndex = Integer.parseInt(request.getParameter("add"));
          } catch (NumberFormatException e) {
            reportIndex = -1;
          }

          // we have already selected a report and now want to chose the basic
          // parameters for it.
          // Causes problems with corbetura?
          // @SuppressWarnings (value="unchecked")
          final Vector<AlarmReport> reports = (Vector<AlarmReport>) session.getAttribute("reportlist");

          // check that all the input is valid.
          if (reports == null || reports.size() < 1) {
            // the reports could not be found. Show a warning and forward to the
            // start page.
            request.setAttribute("message", "Found no reports to configure!");
            log.warning("Trying to configure a report even though none exist.");

            selectAlarms(session, dbSession, interfaces);
            forward = CHOOSE_ALARMS_JSP;
          } else if (reportIndex < 0 || reports.size() < reportIndex) {
            // the report index is faulty. Show an error.
            log.warning("Trying to access a report with id " + reportIndex
                + " even though the numer of reports is only " + reports.size());
            request.setAttribute("message", "Failed to configure a report with the invalid id " + reportIndex);

            selectAlarms(session, dbSession, interfaces);
            forward = CHOOSE_ALARMS_JSP;
          } else {
            // the input checks out. Now save the data we need for the view.
            final DWHInfoFactory factory = new DefaultDWHInfoFactory(dbSession);
            session.setAttribute("reportparameters", factory.getInformation());

            session.setAttribute("addreport", reports.get(reportIndex));

           	// now we want the user to specify the user parameters.
           	forward = CHOOSE_PARAMETERS_JSP;
           	log.finest("Forwarding to the parameters selection since the alarm report id has been set.");
          }
        }
        request.getRequestDispatcher(forward).forward(request, response);
      }
    }
  }

  /**
   * The logic for getting the available reports. (and saving them into the
   * "reportlist" attribute)
   * 
   * @param session
   *          the current session
   * @param dbSession
   *          the db interface
   * @param interfaces
   *          the alarm interface data structure.
   * @return true if the session data is intact and we can show the alarm
   *         reports, false otherwise.
   */
  protected boolean selectAlarms(final HttpSession session, final DatabaseSession dbSession,
      final AlarmInterfaceContainer interfaces) {

    session.removeAttribute("reportlist");
    
    // fetch the alarm category.
    final AlarmProperties alarmProperties = (AlarmProperties) session.getAttribute("alarmproperties");
    if (alarmProperties == null) {
      log.warning("Alarm properties is null!");
      return false;
    } else {
      final String alarmCategory = alarmProperties.getProperty(AlarmProperties.BOALARMCATEGORY);
      final String alarmCategoryRD = alarmProperties.getProperty(AlarmProperties.BOALARMCATEGORYRD);

      // get the report lister and retrieve a list of all reports that are
      // available. Scheduled interfaces can have both types of reports. 
      
      final List<AlarmReport> descriptors;
      if (interfaces.getCurrentInterface().isReducedDelay()) {
        final AlarmReportLister lister = getAuthSession(session).getAlarmReportLister(dbSession,
            interfaces.getCurrentInterface(), new String [] {alarmCategoryRD}, new Boolean [] {false});
        descriptors = lister.getAvailableReports();
      } else {
        final AlarmReportLister lister = getAuthSession(session).getAlarmReportLister(dbSession,
            interfaces.getCurrentInterface(), new String [] {alarmCategory, alarmCategoryRD}, new Boolean [] {false, true});
        descriptors = lister.getAvailableReports();
      }
      
      // log a message if there are no reports.
      if (descriptors == null || descriptors.size() < 1) {
        log.finer("No reports returned from the lister.");
        return false;
      }

      // save the descriptors in the session
      session.setAttribute("reportlist", descriptors);
      return true;
    }
  }
}
