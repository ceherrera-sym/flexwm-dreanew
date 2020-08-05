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
<%@page import="com.symgae.server.SFServerUtil"%>
<%@page import="com.flexwm.server.FlexUtil"%>
<%@page import="com.flexwm.server.AmountByWord"%>
<%@page import="com.flexwm.server.AmountByWorden"%>
<%@page import="com.flexwm.server.AmountByWordes"%>
<%@page import="com.flexwm.server.IAmountByWord"%>
<%@page import="com.flexwm.server.NumberByWord"%>

<%@include file="../inc/login_opt.jsp" %>
<%
String title = "Contrato de Lotes ECR";

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

		//Cliente
		BmoCustomer bmoCustomer = new BmoCustomer();
		PmCustomer pmCustomer = new PmCustomer(sFParams);
		bmoCustomer = (BmoCustomer)pmCustomer.get(bmoPropertySale.getCustomerId().toInteger());
		
		//Vivienda
		BmoProperty bmoProperty = new BmoProperty();
		PmProperty pmProperty = new PmProperty(sFParams);
		bmoProperty = (BmoProperty)pmProperty.get(bmoPropertySale.getPropertyId().toInteger());
		
		//Modelo
		BmoPropertyModel bmoPropertyModel = new BmoPropertyModel();
		PmPropertyModel pmPropertyModel = new PmPropertyModel(sFParams);
		bmoPropertyModel = (BmoPropertyModel)pmPropertyModel.get(bmoPropertySale.getBmoProperty().getPropertyModelId().toInteger());
		
		//Desarrollo
		BmoDevelopment bmoDevelopment = new BmoDevelopment();
		PmDevelopment pmDevelopment = new PmDevelopment(sFParams);
		bmoDevelopment= (BmoDevelopment)pmDevelopment.get(bmoPropertySale.getBmoProperty().getBmoDevelopmentBlock().getBmoDevelopmentPhase().getDevelopmentId().toInteger());
		
		//Etapa Desarrollo
		BmoDevelopmentPhase bmoDevelopmentPhase = new BmoDevelopmentPhase();
		PmDevelopmentPhase pmDevelopmentPhase = new PmDevelopmentPhase(sFParams);
		bmoDevelopmentPhase= (BmoDevelopmentPhase)pmDevelopmentPhase.get(bmoProperty.getBmoDevelopmentBlock().getDevelopmentPhaseId().toInteger());				
		
		//Ciudad del Desarrollo
		BmoCity bmoCityDevelopment = new BmoCity();
		PmCity pmCityDevelopment = new PmCity(sFParams);
		bmoCityDevelopment = (BmoCity)pmCityDevelopment.get(bmoPropertyModel.getBmoDevelopment().getCityId().toInteger());
		
		//Empresa
		BmoCompany bmoCompany = new BmoCompany();
		PmCompany  pmCompany  = new PmCompany (sFParams);
		bmoCompany = (BmoCompany)pmCompany.get(bmoProperty.getCompanyId().toInteger());
		
		// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
		String logoURL = "";
		if (!bmoCompany.getLogo().toString().equals(""))
			logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
		else
			logoURL = sFParams.getMainImageUrl();

		// Cuentas de banco
		BmoBankAccount bmoBankAccount = new BmoBankAccount();
		PmBankAccount pmBankAccount = new PmBankAccount(sFParams);
		if (bmoDevelopmentPhase.getBankAccountId().toInteger() > 0)
			bmoBankAccount = (BmoBankAccount) pmBankAccount
					.get(bmoDevelopmentPhase.getBankAccountId().toInteger());

		//Ciudad de la Empresa
		BmoCity bmoCityCompany = new BmoCity();
		PmCity pmCity = new PmCity(sFParams);
		bmoCityCompany = (BmoCity) pmCity.get(bmoCompany.getCityId().toInteger());

		//Direcciones del Cliente
		PmConn pmConnCustomer = new PmConn(sFParams);
		pmConnCustomer.open();
		boolean cuad = false;
		BmoCustomerAddress bmoCustomerAddress = new BmoCustomerAddress();
		PmCustomerAddress pmCustomerAddress = new PmCustomerAddress(sFParams);
		String sqlCustAddress = " SELECT * FROM customeraddress WHERE cuad_customerid = "
				+ bmoPropertySale.getBmoCustomer().getId();
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
		<td class="reportTitleCell" style="text-align:right; font-size: 9px">
			Clave Doc:<br>FO-07.5.2.1-3<br><%= bmoPropertySale.getCode().toString() %>
		</td>
	</tr>
</table>
<br>

<p align="justify" >
	<b>
		CONTRATO DE PROMESA DE COMPRA-VENTA CON RESERVA DE DOMINIO QUE CELEBRAN POR UNA PARTE LA SOCIEDAD MERCANTIL
		DENOMINADA <%= bmoCompany.getLegalName().toString().toUpperCase() %>,
		REPRESENTADA EN ESTE ACTO POR SUS REPRESENTANTE LEGAL, <%= bmoCompany.getLegalRep().toString().toUpperCase() %>
		QUIEN EN LO SUCESIVO SE DENOMINAR&Aacute; EL "VENDEDOR", Y POR OTRA PARTE
			(<%= bmoCustomer.getCode().toString().toUpperCase() %>)
		    <%= bmoCustomer.getDisplayName().toString().toUpperCase() %>
		
		A QUIEN EN LO SUCESIVO SE DENOMINAR&Aacute; COMO EL "COMPRADOR", EL CUAL SUJETAN AL TENOR DE LOS SIGUIENTES
		DECLARACIONES Y CL&Aacute;USULAS:
	</b>
</p>

<p align="center" >
    <b>D E C L A R A C I O N E S:</b>
</p>


<p align="justify" >
    <b>I.- Declara el representante Legal de la sociedad mercantil <b><%= bmoCompany.getLegalName().toString().toUpperCase() %></b>, El <b>"VENDEDOR"</b>:</b>

    <ol type="a" style="text-align:justify;">
        <li >
            Que su representada es una sociedad legalmente constituida de conformidad con la legislaci&oacute;n mexicana de la materia,
            seg&uacute;n consta en la Escritura P&uacute;blica n&uacute;mero 
            <%= bmoCompany.getDeedNumber().toString().toUpperCase() %> 
            de fecha <%= bmoCompany.getDeedDate().toString().toUpperCase() %>, 
            protocolizada ante la fe del Notario P&uacute;blico n&uacute;mero <%= bmoCompany.getDeedNotaryNumber().toString().toUpperCase() %> 
            misma que se encuentra inscrita en el Registro P&uacute;blico de la Propiedad y de Comercio del Estado de Guanajuato, bajo el folio mercantil electr&oacute;nico 
            n&uacute;mero <%= bmoPropertySale.getBmoProperty().getBmoDevelopmentBlock().getBmoDevelopmentPhase().getElectronicFolio().toString()%>.
        </li>
        <br>
        <li>
            Que su representada se encuentra en quieta, p&uacute;blica y pac&iacute;fica posesi&oacute;n de los bienes inmuebles materia de esta operaci&oacute;n,
            ubicados en el municipio de <%= bmoCityDevelopment.getName().toString().toUpperCase() %> del estado de <%= bmoCityDevelopment.getBmoState().getName().toString().toUpperCase() %>, en donde actualmente desarrolla un fraccionamiento mixto
            (residencial y comercial) denominado como "<%= bmoDevelopmentPhase.getName().toString().toUpperCase() %>", autorizado como tal por las autoridades
            estatales y municipales competentes. Asimismo, en su calidad de
            desarrollador Fidecomitente y Fideicomisaria B del Fideicomiso identificado con el n&uacute;mero 
            <%= bmoPropertySale.getBmoProperty().getBmoDevelopmentBlock().getBmoDevelopmentPhase().getFideicomisoNumber().toString() %>
            en donde es Fiduciaria la instituci&oacute;n de cr&eacute;dito BANCO DEL BAJIO, SOCIEDAD AN&Oacute;NIMA, INSTITUCI&Oacute;N DE BANCA
            M&Uacute;LTIPLE, DIVISI&Oacute;N FIDUCIARIA, cuenta con la capacidad y derecho para celebrar contratos de promesas de
            compraventa y recibir dep&oacute;sitos en garant&iacute;a y anticipos respecto de los lotes de terreno propios del
            Fraccionamiento residencial referido, as&iacute; como para dar instrucciones a la fiduciaria para que se escrituren a
            favor de los adquirentes que hayan pagado su precio.
        </li>
        <br>
        <li>
            Que forma parte del Desarrollo Inmobiliario denominado <%= bmoDevelopmentPhase.getName().toString().toUpperCase() %>, 
            sujeto a R&eacute;gimen de Fraccionamiento, el inmueble identificado como Lote 
            <%= bmoProperty.getLot().toString().toUpperCase() %>, 
            <%= bmoProperty.getBmoDevelopmentBlock().getName().toString().toUpperCase() %>,
            de la calle <b><%= bmoPropertySale.getBmoProperty().getStreet().toString().toUpperCase() %></b> 
            con N&uacute;mero Oficial <b><%= bmoProperty.getNumber().toString().toUpperCase() %></b>, y con una superficie de
            <%= bmoProperty.getLandSize().toString().toUpperCase() %> m2, con las siguientes medidas y colindancias:
            <br>
            <%= bmoProperty.getAdjoins().toString()%>
        </li>
    </ol>
</p>

<p align="justify" >
    <b>
	    II.- Declara el Sr.(a) <b>
	    <%= bmoCustomer.getDisplayName().toString().toUpperCase() %>,
	</b> El <b>"COMPRADOR"</b>:</b>
    <ol type="a" style="text-align:justify;">
        <li>
           Ser mexicano mayor de edad, con registro federal de contribuyente <b><%= bmoCustomer.getRfc().toString().toUpperCase() %></b>.
        </li>
        <li>
            Que goza de las facultades necesarias y suficientes para celebrar el presente contrato y obligarse en los t&eacute;rminos y condiciones del mismo.
        </li>
        <li>
            Que es su deseo celebrar el presente contrato y, en sus t&eacute;rminos, obligarse a suscribir en el futuro un contrato definitivo de compraventa
            respecto del inmueble descrito en el inciso d) de la declaraci&oacute;n que antecede.
        </li>
    </ol>
