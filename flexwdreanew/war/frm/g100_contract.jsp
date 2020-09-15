<%@page import="com.flexwm.server.wf.PmWFlowDocument"%>
<%@page import="com.flexwm.shared.wf.BmoWFlowDocument"%>
<%@page import="java.time.ZoneId"%>
<%@page import="java.time.LocalDate"%>
<%@page import="java.time.LocalDateTime"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="com.flexwm.server.FlexUtil"%>
<%@page import="com.flexwm.server.AmountByWord"%>
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
<%@page import= "com.symgae.server.SFServerUtil"%>
<%@page import= "java.util.Calendar"%>
<%@page import="java.sql.Types"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.DecimalFormat "%>

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
//Imprimir
String print = "0", permissionPrint = "";
if ((String)request.getParameter("print") != null) print = (String)request.getParameter("print");

//Exportar a Excel
String exportExcel = "0";
if ((String)request.getParameter("exportexcel") != null) exportExcel = (String)request.getParameter("exportexcel");
if (exportExcel.equals("1") && sFParams.hasPrint(bmoProgram.getCode().toString())) {
response.setContentType("application/vnd.ms-excel");
response.setHeader("Content-Disposition", "inline; filename=\""+title+".xls\"");
}

//Si se visualiza y no se tienen permisos, deshabilita: copiar, pegar, seleccionar, menÃÆÃÂº(clic-derecho).
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
<body class="default" <%= permissionPrint %>>

