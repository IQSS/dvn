<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.icpsr.umich.edu/DDI" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    <xsl:strip-space elements="*"/>
    <xsl:param name="schemaLocation">http://www.icpsr.umich.edu/DDI/Version2-0.xsd</xsl:param>
    <xsl:template match="/">
         <xsl:apply-templates select="metadata"/>
    </xsl:template>
    
    <xsl:template match="metadata">
	<!-- Nest all in a codebook element -->
        <xsl:element name="codeBook" namespace="http://www.icpsr.umich.edu/DDI">
            <xsl:attribute name="xsi:schemaLocation"><xsl:text>http://www.icpsr.umich.edu/DDI </xsl:text>
		<xsl:value-of select="$schemaLocation"/></xsl:attribute>
            <docDscr>
            	<citation>
        	    <xsl:call-template name="titlStmt"/>
        	    	<xsl:if test="normalize-space(idinfo/ptcontac/cntinfo/cntperp/cntper) != '' or normalize-space(idinfo/citation/citeinfo/pubdate) != '' or normalize-space(idinfo/ptcontac/cntinfo/cntorgp/cntorg) != '' or normalize-space(idinfo/citation/citeinfo/origin) != ''">
                    	<prodStmt>
                    		<xsl:if test="normalize-space(idinfo/ptcontac/cntinfo/cntorgp/cntorg) != ''">
                         		<producer>
					<xsl:value-of select="normalize-space(idinfo/ptcontac/cntinfo/cntorgp/cntorg)"/>
                          		</producer>
                          	</xsl:if>
				<xsl:if test="normalize-space(dataqual/lineage/procstep/proccont/cntinfo/cntperp/cntper) != ''">
					<producer>
					<xsl:value-of select="normalize-space(dataqual/lineage/procstep/proccont/cntinfo/cntperp/cntper)"/>
					</producer>
				</xsl:if>					

                    		<xsl:for-each select="idinfo/citation/citeinfo/origin">
				<xsl:if test="normalize-space(.) != ''">
                         		<producer>
					<xsl:value-of select="normalize-space(.)"/>
                          		</producer>
                          	</xsl:if>
                          	</xsl:for-each>


                         	<xsl:if test="normalize-space(idinfo/citation/citeinfo/pubdate) != ''">
                          		<prodDate>
                          			<xsl:attribute name="date"><xsl:value-of select="normalize-space(idinfo/citation/citeinfo/pubdate)"/></xsl:attribute>
                          		</prodDate>
                          	</xsl:if>
                          	<xsl:if test="normalize-space(idinfo/citation/citeinfo/pubinfo/pubplace) != ''">
                          		<prodPlac>
					<xsl:value-of select="normalize-space(idinfo/citation/citeinfo/pubinfo/pubplace)"/>
                          		</prodPlac>
                          	</xsl:if>
                    	</prodStmt>
                    </xsl:if>

                    <distStmt>
			<distrbtr>
			Harvard Geospatial Library
            		</distrbtr>
		    </distStmt>

		    <xsl:if test="normalize-space(idinfo/citation/citeinfo/onlink) != ''">
		       <holdings>
			  <xsl:value-of select="normalize-space(idinfo/citation/citeinfo/onlink)"/>
		       </holdings>
		    </xsl:if>

                </citation>
            </docDscr>

            <stdyDscr>
            	<citation>
        	    <xsl:call-template name="titlStmt"/>


                    <rspStmt>
                     <xsl:for-each select="idinfo/citation/citeinfo/origin">
                       <xsl:if test="normalize-space(.) != ''">
                         	<AuthEnty>
                            	 <xsl:value-of select="normalize-space(.)"/>
                        	</AuthEnty>   
		       </xsl:if>
		      </xsl:for-each>
                     </rspStmt>

                    <xsl:if test="normalize-space(idinfo/citation/citeinfo/pubdate) != ''">
                    	<prodStmt>
				<prodDate>
                          	<xsl:attribute name="date"><xsl:value-of select="normalize-space(idinfo/citation/citeinfo/pubdate)"/></xsl:attribute>
                          	</prodDate>
                    	</prodStmt>
                    </xsl:if>
		    

                    <distStmt>
                       <distrbtr>
				Harvard Geospatial Library
                       </distrbtr>
                    </distStmt>

                    <xsl:if test="normalize-space(idinfo/citation/citeinfo/serinfo/sername) != ''">
		    <serStmt>
			<serName>
                        <xsl:value-of select="normalize-space(idinfo/citation/citeinfo/serinfo/sername)"/>
			</serName>
		    </serStmt>
		    </xsl:if>

               </citation>

               <stdyInfo>
		    <subject>
			<xsl:for-each select="idinfo/keywords/theme">
			    <xsl:param name="vocab"><xsl:value-of select="./themekt"/></xsl:param>
			    <xsl:for-each select="./themekey">
			     <topcClas source="archive">
			     <xsl:attribute name="vocab"><xsl:value-of select="$vocab"/></xsl:attribute>
				   <xsl:value-of select="normalize-space(.)"/>
			     </topcClas>
			    </xsl:for-each>
			</xsl:for-each>
		    </subject>                         

		    <xsl:if test="normalize-space(idinfo/descript/abstract) != ''">
                    <abstract>
						<xsl:value-of select="normalize-space(idinfo/descript/abstract)"/>	
                    </abstract>
		    </xsl:if>

		    <xsl:if test="normalize-space(idinfo/timeprd/timeinfo/rngdates) != '' or normalize-space(idinfo/spdom/bounding) != '' or normalize-space(idinfo/timeprd/timeinfo/sngdate) != '' or normalize-space(idinfo/timeprd/timeinfo/mdattim) != '' or normalize-space(idinfo/keywords/place/placekey) != ''">
                    <sumDscr>
			<xsl:if test="normalize-space(idinfo/timeperd/timeinfo/sngdate/caldate) != ''">
                         <timePrd event="single">
			 <xsl:value-of select="normalize-space(idinfo/timeperd/timeinfo/sngdate/caldate)"/> 
                         </timePrd>
                        </xsl:if>

			<xsl:for-each select="idinfo/timeperd/timeinfo/mdattim/sngdate/caldate">
			<xsl:if test="normalize-space(.) != ''">
                         <timePrd event="single">
			 <xsl:value-of select="normalize-space(.)"/> 
                         </timePrd>
                        </xsl:if>
			</xsl:for-each>

			<xsl:if test="normalize-space(idinfo/timeperd/timeinfo/rngdates/begdate) != ''">
                         <timePrd event="start">
			 <xsl:value-of select="normalize-space(idinfo/timeperd/timeinfo/rngdates/begdate)"/> 
                         </timePrd>
                        </xsl:if>

			<xsl:if test="normalize-space(idinfo/timeperd/timeinfo/rngdates/enddate) != ''">
                         <timePrd event="end">
			 <xsl:value-of select="normalize-space(idinfo/timeperd/timeinfo/rngdates/enddate)"/> 
                         </timePrd>
                        </xsl:if>

			<xsl:if test="normalize-space(idinfo/keywords/place/placekey) != ''">
                         <geogCover>
				<xsl:value-of select="normalize-space(idinfo/keywords/place/placekey)"/>
                         </geogCover>
                        </xsl:if>

			<xsl:if test="normalize-space(idinfo/spdom/bounding) != ''">
                         <geoBndBox>
				<westBL><xsl:value-of select="normalize-space(idinfo/spdom/bounding/westbc)"/></westBL>
				<eastBL><xsl:value-of select="normalize-space(idinfo/spdom/bounding/eastbc)"/></eastBL>
				<southBL><xsl:value-of select="normalize-space(idinfo/spdom/bounding/southbc)"/></southBL>
				<northBL><xsl:value-of select="normalize-space(idinfo/spdom/bounding/northbc)"/></northBL>
                         </geoBndBox>
                        </xsl:if>

			<xsl:if test="normalize-space(idinfo/citation/citeinfo/geoform) != ''">	
			 <dataKind>
			   <xsl:value-of select="normalize-space(idinfo/citation/citeinfo/geoform)"/>
			 </dataKind>
			</xsl:if>
                    </sumDscr>
                    </xsl:if>
                </stdyInfo>

		<xsl:if test="normalize-space(dataqual/lineage/procstep/procdesc) != ''">
		   <method>
		      <dataColl>
		         <sampProc>
			    <xsl:value-of select="normalize-space(dataqual/lineage/procstep/procdesc)"/>
			 </sampProc>
		      </dataColl>
		   </method>
		</xsl:if>

		<xsl:if test="normalize-space(idinfo/accconst) != '' or normalize-space(idinfo/useconst) != ''" >
		<dataAccs>
		<useStmt>
		<specPerm>
			<xsl:value-of select="normalize-space(idinfo/accconst)"/>
		</specPerm>
		<restrctn>
			<xsl:value-of select="normalize-space(idinfo/useconst)"/>
		</restrctn>
		</useStmt>     	
		</dataAccs>
		</xsl:if>


            </stdyDscr>

	    <xsl:if test="normalize-space(idinfo/citation/citeinfo/onlink) != ''">
	    <otherMat level="study">
	    <xsl:attribute name="URI"><xsl:value-of select="normalize-space(idinfo/citation/citeinfo/onlink)"/></xsl:attribute>
	    <xsl:if test="normalize-space(idinfo/citation/citeinfo/geoform) != ''">
	       <labl>
		<xsl:value-of select="idinfo/citation/citeinfo/geoform"/>
	       </labl>
	    </xsl:if>
	    <xsl:if test="normalize-space(distinfo/stdorder/digform/digtinfo/formname) != ''">
	       <labl>
		<xsl:value-of select="normalize-space(distinfo/stdorder/digform/digtinfo/formname)"/>
	       </labl>
	    </xsl:if>
	    </otherMat>
	    </xsl:if>

	</xsl:element>
    </xsl:template>

    <xsl:template name="titlStmt">
               	   <titlStmt>
                      <titl>                      
                           <xsl:value-of select="normalize-space(idinfo/citation/citeinfo/title)"/>
                       </titl>

                    </titlStmt>
        </xsl:template>

</xsl:stylesheet>
