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
    <title>:::Estado de Cuenta:::</title>
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
		int orderId = 0;	
		if (isExternal) orderId = decryptId;
		else orderId = Integer.parseInt(request.getParameter("foreignid"));		
		
		PmOrder pmOrder = new PmOrder(sFParams);
		BmoOrder bmoOrder = (BmoOrder)pmOrder.get(orderId);
		
		//Empresa
		BmoCompany bmoCompany = new BmoCompany();
		PmCompany  pmCompany  = new PmCompany (sFParams);
		bmoCompany = (BmoCompany)pmCompany.get(bmoOrder.getCompanyId().toInteger());
		
		// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
		String logoURL = "";
		if (!bmoCompany.getLogo().toString().equals(""))
			logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
		else
			logoURL = sFParams.getMainImageUrl();
		
		BmoProject bmoProject = new BmoProject();
		PmProject pmProject = new PmProject(sFParams);
		
	    AmountByWord amountByWord = new AmountByWord();
		amountByWord.setLanguage("es");
		amountByWord.setCurrency("es");
	    
		
		PmUser pmUser = new PmUser(sFParams);
		BmoUser bmoUser = null;
 %>   


<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr>
		<td align="left" width="" rowspan="3" valign="top">
			<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
		</td>
		<td colspan="5" class="reportTitleCell">
			Pedido: <%= bmoOrder.getCode().toString() %> 
		</td>
	</tr>     
	<tr>
		<th align = "left" class="reportCellEven">Cliente:</th>
		<td class="reportCellEven">
			<%= bmoOrder.getBmoCustomer().getDisplayName().toString() %>                
		</td>
		<th align = "left" class="reportCellEven">Fecha:</th>
		<td class="reportCellEven" colspan="2">
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
		<td class="reportCellEven" colspan="2">
			<%
				
						
					// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
					logoURL = "";
					if (!bmoCompany.getLogo().toString().equals(""))
						logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
					else
						logoURL = sFParams.getMainImageUrl();

					if (bmoOrder.getCompanyId().toInteger() > 0)
						bmoCompany = (BmoCompany) pmCompany.get(bmoOrder.getCompanyId().toInteger());
			%>                
			<%= bmoCompany.getName().toString() %>
		</td>		
	</tr>
	<tr class="reportTitleCell">
		<td class="reportHeaderCell" colspan="6">Conceptos del Pedido</td>		
	</tr>
	<%
		sql = " SELECT * FROM ordersessiontypepackages " +
	          " LEFT JOIN sessiontypepackages ON (orsp_sessiontypepackageid = setp_sessiontypepackageid) " +
	          " WHERE orsp_orderid = " + bmoOrder.getId() +
	          " ORDER BY orsp_ordersessiontypepackageid";
		pmConn.doFetch(sql);
		while(pmConn.next()) { %>
			<tr>
				<td class="reportCellEven" align="center" colspan="">  
					&nbsp;<%= pmConn.getInt("orsp_quantity") %>
				</td>
				<td class="reportCellEven" align="left" colspan="">  
					&nbsp;<%= pmConn.getString("ordersessiontypepackages", "orsp_name") %>
				</td>
				<td class="reportCellEven" align="left" colspan="2">  
					&nbsp;<%= pmConn.getString("sessiontypepackages", "setp_description") %>
				</td>
				<td class="reportCellEven" align="right" colspan="2">  
					&nbsp;<%= formatCurrency.format(pmConn.getDouble("setp_saleprice")) %>
				</td>
			</tr>
	<%
		}
	%>	
	<tr class="reportTitleCell">
		<td class="reportHeaderCell" colspan="6">Items</td>		
	</tr>
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
			       	<td class="reportCellEven" align ="center" colspan="">
			        	<%= bmoOrderItem.getQuantity().toHtml() %>
			        </td>  	                      
			        <td class="reportCellEven" align ="left" colspan="4">
			        	<%= bmoOrderItem.getName().toHtml() %>
			        	<%= bmoOrderItem.getBmoProduct().getModel().toHtml() %>
			        	<br>
			        	<span class="documentSubText" >
			        		<%= bmoOrderItem.getDescription().toHtml() %>
			        	</span>
			        </td>
			        <td class="reportCellEven" align ="right" colspan="">
			        	<%= formatCurrency.format(bmoOrderItem.getPrice().toDouble()) %>
			        </td>
		      </tr>  
		        
		<% } 	
	 } %>
	<tr class="reportTitleCell">
		<td class="reportHeaderCell" colspan="6">Extras</td>		
	</tr>
	<%
		BmoOrderSessionExtra bmoOrderSessionExtra = new BmoOrderSessionExtra();
		PmOrderSessionExtra pmOrderSessionExtra = new PmOrderSessionExtra(sFParams);
	    bmFilter = new BmFilter();
	    bmFilter.setValueFilter(bmoOrderSessionExtra.getKind(), bmoOrderSessionExtra.getOrderId().getName(), bmoOrder.getId());
	    Iterator<BmObject> extrasList = pmOrderSessionExtra.list(bmFilter).iterator();
	    while (extrasList.hasNext()) {
	    	bmoOrderSessionExtra = (BmoOrderSessionExtra)extrasList.next();	    	
	%>
			<tr>
				<td class="reportCellEven" align="center" colspan="">  
					&nbsp;<%= bmoOrderSessionExtra.getQuantity().toInteger() %>
				</td>
				<td class="reportCellEven" align="center" colspan="">  
					&nbsp;<%= bmoOrderSessionExtra.getBmoSessionTypeExtra().getName().toHtml() %>
				</td>
				<td class="reportCellEven" align="center" colspan="">  
					&nbsp;<%= bmoOrderSessionExtra.getBmoSessionTypeExtra().getName().toHtml() %>
				</td>
				<td class="reportCellEven" align="right" colspan="3">  
					&nbsp;<%= formatCurrency.format(bmoOrderSessionExtra.getAmount().toDouble()) %>
				</td>
			</tr>	
	<% } %>
	<tr>
	 	<td colspan="4">			
	 		&nbsp;
	 	</td>
	</tr>
			<tr>
		        <td colspan="9" class="reportHeaderCell">
		                Pagos Realizados                
		        </td>
		    </tr>
	        <tr class="">                    
	            <th class="reportCellEven" align="center">#</th>
	            <th class="reportCellEven" align="left">Mov.Banco</th>
	            <th class="reportCellEven" align="left">Fecha</th>
	            <th class="reportCellEven" align="left">Descripci&oacute;n</th>	            
	            <th class="reportCellEven" align="left">Cuenta de Banco</th>   
	            <th class="reportCellEven" align="right">Total</th>
	        </tr>
	<% 	 
		 i = 1;
		 double sumPayments = 0;
	     sql = " SELECT * FROM bankmovconcepts " +
		       " LEFT JOIN raccounts ON (bkmc_raccountid = racc_raccountid) " +
	           " LEFT JOIN raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) " +	    	   
	    	   " LEFT JOIN bankmovements ON (bkmc_bankmovementid = bkmv_bankmovementid) " +
			   " LEFT JOIN bankaccounts ON (bkmv_bankaccountid = bkac_bankaccountid) " +
	           " WHERE racc_orderid = " + bmoOrder.getId() +
			   " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" +
	           " ORDER BY racc_duedate ";
	     pmConn.doFetch(sql);
	     while(pmConn.next()) { %>
	    	  <tr class=""> 
	    	  	   <td class="reportCellEven" align="center">
	                    <%= i %>                  
	               </td>
	               <td class="reportCellEven" align="left">
	                    <%= pmConn.getString("bankmovements", "bkmv_code") %>                  
	               </td>
	               <td class="reportCellEven" align="left">
	                    <%= pmConn.getString("bankmovements", "racc_duedate") %>                  
	               </td>
	               <td class="reportCellEven" align="left">
	                    <%= pmConn.getString("bankmovements", "bkmv_description") %>                  
	               </td>
	               <td class="reportCellEven" align="left">
	                    <%= pmConn.getString("bankaccounts", "bkac_name") %>                  
	               </td>
	               <td class="reportCellEven" align="right">
	                    <%= formatCurrency.format(pmConn.getDouble("bkmc_amount")) %>                  
	               </td>
	          </tr>    
	<%   
		sumPayments += 	pmConn.getDouble("bkmc_amount");
		  i++;
	    }
    %>
    <tr>
		<th class="reportCellEven" colspan="5" align="right">&nbsp;</th>
		<td class="reportCellEven" align="right">		
			<b><%= SFServerUtil.formatCurrency(sumPayments) %></b>
		</td>
	</tr>
	<tr>
	    <td colspan="6" class="reportHeaderCell">
	        Totales                
	    </td>
	</tr>
	<tr>		
		<th class="reportCellEven" colspan="5" align="right">Venta Total</th>
		<td class="reportCellEven" align="right" colspan="">
			<b><%= SFServerUtil.formatCurrency(bmoOrder.getTotal().toDouble()) %></b>
		</td>
	</tr>
	<tr>
		<th class="reportCellEven" colspan="5" align="right">Pagos</th>
		<td class="reportCellEven" align="right">		
			<b><%= SFServerUtil.formatCurrency(sumPayments) %></b>
		</td>
	</tr>
	<tr>
		<th class="reportCellEven" colspan="5" align="right">Saldo</th>
		<td class="reportCellEven" align="right">		
			<b><%= SFServerUtil.formatCurrency(bmoOrder.getTotal().toDouble() - sumPayments) %></b>
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
