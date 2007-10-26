#!/bin/bash

if [ "x"$1 = "x" ]
then
    echo "usage: script_producemeta_ddi.sh <HANDLESPACE>"
    exit 1
fi

perl -ne 'chop; $id=sprintf ( "%05d", $_ ); printf ( "hdl:'$1'/$id text/xml ddi vdcClass=PUBLIC_OBJ,o=vdc '$1'/$id/study.xml\n" );'     

