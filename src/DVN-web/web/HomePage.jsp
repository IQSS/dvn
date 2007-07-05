<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
    <f:subview id="homePageView">
                   <h:form  id="form1">
                       <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
                       <ui:panelLayout panelLayout="flow" styleClass="vdcSectionMiddleMessage" style="width: 450px; margin-top: 10px; margin-bottom: -40px" rendered="#{!empty HomePage.msg.messageText}">
                         <ui:panelGroup block="true"  styleClass="#{HomePage.msg.styleClass}" >
                           <h:outputText id="statusMessage" escape="false" value="#{HomePage.msg.messageText}" />
                         </ui:panelGroup>
                       </ui:panelLayout> 
                       
                       <ui:panelLayout id="layoutPanelContent" panelLayout="flow" styleClass="vdcSectionMiddleNoBorder">        
                        <h:panelGrid  cellpadding="0" cellspacing="0" columnClasses="vdcContentCol1, vdcContentCol2" columns="2" id="homeGrid" width="100%">
                            <ui:panelLayout  id="homeCol1" panelLayout="flow" styleClass="vdcSectionCol">
                                
                                <!-- START SEARCH AREA -->
                                <ui:panelLayout  id="searchHeaderPanel" panelLayout="flow" styleClass="vdcSectionHeader">
                                    <h:outputText  value="Search"/>
                                </ui:panelLayout>
                                <ui:panelLayout  id="searchPanel" panelLayout="flow" styleClass="vdcSearchSection">
                                    <ui:panelGroup  block="true" id="groupPanel1" styleClass="vdcSearchGroupFirst">
                                        <h:outputLink id="searchAnchor">
                                            <h:outputText id="searchAnchorText" value=" "/>
                                        </h:outputLink>
                                        <h:outputLink  id="hyperlink3"  value="/dvn#{VDCRequest.currentVDCURL}/faces/AdvSearchPage.jsp">
                                            <h:outputText  id="hyperlink3Text" value="Advanced Search"/>
                                        </h:outputLink>
                                         <h:outputText  value=" | "/>
                                        <h:outputLink value="http://thedata.org/help/browsesearch" target="_blank">
                                            <h:outputText  value="Search Help"/>
                                        </h:outputLink>
                                       
                                    </ui:panelGroup>
                                    <ui:panelGroup  block="true" id="groupPanel2" styleClass="vdcSearchGroupLast">
                                         <h:outputText  id="outputText2a" value=" Search all public dataverses: " rendered="#{VDCRequest.currentVDC == null}"/>
                                         <h:outputText  id="outputText2b" value=" Search this dataverse: " rendered="#{VDCRequest.currentVDC != null}"/>
                                         <h:selectOneMenu  id="dropdown1" value="#{HomePage.searchField}">
                                                <f:selectItem   itemLabel="Cataloging Information" itemValue="any" />
                                                <f:selectItem   itemLabel="- Author" itemValue="authorName" />
                                                <f:selectItem   itemLabel="- Title" itemValue="title" />
                                                <f:selectItem   itemLabel="- Study ID" itemValue="studyId" />
                                                <f:selectItem   itemLabel="Variable Information" itemValue="variable" />
                                        </h:selectOneMenu>
                                        <h:outputText  id="outputText4"  value=" For: "/>
                                        <h:inputText onkeypress="if (window.event) return processEvent('', 'content:homePageView:form1:searchButton'); else return processEvent(event, 'content:homePageView:form1:searchButton');" id="textField2" value="#{HomePage.searchValue}"/>
                                        <h:commandButton id="searchButton" value="Search" type="submit" action="#{HomePage.search_action}"/>
                                    </ui:panelGroup>
                                </ui:panelLayout>
                                
                                <!-- END SEARCH AREA -->
                                
                                <ui:panelLayout id="collectionBlockPanel" panelLayout="flow">
                                    <ui:panelLayout  id="browseHeaderPanel" panelLayout="flow" styleClass="vdcSectionHeader">
                                        <h:outputText  value="Browse"/>
                                    </ui:panelLayout>
                                    <ui:panelLayout  id="noDataversePanel" panelLayout="flow" style="padding-top:30px; padding-bottom: 0px; padding-left:20px;" rendered="#{empty HomePage.vdcs}">
                                        <h:outputText styleClass="vdcTextStandOut" value="There are no Dataverses yet in #{VDCRequest.vdcNetwork.name} Dataverse Network."/>
                                    </ui:panelLayout>
                                    
                                    <!-- add the network home page here -->
                                    <h:panelGrid binding="#{HomePage.mainDataTable}" rendered="#{VDCRequest.currentVDC == null}">
                                      <!-- network collections fragment --> 
                                    </h:panelGrid>
                                    <!-- end network home page -->
                                    
                                    <ui:panelLayout id="browsePanel" panelLayout="flow" style="margin: 0px; padding: 30px 2px 30px 15px;" rendered="#{VDCRequest.currentVDC != null}">
                                        <h:panelGrid  cellpadding="0" cellspacing="0" columns="1"  width="100%"> 
                                            <ui:tree  binding="#{HomePage.collectionTree}" id="collectionTree"  text="" />
                                        </h:panelGrid>
                                    </ui:panelLayout>
                                </ui:panelLayout>                                 
                            </ui:panelLayout>
                            
                            <ui:panelGroup block="true" id="groupRightColumn" rendered="#{HomePage.showRequestCreator or HomePage.showRequestContributor or (VDCRequest.currentVDC != null and VDCRequest.currentVDC.displayNewStudies == true) or ((VDCRequest.currentVDC != null and VDCRequest.currentVDC.displayNewStudies == true) or( (VDCRequest.vdcNetwork.displayAnnouncements == true) and (VDCRequest.currentVDC == null) ) or ( (VDCRequest.currentVDC.displayNetworkAnnouncements == true) and (VDCRequest.currentVDC != null) ) or VDCRequest.currentVDC.displayAnnouncements == true)}">
                                
                            <ui:panelLayout  id="createDataverse" panelLayout="flow" style="margin-bottom: 30px" rendered="#{HomePage.showRequestCreator}">      
                                <ui:panelGroup  block="true" id="groupCreateDataverse" styleClass="vdcRequestPanelDataverse">
                                    <ui:panelGroup  block="true" styleClass="vdcRequestTitleDataverse">
                                        <h:outputText  value="Create Your Own Dataverse"/>
                                    </ui:panelGroup>  
                                    <h:outputText  value="Are you interested in having your on-line archive? Send a request to "/>
                                     <h:outputLink rendered="#{VDCSession.loginBean==null}"  styleClass="vdcRequestPanelLink" value="/dvn/faces/login/CreatorRequestAccountPage.jsp">
                                          <h:outputText  styleClass="vdcRequestPanelLink" value="Create your own Dataverse."/>
                                     </h:outputLink>
                                     <h:outputLink rendered="#{VDCSession.loginBean!=null}" styleClass="vdcRequestPanelLink" value="/dvn/faces/login/CreatorRequestPage.jsp">
                                          <h:outputText  styleClass="vdcRequestPanelLink" value="Create your own Dataverse."/>
                                     </h:outputLink>
                                    <h:outputText  value=" Or find out more about "/>
                                     <h:outputLink value="http://thedata.org/help/createdataverse" target="_blank">
                                        <h:outputText value="how to create and use a dataverse" /> 
                                     </h:outputLink>  
                                     <h:outputText value=" before sending a request."/>
                                </ui:panelGroup>
                            </ui:panelLayout>   
                            
                            <ui:panelLayout  id="beContributor" panelLayout="flow" style="margin-bottom: 30px" rendered="#{HomePage.showRequestContributor}">
                                <ui:panelGroup  block="true" id="groupBeContributor" styleClass="vdcRequestPanelContributor">
                                    <ui:panelGroup  block="true" styleClass="vdcRequestTitleContributor">
                                        <h:outputText  value="Become a Contributor"/>
                                    </ui:panelGroup>    
                                     <h:outputText value="Are you interested in uploading your data to  "/>
                                     <h:outputText style="font-weight: bold;" value="#{VDCRequest.currentVDC.name}"/>
                                     <h:outputText value=" Dataverse? Send a request to "/>
                                     <h:outputLink rendered="#{VDCSession.loginBean==null}"  styleClass="vdcRequestPanelLink" value="/dvn/dv/#{VDCRequest.currentVDC.alias}/faces/login/ContributorRequestAccountPage.jsp">
                                          <h:outputText  styleClass="vdcRequestPanelLink" value="Become a Contributor."/>
                                     </h:outputLink>
                                     <h:outputLink rendered="#{VDCSession.loginBean!=null}"  styleClass="vdcRequestPanelLink" value="/dvn/dv/#{VDCRequest.currentVDC.alias}/faces/login/ContributorRequestPage.jsp">
                                          <h:outputText   styleClass="vdcRequestPanelLink" value="Become a Contributor."/>
                                     </h:outputLink>
                                     <h:outputText  value=" Or find out more about "/>
                                     <h:outputLink value="http://thedata.org/help/contributor" target="_blank">
                                        <h:outputText value="being a contributor" /> 
                                     </h:outputLink>  
                                     <h:outputText value=" before sending a request."/>
                                </ui:panelGroup>
                            </ui:panelLayout>   
                                
                            <ui:panelLayout id="homeCol2" panelLayout="flow" styleClass="vdcSectionColRight" rendered="#{(VDCRequest.currentVDC != null and VDCRequest.currentVDC.displayNewStudies == true) or( (VDCRequest.vdcNetwork.displayAnnouncements == true) and (VDCRequest.currentVDC == null) ) or ( (VDCRequest.currentVDC.displayNetworkAnnouncements == true) and (VDCRequest.currentVDC != null) ) or VDCRequest.currentVDC.displayAnnouncements == true}">                                
                                <ui:panelLayout id="recentHeaderPanel" panelLayout="flow" styleClass="vdcSectionHeader" rendered="#{VDCRequest.currentVDC != null and VDCRequest.currentVDC.displayNewStudies == true}" >
                                    <h:outputText value="Most Recent"/>
                                </ui:panelLayout>
                                <ui:panelLayout id="recentPanel" panelLayout="flow"  styleClass="vdcOptionalPanel" rendered="#{VDCRequest.currentVDC != null and VDCRequest.currentVDC.displayNewStudies == true}">
                                   <ui:panelLayout  id="noRecentStudiesPanel" panelLayout="flow" rendered="#{empty HomePage.recentStudies}">
                                        <h:outputText value="There are no studies released in #{VDCRequest.currentVDC.name} Dataverse." styleClass="warnMessage"/>
                                    </ui:panelLayout> 
                                    <ui:panelLayout  id="yesRecentStudiesPanel" panelLayout="flow" rendered="#{!empty HomePage.recentStudies}">
                                       <h:dataTable  id="dataTable1" 
                                            value="#{HomePage.recentStudies}" var="study" width="100%" columnClasses="vdcMostRecentCol1, vdcMostRecentCol2">
                                                <h:column  id="column0">    
                                                    <h:graphicImage  id="image9" style="padding-left: 4px; padding-right: 4px" value="/resources/icon_bullet.gif"/> 
                                                </h:column>
                                            <h:column  id="column1" >
                                                <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/study/StudyPage.jsp?studyId=#{study.id}"  id="recentStudy">
                                                    <h:outputText  id="outputText11"  value="#{study.title}"/>
                                                </h:outputLink>                                            
                                            </h:column>
                                        </h:dataTable>
                                        <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/SearchPage.jsp?mode=4&amp;numResults=100"  id="recentStudy">
                                            <h:outputText  id="outputText11" value="more >>"/>
                                        </h:outputLink>   
                                   </ui:panelLayout>
                                </ui:panelLayout>
                                <ui:panelLayout rendered="#{( (VDCRequest.vdcNetwork.displayAnnouncements == true) and (VDCRequest.currentVDC == null) ) or ( (VDCRequest.currentVDC.displayNetworkAnnouncements and VDCRequest.vdcNetwork.displayAnnouncements) and (VDCRequest.currentVDC != null) )}" id="networkAnnouncementsHeaderPanel" panelLayout="flow" styleClass="vdcSectionHeader">
                                    <h:outputText value="#{bundle.networkAnnouncementsHeading}"/>
                                </ui:panelLayout>
                                <ui:panelLayout rendered="#{( (VDCRequest.vdcNetwork.displayAnnouncements == true) and (VDCRequest.currentVDC == null) ) or ( (VDCRequest.currentVDC.displayNetworkAnnouncements and VDCRequest.vdcNetwork.displayAnnouncements) and (VDCRequest.currentVDC != null) )}" id="networkAnnouncementsPanel" panelLayout="flow" styleClass="vdcOptionalPanel">
                                    <h:outputText id="networkAnnouncementsMessages" escape="false" value="#{HomePage.parsedNetworkAnnouncements}"/>
                                </ui:panelLayout>
                                <ui:panelLayout rendered="#{VDCRequest.currentVDC.displayAnnouncements == true}" id="announcementsHeaderPanel" panelLayout="flow" styleClass="vdcSectionHeader">
                                    <h:outputText value="#{bundle.localAnnouncementsHeading}"/>
                                </ui:panelLayout>
                                <ui:panelLayout rendered="#{VDCRequest.currentVDC.displayAnnouncements == true}" id="localAnnouncementsPanel" panelLayout="flow" styleClass="vdcOptionalPanel">
                                    <h:outputText rendered="#{VDCRequest.currentVDC.displayAnnouncements == true}" id="localAnnouncementsMessages" escape="false" value="#{HomePage.parsedLocalAnnouncements}"/>
                                </ui:panelLayout>
                            </ui:panelLayout>
                            </ui:panelGroup>
                        </h:panelGrid>
                        </ui:panelLayout>
                        
                    </h:form>
    </f:subview>
</jsp:root>
