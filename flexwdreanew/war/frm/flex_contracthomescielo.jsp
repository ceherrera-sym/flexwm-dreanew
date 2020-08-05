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
<%@page import="com.flexwm.server.FlexUtil"%>
<%@page import="com.flexwm.server.AmountByWord"%>
<%@page import="com.flexwm.server.AmountByWorden"%>
<%@page import="com.flexwm.server.AmountByWordes"%>
<%@page import="com.flexwm.server.IAmountByWord"%>
<%@page import="com.flexwm.server.NumberByWord"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@include file="../inc/login_opt.jsp" %>
<%
	String title = "Contrato Casas ECR y SM";
	
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
	<title>:::<%= title %>:::</title>
    <link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>"> 
     <link rel="stylesheet" type="text/css" href="../css/flexwm.css"> 
</head>
<body class="default" <%= permissionPrint %> style="font-size: 12px">

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
		
		//Pedido 
		BmoOrder bmoOrder = new BmoOrder();
		PmOrder pmOrder = new PmOrder(sFParams);
		bmoOrder = (BmoOrder)pmOrder.get(bmoPropertySale.getOrderId().toInteger());
		
		//Pedido-Inmueble		
		BmoOrderProperty bmoOrderProperty = new BmoOrderProperty();
		PmOrderProperty pmOrderProperty = new PmOrderProperty(sFParams);
		bmoOrderProperty = (BmoOrderProperty)pmOrderProperty.getBy(bmoOrder.getId(), bmoOrderProperty.getOrderId().getName());
		
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
		if (!bmoCompany.getLogo().toString().equals(""))
			logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
		else
			logoURL = sFParams.getMainImageUrl();

		//Ciudad de la Empresa
		BmoCity bmoCityCompany = new BmoCity();
		PmCity pmCity = new PmCity(sFParams);
		bmoCityCompany = (BmoCity) pmCity.get(bmoCompany.getCityId().toInteger());

		//Vivienda
		BmoProperty bmoProperty = new BmoProperty();
		PmProperty pmProperty = new PmProperty(sFParams);
		bmoProperty = (BmoProperty) pmProperty.get(bmoPropertySale.getPropertyId().toInteger());

		//Modelo
		BmoPropertyModel bmoPropertyModel = new BmoPropertyModel();
		PmPropertyModel pmPropertyModel = new PmPropertyModel(sFParams);
		bmoPropertyModel = (BmoPropertyModel) pmPropertyModel
				.get(bmoPropertySale.getBmoProperty().getPropertyModelId().toInteger());

		// Etapa Desarrollo
		BmoDevelopmentPhase bmoDevelopmentPhase = new BmoDevelopmentPhase();
		PmDevelopmentPhase pmDevelopmentPhase = new PmDevelopmentPhase(sFParams);
		bmoDevelopmentPhase = (BmoDevelopmentPhase) pmDevelopmentPhase.get(
				bmoPropertySale.getBmoProperty().getBmoDevelopmentBlock().getDevelopmentPhaseId().toInteger());

		// Cuentas de banco
		BmoBankAccount bmoBankAccount = new BmoBankAccount();
		PmBankAccount pmBankAccount = new PmBankAccount(sFParams);
		if (bmoDevelopmentPhase.getBankAccountId().toInteger() > 0)
			bmoBankAccount = (BmoBankAccount) pmBankAccount
					.get(bmoDevelopmentPhase.getBankAccountId().toInteger());

		//Ciudad del Desarrollo
		BmoCity bmoCityDevelopment = new BmoCity();
		PmCity pmCityDevelopment = new PmCity(sFParams);
		bmoCityDevelopment = (BmoCity) pmCityDevelopment
				.get(bmoPropertyModel.getBmoDevelopment().getCityId().toInteger());

		//Direcciones del Cliente
		PmConn pmConnCustomer = new PmConn(sFParams);
		pmConnCustomer.open();
		boolean cuad = false;
		BmoCustomerAddress bmoCustomerAddress = new BmoCustomerAddress();
		PmCustomerAddress pmCustomerAddress = new PmCustomerAddress(sFParams);
		String sqlCustAddress = " SELECT * FROM customeraddress WHERE cuad_customerid = "
				+ bmoPropertySale.getBmoCustomer().getId() + " ORDER BY cuad_type ASC";
		pmConnCustomer.doFetch(sqlCustAddress);
		if (pmConnCustomer.next())
			cuad = true;
		if (cuad)
			bmoCustomerAddress = (BmoCustomerAddress) pmCustomerAddress.getBy(
					bmoPropertySale.getBmoCustomer().getId(), bmoCustomerAddress.getCustomerId().getName());
		pmConnCustomer.close();

		//Vendedor
		BmoUser bmoSalesUser = new BmoUser();
		PmUser pmSalesUser = new PmUser(sFParams);
		bmoSalesUser = (BmoUser) pmSalesUser.get(bmoPropertySale.getSalesUserId().toInteger());

		DecimalFormat df = new DecimalFormat("###.##");
		String nowMonth = SFServerUtil.nowToString(sFParams, "MM");

		switch (Integer.parseInt(nowMonth)) {
		case 1:
			nowMonth = "Enero";
			break;
		case 2:
			nowMonth = "Febrero";
			break;
		case 3:
			nowMonth = "Marzo";
			break;
		case 4:
			nowMonth = "Abril";
			break;
		case 5:
			nowMonth = "Mayo";
			break;
		case 6:
			nowMonth = "Junio";
			break;
		case 7:
			nowMonth = "Julio";
			break;
		case 8:
			nowMonth = "Agosto";
			break;
		case 9:
			nowMonth = "Septiembre";
			break;
		case 10:
			nowMonth = "Octubre";
			break;
		case 11:
			nowMonth = "Noviembre";
			break;
		case 12:
			nowMonth = "Diciembre";
			break;
		default:
			nowMonth = "n/d";
		}
%>

<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr>
		<td width="5%">
			<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
		</td>
		<td class="reportTitleCell">
			CONTRATO DE PROMESA DE COMPRA VENTA
		</td>
		<td class="reportTitleCell" style="text-align:right;font-size: 9px">
			Clave Doc:<br>FO-07.5.2.1-3<br><%= bmoPropertySale.getCode().toString() %>
		</td>
	</tr>
</table>

<br>
<p align="justify">
	<b>
		CONTRATO DE PROMESA DE COMPRA-VENTA CON RESERVA DE DOMINIO QUE CELEBRAN POR UNA PARTE LA SOCIEDAD MERCANTIL
		DENOMINADA <%= bmoCompany.getLegalName().toString().toUpperCase() %>,
		REPRESENTADA EN ESTE ACTO POR SUS REPRESENTANTE LEGAL, 
		<%= bmoCompany.getLegalRep().toString().toUpperCase() %>
		QUIEN EN LO SUCESIVO SE DENOMINAR&Aacute; EL "VENDEDOR", Y POR OTRA PARTE 
			(<%= bmoCustomer.getCode().toString().toUpperCase() %>)
		    <%= bmoCustomer.getDisplayName().toString().toUpperCase() %>
		    
		A QUIEN EN LO SUCESIVO SE DENOMINAR&Aacute; COMO EL "COMPRADOR", EL CUAL SUJETAN AL TENOR DE LOS SIGUIENTES
		DECLARACIONES Y CL&Aacute;USULAS:
	</b>
</p>



<p align="center" class="contractTitle">
	<b>D E C L A R A C I O N E S:</b>
</p>



<p align="justify" >
	<b>I.- Declara el representante Legal de la sociedad mercantil <b><%= bmoCompany.getLegalName().toString().toUpperCase() %></b>, El <b>"VENDEDOR"</b>:</b>

	<ol type="a" style="text-align:justify;">
	    <li > 
	        Que su representada es una sociedad legalmente constituida de conformidad con la legislaci&oacute;n mexicana de la materia,
	        seg&uacute;n consta en la Escritura P&uacute;blica n&uacute;mero <%= bmoCompany.getDeedNumber().toString().toUpperCase() %> de fecha <%= bmoCompany.getDeedDate().toString().toUpperCase() %>, protocolizada ante la fe del
	        Notario P&uacute;blico n&uacute;mero <%= bmoCompany.getDeedNotaryNumber().toString().toUpperCase() %> misma que se encuentra inscrita en el Registro P&uacute;blico de la
	        Propiedad y de Comercio del Estado de Guanajuato, bajo el folio mercantil electr&oacute;nico n&uacute;mero <%= bmoPropertySale.getBmoProperty().getBmoDevelopmentBlock().getBmoDevelopmentPhase().getElectronicFolio().toString() %>.
	    </li>
	    <br>
	    <li >
	        Que su representada se encuentra en quieta, p&uacute;blica y pac&iacute;fica posesi&oacute;n de los bienes inmuebles materia de esta operaci&oacute;n,
	        ubicados en el municipio de <%= bmoCityDevelopment.getName().toString().toUpperCase() %> 
	        del estado de <%= bmoCityDevelopment.getBmoState().getName().toString().toUpperCase() %>, 
	        en donde actualmente desarrolla un fraccionamiento mixto (residencial y comercial) denominado como 
	        "<%= bmoDevelopment.getName().toString().toUpperCase() %>", 
	        autorizado como tal por las autoridades estatales y municipales competentes. Asimismo, en su calidad de desarrollador Fidecomitente y Fideicomisaria B del Fideicomiso identificado con el n&uacute;mero 
	        <%= bmoPropertySale.getBmoProperty().getBmoDevelopmentBlock().getBmoDevelopmentPhase().getFideicomisoNumber().toString() %>
	        en donde es Fiduciaria la instituci&oacute;n de cr&eacute;dito BANCO DEL BAJIO, SOCIEDAD AN&Oacute;NIMA, INSTITUCI&Oacute;N DE BANCA
	        M&Uacute;LTIPLE, DIVISI&Oacute;N FIDUCIARIA, cuenta con la capacidad y derecho para celebrar contratos de promesas de compraventa y recibir dep&oacute;sitos 
	        en garant&iacute;a y anticipos respecto de las viviendas propias del Fraccionamiento residencial referido, as&iacute; 
	        como para dar instrucciones a la fiduciaria para que se escrituren a favor de los adquirentes que hayan pagado su precio.
	    </li>
	    <br>
	    <li >
	        Que forma parte del Desarrollo Inmobiliario denominado <b><%= bmoDevelopment.getName().toString().toUpperCase() %></b>,
	        sujeto a R&eacute;gimen de Fraccionamiento, el inmueble identificado como
	        <b><%= bmoPropertyModel.getName().toString().toUpperCase() %></b>, ubicado en el Lote
	        <b><%= bmoPropertySale.getBmoProperty().getLot().toString().toUpperCase() %></b>,
	        <b><%= bmoPropertySale.getBmoProperty().getBmoDevelopmentBlock().getName().toString().toUpperCase() %></b>,
	        de la calle <b><%= bmoPropertySale.getBmoProperty().getStreet().toString().toUpperCase() %></b>
	        con N&uacute;mero Oficial <b><%= bmoPropertySale.getBmoProperty().getNumber().toString().toUpperCase() %></b>,
	        en el fraccionamiento
	        <b><%= bmoDevelopment.getName().toString().toUpperCase() %></b>,
	        etapa <b><%= bmoDevelopmentPhase.getName().toString().toUpperCase() %></b>
	        en la ciudad de <b><%= bmoCityDevelopment.getName().toString().toUpperCase() %>, <%= bmoCityDevelopment.getBmoState().getName().toString().toUpperCase() %></b>.
	    </li>
	</ol>
</p>

<p align="justify" >
	<b>II.- Declara el Sr.(a) 
		    <%= bmoCustomer.getDisplayName().toString().toUpperCase() %>,
	</b> El <b>"COMPRADOR"</b>:</b>
	<ol style="text-align:justify;" type="a">
	    <li align="justify" >
	       Ser mexicano mayor de edad, que tiene su domicilio ubicado en 
	       <b><%= bmoCustomerAddress.getStreet().toString().toUpperCase() %></b> 
	       <b><%= bmoCustomerAddress.getNumber().toString().toUpperCase() %></b>,
	       <b><%= bmoCustomerAddress.getNeighborhood().toString().toUpperCase() %></b>, 
	       de la ciudad de <b><%= bmoCustomerAddress.getBmoCity().getName().toString().toUpperCase() %></b>, 
	       con registro federal de contribuyente <b><%= bmoCustomer.getRfc().toString().toUpperCase() %></b>.
	    </li>
	    <li>
	        Que est&aacute; consciente e informado de las disposiciones establecidas en la Ley Federal para la Prevenci&oacute;n e Identificaci&oacute;n de Operaciones con
	        Recursos de Procedencia Il&iacute;cita como a su reglamento y Reglas Generales. Por lo anterior, manifiesta "bajo protesta de decir verdad" y
	        conviene en:
	        <ol type="a">
	            <li>
	                Que el objetivo de la operaci&oacute;n materia del presente contrato se encuentra definida como Actividad Vulnerable.
	            </li>
	            <li>
	                Que la presente operaci&oacute;n la celebra a t&iacute;tulo personal y no en representaci&oacute;n de persona f&iacute;sica o moral alguna; por consiguiente,
	                no representa un beneficiario controlador y que los recursos econ&oacute;micos a que se refiere este contrato son de su exclusiva y
	                personal propiedad.
	            </li>
	            <li>
	                Que los recursos que destinar&aacute; al pago y cumplimiento de las obligaciones dinerarias que asumen acuerdos en este contrato no
	                proceden ni proceder&aacute;n de recursos o actividades il&iacute;citas ni de actividades de delincuencia organizada. Por consiguiente,
	                sabe que los recursos que procedan o tengan origen en &eacute;ste tipo de actividades se encuentran sancionadas por la ley penal como delito.
	            </li>
	            <li>
	                Que la clave del Registro Federal de Contribuyentes antes se&ntilde;alado fue proporcionado "bajo protesta de decir verdad" y
	                que su domicilio fiscal manifestado ante dicho registro es 
	                <b>
	                <%= bmoCustomerAddress.getStreet().toString().toUpperCase() %>
	     	        <%= bmoCustomerAddress.getNumber().toString().toUpperCase() %>,
	     	        <%= bmoCustomerAddress.getNeighborhood().toString().toUpperCase() %>
	                </b>
	                y CURP
	                <%= bmoCustomer.getCurp().toString().toUpperCase() %>.
	                
	            </li>
	            <li>
	                De acuerdo con la Ley, como cliente de Fraccionamiento 
	                <%= bmoDevelopment.getName().toString()%> (<%= bmoCompany.getLegalName().toString()%>) 
	                <!-- Residencial El Cielo (Inmobiliaria Saregi SA de CV) --> por el presente autoriza a dicha empresa, 
	                sus funcionarios y representantes legales a reportar y avisar de la presente operaci&oacute;n a las autoridades
	                competentes cuando as&iacute; lo exija la Ley y de acuerdo con lo que ella misma establece, as&iacute; como a proporcionar la informaci&oacute;n y
	                datos personales de identificaci&oacute;n y actividades que "bajo protesta de decir verdad" ha proporcionado a dicha empresa de
	                acuerdo con el Anexo 3 (aqui va el anexo de lavado de dinero) del Acuerdo 2/2013 por el que se emiten Reglas de Car&aacute;cter General
	                a que se refiere la Ley y la "Resoluci&oacute;n por la que se Expiden los Formatos Oficiales de los Avisos e Informes que deben presentar
	                quienes realicen operaciones vulnerables" publicados en el Diario Oficial de la Federaci&oacute;n el d&iacute;a 23 de Agosto del 2013 y 30 de
	                Agosto de 2013, respectivamente. Dicho Anexo 3 de cualquier forma queda en resguardo de la empresa para exclusivos de los
	                fines autorizados.
	            </li>
	            <li>
	                 Adem&aacute;s, ha proporcionado a la empresa copia de su identificaci&oacute;n personal y de identidad Anexa en copia simple a &eacute;ste contrato
	            </li>
	            <li>
	                Que su actividad u ocupaci&oacute;n es: <%= bmoCustomer.getPosition().toString().toUpperCase() %> activo, sin obligaciones fiscales, de acuerdo con las obligaciones de alta,
	                o inscripci&oacute;n ante el Registro Federal de Contribuyentes, cuya copia se agrega al presente contrato para su custodia y
	                resguardo por el plazo de 5 a&ntilde;os.
	            </li>
	            <li>
	                Las aportaciones de los dep&oacute;sitos en garant&iacute;a los har&aacute; en la cuenta bancaria de la empresa con cualquier forma permitida por la ley.
	                Cuando el importe de los mismos supere 8,025 salarios m&iacute;nimos vigentes en el DF, autoriza a la empresa a presentar los avisos que
	                previene la Ley Referida. As&iacute; mismo se da por enterado y se compromete a cumplir con la prohibici&oacute;n de realizar dep&oacute;sitos en
	                efectivo de la cuenta bancaria de la empresa por un monto igual o mayor a 8,025 salarios m&iacute;nimos vigentes en el Distrito Federal.
	            </li>
	            <li>
	                Otorgar&aacute; el Notario P&uacute;blico ante quien se celebre el contrato definitivo de compra venta a que se obtenga de acuerdo con este contrato,
	                toda la informaci&oacute;n y facilidades para que cumpla a su vez con lo que previene el art&iacute;culo 23 y
	                dem&aacute;s aplicables a la ley y su reglamentos referidos.
	            </li>
	        </ol>
	    </li>
	</ol>
