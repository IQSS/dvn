<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
    xmlns:h="http://java.sun.com/jsf/html" 
    xmlns:jsp="http://java.sun.com/JSP/Page" 
    xmlns:ui="http://www.sun.com/web/ui"
    xmlns:tiles="http://struts.apache.org/tags-tiles"
    xmlns:c="http://java.sun.com/jsp/jstl/core">

   <f:subview id="customBannerView">
       <h:outputText escape="false" rendered="#{VDCRequest.currentVDCId != null}" value="#{VDCRequest.currentVDC.header}"/>
       <h:outputText escape="false" rendered="#{VDCRequest.currentVDCId == null}" value="#{VDCRequest.vdcNetwork.networkPageHeader}"/>
   </f:subview>
</jsp:root>
