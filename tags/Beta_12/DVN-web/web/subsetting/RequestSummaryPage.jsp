<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.1" 
xmlns:t="/WEB-INF/tlds/scroller"
xmlns:f="http://java.sun.com/jsf/core" 
xmlns:h="http://java.sun.com/jsf/html" 
xmlns:jsp="http://java.sun.com/JSP/Page" 
xmlns:ui="http://www.sun.com/web/ui"
>

<!-- to change the content type or response encoding change the following line -->
<jsp:directive.page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"/>
<jsp:directive.page import="java.util.*" />
<!-- scriptlet to check request header and parameters -->
<jsp:scriptlet>
<![CDATA[  
  //value=request.getParameter("form1:tabSet1:tabDwnld:dwnldBttn");

  List<String> vec = new ArrayList<String>();
  Enumeration em = request.getHeaderNames();
  while( em.hasMoreElements() ) {
      String name = (String)em.nextElement();
      //String[] values = request.getHeader( name );
      Enumeration emx = request.getHeaders( name );
      while (emx.hasMoreElements()) {
        vec.add( "<tr><td>"+name + "</td><td>" + (String)emx.nextElement() +"</td></tr>");
      }
  }

  Map hm = request.getParameterMap();
  Set es = hm.entrySet();
  String dataFileId = request.getParameter("dtId");
]]></jsp:scriptlet>


<!-- to be renamed to subview -->


<f:subview id="requestSummaryPageView">



<h4>requested parameters</h4> 
<table border="1">
  <jsp:scriptlet>
<![CDATA[  
    for(Iterator itr = es.iterator();itr.hasNext();){
    Map.Entry entry = (Map.Entry)itr.next();
    StringBuffer sb = new StringBuffer();
    sb.append("<tr><td>"+ entry.getKey() + "</td><td>");
    String[] arr = (String[]) entry.getValue();
    for(int i=0;i<arr.length;i++){
      sb.append(arr[i]);
    }
    sb.append("</td></tr>");
    out.println(sb.toString());
    } 
]]>
</jsp:scriptlet>

</table>

<h4>Request Header</h4>
<table border="1">
<jsp:scriptlet>
<![CDATA[  
  
    for( int i = 0; i < vec.size(); i++ ) {
]]>
</jsp:scriptlet>
<jsp:expression>vec.get(i)</jsp:expression>
<jsp:scriptlet>
    }
</jsp:scriptlet>
</table>



<!-- to be renamed to subview -->
</f:subview>

</jsp:root>