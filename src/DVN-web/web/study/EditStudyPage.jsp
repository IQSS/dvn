<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
          xmlns:h="http://java.sun.com/jsf/html" 
          xmlns:jsp="http://java.sun.com/JSP/Page" 
          xmlns:ui="http://www.sun.com/web/ui"
          xmlns:tiles="http://struts.apache.org/tags-tiles"
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          xmlns:ihelp="/WEB-INF/tlds/InlineHelp"
          xmlns:ihelpsupport="/WEB-INF/tlds/InlineHelpSupport">
        
    <f:loadBundle basename="EditStudyBundle" var="editstudybundle"/>
    
    <f:subview id="editStudyPageView">
        <f:verbatim>           
            <script language="Javascript">
                    // functions to work with category selection
                    // note a few assumptions that these functions have:
                    // the id of the dropdown (including parent nodes); the partial id of the checkboxes;
                    // and that the checkbox is two elements before the text field (there is empty text
                    // in between, created by the div)
                   function updateCategory( checkbox ) {
                        if (checkbox.checked) {
                            textField = document.getElementById(checkbox.id).nextSibling.nextSibling;
                            dropdown = document.getElementById("content:editStudyPageView:studyForm:tabSet1:files:fileDataTableWrapper:0:fileDataTable:catDropdown");
                            textField.value = dropdown.value;
                        }
                    }
                    function deselectCategory( textField ) {
                        checkbox = document.getElementById(textField.id).previousSibling.previousSibling;
                        checkbox.checked = false;
                    }
                    function updateAllCheckedCategories( dropdown ) {
                        checkboxes=document.getElementsByTagName("input");
                         for(i=0; i &lt; checkboxes.length; i++) {
                            if (checkboxes[i].id.indexOf("catCheckbox") != -1 &amp;&amp; checkboxes[i].checked == true) {
                                textField = checkboxes[i].nextSibling.nextSibling;
                                textField.value = dropdown.value;                            
                            }
                         }
                    }  
                    /** commenting out this code pending
                    * input from alpha testing re: validation
                    * required or no
                    * To activate uncomment the function
                    * below and uncomment the related div 
                    * just below the author name. See below. 
                    * @author Wendy Bossons
                    */
                    function validateNameFormat(obj) {
                        if (obj.value.length == 0) {
                            return;
                        } else {
                            var regexp = /[^\s^]/i;
                            var helpObj = eval("document.getElementById('input_authorNameHelp')");
                            if (regexp.test(obj.value))
                                helpObj.style.display = 'block';
                        } 
                    }
            </script> 
        </f:verbatim>
        <ihelpsupport:inlinehelpsupport writeHelpDiv="true" writeTipDiv="true" rendered="true"/>
        <f:loadBundle  basename="BundleHelp" var="helpText" />
        <h:form id="studyForm">
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
            <input type="hidden" name="pageName" value="EditStudyPage"/>
            <h:inputHidden id="studyId" value="#{EditStudyPage.studyId}" />                        
          
            
            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                    
                        <h:outputText value="#{EditStudyPage.study.title}"/>
                     
                </div>            
                <div class="dvn_sectionBoxNoBorders"> 
                    
                    <!-- validation errors occurred? -->
                    <ui:panelGroup id="messagePanel" styleClass="errorMessage" >
                        <h:outputText  value="Some errors occurred, please check details below" rendered="#{! empty facesContext.maximumSeverity}" />
                    </ui:panelGroup>
                    
                    <ui:panelGroup block="true" id="groupPanel10" styleClass="vdcTextRight">              
                        <h:commandButton id="button1" value="Save" action="#{EditStudyPage.save}"/>
                        <h:commandButton immediate="true" id="button2" style="margin-left: 30px; margin-right: 15px" value="Cancel" action="#{EditStudyPage.cancel}"/>   
                    </ui:panelGroup> 
                    
                    <ui:tabSet binding="#{EditStudyPage.tabSet1}" id="tabSet1" lite="true" mini="true" >
                        <ui:tab  action="#{EditStudyPage.tab1_action}" id="catalog" text="Cataloging Information">                                  
                           <ui:panelLayout id="layoutPanel1" panelLayout="flow" style="width: 98%"> 
                                <h:panelGrid cellpadding="0" cellspacing="0" columns="2" width="100%" style="margin-bottom: 5px"> 
                                    <ui:panelGroup block="true" id="groupPanel9b" >
                                        <h:commandButton  value="Show Additional Fields" title="See all fields in Cataloging Information"  binding="#{EditStudyPage.commandButtonShowFields}"  action="#{EditStudyPage.showFields}"/>
                                        <h:commandButton  value="Hide Additional Fields" title="Hide additional fields in Cataloging Information"  binding="#{EditStudyPage.commandButtonHideFields}"  rendered="false" actionListener="#{EditStudyPage.hideFields}"/>                    
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
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.titleHelp}" linkText="#{editstudybundle.titleLabel}" heading="#{editstudybundle.titleHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>                                            
                                        <h:graphicImage id="image39" value="/resources/icon_required.gif" rendered="#{EditStudyPage.studyMap[sfc.title].required}" />
                                        <h:graphicImage id="image40" value="/resources/icon_recommended.gif" rendered="#{EditStudyPage.studyMap[sfc.title].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup block="true" id="groupPanel16" >
                                        <h:inputText id="input_title" size="90"  value="#{EditStudyPage.study.title}" required="#{EditStudyPage.studyMap[sfc.title].required}"/>
                                        <h:message styleClass="errorMessage" for="input_title"/> 
                                    </ui:panelGroup> 
                                    
                                    <ui:panelGroup block="true"  styleClass="vdcEditStudyField"  rendered="#{EditStudyPage.studyMap[sfc.subTitle].rendered}">                                              
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.subtitleHelp}" linkText="#{editstudybundle.subtitleLabel}" heading="#{editstudybundle.subtitleHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>                                             
                                        <h:graphicImage  value="/resources/icon_required.gif" rendered="#{EditStudyPage.studyMap[sfc.subTitle].required}" />
                                        <h:graphicImage  value="/resources/icon_recommended.gif" rendered="#{EditStudyPage.studyMap[sfc.subTitle].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup block="true" rendered="#{EditStudyPage.studyMap[sfc.subTitle].rendered}">
                                        <h:inputText id="input_subtitle" size="90" value="#{EditStudyPage.study.subTitle}" required="#{EditStudyPage.studyMap[sfc.subTitle].required}"/>                                                                 
                                        <h:message styleClass="errorMessage" for="input_subtitle"/> 
                                    </ui:panelGroup> 
                                    
                                    <!-- STUDY ID-->
                                            
                                    <!--ui:panelGroup id="groupPanel38" rendered="#{!EditStudyPage.studyMap[sfc.studyId].rendered or EditStudyPage.commandButtonHideFields.rendered}"-->
                                    <ui:panelGroup block="true"  id="groupPanel38" rendered="#{EditStudyPage.studyMap[sfc.studyId].rendered}" styleClass="vdcEditStudyField">                                              
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.studyidHelp}" linkText="#{editstudybundle.studyidLabel}" heading="#{editstudybundle.studyidHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        
                                        <h:graphicImage  value="/resources/icon_required.gif" rendered="#{EditStudyPage.studyMap[sfc.studyId].required and EditStudyPage.study.id==null}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif" rendered="#{EditStudyPage.studyMap[sfc.studyId].recommended and EditStudyPage.study.id==null}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup block="true" id="groupPanel35" rendered="#{EditStudyPage.studyMap[sfc.studyId].rendered} " >
                                        <h:outputText value="#{EditStudyPage.study.protocol}:#{EditStudyPage.study.authority}/" rendered="#{EditStudyPage.study.id==null}" />                                     
                                        <h:inputText maxlength="255" id="input_studyId" value="#{EditStudyPage.study.studyId}"  validator ="#{EditStudyPage.validateStudyId}" required="#{EditStudyPage.studyMap[sfc.studyId].required}" rendered="#{EditStudyPage.study.id==null}" />
                                        <h:outputText value="#{EditStudyPage.study.globalId}" rendered="#{EditStudyPage.study.id!=null}" />
                                        <h:message styleClass="errorMessage" for="input_studyId"/> 
                                    </ui:panelGroup>
                                    
                                    
                                </h:panelGrid>
                                
                                <!-- OTHER IDS -->
                                <!-- this is a dummy datatable wrapper used to force data retention on validation error-->
                                <h:dataTable cellpadding="0" cellspacing="0" width="100%" value="dummy_datatable" >
                                    <h:column>                                  
                                        <h:dataTable cellpadding="0" cellspacing="0" 
                                                     columnClasses="vdcEditStudyCol1b, vdcEditStudyCol2, vdcEditStudyCol3"
                                                     binding="#{EditStudyPage.dataTableOtherIds}" 
                                                     value="#{EditStudyPage.study.studyOtherIds}" var="currentRow" width="100%" 
                                                     rendered="#{EditStudyPage.studyMap[sfc.otherId].rendered}">
                                            
                                            <h:column >
                                                <ui:panelGroup block="true" styleClass="vdcEditStudyField"> 
                                                    <ihelp:inlinehelp helpMessage="#{editstudybundle.otheridHelp}" linkText="#{editstudybundle.otheridLabel}" heading="#{editstudybundle.otheridHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                    <h:graphicImage  value="/resources/icon_required.gif" rendered="#{EditStudyPage.studyMap[sfc.otherId].required}"/>
                                                    <h:graphicImage  value="/resources/icon_recommended.gif" rendered="#{EditStudyPage.studyMap[sfc.otherId].recommended}"/>
                                                </ui:panelGroup> 
                                            </h:column>
                                            <h:column >
                                                <ui:panelGroup block="true" > 
                                                    <h:inputText binding="#{EditStudyPage.inputOtherId}" id = "input_otherId" value="#{currentRow.otherId}" required="#{EditStudyPage.studyMap[sfc.otherId].required}"/>
                                                    <h:message styleClass="errorMessage" for="input_otherId"/> 
                                                    <ui:panelGroup styleClass="vdcEditStudyGroupField">
                                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.otherAgencyHelp}" linkText="#{editstudybundle.otherAgencyLabel}" heading="#{editstudybundle.otherAgencyHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif" rendered="#{EditStudyPage.studyMap[sfc.otherIdAgency].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif" rendered="#{EditStudyPage.studyMap[sfc.otherIdAgency].recommended}"/>
                                                        <h:inputText  binding="#{EditStudyPage.inputOtherIdAgency}"     validator ="#{EditStudyPage.validateStudyOtherId}" 
                                                                      id="input_otherIdAgency" value="#{currentRow.agency}"  required="#{EditStudyPage.studyMap[sfc.otherIdAgency].required}"/>
                                                    </ui:panelGroup>
                                                    <h:message styleClass="errorMessage" for="input_otherIdAgency"/> 
                                                </ui:panelGroup>
                                            </h:column> 
                                            <h:column id="groupPanel61">
                                                <!--f:facet name="header">
                                                    <ui:panelGroup-->
                                                <h:commandButton  image="/resources/icon_add.gif" actionListener="#{EditStudyPage.addRow}"/> 
                                                <!--/ui:panelGroup>
                                                </f:facet-->
                                                <h:commandButton  image="/resources/icon_remove.gif" actionListener="#{EditStudyPage.removeRow}" /> 
                                            </h:column>
                                            
                                        </h:dataTable>
                                    </h:column>
                                </h:dataTable>                                
                                
                                <!-- Authors -->
                                        
                                <!-- this is a dummy datatable wrapper used to force data retention on validation error-->
                                <h:dataTable cellpadding="0" cellspacing="0" width="100%" value="dummy_datatable" >
                                    <h:column>                                          
                                        <h:dataTable cellpadding="0" cellspacing="0"
                                                     columnClasses="vdcEditStudyCol1b, vdcEditStudyCol2, vdcEditStudyCol3"
                                                     binding="#{EditStudyPage.dataTableAuthors}" 
                                                     value="#{EditStudyPage.study.studyAuthors}" var="currentAuthor" width="100%"  
                                                     rendered="#{EditStudyPage.studyMap[sfc.authorName].rendered}">
                                            
                                            <h:column>
                                                <ui:panelGroup block="true" styleClass="vdcEditStudyField"> 
                                                    <ihelp:inlinehelp helpMessage="#{editstudybundle.authorHelp}" linkText="#{editstudybundle.authorLabel}" heading="#{editstudybundle.authorHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                    <h:graphicImage  value="/resources/icon_required.gif" rendered="#{EditStudyPage.studyMap[sfc.authorName].required}"/>
                                                    <h:graphicImage  value="/resources/icon_recommended.gif" rendered="#{EditStudyPage.studyMap[sfc.authorName].recommended}"/>
                                                </ui:panelGroup>
                                            </h:column>
                                            
                                            <h:column>
                                                <ui:panelGroup block="true" >
                                                    <h:inputText id="input_authorName" 
                                                                 value="#{currentAuthor.name}" 
                                                                 size="45" 
                                                                 binding="#{EditStudyPage.inputAuthorName}"                                                      
                                                                 required="#{EditStudyPage.studyMap[sfc.authorName].required}" 
                                                    /> 
                                                    <h:message styleClass="errorMessage" for="input_authorName"/>
                                                    <!-- Commenting out this code pending alpha testing input
                                                 To restore, add this to inputText above, onblur="validateNameFormat(this);"
                                                 and uncomment the f:verbatim block below.
                                                 <f:verbatim>
                                                    <div id="input_authorNameHelp" class="errorMessage" style="display:none;">
                                                        Info: Please enter your name in the format: &lt;First Name&gt; &lt;Last Name&gt;.
                                                        First name is optional, but should be separated by a comma from the last name if present.
                                                    </div>
                                                </f:verbatim> end comment -->
                                                    <ui:panelGroup >
                                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.authorAffiliationHelp}" linkText="#{editstudybundle.authorAffiliationLabel}" heading="#{editstudybundle.authorAffiliationHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif" rendered="#{EditStudyPage.studyMap[sfc.authorAffiliation].required}"/>
                                                        <h:graphicImage id="image83" value="/resources/icon_recommended.gif" rendered="#{EditStudyPage.studyMap[sfc.authorAffiliation].recommended}"/>
                                                        <h:inputText id="input_authorAffiliation" size="45"
                                                                     binding="#{EditStudyPage.inputAuthorAffiliation}"
                                                                     value="#{currentAuthor.affiliation}" 
                                                                     validator ="#{EditStudyPage.validateStudyAuthor}" 
                                                                     required="#{EditStudyPage.studyMap[sfc.authorAffiliation].required}"
                                                                     styleClass="formHtmlEnabled">
                                                            <f:validator validatorId="XhtmlValidator"/>
                                                        </h:inputText>
                                                    </ui:panelGroup>
                                                    <h:message styleClass="errorMessage" for="input_authorAffiliation"/>
                                                    <f:verbatim><br /></f:verbatim>
                                                </ui:panelGroup>
                                            </h:column>
                                            
                                            <h:column>
                                                <h:commandButton   image="/resources/icon_add.gif" actionListener="#{EditStudyPage.addRow}"/> 
                                                <h:commandButton   image="/resources/icon_remove.gif" actionListener="#{EditStudyPage.removeRow}" /> 
                                            </h:column>               
                                        </h:dataTable>
                                    </h:column>
                                </h:dataTable>
                                
                                <!--Producers-->   
                                <!-- this is a dummy datatable wrapper used to force data retention on validation error-->
                                <h:dataTable cellpadding="0" cellspacing="0" width="100%" value="dummy_datatable" >
                                    <h:column>                                            
                                        <h:dataTable cellpadding="0" cellspacing="0"
                                                     columnClasses="vdcEditStudyCol1b, vdcEditStudyCol2, vdcEditStudyCol3"
                                                     binding="#{EditStudyPage.dataTableProducers}" 
                                                     value="#{EditStudyPage.study.studyProducers}" var="currentRow" width="100%"  
                                                     rendered="#{EditStudyPage.studyMap[sfc.producerName].rendered}">
                                            
                                            <h:column>
                                                <ui:panelGroup block="true" styleClass="vdcEditStudyField"> 
                                                    <ihelp:inlinehelp helpMessage="#{editstudybundle.producerHelp}" linkText="#{editstudybundle.producerLabel}" heading="#{editstudybundle.producerHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                    <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.producerName].required}"/>
                                                    <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.producerName].recommended}"/>
                                                </ui:panelGroup>
                                            </h:column>
                                            <h:column>
                                                <ui:panelGroup block="true"> 
                                                    <h:inputText  binding="#{EditStudyPage.inputProducerName}" id="input_producerName" value="#{currentRow.name}" size="45" required="#{EditStudyPage.studyMap[sfc.producerName].required}"/>
                                                    <h:message styleClass="errorMessage" for="input_producerName"/>
                                                    <ui:panelGroup styleClass="vdcEditStudyGroupField">
                                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.producerAffiliationHelp}" linkText="#{editstudybundle.producerAffiliationLabel}" heading="#{editstudybundle.producerAffiliationHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.producerAffiliation].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.producerAffiliation].recommended}"/>
                                                        <h:inputText  validator ="#{EditStudyPage.validateStudyProducer}"  binding="#{EditStudyPage.inputProducerAffiliation}"    id="input_producerAffiliation" size="45" value="#{currentRow.affiliation}" required="#{EditStudyPage.studyMap[sfc.producerAffiliation].required}"
                                                                      styleClass="formHtmlEnabled">
                                                            <f:validator validatorId="XhtmlValidator"/>
                                                        </h:inputText>
                                                    </ui:panelGroup>
                                                    <h:message styleClass="errorMessage" for="input_producerAffiliation"/>
                                                    <f:verbatim><br /></f:verbatim>
                                                    <ui:panelGroup styleClass="vdcEditStudyGroupField">
                                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.producerAbbreviationHelp}" linkText="#{editstudybundle.producerAbbreviationLabel}" heading="#{editstudybundle.producerAbbreviationHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.producerAbbreviation].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.producerAbbreviation].recommended}"/>
                                                        <h:inputText validator ="#{EditStudyPage.validateStudyProducer}"   binding="#{EditStudyPage.inputProducerAbbreviation}"  id="input_producerAbbreviation" value="#{currentRow.abbreviation}" required="#{EditStudyPage.studyMap[sfc.producerAbbreviation].required}"/>
                                                    </ui:panelGroup>
                                                    <h:message styleClass="errorMessage" for="input_producerAbbreviation"/>
                                                    <ui:panelGroup styleClass="vdcEditStudyGroupField">
                                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.producerURLHelp}" linkText="#{editstudybundle.producerURLLabel}" heading="#{editstudybundle.producerURLHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.producerURL].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.producerURL].recommended}"/>
                                                        <h:inputText id="input_producerURL" 
                                                                     validator ="#{EditStudyPage.validateStudyProducer}"
                                                                     binding="#{EditStudyPage.inputProducerUrl}" 
                                                                     value="#{currentRow.url}" 
                                                                     size="45" 
                                                                     required="#{EditStudyPage.studyMap[sfc.producerURL].required}"
                                                        >
                                                            <f:validator validatorId="UrlValidator"/>
                                                        </h:inputText>
                                                    </ui:panelGroup>
                                                    <h:message styleClass="errorMessage" for="input_producerURL"/>
                                                    <ui:panelGroup styleClass="vdcEditStudyGroupField">
                                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.producerLogoHelp}" linkText="#{editstudybundle.producerLogoLabel}" heading="#{editstudybundle.producerLogoHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.producerLogo].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.producerLogo].recommended}"/>
                                                        <h:inputText id="input_producerLogo" 
                                                                     binding="#{EditStudyPage.inputProducerLogo}" 
                                                                     validator ="#{EditStudyPage.validateStudyProducer}"
                                                                     value="#{currentRow.logo}" 
                                                                     size="45" 
                                                                     required="#{EditStudyPage.studyMap[sfc.producerLogo].required}"
                                                        >
                                                            <f:validator validatorId="UrlValidator"/>
                                                        </h:inputText>
                                                    </ui:panelGroup>
                                                    <h:message styleClass="errorMessage" for="input_producerLogo"/>
                                                </ui:panelGroup>
                                            </h:column>
                                            <h:column>
                                                <h:commandButton   image="/resources/icon_add.gif" actionListener="#{EditStudyPage.addRow}"/> 
                                                <h:commandButton   image="/resources/icon_remove.gif" actionListener="#{EditStudyPage.removeRow}" /> 
                                            </h:column>
                                        </h:dataTable>
                                    </h:column>
                                </h:dataTable>
                                
                                <h:panelGrid cellpadding="0" cellspacing="0"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"
                                             width="100%">
                                    
                                    
                                    <ui:panelGroup id="groupPanel42"  rendered="#{EditStudyPage.studyMap[sfc.productionDate].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.productionDateHelp}" linkText="#{editstudybundle.productionDateLabel}" heading="#{editstudybundle.productionDateHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.productionDate].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.productionDate].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup id="groupPanel19"  rendered="#{EditStudyPage.studyMap[sfc.productionDate].rendered}">
                                        <h:inputText id="input_productionDate" styleClass="formDateFormat" value="#{EditStudyPage.study.productionDate}"  required="#{EditStudyPage.studyMap[sfc.productionDate].required}">
                                            <f:validator validatorId="DateValidator"/>    
                                        </h:inputText>
                                        <h:outputText value=" (Enter date as YYYY or YYYY-MM or YYYY-MM-DD)"/>
                                        <h:message styleClass="errorMessage" for="input_productionDate"><f:param name="x" value="Production Date:" /> </h:message>
                                    </ui:panelGroup>
                                    
                                    
                                    <ui:panelGroup id="groupPanel43"  rendered="#{EditStudyPage.studyMap[sfc.productionPlace].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.productionPlaceHelp}" linkText="#{editstudybundle.productionPlaceLabel}" heading="#{editstudybundle.productionPlaceHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.productionPlace].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.productionPlace].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup id="groupPanel20"  rendered="#{EditStudyPage.studyMap[sfc.productionPlace].rendered}">
                                        <h:inputText id="input_productionPlace" size="45"  value="#{EditStudyPage.study.productionPlace}" required="#{EditStudyPage.studyMap[sfc.productionPlace].required}"/>
                                        <h:message styleClass="errorMessage" for="input_productionPlace"/>
                                    </ui:panelGroup>
                                    
                                </h:panelGrid>     
                                <!-- Software -->
                                            
                                <!-- this is a dummy datatable wrapper used to force data retention on validation error-->
                                <h:dataTable cellpadding="0" cellspacing="0" width="100%" value="dummy_datatable" >
                                    <h:column>
                                        <h:dataTable cellpadding="0" cellspacing="0"
                                                     columnClasses="vdcEditStudyCol1b, vdcEditStudyCol2, vdcEditStudyCol3"
                                                     binding="#{EditStudyPage.dataTableSoftware}" 
                                                     value="#{EditStudyPage.study.studySoftware}" var="currentRow" width="100%"  
                                                     rendered="#{EditStudyPage.studyMap[sfc.softwareName].rendered}">
                                            
                                            <h:column>
                                                <ui:panelGroup block="true" styleClass="vdcEditStudyField" > 
                                                    <ihelp:inlinehelp helpMessage="#{editstudybundle.softwareNameHelp}" linkText="#{editstudybundle.softwareNameLabel}" heading="#{editstudybundle.softwareNameHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                    <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.softwareName].required}"/>
                                                    <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.softwareName].recommended}"/>
                                                </ui:panelGroup>
                                            </h:column>
                                            <h:column>
                                                <ui:panelGroup block="true">
                                                    <h:inputText  binding="#{EditStudyPage.inputSoftwareName}" id="input_softwareName" size="45"  value="#{currentRow.name}" required="#{EditStudyPage.studyMap[sfc.softwareName].required}"/>
                                                    <h:message styleClass="errorMessage" for="input_softwareName"/>  
                                                    <ui:panelGroup styleClass="vdcEditStudyGroupField">
                                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.softwareVersionHelp}" linkText="#{editstudybundle.softwareVersionLabel}" heading="#{editstudybundle.softwareVersionHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.softwareVersion].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.softwareVersion].recommended}"/>
                                                        <h:inputText  binding="#{EditStudyPage.inputSoftwareVersion}" validator ="#{EditStudyPage.validateStudySoftware}"  id="input_softwareVersion" value="#{currentRow.softwareVersion}" required="#{EditStudyPage.studyMap[sfc.softwareVersion].required}"/>
                                                    </ui:panelGroup>
                                                    <h:message styleClass="errorMessage" for="input_softwareVersion"/>                                
                                                </ui:panelGroup>
                                            </h:column> 
                                            <h:column>
                                                <h:commandButton   image="/resources/icon_add.gif" actionListener="#{EditStudyPage.addRow}"/> 
                                                <h:commandButton   image="/resources/icon_remove.gif" actionListener="#{EditStudyPage.removeRow}" /> 
                                            </h:column>                                       
                                        </h:dataTable>
                                    </h:column>
                                </h:dataTable>                                
                                <h:panelGrid cellpadding="0" cellspacing="0"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"
                                             width="100%">
                                    
                                    
                                    <ui:panelGroup id="groupPanel45"  rendered="#{EditStudyPage.studyMap[sfc.fundingAgency].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.fundingAgencyHelp}" linkText="#{editstudybundle.fundingAgencyLabel}" heading="#{editstudybundle.fundingAgencyHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.fundingAgency].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.fundingAgency].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup id="groupPanel22"  rendered="#{EditStudyPage.studyMap[sfc.fundingAgency].rendered}">
                                        <h:inputText id="input_fundingAgency" size="45"  value="#{EditStudyPage.study.fundingAgency}" required="#{EditStudyPage.studyMap[sfc.fundingAgency].required}"/>
                                        <h:message styleClass="errorMessage" for="input_fundingAgency"/>                                
                                    </ui:panelGroup>
                                    
                                </h:panelGrid>        
                                <!-- this is a dummy datatable wrapper used to force data retention on validation error-->
                                <h:dataTable cellpadding="0" cellspacing="0" width="100%" value="dummy_datatable" >
                                    <h:column>                                
                                        <h:dataTable cellpadding="0" cellspacing="0"
                                                     columnClasses="vdcEditStudyCol1b, vdcEditStudyCol2, vdcEditStudyCol3"
                                                     binding="#{EditStudyPage.dataTableGrants}" 
                                                     value="#{EditStudyPage.study.studyGrants}" var="currentRow" width="100%"  
                                                     rendered="#{EditStudyPage.studyMap[sfc.grantNumber].rendered}">
                                            
                                            <h:column>
                                                <ui:panelGroup block="true" styleClass="vdcEditStudyField" > 
                                                    <ihelp:inlinehelp helpMessage="#{editstudybundle.grantNumberHelp}" linkText="#{editstudybundle.grantNumberLabel}" heading="#{editstudybundle.grantNumberHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                    <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.grantNumber].required}"/>
                                                    <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.grantNumber].recommended}"/>
                                                </ui:panelGroup>                                              
                                            </h:column>
                                            <h:column>
                                                <ui:panelGroup block="true">
                                                    <h:inputText  immediate="true" binding="#{EditStudyPage.inputGrantNumber}" id="input_grantNumber" size="45"  value="#{currentRow.number}" required="#{EditStudyPage.studyMap[sfc.grantNumber].required}"/>
                                                    <h:message styleClass="errorMessage" for="input_grantNumber"/>    
                                                    <ui:panelGroup styleClass="vdcEditStudyGroupField">
                                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.grantingAgencyHelp}" linkText="#{editstudybundle.grantingAgencyLabel}" heading="#{editstudybundle.grantingAgencyHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.grantNumberAgency].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.grantNumberAgency].recommended}"/>
                                                        <h:inputText  immediate="true" binding="#{EditStudyPage.inputGrantAgency}" validator ="#{EditStudyPage.validateStudyGrant}" id="input_grantNumberAgency" size="45" value="#{currentRow.agency}" required="#{EditStudyPage.studyMap[sfc.grantNumberAgency].required}"/>
                                                    </ui:panelGroup>
                                                    <h:message styleClass="errorMessage" for="input_grantNumberAgency"/>                                
                                                </ui:panelGroup>
                                            </h:column>
                                            <h:column>
                                                <h:commandButton   image="/resources/icon_add.gif" actionListener="#{EditStudyPage.addRow}"/> 
                                                <h:commandButton   image="/resources/icon_remove.gif" actionListener="#{EditStudyPage.removeRow}" /> 
                                            </h:column>
                                        </h:dataTable>
                                    </h:column>
                                </h:dataTable>                                
                                
                                <h:column>                                
                                    <h:dataTable cellpadding="0" cellspacing="0"
                                                 columnClasses="vdcEditStudyCol1b, vdcEditStudyCol2, vdcEditStudyCol3"
                                                 binding="#{EditStudyPage.dataTableDistributors}" 
                                                 value="#{EditStudyPage.study.studyDistributors}" var="currentRow" width="100%" 
                                                 rendered="#{EditStudyPage.studyMap[sfc.distributorName].rendered}">
                                        
                                        <h:column>
                                            <ui:panelGroup block="true" styleClass="vdcEditStudyField" > 
                                                <ihelp:inlinehelp helpMessage="#{editstudybundle.distributorHelp}" linkText="#{editstudybundle.distributorLabel}" heading="#{editstudybundle.distributorHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.distributorName].required}"/>
                                                <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.distributorName].recommended}"/>
                                            </ui:panelGroup>
                                        </h:column>
                                        <h:column>
                                            <ui:panelGroup block="true">
                                                <h:inputText  binding="#{EditStudyPage.inputDistributorName}" id="input_distributorName" value="#{currentRow.name}" size="45" required="#{EditStudyPage.studyMap[sfc.distributorName].required}"/>
                                                <h:message styleClass="errorMessage" for="input_distributorName"/> 
                                                <ui:panelGroup styleClass="vdcEditStudyGroupField">
                                                    <ihelp:inlinehelp helpMessage="#{editstudybundle.distributorAffiliationHelp}" linkText="#{editstudybundle.distributorAffiliationLabel}" heading="#{editstudybundle.distributorAffiliationHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                    <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.distributorAffiliation].required}"/>
                                                    <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.distributorAffiliation].recommended}"/>
                                                    <h:inputText id="input_distributorAffiliation" validator ="#{EditStudyPage.validateStudyDistributor}" binding="#{EditStudyPage.inputDistributorAffiliation}" size="45"
                                                                 value="#{currentRow.affiliation}" 
                                                                 required="#{EditStudyPage.studyMap[sfc.distributorAffiliation].required}" 
                                                                 styleClass="formHtmlEnabled">
                                                        <f:validator validatorId="XhtmlValidator"/>
                                                    </h:inputText>
                                                </ui:panelGroup>
                                                <h:message styleClass="errorMessage" for="input_distributorAffiliation"/> 
                                                <f:verbatim><br /></f:verbatim>
                                                <ui:panelGroup styleClass="vdcEditStudyGroupField">
                                                    <ihelp:inlinehelp helpMessage="#{editstudybundle.distributorAbbreviationHelp}" linkText="#{editstudybundle.distributorAbbreviationLabel}" heading="#{editstudybundle.distributorAbbreviationHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                    <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.distributorAbbreviation].required}"/>
                                                    <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.distributorAbbreviation].recommended}"/>
                                                    <h:inputText validator ="#{EditStudyPage.validateStudyDistributor}" binding="#{EditStudyPage.inputDistributorAbbreviation}" id="input_distributorAbbreviation" value="#{currentRow.abbreviation}"  required="#{EditStudyPage.studyMap[sfc.distributorAbbreviation].required}"/>
                                                </ui:panelGroup>
                                                <h:message styleClass="errorMessage" for="input_distributorAbbreviation"/>   
                                                <ui:panelGroup styleClass="vdcEditStudyGroupField">
                                                    <ihelp:inlinehelp helpMessage="#{editstudybundle.distributorURLHelp}" linkText="#{editstudybundle.distributorURLLabel}" heading="#{editstudybundle.distributorURLHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                    <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.distributorURL].required}"/>
                                                    <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.distributorURL].recommended}"/>
                                                    <h:inputText id="input_distributorURL" 
                                                                 validator ="#{EditStudyPage.validateStudyDistributor}"
                                                                 binding="#{EditStudyPage.inputDistributorUrl}" 
                                                                 value="#{currentRow.url}" 
                                                                 size="45"  
                                                                 required="#{EditStudyPage.studyMap[sfc.distributorURL].required}"
                                                    >
                                                        <f:validator validatorId="UrlValidator"/>
                                                    </h:inputText>
                                                </ui:panelGroup>
                                                <h:message styleClass="errorMessage" for="input_distributorURL"/>   
                                                <ui:panelGroup styleClass="vdcEditStudyGroupField">
                                                    <ihelp:inlinehelp helpMessage="#{editstudybundle.distributorLogoHelp}" linkText="#{editstudybundle.distributorLogoLabel}" heading="#{editstudybundle.distributorLogoHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                    <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.distributorLogo].required}"/>
                                                    <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.distributorLogo].recommended}"/>
                                                    <h:inputText binding="#{EditStudyPage.inputDistributorLogo}" id="input_distributorLogo"  
                                                                 validator ="#{EditStudyPage.validateStudyDistributor}"
                                                                 value="#{currentRow.logo}" 
                                                                 size="45"  
                                                                 required="#{EditStudyPage.studyMap[sfc.distributorLogo].required}"
                                                    >
                                                        <f:validator validatorId="UrlValidator"/>
                                                    </h:inputText>
                                                </ui:panelGroup>
                                                <h:message styleClass="errorMessage" for="input_distributorLogo"/>   
                                            </ui:panelGroup>
                                        </h:column>
                                        <h:column>
                                            <h:commandButton   image="/resources/icon_add.gif" actionListener="#{EditStudyPage.addRow}"/> 
                                            <h:commandButton   image="/resources/icon_remove.gif" actionListener="#{EditStudyPage.removeRow}" /> 
                                        </h:column>
                                    </h:dataTable>
                                </h:column>
                                
                                <h:panelGrid cellpadding="0" cellspacing="0"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2"
                                             width="100%" >
                                    
                                    
                                    <ui:panelGroup  rendered="#{EditStudyPage.studyMap[sfc.distributorContact].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.distributorContactHelp}" linkText="#{editstudybundle.distributorContactLabel}" heading="#{editstudybundle.distributorContactHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.distributorContact].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.distributorContact].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup  rendered="#{EditStudyPage.studyMap[sfc.distributorContact].rendered}">
                                        <h:inputText binding ="#{EditStudyPage.inputDistributorContact}" id="input_distributorContact" size="45"  value="#{EditStudyPage.study.distributorContact}"  required="#{EditStudyPage.studyMap[sfc.distributorContact].required}"/>
                                        <h:message styleClass="errorMessage" for="input_distributorContact"/>                                
                                        <ui:panelGroup styleClass="vdcEditStudyGroupField" rendered="#{EditStudyPage.studyMap[sfc.distributorContact].rendered}">
                                            <ihelp:inlinehelp helpMessage="#{editstudybundle.distributorContactAffiliationHelp}" linkText="#{editstudybundle.distributorContactAffiliationLabel}" heading="#{editstudybundle.distributorContactAffiliationHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                            <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.distributorContactAffiliation].required}"/>
                                            <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.distributorContactAffiliation].recommended}"/>
                                            <h:inputText id="distributorContactAffiliation"  
                                                         binding ="#{EditStudyPage.inputDistributorContactAffiliation}" 
                                                         value="#{EditStudyPage.study.distributorContactAffiliation}" 
                                                         validator="#{EditStudyPage.validateDistributorContact}"
                                                         required="#{EditStudyPage.studyMap[sfc.distributorContactAffiliation].required}"
                                                         styleClass="formHtmlEnabled">
                                                <f:validator validatorId="XhtmlValidator"/>
                                            </h:inputText>
                                            <h:message styleClass="errorMessage" for="distributorContactAffiliation"/>                                
                                        </ui:panelGroup>
                                        <ui:panelGroup styleClass="vdcEditStudyGroupField" rendered="#{EditStudyPage.studyMap[sfc.distributorContactEmail].rendered}">
                                            <ihelp:inlinehelp helpMessage="#{editstudybundle.distributorContactEmailHelp}" linkText="#{editstudybundle.distributorContactEmailLabel}" heading="#{editstudybundle.distributorContactEmailHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                            <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.distributorContactEmail].required}"/>
                                            <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.distributorContactEmail].recommended}"/>
                                            <h:inputText  binding ="#{EditStudyPage.inputDistributorContactEmail}"  id="input_distributorContactEmail" size="45" value="#{EditStudyPage.study.distributorContactEmail}" required="#{EditStudyPage.studyMap[sfc.distributorContactEmail].required}"/>
                                            <h:message styleClass="errorMessage" for="input_distributorContactEmail"/>                                
                                        </ui:panelGroup>
                                    </ui:panelGroup>                                          
                                    
                                    
                                    <ui:panelGroup id="groupPanel49" rendered="#{EditStudyPage.studyMap[sfc.distributionDate].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.distributionDateHelp}" linkText="#{editstudybundle.distributionDateLabel}" heading="#{editstudybundle.distributionDateHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.distributionDate].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.distributionDate].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup id="groupPanel26" rendered="#{EditStudyPage.studyMap[sfc.distributionDate].rendered}">
                                        <h:inputText id="input_distributionDate" styleClass="formDateFormat" value="#{EditStudyPage.study.distributionDate}"  required="#{EditStudyPage.studyMap[sfc.distributionDate].required}">
                                            <f:validator validatorId="DateValidator"/>    
                                        </h:inputText>
                                        <h:outputText value=" (Enter date as YYYY or YYYY-MM or YYYY-MM-DD)"/>
                                        <h:message styleClass="errorMessage" for="input_distributionDate"/>                                
                                    </ui:panelGroup>
                                    
                                    
                                    <ui:panelGroup id="groupPanel50" rendered="#{EditStudyPage.studyMap[sfc.depositor].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.depositorHelp}" linkText="#{editstudybundle.depositorLabel}" heading="#{editstudybundle.depositorHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.depositor].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.depositor].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup id="groupPanel27" rendered="#{EditStudyPage.studyMap[sfc.depositor].rendered}">
                                        <h:inputText id="input_depositor" size="45"  value="#{EditStudyPage.study.depositor}" required="#{EditStudyPage.studyMap[sfc.depositor].required}"/>
                                        <h:message styleClass="errorMessage" for="input_depositor"/>                                                                                
                                    </ui:panelGroup>
                                    
                                    
                                    <ui:panelGroup id="groupPanel51" rendered="#{EditStudyPage.studyMap[sfc.dateOfDeposit].rendered}">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.depositDateHelp}" linkText="#{editstudybundle.depositDateLabel}" heading="#{editstudybundle.depositDateHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.dateOfDeposit].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.dateOfDeposit].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup id="groupPanel28" rendered="#{EditStudyPage.studyMap[sfc.dateOfDeposit].rendered}">
                                        <h:inputText id="input_dateOfDeposit" styleClass="formDateFormat" value="#{EditStudyPage.study.dateOfDeposit}"  required="#{EditStudyPage.studyMap[sfc.dateOfDeposit].required}">
                                            <f:validator validatorId="DateValidator"/>
                                        </h:inputText>
                                        <h:outputText value=" (Enter date as YYYY or YYYY-MM or YYYY-MM-DD)"/>
                                        <h:message styleClass="errorMessage" for="input_dateOfDeposit"/>                                                                                
                                    </ui:panelGroup>                                        
                                    
                                    
                                    <ui:panelGroup id="groupPanel52" rendered="#{EditStudyPage.studyMap[sfc.seriesName].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.seriesHelp}" linkText="#{editstudybundle.seriesLabel}" heading="#{editstudybundle.seriesHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.seriesName].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.seriesName].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup id="groupPanel29" rendered="#{EditStudyPage.studyMap[sfc.seriesName].rendered}">
                                        <h:inputText immediate="true" binding="#{EditStudyPage.inputSeries}" id="input_seriesName" 
                                                     value="#{EditStudyPage.study.seriesName}"  
                                                     required="#{EditStudyPage.studyMap[sfc.seriesName].required}" 
                                                     rendered="#{EditStudyPage.studyMap[sfc.seriesName].rendered}"
                                                     
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_seriesName"/>                                                                               
                                        <ui:panelGroup styleClass="vdcEditStudyGroupField" rendered="#{EditStudyPage.studyMap[sfc.seriesInformation].rendered}">
                                            <ihelp:inlinehelp helpMessage="#{editstudybundle.seriesInformationHelp}" linkText="#{editstudybundle.seriesInformationLabel}" heading="#{editstudybundle.seriesInformationHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                            <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.seriesInformation].required}"/>
                                            <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.seriesInformation].recommended}"/>
                                            <h:inputText immediate="true" binding ="#{EditStudyPage.inputSeriesInformation}" id="input_seriesInformation" 
                                                         size="45"  
                                                         value="#{EditStudyPage.study.seriesInformation}"  
                                                         required="#{EditStudyPage.studyMap[sfc.seriesInformation].required}"
                                                         validator="#{EditStudyPage.validateSeries}"
                                                         styleClass="formHtmlEnabled">
                                                <f:validator validatorId="XhtmlValidator"/>
                                            </h:inputText>
                                            <h:message styleClass="errorMessage" for="input_seriesInformation"/>                                
                                        </ui:panelGroup >
                                    </ui:panelGroup>
                                    
                                    
                                    <ui:panelGroup id="groupPanel53"  rendered="#{EditStudyPage.studyMap[sfc.studyVersion].rendered}" styleClass="vdcEditStudyField" block="true" >
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.seriesVersionHelp}" linkText="#{editstudybundle.seriesVersionLabel}" heading="#{editstudybundle.seriesVersionHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.studyVersion].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.studyVersion].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup id="groupPanel30"  rendered="#{EditStudyPage.studyMap[sfc.studyVersion].rendered}">
                                        <h:inputText immediate="true" binding="#{EditStudyPage.inputVersion}" id="input_studyVersion"  value="#{EditStudyPage.study.studyVersion}"  required="#{EditStudyPage.studyMap[sfc.studyVersion].required}"/>
                                        <h:message styleClass="errorMessage" for="input_studyVersion"/>                                
                                        <ui:panelGroup styleClass="vdcEditStudyGroupField">
                                            <ihelp:inlinehelp helpMessage="#{editstudybundle.seriesVersionDateHelp}" linkText="#{editstudybundle.seriesVersionDateLabel}" heading="#{editstudybundle.seriesVersionDateHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                            <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.versionDate].required}"/>
                                            <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.versionDate].recommended}"/>
                                            <h:inputText immediate="true" binding="#{EditStudyPage.inputVersionDate}" validator="#{EditStudyPage.validateVersion}" id="input_versionDate" styleClass="formDateFormat" value="#{EditStudyPage.study.versionDate}" required="#{EditStudyPage.studyMap[sfc.versionDate].required}" >
                                                <f:validator validatorId="DateValidator"/>
                                            </h:inputText>
                                        </ui:panelGroup>
                                        <h:outputText value=" (Enter date as YYYY or YYYY-MM or YYYY-MM-DD)"/>
                                        <h:message styleClass="errorMessage" for="input_versionDate"/>                                
                                    </ui:panelGroup>
                                    
                                    <ui:panelGroup  rendered="#{EditStudyPage.studyMap[sfc.replicationFor].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.replicationForHelp}" linkText="#{editstudybundle.replicationForLabel}" heading="#{editstudybundle.replicationForHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.replicationFor].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.replicationFor].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup  rendered="#{EditStudyPage.studyMap[sfc.replicationFor].rendered}">
                                        
                                        <h:inputTextarea cols="90" rows="4"  
                                                         id="input_replicationFor" 
                                                         value="#{EditStudyPage.study.replicationFor}" 
                                                         required="#{EditStudyPage.studyMap[sfc.replicationFor].required}"
                                                         styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputTextarea>
                                        <h:message styleClass="errorMessage" for="input_replicationFor"/>
                                        <f:verbatim><br /></f:verbatim>
                                    </ui:panelGroup>
                                </h:panelGrid>        
                                
                                
                                
                                
                                
                                <ui:panelGroup block="true" id="groupPanel12" styleClass="vdcStudyInfoHeader">
                                    <h:outputText id="outputText65" value="Abstract and Scope"/>
                                    
                                </ui:panelGroup>
                                
                                <!-- this is a dummy datatable wrapper used to force data retention on validation error-->
                                <h:dataTable cellpadding="0" cellspacing="0" width="100%" value="dummy_datatable" >
                                    <h:column>                                
                                        <h:dataTable cellpadding="0" cellspacing="0"
                                                     columnClasses="vdcEditStudyCol1b, vdcEditStudyCol2, vdcEditStudyCol3"
                                                     binding="#{EditStudyPage.dataTableAbstracts}" 
                                                     value="#{EditStudyPage.study.studyAbstracts}" var="currentRow" width="100%"
                                                     rendered="#{EditStudyPage.studyMap[sfc.abstractText].rendered}">
                                            
                                            <h:column>
                                                <ui:panelGroup block="true" styleClass="vdcEditStudyField"> 
                                                    <ihelp:inlinehelp helpMessage="#{editstudybundle.abstractHelp}" linkText="#{editstudybundle.abstractLabel}" heading="#{editstudybundle.abstractHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                    <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.abstractText].required}"/>
                                                    <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.abstractText].recommended}"/>
                                                </ui:panelGroup>
                                            </h:column>
                                            <h:column>
                                                <ui:panelGroup block="true">
                                                    <h:inputTextarea cols="90" rows="12" 
                                                                     id="input_abstractText" 
                                                                     binding="#{EditStudyPage.inputAbstractText}"
                                                                     value="#{currentRow.text}" 
                                                                     required="#{EditStudyPage.studyMap[sfc.abstractText].required}"
                                                                     styleClass="formHtmlEnabled">
                                                        <f:validator validatorId="XhtmlValidator"/>
                                                    </h:inputTextarea>
                                                    <h:message styleClass="errorMessage" for="input_abstractText"/>                                
                                                    <ui:panelGroup styleClass="vdcEditStudyGroupField">
                                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.abstractDateHelp}" linkText="#{editstudybundle.abstractDateLabel}" heading="#{editstudybundle.abstractDateHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.abstractDate].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.abstractDate].recommended}"/>
                                                        <h:inputText binding="#{EditStudyPage.inputAbstractDate}" validator="#{EditStudyPage.validateStudyAbstract}" id="input_abstractDate" styleClass="formDateFormat" value="#{currentRow.date}" required="#{EditStudyPage.studyMap[sfc.abstractDate].required}">
                                                            <f:validator validatorId="DateValidator"/>    
                                                        </h:inputText>
                                                        <h:outputText value=" (Enter date as YYYY or YYYY-MM or YYYY-MM-DD)"/>
                                                    </ui:panelGroup>
                                                    <h:message styleClass="errorMessage" for="input_abstractDate"/>  
                                                </ui:panelGroup>
                                            </h:column>
                                            <h:column>
                                                <h:commandButton   image="/resources/icon_add.gif" actionListener="#{EditStudyPage.addRow}"/> 
                                                <h:commandButton   image="/resources/icon_remove.gif" actionListener="#{EditStudyPage.removeRow}" /> 
                                            </h:column>
                                        </h:dataTable>
                                    </h:column>
                                </h:dataTable>    
                                <!-- this is a dummy datatable wrapper used to force data retention on validation error-->
                                <h:dataTable cellpadding="0" cellspacing="0" width="100%" value="dummy_datatable" >
                                    <h:column>                                
                                        <h:dataTable cellpadding="0" cellspacing="0"
                                                     columnClasses="vdcEditStudyCol1b, vdcEditStudyCol2, vdcEditStudyCol3"
                                                     binding="#{EditStudyPage.dataTableKeywords}" 
                                                     value="#{EditStudyPage.study.studyKeywords}" var="currentRow" width="100%" 
                                                     rendered="#{EditStudyPage.studyMap[sfc.keywordValue].rendered}">
                                            
                                            <h:column>
                                                <ui:panelGroup block="true" styleClass="vdcEditStudyField"> 
                                                    <ihelp:inlinehelp helpMessage="#{editstudybundle.keywordHelp}" linkText="#{editstudybundle.keywordLabel}" heading="#{editstudybundle.keywordHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                    <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.keywordValue].required}"/>
                                                    <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.keywordValue].recommended}"/>
                                                </ui:panelGroup>
                                            </h:column>
                                            <h:column>
                                                <ui:panelGroup block="true">
                                                    <h:inputText binding="#{EditStudyPage.inputKeywordValue}" id="input_keywordValue" value="#{currentRow.value}" required="#{EditStudyPage.studyMap[sfc.keywordValue].required}"/>
                                                    <h:message styleClass="errorMessage" for="input_keywordValue"/>                                
                                                    <ui:panelGroup styleClass="vdcEditStudyGroupField">
                                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.keywordVocabularyHelp}" linkText="#{editstudybundle.keywordVocabularyLabel}" heading="#{editstudybundle.keywordVocabularyHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.keywordVocab].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.keywordVocab].recommended}"/>
                                                        <h:inputText validator ="#{EditStudyPage.validateStudyKeyword}" binding="#{EditStudyPage.inputKeywordVocab}" id="input_keywordVocab" value="#{currentRow.vocab}" required="#{EditStudyPage.studyMap[sfc.keywordVocab].required}"/>
                                                    </ui:panelGroup>
                                                    <h:message styleClass="errorMessage" for="input_keywordVocab"/>                                
                                                    <ui:panelGroup styleClass="vdcEditStudyGroupField">
                                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.keywordVocabularyURLHelp}" linkText="#{editstudybundle.keywordVocabularyURLLabel}" heading="#{editstudybundle.keywordVocabularyURLHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.keywordVocabURI].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.keywordVocabURI].recommended}"/>
                                                        <h:inputText id="input_keywordVocabURI" 
                                                                     validator ="#{EditStudyPage.validateStudyKeyword}"
                                                                     binding="#{EditStudyPage.inputKeywordVocabUri}"
                                                                     size="45" 
                                                                     value="#{currentRow.vocabURI}" 
                                                                     required="#{EditStudyPage.studyMap[sfc.keywordVocabURI].required}"
                                                        >
                                                            <f:validator validatorId="UrlValidator"/>
                                                        </h:inputText>  
                                                    </ui:panelGroup>
                                                    <h:message styleClass="errorMessage" for="input_keywordVocabURI"/>   
                                                </ui:panelGroup>
                                            </h:column>
                                            <h:column>
                                                <h:commandButton   image="/resources/icon_add.gif" actionListener="#{EditStudyPage.addRow}"/> 
                                                <h:commandButton   image="/resources/icon_remove.gif" actionListener="#{EditStudyPage.removeRow}" /> 
                                            </h:column>
                                        </h:dataTable>
                                    </h:column>
                                </h:dataTable>                                
                                <!-- this is a dummy datatable wrapper used to force data retention on validation error-->
                                <h:dataTable cellpadding="0" cellspacing="0" width="100%" value="dummy_datatable" >
                                    <h:column>                                
                                        <h:dataTable cellpadding="0" cellspacing="0"
                                                     columnClasses="vdcEditStudyCol1b, vdcEditStudyCol2, vdcEditStudyCol3"
                                                     id="dataTableTopicClass"
                                                     binding="#{EditStudyPage.dataTableTopicClass}" 
                                                     value="#{EditStudyPage.study.studyTopicClasses}" var="currentRow" width="100%"
                                                     rendered="#{EditStudyPage.studyMap[sfc.topicClassValue].rendered}">
                                            
                                            <h:column>
                                                <ui:panelGroup block="true" styleClass="vdcEditStudyField"> 
                                                    <ihelp:inlinehelp helpMessage="#{editstudybundle.topicClassificationHelp}" linkText="#{editstudybundle.topicClassificationLabel}" heading="#{editstudybundle.topicClassificationHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                    <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.topicClassValue].required}"/>
                                                    <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.topicClassValue].recommended}"/>
                                                </ui:panelGroup>
                                            </h:column>
                                            <h:column>
                                                <ui:panelGroup block="true">
                                                    <h:inputText binding="#{EditStudyPage.inputTopicClassValue}" id="input_topicClassValue" value="#{currentRow.value}" required="#{EditStudyPage.studyMap[sfc.topicClassValue].required}"/>
                                                    <h:message styleClass="errorMessage" for="input_topicClassValue"/>                                
                                                    <ui:panelGroup styleClass="vdcEditStudyGroupField">
                                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.topicClassVocabularyHelp}" linkText="#{editstudybundle.topicClassVocabularyLabel}" heading="#{editstudybundle.topicClassVocabularyHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.topicClassVocab].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.topicClassVocab].recommended}"/>
                                                        <h:inputText validator ="#{EditStudyPage.validateStudyTopicClass}" binding="#{EditStudyPage.inputTopicClassVocab}" id="input_topicClassVocab" value="#{currentRow.vocab}" required="#{EditStudyPage.studyMap[sfc.topicClassVocab].required}"/>
                                                    </ui:panelGroup>
                                                    <h:message styleClass="errorMessage" for="input_topicClassVocab"/>                                
                                                    <ui:panelGroup styleClass="vdcEditStudyGroupField">
                                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.topicClassVocabularyURLHelp}" linkText="#{editstudybundle.topicClassVocabularyURLLabel}" heading="#{editstudybundle.topicClassVocabularyURLHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.topicClassVocabURI].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.topicClassVocabURI].recommended}"/>
                                                        <h:inputText id="input_topicClassVocabURI" 
                                                                     binding="#{EditStudyPage.inputTopicClassVocabUri}"
                                                                     validator ="#{EditStudyPage.validateStudyTopicClass}"
                                                                     size="45" 
                                                                     value="#{currentRow.vocabURI}" 
                                                                     required="#{EditStudyPage.studyMap[sfc.topicClassVocabURI].required}"
                                                        >
                                                            <f:validator validatorId="UrlValidator"/>
                                                        </h:inputText>
                                                    </ui:panelGroup>
                                                    <h:message styleClass="errorMessage" for="input_topicClassVocabURI"/> 
                                                </ui:panelGroup>
                                            </h:column>
                                            <h:column>
                                                <h:commandButton   image="/resources/icon_add.gif" actionListener="#{EditStudyPage.addRow}"/> 
                                                <h:commandButton   image="/resources/icon_remove.gif" actionListener="#{EditStudyPage.removeRow}" /> 
                                            </h:column>
                                        </h:dataTable>
                                    </h:column>
                                </h:dataTable>                                
                                <!-- this is a dummy datatable wrapper used to force data retention on validation error-->
                                <h:dataTable cellpadding="0" cellspacing="0" width="100%" value="dummy_datatable" >
                                    <h:column>                                          
                                        <h:dataTable cellpadding="0" cellspacing="0"
                                                     columnClasses="vdcEditStudyCol1b, vdcEditStudyCol2, vdcEditStudyCol3"
                                                     binding="#{EditStudyPage.dataTableRelPublications}" 
                                                     value="#{EditStudyPage.study.studyRelPublications}" var="currentRow" width="100%"  
                                                     rendered="#{EditStudyPage.studyMap[sfc.relatedPublications].rendered}">
                                            
                                            <h:column>
                                                <ui:panelGroup block="true" styleClass="vdcEditStudyField"> 
                                                    <ihelp:inlinehelp helpMessage="#{editstudybundle.relatedPublicationsHelp}" linkText="#{editstudybundle.relatedPublicationsLabel}" heading="#{editstudybundle.relatedPublicationsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                    <h:graphicImage  value="/resources/icon_required.gif" rendered="#{EditStudyPage.studyMap[sfc.relatedPublications].required}"/>
                                                    <h:graphicImage  value="/resources/icon_recommended.gif" rendered="#{EditStudyPage.studyMap[sfc.relatedPublications].recommended}"/>
                                                </ui:panelGroup>
                                            </h:column>
                                            
                                            <h:column>
                                                <ui:panelGroup block="true" >
                                                    
                                                    <h:inputTextarea cols="90" rows="4"  
                                                                     id="input_relatedPublications" 
                                                                     binding="#{EditStudyPage.inputRelPublicationName}" 
                                                                     value="#{currentRow.text}" 
                                                                     required="#{EditStudyPage.studyMap[sfc.relatedPublications].required}" 
                                                                     styleClass="formHtmlEnabled">
                                                        <f:validator validatorId="XhtmlValidator"/>
                                                    </h:inputTextarea>
                                                    <h:message styleClass="errorMessage" for="input_relatedPublications"/>
                                                    <f:verbatim><br /></f:verbatim>
                                                </ui:panelGroup>
                                            </h:column>
                                            
                                            <h:column>
                                                <h:commandButton   image="/resources/icon_add.gif" actionListener="#{EditStudyPage.addRow}"/> 
                                                <h:commandButton   image="/resources/icon_remove.gif" actionListener="#{EditStudyPage.removeRow}" /> 
                                            </h:column>               
                                        </h:dataTable>
                                    </h:column>
                                </h:dataTable>
                                <!-- this is a dummy datatable wrapper used to force data retention on validation error-->
                                <h:dataTable cellpadding="0" cellspacing="0" width="100%" value="dummy_datatable" >
                                    <h:column>                                          
                                        <h:dataTable cellpadding="0" cellspacing="0"
                                                     columnClasses="vdcEditStudyCol1b, vdcEditStudyCol2, vdcEditStudyCol3"
                                                     binding="#{EditStudyPage.dataTableRelMaterials}" 
                                                     value="#{EditStudyPage.study.studyRelMaterials}" var="currentRow" width="100%"  
                                                     rendered="#{EditStudyPage.studyMap[sfc.relatedMaterial].rendered}">
                                            
                                            <h:column>
                                                <ui:panelGroup block="true" styleClass="vdcEditStudyField"> 
                                                    <ihelp:inlinehelp helpMessage="#{editstudybundle.relatedMaterialHelp}" linkText="#{editstudybundle.relatedMaterialLabel}" heading="#{editstudybundle.relatedMaterialHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                    <h:graphicImage  value="/resources/icon_required.gif" rendered="#{EditStudyPage.studyMap[sfc.relatedMaterial].required}"/>
                                                    <h:graphicImage  value="/resources/icon_recommended.gif" rendered="#{EditStudyPage.studyMap[sfc.relatedMaterial].recommended}"/>
                                                </ui:panelGroup>
                                            </h:column>
                                            
                                            <h:column>
                                                <ui:panelGroup block="true" >
                                                    <h:inputTextarea cols="90" rows="4"  
                                                                     id="input_relatedMaterial" 
                                                                     binding="#{EditStudyPage.inputRelMaterial}" 
                                                                     value="#{currentRow.text}" 
                                                                     required="#{EditStudyPage.studyMap[sfc.relatedMaterial].required}" 
                                                                     styleClass="formHtmlEnabled">
                                                        <f:validator validatorId="XhtmlValidator"/>
                                                    </h:inputTextarea>
                                                    <h:message styleClass="errorMessage" for="input_relatedMaterial"/>
                                                    <f:verbatim><br /></f:verbatim>
                                                </ui:panelGroup>
                                            </h:column>
                                            
                                            <h:column>
                                                <h:commandButton   image="/resources/icon_add.gif" actionListener="#{EditStudyPage.addRow}"/> 
                                                <h:commandButton   image="/resources/icon_remove.gif" actionListener="#{EditStudyPage.removeRow}" /> 
                                            </h:column>               
                                        </h:dataTable>
                                    </h:column>
                                </h:dataTable>
                                <!-- this is a dummy datatable wrapper used to force data retention on validation error-->
                                <h:dataTable cellpadding="0" cellspacing="0" width="100%" value="dummy_datatable" >
                                    <h:column>                                          
                                        <h:dataTable cellpadding="0" cellspacing="0"
                                                     columnClasses="vdcEditStudyCol1b, vdcEditStudyCol2, vdcEditStudyCol3"
                                                     binding="#{EditStudyPage.dataTableRelStudies}" 
                                                     value="#{EditStudyPage.study.studyRelStudies}" var="currentRow" width="100%"  
                                                     rendered="#{EditStudyPage.studyMap[sfc.relatedStudies].rendered}">
                                            
                                            <h:column>
                                                <ui:panelGroup block="true" styleClass="vdcEditStudyField"> 
                                                    <ihelp:inlinehelp helpMessage="#{editstudybundle.relatedStudiesHelp}" linkText="#{editstudybundle.relatedStudiesLabel}" heading="#{editstudybundle.relatedStudiesHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                    <h:graphicImage  value="/resources/icon_required.gif" rendered="#{EditStudyPage.studyMap[sfc.relatedStudies].required}"/>
                                                    <h:graphicImage  value="/resources/icon_recommended.gif" rendered="#{EditStudyPage.studyMap[sfc.relatedStudies].recommended}"/>
                                                </ui:panelGroup>
                                            </h:column>
                                            
                                            <h:column>
                                                <ui:panelGroup block="true" >
                                                    <h:inputTextarea cols="90" rows="4"  
                                                                     id="input_relatedStudies" 
                                                                     binding="#{EditStudyPage.inputRelStudy}" 
                                                                     value="#{currentRow.text}" 
                                                                     required="#{EditStudyPage.studyMap[sfc.relatedStudies].required}" 
                                                                     styleClass="formHtmlEnabled">
                                                        <f:validator validatorId="XhtmlValidator"/>
                                                    </h:inputTextarea>
                                                    <h:message styleClass="errorMessage" for="input_relatedStudies"/>
                                                    <f:verbatim><br /></f:verbatim>
                                                </ui:panelGroup>
                                            </h:column>
                                            
                                            <h:column>
                                                <h:commandButton   image="/resources/icon_add.gif" actionListener="#{EditStudyPage.addRow}"/> 
                                                <h:commandButton   image="/resources/icon_remove.gif" actionListener="#{EditStudyPage.removeRow}" /> 
                                            </h:column>               
                                        </h:dataTable>
                                    </h:column>
                                </h:dataTable>
                                <!-- this is a dummy datatable wrapper used to force data retention on validation error-->
                                <h:dataTable cellpadding="0" cellspacing="0" width="100%" value="dummy_datatable" >
                                    <h:column>                                          
                                        <h:dataTable cellpadding="0" cellspacing="0"
                                                     columnClasses="vdcEditStudyCol1b, vdcEditStudyCol2, vdcEditStudyCol3"
                                                     binding="#{EditStudyPage.dataTableOtherReferences}" 
                                                     value="#{EditStudyPage.study.studyOtherRefs}" var="currentRow" width="100%"  
                                                     rendered="#{EditStudyPage.studyMap[sfc.relatedMaterial].rendered}">
                                            
                                            <h:column>
                                                <ui:panelGroup block="true" styleClass="vdcEditStudyField"> 
                                                    <ihelp:inlinehelp helpMessage="#{editstudybundle.otherReferencesHelp}" linkText="#{editstudybundle.otherReferencesLabel}" heading="#{editstudybundle.otherReferencesHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                    <h:graphicImage  value="/resources/icon_required.gif" rendered="#{EditStudyPage.studyMap[sfc.otherReferences].required}"/>
                                                    <h:graphicImage  value="/resources/icon_recommended.gif" rendered="#{EditStudyPage.studyMap[sfc.otherReferences].recommended}"/>
                                                </ui:panelGroup>
                                            </h:column>
                                            
                                            <h:column>
                                                <ui:panelGroup block="true" >
                                                    <h:inputText id="input_otherReferences" 
                                                                 binding="#{EditStudyPage.inputOtherReference}"
                                                                 value="#{currentRow.text}" 
                                                                 size="90" 
                                                                 required="#{EditStudyPage.studyMap[sfc.otherReferences].required}"
                                                                 styleClass="formHtmlEnabled">
                                                        <f:validator validatorId="XhtmlValidator"/>
                                                    </h:inputText>
                                                    <h:message styleClass="errorMessage" for="input_otherReferences"/>
                                                    <f:verbatim><br /></f:verbatim>
                                                </ui:panelGroup>
                                            </h:column>
                                            
                                            <h:column>
                                                <h:commandButton   image="/resources/icon_add.gif" actionListener="#{EditStudyPage.addRow}"/> 
                                                <h:commandButton   image="/resources/icon_remove.gif" actionListener="#{EditStudyPage.removeRow}" /> 
                                            </h:column>               
                                        </h:dataTable>
                                    </h:column>
                                </h:dataTable>                            
                                
                                <h:panelGrid cellpadding="0" cellspacing="0"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2" width="100%">
                                    
                                    <ui:panelGroup rendered="#{EditStudyPage.studyMap[sfc.timePeriodCoveredStart].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.timePeriodStartHelp}" linkText="#{editstudybundle.timePeriodStartLabel}" heading="#{editstudybundle.timePeriodStartHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.timePeriodCoveredStart].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.timePeriodCoveredStart].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup rendered="#{EditStudyPage.studyMap[sfc.timePeriodCoveredStart].rendered}">
                                        <h:inputText id="input_timePeriodCoveredStart" styleClass="formDateFormat" value="#{EditStudyPage.study.timePeriodCoveredStart}" required="#{EditStudyPage.studyMap[sfc.timePeriodCoveredStart].required}">
                                            <f:validator validatorId="DateValidator"/>    
                                        </h:inputText>
                                        <h:outputText value=" (Enter date as YYYY or YYYY-MM or YYYY-MM-DD)"/>
                                        <h:message styleClass="errorMessage" for="input_timePeriodCoveredStart"/>                                
                                    </ui:panelGroup>
                                    
                                    <ui:panelGroup rendered="#{EditStudyPage.studyMap[sfc.timePeriodCoveredEnd].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.timePeriodEndHelp}" linkText="#{editstudybundle.timePeriodEndLabel}" heading="#{editstudybundle.timePeriodEndHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.timePeriodCoveredEnd].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.timePeriodCoveredEnd].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup rendered="#{EditStudyPage.studyMap[sfc.timePeriodCoveredEnd].rendered}">
                                        <h:inputText id="input_timePeriodCoveredEnd" styleClass="formDateFormat" value="#{EditStudyPage.study.timePeriodCoveredEnd}" required="#{EditStudyPage.studyMap[sfc.timePeriodCoveredEnd].required}">
                                            <f:validator validatorId="DateValidator"/>    
                                        </h:inputText>
                                        <h:outputText value=" (Enter date as YYYY or YYYY-MM or YYYY-MM-DD)"/>
                                        <h:message styleClass="errorMessage" for="input_timePeriodCoveredEnd"/>                                
                                    </ui:panelGroup>
                                    
                                    
                                    <ui:panelGroup rendered="#{EditStudyPage.studyMap[sfc.dateOfCollectionStart].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.collectionDateStartHelp}" linkText="#{editstudybundle.collectionDateStartLabel}" heading="#{editstudybundle.collectionDateStartHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.dateOfCollectionStart].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.dateOfCollectionStart].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup rendered="#{EditStudyPage.studyMap[sfc.dateOfCollectionStart].rendered}">
                                        <h:inputText id="input_dateOfCollectionStart" styleClass="formDateFormat" value="#{EditStudyPage.study.dateOfCollectionStart}"  required="#{EditStudyPage.studyMap[sfc.dateOfCollectionStart].required}">
                                            <f:validator validatorId="DateValidator"/>    
                                        </h:inputText>
                                        <h:outputText value=" (Enter date as YYYY or YYYY-MM or YYYY-MM-DD)"/>
                                        <h:message styleClass="errorMessage" for="input_dateOfCollectionStart"/>                                
                                    </ui:panelGroup>
                                    
                                    <ui:panelGroup rendered="#{EditStudyPage.studyMap[sfc.dateOfCollectionEnd].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.collectionDateEndHelp}" linkText="#{editstudybundle.collectionDateEndLabel}" heading="#{editstudybundle.collectionDateEndHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.dateOfCollectionEnd].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.dateOfCollectionEnd].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup rendered="#{EditStudyPage.studyMap[sfc.dateOfCollectionEnd].rendered}">
                                        <h:inputText id="input_dateOfCollectionEnd" styleClass="formDateFormat" value="#{EditStudyPage.study.dateOfCollectionEnd}"  required="#{EditStudyPage.studyMap[sfc.dateOfCollectionEnd].required}">
                                            <f:validator validatorId="DateValidator"/>    
                                        </h:inputText>
                                        <h:outputText value=" (Enter date as YYYY or YYYY-MM or YYYY-MM-DD)"/>
                                        <h:message styleClass="errorMessage" for="input_dateOfCollectionEnd"/>                                
                                    </ui:panelGroup>
                                    
                                    
                                    <ui:panelGroup rendered="#{EditStudyPage.studyMap[sfc.country].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.countryHelp}" linkText="#{editstudybundle.countryLabel}" heading="#{editstudybundle.countryHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.country].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.country].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup rendered="#{EditStudyPage.studyMap[sfc.country].rendered}">
                                        <h:inputText id="input_country" size="45" value="#{EditStudyPage.study.country}" required="#{EditStudyPage.studyMap[sfc.country].required}"/>
                                        <h:message styleClass="errorMessage" for="input_country"/>                                
                                    </ui:panelGroup>
                                    
                                    <ui:panelGroup rendered="#{EditStudyPage.studyMap[sfc.geographicCoverage].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.geographicCoverageHelp}" linkText="#{editstudybundle.geographicCoverageLabel}" heading="#{editstudybundle.geographicCoverageHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.geographicCoverage].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.geographicCoverage].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup rendered="#{EditStudyPage.studyMap[sfc.geographicCoverage].rendered}">
                                        <h:inputText id="input_geographicCoverage" size="45"  value="#{EditStudyPage.study.geographicCoverage}" required="#{EditStudyPage.studyMap[sfc.geographicCoverage].required}"/>
                                        <h:message styleClass="errorMessage" for="input_geographicCoverage"/>                                
                                    </ui:panelGroup>
                                    
                                    <ui:panelGroup rendered="#{EditStudyPage.studyMap[sfc.geographicUnit].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.geographicUnitHelp}" linkText="#{editstudybundle.geographicUnitLabel}" heading="#{editstudybundle.geographicUnitHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.geographicUnit].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.geographicUnit].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup rendered="#{EditStudyPage.studyMap[sfc.geographicUnit].rendered}">
                                        <h:inputText id="input_geographicUnit" size="45"  value="#{EditStudyPage.study.geographicUnit}" required="#{EditStudyPage.studyMap[sfc.geographicUnit].required}"/>
                                        <h:message styleClass="errorMessage" for="input_geographicUnit"/>                                
                                    </ui:panelGroup>
                                    
                                    <ui:panelGroup block="true" styleClass="vdcEditStudyField"> 
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.geographicBoundingBoxHelp}" linkText="#{editstudybundle.geographicBoundingBoxLabel}" heading="#{editstudybundle.geographicBoundingBoxHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.westLongitude].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.westLongitude].recommended}"/>
                                    </ui:panelGroup>
                                    
                                    <ui:panelGroup block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.westLongitudeHelp}" linkText="#{editstudybundle.westLongitudeLabel}" heading="#{editstudybundle.westLongitudeHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:inputText id="input_westLongitude"  binding="#{EditStudyPage.inputWestLongitude}" value="#{EditStudyPage.study.studyGeoBoundings[0].westLongitude}"  validator ="#{EditStudyPage.validateLongitude}" required="#{EditStudyPage.studyMap[sfc.westLongitude].required}">
                                            
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_westLongitude"/>        
                                        
                                        <ui:panelGroup styleClass="vdcEditStudyGroupField">                                                   
                                            <ihelp:inlinehelp helpMessage="#{editstudybundle.eastLongitudeHelp}" linkText="#{editstudybundle.eastLongitudeLabel}" heading="#{editstudybundle.eastLongitudeHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                            <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.eastLongitude].required}"/>
                                            <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.eastLongitude].recommended}"/>
                                            <h:inputText binding="#{EditStudyPage.inputEastLongitude}" id="input_eastLongitude" value="#{EditStudyPage.study.studyGeoBoundings[0].eastLongitude}" validator ="#{EditStudyPage.validateLongitude}" required="#{EditStudyPage.studyMap[sfc.eastLongitude].required}">
                                                
                                            </h:inputText>
                                        </ui:panelGroup>
                                        <h:message styleClass="errorMessage" for="input_eastLongitude"/>        
                                        
                                        <ui:panelGroup styleClass="vdcEditStudyGroupField">                                                   
                                            <ihelp:inlinehelp helpMessage="#{editstudybundle.northLatitudeHelp}" linkText="#{editstudybundle.northLatitudeLabel}" heading="#{editstudybundle.northLatitudeHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                            <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.northLatitude].required}"/>
                                            <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.northLatitude].recommended}"/>
                                            <h:inputText binding="#{EditStudyPage.inputNorthLatitude}"  id="input_northLatitude" value="#{EditStudyPage.study.studyGeoBoundings[0].northLatitude}" validator ="#{EditStudyPage.validateLatitude}" required="#{EditStudyPage.studyMap[sfc.northLatitude].required}"/>
                                        </ui:panelGroup>
                                        <h:message styleClass="errorMessage" for="input_northLatitude"/>      
                                        
                                        <ui:panelGroup styleClass="vdcEditStudyGroupField">                                                   
                                            <ihelp:inlinehelp helpMessage="#{editstudybundle.southLatitudeHelp}" linkText="#{editstudybundle.southLatitudeLabel}" heading="#{editstudybundle.southLatitudeHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                            <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.southLatitude].required}"/>
                                            <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.southLatitude].recommended}"/>
                                            <h:inputText binding="#{EditStudyPage.inputSouthLatitude}" id="input_southLatitude" value="#{EditStudyPage.study.studyGeoBoundings[0].southLatitude}" validator ="#{EditStudyPage.validateLatitude}" required="#{EditStudyPage.studyMap[sfc.southLatitude].required}"/>
                                        </ui:panelGroup>
                                        <h:message styleClass="errorMessage" for="input_southLatitude"/>      
                                        <h:inputHidden validator="#{EditStudyPage.validateGeographicBounding}" value="test" required="true"/>
                                    </ui:panelGroup>
                                    
                                    
                                    <ui:panelGroup rendered="#{EditStudyPage.studyMap[sfc.unitOfAnalysis].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.unitOfAnalysisHelp}" linkText="#{editstudybundle.unitOfAnalysisLabel}" heading="#{editstudybundle.unitOfAnalysisHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.unitOfAnalysis].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.unitOfAnalysis].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup rendered="#{EditStudyPage.studyMap[sfc.unitOfAnalysis].rendered}">
                                        <h:inputText id="input_unitOfAnalysis" size="45"  value="#{EditStudyPage.study.unitOfAnalysis}" required="#{EditStudyPage.studyMap[sfc.unitOfAnalysis].required}"/>
                                        <h:message styleClass="errorMessage" for="input_unitOfAnalysis"/>          
                                        <h:inputHidden validator="#{EditStudyPage.validateGeographicBounding}" value="test" required="true"/>   
                                    </ui:panelGroup>
                                    
                                    <ui:panelGroup rendered="#{EditStudyPage.studyMap[sfc.universe].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.universeHelp}" linkText="#{editstudybundle.universeLabel}" heading="#{editstudybundle.universeHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.universe].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.universe].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup rendered="#{EditStudyPage.studyMap[sfc.universe].rendered}">
                                        <h:inputText id="input_universe" size="45"  value="#{EditStudyPage.study.universe}" required="#{EditStudyPage.studyMap[sfc.universe].required}"/>
                                        <h:message styleClass="errorMessage" for="input_universe"/>                                
                                    </ui:panelGroup>
                                    
                                    
                                    <ui:panelGroup rendered="#{EditStudyPage.studyMap[sfc.kindOfData].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.kindOfDataHelp}" linkText="#{editstudybundle.kindOfDataLabel}" heading="#{editstudybundle.kindOfDataHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.kindOfData].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.kindOfData].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup rendered="#{EditStudyPage.studyMap[sfc.kindOfData].rendered}">
                                        <h:inputText id="input_kindOfData"  size="45" value="#{EditStudyPage.study.kindOfData}" required="#{EditStudyPage.studyMap[sfc.kindOfData].required}"/>
                                        <h:message styleClass="errorMessage" for="input_kindOfData"/>                                
                                    </ui:panelGroup>
                                    
                                    
                                </h:panelGrid>
                                <ui:panelGroup block="true" id="groupPanel13" styleClass="vdcStudyInfoHeader" rendered="#{EditStudyPage.studyMap[sfc.otherDataAppraisal].rendered or EditStudyPage.studyMap[sfc.samplingErrorEstimates].rendered or EditStudyPage.studyMap[sfc.responseRate].rendered or EditStudyPage.studyMap[sfc.cleaningOperations].rendered or EditStudyPage.studyMap[sfc.studyLevelErrorNotes].rendered or EditStudyPage.studyMap[sfc.controlOperations].rendered or EditStudyPage.studyMap[sfc.actionsToMinimizeLoss].rendered or EditStudyPage.studyMap[sfc.dataCollectionSituation].rendered or EditStudyPage.studyMap[sfc.accessToSources].rendered or EditStudyPage.studyMap[sfc.characteristicOfSources].rendered or EditStudyPage.studyMap[sfc.originOfSources].rendered or EditStudyPage.studyMap[sfc.dataSources].rendered or EditStudyPage.studyMap[sfc.researchInstrument].rendered or EditStudyPage.studyMap[sfc.collectionMode].rendered or EditStudyPage.studyMap[sfc.deviationsFromSampleDesign].rendered or EditStudyPage.studyMap[sfc.samplingProcedure].rendered or EditStudyPage.studyMap[sfc.frequencyOfDataCollection].rendered or EditStudyPage.studyMap[sfc.dataCollector].rendered or EditStudyPage.studyMap[sfc.timeMethod].rendered}">
                                    <h:outputText id="outputText90" value="Data Collection / Methodology"/>
                                    
                                </ui:panelGroup>
                                <h:panelGrid cellpadding="0" cellspacing="0"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2" id="gridPanel4" width="100%" >
                                    
                                    <ui:panelGroup block="true" visible="#{EditStudyPage.studyMap[sfc.timeMethod].rendered}" styleClass="vdcEditStudyField" >
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.timeMethodHelp}" linkText="#{editstudybundle.timeMethodLabel}" heading="#{editstudybundle.timeMethodHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.timeMethod].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.timeMethod].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup block="true"  visible="#{EditStudyPage.studyMap[sfc.timeMethod].rendered}"  >
                                        <h:inputText id="input_timeMethod" size="90" 
                                                     value="#{EditStudyPage.study.timeMethod}" 
                                                     required="#{EditStudyPage.studyMap[sfc.timeMethod].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_timeMethod"/>                                
                                    </ui:panelGroup>
                                    
                                    <ui:panelGroup block="true" visible="#{EditStudyPage.studyMap[sfc.dataCollector].rendered}" styleClass="vdcEditStudyField">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.dataCollectorHelp}" linkText="#{editstudybundle.dataCollectorLabel}" heading="#{editstudybundle.dataCollectorHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.dataCollector].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.dataCollector].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup block="true" visible="#{EditStudyPage.studyMap[sfc.dataCollector].rendered}">
                                        <h:inputText id="input_dataCollector" size="90" 
                                                     value="#{EditStudyPage.study.dataCollector}" 
                                                     required="#{EditStudyPage.studyMap[sfc.dataCollector].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_dataCollector"/>                                
                                    </ui:panelGroup>
                                    
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.frequencyOfDataCollection].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.frequencyHelp}" linkText="#{editstudybundle.frequencyLabel}" heading="#{editstudybundle.frequencyHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.frequencyOfDataCollection].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.frequencyOfDataCollection].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.frequencyOfDataCollection].rendered}">
                                        <h:inputText id="input_frequencyOfDataCollection"  size="90"
                                                     value="#{EditStudyPage.study.frequencyOfDataCollection}" 
                                                     required="#{EditStudyPage.studyMap[sfc.frequencyOfDataCollection].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_frequencyOfDataCollection"/>
                                    </ui:panelGroup>
                                    
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.samplingProcedure].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.samplingProcedureHelp}" linkText="#{editstudybundle.samplingProcedureLabel}" heading="#{editstudybundle.samplingProcedureHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.samplingProcedure].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.samplingProcedure].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.samplingProcedure].rendered}" >
                                        <h:inputText id="input_samplingProcedure" 
                                                     size="90" 
                                                     value="#{EditStudyPage.study.samplingProcedure}" 
                                                     required="#{EditStudyPage.studyMap[sfc.samplingProcedure].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_samplingProcedure"/>
                                    </ui:panelGroup>
                                    
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.deviationsFromSampleDesign].rendered}" styleClass="vdcEditStudyField" block="true" >
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.majorDeviationsHelp}" linkText="#{editstudybundle.majorDeviationsLabel}" heading="#{editstudybundle.majorDeviationsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.deviationsFromSampleDesign].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.deviationsFromSampleDesign].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.deviationsFromSampleDesign].rendered}">
                                        <h:inputTextarea cols="90" rows="4" 
                                                         id="input_deviationsFromSampleDesign"  
                                                         value="#{EditStudyPage.study.deviationsFromSampleDesign}" 
                                                         required="#{EditStudyPage.studyMap[sfc.deviationsFromSampleDesign].required}"
                                                         styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputTextarea>
                                        <h:message styleClass="errorMessage" for="input_deviationsFromSampleDesign"/>
                                    </ui:panelGroup>
                                    
                                    
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.collectionMode].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.collectionModeHelp}" linkText="#{editstudybundle.collectionModeLabel}" heading="#{editstudybundle.collectionModeHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.collectionMode].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.collectionMode].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.collectionMode].rendered}">
                                        <h:inputText id="input_collectionMode"  size="90"
                                                     value="#{EditStudyPage.study.collectionMode}" 
                                                     required="#{EditStudyPage.studyMap[sfc.collectionMode].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_collectionMode"/>
                                    </ui:panelGroup>
                                    
                                    
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.researchInstrument].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.researchInstrumentHelp}" linkText="#{editstudybundle.researchInstrumentLabel}" heading="#{editstudybundle.researchInstrumentHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.researchInstrument].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.researchInstrument].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.researchInstrument].rendered}">
                                        <h:inputText id="input_researchInstrument" 
                                                     size="90" 
                                                     value="#{EditStudyPage.study.researchInstrument}" 
                                                     required="#{EditStudyPage.studyMap[sfc.researchInstrument].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_researchInstrument"/>
                                    </ui:panelGroup>
                                    
                                    
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.dataSources].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.dataSourcesHelp}" linkText="#{editstudybundle.dataSourcesLabel}" heading="#{editstudybundle.dataSourcesHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.dataSources].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.dataSources].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.dataSources].rendered}" >
                                        <h:inputText id="input_dataSources"  size="90"
                                                     value="#{EditStudyPage.study.dataSources}" 
                                                     required="#{EditStudyPage.studyMap[sfc.dataSources].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_dataSources"/>
                                    </ui:panelGroup>
                                    
                                    
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.originOfSources].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.originOfSourcesHelp}" linkText="#{editstudybundle.originOfSourcesLabel}" heading="#{editstudybundle.originOfSourcesHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.originOfSources].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.originOfSources].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.originOfSources].rendered}">
                                        <h:inputText id="input_originOfSources"  size="90"
                                                     value="#{EditStudyPage.study.originOfSources}" 
                                                     required="#{EditStudyPage.studyMap[sfc.originOfSources].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_originOfSources"/>
                                    </ui:panelGroup>
                                    
                                    
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.characteristicOfSources].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.sourceCharacteristicsHelp}" linkText="#{editstudybundle.sourceCharacteristicsLabel}" heading="#{editstudybundle.sourceCharacteristicsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.characteristicOfSources].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.characteristicOfSources].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.characteristicOfSources].rendered}">
                                        <h:inputText id="input_characteristicOfSources" 
                                                     size="90" 
                                                     value="#{EditStudyPage.study.characteristicOfSources}" 
                                                     required="#{EditStudyPage.studyMap[sfc.characteristicOfSources].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_characteristicOfSources"/>
                                    </ui:panelGroup>
                                    
                                    
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.accessToSources].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.sourceDocumentationHelp}" linkText="#{editstudybundle.sourceDocumentationLabel}" heading="#{editstudybundle.sourceDocumentationHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.accessToSources].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.accessToSources].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.accessToSources].rendered}">
                                        <h:inputTextarea cols="90" rows="4" 
                                                         id="input_accessToSources" 
                                                         value="#{EditStudyPage.study.accessToSources}" 
                                                         required="#{EditStudyPage.studyMap[sfc.accessToSources].required}"
                                                         styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputTextarea>
                                        <h:message styleClass="errorMessage" for="input_accessToSources"/>
                                    </ui:panelGroup>
                                    
                                    
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.dataCollectionSituation].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.dataCollectionCharacteristicsHelp}" linkText="#{editstudybundle.dataCollectionCharacteristicsLabel}" heading="#{editstudybundle.dataCollectionCharacteristicsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.dataCollectionSituation].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.dataCollectionSituation].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.dataCollectionSituation].rendered}" >
                                        <h:inputText id="input_dataCollectionSituation" 
                                                     size="90" 
                                                     value="#{EditStudyPage.study.dataCollectionSituation}" 
                                                     required="#{EditStudyPage.studyMap[sfc.dataCollectionSituation].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_dataCollectionSituation"/>
                                    </ui:panelGroup>
                                    
                                    
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.actionsToMinimizeLoss].rendered}" styleClass="vdcEditStudyField" block="true" >
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.minimizeActionsHelp}" linkText="#{editstudybundle.minimizeActionsLabel}" heading="#{editstudybundle.minimizeActionsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.actionsToMinimizeLoss].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.actionsToMinimizeLoss].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.actionsToMinimizeLoss].rendered}">
                                        <h:inputText id="input_actionsToMinimizeLoss" 
                                                     size="90" 
                                                     value="#{EditStudyPage.study.actionsToMinimizeLoss}" 
                                                     required="#{EditStudyPage.studyMap[sfc.actionsToMinimizeLoss].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_actionsToMinimizeLoss"/>
                                    </ui:panelGroup>
                                    
                                    
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.controlOperations].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.controlOperationsHelp}" linkText="#{editstudybundle.controlOperationsLabel}" heading="#{editstudybundle.controlOperationsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.controlOperations].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.controlOperations].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.controlOperations].rendered}">
                                        <h:inputText id="input_controlOperations" 
                                                     size="90" 
                                                     value="#{EditStudyPage.study.controlOperations}" 
                                                     required="#{EditStudyPage.studyMap[sfc.controlOperations].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_controlOperations"/>
                                    </ui:panelGroup >
                                    
                                    
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.weighting].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.weightingHelp}" linkText="#{editstudybundle.weightingLabel}" heading="#{editstudybundle.weightingHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.weighting].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.weighting].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.weighting].rendered}">
                                        <h:inputText id="input_weighting"  size="90"
                                                     value="#{EditStudyPage.study.weighting}" 
                                                     required="#{EditStudyPage.studyMap[sfc.weighting].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_weighting"/>
                                    </ui:panelGroup>
                                    
                                    
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.cleaningOperations].rendered}" styleClass="vdcEditStudyField" block="true" >
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.cleaningOperationsHelp}" linkText="#{editstudybundle.cleaningOperationsLabel}" heading="#{editstudybundle.cleaningOperationsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.cleaningOperations].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.cleaningOperations].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.cleaningOperations].rendered}">
                                        <h:inputText id="input_cleaningOperations"  size="90"
                                                     value="#{EditStudyPage.study.cleaningOperations}" 
                                                     required="#{EditStudyPage.studyMap[sfc.cleaningOperations].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_cleaningOperations"/>
                                    </ui:panelGroup>
                                    
                                    
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.studyLevelErrorNotes].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.studyLevelErrorNotesHelp}" linkText="#{editstudybundle.studyLevelErrorNotesLabel}" heading="#{editstudybundle.studyLevelErrorNotesHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.studyLevelErrorNotes].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.studyLevelErrorNotes].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.studyLevelErrorNotes].rendered}">
                                        <h:inputText id="input_studyLevelErrorNotes" size="90"
                                                     value="#{EditStudyPage.study.studyLevelErrorNotes}" 
                                                     required="#{EditStudyPage.studyMap[sfc.studyLevelErrorNotes].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_studyLevelErrorNotes"/>
                                    </ui:panelGroup>
                                    
                                    
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.responseRate].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.responseRateHelp}" linkText="#{editstudybundle.responseRateLabel}" heading="#{editstudybundle.responseRateHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.responseRate].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.responseRate].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.responseRate].rendered}">
                                        <h:inputText id="input_responseRate" size="90" 
                                                     value="#{EditStudyPage.study.responseRate}" 
                                                     required="#{EditStudyPage.studyMap[sfc.responseRate].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_responseRate"/>
                                    </ui:panelGroup>
                                    
                                    
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.samplingErrorEstimates].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.samplingErrorHelp}" linkText="#{editstudybundle.samplingErrorLabel}" heading="#{editstudybundle.samplingErrorHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.samplingErrorEstimates].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.samplingErrorEstimates].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.samplingErrorEstimates].rendered}">
                                        <h:inputText id="input_samplingErrorEstimates" size="90" 
                                                     value="#{EditStudyPage.study.samplingErrorEstimate}" 
                                                     required="#{EditStudyPage.studyMap[sfc.samplingErrorEstimates].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_samplingErrorEstimates"/>
                                    </ui:panelGroup>
                                    
                                    
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.otherDataAppraisal].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.otherDataAppraisalHelp}" linkText="#{editstudybundle.otherDataAppraisalLabel}" heading="#{editstudybundle.otherDataAppraisalHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.otherDataAppraisal].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.otherDataAppraisal].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.otherDataAppraisal].rendered}">
                                        <h:inputText id="input_otherDataAppraisal" 
                                                     size="90" 
                                                     value="#{EditStudyPage.study.otherDataAppraisal}" 
                                                     required="#{EditStudyPage.studyMap[sfc.otherDataAppraisal].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_otherDataAppraisal"/>
                                    </ui:panelGroup>
                                    
                                </h:panelGrid>
                                
                                <ui:panelGroup block="true" id="groupPanel14" styleClass="vdcStudyInfoHeader" rendered="#{EditStudyPage.studyMap[sfc.disclaimer].rendered or EditStudyPage.studyMap[sfc.disclaimer].rendered or EditStudyPage.studyMap[sfc.conditions].rendered or EditStudyPage.studyMap[sfc.depositorRequirements].rendered or EditStudyPage.studyMap[sfc.citationRequirements].rendered or EditStudyPage.studyMap[sfc.contact].rendered or EditStudyPage.studyMap[sfc.restrictions].rendered or EditStudyPage.studyMap[sfc.specialPermissions].rendered or EditStudyPage.studyMap[sfc.confidentialityDeclaration].rendered or EditStudyPage.studyMap[sfc.studyCompletion].rendered or EditStudyPage.studyMap[sfc.collectionSize].rendered or EditStudyPage.studyMap[sfc.availabilityStatus].rendered or EditStudyPage.studyMap[sfc.originalArchive].rendered or EditStudyPage.studyMap[sfc.placeOfAccess].rendered}">
                                    <h:outputText id="outputText131" value="Terms of Use"/>
                                    
                                </ui:panelGroup>
                                
                                <h:panelGrid cellpadding="0" cellspacing="0"
                                             columnClasses="vdcEditStudyCol1, vdcEditStudyCol2" columns="2" id="gridPanel5" width="100%">                                           
                                    
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.placeOfAccess].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.dataAccessPlaceHelp}" linkText="#{editstudybundle.dataAccessPlaceLabel}" heading="#{editstudybundle.dataAccessPlaceHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.placeOfAccess].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.placeOfAccess].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.placeOfAccess].rendered}" >
                                        <h:inputText id="input_placeOfAccess" 
                                                     size="90" 
                                                     value="#{EditStudyPage.study.placeOfAccess}"  
                                                     required="#{EditStudyPage.studyMap[sfc.placeOfAccess].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_placeOfAccess"/>
                                    </ui:panelGroup>
                                    
                                    
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.originalArchive].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.originalArchiveHelp}" linkText="#{editstudybundle.originalArchiveLabel}" heading="#{editstudybundle.originalArchiveHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.originalArchive].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.originalArchive].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.originalArchive].rendered}" >
                                        <h:inputText id="input_originalArchive" 
                                                     size="90" 
                                                     value="#{EditStudyPage.study.originalArchive}" 
                                                     required="#{EditStudyPage.studyMap[sfc.originalArchive].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_originalArchive"/>
                                    </ui:panelGroup>
                                    
                                    
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.availabilityStatus].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.availabilityHelp}" linkText="#{editstudybundle.availabilityLabel}" heading="#{editstudybundle.availabilityHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.availabilityStatus].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.availabilityStatus].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.availabilityStatus].rendered}">
                                        <h:inputText id="input_availabilityStatus" size="90" 
                                                     value="#{EditStudyPage.study.availabilityStatus}" 
                                                     required="#{EditStudyPage.studyMap[sfc.availabilityStatus].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_availabilityStatus"/>
                                    </ui:panelGroup>
                                    
                                    
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.collectionSize].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.collectionSizeHelp}" linkText="#{editstudybundle.collectionSizeLabel}" heading="#{editstudybundle.collectionSizeHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.collectionSize].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.collectionSize].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.collectionSize].rendered}">
                                        <h:inputText id="input_collectionSize" 
                                                     size="90" 
                                                     value="#{EditStudyPage.study.collectionSize}" 
                                                     required="#{EditStudyPage.studyMap[sfc.collectionSize].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_collectionSize"/>
                                    </ui:panelGroup>
                                    
                                    
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.studyCompletion].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.studyCompletionHelp}" linkText="#{editstudybundle.studyCompletionLabel}" heading="#{editstudybundle.studyCompletionHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.studyCompletion].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.studyCompletion].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.studyCompletion].rendered}">
                                        <h:inputText size="90" id="input_studyCompletion"  
                                                     value="#{EditStudyPage.study.studyCompletion}" 
                                                     required="#{EditStudyPage.studyMap[sfc.studyCompletion].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_studyCompletion"/>
                                    </ui:panelGroup>
                                    
                                    
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.confidentialityDeclaration].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.confidentialityHelp}" linkText="#{editstudybundle.confidentialityLabel}" heading="#{editstudybundle.confidentialityHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.confidentialityDeclaration].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.confidentialityDeclaration].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.confidentialityDeclaration].rendered}">
                                        <h:inputTextarea cols="90" rows="4" 
                                                         id="input_confidentialityDeclaration" 
                                                         value="#{EditStudyPage.study.confidentialityDeclaration}" 
                                                         required="#{EditStudyPage.studyMap[sfc.confidentialityDeclaration].required}"
                                                         styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputTextarea>
                                        <h:message styleClass="errorMessage" for="input_confidentialityDeclaration"/>
                                    </ui:panelGroup>
                                    
                                    
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.specialPermissions].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.specialPermissionsHelp}" linkText="#{editstudybundle.specialPermissionsLabel}" heading="#{editstudybundle.specialPermissionsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.specialPermissions].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.specialPermissions].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.specialPermissions].rendered}">
                                        <h:inputTextarea cols="90" rows="4" id="input_specialPermissions" 
                                                         value="#{EditStudyPage.study.specialPermissions}" 
                                                         required="#{EditStudyPage.studyMap[sfc.specialPermissions].required}"
                                                         styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputTextarea>
                                        <h:message styleClass="errorMessage" for="input_specialPermissions"/>
                                    </ui:panelGroup>
                                    
                                    
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.restrictions].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.restrictionsHelp}" linkText="#{editstudybundle.restrictionsLabel}" heading="#{editstudybundle.restrictionsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.restrictions].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.restrictions].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.restrictions].rendered}">
                                        <h:inputTextarea cols="90" rows="4" 
                                                         id="input_restrictions"  
                                                         value="#{EditStudyPage.study.restrictions}" 
                                                         required="#{EditStudyPage.studyMap[sfc.restrictions].required}"
                                                         styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputTextarea>
                                        <h:message styleClass="errorMessage" for="input_restrictions"/>
                                    </ui:panelGroup>
                                    
                                    
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.contact].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.contactHelp}" linkText="#{editstudybundle.contactLabel}" heading="#{editstudybundle.contactHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.contact].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.contact].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.contact].rendered}">
                                        <h:inputText id="input_contact" 
                                                     size="90" 
                                                     value="#{EditStudyPage.study.contact}" 
                                                     required="#{EditStudyPage.studyMap[sfc.contact].required}"
                                                     styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputText>
                                        <h:message styleClass="errorMessage" for="input_contact"/>
                                    </ui:panelGroup>
                                    
                                    
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.citationRequirements].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.citationRequirementsHelp}" linkText="#{editstudybundle.citationRequirementsLabel}" heading="#{editstudybundle.citationRequirementsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.citationRequirements].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.citationRequirements].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.citationRequirements].rendered}">
                                        <h:inputTextarea cols="90" rows="4" id="input_citationRequirements" 
                                                         value="#{EditStudyPage.study.citationRequirements}" 
                                                         required="#{EditStudyPage.studyMap[sfc.citationRequirements].required}"
                                                         styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputTextarea>
                                        <h:message styleClass="errorMessage" for="input_citationRequirements"/>
                                    </ui:panelGroup>
                                    
                                    
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.depositorRequirements].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.depositorRequirementsHelp}" linkText="#{editstudybundle.depositorRequirementsLabel}" heading="#{editstudybundle.depositorRequirementsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.depositorRequirements].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.depositorRequirements].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.depositorRequirements].rendered}">
                                        <h:inputTextarea cols="90" rows="4" 
                                                         id="input_depositorRequirements" 
                                                         value="#{EditStudyPage.study.depositorRequirements}" 
                                                         required="#{EditStudyPage.studyMap[sfc.depositorRequirements].required}"
                                                         styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputTextarea>
                                        <h:message styleClass="errorMessage" for="input_depositorRequirements"/>
                                    </ui:panelGroup>
                                    
                                    
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.conditions].rendered}" styleClass="vdcEditStudyField" block="true" >
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.conditionsHelp}" linkText="#{editstudybundle.conditionsLabel}" heading="#{editstudybundle.conditionsHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.conditions].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.conditions].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.conditions].rendered}">
                                        <h:inputTextarea cols="90" rows="4" 
                                                         id="input_conditions" 
                                                         value="#{EditStudyPage.study.conditions}" 
                                                         required="#{EditStudyPage.studyMap[sfc.conditions].required}"
                                                         styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputTextarea>
                                        <h:message styleClass="errorMessage" for="input_conditions"/>
                                    </ui:panelGroup>
                                    
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.disclaimer].rendered}" styleClass="vdcEditStudyField" block="true">
                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.disclaimerHelp}" linkText="#{editstudybundle.disclaimerLabel}" heading="#{editstudybundle.disclaimerHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.disclaimer].required}"/>
                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.disclaimer].recommended}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup visible="#{EditStudyPage.studyMap[sfc.disclaimer].rendered}">
                                        <h:inputTextarea cols="90" rows="4" 
                                                         id="input_disclaimer" 
                                                         value="#{EditStudyPage.study.disclaimer}" 
                                                         required="#{EditStudyPage.studyMap[sfc.disclaimer].required}"
                                                         styleClass="formHtmlEnabled">
                                            <f:validator validatorId="XhtmlValidator"/>
                                        </h:inputTextarea>
                                        <h:message styleClass="errorMessage" for="input_disclaimer"/>
                                    </ui:panelGroup>    
                                    
                                </h:panelGrid>
                                
                                <ui:panelGroup block="true" id="groupPanel15" styleClass="vdcStudyInfoHeader" rendered="#{EditStudyPage.studyMap[sfc.notesInformationType].rendered}">
                                    <h:outputText id="outputText160" value="Other Information"/>
                                    
                                </ui:panelGroup>
                                
                                <!-- this is a dummy datatable wrapper used to force data retention on validation error-->
                                <h:dataTable cellpadding="0" cellspacing="0" width="100%" value="dummy_datatable" >
                                    <h:column>                                
                                        <h:dataTable id="dataTableNotes" cellpadding="0" cellspacing="0"
                                                     columnClasses="vdcEditStudyCol1b, vdcEditStudyCol2, vdcEditStudyCol3"
                                                     binding="#{EditStudyPage.dataTableNotes}" 
                                                     value="#{EditStudyPage.study.studyNotes}" var="currentRow" width="100%"
                                                     rendered="#{EditStudyPage.studyMap[sfc.notesInformationType].rendered}">
                                            
                                            <h:column>
                                                <ui:panelGroup block="true" visible = "#{EditStudyPage.studyMap[sfc.notesInformationType].rendered}" styleClass="vdcEditStudyField"> 
                                                    <ihelp:inlinehelp helpMessage="#{editstudybundle.notesTypeHelp}" linkText="#{editstudybundle.notesTypeLabel}" heading="#{editstudybundle.notesTypeHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                    <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.notesInformationType].required}"/>
                                                    <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.notesInformationType].recommended}"/>
                                                </ui:panelGroup>
                                            </h:column>
                                            <h:column>
                                                <ui:panelGroup block="true" visible = "#{EditStudyPage.studyMap[sfc.notesInformationType].rendered}">
                                                    <h:inputText binding="#{EditStudyPage.inputNoteType}" id="input_notesInformationType" 
                                                                 value="#{currentRow.type}" 
                                                                 required="#{EditStudyPage.studyMap[sfc.notesInformationType].required}"
                                                                 styleClass="formHtmlEnabled">
                                                        <f:validator validatorId="XhtmlValidator"/>
                                                    </h:inputText>
                                                    <h:message styleClass="errorMessage" for="input_notesInformationType"/>   
                                                    <ui:panelGroup styleClass="vdcEditStudyGroupField"  visible="#{EditStudyPage.studyMap[sfc.notesInformationSubject].rendered}">
                                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.notesSubjectHelp}" linkText="#{editstudybundle.notesSubjectLabel}" heading="#{editstudybundle.notesSubjectHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.notesInformationSubject].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.notesInformationSubject].recommended}"/>
                                                        <h:inputText id="input_notesInformationSubject" 
                                                                     binding="#{EditStudyPage.inputNoteSubject}"
                                                                     value="#{currentRow.subject}" 
                                                                     validator ="#{EditStudyPage.validateStudyNote}"
                                                                     required="#{EditStudyPage.studyMap[sfc.notesInformationSubject].required}"
                                                                     styleClass="formHtmlEnabled">
                                                            <f:validator validatorId="XhtmlValidator"/>
                                                        </h:inputText>
                                                    </ui:panelGroup>
                                                    <h:message styleClass="errorMessage" for="input_notesInformationSubject"  />
                                                    <ui:panelGroup styleClass="vdcEditStudyGroupField" visible="#{EditStudyPage.studyMap[sfc.notesText].rendered}">
                                                        <ihelp:inlinehelp helpMessage="#{editstudybundle.notesTextHelp}" linkText="#{editstudybundle.notesTextLabel}" heading="#{editstudybundle.notesTextHelpHeading}" eventType="mouseover" cssClass="vdcInlineHelpLink" rendered="true"/>
                                                        <h:graphicImage  value="/resources/icon_required.gif"  rendered="#{EditStudyPage.studyMap[sfc.notesText].required}"/>
                                                        <h:graphicImage  value="/resources/icon_recommended.gif"  rendered="#{EditStudyPage.studyMap[sfc.notesText].recommended}"/>
                                                        <h:inputText id="input_notesText" 
                                                                     size="90" 
                                                                     binding="#{EditStudyPage.inputNoteText}"
                                                                     validator ="#{EditStudyPage.validateStudyNote}"
                                                                     value="#{currentRow.text}" 
                                                                     required="#{EditStudyPage.studyMap[sfc.notesText].required}"
                                                                     styleClass="formHtmlEnabled">
                                                            <f:validator validatorId="XhtmlValidator"/>
                                                        </h:inputText>
                                                    </ui:panelGroup>
                                                    <h:message styleClass="errorMessage" for="input_notesText"/>
                                                </ui:panelGroup>
                                            </h:column> 
                                            <h:column>
                                                <ui:panelGroup block="true" visible = "#{EditStudyPage.studyMap[sfc.notesInformationType].rendered}"> 
                                                    <h:commandButton   image="/resources/icon_add.gif" actionListener="#{EditStudyPage.addRow}"/> 
                                                    <h:commandButton   image="/resources/icon_remove.gif" actionListener="#{EditStudyPage.removeRow}" /> 
                                                </ui:panelGroup>
                                            </h:column>                                       
                                        </h:dataTable>
                                    </h:column>
                                </h:dataTable>                                
                                <!-- 
                                        <ui:panelGroup block="true" id="groupPanel6" style="padding-top: 10px" styleClass="vdcTextRight">
                                            <h:commandButton id="button5" value="Save" action="#{EditStudyPage.save}"/>
                                            <h:commandButton id="button6" style="margin-left: 30px" value="Cancel"/>
                                        </ui:panelGroup>
                             
                                       -->
                            </ui:panelLayout>
                        </ui:tab>
                        
                        <!-- FILES TAB -->
                                       
                        <ui:tab  id="files" text="Study Files">
                            <ui:panelGroup  block="true" id="noFilesPanel" style="padding-top: 30px; padding-bottom: 30px; padding-left: 20px;" rendered="#{empty EditStudyPage.files}">
                                <h:outputText value="No files have been provided for this study. To add files, return to the study view (by clicking Save or Cancel) and follow the Add File(s) link."/>
                            </ui:panelGroup>                                    
                            <ui:panelLayout id="layoutPanel2" panelLayout="flow" style="width: 98%" rendered="#{!empty EditStudyPage.files}">
                                <!-- this is a dummy datatable wrapper used to force data retention on validation error-->
                                <h:dataTable id="fileDataTableWrapper" cellpadding="0" cellspacing="0" width="100%" value="dummy_datatable" >
                                    <h:column>
                                        <h:dataTable cellpadding="0" cellspacing="0"
                                                     columnClasses="vdcEditFilesColA, vdcEditFilesColA, vdcEditFilesColB, vdcEditFilesColC"
                                                     headerClass="vdcEditFilesHeader" id="fileDataTable" rowClasses="list-row-even,list-row-odd"
                                                     value="#{EditStudyPage.files}" var="file" width="100%">
                                            <h:column id="column9">
                                                <ui:panelGroup  block="true" style="white-space: nowrap;">
                                                    <!-- note that the Javascript functions currently depends on the placement and ids of these fields (and the dropdown box; 
                                                changing them may require corresponding changes in the Javascript  -->
                                                    <h:selectBooleanCheckbox id="catCheckbox"  onclick="updateCategory(this);"/>
                                                    <h:inputText id="catTextField" size="25"  value="#{file.fileCategoryName}" onfocus="deselectCategory(this)"/>
                                                </ui:panelGroup>
                                                <f:facet name="header">
                                                    <ui:panelGroup id="catColPanelGroup">
                                                        <h:outputText id="catLabel" value="Category"/>
                                                        <h:selectOneMenu  id="catDropdown" onchange="updateAllCheckedCategories(this);">
                                                            <f:selectItems value="#{EditStudyPage.templateFileCategories}" />
                                                        </h:selectOneMenu>  
                                                    </ui:panelGroup>
                                                </f:facet>    
                                            </h:column>
                                            <h:column id="column10">
                                                <h:inputText id="input_fileName" size="30"  value="#{file.studyFile.fileName}" validator ="#{EditStudyPage.validateFileName}" />
                                                <h:message styleClass="errorMessage" for="input_fileName"/><br />
                                                <h:outputText id="outputText17" style="font-size: 11px" value="#{file.studyFile.fileType} "/>
                                                <h:graphicImage  rendered="#{file.studyFile.subsettable}" id="imagefs" styleClass="vdcNoBorders" value="/resources/icon_subsettable.gif"/>
                                                <f:facet name="header">
                                                    <h:outputText id="outputText13" value="File Name"/>
                                                </f:facet>
                                            </h:column>
                                            <h:column id="column11">
                                                <h:inputTextarea cols="30" rows="2"  value="#{file.studyFile.description}"/>
                                                <f:facet name="header">
                                                    <h:outputText id="outputText16" value="Description"/>
                                                </f:facet>
                                            </h:column>
                                            <h:column id="deleteColumn">
                                                <h:selectBooleanCheckbox id="deleteCheckBox" value="#{file.deleteFlag}"/>                                                
                                                <f:facet name="header">
                                                    <h:outputText id="deleteText" value="Delete? "/>
                                                </f:facet>
                                            </h:column>     
                                                                                      
                                        </h:dataTable>
                                    </h:column>
                                </h:dataTable>
                            </ui:panelLayout>
                        </ui:tab>
                    </ui:tabSet>
                    
                    <ui:panelGroup block="true" styleClass="vdcTextRight" style="padding-top: 15px;">              
                        <h:commandButton id="button1b" value="Save" action="#{EditStudyPage.save}"/>
                        <h:commandButton immediate="true" id="button2b" style="margin-left: 30px; margin-right: 15px" value="Cancel" action="#{EditStudyPage.cancel}"/>   
                    </ui:panelGroup> 
                    
                </div>
            </div>
            
        </h:form>
    </f:subview>
</jsp:root>
