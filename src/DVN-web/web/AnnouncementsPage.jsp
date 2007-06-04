<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
                            
   <f:subview id="AnnouncementsPageView">
        <ui:form  id="announcementsForm">  
            <h:inputHidden rendered="#{VDCRequest.currentVDCId != null}" id="vdcId" value="#{VDCRequest.currentVDCId}"/>
                <ui:panelLayout  id="layoutPanel1" panelLayout="flow" styleClass="vdcSectionMiddle">
                    <ui:panelLayout  id="layoutPanel2" panelLayout="flow" styleClass="vdcSectionHeader">
                        <h:outputText  value="#{ (VDCRequest.currentVDCId != null) ? 'Local' : 'Network'} #{bundle.announcementsHeading}"/>
                    </ui:panelLayout>
                    <ui:panelLayout  id="layoutPanel3" panelLayout="flow" style="padding: 40px 40px 30px 40px; ">
                        <h:outputText  escape="false" id="pageContent" value="#{ (VDCRequest.currentVDCId == null) ? VDCRequest.vdcNetwork.announcements : VDCRequest.currentVDC.announcements }"/>
                    </ui:panelLayout>
                </ui:panelLayout>
        </ui:form>
   </f:subview>
</jsp:root>
