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

<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
	<tr>
	    <td align="center">
	        <img src="<%= GwtUtil.getProperUrl(sFParams, "/img/solicitud_infonavit.png")%>">
	    </td>
	    </tr>
</table>

<table class="reportSubTitle" width="100%" border="1" align="center" cellpadding="0" cellspacing="0" >
    <caption>
        <b>1. CR&Eacute;DITO SOLICITADO</b>
    </caption>
    <tr>
	    <td class="reportSubTitle">
		    <table class="reportSubTitle" width="100%" border="0" cellpadding="0" cellspacing="0">
			    <tr>
				    <td align="left">
				    	<b>*PRODUCTO:</b>
				    </td>
				    <td align="right">
				    	&nbsp;INFONAVIT
				    </td>
				    <td width="1%" align="left">
				    	<input type="radio" name="tipocredito" checked value="radiobutton"  >
				    </td>
				    <td align="right"  >
				    	INFONAVIT TOTAL/AG
				    </td>
				    <td width="1%" align="left">
				    	<input type="radio" name="tipocredito" checked value="radiobutton" >
				    </td>
				    <td align="right"  >
				    	COFINAVIT/AG
				    </td>
				    <td width="1%" align="left">
				    	<input type="radio" name="tipocredito" value="radiobutton" >
				    </td>
				    <td align="right"  >
				    	COFINAVIT INGRESOS ADICIONALES
				    </td>
				    <td width="1%" align="left">
				    	<input type="radio" name="tipocredito" value="radiobutton" >
				    </td>
				    <td align="right" >
				    	ENTIDAD FINANCIERA:
				    </td>
				    <td align="left">
				    	<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="30" >
				    </td>
			    </tr>
		    </table>
		</td>
	</tr>
	<tr>
		<td class="reportSubTitle">
			<table class="reportSubTitle"  width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
			        <td width="15%" align="left">
			        	<b>*TIPO DEL CR&Eacute;DITO:</b>
			        </td>
			        <td width="8%"  align="right" >
			        	&nbsp;INDIVIDUAL
			        </td>
			        <td width="5%" align="left">
			        	<input type="radio" name="credito">
			        </td>
			        <td width="10%"  align="right" >
			        	&nbsp;CONYUGAL
			        </td>
			        <td align="left">
			        	<input type="radio" name="credito" >
			        </td>
			        <td width="15%" align="left">
			         	<b> CR&Eacute;DITO EN:</b>
			        </td>
			        <td width="8%" align="right" >
			        	&nbsp;PESOS
			        </td>
			        <td  width="5%" align="left">
			        	<input type="radio" name="creditoEn" checked value="radiobutton">
			        </td>
			        <td  width="10%" align="right" >
			        	&nbsp;VSM
			        </td>
			        <td align="left">
			        	<input type="radio" name="creditoEn" value="radiobutton">
			        </td>
			    </tr>
		    </table>
		</td>
	</tr>
	<tr>
		<td class="reportSubTitle" width="100%">
			<table class="reportSubTitle" width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td class="reportSubTitle"  width="" align="left" rowspan="2">
						<b>* DESTINO DEL CR&Eacute;DITO:</b>
					</td>
					<td align="left">
						Comprar una Vivienda
					</td>
					<td align="left">
						<input type="radio" name="destino">
					</td>
					<td align="left">
						Construir tu Vivienda
					</td>
					<td align="left">
						<input type="radio" name="destino">
					</td>
					<td align="left">
						Reparar, Ampliar o Mejorar la Vivienda
					</td>
					<td align="left">
						<input type="radio" name="destino">
					</td>
					<td align="left">
						Pagar el pasivo o la hipoteca de tu vivienda
					</td>
					<td align="left">
						<input type="radio" name="destino">
					</td>
		       </tr>
		       <tr>
			       <td align="left">
			       		Compra y Mejora de Vivienda
			       </td>
			       <td align="left" >
			       		<input type="radio" name="destino">
			       </td>
			       <td align="left">
			       		Renta con Opci&oacute;n a Compra
			       </td>
			       <td align="left" >
			       		<input type="radio" name="destino">
			       </td>
			   </tr>
		    </table>
		 </td>
    </tr>
    <tr>
                <td class="reportSubTitle" width="100%">
                    <table class="reportSubTitle" width="100%" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                           <td width="12%"  align="left">
                               <b>
                                   &nbsp;Son requisitos y documentaci&oacute;n necesarios para la contrataci&oacute;n del cr&eacute;dito los siguientes:
                                    1.- Participar en el taller de orientaci&oacute;n Saber para Decidir,
                                    2.- Presentar Solicitud de Cr&eacute;dito,
                                    3.- Contar con una relaci&oacute;n laboral Vigente,
                                    4.- Cumplir con las condiciones requeridas conforme a la Evaluaci&oacute;n Integral.<br><br>
                                    El solicitante deber&aacute; reunir los requisitos se&ntilde;alados y presentar la documentaci&oacute;n solicitada al momento de aceptar la Oferta Vinculante.
                               </b>
                            </td>
                        </tr>

                    </table>
                </td>
    </tr>
    <tr>
           <td class="reportSubTitle" width="100%">
               <table class="reportSubTitle" width="100%" border="0" cellspacing="0" cellpadding="0">
	               <tr>
		               <td width="50%" align="left" colspan="2">
			               <b>Plazo del Cr&eacute;dito (Aplica para cr&eacute;dito en pesos y segundo cr&eacute;dito)</b><br>
			               5 a&ntilde;os<input type="radio" name="plazo" checked value="radiobutton" align="left">&nbsp;
			               10 a&ntilde;os<input type="radio" name="plazo"  value="radiobutton" align="left">&nbsp;
			               15 a&ntilde;os<input type="radio" name="plazo"  value="radiobutton" align="left">&nbsp;
			               20 a&ntilde;os<input type="radio" name="plazo"  value="radiobutton" align="left">&nbsp;
			               25 a&ntilde;os<input type="radio" name="plazo"  value="radiobutton" align="left">&nbsp;
			               30 a&ntilde;os<input type="radio" name="plazo"  value="radiobutton" align="left">&nbsp;
			
		               </td>
		               <td width="50%" align="center">
		               		<b>Es el segundo cr&eacute;dito que solicita al Infonavit?</b>
		               		Si<input type="radio" name="solicita" checked value="radiobutton" align="left">&nbsp;
		               		No<input type="radio" name="solicita"  value="radiobutton" align="left">&nbsp;
		               </td>
	               </tr>
               </table>
           </td>
    </tr>
</table> <!-- FIN 1.CREDITO SOLICITADO-->


