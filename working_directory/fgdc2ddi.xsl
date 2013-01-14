<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.icpsr.umich.edu/DDI" xmlns:a="http://www.openarchives.org/OAI/2.0/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" exclude-result-prefixes="a">
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

		    <xsl:if test="normalize-space(a:idinfo/a:citation/a:citeinfo/a:onlink) != ''">
		       <holdings><xsl:attribute name="URI"><xsl:value-of select="normalize-space(a:idinfo/a:citation/a:citeinfo/a:onlink)"/></xsl:attribute>
		       </holdings>
		    </xsl:if>

                </citation>
            </docDscr>

            <stdyDscr>
            	<citation>
        	    <xsl:call-template name="titlStmt"/>

                    <rspStmt>
                     <xsl:for-each select="a:idinfo/a:citation/a:citeinfo/a:origin">
                       <xsl:if test="normalize-space(.) != ''">
                         	<AuthEnty>
                            	 <xsl:value-of select="normalize-space(.)"/>
                        	</AuthEnty>   
		       </xsl:if>
		      </xsl:for-each>
                     </rspStmt>

        	     <xsl:if test="normalize-space(a:idinfo/a:ptcontac/a:cntinfo/a:cntperp/a:cntper) != '' or normalize-space(a:idinfo/a:citation/a:citeinfo/a:pubdate) != '' or normalize-space(a:idinfo/a:ptcontac/a:cntinfo/a:cntorgp/a:cntorg) != '' or normalize-space(a:idinfo/a:citation/a:citeinfo/a:origin) != ''">
                     <prodStmt>
                    	<xsl:if test="normalize-space(a:idinfo/a:ptcontac/a:cntinfo/a:cntorgp/a:cntorg) != ''">
			   <producer>
			      <xsl:value-of select="normalize-space(a:idinfo/a:ptcontac/a:cntinfo/a:cntorgp/a:cntorg)"/>
                           </producer>
                        </xsl:if>
			<xsl:if test="normalize-space(a:dataqual/a:lineage/a:procstep/a:proccont/a:cntinfo/a:cntperp/a:cntper) != ''">
			    <producer>
			       <xsl:value-of select="normalize-space(a:dataqual/a:lineage/a:procstep/a:proccont/a:cntinfo/a:cntperp/a:cntper)"/>
			    </producer>
			</xsl:if>					

                    	<xsl:for-each select="a:idinfo/a:citation/a:citeinfo/a:origin">
			<xsl:if test="normalize-space(.) != ''">
			    <producer>
			       <xsl:value-of select="normalize-space(.)"/>
			    </producer>
                        </xsl:if>
                        </xsl:for-each>


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

                    <xsl:if test="normalize-space(a:idinfo/a:citation/a:citeinfo/a:serinfo/a:sername) != ''">
		    <serStmt>
			<serName>
                        <xsl:value-of select="normalize-space(a:idinfo/a:citation/a:citeinfo/a:serinfo/a:sername)"/>
			</serName>
		    </serStmt>
		    </xsl:if>

		    <xsl:if test="normalize-space(a:idinfo/a:citation/a:citeinfo/a:onlink) != ''">
		       <holdings><xsl:attribute name="URI"><xsl:value-of select="normalize-space(a:idinfo/a:citation/a:citeinfo/a:onlink)"/></xsl:attribute>
		       </holdings>
		    </xsl:if>

               </citation>

               <stdyInfo>
		    <subject>
			<xsl:for-each select="a:idinfo/a:keywords/a:theme">
			    <xsl:variable name="vocab" select="./a:themekt"/>
			    <xsl:for-each select="./a:themekey">
			     <topcClas source="archive">
			     <xsl:attribute name="vocab"><xsl:value-of select="$vocab"/></xsl:attribute>
				   <xsl:value-of select="normalize-space(.)"/>
			     </topcClas>
			    </xsl:for-each>
			</xsl:for-each>
		    </subject>                         

		    <xsl:if test="normalize-space(a:idinfo/a:descript/a:abstract) != ''">
                    <abstract>
						<xsl:value-of select="normalize-space(a:idinfo/a:descript/a:abstract)"/>	
                    </abstract>
		    </xsl:if>

		    <xsl:if test="normalize-space(a:idinfo/a:timeprd/a:timeinfo/a:rngdates) != '' or normalize-space(a:idinfo/a:spdom/a:bounding) != '' or normalize-space(a:idinfo/a:timeprd/a:timeinfo/a:sngdate) != '' or normalize-space(a:idinfo/a:timeprd/a:timeinfo/a:mdattim) != '' or normalize-space(a:idinfo/a:keywords/a:place/a:placekey) != ''">
                    <sumDscr>
			<xsl:if test="normalize-space(a:idinfo/a:timeperd/a:timeinfo/a:sngdate/a:caldate) != ''">
                         <timePrd event="single">
			 <xsl:value-of select="normalize-space(a:idinfo/a:timeperd/a:timeinfo/a:sngdate/a:caldate)"/> 
                         </timePrd>
                        </xsl:if>

			<xsl:for-each select="a:idinfo/a:timeperd/a:timeinfo/a:mdattim/a:sngdate/a:caldate">
			<xsl:if test="normalize-space(.) != ''">
                         <timePrd event="single">
			 <xsl:value-of select="normalize-space(.)"/> 
                         </timePrd>
                        </xsl:if>
			</xsl:for-each>

			<xsl:if test="normalize-space(a:idinfo/a:timeperd/a:timeinfo/a:rngdates/a:begdate) != ''">
                         <timePrd event="start">
			 <xsl:value-of select="normalize-space(a:idinfo/a:timeperd/a:timeinfo/a:rngdates/a:begdate)"/> 
                         </timePrd>
                        </xsl:if>

			<xsl:if test="normalize-space(a:idinfo/a:timeperd/a:timeinfo/a:rngdates/a:enddate) != ''">
                         <timePrd event="end">
			 <xsl:value-of select="normalize-space(a:idinfo/a:timeperd/a:timeinfo/a:rngdates/a:enddate)"/> 
                         </timePrd>
                        </xsl:if>

			<xsl:if test="normalize-space(a:idinfo/a:keywords/a:place/a:placekey) != ''">
                         <geogCover>
				<xsl:value-of select="normalize-space(a:idinfo/a:keywords/a:place/a:placekey)"/>
                         </geogCover>
                        </xsl:if>

			<xsl:if test="normalize-space(a:idinfo/a:spdom/a:bounding) != ''">
                         <geoBndBox>
				<westBL><xsl:value-of select="normalize-space(a:idinfo/a:spdom/a:bounding/a:westbc)"/></westBL>
				<eastBL><xsl:value-of select="normalize-space(a:idinfo/a:spdom/a:bounding/a:eastbc)"/></eastBL>
				<southBL><xsl:value-of select="normalize-space(a:idinfo/a:spdom/a:bounding/a:southbc)"/></southBL>
				<northBL><xsl:value-of select="normalize-space(a:idinfo/a:spdom/a:bounding/a:northbc)"/></northBL>
                         </geoBndBox>
                        </xsl:if>

			<xsl:if test="normalize-space(a:idinfo/a:citation/a:citeinfo/a:geoform) != ''">	
			 <dataKind>
			   <xsl:value-of select="normalize-space(a:idinfo/a:citation/a:citeinfo/a:geoform)"/>
			 </dataKind>
			</xsl:if>
                    </sumDscr>
                    </xsl:if>
                </stdyInfo>

		<xsl:for-each select="a:dataqual/a:lineage/a:procstep/a:procdesc">
		<xsl:if test="normalize-space(.) != ''">
		   <method>
		      <dataColl>
		         <sampProc>
			    <xsl:value-of select="normalize-space(.)"/>
			 </sampProc>
		      </dataColl>
		   </method>
		</xsl:if>
		</xsl:for-each>

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

	    <xsl:if test="normalize-space(a:idinfo/a:citation/a:citeinfo/a:onlink) != ''">
	    <otherMat level="study">
	    <xsl:attribute name="URI"><xsl:value-of select="normalize-space(a:idinfo/a:citation/a:citeinfo/a:onlink)"/></xsl:attribute>
	    <xsl:if test="normalize-space(a:distinfo/a:stdorder/a:digform/a:digtinfo/a:formname) != ''">
	       <labl>
		<xsl:value-of select="normalize-space(a:distinfo/a:stdorder/a:digform/a:digtinfo/a:formname)"/>
	       </labl>
	    </xsl:if>
	    <xsl:if test="normalize-space(a:distinfo/a:stdorder/a:digform/a:digtinfo/a:filedec) != ''">
	       <notes>File Decompression Technique: <xsl:value-of select="normalize-space(a:distinfo/a:stdorder/a:digform/a:digtinfo/a:filedec)"/>
	       </notes>
	    </xsl:if>
	    </otherMat>
	    </xsl:if>

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
