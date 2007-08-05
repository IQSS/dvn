<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
                            
   <f:subview id="graphicalIQSS" rendered="#{SiteStatistics.reportee == 'iqss' and SiteStatistics.reportType == 'html'}">
        <ui:form  id="siteStatisticsViewForm">
                <jsp:include page="/cgi/awstats/custom/awstats.iqss.html"/>
        </ui:form>
    </f:subview>
    <f:subview id="graphicalMIT" rendered="#{SiteStatistics.reportee == 'mit' and SiteStatistics.reportType == 'html'}">
        <ui:form  id="siteStatisticsViewForm">
                <jsp:include page="/cgi/awstats/custom/awstats.mit.html"/>
        </ui:form>
    </f:subview>
    <f:subview id="textMIT" rendered="#{SiteStatistics.reportee == 'mitMonthly' and SiteStatistics.reportType == 'txt'}">
        <ui:form  id="siteStatisticsViewForm">
                <jsp:include page="/cgi/awstats/custom/awstats.mit.txt"/>
        </ui:form>
    </f:subview>
</jsp:root>