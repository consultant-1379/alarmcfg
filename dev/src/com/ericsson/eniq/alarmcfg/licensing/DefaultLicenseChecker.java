/**
 * 
 */
package com.ericsson.eniq.alarmcfg.licensing;

import java.net.InetAddress;
import java.rmi.Naming;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ericsson.eniq.licensing.cache.DefaultLicenseDescriptor;
import com.ericsson.eniq.licensing.cache.LicenseDescriptor;
import com.ericsson.eniq.licensing.cache.LicensingCache;
import com.ericsson.eniq.licensing.cache.LicensingCacheSecurityManager;
import com.ericsson.eniq.licensing.cache.LicensingResponse;
import com.ericsson.eniq.repository.ETLCServerProperties;

/**
 * @author ecarbjo
 * 
 */
public class DefaultLicenseChecker implements LicenseChecker {

  private static Logger log = Logger.getLogger(DefaultLicenseChecker.class.getName());

  private String serverHostName = "localhost";

  private int serverPort;

  /**
   * Take the properties that contain the server port and hostname, and store
   * these values for usage in the RMI calls in isLicenseValid()
   */
  public DefaultLicenseChecker(final ETLCServerProperties props) {
    System.setSecurityManager(new LicensingCacheSecurityManager());

    if (props != null) {
      serverHostName = props.getProperty("ENGINE_HOSTNAME");
      if (this.serverHostName == null || this.serverHostName.equals("")) {

        try {
          this.serverHostName = InetAddress.getLocalHost().getHostName();
        } catch (java.net.UnknownHostException ex) {
          log.log(Level.FINER, "Could not determine hostname for localhost", ex);
          this.serverHostName = "localhost";
        }
      }

      this.serverPort = 1200;
      final String portStr = props.getProperty("ENGINE_PORT");
      if (portStr != null && !portStr.equals("")) {
        try {
          this.serverPort = Integer.parseInt(portStr);
        } catch (NumberFormatException nfe) {
          log.config("Could not parse number from string " + portStr + "\". Using default port.");
        }
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.licensing.LicenseChecker#isLicenseValid()
   */
  @Override
  public boolean isLicenseValid() {
    try {
      final LicensingCache cache = (LicensingCache) Naming.lookup("rmi://" + this.serverHostName + ":"
          + this.serverPort + "/LicensingCache");

      if (cache == null) {
        log.severe("Could connect to the LicenseManager at " + this.serverHostName
            + ". Please verify that LicenseManager service is running and try again.");
        return false;
      } else {
        // This is the CXC for the Alarm Module. (FAJ 121 1149)
        final LicenseDescriptor license = new DefaultLicenseDescriptor("CXC4010584");

        // get a licensing response for the created descriptors.
        final LicensingResponse response = cache.checkLicense(license);

        if (response.isValid()) {
          log.fine("The Alarm module license is valid: " + response.isValid() + " msg: " + response.getMessage());
          return true;
        } else {
          log.severe("Alarm module license not valid! Please check the validity of the license before proceeding.");
          return false;
        }
      }
    } catch (Exception e) {
      log
          .severe("Caught an exception while trying to validate the license for the Alarm Configuration. The fault was: "
              + e.getMessage());
      return false;
    }
  }
}
