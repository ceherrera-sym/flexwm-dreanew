
<%@include file="../inc/imports.jsp" %>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.server.op.*"%>
<%@page import="com.flexwm.shared.op.*"%>
<%@page import="com.flexwm.server.fi.*"%>
<%@page import="com.flexwm.shared.fi.*"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/login.jsp" %>

<%
	String programTitle = "Proceso de Pedido a las Relaciones";
	String programDescription = programTitle;
	String populateData = "", action = "";
	
%>

<html>
<head>
<title>:::<%= programTitle %>:::</title>
<link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>">
<link rel="stylesheet" type="text/css" href="../css/flexwm.css">  
<meta charset=utf-8>
</head>
<body class="default">
<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr>
		<td align="left" width="" rowspan="5" valign="top">
		    <img border="0" height="" src="<%= sFParams.getMainImageUrl() %>" >
		</td>
		<td colspan="" class="reportTitle">
		    &nbsp;<%= programTitle %>
		</td>
	</tr>
</table>

<% 

PmConn pmConn = new PmConn(sFParams);
//pmConn.open();

PmConn pmConn2 = new PmConn(sFParams);
pmConn2.open();

BmUpdateResult bmUpdateResult = new BmUpdateResult();
NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);

String status = "";
int idStart = 0, idEnd = 0;
if (request.getParameter("status") != null) status = request.getParameter("status");
if (request.getParameter("idstart") != null) idStart = Integer.parseInt(request.getParameter("idstart"));
if (request.getParameter("idend") != null) idEnd = Integer.parseInt(request.getParameter("idend"));

try {		
	
	 String sql = "";
	 
	
	 BmoRaccountLink bmoRaccountLink = new BmoRaccountLink();
     PmRaccountLink pmRaccountLink = new PmRaccountLink(sFParams);     
     
     PmOrder pmOrder = new PmOrder(sFParams);
	   
     int i = 1;
     
     sql = " SELECT * FROM raccountlinks " + 
           " LEFT JOIN raccounts ON (ralk_foreignid = racc_raccountid) " +
    	   " WHERE ralk_ordercode is null" ;    	   
     pmConn2.doFetch(sql);    	 
     while (pmConn2.next()) {
    	pmConn.open();
    	
    	int orderId = pmConn2.getInt("racc_orderid");
    	
    	if (orderId > 0) {
			BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn, orderId);
			sql = " UPDATE raccountlinks SET ralk_ordercode = '" + bmoOrder.getCode().toString() + "'" +
			      " WHERE ralk_raccountlinkid = " + pmConn2.getInt("ralk_raccountlinkid");
			pmConn.doUpdate(sql);
    	}	
    	
    	pmConn.close();
     }
    
    System.out.println("bmUpdateResult " + bmUpdateResult.errorsToString());
    
 	//if (!bmUpdateResult.hasErrors())
 		//pmConn.commit();
%>
<%= bmUpdateResult.errorsToString() %> 		
<%
} catch (Exception e) {		
	//pmConn.rollback();
	throw new SFException(e.toString());

} finally {
	pmConn.close();
	pmConn2.close();
}
	


%>

</table>
</body>
</html>


