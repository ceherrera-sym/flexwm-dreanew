<%@include file="../inc/imports.jsp"%>
<%@page import="com.flexwm.server.op.*"%>
<%@page import="com.flexwm.shared.op.*"%>
<%@page import="com.flexwm.server.fi.*"%>
<%@page import="com.flexwm.shared.fi.*"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.server.cr.*"%>
<%@page import="com.flexwm.shared.cr.*"%>
<%@page import="com.flexwm.server.*"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/login.jsp"%>

<%
	String programTitle = "Actualizar Montos de Créditos";
//Obtener variables url
	int idStart =0, idEnd = 0;
	if (request.getParameter("idStart") != null) idStart = Integer.parseInt(request.getParameter("idStart"));
	if (request.getParameter("idEnd") != null) idEnd = Integer.parseInt(request.getParameter("idEnd"));
%>

<html>
<head>
<title>:::<%= programTitle %>:::
</title>
<link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>">
<meta charset=utf-8>
</head>
<body class="default">
	<table border="0" cellspacing="0" width="100%" cellpadding="0"
		style="font-size: 12px">
		<tr>
			<td align="left" width="" rowspan="5" valign="top"><img
				border="0" height="" src="<%= sFParams.getMainImageUrl() %>">
			</td>
			<td colspan="" class="reportTitle">&nbsp;<%= programTitle %>
			</td>
		</tr>
	</table>

	<% 

	PmConn pmConn = new PmConn(sFParams);
	pmConn.open();
	
	try {
			BmUpdateResult bmUpdateResult = new BmUpdateResult();
			BmoLoan bmoLoanFilter = new BmoLoan();
			PmLoan pmLoan = new PmLoan(sFParams);
			
			ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
			BmFilter filterA = new BmFilter();
			BmFilter filterB = new BmFilter();

			// Filtros de id inicio
			filterA.setValueOperatorFilter(bmoLoanFilter.getKind(), bmoLoanFilter.getIdField(), "" + BmFilter.MAJOREQUAL, idStart);
			filterList.add(filterA);

			// Filtros de fin
			filterB.setValueOperatorFilter(bmoLoanFilter.getKind(), bmoLoanFilter.getIdField(), "" + BmFilter.MINOREQUAL, idEnd);
			filterList.add(filterB);
			Iterator<BmObject> iterator = pmLoan.list(filterList).iterator();
			
			
			//Iterator<BmObject> iterator = pmOrder.list(pmConn).iterator();
			while (iterator.hasNext()) {
				BmoLoan bmoLoan = (BmoLoan)iterator.next();
				pmLoan.updateAmount(pmConn, bmoLoan, bmUpdateResult);
				pmLoan.updateAmountPayment(pmConn, bmoLoan, bmUpdateResult);
				%>
					Credito: <%= bmoLoan.getCode().toString() %> <%= bmoLoan.getName().toString() %> <br>
				<% 
			}
		%>
		
		<%= bmUpdateResult.errorsToString() %>
		<%
		} catch (Exception e) {
			%>
				Error: <%= e.toString() %>
			<% 
		} finally {
			pmConn.close();	
		}
	%>

	</table>
</body>
</html>
