/**
 * 
 */
package com.ericsson.eniq.alarmcfg.database;

import java.sql.SQLException;
import java.util.logging.Logger;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;
import com.ericsson.eniq.alarmcfg.config.AlarmProperties;
import com.ericsson.eniq.alarmcfg.exceptions.DatabaseException;
import com.ericsson.eniq.repository.ETLCServerProperties;

/**
 * @author eheijun
 * @copyright Ericsson (c) 2009
 * 
 */
public class RockfactoryDatabaseConnector implements DatabaseConnector {

  private static final String CONNECTIONNAME = "dwhrep";

  private static final String USER = "USER";

  private static Logger log = Logger.getLogger(RockfactoryDatabaseConnector.class.getName());

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.alarmcfg.database.DatabaseConnector#getDatabaseSession()
   */
  @Override
  public DatabaseSession createDatabaseSession(final ETLCServerProperties etlcServerProperties,
      final AlarmProperties alarmProperties) throws DatabaseException {

    final String dbUrl = etlcServerProperties.getProperty(ETLCServerProperties.DBURL).trim();
    final String username = etlcServerProperties.getProperty(ETLCServerProperties.DBUSERNAME).trim();
    final String password = etlcServerProperties.getProperty(ETLCServerProperties.DBPASSWORD).trim();
    final String dbDriver = etlcServerProperties.getProperty(ETLCServerProperties.DBDRIVERNAME).trim();

    if (alarmProperties == null) {
      throw new DatabaseException("Configuration failure: Invalid alarm properties");
    }

    RockFactory etlrepRock = null;
    RockFactory dwhrepRock = null;

    final String appName = alarmProperties.getProperty(AlarmProperties.APPLICATIONNAME);

    if ((username == null) || (password == null)) {
      throw new DatabaseException("Configuration failure: Invalid username or password");
    } else {
      try {
        etlrepRock = new RockFactory(dbUrl, username, password, dbDriver, appName, true);
        log.info("Etlrep connection ready");
      } catch (SQLException e) {
        log.severe(e.getMessage());
        throw new DatabaseException("SQL Connection failure: Unable to connect etlrep database", e);
      } catch (RockException e) {
        log.severe(e.getMessage());
        throw new DatabaseException("Rock Connection failure: Unable to connect etlrep database", e);
      } catch (Exception e) {
        log.severe(e.getMessage());
        throw new DatabaseException("Fatal Connection failure: Unable to connect etlrep database", e);
      }
    }

    try {
      try {
        final Meta_databases mdb = new Meta_databases(etlrepRock);
        mdb.setConnection_name(CONNECTIONNAME);
        mdb.setType_name(USER);
        final Meta_databasesFactory mdbf = new Meta_databasesFactory(etlrepRock, mdb);
        final Meta_databases dwhrepmdb = (Meta_databases) mdbf.get().get(0);
        dwhrepRock = new RockFactory(dwhrepmdb.getConnection_string(), dwhrepmdb.getUsername(),
            dwhrepmdb.getPassword(), dwhrepmdb.getDriver_name(), appName, true);
        log.info("Dwhrep connection ready");
      } finally {
        etlrepRock.getConnection().close();
      }
    } catch (SQLException e) {
      log.severe(e.getMessage());
      throw new DatabaseException("SQL Connection failure: Unable to connect dwhrep database", e);
    } catch (RockException e) {
      log.severe(e.getMessage());
      throw new DatabaseException("Rock Connection failure: Unable to connect dwhrep database", e);
    } catch (Exception e) {
      log.severe(e.getMessage());
      throw new DatabaseException("Fatal Connection failure: Unable to connect dwhrep database", e);
    }

    return new RockfactoryDatabaseSession(dwhrepRock);
  }

}
