/**
 * NOTE: REQUIRES A DATABASE CONNECTION!
 */
package com.ericsson.eniq.alarmcfg.techpacks;

import static org.junit.Assert.*;

import java.util.Enumeration;
import java.util.Vector;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.ericsson.eniq.alarmcfg.config.AlarmProperties;
import com.ericsson.eniq.alarmcfg.database.DatabaseConnector;
import com.ericsson.eniq.alarmcfg.database.DatabaseConnectorFactory;
import com.ericsson.eniq.alarmcfg.database.DatabaseSession;
import com.ericsson.eniq.repository.ETLCServerProperties;

/**
 * @author ecarbjo
 * @author eheijun
 * 
 */
@Ignore
public class DWHInfoFactoryTest {

  private static DWHInfoFactory factory = null;

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    final ETLCServerProperties etlcServerProperties = new ETLCServerProperties();

    etlcServerProperties.getProperty("ENGINE_DB_URL");
    etlcServerProperties.getProperty("ENGINE_DB_USERNAME");
    etlcServerProperties.getProperty("ENGINE_DB_PASSWORD");
    etlcServerProperties.getProperty("ENGINE_DB_DRIVERNAME");
    
    
    final AlarmProperties alarmProperties = new AlarmProperties();

    // the rest should be the same if user/pass doesn't change unexpectedly
    alarmProperties.addProperty(AlarmProperties.APPLICATIONNAME, "alarmcfg");

    final DatabaseConnector databaseConnector = DatabaseConnectorFactory.createDatabaseConnector();
    final DatabaseSession database = databaseConnector.createDatabaseSession(etlcServerProperties, alarmProperties);
    factory = new DefaultDWHInfoFactory(database);
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
   * Test method for
   * {@link com.ericsson.eniq.alarmcfg.techpacks.DefaultDWHInfoFactory#getInformation()}
   * .
   */
  @Test
  public void testGetInformation() {
    final Vector<TechPack> techPacks = factory.getInformation();
    assertNotNull(techPacks);

    final Vector<String> checkVector = new Vector<String>();

    final Enumeration<TechPack> elements = techPacks.elements();
    while (elements.hasMoreElements()) {
      final TechPack element = elements.nextElement();
      assertTrue(!checkVector.contains(element.getName()));

      checkVector.add(element.getName());
    }
  }

}
