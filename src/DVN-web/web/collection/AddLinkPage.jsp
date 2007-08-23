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
            
            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                        
                        <h:outputText value="Add Collection Link"/>
                    
                </div>            
                <div class="dvn_sectionBox">      
                    <div class="dvn_margin12">
                        
                        <h:outputText value="Select a collection from another (public) dataverse that you would like to add your dataverse (the collection you select will be displayed in the homepage of your dataverse and it will be included in your dataverse searches)."  />
                        
                        <ui:panelGroup  block="true" style="padding-top: 10px;">
                            <h:selectOneMenu  id="dropdown1" binding="#{AddLinkPage.dropdown1}" >
                                <f:selectItems  binding="#{AddLinkPage.dropdown1SelectItems}"  value="#{AddLinkPage.dropdown1DefaultItems}"/>
                            </h:selectOneMenu>
                        </ui:panelGroup>
                        
                        <ui:panelGroup  block="true"  style="padding-top: 20px" >
                            <h:commandButton  id="button1" value="Save" action="#{AddLinkPage.saveLink}" disabled="#{AddLinkPage.saveDisabled}"/>
                            <h:commandButton  id="button2" value="Cancel" style="margin-left: 30px" action="#{AddLinkPage.cancel}" />
                        </ui:panelGroup>            
                        
                    </div>
                </div>
             </div>

        </ui:form>
    </f:subview>
</jsp:root>
