/**
 * 
 */
package com.ericsson.eniq.alarmcfg.config;

import javax.servlet.ServletContext;

import com.ericsson.eniq.alarmcfg.exceptions.AlarmConfigurationException;
import com.ericsson.eniq.repository.ETLCServerProperties;

/**
 * @author eheijun
 * @copyright Ericsson (c) 2009
 * 
 */
public interface AlarmConfiguration {

  AlarmProperties getAlarmProperties(final ServletContext context) throws AlarmConfigurationException;

  ETLCServerProperties getETLCServerProperties(final ServletContext context) throws AlarmConfigurationException;

}
