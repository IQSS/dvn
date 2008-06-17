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
                <div class="dvn_sectionTitle dvn_overflow">
                    <span class="dvn_floatL" style="font-size: 110% !important;">Dataverse Analysis</span> <span class="dvn_floatR" style="text-transform: none; font-weight: bold;"><a href="javascript:window.history.go(-1);">&lt; Go back to the previous page</a></span>
                </div>
                
                <div class="dvn_analysisResultContainer">
                    <span class="dvAnalysisHeader">Dataset Citation Information</span>
                    <div class="dvAnalysisResults">
                        <div class="dvAnalysisInfoMessage"><img src="/dvn/resources/icon_info.gif" alt="Information" style="vertical-align: bottom" title="Information" class="vdcNoBorders" /> Click Citation Info button to see how to cite this data set.</div>
                        <div style="margin-left: 2em;">
                            <h3 class="dvAnalysisTypeLabel">
                                Study Title:  <h:outputText value="#{AnalysisResultsPage.studyTitle}"/> 
                            </h3> <input type="button" value="Citation Info" onclick="" />
                        </div>
                        <div id="citationInfo" class="">
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
                    
                    <span class="dvAnalysisHeader">Request type: <h:outputText value="#{AnalysisResultsPage.requestedOption}"/> Request ID = <h:outputText value="#{AnalysisResultsPage.requestResultPID}"/></span>
                    <div class="dvAnalysisResults">
                        <div class="dvAnalysisInfoMessage"><img src="/dvn/resources/icon_info.gif" alt="Information" style="vertical-align: bottom" title="Information" class="vdcNoBorders" /> Click the following links to check each model's estimation results and descriptive statistics (if applicable).</div>
                        
                        <div style="padding: .5em 0 1em 0; text-align: center;"><strong>NOTE: These statistics results, including this index page and other data files, will be automatically erased one hour after they are created.</strong></div>
                        <div class="dvAnalysis">
                            
                            <h:panelGroup id="pgDwnld" binding="#{AnalysisResultsPage.pgDwnld}"><h3 class="dvAnalysisTypeLabel"><h:outputLink value="#{AnalysisResultsPage.resultURLdwnld}" id="resultURLdwnld"><f:verbatim>Right-click this link to download the requested subsetfile&#160;</f:verbatim><h:graphicImage url="/resources/icon_download.gif"/></h:outputLink></h3></h:panelGroup><br />
                            <h:panelGroup id="pgHtml"  binding="#{AnalysisResultsPage.pgHtml}"><h3 class="dvAnalysisTypeLabel"><h:outputLink value="#{AnalysisResultsPage.resultURLhtml}" id="resultURLhtml"><f:verbatim>Click this link to see reults of an eda or xtab or zelig request&#160;</f:verbatim><h:graphicImage url="/resources/icon_analyze.gif"/></h:outputLink></h3></h:panelGroup><br />
                            <h:panelGroup id="pgRwrksp" binding="#{AnalysisResultsPage.pgRwrksp}"><h3 class="dvAnalysisTypeLabel"><h:outputLink value="#{AnalysisResultsPage.resultURLRworkspace}" id="resultURLRworkspace"><f:verbatim>Right-click this link to download the R workspace data&#160;</f:verbatim><h:graphicImage url="/resources/icon_download.gif"/></h:outputLink></h3></h:panelGroup><br />
                            <br />
                            <!-- input type="button" onclick="shwNshw('Rlog',event,'Log')" value="R Log File" / -->
                        </div>
                        
                        <!-- div id="Rlog" class="dvAnalysisDataContainer" -->
                            <!-- * SAMPLE DATA * -->
                            <!-- h3>R Log File</h3 -->
                            <!-- pre -->

                                <!-- R LOG FILE CODE HERE -->

                            <!-- /pre -->
                            <!-- * SAMPLE DATA * -->
                        <!-- /div -->
                        
                    </div>
                    
                    <span class="dvAnalysisHeader">Download Data File</span>
                    <div class="dvAnalysisResults">
                        <div class="dvAnalysisInfoMessage"><img src="/dvn/resources/icon_info.gif" alt="Information" style="vertical-align: bottom" title="Information" class="vdcNoBorders" /> Click the appropriate button to download the data format that best suits your needs.</div>
                        <a href="http://dsb-2.hmdc.harvard.edu/temp/Zlg_359303/Data.359303.dta" type="application/x-stata">STATA file (Data.359303.dta)</a>
                            <div class="dvAnalysis">
                            <input type="button" value="Tab-Delimited R-Data" />
                            <input type="button" value="R-Code" style="margin-left: 1em;" />
                            <input type="button" value="Workspace Data" style="margin-left: 1em;" />
                        </div>
                    </div>
                </div>
            </ui:panelLayout>
            <ui:panelLayout>
                File was created: <!-- * SAMPLE DATA * -->Wed Mar 19 15:16:16 2008 (US EST)<!-- * SAMPLE DATA * --> - to be erased one hour later. <a target="contact" href="/dvn/faces/ContactUsPage.jsp" onclick="window.open(this.href,'contact','width=640,height=480,left=0,top=0,scrollbars=1,resizable=1');return false;">Contact us about this request</a>.
            </ui:panelLayout>
            <ui:panelLayout>
                <em>Thank you for using this Dataverse.</em>
            </ui:panelLayout>
            
        </ui:form>
        
    </f:subview>

</jsp:root>
