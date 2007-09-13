CREATE TABLE ejb__timer__tbl
(
  creationtimeraw numeric(19) NOT NULL,
  blob bytea,
  timerid varchar(256) NOT NULL,
  containerid numeric(19) NOT NULL,
  ownerid varchar(256),
  state int4 NOT NULL,
  pkhashcode int4 NOT NULL,
  intervalduration numeric(19) NOT NULL,
  initialexpirationraw numeric(19) NOT NULL,
  lastexpirationraw numeric(19) NOT NULL,
  CONSTRAINT pk_ejb__timer__tbl PRIMARY KEY (timerid)
) 
WITHOUT OIDS;
ALTER TABLE ejb__timer__tbl OWNER TO "vdcApp";

