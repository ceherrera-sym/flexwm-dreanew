
<%@include file="../inc/imports.jsp" %>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.server.op.PmSupplierCompany"%>
<%@page import="com.flexwm.shared.op.BmoSupplierCompany"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/login.jsp" %>

<%
	String programTitle = "Proceso de actualizacion de empresa en proveedores";
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

PmConn pmConn3 = new PmConn(sFParams);
pmConn3.open();

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

     int i = 1;
     
     PmSupplierCompany pmSupplierCompany = new PmSupplierCompany(sFParams);
     
     sql = "SELECT * FROM suppliers ";            
     pmConn2.doFetch(sql);
     
     int supplierId = 0;
    	 
     while (pmConn2.next()) {
    	 supplierId = pmConn2.getInt("supl_supplierid");
    	 //System.out.println("supplierId " + supplierId);

    	 BmoSupplierCompany bmoSupplierCompany = new BmoSupplierCompany();
    	 
    	 bmoSupplierCompany.getSupplierId().setValue(supplierId);    	
    	 bmoSupplierCompany.getCompanyId().setValue(1);
    	 
    	 pmSupplierCompany.save(pmConn, bmoSupplierCompany, bmUpdateResult);
 		
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
	pmConn3.close();
}
	


%>

</table>
</body>
</html>


