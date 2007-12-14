<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles"
                        xmlns:dmap="/WEB-INF/tlds/DataList"
                        >
                            
    <f:subview id="homePageView">
       <h:form id="form1">
           <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
           <!-- Success Message -->
         <ui:panelGroup styleClass="#{HomePage.msg.styleClass}" rendered="#{!empty HomePage.msg.messageText}">
           <h:outputText id="statusMessage" escape="false" value="#{HomePage.msg.messageText}" />
         </ui:panelGroup>
       
           <ui:panelGroup styleClass="dvn_hmpgMainMessage" block="true" rendered="#{(VDCRequest.vdcNetwork.displayAnnouncements == true) and (VDCRequest.currentVDC == null) }" id="networkAnnouncementsHeaderPanel" >
                <h:outputText id="networkAnnouncementsMessages" escape="false" value="#{HomePage.parsedNetworkAnnouncements}"/>
            </ui:panelGroup>
            
            <ui:panelGroup styleClass="dvn_hmpgMainMessage" rendered="#{VDCRequest.currentVDC.displayAnnouncements == true}" id="announcementsHeaderPanel" block="true">
                <h:outputText rendered="#{VDCRequest.currentVDC.displayAnnouncements == true}" escape="false" value="#{HomePage.parsedLocalAnnouncements}"/>
            </ui:panelGroup>
      
         <!-- Main Section starts here -->
         <div id="dvn_mainSection">
              <div id="dvn_mainSectionTitle">
              <h:outputText value="Dataverses" rendered="#{VDCRequest.currentVDC == null}"/>
              <h:outputText value="Studies" rendered="#{VDCRequest.currentVDC != null}" />
              </div>
      
              <ui:panelGroup id="createDataverse" block="true" styleClass="requestHmpgSide" rendered="#{HomePage.showRequestCreator}">
                  <h:outputLink styleClass="requestHmpgSideLink" rendered="#{VDCSession.loginBean==null}" value="/dvn/faces/login/CreatorRequestAccountPage.jsp">
                                <h:outputText value="Create your own Dataverse" escape="false"/>
                  </h:outputLink>
                  <h:outputLink styleClass="requestHmpgSideLink" rendered="#{VDCSession.loginBean!=null}" value="/dvn/faces/login/CreatorRequestPage.jsp">
                                <h:outputText value="Create your own Dataverse" escape="false"/>
                  </h:outputLink>
              </ui:panelGroup>
              
               <ui:panelGroup id="beContributor" block="true" styleClass="requestHmpgSide" rendered="#{HomePage.showRequestContributor}">
                    <h:outputLink styleClass="requestHmpgSideLink" rendered="#{VDCSession.loginBean==null}" value="/dvn/dv/#{VDCRequest.currentVDC.alias}/faces/login/ContributorRequestAccountPage.jsp">
                        <h:outputText value="Become a Contributor" escape="false"/>
                    </h:outputLink>
                    <h:outputLink styleClass="requestHmpgSideLink" rendered="#{VDCSession.loginBean!=null}" value="/dvn/dv/#{VDCRequest.currentVDC.alias}/faces/login/ContributorRequestPage.jsp">
                        <h:outputText value="Become a Contributor" escape="false"/>
                    </h:outputLink>
              </ui:panelGroup>

              <!-- Search Section starts here -->                                       
              <div class="dvn_searchblock">
                    <div class="dvn_searchBoxContent">
                    <div class="dvn_searchTitle"><a name="search" title="">Search</a></div>
                    <fieldset>
                        <label for="options">
                            <h:selectOneMenu id="dropdown1" value="#{HomePage.searchField}">
                                <f:selectItem itemLabel="Cataloging Information" itemValue="any" />
                                <f:selectItem itemLabel="- Author" itemValue="authorName" />
                                <f:selectItem itemLabel="- Title" itemValue="title" />
                                <f:selectItem itemLabel="- Study ID" itemValue="studyId" />
                                <f:selectItem itemLabel="Variable Information" itemValue="variable" />
                            </h:selectOneMenu>
                        </label>
                        <label for="search">
                            <span>for -</span><h:inputText onkeypress="if (window.event) return processEvent('', 'content:homePageView:form1:searchButton'); else return processEvent(event, 'content:homePageView:form1:searchButton');" id="textField2" value="#{HomePage.searchValue}"/>
                        </label>
                        <label for="button">
                            <h:commandButton id="searchButton" value="Search" type="submit" action="#{HomePage.search_action}"/>                        
                        </label>
                    </fieldset>
                    <div class="dvn_searchLinks">
                        <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/AdvSearchPage.jsp">
                            <h:outputText value="Advanced Search"/>
                        </h:outputLink>
                    </div>
                </div>
            </div>
            <!-- Search Section ends here -->
    
            <!-- Start Browse section (with side panels, if available) -->
            <div id="dvn_hmpgMainSection">
                <div class="dvn_margin12">
                  <!-- Network Homepage -->
                    <!-- datalist component for NETWORK HOME PAGE -->
                    <h:panelGrid rendered="#{VDCRequest.currentVDC == null}" style="width:98%;">
                        <h:panelGrid>
                            <h:outputText escape="false" value="#{HomePage.networkData}"/>
                        </h:panelGrid>
                        <dmap:datalist contents="#{HomePage.dataMap}" tabs="#{HomePage.tabsMap}" tab="#{HomePage.selectedTab != null ? HomePage.selectedTab : requestScope.tab}" id="dataMap" />
                    </h:panelGrid>
                  <!-- Dataverse Hoempage -->
                    <ui:panelGroup id="recentPanel" block="true" rendered="#{VDCRequest.currentVDC.displayNewStudies == true}" >
                        <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/SearchPage.jsp?mode=4&amp;numResults=100"  id="recentStudy" styleClass="dvn_more">
                        <h:outputText  value="Most Recent" escape="false"/>
                        </h:outputLink>
                     </ui:panelGroup>
                    <!-- Display Tree at dataverse level -->
                    <ui:tree binding="#{HomePage.collectionTree}" id="collectionTree" text="" rendered="#{VDCRequest.currentVDC != null}"/>
                </div>
            </div>

      </div>
      <!-- Main Section ends here -->
    
        </h:form>
    </f:subview>
</jsp:root>
