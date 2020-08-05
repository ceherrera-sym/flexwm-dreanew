<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.server.FlexUtil"%>
<%@page import="com.symgae.server.SFServerUtil"%>
<%@page import="com.flexwm.server.AmountByWord"%>
<%@page import="com.flexwm.server.AmountByWorden"%>
<%@page import="com.flexwm.server.AmountByWordes"%>
<%@page import="com.flexwm.server.IAmountByWord"%>
<%@page import="com.flexwm.server.NumberByWord"%>
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
<%@page import="com.flexwm.server.co.PmDevelopmentPhase"%>
<%@page import="com.flexwm.shared.co.BmoDevelopmentPhase"%>
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
	String title = "Notificaci&oacute;n Pago Mantenimiento";
	
	
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(Locale.US);
	AmountByWord amountByWord = new AmountByWord();
	amountByWord.setLanguage("es");
	amountByWord.setCurrency("es");
	
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
	
		//Logo del desarrollo
		String blobKeyParse = "";
		if (bmoDevelopment.getLogo().toString().length() > 0) {
			blobKeyParse = bmoDevelopment.getLogo().toString();
			if (bmoDevelopment.getLogo().toString().indexOf(".") > 0)
				blobKeyParse = bmoDevelopment.getLogo().toString().substring(0, bmoDevelopment.getLogo().toString().indexOf("."));
		}
		
		
		BmoCompany bmoCompany = new BmoCompany();
		PmCompany pmCompany = new PmCompany(sFParams);
		bmoCompany = (BmoCompany)pmCompany.get(bmoDevelopment.getCompanyId().toInteger());
		
		// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
		String logoURL ="";
		if (!bmoCompany.getLogo().toString().equals(""))
			logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
		else 
			logoURL = sFParams.getMainImageUrl();
		
	
	//Etapa Desarrollo
	BmoDevelopmentPhase bmoDevelopmentPhase = new BmoDevelopmentPhase();
	PmDevelopmentPhase pmDevelopmentPhase = new PmDevelopmentPhase(sFParams);
	bmoDevelopmentPhase= (BmoDevelopmentPhase)pmDevelopmentPhase.get(bmoDevelopmentBlock.getDevelopmentPhaseId().toInteger());
	
	String vigenciaCosto = "";
	if(!bmoDevelopmentPhase.getFeeDueDate().toString().equals(""))
		vigenciaCosto = FlexUtil.dateToLongDate(sFParams, bmoDevelopmentPhase.getFeeDueDate().toString());
	
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

//Si se visualiza y no se tienen permisos, deshabilita: copiar, pegar, seleccionar, men&uacute; (clic-derecho).
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
		<table border="0" cellspacing="0" width="100%" cellpadding="0">
			<tr>
				<!--
				<td width="10%" align="left">
					<img src="/fileserve?blob-key=<%//= blobKeyParse %>" width="<%//= SFParams.LOGO_WIDTH %>" height="<%//= SFParams.LOGO_HEIGHT %>"><
				</td>
				-->
				<!--<td width="10%" align="left"><img src="/fileserve?blob-key=<%//= blobKeyParse %>" width="60" height="60"></td>-->
				<td align="left" width="10%" rowspan="9" valign="top">
						<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
				</td>
				<td colspan="2" class="reportTitleCell">
					<u>MANTENIMIENTO</u>
	            </td>
			</tr>
		</table>
		<br>
		<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
			<tr >
				<td >
					<p align="left">
						<b>Estimado Colono:</b><br>
					</p>
					<ol type="1" style="text-align:justify">
						<li>
							Se hace de su conocimiento, que el pago del mantenimiento correr&aacute; a partir del mes siguiente al que se haya entregado.
						</li>
							<br>
						<li>						
							Se establece que la cuota de mantenimiento es por <b><u><%= SFServerUtil.formatCurrency(bmoDevelopmentPhase.getMaintenanceCost().toDouble())%></u></b> 
							(<%= amountByWord.getMoneyAmountByWord(bmoDevelopmentPhase.getMaintenanceCost().toDouble()).toUpperCase() %>) 
							mensuales, vigente hasta el d&iacute;a <b><%= vigenciaCosto%></b>.
						</li>
							<br>
						<li>
							Se establece que se cobrar&aacute; una mora de $10.00 (DIEZ PESOS 00/100 M.N.) por d&iacute;a de atraso en el pago del mantenimiento, 
							siempre y cuando este no sea cubierto dentro de los primeros 5 d&iacute;as del mes; a partir del d&iacute;a 6 se aplicar&aacute;  la mora establecida. 
						</li>
							<br>
						<li>
							Los conceptos de mantenimiento que se abarca con el pago son:
								<ol type="1" style="text-align:justify">
									<li>
										Guardias de Seguridad.
									</li>
									<li>
										Mantenimiento de &Aacute;reas de Donaci&oacute;n.
									</li>
									<li>
										Servicios Exclusivamente de Caseta de Acceso (Mtto, Agua y Luz)
									</li>
								</ol>
						</li>
							<br>
						<li>
							Se informa que la Empresa Administradora es <b>INMOBILIARIA DAMIROS S.A. DE C.V.</b>, quien recibir&aacute;  los pagos del mantenimiento y ofrecer&aacute;  
							los servicios de administraci&oacute;n de las privadas.
						</li>
							<br>
						<li>
							Los pagos del mantenimiento deber&aacute;n realizarse mediante dep&oacute;sito o transferencia, a la cuenta de banco:
								<ol type="1" style="text-align:justify">
									<li>
										Banco: <b>BanBaj&iacute;o</b>
									</li>
									<li>
										Titular: <b>Inmobiliaria Damiros S.A. de C.V.</b>
									</li>
									<li>
										Cuenta: <b>132568540201</b>
									</li>
									<li>
										Clave Interbancaria: <b>030225900004802874</b>
									</li>
									<li>
										Referencia:  <b><%= bmoProperty.getStreet().toString()+" "+bmoProperty.getNumber().toString()%></b>
									</li>
								</ol>
						</li>
					</ol>
						
					
					<p align="justify">
						<br>
						Nota: Es indispensable que al depositar, le indique al cajero la referencia, de lo contrario no podr&aacute realizar su deposito.
						<!-- En caso que la <b>Casa Club</b> a&uacute;n no haya sido entregada, al pago de la cuota se le descontar&aacute;  la cantidad de 
						<b><u>$200.00</u></b> (doscientos pesos 00/100 mn) de forma mensual.-->
					</p>
				</td>
			</tr>
			<tr align="center" >
				<td>
					<br><br><br>
					Acepto:
				</td>
			</tr>
		</table>
		<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
			<tr align="center" >
				<td>
					<br><br><br>
					______________________________________
				</td>
			</tr>
			<tr align="center" >
				<td>
					<b>EL COMPRADOR</b><br>
				</td>
			</tr>
			<tr align="center" >
				<td>
					<%= bmoCustomer.getDisplayName().toString() %>
				</td>
			</tr>
		</table>
		<p><br><p>
		<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
			<tr >
				<td>
				Cualquier duda o comentario a: Clientes@mdm.mx y a los tels: 01 477 2 12 12 12 y 01 477 2 12 12 16
				 				
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