<table class="reportSubTitle" width="100%" border="1" align="center" cellpadding="0" cellspacing="0">
	<caption>
		<b>2. DATOS PARA DETERMINAR EL MONTO DE CR&Eacute;DITO</b>
	</caption>
	<tr>
		<td class="reportSubTitle">
			<table class="reportSubTitle" width="100%" border="0" cellspacing="0" cellpadding="0" style="font-weight: bold">
				<tr>
		  			<td colspan="5" >
		  				A. EN CASO DE TENER DESCUENTOS FAVOR DE LLENAR LA SIGUIENTE INFORMACI&Oacute;N:
		  			</td>
		  		</tr>
		  		<tr>
                	<td width="70%" colspan="2">
                		&nbsp;
                	</td>
                	<td align="center" >
	  					DERECHOHABIENTE&nbsp;
	  				</td>
	  				<td  align="center" >
	  					C&Oacute;NYUGE
	  				</td>
	  			</tr>
				<tr>
			  		<td colspan="2">
			  			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			  			DESCUENTO MENSUAL POR PENSI&Oacute;N ALIMENTICIA(En su caso):
			  		</td>
		 			<td  align="center" >
		 				$<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="9" value="">
		 			</td>
		  			<td align="center" >
		  				$<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="9" value="">
		  			</td>
				</tr>
				<tr>
					<td colspan="2">
						&nbsp;
					</td>
					<td align="center" style="font-size: 6pt">
						(sin centavos)
					</td>
		  			<td align="center" style="font-size: 6pt">
		  				(sin centavos)
		  			</td>
				</tr>
		        <tr>
		  			<td colspan="5">
		  				B.- EN CASO  DE SOLICITAR UN MONTO DE CR&Eacute;DITO MENOR AL PROPUESTO EN LA PRECALIFICACI&Oacute;N FAVOR DE LLENAR LA SIGUIENTE INFORMACI&Oacute;N:
		  			</td>
		  		</tr>
		  		<tr>
		  			<td colspan="2">
		  				&nbsp;
		  			</td>
					<td align="center" >
						DERECHOHABIENTE&nbsp;
					</td>
			  		<td align="center" >
			  			C&Oacute;NYUGE
					</td>
				</tr>
				<tr>
			  		<td colspan="2">
			  			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			  			MONTO DE CR&Eacute;DITO SOLICITADO:
			  		</td>
		  			<td align="center" >
		      			$<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="9" value="">
		  			</td>
		  			<td align="center" >
		  				$<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="9" value="">
		  			</td>
				</tr>
				<tr>
					<td colspan="2">
						&nbsp;
					</td>
					<td align="center" style="font-size: 6pt">
						(sin centavos)
					</td>
		  			<td align="center" style="font-size: 6pt">
		  				(sin centavos)
		  			</td>
				</tr>
		     	<tr>
		  			<td colspan="6" >
		  				C.- EN CASO DE AHORRO VOLUNTARIO PARA COMPLEMENTAR EL FINANCIAMIENTO DEL INSTITUTO PARA LA ADQUISICI&Oacute;N DE LA VIVIENDA, INDICAR EL MONTO:
		  			</td>
				</tr>
				<tr>
		 		  	<td colspan="2"  >
		 		  		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;MONTO DE AHORRO VOLUNTARIO:
		 		  	</td>
			  		<td align="center" >
			  			$<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="9" value="">
			  		</td>
				</tr>
				<tr>
					<td colspan="2" class="reportSubTitle" >
						&nbsp;
					</td>
					<td align="center" style="font-size: 6pt">
						(sin centavos)
					</td>
				</tr>
				<tr>
		  			<td colspan="6" >
		  				D.- EN CASO DE SOLICITAR CR&Eacute;DITO EN VSM Y HABER OBTENIDO UNA PRECALIFICACI&Oacute;N CON CONDICIONES ACEPTABLES, FAVOR DE SELECCIONAR UNA OPCI&Oacute;N:
		  			</td>
				</tr>
				<tr>
		 		  	<td colspan="4">
		 		  		REDUCCI&Oacute;N DEL 10% DEL MONTO DE CR&Eacute;DITO:
		                <input type="radio" name="vsmPrecalificacion" checked value="radiobutton" align="left">&nbsp;&nbsp;&nbsp;&nbsp;
		                OTORGAR EN GARANT&Iacute;A EL 7.5% DEL VALOR DE LA VIVIENDA DEL SALDO DE SUBCUENTA DE VIVIENDA
		                <input type="radio" name="vsmPrecalificacion"  value="radiobutton" align="left">&nbsp;
		 		  	</td>
				</tr>
			</table>
		</td>
	</tr>
</table><!-- FIN DE: 2. DATOS PARA DETERMINAR EL MONTO DE CREDITO-->


<table class="reportSubTitle" width="100%" border="1" align="center" cellpadding="0" cellspacing="0" >
	<caption>
		<b>3. DATOS DE LA VIVIENDA DESTINO DE CR&Eacute;DITO</b>
	</caption>
	<tr>
		<td class="reportSubTitle" colspan="4">
			<table class="reportSubTitle" width="100%" border="0" cellspacing="0" cellpadding="0" style="font-weight: bold">
				<tr>
					<td align="left" colspan="3" >
		            	&nbsp;
						<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="80" value="<%= bmoProperty.getStreet().toString().toUpperCase()%>">
					</td>
				</tr>
				<tr>
					<td  class="reportSubTitle" align="left" colspan="3" >
						&nbsp;&nbsp;&nbsp;*CALLE
					</td>
				</tr>
	            <tr>
	                <td colspan = "3">&nbsp;</td>
	            </tr>
	            <tr>
                	<td align="left"  width="33%">
						&nbsp;
						&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="6" value="<%= bmoProperty.getNumber().toString().toUpperCase()%>">&nbsp;
		                &nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="6" value="">&nbsp;
						&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="6" value="<%= bmoProperty.getLot().toString().toUpperCase()%>">&nbsp;
						&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="6" value="<%= bmoProperty.getBmoDevelopmentBlock().getCode().toString().toUpperCase()%>">&nbsp;
					</td>
                    <td colspan="2">
                    	&nbsp;
							<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="60" value="<%= bmoDevelopment.getName().toString().toUpperCase()%>">
					</td>
				</tr>
				<tr>
					<td align="left" >
						&nbsp;&nbsp;&nbsp;
						*No. EXT.
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						*No. INT.
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						*LOTE
						&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						*MZA.
					</td>
		                <td colspan="2" >
						&nbsp;&nbsp;&nbsp;*COLONIA O FRACCIONAMIENTO
					</td>
				</tr>
				<tr>
					<td colspan = "3">&nbsp;</td>
				</tr>
				<tr>
					<td align="left" >
						&nbsp;
						<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="40" value="<%= bmoCityDevelopment.getBmoState().getName().toString().toUpperCase()%>">
		         	</td>
		            <td  align="left" >
						&nbsp;
						<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="45" value="<%= bmoCityDevelopment.getName().toString().toUpperCase()%>">
					</td>
					<td align="left" >
						&nbsp;
							<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="8" value="<%= bmoDevelopment.getZip().toString().toUpperCase()%>">
					</td>
				</tr>
				<tr>
					<td  width="33%" align="left">
						&nbsp;&nbsp;&nbsp;*ENTIDAD
					</td>
					<td width="33%" align="left" >
						&nbsp;&nbsp;&nbsp;*MUNICIPIO O DELEGACI&Oacute;N
					</td>
					<td width="33%" align="left" >
						&nbsp;&nbsp;&nbsp;*C&Oacute;DIGO POSTAL
					</td>
				</tr>
				<tr>
					<td class="reportSubTitle" colspan = "3">&nbsp;</td>
				</tr>
				<tr>
					<td width="50%">
						*&iquest;LA VIVIENDA ELEGIDA ES PARA UNA PERSONA CON DISCAPACIDAD?
					</td>
					<td  align="left" class="reportSubTitle"  colspan="2">
						SI<input type="radio" name="discapacidad" value="radiobutton">
						&nbsp;NO<input type="radio" name="discapacidad" value="radiobutton">
					</td>
				</tr>
				<tr>
					<td align="center" colspan="3" style="font-weight: normal">
                    	<br>Nota: En caso de que desee hacer un cambio de vivienda, debe presentar una nueva Solicitud de Inscripci&oacute;n de Cr&eacute;dito
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <tr>
	    <td class="reportSubTitle" colspan="4">
	    	<table class="reportSubTitle" >
	    		<tr>
	    			<td>
	    				&nbsp;Anotar la cantidad que corresponda seg&uacute;n el destino del cr&eacute;dito solicitado:
	    			</td>
	    		</tr>
	    	</table>
	    </td>
    </tr>
    <tr>
    	<td width="25%" align="center">
    		<b>Para comprar vivienda</b>
    	</td>
    	<td width="25%" align="center">
    		<b>Para construir tu vivienda</b>
    	</td>
	    <td  width="25%" align="center">
	    	<b>Para reparar, ampliar o mejorar tu vivienda</b>
	    </td>
	    <td width="25%" align="center">
	    	<b>Para pagar el pasivo o la hipoteca de tu vivienda</b>
	    </td>
    </tr>
    <tr>
        <td class="reportSubTitle" width="25%" align="center" valign="top">
            <table class="reportSubTitle"  border="0" align="center" cellpadding="0" cellspacing="0">
                <tr>
                    <td colspan=""  align="center" >
                        <br>
                        $&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="10" value="">
                    </td>
                </tr>
                <tr>
                    <td align="center" style="font-size: 6pt">
                       (sin centavos)
                    </td>
                </tr>
                <tr>
                    <td align="center">
                        <b>
                            *PRECIO DE COMPRA-VENTA
                        </b>
                    </td>
                </tr>
                <tr>
                    <td align="center">
                        <b> (Precio total pactado libremente entre las partes)</b>
                        <br><br>
                    </td>
                </tr>
            </table>
        </td>
        <td class="reportSubTitle"  width="25%" align="center" valign="top">
            <table class="reportSubTitle" border="0" align="center" cellpadding="0" cellspacing="0">
                <tr>
                    <td align="center" ><br>
                        $&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="10" value="">
                    </td>
                </tr>
                <tr>
                    <td align="center" style="font-size: 6pt">
                        (sin centavos)
                    </td>
                </tr>
                <tr>
                    <td>
                        <b> *MONTO DEL PRESUPUESTO</b>
                    </td>
                </tr>
            </table>
        </td>
        <td class="reportSubTitle" width="25%" align="center" valign="top">
            <table class="reportSubTitle" border="0" align="center" cellpadding="0" cellspacing="0">
                <tr>
                     <td align="center"><br>
                        $&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size=" 10" value="">
                    </td>
                </tr>
                <tr>
                    <td align="center" style="font-size: 6pt">
                    (sin centavos)
                    </td>
                </tr>
                <tr>
                    <td  align="center">
                        <b>*MONTO DEL PRESUPUESTO</b>
                    </td>
                </tr>
                <tr >
                    <td align="center" style="font-size: 9px">
                        <b> AFECTACI&Oacute;N ESTRUCTURAL</b>
                        &nbsp;SI<input type="radio" name="tipocredito" checked value="radiobutton">
                        NO<input type="radio" name="tipocredito" checked value="radiobutton">
                    </td>

                </tr>
            </table>
        </td>
        <td class="reportSubTitle" width="25%" align="center" valign="top">
            <table class="reportSubTitle" border="0" align="center" cellpadding="0" cellspacing="0">
                <tr>
                    <td colspan="" align="center" ><br>
                        $&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="10" value="">
                    </td>
                </tr>
                <tr>
                    <td  align="center" style="font-size: 6pt">
                        (sin centavos)
                    </td>
                </tr>
                <tr>
                    <td align="center">
                        <b>*MONTO DE LA DEUDA</b>
                    </td>
                </tr>
            </table>
        </td>
   </tr>
