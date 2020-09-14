<%@page import="com.flexwm.server.op.PmRequisition"%>
<%@page import="com.flexwm.shared.op.BmoRequisitionType"%>
<%@page import="com.flexwm.shared.op.BmoRequisition"%>
<%@page import="com.flexwm.server.FlexUtil"%>
<%@page import="com.flexwm.shared.op.BmoEquipment"%>
<%@page import="com.flexwm.server.op.PmEquipmentType"%>
<%@page import="com.flexwm.shared.op.BmoEquipmentType"%>
<%@page import="com.flexwm.server.op.PmProjectEquipment"%>
<%@page import="com.flexwm.shared.op.BmoProjectEquipment"%>
<%@page import="com.flexwm.server.cm.PmProjectStaff"%>
<%@page import="com.flexwm.shared.cm.BmoProjectStaff"%>
<%@page import="com.flexwm.server.cm.PmProjectDetail"%>
<%@page import="com.flexwm.shared.cm.BmoProjectDetail"%>
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
		
		PmProjectDetail pmProjectDetail = new PmProjectDetail(sFParams);		
		BmoProjectDetail bmoProjectDetail = (BmoProjectDetail)pmProjectDetail.getBy(bmoProject.getId(), new BmoProjectDetail().getProjectId().getName());
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
					<tr>
						<th align="left"  class="reportCellEven"> Carga de equipo</th>
						<td class="reportCellEven">
							<% if (!bmoProjectDetail.getEquipmentLoadDate().toString().equals("")){ %>
								<%= FlexUtil.dateToLongDate(sFParams, bmoProjectDetail.getEquipmentLoadDate().toString()) %>
							<% } %>
						</td>
					</tr>	
					<tr>
						<th align="left"  class="reportCellEven"> Salida de bodega</th>
						<td class="reportCellEven">
							<% if (!bmoProjectDetail.getExitDate().toString().equals("")){ %>
								<%= FlexUtil.dateToLongDate (sFParams,bmoProjectDetail.getExitDate().toString())%>
							<% } %>
						</td>
					</tr>
					<tr>
						<th align="left"  class="reportCellEven"> Montaje</th>
						<td class="reportCellEven">
							<% if (!bmoProjectDetail.getLoadStartDate().toString().equals("")){ %>
								<%= FlexUtil.dateToLongDate(sFParams, bmoProjectDetail.getLoadStartDate().toString())%>
							<% } %>
						</td>
					</tr>
					<tr>
						<th align="left"  class="reportCellEven"> Pruebas</th>
						<td class="reportCellEven">
							<% if (!bmoProjectDetail.getTestDate().toString().equals("")){ %>
								<%= FlexUtil.dateToLongDate(sFParams, bmoProjectDetail.getTestDate().toString())%>
							<% } %>
						</td>
					</tr>
					<tr>
						<th align="left"  class="reportCellEven"> Entrega</th>
						<td class="reportCellEven">
							<% if (!bmoProjectDetail.getDeliveryDate().toString().equals("")){ %>
								<%= FlexUtil.dateToLongDate(sFParams, bmoProjectDetail.getDeliveryDate().toString())%>
							<% } %>
						</td>
					</tr>					
					<tr>
						<th align="left"  class="reportCellEven"> Evento </th>
						<td class="reportCellEven">
							<% if (!bmoProjectDetail.getEventStartDate().toString().equals("") & bmoProjectDetail.getEventEndDate().toString().equals("")){ %>
								<%= FlexUtil.parseRangeDateTimeToString(sFParams,bmoProjectDetail.getEventStartDate().toString()
									,bmoProjectDetail.getEventEndDate().toString()) %>
							<% } %>
						</td>
					</tr>
					<tr>
						<th align="left"  class="reportCellEven"> Desmontaje</th>
						<td class="reportCellEven">
							<% if (!bmoProjectDetail.getUnloadStartDate().toString().equals("")){ %>
								<%= FlexUtil.dateToLongDate(sFParams, bmoProjectDetail.getUnloadStartDate().toString())%>
							<% } %>
						</td>
					</tr>
					<tr>
						<th align="left"  class="reportCellEven"> Regreso</th>
						<td class="reportCellEven">
							<% if (!bmoProjectDetail.getReturnDate().toString().equals("")){ %>
								<%= FlexUtil.dateToLongDate(sFParams, bmoProjectDetail.getReturnDate().toString())%>
							<% } %>
						</td>
					</tr>
				</table>
			</td>
			<td > &nbsp;</td>
			<td width="50%" valign="top">
				<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
					<%
						PmEquipmentType pmEquipmentType = new PmEquipmentType(sFParams);
						
						Iterator<BmObject> equipmentTypeIterator = pmEquipmentType.list().iterator();
						PmConn pmConn2 = new PmConn(sFParams);
						pmConn2.open();
						
						while (equipmentTypeIterator.hasNext()){
							BmoEquipmentType nextBmoEquipmentType = (BmoEquipmentType)equipmentTypeIterator.next();
							 String sql = "SELECT peqi_projectequipmentid FROM projectequipment LEFT JOIN equipments ON (peqi_equipmentid = equi_equipmentid) "
									+ " LEFT JOIN equipmenttypes ON (equi_equipmenttypeid = eqty_equipmenttypeid) "
									+ " WHERE peqi_projectid = " + bmoProject.getId() + " AND eqty_equipmenttypeid = " + nextBmoEquipmentType.getId();
							 
							 pmConn2.doFetch(sql);
					%>
							<tr>
								<td class="reportHeaderCellCenter" width="50%"  colspan="4"><%=nextBmoEquipmentType.getName().toHtml() %></td>
							</tr>
					<%
					
							while (pmConn2.next()){
								PmProjectEquipment pmProjectEquipment = new PmProjectEquipment(sFParams);
								BmoProjectEquipment nextBmoProjectEquipment = (BmoProjectEquipment)pmProjectEquipment.get(pmConn2.getInt("peqi_projectequipmentid"));
								BmoEquipment nexbmoEquipment = (BmoEquipment)new PmEquipment(sFParams).get(nextBmoProjectEquipment.getEquipmentId().toInteger());
					%>
								<tr>
									<th align="left" class="reportCellEven">Nombre:</th>
									<td align="left" class="reportCellEven"><%=nexbmoEquipment.getName().toHtml()%></td>
								</tr>
								<tr>
									<th align="left" class="reportCellEven">Descripci&oacute;n:</th>
									<td align="left" class="reportCellEven">
										<%= nexbmoEquipment.getDescription().toHtml() %>									
									</td>
								</tr>
					<%
							}
							
						}
						pmConn2.close();
					
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
		
			BmoProjectStaff bmoProjectStaff = new BmoProjectStaff();
			PmProjectStaff pmProjectStaff = new PmProjectStaff(sFParams);
			BmFilter filterStaff = new BmFilter();
			filterStaff.setValueFilter(bmoProjectStaff.getKind(), bmoProjectStaff.getProjectId(), bmoProject.getId());
			
			Iterator<BmObject> staffIterator = pmProjectStaff.list(filterStaff).iterator();
			int i=0;
			while (staffIterator.hasNext()){
				i ++;
				BmoProjectStaff nextBmoProjectStaff = (BmoProjectStaff)staffIterator.next();
		
		%>
				<tr>
					<td class="reportCellEven" align="center" width="5px" ><%=i%></td>
					<td class="reportCellEven" align="left" width="25%" ><%=nextBmoProjectStaff.getBmoUser().getFirstname().toHtml() + " " 
						+ nextBmoProjectStaff.getBmoUser().getMotherlastname().toHtml() + " " + nextBmoProjectStaff.getBmoUser().getFatherlastname().toHtml() %></td>
					<td class="reportCellEven" align="left" width="25%" ><%=nextBmoProjectStaff.getBmoProfile().getName().toHtml()%></td>
					<td class="reportCellEven" align="left" width="25%" ><%=nextBmoProjectStaff.getBmoUser().getBmoArea().getName().toHtml()%></td>
					<td class="reportCellEven" align="left" width="25%" ><%=nextBmoProjectStaff.getNotes().toHtml()%></td>
					
				</tr>
		
		<%
			}
		%>
	</table>
	<br>
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		<tr>
			<td class="reportHeaderCellCenter" colspan="5">Proveedores Externos</td>
		</tr>
		<tr>
			<td class="reportHeaderCell " align="left"  >Nombre</td>
			<td class="reportHeaderCell " align="right"  >Lugar Entrega</td>
			<td class="reportHeaderCell " align="right"  >Hora de entrega</td>
			<td class="reportHeaderCell " align="right"  >Encargado</td>	
			<td class="reportHeaderCell " align="right"  >Observaciones</td>	
		</tr>
		<%
			BmFilter requisitionFilter = new BmFilter();
			BmFilter requisitionTypeFilter = new BmFilter();
			BmFilter orderFilter = new BmFilter();
			ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
			BmoRequisition bmoRequisition = new BmoRequisition();
			PmRequisition pmRequisition = new PmRequisition(sFParams);
			
			requisitionTypeFilter.setValueFilter(bmoRequisition.getBmoRequisitionType().getKind(), bmoRequisition.getBmoRequisitionType().getOutFormat(), 1);
			filterList.add(requisitionTypeFilter);
			
			requisitionFilter.setValueFilter(bmoRequisition.getKind(), bmoRequisition.getShowOnOutFormat(), 1);
			filterList.add(requisitionFilter);
			
			orderFilter.setValueFilter(bmoRequisition.getKind(), bmoRequisition.getOrderId(), bmoProject.getOrderId().toInteger());
			filterList.add(orderFilter);
			
			Iterator<BmObject> requiIterator = pmRequisition.list(filterList).iterator();
		
			while (requiIterator.hasNext()){
				BmoRequisition nextBmoRequisition = (BmoRequisition)requiIterator.next();
				BmoUser responsibleUser = (BmoUser)new PmUser(sFParams).get(nextBmoRequisition.getResponsibleId().toInteger());
		%>
				<tr>
					<td class="reportCellEven " align="left"  ><%= nextBmoRequisition.getBmoSupplier().getName().toHtml() %></td>
					<td class="reportCellEven " ><%= bmoProject.getBmoVenue().getName().toHtml() %></td>
					<td class="reportCellEven " ><%= nextBmoRequisition.getDeliveryTime().toHtml() %></td>
					<td class="reportCellEven " >
						<%= responsibleUser.getFirstname().toHtml() + " " + responsibleUser.getFatherlastname().toHtml() + " " + responsibleUser.getMotherlastname().toHtml() %>					
					</td>	
					<td class="reportCellEven " ><%= nextBmoRequisition.getObservations().toHtml() %></td>
				</tr>
		<%
			}
		%>
	</table>
</body>
</html>

<% 
	pmConn.close();
}catch (Exception e){
	System.out.println("Error al Desplegar Formato : " + e.toString());
}
%>