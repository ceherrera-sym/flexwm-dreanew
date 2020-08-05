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
	PmConn pmConnRacc = new PmConn(sFParams);
 	pmConnRacc.open();
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
		<td class="reportTitleCell" style="text-align:right; font-size: 9px">
			C&oacute;digo del Documento:<br>FO-07.2.2-3(30-Mar-04) <br><%= bmoPropertySale.getCode().toString() %>
		</td>
	</tr>
</table>

<br>
<p align="justify" >
	CONTRATO DE PROMESA DE COMPRAVENTA CON RESERVA DE DOMINIO Y CONDICI&Oacute;N SUSPENSIVA  
	QUE CELEBRAN: POR UNA  PARTE LA EMPRESA MERCANTIL DENOMINADA 
	<b><%= bmoCompany.getLegalName().toString().toUpperCase() %></b>; 
	REPRESENTADA EN ESTE ACTO POR SU REPRESENTANTE LEGAL EL C: 
	<b><%= bmoCompany.getLegalRep().toString().toUpperCase() %></b> 
	A QUIEN EN LO SUCESIVO Y PARA EFECTOS DEL PRESENTE CONTRATO DE PROMESA DE COMPRA VENTA SE LE DENOMINARA COMO LA PROMITENTE  &quot;VENDEDORA&quot;,  
	Y, POR OTRA PARTE, EL SR(A).  
	<b>
    (<%= bmoCustomer.getCode().toString().toUpperCase() %>)
    <%= bmoCustomer.getDisplayName().toString().toUpperCase() %>
    </b>
	QUIEN SE REPRESENTA POR SU PROPIO DERECHO Y A QUIEN EN LO SUCESIVO Y PARA EFECTOS DEL PRESENTE CONTRATO DE PROMESA DE COMPRA VENTA
	SE LE DENOMINAR&Aacute; COMO EL PROMITENTE "COMPRADOR", QUIENES SE SUJETAN AL TENOR DE LAS SIGUIENTE DECLARACIONES Y 
	SUBSECUENTES CL&Aacute;USULAS: 
</p>



<p align="center" class="contractTitle">
	<b>D E C L A R A C I O N E S:</b>
</p>

<p align="justify" >
	<b>PRIMERA:</b> Declara el "VENDEDOR" ser una empresa mercantil constituida conforme a las leyes mexicanas, 
	y cuyo objeto social entre otros, es realizar la construcci&oacute;n, promoci&oacute;n y venta 
	de todo tipo de bienes inmuebles, seg&uacute;n consta en escritura publica n&uacute;mero 
	<b><%= bmoCompany.getDeedNumber().toString().toUpperCase() %></b> 
	tirada ante la fe del notario publico n&uacute;mero 
	<b><%= bmoCompany.getDeedNotaryNumber().toString().toUpperCase() %></b> 
	de este partido judicial y debidamente registrada en el registro publico y de comercio y ante las autoridades 
	fiscales con registro federal de contribuyente 
	<b><%= bmoCompany.getRfc().toString().toUpperCase() %></b>.
</p>

<p align="justify" >
	<b>SEGUNDA:</b> declara la &quot;VENDEDORA&quot; ser legitima propietaria de los lotes de terreno ubicados 
	en el fraccionamiento <b><%= bmoDevelopment.getName().toString().toUpperCase() %></b>,
	etapa <b><%= bmoDevelopmentPhase.getName().toString().toUpperCase() %></b>
	de esta ciudad de <b><%= bmoCityDevelopment.getName().toString().toUpperCase() %></b>,
	<b><%= bmoCityDevelopment.getBmoState().getName().toString().toUpperCase() %></b>   
	en los cuales se construir&aacute;n viviendas descritas en el anexo 1 del presente contrato de promesa de compra venta y 
	que forma parte integrante del mismo como si a la letra se insertase.
</p>

<p align="justify" >
	<b>TERCERA:</b>  declara la parte "COMPRADORA" ser mexicano mayor de edad, que tiene su 
	domicilio ubicado en 
	<b><%= bmoCustomerAddress.getStreet().toString().toUpperCase() %></b> 
	<b>#<%= bmoCustomerAddress.getNumber().toString().toUpperCase() %></b>
	<b><%= bmoCustomerAddress.getNeighborhood().toString().toUpperCase() %></b>, 
	de la ciudad de 
	<b><%= bmoCustomerAddress.getBmoCity().getName().toString().toUpperCase() %></b>, 
	<b><%= bmoCustomerAddress.getBmoCity().getBmoState().getName().toString().toUpperCase() %></b> con 
	registro federal de contribuyente <b><%= bmoCustomer.getRfc().toString().toUpperCase() %></b>.
</p>

<p align="justify" >
	<b>CUARTA:</b> Ambas partes declaran que se reconocen mutuamente su personalidad y que es 
	de su intenci&oacute;n celebrar el presente contrato de promesa de compra venta con reserva de 
	dominio y condici&oacute;n suspensiva en base a las siguientes.
</p>


<p align="center" class="contractTitle">
	<b>C L &Aacute; U S U L A S:</b>
</p>




<p align="justify"  >
	<b>PRIMERA</b>.- El "VENDEDOR" se compromete a vender y el "COMPRADOR" se compromete a comprar en forma real y 
	definitiva la vivienda tipo <b><%= bmoPropertyModel.getName().toString().toUpperCase() %></b>  
	a construir que tendr&aacute; su ubicaci&oacute;n en la 
	<b>"<%= bmoPropertySale.getBmoProperty().getBmoDevelopmentBlock().getName().toString().toUpperCase() %>,"</b> 
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
</p>

<p align="justify" >
	<b>SEGUNDA</b>.- El "COMPRADOR" conviene en pagar al "VENDEDOR" y que se fija de com&uacute;n 
	acuerdo como precio de la vivienda, cantidad que deber&aacute; estar cubierta a mas tardar el d&iacute;a de la firma de las escrituras de compra-venta ante el NOTARIO PUBLICO
	que para este efecto designe el "VENDEDOR", los gastos que se deriven del avalu&oacute; de la vivienda as&iacute; como los
	correspondientes a la escrituraci&oacute;n correr&aacute;n por cuenta del "COMPRADOR". A la firma del presente contrato el valor
	de la vivienda es <b><%= SFServerUtil.formatCurrency(bmoOrder.getTotal().toDouble()) %></b> 
	(<b><%= amountByWord.getMoneyAmountByWord(bmoOrder.getTotal().toDouble()).toUpperCase() %></b>), monto que podr&iacute;a ser actualizado si se 
	modificara el salario m&iacute;nimo mensual del Distrito Federal.El precio de venta ser&aacute; ajustado a la alza en el caso de que transcurran 12 (doce) meses 
	o m&aacute;s a partir de la fecha de firma del presente instrumento, y el "COMPRADOR" no haya liquidado al "VENDEDOR" el monto total del valor 
	de venta, independientemente del motivo del no pago en el plazo establecido.
</p>

<p align="justify" >
	A continuaci&oacute;n se detalla la forma de pago:
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
	    		" ORDER BY racc_duedate ASC, racc_invoiceno ASC";
    
    //System.out.println("sqlRaccW: "+sqlRaccW);
    pmConnRaccW.open();
    pmConnRaccW.doFetch(sqlRaccW);
