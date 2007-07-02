<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
    xmlns:h="http://java.sun.com/jsf/html" 
    xmlns:jsp="http://java.sun.com/JSP/Page" 
    xmlns:ui="http://www.sun.com/web/ui"
    xmlns:tiles="http://struts.apache.org/tags-tiles"
    xmlns:c="http://java.sun.com/jsp/jstl/core">

    <f:subview id="addLinkPageView">
                    <ui:form  id="form1">
                        <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
                            <ui:panelLayout id="layoutPanel1" panelLayout="flow" styleClass="vdcSectionMiddle">
                                <ui:panelLayout  id="layoutPanel2" panelLayout="flow" styleClass="vdcSectionHeader">
                                    <h:outputText value="Add Collection Link"/>
                                </ui:panelLayout>
                                <ui:panelLayout  id="layoutPanel3" panelLayout="flow" style="padding-top: 30px; padding-bottom: 30px; padding-left: 50px; padding-right: 50px">
                                    
                                <ui:panelGroup  block="true" >                                             
                                    <h:outputText value="Select a collection from another (public) dataverse that you would like to add your dataverse (the collection you select will be displayed in the homepage of your dataverse and it will be included in your dataverse searches)."  />
                                </ui:panelGroup>                                                                   
                                <ui:panelGroup  block="true" style="padding-top: 10px;">
                                    <h:selectOneMenu  id="dropdown1" binding="#{AddLinkPage.dropdown1}" >
                                            <f:selectItems  binding="#{AddLinkPage.dropdown1SelectItems}"  value="#{AddLinkPage.dropdown1DefaultItems}"/>
                                    </h:selectOneMenu>
                                </ui:panelGroup>

                                <ui:panelGroup  block="true"  style="padding-top: 20px" >
                                        <h:commandButton  id="button1" value="Save" action="#{AddLinkPage.saveLink}" disabled="#{AddLinkPage.saveDisabled}"/>
                                        <h:commandButton  id="button2" value="Cancel" style="margin-left: 30px" action="#{AddLinkPage.cancel}" />
                                </ui:panelGroup>            
                                    
                                </ui:panelLayout>
                            </ui:panelLayout>
                          
                    </ui:form>
    </f:subview>
</jsp:root>
