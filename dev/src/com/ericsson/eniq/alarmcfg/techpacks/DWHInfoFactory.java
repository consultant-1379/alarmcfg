package com.ericsson.eniq.alarmcfg.techpacks;

import java.util.Vector;

/**
 * @author ecarbjo 
 */

/**
 * The interface through which DWH information (basetable, techpack,
 * techpacktype and tablelevel object) structures can be retrieved. The
 * structure should be generated by any implementing class, and then returned
 * through the getInformation() method.
 */
public interface DWHInfoFactory {

  /**
   * Get techpack/basetable information from the DWH repository.
   * 
   * @return a vector containing TechPacks, which in turn should contain a tree
   *         of the other objects in the information structure. Null is returned
   *         if an exception occurs.
   */
  Vector<TechPack> getInformation();
}
