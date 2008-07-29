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

<gui:param name="pageTitle" value="DVN - Dataverse Creation Success" />

  <gui:define name="body">

        <ui:form  id="creatorRequestForm">
          <input type="hidden" name="pageName" value="CreatorRequestPage"/>
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>

            <ui:panelLayout styleClass="dvn_createDvRequest dvn_section dvn_overflow">
              <ui:panelLayout styleClass="requestHeader dvn_overflow">
                      <h:outputText value="Success! &lt;span&gt;&gt; Create Your Own Dataverse&lt;/span&gt;" escape="false"/>
              </ui:panelLayout>
              <ui:panelLayout rendered="#{LoginWorkflowBean.creatorWorkflow}" styleClass="requestTimeline" style="background-position: 0 -12px;">
                        <div class="requestTimelinePoint" style="left: 51px;"><img src="/dvn/resources/lrg-blue-bullet.gif" class="vdcNoBorder" alt=""/><br/><strong style="color:#035899;">Create Account</strong></div>
                        <div class="requestTimelinePoint" style="left: 249px;"><img src="/dvn/resources/lrg-blue-bullet.gif" class="vdcNoBorder" alt=""/><br/><strong style="color:#035899;">Terms of Use</strong></div>
                        <div class="requestTimelinePoint" style="left: 435px;"><img src="/dvn/resources/lrg-blue-bullet.gif" class="vdcNoBorder" alt=""/><br/><strong style="color:#035899;">Name Dataverse</strong></div>
                        <div class="requestTimelinePoint" style="left: 651px;"><img src="/dvn/resources/lrg-blue-bullet.gif" class="vdcNoBorder" alt=""/><br/><strong style="color:#035899; font-weight:bold;">Success!</strong></div>
              </ui:panelLayout>
              <ui:panelLayout styleClass="requestContent">
                  
                  <ui:panelLayout styleClass="requestContentDescLeft requestContentSucessH4">
                      <h4>Your new dataverse has been created!</h4>
                  </ui:panelLayout>
                  
                  <ui:panelLayout styleClass="requestContentDescRight requestContentSucess">
                  <ui:panelLayout styleClass="dvn_margin12"> 
                      
                        <p>Your new dataverse is set to <em>Not Released</em> by default, and appears on the Coming Soon tab of the Network homepage.
                            </p>
                           <ul>
                            <li> <h:outputText value=" Click "/>                                
                             <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/admin/OptionsPage.jsp">
                                <h:outputText value="My Options"/>
                            </h:outputLink> <h:outputText value=" to administrate your dataverse "/>
                            </li>
                            <li> <h:outputText value=" Begin "/>      
                             <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/study/EditStudyPage.jsp">
                                <h:outputText value="adding studies and uploading files"/>
                             </h:outputLink> <h:outputText value=" or "/>
                             <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/collection/ManageCollectionsPage.jsp">
                                 <h:outputText value="creating collections"/> 
                             </h:outputLink> <h:outputText value=" of data from other dataverses."/>
                            </li>
                            <li>
                             <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/admin/EditBannerFooterPage.jsp">
                                   <h:outputText value="Customize the layout"/>
                            </h:outputLink><h:outputText value=", and then you are ready to "/> 
                            <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/admin/PrivilegedUsersPage.jspp">
                                   <h:outputText value="release"/>
                            </h:outputLink>
                            <h:outputText value=" your dataverse live!"/>
                           </li>
                        </ul>
                        <p>
                        You can access your dataverse directly by entering this URL:</p>
                        <p>
                            <h:outputLink  value="http://#{LoginWorkflowBean.hostUrl}/dvn#{VDCRequest.currentVDCURL}">
                                <h:outputText value="http://#{LoginWorkflowBean.hostUrl}/dvn#{VDCRequest.currentVDCURL}"/>
                            </h:outputLink> 
                        </p>
                      
                  </ui:panelLayout>
                  </ui:panelLayout>
                  
              </ui:panelLayout>
          </ui:panelLayout>

        </ui:form>
            </gui:define>
        </gui:composition>
    </body>
</html>
