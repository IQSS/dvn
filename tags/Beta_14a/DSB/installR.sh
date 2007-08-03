#!/bin/sh

installRmodules()
{
    export DISPLAY="";
    for RPACK in `cat ../etc/Rmodules.txt`
    do
        LOG="/tmp/${RPACK}.$$";
        echo 'Sys.putenv("DISPLAY"=NULL); install.packages("'${RPACK}'", repos="http://up2date.hmdc.harvard.edu/CRAN",dependencies=T, lib=.libPaths()[length(.libPaths())] )' | R --vanilla --slave >${LOG} 2>&1
        ERRORS=`grep -ci -e '^ERROR' -e "No package \"${RPACK}\" on CRAN" ${LOG}`;
        if [ $ERRORS -ne 0 ]; then
            echo -e "\a*** We were unable to install one of the required R packages (${RPACK}).";
            echo -e "*** This will likely cause problems later.\n\n";
            echo -e "Install output:\n";
            cat ${LOG};
            echo "-----------------------------------------------------------------------";
        fi
#        rm -f ${LOG};
    done

    # post-Zelig installs
    echo '
library(VDCutil)
Sys.putenv("DISPLAY"=NULL)
deps= zeligModelDependencies(inZeligOnly=T, schemaVersion="1.1", uninstalledOnly=T, repos="http://up2date.hmdc.harvard.edu/CRAN")
if (!is.null(deps)){
        for (i in 1:dim(deps)[1]) {
                tmpCran = deps[i,2]
                install.packages(deps[i,1],repos=tmpCran,
                        dependencies=T, lib=.libPaths()[length(.libPaths())] )
        }
}' | R --slave --vanilla 2>&1 | egrep "(Error)|(Warning)"

}


installRmodules;


