INSERT INTO harvestformattype (id, metadataprefix, name, stylesheetfilename) VALUES (4, 'dcmi_terms', 'DCMI_terms', 'dcmi_terms2ddi.xsl');
ALTER TABLE variablecategory ADD COLUMN catorder INTEGER;
ALTER TABLE datavariable ADD COLUMN orderedfactor BOOLEAN;

ALTER TABLE studyfile ADD COLUMN md5 character varying (255);
