/**
 * 
 */
package com.ericsson.eniq.alarmcfg.exceptions;

/**
 * @author ecarbjo
 * 
 */
public class DatabaseException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = -3140182723882312265L;

  public DatabaseException(final String msg, final Exception cause) {
    super(msg, cause);
  }

  public DatabaseException(final String msg) {
    super(msg);
  }
}
