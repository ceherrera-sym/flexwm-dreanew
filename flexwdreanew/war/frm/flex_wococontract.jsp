
<!--  
/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author 
 * @version 2013-10
 */ -->
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.shared.co.*"%>
<%@page import="com.flexwm.shared.op.*"%>
<%@page import="com.flexwm.server.co.*"%>
<%@page import="com.flexwm.server.op.*"%>
<%@page import="com.flexwm.shared.wf.*"%>


<%@page import="com.flexwm.server.cm.*"%>
<%@page import="com.flexwm.server.op.*"%>
<%@page import="com.flexwm.server.fi.*"%>
<%@page import="com.flexwm.server.wf.*"%>
<%@page import="com.symgae.client.ui.UiParams"%>

<%@page import="com.flexwm.server.AmountByWord"%>
<%@page import="com.flexwm.server.AmountByWorden"%>
<%@page import="com.flexwm.server.AmountByWordes"%>
<%@page import="com.flexwm.server.IAmountByWord"%>
<%@page import="com.flexwm.server.NumberByWord"%>

<%@include file="../inc/login_opt.jsp" %>

<%
try {
	
	String title = "Contrato de Obra";
	
	BmoWorkContract bmoWorkContract = new BmoWorkContract();
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)sFParams.getBmoProgram(bmoWorkContract.getProgramCode());
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
	
		NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(Locale.US);
	    
	    // Obtener parametros
	    int workContractId = 0;
	    if (isExternal) workContractId = decryptId;
	    else if (request.getParameter("foreignId") != null) workContractId = Integer.parseInt(request.getParameter("foreignId"));
	    
	    PmWorkContract pmWorkContract = new PmWorkContract(sFParams);
	    bmoWorkContract = (BmoWorkContract)pmWorkContract.get(workContractId);
	    
	    AmountByWord amountByWord = new AmountByWord();
		amountByWord.setLanguage("es");
		amountByWord.setCurrency("es");
	    
	    //Ciudad del Proveedor
	    PmSupplierAddress pmSupplierAddress = new PmSupplierAddress(sFParams);
	    BmoSupplierAddress bmoSupplierAddress = new BmoSupplierAddress();
	    PmConn pmConnSupplierAddress = new PmConn(sFParams);
	    pmConnSupplierAddress.open();

	    String sqlSuplAddress = " SELECT * FROM supplieraddress WHERE suad_supplierid = " + bmoWorkContract.getSupplierId().toInteger() + " ORDER BY suad_type DESC";
	    pmConnSupplierAddress.doFetch(sqlSuplAddress);
		if(pmConnSupplierAddress.next()){
			bmoSupplierAddress = (BmoSupplierAddress)pmSupplierAddress.get(pmConnSupplierAddress.getInt("suad_supplieraddressid"));
			System.out.println("bmoSupplierAddress: "+bmoSupplierAddress.getId());
		}

		pmConnSupplierAddress.close();
	    
	    //Ciudad del Company
		PmCity pmCity = new PmCity(sFParams);
	    BmoCity bmoCityCompany = (BmoCity)pmCity.get(bmoWorkContract.getBmoCompany().getCityId().toInteger());
	    
	    BmoDevelopment bmoDevelopment = new BmoDevelopment();
	    PmDevelopment pmDevelopment = new PmDevelopment(sFParams);
	    if(bmoWorkContract.getBmoWork().getDevelopmentPhaseId().toInteger() > 0)
	    bmoDevelopment = (BmoDevelopment)pmDevelopment.get(bmoWorkContract.getBmoWork().getBmoDevelopmentPhase().getDevelopmentId().toInteger());
	    
	    //Ciudad del Desarrollo
	    BmoCity bmoCityDevelopment = new BmoCity();
	    if(bmoDevelopment.getId() > 0)
	    	bmoCityDevelopment = (BmoCity)pmCity.get(bmoDevelopment.getCityId().toInteger());
	    
	    BmoCompany bmoCompany = new BmoCompany();
		PmCompany pmCompany = new PmCompany(sFParams);
		bmoCompany = (BmoCompany)pmCompany.get(bmoWorkContract.getCompanyId().toInteger());
			    
	    // Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
	 	String logoURL ="";
	 	if (!bmoCompany.getLogo().toString().equals(""))
	 		logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
	 	else 
	 		logoURL = sFParams.getMainImageUrl();	    
	   
	    //Anticipo al contracto
	    PmPaccount pmPaccount = new PmPaccount(sFParams);
	    double downPayment = pmPaccount.getDownPayment(bmoWorkContract);
