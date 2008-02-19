<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
   <f:subview id="ReviewStudiesPageView">
        <ui:form binding="#{ReviewStudiesPage.form1}" id="form1">
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>

            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                   
                        <h:outputText value="Review Studies in #{VDCRequest.currentVDC.name} Dataverse" />
                     
                </div>            
                <div class="dvn_sectionBox">
                    <div class="dvn_margin12">
                        
                        <ui:panelGroup block="true" style="padding-bottom: 10px">
                            <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                            <h:outputText  styleClass="vdcHelpText" escape="false" value="When curators or admins add a study, the study is set to 'In Review' by default. Set it to 'Released' if it's ready for users to see it (once study is released, it's not displayed in this page anymore). A study status is set to 'New' when a contributor adds a study. A curator or admin can set to 'In Review' and, when ready, set it to 'Released'. Click the study name to go to the study page and edit/delete options for that study." />
                        </ui:panelGroup>
                        
                        <h:dataTable binding="#{ReviewStudiesPage.dataTable1}" cellpadding="0" cellspacing="0"
                                     headerClass="list-header-left vdcColPadded" id="dataTable1" columnClasses="vdcReviewCol1, vdcReviewCol2, vdcReviewCol3, vdcReviewCol4, vdcReviewCol5, vdcReviewCol6"
                                     rowClasses="list-row-even vdcColPadded, list-row-odd vdcColPadded" value="#{ReviewStudiesPage.reviewStudies}" 
                                     var="currentRow" width="100%">                                          
                            <h:column binding="#{ReviewStudiesPage.column1}" id="column1">
                                <f:facet name="header">
                                    <h:outputText binding="#{ReviewStudiesPage.outputText2}" id="outputText2" value="Studies in Review"/>
                                </f:facet>
                                <h:outputLink binding="#{ReviewStudiesPage.hyperlink1}" id="hyperlink1" value="/dvn/dv/#{VDCRequest.currentVDC.alias}/faces/study/StudyPage.jsp?studyId=#{currentRow[0]}">
                                    <h:outputText binding="#{ReviewStudiesPage.hyperlink1Text1}" id="hyperlink1Text1" value="#{currentRow[1]} - #{currentRow[2]}"/>
                                </h:outputLink>
                            </h:column>
                            <h:column binding="#{ReviewStudiesPage.column2}" id="column2">
                                <h:selectOneMenu binding="#{ReviewStudiesPage.dropdown1}" value="#{currentRow[3]}" id="dropdown1">
                                    <!--
                                    <f:selectItems binding="#{ReviewStudiesPage.dropdown1SelectItems}" id="dropdown1SelectItems" value="#{ReviewStudiesPage.dropdown1DefaultItems}"/>
                                    -->
                                    <f:selectItems binding="#{ReviewStudiesPage.dropdown1SelectItems}" id="dropdown1SelectItems" value="#{currentRow[3] == 'New' ?  ReviewStudiesPage.dropdown1NewItems : ReviewStudiesPage.dropdown1InReviewItems}"/>
                                </h:selectOneMenu>
                                <f:facet name="header">
                                    <h:outputText binding="#{ReviewStudiesPage.outputText4}" id="outputText4" value="Status"/>
                                </f:facet>
                            </h:column>
                            <h:column binding="#{ReviewStudiesPage.column3}" id="column3">
                                <h:outputLink binding="#{ReviewStudiesPage.hyperlink2}" id="hyperlink2" value="/dvn#{VDCRequest.currentVDCURL}/faces/login/AccountPage.jsp?userId=#{currentRow[6]}">
                                    <h:outputText binding="#{ReviewStudiesPage.hyperlink2Text}" id="hyperlink2Text" value="#{currentRow[4]}"/>
                                </h:outputLink>
                                <f:facet name="header">
                                    <h:outputText binding="#{ReviewStudiesPage.outputText3}" id="outputText3" value="Contributor"/>
                                </f:facet>
                            </h:column>
                            <h:column binding="#{ReviewStudiesPage.column4}" id="column4">
                                <h:outputLink binding="#{ReviewStudiesPage.hyperlink3}" id="hyperlink3" value="/dvn#{VDCRequest.currentVDCURL}/faces/login/AccountPage.jsp?userId=#{currentRow[7]}">
                                    <h:outputText binding="#{ReviewStudiesPage.hyperlink2Text1}" id="hyperlink2Text1" value="#{currentRow[5]}"/>
                                </h:outputLink>
                                <f:facet name="header">
                                    <h:outputText binding="#{ReviewStudiesPage.outputText5}" id="outputText5" value="Reviewer"/>
                                </f:facet>
                            </h:column>
                            <h:column id="column5">
                                <f:facet name="header">
                                    <h:outputText  id="createdDate" value="Created Date"/>
                                </f:facet>
                                <h:outputText  id="CreatedDateText" value="#{currentRow[8]}"/>
                            </h:column>
                            <h:column id="column6">
                                <f:facet name="header">
                                    <h:outputText  id="lastModifiedDate" value="Last Modified Date"/>
                                </f:facet>
                                <h:outputText  id="LastModifiedDateText" value="#{currentRow[9]}"/>
                            </h:column>
                        </h:dataTable>
                        
                        <h:dataTable cellpadding="0" cellspacing="0"
                                     headerClass="list-header-left vdcColPadded" columnClasses="vdcReviewCol1, vdcReviewCol2, vdcReviewCol3, vdcReviewCol4, vdcReviewCol5, vdcReviewCol6"
                                     rowClasses="list-row-even vdcColPadded, list-row-odd vdcColPadded" value="#{ReviewStudiesPage.newStudies}" rendered="#{ReviewStudiesPage.newStudies.rowCount > 0 }" 
                                     var="currentRow" width="100%">
                            <h:column >
                                <f:facet name="header">
                                    <h:outputText   value="New Studies"/>
                                </f:facet>
                                <h:outputLink  value="/dvn/dv/#{VDCRequest.currentVDC.alias}/faces/study/StudyPage.jsp?studyId=#{currentRow[0]}">
                                    <h:outputText  value="#{currentRow[1]} - #{currentRow[2]}"/>
                                </h:outputLink>
                            </h:column>
                            
                            <h:column  >
                                <h:outputLink   value="/dvn#{VDCRequest.currentVDCURL}/faces/login/AccountPage.jsp?userId=#{currentRow[6]}">
                                    <h:outputText value="#{currentRow[4]}"/>
                                </h:outputLink>
                                <f:facet name="header">
                                    <h:outputText  value="Contributor"/>
                                </f:facet>
                            </h:column>
                            <h:column >
                                <h:outputLink  value="/dvn#{VDCRequest.currentVDCURL}/faces/login/AccountPage.jsp?userId=#{currentRow[7]}">
                                    <h:outputText  value="#{currentRow[5]}"/>
                                </h:outputLink>
                                <f:facet name="header">
                                    <h:outputText value="Reviewer"/>
                                </f:facet>
                            </h:column>
                            <h:column>
                                <f:facet name="header">
                                    <h:outputText  value="Created Date"/>
                                </f:facet>
                                <h:outputText   value="#{currentRow[8]}"/>
                            </h:column>
                            <h:column>
                                <f:facet name="header">
                                    <h:outputText  value="Last Modified Date"/>
                                </f:facet>
                                <h:outputText  value="#{currentRow[9]}"/>
                            </h:column>
                        </h:dataTable>
                        
                        <ui:panelGroup block="true" style="padding-top: 10px" rendered="#{ReviewStudiesPage.displayNoStudiesText}">
                            <h:outputText id="noStudiesText" value="There are currently no New studies or studies In Review." styleClass="warnMessage"  />
                        </ui:panelGroup>
                        <ui:panelGroup binding="#{ReviewStudiesPage.groupPanel1}" block="true" id="groupPanel1" style="padding-top: 10px" styleClass="vdcTextRight">
                            <h:commandButton binding="#{ReviewStudiesPage.button1}" id="button1" value="Update" actionListener="#{ReviewStudiesPage.updateStudy}"/>
                        </ui:panelGroup>
                        
                    </div>
                </div>
            </div>
        </ui:form>          
    </f:subview>
</jsp:root>
