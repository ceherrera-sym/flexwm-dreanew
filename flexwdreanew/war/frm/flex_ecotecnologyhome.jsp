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
<%@page import= "java.util.Calendar"%>
<%@page import="java.sql.Types"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.DecimalFormat "%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.flexwm.server.FlexUtil"%>

<%@include file="../inc/login_opt.jsp" %>

<%
	String title = "";
	 	
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
	<title>:::<%= appTitle %>:::</title>
    <link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>"> 
     <link rel="stylesheet" type="text/css" href="../css/flexwm.css"> 
</head>
<body class="default" <%= permissionPrint %> style="font-size: 11px">

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
		
		//Vivienda
		BmoProperty bmoProperty = new BmoProperty();
		PmProperty pmProperty = new PmProperty(sFParams);
		bmoProperty = (BmoProperty)pmProperty.get(bmoPropertySale.getPropertyId().toInteger());
		
		//Modelo
		BmoPropertyModel bmoPropertyModel = new BmoPropertyModel();
		PmPropertyModel pmPropertyModel = new PmPropertyModel(sFParams);
		bmoPropertyModel = (BmoPropertyModel)pmPropertyModel.get(bmoProperty.getPropertyModelId().toInteger());
		
		//Ciudad del Desarrollo
		BmoCity bmoCityDevelopment = new BmoCity();
		PmCity pmCityDevelopment = new PmCity(sFParams);
		bmoCityDevelopment = (BmoCity)pmCityDevelopment.get(bmoPropertyModel.getBmoDevelopment().getCityId().toInteger());
		
		//Cliente
		BmoCustomer bmoCustomer = new BmoCustomer();
		PmCustomer pmCustomer = new PmCustomer(sFParams);
		bmoCustomer = (BmoCustomer)pmCustomer.get(bmoPropertySale.getCustomerId().toInteger());
	
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

		//Ciudad de la Empresa
		BmoCity bmoCityCompany = new BmoCity();
		PmCity pmCity = new PmCity(sFParams);
		bmoCityCompany = (BmoCity) pmCity.get(bmoCompany.getCityId().toInteger());

		//Vendedor
		BmoUser bmoSalesUser = new BmoUser();
		PmUser pmSalesUser = new PmUser(sFParams);
		bmoSalesUser = (BmoUser) pmSalesUser.get(bmoPropertySale.getSalesUserId().toInteger());

		//Miscelaneas 
		DecimalFormat df = new DecimalFormat("###.##");
%>

<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr>
		<td>
			<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
		</td>
		<td class="reportTitleCell">
			ANEXO 4 DEL CONTRATO DE PROMESA DE COMPRA VENTA VIVIENDA CON ECOTECNOLOG&Iacute;A
		</td>
		<td class="reportTitleCell" style="text-align:right; font-size: 12px">
			C&oacute;digo del Documento:<br>FO-07.2.2-3(01-Ene-09)
		</td>
	</tr>
</table>

<p >
	<b>
		<%= bmoCompany.getLegalName().toString().toUpperCase() %>
	</b>
	<br>
	PRESENTE
</p>

<p ></p>
<p  align="justify">
	EL COMPRADOR INDICA QUE FUE INFORMADO POR EL VENDEDOR QUE POR DISPOSICIONES DEL GOBIERNO FEDERAL TODAS LAS VIVIENDAS
	QUE SEAN ADQUIRIDAS V&Iacute;A CR&Eacute;DITO OTORGADO POR LA SOCIEDAD HIPOTECARIA FEDERAL Y/O POR EL INFONAVIT O QUE UTILICEN SUBSIDIO FEDERAL 
	PARA SU PAGO, DEBERAN CONTAR CON ELEMENTOS AHORRADORES DE ENERG&Iacute;A Y AGUA, ESTAS VIVIENDAS CON ELEMENTOS AHORRADORES SE DENOMINAN 
	&ldquo;VIVIENDAS CON ECOTECNOLOG&Iacute;A&ldquo;.
</p>

<p  align="justify">
	EN BASE A LO DETALLADO EN EL P&Aacute;RRAFO ANTERIOR, EL COMPRADOR RECONOCE QUE LA VIVIENDA QUE ADQUIERE AL VENDEDOR CUENTA CON ELEMENTOS 
	ECOTECNOL&Oacute;GICOS AHORRADORES DE GAS, ENERG&Iacute;A EL&Eacute;CTRICA Y AGUA, ELEMENTOS QUE SE MENCIONAN M&Aacute;S ADELANTE Y QUE FORMAN PARTE INTEGRANTE 
	DEL MISMO COMO SI A LA LETRA SE INSERTASEN.
</p>
	
