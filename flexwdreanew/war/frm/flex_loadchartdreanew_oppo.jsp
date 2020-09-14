<%@page import="com.flexwm.server.cm.PmQuoteItem"%>
<%@page import="com.flexwm.shared.cm.BmoQuoteItem"%>
<%@page import="com.flexwm.server.cm.PmOpportunity"%>
<%@page import="com.flexwm.shared.cm.BmoOpportunity"%>
<%@page import="com.flexwm.shared.op.BmoProduct"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.server.op.PmOrderItem"%>
<%@page import="com.flexwm.shared.op.BmoOrderItem"%>
<%@page import="com.flexwm.server.cm.PmProject"%>
<%@page import="com.flexwm.shared.cm.BmoProject"%>
<%@include file="../inc/login_opt.jsp"%>

<%
	try {
		String title = "Cuadro de Carga";
		BmoOpportunity bmoOpportunity = new BmoOpportunity();
		PmOpportunity pmOpportunity = new PmOpportunity(sFParams);
		
		BmoProgram bmoProgram = new BmoProgram();
		PmProgram pmProgram = new PmProgram(sFParams);
		bmoProgram = (BmoProgram) sFParams.getBmoProgram(bmoOpportunity.getProgramCode());
			
		PmConn pmConn = new PmConn(sFParams);
		pmConn.open();
%>

<html>
<%
	// Imprimir
	String print = "0", permissionPrint = "";
	if ((String) request.getParameter("print") != null)
		print = (String) request.getParameter("print");

	// Exportar a Excel
	String exportExcel = "0";
	if ((String) request.getParameter("exportexcel") != null)
		exportExcel = (String) request.getParameter("exportexcel");
	if (exportExcel.equals("1") && sFParams.hasPrint(bmoProgram.getCode().toString())) {
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "inline; filename=\"" + title + ".xls\"");
	}
		//Si se visualiza y no se tienen permisos, deshabilita: copiar, pegar, seleccionar, menÃº(clic-derecho).
		//En caso de que mande a imprimir, deshabilita contenido
	if (!(sFParams.hasPrint(bmoProgram.getCode().toString()))) {
%>
<style>
body {
	user-select: none;
	-moz-user-select: none;
	-o-user-select: none;
	-webkit-user-select: none;
	-ie-user-select: none;
	-khtml-user-select: none;
	-ms-user-select: none;
	-webkit-touch-callout: none
}
</style>
<style type="text/css" media="print">
* {
	display: none;
}
</style>
<%
	permissionPrint = "oncopy='return false' oncut='return false' onpaste='return false' oncontextmenu='return false' onkeydown='return false' onselectstart='return false' ondragstart='return false'";
			//Mensaje 
			if (print.equals("1") || exportExcel.equals("1")) {
%>
<script>
	alert('No tiene permisos para imprimir/exportar el documento, el documento saldr\u00E1 en blanco');
</script>
<%
	}
		}
%>
<head>
<title>:::<%=title %>:::</title>
	    <link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>">
