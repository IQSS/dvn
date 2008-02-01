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
         <ui:script url="/resources/dvn_ui.js"/>
         <f:verbatim>
             <script type="text/javascript">
             // <![CDATA[                          
                 var isChild = true;
                 function printMe() {
                    window.print();
                 }
             //]]>                     
             </script>
           
         </f:verbatim>
      </ui:head>
      <body style="background-color:#eeeeee;">        
          <div class="dvn_wrapper">
              <div class="dvn_content">
                      <f:subview id="content">
                          <tiles:insert name="content" flush="false"/>
                      </f:subview>
              </div>
          </div>
      </body>
</ui:html>
</ui:page>
</f:view>
</jsp:root>
