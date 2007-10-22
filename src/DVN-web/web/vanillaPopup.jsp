<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
                            
   <f:subview id="vanillaPopupPage">
        <ui:form  id="vanillaPopupForm">
            <jsp:include page="/networkAdmin/webstatistics/awstats.${param.reportee}.txt"/>
        </ui:form>
    </f:subview>
</jsp:root>
    