<%
	try {		
		AmountByWord amountByWord = new AmountByWord();
		amountByWord.setLanguage("es");
		amountByWord.setCurrency("es");
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
		
		BmoWFlowDocument bmoWFlowDocument = new BmoWFlowDocument();
		PmWFlowDocument pmWFlowDocument = new PmWFlowDocument(sFParams);
		
		//etapa
		BmoDevelopmentPhase bmoDevelopmentPhase = new BmoDevelopmentPhase();
		PmDevelopmentPhase pmDevelopmentPhase = new PmDevelopmentPhase(sFParams);
		bmoDevelopmentPhase= (BmoDevelopmentPhase)pmDevelopmentPhase.get(bmoPropertySale.getBmoProperty().getBmoDevelopmentBlock().getDevelopmentPhaseId().toInteger());
				
		
		//Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
// 		String masterPlan = "";
// 		if (!bmoWFlowDocument.getBmoWFlow().get.equals(""))
// 			masterPlan = HtmlUtil.parseImageLink(sFParams, bmoPropertySale.getBmoProperty().getBmoDevelopmentBlock().getBmoDevelopmentPhase().get);
		
		
		
		
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
		
		//Estado Civil
		BmoMaritalStatus bmoMaritalStatus = new BmoMaritalStatus();
		PmMaritalStatus pmMaritalStatus = new PmMaritalStatus(sFParams);
		bmoMaritalStatus =(BmoMaritalStatus)pmMaritalStatus.getBy(bmoCustomer.getMaritalStatusId().toInteger(), bmoMaritalStatus.getIdFieldName());
		
		//Nacionalidad
		BmoNationality bmoNationality = new BmoNationality();
		PmNationality pmNationality = new PmNationality(sFParams);
		if (bmoCustomer.getNationalityId().toInteger() > 0)
			bmoNationality = (BmoNationality)pmNationality.get(bmoCustomer.getNationalityId().toInteger());
		
		//Pedido 
		BmoOrder bmoOrder = new BmoOrder();
		PmOrder pmOrder = new PmOrder(sFParams);
		bmoOrder = (BmoOrder)pmOrder.get(bmoPropertySale.getOrderId().toInteger());
				
				
		//Direcciones del Cliente
			PmConn pmConnCustomer= new PmConn(sFParams);
			pmConnCustomer.open();
			boolean cuad = false;
			BmoCustomerAddress bmoCustomerAddress = new BmoCustomerAddress();
			PmCustomerAddress pmCustomerAddress = new PmCustomerAddress(sFParams);
			String sqlCustAddress = " SELECT * FROM customeraddress WHERE cuad_customerid = " + bmoCustomer.getId();
			pmConnCustomer.doFetch(sqlCustAddress);
			if(pmConnCustomer.next()) cuad = true;
			if(cuad)
				bmoCustomerAddress = (BmoCustomerAddress)pmCustomerAddress.getBy(bmoCustomer.getId(), bmoCustomerAddress.getCustomerId().getName());
			
			pmConnCustomer.close();

		//Telefonos del Cliente
			String casa = "", movil = "";
			PmConn pmConnCustomerPhones= new PmConn(sFParams);
			pmConnCustomerPhones.open();
			BmoCustomerPhone bmoCustomerPhone = new BmoCustomerPhone();
			PmCustomerPhone pmCustomerPhone = new PmCustomerPhone(sFParams);
			String sqlCustPhone = " SELECT * FROM customerphones WHERE cuph_customerid = " + bmoCustomer.getId();
			pmConnCustomerPhones.doFetch(sqlCustPhone);
			while(pmConnCustomerPhones.next()){
				if(pmConnCustomerPhones.getString("cuph_type").equals(""+BmoCustomerPhone.TYPE_HOME))
					casa = pmConnCustomerPhones.getString("cuph_number");
				if(pmConnCustomerPhones.getString("cuph_type").equals(""+BmoCustomerPhone.TYPE_MOBILE))
					movil = pmConnCustomerPhones.getString("cuph_number");;
			}
			pmConnCustomerPhones.close();
			
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
					personalEmail = pmConnCustomerEmails.getString("cuem_email");
				if(pmConnCustomerEmails.getString("cuem_type").equals("" + BmoCustomerPhone.TYPE_WORK))
					workEmail = pmConnCustomerEmails.getString("cuem_email");
			}
			pmConnCustomerEmails.close();
			
			//Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
			String blueprint = "";
			if (!bmoProperty.getBlueprint().toString().equals(""))
				blueprint = HtmlUtil.parseImageLink(sFParams, bmoProperty.getBlueprint());
			
	
		//Conyuge y familiares del Cliente
			int fam = 1;
			String conyName="", conyAp="", conyAm="", conyPhone= "", conyExt = "", conyCellphone = "", conyEmail = "",
				   familiar1Name = "", familiar1Ap = "", familiar1Am = "", familiar1Phone = "", familiar1Ext = "", familiar1Cellphone = "", familiar1Email = "",
				   familiar2Name = "", familiar2Ap = "", familiar2Am = "", familiar2Phone = "", familiar2Ext = "", familiar2Cellphone = "", familiar2Email = "";
			
			PmConn pmConnCustomerRelative= new PmConn(sFParams);
			pmConnCustomerRelative.open();
			BmoCustomerRelative bmoCustomerRelative = new BmoCustomerRelative();
			PmCustomerRelative pmCustomerRelative = new PmCustomerRelative(sFParams);
			String sqlCustRelative = " SELECT * FROM customerrelatives WHERE curl_customerid = " + bmoCustomer.getId() + " ORDER BY curl_customerrelativeid ASC";
			pmConnCustomerRelative.doFetch(sqlCustRelative);
			while(pmConnCustomerRelative.next()){
				if(pmConnCustomerRelative.getString("curl_type").equals("" + BmoCustomerRelative.TYPE_SPOUSE)){
					conyName = pmConnCustomerRelative.getString("curl_fullname");
					conyAp = pmConnCustomerRelative.getString("curl_fatherlastname");
					conyAm = pmConnCustomerRelative.getString("curl_motherlastname");
					conyEmail = pmConnCustomerRelative.getString("curl_email");
					conyPhone = pmConnCustomerRelative.getString("curl_number");
					conyExt = pmConnCustomerRelative.getString("curl_extension");
					conyCellphone = pmConnCustomerRelative.getString("curl_cellphone");
				}else{
					//Se toman los dos primeros familiares registrados
					if(fam == 1){
						familiar1Name = pmConnCustomerRelative.getString("curl_fullname");
						familiar1Ap = pmConnCustomerRelative.getString("curl_fatherlastname");
						familiar1Am = pmConnCustomerRelative.getString("curl_motherlastname");
						familiar1Email = pmConnCustomerRelative.getString("curl_email");
						familiar1Phone = pmConnCustomerRelative.getString("curl_number");
						familiar1Ext = pmConnCustomerRelative.getString("curl_extension");
						familiar1Cellphone = pmConnCustomerRelative.getString("curl_cellphone");
					}
					if(fam == 2){
						familiar2Name = pmConnCustomerRelative.getString("curl_fullname");
						familiar2Ap = pmConnCustomerRelative.getString("curl_fatherlastname");
						familiar2Am = pmConnCustomerRelative.getString("curl_motherlastname");
						familiar2Email = pmConnCustomerRelative.getString("curl_email");
						familiar2Phone = pmConnCustomerRelative.getString("curl_number");
						familiar2Ext = pmConnCustomerRelative.getString("curl_extension");
						familiar2Cellphone = pmConnCustomerRelative.getString("curl_cellphone");
					}
					fam++;
				}
			}
			pmConnCustomerRelative.close();
			
			
		//Empresa
		BmoCompany bmoCompany = new BmoCompany();
		PmCompany  pmCompany  = new PmCompany (sFParams);
		bmoCompany = (BmoCompany)pmCompany.get(bmoProperty.getCompanyId().toInteger());
		
		
		//Ciudad de la Empresa
		BmoCity bmoCityCompany = new BmoCity();
		PmCity pmCity = new PmCity(sFParams);
		bmoCityCompany = (BmoCity)pmCity.get(bmoCompany.getCityId().toInteger());
			
			
		//Patron
		BmoCustomer bmoEmployer = new BmoCustomer();
		PmCustomer pmEmployer = new PmCustomer(sFParams);
		if(bmoCustomer.getParentId().toInteger() > 0)
			bmoEmployer = (BmoCustomer)pmEmployer.get(bmoCustomer.getParentId().toInteger());
			
			//Direcciones del Patron
				PmConn pmConnEmployer= new PmConn(sFParams);
				pmConnEmployer.open();
				boolean cuadEmpl = false;
				BmoCustomerAddress bmoEmployerAddress = new BmoCustomerAddress();
				PmCustomerAddress pmEmployerAddress = new PmCustomerAddress(sFParams);
				if(bmoCustomer.getParentId().toInteger() > 0){
					String sqlEmplAddress = " SELECT * FROM customeraddress WHERE cuad_customerid = " + bmoCustomer.getParentId().toInteger() + " ORDER BY cuad_type DESC";
					pmConnEmployer.doFetch(sqlEmplAddress);
					if(pmConnEmployer.next()) cuadEmpl = true;
					if(cuadEmpl)
						bmoEmployerAddress = (BmoCustomerAddress)pmEmployerAddress.getBy(bmoCustomer.getParentId().toInteger(), bmoEmployerAddress.getCustomerId().getName());
				}
				pmConnEmployer.close();
				
				
			//Telefonos del Patron
				PmConn pmConnEmployerPhone= new PmConn(sFParams);
				pmConnEmployerPhone.open();
				boolean cuadEmplPhone = false;
				BmoCustomerPhone bmoEmployerPhones = new BmoCustomerPhone();
				PmCustomerPhone pmEmployerPhones = new PmCustomerPhone(sFParams);
				if(bmoCustomer.getParentId().toInteger() > 0){
					String sqlEmplPhones = " SELECT * FROM customerphones WHERE cuph_customerid = " + bmoCustomer.getParentId().toInteger() +  " ORDER BY cuph_type DESC";
					pmConnEmployerPhone.doFetch(sqlEmplPhones);
					if(pmConnEmployerPhone.next()) cuadEmplPhone = true;
					if(cuadEmplPhone)
						bmoEmployerPhones = (BmoCustomerPhone)pmEmployerPhones.getBy(bmoCustomer.getParentId().toInteger(), bmoEmployerPhones.getCustomerId().getName());
				}
				pmConnEmployerPhone.close();
				
		//Vendedor
		BmoUser bmoSalesUser = new BmoUser();
		PmUser pmSalesUser = new PmUser(sFParams);
		bmoSalesUser = (BmoUser)pmSalesUser.get(bmoPropertySale.getSalesUserId().toInteger());
		
		//Miscelaneas 
		DecimalFormat df = new DecimalFormat("###.##");
				
		String mes="";
		String day = SFServerUtil.nowToString(sFParams, "dd");
		String year = SFServerUtil.nowToString(sFParams, "YYYY");
		double yearDouble = Double.parseDouble(year);
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
		
%>
<p align="center"><b>INDICE</b></p>

<table width="100%" border="0" align="" cellpadding="0" cellspacing="0" style="">
	<tr>
		<td width="10%" align="left">
			<b>Contrato</b>
		</td>
		<td width="40%">
			<%String fisrtLetterName = bmoCustomer.getFirstname().toString().toUpperCase(); %>
			G100/LAVALENCIANA/<%=bmoCustomer.getFatherlastname().toString().toUpperCase() %><%=fisrtLetterName.charAt(0)%>
		</td>
	</tr>
	<tr>
		<td width="10%" align="left">
			<br><br>
			<b>Anexo 1</b>
		</td>
		<td width="40%">
			<br><br>
			Ficha T&eacute;cnica del Lote 
		</td>
	</tr>
	<tr>
		<td width="10%" align="left">
			<br><br>
			<b>Anexo 2</b>
		</td>
		<td width="40%">
			<br><br>
			    Croquis del inmueble con ubicaci&oacute;n
			</td>
	</tr>
	<tr>
		<td width="10%" align="left">
			<br><br>
			<b>Anexo 3</b>
		</td>
			<td width="40%">
			<br><br>
		    Identificaci&oacute;n del Comprador
		</td>
	</tr>
	<tr>
		<td width="10%" align="left">
			<br><br>
			<b>Anexo 4</b>
		</td>
		<td width="40%">
			<br><br>
			RFC y CURP del Comprador
		</td>
	</tr>
	<tr>
		<td width="10%" align="left">
			<br><br>
			<b>Anexo 5</b>
		</td>
		<td width="40%">
			<br><br>
		    Cuestionario con Informaci&oacute;n y Documentaci&oacute;n del Inmueble
		</td>
	</tr>
	<tr>
		<td width="10%" align="left">
			<br><br>
			<b>Anexo 6</b>
		</td>
		<td width="40%">
			<br><br>
			Plan de Pagos
		</td>
	</tr>
	<tr>
		<td width="10%" align="left">
			<br><br>
			<b>Anexo 7</b>
		</td>
			<td width="40%">
			<br><br>
			Reglamento de Construcci&oacute;n
		</td>
	</tr>
