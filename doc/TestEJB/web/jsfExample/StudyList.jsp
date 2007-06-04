<!DOCTYPE html
PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<!--
 * Copyright (c) 2006 Sun Microsystems, Inc.  All rights reserved.  U.S. 
 * Government Rights - Commercial software.  Government users are subject 
 * to the Sun Microsystems, Inc. standard license agreement and 
 * applicable provisions of the FAR and its supplements.  Use is subject 
 * to license terms.  
 * 
 * This distribution may include materials developed by third parties. 
 * Sun, Sun Microsystems, the Sun logo, Java and J2EE are trademarks 
 * or registered trademarks of Sun Microsystems, Inc. in the U.S. and 
 * other countries.  
 * 
 * Copyright (c) 2004-2006 Sun Microsystems, Inc. Tous droits reserves.
 * 
 * Droits du gouvernement americain, utilisateurs gouvernementaux - logiciel
 * commercial. Les utilisateurs gouvernementaux sont soumis au contrat de 
 * licence standard de Sun Microsystems, Inc., ainsi qu'aux dispositions 
 * en vigueur de la FAR (Federal Acquisition Regulations) et des 
 * supplements a celles-ci.  Distribue par des licences qui en 
 * restreignent l'utilisation.
 * 
 * Cette distribution peut comprendre des composants developpes par des 
 * tierces parties. Sun, Sun Microsystems, le logo Sun, Java et J2EE 
 * sont des marques de fabrique ou des marques deposees de Sun 
 * Microsystems, Inc. aux Etats-Unis et dans d'autres pays.
-->

<HTML xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
    <HEAD> <title>VDC JSF Example</title> </HEAD>
    <%@ page contentType="application/xhtml+xml" %>
    <%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
    <%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
    <body bgcolor="white">
    <f:view>
    <h:form id="studyListForm" >
     

      <h2>Example List of Studies for Editing </h2>
     
       
                 
      
                            <h:dataTable cellpadding="5" cellspacing="5" headerClass="list-header" id="dataTable1" rowClasses="list-row-even,list-row-odd"
                             value="#{studyList.studies}" var="study">
                             <h:column  id="column1">
                                <h:outputText  id="outputText1" value="#{study.id}"/>
                                <f:facet name="header">
                                    <h:outputText  id="outputText2" value="Study ID"/>
                                </f:facet>
                            </h:column>
                          <h:column  id="column2">
                                <h:outputText  id="outputText3" value="#{study.title}"/>
                                <f:facet name="header">
                                    <h:outputText  id="outputText4" value="Study Title"/>
                                </f:facet>
                            </h:column>
                            <h:column>
                                <h:commandLink action="#{studyList.goToDetails}">
                                    <h:outputText value="View"/>
                                </h:commandLink>
                        </h:column>  
                         <h:column>
                                <h:commandLink action="#{studyList.goToUpdate}">
                                    <h:outputText value="Update"/>
                                </h:commandLink>
                        </h:column>               

                            <h:column>
                                <h:commandLink action="#{studyList.goToDelete}">
                                    <h:outputText value="Delete"/>
                                </h:commandLink>
                        </h:column>               
                </h:dataTable>
      <h:commandButton action="#{studyList.goToAdd}" value="Create New Study" />
      <p>
      <h:messages style="color: red; font-family: 'New Century Schoolbook', serif; font-style: oblique; text-decoration: overline" />
    </p>
   </h:form>
  </f:view>
 </body>
</HTML>  
