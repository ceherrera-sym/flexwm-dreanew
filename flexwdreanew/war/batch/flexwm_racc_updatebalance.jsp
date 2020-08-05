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
	String programTitle = "Actualizar Pagos-Saldos de CxC";

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
	<div>
		<% 
		PmConn pmConn = new PmConn(sFParams);
		pmConn.open();
		
		try {
				BmUpdateResult bmUpdateResult = new BmUpdateResult();
				BmoRaccount bmoRacc = new BmoRaccount();
				PmRaccount pmRaccount = new PmRaccount(sFParams);
	
				ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
				BmFilter filterA = new BmFilter();
				BmFilter filterB = new BmFilter();
	
				// Filtros de categoria
				filterA.setValueOperatorFilter(bmoRacc.getKind(), bmoRacc.getIdField(), "" + BmFilter.MAJOREQUAL, idStart);
				filterList.add(filterA);
	
				// Filtros de categoria
				filterB.setValueOperatorFilter(bmoRacc.getKind(), bmoRacc.getIdField(), "" + BmFilter.MINOREQUAL, idEnd);
				filterList.add(filterB);
				Iterator<BmObject> iterator = pmRaccount.list(filterList).iterator();
	
				// Iterator<BmObject> iterator = pmRaccount.list(pmConn).iterator();
				int countRacc = 1;
				while (iterator.hasNext()) {
					BmoRaccount bmoRaccount = (BmoRaccount)iterator.next();
					//pmRaccount.updateBalance(pmConn, bmoRaccount, bmUpdateResult);
					%>
						#<%= countRacc%> : <%= bmoRaccount.getCode().toString() %> <br>
					<%
					countRacc++;
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

	</div>
</body>
</html>
