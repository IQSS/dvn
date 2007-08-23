<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
    xmlns:h="http://java.sun.com/jsf/html" 
    xmlns:jsp="http://java.sun.com/JSP/Page" 
    xmlns:ui="http://www.sun.com/web/ui"
    xmlns:tiles="http://struts.apache.org/tags-tiles"
    xmlns:c="http://java.sun.com/jsp/jstl/core">

    <f:subview id="addSitePageView">
        <h:outputText id="statusMessage" styleClass="#{AddSitePage.msg.styleClass}" value="#{AddSitePage.msg.messageText}" />
        <ui:form binding="#{AddSitePage.form1}" id="form1">
            
            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                
                        <h:outputText binding="#{AddSitePage.outputText1}" value="Add a New Dataverse"/>
                    
                </div>            
                <div class="dvn_sectionBox">
                    <div class="dvn_margin12"> 
                        
                        <ui:panelGroup block="true" style="padding-left: 20px; padding-right: 30px">
                            <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                            <h:outputText binding="#{AddSitePage.outputText2}" id="outputText2" styleClass="vdcHelpText" value="When your dataverse is  created, it will be set to 'restricted' by default. As soon as you are ready to make it public, you can do so by going to 'My Options' in your new dataverse.  "/>
                        </ui:panelGroup>
                        <h:panelGrid binding="#{AddSitePage.gridPanel1}" cellpadding="0" cellspacing="0"
                                     columnClasses="vdcAddSiteCol1, vdcAddSiteCol2" columns="2" id="gridPanel1" style="margin-top: 30px; margin-bottom: 30px">
                            <ui:panelGroup block="true" style="vertical-align: top;">
                                <h:outputLabel binding="#{AddSitePage.componentLabel1}" for="componentLabel1" id="componentLabel1">
                                    <h:outputText binding="#{AddSitePage.componentLabel1Text}" id="componentLabel1Text" style="white-space: nowrap; padding-right: 10px; " value="Dataverse Name"/>                                              
                                </h:outputLabel>
                            </ui:panelGroup>
                            <ui:panelGroup>
                                <h:inputText binding="#{AddSitePage.dataverseName}" id="dataverseName" required="true" validator="#{AddSitePage.validateName}" size="60"/>
                                <verbatim><br /></verbatim>
                                <h:outputText styleClass="vdcHelpText" value="Name used to refer to this dataverse in Dataverse Network Homepage and other pages."/>
                                <h:message for="dataverseName" showSummary="true" showDetail="false" errorClass="errorMessage" styleClass="errorMessage"/>
                            </ui:panelGroup>
                            <ui:panelGroup block="true" style="vertical-align: top;">
                                <h:outputLabel binding="#{AddSitePage.componentLabel2}" for="componentLabel2" id="componentLabel2">
                                    <h:outputText binding="#{AddSitePage.componentLabel2Text}" id="componentLabel2Text" style="white-space: nowrap; padding-right: 10px; " value="Dataverse Alias"/> 
                                </h:outputLabel>
                            </ui:panelGroup>
                            <ui:panelGroup>
                                <h:inputText binding="#{AddSitePage.dataverseAlias}" id="dataverseAlias" required="true" validator="#{AddSitePage.validateAlias}" />
                                <verbatim><br /></verbatim>
                                <h:outputText styleClass="vdcHelpText" value="Short name used to build the URL for this dataverse, i.e., http://.../dv/'alias'. It is case sensitive."/>
                                <h:message for="dataverseAlias" showSummary="true" showDetail="false" errorClass="errorMessage" styleClass="errorMessage"/>
                            </ui:panelGroup>
                        </h:panelGrid>
                        <ui:panelGroup binding="#{AddSitePage.groupPanel1}" block="true" id="groupPanel1" style="padding-left: 160px">
                            <h:commandButton binding="#{AddSitePage.button1}" id="button1" value="Save" action="#{AddSitePage.create}"/>
                            <h:commandButton binding="#{AddSitePage.button2}" id="button2" style="margin-left: 20px" immediate="true" value="Cancel" action="#{AddSitePage.cancel}"/>
                        </ui:panelGroup>
                        
                    </div>
                </div>
            </div>
        </ui:form>
    </f:subview>
</jsp:root>
