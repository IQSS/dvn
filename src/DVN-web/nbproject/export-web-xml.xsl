<?xml version="1.0" encoding="UTF-8"?>
 
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:w="http://java.sun.com/xml/ns/j2ee">
  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>

  <!-- Default rule for any node is to copy it -->
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="/w:web-app">
    <xsl:copy>
      <xsl:for-each select="@*">
        <xsl:copy-of select="."/>
      </xsl:for-each>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="/comment()">
    <xsl:copy-of select="."/>
    <xsl:text>


    </xsl:text>
  </xsl:template>

  <xsl:template match="/w:web-app/text()"/>

  <xsl:template match="/w:web-app/*">
      <xsl:choose>
        <xsl:when test="name()='servlet' and ./w:servlet-name/text()='ExceptionHandlerServlet'">
        </xsl:when>
        <xsl:when test="name()='servlet-mapping' and ./w:servlet-name/text()='ExceptionHandlerServlet'">
        </xsl:when>
        <xsl:when test="name()='error-page' and ./w:location/text()='/error/ExceptionHandler'">
        </xsl:when>
        <xsl:otherwise>
          <xsl:for-each select="./preceding-sibling::comment()[1]">
            <xsl:text>

</xsl:text>
            <xsl:copy-of select="."/>
              <xsl:text>
              </xsl:text>
            </xsl:for-each>
          <xsl:copy-of select="."/>
        </xsl:otherwise>
      </xsl:choose>
  </xsl:template>
</xsl:stylesheet>
