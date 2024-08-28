/**
 * 
 */
package com.ericsson.eniq.alarmcfg.authenticate;

/**
 * @author ecarbjo This class is an factory method pattern for creating the
 *         authenticator that we need.
 */
public class AuthenticatorFactory {

  public static final int BO_AUTH = 1;

  private static int currentType = BO_AUTH;

  /**
   * Factory method for getting a type of authenticator. Easily changed by tests
   * for example.
   * 
   * @return a new Authenticator.
   */
  public static Authenticator getAuthenticator() {
    return getAuthenticator(currentType);
  }

  /**
   * Factory method for getting a given type of authenticator.
   * 
   * @param type
   *          the type of authenticator that is wanted
   * @return a new authenticator
   */
  public static Authenticator getAuthenticator(final int type) {
    switch (type) {
    case BO_AUTH:
    default:
      return new BOAuthenticator();
    }
  }

  /**
   * Change the type of authenticator that should be created.
   * 
   * @param type
   */
  public static void setAuthenticatorType(final int type) {
    switch (type) {
    case BO_AUTH:
      currentType = type;
      break;
    }
  }
}
