/**
 * 
 */
package com.ericsson.eniq.alarmcfg.prompts;

import java.util.List;
import java.util.Vector;

/**
 * @author ecarbjo
 * 
 */
public class DefaultAlarmReportPrompt extends AlarmReportPromptAdapter {

  private String name;

  private int type;

  final private List<String> values = new Vector<String>();

  final private List<String> previousValues = new Vector<String>();

  /**
   * @param name
   *          the name of the prompt
   * @param type
   *          the type of the prompt
   */
  public DefaultAlarmReportPrompt(final String name, final int type) {
    this.name = name;
    this.type = type;
  }

  /**
   * Constructor for setting type, name and value.
   * 
   * @param name
   * @param value
   * @param type
   */
  public DefaultAlarmReportPrompt(final String name, final String value, final int type) {
    this(name, type);
    this.value = value;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.alarmcfg.prompts.AlarmReportPrompt#getDefaultValues()
   */
  @Override
  public String[] getDefaultValues() {
    return (String[]) values.toArray();
  }

  /*
   * (non-Javadoc)
   * @see com.ericsson.eniq.alarmcfg.prompts.AlarmReportPromptAdapter#getPreviousValues()
   */
  @Override
  public String[] getPreviousValues() {
    return (String[]) previousValues.toArray();
  }
  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.prompts.AlarmReportPrompt#getName()
   */
  @Override
  public String getName() {
    return name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.prompts.AlarmReportPrompt#getType()
   */
  @Override
  public int getType() {
    return type;
  }

  /**
   * Adds a default value to the array.
   * 
   * @param value
   *          the value to add.
   */
  public void addDefaultValue(final String value) {
    if (value != null) {
      values.add(value);
    }
  }

  /**
   * Clears the default values array.
   */
  public void clearDefaultValues() {
    previousValues.addAll(values);
    values.clear();
  }

}
