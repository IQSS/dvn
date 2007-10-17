<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
    xmlns:h="http://java.sun.com/jsf/html" 
    xmlns:jsp="http://java.sun.com/JSP/Page" 
    xmlns:ui="http://www.sun.com/web/ui"
    xmlns:tiles="http://struts.apache.org/tags-tiles"
    xmlns:c="http://java.sun.com/jsp/jstl/core">

    <f:subview id="editSitePageView">
                          
        <ui:form binding="#{EditSitePage.form1}" id="form1">
           <!-- <script language="JavaScript">
                function setCheckedValue(frmElement) {
                    var theForm = document.forms['content:editSitePageView:form1'];
                    for (var i = 0; i &lt; theForm.elements.length; i++) {
                        if (theForm.elements[i].checked) {
                            frmElement.value = theForm.elements[i].value;
                            break;
                        }
                    }
                 }
                 
                 //init the hidden fields
                 function showAll(){
                    var theForm = document.forms['content:editSitePageView:form1'];
                    var showScholarFields = false;
                    for (var i = 0; i &lt; theForm.elements.length; i++) {
                        if (theForm.elements[i].checked &amp;&amp; theForm.elements[i].value == "Scholar") {
                            showScholarFields = true;
                            break;
                        }
                    }
                    if (!showScholarFields) {
                        document.getElementById('content:editSitePageView:form1:firstName').style.display = 'none';
                        document.getElementById('content:editSitePageView:form1:lastName').style.display = 'none';
                    }
                }
            </script> -->
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
             
             <div class="dvn_section">
                 <div class="dvn_sectionTitle">
                     
                         <h:outputText binding="#{EditSitePage.outputText1}" value="Edit Dataverse Name and Alias"/>
                      
                 </div>            
                 <div class="dvn_sectionBox"> 
                     <div class="dvn_margin12">
                         
                         
                         <ui:panelLayout styleClass="#{EditSitePage.msg.styleClass}" rendered="#{!empty EditSitePage.msg.messageText}">
                             <h:outputText id="statusMessage" value="#{EditSitePage.msg.messageText}" /> 
                         </ui:panelLayout>
                         
                         <ui:panelGroup block="true" style="padding-left: 20px; padding-right: 30px">
                             <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                             <h:outputText binding="#{EditSitePage.outputText2}" id="outputText2" styleClass="vdcHelpText" value="Note that if you change the alias, links to this Dataverse will change (http//.../dv/'alias')."/>
                         </ui:panelGroup>
                         <h:panelGrid binding="#{EditSitePage.gridPanel1}" cellpadding="0" cellspacing="0"
                                      columnClasses="vdcAddSiteCol1, vdcAddSiteCol2" columns="2" id="gridPanel1" style="margin-top: 30px; margin-bottom: 30px">
                             <ui:panelGroup block="true" style="vertical-align: top;">
                                <h:outputLabel for="dataverseOption" id="dataverseLabel">
                                    <h:outputText style="white-space: nowrap; padding-right: 10px; " value="Type of Dataverse"/>                                              
                                </h:outputLabel>
                            </ui:panelGroup>
                            <!-- Developed for 16a, but not used pending a solution for cast up from scholar dv to VDC -->
                            <!-- <ui:panelGroup block="true" style="vertical-align: top;">
                                <h:selectOneRadio id="dataverseOption" 
                                                    layout="lineDirection" 
                                                    onchange="setCheckedValue(document.getElementById('content:editSitePageView:form1:dataverseType'));submit();"
                                                    valueChangeListener="#{AddSitePage.changeDataverseOption}"
                                                    required="true"
                                                    value="#{EditSitePage.dataverseType}">
                                    <f:selectItems value="#{AddSitePage.dataverseOptions}"/>
                                </h:selectOneRadio>
                             <h:inputHidden id="dataverseType" immediate="true" value="#{EditSitePage.dataverseType}" valueChangeListener="#{EditSitePage.changeDataverseOption}"/>
                            </ui:panelGroup> -->
                            
                            <ui:panelGroup block="true" style="vertical-align: top;">
                             <h:outputText id="dataverseOption" value="#{EditSitePage.dataverseType}"/>
                            </ui:panelGroup>
                            <!-- first name -->
                            <ui:panelGroup rendered="#{EditSitePage.dataverseType == 'Scholar'}" block="true" style="vertical-align: top;">
                                <h:outputLabel for="firstName" id="firstnameLabel">
                                    <h:outputText style="white-space: nowrap; padding-right: 10px; " value="First Name"/>
                                    <h:graphicImage value="#{bundle.iconRequired}"/>
                                </h:outputLabel>
                            </ui:panelGroup>
                            <ui:panelGroup rendered="#{EditSitePage.dataverseType == 'Scholar'}" block="true" style="vertical-align: top;">
                                <h:inputText id="firstName" 
                                                immediate="true"
                                                rendered="#{EditSitePage.dataverseType == 'Scholar'}"
                                                value="#{EditSitePage.firstName}" 
                                                valueChangeListener="#{EditSitePage.changeFirstName}"
                                                style="display:block;"/>
                            </ui:panelGroup>
                            <!-- last name -->
                            <ui:panelGroup rendered="#{EditSitePage.dataverseType == 'Scholar'}" block="true" style="vertical-align: top;">
                                <h:outputLabel for="lastName" id="lastnameLabel">
                                    <h:outputText style="white-space: nowrap; padding-right: 10px; " value="Last Name"/>  
                                    <h:graphicImage value="#{bundle.iconRequired}"/>
                                </h:outputLabel>
                            </ui:panelGroup>
                            <ui:panelGroup rendered="#{EditSitePage.dataverseType == 'Scholar'}" block="true" style="vertical-align: top;">
                                <h:inputText id="lastName" 
                                                immediate="true"
                                                rendered="#{EditSitePage.dataverseType == 'Scholar'}"
                                                value="#{EditSitePage.lastName}" 
                                                valueChangeListener="#{EditSitePage.changeLastName}"
                                                style="display:block;"/>
                            </ui:panelGroup>
                            <!-- affiliation -->
                            <ui:panelGroup block="true" style="vertical-align: top;">
                                <h:outputLabel for="affiliation" id="affiliationLabel">
                                    <h:outputText style="white-space: nowrap; padding-right: 10px; " value="Affiliation"/>                                              
                                </h:outputLabel>
                            </ui:panelGroup>
                            <ui:panelGroup block="true" style="vertical-align: top;">
                                <h:inputText id="affiliation" 
                                                immediate="true" 
                                                value="#{EditSitePage.affiliation}" 
                                                valueChangeListener="#{EditSitePage.changeAffiliation}" 
                                                />
                            </ui:panelGroup>
                            <!-- dataverse name -->
                             <ui:panelGroup block="true" style="vertical-align: top;">
                                 <h:outputLabel binding="#{EditSitePage.componentLabel1}" for="componentLabel1" id="componentLabel1">
                                     <h:outputText binding="#{EditSitePage.componentLabel1Text}" id="componentLabel1Text" style="white-space: nowrap; padding-right: 10px; " value="Dataverse Name"/>
                                     <h:graphicImage value="#{bundle.iconRequired}"/>
                                 </h:outputLabel>
                             </ui:panelGroup>
                             <ui:panelGroup>
                                 <h:inputText binding="#{EditSitePage.dataverseName}" id="dataverseName" required="true" validator="#{EditSitePage.validateName}" size="60" value="#{VDCRequest.currentVDC.name}"/>
                                 <br />
                                 <h:outputText styleClass="vdcHelpText" value="Name used to refer to this dataverse in Dataverse Network Homepage and other pages."/>
                                 <h:message for="dataverseName" showSummary="true" showDetail="false" styleClass="errorMessage"/>
                             </ui:panelGroup>
                             <ui:panelGroup block="true" style="vertical-align: top;">
                                 <h:outputLabel binding="#{EditSitePage.componentLabel2}" for="componentLabel2" id="componentLabel2">
                                     <h:outputText binding="#{EditSitePage.componentLabel2Text}" id="componentLabel2Text" style="white-space: nowrap; padding-right: 10px; " value="Dataverse Alias"/>
                                     <h:graphicImage value="#{bundle.iconRequired}"/>
                                 </h:outputLabel>
                             </ui:panelGroup>
                             <ui:panelGroup>
                                 <h:inputText binding="#{EditSitePage.dataverseAlias}" id="dataverseAlias" required="true" validator="#{EditSitePage.validateAlias}" value="#{VDCRequest.currentVDC.alias}"/>
                                <br />
                                 <h:outputText styleClass="vdcHelpText" value="Short name used to build the URL for this dataverse, i.e., http://.../dv/'alias'. It is case sensitive."/>
                                 <h:message for="dataverseAlias" showSummary="true" showDetail="false" styleClass="errorMessage"/>
                             </ui:panelGroup>
                         </h:panelGrid>
                         <ui:panelGroup binding="#{EditSitePage.groupPanel1}" block="true" id="groupPanel1" style="padding-left: 160px">
                             <h:commandButton binding="#{EditSitePage.button1}" rendered="#{EditSitePage.dataverseType != 'Scholar'}" id="button1" value="Save" action="#{EditSitePage.edit}"/>
                             <h:commandButton rendered="#{EditSitePage.dataverseType == 'Scholar'}" id="btnEditSdv" value="Save" action="#{EditSitePage.editScholarDataverse}"/>
                             <h:commandButton binding="#{EditSitePage.button2}" id="button2" immediate="true" style="margin-left: 20px" value="Cancel" action="#{EditSitePage.cancel}"/>
                         </ui:panelGroup>
                         
                     </div>
                 </div>
             </div>
             <!-- <script language="JavaScript">
                    // this is done to ensure that the scholar fields are properly inited. wjb
                    showAll();
             </script> -->
        </ui:form>
    </f:subview>
</jsp:root>
