<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
    <f:subview id="CreatorRequestPageView">
        <ui:form  id="creatorRequestForm">
          <input type="hidden" name="pageName" value="CreatorRequestPage"/>
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
            <h:inputHidden binding="#{CreatorRequestPage.hiddenUserId}" value="#{CreatorRequestPage.userId}"/>

            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                    <h3>
                        <h:outputText value="Request to Become a Dataverse Creator"/>   
                    </h3>
                </div>            
                <div class="dvn_sectionBox"> 
                    <div class="dvn_margin12">
                        
                        <ui:panelGroup block="true"  style="padding-top: 10px" rendered="#{ !CreatorRequestPage.alreadyRequested and !CreatorRequestPage.alreadyHasPrivileges}">
                            <!--h:selectBooleanCheckbox id="creatorCheckbox" value="#{CreatorRequestPage.creatorRequest}" /-->
                            <h:outputText value="I want to become a dataverse Creator. This will allow me to create my on-line archive in the #{VDCRequest.vdcNetwork.name} Dataverse Network."/> 
                        </ui:panelGroup>
                        <ui:panelGroup block="true"  style="padding-top: 10px" rendered="#{!CreatorRequestPage.alreadyRequested and !CreatorRequestPage.alreadyHasPrivileges}">
                            <h:commandButton value="Submit Request" action="#{CreatorRequestPage.generateRequest}"/>
                            <h:commandButton value="Cancel" action="home"/>
                        </ui:panelGroup>
                        <ui:panelGroup  block="true"  style="padding-top: 10px" rendered="#{CreatorRequestPage.alreadyRequested }">
                            <h:outputText value="You have already requested to become a dataverse Creator.  Please wait for approval from the administrator."/>
                            <h:outputLink value="/dvn/faces/HomePage.jsp">
                                <h:outputText value="Go to Dataverse Network Home Page"/>
                            </h:outputLink>
                            
                        </ui:panelGroup>
                        <ui:panelGroup  block="true"  style="padding-top: 10px" rendered="#{CreatorRequestPage.alreadyHasPrivileges}">
                            <h:outputText value="You already have privileges to be a dataverse Creator."/>
                            <h:outputLink value="/dvn/faces/HomePage.jsp">
                                <h:outputText value="Go to Dataverse Network Home Page"/>
                            </h:outputLink>
                        </ui:panelGroup>
                        
                    </div>
                </div>
            </div>
        </ui:form>
    </f:subview>
</jsp:root>
