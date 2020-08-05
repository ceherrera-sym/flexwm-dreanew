<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.shared.co.BmoWork"%>
<%@page import="com.flexwm.shared.co.BmoWorkContract"%>
<%@page import="com.flexwm.server.co.PmWorkContract"%>
<%@page import="com.flexwm.shared.co.BmoContractEstimation"%>
<%@page import="com.flexwm.server.co.PmContractEstimation"%>
<%@page import="com.flexwm.shared.fi.BmoBudgetItem"%>
<%@page import="com.flexwm.server.fi.PmBudgetItem"%>
<%@page import="com.flexwm.shared.op.BmoSupplierAddress"%>
<%@page import="com.flexwm.server.op.PmSupplierAddress"%>

<%@page import="com.flexwm.server.AmountByWord"%>
<%@page import="com.flexwm.server.AmountByWorden"%>
<%@page import="com.flexwm.server.AmountByWordes"%>
<%@page import="com.flexwm.server.IAmountByWord"%>
<%@page import="com.flexwm.server.NumberByWord"%>
<%@page import="com.symgae.client.ui.UiParams"%>

<%@page import="com.symgae.server.PmConn"%>
<%@include file="../inc/login_opt.jsp" %>

<html>
<head>
    <title>:::Estimaciones del Contrato.:::</title>
    <link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>"> 
     <link rel="stylesheet" type="text/css" href="../css/flexwm.css"> 
