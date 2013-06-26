
ALTER TABLE studyaccessrequest ADD COLUMN studyfile_id bigint;
ALTER TABLE studyaccessrequest
  ADD CONSTRAINT fk_studyaccessrequest_studyfile_id FOREIGN KEY (studyfile_id)
      REFERENCES studyfile (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;