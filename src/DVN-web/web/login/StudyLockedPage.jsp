<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
    <f:subview id="StudyLockedPageView">
        <ui:form  id="studyLockedForm">
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                    <h3>
                        <h:outputText value="File Upload In Progress"/>   
                    </h3>
                </div>            
                <div class="dvn_sectionBox dvn_pad12"> 
                    <h:outputText  style="warnMessage" value="We are sorry; this study is currently unavailable for editing because files are being uploaded.   When the file upload is completed the study will be editable again. "/>
                    <p>
                        <h:outputText   style="warnMessage" value="#{param.message}"/>   
                    </p>
                </div>
            </div>

        </ui:form>
    </f:subview>
</jsp:root>