</table>
<p style="page-break-after: always"></p>
<table width="100%" border="0" align="" cellpadding="0" cellspacing="0" style="">
<tr>
    
    	<td align="justify" valign="bottom" style= "font-size: 10pt"  font-family:sans-serif;"	>
   
<p ALIGN="justify" class="uppercase"><b>
<b>CONTRATO DE PROMESA DE COMPRAVENTA SUJETO A CONDICI&Oacute;N SUSPENSIVA
DE BIEN INMUEBLE DESTINADO A CASA HABITACION AL QUE EN LO SUCESIVO
SE LE DENOMINAR&Aacute;, EL &quot;CONTRATO&quot; O &quot;CONTRATO DE PROMESA DE
COMPRAVENTA&quot;, QUE CELEBRAN POR UNA PARTE, LA PERSONA MORAL
DENOMINADA MULTISERVICIOS NORDIKA, S.A. DE C.V., REPRESENTADA EN
ESTE ACTO POR EL SE&Ntilde;OR ENRIQUE MU&Ntilde;OZ GONZALEZ, A QUIEN EN LO
SUCESIVO SE LE DENOMINAR&Aacute; LA &quot;PROMITENTE VENDEDORA&quot; Y, POR OTRA
PARTE,<a> <b><%=bmoCustomer.getFirstname().toString().toUpperCase() %> <%=bmoCustomer.getFatherlastname().toString().toUpperCase() %> <%=bmoCustomer.getMotherlastname().toString().toUpperCase() %></b></a>, A QUIEN EN LO SUCESIVO SE LE
DENOMINARA EL &quot;PROMITENTE COMPRADOR&quot;, PERSONAS QUE SE OBLIGAN AL
TENOR DE LAS SIGUIENTES DECLARACIONES Y CL&Aacute;USULAS:</b>
<br>

<div ALIGN="center"><b>DECLARACIONES</b></div>
<br>

<b>I.- DECLARA LA PROMITENTE VENDEDORA, A TRAV&Eacute;S DE SU REPRESENTANTE
LEGAL Y BAJO PROTESTA DE DECIR VERDAD, LO SIGUIENTE:
</b>
</b>
<br><br>

a) Que es una sociedad legalmente constituida de conformidad con las leyes de los Estados Unidos Mexicanos, 
seg&uacute;n consta en la Escritura P&uacute;blica n&uacute;mero 6,886, de fecha 03 de febrero de 1999, 
otorgada ante la fe del Licenciado Xavier Tejada Valadez, titular de la Notar&iacute;a P&uacute;blica n&uacute;mero 70 de la ciudad de Le&oacute;n, 
Guanajuato, misma que se encuentra inscrita, en el Registro P&uacute;blico de la Propiedad y del Comercio del Estado de Baja California, 
bajo el folio mercantil n&uacute;mero 5168870.
<br><br>

b) Que mediante escritura p&uacute;blica 17,188 de fecha 16 de diciembre de 2014, ante la fe del Licenciado Horacio Irianda Alcal&aacute;, 
Notario P&uacute;blico n&uacute;mero 89, se realiz&oacute; el cambio de domicilio social a la ciudad de Le&oacute;n Guanajuato, 
la cual se inscribi&oacute; en Registro P&uacute;blico de la Propiedad y de Comercio del Estado de Guanajuato, 
seg&uacute;n consta en la boleta de inscripci&oacute;n bajo el folio mercantil 66414*20.
<br><br>

c) Que su representante legal cuenta con las facultades suficientes para obligar a su representada en los t&eacute;rminos y condiciones del presente Contrato, 
lo anterior seg&uacute;n lo acredita con la Escritura P&uacute;blica n&uacute;mero 23,915, de fecha 17 de octubre de 2019, 
otorgada ante la fe del Licenciado Jos&eacute; Antonio Zavala Arias, titular de la Notar&iacute;a P&uacute;blica n&uacute;mero 45 de la ciudad de Le&oacute;n, 
Guanajuato, misma que se encuentra inscrita, en el Registro P&uacute;blico de la Propiedad y del Comercio del Estado de Guanajuato, 
bajo el folio mercantil n&uacute;mero 66414; facultades que no le han sido revocadas ni modificadas en forma alguna.
<br><br>

d) Que por escritura 16,353 diecis&eacute;is mil trescientos cincuenta y tres, de fecha 26 veintis&eacute;is de enero del a&ntilde;o 2018 dos mil dieciocho, 
otorgada ante la fe del  Licenciado Luis &Aacute;ngel Alfonso Chico Gonz&aacute;lez, Notario P&uacute;blico n&uacute;mero 5 de la ciudad de Le&oacute;n, 
Guanajuato  e inscrita en el Registro P&uacute;blico de la Propiedad y del Comercio de esta ciudad  en el Folio Real n&uacute;mero R20*3784, 
se formaliz&oacute; entre otros actos el CONTRATO DE FIDEICOMISO n&uacute;mero F/3383 celebrado entre otros por La sociedad mercantil denominada MULTISERVICIOS N&Oacute;RDIKA, 
SOCIEDAD AN&Oacute;NIMA DE CAPITAL VARIABLE, en su car&aacute;cter de FIDEICOMITENTE "B" Y/O FIDEICOMISARIA "B" y/o "DEPOSITARIA";  e  INTERCAM BANCO, 
SOCIEDAD AN&Oacute;NIMA, INSTITUCI&Oacute;N DE BANCA M&Uacute;LTIPLE, INTERCAM GRUPO FINANCIERO, en su car&aacute;cter de FIDUCIARIA; 
mediante el cual los FIDEICOMITENTES "A" aportaron al Patrimonio del Fidecomiso varios inmuebles donde actualmente se desarrolla el Fraccionamiento La Valenciana en esta ciudad.
<br><br>

