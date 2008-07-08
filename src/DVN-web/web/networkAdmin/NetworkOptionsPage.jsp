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

<gui:param name="pageTitle" value="DVN - Dataverse Network Admin Options" />

  <gui:define name="body">
      <f:loadBundle basename="Bundle" var="bundle"/>
     <f:loadBundle basename="BundleAnalysis" var="bundleAnalysis"/>

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
                            <h:outputLink   value="/dvn/faces/networkAdmin/VDCGroupDetailPage.jsp">
                                <h:outputText  value="Manage Dataverse Groups"/>
                            </h:outputLink>
                            <h:outputLink  value="/dvn/faces/networkAdmin/EditExportSchedulePage.jsp">
                                <h:outputText  value="Schedule Study Export"/>
                            </h:outputLink>                           
                              <h:outputLink  value="/dvn/faces/networkAdmin/OAISetsPage.jsp">
                                <h:outputText  value="Manage OAI Harvesting Sets"/>
                            </h:outputLink>
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
                                <h:outputText id="hyperlink17Text" value="Set Contact and Notifications E-Mail"/>
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
                            <h:outputText  id="outputText2" styleClass="vdcSubHeader" value="Users, Groups, Privileges:"/>
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
                                <h:outputText  id="hyperlink6Text" value="Manage Users"/>
                            </h:outputLink>
                            <h:outputLink  id="hyperlink7" value="/dvn/faces/networkAdmin/UserGroupsPage.jsp">
                                <h:outputText  id="hyperlink7Text" value="Manage Users and IP Groups"/>
                            </h:outputLink>
                            
                        </ui:panelGroup>
                        
                        <ui:panelGroup  block="true"  style="padding-top: 20px; padding-bottom: 15px">
                            <h:outputText  styleClass="vdcSubHeader" value="Utilities, Web stats, Version:"/>
                        </ui:panelGroup>
                        <ui:panelGroup  block="true" separator="&lt;br /&gt;" style="padding-left: 10px">
                            
                            <h:outputLink  value="/dvn/faces/networkAdmin/UtilitiesPage.jsp">
                                <h:outputText value="Utilities"/>
                            </h:outputLink>
                             <ui:panelGroup>
                            <h:outputLink  rendered="#{ MainLayoutBean.googleAnalyticsKey != null }" 
                                                id="siteStatslink" 
                                                value="http://www.google.com/analytics/"
                                                target="_blank">
                                                <h:outputText  value="Access Google Analytics"/>
                               </h:outputLink>
                               <h:outputText  rendered="#{ MainLayoutBean.googleAnalyticsKey == null }" 
                                                id="siteStatsText" 
                                                value="Note: Google analytics is not configured for this dataverse network."
                                                />
                               </ui:panelGroup>
                               <h:outputLink   value="/dvn/faces/VersionPage.jsp">
                                <h:outputText  value="About this Dataverse Network installation"/>
                            </h:outputLink>  
                        </ui:panelGroup>
                        
                    </div>
                  </div>
             </div>
        </ui:form>               
            </gui:define>
        </gui:composition>
    </body>
</html>
