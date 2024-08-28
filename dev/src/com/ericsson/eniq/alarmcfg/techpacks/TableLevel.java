/**
 * 
 */
package com.ericsson.eniq.alarmcfg.techpacks;

import java.util.Vector;

/**
 * @author ecarbjo
 * 
 */
public interface TableLevel extends ParameterDescriptor {

  /**
   * Returns a vector containing all the basetables that are associated with
   * this table level.
   * 
   * @return a vector of basetables.
   */
  Vector<BaseTable> getBaseTables();

  /**
   * Adds another base table to the hash map.
   * 
   * @param table
   *          the table to add.
   */
  void addBaseTable(BaseTable table);
}
