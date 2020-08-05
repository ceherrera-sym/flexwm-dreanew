<%@page import="com.flexwm.server.op.PmOrderStaff"%>
<%@page import="com.flexwm.shared.op.BmoOrderStaff"%>
<%@page import="com.flexwm.server.cm.PmOpportunity"%>
<%@page import="com.flexwm.shared.cm.BmoOpportunity"%>
<%@page import="com.flexwm.server.op.PmOrderEquipment"%>
<%@page import="com.flexwm.server.op.PmEquipment"%>
<%@page import="com.flexwm.shared.op.BmoOrderEquipment"%>
<%@page import="com.flexwm.shared.op.BmoProduct"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.server.op.PmOrderItem"%>
<%@page import="com.flexwm.shared.op.BmoOrderItem"%>
<%@page import="com.flexwm.server.cm.PmProject"%>
<%@page import="com.flexwm.shared.cm.BmoProject"%>
<%@include file="../inc/login_opt.jsp"%>

<%
try {
	String title = "Formato de Salida";
	BmoProject bmoProject = new BmoProject();
	PmProject pmProject = new PmProject(sFParams);
	
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram = new PmProgram(sFParams);
	bmoProgram = (BmoProgram) sFParams.getBmoProgram(bmoProject.getProgramCode());
		
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

		//Si se visualiza y no se tienen permisos, deshabilita: copiar, pegar, seleccionar, menú(clic-derecho).
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
		bmoProject = (BmoProject)pmProject.get(Integer.parseInt(request.getParameter("foreignId")));
		//Empresa
		BmoCompany bmoCompany = new BmoCompany();
		PmCompany pmCompany = new PmCompany(sFParams);
		bmoCompany = (BmoCompany) pmCompany.get(bmoProject.getCompanyId().toInteger());
		
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
			<td class="reportHeaderCellCenter" colspan="5"><%=title %></td>
		</tr>
		<tr>
			<td width="50%" valign="top">
				<table border="0" cellspacing="0"  width="100%" cellpadding="0" style="font-size: 12px">
					<tr>
						<td class="reportHeaderCellCenter" width="50%" colspan="2">LOG&Iacute;STICA</td>
					</tr>	
					<tr>
						<th align="left" class="reportCellEven">Proyecto:</th>
						<td class="reportCellEven"><%=bmoProject.getCode().toHtml()%></td>
					</tr>	
					<tr>
						<th align="left" class="reportCellEven">Nombre Proyecto:</th>
						<td class="reportCellEven"><%=bmoProject.getName().toHtml()%></td>
					</tr>	
					<%
						String teamLeader = ""; 
						if (bmoProject.getWarehouseManagerId().toInteger() > 0){
							BmoUser bmoUser = new BmoUser();
							bmoUser = (BmoUser)new PmUser(sFParams).get(bmoProject.getWarehouseManagerId().toInteger());
							teamLeader = bmoUser.getFirstname().toHtml() + " "  + bmoUser.getFatherlastname().toHtml() +
									" "+ bmoUser.getMotherlastname().toHtml();
						}
					%>
					<tr>
						<th align="left" class="reportCellEven">Productor:</th>
						<td class="reportCellEven"><%=teamLeader%></td>
					</tr>	
					<tr>
						<th align="left" class="reportCellEven">Cliente:</th>
						<td class="reportCellEven"><%=bmoProject.getBmoCustomer().getCode()%> <%= bmoProject.getBmoCustomer().getDisplayName().toHtml() %></td>
					</tr>		
					<tr>
						<th align="left" class="reportCellEven">Lugar:</th>
						<td class="reportCellEven"><%=bmoProject.getBmoVenue().getName().toHtml()%></td>
					</tr>	
					<% 
						String direction = "";
						if (bmoProject.getBmoVenue().getHomeAddress().toBoolean()){
							if (bmoProject.getOpportunityId().toInteger() > 0){
								BmoOpportunity bmoOpportunity = new BmoOpportunity();
								bmoOpportunity = (BmoOpportunity)new PmOpportunity(sFParams).get(bmoProject.getOpportunityId().toInteger());
								direction = bmoOpportunity.getCustomField4().toHtml();
							}
						} else {
							direction =  bmoProject.getBmoVenue().getNeighborhood().toHtml() + ", " +  bmoProject.getBmoVenue().getStreet().toHtml() +
									" ,No. " +  bmoProject.getBmoVenue().getNumber().toHtml();
						}
					%>
					<tr>
						<th align="left" class="reportCellEven">Direcci&oacute;n:</th>
						<td class="reportCellEven"><%=direction%></td>
					</tr>		
				</table>
			</td>
			<td > &nbsp;</td>
			<td width="50%" valign="top">
				<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
					<tr>
						<td class="reportHeaderCellCenter" width="50%"  colspan="4">TRANSPORTE</td>
					</tr>
					
					<%
						BmoOrderEquipment bmoOrderEquipment = new BmoOrderEquipment();
						PmOrderEquipment pmOrderEquipment = new PmOrderEquipment(sFParams);
						BmFilter filterEquipment = new BmFilter();
						filterEquipment.setValueFilter(bmoOrderEquipment.getKind(), bmoOrderEquipment.getOrderId(), bmoProject.getOrderId().toInteger());
						
						Iterator<BmObject> equipmentIterator = pmOrderEquipment.list(filterEquipment).iterator();
						while (equipmentIterator.hasNext()){
							BmoOrderEquipment nextBmoOrderEquipment = (BmoOrderEquipment)equipmentIterator.next();
					%>
							<tr>
								<th align="left" class="reportCellEven">Chofer:</th>
								<td align="left" class="reportCellEven"><%=nextBmoOrderEquipment.getBmoEquipment().getBmoUser().getCode() %></td>
								<th align="left" class="reportCellEven">Veh&iacute;culo:</th>
								<td align="left" class="reportCellEven"><%=nextBmoOrderEquipment.getBmoEquipment().getCode() %></td>
							</tr>	
							
					<%
						}
					%>
							
				</table>
			</td>
		</tr>
	</table>
	<br>
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		<tr>
			<td class="reportHeaderCellCenter" colspan="5">Personal</td>
		</tr>
		<tr>
			<td class="reportHeaderCell" align="left"  width="5px">#</td>
			<td class="reportHeaderCell " align="left"  >Nombre</td>
			<td class="reportHeaderCell " align="right"  >Puesto</td>
			<td class="reportHeaderCell " align="right"  >Area</td>
			<td class="reportHeaderCell " align="right"  >Notas</td>	
		</tr>
		<%
		
			BmoOrderStaff bmoOrderStaff = new BmoOrderStaff();
			PmOrderStaff pmOrderStaff = new PmOrderStaff(sFParams);
			BmFilter filterStaff = new BmFilter();
			filterStaff.setValueFilter(bmoOrderStaff.getKind(), bmoOrderStaff.getOrderId(), bmoProject.getOrderId().toInteger());
			
			Iterator<BmObject> staffIterator = pmOrderStaff.list(filterStaff).iterator();
			int i=0;
			while (staffIterator.hasNext()){
				i ++;
				BmoOrderStaff nextBmoOrderStaff = (BmoOrderStaff)staffIterator.next();
		
		%>
				<tr>
					<td class="reportCellEven" align="center" width="5px" ><%=i%></td>
					<td class="reportCellEven" align="left" width="25%" ><%=nextBmoOrderStaff.getName()%></td>
					<td class="reportCellEven" align="left" width="25%" ><%=i%></td>
					<td class="reportCellEven" align="left" width="25%" ><%=nextBmoOrderStaff.getBmoProfile().getName().toHtml()%></td>
					<td class="reportCellEven" align="left" width="25%" ><%=i%></td>
					
				</tr>
		
		<%
			}
		%>
	</table>
	<br>
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		<tr>
			<td class="reportHeaderCellCenter" colspan="5">Porveedores Externos</td>
		</tr>
		<tr>
			<td class="reportHeaderCell " align="left"  >Nombre</td>
			<td class="reportHeaderCell " align="right"  >Lugar Entrega</td>
			<td class="reportHeaderCell " align="right"  >Hora de entrega</td>
			<td class="reportHeaderCell " align="right"  >Encargado</td>	
			<td class="reportHeaderCell " align="right"  >Observaciones</td>	
		</tr>
		<tr>
			<td class="reportCellEven " align="left"  >Gennie x 1 semana</td>
			<td class="reportCellEven " >EXPO</td>
			<td class="reportCellEven " >1 Pm</td>
			<td class="reportCellEven " >Luis</td>	
			<td class="reportCellEven " >500 x persona (pruebas) 4 dias x 1000</td>
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