%>
<table width="95%" align="center" border="0" cellpadding="0" cellspacing="0" style="font-size: 12px">
	<tr>
	    <th class="reportHeaderCell">Documento</th>
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
				if(bmoRaccount.getBmoRaccountType().getType().toChar() == BmoRaccountType.TYPE_WITHDRAW){
				%>
			 		<tr>
			 			<td class="reportCellEven" align = "left">
				        	<%= racc%>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%= pmConnRaccW.getString("racc_invoiceno")%>
			    		</td>
			    		<td class="reportCellEven" align = "left">
				        	<%= pmConnRaccW.getString("payt_name")%>
			    		</td>
			    		<td class="reportCellEven" align = "left">
			        		<%= FlexUtil.dateToLongDate(sFParams, pmConnRaccW.getString("racc_duedate"))%>
			    		</td>
			    		<td class="reportCellEven" align = "right">
				        	<%= SFServerUtil.formatCurrency(pmConnRaccW.getDouble("racc_total"))%>
			    		</td>
							
			   <%
			   		racc += 1;
				}
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
<ol type="a" >
    <li align="justify">
    	El "VENDEDOR" recibir&aacute; como enganche por parte del "COMPRADOR" la cantidad de 
        <b>PENDIENTE_ENGANCHE<%//= MiscUtils.formatCurrency(boAllocation.getDownPayment().toDouble()) %></b>
        (<b>PENDIENTE_ENGANCHE_LETRA<%//= amountByWord.getMoneyAmountByWord(boAllocation.getDownPayment().toDouble()).toUpperCase() %></b>)
        Que servir&aacute; como apartado de la vivienda antes mencionada.
    </li>
    <li>
		La diferencia de esta operaci&oacute;n podr&aacute; ser pagada al "VENDEDOR" de contado o a trav&eacute;s de un cr&eacute;dito
		hipotecario.
	</li>	
	<li>
		Si la diferencia de esta operaci&oacute;n fuese pagada al vendedor de contado el pago deber&aacute; ser por la cantidad correspondiente
		a <b>PEDIENTE_SM<%//= MiscUtils.formatDouble(realPrice/minimumWage,2) %></b> salarios m&iacute;nimos mensuales del Distrito Federal vigente en el momento de la firma de las escrituras 
		descontando el enganche dado por el "COMPRADOR" quedando la cantidad correspondiente a <b>PENDIENTE(realPrice-anticipo)/minimumWage<%//= MiscUtils.formatDouble((realPrice - boAllocation.getDownPayment().toDouble())/minimumWage,2)  %></b>
		salarios m&iacute;nimos mensuales del Distrito Federal, cantidad que deber&aacute; estar cubierta a m&aacute;s tardar el d&iacute;a de la firma de la escritura de compra-venta definitiva ante el NOTARIO 
		que para este efecto designe el "VENDEDOR". Los gastos que se deriven tanto del avalu&oacute; como de la escrituraci&oacute;n correr&aacute;n 
		por cuenta del "COMPRADOR".
    </li>
	<li>
		Si la diferencia de esta operacion fuese pagada a trav&eacute;s de un cr&eacute;dito hipotecario otorgado al
		"COMPRADOR" por la instituci&oacute;n crediticia, deber&aacute; ser pagada al "VENDEDOR" a m&aacute;s tardar
		el d&iacute;a de la firma de escritura ante el notario que designe la instituci&oacute;n crediticia.
		Los gastos que se deriven tanto del avaluo como de la escrituraci&oacute;n correr&aacute;n por cuenta del "COMPRADOR".
    </li>
	<li>	
		Si el monto del cr&eacute;dito otorgado al "COMPRADOR", junto con el enganche, fuera menor al 
        precio de la vivienda, el "COMPRADOR" conviene en pagar la 
        diferencia resultante de 
        <b>PENDIENTE(TotalPrice-creditAmount-DownPayment)
		<%//= MiscUtils.formatCurrency((boAllocation.getTotalPrice().toDouble() - boAllocation.getCreditAmount().toDouble() - boAllocation.getDownPayment().toDouble()))  %>
		</b>
        (<b>PENDIENTE(TotalPrice-creditAmount-DownPayment)-LETRA<%//= amountByWord.getMoneyAmountByWord(boAllocation.getTotalPrice().toDouble() - boAllocation.getCreditAmount().toDouble() - boAllocation.getDownPayment().toDouble()).toUpperCase() %></b>)
		al "VENDEDOR" en la forma pactada y avalada por pagar&eacute;s, diferencia que puede cambiar en el momento de la firma de las escrituras definitivas, derivado del monto del
		credito otorgado y el valor pactado en salarios m&iacute;nimos mensuales del Distrito Federal de la vivienda. En el supuesto que el "COMPRADOR" no pague al "VENDEDOR" los montos pactados
		en los pagar&eacute;s antes o en la fecha de vencimiento de los mismos, autoriza expresamente el "COMPRADOR" al "VENDEDOR" a reubicarlo a otra vivienda, designada por el
		"VENDEDOR", con la mismas caracter&iacute;sticas y el mismo precio de la vivienda contratada originalmente. En el momento que el "COMPRADOR" est&eacute; al corriente en los pagos
		pactados, podr&aacute; reubicarse a una vivienda de su elecci&oacute;n que se encuentre disponible para su venta.
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


<p align="justify"  >
	<b>CUARTA</b>.- Convienen ambas partes de este contrato de compra venta que el pago no
	oportuno de las cantidades pactadas en el presente contrato de compra venta generar&aacute; un inter&eacute;s del 5% (cinco por ciento), por parte del 
	"COMPRADOR" al "VENDEDOR", durante el tiempo que dure la mora, adicionando a lo anterior, el "COMPRADOR" autoriza expresamente al "VENDEDOR" a reubicarlo a otra vivienda,
	designada por el "VENDEDOR", con las mismas caracter&iacute;sticas y el mismo precio de la vivienda contratada originalmente.
</p>

<p align="justify" >
	<b>QUINTA</b>.- En el supuesto de que el cr&eacute;dito fuere negado al "COMPRADOR" por la instituci&oacute;n 
	crediticia, se proceder&aacute; a la cancelaci&oacute;n del presente contrato de promesa de compra venta 
	sin ninguna responsabilidad para el "VENDEDOR", 
	el cual devolver&aacute; al "COMPRADOR" los pagos que haya efectuado al 
	"VENDEDOR" en un plazo de 30 d&iacute;as h&aacute;biles a partir de la fecha en que se conozca 
	que el cr&eacute;dito no fue concedido al "COMPRADOR" por la instituci&oacute;n crediticia.
</p>

<p align="justify" >
	<b>SEXTA</b>.- En el supuesto que el "COMPRADOR" no entregue con oportunidad
	su documentaci&oacute;n completa y fidedigna en original y copia, se
	procedera a la cancelaci&oacute;n inmediata del presente contrato de promesa de compra
	venta sin responsabilidad alguna para el "VENDEDOR" y aplicando una
	penalizacion de $3,000.00 (TRES MIL PESOS CON CERO CENTAVOS M.N.) 
	por concepto de gastos administrativos
	efectuados por el "VENDEDOR", haciendo devoluci&oacute;n al "COMPRADOR" las
	cantidades sobrantes que hubiere entregado el "COMPRADOR" al "VENDEDOR"
	descontando de estas la penalizaci&oacute;n correspondiente.
</p> 

<p align="justify" >
	<b>S&Eacute;PTIMA</b>.- En el supuesto de que al "COMPRADOR" se le niegue el otorgamiento del cr&eacute;dito hipotecario, estar&aacute; en posibilidad de liquidar 
	con sus propios recursos el valor pactado de la vivienda dentro de los siguientes treinta d&iacute;as naturales a partir de la negativa; de no cubrirse el saldo se
	proceder&aacute; a la cancelaci&oacute;n del presente contrato de compra venta, por parte del "VENDEDOR" al "COMPRADOR", sin ninguna
	responsabilidad para el "VENDEDOR", obligandose el "VENDEDOR" a devolver al "COMPRADOR", las cantidades que este hubiere entregado al "VENDEDOR" por concepto 
	de anticipos o enganches, reteniendo &uacute;nicamente los gastos que hubiere efectuado el "VENDEDOR" por concepto de aval&uacute;os, estudios
	socioecon&oacute;micos, o alg&uacute;n otro gasto con cargo al "COMPRADOR".
</p>

<p align="justify" >
	<b>OCTAVA</b>.- Las partes acuerdan que  si el "COMPRADOR" llegare a retrasarse en m&aacute;s 
	de dos de los pagos pactados, el "VENDEDOR" podr&aacute; rescindir inmediata y unilateralmente el 
	presente contrato de promesa de compra venta
	sin responsabilidad alguna, y vender nuevamente el inmueble descrito en el 
	anexo 1 que acompa&ntilde;a el presente contrato de promesa de compra venta, o a decisi&oacute;n del vendedor proceder a reubicarlo a otra vivienda,
	designada por el "VENDEDOR", con las mismas caracter&iacute;sticas y el mismo precio de la vivienda contratada originalmente.  
</p>

<p align="justify" >
	<b>NOVENA</b>.- El comprador est&aacute; enterado y consciente de las caracter&iacute;sticas, materiales de construcci&oacute;n, dise&ntilde;o, dimensiones, superficies y ubicaci&oacute;n de la 
	vivienda materia de este contrato de compra venta, por lo que firma de enterado y aceptaci&oacute;n en el Anexo 1 (uno) del presente contrato de compra venta, anexo que se tiene aqu&iacute; por
	reproducido como si a la letra se insertase.
</p>


<p align="justify" >
	<b>D&Eacute;CIMA</b>.- Si el "COMPRADOR" llegare a cancelar el presente contrato de promesa de compra venta
	en forma unilateral, deber&aacute; dar aviso por escrito al "VENDEDOR", y acepta el
	"COMPRADOR" pagar al "VENDEDOR" por concepto de da&ntilde;os y perjuicios
	producidos por el incumplimiento de la operaci&oacute;n de compra venta y dentro
	de los siguientes treinta dias, una pena convencional de un 5% (cinco
	porciento) del valor total de la operaci&oacute;n de compra venta pactada en el
	presente contrato. 
</p>
<p align="justify" >
	En caso que el cr&eacute;dito del "COMPRADOR" sea de FOVISSSTE, que est&eacute; autorizado dicho cr&eacute;dito y que el 
	"COMPRADOR" decida cancelar la presente operaci&oacute;n de promesa de compra-venta, el "COMPRADOR" acepta 
	pagar al "VENDEDOR" una pena convencional por la cantidad de $20,000.00 (VEINTE MIL PESOS CON CERO CENTAVOS 00/100).
</p>

<p align="justify" >
	<b>D&Eacute;CIMA PRIMERA</b>.-  El "VENDEDOR"  se obliga a transmitir a el "COMPRADOR" el
	inmueble materia del presente contrato de promesa de compra venta, libre de
	todo gravamen, sin limitaciones de dominio y al corriente en el pago de
	sus contribuciones, con todas sus especificaciones as&iacute; como los  planos y
	garant&iacute;as que  correspondan a la vivienda de que se trate , la cual se
	detalla en la cl&aacute;usula PRIMERA del presente contrato de promesa de compra
	venta. En todo caso la vivienda ser&aacute; entregada al "COMPRADOR" dentro de los
	veintiun  d&iacute;as posteriores  a la fecha en que el "VENDEDOR" reciba el pago
	total de la vivienda, esto es que el "COMPRADOR" o la entidad otorgante del cr&eacute;dito la haya
	liquidado o abonado a la cuenta bancaria del "VENDEDOR". 
	EL COMPRADOR est&aacute; consciente de que los gastos de originaci&oacute;n del
	cr&eacute;dito, en el caso de que les sea otorgado por la entidad financiera, para la
	adquisici&oacute;n de la vivienda objeto del presente contrato, gastos que
	corresponden a: escrituraci&oacute;n, estudio socioecon&oacute;mico, comisi&oacute;n por
	apertura de cr&eacute;dito, aval&uacute;o, bur&oacute; de cr&eacute;dito y/o la primera
	mensualidad, son por su cuenta y ser&aacute;n descontados del monto de cr&eacute;dito ortogado.
</p>

<p align="justify" >
	<b>D&Eacute;CIMA SEGUNDA</b>.- EL COMPRADOR, esta enterado y es consciente que en caso que haya sido sujeto al subsidio federal 
	denominado "ESTA ES TU CASA", para efectos del presente contrato de compra venta denominado SUBSIDIO, 
	y que este SUBSIDIO haya sido solicitado por EL COMPRADOR y tramitado por EL VENDEDOR, 
	el precio de la vivienda ser&aacute; pagado, una parte con cr&eacute;dito hipotecario otorgado por entidad 
	financiera (INFONAVIT, FOVISSSTE, ISSEG, ETC) al COMPRADOR y otra parte hasta completar el 
	valor de la vivienda por el SUBSIDIO otorgado al COMPRADOR por el Gobierno Federal. 
	Por lo anterior el VENDEDOR no es responsable ni adquiere ninguna responsabilidad en el supuesto de NO 
	otorgamiento o de no pago del SUBSIDIO por el Gobierno Federal.
</p>

<p align="justify" >
	Por lo anteriormente expuesto y en el supuesto de NO otorgamiento y/o pago del SUBSIDIO, el COMPRADOR acepta con 
	la firma del presente contrato el que el monto del SUBSIDIO no otorgado y/o pagado, y que es parte del valor de 
	la vivienda, se convierte en deuda a cargo del COMPRADOR y en favor de EL VENDEDOR, y deber&aacute; liquidarla en el 
	momento que conozca de la negativa del SUBSIDIO y a m&aacute;s tardar en la fecha de la firma de las escrituras 
	definitivas de la vivienda.
</p>

<p align="justify" >
	<b>D&Eacute;CIMA TERCERA</b>.- Ambas partes contratantes declaran que en este convenio no 
	existe error, dolo, enga&ntilde;o o violencia  alguna  por lo que rec&iacute;procamente renuncian 
	a las acciones de nulidad, que pudieran  fundarse en dicha causa, y al texto mismo 
	de las disposiciones aplicables contenidas en el C&oacute;digo Civil Vigente en el 
	<b>ESTADO DE <%= bmoCityCompany.getBmoState().getName().toString().toUpperCase() %></b>.
</p>

<p align="justify" >
	<b>D&Eacute;CIMA CUARTA</b>.- Para la interpretaci&oacute;n, cumplimiento, y, ejecuci&oacute;n del presente 
	contrato de promesa de compra venta ambas partes se someten a las Leyes del <b>ESTADO DE 
	<%= bmoCityCompany.getBmoState().getName().toString().toUpperCase() %></b>, y a los 
	Tribunales de esta Ciudad, renunciando a cualquier otro fuero de sus presentes o futuros domicilios. 
</p>

<p align="justify" >
	<b>Aviso de privacidad</b>:
	<%= bmoCompany.getLegalName().toString() %>, ratifica que todos los datos personales  que han proporcionado
	y que en el futuro proporcionen a esta empresa y/o a sus subsidiarias los utilizar&aacute;
	para identificarlos como clientes, mantener un expediente f&iacute;sico y procesar
	electr&oacute;nicamente sus datos en los diferentes sistemas internos y en su caso, ofrecerles
	promociones y/o informaci&oacute;n de nuestros productos.
	La empresa por su parte les informa que dar&aacute; cumplimiento a lo que dispone la
	Ley Federal de Protecci&oacute;n de Datos Personales en posesi&oacute;n de los particulares,
	como responsable del tratamiento de Datos Personales, seg&uacute;n se definen en la Ley citada.
	Respecto al procedimiento para solicitar sus Derechos de Acceso, Rectificaci&oacute;n,
	Cancelaci&oacute;n u oposici&oacute;n (ARCO), en relaci&oacute;n a cualesquier modificaci&oacute;n al
	Aviso de Privacidad y Transferencia de Datos Personales, podr&aacute;n hacerlo por medio de nuestra
	p&aacute;gina de Internet http://www.mdm.mx, en el apartado de Privacidad de Datos personales.
</p>

<p align="justify" >
	Los Anexos 3, 4 y 5 forman parte de este contrato como si a la letra se insertase.
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
<p style="page-break-after: always"></p>

<p>&nbsp;</p>

<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr>
		<td align="left" width="5%">
			<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
		</td>
		<td class="reportTitleCell">ANEXO 1 DEL CONTRATO DE PROMESA DE COMPRA VENTA</td>
		<td class="reportTitleCell" style="text-align:right; font-size: 9px"><b>C&oacute;digo del Documento:<br>FO-07.5.2.1-3&nbsp;</b></td>
	</tr>
</table>

<p align="justify"  >
	EN EL QUE SE DEFINE LA VIVIENDA SUJETA POR EL PRESENTE CONTRATO DE PROMESA DE COMPRA VENTA, EMITIDO POR
	LA PARTE &quot;VENDEDORA&quot; 
	<b><%= bmoCompany.getLegalName().toString().toUpperCase() %></b> 
	PARA LA PARTE "COMPRADORA" 
	<b>
	(<%= bmoCustomer.getCode().toString() %>)
	<%= bmoCustomer.getDisplayName().toString().toUpperCase() %>
	
	</b>.
	ESTE DOCUMENTO NO TIENE VALIDEZ SIN SU CONTRAPARTE EL CONTRATO DE PROMESA DE COMPRA VENTA FIRMADO
	POR LA PARTE &quot;VENDEDORA&quot; Y LA PARTE "COMPRADORA".
</p>

<table border="0" cellspacing="0" width="95%" cellpadding="0" style="font-size: 12px"  align="center">
	<tr>
		<th class="reportCellEven" align="left"><b>FRACCIONAMIENTO:</b></th>
		<td class="reportCellEven" align="left"><%= bmoDevelopment.getName().toString().toUpperCase() %></td>
	</tr>
	<tr>
		<th class="reportCellEven" align="left"><b>ETAPA:</b></th>
		<td class="reportCellEven" align="left"><%= bmoDevelopmentPhase.getName().toString().toUpperCase() %></td>
	</tr>
	<tr>
		<th class="reportCellEven" align="left"><b>MANZANA/FRACCI&Oacute;N:</b></th>
		<td class="reportCellEven" align="left"><%= bmoPropertySale.getBmoProperty().getBmoDevelopmentBlock().getCode().toString().toUpperCase() %></td>
	</tr>
	<tr>
		<th class="reportCellEven" align="left"><b>LOTE/FRACCI&Oacute;N:</b></th>
		<td class="reportCellEven" align="left"><%= bmoPropertySale.getBmoProperty().getLot().toString().toUpperCase()%></td>
	</tr>
	<tr>
		<th class="reportCellEven" align="left"><b>CALLE:</b></th>
		<td class="reportCellEven" align="left"><%= bmoPropertySale.getBmoProperty().getStreet().toString().toUpperCase() %></td>
	</tr>
	<tr>
		<th class="reportCellEven" align="left"><b>N&Uacute;MERO OFICIAL:</b></th>
		<td class="reportCellEven" align="left"><%= bmoPropertySale.getBmoProperty().getNumber().toString().toUpperCase() %></td>
	</tr>
	<tr>
		<th class="reportCellEven" align="left"><b>MODELO:</b></th>
		<td class="reportCellEven" align="left"><%= bmoPropertyModel.getName().toString().toUpperCase() %></td>
	</tr>
	<tr>
		<th class="reportCellEven" align="left"><b>DESCRIPCI&Oacute;N:</b></th>
		<td class="reportCellEven" align="justify">
			<%= bmoPropertyModel.getHighLights().toString().toUpperCase() %><br>
			<%= bmoProperty.getDescription().toString().toUpperCase() %>
		</td>
	</tr>
	<tr>
		<th class="reportCellEven" align="left"><b>CUARTOS:</b></th>
		<td class="reportCellEven" align="left"><%= bmoPropertyModel.getRooms().toString().toUpperCase() %></td>
	</tr>
	
	<tr>
		<th class="reportCellEven" align="left"><b>M2 DE TERRENO:</b></th>
		<td class="reportCellEven" align="left">
			<%= bmoPropertySale.getBmoProperty().getLandSize().toDouble() %> mt2
			(<%= amountByWord.getAmountByWord(bmoPropertySale.getBmoProperty().getLandSize().toDouble()).toUpperCase() %> METROS CUADRADOS)
		</td>
	</tr>
	<tr>
		<th class="reportCellEven" align="left"><b>M2 MODELO BASE:</b></th>
		<td class="reportCellEven" align="left">
			<%= bmoPropertyModel.getLandSize().toString().toUpperCase() %> mt2
			(<%= amountByWord.getAmountByWord(bmoPropertyModel.getLandSize().toDouble()).toUpperCase() %> METROS CUADRADOS)
		</td>
	</tr>
	<tr>
		<th class="reportCellEven" align="left"><b>M2 DE TERRENO EXCEDENTES:</b></th>
			<%	double ex = bmoPropertySale.getBmoProperty().getLandSize().toDouble() - bmoPropertyModel.getLandSize().toDouble(); %>
		<td class="reportCellEven" align="left">
			<%= df.format(ex) %> mt2
			(<%= amountByWord.getAmountByWord(ex).toUpperCase() %> METROS CUADRADOS)
		</td>
	</tr>
	<tr>
		<th class="reportCellEven" align="left"><b>M2 DE CONSTRUCCI&Oacute;N:</b></th>
		<td class="reportCellEven" align="left">
			<%= bmoPropertyModel.getConstructionSize().toString().toUpperCase() %> mt2
			(<%= amountByWord.getAmountByWord(bmoPropertyModel.getConstructionSize().toDouble()).toUpperCase() %> METROS CUADRADOS)
		</td>
	</tr>
	<tr>
		<th class="reportCellEven" align="left"><b>CARACTER&Iacute;STICAS:</b></th>
		<td class="reportCellEven" align="justify"><%= bmoPropertyModel.getDetails().toString().toUpperCase() %></td>
	</tr>
	<tr>
		<th class="reportCellEven" align="left"><b>ACABADOS:</b></th>
		<td class="reportCellEven" align="justify"><%= bmoPropertyModel.getFinishing().toString().toUpperCase() %></td>
	</tr>
	<tr>
		<th class="reportCellEven" align="left"><b>ADICIONALES:</b></th>
		<td class="reportCellEven" align="left">
		<%
			double adicionalAnexo1 = 0;
		    PmConn pmConnAdicionalesAnexo1 = new PmConn(sFParams);
		    pmConnAdicionalesAnexo1.open();
		    pmConnAdicionalesAnexo1.disableAutoCommit();
									
		  //Sacar adicionales-paquetes-accesorios Positivos
			String sqlVentaPedidosExtra = " SELECT * from orderpropertymodelextras " +
											" LEFT JOIN propertymodelextras ON(prmx_propertymodelextraid = orpx_propertymodelextraid) " +
											" WHERE orpx_orderpropertymodelextraid > 0 " +
											" AND orpx_orderid = " + bmoPropertySale.getOrderId().toInteger();
		    pmConnAdicionalesAnexo1.doFetch(sqlVentaPedidosExtra);
		
		    while(pmConnAdicionalesAnexo1.next()){
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
					<br>
				<%
	    		}
		    }
		    pmConnAdicionalesAnexo1.close();
		%>
		
		</td>
	</tr>
	<tr>
		<th class="reportCellEven" align="left"><b>OTROS:</b></th>
		<td class="reportCellEven" align="justify">
		    NO SE PERMITE LA COLOCACION DE TENDEDEROS EN LAS COCHERAS Y/O EN LOS BALCONES DE LA 
		    VIVIENDA, POR BIENESTAR Y DISPOSICION DE LOS COLONOS DEL FRACCIONAMIENTO.
		</td>
	</tr>
	<tr>
		<th class="reportCellEven" align="left"><b>FIRMA DE CLIENTE:</b></th>
		<td class="reportCellEven" align="left">
			&nbsp;<br>&nbsp;
		</td>
	</tr>
</table>

<p align="center">
	<b><%= bmoCityCompany.getName().toString().toUpperCase() %>, 
	<%= bmoCityCompany.getBmoState().getName().toString().toUpperCase() %>,  
	A <%= SFServerUtil.nowToString(sFParams, "dd") %> 
	DE <%= nowMonth.toUpperCase() %>, <%= SFServerUtil.nowToString(sFParams, "YYYY") %></b>
</p>


<p style="page-break-after: always"></p>

<table border="0" cellspacing="0" width="100%" cellpadding="0"  style="font-size: 12px">
	<tr>
		<td align="left" width="5%">			
			<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
		</td>
		<td class="reportTitleCell">ANEXO 2 DEL CONTRATO DE PROMESA DE COMPRA VENTA</td>
		<td class="reportTitleCell" style="text-align:right; font-size: 9px"><b>C&oacute;digo del Documento:<br>FO-07.2.2-3(30-Mar-04)&nbsp;</b></td>
	</tr>
</table>
	
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
	 <% 	
	 	
	    String sqlRacc;
	    sqlRacc= " SELECT racc_raccountid, racc_invoiceno, racc_total, racc_duedate, payt_name " +
	    			" FROM raccounts " + 
		    		" LEFT JOIN raccounttypes ON(ract_raccounttypeid = racc_raccounttypeid) " +
		    		" LEFT JOIN paymenttypes ON(payt_paymenttypeid = racc_paymenttypeid) " +
		    		" WHERE racc_orderid = " + bmoPropertySale.getOrderId().toInteger() + 
		    		" ORDER BY racc_duedate ASC, racc_invoiceno ASC";
	    
	    //System.out.println("sqlRacc: "+sqlRacc);
	    
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
	MEDIANTE EL PRESENTE ANEXO DECLARO TENER CONOCIMIENTO DE QUE EN EL SUPUESTO CASO DE NO PAGAR EN TIEMPO Y FORMA <%= a%> 
	DE LOS DOCUMENTOS POR COBRAR (PAGAR&Eacute;S) FIRMADOS POR MI PERSONA Y QUE REPRESENTAN EL PAGO DEL ENGANCHE DE LA VIVIENDA 
	QUE ESTOY ADQUIRIENDO, ACEPTO QUE EN ESTE SUPUESTO PERDER&Eacute; LA ASIGNACI&Oacute;N Y UBICACI&Oacute;N DEL INMUEBLE ELEGIDO Y ESPECIFICADO 
	EN EL PRESENTE CONTRATO DE COMPRA VENTA.
    
	
</p>
<p >
	A CONTINUACI&Oacute;N SE LISTAN LOS DOCUMENTOS POR COBRAR DEL ENGANCHE:
</p>
<table width="95%" align="center" border="0" cellpadding="0" cellspacing="0" style="font-size: 12px">
	<tr>
		<th class="reportHeaderCell">Documento</th>
		<th class="reportHeaderCell">M&eacute;todo de pago</th>
		<th class="reportHeaderCell">Fecha l&iacute;mite de pago</th>
		<th class="reportHeaderCellRight" width="10%" style="padding-right: 9px">Monto</th>
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
			    		<td class="reportCellEven" align = "right">
				        	<%= SFServerUtil.formatCurrency(pmConnRacc.getDouble("racc_total"))%>
			    		</td>
			   <%
			            i += 1;
			}
		} 
			//pmConnRacc.close();
	       %>			
	</tr>
