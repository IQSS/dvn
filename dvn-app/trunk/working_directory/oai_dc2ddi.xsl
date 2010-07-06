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
		      <IDNo>
		        <xsl:attribute name="agency">
		        <xsl:choose>
		         <xsl:when test='starts-with(.,"hdl:")'>handle</xsl:when>
			 <xsl:when test='starts-with(.,"http://hdl.handle.net/")'>handle</xsl:when>
		        </xsl:choose>
		        </xsl:attribute>
		        <xsl:choose>
			 <xsl:when test='starts-with(.,"http://hdl.handle.net/")'>hdl:<xsl:value-of select='substring(.,23)'/></xsl:when>
		         <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
		        </xsl:choose>
		      </IDNo>
		   </xsl:for-each>
	        </titlStmt>
		<rspStmt>
		   <xsl:for-each select="//dc:creator">
		   <AuthEnty><xsl:value-of select="."/></AuthEnty>
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

		<xsl:for-each select="//dc:date">
		<xsl:if test="normalize-space(.)!=''">
		   <prodDate>
		   <xsl:value-of select="normalize-space(.)"/>
		   </prodDate>
		</xsl:if>		
		</xsl:for-each>		
		</prodStmt>		

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
		<xsl:for-each select="//dc:type">
		<xsl:if test="normalize-space(.)!=''">
		   <dataKind>
		      <xsl:value-of select="."/>
		   </dataKind>
		</xsl:if>
		</xsl:for-each>	
		</sumDscr>
	    </stdyInfo>

	    <xsl:if test="normalize-space(//dc:source)!=''">
	    <method>
	       <dataColl>
	          <sources>
		     <xsl:for-each select="//dc:source">
		     <xsl:if test="normalize-space(.)!=''">
	             <dataSrc>
		        <xsl:value-of select="normalize-space(.)"/>
	             </dataSrc>
		     </xsl:if>
		     </xsl:for-each>
	          </sources>
	       </dataColl>
	     </method>
	     </xsl:if>


	    <xsl:for-each select="//dc:rights">
	    <xsl:if test="normalize-space(.)!=''">
            <dataAccs>
	       <useStmt>
	     	  <restrctn>
		   	<xsl:value-of select="normalize-space(.)"/>
		  </restrctn>
               </useStmt>
            </dataAccs>
            </xsl:if>
	    </xsl:for-each>
	    <xsl:if test="normalize-space(//dc:relation)!=''">
	    <othrStdyMat>
	    <xsl:for-each select="//dc:relation">
	    <xsl:if test="normalize-space(.)!=''">
	       <relMat>
	          <xsl:value-of select="normalize-space(.)"/>
	       </relMat>
	    </xsl:if>
	    </xsl:for-each>
	    </othrStdyMat>
	    </xsl:if>
	</stdyDscr>
</codeBook>
</xsl:template>
</xsl:stylesheet>


