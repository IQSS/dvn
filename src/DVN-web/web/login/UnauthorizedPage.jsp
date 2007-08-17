<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
    <f:subview id="UnauthorizedPageView">
        <ui:form  id="unauthorizedForm">
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
            
            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                    <h3>
                        <h:outputText value="Restricted Page"/>
                    </h3>
                </div>            
                <div class="dvn_sectionBox"> 
                    <div class="dvn_margin12">
                        
                        <h:outputText  id="outputText4" style="warnMessage" value="We are sorry; you do not have permissions to view this page."/>            
                        
                    </div>
                </div>
            </div>
        </ui:form>
    </f:subview>
</jsp:root>
