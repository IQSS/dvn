<?xml version="1.0" encoding="UTF-8"?>
 
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:w="http://java.sun.com/xml/ns/j2ee"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  exclude-result-prefixes="#default w xsi">

  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"
      doctype-public="-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
      doctype-system="http://java.sun.com/dtd/web-app_2_3.dtd"/>

  <xsl:template match="text()">
    <xsl:value-of select="."/>
  </xsl:template>

  <xsl:template match="*">
    <xsl:element name="{name()}">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="@*">
    <xsl:attribute name="{name()}">
      <xsl:value-of select="."/>
    </xsl:attribute>
  </xsl:template>

  <xsl:template match="/w:web-app">
    <xsl:element name="web-app">
      <xsl:apply-templates select="node()[name() = 'icon']"/>
      <xsl:apply-templates select="node()[name() = 'display-name']"/>
      <xsl:apply-templates select="node()[name() = 'description']"/>
      <xsl:apply-templates select="node()[name() = 'distributable']"/>
      <xsl:apply-templates select="node()[name() = 'context-param']"/>
      <xsl:apply-templates select="node()[name() = 'filter']"/>
      <xsl:apply-templates select="node()[name() = 'filter-mapping']"/>
      <xsl:apply-templates select="node()[name() = 'listener']"/>
      <xsl:apply-templates select="node()[name() = 'servlet']"/>
      <xsl:apply-templates select="node()[name() = 'servlet-mapping']"/>
      <xsl:apply-templates select="node()[name() = 'session-config']"/>
      <xsl:apply-templates select="node()[name() = 'mime-mapping']"/>
      <xsl:apply-templates select="node()[name() = 'welcome-file-list']"/>
      <xsl:apply-templates select="node()[name() = 'error-page']"/>
      <xsl:apply-templates select="node()[name() = 'taglib']"/>
      <xsl:apply-templates select="node()[name() = 'resource-env-ref']"/>
      <xsl:apply-templates select="node()[name() = 'resource-ref']"/>
      <xsl:apply-templates select="node()[name() = 'security-constraint']"/>
      <xsl:apply-templates select="node()[name() = 'login-config']"/>
      <xsl:apply-templates select="node()[name() = 'security-role']"/>
      <xsl:apply-templates select="node()[name() = 'env-entry']"/>
      <xsl:apply-templates select="node()[name() = 'ejb-ref']"/>
      <xsl:apply-templates select="node()[name() = 'ejb-local-ref']"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="/comment()">
    <xsl:copy-of select="."/>
    <xsl:text>

</xsl:text>
  </xsl:template>

  <xsl:template match="/w:web-app/text()"/>

  <xsl:template match="/w:web-app/*">
    <xsl:choose>
      <xsl:when test="name()='jsp-config'"/>
      <xsl:otherwise>
        <xsl:for-each select="./preceding-sibling::comment()[1]">
          <xsl:text>

</xsl:text>
          <xsl:copy-of select="."/>
          <xsl:text>
</xsl:text>
        </xsl:for-each>
        <xsl:element name="{name()}">
          <xsl:apply-templates select="@*|node()"/>
        </xsl:element>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="/w:web-app/w:welcome-file-list/w:welcome-file">
    <xsl:element name="{name()}">JSCreator_index.jsp</xsl:element>
  </xsl:template>

  <xsl:template match="/w:web-app/w:filter/w:init-param">
    <xsl:element name="{name()}">
      <xsl:apply-templates select="node()[name() != 'description']"/>
      <xsl:apply-templates select="node()[name() = 'description']"/>
    </xsl:element>
  </xsl:template>
</xsl:stylesheet>
