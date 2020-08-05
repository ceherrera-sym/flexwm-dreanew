<%@page import="com.flexwm.server.cm.*"%>
<%@page import="com.flexwm.shared.cm.*"%>
<%@page import="com.flexwm.server.co.*"%>
<%@page import="com.flexwm.shared.co.*"%>
<%@page import="com.flexwm.server.fi.*"%>
<%@page import="com.flexwm.shared.fi.*"%>
<%@page import="com.symgae.shared.sf.BmoCompany"%>
<%@page import="com.symgae.server.sf.PmCompany"%>
<%@page import="com.symgae.server.PmConn"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.server.SFServerUtil"%>
<%@page import="com.flexwm.server.FlexUtil"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import= "java.util.Calendar"%>
<%@page import="java.sql.Types"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.DecimalFormat "%>
<%@page import="com.flexwm.server.AmountByWord"%>
<%@page import="com.flexwm.server.AmountByWorden"%>
<%@page import="com.flexwm.server.AmountByWordes"%>
<%@page import="com.flexwm.server.IAmountByWord"%>
<%@page import="com.flexwm.server.NumberByWord"%>

<%@include file="../inc/login_opt.jsp" %>
<%
	String title = "Pagar&eacute;s en base a CxC";

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
		String sqlCustAddress = " SELECT * FROM customeraddress WHERE cuad_customerid = " + bmoPropertySale.getBmoCustomer().getId() + " ORDER BY cuad_type ASC";
		pmConnCustomer.doFetch(sqlCustAddress);
		if(pmConnCustomer.next()) cuad = true;
		if(cuad)
			bmoCustomerAddress = (BmoCustomerAddress)pmCustomerAddress.getBy(bmoPropertySale.getBmoCustomer().getId(), bmoCustomerAddress.getCustomerId().getName());
		
		pmConnCustomer.close();
		
		//Telefonos del Cliente
		String casa = "", movil = "", trabajo = "", otro = "";
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
				movil = pmConnCustomerPhones.getString("cuph_number");
			if(pmConnCustomerPhones.getString("cuph_type").equals(""+BmoCustomerPhone.TYPE_WORK))
				trabajo = pmConnCustomerPhones.getString("cuph_number");
			if(pmConnCustomerPhones.getString("cuph_type").equals(""+BmoCustomerPhone.TYPE_OTHER))
				otro = pmConnCustomerPhones.getString("cuph_number");
		}
		pmConnCustomerPhones.close();
			
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

			PmConn pmConnRacc = new PmConn(sFParams);
			pmConnRacc.open();

			int count = 0;
			String sqlRacc = "SELECT count(racc_raccountid) as countRacc FROM raccounts "
					+ " LEFT JOIN raccounttypes ON (ract_raccounttypeid = racc_raccounttypeid) "
					+ " WHERE racc_orderid = " + bmoPropertySale.getOrderId().toInteger() + " AND ract_type = '"
					+ BmoRaccountType.TYPE_WITHDRAW + "'" + " ORDER BY racc_raccountid ASC";
			pmConnRacc.doFetch(sqlRacc);
			if (pmConnRacc.next())
				count = pmConnRacc.getInt("countRacc");

			sqlRacc = "SELECT * FROM raccounts "
					+ " LEFT JOIN raccounttypes ON (ract_raccounttypeid = racc_raccounttypeid) "
					+ " WHERE racc_orderid = " + bmoPropertySale.getOrderId().toInteger() + " AND ract_type = '"
					+ BmoRaccountType.TYPE_WITHDRAW + "'" + " ORDER BY racc_raccountid ASC";

			pmConnRacc.doFetch(sqlRacc);

			int p = 0;
			int consecutive = 0;
			while (pmConnRacc.next()) {
				p++;
	%>
		<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		    <tr>
			    <td align="left" width="" rowspan="6" valign="top">
			    	<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
			    </td>
			    <td colspan="3" class="reportTitleCell">
			    	PAGAR&Eacute; No. <%= p %>
			    </td>
	            <td  class="reportTitleCell" style="text-align:right; font-size: 11px">
			    	C&oacute;digo del Documento: FO-07.2.2-6(30-Mar-04)
			    </td>
		    </tr>     
		    <tr>
			    <th align = "left" class="reportCellEven">Bueno por:</th>
			    <td class="reportCellEven">
			    	<%= SFServerUtil.formatCurrency(pmConnRacc.getDouble("racc_total")) %>
			    </td>
			    <th align = "left" class="reportCellEven">Lugar y Fecha:</th>
			    <td class="reportCellEven">
			    	En <%= bmoCityCompany.getName().toString().toUpperCase() %>, 
			    	<%= bmoCityCompany.getBmoState().getName().toString().toUpperCase() %> a
			    	<%= FlexUtil.dateToLongDate(sFParams, SFServerUtil.nowToString(sFParams, "yyyy-MM-dd"))%>
			    </td>
		    </tr>
		    <tr>
			    <td class="reportCellEven" colspan="2" >
			    	<b>Debe(mos) y pagare(mos) incondicionalmente por este Pagar&eacute; a la orden de:</b>
		    	</td>
		    	<td class="reportCellEven" colspan="2" >
		            <%= bmoCompany.getLegalName().toString().toUpperCase() %>
		            en la ciudad de <%= bmoCityCompany.getName().toString().toUpperCase() %>,
		            <%= bmoCityCompany.getBmoState().getName().toString().toUpperCase() %>
		            el d&iacute;a
			    	<%= FlexUtil.dateToLongDate(sFParams, pmConnRacc.getString("racc_duedate"))%>
			    </td>
		    </tr>
		    <tr>
			    <td class="reportCellEven" colspan="2">
				    <b>La cantidad de:</b>
			    </td>
	            <td class="reportCellEven" colspan="2" >
	            	<%= amountByWord.getMoneyAmountByWord(pmConnRacc.getDouble("racc_total")).toUpperCase() %>
		    	</td>
		    </tr>
		    <tr>
			    <td class="reportCellEven" colspan="4">
				    Valor recibido a mi (nuestra) entera satisfacci&oacute;n. Este pagar&eacute; forma parte de una serie numerada 
		            del <b>1</b> al <b><%= count %></b> y todos est&aacute;n sujetos a la condici&oacute;n de que, al no pagarse cualquiera de ellos
		            a su vencimiento, ser&aacute;n exigibles todos los que le sigan en n&uacute;mero, adem&aacute;s de los ya vencidos,
		            desde la fecha de vencimiento de este documento hasta el d&iacute;a de su liquidaci&oacute;n, causar&aacute; intereses
		            moratorios al tipo de 5% mensual, pagadero en esta ciudad juntamente con el principal.
			    </td>
		    </tr>
		</table>
		<br>
		<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
			<tr>
				<td colspan="9" class="reportHeaderCell">
					Nombre y datos del deudor   
				</td>
			</tr>
			
		<%	if (bmoCustomer.getCustomertype().toString().equals(""+BmoCustomer.TYPE_PERSON)) { %>
			<tr>
				<th align = "left" class="reportCellEven">Nombre:</th>
				<td class="reportCellEven" colspan="3">
			            <%= bmoCustomer.getDisplayName().toString().toUpperCase() %>
						(<%= bmoCustomer.getCode().toString() %>)
				</td>
			</tr> 
        <% } else if (bmoCustomer.getCustomertype().toString().equals(""+BmoCustomer.TYPE_COMPANY)) { %>
	        <tr>
				<th align = "left" class="reportCellEven">Nombre:</th>
				<td class="reportCellEven" >
					<%= bmoCustomer.getLegalname().toString().toUpperCase() %>
				</td>
				<th align = "left" class="reportCellEven">Representado por:</th>
				<td class="reportCellEven" >
					<%= bmoCustomer.getFirstname().toString().toUpperCase() %>
                    <%= bmoCustomer.getFatherlastname().toString().toUpperCase() %>
                    <%= bmoCustomer.getMotherlastname().toString().toUpperCase() %>
				</td>
			</tr>
        <% } %>
	        <tr>
		        <th align = "left" class="reportCellEven">RFC:</th>
		        <td class="reportCellEven">
            		<%= bmoCustomer.getRfc().toString().toUpperCase() %>
		        </td>
		        <th align = "left" class="reportCellEven">Direcci&oacute;n:</th>
		        <td class="reportCellEven">
			        <%= bmoCustomerAddress.getStreet().toString().toUpperCase() %>
	            	#<%= bmoCustomerAddress.getNumber().toString().toUpperCase() %>
	            	<%= bmoCustomerAddress.getNeighborhood().toString().toUpperCase() %>              
		        </td>
	        </tr>
	        <tr>
		        <th align = "left" class="reportCellEven">Tel&eacute;fono:</th>
		        <td class="reportCellEven">
			        <% if (casa.equals("")) { %>
		        		<%= movil.toUpperCase() %>
			        <% } else { %> 
			    		<%= casa.toUpperCase() %>
			        <% } %>               
		        </td>
		        <th align = "left" class="reportCellEven">Lugar:</th>
		        <td class="reportCellEven">
			        <%= bmoCustomerAddress.getBmoCity().getName().toString().toUpperCase() %>, 
	                <%= bmoCustomerAddress.getBmoCity().getBmoState().getName().toString().toUpperCase() %>           
		        </td>
	        </tr>
		</table>
		<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0" style="font-size: 12px">
		  <tr align="center">
		      <td align="center" colspan="2">
		      	Acepto(amos) 
		      	<br><br>
		          ___________________
		      </td>
		  </tr>
		  <tr>
		    <td align="center"  colspan="2">
			    Firma(s):
		  	</td>
		  </tr>
		</table>
		<br>
		<%= ((p % 2 == 0) && (count > p)) ? "<span style=\'page-break-after: always\'></span>" : "" %>
	<% 
		
	    }// fin while
	    pmConnRacc.close();
	    
    } catch (Exception e) { 
	String errorLabel = "Error de Contrato";
	String errorText = "El Contrato no pudo ser desplegado exitosamente. Es necesario completar todos los datos faltantes: Venta de Inmuebles.";
	String errorException = e.toString();
	
	response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);
	}

	%>
</body>
</html>