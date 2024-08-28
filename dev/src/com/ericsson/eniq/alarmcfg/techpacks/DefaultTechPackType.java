/**
 * 
 */
package com.ericsson.eniq.alarmcfg.techpacks;

import java.util.Vector;

/**
 * @author ecarbjo
 * 
 */
public class DefaultTechPackType implements TechPackType {

  final private String name;

  final private Vector<TableLevel> levels = new Vector<TableLevel>();

  public DefaultTechPackType(final String typename) {
    this.name = typename;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.techpacks.TechPackType#getName()
   */
  @Override
  public String getName() {
    return name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.techpacks.TechPackType#getTableLevels()
   */
  @Override
  public Vector<TableLevel> getTableLevels() {
    return levels;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.alarmcfg.techpacks.TechPackType#addTableLevel(com.ericsson
   * .eniq.alarmcfg.techpacks.TableLevel)
   */
  @Override
  public void addTableLevel(final TableLevel level) {
    if (level != null) {
      levels.add(level);
    }
  }

}
