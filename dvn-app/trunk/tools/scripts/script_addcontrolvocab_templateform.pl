#!/usr/bin/perl

$openflag = 0; 
$counter = 0; 
$noprintflag = 0; 

$select_vocab_column = qq{<ice:column>
                  <ice:inputText id="input_%FIELDNAME%" size="45" value="#{TemplateFormPage.template.metadata.%FIELDNAME%}" 
                                 rendered="#{empty(TemplateFormPage.studyMap[sfc.%FIELDNAME%].templateField.controlledVocabulary)}" styleClass="formHtmlEnabled"/>
                  <ice:message styleClass="errorMessage" for="input_%FIELDNAME%"/>

                  <ice:selectOneMenu immediate="false"   id="selectOne%FIELDNAMEUPPERCASE%" 
                                     rendered="#{!empty(TemplateFormPage.studyMap[sfc.%FIELDNAME%].templateField.controlledVocabulary)}"                                          
                                           value="#{TemplateFormPage.template.metadata.%FIELDNAME%}" 
                                           >
                            <f:selectItem itemLabel="No Value" itemValue=""/>
                            <f:selectItems value="#{TemplateFormPage.studyMap[sfc.%FIELDNAME%].templateField.controlledVocabulary.selectItems}"/>
                 </ice:selectOneMenu>  
                </ice:column>
                <ice:column>
                 <ice:commandLink value="Select Vocabulary"
                                       action="#{TemplateFormPage.openPopupStandard(TemplateFormPage.studyMap[sfc.%FIELDNAME%].templateField)}"/>
                  <ice:selectOneMenu immediate="false" partialSubmit="true" 
                                        value="#{TemplateFormPage.studyMap[sfc.%FIELDNAME%].templateField.fieldInputLevelString}">
                            <f:attribute name="fieldName" value="%FIELDNAME%" />
                           <f:selectItems value="#{TemplateFormPage.fieldInputLevelSelectItems}"/>
                  </ice:selectOneMenu>
                </ice:column>
}; 


$select_vocab_column_textarea = qq{<ice:column>
                  <ice:inputTextarea cols="90" rows="4"
                                     id="input_%FIELDNAME%"
                                     value="#{TemplateFormPage.template.metadata.%FIELDNAME%}"                                     
                                     rendered="#{empty(TemplateFormPage.studyMap[sfc.%FIELDNAME%].templateField.controlledVocabulary)}" styleClass="formHtmlEnabled">
                  </ice:inputTextarea>

                  <ice:message styleClass="errorMessage" for="input_%FIELDNAME%"/>

                  <ice:selectOneMenu immediate="false"   id="selectOne%FIELDNAMEUPPERCASE%" 
                                     rendered="#{!empty(TemplateFormPage.studyMap[sfc.%FIELDNAME%].templateField.controlledVocabulary)}"                                          
                                           value="#{TemplateFormPage.template.metadata.%FIELDNAME%}" 
                                           >
                            <f:selectItem itemLabel="No Value" itemValue=""/>
                            <f:selectItems value="#{TemplateFormPage.studyMap[sfc.%FIELDNAME%].templateField.controlledVocabulary.selectItems}"/>
                 </ice:selectOneMenu>  
                </ice:column>
                <ice:column>
                 <ice:commandLink value="Select Vocabulary"
                                       action="#{TemplateFormPage.openPopupStandard(TemplateFormPage.studyMap[sfc.%FIELDNAME%].templateField)}"/>
                  <ice:selectOneMenu immediate="false" partialSubmit="true" 
                                        value="#{TemplateFormPage.studyMap[sfc.%FIELDNAME%].templateField.fieldInputLevelString}">
                            <f:attribute name="fieldName" value="%FIELDNAME%" />
                           <f:selectItems value="#{TemplateFormPage.fieldInputLevelSelectItems}"/>
                  </ice:selectOneMenu>
                </ice:column>
}; 


%htmltags = (
	"relatedMaterial",1,
	"relatedStudies",1,
	"otherReferences",1,
	"unitOfAnalysis",1,
	"universe",1,
	"kindOfData",1,
	"timeMethod",1,
	"dataCollector",1,
	"frequencyOfDataCollection",1,
	"samplingProcedure",1,
	"deviationsFromSampleDesign",1,
	"collectionMode",1,
	"researchInstrument",1,
	"dataSources",1,
	"originOfSources",1,
	"characteristicOfSources",1,
	"accessToSources",1,
	"dataCollectionSituation",1,
	"actionsToMinimizeLoss",1,
	"controlOperations",1,
	"weighting",1,
	"cleaningOperations",1,
	"studyLevelErrorNotes",1,
	"responseRate",1,
	"samplingErrorEstimates",1,
	"otherDataAppraisal",1,
	"placeOfAccess",1,
	"originalArchive",1,
	"availabilityStatus",1,
	"collectionSize",1,
	"studyCompletion",1,
	"confidentialityDeclaration",1,
	"specialPermissions",1,
	"restrictions",1,
	"contact",1,
	"citationRequirements",1,
	"depositorRequirements",1,
	"conditions",1,
	"disclaimer",1
); 

%textareatags = (
	"unitOfAnalysis", 1,
	"universe", 1,
	"kindOfData", 1,
	"samplingProcedure", 1,
	"deviationsFromSampleDesign", 1,
	"accessToSources", 1,
	"confidentialityDeclaration", 1,
	"specialPermissions", 1,
	"restrictions", 1,
	"citationRequirements", 1,
	"depositorRequirements", 1,
	"conditions", 1,
	"disclaimer", 1,
);

while (<>) 
{
    if ( $openflag == 1 )
    {
	if ($htmlfieldmode == 1) 
	{
	    if ( /<ice:column/ )
	    {
		$columncounter++; 

		if ($columncounter == 2)
		{
		    print STDERR "entering the column mode...\n";
		    $noprintflag = 1; 

		    if ( $textareatags{$field} == 1 )
		    {
			$vocab_entry = $select_vocab_column_textarea;
		    } 
		    else 
		    {
			$vocab_entry = $select_vocab_column; 
		    }
			
		    $vocab_entry =~s/%FIELDNAME%/$field/g;
		    $ufield = $field; 
		    $ufield =~s/^(.)/uc($1)/ge;
		    $vocab_entry =~s/%FIELDNAMEUPPERCASE%/$ufield/g;
		    
		    $vocab_entry =~s/metadata\.samplingErrorEstimates/metadata.samplingErrorEstimate/g; 
			
		    print "\t\t" . $vocab_entry; 
		    $counter++; 
		}

	    }

	    if ($noprintflag == 0)
	    {
		print; 
	    }
	    else 
	    {
		if (/<\/ice:panelGrid/)
		{
		    print; 

		    $htmlfieldmode = 0;
		    $noprintflag = 0; 
		}
	    }

	}
	else 
	{
	    print; 
	}

	if ( /TemplateFormPage.studyMap\[sfc\.([^\]]*)\]/ )
	{
	    $field = $1; 
	    print STDERR $field . "\n";
	    if ($htmltags{$field} == 1)
	    {
		unless ($htmlfieldmode)
		{
		    print STDERR "html mode on\n";
		    $columncounter = 0;
		    $htmlfieldmode = 1; 
		}
	    }
	    else
	    {
		$htmlfieldmode = 0; 
	    }
	}
    }
    else 
    {
	print; 
	if ( /<ice:panelGrid / )
	{
	    $openflag = 1; 
	    $field = ""; 
	}

    }
	    
    if ( /<\/ice:panelGrid/ )
    {
	$openflag = 0; 
    }
}

print STDERR "\n\nReplaced " . $counter . " entries.\n";