</table><!-- FIN DE: 3. DATOS DE LA VIVIENDA DESTINO DE CR&Eacute;DITO-->

<table class="reportSubTitle" width="100%" border="1" align="center" cellpadding="0" cellspacing="0">
	<caption>
	    <b>4. DATOS DE LA EMPRESA O PATR&Oacute;N</b>
	</caption>
	<tr>
    	<td class="reportSubTitle">
        	<table class="reportSubTitle" width="100%" border="0" cellspacing="0" cellpadding="0" style="font-weight: bold">
				<tr>
   	      			<td >
   	      				&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="50" value="<%= bmoEmployer.getLegalname().toString().toUpperCase() %>">
                   	</td>
                    <td>
                        &nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="50" value="<%//= bmoEmployer.getNrp().toString().toUpperCase() %>">
                    </td>
                </tr>
                <tr>
                	<td width="60%">
                		&nbsp;&nbsp;*NOMBRE DE LA EMPRESA O PATR&Oacute;N
        			</td>
                    <td width="40%">
                        &nbsp;&nbsp;*N&Uacute;MERO DEL REGISTRO PATRONAL (NRP)
                    </td>
                </tr>
                <tr><td colspan="2">&nbsp;</td></tr>

                <tr>
				   	<td>
				   		&nbsp;TEL&Eacute;FONO DE LA EMPRESA DONDE TRABAJA:
				   	</td>
				   	<td>
				   		&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="5" value="<%//= bmoEmployerPhones.getLada().toString().toUpperCase()%>">
				   		&nbsp; <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="11" value="<%= bmoEmployerPhones.getNumber().toString().toUpperCase()%>">
				   		&nbsp; <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="10" value="<%= bmoEmployerPhones.getExtension().toString().toUpperCase()%>">
				   	</td>
				</tr>
				<tr>
					<td class="reportSubTitle">
						&nbsp;
					</td>
					<td>
						&nbsp;&nbsp;&nbsp;
						LADA &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						N&Uacute;MERO
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						EXTENSI&Oacute;N
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table><!-- FIN DE: 4. DATOS DE LA EMPRESA O PATR&Oacute;N ---------------------------------------------------------------------------------------------------- -->

<table class="reportSubTitle" width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
	<tr style="font-size: 8pt">
		<td width="33%" align="center">
			*DATOS OBLIGATORIOS
		</td>
		<td width="33%" align="center">
			HOJA 1 DE 3
		<td  width="33%" align="center">
			CRED.1000.10
		</td>
	</tr>
</table>

<!-- -----------------------------------------------------------------------------PAGE-BREAK----------------------------------------------------------------------------------- -->
<p style="page-break-after: always"></p>

<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
        <tr>
            <td align="center">
            	<img src="<%= GwtUtil.getProperUrl(sFParams, "/img/solicitud_infonavit.png")%>">
            </td>
        </tr>
