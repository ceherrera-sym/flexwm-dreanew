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
	String title = "Contrato Casas";
	
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
			Clave:<%= bmoPropertySale.getCode().toString() %>
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
	        Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.

	    </li>
	    <br>
	    <li >
	        Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.

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
	        Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
:
	        <ol type="a">
	            <li>
	                Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.

	            </li>
	            <li>
	                Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.

	            </li>
	            <li>
	                Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.

	            </li>
	            <li>
	                Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
	            </li>
	            <li>
	                Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.

	            </li>
	            <li>
				Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
	            </li>
	            <li>
	                Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.

	            </li>
	            <li>
	                Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.

	            </li>
	            <li>
	                Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.

	            </li>
	        </ol>
	    </li>
	</ol>
</p>

<p align="justify" >
<b>III.- Declaran ambas</b> 	        Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.


</p>

<p align="center" class="contractTitle">
	<b>C L &Aacute; U S U L A S:</b>
</p>

<p align="justify" >
	<b>PRIMERA.-Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.

</p>

<p align="justify" >
	<b> SEGUNDA.- El <b>"COMPRADOR"</b>  conviene en pagar al <b>"VENDEDOR"</b> y que se fija de com&uacute;n acuerdo como precio
	de la vivienda la cantidad correspondiente a <b><%= SFServerUtil.formatCurrency(bmoOrderProperty.getAmount().toDouble())%> (<%= amountByWord.getMoneyAmountByWord(bmoOrderProperty.getAmount().toDouble()).toUpperCase() %>)</b> 
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
				   
                <% } 
	                  countPosi++;
        	}
        } //Fin pmConnAdicionalesPositivos
    	if (countPosi > 0) {
	        if(totalPrecioPactadoPosi >= 0){
 
		        
	 
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
				
					
	            <% } %>
	            
	<%      countNegat++;
	    	}
	    } //Fin pmConnAdicionalesNegativos
	    if (countNegat > 0) {
		    if(totalPrecioPactadoNegat <= 0){
	    %>
			  
     <% 
		    } 
	    }	%>
	<% pmConnAdicionalesNegativos.close();%>   
	<br>
</p>
<%
	double totalPrecioPaquetes = 0;
	
	totalPrecioPaquetes = totalPrecioPactadoPosi + totalPrecioPactadoNegat;
	
	double precioReal = 0;
	precioReal = bmoOrderProperty.getAmount().toDouble() + totalPrecioPaquetes;
%>
<p align="justify">
					Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.

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



<p align="justify" >
	Los Anexos <b>1 (Especificaciones)...
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
			ANEXO 1 :<br>ESPECIFICACIONES
		</td>
		<td class="reportTitleCell" style="text-align:right;font-size: 9px">
			
		</td>
	</tr>
</table>

<p align="justify" >
	<b>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
</b>
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

<p style="page-break-after: always">&nbsp;</p>

<% 	} catch (Exception e) { 
	String errorLabel = "Error de Contrato";
	String errorText = "El Contrato no pudo ser desplegado exitosamente. Es necesario completar todos los datos faltantes: Venta de Inmuebles.";
	String errorException = e.toString();
	
	response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);
	}

%>
</body>
</html>



