
<%@include file="../inc/imports.jsp" %>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.server.op.PmOrder"%>
<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@page import="com.flexwm.server.fi.PmRaccount"%>
<%@page import="com.flexwm.shared.fi.BmoRaccount"%>
<%@page import="com.flexwm.server.fi.PmRaccountType"%>
<%@page import="com.flexwm.shared.fi.BmoRaccountType"%>
<%@page import="com.flexwm.server.fi.PmBankMovement"%>
<%@page import="com.flexwm.shared.fi.BmoBankMovement"%>
<%@page import="com.flexwm.server.fi.PmBankMovType"%>
<%@page import="com.flexwm.shared.fi.BmoBankMovType"%>

<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/login.jsp" %>

<%
	String programTitle = "Proceso de OC Saldos";
	String programDescription = "Proceso de OC Saldos";
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
	
	 String sql = "", where = "";
	 
	 if (idStart > 0) where += " AND racc_raccountid >= " + idStart;
	 if (idEnd > 0 ) where += " AND racc_raccountid <= " + idEnd;
	 
	 pmConn.disableAutoCommit();
	
	 BmoRaccount bmoRaccount = new BmoRaccount();
     PmRaccount pmRaccount = new PmRaccount(sFParams);     
     PmOrder pmOrder = new PmOrder(sFParams);
     BmoOrder bmoOrder = new BmoOrder();
     
     
     int i = 1;
     
     sql = " SELECT * FROM raccounts " +
           " LEFT JOIN raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) " +    		 
           " WHERE ract_type = '" + BmoRaccountType.TYPE_DEPOSIT + "'" +
           " AND racc_orderid > 0 " + where +  
           " ORDER BY racc_raccountid"; 
    pmConn2.doFetch(sql); 
    System.out.println("sql " + sql);
    	 
    while (pmConn2.next()) {
    	bmoRaccount = (BmoRaccount)pmRaccount.get(pmConn,pmConn2.getInt("racc_raccountid"));
    	System.out.println("bmoRaccount " + bmoRaccount.getId());
    	bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoRaccount.getOrderId().toInteger());
    	
    	int budgetItemId = pmOrder.getBudgetItemByOrder(pmConn, bmoOrder, bmUpdateResult);
    	
    	if (budgetItemId > 0) {
	    	bmoRaccount.getBudgetItemId().setValue(budgetItemId);    	
	    	pmRaccount.saveSimple(pmConn, bmoRaccount, bmUpdateResult);
    	}	
    	
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


