ALTER TABLE vdc ALTER COLUMN defaulttemplate_id DROP NOT NULL;


CREATE INDEX filemetadata_studyversion_id_index ON filemetadata (studyversion_id);