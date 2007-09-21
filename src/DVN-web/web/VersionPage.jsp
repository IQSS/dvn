<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
   <f:subview id="VersionPageView">
        <ui:form  id="versionForm">
            
        <div class="ContentArea">
            <p><span style="font-family: 'lucida grande', geneva, Verdana,Arial,Helvetica,sans-serif; color: #666; font-size: 16px; font-weight: bold; font-style: italic; ">Version and License</span></p>
            
            <div class="ContentAreaBody">
                <br />
                <span style="font-family: 'lucida grande', geneva, Verdana,Arial,Helvetica,sans-serif; font-weight: bold; font-size: 14px; ">Dataverse Network</span>
                <br /><br />
                Version <h:outputText value="#{VersionPage.versionNumber}" /> - Build <h:outputText value="#{VersionPage.buildNumber}" /> />
                <br />
                <br />
                <br />
                This software is distributed under the <a href="http://www.affero.org/oagpl.html" target="_blank">Affero General Public License</a>    
                <br />
                 <br />
            </div>
            
            <div class="ContentAreaFooter">
                <p>
                    <ui:imageHyperlink alt="Powered by the Dataverse Network Project" border="0" imageURL="/resources/poweredby_logo.gif" toolTip="Link to the Dataverse Network Project" url="http://thedata.org"/>
                </p>
            </div>
        </div>
            
        </ui:form>
    </f:subview>
</jsp:root>
