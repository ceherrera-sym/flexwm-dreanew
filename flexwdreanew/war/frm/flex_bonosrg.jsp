<%@page import="com.flexwm.server.cm.*"%>
<%@page import="com.flexwm.shared.cm.*"%>
<%@page import="com.flexwm.server.ev.*"%>
<%@page import="com.flexwm.shared.ev.*"%>
<%@page import="com.flexwm.server.op.*"%>
<%@page import="com.flexwm.shared.op.*"%>
<%@page import="com.flexwm.shared.co.*"%>
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
<%@page import="com.flexwm.server.FlexUtil"%>
<%@page import= "java.util.Calendar"%>
<%@page import="java.sql.Types"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.DecimalFormat "%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.flexwm.server.AmountByWord"%>
<%@page import="com.flexwm.server.AmountByWorden"%>
<%@page import="com.flexwm.server.AmountByWordes"%>
<%@page import="com.flexwm.server.IAmountByWord"%>
<%@page import="com.flexwm.server.NumberByWord"%>

<%@include file="../inc/login_opt.jsp" %>

<%
	String title = "Bonos RG";
	BmoPropertySale bmoPropertySale = new BmoPropertySale();
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)sFParams.getBmoProgram(bmoPropertySale.getProgramCode());
	
	AmountByWord amountByWord = new AmountByWord();
	amountByWord.setLanguage("es");
	amountByWord.setCurrency("es");
	
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
	<title>:::<%= title %>:::</title>
    <link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>"> 
     <link rel="stylesheet" type="text/css" href="../css/flexwm.css"> 
</head>
<body class="default" <%= permissionPrint %> style="font-size: 12px">