</table>
<table class="reportSubTitle" width="100%"  border="1" align="center" cellpadding="0" cellspacing="0" >
	<caption>
	    <b>5. DATOS DE IDENTIFICACI&Oacute;N DEL DERECHO-HABIENTE REFERENCIAS/DATOS QUE SER&Aacute;N VALIDADOS</b>
	</caption>
	<tr>
    	<td class="reportSubTitle">
        	<table class="reportSubTitle" border="0" cellspacing="0" cellpadding="0" style="font-weight: bold">
				<tr>
					<td>
						&nbsp;
						<% if(bmoCustomer.getNss().toString().equals("")){ %>
								<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="25">
		  				<% } else { %>
		  						<%= bmoCustomer.getNss().toString().toUpperCase() %>
		  				<% } %>
		      		</td>
		  			<td>
		  				&nbsp;
		  				<% if(bmoCustomer.getCurp().toString().equals("")){ %>
								<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="25"">
		  				<% } else { %>
		  						<%= bmoCustomer.getCurp().toString().toUpperCase()%>
		  				<% } %>
  					</td>
		  			<td>
		  				&nbsp;
		  				<% if(bmoCustomer.getRfc().toString().equals("")){ %>
		  						<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="14" value="">
		  				<% } else { %>
		  						<%= bmoCustomer.getRfc().toString().toUpperCase() %>
		  				<% } %>
		  			</td>
	  			</tr>
	  			<tr>
				   	<td>
				   		&nbsp;*N&Uacute;MERO DE SEGURIDAD SOCIAL (NSS)
				   	</td>
				   	<td>
				   		&nbsp;CURP
				   	</td>
				   	<td>
				   		&nbsp;R.F.C
				   	</td>
                </tr>
                <tr>
                    <td colspan = "3">&nbsp;</td>
                </tr>
				<tr>
		  			<td >
		  				&nbsp;
		  				<% if(bmoCustomer.getFatherlastname().toString().equals("")){ %>
								<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="60" value="">
		  				<% } else { %>
		  						<%= bmoCustomer.getFatherlastname().toString().toUpperCase() %>
		  				<% } %>
		  			</td>
		  			<td colspan = "2">
		  				&nbsp;
		  				<% if(bmoCustomer.getMotherlastname().toString().equals("")){ %>
		  						<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="60" value="">
		  				<% } else { %>
		  						<%= bmoCustomer.getMotherlastname().toString().toUpperCase() %>
		  				<% } %>
		  			</td>
				</tr>
				<tr>
					<td>
						&nbsp;*APELLIDO PATERNO
					</td>
		  			<td colspan = "2" >
		  				&nbsp;*APELLIDO MATERNO
		  			</td>
				</tr>
                <tr>
                    <td colspan = "3">&nbsp;</td>
                </tr>
				<tr>
		  			<td colspan = "3">
		  				&nbsp;
		  				<% if(bmoCustomer.getFirstname().toString().equals("")){ %>
		  						<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="120" value="">
						<% } else { %>
	  							<%= bmoCustomer.getFirstname().toString().toUpperCase() %>
	  				<% } %>
		  			</td>
				</tr>
				<tr>
					<td colspan = "3" >
		  				&nbsp;*NOMBRE(S)
		  			</td>
  				</tr>	
                <tr>
                    <td colspan = "3">&nbsp;</td>
                </tr>
				<tr>
				   	<td colspan = "3" >
				   		&nbsp;*DOMICILIO ACTUAL DEL DERECHOHABIENTE
				   	</td>
				</tr>
                <tr>
                    <td colspan = "3">&nbsp;</td>
                </tr>
				<tr>
				   	<td colspan = "3" >
				   		&nbsp;
				   		<% if(bmoCustomerAddress.getStreet().toString().equals("") && bmoCustomerAddress.getNumber().toString().equals("")){ %>
				   				<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="100" value="">
		  				<% } else { %>
		  						<%= bmoCustomerAddress.getStreet().toString().toUpperCase() %> <%= bmoCustomerAddress.getNumber().toString().toUpperCase() %> 
		  				<% } %>
				   	</td>
				</tr>
				<tr>
				   	<td colspan = "3" >
		            	&nbsp;*CALLE Y N&Uacute;MERO
				   	</td>
				</tr>
                <tr>
                    <td colspan = "3">&nbsp;</td>
                </tr>
				<tr>
		  			<td>
		  				&nbsp;
		  				<% if(bmoCustomerAddress.getNeighborhood().toString().equals("")){ %>
								<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="40" value="">
		  				<% } else { %>
		  						<%= bmoCustomerAddress.getNeighborhood().toString().toUpperCase() %>
		  				<% } %>
		  			</td>
		  			<td colspan="2">
		  				&nbsp;
		  				<% if(bmoCustomerAddress.getBmoCity().getBmoState().getName().toString().equals("")){ %>
		  						<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="30" value="">
		  				<% } else { %>
		  						&nbsp;<%= bmoCustomerAddress.getBmoCity().getBmoState().getName().toString().toUpperCase() %>
		  				<% } %>
		  			</td>
				</tr>
				<tr>
		  			<td>
		  				&nbsp;*COLONIA O FRACCIONAMIENTO
		  			</td>
		  			<td colspan="2">
		  				&nbsp;* ENTIDAD
		  			</td>
				</tr>
                <tr>
                    <td colspan = "3">&nbsp;</td>
                </tr>
       			<tr>
          			<td>
          				&nbsp;
          				<% if(bmoCustomerAddress.getBmoCity().getName().toString().equals("")){ %>
          						<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="25" value="">
          				<% } else { %>
          						<%= bmoCustomerAddress.getBmoCity().getName().toString().toUpperCase() %>
          				<% } %>
          			</td>
                    <td colspan="2" align="left">
                    	&nbsp;
	          			<% if(bmoCustomerAddress.getZip().toString().equals("")){ %>
								<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="10" value="">
	          			<% } else { %>
	          					<%= bmoCustomerAddress.getZip().toString().toUpperCase() %>
	          			<% } %>
          			</td>
        		</tr>
        		<tr>
   			   		<td>
   			   			&nbsp;*MUNICIPIO O DELEGACI&Oacute;N
   			   		</td>
					<td colspan="2" align="left" >
						&nbsp;* C&Oacute;DIGO POSTAL
					</td>
	      		</tr>
                <tr>
                    <td colspan = "3">&nbsp;</td>
                </tr>
        		<tr>
        			<td>
                    	&nbsp;*TEL&Eacute;FONO: <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="3" value="<%//= bmoCustomerPhones.getLada().toString().toUpperCase() %>">
        				<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="10" value="<%= casa%>">
        			</td>
        			<td colspan="" >
		    			&nbsp;*CELULAR: &nbsp; <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="11" value="<%= movil%>">
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    </td>
                    <td >
                        *G&Eacute;NERO:
				       	M <input type="radio" name="gender" <%--= (bmoCustomer.getGender().toString().equals("t")) ? "checked" : "" --%> value="radiobutton">
				      	F<input type="radio" name="gender" <%--= (bmoCustomer.getGender().toString().equals("f")) ? "checked" : "" --%> value="radiobutton">
			      	</td>
				</tr>
				<tr>
		        	<td width="" colspan="">
		        		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		        		* LADA
		        		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			       		*N&Uacute;MERO
			       	</td>
					<td colspan="2" >
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						N&Uacute;MERO
					</td>
				</tr>
                <tr>
                    <td colspan = "3">&nbsp;</td>
                </tr>
				<tr>
        			<td colspan="3">
        				<% if(personalEmail.equals("")){%>
                    			&nbsp;*CORREO ELECTR&Oacute;NICO: <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="50" value="<%= workEmail%>">
                    	<% }else{ %>
                    			&nbsp;*CORREO ELECTR&Oacute;NICO: <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="50" value="<%= personalEmail%>">
                    	<%}%>
        			</td>
				</tr>
                <tr>
                    <td colspan = "3">&nbsp;</td>
                </tr>
				<tr>
				   	<td colspan="3">
	                    *ESTADO CIVIL:&nbsp;&nbsp;&nbsp;
	                    SOLTERO<input type="radio" name="civil" <%= (bmoCustomer.getMaritalStatus().toChar() == BmoCustomer.MARITALSTATUS_SINGLE) ? "checked" : "" %> >
	                    CASADO<input type="radio" name="civil" <%= (bmoCustomer.getMaritalStatus().toChar() == BmoCustomer.MARITALSTATUS_MARRIED) ? "checked" : "" %> >
	
	                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                    R&Eacute;GIMEN PATRIMONIAL DEL MATRIMONIO:
	                    SEPARACI&Oacute;N DE BIENES<input type="radio" name="regime">
	                    SOCIEDAD CONYUGAL<input type="radio" name="regime">
	                    SOCIEDAD LEGAL<input type="radio" name="regime">
	                </td>
                </tr>
            </table>
        </td>
	</tr>
</table><!-- FIN DE: 5. DATOS DE IDENTIFICACI&Oacute;N DEL DERECHO-HABIENTE REFERENCIAS/DATOS QUE SER&Aacute;N VALIDADOS--------------------------------------------------- -->

