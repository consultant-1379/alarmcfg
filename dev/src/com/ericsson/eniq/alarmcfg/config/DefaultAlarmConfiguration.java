/**
 * 
 */
package com.ericsson.eniq.alarmcfg.config;

import java.io.File;

import javax.servlet.ServletContext;

import com.ericsson.eniq.alarmcfg.exceptions.AlarmConfigurationException;
import com.ericsson.eniq.repository.ETLCServerProperties;

/**
 * @author eheijun
 * @copyright Ericsson (c) 2009
 * 
 */
public class DefaultAlarmConfiguration implements AlarmConfiguration {

  private static final String ETLCSERVER_PROPERTIES = "ETLCServer.properties";

  private static final String CONF_DIR = "CONF_DIR";

  private static final String CONFFILEFOLDER = "conffilefolder";

  private static final String CONFFILE = "conffile";

  public AlarmProperties getAlarmProperties(final ServletContext context) throws AlarmConfigurationException {
    try {
      final String folder = AlarmEnvironment.getInstance().getEnvEntryString(CONFFILEFOLDER);
      final String filename = AlarmEnvironment.getInstance().getEnvEntryString(CONFFILE);
      final String filepath = context.getRealPath(File.separator + folder + File.separator + filename);
      final AlarmProperties alarmProperties = new AlarmProperties(filepath);
      return alarmProperties;
    } catch (Exception e) {
      throw new AlarmConfigurationException(e.getMessage());
    }
  }

  public ETLCServerProperties getETLCServerProperties(final ServletContext context) throws AlarmConfigurationException {
    try {
      final String folder = System.getProperty(CONF_DIR);
      final String filename = ETLCSERVER_PROPERTIES;
      final String filepath = File.separator + folder + File.separator + filename;
      final ETLCServerProperties etlcserverProperties = new ETLCServerProperties(filepath);
      return etlcserverProperties;
    } catch (Exception e) {
      throw new AlarmConfigurationException(e.getMessage());
    }
  }

}