<p  align="justify">
	EL COMPRADOR ESTA ENTERADO Y ES CONCIENTE QUE DENTRO DE LOS  ELEMENTOS AHORRADORES DE ENERG&Iacute;A Y AGUA INSTALADOS EN SU VIVIENDA SE 
	CONSIDERA UN CALENTADOR  DE AGUA QUE UTILIZA ENERG&Iacute;A SOLAR. EL COMPRADOR RECONOCE QUE ESTA ENTERADO QUE SU VIVIENDA CUENTA CON UN
	CALENTADOR SOLAR Y POR LO TANTO A LA FIRMA DE ESTE CONTRATO ACEPTA LA RESPONSABILIDAD TOTAL DEL MANTENIMIENTO Y CUIDADO DEL CITADO
	CALENTADOR SOLAR, ESTE CALENTADOR SOLAR DEBERA SER PROTEGIDO POR EL COMPRADOR PARA NO SUFRIR GOLPES, PEDRADAS, RAMAS DE &Aacute;RBOLES, 
	GRANIZO, ETC. O ACTOS DE VANDALISMO QUE PUDIERAN ROMPER LA CUBIERTA O PARTES DEL MISMO, LO CUAL PODR&Iacute;A PROPICIAR AFECTACI&Oacute;N A LA 
	OPERACI&Oacute;N Y/O FUNCIONAMIENTO DEL MISMO.
</p>

 <p  align="justify">
	EL COMPRADOR ESTA TOTALMENTE CONCIENTE QUE EL CALENTADOR SOLAR CUENTA CON UNA GARANT&Iacute;A OTORGADA POR EL PROVEEDOR DEL MISMO, GARANT&Iacute;A
	QUE CUBRE DEFECTOS DE MATERIALES Y/O FALLAS EN LA INSTALACI&Oacute;N. ESTA GARANT&Iacute;A NO CUBRE ROTURAS DEL VIDRIO DE CUBIERTA O DE ELEMENTOS 
	QUE COMPONEN EL CALENTADOR SOLAR, ROTURAS QUE PUDIERAN DERIVARSE DE ACTOS DE VANDALISMO (PEDRADAS, RAMAS DE &Aacute;RBOLES, ETC.), O CAUSADAS 
	POR FEN&Oacute;MENOS NATURALES COMO PUEDE SER GRANIZO, ETC., EN ESTOS SUPUESTOS LA REPARACI&Oacute;N Y/O REEMPLAZO DE PARTES DEL CALENTADOR ES ENTERA 
	Y TOTAL RESPONSBILIDAD DEL COMPRADOR DE LA VIVIENDA.
</p>
	
<p  align="justify">
	EN BASE A LO DETALLADO EN EL P&Aacute;RRAFO QUE ANTECEDE Y LO MENCIONADO  CORRESPONDIENTE A ELEMENTOS AHORRADORES DE ENERG&Iacute;A Y AGUA, EL VENDEDOR 
	ACEPTA EN ESTE ACTO LA REPONSABILIDAD TOTAL PARA EL MANTENIMIENTO Y PROTECCI&Oacute;N DE ESTOS ELEMENTOS.
</p>
	
<p  align="justify">
	LA &ldquo;VIVIENDA CON ECOTECNOLOG&Iacute;A&ldquo; CUENTA CON LOS ELEMENTOS AHORRADORES DE ENERG&Iacute;A EL&Eacute;CTRICA, GAS Y AGUA QUE A CONTINUACI&Oacute;N SE DETALLAN:	
