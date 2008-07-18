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
        
        
        <gui:param name="pageTitle" value="DVN - My Options" />
            
        <gui:define name="body">
            
            <ui:form id="form1">
                <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
                    
                <div class="dvn_section">
                    <div class="dvn_sectionTitle">
                        <h:outputText  value="My Options in #{VDCRequest.currentVDC.name} dataverse"/>
                    </div>            
                    <div class="dvn_sectionBox">             
                        <div class="dvn_margin12">
                            <ui:panelGroup block="true" rendered="#{VDCSession.loginBean.contributor and not VDCRequest.currentVDC.harvestingDataverse}">
                                <table class="dvnOptionsTable" cellspacing="0" cellpadding="0" border="0">
                                    <tr><td colspan="2" class="dvnOptionsTableHeader"><h:outputText value="#{bundle.ContributorSection}"/></td></tr>
                                    <tr>
                                        <td class="dvnOptionsTableOption">
                                            <h:outputLink id="hyperlink1" value="/dvn#{VDCRequest.currentVDCURL}/faces/study/EditStudyPage.jsp">
                                                <h:outputText id="hyperlink1Text" value="#{bundle.AddStudyTitle}"/>
                                            </h:outputLink>
                                        </td>
                                        <td><h:outputText class="dvnOptionsTableNote" value="#{bundle.AddStudyHelp}"/></td>
                                    </tr>
                                    <tr>
                                        <td class="dvnOptionsTableOption">
                                            <h:outputLink id="hyperlink2" value="/dvn#{VDCRequest.currentVDCURL}/faces/study/MyStudiesPage.jsp">
                                                <h:outputText id="hyperlink2Text" value="#{bundle.MyStudiesTitle}"/>
                                            </h:outputLink>
                                        </td>
                                        <td><h:outputText class="dvnOptionsTableNote" value="#{bundle.MyStudiesHelp}"/></td>
                                    </tr>
                                </table>
                            </ui:panelGroup>   
                                
                            <ui:panelGroup block="true" rendered="#{VDCSession.loginBean.curator or VDCSession.loginBean.admin or VDCSession.loginBean.networkAdmin}">
                                <table class="dvnOptionsTable" cellspacing="0" cellpadding="0" border="0">
                                    <tr><td colspan="2" class="dvnOptionsTableHeader"><h:outputText value="#{bundle.CuratorSection}"/></td></tr>                                    
                                    <gui:fragment rendered="#{not VDCRequest.currentVDC.harvestingDataverse}">
                                        <tr>
                                            <td class="dvnOptionsTableOption">
                                                <h:outputLink id="hyperlink5" value="/dvn#{VDCRequest.currentVDCURL}/faces/study/EditStudyPage.jsp">
                                                    <h:outputText id="hyperlink1Text1" value="#{bundle.AddStudyTitle}"/>
                                                </h:outputLink>
                                            </td>
                                            <td><h:outputText class="dvnOptionsTableNote" value="#{bundle.AddStudyHelp}"/></td>
                                        </tr>
                                        <tr>
                                            <td class="dvnOptionsTableOption">
                                                <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/study/MyStudiesPage.jsp">
                                                    <h:outputText value="#{bundle.MyStudiesTitle}"/>
                                                </h:outputLink>
                                            </td>
                                            <td><h:outputText class="dvnOptionsTableNote" value="#{bundle.MyStudiesHelp}"/></td>
                                        </tr>
                                        <tr>
                                            <td class="dvnOptionsTableOption">
                                                <h:outputLink id="hyperlink6" value="/dvn#{VDCRequest.currentVDCURL}/faces/study/ReviewStudiesPage.jsp">
                                                    <h:outputText id="hyperlink2Text1" value="#{bundle.ReviewStudiesTitle}"/>
                                                </h:outputLink>
                                            </td>
                                            <td><h:outputText class="dvnOptionsTableNote" value="#{bundle.ReviewStudiesHelp}"/></td>
                                        </tr>
                                        <tr>
                                            <td class="dvnOptionsTableOption">
                                                <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/admin/ManageTemplatesPage.jsp?mode=4" >
                                                    <h:outputText value="#{bundle.TemplatesTitle}" escape="false"/>
                                                </h:outputLink>
                                            </td>
                                            <td><h:outputText class="dvnOptionsTableNote" value="#{bundle.TemplatesHelp}"/></td>
                                        </tr>
                                    </gui:fragment>                                    
                                    <tr>
                                        <td class="dvnOptionsTableOption">
                                            <h:outputLink id="hyperlink8" value="/dvn#{VDCRequest.currentVDCURL}/faces/collection/ManageCollectionsPage.jsp">
                                                <h:outputText id="hyperlink4Text1" value="#{bundle.CollectionsTitle}"/>
                                            </h:outputLink>
                                        </td>
                                        <td><h:outputText class="dvnOptionsTableNote" value="#{bundle.CollectionsHelp}"/></td>
                                    </tr>
                                </table>                                    
                            </ui:panelGroup>
                                
                            <ui:panelGroup block="true" rendered="#{VDCSession.loginBean.admin == true or VDCSession.loginBean.networkAdmin == true}">
                                <table class="dvnOptionsTable" cellspacing="0" cellpadding="0" border="0">
                                    <tr><td colspan="2" class="dvnOptionsTableHeader"><h:outputText value="#{bundle.DvSettingsSection}"/></td></tr>
                                    <tr>
                                        <td class="dvnOptionsTableOption">
                                            <h:outputLink id="hyperlink15" value="/dvn#{VDCRequest.currentVDCURL}/faces/site/EditSitePage.jsp">
                                                <h:outputText id="hyperlink15Text" value="#{bundle.DvSettingsTitle}"/>
                                            </h:outputLink>
                                        </td>
                                        <td><h:outputText class="dvnOptionsTableNote" value="#{bundle.DvSettingsHelp}"/></td>
                                    </tr>
                                    <tr>
                                        <td class="dvnOptionsTableOption">
                                            <h:outputLink id="hyperlink3" value="/dvn#{VDCRequest.currentVDCURL}/faces/admin/EditBannerFooterPage.jsp">
                                                <h:outputText id="hyperlink1Text2" value="#{bundle.BrandingTitle}"/>
                                            </h:outputLink>
                                        </td>
                                        <td><h:outputText class="dvnOptionsTableNote" value="#{bundle.BrandingHelp}"/></td>
                                    </tr>
                                    <tr>
                                        <td class="dvnOptionsTableOption">
                                            <h:outputLink id="hyperlink4" value="/dvn#{VDCRequest.currentVDCURL}/faces/admin/EditHomePanelsPage.jsp">
                                                <h:outputText id="hyperlink2Text2" value="#{bundle.DescriptionTitle}"/>
                                            </h:outputLink>
                                        </td>
                                        <td><h:outputText class="dvnOptionsTableNote" value="#{bundle.DvDescriptionHelp}"/></td>
                                    </tr>
                                    <tr>
                                        <td class="dvnOptionsTableOption">
                                            <h:outputLink id="hyperlink17" value="/dvn#{VDCRequest.currentVDCURL}/faces/admin/EditContactUsPage.jsp">
                                                <h:outputText id="hyperlink17Text" value="#{bundle.NotificationsTitle}"/>
                                            </h:outputLink>
                                        </td>
                                        <td><h:outputText class="dvnOptionsTableNote" value="#{bundle.NotificationsHelp}"/></td>
                                    </tr>
                                    <tr>
                                        <td class="dvnOptionsTableOption">
                                            <h:outputLink id="hyperlink16" value="/dvn#{VDCRequest.currentVDCURL}/faces/admin/SearchFieldsPage.jsp">
                                                <h:outputText id="hyperlink16Text" value="#{bundle.FieldsSearchResultsTitle}"/>
                                            </h:outputLink>
                                        </td>
                                        <td><h:outputText class="dvnOptionsTableNote" value="#{bundle.FieldsSearchResultsHelp}"/></td>
                                    </tr>
                                    <tr>
                                        <td class="dvnOptionsTableOption">
                                            <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/admin/PromotionalLinkSearchBoxPage.jsp">
                                                <h:outputText value="#{bundle.CodePromotionalTitle}"/>
                                            </h:outputLink>
                                        </td>
                                        <td><h:outputText class="dvnOptionsTableNote" value="#{bundle.CodePromotionalHelp}"/></td>
                                    </tr>
                                </table>
                                
                                <table class="dvnOptionsTable" cellspacing="0" cellpadding="0" border="0">
                                    <tr><td colspan="2" class="dvnOptionsTableHeader"><h:outputText value="#{bundle.DvTermsSection}"/></td></tr>
                                    <gui:fragment rendered="#{not VDCRequest.currentVDC.harvestingDataverse}">
                                        <tr>
                                            <td class="dvnOptionsTableOption">
                                                <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/admin/EditDepositUseTermsPage.jsp" >
                                                    <h:outputText id="hyperlink5Text3" value="#{bundle.TermsStudyTitle}"/>
                                                </h:outputLink>
                                            </td>
                                            <td><h:outputText class="dvnOptionsTableNote" value="#{bundle.TermsStudyHelp}"/></td>
                                        </tr>
                                    </gui:fragment>
                                    <tr>
                                        <td class="dvnOptionsTableOption">
                                            <h:outputLink id="hyperlink14" value="/dvn#{VDCRequest.currentVDCURL}/faces/admin/EditUseTermsPage.jsp">
                                                <h:outputText id="hyperlink4Text3" value="#{bundle.TermsFileTitle}"/>
                                            </h:outputLink>
                                        </td>
                                        <td><h:outputText class="dvnOptionsTableNote" value="#{bundle.TermsFileHelp}"/></td>
                                    </tr>
                                </table>
                                
                                <table class="dvnOptionsTable" cellspacing="0" cellpadding="0" border="0">
                                    <tr><td colspan="2" class="dvnOptionsTableHeader"><h:outputText  value="#{bundle.DvPrivilegesTitle}"/></td></tr>
                                    <tr>
                                        <td class="dvnOptionsTableOption">
                                            <h:outputLink id="hyperlink11" value="/dvn#{VDCRequest.currentVDCURL}/faces/admin/PrivilegedUsersPage.jsp">
                                                <h:outputText id="hyperlink1Text3" value="#{bundle.DvPrivilegesTitle}"/>
                                            </h:outputLink>
                                        </td>
                                        <td><h:outputText class="dvnOptionsTableNote" value="#{bundle.DvPrivilegesHelp}"/></td>
                                    </tr>
                                    <tr>
                                        <td class="dvnOptionsTableOption">
                                            <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/login/AddAccountPage.jsp">
                                                <h:outputText value="#{bundle.AddAccountTitle}"/>
                                            </h:outputLink>
                                        </td>
                                        <td><h:outputText class="dvnOptionsTableNote" value="#{bundle.AddAccountHelp}"/></td>
                                    </tr>
                                </table>
                            </ui:panelGroup> 
                        </div>
                    </div>
                </div>
            </ui:form>                
        </gui:define>
    </gui:composition>
</body>
</html>