</p>

<p align="justify" >
<b>III.- Declaran ambas</b> partes que se reconocen mutuamente su personalidad y que es de su intenci&oacute;n celebrar el
presente contrato de promesa de compra-venta con reserva de dominio y condici&oacute;n suspensiva en base a las siguientes.

</p>

<p align="center" class="contractTitle">
	<b>C L &Aacute; U S U L A S:</b>
</p>

<p align="justify" >
	<b>PRIMERA.- UBICACI&Oacute;N DEL INMUEBLE</b> - El <b>"VENDEDOR"</b> se compromete a vender y el <b>"COMPRADOR"</b> se compromete a comprar en forma real y definitiva la
	vivienda tipo <b><%= bmoPropertyModel.getName().toString().toUpperCase() %></b> 
	a construir que tendr&aacute; su ubicaci&oacute;n en la
	<b><%= bmoPropertySale.getBmoProperty().getBmoDevelopmentBlock().getName().toString().toUpperCase() %>, 
	VIVIENDA <%= bmoPropertySale.getBmoProperty().getLot().toString().toUpperCase() %></b> lote/fracci&oacute;n de la calle 
	<b><%= bmoPropertySale.getBmoProperty().getStreet().toString().toUpperCase() %></b> con n&uacute;mero oficial 
	<b><%= bmoPropertySale.getBmoProperty().getNumber().toString().toUpperCase() %></b>
	en el fraccionamiento <b><%= bmoDevelopment.getName().toString().toUpperCase() %></b>,
	etapa <b><%= bmoDevelopmentPhase.getName().toString().toUpperCase() %></b>
	en la ciudad de 
	<b><%= bmoCityDevelopment.getName().toString().toUpperCase() %>, <%= bmoCityDevelopment.getBmoState().getName().toString().toUpperCase() %></b>.
</p>

<p align="justify" >
	<b> SEGUNDA.- PRECIO PACTADO</b>  - El <b>"COMPRADOR"</b>  conviene en pagar al <b>"VENDEDOR"</b> y que se fija de com&uacute;n acuerdo como precio
	de la vivienda la cantidad correspondiente a <b><%= SFServerUtil.formatCurrency(bmoOrderProperty.getAmount().toDouble())%> (<%= amountByWord.getMoneyAmountByWord(bmoOrderProperty.getAmount().toDouble()).toUpperCase() %>)</b> 
	cantidad que estar&aacute; sujeta a las adecuaciones de paquetes adicionales y descuentos que se describen a continuaci&oacute;n:
</p>
<p align="justify" >

    <%
	//Sacar adicionales-paquetes-accesorios Positivos
	String sqlVentaPedidosExtra = " SELECT * from orderpropertymodelextras " +
									" LEFT JOIN propertymodelextras ON(prmx_propertymodelextraid = orpx_propertymodelextraid) " +
									" WHERE orpx_orderpropertymodelextraid > 0 " +
									" AND orpx_orderid = " + bmoPropertySale.getOrderId().toInteger();

    boolean menu = true;
    double totalPrecioPactadoPosi = 0, totalPrecioPactadoPositivos = 0, totalPrecioPactadoNegativos = 0;
    int countPosi = 0;
    PmConn pmConnAdicionalesPositivos= new PmConn(sFParams);
    pmConnAdicionalesPositivos.open();
    pmConnAdicionalesPositivos.disableAutoCommit();
							
	pmConnAdicionalesPositivos.doFetch(sqlVentaPedidosExtra);

        while(pmConnAdicionalesPositivos.next()) {
        	//if(!(pmConnAdicionalesPositivos.getInt("prmx_propertymodelextraid") > 0))
            	totalPrecioPactadoPositivos = pmConnAdicionalesPositivos.getDouble("orpx_price") * pmConnAdicionalesPositivos.getDouble("orpx_quantity");
        	//else
        		//totalPrecioPactadoPositivos = pmConnAdicionalesPositivos.getDouble("prmx_price") * pmConnAdicionalesPositivos.getDouble("orpx_quantity");
            
        	if(totalPrecioPactadoPositivos >= 0 ){
            	
            	//if(!(pmConnAdicionalesPositivos.getInt("prmx_propertymodelextraid") > 0))
                	totalPrecioPactadoPosi += pmConnAdicionalesPositivos.getDouble("orpx_price") * pmConnAdicionalesPositivos.getDouble("orpx_quantity");
            	//else
            		//totalPrecioPactadoPosi += pmConnAdicionalesPositivos.getDouble("prmx_price") * pmConnAdicionalesPositivos.getDouble("orpx_quantity");

                if(menu){
                    menu = false;
    %>
				    <table width="90%" align="center" border="0" cellpadding="0" cellspacing="0" style="font-size: 12px">
                    	<caption><b>Aditivas</b></caption>
						<tr>
						    <th class="reportHeaderCell" width="75%">Paquetes Adicionales</td>
						    <th class="reportHeaderCell" width="5%">Cantidad</td>
						    <th class="reportHeaderCellRight" width="10%" style="padding-right: 9px">Precio</td>
						</tr>
                <% } %>
	             <tr>
	             	<td class="reportCellEven" align = "left">
			            <%= pmConnAdicionalesPositivos.getString("prmx_name") %>
			            <% if (!pmConnAdicionalesPositivos.getString("prmx_description").equals("")) { %>
		             	<br> &nbsp;<%= pmConnAdicionalesPositivos.getString("prmx_description") %>
		             	<% } %>
	                    <% if (!pmConnAdicionalesPositivos.getString("orpx_comments").equals("")) { %>
		                    <br>&nbsp;&nbsp;
		                    <%= pmConnAdicionalesPositivos.getString("orpx_comments") %>
	                    <% } %>
	                 </td>
	                 <td class="reportCellEven" align = "left">
	                    &nbsp;<%= pmConnAdicionalesPositivos.getInt("orpx_quantity")%>
	                 </td>
	                 <td class="reportCellEven" align = "right">
	                 <%
	                 	double precioAdicionalesPositivos = 0;
	                 	//if(!(pmConnAdicionalesPositivos.getInt("prmx_propertymodelextraid") > 0))
	                 	precioAdicionalesPositivos = pmConnAdicionalesPositivos.getDouble("orpx_price") * pmConnAdicionalesPositivos.getDouble("orpx_quantity");
	             		//else
	             			//precioAdiocionalesPositivos = pmConnAdicionalesPositivos.getDouble("prmx_price") * pmConnAdicionalesPositivos.getDouble("orpx_quantity");
	                 %>
	                    &nbsp;<%= SFServerUtil.formatCurrency(precioAdicionalesPositivos) %>
	                 </td>
	             </tr>
    <%      countPosi++;
        	}
        } //Fin pmConnAdicionalesPositivos
    	if (countPosi > 0) {
	        if(totalPrecioPactadoPosi >= 0){
    %>
		        <tr>
			    	<td class="documenLabel" colspan="" align="left">&nbsp;</td>
		            <td class="reportCellEven" align="left"><b>Total&nbsp;</b></td>
		            <td class="reportCellEven" align="right"><b><%= SFServerUtil.formatCurrency(totalPrecioPactadoPosi)%></b></td>
		        </tr>
	 <% 
	        } 
        } %>
    </table>
    <% pmConnAdicionalesPositivos.close();%>
<br>
<%
	menu = true;
	double totalPrecioPactadoNegat = 0;
	int countNegat = 0;
	PmConn pmConnAdicionalesNegativos= new PmConn(sFParams);
	pmConnAdicionalesNegativos.open();
	pmConnAdicionalesNegativos.disableAutoCommit();
								
	//Sacar adicionales-paquetes-accesorios Negativos
	pmConnAdicionalesNegativos.doFetch(sqlVentaPedidosExtra);
	
	    while(pmConnAdicionalesNegativos.next()){
	    	//if(!(pmConnAdicionalesNegativos.getInt("prmx_propertymodelextraid") > 0))
	        	totalPrecioPactadoNegativos = pmConnAdicionalesNegativos.getDouble("orpx_price") * pmConnAdicionalesNegativos.getDouble("orpx_quantity");
	    	//else
	    		//totalPrecioPactadoNegativos = pmConnAdicionalesNegativos.getDouble("prmx_price") * pmConnAdicionalesNegativos.getDouble("orpx_quantity");
	        
	    	if(totalPrecioPactadoNegativos < 0 ){
	        	
	        	//if(!(pmConnAdicionalesNegativos.getInt("prmx_propertymodelextraid") > 0))
	            	totalPrecioPactadoNegat += pmConnAdicionalesNegativos.getDouble("orpx_price") * pmConnAdicionalesNegativos.getDouble("orpx_quantity");
	        	//else
	        		//totalPrecioPactadoNegat += pmConnAdicionalesNegativos.getDouble("prmx_price") * pmConnAdicionalesNegativos.getDouble("orpx_quantity");
	
	            if(menu){
	                menu = false;
	%>
				<table width="90%" align="center" border="0" cellpadding="0" cellspacing="0" style="font-size: 12px">
					<caption><b>Descuentos</b></caption>
					<tr>
					    <th class="reportHeaderCell" width="75%">Paquetes Adicionales</td>
					    <th class="reportHeaderCell" width="5%">Cantidad</td>
					    <th class="reportHeaderCellRight" width="10%" style="padding-right: 9px">Precio</td>
					</tr>
					
	            <% } %>
	             <tr>
	             	<td class="reportCellEven" align = "left">
			            <%= pmConnAdicionalesNegativos.getString("prmx_name") %>
			            <% if (!pmConnAdicionalesNegativos.getString("prmx_description").equals("")) { %>
		             		<br> &nbsp;<%= pmConnAdicionalesNegativos.getString("prmx_description") %>
		             	<% } %>
		             	<% if (!pmConnAdicionalesNegativos.getString("orpx_comments").equals("")) { %>
			             	<br>&nbsp;&nbsp;
			             	<%= pmConnAdicionalesNegativos.getString("orpx_comments") %>
		             	<% } %>
	                 </td>
	                 <td class="reportCellEven" align = "left">
	                    &nbsp;<%= pmConnAdicionalesNegativos.getInt("orpx_quantity")%>
	                 </td>
	                 <td class="reportCellEven" align = "right">
	                 <%
	                 	double precioAdiocionalesNegativos = 0;
	                 	//if(!(pmConnAdicionalesNegativos.getInt("prmx_propertymodelextraid") > 0))
	                 	precioAdiocionalesNegativos = pmConnAdicionalesNegativos.getDouble("orpx_price") * pmConnAdicionalesNegativos.getDouble("orpx_quantity");
	             		//else
	             			//precioAdiocionalesNegativos = pmConnAdicionalesNegativos.getDouble("prmx_price") * pmConnAdicionalesNegativos.getDouble("orpx_quantity");
	                 %>
	                    &nbsp;<%= SFServerUtil.formatCurrency(precioAdiocionalesNegativos) %>
	                 </td>
	             </tr>
	<%      countNegat++;
	    	}
	    } //Fin pmConnAdicionalesNegativos
	    if (countNegat > 0) {
		    if(totalPrecioPactadoNegat <= 0){
	    %>
			    <tr>
		        	<td class="documenLabel" colspan="" align="right">&nbsp;</td>
			        <td class="reportCellEven" colspan="" align="left"><b>Total:&nbsp;</b></td>
			        <td class="reportCellEven" align="right"><b><%= SFServerUtil.formatCurrency(totalPrecioPactadoNegat)%></b></td>
			    </tr>
     <% 
		    } 
	    }	%>
	</table>
	<% pmConnAdicionalesNegativos.close();%>   
	<br>
	<table width="90%" align="center" border="0" cellpadding="0" cellspacing="0" style="font-size: 12px">
		<caption><b>TOTAL</b></caption>
		<tr>
		    <th class="reportHeaderCell" width="30%">Aditivas</td>
		    <th class="reportHeaderCellCenter" width="30%">Descuentos</td>
		    <th class="reportHeaderCellRight" width="30%" style="padding-right: 9px">TOTAL</td>
		</tr>
			<td class="reportCellEven" align = "left">
				&nbsp;<%= SFServerUtil.formatCurrency(totalPrecioPactadoPosi) %>
			</td>
			<td class="reportCellEven" align = "center">
				&nbsp;<%= SFServerUtil.formatCurrency(totalPrecioPactadoNegat) %>
			</td>
			<td class="reportCellEven" align = "right">
				&nbsp;<%= SFServerUtil.formatCurrency(totalPrecioPactadoPosi + totalPrecioPactadoNegat) %>
			</td>
		</tr>
    </table>
</p>
<%
	double totalPrecioPaquetes = 0;
	
	totalPrecioPaquetes = totalPrecioPactadoPosi + totalPrecioPactadoNegat;
	
	double precioReal = 0;
	precioReal = bmoOrderProperty.getAmount().toDouble() + totalPrecioPaquetes;
