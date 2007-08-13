<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
    <f:subview id="homePageView">
       <h:form  id="form1">
           <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
           <!-- Success Message -->
         <ui:panelGroup styleClass="#{HomePage.msg.styleClass}" rendered="#{!empty HomePage.msg.messageText}">
           <h:outputText id="statusMessage" escape="false" value="#{HomePage.msg.messageText}" />
         </ui:panelGroup>
         
         
      <!-- Search Section starts here -->                                       
      <div class="dvn_section">        
        <div class="dvn_sectionTitle">
            <h3><em>Search</em> 
                <span>
                    <h:outputLink  value="/dvn#{VDCRequest.currentVDCURL}/faces/AdvSearchPage.jsp">
                        <b><h:outputText  value="Advanced Search"/></b>
                    </h:outputLink>
                    <h:outputText  value="&#160;" escape="false"/>
                    <h:outputLink value="http://thedata.org/help/browsesearch" target="_blank">
                        <h:outputText  value="Search Help"/>
                    </h:outputLink>                
                </span>
            </h3>
        </div>        
        <div class="dvn_searchBox">
            <div class="dvn_searchBoxContent">
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
                    <h:commandButton id="searchButton" value="Search" type="submit" action="#{HomePage.search_action}"  styleClass="button"/>                        
            </div>
            <span class="dvn_xbottom"><span class="dvn_xb4"><h:outputText  value="&#160;" escape="false"/></span><span class="dvn_xb3"><h:outputText  value="&#160;" escape="false"/></span><span class="dvn_xb2"><h:outputText  value="&#160;" escape="false"/></span><span class="dvn_xb1"><h:outputText  value="&#160;" escape="false"/></span></span>
        </div> 
    </div>
    <!-- Search Section ends here -->  
    
    <!-- Start Browse section (with side panels, if available) -->
  
    <div class="dvn_section">    
        
       <ui:panelGroup block="true" styleClass="#{ (HomePage.showRequestCreator or HomePage.showRequestContributor or VDCRequest.currentVDC.displayNewStudies == true or (VDCRequest.vdcNetwork.displayAnnouncements == true and VDCRequest.currentVDC == null) or ( (VDCRequest.currentVDC.displayNetworkAnnouncements and VDCRequest.vdcNetwork.displayAnnouncements) and VDCRequest.currentVDC != null) or VDCRequest.currentVDC.displayAnnouncements == true) ? 'dvn_sectionContent' : 'dvn_sectionContentNoPanels'}">    
            <div class="dvn_sectionTitle">
                <h3>Browse</h3>
            </div>            
            <div class="dvn_sectionBox dvn_pad12">    
                <h:outputText value="There are no Dataverses yet in #{VDCRequest.vdcNetwork.name} Dataverse Network." rendered="#{empty HomePage.vdcs}"/>

                <!-- Display dataverses at network Level -->
                <h:panelGrid binding="#{HomePage.mainDataTable}" rendered="#{VDCRequest.currentVDC == null}">
                </h:panelGrid>

                <!-- Display Tree at dataverse level -->
                <ui:tree  binding="#{HomePage.collectionTree}" id="collectionTree" text="" rendered="#{VDCRequest.currentVDC != null}"/>
            </div>   
        </ui:panelGroup>
        
        <ui:panelGroup block="true" styleClass="dvn_sectionPanels" rendered="#{HomePage.showRequestCreator or HomePage.showRequestContributor or VDCRequest.currentVDC.displayNewStudies == true or (VDCRequest.vdcNetwork.displayAnnouncements == true and VDCRequest.currentVDC == null) or ( (VDCRequest.currentVDC.displayNetworkAnnouncements and VDCRequest.vdcNetwork.displayAnnouncements) and VDCRequest.currentVDC != null) or VDCRequest.currentVDC.displayAnnouncements == true}">    
            <ui:panelGroup  id="createDataverse" block="true" rendered="#{HomePage.showRequestCreator}">   
                <h2>Create Your Own Dataverse</h2>
                <div class="dvn_sectionPanelText">
                    Are you interested in having your own on-line data archive? Send a request to
                    <h:outputLink rendered="#{VDCSession.loginBean==null}"  value="/dvn/faces/login/CreatorRequestAccountPage.jsp">
                        <h:outputText   value="Create your own Dataverse."/>
                    </h:outputLink>
                    <h:outputLink rendered="#{VDCSession.loginBean!=null}" value="/dvn/faces/login/CreatorRequestPage.jsp">
                        <h:outputText value="Create your own Dataverse."/>
                    </h:outputLink>
                    Or find out more about 
                    <a href="http://thedata.org/help/createdataverse" target="_blank">how to create and use a dataverse</a>
                    before sending a request.  
                </div>     
            </ui:panelGroup>
            
            <ui:panelGroup id="beContributor" block="true" rendered="#{HomePage.showRequestContributor}">
                <h2>Become a Contributor</h2>
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
                    <h:outputText  value=" Or find out more about "/>
                    <h:outputLink value="http://thedata.org/help/contributor" target="_blank">
                        <h:outputText value="being a contributor" /> 
                    </h:outputLink>  
                    <h:outputText value=" before sending a request."/>
                </div>
            </ui:panelGroup>
            
            <ui:panelGroup id="recentPanel" block="true" rendered="#{VDCRequest.currentVDC.displayNewStudies == true}" >
                <h2>Most Recent</h2>  
                <ui:panelGroup  id="noRecentStudiesPanel" block="true" rendered="#{empty HomePage.recentStudies}" styleClass="dvn_sectionPanelText">
                    <h:outputText value="There are no studies released in #{VDCRequest.currentVDC.name} Dataverse." styleClass="warnMessage"/>
                </ui:panelGroup> 
                <ui:panelGroup  id="yesRecentStudiesPanel" block="true" rendered="#{!empty HomePage.recentStudies}" styleClass="dvn_sectionPanelText">
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
                        <h:outputText  id="outputText11" value="more &gt;&gt;" escape="false"/>
                    </h:outputLink>   
                </ui:panelGroup>
            </ui:panelGroup>
       
           <ui:panelGroup block="true" rendered="#{( (VDCRequest.vdcNetwork.displayAnnouncements == true) and (VDCRequest.currentVDC == null) ) or ( (VDCRequest.currentVDC.displayNetworkAnnouncements and VDCRequest.vdcNetwork.displayAnnouncements) and (VDCRequest.currentVDC != null) )}" id="networkAnnouncementsHeaderPanel" >
                <h2><h:outputText value="#{bundle.networkAnnouncementsHeading}"/></h2>
                <div class="dvn_sectionPanelText">
                    <h:outputText id="networkAnnouncementsMessages" escape="false" value="#{HomePage.parsedNetworkAnnouncements}"/>
                </div>
            </ui:panelGroup>
            
            <ui:panelGroup rendered="#{VDCRequest.currentVDC.displayAnnouncements == true}" id="announcementsHeaderPanel" block="true">
                <h2><h:outputText value="#{bundle.localAnnouncementsHeading}"/></h2>
                <div class="dvn_sectionPanelText">
                    <h:outputText rendered="#{VDCRequest.currentVDC.displayAnnouncements == true}" id="localAnnouncementsMessages" escape="false" value="#{HomePage.parsedLocalAnnouncements}"/>
                </div>
            </ui:panelGroup>
               
        </ui:panelGroup>        
    </div>                                             
                                    
        </h:form>
    </f:subview>
</jsp:root>
