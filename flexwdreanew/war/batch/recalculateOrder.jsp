
<%@page import="com.flexwm.server.fi.PmRaccount"%>
<%@page import="com.flexwm.shared.fi.BmoRaccount"%>
<%@page import="com.flexwm.shared.fi.BmoRaccountType"%>
<%@include file="../inc/imports.jsp" %>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.server.op.PmOrder"%>
<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@page import="com.flexwm.server.fi.PmPaccount"%>
<%@page import="com.flexwm.shared.fi.BmoPaccount"%>
<%@page import="com.flexwm.server.fi.PmPaccountType"%>
<%@page import="com.flexwm.shared.fi.BmoPaccountType"%>
<%@page import="com.flexwm.server.fi.PmBankMovement"%>
<%@page import="com.flexwm.shared.fi.BmoBankMovement"%>
<%@page import="com.flexwm.server.fi.PmBankMovType"%>
<%@page import="com.flexwm.shared.fi.BmoBankMovType"%>
<%@page import="com.flexwm.shared.cr.*"%>
<%@page import="com.flexwm.server.cr.*"%>
<%@page import="com.flexwm.server.cm.PmCustomer" %>

<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/login.jsp" %>

<%
	String programTitle = "Proceso de Pedidos";
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

PmConn pmConn4 = new PmConn(sFParams);
pmConn4.open();

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
	
	 BmoOrder bmoOrder = new BmoOrder();
     PmOrder pmOrder = new PmOrder(sFParams);
     
     BmoCredit bmoCredit = new BmoCredit();
     PmCredit pmCredit = new PmCredit(sFParams);
    
     int i = 1;
     
     String where = "";
     if (idStart > 0) {
    	 where += " AND orde_orderid >= " + idStart;
     }
     
     if (idEnd > 0) {
    	 where += " AND orde_orderid <= " + idEnd;
     }
     
     
     sql = "SELECT * FROM orders where orde_orderid > 0 " + where;
	 pmConn2.doFetch(sql);
    	
	 //Calcular los pedidos
     while (pmConn2.next()) {
    	int orderid = pmConn2.getInt("orde_orderid"); 
    	
    	
    	bmoOrder = (BmoOrder)pmOrder.get(pmConn,orderid);    	
    	System.out.println("Pedido " + bmoOrder.getCode().toString() + " Pago " + bmoOrder.getPaymentStatus().getSelectedOption().getLabel());
    	
    	//pmOrder.updatePaymentStatus(pmConn, bmoOrder, bmUpdateResult);
    	
    	bmoOrder = (BmoOrder)pmOrder.get(pmConn,orderid);
    	System.out.println("Pago " + bmoOrder.getPaymentStatus().getSelectedOption().getLabel());
    	if (!bmoOrder.getPaymentStatus().equals(BmoOrder.PAYMENTSTATUS_TOTAL))
    		bmoOrder.getStatus().setValue(BmoOrder.STATUS_AUTHORIZED);
    		pmOrder.saveSimple(pmConn, bmoOrder, bmUpdateResult);
    		
    		bmoCredit = (BmoCredit)pmCredit.getBy(bmoOrder.getId(), bmoCredit.getOrderId().getName());
    		bmoCredit.getStatus().setValue(BmoCredit.STATUS_AUTHORIZED);
    		pmCredit.saveSimple(pmConn, bmoCredit, bmUpdateResult);
    		
    	
     }
	 	 
	 PmRaccount pmRaccount = new PmRaccount(sFParams);
	 BmoRaccount bmoRaccDeposit = new BmoRaccount();
     
	 /*
     //Quitar de los abonos el pedido
     sql = " SELECT * FROM raccounts " +
           " LEFT JOIN raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) " +
    	   " WHERE ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" +
           " AND ract_category = '" + BmoRaccountType.CATEGORY_OTHER + "'" +
    	   " AND racc_orderid is null";
     pmConn2.doFetch(sql);
     while(pmConn2.next()) {
    	 //Obtener sus abonos
    	 sql = " SELECT * FROM raccountassignments " +
    	       " where rass_foreignraccountid = " + pmConn2.getInt("racc_raccountid");
    	 pmConn3.doFetch(sql);
    	 while(pmConn3.next()) {
    		 //Cambiar el abono
    		 sql =  " update raccounts set racc_customerid = " + pmConn2.getInt("racc_customerid") + ", " +
    	            " racc_orderid = null where racc_raccountid = " +  pmConn3.getInt("rass_raccountid");
    		 pmConn.doUpdate(sql);
    		 
    		 //Cambiar el MB
    		 int bankmovementId = 0;
    		 sql = " SELECT * FROM bankmovconcepts " +    		       
    		       " WHERE bkmc_raccountid = " + pmConn2.getInt("racc_raccountid");
    		 pmConn4.doFetch(sql);
    		 if (pmConn4.next()) bankmovementId = pmConn4.getInt("bkmc_bankmovementid");
    		 
    		 sql = " UPDATE bankmovements SET bkmv_customerid = " + pmConn2.getInt("racc_customerid") +
    		       " WHERE bkmv_bankmovementid = " + bankmovementId; 
    		 pmConn.doUpdate(sql);    		     		 
    	 }
     }*/
    
    
 	if (!bmUpdateResult.hasErrors())
 		pmConn.commit();
%>
<%= bmUpdateResult.errorsToString() %> 		
<%
} catch (Exception e) {		
	pmConn.rollback();
	throw new SFException(e.toString());

} finally {
	pmConn4.close();
	pmConn3.close();
	pmConn2.close();
	pmConn.close();
	
	
}


%>

</table>
</body>
</html>


