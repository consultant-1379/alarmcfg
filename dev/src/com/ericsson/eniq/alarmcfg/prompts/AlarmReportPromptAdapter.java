/**
 * 
 */
package com.ericsson.eniq.alarmcfg.prompts;

import com.ericsson.eniq.alarmcfg.common.Constants;

/**
 * This is an adapter class for the AlarmReportPrompt that will implement common
 * methods for the classes that inherit is.
 * 
 * @author ecarbjo
 */
public abstract class AlarmReportPromptAdapter implements AlarmReportPrompt {

  protected String value;

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.alarmcfg.prompts.AlarmReportPrompt#getDefaultValues()
   */
  @Override
  public abstract String[] getDefaultValues();

  
  @Override
  public abstract String[] getPreviousValues();
  
  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.prompts.AlarmReportPrompt#getName()
   */
  @Override
  public abstract String getName();

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.prompts.AlarmReportPrompt#getType()
   */
  @Override
  public abstract int getType();

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.prompts.AlarmReportPrompt#getValue()
   */
  @Override
  public String getValue() {
    return this.value;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.prompts.AlarmReportPrompt#isSettable()
   */
  @Override
  public boolean isSettable() {
    final String name = getName();

    // check if the prompt is row status. If so, return false
    if (name.equals(Constants.PROMPT_ROWSTATUS)) {
      return false;
    }

    // the prompt is not ignored, return true.
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.alarmcfg.prompts.AlarmReportPrompt#setValue(java.lang
   * .String)
   */
  @Override
  public void setValue(final String value) {
    this.value = value;
  }

}