</p>

<p align="justify" >
    <b>III.- Declaran ambas</b> partes que se reconocen mutuamente su personalidad y que es de su intenci&oacute;n celebrar el
    presente contrato de promesa de compra-venta con reserva de dominio y condici&oacute;n suspensiva en base a las siguientes.
</p>

<p align="center" >
    <b>C L &Aacute; U S U L A S:</b>
</p>

<p align="justify" >
    <b>PRIMERA.- </b> - El <b>"VENDEDOR"</b> se compromete a vender y el <b>"COMPRADOR"</b> se compromete a comprar en forma real y 
    definitiva el lote de terreno para construir una vivienda ubicado en la
    <b>"<%= bmoProperty.getBmoDevelopmentBlock().getName().toString().toUpperCase() %>,"</b>
    lote/fracci&oacute;n <b><%= bmoProperty.getLot().toString().toUpperCase() %></b> 
    de la calle <b><%= bmoProperty.getStreet().toString().toUpperCase() %></b> 
    con n&uacute;mero oficial <b><%= bmoProperty.getNumber().toString().toUpperCase() %></b>
    en el fraccionamiento <b><%= bmoDevelopmentPhase.getName().toString().toUpperCase() %></b> 
    en la ciudad de <b><%= bmoCityDevelopment.getName().toString().toUpperCase() %>,  <%= bmoCityDevelopment.getBmoState().getName().toString().toUpperCase() %></b>.
</p>
    
<p align="justify" >
    <b> SEGUNDA.- </b>  - El <b>"COMPRADOR"</b>  conviene en pagar al <b>"VENDEDOR"</b>  y que se fija de com&uacute;n acuerdo como precio del inmueble,
    la cantidad correspondiente a <b> <%= SFServerUtil.formatCurrency(bmoOrder.getTotal().toDouble()) %></b> (<%= amountByWord.getMoneyAmountByWord(bmoOrder.getTotal().toDouble()).toUpperCase() %>), 
    cantidad que deber&aacute; estar cubierta a m&aacute;s tardar el d&iacute;a de la firma de las escrituras de compra-venta ante el 
    NOTARIO P&Uacute;BLICO que para este efecto designe el <b>"VENDEDOR"</b>. La forma de pago se establece a continuaci&oacute;n:
</p>

    <%
	PmConn pmConnRaccW = new PmConn(sFParams);
    String sqlRaccW;
    sqlRaccW= " SELECT racc_raccountid, racc_invoiceno, racc_total, racc_duedate,racc_description, payt_name " +
    			" FROM raccounts " + 
	    		" LEFT JOIN raccounttypes ON(ract_raccounttypeid = racc_raccounttypeid) " +
	    		" LEFT JOIN paymenttypes ON(payt_paymenttypeid = racc_paymenttypeid) " +
	    		" WHERE racc_orderid = " + bmoPropertySale.getOrderId().toInteger() + 
	    		" AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" +
	    		" ORDER BY racc_duedate ASC, racc_invoiceno ASC" ;
    
    //System.out.println("sqlRaccW: "+sqlRaccW);
    pmConnRaccW.open();
    pmConnRaccW.doFetch(sqlRaccW);
%>
<table width="95%" align="center" border="0" cellpadding="0" cellspacing="0" style="font-size: 12px">
	<tr>
		<th class="reportHeaderCell">Documento</th>
	    <th class="reportHeaderCell">Descripci&oacute;n</th>
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
				        	&nbsp;<%= racc%>&nbsp;&nbsp;<%= pmConnRaccW.getString("racc_invoiceno")%>
			    		</td>
			    		<td class="reportCellEven" align = "left">
				        	&nbsp;<%= pmConnRaccW.getString("racc_description")%>
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


