<%@page import="com.flexwm.shared.op.*"%>
<%@page import="com.flexwm.server.op.*"%>
<%@page import="com.flexwm.shared.ac.*"%>
<%@page import="com.flexwm.server.ac.*"%>
<%@page import="com.flexwm.shared.BmoFlexConfig"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.shared.fi.*"%>
<%@page import="com.flexwm.server.fi.*"%>
<%@page import="com.flexwm.server.AmountByWord"%>
<%@page import="com.flexwm.server.AmountByWorden"%>
<%@page import="com.flexwm.server.AmountByWordes"%>
<%@page import="com.flexwm.server.IAmountByWord"%>
<%@page import="com.flexwm.server.NumberByWord"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.flexwm.shared.cm.*"%>
<%@page import="com.flexwm.server.cm.*"%>

<%@page import="com.symgae.server.PmConn"%>
<%@include file="../inc/login_opt.jsp" %>

<html>
<head>
    <title>:::Recibo de Cobro.:::</title>
    <link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>"> 
     <link rel="stylesheet" type="text/css" href="../css/flexwm.css"> 
</head>
<body class="default">
<%
	String sql = "";

	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(Locale.US);
	
	PmConn pmConn = new PmConn(sFParams);
	PmConn pmConn2 = new PmConn(sFParams);
	
	pmConn.open();
	pmConn2.open();
	
	try {
		int raccountId = 0;	
		if (isExternal) raccountId = decryptId;
		else raccountId = Integer.parseInt(request.getParameter("raccountid"));
		
		PmRaccount pmRaccount = new PmRaccount(sFParams);
		BmoRaccount bmoRaccount = (BmoRaccount)pmRaccount.get(raccountId);
		
		PmOrder pmOrder = new PmOrder(sFParams);
		BmoOrder bmoOrder = (BmoOrder)pmOrder.get(bmoRaccount.getOrderId().toInteger());
		
		BmoProject bmoProject = new BmoProject();
		PmProject pmProject = new PmProject(sFParams);
		
	    AmountByWord amountByWord = new AmountByWord();
		amountByWord.setLanguage("es");
		amountByWord.setCurrency("es");
	    
		
		PmUser pmUser = new PmUser(sFParams);
		BmoUser bmoUser = null;
		
		
		PmCompany pmCompany = new PmCompany(sFParams);
		BmoCompany bmoCompany = new BmoCompany();
		if (bmoOrder.getCompanyId().toInteger() > 0)
			bmoCompany = (BmoCompany)pmCompany.get(bmoOrder.getCompanyId().toInteger());
		// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
		String logoURL = "";
		if (!bmoCompany.getLogo().toString().equals(""))
			logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
		else
			logoURL = sFParams.getMainImageUrl();
%>   


<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr>
		<td align="left" width="" rowspan="10" valign="top">
			<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
		</td>
		<td colspan="4" class="reportTitleCell">
			Recibo de Cobro: <%= bmoOrder.getCode().toString() %> 
		</td>
	</tr>     
	<tr>
		<th align = "left" class="reportCellEven">Cliente:</th>
		<td class="reportCellEven">
			<%= bmoOrder.getBmoCustomer().getDisplayName().toString() %>                
		</td>
		<th align = "left" class="reportCellEven">Fecha:</th>
		<td class="reportCellEven">
			<%= SFServerUtil.nowToString(sFParams, "dd/MM/yyyy HH:mm") %>
		</td>
	</tr>
	<tr>
		<th align = "left" class="reportCellEven">Vendedor:</th>
		<td class="reportCellEven">
			<%
				bmoUser = new BmoUser();
				if (bmoOrder.getUserId().toInteger() > 0)
					bmoUser = (BmoUser)pmUser.get(bmoOrder.getUserId().toInteger());
			%>
			<%= bmoUser.getFirstname().toString() %> <%= bmoUser.getFatherlastname().toString() %>                
		</td>
		<th align = "left" class="reportCellEven">Empresa:</th>
		<td class="reportCellEven">
			               
			<%= bmoCompany.getName().toString() %>
		</td>
	</tr>	
</table>
<br>
<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr class="documentSubTitle">
		<td class="reportHeaderCell">Conceptos</td>
		<td class="reportHeaderCellCenter">Cantidad</td>
		<td class="reportHeaderCellRight">Precio</td>
		<td class="reportHeaderCellRight">Total</td>
	</tr>
	<%
		sql = " SELECT * FROM ordersessiontypepackages " +
	          " WHERE orsp_orderid = " + bmoOrder.getId() +
	          " ORDER BY orsp_ordersessiontypepackageid";
		pmConn.doFetch(sql);
		while(pmConn.next()) { %>
			<tr>
				<td class="reportCellEven" align="left">  
					&nbsp;<%= pmConn.getString("ordersessiontypepackages", "orsp_name") %>
				</td>
				<td class="reportCellEven" align="left">  
					&nbsp;<%= pmConn.getString("ordersessiontypepackages", "orsp_name") %>
				</td>
				<td class="reportCellEven" align="center">  
					&nbsp;<%= pmConn.getInt("orsp_quantity") %>
				</td>
				<td class="reportCellEven" align="right">  
					&nbsp;<%= SFServerUtil.formatCurrency(pmConn.getDouble("orsp_price")) %>
				</td>
				<td class="reportCellEven" align="right">  
					&nbsp;<%= SFServerUtil.formatCurrency(pmConn.getDouble("orsp_amount")) %>
				</td>
			</tr>
	<%
		}
	%>
</table>	

<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
	<p class="documentTitleCaption" align="left"><br><br> 
		Fecha de Impresi&oacute;n <%= SFServerUtil.nowToString(sFParams, "dd/MM/yyyy HH:mm") %> Por:
		<%=  sFParams.getLoginInfo().getBmoUser().getFirstname() + " " + sFParams.getLoginInfo().getBmoUser().getFatherlastname().toString() %>
	</p>
</table>	


<%  } catch (Exception e) { 
    String errorLabel = "Error en las Estimaciones";
    String errorText = "Las Estimaciones no pudieron ser desplegadas exitosamente.";
    String errorException = e.toString();
    
    response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);
    } finally {
    	pmConn2.close();
    	pmConn.close();
    }
%>
</body>
</html>
