/**
 * Small JavaScript for listing and populating the menus for step 2/2 of the adding reports view. 
 */

/**
 * @param menu
 * @return
 */
function populate(menu) {
	var techpacks = document.getElementById('select_techpacks');
	var levels = document.getElementById('select_levels');
	var types = document.getElementById('select_types');
	var basetables = document.getElementById('select_basetables');

	switch (menu) {
	default:
	case "techpack":
		clear(types);
		clear(levels);
		clear(basetables);
		show("", 'select_techpacks');
		break;
	case "type":
		clear(levels);
		clear(basetables);
		if (techpacks.selectedIndex > 0) {
			show('' + (techpacks.selectedIndex - 1), 'select_types');
		} else {
			clear(types);
		}
		break;
	case "level":
		clear(basetables);
		if (types.selectedIndex > 0) {
			show('' + (techpacks.selectedIndex - 1) + '-'
				+ (types.selectedIndex - 1), 'select_levels');
		} else {
			clear(levels);
		}
		break;
	case "basetable":
		// always clear the base tables. This is to make sure that the selected index is
		// indeed 0, but the time the status for the button is checked. All other combo
		// boxes can be directly populated via the asynchronous call without problems.
		clear(basetables);
		if (levels.selectedIndex > 0) {
			show('' + (techpacks.selectedIndex - 1) + '-'
				+ (types.selectedIndex - 1) + '-' + (levels.selectedIndex - 1),
				'select_basetables');
		}
		break;
	}
	
	// also check the status of the add button
	checkAddStatus();
}

 // TR:HK96425: Started
/**
 * validate the Browser request and return requst  
 * @return http object
 */
function getHTTPObject() {
	var xmlHTTP;
	if(typeof(XMLHttpRequest)!='undefined'){
		// For IE 7 and Mozilla 
		xmlHTTP = new XMLHttpRequest();
	}
	else {
		try{
			//Check for IE 7 of above request does not work 
			xmlHTTP = new ActiveXObject('MSXML2.XMLHTTP.6.0');
		}
		catch(err){}
		try{
			if(!xmlHTTP){
				//Request for IE 6.0
				xmlHTTP = new ActiveXObject('MSXML2.XMLHTTP.3.0'); 
			}
		}
		catch(err){
			
			//loginform.browsermessage.value = "Browser version is not supported";
			
		}
	}

	return xmlHTTP;
}
 /**
 * validate the Browser  
 * @return http object
 */
 function detectBrowser()
 {
	 var browser=navigator.appName;
	 var usrAgent = navigator.userAgent;

	 if (browser=="Microsoft Internet Explorer"){
		 var MSEIStatus = /MSIE (\d+\.\d+);/.test(usrAgent);
		 if(MSEIStatus)
		 {
			 var ieversion =new Number(RegExp.$1);
			 if(ieversion<6){
				 document.loginform.browserstatusmessage.value = "Unsupported browser version";
			 }
			 else
				 document.loginform.browserversionmessage.value = "IE Browser version is " + ieversion;
		 }
	 }
	 else if (browser == "Netscape"){
		 var firefoxstatus = /Firefox[\/\s](\d+\.\d+)/.test(usrAgent);
		 if(firefoxstatus){
			 var ffversion =new Number(RegExp.$1);
			 document.loginform.browserversionmessage.value = "Firefox Browser version is " + ffversion;
		 }	 
		 else{
			 var tempindex = usrAgent.lastIndexOf("rv:");
			 var tempffversion = usrAgent.substr(tempindex+3, 3);
			 document.loginform.browserversionmessage.value = "Mozilla Browser version is " + tempffversion;
			 if(tempffversion < 1.7){
				 document.loginform.browserstatusmessage.value = "Unsupported browser version";
			 }
		 }
	 }
	 else
	 {
		 document.loginform.browserstatusmessage.value = "Browser is not supported";
	 }
 }
//TR:HK96425: End
/**
 * get the combo box data via an Ajax call, and then update the target combo box.
 * @return nothing
 */
function show(path, target) {
//TR:HK96425: Started
// used customzied method instead of new XMLHttpRequest()
	var req = new getHTTPObject();
//end
	req.open('GET', 'AjaxReport?path=' + path, true);
	req.onreadystatechange = function(aEvt) {
		if (req.readyState == 4) {
			var select = document.getElementById(target);
			if (req.status == 200) {
				// null the select list.
				select.innerHTML = "";

				var emptyOpt = document.createElement('option');
				addToSelect(select, emptyOpt);

				var options = req.responseText.split("\n");
				var i = 0;
				//variable created to remove extra space
				var tmpoption;
				for (i = 0; i < options.length; i++) {
					// create a new option element.
					var opt = document.createElement('option');
					if (options[i] != "") {
						//TR:HK96425: Started
						//Removed spaces
						//opt.text = options[i];
						tmpoption = options[i].replace(/\s+/,'');
						opt.text = tmpoption;
						// End
						addToSelect(select, opt);
					}
				}
				select.disabled = false;
			} else {
				select.innerHTML = "";
			}
		}
	};
	req.send(null);
}

/**
 * Add a new option to the selection box.
 * @param select
 * @param option
 * @return
 */
function addToSelect(select, option) {
	try {
		// standard compliant.
		select.add(option, null);
	} catch (ex) {
		// IE.
		select.add(option);
	}
}

/**
 * Clear the "box" selection box, so that there are no options in it. 
 * @param box the select box to clear/disable
 * @return
 */
function clear(box) {
	while (box.length > 0) {
		box.remove(0);
	}

	box.disabled = true;
}

/**
 * Enables the add button if the base table is selected.
 * @return nothing.
 */
function checkAddStatus() {
	var select = document.getElementById('select_basetables');
	if (select != null) {
		var addButton = document.getElementById('add_alarm_button');
		if (select.selectedIndex > 0) {
			addButton.disabled = false;
		} else {
			addButton.disabled = true;
		}
	}
}
function showLimitMsgAlert(promptName)
{	
	var promptValue = document.getElementById(promptName);
	
	if (promptValue.value.length >= 255) { 
		alert('Prompt value reached to maximum limit, please check Alarm User Guide, section: Adding and Activating Alarm Reports, for more information.');
	}
}