<table class="reportSubTitle" width="100%" border="1" align="center" cellpadding="0" cellspacing="0">
	<caption>
		<b>6. DATOS DE INDENTIFICACI&Oacute;N DEL C&Oacute;NYUGE - REFERENCIAS (OBLIGATORIOS EN CR&Eacute;DITOS CONYUGAL)/ DATOS QUE SER&Aacute;N VALIDADOS</b>
	</caption>
	<tr>
		<td class="reportSubTitle">
   			<table class="reportSubTitle" width="100%" border="0" cellspacing="0" cellpadding="0" style="font-weight: bold">
	   			<tr>
		            <td colspan = "3">&nbsp;</td>
		        </tr>
				<tr>
					<td width="33%">
						&nbsp;
						<%// if(nss.equals("")){ %>
							<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="50" value="">
						<%// }else { %>
								<%//= conyAp.toUpperCase()%>
						<%// } %>
					</td>
			        <td width="33%">
				        &nbsp;
						<%// if(curp.equals("")){ %>
							<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="50" value="">
						<%// }else { %>
								<%//= conyAp.toUpperCase()%>
						<%// } %>
			        </td>
		        	<td width="33%">
			        	&nbsp;
						<%// if(rfc.equals("")){ %>
							<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="50" value="">
						<%// }else { %>
								<%//= conyAp.toUpperCase()%>
						<%// } %>
		        	</td>
		        </tr>
		        <tr>
		          	<td>
		            	&nbsp;N&Uacute;MERO DE SEGURIDAD SOCIAL (NSS)
		          	</td>
                    <td>
                        &nbsp;CURP
                    </td>
		          	<td>
		          		&nbsp;R.F.C
		          	</td>
		        </tr>
		        <tr>
	                <td colspan = "3">&nbsp;</td>
	            </tr>
		        <tr>
		          	<td colspan="2" >&nbsp;
		          		<% if(conyAp.equals("")){ %>
								<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="50" value="">
						<% }else { %>
								<%= conyAp.toUpperCase()%>
						<% } %>
		          	</td>
		          	<td >&nbsp;
			      		<% if(conyAm.equals("")){ %>
								<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="50" value="">
						<% }else { %>
								<%= conyAm.toUpperCase() %>
						<% } %>
			      	</td>
		        </tr>
		        <tr>
		          	<td colspan="2" >
		          		&nbsp;*APELLIDO PATERNO
		        	</td>
			      	<td colspan="" >
			      		&nbsp;*APELLIDO MATERNO
			      	</td>
			    </tr>
			    <tr>
	                <td colspan = "3">&nbsp;</td>
	            </tr>
			    <tr>
			      	<td colspan="3" >&nbsp;
			      		<% if(conyName.equals("")){ %>
							<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="125" value="">
						<% } else { %>
								<%= conyName.toUpperCase() %>
						<% } %>
			      	</td>
			    </tr>
		        <tr>
			      	<td colspan="3" >
			      		&nbsp;*NOMBRE(S)
			     	</td>
		        </tr>
		        <tr>
	                <td colspan = "3">&nbsp;</td>
	            </tr>
		        <tr>
		        	<td  colspan="3" >
		        		&nbsp;T&Eacute;LEFONO
		            	<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="5" value="">
		        		<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="8" value="<%= conyPhone %>">
		          		&nbsp;CELULAR: &nbsp;&nbsp;&nbsp;
		          		<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="12" value="<%= conyCellphone %>">
		          	</td>
          		</tr>
        		<tr>
		        	<td  colspan="3" >
		        		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		        		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		        		LADA
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			            N&Uacute;MERO
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						N&Uacute;MERO:
					</td>
		        </tr>
		        <tr>
	                <td colspan = "3">&nbsp;</td>
	            </tr>
		        <tr>
					<td colspan="3">
						&nbsp;CORREO ELECTR&Oacute;NICO: <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="50" value="<%= conyEmail%>">
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td class="reportSubTitle">
	    	<table class="reportSubTitle" width="100%" border="0" cellspacing="0" cellpadding="0" >
	        	<tr valign="MIDDLE">
	        		<td colspan="3" >
		        		<br>
		        		<b>NIVEL DE ESCOLARIDAD:</b>&nbsp;
		        		SIN ESTUDIOS<input type="radio" checked name="nivstudycon">
		        		PRIMARIA<input type="radio" name="nivstudycon">
		        		SECUNDARIA<input type="radio" name="nivstudycon">
		        		PREPARATORIA<input type="radio" name="nivstudycon">
		        		T&Eacute;CNICO<input type="radio" name="nivstudycon">
		        		LICENCIATURA<input type="radio" name="nivstudycon">
		        		POSGRADO<input type="radio" name="nivstudycon">
		        		<br>&nbsp;
	        		</td>
	         	</tr>
	        </table>
	    </td>
	</tr>
	<tr>
		<td class="reportSubTitle">
			<table class="reportSubTitle" width="100%" border="0" cellspacing="0" cellpadding="0" style="font-weight: bold">
				<tr>
					<td>
						&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="50" value="<%--= bmoConjEmployer.getLegalName().toString().toUpperCase()--%>">
					</td>
					<td >
						&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="50" value="<%--= bmoConjEmployer.getNrp().toString().toUpperCase()--%>">
					</td>
				</tr>
				<tr>
					<td width="50%">
						&nbsp;&nbsp;*NOMBRE DE LA EMPRESA O PATR&Oacute;N
					</td>
					<td>
						&nbsp;&nbsp;*N&Uacute;MERO DEL REGISTRO PATRONAL (NRP)
					</td>
				</tr>
				<tr><td colspan="2">&nbsp;</td></tr>
				<tr>
					<td width="50%">
						&nbsp;TEL&Eacute;FONO DE LA EMPRESA DONDE TRABAJA EL C&Oacute;NYUGE:
					</td>
					<td>
						&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="5" value="">
						&nbsp; <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="10" value="">
						&nbsp; <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="10" value="">
					</td>
				</tr>
				<tr>
					<td>
						&nbsp;
					</td>
					<td>
						&nbsp;&nbsp;&nbsp;
						LADA
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						N&Uacute;MERO
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						EXTENSI&Oacute;N
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table><!-- FIN DE: 6. DATOS DE INDENTIFICACI&Oacute; DEL C&Oacute;NYUGE - REFERENCIAS (OBLIGATORIOS EN CR&Eacute;DITOS CONYUGAL)/ DATOS QUE SER&Aacute;N VALIDADOS -->

<table class="reportSubTitle" width="100%" border="1" align="center" cellpadding="0" cellspacing="0">
    <caption>
        <b>7. REFERENCIAS FAMILIARES DEL DERECHOHABIENTE/DATOS QUE SER&Aacute;N VALIDADOS</b>
    </caption>
    <tr>
		<td class="reportSubTitle">
        	<table class="reportSubTitle" width="100%" border="0" cellspacing="0" cellpadding="0" style="font-weight: bold">
                <tr>
                    <td>
                    	<% if(familiar1Ap.equals("")){ %>
                    			<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="50" value="">
                        <% } else { %>
                            	<%= familiar1Ap.toUpperCase()%>
                        <% } %>
                    </td>
                </tr>
                <tr>
                    <td>
                        &nbsp;*APELLIDO PATERNO
                    </td>
                </tr>
                <tr>
                    <td>
                    	<% if(familiar1Am.equals("")){ %>
                        		<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="50" value="">
                        <% } else { %>
                                <%= familiar1Am%>
                        <% } %>
                    </td>
                </tr>
                <tr>
                    <td>
                    	&nbsp;*APELLIDO MATERNO
                    </td>
                </tr>
                <tr>
                    <td>
                        <% if(familiar1Name.equals("")){ %>
                                <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="50" value="">
                        <% } else { %>
                                <%= familiar1Name.toUpperCase()%>
                        <% } %>
                    </td>
                </tr>
                <tr>
                    <td>
                    	&nbsp;*NOMBRE (S)
                    </td>
                </tr>
                <tr>
                    <td>
                        &nbsp;*TEL&Eacute;FONO:
                        &nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="5" value="">
                        <% if(familiar1Phone.equals("")) { %>
                       			&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="10" value="">
                        <% }else {%>
               					&nbsp;&nbsp;&nbsp;<%= familiar1Phone%>
                        <% } %>
                    </td>
                </tr>
                <tr>
	                <td>
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        LADA
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        N&Uacute;MERO
	                </td>
                </tr>
                <tr>
                	<td>
                    	&nbsp;CELULAR:
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        <% if(familiar1Cellphone.equals("")) { %>
		               			&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="10" value="<%= familiar1Cellphone%>">
		                <% }else {%>
		       					&nbsp;&nbsp;&nbsp;<%= familiar1Cellphone%>
		                <% } %>
                	</td>
                </tr>
                <tr>
                    <td>
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        N&Uacute;MERO
                    </td>
                </tr>
            </table>
        </td>
		<td class="reportSubTitle">
			<table class="reportSubTitle" width="100%" border="0" cellspacing="0" cellpadding="0" style="font-weight: bold">
				<tr>
					<td>&nbsp;
						<% if(familiar2Ap.equals("")){ %>
			        			<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="50" value="">
			            <% } else { %>
			                	<%= familiar2Ap.toUpperCase()%>
			            <% } %>
					</td>
				</tr>
				<tr>
					<td>&nbsp;*APELLIDO PATERNO</td>
			    </tr>
			    <tr>
			    	<td>&nbsp;
				    	<% if(familiar2Am.equals("")){ %>
			        			<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="50" value="">
			            <% } else { %>
			                	<%= familiar2Am.toUpperCase()%>
			            <% } %>
					</td>
			    </tr>
			    <tr>
			    	<td>&nbsp;*APELLIDO MATERNO</td>
			    </tr>
			    <tr>
				    <td>&nbsp;
					    <% if(familiar2Name.equals("")){ %>
			        			<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="50" value="">
			            <% } else { %>
			                	<%= familiar2Name.toUpperCase()%>
			            <% } %>
			    	</td>
			    </tr>
			    <tr>
			    	<td colspan="">&nbsp;*NOMBRE (S)</td>
			    </tr>
			    <tr>
				    <td>
					    &nbsp;*TEL&Eacute;FONO: &nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="5" value="">
					    
					    <% if(familiar2Phone.equals("")) { %>
		               			&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="10" value="">
		                <% }else {%>
		       					&nbsp;&nbsp;&nbsp;<%= familiar2Phone%>
		                <% } %>
				    </td>
			    </tr>
			    <tr>
				    <td>
					    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					    LADA
					    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					    &nbsp;N&Uacute;MERO
				    </td>
			    </tr>
			    <tr>
				    <td>
				    	&nbsp;CELULAR:
				    	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	
				    	<% if(familiar2Cellphone.equals("")) { %>
			           			&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="10" value="<%= familiar2Cellphone%>">
			            <% }else {%>
			   					&nbsp;&nbsp;&nbsp;<%= familiar2Cellphone%>
			            <% } %>
			    	</td>
		    	</tr>
		    	<tr>
			    	<td>
				    	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				    	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				    	N&Uacute;MERO
			    	</td>
		    	</tr>
			</table>
		</td>
	</tr>