<!-- A COMO EN COVENT
<ol type="1" >
    <li align="justify">
    	El <b>"VENDEDOR"</b> recibir&aacute; como enganche por parte del <b>"COMPRADOR"</b> la cantidad de 
    	<b><%//= SFServerUtil.formatCurrency(boAllocation.getDownPayment().toDouble()) %> 
    	(<%//= amountByWord.getMoneyAmountByWord(boAllocation.getDownPayment().toDouble()).toUpperCase() %>)</b>
        Que servir&aacute; como apartado del inmueble antes mencionado.
    </li>
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
            inmueble , el <b>"COMPRADOR"</b> conviene en pagar la diferencia resultante de
            <b>FALTA<%//= SFServerUtil.formatCurrency(boAllocation.getTotalPrice().toDouble() - boAllocation.getCreditAmount().toDouble() - boAllocation.getDownPayment().toDouble()) %></b> 
            (<b><%//= amountByWord.getMoneyAmountByWord(boAllocation.getTotalPrice().toDouble() - boAllocation.getCreditAmount().toDouble() - boAllocation.getDownPayment().toDouble()).toUpperCase() %></b>)<b>
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
-->


<p align="justify" >
	<b>TERCERA.- </b> - 
	
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
    <b>CUARTA.- </b> Convienen ambas partes de este contrato de compra-venta que el pago no oportuno de las cantidades pactadas en el presente contrato de 
    compra-venta <b>(Anexo 5)</b> generar&aacute; un inter&eacute;s mensual del <b>3% (tres por ciento)</b>, que el <b>"COMPRADOR"</b> pagar&aacute; al <b>"VENDEDOR"</b>, 
    durante el tiempo que dure la mora. 
    Adicionando a lo anterior, el <b>"COMPRADOR"</b> autoriza expresamente al <b>"VENDEDOR"</b> a modificar el inmueble materia de la operaci&oacute;n, 
    designada por el <b>"VENDEDOR"</b> de forma unilateral, con las mismas caracter&iacute;sticas y el mismo precio de el lote contratada originalmente.
</p>

<p align="justify" >
    <b>QUINTA.- </b>
    La falta de pago de 3 (TRES) mensualidades consecutivas dejar&aacute; inexistente este contrato, el <b>"VENDEDOR"</b> podr&aacute; rescindir el mismo de manera unilateral 
    sin responsabilidad alguna, aplicando la pena convencional establecida en el presente instrumento.  El <b>"VENDEDOR"</b> estar&aacute; en posibilidad de vender 
    nuevamente el inmueble materia de la operaci&oacute;n, dejando sin posibilidad al <b>"COMPRADOR"</b> de exigir devoluci&oacute;n de las cantidades de que hubiera hecho entrega,
    descontando el <b>"VENDEDOR"</b>  de estas lo equivalente a la pena convencional y los respectivos intereses ordinarios y moratorios.
</p>

<p align="justify" >
    <b>SEXTA.- </b>
    En caso de que el <b>"COMPRADOR"</b> llegase a cancelar el presente contrato de promesa de compra-venta en forma unilateral, 
    deber&aacute; dar aviso por escrito al <b>"VENDEDOR"</b>, y acepta el <b>"COMPRADOR"</b> pagar al <b>"VENDEDOR"</b> por concepto de da&ntilde;os 
    y perjuicios producidos por el incumplimiento de la operaci&oacute;n de compra-venta y dentro de los siguientes 30 treinta d&iacute;as naturales, 
    la pena convencional establecida la cl&aacute;usula D&eacute;cima Segunda del presente instrumento.
</p>

<p align="justify" >
    <b>S&Eacute;PTIMA.- </b>
   	Si el <b>"VENDEDOR"</b> no cumple con la entrega (deslinde) del lote objeto de este contrato, sus amenities y vialidades correspondientes a la 
   	primera etapa en un per&iacute;odo m&aacute;ximo de 12 meses contados a partir de la firma del presente contrato, el <b>"VENDEDOR"</b> se obliga a 
   	devolver a el <b>"COMPRADOR"</b> los importes depositados, actualizados a una tasa de CETES desde la fecha en que cada uno fue recibido hasta 
   	la fecha en que aplique por esta cl&aacute;usula. La entrega de &eacute;ste importe se har&aacute; exclusivamente a nombre del <b>"COMPRADOR"</b> en un plazo no 
   	mayor a 60 d&iacute;as naturales mediante (cheque o transferencia electr&oacute;nica).
</p>


<p align="justify" >
    <b> OCTAVA.- </b>
    Ambas partes acuerdan, que una vez que el 51% (cincuenta y un por ciento) del total de 101 (ciento un) lotes que comprenden la primer etapa, 
    se encuentren con el deslinde respectivo, el <b>"VENDEDOR"</b>,  podr&aacute; entregar las facultades administrativas del Fraccionamiento a la Asociaci&oacute;n de Colonos 
    o bien al Comit&eacute; que estos establezcan para tal efecto. 
    En el entendido que la primer etapa la componen los siguientes Lotes y Manzanas:
    <table style="font-size: 12px" width="40%" align="center">
    	<tr class="reportHeaderCell">
	    	<th align="center">Manzana</th>
	    	<th align="center">Lotes</th>
	    	<th align="center">Total de lotes</th>
	    </tr>
	    <tr>
	    	<td class="reportCellEven" align="center">12</td>
	    	<td class="reportCellEven" align="center">Lote 33 al lote 50</td>
	    	<td class="reportCellEven" align="center">18</td>
	    </tr>
	    <tr>
	    	<td class="reportCellEven" align="center">14</td>
	    	<td class="reportCellEven" align="center">Lote 1 al lote 12</td>
	    	<td class="reportCellEven" align="center">12</td>
	    </tr>
	    <tr>
	    	<td class="reportCellEven" align="center">15</td>
	    	<td class="reportCellEven" align="center">Lote 1 al lote 26</td>
	    	<td class="reportCellEven" align="center">26</td>
	    </tr>
	    <tr>
	    	<td class="reportCellEven" align="center">16</td>
	    	<td class="reportCellEven" align="center">Lote 1 al lote 20</td>
	    	<td class="reportCellEven" align="center">20</td>
	    </tr>
	    <tr>
	    	<td class="reportCellEven" align="center">17</td>
	    	<td class="reportCellEven" align="center">Lote 1 al lote 25</td>
	    	<td class="reportCellEven" align="center">25</td>
	    </tr>
    </table>
</p>
    
<p align="justify" >
    <b>NOVENA.- </b>
    El <b>"VENDEDOR"</b> se obliga a transmitir a el <b>"COMPRADOR"</b> el inmueble materia del presente contrato de promesa de compra-venta, 
    libre de todo gravamen, sin limitaciones de dominio y al corriente en el pago de sus contribuciones.
</p>

<p align="justify" >
    <b> D&Eacute;CIMA.- </b>
    El <b>"VENDEDOR"</b> se compromete con el <b>"COMPRADOR"</b> a celebrar la escritura definitiva de compraventa del inmueble materia de la operaci&oacute;n en su favor, 
    en el momento que el inmueble se encuentre en la posibilidad jur&iacute;dica para celebrar dicho acto, comprendiendo para este supuesto  un lapso no 
    mayor a <b>60 d&iacute;as naturales</b> posteriores a la fecha en que se hayan cumplido las condiciones a que se oblig&oacute; el <b>"COMPRADOR"</b> en t&eacute;rminos de la 
    cl&aacute;usula segunda y el <b>(Anexo 5)</b> del presente instrumento, y &eacute;ste no haya omitido cantidad alguna en los pagos, y en su caso, en la realizaci&oacute;n 
    de los dep&oacute;sitos adicionales que deba realizar por haber incurrido en mora en cualquier exhibici&oacute;n que deba realizar conforme se oblig&oacute;, y el 
    <b>"VENDEDOR"</b> haya aplicado dichos dep&oacute;sitos al pago del precio y, en su caso, a los conceptos que convengan para los dep&oacute;sitos complementarios y adicionales.
</p>

<p align="justify" >
    <b> D&Eacute;CIMA PRIMERA.- </b>
    El <b>"COMPRADOR"</b> se obliga desde este momento a respetar y sujetarse a las condicionantes de operaci&oacute;n que se&ntilde;ale la administraci&oacute;n 
    en el <b>Anexo 3</b> del presente contrato, creado para el buen funcionamiento del Fraccionamiento Inmobiliario denominado <b><%= bmoDevelopmentPhase.getName().toString().toUpperCase() %></b>, 
    si es el caso que se formalizara la escritura de compra-venta que las partes se obligaron a celebrar en t&eacute;rminos del presente contrato.
</p>

<p align="justify" >
    No obstante lo anterior, el <b>"COMPRADOR"</b> se obliga a pagar a la Administradora del Fraccionamiento en el momento en que le sea deslindado el 
    inmueble objeto del presente contrato, el importe mensual por pago de cuota de mantenimiento. As&iacute; mismo, se obliga al pago por adelantado del 
    equivalente a <b>3 (tres) cuotas</b>  concepto de mantenimiento, al momento que la casa objeto del terreno del presente contrato est&eacute; en posibilidad 
    de habitarse y con ello se comience a hacer uso de los equipamientos y casa Club. En ese momento, se pagar&aacute;n estas cuotas por &uacute;nica vez para 
    habilitar el uso de las mismas y se fijar&aacute;n por parte de la  Administradora del Fraccionamiento.
</p>

<p align="justify" >
	Para el caso de que el <b>"COMPRADOR"</b> no cumpla con efectuar el pago de las cuotas de mantenimiento referidas, desde este momento y de manera irrevocable 
	se autoriza al el <b>"VENDEDOR"</b> a aplicar el importe de las mismas contra los anticipos. La aplicaci&oacute;n habr&aacute; de realizarse y contabilizarse con efectos 
	a partir del d&iacute;a inmediato siguiente al vencimiento del plazo convenido para el pago y en caso de quedar un saldo insoluto el <b>"COMPRADOR"</b> pagar&aacute; 
	sobre cada cuota mensual insoluta por concepto de pena convencional intereses moratorios de $30.00 treinta pesos diarios a la Administraci&oacute;n del Fraccionamiento. 
	El <b>"COMPRADOR"</b> deber&aacute; realizar los pagos directamente a la Administraci&oacute;n, sin descontar las cuotas pendientes de pago de los dep&oacute;sitos acumulados.
</p>

<p align="justify" >
	El pago de las cuotas de mantenimiento a cargo del <b>"COMPRADOR"</b> de conformidad con el p&aacute;rrafo que antecede ser&aacute; definitivo; por lo tanto, 
	no se le devolver&aacute; ni aun cuando el presente contrato se cancele, rescinda o resuelva bajo cualquier concepto.
</p>

<p align="justify" >
    <b>D&Eacute;CIMA SEGUNDA.- </b>
    Las partes convienen que en caso de incumplimiento a cargo del <b>"COMPRADOR"</b> este ultimo, pagar&aacute; a el <b>"VENDEDOR"</b> el equivalente al <b>20% (VEINTE POR CIENTO)</b> 
    del valor del inmueble como pena convencional, y en caso de cancelaci&oacute;n del presente contrato, deber&aacute; pagar al "VENDEDOR" por concepto de pena convencional 
    el equivalente al <b>10% (DIEZ POR CIENTO)</b> del valor de compra-venta, siempre que se encuentre al corriente en el cumplimiento de las obligaciones 
    que asume conforme al presente contrato; penas que en uno u otro supuesto se aplicar&aacute;n sin necesidad de requerimiento o resoluci&oacute;n judicial o 
    extrajudicial previa del importe total de los dep&oacute;sitos principales en garant&iacute;a que el <b>"COMPRADOR"</b> se oblig&oacute; realizar conforme a la cl&aacute;usula 
    tercera y el anexo del presente contrato, autorizando el <b>"COMPRADOR"</b> al <b>"VENDEDOR"</b> desde este momento a disponer directamente de las cantidades 
    que le hubiere depositado pactadas en la cl&aacute;usula tercera del presente contrato, en pago de las penas convencionales referidas.
    
    <br><br>
    El <b>"COMPRADOR"</b> expresamente otorga al <b>"VENDEDOR"</b> un plazo de <b>60</b> d&iacute;as h&aacute;biles posteriores a la solicitud de cancelaci&oacute;n hecha por escrito, 
    para hacer la devoluci&oacute;n del remanente del dep&oacute;sito una vez descontada la pena convencional.

</p>

<p align="justify" >
    <b>D&Eacute;CIMA TERCERA.-</b>
    En virtud de que el presente contrato constituye una promesa de compra venta, su sola suscripci&oacute;n no transfiere al <b>"COMPRADOR"</b> el dominio ni la 
    posesi&oacute;n jur&iacute;dica del inmueble objeto de este contrato, por lo que el <b>"VENDEDOR"</b> se reserva expresamente el dominio y por tanto la posesi&oacute;n jur&iacute;dica 
    y f&iacute;sica del inmueble objeto de este contrato y de la promesa de compraventa que se constituye en el mismo, hasta en tanto no se celebre el contrato 
    definitivo de compraventa.
</p>

<p align="justify" >
    <b>D&Eacute;CIMA CUARTA.- </b>
    Los gastos, honorarios, aval&uacute;os, derechos e impuestos que se causar&aacute;n por la escritura en la que se formalizara la compra-venta definitiva, 
    ser&iacute;an cubiertos en forma exclusiva por el <b>"COMPRADOR"</b>, con excepci&oacute;n del impuesto sobre la Renta que liquidar&aacute; el <b>"VENDEDOR"</b>.
</p>

<p align="justify" >
	El <b>"COMPRADOR"</b> conviene en cubrir el importe correspondiente al impuesto predial que devengue el lote descrito en la Declaraci&oacute;n I, inciso c), 
	del presente instrumento, a partir de que se le entregue la posesi&oacute;n f&iacute;sica del mismo y/o que se firmen las escrituras definitivas de compra venta, lo que ocurra primero.
</p>


<p align="justify" >
    <b>D&Eacute;CIMA QUINTA.-</b>
    Ambas partes contratantes declaran que en este convenio no existe error, dolo, enga&ntilde;o o violencia alguna por lo que rec&iacute;procamente 
    renuncian a las acciones de nulidad, que pudieran fundarse en dicha causa, y al texto mismo de las disposiciones aplicables contenidas 
    en el C&oacute;digo Civil Vigente en el Estado de Guanajuato, M&eacute;xico.
</p>

<p align="justify" >
    <b>D&Eacute;CIMA SEXTA.-</b>
    Para la interpretaci&oacute;n, cumplimiento, y, ejecuci&oacute;n del presente contrato de promesa de compra-venta ambas partes se someten a las Leyes 
    del ESTADO DE GUANAJUATO, y a los Tribunales de esta Ciudad LE&Oacute;N, renunciando a cualquier otro fuero de sus presentes o futuros domicilios.
</p>

<p align="justify" >
    <b>D&Eacute;CIMA S&Eacute;PTIMA.-</b>
    Para todos los efectos judiciales y extrajudiciales que pudieran derivarse del presente contrato las partes se&ntilde;alan como sus domicilios convencionales los siguientes:
</p>

<p align="justify" >
    &nbsp;&nbsp;&nbsp;
    1.- El <b>"COMPRADOR"</b>.
    <%= bmoCustomerAddress.getStreet().toString().toUpperCase() %>,
    <%= bmoCustomerAddress.getNumber().toString().toUpperCase() %>,
    <%= bmoCustomerAddress.getNeighborhood().toString().toUpperCase() %>, de la ciudad de
    <%= bmoCustomerAddress.getBmoCity().getName().toString().toUpperCase() %>, <%= bmoCustomerAddress.getBmoCity().getBmoState().getName().toString().toUpperCase() %>.
    <br>
    &nbsp;&nbsp;&nbsp;
    2.- El <b>"VENDEDOR"</b>.
    <%= bmoCompany.getStreet().toString().toUpperCase() %>,
    <%= bmoCompany.getNumber().toString().toUpperCase() %>,
    <%= bmoCompany.getNeighborhood().toString().toUpperCase() %>, de la ciudad de
    <%= bmoCityCompany.getName().toString().toUpperCase() %>, <%= bmoCityCompany.getBmoState().getName().toString().toUpperCase() %>.
</p>
<p align="justify" >
    Mientras las partes no se notifiquen en forma indubitable el cambio de su domicilio, todas las notificaciones, citaciones y
    emplazamientos se practicar&aacute;n en los domicilios se&ntilde;alados.
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
	Los anexos <b>1 (Especificaciones), 2 (Pagar&eacute;s), 3 (Anexo referente a la ley de lavado de dinero), 
	4 (Reglamento de construcci&oacute;n), 5 (Corrida financiera en operaciones con cr&eacute;dito directo), 6 (Lugar y forma de pago), 
	7 (Reglamento de uso y convivencia de la casa club)</b> forman parte de &eacute;ste contrato como si a la letra se insertase.
</p>

<p align="justify" >
	EL PRESENTE CONTRATO DE PROMESA DE COMPRA-VENTA SE FIRMA EN LA CIUDAD DE
	<b><%= bmoCityCompany.getName().toString().toUpperCase() %></b>, 
	<b><%= bmoCityCompany.getBmoState().getName().toString().toUpperCase() %></b>,  
	A <b><%= SFServerUtil.nowToString(sFParams, "dd") %> DE <%= nowMonth.toUpperCase() %>,  <%= SFServerUtil.nowToString(sFParams, "YYYY") %></b>
</p>

<p align="justify" >
	NOTAS:  <b><%= bmoOrder.getDescription().toString().toUpperCase() %></b>
</p>

<table align="center" border="0" cellspacing="0" width="70%" cellpadding="0" style="font-size: 12px">
    <tr>
        <td align="center">
	       	&nbsp;			        	
        </td>
        <td align="center">
<!-- 			<img src="../img/firma_lopez.jpg" width="60" height="30"> -->
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
            <b>REPRESENTANTE LEGAL</b>
        </td>
    </tr>
    <tr>
        <td align="center">
            <b>
        		<%= bmoCustomer.getDisplayName().toString().toUpperCase() %>
            </b>
        </td>
        <td align="center">
	        <b><%= bmoCompany.getLegalRep().toString().toUpperCase() %></b><br>
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
    <b>EN EL QUE SE DEFINE EL INMUEBLE SUJETA POR EL PRESENTE CONTRATO DE PROMESA DE COMPRA-VENTA.</b>
    ESTE DOCUMENTO NO TIENE VALIDEZ SIN SU CONTRAPARTE EL CONTRATO DE PROMESA DE COMPRAVENTA FIRMADO POR EL <b>"VENDEDOR"</b> Y EL <b>"COMPRADOR"</b>.
</p>
    
<table border="0" cellspacing="0" width="90%" cellpadding="0" style="font-size: 12px" align="center">
    <tr>
        <th class="reportCellEven" align="left">FRACCIONAMIENTO:</th>
        <td class="reportCellEven" align="left"><%= bmoDevelopmentPhase.getName().toString().toUpperCase() %></td>
    </tr>
    <tr>
        <th class="reportCellEven" align="left">MANZANA/FRACCI&Oacute;N:</th>
        <td class="reportCellEven" align="left"><%= bmoProperty.getBmoDevelopmentBlock().getCode().toString().toUpperCase() %></td>
    </tr>
    <tr>
        <th class="reportCellEven" align="left">LOTE/FRACCI&Oacute;N:</th>
        <td class="reportCellEven" align="left"><%= bmoProperty.getLot().toString().toUpperCase() %></td>
    </tr>
    <tr>
        <th class="reportCellEven" align="left">CALLE:</th>
        <td class="reportCellEven" align="left"><%= bmoProperty.getStreet().toString().toUpperCase() %></td>
    </tr>
    <tr>
        <th class="reportCellEven" align="left">N&Uacute;MERO OFICIAL:</th>
        <td class="reportCellEven" align="left"><%= bmoProperty.getNumber().toString().toUpperCase() %></td>
    </tr>
    <tr>
        <th class="reportCellEven" align="left">MODELO:</th>
        <td class="reportCellEven" align="left"><%= bmoPropertyModel.getName().toString().toUpperCase() %></td>
    </tr>
    <tr>
        <th class="reportCellEven" align="left">DESCRIPCI&Oacute;N:</th>
        <td class="reportCellEven" align="left"><%= bmoPropertyModel.getHighLights().toString().toUpperCase() %></td>
    </tr>

    <tr>
        <th class="reportCellEven" align="left">SUPERFICIE DEL TERRENO:</th>
        <td class="reportCellEven" align="left"><%= bmoProperty.getLandSize().toString().toUpperCase() %> mt2
        (<%= amountByWord.getAmountByWord(bmoProperty.getLandSize().toDouble()).toUpperCase() %> METROS CUADRADOS)
        </td>
    </tr>

</table>

<table border="0" cellspacing="0" width="70%" cellpadding="0" style="font-size: 12px" align="center">
    <tr>
        <td align="center">
	       	&nbsp;
        </td>
        <td align="center">
<!-- 			<img src="../img/firma_lopez.jpg" width="60" height="30" > -->
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
            <b>REPRESENTANTE LEGAL</b>
        </td>
    </tr>
    <tr>
        <td align="center">
            <b>
        		<%= bmoCustomer.getDisplayName().toString().toUpperCase() %>
            </b>
        </td>
        <td align="center">
        	<b><%= bmoCompany.getLegalRep().toString().toUpperCase() %></b><br>        
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
<br>
<p align="center" style="font-size: 8pt" >
	<b>
	<%= bmoCityCompany.getName().toString().toUpperCase() %>, 
	<%= bmoCityCompany.getBmoState().getName().toString().toUpperCase() %>,  
	A <%= FlexUtil.dateToLongDate(sFParams, bmoPropertySale.getStartDate().toString()).toUpperCase()%>

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

<p align="justify" >
	<b>
		<%= bmoCityCompany.getName().toString().toUpperCase() %>, 
		<%= bmoCityCompany.getBmoState().getName().toString().toUpperCase() %>,  
		A <%= FlexUtil.dateToLongDate(sFParams, bmoPropertySale.getStartDate().toString()).toUpperCase()%>
		<br>
		<%= bmoCompany.getLegalName().toString().toUpperCase() %>
	
		<br>
		PRESENTE <br><br><br>
		MEDIANTE EL PRESENTE ANEXO DECLARO TENER CONOCIMIENTO DE QUE EN EL SUPUESTO CASO DE NO PAGAR EN TIEMPO Y FORMA
	    CUALQUIERA DE LOS DOCUMENTOS POR COBRAR (PAGAR&Eacute;S) FIRMADOS POR MI PERSONA Y QUE REPRESENTAN EL PAGO DEL ENGANCHE
	    DE LA VIVIENDA QUE ESTOY ADQUIRIENDO, ACEPTO QUE EN ESTE SUPUESTO PERDER&Eacute; LA ASIGNACI&Oacute;N Y UBICACI&Oacute;N DEL INMUEBLE
	    ELEGIDO Y ESPECIFICADO EN EL PRESENTE CONTRATO DE COMPRA VENTA.
	    <br><br>
	    A CONTINUACI&Oacute;N SE LISTAN LOS DOCUMENTOS POR COBRAR DEL ENGANCHE:
    </b>
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
						&nbsp;<%= i%>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%= pmConnRacc.getString("racc_invoiceno")%>
					</td>
					<td class="reportCellEven" align = "left">
			        	<%= pmConnRacc.getString("payt_name")%>
		    		</td>
					<td class="reportCellEven" align = "left">
    					&nbsp;<%= FlexUtil.dateToLongDate(sFParams, pmConnRacc.getString("racc_duedate"))%>
					</td>
					<td class="reportCellEven" align = "right">
						&nbsp;<%= SFServerUtil.formatCurrency(pmConnRacc.getDouble("racc_total")) %>
					</td>	
				    <%
				      i += 1;
			}
		}
					%>			
				</tr>
