/**
 * 
 */
package com.ericsson.eniq.alarmcfg.authenticate;

import com.crystaldecisions.sdk.exception.SDKException;
import com.crystaldecisions.sdk.framework.CrystalEnterprise;
import com.crystaldecisions.sdk.framework.IEnterpriseSession;
import com.crystaldecisions.sdk.framework.ISessionMgr;
import com.ericsson.eniq.alarmcfg.exceptions.AuthenticationException;

/**
 * @author ecarbjo
 * 
 */
public class BOAuthenticator implements Authenticator {

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
    try {
      // Authenticate with the help of the session manager from the BO SDK, and
      // then wrap the result in a BOAuthSession object before returning
      final ISessionMgr sessionManager = CrystalEnterprise.getSessionMgr();
      final IEnterpriseSession logon = sessionManager.logon(username, password, server, type);
      if (logon == null) {
        return null;
      } else {
        return new BOAuthSession(logon);
      }
    } catch (SDKException e) {
      throw new AuthenticationException("The BO SDK session manager failed to authenticate.", e);
    }
  }

}
