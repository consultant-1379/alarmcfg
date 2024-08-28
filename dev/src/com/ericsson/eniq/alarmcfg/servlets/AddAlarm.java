package com.ericsson.eniq.alarmcfg.servlets;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ericsson.eniq.alarmcfg.common.Constants;
import com.ericsson.eniq.alarmcfg.common.PromptNameValuePair;
import com.ericsson.eniq.alarmcfg.exceptions.DatabaseException;
import com.ericsson.eniq.alarmcfg.prompts.AlarmReportPrompt;
import com.ericsson.eniq.alarmcfg.reports.AlarmReport;

/**
 * Adds an alarm that has been configured via the AddReport servlet.
 * 
 * @author ecarbjo
 */
public class AddAlarm extends AlarmServlet {

  private static final long serialVersionUID = 4560872565170307540L;

  private static Logger log = Logger.getLogger(AddAlarm.class.getName());

  protected static final String dateformat = "yyyy-MM-dd hh:mm:ss";
  
  private String alarmVerificationMessage = "";
  
  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
      IOException {
    // if there is no interfaces container in this session, create one.
    final HttpSession session = request.getSession();

    if (isRequestValid(request, response)) {
      // get the report.
      final AlarmReport report = (AlarmReport) session.getAttribute("addreport");

      if (report == null) {
        request.setAttribute("message", "Could not add the empty report since the session data is incomplete");
        log.warning("Session data is incomplete. addreport value is missing.");
      } else {
        // get the prompts and fill them in.

        // TODO: Add verification and SQL sanitation.
        final Enumeration<AlarmReportPrompt> promptElements = report.getPrompts();

        // fetch all the prompts from the form 
        while (promptElements.hasMoreElements()) {
          final AlarmReportPrompt currentPrompt = promptElements.nextElement();
          if (request.getParameter("prompt_" + currentPrompt.getName()) != null) {
            final String promptStr = ((String) request.getParameter("prompt_" + currentPrompt.getName())).trim();
            if (promptStr != null) {
              currentPrompt.setValue(promptStr);
            } else {
              currentPrompt.setValue("");
            }
          }
        }
        
        // lmfakos: alarm structure verification starts
        final List<PromptNameValuePair> nameValuePairs = new ArrayList<PromptNameValuePair>();
        final Enumeration<String> paramNames = request.getParameterNames();
        
        while (paramNames.hasMoreElements()) {
          String paramName = ((String) paramNames.nextElement()).trim();
           final String paramValue = ((String) request.getParameter(paramName)).trim();
               if (paramName.startsWith("promptValue_")) {
              paramName = paramName.substring(12);
              // prompt needs values as String table
              final String[] paramValues = paramValue.split(Constants.PROMPT_SEPARATOR);
              final PromptNameValuePair nameValuePair = new PromptNameValuePair(paramName, paramValues);
              if(!paramName.equalsIgnoreCase("password")) {
            	  log.finest("prompt " + paramName + " has value " + paramValues);
              }
              nameValuePairs.add(nameValuePair);
          }
        }

        String xmlView = "";
        try {
        	xmlView = report.refresh(nameValuePairs);
        	log.finest("Refreshed report: " + xmlView);
        }
        catch(Exception ex) {
        	log.info("Alarm verification failed: Unable to read xml content from BO server" + ex);
            request.setAttribute("message", "Alarm verification failed: Unable to read xml content from BO server");
            request.getRequestDispatcher("ExistingAlarms").forward(request, response);
            return;
        }
        alarmVerificationMessage = "";
        if(!checkAlarm(xmlView)) {
        	log.info("Alarm verification failed: " + alarmVerificationMessage);
            request.setAttribute("message", "Alarm verification failed: " + alarmVerificationMessage);
            request.getRequestDispatcher("ExistingAlarms").forward(request, response);
            return;
            // lmfakos: alarm structure verification ends
        }
        else {

        	// and now save the basetable.
        	report.setBaseTable(((String) request.getParameter("select_basetables")).trim());

        	try {
        		report.save();
        	} catch (DatabaseException e) {
        		log.severe("Caught a database exception when trying to save the newly created alarm association for report "
        				+ report.getName());
        		request.setAttribute("message",
        		"Could not save the alarm configuration because of an internal database exception");
        	}
        }
      }

      response.sendRedirect("ExistingAlarms");
    }
  }

