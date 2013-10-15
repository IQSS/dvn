-- Table: "EJB__TIMER__TBL"

-- DROP TABLE "EJB__TIMER__TBL";

CREATE TABLE "EJB__TIMER__TBL"
(
  "TIMERID" character varying(255) NOT NULL,
  "APPLICATIONID" bigint,
  "BLOB" bytea,
  "CONTAINERID" bigint,
  "CREATIONTIMERAW" bigint,
  "INITIALEXPIRATIONRAW" bigint,
  "INTERVALDURATION" bigint,
  "LASTEXPIRATIONRAW" bigint,
  "OWNERID" character varying(255),
  "PKHASHCODE" integer,
  "SCHEDULE" character varying(255),
  "STATE" integer,
  CONSTRAINT "EJB__TIMER__TBL_pkey" PRIMARY KEY ("TIMERID")
)
WITH (OIDS=FALSE);
ALTER TABLE "EJB__TIMER__TBL" OWNER TO "dvnApp";
