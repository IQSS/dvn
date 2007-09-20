<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
<f:subview id="UnauthorizedPageView">
    <ui:form  id="contributorRequestForm">
       <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
       <h:inputHidden binding="#{ContributorRequestPage.hiddenUserId}" value="#{ContributorRequestPage.userId}"/>


        <div class="dvn_section">
            <div class="dvn_sectionTitle">
                <a name="request" title="">
                    <h:outputText value="Request to Become a Dataverse Contributor"/>
                </a>
            </div>            
            <div class="dvn_sectionBox">
                <div class="dvn_margin12">
                    
                    <ui:panelGroup  block="true"  style="padding-top: 10px" rendered="#{ContributorRequestPage.alreadyRequested}">
                        <h:outputText value="You have already requested to become a Contributor to #{VDCRequest.currentVDC.name} dataverse.  Please wait for approval from the administrator."/>
                        <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/HomePage.jsp">
                            <h:outputText value="Go to Dataverse Home Page"/>
                        </h:outputLink>
                    </ui:panelGroup>
                    
                    <ui:panelGroup rendered="#{!ContributorRequestPage.alreadyRequested}">
                        <ui:panelGroup block="true"  style="padding-top: 10px"  >
                            <!--h:selectBooleanCheckbox id="contributorCheckbox" value="#{!ContributorRequestPage.alreadyRequested}" /-->
                            <h:outputText value="I want to become a contributor to #{VDCRequest.currentVDC.name} dataverse. This will allow me to upload my own data in this archive."/>
                        </ui:panelGroup>
                        <ui:panelGroup block="true"  style="padding-top: 10px; text-align: center;"  >                                      
                            <h:commandButton value="Submit Request" action="#{ContributorRequestPage.generateRequest}"/>
                            <h:commandButton value="Cancel" action="home"/>
                        </ui:panelGroup>
                    </ui:panelGroup>
                    
                </div>
            </div>
        </div>
    </ui:form>
</f:subview>
</jsp:root>