  private boolean checkAlarm(final String xmlString) {
	  // Check through the xml string of the alarm parameters and check them so that
	  // alarm reports can be later parsed ok
		boolean valid = true;
		final int i1 = xmlString.indexOf("DCDATA_START");
		//final i9=xmlString.indexOf("ct_what");
		log.finest("checkAlarm DCDATA_START: " + i1);
		int intValue = -1;
		if (i1 > 0) {
		  final String pvm = nextTag(xmlString, i1, "CurrentTime");
		  log.finest("checkAlarm pvm: " + pvm);
			if (pvm != null) {
				log.fine("checkAlarm CurrentTime: " + pvm);
				try {
				  final SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
					sdf.parse(pvm);
					log.finest("checkAlarm sdf.parse(pvm);: " + sdf.parse(pvm));
				} catch (Exception e) {
					alarmVerificationMessage += "Invalid CurrentTime format content " + pvm + ", format must be " + dateformat;
					valid = false;
				}
			}
			else {
				alarmVerificationMessage += "CurrentTime data missing in alarm";
				valid = false;
				
			}
			final String eventTime = nextTag(xmlString, i1, "EventTime");
			if (eventTime != null) {
				log.fine("checkAlarm EventTime: " + eventTime);
				try {
				  final SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
					sdf.parse(eventTime);
				} catch (Exception e) {
					alarmVerificationMessage +=  "Invalid EventTime format content " + eventTime + ", format must be " + dateformat;
					valid = false;
				}
			}
			final String specificProblems = nextTag(xmlString, i1, "SpecificProblems");
			log.fine("checkAlarm specificProblems: " + specificProblems);
			if (specificProblems != null) {
				log.fine("checkAlarm specificProblems: " + specificProblems);
				try {
					intValue = Integer.parseInt(specificProblems);
				} catch (Exception e) {
					alarmVerificationMessage +=  "Invalid SpecificProblems integer content " + specificProblems;
					valid = false;
				}
			}
			else {
				alarmVerificationMessage += "SpecificProblems data missing in alarm";
				valid = false;
				
			}
			intValue = -1;
			final String perceivedSeverity = nextTag(xmlString, i1, "PerceivedSeverity");
			log.fine("checkAlarm perceivedSeverity: " + perceivedSeverity);
			if (perceivedSeverity != null) {
				log.fine("checkAlarm PerceivedSeverity: " + perceivedSeverity);
				try {
					intValue = Integer.parseInt(perceivedSeverity);
					log.fine("intValue PerceivedSeverity: " + intValue);
				} catch (Exception e) {
					alarmVerificationMessage +=  "Invalid PerceivedSeverity integer content " + perceivedSeverity;
					valid = false;
				}
				if (intValue < 0 || intValue > 5) {
					alarmVerificationMessage +=  "Illegal PerceivedSeverity value " + intValue;
					valid = false;
				}
			} 
			else {
				alarmVerificationMessage += "PerceivedSeverity data missing in alarm";
				valid = false;
			}
			intValue = -1;
			final String AlarmCriteria = nextTag(xmlString, i1, "AlarmCriteria");
			if (AlarmCriteria != null) {
				log.fine("checkAlarm AlarmCriteria: " + AlarmCriteria);
				try {
					intValue = Integer.parseInt(AlarmCriteria);
				} catch (Exception e) {
					alarmVerificationMessage +=  "Invalid AlarmCriteria integer content " + AlarmCriteria;
					valid = false;
				}
				if (intValue < 0 || intValue > 1) {
					alarmVerificationMessage +=  "Illegal AlarmCriteria value " + intValue;
					valid = false;
				}
			} 
			final String MonitoredAttributeValues = nextTag(xmlString, i1, "MonitoredAttributeValues");
			if (MonitoredAttributeValues != null) {
				log.fine("checkAlarm MonitoredAttributeValues: " + MonitoredAttributeValues);
				try {
					Float.parseFloat(MonitoredAttributeValues);
				} catch (Exception e) {
					alarmVerificationMessage +=  "Invalid MonitoredAttributeValues float content " + MonitoredAttributeValues;
					valid = false;
				}
			} 
		}
		else {
			alarmVerificationMessage +=  "Invalid alarm structure, DCDATA_START missing";
			valid = false;
		}
		return valid;
	}

	private static String nextTag(final String xmlString, final int i1, final String tag) {
		// This function searches the next tagValue field of xml tag. The structure of xml document is:
		// ... <ct what="weblink" >tag</ct> ... <ct what="weblink">tagValue</ct> ...
		// i1                      i2           i3                i4       i5
		int i2, i3, i4, i5,i6;
		i6=xmlString.indexOf("<ct what", i1);
			log.finest("XmlString : " + xmlString);
		log.finest("i6: " + i6);
		log.finest("tag: " + tag);
		String value = null;
		i2 = xmlString.indexOf(tag, i6);
		log.finest("Tag i2: " + i2);
		
		if (i2> 0) {
			i3 = xmlString.indexOf("<ct what", i2);
			log.finest("Tag i3: " + i3);
			if (i3 > 0) {
				i4 = xmlString.indexOf(">", i3 + 1);
				log.finest("Tag i4: " + i4);
				if (i4 > 0) {
					i5 = xmlString.indexOf("<", i4);
					log.finest("Tag i5: " + i5);
					try {
						value = xmlString.substring(i4 + 1, i5);
						log.finest("Value: " + value);
					}
					catch (Exception e) {
						log.info("Problem reading tag value for tag: " + tag);
						value = null;
					}
				}
			}
		}
		return value;
	}
}
