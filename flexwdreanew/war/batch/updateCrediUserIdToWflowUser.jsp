
<%@page import="com.flexwm.shared.cr.*"%>
<%@page import="com.flexwm.server.FlexUtil"%>
<%@page import="com.flexwm.shared.op.*"%>
<%@page import="com.flexwm.server.op.*"%>
<%@include file="../inc/imports.jsp" %>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.server.fi.*"%>
<%@page import="com.flexwm.shared.fi.*"%>

<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/login.jsp" %>

<%
	String programTitle = "Proceso de Oportunidades";
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

PmCurrency pmCurrency = new PmCurrency(sFParams);

String status = "";
int idStart = 0, idEnd = 0;
if (request.getParameter("status") != null) status = request.getParameter("status");
if (request.getParameter("idstart") != null) idStart = Integer.parseInt(request.getParameter("idstart"));
if (request.getParameter("idend") != null) idEnd = Integer.parseInt(request.getParameter("idend"));



try {		
	String	sql = "";
	BmoCredit bmoCredit = new BmoCredit();
	BmoOrder bmoOrder = new BmoOrder();
	
	//" WHERE not orde_currencyid is null " +
    //" AND (orde_currencyparity = 0 OR orde_currencyparity is null)";
	sql = " SELECT * FROM credits " +	      
	      " ORDER BY cred_creditid ";
	pmConn.doFetch(sql);
	while (pmConn.next()) {
		int orderId = pmConn.getInt("cred_orderid");
		int wflowId = pmConn.getInt("cred_wflowid");
		int salesmanId = pmConn.getInt("cred_salesuserid");
		
		//Cambiar el usuario al pedido
		/*sql = "UPDATE orders SET orde_userid = " + salesmanId + " WHERE orde_orderid = " + orderId;
		pmConn2.doUpdate(sql);
		//Cambiar las cxc
		sql = "UPDATE raccounts SET racc_userid = " + salesmanId + " WHERE racc_orderid = " + orderId;
		pmConn2.doUpdate(sql);*/
		//Cambiar el usuario de flujo (Promotor)
		sql = "UPDATE wflowusers SET wflu_userid = " + salesmanId + " WHERE wflu_profileid = 49 AND wflu_wflowid = " + wflowId;
		pmConn2.doUpdate(sql);
		
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