</table>

<p align="justify" >
	<b>
		DE IGUAL FORMA ESTOY DE ACUERDO EN QUE LA NUEVA UBICACI&Oacute;N SE ME RE-ASIGNAR&Aacute; UNA VEZ QUE HAYA PAGADO LOS DOCUMENTOS POR COBRAR
		(PAGAR&Eacute;S) DEL ENGANCHE QUE A LA FECHA EST&Eacute;N VENCIDOS DE ACUERDO A LA FECHA DE PAGO.
		LA NUEVA UBICACI&Oacute;N SER&Aacute; ELEGIDA POR MI PERSONA, DE ACUERDO A LOS INMUEBLES DISPONIBLES EN EL MOMENTO DE LA NUEVA ASIGNACI&Oacute;N,
		PUDIENDO NO SER EL MISMO INMUEBLE QUE EL ESTABLECIDO EN EL PRESENTE CONTRATO DE COMPRA-VENTA,
		POR LO QUE SE ELABORAR&Aacute; UN NUEVO CONTRATO DE COMPRA VENTA.
	</b>
</p>
    
    
<table width="90%" align="center" border="0" cellpadding="0" cellspacing="0" style="font-size: 12px" >
    <tr>
        <td align="center">
	       	&nbsp;			        	
        </td>
        <td align="center">
<!-- 			<img src="../img/firma_lopez.jpg" width="60" height="30"> -->
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
            <b>REPRESENTANTE LEGAL</b>
        </td>
    </tr>
    <tr>
        <td align="center">
	        <b>	
        		<%= bmoCustomer.getDisplayName().toString().toUpperCase() %>
			</b>
        </td>
        <td align="center">
        	<b>
        		<%= bmoCompany.getLegalRep().toString().toUpperCase() %>
        	</b>
        	<br>
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
<p style="page-break-after: always"></p>

<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr>
		<td width="5%">
			<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
		</td>
		<td class="reportTitleCell">
			ANEXO 4 CONTRATO DE PROMESA DE COMPRA-VENTA:<br>REGLAMENTO DE CONSTRUCCI&Oacute;N
		</td>
		<td class="reportTitleCell" style="text-align:right; font-size: 9px">
			Clave Doc:<br>FO-07.5.2.1-3
		</td>
	</tr>
