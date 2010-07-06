/** EventCapture.js
//
// A simple utility to perform needed
// event capture.
//
// @author wbossons
//*/


/** processEvent
//
// used by IE to process keydown events in fields.
// works also with GECKO browsers
//
//@description
// refer to processEvent in your event capture,
// should work whether using window or element level
// event capturing. Here is an example of a
// script that calls this function. **window.event** indicates
// IE event capture. Please note that Gecko always sends the event as
// the first argument in the function, therefore I am sending an empty string
// from IE so as not to confuse the browser. Please note also that Opera
// squawks by default as IE, so it will also be sending the window.event unless
// your Opera is configured to squawk otherwise.
//
// if (window.event) 
//     return processEvent('', 'form1:searchButton'); 
// else        
//     return processEvent('form1:searchButton');
//
// @author wbossons
//*/

function processEvent(evt, obj) {
    var keyCode;
    if (window.event) 
        keyCode = window.event.keyCode;
    else
        keyCode = evt.which;

        if (keyCode == 13) { 
            document.getElementById(obj).focus();
            document.getElementById(obj).click();
            return false;
        } else {
            return true;
        }
}