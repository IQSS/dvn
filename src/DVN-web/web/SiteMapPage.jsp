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

<gui:param name="pageTitle" value="DVN - Site Map" />

  <gui:define name="body">
      <f:loadBundle basename="Bundle" var="bundle"/>
     <f:loadBundle basename="BundleAnalysis" var="bundleAnalysis"/>        
        <ui:form  id="SiteMapForm">  
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
            
            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                    <h:outputText value="Site Map for the Dataverse Network" rendered="#{VDCRequest.currentVDC == null}" />
                    <h:outputText value="Site Map for this Dataverse" rendered="#{VDCRequest.currentVDC != null}" />
                </div>            
                <div class="dvn_sectionBox"> 
                    <div class="dvn_margin12">
                        <!-- sitemapHomeHeading -->
                        <ui:panelGroup block="true" id="groupPanel0">
                            <ui:panelGroup  block="true"  style="padding-top: 20px; padding-bottom: 15px;">
                                <h:outputText  id="homeHeading" styleClass="vdcSubHeader" value="#{bundle.sitemapBrowseHeading}"/>
                            </ui:panelGroup>
                            <ui:panelGroup  block="true" id="groupPanel2" separator="&lt;br /&gt;" style="padding-left: 20px">
                                <h:outputLink id="basicSearchLink"  styleClass="vdcSiteMapLink" value="/dvn#{VDCRequest.currentVDCURL}#search">
                                    <h:outputText  id="basicSearchText" value="#{bundle.sitemapSearchLink}"/>
                                </h:outputLink>
                                <h:outputLink id="advSearchLink3" styleClass="vdcSiteMapLink" value="/dvn#{VDCRequest.currentVDCURL}/faces/AdvSearchPage.jsp#advancedSearch">
                                    <h:outputText  id="advSearchText" value="#{bundle.sitemapAdvancedSearchLink}"/>
                                </h:outputLink>
                                <h:outputLink id="browsedvLink" styleClass="vdcSiteMapLink" value="/dvn#{VDCRequest.currentVDCURL}/faces/HomePage.jsp#browse">
                                    <h:outputText  id="browsedvText" rendered="#{VDCRequest.currentVDC == null}" value="Browse Dataverses"/>
                                    <h:outputText  id="browseCollectionsText" rendered="#{VDCRequest.currentVDC != null}" value="#{bundle.sitemapBrowseCollectionsLink}"/>
                                </h:outputLink> 
                            </ui:panelGroup>
                            
                        </ui:panelGroup>
                        
                        <!-- sitemapAccountHeading -->
                        <ui:panelGroup block="true">
                            <ui:panelGroup  block="true" id="groupPanel7" style="padding-top: 20px; padding-bottom: 15px;">
                                <h:outputText  id="accountHeading" styleClass="vdcSubHeader" value="#{bundle.sitemapAccountHeading}"/>
                            </ui:panelGroup>
                            <ui:panelGroup block="true" id="groupPanel8"  style="padding-left: 20px">
                                <h:outputLink id="sitemaplink8" styleClass="vdcSiteMapLink" value="/dvn#{VDCRequest.currentVDCURL}/faces/login/LoginPage.jsp#login">
                                    <h:outputText id="sitemaplink8Text" value="#{bundle.sitemapLoginLink}"/>
                                </h:outputLink>  
                                <ui:panelGroup block="true" rendered="#{HomePage.showRequestContributor}" >
                                    <h:outputLink id="contributorRequestAccountLink"  styleClass="vdcSiteMapLink" value="/dvn#{VDCRequest.currentVDCURL}/faces/login/ContributorRequestInfoPage.jsp">
                                        <h:outputText id="contributorRequestAccountText" value="#{bundle.sitemapBecomeContributorLink}"/>
                                    </h:outputLink> 
                                </ui:panelGroup>
                                <ui:panelGroup block="true" rendered="#{HomePage.showRequestCreator}" >
                                    <h:outputLink  styleClass="vdcSiteMapLink" value="/dvn/faces/login/CreatorRequestInfoPage.jsp">
                                        <h:outputText   value="Create your own Dataverse"/>
                                    </h:outputLink>
                                </ui:panelGroup>
                            </ui:panelGroup>
                        </ui:panelGroup>
                        
                        <!-- sitemapAboutHeading -->
                        <ui:panelGroup block="true">
                            <ui:panelGroup  block="true" id="groupPanel9" style="padding-top: 20px; padding-bottom: 15px;">
                                <h:outputText  id="aboutHeading" styleClass="vdcSubHeader" value="#{bundle.sitemapAboutHeading}"/>
                            </ui:panelGroup>
                            <ui:panelGroup block="true" id="groupPanel10" style="padding-left: 20px">
                                
                                <f:verbatim>
                                    <!-- note this is a workaround to fix an issue that was caused by the 3rd nested panelGroup that
                                    paired the You are here text with the link previously. This caused the renderer to start outputting
                                    closing span tags at the end of this section when closing div tags were required. -->
                                    <span>
                                        <h:outputLink id="sitemaplink14" styleClass="vdcSiteMapLink" value="/dvn#{VDCRequest.currentVDCURL}/faces/SiteMapPage.jsp">
                                            <h:outputText  id="sitemaplink14Text" value="#{bundle.sitemapSitemapLink}"/>
                                        </h:outputLink>
                                        &#160;&#160;
                                        <h:outputText id="aboutHere" style="display:inline;font-style:italic;" value="#{bundle.sitemapHereMsg}"/>
                                    </span>
                                </f:verbatim> 
                                <f:verbatim rendered="#{(VDCRequest.vdcNetwork.displayAnnouncements == true) and (VDCRequest.currentVDC == null)}" ><br /></f:verbatim>
                                <h:outputLink rendered="#{(VDCRequest.vdcNetwork.displayAnnouncements == true) and (VDCRequest.currentVDC == null)}" 
                                              id="sitemaplink4" 
                                              styleClass="vdcSiteMapLink" 
                                              value="/dvn#{VDCRequest.currentVDCURL}/faces/AnnouncementsPage.jsp#announcements">
                                    <h:outputText rendered="#{((VDCRequest.vdcNetwork.displayAnnouncements == true) and (VDCRequest.currentVDC == null))}" id="sitemaplink4Text" value="#{bundle.sitemapNetworkAnnouncementsLink}"/>
                                </h:outputLink>
                                <f:verbatim rendered="#{VDCRequest.currentVDC != null and VDCRequest.currentVDC.displayAnnouncements == true}"><br /></f:verbatim>
                                <h:outputLink rendered="#{VDCRequest.currentVDC != null and VDCRequest.currentVDC.displayAnnouncements == true}" id="sitemaplink5" styleClass="vdcSiteMapLink" value="/dvn#{VDCRequest.currentVDCURL}/faces/AnnouncementsPage.jsp#announcements">
                                    <h:outputText id="sitemaplink5Text" value="#{bundle.sitemapLocalAnnouncementsLink}"/>
                                </h:outputLink>
                                <f:verbatim><br /></f:verbatim>
                                <h:outputLink id="sitemaplink15" styleClass="vdcSiteMapLink" value="/dvn#{VDCRequest.currentVDCURL}/faces/ContactUsPage.jsp#contact">
                                    <h:outputText  id="sitemaplink15Text" value="#{bundle.sitemapContactUsLink}"/>
                                </h:outputLink>
                                <f:verbatim><br /></f:verbatim>
                                <h:outputLink id="sitemaplinkUserManual" styleClass="vdcSiteMapLink" target="_blank" value="http://thedata.org/guides">
                                    <h:outputText  id="sitemaplinkUserManualText" value="#{bundle.sitemapUserManualLink}"/>
                                </h:outputLink>
                            </ui:panelGroup>
                            
                        </ui:panelGroup>
                        
                    </div>
                </div>
            </div>  
        </ui:form>
            </gui:define>
        </gui:composition>
    </body>
</html>