e). - Mediante escritura p&uacute;blica n&uacute;mero 16,762 diecis&eacute;is mil setecientos sesenta y dos, 
de fecha 09 nueve de enero del a&ntilde;o 2019 dos mil diecinueve, otorgada ante la fe del Licenciado Luis &Aacute;ngel Alfonso Chico Gonz&aacute;lez, 
Notario P&uacute;blico N&uacute;mero 5 Cinco en legal ejercicio en la ciudad de Le&oacute;n, Guanajuato, 
se hizo constar el PRIMER CONVENIO MODIFICATORIO AL CONTRATO DE FIDEICOMISO IDENTIFICADO CON EL N&Uacute;MERO F/3383 (F DIAGONAL TRES MIL TRESCIENTOS OCHENTA Y TRES). 
<br><br>

f) Que conforme a lo establecido en el contrato de Fideicomiso, MULTISERVICIOS NORDIKA, SOCIEDAD AN&Oacute;NIMA DE CAPITAL VARIABLE, 
ya es propietaria de gran parte del predio urbano ubicado en Carretera Federal Libre 45, Le&oacute;n Lagos de Moreno 16 Km SN, 
Congregaci&oacute;n Lagunillas, de la ciudad Le&oacute;n, Guanajuato, C.P. 37669 (en lo sucesivo denominado como el "PREDIO"), 
donde actualmente est&aacute; construyendo el desarrollo denominado "La Valenciana" (en lo sucesivo denominado como el "DESARROLLO INMOBILIARIO"),  
el cual comprender&aacute; varios lotes de terreno para la edificaci&oacute;n casas, 
los cuales estar&aacute;n sujetos al r&eacute;gimen de propiedad en condominio, y que dicho desarrollo contar&aacute; con la 
infraestructura para el adecuado funcionamiento de los servicios de suministro de energ&iacute;a el&eacute;ctrica, agua potable, 
drenaje y alcantarillado, vialidades vehiculares interiores y de conexi&oacute;n con la red vial urbana y dem&aacute;s obras de equipamiento urbano, 
como se se&ntilde;alan en el proyecto ejecutivo de construcci&oacute;n completo, traza autorizada y las licencias, autorizaciones y permisos respectivos.
<br><br>

g) Que actualmente el Desarrollo y su tramitaci&oacute;n se encuentran en proceso.
<br><br>

h) Que conforme a la traza autorizada dentro del DESARROLLO INMOBILIARIO de la parte que ya le pertenece a MULTISERVICIOS N&Oacute;RDIKA, 
SOCIEDAD AN&Oacute;NIMA DE CAPITAL VARIABLE, se encuentra el objeto cuya ubicaci&oacute;n se desea garantizar mediante el presente 
Contrato de PROMESA DE COMPRAVENTA consistente en un lote de terreno bajo el r&eacute;gimen de propiedad en condominio, los cuales se identifican como:
<br><br>

1.- Lote ubicado en la Privada <b><%= bmoProperty.getNumber() %></b>, de la Manzana <b><%=bmoProperty.getBmoDevelopmentBlock().getName() %></b>, Lote <%= bmoProperty.getLot() %> con una superficie de 
<b> <%= (bmoPropertySale.getBmoProperty().getLandSize().toDouble())%> </b>metros cuadrados,<b> (Anexo 1). (En lo sucesivo EL INMUEBLE).</b>	
<br><br>

As&iacute; como las correspondientes &aacute;reas de uso com&uacute;n o porcentaje indiviso que se&ntilde;ale la escritura del r&eacute;gimen 
en propiedad en condominio dentro del DESARROLLO INMOBILIARIO que incluye entre otras: motivo de acceso, casa club, controles de acceso de cada una de las privadas, 
&aacute;reas verdes, etc&eacute;tera y que dichos bienes ser&aacute;n administrados a trav&eacute;s de la Asociaci&oacute;n Civil que se constituya para dicho fin.
<br><br>

Y las correspondientes &aacute;reas de uso com&uacute;n o porcentaje indiviso que se&ntilde;ale la escritura del r&eacute;gimen 
en propiedad en condominio dentro del DESARROLLO INMOBILIARIO en lo sucesivo el INMUEBLE.
<br><br>

Se acompa&ntilde;a como <b>(Anexo 2)</b> al presente croquis del INMUEBLE con la ubicaci&oacute;n del
mismo.
<br><br>

i) Que es su deseo comprometerse a vender el INMUEBLE una vez cumplida a condici&oacute;n suspensiva, 
de acuerdo con los t&eacute;rminos y condiciones establecidos en este Contrato.
<br><br>

j) Que En fecha 20 de marzo de 2020 (dos mil veinte) MULTISERVICIOS N&Oacute;RDIKA, S.A. DE C.V. con el car&aacute;cter 
de Fideicomitente y Fideicomisario en Segundo Lugar, celebr&oacute; con BANCO BANCREA, S.A., INSTITUCI&Oacute;N DE BANCA M&Uacute;LTIPLE, 
&eacute;ste &uacute;ltimo, en su car&aacute;cter de Fiduciario, un Contrato Irrevocable de Fideicomiso de Administraci&oacute;n y Fuente Alterna de Pago, 
el cual qued&oacute; registrado administrativamente con el n&uacute;mero BA320, en el que adem&aacute;s intervino con el 
car&aacute;cter de Fideicomisario en Primer Lugar, Banco Bancrea, S.A., Instituci&oacute;n de Banca M&uacute;ltiple (en lo sucesivo el "FIDEICOMISO"), 
dentro del cual se oblig&oacute; a transmitir al patrimonio del citado FIDEICOMISO, todos y cada uno de los flujos que le corresponden, 
que derivan de los CONTRATOS DE COMPRAVENTA que celebre MULTISERVICIOS NORDIKA, respecto a los inmuebles correspondientes al 
proyecto residencial denominado "La Valenciana" de la ciudad de Le&oacute;n, Guanajuato, dentro de los cuales se encuentra el inmueble objeto del presente instrumento.
<br><br>

<b>II.- DECLARA EL PROMITENTE COMPRADOR, POR SU PROPIO DERECHO Y BAJO
PROTESTA DE DECIR VERDAD, LO SIGUIENTE:</b>
<br><br>

a) Ser una persona f&iacute;sica, mayor de edad, de nacionalidad <b><%=bmoNationality.getName()%> </b>, lo que acredita con<b>
<%=bmoCustomer.getOficialIdentify() %></b> con n&uacute;mero <b> <%=bmoCustomer.getNss() %></b> <b>(Anexo 3),</b> <b> <%=bmoMaritalStatus.getName()%> </b> y que cuenta con facultades suficientes
para obligarse en los t&eacute;rminos del presente Contrato.
<br><br>

b) Que est&aacute; inscrito en el Registro Federal de Contribuyentes con el n&uacute;mero <b><%=bmoCustomer.getRfc() %></b>, y que
cuenta con el CURP n&uacute;mero <b><%=bmoCustomer.getCurp() %></b>, tal y como se hace constar en las copias de las c&eacute;dulas
que se integran al presente Contrato como<b> Anexo 4.</b>
<br><br>

