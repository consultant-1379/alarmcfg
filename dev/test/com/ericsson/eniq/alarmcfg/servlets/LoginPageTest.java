/**
 * Test class for the LoginPage servlet
 */
package com.ericsson.eniq.alarmcfg.servlets;

import static org.junit.Assert.*;

import java.io.File;

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

import com.ericsson.eniq.alarmcfg.authenticate.AuthSession;
import com.ericsson.eniq.alarmcfg.authenticate.Authenticator;
import com.ericsson.eniq.alarmcfg.exceptions.AuthenticationException;
import com.meterware.httpunit.GetMethodWebRequest;
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
public class LoginPageTest {

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
   * {@link com.ericsson.eniq.alarmcfg.servlets.LoginPage#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * . This test doesn't really do anything since the doGet method shouldn't
   * return any values (we use post for login)
   */
  @Ignore("Does not work.") 
  @Test
  public void testDoGet() {
    final WebRequest request = new GetMethodWebRequest("http://localhost/LoginPage");

    try {
      final InvocationContext ic = sc.newInvocation(request);

      assertNull("A session already exists", ic.getRequest().getSession(false));

      // get the response
      ic.getServletResponse();

    } catch (Exception e) {
      e.printStackTrace();
      fail("Exception was thrown: " + e);
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.alarmcfg.servlets.LoginPage#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Ignore("Does not work.") 
  @Test
  public void testDoPostSuccess() {
    // create the post request.
    final WebRequest request = new PostMethodWebRequest("http://localhost/LoginPage");

    // create a mock authenticator
    final Authenticator mockAuth = context.mock(Authenticator.class);
    final AuthSession mockSession = context.mock(AuthSession.class);

    final String testString = "some";

    // and set the expectations.
    try {
      context.checking(new Expectations() {

        {
          oneOf(mockAuth).getAuthSession(testString, testString, testString, testString);
          will(returnValue(mockSession));
          // oneOf (mockSession).getUserName();
          // will(returnValue("administrator"));
        }
      });
    } catch (AuthenticationException e1) {
      e1.printStackTrace();
    }

    // set the parameters that we need for login. Only use the test string set
    // above.
    request.setParameter("username", testString);
    request.setParameter("password", testString);
    request.setParameter("CMS", testString);
    request.setParameter("authtype", testString);

    try {
      // get the context.
      final InvocationContext ic = sc.newInvocation(request);

      // create the login page servlet.
      final LoginPage page = (LoginPage) ic.getServlet();

      assertNull("A session already exists", ic.getRequest().getSession(false));

      // set our mock authenticator
      final HttpServletRequest postRequest = ic.getRequest();
      postRequest.getSession().setAttribute("authenticator", mockAuth);

      // get the response.
      final HttpServletResponse postResponse = ic.getResponse();

      // and do the post.
      page.doPost(postRequest, postResponse);

      // now check that we have the right variables set.
      assertNotNull(postRequest.getSession().getAttribute("AuthSession"));
      assertNotNull(postRequest.getSession().getAttribute("interfaces"));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Exception was thrown: " + e);
    }

    context.assertIsSatisfied();
  }

}
