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
                                    <h:outputText  id="homeHeading" styleClass="vdcSubHeader" value="#{bundle.sitemapHomeHeading}"/>
                                </ui:panelGroup>
                                <ui:panelGroup  block="true" id="groupPanel2" separator="&lt;br /&gt;&lt;br /&gt;" style="padding-left: 10px">
                                    <h:outputLink id="sitemaplink1"  styleClass="vdcSiteMapLink" value="/dvn#{VDCRequest.currentVDCURL}">
                                        <h:outputText  id="sitemaplink1Text" value="#{bundle.sitemapSearchLink}"/>
                                    </h:outputLink>
                                    <!--  Search Tips converted to Search Help-->
                                    <h:outputLink value="http://thedata.org/help/browsesearch" styleClass="vdcSiteMapLink" target="_blank">
                                        <h:outputText  value="#{bundle.sitemapSearchTipsLink}"/>
                                    </h:outputLink>
                                    <h:outputLink id="sitemaplink3" styleClass="vdcSiteMapLink" value="/dvn#{VDCRequest.currentVDCURL}/faces/AdvSearchPage.jsp">
                                        <h:outputText  id="sitemaplink3Text" value="#{bundle.sitemapAdvancedSearchLink}"/>
                                    </h:outputLink>
                                </ui:panelGroup>
                            </ui:panelGroup>
                            
                            <!-- sitemapAnnouncementsHeading -->
                            <ui:panelGroup block="true">
                                <ui:panelGroup rendered="#{( (VDCRequest.currentVDC != null and (VDCRequest.currentVDC.displayAnnouncements or VDCRequest.currentVDC.displayNetworkAnnouncements)) or (VDCRequest.currentVDC == null and VDCRequest.vdcNetwork.displayAnnouncements) )}" block="true" id="groupPanel3" style="padding-top: 20px; padding-bottom: 15px">
                                    <h:outputText  id="announcementsHeading" styleClass="vdcSubHeader" value="#{bundle.sitemapAnnouncementsHeading}"/>
                                </ui:panelGroup>
                                <ui:panelGroup block="true" id="groupPanel4" style="padding-left: 10px">
                                    <h:outputLink rendered="#{((VDCRequest.vdcNetwork.displayAnnouncements == true) and (VDCRequest.currentVDC == null)) or ((VDCRequest.currentVDC.displayNetworkAnnouncements and VDCRequest.vdcNetwork.displayAnnouncements) and (VDCRequest.currentVDC != null))}" 
                                                  id="sitemaplink4" 
                                                  styleClass="vdcSiteMapLink" 
                                                  value="/dvn#{VDCRequest.currentVDCURL}/faces/AnnouncementsPage.jsp">
                                        <h:outputText rendered="#{((VDCRequest.vdcNetwork.displayAnnouncements == true) and (VDCRequest.currentVDC == null)) or ((VDCRequest.currentVDC.displayNetworkAnnouncements and VDCRequest.vdcNetwork.displayAnnouncements) and (VDCRequest.currentVDC != null))}" id="sitemaplink4Text" value="#{bundle.sitemapNetworkAnnouncementsLink}"/>
                                    </h:outputLink>
                                    <f:verbatim rendered="#{VDCRequest.currentVDC != null and VDCRequest.currentVDC.displayAnnouncements == true and VDCRequest.currentVDC.displayNetworkAnnouncements == true}"><br /><br /></f:verbatim>
                                    <h:outputLink rendered="#{VDCRequest.currentVDC != null and VDCRequest.currentVDC.displayAnnouncements == true}" id="sitemaplink5" styleClass="vdcSiteMapLink" value="/dvn#{VDCRequest.currentVDCURL}/faces/AnnouncementsPage.jsp">
                                        <h:outputText id="sitemaplink5Text" value="#{bundle.sitemapLocalAnnouncementsLink}"/>
                                    </h:outputLink>
                                </ui:panelGroup>
                            </ui:panelGroup>
                            
                            <!-- sitemapAccountHeading -->
                            <ui:panelGroup block="true">
                                <ui:panelGroup  block="true" id="groupPanel7" style="padding-top: 20px; padding-bottom: 15px">
                                    <h:outputText  id="accountHeading" styleClass="vdcSubHeader" value="#{bundle.sitemapAccountHeading}"/>
                                </ui:panelGroup>
                                <ui:panelGroup block="true" id="groupPanel8" separator="&lt;br /&gt;&lt;br /&gt;" style="padding-left: 10px">
                                    <h:outputLink id="sitemaplink8" styleClass="vdcSiteMapLink" value="/dvn#{VDCRequest.currentVDCURL}/faces/login/LoginPage.jsp">
                                        <h:outputText  id="sitemaplink8Text" value="#{bundle.sitemapLoginLink}"/>
                                    </h:outputLink>
                                    <h:outputLink  id="sitemaplink9" styleClass="vdcSiteMapLink" value="/dvn#{VDCRequest.currentVDCURL}/faces/login/AddAccountPage.jsp">
                                        <h:outputText id="sitemaplink9Text" value="#{bundle.sitemapCreateAccountLink}"/>
                                    </h:outputLink>
                                    <h:outputLink id="sitemaplink10" styleClass="vdcSiteMapLink" value="/dvn#{VDCRequest.currentVDCURL}/faces/login/ContributorRequestPage.jsp">
                                        <h:outputText  id="sitemaplink10Text" value="#{bundle.sitemapBecomeContributorLink}"/>
                                    </h:outputLink>
                                    <h:outputLink rendered="#{VDCSession.loginBean.networkAdmin == true or VDCSession.loginBean.networkCreator == true}" id="sitemaplink11" styleClass="vdcSiteMapLink" value="/dvn#{VDCRequest.currentVDCURL}/faces/site/AddSitePage.jsp">
                                        <h:outputText  id="sitemaplink11Text" value="#{bundle.sitemapCreateDataverseLink}"/>
                                    </h:outputLink>
                                </ui:panelGroup>
                            </ui:panelGroup>
                            
                            <!-- sitemapAboutHeading -->
                            <ui:panelGroup block="true">
                                <ui:panelGroup  block="true" id="groupPanel9" style="padding-top: 20px; padding-bottom: 15px">
                                    <h:outputText  id="aboutHeading" styleClass="vdcSubHeader" value="#{bundle.sitemapAboutHeading}"/>
                                </ui:panelGroup>
                                <ui:panelGroup block="true" id="groupPanel10" style="padding-left: 10px">
                                    <h:outputLink rendered="#{VDCRequest.currentVDCId != null}" id="sitemaplink12" styleClass="vdcSiteMapLink" value="/dvn#{VDCRequest.currentVDCURL}/faces/AboutPage.jsp">
                                        <h:outputText  rendered="#{VDCRequest.currentVDCId != null}" id="sitemaplink12Text" value="#{bundle.sitemapAboutDataverseLink}"/>
                                    </h:outputLink>
                                    <!-- another work-around using f:verbatim because jsf renders double breaks in separator based on the
                              pre-rendered code. Therefore, if I don't render a link, there is a big whole in the UI -->
                                    <f:verbatim rendered="#{VDCRequest.currentVDCId != null}"><br /><br /></f:verbatim>
                                    <h:outputLink  id="sitemaplink13" styleClass="vdcSiteMapLink" value="/dvn#{VDCRequest.currentVDCURL}/faces/AboutPage.jsp">
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
                                    <h:outputLink id="sitemaplink15" styleClass="vdcSiteMapLink" value="/dvn#{VDCRequest.currentVDCURL}/faces/ContactUsPage.jsp">
                                        <h:outputText  id="sitemaplink15Text" value="#{bundle.sitemapContactUsLink}"/>
                                    </h:outputLink>
                                </ui:panelGroup>
                            </ui:panelGroup>
                            
                            <!-- HELP -->
                            <ui:panelGroup block="true">
                                <ui:panelGroup  block="true" id="groupPanelHelp" style="padding-top: 20px; padding-bottom: 15px">
                                    <h:outputText  id="helpHeading" styleClass="vdcSubHeader" value="#{bundle.sitemapHelpHeading}"/>
                                </ui:panelGroup>
                                <ui:panelGroup  block="true" id="groupPanelHelpLinks" style="padding-left: 10px;">
                                    <h:outputLink id="sitemaplinkUserManual" styleClass="vdcSiteMapLink" value="/dvn#{VDCRequest.currentVDCURL}/help/Manual.doc">
                                        <h:outputText  id="sitemaplinkUserManualText" value="#{bundle.sitemapUserManualLink}"/>
                                    </h:outputLink>
                                </ui:panelGroup>
                            </ui:panelGroup>
                            
                            
                        </ui:panelLayout>
                        
                        <!-- COLUMN 2 -->
                        <ui:panelLayout id="sitemapColumn2" panelLayout="flow" styleClass="vdcSectionTwoCol" style="padding-top: 20px; padding-bottom: 30px; padding-right: 80px;">
                            <!-- sitemapCollectionsHeading -->
                            <ui:panelGroup block="true" id="siteMapCollectionsGroup">
                                <ui:panelGroup  block="true" id="groupPanel5" style="padding-top: 20px; padding-bottom: 15px">
                                    <h:outputText  id="collectionsHeading" styleClass="vdcSubHeader" value="#{bundle.sitemapCollectionsHeading}"/>
                                </ui:panelGroup>
                                
                                <ui:panelLayout  id="browsePanel" panelLayout="flow" style="margin: 0px; padding: 0px 10px 10px 10px; height: 100%; width: 100%">
                                    <ui:tree  binding="#{CollectionTreeBuilder.collectionTree}" id="collectionTree" style="width: 80%" text="" />
                                </ui:panelLayout>
                                
                                <f:verbatim><br/></f:verbatim>
                                <h:outputLink rendered="#{(VDCRequest.currentVDCId != null and VDCRequest.currentVDC.displayNewStudies == true)}"  id="sitemaplink7" styleClass="vdcSiteMapLink" value="/dvn#{VDCRequest.currentVDCURL}/faces/HomePage.jsp">
                                    <h:outputText rendered="#{(VDCRequest.currentVDCId != null and VDCRequest.currentVDC.displayNewStudies == true)}" id="sitemaplink7Text" value="#{bundle.sitemapMostRecentLink}"/>
                                </h:outputLink>
                                
                                
                            </ui:panelGroup>
                        </ui:panelLayout>
                        
                    </h:panelGrid>
                    
                    
                </div>
            </div>
        </div>  
    </ui:form>
  </f:subview>
</jsp:root>
