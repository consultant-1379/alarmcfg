/**
 * 
 */
package com.ericsson.eniq.alarmcfg.techpacks;

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Logger;

import com.distocraft.dc5000.repository.dwhrep.Dwhtype;
import com.ericsson.eniq.alarmcfg.database.DatabaseSession;
import com.ericsson.eniq.alarmcfg.exceptions.DatabaseException;

/**
 * @author ecarbjo
 * 
 */
public class DefaultDWHInfoFactory implements DWHInfoFactory {
  // sorter for the dwhtype vector.
  private final static Comparator<Dwhtype> COMPARATOR = new Comparator<Dwhtype>() {
    // sort the content by tech pack name, type name, table level, and base
    // table name.
    public int compare(final Dwhtype d1, final Dwhtype d2) {
      // compare the tech pack name
      final int techComp = d1.getTechpack_name().compareTo(d2.getTechpack_name());
      if (techComp == 0) {
        // tech pack name matches, compare the type.
        final int typeComp = d1.getTypename().compareTo(d2.getTypename());
        if (typeComp == 0) {
          // type also matches, compare table level.
          final int levelComp = d1.getTablelevel().compareTo(d2.getTablelevel());
          if (levelComp == 0) {
            // level also match, return the comparison between the table names.
            return d1.getBasetablename().compareTo(d2.getBasetablename());
          } else {
            return levelComp;
          }
        } else {
          return typeComp;
        }
      } else {
        return techComp;
      }
    }
  };

  final private static Logger log = Logger.getLogger(DefaultDWHInfoFactory.class.getName());

  final private DatabaseSession databaseSession;

  /**
   * Default constructor. Takes an instance of the database interface to be able
   * to query the DWH.
   * 
   * @param session
   *          the database session to use.
   */
  public DefaultDWHInfoFactory(final DatabaseSession session) {
    this.databaseSession = session;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.techpacks.DHWInfoFactory#getInformation()
   */
  @Override
  public Vector<TechPack> getInformation() {
    final Vector<TechPack> techPacks = new Vector<TechPack>();
    try {
      final Vector<Dwhtype> dwhtypes = databaseSession.getDwhinfos();
      // first we sort the dwhtypes vector correctly, and then we add the
      // contents into a tree like structure for use in the view.
      Collections.sort(dwhtypes, COMPARATOR);
      final Vector<Dwhtype> activePMdwhtypes = getActiveUsedWHTypes(dwhtypes);
      
      final Enumeration<Dwhtype> typeEnum = activePMdwhtypes.elements();

      // init the tracking variables.
      TechPack currentTechPack = null;
      TableLevel currentTableLevel = null;
      TechPackType currentTechPackType = null;

      

       // now iterate through the dwhtypes
      while (typeEnum.hasMoreElements()) {
        final Dwhtype currentType = typeEnum.nextElement();

        // if the current tech pack doesn't already have an object, create a new
        // one and add it to the vector.
        if (currentTechPack == null || !currentTechPack.getName().equals(currentType.getTechpack_name())) {
          // create and add the new tech pack.
        	
        	final String techPackName = currentType.getTechpack_name(); 
        	currentTechPack = new DefaultTechPack(techPackName);
            techPacks.add(currentTechPack);

          // null all the other current objects, since we've switched their
          // parent node (we need to create new ones now either way)
          currentTechPackType = null;
          currentTableLevel = null;
        }

        if (currentTechPackType == null || !currentTechPackType.getName().equals(currentType.getTypename())) {
          // create and add a new type.
          currentTechPackType = new DefaultTechPackType(currentType.getTypename());
          currentTechPack.addType(currentTechPackType);

          // null the currentTableLevel since we have a new parent node for the
          // coming levels.
          currentTableLevel = null;
        }

        if (currentTableLevel == null || !currentTableLevel.getName().equals(currentType.getTablelevel())) {
          // create and add the current level.
          currentTableLevel = new DefaultTableLevel(currentType.getTablelevel());
          currentTechPackType.addTableLevel(currentTableLevel);
        }

        // create and add the base table to the current table level.
        final BaseTable newBaseTable = new DefaultBaseTable(currentType.getBasetablename());
        currentTableLevel.addBaseTable(newBaseTable);
      }
    } catch (DatabaseException e) {
      log.warning("The database interface threw a DatabaseException when trying to retrieve "
          + "the DWH information. The exception message was: " + e.getMessage());
      return null;
    }
    return techPacks;
  }
  
  private Vector<Dwhtype> getActiveUsedWHTypes(final Vector<Dwhtype> dwhtypes) throws DatabaseException{

	  final Vector<Dwhtype> activeUseddwhtypes = new Vector<Dwhtype>();

	  final Vector<String> activeUsedTypes = databaseSession.getActivatedUsedTypeVersionings();
	  final Enumeration<String> tpEnum = activeUsedTypes.elements();

	  while(tpEnum.hasMoreElements()){
		  final String activeTP = tpEnum.nextElement();
		  final Enumeration<Dwhtype> typeEnum = dwhtypes.elements();
		  while(typeEnum.hasMoreElements()){
			  final Dwhtype currentType = typeEnum.nextElement();

			  if(currentType.getTechpack_name().equals(activeTP)){
				  activeUseddwhtypes.add(currentType);
			  }
		  }

	  }

  return activeUseddwhtypes;

  }
}