c) Conoce el proyecto arquitect&oacute;nico y urban&iacute;stico del DESARROLLO INMOBILIARIO que la PROMITENTE VENDEDORA est&aacute; 
ejecutando en el PREDIO y manifiesta que la informaci&oacute;n y documentaci&oacute;n relativa a la misma, que se le proporcion&oacute;, 
es la que se especifica en el <b>(Anexo 5)</b> del presente contrato, el cual firmado por ambas partes forma
parte integrante del mismo.
<br><br>

d) Que est&aacute; al tanto de que el INMUEBLE materia del presente Contrato, cuenta con uso de suelo habitacional, 
y que el mismo formar&aacute; parte de un desarrollo en condominio, por lo cual estar&aacute; sujeto a varias limitaciones, 
por lo que el INMUEBLE que desea apartar se destinar&aacute; exclusivamente a vivienda unifamiliar con las limitaciones 
que establezca el R&eacute;gimen y el Reglamento del Condominio, as&iacute; como el Reglamento de Construcci&oacute;n al que estar&aacute; sujeto.
<br><br>

e) Que es su deseo comprometerse a adquirir de la PROMITENTE VENDEDORA el INMUEBLE una vez cumplida la condici&oacute;n suspensiva, 
con todo lo que de hecho y por derecho le corresponda, de acuerdo con los t&eacute;rminos y condiciones establecidos en este Contrato.
<br><br>

f) Que sabe que actualmente el Desarrollo y su tramitaci&oacute;n se encuentra en proceso.
<br><br>

<b>III.- DECLARAN LAS PARTES QUE:</b>
<br><br>

a) Mutuamente se reconocen la personalidad con que comparecen a la celebraci&oacute;n del
presente Contrato.
<br><br>

En virtud de las declaraciones manifestadas por las Partes, &eacute;stas otorgan y se sujetan al
contenido de las siguientes:
<br><br>

<div align= "center"><b>CL&Aacute;USULAS</b></div>
<br>

<b>PRIMERA. OBJETO.</b> - En virtud de este acuerdo de voluntades, la PROMITENTE VENDEDORA se compromete a vender, 
una vez cumplida la condici&oacute;n suspensiva que se detalla m&aacute;s adelante, al PROMITENTE COMPRADOR quien se compromete adquirir para s&iacute;, 
el INMUEBLE especificado en la<b>declaraci&oacute;n I inciso g) anterior.</b>
<br>
<br>

<b>SEGUNDA. PRECIO Y FORMA DE PAGO.</b> - Las Partes convienen que el precio total de la COMPRAVENTA que se celebre del inmueble apartado, 
ser&aacute; la cantidad de <b><%= SFServerUtil.formatCurrency(bmoOrder.getTotal().toDouble()) %></b> 
M.N. (<b><%= amountByWord.getMoneyAmountByWord(bmoOrder.getTotal().toDouble()).toUpperCase() %></b>), 
precio total que el PROMITENTE COMPRADOR se obligar&aacute; a pagar a la PROMITENTE VENDEDORA de acuerdo al Plan de Pagos, 
que se anexa al presente contrato como <b>(Anexo 6)</b> sin que exista penalizaci&oacute;n por pagos anticipado. 
En caso de haber un pago anticipado, se le notificar&aacute; al PROMITENTE VENDEDOR con 30 d&iacute;as naturales de anticipaci&oacute;n para que, 
dependiendo de la fecha y el monto anticipado, 
notifique al PROMITENTE COMPRADOR un descuento por pronto pago v&iacute;a correo electr&oacute;nico para su visto bueno y pago correspondiente.
<br><br>

Que dentro del precio de compra venta se est&aacute;n incluyendo las partes proporcionales 
correspondientes &aacute;reas de uso com&uacute;n o porcentaje indiviso que se&ntilde;ale la escritura del r&eacute;gimen en 
propiedad en condominio dentro del DESARROLLO INMOBILIARIO que incluye entre otras: motivo de acceso, casa club, 
controles de acceso de cada una de las privadas, &aacute;reas verdes, etc&eacute;tera y que 
dichos bienes ser&aacute;n administrados a trav&eacute;s de la Asociaci&oacute;n Civil que se constituya para dicho fin.
<br><br>

Dicho pago deber&aacute; realizarse, en el domicilio de la PROMITENTE VENDEDORA o a trav&eacute;s 
de transferencia electr&oacute;nica o dep&oacute;sitos en cualquier de las siguientes cuentas:
<br><br>

<b>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;Raz&oacute;n Social: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;BANCREA, S.A. FIDEICOMISO BA320<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Banco: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;BANCREA, S.A. INSTITUCI&Oacute;N DE BANCA M&Uacute;LTIPLE<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Cuenta: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;12000024854<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;CLABE: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;152225120000248543<br>
<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;Raz&oacute;n Social: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;BANCO BANCREA SA INSTITUCION DE BANCA MULTIPLE FID BA320<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Banco: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;BANORTE<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Cuenta: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1106929536<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;CLABE: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;072244011069295367<br>
</b>

<br>
<b>NOTIFICACION DE LA CESION DE DERECHOS DE COBRO.-</b> MULTISERVICIOS NORDIKA, S.A. DE C.V., notifica al COMPRADOR que los flujos que le corresponde recibir respecto a los 
pagos que &eacute;ste &uacute;ltimo debe realizar por la adquisici&oacute;n del inmueble, objeto del presente instrumento, 
se ceder&aacute;n al FIDEICOMISO. El COMPRADOR se da, con lo anterior, por notificado en t&eacute;rminos del art&iacute;culo 2036 (dos mil treinta y seis) del C&oacute;digo Civil Federal, 
por lo cual se obliga a realizar todos los pagos en la CUENTA DEL FIDEICOMISO, cuyos datos se precisan en la cl&aacute;usula titulada "PRECIO Y FORMA DE PAGO". 
Los comparecientes reconocen que BANCO BANCREA S.A. INSTITUCION DE BANCA MULTIPLE en su car&aacute;cter de Fiduciario del Fideicomiso BA320, 
no asumir&aacute; ninguna obligaci&oacute;n derivada del presente CONTRATO DE COMPRAVENTA, cuyo cumplimiento reconocen que es y 
ser&aacute; exclusiva responsabilidad y obligaci&oacute;n del COMPRADOR, del VENDEDOR y de MULTISERVICIOS NORDIKA, S.A. DE C.V.
<br><br>

Las Partes acuerdan que, la PROMITENTE VENDEDORA tendr&aacute; el derecho a su elecci&oacute;n de rescindir el 
presente Contrato o exigir su cumplimiento forzoso, en caso de que el PROMITENTE COMPRADOR incumpla con cualquiera 
de las obligaciones a su cargo asumidas en el presente instrumento.<br><br>

