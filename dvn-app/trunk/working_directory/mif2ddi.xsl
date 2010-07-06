<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.icpsr.umich.edu/DDI" xmlns:a="http://www.thedataweb.org/mif" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" exclude-result-prefixes="a">
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    <xsl:strip-space elements="*"/>
    <xsl:param name="schemaLocation">http://www.icpsr.umich.edu/DDI/Version1-3.xsd</xsl:param>
    <xsl:param name="fileDscrId" select="concat('ID',generate-id())"/>
    <!-- *** -->
    <!-- 	Default Template to start processng of MIF Document    -->
    <!-- *** -->
    <xsl:template match="/">
            <xsl:choose>
                <xsl:when test="a:mifSet">
                    <xsl:apply-templates select="a:mifSet/a:mif[position() = 1]"/>
    		    <!-- TODO: Deal with multiple docs in a mifset-->
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates select="a:mif"/>
                </xsl:otherwise>
            </xsl:choose>
    </xsl:template>
    
    <xsl:template match="a:mif">
	<!-- Nest all in a codebook element -->
        <xsl:element name="codeBook" namespace="http://www.icpsr.umich.edu/DDI">
            <xsl:attribute name="xsi:schemaLocation"><xsl:text>http://www.icpsr.umich.edu/DDI </xsl:text>
		<xsl:value-of select="$schemaLocation"/></xsl:attribute>
            <docDscr>
            	<citation>
        	    <xsl:call-template name="titlStmt"/>
        	    	<xsl:if test="normalize-space(a:documentDescription/a:producer) != '' or normalize-space(a:documentDescription/a:producer/@prodDate) != ''">
                    	<prodStmt>
                    		<xsl:if test="normalize-space(a:documentDescription/a:producer) != ''">
                         		<producer>
                                	<xsl:value-of select="normalize-space(a:documentDescription/a:producer)"/>
                          		</producer>
                          	</xsl:if>
                          	<xsl:if test="normalize-space(a:documentDescription/a:producer/@prodDate) != ''">
                          		<prodDate>
                          			<xsl:attribute name="date"><xsl:value-of select="normalize-space(a:documentDescription/a:producer/@prodDate)"/></xsl:attribute>
                          		</prodDate>
                          	</xsl:if>
                    	</prodStmt>
                    </xsl:if>
                    <distStmt>
                    <!--  We are adding this temporarily. Census should provide this information in the MIF.  -->
           			 	<distrbtr>
               				<ExtLink URI="http://www.thedataweb.org/images/ferrett_dataweb.jpg" title="Logo" role="image"/>
                        	U.S. Census Bureau
                        	<ExtLink URI="http://www.thedataweb.org/index.html" title="URL"/>
            			</distrbtr>
        			 </distStmt>
                    <xsl:if test="normalize-space(a:documentDescription/a:version/@versionnum) != ''">
                    	<verStmt>
                        	<version>
                             <xsl:attribute name="date"><xsl:value-of select="normalize-space(a:documentDescription/a:version/@modDate)"/></xsl:attribute>
                             <xsl:value-of select="normalize-space(a:documentDescription/a:version/@versionnum)"/>
                        	 </version>
                    	</verStmt>
                    </xsl:if>
                    <!--  Add holdings for all studies from thedataweb -->
                    <holdings URI="http://www.thedataweb.org"/>
                </citation>
            </docDscr>
            <stdyDscr>
            	<citation>
        	    <xsl:call-template name="titlStmt"/>
                    <xsl:if test="normalize-space(a:dataSet/a:providerInfo) != '' and (normalize-space(a:dataSet/a:providerInfo/@name) != 0 or normalize-space(a:dataSet/a:providerInfo/@name) != '' )">
                    	<rspStmt>
                         	<AuthEnty>
                            	 <xsl:value-of select="normalize-space(a:dataSet/a:providerInfo/@name)"/>
                    	     	<xsl:text>:</xsl:text>
                             	<xsl:value-of select="normalize-space(a:dataSet/a:providerInfo)"/>
                        	</AuthEnty>   
                    	</rspStmt>
                    </xsl:if>
                    <xsl:if test="normalize-space(a:dataSet/a:sponsorInfo/@name) != '' or normalize-space(a:dataSet/a:sponsorInfo/@imageUrl) != '' or normalize-space(a:dataSet/a:sponsorInfo/@homepageUrl) != ''">
                    <distStmt>
                       <distrbtr>
                        <xsl:if test="normalize-space(a:dataSet/a:sponsorInfo/@imageUrl) != ''">
                       		<ExtLink title="Logo" role="image">
                       		<xsl:attribute name="URI"><xsl:value-of select="normalize-space(a:dataSet/a:sponsorInfo/@imageUrl)"/></xsl:attribute>
                       	    </ExtLink>
                       	</xsl:if>
                        	<xsl:value-of select="normalize-space(a:dataSet/a:sponsorInfo/@name)"/>
                        <xsl:if test="normalize-space(a:dataSet/a:sponsorInfo/@homepageUrl) != ''">
                        	<ExtLink title="URL">
                       		<xsl:attribute name="URI"><xsl:value-of select="normalize-space(a:dataSet/a:sponsorInfo/@homepageUrl)"/></xsl:attribute>
                       	    </ExtLink>
                        </xsl:if>
                       </distrbtr>
                    </distStmt>
                    </xsl:if>
                </citation>
                <stdyInfo>
		    <subject>
			<topcClas source="archive" vocab="TheDataWeb">TheDataWeb</topcClas> 
			<!-- TODO: Grab variable concepts and put them here --> 
		    </subject>
		    	<xsl:if test="normalize-space(a:dataSet/a:abstract) != ''">
                    <abstract>
						<xsl:value-of select="a:dataSet/a:abstract"/>	
                    </abstract>
                 </xsl:if>
                 <xsl:if test="normalize-space(a:dataSet/a:collectDate/@end) != '' or normalize-space(a:dataSet/a:collectDate/@start) != '' or  normalize-space(a:dataSet/a:category) != '' ">               
                    <sumDscr>
                       <xsl:if test="normalize-space(a:dataSet/a:collectDate/@end) != '' or normalize-space(a:dataSet/a:collectDate/@start) != ''">
                         <collDate>
                             <xsl:attribute name="date"><xsl:value-of select="normalize-space(a:dataSet/a:collectDate/@start)"/></xsl:attribute>
			    			 <xsl:attribute name="event">start</xsl:attribute>
						</collDate>
                         <collDate>
                            <xsl:attribute name="date"><xsl:value-of select="normalize-space(a:dataSet/a:collectDate/@end)"/></xsl:attribute>
			     <xsl:attribute name="event">end</xsl:attribute>
                         </collDate>
                         </xsl:if>
                         <xsl:if test="normalize-space(a:dataSet/a:category) != ''">
                         <dataKind>
                              <xsl:value-of select="normalize-space(a:dataSet/a:category)"/>
                         </dataKind>
                         </xsl:if>
                    </sumDscr>
                  </xsl:if>
                </stdyInfo>

		<!-- The original MIFs should have some text in restrictions explaining where to get the data. -->
        
        	<xsl:if test="normalize-space(a:dataSet/a:restriction/@originaluri) != ''">   
			<dataAccs>
					<useStmt>
            			<specPerm>
            			<xsl:attribute name="URI"><xsl:value-of select="normalize-space(a:dataSet/a:restriction/@originaluri)"/></xsl:attribute>
            			You must agree to the terms and conditions described here: <xsl:value-of select="normalize-space(a:dataSet/a:restriction/@originaluri)"/></specPerm>
            		</useStmt>     	
 			</dataAccs>
			</xsl:if>

            </stdyDscr>
            <xsl:if test="a:dataSet/a:extractionHost">
               <fileDscr>
                    <xsl:attribute name="ID"><xsl:value-of select="$fileDscrId"/></xsl:attribute>
                    <xsl:attribute name="URI">
			<!-- TODO: Add virtualid match for round-tripping -->
			<!-- TODO: Need to url-encode these -->
			<!-- TODO: Need to  add port-->
			<xsl:value-of select="a:dataSet/a:extractionHost/@uri"/>
			<xsl:text>/TheDataWeb_Tabulation/VDCRepositoryServlet/</xsl:text>	
			<xsl:value-of select="a:dataSet/a:shortName"/>
			<xsl:text>/</xsl:text>	
			<xsl:value-of select="a:dataSet/a:subsurveyName"/>
			<xsl:text>/</xsl:text>	
			<xsl:value-of select="a:dataSet/a:component"/>
			<xsl:text>/</xsl:text>	
			<xsl:value-of select="a:dataSet/a:instance"/>
			<xsl:text>/</xsl:text>	
			<xsl:value-of select="a:dataSet/a:extractionHost/@type"/>
                    </xsl:attribute>
                    <fileTxt>
                        <fileName>Data File</fileName>
                        <fileCont><xsl:value-of select="a:dataSet/a:longName"/></fileCont>
                    </fileTxt>                 
               </fileDscr>
            </xsl:if>
            <xsl:apply-templates select="a:variables"/>
        </xsl:element>
    </xsl:template>
    <!-- ********************************************************************************************************************************-->
    <!-- ***                        Variables Template: matches variable section of MIF Document                                      ***-->
    <!-- ********************************************************************************************************************************-->
    <xsl:template match="a:variables">
        <dataDscr>
            <xsl:apply-templates select="a:var"/>
        </dataDscr>
    </xsl:template>
    <!-- ********************************************************************************************************************************-->
    <!-- ***                        Variable Template: transforms each MIF Variable to a DDI Variable                                 ***-->
    <!-- ********************************************************************************************************************************-->
    <xsl:template match="a:var">
        <var>
            <xsl:attribute name="name"><xsl:value-of select="@id"/></xsl:attribute>
            <xsl:attribute name="ID"><xsl:value-of select="@id"/></xsl:attribute>
            <xsl:if test="./a:type/@isweight = 'Y'">
                <xsl:attribute name="wgt">wgt</xsl:attribute>
            </xsl:if>
            <xsl:if test="./a:type/@weightvar">
                <xsl:attribute name="wgt-var"><xsl:value-of select="./a:type/@weightvar"/></xsl:attribute>
            </xsl:if>
            <xsl:if test="./a:type/@decimal and ./a:type/@decimal > 0">
                <xsl:attribute name="dcml"><xsl:value-of select="./a:type/@decimal"/></xsl:attribute>
            </xsl:if>
            <xsl:if test="./a:type/@datatype and ./a:type/@decimal">
                <xsl:attribute name="intrvl"><xsl:choose>
                    <xsl:when test="(./a:type/@datatype = 'numeric' or ./a:type/@datatype = 'impliedDecimal') and ./a:type/@decimal > 0 ">contin</xsl:when>
                    <xsl:when test="./a:type/@datatype = 'numeric' and ./a:type/@decimal = 0 and ./a:values">discrete</xsl:when>
                    <xsl:otherwise>contin</xsl:otherwise>
                </xsl:choose></xsl:attribute>
            </xsl:if>
            <location fileid="{$fileDscrId}"/>
            <xsl:apply-templates select="a:label"/>
            <xsl:apply-templates select="a:security"/>
            <xsl:apply-templates select="a:values"/>
            <xsl:apply-templates select="a:universe"/>
            <!--xsl:apply-templates select="a:longDscr"/-->
            <!--xsl:apply-templates select="a:type/a:categories/a:catValu"/-->
            <!--xsl:apply-templates select="a:concept"/-->
            <!-- DDI MAPPING NOTE: codeBook/stdyDescr/var/varFormat, var[-->
	    <varFormat>
	    <xsl:attribute name="type">
               <xsl:choose>
                 <xsl:when test="./a:type[@datatype='floatingPoint']">numeric</xsl:when>
                 <xsl:when test="./a:type[@datatype='numeric']">numeric</xsl:when>
                 <xsl:when test="./a:type[@datatype='impliedDecimal']">numeric</xsl:when>
                 <xsl:otherwise>character</xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
	    <xsl:attribute name="formatname"><xsl:value-of select="./a:type[@datatype]"/></xsl:attribute>
	    <xsl:attribute name="schema">other</xsl:attribute>
	    </varFormat>
            <xsl:apply-templates select="a:period"/>
            <xsl:apply-templates select="a:attachment"/>
            <xsl:apply-templates select="a:synonyms"/>
        </var>
    </xsl:template>
    <xsl:template match="a:synonyms">
        <xsl:for-each select="a:synonym">
            <notes type="mif/variables/var/synonyms/synonym">
                <xsl:value-of select="."/>
            </notes>
        </xsl:for-each>
    </xsl:template>
    <xsl:template match="a:attachment">
        <notes type="mif/variables/var/attachment[{position()}]/@type">
            <xsl:value-of select="@type"/>
        </notes>
        <notes type="mif/variables/var/attachment[{position()}]/@title">
            <xsl:value-of select="@title"/>
        </notes>
        <notes type="mif/variables/var/attachment[{position()}]/@uri">
            <xsl:value-of select="@uri"/>
        </notes>
        <notes type="mif/variables/var/attachment[{position()}]/text()">
            <xsl:value-of select="."/>
        </notes>
    </xsl:template>
    <!-- ********************************************************************************************************************************-->
    <!-- ***                        Long Description Template                                                                                                 ***-->
    <!-- ********************************************************************************************************************************-->
    <xsl:template match="a:longDscr">
        <xsl:if test=". != ''">
        <txt>
            <xsl:value-of select="."/>
        </txt>
        </xsl:if>
    </xsl:template>
    <!-- ********************************************************************************************************************************-->
    <!-- ***                        values Templates                                                                                                             ***-->
    <!-- ********************************************************************************************************************************-->
    <xsl:template match="a:values">
    	<xsl:variable name="iNodes" select="count(a:item)"/>
    	<xsl:variable name="rNodes" select="count(a:range)"/>
    	
    	<xsl:if test="$rNodes > 0 or $iNodes > 0">
    		<valrng>
    			<xsl:if test="$rNodes > 0">
    				<xsl:apply-templates select="a:range" mode="range"/>
    			</xsl:if>
    			<xsl:if test="$iNodes > 0">
    				<xsl:apply-templates select="a:item" mode="items"/>
    			</xsl:if>
    			<xsl:if test="$rNodes > 0">
    				<key><xsl:text>&#xA;</xsl:text>
    					<xsl:apply-templates select="a:range" mode="rkey"/>
    				</key>
    			</xsl:if>

    		</valrng>
    	</xsl:if>
    	<xsl:if test="$iNodes > 0">
    		<xsl:apply-templates select="a:item" mode="categry"/>
    	</xsl:if>
    </xsl:template>
    
    
    <xsl:template match="a:range" mode="range">
            <range>
                <xsl:attribute name="min"><xsl:value-of select="./@min"/></xsl:attribute>
                <xsl:attribute name="max"><xsl:value-of select="./@max"/></xsl:attribute>
                <!-- xsl:value-of select="."/ -->
            </range>
            <!-- notes>
                <xsl:value-of select="normalize-space(.)"/>
            </notes -->
    </xsl:template>
    
    <xsl:template match="a:item" mode="items">
        <item>
                <xsl:attribute name="VALUE"><xsl:value-of select="./@value"/></xsl:attribute>
        </item>
    </xsl:template>


    <xsl:template match="a:range" mode="rkey">
                <xsl:text>(</xsl:text><xsl:value-of select="./@min"/><xsl:text>-</xsl:text><xsl:value-of select="./@max"/><xsl:text>) = (</xsl:text><xsl:value-of select="normalize-space(.)"/><xsl:text>)&#xA;</xsl:text>
    </xsl:template>

    <xsl:template match="a:item" mode="categry">
        <catgry>
            <xsl:if test="./@missing">
                <xsl:attribute name="missing"><xsl:choose><xsl:when test="./@missing = 'Y'">Y</xsl:when><xsl:otherwise>N</xsl:otherwise></xsl:choose></xsl:attribute>
            </xsl:if>
            <catValu>
                <xsl:value-of select="./@value"/>
            </catValu>
            <labl>
            	<xsl:value-of select="normalize-space(.)"/>
            </labl>
            <xsl:if test="./@count">
                <catStat type="freq">
                    <xsl:value-of select="./@count"/>
                </catStat>
            </xsl:if>
        </catgry>
    </xsl:template>
    
    
    <!-- ********************************************************************************************************************************-->
    <!-- ***                       Universe Template                                                                                                              ***-->
    <!-- ********************************************************************************************************************************-->
    <xsl:template match="a:universe">
        <universe>
        	<xsl:if test=". != ''">
            <txt>
                <xsl:value-of select="normalize-space(.)"/>
            </txt>
            </xsl:if>
            <concept>
                <xsl:value-of select="./@type"/>
            </concept>
        </universe>
    </xsl:template>
    <!-- ********************************************************************************************************************************-->
    <!-- ***                       Universe Template                                                                                                              ***-->
    <!-- ********************************************************************************************************************************-->
    <xsl:template match="a:concept">
        <concept>
            <xsl:attribute name="vocab"><xsl:value-of select="./@type"/></xsl:attribute>
            <xsl:value-of select="normalize-space(.)"/>
        </concept>
    </xsl:template>
    <!-- ********************************************************************************************************************************-->
    <!-- ***                       Variable Label Template                                                                                                      ***-->
    <!-- ********************************************************************************************************************************-->
    <xsl:template match="a:label">
        <labl level="variable">
            <xsl:value-of select="normalize-space(.)"/>
        </labl>
    </xsl:template>
    <!-- ********************************************************************************************************************************-->
    <!-- ***                       Security Template                                                                                                               ***-->
    <!-- ********************************************************************************************************************************-->
    <xsl:template match="a:security">
        <security>
            <xsl:attribute name="date"><xsl:value-of select="./@date"/></xsl:attribute>
            <xsl:value-of select="./@level"/>
        </security>
    </xsl:template>
    <!-- ********************************************************************************************************************************-->
    <!-- ***                       Value Range Template                                                                                                        ***-->
    <!-- ********************************************************************************************************************************-->
    <!-- ********************************************************************************************************************************-->
    <!-- ***                       Variable Period Template                                                                                                    ***-->
    <!-- ********************************************************************************************************************************-->
    <xsl:template match="a:period">
        <notes type="mif/variables/var/period/@start">
            <xsl:value-of select="./@start"/>
        </notes>
        <notes type="mif/variables/var/period/@end">
            <xsl:value-of select="./@end"/>
        </notes>
    </xsl:template>

    <xsl:template match="*|@*">
        <xsl:comment><xsl:call-template name="full-path"/><xsl:value-of select="concat('=',.)"/></xsl:comment>
    </xsl:template>
<!-- ********************************************************************************************************************************-->
    <!-- ***                       Title/ handle Template                                                                                                    ***-->
    <!-- ********************************************************************************************************************************-->
    <xsl:template name="titlStmt">
               	   <titlStmt>
                      <titl>                      
                           <xsl:value-of select="normalize-space(a:dataSet/a:longName)"/>
                           <xsl:if test="normalize-space(a:dataSet/a:shortName) != ''">
                    			<xsl:text>(</xsl:text>
								<xsl:value-of select="normalize-space(a:dataSet/a:shortName)"/>
                    			<xsl:text>)</xsl:text>
                    		</xsl:if>
                    		<xsl:if test="normalize-space(a:dataSet/a:subsurveyName) != ''">
                    			<xsl:text>:</xsl:text>
								<xsl:value-of select="normalize-space(a:dataSet/a:subsurveyName)"/>
							</xsl:if>
							<xsl:if test="normalize-space(a:dataSet/a:inheritedComponent) != ''">
                    			<xsl:text>:</xsl:text>
								<xsl:value-of select="normalize-space(a:dataSet/a:inheritedComponent)"/>
							</xsl:if>
							<xsl:if test="normalize-space(a:dataSet/a:component) != ''">	
                    			<xsl:text>:</xsl:text>
 								<xsl:value-of select="normalize-space(a:dataSet/a:component)"/>
 							</xsl:if>
 							<xsl:if test="normalize-space(a:dataSet/a:component) != ''">	
                    			<xsl:text>:</xsl:text>
 								<xsl:value-of select="normalize-space(a:dataSet/a:instance)"/>
 							</xsl:if>
 							
                       </titl>
                       <IDNo agency="producer">
                                <xsl:value-of select="normalize-space(a:dataSet/a:shortName)"/>
                    		<xsl:text>/</xsl:text>
				<xsl:value-of select="normalize-space(a:dataSet/a:subsurveyName)"/>
                    		<xsl:text>/</xsl:text>
				<xsl:value-of select="normalize-space(a:dataSet/a:component)"/>
                    		<xsl:text>/</xsl:text>
				<xsl:value-of select="normalize-space(a:dataSet/a:instance)"/>
                        </IDNo>
                    </titlStmt>
        </xsl:template>

        <xsl:template name="full-path">
            <xsl:for-each select="ancestor-or-self::*">
		<xsl:variable name="id" select="generate-id(.)"/>
		<xsl:variable name="name" select="name()"/>
		<xsl:value-of select="concat('/',name())"/>
		<xsl:for-each select="../*[name()=$name]">
                    <xsl:if test="generate-id(.)=$id">
			<xsl:text>[</xsl:text>
			<xsl:value-of select="position()"/>
			<xsl:text>]</xsl:text>
                    </xsl:if>
		</xsl:for-each>
            </xsl:for-each>
            <xsl:if test="not(self::*)">
                <xsl:choose>
                    <xsl:when test="self::text()">
                    	<xsl:text>/text()</xsl:text>
                    	<xsl:text>[</xsl:text>
							<xsl:value-of select="position()"/>
						<xsl:text>]</xsl:text>
                    </xsl:when>
                    <xsl:when test="self::comment()">      
                        <xsl:text>/comment()</xsl:text>
                        <xsl:text>[</xsl:text>
							<xsl:value-of select="position()"/>
						<xsl:text>]</xsl:text>
                    </xsl:when>
                    <xsl:when test="self::processing-instruction()">   
                       <xsl:text>/processing-instruction()</xsl:text>
                       <xsl:text>[</xsl:text>
							<xsl:value-of select="position()"/>
						<xsl:text>]</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="concat('/@',name())"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
        </xsl:template>

</xsl:stylesheet>
