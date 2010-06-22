VDCWebSearch<-function(search="",host="vdc.hmdc.harvard.edu")  {
  VDCSearchPath = "/VDC/View/index.jsp?op_query="
  VDCSearchUrl= paste( "http://", host, VDCSearchPath,
      URLencode(search,reserved=T), sep="")
  
   browseURL(VDCSearchUrl)
}
