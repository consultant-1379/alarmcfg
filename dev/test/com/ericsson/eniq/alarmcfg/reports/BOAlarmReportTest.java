/**
 * 
 */
package com.ericsson.eniq.alarmcfg.reports;

import static org.junit.Assert.*;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.businessobjects.rebean.wi.DocumentInstance;
import com.businessobjects.rebean.wi.PaginationMode;
import com.businessobjects.rebean.wi.Prompt;
import com.businessobjects.rebean.wi.Prompts;
import com.businessobjects.rebean.wi.Report;
import com.businessobjects.rebean.wi.ReportEngine;
import com.businessobjects.rebean.wi.ReportEngines;
import com.businessobjects.rebean.wi.Reports;
import com.businessobjects.rebean.wi.XMLView;
import com.ericsson.eniq.alarmcfg.common.PromptNameValuePair;
import com.ericsson.eniq.alarmcfg.database.DatabaseSession;
import com.ericsson.eniq.alarmcfg.interfaces.AlarmInterface;
import com.ericsson.eniq.alarmcfg.prompts.AlarmReportPrompt;


/**
 * @author eheijun
 *
 */
public class BOAlarmReportTest {
  
  private static final String SAMPLEXML = "<SAMPLEXML/>";
  
  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };
  private BODocument<?> mockBODocument;
  
  private DatabaseSession mockDatabaseSession;
  private AlarmInterface mockAlarmInterface;
  private ReportEngines mockReportEngines;
  private ReportEngine mockReportEngine;
  private DocumentInstance mockDocumentInstance;
  private Prompts mockPrompts;
  private Prompt mockPrompt0;
  private Prompt mockPrompt1;
  private Prompt mockPrompt2;
  private Reports mockReports;
  private Report mockReport;
  protected XMLView mockXMLView;

  
  @Before
  public void setUp() throws RemoteException {

    mockDatabaseSession = context.mock(DatabaseSession.class);
    mockAlarmInterface = context.mock(AlarmInterface.class);
    mockReportEngines = context.mock(ReportEngines.class);
    mockBODocument = context.mock(BODocument.class);
    mockReportEngine = context.mock(ReportEngine.class);
    mockDocumentInstance = context.mock(DocumentInstance.class);
    mockPrompts = context.mock(Prompts.class);
    mockPrompt0 = context.mock(Prompt.class, "0");
    mockPrompt1 = context.mock(Prompt.class, "1");
    mockPrompt2 = context.mock(Prompt.class, "2");
    mockReports = context.mock(Reports.class);
    mockReport = context.mock(Report.class);
    mockXMLView = context.mock(XMLView.class);
    
    context.checking(new Expectations() {

      {
        allowing(mockReportEngines).getService(ReportEngines.ReportEngineType.WI_REPORT_ENGINE);
        will(returnValue(mockReportEngine));
        allowing(mockReportEngine).openDocument(with(any(Integer.class)));
        will(returnValue(mockDocumentInstance));
        allowing(mockDocumentInstance).getPrompts();
        will(returnValue(mockPrompts));
        
        allowing(mockDocumentInstance).refresh();
        allowing(mockDocumentInstance).setPrompts();
        allowing(mockDocumentInstance).getReports();
        will(returnValue(mockReports));
        allowing(mockReports).getCount();
        will(returnValue(1));
        allowing(mockReports).getItem(0);
        will(returnValue(mockReport));
        allowing(mockReport).setPaginationMode(PaginationMode.Listing);
        allowing(mockReport).getXMLView(XMLView.CONTENT);
        will(returnValue(mockXMLView));
        allowing(mockXMLView).getContent();
        will(returnValue(SAMPLEXML));
        
        allowing(mockPrompts).getCount();
        will(returnValue(3));
        allowing(mockPrompts).getItem(0);
        will(returnValue(mockPrompt0));
        allowing(mockPrompts).getItem(1);
        will(returnValue(mockPrompt1));
        allowing(mockPrompts).getItem(2);
        will(returnValue(mockPrompt2));
        allowing(mockPrompt0).getName();
        will(returnValue("Select Name:"));
        allowing(mockPrompt0).hasLOV();
        will(returnValue(false));
        allowing(mockPrompt1).getName();
        will(returnValue("Select Date:"));
        allowing(mockPrompt1).hasLOV();
        will(returnValue(false));
        allowing(mockPrompt2).getName();
        will(returnValue("Select ABC:"));
        allowing(mockPrompt2).hasLOV();
        will(returnValue(false));
        allowing(mockPrompt0).enterValues(with(any(String[].class)));
        
        allowing(mockBODocument).getId();
        will(returnValue(4321));
        allowing(mockBODocument).getTitle();
        will(returnValue("TEST"));
        allowing(mockDocumentInstance).closeDocument();
      }
    });
  }

  /**
   * Test method for {@link com.ericsson.eniq.alarmcfg.reports.BOAlarmReport#getPrompts()}.
   */
  @Test
  public void testGetPrompts() {
    final BOAlarmReport alarmReport =  new BOAlarmReport(mockDatabaseSession, mockAlarmInterface,
        mockReportEngines, true, mockBODocument);
    try {
      int count = 0;
      final Enumeration<AlarmReportPrompt> prompts = alarmReport.getPrompts();
      while (prompts.hasMoreElements()) {
        final AlarmReportPrompt arp = prompts.nextElement();
        if (count == 0) {
          assertTrue(arp.getName().equals(mockPrompt2.getName()));
        } else if (count == 1) {
          assertTrue(arp.getName().equals(mockPrompt1.getName()));
        } else if (count == 2) {
          assertTrue(arp.getName().equals(mockPrompt0.getName()));
        } else {
          fail("Invalid amount of prompts returned");
        }
        count++;
      }
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for {@link com.ericsson.eniq.alarmcfg.reports.BOAlarmReport#refresh(java.util.List)}.
   */
  @Test
  public void testRefresh() {
    final BOAlarmReport alarmReport =  new BOAlarmReport(mockDatabaseSession, mockAlarmInterface,
        mockReportEngines, true, mockBODocument);
    try {
      
      final List<PromptNameValuePair> nameValues = new ArrayList<PromptNameValuePair>();
      final String[] values1 = {"SOMENAME;OTHERNAME"};
      final String[] values2 = {"10/10/2010"};
      final String[] values3 = {""};
      final PromptNameValuePair test0 = new PromptNameValuePair(mockPrompt0.getName(), values1);
      final PromptNameValuePair test1 = new PromptNameValuePair(mockPrompt0.getName(), values2);
      final PromptNameValuePair test2 = new PromptNameValuePair(mockPrompt0.getName(), values3);
      nameValues.add(test0);
      nameValues.add(test1);
      nameValues.add(test2);
      final String result = alarmReport.refresh(nameValues);
      assertTrue(SAMPLEXML.equals(result));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

}
