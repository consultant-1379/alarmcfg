package com.ericsson.eniq.alarmcfg.reports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.logging.Logger;

import com.businessobjects.rebean.wi.DocumentInstance;
import com.businessobjects.rebean.wi.PaginationMode;
import com.businessobjects.rebean.wi.Prompt;
import com.businessobjects.rebean.wi.PromptType;
import com.businessobjects.rebean.wi.Prompts;
import com.businessobjects.rebean.wi.Report;
import com.businessobjects.rebean.wi.ReportEngine;
import com.businessobjects.rebean.wi.ReportEngines;
import com.businessobjects.rebean.wi.Reports;
import com.businessobjects.rebean.wi.ValueFromLov;
import com.businessobjects.rebean.wi.Values;
import com.businessobjects.rebean.wi.XMLView;

import com.ericsson.eniq.alarmcfg.common.PromptNameValuePair;
import com.ericsson.eniq.alarmcfg.database.DatabaseSession;
import com.ericsson.eniq.alarmcfg.exceptions.DatabaseException;
import com.ericsson.eniq.alarmcfg.interfaces.AlarmInterface;
import com.ericsson.eniq.alarmcfg.prompts.AlarmReportPrompt;
import com.ericsson.eniq.alarmcfg.prompts.BOAlarmReportPrompt;

/**
 * @author ecarbjo
 * @author eheijun
 * @copyright Ericsson (c) 2009
 * 
 */
public class BOAlarmReport extends AlarmReportAdapter implements Serializable {

  private int boId;

  final private ReportEngines reportEngines;

  private static Logger log = Logger.getLogger(BOAlarmReport.class.getName());

