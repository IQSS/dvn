<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:jsp="http://java.sun.com/JSP/Page"
  xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html"  xmlns:ui="http://www.sun.com/web/ui">

  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" cdata-section-elements="jsp:text"/>

  <xsl:template match="text()">
    <xsl:value-of select="."/>
  </xsl:template>

  <xsl:template match="*">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="@*">
    <xsl:attribute name="{name()}">
      <xsl:value-of select="."/>
    </xsl:attribute>
  </xsl:template>

  <xsl:template match="/comment()">
    <xsl:copy-of select="."/>
    <xsl:text>

</xsl:text>
  </xsl:template>

  <xsl:template match="jsp:directive.include">
    <xsl:apply-templates select="document(@file)"/>
  </xsl:template>
</xsl:stylesheet>
