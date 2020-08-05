<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.server.FlexUtil"%>
<%@page import="com.symgae.server.SFServerUtil"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.flexwm.server.co.PmOrderProperty"%>
<%@page import="com.flexwm.shared.co.BmoOrderProperty"%>
<%@page import="com.flexwm.server.op.PmOrder"%>
<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@page import="com.flexwm.server.co.PmPropertySale"%>
<%@page import="com.flexwm.shared.co.BmoPropertySale"%>
<%@page import="com.flexwm.server.co.PmProperty"%>
<%@page import="com.flexwm.shared.co.BmoProperty"%>
<%@page import="com.flexwm.server.co.PmPropertyType"%>
<%@page import="com.flexwm.shared.co.BmoPropertyType"%>
<%@page import="com.flexwm.server.co.PmPropertyModel"%>
<%@page import="com.flexwm.shared.co.BmoPropertyModel"%>
<%@page import="com.flexwm.server.co.PmDevelopmentBlock"%>
<%@page import="com.flexwm.shared.co.BmoDevelopmentBlock"%>
<%@page import="com.flexwm.server.co.PmDevelopment"%>
<%@page import="com.flexwm.shared.co.BmoDevelopment"%>
<%@page import="com.symgae.server.sf.PmUser"%>
<%@page import="com.symgae.shared.sf.BmoUser"%>
<%@page import="com.flexwm.server.cm.PmCustomerAddress"%>
<%@page import="com.flexwm.shared.cm.BmoCustomerAddress"%>
<%@page import="com.flexwm.server.cm.PmCustomer"%>
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="com.flexwm.shared.wf.BmoWFlowType"%>
<%@page import="com.flexwm.server.wf.PmWFlowType"%>
<%@include file="../inc/login_opt.jsp" %>