<%
	try {				
	    // Si es llamada externa, utilizar llave desencriptada
	    int propertySaleId = 0;
	    if (isExternal) propertySaleId = decryptId;
	    else propertySaleId = Integer.parseInt(request.getParameter("foreignId"));	 

	    double cantidad = 2000;
		NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(Locale.US);

	    //Venta
		PmPropertySale pmPropertySale = new PmPropertySale(sFParams);
		bmoPropertySale = (BmoPropertySale)pmPropertySale.get(propertySaleId);
	
		//Cliente
		BmoCustomer bmoCustomer = new BmoCustomer();
		PmCustomer pmCustomer = new PmCustomer(sFParams);
		bmoCustomer = (BmoCustomer)pmCustomer.get(bmoPropertySale.getCustomerId().toInteger());
		
		//Cliente recomendado
		BmoCustomer bmoCustomerRG = new BmoCustomer();
	    PmCustomer pmCustomerRG = new PmCustomer(sFParams);
	    if (bmoCustomer.getRecommendedBy().toInteger() > 0)
	    	bmoCustomerRG = (BmoCustomer)pmCustomerRG.get(bmoCustomer.getRecommendedBy().toInteger());
		
		//Obtener el Desarrollo
		BmoDevelopment bmoDevelopment = new BmoDevelopment();
		PmDevelopment pmDevelopment = new PmDevelopment(sFParams);
		bmoDevelopment = (BmoDevelopment)pmDevelopment.get(bmoPropertySale.getBmoProperty().getBmoDevelopmentBlock().getBmoDevelopmentPhase().getDevelopmentId().toInteger());
		
		//Modelo
		BmoPropertyModel bmoPropertyModel = new BmoPropertyModel();
		PmPropertyModel pmPropertyModel = new PmPropertyModel(sFParams);
		bmoPropertyModel = (BmoPropertyModel)pmPropertyModel.get(bmoPropertySale.getBmoProperty().getPropertyModelId().toInteger());
		
		//Empresa
		BmoCompany bmoCompany = new BmoCompany();
		PmCompany  pmCompany  = new PmCompany (sFParams);
		bmoCompany = (BmoCompany)pmCompany.get(bmoDevelopment.getCompanyId().toInteger());
		
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

		if (bmoCustomer.getRecommendedBy().toInteger() > 0) {
%>
			<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
			      <tr>
			            <td align="left" width="" rowspan="5" valign="top">
							<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
			            </td>
			            <td colspan="3" class="reportTitleCell">
			                Bonos RG
			            </td>
			            <td  class="reportTitleCell" style="text-align:right; font-size: 12px">
			            	C&oacute;digo del Documento:<br>FO-07.2.2-4(30-mar-04)
			            </td>
			      </tr>     
			      <tr>
			            <th align = "left" class="reportCellEven">Otorgado a:</th>
			            <td class="reportCellEven">
			                 <%= bmoCustomerRG.getDisplayName().toString() %> (<%= bmoCustomerRG.getCode().toString() %>)
			            </td>
			            <th align = "left" class="reportCellEven">Lugar y Fecha:</th>
			            <td class="reportCellEven">
				            <%= bmoCityCompany.getName().toString() %>, 
				        	<%= bmoCityCompany.getBmoState().getName().toString()%>,
				        	a <%= FlexUtil.dateToLongDate(sFParams, SFServerUtil.nowToString(sFParams, "yyyy-MM-dd"))%>
			            </td>
			      </tr>
			      <tr>
			            <th align = "left" class="reportCellEven">La cantidad de:</th>
			            <td class="reportCellEven">
			                <%= formatCurrency.format(bmoPropertyModel.getBonusRG().toDouble()) %>
			            </td>
			            <th align = "left" class="reportCellEven">Cantidad con letra:</th>
			            <td class="reportCellEven">
		            		<%= amountByWord.getMoneyAmountByWord(bmoPropertyModel.getBonusRG().toDouble()).toUpperCase() %>  
			            </td>
			      </tr>
			      <tr>
			            <th align = "left" class="reportCellEven">Por concepto de:</th>
			            <td class="reportCellEven" colspan="3">
			            	Programa recomienda y gana: un cliente que usted nos recomend&oacute; ha firmado su contrato de compra venta de una casa MDM. 
			            	Usted nos recomendo a <b><%= bmoCustomer.getDisplayName().toString() %> (<%= bmoCustomer.getCode().toString() %>)</b>, en el momento que este cliente firme las escrituras de su nueva 
			            	casa MDM y Corporaci&oacute;n MDM reciba el pago total de la misma, usted se har&aacute; acreedor y podr&aacute; cobrar este bono especial.
			            </td>
			      </tr>
			      <tr>
			          <th align = "left" class="reportCellEven">Instrucciones:</th>
			          <td class="reportCellEven" colspan="3">
			          S&iacute;rvase pasar con el presente bono a nuestras oficinas de cr&eacute;dito y cobranza, presentando su identificaci&oacute;n oficial con fotograf&iacute;a, 
			          para que se le entregue su premio en efectivo. El premio en efectivo solo se entregar&aacute; al beneficiario del mismo, en persona y 
			          acredit&aacute;ndose con su identificaci&oacute;n. IMPORTANTE: El cliente <b><%= bmoCustomer.getDisplayName().toString() %> (<%= bmoCustomer.getCode().toString() %>)</b> deber&aacute; de haber firmado las escrituras 
			          de su nueva casa MDM para poder reclamar este bono.                
			          </td>
			    </tr>
			      
			</table>
			<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0" style="font-size: 12px">
			    <tr align="center">
			        <td align="center" colspan="2"><br><br>
			            ___________________
			        </td>
			    </tr>
			    <tr>
				    <td align="center"  colspan="2">
						<br>
					    Bono autorizado por:<br>
						<b>
						    <%= sFParams.getLoginInfo().getBmoUser().getFatherlastname().toString() %>
				        	<%= sFParams.getLoginInfo().getBmoUser().getMotherlastname().toString() %> 
				        	<%= sFParams.getLoginInfo().getBmoUser().getFirstname().toString() %>
			        	</b>
			        	<br><br>
		        	</td>
			    </tr>
			    <tr>
					<td align="right"><b>ORIGINAL CLIENTE</b></td>
				</tr>
			</table>
			<br><br>
			<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		    	<tr>
			          <td align="left" width="" rowspan="5" valign="top">
						<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
			          </td>
			          <td colspan="3" class="reportTitleCell">
			              Bonos RG
			          </td>
			          <td  class="reportTitleCell" style="text-align:right; font-size: 12px">
			          	C&oacute;digo del Documento:<br>FO-07.2.2-4(30-mar-04)
			          </td>
			    </tr>     
			    <tr>
			          <th align = "left" class="reportCellEven">Otorgado a:</th>
			          <td class="reportCellEven">
			               <%= bmoCustomerRG.getDisplayName().toString() %> (<%= bmoCustomerRG.getCode().toString() %>)
			          </td>
			          <th align = "left" class="reportCellEven">Lugar y Fecha:</th>
			          <td class="reportCellEven">
				            <%= bmoCityCompany.getName().toString() %>, 
				        	<%= bmoCityCompany.getBmoState().getName().toString()%>,
				        	a <%= FlexUtil.dateToLongDate(sFParams, SFServerUtil.nowToString(sFParams, "yyyy-MM-dd"))%>
			          </td>
			    </tr>
			    <tr>
			          <th align = "left" class="reportCellEven">La cantidad de:</th>
			          <td class="reportCellEven">
			              	<%= formatCurrency.format(bmoPropertyModel.getBonusRG().toDouble()) %>
			          </td>
			          <th align = "left" class="reportCellEven">Cantidad con letra:</th>
			          <td class="reportCellEven">
			          		<%= amountByWord.getMoneyAmountByWord(bmoPropertyModel.getBonusRG().toDouble()).toUpperCase() %>          
			          </td>
			    </tr>
			    <tr>
			          <th align = "left" class="reportCellEven">Por concepto de:</th>
			          <td class="reportCellEven" colspan="3">
				          Firma de escrituras de nueva vivienda MDM por cliente recomendado para el  programa recomienda y gana. Usted nos recomendo a <b><%= bmoCustomer.getDisplayName().toString()%></b> 
				          <b>(<%=  bmoCustomer.getCode().toString() %>)</b>, en el momento que este cliente firme las escrituras de su nueva casa MDM y Corporaci&oacute;n MDM reciba el pago total de la misma,			
				          usted se har&aacute; acreedor y podr&aacute; cobrar este bono especial.
			          </td>
			    </tr>
			    <tr>
			        <th align = "left" class="reportCellEven">Instrucciones:</th>
			        <td class="reportCellEven" colspan="3">
				        S&iacute;rvase pasar con el presente bono a nuestras oficinas de cr&eacute;dito y cobranza, presentando su identificaci&oacute;n oficial con fotograf&iacute;a, 
				        para que se le entregue su premio en efectivo. El premio en efectivo solo se entregar&aacute; al beneficiario del mismo, en persona y acredit&aacute;ndose con su identificaci&oacute;n. 
				        IMPORTANTE: El cliente <b>
				        <%= bmoCustomer.getDisplayName().toString()%>
			        </b> 
			         <b>(<%= bmoCustomer.getCode().toString()%>)</b> deber&aacute; de haber firmado las escrituras de su nueva casa mdm para poder reclamar este bono.             
			        </td>
			  </tr>
			    
			</table>
			<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0"  style="font-size: 12px">
				<tr align="center">
					<td align="center" colspan="2"><br><br>
						___________________
					</td>
				</tr>
				<tr>
					<td align="center" colspan="2">
						<br>
						Bono autorizado por:<br>
						<b>
						<%= sFParams.getLoginInfo().getBmoUser().getFatherlastname().toString() %>
						<%= sFParams.getLoginInfo().getBmoUser().getMotherlastname().toString() %> 
						<%= sFParams.getLoginInfo().getBmoUser().getFirstname().toString() %>
						</b>
						<br><br>
					</td>
				</tr>
				<tr>
					<td align="right"><b>COPIA EMPRESA</b></td>
				</tr>
			</table>
<%	
		} else { %>
			<script>
				alert("El Cliente no fue recomendado");
			</script>
		<% }
	} catch (Exception e) { 
		String errorLabel = "Error de Formato";
		String errorText = "El Formato Bonos RG no pudo ser desplegado exitosamente.";
		String errorException = e.toString();
		
		response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);
	}

%>
</body>
</html>



