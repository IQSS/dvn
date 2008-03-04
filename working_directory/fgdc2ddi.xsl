<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.icpsr.umich.edu/DDI" xmlns:a="http://www.fgdc.gov/metadata" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" exclude-result-prefixes="a">
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    <xsl:strip-space elements="*"/>
    <xsl:param name="schemaLocation">http://www.icpsr.umich.edu/DDI/Version2-0.xsd</xsl:param>
    <xsl:template match="/">
         <xsl:apply-templates select="a:metadata"/>
    </xsl:template>
    
    <xsl:template match="a:metadata">
	<!-- Nest all in a codebook element -->
        <xsl:element name="codeBook" namespace="http://www.icpsr.umich.edu/DDI">
            <xsl:attribute name="xsi:schemaLocation"><xsl:text>http://www.icpsr.umich.edu/DDI </xsl:text>
		<xsl:value-of select="$schemaLocation"/></xsl:attribute>
            <docDscr>
            	<citation>
        	    <xsl:call-template name="titlStmt"/>
        	    	<xsl:if test="normalize-space(a:idinfo/a:ptcontac/a:cntinfo/a:cntperp/a:cntper) != '' or normalize-space(a:idinfo/a:citation/a:citeinfo/a:pubdate) != '' or normalize-space(a:idinfo/a:ptcontac/a:cntinfo/a:cntorgp/a:cntorg) != '' or normalize-space(a:idinfo/a:citation/a:citeinfo/a:origin) != ''">
                    	<prodStmt>
                    		<xsl:if test="normalize-space(a:idinfo/a:ptcontac/a:cntinfo/a:cntorgp/a:cntorg) != ''">
                         		<producer>
					<xsl:value-of select="normalize-space(a:idinfo/a:ptcontac/a:cntinfo/a:cntorgp/a:cntorg)"/>
					<xsl:if test="normalize-space(a:idinfo/a:citation/a:citeinfo/a:onlink) != ''">
						<ExtLink title="URL">
						<xsl:attribute name="URI"><xsl:value-of select="normalize-space(a:idinfo/a:citation/a:citeinfo/a:onlink)"/></xsl:attribute>
						</ExtLink>
					</xsl:if>
                          		</producer>
                          	</xsl:if>
				<xsl:if test="normalize-space(a:dataqual/a:lineage/a:procstep/a:proccont/a:cntinfo/a:cntperp/a:cntper) != ''">
					<producer>
					<xsl:value-of select="normalize-space(a:dataqual/a:lineage/a:procstep/a:proccont/a:cntinfo/a:cntperp/a:cntper)"/>
					</producer>
				</xsl:if>					

                    		<xsl:if test="normalize-space(a:idinfo/a:citation/a:citeinfo/a:origin) != ''">
                         		<producer>
					<xsl:value-of select="normalize-space(a:idinfo/a:citation/a:citeinfo/a:origin)"/>
					<xsl:if test="normalize-space(a:idinfo/a:citation/a:citeinfo/a:onlink) != ''">
						<ExtLink title="URL">
						<xsl:attribute name="URI"><xsl:value-of select="normalize-space(a:idinfo/a:citation/a:citeinfo/a:onlink)"/></xsl:attribute>
						</ExtLink>
					</xsl:if>
                          		</producer>
                          	</xsl:if>


                         	<xsl:if test="normalize-space(a:idinfo/a:citation/a:citeinfo/a:pubdate) != ''">
                          		<prodDate>
                          			<xsl:attribute name="date"><xsl:value-of select="normalize-space(a:idinfo/a:citation/a:citeinfo/a:pubdate)"/></xsl:attribute>
                          		</prodDate>
                          	</xsl:if>
                          	<xsl:if test="normalize-space(a:idinfo/a:citation/a:citeinfo/a:pubinfo/a:pubplace) != ''">
                          		<prodPlac>
					<xsl:value-of select="normalize-space(a:idinfo/a:citation/a:citeinfo/a:pubinfo/a:pubplace)"/>
                          		</prodPlac>
                          	</xsl:if>
                    	</prodStmt>
                    </xsl:if>

                    <distStmt>
			<distrbtr>
			Harvard Geospatial Library
            		</distrbtr>
		    </distStmt>

                    <holdings URI="http://hul.harvard.edu"/>
                </citation>
            </docDscr>

            <stdyDscr>
            	<citation>
        	    <xsl:call-template name="titlStmt"/>


                     <xsl:if test="normalize-space(a:idinfo/a:crossref/a:citeinfo/a:origin) != ''">
                    	<rspStmt>
                         	<AuthEnty>
                            	 <xsl:value-of select="normalize-space(a:idinfo/a:crossref/a:citeinfo/a:origin)"/>
                        	</AuthEnty>   
                    	</rspStmt>
                    </xsl:if>

                    <xsl:if test="normalize-space(a:idinfo/a:citation/a:citeinfo/a:pubdate) != ''">
                    	<prodStmt>
				<prodDate>
                          	<xsl:attribute name="date"><xsl:value-of select="normalize-space(a:idinfo/a:citation/a:citeinfo/a:pubdate)"/></xsl:attribute>
                          	</prodDate>
                    	</prodStmt>
                    </xsl:if>
		    

                    <distStmt>
                       <distrbtr>
				Harvard Geospatial Library
                       </distrbtr>
                    </distStmt>

                    <xsl:if test="normalize-space(a:idinfo/a:citation/a:citeinfo/a:serinfo/a:sername) != ''">
		    <serStmt>
			<serName>
                        <xsl:value-of select="normalize-space(a:idinfo/a:citation/a:citeinfo/a:serinfo/a:sername)"/>
			</serName>
		    </serStmt>
		    </xsl:if>

               </citation>

               <stdyInfo>
		    <subject>
			<topcClas source="archive" vocab="HarvardGeospatial">HarvardGeospatial</topcClas>
			<xsl:for-each select="a:idinfo/a:keywords/a:theme/a:themekey">
			     <xsl:if test="normalize-space(.) != ''">
			     <topcClas source="archive" vocab="HarvardGeospatial">
				   <xsl:value-of select="normalize-space(.)"/>
			     </topcClas>
		             </xsl:if>
			</xsl:for-each>
		    </subject>                         

		    <xsl:if test="normalize-space(a:idinfo/a:descript/a:abstract) != ''">
                    <abstract>
						<xsl:value-of select="normalize-space(a:idinfo/a:descript/a:abstract)"/>	
                    </abstract>
		    </xsl:if>

		    <xsl:if test="normalize-space(a:dataSet/a:collectDate/@end) != '' or normalize-space(a:dataSet/a:collectDate/@start) != '' or  normalize-space(a:dataSet/a:category) != '' ">               
                    <sumDscr>
			<xsl:if test="normalize-space(a:idinfo/a:keywords/a:temporal/a:tempkey) != ''">

                         <collDate>
			 <xsl:value-of select="normalize-space(a:idinfo/a:keywords/a:temporal/a:tempkey)"/> 
                         </collDate>
                        </xsl:if>

                    </sumDscr>
                    </xsl:if>
                </stdyInfo>
		<xsl:if test="normalize-space(a:idinfo/a:citation/a:citeinfo/a:geoform) != '' or normalize-space(a:dataqual/a:lineage/a:procstep/a:procdesc) != ''">		<method>
			<dataColl>
				<xsl:if test="normalize-space(a:dataqual/a:lineage/a:procstep/a:procdesc) != ''">
				<sampProc>
				<xsl:value-of select="normalize-space(a:dataqual/a:lineage/a:procstep/a:procdesc)"/>
				</sampProc>
				</xsl:if>
				<xsl:if test="normalize-space(a:idinfo/a:citation/a:citeinfo/a:geoform) != ''">
				<collMode>
				<xsl:value-of select="normalize-space(a:idinfo/a:citation/a:citeinfo/a:geoform)"/>
				</collMode>
				</xsl:if>
			</dataColl>
		</method>
		</xsl:if>

		<xsl:if test="normalize-space(a:idinfo/a:accconst) != '' or normalize-space(a:idinfo/a:useconst) != ''" >
		<dataAccs>
		<useStmt>
		<specPerm>
			<xsl:value-of select="normalize-space(a:idinfo/a:accconst)"/>
		</specPerm>
		<restrctn>
			<xsl:value-of select="normalize-space(a:idinfo/a:useconst)"/>
		</restrctn>
		</useStmt>     	
		</dataAccs>
		</xsl:if>


            </stdyDscr>

	</xsl:element>
    </xsl:template>

    <xsl:template name="titlStmt">
               	   <titlStmt>
                      <titl>                      
                           <xsl:value-of select="normalize-space(a:idinfo/a:citation/a:citeinfo/a:title)"/>
                       </titl>

                    </titlStmt>
        </xsl:template>

</xsl:stylesheet>
