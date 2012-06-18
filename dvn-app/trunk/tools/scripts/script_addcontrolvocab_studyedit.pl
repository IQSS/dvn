#!/usr/bin/perl

$openflag = 0; 
$replacement_counter = 0; 
$noprintflag = 0; 
$panelidcounter = 62; 



$new_vocab_div = qq{
                    <div jsfc="ice:panelGroup" id="groupPanel%PANELID%">
                      <ice:inputText id="input_%FIELDNAME%" size="45" value="#{EditStudyPage.metadata.%FIELDNAME%}" 
                                     required="#{EditStudyPage.studyMap[sfc.%FIELDNAME%].required and EditStudyPage.validateRequired}" 
                                     rendered="#{empty(EditStudyPage.studyMap[sfc.%FIELDNAME%].templateField.controlledVocabulary)}"
                                     requiredMessage="This field is required."
                                     styleClass="formHtmlEnabled">
                        <f:validator validatorId="XhtmlValidator"/> 
                      </ice:inputText> 
                      <ice:message styleClass="errorMessage" for="input_%FIELDNAME%"/>                      
                      <ice:selectOneMenu immediate="false" id="%FIELDNAME%_SelectOne"
                                          rendered="#{!empty(EditStudyPage.studyMap[sfc.%FIELDNAME%].templateField.controlledVocabulary)}"
                                          required="#{EditStudyPage.studyMap[sfc.%FIELDNAME%].required and EditStudyPage.validateRequired}" 
                                           value="#{EditStudyPage.metadata.%FIELDNAME%}"  
                                           requiredMessage="This field is required." >
                                <f:selectItem itemLabel="No Value" itemValue=""/>
                                <f:selectItems value="#{EditStudyPage.studyMap[sfc.%FIELDNAME%].templateField.controlledVocabulary.selectItems}"/>
                       </ice:selectOneMenu>
                    </div>
};

$new_vocab_div_textarea = qq{
                    <div jsfc="ice:panelGroup" id="groupPanel%PANELID%">
                      <ice:inputTextarea id="input_%FIELDNAME%" 
                                     cols="90" 
                                     rows="2" 
                                     rendered="#{empty(EditStudyPage.studyMap[sfc.%FIELDNAME%].templateField.controlledVocabulary)}"
                                     value="#{EditStudyPage.metadata.unitOfAnalysis}" 
                                     required="#{EditStudyPage.studyMap[sfc.unitOfAnalysis].required and EditStudyPage.validateRequired}" 
                                     requiredMessage="This field is required." 
                                     styleClass="formHtmlEnabled">
                        <f:validator validatorId="XhtmlValidator"/> 
                      </ice:inputTextarea> 
                      <ice:message styleClass="errorMessage" for="input_%FIELDNAME%"/>                      
                      <ice:selectOneMenu immediate="false" id="%FIELDNAME%_SelectOne"
                                          rendered="#{!empty(EditStudyPage.studyMap[sfc.%FIELDNAME%].templateField.controlledVocabulary)}"
                                          required="#{EditStudyPage.studyMap[sfc.%FIELDNAME%].required and EditStudyPage.validateRequired}" 
                                           value="#{EditStudyPage.metadata.%FIELDNAME%}"  
                                           requiredMessage="This field is required." >
                                <f:selectItem itemLabel="No Value" itemValue=""/>
                                <f:selectItems value="#{EditStudyPage.studyMap[sfc.%FIELDNAME%].templateField.controlledVocabulary.selectItems}"/>
                       </ice:selectOneMenu>
                    </div>
};



%htmltags = (
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
	    if ( /<div/ )
	    {
		$divcounter++; 

		if ($divcounter == 2)
		{
		    print STDERR "entering the div mode...\n";
		    $noprintflag = 1; 

                    if ( $textareatags{$field} == 1 )
                    {
                        $vocab_entry = $new_vocab_div_textarea;
                    }
                    else
                    {
                        $vocab_entry = $new_vocab_div;
                    }

			
		    $vocab_entry =~s/%FIELDNAME%/$field/g;
		    $ufield = $field; 
		    $ufield =~s/^(.)/uc($1)/ge;
		    $vocab_entry =~s/%FIELDNAMEUPPERCASE%/$ufield/g;
		    
		    $vocab_entry =~s/metadata\.samplingErrorEstimates/metadata.samplingErrorEstimate/g; 

		    $vocab_entry =~s/%PANELID%/$panelidcounter/g; 
		    $panelidcounter++; 
			
		    print "\t\t" . $vocab_entry; 

		    $replacement_counter++; 
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

	if ( /EditStudyPage.studyMap\[sfc\.([^\]]*)\]/ )
	{
	    $field = $1; 
	    print STDERR $field . "\n";
	    if ($htmltags{$field} == 1)
	    {
		unless ($htmlfieldmode)
		{
		    print STDERR "html mode on\n";
		    $divcounter = 0;
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


print STDERR "\n\nReplaced " . $replacement_counter . " entries.\n";
