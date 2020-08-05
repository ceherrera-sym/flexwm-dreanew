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
		int bankMovConceptId = 0;	
		if (isExternal) bankMovConceptId = decryptId;
		else bankMovConceptId = Integer.parseInt(request.getParameter("foreignid"));		
		double payment = Double.parseDouble(request.getParameter("payment"));
		
		PmBankMovConcept pmBankMovConcept = new PmBankMovConcept(sFParams);
		BmoBankMovConcept bmoBankMovConcept = (BmoBankMovConcept)pmBankMovConcept.get(bankMovConceptId);
		
		PmOrder pmOrder = new PmOrder(sFParams);
		BmoOrder bmoOrder = (BmoOrder)pmOrder.get(bmoBankMovConcept.getBmoRaccount().getOrderId().toInteger());
		
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
		String logoURL ="";
		if (!bmoCompany.getLogo().toString().equals(""))
			logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
		else 
			logoURL = sFParams.getMainImageUrl();
 %>   


<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr>
		<td align="left" width="" rowspan="4" valign="top">
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
			<%
				
				
			%>                
			<%= bmoCompany.getName().toString() %>
		</td>		
	</tr>
	<tr>
		<th align = "left" class="reportCellEven">Cuenta Banco:</th>
		<td class="reportCellEven" colspan="4">
			<%
				//Obtener Cuenta de Banco
				String bankAccountName = "";
				sql = " SELECT * FROM bankmovconcepts " + 
			          " LEFT JOIN bankmovements ON (bkmc_bankmovementid = bkmv_bankmovementid) " +
					  " LEFT JOIN bankaccounts ON (bkmv_bankaccountid = bkac_bankaccountid) " +
			          " WHERE bkmc_bankmovconceptid = " + bankMovConceptId;					   
				pmConn.doFetch(sql);
				if (pmConn.next()) {
					bankAccountName = pmConn.getString("bkac_name");
				}
			%>
			<%= bankAccountName %>
		</td>
	<tr>	
	
	<tr class="reportTitleCell">
		<td class="reportHeaderCell" colspan="5">Conceptos del Pedido</td>		
	</tr>
	<%
		sql = " SELECT * FROM ordersessiontypepackages " +
	          " LEFT JOIN sessiontypepackages ON (orsp_sessiontypepackageid = setp_sessiontypepackageid) " +
	          " WHERE orsp_orderid = " + bmoOrder.getId() +
	          " ORDER BY orsp_ordersessiontypepackageid";
		pmConn.doFetch(sql);
		while(pmConn.next()) { %>
			<tr>
				<td class="reportCellEven" align="left" colspan="2">  
					&nbsp;<%= pmConn.getString("ordersessiontypepackages", "orsp_name") %>
				</td>
				<td class="reportCellEven" align="left" colspan="3">  
					&nbsp;<%= pmConn.getString("sessiontypepackages", "setp_description") %>
				</td>
			</tr>
	<%
		}
	%>
	<%
 	 sql = "";
	 double subTotal = 0, iva = 0;
	 double subTotalGeneral = 0;
     BmoOrderGroup bmoOrderGroup = new BmoOrderGroup();
     PmOrderGroup pmOrderGroup = new PmOrderGroup(sFParams);
     BmFilter bmFilter = new BmFilter();
     bmFilter.setValueFilter(bmoOrderGroup.getKind(), bmoOrderGroup.getOrderId().getName(), bmoOrder.getId());
     Iterator<BmObject> quoteGroups = pmOrderGroup.list(bmFilter).iterator();
     int i = 1;
     while (quoteGroups.hasNext()) {
	   	  bmoOrderGroup = (BmoOrderGroup)quoteGroups.next();


   BmoOrderItem bmoOrderItem = new BmoOrderItem();
   PmOrderItem pmOrderItem = new PmOrderItem(sFParams);
   BmFilter bmFilterO = new BmFilter();
   bmFilterO.setValueFilter(bmoOrderItem.getKind(), bmoOrderItem.getOrderGroupId().getName(), bmoOrderGroup.getId());
   ArrayList list = pmOrderItem.list(bmFilterO);
   Iterator<BmObject> items = list.iterator();
   int index = 1, count = list.size();
   while(items.hasNext()) {
 	  bmoOrderItem = (BmoOrderItem)items.next(); %>
       <tr>	   	                      
        <td class="reportCellEven" align ="left" colspan="5">
        	<%= bmoOrderItem.getName().toHtml() %>
        	<%= bmoOrderItem.getBmoProduct().getModel().toHtml() %>
        	<br>
        	<span class="documentSubText" >
        		<%= bmoOrderItem.getDescription().toHtml() %>
        	</span>
        </td>
       

	<% } %>	
<% } %>
<tr>
 	<td colspan="4">			
 		&nbsp;
 	</td>
</tr>
<tr>		
		<th class="reportCellEven" colspan="3" align="left">Recib&iacute; la Cantidad</th>
		<td class="reportCellEven" align="right">
		<b>(<b><%= amountByWord.getMoneyAmountByWord(payment).toUpperCase() %></b>)</b>
		</td>
		<td class="reportCellEven" align="right" colspan="">
			<%= SFServerUtil.formatCurrency(payment) %>
		</td>
	</tr>	
</table>
<br>


<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
	<p class="documentTitleCaption" align="left"><br><br> 
		Fecha de Impresi&oacute;n <%= SFServerUtil.nowToString(sFParams, "dd/MM/yyyy HH:mm") %> Por:
		<%=  sFParams.getLoginInfo().getBmoUser().getFirstname() + " " + sFParams.getLoginInfo().getBmoUser().getFatherlastname().toString() %>
	</p>
</table>	


<%  } catch (Exception e) { 
    String errorLabel = "Error en el Recibo";
    String errorText = "El Recibo no pudo ser desplegado exitosamente.";
    String errorException = e.toString();
    
    response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException + "/" +e.toString());
    } finally {
    	pmConn2.close();
    	pmConn.close();
    }
%>
</body>
</html>
