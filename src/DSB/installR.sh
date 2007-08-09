#!/bin/sh

installRmodules()
{
    export DISPLAY=""

    LOG="/tmp/RINSTALL.$$.LOG";
    for RPACK in `cat ../etc/Rmodules.txt`
    do
        echo 'install.packages("'${RPACK}'", repos="http://up2date.hmdc.harvard.edu/CRAN",dependencies=T, lib=.libPaths()[length(.libPaths())] )' | R --vanilla --slave 2>&1 | tee -a ${LOG}
    done

    # post-Zelig installs

    echo '
library(VDCutil)
deps= zeligModelDependencies(inZeligOnly=T, schemaVersion="1.1", uninstalledOnly=T, repos="http://up2date.hmdc.harvard.edu/CRAN")
if (!is.null(deps)){
        for (i in 1:dim(deps)[1]) {
                tmpCran = deps[i,2]
                install.packages(deps[i,1],repos=tmpCran,
                        dependencies=T, lib=.libPaths()[length(.libPaths())] )
        }
}' | R --slave --vanilla 2>&1 | tee -a ${LOG}


}


installRmodules;
