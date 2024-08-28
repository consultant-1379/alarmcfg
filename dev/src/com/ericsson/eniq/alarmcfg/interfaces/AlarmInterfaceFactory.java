/**
 * 
 */
package com.ericsson.eniq.alarmcfg.interfaces;

import java.util.List;
import java.util.logging.Logger;

import com.distocraft.dc5000.repository.dwhrep.Alarminterface;
import com.ericsson.eniq.alarmcfg.authenticate.AuthSession;
import com.ericsson.eniq.alarmcfg.config.AlarmProperties;
import com.ericsson.eniq.alarmcfg.database.DatabaseSession;
import com.ericsson.eniq.alarmcfg.exceptions.DatabaseException;

/**
 * @author ecarbjo
 * 
 */
public class AlarmInterfaceFactory {

  private static Logger log = Logger.getLogger(AlarmInterfaceFactory.class.getName());

  // current database session
  final private DatabaseSession databaseSession;

  final private AuthSession authSession;

  final private AlarmProperties alarmProperties;

  // if the class is being unit-tested, change this to whatever we want
  // returned.
  public static AlarmInterfaceContainer returnContainer = null;

  /**
   * @param databaseSession
   *          the database session to create the interfaces and reports from.
   * @param authSession
   *          the authenticated session that contains session data.
   */
  public AlarmInterfaceFactory(final DatabaseSession databaseSession, final AuthSession authSession,
      final AlarmProperties alarmProperties) {
    this.databaseSession = databaseSession;
    this.authSession = authSession;
    this.alarmProperties = alarmProperties;
  }

  /**
   * @return a new AlarmInterfaceContainer with all interfaces from the
   *         database.
   */
  public AlarmInterfaceContainer getAlarmInterfaces() {
    if (returnContainer != null) {
      System.out.println("WARNING: RETURNING PRESET ALARMINTERFACECONTAINER!");
      // we are doing a unit test if this variable is set.
      final AlarmInterfaceContainer alarms = returnContainer;
      returnContainer = null;
      return alarms;
    }

    // create the alarm interface container.
    final AlarmInterfaceContainer alarms = new DefaultAlarmInterfaceContainer();

    if (databaseSession == null) {
      log.warning("DatabaseSession does not exits");
    } else {
      try {
        final List<Alarminterface> rockAlarminterfaces = databaseSession.getAlarminterfaces();
        if (rockAlarminterfaces != null) {
          // create interface objects for the given results.
          for (Alarminterface rockAlarminterface : rockAlarminterfaces) {
            final AlarmInterface alarmInterface = new DefaultAlarmInterface(databaseSession, authSession,
                alarmProperties, rockAlarminterface.getInterfaceid());
            alarmInterface.update();
            alarms.addAlarmInterface(alarmInterface);
          }

          // add the special "ALL" pseudo interface.
          final AlarmInterface all = new AllAlarmInterface(databaseSession, authSession, alarmProperties, alarms);
          alarms.addAlarmInterface(all);

          // select the all interface, but do not update the interfaces a second
          // time.
          alarms.select(all.getId(), false);
        }
      } catch (DatabaseException e) {
        log.severe(e.getMessage());
      }
    }

    return alarms;
  }
}
