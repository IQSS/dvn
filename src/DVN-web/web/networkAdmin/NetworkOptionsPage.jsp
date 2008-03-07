<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
   <f:subview id="NetworkOptionsPageView">
        <ui:form  id="form1">
            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                    
                        <h:outputText  value="#{VDCRequest.vdcNetwork.name} Dataverse Network Admin Options"/>
                     
                </div>            
                <div class="dvn_sectionBox">
                    <div class="dvn_margin12">
                         <ui:panelGroup  block="true" style="padding-top: 20px; padding-bottom: 15px">
                            <h:outputText   styleClass="vdcSubHeader" value="Dataverses, Harvesting, Exporting and OAI Sets:"/>
                        </ui:panelGroup>
                        <ui:panelGroup  block="true" id="groupPanel7"
                                        separator="&lt;br /&gt;" style="padding-left: 10px">
                            <h:outputLink  id="hyperlink4" value="/dvn/faces/site/AddSitePage.jsp">
                                <h:outputText  id="hyperlink4Text" value="Create a New Dataverse"/>
                            </h:outputLink>                  
                            <h:outputLink  value="/dvn/faces/site/EditHarvestSitePage.jsp">
                                <h:outputText   value="Create a New Harvesting Dataverse"/>
                            </h:outputLink>                       
                            <h:outputLink   value="/dvn/faces/site/HarvestSitesPage.jsp">
                                <h:outputText  value="Manage Dataverses"/>
                            </h:outputLink>
                            <h:outputLink   value="/dvn/faces/networkAdmin/DVGroupDetailPage.jsp">
                                <h:outputText  value="Manage Dataverse Groups"/>
                            </h:outputLink>
                            <h:outputLink  value="/dvn/faces/networkAdmin/EditExportSchedulePage.jsp">
                                <h:outputText  value="Study Export Schedule"/>
                            </h:outputLink>                           
                              <h:outputLink  value="/dvn/faces/networkAdmin/OAISetsPage.jsp">
                                <h:outputText  value="OAI Harvesting Sets"/>
                            </h:outputLink>
                            <h:outputLink  rendered="#{ MainLayoutBean.googleAnalyticsKey != null }" 
                                                id="siteStatslink" 
                                                value="http://www.google.com/analytics/"
                                                target="_blank"
                                                >
                                                <h:outputText  value="Google Analytics"/>
                               </h:outputLink>
                               <h:outputText  rendered="#{ MainLayoutBean.googleAnalyticsKey == null }" 
                                                id="siteStatsText" 
                                                value="Note: Google analytics is not configured for this dataverse network."
                                                />
                        </ui:panelGroup>
                        <ui:panelGroup  block="true" id="groupPanel3" style="padding-top: 20px; padding-bottom: 15px">
                            <h:outputText  id="outputText3" styleClass="vdcSubHeader" value="Network Customization:"/>
                        </ui:panelGroup>
                        <ui:panelGroup  block="true" id="groupPanel4"
                                        separator="&lt;br /&gt;" style="padding-left: 10px">
                            <h:outputLink  id="hyperlink12" value="/dvn/faces/networkAdmin/EditNetworkNamePage.jsp">
                                <h:outputText  id="hyperlink12Text" value="Edit Dataverse Network Name"/>
                            </h:outputLink>
                            <h:outputLink  id="hyperlink3" value="/dvn/faces/admin/EditBannerFooterPage.jsp">
                                <h:outputText  id="hyperlink1Text2" value="Edit Banner and Footer for Network Pages"/>
                            </h:outputLink>
                            <h:outputLink  id="hyperlink5" value="/dvn/faces/networkAdmin/EditNetworkAnnouncementsPage.jsp">
                                <h:outputText  id="hyperlink3Text1" value="Edit Network Homepage description"/>
                            </h:outputLink>                                                  
                            <h:outputLink id="hyperlink17" value="/dvn/faces/admin/EditContactUsPage.jsp">
                                <h:outputText id="hyperlink17Text" value="Set Contact Us E-Mail"/>
                            </h:outputLink>  
                          </ui:panelGroup>
                          
                        <ui:panelGroup  block="true"  style="padding-top: 20px; padding-bottom: 15px">
                            <h:outputText  styleClass="vdcSubHeader" value="Terms of Use:"/>
                        </ui:panelGroup>
                        <ui:panelGroup  block="true" 
                                        separator="&lt;br /&gt;" style="padding-left: 10px">  
                             <h:outputLink  value="/dvn/faces/networkAdmin/EditAccountUseTermsPage.jsp">
                                <h:outputText  value="Edit Network Terms of Use for Account Creation"/>
                            </h:outputLink>                                                  
                           <h:outputLink  value="/dvn/faces/networkAdmin/EditNetworkDepositUseTermsPage.jsp">
                                <h:outputText  value="Edit Network Terms Of Use for Study Creation and Data Deposit"/>
                            </h:outputLink>                                                  
                            <h:outputLink  value="/dvn/faces/networkAdmin/EditNetworkDownloadUseTermsPage.jsp">
                                <h:outputText  value="Edit Network Terms Of Use for File Download"/>
                            </h:outputLink>      
                         </ui:panelGroup>
                         
                        <ui:panelGroup  block="true" id="groupPanel1" style="padding-top: 20px; padding-bottom: 15px">
                            <h:outputText  id="outputText2" styleClass="vdcSubHeader" value="Users, Groups, Permissions:"/>
                        </ui:panelGroup>
                        <ui:panelGroup  block="true" id="groupPanel2"
                                        separator="&lt;br /&gt;" style="padding-left: 10px">
                            <h:outputLink  id="hyperlink1" value="/dvn/faces/networkAdmin/NetworkPrivilegedUsersPage.jsp">
                                <h:outputText  id="hyperlink1Text1" value="Manage Network Users' Privileges (Dataverse creators and Network admins)"/>
                            </h:outputLink>
                            <h:outputLink   value="/dvn/faces/login/AddAccountPage.jsp">
                                <h:outputText styleClass="vdcMenuItem" value="Add New User Account"/>
                            </h:outputLink>
                            <h:outputLink  id="hyperlink6" value="/dvn/faces/networkAdmin/AllUsersPage.jsp">
                                <h:outputText  id="hyperlink6Text" value="All Users"/>
                            </h:outputLink>
                            <h:outputLink  id="hyperlink7" value="/dvn/faces/networkAdmin/UserGroupsPage.jsp">
                                <h:outputText  id="hyperlink7Text" value="User Groups"/>
                            </h:outputLink>
                            
                        </ui:panelGroup>
                        <ui:panelGroup  block="true" style="padding-top: 20px; padding-bottom: 15px">
                            <h:outputText  styleClass="vdcSubHeader" value="Version and License"/> 
                            <h:outputLink   value="/dvn/faces/VersionPage.jsp"> - 
                                <h:outputText  value="About this Dataverse Network installation"/>
                            </h:outputLink>
                            
                        </ui:panelGroup>
                        
                    </div>
                  </div>
             </div>
        </ui:form>               
     </f:subview>
</jsp:root>