</table>

<p align="left" class="contractTitle">
	<b>INTRODUCCI&Oacute;N</b>
</p>

<p align="justify" >
	En un mundo que poco ofrece al esp&iacute;ritu, en el cual el aceleramiento y la vida mecanizada parecen marcar la mente y el derrotero del hombre,
	es necesario y conveniente retornar constantemente a las fuentes de la tranquilidad y de la reflexi&oacute;n, donde la
	armon&iacute;a sea nuestro ambiente y alimento diarios.
	<br><br>
	El Fraccionamiento &nbsp;<%= bmoDevelopmentPhase.getName().toString().toUpperCase() %>, se presenta forjador de espacios urbanos, donde el hombre pueda convivir en esa sana y
	necesaria armon&iacute;a con la naturaleza, y su hermano, el hombre.
	El presente reglamento interno de construcci&oacute;n es el instrumento propio de <%= bmoDevelopmentPhase.getName().toString().toUpperCase() %> para lograr el beneficio de crear
	los espacios apropiados para esa ansiada convivencia hombre-naturaleza.
	<br><br>
</p>

<p align="justify" class="contractTitle">
<b>CONTENIDO</b>
</p>

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


<p align="justify" >
	<b>I. OBJETIVO DEL REGLAMENTO</b>
	<br><br>
	<b>ART&Iacute;CULO 1</b><br>
	Con el objeto de propiciar la convivencia arm&oacute;nica de las familias habitantes del  fraccionamiento, especialmente de los ni&ntilde;os, desarrollar y
	conservar el medio ambiente y orientar a que las construcciones se realicen arm&oacute;nicamente para conformar un conjunto urbano integral, 
	se establece el presente Reglamento Interno de Construcci&oacute;n para el Fraccionamiento <%= bmoDevelopmentPhase.getName().toString().toUpperCase() %>
	Para que, al realizar construcciones, ampliaciones o modificaciones, se requerir&aacute; de la aprobaci&oacute;n expresa del 
	Comit&eacute; T&eacute;cnico de Construcci&oacute;n y/o la Administradora del Fraccionamiento. Las disposiciones de este Reglamento son complementarias 
	a las establecidas en el C&oacute;digo de Ordenamiento Territorial, Desarrollo Urbano y Vivienda para el Estado de
	<%= bmoCityDevelopment.getBmoState().getName().toString().toUpperCase() %>, C&oacute;digo Urbano de La Cd de 
	<%= bmoCityDevelopment.getName().toString().toUpperCase() %>, leyes y ordenamientos del Gobierno Federal, Gobierno del Estado de 
	<%= bmoCityDevelopment.getBmoState().getName().toString().toUpperCase() %>  y del Gobierno del Municipio de 
	<%= bmoCityDevelopment.getName().toString().toUpperCase() %>, en materia de construcciones, uso del suelo y ecolog&iacute;a.
</p>

<p align="justify" >
	<b>II. BASES JUR&Iacute;DICO-ADMINISTRATIVAS</b>
	<br><br>
	<b>ART&Iacute;CULO 2</b><br>
	El Reglamento Interno de Construcci&oacute;n del Fraccionamiento <%= bmoDevelopmentPhase.getName().toString().toUpperCase() %>,
	forma parte del Reglamento de la Administradora del Fraccionamiento acorde al proyecto aprobado para el fraccionamiento 
	<%= bmoDevelopmentPhase.getName().toString().toUpperCase() %>
	compuesto por 8 etapas  y ambos est&aacute;n debidamente registrados para su observancia en el Registro P&uacute;blico de la Propiedad,
	lo Direcci&oacute;n de Catastro, la Secretaria de Desarrollo Urbano del Municipio de <%= bmoCityDevelopment.getName().toString().toUpperCase() %> 
	y la Secretar&iacute;a de Gesti&oacute;n Urban&iacute;stica y Ordenamiento Territorial del Estado de 
	<%= bmoCityDevelopment.getBmoState().getName().toString().toUpperCase() %>. 
	Estas normas son obligatorias para la construcci&oacute;n de las viviendas en los predios y forman parte del compromiso de compra venta.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 3</b><br>
	El Comit&eacute; T&eacute;cnico de Construcci&oacute;n, de conformidad con los estatutos de la Administradora del Fraccionamiento,
	recibe autoridad para que el tr&aacute;mite de solicitud para la construcci&oacute;n se inicie con la presentaci&oacute;n del proyecto al 
	Comit&eacute; T&eacute;cnico de Construcci&oacute;n y/o la Administradora del Fraccionamiento y, una vez otorgado el Visto Bueno, 
	se solicita a Gobierno Municipal la licencia de construcci&oacute;n correspondiente.
</p>

<p align="justify" >
	<b>III. EL COMIT&Eacute; T&Eacute;CNICO DE CONSTRUCCI&Oacute;N </b>
	<br><br>
	<b>ART&Iacute;CULO 4</b><br>
	Con objeto de vigilar el cumplimiento del Reglamento interno de Construcci&oacute;n,
	la Administradora del Fraccionamiento designar&aacute; un Comit&eacute; T&eacute;cnico de Construcci&oacute;n integrado al menos de tres personas,
	dos de los cuales deber&aacute;n ser de profesi&oacute;n arquitecto o ingeniero civil y estar t&eacute;cnicamente calificados y registrados para ejercer profesionalmente.
	El Comit&eacute; T&eacute;cnico de Construcci&oacute;n se har&aacute; cargo de las siguientes tareas:
</p>

<ol type="1" style="text-align:justify;">
	<li>
		Coadyuvar con las Secretaria de Desarrollo Urbano Municipal y de Servicios P&uacute;blicos con sus Direcciones de Desarrollo Urbano, Ecolog&iacute;a,
	    Parques y Jardines, Obras y Servicios P&uacute;blicos del Municipio de Le&oacute;n en lo relacionado al Fraccionamiento.
	</li>
	<li>
		Recibir y revisar los proyectos de construcci&oacute;n que sometan a su consideraci&oacute;n los propietarios del Terreno del Fraccionamiento,
	    revisarlos y otorgar, en su caso, el Visto e Bueno de cumplimiento del Reglamento Interno de Construcci&oacute;n.
	</li>
	<li>
		Establecer vigilancia en el Fraccionamiento para evitar la tala de &aacute;rboles.
	</li>
	<li>
		Establecer vigilancia para evitar que se inicien construcciones sin Visto Bueno y sin Licencia Municipal, que se construyan barracas,
	    anuncios o cualquier tipo de estructura en los predios.
	</li>
	<li>
		Vigilar que no se depositen materiales de construcci&oacute;n en las &aacute;reas comunes o en los predios que no tienen licencia de construcci&oacute;n**.
	</li>
	<li>
		Revisar constantemente el estado en que se encuentran los pavimentos, el ajardinada de los parques, el servicio de alumbrado p&uacute;blico,
	    y las redes de agua, drenaje, alcantarillado, tel&eacute;fono, gas y energ&iacute;a el&eacute;ctrica en el Fraccionamiento
	    <%= bmoDevelopmentPhase.getName().toString().toUpperCase() %> e informar a la Administradora del Fraccionamiento.
	</li>
	<li>
		Reportar las violaciones a este Reglamento Interno, Administradora del Fraccionamiento, Gobierno Municipal, y en su caso
	    a La Secretar&iacute;a de Gesti&oacute;n Urban&iacute;stica y Ordenamiento del Gobierno del Estado.
	</li>
</ol>

<p align="justify" >
	<b>ART&Iacute;CULO 5</b><br>
	El Gobierno Municipal de Le&oacute;n acuerda no tramitar la solicitud de licencia de construcci&oacute;n en los predios del
	Fraccionamiento <%= bmoDevelopmentPhase.getName().toString().toUpperCase() %>,
	si el proyecto no contiene eI Visto Bueno del Comit&eacute; T&eacute;cnico de Construcci&oacute;n y/o la Administradora del Fraccionamiento;
	en caso contrario lo har&aacute; saber al solicitante, suspendiendo su tr&aacute;mite hasta cumplir con este requisito.
</p>


<p align="justify" >
	<b>ART&Iacute;CULO 6</b><br>
	El Comit&eacute; T&eacute;cnico de Construcci&oacute;n  y/o la Administradora del Fraccionamiento reportar&aacute; al Gobierno del Municipio de 
	<%= bmoCityDevelopment.getName().toString().toUpperCase() %>,
	las violaciones que cometan los propietarios de los predios, a las disposiciones de uso del suelo y construcci&oacute;n establecidas en el
	Reglamento interno de Construcci&oacute;n, y por los gobiernos Federal, Estatal y Municipal.
</p>

