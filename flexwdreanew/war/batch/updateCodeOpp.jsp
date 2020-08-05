
<%@page import="com.flexwm.shared.cm.BmoOpportunity"%>
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
	BmoOpportunity bmoOpportunity = new BmoOpportunity();
	BmoOrder bmoOrder = new BmoOrder();
	
	//" WHERE not orde_currencyid is null " +
    //" AND (orde_currencyparity = 0 OR orde_currencyparity is null)";
	sql = " SELECT * FROM opportunities " +	      
	      " ORDER BY oppo_opportunityid ";
	pmConn.doFetch(sql);
	while (pmConn.next()) {
		String codeFormat = FlexUtil.codeFormatDigits(pmConn.getInt("oppo_opportunityid"), 4, bmoOpportunity.CODE_PREFIX);
		
		sql = " UPDATE opportunities SET oppo_code = '" + codeFormat + "'" +			    
			  " WHERE oppo_opportunityid = " + pmConn.getInt("oppo_opportunityid");
		pmConn2.doUpdate(sql);
	}
	
	sql = " SELECT * FROM orders " +	      
	      " ORDER BY orde_orderid ";
	pmConn.doFetch(sql);
	while (pmConn.next()) {
		String codeFormat = FlexUtil.codeFormatDigits(pmConn.getInt("orde_orderid"), 4, bmoOrder.CODE_PREFIX);
		
		sql = " UPDATE orders SET orde_code = '" + codeFormat + "'" +			    
			  " WHERE orde_orderid = " + pmConn.getInt("orde_orderid");
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
