
<%@include file="../inc/imports.jsp" %>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.server.op.PmRequisition"%>
<%@page import="com.flexwm.shared.op.BmoRequisition"%>
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
	String programTitle = "Proceso de actualizacion de partidas en OC";
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
	
	 BmoRequisition bmoRequisition = new BmoRequisition();
     PmRequisition pmRequisition = new PmRequisition(sFParams);     
     /*BmFilter bmFilter = new BmFilter();
     if (status.equals("A")) {
    	 //bmFilter.setValueFilter(bmoRequisition.getKind(), bmoRequisition.getStatus().getName(), "" + BmoRequisition.STATUS_AUTHORIZED);
     } else {
    	 bmFilter.setValueFilter(bmoRequisition.getKind(), bmoRequisition.getStatus().getName(), "" + BmoRequisition.STATUS_REVISION);
     }
     
     Iterator<BmObject> requisitionList = pmRequisition.list(bmFilter).iterator();*/
     int i = 1;
     
     /*sql = "SELECT * FROM requisitions " +
           " LEFT JOIN budgetitems ON (reqi_budgetitemid = bgit_budgetitemid) " +
           " LEFT JOIN budgetitemtypes ON (bgit_budgetitemtypeid = bgty_budgetitemtypeid) " +
           " WHERE reqi_status = '" + BmoRequisition.STATUS_AUTHORIZED + "'"*/
           
           //" AND bgit_budgetid = 3 ";
     
     /*sql = " SELECT reqi_requisitionid, pacc_paccountid, pacc_budgetitemid FROM paccounts " +  
		   " LEFT JOIN paccounttypes ON (pacc_paccounttypeid = pact_paccounttypeid) " +
		   " LEFT JOIN requisitionreceipts ON (pacc_requisitionreceiptid = rerc_requisitionreceiptid) " + 	 
		   " LEFT JOIN requisitions ON (rerc_requisitionid = reqi_requisitionid) " +
		   " WHERE pact_type = '" + BmoPaccountType.TYPE_WITHDRAW + "'" +
		   " AND pacc_requisitionreceiptid > 0 " +
		   " AND pacc_budgetitemid is null ";*/
		   
	/* sql = " SELECT * FROM requisitionreceipts " +
		   " LEFT JOIN requisitions ON (rerc_requisitionid = reqi_requisitionid) " +
		   " WHERE rerc_budgetitemid is null ";*/
	 
     /*if (idStart > 0)
    	 sql += " AND reqi_requisitionid >= " + idStart;
     
     if (idEnd > 0)
    	 sql += " AND reqi_requisitionid <= " + idEnd;*/
     
     //sql += " GROUP BY reqi_requisitionid ";
    
    System.out.println("sql " + sql);
    pmConn2.doFetch(sql);
    	 
     while (pmConn2.next()) {
    	int requisitionid = pmConn2.getInt("reqi_requisitionid"); %>
    	
    	<%= requisitionid %><br>
   <% 	bmoRequisition = (BmoRequisition)pmRequisition.get(pmConn,requisitionid);    	
    	
 		pmRequisition.updateBudgetItem(pmConn, bmoRequisition, bmUpdateResult);
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