<b>TERCERA. INTERESES MONETARIOS. -</b>EL PROMITENTE COMPRADOR, est&aacute; de acuerdo en cubrir un inter&eacute;s moratorio del 2% (dos por ciento) 
mensual aplicado al monto de la mensualidad incumplida en el Plan de Pagos <b>(Anexo 6)</b>, m&aacute;s el IVA que generen dichos intereses. 
Los intereses moratorios se causar&aacute;n durante todo el tiempo en que permanezcan insolutas las mensualidades a cargo del PROMITENTE COMPRADOR. 
Los intereses moratorios, en caso de que se causen, junto con el IVA, que generen de acuerdo con las leyes respectivas, 
deber&aacute;n pagarse al momento en que se liquide la mensualidad vencida.<br><br>

<b>CUARTA. RESCICI&Oacute;N:</b> EL PROMITENTE COMPRADOR, est&aacute; de acuerdo de que el caso de que llegar&aacute; 
a atrasarse en el pago de 3 (tres) o m&aacute;s mensualidades establecidas en el Plan de Pagos <b>(Anexo 6)</b>,
el PROMITENTE VENDEDOR, podr&aacute; rescindir el presente Contrato de Promesa de Compra Venta, 
para lo cual se sujetar&aacute; a lo establecido en la Cl&aacute;usula Novena, Pena Convencional, 
del presente Contrato de Promesa de Compra Venta.<br><br>

<b>QUINTA. FIRMA DE ESCRITURA P&Uacute;BLICA. -</b> Las Partes acuerdan que dentro de los 30 (Treinta) d&iacute;as naturales 
siguientes a que se cumpla la condici&oacute;n suspensiva, y una vez firmado el contrato de compra venta definitivo y debidamente liquidado, 
concurrir&aacute;n ante el notario p&uacute;blico que en su momento sea designado por la PROMITENTE VENDEDORA, 
con el fin de otorgar y formalizar en escritura p&uacute;blica la COMPRAVENTA del inmueble apartado.
<br><br>

Las Partes convienen en que, todos y cada uno de los gastos, honorarios, derechos, impuestos, y dem&aacute;s 
erogaciones que se deban sufragar con motivo de la operaci&oacute;n de PROMESA DE COMPRAVENTA y 
su escrituraci&oacute;n correr&aacute;n por cuenta exclusiva del PROMITENTE COMPRADOR, 
excepto por el Impuesto Sobre la Renta a cargo de la PROMITENTE VENDEDORA derivado de la venta efectuada.
<br><br>

<b>SEXTA. POSESI&Oacute;N DEL INMUEBLE.</b> - La PROMITENTE VENDEDORA entregar&aacute; al PROMITENTE COMPRADOR la posesi&oacute;n f&iacute;sica del INMUEBLE, 
una vez que se cumpla la condici&oacute;n suspensiva, mediante su entrega real a m&aacute;s tardar <b>30 (treinta)</b> 
d&iacute;as posteriores a la firma del contrato definitivo de COMPRAVENTA, siempre y cuando se hayan concluido las 
obras de urbanizaci&oacute;n y equipamiento y se cuente con las autorizaciones necesarias, y adem&aacute;s estar al corriente en los pagos. 
Lo anterior con independencia del avance en la construcci&oacute;n del resto del DESARROLLO INMOBILIARIO, 
pero en todo caso garantizando el debido funcionamiento de los servicios con que contar&aacute; el INMUEBLE.
A partir de la fecha de entrega, el PROMITENTE COMPRADOR se obliga a realizar los pagos del impuesto predial del INMUEBLE.
<br><br>

<b>S&Eacute;PTIMA. REGLAMENTO Y CUOTAS DE MANTENIMIENTO. -</b> Una vez que el PROMITENTE COMPRADOR reciba la posesi&oacute;n f&iacute;sica del INMUEBLE se obliga a cumplir, 
en todos sus t&eacute;rminos, con el R&eacute;gimen y el Reglamento del Condominio al que estar&aacute; sujeto, 
as&iacute; como las disposiciones legales aplicables, incluyendo el pago de los servicios y/o contribuciones. 
Asimismo, el PROMITENTE COMPRADOR estar&aacute; obligado al pago de cantidades que determine la 
Asamblea de Condominios y/o la Administraci&oacute;n del Condominio, para la constituci&oacute;n de fondos de mantenimiento, 
reservas, fianzas para cubrir dichos fondos, as&iacute; como las cuotas de mantenimiento, ordinarias y extraordinarias, 
respectivas, con independencia de los avances sobre el resto del DESARROLLO INMOBILIARIO, en cualquiera de sus etapas. 
<br><br>

Con el prop&oacute;sito de conservar el entorno urban&iacute;stico y arquitect&oacute;nico del DESARROLLO INMOBILIARIO, 
el PROMITENTE COMPRADOR se obliga a  respetar el Reglamento de Construcci&oacute;n y a no construir o edificar obra alguna distinta 
de las que autorice la licencia de construcci&oacute;n respectiva, para lo cual someter&aacute; al visto bueno de la 
PROMITENTE VENDEDORA la solicitud de la licencia de construcci&oacute;n, en el entendido que la PROMITENTE VENDEDORA 
tendr&aacute; un plazo no mayor a 60 (sesenta) d&iacute;as naturales para otorgar el Visto Bueno, 
de lo contrario se entender&aacute; que el proyecto ha sido autorizado.
<br><br>

Asimismo, el PROMITENTE COMPRADOR se obliga a respetar las siguientes restricciones de construcci&oacute;n a las que est&aacute; sujeto el INMUEBLE, 
adem&aacute;s de las que se lleguen a establecer en el Reglamento de Condominio: 
<br>
<ul>
	<li> No realizar construcci&oacute;n dentro de 3 (tres) metros de frente del INMUEBLE.</li>
	<li> No realizar construcci&oacute;n dentro de 6 (seis) metros de atr&aacute;s del INMUEBLE.</li>
</ul>

<b>OCTAVA. ASOCIACI&Oacute;N DE COND&Oacute;MINOS. -</b> En su caso y toda vez que la PROMITENTE VENDEDORA ha propuesto al PROMITENTE COMPRADOR 
formar parte de la asociaci&oacute;n civil que se constituir&aacute; como consecuencia del DESARROLLO INMOBILIARIO, 
cuyo objeto ser&aacute; la representaci&oacute;n de todos los cond&oacute;minos del DESARROLLO INMOBILIARIO, 
el PROMITENTE COMPRADOR acepta expresamente integrarse a la misma como asociado y que ratificar&aacute; al momento de la firma de su escritura. 
Los proyectos de estatutos de la asociaci&oacute;n civil, reglamento del condominio y acta de integraci&oacute;n como asociado, 
as&iacute; como el Reglamento de Construcci&oacute;n, los cuales se adjuntan al presente Contrato como <b>(Anexo 7)</b>, 
el cual firmado por las partes forma parte integrante del mismo.
<br><br>

