<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
   <f:subview id="AnnouncementsPageView">
        <ui:form  id="aboutUsForm">
            <h:inputHidden rendered="#{VDCRequest.currentVDCId != null}" id="vdcId" value="#{VDCRequest.currentVDCId}"/>

            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                    
                        <h:outputText  value="About #{VDCRequest.currentVDC.name} Dataverse" rendered="#{VDCRequest.currentVDCId != null}"/>
                        <h:outputText  value="About #{VDCRequest.vdcNetwork.name} Dataverse Network" rendered="#{VDCRequest.currentVDCId == null}"/>
                 
                </div>            
                <div class="dvn_sectionBox"> 
                    <div class="dvn_margin12">
                        
                        <h:outputText  escape="false"  value="#{ (VDCRequest.currentVDCId == null) ? VDCRequest.vdcNetwork.aboutThisDataverseNetwork : VDCRequest.currentVDC.aboutThisDataverse }"/>
                        
                    </div>
                </div>
            </div>
            
        </ui:form>
    </f:subview>
</jsp:root>
