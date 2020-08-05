
<%@include file="../inc/imports.jsp" %>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.server.op.PmProductCompany"%>
<%@page import="com.flexwm.shared.op.BmoProductCompany"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/login.jsp" %>

<%
	String programTitle = "Proceso de actualizacion de empresa en producto";
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
pmConn.open();

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
	 pmConn.disableAutoCommit();
	
	      
     /*BmFilter bmFilter = new BmFilter();
     if (status.equals("A")) {
    	 //bmFilter.setValueFilter(bmoProduct.getKind(), bmoProduct.getStatus().getName(), "" + BmoProduct.STATUS_AUTHORIZED);
     } else {
    	 bmFilter.setValueFilter(bmoProduct.getKind(), bmoProduct.getStatus().getName(), "" + BmoProduct.STATUS_REVISION);
     }
     
     Iterator<BmObject> ProductList = pmProduct.list(bmFilter).iterator();*/
     int i = 1;
     
     PmProductCompany pmProductCompany = new PmProductCompany(sFParams);
     
     sql = "SELECT * FROM products ";            
     pmConn2.doFetch(sql);
     
     int ProductId = 0;
    	 
     while (pmConn2.next()) {
    	ProductId = pmConn2.getInt("prod_productid");
    	System.out.println("ProductId " + ProductId);
    	
    	BmoProductCompany bmoProductCompany = new BmoProductCompany();
    	
    	bmoProductCompany.getProductId().setValue(ProductId);    	
    	bmoProductCompany.getCompanyId().setValue(2);
    	
    	pmProductCompany.save(pmConn, bmoProductCompany, bmUpdateResult);    	
 		
     }
     
 	if (!bmUpdateResult.hasErrors())
 		pmConn.commit();
%>
<%= bmUpdateResult.errorsToString() %> 		
<%
} catch (Exception e) {		
	pmConn.rollback();
	throw new SFException(e.toString());

} finally {
	pmConn.close();
	pmConn2.close();
}
	


%>

</table>
</body>
</html>


