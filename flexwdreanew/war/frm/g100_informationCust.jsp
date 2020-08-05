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

//Si se visualiza y no se tienen permisos, deshabilita: copiar, pegar, seleccionar, menÃº(clic-derecho).
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

<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" >
<tr>
    
    	<td align="left" valign="bottom" style= "font-size: 10pt"  font-family:sans-serif;"	>
 		<b>INTEGRACI&Oacute;N DE EXPEDIENTE DE CLIENTES (Hoja 1 de 3)</b>
        </td>
        <td align="right" >
        <img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src = <%= GwtUtil.getProperUrl(sFParams, "img/valenciana.png") %> >
        </td>
        </tr>
        </table>
        
        
		    <table class="reportSubTitle" width="100%" cellpadding="0" cellspacing="0" style= "background-color: black">
				<tr>
				<td>
				  <p style="color:WHITE ";>DATOS GENERALES</p>
				</td>		
				</tr>
				</table> 
				<table class="" width="100%" cellpadding="0" cellspacing="0" border ="1"  >
		    	<tr>
		  		<td align="left"  style= "font-size: 6pt "  font-family:sans-serif;" VALIGN="TOP">
				Denominaci&oacuten o raz&oacuten social: Nombre(s), apellido paterno, apellido materno (sin abreviaturas)</a></small>
				</td>
				
					<td align="left"  style= "font-size: 6pt"  font-family:sans-serif;"  VALIGN="TOP"> 
					<a>Fecha de Constituci&oacuten / Nacimiento</a>
					</td>
					<td align="left" colspan="5" style= "font-size: 6pt"  font-family: sans-serif"  VALIGN="TOP"> 
						<div class="row">
						Estado civil: <br>
						Soltero    :  
						____
<!-- 						<input type="checkbox" > -->
						</div>
						Casado S    :
						____
<!-- 						<input type="checkbox" > -->
						
						
						
						<div>
						Si es casado favor de anotar:      
						<br>
						SociedadConyugal    :
						____
<!-- 						<input type="checkbox" > -->
						<br>
						
						Separaci&oacute;n deBienes    :
						____
<!-- 						<input type="checkbox" > -->
						</div>
					</td>
					</tr>
					</table>
				<table class="" width="100%" cellpadding="0" cellspacing="0" border ="1"  >
					<TR >
					<td align="left" colspan="5" style= "font-size: 6pt" width="33%" font-family: sans-serif"  VALIGN="TOP"> 
					RFC (C&eacute;dula Fiscal) ID TAX
					<br>
					</td>
										 
					
					<td align="left" colspan="5" style= "font-size: 6pt ; background-color: grey" width="33%" font-family: sans-serif"  VALIGN="TOP">  
					Nota: En el caso de ser persona f&iacute;sica y no proporcionar RFC, su CFDI<br>
					por adquisici&oacute;n ser&aacute; tratado como p&uacute;blico en general. Y en todos los<br>
					casos se facturara el monto total del contrato.
					</td >
					<td align="left" colspan="5" style= "font-size: 6pt" width="33%"  font-family: sans-serif"  VALIGN="TOP">
					CURP
					</td>
					</TR>					
			
					</table>
				<table class="" width="100%" cellpadding="0" cellspacing="0" border ="1"  >
					<TR >
					<td align="left" colspan="5" style= "font-size: 6pt" width="35%"  font-family: sans-serif"  VALIGN="TOP">
					Actividad, ocupaci&oacute;n, profesi&oacute;n, actividad o giro del negocio
					al que se dedique el cliente
					<br>
					</td>
					<td align="left" colspan="5" style= "font-size: 6pt" width="40%"  font-family: sans-serif"  VALIGN="TOP">

					Empleado de gobierno Especifique puesto y dependencia
					</td >
					<td align="left" colspan="5" style= "font-size: 6pt" width="25%"  font-family: sans-serif"  VALIGN="TOP">
						Nacionalidad<br>
						<br>
						<br>
						Mexicana &nbsp;&nbsp;&nbsp;&nbsp;
						Extranjera _________
					</td>
					</TR>
			</table>
				<table class="" width="100%" cellpadding="0" cellspacing="0" border ="1"  >
					<TR >
					<td align="left" colspan="5" style= "font-size: 6pt" width="70%"  font-family: sans-serif"  VALIGN="TOP">
					Actividad, ocupaci&oacute;n, profesi&oacute;n, actividad o giro del negocio
