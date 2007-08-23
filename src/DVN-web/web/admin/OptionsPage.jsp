<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
   <f:subview id="OptionsPageView">
      <ui:form id="form1">
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
            
            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                   
                        <h:outputText  value="My Options in #{VDCRequest.currentVDC.name} dataverse"/>  
                    
                </div>            
                <div class="dvn_sectionBox">             
                    <div class="dvn_margin12"> 
                        <ui:panelGroup block="true" rendered="#{VDCSession.loginBean.contributor and not VDCRequest.currentVDC.harvestingDataverse}">
                            <ui:panelGroup  block="true" id="groupPanel1" style="padding-top: 20px; padding-bottom: 15px">
                                <h:outputText  id="outputText2" styleClass="vdcSubHeader" value="Contributor Options"/>
                            </ui:panelGroup>
                            <ui:panelGroup  block="true" id="groupPanel2" separator="&lt;br /&gt;&lt;br /&gt;" style="padding-left: 10px">
                                <h:outputLink id="hyperlink1" value="/dvn#{VDCRequest.currentVDCURL}/faces/study/EditStudyPage.jsp">
                                    <h:outputText  id="hyperlink1Text" value="Add New Study"/>
                                </h:outputLink>
                                <h:outputLink id="hyperlink2" value="/dvn#{VDCRequest.currentVDCURL}/faces/study/MyStudiesPage.jsp">
                                    <h:outputText  id="hyperlink2Text" value="View My Studies (see status and edit studies that you have uploaded)"/>
                                </h:outputLink>
                            </ui:panelGroup>
                        </ui:panelGroup>
                        <ui:panelGroup block="true" rendered="#{VDCSession.loginBean.curator or VDCSession.loginBean.admin or VDCSession.loginBean.networkAdmin}">
                            <ui:panelGroup  block="true" id="groupPanel3" style="padding-top: 20px; padding-bottom: 15px">
                                <h:outputText  id="outputText3" styleClass="vdcSubHeader" value="Curator Options"/>
                            </ui:panelGroup>
                            <ui:panelGroup block="true"  rendered="#{not VDCRequest.currentVDC.harvestingDataverse}" separator="&lt;br /&gt;&lt;br /&gt;" style="padding-left: 10px">
                                <h:outputLink id="hyperlink5"  value="/dvn#{VDCRequest.currentVDCURL}/faces/study/EditStudyPage.jsp">
                                    <h:outputText  id="hyperlink1Text1" value="Add New Study"/>
                                </h:outputLink>
                                <h:outputLink   value="/dvn#{VDCRequest.currentVDCURL}/faces/study/MyStudiesPage.jsp">
                                    <h:outputText   value="View My Studies (see status of studies that you have uploaded)"/>
                                </h:outputLink>
                                <h:outputLink  id="hyperlink6" value="/dvn#{VDCRequest.currentVDCURL}/faces/study/ReviewStudiesPage.jsp">
                                    <h:outputText id="hyperlink2Text1" value="Review Studies (see and review all New Studies and Studies In Review)"/>
                                </h:outputLink>
                                <h:outputText value=""/> <!-- a hack to make the spacing work (sorry Merce) -->
                            </ui:panelGroup>
                            <ui:panelGroup block="true"  separator="&lt;br /&gt;&lt;br /&gt;" style="padding-left: 10px">
                                <h:outputLink id="hyperlink7b" value="/dvn#{VDCRequest.currentVDCURL}/faces/collection/AddCollectionStudiesPage.jsp">
                                    <h:outputText  id="hyperlink3Text2" value="Add Collection by Assigning Studies"/>
                                </h:outputLink>
                                <h:outputLink id="hyperlink7a" value="/dvn#{VDCRequest.currentVDCURL}/faces/collection/AddCollectionQueryPage.jsp">
                                    <h:outputText  id="hyperlink3Text1" value="Add Collection as a Query"/>
                                </h:outputLink> 
                                <h:outputLink id="hyperlink7c" value="/dvn#{VDCRequest.currentVDCURL}/faces/collection/AddLinkPage.jsp">
                                    <h:outputText  id="hyperlink3Text3" value="Add Collection Link"/>
                                </h:outputLink>
                                <h:outputLink  id="hyperlink8" value="/dvn#{VDCRequest.currentVDCURL}/faces/collection/ManageCollectionsPage.jsp">
                                    <h:outputText  id="hyperlink4Text1" value="Manage Collections / Links"/>
                                </h:outputLink >
                                <ui:panelGroup block="true" >
                                    <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                                    <h:outputText id="outputText4" styleClass="vdcHelpText" value="To Edit Studies (Edit Cataloging Information, Add or Edit Files, Change Study Permissions or Delete Study) search for the study and follow edit options displayed  in the study page. Studies that are not owned by  #{VDCRequest.currentVDC.name} Dataverse cannot be edited here."/>
                                </ui:panelGroup>
                            </ui:panelGroup>
                        </ui:panelGroup>
                        <ui:panelGroup block="true" rendered="#{VDCSession.loginBean.admin == true or VDCSession.loginBean.networkAdmin == true}">
                            <ui:panelGroup block="true" id="groupPanel5" style="padding-top: 20px; padding-bottom: 15px">
                                <h:outputText id="outputText5" styleClass="vdcSubHeader" value="Admin Options"/>
                            </ui:panelGroup>
                            <ui:panelGroup block="true" id="groupPanel6" separator="&lt;br /&gt;&lt;br /&gt;" style="padding-left: 10px" >
                                <h:outputText id="outputText7" styleClass="vdcSubHeaderColor" value="Customization:"/>
                                <ui:panelGroup block="true" id="groupPanel7"
                                               separator="&lt;br /&gt;&lt;br /&gt;" style="padding-left: 10px">
                                    <h:outputLink id="hyperlink15" value="/dvn#{VDCRequest.currentVDCURL}/faces/site/EditSitePage.jsp">
                                        <h:outputText id="hyperlink15Text" value="Edit Name and Alias"/>
                                    </h:outputLink>
                                    <h:outputLink id="hyperlink3" value="/dvn#{VDCRequest.currentVDCURL}/faces/admin/EditBannerFooterPage.jsp">
                                        <h:outputText id="hyperlink1Text2" value="Edit Banner and Footer"/>
                                    </h:outputLink>
                                    <h:outputLink id="hyperlink4" value="/dvn#{VDCRequest.currentVDCURL}/faces/admin/EditHomePanelsPage.jsp">
                                        <h:outputText id="hyperlink2Text2" value="Edit Homepage Panels (Announcements, Most Recent)"/>
                                    </h:outputLink>
                                    <h:outputLink id="hyperlink10" value="/dvn#{VDCRequest.currentVDCURL}/faces/admin/EditAboutPage.jsp">
                                        <h:outputText id="hyperlink4Text2" value="Edit About Page"/>
                                    </h:outputLink>
                                    <h:outputLink id="hyperlink17" value="/dvn#{VDCRequest.currentVDCURL}/faces/admin/EditContactUsPage.jsp">
                                        <h:outputText id="hyperlink17Text" value="Set Contact Us E-Mail"/>
                                    </h:outputLink>
                                    <h:outputLink id="hyperlink16" value="/dvn#{VDCRequest.currentVDCURL}/faces/admin/SearchFieldsPage.jsp">
                                        <h:outputText id="hyperlink16Text" value="Set Additional Fields to Display in Search Results"/>
                                    </h:outputLink>
                                </ui:panelGroup>
                                <h:outputText id="outputText6" styleClass="vdcSubHeaderColor" value="Users/Permissions/Conditions:"/>
                                <ui:panelGroup block="true" id="groupPanel8"
                                               separator="&lt;br /&gt;&lt;br /&gt;" style="padding-left: 10px">
                                    <h:outputLink id="hyperlink11" value="/dvn#{VDCRequest.currentVDCURL}/faces/admin/PrivilegedUsersPage.jsp">
                                        <h:outputText id="hyperlink1Text3" value="Manage Dataverse Restrictions and Users' Privileges (restricted/public dataverse, contributors, curators, admins)"/>
                                    </h:outputLink>
                                    <h:outputLink   value="/dvn#{VDCRequest.currentVDCURL}/faces/login/AddAccountPage.jsp">
                                        <h:outputText styleClass="vdcMenuItem" value="Add New User Account"/>
                                    </h:outputLink>
                                    <h:outputLink id="hyperlink14" value="/dvn#{VDCRequest.currentVDCURL}/faces/admin/EditUseTermsPage.jsp">
                                        <h:outputText id="hyperlink4Text3" value="Set Terms of Use"/>
                                    </h:outputLink>
                                </ui:panelGroup>
                            </ui:panelGroup>
                        </ui:panelGroup>
                    </div>
                </div>
            </div>
        </ui:form>                
     </f:subview>
</jsp:root>
