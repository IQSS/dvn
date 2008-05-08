<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
    xmlns:h="http://java.sun.com/jsf/html" 
    xmlns:jsp="http://java.sun.com/JSP/Page" 
    xmlns:ui="http://www.sun.com/web/ui"
    xmlns:tiles="http://struts.apache.org/tags-tiles"
    xmlns:c="http://java.sun.com/jsp/jstl/core">

    <f:subview id="editSitePageView">
                          
        <ui:form binding="#{EditSitePage.form1}" id="form1">
           <script type="text/javascript">
               //<![CDATA[
                 function changeValue(obj) {
                    if (window.event)
                        obj.value = window.event.srcElement.value;
                        showAll();
                        document.getElementById('content:editSitePageView:form1').submit();
                }
                 
                //init the hidden fields
                 function showAll(){
                    var theForm = document.getElementById('content:editSitePageView:form1');
                    var showScholarFields = false;
                    for (var i = 0; i < theForm.elements.length; i++) {
                        if ( (theForm.elements[i].checked) && (theForm.elements[i].value == "Scholar") ) {
                            showScholarFields = true;
                            document.getElementById('content:editSitePageView:form1:firstnameLabel').style.display = 'block';
                            document.getElementById('content:editSitePageView:form1:lastnameLabel').style.display = 'block';
                            document.getElementById('content:editSitePageView:form1:firstName').style.display = 'block';
                            document.getElementById('content:editSitePageView:form1:lastName').style.display = 'block';
                            break;
                        }
                    }
                    if (!showScholarFields) {
                        document.getElementById('content:editSitePageView:form1:firstnameLabel').style.display = 'none';
                        document.getElementById('content:editSitePageView:form1:lastnameLabel').style.display = 'none';
                        document.getElementById('content:editSitePageView:form1:firstName').style.display = 'none';
                        document.getElementById('content:editSitePageView:form1:lastName').style.display = 'none';
                    }
                }
                
                function createDvName() {
                    if (document.getElementById('content:editSitePageView:form1:firstName').value != "" && document.getElementById('content:editSitePageView:form1:lastName').value != "") {
                        document.getElementById('content:editSitePageView:form1:dataverseName').value = document.getElementById('content:editSitePageView:form1:firstName').value + " " + document.getElementById('content:editSitePageView:form1:lastName').value;
                    }
                }
                 // ]]>
            </script>
             
             <div class="dvn_section">
                 <div class="dvn_sectionTitle">
                     
                         <h:outputText binding="#{EditSitePage.outputText1}" value="Edit Dataverse Name and Alias"/>
                      
                 </div>            
                 <div class="dvn_sectionBox"> 
                     <div class="dvn_margin12">
                         
                         <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
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
                            <ui:panelGroup block="true" style="vertical-align: top;">
                                <h:selectOneRadio id="dataverseOption" 
                                                    layout="pageDirection" 
                                                    immediate="true"
                                                    onclick="changeValue(this);"
                                                    valueChangeListener="#{AddSitePage.changeDataverseOption}"
                                                    value="#{EditSitePage.dataverseType}">
                                    <f:selectItems value="#{AddSitePage.dataverseOptions}"/>
                                </h:selectOneRadio>
                            </ui:panelGroup>
                            
                            <!-- first name -->
                            <ui:panelGroup block="true" style="vertical-align: top;">
                                <h:outputLabel for="firstName" id="firstnameLabel">
                                    <h:outputText style="white-space: nowrap; padding-right: 10px; " value="First Name"/>
                                    <h:graphicImage value="#{bundle.iconRequired}"/>
                                </h:outputLabel>
                            </ui:panelGroup>
                            <ui:panelGroup block="true" style="vertical-align: top;">
                                <h:inputText id="firstName" 
                                                value="#{EditSitePage.firstName}" 
                                                valueChangeListener="#{EditSitePage.changeFirstName}" 
                                                validator="#{EditSitePage.validateIsEmpty}" 
                                                style="display:block;"/>
                                <h:message for="firstName" showSummary="true" showDetail="false" errorClass="errorMessage" styleClass="errorMessage"/>
                            </ui:panelGroup>
                            <!-- last name -->
                            <ui:panelGroup block="true" style="vertical-align: top;">
                                <h:outputLabel for="lastName" id="lastnameLabel">
                                    <h:outputText style="white-space: nowrap; padding-right: 10px; " value="Last Name"/>  
                                    <h:graphicImage value="#{bundle.iconRequired}"/>
                                </h:outputLabel>
                            </ui:panelGroup>
                            <ui:panelGroup block="true" style="vertical-align: top;">
                                <h:inputText id="lastName" 
                                                value="#{EditSitePage.lastName}" 
                                                valueChangeListener="#{EditSitePage.changeLastName}" 
                                                validator="#{EditSitePage.validateIsEmpty}"
                                                style="display:block;"/>
                                                
                                <h:message for="lastName" showSummary="true" showDetail="false" errorClass="errorMessage" styleClass="errorMessage"/>
                            </ui:panelGroup>
                            <!-- affiliation -->
                            <ui:panelGroup block="true" style="vertical-align: top;">
                                <h:outputLabel for="affiliation" id="affiliationLabel">
                                    <h:outputText style="white-space: nowrap; padding-right: 10px; " value="Affiliation"/>                                              
                                </h:outputLabel>
                            </ui:panelGroup>
                            <ui:panelGroup block="true" style="vertical-align: top;">
                                <h:inputText id="affiliation" 
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
                                 <h:inputText binding="#{EditSitePage.dataverseName}" 
                                                id="dataverseName" 
                                                required="true"
                                                requiredMessage="This field is required."
                                                size="60" 
                                                validator="#{EditSitePage.validateName}"
                                                value="#{VDCRequest.currentVDC.name}"/>
                                 <br />
                                 <h:outputText styleClass="vdcHelpText" value="Name used to refer to this dataverse in Dataverse Network Homepage and other pages."/>
                                 <h:message for="dataverseName" showSummary="true" showDetail="false" styleClass="errorMessage"/>
                             </ui:panelGroup>
                             <ui:panelGroup block="true" style="vertical-align: top;">
                                 <h:outputLabel binding="#{EditSitePage.componentLabel2}" for="componentLabel2" id="componentLabel2">
                                     <h:outputText binding="#{EditSitePage.componentLabel2Text}" 
                                                    id="componentLabel2Text" 
                                                    style="white-space: nowrap; padding-right: 10px; " 
                                                    value="Dataverse Alias"/>
                                     <h:graphicImage value="#{bundle.iconRequired}"/>
                                 </h:outputLabel>
                             </ui:panelGroup>
                             <ui:panelGroup>
                                 <h:inputText binding="#{EditSitePage.dataverseAlias}" 
                                            id="dataverseAlias" 
                                            required="true" 
                                            requiredMessage="This field is required."
                                            validator="#{EditSitePage.validateAlias}"
                                            value="#{VDCRequest.currentVDC.alias}"/>
                                <br />
                                 <h:outputText styleClass="vdcHelpText" value="Short name used to build the URL for this dataverse, e.g., http://.../dv/'alias'. It is case sensitive."/>
                                 <h:message for="dataverseAlias" showSummary="true" showDetail="false" styleClass="errorMessage"/>
                             </ui:panelGroup>
                         </h:panelGrid>
                         <ui:panelGroup binding="#{EditSitePage.groupPanel1}" block="true" id="groupPanel1" style="padding-left: 160px">
                             <h:commandButton binding="#{EditSitePage.button1}" id="button1"  value="Save" action="#{EditSitePage.edit}"/>
                             <h:commandButton binding="#{EditSitePage.button2}" id="button2" style="margin-left: 20px" value="Cancel" action="#{EditSitePage.cancel}"/>
                         </ui:panelGroup>
                         
                     </div>
                 </div>
             </div>
             <script type="text/javascript">
                  //<![CDATA[
                    // this is done to ensure that the scholar fields are properly inited. wjb
                    showAll();
                    // ]]>
            </script>
        </ui:form>
    </f:subview>
</jsp:root>