</table>
<p>
	&nbsp;
</p>
<p align="justify" >
    <b>
	    DE IGUAL FORMA ESTOY DE ACUERDO EN QUE LA NUEVA UBICACI&Oacute;N SE ME REASIGNAR&Aacute; UNA VEZ QUE HAYA PAGADO LOS DOCUMENTOS 
		POR COBRAR (PAGAR&Eacute;S) DEL ENGANCHE QUE A LA FECHA ESTEN VENCIDOS DE ACUERDO A LA FECHA DE PAGO. LA NUEVA UBICACI&Oacute;N 
		SER&Aacute; ELEGIDA POR MI PERSONA, DE ACUERDO A LOS INMUEBLES DISPONIBLES EN EL MOMENTO DE LA NUEVA ASIGNACI&Oacute;N, PUDIENDO 
		NO SER EL MISMO INMUEBLE O INCLUSIVE EL MISMO FRACCIONAMIENTO QUE EL ESTABLECIDO EN EL PRESENTE CONTRATO DE COMPRA 
		VENTA, POR LO QUE SE ELABORAR&Aacute; UN NUEVO CONTRATO DE COMPRA VENTA.
    </b>
</p>


<table width="90%" align="center" border="0" cellpadding="0" cellspacing="0" style="font-size: 12px">
	<tr>
	    <td align="center">
	       	&nbsp;			        	
	    </td>
	    <td align="center">
	   		 <br>
			<!--<img src="../img/firma_lopez.jpg" width="60" height="30" >--><br>
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

