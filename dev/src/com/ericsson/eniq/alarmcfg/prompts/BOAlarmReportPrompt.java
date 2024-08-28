/**
 * 
 */
package com.ericsson.eniq.alarmcfg.prompts;

import com.businessobjects.rebean.wi.Prompt;

/**
 * @author ecarbjo
 * 
 */
public class BOAlarmReportPrompt extends AlarmReportPromptAdapter {

  final private Prompt boPrompt;

  /**
   * Initializes a report prompt with the help of a BO Prompt object.
   * 
   * @param boPrompt
   */
  public BOAlarmReportPrompt(final Prompt boPrompt) {
    this.boPrompt = boPrompt;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.alarmcfg.prompts.AlarmReportPrompt#getDefaultValues()
   */
  @Override
  public String[] getDefaultValues() {
    return boPrompt.getDefaultValues();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.prompts.AlarmReportPrompt#getValue()
   */
  @Override
  public String[] getPreviousValues() {
    return this.boPrompt.getPreviousValues();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.prompts.AlarmReportPrompt#getName()
   */
  @Override
  public String getName() {
    return this.boPrompt.getName();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.prompts.AlarmReportPrompt#getType()
   */
  @Override
  public int getType() {
    return this.boPrompt.getType().value();
  }
}