%>
<p align="justify">
	Resultando un valor real de la operaci&oacute;n correspondiente a
	<b><%= SFServerUtil.formatCurrency(precioReal)%> (<%= amountByWord.getMoneyAmountByWord(precioReal).toUpperCase() %>)</b>
	cantidad que deber&aacute; estar cubierta a m&aacute;s tardar el d&iacute;a de la firma de las escrituras de compra-venta ante
	NOTARIO P&Uacute;BLICO que para &eacute;ste efecto designe el <b>"VENDEDOR"</b>;
	los gastos que se deriven del aval&uacute;o de la vivienda as&iacute; como los correspondientes a la escrituraci&oacute;n correr&aacute;n por cuenta
	del <b>"COMPRADOR"</b>. A la firma del presente contrato el valor de la vivienda es 
	<b><%= SFServerUtil.formatCurrency(precioReal)%> (<%= amountByWord.getMoneyAmountByWord(precioReal).toUpperCase() %>).</b>
</p>
<%
	PmConn pmConnRaccW = new PmConn(sFParams);
    String sqlRaccW;
    sqlRaccW= " SELECT racc_raccountid, racc_invoiceno, racc_total, racc_duedate, payt_name " +
    			" FROM raccounts " + 
	    		" LEFT JOIN raccounttypes ON(ract_raccounttypeid = racc_raccounttypeid) " +
	    		" LEFT JOIN paymenttypes ON(payt_paymenttypeid = racc_paymenttypeid) " +
	    		" WHERE racc_orderid = " + bmoPropertySale.getOrderId().toInteger() + 
	    		" AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" +
	    		" ORDER BY racc_duedate ASC, racc_invoiceno ASC" ;
    
    System.out.println("sqlRaccW: "+sqlRaccW);
    pmConnRaccW.open();
    pmConnRaccW.doFetch(sqlRaccW);
%>
<table width="90%" align="center" border="0" cellpadding="0" cellspacing="0" style="font-size: 12px">
	<tr>
	    <th class="reportHeaderCell" >Documento</th>
	    <th class="reportHeaderCell">M&eacute;todo de pago</th>
	    <th class="reportHeaderCell">Fecha l&iacute;mite de pago</th>
	    <th class="reportHeaderCellRight" width="10%" style="padding-right: 9px">Monto</td>
	</tr>
	
	<%
		int racc = 1;
		while (pmConnRaccW.next()) {
			BmoRaccount bmoRaccount = new BmoRaccount();
			
			PmRaccount pmRaccount = new PmRaccount(sFParams);
	    	bmoRaccount = (BmoRaccount)pmRaccount.get(pmConnRaccW.getInt("racc_raccountid"));
	
			if(bmoRaccount.getBmoRaccountType().getCategory().toChar() == BmoRaccountType.CATEGORY_ORDER){
				%>
			 		<tr>
			 			<td class="reportCellEven" align = "left">
				        	&nbsp;<%= racc%>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%= pmConnRaccW.getString("racc_invoiceno")%>
			    		</td>
			    		<td class="reportCellEven" align = "left">
				        	<%= pmConnRaccW.getString("payt_name")%>
			    		</td>
			    		<td class="reportCellEven" align = "left">
		        			&nbsp;<%= FlexUtil.dateToLongDate(sFParams, pmConnRaccW.getString("racc_duedate"))%>
			    		</td>
			    		<td class="reportCellEven" align = "right">
				        	&nbsp;<%= SFServerUtil.formatCurrency(pmConnRaccW.getDouble("racc_total"))%>
			    		</td>
			   <%
			   	racc += 1;
			}
		} 
		pmConnRaccW.close();
	       %>			
	</tr>
</table>

<ol type="1" style="text-align:justify;">
	<li>
	    La diferencia de esta operaci&oacute;n podr&aacute; ser pagada al <b>"VENDEDOR"</b> de contado o a trav&eacute;s 
	    de un cr&eacute;dito mediante alguna instituci&oacute;n financiera, mediante autofinanciamiento y/o cr&eacute;dito directo:
	</li>
	<ol type="a" >
	    <li>
	        Si la diferencia de esta operaci&oacute;n fuese pagada al <b>"VENDEDOR"</b> de contado el pago deber&aacute; ser por la cantidad total misma que
	        deber&aacute; estar cubierta a m&aacute;s tardar el d&iacute;a de la firma de la escritura de compra-venta definitiva ante el NOTARIO que para este
	        efecto designe el <b>"VENDEDOR"</b>. Si la diferencia de esta operaci&oacute;n fuese pagada a trav&eacute;s de un cr&eacute;dito hipotecario otorgado al
	        <b>"COMPRADOR"</b> por alguna instituci&oacute;n crediticia, deber&aacute; ser pagada al <b>"VENDEDOR"</b> a m&aacute;s tardar el d&iacute;a de la firma de escritura ante
	        el notario que designe la instituci&oacute;n crediticia.
	    </li>
	    <li>
	        Si el monto del cr&eacute;dito otorgado por la instituci&oacute;n financiera al "COMPRADOR", en conjunto con el enganche, fuera menor al precio del
	        inmueble , el <b>"COMPRADOR"</b> conviene en pagar la diferencia,
	        m&aacute;s intereses</b> al <b>"VENDEDOR"</b> en la forma pactada y avalada por pagar&eacute;s, descritos en el Anexo 2 del presente instrumento, diferencia que puede
	        cambiar en el momento de la firma de las escrituras definitivas, derivado del monto del cr&eacute;dito otorgado y el valor pacto del lote.
	        En el supuesto que el <b>"COMPRADOR"</b> no pague al <b>"VENDEDOR"</b> los montos pactados en los pagar&eacute;s antes o en la fecha de vencimiento de los
	        mismos, autoriza expresamente el <b>"COMPRADOR"</b> al <b>"VENDEDOR"</b> a cancelar la operaci&oacute;n y aplicar la pena convencional establecida en el
	        presente instrumento.
	    </li>
	    <li>
	        Si la opci&oacute;n fue la de cr&eacute;dito directo, se deber&aacute; firmar la corrida financiera anexa (ANEXO 4) correspondiente que incluir&aacute;
	        los pagos mensuales netos en el per&iacute;odo de tiempo estipulado en la misma.
	    </li>
	</ol>
</ol>


<!--
<ol type="1" >
	<li align="justify">
		El <b>"VENDEDOR"</b> recibir&aacute; como enganche por parte del <b>"COMPRADOR"</b> la cantidad de <b>XXXX<%//= SFServerUtil.formatCurrency(boAllocation.getDownPayment().toDouble()) %> (PENDIENTE CANTIDAD<%//= amountByWord.getMoneyAmountByWord(boAllocation.getDownPayment().toDouble()).toUpperCase() %> )</b>
	    Que servir&aacute; como apartado de la vivienda antes mencionada.
	</li>
	<li>
	    La diferencia de esta operaci&oacute;n podr&aacute; ser pagada al el <b>"VENDEDOR"</b> de contado o a trav&eacute;s de un cr&eacute;dito hipotecario.
	</li>
	<li>
	    Si la diferencia de esta operaci&oacute;n fuese pagada al <b>"VENDEDOR"</b> de contado el pago deber&aacute; ser por la cantidad correspondiente antes mencionada
	    misma que deber&aacute; estar cubierta a m&aacute;s tardar el d&iacute;a de la firma de la escritura de compra-venta definitiva ante el NOTARIO que para este
	    efecto designe el <b>"VENDEDOR"</b>. Los gastos que se deriven tanto del aval&uacute;o como de la escrituraci&oacute;n correr&aacute;n por cuenta del <b>"COMPRADOR"</b>.
	</li>
	<li>
	    Si la diferencia de esta operaci&oacute;n fuese pagada a trav&eacute;s de un cr&eacute;dito hipotecario otorgado al <b>"COMPRADOR"</b> por alguna instituci&oacute;n crediticia,
	    deber&aacute; ser pagada al <b>"VENDEDOR"</b> a m&aacute;s tardar el d&iacute;a de la firma de escritura ante el notario que designe la instituci&oacute;n crediticia.
	    Los gastos que se deriven tanto del aval&uacute;o como de la escrituraci&oacute;n correr&aacute;n por cuenta del <b>"COMPRADOR"</b>.
	</li>
	<li>
	    Si el monto del cr&eacute;dito otorgado al <b>"COMPRADOR"</b>, en conjunto con el enganche, fuera menor al precio de la vivienda,
	    el <b>"COMPRADOR"</b> conviene en pagar la diferencia resultante de 
	    <b>XXXX<%//= SFServerUtil.formatCurrency(precioReal - boAllocation.getCreditAmount().toDouble() - boAllocation.getDownPayment().toDouble()) %></b> 
	    (<b>XXXX<%//= amountByWord.getMoneyAmountByWord(boAllocation.getTotalPrice().toDouble() - boAllocation.getCreditAmount().toDouble() - boAllocation.getDownPayment().toDouble()).toUpperCase()%></b>) 
	    al <b>"VENDEDOR"</b> en la forma pactada y avalada por pagar&eacute;s, descritos en el Anexo 2 del presente instrumento, diferencia que puede cambiar en el momento de la firma de
	    las escrituras definitivas, derivado del monto del cr&eacute;dito otorgado y el valor pactado en salarios m&iacute;nimos mensuales del Distrito Federal de la
	    vivienda. En el supuesto que el <b>"COMPRADOR"</b> no pague al <b>"VENDEDOR"</b> los montos pactados en los pagar&eacute;s antes o en la fecha de
	    vencimiento de los mismos, autoriza expresamente el <b>"COMPRADOR"</b> al <b>"VENDEDOR"</b> a reubicarse a otra vivienda, designada por el <b>"VENDEDOR"</b>,
	    con la mismas caracter&iacute;sticas y el mismo precio de la vivienda contratada originalmente. En el momento que el <b>"COMPRADOR"</b> est&eacute; al corriente
	    en los pagos pactados, podr&aacute; reubicarse a una vivienda de su elecci&oacute;n que se encuentre disponible para su venta.
	</li>
</ol>
-->

<p align="justify" >
	<b>TERCERA.- LUGAR Y FORMA DE PAGOS</b> - 
	
	Acepta el <b>"COMPRADOR"</b> pagar al <b>"VENDEDOR"</b> la diferencia que resulte a su cargo, de acuerdo a lo que se menciona en el punto 1 (uno) inciso b) 
	de la cl&aacute;usula Segunda de este contrato; la cual se har&aacute; en el lugar y las fechas establecidas en los pagar&eacute;s y/o cuando se lo
	indique el <b>"VENDEDOR"</b> previo a la firma de escrituras.
	<p>
	Los pagos los realizara el <b>"COMPRADOR"</b> en el:
	<table width="100%" align="left" border="0" cellpadding="0" cellspacing="0" style="font-size: 12px">
		<tr>
				<td class="reportCellEven" align="left">
					Nombre del Banco:
				</td>
				<td class="reportCellEven" align="left">
					<b><%= bmoBankAccount.getBankName().toString()%></b>
				</td>
		</tr>
		<tr>
				<td class="reportCellEven" align="left" width="20%">
					Empresa Inmobiliaria:
				</td>
				<td class="reportCellEven" align="left">
					<b><%= bmoBankAccount.getBmoCompany().getName().toString()%></b>
				</td>
		</tr>
		<tr>
				<td class="reportCellEven" align="left">
					Cuenta Bancaria:
				</td>
				<td class="reportCellEven" align="left">
					<b><%= bmoBankAccount.getAccountNo().toString()%></b>
				</td>
		</tr>
		<tr>
				<td class="reportCellEven" align="left">
					CLABE Interbancaria:
				</td>
				<td class="reportCellEven" align="left">
					<b><%= bmoBankAccount.getClabe().toString()%></b>
				</td>
		</tr>
		<tr>
				<td align="left" colspan="2">
					<br>
					Para el caso de transferir  dep&oacute;sito con Cheque, entregar copia del mismo y mencionar el:
				</td>
		</tr>
		<tr>
				<td class="reportCellEven" align="left">
					Nombre del titular de la Cuenta Bancaria:
				</td>
				<td class="reportCellEven" align="left">
					<b>(Anotar el nombre del Titular de la Cuenta Bancaria)
					</b>
				</td>
		</tr>
		<tr>
				<td align="left" colspan="2">
					<br>
					La ficha de dep&oacute;sito en efectivo, con cheque o transferencia; deber&aacute; contener sin excepci&oacute;n los
					siguientes datos ya que son indispensables para identificar quien hizo el dep&oacute;sito y son:
				</td>
		</tr>
		<tr>
				<td class="reportCellEven" align="left">
					Nombre del Cliente:
				</td>
				<td class="reportCellEven" align="left">
					<b><%= bmoCustomer.getDisplayName().toString()%></b>
				</td>
			</tr>
			<tr>
				<td class="reportCellEven" align="left">
					Referencia:
				</td>
				<td class="reportCellEven" align="left">
					<b><%= bmoCustomer.getCode().toString()%></b>
				</td>
		</tr>
		<tr>
				<td class="reportCellEven" align="left">
					Servicio:
				</td>
				<td class="reportCellEven" align="left">
					<b>PAGO DE COMPRA DE INMUEBLE</b>
				</td>
		</tr>
		<tr>
				<td align="left" colspan="2">
					<br>
					Una vez realizado su dep&oacute;sito debera informar al promotor para que lo haga de conocimiento a la Empresa y pueda ser
					considerado el pago de su adeudo.
				</td>
		</tr>
		<tr><td colspan="2">&nbsp;</td></tr>
	</table>	
</p>

<p align="justify" >
	<b>CUARTA.- INTER&Eacute;S POR MORA</b> - Convienen ambas partes de este contrato de promesa de compra-venta que el pago no oportuno de las cantidades pactadas en el
	presente contrato de promesa de compra-venta generar&aacute; un inter&eacute;s mensual del <b>3%</b> (tres por ciento), que el <b>"COMPRADOR"</b> pagar&aacute; al <b>"VENDEDOR"</b>,
	durante el tiempo que dure la mora. Adicionando a lo anterior, el <b>"COMPRADOR"</b> autoriza expresamente al <b>"VENDEDOR"</b> a reubicar a otra vivienda,
	designada por el <b>"VENDEDOR"</b>, con las mismas caracter&iacute;sticas y el mismo precio de la vivienda contratada originalmente.
</p>

<!--
<p align="justify" >
	<b>CUARTA.- DEVOLUCI&Oacute;N POR NO OTORGAMIENTO DE CR&Eacute;DITO</b> - En el supuesto de que el cr&eacute;dito fuere negado al <b>"COMPRADOR"</b> por la instituci&oacute;n crediticia,
	se proceder&aacute; a la cancelaci&oacute;n del presente contrato de promesa de compra-venta sin ninguna responsabilidad para el <b>"VENDEDOR"</b>,
	el cual devolver&aacute; al <b>"COMPRADOR"</b> los pagos que haya efectuado al <b>"VENDEDOR"</b> en un plazo de <b>30</b> treinta d&iacute;as h&aacute;biles a partir de la
	fecha en que se conozca que el cr&eacute;dito no fue concedido al <b>"COMPRADOR"</b> por la instituci&oacute;n crediticia.
</p>
-->