<ol type="a" style="text-align:justify;">
		<li>
			<strong><u>FOCOS AHORRADORES DE ENERG&Iacute;A:</u><br>
			<u>DESCRIPCI&Oacute;N:</u></p></strong>
			
			<p>CARACTER&Iacute;STICAS:<br>
			<strong>AHORRAN ENERG&Iacute;A EL&Eacute;CTRICA, TIENEN UNA VIDA APROXIMADA DE 10 VECES M&Aacute;S QUE LOS FOCOS CONVENCIONALES Y CON UN MISMO GRADO DE ILUMINACI&Oacute;N.</strong></p>
			<p>
			MANTENIMIENTO:<br>
			<strong>LIMPIEZA CADA 30 D&Iacute;AS</strong></p> 
			<p>
			CUIDADOS: <br>
			<strong>LIMPIEZA Y/O CAMBIO</strong>
		</li>
	<li>
		<p>
			<strong>
				<u>CALENTADOR SOLAR:</u><br>
				<u>DESCRIPCI&Oacute;N:</u>
			</strong>
		</p>
		<p>
			CARACTER&Iacute;STICAS:<br>
			<strong>AHORRO CONSIDERABLE EN GAS, YA QUE EL AGUA ES CALENTADA CON ENERG&Iacute;A SOLAR. 
			CUENTA CON UN CALENTADOR DE RESPALDO DE GAS PARA REFORZAR LA TEMPERATURA EN CASO DE QUE SEA REQUERIDO.</strong>
		</p>
		<p>
			MANTENIMIENTO:<br> 
			<strong>LIMPIEZA CONSTANTE DEL PANEL, EVITANDO ACUMULACI&Oacute;N DE POLVO O CUALQUIER OTRO 
			ELEMENTO QUE IMPIDA LA CAPTACI&Oacute;N SOLAR.</strong>
		</p>
		<p>
			CUIDADOS:<br>
			<strong>SE RECOMIENDA INSTALAR UNA MALLA PARA PROTECCI&Oacute;N CONTRA VANDALISMO (PIEDRAS, RAMAS, ETC.) Y/O FEN&Oacute;MENOS NATURALES (GRANIZO, RAYOS, ETC.) , YA QUE LA GARANT&Iacute;A DEL EQUIPO ES SOLO CONTRA DEFECTOS DE INSTALACI&Oacute;N Y/O FABRICACI&Oacute;N (MAL FUNCIONAMIENTO), MAS  NO CONTRA ACTOS VAND&Aacute;LICOS Y/O FEN&Oacute;MENOS NATURALES, EN ESTOS SUPUESTOS ES TOTAL RESPONSABILIDAD DEL COMPRADOR EL REPARAR A SU COSTO EL O LOS ELEMENTOS DA&Ntilde;ADOS DEL CALENTADOR SOLAR.</strong>
		</p>
	</li>
	<li>
		<p>
			<strong><u>ELEMENTOS AHORRADORES DE AGUA:</u></strong><br>
			<u><strong>DESCRIPCI&Oacute;N:</strong></u>
		</p>
		<p>
			CARACTERISTICAS:<br>
			<strong>AHORRO EN EL CONSUMO DE AGUA, ES DECIR ES UN DISPOSITIVO REDUCTOR DE FLUJO QUE EMITE LA CANTIDAD DE AGUA NECESARIA PARA EL SERVICIO DESEADO.</strong>
		</p>
		<p>
			MANTENIMIENTO:<br>
			<strong>LIMPIEZA CADA 30 D&Iacute;AS</strong>
		</p>
		<p>
			CUIDADOS:<br>
			<strong>LIMPIEZA</strong>
		</p>
	</li>
</ol>
</p>
<p  align="justify">
	FIRMADO EN 
	<b>
		<%= bmoCityCompany.getName().toString().toUpperCase() %>,
		<%= bmoCityCompany.getBmoState().getName().toString().toUpperCase() %>,
		A <%= FlexUtil.dateToLongDate(sFParams, SFServerUtil.nowToString(sFParams, sFParams.getDateFormat())).toUpperCase()%>
	</b>
</p>
<table width="70%" align="center" style="font-size: 11px" >
   <tr>
       <td align="center">
	       	&nbsp;			        	
       </td>
       <td align="center">
			<img  src="../img/firma_lopez.jpg" width="100" height="50">
			<br>
       </td>
   </tr>    
   <tr>
       <td align="center">
           ______________________________
       </td>
       <td align="center">
       	
           ______________________________
       </td>
   </tr>    
   <tr>
       <td align="center">
           EL PROMITENTE COMPRADOR
       </td>
       <td align="center">
           EL PROMITENTE VENDEDOR
       </td>
   </tr>
   <tr>
       <td align="center">
           <b>	<%= bmoCustomer.getFatherlastname().toString().toUpperCase() %>
				<%= bmoCustomer.getMotherlastname().toString().toUpperCase() %>
				<%= bmoCustomer.getFirstname().toString().toUpperCase() %></b>
       </td>
       <td align="center">
           <b><%= bmoCompany.getLegalName().toString().toUpperCase() %></b>
       </td>
   </tr>
   <tr>
       <td>
           &nbsp;
       </td>
       <td>
           &nbsp;
       </td>
   </tr>
   <tr>
       <td>
           &nbsp;
       </td>
       <td>
           &nbsp;
       </td>
   </tr>
   <tr>
       <td align="center">
           ______________________________
       </td>
       <td align="center">
           ______________________________
       </td>
   </tr>
   <tr>
       <td align="center">
           TESTIGO
       </td>
       <td align="center">
           TESTIGO
       </td>
   </tr>
   <tr>
       <td align="center">
           <b>
	           <%= bmoSalesUser.getFatherlastname().toString().toUpperCase() %>
	           <%= bmoSalesUser.getMotherlastname().toString().toUpperCase() %>
	           <%= bmoSalesUser.getFirstname().toString().toUpperCase() %>
           </b>
       </td>
       <td align="center">
	       <b>
			   	<%= sFParams.getLoginInfo().getBmoUser().getFatherlastname().toString().toUpperCase() %>
			   	<%= sFParams.getLoginInfo().getBmoUser().getMotherlastname().toString().toUpperCase() %> 
			   	<%= sFParams.getLoginInfo().getBmoUser().getFirstname().toString().toUpperCase() %> 
		   </b>
       </td>
   </tr>
</table>

<%	} catch (Exception e) { 
	String errorLabel = "Error de Formato";
	String errorText = "El Formato Vivienda con Ecotecnología, no pudo ser desplegado exitosamente.";
	String errorException = e.toString();
	
	response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);
	}

%>
</body>
</html>



