/**
 * 
 */
package com.ericsson.eniq.alarmcfg.database;

/**
 * @author eheijun
 * 
 */
public class DatabaseConnectorFactory {

  /**
   * Hide constructor
   */
  private DatabaseConnectorFactory() {
    super();
  }

  /**
   * Create connector class for dwhrep database
   * 
   * @return RockfactoryDatabaseConnector
   */
  public static DatabaseConnector createDatabaseConnector() {
    return new RockfactoryDatabaseConnector();
  }

}
