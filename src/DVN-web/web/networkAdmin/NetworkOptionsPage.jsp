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
                
                <ui:form  id="form1">
                    <div class="dvn_section">
                        <div class="dvn_sectionTitle">   
                            <h:outputText  value="#{VDCRequest.vdcNetwork.name} Dataverse Network Admin Options"/>
                        </div>            
                        <div class="dvn_sectionBox">
                            <div class="dvn_margin12">
                                <div>
                                    <h:outputText value="#{bundle.DataversesSection}"/>
                                </div>
                                <div>
                                    <h:outputLink  id="hyperlink4" value="/dvn/faces/site/AddSitePage.jsp">
                                        <h:outputText  id="hyperlink4Text" value="#{bundle.CreateDvTitle}"/>
                                    </h:outputLink> 
                                    <h:outputText  value=" - #{bundle.CreateDvHelp}"/>
                                    <br />
                                    <h:outputLink  value="/dvn/faces/site/EditHarvestSitePage.jsp">
                                        <h:outputText   value="#{bundle.CreateHarvestingDvTitle}"/>
                                    </h:outputLink>
                                    <h:outputText  value=" - #{bundle.CreateHarvestingDvHelp}"/>
                                    <br />
                                    <h:outputLink   value="/dvn/faces/site/HarvestSitesPage.jsp">
                                        <h:outputText  value="#{bundle.ManageDvsTitle}"/>
                                    </h:outputLink>
                                    <h:outputText  value=" - #{bundle.ManageDvsHelp}"/>
                                    <br />
                                    <h:outputLink   value="/dvn/faces/networkAdmin/VDCGroupDetailPage.jsp">
                                        <h:outputText  value="#{bundle.ManageDvGroupsTitle}"/>
                                    </h:outputLink>
                                    <h:outputText  value=" - #{bundle.ManageDvGroupsHelp}"/>
                                    <br />
                                    <h:outputLink  value="/dvn/faces/networkAdmin/EditExportSchedulePage.jsp">
                                        <h:outputText  value="#{bundle.ExportsTitle}"/>
                                    </h:outputLink> 
                                    <h:outputText  value=" - #{bundle.ExportsHelp}"/>
                                    <br />
                                    <h:outputLink  value="/dvn/faces/networkAdmin/OAISetsPage.jsp">
                                        <h:outputText  value="#{bundle.OAISetsTitle}"/>
                                    </h:outputLink>
                                    <h:outputText  value=" - #{bundle.OAISetsHelp}"/>
                                    <br />
                                </div>
                                <div>
                                    <h:outputText  id="outputText3" value="#{bundle.NetworkSettingsSection}"/>
                                </div>
                                <div>
                                    <h:outputLink  id="hyperlink12" value="/dvn/faces/networkAdmin/EditNetworkNamePage.jsp">
                                        <h:outputText  id="hyperlink12Text" value="#{bundle.NetworkNameTitle}"/>
                                    </h:outputLink>
                                    <h:outputText  value=" - #{bundle.NetworkNameHelp}"/>
                                    <br />
                                    <h:outputLink  id="hyperlink3" value="/dvn/faces/admin/EditBannerFooterPage.jsp">
                                        <h:outputText  id="hyperlink1Text2" value="#{bundle.BrandingTitle}"/>
                                    </h:outputLink>
                                    <h:outputText  value=" - #{bundle.BrandingHelp}"/>
                                    <br />
                                    <h:outputLink  id="hyperlink5" value="/dvn/faces/networkAdmin/EditNetworkAnnouncementsPage.jsp">
                                        <h:outputText  id="hyperlink3Text1" value="#{bundle.DescriptionTitle}"/>
                                    </h:outputLink> 
                                    <h:outputText  value=" - #{bundle.NetworkDescriptionHelp}"/>
                                    <br />
                                    <h:outputLink id="hyperlink17" value="/dvn/faces/admin/EditContactUsPage.jsp">
                                        <h:outputText id="hyperlink17Text" value="#{bundle.NotificationsTitle}"/>
                                    </h:outputLink>  
                                    <h:outputText  value=" - #{bundle.NotificationsHelp}"/>
                                    <br />
                                </div>
                                
                                <div>
                                    <h:outputText  value="#{bundle.NetworkTermsSection}"/>
                                </div>
                                <div>  
                                    <h:outputLink  value="/dvn/faces/networkAdmin/EditAccountUseTermsPage.jsp">
                                        <h:outputText  value="#{bundle.TermsAccountTitle}"/>
                                    </h:outputLink>     
                                    <h:outputText  value=" - #{bundle.TermsAccountHelp}"/>
                                    <br />
                                    <h:outputLink  value="/dvn/faces/networkAdmin/EditNetworkDepositUseTermsPage.jsp">
                                        <h:outputText  value="#{bundle.TermsStudyTitle}"/>
                                    </h:outputLink>  
                                    <h:outputText  value=" - #{bundle.TermsStudyHelp}"/>
                                    <br />
                                    <h:outputLink  value="/dvn/faces/networkAdmin/EditNetworkDownloadUseTermsPage.jsp">
                                        <h:outputText  value="#{bundle.TermsFileTitle}"/>
                                    </h:outputLink>      
                                    <h:outputText  value=" - #{bundle.TermsFileHelp}"/>
                                    <br />
                                </div>
                                
                                <div>
                                    <h:outputText value="#{bundle.NetworkPrivilegesSection}"/>
                                </div>
                                <div>
                                    <h:outputLink  id="hyperlink1" value="/dvn/faces/networkAdmin/NetworkPrivilegedUsersPage.jsp">
                                        <h:outputText  id="hyperlink1Text1" value="#{bundle.NetworkPrivilegesTitle}"/>
                                    </h:outputLink>
                                    <h:outputText  value=" - #{bundle.NetworkPrivilegesHelp}"/>
                                    <br />
                                    <h:outputLink   value="/dvn/faces/login/AddAccountPage.jsp">
                                        <h:outputText styleClass="vdcMenuItem" value="#{bundle.AddAccountTitle}"/>
                                    </h:outputLink>
                                    <h:outputText  value=" - #{bundle.AddAccountHelp}"/>
                                    <br />
                                    <h:outputLink  id="hyperlink6" value="/dvn/faces/networkAdmin/AllUsersPage.jsp">
                                        <h:outputText  id="hyperlink6Text" value="#{bundle.UsersTitle}"/>
                                    </h:outputLink>
                                    <h:outputText  value=" - #{bundle.UsersHelp}"/>
                                    <br />
                                    <h:outputLink  id="hyperlink7" value="/dvn/faces/networkAdmin/UserGroupsPage.jsp">
                                        <h:outputText  id="hyperlink7Text" value="#{bundle.GroupsTitle}"/>
                                    </h:outputLink>
                                    <h:outputText  value=" - #{bundle.GroupsHelp}"/>
                                    <br />
                                    
                                </div>
                                
                                <div>
                                    <h:outputText  value="#{bundle.UtilitiesSection}"/>
                                </div>
                                <div>                      
                                    <h:outputLink  value="/dvn/faces/networkAdmin/UtilitiesPage.jsp">
                                        <h:outputText value="#{bundle.TroubleshootTitle}"/>
                                    </h:outputLink>
                                    <h:outputText  value=" - #{bundle.TroubleshootHelp}"/>
                                    <br />
                                    
                                    <h:outputLink  rendered="#{ MainLayoutBean.googleAnalyticsKey != null }" 
                                                   id="siteStatslink" 
                                                   value="http://www.google.com/analytics/"
                                                   target="_blank">
                                        <h:outputText  value="#{bundle.WebUsageTitle}"/>
                                    </h:outputLink>
                                    <h:outputText rendered="#{ MainLayoutBean.googleAnalyticsKey != null }" value=" - #{bundle.WebUsageHelp}"/>
                                    <h:outputText  rendered="#{ MainLayoutBean.googleAnalyticsKey == null }" 
                                                   id="siteStatsText" 
                                                   value="Note: Google analytics is not configured for this Dataverse Network."
                                                   />
                                    
                                    <br />
                                    <div>
                                    Dataverse Network Version <h:outputText value="#{VersionPage.versionNumber}" />.<h:outputText value="#{VersionPage.buildNumber}" />
                                    <br />
                                    This software is distributed under the <a href="http://www.affero.org/oagpl.html" target="_blank">Affero General Public License</a>    
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </ui:form>               
            </gui:define>
        </gui:composition>
    </body>
</html>
