
<%@include file="../inc/imports.jsp" %>
<%@page import="com.flexwm.shared.fi.BmoBudgetItem"%>
<%@page import="com.flexwm.server.fi.PmBudgetItem"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/login.jsp" %>

<%
	String errorLabel = request.getParameter("errorLabel");
	String errorText = request.getParameter("errorText");
	String errorException = request.getParameter("errorException");
	String programTitle = "Calculo de Presupuestos";
	String programDescription = "Calculo de Presupuestos";	
	
%>

<html>
<head>
<title>:::Calculo de Presupuestos:::</title>
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

try { 

	BmUpdateResult bmUpdateResult = new BmUpdateResult();
	PmBudgetItem pmBudgetItem = new PmBudgetItem(sFParams);
	BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();
	
	pmConn.disableAutoCommit();
	
	String sql = "";
	sql = " SELECT distinct(reqi_budgetitemid) FROM requisitions " +
	      " WHERE reqi_budgetitemid > 0 "; 
	pmConn.doFetch(sql);
	while(pmConn.next()) {
		bmoBudgetItem  = (BmoBudgetItem)pmBudgetItem.get(pmConn, pmConn.getInt("reqi_budgetitemid"));
		
		pmBudgetItem.updateBalance(pmConn, bmoBudgetItem, bmUpdateResult);
	}
	
	pmConn.commit();
%>

<% 
} catch (Exception e) {
	pmConn.rollback();
	errorLabel = "Error ";
	errorText = "Error";
	errorException = e.toString();
	
	response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);

%>

<%
} finally {
	pmConn.close();
}
%>
</body>
</html>


