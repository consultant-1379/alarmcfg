/**
 * 
 */
package com.ericsson.eniq.alarmcfg.common;

/**
 * @author eheijun
 * @copyright Ericsson (c) 2009
 * 
 */
public class PromptNameValuePair {

  private String name;

  private String[] value;

  /**
   * @param name
   * @param value
   */
  public PromptNameValuePair(final String name, final String[] value) {
    this.name = name;
    this.value = value;
  }

  /**
   * 
   */
  public PromptNameValuePair() {
    super();
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name
   *          the name to set
   */
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * @return the value
   */
  public String[] getValue() {
    return value;
  }

  /**
   * @param value
   *          the value to set
   */
  public void setValue(final String[] value) {
    this.value = value;
  }

}