<p align="justify" >
	<b>IV. PRESERVACI&Oacute;N ECOL&Oacute;GICA</b>
	<br><br>
	<b>ART&Iacute;CULO 7</b><br>
	Esta prohibida la tala o derribo de &aacute;rboles, El Comit&eacute; T&eacute;cnico de Construcci&oacute;n y/o la Administradora del Fraccionamiento reportar&aacute; 
	al propietario del predio en donde se talen o derriben &aacute;rboles ante la Administradora del Fraccionamiento y en su caso ante la autoridad
	municipal competente. Se deber&aacute; sembrar al menos un &aacute;rbol por cada predio y su cuidado estar&aacute; a cargo del propietario.
	Dado que las redes de energ&iacute;a el&eacute;ctrica, tel&eacute;fonos y especiales, ser&aacute;n subterr&aacute;neas, los &aacute;rboles ser&aacute;n 
	sembrados en el &aacute;rea de restricci&oacute;n de cada predio y no en los senderos, andadores o cualquier tipo de calles y circulaciones, 
	Trat&aacute;ndose de los &aacute;rboles de reciente plantaci&oacute;n, se evitar&aacute; moverlos del sitio de su ubicaci&oacute;n, salvo en casos de 
	extrema necesidad, justificada ante el Comit&eacute; T&eacute;cnico de Construcci&oacute;n y/o la Administradora del Fraccionamiento, quien resolver&aacute; 
	sobre su aprobaci&oacute;n.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 8</b><br>
	Las &aacute;reas libres destinadas a parques y los espacios jardinados de las &aacute;reas comunes deber&aacute;n preservarse con vegetaci&oacute;n y limpios de escombro y basura.
	El Comit&eacute; T&eacute;cnico de Construcci&oacute;n y/o la Administradora del Fraccionamiento cuidar&aacute; de que &eacute;stos trabajos se realicen hasta el momento de la
	entrega de cada una de las Etapas, para su operaci&oacute;n y mantenimiento.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 9</b><br>
	Para evitar el mal uso del terreno, el Comit&eacute; Tecnico de Construcci&oacute;n y/o la Administradora del Fraccionamiento vigilar&aacute; que no se
	realicen asentamientos irregulares o se utilicen para depositar materiales, chatarra, equipos elementos nocivos, basura u otros.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 10</b><br>
	No se permitir&aacute; el desag&uuml;e temporal o definitivo de los desechos de aguas negras o de aguas contaminadas a las &aacute;reas circunvecinas al Fraccionamiento.
	Para establecer alg&uacute;n sistema de uso del agua pluvial se deber&aacute; presentar el proyecto de este al Comit&eacute; T&eacute;cnico de Construcci&oacute;n y/o la
	Administradora del Fraccionamiento para su aprobaci&oacute;n.
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
	En el Fraccionamiento <%= bmoDevelopmentPhase.getName().toString().toUpperCase() %>
	se establecen tres zonas destinadas a los siguiente uso: residencial unifamiliar.
	Dentro de cada privada,  los usos ser&aacute;n: vivienda unifamiliar, estacionamiento de visitas y &aacute;reas verdes.
	Estas zonas s&oacute;lo podr&aacute;n ser utilizadas para el uso aprobado y las construcciones se ajustar&aacute;n a las disposiciones de este Reglamento interno,
	y las de construcci&oacute;n y uso del suelo del Estado y Municipio.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 14</b><br>
	En todos los predios se deber&aacute; dejar totalmente libre de construcci&oacute;n una franja de 6.00 metros
	al frente del predio en Planta Baja (desde el punto m&aacute;s largo al fondo del terreno), en Planta Alta la restricci&oacute;n
	ser&aacute; de 4.50 mts (desde el punto m&aacute;s largo al fondo del terreno); y 2.50 metros al fondo del mismo (libre de
	construcción).
</p>

<p align="center">
	<img border="0" width="70%" height="80%" src="<%= GwtUtil.getProperUrl(sFParams, "/img/lote_art14_2.png")%>">
</p>

<p align="justify" >
	En la restricci&oacute;n de 2.50 metros al fondo del predio se podr&aacute; construir un m&aacute;ximo de 8.00 x 2.50 metros en 
	planta baja, siempre y cuando se trate de un espacio separado de la vivienda, del mismo lado de la 
	construcci&oacute;n y que su cubierta sea a base de techos con altura m&aacute;xima de 2.40 metros a la c&uacute;spide. Lo anterior 
	a fin de evitar la continuidad de construcci&oacute;n y el uso de la planta alta, de manera que se conserven las 
	condiciones de luz, aire y espacio para la vivienda y las colindantes.
</p>

<p align="justify" >
	Por razones de seguridad ser&aacute;n exceptuados de la restricci&oacute;n de 2.50 metros al fondo, los predios colindantes con la barda perimetral del Fraccionamiento. 
	En caso de duda, queda establecido que el plano de siembra de la Etapa, define la superficie en la que se puede construir. Trat&aacute;ndose de predios en colindancia con la barda 
	perimetral de la Etapa, no se podr&aacute; apoyar ning&uacute;n elemento de construcci&oacute;n en dicha barda.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 15</b><br>
	En el &aacute;rea de restricci&oacute;n frontal se permitir&aacute;, para usos en cochera, la colocaci&oacute;n de p&eacute;rgolas, defini&eacute;ndose &eacute;stas como;
	la viguer&iacute;a o entramado que sirva para provocar sombra horizontal. Estos elementos tendr&aacute;n sus apoyos a partir de 4.50 metros del
	frente y la distancia entre apoyos ser&aacute; mayor de 3.00 metros entre s&iacute;.
</p>

<p align="justify" >
	La p&eacute;rgola podr&aacute; volar sin exceder los 4.50 metros en su frente. La prolongaci&oacute;n de la p&eacute;rgola ser&oacute; solo en estructura de acero no estructural,
	prefabricado, forrada con durock, a cantilever y con p&eacute;rgolas horizontales.
	Los apoyos verticales no exceder&aacute;n en su secci&oacute;n un di&aacute;metro o lado de 30 cent&iacute;metros.
</p>

<p align="justify" >
	Las trabes p&eacute;rgolas no exceder&aacute;n de 15 cent&iacute;metros de di&aacute;metro o ancho 30 cent&iacute;metros su peralte.
	Su separaci&oacute;n m&iacute;nima ser&aacute; de 50 cent&iacute;metros entre s&iacute; y podr&aacute; ahogarse o colocarse sobre ellas material de cubierta y se utilice como sombra y
	nunca para uso alguno sobre ella.
</p>

<p align="justify" >
	Se podr&aacute;n construir tejados a base de vigas y teja de barro, s&oacute;lo en el caso de cubiertas inclinadas y para fin de cubrir parcialmente el &aacute;rea de autos.
	Esta &aacute;rea tendr&aacute; un largo m&aacute;ximo del frente del predio y una inclinaci&oacute;n m&iacute;nima del 20%. Los materiales a utilizar ser&aacute;n s&oacute;lidos,
	evitando el uso de toldos o l&aacute;minas que degraden la fisonom&iacute;a de la vivienda.
	En ning&uacute;n caso estos elementos ser&aacute;n preponderantes en el frente y se evitar&aacute; el crecimiento de vegetaci&oacute;n que forme una barrera visual en el
	paisaje de la calle, su paisaje y que demerite la seguridad del peat&oacute;n, en especial de los ni&ntilde;os.
</p>

<p align="center">
	<img border="0" width="40%" height="35%" src="../img/lote_art15.jpg">
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 16</b><br>
	En el caso de que dos o m&aacute;s predios se integren para la construcci&oacute;n de una vivienda unifamiliar, la colindancia intermedia entre los predios
	podr&aacute; ser utilizada para construcci&oacute;n, siempre que se adecue al orden de apareamiento especificado en el plano de siembra del conjunto y se
	obtenga la autorizaci&oacute;n del Comit&eacute; T&eacute;cnico de Construcci&oacute;n y/o la Administradora del Fraccionamiento.
</p>

<p align="center">
	<img border="0" width="40%" height="35%" src="../img/lote_art16.jpg">
</p>

<p align="justify" >
	<b>VI. SUBDIVISION DE PREDIOS</b>
	<br><br>
	<b>ART&Iacute;CULO 17</b><br>
	Los predios del Fraccionamiento no son susceptibles de ser subdivididos en fracciones siendo permisible la construcci&oacute;n de una sola
	vivienda en dos o m&aacute;s predios con autorizaci&oacute;n expresa del Comit&eacute; T&eacute;cnico de Construcci&oacute;n y/o la Administradora del Fraccionamiento,
	con respecto a restricciones se conservar&aacute;n las de origen; al fondo de 2.5 metros y frontal de 6.00 metros por cada predio.
</p>

<p align="justify" >
	<b>VII. DISE&Ntilde;O ARQUITECTONICO</b>
	<br><br>
	<b>ART&Iacute;CULO 18</b><br>
	Se deber&aacute; dise&ntilde;ar la vivienda acorde a los lineamientos de este reglamento, de manera particular a cada Etapa, evitando la repetici&oacute;n de
	fachadas e integr&aacute;ndose al contexto del Fraccionamiento y su paisaje urbano, con las siguientes limitantes:
</p>

<p align="justify" >
	Las alturas de las construcciones no exceder&aacute;n de lo siguiente: 7.50 metros en el frente de la construcci&oacute;n: 10.50 metros medidos en un
	punto ubicado a la mitad de la casa y hasta el fondo. La altura de entrepisos y/o habitaciones ser&aacute; de 2.45 metros m&iacute;nimo.
	La altura m&aacute;xima permisible para las habitaciones antes citadas ser&aacute; de 3.50 metros para habitaciones con techo plano y 4.50 metros
	para habitaciones con techo inclinado en su lado m&aacute;s alto. Lo anterior a partir del nivel de desplante de la construcci&oacute;n,
	siendo este 45 cent&iacute;metros sobre el nivel de calle. En el caso de construirse un tercer piso,
	&eacute;ste no exceder&aacute; del 50% de la superficie del segundo piso y se ubicar&aacute; en la mitad posterior de la construcci&oacute;n.
	El 50% frontal restante, se podr&aacute; construir solo un proyecto a cielo abierto.
</p>

<p align="center">
	<img border="0" width="70%" height="20%" src="../img/lote_art18.jpg">
</p>

<p align="justify" >
	Para los predios en esquina y at&iacute;picos se aplicar&aacute; el mismo criterio en proporci&oacute;n a su &aacute;rea.
	Queda expresamente limitada la construcci&oacute;n de s&oacute;tanos a la autorizaci&oacute;n del Comit&eacute; T&eacute;cnico de Construcci&oacute;n,
	bajo la estricta responsabilidad del Fraccionamiento, siempre que no ponga en riesgo la estabilidad, seguridad y confort del
	Fraccionamiento y se haga cargo de la operaci&oacute;n del equipo de emergencia para evitar y en su caso extraer el agua que pueda penetrar a dicho s&oacute;tano.
	Dicho s&oacute;tano deber&aacute; quedar separado un m&iacute;nimo de un metro de cualquier colindancia y no podr&aacute; tener instalaciones hidr&aacute;ulicas ni sanitarias.
</p>

<p align="justify" >
	<b>ALTURAS Y NIVELES DE CONSTRUCCI&Oacute;N PERMITIDOS</b>
</p>

