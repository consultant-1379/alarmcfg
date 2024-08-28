package com.ericsson.eniq.alarmcfg.techpacks;

/**
 * @author ecarbjo
 * 
 */
public class DefaultBaseTable implements BaseTable {

  final private String name;

  /**
   * Constructor that sets the name for the base table.
   * 
   * @param basetablename
   *          the name of the base table.
   */
  public DefaultBaseTable(final String basetablename) {
    this.name = basetablename;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.techpacks.BaseTable#getName()
   */
  @Override
  public String getName() {
    return name;
  }

}
