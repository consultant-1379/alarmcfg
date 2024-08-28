/**
 * 
 */
package com.ericsson.eniq.alarmcfg.common;

import static org.junit.Assert.*;

import org.junit.Test;


/**
 * @author eheijun
 *
 */
public class PromptNameValuePairTest {

  private static final String TEST = "TEST";
  private static final String SPACE = "";
  private static final String ABC = "ABC";
  private static final String DEF = "DEF";

  /**
   * Test method for {@link com.ericsson.eniq.alarmcfg.common.PromptNameValuePair#PromptNameValuePair(java.lang.String, java.lang.String[])}.
   */
  @Test
  public void testPromptNameValuePairStringStringArray() {
    final String[] promptValue1 = {SPACE};
    final PromptNameValuePair pair1 = new PromptNameValuePair(TEST, promptValue1);
    assertTrue(TEST.equals(pair1.getName()));
    assertTrue(SPACE.equals(pair1.getValue()[0]));
    
    final String[] promptValue2 = {ABC};
    final PromptNameValuePair pair2 = new PromptNameValuePair(TEST, promptValue2);
    assertTrue(TEST.equals(pair2.getName()));
    assertTrue(ABC.equals(pair2.getValue()[0]));
    
    final String[] promptValue3 = {ABC, DEF};
    final PromptNameValuePair pair3 = new PromptNameValuePair(TEST, promptValue3);
    assertTrue(TEST.equals(pair3.getName()));
    assertTrue(ABC.equals(pair3.getValue()[0]));
    assertTrue(DEF.equals(pair3.getValue()[1]));
  }

}