<p align="center">
	<img border="0" width="65%" height="40%" src="../img/lote_alt_niv.jpg">
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 19</b><br>
	ARTÍCULO 19
	Los techos y cubiertas de las construcciones ser&aacute;n de por lo menos un 15% del total de la construcci&oacute;n, inclinados.
	Deber&aacute;n de ser visibles desde el exterior, y estar&aacute;n recubiertos de teja color arena.
	
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 20</b><br>
	Los volados en losas, marquesinas, balcones y terrazas, no podr&aacute;n sobresalir del &aacute;rea de restricci&oacute;n.
	En ning&uacute;n caso se permitir&aacute;n elementos de m&aacute;s de 1.00 metros de altura, que formen parte del espacio habitable de la vivienda,
	respetando las restricciones de construcci&oacute;n de pa&ntilde;o exterior a pa&ntilde;o exterior.
</p>

<p align="center">
	<img border="0" width="65%" height="20%" src="../img/lote_art20.jpg">
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 21</b><br>
	Los tanques para agua, o combustible quedar&aacute;n ocultos desde cualquier punto del exterior del predio.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 22</b><br>
	Los patios de servicio, escaleras de servicio, tendederos, calentadores, dep&oacute;sitos de basura, antenas de TV, parab&oacute;licas,
	equipos de aire acondicionado y otros, quedar&aacute;n ocultos desde cualquier punto exterior del predio.
	Las torres de radio y radio comunicaci&oacute;n en cuanto a su colocaci&oacute;n ser&aacute; sujeta a la revisi&oacute;n y
	aprobaci&oacute;n del Comit&eacute; T&eacute;cnico de Construcci&oacute;n y/o la Administradora del Fraccionamiento.
</p>

<p align="justify" >
	<b>INSTALACIONES OCULTAS</b><br>
</p>

<p align="center">
	<img border="0" width="65%" height="20%" src="../img/lote_inst_ocu.jpg">
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 23</b><br>
	No se permite lo construcci&oacute;n de bardas ni la colocaci&oacute;n de rejas de ning&uacute;n tipo  en el frente de los predios ni el las colindancias laterales del frente.
	En estos linderos se permiten los setos de vegetaci&oacute;n de hasta 1.00 metro de altura.
	En las colindancias del fondo, se deber&aacute;n construir bardas de colindancia de 2.50 metros de altura que medir&aacute;n de fondo o frente.
</p>

<p align="justify" >
	<b>BARDAS COLINDANTES</b><br>
</p>

<p align="center">
	<img border="0" width="40%" height="35%" src="../img/lote_barda_col.jpg">
</p>

<p align="justify" >
	<b>VIII. MATERIALES DE CONSTRUCCI&Oacute;N</b>
	<br><br>
	<b>ART&Iacute;CULO 24</b><br>
	El esp&iacute;ritu de este reglamento es en el sentido de crear una imagen arm&oacute;nica en base a texturas, materiales y colores propios de la regi&oacute;n.
	Los colores ser&aacute;n arm&oacute;nicos, evitando los altos contrastes o colores brillantes que deterioren el paisaje o den mal aspecto,
	para lo cual se tendr&aacute; una tabla de colores aceptados para macizos y otra para detalles arquitect&oacute;nicos que podr&aacute;n ser aplicados
	hasta en un 5% del total de las fachadas, sujeto a aprobaci&oacute;n del Comit&eacute; T&eacute;cnico de Construcci&oacute;n y/o la Administradora del Fraccionamiento.
	(Anexo c&oacute;digos de color).
</p>

<p align="justify" >
	En el caso de recubrimiento con p&eacute;treos o cer&aacute;micos quedar&aacute; sujeto a la aprobaci&oacute;n del Comit&eacute; T&eacute;cnico de Construcci&oacute;n
	y/o la Administradora del Fraccionamiento.
</p>

<p align="justify" >
	Los materiales de construcci&oacute;n que pueden utilizarse en los muros, fachadas, techos y pisos son los siguientes:
</p>

<p align="justify" >
	Muros y Fachadas: Materiales aparentes como piedra, sillar, tabique de barro, adobe, madera, cantera, etc.
	Materiales artificiales o manufacturados, siempre que se terminen aplanados y/o pintados o se utilice material de recubrimiento, tales como:
	fachaleta, loseta cantera, piedra artificial, pastas, madera, tiroles, etc.
	Todas las construcciones deber&aacute;n de ser terminadas con igual tratamiento en todas sus fachadas y vistas;
	especialmente las fachadas de las construcciones ubicadas en colindancia con la barda perimetral del frente del Fraccionamiento.
</p>

<p align="justify" >
	Techos: Podr&aacute;n ser planos o inclinados, en l&iacute;neas rectas o curvas visibles desde el exterior y de cualquier material
	estructural, siempre y cuando se terminen recubiertos con teja color arena de media ca&ntilde;a o similar. Se proh&iacute;be 
	expresamente el uso de l&aacute;mina de cartón, metal, asbesto, cemento o cualquier otra l&aacute;mina de baja calidad y duraci&oacute;n.
</p>

<p align="justify" >
	Pisos exteriores: De adoqu&iacute;n, adocreto, adopasto, barro, materiales naturales o con apariencia de ellos,
	respetando un m&iacute;nimo del 50% de vegetaci&oacute;n en el frente entendi&eacute;ndose &eacute;ste la restricci&oacute;n de 6 metros por el frente del predio.
	En todos los casos se recomienda el uso de colores naturales propios de la regi&oacute;n evitando el uso de colores oscuros o demasiado brillantes.
</p>

<p align="justify" >
	En ning&uacute;n caso se permitir&aacute; el uso de material deslumbrante y/o reflejante cualquiera que sea su lugar de colocaci&oacute;n al exterior de la construcci&oacute;n.
</p>

<p align="justify" >
	<b>IX. MEDIDAS DE SEGURIDAD DURANTE LA CONSTRUCCI&Oacute;N</b>
	<br><br>
	<b>ART&Iacute;CULO 25</b><br>
	Durante el proceso de construcci&oacute;n el propietario del predio se compromete a:
</p>

<ol type="1" style="text-align:justify;">
	<li>
	    Seguir las normas de seguridad establecidas en el Fraccionamiento, siendo obligatorio el uso de cascos,
	    con excepci&oacute;n de los yeseros y los pintores con andamiaje de hasta 1.00 metro de altura en trabajos interiores:
	    y botas de trabajo como m&iacute;nimo, para sus trabajadores en la obra.
	</li>
	<li>
	    Obtener del organismo correspondiente toma provisional de energ&iacute;a el&eacute;ctrica para el proceso de construcci&oacute;n.
	    En cuanto al agua, deber&aacute;n instalar su cisterna para comprar agua tratada conforme lo establece la ley.
	</li>
	<li>
	    Evitar la presencia de animales.
	</li>
	<li>
	    Previo al inicio de los trabajos, instalar una letrina para uso de los trabajadores durante el tiempo que dure la construcci&oacute;n.
	    Dicha letrina tendr&aacute; como m&iacute;nimo la y estructura de madera, l&aacute;mina de cart&oacute;n asfaltado y puerta.
	</li>
	<li>
	    En caso de requerirse el uso de maquinaria pesada que tenga que transitar por las &aacute;reas comunes se deber&aacute; obtener autorizaci&oacute;n
	    del Comit&eacute; de Construcci&oacute;n y/o la Administradora del Fraccionamiento.
	    Se tendr&aacute; un control de entrada y salida de materiales al Fraccionamiento y a las obras.
	</li>
	<li>
	    Ning&uacute;n material o escombro podr&aacute; permanecer en las &aacute;reas comunes. La carga y descarga de material deber&aacute; realizarse dentro del predio y
	    no se permitir&aacute; la invasi&oacute;n de las &aacute;reas comunes.
	</li>
	<li>
	    Se tendr&aacute; un control de entrada y salida de personal para lo cual se deber&aacute; de entregar a la caseta de vigilancia semanalmente el listado
	    de trabajadores que se ocupen en la construcci&oacute;n de la vivienda para su registro; Se sancionar&aacute; a la persona que circule o se le encuentre
	    en &aacute;reas no permitidas fuera de su &aacute;rea autorizada para realizar alguna labor o prestar alg&uacute;n servicios.
	</li>
	<li>
	    Se deber&aacute; de mantener el orden y la higiene en la obra.
	</li>
	<li>
	    Una vez iniciada la construcci&oacute;n de la vivienda en el Fraccionamiento, se tendr&aacute; un plazo m&aacute;ximo de 24 (veinticuatro)
	    meses para su terminaci&oacute;n m&iacute;nima para su ocupaci&oacute;n.
	</li>
</ol>

<p align="justify" >
	<b>X. REDES DE SERVICIOS</b>
	<br><br>
	<b>ART&Iacute;CULO 26</b><br>
	Las redes de servicios urbanos agua potable, drenaje, alcantarillado, energ&iacute;a el&eacute;ctrica y tel&eacute;fono, estan previstas en ductos subterr&aacute;neos,
	por lo que NO se permite la instalaci&oacute;n de ning&uacute;n tipo de tuber&iacute;a o cables de l&iacute;neas a&eacute;reas de antenas de radio o televisi&oacute;n que sean
	visibles desde el exterior de los predios. Se deber&aacute; de construir una cisterna con un volumen m&iacute;nimo equivalente al consumo diario,
	de acuerdo al proyecto aprobado.
</p>

<p align="justify" >
	<b>XI. USO DE LA VIA P&Uacute;BLICA Y &Aacute;REAS COMUNES</b>
	<br><br>
	<b>ART&Iacute;CULO 27</b><br>
	 No se permitir&aacute; ordinariamente, el estacionamiento de veh&iacute;culos de carga de m&aacute;s de 3 toneladas y largo excesivo,
	 tales como trailers o remolques: salvo autorizaci&oacute;n expresa del Comit&eacute; T&eacute;cnico de Construcci&oacute;n,
	 los autos de visitas deber&aacute;n de ocupar las &aacute;reas ex profesa y el personal de la obra ocupar&aacute; las &aacute;reas que para
	 cada caso se les asignen y respetar el acceso a las viviendas.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 28</b><br>
	Los Propietarios de los predios en donde se construya deber&aacute; cuidar sus colindancias y respetar los predios que se encuentran en ellas,
	evitando todo tipo de invasiones, as&iacute; como dejar escombro o materiales que produzcan alima&ntilde;as o hierba.
