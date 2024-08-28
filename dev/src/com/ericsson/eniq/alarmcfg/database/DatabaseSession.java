/**
 * 
 */
package com.ericsson.eniq.alarmcfg.database;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.distocraft.dc5000.repository.dwhrep.Alarminterface;
import com.distocraft.dc5000.repository.dwhrep.Alarmreport;
import com.distocraft.dc5000.repository.dwhrep.Alarmreportparameter;
import com.distocraft.dc5000.repository.dwhrep.Dwhtype;
//import com.distocraft.dc5000.repository.dwhrep.Tpactivation;
import com.ericsson.eniq.alarmcfg.exceptions.DatabaseException;

/**
 * @author eheijun & ecarbjo
 * 
 */
public interface DatabaseSession {

  /**
   * Get all AlarmInterfaces form the repository.
   * @return vector of Alarminteface objects
   * @throws DatabaseException
   */
  List<Alarminterface> getAlarminterfaces() throws DatabaseException;

  /**
   * Get one AlarmInterface by id.
   * @param interfaceid id of the interface
   * @return Alarminterface object 
   * @throws DatabaseException
   */
  Alarminterface getAlarminterface(String interfaceid) throws DatabaseException;

  /**
   * Get all alarm reports for one Alarminterface
   * @param interfaceid id of the interface
   * @return
   * @throws DatabaseException
   */
  List<Alarmreport> getAlarmreports(String interfaceid) throws DatabaseException;

  /**
   * Get one alarm report by report id.
   * @param reportid
   * @return
   * @throws DatabaseException
   */
  Alarmreport getAlarmreport(String reportid) throws DatabaseException;

  /**
   * Create new Alarmreport into repository
   * @param interfaceid
   * @param reportid
   * @param reportname
   * @param status
   * @param parameters
   * @return
   * @throws DatabaseException
   */
  Alarmreport createAlarmreport(String interfaceid, String reportid, String reportname, String status, Boolean simultaneous, 
      Hashtable<String, String> parameters) throws DatabaseException;

  /**
   * Save Alarmreport into repository
   * @param alarmreport to be saved
   * @throws DatabaseException
   */
  void saveAlarmreport(Alarmreport alarmreport) throws DatabaseException;

  /**
   * Remove alarmreport from repository 
   * @param alarmreport
   * @throws DatabaseException
   */
  void removeAlarmreport(Alarmreport alarmreport) throws DatabaseException;

  /**
   * Get parameters for Alarmreport
   * @param reportid
   * @return
   * @throws DatabaseException
   */
  List<Alarmreportparameter> getAlarmreportparameters(String reportid) throws DatabaseException;

  /**
   * Save parameters for the Alarmreport
   * @param alarmreportparameters
   * @throws DatabaseException
   */
  void saveAlarmreportparameters(List<Alarmreportparameter> alarmreportparameters) throws DatabaseException;

  /**
   * Get all the DWH information from the repository. This includes all
   * information needed to build a tree structure for Techpacks, techpacktypes,
   * basetables and table levels.
   * 
   * @return a vector of dwhtype rock objects.
   * @throws DatabaseException
   */
  Vector<Dwhtype> getDwhinfos() throws DatabaseException;

  /**
   * Get all the DWH information from the repository. This includes all
   * information needed to build a tree structure for Techpacks, techpacktypes,
   * basetables and table levels.
   * 
   * @return a vector of active tech packs of types (PM,CM and CUSTOM).
   * @throws DatabaseException
   */
  Vector<String> getActivatedUsedTypeVersionings() throws DatabaseException;
  
  /**
   * Clear database session 
   */
  void clear();
  
}
