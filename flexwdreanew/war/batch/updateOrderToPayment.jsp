
<%@include file="../inc/imports.jsp" %>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.server.fi.*"%>
<%@page import="com.flexwm.shared.fi.*"%>

<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/login.jsp" %>

<%
	String programTitle = "Proceso de Raccounts";
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
	
	 BmoRaccount bmoRaccount = new BmoRaccount();
     PmRaccount pmRaccount = new PmRaccount(sFParams);
	
	 BmoRaccountItem bmoRaccountItem = new BmoRaccountItem();
     PmRaccountItem pmRaccountItem = new PmRaccountItem(sFParams);
     
     int i = 1;
     
     sql = " SELECT * FROM orders WHERE orde_tax > 0 ";
          
     if (idStart > 0)
    	 sql += " AND orde_orderid >= " + idStart;
     
     if (idEnd > 0)
    	 sql += " AND orde_orderid <= " + idEnd;
    
    pmConn2.doFetch(sql);
    	 
     while (pmConn2.next()) {
    	 
    	sql = " SELECT * FROM raccounts " +
    		  " LEFT JOIN raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) " +
    		  " WHERE racc_orderid = " + pmConn2.getInt("orde_orderid") +
    		  " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" +
    		  " GROUP BY racc_orderid";
    	pmConn3.doFetch(sql);
    	while (pmConn3.next()) {
    		pmConn.open();
    		    		
    		bmoRaccount = (BmoRaccount)pmRaccount.get(pmConn3.getInt("racc_raccountid"));
    		//Obtener el Total
    		double total = 0; 
    		double amount = 0;
    		double tax = 0;
    		total = bmoRaccount.getTotal().toDouble();
    		//Obtener el monto
    		amount = total / 1.16;
    		//Obtener Iva
    		tax = amount *.16;
    		
    		bmoRaccount.getAmount().setValue(amount);
    		bmoRaccount.getTax().setValue(tax);
    		bmoRaccount.getTaxApplies().setValue(1);
    		pmRaccount.saveSimple(bmoRaccount, bmUpdateResult);
    		
    		//Modificar el item;
    		
    		bmoRaccountItem = (BmoRaccountItem)pmRaccountItem.getBy(bmoRaccount.getId(), bmoRaccountItem.getRaccountId().getName());
    		bmoRaccountItem.getPrice().setValue(amount);
    		bmoRaccountItem.getAmount().setValue(amount);
    		pmRaccountItem.saveSimple(bmoRaccountItem, bmUpdateResult);
    			
    		    	
    		pmConn.close();
    	}
    	
 			
 		
 		
 		
 		
     }
     
 	
%>
<%= bmUpdateResult.errorsToString() %> 		
<%
} catch (Exception e) {
	throw new SFException(e.toString());

} finally {
	pmConn3.close();
	pmConn2.close();
	pmConn.close();
	
	
}
	


%>

</table>
</body>
</html>
