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
                      <h:outputText value="Success! &lt;span&gt;&gt; Create Your Own Dataverse&lt;/span&gt;" escape="false"/>
              </ui:panelLayout>
              <ui:panelLayout rendered="#{LoginWorkflowBean.creatorWorkflow}" styleClass="requestTimeline" style="background-position: 0 -12px;">
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
                      
                        <p>Your new dataverse is set to <em>Not Released</em> by default, and appears on the Coming Soon tab of the Network homepage.
                            </p>
                           <ul>
                            <li> Click                              
                            <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/admin/OptionsPage.jsp">
                                <h:outputText value="My Options"/>
                            </h:outputLink> to administrate your dataverse.
                            </li>
                            <li> Begin    
                             <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/study/EditStudyPage.jsp">
                                <h:outputText value="adding studies and uploading files"/>
                             </h:outputLink> or 
                             <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/collection/ManageCollectionsPage.jsp">
                                 <h:outputText value="creating collections"/> 
                             </h:outputLink>of data from other dataverses.
                            </li>
                            <li>
                             <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/admin/EditBannerFooterPage.jsp">
                                   <h:outputText value="Customize the layout"/>
                            </h:outputLink>, and then you are ready to 
                            <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/admin/PrivilegedUsersPage.jspp">
                                   <h:outputText value="release"/>
                            </h:outputLink>
                            your dataverse live!
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
    </f:subview>
</jsp:root>
