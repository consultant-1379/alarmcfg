package com.ericsson.eniq.alarmcfg.techpacks;

import java.util.Vector;

/**
 * @author ecarbjo
 */
public class DefaultTechPack implements TechPack {

  final private String name;

  final private Vector<TechPackType> types = new Vector<TechPackType>();

  /**
   * Constructor that sets the name of the tech pack.
   * 
   * @param name
   *          the name of the tech pack
   */
  public DefaultTechPack(final String name) {
    this.name = name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.techpacks.TechPack#getTypes()
   */
  @Override
  public Vector<TechPackType> getTypes() {
    return types;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.alarmcfg.techpacks.TechPack#addType(com.ericsson.eniq
   * .alarmcfg.techpacks.TechPackType)
   */
  @Override
  public void addType(final TechPackType type) {
    if (type != null) {
      types.add(type);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.techpacks.TechPack#getName()
   */
  @Override
  public String getName() {
    return name;
  }

}
