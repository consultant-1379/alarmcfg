/**
 * 
 */
package com.ericsson.eniq.alarmcfg.reports;

import static org.junit.Assert.*;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.businessobjects.rebean.wi.ReportEngines;
import com.crystaldecisions.sdk.occa.infostore.IInfoStore;
import com.ericsson.eniq.alarmcfg.database.DatabaseSession;
import com.ericsson.eniq.alarmcfg.interfaces.AlarmInterface;


/**
 * @author eheijun
 *
 */
public class BOAlarmReportListerTest {

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };
  
  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
  }

  /**
   * Test method for {@link com.ericsson.eniq.alarmcfg.reports.BOAlarmReportLister#BOAlarmReportLister(com.crystaldecisions.sdk.occa.infostore.IInfoStore, com.businessobjects.rebean.wi.ReportEngines, com.ericsson.eniq.alarmcfg.database.DatabaseSession, com.ericsson.eniq.alarmcfg.interfaces.AlarmInterface, java.lang.String[])}.
   */
  @Test
  public void testBOAlarmReportLister() {
    
    final IInfoStore infoStore = context.mock(IInfoStore.class);
    final ReportEngines reportEngines = context.mock(ReportEngines.class);
    final DatabaseSession dbSession = context.mock(DatabaseSession.class);
    final AlarmInterface alarmInterface  = context.mock(AlarmInterface.class);
    final String[] categories = new String[] {"OLD", "NEW"};
    final Boolean[] categoryTypes = new Boolean[] {false, true};
    
    BOAlarmReportLister reportLister = new BOAlarmReportLister(infoStore, reportEngines, dbSession, alarmInterface, categories, categoryTypes);
    assertNotNull(reportLister);
  }

}