<p style="page-break-after: always"></p>

<table border="0" cellspacing="0" width="100%" cellpadding="0"  style="font-size: 12px">
	<tr>
		<td align="left" width="5%">
			<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
		</td>
		<td class="reportTitleCell">ANEXO 3 REGLAMENTO DE COLONOS</td>
	</tr>
</table>
<p align="justify" style="font-size: 12px">
	El presente reglamento tiene como finalidad el logro de la mejor convivencia y calidad de vida de todos los propietarios u ocupantes del fraccionamiento, 
	contemplando la mejor conservaci&oacute;n de los bienes comunes, el aseo, la seguridad y salubridad de los propietarios.
	<br>
</p>
<p align="justify" style="font-size: 12px">
	<b>ART&Iacute;CULO 1</b><br>
	Todo propietario u ocupante est&aacute; obligado a sujetarse a las disposiciones del presente Reglamento de Colonos. 
	Es responsabilidad de los propietarios y/o ocupantes, cumplir y hacer cumplir las reglas establecidas en el presente reglamento, 
	aceptando y reconociendo su vigencia, validez e importancia, sujet&aacute;ndose a las disposiciones legales que implique el incumplimiento 
	u omisi&oacute;n de las mismas, y forman parte del compromiso de la compra venta.
</p>
	
<p align="justify" style="font-size: 12px">
	<b>ART&Iacute;CULO 2</b><br>
	El presente Reglamento de Colonos, forma parte del proyecto aprobado para el fraccionamiento <b><%= bmoDevelopment.getName().toString().toUpperCase() %></b>, 
	mismo que se encuentra debidamente registrado para su observancia en el Registro P&uacute;blico de la Propiedad, 
	la Direcci&oacute;n de Catastro as&iacute; como la Secretar&iacute;a de Desarrollo Urbano Municipal y/o Ordenamiento Territorial. 
</p>
<p align="justify" style="font-size: 12px">
	<b>ART&Iacute;CULO 3</b><br>
	Tu casa forma parte de un Fraccionamiento Municipalizado, no de un Conjunto o Condominio Privado. Esto significa que la dotaci&oacute;n de los 
	servicios tanto de Alumbrado P&uacute;blico, Recolecci&oacute;n de Basura, y Mantenimiento de Vialidades le corresponder&aacute; al 
	Municipio una vez municipalizado, y no a los propietarios u ocupantes del Fraccionamiento. Los servicios de Vigilancia Privada, 
	Mantenimiento de &Aacute;reas Verdes, entre otros servicios, s&iacute; son responsabilidad de los propietarios u ocupantes del Fraccionamiento.
</p>
<p align="justify" style="font-size: 12px">
	<b>ART&Iacute;CULO 4</b><br>
	Todas las Viviendas que integran el Fraccionamiento son para uso habitacional exclusivamente. Cada propietario u ocupante usar&aacute; 
	su vivienda en forma ordenada y tranquila, por lo tanto no podr&aacute; destinarlo a usos contrarios a la moral o a las buenas costumbres, 
	ni hacerlo servir a otro objeto distinto del habitacional, ni efectuar acto alguno que perturbe la tranquilidad de los dem&aacute;s propietarios u 
	ocupantes o que comprometa la solidez o seguridad, salubridad o comodidad del Fraccionamiento, o incurrir en omisiones que produzcan los mismos resultados.
</p>
<p align="justify" style="font-size: 12px">
	<b>ART&Iacute;CULO 5</b><br>
	La Asociaci&oacute;n de Colonos, o en tanto no sea constituida la Empresa Desarrolladora, reciben autoridad para que el tr&aacute;mite de solicitud 
	para la construcci&oacute;n de modificaciones y/o adecuaciones se inicie con la presentaci&oacute;n del proyecto por escrito. 
	El propietario u ocupante, no podr&aacute; realizar obras, sin contar con la autorizaci&oacute;n por escrito de la Empresa Desarrolladora o 
	de la Asociaci&oacute;n de Colonos si esta ya fue formalizada. Cualquier ampliaci&oacute;n o remodelaci&oacute;n de su vivienda podr&aacute; 
	hacerse &uacute;nicamente teniendo como base el proyecto estructural original. Se deber&aacute; respetar los materiales, colores y dem&aacute;s 
	caracter&iacute;sticas del proyecto, de lo contrario el administrador en curso, podr&aacute; solicitar a las autoridades la suspensi&oacute;n de las obras.
	<br><br>
	Podr&aacute; solicitar la autorizaci&oacute;n a la Empresa Desarrolladora al &aacute;rea de Atenci&oacute;n a Clientes mediante 
	correo electr&oacute;nico <b>clientes@mdm.mx</b>., o bien de manera escrita y entregada en las oficinas del desarrollador ubicadas en
	<b><%= bmoCompany.getStreet().toHtml()%> #1938 Interior 302 y 402. <%= bmoCompany.getNeighborhood().toHtml()%>,
	<%= bmoCityCompany.getName().toHtml()%>, <%= bmoCityCompany.getBmoState().getName().toHtml()%>.</b>
	<!--Blvd. Manuel J. Clouthier #1938 Interior 302 y 402. Residencial del Moral II, Le&oacute;n, Guanajuato. -->
	Una vez conformada la Asociaci&oacute;n de Colonos, ser&aacute; esta &uacute;ltima quien ostenta este facultad para autorizar dichas modificaciones.   
	<br><br>
	El propietario u ocupante ser&aacute; responsable de los da&ntilde;os y perjuicios que se causen a los dem&aacute;s 
	propietarios u ocupantes, quedando obligado a reparar por su exclusiva cuenta los da&ntilde;os que se hubieren causado. 
	<br><br>
	En caso de realizar obras sin la autorizaci&oacute;n citada, se dar&aacute; aviso a las autoridades correspondientes 
	para detener las obras y aplicar todas las sanciones que en derecho procedan. Adicionalmente, mientras subsista el incumplimiento, 
	el propietario u ocupante perder&aacute; los derechos de Apertura de Plumas en el Acceso, as&iacute; como la Vigilancia y Seguridad privada en su propiedad.
</p>
<p align="justify" style="font-size: 12px">
	<b>ART&Iacute;CULO 6</b><br>
	La Asociaci&oacute;n de Colonos y/o la Empresa Desarrolladora reportar&aacute; al Gobierno del Municipio, 
	las violaciones que cometan los propietarios de los predios, a las disposiciones de uso del suelo y construcci&oacute;n 
	establecidas en el Reglamento interno de Construcci&oacute;n, y por los gobiernos Federal, Estatal y Municipal.
</p>
<p align="justify" style="font-size: 12px">
	<b>ART&Iacute;CULO 7</b><br>
	Est&aacute; prohibida la tala o derribo de &aacute;rboles, La Asociaci&oacute;n de Colonos y/o la Empresa Desarrolladora 
	reportar&aacute; al propietario del predio en donde se talen o derriben &aacute;rboles ante la autoridad municipal competente.
</p>
<p align="justify" style="font-size: 12px">
	<b>ART&Iacute;CULO 8</b><br>
	Para el caso de adecuaciones a la vivienda objeto de este contrato en materia de "COCHERA", s&oacute;lo se permitir&aacute; 
	el dise&ntilde;o previamente autorizado y revisado por La Asociaci&oacute;n de Colonos y/o la Empresa Desarrolladora.
</p>
<p align="justify" style="font-size: 12px">
	<b>ART&Iacute;CULO 9</b><br>
	Para el caso de adecuaciones a la vivienda objeto de este contrato en materia de "ROOF GARDEN", s&oacute;lo se permitir&aacute; 
	el dise&ntilde;o previamente autorizado y revisado por La Asociaci&oacute;n de Colonos y/o la Empresa Desarrolladora.
</p>
<p align="justify" style="font-size: 12px">
	<b>ART&Iacute;CULO 10</b><br>
	No se permite lo construcci&oacute;n de bardas ni la colocaci&oacute;n de rejas de ning&uacute;n tipo en el frente de los predios 
	ni el las colindancias laterales del frente. Se proh&iacute;be tener corrales al frente, almacenar o reciclar fierro, cart&oacute;n o 
	cualquier otro material en las azoteas y cocheras.
