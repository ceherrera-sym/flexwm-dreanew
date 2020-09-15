<%@page import="com.symgae.shared.GwtUtil"%>
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
<%@page import="java.sql.Types"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.DecimalFormat "%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.flexwm.server.FlexUtil"%>
<%@page import="com.flexwm.server.AmountByWord"%>
<%@page import="com.flexwm.server.AmountByWorden"%>
<%@page import="com.flexwm.server.AmountByWordes"%>
<%@page import="com.flexwm.server.IAmountByWord"%>
<%@page import="com.flexwm.server.NumberByWord"%>

<%@include file="../inc/login_opt.jsp" %>
<%
	String title = "Contrato Compra-Venta Casas";

	BmoPropertySale bmoPropertySale = new BmoPropertySale(); 	
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)sFParams.getBmoProgram(bmoPropertySale.getProgramCode());
	PmConn pmConnCustomer = new PmConn(sFParams);
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
<style type="text/css">
   	th, td {
  		border: 1px solid black;
	}
</style>
<head>
	<title>:::<%= title %>:::</title>
		<link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>">
</head>

<body class="default" <%= permissionPrint %> style="font-size: 12px">
<%
	try {	
		
	    // Si es llamada externa, utilizar llave desencriptada
	    int propertySaleId = 0;
	    if (isExternal) propertySaleId = decryptId;
	    else propertySaleId = Integer.parseInt(request.getParameter("foreignId"));	    
	    
	    //Venta
		PmPropertySale pmPropertySale = new PmPropertySale(sFParams);
		bmoPropertySale = (BmoPropertySale)pmPropertySale.get(propertySaleId);
		
		//Desarrollo
		BmoDevelopment bmoDevelopment = new BmoDevelopment();
		PmDevelopment pmDevelopment = new PmDevelopment(sFParams);
		bmoDevelopment= (BmoDevelopment)pmDevelopment.get(bmoPropertySale.getBmoProperty().getBmoDevelopmentBlock().getBmoDevelopmentPhase().getDevelopmentId().toInteger());
		
		//Cliente
		BmoCustomer bmoCustomer = new BmoCustomer();
		PmCustomer pmCustomer = new PmCustomer(sFParams);
		bmoCustomer = (BmoCustomer)pmCustomer.get(bmoPropertySale.getCustomerId().toInteger());
		
		//Empresa
		BmoCompany bmoCompany = new BmoCompany();
		PmCompany  pmCompany  = new PmCompany (sFParams);
		bmoCompany = (BmoCompany)pmCompany.get(bmoPropertySale.getCompanyId().toInteger());
		
		// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
		String logoURL = "";
		if (!bmoDevelopment.getLogo().toString().equals(""))
			logoURL = HtmlUtil.parseImageLink(sFParams, bmoDevelopment.getLogo());
		else
			logoURL = sFParams.getMainImageUrl();

		
		//Direcciones del Cliente
		pmConnCustomer.open();
		boolean cuad = false;
		BmoCustomerAddress bmoCustomerAddress = new BmoCustomerAddress();
		PmCustomerAddress pmCustomerAddress = new PmCustomerAddress(sFParams);
		String sqlCustAddress = " SELECT cuad_customeraddressid FROM customeraddress WHERE cuad_customerid = " + bmoPropertySale.getCustomerId().toInteger() + " ORDER BY cuad_type ASC";
		pmConnCustomer.doFetch(sqlCustAddress);
		if (pmConnCustomer.next())
			bmoCustomerAddress = (BmoCustomerAddress)pmCustomerAddress.get(pmConnCustomer.getInt("cuad_customeraddressid"));
%>

<br>
<div align="right">
	<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
</div>
<div align="justify">
	<p align="center"><b>AVISO DE PREVENCI&Oacute;N DE LAVADO DE DINERO PARA CLIENTES</b></p>
	<br>
	<ol type="1">
		<li>
			<span style="text-decoration: underline;"><b>IDENTIDAD Y DOMICILIO DEL RESPONSABLE:</b></span><br><br>
			En cumplimiento a la Ley Federal para la Prevenci&oacute;n e Identificaci&oacute;n de Operaciones con Recursos de Procedencia Il&iacute;cita (LFPIORPI), 
			y su Reglamento. <b><%= bmoCustomer.getDisplayName().toHtml()%></b> con domicilio en 
			<b><%= bmoCustomerAddress.getStreet().toHtml() %>,
		    <%= bmoCustomerAddress.getNumber().toHtml()%>,
		    <%= bmoCustomerAddress.getNeighborhood().toHtml() %>,
		    <%= bmoCustomerAddress.getBmoCity().getName().toHtml() %>, <%= bmoCustomerAddress.getBmoCity().getBmoState().getName().toString() %>,</b>
			en su car&aacute;cter de Actividad Vulnerable pone a su disposici&oacute;n 
			el presente Aviso de Prevenci&oacute;n de Lavado de Dinero (en lo sucesivo referido como "Aviso"), con el fin de informarle los 
			t&eacute;rminos conforme a los cuales se dar&aacute; cumplimiento a la presente legislaci&oacute;n. 
			<p>
		</li>
		
		<li>
			<span style="text-decoration: underline;"><b>DATOS DE IDENTIFICACI&Oacute;N DEL CLIENTE:</b></span><br><br>
			Atendiendo a lo dispuesto en el Art&iacute;culo 18 Fracci&oacute;n I, II, III, de la LFPIORPI:<br><br>
			<ul>
				<li>
					Identificaci&oacute;n de clientes y usuarios bas&aacute;ndose en credenciales o documentaci&oacute;n oficial.
				</li>
				<li>
					Brindar Informaci&oacute;n sobre su actividad u ocupaci&oacute;n, bas&aacute;ndose entre otros, 
					en los avisos de inscripci&oacute;n y actualizaci&oacute;n de actividades presentados para efectos del Registro Federal de Contribuyentes.
				</li>
				<li>
					Proporcionar informaci&oacute;n sobre el conocimiento de la existencia del due&ntilde;o beneficiario y, en su caso, exhibir documentaci&oacute;n oficial que permita identificarlo.
				</li>
			</ul>
			<p>
		</li>
		
		<li><span style="text-decoration: underline;"><b>RESTRICCI&Oacute;N AL EFECTIVO:</b></span><br><br>
			De conformidad con el <b>Art&iacute;culo 32 fracci&oacute;n I</b>, de la LFPIORPI:<br><br>
		
			"Queda prohibido dar cumplimiento a obligaciones y, en general, liquidar o pagar, as&iacute; como aceptar la liquidaci&oacute;n o el pago, 
			de actos u operaciones mediante el uso de monedas y billetes en la Constituci&oacute;n o transmisi&oacute;n de derechos reales sobre 
			bienes inmuebles por un valor igual o superior al equivalente a ocho mil veinticinco veces el salario m&iacute;nimo vigente".
		</li>
	</ol>

	<table style="width: 100%; text-align: center; border-collapse: collapse;">
		<thead style="background-color: #545454; color: white">
			<tr>
				<th>Actividad</th>
				<th>L&iacute;mite en UMA*</th>
				<th>Monto l&iacute;mite en MN**</th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td>Compraventa de inmuebles</td>
				<td>8,025</td>
				<td>$697,212.00</td>
			</tr>
		</tbody>
	</table>
	<br>
	<div align="justify">
		<b>NOTA IMPORTANTE</b>: Y por tanto, en La Valenciana Arquitectura Residencial (Multiservicios N&oacute;rdika, SA de CV) te pedimos, 
		que solamente deposites el IMPORTE de HASTA $680,000.00 (seiscientos ochenta mil pesos 00/100 MN) como m&aacute;ximo para evitar cualquier confusi&oacute;n, 
		que sobrepase este l&iacute;mite y por tanto, pueda ocasionar inconvenientes en el momento de tu escritura.
		<br><br><br>
		Atentamente,<br><br><br>
		<%= bmoDevelopment.getName().toHtml() %><br><br>
		<img src="../img/firma_CharlottDomenech_g100.png" width="100px" height="50px"> 
		<br>
		Ma Charlott Domenech
	</div>
</div>
<% 	} catch (Exception e) { 
	String errorLabel = "Error de Contrato";
	String errorText = "El Contrato no pudo ser desplegado exitosamente. Es necesario completar todos los datos faltantes: Venta de Inmuebles.";
	String errorException = e.toString();
	
	response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);
	}
	
	finally{
		pmConnCustomer.open();
	}
%>
</body>
</html>



