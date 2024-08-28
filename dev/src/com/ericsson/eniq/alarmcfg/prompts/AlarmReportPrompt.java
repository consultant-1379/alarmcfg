package com.ericsson.eniq.alarmcfg.prompts;

import java.util.Comparator;

/**
 * @author ecarbjo
 * 
 */
public interface AlarmReportPrompt {

  /**
   * @return the name (key) of the prompt.
   */
  String getName();

  /**
   * @return the value of the prompt, or null if none is set.
   */
  String getValue();

  /**
   * Get the type of this prompt.
   * 
   * @return the type in the form of an integer.
   */
  int getType();

  /**
   * Get the default values for this prompt.
   * 
   * @return an array of the default values for this prompt.
   */
  String[] getDefaultValues();

  /**
   * Get the previous values for this prompt.
   * 
   * @return an array of the previous values for this prompt.
   */
  String[] getPreviousValues();

  /**
   * Set the value for this prompt.
   * 
   * @param value
   *          the value to set.
   */
  void setValue(String value);

  /**
   * Checks if the prompt is settable by the user. This checks if the prompts is
   * present in the ignore list and thus if it should be shown to the user. If
   * it is not in the ignore list (defined in the Constants class) this method
   * should return true.
   * 
   * @return false if the prompt is present in the IGNORE_PROMPTS array (or
   *         because of another reason should be filtered away), true otherwise.
   */
  boolean isSettable();

  /**
   * Sorter for prompts
   */
  Comparator<AlarmReportPrompt> PROMPTNAMECOMPARATOR = new Comparator<AlarmReportPrompt>() {

    public int compare(final AlarmReportPrompt d1, final AlarmReportPrompt d2) {
      final String s1 = d1.getName();
      final String s2 = d2.getName();
      return s1.compareTo(s2);
    }
  };

}