<b>NOVENA. PENA CONVENCIONAL. -</b> Las Partes acuerdan para el caso de incumplimiento del PROMITENTE COMPRADOR 
de cualquiera de las obligaciones contra&iacute;das en el presente Contrato de Promesa de Compra Venta, 
una pena convencional de la cantidad equivalente al 20% (veinte por ciento) del precio m&aacute;ximo total se&ntilde;alado la Cl&aacute;usula Segunda.
<br><br>

Las partes acuerdan, que la PROMITENTE VENDEDORA podr&aacute; retener la pena convencional, de cualquier cantidad entregada por el PROMITENTE COMPRADOR. 
Las cantidades que resultaren excedentes a favor del PROMITENTE COMPRADOR deber&aacute;n ser devueltas por la PROMITENTE VENDEDORA 
dentro de los <b>30 (treinta)</b> d&iacute;as naturales siguientes a la fecha de la rescisi&oacute;n del Contrato de PROMESA DE COMPRAVENTA. 
En caso de que no se restituyeren las cantidades excedentes dentro del plazo establecido, la PROMITENTE VENDEDORA 
deber&aacute; pagar un inter&eacute;s del equivalente al 2% por cada mes transcurrido de retraso en dicha restituci&oacute;n.
<br><br>

<b>D&Eacute;CIMA.. CESI&Oacute;N DE DERECHOS. -</b> El PROMITENTE COMPRADOR no podr&aacute; ceder o traspasar total o parcialmente 
los derechos que adquiere por este Contrato sin previa autorizaci&oacute;n por escrito de la PROMITENTE VENDEDORA; 
por su parte &eacute;sta &uacute;ltima est&aacute; autorizada para ceder, total o parcialmente los derechos y obligaciones 
de que es titular con motivo del presente Contrato, debiendo dar aviso por escrito al PROMITENTE COMPRADOR.
<br><br>

<b>D&Eacute;CIMA PRIMERA. AUSENCIA DE VICIOS DEL CONSENTIMIENTO. -</b> Las Partes declaran que en este Contrato no existe dolo, error, 
lesi&oacute;n o cualquier otro vicio que pudiera invalidado, que el precio asignado al INMUEBLE es justo y leg&iacute;timo, por lo tanto, 
renuncian a la acci&oacute;n de nulidad y de los t&eacute;rminos para ejercitada. En el entendido que, 
si por cualquier circunstancia alguna parte del presente Contrato fuera declarada nula, 
dicha nulidad no ser&aacute; impedimento para que el resto del clausulado surta plenamente sus efectos.
<br><br>

<b>D&Eacute;CIMA SEGUNDA. MODIFICACIONES. -</b> Las Partes est&aacute;n de acuerdo en que cualquier modificaci&oacute;n a lo dispuesto 
en el presente Contrato deber&aacute; constar por convenio escrito y debidamente firmado por ellas.
<br><br>

<b>D&Eacute;CIMA TERCERA. DOMICILIO PARA NOTIFICACIONES. -</b> Todas las notificaciones, requerimientos, autorizaciones, renuncias, 
avisos y otras comunicaciones que deban darse conforme a este Contrato, deber&aacute;n hacerse por escrito y deber&aacute;n 
considerarse como debidamente entregadas si se encuentran firmadas por el respectivo 
representante legal o persona que la env&iacute;a y entregadas con acuse de recibo al destinatario a:
<br><br>


&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;La PROMITENTE VENDEDORA:<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Domicilio: <b>
<b><%= bmoCompany.getStreet().toString() %> <%= bmoCompany.getNumber() %>, <%= bmoCompany.getNeighborhood() %>,
 <%= bmoCityCompany.getName() %>,  <br>
<!-- Plaza de La Paz No. 102, Guanajuato Puerto Interior, Silao, Guanajuato, -->
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%= bmoCityCompany.getBmoState().getName() %>, C.P <%= bmoCompany.getZip() %></b><br><br></b>

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;El PROMITENTE COMPRADOR:<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Domicilio: <b>
<b><%= bmoCustomerAddress.getStreet().toString() %> <%= bmoCustomerAddress.getNumber() %>, <%= bmoCustomerAddress.getNeighborhood() %>,
 <%= bmoCustomerAddress.getBmoCity().getName() %>,  <br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%= bmoCustomerAddress.getBmoCity().getBmoState().getName() %>, C.P <%= bmoCustomerAddress.getZip() %></b><br><br></b>

<b>D&Eacute;CIMA CUARTA. SOLUCI&Oacute;N DE CONTROVERSIAS. -</b> Las Partes se someten a la jurisdicci&oacute;n de los tribunales competentes de la Ciudad de Le&oacute;n, 
Guanajuato, renunciando expresamente a cualquier otra jurisdicci&oacute;n que pudiera corresponderles, 
por raz&oacute;n de sus domicilios presentes o futuros o por cualquier otra raz&oacute;n.
<br><br>

<b>D&Eacute;CIMA QUINTA.. CONDICI&Oacute;N SUSPENSIVA Y EFECTOS DEL CONTRATO. -</b> En los t&eacute;rminos establecidos por los art&iacute;culos 1425 y 1428 del 
C&oacute;digo Civil para el Estado de Guanajuato, las partes se&ntilde;alan que los efectos del presente contrato se limitan 
al apartado del inmueble y a la promesa de enajenaci&oacute;n DEL INMUEBLE mediante la celebraci&oacute;n futura 
de un contrato definitivo de COMPRAVENTA, cuya celebraci&oacute;n queda sujeta al debido y puntual cumplimiento de la condici&oacute;n suspensiva, 
la cual consiste en que LA PROMITENTE VENDEDORA obtenga los permisos y autorizaciones relativas al Fraccionamiento que est&aacute; desarrollando, 
a m&aacute;s 60 (sesenta) d&iacute;as, por lo que una vez cumplida dicha condici&oacute;n, 
LAS PARTES deber&aacute;n celebrar el contrato definitivo de compraventa observando los t&eacute;rminos y obligaciones expresamente pactadas en el presente contrato.
<br><br>

Le&iacute;do que fue por las Partes el contenido del presente Contrato y sabedoras de su alcance
legal, lo firman por duplicado en la Ciudad de Le&oacute;n, Guanajuato, a los  <b><%= day%> </b>d&iacute;as del mes de <%= mes%> del 
dos mil veinte.  Entreg&aacute;ndosele una copia del mismo al PROMITENTE
COMPRADOR.
<br>
<br>
<table width="100%" border="0" align="" cellpadding="0" cellspacing="0" >
<tr>
<td align="CENTER" valign="bottom" style= "font-size: 10pt" width="50%" font-family:sans-serif;"	>

<b>LA PROMITENTE VENDEDORA</b>

<br>
</td>
<td align="CENTER" valign="bottom" style= "font-size: 10pt" width="50%" font-family:sans-serif;"	>
<b>EL PROMITENTE COMPRADOR</b>

</td>
</table>
<br>
<br>
<br>
<table width="100%" border="0" align="" cellpadding="0" cellspacing="0" >
<tr>
<td align="CENTER" valign="bottom" style= "font-size: 10pt" width="50%" font-family:sans-serif;"	>

