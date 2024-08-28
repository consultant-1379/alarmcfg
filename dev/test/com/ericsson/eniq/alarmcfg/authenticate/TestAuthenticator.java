/**
 * 
 */
package com.ericsson.eniq.alarmcfg.authenticate;

import com.ericsson.eniq.alarmcfg.exceptions.AuthenticationException;

/**
 * @author ecarbjo
 * 
 */
public class TestAuthenticator implements Authenticator {

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.alarmcfg.authenticate.Authenticator#getAuthSession(java
   * .lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public AuthSession getAuthSession(final String username, final String password, final String server, final String type)
      throws AuthenticationException {
    if (username.equals("eniq_alarm") && password.equals("eniq_alarm") && server.equals("alarm_server")
        && type.equals("ENTERPRISE")) {
      return new TestAuthSession();
    } else {
      throw new AuthenticationException("TEST");
    }
  }

}
