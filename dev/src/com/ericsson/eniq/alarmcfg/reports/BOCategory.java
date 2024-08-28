/**
 * 
 */
package com.ericsson.eniq.alarmcfg.reports;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.businessobjects.sdk.plugin.desktop.category.ICategory;
import com.businessobjects.sdk.plugin.desktop.webi.IWebi;
import com.crystaldecisions.sdk.exception.SDKException;
import com.crystaldecisions.sdk.occa.infostore.CeSecurityID;
import com.crystaldecisions.sdk.occa.infostore.IInfoObject;
import com.crystaldecisions.sdk.occa.infostore.IInfoObjects;
import com.crystaldecisions.sdk.occa.infostore.IInfoStore;
import com.ericsson.eniq.alarmcfg.exceptions.RepositoryException;

/**
 * @author eheijun
 * @copyright Ericsson (c) 2009
 */
public class BOCategory {

  private final IInfoStore infoStore;

  private final ICategory parentCategory;

  private final ICategory category;

  private final List<BOCategory> categories;

  private final List<BODocument<IWebi>> widocuments;

  private final int id;

  /**
   * Creates corporate category instance
   * 
   * @param category
   * @param parentCategory
   */
  public BOCategory(final IInfoStore infoStore) {
    this(infoStore, null, null);
  }

  /**
   * Creates category instance
   * 
   * @param category
   * @param parentCategory
   */
  public BOCategory(final IInfoStore infoStore, final ICategory category, final ICategory parentCategory) {
    super();
    this.infoStore = infoStore;
    this.category = category;
    this.parentCategory = parentCategory;
    this.categories = new ArrayList<BOCategory>(0);
    if (category == null) {
      this.id = CeSecurityID.Folder.CORPORATE_CATEGORIES;
      // Root category can not contain documents
      this.widocuments = null;
    } else {
      this.id = category.getID();
      this.widocuments = new ArrayList<BODocument<IWebi>>(0);
    }
  }

  /**
   * Reads all sub categories for category
   * 
   * @throws RepositoryException
   */
  public void refreshCategories() throws RepositoryException {
    // Search all sub categories
    final String sCategoryQuery = "SELECT SI_ID, SI_NAME, SI_KIND, SI_PATH, SI_DOCUMENTS FROM CI_INFOOBJECTS WHERE SI_PARENTID = "
        + id + " AND SI_KIND = '" + ICategory.CATEGORY_KIND + "'";
    try {
      categories.clear();
      final IInfoObjects objects = (IInfoObjects) infoStore.query(sCategoryQuery);
      for (final Iterator<IInfoObject> it = objects.iterator(); it.hasNext();) {
        final ICategory subCategory = (ICategory) it.next();
        final BOCategory bocategory = new BOCategory(infoStore, subCategory, this.category);
        categories.add(bocategory);
      }
    } catch (Exception e) {
      throw new RepositoryException(e.getMessage());
    }
  }

  private String getListOfDocumentIDs(final Set<Integer> documents) {
    String listOfDocumentIDs = "";
    for (final Iterator<Integer> it = documents.iterator(); it.hasNext();) {
      final Integer id = it.next();
      if (!listOfDocumentIDs.equals("")) {
        listOfDocumentIDs += ",";
      }
      listOfDocumentIDs += id;
    }
    return listOfDocumentIDs;
  }

  /**
   * Reads all webi documents for category.
   * 
   * @throws RepositoryException
   */
  public List<BODocument<IWebi>> refreshDocuments() throws RepositoryException {
    if (category == null) {
      throw new RepositoryException("No documents in the category");
    }

    // we need only webi document support
    final List<BODocument<IWebi>> alldocuments = new ArrayList<BODocument<IWebi>>();

    widocuments.clear();

    String listOfIDs = "";
    try {
      listOfIDs = getListOfDocumentIDs(category.getDocuments());
    } catch (Exception e) {
      throw new RepositoryException(e.getMessage());
    }

    if (!listOfIDs.equals("")) {
      // Query for all InfoObjects in the collection.
      final String sDocumentQuery = "SELECT SI_ID, SI_NAME, SI_KIND FROM CI_INFOOBJECTS WHERE SI_ID IN (" + listOfIDs
          + ")" + " AND SI_KIND = '" + IWebi.KIND+ "'";
      try {
        final IInfoObjects objects = infoStore.query(sDocumentQuery);
        for (final Iterator<IInfoObject> it = objects.iterator(); it.hasNext();) {
          final IInfoObject currentObject = it.next();
          final BODocument<IWebi> document = new BODocument<IWebi>(infoStore, (IWebi) currentObject, category);
          widocuments.add(document);
        }
      } catch (Exception e) {
        throw new RepositoryException(e.getMessage());
      }
    }

    alldocuments.addAll(widocuments);
    return alldocuments;
  }

  /**
   * @return the infoParentObject
   */
  public IInfoObject getInfoParentObject() {
    return parentCategory;
  }

  /**
   * @return the infoObject
   */
  public IInfoObject getInfoObject() {
    return category;
  }

  /**
   * @return the categories
   */
  public List<BOCategory> getCategories() {
    return categories;
  }

  /**
   * @return the widocuments
   */
  public List<BODocument<IWebi>> getWidocuments() {
    return widocuments;
  }

  /**
   * @return the name
   */
  public String getName() {
    return category.getTitle();
  }

  /**
   * @return the full name
   */
  public String getFullName() {
    String path = "\\";
    try {
      for (String pathElement : this.category.getPath()) {
        path += pathElement + "\\"; 
      }
    } catch (SDKException e) {
      path += ""; 
    }
    return path + category.getTitle();
  }

}
