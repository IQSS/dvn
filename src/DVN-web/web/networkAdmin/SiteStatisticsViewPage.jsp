<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:gui="http://java.sun.com/jsf/facelets"
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


<gui:param name="pageTitle" value="DVN Site Statistics View"/>

  <gui:define name="body">
                            

        <ui:form  id="siteStatisticsViewForm"> 
                <jsp:include page="/networkAdmin/webstatistics/awstats.${requestScope.reportee}.${requestScope.reportType}"/>
        </ui:form>
            </gui:define>
        </gui:composition>
    </body>
</html>