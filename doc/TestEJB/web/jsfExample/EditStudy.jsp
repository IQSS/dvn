<!DOCTYPE html
PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<HTML xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
    <HEAD> <title>Hello</title> </HEAD>
    <%@ page contentType="application/xhtml+xml" %>
    <%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
    <%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
    <body bgcolor="white">
        <f:view>
    
            <h2>Hi. My name is Duke. Here is your Study form. </h2>
            <h:graphicImage id="waveImg" url="/jsfExample/wave.med.gif" alt="Duke waving" />

            <h2>Here is your Study Form</h2>
            <h:form>
                <h:panelGrid columns="2" cellpadding="5" cellspacing="5">
                    <h:outputLabel for="titleInput">
                        <h:outputText value="Title:"/>
                    </h:outputLabel>
                    <h:inputText id="studyTitle" value="#{VDCSessionBean.currentStudy.title}"/>
                    
                     <h:outputLabel for="producerInput">
                        <h:outputText value="Producer:"/>
                    </h:outputLabel>
                  <h:inputText id="studyProducer" value="#{VDCSessionBean.currentStudy.producerName}"/>
    
      
                    <h:commandButton action="#{studyDetail.add}" value="Create Study" rendered="#{studyDetail.isNew}" />
                    <h:commandButton action="#{studyDetail.update}" value="Update Study" rendered="#{!studyDetail.isNew}" />
                </h:panelGrid>
            </h:form>
     
        </f:view>
    </body>
</HTML>  
