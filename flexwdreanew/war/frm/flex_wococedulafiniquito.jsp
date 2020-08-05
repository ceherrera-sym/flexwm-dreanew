<%@page import="com.flexwm.shared.BmoFlexConfig"%>
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
    <title>:::Cedula Finiquito.:::</title>
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
			<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
		</td>
		<td colspan="4" class="reportTitleCell">
			Cedula Finiquito del Contratato: <%= bmoWorkContract.getName().toString() %>
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
		<th align = "left" class="reportCellEven">Ubicaci&oacute;n:</th>
		<td class="reportCellEven">
			<%= bmoWorkContract.getBmoWork().getBmoDevelopmentPhase().getName().toString() %>               
		</td>
		<th align = "left" class="reportCellEven">Presupuesto</th>
		<td class="reportCellEven">
			<%= bmoBudgetItem.getBmoBudget().getName().toString() %>&nbsp;&nbsp;|&nbsp;&nbsp;<%= bmoBudgetItem.getBmoBudgetItemType().getName().toString() %>            
		</td>
	</tr>
	<tr>
		<th align = "left" class="reportCellEven">Fecha Inicio:</th>
		<td class="reportCellEven">
			<%= bmoWorkContract.getStartDate().toString() %>          
		</td>
		<th align = "left" class="reportCellEven">Fecha Terminaci&oacute;n:</th>
		<td class="reportCellEven">
			<%= bmoWorkContract.getEndDate().toString() %>             
		</td>
	</tr>
	<tr>
		<th align = "left" class="reportCellEven">Importe:</th>
		<td class="reportCellEven">
			<%= SFServerUtil.formatCurrency(bmoWorkContract.getSubTotal().toDouble()) %>                
		</td>
		<th align = "left" class="reportCellEven">Iva Contrato:</th>
		<td class="reportCellEven">
			<%= SFServerUtil.formatCurrency(bmoWorkContract.getTax().toDouble()) %>               
		</td>
	</tr>     
	<tr>
		<th align = "left" class="reportCellEven">Importe Anticipo:</th>
		<td class="reportCellEven">
			<%= SFServerUtil.formatCurrency(bmoWorkContract.getDownPayment().toDouble()) %>
		</td>
		<th align = "left" class="reportCellEven">% Anticipo:</th>
		<td class="reportCellEven">
			<%= SFServerUtil.roundCurrencyDecimals(bmoWorkContract.getPercentDownPayment().toDouble()) %>             
		</td>
	</tr>
	<tr>
		<th align = "left" class="reportCellEven">Iva Anticipo:</th>
		<td class="reportCellEven">
			<% 
				double taxRate = (((BmoFlexConfig)sFParams.getBmoAppConfig()).getTax().toDouble() / 100);
				double tax = 0;
				if (bmoWorkContract.getHasTax().toBoolean())
					tax = bmoWorkContract.getDownPayment().toDouble() * taxRate;
			%>
			<%= SFServerUtil.formatCurrency(tax) %>             
		</td>
		<th align = "left" class="reportCellEven">% Fondo de Garantia:</th>
		<td class="reportCellEven">
			<%= SFServerUtil.roundCurrencyDecimals(bmoWorkContract.getPercentGuaranteeFund().toDouble()) %>
		</td>
	</tr>
</table>
<br>
<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr class="documentSubTitle">
		<td class="reportHeaderCellCenter">&nbsp;</td>
		<td class="reportHeaderCellCenter" colspan ="2">Periodo</td>
		<td class="reportHeaderCellCenter" colspan ="">Importe Estimaci&oacute;n</td>
		<td class="reportHeaderCellCenter">Traslado</td>
		<td class="reportHeaderCellCenter" colspan="3">Anticipo</td>
		<td class="reportHeaderCellCenter" colspan="3">&nbsp;</td>
	</tr>
	<tr class="documentSubTitle">
		<td class="reportHeaderCellCenter">No.Est.</td>
		<td class="reportHeaderCellCenter">Inicio</td>
		<td class="reportHeaderCellCenter">Fin</td>
		<td class="reportHeaderCellCenter">Monto</td>
		<td class="reportHeaderCellCenter">Iva</td>
		<td class="reportHeaderCellRight">Amortizado</td>
		<td class="reportHeaderCellRight">Por Amortizar</td>
		<td class="reportHeaderCellRight">Iva</td>		
		<td class="reportHeaderCellRight">Fondo de Garat&iacute;a &nbsp;</td>
		<td class="reportHeaderCellRight">Otros Cargos &nbsp;</td>
		<td class="reportHeaderCellRight">Neto Pagar &nbsp;</td>
	</tr>