</head>
<body class="default">
<%
	
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(Locale.US);
	
	PmConn pmConn = new PmConn(sFParams);
	PmConn pmConn2 = new PmConn(sFParams);
	
	pmConn.open();
	pmConn2.open();
	
	try {
		int workContractId = 0;	
		if (isExternal) workContractId = decryptId;
		else workContractId = Integer.parseInt(request.getParameter("foreignId"));
		
		PmWorkContract pmWorkContract = new PmWorkContract(sFParams);
		BmoWorkContract bmoWorkContract = (BmoWorkContract)pmWorkContract.get(workContractId);
		
		BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();
		PmBudgetItem pmBudgetItem = new PmBudgetItem(sFParams);
		if(bmoWorkContract.getBudgetItemId().toInteger() > 0)
			bmoBudgetItem = (BmoBudgetItem)pmBudgetItem.get(bmoWorkContract.getBudgetItemId().toInteger());
		
		
	    //Ciudad del Proveedor
	    PmSupplierAddress pmSupplierAddress = new PmSupplierAddress(sFParams);
	    BmoSupplierAddress bmoSupplierAddress = new BmoSupplierAddress();
	    PmConn pmConnSupplierAddress = new PmConn(sFParams);
	    pmConnSupplierAddress.open();

	    String sqlSuplAddress = " SELECT * FROM supplieraddress WHERE suad_supplierid = " + bmoWorkContract.getSupplierId().toInteger() + " ORDER BY suad_type DESC";
	    pmConnSupplierAddress.doFetch(sqlSuplAddress);
	    System.out.println("sqlSuplAddress: "+sqlSuplAddress);
		if(pmConnSupplierAddress.next()){
			bmoSupplierAddress = (BmoSupplierAddress)pmSupplierAddress.get(pmConnSupplierAddress.getInt("suad_supplieraddressid"));
		}

		pmConnSupplierAddress.close();
	    
	    //Ciudad del Company	    
		PmCity pmCity = new PmCity(sFParams);
	    BmoCity bmoCityCompany = (BmoCity)pmCity.get(bmoWorkContract.getBmoCompany().getCityId().toInteger());
	
	    AmountByWord amountByWord = new AmountByWord();
		amountByWord.setLanguage("es");
		amountByWord.setCurrency("es");
	    
		
		PmUser pmUser = new PmUser(sFParams);
		BmoUser bmoUser = null;
		
		BmoCompany bmoCompany = new BmoCompany();
		PmCompany pmCompany = new PmCompany(sFParams);
		bmoCompany = (BmoCompany)pmCompany.get(bmoWorkContract.getCompanyId().toInteger());
		
		// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
		String logoURL ="";
		if (!bmoCompany.getLogo().toString().equals(""))
			logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
		else 
			logoURL = sFParams.getMainImageUrl();
 %>   


<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr>
		<td align="left" width="" rowspan="10" valign="top">
			<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= sFParams.getMainImageUrl() %>" >
		</td>
		<td colspan="4" class="reportTitleCell">
			Estimaciones del Contrato: <%= bmoWorkContract.getCode().toString() %>
		</td>
	</tr>     
	<tr>
		<th align = "left" class="reportCellEven">Contrato:</th>
		<td class="reportCellEven">
			<%= bmoWorkContract.getName().toString() %>                
		</td>
		<th align = "left" class="reportCellEven">Compa&ntilde;&iacute;a:</th>
		<td class="reportCellEven">
			<%= bmoWorkContract.getBmoCompany().getLegalName().toString() %>                
		</td>
	</tr>
	<tr>
		<th align = "left" class="reportCellEven">Descripci&oacute;n:</th>
		<td class="reportCellEven">
			<%= bmoWorkContract.getDescription().toString() %>          
		</td>
		<th align = "left" class="reportCellEven">Contratista:</th>
		<td class="reportCellEven">
			<%= bmoWorkContract.getBmoSupplier().getCode().toString() %>&nbsp;&nbsp;<%= bmoWorkContract.getBmoSupplier().getName().toString() %>             
		</td>
	</tr>
	<tr>
		<th align = "left" class="reportCellEven">Obra Autorizada:</th>
		<td class="reportCellEven">
			<%= bmoWorkContract.getBmoWork().getCode().toString() %>&nbsp;|&nbsp;<%= bmoWorkContract.getBmoWork().getName().toString() %>               
		</td>
		<th align = "left" class="reportCellEven">Presupuesto</th>
		<td class="reportCellEven">
			<%= bmoBudgetItem.getBmoBudget().getName().toString() %>&nbsp;&nbsp;|&nbsp;&nbsp;<%= bmoBudgetItem.getBmoBudgetItemType().getName().toString() %>            
		</td>
	</tr>
		<th align = "left" class="reportCellEven">Fecha de Inicio:</th>
		<td class="reportCellEven">
			<%= bmoWorkContract.getStartDate().toString() %>                
		</td>
		<th align = "left" class="reportCellEven">Fecha T&eacute;rmino:</th>
		<td class="reportCellEven">
			<%= bmoWorkContract.getEndDate().toString() %>               
		</td>
	</tr>     
	<tr>
		<th align = "left" class="reportCellEven">Fecha Pago Anticipo:</th>
		<td class="reportCellEven">
			<%= bmoWorkContract.getPaymentDate().toString() %>             
		</td>
		<th align = "left" class="reportCellEven">Fecha Contrato:</th>
		<td class="reportCellEven">
			<%= bmoWorkContract.getDateContract().toString() %>              
		</td>
	</tr>
</table>
<br>
<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr class="documentSubTitle">
		<td class="reportHeaderCellCenter">Consecutivo</td>
		<td class="reportHeaderCell">Fecha Inicio</td>
		<td class="reportHeaderCell">Fecha T&eacute;rmino</td>
		<td class="reportHeaderCell">Estatus</td>
		<td class="reportHeaderCell">Pago</td>
		<td class="reportHeaderCellRight">Monto &nbsp;</td>
		<td class="reportHeaderCellRight">IVA &nbsp;</td>
		<td class="reportHeaderCellRight">SubTotal &nbsp;</td>
		<td class="reportHeaderCellRight">Amort. Ant. &nbsp;</td>
		<td class="reportHeaderCellRight">Fondo de Garat&iacute;a &nbsp;</td>
		<td class="reportHeaderCellRight">Otros Cargos &nbsp;</td>
		<td class="reportHeaderCellRight">Neto Pagar &nbsp;</td>
	</tr>
<%
	double monto = 0, tax = 0, subTotal = 0, amort = 0, fondoGarantia = 0, otrosCargos = 0, netoPagar = 0;
	double subTotalGeneral = 0;
	BmoContractEstimation bmoContractEstimation = new BmoContractEstimation();
	PmContractEstimation pmContractEstimation = new PmContractEstimation(sFParams);
	BmFilter bmFilter = new BmFilter();

	bmFilter.setValueFilter(bmoContractEstimation.getKind(), bmoContractEstimation.getWorkContractId().getName(), bmoWorkContract.getId());
	Iterator<BmObject> estimations = pmContractEstimation.list(bmFilter).iterator();

	int i = 1;
	while (estimations.hasNext()) {
		bmoContractEstimation = (BmoContractEstimation)estimations.next();

		monto += bmoContractEstimation.getAmount().toDouble();
		tax += bmoContractEstimation.getTax().toDouble();
		subTotal += bmoContractEstimation.getSubTotal().toDouble(); 
		amort += bmoContractEstimation.getDownPayment().toDouble();
		fondoGarantia += bmoContractEstimation.getWarrantyFund().toDouble(); 
		otrosCargos += bmoContractEstimation.getOthersExpenses().toDouble();
		netoPagar += bmoContractEstimation.getTotal().toDouble();
%>
		<tr>
			<td class="reportCellEven" align ="center">  
				&nbsp;<%= bmoContractEstimation.getConsecutive().toHtml() %>
			</td>
			<td class="reportCellEven" align="left">  
				&nbsp;<%= bmoContractEstimation.getStartDate().toHtml() %>
			</td>
			<td class="reportCellEven" align="left">  
				&nbsp;<%= bmoContractEstimation.getEndDate().toHtml() %>
			</td>
			<td class="reportCellEven" align="left">
				&nbsp;<%= bmoContractEstimation.getStatus().getSelectedOption().getLabeltoHtml() %>
			</td>
			<td class="reportCellEven" align="left">
				&nbsp;<%= bmoContractEstimation.getPaymentStatus().getSelectedOption().getLabeltoHtml() %>
			</td>
			<td class="reportCellEven" align="right">
				&nbsp;<%= formatCurrency.format(bmoContractEstimation.getAmount().toDouble()) %>
			</td>
			<td class="reportCellEven" align="right">
				&nbsp;<%= formatCurrency.format(bmoContractEstimation.getTax().toDouble()) %>
			</td>
			<td class="reportCellEven" align ="right">  
				&nbsp;<%= formatCurrency.format(bmoContractEstimation.getSubTotal().toDouble()) %>
			</td>
			<td class="reportCellEven" align ="right">  
				&nbsp;<%= formatCurrency.format(bmoContractEstimation.getDownPayment().toDouble()) %>
			</td>
			<td class="reportCellEven" align="right">  
				&nbsp;<%= formatCurrency.format(bmoContractEstimation.getWarrantyFund().toDouble()) %>
			</td>
			<td class="reportCellEven" align="right">  
				&nbsp;<%= formatCurrency.format(bmoContractEstimation.getOthersExpenses().toDouble()) %>
			</td>
			<td class="reportCellEven" align="right">
				&nbsp;<%= formatCurrency.format(bmoContractEstimation.getTotal().toDouble()) %>
			</td>
		<tr>
<%
      }
%>
	<tr>
		<td class="reportCellEven" align ="left" colspan="5">  
			&nbsp;
		</td>
		<td class="reportCellEven" align="right">
			&nbsp;<b><%= formatCurrency.format(monto)%></b>
		</td>
		<td class="reportCellEven" align="right">
			&nbsp;<b><%= formatCurrency.format(tax) %></b>
		</td>
		<td class="reportCellEven" align ="right">  
			&nbsp;<b><%= formatCurrency.format(subTotal) %></b>
		</td>
		<td class="reportCellEven" align ="right">  
			&nbsp;<b><%= formatCurrency.format(amort) %></b>
		</td>
		<td class="reportCellEven" align="right">  
			&nbsp;<b><%= formatCurrency.format(fondoGarantia) %></b>
		</td>
		<td class="reportCellEven" align="right">  
			&nbsp;<b><%= formatCurrency.format(otrosCargos) %></b>
		</td>
		<td class="reportCellEven" align="right">
			&nbsp;<b><%= formatCurrency.format(netoPagar) %></b>
		</td>
	<tr>
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