<%
try {
	String title = "Carta de Programaci&oacute;n de Entrega de Vivienda Celaya";
	
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(Locale.US);
		
    // Obtener parametros
    int propertySaleId = 0;
    if (isExternal) propertySaleId = decryptId;
    else propertySaleId = Integer.parseInt(request.getParameter("foreignId"));    

	BmoPropertySale bmoPropertySale = new BmoPropertySale();
	PmPropertySale pmPropertySale = new PmPropertySale(sFParams);
	bmoPropertySale = (BmoPropertySale)pmPropertySale.get(propertySaleId);
	
	BmoWFlowType bmoProjectWFlowType = new BmoWFlowType();
	PmWFlowType pmWFlowType = new PmWFlowType(sFParams);
	bmoProjectWFlowType = (BmoWFlowType)pmWFlowType.get(bmoPropertySale.getWFlowTypeId().toInteger());
	
	BmoOrder bmoOrder = new BmoOrder();
	PmOrder pmOrder = new PmOrder(sFParams);
	bmoOrder = (BmoOrder)pmOrder.get(bmoPropertySale.getOrderId().toInteger());
	
	//if (bmoOrder.getStatus().toChar() != BmoOrder.STATUS_AUTHORIZED) throw new Exception("El pedido no esta autorizado - no se puede desplegar.");
	//if (!(bmoOrder.getWFlowTypeId().toInteger() > 0)) throw new Exception("El pedido no cuenta con el tipo de efecto - no se puede desplegar.");
	
	//Vivienda
	BmoProperty bmoProperty = new BmoProperty();
	PmProperty pmProperty = new PmProperty(sFParams);
	bmoProperty = (BmoProperty)pmProperty.get(bmoPropertySale.getPropertyId().toInteger());
	
	//Modelo
	BmoPropertyModel bmoPropertyModel = new BmoPropertyModel();
	PmPropertyModel pmPropertyModel = new PmPropertyModel(sFParams);
	bmoPropertyModel = (BmoPropertyModel)pmPropertyModel.get(bmoProperty.getPropertyModelId().toInteger());
	
	//Modelo
	BmoPropertyType bmoPropertyType = new BmoPropertyType();
	PmPropertyType pmPropertyType = new PmPropertyType(sFParams);
	bmoPropertyType = (BmoPropertyType)pmPropertyType.get(bmoPropertyModel.getPropertyTypeId().toInteger());
	
	
	//Manzana
	BmoDevelopmentBlock bmoDevelopmentBlock = new BmoDevelopmentBlock();
	PmDevelopmentBlock pmDevelopmentBlock = new PmDevelopmentBlock(sFParams);
	bmoDevelopmentBlock = (BmoDevelopmentBlock)pmDevelopmentBlock.get(bmoProperty.getDevelopmentBlockId().toInteger());
			
	//Desarrollo
	BmoDevelopment bmoDevelopment = new BmoDevelopment();
	PmDevelopment pmDevelopment = new PmDevelopment(sFParams);
	bmoDevelopment = (BmoDevelopment)pmDevelopment.get(bmoPropertyModel.getDevelopmentId().toInteger());
	
	//Ciudad del Desarrollo
	BmoCity bmoCityDevelopment = new BmoCity();
	PmCity pmCityDevelopment = new PmCity(sFParams);
	bmoCityDevelopment = (BmoCity)pmCityDevelopment.get(bmoPropertyModel.getBmoDevelopment().getCityId().toInteger());			
	

	//Cliente
	BmoCustomer bmoCustomer = new BmoCustomer();
	PmCustomer pmCustomer = new PmCustomer(sFParams);
	bmoCustomer = (BmoCustomer)pmCustomer.get(bmoPropertySale.getCustomerId().toInteger());
	
	String customer = "";
	if (bmoCustomer.getCustomertype().toChar() == BmoCustomer.TYPE_PERSON) {
		customer =  bmoCustomer.getFirstname().toHtml() + " " +
					bmoCustomer.getFatherlastname().toHtml() + " " +
					bmoCustomer.getMotherlastname().toHtml();
	} else {
		customer = bmoCustomer.getLegalname().toHtml();
	}
		
	
	//Vendedor
	String vendedor="";
	BmoUser bmoSalesUser = new BmoUser();
	PmUser pmSalesUser = new PmUser(sFParams);
	bmoSalesUser = (BmoUser)pmSalesUser.get(bmoPropertySale.getSalesUserId().toInteger());
	//vendedor = bmoSalesUser.getFirstname().toString() + " " + bmoSalesUser.getFatherlastname() + " " + bmoSalesUser.getMotherlastname();
	vendedor =  bmoSalesUser.getCode().toString();

	//Coordinador
	String coordinador="";
	BmoUser bmoCoordinadorUser = new BmoUser();
	PmUser pmCoordinadorUser = new PmUser(sFParams);
	if(bmoSalesUser.getParentId().toInteger() > 0){
		bmoCoordinadorUser = (BmoUser)pmCoordinadorUser.get(bmoSalesUser.getParentId().toInteger());
		//coordinador = bmoCoordinadorUser.getFirstname().toString() + " " + bmoCoordinadorUser.getFatherlastname().toString() + " " + bmoCoordinadorUser.getMotherlastname().toString();
		coordinador =  bmoCoordinadorUser.getCode().toString();
	}
	
	//Gerente
	String gerente="";
	BmoUser bmoGerenteUser = new BmoUser();
	PmUser pmGerenteUser = new PmUser(sFParams);
	if(bmoCoordinadorUser.getParentId().toInteger() > 0){
		bmoGerenteUser = (BmoUser)pmGerenteUser.get(bmoCoordinadorUser.getParentId().toInteger());
		//gerente = bmoGerenteUser.getFirstname().toString() + " " + bmoGerenteUser.getFatherlastname().toString() + " " + bmoGerenteUser.getMotherlastname().toString();
		gerente =  bmoGerenteUser.getCode().toString();
	}

	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)sFParams.getBmoProgram(bmoOrder.getProgramCode());	
	
	BmoCompany bmoCompany = new BmoCompany();
	PmCompany pmCompany = new PmCompany(sFParams);
	bmoCompany = (BmoCompany)pmCompany.get(bmoOrder.getCompanyId().toInteger());
	
	// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
	String logoURL ="";
	if (!bmoCompany.getLogo().toString().equals(""))
		logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
	else 
		logoURL = sFParams.getMainImageUrl();

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
		<p><br><p><br><p><br><p><br><p>
		<table border="1" cellspacing="0" width="100%" cellpadding="0" rules="all">
			<tr>
				<td width="20%" align="left">
					<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
				</td>
				<td width="60%" class="documentTitle" align="center">
					Seccion 7.5<br>
					Carta de Programaci&oacute;n de Entrega de Vivienda<br>
					Tipo de cr&eacute;dito: <%= bmoProjectWFlowType.getName().toString() %><br>
					Modelo: <%= bmoPropertyModel.getName().toString() + "&nbsp;("+bmoPropertyModel.getCode().toString() + ")" %><br>
					Tipo Casa: <%= bmoPropertyType.getName().toString() %>
				</td>
				
				<td width="20%" class="documentComments" align="right">
					C&oacute;digo del Documento: FO-07.5.4-4 <br> 
			    	Fecha de Efectividad:<br>15-Oct-04<br>
			    	No. Cliente: <%= bmoCustomer.getCode().toString()%><br>
			    	Vendedor:&nbsp;<%= vendedor %>
			    	Cordinador:&nbsp;<%= coordinador%>
			    	Gerente:&nbsp;<%= gerente%>
				</td>
			</tr>
		</table>
		<br>
		<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
			<tr>
				<td class="documentLabel" align="right">
					&nbsp;<%= bmoCityDevelopment.getName().toHtml()%>, <%= bmoCityDevelopment.getBmoState().getName().toHtml()%>, a
					<%= FlexUtil.dateToLongDate(sFParams, SFServerUtil.nowToString(sFParams, "yyyy-MM-dd"))%>
				</td>
			</tr>
			<tr class="documentText">
				<td >
					<p align="left">
						AT'N:<br>				
						Atenci&oacute;n al cliente<br>
						P r e s e n t e.
					</p>
					<p align="justify">
						Agradecer&eacute; a usted proceder a la entrega de la casa ubicada en la ubicada en la 
						<b><%= bmoDevelopmentBlock.getName().toString().toUpperCase() %></b>, 
						lote <b><%= bmoProperty.getLot().toString().toUpperCase() %></b> 
						con domicilio en <b><%= bmoProperty.getStreet().toString()%> <%= bmoProperty.getNumber().toString()%>,</b> 
						del fraccionamiento <b><%= bmoDevelopment.getName().toString().toUpperCase() %></b> de esta ciudad.
					</p>
					<p align="justify">
						Lo anterior derivado de que el Sr.(a) <b><%= bmoCustomer.getDisplayName().toString() %></b>
						Propietario de la vivienda se encuentra al corriente en sus tr&aacute;mites ante esta empresa.
					</p>
					<p align="justify">
						Agradeciendo de antemano las atenciones prestadas a la presente, quedo de usted.
					</p>			
					<p align="justify">
						Nota: Deber&aacute; llevar al momento de la entrega de sus llaves original y copia de su credencial de elector.
					</p>
					<p>&nbsp;</p>
				</td>
			</tr>
			<tr class="documentText">
				<td>
					<b>La vivienda debe contar con caracter&iacute;sticas de ecotecnolog&iacute;a.</b>
				</td>
			</tr>
			<%
			double mtsPrivativos = bmoProperty.getLandSize().toDouble() - bmoPropertyModel.getLandSize().toDouble();
			double mtsConst = bmoProperty.getConstructionSize().toDouble() - bmoPropertyModel.getConstructionSize().toDouble();
			if(mtsPrivativos > 0 || mtsConst > 0){
				%>
				<tr class="documentText">
					<td>
						&nbsp;
						<p><b>Vivienda con Metros Adicionales: </b></p>
						<p><b>Terreno Excedente:</b> <%= Math.round(mtsPrivativos *100)/100.0d%></p>
						<p><b>Construcci&oacute;n:</b> <%= Math.round(mtsConst *100)/100.0d%></p>
						<p><br></p><p><br></p><p><br></p>
					</td>
				</tr>
				<% } %>
				<tr align="center" class="documentText">
					<td>
					Recib&iacute; Original
					</td>
				</tr>
				<tr align="center" class="documentText">
					<td>
						<br><br><br>
						______________________________________
					</td>
				</tr>
				<tr align="center" class="documentText">
					<td>
						<b><%= bmoCustomer.getDisplayName().toString() %></b>
					</td>
				</tr>
		</table>
		
		<p style="page-break-after: always">&nbsp;</p>	
		<p><br><p><br><p><br><p><br><p>
		<table border="1" cellspacing="0" width="100%" cellpadding="0" rules="all">
			<tr>
				<td width="20%" align="left">
					<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
				</td>
				<td width="60%" class="documentTitle" align="center">
					Seccion 7.5<br>
					Oficio para contrataci&oacute;n de servicio de energ&iacute;a el&eacute;ctrica<br>
				</td>
				
				<td width="20%" class="documentComments" align="right">
					C&oacute;digo del Documento: FO-07.5.4-6<br> 
			    	Fecha de Efectividad: 15-Oct-04<br>
			    	No. Cliente: <%= bmoCustomer.getCode().toString()%><br>
				</td>
			</tr>
		</table>
		<br>
		<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
			<tr>
				<td class="documentLabel" align="right">
					&nbsp;<%= bmoCityDevelopment.getName().toHtml()%>, <%= bmoCityDevelopment.getBmoState().getName().toHtml()%>, a
					<span class="documentText">
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					</span>
				</td>
			</tr>
			<tr class="documentText">
				<td >
					<p align="left">
						<b>CFE</b><br>				
						Contratos<br>	
					</p>
					<p align="justify">
						En relaci&oacute;n a los convenios celebrados con ustedes y pagos de derechos ya realizados, mucho agradecer&eacute; se sirvan aceptar al 
						<b>C. <%= bmoCustomer.getDisplayName().toHtml()%></b> para que pueda realizar su contrataci&oacute;n del servicio de energ&iacute;a
						el&eacute;ctrica en el domicilio <b><%= bmoProperty.getStreet().toString()%> <%= bmoProperty.getNumber().toString()%>,</b> 
						lote <b><%= bmoProperty.getLot().toString().toUpperCase() %></b> 
						<b><%= bmoDevelopmentBlock.getName().toString().toUpperCase() %></b>, 
						del fraccionamiento <b>
							<input type="text"  size="35" style="font-weight: bold; border: 0" class="documentText" value="<%= bmoDevelopment.getName().toString().toUpperCase() %>">, de esta ciudad.	
						</b> que se encuentra entre las calles
						<input type="text" size="35" style="border: 0" class="documentText">, de esta ciudad.
					</p>
					<p align="justify">
						Agradeciendo de antemano las atenciones prestadas a la presente, quedo de ustedes.
					</p>			
					<p>&nbsp;</p>
				</td>
			</tr>			
			<tr align="center" class="documentText">
				<td>
				Recib&iacute; Original
				</td>
			</tr>
			<tr align="center" class="documentText">
				<td>
					<br><br><br>
					______________________________________
				</td>
			</tr>
			<tr align="center" class="documentText">
				<td>
					<b>
						Ing. Erick Luviano H.<br>
						Gte. Calidad y Atenci&oacute;n al Cliente
					</b>
				</td>
			</tr>
		</table>
		
		<p style="page-break-after: always">&nbsp;</p>	
		<p><br><p><br><p><br><p><br><p>
		<table border="1" cellspacing="0" width="100%" cellpadding="0" rules="all">
			<tr>
				<td width="20%" align="left">
					<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
				</td>
				<td width="60%" class="documentTitle" align="center">
					Seccion 7.5<br>
					Oficio para contrataci&oacute;n de servicio de agua<br>
				</td>
				
				<td width="20%" class="documentComments" align="right">
					C&oacute;digo del Documento: FO-07.5.4-5<br> 
			    	Fecha de Efectividad: 15-Oct-04<br>
			    	No. Cliente: <%= bmoCustomer.getCode().toString()%><br>
				</td>
			</tr>
		</table>
		<br>
		<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
			<tr>
				<td class="documentLabel" align="right">
					&nbsp;<%= bmoCityDevelopment.getName().toHtml()%>, <%= bmoCityDevelopment.getBmoState().getName().toHtml()%>, a
					<span class="documentText">
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					</span>
				</td>
			</tr>
			<tr class="documentText">
				<td >
					<p align="left">
						<b>JUMAPA</b><br>				
						Contratos<br>	
					</p>
					<p align="justify">
						En relaci&oacute;n a los convenios celebrados con ustedes y pagos de derechos ya realizados, mucho agradecer&eacute; se sirvan aceptar al 
						<b>C. <%= bmoCustomer.getDisplayName().toHtml()%></b> para que pueda realizar su contrataci&oacute;n del servicio de toma
						de agua en el domicilio <b><%= bmoProperty.getStreet().toString()%> <%= bmoProperty.getNumber().toString()%>,</b> 
						lote <b><%= bmoProperty.getLot().toString().toUpperCase() %></b> 
						<b><%= bmoDevelopmentBlock.getName().toString().toUpperCase() %></b>, 
						del fraccionamiento <b>
						<input type="text" size="35" style="font-weight: bold; border: 0" class="documentText" value="<%= bmoDevelopment.getName().toString().toUpperCase() %>">, de esta ciudad.
						</b> que se encuentra entre las calles
						<input type="text" size="35" style="border: 0" class="documentText">, de esta ciudad.
					</p>
					<p align="justify">
						Agradeciendo de antemano las atenciones prestadas a la presente, quedo de ustedes.
					</p>			
					<p>&nbsp;</p>
				</td>
			</tr>			
			<tr align="center" class="documentText">
				<td>
				Recib&iacute; Original
				</td>
			</tr>
			<tr align="center" class="documentText">
				<td>
					<br><br><br>
					______________________________________
				</td>
			</tr>
			<tr align="center" class="documentText">
				<td>
					<b>
						Ing. Erick Luviano H.<br>
						Gte. Calidad y Atenci&oacute;n al Cliente
					</b>
				</td>
			</tr>
		</table>
		
		<p style="page-break-after: always">&nbsp;</p>	
		<p><br><p><br><p><br><p><br><p>
		<table border="1" cellspacing="0" width="100%" cellpadding="0" rules="all">
			<tr>
				<td width="20%" align="left">
					<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
				</td>
				<td width="60%" class="documentTitle" align="center">
					Seccion 7.5<br>
					Recibio de Oficios para contrataci&oacute;n de servicios<br>
				</td>
				
				<td width="20%" class="documentComments" align="right">
					C&oacute;digo del Documento: FO-07.5.4-7<br> 
			    	Fecha de Efectividad: 15-Oct-04<br>
			    	No. Cliente: <%= bmoCustomer.getCode().toString()%><br>
				</td>
			</tr>
		</table>
		<br>
		<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
			<tr>
				<td class="documentLabel" align="right">
					&nbsp;<%= bmoCityDevelopment.getName().toHtml()%>, <%= bmoCityDevelopment.getBmoState().getName().toHtml()%>, a
					<%= FlexUtil.dateToLongDate(sFParams, SFServerUtil.nowToString(sFParams, "yyyy-MM-dd"))%>
				</td>
			</tr>
			<tr class="documentText">
				<td >
					<p align="left">
						&nbsp;<br>	
					</p>
					<p align="justify">
						Recib&iacute; a entera satisfacci&oacute;n las cartas para contrataci&oacute;n de los servicios generales de agua y luz de la
						vivienda ubicada en el domicilio <b><%= bmoProperty.getStreet().toString()%> <%= bmoProperty.getNumber().toString()%>,</b> 
						lote <b><%= bmoProperty.getLot().toString().toUpperCase() %></b> 
						<b><%= bmoDevelopmentBlock.getName().toString().toUpperCase() %></b>, 
						del fraccionamiento <b><%= bmoDevelopment.getName().toString().toUpperCase() %></b>, de esta ciudad.
					</p>		
					<p>&nbsp;</p>
				</td>
			</tr>			
			<tr align="center" class="documentText">
				<td>
				Atentamente
				</td>
			</tr>
			<tr align="center" class="documentText">
				<td>
					<br><br><br>
					______________________________________
				</td>
			</tr>
			<tr align="center" class="documentText">
				<td>
					<b>
						<%= bmoCustomer.getDisplayName().toHtml()%>
					</b>
				</td>
			</tr>
		</table>
		
		<p style="page-break-after: always">&nbsp;</p>	
		<p><br><p><br><p><br><p><br><p>
		<table border="1" cellspacing="0" width="100%" cellpadding="0" rules="all">
			<tr>
				<td width="20%" align="left">
					<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
				</td>
				<td width="60%" class="documentTitle" align="center">
					Oficio para contrataci&oacute;n de servicio de gas natural<br>
				</td>
				
				<td width="20%" class="documentComments" align="right">
			    	No. Cliente: <%= bmoCustomer.getCode().toString()%><br>
				</td>
			</tr>
		</table>
		<br>
		<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
			<tr>
				<td class="documentLabel" align="right">
					&nbsp;<%= bmoCityDevelopment.getName().toHtml()%>, <%= bmoCityDevelopment.getBmoState().getName().toHtml()%>, a
					<span class="documentText">
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					</span>
				</td>
			</tr>
			<tr class="documentText">
				<td >
					<p align="left">
						<b>Gas Natural M&eacute;xico, S.A. de C.V.</b><br>				
						Contratos<br>	
					</p>
					<p align="justify">
						En relaci&oacute;n al convenio celebrado con ustedes y pago de derechos ya realizados, mucho agradecer&eacute; se sirvan aceptar al 
						<b>C. <%= bmoCustomer.getDisplayName().toHtml()%></b> para que pueda realizar su contrataci&oacute;n del servicio de gas
						natural en el domicilio <b><%= bmoProperty.getStreet().toString()%> <%= bmoProperty.getNumber().toString()%>,</b> 
						lote <b><%= bmoProperty.getLot().toString().toUpperCase() %></b> 
						<b><%= bmoDevelopmentBlock.getName().toString().toUpperCase() %></b>, 
						que se encuentra entre las calles
						<input type="text" id="texto" size="35" style="border: 0" class="documentText">, 
					    del fraccionamiento <b><%= bmoDevelopment.getName().toString().toUpperCase() %></b> de esta ciudad.
					</p>
					<p align="justify">
						Agradeciendo de antemano las atenciones prestadas a la presente, quedo de ustedes.
					</p>			
					<p>&nbsp;</p>
				</td>
			</tr>			
			<tr align="center" class="documentText">
				<td>
					&nbsp;
				</td>
			</tr>
			<tr align="center" class="documentText">
				<td>
					<br><br><br>
					______________________________________
				</td>
			</tr>
			<tr align="center" class="documentText">
				<td>
					<b>
						Ing. Erick Luviano H.<br>
						Gte. Calidad y Atenci&oacute;n al Cliente
					</b>
				</td>
			</tr>
		</table>
			<br>
		<table border="0" cellspacing="0" width="100%" cellpadding="0">
			<tr class="documentText">
				<td align="left" width="15%">
					<b>
						Comercializadora "Reyes Reyna"<br>
						Tel&eacute;fonos para contrataciones:
					</b>
				</td>
				<td witdh="85%">
					&nbsp;
				</td>
			</tr>
			<tr align="left" class="documentText">
				<td>
					&nbsp;
				</td>
				<td>
					<b>
						(477) 104 4616<br>
						(477) 776 4616<br>
						(477) 135 6204 Cel.
					</b>
				</td>
			</tr>
		</table>
		<% 
		
		} catch (Exception e) { 
				String errorLabel = "Error de Carta de Prog";
				String errorText = "El documento no puede ser desplegado.";
				String errorException = e.toString();
				//response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);
		%>
			<%= errorException %>
		<%
			}
		%>

</body>
</html>