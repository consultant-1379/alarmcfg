/**
 * 
 */
package com.ericsson.eniq.alarmcfg.reports;

import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import com.distocraft.dc5000.repository.dwhrep.Alarmreport;
import com.ericsson.eniq.alarmcfg.common.Constants;
import com.ericsson.eniq.alarmcfg.database.DatabaseSession;
import com.ericsson.eniq.alarmcfg.exceptions.DatabaseException;
import com.ericsson.eniq.alarmcfg.interfaces.AlarmInterface;
import com.ericsson.eniq.alarmcfg.prompts.AlarmReportPrompt;

/**
 * This adapter implements the common elements of the BOAlarmReport and the DWHAlarmReport classes.
 * 
 * @author ecarbjo
 */
public abstract class AlarmReportAdapter implements AlarmReport {

  /*
   * First we have the common methods that are implemented the same way for both BOAlarmReports and the DWHAlarmReports
   */
  protected static final String INACTIVE = "INACTIVE";

  protected static final String ACTIVE = "ACTIVE";

  protected static final String dateformat = "yyyy-MM-dd hh:mm:ss";

  private static Logger log = Logger.getLogger(AlarmReportAdapter.class.getName());

  protected DatabaseSession databaseSession;

  protected Vector<AlarmReportPrompt> prompts = new Vector<AlarmReportPrompt>();

  protected String id;

  protected AlarmInterface alarmInterface;

  protected String name;

  protected String status;

  protected String baseTable = null;
  
  protected Boolean isReducedDelay;