<p align="justify" >
	<b>QUINTA.- ENTREGA DE DOCUMENTACI&Oacute;N</b> - En el supuesto que el <b>"COMPRADOR"</b> no entregue con oportunidad su documentaci&oacute;n completa y
	fidedigna en original y copia, se proceder&aacute; a la cancelaci&oacute;n inmediata del presente contrato de promesa de compra-venta sin
	responsabilidad alguna para el <b>"VENDEDOR"</b> y aplicando una penalizaci&oacute;n de <b>$5,000.00</b> (CINCO MIL PESOS 00/100 MONEDA NACIONAL) por concepto de
	gastos administrativos efectuados por el <b>"VENDEDOR"</b>, haciendo devoluci&oacute;n al <b>"COMPRADOR"</b> las cantidades sobrantes que hubiere entregado
	el <b>"COMPRADOR"</b> al <b>"VENDEDOR"</b> descontando de estas la penalizaci&oacute;n correspondiente.
</p>

<p align="justify" >
	<b>SEXTA.- NEGATIVA CREDITICIA</b> - En el supuesto de que al <b>"COMPRADOR"</b> se le niegue el otorgamiento del cr&eacute;dito hipotecario, estar&aacute; en
	posibilidad de liquidar con sus propios recursos el valor pactado de la vivienda dentro de los siguientes treinta d&iacute;as naturales a
	partir de la negativa; de no cubrirse el saldo se proceder&aacute; a la cancelaci&oacute;n del presente contrato de promesa de compra-venta, por parte
	del <b>"VENDEDOR"</b> al <b>"COMPRADOR"</b>, sin ninguna responsabilidad para el <b>"VENDEDOR"</b>, oblig&aacute;ndose el <b>"VENDEDOR"</b> a devolver al <b>"COMPRADOR"</b>,
	las cantidades que &eacute;ste hubiere entregado al <b>"VENDEDOR"</b> por concepto de anticipos o enganches, reteniendo &uacute;nicamente los gastos que
	hubiere efectuado el <b>"VENDEDOR"</b> por concepto de aval&uacute;os, estudios socioecon&oacute;micos, o alg&uacute;n otro gasto con cargo al <b>"COMPRADOR"</b>.
</p>

<p align="justify" >
	<b>S&Eacute;PTIMA.- MORA EN LOS PAGOS</b> - Las partes acuerdan que si el <b>"COMPRADOR"</b> llegara a retrasarse en m&aacute;s de dos de los pagos pactados,
	el <b>"VENDEDOR"</b> podr&aacute; rescindir inmediata y unilateralmente el presente contrato de promesa de compra-venta sin responsabilidad alguna,
	y vender nuevamente el inmueble descrito en el Anexo 1 que acompa&ntilde;a el presente contrato de promesa de compra-venta, o a decisi&oacute;n del
	<b>"VENDEDOR"</b> proceder a reubicar a otra vivienda, designada por el <b>"VENDEDOR"</b>, con las mismas caracter&iacute;sticas y el mismo precio de la vivienda
	contratada originalmente.
</p>

<p align="justify" >
	<b>OCTAVA.- CARACTER&Iacute;STICAS DEL INMUEBLE</b> - El comprador est&aacute; enterado y consciente de las caracter&iacute;sticas,
	materiales de construcci&oacute;n, dise&ntilde;o, dimensiones, superficies y ubicaci&oacute;n de la vivienda materia de este contrato
	de promesa de compra-venta, por lo que firma de enterado y aceptaci&oacute;n en el Anexo 1 (uno) del presente contrato de promesa de compra-venta,
	anexo que se tiene aqu&iacute; por reproducido como si a la letra se insertase.
</p>


<p align="justify" >
	<b>NOVENA.- CANCELACI&Oacute;N</b>
	- En el caso de que "COMPRADOR" llegara a cancelar el presente contrato de promesa de compra-venta, por cualquier causa, 
	deber&aacute; dar aviso por escrito al "VENDEDOR", y acepta el "COMPRADOR" pagar al "VENDEDOR" por concepto de da&ntilde;os y perjuicios 
	producidos por el incumplimiento de la operaci&oacute;n de promesa de compra-venta y dentro de los siguientes 30 treinta d&iacute;as, 
	una pena convencional de un 5% (cinco porciento) del valor total de la operaci&oacute;n de promesa de compra-venta pactada en el presente contrato.
	
	<!-- <b> NOVENA.- CANCELACI&Oacute;N UNILATERAL</b> - En el caso de que <b>"COMPRADOR"</b> llegara a cancelar el presente contrato de promesa de compra-venta en
	forma unilateral, deber&aacute; dar aviso por escrito al <b>"VENDEDOR"</b>, y acepta el <b>"COMPRADOR"</b> pagar al <b>"VENDEDOR"</b> por concepto de da&ntilde;os y
	perjuicios producidos por el incumplimiento de la operaci&oacute;n de promesa de compra-venta y dentro de los siguientes 30 treinta d&iacute;as, una
	pena convencional de un 5% (cinco porciento) del valor total de la operaci&oacute;n de promesa de compra-venta pactada en el presente contrato.
	-->
</p>
	
<p align="justify" >
	<b>D&Eacute;CIMA.- TRANSMISI&Oacute;N DE PROPIEDAD</b> - El <b>"VENDEDOR"</b> se obliga a transmitir al <b>"COMPRADOR"</b> el inmueble materia del presente contrato de promesa de compra-venta, 
	libre de todo gravamen, sin limitaciones de dominio y al corriente en el pago de sus contribuciones, con todas sus especificaciones as&iacute; como los 
	planos y garant&iacute;as que correspondan a la vivienda de que se trate, la cual se detalla en la cl&aacute;usula PRIMERA del presente contrato de promesa de compra venta. 
	En todo caso la vivienda ser&aacute; entregada al <b>"COMPRADOR"</b> dentro de los 21 veinti&uacute;n d&iacute;as posteriores a la fecha en que el <b>"VENDEDOR"</b> reciba el pago total de la vivienda, 
	siempre y cuando el <b>"COMPRADOR"</b> o la entidad otorgante del cr&eacute;dito la haya liquidado o abonado a la cuenta bancaria del <b>"VENDEDOR"</b> y se haya formalizado la 
	Escritura definitiva de Compra Venta. El <b>"COMPRADOR"</b> est&aacute; consciente de que los gastos de originaci&oacute;n del cr&eacute;dito, en el caso de que les sea otorgado por la 
	entidad financiera, para la adquisici&oacute;n de la vivienda objeto del presente contrato, gastos que corresponden a: escrituraci&oacute;n, estudio socioecon&oacute;mico, 
	comisi&oacute;n por apertura de cr&eacute;dito, aval&uacute;o, bur&oacute; de cr&eacute;dito y/o la primera mensualidad, son por su cuenta y ser&aacute;n descontados del monto de cr&eacute;dito otorgado.
</p>


<p align="justify" >
	<b>D&Eacute;CIMA PRIMERA.- DE LA NORMATIVIDAD DEL DESARROLLO</b>.- A fin de salvaguardar el orden y la calidad en el Desarrollo Inmobiliario
	Residencial denominado <b><%= bmoDevelopment.getName().toString().toUpperCase() %></b>, el <b>"COMPRADOR"</b> desde este momento se compromete a cumplir con todas y
	cada una de las disposiciones, ordenamientos, cuotas y sanciones que emanan y se estipulen en el Reglamento de Propiedad y Construcci&oacute;n
	del desarrollo, as&iacute; como leyes y Reglamento Generales en la materia, adem&aacute;s deber&aacute; de cumplir con los &Oacute;rganos de Gobierno y
	dem&aacute;s disposiciones legales y normativas vigentes, del Desarrollo Inmobiliario denominado Residencial <b><%= bmoDevelopment.getName().toString().toUpperCase() %></b>,
	si es el caso que se formaliza la escritura de compra-venta definitiva.
</p>

<p align="justify" >
	<b>D&Eacute;CIMA SEGUNDA. LA ADMINISTRADORA DEL DESARROLLO</b>.- El <b>"COMPRADOR"</b> se obliga desde este momento a respetar y sujetarse a las condicionantes de
	operaci&oacute;n que se&ntilde;ale la administraci&oacute;n en el Anexo 4 del presente contrato para el buen funcionamiento del Desarrollo Inmobiliario
	denominado <b><%= bmoDevelopment.getName().toString().toUpperCase() %></b>, si es el caso que se formaliza la escritura de compra-venta que las partes se obligaron a
	celebrar en t&eacute;rminos del presente contrato.
	<br><br>
	No obstante lo anterior, el <b>"COMPRADOR"</b> desde este momento se obliga a pagar las cuotas que por concepto de mantenimiento le fije la
	sociedad la Administradora y, en su caso, la mismas que podr&aacute;n requerirse a partir del siguiente mes inmediato a que se firme la escritura del inmueble objeto de este contrato,
	pagaderas dentro de los primeros 5 d&iacute;as h&aacute;biles del mes corriente.
	<br><br>
	Para el caso de que el <b>"COMPRADOR"</b> no cumpla con efectuar el pago de las cuotas de mantenimiento referidas,
	dentro de los primeros 5 (cinco) d&iacute;as h&aacute;biles del mes corriente, se le aplicar&aacute; una pena convencional de $10.00 (diez pesos 00/100mn) diarios
	que se acumular&aacute;n por cada d&iacute;a en los que no se cumpla con dicho pago; as&iacute; mismo, se le negar&aacute;n los servicios de Areas Comunes y
	Vigilancia a las que su Etapa y Privada correspondan y que sean las estipuladas dentro del Reglamento de la Administradora.
	<br><br>
	El pago de las cuotas de mantenimiento a cargo del <b>"COMPRADOR"</b> de conformidad con el p&aacute;rrafo que antecede ser&aacute; definitivo;
	por lo tanto, no se le devolver&aacute; ni aun cuando el presente contrato se cancele, rescinda o resuelva bajo cualquier concepto.
</p>

<p align="justify" >
	<b>D&Eacute;CIMA TERCERA.- ACEPTACI&Oacute;N</b> - Ambas partes contratantes declaran que en este convenio no existe error, dolo, enga&ntilde;o o violencia
	alguna por lo que rec&iacute;procamente renuncian a las acciones de nulidad, que pudieran fundarse en dicha causa, y al texto mismo
	de las disposiciones aplicables contenidas en el C&oacute;digo Civil Vigente en el Estado de <%= bmoCityDevelopment.getBmoState().getName().toString().toUpperCase() %>, M&eacute;xico.
</p>

<p align="justify" >
	<b>D&Eacute;CIMA CUARTA.- JURISDICCI&Oacute;N</b> - Para la interpretaci&oacute;n, cumplimiento, y, ejecuci&oacute;n del presente contrato de promesa de
	compra-venta ambas partes se someten a las Leyes del <b>ESTADO DE <%= bmoCityDevelopment.getBmoState().getName().toString().toUpperCase() %></b>, y a los Tribunales de esta Ciudad, renunciando a
	cualquier otro fuero de sus presentes o futuros domicilios.
</p>

<p align="justify" >
	<b>Aviso de privacidad</b>:
	La Sociedad Mercantil <b><%= bmoCompany.getLegalName().toString().toUpperCase() %></b> ratific&oacute; a que todos los datos personales que han proporcionado y que en el futuro proporcionen
	a esta empresa y/o a sus subsidiarias los utilizar&aacute; para identificarlos como clientes, mantener un expediente f&iacute;sico y procesar
	electr&oacute;nicamente sus datos en los diferentes sistemas internos y en su caso, ofrecerles promociones y/o informaci&oacute;n de
	nuestros productos. La empresa por su parte les informa que dar&aacute; cumplimiento a lo que dispone la Ley Federal de
	Protecci&oacute;n de Datos Personales en posesi&oacute;n de los particulares, como responsable del tratamiento de Datos Personales,
	seg&uacute;n se definen en la Ley citada. Respecto al procedimiento para solicitar sus Derechos de Acceso, Rectificaci&oacute;n,
	Cancelaci&oacute;n u oposici&oacute;n (ARCO), en relaci&oacute;n a cualquier modificaci&oacute;n al Aviso de Privacidad y Transferencia de Datos Personales,
	podr&aacute;n hacerlo por medio de nuestra p&aacute;gina de Internet http://www.mdm.mx, en el apartado de Privacidad de Datos personales.
</p>

<p align="justify" >
	Los Anexos <b>
	1 (Especificaciones), 2 (Pagar&eacute;s), 3 (Datos personales bajo protesta de decir verdad), 4 (Reglamento), 5 (Vivienda con Ecotecnolog&iacute;a),
	6 (Lugar y Forma de pagos)
	</b> 
	forman parte de este contrato como si a la letra se insertase.
</p>
	
<p align="justify" >
	EL PRESENTE CONTRATO DE PROMESA DE COMPRA-VENTA SE FIRMA EN LA CIUDAD DE
	<b><%= bmoCityCompany.getName().toString().toUpperCase() %></b>, 
	<b><%= bmoCityCompany.getBmoState().getName().toString().toUpperCase() %></b>,  
	A <b><%= SFServerUtil.nowToString(sFParams, "dd") %> DE <%= nowMonth.toUpperCase() %>,  <%= SFServerUtil.nowToString(sFParams, "YYYY") %></b>
	<br>
	NOTAS:  <b><%= bmoOrder.getDescription().toString().toUpperCase() %></b>
</p>

<table  width="70%" align="center" style="font-size: 12px">
	<tr>
	    <td align="center">
	       	&nbsp;			        	
	    </td>
	    <td align="center">
			<!--<img src="../img/firma_lopez.jpg" width="60" height="30">-->
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
	        <b>EL COMPRADOR</b>
	    </td>
	    <td align="center">
	        <b>EL VENDEDOR</b>
	        
	    </td>
	</tr>
	<tr>
	    <td align="center">
	        <b>	
	        	<%= bmoCustomer.getDisplayName().toString().toUpperCase() %>
			</b>
	    </td>
	    <td align="center">
	        <b><%= bmoCompany.getLegalName().toString().toUpperCase() %></b><br>
	        <b><%= bmoCompany.getLegalRep().toString().toUpperCase() %></b><br>
	        <b>Representante Legal</b><br>
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
	        <b>TESTIGO</b>
	    </td>
	    <td align="center">
	        <b>TESTIGO</b>
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
</table>
<p style="page-break-after: always">&nbsp;</p>

<p>&nbsp;</p>


<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr>
		<td width="5%">
			<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
		</td>
		<td class="reportTitleCell">
			ANEXO 1 CONTRATO DE PROMESA DE COMPRA-VENTA:<br>ESPECIFICACIONES
		</td>
		<td class="reportTitleCell" style="text-align:right;font-size: 9px">
		Clave Doc:<br>FO-07.5.2.1-3
		</td>
	</tr>
</table>

<p align="justify" >
	<b>EN EL QUE SE DEFINE LA VIVIENDA SUJETA POR EL PRESENTE CONTRATO DE PROMESA DE COMPRA-VENTA.</b>
	ESTE DOCUMENTO NO TIENE VALIDEZ SIN SU CONTRAPARTE EL CONTRATO DE PROMESA DE COMPRAVENTA FIRMADO POR EL <b>"VENDEDOR"</b> Y EL <b>"COMPRADOR"</b>.
</p>
	
