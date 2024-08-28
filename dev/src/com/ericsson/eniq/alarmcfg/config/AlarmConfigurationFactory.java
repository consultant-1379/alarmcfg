/**
 * 
 */
package com.ericsson.eniq.alarmcfg.config;

/**
 * @author eheijun
 * 
 */
public class AlarmConfigurationFactory {

  /**
   * Hide constructor
   */
  protected AlarmConfigurationFactory() {
    super();
  }

  public static AlarmConfiguration getAlarmConfiguration() {
    return new DefaultAlarmConfiguration();
  }
}
