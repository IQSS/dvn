<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
                            
   <f:subview id="siteStatistics">
        <ui:form  id="siteStatisticsForm"> 
            <ui:panelLayout  id="layoutPanel1" panelLayout="flow" styleClass="vdcSectionMiddleFixed" style="width: 700px">
                    <ui:panelLayout  id="layoutPanel2" panelLayout="flow" styleClass="vdcSectionHeader">
                        <h:outputText  value="Site Statistics Selector"/>
                    </ui:panelLayout>
                   <ui:panelLayout rendered="#{SiteStatistics.success}" panelLayout="flow" styleClass="vdcSectionMiddleMessage" style="width: 400px; margin-top: 10px; margin-bottom: -10px">
                         <h:messages styleClass="successMessage" layout="table" showDetail="false" showSummary="true"/>
                    </ui:panelLayout>
                    <ui:panelLayout id="layoutPanel3" panelLayout="flow" style="padding: 30px 50px 30px 50px; ">
                        <ui:panelGroup  block="true" id="groupPanel2">
                            <h:outputText  id="viewNetwork" value="Select a Report Type"/>
                            <h:selectOneRadio id="reportee" layout="pageDirection" onchange="submit();" required="true" value="#{SiteStatistics.reportee}" valueChangeListener="#{SiteStatistics.changeReportee}">
                                <f:selectItems value="#{SiteStatistics.reportees}"/>
                            </h:selectOneRadio>
                            <h:inputHidden id="reportType" value="#{SiteStatistics.reportType}" valueChangeListener="#{SiteStatistics.changeReportType}"/>
                            <ui:panelGroup  block="true" style="padding-right: 70px">
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
                    </ui:panelLayout>
                </ui:panelLayout>       
        </ui:form>
    </f:subview>
</jsp:root>
