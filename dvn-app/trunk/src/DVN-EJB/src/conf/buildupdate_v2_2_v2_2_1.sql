
-- this table is no longer needed - version is stored in a property file
drop table dvnversion;

-- Add fields to datatable to support visualization source url

Alter Table dataTable
add visualizationUrlIncludeVariableNames boolean;

update dataTable
set visualizationUrlIncludeVariableNames = false;

Alter Table dataTable
add visualizationUrl character varying(255);