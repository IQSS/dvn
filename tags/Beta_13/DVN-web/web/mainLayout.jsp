<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
    xmlns:h="http://java.sun.com/jsf/html" 
    xmlns:jsp="http://java.sun.com/JSP/Page" 
    xmlns:ui="http://www.sun.com/web/ui"
    xmlns:tiles="http://struts.apache.org/tags-tiles"
    xmlns:c="http://java.sun.com/jsp/jstl/core">

<jsp:directive.page errorPage="/ExceptionHandler" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"/>       
   
<tiles:importAttribute name="pageTitle" scope="request"/>

<f:loadBundle basename="Bundle" var="bundle"/>
<f:loadBundle basename="BundleAnalysis" var="bundleAnalysis"/>
<f:view>
<ui:page id="layoutpage">
<ui:html>
      <ui:head title="#{pageTitle.value}">
         <ui:link  id="link2" url="/resources/stylesheet.css"/>
         <ui:script url="/resources/EventCapture.js"/>
      </ui:head>
      <body>
           <f:subview id="banner">
              <tiles:insert name="banner" flush="false"/>
           </f:subview>
           
          <!-- <ui:panelGroup style="max-width:1000px; text-align: center; margin-left: auto; margin-right: auto; " block="true"> -->
           <f:subview id="menubar">
              <tiles:insert name="menubar" flush="false"/>
           </f:subview>

           <f:subview id="content">
                <tiles:insert name="content" flush="false"/>
            </f:subview>
            <!-- </ui:panelGroup> -->
            <f:subview id="footer">
                <tiles:insert name="footer" flush="false"/>
            </f:subview>
      </body>
</ui:html>
</ui:page>
</f:view>
</jsp:root>
