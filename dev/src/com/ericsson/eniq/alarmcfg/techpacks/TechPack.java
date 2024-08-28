/**
 * 
 */
package com.ericsson.eniq.alarmcfg.techpacks;

import java.util.Vector;

/**
 * @author ecarbjo
 * 
 */
public interface TechPack extends ParameterDescriptor {

  /**
   * This method should return a vector containing all types that this tech pack
   * has.
   * 
   * @return vector of techpack types.
   */
  Vector<TechPackType> getTypes();

  /**
   * Adds a new techpack type to the hashmap.
   * 
   * @param type
   *          the techpack type to add.
   */
  void addType(TechPackType type);
}
