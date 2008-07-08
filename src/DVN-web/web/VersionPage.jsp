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

<gui:param name="pageTitle" value="DVN - Version and License" />

  <gui:define name="body">
      <f:loadBundle basename="Bundle" var="bundle"/>
     <f:loadBundle basename="BundleAnalysis" var="bundleAnalysis"/>

        <ui:form  id="versionForm">
            
        <div class="ContentArea">
            <p><span style="font-family: 'lucida grande', geneva, Verdana,Arial,Helvetica,sans-serif; color: #666; font-size: 16px; font-weight: bold; font-style: italic; ">Version and License</span></p>
            
            <div class="ContentAreaBody">
                <br />
                <span style="font-family: 'lucida grande', geneva, Verdana,Arial,Helvetica,sans-serif; font-weight: bold; font-size: 14px; ">Dataverse Network</span>
                <br /><br />
                Version <h:outputText value="#{VersionPage.versionNumber}" />.<h:outputText value="#{VersionPage.buildNumber}" />
                <br />
                <br />
                This software is distributed under the <a href="http://www.affero.org/oagpl.html" target="_blank">Affero General Public License</a>    
                <br />
            </div>
            
            <div class="ContentAreaFooter">
                <p>
                    <ui:imageHyperlink alt="Powered by the Dataverse Network Project" border="0" imageURL="/resources/dvnPoweredByLogo.gif" toolTip="Link to the Dataverse Network Project" url="http://thedata.org"/>
                </p>
            </div>
        </div>
            
        </ui:form>
            </gui:define>
        </gui:composition>
    </body>
</html>
