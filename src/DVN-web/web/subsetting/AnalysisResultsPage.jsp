<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
          xmlns:h="http://java.sun.com/jsf/html" 
          xmlns:jsp="http://java.sun.com/JSP/Page" 
          xmlns:ui="http://www.sun.com/web/ui"
          xmlns:tiles="http://struts.apache.org/tags-tiles"
          xmlns:c="http://java.sun.com/jsp/jstl/core">
    
    <f:subview id="dataverseAnalysisResultsView">
        
        <ui:form id="form">
            
            <ui:panelLayout styleClass="dvn_section">
                <span class="dvn_sectionTitleR"><a href="javascript:window.history.go(-1);">Back to Analysis and Subsetting</a></span>
                <div class="dvn_sectionTitle">                
                        <h:outputText value="#{AnalysisResultsPage.studyTitle}"/>                       
                        <br />
                        <span class="dvn_preFileTitle">Data File: </span>
                        <h:outputText styleClass="dvn_fileTitle" value="#{AnalysisPage.fileName}"/>     
                </div>
                
                <div class="dvn_analysisResultContainer">
            <ui:panelLayout>
                Request ID = <h:outputText value="#{AnalysisResultsPage.requestResultPID}"/><br />
                File was created: <!-- * SAMPLE DATA * --><h:outputText value="#{AnalysisResultsPage.rexecDate}"/>(US EST)<!-- * SAMPLE DATA * --> - to be erased one hour later.
            </ui:panelLayout>
                    
                    <h:panelGroup id="pgHtml" binding="#{AnalysisResultsPage.pgHtml}">
                        <span class="dvAnalysisHeader"><h:outputText value="#{AnalysisResultsPage.requestedOption}"/></span>
                        <div class="dvAnalysisResults">
                            <div class="dvAnalysis">
                                <p style="margin-top: 0; padding-top: 0; text-align: right;"><h:outputLink value="#{AnalysisResultsPage.resultURLhtml}" id="resultURLhtml" target="_blank"><f:verbatim>Open results in a new window</f:verbatim></h:outputLink></p>
                                <h:outputText escape="false" value="&lt;iframe style=&quot;width:100%;height:440px;border:1px solid #6e7f90;&quot; src=&quot;#{AnalysisResultsPage.resultURLhtml}&quot;&gt;&lt;!-- results --&gt;&lt;/iframe&gt;"/>
                            </div>
                        </div>
                    </h:panelGroup>
                    
                    <h:panelGroup id="pgRwrksp" binding="#{AnalysisResultsPage.pgRwrksp}">
                        <span class="dvAnalysisHeader">Replication</span>
                        <div class="dvAnalysisResults dvn_overflow">
                            <div style="float: left; width: 15%; min-width: 180px;">
                                <h:commandButton value="Download Data File" action="#{AnalysisResultsPage.resultURLRworkspace}" id="resultURLRworkspace" />
                            </div>
                            <div style="float: right; width: 85%;">
                                This R workspace image contains the data file that you have used and manipulated in the current session. Download and run it on your local R installation for further analyses.
                                <div style="font-size: 90% !important;">
                                    <span style="font-size: 100% !important; color: silver;">Statistical Software Info:</span><br />
                                    <h:outputText value="#{AnalysisResultsPage.rversion}"/>, R package Zelig 3.1-1 - more info: <a href="http://gking.harvard.edu/zelig/" style="font-size: 100% !important;">http://gking.harvard.edu/zelig/</a>
                                </div>
                            </div>
                        </div>
                    </h:panelGroup>
                    
                    <h:panelGroup id="pgDwnld" binding="#{AnalysisResultsPage.pgDwnld}">
                        <span class="dvAnalysisHeader">Download Subset</span>
                        <div class="dvAnalysisResults">
                            <h:outputLink value="#{AnalysisResultsPage.resultURLdwnld}" id="resultURLdwnld"><f:verbatim>Right-click this link to download the requested subsetfile&#160;</f:verbatim></h:outputLink>
                        </div>
                    </h:panelGroup>
                    
                    <span class="dvAnalysisHeader">Citation Information</span>
                    <div class="dvAnalysisResults">
                        <div id="citationInfo" style="width:98%;padding:4px;border:1px solid #6e7f90;">
                            <!-- * SAMPLE DATA * -->
                            <pre>_Citation_ for the full data set you chose_:</pre>                
                            <blockquote>
                              <h:outputText value="#{AnalysisResultsPage.offlineCitation}"/>
                            </blockquote>
                            <pre>_Citation for this subset used in your analysis_:</pre>                
                            <blockquote>
                            <h:outputText value="#{AnalysisResultsPage.offlineCitation}"/>
                            
                            <h:outputText value="#{AnalysisResultsPage.variableList}"/>
                             [VarGrp/@var(DDI)]; 
                            <h:outputText value="#{AnalysisResultsPage.fileUNF}"/>                            
                            </blockquote>
                            <!-- * SAMPLE DATA * -->
                        </div>
                    </div>
                </div>
            </ui:panelLayout>
            
        </ui:form>
        
    </f:subview>

</jsp:root>