<table border="0" cellspacing="0" width="90%" cellpadding="0" style="font-size: 12px" align="center">
	<tr>
	    <th class="reportCellEven" align="left">&nbsp;<b>FRACCIONAMIENTO:</b></th>
	    <td class="reportCellEven" align="left">&nbsp;<%= bmoDevelopment.getName().toString().toUpperCase() %></td>
	</tr>
	<tr>
	    <th class="reportCellEven" align="left">&nbsp;<b>ETAPA:</b></th>
	    <td class="reportCellEven" align="left">&nbsp;<%= bmoDevelopmentPhase.getName().toString().toUpperCase() %></td>
	</tr>
	<tr>
	    <th class="reportCellEven" align="left">&nbsp;<b>MANZANA/FRACCI&Oacute;N:</b></th>
	    <td class="reportCellEven" align="left">&nbsp;<%= bmoPropertySale.getBmoProperty().getBmoDevelopmentBlock().getCode().toString().toUpperCase() %></td>
	</tr>
	<tr>
	    <th class="reportCellEven" align="left">&nbsp;<b>LOTE/FRACCI&Oacute;N:</b></th>
	    <td class="reportCellEven" align="left">&nbsp;<%= bmoPropertySale.getBmoProperty().getLot().toString().toUpperCase()%></td>
	</tr>
	<tr>
	    <th class="reportCellEven" align="left">&nbsp;<b>CALLE:</b></td>
	    <td class="reportCellEven" align="left">&nbsp;<%= bmoPropertySale.getBmoProperty().getStreet().toString().toUpperCase() %></td>
	</tr>
	<tr>
	    <th class="reportCellEven" align="left">&nbsp;<b>N&Uacute;MERO OFICIAL:</b></th>
	    <td class="reportCellEven" align="left">&nbsp;<%= bmoPropertySale.getBmoProperty().getNumber().toString().toUpperCase() %></td>
	</tr>
	<tr>
	    <th class="reportCellEven" align="left">&nbsp;<b>MODELO:</b></th>
	    <td class="reportCellEven" align="left">&nbsp;<%= bmoPropertyModel.getName().toString().toUpperCase() %></td>
	</tr>
	<tr>
	    <th class="reportCellEven" align="left">&nbsp;<b>DESCRIPCI&Oacute;N:</b></th>
	    <td class="reportCellEven" align="left">
	    	&nbsp;<%= bmoPropertyModel.getHighLights().toString().toUpperCase() %>
	    	<br>
	    	&nbsp;<%= bmoProperty.getDescription().toString().toUpperCase() %>
	    </td>
	</tr>
	<tr>
	    <th class="reportCellEven" align="left">&nbsp;<b>CUARTOS:</b></th>
	    <td class="reportCellEven" align="left">&nbsp;<%= bmoPropertyModel.getRooms().toString().toUpperCase() %></td>
	</tr>
	
	<tr>
	    <th class="reportCellEven" align="left">&nbsp;<b>M2 DE TERRENO::</b></th>
	    <td class="reportCellEven" align="left">
	    	&nbsp;<%= bmoPropertySale.getBmoProperty().getLandSize().toString().toUpperCase() %> mt2
	    	(<%= amountByWord.getAmountByWord(bmoPropertySale.getBmoProperty().getLandSize().toDouble()).toUpperCase() %> METROS CUADRADOS)
	    </td>
	</tr>
	<tr>
	    <th class="reportCellEven" align="left">&nbsp;<b>M2 MODELO BASE::</b></th>
	    <td class="reportCellEven" align="left">
	    	&nbsp;<%= bmoPropertyModel.getLandSize().toString().toUpperCase() %> mt2
	    	(<%= amountByWord.getAmountByWord(bmoPropertyModel.getLandSize().toDouble()).toUpperCase() %> METROS CUADRADOS)
	    </td>
	</tr>
	<tr>
	    <th class="reportCellEven" align="left">&nbsp;<b>M2 DE TERRENO EXCEDENTES:</b></th>
	    	<%	double ex = bmoPropertySale.getBmoProperty().getLandSize().toDouble() - bmoPropertyModel.getLandSize().toDouble(); %>
	    <td class="reportCellEven" align="left">
	    	&nbsp;<%= df.format(ex) %> mt2
	    	(<%= amountByWord.getAmountByWord(ex).toUpperCase() %> METROS CUADRADOS)
	    </td>
	</tr>
	<tr>
	    <th class="reportCellEven" align="left">&nbsp;<b>M2 DE CONSTRUCCI&Oacute;N:</b></th>
	    <td class="reportCellEven" align="left">&nbsp;<%= bmoPropertyModel.getConstructionSize().toString().toUpperCase() %> mt2
	    (<%= amountByWord.getAmountByWord(bmoPropertyModel.getConstructionSize().toDouble()).toUpperCase() %> METROS CUADRADOS)
	    </td>
	</tr>
	<tr>
	    <th class="reportCellEven" align="left">&nbsp;<b>CARACTER&Iacute;STICAS:</b></th>
	    <td class="reportCellEven" align="left">&nbsp;<%= bmoPropertyModel.getDetails().toString().toUpperCase() %></td>
	</tr>
	<tr>
	    <th class="reportCellEven" align="left">&nbsp;<b>ACABADOS:</b></th>
	    <td class="reportCellEven" align="left">&nbsp;<%= bmoPropertyModel.getFinishing().toString().toUpperCase() %></td>
	</tr>
	<tr>
	    <th class="reportCellEven" align="left">&nbsp;<b>ADICIONALES:</b></th>
	    <td class="reportCellEven" align="left">
	    <%
	    	double adicionalAnexo1 = 0;
		    PmConn pmConnAdicionalesAnexo1 = new PmConn(sFParams);
		    pmConnAdicionalesAnexo1.open();
		    pmConnAdicionalesAnexo1.disableAutoCommit();
									
		    pmConnAdicionalesAnexo1.doFetch(sqlVentaPedidosExtra);
	
	        while(pmConnAdicionalesAnexo1.next()) {
	        	//if(!(pmConnAdicionalesAnexo1.getInt("prmx_propertymodelextraid") > 0))
	        		adicionalAnexo1 = pmConnAdicionalesAnexo1.getDouble("orpx_price") * pmConnAdicionalesAnexo1.getDouble("orpx_quantity");
	        	//else
	        		//adicionalAnexo1 = pmConnAdicionalesAnexo1.getDouble("prmx_price") * pmConnAdicionalesAnexo1.getDouble("orpx_quantity");   
	    
	        	%>
	    		<li>
	    			(<%= pmConnAdicionalesAnexo1.getInt("orpx_quantity")%>)
	    			
	    			<% if(!(pmConnAdicionalesAnexo1.getInt("prmx_propertymodelextraid") > 0)){ %>
	    				<%= pmConnAdicionalesAnexo1.getString("orpx_comments") %>,&nbsp;
	    			<% }else{ %>
	    				<%= pmConnAdicionalesAnexo1.getString("prmx_name") %>,&nbsp;
	    			<% } %>
	    			<%= SFServerUtil.formatCurrency(adicionalAnexo1)%>
	    		</li>
	    		<% if(pmConnAdicionalesAnexo1.getInt("prmx_propertymodelextraid") > 0){ %>
	    			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	    			<%= pmConnAdicionalesAnexo1.getString("prmx_description").toUpperCase() %><br>
	    		<% } %>
	    		<% if (!pmConnAdicionalesAnexo1.getString("orpx_comments").equals("")) { %>
		    		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		    		<%= pmConnAdicionalesAnexo1.getString("orpx_comments") %>
		    		<br><br>
	    		<%
	    		}
	        }
	        pmConnAdicionalesAnexo1.close();
	    %>
	
	    </td>
	</tr>
	<tr>
	    <th class="reportCellEven" align="left">&nbsp;<b>OTROS:</b></th>
	    <td class="reportCellEven" align="left">&nbsp;
	    </td>
	</tr>
</table>

<table border="0" cellspacing="0" width="70%" cellpadding="0" style="font-size: 12px" align="center">
	<tr>
	    <td align="center">
	       	&nbsp;
	    </td>
	    <td align="center">
			<!--<img src="../img/firma_lopez.jpg" width="60" height="30"><br>-->
	    </td>
	</tr>
	<tr>
	    <td  align="center">
	        ______________________________
	    </td>
	    <td  align="center">
	
	        ______________________________
	    </td>
	</tr>
	<tr>
	    <td  align="center">
	        <b>EL COMPRADOR</b>
	    </td>
	    <td  align="center">
	        <b>EL VENDEDOR</b>
	    </td>
	</tr>
	<tr>
	    <td  align="center">
	        <b>
        		<%= bmoCustomer.getDisplayName().toString().toUpperCase() %>
        	</b>
	    </td>
	    <td  align="center">
	        <b><%= bmoCompany.getLegalName().toString().toUpperCase() %></b><br>
	        <b><%= bmoCompany.getLegalRep().toString().toUpperCase() %></b><br>
	        <b>Representante Legal</b><br>
	    </td>
	</tr>
</table>
<p align="center" style="font-size: 8pt">
	<b>
		<%= bmoCityCompany.getName().toString().toUpperCase() %>, 
		<%= bmoCityCompany.getBmoState().getName().toString().toUpperCase() %>,  
		A <%= SFServerUtil.nowToString(sFParams, "dd") %> DE <%= nowMonth.toUpperCase() %>,  <%= SFServerUtil.nowToString(sFParams, "YYYY") %>
	</b>
</p>

<p style="page-break-after: always"></p>

<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr>
		<td width="5%">
			<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
		</td>
		<td class="reportTitleCell">
			ANEXO 2 CONTRATO DE PROMESA DE COMPRA-VENTA:<br>PAGAR&Eacute;S
		</td>
		<td class="reportTitleCell" style="text-align:right;font-size: 9px">
			Clave Doc:<br>FO-07.5.2.1-3
		</td>
	</tr>
</table>

</p>
	
<p align="justify" >
	<b>
		<%= bmoCityCompany.getName().toString().toUpperCase() %>, 
		<%= bmoCityCompany.getBmoState().getName().toString().toUpperCase() %>,  
		A <%= SFServerUtil.nowToString(sFParams, "dd") %> DE <%= nowMonth.toUpperCase() %>,  <%= SFServerUtil.nowToString(sFParams, "YYYY") %>
		<br>
		<%= bmoCompany.getLegalName().toString().toUpperCase() %>
	</b>
	<br>
	PRESENTE <br><br><br>
	
	MEDIANTE EL PRESENTE ANEXO DECLARO TENER CONOCIMIENTO DE QUE EN EL SUPUESTO CASO DE NO PAGAR EN TIEMPO Y FORMA
    CUALQUIERA DE LOS DOCUMENTOS POR COBRAR (PAGAR&Eacute;S) FIRMADOS POR MI PERSONA Y QUE REPRESENTAN EL PAGO DEL ENGANCHE
    DE LA VIVIENDA QUE ESTOY ADQUIRIENDO, ACEPTO QUE EN ESTE SUPUESTO PERDER&Eacute; LA ASIGNACI&Oacute;N Y UBICACI&Oacute;N DEL INMUEBLE
    ELEGIDO Y ESPECIFICADO EN EL PRESENTE CONTRATO DE COMPRA VENTA.
    
	 <%
	 
	 	PmConn pmConnRacc = new PmConn(sFParams);
	    String sqlRacc;
	    sqlRacc= " SELECT racc_raccountid, racc_invoiceno, racc_total, racc_duedate, payt_name " +
	    			" FROM raccounts " + 
		    		" LEFT JOIN raccounttypes ON(ract_raccounttypeid = racc_raccounttypeid) " +
		    		" LEFT JOIN paymenttypes ON(payt_paymenttypeid = racc_paymenttypeid) " +
		    		" WHERE racc_orderid = " + bmoPropertySale.getOrderId().toInteger() + 
		    		" ORDER BY racc_duedate ASC, racc_invoiceno ASC";
	    
	    System.out.println("sqlRacc: "+sqlRacc);
	    pmConnRacc.open();
	    pmConnRacc.doFetch(sqlRacc);
	    int i = 1;
	    int a = 0;
	
	    while (pmConnRacc.next()) {
		    BmoRaccount bmoRaccount = new BmoRaccount();
	    	PmRaccount pmRaccount = new PmRaccount(sFParams);
	    	bmoRaccount = (BmoRaccount)pmRaccount.get(pmConnRacc.getInt("racc_raccountid"));
	
	    	if(bmoRaccount.getBmoRaccountType().getCategory().toChar() == BmoRaccountType.CATEGORY_ORDER){
	    		a += 1;
	    	}
	    }
	 %>
</p>
<p >
	A CONTINUACI&Oacute;N SE LISTAN LOS DOCUMENTOS POR COBRAR DEL ENGANCHE:
</p>
<table border="0" cellspacing="0" width="90%" cellpadding="0"  style="font-size: 12px" align="center">
	<tr>
		<th class="reportHeaderCell">Documento</th>
		<th class="reportHeaderCell">M&eacute;todo de pago</th>
		<th class="reportHeaderCell">Fecha l&iacute;mite de pago</th>
		<th class="reportHeaderCellRight" width="10%" style="padding-right: 9px">Monto</td>
	</tr>	
	<%
	
		pmConnRacc.beforeFirst();
		while (pmConnRacc.next()) {
			BmoRaccount bmoRaccount = new BmoRaccount();
			
			PmRaccount pmRaccount = new PmRaccount(sFParams);
	    	bmoRaccount = (BmoRaccount)pmRaccount.get(pmConnRacc.getInt("racc_raccountid"));
	
			if(bmoRaccount.getBmoRaccountType().getCategory().toChar() == BmoRaccountType.CATEGORY_ORDER){
				%>
			 		<tr>
			    	    <td class="reportCellEven" align = "left">
				        	<%= i%>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%= pmConnRacc.getString("racc_invoiceno")%>
			    		</td>
			    		<td class="reportCellEven" align = "left">
				        	<%= pmConnRacc.getString("payt_name")%>
			    		</td>
			    		<td class="reportCellEven" align = "left">
	        				<%= FlexUtil.dateToLongDate(sFParams, pmConnRacc.getString("racc_duedate"))%>
			    		</td>
			    		<td class="reportCellEven"align = "right">
				        	<%= SFServerUtil.formatCurrency(pmConnRacc.getDouble("racc_total"))%>
			    		</td>
					</tr>
			   <%
			            i += 1;
			}
		} 
			//pmConnRacc.close();
       %>			
</table>
<p>
	&nbsp;
</p>
<p align="justify" >
    <b>
        DE IGUAL FORMA ESTOY DE ACUERDO EN QUE LA NUEVA UBICACI&Oacute;N SE ME RE-ASIGNAR&Aacute; UNA VEZ QUE HAYA PAGADO LOS DOCUMENTOS POR COBRAR
        (PAGAR&Eacute;S) DEL ENGANCHE QUE A LA FECHA EST&Eacute;N VENCIDOS DE ACUERDO A LA FECHA DE PAGO.
        LA NUEVA UBICACI&Oacute;N SER&Aacute; ELEGIDA POR MI PERSONA, DE ACUERDO A LOS INMUEBLES DISPONIBLES EN EL MOMENTO DE LA NUEVA ASIGNACI&Oacute;N,
        PUDIENDO NO SER EL MISMO INMUEBLE QUE EL ESTABLECIDO EN EL PRESENTE CONTRATO DE COMPRA-VENTA,
        POR LO QUE SE ELABORAR&Aacute; UN NUEVO CONTRATO DE COMPRA VENTA.
    </b>
