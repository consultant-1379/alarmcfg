/**
 * 
 */
package com.ericsson.eniq.alarmcfg.config;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author eheijun
 * @copyright Ericsson (c) 2009
 * 
 */
public class AlarmProperties {

  private static Logger log = Logger.getLogger(AlarmProperties.class.getName());

  public final static String APPLICATIONNAME = "appName";

  public static final String BOALARMCATEGORY = "alarmCategory";

  public static final String BOALARMCATEGORYRD = "alarmCategoryRD";;

  final private Properties properties;

  public AlarmProperties() {
    this.properties = new Properties();
  }

  /**
   * @param file
   *          path
   */
  public AlarmProperties(final String filepath) {
    this();

    final File f = new File(filepath);

    if (f.exists() && f.isFile() && f.canRead()) {
      try {
        final FileInputStream fis = new FileInputStream(f);
        try {
          this.properties.load(fis);
        } finally {
          fis.close();
        }
      } catch (Exception e) {
        log.severe(e.getMessage());
      }
    }
  }

  /**
   * @param propertyName
   * @return
   */
  public String getProperty(final String propertyName) {
    return properties.getProperty(propertyName, "");
  }

  /**
   * @param propertyName
   * @param propertyValue
   */
  public void addProperty(final String propertyName, final String propertyValue) {
    properties.setProperty(propertyName, propertyValue);
  }

}
