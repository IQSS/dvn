<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
          xmlns:h="http://java.sun.com/jsf/html" 
          xmlns:jsp="http://java.sun.com/JSP/Page" 
          xmlns:ui="http://www.sun.com/web/ui"
          xmlns:tiles="http://struts.apache.org/tags-tiles"
          xmlns:c="http://java.sun.com/jsp/jstl/core">
    
    <f:subview id="termsOfUsePageView">
        
        <ui:form  id="form1">
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/> 
            <input type="hidden" value="TermsOfUsePage" name="pageName"/>
       
              <div id="dvn_createDvRequest" class="dvn_section dvn_overflow">
    	<div id="requestHeader">Terms of Use <span>&gt; Create a Dataverse</span></div>
        <div id="requestContent"><div class="dvn_margin12">
        <div id="requestContentDescLeft">Please read and agree to the terms of use outlined below.  Check the "I agree and accept" box below and continue.</div>
        <div id="requestContentFormRight">
        <h:outputText value="Terms of Use" styleClass="reqContentActionTitle" />
        <br />
<div id="requestDataverseTerms">
<h:outputText value="#{AccountTermsOfUsePage.termsOfUse}" escape="false"/>
</div>
        <br />
                        
                         <ui:panelGroup  style="padding-top: 20px;" block="true">
                            <h:selectBooleanCheckbox id="termsAccepted" required="true" value="#{AccountTermsOfUsePage.termsAccepted}" />
                            <h:outputText styleClass="agreeCheckbox" value="I agree and accept the IQSS DVN terms of use." />
                        </ui:panelGroup>
                        
                        <ui:panelGroup block="true" style="padding-top: 20px; text-align: center">
                            <h:commandButton  id="termsButton" value="Continue" action="#{AccountTermsOfUsePage.acceptTerms_action}"/>
                        </ui:panelGroup>
                        
        </div>
        </div></div>
    </div>

        </ui:form>
    </f:subview>
</jsp:root>