</p>


<table width="90%" align="center" border="0" cellpadding="0" cellspacing="0" style="font-size: 12px">
	<tr>
	    <td align="center">
	       	&nbsp;			        	
	    </td>
	    <td align="center">
			<!--<img src="../img/firma_lopez.jpg" width="60" height="30">-->
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
	        <b>EL COMPRADOR</b>
	    </td>
	    <td align="center">
	        <b>EL VENDEDOR</b>
	    </td>
	</tr>
	<tr>
	    <td align="center">
	        <b>	
        		<%= bmoCustomer.getDisplayName().toString().toUpperCase() %>
			</b>
	    </td>
	    <td align="center">
	        <b><%= bmoCompany.getLegalName().toString().toUpperCase() %></b><br>
	        <b><%= bmoCompany.getLegalRep().toString().toUpperCase() %></b><br>
	        <b>Representante Legal</b><br>
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
	        <b>TESTIGO</b>
	    </td>
	    <td align="center">
	        <b>TESTIGO</b>
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
</table>

<p>&nbsp;</p>

<p style="page-break-after: always">&nbsp;</p>

<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr>
		<td width="5%">
			<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
		</td>
		<td class="reportTitleCell">
			ANEXO 4 CONTRATO DE PROMESA DE COMPRA-VENTA:<br>REGLAMENTO
		</td>
		<td class="reportTitleCell" style="text-align:right; font-size: 9px">
			Clave Doc:<br>FO-07.5.2.1-3
		</td>
	</tr>
</table>

<p align="left" class="documentTitle">
	<b>INTRODUCCI&Oacute;N</b>
	</p>
	<p align="justify"  class="documentInput">
	En un mundo que poco ofrece al esp&iacute;ritu, en el cual el aceleramiento y la vida mecanizada parecen marcar la mente y el derrotero del hombre,
	es necesario y conveniente retornar constantemente a las fuentes de la tranquilidad y de la reflexi&oacute;n, donde la
	armon&iacute;a sea nuestro ambiente y alimento diarios.
	<br><br>
	El Fracconamiento &nbsp;<%= bmoDevelopment.getName().toString().toUpperCase() %>, se presenta forjador de espacios urbanos, donde el hombre pueda convivir en esa sana y
	necesaria armon&iacute;a con la naturaleza y su hermano, el hombre.
	El presente reglamento interno de construcci&oacute;n es el instrumento propio de <%= bmoDevelopment.getName().toString().toUpperCase() %>, para lograr el beneficio de crear
	los espacios apropiados para esa ansiada convivencia hombre-naturaleza.
	<br><br>
</p>

<p align="justify" >
<b>CONTENIDO</b>

<ol type="I" >
	<li>OBJETIVO DEL REGLAMENTO</li>
	<li>BASES JUR&Iacute;DICO ADMINISTRATIVAS</li>
	<li>COMIT&Eacute; T&Eacute;CNICO DE CONSTRUCCI&Oacute;N</li>
	<li>RESERVACI&Oacute;N ECOL&Oacute;GICA</li>
	<li>USO DEL SUELO</li>
	<li>SUBDIVISI&Oacute;N DE PREDIOS
	<li>DISE&Ntilde;O ARQUITECT&Oacute;NICO</li>
	<li>MATERIALES DE CONSTRUCCI&Oacute;N</li>
	<li>MEDIDAS DE SEGURIDAD DURANTE LA CONSTRUCCI&Oacute;N</li>
	<li>REDES DE SERVICIOS</li>
	<li>USO DE LA V&Iacute;A P&Uacute;BLICA Y &Aacute;REAS COMUNES</li>
	<li>CONSERVACI&Oacute;N DE PREDIOS Y CONSTRUCCIONES</li>
	<li>NOMENCLATURA</li>
	<li>AUTORIZACIONES</li>
</ol>
</p>

<p align="justify" >
	<b>I. OBJETIVO DEL REGLAMENTO</b>
	<br><br>
	<b>ART&Iacute;CULO 1</b><br>
	Con el objeto de propiciar la convivencia arm&oacute;nica de las familias habitantes del  fraccionamiento, especialmente de los ni&ntilde;os, desarrollar y
	conservar el medio ambiente y orientar a que las construcciones se realicen arm&oacute;nicamente para conformar un
	conjunto urbano integral, se establece el presente Reglamento Interno de Construcci&oacute;n para el Fraccionamiento <%= bmoDevelopment.getName().toString().toUpperCase() %>.
	Para que, al realizar construcciones, ampliaciones o modificaciones, se requerir&aacute; de la aprobaci&oacute;n expresa del Comit&eacute; T&eacute;cnico de Construcci&oacute;n y/o
	la Administradora del Fraccionamiento. Las disposiciones de este Reglamento son complementarias a las establecidas en el C&oacute;digo de Ordenamiento
	Territorial, Desarrollo Urbano y Vivienda para el Estado de <%= bmoCityDevelopment.getBmoState().getName().toString().toUpperCase() %>, 
	C&oacute;digo Urbano de La Cd de <%= bmoCityDevelopment.getName().toString().toUpperCase() %>, leyes y ordenamientos del Gobierno Federal,
	Gobierno del Estado de <%= bmoCityDevelopment.getBmoState().getName().toString().toUpperCase() %>  y del Gobierno del Municipio 
	de <%= bmoCityDevelopment.getName().toString().toUpperCase() %>, en materia de construcciones, uso del suelo y ecolog&iacute;a.
</p>

<p align="justify" >
	<b>II. BASES JUR&Iacute;DICO-ADMINISTRATIVAS</b>
	<br><br>
	<b>ART&Iacute;CULO 2</b><br>
	El Reglamento Interno de Construcci&oacute;n del Fraccionamiento <%= bmoDevelopment.getName().toString().toUpperCase() %>,
	forma parte del Reglamento de la Administradora del Fraccionamiento acorde al proyecto aprobado para el fraccionamiento 
	<%= bmoDevelopment.getName().toString().toUpperCase() %> compuesto por 8 etapas  y ambos est&aacute;n debidamente registrados para su observancia 
	en el Registro P&uacute;blico de la Propiedad, lo Direcci&oacute;n de Catastro, la Secretaria de Desarrollo Urbano del 
	Municipio de <%= bmoCityDevelopment.getName().toString().toUpperCase() %> y la Secretar&iacute;a de Gesti&oacute;n Urban&iacute;stica y Ordenamiento Territorial 
	del Estado de <%= bmoCityDevelopment.getBmoState().getName().toString().toUpperCase() %>. 
	Estas normas son obligatorias para la construcci&oacute;n de las viviendas en los predios y forman parte del compromiso de compra venta.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 3</b><br>
	El Comit&eacute; T&eacute;cnico de Construcci&oacute;n, de conformidad con los estatutos de la Administradora del Fraccionamiento,
	recibe autoridad para que el tr&aacute;mite de solicitud para la construcci&oacute;n se inicie con la presentaci&oacute;n del proyecto 
	al Comit&eacute; T&eacute;cnico de Construcci&oacute;n y/o	la Administradora del Fraccionamiento y, una vez otorgado el Visto Bueno, 
	se solicita a Gobierno Municipal la licencia de construcci&oacute;n correspondiente.
</p>

<p align="justify" >
	<b>III. EL COMIT&Eacute; T&Eacute;CNICO DE CONSTRUCCI&Oacute;N </b>
	<br><br>
	<b>ART&Iacute;CULO 4</b><br>
	Con objeto de vigilar el cumplimiento del Reglamento interno de Construcci&oacute;n,
	La Administradora del Fraccionamiento designar&aacute; un Comit&eacute; T&eacute;cnico de Construcci&oacute;n integrado al menos de tres personas,
	dos de los cuales deber&aacute;n ser de profesi&oacute;n arquitecto o ingeniero civil y estar t&eacute;cnicamente calificados 
	y registrados para ejercer profesionalmente. El Comit&eacute; T&eacute;cnico de Construcci&oacute;n se har&aacute; cargo de las siguientes tareas:
</p>

<ol type="1" style="text-align:justify;">
<li>Coadyuvar con las Secretaria de Desarrollo Urbano Municipal y de Servicios P&uacute;blicos con sus Direcciones de Desarrollo Urbano, Ecolog&iacute;a,
    Parques y Jardines, Obras y Servicios P&uacute;blicos del Municipio de <%= bmoCityDevelopment.getName().toString().toUpperCase() %>, 
    <%= bmoCityDevelopment.getBmoState().getName().toString().toUpperCase() %> en lo relacionado al Fraccionamiento.
</li>
<li>Recibir y revisar los proyectos de construcci&oacute;n que sometan a su consideraci&oacute;n los propietarios del Terreno del Fraccionamiento,
    revisarlos y otorgar, en su caso, el Visto e Bueno de cumplimiento del Reglamento Interno de Construcci&oacute;n.
</li>
<li>Establecer vigilancia en el Fraccionamiento para evitar la tala de &aacute;rboles.
</li>
<li>Establecer vigilancia para evitar que se inicien construcciones sin Visto Bueno y sin Licencia Municipal, que se construyan barracas,
    anuncios o cualquier tipo de estructura en los predios.
</li>
<li>Vigilar que no se depositen materiales de construcci&oacute;n en las &aacute;reas comunes o en los predios que no tienen licencia de construcci&oacute;n**.
</li>
<li>Revisar constantemente el estado en que se encuentran los pavimentos, el ajardinada de los parques, el servicio de alumbrado p&uacute;blico,
    y las redes de agua, drenaje, alcantarillado, tel&eacute;fono, gas y energ&iacute;a el&eacute;ctrica en el Fraccionamiento
    <%= bmoDevelopment.getName().toString().toUpperCase() %> e informar a la Administradora del Fraccionamiento.
</li>
<li>Reportar las violaciones a este Reglamento Interno, Administradora del Fraccionamiento, Gobierno Municipal, y en su caso
    a La Secretar&iacute;a de Gesti&oacute;n Urban&iacute;stica y Ordenamiento del Gobierno del Estado.
</li>
</ol>

<p align="justify" >
	<b>ART&Iacute;CULO 5</b><br>
	El Gobierno Municipal de <%= bmoCityDevelopment.getName().toString().toUpperCase() %> acuerda no tramitar la solicitud de licencia de construcci&oacute;n 
	en los predios del Fraccionamiento <%= bmoDevelopment.getName().toString().toUpperCase() %>,
	si el proyecto no contiene eI Visto Bueno del Comit&eacute; T&eacute;cnico de Construcci&oacute;n y/o la Administradora del Fraccionamiento;
	en caso contrario lo har&aacute; saber al solicitante, suspendiendo su tr&aacute;mite hasta cumplir con este requisito.
</p>


<p align="justify" >
	<b>ART&Iacute;CULO 6</b><br>
	El Comit&eacute; T&eacute;cnico de Construcci&oacute;n  y/o la Administradora del Fraccionamiento reportar&aacute; al Gobierno del Municipio 
	de <%= bmoCityDevelopment.getName().toString().toUpperCase() %>, <%= bmoCityDevelopment.getBmoState().getName().toString().toUpperCase() %>,
	las violaciones que cometan los propietarios de los predios, a las disposiciones de uso del suelo y construcci&oacute;n establecidas en el
	Reglamento interno de Construcci&oacute;n, y por los gobiernos Federal, Estatal y Municipal.
</p>

<p align="justify" >
	<b>IV. PRESERVACI&Oacute;N ECOL&Oacute;GICA</b>
	<br><br>
	<b>ART&Iacute;CULO 7</b><br>
	Est&aacute; prohibida la tala o derribo de &aacute;rboles, El Comit&eacute; T&eacute;cnico de Construcci&oacute;n y/o la Administradora del Fraccionamiento 
	reportar&aacute; al	propietario del predio en donde se talen o derriben &aacute;rboles ante la Administradora del Fraccionamiento y en su caso ante la autoridad
	municipal competente. Se deber&aacute; sembrar al menos un &aacute;rbol por cada predio y su cuidado estar&aacute; a cargo del propietario.
	Dado que las redes de energ&iacute;a el&eacute;ctrica, tel&eacute;fonos y especiales, ser&aacute;n subterr&aacute;neas, los &aacute;rboles ser&aacute;n 
	sembrados en el &aacute;rea de restricci&oacute;n de cada predio y no en los senderos, andadores o cualquier tipo de calles y circulaciones, 
	Trat&aacute;ndose de los &aacute;rboles de reciente plantaci&oacute;n, se evitar&aacute; moverlos del sitio de su ubicaci&oacute;n, salvo en casos de extrema 
	necesidad, justificada ante el Comit&eacute; T&eacute;cnico de Construcci&oacute;n y/o la
	Administradora del Fraccionamiento, quien resolver&aacute; sobre su aprobaci&oacute;n.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 8</b><br>
	Las &aacute;reas libres destinadas a parques y los espacios jardinados de las &aacute;reas comunes deber&aacute;n preservarse con vegetaci&oacute;n y limpios 
	de escombro y basura. El Comit&eacute; T&eacute;cnico de Construcci&oacute;n y/o la Administradora del Fraccionamiento cuidar&aacute; de que &eacute;stos 
	trabajos se realicen hasta el momento de la	entrega de cada una de las Etapas, para su operaci&oacute;n y mantenimiento.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 9</b><br>
	Para evitar el mal uso del terreno, el Comit&eacute; T&eacute;cnico de Construcci&oacute;n y/o la Administradora del Fraccionamiento vigilar&aacute; que no se
	realicen asentamientos irregulares o se utilicen para depositar materiales, chatarra, equipos elementos nocivos, basura u otros.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 10</b><br>
	No se permitir&aacute; el desag&uuml;e temporal o definitivo de los desechos de aguas negras o de aguas contaminadas a las &aacute;reas circunvecinas al 
	Fraccionamiento. Para establecer alg&uacute;n sistema de uso del agua pluvial se deber&aacute; presentar el proyecto de este al Comit&eacute; T&eacute;cnico 
	de Construcci&oacute;n y/o la Administradora del Fraccionamiento para su aprobaci&oacute;n.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 11</b><br>
	Las redes de aguas negras y aguas pluviales ser&aacute;n separadas tanto en el Fraccionamiento como en las construcciones de
	manera que se pueda descargar el agua pluvial a los cauces naturales y las aguas negras al Colector General de SAPAL.
	Se consideran aguas negras las descargas en desag&uuml;es de excusados, lavabos, fregaderos, lavaderos y regaderas, u otro tipo de muebles similares.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 12</b><br>
	La Etapa est&aacute; integrada por predios, destinados al uso habitacional unifamiliar.
	En estos predios s&oacute;lo se permite la construcci&oacute;n de una vivienda con ba&ntilde;o y cocina.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 13</b><br>
	En el Fraccionamiento <%= bmoDevelopment.getName().toString().toUpperCase() %>
	se establecen tres zonas destinadas a los siguiente uso: residencial unifamiliar. Dentro de cada privada,
	los usos ser&aacute;n: vivienda unifamiliar, estacionamiento de visitas y &aacute;reas verdes.
	Estas zonas s&oacute;lo podr&aacute;n ser utilizadas para el uso aprobado y las construcciones se ajustar&aacute;n a las disposiciones de este Reglamento interno,
	y las de construcci&oacute;n y uso del suelo del Estado y Municipio.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 14</b><br>
	Para el caso de adecuaciones a la vivienda objeto de este contrato en materia de "COCHERA",
	s&oacute;lo se permitir&aacute; el dise&ntilde;o previamente autorizado y revisado por el Comit&eacute; T&eacute;cnico que designado por la Administradora del
	Fraccionamiento <%= bmoDevelopment.getName().toString().toUpperCase() %>, se muestra en el Anexo A1 o A2,
	seg&uacute;n corresponda.<br>
	Este Anexo (A1 o A2) seg&uacute;n corresponda, deber&aacute; acatarse totalmente y por ello, se proporcionan en el mismo: Renders a Color,
	Plano, Especificaciones y Ficha T&eacute;cnica con Acabados.
	<br><br>
	Para el caso en el que el Comprador contrate los servicios del Fraccionamiento <%= bmoDevelopment.getName().toString().toUpperCase() %>
	para la construcci&oacute;n de la COCHERA, &eacute;sta deber&aacute; ser documentada en el mismo contrato y de inicio al firmarse &eacute;ste mismo y hacer los pagos correspondientes a &eacute;sta aditiva.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 15</b><br>
	Para el caso de adecuaciones a la vivienda objeto de este contrato en materia de "ROOF GARDEN",
	s&oacute;lo se permitir&aacute; el dise&ntilde;o previamente autorizado y revisado por el Comit&eacute; T&eacute;cnico que designado por la Administradora del
	Fraccionamiento <%= bmoDevelopment.getName().toString().toUpperCase() %>.
	<!--, se muestra en el Anexo B1 o B2 , seg&uacute;n corresponda.
	<br>
	Este Anexo (B1 o B2) seg&uacute;n corresponda, deber&aacute; acatarse totalmente y por ello, se proporcionan en el mismo: Renders a Color, Plano,
	Especificaciones y Ficha T&eacute;cnica con Acabados.
	<br><br>
	Para el caso en el que el Comprador contrate los servicios del Fraccionamiento <%//= bmoDevelopment.getName().toString().toUpperCase() %>  para la construcci&oacute;n de
	ROOF GARDEN, &eacute;sta deber&aacute; ser documentada en el mismo contrato y de inicio al firmarse &eacute;ste mismo y hacer los pagos correspondientes a &eacute;sta aditiva.
	-->
