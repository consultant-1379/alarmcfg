/**
 * 
 */
package com.ericsson.eniq.alarmcfg.interfaces;

import static org.junit.Assert.*;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.distocraft.dc5000.repository.dwhrep.Alarminterface;
import com.distocraft.dc5000.repository.dwhrep.Alarmreport;
import com.distocraft.dc5000.repository.dwhrep.Alarmreportparameter;
import com.ericsson.eniq.alarmcfg.authenticate.AuthSession;
import com.ericsson.eniq.alarmcfg.config.AlarmProperties;
import com.ericsson.eniq.alarmcfg.database.DatabaseSession;
import com.ericsson.eniq.alarmcfg.exceptions.DatabaseException;
import com.ericsson.eniq.alarmcfg.reports.AlarmReport;
import com.ericsson.eniq.alarmcfg.reports.AlarmReportLister;
//import edu.emory.mathcs.backport.java.util.Arrays;
import java.util.Arrays;


/**
 * @author eheijun
 *
 */
public class DefaultAlarmInterfaceTest {

  private static final String TEST_REPORT_ID = "27deacab-9ff4-4375-a972-8c48d292ad96";

  private static final String TEST_REPORT_NAME = "AM_DC_E_RAN_IULINK_RAW_pmInOutOfSequenceFrames";

  private static final String TEST_REPORT_NAME_RD = "AM_DC_E_RAN_IULINK_RAW_pmOutInOfSequenceFrames";

  private static final String TEST_REPORT_BASETABLE = "DC_E_RAN_IULINK_RAW";

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };
  
  private AuthSession mockAuthSession;

  private AlarmProperties mockAlarmProperties;

  private DatabaseSession mockDatabaseSession;

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

    mockDatabaseSession = context.mock(DatabaseSession.class);
    
    mockAuthSession = context.mock(AuthSession.class);
    
    mockAlarmProperties = context.mock(AlarmProperties.class);
    
    context.checking(new Expectations() {

      {
        allowing(mockAlarmProperties).getProperty("alarmCategory");
        will(returnValue("TEST"));
        allowing(mockAlarmProperties).getProperty("alarmCategoryRD");
        will(returnValue("TEST RD"));
      }
    });
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
  }

  /**
   * Test method for {@link com.ericsson.eniq.alarmcfg.interfaces.DefaultAlarmInterface#update()}.
   * @throws DatabaseException 
   */
  @Test
  public void testUpdate() throws DatabaseException {
    
    final String testInterfaceid = "AlarmInterface_RD";
    
    final Alarminterface mockAlarminterface = context.mock(Alarminterface.class);
    
    final AlarmReportLister mockScheduledReportLister = context.mock(AlarmReportLister.class, "Scheduled");
    
    final AlarmReportLister mockReducedDelayReportLister = context.mock(AlarmReportLister.class, "ReducedDelay");
    
    final AlarmReport mockAlarmReport = context.mock(AlarmReport.class, "ScheduledReport");
    
    final AlarmReport mockAlarmReportRD = context.mock(AlarmReport.class, "ReducedDelayReport");
    
    final AlarmReport[] alarmReportTable = new AlarmReport[] { mockAlarmReport };
    
    final AlarmReport[] alarmReportTableRD = new AlarmReport[] { mockAlarmReportRD };
    
    final Alarmreport mockAlarmreport  = context.mock(Alarmreport.class);
    
    final Alarmreport[] alarmreportTable = new Alarmreport[] { mockAlarmreport };
    
    final Alarmreportparameter mockAlarmreportparameter  = context.mock(Alarmreportparameter.class);
    
    final Alarmreportparameter[] alarmreportparameterTable = new Alarmreportparameter[] { mockAlarmreportparameter };
    
    context.checking(new Expectations() {

      {
        allowing(mockDatabaseSession).getAlarminterface(testInterfaceid);
        will(returnValue(mockAlarminterface));
        allowing(mockDatabaseSession).getAlarmreports(testInterfaceid);
        will(returnValue(Arrays.asList(alarmreportTable)));
        allowing(mockDatabaseSession).getAlarmreport(TEST_REPORT_ID);
        will(returnValue(mockAlarmreport));
        allowing(mockDatabaseSession).getAlarmreportparameters(TEST_REPORT_ID);
        will(returnValue(Arrays.asList(alarmreportparameterTable)));
        allowing(mockAlarminterface).getDescription();
        will(returnValue("Interface for RD alarms"));
        one(mockAuthSession).getAlarmReportLister(with(any(DatabaseSession.class)), with(any(AlarmInterface.class)), with(any(String[].class)), with(any(Boolean[].class)) );
        will(returnValue(mockScheduledReportLister));
        one(mockAuthSession).getAlarmReportLister(with(any(DatabaseSession.class)), with(any(AlarmInterface.class)), with(any(String[].class)), with(any(Boolean[].class)) );
        will(returnValue(mockReducedDelayReportLister));
        allowing(mockScheduledReportLister).getAvailableReports();
        will(returnValue(Arrays.asList(alarmReportTable)));
        allowing(mockReducedDelayReportLister).getAvailableReports();
        will(returnValue(Arrays.asList(alarmReportTableRD)));
        allowing(mockAlarmreport).getReportid();
        will(returnValue(TEST_REPORT_ID));
        allowing(mockAlarmreport).getReportname();
        will(returnValue(TEST_REPORT_NAME_RD));
        allowing(mockAlarmreport).getStatus();
        will(returnValue("ACTIVE"));
        allowing(mockAlarmreportparameter).getName();
        will(returnValue("eniqBasetableName"));
        allowing(mockAlarmreportparameter).getValue();
        will(returnValue(TEST_REPORT_BASETABLE));
        allowing(mockAlarmReport).getName();
        will(returnValue(TEST_REPORT_NAME));
        allowing(mockAlarmReportRD).getName();
        will(returnValue(TEST_REPORT_NAME_RD));
      }
    });
    
    
    DefaultAlarmInterface alarmInterface = new DefaultAlarmInterface(mockDatabaseSession, mockAuthSession,
        mockAlarmProperties, testInterfaceid);
    alarmInterface.update();
    
    AlarmReport expected = alarmInterface.getReports().get(0);
    assertTrue(TEST_REPORT_ID.equals(expected.getId()));
    
    
  }

}
