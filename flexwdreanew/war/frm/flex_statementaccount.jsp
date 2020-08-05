<%@page import="com.flexwm.server.cm.*"%>
<%@page import="com.flexwm.shared.cm.*"%>
<%@page import="com.flexwm.server.ev.*"%>
<%@page import="com.flexwm.shared.ev.*"%>
<%@page import="com.flexwm.server.op.*"%>
<%@page import="com.flexwm.shared.op.*"%>
<%@page import="com.flexwm.server.co.*"%>
<%@page import="com.flexwm.shared.co.*"%>
<%@page import="com.flexwm.server.fi.*"%>
<%@page import="com.flexwm.shared.fi.*"%>
<%@page import="com.symgae.shared.sf.BmoCompany"%>
<%@page import="com.symgae.server.sf.PmCompany"%>
<%@page import="com.symgae.shared.BmFilter"%>
<%@page import="com.flexwm.shared.wf.BmoWFlowType"%>
<%@page import="com.flexwm.server.wf.PmWFlowType"%>
<%@page import="com.symgae.server.PmConn"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import= "com.symgae.server.SFServerUtil"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import= "java.util.Calendar"%>
<%@page import="java.sql.Types"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.DecimalFormat "%>

<%@include file="../inc/login_opt.jsp" %>

<%
	String title = "Estado de Cuenta del Cliente";

	BmoPropertySale bmoPropertySale = new BmoPropertySale(); 	
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)sFParams.getBmoProgram(bmoPropertySale.getProgramCode());

%>

<html>
	<% 
	// Imprimir
	String print = "0", permissionPrint = "";
	if ((String)request.getParameter("print") != null) print = (String)request.getParameter("print");
	
	// Exportar a Excel
	String exportExcel = "0";
	if ((String)request.getParameter("exportexcel") != null) exportExcel = (String)request.getParameter("exportexcel");
	if (exportExcel.equals("1") && sFParams.hasPrint(bmoProgram.getCode().toString())) {
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "inline; filename=\""+title+".xls\"");
	}
	
	//Si se visualiza y no se tienen permisos, deshabilita: copiar, pegar, seleccionar, menú(clic-derecho).
	//En caso de que mande a imprimir, deshabilita contenido
	if(!(sFParams.hasPrint(bmoProgram.getCode().toString()))) { %>
	<style> 
		body{
			user-select: none;
			-moz-user-select: none; 
			-o-user-select: none; 
			-webkit-user-select: none; 
			-ie-user-select: none; 
			-khtml-user-select:none; 
			-ms-user-select:none; 
			-webkit-touch-callout:none
		}
	</style>
	<style type="text/css" media="print">
	    * { display: none; }
	</style>
	<%
	permissionPrint = "oncopy='return false' oncut='return false' onpaste='return false' oncontextmenu='return false' onkeydown='return false' onselectstart='return false' ondragstart='return false'";
		//Mensaje 
		if(print.equals("1") || exportExcel.equals("1")) { %>
			<script>
				alert('No tiene permisos para imprimir/exportar el documento, el documento saldr\u00E1 en blanco');
			</script>
		<% }
	}
	%>

<head>
	<title>:::<%= title %>:::</title>
    <link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>"> 
     <link rel="stylesheet" type="text/css" href="../css/flexwm.css"> 
</head>
<body class="default" <%= permissionPrint %>>

