/**
 * This interface handles logging in to a session using the BO SDK, or other authentication method that returns a EnterpriseSession object.
 */
package com.ericsson.eniq.alarmcfg.authenticate;

import com.ericsson.eniq.alarmcfg.exceptions.AuthenticationException;

/**
 * @author ecarbjo
 */
public interface Authenticator {

  /**
   * A method for acquiring the current session if authentication passes. If
   * authentication fails, null or an appropriate exception should be returned.
   * 
   * @param username
   *          the username
   * @param password
   *          the password
   * @param server
   *          the server and possibly port that we need to authenticate against.
   * @param type
   *          the type of authentication if applicable
   * @return the current session, or null if the authentication failed.
   */
  AuthSession getAuthSession(String username, String password, String server, String type)
      throws AuthenticationException;
}
