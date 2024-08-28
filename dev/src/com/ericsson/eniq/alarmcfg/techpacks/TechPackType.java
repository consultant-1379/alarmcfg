/**
 * 
 */
package com.ericsson.eniq.alarmcfg.techpacks;

import java.util.Vector;

/**
 * @author ecarbjo
 * 
 */
public interface TechPackType extends ParameterDescriptor {

  /**
   * Gets a vector containing all the table levels associated with this tech
   * pack type.
   * 
   * @return a vector of table levels.
   */
  Vector<TableLevel> getTableLevels();

  /**
   * Adds a new table level to the hash map.
   * 
   * @param level
   *          the level to add.
   */
  void addTableLevel(TableLevel level);
}
