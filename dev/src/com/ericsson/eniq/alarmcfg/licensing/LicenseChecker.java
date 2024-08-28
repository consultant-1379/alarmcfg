/**
 * 
 */
package com.ericsson.eniq.alarmcfg.licensing;

/**
 * @author ecarbjo
 * 
 */
public interface LicenseChecker {

  /**
   * Checks if the LicenseChecker can obtain a valid license from whatever
   * license system it implements.
   * 
   * @return true if a valid license can be found, false otherwize.
   */
  boolean isLicenseValid();
}
