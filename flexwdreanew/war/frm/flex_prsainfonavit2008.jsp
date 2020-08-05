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
<%@page import="com.symgae.server.SFServerUtil"%>
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


<%
	try {		
	    // Si es llamada externa, utilizar llave desencriptada
	    int propertySaleId = 0;
	    if (isExternal) propertySaleId = decryptId;
	    else propertySaleId = Integer.parseInt(request.getParameter("foreignId"));	    
	    
	    //Venta		
		PmPropertySale pmPropertySale = new PmPropertySale(sFParams);
		bmoPropertySale = (BmoPropertySale)pmPropertySale.get(propertySaleId);
		
		//Venta
		BmoOrder bmoOrder = new BmoOrder();
		PmOrder pmOrder = new PmOrder(sFParams);
		bmoOrder = (BmoOrder)pmOrder.get(bmoPropertySale.getOrderId().toInteger());
		
		//Desarrollo
		BmoDevelopment bmoDevelopment = new BmoDevelopment();
		PmDevelopment pmDevelopment = new PmDevelopment(sFParams);
		bmoDevelopment= (BmoDevelopment)pmDevelopment.get(bmoPropertySale.getBmoProperty().getBmoDevelopmentBlock().getBmoDevelopmentPhase().getDevelopmentId().toInteger());
		
		//Vivienda
		BmoProperty bmoProperty = new BmoProperty();
		PmProperty pmProperty = new PmProperty(sFParams);
		bmoProperty = (BmoProperty)pmProperty.get(bmoPropertySale.getPropertyId().toInteger());
		
		//Reg. Paquetes
		BmoDevelopmentRegistry bmoDevelopmentRegistry = new BmoDevelopmentRegistry();
		PmDevelopmentRegistry pmDevelopmentRegistry = new PmDevelopmentRegistry(sFParams);
		if(bmoProperty.getDevelopmentRegistryId().toInteger() > 0)
			bmoDevelopmentRegistry = (BmoDevelopmentRegistry)pmDevelopmentRegistry.get(bmoProperty.getDevelopmentRegistryId().toInteger());
		
			//Reg. Paquetes - Acreedor
				BmoSupplier bmoSupplierDvrg = new BmoSupplier();
				PmSupplier pmSupplier = new PmSupplier(sFParams);
				if(bmoProperty.getDevelopmentRegistryId().toInteger() > 0){
					if(bmoDevelopmentRegistry.getSupplierId().toInteger() > 0)
						bmoSupplierDvrg = (BmoSupplier)pmSupplier.get(bmoDevelopmentRegistry.getSupplierId().toInteger());
				}
				
				//Tels del Proveedor
			    PmSupplierPhone pmSupplierPhone = new PmSupplierPhone(sFParams);
			    BmoSupplierPhone bmoSupplierPhone = new BmoSupplierPhone();
			    PmConn pmConnSupplierPhone = new PmConn(sFParams);
			    pmConnSupplierPhone.open();

			    String movilDvrg = "", telDvrg = "";
			    String sqlSuplPhone = " SELECT * FROM supplierphones WHERE suph_supplierid = " + bmoSupplierDvrg.getId() + " ORDER BY suph_type DESC";
			    pmConnSupplierPhone.doFetch(sqlSuplPhone);
			    System.out.println("sqlSuplPhone: "+sqlSuplPhone);
				while (pmConnSupplierPhone.next()) {
					bmoSupplierPhone = (BmoSupplierPhone)pmSupplierPhone.get(pmConnSupplierPhone.getInt("suph_supplierphoneid"));
					if(bmoSupplierPhone.getType().equals(BmoSupplierPhone.TYPE_WORK))
						telDvrg  = bmoSupplierPhone.getNumber().toString();
					if(bmoSupplierPhone.getType().equals(BmoSupplierPhone.TYPE_MOBILE))
						movilDvrg  = bmoSupplierPhone.getNumber().toString();
				}

				pmConnSupplierPhone.close();
		
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
<head>
<title>:::<%= appTitle %>:::</title>
<link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>"> 
 <link rel="stylesheet" type="text/css" href="../css/flexwm.css"> 
</head>
<body class="default" <%= permissionPrint %>>

<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
	<tr>
	    <td align="center">
	        <img src="<%= GwtUtil.getProperUrl(sFParams, "/img/solicitud_infonavit.png")%>" width="35%">
	    </td>
    </tr>
</table>

<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
<caption>
	<b>1. INFORMACI&Oacute;N DEL CR&Eacute;DITO</b>
</caption>
<tr>
	<td>
        <table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="1" align="center" cellpadding="0" cellspacing="0" >
			<tr> 
		    	<td>
		   	 		<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="0" cellpadding="0" cellspacing="0" valign="top">
		        		<tr > 
				        	<td width="10%" align="left" class="reportSubTitle"  style="font-size: 8pt" >
				        		<b>* PRODUCTO: </b>
				        	</td>
                            <td width="5%" class="reportSubTitle"  style="font-size: 8pt" align="right" >
		          				&nbsp;Infonavit
		          			</td>
		          			<td width="" align="left" height="3">
		          				<input type="radio" name="tipocredito" checked value="radiobutton"  >
		          			</td>
		          			<td width="" class="reportSubTitle"  style="font-size: 8pt" align="right"  >
		          				&nbsp;Infonavit Total
		          			</td>
		          			<td width="" align="left">
		          				<input type="radio" name="tipocredito" checked value="radiobutton" >
		          			</td>
					        <td width="8%" class="reportSubTitle"  style="font-size: 8pt "align="right"  >
					        	&nbsp;COFINAVIT
					        </td>
					        <td width="" align="left">
					        	<input type="radio" name="tipocredito" value="radiobutton" >
					        </td>
					        <td width="20%" class="reportSubTitle"  style="font-size: 8pt"align="right"  >
					        	&nbsp;COFINAVIT Ingresos Adicionales
					        </td>
					        <td width="" align="left">
					        	<input type="radio" name="tipocredito" value="radiobutton" >
					        </td>
					        <td class="reportSubTitle"  style="font-size: 8pt"width="" valign="center" align="right" >
					        	Entidad Financiera: 
					        </td> 
					        <td class="reportSubTitle"  style="font-size: 8pt"width="" valign="center" align="left" >
					        	<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="30" style="">
					        </td>
		             	</tr>
			      	</table>
		    	</td>
			</tr>
		  	<tr> 
		   		<td>
		   			<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="0" cellspacing="0" cellpadding="0" valign="top">
		        		<tr> 
					        <td width="15%" align="left" class="reportSubTitle"  style="font-size: 8pt">
					        	<b>* TIPO DEL CR&Eacute;DITO:</b>
					        </td>
					        <td width="8%" class="reportSubTitle"  style="font-size: 8pt" align="right" >
					        	&nbsp;INDIVIDUAL
					        </td>
					        <td width="5%" align="left">
					        	<input type="radio" name="credito" value="radiobutton">
					        </td>
					        <td width="10%" class="reportSubTitle"  style="font-size: 8pt" align="right" >
					        	&nbsp;CONYUGAL
					        </td>
					        <td width="" align="left">
					        	<input type="radio" class="reportSubTitle"  style="font-size: 9pt"name="credito" value="radiobutton">
					        </td>
				       </tr>
		      		</table>
			   </td>
		  	</tr>
		  	<tr> 
		    	<td width="100%">
					<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="0" cellspacing="0" cellpadding="0" valign="top">
		        		<tr>
					        <td  width="15%" class="reportSubTitle"  style="font-size: 8pt">
					        	<b>* DESTINO DEL CR&Eacute;DITO:</b>
					        </td>
					        <td width="12%" class="reportSubTitle"  style="font-size: 8pt" valign="center" align="right" >
					        	Comprar una Vivienda
					        </td>
					        <td width="2%" class="reportSubTitle"  style="font-size: 8pt" valign="center" align="left" >
					        	<input type="radio" name="destino" checked value="radiobutton">
					        </td>
					        <td width="12%" class="reportSubTitle"  style="font-size: 8pt"valign="center" align="right" >
					        	Construir tu Vivienda 
					        </td>
					        <td width="2%" class="reportSubTitle"  style="font-size: 8pt"valign="center" align="left" >
					        	<input type="radio" name="destino"  value="radiobutton">
					        </td>
					        <td width="20%" class="reportSubTitle"  style="font-size: 8pt"valign="center" align="right" >
					        	Reparar, Ampliar o Mejorar tu Vivienda 
					        </td> 
							<td width="2%" class="reportSubTitle"  style="font-size: 8pt"valign="center" align="left" >
								<input type="radio" name="destino"  value="radiobutton">
							</td>
					        <td width="22%" class="reportSubTitle"  style="font-size: 8pt"valign="center" align="right" >
					        	Pagar el pasivo o la hipoteca de tu vivienda 
					        </td> 
					        <td width="2%" class="reportSubTitle"  style="font-size: 8pt"valign="center" align="left" >
					        	<input type="radio" name="destino"  value="radiobutton">
					        </td>
				       </tr>
		      		</table>  
		      	</td>
		  	</tr>
		</table>
	</td>
</tr>
<tr>
	<td>
		<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="0" align="center" cellpadding="0" cellspacing="0" bordercolor="">  
			<tr>
		    	<td align="center" width="100%">
		    		<b>2. DATOS DE IDENTIFICACI&Oacute;N DEL DERECHOHABIENTE</b>
		    	</td>
		  	</tr>
		</table>
	</td>
</tr>
<tr> 
	<td>
		<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="1" align="center" cellpadding="0" cellspacing="0" >  
			<tr>
				<td>
		  			<table class="reportSubTitle"  style="font-size: 9pt" width="100%" border="0" align="center" cellpadding="0" cellspacing="0" bordercolor="">  
		        		<tr style="font-weight: bold">       						
		        			<td width="22%" class="reportSubTitle"  style="font-size: 9pt">
			        			&nbsp;
								<% if(bmoCustomer.getNss().toString().equals("")){ %>
										<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="25">
				  				<% } else { %>
				  						<%= bmoCustomer.getNss().toString().toUpperCase() %>
				  				<% } %>
			          		</td>
		          			<td width="30%" colspan="2" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" >
			          			&nbsp;
				  				<% if(bmoCustomer.getCurp().toString().equals("")){ %>
										<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="25">
				  				<% } else { %>
				  						<%= bmoCustomer.getCurp().toString().toUpperCase()%>
				  				<% } %>
		    	      		</td>
		          			<td width="30%" class="reportSubTitle"  style="font-size: 9pt">
			          			&nbsp;
				  				<% if(bmoCustomer.getRfc().toString().equals("")){ %>
				  						<input type="text" class="reportSubTitle"  style="font-size: 9pt font-weight: bold" size="14" value="">
				  				<% } else { %>
				  						<%= bmoCustomer.getRfc().toString().toUpperCase() %>
				  				<% } %>
		          			</td>
						</tr>
		        		<tr> 
						   	<td colspan="" class="reportSubTitle"  style="font-size: 8pt">
						   		&nbsp;*NUMERO DE SEGURIDAD SOCIAL (NSS)
						   	</td>
						   	<td colspan="2" class="reportSubTitle"  style="font-size: 8pt">
						   		CURP&nbsp;
						   	</td>
						   	<td  class="reportSubTitle"  style="font-size: 8pt">
						   		R.F.C
						   	</td>
						</tr>
		       			<tr style="font-weight: bold"> 
		          			<td  width="50%" class="reportSubTitle"  style="font-size: 9pt"colspan = "2" >
			          			&nbsp;
				  				<% if(bmoCustomer.getFatherlastname().toString().equals("")){ %>
										<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="60" value="">
				  				<% } else { %>
				  						<%= bmoCustomer.getFatherlastname().toString().toUpperCase() %>
				  				<% } %>
		          			</td>
		          			<td class="reportSubTitle"  style="font-size: 9pt"colspan = "2" >
			          			&nbsp;
				  				<% if(bmoCustomer.getMotherlastname().toString().equals("")){ %>
				  						<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="60" value="">
				  				<% } else { %>
				  						<%= bmoCustomer.getMotherlastname().toString().toUpperCase() %>
				  				<% } %>	      							
			      			</td>
		   				</tr>
		   				<tr>
		      				<td colspan = "2"  class="reportSubTitle"  style="font-size: 8pt">
		      					&nbsp;*APELLIDO PATERNO
		      				</td>
											
		          			<td colspan = "2" class="reportSubTitle"  style="font-size: 8pt">
		          				*APELLIDO MATERNO
		          			</td>
			   			</tr>	  
			   			<tr style="font-weight: bold">
		 		  			<td colspan = "4" class="reportSubTitle"  style="font-size: 9pt" >
			 		  			&nbsp;
				  				<% if(bmoCustomer.getFirstname().toString().equals("")){ %>
				  						<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="120" value="">
								<% } else { %>
			  							<%= bmoCustomer.getFirstname().toString().toUpperCase() %>
	  							<% } %>	  							
		 		  			</td>
		 	   			</tr>   
		       			<tr>	  
			  	  			<td colspan = "4" class="reportSubTitle"  style="font-size: 8pt">
			  	  				&nbsp;*NOMBRE(S)
			  	  			</td>
		        		</tr>
		        		<tr>
						   	<td  colspan = "4" class="reportSubTitle"  style="font-size: 8pt">
						   		&nbsp;*DOMICILIO ACTUAL DEL DERECHOHABIENTE
						   	</td>
		        		</tr>
		        		<tr style="font-weight: bold"> 
						   	<td colspan = "4" class="reportSubTitle"  style="font-size: 9pt">&nbsp;
						   	<% if(bmoCustomerAddress.getStreet().toString().equals("") && bmoCustomerAddress.getNumber().toString().equals("")){ %>
					   				<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="100" value="">
			  				<% } else { %>
			  						<%= bmoCustomerAddress.getStreet().toString().toUpperCase() %> <%= bmoCustomerAddress.getNumber().toString().toUpperCase() %> 
			  				<% } %>					    		
						   	</td>
						</tr> 
						<tr>
						   	<td colspan = "4" class="reportSubTitle"  style="font-size: 8pt">
						   		&nbsp;*CALLE Y NUMERO
						   	</td>
						</tr>
						<tr >
		          			<td colspan="2" class="reportSubTitle"  style="font-size: 9pt"style="font-weight: bold">
			          			&nbsp;
				  				<% if(bmoCustomerAddress.getNeighborhood().toString().equals("")){ %>
										<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="40" value="">
				  				<% } else { %>
				  						<b><%= bmoCustomerAddress.getNeighborhood().toString().toUpperCase() %></b>
				  				<% } %>
		          			</td>
			      			<td class="reportSubTitle"  style="font-size: 9pt"colspan="2" >
				      			&nbsp;
				  				<% if(bmoCustomerAddress.getBmoCity().getBmoState().getName().toString().equals("")){ %>
				  						<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="30" value="">
				  				<% } else { %>
				  						&nbsp;<b><%= bmoCustomerAddress.getBmoCity().getBmoState().getName().toString().toUpperCase() %></b>
				  				<% } %>
			      			</td>
		        		</tr>
		        		<tr>
			      			<td  colspan="2" class="reportSubTitle"  style="font-size: 8pt">
			      				&nbsp;*COLONIA O FRACCIONAMIENTO
			      			</td>
		          			<td colspan="2" class="reportSubTitle"  style="font-size: 8pt">
		          				&nbsp;* ENTIDAD
		          			</td>	
						</tr>
		        		<tr>
		        			<td colspan="4" style="font-weight: bold">
		        				<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="0" cellspacing="0" cellpadding="0" valign="top">
					        		
					       			<tr style="font-weight: bold">
					          			<td width="" colspan="2" class="reportSubTitle"  style="font-size: 9pt">
						          			&nbsp;
					          				<% if(bmoCustomerAddress.getBmoCity().getName().toString().equals("")){ %>
					          						<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="25" value="">
					          				<% } else { %>
					          						<%= bmoCustomerAddress.getBmoCity().getName().toString().toUpperCase() %>
					          				<% } %>
					          			</td>
					          			<td colspan="" class="reportSubTitle"  style="font-size: 9pt"align="left">
						          			&nbsp;
						          			<% if(bmoCustomerAddress.getZip().toString().equals("")){ %>
													<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="10" value="">
						          			<% } else { %>
						          					<%= bmoCustomerAddress.getZip().toString().toUpperCase() %>
						          			<% } %>
					          			</td>  
					          			            
					        		</tr>
					        		<tr>
					   			   		<td colspan="2" class="reportSubTitle"  style="font-size: 8pt">
					   			   			&nbsp;*MUNICIPIO O DELEGACI&Oacute;N
					   			   		</td>	
										<td colspan="" class="reportSubTitle"  style="font-size: 8pt"align="left" >
											&nbsp;* C&Oacute;DIGO POSTAL
										</td> 	
						      		</tr>
					        		<tr>
					        			<td colspan="2" colspan="" class="reportSubTitle" style="font-size: 8pt">
					        				&nbsp;*TELEFONO: (<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="3" value=""> )
					        				<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="10" value="<%= casa %>">
					        			</td>
					        			<td  class="reportSubTitle"  style="font-size: 8pt" colspan="" >
							    			&nbsp;&nbsp;&nbsp;&nbsp;*CELULAR: &nbsp;044 <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="10" value="<%= movil%>">
							    		</td>	
									</tr>
									<tr>
							        	<td width="" colspan="2" class="reportSubTitle"  style="font-size: 8pt">
							        		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							        		* LADA 
							        		&nbsp;&nbsp;&nbsp;
								       		*N&Uacute;MERO
								       	</td>
										<td colspan="" class="reportSubTitle"  style="font-size: 8pt">
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
											* N&Uacute;MERO
										</td>
									</tr>
									<tr>
					        			<td  class="reportSubTitle"  style="font-size: 8pt"colspan="3" width="%" >
					        				&nbsp;*CORREO ELECTRONICO: <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="50" value="<%= personalEmail%>">
					        			</td>
									</tr>
									<tr>
									  	<td width="15%" class="reportSubTitle"  style="font-size: 8pt">
									       	&nbsp;*GENERO: 
									       	M <input type="radio" name="gender" <%--= (boAllocation.getBoCustomer().getGender().toString().equals("t")) ? "checked" : "" --%> value="radiobutton">
									      	F<input type="radio" name="gender" <%--= (boAllocation.getBoCustomer().getGender().toString().equals("f")) ? "checked" : "" --%> value="radiobutton">
									   	</td>	
									   	<td width="30%" colspan="" class="reportSubTitle"  style="font-size: 8pt">
									   		*ESTADO CIVIL:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; SOLTERO<input type="radio" name="civil" <%= (bmoCustomer.getMaritalStatus().toChar() == BmoCustomer.MARITALSTATUS_SINGLE ) ? "checked" : "" %> value="radiobutton">
									   		CASADO<input type="radio" name="civil" <%= (bmoCustomer.getMaritalStatus().toChar() == BmoCustomer.MARITALSTATUS_MARRIED ) ? "checked" : "" %> value="radiobutton">
							          	</td>
								    </tr>  			  
								    <tr>
								      	<td colspan="">
											&nbsp;
										</td>
										<td colspan="2" class="reportSubTitle"  style="font-size: 8pt"align="left" >
											&nbsp;
											R&Eacute;GIMEN PATRIMONIAL DEL MATRIMONIO:
											SEPARACION DE BIENES<input type="radio" name="regime" value="radiobutton">
										  	SOCIEDAD CONYUGAL<input type="radio" name="regime" value="radiobutton">
											SOCIEDAD LEGAL<input type="radio" name="regime" value="radiobutton">
										</td>			  
									</tr>
								</table>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td>
					<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="0" cellspacing="0" cellpadding="0" valign="top">
							<tr cellpadding="0" cellspacing="0">
							   	<td cellpadding="0" cellspacing="0" class="reportSubTitle"  style="font-size: 8pt"colspan = "3" >LA VIVIENDA QUE ACTUALMENTE HABITA ES:
							       	PROPIA<input type="radio" name="typehouse" value="radiobutton">
							       	RENTADA<input type="radio" name="typehouse" value="radiobutton">
							       	DE FAMILIARES<input type="radio" name="typehouse" value="radiobutton">
							       	HIPOTECADA<input type="radio" name="typehouse" value="radiobutton">
							   	</td>
							
							 	<td class="reportSubTitle"  style="font-size: 8pt"colspan ="" >
							   		NUMERO DE DEPENDIENTES ECONOMICOS:<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="2" value="">
							   	</td>        	
							</tr>
							<tr>
							   	<td class="reportSubTitle"  style="font-size: 8pt"colspan = "4" >
							      	NIVEL DE ESCOLARIDAD: SIN ESTUDIOS<input type="radio" name="nivstudy" value="radiobutton">	
						    	    PRIMARIA<input type="radio" name="nivstudy" value="radiobutton">
						    	    SECUNDARIA<input type="radio" name="nivstudy" value="radiobutton">
									PREPARATORIA<input type="radio" name="nivstudy" value="radiobutton">
									TECNICO<input type="radio" name="nivstudy" value="radiobutton">
									LICENCIATURA<input type="radio" name="nivstudy" value="radiobutton">
									POSGRADO<input type="radio" name="nivstudy" value="radiobutton">
								</td>
					        </tr>
					 </table>
				</td>
			</tr>
			<tr>
				<td>
					<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="0" cellspacing="0" cellpadding="0" valign="top">
							<tr>
				   	      		<td class="reportSubTitle"  style="font-size: 9pt"colspan = "2" >
		   	      					&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="50" value="<%= bmoEmployer.getLegalname().toString().toUpperCase() %>">
				   	      		</td>    
								<td class="reportSubTitle"colspan = "" >
		                        	&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="50" value="<%//= bmoEmployer.getNrp().toString().toUpperCase() %>">
								</td>    
				        	</tr>
						    <tr>
							   	<td colspan = "2"  class="reportSubTitle"  style="font-size: 8pt">
							   		*NOMBRE DE LA EMPRESA O PATR&Oacute;N
							   	</td>    
								<td colspan = ""  class="reportSubTitle"  style="font-size: 8pt">
									*N&Uacute;MERO DEL REGISTRO PATRONAL
								</td>    
							</tr>
							<tr>
								<td colspan="4">
									<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="0" cellspacing="0" cellpadding="0" valign="top">
										<tr>
										   	<td width="18%" class="reportSubTitle"  style="font-size: 8pt">
										   		*CENTRAL OBRERA: <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="2" value="">
										   	</td>    
										   	<td colspan="3" class="reportSubTitle"  style="font-size: 8pt">
										  		*CLAVES CENTRALES OBRERAS 
										   		01 NO SIND o SIN AFIL; 02 CTM;03 CROC; 04 CROM; 05 COR; 06 CGT; 07 COM; 08 FSR; 09 FENASI; 10 COCEM; 11 CRT; 
										   		12 SINDICATO INDEPENDIENTE; 13 FENASIB; 14 CONASIB; 15 SNTMMS; 
										   		16 STFFRM; 17 STPRM; 18 TELEFONISTAS; 19 SNTSS; 20 CTC; 99 OTROS(ESPECIFICAR)
										   	</td>
										</tr>
								        <tr>
								        	<td colspan="">
								        		&nbsp;
								        	</td>
											<td width="" colspan="2"  class="documentItem" >
												&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="55" value="">
											</td>    
										</tr>
									</table>
								</td>
							<tr>
							<tr>
								<td colspan="4">
									<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="0" cellspacing="0" cellpadding="0" valign="top">
										<tr>
										   	<td class="reportSubTitle"  style="font-size: 8pt"colspan="" width="20%" >
										   		TEL&Eacute;FONO DE LA EMPRESA O PATR&Oacute;N 
										   	</td>	
										   	<td class="reportSubTitle"  style="font-size: 9pt"colspan="" width="10%" align="center" >
										   		(<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="5" value="">) 
										   	</td>	
										   	<td class="reportSubTitle"  style="font-size: 9pt"colspan="" width="7%" align="center">
										   		&nbsp; 
										   		<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="10" value="">
										   	</td>
										   	<td class="reportSubTitle"  style="font-size: 9pt"colspan="">
										   		&nbsp; <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="10" value=""> 
										   	</td>
										   	<td class="reportSubTitle"  style="font-size: 8pt"colspan="" >
										   		Horarios <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="2" value="">:&nbsp; <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="2" value="">
										   		a
										   		<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="2" value="">:&nbsp; <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="2" value=""> 
										   	</td>
										</tr>
										<tr>
											<td	class="reportSubTitle"  style="font-size: 8pt"colspan="" align="" >
												DONDE TRABAJA EL C&Oacute;NYUGE 
											</td>	
										   	<td class="reportSubTitle"  style="font-size: 8pt"colspan="" width="%" align="center" >
												LADA
											</td>	
										   	<td class="reportSubTitle"  style="font-size: 8pt"colspan="" width="%" align="center" >
												
												N&Uacute;MERO
											</td>
											<td class="reportSubTitle"  style="font-size: 8pt"colspan="">
												&nbsp;&nbsp;&nbsp; 
												EXTENSI&Oacute;N
											</td>
										</tr>
									</table>
								</td>
							</tr>
					</table>
				</td>
			</tr>
		</table>
	</td>
</tr>
<tr>
	<td>
		<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="0" align="center" cellpadding="0" cellspacing="0" bordercolor="">  
			<tr>
		    	<td align="center" width="100%">
		    		<b>3. DATOS DE IDENTIFICACI&Oacute;N DEL C&Oacute;NYUGE (SOLO EN CASO DE CR&Eacute;DITO CONYUGAL)</b>
		    	</td>
		  	</tr>
		</table>
	</td>
</tr>
<tr>
	<td>
		<table width="100%" border="1" align="center" cellpadding="0" cellspacing="0"  >
			<tr> 
		    	<td>
				   	<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="0" cellspacing="0" cellpadding="0" valign="top">
    					<tr >       						
    						<td width="23%"class="reportSubTitle"  style="font-size: 9pt">
									<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="20" value="">       								
      						</td>
					        <td class="reportSubTitle"  style="font-size: 9pt">
									<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="35" value="">       								
					        </td>
				        	<td class="reportSubTitle"  style="font-size: 9pt">
									<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="22" value="">      								
				        	</td>
				        </tr>
				        <tr> 
				          	<td width="18%" class="reportSubTitle"  style="font-size: 8pt">
				          		&nbsp;NUMERO DE SEGURIDAD SOCIAL (NSS)
				          	</td>
						  	<td width="" class="reportSubTitle"  style="font-size: 8pt">
						  		&nbsp;CURP
						  	</td>
				          	<td width="" class="reportSubTitle"  style="font-size: 8pt">
				          		&nbsp;R.F.C
				          	</td>
				        </tr>
				        <tr style="font-weight: bold"> 
				          	<td class="reportSubTitle"  style="font-size: 9pt"colspan="2" >
				          		&nbsp;
				          		<% if(conyAp.equals("")){ %>
									<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="50" value="">
								<% }else { %>
										<%= conyAp.toUpperCase()%>
								<% } %>
				          	</td>
				          	<td class="reportSubTitle"  style="font-size: 9pt"colspan="" >
					          	&nbsp;
					      		<% if(conyAm.equals("")){ %>
										<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="50" value="">
								<% }else { %>
										<%= conyAm.toUpperCase() %>
								<% } %>
					      	</td>
				        </tr>  
				        <tr>
				          	<td class="reportSubTitle"  style="font-size: 8pt" colspan="2" >
				          		&nbsp;*APELLIDO PATERNO
				        	</td>
					      	<td class="reportSubTitle"  style="font-size: 8pt" colspan="" >
					      		&nbsp;*APELLIDO MATERNO
					      	</td>
					    </tr>  
					    <tr style="font-weight: bold">
					      	<td class="reportSubTitle"  style="font-size: 9pt"colspan="3" >
					      		&nbsp;
					      		<% if(conyName.equals("")){ %>
									<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="125" value="">
								<% } else { %>
										<%= conyName.toUpperCase() %>
								<% } %>						      		
					      	</td>
					    </tr>  
				        <tr>  
					      	<td class="reportSubTitle"  style="font-size: 8pt" colspan="3" >
					      		&nbsp;*NOMBRE(S)
					     	</td>
				        </tr>
				        <tr>
				        	<td class="reportSubTitle"  style="font-size: 8pt"colspan="2" >
				        		*T&Eacute;LEFONO	<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="5" value=""> 
				        		<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"size="8" value="<%= conyPhone%>">
				        	
				          		&nbsp;CELULAR: &nbsp;&nbsp;&nbsp;<class="reportSubTitle">
				          		044<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="12" value="<%= conyCellphone%>"> 
				          	</td>   
				        </tr>
				        <tr>
				        	<td  colspan="2" class="reportSubTitle"  style="font-size: 8pt">
				        		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				        		&nbsp;&nbsp;&nbsp;
				        		* LADA	
								&nbsp;&nbsp;
								* N&Uacute;MERO
								
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								* N&Uacute;MERO: 
							</td>
				        </tr>
				        <tr>
		        			<td  class="reportSubTitle"  style="font-size: 8pt"colspan="3" width="%" >
		        				*CORREO ELECTRONICO: <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="50" value="<%= conyEmail%>">
		        			</td>
						</tr>
					</table>					 
				</td>
			</tr>
			<tr>
				<td>
					<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="0" cellspacing="0" cellpadding="0" valign="top">
				        <tr valign="MIDDLE">
				        	<td colspan="3" class="reportSubTitle"  style="font-size: 8pt">
				        		NIVEL DE ESCOLARIDAD: SIN ESTUDIOS<input type="radio" name="nivstudycon" value="radiobutton">	
				    	    	PRIMARIA<input type="radio" name="nivstudycon" value="radiobutton">
				    	    	SECUNDARIA<input type="radio" name="nivstudycon" value="radiobutton">
								PREPARATORIA<input type="radio" name="nivstudycon" value="radiobutton">
								TECNICO<input type="radio" name="nivstudycon" value="radiobutton">
								LICENCIATURA<input type="radio" name="nivstudycon" value="radiobutton">
								POSGRADO<input type="radio" name="nivstudycon" value="radiobutton">
							</td>				
				        </tr>
				    </table>
				</td>
			</tr>
			<tr>
				<td>
					<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="0" cellspacing="0" cellpadding="0" valign="top">
				        <tr>
				   	      	<td colspan="2" class="documentItem" >
				   	      		&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="50" value="<%--= bmoConjEmployer.getLegalName().toString().toUpperCase() --%>">
				   	      	</td>    
						  	<td colspan="" class="documentItem" >
						  		&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="50" value="<%--= bmoConjEmployer.getNrp().toString().toUpperCase() --%>">
						  	</td>    
						</tr>  
				        <tr>
				          	<td colspan="2" class="reportSubTitle"  style="font-size: 8pt">
				          		&nbsp;&nbsp;*NOMBRE DE LA EMPRESA O PATR&Oacute;N
				          	</td>    
						  	<td colspan="" class="reportSubTitle"  style="font-size: 8pt">
						  		&nbsp;&nbsp;*N&Uacute;MERO DEL REGISTRO PATRONAL
						  	</td>    
				        </tr>
				        <tr>
				        	<td colspan="4">
					        	<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="0" cellspacing="0" cellpadding="0" valign="top">
					        		<tr>					        		
							          	<td width="18%" class="reportSubTitle"  style="font-size: 8pt">
							          		&nbsp;&nbsp;*CENTRAL OBRERA: &nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="2" value="">
							          	</td>    
							          	<td  colspan="3" class="reportSubTitle" style="font-size: 8pt">
							        		*CLAVES CENTRALES OBRERAS	
							        		01 NO SIND o SIN AFIL; 02 CTM; 03 CROC; 04 CROM; 05 COR; 06 CGT; 07 COM; 08 FSR; 09 FENASI; 10 COCEM; 11 CRT; 12 SINDICATO INDEPENDIENTE; 13 FENASIB; 14 CONASIB; 15 SNTMMS; 
							        		16 STFFRM; 17 STPRM; 18 TELEFONISTAS; 19 SNTSS; 20 CTC; 99 OTROS(ESPECIFICAR)
								      	</td>
								   </tr>
								</table>
							<td>
				       </tr>
				       <tr>
					        <td>
					        	&nbsp;
					        </td>	
							<td colspan="3"  class="documentItem">
								&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="90" value="">
							</td>    
					   </tr>	
					   <tr>
						   	<td class="reportSubTitle"  style="font-size: 8pt"colspan="" >
						   		&nbsp;TEL&Eacute;FONO DE LA EMPRESA O PATR&Oacute;N EN
						   	</td>
						   	<td class="reportSubTitle"  style="font-size: 8pt"colspan="">
						   		&nbsp;(<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="5" value="">) 
						   		&nbsp; <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="10" value="">	
						   		&nbsp; <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="10" value=""> 
						   	</td>
						   	<td class="reportSubTitle"  style="font-size: 8pt"colspan="">
						   		Horarios <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="2" value="">:&nbsp; <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="2" value="">
						   		a
						   		<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="2" value="">:&nbsp; <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="2" value=""> 
						   	</td>
						</tr>
						<tr>
							<td	class="reportSubTitle"  style="font-size: 8pt"colspan="" >
								DONDE TRABAJA EL C&Oacute;NYUGE 
							</td>
							<td class="reportSubTitle"  style="font-size: 8pt"colspan="" >
								&nbsp;&nbsp;&nbsp;&nbsp;
								LADA &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								N&Uacute;MERO
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								EXTENSI&Oacute;N
							</td>
						</tr>
  					</table>
  				</td>
			</tr> 
		</table>
	</td>
</tr>
	<tr>
	<td>
		<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="0" align="center" cellpadding="0" cellspacing="0" bordercolor="">  
			<tr>
		    	<td align="center" width="100%">
		    		<b>4. REFERENCIAS FAMILIARES DEL DERECHOHABIENTE</b>
	    		</td>
		  	</tr>
		</table>
	</td>
</tr>
<tr>
	<td>
		<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="1" align="center" cellpadding="0" cellspacing="0"  >
			<tr>
				<td>
							<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="0" cellspacing="0" cellpadding="0" valign="top">
								<tr>
						       		<td class="reportSubTitle"  style="font-size: 9pt" style="font-weight: bold">
						       			&nbsp;
							       		<% if(familiar1Ap.equals("")){ %>
				                    			<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="50" value="">
				                        <% } else { %>
				                            	<%= familiar1Ap.toUpperCase()%>
				                        <% } %>
						       		</td>			   
							   	</tr>
							   	<tr>
							       	<td class="reportSubTitle" style="font-size: 8pt">
							       		&nbsp;*APELLIDO PATERNO
							       	</td>
							   	</tr>	
							   	<tr>
							       	<td class="reportSubTitle"  style="font-size: 9pt"style="font-weight: bold">
							       		&nbsp;
								       	<% if(familiar1Am.equals("")){ %>
				                        		<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="50" value="">
				                        <% } else { %>
				                                <%= familiar1Am%>
				                        <% } %>
							       	</td>			   
							   	</tr>
					   			<tr>
							       	<td class="reportSubTitle"  style="font-size: 8pt">
							       		&nbsp;*APELLIDO MATERNO
							       	</td>
							   	</tr>   
							   	<tr>
							       	<td class="reportSubTitle"  style="font-size: 9pt" style="font-weight: bold">
							       		&nbsp;
								       	<% if(familiar1Name.equals("")){ %>
				                                <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="50" value="">
				                        <% } else { %>
				                                <%= familiar1Name.toUpperCase()%>
				                        <% } %> 
							       	</td>			   
							   	</tr>
							   	<tr>
							       <td colspan="" class="reportSubTitle"  style="font-size: 8pt">
							       		&nbsp;*NOMBRE (S)
							       	</td>
							   	</tr>
							   	<tr>
							      	<td colspan=""  class="reportSubTitle"  style="font-size: 8pt">
							      		&nbsp;*TEL&Eacute;FONO: 
										&nbsp;(<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="5" value="">)
										&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="20" value="<%= familiar1Phone%>">
									</td>			   			     
								</tr>	
								<tr> 
									<td colspan=""  class="reportSubTitle"  style="font-size: 8pt">
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										*LADA
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										*N&Uacute;MERO
									</td> 
								</tr>	
								<tr>
							      	<td colspan="" class="reportSubTitle"  style="font-size: 8pt">
							      		&nbsp;*CELULAR: 
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								      	044 <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="20" value="<%= familiar1Cellphone%>">
								      </td>				
								</tr>	
								<tr> 
									<td colspan="" class="reportSubTitle"  style="font-size: 8pt">
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										&nbsp;&nbsp;&nbsp;
										*N&Uacute;MERO
									</td> 
								</tr>	
							</table>
						</td>
						<td>
							<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="0" cellspacing="0" cellpadding="0" valign="top">
								<tr>
							       <td class="reportSubTitle"  style="font-size: 9pt" style="font-weight: bold">
								       	&nbsp;
								       	<% if(familiar2Ap.equals("")){ %>
							        			<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="50" value="">
							           <% } else { %>
							                	<%= familiar2Ap.toUpperCase()%>
							           <% } %>
							       </td>			   
							   </tr>
							   <tr>
							       <td class="reportSubTitle"  style="font-size: 8pt">&nbsp;*APELLIDO PATERNO</td>
							   </tr>	
							   <tr>
							       <td class="reportSubTitle"  style="font-size: 9pt" style="font-weight: bold">
								       &nbsp
								    	<% if(familiar2Am.equals("")){ %>
							        			<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="50" value="">
							            <% } else { %>
							                	<%= familiar2Am.toUpperCase()%>
							            <% } %>
							       	</td>			   
							   </tr>
							   <tr>
							       <td class="reportSubTitle"  style="font-size: 8pt">&nbsp;*APELLIDO MATERNO</td>
							   </tr>   
							   <tr>
							   		<td class="reportSubTitle"  style="font-size: 9pt" style="font-weight: bold">
								   		&nbsp;
									    <% if(familiar2Name.equals("")){ %>
							        			<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="50" value="">
							            <% } else { %>
							                	<%= familiar2Name.toUpperCase()%>
							            <% } %>
							       </td>			   
							   </tr>
							   <tr>
							       <td colspan="" class="reportSubTitle"  style="font-size: 8pt">&nbsp;*NOMBRE (S)</td>
							   </tr>
							   <tr>
							       <td colspan="" class="reportSubTitle"  style="font-size: 8pt">
							       		&nbsp;*TEL&Eacute;FONO: &nbsp;(<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="5" value="">)
								   		&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="20" value="<%= familiar2Phone%>">
								   	</td>			   			     
							   </tr>	
							   <tr> 
									<td colspan="" class="reportSubTitle"  style="font-size: 8pt">
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										&nbsp;*LADA
										&nbsp;&nbsp;&nbsp;&nbsp;
										&nbsp;*N&Uacute;MERO
									</td> 
							   </tr>	
							   <tr>
									<td colspan="" class="reportSubTitle"  style="font-size: 8pt">
										&nbsp;*CELULAR: 
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										044
										<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="20" value="<%= familiar2Cellphone%>">
									</td>				
					  		  </tr>	
					  		  <tr> 
									<td colspan="" class="reportSubTitle"  style="font-size: 8pt">
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										&nbsp;&nbsp;&nbsp;
										*N&Uacute;MERO
									</td> 
							   </tr>		   
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	<td>	
</tr>
<tr>
	<td>
		<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="0" align="center" cellpadding="0" cellspacing="0" bordercolor="">  
			<tr>
		    	<td align="center" width="100%">
		    		<b>5. DATOS PARA DETERMINAR EL CR&Eacute;DITO</b>
		    	</td>
		  	</tr>
		</table>
	</td>
</tr>
<TR>
	<TD>
		<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="1" align="center" cellpadding="0" cellspacing="0"  >
			<tr>
				<td>
		      		<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="0" cellspacing="0" cellpadding="0">
		        		<tr>
		          			<td colspan="5" class="reportSubTitle"  style="font-size: 8pt">
		          				A. EN CASO DE TENER DESCUENTOS FAVOR DE LLENAR LA SIGUIENTE INFORMACI&Oacute;N:
		          			</td>	
		          		</tr>	  
		          		<tr>
		          			<td colspan="2" class="reportSubTitle"  style="font-size: 8pt">
		          				&nbsp;
		          			</td>
		      	  			<td width="" class="reportSubTitle"  style="font-size: 8pt" align="center" >
		      	  				DERECHOHABIENTE
		      	  			</td>
		      	  			<td width="" class="reportSubTitle"  style="font-size: 8pt"align="left" >
		      	  				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		      	  				CONYUGE
		      	  			</td>
		        		</tr>
		        		<tr> 
		      	  			<td colspan="2" class="reportSubTitle"  style="font-size: 8pt">
		      	  				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		      	  				DESCUENTO MENSUAL POR PENSI&Oacute;N ALIMENTICIA(En su caso):
		      	  			</td>
		         			<td width="" class="reportSubTitle"  style="font-size: 8pt"align="center" >
		         				$&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="10" value="">
		         			</td>		  
		          			<td width="" class="reportSubTitle"  style="font-size: 8pt"align="left" >
		          				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		          				$&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="10" value="">
		          			</td>		  
		        		</tr>  
		        		<tr>
		        			<td colspan="2" class="reportSubTitle"  style="font-size: 9pt">
		        				&nbsp;
		        			</td>		  
		        			<td align="center" class="abc" style="font-size: 8pt">
		        				&nbsp;(sin centavos)
		        			</td>		  
		          			<td class="abc" style="font-size: 8pt">
		          				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		          				(sin centavos)
		          			</td>		  
		        		</tr>
		    	        <tr>
		          			<td colspan="5" class="reportSubTitle"  style="font-size: 8pt" >
		          			B.- EN CASO  DE SOLICITAR UN MONTO DE CR&Eacute;DITO MENOR AL PROPUESTO EN LA PRECALIFICACI&Oacute;N FAVOR DE LLENAR LA SIGUIENTE INFORMACI&Oacute;N:</td>
		          		</tr>
		          		<tr>		  
		          			<td colspan="2" class="reportSubTitle"  style="font-size: 8pt">
		          				&nbsp;
		          			</td>
		        			<td colspan="" class="reportSubTitle"  style="font-size: 8pt"align="center" >
		        				DERECHOHABIENTE
		        			</td>
		      	  			<td width="" class="reportSubTitle"  style="font-size: 8pt" align="left" >
		      	  				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		      	  				CONYUGE:
	      	  				</td>
		        		</tr>
		        		<tr> 
		      	  			<td colspan="2" class="reportSubTitle"  style="font-size: 8pt">
		      	  				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		      	  				MONTO DE CR&Eacute;DITO SOLICITADO:
		      	  			</td>
		          			<td colspan="" class="reportSubTitle"  style="font-size: 8pt" align="center" >
			          			$&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="10" value="">
		          			</td>		  
		          			<td width="" class="reportSubTitle"  style="font-size: 8pt" align="left" >
		          				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		          				$&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="10" value="">
		          			</td>		  
		        		</tr>  
		        		<tr>
		        			<td colspan="2" class="reportSubTitle">
		        				&nbsp;
		        			</td>		  
		        			<td align="center"class="abc" style="font-size: 8pt">
		        				(sin centavos)
		        			</td>		  
		          			<td class="abc" style="font-size: 8pt">
			          			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		          				(sin centavos)
		          			</td>		  
		        		</tr>
		             	<tr>
		          			<td width="" class="reportSubTitle"  style="font-size: 8pt"colspan="6" >
		          				C.- EN CASO DE NO OBTENER LA PUNTUACI&Oacute;N MINIMA REQUERIDA, FAVOR DE LLENAR LA SIGUIENTE INFORMACI&Oacute;N:
		          			</td>		  
		        		</tr>
		        		<tr> 
		         		  	<td colspan="2" class="reportSubTitle"  style="font-size: 8pt">
		         		  		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;MONTO DE AHORRO VOLUNTARIO:
		         		  	</td>
		      	  			<td colspan="" class="reportSubTitle"  style="font-size: 8pt"align="center" >
		      	  				$&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="10" value="">
		      	  			</td>
		      	  			<td colspan="" class="reportSubTitle"  style="font-size: 8pt">
		      	  				INCREMENTAR LA CAPACIDAD DE COMPRA<input type="radio" name="tipocredito" checked value="radiobutton">
			      	  			REDUCIR EL MONTO M&Aacute;XIMO DE CR&Eacute;DITO<input type="radio" name="tipocredito" checked value="radiobutton">
			      	  		</td>
		        		</tr>  
		        		<tr>
		        			<td colspan="2" class="reportSubTitle"  style="font-size: 8pt">
		        				&nbsp;
		        			</td>
		        			<td colspan="" align="center" class="abc" style="font-size: 8pt">
		        				(sin centavos)
		        			</td>
		        		</tr>
		        		<tr>
		          			<td width="" class="reportSubTitle"  style="font-size: 8pt" colspan="6" >
		          				D.- EN CASO DE EXISTIR DIFERENCIA ENTRE EL VALOR DE PRESUPUESTO DE LA CONSTRUCCI&Oacute;N Y CON LO QUE CUENTA EL TRABAJADOR:
		          				</td>		  
		        		</tr>
		        		<tr> 
		         			<td colspan="2" class="reportSubTitle"  style="font-size: 8pt">
		         				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;CANTIDAD A CUBRIR CON DEP&Oacute;SITO:
		         			</td>
		      	  			<td width="" class="reportSubTitle"  style="font-size: 8pt"align="center" >
		      	  				$&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="10" value="">
		      	  			</td>		  
		
		        		</tr>  
		        		<tr>
		        			<td colspan="2" class="reportSubTitle"  style="font-size: 9pt">
		        				&nbsp;
		        			</td>
		        			<td colspan="" align="center" class="abc" align="left" style="font-size: 9pt">
		        				&nbsp;&nbsp;
		        				(sin centavos)
		        			</td>
		        		</tr>
		      		</table>
		    	</td>
		    </tr>
		</table>
	</TD>
</TR>
<tr>
	<td>
		<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="0" align="center" cellpadding="0" cellspacing="0" bordercolor="" >
		   	<tr style="font-size: 8pt">	  
				<td width="33%" align="center" >
					*DATOS OBLIGATORIOS
				</td>
				<td width="33%" align="center" >
					HOJA 1 DE 2
				<td width="33%"  align="center" >
					CRED.1000.07
				</td>
			</tr>	
		</table>				
	</td>
</tr>	
</table>
<!-- -----------------------------------------------------------------------------PAGE-BREAK----------------------------------------------------------------------------------- -->
<p style="page-break-after: always"></p>
<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="0" align="center" cellpadding="0" cellspacing="0">

<tr>
	<td>
		<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="0" align="center" cellpadding="0" cellspacing="0" bordercolor="">
			<tr align="rigth">
		  		<td align="center" colspan="2" >
					<img src="<%= GwtUtil.getProperUrl(sFParams, "/img/solicitud_infonavit.png")%>" width="35%">
				</td>
		  	</tr>
		</table>
	</td>
</tr>
<tr>
	<td>
		<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="0" align="center" cellpadding="0" cellspacing="0" bordercolor="">  
			<tr>
		    	<td align="center" width="100%">
		    		<b>6. DATOS DE LA VIVIENDA DESTINO DEL CREDITO</b>
		    	</td>
		  	</tr>
		</table>
	</td>
</tr>
<tr>
	<td>
		<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="1" cellspacing="0" cellpadding="0" align="center" >	
			<tr>
				<td colspan="4">
					<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="0" cellpadding="0" cellspacing="0" bordercolor="">
						<tr> 
							<td class="reportSubTitle"  style="font-size: 9pt"align="left" colspan="3" >
								&nbsp;
									<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="80" value="<%= bmoProperty.getStreet().toString().toUpperCase()%>">     								
							</td>
						</tr>	
						<tr>                    
							<td  class="reportSubTitle"  style="font-size: 8pt"align="left" colspan="2" >
								&nbsp;&nbsp;&nbsp;*CALLE
							</td>
						</tr>
						<tr>
							<td  class="reportSubTitle"  style="font-size: 9pt"align="left" >
								&nbsp;
								&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="6" value="<%= bmoProperty.getNumber().toString().toUpperCase()%>">
								&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="6" value="">
								&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="6" value="<%= bmoProperty.getLot().toString().toUpperCase()%>">
								&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="6" value="<%= bmoProperty.getBmoDevelopmentBlock().getCode().toString().toUpperCase()%>">
								
							</td>
							<td  class="reportSubTitle"  style="font-size: 9pt">
								&nbsp;
									<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="60" value="<%= bmoDevelopment.getName().toString().toUpperCase()%>">     								
							</td>
							<td>
								&nbsp;
							</td>
						</tr>
						<tr>
							<td width="" class="reportSubTitle"  style="font-size: 8pt"align="left" >
								&nbsp;&nbsp;&nbsp;&nbsp;
								*No. EXT.
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp&nbsp;&nbsp;
								*No. INT. 
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								&nbsp;&nbsp&nbsp;&nbsp;&nbsp;&nbsp;
								&nbsp;&nbsp&nbsp;&nbsp;
								*LOTE 
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								&nbsp;&nbsp&nbsp;&nbsp;&nbsp;&nbsp;
								&nbsp;&nbsp&nbsp;
								*MZA.
							</TD>	
							<td class="reportSubTitle"  style="font-size: 8pt">
								&nbsp;&nbsp;&nbsp;*COLONIA O FRACCIONAMIENTO
							</td>
							<td>
								&nbsp;
							</td>
						</tr>
						<tr>
							<td width="" height="10" class="documentItem" align="left" >
								&nbsp;
									<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="50" value="<%= bmoCityDevelopment.getBmoState().getName().toString().toUpperCase()%>"></td>     								
							</td>
							<td width="" height="10" class="documentItem" align="left" >
								&nbsp;
									<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="50" value="<%= bmoCityDevelopment.getName().toString().toUpperCase()%>">
							</td>    
								
							<td class="reportSubTitle"  style="font-size: 9pt"align="left" >
								&nbsp;
									<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="10" value="<%= bmoDevelopment.getZip().toString().toUpperCase()%>">   
							</td>
						</tr>
						<tr>
							<td width="" class="reportSubTitle"  style="font-size: 8pt"align="left" ">
								&nbsp;&nbsp;&nbsp;*ENTIDAD
							</td>
							<td width="" class="reportSubTitle"  style="font-size: 8pt"align="left" >
								&nbsp;&nbsp;&nbsp;*MUNICIPIO O DELEGACI&Oacute;N
							</td>
							<td width="" class="reportSubTitle"  style="font-size: 8pt"align="left" >
								&nbsp;&nbsp;&nbsp;*CODIGO POSTAL
							</td>
						</tr>
						
						<tr>
							<td class="reportSubTitle"  style="font-size: 8pt"aling="center" colspan="15" >
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								Nota: En caso de que desee hacer un cambio de vivienda, debe presentar una nueva Solicitud de Inscripci&oacute;n de Cr&eacute;dito
							</td>
						</tr>
						<tr>
							<td class="reportSubTitle"  style="font-size: 8pt">
								<b>*&iquest;La vivienda elegida es para una persona con discapacidad?</b>
							</td>
							<td align="left" class="reportSubTitle"  style="font-size: 8pt">
								SI<input type="radio" name="tipocredito" checked value="radiobutton">
								&nbsp;NO<input type="radio" name="tipocredito" checked value="radiobutton">
							</td>
						</tr>
						
					</table> 
					
				</td>	
			</tr>	
			<tr>
				<td colspan="4"class="reportSubTitle"  style="font-size: 8pt">
					Anotar la cantidad que corresponda seg&uacute;n el destino del cr&eacute;dito solicitado: 
				</td>
			</tr>
			<tr >
				<td  rowspan="" align="center" class="reportSubTitle"  style="font-size: 8pt">&nbsp;Para Comprar una Vivienda</td>
				<td  rowspan="" align="center" class="reportSubTitle"  style="font-size: 8pt">&nbsp;Para Construir tu Vivienda</td>
				<td  rowspan="" align="center" class="reportSubTitle"  style="font-size: 8pt">&nbsp;Para Reparar, Ampliar o Mejorar tu Vivienda</td>
				<td  rowspan="" align="center" class="reportSubTitle"  style="font-size: 8pt">&nbsp;Para Pagar el Pasivo la Hipoteca de tu Vivienda</td>
		  	</tr>
			<tr>
				<td width="25%" align="center" valign="">
					<table class="reportSubTitle"  style="font-size: 9pt"border="0" cellpadding="0" cellspacing="0" bordercolor="">
						<tr>
							<td  align="center" class="reportSubTitle"  style="font-size: 8pt">&nbsp;
								<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="9" value="<%= SFServerUtil.formatCurrency(bmoOrder.getTotal().toDouble())%>">
							</td>
						</tr>
						<tr>
							<td  align="center" class="reportSubTitle"  style="font-size: 8pt">&nbsp;
								(Sin Centavos)
							</td>
						</tr>
						<tr>
							<td  align="center" class="reportSubTitle"  style="font-size: 8pt">&nbsp;
								&nbsp;
							</td>
						</tr>		
						<tr>	
							<td  align="center" class="reportSubTitle"  style="font-size: 8pt">&nbsp;
								* PRECIO DE COMPRA-VENTA
							</td>
						</tr>
						<tr>
							<td  align="center" class="reportSubTitle"  style="font-size: 8pt">&nbsp;
								(Precio total pactado libremente entre las partes)
							</td>
						</tr>
					</table>
				</td>	
				<td width="25%" valign="top" align="center">
					<table class="reportSubTitle"  style="font-size: 9pt"border="0" cellpadding="0" cellspacing="0" bordercolor="">
						<tr>
							<td  align="center" class="reportSubTitle"  style="font-size: 8pt">&nbsp;
								$<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="9" value="">
							</td>
						</tr>
						<tr>
							<td  align="center" class="reportSubTitle"  style="font-size: 8pt">&nbsp;
								(Sin Centavos)
							</td>
						</tr>
						<tr>
							<td  align="center" class="reportSubTitle"  style="font-size: 8pt">&nbsp;
								&nbsp;
							</td>
						</tr>		
						<tr>	
							<td  align="center" class="reportSubTitle"  style="font-size: 8pt">&nbsp;
								* MONTO DEL PRESUPUESTO PARA LA CONSTRUCCI&Oacute;N
							</td>
						</tr>
						<tr>
							<td  align="center" class="reportSubTitle"  style="font-size: 8pt">&nbsp;
								(Valor estimado del proyecto a su terminaci&oacute;n)
							</td>
						</tr>
					</table>
				</td>	
				<td width="25%" align="center" valign="top">
					<table class="reportSubTitle"  style="font-size: 9pt"border="0" cellpadding="0" cellspacing="0" bordercolor="">
						<tr>
							<td  align="center" class="reportSubTitle"  style="font-size: 8pt">&nbsp;
								$<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" readonly size="9" value="">
							</td>
						</tr>
						<tr>
							<td  align="center" class="reportSubTitle"  style="font-size: 8pt">&nbsp;
								(Sin Centavos)
							</td>
						</tr>
						<tr>
							<td  align="center" class="reportSubTitle"  style="font-size: 8pt">&nbsp;
								&nbsp;
							</td>
						</tr>		
						<tr>	
							<td  align="center" class="reportSubTitle"  style="font-size: 8pt">&nbsp;
								* MONTO DEL PRESUPUESTO PARA LA, AMPLIACI&Oacute;N, REMODELACI&Oacute;N O MEJORAS 
							</td>
						</tr>
						<tr>
							<td  align="center" class="reportSubTitle"  style="font-size: 9pt">&nbsp;
								(Valor estimado del proyecto a su terminaci&oacute;n)
							</td>
						</tr>
						<tr>
							<td  align="center" class="reportSubTitle"  style="font-size: 8pt">&nbsp;
								Afectaci&oacute;n Estructural &nbsp; SI <input name="structural" type="radio"> &nbsp; NO <input name="structural" checked type="radio">
							</td>
						</tr>
					</table>
				</td>	
				<td width="25%" align="center" valign="top">
					<table  class="reportSubTitle"  style="font-size: 9pt"border="0" cellpadding="0" cellspacing="0" bordercolor="">
						<tr>
							<td  align="center" class="reportSubTitle"  style="font-size: 8pt">&nbsp;<b>
								$<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" readonly size="9" value="">
								</b>
							</td>
						</tr>
						<tr>
							<td  align="center" class="reportSubTitle"  style="font-size: 8pt">&nbsp;
								(Sin Centavos)
							</td>
						</tr>
						<tr>
							<td  align="center" class="reportSubTitle"  style="font-size: 8pt">&nbsp;
								&nbsp;
							</td>
						</tr>		
						<tr>	
							<td  align="center" class="reportSubTitle"  style="font-size: 8pt">&nbsp;
								* MONTO DE LA DEUDA
							</td>
						</tr>
						<tr>
							<td  align="center" class="reportSubTitle"  style="font-size: 8pt">&nbsp;</td>
						</tr>
					</table>
				</td>		
			</tr>	
		</table>			
	</td>
</tr>
<tr>
	<td>
		<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="0" align="center" cellpadding="0" cellspacing="0" bordercolor="">  
			<tr>
		    	<td align="center" width="80%">
		    			<b>7. DATOS PARA ABONO EN CUENTA DEL CR&Eacute;DITO</b>
		    	</td>
		  	</tr>
		</table>
	</td>
</tr>
<tr>
	<td>
		<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="1" cellspacing="0" cellpadding="0" align="center" >	
			<tr>		
				<td width="50%">
					<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="0" cellspacing="0" cellpadding="0" valign="top">
		        		<tr >
							<td width="" class="reportSubTitle"  style="font-size: 8pt"align="left" >
								&nbsp;
								*DATOS DEL VENDEDOR Y/O APODERADO DEL VENDEDOR <input name="salesman" type="radio"> &nbsp;
								&nbsp; DATOS DEL AGENTE INMOBILIARIO <input name="salesman" type="radio"> <br>
								&nbsp; DATOS DE LA ADMINISTRADORA DESIGNADA PARA CONSTRUCCI&Oacute;N <input name="salesman" type="radio"> <br> 
								&nbsp; DATOS DEL DERECHOHABIENTE <input name="salesman" type="radio">
							</td>
						</tr>
						<tr>
							<td width="" class="reportSubTitle"  style="font-size: 9pt" style="font-weight: bold">
								&nbsp;
								<% if (bmoCompany.getLegalName().toString().equals("")){ %>
									<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="60" value="">     								
		   						<% } else { %> 
									<b><%= bmoCompany.getLegalName().toString().toUpperCase() %></b>
								<%} %>
							</td>
						</tr>
						<tr>
							<td width="" class="reportSubTitle"  style="font-size: 9pt">
								&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="70" value="">
							</td>
						</tr>
						<tr>
							<td width="" class="reportSubTitle"  style="font-size: 9pt">
								&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="70" value="">
							</td>
						</tr>
						<tr>
							<td width="" class="reportSubTitle"  style="font-size: 8pt">
								&nbsp;*R.F.C.&nbsp;&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="65" value="<%= bmoCompany.getRfc().toString().toUpperCase() %>">					
							</td>
						</tr>
						<tr>
							<td width="" class="reportSubTitle"  style="font-size: 9pt" style="font-weight: bold">
								&nbsp;
								<% if (bmoCompany.getLegalName().toString().equals("")){ %>
									<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="60" value="">     								
		   						<% } else { %> 
		   							<b><%= bmoCompany.getLegalName().toString().toUpperCase() %></b>
								<%} %>
							</td>
						</tr>
						<tr>
							<td width="" class="reportSubTitle"  style="font-size: 8pt">
								&nbsp;*NOMBRE O DENOMINACION O RAZON SOCIAL COMO APARECE EN EL ESTADO DE CUENTA
							</td>
						</tr>
						<tr style="font-weight: bold">
							<td width="" class="reportSubTitle"  style="font-size: 9pt">
								&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="70" value="">
							</td>
						</tr>
						<tr>
							<td width="" class="reportSubTitle"  style="font-size: 8pt">
								&nbsp;*CLAVE BANCARIA ESTANDARIZADA (CLABE)
							</td>
						</tr>
					</table> 
				</td>
				<td width="50%" valign="top">
					<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="0" cellspacing="0" cellpadding="0" valign="top">
						<tr>
							<td width="" class="reportSubTitle"  style="font-size: 8pt"align="center" >
								<br>
								&nbsp;*DATOS DEL ACREEDOR HIPOTECARIO<br>
							</td>
						</tr>
						<tr height="">
							<td>
								&nbsp;
							</td>
						</tr>
						<tr>
							<td class="reportSubTitle"  style="font-size: 8pt">                                                                            
                                 &nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="60" value="<%= bmoSupplierDvrg.getLegalName().toString().toUpperCase()%>">
							</td>
						</tr>
						<tr>
							<td width="" class="reportSubTitle"  style="font-size: 8pt">
								&nbsp;NOMBRE O DENOMINACI&Oacute;N O RAZ&Oacute;N SOCIAL
							</td>
						</tr>
						<tr>
							<td width="" class="reportSubTitle"  style="font-size: 8pt">
								&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="60" value="">
							</td>
						</tr>
						<tr>
							<td width="" class="reportSubTitle"  style="font-size: 8pt"">
								&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="60" value="">
							</td>
						</tr>
						<tr>
							<td width="" class="reportSubTitle"  style="font-size: 8pt">
								&nbsp;*R.F.C.<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="56" value="<%= bmoSupplierDvrg.getRfc().toString().toUpperCase()%>">
							</td>
						</tr>
						<tr>
							<td width="" class="reportSubTitle"  style="font-size: 8pt" style="font-weight: bold">
								<% if (bmoSupplierDvrg.getName().toString().equals("")){ %>
									&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="60" value="<%= bmoSupplierDvrg.getName().toString().toUpperCase()%>">     								
		   						<% } else { %> 
		   							&nbsp;<b><%= bmoSupplierDvrg.getLegalName().toString().toUpperCase()%></b>
								<% } %>						
							</td>
						</tr>
						<tr>
							<td width="" class="reportSubTitle"  style="font-size: 8pt">
								&nbsp;*NOMBRE O DENOMINACION O RAZ&Oacute;N SOCIAL COMO APARECE EN EL ESTADO DE CUENTA
							</td>
						</tr>
						<tr>
							<td class="reportSubTitle"  style="font-size: 8pt">
								&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="60" value="">     								
							</td>
						</tr>
						<tr>
							<td class="reportSubTitle"  style="font-size: 8pt">
								&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="60" value="">     								
							</td>
						</tr>							
						<tr>
							<td class="reportSubTitle"  style="font-size: 8pt">
									<!--&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="60" value="<%//= bmoSupplierDvrg.getClabe().toString().toUpperCase() %>">-->
									&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="60" value="<%= bmoDevelopmentRegistry.getAccount().toString().toUpperCase() %>"> 
							</td>
						</tr>
						<tr>
							<td width="" class="reportSubTitle"  style="font-size: 8pt">
								&nbsp;*CLAVE BANCARIA ESTANDARIZADA (CLABE)
							</td>
						</tr>
					</table> 
				</td>	
			</tr>
			<TR>
				<TD colspan = "2"class="reportSubTitle"  style="font-size: 8pt" align="center" >
								En caso de existir dos o mas vendedores y/o acreedores hipotecarios se deber&aacute; de reimprimir la segunda hoja de esta solicitud
								para su llenado.
								Asimismo, es importante considerar que se deber&aacute; de indicar al notario las cantidades a pagar a cada parte.															
				</TD>
			</TR>
			<TR>
				<TD  colspan="2">
					<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="0" cellspacing="0" cellpadding="0">
						<TR>
							<TD colspan ="" class="reportSubTitle"  style="font-size: 8pt"valign="center" >
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								DERECHOHABIENTE
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								CONYUGE
							</TD>
						</TR>
						<TR>
							<TD class="reportSubTitle"  style="font-size: 8pt">
								N&Uacute;MERO DE CR&Eacute;DITO OTORGADO POR INFONAVIT
								&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="30" value="">
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="30" value="">
							</TD>
						</TR>
						<TR>
							<TD class="reportSubTitle"  style="font-size: 8pt"colspan="2" align="center" ">
								Solo para ser llenado en caso de que la vivienda tenga un cr&eacute;dito vigente 
							</TD>
						</TR>
						<TR>
							<TD class="reportSubTitle" style="font-size: 8pt;>
								N&Uacute;MERO DE INVENTARIO VIVIENDA RECUPERADA
								&nbsp;&nbsp;&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="30" value="">
							</TD>
						</TR>
						<TR>
							<TD class="reportSubTitle"  style="font-size: 8pt">
								N&Uacute;MERO DE CR&Eacute;DITO DE LA ENTIDAD FINANCIERA
								&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="60" value="">
							</TD>
						</TR>
						<TR>
							<TD class="reportSubTitle"  style="font-size: 8pt" colspan="2" align="center" >
								Solo aplica cuando el destino del cr&eacute;dito es para pagar el Pasivo o la Hipoteca de tu Vivienda
							</TD>
						</TR>
					</TABLE>	
				</TD>
			</TR>
			
		</TABLE>				
	</td>
</tr>
<tr>
	<td>
		<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="0" align="center" cellpadding="0" cellspacing="0" bordercolor="">  
			<tr>
		    	<td align="center" width="80%">
		    		<b>8. DESIGNACI&Oacute;N DEL REPRESENTANTE (EN SU CASO)</b>
		    	</td>
		  	</tr>
		</table>
	</td>
</tr>
<tr>
	<td>
		<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="1" cellspacing="0" cellpadding="0" align="center" >	
			<tr>
				<td >
					<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="0" cellspacing="0" cellpadding="0" align="center" bordercolor="">	
						<tr>
							<td width="" colspan="3" class="reportSubTitle"  style="font-size: 8pt"">
								&nbsp;DESIGNO COMO REPRESENTANTE PARA QUE EN MI (O NUESTRO) NOMBRE Y RESPRESENTACI&Oacute;N SOLICITE Y TRAMITE LA INSCRIPCI&Oacute;N DE CR&Eacute;DITO EN LOS T&Eacute;RMINOS DE LAS REGLAS APLICABLES: 
							</td>				
						</tr>
						<tr>
							<td width="50%" class="reportSubTitle"  style="font-size: 9pt"colspan=""  style="font-weight: bold">
								&nbsp;<% if(bmoProperty.getDevelopmentRegistryId().toInteger() > 0){ %>
									<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="60" value="<%= bmoSupplierDvrg.getLegalRep().toString().toUpperCase()%>">     								
		   						<% } else { %> 
									<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="60" value="">     								
								<% } %>
							</td>
							<td width="50%" class="reportSubTitle"  style="font-size: 9pt"colspan=""  style="font-weight: bold">
								&nbsp;<% if(bmoProperty.getDevelopmentRegistryId().toInteger() > 0){ %>
									<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="60" value="<%= bmoSupplierDvrg.getLegalRep().toString().toUpperCase()%>">     								
		   						<% } else { %> 
									<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="60" value="">     								
								<% } %>									
							</td>
						</tr>
						<tr>
							<td width="50%" class="reportSubTitle"  style="font-size: 8pt"colspan="" >
								&nbsp;&nbsp;&nbsp;APELLIDO PATERNO
							</td>
							<td width="50%" colspan="" class="reportSubTitle"  style="font-size: 8pt">
								&nbsp;&nbsp;&nbsp;APELLIDO MATERNO
							</td>
						</tr>
						<tr>
							<td class="reportSubTitle"  style="font-size: 9pt"colspan="3"  style="font-weight: bold">
								&nbsp;
								<% if (bmoProperty.getDevelopmentRegistryId().toInteger() > 0){ %>
									<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="130" value="<%= bmoSupplierDvrg.getLegalRep().toString().toUpperCase()%>">     								
		   						<% } else { %> 
									<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="130" value="">     								
								<% } %>
							</td>
						</tr>
						<tr>
							<td width=""  colspan="3" class="reportSubTitle"  style="font-size: 8pt">
								&nbsp;&nbsp;NOMBRE(S)
							</td>
						</tr> 
						<tr>
							<td colspan="2" class="reportSubTitle" style="font-size: 8pt">
								&nbsp;&nbsp;TEL&Eacute;FONO: &nbsp;
								<% if (bmoProperty.getDevelopmentRegistryId().toInteger() > 0){ %>
									<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="5" value="">
								<%}else{%>
									<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="5" value="">
								<%}%>
								<% if (bmoProperty.getDevelopmentRegistryId().toInteger() > 0){ %>
									&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="10" value="<%= telDvrg%>">
								<%}else{%>
									&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="10" value="">
								<%}%>
						
								CELULAR: &nbsp;044 
								<% if (bmoProperty.getDevelopmentRegistryId().toInteger()> 0){ %>
									<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="10" value="<%= movilDvrg%>">
								<%}else{%>
									<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="5" value="">
								<%}%>
							</td>	
						</tr>
						<tr>
							<td colspan="2" class="reportSubTitle" style="font-size: 8pt">&nbsp;
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								&nbsp;&nbsp;&nbsp;
								LADA
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								N&Uacute;MERO:
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								N&Uacute;MERO.
							</td>
						</tr>
						<tr>
							<td height="25">&nbsp;</td>
						</tr>
						<tr>
							<td colspan="2">
								<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="0" cellspacing="0" cellpadding="0" align="center" bordercolor="">	
									<tr>
										<td width="33%" align="center" >
											|_____________________|
										</td>
										<td width="33%" align="center" >
											|_____________________|
										</td>
										<td width="33%" align="center" >
											|_____________________|
										</td>
									</tr>
									<tr>
										<td width="" class="reportSubTitle"  style="font-size: 8pt"align="center" >
											&nbsp;FIRMA DEL REPRESENTANTE
										</td>
										<td width="" class="reportSubTitle"  style="font-size: 8pt"align="center" >
											&nbsp;FIRMA DE DERECHOHABIENTE
										</td>	
										<td width="" class="reportSubTitle"  style="font-size: 8pt"align="center"  >
											&nbsp;FIRMA DEL C&Oacute;NYUGE
										</td>												
									</tr>
									<tr>
										<td>
											&nbsp;
										</td>
										<td>
											&nbsp;
										</td>
										<td width="" class="reportSubTitle"  style="font-size: 9pt"align="center" >
											(S&oacute;lo en caso de cr&eacute;dito conyugal)
										</td>
									</tr>
								</table>
							</td>
						</tr>
					</table>				
				</td>
			</tr>		
		</table>	
	</td>
</tr>
<tr>
	<td>
		<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="0" align="center" cellpadding="0" cellspacing="0" bordercolor="">  
			<tr>
		    	<td align="center" width="80%">
		    		<b>9. DATOS DE INDENTIFIACI&Oacute;N DEL ASESOR DE CR&Eacute;DITO</b>
		    	</td>
		  	</tr>
		</table>
	</td>
</tr>
<tr>
	<td>
		<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="1" cellspacing="0" cellpadding="0" align="center" >	
			<tr>
				<td >
					<table class="reportSubTitle"  style="font-size: 9pt" width="100%" border="0" cellspacing="0" cellpadding="0" align="center" bordercolor="">	
						<tr>
							<td width="" colspan="3" class="reportSubTitle" style="font-size: 8pt">
								&nbsp;DESIGNO COMO REPRESENTANTE PARA QUE EN MI (O NUESTRO) NOMBRE Y RESPRESENTACI&Oacute;N SOLICITE Y TRAMITE LA INSCRIPCI&Oacute;N DE CR&Eacute;DITO EN LOS T&Eacute;RMINOS DE LAS REGLAS APLICABLES: 
							</td>				
						</tr>
						<tr>
							<td class="reportSubTitle"  style="font-size: 9pt"colspan="3" align="left" style="font-weight: bold">
								&nbsp;
								<% if (bmoProperty.getDevelopmentRegistryId().toInteger() > 0){ %>
									<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="132" value="">     								
									<% } else { %> 
									<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="132" value="">     								
								<% } %>
							</td>
						</tr>
						<tr>
							<td class="reportSubTitle"  style="font-size: 8pt" colspan="3" >
								&nbsp;CURP
							</td>
						</tr>
						<tr>
							<td width="50%" class="reportSubTitle"  style="font-size: 9pt"colspan="">
								&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="65" value="">     								
							</td>
							<td width="50%" class="reportSubTitle"  style="font-size: 9pt"colspan="">
								&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="65" value="">     								
							</td>
						</tr>
						<tr>
							<td width="50%" class="reportSubTitle"  style="font-size: 8pt" colspan="" >
								&nbsp;APELLIDO PATERNO
							</td>
							<td width="50%" colspan="" class="reportSubTitle" style="font-size: 8pt">
								&nbsp;APELLIDO MATERNO
							</td>
						</tr>
						<tr>
							<td class="reportSubTitle"  style="font-size: 9pt" colspan="3" >
								&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="135" value="">     								
							</td>
						</tr>
						<tr>
							<td width=""  colspan="3" class="reportSubTitle" style="font-size: 8pt">
								&nbsp;NOMBRE(S)
							</td>
						</tr> 
					</table>
				</td>
			</tr>
		</table>
	</td>
</tr>
<tr>
	<td>
		<table class="reportSubTitle"  style="font-size: 8pt"width="100%" border="0" cellspacing="0" cellpadding="0" align="center" bordercolor="">		
			<tr>
				<td width="" class="reportSubTitle"  style="font-size: 8pt"align="justify">
					Manifiesto(amos) que todos los datos proporcionados son verdaderos, con pleno conocimiento del art&iacute;culo 58 de la Ley del Infonavit que a la letra dice "Se reputar&aacute; 
					como fraude y se sancionar&aacute; como tal, en los t&eacute;rminos del C&oacute;digo Penal Federal, el obtener los cr&eacute;ditos o recibir los dep&oacute;sitos a que esta Ley se refiere, 
					sin tener derecho a ello, mediante enga&ntilde;o, simulaci&oacute;n o sustituci&oacute;n de persona".<br>
					Asimismo, manifiesto(amos) mi(nuestra) conformidad en que esta solicitud quedar&aacute; sin efectos en el caso de que se hubiera otorgado otro cr&eacute;dito a mi(nuestro) 
					favor, de conformidad con el articulo 47 de la Ley del Infonavit.
					
				</td>
			</tr>
		</table>			
	</td>
<tr>
<tr>
	<td>
		<table class="reportSubTitle"  style="font-size: 8pt"width="100%" border="0" align="center" cellpadding="0" cellspacing="0" bordercolor="">
		   <tr>	  
				<td colspan="4">
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ciudad de <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="40" value="<%= bmoCityCompany.getName().toString().toUpperCase() %>, <%= bmoCityCompany.getBmoState().getName().toString().toUpperCase() %>">  
					&nbsp;a&nbsp; <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="5" value="<%= day%>">  
					&nbsp;de&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="20" value="<%= mes%>">
					&nbsp;de&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold" size="6" value="<%= year%>">  
				</td>
			</tr>	
		</table>				
	</td>
</tr>
<tr>
	<td>
		<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="0" cellspacing="0" cellpadding="0" align="center" bordercolor="">	
			<tr>
				<td width="50%" height="60" align="center">
					|_____________________|
				</td>
				<td width="50%" align="center">
					|_____________________|
				</td>
			</tr>
			<tr>
				<td width="" class="reportSubTitle"  style="font-size: 8pt"align="center">
					&nbsp;FIRMA DE DERECHOHABIENTE
				</td>	
				<td width="" class="reportSubTitle"  style="font-size: 8pt"align="center">
					&nbsp;FIRMA DEL C&Oacute;NYUGE
				</td>												
			</tr>
			<tr>
				<td>
					&nbsp;
				</td>
				<td width="" class="reportSubTitle"  style="font-size: 8pt"align="center">
					(S&oacute;lo en caso de cr&eacute;dito conyugal)
				</td>
			</tr>
		</table>
	</td>
</tr>
<tr>
	<td>
		<table class="reportSubTitle"  style="font-size: 8pt" width="100%" border="0" align="center" cellpadding="0" cellspacing="0" bordercolor="">
		   <tr>	  
				<td colspan="4">
					<b>En el Infonavit todos los tr&aacute;mites son gratuitos</b>
					Consulta paso a paso el avence de tu solicitud de cr&eacute;dito en "Mi espacio Infonavit" www.infonavit.org.mx o para cualquier duda o aclaraci&oacute;n favor de contactarnos a trav&eacute;s
					de Infonatel al tel&eacute;fono 91 71 5050(D.F.) &oacute; para el interior de la Rep&uacute;blica, o en cualquier de nuestras oficinas
				</td>
			</tr>	
		</table>				
	</td>
</tr>
<tr>
	<td>
		<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="0" align="center" cellpadding="0" cellspacing="0" bordercolor="">
		   	<tr>	  
				<td height="90" width="100%" class="reportSubTitle"  style="font-size: 8pt"colspan="" align="center">
					&nbsp;
				</td>
			</tr>	
		</table>				
	</td>
</tr>	
<tr>
	<td>
		<table class="reportSubTitle"  style="font-size: 9pt"width="100%" border="0" align="center" cellpadding="0" cellspacing="0" bordercolor="">
		   	<tr style="font-size: 8pt">	  
				<td width="33%" class="reportSubTitle"  style="font-size: 8pt" colspan="" align="center">
					*DATOS OBLIGATORIOS
				</td>
				<td width="33%" class="reportSubTitle"  style="font-size: 8pt" colspan="" align="center">
					HOJA 2 DE 2
				<td width="33%" class="reportSubTitle"  style="font-size: 8pt" colspan="" align="center">
					CRED.1000.07
				</td>
			</tr>	
		</table>				
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



