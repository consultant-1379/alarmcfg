/**
 * 
 */
package com.ericsson.eniq.alarmcfg.database;

import com.ericsson.eniq.alarmcfg.config.AlarmProperties;
import com.ericsson.eniq.alarmcfg.exceptions.DatabaseException;
import com.ericsson.eniq.repository.ETLCServerProperties;

/**
 * @author eheijun
 * @copyright Ericsson (c) 2009
 * 
 */
public interface DatabaseConnector {

  DatabaseSession createDatabaseSession(ETLCServerProperties etlcServerProperties, AlarmProperties alarmProperties)
      throws DatabaseException;

}
