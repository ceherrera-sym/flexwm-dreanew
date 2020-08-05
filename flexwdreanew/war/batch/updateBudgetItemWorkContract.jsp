
<%@include file="../inc/imports.jsp" %>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.server.co.PmWorkContract"%>
<%@page import="com.flexwm.shared.co.BmoWorkContract"%>
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
	
	 BmoWorkContract bmoWorkContract = new BmoWorkContract();
     PmWorkContract pmWorkContract = new PmWorkContract(sFParams);     
     /*BmFilter bmFilter = new BmFilter();
     if (status.equals("A")) {
    	 //bmFilter.setValueFilter(bmoWorkContract.getKind(), bmoWorkContract.getStatus().getName(), "" + BmoWorkContract.STATUS_AUTHORIZED);
     } else {
    	 bmFilter.setValueFilter(bmoWorkContract.getKind(), bmoWorkContract.getStatus().getName(), "" + BmoWorkContract.STATUS_REVISION);
     }
     
     Iterator<BmObject> WorkContractList = pmWorkContract.list(bmFilter).iterator();*/
     int i = 1;
     
     sql = "SELECT * FROM workcontracts" ;
           
    
    System.out.println("sql " + sql); 
    pmConn2.doFetch(sql);
    	 
     while (pmConn2.next()) {
    	int workContractid = pmConn2.getInt("woco_workcontractid");
    	System.out.println("workContractid " + workContractid);
    	bmoWorkContract = (BmoWorkContract)pmWorkContract.get(pmConn,workContractid);    	
    	
 		pmWorkContract.updateBudgetItem(pmConn, bmoWorkContract, bmUpdateResult);
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