</table><!-- 7. REFERENCIAS FAMILIARES DEL DERECHOHABIENTE/DATOS QUE SER&Aacute;N VALIDADOS----------------------------------------------------------------- -->

<table class="reportSubTitle" width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
	<tr style="font-size: 8pt">
		<td width="33%" align="center" >
			*DATOS OBLIGATORIOS
		</td>
		<td width="33%" align="center" >
			HOJA 2 DE 3
		<td width="33%" align="center" >
			CRED.1000.10
		</td>
	</tr>
</table>

<!-- ---------------------------------------------------------------------------------PAGE-BREAK------------------------------------------------------------------------------------------>
<p style="page-break-after: always"></p>

<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
	<tr>
	    <td align="center">
	    	<img src="<%= GwtUtil.getProperUrl(sFParams, "/img/solicitud_infonavit.png")%>">
	    </td>
	</tr>
</table>
<table class="reportSubTitle" width="100%" border="1" cellspacing="0" cellpadding="0" align="center" >
	<caption>
		<b>8. DATOS PARA ABONO EN CUENTA DEL CR&Eacute;DITO</b>
	</caption>
	<tr>
		<td class="reportSubTitle" width="50%">
			<table class="reportSubTitle" width="100%" border="0" cellspacing="0" cellpadding="0" style="font-weight: bold">
				<tr>
			        <td align="left" >
			            &nbsp;
			            *DATOS DEL: VENDEDOR Y/O APODERADO DEL VENDEDOR <input name="salesman" checked type="radio">
			            &nbsp; AGENTE INMOBILIARIO <input name="salesman" type="radio">
			            &nbsp; ADMINISTRADORA DESIGNADA PARA CONSTRUCCI&Oacute;N <input name="salesman" type="radio">
			            &nbsp; DERECHOHABIENTE <input name="salesman" type="radio">
			            &nbsp;EMPRENDEDOR <input type="radio" name="salesman" >
			        </td>
	            </tr>
	            <tr>
		            <td>
		                &nbsp;
		                <% if (bmoCompany.getLegalName().toString().equals("")){ %>
		                        <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="60">
		                <% } else { %>
		                        <%= bmoCompany.getLegalName().toString().toUpperCase() %>
		                <%} %>
		            </td>
	            </tr>
	            <tr>
	                <td>
	                    &nbsp;* NOMBRE O DENOMINACI&Oacute;N O RAZ&Oacute;N SOCIAL
	                </td>
	            </tr>
	            <tr>
	                <td>
	                    &nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="60">
	                </td>
	            </tr>
	            <tr>
	                <td>
	                    &nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="60">
	                </td>
	            </tr>
	            <tr>
	                <td>
	                    &nbsp;*R.F.C.&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="53" value="<%= bmoCompany.getRfc().toString().toUpperCase() %>">
	                </td>
	            </tr>
	            <tr>
	                <td>
	                    &nbsp;
	                    <% if (bmoCompany.getLegalName().toString().equals("")){ %>
	                            <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="60" value="">
	                    <% } else { %>
	                            <%= bmoCompany.getLegalName().toString().toUpperCase() %>
	                    <% } %>
	                </td>
	            </tr>
	            <tr>
	                <td>
	                    &nbsp;*NOMBRE O DENOMINACI&Oacute;N O RAZ&Oacute;N SOCIAL COMO APARECE EN EL ESTADO DE CUENTA
	                </td>
	            </tr>
	            <tr>
	                <td>
	                    &nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="60" value="">
	                </td>
	            </tr>
	            <tr>
	                <td>
	                    &nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="60" value="">
	                </td>
	            </tr>
	            <tr>
	                <td>
	                    &nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="60" value="">
	                </td>
	            </tr>
	            <tr>
	                <td>
	                    &nbsp;*CLAVE BANCARIA ESTANDARIZADA (CLABE)
	                </td>
	            </tr>
	        </table>
		</td>
		<td class="reportSubTitle" width="50%" >
			<table class="reportSubTitle" width="100%" border="0" cellspacing="0" cellpadding="0" style="font-weight: bold">
				<tr>
	                <td align="center">
                        DATOS DEL ACREEDOR HIPOTECARIO
	                </td>
				</tr>
				<tr>
		            <td>
		                &nbsp;
		            </td>
				</tr>
				<tr>
					<td>
						<%
							//Paquete
							BmoDevelopmentRegistry bmoDevelopmentRegistry = new BmoDevelopmentRegistry();
							PmDevelopmentRegistry pmDevelopmentRegistry = new PmDevelopmentRegistry(sFParams);
							if(bmoProperty.getDevelopmentRegistryId().toInteger() > 0)
								bmoDevelopmentRegistry = (BmoDevelopmentRegistry)pmDevelopmentRegistry.get(bmoProperty.getDevelopmentRegistryId().toInteger());
							
							//Acreedor
							BmoSupplier bmoSupplier = new BmoSupplier();
							PmSupplier pmSupplier = new PmSupplier(sFParams);
							if(bmoDevelopmentRegistry.getSupplierId().toInteger() > 0)
								bmoSupplier = (BmoSupplier)pmSupplier.get(bmoDevelopmentRegistry.getSupplierId().toInteger());
						%>
						&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="50" value="<%= bmoSupplier.getName().toString().toUpperCase()%>">
			
					</td>
				</tr>
				<tr>
					<td>
						&nbsp;NOMBRE O DENOMINACI&Oacute;N O RAZ&Oacute;N SOCIAL
					</td>
				</tr>
				<tr>
					<td>
						&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="60" value="">
					</td>
				</tr>
				<tr>
					<td>
						&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="60" value="">
					</td>
				</tr>
				<tr>
					<td>
						&nbsp;*R.F.C.&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="53" value="<%= bmoSupplier.getRfc().toString().toUpperCase()%>">
					</td>
				</tr>
				<tr>
					<td>
						<% if (bmoSupplier.getName().toString().equals("")){ %>
								&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="50" value="<%= bmoSupplier.getName().toString().toUpperCase()%>">
						<% } else { %>
								&nbsp;&nbsp;<%= bmoSupplier.getName().toString().toUpperCase()%>
						<% } %>
					</td>
				</tr>
				<tr>
					<td>
						&nbsp;*NOMBRE O DENOMINACI&Oacute;N O RAZ&Oacute;N SOCIAL COMO APARECE EN EL ESTADO DE CUENTA
					</td>
				</tr>
				<tr>
					<td>
						&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="60" value="">
					</td>
				</tr>
				<tr>
					<td>
						&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="60" value="">
					</td>
				</tr>
				<tr>
					<td>
						<!--&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="60" value="<%//= boAllocation1.getBoHouse().getBoPackage().getBankCode().toString().toUpperCase() %>">-->
						&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"   style="font-size: 9pt; font-weight: bold" size="60" value="<%= bmoDevelopmentRegistry.getAccount().toString().toUpperCase() %>"> 
					</td>
				</tr>
				<tr>
					<td>
						&nbsp;*CLAVE BANCARIA ESTANDARIZADA (CLABE)
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td colspan = "2" align="center" >
        	<i>En caso de existir varios destinatarios de pago y/o acreedores hipotecarios se deber&aacute; reimprimir la tercera hoja de esta solicitud para su llenado.</i>
		</td>
	</tr>
	<tr>
		<td class="reportSubTitle"  colspan="2">
			<table class="reportSubTitle" width="100%" border="0" cellspacing="0" cellpadding="0" style="font-weight: bold">
				<tr>
					<td  valign="center" >
                    	TITULAR Y C&Oacute;NYUGE (EN CASO DE CR&Eacute;DITO C&Oacute;NYUGAL)
                    </td>
            	</tr>
				<tr>
					<td >
						N&Uacute;MERO DE CR&Eacute;DITO OTORGADO POR INFONAVIT
						&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="30" value="">
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="30" value=""><br>
					</td>
				</tr>
                <tr>
                    <td align="left">
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        S&oacute;lo para ser llenado en caso de que la vivienda tenga cr&eacute;dito(s) vigente(s)
                    </td>
                </tr>
				<tr>
					<td >
						N&Uacute;MERO DE INVENTARIO VIVIENDA RECUPERADA
		                &nbsp;&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="30">
					</td>
				</tr>
				<tr>
					<td >
						N&Uacute;MERO DE CR&Eacute;DITO DE LA ENTIDAD FINANCIERA
						&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="60">
					</td>
				</tr>
				<tr>
					<td colspan="2" align="left">
						S&oacute;lo aplica cuando el destino del cr&eacute;dito es para pagar el Pasivo o la Hipoteca de tu Vivienda
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table><!-- FIN DE: 8. DATOS PARA ABONO EN CUENTA DEL CR&Eacute;DITO -------------------------------------------------------------------------- --> 

