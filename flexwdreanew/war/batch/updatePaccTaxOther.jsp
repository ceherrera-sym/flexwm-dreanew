
<%@include file="../inc/imports.jsp" %>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.server.fi.*"%>
<%@page import="com.flexwm.shared.fi.*"%>

<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/login.jsp" %>

<%
	String programTitle = "Proceso de Paccounts";
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
	
	 BmoPaccount bmoPaccount = new BmoPaccount();
     PmPaccount pmPaccount = new PmPaccount(sFParams);
	
	 BmoPaccountItem bmoPaccountItem = new BmoPaccountItem();
     PmPaccountItem pmPaccountItem = new PmPaccountItem(sFParams);
     
     int i = 1;
     
     sql = " SELECT * FROM requisitionreceipts WHERE rerc_tax > 0 ";
          
     if (idStart > 0)
    	 sql += " AND rerc_requisitionreceiptid >= " + idStart;
     
     if (idEnd > 0)
    	 sql += " AND rerc_requisitionreceiptid <= " + idEnd;
    
     pmConn2.doFetch(sql);
    	 
     while (pmConn2.next()) {
    	 
    	sql = " SELECT * FROM paccounts " +
    	      " LEFT JOIN requisitionreceipts ON (pacc_requisitionreceiptid = rerc_requisitionreceiptid) " +
    		  " LEFT JOIN paccounttypes ON (pacc_paccounttypeid = pact_paccounttypeid) " +
    		  " WHERE pacc_requisitionreceiptid = " + pmConn2.getInt("rerc_requisitionreceiptid") +
    		  " AND pact_type = '" + BmoPaccountType.TYPE_WITHDRAW + "'" +
    		  " GROUP BY pacc_requisitionreceiptid";
    	pmConn3.doFetch(sql);
    	while (pmConn3.next()) {
    		pmConn.open();
    		    		
    		bmoPaccount = (BmoPaccount)pmPaccount.get(pmConn3.getInt("pacc_paccountid"));
    		//Obtener el Total
    		double total = 0; 
    		double amount = 0;
    		double tax = 0;
    		total = bmoPaccount.getTotal().toDouble();
    		//Obtener el monto
    		amount = total / 1.16;
    		//Obtener Iva
    		tax = amount *.16;
    		
    		bmoPaccount.getAmount().setValue(amount);
    		bmoPaccount.getTax().setValue(tax);
    		bmoPaccount.getTaxApplies().setValue(1);
    		pmPaccount.saveSimple(bmoPaccount, bmUpdateResult);
    		
    		//Modificar el item;
    		
    		bmoPaccountItem = (BmoPaccountItem)pmPaccountItem.getBy(bmoPaccount.getId(), bmoPaccountItem.getPaccountId().getName());
    		bmoPaccountItem.getPrice().setValue(amount);
    		bmoPaccountItem.getAmount().setValue(amount);
    		pmPaccountItem.saveSimple(bmoPaccountItem, bmUpdateResult);
    			
    		    	
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
