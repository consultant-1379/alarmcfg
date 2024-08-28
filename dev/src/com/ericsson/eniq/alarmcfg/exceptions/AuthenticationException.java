/**
 * 
 */
package com.ericsson.eniq.alarmcfg.exceptions;

/**
 * @author ecarbjo
 * 
 */
public class AuthenticationException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = -1551726920067939579L;

  public AuthenticationException(final String msg, final Exception cause) {
    super(msg, cause);
  }

  public AuthenticationException(final String msg) {
    super(msg);
  }
}
