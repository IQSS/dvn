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

<gui:param name="pageTitle" value="DVN - Add/Edit Account" />

  <gui:define name="body">
      <f:loadBundle basename="Bundle" var="bundle"/>
     <f:loadBundle basename="BundleAnalysis" var="bundleAnalysis"/>
                            
     

        <ui:form  id="form1">
           <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
            <h:inputHidden id="studyId" value="#{AddAccountPage.studyId}"/>
            <h:inputHidden binding="#{AddAccountPage.hiddenWorkflow}" value="#{AddAccountPage.workflow}"/>

          <input type="hidden" name="pageName" value="AddAccountPage"/>
                                           
          <ui:panelLayout styleClass="#{ (LoginWorkflowBean.creatorWorkflow or LoginWorkflowBean.contributorWorkflow) ? 'dvn_createDvRequest dvn_section dvn_overflow' : 'dvn_section'}">
              <ui:panelLayout styleClass="#{ (LoginWorkflowBean.creatorWorkflow or LoginWorkflowBean.contributorWorkflow) ? 'requestHeader dvn_overflow' : 'dvn_sectionTitle'}">
                      <h:outputText value="Create a New Account" rendered="#{LoginWorkflowBean.plainWorkflow}"/>
                      <h:outputText value="Create a New Account" rendered="#{LoginWorkflowBean.fileAccessWorkflow}"/>
                      <h:outputText value="Create Account &lt;span&gt;&gt; Create Your Own Dataverse&lt;/span&gt;" rendered="#{LoginWorkflowBean.creatorWorkflow}" escape="false"/>
                      <h:outputText value="Create Account &lt;span&gt;&gt; Become a Contributor&lt;/span&gt;" rendered="#{LoginWorkflowBean.contributorWorkflow}" escape="false"/>
              </ui:panelLayout>
              <ui:panelLayout rendered="#{LoginWorkflowBean.creatorWorkflow}" styleClass="requestTimeline" style="background-position: 0 0;">
                        <div class="requestTimelinePoint" style="left: 51px;"><img src="/dvn/resources/lrg-blue-bullet.gif" class="vdcNoBorder" alt=""/><br/><strong style="color:#035899; font-weight:bold;">Create Account</strong></div>
                        <div class="requestTimelinePoint" style="left: 249px;"><img src="/dvn/resources/lrg-grey-bullet.gif" class="vdcNoBorder" alt=""/><br/><strong>Terms of Use</strong></div>
                        <div class="requestTimelinePoint" style="left: 435px;"><img src="/dvn/resources/lrg-grey-bullet.gif" class="vdcNoBorder" alt=""/><br/><strong>Name Dataverse</strong></div>
                        <div class="requestTimelinePoint" style="left: 651px;"><img src="/dvn/resources/lrg-grey-bullet.gif" class="vdcNoBorder" alt=""/><br/><strong>Success!</strong></div>
              </ui:panelLayout>
              <ui:panelLayout rendered="#{LoginWorkflowBean.contributorWorkflow}" styleClass="requestTimeline" style="background-position: 98px 0;">
                        <div class="requestTimelinePoint" style="left: 144px;"><img src="/dvn/resources/lrg-blue-bullet.gif" class="vdcNoBorder" alt=""/><br/><strong style="color:#035899; font-weight:bold;">Create Account</strong></div>
                        <div class="requestTimelinePoint" style="left: 347px;"><img src="/dvn/resources/lrg-grey-bullet.gif" class="vdcNoBorder" alt=""/><br/><strong>Terms of Use</strong></div>
                        <div class="requestTimelinePoint" style="left: 554px;"><img src="/dvn/resources/lrg-grey-bullet.gif" class="vdcNoBorder" alt=""/><br/><strong>Success!</strong></div>
              </ui:panelLayout>
              <ui:panelLayout styleClass="#{ (LoginWorkflowBean.creatorWorkflow or LoginWorkflowBean.contributorWorkflow) ? 'requestContent' : 'dvn_sectionBox'}">
                  
                  <ui:panelLayout rendered="#{LoginWorkflowBean.creatorWorkflow or LoginWorkflowBean.contributorWorkflow}" styleClass="requestContentDescLeft">
                      <p>Please create an account to join the
                          <h:outputText value="#{VDCRequest.vdcNetwork.name}" />
                      Dataverse Network.</p>
                  </ui:panelLayout>
                  
                  <ui:panelLayout styleClass="#{ (LoginWorkflowBean.creatorWorkflow or LoginWorkflowBean.contributorWorkflow) ? 'requestContentDescRight' : 'empty'}">
                  <ui:panelLayout styleClass="dvn_margin12"> 
                      
                      <ui:panelGroup block="true" style="padding-bottom: 15px;">
                          <h:graphicImage value="/resources/icon_required.gif"/> <h:outputText style="vdcHelpText" value="Indicates a required field."/>
                      </ui:panelGroup>
                      <h:panelGrid cellpadding="0" cellspacing="0"
                                    columnClasses="vdcColPadded, vdcColPadded" columns="2" id="gridPanel2">
                          <ui:panelGroup>
                              <h:outputText value="Username"/>
                              <h:graphicImage id="image1" value="/resources/icon_required.gif"/>
                          </ui:panelGroup>
                          <ui:panelGroup>
                              <h:inputText id="inputUserName" validator="#{AddAccountPage.validateUserName}" value="#{AddAccountPage.user.userName}" required="true" requiredMessage="This field is required.">
                              </h:inputText>
                              <h:message for="inputUserName" styleClass="errorMessage"/>
                          </ui:panelGroup>
                          <ui:panelGroup>
                              <h:outputText value="Password"/>
                              <h:graphicImage value="/resources/icon_required.gif"/>
                          </ui:panelGroup>
                          <ui:panelGroup> 
                              <h:inputSecret binding="#{AddAccountPage.inputPassword}" id="inputPassword" value="#{AddAccountPage.editUserService.newPassword1}" required="true" requiredMessage="This field is required."/>
                              <h:message for="inputPassword"  styleClass="errorMessage"/>
                          </ui:panelGroup>
                          <ui:panelGroup>
                              <h:outputText value="Retype Password"/>
                              <h:graphicImage value="/resources/icon_required.gif"/>
                          </ui:panelGroup>
                          <ui:panelGroup> 
                              <h:inputSecret id="retypePassword" value="#{AddAccountPage.editUserService.newPassword2}" validator="#{AddAccountPage.validatePassword}" required="true" requiredMessage="This field is required."/>
                              <h:message for="retypePassword"  styleClass="errorMessage"/>
                          </ui:panelGroup>                          
                          <ui:panelGroup>
                              <h:outputText value="First Name"/>
                              <h:graphicImage value="/resources/icon_required.gif"/>
                          </ui:panelGroup>
                          <ui:panelGroup>
                              <h:inputText id="inputFirstName" value="#{AddAccountPage.user.firstName}" required="true" requiredMessage="This field is required."/> 
                              <h:message for="inputFirstName"  styleClass="errorMessage"/>
                          </ui:panelGroup>
                          <ui:panelGroup>
                              <h:outputText value="Last Name"/>
                              <h:graphicImage value="/resources/icon_required.gif"/>
                          </ui:panelGroup>
                          <ui:panelGroup>
                              <h:inputText id="inputLastName" value="#{AddAccountPage.user.lastName}" required="true" requiredMessage="This field is required."/> 
                              <h:message for="inputLastName" styleClass="errorMessage"/>
                          </ui:panelGroup>
                          <ui:panelGroup>
                              <h:outputText value="E-Mail"/>
                              <h:graphicImage value="/resources/icon_required.gif"/>
                          </ui:panelGroup>
                          <ui:panelGroup>
                              <h:inputText id="inputEmail" value="#{AddAccountPage.user.email}" size="40" required="true" requiredMessage="This field is required.">
                                  <f:validator validatorId="EmailValidator"/>
                              </h:inputText>
                              <h:message for="inputEmail" styleClass="errorMessage"/>
                          </ui:panelGroup>
                          <ui:panelGroup>    
                              <h:outputText value="Institution"/>
                          </ui:panelGroup>
                          <h:inputText id="inputInstitution" value="#{AddAccountPage.user.institution}" size="40"/>
                          <ui:panelGroup>
                              <h:outputText value="Position"/>
                          </ui:panelGroup>
                          <h:selectOneMenu value="#{AddAccountPage.user.position}" >
                              <f:selectItem itemLabel="Student" itemValue="Student" />
                              <f:selectItem itemLabel="Faculty" itemValue="Faculty" />
                              <f:selectItem itemLabel="Staff" itemValue="Staff" />
                              <f:selectItem itemLabel="Other" itemValue="Other" />
                          </h:selectOneMenu>
                          <ui:panelGroup>
                              <h:outputText  value="Phone Number"/>
                          </ui:panelGroup>
                          <h:inputText  id="inputPhoneNumber" value="#{AddAccountPage.user.phoneNumber}"/>
                      </h:panelGrid>
                      <!--
                    <ui:panelGroup  block="true"  style="padding-left: 100px; padding-top: 20px" rendered="#{VDCRequest.currentVDCId!=null and VDCRequest.currentVDC.allowContributorRequests and AddAccountPage.studyId==null and VDCSession.loginBean.user==null}"> 
                        <h:selectBooleanCheckbox id="contributorCheckbox" value="#{AddAccountPage.contributorRequest}"/>
                        <h:outputLabel for="contributorCheckbox" value="I want to become a contributor to #{VDCRequest.currentVDC.name} dataverse. This will allow me to upload my own data in this archive."/>
                    </ui:panelGroup>
                    <ui:panelGroup  block="true" style="padding-left: 100px; padding-top: 20px" rendered="#{VDCRequest.vdcNetwork.allowCreateRequest and VDCRequest.currentVDCId==null and VDCSession.loginBean.user==null}">
                        <h:selectBooleanCheckbox id="creatorCheckbox" value="#{AddAccountPage.creatorRequest}" />
                        <h:outputLabel for="creatorCheckbox" value="I want to become a dataverse creator. This will allow me to create my virtual archive in the #{VDCRequest.vdcNetwork.name} Dataverse Network."/>
                    </ui:panelGroup>
                    <ui:panelGroup  block="true"  style="padding-left: 100px; padding-top: 20px" rendered="#{AddAccountPage.studyId!=null and VDCSession.loginBean.user==null}">
                        <h:selectBooleanCheckbox id="studyCheckbox" value="#{AddAccountPage.accessStudy}"  />
                        <h:outputLabel for="studyCheckbox" value="I want to access the restricted files of Study: #{AddAccountPage.study.globalId}"/>
                    </ui:panelGroup>
                    -->
                      <ui:panelGroup block="true"  style="padding-left: 100px; padding-top: 20px">
                          <h:commandButton  value="Create Account" action="#{AddAccountPage.createAccount}"/>
                          <h:commandButton  immediate="true" value="Cancel" action="#{AddAccountPage.cancel}"/>
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
