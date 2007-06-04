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
    <h:form>
      <h2>Hi. My name is Duke. Here is your Study. </h2>
      <h:graphicImage id="waveImg" url="/jsfExample/wave.med.gif" alt="Duke waving" />
      
      <h2>Study Details</h2>
      <h:panelGrid columns="2" cellpadding="5" cellspacing="5">
         <h:outputText value="Title: "/>  <h:outputText value="#{VDCSessionBean.currentStudy.title}"/>
        <h:outputText value="Producer: "/>  <h:outputText value="#{VDCSessionBean.currentStudy.producerName}"/>
            <h:commandButton immediate="true" action="success" value="Go Back To List"/>
      </h:panelGrid>
    </h:form>
  </f:view>
 </body>
</HTML>  
