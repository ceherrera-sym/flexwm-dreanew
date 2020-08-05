<%@page import="com.symgae.shared.SQLUtil"%>
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
	String programTitle = "Añadir numeros a folio de las CxC";
	int zeros = 5;
	//Obtener variables url
// 	int idStart =0, idEnd = 0;
// 	if (request.getParameter("idStart") != null) idStart = Integer.parseInt(request.getParameter("idStart"));
// 	if (request.getParameter("idEnd") != null) idEnd = Integer.parseInt(request.getParameter("idEnd"));
%>

<html>
<head>
<title>:::<%= programTitle %>:::
</title>
<link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>">
<meta charset=utf-8>
</head>
<body class="default">
	<table border="0" style="font-size: 12px;width:100%">
		<tr>
			<td align="left" width="" rowspan="5" valign="top">
				<img border="0" width="<%=SFParams.LOGO_WIDTH%>" height="<%=SFParams.LOGO_HEIGHT%>" src="<%= sFParams.getMainImageUrl() %>" >
			</td>
			<td colspan="" class="reportTitle">&nbsp;<%= programTitle %>
			</td>
		</tr>
	</table>
	<table class="report"  border="0" cellspacing="0" style="width:100%" cellpadding="0"
		<tr >
			<th class="reportHeaderCell">
				#
			</th>
			<th class="reportHeaderCell">
				Clave
			</th>
			<th class="reportHeaderCell">
				Serie
			</th>
			<th class="reportHeaderCell">
				Folio
			</th>
			<th class="reportHeaderCell">
				Folio con <%= zeros %> ceros
			</th>
			<th class="reportHeaderCell">
				Factura
			</th>
			<th class="reportHeaderCell">
				Factura compuesta
			</th>
		</tr>
		<% 
		PmConn pmConn = new PmConn(sFParams);
		pmConn.open();
		pmConn.disableAutoCommit();
		
		BmUpdateResult bmUpdateResult = new BmUpdateResult();
		BmoRaccount bmoRaccount = new BmoRaccount();
		PmRaccount pmRaccount = new PmRaccount(sFParams);
		PmConn pmConnSQL = new PmConn(sFParams);
		pmConnSQL.open();

		PmConn pmConnSQL2 = new PmConn(sFParams);
		pmConnSQL2.open();
		String sql = "";
		int i = 1;
		
		// Cuentas de banco
		pmConnSQL.doFetch("SELECT racc_raccountid, racc_code, racc_invoiceno, racc_serie, racc_folio " +
				" FROM " + SQLUtil.formatKind(sFParams, "raccounts") +
				" WHERE racc_folio >= 0");
		while (pmConnSQL.next()) { %>
			<tr class="reportCellEven">
				<td class="reportCellEven">
					<%= i%>
				</td>
				<td class="reportCellEven">
					<%= pmConnSQL.getString("racc_code")%>
				</td>
				<td class="reportCellEven">
					<%= pmConnSQL.getString("racc_serie")%>
				</td>
				<td class="reportCellEven">
					<%= pmConnSQL.getString("racc_folio")%>
				</td>
				<td class="reportCellEven">
					<%= FlexUtil.codeFormatDigits(pmConnSQL.getInt("racc_folio"), zeros, "")%>
				</td>
				<td class="reportCellEven">
					<%= pmConnSQL.getString("racc_invoiceno")%>
				</td>
				<td class="reportCellEven">
					<%= pmConnSQL.getString("racc_serie")%><%= FlexUtil.codeFormatDigits(pmConnSQL.getInt("racc_folio"), zeros, "")%>
				</td>
			</tr>
			<%
			
			bmoRaccount = (BmoRaccount)pmRaccount.get(pmConnSQL.getInt("racc_raccountid"));
			
			bmoRaccount.getFolio().setValue(FlexUtil.codeFormatDigits(pmConnSQL.getInt("racc_folio"), zeros, ""));
			bmoRaccount.getInvoiceno().setValue(bmoRaccount.getSerie().toString() + bmoRaccount.getFolio().toString());
			pmRaccount.saveSimple(pmConn, bmoRaccount, bmUpdateResult);
			
			if (!bmUpdateResult.hasErrors()) {
		 		pmConn.commit();
			}
			
			
			i++;
		}
		pmConnSQL.close();
		pmConnSQL2.close();
	%>
	</table>
</body>
</html>
