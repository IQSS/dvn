<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
    <f:subview id="CreatorRequestSuccessPageView">
        <ui:form  id="creatorRequestForm">
          <input type="hidden" name="pageName" value="CreatorRequestPage"/>
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>

            <ui:panelLayout styleClass="dvn_createDvRequest dvn_section dvn_overflow">
              <ui:panelLayout styleClass="requestHeader dvn_overflow">
                      <h:outputText value="Create Account &lt;span&gt;&gt; Create Your Own Dataverse&lt;/span&gt;" escape="false"/>
              </ui:panelLayout>
              <ui:panelLayout rendered="#{LoginWorkflowBean.creatorWorkflow}" styleClass="requestTimeline" style="background-position: 0 12px;">
                        <div class="requestTimelinePoint" style="left: 53px;"><img src="/dvn/resources/lrg-blue-bullet.gif" class="vdcNoBorder" alt=""/><br/><strong style="color:#035899;">Create Account</strong></div>
                        <div class="requestTimelinePoint" style="left: 271px;"><img src="/dvn/resources/lrg-blue-bullet.gif" class="vdcNoBorder" alt=""/><br/><strong style="color:#035899;">Terms of Use</strong></div>
                        <div class="requestTimelinePoint" style="left: 474px;"><img src="/dvn/resources/lrg-blue-bullet.gif" class="vdcNoBorder" alt=""/><br/><strong style="color:#035899;">Name Dataverse</strong></div>
                        <div class="requestTimelinePoint" style="left: 709px;"><img src="/dvn/resources/lrg-blue-bullet.gif" class="vdcNoBorder" alt=""/><br/><strong style="color:#035899; font-weight:bold;">Success!</strong></div>
              </ui:panelLayout>
              <ui:panelLayout styleClass="requestContent">
                  
                  <ui:panelLayout styleClass="requestContentDescLeft requestContentSucessH4">
                      <h4>Your new dataverse has been created!</h4>
                  </ui:panelLayout>
                  
                  <ui:panelLayout styleClass="requestContentDescRight requestContentSucess">
                  <ui:panelLayout styleClass="dvn_margin12"> 
                      
                        <p>Your new dataverse is restricted, it will be set to 'Coming Soon' (Not Released) by default. Go to <a href="/dvn/faces/admin/OptionsPage.jsp">My Options</a> to make it public.
                        <ul>
                            <li>Begin <a href="/dvn/faces/study/EditStudyPage.jsp">adding data studies</a> or <a href="/dvn/faces/collection/ManageCollectionsPage.jsp">creating collections</a> of data from other dataverses.</li>
                            <li><a href="/dvn/faces/admin/EditBannerFooterPage.jsp">Customize the layout</a>, and then you are ready to release your dataverse live!</li>
                        </ul>
                        You can access your dataverse directly by entering this URL:</p>
                        <p><a href="#">http://dvn.iq.harvard.edu/dvn/dv/dummyURL</a></p>
                      
                  </ui:panelLayout>
                  </ui:panelLayout>
                  
              </ui:panelLayout>
          </ui:panelLayout>

        </ui:form>
    </f:subview>
</jsp:root>