<%
	double amountCoes = 0, taxesCoes = 0, taxesDownPayment = 0, subTotal = 0, amortizedTotal = 0, toAmortizedTotal = 0, 
	warrantyFound = 0, others = 0, netoPagar = 0;
	double subTotalGeneral = 0;
	BmoContractEstimation bmoContractEstimation = new BmoContractEstimation();
	PmContractEstimation pmContractEstimation = new PmContractEstimation(sFParams);
	BmFilter bmFilter = new BmFilter();

	bmFilter.setValueFilter(bmoContractEstimation.getKind(), bmoContractEstimation.getWorkContractId().getName(), bmoWorkContract.getId());
	Iterator<BmObject> estimations = pmContractEstimation.list(bmFilter).iterator();

	int i = 1;
	while (estimations.hasNext()) {
		bmoContractEstimation = (BmoContractEstimation)estimations.next();

		amountCoes += bmoContractEstimation.getAmount().toDouble();
		taxesCoes += bmoContractEstimation.getTax().toDouble();
		subTotal += bmoContractEstimation.getSubTotal().toDouble();
		warrantyFound += bmoContractEstimation.getWarrantyFund().toDouble(); 
		others += bmoContractEstimation.getOthersExpenses().toDouble();
		netoPagar += bmoContractEstimation.getTotal().toDouble();
%>
		<tr>
			<td class="reportCellEven" align ="center">  
				&nbsp;<%= bmoContractEstimation.getConsecutive().toHtml() %>
			</td>
			<td class="reportCellEven" align="center">  
				&nbsp;<%= bmoContractEstimation.getStartDate().toHtml() %>
			</td>
			<td class="reportCellEven" align="center">  
				&nbsp;<%= bmoContractEstimation.getEndDate().toHtml() %>
			</td>
			<td class="reportCellEven" align="right">
				&nbsp;<%= formatCurrency.format(bmoContractEstimation.getSubTotal().toDouble()) %>
			</td>
			<td class="reportCellEven" align="right">
				&nbsp;<%= formatCurrency.format(bmoContractEstimation.getTax().toDouble()) %>
			</td>
			<td class="reportCellEven" align ="right"> 
				<%
					//Amortizado
					double amortized = (bmoContractEstimation.getSubTotal().toDouble() * bmoWorkContract.getPercentDownPayment().toDouble()) /100;
					amortizedTotal += amortized;
				%> 
				&nbsp;<%= formatCurrency.format(amortized) %>
			</td>
			<td class="reportCellEven" align ="right">
			  	<%
					//Por Amortizado
					double toAmortized = (bmoContractEstimation.getSubTotal().toDouble() * bmoWorkContract.getPercentDownPayment().toDouble() / 100) - amortized;
			  		toAmortizedTotal += toAmortized;
				%>
				&nbsp;<%= formatCurrency.format(toAmortized) %>
			</td>
			<td class="reportCellEven" align ="right">
				<%					
					//Amortizado Iva
					double amortizedTax = 0;
					if (bmoWorkContract.getHasTax().toBoolean())
						amortizedTax = (amortized * taxRate) /100;	
					taxesDownPayment += amortizedTax;
				%>
				&nbsp;<%= formatCurrency.format(amortizedTax) %>
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
		<td class="reportCellEven" align ="left" colspan="3">  
			&nbsp;
		</td>
		<td class="reportCellEven" align="right">
			&nbsp;<b><%= formatCurrency.format(amountCoes)%></b>
		</td>
		<td class="reportCellEven" align="right">
			&nbsp;<b><%= formatCurrency.format(taxesCoes) %></b>
		</td>
		<td class="reportCellEven" align ="right">  
			&nbsp;<b><%= formatCurrency.format(amortizedTotal) %></b>
		</td>
		<td class="reportCellEven" align ="right">  
			&nbsp;<b><%= formatCurrency.format(toAmortizedTotal) %></b>
		</td>
		<td class="reportCellEven" align ="right">  
			&nbsp;<b><%= formatCurrency.format(taxesDownPayment) %></b>
		</td>
		<td class="reportCellEven" align="right">  
			&nbsp;<b><%= formatCurrency.format(warrantyFound) %></b>
		</td>
		<td class="reportCellEven" align="right">  
			&nbsp;<b><%= formatCurrency.format(others) %></b>
		</td>
		<td class="reportCellEven" align="right">
			&nbsp;<b><%= formatCurrency.format(netoPagar) %></b>
		</td>		
	<tr>
</table>
<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr>
		<td colspan="2">&nbsp;</td>
	</tr>
</table>
<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr>
		<th align = "left" class="reportCellEven">Importe Contrato:</th>
		<td class="reportCellEven" align="">
			<%= SFServerUtil.formatCurrency(bmoWorkContract.getSubTotal().toDouble()) %>                
		</td>
		<th align = "left" class="reportCellEven">Saldo Contrato:</th>
		<td class="reportCellEven" align="">
			<%= SFServerUtil.formatCurrency(bmoWorkContract.getSubTotal().toDouble() - amountCoes) %>               
		</td>
	</tr>   
	<tr>
		<th align = "left" class="reportCellEven">Total Estimado:</th>
		<td class="reportCellEven" align="">
			<%= SFServerUtil.formatCurrency(amountCoes) %>                
		</td>
		<th align = "left" class="reportCellEven">Saldo Anticipo Amortizar:</th>
		<td class="reportCellEven" align="">			
			<%= SFServerUtil.formatCurrency((bmoWorkContract.getSubTotal().toDouble() * bmoWorkContract.getPercentDownPayment().toDouble() /100) - amortizedTotal)%>               
		</td>
	</tr>   
	<tr>
		<th align = "left" class="reportCellEven">Anticipo Entregado:</th>
		<td class="reportCellEven" align="">
			<%= SFServerUtil.formatCurrency(bmoWorkContract.getDownPayment().toDouble()) %>                
		</td>
		<th align = "left" class="reportCellEven">Iva Anticipo Amortizar:</th>
		<td class="reportCellEven" align="">			
			<%			
			double taxDownPayment = (bmoWorkContract.getSubTotal().toDouble() * bmoWorkContract.getPercentDownPayment().toDouble() * taxRate) / 100;
			taxDownPayment = taxDownPayment - taxesDownPayment;
			%>
			<%= SFServerUtil.formatCurrency(taxDownPayment)%>               
		</td>
	</tr>   
	<tr>
		<th align = "left" class="reportCellEven">Anticipo Amortizado:</th>
		<td class="reportCellEven" align="">
			<%= SFServerUtil.formatCurrency(amortizedTotal) %>                
		</td>
		<th align = "left" class="reportCellEven">Otros:</th>
		<td class="reportCellEven" align="">
			<%= SFServerUtil.formatCurrency(others)%>               
		</td>
	</tr>
	<tr>
		<th align = "left" class="reportCellEven">Iva Anticipo Entregado:</th>
		<td class="reportCellEven" align="">
			<%= SFServerUtil.formatCurrency(tax) %>                
		</td>
		<th align = "left" class="reportCellEven">Fondo Garantia por Liberar:</th>
		<td class="reportCellEven" align="">			
			<%= SFServerUtil.formatCurrency(warrantyFound)%>			               
		</td>
	</tr>
	<tr>
		<th align = "left" class="reportCellEven">Iva Anticipo Amortizado:</th>
		<td class="reportCellEven" align="">
			<%= SFServerUtil.formatCurrency(taxesCoes) %>
		</td>
		<th align = "left" class="reportCellEven">Otras Deducciones:</th>
			<language javascript="javascript">
        	<FORM action="" method="GET" name="form">
        		<% System.out.println("Valor " + warrantyFound); %>
			    <input type="hidden" name="warranty" value="<%= SFServerUtil.roundCurrencyDecimals(warrantyFound) %>">
				<td class="reportCellEven" align="">
	                &nbsp;<input type="text" name="woco_dato" onBlur="javascript:resta();">
				</td>
	</tr>
	<tr>			
		<th align = "left" class="reportCellEven">Total a Pagar:</th>
		<td class="reportCellEven" align="">
			&nbsp;<input type="text" align="right" name="totalPago" value="" readonly="true">
		</td>
				
		<th align = "left" class="reportCellEven">Comentarios:</th>
		<td class="documentComments">
			<textarea rows="10" cols="25"></textarea>
		</td>
		</FORM>	
	</tr>		   	
</table>
<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
	<p class="documentTitleCaption" align="left"><br><br> 
		Fecha de Impresi&oacute;n <%= SFServerUtil.nowToString(sFParams, "dd/MM/yyyy HH:mm") %> Por:
		<%=  sFParams.getLoginInfo().getBmoUser().getFirstname() + " " + sFParams.getLoginInfo().getBmoUser().getFatherlastname().toString() %>
	</p>
</table>	
<script>  //funcion para realizar laresta de un valor ya dado y uno q esta en el campo en blanco
           function resta() {
				
               //var v1= parseFloat(document.form.woco_totalWarrantyFund.value);
               var valor1 = document.form.warranty.value;
               var valor2 = document.form.woco_dato.value;
               var restavalor = parseFloat(valor1) - parseFloat(valor2);

               document.form.totalPago.value = formatCurrency(restavalor);
               document.form.woco_dato.value = formatCurrency(valor2);

           }
           
           function formatCurrency(num) {
        		num = num.toString().replace(/\$|\,/g,'');
        		if(isNaN(num))
        		num = "0";
        		sign = (num == (num = Math.abs(num)));
        		num = Math.floor(num*100+0.50000000001);
        		cents = num%100;
        		num = Math.floor(num/100).toString();
        		if(cents<10)
        		cents = "0" + cents;
        		for (var i = 0; i < Math.floor((num.length-(1+i))/3); i++)
        		num = num.substring(0,num.length-(4*i+3))+','+
        		num.substring(num.length-(4*i+3));
        		return (((sign)?'':'-') + '$' + num + '.' + cents);
        	}
</script>

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
