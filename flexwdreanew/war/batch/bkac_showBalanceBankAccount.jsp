<%@page import="com.symgae.shared.GwtUtil"%>
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
	String programTitle = "Revision de Saldo de Cuentas de Banco";
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
	<table border="0" cellspacing="0" width="100%" cellpadding="0"
		style="font-size: 12px">
		<tr>
			<td align="left" width="" rowspan="5" valign="top">
				<img border="0" width="<%=SFParams.LOGO_WIDTH%>" height="<%=SFParams.LOGO_HEIGHT%>" src="<%= sFParams.getMainImageUrl() %>" >
			</td>
			<td colspan="" class="reportTitle">&nbsp;<%= programTitle %>
			</td>
		</tr>
	</table>
	<% 
		PmConn pmConn = new PmConn(sFParams);
		pmConn.open();

		BmUpdateResult bmUpdateResult = new BmUpdateResult();
		PmBankAccount pmBankAccount = new PmBankAccount(sFParams);
		PmConn pmConnSQL = new PmConn(sFParams);
		pmConnSQL.open();

		PmConn pmConnSQL2 = new PmConn(sFParams);
		pmConnSQL2.open();
		String sql = "";
		
		// Cuentas de banco
		pmConnSQL.doFetch("SELECT * FROM " + SQLUtil.formatKind(sFParams, "bankaccounts" + " ORDER BY bkac_bankaccountid ASC;"));
		while (pmConnSQL.next()) {
			double balanceBkac = 0, balanceAmount = 0, sumWithdraw = 0, sumDeposit = 0;

			balanceBkac = pmConnSQL.getDouble("bkac_balance");
			balanceBkac = Double.parseDouble(SFServerUtil.roundCurrencyDecimals(balanceBkac));

			// Obtener Suma de Cargos		
			sql = " SELECT SUM(bkmv_withdraw) AS sumWithdraw "
					+ " FROM " + SQLUtil.formatKind(sFParams, "bankmovements")
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "bankaccounts") + " ON (bkac_bankaccountid = bkmv_bankaccountid) "
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "bankmovtypes") + " ON (bkmt_bankmovtypeid= bkmv_bankmovtypeid) "
					+ " WHERE bkac_bankaccountid = " + pmConnSQL.getInt("bkac_bankaccountid")
					+ " AND  bkmt_type = '" + BmoBankMovType.TYPE_WITHDRAW + "' "
					+ " ORDER BY bkmv_duedate, bkmv_bankmovementid;";

			System.out.println("sql_W:"+sql);
			pmConnSQL2.doFetch(sql);
			if (pmConnSQL2.next()) sumWithdraw = pmConnSQL2.getDouble("sumWithdraw");

			// Obtener Suma de Abonos		
			sql = " SELECT SUM(bkmv_deposit) AS sumDeposit"
					+ " FROM " + SQLUtil.formatKind(sFParams, "bankmovements")
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "bankaccounts") + " ON (bkac_bankaccountid = bkmv_bankaccountid) "
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, "bankmovtypes") + " ON (bkmt_bankmovtypeid= bkmv_bankmovtypeid) "
					+ " WHERE bkac_bankaccountid = " + pmConnSQL.getInt("bkac_bankaccountid")
					+ " AND  bkmt_type = '" + BmoBankMovType.TYPE_DEPOSIT + "' "
					+ " ORDER BY bkmv_duedate, bkmv_bankmovementid;";

			System.out.println("sql_D:"+sql);
			pmConnSQL2.doFetch(sql);
			if (pmConnSQL2.next()) sumDeposit = pmConnSQL2.getDouble("sumDeposit");

			sumDeposit = Double.parseDouble(SFServerUtil.roundCurrencyDecimals(sumDeposit));
			sumWithdraw = Double.parseDouble(SFServerUtil.roundCurrencyDecimals(sumWithdraw));

			balanceAmount = sumDeposit - sumWithdraw;
			balanceAmount = Double.parseDouble(SFServerUtil.roundCurrencyDecimals(balanceAmount));

// 			System.out.println("SALDO SUM: "+balanceAmount);
// 			System.out.println("Saldo CTA: "+balanceBkac);
%>			
			Cuenta de banco: "<b><%= pmConnSQL.getString("bkac_name")%></b>"
			<% 	if (balanceAmount == balanceBkac) {%>
					<img src="<%= GwtUtil.getProperUrl(sFParams, "/icons/boolean_true.png")%>">
			<% 	} else {%>
					<img src="<%= GwtUtil.getProperUrl(sFParams, "/icons/boolean_false.png")%>">
			<%	} %>
			
			<br>
			Balance Cta. Banco: <span><%= SFServerUtil.formatCurrency(balanceBkac) %></span><br>
			Balance Sumatoria MB: <span><%= SFServerUtil.formatCurrency(balanceAmount) %></span>
			<br><br>
<%			
		}
		pmConnSQL.close();
		pmConnSQL2.close();
	%>
	</table>
</body>
</html>