<%
	try {
	    // Si es llamada externa, utilizar llave desencriptada
	    int propertySaleId = 0;
	    if (isExternal) propertySaleId = decryptId;
	    else propertySaleId = Integer.parseInt(request.getParameter("foreignId"));	    
	    
		//Locale locale = new Locale("es", "MX");
		NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(Locale.US);
	    
	    //Venta
		PmPropertySale pmPropertySale = new PmPropertySale(sFParams);
		bmoPropertySale = (BmoPropertySale)pmPropertySale.get(propertySaleId);
		
		//Pedido 
		BmoOrder bmoOrder = new BmoOrder();
		PmOrder pmOrder = new PmOrder(sFParams);
		bmoOrder = (BmoOrder)pmOrder.get(bmoPropertySale.getOrderId().toInteger());
		
		//Pedido-Inmueble		
		BmoOrderProperty bmoOrderProperty = new BmoOrderProperty();
		PmOrderProperty pmOrderProperty = new PmOrderProperty(sFParams);
		bmoOrderProperty = (BmoOrderProperty)pmOrderProperty.getBy(bmoOrder.getId(), bmoOrderProperty.getOrderId().getName());
		
		//Vivienda
		BmoProperty bmoProperty = new BmoProperty();
		PmProperty pmProperty = new PmProperty(sFParams);
		bmoProperty = (BmoProperty)pmProperty.get(bmoPropertySale.getPropertyId().toInteger());
		
		//Manzanas-Torres
		BmoDevelopmentBlock bmoDevelopmentBlock = new BmoDevelopmentBlock();
		PmDevelopmentBlock pmDevelopmentBlock = new PmDevelopmentBlock(sFParams);
		bmoDevelopmentBlock = (BmoDevelopmentBlock)pmDevelopmentBlock.get(bmoProperty.getDevelopmentBlockId().toInteger());
		
		//Desarrollo
		BmoDevelopment bmoDevelopment = new BmoDevelopment();
		PmDevelopment pmDevelopment = new PmDevelopment(sFParams);
		bmoDevelopment= (BmoDevelopment)pmDevelopment.get(bmoDevelopmentBlock.getBmoDevelopmentPhase().getDevelopmentId().toInteger());
		/*
		//Modelo
		BmoPropertyModel bmoPropertyModel = new BmoPropertyModel();
		PmPropertyModel pmPropertyModel = new PmPropertyModel(sFParams);
		bmoPropertyModel = (BmoPropertyModel)pmPropertyModel.get(bmoProperty.getPropertyModelId().toInteger());
		
		//Ciudad del Desarrollo
		BmoCity bmoCityDevelopment = new BmoCity();
		PmCity pmCityDevelopment = new PmCity(sFParams);
		bmoCityDevelopment = (BmoCity)pmCityDevelopment.get(bmoPropertyModel.getBmoDevelopment().getCityId().toInteger());
		*/
		
		//Cliente
		BmoCustomer bmoCustomer = new BmoCustomer();
		PmCustomer pmCustomer = new PmCustomer(sFParams);
		bmoCustomer = (BmoCustomer)pmCustomer.get(bmoPropertySale.getCustomerId().toInteger());
		/*
		//Direcciones del Cliente
	PmConn pmConnCustomer= new PmConn(sFParams);
	pmConnCustomer.open();
	boolean cuad = false;
	BmoCustomerAddress bmoCustomerAddress = new BmoCustomerAddress();
	PmCustomerAddress pmCustomerAddress = new PmCustomerAddress(sFParams);
	String sqlCustAddress = " SELECT * FROM customeraddress WHERE cuad_customerid = " + bmoCustomer.getId() + " ORDER BY cuad_type";
	pmConnCustomer.doFetch(sqlCustAddress);
	if(pmConnCustomer.next()) cuad = true;
	if(cuad)
		bmoCustomerAddress = (BmoCustomerAddress)pmCustomerAddress.getBy(bmoCustomer.getId(), bmoCustomerAddress.getCustomerId().getName());
	
	pmConnCustomer.close();
		*/
		//Telefonos del Cliente
	String homePhone = "", mobilPhone = "", workPhone = "", otherPhone = "";
	PmConn pmConnCustomerPhones= new PmConn(sFParams);
	pmConnCustomerPhones.open();
	BmoCustomerPhone bmoCustomerPhone = new BmoCustomerPhone();
	PmCustomerPhone pmCustomerPhone = new PmCustomerPhone(sFParams);
	String sqlCustPhone = " SELECT * FROM customerphones WHERE cuph_customerid = " + bmoCustomer.getId() + " ORDER BY cuph_type ";
	pmConnCustomerPhones.doFetch(sqlCustPhone);
	while(pmConnCustomerPhones.next()){
		if(pmConnCustomerPhones.getString("cuph_type").equals("" + BmoCustomerPhone.TYPE_HOME)){
			homePhone += pmConnCustomerPhones.getString("cuph_number") + " ";
		}else if(pmConnCustomerPhones.getString("cuph_type").equals("" + BmoCustomerPhone.TYPE_MOBILE)){
			mobilPhone += pmConnCustomerPhones.getString("cuph_number") + " ";
		}else if(pmConnCustomerPhones.getString("cuph_type").equals("" + BmoCustomerPhone.TYPE_WORK)){
			workPhone += pmConnCustomerPhones.getString("cuph_number") + " ";
		}else if(pmConnCustomerPhones.getString("cuph_type").equals("" + BmoCustomerPhone.TYPE_OTHER)){
			otherPhone += pmConnCustomerPhones.getString("cuph_number") + " ";
		}
	}
	pmConnCustomerPhones.close();
		
		/*
		//Correos del Cliente
	String personalEmail = "", workEmail = "";
	PmConn pmConnCustomerEmails= new PmConn(sFParams);
	pmConnCustomerEmails.open();
	BmoCustomerEmail bmoCustomerEmail = new BmoCustomerEmail();
	PmCustomerEmail pmCustomerEmail = new PmCustomerEmail(sFParams);
	String sqlCustEmail = " SELECT * FROM customeremails WHERE cuem_customerid = " + bmoCustomer.getId();
	pmConnCustomerEmails.doFetch(sqlCustEmail);
	while(pmConnCustomerEmails.next()){
		if(pmConnCustomerEmails.getString("cuem_type").equals("" + BmoCustomerEmail.TYPE_PERSONAL))
			personalEmail += pmConnCustomerEmails.getString("cuem_email") + "&nbsp;&nbsp;";
		if(pmConnCustomerEmails.getString("cuem_type").equals("" + BmoCustomerPhone.TYPE_WORK))
			workEmail += pmConnCustomerEmails.getString("cuem_email") + "&nbsp;&nbsp;";
	}
	pmConnCustomerEmails.close();
	*/
		//Empresa
		BmoCompany bmoCompany = new BmoCompany();
		PmCompany  pmCompany  = new PmCompany (sFParams);
		bmoCompany = (BmoCompany)pmCompany.get(bmoDevelopment.getCompanyId().toInteger());
		
		// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
		String logoURL = "";
		if (!bmoCompany.getLogo().toString().equals(""))
			logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
		else
			logoURL = sFParams.getMainImageUrl();

		/*
		//Ciudad de la Empresa
			BmoCity bmoCityCompany = new BmoCity();
			PmCity pmCity = new PmCity(sFParams);
			bmoCityCompany = (BmoCity)pmCity.get(bmoCompany.getCityId().toInteger());
			
		//Vendedor
		BmoUser bmoSalesUser = new BmoUser();
		PmUser pmSalesUser = new PmUser(sFParams);
		bmoSalesUser = (BmoUser)pmSalesUser.get(bmoPropertySale.getSalesUserId().toInteger());
		
		//Miscelaneas 
		//Formato numero de dos decimales
		DecimalFormat df = new DecimalFormat("###.##");
				
		//Fecha de hoy, '1 de Enero de 2015'
		String mes="";
		String day = SFServerUtil.nowToString(sFParams, "dd");
		String year = SFServerUtil.nowToString(sFParams, "YYYY");
		String nowMonth = SFServerUtil.nowToString(sFParams, "MM");
		switch ((Integer.parseInt(nowMonth))-1) {
			case 0:
				mes = "Enero";
				break;
			case 1:
				mes = "Febrero";
				break;
			case 2:
				mes = "Marzo";
				break;
			case 3:
				mes = "Abril";
				break;
			case 4:
				mes = "Mayo";
				break;
			case 5:
				mes = "Junio";
				break;
			case 6:
				mes = "Julio";
				break;
			case 7:
				mes = "Agosto";
				break;
			case 8:
				mes = "Septiembre";
				break;
			case 9:
				mes = "Octubre";
				break;
			case 10:
				mes = "Noviembre";
				break;
			case 11:
				mes = "Diciembre";
				break;
			default:
				mes = "n/d";
				break;
		}
		*/

		double paquetes = 0;
		BmoOrderPropertyModelExtra bmoOrderPropertyModelExtra = new BmoOrderPropertyModelExtra();
		PmOrderPropertyModelExtra pmOrderPropertyModelExtra = new PmOrderPropertyModelExtra(sFParams);
		BmFilter bmFilterOrpxSum = new BmFilter();
		bmFilterOrpxSum.setValueFilter(bmoOrderPropertyModelExtra.getKind(),
				bmoOrderPropertyModelExtra.getOrderId().getName(), bmoOrder.getId());
		Iterator<BmObject> itemsOrpxSum = pmOrderPropertyModelExtra.list(bmFilterOrpxSum).iterator();

		while (itemsOrpxSum.hasNext()) {
			bmoOrderPropertyModelExtra = (BmoOrderPropertyModelExtra) itemsOrpxSum.next();
			paquetes += bmoOrderPropertyModelExtra.getAmount().toDouble();
		}
%>

	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		<tr>
			<td align="left" width="5%" rowspan="10" valign="top">
				<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
			</td>
			<td colspan="4" class="reportTitleCell">
				Estado de Cuenta del Cliente: <%= bmoPropertySale.getCode().toString() %>
			</td>
		</tr>     
		<tr>
			<th align = "left" class="reportCellEven">Cliente:</th>
			<td class="reportCellEven">
				<%= bmoCustomer.getCode().toString() %>  <%= bmoCustomer.getDisplayName().toString() %>                 
			</td>
			<th align = "left" class="reportCellEven">Tel. Cel.:</th>
			<td class="reportCellEven">
				<%if(mobilPhone.equals("")){ %>
					<%= homePhone %>
				<%}else{%>
					<%= mobilPhone %>
				<%}%>
			</td>
		</tr>
		<tr>
			<th align = "left" class="reportCellEven">Direcci&oacute;n:</th>
			<td class="reportCellEven">
				<%= bmoProperty.getStreet().toString() %>           
			</td>
			<th align = "left" class="reportCellEven">N&uacute;mero:</th>
			<td class="reportCellEven">
				<%= bmoProperty.getNumber().toString() %>                
			</td>
		</tr>
		<tr>
			<th align = "left" class="reportCellEven">Fraccionamiento:</th>
			<td class="reportCellEven">
				<%= bmoDevelopment.getName().toString() %>                
			</td>
			<th align = "left" class="reportCellEven">Manzana - Lote</th>
			<td class="reportCellEven">
				<%= bmoDevelopmentBlock.getCode().toString() %> - <%= bmoProperty.getLot().toString() %>             
			</td>
		</tr>
		<tr>
			<th align = "left" class="reportCellEven">Precio Base:</th>
			<td class="reportCellEven">
				<%= SFServerUtil.formatCurrency(bmoOrderProperty.getPrice().toDouble()) %>           
			</td>
			<th align = "left" class="reportCellEven">Costo Adicional:</th>
			<td class="reportCellEven">
				<%= SFServerUtil.formatCurrency((bmoOrderProperty.getExtraLand().toDouble() + bmoOrderProperty.getExtraConstruction().toDouble() + bmoOrderProperty.getExtraOther().toDouble())) %>             
			</td>
		</tr>     
		<tr>
			<th align = "left" class="reportCellEven">Paquetes:</th>
			<td class="reportCellEven">
				<%= SFServerUtil.formatCurrency(paquetes) %>             
			</td>
			<th align = "left" class="reportCellEven">Precio Total:</th>
			<td class="reportCellEven">
				<%= SFServerUtil.formatCurrency(bmoOrder.getTotal().toDouble()) %>            
			</td>
		</tr>
	</table>
	<br>
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		<tr>
		    <td colspan="12" class="reportHeaderCell">
		        Detalle de CxC              
		    </td>
		</tr>
		<tr>                    
			<th class="reportCellEven" align="left">Clave</th>
			<th class="reportCellEven" align="left">Concepto</th>
			<th class="reportCellEven" align="left">Factura</th>
			<th class="reportCellEven" align="left">Folio</th>   
			<th class="reportCellEven" align="left">Descripci&oacute;n</th>                               
			<th class="reportCellEven" align="left">Ingreso</th>
			<th class="reportCellEven" align="left">Vencimiento</th>         
			<th class="reportCellEven" align="left">Estatus</th>
			<th class="reportCellEven" align="left">Estatus Pago</th>
			<th class="reportCellEven" align="right">Cargo</th>
			<th class="reportCellEven" align="right">Abono</th>         
			<th class="reportCellEven" align="right">Saldo</th>
		</tr>
		<%
        
		double amountW = 0, paymentsD = 0;
		BmoRaccount bmoRaccount = new BmoRaccount();
		PmRaccount pmRaccount = new PmRaccount(sFParams);
		// CXC CARGOS
		ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
		BmFilter filterRaccount = new BmFilter();
		BmFilter filterRaccountW = new BmFilter();

		filterRaccount.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getOrderId(), bmoOrder.getId());
		filterList.add(filterRaccount);
		filterRaccountW.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getBmoRaccountType().getType(), ""+BmoRaccountType.TYPE_WITHDRAW);
		filterList.add(filterRaccountW);
		
		Iterator<BmObject> items = new PmRaccount(sFParams).list(filterList).iterator();

		while (items.hasNext()) {
			bmoRaccount = (BmoRaccount)items.next();
			amountW += bmoRaccount.getAmount().toDouble();
		%>
			<tr> 
				<td class="reportCellEven" align="left">
					<%= bmoRaccount.getCode().toString() %>                  
				</td>
				<td class="reportCellEven" align="left">
					<%= bmoRaccount.getBmoRaccountType().getName().toString() %>                 
				</td>
				<td class="reportCellEven" align="left">
					<%= bmoRaccount.getInvoiceno().toString() %>                 
				</td>
				<td class="reportCellEven" align="left">
					<%= bmoRaccount.getFolio().toString() %>                 
				</td>
				<td class="reportCellEven" align="left">
					<%= bmoRaccount.getDescription().toString() %>                 
				</td>
				<td class="reportCellEven" align="left">
					<%= bmoRaccount.getReceiveDate().toString() %>                 
				</td>
				<td class="reportCellEven" align="left">
					<%= bmoRaccount.getDueDate().toString() %>                 
				</td>
				<td class="reportCellEven" align="left">
					<%= bmoRaccount.getStatus().getSelectedOption().getLabel() %>                 
				</td>
				<td class="reportCellEven" align="left">
					<%= bmoRaccount.getPaymentStatus().getSelectedOption().getLabel() %>                 
				</td>
				<td class="reportCellEven" align="right">
					<%= formatCurrency.format(bmoRaccount.getAmount().toDouble()) %>                 
				</td>
				<td class="reportCellEven" align="right">
					<%= formatCurrency.format(0) %>                 
				</td>
				<td class="reportCellEven" align="right">
					<%= formatCurrency.format(0) %>                 
				</td>
			</tr>			
			
			<%
		}
		
		%>
			<tr>
			    <td colspan="12" class="reportHeaderCell">
			        Pagos en Bancos              
			    </td>
			</tr>
		<%
		
		BmoBankMovConcept bmoBankMovConcept = new BmoBankMovConcept();
		
		ArrayList<BmFilter> filterListD = new ArrayList<BmFilter>();
		BmFilter filterRaccountOrde = new BmFilter();
		BmFilter filterBankMovConcept = new BmFilter();

		filterRaccountOrde.setValueFilter(bmoBankMovConcept.getKind(), bmoBankMovConcept.getBmoRaccount().getOrderId(), bmoOrder.getId());
		filterListD.add(filterRaccountOrde);
		
		Iterator<BmObject> itemsD = new PmBankMovConcept(sFParams).list(filterListD).iterator();

		while (itemsD.hasNext()) {
			bmoBankMovConcept = (BmoBankMovConcept)itemsD.next();
			paymentsD += bmoBankMovConcept.getAmount().toDouble();
		%>
			<tr> 
				<td class="reportCellEven" align="left">
					<%= bmoBankMovConcept.getBmoBankMovement().getCode().toString() %>                  
				</td>
				<td class="reportCellEven" align="left">
					<%= bmoBankMovConcept.getBmoBankMovement().getBmoBankMovType().getName().toString() %>                 
				</td>				
				<td class="reportCellEven" align="left">
					&nbsp;                 
				</td>
				<td class="reportCellEven" align="left">
					&nbsp;                 
				</td>
				<td class="reportCellEven" align="left">
					<%= bmoBankMovConcept.getBmoBankMovement().getDescription().toString() %>                 
				</td>				
				<td class="reportCellEven" align="left">
					<%= bmoBankMovConcept.getBmoBankMovement().getDueDate().toString() %>                 
				</td>
				<td class="reportCellEven" align="left">
					&nbsp;                 
				</td>				
				<td class="reportCellEven" align="left">
					<%= bmoBankMovConcept.getBmoBankMovement().getStatus().getSelectedOption().getLabel() %>                 
				</td>
				<td class="reportCellEven" align="left">
					&nbsp;                 
				</td>				
				<td class="reportCellEven" align="right">
					<%= formatCurrency.format(0) %>                 
				</td>
				<td class="reportCellEven" align="right">
					<%= formatCurrency.format(bmoBankMovConcept.getAmount().toDouble()) %>                 
				</td>
				<td class="reportCellEven" align="right">
					<%= formatCurrency.format(0) %>                 
				</td>
			</tr>
			<%
		}
		
		%>
			<tr>
			    <td colspan="12" class="reportHeaderCell">
			        Notas de Cr&eacute;dito              
			    </td>
			</tr>
		<%
		
		bmoRaccount = new BmoRaccount();
		
		filterListD = new ArrayList<BmFilter>();
		filterRaccountOrde = new BmFilter();
		BmFilter category = new BmFilter();
		
		category.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getBmoRaccountType().getCategory(), "" +BmoRaccountType.CATEGORY_CREDITNOTE);
		filterListD.add(category);
		
		filterRaccountW.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getBmoRaccountType().getType(), "" +BmoRaccountType.TYPE_DEPOSIT);
		filterListD.add(filterRaccountW);

		filterRaccountOrde.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getOrderId(), bmoOrder.getId());
		filterListD.add(filterRaccountOrde);
		
		itemsD = new PmRaccount(sFParams).list(filterListD).iterator();

		while (itemsD.hasNext()) {
			bmoRaccount = (BmoRaccount)itemsD.next();
			paymentsD += bmoRaccount.getTotal().toDouble();
		%>	
			<tr> 
			<td class="reportCellEven" align="left">
				<%= bmoRaccount.getCode().toString() %>                  
			</td>
			<td class="reportCellEven" align="left">
				<%= bmoRaccount.getBmoRaccountType().getName().toString() %>                 
			</td>
			<td class="reportCellEven" align="left">
				<%= bmoRaccount.getInvoiceno().toString() %>                 
			</td>
			<td class="reportCellEven" align="left">
				<%= bmoRaccount.getFolio().toString() %>                 
			</td>
			<td class="reportCellEven" align="left">
				<%= bmoRaccount.getDescription().toString() %>                 
			</td>
			<td class="reportCellEven" align="left">
				<%= bmoRaccount.getReceiveDate().toString() %>                 
			</td>
			<td class="reportCellEven" align="left">
				<%= bmoRaccount.getDueDate().toString() %>                 
			</td>
			<td class="reportCellEven" align="left">
				<%= bmoRaccount.getStatus().getSelectedOption().getLabel() %>                 
			</td>
			<td class="reportCellEven" align="left">
				<%= bmoRaccount.getPaymentStatus().getSelectedOption().getLabel() %>                 
			</td>
			<td class="reportCellEven" align="right">
				<%= formatCurrency.format(0) %>
			</td>
			<td class="reportCellEven" align="right">
				<%= formatCurrency.format(bmoRaccount.getTotal().toDouble()) %>                 
			</td>
			<td class="reportCellEven" align="right">
				<%= formatCurrency.format(0) %>                 
			</td>
		</tr>
	    <%
	    
		}	
		
		%>
		<tr>
			<td class="reportCellEven" colspan="9" >
				&nbsp;
			</td>
			<td class="reportCellEven" align="right">
				<b><%= formatCurrency.format(amountW) %></b>          
			</td>
			<td class="reportCellEven" align="right">
				<b><%= formatCurrency.format(paymentsD) %></b>
			</td>
			<td class="reportCellEven" align="right">
				<b><%= formatCurrency.format(amountW - paymentsD) %></b>
			</td>
		</tr>
	</table>  
	<br>
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		<tr>
		    <td colspan="12" class="reportHeaderCell">
		        Accesorios de la Venta           
		    </td>
		</tr>
		<tr>                    
			<th class="reportCellEven" align="left">Accesorio</th>
			<th class="reportCellEven" align="right">Cantidad</th>
			<th class="reportCellEven" align="right">Precio</th>
		</tr>
		<%
		double sumOrpx = 0;
        BmFilter bmFilterOrpx = new BmFilter();
        bmFilterOrpx.setValueFilter(bmoOrderPropertyModelExtra.getKind(), bmoOrderPropertyModelExtra.getOrderId().getName(), bmoOrder.getId());
        Iterator<BmObject> itemsOrpx = pmOrderPropertyModelExtra.list(bmFilterOrpx).iterator();
	
		while (itemsOrpx.hasNext()) {
			bmoOrderPropertyModelExtra = (BmoOrderPropertyModelExtra)itemsOrpx.next();
			sumOrpx += bmoOrderPropertyModelExtra.getPrice().toDouble();
		%>
			<tr> 
				<td class="reportCellEven" align="left">
					<%= bmoOrderPropertyModelExtra.getBmoPropertyModelExtra().getName().toString() %>
					<% if(!bmoOrderPropertyModelExtra.getComments().toString().equals("")){ %>
						 - <%= bmoOrderPropertyModelExtra.getComments().toString() %>
					<% } %>
				</td>
				<td class="reportCellEven" align="right">
					<%= bmoOrderPropertyModelExtra.getQuantity().toInteger() %>             
				</td>
				<td class="reportCellEven" align="right">
					<%= formatCurrency.format(bmoOrderPropertyModelExtra.getPrice().toDouble()) %>             
				</td>
			</tr>
		<%
		}
		%> 
		<tr>
			<td class="reportCellEven" colspan="2" >
				&nbsp;
			</td>
			<td class="reportCellEven" align="right">
				<b><%= formatCurrency.format(sumOrpx) %></b>
			</td>
		</tr>
	</table>

<%	} catch (Exception e) { 
	String errorLabel = "Error de Formato: Estado de Cuenta del Cliente";
	String errorText = "El Formato: Estado de Cuenta del Cliente no pudo ser desplegado exitosamente.";
	String errorException = e.toString();
	
	response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);
	}

%>
</body>
</html>



