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
                            <div>
                                <h:outputText value="#{bundle.ContributorSection}"/>
                            </div>
                            <div>
                                <h:outputLink id="hyperlink1" value="/dvn#{VDCRequest.currentVDCURL}/faces/study/EditStudyPage.jsp">
                                    <h:outputText  id="hyperlink1Text" value="#{bundle.AddStudyTitle}"/>
                                </h:outputLink>
                                <h:outputText  value=" - #{bundle.AddStudyHelp}"/>
                                <br />
                                <h:outputLink id="hyperlink2" value="/dvn#{VDCRequest.currentVDCURL}/faces/study/MyStudiesPage.jsp">
                                    <h:outputText  id="hyperlink2Text" value="#{bundle.MyStudiesTitle}"/>
                                </h:outputLink>
                                <h:outputText  value=" - #{bundle.MyStudiesHelp}"/> 
                            </div>
                         </ui:panelGroup>   
                        
                        <ui:panelGroup block="true" rendered="#{VDCSession.loginBean.curator or VDCSession.loginBean.admin or VDCSession.loginBean.networkAdmin}">
                            <div>
                                <h:outputText value="#{bundle.CuratorSection}"/>
                            </div>
                            <ui:panelGroup block="true"  rendered="#{not VDCRequest.currentVDC.harvestingDataverse}">
                                <h:outputLink id="hyperlink5"  value="/dvn#{VDCRequest.currentVDCURL}/faces/study/EditStudyPage.jsp">
                                    <h:outputText  id="hyperlink1Text1" value="#{bundle.AddStudyTitle}"/>
                                </h:outputLink>
                                <h:outputText  value=" - #{bundle.AddStudyHelp}"/>
                                <br />
                                <h:outputLink   value="/dvn#{VDCRequest.currentVDCURL}/faces/study/MyStudiesPage.jsp">
                                    <h:outputText   value="#{bundle.MyStudiesTitle}"/>
                                </h:outputLink>
                                <h:outputText  value=" - #{bundle.MyStudiesHelp}"/>
                                <br />
                                <h:outputLink  id="hyperlink6" value="/dvn#{VDCRequest.currentVDCURL}/faces/study/ReviewStudiesPage.jsp">
                                    <h:outputText id="hyperlink2Text1" value="#{bundle.ReviewStudiesTitle}"/>
                                </h:outputLink>
                                 <h:outputText  value=" - #{bundle.ReviewStudiesHelp}"/>
                                 <br />
                                 <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/admin/ManageTemplatesPage.jsp?mode=4" >
                                    <h:outputText  value="#{bundle.TemplatesTitle}" escape="false"/>
                                </h:outputLink>
                                <h:outputText  value=" - #{bundle.TemplatesHelp}"/>
                            </ui:panelGroup>
                              
                                <h:outputLink  id="hyperlink8" value="/dvn#{VDCRequest.currentVDCURL}/faces/collection/ManageCollectionsPage.jsp">
                                    <h:outputText  id="hyperlink4Text1" value="#{bundle.CollectionsTitle}"/>
                                </h:outputLink >
                                <h:outputText  value=" - #{bundle.CollectionsHelp}"/>
                                 <br />
                                
                       </ui:panelGroup>
                       
                        <ui:panelGroup block="true" rendered="#{VDCSession.loginBean.admin == true or VDCSession.loginBean.networkAdmin == true}">
                            <div>
                                <h:outputText  value="#{bundle.AdminSection}"/>
                            </div>
                            <div>
                                <h:outputText value="#{bundle.DvSettingsSection}"/>
                              </div>
                             <div>
                                    <h:outputLink id="hyperlink15" value="/dvn#{VDCRequest.currentVDCURL}/faces/site/EditSitePage.jsp">
                                        <h:outputText id="hyperlink15Text" value="#{bundle.DvSettingsTitle}"/>
                                    </h:outputLink>
                                    <h:outputText  value=" - #{bundle.DvSettingsHelp}"/>
                                    <br />
                                    
                                    <h:outputLink id="hyperlink3" value="/dvn#{VDCRequest.currentVDCURL}/faces/admin/EditBannerFooterPage.jsp">
                                        <h:outputText id="hyperlink1Text2" value="#{bundle.BrandingTitle}"/>
                                    </h:outputLink>
                                    <h:outputText  value=" - #{bundle.BrandingHelp}"/>
                                    <br />
                                    
                                    <h:outputLink id="hyperlink4" value="/dvn#{VDCRequest.currentVDCURL}/faces/admin/EditHomePanelsPage.jsp">
                                        <h:outputText id="hyperlink2Text2" value="#{bundle.DescriptionTitle}"/>
                                    </h:outputLink>
                                    <h:outputText  value=" - #{bundle.DvDescriptionHelp}"/>
                                    <br />
                                    
                                    <h:outputLink id="hyperlink17" value="/dvn#{VDCRequest.currentVDCURL}/faces/admin/EditContactUsPage.jsp">
                                        <h:outputText id="hyperlink17Text" value="#{bundle.NotificationsTitle}"/>
                                    </h:outputLink>
                                    <h:outputText  value=" - #{bundle.NotificationsHelp}"/>
                                    <br />
                                    
                                    <h:outputLink id="hyperlink16" value="/dvn#{VDCRequest.currentVDCURL}/faces/admin/SearchFieldsPage.jsp">
                                        <h:outputText id="hyperlink16Text" value="#{bundle.FieldsSearchResultsTitle}"/>
                                    </h:outputLink>
                                    <h:outputText  value=" - #{bundle.FieldsSearchResultsHelp}"/>
                                    <br />
                                    
                                    <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/admin/PromotionalLinkSearchBoxPage.jsp">
                                        <h:outputText value="#{bundle.CodePromotionalTitle}"/>
                                    </h:outputLink>
                                    <h:outputText  value=" - #{bundle.CodePromotionalHelp}"/>
                                    <br />
                                </div>    
                                  <div>
                                     <h:outputText value="#{bundle.DvTermsSection}"/>
                                    </div>
                                    
                                    <ui:panelGroup block="true" rendered="#{not VDCRequest.currentVDC.harvestingDataverse}">
                                        <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/admin/EditDepositUseTermsPage.jsp" >
                                            <h:outputText id="hyperlink5Text3" value="#{bundle.TermsStudyTitle}"/>
                                        </h:outputLink>
                                         <h:outputText  value=" - #{bundle.TermsStudyHelp}"/>
                                        <br />
                                    </ui:panelGroup>
                                    
                                     <h:outputLink id="hyperlink14"  value="/dvn#{VDCRequest.currentVDCURL}/faces/admin/EditUseTermsPage.jsp">
                                        <h:outputText id="hyperlink4Text3" value="#{bundle.TermsFileTitle}"/>
                                    </h:outputLink>
                                    <h:outputText  value=" - #{bundle.TermsFileHelp}"/>
                                    <br />
                                 
                                 <div>
                                     <h:outputText  value="#{bundle.DvPrivilegesTitle}"/>
                                 </div>
                                  
                                <div>
                                    <h:outputLink id="hyperlink11" value="/dvn#{VDCRequest.currentVDCURL}/faces/admin/PrivilegedUsersPage.jsp">
                                        <h:outputText id="hyperlink1Text3" value="#{bundle.DvPrivilegesTitle}"/>
                                    </h:outputLink>
                                    <h:outputText  value=" - #{bundle.DvPrivilegesHelp}"/>
                                    <br />
                                    
                                    <h:outputLink   value="/dvn#{VDCRequest.currentVDCURL}/faces/login/AddAccountPage.jsp">
                                        <h:outputText  value="#{bundle.AddAccountTitle}"/>
                                    </h:outputLink>
                                    <h:outputText  value=" - #{bundle.AddAccountHelp}"/>
                                    <br />
                                    
                                </div>
                            </ui:panelGroup> 
                        </div>
                    </div>
                </div>
        </ui:form>                
            </gui:define>
        </gui:composition>
    </body>
</html>