</p>
<p align="justify" style="font-size: 12px">
	<b>ART&Iacute;CULO 11</b><br>
	A los propietarios u ocupantes les est&aacute; prohibida toda modificaci&oacute;n que afecte la imagen (fachada), estructura, 
	muros de carga u otros elementos, esenciales de las viviendas o que pueda perjudicar su solidez, seguridad, salubridad o comodidad. 
	<br><br>
	Se proh&iacute;be modificar las fachadas de las viviendas o colocar cualquier objeto o estructura en la misma. Todas las fachadas 
	de las viviendas deber&aacute;n de mantenerse en buen estado. Queda estrictamente prohibido cambiar el color de las fachadas de las viviendas, 
	&eacute;stas deber&aacute;n de conservar el mismo color que tiene desde la entrega de la misma.
</p>
<p align="justify" style="font-size: 12px">
	<b>ART&Iacute;CULO 12</b><br>
	Los propietarios u ocupantes, se obligan a no darle a la vivienda un uso distinto al se&ntilde;alado (uso habitacional), 
	y por lo tanto no podr&aacute;n establecer en ellos oficinas, comercios, dep&oacute;sitos, talleres, etc., 
	ni podr&aacute;n usar sus fachadas y &aacute;reas exteriores como bodegas, por lo que &eacute;stas tendr&aacute;n como &uacute;nico 
	uso el de cocheras para los autom&oacute;viles.
</p>
<p align="justify" style="font-size: 12px">
	<b>ART&Iacute;CULO 13</b><br>
	&Uacute;nicamente se permite la tenencia de mascotas dom&eacute;sticas, siempre y cuando se acaten las normas sanitarias y se cumpla 
	con las caracter&iacute;sticas de cantidad, tama&ntilde;o, raza y seguridad de los propietarios u ocupantes del fraccionamiento y 
	permanezcan dentro de las viviendas. Queda estrictamente prohibido tener animales que molesten o perturben la tranquilidad de los 
	vecinos (porcinos, ovinos, caprinos, etc. y en general cualquier mascota que genere ruidos, molestias, insalubridad o inseguridad a los propietarios u ocupantes).
</p>
<p align="justify" style="font-size: 12px">
	<b>ART&Iacute;CULO 14</b><br>
	La basura ser&aacute; depositada indistintamente en el lugar designado para ello; deber&aacute; de estar en bolsas pl&aacute;sticas bien amarradas, 
	no debe contener objetos cortantes, material descompuesto o animales muertos. Lo anterior con la finalidad de evitar malos olores y focos de infecci&oacute;n. 
</p>
<p align="justify" style="font-size: 12px">
	<b>ART&Iacute;CULO 15</b><br>
	No producir ruidos, ni ejecutar actos que perturben la tranquilidad de los propietarios.
</p>
<p align="justify" style="font-size: 12px">
	<b>ART&Iacute;CULO 16</b><br>
	Todos los propietarios u ocupantes tienen derecho a realizar fiestas en sus viviendas, pero es su deber respetar las normas establecidas, 
	todo en un ambiente moderado que no moleste a los dem&aacute;s propietarios u ocupantes.
</p>
<p align="justify" style="font-size: 12px">
	<b>ART&Iacute;CULO 17</b><br>
	Ning&uacute;n propietario u ocupante, puede sacar ropa a secar, en lugares a la vista exterior de sus fachadas o cocheras de sus viviendas, 
	azoteas, &aacute;reas comunes o &aacute;reas ajardinadas.
</p>
<p align="justify" style="font-size: 12px">
	<b>ART&Iacute;CULO 18</b><br>
	Queda prohibido el almacenamiento y posesi&oacute;n en sus viviendas o &aacute;reas comunes, 
	de sustancias qu&iacute;micas y/o explosivas que sean nocivas para la salud o que puedan causar incendios.
</p>
<p align="justify" style="font-size: 12px">
	<b>ART&Iacute;CULO 19</b><br>
	El l&iacute;mite m&aacute;ximo de velocidad permitido dentro del fraccionamiento es de <b>20 km/hr.</b>, este l&iacute;mite es aplicable a 
	cualquier tipo de veh&iacute;culo (autom&oacute;vil, cami&oacute;n de carga, motocicleta, cuatrimoto, bicicleta, etc.) 
	l&iacute;mite establecido en base al <b>REGLAMENTO DE TR&Aacute;NSITO MUNICIPAL DE LEON, Art&iacute;culo 7 Fracci&oacute;n VI, Inciso "C"</b>, 
	as&iacute; mismo deber&aacute;n de respetarse el sentido de circulaci&oacute;n establecido en las calles del fraccionamiento, 
	debiendo dar preferencia al peat&oacute;n en todos los accesos y cruces de calles.
</p>
<p align="justify" style="font-size: 12px">
	<b>ART&Iacute;CULO 20</b><br>
	Los habitantes del <b>FRACCIONAMIENTO</b> deber&aacute;n conformar la Asociaci&oacute;n de Colonos, mismo que ser&aacute; responsable de la contrataci&oacute;n 
	de servicios de Seguridad Privada, Mantenimiento de &Aacute;reas Verdes, entre otros, por lo que cada propietario u ocupante deber&aacute; 
	contribuir con el pago de una cuota para cubrir los gastos de la seguridad del fraccionamiento y de todos los bienes comunes de los que 
	gozan los propietarios u ocupantes.
</p>
<p align="justify" style="font-size: 12px">
	<b>ART&Iacute;CULO 21</b><br>
	En tanto la Asociaci&oacute;n de Colonos no se encuentre conformada, la Empresa Desarrolladora administrar&aacute; 
	la cobranza y aplicaci&oacute;n de las cuotas de mantenimiento, sin beneficio monetario; El propietario y ocupante deber&aacute; 
	realizar los pagos de las mismas, que ser&aacute;n destinadas en beneficio de todos los habitantes, para el pago de los siguientes servicios, entre otros:
	<ul style="text-align:justify;">
    	<li>17.1 - Seguridad Privada para Control de Acceso y Vigilancia Perimetral</li>
    	<li>17.2 - Mantenimiento de &Aacute;reas Verdes</li>
    	<li>17.3 - Mantenimiento de &Aacute;reas de Donaci&oacute;n</li>
    	<li>17.4 - Recolecci&oacute;n de basura*</li>
    	<li>17.5 - Alumbrado P&uacute;blico*</li>
    </ul>

	En caso que alg&uacute;n propietario u ocupante no realice los pagos de mantenimiento de manera puntual, perder&aacute; los derechos de Apertura de Plumas en el Acceso, Recolecci&oacute;n de Basura*, as&iacute; como la Vigilancia y Seguridad en su propiedad
	<br><br>
	* <i>En tanto no haya sido municipalizado el Fraccionamiento.</i>
</p>
<p align="justify" style="font-size: 12px">
	<b>ART&Iacute;CULO 22</b><br>
	Los propietarios u ocupantes deber&aacute;n procurar participar en las Asambleas del Fraccionamiento y, de ser posible, 
	en las actividades sociales y de recreaci&oacute;n que se organicen.
</p>
<p align="justify" style="font-size: 12px">
	<b>ART&Iacute;CULO 23</b><br>
	En el caso de Vivienda vertical, los colonos se obligan a mantener en buen estado la limpieza e imagen del edificio, 
	as&iacute; como a pagar las cuotas comunitarias que se establezcan para dicho fin con car&aacute;cter obligatorio.
</p>
<p align="justify" style="font-size: 12px">
	<b>ART&Iacute;CULO 24</b><br>
	Es obligaci&oacute;n de todos y cada uno de los propietarios u ocupante contribuir con la limpieza y orden de su vivienda, 
	evitando la acumulaci&oacute;n de basura y desperdicios fuera de su domicilio, depositar los desechos org&aacute;nicos en 
	bolsa de pl&aacute;stico herm&eacute;ticamente cerradas para evitar malos olores y el incremento de focos de infecci&oacute;n, 
	as&iacute; como abstenerse de tirar basura en la v&iacute;a p&uacute;blica, &aacute;reas verdes y lotes bald&iacute;os.
</p>
<p align="justify" style="font-size: 12px">
	<b>ART&Iacute;CULO 25</b><br>
	Cualquier queja o problema que se presente en contra de un propietario u ocupante por el incumplimiento de las normas aqu&iacute; establecidas, se har&aacute; acreedor a las sanciones establecidas en la Ley de Fraccionamientos y de la Asociaci&oacute;n de Colonos. 
	<br><br>
	Este reglamento tiene car&aacute;cter obligatorio para todos los propietarios u ocupantes de este fraccionamiento y pretende salvaguardar los intereses comunes, protegiendo la imagen urbana del fraccionamiento, la cordial convivencia y la plusval&iacute;a de su patrimonio.
</p>

	

<table width="90%" align="center" border="0" cellpadding="0" cellspacing="0" style="font-size: 12px">
	<tr>
		<td align="center">
			&nbsp;
		</td>
	</tr>
	<tr>
	    <td align="center">
	    	<b>Firma de Conformidad y Obligaci&oacute;n de Cumplir el Reglamento</b>
	    </td>
	</tr> 
	<tr>
	    <td align="center">
	    	&nbsp;
	    </td>
    </tr>
    <tr>
	    <td align="center">
	    	&nbsp;
	    </td>
	</tr>
	<tr>
	    <td align="center">
	    	
	        ______________________________
	    </td>
	</tr>    
	<tr>

	    <td align="center">
	    	<!--
		    <%//= bmoCustomer.getDisplayName().toString().toUpperCase() %>
			<br>
			<%//= bmoProperty.getStreet().toString().toUpperCase() %>
			# <%//= bmoProperty.getNumber().toString().toUpperCase() %>
			FRACCIONAMIENTO <%//= bmoDevelopment.getName().toString().toUpperCase() %>,
			ETAPA <%//= bmoDevelopmentPhase.getName().toString().toUpperCase() %>.
			-->
	    </td>
	</tr>
	<tr>
	    <td align="center">
	    	&nbsp;
	    </td>
	</tr>
	<tr>
	    <td align="center">
	    	&nbsp;
	    </td>
	</tr> 
</table>

<p style="page-break-after: always"></p>


<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr>
		<td align="left" width="5%">
			<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
		</td>
		<td class="reportTitleCell" valing="middle">
			ANEXO 3 PROGRAMA VECINO RESPONSABLE
		</td>
	</tr>