<meta charset=utf-8>
</head>
<body class="default" <%=permissionPrint%>>
	<%
		NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(Locale.US);
		bmoOpportunity = (BmoOpportunity)pmOpportunity.get(Integer.parseInt(request.getParameter("foreignId")));
		//Empresa
		BmoCompany bmoCompany = new BmoCompany();
		PmCompany pmCompany = new PmCompany(sFParams);
		bmoCompany = (BmoCompany) pmCompany.get(bmoOpportunity.getCompanyId().toInteger());
		
		BmoCity bmoCompanyCity = new BmoCity();
		PmCity pmCity = new PmCity(sFParams);
		if (bmoCompany.getCityId().toInteger() > 0)
			bmoCompanyCity = (BmoCity) pmCity.get(bmoCompany.getCityId().toInteger());
		// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
		String logoURL = "";
		if (!bmoCompany.getLogo().toString().equals(""))
			logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
		else
			logoURL = sFParams.getMainImageUrl();
	%>
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		<tr>
			<td align="left" width="6%" rowspan="2" valign="top"><img
				border="0" width="<%=SFParams.LOGO_WIDTH%>"
				height="<%=SFParams.LOGO_HEIGHT%>" src="<%=logoURL%>"></td>
			<td class="contracSubTitle" align="center"><b><%=bmoCompany.getLegalName().toString().toUpperCase()%></b>
			</td>
		</tr>
		<tr>
			<td class="contractTitleCaption" align="center"><%=bmoCompany.getStreet().toHtml()%>
				<%=bmoCompany.getNumber().toHtml()%><br> <%=bmoCompany.getNeighborhood().toHtml()%>,
				C.P. <%=bmoCompany.getZip().toHtml()%><br> <%=bmoCompanyCity.getName().toHtml()%>,
				<%=bmoCompanyCity.getBmoState().getName().toHtml()%>,
				<%=bmoCompanyCity.getBmoState().getBmoCountry().getName().toHtml()%>.<br>
				TEL: <%=bmoCompany.getPhone().toHtml()%><br> <b><%=bmoCompany.getWww().toHtml()%></b></td>
		</tr>
	</table>
	<br>
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		<tr>
<!-- 			<td class="reportHeaderCell" colspan="4">Informaci&oacute;n T&eacute;cnica</td> -->
			<td class="reportHeaderCellCenter" colspan="4"><%=title %></td>
		</tr>
		<tr>
			<th align="left" class="reportCellEven">Proyecto:</th>
			<td class="reportCellEven"><%=bmoOpportunity.getCode() + " " + bmoOpportunity.getName()  %></td>
			<th alignpmConn="left" class="reportCellEven">Fecha/Hora:</th>
			<td class="reportCellEven">
				de:  <%=SFServerUtil.formatDate(sFParams, sFParams.getDateTimeFormat(), sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString(),bmoOpportunity.getStartDate().toString())%>
				 &nbsp;a:&nbsp;<%=SFServerUtil.formatDate(sFParams, sFParams.getDateTimeFormat(), sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString(), bmoOpportunity.getEndDate().toString())%>
			</td>
		</tr>
		
	</table>
	<br>
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		<tr >
			
			<td class="reportHeaderCell" colspan="6">Amperaje</td>
			
		</tr>
		<tr>
			<td class="reportHeaderCell" align="left" width="20%" >Descripcion</td>
			<td class="reportHeaderCellRight " align="right" width="16%" >Cantidad</td>
			<td class="reportHeaderCellRight " align="right" width="16%" >Consumo Amperes</td>
			<td class="reportHeaderCellRight " align="right" width="16%" >Total Consumo Amperes</td>	
					
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		
			<%
				double sumAmperage = 0;
				double sumM3 = 0;
				double sumWeigth = 0;
				String sql = "SELECT qogr_quotegroupid FROM quotegroups WHERE qogr_quoteid = "+ bmoOpportunity.getQuoteId().toInteger();
				pmConn.doFetch(sql);
				BmoQuoteItem bmoQuoteItem = new BmoQuoteItem();
				PmQuoteItem PmQuoteItem = new PmQuoteItem(sFParams);
