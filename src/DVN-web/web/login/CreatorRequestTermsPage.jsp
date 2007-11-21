<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
    <f:subview id="CreatorRequestTermsPageView">
        <ui:form  id="creatorRequestForm">
          <input type="hidden" name="pageName" value="CreatorRequestPage"/>
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>

            <div id="dvn_createDvRequest" class="dvn_section dvn_overflow">
    	<div id="requestHeader">Send a Request <span>&gt; Create a Dataverse</span></div>
        <div id="requestTimeline" style="background-position: 0 12px;">
        	<div class="requestTimelinePoint" style="left: 20px;"><img src="/dvn/resources/lrg-blue-bullet.gif" class="vdcNoBorder" alt="" /><br /><strong>Register/Log In</strong></div>
        	<div class="requestTimelinePoint" style="left: 289px;"><img src="/dvn/resources/lrg-blue-bullet.gif" class="vdcNoBorder" alt="" /><br /><strong style="font-weight: bold;">Terms of Use</strong></div>
        	<div class="requestTimelinePoint" style="left: 558px;"><img src="/dvn/resources/lrg-grey-bullet.gif" class="vdcNoBorder" alt="" /><br /><strong>Comments</strong></div>
        	<div class="requestTimelinePoint" style="left: 827px;"><img src="/dvn/resources/lrg-grey-bullet.gif" class="vdcNoBorder" alt="" /><br /><strong>Submit</strong></div>
        </div>
        <div id="requestContent"><div class="dvn_margin12">
        <div id="requestContentDescLeft">Please read and agree to the terms of use outlined below.  Check the "I agree and accept" box below and continue.</div>
        <div id="requestContentFormRight">
        <span id="actionTitle">Terms of Use</span>
        <br />
            <textarea name="termsofuse" cols="90" rows="20">
Terms of use here
            </textarea>
        <br />
        <input type="checkbox" name="agree" /> <span id="agreeCheckbox">I agree and accept the IQSS DVN terms and conditions updated October 22, 2007.</span>
            <div style="padding-top: 20px; text-align: center;">
            <input type="submit" name="continue" value="Continue" />
            <input type="submit" name="cancel" value="Cancel" style="margin-left: 30px" />
            </div>
        </div>
        </div></div>
    </div>

        </ui:form>
    </f:subview>
</jsp:root>