</table>
<br>
<p align="justify">
	<p align="center"><img border="0" src="<%= GwtUtil.getProperUrl(sFParams, "/img/vecino_responsable.png")%>" height="50" width="350"></p>
	<br>
	Para una mejor calidad de vida tu participaci&oacute;n es fundamental Programa Yo soy  Vecino Responsable.
	<br><br>
	- El Vecino Responsable es el que participa activamente en favor del bien com&uacute;n,
	en conjunto con la asociaci&oacute;n de colonos, practica principios b&aacute;sicos como ser siempre positivo,
	fomenta la uni&oacute;n y el respeto a sus vecinos, coordina o trabaja en conjunto con el comit&eacute;
	de colonos para lograr la armon&iacute;a en el fraccionamiento.
	Concilia los diferentes puntos de vista, dentro de un marco de respeto, equidad, participaci&oacute;n y trabajo coordinado.
	<br><br>
	- El Vecino Responsable cuida la calidad de vida de su hogar y est&aacute; consciente que de ella, no s&oacute;lo depende del bien inmueble que est&aacute; adquiriendo, ni del hecho de tener una casa nueva.
	Est&aacute; basada en como participa con sus vecinos para lograr que se respeten las leyes y el reglamento de su fraccionamiento.
	<br><br>
	- El vecino Responsable evita la desuni&oacute;n, la desconfianza, la apat&iacute;a y las influencias partidistas, pol&iacute;ticas, religiosas o de intereses particulares que no tomen en cuenta los valores y el provecho comunitarios.
	<br><br>
	- El vecino responsable lee con cuidado el Reglamento Interno y recurre a &eacute;l cada vez que sea necesario y lo pr&aacute;ctica. Platica con su familia sobre su contenido y hace que los miembros de su hogar lo respeten.
	<br><br>
	- El Vecino Responsable comenta con los vecinos y amigos lo que dicen estos documentos.
	<br><br>
	
	Desde el momento en que adquieres una casa con Corporaci&oacute;n MDM formas parte de este programa, y de ti depende que se sumen esfuerzos, habilidades aptitudes y conocimientos para un mismo fin.
	<br>
</p>
<p align="center">
	Yo me comprometo a ser un vecino responsable.
	<br><br>
</p>
<p style="page-break-after: always"></p>

<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr>
		<td width="5%">
			<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
		</td>
		<td class="reportTitleCell">
			ANEXO 4 DEL CONTRATO DE PROMESA DE COMPRA VENTA VIVIENDA CON ECOTECNOLOG&Iacute;A
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
			ANEXO 5 LUGAR Y FORMA DE PAGOS
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

