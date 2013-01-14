/** popupSupport.js 
*
* additional functions to support 
* Matt Kruse's popup.js which
* is used on this site
*
* @author wbossons
*
*/

// Create PopupWindow objects


var helpPopup = new PopupWindow("helpDiv");
helpPopup.offsetX=-25;
helpPopup.offsetY=15;

//
//@param anchorname the name applied to the a element
//@param message the help text
//@param heading the heading to apply to the help popup
// because it is overloaded. 
// wbossons April 2007
function popupInlineHelp(anchorname, message, heading, evt) {
    message = "<div id=\"vdcInlineHelpWrapperDiv\" class=\"vdcInlineHelpWrapper\">&nbsp;&nbsp;" + heading + "<p id=\"helpContentsDiv\" class=\"vdcInlineHelpContents\">" + message + "</p></div>";
    helpPopup.populate(message);
    rootScrollTopReference = document.documentElement.scrollTop ? document.documentElement.scrollTop : document.body.scrollTop; //true? ie7version otherwise ie6 version
    rootScrollLeftReference = document.documentElement.scrollLeft ? document.documentElement.scrollLeft : document.body.scrollLeft; 
    if (navigator.userAgent.indexOf("Safari") != -1 && navigator.userAgent.indexOf("Macintosh") != -1) {
        xCoordinate = window.event.clientX - document.getElementById('tooltipDiv').offsetParent.offsetLeft;
        yCoordinate = window.event.clientY - document.getElementById('tooltipDiv').offsetParent.offsetTop;
    } else if (window.event == null) {
        xCoordinate = evt.pageX - document.getElementById('helpDiv').offsetParent.offsetLeft;
        yCoordinate = evt.pageY - document.getElementById('helpDiv').offsetParent.offsetTop;
    } else if (window.event) {
        xCoordinate = window.event.clientX - document.getElementById('helpDiv').offsetParent.offsetLeft + rootScrollLeftReference;
        yCoordinate = window.event.clientY - document.getElementById('helpDiv').offsetParent.offsetTop + rootScrollTopReference;
    }
    helpPopup.showPopup(anchorname, parseInt(xCoordinate), yCoordinate);
}

function hideInlineHelp() {
    helpPopup.hidePopup();
}

/* toolbar help
 *
 * functions to support
 * minimal gui popups
 */

var tooltip = new PopupWindow("tooltipDiv");
tooltip.offsetX=-30;
tooltip.offsetY=10;

//
//@param anchorname the name applied to the a element
//@param message the help text
//@param heading the heading to apply to the help popup
// because it is overloaded. 
// wbossons April 2007

function popupTooltip(anchorname, message, heading, evt) {
    if (heading != "")
        message = "<div id=\"vdcTooltipWrapperDiv\" class=\"vdcTooltipWrapper\">&nbsp;&nbsp;" + heading + "<p id=\"tooltipContentsDiv\" class=\"vdcTooltipContents\">" + message + "</p></div>";
    else
        message = "<p id=\"tooltipContentsDiv\" class=\"vdcTooltipContents\">" + message + "</p>";
    tooltip.populate(message);
    rootScrollTopReference = document.documentElement.scrollTop ? document.documentElement.scrollTop : document.body.scrollTop; //true? ie7version otherwise ie6 version
    rootScrollLeftReference = document.documentElement.scrollLeft ? document.documentElement.scrollLeft : document.body.scrollLeft;
    if (navigator.userAgent.indexOf("Safari") != -1 && navigator.userAgent.indexOf("Macintosh") != -1) {
        xCoordinate = window.event.clientX - document.getElementById('tooltipDiv').offsetParent.offsetLeft;
        yCoordinate = window.event.clientY - document.getElementById('tooltipDiv').offsetParent.offsetTop;
    } else if (window.event == null) {
        xCoordinate = evt.pageX - document.getElementById('tooltipDiv').offsetParent.offsetLeft;
        yCoordinate = evt.pageY - document.getElementById('tooltipDiv').offsetParent.offsetTop;
    } else if (window.event) {
        xCoordinate = window.event.clientX - document.getElementById('tooltipDiv').offsetParent.offsetLeft + rootScrollLeftReference;
        yCoordinate = window.event.clientY - document.getElementById('tooltipDiv').offsetParent.offsetTop + rootScrollTopReference;
    }
    tooltip.showPopup(anchorname, parseInt(xCoordinate), yCoordinate);
}


function hideTooltip() {
    tooltip.hidePopup();
}

var popup = new PopupWindow("popupDiv");
popup.offsetX=-400;
popup.offsetY=10;

//
//@param anchorname the name applied to the a element
//@param message the help text
//@param heading the heading to apply to the help popup
// because it is overloaded. 
// wbossons April 2007
function popupPopup(anchorname, message, heading, evt) {
    if (arguments.length = 5)
        closeText = arguments[4];
    else 
        closeText = "";
    if (heading != "")
        message = "<div id=\"vdcPopupWrapperDiv\" class=\"vdcPopupWrapper\">&nbsp;&nbsp;" + heading + "<p id=\"PopupContentsDiv\" class=\"vdcPopupContents\">" + message + "</p></div>";
    else
        message = "<p id=\"popupContentsDiv\" class=\"vdcPopupContents\">" + closeText + message + "</p>";
    popup.populate(message);
    rootScrollTopReference = document.documentElement.scrollTop ? document.documentElement.scrollTop : document.body.scrollTop; //true? ie7version otherwise ie6 version
    rootScrollLeftReference = document.documentElement.scrollLeft ? document.documentElement.scrollLeft : document.body.scrollLeft;
    if (navigator.userAgent.indexOf("Safari") != -1 && navigator.userAgent.indexOf("Macintosh") != -1) {
        xCoordinate = window.event.clientX - document.getElementById('tooltipDiv').offsetParent.offsetLeft;
        yCoordinate = window.event.clientY - document.getElementById('tooltipDiv').offsetParent.offsetTop;
    } else if (window.event == null) {
        xCoordinate = evt.pageX - document.getElementById('popupDiv').offsetParent.offsetLeft;
        yCoordinate = evt.pageY - document.getElementById('popupDiv').offsetParent.offsetTop;
    } else if (window.event) {
        xCoordinate = window.event.clientX - document.getElementById('popupDiv').offsetParent.offsetLeft + rootScrollLeftReference;
        yCoordinate = window.event.clientY - document.getElementById('popupDiv').offsetParent.offsetTop + rootScrollTopReference;
    }
    popup.showPopup(anchorname, parseInt(xCoordinate), yCoordinate);
}

function hidePopup() {
    popup.hidePopup();
}
