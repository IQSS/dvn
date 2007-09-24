<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles"
                        xmlns:c="http://java.sun.com/jsp/jstl/core">
                            
<f:subview id="SiteMapPageView">

        
<ui:form  id="SiteMapForm">  
 <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
    
  <div class="dvn_section">
        <div class="dvn_sectionTitle">
          
                <h:outputText value="#{bundle.sitemapHeading}"/>
          
        </div>            
        <div class="dvn_sectionBox"> 
                <div class="dvn_margin12">
                    
                    <h:panelGrid  cellpadding="0" cellspacing="0" columnClasses="vdcTwoColLayout, vdcTwoColLayout" columns="2" id="homeGrid" width="100%">
                        
                        <ui:panelLayout id="sitemapColumn1" panelLayout="flow" styleClass="vdcSectionTwoCol" style="padding-left: 60px; padding-top: 20px; padding-bottom: 30px; padding-right: 80px;">
                            <!-- sitemapHomeHeading -->
                            <ui:panelGroup block="true" id="groupPanel0">
                                <ui:panelGroup  block="true" style="padding-top: 20px; padding-bottom: 15px">
                                    <h:outputText  id="homeHeading" styleClass="vdcSubHeader" value="#{bundle.sitemapBrowseHeading}"/>
                                </ui:panelGroup>
                                <ui:panelGroup  block="true" id="groupPanel2" separator="&lt;br /&gt;&lt;br /&gt;" style="padding-left: 10px">
                                    <h:outputLink id="basicSearchLink"  styleClass="vdcSiteMapLink" value="/dvn#{VDCRequest.currentVDCURL}#search">
                                        <h:outputText  id="basicSearchText" value="#{bundle.sitemapSearchLink}"/>
                                    </h:outputLink>
                                    <h:outputLink id="advSearchLink3" styleClass="vdcSiteMapLink" value="/dvn#{VDCRequest.currentVDCURL}/faces/AdvSearchPage.jsp#advancedSearch">
                                        <h:outputText  id="advSearchText" value="#{bundle.sitemapAdvancedSearchLink}"/>
                                    </h:outputLink>
                                    <h:outputLink id="browseCollectionsLink" styleClass="vdcSiteMapLink" value="/dvn#{VDCRequest.currentVDCURL}/faces/HomePage.jsp#browse">
                                        <h:outputText  id="browseCollectionsText" value="#{bundle.sitemapBrowseCollectionsLink}"/>
                                    </h:outputLink>
                                    <h:outputLink rendered="#{VDCRequest.currentVDCId != null}" id="MostRecentLink" styleClass="vdcSiteMapLink" value="/dvn#{VDCRequest.currentVDCURL}/faces/SearchPage.jsp?mode=4&amp;numResults=100#recent">
                                        <h:outputText  id="MostRecentText" value="#{bundle.sitemapMostRecentLink}"/>
                                    </h:outputLink>
                                </ui:panelGroup>
                            </ui:panelGroup>
                            
                            <!-- sitemapAccountHeading -->
                            <ui:panelGroup block="true">
                                <ui:panelGroup  block="true" id="groupPanel7" style="padding-top: 20px; padding-bottom: 15px">
                                    <h:outputText  id="accountHeading" styleClass="vdcSubHeader" value="#{bundle.sitemapAccountHeading}"/>
                                </ui:panelGroup>
                                <ui:panelGroup block="true" id="groupPanel8" style="padding-left: 10px">
                                    <h:outputLink id="sitemaplink8" styleClass="vdcSiteMapLink" value="/dvn#{VDCRequest.currentVDCURL}/faces/login/LoginPage.jsp#login">
                                        <h:outputText  id="sitemaplink8Text" value="#{bundle.sitemapLoginLink}"/>
                                    </h:outputLink>
                                    <!-- another work-around using f:verbatim because jsf renders double breaks in separator based on the
                              pre-rendered code. Therefore, if I don't render a link, there is a big whole in the UI -->
                                    <f:verbatim rendered="#{VDCRequest.currentVDCId != null}"><br /><br /></f:verbatim>
                                    <h:outputLink id="contributorRequestAccountLink" rendered="#{VDCRequest.currentVDC != null and VDCSession.loginBean == null and HomePage.showRequestContributor}" styleClass="vdcSiteMapLink" value="/dvn#{VDCRequest.currentVDCURL}/faces/login/ContributorRequestAccountPage.jsp#request">
                                        <h:outputText id="contributorRequestAccountText" value="#{bundle.sitemapBecomeContributorLink}"/>
                                    </h:outputLink>
                                    <h:outputLink id="contributorRequestLink" rendered="#{VDCRequest.currentVDC != null and VDCSession.loginBean != null and HomePage.showRequestContributor}" styleClass="vdcSiteMapLink" value="/dvn#{VDCRequest.currentVDCURL}/faces/login/ContributorRequestPage.jsp#request">
                                        <h:outputText id="contributorRequestText" value="#{bundle.sitemapBecomeContributorLink}"/>
                                    </h:outputLink>
                                    <f:verbatim rendered="#{VDCRequest.currentVDCId == null}"><br /><br /></f:verbatim>
                                    <h:outputLink rendered="#{VDCRequest.currentVDC == null and VDCSession.loginBean == null and HomePage.showRequestCreator}"  value="/dvn/faces/login/CreatorRequestAccountPage.jsp">
                                        <h:outputText   value="Create your own Dataverse."/>
                                    </h:outputLink>
                                    <h:outputLink rendered="#{VDCRequest.currentVDC == null and VDCSession.loginBean != null and HomePage.showRequestCreator}" value="/dvn/faces/login/CreatorRequestPage.jsp">
                                        <h:outputText value="Create your own Dataverse."/>
                                    </h:outputLink>
                                </ui:panelGroup>
                            </ui:panelGroup>
                       
                            <!-- sitemapAboutHeading -->
                            <ui:panelGroup block="true">
                                <ui:panelGroup  block="true" id="groupPanel9" style="padding-top: 20px; padding-bottom: 15px">
                                    <h:outputText  id="aboutHeading" styleClass="vdcSubHeader" value="#{bundle.sitemapAboutHeading}"/>
                                </ui:panelGroup>
                                <ui:panelGroup block="true" id="groupPanel10" style="padding-left: 10px">
                                    <h:outputLink rendered="#{VDCRequest.currentVDCId != null}" id="sitemaplink12" styleClass="vdcSiteMapLink" value="/dvn#{VDCRequest.currentVDCURL}/faces/AboutPage.jsp#about">
                                        <h:outputText  rendered="#{VDCRequest.currentVDCId != null}" id="sitemaplink12Text" value="#{bundle.sitemapAboutDataverseLink}"/>
                                    </h:outputLink>
                                    <!-- another work-around using f:verbatim because jsf renders double breaks in separator based on the
                              pre-rendered code. Therefore, if I don't render a link, there is a big whole in the UI -->
                                    <f:verbatim rendered="#{VDCRequest.currentVDCId != null}"><br /><br /></f:verbatim>
                                    <h:outputLink  id="sitemaplink13" styleClass="vdcSiteMapLink" value="/dvn#{VDCRequest.currentVDCURL}/faces/AboutPage.jsp#about">
                                        <h:outputText id="sitemaplink13Text" value="#{bundle.sitemapAboutNetworkLink}"/>
                                    </h:outputLink>
                                    <f:verbatim><br /><br /></f:verbatim>
                                    <f:verbatim>
                                        <!-- note this is a workaround to fix an issue that was caused by the 3rd nested panelGroup that
                                      paired the You are here text with the link previously. This caused the renderer to start outputting
                                      closing span tags at the end of this section when closing div tags were required. -->
                                        <span>
                                            <h:outputLink id="sitemaplink14" styleClass="vdcSiteMapLink" value="/dvn#{VDCRequest.currentVDCURL}/faces/SiteMapPage.jsp">
                                                <h:outputText  id="sitemaplink14Text" value="#{bundle.sitemapSitemapLink}"/>
                                            </h:outputLink>
                                            &amp;nbsp;&amp;nbsp;
                                            <h:outputText id="aboutHere" style="display:inline;" value="#{bundle.sitemapHereMsg}"/>
                                        </span>
                                    </f:verbatim>
                                    <f:verbatim><br /><br /></f:verbatim>
                                    <h:outputLink id="sitemaplink15" styleClass="vdcSiteMapLink" value="/dvn#{VDCRequest.currentVDCURL}/faces/ContactUsPage.jsp#contact">
                                        <h:outputText  id="sitemaplink15Text" value="#{bundle.sitemapContactUsLink}"/>
                                    </h:outputLink>
                                    <f:verbatim><br /><br /></f:verbatim>
                                    <h:outputLink rendered="#{((VDCRequest.vdcNetwork.displayAnnouncements == true) and (VDCRequest.currentVDC == null)) or ((VDCRequest.currentVDC.displayNetworkAnnouncements and VDCRequest.vdcNetwork.displayAnnouncements) and (VDCRequest.currentVDC != null))}" 
                                                  id="sitemaplink4" 
                                                  styleClass="vdcSiteMapLink" 
                                                  value="/dvn#{VDCRequest.currentVDCURL}/faces/AnnouncementsPage.jsp#announcements">
                                        <h:outputText rendered="#{((VDCRequest.vdcNetwork.displayAnnouncements == true) and (VDCRequest.currentVDC == null)) or ((VDCRequest.currentVDC.displayNetworkAnnouncements and VDCRequest.vdcNetwork.displayAnnouncements) and (VDCRequest.currentVDC != null))}" id="sitemaplink4Text" value="#{bundle.sitemapNetworkAnnouncementsLink}"/>
                                    </h:outputLink>
                                    <f:verbatim rendered="#{VDCRequest.currentVDC != null and VDCRequest.currentVDC.displayAnnouncements == true and VDCRequest.currentVDC.displayNetworkAnnouncements == true}"><br /><br /></f:verbatim>
                                    <h:outputLink rendered="#{VDCRequest.currentVDC != null and VDCRequest.currentVDC.displayAnnouncements == true}" id="sitemaplink5" styleClass="vdcSiteMapLink" value="/dvn#{VDCRequest.currentVDCURL}/faces/AnnouncementsPage.jsp#announcements">
                                        <h:outputText id="sitemaplink5Text" value="#{bundle.sitemapLocalAnnouncementsLink}"/>
                                    </h:outputLink>
                                </ui:panelGroup>
                            </ui:panelGroup>
                            
                            <!-- HELP -->
                            <ui:panelGroup block="true">
                                <ui:panelGroup  block="true" id="groupPanelHelp" style="padding-top: 20px; padding-bottom: 15px">
                                    <h:outputText  id="helpHeading" styleClass="vdcSubHeader" value="#{bundle.sitemapHelpHeading}"/>
                                </ui:panelGroup>
                                <ui:panelGroup  block="true" id="groupPanelHelpLinks" style="padding-left: 10px;">
                                    <h:outputLink id="sitemaplinkUserManual" styleClass="vdcSiteMapLink" target="_blank" value="http://thedata.org/help">
                                        <h:outputText  id="sitemaplinkUserManualText" value="#{bundle.sitemapUserManualLink}"/>
                                    </h:outputLink>
                                    <f:verbatim><br /><br /></f:verbatim>
                                    <!--  Search Tips converted to Search Help-->
                                    <h:outputLink value="http://thedata.org/help/browsesearch" styleClass="vdcSiteMapLink" target="_blank">
                                        <h:outputText  value="#{bundle.sitemapSearchTipsLink}"/>
                                    </h:outputLink>
                                </ui:panelGroup>
                            </ui:panelGroup>
                            
                        </ui:panelLayout>
                        
                    </h:panelGrid>
                    
                    
                </div>
            </div>
        </div>  
    </ui:form>
  </f:subview>
</jsp:root>
