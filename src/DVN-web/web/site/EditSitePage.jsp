<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
    xmlns:h="http://java.sun.com/jsf/html" 
    xmlns:jsp="http://java.sun.com/JSP/Page" 
    xmlns:ui="http://www.sun.com/web/ui"
    xmlns:tiles="http://struts.apache.org/tags-tiles"
    xmlns:c="http://java.sun.com/jsp/jstl/core">

    <f:subview id="editSitePageView">
                          
        <ui:form binding="#{EditSitePage.form1}" id="form1">
             <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
               
             <div class="dvn_section">
                 <div class="dvn_sectionTitle">
                     <h3>
                         <h:outputText binding="#{EditSitePage.outputText1}" value="Edit Dataverse Name and Alias"/>
                     </h3>
                 </div>            
                 <div class="dvn_sectionBox"> 
                     <div class="dvn_margin12">
                         
                         
                         <ui:panelGroup styleClass="#{EditSitePage.msg.styleClass}" rendered="#{!empty EditSitePage.msg.messageText}">
                             <h:outputText id="statusMessage" value="#{EditSitePage.msg.messageText}" /> 
                         </ui:panelGroup>
                         
                         <ui:panelGroup block="true" style="padding-left: 20px; padding-right: 30px">
                             <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                             <h:outputText binding="#{EditSitePage.outputText2}" id="outputText2" styleClass="vdcHelpText" value="Note that if you change the alias, links to this Dataverse will change (http//.../dv/'alias')."/>
                         </ui:panelGroup>
                         <h:panelGrid binding="#{EditSitePage.gridPanel1}" cellpadding="0" cellspacing="0"
                                      columnClasses="vdcAddSiteCol1, vdcAddSiteCol2" columns="2" id="gridPanel1" style="margin-top: 30px; margin-bottom: 30px">
                             <ui:panelGroup block="true" style="vertical-align: top;">
                                 <h:outputLabel binding="#{EditSitePage.componentLabel1}" for="componentLabel1" id="componentLabel1">
                                     <h:outputText binding="#{EditSitePage.componentLabel1Text}" id="componentLabel1Text" style="white-space: nowrap; padding-right: 10px; " value="Dataverse Name"/>
                                 </h:outputLabel>
                             </ui:panelGroup>
                             <ui:panelGroup>
                                 <h:inputText binding="#{EditSitePage.dataverseName}" id="dataverseName" required="true" validator="#{EditSitePage.validateName}" size="60" value="#{VDCRequest.currentVDC.name}"/>
                                 <verbatim><br /></verbatim>
                                 <h:outputText styleClass="vdcHelpText" value="Name used to refer to this dataverse in Dataverse Network Homepage and other pages."/>
                                 <h:message for="dataverseName" showSummary="true" showDetail="false" styleClass="errorMessage"/>
                             </ui:panelGroup>
                             <ui:panelGroup block="true" style="vertical-align: top;">
                                 <h:outputLabel binding="#{EditSitePage.componentLabel2}" for="componentLabel2" id="componentLabel2">
                                     <h:outputText binding="#{EditSitePage.componentLabel2Text}" id="componentLabel2Text" style="white-space: nowrap; padding-right: 10px; " value="Dataverse Alias"/>
                                 </h:outputLabel>
                             </ui:panelGroup>
                             <ui:panelGroup>
                                 <h:inputText binding="#{EditSitePage.dataverseAlias}" id="dataverseAlias" required="true" validator="#{EditSitePage.validateAlias}" value="#{VDCRequest.currentVDC.alias}"/>
                                 <verbatim><br /></verbatim>
                                 <h:outputText styleClass="vdcHelpText" value="Short name used to build the URL for this dataverse, i.e., http://.../dv/'alias'. It is case sensitive."/>
                                 <h:message for="dataverseAlias" showSummary="true" showDetail="false" styleClass="errorMessage"/>
                             </ui:panelGroup>
                         </h:panelGrid>
                         <ui:panelGroup binding="#{EditSitePage.groupPanel1}" block="true" id="groupPanel1" style="padding-left: 160px">
                             <h:commandButton binding="#{EditSitePage.button1}" id="button1" value="Save" action="#{EditSitePage.edit}"/>
                             <h:commandButton binding="#{EditSitePage.button2}" id="button2" immediate="true" style="margin-left: 20px" value="Cancel" action="#{EditSitePage.cancel}"/>
                         </ui:panelGroup>
                         
                     </div>
                 </div>
             </div>
        </ui:form>
    </f:subview>
</jsp:root>
