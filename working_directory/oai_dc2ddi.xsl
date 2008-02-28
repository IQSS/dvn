<xsl:stylesheet version="1.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:dc="http://purl.org/dc/elements/1.1/"
		exclude-result-prefixes="dc"
>
<xsl:output method="xml" version="1.0" encoding="UTF-8"
		indent="yes" />
<xsl:template match="/">
<codeBook 
          xmlns="http://www.icpsr.umich.edu/DDI" 
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
          xsi:schemaLocation="http://www.icpsr.umich.edu/DDI 
          http://www.icpsr.umich.edu/DDI/Version2-0.xsd">
        <stdyDscr>
            <citation>
                <titlStmt>
		   <titl>
		   <xsl:for-each select="//dc:title">
			<xsl:value-of select="."/>
		   </xsl:for-each>
		   </titl>			
		   <xsl:for-each select="//dc:identifier">
		      <IDNo agency="producer"><xsl:value-of select="."/></IDNo>
		   </xsl:for-each>
	        </titlStmt>
		<rspStmt>
		   <xsl:for-each select="//dc:creator">
		   <AuthEnty><xsl:value-of select="."/></AuthEnty>
		   </xsl:for-each>
		   <xsl:for-each select="//dc:contributor">
		   <othId><xsl:value-of select="."/></othId>
		   </xsl:for-each>
		</rspStmt>

		<prodStmt>
		<xsl:for-each select="//dc:publisher">
		<xsl:if test="normalize-space(.)!=''">
		   <producer>
		   <xsl:value-of select="."/>
		   </producer>
		</xsl:if>		
		</xsl:for-each>
		<xsl:if test="normalize-space(//dc:rights)!=''">
		   <copyright>
		   <xsl:value-of select="//dc:rights"/>
		   </copyright>
		</xsl:if>		
		</prodStmt>		

		<xsl:if test="normalize-space(//dc:date)!=''">
		<distStmt>
		   <distDate>
		   <xsl:value-of select="//dc:date"/>
		   </distDate>
		</distStmt>		
		</xsl:if>		
	    </citation>
	    <stdyInfo>
		<subject>
		<xsl:for-each select="//dc:subject">
		   <keyword><xsl:value-of select="."/></keyword>
		</xsl:for-each>	
		</subject>
		<xsl:for-each select="//dc:description">
		<abstract>
		   <xsl:value-of select="."/>
		</abstract>
		</xsl:for-each>	
		<sumDscr>
		<xsl:for-each select="//dc:coverage">
		<xsl:if test="normalize-space(.)!=''">
		   <geogCover>
		      <xsl:value-of select="."/>
		   </geogCover>
		</xsl:if>
		</xsl:for-each>	
		</sumDscr>
	    </stdyInfo>
	</stdyDscr>
</codeBook>
</xsl:template>
</xsl:stylesheet>


