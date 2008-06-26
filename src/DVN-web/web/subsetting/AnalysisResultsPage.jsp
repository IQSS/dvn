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
                    
                    <h:panelGroup id="pgHtml" binding="#{AnalysisResultsPage.pgHtml}">
                        <span class="dvAnalysisHeader"><h:outputText value="#{AnalysisResultsPage.requestedOption}"/></span>
                        <div class="dvAnalysisResults">
                            Request ID: <h:outputText value="#{AnalysisResultsPage.requestResultPID}"/><br/>
                            File Created: <h:outputText value="#{AnalysisResultsPage.rexecDate}"/> (US EST) - Note: will be erased one hour later.
                            <p class="resultsNewWindowLink"><h:outputLink value="#{AnalysisResultsPage.resultURLhtml}" id="resultURLhtml" target="_blank"><f:verbatim>Open results in a new window</f:verbatim></h:outputLink></p>
                            <h:outputText escape="false" value="&lt;iframe src=&quot;#{AnalysisResultsPage.resultURLhtml}&quot;&gt;&lt;!-- results --&gt;&lt;/iframe&gt;"/>
                        </div>
                    </h:panelGroup>
                    
                    <h:panelGroup id="pgRwrksp" binding="#{AnalysisResultsPage.pgRwrksp}">
                        <span class="dvAnalysisHeader">Replication</span>
                        <div class="dvAnalysisResults dvn_overflow">
                            This R workspace image contains the data file that you have used and manipulated in the current session. Download and run it on your local R installation for further analyses.
                            <div id="rWrkspButton">
                                <h:commandButton value="Download Data File" action="#{AnalysisResultsPage.resultURLRworkspace}" id="resultURLRworkspace" />
                            </div>
                            <div id="rWrkspMessage">
                                <div id="rZeligInfo">
                                    <span>Statistical Software Info:</span><br />
                                    <h:outputText value="#{AnalysisResultsPage.rversion}"/>, R package Zelig 3.1-1 - more info: <a href="http://gking.harvard.edu/zelig/">http://gking.harvard.edu/zelig/</a>
                                </div>
                            </div>
                        </div>
                    </h:panelGroup>
                    
                    <h:panelGroup id="pgDwnld" binding="#{AnalysisResultsPage.pgDwnld}">
                        <span class="dvAnalysisHeader">Download Subset</span>
                        <div class="dvAnalysisResults">
                            Download instrustions: <h:outputLink value="#{AnalysisResultsPage.resultURLdwnld}" id="resultURLdwnld"><f:verbatim>Right-click this link and select, 'Save Link As...'</f:verbatim></h:outputLink> to download the requested subset file.
                        </div>
                    </h:panelGroup>
                    
                    <span class="dvAnalysisHeader">Citation Information</span>
                    <div class="dvAnalysisResults">
                        <div id="citationInfo">
                            Citation for full data set:
                            <blockquote>
                                <h:outputText value="#{AnalysisResultsPage.offlineCitation}" escape="false"/>
                            </blockquote>
                            Citation for subset analysis:
                            <blockquote>
                                <h:outputText value="#{AnalysisResultsPage.offlineCitation}" escape="false"/>
                                <h:outputText value="#{AnalysisResultsPage.variableList}"/>
                                 [VarGrp/@var(DDI)]; 
                                <h:outputText value="#{AnalysisResultsPage.fileUNF}"/>                            
                            </blockquote>
                        </div>
                    </div>
                </div>
            </ui:panelLayout>
            
        </ui:form>
        
    </f:subview>

</jsp:root>