al que se dedique el cliente
					<br>
					<br>
					</td>
					<td align="left" colspan="5" style= "font-size: 6pt" width="15%"  font-family: sans-serif"  VALIGN="TOP">
					N&uacute;mero exterior
					</td>
					<td align="left" colspan="5" style= "font-size: 6pt" width="15%"  font-family: sans-serif"  VALIGN="TOP">
					N&uacute;mero interior
					</td>
					</TR>
			</table>
			
			<table class="" width="100%" cellpadding="0" cellspacing="0" border ="1"  >
					<TR >
					<td align="left" colspan="5" style= "font-size: 6pt" width="40%"  font-family: sans-serif"  VALIGN="TOP">
					Colonia
					<br>
					<br>
					</td>
					<td align="left" colspan="5" style= "font-size: 6pt" width="20%"  font-family: sans-serif"  VALIGN="TOP">
					Ciudad o Delegaci&oacute;n
					</td>
					<td align="left" colspan="5" style= "font-size: 6pt" width="20%"  font-family: sans-serif"  VALIGN="TOP">
					Estado o Delegaci&oacute;n
					</td>
					
					<td align="left" colspan="5" style= "font-size: 6pt" width="20%"  font-family: sans-serif"  VALIGN="TOP">
					C&oacute;digo postal
					</td>
					</TR>
			</table>
		
						<table class="" width="100%" cellpadding="0" cellspacing="0" border ="1"  >

					<TR >
					<td align="left" colspan="5" style= "font-size: 6pt" width="33%"  font-family: sans-serif"  VALIGN="TOP">
					N&uacute;mero de tel&eacute;fono fijo(incluir lada)
					<br>
					
					</td>
					<td align="left" colspan="5" style= "font-size: 6pt" width="33%"  font-family: sans-serif"  VALIGN="TOP"> 
					N&uacute;mero de tel&eacute;fono celular (incluir lada)
					</td>
					<td align="left" colspan="5" style= "font-size: 6pt" width="33%"  font-family: sans-serif"  VALIGN="TOP">
					Correo electr&oacute;nico
					</td>
					</TR>
			</table>
			
						<table class="" width="100%" cellpadding="0" cellspacing="0" border ="1"  >

					<TR >
					<td align="left"  style= "font-size: 6pt" width="33%"  font-family: sans-serif"  VALIGN="TOP">
					Persona f&iacute;sica/moral
					<br>
					<br>
					</td>
					<td align="left"  style= "font-size: 6pt" width="33%"  font-family: sans-serif"  VALIGN="TOP">  
					Uso CFDI
					</td>
					
					<td align="left" rowspan="2" style= "font-size: 6pt" width="33%"  font-family: sans-serif"  VALIGN="TOP">
					Medio por el que se enter&oacute; del desarrollo:
					</td>
					</tr>
					<tr>
					<td align="left"  style= "font-size: 6pt" width="33%"  font-family: sans-serif"  VALIGN="TOP">
					Cuenta bancaria
					<br>
					<br>
					</td>
					<td align="left"  style= "font-size: 6pt" width="33%"  font-family: sans-serif"  VALIGN="TOP">  
					Banco
					</td>
					</TR>
			</table>
			
				   <table class="reportSubTitle" width="100%" cellpadding="0" cellspacing="0" style= "background-color: black">
				<tr>
				<td>
				  <p style="color:WHITE ";>Representante Legal / Apoderado Legal</p>
				</td>		
				</tr>
				</table> 
			
						<table class="" width="100%" cellpadding="0" cellspacing="0" border ="1"  >

					<TR >
					<td align="left"  style= "font-size: 6pt" width="66%"  font-family: sans-serif"  VALIGN="TOP">
					Nombre(s), apellido paterno, apellido materno (sin abreviaturas)
					<br>
					<br>
					</td>
					<td align="left"  style= "font-size: 6pt" width="33%"  font-family: sans-serif"  VALIGN="TOP">
					RFC
					</td>
					</TR>
			</table>
			
						<table class="" width="100%" cellpadding="0" cellspacing="0" border ="1"  >

					<TR >
					<td align="left"  style= "font-size: 6pt" width="60%"  font-family: sans-serif"  VALIGN="TOP">
					CURP
					<br>
					<br>
					</td>
					<td align="left"  style= "font-size: 6pt" width="40%"  font-family: sans-serif"  VALIGN="TOP"> 
					Fecha de Nacimiento
					</td>
					<td>
					
					</td>
					</TR>
			</table>
			
				<table class="reportSubTitle" width="100%" cellpadding="0" cellspacing="0" style= "background-color: black">
					<tr>
					<td>
					  <p style="color:WHITE ";>Beneficiarios</p>
					</td>		
					</tr>
				</table> 
			
						<table class="" width="100%" cellpadding="0" cellspacing="0" border ="1"  >

					<TR >
					<td align="left"  style= "font-size: 6pt"   font-family: sans-serif"  VALIGN="TOP">
						¿El cliente act&uacute;a en su nombre y cuenta propia?	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;___  SI 	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;		___  NO
					<br>
					<br>
					¿Existe alg&uacute;n beneficiario?		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;___  SI 	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;		___  NO
					<br>
					<br>
					
					Nota : en caso de existir beneficiario completar lasiguiente informaci&oacute;n:
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					Nombre y Firma del cliente o representante legal
					</TR>
				</table>
			
						<table class="" width="100%" cellpadding="0" cellspacing="0" border ="1"  >

					<TR >
					<td align="left"  style= "font-size: 6pt" width="66%"  font-family: sans-serif"  VALIGN="TOP">
					Denominaci&oacute;n: Nombre(s) , apellido paterno, apellido materno (sin abreviaturas)
					<br>
					<br>
					<br>
					</td>
					<td align="left"  style= "font-size: 6pt" width="33%"  font-family: sans-serif"  VALIGN="TOP">
					Porcentaje
					</td>
					</TR>
			</table>
			
						<table class="" width="100%" cellpadding="0" cellspacing="0" border ="1"  >

					<TR >
					<td align="left"  style= "font-size: 6pt" width="66%"  font-family: sans-serif"  VALIGN="TOP">
					Fecha de nacimiento
					<br>
					<br>
					<br>
					</td>
					<td align="left"  style= "font-size: 6pt" width="33%"  font-family: sans-serif"  VALIGN="TOP">
					Nacionalidad:
					<br>
					<br>
					Mexicana&nbsp;&nbsp;&nbsp; Extranjera___________________
					</td>
					</TR>
			</table>
			
						<table class="" width="100%" cellpadding="0" cellspacing="0" border ="1"  >

					<TR >
					<td align="left"  style= "font-size: 6pt" width="22%"  font-family: sans-serif"  VALIGN="TOP">
					Colonia
					<br>
					<br>
					</td>
					<td align="left"  style= "font-size: 6pt" width="22%"  font-family: sans-serif"  VALIGN="TOP">
					Ciudad o Delegaci&oacute;n
					<br>
					<br>
					</td>
						<td align="left"  style= "font-size: 6pt" width="22%"  font-family: sans-serif"  VALIGN="TOP">
					Estado o Provincia
					<br>
					<br>
					</td>
						<td align="left"  style= "font-size: 6pt" width="22%"  font-family: sans-serif"  VALIGN="TOP">
					C&oacute;digo Postal
					<br>
					<br>
					</td>
					</TR>
			</table>
			
			<table class="" width="100%" cellpadding="0" cellspacing="0" border ="1"  >

					<TR >
					<td align="left"  style= "font-size: 6pt" width="33%"  font-family: sans-serif"  VALIGN="TOP">
					N&uacute;mero de tel&eacute;fono fijo (incluir lada)
					<br>
					<br>
					</td>
					<td align="left"  style= "font-size: 6pt" width="33%"  font-family: sans-serif"  VALIGN="TOP">
					N&uacute;mero de tel&eacute;fono celular (Incluir lada)
					</td>
					<td align="left"  style= "font-size: 6pt" width="33%"  font-family: sans-serif"  VALIGN="TOP">
					Correo electr&oacute;nico
					</td>
					</TR>
			</table>
			
				<table class="reportSubTitle" width="100%" cellpadding="0" cellspacing="0" style= "background-color: black">
					<tr>
					<td>
					  <p style="color:WHITE ";>Beneficiario</p>
					</td>		
					</tr>
				</table> 
			
			<table class="" width="100%" cellpadding="0" cellspacing="0" border ="1"  >

					<TR >
					<td align="left"  style= "font-size: 6pt" width="66%"  font-family: sans-serif"  VALIGN="TOP">
					Denominaci&oacute;n: Nombre(s) , apellido paterno, apellido materno (sin abreviaturas)
					<br>
					<br>
					</td>
					<td align="left"  style= "font-size: 6pt" width="33%"  font-family: sans-serif"  VALIGN="TOP">
					Porcentaje
					</td>
					</TR>
			</table>
			
			<table class="" width="100%" cellpadding="0" cellspacing="0" border ="1"  >

					<TR >
					<td align="left"  style= "font-size: 6pt" width="66%"  font-family: sans-serif"  VALIGN="TOP">
					Fecha de nacimiento
					<br>
					<br>
					</td>
					<td align="left" colspan="5" style= "font-size: 6pt" width="33%"  font-family: sans-serif"  VALIGN="TOP">
						Nacionalidad<br>
						Mexicana&nbsp;&nbsp;&nbsp;
						Extranjera _________
					</td>
					</TR>
			</table>
			
			<table class="" width="100%" cellpadding="0" cellspacing="0" border ="1"  >
					<TR >
					<td align="left" colspan="5" style= "font-size: 6pt" width="40%"  font-family: sans-serif"  VALIGN="TOP">
					Colonia
					<br>
					<br>
					</td>
					<td align="left" colspan="5" style= "font-size: 6pt" width="20%"  font-family: sans-serif"  VALIGN="TOP">
					Ciudad o Delegaci&oacute;n
					</td>
					<td align="left" colspan="5" style= "font-size: 6pt" width="20%"  font-family: sans-serif"  VALIGN="TOP">
					Estado o Delegaci&oacute;n
					</td>
					
					<td align="left" colspan="5" style= "font-size: 6pt" width="20%"  font-family: sans-serif"  VALIGN="TOP">
					C&oacute;digo postal
					</td>
					</TR>
			</table>
			
		<table class="" width="100%" cellpadding="0" cellspacing="0" border ="1"  >

					<TR >
					<td align="left" colspan="5" style= "font-size: 6pt" width="33%"  font-family: sans-serif"  VALIGN="TOP">
					N&uacute;mero de tel&eacute;fono fijo(incluir lada)
					<br>
					<br>
					</td>
					<td align="left" colspan="5" style= "font-size: 6pt" width="33%"  font-family: sans-serif"  VALIGN="TOP"> 
					N&uacute;mero de tel&eacute;fono celular (incluir lada)
					</td>
					<td align="left" colspan="5" style= "font-size: 6pt" width="33%"  font-family: sans-serif"  VALIGN="TOP">
					Correo electr&oacute;nico
					</td>
					</TR>
			</table>
			
						<table class="reportSubTitle" width="100%" cellpadding="0" cellspacing="0" style= "background-color: black">
					<tr>
					<td>
					  <p style="color:WHITE ";>Beneficiario</p>
					</td>		
					</tr>
				</table> 
			
			<table class="" width="100%" cellpadding="0" cellspacing="0" border ="1"  >

					<TR >
					<td align="left"  style= "font-size: 6pt" width="66%"  font-family: sans-serif"  VALIGN="TOP">
					Denominaci&oacute;n: Nombre(s) , apellido paterno, apellido materno (sin abreviaturas)
					<br>
					<br>
					</td>
					<td align="left"  style= "font-size: 6pt" width="33%"  font-family: sans-serif"  VALIGN="TOP">
					Porcentaje
					</td>
					</TR>
			</table>
			
			<table class="" width="100%" cellpadding="0" cellspacing="0" border ="1"  >

					<TR >
					<td align="left"  style= "font-size: 6pt" width="66%"  font-family: sans-serif"  VALIGN="TOP">
					Fecha de nacimiento
					<br>
					<br>
					</td>
					<td align="left" colspan="5" style= "font-size: 6pt" width="33%"  font-family: sans-serif"  VALIGN="TOP">
						Nacionalidad<br>
						Mexicana&nbsp;&nbsp;&nbsp;
						Extranjera _________
					</td>
					</TR>
			</table>
			
			<table class="" width="100%" cellpadding="0" cellspacing="0" border ="1"  >
					<TR >
					<td align="left" colspan="5" style= "font-size: 6pt" width="40%"  font-family: sans-serif"  VALIGN="TOP">
					Colonia
					<br>
					<br>
					</td>
					<td align="left" colspan="5" style= "font-size: 6pt" width="20%"  font-family: sans-serif"  VALIGN="TOP">
					Ciudad o Delegaci&oacute;n
					</td>
					<td align="left" colspan="5" style= "font-size: 6pt" width="20%"  font-family: sans-serif"  VALIGN="TOP">
					Estado o Delegaci&oacute;n
					</td>
					
					<td align="left" colspan="5" style= "font-size: 6pt" width="20%"  font-family: sans-serif"  VALIGN="TOP">
					C&oacute;digo postal
					</td>
					</TR>
			</table>
			
		<table class="" width="100%" cellpadding="0" cellspacing="0" border ="1"  >

					<TR >
					<td align="left" colspan="5" style= "font-size: 6pt" width="33%"  font-family: sans-serif"  VALIGN="TOP">
					N&uacute;mero de tel&eacute;fono fijo(incluir lada)
					<br>
					<br>
					</td>
					<td align="left" colspan="5" style= "font-size: 6pt" width="33%"  font-family: sans-serif"  VALIGN="TOP"> 
					N&uacute;mero de tel&eacute;fono celular (incluir lada)
					</td>
					<td align="left" colspan="5" style= "font-size: 6pt" width="33%"  font-family: sans-serif"  VALIGN="TOP">
					Correo electr&oacute;nico
					</td>
					</TR>
			</table>
			<!-- Segunda pagina -->
	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" >
		<tr>
    
    	<td align="left" valign="bottom" style= "font-size: 10pt"  font-family:sans-serif;"	>
 		<b>INTEGRACI&Oacute;N DE EXPEDIENTE DE CLIENTES (Hoja 2 de 3)</b>
        </td>
        <td align="right" >
        <img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src = <%= GwtUtil.getProperUrl(sFParams, "img/valenciana.png") %> >
        </td>
        </tr>
        </table>
        		<br>
				<br>
				<br>
        
		    <table class="reportSubTitle" width="100%" cellpadding="0" cellspacing="0" style= "background-color: black">
				<tr>
				<td>

				  <p style="color:WHITE ";>Datos del cliente</p>
				</td>		
				</tr>
			</table> 
			
						<table class="" width="100%" cellpadding="0" cellspacing="0" border ="1"  >

					<TR >
					<td align="left" colspan="5" style= "font-size: 6pt" width="100%"  font-family: sans-serif"  VALIGN="TOP">
					Declaro bajo protesta de decir verdad, que la informaci&oacute;n contenida en la presente forma, as&iacute; como en la documentaci&oacute;n que he proporcionado para respaldar el presente documento es ver&iacute;dica y autentica. Estoy de acuerdo
					que en caso de ser persona f&iacute;sica y no proporcionar el RFC, los CFDI correspondientes a la operaci&oacute;n ser&aacute;n tratados como p&uacute;blicos en general.<br><br>
					
					Estoy enterado que la ley federal para la prevenci&oacute;n e identificaci&oacute;n de operaciones con recursos de procedencia il&iacute;cita (www.diputados.gob.mx/LeyesBiblio/pdf/LFPIORPI.pdf)(LFPIORPI) califica toda compraventa de inmuebles
					como actividad vulnerable, en su Art&iacute;culo 17, fracc. V y por tanto estoy obligado conforme al Art. 22 primer p&aacute;rrafo en proporcionar a la empresa toda la documentaci&oacute;n requerida en el Art. 18 de dicho ordenamiento consistente
					en identificaci&oacute;n, demostrar la actividad u ocupaci&oacute;n o declarar al dueño beneficiario.

					<!-- INicio de marcatexto lol -->
					<p style="background-color: grey">Estoy enterado que por disposici&oacute;n de la LFPIORPIse dar&aacute; aviso a las autoridades por las operaciones que en lo individual o colectivo sean equivalentes o superiores a 8,025 UMA ($678,032.00 para 2019).</p>
					<p style="background-color: grey">Estoy enterado que por disposici&oacute;n de la LFPIORPI se establece la restricci&oacute;n de liquidar o pagar, as&iacute; como de aceptar la liquidaci&oacute;n o el pago de actos u operaciones, mediante el uso de efectivo hasta el l&iacute;mite de 8,025 UMA
					($678,032.00 para 2019). El tope para pago en efectivo antes mencionado no elimina la obligaci&oacute;n de declarar los pagos cuyo monto sea superior a $100,000 en efectivo en el transcurso del mes.</p>
					<p style="background-color: grey">Estoy enterado que el notario tiene la obligaci&oacute;n de incluir en la escritura de compra-venta el detalle de cada uno de los pagos que haya hecho para el terreno, as&iacute; como de reportar las operaciones que sobrepasen el l&iacute;mite
					antes mencionado.</p>

					Asimismo, declaro que yo soy el propietario titular del Inmueble sujeto a la operaci&oacute;n de compra-venta y que, en caso de existir alg&uacute;n dueño beneficiario, me obligo a proporcionar la informaci&oacute;n del dueño beneficiario o bien
					presentar una declaratoria de mi parte afirmando que no cuenta con la informaci&oacute;n fiscal requerida.
					<br>
					Acepto que, en cumplimiento con el Art. 21 segundo p&aacute;rrafo de la LFPIORPI y en caso de una negativa de mi parte en proporcionar la documentaci&oacute;n requerida, la empresa tiene la obligaci&oacute;n, sin responsabilidad alguna, de
					efectuar la cancelaci&oacute;n de la operaci&oacute;n de compra-venta aplicando la penalidad correspondiente y efectuar la devoluci&oacute;n del dinero remanente a mi favor en caso de existir &eacute;ste.
					<br>
					Finalmente, hago de su conocimiento que conozco las sanciones que establece el Art. 62 de la LFPIORPI que a la letra dice:
					<br>
					“Se sancionar&aacute; con prisi&oacute;n de dos a ocho años y con quinientos a dos mil d&iacute;as de multa conforme el C&oacute;digo Penal Federal, a quien:
					<br>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					I. Proporcione de manera dolosa a quienes deban dar Avisos, informaci&oacute;n, documentaci&oacute;n, datos o im&aacute;genes que sean falsos, o sean completamente 
					<br>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					ilegibles, para serincorporados en aquellos que deban presentarse;
					<br>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					II. De manera dolosa, modifique o altere informaci&oacute;n, documentaci&oacute;n, datos o im&aacute;genes destinados a ser incorporados a los Avisos, o incorporados en 
					<br>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					avisos presentados “.	
					<br>
					A la persona o grupo de personas que por medio de otra o de cualquier acto, obtiene el beneficio derivado de &eacute;stos y es quien, en &uacute;ltima instancia,
					<br>
					ejerce los derechos de usos, goce, disfrute, aprovechamientos o disposici&oacute;n de un bien o servicio.
					<br>
					<br>
					<br>				
					</td>
					</TR>
			</table>
			
			<table class="reportSubTitle" width="100%" cellpadding="0" cellspacing="0" style= "background-color: black">
				<tr>
				<td>
				  <p style="color:WHITE ";>Condiciones de Venta</p>
				</td>		
				</tr>
			</table> 
			
			<table class="" width="100%" cellpadding="0" cellspacing="0" border ="1"  >
					<TR >	
					<td align="left" colspan="5" style= "font-size: 6pt" width="33%"  font-family: sans-serif"  VALIGN="TOP">
					<br>
					<br>
					Privada: ______________
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					Precio de venta: _______________________
					<br>
					<br>
					Manzana: _____________
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					Plazo: ______________
					<br>
					<br>
					Lote: _____________
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					Enganche: _____________________
					<br>
					<br>
					Clasificaci&oacute;n: _______________
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					Especificar como se pagar&aacute; el enganche y fecha de primera mensualidad:
					<br>
					<br>
					M2: ___________________
					<br>
					<br>
					<br>
					<br>
					<br>
					<br>
					<br>
					</td>
					</TR>
			</table>
			
			<table class="reportSubTitle" width="100%" cellpadding="0" cellspacing="0" style= "background-color: grey">
				<tr>
				
				<td align="left" colspan="5" style= "font-size: 8pt; color:WHITE" width="100%"  font-family: sans-serif"  VALIGN="top">
				<br>
				  Cliente: Verique que la informaci&oacute;n este correctamente descrita
				  
				</td>		
				</tr>
			</table> 
			
				<table class="" width="100%" cellpadding="0" cellspacing="0" border ="1"  >

					<TR >
					<td align="center"  style= "font-size: 6pt" width="33%"  font-family: sans-serif"  VALIGN="top">
					Nombre del cliente o Representante Legal
					</td>
					<td align="center"  style= "font-size: 6pt" width="33%"  font-family: sans-serif"  VALIGN="top">
					Firma
					</td>
					<td align="center"  style= "font-size: 6pt" width="33%"  font-family: sans-serif"  VALIGN="top">
					Fecha
					<br>
					<br>
					<br>
					<br>
					<br>
					<br>
					<br>
					<br>
				
					</td>
					
					</TR>
			</table>
					
					<br>
					<br>
					<br>
					<br>
					<br>
			<!-- tercer pagina -->		
			<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" >
		<tr>
    
    	<td align="left" valign="bottom" style= "font-size: 10pt"  font-family:sans-serif;"	>
 		<b>INTEGRACI&Oacute;N DE EXPEDIENTE DE CLIENTES (Hoja 3 de 3)</b>
        </td>
        <td align="right" >
        <img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src = <%= GwtUtil.getProperUrl(sFParams, "img/valenciana.png") %> >
        </td>
        </tr>
        </table>
                
		    <table class="reportSubTitle" width="100%" cellpadding="0" cellspacing="0" style= "background-color: black">
				<tr>
				<td>

				  <p style="color:WHITE ";>Documentos Anexos al Expediente</p>
				</td>		
				</tr>
			</table> 
			<table class="" width="100%" cellpadding="0" cellspacing="0" border ="1"  >

					<TR >
					<td align="left"  style= "font-size: 6pt" width="33%"  font-family: sans-serif"  VALIGN="top">
					Documentos del Cliente Persona F&iacute;sica:
					<br>
					<br>
					<ul>
					<li type="square">Identificaci&oacute;n oficial<a>&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|__|</a></li>
					
					<li type="square">Acta de nacimiento
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|__|</li>
					<li type="square">Comprobante de Domicilio (menor a 3 mses de antigüedad)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|__|</li>
					<li type="square">RFC (C&eacute;dula fiscal)<a>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|__|</a></li>
					<li type="square">CURP<a>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|__|</a></li>
					<li type="square">Caratula de estado de cuenta bancario
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;|__|</li>
					</ul>
					Documentos del Cliente Persona Moral:			
					<ul>
					<li type="square">Acta constitutiva<a>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|__|</a></li>
					<li type="square">RFC (C&eacute;dula fiscal)<a>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|__|</a></li>
					
					<li type="square">Comprobante de Domicilio (menor a 3 mesesde antigüedad)
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|__|</li>
					</ul>	
					Documentos de Representante legal:		
					<ul>
					<li type="square">Identificaci&oacute;n oficial<a>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|__|</a></li>
					<li type="square">Testimonio o instrumento certificado que contenga los poderes delrepresentante legal<a>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|__|</a></li>
					<li type="square">RFC (C&eacute;dula fiscal como persona f&iacute;sica)
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|__|</li>
					<li type="square">CURP del representante legal<a>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|__|</a></li>
					<li type="square">Comprobante de Domicilio (menor a 3 meses de antigüedad)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|__|</li>
					</ul>
					Otros documentos:	
					<ul>
					<li type="square">Acta de Matrimonio (Si aplica)<a>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|__|</a></li>
					<li type="square">Identificaci&oacute;n oficial c&oacute;nyuge (si aplica)
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|__|</li>
					<li type="square">Acta de nacimiento c&oacute;nyuge (si aplica)
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|__|</li>
					<li type="square">RFC c&oacute;nyuge (Si aplica)<a>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|__|</a></li>
					<li type="square">CURP c&oacute;nyuge (Si aplica)<a>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|__|</a></li>
					<li type="square">Comprobante de Domicilio (Si aplica)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|__|</li>
					</ul>
					</td>
					</TR>
			</table>
			<table class="reportSubTitle" width="100%" cellpadding="0" cellspacing="0" style= "background-color: black">
				<tr>
				<td>
				  <p style="color:WHITE ">Declaraci&oacute;n de Embajador, Coordinador y Mesa de Control</p>
				</td>		
				</tr>
			</table> 
				<table class="reportSubTitle" width="100%" cellpadding="0" cellspacing="0" style= "background-color: grey">
				<tr>
				
				<td align="left" colspan="5" style= "font-size: 8pt; color:WHITE" width="100%"  font-family: sans-serif"  VALIGN="top">
				<br>
				  Cliente: Verique que la informaci&oacute;n este correctamente descrita	  
				</td>		
				</tr>
			</table> 
			<table class="" width="100%" cellpadding="0" cellspacing="0" border ="1"  >

					<TR >
					<td align="center"  style= "font-size: 6pt" width="33%"  font-family: sans-serif"  VALIGN="top">
					Nombre del cliente o Representante Legal
					</td>
					<td align="center"  style= "font-size: 6pt" width="33%"  font-family: sans-serif"  VALIGN="top"> 
					Firma
					</td>
					<td align="center"  style= "font-size: 6pt" width="33%"  font-family: sans-serif"  VALIGN="top">
					Fecha
					<br>
					<br>
					<br>
					<br>
					<br>
					<br>
					<br>
					<br>
					<br>
					</td>
					</TR>
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



