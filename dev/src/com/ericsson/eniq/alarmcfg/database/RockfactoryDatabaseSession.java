/**
 * 
 */
package com.ericsson.eniq.alarmcfg.database;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import com.distocraft.dc5000.etl.engine.main.EngineAdmin;
import com.distocraft.dc5000.repository.dwhrep.Alarminterface;
import com.distocraft.dc5000.repository.dwhrep.AlarminterfaceFactory;
import com.distocraft.dc5000.repository.dwhrep.Alarmreport;
import com.distocraft.dc5000.repository.dwhrep.AlarmreportFactory;
import com.distocraft.dc5000.repository.dwhrep.Alarmreportparameter;
import com.distocraft.dc5000.repository.dwhrep.AlarmreportparameterFactory;
import com.distocraft.dc5000.repository.dwhrep.DwhtypeFactory;
import com.distocraft.dc5000.repository.dwhrep.Dwhtype;
import com.distocraft.dc5000.repository.dwhrep.Tpactivation;
import com.distocraft.dc5000.repository.dwhrep.TpactivationFactory;
import com.ericsson.eniq.alarmcfg.common.Constants;
import com.ericsson.eniq.alarmcfg.exceptions.DatabaseException;

import com.distocraft.dc5000.etl.engine.main.EngineAdminFactory;

import ssc.rockfactory.RockFactory;

/**
 * @author eheijun
 * 
 */
public class RockfactoryDatabaseSession implements DatabaseSession {

  private static Logger log = Logger.getLogger(RockfactoryDatabaseSession.class.getName());

  private final RockFactory dwhrepRockFactory;

  /**
   * @param dwhrepRockFactory
   */
  public RockfactoryDatabaseSession(final RockFactory dwhrepRockFactory) {
    super();
    if (dwhrepRockFactory == null) {
      log.warning("RockFactory has not been initialized");
    }
    this.dwhrepRockFactory = dwhrepRockFactory;
  }
  
  private void refreshAlarmConfigCache() {
    final EngineAdmin engineAdmin = EngineAdminFactory.getInstance();
    try {
      engineAdmin.reloadAlarmConfigCache();
    } catch (Exception e) {
      log.severe("AlarmConfigCache relad failed. " + e.getMessage());
    }
  }

  /**
   * Sorter for Alarminterfaces
   */
  private final static Comparator<Dwhtype> TYPECOMPARATOR = new Comparator<Dwhtype>() {

    public int compare(final Dwhtype d1, final Dwhtype d2) {
      return d1.getTechpack_name().compareTo(d2.getTechpack_name());
    }
  };

  /**
   * Sorter for Alarminterfaces
   */
  private final static Comparator<Alarminterface> ALARMINTERFACECOMPARATOR = new Comparator<Alarminterface>() {

    public int compare(final Alarminterface d1, final Alarminterface d2) {
      final Long i1 = d1.getQueue_number().longValue();
      final Long i2 = d2.getQueue_number().longValue();
      return i1.compareTo(i2);
    }
  };

  /**
   * Sorter for Alarmreports
   */
  private final static Comparator<Alarmreport> ALARMREPORTCOMPARATOR = new Comparator<Alarmreport>() {

    public int compare(final Alarmreport d1, final Alarmreport d2) {
      final String s1 = d1.getReportname();
      final String s2 = d2.getReportname();
      return s1.compareTo(s2);
    }
  };

