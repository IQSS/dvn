<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
    xmlns:h="http://java.sun.com/jsf/html" 
    xmlns:jsp="http://java.sun.com/JSP/Page" 
    xmlns:ui="http://www.sun.com/web/ui"
    xmlns:t="/WEB-INF/tlds/scroller"
    xmlns:tiles="http://struts.apache.org/tags-tiles"
    xmlns:c="http://java.sun.com/jsp/jstl/core">
    
    <f:subview id="searchPageView">
        <ui:form  id="form1">
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
            <input type="hidden" name="pageName" value="SearchPage" />
            <f:verbatim>
                <script language="javascript">
                   // workaround for issue where f:selectItems does not provide setStyle or setStyleClass wbossons
                    function setSearchOptionsClass() {
                        var table = document.getElementById('content:searchPageView:form1:radioButtonList1'); //content:searchPageView:form1:radioButtonList1
                        var cells = table.getElementsByTagName('TD');
                        for (var i = 0; i &lt; cells.length; i++) {
                            cells[i].className="vdcSearchOptions";
                        }
                    }
                    
                </script>
            </f:verbatim>
            <h:inputHidden  id="studyListingIndex" value="#{SearchPage.studyListingIndex}" />
                        
            <ui:panelLayout id="layoutPanelContent" panelLayout="flow" styleClass="vdcSectionMiddleNoBorder">
                <h:panelGrid  cellpadding="0" cellspacing="0" columnClasses="vdcContentCol1, vdcContentCol2" columns="2" id="gridPanel1" width="100%">
                    <ui:panelLayout  id="layoutPanel1" panelLayout="flow" styleClass="vdcSectionCol">
                        
                        <!-- START SEARCH AREA -->
                        <ui:panelLayout id="searchBlockPanel" panelLayout="flow" rendered="#{SearchPage.renderSearch}">
                            <ui:panelLayout  id="searchHeaderPanel1" panelLayout="flow" styleClass="vdcSectionHeader">
                                 <h:outputText  value="Search #{VDCRequest.currentVDC.name} Dataverse" rendered="#{VDCRequest.currentVDC != null}"/>
                                 <h:outputText  value="Search All Dataverses" rendered="#{VDCRequest.currentVDC == null}"/>     
                            </ui:panelLayout>
                            <ui:panelLayout  id="searchPanel1" panelLayout="flow" styleClass="vdcSearchSection">
                                <ui:panelGroup  block="true" id="groupPanel3" styleClass="vdcSearchGroupFirst">
                                    <h:outputLink  id="hyperlink6" value="/dvn#{VDCRequest.currentVDCURL}/faces/AdvSearchPage.jsp">
                                        <h:outputText  id="hyperlink3Text1" value="Advanced Search"/>
                                    </h:outputLink>
                                    <h:outputText  value=" | "/>
                                    <h:outputLink value="http://thedata.org/help/browsesearch" target="_blank">
                                            <h:outputText  value="Search Help"/>
                                    </h:outputLink>
                                </ui:panelGroup>
                                <ui:panelGroup  block="true" id="groupPanel6" styleClass="vdcSearchGroupMiddle">
                                    <h:outputText  id="outputText2" value=" Search: "/>
                                    <h:selectOneMenu  id="dropdown1" value="#{SearchPage.searchField}">
                                        <f:selectItem   itemLabel="Cataloging Information" itemValue="any" />
                                        <f:selectItem   itemLabel="- Author" itemValue="authorName" />
                                        <f:selectItem   itemLabel="- Title" itemValue="title" />
                                        <f:selectItem   itemLabel="- Study ID" itemValue="studyId" />
                                        <f:selectItem   itemLabel="Variable Information" itemValue="variable" />
                                    </h:selectOneMenu>
                                    <h:outputText  id="outputText3"  value=" For: "/>
                                    <h:inputText onkeypress="if (window.event) return processEvent('', 'content:searchPageView:form1:search'); else return processEvent(event, 'content:searchPageView:form1:search');" id="textField2" value="#{SearchPage.searchValue}"/>
                                    <h:commandButton  action="#{SearchPage.search_action}" id="search" value="Search"/>
                                </ui:panelGroup>
                                <ui:panelGroup block="true" id="groupPanel17" styleClass="vdcSearchGroupLast">
                                    <h:selectOneRadio id="radioButtonList1" value="#{SearchPage.searchFilter}" styleClass="vdcSearchOptions">
                                        <f:selectItems id="radio1SelectItem" value="#{SearchPage.searchRadioItems}" />
                                    </h:selectOneRadio>
                                </ui:panelGroup>
                            </ui:panelLayout>
                        </ui:panelLayout>
                        <f:verbatim>
                            <script language="javascript">
                                onLoad=setSearchOptionsClass();
                            </script>
                        </f:verbatim>
                        <!-- END SEARCH AREA -->
                        
                        <ui:panelLayout  id="layoutPanel5" panelLayout="flow" styleClass="vdcSectionHeader">
                            <h:outputText value="#{SearchPage.listHeader}"/>
                        </ui:panelLayout>
                        <ui:panelLayout  id="layoutPanel6" panelLayout="flow" style="margin: 0px; padding: 5px 0px 25px 0px; height: 100%; width: 100%">
                            <ui:panelGroup  block="true" style="padding-left: 5px;">
                                <h:outputText  styleClass="vdcSubHeaderColor" value="#{SearchPage.subListHeader}"/>
                            </ui:panelGroup>
                            <ui:panelGroup  block="true"  style="padding-left: 15px; padding-top: 15px;"> 
                                <h:outputText  id="listMessagePrefix" value="#{SearchPage.listMessagePrefix}"/>
                                <h:outputText  id="listMessageContent" styleClass="warnMessage" value="#{SearchPage.listMessageContent}"/>
                                <h:outputText  id="listMessageSuffix" value="#{SearchPage.listMessageSuffix}"/>
                            </ui:panelGroup>
                            <h:panelGrid  cellpadding="0" cellspacing="0" columns="2" id="gridPanel2"
                                styleClass="vdcSResultsTop" width="100%" rendered="#{SearchPage.renderSort or SearchPage.renderScroller}">
                                <ui:panelGroup  id="groupPanel5" rendered="#{SearchPage.renderSort}">
                                    <h:selectOneMenu  id="dropdown2" valueChangeListener="#{SearchPage.sort_action}" onchange="submit();">
                                        <f:selectItem   itemLabel="Sort By:" itemValue="" />
                                        <f:selectItem   itemLabel="- Study ID" itemValue="studyId" />
                                        <f:selectItem   itemLabel="- Title" itemValue="title" />
                                        <f:selectItem   itemLabel="- Number of Downloads" itemValue="numberOfDownloads desc" />
                                    </h:selectOneMenu>
                                </ui:panelGroup>
                                <ui:panelGroup  block="true" id="groupPanel4" style="text-align: right" rendered="#{SearchPage.renderScroller}">
                                    <t:scroller binding="#{SearchPage.scroller}" for="dataTable1" actionListener="#{SearchPage.scroll_action}">
                                        <f:facet name="previous">
                                            <h:graphicImage  value="/resources/arrow-left.gif" styleClass="vdcNoBorders"/>
                                        </f:facet>
                                        <f:facet name="next">
                                            <h:graphicImage  value="/resources/arrow-right.gif" styleClass="vdcNoBorders"/>
                                        </f:facet>                                            
                                    </t:scroller>
                                </ui:panelGroup>
                            </h:panelGrid>
                            <h:dataTable  binding="#{SearchPage.studyTable}" rows="10" headerClass="list-header" id="dataTable1"
                                rowClasses="list-row-odd, list-row-even" columnClasses="vdcSResultsList" value="#{SearchPage.studies}" var="studyUI" width="100%">
                                <h:column  id="column1">
                                    <ui:panelGroup  block="true" id="groupPanel1" style="padding-bottom: 5px">
                                        <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/study/StudyPage.jsp?studyId=#{studyUI.study.id}"  id="hyperlink3" styleClass="vdcSResultsStudyTitle">
                                            <h:outputText  id="hyperlink3Text" value="#{studyUI.study.title}"/>
                                        </h:outputLink>
                                        <h:outputText  id="outputText13" value="by #{studyUI.authors}" rendered="#{studyUI.authors != null and studyUI.authors != ''}"/>
                                    </ui:panelGroup>
                                    <ui:panelGroup  block="true">
                                        <h:outputText  value="Study Global ID:" styleClass="vdcSResultsField"  />
                                        <h:outputText  value="#{studyUI.study.globalId}"/>
                                    </ui:panelGroup>                                            
                                    <ui:panelGroup block="true" id="groupPanel2" rendered="#{studyUI.abstracts != null and studyUI.abstracts != ''}" >
                                        <h:outputText  id="outputText10" styleClass="vdcSResultsField" value="Abstract:"/>
                                        <ui:panelGroup block="true" styleClass="vdcAbstractSResults"> 
                                            <h:outputText  id="outputText14" escape="false" value="#{studyUI.truncatedAbstracts}" />
                                            <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/study/StudyPage.jsp?studyId=#{studyUI.study.id}" rendered="#{studyUI.renderAbstractsMoreLink}" >
                                                <h:outputText  value="more >>"/>
                                            </h:outputLink>
                                        </ui:panelGroup>
                                    </ui:panelGroup>
                                    <ui:panelGroup  block="true" id="groupPanel7" rendered="#{studyUI.study.productionDate != null and studyUI.study.productionDate != ''}">
                                        <h:outputText  id="outputText15" styleClass="vdcSResultsField" value="Production Date:"/>
                                        <h:outputText  id="outputText11" value="#{studyUI.study.productionDate}"/>
                                    </ui:panelGroup>
                                            
                                    <!-- Optional search fields -->
                                    <ui:panelGroup  block="true" rendered="#{!empty SearchPage.studyFields[sfc.producerName]  and !empty studyUI.producers}">
                                        <h:outputText  value="Producer:" styleClass="vdcSResultsField"  />
                                        <h:outputText  value="#{studyUI.producers}"  escape="false" />
                                    </ui:panelGroup>
                                    <ui:panelGroup  block="true" rendered="#{!empty SearchPage.studyFields[sfc.distributionDate]  and !empty studyUI.study.distributionDate}">
                                        <h:outputText  value="Distribution Date:" styleClass="vdcSResultsField"  />
                                        <h:outputText  value="#{studyUI.study.distributionDate}"  escape="false" />
                                    </ui:panelGroup>
                                    <ui:panelGroup  block="true" rendered="#{!empty SearchPage.studyFields[sfc.distributorName]  and !empty studyUI.distributors}">
                                        <h:outputText  value="Distributor:" styleClass="vdcSResultsField"  />
                                        <h:outputText  value="#{studyUI.distributors}"  escape="false" />
                                    </ui:panelGroup>
                                    <ui:panelGroup  block="true" rendered="#{!empty SearchPage.studyFields[sfc.replicationFor]  and !empty studyUI.study.replicationFor}">
                                        <h:outputText  value="Replication For:" styleClass="vdcSResultsField"  />
                                        <h:outputText  value="#{studyUI.study.replicationFor}"  escape="false" />
                                    </ui:panelGroup>
                                    <ui:panelGroup  block="true" rendered="#{!empty SearchPage.studyFields[sfc.relatedPublications]  and !empty studyUI.relPublications}">
                                        <h:outputText  value="Related Publications:" styleClass="vdcSResultsField"  />
                                        <h:outputText  value="#{studyUI.relPublications}"  escape="false" />
                                    </ui:panelGroup>
                                    <ui:panelGroup  block="true" rendered="#{!empty SearchPage.studyFields[sfc.relatedMaterial]  and !empty studyUI.relMaterials}">
                                        <h:outputText  value="Related Material:" styleClass="vdcSResultsField"  />
                                        <h:outputText  value="#{studyUI.relMaterials}"  escape="false" />
                                    </ui:panelGroup>
                                    <ui:panelGroup  block="true" rendered="#{!empty SearchPage.studyFields[sfc.relatedStudies]  and !empty studyUI.relStudies}">
                                        <h:outputText  value="Related Studies:" styleClass="vdcSResultsField"  />
                                        <h:outputText  value="#{studyUI.relStudies}"  escape="false" />
                                    </ui:panelGroup>

                                            
                                    <!-- Comment 'search term found in:' - if we don't add this functionality later, we should remove it from here
                                    <ui:panelGroup  block="true" id="groupPanel8">
                                    <h:outputText  id="outputText16" styleClass="vdcSResultsField" value="Search term found in:"/>
                                    <h:outputLink  id="hyperlink4" value="http://www.sun.com/jscreator">
                                    <h:outputText  id="hyperlink4Text" value="abstract"/>
                                    </h:outputLink>
                                    </ui:panelGroup>
                                    -->
                                    <ui:panelGroup  block="true" rendered="#{ !empty studyUI.foundInVariables}">
                                        <h:outputText  value="Found In Variables:" styleClass="vdcSResultsField"  />
                                        <h:dataTable    id="dvDataTable" value="#{studyUI.foundInVariables}" var="dv" width="100%">
                                            <h:column>
                                                <h:outputLink  value="/dvn#{VDCRequest.currentVDCURL}/faces/study/VariablePage.jsp?dvId=#{dv.id}">
                                                    <h:outputText  value="#{dv.name}" />
                                                </h:outputLink>
                                            </h:column>
                                            <h:column>
                                                <h:outputText  value="#{dv.label}" />
                                            </h:column>                                            
                                            <h:column>
                                                <h:outputText  value="#{dv.dataTable.studyFile.fileName}"  />
                                            </h:column>
                                        </h:dataTable>
                                    </ui:panelGroup>                                    
                                    <h:panelGrid  cellpadding="0" cellspacing="0"
                                        columnClasses="vdcSResultsCol1, vdcSResultsCol2" columns="2" id="gridPanel3" >
                                        <ui:panelGroup  id="groupPanel9" >
                                            <h:outputText  id="outputText17" styleClass="vdcSResultsField" value="Number of Downloads:"/>
                                            <h:outputText  id="outputText18" value="#{studyUI.numberOfDownloads}"/>
                                        </ui:panelGroup>
                                        <ui:panelGroup  id="groupPanel10" style="padding-right: 8px">
                                            <h:graphicImage alt="This study contains no files." rendered="#{!studyUI.files}"
                                            title="This study contains no files." value="/resources/icon_nofiles.gif"/>
                                            <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/study/StudyPage.jsp?studyId=#{studyUI.study.id}&amp;tab=files" rendered="#{studyUI.files}">
                                                <h:graphicImage styleClass="vdcNoBorders" alt="This study contains subsettable files." rendered="#{studyUI.subsettable}"
                                                title="This study contains subsettable files." value="/resources/icon_subsettable.gif"/>
                                                <h:graphicImage styleClass="vdcNoBorders" alt="This study contains only non-subsettable files." rendered="#{!studyUI.subsettable}"
                                                title="This study contains only non-subsettable files." value="/resources/icon_files.gif"/>
                                            </h:outputLink>
                                        </ui:panelGroup>
                                    </h:panelGrid>
                                </h:column>
                            </h:dataTable>
                        </ui:panelLayout>
                    </ui:panelLayout>
                            
                    <ui:panelLayout  id="layoutPanel2" panelLayout="flow" styleClass="vdcSectionColRight" rendered="#{SearchPage.renderTree}">
                        <ui:panelLayout  id="layoutPanel7" panelLayout="flow" styleClass="vdcSectionHeader">
                            <h:outputText  value="#{SearchPage.treeHeader}"/>
                        </ui:panelLayout>
                        <ui:panelLayout  id="layoutPanel8" panelLayout="flow" style="padding: 10px 2px 10px 5px;">
                            <h:panelGrid  cellpadding="0" cellspacing="0" columns="1"  width="100%">
                                <ui:tree  binding="#{SearchPage.collectionTree}" id="collectionTree" text=""  />
                            </h:panelGrid>
                        </ui:panelLayout>
                    </ui:panelLayout>
                            
                </h:panelGrid>
            </ui:panelLayout>

        </ui:form>
    </f:subview>
</jsp:root>
