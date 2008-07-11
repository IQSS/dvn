<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:gui="http://java.sun.com/jsf/facelets"
      xmlns:h="http://java.sun.com/jsf/html"
      xmlns:f="http://java.sun.com/jsf/core" 
      xmlns:jsp="http://java.sun.com/JSP/Page"
      xmlns:ui="http://www.sun.com/web/ui"
      xmlns:dvn="/WEB-INF/tlds/dvn-components"
      xmlns:a4j="https://ajax4jsf.dev.java.net/ajax">
<head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>

</head>

<body>
<gui:composition template="/template.xhtml">

<gui:param name="pageTitle" value="DVN - Home" />

  <gui:define name="body">
   

       <h:form id="form1">
           <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
           <!-- Success Message -->
         <ui:panelLayout styleClass="#{HomePage.msg.styleClass}" rendered="#{!empty HomePage.msg.messageText}">
           <h:outputText id="statusMessage" escape="false" value="#{HomePage.msg.messageText}" />
         </ui:panelLayout>
       
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
                  <h:outputLink styleClass="requestHmpgSideLink"  value="/dvn/faces/login/CreatorRequestInfoPage.jsp">
                           <h:outputText value="Create your own Dataverse" escape="false"/>
                  </h:outputLink>
                 
              </ui:panelGroup>
              
               <ui:panelGroup id="beContributor" block="true" styleClass="requestHmpgSide" rendered="#{HomePage.showRequestContributor}">
                    <h:outputLink styleClass="requestHmpgSideLink"  value="/dvn/dv/#{VDCRequest.currentVDC.alias}/faces/login/ContributorRequestInfoPage.jsp">
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
                                <f:selectItem itemLabel="- Global ID" itemValue="globalId" />
                                <f:selectItem itemLabel="Variable Information" itemValue="variable" />
                            </h:selectOneMenu>
                        </label>
                        <label for="search">
                            <h:inputText onkeypress="if (window.event) return processEvent('', 'form1:searchButton'); else return processEvent(event, 'form1:searchButton');" id="textField2" value="#{HomePage.searchValue}"/>
                        </label>
                        <label for="button">
                            <h:commandButton id="searchButton" value="Go" type="submit" action="#{HomePage.search_action}"/>                        
                        </label>
                    </fieldset>
                    <div class="dvn_searchLinks">
                        <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/AdvSearchPage.jsp">
                            <h:outputText value="Advanced Search"/>
                        </h:outputLink>
                        &#160; | &#160;
                        <h:outputLink value="http://thedata.org/guides/users/search/tips" target="_blank" style="margin: 0;">
                            <h:outputText value="Tips"/>
                        </h:outputLink>
                    </div>
                </div>
            </div>
            <!-- Search Section ends here -->
    
            <!-- Start Browse section (with side panels, if available) -->
            <div id="dvn_hmpgMainSection">
                  <!-- Network Homepage -->
                    <!-- datalist component for NETWORK HOME PAGE -->
                    <ui:panelLayout rendered="#{VDCRequest.currentVDC == null}">
                        <div class="dvn_Totals">
                            <h:outputText escape="false" value="#{HomePage.networkData}"/>
                        </div>
                   
                     <a4j:region id="ajaxRegionBak" renderRegionOnly="true">
                            <h:messages layout="table"/>
                            <a4j:outputPanel id="dataMapOutput" layout="block" ajaxRendered="true">
                                    <dvn:datalist binding="#{HomePage.dataList}" 
                                                    rendered="#{VDCRequest.currentVDC == null}"
                                                    idName="#{HomePage.dataMapId}"/>
                            </a4j:outputPanel> 
                       </a4j:region>
                    </ui:panelLayout>
                    
                  
                  <!-- Dataverse Homepage -->
                    <!-- Display Tree at dataverse level -->
                    <ui:tree binding="#{HomePage.collectionTree}" id="collectionTree" text="" rendered="#{VDCRequest.currentVDC != null}"/>
            </div>

      </div>
      <!-- Main Section ends here -->
    
        </h:form>
            </gui:define>
        </gui:composition>
    </body>
</html>