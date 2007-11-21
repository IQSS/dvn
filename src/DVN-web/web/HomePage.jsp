<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles"
                        xmlns:dmap="/WEB-INF/tlds/DataList">
                            
    <f:subview id="homePageView">
       <h:form  id="form1">
           <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
           <!-- Success Message -->
         <ui:panelGroup styleClass="#{HomePage.msg.styleClass}" rendered="#{!empty HomePage.msg.messageText}">
           <h:outputText id="statusMessage" escape="false" value="#{HomePage.msg.messageText}" />
         </ui:panelGroup>
         
         
      <!-- Search Section starts here -->                                       
      <div class="dvn_searchblock">        
                <span class="dvn_sectionTitleL"><a name="search" title="">Search</a></span> 
                <span class="dvn_sectionTitleR">
                    <h:outputLink  value="/dvn#{VDCRequest.currentVDCURL}/faces/AdvSearchPage.jsp">
                        <h:outputText  value="Advanced Search"/>
                    </h:outputLink>
                    <h:outputText  value="&#160;" escape="false"/>
                    <h:outputLink value="http://thedata.org/help/browsesearch" target="_blank">
                        <h:outputText  value="Search Help"/>
                    </h:outputLink>                
                </span>
            <div class="dvn_searchBoxContent">
                    
                    <fieldset>
                        <label for="options">
                            <span>
                                <h:outputText value="Search all public dataverses -" rendered="#{VDCRequest.currentVDC == null}"/>
                                <h:outputText value="Search this dataverse -" rendered="#{VDCRequest.currentVDC != null}" />
                            </span>
                            <h:selectOneMenu  id="dropdown1" value="#{HomePage.searchField}">
                                <f:selectItem   itemLabel="Cataloging Information" itemValue="any" />
                                <f:selectItem   itemLabel="- Author" itemValue="authorName" />
                                <f:selectItem   itemLabel="- Title" itemValue="title" />
                                <f:selectItem   itemLabel="- Study ID" itemValue="studyId" />
                                <f:selectItem   itemLabel="Variable Information" itemValue="variable" />
                            </h:selectOneMenu>
                        </label>
                        
                        <label for="search">
                            <span>for -</span>
                            <h:inputText onkeypress="if (window.event) return processEvent('', 'content:homePageView:form1:searchButton'); else return processEvent(event, 'content:homePageView:form1:searchButton');" id="textField2" value="#{HomePage.searchValue}"/>
                        </label>  
                        <label for="button"><span><h:outputText  value="&#160;" escape="false"/></span>
                            <h:commandButton id="searchButton" value="Search" type="submit" action="#{HomePage.search_action}"  styleClass="dvn_button"/>                        
                        </label>
                    </fieldset>
                </div>
    </div>
    <!-- Search Section ends here -->  
    
    <!-- Start Browse section (with side panels, if available) -->
  
    <ui:panelGroup block="true" styleClass="#{ (HomePage.showRequestCreator or HomePage.showRequestContributor or VDCRequest.currentVDC.displayNewStudies == true or (VDCRequest.vdcNetwork.displayAnnouncements == true and VDCRequest.currentVDC == null) or ( (VDCRequest.currentVDC.displayNetworkAnnouncements and VDCRequest.vdcNetwork.displayAnnouncements) and VDCRequest.currentVDC != null) or VDCRequest.currentVDC.displayAnnouncements == true) ? 'dvn_section dvn_overflow' : 'dvn_section'}">       
        
       <ui:panelGroup block="true" styleClass="#{ (HomePage.showRequestCreator or HomePage.showRequestContributor or VDCRequest.currentVDC.displayNewStudies == true or (VDCRequest.vdcNetwork.displayAnnouncements == true and VDCRequest.currentVDC == null) or ( (VDCRequest.currentVDC.displayNetworkAnnouncements and VDCRequest.vdcNetwork.displayAnnouncements) and VDCRequest.currentVDC != null) or VDCRequest.currentVDC.displayAnnouncements == true) ? 'dvn_sectionContent' : 'dvn_sectionContentNoPanels'}">    
            <div class="dvn_sectionTitle">
                <a name="browse" title="">Browse</a>
            </div>            
            <div class="dvn_sectionBox">
                <div class="dvn_margin12">
                    <h:outputText value="There are no Dataverses yet in #{VDCRequest.vdcNetwork.name} Dataverse Network." rendered="#{empty HomePage.vdcs}"/>
                    <!-- datalist component for NETWORK HOME PAGE -->
                    <h:panelGrid rendered="#{VDCRequest.currentVDC == null}">
                        <dmap:datalist contents="#{HomePage.dataMap}" id="dataMap" />
                    </h:panelGrid>
                    <!-- Display Tree at dataverse level -->
                    <ui:tree  binding="#{HomePage.collectionTree}" id="collectionTree" text="" rendered="#{VDCRequest.currentVDC != null}"/>
                </div>
            </div>   
        </ui:panelGroup>
        
        <ui:panelGroup block="true" styleClass="dvn_sectionPanels" rendered="#{HomePage.showRequestCreator or HomePage.showRequestContributor or VDCRequest.currentVDC.displayNewStudies == true or (VDCRequest.vdcNetwork.displayAnnouncements == true and VDCRequest.currentVDC == null) or ( (VDCRequest.currentVDC.displayNetworkAnnouncements and VDCRequest.vdcNetwork.displayAnnouncements) and VDCRequest.currentVDC != null) or VDCRequest.currentVDC.displayAnnouncements == true}">    
            <ui:panelGroup id="createDataverse" block="true" styleClass="requestHmpgSide" rendered="#{HomePage.showRequestCreator}">   

                    <h:outputLink styleClass="requestHmpgSideLink" rendered="#{VDCSession.loginBean==null}" value="/dvn/faces/login/CreatorRequestAccountPage.jsp">
                        <h:outputText   value="Create your &lt;br /&gt; own Dataverse." escape="false"/>
                    </h:outputLink>
                    <h:outputLink styleClass="requestHmpgSideLink" rendered="#{VDCSession.loginBean!=null}" value="/dvn/faces/login/CreatorRequestPage.jsp">
                        <h:outputText value="Create your &lt;br /&gt; own Dataverse." escape="false"/>
                    </h:outputLink>
            </ui:panelGroup>
            
            <ui:panelGroup id="beContributor" block="true" rendered="#{HomePage.showRequestContributor}">
                <div class="dvn_sectionPanelTitle">Become a Contributor</div>
                <div class="dvn_sectionPanelText">
                    Are you interested in uploading your data to 
                    <h:outputText style="font-weight: bold;" value="#{VDCRequest.currentVDC.name}"/>
                    Dataverse? Send a request to
                    <h:outputLink rendered="#{VDCSession.loginBean==null}" value="/dvn/dv/#{VDCRequest.currentVDC.alias}/faces/login/ContributorRequestAccountPage.jsp">
                        <h:outputText value="Become a Contributor."/>
                    </h:outputLink>
                    <h:outputLink rendered="#{VDCSession.loginBean!=null}" value="/dvn/dv/#{VDCRequest.currentVDC.alias}/faces/login/ContributorRequestPage.jsp">
                        <h:outputText value="Become a Contributor."/>
                    </h:outputLink>
                   
                </div>
            </ui:panelGroup>
            
            <ui:panelGroup id="recentPanel" block="true" rendered="#{VDCRequest.currentVDC.displayNewStudies == true}" >
                <div class="dvn_sectionPanelTitle">Most Recent</div>  
                <ui:panelGroup  id="noRecentStudiesPanel" block="true" rendered="#{empty HomePage.recentStudies}" styleClass="dvn_sectionPanelText warnMessage" >
                    <h:outputText value="There are no studies released in #{VDCRequest.currentVDC.name} Dataverse." />
                </ui:panelGroup> 
                <ui:panelGroup  id="yesRecentStudiesPanel" block="true" rendered="#{!empty HomePage.recentStudies}" styleClass="dvn_sectionPanelText">
                    <h:dataTable  id="dataTable1" cellpadding="0" cellspacing="0" value="#{HomePage.recentStudies}" var="study">
                        
                        <h:column  id="column1" >
                            <ul><li>
                                    <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/study/StudyPage.jsp?studyId=#{study.id}"  id="recentStudy">
                                        <h:outputText  id="outputText11"  value="#{study.title}"/>
                                    </h:outputLink>
                                </li>
                            </ul>
                        </h:column>
                    </h:dataTable>
                    <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/SearchPage.jsp?mode=4&amp;numResults=100"  id="recentStudy" styleClass="dvn_more">
                        <h:outputText  value="more &gt;&gt;" escape="false"/>
                    </h:outputLink>   
                </ui:panelGroup>
            </ui:panelGroup>
       
           <ui:panelGroup block="true" rendered="#{( (VDCRequest.vdcNetwork.displayAnnouncements == true) and (VDCRequest.currentVDC == null) ) or ( (VDCRequest.currentVDC.displayNetworkAnnouncements and VDCRequest.vdcNetwork.displayAnnouncements) and (VDCRequest.currentVDC != null) )}" id="networkAnnouncementsHeaderPanel" >
                <div class="dvn_sectionPanelTitle"><h:outputText value="#{bundle.networkAnnouncementsHeading}"/></div>
                <div class="dvn_sectionPanelText">
                    <h:outputText id="networkAnnouncementsMessages" escape="false" value="#{HomePage.parsedNetworkAnnouncements}"/>
                </div>
            </ui:panelGroup>
            
            <ui:panelGroup rendered="#{VDCRequest.currentVDC.displayAnnouncements == true}" id="announcementsHeaderPanel" block="true">
                <div class="dvn_sectionPanelTitle"><h:outputText value="#{bundle.localAnnouncementsHeading}"/></div>
                <div class="dvn_sectionPanelText">
                    <h:outputText rendered="#{VDCRequest.currentVDC.displayAnnouncements == true}" escape="false" value="#{HomePage.parsedLocalAnnouncements}"/>
                </div>
            </ui:panelGroup>
               
        </ui:panelGroup>        
    </ui:panelGroup>                                             
                                    
        </h:form>
    </f:subview>
</jsp:root>