  /**
   * Sorter for Alarmreport parameters
   */
  public final static Comparator<Alarmreportparameter> PARAMETERCOMPARATOR = new Comparator<Alarmreportparameter>() {

    public int compare(final Alarmreportparameter d1, final Alarmreportparameter d2) {
      final String s1 = d1.getName();
      final String s2 = d2.getName();
      return s1.compareTo(s2);
    }
  };

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.alarmcfg.database.DatabaseSession#getAlarminterfaces()
   */
  @Override
  public List<Alarminterface> getAlarminterfaces() throws DatabaseException {
    if (dwhrepRockFactory != null) {
      final Alarminterface whereAlarminterface = new Alarminterface(dwhrepRockFactory, false);
      try {
        final AlarminterfaceFactory alarminterfaceFactory = new AlarminterfaceFactory(dwhrepRockFactory,
            whereAlarminterface);
        final Vector<Alarminterface> alarminterfaces = alarminterfaceFactory.get();
        log.finest(alarminterfaces.size() + " interfaces found in dwhrep db");
        Collections.sort(alarminterfaces, ALARMINTERFACECOMPARATOR);
        return alarminterfaces;
      } catch (Exception e) {
        log.severe("Rock Error:" + e.getMessage());
        throw new DatabaseException("Failed to get Alarminterfaces");
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.alarmcfg.database.DatabaseSession#getAlarminterface()
   */
  @Override
  public Alarminterface getAlarminterface(final String interfaceid) throws DatabaseException {
    if (dwhrepRockFactory != null) {
      final Alarminterface whereAlarminterface = new Alarminterface(dwhrepRockFactory, false);
      whereAlarminterface.setInterfaceid(interfaceid);
      try {
        final AlarminterfaceFactory alarminterfaceFactory = new AlarminterfaceFactory(dwhrepRockFactory,
            whereAlarminterface);
        final Alarminterface alarminterface = alarminterfaceFactory.getElementAt(0);
        log.finest("Alarminterface " + alarminterface + " found in dwhrep db");
        return alarminterface;
      } catch (Exception e) {
        log.severe("Rock Error:" + e.getMessage());
        throw new DatabaseException("Failed to get Alarminterface " + interfaceid);
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.alarmcfg.database.DatabaseSession#getAlarmreports
   */
  @Override
  public Vector<Alarmreport> getAlarmreports(final String interfaceid) throws DatabaseException {
    if (dwhrepRockFactory != null) {
      final Alarmreport whereAlarmreport = new Alarmreport(dwhrepRockFactory, false);
      whereAlarmreport.setInterfaceid(interfaceid);
      try {
        final AlarmreportFactory alarmreportFactory = new AlarmreportFactory(dwhrepRockFactory, whereAlarmreport);
        final Vector<Alarmreport> alarmreports = alarmreportFactory.get();
        log.finest(alarmreports.size() + " reports found in dwhrep db for interface " + interfaceid);
        Collections.sort(alarmreports, ALARMREPORTCOMPARATOR);
        return alarmreports;
      } catch (Exception e) {
        log.severe("Rock Error:" + e.getMessage());
        throw new DatabaseException("Failed to get Alarmreports");
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.alarmcfg.database.DatabaseSession#getAlarmreport
   */
  @Override
  public Alarmreport getAlarmreport(final String reportid) throws DatabaseException {
    if (dwhrepRockFactory != null) {
      final Alarmreport whereAlarmreport = new Alarmreport(dwhrepRockFactory, false);
      whereAlarmreport.setReportid(reportid);
      try {
        final AlarmreportFactory alarmreportFactory = new AlarmreportFactory(dwhrepRockFactory, whereAlarmreport);
        final Alarmreport alarmreport = alarmreportFactory.getElementAt(0);
        log.finest("Alarmreport " + alarmreport + " found in dwhrep db");
        return alarmreport;
      } catch (Exception e) {
        log.severe("Rock Error:" + e.getMessage());
        throw new DatabaseException("Failed to get Alarmreport " + reportid);
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.alarmcfg.database.DatabaseSession#getAlarmreportparameters
   */
  @Override
  public Vector<Alarmreportparameter> getAlarmreportparameters(final String reportid) throws DatabaseException {
    if (dwhrepRockFactory != null) {
      final Alarmreportparameter whereAlarmreportparameter = new Alarmreportparameter(dwhrepRockFactory, false);
      whereAlarmreportparameter.setReportid(reportid);
      try {
        final AlarmreportparameterFactory AlarmreportparameterFactory = new AlarmreportparameterFactory(
            dwhrepRockFactory, whereAlarmreportparameter);
        final Vector<Alarmreportparameter> alarmreportparameters = AlarmreportparameterFactory.get();
        Collections.sort(alarmreportparameters, PARAMETERCOMPARATOR);
        return alarmreportparameters;
      } catch (Exception e) {
        log.severe("Rock Error:" + e.getMessage());
        throw new DatabaseException("Failed to get Alarmreportparameters");
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.alarmcfg.database.DatabaseSession#createAlarmreport
   */
  @Override
  public Alarmreport createAlarmreport(final String interfaceid, final String reportid, final String reportname,
      final String status, final Boolean simultaneous, final Hashtable<String, String> parameters) throws DatabaseException {
    if (dwhrepRockFactory != null) {
      try {
        final StringBuffer url = new StringBuffer("");
        url.append("reportname=" + reportname);
        url.append("&reportid=" + reportid);
        Enumeration<String> keys = parameters.keys();
        while (keys.hasMoreElements()) {
          final String key = keys.nextElement();
          final String value = parameters.get(key);
          if ((key != null) && (value != null)) {
            if ((key.equals(Constants.BASETABLE_NAME)) || (key.equals(Constants.PROMPT_ROWSTATUS))) {
              // do nothing
            } else {
              url.append("&promptValue_" + key + "=" + value);
            }
          }
        }
        dwhrepRockFactory.getConnection().setAutoCommit(false);
        final Alarmreport alarmreport = new Alarmreport(dwhrepRockFactory, true);
        alarmreport.setNewItem(true);
        alarmreport.setInterfaceid(interfaceid);
        alarmreport.setReportid(reportid);
        alarmreport.setReportname(reportname);
        alarmreport.setUrl(url.toString());
        alarmreport.setStatus(status);
        alarmreport.setSimultaneous((simultaneous ? 1 : 0));
        alarmreport.saveToDB();
        keys = parameters.keys();
        while (keys.hasMoreElements()) {
          final String key = keys.nextElement();
          final String value = parameters.get(key);
          if ((key == null) || (value == null)) {
            // do nothing
          } else {
            final Alarmreportparameter alarmreportparameter = new Alarmreportparameter(dwhrepRockFactory, true);
            alarmreportparameter.setReportid(reportid);
            alarmreportparameter.setName(key);
            alarmreportparameter.setValue(value);
            alarmreportparameter.saveToDB();
          }
        }
        dwhrepRockFactory.getConnection().commit();
        refreshAlarmConfigCache();
        return alarmreport;
      } catch (Exception e) {
        try {
          dwhrepRockFactory.getConnection().rollback();
        } catch (SQLException e1) {
          e1.printStackTrace();
        }
        log.severe("Rock Error:" + e.getMessage());
        throw new DatabaseException("Failed to create Alarmreport " + reportid);
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.alarmcfg.database.DatabaseSession#saveAlarmreport
   */
  @Override
  public void saveAlarmreport(final Alarmreport alarmreport) throws DatabaseException {
    if (dwhrepRockFactory != null) {
      try {
        dwhrepRockFactory.getConnection().setAutoCommit(false);
        alarmreport.saveToDB();
        dwhrepRockFactory.getConnection().commit();
        refreshAlarmConfigCache();
      } catch (Exception e) {
        try {
          dwhrepRockFactory.getConnection().rollback();
        } catch (SQLException e1) {
          e1.printStackTrace();
        }
        log.severe("Rock Error:" + e.getMessage());
        throw new DatabaseException("Failed to save Alarmreport " + alarmreport.getReportid());
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.database.DatabaseSession#saveAlarmreportparameters
   */
  @Override
  public void saveAlarmreportparameters(final List<Alarmreportparameter> alarmreportparameters)
      throws DatabaseException {
    if (dwhrepRockFactory != null) {
      try {
        dwhrepRockFactory.getConnection().setAutoCommit(false);
        for (Alarmreportparameter alarmreportparameter : alarmreportparameters) {
          alarmreportparameter.saveToDB();
        }
        dwhrepRockFactory.getConnection().commit();
        refreshAlarmConfigCache();
      } catch (Exception e) {
        try {
          dwhrepRockFactory.getConnection().rollback();
        } catch (SQLException e1) {
          e1.printStackTrace();
        }
        log.severe("Rock Error:" + e.getMessage());
        throw new DatabaseException("Failed to save Alarmreportparameters");
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.database.DatabaseSession#getDwhinfos()
   */
  @Override
  public void removeAlarmreport(final Alarmreport alarmreport) throws DatabaseException {
    if (dwhrepRockFactory != null) {
      try {
        dwhrepRockFactory.getConnection().setAutoCommit(false);
        final Alarmreportparameter whereAlarmreportparameter = new Alarmreportparameter(dwhrepRockFactory);
        whereAlarmreportparameter.setReportid(alarmreport.getReportid());
        whereAlarmreportparameter.deleteDB();
        alarmreport.deleteDB();
        dwhrepRockFactory.getConnection().commit();
        refreshAlarmConfigCache();
      } catch (Exception e) {
        try {
          dwhrepRockFactory.getConnection().rollback();
        } catch (SQLException e1) {
          e1.printStackTrace();
        }
        log.severe("Rock Error:" + e.getMessage());
        throw new DatabaseException("Failed to save Alarmreport " + alarmreport.getReportid());
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.database.DatabaseSession#getDwhinfos()
   */
  @Override
  public Vector<Dwhtype> getDwhinfos() throws DatabaseException {
    if (dwhrepRockFactory != null) {
      final Dwhtype type = new Dwhtype(dwhrepRockFactory, false);
      try {
        final DwhtypeFactory factory = new DwhtypeFactory(dwhrepRockFactory, type);
        final Vector<Dwhtype> types = factory.get();
        log.finest(types.size() + " dwhtypes found in dwhrep db");
        Collections.sort(types, TYPECOMPARATOR);
        return types;
      } catch (Exception e) {
        log.severe("Rock Error:" + e.getMessage());
        throw new DatabaseException("Failed to get Alarminterfaces");
      }
    }
    return null;
  }
  
  /**
   * Get all activated PM TechPack Type Versionings from the repository sorted by VersionId.
   * 
   * @return vector of Versioning objects
   * @throws DatabaseException
   */
  public Vector<String> getActivatedUsedTypeVersionings() throws DatabaseException {
    if (dwhrepRockFactory != null) {
      try {
        final Tpactivation a = new Tpactivation(dwhrepRockFactory);
        a.setStatus("ACTIVE");
        final TpactivationFactory aF = new TpactivationFactory(dwhrepRockFactory, a, false);
        final Vector<Tpactivation> tpactivations = aF.get();
        final Vector<String> usedActiveTPs = new Vector<String>();
        final Enumeration<Tpactivation> tpEnumns = tpactivations.elements();
        while (tpEnumns.hasMoreElements()){
        	
        	final Tpactivation tpActivation = tpEnumns.nextElement();
        	if (tpActivation!=null){
        		
            	if("PM".equals(tpActivation.getType()) || "CM".equals(tpActivation.getType()) || "CUSTOM".equals(tpActivation.getType())){
            		
            		if(!(tpActivation.getTechpack_name().startsWith("DIM"))){
            			usedActiveTPs.add(tpActivation.getTechpack_name());
            		}
            	}
        	}
        }
        return usedActiveTPs;
      } catch (Exception e) {
        log.severe("Rock Error:" + e.getMessage());
        throw new DatabaseException("Failed to get Tpactivations");
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.database.DatabaseSession#dropConnection()
   */
  @Override
  public void clear() {
    try {
      if (dwhrepRockFactory != null) {
        if (dwhrepRockFactory.getConnection() != null) {
          if (!dwhrepRockFactory.getConnection().isClosed()) {
            dwhrepRockFactory.getConnection().close();
          }
        }
      }
    } catch (Exception e) {
      log.severe("Rock Error:" + e.getMessage());
    }
  }

}
