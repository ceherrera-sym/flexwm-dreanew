
<%@page import="com.flexwm.server.fi.*"%>
<%@page import="com.flexwm.shared.fi.*"%>
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
<%@page import="com.flexwm.server.cm.PmCustomer" %>

<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/login.jsp" %>

<%
	String programTitle = "Proceso de Relacionar las CxC Penalizacion";
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
    
     int i = 1;
      	 
     PmRaccountLink pmRaccountLink = new PmRaccountLink(sFParams);
	 BmoRaccountLink bmoRaccountLink = new BmoRaccountLink();
     
     //Quitar de los abonos el pedido
     sql = " SELECT * FROM raccounts " +
           " LEFT JOIN raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) " +
    	   " WHERE ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" +           
    	   " AND racc_failure = 1";
     pmConn2.doFetch(sql);
     while(pmConn2.next()) {
    	 int failureId = pmConn2.getInt("racc_raccountid");
    	 //Obtener sus fallas
    	 sql = " SELECT * FROM raccounts " +
    	       " where racc_relatedraccountid = " + failureId;
    	 pmConn3.doFetch(sql);
    	 while(pmConn3.next()) {
    		int linkedId = pmConn3.getInt("racc_raccountid"); 
    		bmoRaccountLink = new BmoRaccountLink();
 			bmoRaccountLink.getRaccountId().setValue(failureId);
 			bmoRaccountLink.getForeignId().setValue(linkedId);
 			
 			pmRaccountLink.save(pmConn, bmoRaccountLink, bmUpdateResult);
    				     		 
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
	pmConn4.close();
	pmConn3.close();
	pmConn2.close();
	pmConn.close();
	
	
}


%>

</table>
</body>
</html>