  String threadName = Thread.currentThread().getName();
  /**
   * @param dbSession
   * @param document
   * @param alarmInterface
   * @param reportEngines
   */
  public BOAlarmReport(final DatabaseSession dbSession, final AlarmInterface alarmInterface,
      final ReportEngines reportEngines, final Boolean isReducedDelay, final BODocument<?> document) {
	 
    // we use lazy loading, null the vector until we actually need it.
    prompts = null;

    this.databaseSession = dbSession;
    this.alarmInterface = alarmInterface;
    this.reportEngines = reportEngines;

    // generate a new UUID for saving if needed.
    this.id = UUID.randomUUID().toString();

    this.boId = document.getId();
    this.name = document.getTitle();
    this.isReducedDelay = isReducedDelay;

    // default the status to ACTIVE
    this.status = AlarmReportAdapter.ACTIVE;

    log.finest( threadName+" Instantiated a new BOAlarmReport object (" + name + ") " );
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.reports.AlarmReport#getReportPrompts()
   */
  @Override
  public Enumeration<AlarmReportPrompt> getPrompts() {
    // do lazy loading of the alarm prompts.
    if (prompts == null) {
      fetchAlarmPrompts();
    }

    // return the enumeration to the prompts vector.
    return this.prompts.elements();
  }

  /**
   * Fetch the alarm report prompts for this report from the BO repository
   */
  private void fetchAlarmPrompts() {
   // first get the report engine, and then load the document by the boId.
	   DocumentInstance document = null ;
	   ReportEngine reportEngine = null ;
	  try{
		  reportEngine = reportEngines.getService(ReportEngines.ReportEngineType.WI_REPORT_ENGINE);
		  document = reportEngine.openDocument(boId);
		  final Prompts boPrompts = document.getPrompts();
    // create a vector for the new prompts.
		  this.prompts = new Vector<AlarmReportPrompt>();

    // add all prompts as BOAlarmReportPrompts into the vector.
		  for (int i = 0; i < boPrompts.getCount(); i++) {
			  prompts.add(new BOAlarmReportPrompt(boPrompts.getItem(i)));
		  }
		  Collections.sort(prompts, AlarmReportPrompt.PROMPTNAMECOMPARATOR);

		  log.fine(threadName+ " Fetched " + prompts.size() + " prompts for the document " + name + " ");
  
	  }catch (Exception e){
		  log.severe(threadName+ " Exception while getting the document instance ");
	  }
	 	  }
  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.reports.AlarmReportAdapter#update()
   */
  @Override
  public void update() {
    // Don't do anything here. The BOAlarmReports are read via the
    // BOAlarmReportLister. After this fact the object is static and if it
    // should be refreshed, the lister should create a new instance of the
    // object.
  }

  /**
   * Set the BO alarm report id for this document.
   * 
   * @param id
   *          the id that references this report in the BO repository.
   */
  public void setBOId(final int id) {
    this.boId = id;
  }

  /**
   * @return the set boId for this alarm report.
   */
  public int getBOId() {
    return this.boId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.reports.AlarmReportAdapter#remove()
   */
  @Override
  public void remove() throws DatabaseException {
    // don't do anything here, we don't allow the deletion of BOAlarmReports. If
    // the association in the DWH repository is to be removed, use a
    // DWHAlarmReport object instead.
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.reports.AlarmReportAdapter#saveStatus()
   */
  @Override
  protected void saveStatus() {
    // we don't do anything here because we don't save status for
    // BOAlarmReports. The status is saved when the whole object is saved to the
    // association database via the save() method.
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.reports.AlarmReportAdapter#refresh()
   */
  @Override
  public String refresh(final List<PromptNameValuePair> nameValues) {
	  String lines = "";

    // first get the report engine, and then load the document by the boId.
    final ReportEngine reportEngine = reportEngines.getService(ReportEngines.ReportEngineType.WI_REPORT_ENGINE);
    final DocumentInstance document = reportEngine.openDocument(boId);
    try {
    // refresh data
    log.finest(threadName+" Calling document refresh ");
    document.refresh();
    
    log.finest(threadName+" Calling document.refresh Done ");
    final Prompts boPrompts = document.getPrompts();

    for (int ind = 0; ind < boPrompts.getCount(); ind++) {
      final Prompt prompt = boPrompts.getItem(ind);
        for (PromptNameValuePair nameValue : nameValues) {
          if (prompt.hasLOV() && prompt.isConstrained()) {
            final Values allLovValues = prompt.getLOV().getAllValues();
            final List<ValueFromLov> tmpList = new ArrayList<ValueFromLov>();
            for (int knd = 0; knd < allLovValues.getCount(); knd++){
              final String lovValue = allLovValues.getValue(knd);
              if (prompt.getType().equals(PromptType.Mono)) {
                final String testValue = nameValue.getValue()[0];
                if (lovValue.equals(testValue)) {
                  tmpList.add(allLovValues.getValueFromLov(knd));
                }
              } else {
                for (String testValue : nameValue.getValue()) {
                  if (lovValue.equals(testValue)) {
                    tmpList.add(allLovValues.getValueFromLov(knd));
                  }
                }
              }
            }
            ValueFromLov[] tmpArray = new ValueFromLov[tmpList.size()];
            for (int jnd = 0; jnd < tmpArray.length; jnd++) {
              tmpArray[jnd] = tmpList.get(jnd);
            }
            prompt.enterValues(tmpArray);
          } else {
            if (nameValue.getName().equals(prompt.getName())) {
              prompt.enterValues(nameValue.getValue());
            }
          }
        }
    }
    
    document.setPrompts();
    final Reports reports = document.getReports();  
    final int iReportCount = reports.getCount();
    if (iReportCount > 0) {

      // Get first report
      final Report report = reports.getItem(0);
      report.setPaginationMode(PaginationMode.Listing);
      //report.setPaginationMode(PaginationMode.QuickDisplay);

      // Get the XML View
      final XMLView cdzXmlView = (XMLView) report.getXMLView(XMLView.CONTENT);

      lines = cdzXmlView.getContent();
      }
    }
    	catch( Exception e){
    		
		 	 log.severe(threadName+" Exception while refreshing the document " +e);
	
    	}
    	finally{
    		try{
    			document.closeDocument();
    		    			
    			log.fine(threadName+" Document is closed successfully ");
    	  		if(reportEngine!=null)
    	  			{
    	  				reportEngine.close();
    	  				log.finest(threadName+" ReportEngine " + this.name + "is closed successfully " );
    	  			}
    	  	}catch(Exception e){
    		 
   		 	 log.severe(threadName+" Exception while closing the document and reportengine " +e);
    		}
    	  }

    		return lines;
	  }

  }

 

