<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
    <f:subview id="ContributorRequestSuccessPageView">
        <ui:form  id="contributorRequestForm">
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>

            <ui:panelLayout styleClass="dvn_createDvRequest dvn_section dvn_overflow">
              <ui:panelLayout styleClass="requestHeader dvn_overflow">
                      <h:outputText value="Sucess! &lt;span&gt;&gt; Become a Contributor&lt;/span&gt;" escape="false"/>
              </ui:panelLayout>
              <ui:panelLayout rendered="#{LoginWorkflowBean.creatorWorkflow}" styleClass="requestTimeline" style="background-position: 0 12px;">
                        <div class="requestTimelinePoint" style="left: 53px;"><img src="/dvn/resources/lrg-blue-bullet.gif" class="vdcNoBorder" alt=""/><br/><strong style="color:#035899;">Create Account</strong></div>
                        <div class="requestTimelinePoint" style="left: 372px;"><img src="/dvn/resources/lrg-blue-bullet.gif" class="vdcNoBorder" alt=""/><br/><strong style="color:#035899;">Terms of Use</strong></div>
                        <div class="requestTimelinePoint" style="left: 709px;"><img src="/dvn/resources/lrg-blue-bullet.gif" class="vdcNoBorder" alt=""/><br/><strong style="color:#035899; font-weight:bold;">Success!</strong></div>
              </ui:panelLayout>
              <ui:panelLayout styleClass="requestContent">
                  
                  <ui:panelLayout styleClass="requestContentDescLeft requestContentSucessH4">
                      <h4>Your are now a contributor to this dataverse!</h4>
                  </ui:panelLayout>
                  
                  <ui:panelLayout styleClass="requestContentDescRight requestContentSucess">
                  <ui:panelLayout styleClass="dvn_margin12"> 
                      
                        <p>You can add your new study to this dataverse by going to <a href="/dvn/faces/admin/OptionsPage.jsp">My Options</a>.  Then set the study to 'Released', to have it reviewed by the dataverse administrator.</p>
                      
                  </ui:panelLayout>
                  </ui:panelLayout>
                  
              </ui:panelLayout>
          </ui:panelLayout>
          
        </ui:form>
    </f:subview>
</jsp:root>