  // we default this to true
  protected boolean valid = true;

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.reports.AlarmReport#isValid()
   */
  @Override
  public boolean isValid() {
    return this.valid;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.reports.AlarmReport#setValid(boolean)
   */
  @Override
  public void setValid(final boolean valid) {
    this.valid = valid;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.reports.AlarmReport#getId()
   */
  @Override
  public String getId() {
    return this.id;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.reports.AlarmReport#getInterface()
   */
  @Override
  public AlarmInterface getInterface() {
    return this.alarmInterface;
  }

  /*
   * (non-Javadoc)
   * @see com.ericsson.eniq.alarmcfg.reports.AlarmReport#isReducedDelay()
   */
  @Override
  public Boolean isReducedDelay() {
    return this.isReducedDelay;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.reports.AlarmReport#getName()
   */
  @Override
  public String getName() {
    return this.name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.reports.AlarmReport#isEnabled()
   */
  @Override
  public boolean isEnabled() {
    return status.equalsIgnoreCase(ACTIVE);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.reports.AlarmReport#setEnabled(boolean)
   */
  @Override
  public boolean setEnabled(final boolean enable) throws DatabaseException {
    if (!isEnabled() && enable) {
      this.status = ACTIVE;
      saveStatus();
      return true;
    } else if (isEnabled() && !enable) {
      this.status = INACTIVE;
      saveStatus();
      return true;
    }
    return false;
  }

  /**
   * Set the name of this report.
   * 
   * @param name
   *          the name of the report.
   */
  public void setName(final String name) {
    this.name = name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.reports.AlarmReport#setBaseTable(java.lang.String )
   */
  @Override
  public void setBaseTable(final String tableName) {
    this.baseTable = tableName;
  }

  /*
   * And below we have the abstract methods that this adapter doesn't implement.
   * ---------------------------------------------------------------------------
   */

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.reports.AlarmReport#getPrompts()
   */
  @Override
  public abstract Enumeration<AlarmReportPrompt> getPrompts();

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.reports.AlarmReport#update()
   */
  @Override
  public abstract void update();

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.reports.AlarmReport#save()
   */
  @Override
  public void save() throws DatabaseException {
    if (databaseSession != null) {
      try {
        Alarmreport rockAlarmreport = databaseSession.getAlarmreport(this.id);
        if (rockAlarmreport == null) {
          final Hashtable<String, String> newParameters = new Hashtable<String, String>();

          // save the base table name into the parameters if it is set.
          if (baseTable != null) {
            newParameters.put(Constants.BASETABLE_NAME, baseTable);
          }

          final Enumeration<AlarmReportPrompt> parameters = prompts.elements();
          while (parameters.hasMoreElements()) {
            final AlarmReportPrompt parameter = parameters.nextElement();
            // if (parameter.isSettable()) {
            if (parameter.getValue() == null) {
              parameter.setValue("");
            }
            newParameters.put(parameter.getName(), parameter.getValue());
            // }
          }
          
          rockAlarmreport = databaseSession.createAlarmreport(this.alarmInterface.getId(), this.id, this.name,
              this.status, alarmInterface.isReducedDelay() || this.isReducedDelay, newParameters);
        } else {
          databaseSession.saveAlarmreport(rockAlarmreport);
        }
        log.info("Alarmreport " + rockAlarmreport.getReportid() + " saved successfully");
      } catch (DatabaseException e) {
        log.severe("Database error: " + e.getMessage());
      } catch (Exception e) {
        log.severe("Fatal error: " + e.toString() + "(" + e.getMessage() + ")");
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.reports.AlarmReport#remove()
   */
  @Override
  public abstract void remove() throws DatabaseException;

  /**
   * Save the status variable to the database.
   */
  protected abstract void saveStatus();

  /**
   * Check the alarm structure
   * 
   * @return true if the structure is ok, false if not
   */
  public boolean checkAlarmStructure() {

    int i;
    String name;
    boolean found;
    if (prompts == null) {
      log.fine("Prompts is null");
      return true;
    }
    Enumeration<AlarmReportPrompt> parameters = prompts.elements();
    if (parameters != null) {
      while (parameters.hasMoreElements()) {
        final AlarmReportPrompt parameter = parameters.nextElement();
        if (parameter != null) {
          name = parameter.getName();
          found = false;
          // Check if this name is found in the valid names list
          for (i = 0; i < validNames.length; ++i) {
            if (name.equals(validNames[i])) {
              found = true;
            }
          }
          if (!found) {
            // This parameter is not in valid list, alarm structure is invalid
            log.fine("Parameter " + name + " not found in the valid names list");
            return false;
          }
        }
      }
    } else {
      log.fine("Parameters is null");
      return true;
    }
    // At this point all the prompt names are found in valid list
    for (i = 0; i < validNames.length; ++i) {
      // parameters is not null if this is reached
      parameters = prompts.elements();
      found = false;
      // Check if this valid name item is found in the alarm report
      while (parameters.hasMoreElements()) {
        final AlarmReportPrompt parameter = parameters.nextElement();
        if (parameter != null) {
          name = parameter.getName();
          if (name.equals(validNames[i])) {
            found = true;
          }
        }
      }
      if (!found) {
        // This valid list name is not found with parameters, alarm structure is invalid
        log.fine("Prompt name " + validNames[i] + " not found in the alarm prompts");
        return false;
      }
    }
    // At this point all the valid list items are found in the prompt names
    return true;
  }

  public boolean checkAlarmContent() {
    // This function checks if the report variables have ok values
    boolean found, valid = true;
    String name, value;
    int i, index = -1, intValue = 0;
    if (prompts == null) {
      log.fine("Prompts is null");
      return true;
    }
    final Enumeration<AlarmReportPrompt> parameters = prompts.elements();
    if (parameters != null) {
      while (parameters.hasMoreElements()) {
        final AlarmReportPrompt parameter = parameters.nextElement();
        if (parameter != null) {
          name = parameter.getName();
          value = parameter.getValue();
          log.fine("Checking name " + name + " and value " + value);
          found = false;
          for (i = 0; i < validNames.length; ++i) {
            if (name.equals(validNames[i])) {
              index = i;
              found = true;
            }
          }
          if (found) {
            log.fine("Parameter " + name + " found in the valid names list at position " + index
                + ", checking its value content");
            switch (index) {
            case 3: // 04_SpecificProblems
            case 5: // 06_PerceivedSeverity
            case 18: // 19_AlarmCriteria
              try {
                intValue = Integer.parseInt(value);
              } catch (Exception e) {
                log.fine("Invalid integer content " + value);
                valid = false;
              }
              if (index == 5) {
                // 06_PerceivedSeverity
                if (intValue < 0 || intValue > 5) {
                  log.fine("Illegal 06_PerceivedSeverity value " + intValue);
                  valid = false;
                }
              } else if (index == 18) {
                // 19_AlarmCriteria
                if (intValue < 0 || intValue > 1) {
                  log.fine("Illegal 19_AlarmCriteria value " + intValue);
                  valid = false;
                }
              }
              break;
            case 15: // 16_MonitoredAttributeValues
              try {
                Float.parseFloat(value);
              } catch (Exception e) {
                log.fine("Invalid float content " + value);
                valid = false;
              }
              break;
            case 10: // 11_CurrentTime
            case 13: // 14_EventTime
              try {
                final SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
                sdf.parse(value);
              } catch (Exception e) {
                log.fine("Invalid date format content " + value + ", format must be " + dateformat);
                valid = false;
              }
              break;
            }
          } else {
            // This parameter is not in the valid list
            log.fine("Parameter " + name + " not found in the valid names list");
          }
        } else {
          log.fine("Parameter is null");
        }
      }
    } else {
      log.fine("Parameters is null");
    }
    return valid;
  }

  /**
   * This is the valid structure of alarm reports
   */
  private static String[] validNames = new String[] { "01_ManagedObjectClass", "02_ETText", "03_PCText",
      "04_SpecificProblems", "05_SPText", "06_PerceivedSeverity", "07_PerceivedSeverityText",
      "08_ThresholdInformation", "09_AdditionalText", "10_AdditionalInformation", "11_CurrentTime",
      "12_ManagedObjectInstance", "13_ObjectOfReference", "14_EventTime", "15_MonitoredAttributes",
      "16_MonitoredAttributeValues", "17_Variable", "18_Threshold", "19_AlarmCriteria", "20_OssName", "21_Title" };
}