// 				ArrayList<BmObject> productList = new ArrayList<BmObject>();
				 while (pmConn.next()){
					 
					 BmFilter filterByProduct = new BmFilter();
					 BmFilter filterByGroup = new BmFilter();
					 ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
					
					 filterByGroup.setValueFilter(bmoQuoteItem.getKind(), bmoQuoteItem.getQuoteGroupId(), pmConn.getInt("qogr_quotegroupid"));
					 //que sean con producto
					 filterByProduct.setValueOperatorFilter(bmoQuoteItem.getKind(),
							 bmoQuoteItem.getProductId(), BmFilter.MAYOR, 1);
					 
					 filterList.add(filterByProduct);
					 filterList.add(filterByGroup);
					 
					 Iterator<BmObject> itemIterator = PmQuoteItem.list(filterList).iterator();
					  while(itemIterator.hasNext()){
						  BmoQuoteItem nextBmoQuoteItem = (BmoQuoteItem)itemIterator.next();
						  double amparage = 0;
						  double m3 = 0;
						  double weigth = 0;
						  double caseQuantity = 0;
						  //Primero se toma el valor de Amperaje 220v si no existe se toma el de 110v
						  if(nextBmoQuoteItem.getBmoProduct().getAmperage220().toDouble() > 0){
							  amparage = nextBmoQuoteItem.getBmoProduct().getAmperage220().toDouble();
						  } else if (nextBmoQuoteItem.getBmoProduct().getAmperage110().toDouble() > 0){
							  amparage = nextBmoQuoteItem.getBmoProduct().getAmperage110().toDouble();
						  } 
						  //Si el producto usa case se calcula el peso y m3 segun el case
						  if (nextBmoQuoteItem.getBmoProduct().getUseCase().toBoolean()){
							  caseQuantity = nextBmoQuoteItem.getQuantity().toDouble()/ nextBmoQuoteItem.getBmoProduct().getQuantityForCase().toDouble();
							  if ( nextBmoQuoteItem.getQuantity().toDouble() % nextBmoQuoteItem.getBmoProduct().getQuantityForCase().toDouble() > 0) {
								  Math.ceil(caseQuantity);
							  }							  
							  m3 = nextBmoQuoteItem.getBmoProduct().getCaseCubicMeter().toDouble() * caseQuantity;
							  weigth = nextBmoQuoteItem.getBmoProduct().getWeightCase().toDouble() * caseQuantity;			
						  } else {
							  m3 = nextBmoQuoteItem.getBmoProduct().getCubicMeter().toDouble() * nextBmoQuoteItem.getQuantity().toDouble();
							  weigth = nextBmoQuoteItem.getBmoProduct().getWeight().toDouble() * nextBmoQuoteItem.getQuantity().toDouble();
						  }
			%>	
						<tr>
							<td class="reportCellEven" align="left" ><b><%=nextBmoQuoteItem.getBmoProduct().getCode().toHtml() %></b> <%= nextBmoQuoteItem.getBmoProduct().getName().toHtml() %> 
								[<%=nextBmoQuoteItem.getBmoProduct().getDescription	().toHtml() %>]
							</td>
							<td class="reportCellEven" align="right" ><%=nextBmoQuoteItem.getQuantity()%></td>
							<td class="reportCellEven" align="right" ><%=amparage %></td>
							<td class="reportCellEven" align="right" ><%=nextBmoQuoteItem.getQuantity().toDouble() * amparage %></td>	
						</tr>	
			<%			//Suma total de amperaje productos
						sumAmperage = sumAmperage + (nextBmoQuoteItem.getQuantity().toDouble() * amparage);
						//Suma total de M3 y peso de productos
						sumM3 = sumM3 + m3;
						sumWeigth = sumWeigth + weigth;
					  }
					 
				 }
			%>
		
		<tr>			
			<th class="" colspan="3" align="right">Consumo Total:</th>
			<td class="reportCellEven" align="right"><b><%=String.format("%.2f", sumAmperage) %></b></td>
		</tr>
		<tr>			
			<th class="" colspan="3" align="right">Consumo de Aperes por Fase:</th>
			<td class="reportCellEven" align="right"><b><%=String.format("%.2f", (sumAmperage/3))%></b></td>
			
		</tr>
	</table>
	<br>
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		<tr >			 
			<td class="reportHeaderCell" colspan="2">Peso Y Mts³</td>			 
 		</tr> 
 		<tr>
	 		<th class="reportCellEven" align="left" >
	 			Peso total
	 		</th>
	 		<td class="reportCellEven" align="right">
	 			<b><%=String.format("%.2f", sumWeigth) %></b>
	 		</td>
	 	</tr>
	 	<tr>
	 		<th class="reportCellEven" align="left" >
	 			M3 totales
	 		</th>
	 		<td class="reportCellEven" align="right">
	 			<b><%=String.format("%.2f", sumM3) %></b>
	 		</td>
 		</tr>
	</table>
</body>
</html>

<% 
	pmConn.close();
}catch (Exception e){
	System.out.println("Error al Desplegar Formato : " + e.toString());
}
%>