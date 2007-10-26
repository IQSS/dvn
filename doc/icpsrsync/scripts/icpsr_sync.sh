#!/bin/sh

PATH=/usr/local/vdc-admin/icpsrsync/bin:$PATH; export PATH

DATA_SCRATCH=/usr2/VDC/icpsrsync
REP_SERVER=vdc.hmdc.harvard.edu
SYNC_DATE=`date +%d-%b-%Y | tr '[a-z]' '[A-Z]'`

mkdir -p $DATA_SCRATCH/$SYNC_DATE

(cd $DATA_SCRATCH/$SYNC_DATE; 
#    for file in ddi_sec4.mlt.pl mlt11.u3.pl parsesps.u8.pl mergedcml.xsl mergesumStateach.2.xsl Rcode_tab.2.xsl .Rprofile; do ln -s ../$file; done
    for file in DDIsec4sps.pl; do ln -s /usr/local/vdc-admin/icpsrsync/bin/$file; done
    icpsr_sync.pl `cat ../LAST` `pwd` $REP_SERVER > MAIN.LOG 2>&1;

    a=`grep 'is valid' VALIDATE.MERGED.OUT | wc -l | sed 's/^[^0-9]*//g'`;
    
    if [ $a != 0 ];
	then \
	    (echo "The following $a ICPSR studies have been successfully processed:";
	    ls -d [0-9][0-9][0-9][0-9][0-9];
	    b=`ls -d [0-9][0-9][0-9][0-9][0-9].INVALID 2>/dev/null | wc -l | sed 's/^[^0-9]*//g'`;
	    if [ $b != 0 ];
		then \
		    echo; \
		    echo "ATTENTION!"; \
		    echo; \
		    echo "Processing failed on $b ICPSR studies."; \
		    echo "please consult the processing logs in "`pwd`; \
		    echo "and the following subdirectories:"; \
		    ls -d [0-9][0-9][0-9][0-9][0-9].INVALID;
	    fi) | mail -s "ICPSR syncer" vdc-dev@latte.harvard.edu; 
	    echo $SYNC_DATE > ../LAST;
    else
	cd ..; \
	    /bin/rm -rf $DATA_SCRATCH/$SYNC_DATE;
    fi
)


    
