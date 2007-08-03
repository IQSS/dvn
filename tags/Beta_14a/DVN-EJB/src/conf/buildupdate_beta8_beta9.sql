
CREATE SEQUENCE studylock_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE studylock_id_seq OWNER TO "vdcApp";

ALTER TABLE studylock ALTER COLUMN id SET DEFAULT nextval('studylock_id_seq'::regclass);