%>	    
		<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
			<tr>
				<td width="20%" align="left">
					<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
				</td>
				<td width="60%" class="documentTitle" align="center">SECCI&Oacute;N 07.5 <br> CONTRATO DE OBRA A PRECIOS UNITARIOS Y TIEMPO DETERMINADO
				<td width="20%" class="documentComments" align="right"><b>C&oacute;digo del Documento: FO-07.5.2.1-1 <br> (30-Sep-04)</b></td>
			</tr>			
		</table>
		<br>
		<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		  <tr>
		    <td class="contractLabel">CONTRATO No.</td>
		    <td class="contractText">
		    	<%= bmoWorkContract.getName().toString().toUpperCase()%>
		    </td>
		    <td class="contractLabel">&nbsp;</td>
		    <td class="">
		    	<input type="text">
		    </td>
		  </tr>
	  </table>
	  <br>
	  <p align="justify" class="documentComments">
		  CONTRATO DE OBRA QUE CELEBRA POR UNA PARTE LA EMPRESA DENOMINADA 
		  <b><%= bmoWorkContract.getBmoCompany().getLegalName().toString().toUpperCase() %></b> REPRESENTADA PARA ESTE ACTO 
		  POR SU REPRESENTANTE GENERAL, <b><%= bmoWorkContract.getBmoCompany().getLegalRep().toString().toUpperCase() %></b>,
		   QUIEN EN LO SUCESIVO Y PARA EFECTO 
		  DEL PRESENTE CONTRATO SE LE DENOMINARA EXCLUSIVAMENTE COMO LA CONTRATANTE Y POR LA OTRA  
		  <b><%= bmoWorkContract.getBmoSupplier().getLegalName().toString().toUpperCase() %></b> 
		  <% if (bmoWorkContract.getBmoSupplier().getFiscalType().equals(BmoSupplier.FISCALTYPE_PERSON)) { %>
		  	REPRESENTADA POR EL SE&Ntilde;OR (A)
		  <% } else  { %>
		  	QUIEN SE REPRESENTA POR
		  <% } %>
		  <% if (bmoWorkContract.getBmoSupplier().getFiscalType().equals(BmoSupplier.FISCALTYPE_PERSON)) { %>
		  	  <%= bmoWorkContract.getBmoSupplier().getLegalRep().toString().toUpperCase() %> 
		  <% } else  { %>
		  	<b>SU PROPIO DERECHO</b>
		  <% } %>
	
		  Y A QUIEN EN LO SUCESIVO Y 
		  PARA EFECTOS DEL PRESENTE CONTRATO SE DENOMINARA UNICAMENTE COMO EL CONTRATISTA, CONTRATO 
		  QUE CELEBRAN AL TENOR DE LAS SIGUIENTES:
	  </p>
		  
	  <p align="center" class="contractTitle">
	  	<b>D E C L A R A C I O N E S</b>
	  </p>
	  
	  <p align="justify" class="documentComments">
		  <b>PRIMERA</b>.- DECLARA EL REPRESENTANTE DE LA CONTRATANTE QUE CUENTA CON PODER SUFICIENTE PARA OBLIGARSE EN 
		  REPRESENTACI&Oacute;N DE LA MISMA COMO LO ACREDITA MEDIANTE EL TESTIMONIO NOTARIAL 
		  <b><%= bmoWorkContract.getBmoCompany().getDeedNumber().toString().toUpperCase() %></b> DE FECHA <b><%= bmoWorkContract.getBmoCompany().getDeedDate().toString().toUpperCase() %></b> OTORGADO ANTE LA FE
		  DEL NOTARIO PUBLICO No. <b><%= bmoWorkContract.getBmoCompany().getDeedNotaryNumber().toString().toUpperCase() %>  LIC.<%= bmoWorkContract.getBmoCompany().getDeedNotaryName().toString().toUpperCase() %> </b>
		  EN LEGAL EJERCICIO EN ESTA CIUDAD DE <b><%= bmoCityCompany.getName().toString().toUpperCase() %></b>,
		  <b><%= bmoCityCompany.getBmoState().getName().toString().toUpperCase() %></b>.
	  </p>

	  <p align="justify" class="documentComments">
		  <b>SEGUNDA</b>.- DECLARA EL REPRESENTANTE DEL CONTRATISTA:
	      <ul class="documentComments">	  
			  <li><b>A)</b>	QUE CUENTA CON TODAS Y CADA UNA DE LAS FACULTADES PARA OBLIGAR A  EL CONTRATISTA  Y QUE EL PODER QUE SE LE HA OTORGADO
			      NO SE LE  A  REVOCADO NI LIMITADO DE ACUERDO AL TESTIMONIO NOTARIAL N&Uacute;MERO <b><%= bmoWorkContract.getBmoSupplier().getLawyerDeed().toString().toUpperCase() %></b>
			              DE FECHA <b><%= bmoWorkContract.getBmoSupplier().getLawyerDeedDate().toString().toUpperCase() %></b>, OTORGADO ANTE LA FE DEL <b>LIC.<%= bmoWorkContract.getBmoSupplier().getLawyerName().toString().toUpperCase() %> </b>
			              NOTARIO PUBLICO NUMERO <b><%= bmoWorkContract.getBmoSupplier().getLawyerNumber().toString().toUpperCase() %></b>, EN LEGAL EJERCICIO DE LA CIUDAD DE <b><%= bmoCityCompany.getName().toString().toUpperCase() %></b>,
			  <b><%= bmoSupplierAddress.getBmoCity().getBmoState().getName().toString().toUpperCase() %></b>.
			  	<br><br>
		  	  </li>	
			 <li><b>B)</b>	QUE TIENE CAPACIDAD JUR&Iacute;DICA PARA CONTRATAR Y REUNE LAS CONDICIONES T&Eacute;CNICAS Y ECON&Oacute;MICAS PARA OBLIGARSE 
			  	A LA EJECUCI&Oacute;N DE LA OBRA DE ESTE CONTRATO Y QUE SU DOMICILIO SE ENCUENTRA UBICADO EN
			  	<b><%= bmoSupplierAddress.getStreet().toString().toUpperCase() %></b>
			  	<b><%= bmoSupplierAddress.getNumber().toString() %></b>
			  	<b><%= bmoSupplierAddress.getNeighborhood().toString().toUpperCase() %></b>
			  	<b><%= bmoSupplierAddress.getZip().toString().toUpperCase() %></b>
			  	<b><%= bmoSupplierAddress.getBmoCity().getName().toString().toUpperCase() %></b>,
			  	<b><%= bmoSupplierAddress.getBmoCity().getBmoState().getName().toString().toUpperCase() %></b>
			  	<br><br>	
		  	</li>
		  	<li><b>C)</b>	QUE SU REGISTRO PATRONAL ANTE EL INSTITUTO MEXICANO DEL SEGURO SOCIAL ES EL 
		  		<b><%= bmoWorkContract.getBmoSupplier().getImss().toString().toUpperCase() %></b> COMO LO ACREDITA CON 
		  		COPIAS FOTOST&Aacute;TICAS SIMPLES DE LOS MISMOS.
		  	</li>
		  </ul>	
	  </p>
	  
	  <p align="justify" class="documentComments">
		  <b>TERCERA</b>.- LA CONTRATANTE DECLARA HABER CONTRATADO LOS SERVICIOS DEL CONTRATISTA PARA LOS TRABAJOS DETALLADOS EN EL
		  PRESUPUESTO ANEXO AL PRESENTE CONTRATO Y QUE FORMA PARTE INTEGRANTE DEL MISMO, TRABAJOS QUE HAN DE DESARROLLARSE EN FRACC. 
		  <b><%= bmoDevelopment.getName().toString().toUpperCase() %></b> EN 
		  <b><%= bmoCityDevelopment.getName().toString().toUpperCase() %></b>, 
		  <b><%= bmoCityDevelopment.getBmoState().getName().toString().toUpperCase() %></b>.
		  </p>
	
	  <p align="justify" class="documentComments">
		  <b>CUARTA</b>.- EL CONTRATISTA DECLARA SER UNA EMPRESA PROFESIONAL Y DEDICADA A LOS TRABAJOS CONTRATADOS, ASI
		  COMO CONOCER LA UBICACI&Oacute;N DE LA OBRA EN LA CUAL HAN DE REALIZARSE LOS MISMOS, RECONOCIENDO QUE DICHO LUGAR
		  RESULTA PROPIO PARA ELLO Y QUE NO REPRESENTA CARACTER&Iacute;STICAS QUE IMPIDAN SU REALIZACI&Oacute;N, EN BASE A LOS PLANOS Y
		  PROYECTOS QUE EXISTEN AL RESPECTO.
	  </p>

	<p align="justify" class="documentComments">
	  <b>QUINTA</b>.- AMBAS PARTES DECLARAN ESTAR DE ACUERDO CON LAS DECLARACIONES QUE ANTECEDEN Y ESTAN DE ACUERDO EN EL DESARROLLO
	      DEL OBJETO DEL PRESENTE CONTRATO DE ACUERDO A LAS SIGUIENTES:
	</p>
	  
    <p align="center" class="contractTitle">
	    <b>C L &Aacute; U S U L A S</b>
	</p>
	<p align="justify" class="documentComments">
		<b>PRIMERA</b>.- <b>CARACTERIZTICAS DE LA OBRA</b>. LA CONTRATANTE ENCOMIENDA AL CONTRATISTA LA REALIZACI&Oacute;N DE UNA OBRA CONSISTENTE EN DESARROLLO DE
		TRABAJOS DE <b><%= bmoWorkContract.getDescription().toString().toUpperCase() %></b>, 
		SUJET&Aacute;NDOSE A LOS PLANOS, ESPECIFICACIONES Y PRESUPUESTOS QUE FUERON 
		DEBIDAMENTE FIRMADOS POR LAS PARTES Y QUE SE ANEXAN EN ESTE CONTRATO COMO PARTE INTEGRANTE DEL MISMO.
		<%
			//Listar las viviendas
		    PmConn pmConn = new PmConn(sFParams);
			pmConn.open();
			String sql = "";
			sql = " SELECT * FROM workcontractproperties " +
				  " LEFT JOIN properties ON (wcpr_propertyid = prty_propertyid) " +		
			      " WHERE wcpr_workcontractid = " + bmoWorkContract.getId();
			pmConn.doFetch(sql);
			while(pmConn.next()) {     
		%>
			    <B><%= pmConn.getString("properties", "prty_code") %></B>,
		<% }
		   pmConn.close();	
		%>
	
		
		EL TOTAL DE UNIDADES A TRABAJAR ES DE <B><%= bmoWorkContract.getQuantity().toInteger() %></B>		 
		.
	</p>
		
	<p align="justify" class="documentComments">
		<b>SEGUNDA</b>.-<b> DEL INICIO Y TERMINO DE LA OBRA</b>. EL CONTRATISTA SE OBLIGA A INICIAR LOS TRABAJOS QUE SE LE ENCOMIENDAN EN EL PRESENTE INSTRUMENTO LEGAL
		A PARTIR DEL DIA  <b><%= bmoWorkContract.getStartDate().toString() %></b> 
		Y TERMINARLOS A MAS TARDAR EL DIA <b><%= bmoWorkContract.getEndDate().toString() %></b>
	</p>

	<p align="justify" class="documentComments">
		<b>TERCERA</b>.- <b>VALOR TOTAL DEL CONTRATO</b>. LA CONTRATANTE Y EL CONTRATISTA ACUERDAN EN QUE EL VALOR TOTAL DEL PRESENTE CONTRATO ES POR LA
		CANTIDAD DE <b><%= SFServerUtil.formatCurrency(bmoWorkContract.getSubTotal().toDouble()) %></b>
		(<b><%= amountByWord.getMoneyAmountByWord(bmoWorkContract.getSubTotal().toDouble()).toUpperCase() %></b>)
		 
		 CON UN IVA DE <b><%= SFServerUtil.formatCurrency(bmoWorkContract.getTax().toDouble()) %></b>, ESTE CONTRATO INCLUYE 
		MANO DE OBRA Y SUMINISTRO DE MATERIALES, AS&Iacute; COMO COSTOS DIRECTOS E INDIRECTOS QUE LAS OBRAS ORIGINEN, Y FUE 
		ESTIMADO EN BASE A LOS PRECIOS UNITARIOS ESTABLECIDOS EN EL PRESUPUESTO FIRMADO. EL CUAL PODR&Aacute; SER MODIFICADO 
		CUANDO SE DEMUESTRE LA EXISTENCIA DE UN AUMENTO O DISMINUCI&Oacute;N DE LOS MATERIALES Y/O MANO DE OBRA QUE SE REQUIERAN 
		PARA LA EJECUCI&Oacute;N DE LAS OBRAS OBJETO DE ESTE CONTRATO PREVIO CONVENIO POR ESCRITO, SIN QUE POR ESTO SE ENTIENDA 
		NOVADO EL CONTRATO PRINCIPAL. TODA VEZ QUE LAS OBLIGACIONES CONTINUAN SIENDO LAS ORIGINALMENTE PACTADAS.
	</p>

	<p align="justify" class="documentComments">
		<b>CUARTA</b>.- <b>DISPONIBILIDAD DEL INMUEBLE Y DOCUMENTOS ADMINISTRATIVOS</b>. LA CONTRATANTE SE OBLIGA A PONER A DISPOSICI&Oacute;N
		DE EL CONTRATISTA EL O LOS INMUEBLES EN QUE SE DEBAN A LLEVAR A CABO LOS TRABAJOS MATERIA DE ESTE CONTRATO, ASI COMO 
		LOS PERMISOS, LICENCIAS Y DEMAS AUTORIZACIONES QUE REQUIERAN PARA SU REALIZACI&Oacute;N.
	</p>
		
	<p align="justify" class="documentComments">
		<b>QUINTA</b>.-<b> ANTICIPOS</b>. PARA LOS TRABAJOS OBJETO DEL PRESENTE CONTRATO, LA CONTRATANTE OTORGARA UN ANTICIPO POR EL
		<b><%= bmoWorkContract.getPercentDownPayment().toDouble() %>%</b> 
		DEL IMPORTE DEL CONTRATO, MISMO QUE EL CONTRATISTA SE COMPROMETE A UTILIZAR EN SU TOTALIDAD EN LA REALIZACI&Oacute;N 
		DE LA OBRA CONTRATADA, Y QUE AMORTIZARA SEG&Uacute;N EL AVANCE DE OBRA DESCONTANDOSE PARA TAL EFECTO UN 
		<b><%= bmoWorkContract.getPercentDownPayment().toDouble() %>%</b> DEL IMPORTE 
		DE LAS ESTIMACIONES PRESENTADAS Y AUTORIZADAS CONCLUYENDO EXACTAMENTE CON EL PAGO DE LA ULTIMA ESTIMACI&Oacute;N, 
		<b> DICHO ANTICIPO SE ENTREGARA CUANDO EL CONTRATISTA HUBIERE ENTREGADO COPIA DE SATIC-02 EXPEDIDO POR EL INSTITUTO MEXICANO
		DEL SEGURO SOCIAL.</b>
	</p>

	<p align="justify" class="documentComments">
		<b>SEXTA</b>.-<b> FORMA DE PAGO</b>. LAS PARTES CONVIENEN QUE LOS TRABAJOS OBJETO DEL PRESENTE CONTRATO, SE PAGUEN MEDIANTE
		LA FORMULACI&Oacute;N DE ESTIMACIONES QUE ABARCARAN PERIODOS DE 12 DIAS H&Aacute;BILES, LAS QUE SERAN PRESENTADAS POR EL 
		CONTRATISTA A LAS RESIDENCIAS DE SUPERVISI&Oacute;N DE LA CONTRATANTE DENTRO DE LOS CUATRO DIAS HABILES SIGUIENTES A LA 
		FECHA DE CORTE LA CONTRATANTE SE COMPROMETE A EFECTUAR LOS PAGOS CORRESPONDIENTES DENTRO DE LOS TRES DIAS POSTERIORES 
		A LA DE LA PRESENTACI&Oacute;N DE LAS ESTIMACIONES. CUANDO LAS ESTIMACIONES NO SEAN PRESENTADAS EN LOS TERMINOS SE&Ntilde;ALADOS, 
		SE INCORPORARAN A LA SIGUIENTE ESTIMACI&Oacute;N PARA QUE EL CONTRATANTE INICIE EL TR&Aacute;MITE PARA SU PAGO.
	</p>
		
	<p align="justify" class="documentComments">
		<b>S&Eacute;PTIMA</b>.- <b>GARANTIAS</b>. EL CONTRATISTA SE OBLIGA A CONSTITUIR A FAVOR DE LA CONTRATANTE A EFECTO DE GARANTIZAR EL
		CUMPLIMIENTO DEL PRESENTE CONTRATO, EN CUANTO A ANTICIPOS, TIEMPO Y CALIDAD DE LA OBRA LAS SIGUIENTES FIANZAS:
		<ul class="documentComments">
		<li><b>A)</b>	FIANZA PARA GARANTIZAR POR EL CONTRATISTA LA DEBIDA INVERSI&Oacute;N Y/O DEVOLUCI&Oacute;N TOTAL O PARCIAL DEL IMPORTE DEL
			ANTICIPO OTORGADO PARA LA REALIZACI&Oacute;N DE LAS OBRAS CONTRATADAS AL <b>100% DEL PROPIO ANTICIPO.</b>
			<br><br>
		</li>	
		<li><b>B)</b>	FIANZA PARA GARANTIZAR POR EL CONTRATISTA EL FIEL Y EXACTO CUMPLIMIENTO DE LAS CL&Aacute;USULAS COMPRENDIDAS EN EL CONTRATO
			EN CUANTO AL TIEMPO Y CALIDAD DE LA OBRA, HASTA POR EL <b>100% DEL VALOR DEL CONTRATO.</b>
			<br><br>
		</li>
		<li><b>C)</b>	FIANZA PARA GARANTIZAR POR EL CONTRATISTA A LA CONTRATANTE EL CUMPLIMIENTO DE TODAS Y CADA UNA DE LAS 
			OBLIGACIONES DERIVADAS DE ESTE CONTRATO Y CONTINUARA VIGENTE TAMBIEN CONSIDERANDO LA FECHA DE RECEPCI&Oacute;N TOTAL DE LAS OBRAS
			DETALLADAS Y DURANTE EL A&Ntilde;O SIGUIENTE TANTO PARA RESPONDER POR LOS DEFECTOS Y/O VICIOS OCULTOS DE LA CONSTRUCCI&Oacute;N 
			COMO DE CUALQUIER RESPONSABILIDAD QUE RESULTARA A CARGO DEL CONTRATISTA, HASTA POR EL <b>100% DEL VALOR TOTAL DEL CONTRATO.</b>
		</li>	
		</ul>	
	</p>

	<p align="justify" class="documentComments">
		<b>OCTAVA</b>.- <b>PENAS CONVENCIONALES</b>. EL CONTRATISTA CONVIENE EN QUE DE NO EJECUTAR LA OBRA DENTRO DEL PLAZO ESTABLECIDO EN LA
		CL&Aacute;USULA SEGUNDA DEL PRESENTE CONTRATO, O QUE NO LA EJECUTE DE ACUERDO AL CONTRATO, PAGARA A LA CONTRATANTE UNA PENALIZACI&Oacute;N A RAZON
		DE <b>			
				<%= SFServerUtil.formatCurrency(bmoWorkContract.getDailySanction().toDouble()) %> (<b><%= amountByWord.getMoneyAmountByWord(bmoWorkContract.getDailySanction().toDouble()).toUpperCase() %></b>)			
		   </b>
		POR CADA DIA DE RETRAZO EN LAS OBRAS A MENOS QUE EXISTA CAUSA JUSTIFICADA Y POR ESCRITO A CRITERIO DE LA CONTRATANTE.
	</p>


	<p align="justify" class="documentComments">
		<b>NOVENA</b>.- EL CONTRATISTA ESTA CONFORME EN ASUMIR TODAS LAS RESPONSABILIDADES LABORALES QUE COMO PATRON LE CORRESPONDEN EN RELACION CON
		LOS TRABAJOS QUE SE ENCOMIENDAN, POR LO TANTO QUEDARAN A SU CARGO EL PAGO DE SALARIO DE SUS TRABAJADORES, INSCRIBIRLOS
		AL I.M.S.S., RETENCION DEL I.S.P.T.,  S.A.R. INDEMNIZACIONES EN CASO DE ACCIDENTE Y EN GENERAL CUMPLIR CON LAS OBLIGACIONES QUE COMO PATRON SE IMPONEN DE ACUERDO
		CON LAS LEYES Y REGLAMENTOS RESPECTIVOS, DEBIENDO ENTREGAR FOTO-COPIA SIMPLE DE LAS LIQUIDACIONES BIMESTRALES DURANTE EL PERIODO
		DE DURACI&Oacute;N DE ESTE CONTRATO; <b>AS&Iacute; MISMO EL CONTRATISTA QUEDA ENTENDIDO QUE LA CONTRATANTE REQUIERE EN FORMA OBLIGATORIA
		LA DICTAMINACI&Oacute;N ANTE EL INSTITUTO MEXICANO DEL SEGURO SOCIAL POR EL PER&Iacute;ODO QUE ABARQUE EL PRESENTE CONTRATO. ADICIONALMENTE
		PARA QUE SE TENGA CERTEZA DE ESTE CUMPLIMIENTO LA CONTRATANTE DEBER&Aacute; CONTAR CON UNA COPIA DEL AVISO DE DICTAMEN SELLADO POR EL
		INSTITUTO MEXICANO DEL SEGURO SOCIAL A MAS TARDAR EL D&Iacute;A 30 DE ABRIL DEL A&Ntilde;O POSTERIOR AL EJERCICIO DE QUE SE TRATE.</B>
	</p>

	<p align="justify" class="documentComments">
		   <b>DECIMA</b>.- CUANDO EL CONTRATISTA DESPIDA A UN TRABAJADOR DE LOS QUE TIENE A SU CARGO O SE GENERE UN CONFLICTO DE CAR&Aacute;CTER LABORAL POR EL MOTIVO QUE SEA, SERA ESTE QUIEN ASUMA TODAS LAS
		RESPONSABILIDADES QUE SE ORIGINEN POR DICHO DESPIDO O CONFLICTO, YA QUE ES EL CONTRATISTA EL PATRON Y DE NINGUNA FORMA SE ENTIENDE POR ESTE CONTRATO LA
		SUSTITUCION PATRONAL.
	</p>


	<p align="justify" class="documentComments">
		<b>DECIMA PRIMERA</b>.- EN BASE A LOS SUPUESTOS QUE ESTABLECE EL ARTICULO 9 FRACCION II DEL IMPUESTO AL VALOR AGREGADO EN RELACI&Oacute;N 
		CON LO QUE SE&Ntilde;ALA EL ARTICULO 21-A DEL REGLAMENTO DE LA CITADA LEY; EL CUAL EXPLICA LA EXENSI&Oacute;N DEL PAGO DEL IMPUESTO DE 
		INMUEBLES DESTINADOS A CASA HABITACI&Oacute;N, QUEDA EXCENTO EL PAGO DE DICHO IMPUESTO SIEMPRE Y CUANDO EL CONTRATISTA PROPORCIONE 
		MANO DE OBRA Y MATERIALES.
	</p>

	<p align="justify" class="documentComments">
		<b>DECIMA SEGUNDA</b>.- LAS PARTES MANIFIESTAN SU CONFORMIDAD EN EL SENTIDO DE QUE COMO FONDO DE GARANT&Iacute;A LA CONTRATANTE RETENGA AL CONTRATISTA EL <b><%= bmoWorkContract.getPercentGuaranteeFund().toString()%>%</b> DE CADA
		ESTIMACI&Oacute;N POR LO CUAL SE FORMARA UN DEP&Oacute;SITO QUE SERVIR&Aacute; COMO GARANT&Iacute;A PARA RESPONDER A SATISFACCI&Oacute;N DE LA CONTRATANTE, 
		DE LOS DEFECTOS DE LAS OBRAS MATERIA DE ESTE CONTRATO, AS&Iacute; COMO CUALQUIER VICIO APARENTE U OCULTO QUE RESULTE O LLEGARA A RESULTAR COMO CONSECUENCIA
		DE LAS OBRAS REALIZADAS Y DE CUALQUIER DIFERENCIA,
		RESPONSABILIDAD O RECLAMACI&Oacute;N QUE RESULTARA SIENDO ESTA RESPONSABILIDAD A CARGO DEL PROPIO CONTRATISTA, ASI COMO PARA GARANTIZAR EL
		CUMPLIMIENTO DE 
		TODAS LAS ESTIPULACIONES CONVENIDAS EN ESTE CONTRATO, EL FONDO DE GARANTIA SERA REEMBOLSADO POR LA CONTRATANTE AL CONTRATISTA, UNA VEZ
		QUE HAYA RECIBIDO DE CONFORMIDAD EL TOTAL DE LAS OBRAS CONTRATADAS 
		<b>Y DESPU&Eacute;S DE UN A&Ntilde;O CONTADO A PARTIR DE LA FECHA DEL FINIQUITO DEL CONTRATO</b>,
		SOLICITUD QUE DEBERA SER ACOMPA&Ntilde;ADA POR EL <b>AVISO DE 
		TERMINACI&Oacute;N DE OBRA DADO AL I.M.S.S. AS&Iacute; COMO CARTA DE CUMPLIMIENTO EMITIDA POR EL DEPARTAMENTO DE AUDITORIA A PATRONES DEL
		I.M.S.S.</b>
	</p>
	
	<p align="justify" class="documentComments">
	    <b>DECIMA TERCERA</b>.-<b>RESCISI&Oacute;N DEL CONTRATO</b>. LAS PARTES CONVIENEN Y EL CONTRATISTA ACEPTA EN FORMA EXPRESA, QUE EL CONTRATANTE
	    PODRA RESCINDIR EL PRESENTE CONTRATO POR CUALESQUIERA CIRCUSTANCIA LEGAL Y DENTRO DE LAS QUE DE MANERA ENUNCIATIVA MAS NO LIMITATIVA A CONTINUACI&Oacute;N SE MENCIONAN:
		<ul class="documentComments">
		<li><b>1.-</b>	CUANDO EL CONTRATISTA NO INICIE LAS OBRAS EN LA FECHA PROGRAMADA.	<br><br></li>
		
		<li><b>2.-</b>	CUANDO EL CONTRATISTA SUSPENDA INJUSTIFICADAMENTE LAS OBRAS, O SE NIEGUE A REPARAR O RESPONDER DE LAS OBRAS QUE LE 
			SEAN RECHAZADAS POR LA CONTRATANTE, POR NO AJUSTARSE A LAS ESPECIFICACIONES PACTADAS.	<br><br></li>
		
		<li><b>3.-</b>	CUANDO EL CONTRATISTA NO EJECUTE LOS TRABAJOS CONFORME A LO ESTABLECIDO EN EL CALENDARIO DE LA OBRA, Y EN GENERAL
			CUANDO CONTRAVENGA LOS TERMINOS DE ESTE CONTRATO.<br><br></li>
			
		<li><b>4.-</b>	CUANDO LA CONTRATANTE AS&Iacute; LO DECIDA POR CONVENIR A SUS PROPIOS INTERESES.<br><br></li>
		
		<li><b>5.-</b>	CUANDO EL SUBCONTRATISTA CEDA O TRASPASE A FAVOR DE TERCERAS PERSONAS LOS DERECHOS Y OBLIGACIONES DERIVADAS DE ESTE CONTRATO SIN LA
		    ANUENCIA PREVIA DE LA COMPA&Ntilde;IA DADA POR ESCRITO.</li>
		</ul>
	</p>
	
	<p align="justify" class="documentComments">
		<b>DECIMA CUARTA</b>.- EL CONTRATISTA ACEPTA EXPRESAMENTE QUE EN SUPUESTO DE RESCISI&Oacute;N DEL CONTRATO POR ALGUNA DE LAS CAUSALES DETALLADAS 
		EN EL PUNTO QUE ANTECEDE, LA CONTRATANTE EJERCERA LAS FIANZAS QUE SE PRECISAN EN EL PRESENTE CONTRATO Y QUE CORRESPONDAN AL SUPUESTO QUE SE GARANTIZO, AS&Iacute; COMO QUEDARA A SU FAVOR EL FONDO DE GARANT&Iacute;A
		RETENIDO AL CONTRATISTA.
	</p>
	
	<p align="justify" class="documentComments">
		<b>DECIMA QUINTA</b>.- EL CONTRATISTA ACEPTA EXPRESAMENTE EL REPARAR LOS DA&Ntilde;OS QUE PUDIERAN SUFRIR LAS OBRAS EN CONSTRUCCI&Oacute;N DENTRO DE 
		SU &Aacute;REA DE ESPECIALIDAD Y DURANTE EL TIEMPO EN QUE LA OBRA SE ENCUENTRE EN PROCESO, AUN EN EL SUPUESTO DE QUE LOS DA&Ntilde;OS SEAN 
		CAUSADOS POR PERSONAS AJENAS AL CONTRATISTA, LAS REPARACIONES SE EFECTUARAN SIN COSTO PARA LA CONTRATANTE, ASI MISMO EL CONTRATISTA 
		ES RESPONSABLE DEL RETIRO DE ESCOMBRO Y/O SOBRANTES DE MATERIAL QUE SE HUBIEREN GENERADO DURANTE EL DESARROLLO DE LOS TRABAJOS 
		ENCOMENDADOS A ELLA, EL ESCOMBRO Y/O MATERIAL SOBRANTE RETIRADOS SERAN DEPOSITADOS POR EL CONTRATISTA EN LOS LUGARES QUE SE&Ntilde;ALEN 
		LAS AUTORIDADES CORRESPONDIENTES.
	</p>
	
	<p align="justify" class="documentComments">
		<b>DECIMA SEXTA</b>.- SERA RESPONSABILIDAD DEL CONTRATISTA EL CUMPLIMIENTO DEL PROGRAMA INTERNO DE PROTECCION CIVIL PARA LA ATENCION DE CONTINGENCIAS AMBIENTALES, DEL CUAL SE ANEXA COPIA ADEMAS DE CUALQUIER TIPO DE ACCIDENTE OCURRIDO A SUS TRABAJADORES O A PERSONAS AJENAS
		A LA OBRA DEBIENDO CUBRIR TODO TIPO DE GASTO Y/O RESPONSABILIDAD DE TIPO LEGAL YA SEA CIVIL, LABORAL, ADMINISTRATIVA, PENAL O DE CUALQUIER INDOLE, QUEDANDO EXENTA DE CUALQUIER
		TIPO DE RESPONSABILIDAD LA CONTRATANTE.
	</p>
		
	<p align="justify" class="documentComments">
		<b>DECIMA S&Eacute;PTIMA</b>.- EL CONTRATISTA ACEPTA LA RESPONSABILIDAD DE COMPARECER ANTE LAS AUTORIDADES COMPETENTES (PROFECO, JUZGADOS DE LO CIVIL, 
		ETC.) A FIN DE DESAHOGAR TR&Aacute;MITES POR DEMANDAS HECHAS A LA CONTRATANTE Y QUE SE REFIEREN A DEFECTOS Y/O VICIOS OCULTOS DE LA 
		CONSTRUCCI&Oacute;N, CALIDAD DE MATERIALES, O MANO DE OBRA U OTROS QUE CORRESPONDAN A LA RESPONSABILIDAD DEL CONTRATISTA POR TRABAJOS 
		DESARROLLADOS EN BASE A ESTE CONTRATO Y DE NO COMPARECER Y POR ESTE INCUMPLIMIENTO LO HAGA LA CONTRATANTE TODOS Y CADA UNO DE LOS GASTOS QUE SE GENEREN
		CORRERAN A CARGO DEL CONTRATISTA Y QUE LE SERAN DESCONTADOS DE SUS FONDOS DE GARANTIA.
	</p>

	<p align="justify" class="documentComments">
		<b>DECIMA OCTAVA</b>.- EL CONTRATISTA ACEPTA EXPRESAMENTE EN ATENDER EN EL PLAZO DE 48 HORAS CUALQUIER PROBLEMA QUE LE SEA REPORTADO POR LA 
		CONTRATANTE Y/O LOS ADQUIRENTES PROPIETARIOS DE VIVIENDA, ASI MISMO SE COMPROMETE A REPARAR EN FORMA INMEDIATA LOS DESPERFECTOS Y/O 
		VICIOS OCULTOS DE LA CONSTRUCCI&Oacute;N EN LO REFERIDO A ESTE CONTRATO Y QUE CORRESPONDAN A SU RESPONSABILIDAD.
	</p>

	<p align="justify" class="documentComments">
		<b>DECIMA NOVENA</b>.- ES RESPONSABILIDAD DEL CONTRATISTA EL EFECTUAR LOS TR&Aacute;MITES PERTINENTES PARA LA OBTENCI&Oacute;N DE PERMISOS Y/O AUTORIZACIONES
		NECESARIOS PARA EL INICIO DE LOS TRABAJOS CONTRATADOS, ASI MISMO EFECTUAR LOS TR&Aacute;MITES QUE CORRESPONDAN ANTE LAS AUTORIDADES 
		COMPETENTES PARA LLEVAR A CABO EL DESARROLLO DE LOS TRABAJOS CONTRATADOS Y EFECTUAR LOS TR&Aacute;MITES PERTINENTES DE ENTREGA,
		DEBIENDO RECABAR LAS ACTAS RESPECTIVAS Y ENTREGARLAS A LA CONTRATANTE.
	</p>

	<p align="justify" class="documentComments">
		<b>VIG&Eacute;SIMA</b>.- LAS PARTES CONTRATANTES DECLARAN SUJETARSE EN CASO DE DESACUERDO EN LA INTERPRETACI&Oacute;N DE LAS CL&Aacute;USULAS DE ESTE CONTRATO 
		A LOS TRIBUNALES DE ESTAS JURISDICCI&Oacute;N DE LE&Oacute;N, GTO. RENUNCIANDO EXPRESAMENTE A CUALQUIER OTRO FUERO QUE POR RAZONES DE VECINDAD 
		LES LLEGAR&Eacute; A CORRESPONDER EN EL PRESENTE O EN EL FUTURO.
	</p>


	<p align="justify" class="documentComments">
		<b>VIG&Eacute;SIMA PRIMERA</b>.- EL CONTRATISTA DEBER&Aacute; PRESENTAR A MAS TARDAR 90 DIAS NATURALES POSTERIORES A LA TERMINACION DE LA OBRA ASI COMO EL OFICIO DE CONCLUSION DE TRAMITE,  UNA CARTA DE NO ADEUDO ANTE EL
		INFONAVIT E IMSS DONDE SE MANIFIESTE QUE SE ENCUENTRAN AL CORRIENTE DE SUS PAGOS ANTE ESTAS DEPENDECIAS LA CUAL DEBER&Aacute; SER RENOVADA EN UN PERIODO NO MAYOR DE 2 MESES 
		DE LO CONTRARIO SE LE RETENDR&Aacute;N SUS PAGOS DE ESTIMACIONES O NO SE LES CONTRATAR&Aacute; OBRA NUEVA.
	</p>

	<p align="justify" class="documentComments">
		MANIFIESTO QUE LOS INMUEBLES EN CONSTRUCCI&Oacute;N DEL PRESENTE CONTRATO SON DEDICADOS EXCLUSIVAMENTE A CASA HABITACI&Oacute;N DE
		ACUERDO CON LAS ESPECIFICACIONES DE CADA INMUEBLE, AS&Iacute; MISMO, MANIFIESTO QUE EL PRESENTE CONTRATO CORRESPONDE
		AL (LOS) N&Uacute;MERO (OS) DE PERMISO DE CONSTRUCCI&Oacute;N: <input type="text"> , ADEM&Aacute;S, MANIFIESTO MI VOLUNTAD DE ASUMIR LA RESPONSABILIDAD
		SOLIDARIA EN LOS T&Eacute;RMINOS DEL ART. 26 FRACC. VIII DEL C&Oacute;DIGO FISCAL DE LA FEDERACI&Oacute;N POR EL IMPUESTO AL VALOR AGREGADO QUE
		CORRESPONDA AL SERVICIO PARCIAL DE CONSTRUCCI&Oacute;N MANIFESTADO EN EL PRESENTE CONTRATO EN CASO DE QUE SUFRA ALTERACI&Oacute;N EL DESTINO
		DE CASA HABITACI&Oacute;N DECLARADO EN LAS LICENCIAS DE AUTORIZACI&Oacute;N CORRESPONDIENTE.
	</p>
		
	<p align="center" class="documentComments">
		&nbsp;<b><%= bmoCityCompany.getName().toString().toUpperCase() %></b>, <b><%= bmoCityCompany.getBmoState().getName().toString().toUpperCase() %></b>,  
		A <b><%= bmoWorkContract.getDateContract().toString() %></b>
	</p>
	<p class="documentComments">&nbsp;</p>	
	
	<table width="100%" align="center" border="0">
	    <tr>
	        <td align="center" class="documentComments">
	            ___________________<br>
	            CONTRATANTE
	        </td>
	        <td align="center" class="documentComments">
	            ___________________<br>
	            CONTRATISTA
	        </td>
	    </tr>    
	
	    <tr>
	        <td align="center" class="documentComments">
	             
	            <b><%= bmoWorkContract.getBmoCompany().getLegalName().toString()%></b>
	        </td>
	        <td align="center" class="documentComments">
	            <b><%= bmoWorkContract.getBmoSupplier().getLegalName().toString() %></b>
	        </td>
	    </tr>
	    <tr>
	    	<td colspan="2" height="80">
	    		&nbsp;
	    	</td>
		</tr>
		<tr>
	        <td align="center" class="documentComments">
	            ___________________
	        </td>
	        <td align="center" class="documentComments">
	            ___________________
	        </td>
	    </tr>    
	    <tr>
	        <td align="center" class="documentComments">
	            TESTIGO
	        </td>
	        <td align="center" class="documentComments">
				TESTIGO
	        </td>
	    </tr>
	</table>
	<br><br>
	
</body>
</html>
<%  
	} catch (Exception e) { 
	    String errorLabel = "Error del Contracto de Obra";
	    String errorText = "El Contracto de Obra no pudo ser desplegado exitosamente.";
	    String errorException = e.toString();
	    
	    response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);
    }
%>	