<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
<f:subview id="MyStudiesPageView">
    <ui:form binding="#{MyStudiesPage.form1}" id="form1">
        <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
        
            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                    <h3>
                        <h:outputText value="My Studies in #{VDCRequest.currentVDC.name} Dataverse"/>
                    </h3>
                </div>            
                <div class="dvn_sectionBox dvn_pad12"> 
                    
                    <ui:panelGroup block="true" style="padding-bottom: 10px">
                        <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                        <h:outputText  styleClass="vdcHelpText" escape="false" value="Click the study name to go to the study page and edit/delete options for that study." />
                    </ui:panelGroup>
                    <h:dataTable binding="#{MyStudiesPage.dataTable1}" cellpadding="0" cellspacing="0"
                                 headerClass="list-header-left vdcColPadded" id="dataTable1" columnClasses="vdcMyStudiesCol1, vdcMyStudiesCol2, vdcMyStudiesCol3,  vdcMyStudiesCol4"
                                 rowClasses="list-row-even vdcColPadded, list-row-odd vdcColPadded" value="#{MyStudiesPage.contributorStudies}"
                                 var="currentRow" width="100%">
                        <h:column binding="#{MyStudiesPage.column1}" id="column1">
                            <f:facet name="header">
                                <h:outputText binding="#{MyStudiesPage.outputText3}" id="outputText3" value="Study ID - Study Title" />
                            </f:facet>
                            <h:outputLink binding="#{MyStudiesPage.hyperlink1}" id="hyperlink1" value="/dvn/dv/#{VDCRequest.currentVDC.alias}/faces/study/StudyPage.jsp?studyId=#{currentRow[3]}">
                                <h:outputText binding="#{MyStudiesPage.hyperlink1Text}" id="hyperlink1Text" value="#{currentRow[0]} - #{currentRow[1]}"/>
                            </h:outputLink>
                        </h:column>
                        <h:column binding="#{MyStudiesPage.column2}" id="column2">
                            <h:outputText binding="#{MyStudiesPage.outputText4}" id="outputText4" value="#{currentRow[2]}"/>
                            <f:facet name="header">
                                <h:outputText binding="#{MyStudiesPage.outputText5}" id="outputText5" value="Status"  />
                            </f:facet>
                        </h:column>
                        <h:column id="column3">
                            <f:facet name="header">
                                <h:outputText  id="createdDate" value="Created Date"/>
                            </f:facet>
                            <h:outputText  id="CreatedDateText" value="#{currentRow[4]}"/>
                        </h:column>
                        <h:column id="column4">
                            <f:facet name="header">
                                <h:outputText  id="lastModifiedDate" value="Last Modified Date"/>
                            </f:facet>
                            <h:outputText  id="LastModifiedDateText" value="#{currentRow[5]}"/>
                        </h:column>
                    </h:dataTable>
                    <ui:panelGroup block="true" style="padding-top: 10px">
                        <h:outputText binding="#{MyStudiesPage.outputText6}" id="noStudiesText" value="You have not yet uploaded any studies in this Dataverse." rendered="#{MyStudiesPage.contributorStudies.rowCount == 0 }" styleClass="warnMessage" />
                    </ui:panelGroup>
                    
                </div>
            </div>
        </ui:form>            
    </f:subview>
</jsp:root>
