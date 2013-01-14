INSERT INTO visualizationdisplay (
             showdatatable, defaultdisplay, showimagegraph, sourceinfolabel, 
            measuretypelabel, showflashgraph, datatable_id)
	SELECT true, 0, true, 'Source Info', 
            'Measure', true, id  FROM datatable
	where visualizationenabled = true