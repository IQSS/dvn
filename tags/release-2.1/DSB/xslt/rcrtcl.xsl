<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:saxon="http://icl.com/saxon"  xmlns:ddi="http://www.icpsr.umich.edu/DDI">
<xsl:param name="fileid"/>
<xsl:param name="filename"/>
<xsl:param name="lstx"/>
<xsl:variable name="lst" select="saxon:evaluate(concat('/ddi:codeBook/ddi:dataDscr/ddi:var[',$lstx, ']'))"/>
<xsl:variable name="quot">"</xsl:variable>
<xsl:variable name="apos">'</xsl:variable>
<!--
<xsl:text>&#009;</xsl:text>
-->


<!-- main body  -->

<xsl:output method="text"/>
<xsl:template match="/">
	<xsl:value-of select="/ddi:codeBook/ddi:fileDscr[@ID=$fileid]/ddi:fileTxt/ddi:dimensns/ddi:recPrCas"/><xsl:text>&#xA;</xsl:text>
	<xsl:for-each select="$lst">
		<!-- xsl:value-of select="position()"/><xsl:text>&#009;</xsl:text -->
		<xsl:choose>
			<xsl:when test="@ID"><xsl:value-of select="@ID"/><xsl:text>&#009;</xsl:text></xsl:when>
			<xsl:otherwise><xsl:value-of select="@name"/><xsl:text>&#009;</xsl:text></xsl:otherwise>
		</xsl:choose>
		<xsl:value-of select="ddi:location/@RecSegNo"/><xsl:text>&#009;</xsl:text>
		<xsl:value-of select="@name"/><xsl:text>&#009;</xsl:text>
		<xsl:value-of select="ddi:location/@StartPos"/><xsl:text>&#009;</xsl:text>
		<xsl:value-of select="ddi:location/@EndPos"/><xsl:text>&#009;</xsl:text>
		<xsl:choose>
			<xsl:when test='ddi:varFormat/@type="character"'>A</xsl:when>
			<xsl:when test="@intrvl='contin'">F</xsl:when>
			<xsl:when test="@intrvl='discrete'">I</xsl:when>
			<xsl:otherwise>I</xsl:otherwise>
		</xsl:choose>
		<xsl:text>&#009;</xsl:text>
		<xsl:value-of select="ddi:location/@EndPos - ddi:location/@StartPos + 1"/><xsl:text>&#009;</xsl:text>
		<xsl:choose>
			<xsl:when test="@dcml"><xsl:value-of select="@dcml"/></xsl:when>
			<xsl:otherwise></xsl:otherwise>
		</xsl:choose>
		<xsl:text>&#xA;</xsl:text>
		
	</xsl:for-each>
</xsl:template>
<!-- End of the main part -->
</xsl:stylesheet>

<!-- coding note: space = &#x20; carriage return = &#xA;  -->
