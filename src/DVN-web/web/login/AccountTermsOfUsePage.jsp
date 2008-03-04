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
                      <h:outputText value="Terms of Use" rendered="#{LoginWorkflowBean.plainWorkflow}"/>
                      <h:outputText value="Create a New Account" rendered="#{LoginWorkflowBean.fileAccessWorkflow}"/>
                      <h:outputText value="Create Account &lt;span&gt;&gt; Create Your Own Dataverse&lt;/span&gt;" rendered="#{LoginWorkflowBean.creatorWorkflow}" escape="false"/>
                      <h:outputText value="Create Account &lt;span&gt;&gt; Become a Contributor&lt;/span&gt;" rendered="#{LoginWorkflowBean.contributorWorkflow}" escape="false"/>
              </ui:panelLayout>
              <ui:panelLayout rendered="#{LoginWorkflowBean.creatorWorkflow}" styleClass="requestTimeline" style="background-position: 0 -4px;">
                        <div class="requestTimelinePoint" style="left: 51px;"><img src="/dvn/resources/lrg-blue-bullet.gif" class="vdcNoBorder" alt=""/><br/><strong style="color:#035899;">Create Account</strong></div>
                        <div class="requestTimelinePoint" style="left: 249px;"><img src="/dvn/resources/lrg-blue-bullet.gif" class="vdcNoBorder" alt=""/><br/><strong style="color:#035899; font-weight:bold;">Terms of Use</strong></div>
                        <div class="requestTimelinePoint" style="left: 435px;"><img src="/dvn/resources/lrg-grey-bullet.gif" class="vdcNoBorder" alt=""/><br/><strong>Name Dataverse</strong></div>
                        <div class="requestTimelinePoint" style="left: 651px;"><img src="/dvn/resources/lrg-grey-bullet.gif" class="vdcNoBorder" alt=""/><br/><strong>Success!</strong></div>
              </ui:panelLayout>  
              <ui:panelLayout rendered="#{LoginWorkflowBean.contributorWorkflow}" styleClass="requestTimeline" style="background-position: 108px -4px;">
                        <div class="requestTimelinePoint" style="left: 144px;"><img src="/dvn/resources/lrg-blue-bullet.gif" class="vdcNoBorder" alt=""/><br/><strong style="color:#035899;">Create Account</strong></div>
                        <div class="requestTimelinePoint" style="left: 347px;"><img src="/dvn/resources/lrg-blue-bullet.gif" class="vdcNoBorder" alt=""/><br/><strong style="color:#035899; font-weight:bold;">Terms of Use</strong></div>
                        <div class="requestTimelinePoint" style="left: 554px;"><img src="/dvn/resources/lrg-grey-bullet.gif" class="vdcNoBorder" alt=""/><br/><strong>Success!</strong></div>
              </ui:panelLayout>       
              <ui:panelLayout styleClass="#{ (LoginWorkflowBean.creatorWorkflow or LoginWorkflowBean.contributorWorkflow) ? 'requestContent' : 'dvn_sectionBox'}">
                  
                  <ui:panelLayout rendered="#{LoginWorkflowBean.creatorWorkflow or LoginWorkflowBean.contributorWorkflow}" styleClass="requestContentDescLeft">
                  	<p>Please read and agree to the terms of use for the 
                        <h:outputText value="#{VDCRequest.vdcNetwork.name}" />
                    Dataverse Network. Check the "I agree and accept" box and continue.</p>
                  </ui:panelLayout>
                  
                  <ui:panelLayout styleClass="#{ (LoginWorkflowBean.creatorWorkflow or LoginWorkflowBean.contributorWorkflow) ? 'requestContentFormRight' : 'empty'}">
                  <ui:panelLayout styleClass="dvn_margin12">
                        <ui:panelGroup styleClass="termsAgreementMessage" rendered="#{LoginWorkflowBean.plainWorkflow or LoginWorkflowBean.fileAccessWorkflow}">
                            <h:outputText value="Please read and agree to the terms of use for the #{VDCRequest.vdcNetwork.name} Dataverse Network. Check the &quot;I agree and accept&quot; box and continue." />
                        </ui:panelGroup>
                        <div class="termsAgreementBox">
                            <h:outputText value="#{AccountTermsOfUsePage.termsOfUse}" escape="false"/>
                        </div>
                        <ui:panelGroup block="true">
                            <h:selectBooleanCheckbox id="termsAccepted" required="true" validator="#{AccountTermsOfUsePage.validateTermsAccepted}"  value="#{AccountTermsOfUsePage.termsAccepted}" />
                            <h:outputText styleClass="agreeCheckbox" value="I agree and accept these terms of use." />
                            <h:message for="termsAccepted" styleClass="errorMessage"/>
                        </ui:panelGroup>
                        <ui:panelGroup block="true" styleClass="termsAgreementButtons">
                            <h:commandButton id="termsButton" value="Continue" action="#{AccountTermsOfUsePage.acceptTerms_action}"/>
                            <h:commandButton id="termsCancelButton" value="Cancel" immediate="true" style="margin-left: 20px;" action="home"/>
                        </ui:panelGroup>
                  </ui:panelLayout>
                  </ui:panelLayout>
                  
              </ui:panelLayout>
          </ui:panelLayout> 

        </ui:form>
    </f:subview>
</jsp:root>
