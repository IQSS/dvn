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
                
                <ui:form id="form1">
                    <div class="dvn_section">
                        <div class="dvn_sectionTitle">   
                            <h:outputText value="#{VDCRequest.vdcNetwork.name} Dataverse Network Admin Options"/>
                        </div>            
                        <div class="dvn_sectionBox">
                            <div class="dvn_margin12">
                                <table class="dvnOptionsTable" cellspacing="0" cellpadding="0" border="0">
                                    <tr><td colspan="2" class="dvnOptionsTableHeader"><h:outputText value="#{bundle.DataversesSection}"/></td></tr>
                                    <tr>
                                        <td class="dvnOptionsTableOption">
                                            <h:outputLink id="hyperlink4" value="/dvn/faces/site/AddSitePage.jsp">
                                                <h:outputText id="hyperlink4Text" value="#{bundle.CreateDvTitle}"/>
                                            </h:outputLink>
                                        </td>
                                        <td><h:outputText class="dvnOptionsTableNote" styleClass="dvnOptionsTableNote" value="#{bundle.CreateDvHelp}"/></td>
                                    </tr>
                                    <tr>
                                        <td class="dvnOptionsTableOption">
                                            <h:outputLink value="/dvn/faces/site/EditHarvestSitePage.jsp">
                                                <h:outputText value="#{bundle.CreateHarvestingDvTitle}"/>
                                            </h:outputLink>
                                        </td>
                                        <td><h:outputText class="dvnOptionsTableNote" value="#{bundle.CreateHarvestingDvHelp}"/></td>
                                    </tr>
                                    <tr>
                                        <td class="dvnOptionsTableOption">
                                            <h:outputLink value="/dvn/faces/site/HarvestSitesPage.jsp">
                                                <h:outputText value="#{bundle.ManageDvsTitle}"/>
                                            </h:outputLink>
                                        </td>
                                        <td><h:outputText class="dvnOptionsTableNote" value="#{bundle.ManageDvsHelp}"/></td>
                                    </tr>
                                    <tr>
                                        <td class="dvnOptionsTableOption">
                                            <h:outputLink value="/dvn/faces/networkAdmin/VDCGroupDetailPage.jsp">
                                                <h:outputText value="#{bundle.ManageDvGroupsTitle}"/>
                                            </h:outputLink>
                                        </td>
                                        <td><h:outputText class="dvnOptionsTableNote" value="#{bundle.ManageDvGroupsHelp}"/></td>
                                    </tr>
                                    <tr>
                                        <td class="dvnOptionsTableOption">
                                            <h:outputLink value="/dvn/faces/networkAdmin/EditExportSchedulePage.jsp">
                                                <h:outputText value="#{bundle.ExportsTitle}"/>
                                            </h:outputLink>
                                        </td>
                                        <td><h:outputText class="dvnOptionsTableNote" value="#{bundle.ExportsHelp}"/></td>
                                    </tr>
                                    <tr>
                                        <td class="dvnOptionsTableOption">
                                            <h:outputLink value="/dvn/faces/networkAdmin/OAISetsPage.jsp">
                                                <h:outputText value="#{bundle.OAISetsTitle}"/>
                                            </h:outputLink>
                                        </td>
                                        <td><h:outputText class="dvnOptionsTableNote" value="#{bundle.OAISetsHelp}"/></td>
                                    </tr>
                                </table>
                                <table class="dvnOptionsTable" cellspacing="0" cellpadding="0" border="0">
                                    <tr><td colspan="2" class="dvnOptionsTableHeader"><h:outputText id="outputText3" value="#{bundle.NetworkSettingsSection}"/></td></tr>
                                    <tr>
                                        <td class="dvnOptionsTableOption">
                                            <h:outputLink id="hyperlink12" value="/dvn/faces/networkAdmin/EditNetworkNamePage.jsp">
                                                <h:outputText id="hyperlink12Text" value="#{bundle.NetworkNameTitle}"/>
                                            </h:outputLink>
                                        </td>
                                        <td><h:outputText class="dvnOptionsTableNote" value="#{bundle.NetworkNameHelp}"/></td>
                                    </tr>
                                    <tr>
                                        <td class="dvnOptionsTableOption">
                                            <h:outputLink id="hyperlink3" value="/dvn/faces/admin/EditBannerFooterPage.jsp">
                                                <h:outputText id="hyperlink1Text2" value="#{bundle.BrandingTitle}"/>
                                            </h:outputLink>
                                        </td>
                                        <td><h:outputText class="dvnOptionsTableNote" value="#{bundle.BrandingHelp}"/></td>
                                    </tr>
                                    <tr>
                                        <td class="dvnOptionsTableOption">
                                            <h:outputLink id="hyperlink5" value="/dvn/faces/networkAdmin/EditNetworkAnnouncementsPage.jsp">
                                                <h:outputText id="hyperlink3Text1" value="#{bundle.DescriptionTitle}"/>
                                            </h:outputLink>
                                        </td>
                                        <td><h:outputText class="dvnOptionsTableNote" value="#{bundle.NetworkDescriptionHelp}"/></td>
                                    </tr>
                                    <tr>
                                        <td class="dvnOptionsTableOption">
                                            <h:outputLink id="hyperlink17" value="/dvn/faces/admin/EditContactUsPage.jsp">
                                                <h:outputText id="hyperlink17Text" value="#{bundle.NotificationsTitle}"/>
                                            </h:outputLink>
                                        </td>
                                        <td><h:outputText class="dvnOptionsTableNote" value="#{bundle.NotificationsHelp}"/></td>
                                    </tr>
                                </table>
                                <table class="dvnOptionsTable" cellspacing="0" cellpadding="0" border="0">
                                    <tr><td colspan="2" class="dvnOptionsTableHeader"><h:outputText value="#{bundle.NetworkTermsSection}"/></td></tr>
                                    <tr>
                                        <td class="dvnOptionsTableOption">
                                            <h:outputLink value="/dvn/faces/networkAdmin/EditAccountUseTermsPage.jsp">
                                                <h:outputText value="#{bundle.TermsAccountTitle}"/>
                                            </h:outputLink>
                                        </td>
                                        <td><h:outputText class="dvnOptionsTableNote" value="#{bundle.TermsAccountHelp}"/></td>
                                    </tr>
                                    <tr>
                                        <td class="dvnOptionsTableOption">
                                            <h:outputLink value="/dvn/faces/networkAdmin/EditNetworkDepositUseTermsPage.jsp">
                                                <h:outputText value="#{bundle.TermsStudyTitle}"/>
                                            </h:outputLink>
                                        </td>
                                        <td><h:outputText class="dvnOptionsTableNote" value="#{bundle.TermsStudyHelp}"/></td>
                                    </tr>
                                    <tr>
                                        <td class="dvnOptionsTableOption">
                                            <h:outputLink value="/dvn/faces/networkAdmin/EditNetworkDownloadUseTermsPage.jsp">
                                                <h:outputText value="#{bundle.TermsFileTitle}"/>
                                            </h:outputLink>
                                        </td>
                                        <td><h:outputText class="dvnOptionsTableNote" value="#{bundle.TermsFileHelp}"/></td>
                                    </tr>
                                </table>
                                <table class="dvnOptionsTable" cellspacing="0" cellpadding="0" border="0">
                                    <tr><td colspan="2" class="dvnOptionsTableHeader"><h:outputText value="#{bundle.NetworkPrivilegesSection}"/></td></tr>
                                    <tr>
                                        <td class="dvnOptionsTableOption">
                                            <h:outputLink id="hyperlink1" value="/dvn/faces/networkAdmin/NetworkPrivilegedUsersPage.jsp">
                                                <h:outputText id="hyperlink1Text1" value="#{bundle.NetworkPrivilegesTitle}"/>
                                            </h:outputLink>
                                        </td>
                                        <td><h:outputText class="dvnOptionsTableNote" value="#{bundle.NetworkPrivilegesHelp}"/></td>
                                    </tr>
                                    <tr>
                                        <td class="dvnOptionsTableOption">
                                            <h:outputLink value="/dvn/faces/login/AddAccountPage.jsp">
                                                <h:outputText value="#{bundle.AddAccountTitle}"/>
                                            </h:outputLink>
                                        </td>
                                        <td><h:outputText class="dvnOptionsTableNote" value="#{bundle.AddAccountHelp}"/></td>
                                    </tr>
                                    <tr>
                                        <td class="dvnOptionsTableOption">
                                            <h:outputLink id="hyperlink6" value="/dvn/faces/networkAdmin/AllUsersPage.jsp">
                                                <h:outputText id="hyperlink6Text" value="#{bundle.UsersTitle}"/>
                                            </h:outputLink>
                                        </td>
                                        <td><h:outputText class="dvnOptionsTableNote" value="#{bundle.UsersHelp}"/></td>
                                    </tr>
                                    <tr>
                                        <td class="dvnOptionsTableOption">
                                            <h:outputLink id="hyperlink7" value="/dvn/faces/networkAdmin/UserGroupsPage.jsp">
                                                <h:outputText id="hyperlink7Text" value="#{bundle.GroupsTitle}"/>
                                            </h:outputLink>
                                        </td>
                                        <td><h:outputText class="dvnOptionsTableNote" value="#{bundle.GroupsHelp}"/></td>
                                    </tr>
                                </table>
                                <table class="dvnOptionsTable" cellspacing="0" cellpadding="0" border="0">
                                    <tr><td colspan="2" class="dvnOptionsTableHeader"><h:outputText value="#{bundle.UtilitiesSection}"/></td></tr>
                                    <tr>
                                        <td class="dvnOptionsTableOption">
                                            <h:outputLink value="/dvn/faces/networkAdmin/UtilitiesPage.jsp">
                                                <h:outputText value="#{bundle.TroubleshootTitle}"/>
                                            </h:outputLink>
                                        </td>
                                        <td><h:outputText class="dvnOptionsTableNote" value="#{bundle.TroubleshootHelp}"/></td>
                                    </tr>
                                    <tr>
                                        <td class="dvnOptionsTableOption">
                                            <h:outputText rendered="#{MainLayoutBean.googleAnalyticsKey == null }"
                                                          value="Google Analytics"/>
                                            <h:outputLink rendered="#{MainLayoutBean.googleAnalyticsKey != null }" 
                                                          id="siteStatslink" 
                                                          value="http://www.google.com/analytics/"
                                                          target="_blank">
                                                <h:outputText value="#{bundle.WebUsageTitle}"/>
                                            </h:outputLink>
                                        </td>
                                        <td>
                                            <h:outputText class="dvnOptionsTableNote" rendered="#{MainLayoutBean.googleAnalyticsKey != null }" value="#{bundle.WebUsageHelp}"/>
                                            <h:outputText class="dvnOptionsTableNote" rendered="#{MainLayoutBean.googleAnalyticsKey == null }" 
                                                          id="siteStatsText" 
                                                          value="NOTE: Google Analytics is not configured for this Dataverse Network"/>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="dvnOptionsTableOption">
                                            Dataverse Network Version
                                        </td>
                                        <td><h:outputText class="dvnOptionsTableNote" value="#{VersionPage.versionNumber}.#{VersionPage.buildNumber}"/></td>
                                    </tr>
                                </table>
                                
                                <div>This software is distributed under the <a href="http://www.affero.org/oagpl.html" target="_blank">Affero General Public License</a></div>
                                
                            </div>
                        </div>
                    </div>
                </ui:form>               
            </gui:define>
        </gui:composition>
    </body>
</html>
