package com.ericsson.eniq.alarmcfg.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ericsson.eniq.alarmcfg.techpacks.BaseTable;
import com.ericsson.eniq.alarmcfg.techpacks.ParameterDescriptor;
import com.ericsson.eniq.alarmcfg.techpacks.TableLevel;
import com.ericsson.eniq.alarmcfg.techpacks.TechPack;
import com.ericsson.eniq.alarmcfg.techpacks.TechPackType;

/**
 * Servlet implementation class AjaxReport
 */
public class AjaxReport extends AlarmServlet {

  private static final long serialVersionUID = 1L;

  private final static Logger log = Logger.getLogger(AjaxReport.class.getName());

  /**
   * @see HttpServlet#doGet(HttpServletRequest request,
   *      HttpServletResponseresponse)
   */
  /**
   * This get method will output a list of <option> HTML tags from the session
   * data corresponding to the given "path". This path is a series of numbers
   * separated by dashes. The numbers correspond to the vector id of the report
   * data structure. For example a path of "1-3-10" will return option tags for
   * the base tables in tech pack id 1, type name id 3 and table level id 10.
   */
  protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
      IOException {
    // check that the session is authed properly, and that it contains the
    // needed data.
    // do a silent request for checking the session
    if (isRequestValid(request, response, false)) {
      final HttpSession session = request.getSession(false);
      if (request.getParameter("path") != null && session.getAttribute("reportparameters") != null) {
        // get the session parameters, and split the path at "-"
        final String path = (String) request.getParameter("path");
        final Vector<TechPack> parameters = (Vector<TechPack>) session.getAttribute("reportparameters");
        final PrintWriter out = response.getWriter();

        if (path.length() == 0) {
          // we have a path of length 0. This means we want data for the tech
          // packs.
          printOptions(parameters, out);
        } else {
          try {
            // we have data in the path, split it so we can check individual
            // components.
            final String[] pathParts = path.split("-");
            if (pathParts.length > 0) {
              // a path length > 0 means that we want to print info for the tech
              // pack that has id pathParts[0]
              final int typeIndex = Integer.parseInt(pathParts[0]);

              // fetch the types.
              final Vector<TechPackType> types = parameters.get(typeIndex).getTypes();

              // check if we need to go further down the tree.
              if (pathParts.length > 1) {

                // a path length > 1 means that we want to print info for the
                // type name that has id pathParts[1]
                final int levelIndex = Integer.parseInt(pathParts[1]);

                // fetch the levels.
                final Vector<TableLevel> levels = types.get(levelIndex).getTableLevels();

                if (pathParts.length > 2) {
                  // a path length > 2 means that we want to print info for the
                  // table level that has id pathParts[2]
                  final int baseIndex = Integer.parseInt(pathParts[2]);

                  // fetch the base tables.
                  final Vector<BaseTable> tables = levels.get(baseIndex).getBaseTables();

                  // length == 3 (or more). print the base tables.
                  printOptions(tables, out);
                } else {
                  // length == 2, print the levels.
                  printOptions(levels, out);
                }
              } else {
                printOptions(types, out);
              }
            } else {
              log.finer("The path of length " + pathParts.length + " was ignored. Nothing returned.");
            }
          } catch (Exception e) {
            log.warning("An exception was caught while trying to return selection box data for the path "
                + request.getParameter("path"));
          }
        }
      } else {
        log.finest("Returning empty contents to AJAX report request since session data incomplete.");
        log.finest("Path: " + request.getParameter("path") + ", Report data: "
            + session.getAttribute("reportparameters"));
      }
    } else {
      log.finest("Returning empty contents to AJAX report request since session not authenticated.");
    }
  }

  /**
   * This method prints a list of option HTML tags into the default writer.
   * 
   * @param descriptors
   *          a vector of parameter descriptors that we are about to print
   * @param out
   *          the printwriter to write to.
   */
  protected void printOptions(final Vector<? extends ParameterDescriptor> descriptors, final PrintWriter out) {
    final Enumeration<? extends ParameterDescriptor> elements = descriptors.elements();
    while (elements.hasMoreElements()) {
      final ParameterDescriptor current = elements.nextElement();
      // out.println("<option>" + current.getName() + "</option>");
      out.println(current.getName());
    }
  }

}