<table class="reportSubTitle" width="100%" border="1" cellspacing="0" cellpadding="0" align="center">
	<caption>
		<b>9. DESIGNACI&Oacute;N DEL REPRESENTANTE (EN SU CASO)</b>
	</caption>
	<tr>
		<td class="reportSubTitle" >
			<table class="reportSubTitle" width="100%" border="0" cellspacing="0" cellpadding="0" align="center" style="font-weight: bold">
				<tr>
					<td colspan="3">
						&nbsp;DESIGNO COMO REPRESENTANTE PARA QUE EN MI (O NUESTRO) NOMBRE Y RESPRESENTACI&Oacute;N SOLICITE Y TRAMITE LA INSCRIPCI&Oacute;N DE CR&Eacute;DITO EN LOS T&Eacute;RMINOS DE LAS REGLAS APLICABLES:
					</td>
				</tr>
				<tr>
					<td width="50%" colspan="" >
						&nbsp;<% if (bmoDevelopmentRegistry.getId() > 0){ %>
									<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="60" value="<%//= bmoDevelopmentRegistry.getFatherLastNameRep().toString().toUpperCase()%>">
							  <% } else { %>
									<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="60" value=">">
							  <% } %>
					</td>
					<td width="50%" colspan="" >
						&nbsp;<% if (bmoDevelopmentRegistry.getId() > 0){ %>
							<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="60" value="<%//= bmoDevelopmentRegistry.getMotherLastNameRep().toString().toUpperCase()%>">
							<% } else { %>
							<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="60" value="">
						<% } %>
					</td>
				</tr>
				<tr>
					<td width="50%" colspan="" >
						&nbsp;&nbsp;&nbsp;*APELLIDO PATERNO
					</td>
					<td width="50%" colspan="" >
						&nbsp;&nbsp;&nbsp;*APELLIDO MATERNO
					</td>
				</tr>
				<tr>
					<td class="reportSubTitle" colspan="3" >
						&nbsp;
						<% if (bmoDevelopmentRegistry.getId() > 0){ %>
								<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="120" value="<%//= bmoDevelopmentRegistry.getFirstNameRep().toString().toUpperCase()%>">
						<% } else { %>
								<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="120" value="">
						<% } %>
					</td>
				</tr>
				<tr>
					<td  width=""  colspan="3">
						&nbsp;&nbsp;NOMBRE(S)
					</td>
				</tr>
				<tr>
					<td colspan="2" >
						&nbsp;&nbsp;TEL&Eacute;FONO: &nbsp;
						<%-- if (bmoDevelopmentRegistry.getId() > 0){ --%>
								<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="5" value="<%--= boAllocation1.getBoHouse().getBoPackage().getLadaRep().toString().toUpperCase()--%>">
						<%-- }else{--%>
								<!--<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="5" value="">-->
						<%-- }--%>
						<%-- if (bmoDevelopmentRegistry.getId() > 0){ --%>
								&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="10" value="<%--= boAllocation1.getBoHouse().getBoPackage().getTelephoneRep().toString().toUpperCase()--%>">
						<%-- }else{--%>
								<!-- &nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="10" value="<%--= boLoggedUser1.getOfficephone().toString().toUpperCase()--%>">-->
						<%-- }--%>
		
						CELULAR:&nbsp;
						<%-- if (bmoDevelopmentRegistry.getId() > 0){ --%>
								<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="10" value="<%--= boAllocation1.getBoHouse().getBoPackage().getExtRep().toString().toUpperCase()--%>">
						<%-- }else{--%>
								<!-- <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="5" value="<%--= boLoggedUser1.getMobile().toString().toUpperCase()--%>">-->
						<%-- }--%>
						
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="30" value="">
					</td>
				</tr>
				<tr>
					<td colspan="2">&nbsp;
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						LADA
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						N&Uacute;MERO
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						N&Uacute;MERO
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						N&Uacute;MERO (CREDENCIAL IFE / PASAPORTE)
					</td>
				</tr>
				<tr>
					<td class="reportSubTitle" height="25" colspan="3">&nbsp;</td>
				</tr>
				<tr>
					<td class="reportSubTitle" colspan="2">
						<table class="reportSubTitle" width="100%" border="0" cellspacing="0" cellpadding="0" align="center" style="font-weight: bold">
							<tr>
								<td width="33%"  align="center" >
									|____________________________|
								</td>
								<td width="33%"  align="center" >
									|____________________________|
								</td>
								<td width="33%"  align="center" >
									|____________________________|
								</td>
							</tr>
							<tr>
								<td width="33%" align="center" >
									&nbsp;FIRMA DEL REPRESENTANTE
								</td>
								<td width="33%"  align="center" >
									&nbsp;FIRMA DE DERECHOHABIENTE
								</td>
								<td width="33%"  align="center"  >
									&nbsp;FIRMA DEL C&Oacute;NYUGE (S&oacute;lo en caso de cr&eacute;dito conyugal)
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table> <!-- FIN DE: 9. DESIGNACI&Oacute;N DEL REPRESENTANTE (EN SU CASO)-------------------------------------------------------------------------------- -->


