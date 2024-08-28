package com.ericsson.eniq.alarmcfg.techpacks;

import java.util.Vector;

public class DefaultTableLevel implements TableLevel {

  final private String name;

  final private Vector<BaseTable> tables = new Vector<BaseTable>();

  /**
   * Default constructor that sets a name for the table level
   * 
   * @param tablelevel
   *          the name of the table level object.
   */
  public DefaultTableLevel(final String tablelevel) {
    this.name = tablelevel;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.techpacks.TableLevel#getBaseTables()
   */
  @Override
  public Vector<BaseTable> getBaseTables() {
    return tables;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.techpacks.TableLevel#getName()
   */
  @Override
  public String getName() {
    return name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.alarmcfg.techpacks.TableLevel#addBaseTable(com.ericsson
   * .eniq.alarmcfg.techpacks.BaseTable)
   */
  @Override
  public void addBaseTable(final BaseTable table) {
    if (table != null) {
      tables.add(table);
    }
  }

}
