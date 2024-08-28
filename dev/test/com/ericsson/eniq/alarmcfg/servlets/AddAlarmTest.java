/**
 * Test class for the LoginPage servlet
 */
package com.ericsson.eniq.alarmcfg.servlets;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.jmock.Mockery;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;

import com.ericsson.eniq.alarmcfg.interfaces.AlarmInterface;
import com.ericsson.eniq.alarmcfg.interfaces.AlarmInterfaceContainer;
import com.ericsson.eniq.alarmcfg.interfaces.AlarmInterfaceFactory;
import com.ericsson.eniq.alarmcfg.prompts.AlarmReportPrompt;
import com.ericsson.eniq.alarmcfg.reports.AlarmReport;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.servletunit.InvocationContext;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;

/**
 * @author ecarbjo
 * 
 */
@RunWith(JMock.class)
public class AddAlarmTest {

  // the jMock mockery.
  static Mockery context;

  static ServletRunner sr = null;

  static ServletUnitClient sc = null;

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
    context = new JUnit4Mockery();

    // set up the environment for the servlet.
    sr = null;
    try {
      sr = new ServletRunner(new File("web/WEB-INF/web.xml"), "");
    } catch (Exception e) {
      fail("Could not initialize the servlet runner!");
      return;
    }
    // get a new client
    sc = sr.newClient();
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.alarmcfg.servlets.AddAlarm#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Ignore("Does not work.") 
  @Test
  public void testDoPostSuccess() {
    // create the post request.
    final WebRequest request = new PostMethodWebRequest("http://localhost/AddAlarm");

    // create a mock authenticator
    final AlarmInterfaceContainer mockContainer = context.mock(AlarmInterfaceContainer.class);

    // set this variable to be able to return it on the next call to
    // getAlarmInterfaces()
    AlarmInterfaceFactory.returnContainer = mockContainer;

    final AlarmInterface mockInterface = context.mock(AlarmInterface.class);
    final Vector<AlarmInterface> interfaces = new Vector<AlarmInterface>();
    interfaces.add(mockInterface);

    // create mock reports.
    final AlarmReport mockReport = context.mock(AlarmReport.class);
    final Vector<AlarmReport> reports = new Vector<AlarmReport>();
    reports.add(mockReport);

    final Enumeration<AlarmReportPrompt> prompts = (new Vector<AlarmReportPrompt>()).elements();

    // and set the expectations.
    try {
      context.checking(new Expectations() {

        {
          oneOf(mockContainer).getCurrentInterface();
          will(returnValue(mockInterface));
          oneOf(mockContainer).getElements();
          will(returnValue(interfaces.elements()));
          oneOf(mockInterface).isSelected();
          will(returnValue(true));
          oneOf(mockInterface).getName();
          will(returnValue("A test interface"));
          oneOf(mockInterface).getReports();
          will(returnValue(reports.elements()));
          allowing(mockReport).isEnabled();
          will(returnValue(true));
          allowing(mockReport).getId();
          will(returnValue("asdfhahsdf"));
          oneOf(mockReport).getName();
          will(returnValue("A test report"));
          oneOf(mockReport).getPrompts();
          will(returnValue(prompts));
        }
      });
    } catch (Exception e1) {
      e1.printStackTrace();
    }

    // set the parameters that we need for login. Only use the test string set
    // above.
    try {
      // get the context.
      final InvocationContext ic = sc.newInvocation(request);

      // create the login page servlet.
      final ExistingAlarms page = (ExistingAlarms) ic.getServlet();

      assertNull("A session already exists", ic.getRequest().getSession(false));

      // set our mock authenticator
      final HttpServletRequest postRequest = ic.getRequest();
      // postRequest.getSession().setAttribute("authenticator", mockAuth);

      // get the response.
      final HttpServletResponse postResponse = ic.getResponse();

      // and do the post.
      page.doPost(postRequest, postResponse);

      // now check that we have the right variables set.
      assertNotNull(postRequest.getSession().getAttribute("interfaces"));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Exception was thrown: " + e);
    }

    context.assertIsSatisfied();
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.alarmcfg.servlets.AddAlarm#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * For selecting the current interface. .
   */
  @Ignore("Does not work.") 
  @Test
  public void testDoGetSelectInterface() {
    // create the get
    final WebRequest request = new PostMethodWebRequest("http://localhost/ExistingAlarms?currentInterface=test");

    // create a mock authenticator
    final AlarmInterfaceContainer mockContainer = context.mock(AlarmInterfaceContainer.class);

    // set this variable to be able to return it on the next call to
    // getAlarmInterfaces()
    AlarmInterfaceFactory.returnContainer = mockContainer;

    final AlarmInterface mockInterface = context.mock(AlarmInterface.class);
    final Vector<AlarmInterface> interfaces = new Vector<AlarmInterface>();
    interfaces.add(mockInterface);

    // create mock reports.
    final AlarmReport mockReport = context.mock(AlarmReport.class);
    final Vector<AlarmReport> reports = new Vector<AlarmReport>();
    reports.add(mockReport);

    // create mock prompts.
    final Enumeration<AlarmReportPrompt> prompts = (new Vector<AlarmReportPrompt>()).elements();

    // and set the expectations.
    try {
      context.checking(new Expectations() {

        {
          oneOf(mockContainer).getCurrentInterface();
          will(returnValue(mockInterface));
          oneOf(mockContainer).getElements();
          will(returnValue(interfaces.elements()));
          oneOf(mockContainer).select("test");
          oneOf(mockInterface).isSelected();
          will(returnValue(true));
          oneOf(mockInterface).getName();
          will(returnValue("A test interface"));
          oneOf(mockInterface).getReports();
          will(returnValue(reports.elements()));
          allowing(mockReport).isEnabled();
          will(returnValue(true));
          allowing(mockReport).getId();
          will(returnValue("asdfhahsdf"));
          oneOf(mockReport).getName();
          will(returnValue("A test report"));
          oneOf(mockReport).getPrompts();
          will(returnValue(prompts));
        }
      });
    } catch (Exception e1) {
      e1.printStackTrace();
    }

    // set the parameters that we need for login. Only use the test string set
    // above.
    try {
      // get the context.
      final InvocationContext ic = sc.newInvocation(request);

      // create the login page servlet.
      final ExistingAlarms page = (ExistingAlarms) ic.getServlet();

      assertNull("A session already exists", ic.getRequest().getSession(false));

      // set our mock authenticator
      final HttpServletRequest getRequest = ic.getRequest();

      // get the response.
      final HttpServletResponse getResponse = ic.getResponse();

      // and do the post.
      page.doGet(getRequest, getResponse);

      // now check that we have the right variables set.
      assertNotNull(getRequest.getSession().getAttribute("interfaces"));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Exception was thrown: " + e);
    }

    context.assertIsSatisfied();
  }
}
