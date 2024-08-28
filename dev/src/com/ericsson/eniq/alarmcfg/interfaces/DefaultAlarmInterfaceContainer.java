/**
 * 
 */
package com.ericsson.eniq.alarmcfg.interfaces;

import java.util.Enumeration;
import java.util.Vector;

/**
 * @author ecarbjo A container class for the AlarmInterface instances.
 */
public class DefaultAlarmInterfaceContainer implements AlarmInterfaceContainer {

  private Vector<AlarmInterface> interfaces = new Vector<AlarmInterface>();

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.alarmcfg.interfaces.AlarmInterfaceContainer#addAlarmInterface
   * (com.ericsson.eniq.alarmcfg.interfaces.AlarmInterface)
   */
  public void addAlarmInterface(final AlarmInterface alarm) {
    interfaces.add(alarm);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.alarmcfg.interfaces.AlarmInterfaceContainer#getInterfaceById
   * (java.lang.String)
   */
  public AlarmInterface getInterfaceById(final String id) {
    final Enumeration<AlarmInterface> contents = interfaces.elements();
    while (contents.hasMoreElements()) {
      final AlarmInterface currentInterface = contents.nextElement();
      if (currentInterface.getId().equals(id)) {
        return currentInterface;
      }
    }

    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @seecom.ericsson.eniq.alarmcfg.interfaces.AlarmInterfaceContainer#
   * getInterfaceByName(java.lang.String)
   */
  public AlarmInterface getInterfaceByName(final String name) {
    final Enumeration<AlarmInterface> contents = interfaces.elements();
    while (contents.hasMoreElements()) {
      final AlarmInterface currentInterface = contents.nextElement();
      if (currentInterface.getName().equals(name)) {
        return currentInterface;
      }
    }

    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.alarmcfg.interfaces.AlarmInterfaceContainer#getElements()
   */
  public Enumeration<AlarmInterface> getElements() {
    return interfaces.elements();
  }

  /*
   * (non-Javadoc)
   * 
   * @seecom.ericsson.eniq.alarmcfg.interfaces.AlarmInterfaceContainer#
   * getCurrentInterface()
   */
  public AlarmInterface getCurrentInterface() {
    final Enumeration<AlarmInterface> contents = interfaces.elements();
    while (contents.hasMoreElements()) {
      final AlarmInterface currentInterface = contents.nextElement();
      if (currentInterface.isSelected()) {
        return currentInterface;
      }
    }

    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.alarmcfg.interfaces.AlarmInterfaceContainer#select(java
   * .lang.String)
   */
  @Override
  public boolean select(final String id, final boolean update) {
    // if the alarm interface doesn't exist in this container, return false.
    final Enumeration<AlarmInterface> contents = interfaces.elements();

    boolean interfaceSelected = false;

    while (contents.hasMoreElements()) {
      final AlarmInterface currentInterface = contents.nextElement();

      // check if the id match and then select or deselect the interface.
      if (currentInterface.getId().equals(id)) {
        currentInterface.setSelected(true);

        interfaceSelected = true;

        // update the interface to make sure we show relevant data on screen.
        if (update) {
          currentInterface.update();
        }
      } else {
        currentInterface.setSelected(false);
      }
    }

    return interfaceSelected;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.alarmcfg.interfaces.AlarmInterfaceContainer#updateAll()
   */
  @Override
  public void updateAll() {
    final Enumeration<AlarmInterface> contents = interfaces.elements();
    while (contents.hasMoreElements()) {
      final AlarmInterface currentInterface = contents.nextElement();

      // if the current interface is a _real_ interface, then update it.
      if (currentInterface.isRealInterface()) {
        currentInterface.update();
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.alarmcfg.interfaces.AlarmInterfaceContainer#select(java
   * .lang.String)
   */
  @Override
  public boolean select(final String id) {
    return select(id, true);
  }
}