<table class="reportSubTitle" width="100%" border="1" cellspacing="0" cellpadding="0" align="center">
	<caption>
	    <b>10. DATOS DE IDENTIFICACI&Oacute;N DEL CONTACTO</b>
	</caption>
	<tr>
	    <td class="reportSubTitle">
	        <table class="reportSubTitle" width="100%" border="0" cellspacing="0" cellpadding="0" align="center" style="font-weight: bold">
	            <tr>
	                <td width="20%" colspan="">&nbsp;
	                    <% if(bmoCustomer.getCurp().toString().equals("")){ %>
	                            <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="20" value="">
	                    <% } else { %>
	                            <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="20" value="<%= bmoCustomer.getCurp().toString().toUpperCase()%>">
	                    <% } %>
	                </td>
	                <td width="20"  align="right" >
                        &nbsp; ASESOR DE CR&Eacute;DITO
	                </td>
	                <td width="3%" align="left">
                        <input  type="radio" name="tipocredito" checked value="radiobutton"  >
	                </td>
	                <td  width="20%" align="right"  >
                        &nbsp;EMPRENDEDOR
	                </td>
	                <td width="3%" align="left">
                        <input  type="radio" name="tipocredito" checked value="radiobutton" >
	                </td>
	                <td  width="20%"  align="right"  >
                        &nbsp;AGENTE INMOBILIARIO
	                </td>
	                <td width="3%" align="left">
                        <input  type="radio" name="tipocredito" value="radiobutton" >
	                </td>
	            </tr>
	            <tr>
                    <td class="reportSubTitle" colspan="8"  >
                        *CURP&nbsp;
                    </td>
	            </tr>
	            <tr>
                    <td class="reportSubTitle"  colspan="" >
                        <%--// if(boAllocation1.getBoCustomer().getConjFatherLastName().toString().equals("")){ --%>
                                        <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="60" value="">
                        <%-- //} else { --%>
                                <%--//= boAllocation1.getBoCustomer().getConjFatherLastName().toString().toUpperCase() --%>
                        <%--//} --%>
                    </td>
                    <td class="reportSubTitle" colspan="6" >
                        <%-- //if(boAllocation1.getBoCustomer().getConjMotherLastName().toString().equals("")){ --%>
                                        <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="60" value="">
                        <%-- //} else { --%>
                                <%--//= boAllocation1.getBoCustomer().getConjMotherLastName().toString().toUpperCase() --%>
                        <%--//} --%>
                    </td>
	            </tr>
		        <tr>
	                <td class="reportSubTitle" colspan="" >
	                    &nbsp;*APELLIDO PATERNO
	                </td>
	                <td class="reportSubTitle" colspan="6" >
	                    &nbsp;*APELLIDO MATERNO
	                </td>
	            </tr>
	            <tr>
                    <td class="reportSubTitle" colspan="7" >
                        <%-- //if(boAllocation1.getBoCustomer().getConjFirstName().toString().equals("")){ --%>
                                        <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="125" value="">
                        <%-- //} else { --%>
                                <%--//= boAllocation1.getBoCustomer().getConjFirstName().toString().toUpperCase() --%>
                        <%--//} --%>
                    </td>
	            </tr>
	            <tr>
                    <td class="reportSubTitle"  colspan="7" >
                        &nbsp;*NOMBRE(S)
                    </td>
	            </tr>
	            <tr>
                    <td class="reportSubTitle" colspan="7">
                        *T&Eacute;LEFONO
                        <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="5" value="">
                        <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="8" value="<%--= boAllocation1.getBoCustomer().getConjPhone().toString().toUpperCase() --%>">
                    </td>
	            </tr>
	            <tr>
                    <td class="reportSubTitle" colspan="7">
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        LADA
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        N&Uacute;MERO
                    </td>
	            </tr>
	        </table>
	    </td>
	</tr>
	<tr>
		<td class="reportSubTitle" align="center">
			<i>En caso de existir asesor de cr&eacute;dito y/o emprendedor y/o agente inmobiliario se deber&aacute; reimprimir la tercera hoja de esta solicitud para su llenado.</i>
		</td>
	</tr>
</table> <!-- FIN DE: 10. DATOS DE IDENTIFICACI&Oacute;N DEL CONTACTO ----------------------------------------------------------------------------------------- -->

<table  width="100%" border="1" cellspacing="0" cellpadding="0" align="center">
	<caption>
	    <b class="reportSubTitle">11. OFERTA VINCULANTE</b>
	</caption>
	<tr>
	    <td style="font-size: 10px" >
	        &nbsp; EL SOLICITANTE ______________________________________________________________ REQUIERE UNA OFERTA VINCULANTE
	        por parte del INFONAVIT en el entendido de que al solicitar dicha oferta vinculante no requiere presentar la documentaci&oacute;n
	        solicitada para efectos de la contrataci&oacute;n del cr&eacute;dito sino hasta que dicha Oferta Vinculante haya sido aceptada en su totalidad.
	        <br>
	
	        &nbsp;REQUIERO OFERTA VINCULANTE: &nbsp;&nbsp;&nbsp;&nbsp;
	        SI<input type="radio" name="ofertaVinculante" value="radiobutton">
	        NO<input type="radio" name="ofertaVinculante" value="radiobutton">
	        <br>
	        &nbsp;NOTA: El INFONAVIT se obliga a otorgar el cr&eacute;dito a que se refiere esta solicitud, siempre y cuando verifique que los datos proporcionados
	        por el solicitante son veraces y no se modifiquen durante el tr&aacute;mite del cr&eacute;dito y hasta el momento de otorgamiento del mismo.
	   </td>
	<tr>
</table>

<table class="reportSubTitle" width="99%" border="0" cellspacing="0" cellpadding="0" align="center">
	<tr>
		<td >
			<table class="reportSubTitle" width="100%" border="0" cellspacing="0" cellpadding="0" align="center">
				<tr>
					<td style="font-size: 10px" width="" align="justify">
						Manifiesto (amos) que: a) todos los datos todos los datos proporcionados son verdaderos, con pleno conocimiento del art&iacute;culo 58 de la Ley del Infonavit que a la letra dice "Se reputar&aacute;
						como fraude y se sancionar&aacute; como tal, en los t&eacute;rminos del C&oacute;digo Penal Federal, el obtener los cr&eacute;ditos o recibir los dep&oacute;sitos a que esta Ley se refiere,
						sin tener derecho a ello, mediante enga&ntilde;o, simulaci&oacute;n o sustituci&oacute;n de persona".
						Asimismo, b) acepto (amos) incorporar las ecotecnolog&iacute;as que aseguren el ahorro en agua, energ&iacute;a el&eacute;ctrica y gas, definido por el Infonavit, de acuerdo a mi (nuestro) ingreso salarial.
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<table class="reportSubTitle" width="97%" border="0" align="center" cellpadding="0" cellspacing="0">
	<tr>
       <td class="reportSubTitle"  align="center" colspan="4" style="font-weight: bold">
			Ciudad de <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="30" value="<%= bmoCityDevelopment.getName().toString().toUpperCase() %>, <%= bmoCityDevelopment.getBmoState().getName().toString().toUpperCase() %>">
			&nbsp;a&nbsp; <input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="2" value="<%= day %>">
			&nbsp;de&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="10" value="<%= mes%>">
			&nbsp;de&nbsp;<input type="text" class="reportSubTitle"  style="font-size: 9pt; font-weight: bold"  size="4" value=" <%= year %>">
		</td>
	</tr>
</table>

<table class="reportSubTitle"width="100%" border="0" cellspacing="0" cellpadding="0" align="center">
	<tr>
		<td width="50%" align="center"><br><br>
			<b>|__________________________|</b>
		</td>
		<td width="50%" align="center"><br><br>
			<b>|__________________________|</b>
		</td>
	</tr>
	<tr>
		<td width="50%" align="center">
        	<b>FIRMA DE DERECHOHABIENTE</b>
		</td>
		<td width="50%" align="center">
        	<b>FIRMA DEL C&Oacute;NYUGE (S&oacute;lo en caso de cr&eacute;dito conyugal)</b>
		</td>
	</tr>
</table>

<table width="100%" border="1" align="center" cellpadding="0" cellspacing="0">
    <tr>
        <td align="center" style="font-size: 7pt">
            <b>En el Infonavit todos los tr&aacute;mites son gratuitos.</b><br>
            Consulta paso a paso el avence de tu solicitud de cr&eacute;dito en "Mi espacio Infonavit" www.infonavit.org.mx o para cualquier duda o aclaraci&oacute;n favor de contactarnos a trav&eacute;s
            de Infonatel al tel&eacute;fono 91 71 5050(D.F.) &oacute; 01 800 00 83 900 para el interior de la Rep&uacute;blica, o en cualquier de nuestras oficinas.
        </td>
    </tr>
</table>

<table class="reportSubTitle" width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
	<tr style="font-size: 8pt">
		<td width="33%" align="center">
			*DATOS OBLIGATORIOS
		</td>
		<td width="33%" align="center">
			HOJA 3 DE 3
        </td>
		<td width="33%" align="center">
			CRED.1000.10
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



