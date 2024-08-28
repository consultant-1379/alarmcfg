/**
 * 
 */
package com.ericsson.eniq.alarmcfg.reports;

import com.businessobjects.sdk.plugin.desktop.category.ICategory;
import com.crystaldecisions.sdk.occa.infostore.IInfoObject;
import com.crystaldecisions.sdk.occa.infostore.IInfoStore;

/**
 * @author eheijun
 * 
 * @param <V>
 *          the report type used for document interface
 * 
 */
public class BODocument<V extends IInfoObject> {

  private final IInfoStore infoStore;

  private final ICategory category;

  private final V document;

  /**
   * @param infoStore
   * @param document
   */
  public BODocument(final IInfoStore infoStore, final V document, final ICategory category) {
    super();
    this.infoStore = infoStore;
    this.document = document;
    this.category = category;
  }

  /**
   * @return the infoStore
   */
  public IInfoStore getInfoStore() {
    return infoStore;
  }

  /**
   * @return the category
   */
  public ICategory getCategory() {
    return category;
  }

  /**
   * @return the document
   */
  public V getDocument() {
    return document;
  }

  /**
   * @return the document id
   */
  public int getId() {
    final IInfoObject object = (IInfoObject) getDocument();
    return object.getID();
  }

  /**
   * @return the document title
   */
  public String getTitle() {
    final IInfoObject object = (IInfoObject) getDocument();
    return object.getTitle();
  }

}
