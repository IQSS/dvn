<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : copy.xsl
    Created on : May 22, 2007, 2:36 PM
    Author     : Ellen Kraffmiller
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:oai="http://www.openarchives.org/OAI/2.0/" >
    

    <!-- TODO customize transformation rules 
         syntax recommendation http://www.w3.org/TR/xslt 
    -->
    
  
    
    <xsl:template match="/">
       <OAI-PMH>
            <xsl:apply-templates select="oai:OAI-PMH/oai:error"/>
       </OAI-PMH>
    </xsl:template>

    <xsl:template match="oai:OAI-PMH/oai:error">
        <error>
            <xsl:apply-templates select="@* | node()"/>
        </error>
    </xsl:template>

   <xsl:template match="@* | node()">
      <xsl:copy>
          <xsl:apply-templates select="@* | node()"/>
      </xsl:copy>
    </xsl:template>
    
   
</xsl:stylesheet>
