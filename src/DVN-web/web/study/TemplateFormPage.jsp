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

<gui:param name="pageTitle" value="DVN - Template Form View" />

  <gui:define name="body">
      <f:loadBundle basename="Bundle" var="bundle"/>
     <f:loadBundle basename="BundleAnalysis" var="bundleAnalysis"/>
        <f:verbatim>           
            <script type="text/javascript">
                // <![CDATA[ 
                // Functions for hiding and showing study fields
                //
                function getElementsByClassName( strClassName, obj ) {
                    var ar = arguments[2] || new Array();
                    var re = new RegExp("\\b" + strClassName + "\\b", "g");

                    if ( re.test(obj.className) ) {
                        ar.push( obj );
                    }
                    for ( var i = 0; i < obj.childNodes.length; i++ )
                        getElementsByClassName( strClassName, obj.childNodes[i], ar );
    
                    return ar;
                }
              
        
                function addLoadEvent(func) {
                    var oldonload = window.onload;
                    if (typeof window.onload != 'function') {
                        window.onload = func;
                    } else {
                        window.onload = function() {
                            if (oldonload) {
                                oldonload();
                            }
                            func();
                        }
                    }
                }

                function hideShowOptional(actionString) {
                           
                    var optionalDivs = getElementsByClassName("optional", document.body);
                    for (var i = 0; i < optionalDivs.length; i++)
                    {
                        //  alert("setting optional div");
                        var optionalDiv = optionalDivs[i];
                        if (actionString=="hide") {  
                            //   alert("hiding div");
                            optionalDiv.style.display='none';
                            
                        } else {
                            //  alert("showing div");
                            optionalDiv.style.display='';
                        }
                            
                    }
                }
                function initHide() {
                    //  alert("in initHide")
                    //  document.getElementById("hideRadio").checked=true
                    hideShowOptional("hide");
                }
                
                function updateHideShow(radioButton) {
                    // alert("in updateHideShow")
                    hideShowOptional(radioButton.value);   
                }
       
        
     
                // ]]> 
            </script> 
        </f:verbatim>
        <dvn:inlinehelpsupport writeHelpDiv="true" writeTipDiv="true" rendered="true"/>
        <f:loadBundle  basename="BundleHelp" var="helpText" />
        <h:form id="templateForm">
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
            <input type="hidden" name="pageName" value="TemplateFormPage"/>
                               
            <h:inputHidden id="token" value="#{TemplateFormPage.token}" />                        
            


            <div class="dvn_section" >
                <div class="dvn_sectionTitle">
                    <h:outputText value="Edit Study Template"/>
                </div>            
                <div class="dvn_sectionBoxNoBorders"> 
                    
                    <!-- validation errors occurred? -->
                    <ui:panelGroup id="messagePanel" styleClass="errorMessage" >
                        <h:outputText  value="Please check messages below" rendered="#{!empty facesContext.maximumSeverity}" />
                    </ui:panelGroup>
                    
                    <ui:panelGroup block="true" id="groupPanel10" styleClass="vdcTextRight">              
                        <h:commandButton id="button1" value="Save" action="#{TemplateFormPage.save}"/>
                        <h:commandButton immediate="true" id="button2" style="margin-left: 30px; margin-right: 15px" value="Cancel" action="#{TemplateFormPage.cancel}"/>   
                    </ui:panelGroup> 
                    
                                              
                            <ui:panelLayout id="layoutPanel1" panelLayout="flow" style="width: 98%"> 
                                <h:panelGrid cellpadding="0" cellspacing="0" columns="2" width="100%" style="margin-bottom: 5px"> 
                                    <ui:panelGroup block="true" id="groupPanel9b" >
                                                                            
                                       
                                        <input checked="checked" type="radio" id="hideRadio" name="hideShowRadio" value="hide" onclick="updateHideShow(this);"/> Show Required and Recommended Fields                                       
                                        <input type="radio" id="showRadio" name="hideShowRadio" value="show" onclick="updateHideShow(this);" /> Show All Fields
                                        <br/><h:outputText value="Template Name: "/><h:inputText value='#{TemplateFormPage.template.name}'/>
                                    </ui:panelGroup>
                                    <ui:panelGroup block="true">
                                        <h:graphicImage value="/resources/icon_required.gif"/> <h:outputText style="vdcHelpText" value="Required Fields"/>
                                        <h:graphicImage value="/resources/icon_recommended.gif"/><h:outputText style="vdcHelpText" value="Recommended Fields"/>
                                        <h:graphicImage value="/resources/icon_htmlformat.gif"/>
                                        <h:outputText value="#{bundle.htmlHelpText}"/>
                                        <ui:panelGroup block="true">
                                            <h:graphicImage value="/resources/icon_dateformat.gif"/><h:outputText style="vdcHelpText" value="A light orange form indicates that a Date format is required. Please enter YYYY or YYYY-MM or YYYY-MM-DD."/>    
                                        </ui:panelGroup> 
                                        <ui:panelGroup block="true">
                                            <h:graphicImage value="/resources/icon_add.gif"/><h:outputText style="vdcHelpText" value="Add a new row (e.g., when add multiple authors)."/><h:graphicImage value="/resources/icon_remove.gif"/><h:outputText style="vdcHelpText" value="Remove an existing row."/>    
                                        </ui:panelGroup>
                                    </ui:panelGroup>
                                    
                                </h:panelGrid> 
                                
                                <ui:panelGroup block="true" id="groupPanel9" styleClass="vdcStudyInfoHeader">
                                    <h:outputText id="outputText26" value="Citation Information"/>      
                                </ui:panelGroup>                                    
                                
                                <h:panelGrid cellpadding="0" cellspacing="0" columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2" width="100%">
                                
                                    <!-- TITLE -->
                                    
                                    <ui:panelGroup block="true" id="groupPanel37" styleClass="vdcEditStudyField">                                              
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.titleHelp}" linkText="#{editstudybundle.titleLabel}" heading="#{editstudybundle.titleHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>                                            
                                        <h:graphicImage id="image39" value="/resources/icon_required.gif" rendered="#{TemplateFormPage.studyMap[sfc.title].required}" />
                                        <h:graphicImage id="image40" value="/resources/icon_recommended.gif" rendered="#{TemplateFormPage.studyMap[sfc.title].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup block="true" id="groupPanel16" >
                                        <h:inputText binding="#{TemplateFormPage.inputTitle}" id="input_title" size="90"  value="#{TemplateFormPage.template.metadata.title}" />
                                        <h:message styleClass="errorMessage" for="input_title"/> 
                                    </ui:panelGroup> 
                                </h:panelGrid>
                                
                                <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.subTitle].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >
                                    
                                    <ui:panelGroup block="true"  styleClass="vdcEditStudyField" >                                              
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.subtitleHelp}" linkText="#{editstudybundle.subtitleLabel}" heading="#{editstudybundle.subtitleHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>                                             
                                        <h:graphicImage  value="/resources/icon_required.gif" rendered="#{TemplateFormPage.studyMap[sfc.subTitle].required}" />
                                        <h:graphicImage  value="/resources/icon_recommended.gif" rendered="#{TemplateFormPage.studyMap[sfc.subTitle].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup block="true" >
                                        <h:inputText id="input_subtitle" size="90" value="#{TemplateFormPage.template.metadata.subTitle}" required="#{TemplateFormPage.studyMap[sfc.subTitle].required}"/>                                                                 
                                        <h:message styleClass="errorMessage" for="input_subtitle"/> 
                                    </ui:panelGroup> 
                                </h:panelGrid>
                                
                             
                                
                                <!-- OTHER IDS -->
                                
                                <h:dataTable rowClasses="#{TemplateFormPage.otherIdLevel}" cellpadding="0" cellspacing="0" width="100%" value="dummy_datatable" >
                                    <h:column>                                  
                                        <h:dataTable  cellpadding="0" cellspacing="0" 
                                                      columnClasses="vdcEditStudyCol1b, vdcEditStudyCol2, vdcEditStudyCol3"
                                                      binding="#{TemplateFormPage.dataTableOtherIds}" 
                                                      value="#{TemplateFormPage.template.metadata.studyOtherIds}" var="currentRow" width="100%" >
                                            
                                            <h:column  >
                                                <ui:panelGroup  block="true" styleClass="vdcEditStudyField"> 
                                                    <dvn:inlinehelp helpMessage="#{editstudybundle.otheridHelp}" linkText="#{editstudybundle.otheridLabel}" heading="#{editstudybundle.otheridHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>         
                                                    <h:graphicImage  value="/resources/icon_required.gif" rendered="#{TemplateFormPage.otherIdLevel=='required'}"/>
                                                    <h:graphicImage  value="/resources/icon_recommended.gif" rendered="#{TemplateFormPage.otherIdLevel=='recommended'}"/>
                                                </ui:panelGroup> 
                                            </h:column>
                                            <h:column >
                                                <ui:panelGroup block="true" >
                                                    <ui:panelGroup  styleClass="#{TemplateFormPage.studyMap[sfc.otherId].templateField.fieldInputLevel.name}">    
                                                        <dvn:inlinehelp helpMessage="#{editstudybundle.otheridHelp}" linkText="#{editstudybundle.otheridLabel}" heading="#{editstudybundle.otheridHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>         
                                                        <h:graphicImage  value="/resources/icon_required.gif" rendered="#{TemplateFormPage.studyMap[sfc.otherId].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif" rendered="#{TemplateFormPage.studyMap[sfc.otherId].recommended}"/>
                                                        <f:verbatim><br/></f:verbatim>
                                                        <h:inputText maxlength="255" binding="#{TemplateFormPage.inputOtherId}" id = "input_otherId" value="#{currentRow.otherId}" required="#{TemplateFormPage.studyMap[sfc.otherId].required}"/>
                                                        <h:message styleClass="errorMessage" for="input_otherId"/> 
                                                        <f:verbatim><br/></f:verbatim>
                                                    </ui:panelGroup>
                                                    <ui:panelGroup styleClass="vdcEditStudyGroupField #{TemplateFormPage.studyMap[sfc.otherIdAgency].templateField.fieldInputLevel.name}">
                                                        <dvn:inlinehelp helpMessage="#{editstudybundle.otherAgencyHelp}" linkText="#{editstudybundle.otherAgencyLabel}" heading="#{editstudybundle.otherAgencyHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif" rendered="#{TemplateFormPage.studyMap[sfc.otherIdAgency].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif" rendered="#{TemplateFormPage.studyMap[sfc.otherIdAgency].recommended}"/>
                                                        <f:verbatim><br/></f:verbatim> 
                                                        <h:inputText maxlength="255" binding="#{TemplateFormPage.inputOtherIdAgency}"     validator ="#{TemplateFormPage.validateStudyOtherId}" 
                                                                     id="input_otherIdAgency" value="#{currentRow.agency}"  required="#{TemplateFormPage.studyMap[sfc.otherIdAgency].required}"/>
                                                        <h:message styleClass="errorMessage" for="input_otherIdAgency"/> 
                                                    </ui:panelGroup>
                                                    
                                                </ui:panelGroup>
                                            </h:column> 
                                            <h:column id="groupPanel61">
                                                <h:commandButton  image="/resources/icon_add.gif" actionListener="#{TemplateFormPage.addRow}"/> 
                                                <h:commandButton  image="/resources/icon_remove.gif" actionListener="#{TemplateFormPage.removeRow}" /> 
                                            </h:column>
                                        </h:dataTable>
                                    </h:column>
                                </h:dataTable>                                
                                
                                <!-- Authors -->
                                
                                <!-- this is a dummy datatable wrapper used to force data retention on validation error-->
                                <h:dataTable rowClasses="#{TemplateFormPage.authorInputLevel}" cellpadding="0" cellspacing="0" width="100%" value="dummy_datatable" >
                                    <h:column>                                          
                                        <h:dataTable cellpadding="0" cellspacing="0"
                                                     columnClasses="vdcEditStudyCol1b, vdcEditStudyCol2, vdcEditStudyCol3"
                                                     binding="#{TemplateFormPage.dataTableAuthors}" 
                                                     value="#{TemplateFormPage.template.metadata.studyAuthors}" var="currentAuthor" width="100%"  >
                                            
                                            <h:column>
                                                <ui:panelGroup block="true" styleClass="vdcEditStudyField"> 
                                                    <dvn:inlinehelp helpMessage="#{editstudybundle.authorHelp}" linkText="#{editstudybundle.authorLabel}" heading="#{editstudybundle.authorHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                    <h:graphicImage  value="/resources/icon_required.gif" rendered="#{TemplateFormPage.authorInputLevel=='required'}"/>
                                                    <h:graphicImage  value="/resources/icon_recommended.gif" rendered="#{TemplateFormPage.authorInputLevel=='recommended'}"/>
                                                </ui:panelGroup>
                                            </h:column>
                                            
                                            <h:column>
                                                <ui:panelGroup block="true" >
                                                    
                                                    <ui:panelGroup styleClass="#{TemplateFormPage.studyMap[sfc.authorName].templateField.fieldInputLevel.name}">
                                                        <dvn:inlinehelp helpMessage="#{editstudybundle.authorNameHelp}" linkText="#{editstudybundle.authorNameLabel}" heading="#{editstudybundle.authorNameHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif" rendered="#{TemplateFormPage.studyMap[sfc.authorName].required}"/>
                                                        <h:graphicImage value="/resources/icon_recommended.gif" rendered="#{TemplateFormPage.studyMap[sfc.authorName].recommended}"/>
                                                        <f:verbatim><br/></f:verbatim>
                                                        <h:inputText id="input_authorName" 
                                                                     value="#{currentAuthor.name}" 
                                                                     size="45" 
                                                                     maxlength="255"
                                                                     binding="#{TemplateFormPage.inputAuthorName}"                                                      
                                                                     required="#{TemplateFormPage.studyMap[sfc.authorName].required}" /> 
                                                        <h:outputText value="(firstName LastName)"/>
                                                        <h:message styleClass="errorMessage" for="input_authorName"/>
                                                        <f:verbatim><br/></f:verbatim>
                                                    </ui:panelGroup>
                                                    <ui:panelGroup styleClass="#{TemplateFormPage.studyMap[sfc.authorAffiliation].templateField.fieldInputLevel.name}">
                                                        <dvn:inlinehelp helpMessage="#{editstudybundle.authorAffiliationHelp}" linkText="#{editstudybundle.authorAffiliationLabel}" heading="#{editstudybundle.authorAffiliationHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif" rendered="#{TemplateFormPage.studyMap[sfc.authorAffiliation].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif" rendered="#{TemplateFormPage.studyMap[sfc.authorAffiliation].recommended}"/>
                                                        <f:verbatim><br/></f:verbatim>
                                                        <h:inputText id="input_authorAffiliation" size="45" maxlength="255"
                                                                     binding="#{TemplateFormPage.inputAuthorAffiliation}"
                                                                     value="#{currentAuthor.affiliation}" 
                                                                     validator ="#{TemplateFormPage.validateStudyAuthor}" 
                                                                     required="#{TemplateFormPage.studyMap[sfc.authorAffiliation].required}"
                                                                     styleClass="formHtmlEnabled">
                                                            <f:validator validatorId="XhtmlValidator"/>
                                                        </h:inputText>
                                                        <h:message styleClass="errorMessage" for="input_authorAffiliation"/>
                                                    </ui:panelGroup>
                                                    
                                                    
                                                </ui:panelGroup>
                                            </h:column>
                                            
                                            <h:column>
                                                <h:commandButton   image="/resources/icon_add.gif" actionListener="#{TemplateFormPage.addRow}"/> 
                                                <h:commandButton   image="/resources/icon_remove.gif" actionListener="#{TemplateFormPage.removeRow}" /> 
                                            </h:column>               
                                        </h:dataTable>
                                    </h:column>
                                </h:dataTable>
                                
                                <!--Producers-->   
                                <!-- this is a dummy datatable wrapper used to force data retention on validation error-->
                                <h:dataTable rowClasses="#{TemplateFormPage.producerInputLevel}" cellpadding="0" cellspacing="0" width="100%" value="dummy_datatable" >
                                    <h:column>                                            
                                        <h:dataTable cellpadding="0" cellspacing="0"
                                                     columnClasses="vdcEditStudyCol1b, vdcEditStudyCol2, vdcEditStudyCol3"
                                                     binding="#{TemplateFormPage.dataTableProducers}" 
                                                     value="#{TemplateFormPage.template.metadata.studyProducers}" var="currentRow" width="100%">
                                            
                                            <h:column>
                                                <ui:panelGroup block="true" styleClass="vdcEditStudyField"> 
                                                    <dvn:inlinehelp helpMessage="#{editstudybundle.producerHelp}" linkText="#{editstudybundle.producerLabel}" heading="#{editstudybundle.producerHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                    <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.producerInputLevel=='required'}"/>
                                                    <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.producerInputLevel=='recommended'}"/>
                                                </ui:panelGroup>
                                            </h:column>
                                            <h:column>
                                                <ui:panelGroup block="true"> 
                                                    <ui:panelGroup styleClass="vdcEditStudyGroupField #{TemplateFormPage.studyMap[sfc.producerName].templateField.fieldInputLevel.name}">
                                                        <dvn:inlinehelp helpMessage="#{editstudybundle.producerHelp}" linkText="#{editstudybundle.producerLabel}" heading="#{editstudybundle.producerHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.producerName].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.producerName].recommended}"/>
                                                        <f:verbatim><br/></f:verbatim>
                                                        <h:inputText maxlength="255" binding="#{TemplateFormPage.inputProducerName}" id="input_producerName" value="#{currentRow.name}" size="45" required="#{TemplateFormPage.studyMap[sfc.producerName].required}"/>
                                                        <h:message styleClass="errorMessage" for="input_producerName"/>
                                                        <f:verbatim><br/></f:verbatim>
                                                    </ui:panelGroup>
                                                    <ui:panelGroup styleClass="vdcEditStudyGroupField  #{TemplateFormPage.studyMap[sfc.producerAffiliation].templateField.fieldInputLevel.name}">
                                                        <dvn:inlinehelp helpMessage="#{editstudybundle.producerAffiliationHelp}" linkText="#{editstudybundle.producerAffiliationLabel}" heading="#{editstudybundle.producerAffiliationHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.producerAffiliation].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.producerAffiliation].recommended}"/>
                                                        <f:verbatim><br/></f:verbatim>
                                                        <h:inputText  maxlength="255" validator ="#{TemplateFormPage.validateStudyProducer}"  binding="#{TemplateFormPage.inputProducerAffiliation}"    id="input_producerAffiliation" size="45" value="#{currentRow.affiliation}" required="#{TemplateFormPage.studyMap[sfc.producerAffiliation].required}"
                                                                      styleClass="formHtmlEnabled">
                                                            <f:validator validatorId="XhtmlValidator"/>
                                                        </h:inputText>
                                                        <h:message styleClass="errorMessage" for="input_producerAffiliation"/>
                                                        <f:verbatim><br /></f:verbatim>
                                                    </ui:panelGroup>
                                                    <ui:panelGroup styleClass="vdcEditStudyGroupField #{TemplateFormPage.studyMap[sfc.producerAbbreviation].templateField.fieldInputLevel.name}">
                                                        <dvn:inlinehelp helpMessage="#{editstudybundle.producerAbbreviationHelp}" linkText="#{editstudybundle.producerAbbreviationLabel}" heading="#{editstudybundle.producerAbbreviationHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.producerAbbreviation].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.producerAbbreviation].recommended}"/>
                                                        <f:verbatim><br/></f:verbatim>
                                                        <h:inputText maxlength="255" validator ="#{TemplateFormPage.validateStudyProducer}"   binding="#{TemplateFormPage.inputProducerAbbreviation}"  id="input_producerAbbreviation" value="#{currentRow.abbreviation}" required="#{TemplateFormPage.studyMap[sfc.producerAbbreviation].required}"/>
                                                        <h:message styleClass="errorMessage" for="input_producerAbbreviation"/>
                                                        <f:verbatim><br /></f:verbatim>
                                                    </ui:panelGroup>
                                                    <ui:panelGroup styleClass="vdcEditStudyGroupField #{TemplateFormPage.studyMap[sfc.producerURL].templateField.fieldInputLevel.name}">
                                                        <dvn:inlinehelp helpMessage="#{editstudybundle.producerURLHelp}" linkText="#{editstudybundle.producerURLLabel}" heading="#{editstudybundle.producerURLHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.producerURL].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.producerURL].recommended}"/>
                                                        <f:verbatim><br /></f:verbatim>
                                                        <h:inputText id="input_producerURL" 
                                                                     validator ="#{TemplateFormPage.validateStudyProducer}"
                                                                     binding="#{TemplateFormPage.inputProducerUrl}" 
                                                                     value="#{currentRow.url}" 
                                                                     size="45" 
                                                                     maxlength="255"
                                                                     required="#{TemplateFormPage.studyMap[sfc.producerURL].required}"
                                                                     >
                                                            <f:validator validatorId="UrlValidator"/>
                                                        </h:inputText>
                                                        <h:message styleClass="errorMessage" for="input_producerURL"/>
                                                        <f:verbatim><br /></f:verbatim>
                                                    </ui:panelGroup>
                                                    <ui:panelGroup styleClass="vdcEditStudyGroupField  #{TemplateFormPage.studyMap[sfc.producerLogo].templateField.fieldInputLevel.name}">
                                                        <dvn:inlinehelp helpMessage="#{editstudybundle.producerLogoHelp}" linkText="#{editstudybundle.producerLogoLabel}" heading="#{editstudybundle.producerLogoHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.producerLogo].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.producerLogo].recommended}"/>
                                                        <f:verbatim><br /></f:verbatim>
                                                        <h:inputText id="input_producerLogo" 
                                                                     binding="#{TemplateFormPage.inputProducerLogo}" 
                                                                     validator ="#{TemplateFormPage.validateStudyProducer}"
                                                                     value="#{currentRow.logo}" 
                                                                     size="45" 
                                                                     maxlength="255"
                                                                     required="#{TemplateFormPage.studyMap[sfc.producerLogo].required}"
                                                                     >
                                                            <f:validator validatorId="UrlValidator"/>
                                                        </h:inputText>
                                                        <h:message styleClass="errorMessage" for="input_producerLogo"/>
                                                        <f:verbatim><br /></f:verbatim>
                                                    </ui:panelGroup>
                                                </ui:panelGroup>
                                            </h:column>
                                            <h:column>
                                                <h:commandButton   image="/resources/icon_add.gif" actionListener="#{TemplateFormPage.addRow}"/> 
                                                <h:commandButton   image="/resources/icon_remove.gif" actionListener="#{TemplateFormPage.removeRow}" /> 
                                            </h:column>
                                        </h:dataTable>
                                    </h:column>
                                </h:dataTable>
                                
                                <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.productionDate].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >
                                    <ui:panelGroup id="groupPanel42"  styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.productionDateHelp}" linkText="#{editstudybundle.productionDateLabel}" heading="#{editstudybundle.productionDateHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.productionDate].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.productionDate].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup id="groupPanel19" >
                                        <h:inputText id="input_productionDate" styleClass="formDateFormat" value="#{TemplateFormPage.template.metadata.productionDate}"  required="#{TemplateFormPage.studyMap[sfc.productionDate].required}">
                                            <f:validator validatorId="DateValidator"/>    
                                        </h:inputText>
                                        <h:outputText value=" (Enter date as YYYY or YYYY-MM or YYYY-MM-DD)"/>
                                        <h:message styleClass="errorMessage" for="input_productionDate"><f:param name="x" value="Production Date:" /> </h:message>
                                    </ui:panelGroup>
                                </h:panelGrid>      
                                
                                <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.productionPlace].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >   
                                    <ui:panelGroup id="groupPanel43"   styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.productionPlaceHelp}" linkText="#{editstudybundle.productionPlaceLabel}" heading="#{editstudybundle.productionPlaceHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.productionPlace].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.productionPlace].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup id="groupPanel20" >
                                        <h:inputText id="input_productionPlace" size="45"  value="#{TemplateFormPage.template.metadata.productionPlace}" required="#{TemplateFormPage.studyMap[sfc.productionPlace].required}"/>
                                        <h:message styleClass="errorMessage" for="input_productionPlace"/>
                                    </ui:panelGroup>
                                </h:panelGrid>     
                                
                                <!-- Software -->        
                                <!-- this is a dummy datatable wrapper used to force data retention on validation error-->
                                <h:dataTable rowClasses="#{TemplateFormPage.softwareInputLevel}" cellpadding="0" cellspacing="0" width="100%" value="dummy_datatable" >
                                    <h:column>
                                        <h:dataTable cellpadding="0" cellspacing="0"
                                                     columnClasses="vdcEditStudyCol1b, vdcEditStudyCol2, vdcEditStudyCol3"
                                                     binding="#{TemplateFormPage.dataTableSoftware}" 
                                                     value="#{TemplateFormPage.template.metadata.studySoftware}" var="currentRow" width="100%" >
                                            
                                            <h:column>
                                                <ui:panelGroup block="true" styleClass="vdcEditStudyField" >  
                                                     <dvn:inlinehelp helpMessage="#{editstudybundle.softwareNameHelp}" linkText="#{editstudybundle.softwareNameLabel}" heading="#{editstudybundle.softwareNameHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                    <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.softwareInputLevel=='required'}"/>
                                                    <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.softwareInputLevel=='recommended'}"/>
                                                </ui:panelGroup>
                                            </h:column>
                                            <h:column>
                                                <ui:panelGroup block="true">
                                                    <ui:panelGroup styleClass="#{TemplateFormPage.studyMap[sfc.softwareName].templateField.fieldInputLevel.name}">
                                                        <dvn:inlinehelp helpMessage="#{editstudybundle.softwareNameHelp}" linkText="#{editstudybundle.softwareNameLabel}" heading="#{editstudybundle.softwareNameHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.softwareName].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.softwareName].recommended}"/>
                                                        <f:verbatim><br /></f:verbatim>
                                                        <h:inputText maxlength="255" binding="#{TemplateFormPage.inputSoftwareName}" id="input_softwareName" size="45"  value="#{currentRow.name}" required="#{TemplateFormPage.studyMap[sfc.softwareName].required}"/>
                                                        <h:message styleClass="errorMessage" for="input_softwareName"/>  
                                                        <f:verbatim><br /></f:verbatim>
                                                    </ui:panelGroup>
                                                    <ui:panelGroup styleClass="vdcEditStudyGroupField #{TemplateFormPage.studyMap[sfc.softwareVersion].templateField.fieldInputLevel.name}">
                                                        <dvn:inlinehelp helpMessage="#{editstudybundle.softwareVersionHelp}" linkText="#{editstudybundle.softwareVersionLabel}" heading="#{editstudybundle.softwareVersionHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.softwareVersion].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.softwareVersion].recommended}"/>
                                                        <f:verbatim><br /></f:verbatim>
                                                        <h:inputText maxlength="255" binding="#{TemplateFormPage.inputSoftwareVersion}" validator ="#{TemplateFormPage.validateStudySoftware}"  id="input_softwareVersion" value="#{currentRow.softwareVersion}" required="#{TemplateFormPage.studyMap[sfc.softwareVersion].required}"/>
                                                        <h:message styleClass="errorMessage" for="input_softwareVersion"/> 
                                                        <f:verbatim><br /></f:verbatim>
                                                    </ui:panelGroup>                                                                                  
                                                </ui:panelGroup>
                                            </h:column> 
                                            <h:column>
                                                <h:commandButton   image="/resources/icon_add.gif" actionListener="#{TemplateFormPage.addRow}"/> 
                                                <h:commandButton   image="/resources/icon_remove.gif" actionListener="#{TemplateFormPage.removeRow}" /> 
                                            </h:column>                                       
                                        </h:dataTable>
                                    </h:column>
                                </h:dataTable>                                
                                <h:panelGrid cellpadding="0" cellspacing="0" styleClass="#{TemplateFormPage.studyMap[sfc.fundingAgency].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"
                                             width="100%">
                                    <ui:panelGroup   styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.fundingAgencyHelp}" linkText="#{editstudybundle.fundingAgencyLabel}" heading="#{editstudybundle.fundingAgencyHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.fundingAgency].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.fundingAgency].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup >
                                        <h:inputText id="input_fundingAgency" size="45"  value="#{TemplateFormPage.template.metadata.fundingAgency}" required="#{TemplateFormPage.studyMap[sfc.fundingAgency].required}"/>
                                        <h:message styleClass="errorMessage" for="input_fundingAgency"/>                                
                                    </ui:panelGroup>
                                    
                                </h:panelGrid>        
                                <!-- this is a dummy datatable wrapper used to force data retention on validation error-->
                                <h:dataTable rowClasses="#{TemplateFormPage.grantInputLevel}"  cellpadding="0" cellspacing="0" width="100%" value="dummy_datatable" >
                                    <h:column>                                
                                        <h:dataTable cellpadding="0" cellspacing="0"
                                                     columnClasses="vdcEditStudyCol1b, vdcEditStudyCol2, vdcEditStudyCol3"
                                                     binding="#{TemplateFormPage.dataTableGrants}" 
                                                     value="#{TemplateFormPage.template.metadata.studyGrants}" var="currentRow" width="100%"  
                                                     >
                                            
                                            <h:column>
                                                <ui:panelGroup block="true" styleClass="vdcEditStudyField" > 
                                                    <dvn:inlinehelp helpMessage="#{editstudybundle.grantNumberHelp}" linkText="#{editstudybundle.grantNumberLabel}" heading="#{editstudybundle.grantNumberHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                    <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.grantInputLevel=='required'}"/>
                                                    <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.grantInputLevel=='recommended'}"/>
                                                </ui:panelGroup>                                              
                                            </h:column>
                                            <h:column>
                                                <ui:panelGroup block="true">
                                                    <ui:panelGroup styleClass="vdcEditStudyGroupField #{TemplateFormPage.studyMap[sfc.grantNumber].templateField.fieldInputLevel.name}">
                                                        <dvn:inlinehelp helpMessage="#{editstudybundle.grantNumberHelp}" linkText="#{editstudybundle.grantNumberLabel}" heading="#{editstudybundle.grantNumberHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.grantNumber].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.grantNumber].recommended}"/>
                                                        <f:verbatim><br/></f:verbatim>
                                                        <h:inputText maxlength="255" immediate="true" binding="#{TemplateFormPage.inputGrantNumber}" id="input_grantNumber" size="45"  value="#{currentRow.number}" required="#{TemplateFormPage.studyMap[sfc.grantNumber].required}"/>
                                                        <h:message styleClass="errorMessage" for="input_grantNumber"/> 
                                                        <f:verbatim><br/></f:verbatim>
                                                    </ui:panelGroup>
                                                    <ui:panelGroup styleClass="vdcEditStudyGroupField #{TemplateFormPage.studyMap[sfc.grantNumberAgency].templateField.fieldInputLevel.name}">
                                                        <dvn:inlinehelp helpMessage="#{editstudybundle.grantingAgencyHelp}" linkText="#{editstudybundle.grantingAgencyLabel}" heading="#{editstudybundle.grantingAgencyHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.grantNumberAgency].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.grantNumberAgency].recommended}"/>
                                                        <f:verbatim><br/></f:verbatim>
                                                        <h:inputText  maxlength="255" immediate="true" binding="#{TemplateFormPage.inputGrantAgency}" validator ="#{TemplateFormPage.validateStudyGrant}" id="input_grantNumberAgency" size="45" value="#{currentRow.agency}" required="#{TemplateFormPage.studyMap[sfc.grantNumberAgency].required}"/>
                                                        <f:verbatim><br/></f:verbatim>
                                                        <h:message styleClass="errorMessage" for="input_grantNumberAgency"/>
                                                    </ui:panelGroup>                                                                                    
                                                </ui:panelGroup>
                                            </h:column>
                                            <h:column>
                                                <h:commandButton   image="/resources/icon_add.gif" actionListener="#{TemplateFormPage.addRow}"/> 
                                                <h:commandButton   image="/resources/icon_remove.gif" actionListener="#{TemplateFormPage.removeRow}" /> 
                                            </h:column>
                                        </h:dataTable>
                                    </h:column>
                                </h:dataTable>                                
                                
                                <!-- this is a dummy datatable wrapper used to force data retention on validation error-->
                                <h:dataTable rowClasses="#{TemplateFormPage.distributorInputLevel}"  cellpadding="0" cellspacing="0" width="100%" value="dummy_datatable" >
                                    <h:column>                                
                                        <h:dataTable cellpadding="0" cellspacing="0"  
                                                     columnClasses="vdcEditStudyCol1b, vdcEditStudyCol2, vdcEditStudyCol3"
                                                     binding="#{TemplateFormPage.dataTableDistributors}" 
                                                     value="#{TemplateFormPage.template.metadata.studyDistributors}" var="currentRow" width="100%"  >
                                            
                                            <h:column>
                                                <ui:panelGroup block="true" styleClass="vdcEditStudyField" > 
                                                    <dvn:inlinehelp helpMessage="#{editstudybundle.distributorHelp}" linkText="#{editstudybundle.distributorLabel}" heading="#{editstudybundle.distributorHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                    <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.distributorInputLevel=='required'}"/>
                                                    <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.distributorInputLevel=='recommended'}"/>
                                                </ui:panelGroup>
                                            </h:column>
                                            <h:column>
                                                <ui:panelGroup block="true" >
                                                    <ui:panelGroup styleClass="#{TemplateFormPage.studyMap[sfc.distributorName].templateField.fieldInputLevel.name}">
                                                        <dvn:inlinehelp helpMessage="#{editstudybundle.distributorHelp}" linkText="#{editstudybundle.distributorLabel}" heading="#{editstudybundle.distributorHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.distributorName].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.distributorName].recommended}"/>
                                                        <f:verbatim><br/></f:verbatim>
                                                        <h:inputText maxlength="255" binding="#{TemplateFormPage.inputDistributorName}" id="input_distributorName" value="#{currentRow.name}" size="45" required="#{TemplateFormPage.studyMap[sfc.distributorName].required}"/>
                                                        <h:message styleClass="errorMessage" for="input_distributorName"/> 
                                                        <f:verbatim><br/></f:verbatim>
                                                    </ui:panelGroup>
                                                    <ui:panelGroup styleClass="vdcEditStudyGroupField #{TemplateFormPage.studyMap[sfc.distributorAffiliation].templateField.fieldInputLevel.name}">
                                                        <dvn:inlinehelp helpMessage="#{editstudybundle.distributorAffiliationHelp}" linkText="#{editstudybundle.distributorAffiliationLabel}" heading="#{editstudybundle.distributorAffiliationHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.distributorAffiliation].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.distributorAffiliation].recommended}"/>
                                                        <f:verbatim><br/></f:verbatim>
                                                        <h:inputText maxlength="255" id="input_distributorAffiliation" validator ="#{TemplateFormPage.validateStudyDistributor}" binding="#{TemplateFormPage.inputDistributorAffiliation}" size="45"
                                                                     value="#{currentRow.affiliation}" 
                                                                     required="#{TemplateFormPage.studyMap[sfc.distributorAffiliation].required}" 
                                                                     styleClass="formHtmlEnabled">
                                                            <f:validator validatorId="XhtmlValidator"/>
                                                        </h:inputText>
                                                        <h:message styleClass="errorMessage" for="input_distributorAffiliation"/> 
                                                        <f:verbatim><br /></f:verbatim>
                                                    </ui:panelGroup>                                                   
                                                    <ui:panelGroup styleClass="vdcEditStudyGroupField #{TemplateFormPage.studyMap[sfc.distributorAbbreviation].templateField.fieldInputLevel.name}">
                                                        <dvn:inlinehelp helpMessage="#{editstudybundle.distributorAbbreviationHelp}" linkText="#{editstudybundle.distributorAbbreviationLabel}" heading="#{editstudybundle.distributorAbbreviationHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.distributorAbbreviation].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.distributorAbbreviation].recommended}"/>
                                                        <f:verbatim><br/></f:verbatim>
                                                        <h:inputText maxlength="255" validator ="#{TemplateFormPage.validateStudyDistributor}" binding="#{TemplateFormPage.inputDistributorAbbreviation}" id="input_distributorAbbreviation" value="#{currentRow.abbreviation}"  required="#{TemplateFormPage.studyMap[sfc.distributorAbbreviation].required}"/>
                                                        <h:message styleClass="errorMessage" for="input_distributorAbbreviation"/>  
                                                        <f:verbatim><br/></f:verbatim>
                                                    </ui:panelGroup>
                                                    
                                                    <ui:panelGroup styleClass="vdcEditStudyGroupField #{TemplateFormPage.studyMap[sfc.distributorURL].templateField.fieldInputLevel.name}">
                                                        <dvn:inlinehelp helpMessage="#{editstudybundle.distributorURLHelp}" linkText="#{editstudybundle.distributorURLLabel}" heading="#{editstudybundle.distributorURLHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.distributorURL].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.distributorURL].recommended}"/>
                                                        <f:verbatim><br/></f:verbatim>
                                                        <h:inputText id="input_distributorURL" 
                                                                     validator ="#{TemplateFormPage.validateStudyDistributor}"
                                                                     binding="#{TemplateFormPage.inputDistributorUrl}" 
                                                                     value="#{currentRow.url}" 
                                                                     size="45"  
                                                                     maxlength="255"
                                                                     required="#{TemplateFormPage.studyMap[sfc.distributorURL].required}"
                                                                     >
                                                            <f:validator validatorId="UrlValidator"/>
                                                        </h:inputText>
                                                        <h:message styleClass="errorMessage" for="input_distributorURL"/>
                                                        <f:verbatim><br/></f:verbatim>
                                                    </ui:panelGroup>
                                                    
                                                    <ui:panelGroup styleClass="vdcEditStudyGroupField  #{TemplateFormPage.studyMap[sfc.distributorLogo].templateField.fieldInputLevel.name}">
                                                        <dvn:inlinehelp helpMessage="#{editstudybundle.distributorLogoHelp}" linkText="#{editstudybundle.distributorLogoLabel}" heading="#{editstudybundle.distributorLogoHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.distributorLogo].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.distributorLogo].recommended}"/>
                                                        <f:verbatim><br/></f:verbatim>
                                                        <h:inputText binding="#{TemplateFormPage.inputDistributorLogo}" id="input_distributorLogo"  
                                                                     validator ="#{TemplateFormPage.validateStudyDistributor}"
                                                                     value="#{currentRow.logo}" 
                                                                     size="45"  
                                                                     maxlength="255"
                                                                     required="#{TemplateFormPage.studyMap[sfc.distributorLogo].required}"
                                                                     >
                                                            <f:validator validatorId="UrlValidator"/>
                                                        </h:inputText>
                                                        <h:message styleClass="errorMessage" for="input_distributorLogo"/>
                                                        <f:verbatim><br/></f:verbatim>
                                                    </ui:panelGroup>
                                                    
                                                </ui:panelGroup>
                                            </h:column>
                                            <h:column>
                                                <h:commandButton   image="/resources/icon_add.gif" actionListener="#{TemplateFormPage.addRow}"/> 
                                                <h:commandButton   image="/resources/icon_remove.gif" actionListener="#{TemplateFormPage.removeRow}" /> 
                                            </h:column>
                                        </h:dataTable>
                                    </h:column>
                                </h:dataTable>
                                
                                
                                <h:panelGrid styleClass="#{TemplateFormPage.distributorInputLevel}" cellpadding="0" cellspacing="0"  
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >
                                    <ui:panelGroup   styleClass="vdcEditStudyField #{TemplateFormPage.distributorInputLevel}" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.distributorContactHelp}" linkText="#{editstudybundle.distributorContactLabel}" heading="#{editstudybundle.distributorContactHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.distributorContact].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.distributorContact].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup >
                                        <ui:panelGroup styleClass=" #{TemplateFormPage.studyMap[sfc.distributorContact].templateField.fieldInputLevel.name}">
                                            <dvn:inlinehelp helpMessage="#{editstudybundle.distributorContactHelp}" linkText="#{editstudybundle.distributorContactLabel}" heading="#{editstudybundle.distributorContactHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                            <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.distributorContact].required}"/>
                                            <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.distributorContact].recommended}"/>
                                            <f:verbatim><br/></f:verbatim>
                                            
                                            <h:inputText binding ="#{TemplateFormPage.inputDistributorContact}" id="input_distributorContact" size="45"  value="#{TemplateFormPage.template.metadata.distributorContact}"  required="#{TemplateFormPage.studyMap[sfc.distributorContact].required}"/>
                                            <h:message styleClass="errorMessage" for="input_distributorContact"/>    
                                            <f:verbatim><br/></f:verbatim>
                                        </ui:panelGroup>
                                        
                                        <ui:panelGroup styleClass="vdcEditStudyGroupField #{TemplateFormPage.studyMap[sfc.distributorContactAffiliation].templateField.fieldInputLevel.name}">
                                            <dvn:inlinehelp helpMessage="#{editstudybundle.distributorContactAffiliationHelp}" linkText="#{editstudybundle.distributorContactAffiliationLabel}" heading="#{editstudybundle.distributorContactAffiliationHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                            <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.distributorContactAffiliation].required}"/>
                                            <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.distributorContactAffiliation].recommended}"/>
                                            <f:verbatim><br/></f:verbatim>
                                            <h:inputText id="distributorContactAffiliation"  
                                                         binding ="#{TemplateFormPage.inputDistributorContactAffiliation}" 
                                                         value="#{TemplateFormPage.template.metadata.distributorContactAffiliation}" 
                                                         validator="#{TemplateFormPage.validateDistributorContact}"
                                                         required="#{TemplateFormPage.studyMap[sfc.distributorContactAffiliation].required}"
                                                         styleClass="formHtmlEnabled">
                                                <f:validator validatorId="XhtmlValidator"/>
                                            </h:inputText>   
                                            <h:message styleClass="errorMessage" for="distributorContactAffiliation"/>
                                            <f:verbatim><br/></f:verbatim>
                                        </ui:panelGroup>
                                        
                                        <ui:panelGroup styleClass="vdcEditStudyGroupField #{TemplateFormPage.studyMap[sfc.distributorContactEmail].templateField.fieldInputLevel.name}" >
                                            <dvn:inlinehelp helpMessage="#{editstudybundle.distributorContactEmailHelp}" linkText="#{editstudybundle.distributorContactEmailLabel}" heading="#{editstudybundle.distributorContactEmailHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                            <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.distributorContactEmail].required}"/>
                                            <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.distributorContactEmail].recommended}"/>
                                            <f:verbatim><br/></f:verbatim>
                                            <h:inputText  binding ="#{TemplateFormPage.inputDistributorContactEmail}"  id="input_distributorContactEmail" size="45" value="#{TemplateFormPage.template.metadata.distributorContactEmail}" required="#{TemplateFormPage.studyMap[sfc.distributorContactEmail].required}"/>
                                            <h:message styleClass="errorMessage" for="input_distributorContactEmail"/>    
                                            <f:verbatim><br/></f:verbatim>
                                        </ui:panelGroup>
                                    </ui:panelGroup>                                          
                                 </h:panelGrid>
                                 <h:panelGrid  styleClass="#{TemplateFormPage.studyMap[sfc.distributionDate].templateField.fieldInputLevel.name}"  cellpadding="0" cellspacing="0"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2" width="100%">
                     
                                    
                                    <ui:panelGroup id="groupPanel49" styleClass="vdcEditStudyField #{TemplateFormPage.studyMap[sfc.distributionDate].templateField.fieldInputLevel.name}" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.distributionDateHelp}" linkText="#{editstudybundle.distributionDateLabel}" heading="#{editstudybundle.distributionDateHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.distributionDate].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.distributionDate].recommended}"/>
                                        
                                    </ui:panelGroup>
                                    <ui:panelGroup id="groupPanel26" styleClass="#{TemplateFormPage.studyMap[sfc.distributionDate].templateField.fieldInputLevel.name}">
                                        <h:inputText id="input_distributionDate" styleClass="formDateFormat" value="#{TemplateFormPage.template.metadata.distributionDate}"  required="#{TemplateFormPage.studyMap[sfc.distributionDate].required}">
                                            <f:validator validatorId="DateValidator"/>    
                                        </h:inputText>
                                        <h:outputText value=" (Enter date as YYYY or YYYY-MM or YYYY-MM-DD)"/>
                                        <h:message styleClass="errorMessage" for="input_distributionDate"/>                                
                                    </ui:panelGroup>
                                    </h:panelGrid>
                                    <h:panelGrid cellpadding="0" cellspacing="0" styleClass="#{TemplateFormPage.studyMap[sfc.depositor].templateField.fieldInputLevel.name}" columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2" width="100%">
   
                                        <ui:panelGroup id="groupPanel50"  styleClass="vdcEditStudyField"  block="true">
                                            <dvn:inlinehelp helpMessage="#{editstudybundle.depositorHelp}" linkText="#{editstudybundle.depositorLabel}" heading="#{editstudybundle.depositorHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                            <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.depositor].required}"/>
                                            <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.depositor].recommended}"/>
                                        </ui:panelGroup>
                                        <ui:panelGroup id="groupPanel27" styleClass="#{TemplateFormPage.studyMap[sfc.depositor].templateField.fieldInputLevel.name}">
                                            <h:inputText id="input_depositor" size="45"  value="#{TemplateFormPage.template.metadata.depositor}" required="#{TemplateFormPage.studyMap[sfc.depositor].required}"/>
                                            <h:message styleClass="errorMessage" for="input_depositor"/>                                                                                
                                        </ui:panelGroup>
                                    </h:panelGrid> 
                                    <h:panelGrid cellpadding="0" cellspacing="0" styleClass="#{TemplateFormPage.studyMap[sfc.dateOfDeposit].templateField.fieldInputLevel.name}" columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2" width="100%">
                                    <ui:panelGroup id="groupPanel51"  styleClass="vdcEditStudyField #{TemplateFormPage.studyMap[sfc.dateOfDeposit].templateField.fieldInputLevel.name}">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.depositDateHelp}" linkText="#{editstudybundle.depositDateLabel}" heading="#{editstudybundle.depositDateHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.dateOfDeposit].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.dateOfDeposit].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup id="groupPanel28" styleClass="vdcEditStudyField #{TemplateFormPage.studyMap[sfc.dateOfDeposit].templateField.fieldInputLevel.name}">
                                        <h:inputText id="input_dateOfDeposit" styleClass="formDateFormat" value="#{TemplateFormPage.template.metadata.dateOfDeposit}"  required="#{TemplateFormPage.studyMap[sfc.dateOfDeposit].required}">
                                            <f:validator validatorId="DateValidator"/>
                                        </h:inputText>
                                        <h:outputText value=" (Enter date as YYYY or YYYY-MM or YYYY-MM-DD)"/>
                                        <h:message styleClass="errorMessage" for="input_dateOfDeposit"/>                                                                                
                                    </ui:panelGroup>                                        
                                    
                                     </h:panelGrid> 
                                    <h:panelGrid cellpadding="0" cellspacing="0" styleClass="#{TemplateFormPage.seriesInputLevel}" columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2" width="100%">
                                  
                                    <ui:panelGroup id="groupPanel52"  styleClass="vdcEditStudyField #{TemplateFormPage.seriesInputLevel}" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.seriesHelp}" linkText="#{editstudybundle.seriesLabel}" heading="#{editstudybundle.seriesHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.seriesInputLevel=='required'}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.seriesInputLevel=='recommended'}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup >
                                        <ui:panelGroup id="groupPanel29" styleClass="vdcEditStudyField #{TemplateFormPage.studyMap[sfc.seriesName].templateField.fieldInputLevel.name}">
                                            <dvn:inlinehelp helpMessage="#{editstudybundle.seriesHelp}" linkText="#{editstudybundle.seriesLabel}" heading="#{editstudybundle.seriesHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                            <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.seriesName].required}"/>
                                            <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.seriesName].recommended}"/>
                                            <f:verbatim><br/></f:verbatim>
                                            <h:inputText  binding="#{TemplateFormPage.inputSeries}" id="input_seriesName" 
                                                          value="#{TemplateFormPage.template.metadata.seriesName}"  
                                                          required="#{TemplateFormPage.studyMap[sfc.seriesName].required}"                                                     
                                                          styleClass="formHtmlEnabled">
                                                <f:validator validatorId="XhtmlValidator"/>
                                            </h:inputText>                                         
                                            <h:message styleClass="errorMessage" for="input_seriesName"/>  
                                            <f:verbatim><br/></f:verbatim>
                                        </ui:panelGroup>
                                        <ui:panelGroup styleClass="vdcEditStudyGroupField #{TemplateFormPage.studyMap[sfc.seriesName].templateField.fieldInputLevel.name}" >
                                            <dvn:inlinehelp helpMessage="#{editstudybundle.seriesInformationHelp}" linkText="#{editstudybundle.seriesInformationLabel}" heading="#{editstudybundle.seriesInformationHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                            <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.seriesInformation].required}"/>
                                            <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.seriesInformation].recommended}"/>
                                            <f:verbatim><br/></f:verbatim>
                                            <h:inputText  binding ="#{TemplateFormPage.inputSeriesInformation}" id="input_seriesInformation" 
                                                          size="45"  
                                                          value="#{TemplateFormPage.template.metadata.seriesInformation}"  
                                                          required="#{TemplateFormPage.studyMap[sfc.seriesInformation].required}"
                                                          validator="#{TemplateFormPage.validateSeries}"
                                                          styleClass="formHtmlEnabled">
                                                <f:validator validatorId="XhtmlValidator"/>
                                            </h:inputText>
                                            <h:message styleClass="errorMessage" for="input_seriesInformation"/>  
                                            <f:verbatim><br/></f:verbatim>
                                        </ui:panelGroup >
                                    </ui:panelGroup>
                                    </h:panelGrid> 
                                    <h:panelGrid cellpadding="0" cellspacing="0" styleClass="#{TemplateFormPage.versionInputLevel}" columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2" width="100%">
                                  
                                    <ui:panelGroup id="groupPanel53"   styleClass="#{TemplateFormPage.versionInputLevel}" block="true" >
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.seriesVersionHelp}" linkText="#{editstudybundle.seriesVersionLabel}" heading="#{editstudybundle.seriesVersionHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.inputVersion=='required'}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.inputVersion=='recommended'}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup id="groupPanel30"  >
                                        <ui:panelGroup styleClass= "#{TemplateFormPage.studyMap[sfc.studyVersion].templateField.fieldInputLevel.name}">
                                            <dvn:inlinehelp helpMessage="#{editstudybundle.seriesVersionHelp}" linkText="#{editstudybundle.seriesVersionLabel}" heading="#{editstudybundle.seriesVersionHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>                                           
                                            <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.studyVersion].required}"/>
                                            <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.studyVersion].recommended}"/>
                                            <f:verbatim><br/></f:verbatim>
                                            <h:inputText  binding="#{TemplateFormPage.inputVersion}" id="input_studyVersion"  value="#{TemplateFormPage.template.metadata.studyVersion}"  required="#{TemplateFormPage.studyMap[sfc.studyVersion].required}"/>
                                            <h:message styleClass="errorMessage" for="input_studyVersion"/>  
                                            <f:verbatim><br/></f:verbatim>
                                        </ui:panelGroup>
                                        <ui:panelGroup styleClass="vdcEditStudyGroupField #{TemplateFormPage.studyMap[sfc.versionDate].templateField.fieldInputLevel.name}">
                                            <dvn:inlinehelp helpMessage="#{editstudybundle.seriesVersionDateHelp}" linkText="#{editstudybundle.seriesVersionDateLabel}" heading="#{editstudybundle.seriesVersionDateHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                            <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.versionDate].required}"/>
                                            <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.versionDate].recommended}"/>
                                            <f:verbatim><br/></f:verbatim>
                                            <h:inputText  binding="#{TemplateFormPage.inputVersionDate}" validator="#{TemplateFormPage.validateVersion}" id="input_versionDate" styleClass="formDateFormat" value="#{TemplateFormPage.template.metadata.versionDate}" required="#{TemplateFormPage.studyMap[sfc.versionDate].required}" >
                                                <f:validator validatorId="DateValidator"/>
                                            </h:inputText>
                                            <h:outputText value=" (Enter date as YYYY or YYYY-MM or YYYY-MM-DD)"/>
                                            <h:message styleClass="errorMessage" for="input_versionDate"/>                                

                                        </ui:panelGroup>
                                     </ui:panelGroup>
                                     </h:panelGrid> 
                                     <h:panelGrid cellpadding="0" cellspacing="0" styleClass="#{TemplateFormPage.studyMap[sfc.replicationFor].templateField.fieldInputLevel.name}" columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2" width="100%">
                              
                                    <ui:panelGroup styleClass="vdcEditStudyGroupField #{TemplateFormPage.studyMap[sfc.replicationFor].templateField.fieldInputLevel.name}" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.replicationForHelp}" linkText="#{editstudybundle.replicationForLabel}" heading="#{editstudybundle.replicationForHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.replicationFor].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.replicationFor].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup  styleClass="#{TemplateFormPage.studyMap[sfc.replicationFor].templateField.fieldInputLevel.name}">
                                        
                                        <h:inputTextarea cols="90" rows="4"  
                                                         id="input_replicationFor" 
                                                         value="#{TemplateFormPage.template.metadata.replicationFor}" 
                                                         required="#{TemplateFormPage.studyMap[sfc.replicationFor].required}"
                                                         styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputTextarea>
                                        <h:message styleClass="errorMessage" for="input_replicationFor"/>
                                        <f:verbatim><br /></f:verbatim>
                                    </ui:panelGroup>
                                </h:panelGrid>        
                                
                                <ui:panelGroup block="true" id="groupPanel12" styleClass="vdcStudyInfoHeader #{TemplateFormPage.abstractAndScopeInputLevel}">
                                    <h:outputText id="outputText65" value="Abstract and Scope"/>
                                    
                                </ui:panelGroup>
                                
                                <!-- this is a dummy datatable wrapper used to force data retention on validation error-->
                                <h:dataTable rowClasses="#{TemplateFormPage.abstractInputLevel}" cellpadding="0" cellspacing="0" width="100%" value="dummy_datatable" >
                                    <h:column>                                
                                        <h:dataTable cellpadding="0" cellspacing="0"
                                                     columnClasses="vdcEditStudyCol1b, vdcEditStudyCol2, vdcEditStudyCol3"
                                                     binding="#{TemplateFormPage.dataTableAbstracts}" 
                                                     value="#{TemplateFormPage.template.metadata.studyAbstracts}" var="currentRow" width="100%"
                                                     >
                                            
                                            <h:column>
                                                <ui:panelGroup block="true" styleClass="vdcEditStudyField"> 
                                                    <dvn:inlinehelp helpMessage="#{editstudybundle.abstractHelp}" linkText="#{editstudybundle.abstractLabel}" heading="#{editstudybundle.abstractHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                    <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.abstractInputLevel=='required'}"/>
                                                    <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.abstractInputLevel=='recommended'}"/>
                                                </ui:panelGroup>
                                            </h:column>
                                            <h:column>
                                                <ui:panelGroup block="true">
                                                <ui:panelGroup block="true" styleClass="#{TemplateFormPage.studyMap[sfc.abstractText].templateField.fieldInputLevel.name}">
                                                    <h:outputText value="Note: Copying and pasting from a Word document can create errors when you save this page."/>
                                                    <br />
                                                     <dvn:inlinehelp helpMessage="#{editstudybundle.abstractHelp}" linkText="#{editstudybundle.abstractLabel}" heading="#{editstudybundle.abstractHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                    <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.abstractInputLevel=='required'}"/>
                                                    <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.abstractInputLevel=='recommended'}"/>
                                                    <br/>
                                                    <h:inputTextarea cols="90" rows="12" 
                                                                     id="input_abstractText" 
                                                                     binding="#{TemplateFormPage.inputAbstractText}"
                                                                     value="#{currentRow.text}" 
                                                                     required="#{TemplateFormPage.studyMap[sfc.abstractText].required}"
                                                                     styleClass="formHtmlEnabled">
                                                        <f:validator validatorId="XhtmlValidator"/>
                                                    </h:inputTextarea>
                                                    <h:message styleClass="errorMessage" for="input_abstractText"/>  
                                                </ui:panelGroup>
                                                <ui:panelGroup styleClass="vdcEditStudyGroupField #{TemplateFormPage.studyMap[sfc.abstractDate].templateField.fieldInputLevel.name}">
                                                        <dvn:inlinehelp helpMessage="#{editstudybundle.abstractDateHelp}" linkText="#{editstudybundle.abstractDateLabel}" heading="#{editstudybundle.abstractDateHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.abstractDate].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.abstractDate].recommended}"/>
                                                        <br/>
                                                        <h:inputText binding="#{TemplateFormPage.inputAbstractDate}" validator="#{TemplateFormPage.validateStudyAbstract}" id="input_abstractDate" styleClass="formDateFormat" value="#{currentRow.date}" required="#{TemplateFormPage.studyMap[sfc.abstractDate].required}">
                                                            <f:validator validatorId="DateValidator"/>    
                                                        </h:inputText>
                                                        <h:outputText value=" (Enter date as YYYY or YYYY-MM or YYYY-MM-DD)"/>
                                                        <h:message styleClass="errorMessage" for="input_abstractDate"/> 
                                                        <br/>
                                                    </ui:panelGroup>
                                                  
                                                 </ui:panelGroup>   
                                                 
                                            </h:column>
                                            <h:column>
                                                <h:commandButton   image="/resources/icon_add.gif" actionListener="#{TemplateFormPage.addRow}"/> 
                                                <h:commandButton   image="/resources/icon_remove.gif" actionListener="#{TemplateFormPage.removeRow}" /> 
                                            </h:column>
                                        </h:dataTable>
                                    </h:column>
                                </h:dataTable>    
                                <!-- this is a dummy datatable wrapper used to force data retention on validation error-->
                                <h:dataTable rowClasses="#{TemplateFormPage.keywordInputLevel}" cellpadding="0" cellspacing="0" width="100%" value="dummy_datatable" >
                                    <h:column>                                
                                        <h:dataTable cellpadding="0" cellspacing="0"
                                                     columnClasses="vdcEditStudyCol1b, vdcEditStudyCol2, vdcEditStudyCol3"
                                                     binding="#{TemplateFormPage.dataTableKeywords}" 
                                                     value="#{TemplateFormPage.template.metadata.studyKeywords}" var="currentRow" width="100%" 
                                                     >
                                            
                                            <h:column>
                                                <ui:panelGroup block="true" styleClass="vdcEditStudyField"> 
                                                    <dvn:inlinehelp helpMessage="#{editstudybundle.keywordHelp}" linkText="#{editstudybundle.keywordLabel}" heading="#{editstudybundle.keywordHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                    <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.keywordInputLevel=='required'}"/>
                                                    <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.keywordInputLevel=='recommended'}"/>
                                                </ui:panelGroup>
                                            </h:column>
                                            <h:column>
                                                <ui:panelGroup block="true">
                                                    <ui:panelGroup styleClass="#{TemplateFormPage.studyMap[sfc.keywordValue].templateField.fieldInputLevel.name}">
                                                        <dvn:inlinehelp helpMessage="#{editstudybundle.keywordHelp}" linkText="#{editstudybundle.keywordLabel}" heading="#{editstudybundle.keywordHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.keywordValue].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.keywordValue].recommended}"/>
                                                        <br/>
                                                       <h:inputText binding="#{TemplateFormPage.inputKeywordValue}" id="input_keywordValue" value="#{currentRow.value}" required="#{TemplateFormPage.studyMap[sfc.keywordValue].required}"/>
                                                        <h:message styleClass="errorMessage" for="input_keywordValue"/>   
                                                        <br/>
                                                    </ui:panelGroup>
                                                    <ui:panelGroup styleClass="vdcEditStudyGroupField #{TemplateFormPage.studyMap[sfc.keywordVocab].templateField.fieldInputLevel.name}">
                                                        <dvn:inlinehelp helpMessage="#{editstudybundle.keywordVocabularyHelp}" linkText="#{editstudybundle.keywordVocabularyLabel}" heading="#{editstudybundle.keywordVocabularyHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.keywordVocab].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.keywordVocab].recommended}"/>
                                                        <br/>
                                                        <h:inputText validator ="#{TemplateFormPage.validateStudyKeyword}" binding="#{TemplateFormPage.inputKeywordVocab}" id="input_keywordVocab" value="#{currentRow.vocab}" required="#{TemplateFormPage.studyMap[sfc.keywordVocab].required}" maxlength="255"/>
                                                        <h:message styleClass="errorMessage" for="input_keywordVocab"/>  
                                                        <br/>
                                                    </ui:panelGroup>
                                                                                
                                                    <ui:panelGroup styleClass="vdcEditStudyGroupField #{TemplateFormPage.studyMap[sfc.keywordVocabURI].templateField.fieldInputLevel.name}">
                                                        <dvn:inlinehelp helpMessage="#{editstudybundle.keywordVocabularyURLHelp}" linkText="#{editstudybundle.keywordVocabularyURLLabel}" heading="#{editstudybundle.keywordVocabularyURLHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.keywordVocabURI].required}" />
                                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.keywordVocabURI].recommended}"/>
                                                        <br/>
                                                        <h:inputText id="input_keywordVocabURI" 
                                                                     validator ="#{TemplateFormPage.validateStudyKeyword}"
                                                                     binding="#{TemplateFormPage.inputKeywordVocabUri}"
                                                                     size="45" 
                                                                     maxlength="255"
                                                                     value="#{currentRow.vocabURI}" 
                                                                     required="#{TemplateFormPage.studyMap[sfc.keywordVocabURI].required}"
                                                                     >
                                                            <f:validator validatorId="UrlValidator"/>
                                                        </h:inputText>  
                                                        <h:message styleClass="errorMessage" for="input_keywordVocabURI"/>   
                                                    </ui:panelGroup>                                 
                                                </ui:panelGroup>
                                            </h:column>
                                            <h:column>
                                                <h:commandButton   image="/resources/icon_add.gif" actionListener="#{TemplateFormPage.addRow}"/> 
                                                <h:commandButton   image="/resources/icon_remove.gif" actionListener="#{TemplateFormPage.removeRow}" /> 
                                            </h:column>
                                        </h:dataTable>
                                    </h:column>
                                </h:dataTable>                                
                                <!-- this is a dummy datatable wrapper used to force data retention on validation error-->
                                <h:dataTable rowClasses="#{TemplateFormPage.topicInputLevel}" cellpadding="0" cellspacing="0" width="100%" value="dummy_datatable" >
                                    <h:column>                                
                                        <h:dataTable cellpadding="0" cellspacing="0"
                                                     columnClasses="vdcEditStudyCol1b, vdcEditStudyCol2, vdcEditStudyCol3"
                                                     id="dataTableTopicClass"
                                                     binding="#{TemplateFormPage.dataTableTopicClass}" 
                                                     value="#{TemplateFormPage.template.metadata.studyTopicClasses}" var="currentRow" width="100%"
                                                     >
                                            
                                            <h:column>
                                                <ui:panelGroup block="true" styleClass="vdcEditStudyField"> 
                                                    <dvn:inlinehelp helpMessage="#{editstudybundle.topicClassificationHelp}" linkText="#{editstudybundle.topicClassificationLabel}" heading="#{editstudybundle.topicClassificationHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                    <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.topicInputLevel=='required'}"/>
                                                    <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.topicInputLevel=='recommended'}"/>
                                                </ui:panelGroup>
                                            </h:column>
                                            <h:column>
                                                <ui:panelGroup block="true">
                                                    <ui:panelGroup styleClass="vdcEditStudyGroupField #{TemplateFormPage.studyMap[sfc.topicClassValue].templateField.fieldInputLevel.name}">
                                                        <dvn:inlinehelp helpMessage="#{editstudybundle.topicClassificationHelp}" linkText="#{editstudybundle.topicClassificationLabel}" heading="#{editstudybundle.topicClassificationHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.topicClassValue].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.topicClassValue].recommended}"/>
                                                        <br/>
                                                        <h:inputText  binding="#{TemplateFormPage.inputTopicClassValue}" id="input_topicClassValue" value="#{currentRow.value}" required="#{TemplateFormPage.studyMap[sfc.topicClassValue].required}"/>
                                                        <h:message styleClass="errorMessage" for="input_topicClassValue"/>
                                                        <br/>
                                                    </ui:panelGroup>
                                                    <ui:panelGroup styleClass="vdcEditStudyGroupField #{TemplateFormPage.studyMap[sfc.topicClassVocab].templateField.fieldInputLevel.name}">
                                                        <dvn:inlinehelp helpMessage="#{editstudybundle.topicClassVocabularyHelp}" linkText="#{editstudybundle.topicClassVocabularyLabel}" heading="#{editstudybundle.topicClassVocabularyHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.topicClassVocab].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.topicClassVocab].recommended}"/>
                                                        <br/>
                                                        <h:inputText maxlength="255" validator ="#{TemplateFormPage.validateStudyTopicClass}" binding="#{TemplateFormPage.inputTopicClassVocab}" id="input_topicClassVocab" value="#{currentRow.vocab}" required="#{TemplateFormPage.studyMap[sfc.topicClassVocab].required}"/>
                                                        <h:message styleClass="errorMessage" for="input_topicClassVocab"/> 
                                                        <br/>
                                                    </ui:panelGroup>
                                                    <ui:panelGroup styleClass="vdcEditStudyGroupField #{TemplateFormPage.studyMap[sfc.topicClassVocabURI].templateField.fieldInputLevel.name}">
                                                        <dvn:inlinehelp helpMessage="#{editstudybundle.topicClassVocabularyURLHelp}" linkText="#{editstudybundle.topicClassVocabularyURLLabel}" heading="#{editstudybundle.topicClassVocabularyURLHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.topicClassVocabURI].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.topicClassVocabURI].recommended}"/>
                                                        <br/>
                                                        <h:inputText id="input_topicClassVocabURI" 
                                                                     binding="#{TemplateFormPage.inputTopicClassVocabUri}"
                                                                     validator ="#{TemplateFormPage.validateStudyTopicClass}"
                                                                     size="45" 
                                                                     maxlength="255" 
                                                                     value="#{currentRow.vocabURI}" 
                                                                     required="#{TemplateFormPage.studyMap[sfc.topicClassVocabURI].required}"
                                                                     >
                                                            <f:validator validatorId="UrlValidator"/>
                                                        </h:inputText>
                                                        <h:message styleClass="errorMessage" for="input_topicClassVocabURI"/> 
                                                        <br/>
                                                    </ui:panelGroup>                                                    
                                                </ui:panelGroup>
                                            </h:column>
                                            <h:column>
                                                <h:commandButton   image="/resources/icon_add.gif" actionListener="#{TemplateFormPage.addRow}"/> 
                                                <h:commandButton   image="/resources/icon_remove.gif" actionListener="#{TemplateFormPage.removeRow}" /> 
                                            </h:column>
                                        </h:dataTable>
                                    </h:column>
                                </h:dataTable>                                
                                <!-- this is a dummy datatable wrapper used to force data retention on validation error-->
                                <h:dataTable rowClasses="#{TemplateFormPage.studyMap[sfc.relatedPublications].templateField.fieldInputLevel.name}" cellpadding="0" cellspacing="0" width="100%" value="dummy_datatable" >
                                    <h:column>                                          
                                        <h:dataTable cellpadding="0" cellspacing="0"
                                                     columnClasses="vdcEditStudyCol1b, vdcEditStudyCol2, vdcEditStudyCol3"
                                                     binding="#{TemplateFormPage.dataTableRelPublications}" 
                                                     value="#{TemplateFormPage.template.metadata.studyRelPublications}" var="currentRow" width="100%"  
                                                     >
                                            
                                            <h:column>
                                                <ui:panelGroup block="true" styleClass="vdcEditStudyField"> 
                                                    <dvn:inlinehelp helpMessage="#{editstudybundle.relatedPublicationsHelp}" linkText="#{editstudybundle.relatedPublicationsLabel}" heading="#{editstudybundle.relatedPublicationsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                    <h:graphicImage  value="/resources/icon_required.gif" rendered="#{TemplateFormPage.studyMap[sfc.relatedPublications].required}"/>
                                                    <h:graphicImage  value="/resources/icon_recommended.gif" rendered="#{TemplateFormPage.studyMap[sfc.relatedPublications].recommended}"/>
                                                </ui:panelGroup>
                                            </h:column>
                                            
                                            <h:column>
                                                <ui:panelGroup block="true" >
                                                    
                                                    <h:inputTextarea cols="90" rows="4"  
                                                                     id="input_relatedPublications" 
                                                                     binding="#{TemplateFormPage.inputRelPublicationName}" 
                                                                     value="#{currentRow.text}" 
                                                                     required="#{TemplateFormPage.studyMap[sfc.relatedPublications].required}" 
                                                                     styleClass="formHtmlEnabled">
                                                        <f:validator validatorId="XhtmlValidator"/>
                                                    </h:inputTextarea>
                                                    <h:message styleClass="errorMessage" for="input_relatedPublications"/>
                                                    <f:verbatim><br /></f:verbatim>
                                                </ui:panelGroup>
                                            </h:column>
                                            
                                            <h:column>
                                                <h:commandButton   image="/resources/icon_add.gif" actionListener="#{TemplateFormPage.addRow}"/> 
                                                <h:commandButton   image="/resources/icon_remove.gif" actionListener="#{TemplateFormPage.removeRow}" /> 
                                            </h:column>               
                                        </h:dataTable>
                                    </h:column>
                                </h:dataTable>
                                <!-- this is a dummy datatable wrapper used to force data retention on validation error-->
                                <h:dataTable rowClasses="#{TemplateFormPage.studyMap[sfc.relatedMaterial].templateField.fieldInputLevel.name}" cellpadding="0" cellspacing="0" width="100%" value="dummy_datatable" >
                                    <h:column>                                          
                                        <h:dataTable cellpadding="0" cellspacing="0"
                                                     columnClasses="vdcEditStudyCol1b, vdcEditStudyCol2, vdcEditStudyCol3"
                                                     binding="#{TemplateFormPage.dataTableRelMaterials}" 
                                                     value="#{TemplateFormPage.template.metadata.studyRelMaterials}" var="currentRow" width="100%"  
                                                     >
                                            
                                            <h:column>
                                                <ui:panelGroup block="true" styleClass="vdcEditStudyField"> 
                                                    <dvn:inlinehelp helpMessage="#{editstudybundle.relatedMaterialHelp}" linkText="#{editstudybundle.relatedMaterialLabel}" heading="#{editstudybundle.relatedMaterialHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                    <h:graphicImage  value="/resources/icon_required.gif" rendered="#{TemplateFormPage.studyMap[sfc.relatedMaterial].required}"/>
                                                    <h:graphicImage  value="/resources/icon_recommended.gif" rendered="#{TemplateFormPage.studyMap[sfc.relatedMaterial].recommended}"/>
                                                </ui:panelGroup>
                                            </h:column>
                                            
                                            <h:column>
                                                <ui:panelGroup block="true" >
                                                    <h:inputTextarea cols="90" rows="4"  
                                                                     id="input_relatedMaterial" 
                                                                     binding="#{TemplateFormPage.inputRelMaterial}" 
                                                                     value="#{currentRow.text}" 
                                                                     required="#{TemplateFormPage.studyMap[sfc.relatedMaterial].required}" 
                                                                     styleClass="formHtmlEnabled">
                                                        <f:validator validatorId="XhtmlValidator"/>
                                                    </h:inputTextarea>
                                                    <h:message styleClass="errorMessage" for="input_relatedMaterial"/>
                                                    <f:verbatim><br /></f:verbatim>
                                                </ui:panelGroup>
                                            </h:column>
                                            
                                            <h:column>
                                                <h:commandButton   image="/resources/icon_add.gif" actionListener="#{TemplateFormPage.addRow}"/> 
                                                <h:commandButton   image="/resources/icon_remove.gif" actionListener="#{TemplateFormPage.removeRow}" /> 
                                            </h:column>               
                                        </h:dataTable>
                                    </h:column>
                                </h:dataTable>
                                <!-- this is a dummy datatable wrapper used to force data retention on validation error-->
                                <h:dataTable  rowClasses="#{TemplateFormPage.studyMap[sfc.relatedStudies].templateField.fieldInputLevel.name}"  cellpadding="0" cellspacing="0" width="100%" value="dummy_datatable" >
                                    <h:column>                                          
                                        <h:dataTable cellpadding="0" cellspacing="0"
                                                     columnClasses="vdcEditStudyCol1b, vdcEditStudyCol2, vdcEditStudyCol3"
                                                     binding="#{TemplateFormPage.dataTableRelStudies}" 
                                                     value="#{TemplateFormPage.template.metadata.studyRelStudies}" var="currentRow" width="100%"  
                                                     >
                                            
                                            <h:column>
                                                <ui:panelGroup block="true" styleClass="vdcEditStudyField"> 
                                                    <dvn:inlinehelp helpMessage="#{editstudybundle.relatedStudiesHelp}" linkText="#{editstudybundle.relatedStudiesLabel}" heading="#{editstudybundle.relatedStudiesHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                    <h:graphicImage  value="/resources/icon_required.gif" rendered="#{TemplateFormPage.studyMap[sfc.relatedStudies].required}"/>
                                                    <h:graphicImage  value="/resources/icon_recommended.gif" rendered="#{TemplateFormPage.studyMap[sfc.relatedStudies].recommended}"/>
                                                </ui:panelGroup>
                                            </h:column>
                                            
                                            <h:column>
                                                <ui:panelGroup block="true" >
                                                    <h:inputTextarea cols="90" rows="4"  
                                                                     id="input_relatedStudies" 
                                                                     binding="#{TemplateFormPage.inputRelStudy}" 
                                                                     value="#{currentRow.text}" 
                                                                     required="#{TemplateFormPage.studyMap[sfc.relatedStudies].required}" 
                                                                     styleClass="formHtmlEnabled">
                                                        <f:validator validatorId="XhtmlValidator"/>
                                                    </h:inputTextarea>
                                                    <h:message styleClass="errorMessage" for="input_relatedStudies"/>
                                                    <f:verbatim><br /></f:verbatim>
                                                </ui:panelGroup>
                                            </h:column>
                                            
                                            <h:column>
                                                <h:commandButton   image="/resources/icon_add.gif" actionListener="#{TemplateFormPage.addRow}"/> 
                                                <h:commandButton   image="/resources/icon_remove.gif" actionListener="#{TemplateFormPage.removeRow}" /> 
                                            </h:column>               
                                        </h:dataTable>
                                    </h:column>
                                </h:dataTable>
                                <!-- this is a dummy datatable wrapper used to force data retention on validation error-->
                                <h:dataTable  rowClasses="#{TemplateFormPage.studyMap[sfc.otherReferences].templateField.fieldInputLevel.name}"  cellpadding="0" cellspacing="0" width="100%" value="dummy_datatable" >
                                    <h:column>                                          
                                        <h:dataTable cellpadding="0" cellspacing="0"
                                                     columnClasses="vdcEditStudyCol1b, vdcEditStudyCol2, vdcEditStudyCol3"
                                                     binding="#{TemplateFormPage.dataTableOtherReferences}" 
                                                     value="#{TemplateFormPage.template.metadata.studyOtherRefs}" var="currentRow" width="100%"  
                                                     >
                                            
                                            <h:column>
                                                <ui:panelGroup block="true" styleClass="vdcEditStudyField"> 
                                                    <dvn:inlinehelp helpMessage="#{editstudybundle.otherReferencesHelp}" linkText="#{editstudybundle.otherReferencesLabel}" heading="#{editstudybundle.otherReferencesHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                    <h:graphicImage  value="/resources/icon_required.gif" rendered="#{TemplateFormPage.studyMap[sfc.otherReferences].required}"/>
                                                    <h:graphicImage  value="/resources/icon_recommended.gif" rendered="#{TemplateFormPage.studyMap[sfc.otherReferences].recommended}"/>
                                                </ui:panelGroup>
                                            </h:column>
                                            
                                            <h:column>
                                                <ui:panelGroup block="true" >
                                                    <h:inputText id="input_otherReferences" 
                                                                 binding="#{TemplateFormPage.inputOtherReference}"
                                                                 value="#{currentRow.text}" 
                                                                 size="90" 
                                                                 required="#{TemplateFormPage.studyMap[sfc.otherReferences].required}"
                                                                 styleClass="formHtmlEnabled">
                                                        <f:validator validatorId="XhtmlValidator"/>
                                                    </h:inputText>
                                                    <h:message styleClass="errorMessage" for="input_otherReferences"/>
                                                    <f:verbatim><br /></f:verbatim>
                                                </ui:panelGroup>
                                            </h:column>
                                            
                                            <h:column>
                                                <h:commandButton   image="/resources/icon_add.gif" actionListener="#{TemplateFormPage.addRow}"/> 
                                                <h:commandButton   image="/resources/icon_remove.gif" actionListener="#{TemplateFormPage.removeRow}" /> 
                                            </h:column>               
                                        </h:dataTable>
                                    </h:column>
                                </h:dataTable>                            
                                
                                <h:panelGrid  styleClass="#{TemplateFormPage.studyMap[sfc.timePeriodCoveredStart].templateField.fieldInputLevel.name}"  cellpadding="0" cellspacing="0"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2" width="100%">
                                    
                                    <ui:panelGroup styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.timePeriodStartHelp}" linkText="#{editstudybundle.timePeriodStartLabel}" heading="#{editstudybundle.timePeriodStartHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.timePeriodCoveredStart].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.timePeriodCoveredStart].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup >
                                        <h:inputText id="input_timePeriodCoveredStart" styleClass="formDateFormat" value="#{TemplateFormPage.template.metadata.timePeriodCoveredStart}" required="#{TemplateFormPage.studyMap[sfc.timePeriodCoveredStart].required}">
                                            <f:validator validatorId="DateValidator"/>    
                                        </h:inputText>
                                        <h:outputText value=" (Enter date as YYYY or YYYY-MM or YYYY-MM-DD)"/>
                                        <h:message styleClass="errorMessage" for="input_timePeriodCoveredStart"/>                                
                                    </ui:panelGroup>
                                </h:panelGrid>
                                <h:panelGrid  styleClass="#{TemplateFormPage.studyMap[sfc.timePeriodCoveredEnd].templateField.fieldInputLevel.name}"  cellpadding="0" cellspacing="0"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2" width="100%">
                                    <ui:panelGroup styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.timePeriodEndHelp}" linkText="#{editstudybundle.timePeriodEndLabel}" heading="#{editstudybundle.timePeriodEndHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.timePeriodCoveredEnd].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.timePeriodCoveredEnd].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup >
                                        <h:inputText id="input_timePeriodCoveredEnd" styleClass="formDateFormat" value="#{TemplateFormPage.template.metadata.timePeriodCoveredEnd}" required="#{TemplateFormPage.studyMap[sfc.timePeriodCoveredEnd].required}">
                                            <f:validator validatorId="DateValidator"/>    
                                        </h:inputText>
                                        <h:outputText value=" (Enter date as YYYY or YYYY-MM or YYYY-MM-DD)"/>
                                        <h:message styleClass="errorMessage" for="input_timePeriodCoveredEnd"/>                                
                                    </ui:panelGroup>
                                </h:panelGrid>
                                <h:panelGrid  styleClass="#{TemplateFormPage.studyMap[sfc.dateOfCollectionStart].templateField.fieldInputLevel.name}"  cellpadding="0" cellspacing="0"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2" width="100%">
                                    <ui:panelGroup styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.collectionDateStartHelp}" linkText="#{editstudybundle.collectionDateStartLabel}" heading="#{editstudybundle.collectionDateStartHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.dateOfCollectionStart].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.dateOfCollectionStart].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup >
                                        <h:inputText id="input_dateOfCollectionStart" styleClass="formDateFormat" value="#{TemplateFormPage.template.metadata.dateOfCollectionStart}"  required="#{TemplateFormPage.studyMap[sfc.dateOfCollectionStart].required}">
                                            <f:validator validatorId="DateValidator"/>    
                                        </h:inputText>
                                        <h:outputText value=" (Enter date as YYYY or YYYY-MM or YYYY-MM-DD)"/>
                                        <h:message styleClass="errorMessage" for="input_dateOfCollectionStart"/>                                
                                    </ui:panelGroup>
                                </h:panelGrid>
                                <h:panelGrid  styleClass="#{TemplateFormPage.studyMap[sfc.dateOfCollectionEnd].templateField.fieldInputLevel.name}"  cellpadding="0" cellspacing="0"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2" width="100%">
                                    
                                    <ui:panelGroup  styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.collectionDateEndHelp}" linkText="#{editstudybundle.collectionDateEndLabel}" heading="#{editstudybundle.collectionDateEndHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.dateOfCollectionEnd].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.dateOfCollectionEnd].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup >
                                        <h:inputText id="input_dateOfCollectionEnd" styleClass="formDateFormat" value="#{TemplateFormPage.template.metadata.dateOfCollectionEnd}"  required="#{TemplateFormPage.studyMap[sfc.dateOfCollectionEnd].required}">
                                            <f:validator validatorId="DateValidator"/>    
                                        </h:inputText>
                                        <h:outputText value=" (Enter date as YYYY or YYYY-MM or YYYY-MM-DD)"/>
                                        <h:message styleClass="errorMessage" for="input_dateOfCollectionEnd"/>                                
                                    </ui:panelGroup>
                                 </h:panelGrid>
                                <h:panelGrid  styleClass="#{TemplateFormPage.studyMap[sfc.country].templateField.fieldInputLevel.name}"  cellpadding="0" cellspacing="0"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2" width="100%">
                                   
                                    <ui:panelGroup  styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.countryHelp}" linkText="#{editstudybundle.countryLabel}" heading="#{editstudybundle.countryHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.country].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.country].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup >
                                        <h:inputText id="input_country" size="45" value="#{TemplateFormPage.template.metadata.country}" required="#{TemplateFormPage.studyMap[sfc.country].required}"/>
                                        <h:message styleClass="errorMessage" for="input_country"/>                                
                                    </ui:panelGroup>
                                 </h:panelGrid>
                                 <h:panelGrid  styleClass="#{TemplateFormPage.studyMap[sfc.geographicCoverage].templateField.fieldInputLevel.name}"  cellpadding="0" cellspacing="0"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2" width="100%">
                                    
                                    <ui:panelGroup  styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.geographicCoverageHelp}" linkText="#{editstudybundle.geographicCoverageLabel}" heading="#{editstudybundle.geographicCoverageHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.geographicCoverage].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.geographicCoverage].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup >
                                        <h:inputText id="input_geographicCoverage" size="45"  value="#{TemplateFormPage.template.metadata.geographicCoverage}" required="#{TemplateFormPage.studyMap[sfc.geographicCoverage].required}"/>
                                        <h:message styleClass="errorMessage" for="input_geographicCoverage"/>                                
                                    </ui:panelGroup>
                                  </h:panelGrid>
                                 <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.geographicUnit].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >                                
                                    <ui:panelGroup styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.geographicUnitHelp}" linkText="#{editstudybundle.geographicUnitLabel}" heading="#{editstudybundle.geographicUnitHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.geographicUnit].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.geographicUnit].recommended}"/>
                                    </ui:panelGroup>
                                
                                    <ui:panelGroup >
                                        <h:inputText id="input_geographicUnit" size="45"  value="#{TemplateFormPage.template.metadata.geographicUnit}" required="#{TemplateFormPage.studyMap[sfc.geographicUnit].required}"/>
                                        <h:message styleClass="errorMessage" for="input_geographicUnit"/>                                
                                    </ui:panelGroup>
                                </h:panelGrid>
                                 <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.westLongitude].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >
                                   
                                    <ui:panelGroup block="true" styleClass="vdcEditStudyField"> 
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.geographicBoundingBoxHelp}" linkText="#{editstudybundle.geographicBoundingBoxLabel}" heading="#{editstudybundle.geographicBoundingBoxHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.westLongitude].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.westLongitude].recommended}"/>
                                    </ui:panelGroup>
                                    
                                    <ui:panelGroup block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.westLongitudeHelp}" linkText="#{editstudybundle.westLongitudeLabel}" heading="#{editstudybundle.westLongitudeHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:inputText   id="input_westLongitude"  binding="#{TemplateFormPage.inputWestLongitude}" value="#{TemplateFormPage.template.metadata.studyGeoBoundings[0].westLongitude}"   required="#{TemplateFormPage.studyMap[sfc.westLongitude].required}">
                                            
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_westLongitude"/>        
                                        
                                        <ui:panelGroup styleClass="vdcEditStudyGroupField">                                                   
                                            <dvn:inlinehelp helpMessage="#{editstudybundle.eastLongitudeHelp}" linkText="#{editstudybundle.eastLongitudeLabel}" heading="#{editstudybundle.eastLongitudeHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                            <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.eastLongitude].required}"/>
                                            <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.eastLongitude].recommended}"/>
                                            <h:inputText  binding="#{TemplateFormPage.inputEastLongitude}" id="input_eastLongitude" value="#{TemplateFormPage.template.metadata.studyGeoBoundings[0].eastLongitude}"  required="#{TemplateFormPage.studyMap[sfc.eastLongitude].required}">
                                                
                                            </h:inputText>
                                        </ui:panelGroup>
                                        <h:message styleClass="errorMessage" for="input_eastLongitude"/>        
                                        
                                        <ui:panelGroup styleClass="vdcEditStudyGroupField">                                                   
                                            <dvn:inlinehelp helpMessage="#{editstudybundle.northLatitudeHelp}" linkText="#{editstudybundle.northLatitudeLabel}" heading="#{editstudybundle.northLatitudeHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                            <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.northLatitude].required}"/>
                                            <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.northLatitude].recommended}"/>
                                            <h:inputText   binding="#{TemplateFormPage.inputNorthLatitude}"  id="input_northLatitude" value="#{TemplateFormPage.template.metadata.studyGeoBoundings[0].northLatitude}"  required="#{TemplateFormPage.studyMap[sfc.northLatitude].required}"/>
                                        </ui:panelGroup>
                                        <h:message styleClass="errorMessage" for="input_northLatitude"/>      
                                        
                                        <ui:panelGroup styleClass="vdcEditStudyGroupField">                                                   
                                            <dvn:inlinehelp helpMessage="#{editstudybundle.southLatitudeHelp}" linkText="#{editstudybundle.southLatitudeLabel}" heading="#{editstudybundle.southLatitudeHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                            <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.southLatitude].required}"/>
                                            <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.southLatitude].recommended}"/>
                                            <h:inputText  binding="#{TemplateFormPage.inputSouthLatitude}" id="input_southLatitude" value="#{TemplateFormPage.template.metadata.studyGeoBoundings[0].southLatitude}"  required="#{TemplateFormPage.studyMap[sfc.southLatitude].required}"/>
                                        </ui:panelGroup>
                                        <h:message styleClass="errorMessage" for="input_southLatitude"/>      
                                        <h:inputHidden validator="#{TemplateFormPage.validateGeographicBounding}" value="test" required="true"/>
                                    </ui:panelGroup>
                               </h:panelGrid>
                                 <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.unitOfAnalysis].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >
                                    
                                    <ui:panelGroup  styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.unitOfAnalysisHelp}" linkText="#{editstudybundle.unitOfAnalysisLabel}" heading="#{editstudybundle.unitOfAnalysisHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.unitOfAnalysis].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.unitOfAnalysis].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup >
                                        <h:inputTextarea id="input_unitOfAnalysis" cols="90" rows="2" value="#{TemplateFormPage.template.metadata.unitOfAnalysis}" required="#{TemplateFormPage.studyMap[sfc.unitOfAnalysis].required}" styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputTextarea>
                                        <h:message styleClass="errorMessage" for="input_unitOfAnalysis"/>          
                                    </ui:panelGroup>
                               </h:panelGrid>
                                 <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.universe].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >
                                    
                                    <ui:panelGroup  styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.universeHelp}" linkText="#{editstudybundle.universeLabel}" heading="#{editstudybundle.universeHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.universe].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.universe].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup >
                                        <h:inputTextarea id="input_universe" cols="90" rows="2"  value="#{TemplateFormPage.template.metadata.universe}" required="#{TemplateFormPage.studyMap[sfc.universe].required}" styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputTextarea>
                                        <h:message styleClass="errorMessage" for="input_universe"/>                                
                                    </ui:panelGroup>
                                 </h:panelGrid>
                                 <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.kindOfData].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >
                                   
                                    
                                    <ui:panelGroup styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.kindOfDataHelp}" linkText="#{editstudybundle.kindOfDataLabel}" heading="#{editstudybundle.kindOfDataHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.kindOfData].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.kindOfData].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup >
                                        <h:inputText id="input_kindOfData"  size="45" value="#{TemplateFormPage.template.metadata.kindOfData}" required="#{TemplateFormPage.studyMap[sfc.kindOfData].required}"/>
                                        <h:message styleClass="errorMessage" for="input_kindOfData"/>                                
                                    </ui:panelGroup>
                                    
                                    
                                </h:panelGrid>
                                <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.dataCollectionMethodologyInputLevel}"
                                             columns="1"  width="100%" >
                                    <ui:panelGroup block="true" id="groupPanel13" styleClass="vdcStudyInfoHeader" >
                                        <h:outputText id="outputText90" value="Data Collection / Methodology" styleClass="#{TemplateFormPage.dataCollectionMethodologyInputLevel}" />
                                    </ui:panelGroup>
                                </h:panelGrid>
                                
                                <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.timeMethod].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >
                                    <ui:panelGroup  block="true"  styleClass="vdcEditStudyField" >
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.timeMethodHelp}" linkText="#{editstudybundle.timeMethodLabel}" heading="#{editstudybundle.timeMethodHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.timeMethod].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.timeMethod].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup block="true"   >
                                        <h:inputText id="input_timeMethod" size="90" 
                                                     value="#{TemplateFormPage.template.metadata.timeMethod}" 
                                                     required="#{TemplateFormPage.studyMap[sfc.timeMethod].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_timeMethod"/>                                
                                    </ui:panelGroup>
                                </h:panelGrid>
                                <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.dataCollector].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >
                                    
                                    <ui:panelGroup block="true" styleClass="vdcEditStudyField">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.dataCollectorHelp}" linkText="#{editstudybundle.dataCollectorLabel}" heading="#{editstudybundle.dataCollectorHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.dataCollector].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.dataCollector].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup block="true" >
                                        <h:inputText id="input_dataCollector" size="90" 
                                                     value="#{TemplateFormPage.template.metadata.dataCollector}" 
                                                     required="#{TemplateFormPage.studyMap[sfc.dataCollector].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_dataCollector"/>                                
                                    </ui:panelGroup>
                                </h:panelGrid>
                                
                                <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.frequencyOfDataCollection].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >      
                                    <ui:panelGroup  styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.frequencyHelp}" linkText="#{editstudybundle.frequencyLabel}" heading="#{editstudybundle.frequencyHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.frequencyOfDataCollection].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.frequencyOfDataCollection].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup >
                                        <h:inputText id="input_frequencyOfDataCollection"  size="90"
                                                     value="#{TemplateFormPage.template.metadata.frequencyOfDataCollection}" 
                                                     required="#{TemplateFormPage.studyMap[sfc.frequencyOfDataCollection].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_frequencyOfDataCollection"/>
                                    </ui:panelGroup>
                                </h:panelGrid>
                                
                                <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.samplingProcedure].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >
                                    <ui:panelGroup  styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.samplingProcedureHelp}" linkText="#{editstudybundle.samplingProcedureLabel}" heading="#{editstudybundle.samplingProcedureHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.samplingProcedure].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.samplingProcedure].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup  >
                                        <h:inputTextarea id="input_samplingProcedure" 
                                                         cols="90" rows="4"
                                                         value="#{TemplateFormPage.template.metadata.samplingProcedure}" 
                                                         required="#{TemplateFormPage.studyMap[sfc.samplingProcedure].required}"
                                                         styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputTextarea>
                                        <h:message styleClass="errorMessage" for="input_samplingProcedure"/>
                                    </ui:panelGroup>
                                    
                                </h:panelGrid>
                                
                                <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.deviationsFromSampleDesign].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >
                                    <ui:panelGroup  styleClass="vdcEditStudyField" block="true" >
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.majorDeviationsHelp}" linkText="#{editstudybundle.majorDeviationsLabel}" heading="#{editstudybundle.majorDeviationsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.deviationsFromSampleDesign].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.deviationsFromSampleDesign].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup >
                                        <h:inputTextarea cols="90" rows="4" 
                                                         id="input_deviationsFromSampleDesign"  
                                                         value="#{TemplateFormPage.template.metadata.deviationsFromSampleDesign}" 
                                                         required="#{TemplateFormPage.studyMap[sfc.deviationsFromSampleDesign].required}"
                                                         styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputTextarea>
                                        <h:message styleClass="errorMessage" for="input_deviationsFromSampleDesign"/>
                                    </ui:panelGroup>
                                </h:panelGrid>
                                
                                <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.collectionMode].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >
                                    <ui:panelGroup  styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.collectionModeHelp}" linkText="#{editstudybundle.collectionModeLabel}" heading="#{editstudybundle.collectionModeHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.collectionMode].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.collectionMode].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup >
                                        <h:inputText id="input_collectionMode"  size="90"
                                                     value="#{TemplateFormPage.template.metadata.collectionMode}" 
                                                     required="#{TemplateFormPage.studyMap[sfc.collectionMode].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_collectionMode"/>
                                    </ui:panelGroup>
                                </h:panelGrid>
                                
                                <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.researchInstrument].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >
                                    <ui:panelGroup  styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.researchInstrumentHelp}" linkText="#{editstudybundle.researchInstrumentLabel}" heading="#{editstudybundle.researchInstrumentHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.researchInstrument].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.researchInstrument].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup >
                                        <h:inputText id="input_researchInstrument" 
                                                     size="90" 
                                                     value="#{TemplateFormPage.template.metadata.researchInstrument}" 
                                                     required="#{TemplateFormPage.studyMap[sfc.researchInstrument].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_researchInstrument"/>
                                    </ui:panelGroup>
                                </h:panelGrid>
                                
                                <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.dataSources].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >
                                    <ui:panelGroup  styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.dataSourcesHelp}" linkText="#{editstudybundle.dataSourcesLabel}" heading="#{editstudybundle.dataSourcesHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.dataSources].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.dataSources].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup >
                                        <h:inputText id="input_dataSources"  size="90"
                                                     value="#{TemplateFormPage.template.metadata.dataSources}" 
                                                     required="#{TemplateFormPage.studyMap[sfc.dataSources].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_dataSources"/>
                                    </ui:panelGroup>
                                </h:panelGrid>
                                
                                <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.originOfSources].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >
                                    <ui:panelGroup  styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.originOfSourcesHelp}" linkText="#{editstudybundle.originOfSourcesLabel}" heading="#{editstudybundle.originOfSourcesHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.originOfSources].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.originOfSources].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup >
                                        <h:inputText id="input_originOfSources"  size="90"
                                                     value="#{TemplateFormPage.template.metadata.originOfSources}" 
                                                     required="#{TemplateFormPage.studyMap[sfc.originOfSources].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_originOfSources"/>
                                    </ui:panelGroup>
                                </h:panelGrid>
                                
                                <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.characteristicOfSources].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >
                                    <ui:panelGroup styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.sourceCharacteristicsHelp}" linkText="#{editstudybundle.sourceCharacteristicsLabel}" heading="#{editstudybundle.sourceCharacteristicsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.characteristicOfSources].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.characteristicOfSources].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup >
                                        <h:inputText id="input_characteristicOfSources" 
                                                     size="90" 
                                                     value="#{TemplateFormPage.template.metadata.characteristicOfSources}" 
                                                     required="#{TemplateFormPage.studyMap[sfc.characteristicOfSources].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_characteristicOfSources"/>
                                    </ui:panelGroup>
                                </h:panelGrid>
                                
                                <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.accessToSources].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >
                                    <ui:panelGroup  styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.sourceDocumentationHelp}" linkText="#{editstudybundle.sourceDocumentationLabel}" heading="#{editstudybundle.sourceDocumentationHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.accessToSources].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.accessToSources].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup >
                                        <h:inputTextarea cols="90" rows="4" 
                                                         id="input_accessToSources" 
                                                         value="#{TemplateFormPage.template.metadata.accessToSources}" 
                                                         required="#{TemplateFormPage.studyMap[sfc.accessToSources].required}"
                                                         styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputTextarea>
                                        <h:message styleClass="errorMessage" for="input_accessToSources"/>
                                    </ui:panelGroup>
                                </h:panelGrid>
                                
                                <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.dataCollectionSituation].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >
                                    <ui:panelGroup  styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.dataCollectionCharacteristicsHelp}" linkText="#{editstudybundle.dataCollectionCharacteristicsLabel}" heading="#{editstudybundle.dataCollectionCharacteristicsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.dataCollectionSituation].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.dataCollectionSituation].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup >
                                        <h:inputText id="input_dataCollectionSituation" 
                                                     size="90" 
                                                     value="#{TemplateFormPage.template.metadata.dataCollectionSituation}" 
                                                     required="#{TemplateFormPage.studyMap[sfc.dataCollectionSituation].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_dataCollectionSituation"/>
                                    </ui:panelGroup>
                                </h:panelGrid>
                                
                                <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.actionsToMinimizeLoss].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >
                                    <ui:panelGroup  styleClass="vdcEditStudyField" block="true" >
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.minimizeActionsHelp}" linkText="#{editstudybundle.minimizeActionsLabel}" heading="#{editstudybundle.minimizeActionsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.actionsToMinimizeLoss].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.actionsToMinimizeLoss].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup >
                                        <h:inputText id="input_actionsToMinimizeLoss" 
                                                     size="90" 
                                                     value="#{TemplateFormPage.template.metadata.actionsToMinimizeLoss}" 
                                                     required="#{TemplateFormPage.studyMap[sfc.actionsToMinimizeLoss].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_actionsToMinimizeLoss"/>
                                    </ui:panelGroup>
                                </h:panelGrid>
                                
                                <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.controlOperations].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >
                                    <ui:panelGroup  styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.controlOperationsHelp}" linkText="#{editstudybundle.controlOperationsLabel}" heading="#{editstudybundle.controlOperationsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.controlOperations].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.controlOperations].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup >
                                        <h:inputText id="input_controlOperations" 
                                                     size="90" 
                                                     value="#{TemplateFormPage.template.metadata.controlOperations}" 
                                                     required="#{TemplateFormPage.studyMap[sfc.controlOperations].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_controlOperations"/>
                                    </ui:panelGroup >
                                </h:panelGrid>
                                
                                <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.weighting].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >
                                    <ui:panelGroup  styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.weightingHelp}" linkText="#{editstudybundle.weightingLabel}" heading="#{editstudybundle.weightingHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.weighting].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.weighting].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup >
                                        <h:inputText id="input_weighting"  size="90"
                                                     value="#{TemplateFormPage.template.metadata.weighting}" 
                                                     required="#{TemplateFormPage.studyMap[sfc.weighting].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_weighting"/>
                                    </ui:panelGroup>
                                </h:panelGrid>
                                
                                <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.cleaningOperations].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >
                                    <ui:panelGroup styleClass="vdcEditStudyField" block="true" >
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.cleaningOperationsHelp}" linkText="#{editstudybundle.cleaningOperationsLabel}" heading="#{editstudybundle.cleaningOperationsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.cleaningOperations].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.cleaningOperations].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup >
                                        <h:inputText id="input_cleaningOperations"  size="90"
                                                     value="#{TemplateFormPage.template.metadata.cleaningOperations}" 
                                                     required="#{TemplateFormPage.studyMap[sfc.cleaningOperations].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_cleaningOperations"/>
                                    </ui:panelGroup>
                                </h:panelGrid>
                                
                                <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.studyLevelErrorNotes].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >
                                    <ui:panelGroup  styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.studyLevelErrorNotesHelp}" linkText="#{editstudybundle.studyLevelErrorNotesLabel}" heading="#{editstudybundle.studyLevelErrorNotesHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.studyLevelErrorNotes].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.studyLevelErrorNotes].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup >
                                        <h:inputText id="input_studyLevelErrorNotes" size="90"
                                                     value="#{TemplateFormPage.template.metadata.studyLevelErrorNotes}" 
                                                     required="#{TemplateFormPage.studyMap[sfc.studyLevelErrorNotes].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_studyLevelErrorNotes"/>
                                    </ui:panelGroup>
                                </h:panelGrid>
                                
                                <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.responseRate].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >
                                    <ui:panelGroup  styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.responseRateHelp}" linkText="#{editstudybundle.responseRateLabel}" heading="#{editstudybundle.responseRateHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.responseRate].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.responseRate].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup >
                                        <h:inputText id="input_responseRate" size="90" 
                                                     value="#{TemplateFormPage.template.metadata.responseRate}" 
                                                     required="#{TemplateFormPage.studyMap[sfc.responseRate].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_responseRate"/>
                                    </ui:panelGroup>
                                </h:panelGrid>
                                
                                <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.samplingErrorEstimates].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >
                                    <ui:panelGroup  styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.samplingErrorHelp}" linkText="#{editstudybundle.samplingErrorLabel}" heading="#{editstudybundle.samplingErrorHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.samplingErrorEstimates].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.samplingErrorEstimates].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup >
                                        <h:inputText id="input_samplingErrorEstimates" size="90" 
                                                     value="#{TemplateFormPage.template.metadata.samplingErrorEstimate}" 
                                                     required="#{TemplateFormPage.studyMap[sfc.samplingErrorEstimates].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_samplingErrorEstimates"/>
                                    </ui:panelGroup>
                                </h:panelGrid>
                                
                                <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.otherDataAppraisal].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >
                                    <ui:panelGroup styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.otherDataAppraisalHelp}" linkText="#{editstudybundle.otherDataAppraisalLabel}" heading="#{editstudybundle.otherDataAppraisalHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.otherDataAppraisal].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.otherDataAppraisal].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup >
                                        <h:inputText id="input_otherDataAppraisal" 
                                                     size="90" 
                                                     value="#{TemplateFormPage.template.metadata.otherDataAppraisal}" 
                                                     required="#{TemplateFormPage.studyMap[sfc.otherDataAppraisal].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_otherDataAppraisal"/>
                                    </ui:panelGroup>
                                    
                                </h:panelGrid>
                                <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.dataSetAvailabilityInputLevel}"
                                             columns="1"  width="100%" >
                                    
                                    <ui:panelGroup block="true" id="groupPanel14a" styleClass="vdcStudyInfoHeader" >
                                        <h:outputText id="outputText131a" value="Data Set Availability" />   
                                    </ui:panelGroup>
                                </h:panelGrid>
                                
                                <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.placeOfAccess].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2" id="gridPanel5a" width="100%"> 
                                    
                                    <ui:panelGroup  styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.dataAccessPlaceHelp}" linkText="#{editstudybundle.dataAccessPlaceLabel}" heading="#{editstudybundle.dataAccessPlaceHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.placeOfAccess].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.placeOfAccess].recommended}"/>
                                    </ui:panelGroup>              
                                    <ui:panelGroup  >
                                        <h:inputText id="input_placeOfAccess" 
                                                     size="90" 
                                                     value="#{TemplateFormPage.template.metadata.placeOfAccess}"  
                                                     required="#{TemplateFormPage.studyMap[sfc.placeOfAccess].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_placeOfAccess"/>
                                    </ui:panelGroup>
                                </h:panelGrid>
                                
                                <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.originalArchive].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >
                                    <ui:panelGroup  styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.originalArchiveHelp}" linkText="#{editstudybundle.originalArchiveLabel}" heading="#{editstudybundle.originalArchiveHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.originalArchive].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.originalArchive].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup >
                                        <h:inputText id="input_originalArchive" 
                                                     size="90" 
                                                     value="#{TemplateFormPage.template.metadata.originalArchive}" 
                                                     required="#{TemplateFormPage.studyMap[sfc.originalArchive].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_originalArchive"/>
                                    </ui:panelGroup>
                                </h:panelGrid>
                                
                                <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.availabilityStatus].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >
                                    <ui:panelGroup  styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.availabilityHelp}" linkText="#{editstudybundle.availabilityLabel}" heading="#{editstudybundle.availabilityHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.availabilityStatus].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.availabilityStatus].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup >
                                        <h:inputText id="input_availabilityStatus" size="90" 
                                                     value="#{TemplateFormPage.template.metadata.availabilityStatus}" 
                                                     required="#{TemplateFormPage.studyMap[sfc.availabilityStatus].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_availabilityStatus"/>
                                    </ui:panelGroup>
                                </h:panelGrid>
                                
                                <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.collectionSize].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >
                                    <ui:panelGroup  styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.collectionSizeHelp}" linkText="#{editstudybundle.collectionSizeLabel}" heading="#{editstudybundle.collectionSizeHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.collectionSize].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.collectionSize].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup >
                                        <h:inputText id="input_collectionSize" 
                                                     size="90" 
                                                     value="#{TemplateFormPage.template.metadata.collectionSize}" 
                                                     required="#{TemplateFormPage.studyMap[sfc.collectionSize].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_collectionSize"/>
                                    </ui:panelGroup>
                                </h:panelGrid>
                                
                                <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.studyCompletion].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >
                                    <ui:panelGroup  styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.studyCompletionHelp}" linkText="#{editstudybundle.studyCompletionLabel}" heading="#{editstudybundle.studyCompletionHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.studyCompletion].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.studyCompletion].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup>
                                        <h:inputText size="90" id="input_studyCompletion"  
                                                     value="#{TemplateFormPage.template.metadata.studyCompletion}" 
                                                     required="#{TemplateFormPage.studyMap[sfc.studyCompletion].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_studyCompletion"/>
                                    </ui:panelGroup>
                                </h:panelGrid>
                                <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.termsOfUseInputLevel}"
                                             columns="1"  width="100%" >       
                                    <ui:panelGroup block="true" id="groupPanel14" styleClass="vdcStudyInfoHeader" >
                                        <h:outputText id="outputText131" value="Terms of Use" styleClass="#{TemplateFormPage.termsOfUseInputLevel}"/>  
                                    </ui:panelGroup>
                                </h:panelGrid>
                                
                                <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.confidentialityDeclaration].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2" id="gridPanel5" width="100%">                                           
                                    
                                    
                                    <ui:panelGroup styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.confidentialityHelp}" linkText="#{editstudybundle.confidentialityLabel}" heading="#{editstudybundle.confidentialityHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.confidentialityDeclaration].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.confidentialityDeclaration].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup>
                                        <h:inputTextarea cols="90" rows="4" 
                                                         id="input_confidentialityDeclaration" 
                                                         value="#{TemplateFormPage.template.metadata.confidentialityDeclaration}" 
                                                         required="#{TemplateFormPage.studyMap[sfc.confidentialityDeclaration].required}"
                                                         styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputTextarea>
                                        <h:message styleClass="errorMessage" for="input_confidentialityDeclaration"/>
                                    </ui:panelGroup>
                                </h:panelGrid>
                                
                                <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.specialPermissions].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >
                                    <ui:panelGroup  styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.specialPermissionsHelp}" linkText="#{editstudybundle.specialPermissionsLabel}" heading="#{editstudybundle.specialPermissionsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.specialPermissions].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.specialPermissions].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup>
                                        <h:inputTextarea cols="90" rows="4" id="input_specialPermissions" 
                                                         value="#{TemplateFormPage.template.metadata.specialPermissions}" 
                                                         required="#{TemplateFormPage.studyMap[sfc.specialPermissions].required}"
                                                         styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputTextarea>
                                        <h:message styleClass="errorMessage" for="input_specialPermissions"/>
                                    </ui:panelGroup>
                                </h:panelGrid>
                                
                                <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.restrictions].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >
                                    <ui:panelGroup styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.restrictionsHelp}" linkText="#{editstudybundle.restrictionsLabel}" heading="#{editstudybundle.restrictionsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.restrictions].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.restrictions].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup >
                                        <h:inputTextarea cols="90" rows="4" 
                                                         id="input_restrictions"  
                                                         value="#{TemplateFormPage.template.metadata.restrictions}" 
                                                         required="#{TemplateFormPage.studyMap[sfc.restrictions].required}"
                                                         styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputTextarea>
                                        <h:message styleClass="errorMessage" for="input_restrictions"/>
                                    </ui:panelGroup>
                                </h:panelGrid>
                                
                                <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.contact].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >
                                    <ui:panelGroup  styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.contactHelp}" linkText="#{editstudybundle.contactLabel}" heading="#{editstudybundle.contactHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.contact].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.contact].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup >
                                        <h:inputText id="input_contact" 
                                                     size="90" 
                                                     value="#{TemplateFormPage.template.metadata.contact}" 
                                                     required="#{TemplateFormPage.studyMap[sfc.contact].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_contact"/>
                                    </ui:panelGroup>
                                </h:panelGrid>
                                
                                <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.citationRequirements].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >
                                    <ui:panelGroup  styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.citationRequirementsHelp}" linkText="#{editstudybundle.citationRequirementsLabel}" heading="#{editstudybundle.citationRequirementsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.citationRequirements].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.citationRequirements].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup >
                                        <h:inputTextarea cols="90" rows="4" id="input_citationRequirements" 
                                                         value="#{TemplateFormPage.template.metadata.citationRequirements}" 
                                                         required="#{TemplateFormPage.studyMap[sfc.citationRequirements].required}"
                                                         styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputTextarea>
                                        <h:message styleClass="errorMessage" for="input_citationRequirements"/>
                                    </ui:panelGroup>
                                </h:panelGrid>
                                
                                <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.depositorRequirements].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >
                                    <ui:panelGroup  styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.depositorRequirementsHelp}" linkText="#{editstudybundle.depositorRequirementsLabel}" heading="#{editstudybundle.depositorRequirementsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.depositorRequirements].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.depositorRequirements].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup>
                                        <h:inputTextarea cols="90" rows="4" 
                                                         id="input_depositorRequirements" 
                                                         value="#{TemplateFormPage.template.metadata.depositorRequirements}" 
                                                         required="#{TemplateFormPage.studyMap[sfc.depositorRequirements].required}"
                                                         styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputTextarea>
                                        <h:message styleClass="errorMessage" for="input_depositorRequirements"/>
                                    </ui:panelGroup>
                                </h:panelGrid>
                                
                                <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.conditions].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >
                                    <ui:panelGroup styleClass="vdcEditStudyField" block="true" >
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.conditionsHelp}" linkText="#{editstudybundle.conditionsLabel}" heading="#{editstudybundle.conditionsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.conditions].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.conditions].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup>
                                        <h:inputTextarea cols="90" rows="4" 
                                                         id="input_conditions" 
                                                         value="#{TemplateFormPage.template.metadata.conditions}" 
                                                         required="#{TemplateFormPage.studyMap[sfc.conditions].required}"
                                                         styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputTextarea>
                                        <h:message styleClass="errorMessage" for="input_conditions"/>
                                    </ui:panelGroup>
                                </h:panelGrid>
                                
                                <h:panelGrid cellpadding="0" cellspacing="0"  styleClass="#{TemplateFormPage.studyMap[sfc.disclaimer].templateField.fieldInputLevel.name}"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"  width="100%" >
                                    <ui:panelGroup  styleClass="vdcEditStudyField" block="true">
                                        <dvn:inlinehelp helpMessage="#{editstudybundle.disclaimerHelp}" linkText="#{editstudybundle.disclaimerLabel}" heading="#{editstudybundle.disclaimerHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.disclaimer].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.disclaimer].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup >
                                        <h:inputTextarea cols="90" rows="4" 
                                                         id="input_disclaimer" 
                                                         value="#{TemplateFormPage.template.metadata.disclaimer}" 
                                                         required="#{TemplateFormPage.studyMap[sfc.disclaimer].required}"
                                                         styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputTextarea>
                                        <h:message styleClass="errorMessage" for="input_disclaimer"/>
                                    </ui:panelGroup>    
                                    
                                </h:panelGrid>
                                
                                <ui:panelGroup block="true" id="groupPanel15" styleClass="vdcStudyInfoHeader #{TemplateFormPage.noteInputLevel}" >
                                    <h:outputText id="outputText160" value="Other Information"/>
                                    
                                </ui:panelGroup>
                                
                                <!-- this is a dummy datatable wrapper used to force data retention on validation error-->
                                <h:dataTable rowClasses="#{TemplateFormPage.noteInputLevel}" cellpadding="0" cellspacing="0" width="100%" value="dummy_datatable" >
                                    <h:column>  
                                        <h:dataTable id="dataTableNotes" cellpadding="0" cellspacing="0"
                                                     columnClasses="vdcEditStudyCol1b, vdcEditStudyCol2, vdcEditStudyCol3"
                                                     binding="#{TemplateFormPage.dataTableNotes}" 
                                                     value="#{TemplateFormPage.template.metadata.studyNotes}" var="currentRow" width="100%" >
                                            
                                            <h:column>
                                                <ui:panelGroup block="true"  styleClass="vdcEditStudyField"> 
                                                    <dvn:inlinehelp helpMessage="#{editstudybundle.notesTypeHelp}" linkText="#{editstudybundle.notesTypeLabel}" heading="#{editstudybundle.notesTypeHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                    <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.noteInputLevel=='required'}"/>
                                                    <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.noteInputLevel=='recommended'}"/>
                                                </ui:panelGroup>
                                            </h:column>
                                            <h:column>
                                                <ui:panelGroup block="true" >
                                                    <ui:panelGroup styleClass="#{TemplateFormPage.studyMap[sfc.notesInformationType].templateField.fieldInputLevel.name}">
                                                        <dvn:inlinehelp helpMessage="#{editstudybundle.notesTypeHelp}" linkText="#{editstudybundle.notesTypeLabel}" heading="#{editstudybundle.notesTypeHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.notesInformationType].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.notesInformationType].recommended}"/>
                                                        <br/>
                                                        <h:inputText maxlength="255" binding="#{TemplateFormPage.inputNoteType}" id="input_notesInformationType" 
                                                                     value="#{currentRow.type}" 
                                                                     required="#{TemplateFormPage.studyMap[sfc.notesInformationType].required}"
                                                                     styleClass="formHtmlEnabled">
                                                            <f:validator validatorId="XhtmlValidator"/>
                                                        </h:inputText>
                                                        <h:message styleClass="errorMessage" for="input_notesInformationType"/>   
                                                        <br/>
                                                    </ui:panelGroup>
                                                    <ui:panelGroup styleClass="vdcEditStudyGroupField #{TemplateFormPage.studyMap[sfc.notesInformationSubject].templateField.fieldInputLevel.name}"  >
                                                        <dvn:inlinehelp helpMessage="#{editstudybundle.notesSubjectHelp}" linkText="#{editstudybundle.notesSubjectLabel}" heading="#{editstudybundle.notesSubjectHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.notesInformationSubject].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.notesInformationSubject].recommended}"/>
                                                        <f:verbatim><br/></f:verbatim>
                                                        <h:inputText id="input_notesInformationSubject" 
                                                                     maxlength="255"
                                                                     binding="#{TemplateFormPage.inputNoteSubject}"
                                                                     value="#{currentRow.subject}" 
                                                                     validator ="#{TemplateFormPage.validateStudyNote}"
                                                                     required="#{TemplateFormPage.studyMap[sfc.notesInformationSubject].required}"
                                                                     styleClass="formHtmlEnabled">
                                                            <f:validator validatorId="XhtmlValidator"/>
                                                        </h:inputText>
                                                        <h:message styleClass="errorMessage" for="input_notesInformationSubject"  />
                                                        <f:verbatim><br/></f:verbatim>
                                                    </ui:panelGroup>                                                   
                                                    <ui:panelGroup styleClass="vdcEditStudyGroupField #{TemplateFormPage.studyMap[sfc.notesText].templateField.fieldInputLevel.name} " >
                                                        <dvn:inlinehelp helpMessage="#{editstudybundle.notesTextHelp}" linkText="#{editstudybundle.notesTextLabel}" heading="#{editstudybundle.notesTextHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{TemplateFormPage.studyMap[sfc.notesText].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{TemplateFormPage.studyMap[sfc.notesText].recommended}"/>
                                                        <f:verbatim><br/></f:verbatim>
                                                        <h:inputText id="input_notesText" 
                                                                     size="90" 
                                                                     binding="#{TemplateFormPage.inputNoteText}"
                                                                     validator ="#{TemplateFormPage.validateStudyNote}"
                                                                     value="#{currentRow.text}" 
                                                                     required="#{TemplateFormPage.studyMap[sfc.notesText].required}"
                                                                     styleClass="formHtmlEnabled">
                                                            <f:validator validatorId="XhtmlValidator"/>
                                                        </h:inputText>
                                                        <h:message styleClass="errorMessage" for="input_notesText"/>
                                                        <f:verbatim><br/></f:verbatim>
                                                    </ui:panelGroup>
                                                    
                                                </ui:panelGroup>
                                            </h:column> 
                                            <h:column>
                                                <ui:panelGroup block="true" > 
                                                    <h:commandButton   image="/resources/icon_add.gif" actionListener="#{TemplateFormPage.addRow}"/> 
                                                    <h:commandButton   image="/resources/icon_remove.gif" actionListener="#{TemplateFormPage.removeRow}" /> 
                                                </ui:panelGroup>
                                            </h:column>                                       
                                        </h:dataTable>
                                    </h:column>
                                </h:dataTable>                                
                                
                            </ui:panelLayout>
                     
                    
                    <ui:panelGroup block="true" styleClass="vdcTextRight" style="padding-top: 15px;">              
                        <h:commandButton id="button1b" value="Save" action="#{TemplateFormPage.save}"/>
                        <h:commandButton immediate="true" id="button2b" style="margin-left: 30px; margin-right: 15px" value="Cancel" action="#{TemplateFormPage.cancel}"/>   
                    </ui:panelGroup> 
                    
                </div>
            </div>
            
        </h:form>
        <f:verbatim>           
        
            <script type="text/javascript">
                addLoadEvent(initHide());
            </script>
        </f:verbatim>     
     </gui:define>
    </gui:composition>
  </body>
</html>
