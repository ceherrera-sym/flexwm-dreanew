
<%@include file="../inc/imports.jsp" %>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.server.op.PmRequisitionReceipt"%>
<%@page import="com.flexwm.shared.op.BmoRequisitionReceipt"%>
<%@page import="com.flexwm.server.fi.PmPaccount"%>
<%@page import="com.flexwm.shared.fi.BmoPaccount"%>
<%@page import="com.flexwm.server.fi.PmPaccountType"%>
<%@page import="com.flexwm.shared.fi.BmoPaccountType"%>
<%@page import="com.flexwm.server.fi.PmBankMovement"%>
<%@page import="com.flexwm.shared.fi.BmoBankMovement"%>
<%@page import="com.flexwm.server.fi.PmBankMovType"%>
<%@page import="com.flexwm.shared.fi.BmoBankMovType"%>

<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/login.jsp" %>

<%
	String programTitle = "Proceso de Actualización de Presupuestos de CxP";
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
<table bRequisitionReceipt="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr>
		<td align="left" width="" rowspan="5" valign="top">
		    <img bRequisitionReceipt="0" height="" src="<%= sFParams.getMainImageUrl() %>" >
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
int idStart = 0, idEnd = 0, supplierId = 0;
if (request.getParameter("status") != null) status = request.getParameter("status");
if (request.getParameter("idstart") != null) idStart = Integer.parseInt(request.getParameter("idstart"));
if (request.getParameter("idend") != null) idEnd = Integer.parseInt(request.getParameter("idend"));
if (request.getParameter("supplierid") != null) supplierId = Integer.parseInt(request.getParameter("supplierid"));

try {		
	
	 String sql = "", where = "";
	 
	 if (idStart > 0) where += " AND pacc_paccountid >= " + idStart;
	 if (idEnd > 0 ) where += " AND pacc_paccountid <= " + idEnd;
	 if (supplierId > 0 ) where += " AND pacc_supplierid = " + supplierId;
	 
	 pmConn.disableAutoCommit();
	
	 BmoPaccount bmoPaccount = new BmoPaccount();
     PmPaccount pmPaccount = new PmPaccount(sFParams);     
     PmRequisitionReceipt pmRequisitionReceipt = new PmRequisitionReceipt(sFParams);
     BmoRequisitionReceipt bmoRequisitionReceipt = new BmoRequisitionReceipt();
     
     
     int i = 1;
     
     sql = " SELECT distinc(reqi_requisitionid) FROM paccounts " +
           " LEFT JOIN paccounttypes ON (pacc_paccounttypeid = pact_paccounttypeid) " +    	  	 
           " WHERE pact_type = '" + BmoPaccountType.TYPE_WITHDRAW + "'" +
           " AND pacc_requisitionreceiptid > 0 " +
           " AND pacc_budgetitemid is null " + where +
           " ORDER By pacc_requisitionreceiptid ";
            
    pmConn2.doFetch(sql); 
    
    	 
    while (pmConn2.next()) {
    	bmoPaccount = (BmoPaccount)pmPaccount.get(pmConn,pmConn2.getInt("pacc_paccountid"));
    	System.out.println("bmoPaccount " + bmoPaccount.getId());
    	bmoRequisitionReceipt = (BmoRequisitionReceipt)pmRequisitionReceipt.get(pmConn, bmoPaccount.getRequisitionReceiptId().toInteger());
    	
    	int budgetItemId = bmoRequisitionReceipt.getBudgetItemId().toInteger();
    	
    	if (budgetItemId > 0) {
	    	bmoPaccount.getBudgetItemId().setValue(budgetItemId);    	
	    	pmPaccount.saveSimple(pmConn, bmoPaccount, bmUpdateResult);
	    	
	    	pmPaccount.updateBudgetItem(pmConn, bmoRequisitionReceipt, bmUpdateResult);
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