<p style="page-break-after: always"></p>
<% if(bmoCustomer.TYPE_PERSON == 'P'){ %>
<table class="detailText" style="font-size: 5pt;" border="0" align="center" cellpading="0" cellspacing="0" width="95%"  >
<caption>
    <b>Formato de Identificaci&oacute;n del cliente persona f&iacute;sica y que declaren ser de Nacionalidad Mexicana o de Nacionalidad Extranjera con las condiciones de Residente
    Temporal o Permanente</b> <br>
    (Favor de ser llenado con letra de molde de pu&ntilde;o y letra y de forma clara)
</caption>
<table class="detailText" style="font-size: 5pt;" border="0" align="center" cellpading="0" cellspacing="0" width="95%">
    <tr>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="5" >
             <table class="detailText" style="font-size: 5pt;" border="0" align="center" cellpading="0" cellspacing="0" width="100%">
                 <tr>
                     <td class="detailText" style="font-size: 5pt;" align="center" bgcolor="gray">
                         <b>Por este medio proporciono los datos y documentos requeridos con la &uacute;nica finalidad de
                         identificarme</b>
                     </td> 
                 </tr>
             </table>
        </td>
    </tr>
    <tr><td colspan="5">&nbsp;</td> </tr>
    <tr>
        <td align="center" class="detailText" style="font-size: 5pt;" colspan="5">
            Fecha: ___________________________________________________________________________________________________________________________________________________________________________________________________________________________
        </td>
    </tr>
    <tr><td colspan="5">&nbsp;</td> </tr>
    <tr>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="5">
            Nombre del Cliente: ________________________________________________________________________________________________________________________________________________________________________________________________________________
        </td>
        </td>
    </tr>
    <tr>
        <td class="detailText" style="font-size: 5pt;"  align="center" width="20%">
            (Sin abreviaturas)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center">
            Apellido Paterno
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center" width="25%">
            Apellido Materno
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="2">
            Nombre(s)
        </td>
    </tr>
    <tr><td colspan="5">&nbsp;</td> </tr>
    <tr>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="5">
            ___________________________________________________________________________________________________________________________________________________________________________________________________________________________________
        </td>
    </tr>
    <tr>
        <td class="detailText" style="font-size: 5pt;" align="center">
            Fecha de Nacimiento
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center">
            Pa&iacute;s de Nacimiento
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center">
            Nacionalidad
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="2">
            R.F.C. (En caso de contar con el)
        </td>
    </tr>
    <tr><td colspan="5">&nbsp;</td> </tr>
     <tr>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="5">
            ___________________________________________________________________________________________________________________________________________________________________________________________________________________________________
        </td>
    </tr>
    <tr>
        <td align="center" class="detailText" style="font-size: 5pt;" colspan="5">
            CURP (En caso de contar con el)
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            Actividad, Ocupaci&oacute;n, Profesi&oacute;n o Giro del negocio
        </td>
    </tr>
    <tr><td colspan="5">&nbsp;</td> </tr>
     <tr>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="5">
            ____________________________________________________________________________________________________________________________________________________________________________________________________________________________________
        </td>
    </tr>
    <tr>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="5">
            Tipo de identificaci&oacute;n emitida por autoridad local o federal
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;
            N&uacute;mero de folio
        </td>

    </tr>
    <tr><td colspan="5">&nbsp;</td> </tr>
     <tr>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="5">
            ___________________________________________________________________________________________________________________________________________________________________________________________________________________________________
        </td>
    </tr>
    <tr>
        <td class="detailText" style="font-size: 5pt;" align="center">
            Domicilio particular
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center">
            Calle, Avenida &oacute; v&iacute;a
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center">
            N&uacute;mero exterior
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="2">
            N&uacute;mero interior
        </td>
    </tr>
    <tr><td colspan="5">&nbsp;</td> </tr>
    <tr>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="5">
            ___________________________________________________________________________________________________________________________________________________________________________________________________________________________________
        </td>
    </tr>
    <tr>
        <td class="detailText" style="font-size: 5pt;" align="center">
            Colonia
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="2">
            Delegaci&oacute;n/Municipio/Demarcaci&oacute;n pol&iacute;tica
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            Pa&iacute;s
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="2">
            Entidad Federativa/Estado
        </td>

    </tr>
    <tr><td colspan="5">&nbsp;</td> </tr>
    <tr>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="5">
            ____________________________________________________________________________________________________________________________________________________________________________________________________________________________________
        </td>
    </tr>
    <tr>
        <td class="detailText" style="font-size: 5pt;" align="center">
            C.P.
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="2">
            Ciudad/Poblaci&oacute;n
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="2">
            Celular
        </td>
    </tr>
    <tr><td class="detailText" style="font-size: 5pt;" colspan="5">&nbsp;</td> </tr>
    <tr>
        <td align="center" class="detailText" style="font-size: 5pt;" colspan="5">
            ___________________________________________________________________________________________________________________________________________________________________________________________________________________________________
        </td>
    </tr>
    <tr>
        <td class="detailText" style="font-size: 5pt;" align="center">
            Tel&eacute;fono con clave lada
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center">
            Extensi&oacute;n
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center">
            Tel&eacute;fono 2 Incluir clave lada
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center">
            Extensi&oacute;n
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center">
            Correo electr&oacute;nico
        </td>
    </tr>
</table>
<table class="detailText" style="font-size: 5pt;" border="0" align="center" cellpading="0" cellspacing="0" width="95%">
    <tr>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="5" >
            <table class="detailText" style="font-size: 5pt;" border="0" align="center" cellpading="0" cellspacing="0" width="100%">
                 <tr>
                     <td class="detailText" style="font-size: 5pt;" align="center" bgcolor="gray">
                        <b>En caso de ser Extranjero y que cuente con domicilio en territorio nacional en donde reciba correspondencia, requisitar los siguientes Datos.
                        <br>Por este medio proporciono los datos y documentos requeridos con la &uacute;nica finalidad de identificarme</b>
                     </td>
                 </tr>
             </table>
        </td>
    </tr>
    <tr><td colspan="5">&nbsp;</td> </tr>
    <tr>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="5">
            ___________________________________________________________________________________________________________________________________________________________________________________________________________________________________
        </td>
    </tr>
    <tr>
        <td class="detailText" style="font-size: 5pt;" align="center">
            Domicilio particular
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center">
            Calle, Avenida &oacute; v&iacute;a
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center">
            N&uacute;mero exterior
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="2">
            N&uacute;mero interior
        </td>
    </tr>
    <tr><td colspan="5">&nbsp;</td> </tr>
    <tr>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="5">
            ___________________________________________________________________________________________________________________________________________________________________________________________________________________________________
        </td>
    </tr>
    <tr>
        <td class="detailText" style="font-size: 5pt;" align="center">
            Colonia
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="2">
            Delegaci&oacute;n/Municipio/Demarcaci&oacute;n pol&iacute;tica
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center">
            Pa&iacute;s
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center" >
            Entidad Federativa/Estado
        </td>

    </tr>
    <tr><td colspan="5">&nbsp;</td> </tr>
    <tr>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="5">
            ___________________________________________________________________________________________________________________________________________________________________________________________________________________________________
        </td>
    </tr>
    <tr>
        <td class="detailText" style="font-size: 5pt;" align="center">
            C.P.
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="2">
            Ciudad/Poblaci&oacute;n
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="2">
            Celular
        </td>
    </tr>
    <tr><td colspan="5">&nbsp;</td> </tr>
    <tr>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="5">
            ___________________________________________________________________________________________________________________________________________________________________________________________________________________________________
        </td>
    </tr>
    <tr>
        <td class="detailText" style="font-size: 5pt;" align="center">
            RFC
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center">
            CURP
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center">
            Tipo de Identificaci&oacute;n
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center">
            N&uacute;mero de Folio
        </td>

    </tr>
    <tr>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="5">
            El acto u operaci&oacute;n celebrada con la presente Inmobiliaria ser&aacute; para beneficio propio y no tengo conocimiento de la existencia de alg&uacute;n due&ntilde;o beneficiario.
            (En caso de conocer al Due&ntilde;o Beneficiario, requisitar lo siguiente)
        </td>
    </tr>
</table>
<table class="detailText" style="font-size: 5pt;" border="0" align="center" cellpading="0" cellspacing="0" width="95%">
    <caption>
        <b>Formato de Identificaci&oacute;n del Beneficiario Controlador</b>
    </caption>
    <tr>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="5" >
            <table class="detailText" style="font-size: 5pt;" border="0" align="center" cellpading="0" cellspacing="0" width="100%">
                 <tr>
                     <td class="detailText" style="font-size: 5pt;" align="center" bgcolor="gray">
                        <b>Por este medio proporciono los datos y documentos requeridos con la &uacute;nica finalidad de identificarme</b>
                     </td>
                 </tr>
             </table>
        </td>
    </tr>
    <tr><td colspan="5">&nbsp;</td> </tr>
    <tr>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="5">
            ___________________________________________________________________________________________________________________________________________________________________________________________________________________________________
        </td>
    </tr>
    <tr>
        <td class="detailText" style="font-size: 5pt;" align="center" width=17%">
            Apellido Paterno
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center">
            &nbsp;&nbsp;&nbsp;Apellido Materno
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center"  colspan="3">
            Nombre(s)
        </td>
    </tr>
    <tr><td colspan="5">&nbsp;</td> </tr>
    <tr>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="5">
            ___________________________________________________________________________________________________________________________________________________________________________________________________________________________________
        </td>
    </tr>
    <tr>
        <td class="detailText" style="font-size: 5pt;" align="center">
            Fecha de Nacimiento
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center">
            Pa&iacute;s de Nacionalidad
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center">
            Nacionalidad
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="2">
            R.F.C. (En caso de contar con el)
        </td>
    </tr>
    <tr><td colspan="5">&nbsp;</td> </tr>
     <tr>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="5">
            ___________________________________________________________________________________________________________________________________________________________________________________________________________________________________
        </td>
    </tr>
    <tr>
        <td  class="detailText" style="font-size: 5pt;" align="center" colspan="5">
            CURP (En caso de contar con el)
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            Actividad, Ocupaci&oacute;n o Profesi&oacute;n
        </td>
    </tr>
    <tr><td colspan="5">&nbsp;</td> </tr>
     <tr>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="5">
            ___________________________________________________________________________________________________________________________________________________________________________________________________________________________________
        </td>
    </tr>
    <tr>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="5">
            Tipo de identificaci&oacute;n emitida por autoridad local o federal
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            N&uacute;mero de folio
        </td>

    </tr>
    <tr><td colspan="5">&nbsp;</td> </tr>
     <tr>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="5">
            ___________________________________________________________________________________________________________________________________________________________________________________________________________________________________
        </td>
    </tr>
    <tr>
        <td class="detailText" style="font-size: 5pt;" align="center">
            Domicilio particular
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center">
            Calle, Avenida &oacute; v&iacute;a
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center">
            N&uacute;mero exterior
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="2">
            N&uacute;mero interior
        </td>
    </tr>
    <tr><td colspan="5">&nbsp;</td> </tr>
    <tr>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="5">
            ___________________________________________________________________________________________________________________________________________________________________________________________________________________________________
        </td>
    </tr>
    <tr>
        <td class="detailText" style="font-size: 5pt;" align="center">
            Colonia
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="2">
            Delegaci&oacute;n/Municipio/Demarcaci&oacute;n pol&iacute;tica
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            Pa&iacute;s
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="2">
            Entidad Federativa/Estado
        </td>

    </tr>
    <tr><td colspan="5">&nbsp;</td> </tr>
    <tr>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="5">
            ___________________________________________________________________________________________________________________________________________________________________________________________________________________________________
        </td>
    </tr>
    <tr>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="5">
            C.P.
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            Ciudad/Poblaci&oacute;n
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            Celular
        </td>
    </tr>
    <tr><td colspan="5">&nbsp;</td> </tr>
    <tr>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="5">
            ___________________________________________________________________________________________________________________________________________________________________________________________________________________________________
        </td>
    </tr>
    <tr>
        <td class="detailText" style="font-size: 5pt;" align="center">
            Tel&eacute;fono con clave lada
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center">
            Extensi&oacute;n
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center">
            Tel&eacute;fono 2 Incluir clave lada
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center">
            Extensi&oacute;n
        </td>
        <td class="detailText" style="font-size: 5pt;" align="center">
            Correo electr&oacute;nico
        </td>
    </tr>
</table>
<table class="detailText" style="font-size: 5pt;" border="0" align="center" cellpading="0" cellspacing="0" width="95%">
    <tr>
        <td class="detailText" style="font-size: 5pt;" align="center" colspan="5">
            <table class="detailText" style="font-size: 5pt;" border="0" align="center" cellpading="0" cellspacing="0" width="100%">
                 <tr>
                     <td class="detailText" style="font-size: 5pt;" align="center" bgcolor="gray">
                        <b>AGREGO A LA PRESENTE COPIA SIMPLE LEGIBLE DE LOS SIGUIENTES DOCUMENTOS PREVIAMENTE COTEJADOS</b>
                     </td>
                 </tr>
             </table>
        </td>
    </tr>
    <tr align="center">
        <td class="detailText" style="font-size: 5pt;" width="75%">
            <b>DEL CLIENTE:</b>
        </td>
        <td class="detailText" style="font-size: 5pt;" >
            <b>DEL BENEFICIARIO CONTROLADOR:</b>
        </td>
    </tr>
    <tr>
        <td class="detailText" style="font-size: 5pt;" width="75%">
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="checkbox">IDENTIFICACI&Oacute;N OFICIAL DEL CLIENTE
            (En caso de ser extranjero Pasaporte o Documento expedido por Migraci&oacute;n que acredite su condici&oacute;n en el Pa&iacute;s).
        </td>
        <td class="detailText" style="font-size: 5pt;" >
            <input type="checkbox">IDENTIFICACION OFICIAL
        </td>
    </tr>
    <tr>
        <td class="detailText" style="font-size: 5pt;" width="75%">
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="checkbox">CURP
        </td>
        <td class="detailText" style="font-size: 5pt;" >
            <input type="checkbox">CURP
        </td>
    </tr>
    <tr>
        <td class="detailText" style="font-size: 5pt;" width="75%">
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="checkbox">COMPROBANTE DE DOMICILIO
            (En caso de ser extranjero recabar ambos comprobantes y
            no mayor a tres meses a su fecha de emisi&oacute;n.)
        </td>
        <td class="detailText" style="font-size: 5pt;" >
            <input type="checkbox">COMPROBANTE DE DOMICILIO
        </td>
    </tr>
    <tr>
        <td class="detailText" style="font-size: 5pt;" width="75%">
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="checkbox">RFC
        </td>
        <td class="detailText" style="font-size: 5pt;" >
            <input type="checkbox">RFC
        </td>
    </tr>
    <tr>
        <td class="detailText" style="font-size: 5pt;" colspan="4">
            <br><br><br>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            Manifiesto que he tenido a la vista los documentos originales para cotejo. _______________________________________________________________
            <br>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            Nombre y firma del funcionario de la Inmobiliaria
        </td>
    </tr>
    <tr>
        <td style="font-size: 5pt;" align="center" colspan="4">
           <b>Declaro bajo protesta de decir verdad que todos y cada uno de los datos proporcionados son verdaderos.</b>
        </td>
    </tr>
    <tr>
        <td style="font-size: 5pt;" align="center" colspan="4">
            <br><br>
            _________________________________________________________________________________________________________
        </td>
    </tr>
    <tr>
        <td style="font-size: 5pt;" align="center" colspan="4">
            <b>Nombre y Firma del Cliente</b>
        </td>
    </tr>

</table>

</table>
<% } else { %>
            <table class="detailText" style="font-size: 5pt;" border="0" align="center" cellpading="0" cellspacing="0" width="95%"  >
                <caption>
                    <b>Formato de Identificaci&oacute;n del cliente Persona Moral con Nacionalidad Mexicana
                    </b> <br>
                    (Favor de ser llenado con letra de molde de pu&ntilde;o y letra y de forma clara)
                </caption>
                <table class="detailText" style="font-size: 5pt;" border="0" align="center" cellpading="0" cellspacing="0" width="95%">
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" align="center" colspan="6" >
                            <table class="detailText" style="font-size: 5pt;" border="0" align="center" cellpading="0" cellspacing="0" width="100%">
                                <tr>
                                    <td class="detailText" style="font-size: 5pt;" align="center" bgcolor="gray">
                                        <b>Por este medio proporciono los datos y documentos requeridos con la &uacute;nica finalidad de
                                        identificar a la persona moral a la que represento
                                        </b>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <tr><td colspan="6">&nbsp;</td> </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" colspan="6">
                            Fecha: _______________________________________________________________________________________________________________________________________________________________________________________________________________________________
                        </td>
                    </tr>
                    <tr><td colspan="6">&nbsp;</td> </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" colspan="6">
                            ______________________________________________________________________________________________________________________________________________________________________________________________________________________________________
                        </td>
                    </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" width="20%" colspan="6">
                            Raz&oacute;n o denominaci&oacute;n social (Sin abreviatura)
                        </td>
                    </tr>
                    <tr><td colspan="6">&nbsp;</td> </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" colspan="6">
                            _______________________________________________________________________________________________________________________________________________________________________________________________________________________________________
                        </td>
                    </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" align="center" colspan="2">
                            Fecha de Constituci&oacute;n
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center" colspan="2">
                            Pa&iacute;s de Nacionalidad
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center" colspan="2">
                            R.F.C.
                        </td>
                    </tr>
                    <tr><td colspan="6">&nbsp;</td> </tr>
                     <tr>
                        <td class="detailText" style="font-size: 5pt;" colspan="6">
                            ________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
                        </td>
                    </tr>
                    <tr>
                        <td align="center" class="detailText" style="font-size: 5pt;" colspan="3">
                            Actividad, Giro mercantile u Objeto social que desempe&ntilde;e
                        </td>
                        <td align="center" class="detailText" style="font-size: 5pt;" colspan="3">
                            Fecha de registro en el registro p&uacute;blico de la propiedad
                        </td>
                    </tr>
                    <tr><td colspan="6">&nbsp;</td> </tr>
                     <tr>
                        <td class="detailText" style="font-size: 5pt;" colspan="6">
                            ________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
                        </td>
                    </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" align="center" colspan="2">
                            Domicilio
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center" colspan="2">
                            Calle, Avenida &oacute; v&iacute;a
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center">
                            N&uacute;mero exterior
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center">
                            N&uacute;mero interior
                        </td>
                    </tr>
                    <tr><td colspan="6">&nbsp;</td> </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" colspan="6">
                            ________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
                        </td>
                    </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" align="center" colspan="2">
                            Colonia
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center" colspan="">
                            Delegaci&oacute;n/Municipio/Demarcaci&oacute;n pol&iacute;tica
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center">
                            Pa&iacute;s
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center">
                            Ciudad/Poblaci&oacute;n
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center">
                            Entidad Federativa/Estado
                        </td>
                    </tr>
                    <tr><td class="detailText" style="font-size: 5pt;" colspan="6">&nbsp;</td> </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" colspan="6">
                            ________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
                        </td>
                    </tr>
                    <tr>
                         <td class="detailText" style="font-size: 5pt;" align="center">
                            C.P.
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center">
                            Tel&eacute;fono 1 con Clave Lada
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center">
                            Extensi&oacute;n &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center">
                            Tel&eacute;fono 2 Incluir Clave Lada
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center">
                            Extensi&oacute;n
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center">
                            Correo electr&oacute;nico
                        </td>
                    </tr>
                </table>
                <table class="detailText" style="font-size: 5pt;" border="0" align="center" cellpading="0" cellspacing="0" width="95%">
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" align="center" colspan="6" >
                            <table class="detailText" style="font-size: 5pt;" border="0" align="center" cellpading="0" cellspacing="0" width="100%">
                                <tr>
                                    <td class="detailText" style="font-size: 5pt;" align="center" bgcolor="gray">
                                        <b>DATOS DEL REPRESENTANTE LEGAL</b>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <tr><td colspan="6">&nbsp;</td> </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" colspan="6">
                            ________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
                        </td>
                        </td>
                    </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" align="center" colspan="2">
                            Apellido Paterno
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center" colspan="2">
                            Apellido Materno
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center" colspan="2">
                            Nombre(s)(Sin abreviaturas)
                        </td>
                    </tr>
                    <tr><td colspan="6">&nbsp;</td> </tr>
                     <tr>
                        <td class="detailText" style="font-size: 5pt;" colspan="6">
                            ________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
                        </td>
                    </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" align="center" colspan="2">
                            Domicilio
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center" colspan="2">
                            Calle, Avenida &oacute; v&iacute;a
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center">
                            N&uacute;mero exterior
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center">
                            N&uacute;mero interior
                        </td>
                    </tr>
                    <tr><td colspan="6">&nbsp;</td> </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" colspan="6">
                            ________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
                        </td>
                    </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" align="center" colspan="2">
                            Colonia
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center" colspan="2">
                            Delegaci&oacute;n/Municipio/Demarcaci&oacute;n pol&iacute;tica
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center">
                            Pa&iacute;s
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center">
                            Entidad Federativa/Estado
                        </td>
                    </tr>
                    <tr><td colspan="6">&nbsp;</td> </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" colspan="6">
                            ________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
                        </td>
                    </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" align="center" colspan="2">
                            C.P.
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center" colspan="2">
                            Ciudad/Poblaci&oacute;n
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center" colspan="2">
                            Celular
                        </td>
                    </tr>
                    <tr><td class="detailText" style="font-size: 5pt;" colspan="6">&nbsp;</td> </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" colspan="6">
                            ________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
                        </td>
                    </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" align="center" colspan="2">
                            Tel&eacute;fono 1 con Clave Lada
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center">
                            Extensi&oacute;n
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center" colspan="2">
                            Tel&eacute;fono 2 Incluir Clave Lada
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center">
                            Extensi&oacute;n
                        </td>
                    </tr>
                    <tr><td class="detailText" style="font-size: 5pt;" colspan="6">&nbsp;</td> </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" colspan="6">
                            ________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
                        </td>
                    </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" align="center">
                            RFC
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center">
                            CURP
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center" colspan="2">
                            Tipo de Identificaci&oacute;n
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center">
                            N&uacute;mero de Folio
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center">
                            Fecha del Poder
                        </td>
                    </tr>
                    
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" colspan="6">
                            <br>El acto u operaci&oacute;n celebrada con la presente Inmobiliaria ser&aacute; para beneficio propio y no tengo conocimiento de la existencia de alg&uacute;n due&ntilde;o beneficiario. (En caso de conocer al Due&ntilde;o Beneficiario, requisitar lo
                            siguiente).
                        </td>
                    </tr>
                </table>
                <table class="detailText" style="font-size: 5pt;" border="0" align="center" cellpading="0" cellspacing="0" width="95%">
                    <caption>
                        <b>Formato de Identificaci&oacute;n del Beneficiario Controlador</b>
                    </caption>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" align="center" colspan="5" >
                            <table class="detailText" style="font-size: 5pt;" border="0" align="center" cellpading="0" cellspacing="0" width="100%">
                                <tr>
                                    <td class="detailText" style="font-size: 5pt;" align="center" bgcolor="gray">
                                        <b>Por este medio proporciono los datos y documentos requeridos con la &uacute;nica finalidad de identificarme
                                        </b>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <tr><td colspan="5">&nbsp;</td> </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" colspan="5">
                            ________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
                        </td>
                    </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" align="center" width=17%">
                            Apellido Paterno
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center">
                            &nbsp;&nbsp;&nbsp;Apellido Materno
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center"  colspan="3">
                            Nombre(s)
                        </td>
                    </tr>
                    <tr><td colspan="5">&nbsp;</td> </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" colspan="5">
                            ________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
                        </td>
                    </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" align="center">
                            Fecha de Nacimiento
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center">
                            Pa&iacute;s de Nacionalidad
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center">
                            Nacionalidad
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center" colspan="2">
                            R.F.C. (En caso de contar con el)
                        </td>
                    </tr>
                    <tr><td colspan="5">&nbsp;</td> </tr>
                     <tr>
                        <td class="detailText" style="font-size: 5pt;" colspan="5">
                            ________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
                        </td>
                    </tr>
                    <tr>
                        <td  class="detailText" style="font-size: 5pt;" align="center" colspan="5">
                            CURP (En caso de contar con el)
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            Actividad, Ocupaci&oacute;n o Profesi&oacute;n
                        </td>
                    </tr>
                    <tr><td colspan="5">&nbsp;</td> </tr>
                     <tr>
                        <td class="detailText" style="font-size: 5pt;" colspan="5">
                            ________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
                        </td>
                    </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" align="center" colspan="5">
                            Tipo de identificaci&oacute;n emitida por autoridad local o federal
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            N&uacute;mero de folio
                        </td>

                    </tr>
                    <tr><td colspan="5">&nbsp;</td> </tr>
                     <tr>
                        <td class="detailText" style="font-size: 5pt;" colspan="5">
                            ________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
                        </td>
                    </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" align="center">
                            Domicilio particular
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center">
                            Calle, Avenida &oacute; v&iacute;a
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center">
                            N&uacute;mero exterior
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center" colspan="2">
                            N&uacute;mero interior
                        </td>
                    </tr>
                    <tr><td colspan="5">&nbsp;</td> </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" colspan="5">
                            ________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
                        </td>
                    </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" align="center">
                            Colonia
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center" colspan="3">
                            Delegaci&oacute;n/Municipio/Demarcaci&oacute;n pol&iacute;tica
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            Pa&iacute;s
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center" >
                            Entidad Federativa/Estado
                        </td>

                    </tr>
                    <tr><td colspan="5">&nbsp;</td> </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" colspan="5">
                            ________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
                        </td>
                    </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" align="center" colspan="5">
                            C.P.
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            Ciudad/Poblaci&oacute;n
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            Celular
                        </td>
                    </tr>
                    <tr><td colspan="5">&nbsp;</td> </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" colspan="5">
                            ________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
                        </td>
                    </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" align="center">
                            Tel&eacute;fono 1 con Clave Lada
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center">
                            Extensi&oacute;n
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center">
                            Tel&eacute;fono 2 Incluir Clave Lada
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center">
                            Extensi&oacute;n
                        </td>
                        <td class="detailText" style="font-size: 5pt;" align="center">
                            Correo electr&oacute;nico
                        </td>
                    </tr>
                </table>
                <table class="detailText" style="font-size: 5pt;" border="0" align="center" cellpading="0" cellspacing="0" width="95%">
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" align="center" colspan="3" >
                            <table class="detailText" style="font-size: 5pt;" border="1" align="center" cellpading="0" cellspacing="0" width="100%">
                                <tr>
                                    <td class="detailText" style="font-size: 5pt;" align="center" bgcolor="gray">
                                        <b>AGREGO A LA PRESENTE COPIA SIMPLE LEGIBLE DE LOS SIGUIENTES DOCUMENTOS PREVIAMENTE COTEJADOS
                                        </b>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <tr align="center">
                        <td class="detailText" style="font-size: 5pt;">
                            <b>DE LA EMPRESA</b>
                        </td>
                        <td class="detailText" style="font-size: 5pt;">
                            <b>DEL APODERADO LEGAL:</b>
                        </td>
                        <td class="detailText" style="font-size: 5pt;">
                            <b>BENEFICIARIO CONTROLADOR:</b>
                        </td>
                    </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;">
                            <input type="checkbox">ACTA CONSTITUTIVA DEBIDAMENTE INSCRITA EN EL REGISTRO PÚBLICO
                        </td>
                        <td class="detailText" style="font-size: 5pt;">
                            <input type="checkbox">IDENTIFICACI&Oacute;N OFICIAL
                        </td>
                        <td class="detailText" style="font-size: 5pt;" >
                            <input type="checkbox">IDENTIFICACI&Oacute;N OFICIAL
                        </td>
                    </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;">
                            <input type="checkbox">C&Eacute;DULA DE IDENTIFICACI&Oacute;N FISCAL
                        </td>
                        <td class="detailText" style="font-size: 5pt;">
                            <input type="checkbox">CURP
                        </td>
                        <td class="detailText" style="font-size: 5pt;" >
                            <input type="checkbox">CURP
                        </td>
                    </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" >
                            <input type="checkbox">CONSTANCIA DE INSCRIPCI&Oacute;N EN EL REGISTRO FEDERAL DE CONTRIBUYENTES
                        </td>
                        <td class="detailText" style="font-size: 5pt;">
                            <input type="checkbox">COMPROBANTE DE DOMICILIO
                            (No mayor a 3 meses a su fecha de emisi&oacute;n.)
                        </td>
                        <td class="detailText" style="font-size: 5pt;" >
                            <input type="checkbox">COMPROBANTE DE DOMICILIO
                            (No mayor a 3 meses a su fecha de emisi&oacute;n.)
                        </td>
                    </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;">
                            <input type="checkbox">COMPROBANTE DE DOMICILIO
                            (No mayor a 3 meses a su fecha de emisi&oacute;n.)
                        </td>
                        <td class="detailText" style="font-size: 5pt;">
                            <input type="checkbox">RFC
                        </td>
                        <td class="detailText" style="font-size: 5pt;" >
                            <input type="checkbox">RFC
                        </td>
                    </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;">
                            &nbsp;
                        </td>
                        <td class="detailText" style="font-size: 5pt;">
                            <input type="checkbox">COPIA DEL PODER
                        </td>
                        <td class="detailText" style="font-size: 5pt;">
                            &nbsp;
                        </td>
                    </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" colspan="3">
                            Manifiesto que he tenido a la vista los documentos originales para cotejo. _____________________________________________________________________________________________________________________________
                            <br>
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            Nombre y firma del funcionario de la Inmobiliaria
                        </td>
                    </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" align="center" colspan="3">
                            <b>Declaro bajo protesta de decir verdad que todos y cada uno de los datos proporcionados son verdaderos.</b>
                        </td>
                    </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" align="center" >
                            <br><br>
                            __________________________________________________________________________________________
                        </td>
                        <td></td>
                        <td class="detailText" style="font-size: 5pt;" align="center" >
                            <br><br>
                            __________________________________________________________________________________________
                        </td>
                    </tr>
                    <tr>
                        <td class="detailText" style="font-size: 5pt;" align="center" >
                            <b>NOMBRE O RAZ&Oacute;N SOCIAL DEL CLIENTE</b>
                        </td>
                        <td></td>
                        <td class="detailText" style="font-size: 5pt;" align="center" >
                            <b>NOMBRE Y FIRMA DEL APODERADO LEGAL</b>
                        </td>
                    </tr>
                </table>
            </table>
    <% } %>

<% 	} catch (Exception e) { 
	String errorLabel = "Error de Contrato";
	String errorText = "El Contrato no pudo ser desplegado exitosamente. Es necesario completar todos los datos faltantes: Venta de Inmuebles.";
	String errorException = e.toString();
	
	response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);
	}
	
	finally{
		pmConnRacc.close();
	}

%>
</body>
</html>



