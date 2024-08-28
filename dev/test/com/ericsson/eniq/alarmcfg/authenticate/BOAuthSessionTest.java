/**
 * 
 */
package com.ericsson.eniq.alarmcfg.authenticate;

import static org.junit.Assert.*;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import com.businessobjects.rebean.wi.ReportEngines;
import com.crystaldecisions.sdk.exception.SDKException;
import com.crystaldecisions.sdk.framework.IEnterpriseSession;
import com.crystaldecisions.sdk.occa.infostore.IInfoStore;
import com.ericsson.eniq.alarmcfg.database.DatabaseSession;
import com.ericsson.eniq.alarmcfg.interfaces.AlarmInterface;
import com.ericsson.eniq.alarmcfg.reports.AlarmReportLister;
import com.ericsson.eniq.alarmcfg.reports.BOAlarmReportLister;

/**
 * @author eheijun
 * 
 */
public class BOAuthSessionTest {

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };

  /**
   * Test method for
   * {@link com.ericsson.eniq.alarmcfg.authenticate.BOAuthSession#getAlarmReportLister(com.ericsson.eniq.alarmcfg.database.DatabaseSession, com.ericsson.eniq.alarmcfg.interfaces.AlarmInterface, java.lang.String[])}
   * .
   * @throws SDKException 
   */
  @Test
  public void testGetAlarmReportLister() throws SDKException {

    final IEnterpriseSession boSession = context.mock(IEnterpriseSession.class);
    final IInfoStore infoStore = context.mock(IInfoStore.class);
    final ReportEngines reportEngines = context.mock(ReportEngines.class);
    DatabaseSession dbSession = context.mock(DatabaseSession.class);
    AlarmInterface alarmInterface = context.mock(AlarmInterface.class);
    String[] alarmCategories = new String[] { "/TEST CATEGORY", "/ANOTHER TEST CATEGORY" };
    Boolean[] alarmCategoryTypes = new Boolean[] { true, false };

    context.checking(new Expectations() {

      {
        allowing(boSession).getService("InfoStore");
        will(returnValue(infoStore));
        allowing(boSession).getService("ReportEngines");
        will(returnValue(reportEngines));
      }
    });

    BOAuthSession authSession = new BOAuthSession(boSession);
    AlarmReportLister alarmReportLister = authSession.getAlarmReportLister(dbSession, alarmInterface, alarmCategories, alarmCategoryTypes);
    assertTrue(alarmReportLister instanceof BOAlarmReportLister);
  }

}
