/**
 * 
 */
package com.ericsson.eniq.alarmcfg.servlets;

import java.util.logging.Logger;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.ericsson.eniq.alarmcfg.authenticate.AuthSession;
import com.ericsson.eniq.alarmcfg.database.DatabaseSession;

/**
 * @author eheijun
 * 
 */
public class AlarmConfigurationSessionListener implements HttpSessionListener {

  private static Logger log = Logger.getLogger(AlarmConfigurationSessionListener.class.getName());

  @Override
  public void sessionCreated(final HttpSessionEvent sessionEvent) {
    final HttpSession session = sessionEvent.getSession();
    log.fine("session " + session.getId() + " created");
  }

  @Override
  public void sessionDestroyed(final HttpSessionEvent sessionEvent) {
    final HttpSession session = sessionEvent.getSession();
    final AuthSession authSession = (AuthSession) session.getAttribute("authsession");
    final DatabaseSession databaseSession = (DatabaseSession) session.getAttribute("databasesession");
    
    if (authSession != null) {
      // do final cleanup for the session.
      authSession.logout();
      log.fine("session " + session.getId() + " authSession cleaned");
    }

    if (databaseSession != null) {
      // clear database session
      databaseSession.clear();
      log.fine("session " + session.getId() + " databaseSession cleaned");
    }

    log.fine("session " + session.getId() + " destroyed");
  }

}
