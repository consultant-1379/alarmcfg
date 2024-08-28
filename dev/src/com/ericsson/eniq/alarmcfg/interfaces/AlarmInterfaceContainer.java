package com.ericsson.eniq.alarmcfg.interfaces;

import java.util.Enumeration;

public interface AlarmInterfaceContainer {

  /**
   * Add a new alarm interface to this container.
   * 
   * @param alarm
   *          the alarm interface to add.
   */
  void addAlarmInterface(AlarmInterface alarm);

  /**
   * Get an alarm interface based on index.
   * 
   * @param id
   *          the ID of the alarm interface to return.
   * @return null if the id wasn't found, or the alarm interface otherwize.
   */
  AlarmInterface getInterfaceById(String id);

  /**
   * Get an alarm interface given by name.
   * 
   * @param name
   *          the name to search for
   * @return the alarm interface or null if none was found
   */
  AlarmInterface getInterfaceByName(String name);

  /**
   * @return an enumeration of all the interfaces.
   */
  Enumeration<AlarmInterface> getElements();

  /**
   * Returns the first selected interface that is found. This should also be the
   * only selected interface.
   * 
   * @return a selected interface or null if no interface is selected.
   */
  AlarmInterface getCurrentInterface();

  /**
   * Selects the given alarm, and provides a choice to either update the
   * interface or not.
   * 
   * @param id
   *          the alarm interface to select.
   * @param update
   *          true if the interface should be refreshed from the data source.
   * @return true if an interface could actually be selected, false otherwize.
   */
  boolean select(String id, boolean update);

  /**
   * Selects a given alarm interface to make sure it will be shown on screen the
   * next time the view is refreshed.
   * 
   * @param id
   *          the id of the alarm interface to select. (all others will be
   *          de-selected)
   * @return true if an interface could actually be selected, false otherwize.
   */
  boolean select(String id);

  /**
   * Update all REAL interfaces from the database.
   */
  void updateAll();

}