</p>

<p align="justify" >
	En ning&uacute;n caso se podr&aacute; cambiar el dise&ntilde;o de la guarnici&oacute;n, pavimento, cuneta, banqueta, etc.,
	ni aumentar o quitar elementos que impidan el funcionamiento de los mismos.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 29</b><br>
	Cuando en la ejecuci&oacute;n de una obra, por el uso de veh&iacute;culos, objetos, sustancias o por alguna otra causa se produzcan da&ntilde;os a
	cualquier servicio p&uacute;blico, la reparaci&oacute;n inmediata de los da&ntilde;os ser&aacute; por cuenta del propietario del predio, veh&iacute;culo,
	objeto o sustancia, haciendo el pago a favor de la Administradora del Fraccionamiento; qui&eacute;n reparar&aacute; de inmediato el da&ntilde;o.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 30</b><br>
	El pavimento de las calles solamente podr&aacute; romperse y reponerse por la Administradora del Fraccionamiento, seg&uacute;n sea el caso.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 31</b><br>
	Los desperfectos de las &aacute;reas comunes o sus instalaciones de servicio p&uacute;blico deber&aacute;n ser repuestos por la Administradora del Fraccionamiento.
	Los particulares no estan autorizados para reparar estas instalaciones. El costo ser&aacute; a cargo del responsable del desperfecto.
</p>

<p align="justify" >
	<b>XII. CONSERVACI&Oacute;N DE PREDIOS Y CONSTRUCCIONES</b>
	<br><br>
	<b>ART&Iacute;CULO 32</b><br>
	A partir de la entrega del predio, el propietario tiene la obligaci&oacute;n de mantener en buenas condiciones de aspecto e higiene.
	Est&aacute; prohibido el dep&oacute;sito materiales de construcci&oacute;n, escombro o basura, que permita alima&ntilde;as o hierba nociva;
	y la construcci&oacute;n de barracas o casa de cuidador en tanto no se obtenga la licencia de construcci&oacute;n.
	De violarse estas disposiciones, el Comit&eacute; T&eacute;cnico de Construcci&oacute;n y/o la Administradora del Fraccionamiento dar&aacute; aviso al
	propietario y al Gobierno Municipal para que se proceda al desalojo de la basura o a la demolici&oacute;n de la construcci&oacute;n en su caso;
	ello con cargo al propietario de la construcci&oacute;n.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 33</b><br>
	Los predios colindantes no podr&aacute;n ser utilizados para depositar materiales de construcci&oacute;n, desechos de obra o basura, salvo autorizaci&oacute;n
	por escrito del colindante m&aacute;s cercano y sujeto a disponibilidad. No se permite el paso de veh&iacute;culos o de operarios a trav&eacute;s de los predios.
	El Comit&eacute; T&eacute;cnico de Construcci&oacute;n y/o la Administradora del Fraccionamiento reportar&aacute; las violaciones a la Administraci&oacute;n del Fraccionamiento o
	al Gobierno Municipal, y coadyuvar&aacute; con los propietarios afectados.
</p>

<p align="justify" >
	En caso de que el propietario no mantenga las condiciones mencionadas en los art&iacute;culos 32 y 33, el administrador podr&aacute; realizar
	la limpieza con cargo al propietario infractor.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 34</b><br>
	Los propietarios deber&aacute;n conservar las fachadas pintadas, aseadas y con buena apariencia y las &aacute;reas jardinadas en buen estado.
	En caso contrario el Comit&eacute; T&eacute;cnico de Construcci&oacute;n y/o la Administradora del Fraccionamiento.
</p>

<p align="justify" >
	<b>XIII. NOMENCLATURA</b>
	<br><br>
	<b>ART&Iacute;CULO 35</b><br>
	El n&uacute;mero oficial correspondiente a cada predio, deber&aacute; ser colocado en el murete de acometida de servicios y/o en parte visible del acceso principal.
	El murete ser&aacute; de acuerdo a las especificaciones y medidas del modelo aprobado por el organismo operador de los servicios.
</p>

<p align="justify" >
	<b>XIV. AUTORIZACIONES</b>
	<br><br>
	<b>ART&Iacute;CULO 36</b><br>
	Para la construcci&oacute;n, modificaci&oacute;n o ampliaci&oacute;n en predios privados, su propietario deber&aacute; presentar al Comit&eacute; T&eacute;cnico de Construcci&oacute;n y/o
	la Administradora del Fraccionamiento el proyecto arquitect&oacute;nico para que reciba el Visto Bueno, previo a los tr&aacute;mites de solicitud
	de licencia de construcci&oacute;n ante las autoridades del Municipio.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 37</b><br>
	La construcci&oacute;n que fuere, iniciada dentro del predio &oacute; &aacute;rea privada en el Fraccionamiento, sin haber sido aprobada por
	el Comit&eacute; T&eacute;cnico de Construcci&oacute;n y/o la Administradora del Fraccionamiento, ser&aacute; suspendida por &eacute;ste y reportada al Municipio.
	Tambi&eacute;n ser&aacute; motivo de suspensi&oacute;n el incumplimiento del Reglamento interno de Construcci&oacute;n, o la modificaci&oacute;n del proyecto sin aprobaci&oacute;n previa.
	Para suspender la obra, el Comit&eacute; T&eacute;cnico de Construcci&oacute;n y/o la Administradora del Fraccionamiento
	notificar&aacute; al propietario y a la Oficina de licencias de la Secretar&iacute;a de Desarrollo Urbano del Municipio.
</p>

<p align="justify" >
	Para lo anterior el Comit&eacute; T&eacute;cnico de Construcci&oacute;n y/o la Administradora del Fraccionamiento tendr&aacute; facultades para llevar a cabo
	la supervisi&oacute;n peri&oacute;dica durante el proceso de construcci&oacute;n de las viviendas.
</p>

<p align="justify" >
	En caso de omisi&oacute;n o reincidencia en el incumplimiento, la Administraci&oacute;n del Fraccionamiento y/o el Comit&eacute; T&eacute;cnico de Construcci&oacute;n y/o
	la Administradora del Fraccionamiento se reservan el derecho de veto al constructor y su personal, pudiendo reportarlo a las autoridades
	y al colegio de profesionales al que pertenezca, reclamando su comportamiento &eacute;tico y profesional.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 38</b><br>
	La solicitud para obtener el Visto Bueno de construcci&oacute;n del Comit&eacute; T&eacute;cnico de Construcci&oacute;n y/o la Administradora del Fraccionamiento
	se acompa&ntilde;ar&aacute; de los planos y documentos siguientes:
</p>

<ol type="1" style="text-align:justify;">
	<li>
	    Planta del predio: indicando los &aacute;rboles existentes, las restricciones de construcci&oacute;n que marca el Reglamento,
	    la ubicaci&oacute;n de la construcci&oacute;n y la superficie construida.
	</li>
	<li>
	    Cortes y fachadas; en donde se anoten las alturas m&aacute;ximas de la construcci&oacute;n sobre los niveles de banqueta y del
	    desplante de la planta baja, as&iacute; como tambi&eacute;n se indiquen las instalaciones sanitarias.
	</li>
	<li>
	    Plantas arquitect&oacute;nicas; con las indicaciones de: altura de las bardas, materiales  de construcci&oacute;n y acabados al exterior.
	</li>
	<li>
	    Planos estructurales y de instalaciones.
	</li>
	<li>
	    Los planos deber&aacute;n contener el n&uacute;mero del predio, nombres del propietario y nombre del profesionista perito responsable de la obra de edificaci&oacute;n.
	</li>
</ol>

<p align="justify" >
	<b>ART&Iacute;CULO 39</b><br>
	Para que el proyecto arquitect&oacute;nico sea autorizado por el Comit&eacute; T&eacute;cnico de Construcci&oacute;n y/o la Administradora del Fraccionamiento deber&aacute;
	cumplir con los requisitos que establece este Reglamento interno de Construcci&oacute;n y las normas municipales aplicables.
	Para ello el propietario acreditar&aacute; al profesionista que se har&aacute; cargo de la construcci&oacute;n de la vivienda y responder&aacute; ante
	el Comit&eacute; T&eacute;cnico de Construcci&oacute;n y/o la Administradora del Fraccionamiento y la autoridad municipal.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 40</b><br>
	Una vez terminada la construcci&oacute;n se deber&aacute; dar aviso al Comit&eacute; T&eacute;cnico de Construcci&oacute;n y/o la Administradora del Fraccionamiento y a
	la Secretar&iacute;a de Desarrollo Urbano Municipal para obtener su autorizaci&oacute;n de habitar la vivienda.
	Se entender&aacute; como vivienda terminada, aqu&eacute;lla que presente la totalidad del exterior con acabados y los servicios de cocina y
	ba&ntilde;os totalmente instalados y en operaci&oacute;n, tales como: Instalaci&oacute;n el&eacute;ctrica, hidr&aacute;ulica y sanitaria.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO 41</b><br>
	Todas las solicitudes y autorizaciones a las que se hace referencia en este reglamento se har&aacute;n por escrito y el Comit&eacute; T&eacute;cnico de Construcci&oacute;n y/o
	la Administradora del Fraccionamiento se reserva la facultad para resolver sobre cualquier duda o controversia que se presente relacionado
	con cualquier punto no expresado en el presente Reglamento Interno de Construcci&oacute;n, as&iacute; como para modificar la siembra de viviendas en una,
	varias o todas las etapas del Fraccionamiento si las circunstancias que determine la Administradora del Fraccionamiento as&iacute; lo requieren.
</p>

<p style="page-break-after: always"></p>

<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr>
		<td width="5%">
			<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
		</td>
		<td class="reportTitleCell">
			ANEXO 6 LUGAR Y FORMA DE PAGO
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
			<b><%= bmoDevelopmentPhase.getName().toString().toUpperCase() %></b>
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
			<b><%= bmoDevelopmentPhase.getName().toString().toUpperCase() %></b>
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