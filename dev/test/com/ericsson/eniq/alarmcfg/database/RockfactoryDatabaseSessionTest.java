package com.ericsson.eniq.alarmcfg.database;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.util.Hashtable;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.distocraft.dc5000.etl.engine.main.EngineAdmin;
import com.distocraft.dc5000.etl.engine.main.EngineAdminFactory;
import com.distocraft.dc5000.repository.dwhrep.Alarmreport;
import com.ericsson.eniq.alarmcfg.common.Constants;
import com.ericsson.eniq.alarmcfg.exceptions.DatabaseException;
import com.ericsson.eniq.alarmcfg.reports.AlarmReport;

import ssc.rockfactory.RockFactory;


public class RockfactoryDatabaseSessionTest {
  
  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };
  
  private RockFactory mockFactory;

  private EngineAdmin mockEngineAdmin;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    mockFactory = context.mock(RockFactory.class);
    mockEngineAdmin = context.mock(EngineAdmin.class);
    
    context.checking(new Expectations() {
      private Connection mockConnection = context.mock(Connection.class);

      {
        allowing(mockFactory).getConnection();
        will(returnValue(mockConnection));
        allowing(mockConnection).setAutoCommit(false);
        allowing(mockFactory).insertData(with(any(AlarmReport.class)));
        will(returnValue(1));
        allowing(mockConnection).commit();
        allowing(mockConnection).rollback();
        allowing(mockEngineAdmin).reloadAlarmConfigCache();
      }
    });
    
    EngineAdminFactory.setInstance(mockEngineAdmin);    
    
  }
  
  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    EngineAdminFactory.setInstance(null);    
  }

  /**
   * Test for create alarmreport method
   * @throws DatabaseException
   */
  @Test
  public void testCreateAlarmreport() throws DatabaseException {
    DatabaseSession databaseSession = new RockfactoryDatabaseSession(mockFactory);
    String interfaceid = "AlarmInterface_RD";
    String reportid = "27deacab-9ff4-4375-a972-8c48d292ad96";
    String reportname = "AM_DC_E_RAN_IULINK_RAW_pmInOutOfSequenceFrames";
    String status = "ACTIVE";
    Boolean simultaneous = true;
    Hashtable<String, String> parameters = new Hashtable<String, String>();
    parameters.put(Constants.BASETABLE_NAME, "DC_E_RAN_IULINK_RAW");
    parameters.put("Some prompt value:", "1");
    Alarmreport alarmReport = databaseSession.createAlarmreport(interfaceid, reportid, reportname, status, simultaneous, parameters);
    assertNotNull(alarmReport);
  }
  
  /**
   * Test for create alarmreport method
   * @throws DatabaseException
   */
  @Test(expected = DatabaseException.class)
  public void testCreateAlarmreportFail() throws DatabaseException {
    DatabaseSession databaseSession = new RockfactoryDatabaseSession(mockFactory);
    Alarmreport alarmReport = databaseSession.createAlarmreport(null, null, null, null, null, null);
    assertNotNull(alarmReport);
  }

  

}
