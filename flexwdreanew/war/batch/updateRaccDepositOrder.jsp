
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
	sql = "select * from raccountassignments " +
		  "left join raccounts on (rass_raccountid = racc_raccountid) " +
		"left join raccounttypes on (racc_raccounttypeid = ract_raccounttypeid) " +
		"where ract_type = 'D' " +
		"and (racc_orderid = 0 or racc_orderid is null) " +
		"and rass_foreignraccountid in ( " +
		"select racc_raccountid from raccounts " +
		"left join raccounttypes on (racc_raccounttypeid = ract_raccounttypeid) " +
		"where racc_orderid > 0 " +
		"and ract_type = 'W'" +
		")";
	pmConn.doFetch(sql);
	while(pmConn.next()) {
		sql = " SELECT racc_orderid FROM raccounts " +
	          " WHERE racc_raccountid = " + pmConn.getInt("rass_foreignraccountid");
		pmConn2.doFetch(sql);
		if (pmConn2.next()) {
			sql = "update raccounts set racc_orderid = " + pmConn2.getInt("racc_orderid") + " where racc_raccountid = " + pmConn.getInt("rass_raccountid");
			pmConn3.doUpdate(sql);
		}
	}
%>
<%= bmUpdateResult.errorsToString() %> 		
<%
} catch (Exception e) {
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
