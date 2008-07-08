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
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>

</head>

<body>
<gui:composition template="/template.xhtml">

<gui:param name="pageTitle" value="DVN - Site Statistics" />

  <gui:define name="body">
      <f:loadBundle basename="Bundle" var="bundle"/>
     <f:loadBundle basename="BundleAnalysis" var="bundleAnalysis"/>
                            

    <ui:form  id="siteStatisticsForm"> 
        <f:verbatim>
            <script type="text/javascript">
               //<![CDATA[                  
                function changeValue(obj) {
                    if (window.event)
                        obj.value = window.event.srcElement.value;
                        document.forms['content:siteStatistics:siteStatisticsForm'].submit();
                }
               //]]>                
            </script>
        </f:verbatim>
        <div class="dvn_section">
            <div class="dvn_sectionTitle">
                
                    <h:outputText  value="Site Statistics Selector"/>
               
            </div>            
            <div class="dvn_sectionBox"> 
                <div class="dvn_margin12">
                    
                    <ui:panelGroup rendered="#{SiteStatistics.success}" >
                        <h:messages styleClass="successMessage" layout="list" showDetail="false" showSummary="true"/>
                    </ui:panelGroup>
                    
                    <ui:panelGroup  block="true" id="groupPanel2">
                        <h:outputText  id="viewNetwork" value="Select a Report Type"/>
                        <h:selectOneRadio id="reportee" layout="pageDirection" onclick="changeValue(this);" required="true" value="#{SiteStatistics.reportee}" valueChangeListener="#{SiteStatistics.changeReportee}">
                            <f:selectItems value="#{SiteStatistics.reportees}"/>
                        </h:selectOneRadio>
                        <h:inputHidden id="reportType" value="#{SiteStatistics.reportType}" valueChangeListener="#{SiteStatistics.changeReportType}"/>
                        <ui:panelGroup rendered="#{SiteStatistics.reportee == 'mitMonthly'}"  block="true" style="padding-right: 70px">
                            <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />  
                            <h:outputText value="The MIT montly report will be reported for each month beginning with July and ending in the current month." styleClass="vdcHelpText" />
                        </ui:panelGroup>
                    </ui:panelGroup>
                    <h:message id="reporteeMsg" 
                               for="reportee"
                               styleClass="errorMessage"/>
                    <f:verbatim><br /></f:verbatim>
                    
                    <ui:panelGroup  block="true" id="groupPanel5" style="padding-left: 200px; padding-top: 20px">
                        <h:commandButton id="btnSubmit" action="#{SiteStatistics.viewStatistics}" value="View Statistics"/>
                        <h:commandButton  id="btnCancel" style="margin-left: 30px" value="#{bundle.cancelButtonLabel}" action="#{EditNetworkAnnouncementsPage.cancel_action}"/>
                    </ui:panelGroup>
                    
                </div>
            </div>
        </div>
                
    </ui:form>
            </gui:define>
        </gui:composition>
    </body>
</html>
