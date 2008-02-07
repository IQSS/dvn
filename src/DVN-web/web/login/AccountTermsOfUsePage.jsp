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
       
              <ui:panelLayout styleClass="#{ (LoginWorkflowBean.creatorWorkflow or LoginWorkflowBean.contributorWorkflow) ? 'dvn_createDvRequest dvn_section dvn_overflow' : 'dvn_section'}">
              <ui:panelLayout styleClass="#{ (LoginWorkflowBean.creatorWorkflow or LoginWorkflowBean.contributorWorkflow) ? 'requestHeader dvn_overflow' : 'dvn_sectionTitle'}">
                      <h:outputText value="Create a New Account" rendered="#{LoginWorkflowBean.plainWorkflow}"/>
                      <h:outputText value="Create a New Account" rendered="#{LoginWorkflowBean.fileAccessWorkflow}"/>
                      <h:outputText value="Create Account &gt; Create Your Own Dataverse" rendered="#{LoginWorkflowBean.creatorWorkflow}"/>
                      <h:outputText value="Create Account &gt; Become a Contributor" rendered="#{LoginWorkflowBean.contributorWorkflow}"/>
              </ui:panelLayout>            
              <ui:panelLayout styleClass="#{ (LoginWorkflowBean.creatorWorkflow or LoginWorkflowBean.contributorWorkflow) ? 'requestContent' : 'dvn_sectionBox'}">
                  
                  <ui:panelLayout rendered="#{LoginWorkflowBean.creatorWorkflow or LoginWorkflowBean.contributorWorkflow}" styleClass="requestContentDescLeft">
                  		<p>Please read and agree to the terms of use for this dataverse network. Check the "I agree and accept" box and continue.</p>
                  </ui:panelLayout>
                  
                  <ui:panelLayout styleClass="#{ (LoginWorkflowBean.creatorWorkflow or LoginWorkflowBean.contributorWorkflow) ? 'requestContentFormRight' : 'empty'}">
                  <ui:panelLayout styleClass="dvn_margin12">
                        <h:outputText value="Terms of Use" styleClass="reqContentActionTitle" />
                        <br/>
                        <div id="requestDataverseTerms">
                            <h:outputText value="#{AccountTermsOfUsePage.termsOfUse}" escape="false"/>
                        </div>
                        <br/>
                        <ui:panelGroup  style="padding-top: 20px;" block="true">
                            <h:selectBooleanCheckbox id="termsAccepted" required="true" value="#{AccountTermsOfUsePage.termsAccepted}" />
                            <h:outputText styleClass="agreeCheckbox" value="I agree and accept the IQSS DVN terms of use." />
                        </ui:panelGroup>
                        <ui:panelGroup block="true" style="padding-top: 20px; text-align: center">
                            <h:commandButton  id="termsButton" value="Continue" action="#{AccountTermsOfUsePage.acceptTerms_action}"/>
                        </ui:panelGroup>
                  </ui:panelLayout>
                  </ui:panelLayout>
                  
              </ui:panelLayout>
          </ui:panelLayout> 

        </ui:form>
    </f:subview>
</jsp:root>
