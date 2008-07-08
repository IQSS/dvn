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

<gui:param name="pageTitle" value="DVN - Add Files" />

  <gui:define name="body">
      <f:loadBundle basename="Bundle" var="bundle"/>
     <f:loadBundle basename="BundleAnalysis" var="bundleAnalysis"/>


        <f:verbatim>
            <script type="text/javascript">
             // <![CDATA[                  
                // functions to work with category selection
                // note a few assumptions that these functions have:
                // the id of the dropdown (including parent nodes); the partial id of the checkboxes;
                // and that the checkbox is two elements before the text field (there is empty text
                // in between, created by the div)
                function updateCategory( checkbox ) {
                if (checkbox.checked) {
                textField = document.getElementById(checkbox.id).nextSibling.nextSibling;
                dropdown = document.getElementById("form1:fileDataTableWrapper:0:fileDataTable:catDropdown");
                textField.value = dropdown.value;
                }
                }
                function deselectCategory( textField ) {
                checkbox = document.getElementById(textField.id).previousSibling.previousSibling;
                checkbox.checked = false;
                }
                function updateAllCheckedCategories( dropdown ) {
                checkboxes=document.getElementsByTagName("input");
                for(i=0; i < checkboxes.length; i++) {
                if (checkboxes[i].id.indexOf("catCheckbox") != -1 && checkboxes[i].checked == true) {
                textField = checkboxes[i].nextSibling.nextSibling;
                textField.value = dropdown.value;                            
                }
                }
                }
                
                function disableSave() {
                document.getElementById("form1:saveButton").disabled = true;
                }
              // ]]>
            </script>
        </f:verbatim>
        <ui:form  id="form1">
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
            <h:inputHidden id="studyId" value="#{AddFilesPage.study.id}"/>
            <input type="hidden"  name="pageName" value="AddFilesPage"/>
           
            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                     
                        <h:outputText value="Upload Files"/>
                     
                </div>            
                <div class="dvn_sectionBox"> 
                    <div class="dvn_margin12">
                        
                        <!-- validation errors occurred? -->
                        <ui:panelGroup id="messagePanel" styleClass="errorMessage" rendered="#{! empty facesContext.maximumSeverity}">
                            <h:outputText value="Some errors occurred, please check details below" rendered="#{! empty facesContext.maximumSeverity}" />
                        </ui:panelGroup>     
                        <ui:panelGroup  block="true" id="groupPanel1" style="padding-bottom: 15px">
                            <h:outputText  id="outputText1" value="Adding Files to: "/>
                            <h:outputText  id="outputText2b" styleClass="vdcSubHeaderColor" value="#{AddFilesPage.study.title}"/>
                        </ui:panelGroup>
                        <ui:panelGroup block="true" style="padding-bottom: 5px;">
                            <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                            <h:outputText styleClass="vdcHelpText" value="You may upload multiple files to this page (by browsing each file individually) and then save all of them at once. If you upload a STATA (.dta) or SPSS (.sav or .por) file, it will be processed as a subsettable data file."/>
                        </ui:panelGroup>
                        <ui:panelGroup  block="true" id="groupPanel2" style="padding-bottom: 5px;">
                            <ui:upload  id="fileBrowser" onChange="disableSave();submit();" valueChangeListener="#{AddFilesPage.fileBrowser_add}"/>
                        </ui:panelGroup>
                        
                        <!-- Comment Save and Cancel buttons on top (per Isabelle's request)
                        <ui:panelGroup  block="true" id="groupPanel3"
                        style="padding-top: 20px; padding-bottom: 10px" styleClass="vdcTextRight">
                        <h:commandButton  id="button2" value="Save" action="#{AddFilesPage.save_action}"/>
                        <h:commandButton  immediate="true" id="button4" value="Cancel" action="#{AddFilesPage.cancel_action}" style="margin-left: 30px;"/>
                        </ui:panelGroup>
                        -->
                        <!-- this is a dummy datatable wrapper used to force data retention on validation error-->
                        <h:dataTable id="fileDataTableWrapper" cellpadding="0" cellspacing="0" width="98%" value="dummy_datatable">
                            <h:column>                              
                                <h:dataTable  cellpadding="0" cellspacing="0" binding="#{AddFilesPage.filesDataTable}"
                                              columnClasses="vdcEditFilesColA, vdcEditFilesColA, vdcEditFilesColB, vdcEditFilesColC"
                                              headerClass="vdcEditFilesHeader" id="fileDataTable" rowClasses="list-row-even,list-row-odd"
                                              value="#{AddFilesPage.files}" var="file" width="98%">
                                    <h:column id="catCol">
                                        <ui:panelGroup block="true" style="white-space: nowrap;">   
                                            <!-- note that the Javascript functions currently depends on the placement and ids of these fields (and the dropdown box; 
                                            changing them may require corresponding changes in the Javascript  -->
                                            <h:selectBooleanCheckbox id="catCheckbox"  onclick="updateCategory(this);"/>
                                            <h:inputText id="catTextField" size="30" value="#{file.fileCategoryName}" onfocus="deselectCategory(this)"/>
                                        </ui:panelGroup>
                                        <f:facet name="header">
                                            <ui:panelGroup id="catColPanelGroup">
                                                <h:outputText id="catLabel" value="Category"/>
                                                <h:selectOneMenu id="catDropdown" onchange="updateAllCheckedCategories(this);">
                                                    <f:selectItems value="#{AddFilesPage.templateFileCategories}" />
                                                </h:selectOneMenu>  
                                            </ui:panelGroup>
                                        </f:facet>   
                                    </h:column>
                                    <h:column id="column3">
                                        <h:inputText id="input_fileName" size="30" value="#{file.studyFile.fileName}" required="true" requiredMessage="This field is required." validator ="#{AddFilesPage.validateFileName}"/>
                                        <h:message styleClass="errorMessage" for="input_fileName"/>
                                        <h:message styleClass="errorMessage" for="fileBrowser" rendered="#{AddFilesPage.newFileAdded and AddFilesPage.filesDataTable.rowCount == AddFilesPage.filesDataTable.rowIndex +1}" />
                                        <f:facet name="header">
                                            <h:outputText id="input_fileNameHeader2" value="File Name"/>
                                        </f:facet>
                                    </h:column>
                                    <h:column id="column4">
                                        <h:inputTextarea id="textField3" style="width:90%;" rows="2" value="#{file.studyFile.description}"/>
                                        <f:facet name="header">
                                            <h:outputText id="outputText9" value="Description"/>
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
                        <ui:panelGroup  block="true" rendered="#{AddFilesPage.emailRequested}"
                                        style="padding-top: 20px; padding-bottom: 10px" >
                            <h:outputText  styleClass="vdcTextStandOut" value="NOTE: "/>     
                            <h:outputText id="email" escape="false" styleClass="warnMessage" value="SPSS and STATA files will be converted to a &lt;b&gt; tab delimited &lt;/b&gt; format and become &lt;b&gt; subsettable &lt;/b&gt; . You will still be able to download the original file, as well as other formats.
               Subsettable files might take long time to upload and be processed. We will send you an e-mail notification when the upload process starts and when it completes."/> 
                          <f:verbatim><br /></f:verbatim>
                            <h:outputText value="E-Mail Address:"/>
                            <h:inputText  id="textField3" size="50" value="#{AddFilesPage.ingestEmail}">
                                <f:validator validatorId="EmailValidator"/>
                            </h:inputText>
                            <h:message for="textField3" id="validatorMessage" styleClass="errorMessage"/>
                            
                            <ui:panelGroup>
                                <f:verbatim><br /></f:verbatim>
                                <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                                <h:outputText id="outputText4" styleClass="vdcHelpText" value="Separate multiple email addresses with a comma (and no spaces). Example: admin@mydvn.edu,user@mydvn.edu"/>
                                <f:verbatim><br /></f:verbatim>
                            </ui:panelGroup>
                            
                        </ui:panelGroup>                                
                        <ui:panelGroup  block="true" id="groupPanel5"
                                        style="padding-top: 20px; " styleClass="vdcTextRight">
                            <h:commandButton  id="saveButton" value="Save" action="#{AddFilesPage.save_action}" onclick="this.style.display='none';this.nextSibling.nextSibling.style.display='';"/>
                            <h:commandButton  id="saveButtonDisabled" value="Save" disabled="true" style="display:none;" />
                            <h:commandButton  immediate="true" id="button8" value="Cancel" action="#{AddFilesPage.cancel_action}" style="margin-left: 30px;" />
                        </ui:panelGroup>
                        
                    </div>
                </div>
            </div>
        </ui:form>
            </gui:define>
        </gui:composition>
    </body>
</html>
