#!/bin/sh
java -jar /tmp/schemaSpy_5.0.0.jar -t pgsql -host localhost -db dvnDb -u dvnApp -p secret -dp tools/installer/dvninstall/pgdriver/postgresql-8.4-703.jdbc4.jar -o /tmp/out -s public
