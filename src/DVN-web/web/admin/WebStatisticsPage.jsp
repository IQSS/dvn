<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
      xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
      >
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>Facelets: DVN Style</title>
</head>

<body>
<gui:composition template="/template.xhtml">


This text will also not be displayed.

  <gui:define name="body">
      <f:loadBundle basename="Bundle" var="bundle"/>
     <f:loadBundle basename="BundleAnalysis" var="bundleAnalysis"/>


        <jsp:include flush="true" page="/data/awstats.${WebStatisticsPage.alias}.conf"/>
    </body>
</html>
