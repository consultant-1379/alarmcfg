/**
 * 
 */
package com.ericsson.eniq.alarmcfg.config;

import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * @author eheijun
 * 
 */
class AlarmEnvironment {

  private static Logger log = Logger.getLogger(AlarmEnvironment.class.getName());

  /**
   * Hide constructor
   */
  protected AlarmEnvironment() {
    super();
  }

  private static AlarmEnvironment _instance;

  public static AlarmEnvironment getInstance() {
    if (_instance == null) {
      _instance = new AlarmEnvironment();
    }
    return _instance;
  }

  /**
   * Gets env entry String value from web.xml
   * 
   * @param entry
   * @return value
   */
  public String getEnvEntryString(final String entry) {
    String value = "";

    try {
      final InitialContext context = new InitialContext();
      final Context envCtx = (Context) context.lookup("java:comp/env");
      value = (String) envCtx.lookup(entry);
    } catch (NamingException e) {
      log.severe(e.getMessage());
    } catch (Exception e) {
      log.severe(e.getMessage());
    }

    return value;
  }
}
