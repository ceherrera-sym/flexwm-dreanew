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
	String title = "P&oacute;liza de Garant&iacute;a Contra Vicios Ocultos";
	
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(Locale.US);
	
    // Obtener parametros
    int propertySaleId = 0;
    if (isExternal) propertySaleId = decryptId;
    else propertySaleId = Integer.parseInt(request.getParameter("foreignId"));    

	BmoPropertySale bmoPropertySale = new BmoPropertySale();
	PmPropertySale pmPropertySale = new PmPropertySale(sFParams);
	bmoPropertySale = (BmoPropertySale)pmPropertySale.get(propertySaleId);
	
	BmoOrder bmoOrder = new BmoOrder();
	PmOrder pmOrder = new PmOrder(sFParams);
	bmoOrder = (BmoOrder)pmOrder.get(bmoPropertySale.getOrderId().toInteger());

	//Vivienda
	BmoProperty bmoProperty = new BmoProperty();
	PmProperty pmProperty = new PmProperty(sFParams);
	bmoProperty = (BmoProperty)pmProperty.get(bmoPropertySale.getPropertyId().toInteger());
	
	//Modelo
	BmoPropertyModel bmoPropertyModel = new BmoPropertyModel();
	PmPropertyModel pmPropertyModel = new PmPropertyModel(sFParams);
	bmoPropertyModel = (BmoPropertyModel)pmPropertyModel.get(bmoProperty.getPropertyModelId().toInteger());
	
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
	
	//Empresa
	BmoCompany bmoCompany = new BmoCompany();
	PmCompany  pmCompany  = new PmCompany (sFParams);
	if(bmoDevelopment.getCompanyId().toInteger() > 0)
		bmoCompany = (BmoCompany)pmCompany.get(bmoProperty.getCompanyId().toInteger());
	
	// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
	String logoURL ="";
	if (!bmoCompany.getLogo().toString().equals(""))
		logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
	else 
		logoURL = sFParams.getMainImageUrl();
	
	//Ciudad de la Empresa
	BmoCity bmoCityCompany = new BmoCity();
	PmCity pmCity = new PmCity(sFParams);
	bmoCityCompany = (BmoCity)pmCity.get(bmoCompany.getCityId().toInteger());
			
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

//Si se visualiza y no se tienen permisos, deshabilita: copiar, pegar, seleccionar, men&uacute;(clic-derecho).
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
		<br><br><br><br><br><br><br><br><br><br><br>
		<table width="90%" align="center" style="font-size: 12px">
			<tr >
				<!--
	    		<td align="left" width="" rowspan="5" valign="top">
	    			<img border="0" width="<%//= SFParams.LOGO_WIDTH %>" height="<%//= SFParams.LOGO_HEIGHT %>" src="<%//= logoURL %>" >
	    		</td>
	    		-->
	    		<td class="reportTitleCell" style="text-align:center">
		    		P&oacute;liza de garant&iacute;a contra vicios ocultos e impermeabilizaci&oacute;n
	    		</td>
			</tr>
			<tr><td>&nbsp;</td></tr>
			<tr>
				<td>
					<p align="justify">
						Por este conducto emitimos la m&aacute;s amplia garant&iacute;a contra vicios ocultos por 2 a&ntilde;os e impermeabilizaci&oacute;n por 3 a&ntilde;os a partir de la presente fecha 
						<b><%= FlexUtil.dateToLongDate(sFParams, SFServerUtil.nowToString(sFParams, "yyyy-MM-dd"))%></b> 
						para la vivienda marcada con el n&uacute;mero <b><%= bmoProperty.getNumber().toString()%></b>  
						la calle <b><%= bmoProperty.getStreet().toString()%></b> del fraccionamiento 
					<b><%= bmoDevelopment.getName().toString()%></b> de esta ciudad.
					</p>
					
					<p align="justify">
						Esta garant&iacute;a dejara de surtir efecto, si el acreditado le da uso diferente a la vivienda, 
						para lo que fue construida que es el de casa habitaci&oacute;n, tambi&eacute;n si el acreditado deja de realizar acciones de limpieza y 
						mantenimiento necesarios a los diferentes elementos, azotea e instalaciones que forman la vivienda.
					</p>
					
					<p align="justify">
						Para hacer efectiva esta garant&iacute;a, el acreditado deber&aacute; solicitar y/o reportar la falla correspondiente al tel&eacute;fono de atenci&oacute;n al 
						cliente 717 59 05 &oacute; 01 800 0000 mdm (636) o por escrito en nuestras oficinas, describiendo ampliamente el vicio oculto y haciendo 
						referencia a la vigencia de la garant&iacute;a.
					</p>
					
					<p align="justify">
						En caso de cualquier otra duda o controversia, el acreditado y la empresa otorgante de la garant&iacute;a aceptan sujetarse a lo indicado 
						por la legislaci&oacute;n vigente enmarcada en el c&oacute;digo civil y de procedimiento del estado de
				        <% if(bmoDevelopment.getCityId().toInteger() > 0) {%>
				        <%= bmoCityDevelopment.getBmoState().getName().toString()%>.
				        <% }else {%>Guanajuato. <%}%>
					</p>
					
					<p align="left">
						<b>
					        <% if(bmoDevelopment.getCityId().toInteger() > 0) {%>
						<%= bmoCityDevelopment.getName().toString()%>, <%= bmoCityDevelopment.getBmoState().getName().toString()%>
					        <% }else {%>Le&oacute;n Guanajuato, <%}%>
						 a <%= FlexUtil.dateToLongDate(sFParams, SFServerUtil.nowToString(sFParams, "yyyy-MM-dd"))%><br>
							Nombre del propietario: <%= bmoCustomer.getDisplayName().toString()%>
							<br><br><br>
						</b>
					</p>
					
					<p align="center">
						<b>Atentamente</b>
						<br><br><br><br><br>
					</p>
					<p align="center">
						<b>
							<img src="../img/firma_lopez.jpg" width="100" height="50">
						<br>
						<%= bmoCompany.getLegalRep().toString()%>
						<br>
						Representante Legal<br>
						<%= bmoCompany.getLegalName().toString()%></b>
					</p>
				</td>
			</tr>
		</table>
		<% 
			
		} catch (Exception e) { 
				String errorLabel = "Error de Poliza de Garantia Contra Vicios Ocultos...";
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