</p>


<p align="justify" >
	<b>ART&Iacute;CULO 16</b><br>
	Los tendederos, calentadores, dep&oacute;sitos de basura, antenas de TV, parab&oacute;licas, equipos de aire acondicionado y otros,
	quedar&aacute;n ocultos desde cualquier punto exterior del predio.
	Las torres de radio y radio comunicaci&oacute;n en cuanto a su colocaci&oacute;n ser&aacute; sujeta a Ia revisi&oacute;n y aprobaci&oacute;n del Comit&eacute; 
	T&eacute;cnico de Construcci&oacute;n y/o la Administradora del Fraccionamiento.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 17</b><br>
	No se permite lo construcci&oacute;n de bardas ni la colocaci&oacute;n de rejas de ning&uacute;n tipo  en el frente de los predios ni el 
	las colindancias laterales del frente.
</p>


<p align="justify" >
	<b>VIII. MATERIALES DE CONSTRUCCI&Oacute;N</b>
	<br><br>
	<b>ART&Iacute;CULO 18</b><br>
	El esp&iacute;ritu de este reglamento es en el sentido de crear una imagen arm&oacute;nica en base a texturas, materiales y colores propios de Ia regi&oacute;n.
	Los colores que la vivienda objeto de este contrato tendr&aacute;n a su entrega, no se podr&aacute;n cambiar ni modificar en el presente ni en el futuro.
</p>

<p align="justify" >
	<b>IX. MEDIDAS DE SEGURIDAD DURANTE LA CONSTRUCCI&Oacute;N</b>
	<br><br>
	<b>ART&Iacute;CULO 19</b><br>
	Durante el proceso de construcci&oacute;n el propietario del predio se compromete a:
</p>

<ol type="1" style="text-align:justify;">
<li>
    Seguir las normas de seguridad establecidas en el Fraccionamiento, siendo obligatorio el uso de cascos, con excepci&oacute;n de los yeseros y
    los pintores con andamiaje de hasta 1.00 metro de altura en trabajos interiores: y botas de trabajo como m&iacute;nimo, para sus trabajadores en Ia obra.
</li>
<li>
    En caso de requerirse el uso de maquinaria pesada que tenga que transitar por las &aacute;reas comunes se deber&aacute; obtener autorizaci&oacute;n del
    Comit&eacute; de Construcci&oacute;n y/o la Administradora del Fraccionamiento. Se tendr&aacute; un control de entrada y salida de materiales al Fraccionamiento
    y a las obras.
</li>
<li>
    Ning&uacute;n material o escombro podr&aacute; permanecer en las &aacute;reas comunes. La carga y descarga de material deber&aacute; realizarse dentro del predio y
    no se permitir&aacute; la invasi&oacute;n de las &aacute;reas comunes.
</li>
<li>
    Se tendr&aacute; un control de entrada y salida de personal para lo cual se deber&aacute; de entregar a Ia caseta de vigilancia semanalmente el
    listado de trabajadores que se ocupen en la construcci&oacute;n de Ia vivienda para su registro; Se sancionar&aacute; a la persona que circule o se le 
    encuentre en &aacute;reas no permitidas fuera de su &aacute;rea autorizada para realizar alguna labor o prestar alg&uacute;n servicios.
</li>
<li>
    Se deber&aacute; de mantener el orden y la higiene en la obra.
</li>
<li>
    Una vez iniciada la construcci&oacute;n de las ampliaciones y/o adecuaciones en el Fraccionamiento, se tendr&aacute; un plazo m&aacute;ximo de 6 (seis)
    meses para su terminaci&oacute;n m&iacute;nima para su ocupaci&oacute;n.
</li>
</ol>

<p align="justify" >
	<b>X. REDES DE SERVICIOS</b>
	<br><br>
	<b>ART&Iacute;CULO 20</b><br>
	Las redes de servicios urbanos agua potable, drenaje, alcantarillado, energ&iacute;a el&eacute;ctrica y tel&eacute;fono, est&aacute;n previstas en ductos 
	subterr&aacute;neos, por lo que NO se permite la instalaci&oacute;n de ning&uacute;n tipo de tuber&iacute;a o cables de l&iacute;neas a&eacute;reas de 
	antenas de radio o televisi&oacute;n que sean visibles desde el exterior de los predios. Se deber&aacute; de construir una cisterna con un volumen 
	m&iacute;nimo equivalente al consumo diario, de acuerdo al proyecto aprobado.
</p>

<p align="justify" >
	<b>XI. USO DE LA V&Iacute;A P&Uacute;BLICA Y &Aacute;REAS COMUNES</b>
	<br><br>
	<b>ART&Iacute;CULO 21</b><br>
	No se permitir&aacute; ordinariamente, el estacionamiento de veh&iacute;culos de carga de m&aacute;s de 3 toneladas y largo excesivo, tales como trailers o remolques:
	salvo autorizaci&oacute;n expresa del Comit&eacute; T&eacute;cnico de Construcci&oacute;n, los autos de visitas deber&aacute;n de ocupar las &aacute;reas 
	ex profesa y el	personal de Ia obra ocupar&aacute; las &aacute;reas que para cada caso se les asignen y respetar el acceso a las viviendas.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 22</b><br>
	Los Propietarios de las viviendas en donde se construyan ampliaciones y/o adecuaciones mencionadas y previamente autorizadas,
	deber&aacute;n cuidar sus colindancias y respetar las viviendas que se encuentran en ellas, evitando todo tipo de invasiones,
	as&iacute; como dejar escombro o materiales que produzcan alima&ntilde;as o hierba.
</p>

<p align="justify" >
	En ning&uacute;n caso se podr&aacute; cambiar el dise&ntilde;o de Ia guarnici&oacute;n, pavimento, cuneta, banqueta, etc., ni aumentar o
	quitar elementos que impidan el funcionamiento de los mismos.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 23</b><br>
	Cuando en la ejecuci&oacute;n de una obra, por el uso de veh&iacute;culos, objetos, sustancias o por alguna otra causa se produzcan da&ntilde;os a
	cualquier servicio p&uacute;blico, la reparaci&oacute;n inmediata de los da&ntilde;os ser&aacute; por cuenta del propietario del predio, veh&iacute;culo,
	objeto o sustancia, haciendo el pago a favor de la Administradora del Fraccionamiento; qui&eacute;n reparar&aacute; de inmediato el da&ntilde;o.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 24</b><br>
	El pavimento de las calles solamente podr&aacute; romperse y reponerse por la Administradora del Fraccionamiento, seg&uacute;n sea el caso.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 25</b><br>
	Los desperfectos de las &aacute;reas comunes o sus instalaciones de servicio p&uacute;blico deber&aacute;n ser repuestos por la Administradora del Fraccionamiento.
	Los particulares no estan autorizados para reparar estas instalaciones. El costo ser&aacute; a cargo del responsable del desperfecto.
</p>


<p align="justify" >
	<b>XII. CONSERVACI&Oacute;N DE VIVIENDAS Y CONSTRUCCIONES</b>
	<br><br>
	<b>ART&Iacute;CULO 26</b><br>
	A partir de la entrega de la vivienda, el propietario tiene la obligaci&oacute;n de mantener en buenas condiciones de aspecto e higiene.
	Est&aacute; prohibido el dep&oacute;sito materiales de construcci&oacute;n, escombro o basura, que permita alima&ntilde;as o hierba nociva. De violar estas disposiciones,
	el Comit&eacute; T&eacute;cnico de Construcci&oacute;n y/o la Administradora del Fraccionamiento dar&aacute; aviso al propietario y al Gobierno Municipal
	para que se proceda al desalojo de la basura o a la demolici&oacute;n de Ia construcci&oacute;n en su caso; ello con cargo al propietario de la construcci&oacute;n.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 27</b><br>
	Las viviendas colindantes no podr&aacute;n ser utilizados para depositar materiales de construcci&oacute;n, desechos de obra o basura,
	salvo autorizaci&oacute;n por escrito del colindante m&aacute;s cercano y sujeto a disponibilidad. No se permite el paso de veh&iacute;culos o de operarios
	a trav&eacute;s de los predios. El Comit&eacute; T&eacute;cnico de Construcci&oacute;n y/o la Administradora del Fraccionamiento reportar&aacute; las violaciones a
	la Administraci&oacute;n del Fraccionamiento o al Gobierno Municipal, y coadyuvar&aacute; con los propietarios afectados.
</p>
	
<p align="justify" >
	En caso de que el propietario no mantenga las condiciones mencionadas en los art&iacute;culos 32 y 33, el
	administrador podr&aacute; realizar la limpieza con cargo al propietario infractor.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 28</b><br>
	Los propietarios est&aacute;n obligados a  conservar las fachadas pintadas, aseadas y con buena apariencia y las &aacute;reas jardinadas en buen estado.
</p>



<p align="justify" >
	<b>ART&Iacute;CULO 29</b><br>
	La construcci&oacute;n que fuere, iniciada dentro de la vivienda &oacute; &aacute;rea privada en el Fraccionamiento, sin haber sido aprobada por el
	Comit&eacute; T&eacute;cnico de Construcci&oacute;n y/o la Administradora del Fraccionamiento, ser&aacute; suspendida por &eacute;ste y reportada al Municipio.
	Tambi&eacute;n ser&aacute; motivo de suspensi&oacute;n el incumplimiento del Reglamento interno de Construcci&oacute;n, o la modificaci&oacute;n del proyecto sin
	aprobaci&oacute;n previa. Para suspender la obra, el Comit&eacute; T&eacute;cnico de Construcci&oacute;n y/o la Administradora del Fraccionamiento notificar&aacute; al
	propietario y a la Oficina de licencias de la Secretar&iacute;a de Desarrollo Urbano del Municipio.
</p>

<p align="justify" >
	Para lo anterior el Comit&eacute; T&eacute;cnico de Construcci&oacute;n y/o la Administradora del Fraccionamiento tendr&aacute; facultades para llevar a cabo
	la supervisi&oacute;n peri&oacute;dica durante el proceso de construcci&oacute;n de las viviendas. En caso de omisi&oacute;n o reincidencia en el incumplimiento,
	la Administraci&oacute;n del Fraccionamiento y/o el Comit&eacute; T&eacute;cnico de Construcci&oacute;n y/o la Administradora del Fraccionamiento se reservan el
	derecho de veto al constructor y su personal, pudiendo reportarlo a las autoridades y al colegio de profesionales al que pertenezca,
	reclamando su comportamiento &eacute;tico y profesional.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 30</b><br>
	Para que el proyecto arquitect&oacute;nico sea autorizado por el Comit&eacute; T&eacute;cnico de Construcci&oacute;n y/o la Administradora del Fraccionamiento
	&eacute;ste deber&aacute; ser el proporcionado por el Fraccionamiento en materia de "COCHERA" y/o "ROOF GARDEN", anexado en &eacute;ste contrato y deber&aacute;
	cumplir con los requisitos que establece este Reglamento interno de Construcci&oacute;n y las normas municipales aplicables. Para ello el propietario
	acreditar&aacute; al profesionista que se har&aacute; cargo de la construcci&oacute;n de la vivienda y responder&aacute; ante el Comit&eacute; T&eacute;cnico de Construcci&oacute;n y/o la
	Administradora del Fraccionamiento y la autoridad municipal.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 31</b><br>
	Todas las solicitudes y autorizaciones a las que se hace referencia en este reglamento se har&aacute;n por escrito y el Comit&eacute; T&eacute;cnico de
	Construcci&oacute;n y/o la Administradora del Fraccionamiento se reserva la facultad para resolver sobre cualquier duda o controversia que se
	presente relacionado con cualquier punto no expresado en el presente Reglamento Interno de Construcci&oacute;n, as&iacute; como para modificar Ia siembra
	de viviendas en una, varias o todas las etapas del Fraccionamiento si las circunstancias que determine la Administradora del
	Fraccionamiento as&iacute; lo requieren.