<b>____________________________</b>

<br>
</td>
<td align="CENTER" valign="bottom" style= "font-size: 10pt" width="50%" font-family:sans-serif;"	>
<b>____________________________</b>

</td>
</table>

<table width="100%" border="0" align="" cellpadding="0" cellspacing="0" >
<tr>
<td align="CENTER" valign="top" style= "font-size: 10pt" width="50%" font-family:sans-serif;"	>

<b>MULTISERVICIOS NORDIKA, S.A. DE
C.V., REPRESENTADA EN ESTE ACTO
POR EL SE&Ntilde;OR ENRIQUE MU&Ntilde;OZ
GONZALEZ.</b>

<br>
</td>
<td align="CENTER" valign="top" style= "font-size: 10pt" width="50%" font-family:sans-serif;">
<b><%=bmoCustomer.getFirstname().toString().toUpperCase() %> <%=bmoCustomer.getFatherlastname().toString().toUpperCase() %> 
<%=bmoCustomer.getMotherlastname().toString().toUpperCase() %>
</b>

</td>
</tr>
</table>

</tr>

</table>
<p style="page-break-after: always"></p>
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" >
<tr>
<td align="CENTER" valign="bottom" style= "font-size: 10pt" width="100%" font-family:sans-serif;"	>
<b>ANEXO 5</b>
<br>
<br>
</td>
</tr>
<tr>
<td align="justify" valign="bottom" style= "font-size: 10pt" width="100%" font-family:sans-serif;"	>

<b>INFORMACION Y DOCUMENTACION DEL INMUEBLE QUE SE PONE A DISPOSICION
DEL PROMITENTE COMPRADOR</b>
<br>
<br>
</td>
</tr>
</table>
<table class="" width="100%" cellpadding="0" cellspacing="0" border ="1"  >

			<TR >
			<td align="left"  style= "font-size: 9pt" width="90%"  font-family: sans-serif"  VALIGN="top">
				&#191;Le exhibieron el Master Plan?
			</td>
			<td align="center"  style= "font-size: 6pt" width="5%"  font-family: sans-serif"  VALIGN="top">
			
			</td>
			<td align="center"  style= "font-size: 6pt" width="5%"  font-family: sans-serif"  VALIGN="top">
			
			</td>
			</tr>
			<TR >
			<td align="left"  style= "font-size: 9pt" width="90%"  font-family: sans-serif"  VALIGN="top">
				&#191;Le informaron respecto de las opciones de pago que puede elegir y sobre el
			monto total a pagar en cada una de ellas?	
			</td>
			<td align="center"  style= "font-size: 6pt" width="5%"  font-family: sans-serif"  VALIGN="top">
			
			</td>
			<td align="center"  style= "font-size: 6pt" width="5%"  font-family: sans-serif"  VALIGN="top">
			
			</td>
			</tr>
			<TR >
			<td align="left"  style= "font-size: 9pt" width="90%"  font-family: sans-serif"  VALIGN="top">
				&#191;En caso de que la operaci&oacute;n sea a cr&eacute;dito, le informaron sobre el tipo de cr&eacute;dito
			de que se trata?
			</td>
			<td align="center"  style= "font-size: 6pt" width="5%"  font-family: sans-serif"  VALIGN="top">
			
			</td>
			<td align="center"  style= "font-size: 6pt" width="5%"  font-family: sans-serif"  VALIGN="top">
			
			</td>
			</tr>
			<TR >
			<td align="left"  style= "font-size: 9pt" width="90%"  font-family: sans-serif"  VALIGN="top">
				&#191;Le informaron de las condiciones bajo las cuales se llevar&aacute; a cabo el proceso de
			escrituraci&oacute;n, as&iacute; como las erogaciones distintas del precio de la venta que deba
			realizar?
			</td>
			<td align="center"   width="5%"  font-family: sans-serif"  VALIGN="top">
			
			</td>
			<td align="center"  style= "font-size: 6pt" width="5%"  font-family: sans-serif"  VALIGN="top">
			
			</td>
			</tr>
			
</table>
<br>
<br>
				<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" >
				<tr>
<td align="justify" valign="bottom" style= "font-size: 10pt" width="100%" font-family:sans-serif;"	>
			<p style="text-indent:50">	<b>IMPORTANTE PARA EL PROMITENTE COMPRADOR: Antes de que firme como
									constancia de que tuvo a su disposici&oacute;n la informaci&oacute;n y documentaci&oacute;n relativa al
									INMUEBLE, cerci&oacute;rese de que la misma coincida con la que efectivamente le hayan
									mostrado y/o proporcionado la PROMITENTE VENDEDORA.
									</b></p>
									<td>
									</tr>
								</table>	
	<br>
	<br>
					
<table width="100%" border="0" align="" cellpadding="0" cellspacing="0" >
<tr>
<td align="CENTER" valign="bottom" style= "font-size: 10pt" width="50%" font-family:sans-serif;"	>

<b>LA PROMITENTE VENDEDORA</b>

<br>
</td>
<td align="CENTER" valign="bottom" style= "font-size: 10pt" width="50%" font-family:sans-serif;"	>
<b>EL PROMITENTE COMPRADOR</b>

</td>
</table>
<br>
<br>
<br>
<table width="100%" border="0" align="" cellpadding="0" cellspacing="0" >
<tr>
<td align="CENTER" valign="bottom" style= "font-size: 10pt" width="50%" font-family:sans-serif;"	>

<b>____________________________</b>

<br>
</td>
<td align="CENTER" valign="bottom" style= "font-size: 10pt" width="50%" font-family:sans-serif;"	>
<b>____________________________</b>

</td>
</table>



<table width="100%" border="0" align="" cellpadding="0" cellspacing="0" >
<tr>
<td align="CENTER" valign="top" style= "font-size: 10pt" width="50%" font-family:sans-serif;"	>

<b>MULTISERVICIOS NORDIKA, S.A. DE
C.V., REPRESENTADA EN ESTE ACTO
POR EL SE&Ntilde;OR ENRIQUE MU&Ntilde;OZ
GONZALEZ.</b>

<br>
</td>
<td align="CENTER" valign="top" style= "font-size: 10pt" width="50%" font-family:sans-serif;">
<b><%=bmoCustomer.getFirstname().toString().toUpperCase() %> <%=bmoCustomer.getFatherlastname().toString().toUpperCase() %> 
<%=bmoCustomer.getMotherlastname().toString().toUpperCase() %>
</b>

</td>
</tr>
</table>
	    	 
<%	} catch (Exception e) { 
	String errorLabel = "Error de Contrato";
	String errorText = "El Contrato no pudo ser desplegado exitosamente. Es necesario completar todos los datos faltantes: Venta de Inmuebles.";
	String errorException = e.toString();
	
	response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);
	}

%>
</body>
</html>
 
