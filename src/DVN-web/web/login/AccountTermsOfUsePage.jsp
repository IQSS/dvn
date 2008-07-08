<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:gui="http://java.sun.com/jsf/facelets"
      xmlns:h="http://java.sun.com/jsf/html"
      xmlns:f="http://java.sun.com/jsf/core" 
      xmlns:jsp="http://java.sun.com/JSP/Page"
      xmlns:ui="http://www.sun.com/web/ui"
      >
<head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>

</head>

<body>
<gui:composition template="/template.xhtml">

<gui:param name="pageTitle" value="DVN - Account Terms of Use" />

  <gui:define name="body">
      <f:loadBundle basename="Bundle" var="bundle"/>
     <f:loadBundle basename="BundleAnalysis" var="bundleAnalysis"/>
                            

        
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
            </gui:define>
        </gui:composition>
    </body>
</html>