</p>


	<p style="page-break-after: always"></p>

	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		<tr>
			<td width="5%">
				<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
			</td>
			<td class="reportTitleCell">
				ANEXO 5 DEL CONTRATO DE PROMESA DE COMPRA VENTA VIVIENDA CON ECOTECNOLOG&Iacute;A
			</td>
			<td class="reportTitleCell" style="text-align:right; font-size: 9px">
				C&oacute;digo del Documento:<br>FO-07.2.2-3(01-Ene-09)
			</td>
		</tr>
	</table>

	<p style="font-size: 11px">
		<b>
			<%= bmoCompany.getLegalName().toString().toUpperCase() %><br>
			PRESENTE
		</b>
	</p>
	<p  align="justify" style="font-size: 11px">
		EL COMPRADOR INDICA QUE FUE INFORMADO POR EL VENDEDOR QUE POR DISPOSICIONES DEL GOBIERNO FEDERAL TODAS LAS VIVIENDAS
		QUE SEAN ADQUIRIDAS V&Iacute;A CR&Eacute;DITO OTORGADO POR LA SOCIEDAD HIPOTECARIA FEDERAL Y/O POR EL INFONAVIT O QUE UTILICEN SUBSIDIO FEDERAL 
		PARA SU PAGO, DEBERAN CONTAR CON ELEMENTOS AHORRADORES DE ENERG&Iacute;A Y AGUA, ESTAS VIVIENDAS CON ELEMENTOS AHORRADORES SE DENOMINAN 
		&ldquo;VIVIENDAS CON ECOTECNOLOG&Iacute;A&ldquo;.
		<br>
		EN BASE A LO DETALLADO EN EL P&Aacute;RRAFO ANTERIOR, EL COMPRADOR RECONOCE QUE LA VIVIENDA QUE ADQUIERE AL VENDEDOR CUENTA CON ELEMENTOS 
		ECOTECNOL&Oacute;GICOS AHORRADORES DE GAS, ENERG&Iacute;A EL&Eacute;CTRICA Y AGUA, ELEMENTOS QUE SE MENCIONAN M&Aacute;S ADELANTE Y QUE FORMAN PARTE INTEGRANTE 
		DEL MISMO COMO SI A LA LETRA SE INSERTASEN.
		<br>
		EL COMPRADOR ESTA ENTERADO Y ES CONCIENTE QUE DENTRO DE LOS  ELEMENTOS AHORRADORES DE ENERG&Iacute;A Y AGUA INSTALADOS EN SU VIVIENDA SE 
		CONSIDERA UN CALENTADOR  DE AGUA QUE UTILIZA ENERG&Iacute;A SOLAR. EL COMPRADOR RECONOCE QUE ESTA ENTERADO QUE SU VIVIENDA CUENTA CON UN
		CALENTADOR SOLAR Y POR LO TANTO A LA FIRMA DE ESTE CONTRATO ACEPTA LA RESPONSABILIDAD TOTAL DEL MANTENIMIENTO Y CUIDADO DEL CITADO
		CALENTADOR SOLAR, ESTE CALENTADOR SOLAR DEBERA SER PROTEGIDO POR EL COMPRADOR PARA NO SUFRIR GOLPES, PEDRADAS, RAMAS DE &Aacute;RBOLES, 
		GRANIZO, ETC. O ACTOS DE VANDALISMO QUE PUDIERAN ROMPER LA CUBIERTA O PARTES DEL MISMO, LO CUAL PODR&Iacute;A PROPICIAR AFECTACI&Oacute;N A LA 
		OPERACI&Oacute;N Y/O FUNCIONAMIENTO DEL MISMO.
		<br>
		EL COMPRADOR ESTA TOTALMENTE CONCIENTE QUE EL CALENTADOR SOLAR CUENTA CON UNA GARANT&Iacute;A OTORGADA POR EL PROVEEDOR DEL MISMO, GARANT&Iacute;A
		QUE CUBRE DEFECTOS DE MATERIALES Y/O FALLAS EN LA INSTALACI&Oacute;N. ESTA GARANT&Iacute;A NO CUBRE ROTURAS DEL VIDRIO DE CUBIERTA O DE ELEMENTOS 
		QUE COMPONEN EL CALENTADOR SOLAR, ROTURAS QUE PUDIERAN DERIVARSE DE ACTOS DE VANDALISMO (PEDRADAS, RAMAS DE &Aacute;RBOLES, ETC.), O CAUSADAS 
		POR FEN&Oacute;MENOS NATURALES COMO PUEDE SER GRANIZO, ETC., EN ESTOS SUPUESTOS LA REPARACI&Oacute;N Y/O REEMPLAZO DE PARTES DEL CALENTADOR ES ENTERA 
		Y TOTAL RESPONSBILIDAD DEL COMPRADOR DE LA VIVIENDA.
		<br>
		EN BASE A LO DETALLADO EN EL P&Aacute;RRAFO QUE ANTECEDE Y LO MENCIONADO  CORRESPONDIENTE A ELEMENTOS AHORRADORES DE ENERG&Iacute;A Y AGUA, EL VENDEDOR 
		ACEPTA EN ESTE ACTO LA REPONSABILIDAD TOTAL PARA EL MANTENIMIENTO Y PROTECCI&Oacute;N DE ESTOS ELEMENTOS.
	</p>
		
	<p  align="justify" style="font-size: 11px">
		LA &ldquo;VIVIENDA CON ECOTECNOLOG&Iacute;A&ldquo; CUENTA CON LOS ELEMENTOS AHORRADORES DE ENERG&Iacute;A EL&Eacute;CTRICA, GAS Y AGUA QUE A CONTINUACI&Oacute;N SE DETALLAN:
	</p>
		
	<ol type="a" style="font-size: 11px; text-align:justify;">
			<li>
				<strong><u>FOCOS AHORRADORES DE ENERG&Iacute;A:</u><br>
				<u>DESCRIPCI&Oacute;N:</u></strong>
				
				<br>CARACTER&Iacute;STICAS:<br>
				<strong>AHORRAN ENERG&Iacute;A EL&Eacute;CTRICA, TIENEN UNA VIDA APROXIMADA DE 10 VECES M&Aacute;S QUE LOS FOCOS CONVENCIONALES Y CON UN MISMO GRADO DE ILUMINACI&Oacute;N.</strong></p>
				<p>
				MANTENIMIENTO:<br>
				<strong>LIMPIEZA CADA 30 D&Iacute;AS</strong></p> 
				<p>
				CUIDADOS: <br>
				<strong>LIMPIEZA Y/O CAMBIO</strong>
			</li>
		<li>
			<strong>
				<u>CALENTADOR SOLAR:</u><br>
				<u>DESCRIPCI&Oacute;N:</u>
			</strong>
			<br>CARACTER&Iacute;STICAS:<br>
				<strong>AHORRO CONSIDERABLE EN GAS, YA QUE EL AGUA ES CALENTADA CON ENERG&Iacute;A SOLAR. 
				CUENTA CON UN CALENTADOR DE RESPALDO DE GAS PARA REFORZAR LA TEMPERATURA EN CASO DE QUE SEA REQUERIDO.</strong>
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
			<strong><u>ELEMENTOS AHORRADORES DE AGUA:</u></strong><br>
			<u><strong>DESCRIPCI&Oacute;N:</strong></u>
			<br>CARACTERISTICAS:<br>
			<strong>AHORRO EN EL CONSUMO DE AGUA, ES DECIR ES UN DISPOSITIVO REDUCTOR DE FLUJO QUE EMITE LA CANTIDAD DE AGUA NECESARIA PARA EL SERVICIO DESEADO.</strong>
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
				<!--<img src="../img/firma_lopez.jpg" width="60" height="30">-->
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
	<p style="page-break-after: always"></p>

	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		<tr>
			<td width="5%">
				<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
			</td>
			<td class="reportTitleCell">
				ANEXO 6 LUGAR Y FORMA DE PAGOS
			</td>
		</tr>
	</table>
	<br>
	<table width="95%" align="center" border="0" cellpadding="0" cellspacing="0" style="font-size: 12px">
		<tr>
			<th class="reportCellEven" align="left">Cliente:</th>
			<td class="reportCellEven" align="left" colspan="3">
				<%= bmoCustomer.getCode().toString()%> <%= bmoCustomer.getDisplayName().toString()%>
			<td>
		</tr>
		<tr>
			<th class="reportCellEven" align="left">Ubicaci&oacute;n:</th>
			<td class="reportCellEven" align="left" colspan="3">
				<b><%= bmoPropertySale.getBmoProperty().getBmoDevelopmentBlock().getName().toString().toUpperCase() %>,</b> 
				lote/fracci&oacute;n 
				<b><%= bmoPropertySale.getBmoProperty().getLot().toString().toUpperCase() %></b> 
				de la calle 
				<b><%= bmoPropertySale.getBmoProperty().getStreet().toString().toUpperCase() %></b> 
				con n&uacute;mero oficial 
				<b><%= bmoPropertySale.getBmoProperty().getNumber().toString().toUpperCase() %></b> en el 
				fraccionamiento 
				<b><%= bmoDevelopment.getName().toString().toUpperCase() %></b>,
				etapa <b><%= bmoDevelopmentPhase.getName().toString().toUpperCase() %></b>
				en la ciudad de 
				<b><%= bmoCityDevelopment.getName().toString().toUpperCase() %></b>,  
				<b><%= bmoCityDevelopment.getBmoState().getName().toString().toUpperCase() %></b>.
			<td>
		</tr>
		<tr>
			<td colspan="4">
				&nbsp;
			</td>
		</tr>
		<tr>
			<th class="reportHeaderCell">Documento</th>
			<th class="reportHeaderCell">M&eacute;todo de pago</th>
			<th class="reportHeaderCell">Fecha l&iacute;mite de pago</th>
			<th class="reportHeaderCellRight" width="10%" style="padding-right: 9px">Monto</th>
		</tr>
		<%
			pmConnRacc.beforeFirst();
			i = 1;
			while (pmConnRacc.next()) {
				BmoRaccount bmoRaccount = new BmoRaccount();
				
				PmRaccount pmRaccount = new PmRaccount(sFParams);
		    	bmoRaccount = (BmoRaccount)pmRaccount.get(pmConnRacc.getInt("racc_raccountid"));
		
				if(bmoRaccount.getBmoRaccountType().getCategory().toChar() == BmoRaccountType.CATEGORY_ORDER){
					%>
				 		<tr>
				 			<td class="reportCellEven" align = "left">
					        	<%= i%>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%= pmConnRacc.getString("racc_invoiceno")%>
				    		</td>
				    		<td class="reportCellEven" align = "left">
					        	<%= pmConnRacc.getString("payt_name")%>
				    		</td>
				    		<td class="reportCellEven" align = "left">
			        			&nbsp;<%= FlexUtil.dateToLongDate(sFParams, pmConnRacc.getString("racc_duedate"))%>
				    		</td>
				    		<td class="reportCellEven" align = "right">
					        	&nbsp;<%= SFServerUtil.formatCurrency(pmConnRacc.getDouble("racc_total"))%>
				    		</td>
				   <%
				            i += 1;
				}
			} 
				//pmConnRacc.close();
	       %>			
		</tr>
	</table>
	<table width="95%" align="center" border="0" cellpadding="0" cellspacing="0" style="font-size: 12px">
		<tr>
		    <td align="center" height="40">
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
	        <td>
		    	&nbsp;
		    </td>
		</tr>    
		<tr>
		    <td align="center">
		        <b>EL COMPRADOR</b>
		    </td>
		    <td>
		    	&nbsp;
		    </td>
		</tr>
		<tr>
		    <td align="center">
		        <b>	
		        	<%= bmoCustomer.getDisplayName().toString().toUpperCase() %>
				</b>
		    </td>
		    <td align="right">
		    	Empresa
		    </td>
		</tr>
	</table>
	<p><br><br></p>
	<table width="95%" align="center" border="0" cellpadding="0" cellspacing="0" style="font-size: 12px">
		<tr>
			<th class="reportCellEven" align="left">Cliente:</th>
			<td class="reportCellEven" align="left" colspan="3">
				<%= bmoCustomer.getCode().toString()%> <%= bmoCustomer.getDisplayName().toString()%>
			<td>
		</tr>
		<tr>
			<th class="reportCellEven" align="left">Ubicaci&oacute;n:</th>
			<td class="reportCellEven" align="left" colspan="3">
				<b><%= bmoPropertySale.getBmoProperty().getBmoDevelopmentBlock().getName().toString().toUpperCase() %>,</b> 
				lote/fracci&oacute;n 
				<b><%= bmoPropertySale.getBmoProperty().getLot().toString().toUpperCase() %></b> 
				de la calle 
				<b><%= bmoPropertySale.getBmoProperty().getStreet().toString().toUpperCase() %></b> 
				con n&uacute;mero oficial 
				<b><%= bmoPropertySale.getBmoProperty().getNumber().toString().toUpperCase() %></b> en el 
				fraccionamiento 
				<b><%= bmoDevelopment.getName().toString().toUpperCase() %></b>,
				etapa <b><%= bmoDevelopmentPhase.getName().toString().toUpperCase() %></b>
				en la ciudad de 
				<b><%= bmoCityDevelopment.getName().toString().toUpperCase() %></b>,  
				<b><%= bmoCityDevelopment.getBmoState().getName().toString().toUpperCase() %></b>.
			<td>
		</tr>
		<tr>
			<td colspan="4">
				&nbsp;
			</td>
		</tr>
		<tr>
			<th class="reportHeaderCell">Documento</th>
			<th class="reportHeaderCell">M&eacute;todo de pago</th>
			<th class="reportHeaderCell">Fecha l&iacute;mite de pago</th>
			<th class="reportHeaderCellRight" width="10%" style="padding-right: 9px">Monto</th>
		</tr>
		<%
			pmConnRacc.beforeFirst();
			i = 1;
			while (pmConnRacc.next()) {
				BmoRaccount bmoRaccount = new BmoRaccount();
				
				PmRaccount pmRaccount = new PmRaccount(sFParams);
		    	bmoRaccount = (BmoRaccount)pmRaccount.get(pmConnRacc.getInt("racc_raccountid"));
		
				if(bmoRaccount.getBmoRaccountType().getCategory().toChar() == BmoRaccountType.CATEGORY_ORDER){
					%>
				 		<tr>
				 			<td class="reportCellEven" align = "left">
					        	<%= i%>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%= pmConnRacc.getString("racc_invoiceno")%>
				    		</td>
				    		<td class="reportCellEven" align = "left">
					        	<%= pmConnRacc.getString("payt_name")%>
				    		</td>
				    		<td class="reportCellEven" align = "left">
			        			&nbsp;<%= FlexUtil.dateToLongDate(sFParams, pmConnRacc.getString("racc_duedate"))%>
				    		</td>
				    		<td class="reportCellEven" align = "right">
					        	&nbsp;<%= SFServerUtil.formatCurrency(pmConnRacc.getDouble("racc_total"))%>
				    		</td>
				   <%
				            i += 1;
				}
			} 
				pmConnRacc.close();
	       %>			
		</tr>
		</table>
		<table width="95%" align="center" border="0" cellpadding="0" cellspacing="0" style="font-size: 12px">
		<tr>
		    <td align="center" height="40">
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
		    <td>
		    	&nbsp;
		    </td>
		</tr>    
		<tr>
		    <td align="center">
		        <b>EL COMPRADOR</b>
		    </td>
		    <td>
		    	&nbsp;
		    </td>
		</tr>
		<tr>
		    <td align="center">
		        <b>	
		        	<%= bmoCustomer.getDisplayName().toString().toUpperCase() %>
				</b>
		    </td>
		    <td align="right">
		    	Cliente
		    </td>
		</tr>
	</table>

<% 	} catch (Exception e) { 
	String errorLabel = "Error de Contrato";
	String errorText = "El Contrato no pudo ser desplegado exitosamente. Es necesario completar todos los datos faltantes: Venta de Inmuebles.";
	String errorException = e.toString();
	
	response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);
	}

%>
</body>
</html>



