<%@page import="com.flexwm.shared.co.BmoPropertyModelExtra"%>
<%@page import="com.flexwm.server.ac.PmOrderSessionExtra"%>
<%@page import="com.flexwm.shared.ac.BmoOrderSessionExtra"%>
<%@page import="com.flexwm.shared.fi.BmoCommission"%>
<%@page import="com.flexwm.shared.op.BmoProduct"%>
<%@page import="com.flexwm.server.ac.PmSessionSale"%>
<%@page import="com.flexwm.server.ac.PmOrderSessionTypePackage"%>
<%@page import="com.flexwm.server.ac.PmSessionTypePackage"%>
<%@page import="com.flexwm.shared.ac.BmoSessionTypePackage"%>
<%@page import="com.flexwm.server.ac.PmOrderSession"%>
<%@page import="com.flexwm.shared.ac.BmoOrderSession"%>
<%@page import="com.flexwm.server.ac.PmSession"%>
<%@page import="com.flexwm.shared.ac.BmoSession"%>
<%@page import="com.flexwm.shared.ac.BmoSessionSale"%>
<%@page import="com.flexwm.server.cr.PmOrderCredit"%>
<%@page import="com.flexwm.shared.cr.BmoOrderCredit"%>
<%@page import="com.flexwm.server.ev.PmVenue"%>
<%@page import="com.flexwm.shared.ev.BmoVenue"%>
<%@page import="com.flexwm.server.cm.PmCustomerAddress"%>
<%@page import="com.flexwm.shared.cm.BmoCustomerAddress"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.server.op.PmOrderGroup"%>
<%@page import="com.flexwm.shared.op.BmoOrderType"%>
<%@page import="com.flexwm.shared.op.BmoOrderGroup"%>
<%@page import="com.flexwm.shared.op.BmoOrderItem"%>
<%@page import="com.flexwm.shared.op.BmoOrderEquipment"%>
<%@page import="com.flexwm.shared.op.BmoOrderStaff"%>
<%@page import="com.flexwm.server.op.PmOrderItem"%>
<%@page import="com.flexwm.server.op.PmOrderEquipment"%>
<%@page import="com.flexwm.server.op.PmOrderStaff"%>
<%@page import="com.flexwm.server.op.PmOrder"%>
<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@page import="com.flexwm.server.cm.PmProject"%>
<%@page import="com.flexwm.shared.cm.BmoProject"%>
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="com.flexwm.shared.wf.BmoWFlowType"%>
<%@page import="com.flexwm.server.wf.PmWFlowType"%>
<%@page import="com.symgae.shared.BmFilter"%>
<%@page import="com.symgae.shared.sf.BmoProgram"%>
<%@page import="com.flexwm.shared.BmoFlexConfig"%>
<%@page import="com.symgae.shared.BmObject"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.symgae.shared.BmObject"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.flexwm.server.op.PmOrderType"%>
<%@page import="com.flexwm.shared.op.BmoOrderType"%>
<%@page import="com.flexwm.server.co.PmPropertySale"%>
<%@page import="com.flexwm.shared.co.BmoPropertySale"%>
<%@page import="com.flexwm.server.co.PmOrderProperty"%>
<%@page import="com.flexwm.shared.co.BmoOrderProperty"%>
<%@page import="com.flexwm.shared.co.BmoDevelopmentBlock"%>
<%@page import="com.flexwm.server.co.PmOrderPropertyModelExtra"%>
<%@page import="com.flexwm.shared.co.BmoOrderPropertyModelExtra"%>
<%@page import="com.flexwm.server.fi.PmRaccount"%>
<%@page import="com.flexwm.shared.fi.BmoRaccount"%>
<%@page import="com.symgae.server.sf.PmCompany"%>
<%@page import="com.symgae.shared.sf.BmoCompany"%>
<%@include file="../inc/login_opt.jsp"%>

<%
	String title = "Pedido";
	try {
		BmoOrder bmoOrder = new BmoOrder();
		BmoPropertySale bmoPropertySale = new BmoPropertySale();
		BmoWFlowType bmoPropertySaleWFlowType = new BmoWFlowType();
		BmoOrderProperty bmoOrderProperty = new BmoOrderProperty();
		BmoDevelopmentBlock bmoDevelopmentBlock = new BmoDevelopmentBlock();
		BmoOrderPropertyModelExtra bmoOrderPropertyModelExtra = new BmoOrderPropertyModelExtra();
		BmoRaccount bmoRaccount= new BmoRaccount();
		
		BmoProgram bmoProgram = new BmoProgram();
		PmProgram pmProgram  = new PmProgram(sFParams);
		bmoProgram = (BmoProgram)sFParams.getBmoProgram(bmoOrder.getProgramCode());	
		
		BmoOrderType bmoOrderType = new BmoOrderType();
		PmOrderType pmOrderType = new PmOrderType(sFParams);
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
			if(print.equals("1") || exportExcel.equals("1")) { %>
<script>
					alert('No tiene permisos para imprimir/exportar el documento, el documento saldr\u00E1 en blanco');
				</script>
<% }
		}
		 
		%>
<head>
<title>:::<%= title %>:::
</title>
<link rel="stylesheet" type="text/css"
	href="<%= sFParams.getAppURL()%>/css/<%= defaultCss %>">
</head>
<body class="default" <%= permissionPrint %>>

	<%
		NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(Locale.US);

	    // Obtener parametros
	    int foreignId = 0;
	    if (isExternal) foreignId = decryptId;
	    else foreignId = Integer.parseInt(request.getParameter("foreignId"));    
		
		PmOrder pmOrder = new PmOrder(sFParams);
		bmoOrder = (BmoOrder)pmOrder.get(foreignId);
		
		bmoOrderType = (BmoOrderType)pmOrderType.get(bmoOrder.getOrderTypeId().toInteger());	

		// Empresa
		BmoCompany bmoCompany = new BmoCompany();
		PmCompany pmCompany = new PmCompany(sFParams);
		bmoCompany = (BmoCompany)pmCompany.get(bmoOrder.getCompanyId().toInteger());
		
		// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
		String logoURL = "";
		if (!bmoCompany.getLogo().toString().equals(""))
			logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
		else 
			logoURL = sFParams.getMainImageUrl();
		
		if (bmoOrderType.getType().equals("" + BmoOrderType.TYPE_RENTAL)) {

			BmoWFlowType bmoProjectWFlowType = new BmoWFlowType();
			PmWFlowType pmWFlowType = new PmWFlowType(sFParams);
			bmoProjectWFlowType = (BmoWFlowType) pmWFlowType.get(bmoOrder.getWFlowTypeId().toInteger());

			BmoProject bmoProject = new BmoProject();
			PmProject pmProject = new PmProject(sFParams);
			bmoProject = (BmoProject) pmProject.getBy(bmoOrder.getId(), bmoProject.getOrderId().getName());

			PmConn pmConn = new PmConn(sFParams);

			String customer = "";
			if (bmoProject.getBmoCustomer().getCustomertype().toChar() == BmoCustomer.TYPE_PERSON) {
				customer = bmoProject.getBmoCustomer().getCode().toHtml() + " "
						+ bmoProject.getBmoCustomer().getFirstname().toHtml() + " "
						+ bmoProject.getBmoCustomer().getFatherlastname().toHtml() + " "
						+ bmoProject.getBmoCustomer().getMotherlastname().toHtml();
			} else {
				customer = bmoProject.getBmoCustomer().getCode().toHtml() + " "
						+ bmoProject.getBmoCustomer().getLegalname().toHtml();
			}
			
			// Obtener Direccion del evento
			String venue = "", venueDir =  "";
			// direccion cliente
			BmoCustomerAddress bmoCustomerAddress = new BmoCustomerAddress();
			PmCustomerAddress pmCustomerAddress = new PmCustomerAddress(sFParams);
			// direccion del salon
			BmoVenue bmoVenue = new BmoVenue();
			PmVenue pmVenue = new PmVenue(sFParams);
			
			// Si viene de un salon tomar datos, sino de la direccion del cliente
			if (bmoProject.getVenueId().toInteger() > 0) {
					bmoVenue = (BmoVenue)pmVenue.get(bmoProject.getVenueId().toInteger());
	
					if (bmoVenue.getHomeAddress().toInteger() > 0) {
						venue = bmoVenue.getName().toHtml() + ": " + bmoProject.getHomeAddress().toString();
					} else {
						venue = bmoVenue.getName().toHtml() + ": " +
								bmoVenue.getStreet().toHtml() + " " +
								" #" + bmoVenue.getNumber().toHtml() + " " +
								bmoVenue.getNeighborhood().toHtml() + ", " +
								bmoVenue.getBmoCity().getName().toHtml() + ", " +
								bmoVenue.getBmoCity().getBmoState().getCode().toHtml() + ", " +
								bmoVenue.getBmoCity().getBmoState().getBmoCountry().getCode().toHtml() + ".";
					}
			}
// 			else if (bmoProject.getCustomerAddressId().toInteger() > 0) {
// 				bmoCustomerAddress = (BmoCustomerAddress)pmCustomerAddress.get(bmoProject.getCustomerAddressId().toInteger());
				
// 				venue = bmoCustomerAddress.getStreet().toHtml() + ": " +
// 						bmoCustomerAddress.getNumber().toHtml() + " " +
// 						bmoCustomerAddress.getNeighborhood().toHtml() + ". ";
						
// 				venueDir = bmoCustomerAddress.getBmoCity().getName().toHtml() + ", " +
// 						bmoCustomerAddress.getBmoCity().getBmoState().getCode().toHtml() + ", " +
// 						bmoCustomerAddress.getBmoCity().getBmoState().getBmoCountry().getCode().toHtml() + ".";
// 			}
			%>
	<table border="0" cellspacing="0" width="100%" cellpadding="0"
		style="font-size: 12px">
		<tr>
			<td align="left" width="" rowspan="7" valign="top"><img
				border="0" width="<%= SFParams.LOGO_WIDTH %>"
				height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>"></td>
			<td colspan="4" class="reportTitleCell">Pedido</td>
		</tr>
		<tr>
			<th align="left" class="reportCellEven">Empresa/Cliente:</th>
			<td align="left" class="reportCellEven"><%= customer %></td>
			<th align="left" class="reportCellEven">Fecha/Hora Evento:</th>
			<td align="left" class="reportCellEven">
				<% if (!bmoProject.getStartDate().toString().equals("")) { %> de: <%= SFServerUtil.formatDate(sFParams, sFParams.getDateTimeFormat(), sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString(), bmoProject.getStartDate().toString()) %>
				<% } %> <% if (!bmoProject.getEndDate().toString().equals("")) { %>
				&nbsp;a:&nbsp; <%= SFServerUtil.formatDate(sFParams, sFParams.getDateTimeFormat(), sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString(), bmoProject.getEndDate().toString()) %>
				<% } %>
			</td>
		</tr>
		<tr>
			<th align="left" class="reportCellEven">Proyecto:</th>
			<td align="left" class="reportCellEven"><%= bmoProject.getCode().toHtml() + " - " + bmoProject.getName().toHtml() %>
			</td>
			<th align="left" class="reportCellEven"">Ciudad:</th>
			<td align="left" class="reportCellEven"><%= venueDir %></td>
		</tr>
		<tr>
			<th align="left" class="reportCellEven">Lugar:</th>
			<td align="left" class="reportCellEven"><%= venue%></td>
			<th align="left" class="reportCellEven">Tipo de Evento:</th>
			<td align="left" class="reportCellEven"><%= bmoProjectWFlowType.getName().toHtml() %>
			</td>
		</tr>
		<tr>
			<th align="left" class="reportCellEven">Ejecutivo Comercial:</th>
			<td align="left" class="reportCellEven"><%= bmoProject.getBmoUser().getFirstname().toHtml()%>
				<%= bmoProject.getBmoUser().getFatherlastname().toHtml()%> <%= bmoProject.getBmoUser().getMotherlastname().toHtml()%>
			</td>
			<th align="left" class="reportCellEven">Fecha Impresi&oacute;n:
			</th>
			<td align="left" class="reportCellEven"><%= SFServerUtil.nowToString(sFParams, sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString()) %>
			</td>
		</tr>
		<tr>
			<th align="left" class="reportCellEven">Moneda:</th>
			<td align="left" class="reportCellEven"><%= bmoOrder.getBmoCurrency().getCode().toHtml()%>
				- <%= bmoOrder.getBmoCurrency().getName().toHtml()%></td>
			<th align="left" class="reportCellEven">Estatus Pedido:</th>
			<td align="left" class="reportCellEven"><%= bmoOrder.getStatus().getSelectedOption().getLabeltoHtml()%>
			</td>
		</tr>
		<tr>
			<th align="left" class="reportCellEven">Estatus Entrega:</th>
			<td align="left" class="reportCellEven" colspan="3"><%= bmoOrder.getDeliveryStatus().getSelectedOption().getLabeltoHtml()%>
			</td>
		</tr>
	</table>
	<br>
	<table border="0" cellspacing="0" width="100%" cellpadding="0"
		style="font-size: 12px">
		<tr>
			<td class="reportHeaderCell" width="50%">Rubro</td>
			<td class="reportHeaderCellCenter" width="10%">Cantidad</td>
			<td class="reportHeaderCellCenter" width="10%">D&iacute;as</td>
			<td class="reportHeaderCellRight" width="10%">Precio</td>
			<td class="reportHeaderCellRight" width="10%">Subtotal</td>
			<td class="reportHeaderCellRight" width="10%">Total&nbsp;</td>
		</tr>
		<tr>
			<td colspan="6">&nbsp;</td>
		</tr>
		<%
		        String sql = "";
		        double subTotal = 0, iva = 0;
	       	 	double subTotalGeneral = 0;
              	BmoOrderGroup bmoOrderGroup = new BmoOrderGroup();
              	PmOrderGroup pmOrderGroup = new PmOrderGroup(sFParams);
	              BmFilter bmFilter = new BmFilter();
	              bmFilter.setValueFilter(bmoOrderGroup.getKind(), bmoOrderGroup.getOrderId().getName(), bmoOrder.getId());
	              Iterator<BmObject> quoteGroups = pmOrderGroup.list(bmFilter).iterator();
	              int i = 1;
	              while (quoteGroups.hasNext()) {
	            	  bmoOrderGroup = (BmoOrderGroup)quoteGroups.next();%>
		<tr>
			<td class="reportHeaderCell" colspan="5" style="white-space: normal">
				<%= i++ %>. <%= bmoOrderGroup.getName().toHtml() %> <%	if (bmoOrderGroup.getDescription().toString().length() > 0) { %>
				<br><%= bmoOrderGroup.getDescription().toHtml() %> <%	} %>
			</td>
			<td class="reportHeaderCellRight">
				<% if (bmoOrderGroup.getShowAmount().toBoolean()) { %> <%= formatCurrency.format(bmoOrderGroup.getAmount().toDouble()) %>&nbsp;
				<% } %>
			</td>
		</tr>
		<%	  
	          	  // Si es Kit solo pone descripcion
	          	  //if (bmoOrderGroup.getIsKit().toBoolean()) {
	          	
	          	  %><!--
	          		<tr>
	          			<td class="reportCellEven" colspan="5">
	                         &nbsp;<%//= bmoOrderGroup.getDescription().toHtml() %>
	                     </td>
		                  <td class="reportCellEven" align ="right">
		                       &nbsp;
							<% //if (bmoOrderGroup.getShowGroupImage().toBoolean() && (bmoOrderGroup.getImage().toString().length() > 0)) { 
								//String blobKeyParse = bmoOrderGroup.getImage().toString();
								//if (bmoOrderGroup.getImage().toString().indexOf(".") > 0)
									//blobKeyParse = bmoOrderGroup.getImage().toString().substring(0, bmoOrderGroup.getImage().toString().indexOf("."));
							%> 
		                    	<img src="/fileserve?blob-key=<%//= blobKeyParse %>" width="300" height="200" padding="4"> 
		                  	<% //} %> 
		                  </td> 
						</tr>-->
		<%
		  	  				//} else {
		
			              BmoOrderItem bmoOrderItem = new BmoOrderItem();
			              PmOrderItem pmOrderItem = new PmOrderItem(sFParams);
			              BmFilter bmFilterO = new BmFilter();
			              bmFilterO.setValueFilter(bmoOrderItem.getKind(), bmoOrderItem.getOrderGroupId().getName(), bmoOrderGroup.getId());
			              ArrayList<BmObject> list = pmOrderItem.list(bmFilterO);
			              Iterator<BmObject> items = list.iterator();
			              int index = 1, count = list.size();
			              while(items.hasNext()) {
			            	  bmoOrderItem = (BmoOrderItem)items.next(); %>
		<tr>
			<td class="reportCellEven" align="left">
				<% if(bmoOrderItem.getBmoProduct().getDisplayName().toString().equals("")) { %>
				<%= bmoOrderItem.getName().toHtml() %> <%= bmoOrderItem.getBmoProduct().getBrand().toHtml() %>
				<%= bmoOrderItem.getBmoProduct().getModel().toHtml() %> <% } else { %>
				<%= bmoOrderItem.getBmoProduct().getDisplayName().toHtml() %> <%= bmoOrderItem.getBmoProduct().getBrand().toHtml() %>
				<%= bmoOrderItem.getBmoProduct().getModel().toHtml() %> <% } %> <br>
				<span class="documentSubText"> <%= bmoOrderItem.getDescription().toHtml() %>
			</span>
			</td>
			<td class="reportCellEven" align="center">
				<% if (bmoOrderGroup.getShowQuantity().toBoolean()) { %> <%= bmoOrderItem.getQuantity().toDouble() %>
				<% } %>
			</td>
			<td class="reportCellEven" align="center">
				<% if (bmoOrderGroup.getShowQuantity().toBoolean()) { %> <%= bmoOrderItem.getDays().toDouble() %>
				<% } %>
			</td>
			<td class="reportCellEven" align="right">
				<% if (bmoOrderGroup.getShowPrice().toBoolean()) { %> <%= formatCurrency.format(bmoOrderItem.getPrice().toDouble()) %>
				<% } %>
			</td>
			<td class="reportCellEven" align="right">
				<% if (bmoOrderGroup.getShowPrice().toBoolean()) { %> <%= formatCurrency.format(bmoOrderItem.getAmount().toDouble()) %>
				<% } %>
			</td>
			<%
		                         
				                  // Si es Kit
				                  if (bmoOrderGroup.getIsKit().toBoolean()) {
				                	  if (index == 1) { %>
			<td class="reportCellEven" align="center" colspan="2"
				rowspan="<%= count %>" style="white-space: nowrap;" valign="center">&nbsp;
				<% if (bmoOrderGroup.getShowGroupImage().toBoolean() 
					                  				&& (bmoOrderGroup.getImage().toString().length() > 0)) {
					                  			String blobKeyParse = HtmlUtil.parseImageLink(sFParams, bmoOrderGroup.getImage());
				                        	 %> <img src="<%= blobKeyParse %>"
				width="300" height="200"> <% } %>
			</td>
			<% 
				                	  }	
				                  } else {		                         
			             					//Si estan seleccionados Mostrar-imgGrupo e imgProd, le da prioridad a la imgGrupo por mostrar y si selecciona solo imgGrupo solamente
		     							if ((bmoOrderGroup.getShowGroupImage().toBoolean() 
		     									&& (bmoOrderGroup.getImage().toString().length() > 0) 
		     									&& bmoOrderGroup.getShowProductImage().toBoolean() 
		     									//&& (bmoOrderItem.getBmoProduct().getImage().toString().length() > 0) 
		     									) 
		     									||
		     									(bmoOrderGroup.getShowGroupImage().toBoolean() 
		                     					&& (bmoOrderGroup.getImage().toString().length() > 0)
		                     					&& !bmoOrderGroup.getShowProductImage().toBoolean() )) {
		                     				if (index == 1) {
		                     					String blobKeyParse = HtmlUtil.parseImageLink(sFParams, bmoOrderGroup.getImage());
											%>
			<td class="reportCellEven" align="center" rowspan="<%= count %>"
				style="white-space: nowrap;" valign="center"><img
				src="<%= blobKeyParse %>" width="300" height="200" padding="4">
			</td>
			<% }//Fin index 						                         				
		                     				// Muestra solo imgProd si es seleccionada
		     								} else if (bmoOrderGroup.getShowProductImage().toBoolean() 
		     										&& (bmoOrderItem.getBmoProduct().getImage().toString().length() > 0)
			                    						&& !bmoOrderGroup.getShowGroupImage().toBoolean() ) {
		     									
		     										String blobKeyParse = HtmlUtil.parseImageLink(sFParams, bmoOrderItem.getBmoProduct().getImage());
		     										%>
			<td class="reportCellEven" align="center"
				style="white-space: nowrap;" valign="center"><img
				src="<%= blobKeyParse %>" width="100" height="100"></td>
			<% } else  { %>
			<td class="reportCellEven" align="center"
				style="white-space: nowrap;" valign="center">&nbsp;</td>
			<% }
		      	             		} %>
		</tr>
		<% 
			             	index++;
			             	} // fin de items
			             //}//else - Si es Kit
			             %>
		<tr>
			<td colspan="6">&nbsp;</td>
		</tr>
		<% } // Fin orderGroups %>
		<%	  
					BmoOrderEquipment bmoOrderEquipment = new BmoOrderEquipment();
		          	PmOrderEquipment pmOrderEquipment = new PmOrderEquipment(sFParams);
		          	BmFilter bmFilterEq = new BmFilter();
		          	bmFilterEq.setValueFilter(bmoOrderEquipment.getKind(), bmoOrderEquipment.getOrderId().getName(), bmoOrder.getId());
		          	Iterator<BmObject> items = pmOrderEquipment.list(bmFilterEq).iterator();
		          	int iOEquipment = 1;
		          	while(items.hasNext()) {
		          		bmoOrderEquipment = (BmoOrderEquipment)items.next();
		          		if (iOEquipment == 1) {
		        		%>
		<tr>
			<td class="reportHeaderCell" colspan="5">Recursos</td>
			<td class="reportHeaderCellRight">
				<%                    	
					            		//Obtener el total del staff                    		
					            		pmConn.open();                    		
					            		double totalEquipment = 0;                    		
					            		sql = " SELECT SUM(ordq_amount) AS totalequipments FROM orderequipments " +
					            		      " WHERE ordq_orderid = " +  bmoOrder.getId();                    		
					            		pmConn.doFetch(sql);
					            		if (pmConn.next()) { 
					            			totalEquipment = pmConn.getDouble("totalequipments");
					            	%> <%= formatCurrency.format(totalEquipment) %>&nbsp;
				<% }                    		
					            	  pmConn.close(); 
					                %>
			</td>
		</tr>
		<%		} %>
		<tr>
			<td class="reportCellEven" align="left"><%= bmoOrderEquipment.getBmoEquipment().getCode().toHtml() %>
				<%= bmoOrderEquipment.getName().toHtml() %> <%= bmoOrderEquipment.getDescription().toHtml() %>
			</td>
			<td class="reportCellEven" align="center">
				<% if (bmoOrder.getShowEquipmentQuantity().toBoolean()) { %> <%= bmoOrderEquipment.getQuantity().toInteger() %>
				<% } %>
			</td>
			<td class="reportCellEven" align="center">
				<% if (bmoOrder.getShowEquipmentQuantity().toBoolean()) { %> <%= bmoOrderEquipment.getDays().toDouble() %>
				<% } %>
			</td>
			<td class="reportCellEven" align="right">
				<% if (bmoOrder.getShowEquipmentPrice().toBoolean()) { %> <%= formatCurrency.format(bmoOrderEquipment.getPrice().toDouble()) %>
				<% } %>
			</td>
			<td class="reportCellEven" align="right">
				<% if (bmoOrder.getShowEquipmentPrice().toBoolean()) { %> <%= formatCurrency.format(bmoOrderEquipment.getAmount().toDouble()) %>
				<% } %>
			</td>
			<td class="reportCellEven" align="left">&nbsp;</td>
		<tr>
			<td colspan="6" class="">&nbsp;</td>
		</tr>
		<tr>
			<% 
		        	iOEquipment++;
		          	} 
		          	
					BmoOrderStaff bmoOrderStaff = new BmoOrderStaff();
		          	PmOrderStaff pmOrderStaff = new PmOrderStaff(sFParams);
		          	BmFilter bmFilterSt = new BmFilter();
		          	bmFilterSt.setValueFilter(bmoOrderStaff.getKind(), bmoOrderStaff.getOrderId().getName(), bmoOrder.getId());
		          	Iterator<BmObject> itemsStaff = pmOrderStaff.list(bmFilterSt).iterator();
		          	int iOStaff = 1;
		          	while(itemsStaff.hasNext()) {
		          		bmoOrderStaff = (BmoOrderStaff)itemsStaff.next();
		          		if (iOStaff == 1) {
		        	%>
		
		<tr>
			<td class="reportHeaderCell" colspan="5">Personal</td>
			<td class="reportHeaderCellRight">
				<%                    	
						            //Obtener el total del staff                    		
						            pmConn.open();                    		
						            double totalStaff = 0;                    		
						            sql = " SELECT SUM(ords_amount) AS totalstaff FROM orderstaff " +
						            		" WHERE ords_orderid = " +  bmoOrder.getId();                    		
						            pmConn.doFetch(sql);
						            if (pmConn.next()) { 
						            	totalStaff = pmConn.getDouble("totalstaff");
					            	%> <%= formatCurrency.format(totalStaff) %>&nbsp; <% }                    		
						            pmConn.close(); 
					            %>
			</td>
		</tr>
		<%	} %>
		<tr>
			<td class="reportCellEven" align="left"><%= bmoOrderStaff.getName().toHtml() %>
				<%= bmoOrderStaff.getDescription().toHtml() %></td>
			<td class="reportCellEven" align="center">
				<% if (bmoOrder.getShowStaffQuantity().toBoolean()) { %> <%= bmoOrderStaff.getQuantity().toInteger() %>
				<% } %>
			</td>
			<td class="reportCellEven" align="center">
				<% if (bmoOrder.getShowStaffQuantity().toBoolean()) { %> <%= bmoOrderStaff.getDays().toDouble()%>
				<% } %>
			</td>
			<td class="reportCellEven" align="right">
				<% if (bmoOrder.getShowStaffPrice().toBoolean()) { %> <%= formatCurrency.format(bmoOrderStaff.getPrice().toDouble()) %>
				<% } %>
			</td>
			<td class="reportCellEven" align="right">
				<% if (bmoOrder.getShowStaffPrice().toBoolean()) { %> <%= formatCurrency.format(bmoOrderStaff.getAmount().toDouble()) %>
				<% } %>
			</td>
			<td class="reportCellEven" align="left">&nbsp;</td>
		<tr>
		<tr>
			<td colspan="6" class="">&nbsp;</td>
		</tr>
		<% 	
		        		iOStaff++;
	          		} %>

		<tr>
			<td colspan="4" rowspan="4" valign="top" align="left"
				class="detailStart">
				<p class="documentComments">
					<b>Notas:</b> <br> Personas:
					<%= bmoProject.getGuests().toString() %>
					<br>
					<%= bmoOrder.getDescription().toHtml() %>
				</p>
			</td>
			<th class="" align="right">Subtotal:</th>
			<td class="reportCellEven" align="right"><b><%=  formatCurrency.format(bmoOrder.getAmount().toDouble()) %></b>
			</td>
		</tr>
		<tr>
			<th class="" align="right">
				<% if (bmoOrder.getDiscount().toDouble() > 0) { %> Descuento: <% } %>
			</th>
			<% if (bmoOrder.getDiscount().toDouble() > 0) { %>
			<td class="reportCellEven" align="right"><b><%=  formatCurrency.format(bmoOrder.getDiscount().toDouble()) %></b>
			</td>
			<% } else { %>
			<td class="" align="right"></td>
			<% } %>
		</tr>
		<tr>
			<th class="" align="right">
				<% if (bmoOrder.getTax().toDouble() > 0) { %> IVA: <% } %>
			</th>
			<% if (bmoOrder.getTax().toDouble() > 0) { %>
			<td class="reportCellEven" align="right"><b><%=  formatCurrency.format(bmoOrder.getTax().toDouble()) %></b>
			</td>
			<% } else { %>
			<td class="" align="right"></td>
			<% } %>
		</tr>
		<tr>
			<th class="" align="right">Total:</th>
			<td class="reportCellEven" align="right"><b><%=  formatCurrency.format(bmoOrder.getTotal().toDouble()) %></b>
			</td>
		</tr>
	</table>
	<%
				
			}else if(bmoOrderType.getType().equals("" + BmoOrderType.TYPE_PROPERTY)){
				
				PmPropertySale pmPropertySale= new PmPropertySale(sFParams);
				PmWFlowType pmWFlowType = new PmWFlowType(sFParams);
				PmOrderProperty pmOrderProperty = new PmOrderProperty(sFParams);
				
				bmoOrder = (BmoOrder)pmOrder.get(foreignId);
				
				bmoPropertySale = (BmoPropertySale)pmPropertySale.getBy(bmoOrder.getId(), bmoPropertySale.getOrderId().getName());
				bmoPropertySaleWFlowType = (BmoWFlowType)pmWFlowType.get(bmoPropertySale.getWFlowTypeId().toInteger());
				bmoOrderProperty = (BmoOrderProperty)pmOrderProperty.getBy(bmoOrder.getId(), bmoOrderProperty.getOrderId().getName());
				
				PmConn pmConn = new PmConn(sFParams);
				
				String customer = "";
				if (bmoPropertySale.getBmoCustomer().getCustomertype().toChar() == BmoCustomer.TYPE_PERSON) {
					customer =  bmoPropertySale.getBmoCustomer().getCode().toHtml() + " " +
								bmoPropertySale.getBmoCustomer().getDisplayName().toHtml();
				} else {
					customer = bmoPropertySale.getBmoCustomer().getCode().toHtml() + " - " +
								bmoPropertySale.getBmoCustomer().getLegalname().toHtml();
				}
				BmoUser bmoUser = new BmoUser();
				PmUser pmUser = new PmUser(sFParams);
				bmoUser = (BmoUser)pmUser.get(bmoOrder.getUserId().toInteger());

		%>
	<table border="0" cellspacing="0" width="100%" cellpadding="0"
		style="font-size: 12px">
		<tr>
			<td align="left" width="" rowspan="6" valign="top"><img
				border="0" width="<%= SFParams.LOGO_WIDTH %>"
				height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>"></td>
			<td colspan="4" class="reportHeaderCell">Pedido <%= bmoOrder.getCode().toHtml()%>
			</td>
		</tr>
		<tr>
			<th class="reportCellEven" align="left">Cliente:</th>
			<td class="reportCellEven" align="left"><%= customer%></td>
			<th class="reportCellEven" align="left">Inmueble:</th>
			<td class="reportCellEven" align="left"><%= bmoPropertySale.getBmoProperty().getCode().toHtml()%>
			</td>
		</tr>
		<tr>
			<th class="reportCellEven" align="left">Venta Inmueble:</th>
			<td class="reportCellEven" align="left"><%= bmoPropertySale.getCode().toHtml() %>
			</td>
			<th class="reportCellEven" align="left">Tipo de Cr&eacute;dito:
			</th>
			<td class="reportCellEven" align="left"><%= bmoPropertySaleWFlowType.getName().toHtml() %>
			</td>
		</tr>
		<tr>
			<th class="reportCellEven" align="left">Ejecutivo Comercial:</th>
			<td class="reportCellEven" align="left"><%= bmoPropertySale.getBmoSalesUser().getFirstname().toHtml()%>
				<%= bmoPropertySale.getBmoSalesUser().getFatherlastname().toHtml()%>
			</td>
			<th class="reportCellEven" align="left">Fecha Impresi&oacute;n:
			</th>
			<td class="reportCellEven" align="left"><%= SFServerUtil.nowToString(sFParams, sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString()) %>
			</td>
		</tr>
		<tr>
			<th class="reportCellEven" align="left">Moneda:</th>
			<td class="reportCellEven" align="left"><%= bmoOrder.getBmoCurrency().getCode().toHtml() %>
				- <%= bmoOrder.getBmoCurrency().getName().toHtml() %></td>
			<th class="reportCellEven" align="left">Status Pedido:</th>
			<td class="reportCellEven" align="left"><%= bmoOrder.getStatus().getSelectedOption().getLabeltoHtml()%>
			</td>
		</tr>
		<tr>
			<th class="reportCellEven" align="left">Status Entrega:</th>
			<td class="reportCellEven" align="left" colspan="3"><%= bmoOrder.getDeliveryStatus().getSelectedOption().getLabeltoHtml()%>
			</td>
		</tr>
	</table>
	<br>
	<table border="0" cellspacing="0" width="100%" cellpadding="0"
		style="font-size: 12px">
		<tr>
			<td class="reportHeaderCell" align="left" width="10%" colspan="9">
				Inmueble</td>
		</tr>
		<tr>
			<th class="reportCellEven" align="left" width="12%">Clave</th>
			<th class="reportCellEven" align="left" width="10%">Mza</th>
			<th class="reportCellEven" align="left" width="8%">Lote</th>
			<th class="reportCellEven" align="left" width="20%">Calle y
				N&uacute;mero</th>
			<th class="reportCellEven" align="right" width="10%">Precio</th>
			<th class="reportCellEven" align="right" width="10%">$ T. Ex.</th>
			<th class="reportCellEven" align="right" width="10%">$ C. Ex.</th>
			<th class="reportCellEven" align="right" width="10%">$ Otros</th>
			<th class="reportCellEven" align="right" width="10%">Total</th>
		</tr>
		<tr>
			<td class="reportCellEven" align="left"><%= bmoOrderProperty.getBmoProperty().getCode().toHtml()%>
			</td>
			<td class="reportCellEven" align="left"><%= bmoOrderProperty.getBmoProperty().getBmoDevelopmentBlock().getCode().toHtml()%>
			</td>
			<td class="reportCellEven" align="left"><%= bmoOrderProperty.getBmoProperty().getLot().toHtml()%>
			</td>
			<td class="reportCellEven" align="left"><%= bmoOrderProperty.getBmoProperty().getStreet().toHtml()%>
				#<%= bmoOrderProperty.getBmoProperty().getNumber().toHtml()%></td>
			<td class="reportCellEven" align="right"><%= formatCurrency.format(bmoOrderProperty.getPrice().toDouble())%>
			</td>
			<td class="reportCellEven" align="right"><%= formatCurrency.format(bmoOrderProperty.getExtraLand().toDouble())%>
			</td>
			<td class="reportCellEven" align="right"><%= formatCurrency.format(bmoOrderProperty.getExtraConstruction().toDouble())%>
			</td>
			<td class="reportCellEven" align="right"><%= formatCurrency.format(bmoOrderProperty.getExtraOther().toDouble())%>
			</td>
			<td class="reportCellEven" align="right"><%= formatCurrency.format(bmoOrderProperty.getAmount().toDouble()) %>
			</td>
		</tr>
	</table>
	<br>
	<br>
	<table border="0" cellspacing="0" width="100%" cellpadding="0"
		style="font-size: 12px">
		<tr>
			<td class="reportHeaderCell" align="left" width="10%" colspan="6">
				Extras</td>
		</tr>
		<tr>
			<th class="reportCellEven" align="left" width="12%">Cantidad</th>
			<th class="reportCellEven" align="left" width="30%">Extra</th>
			<th class="reportCellEven" align="left" width="33%">Comentarios</th>
			<th class="reportCellEven" align="right" width="15%">Precio</th>
			<th class="reportCellEven" align="right" width="15%">Total</th>
		</tr>
		<%	  
				    	  double totalExtras = 0;
			              PmOrderPropertyModelExtra pmOrderPropertyModelExtra = new PmOrderPropertyModelExtra(sFParams);
			              BmFilter bmFilterOrpx = new BmFilter();
			              bmFilterOrpx.setValueFilter(bmoOrderPropertyModelExtra.getKind(), bmoOrderPropertyModelExtra.getOrderId().getName(), bmoOrder.getId());
			              Iterator<BmObject> extras = pmOrderPropertyModelExtra.list(bmFilterOrpx).iterator();
			              while(extras.hasNext()) {
			            	  bmoOrderPropertyModelExtra = (BmoOrderPropertyModelExtra)extras.next();	
			            	  totalExtras += bmoOrderPropertyModelExtra.getAmount().toDouble();
			                 %>
		<tr>
			<td class="reportCellEven" align="left"><%= bmoOrderPropertyModelExtra.getQuantity().toHtml() %>
			</td>
			<td class="reportCellEven" align="left"><%= bmoOrderPropertyModelExtra.getBmoPropertyModelExtra().getName().toHtml() %>
			</td>
			<td class="reportCellEven" align="left"><%= bmoOrderPropertyModelExtra.getComments().toHtml() %>
			</td>
			<td class="reportCellEven" align="right"><%= formatCurrency.format(bmoOrderPropertyModelExtra.getPrice().toDouble())%>
			</td>
			<td class="reportCellEven" align="right"><%= formatCurrency.format(bmoOrderPropertyModelExtra.getAmount().toDouble()) %>
			</td>
		<tr>
			<% } %>
		
		<tr>
			<th class="reportCellEven" align="right" colspan="4">Total:</th>
			<td class="reportCellEven" align="right"><b><%= formatCurrency.format(totalExtras) %></b>
			</td>
		</tr>
	</table>
	<br>
	<table border="0" cellspacing="0" width="100%" cellpadding="0"
		style="font-size: 12px">
		<tr>
			<td class="reportHeaderCell" align="left" width="10%" colspan="10">
				CxC</td>
		</tr>
		<tr>
			<th class="reportCellEven" align="left" width="12%">Concepto</th>
			<th class="reportCellEven" align="left" width="12%">Factura</th>
			<th class="reportCellEven" align="left" width="10%">Folio</th>
			<th class="reportCellEven" align="left" width="15%">Descripci&oacute;n</th>
			<th class="reportCellEven" align="left" width="8%">F. Ingreso</th>
			<th class="reportCellEven" align="left" width="8%">F.
				Vencimiento</th>
			<th class="reportCellEven" align="left" width="5%">Estatus</th>
			<th class="reportCellEven" align="right" width="10%">Cargo</th>
			<th class="reportCellEven" align="right" width="10%">Abono</th>
			<th class="reportCellEven" align="right" width="10%">Saldo</th>

		</tr>
		<%	  
				    double totalAmount = 0, totalPayments = 0, totalBalance = 0;
			          PmRaccount pmRaccount = new PmRaccount(sFParams);
			          BmFilter bmFilterRacc = new BmFilter();
			          bmFilterRacc.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getOrderId().getName(), bmoOrder.getId());
			          Iterator<BmObject> racc = pmRaccount.list(bmFilterRacc).iterator();
			          while(racc.hasNext()) {
			        	  bmoRaccount = (BmoRaccount)racc.next();	
			        	  totalAmount += bmoRaccount.getAmount().toDouble();
			        	  totalPayments += bmoRaccount.getPayments().toDouble();
			        	  totalBalance += bmoRaccount.getBalance().toDouble();
			             %>
		<tr>
			<td class="reportCellEven" align="left"><%= bmoRaccount.getBmoRaccountType().getName().toHtml() %>
			</td>
			<td class="reportCellEven" align="left"><%= bmoRaccount.getInvoiceno().toHtml() %>
			</td>
			<td class="reportCellEven" align="left"><%= bmoRaccount.getFolio().toHtml() %>
			</td>
			<td class="reportCellEven" align="left"><%= bmoRaccount.getDescription().toHtml() %>
			</td>
			<td class="reportCellEven" align="left"><%= bmoRaccount.getReceiveDate().toHtml() %>
			</td>
			<td class="reportCellEven" align="left"><%= bmoRaccount.getDueDate().toHtml() %>
			</td>
			<td class="reportCellEven" align="left"><%= bmoRaccount.getStatus().getSelectedOption().getLabel() %>
			</td>
			<td class="reportCellEven" align="right"><%= formatCurrency.format(bmoRaccount.getAmount().toDouble())%>
			</td>
			<td class="reportCellEven" align="right"><%= formatCurrency.format(bmoRaccount.getPayments().toDouble()) %>
			</td>
			<td class="reportCellEven" align="right"><%= formatCurrency.format(bmoRaccount.getBalance().toDouble()) %>
			</td>
		<tr>
			<% } %>
		
		<tr>
			<th class="reportCellEven" align="right" colspan="7">Total:</th>
			<td class="reportCellEven" align="right"><b><%= formatCurrency.format(totalAmount) %></b>
			</td>
			<td class="reportCellEven" align="right"><b><%= formatCurrency.format(totalPayments) %></b>
			</td>
			<td class="reportCellEven" align="right"><b><%= formatCurrency.format(totalBalance) %></b>
			</td>
		</tr>
	</table>
	<%
			}else if(bmoOrderType.getType().equals("" + BmoOrderType.TYPE_SALE)){
				//System.out.println("Venta");
				BmoWFlowType bmoWFlowType = new BmoWFlowType();
				PmWFlowType pmWFlowTypeSale = new PmWFlowType(sFParams);
				bmoWFlowType = (BmoWFlowType)pmWFlowTypeSale.get(bmoOrder.getWFlowTypeId().toInteger());
				
				BmoUser bmoUser= new BmoUser();
				PmUser pmUser = new PmUser(sFParams);
				bmoUser = (BmoUser)pmUser.get(bmoOrder.getUserId().toInteger());
				
				
				String customer = "";
				if (bmoOrder.getBmoCustomer().getCustomertype().toChar() == BmoCustomer.TYPE_PERSON) {
					customer =  bmoOrder.getBmoCustomer().getCode().toHtml() + " " +  bmoOrder.getBmoCustomer().getDisplayName().toHtml();
				} else {
					customer = bmoOrder.getBmoCustomer().getCode().toHtml() + " " + 
							bmoOrder.getBmoCustomer().getDisplayName().toHtml();
				}	
				
		%>
	<table border="0" cellspacing="0" width="100%" cellpadding="0"
		style="font-size: 12px">
		<tr>
			<td align="left" width="" rowspan="6" valign="top"><img
				border="0" width="<%= SFParams.LOGO_WIDTH %>"
				height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>"></td>
			<td colspan="4" class="reportTitleCell">Pedido: <%= bmoOrder.getCode().toHtml() %>
			</td>
		</tr>
		<tr>
			<th class="reportCellEven" align="left">Nombre:</th>
			<td class="reportCellEven" align="left"><%= bmoOrder.getName().toHtml() %>
			</td>
			<th class="reportCellEven" align="left">Tipo:</th>
			<td class="reportCellEven" align="left"><%= bmoWFlowType.getName().toHtml() %>
			</td>
		</tr>
		<tr>
			<th class="reportCellEven" align="left">Empresa/Cliente:</th>
			<td class="reportCellEven" align="left"><%= customer %></td>
			<th class="reportCellEven" align="left">Raz&oacute;n Social:</th>
			<td class="reportCellEven" align="left"><%= bmoOrder.getBmoCustomer().getLegalname().toHtml() %>
			</td>
		</tr>
		<tr>
			<th class="reportCellEven" align="left">Ejecutivo Comercial:</th>
			<td class="reportCellEven" align="left"><%= bmoUser.getFirstname().toHtml()%>
				<%= bmoUser.getFatherlastname().toHtml()%></td>
			<th class="reportCellEven" align="left">Fecha/Hora Pedido:</th>
			<td class="reportCellEven" align="left">
				<% if (!bmoOrder.getLockStart().toString().equals("")) { %> <%= SFServerUtil.formatDate(sFParams, sFParams.getDateTimeFormat(), sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString(), bmoOrder.getLockStart().toString()) %>
				<% } %> <% if (!bmoOrder.getLockEnd().toString().equals("")) { %> - <%= SFServerUtil.formatDate(sFParams, sFParams.getDateTimeFormat(), sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString(), bmoOrder.getLockEnd().toString()) %>
				<% } %>
			</td>
		</tr>
		<tr>
			<th class="reportCellEven" align="left">Moneda:</th>
			<td class="reportCellEven" align="left"><%= bmoOrder.getBmoCurrency().getCode().toHtml()%>
				- <%= bmoOrder.getBmoCurrency().getName().toHtml()%></td>
			<th class="reportCellEven" align="left">Fecha Impresi&oacute;n:
			</th>
			<td class="reportCellEven" align="left"><%= SFServerUtil.nowToString(sFParams, sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString()) %>
			</td>
		</tr>
		<tr>
			<th class="reportCellEven" align="left">Estatus Pedido:</th>
			<td class="reportCellEven" align="left"><%= bmoOrder.getStatus().getSelectedOption().getLabeltoHtml()%>
			</td>
			<th class="reportCellEven" align="left">Estatus Entrega:</th>
			<td class="reportCellEven" align="left"><%= bmoOrder.getDeliveryStatus().getSelectedOption().getLabeltoHtml()%>
			</td>
		</tr>
	</table>
	<br>
	<table border="0" cellspacing="0" width="100%" cellpadding="0"
		style="font-size: 12px">
		<tr>
			<th class="reportHeaderCell" width="50%" colspan="3">Rubro</th>
			<th class="reportHeaderCellCenter" width="10%">Cantidad</th>
			<!--<th class="reportHeaderCellCenter" width="10%">D&iacute;as</th>-->
			<th class="reportHeaderCellRight" width="10%">Precio</th>
			<th class="reportHeaderCellRight" width="10%">Subtotal</th>
			<th class="reportHeaderCellRight" width="10%" colspan="2">Total</th>
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		<tr>
			<%
						
						
						
						
						

						double subTotal = 0, iva = 0;
						double subTotalGeneral = 0;
						BmoOrderGroup bmoOrderGroup = new BmoOrderGroup();
						PmOrderGroup pmOrderGroup = new PmOrderGroup(sFParams);
						BmFilter bmFilter = new BmFilter();
						bmFilter.setValueFilter(bmoOrderGroup.getKind(), bmoOrderGroup.getOrderId().getName(), bmoOrder.getId());
						Iterator<BmObject> quoteGroups = pmOrderGroup.list(bmFilter).iterator();
						int i = 1;
						while (quoteGroups.hasNext()) {
							bmoOrderGroup = (BmoOrderGroup)quoteGroups.next(); %>
		
		<tr>
			<td class="reportHeaderCell" colspan="6" style="white-space: normal">
				<%= i++ %>. <%= bmoOrderGroup.getName().toHtml() %> <%	if (bmoOrderGroup.getDescription().toString().length() > 0) { %>
				<br><%= bmoOrderGroup.getDescription().toHtml() %> <%	} %>
			</td>
			<td class="reportHeaderCellRight" colspan="2"><%= formatCurrency.format(bmoOrderGroup.getAmount().toDouble()) %>
			</td>
		</tr>
		<%	  
							// Si es Kit solo pone descripcion
							//if (bmoOrderGroup.getIsKit().toBoolean()) {
			
							%><!--
							<tr>
								<td class="reportCellEven" colspan="7">
									&nbsp;<%//= bmoOrderGroup.getDescription().toHtml() %>
								</td>
								<td align ="right">
									&nbsp;
								<% 	//if (bmoOrderGroup.getShowGroupImage().toBoolean() && (bmoOrderGroup.getImage().toString().length() > 0)) { 
									//String blobKeyParse = bmoOrderGroup.getImage().toString();
									//if (bmoOrderGroup.getImage().toString().indexOf(".") > 0)
									//blobKeyParse = bmoOrderGroup.getImage().toString().substring(0, bmoOrderGroup.getImage().toString().indexOf("."));
								%> 
									<img src="/fileserve?blob-key=<%//= blobKeyParse %>" width="300" height="200" padding="4"> 
								<% //} %> 
								</td> 
							</tr>-->
		<%
			//} else {

						BmoOrderItem bmoOrderItem = new BmoOrderItem();
						PmOrderItem pmOrderItem = new PmOrderItem(sFParams);
						BmFilter bmFilterO = new BmFilter();
						bmFilterO.setValueFilter(bmoOrderItem.getKind(), bmoOrderItem.getOrderGroupId().getName(),
								bmoOrderGroup.getId());
						ArrayList<BmObject> list = pmOrderItem.list(bmFilterO);
						Iterator<BmObject> items = list.iterator();
						int index = 1, count = list.size();
						while (items.hasNext()) {
							bmoOrderItem = (BmoOrderItem) items.next();
		%>
		<tr>
			<td class="reportCellEven" align="left" colspan="3">
				<%
					if (bmoOrderItem.getProductId().toInteger() > 0) {
										if (bmoOrderItem.getBmoProduct().getDisplayName().toString().equals("")) {
				%>
				<%=bmoOrderItem.getBmoProduct().getName().toHtml()%> <%
 	} else {
 %>
				<%=bmoOrderItem.getBmoProduct().getDisplayName().toHtml()%> <%
 	}
 					} else {
 %> <%=bmoOrderItem.getName().toHtml()%> <%
 	}
 %> <br>
			<span class="documentSubText"><%=bmoOrderItem.getDescription().toHtml()%></span>
			</td>
			<td class="reportCellEven" align="center">
				<%
					//if (bmoOrderGroup.getShowQuantity().toBoolean()) {
				%> <%=bmoOrderItem.getQuantity().toDouble()%>
				<%
					//}
				%>
			</td>
			<!--
									<td class="reportCellEven" align="center">
										<%// if (bmoOrderGroup.getShowQuantity().toBoolean()) { %>  
											<%//= bmoOrderItem.getDays().toDouble() %>
										<%// } %>  	                        	
									</td>
									-->
			<td class="reportCellEven" align="right">
				<%
					//if (bmoOrderGroup.getShowPrice().toBoolean()) {
				%> <%=formatCurrency.format(bmoOrderItem.getPrice().toDouble())%>
				<%
					//}
				%>
			</td>

			<td class="reportCellEven" align="right">
				<%
					//if (bmoOrderGroup.getShowPrice().toBoolean()) {
				%> <%=formatCurrency.format(bmoOrderItem.getAmount().toDouble())%>
				<%
					//}
				%>
			</td>
			<%
				// Si es Kit
								if (bmoOrderGroup.getIsKit().toBoolean()) {
			%>
			<%
				if (index == 1) {
			%>
			<td class="reportCellEven" align="center" colspan="2"
				rowspan="<%=count%>" style="white-space: nowrap;" valign="center">
				<%
					if (bmoOrderGroup.getShowGroupImage().toBoolean()
													&& (bmoOrderGroup.getImage().toString().length() > 0)) {

												String blobKeyParse = HtmlUtil.parseImageLink(sFParams,
														bmoOrderGroup.getImage());
				%> <img src="<%=blobKeyParse%>" width="300" height="200"
				colspan="2"> <%
 	}
 %>
			</td>
			<%
				}
								} else {
									//Si estan seleccionados Mostrar-imgGrupo e imgProd, le da prioridad a la imgGrupo por mostrar y si selecciona solo imgGrupo solamente

									if ((bmoOrderGroup.getShowGroupImage().toBoolean()
											&& (bmoOrderGroup.getImage().toString().length() > 0)
											&& bmoOrderGroup.getShowProductImage().toBoolean()
									//&& (bmoOrderItem.getBmoProduct().getImage().toString().length() > 0)
									) || (bmoOrderGroup.getShowGroupImage().toBoolean()
											&& (bmoOrderGroup.getImage().toString().length() > 0)
											&& !bmoOrderGroup.getShowProductImage().toBoolean())) {
										if (index == 1) {
											String blobKeyParse = HtmlUtil.parseImageLink(sFParams,
													bmoOrderGroup.getImage());
			%>
			<td class="reportCellEven" align="center" colspan="2"
				rowspan="<%=count%>" style="white-space: nowrap;" valign="center">
				<img src="<%=blobKeyParse%>" width="300" height="200" padding="4">
			</td>
			<%
				} //Fin index 						                         				
											// Muestra solo imgProd si es seleccionada
									} else if (bmoOrderGroup.getShowProductImage().toBoolean()
											&& (bmoOrderItem.getBmoProduct().getImage().toString().length() > 0)
											&& !bmoOrderGroup.getShowGroupImage().toBoolean()) {

										String blobKeyParse = HtmlUtil.parseImageLink(sFParams,
												bmoOrderItem.getBmoProduct().getImage());
			%>
			<td class="reportCellEven" colspan="2" align="center"
				style="white-space: nowrap;" valign="center"><img
				src="<%=blobKeyParse%>" width="100" height="100"></td>
			<%
				} else {
			%>
			<td class="reportCellEven" align="center"
				style="white-space: nowrap;" valign="center" colspan="2">
				&nbsp;</td>
			<%
				}
								}
			%>
		</tr>
		<%
			index++;
						} // fin de while
							//}//else - Si es Kit
		%>
		<%
			} //fin de groups
		%>
		<tr>
			<td colspan="8" class="">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="6" rowspan="4" valign="top" align="left"
				class="detailStart">
				<p class="documentComments">
					<b>Notas:</b> <br>
					<%=bmoOrder.getDescription().toHtml()%></p>
			</td>
			<td class="" align="right"><b>Subtotal:</b></td>
			<td class="reportCellEven" align="right"><b><%=formatCurrency.format(bmoOrder.getAmount().toDouble())%></td>
		</tr>
		<tr>
			<td class="" align="right">
				<%
					if (bmoOrder.getDiscount().toDouble() > 0) {
				%> <b>Descuento:</b> <%
 	}
 %>
			</td>
			<%
				if (bmoOrder.getDiscount().toDouble() > 0) {
			%>
			<td class="reportCellEven" align="right"><b><%=formatCurrency.format(bmoOrder.getDiscount().toDouble())%></b>
				<%
					} else {
				%>
			<td class="" align="right"></td>
			<%
				}
			%>
			</td>
		</tr>
		<tr>
			<td class="" align="right">
				<%
					if (bmoOrder.getTax().toDouble() > 0) {
				%> <b>IVA:</b> <%
 	}
 %>
			</td>
			<%
				if (bmoOrder.getTax().toDouble() > 0) {
			%>
			<td class="reportCellEven" align="right"><b><%=formatCurrency.format(bmoOrder.getTax().toDouble())%></b>
			</td>
			<%
				} else {
			%>
			<td class="" align="right"></td>
			<%
				}
			%>
		</tr>
		<tr>
			<td class="" align="right"><b>Total:</b></td>
			<td class="reportCellEven" align="right"><b><%=formatCurrency.format(bmoOrder.getTotal().toDouble())%></b>
			</td>
		</tr>
	</table>
	<%
		}
			if (bmoOrderType.getType().equals("" + BmoOrderType.TYPE_CREDIT)) {
				BmoWFlowType bmoWFlowType = new BmoWFlowType();
				PmWFlowType pmWFlowTypeSale = new PmWFlowType(sFParams);
				bmoWFlowType = (BmoWFlowType) pmWFlowTypeSale.get(bmoOrder.getWFlowTypeId().toInteger());

				BmoUser bmoUser = new BmoUser();
				PmUser pmUser = new PmUser(sFParams);
				bmoUser = (BmoUser) pmUser.get(bmoOrder.getUserId().toInteger());

				String customer = "";
				if (bmoOrder.getBmoCustomer().getCustomertype().toChar() == BmoCustomer.TYPE_PERSON) {
					customer = bmoOrder.getBmoCustomer().getCode().toHtml() + " "
							+ bmoOrder.getBmoCustomer().getDisplayName().toHtml();
				} else {
					customer = bmoOrder.getBmoCustomer().getCode().toHtml() + " "
							+ bmoOrder.getBmoCustomer().getDisplayName().toHtml();
				}
	%>
	<table border="0" cellspacing="0" width="100%" cellpadding="0"
		style="font-size: 12px">
		<tr>
			<td align="left" width="" rowspan="6" valign="top"><img
				border="0" width="<%=SFParams.LOGO_WIDTH%>"
				height="<%=SFParams.LOGO_HEIGHT%>" src="<%=logoURL%>"></td>
			<td colspan="4" class="reportTitleCell">Pedido: <%=bmoOrder.getCode().toHtml()%>
			</td>
		</tr>
		<tr>
			<th class="reportCellEven" align="left" >Nombre:</th>
			<td class="reportCellEven" align="left"><%=bmoOrder.getName().toHtml()%>
			</td>
			<th class="reportCellEven" align="left">Tipo:</th>
			<td class="reportCellEven" align="left"><%=bmoWFlowType.getName().toHtml()%>
			</td>
		</tr>
		<tr>
			<th class="reportCellEven" align="left">Empresa/Cliente:</th>
			<td class="reportCellEven" align="left"><%=customer%></td>
			<th class="reportCellEven" align="left">Raz&oacute;n Social:</th>
			<td class="reportCellEven" align="left"><%=bmoOrder.getBmoCustomer().getLegalname().toHtml()%>
			</td>
		</tr>
		<tr>
			<th class="reportCellEven" align="left">Ejecutivo Comercial:</th>
			<td class="reportCelltext" align="left"><%=bmoUser.getFirstname().toHtml()%>
				<%=bmoUser.getFatherlastname().toHtml()%></td>
			<th class="reportCellEven" align="left">Fecha/Hora Pedido:</th>
			<td class="reportCellEven" align="left">
				<%
					if (!bmoOrder.getLockStart().toString().equals("")) {
				%> <%=SFServerUtil.formatDate(sFParams, sFParams.getDateTimeFormat(),
								sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString(),
								bmoOrder.getLockStart().toString())%>
				<%
					}
				%> <%
 	if (!bmoOrder.getLockEnd().toString().equals("")) {
 %> - <%=SFServerUtil.formatDate(sFParams, sFParams.getDateTimeFormat(),
								sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString(),
								bmoOrder.getLockEnd().toString())%>
				<%
					}
				%>
			</td>
		</tr>
		<tr>
			<th class="reportCellEven" align="left">Moneda:</th>
			<td class="reportCellEven" align="left"><%=bmoOrder.getBmoCurrency().getCode().toHtml()%>
				- <%=bmoOrder.getBmoCurrency().getName().toHtml()%></td>
			<th class="reportCellEven" align="left">Fecha Impresi&oacute;n:
			</th>
			<td class="reportCellEven" align="left"><%=SFServerUtil.nowToString(sFParams,
							sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString())%>
			</td>
		</tr>
		<tr>
			<th class="reportCellEven" align="left">Estatus Pedido:</th>
			<td class="reportCellEven" align="left"><%=bmoOrder.getStatus().getSelectedOption().getLabeltoHtml()%>
			</td>
			<th class="reportCellEven" align="left">Estatus Entrega:</th>
			<td class="reportCellEven" align="left"><%=bmoOrder.getDeliveryStatus().getSelectedOption().getLabeltoHtml()%>
			</td>
		</tr>
		<br>
		<br>

	</table>
	<br>
	<table border="0" cellspacing="0" width="100%" cellpadding="0"
		style="font-size: 12px">
		<tr>
			<th class="reportHeaderCell">Cantidad</th>
			<th class="reportHeaderCell" colspan="2">Nombre</th>
			<th class="reportHeaderCell" colspan="4">Descripcion</th>
			<th class="reportHeaderCellRight">Precio</th>
			<th class="reportHeaderCellRight">Inter�s</th>
			<th class="reportHeaderCellRight">Total</th>
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		<tr>
			<%
				double subTotal = 0, iva = 0;
						double subTotalGeneral = 0;
						BmoOrderCredit bmoOrderCredit = new BmoOrderCredit();
						PmOrderCredit pmOrderCredit = new PmOrderCredit(sFParams);
						BmFilter bmFilter = new BmFilter();
						bmFilter.setValueFilter(bmoOrderCredit.getKind(), bmoOrderCredit.getOrderId().getName(),
								bmoOrder.getId());
						int i = 1;
						Iterator<BmObject> quoteOrderCredit = pmOrderCredit.list(bmFilter).iterator();
						while (quoteOrderCredit.hasNext()) {
							bmoOrderCredit = (BmoOrderCredit) quoteOrderCredit.next();
			%>
		
		<tr>
			<td class="reportCellText"><%=bmoOrderCredit.getQuantity().toHtml()%>

			</td>
			<td class="reportCellText" colspan="2"><%=bmoOrderCredit.getName().toHtml()%>
			</td>
			<td class="reportCellText" colspan="4"><%=bmoOrderCredit.getDescription().toHtml()%>
			</td>
			<td class="reportCellEven	" align="right">$<%=bmoOrderCredit.getPrice().toHtml()%>
			</td>
			<td class="reportCellEven"  align="right">$<%=bmoOrderCredit.getInterest().toHtml()%>
			</td> 
			<td class="reportCellEven" align="right">$<%=bmoOrderCredit.getAmount().toHtml()%>
			</td>
		</tr>
		<%
			//} else {
		%>
		<%
			} //fin de groups
		%>
		<tr>
			<td colspan="8" class="">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="8" rowspan="4" valign="top" align="left"
				class="detailStart">
				<p class="documentComments">
					<b>Notas:</b> <br>
					<%=bmoOrder.getDescription().toHtml()%></p>
			</td>
			<td class="" align="right"><b>Subtotal:</b></td>
			<td class="reportCellEven" align="right"><b><%=formatCurrency.format(bmoOrder.getAmount().toDouble())%></td>
		</tr>
		<tr>
			<td class="" align="right">
				<%
					if (bmoOrder.getDiscount().toDouble() > 0) {
				%> <b>Descuento:</b> <%
 	}
 %>
			</td>
			<%
				if (bmoOrder.getDiscount().toDouble() > 0) {
			%>
			<td class="reportCellEven" align="right"><b><%=formatCurrency.format(bmoOrder.getDiscount().toDouble())%></b>
				<%
					} else {
				%>
			<td class="" align="right"></td>
			<%
				}
			%>
			</td>
		</tr>
		<tr>
			<td class="" align="right">
				<%
					if (bmoOrder.getTax().toDouble() > 0) {
				%> <b>IVA:</b> <%
 	}
 %>
			</td>
			<%
				if (bmoOrder.getTax().toDouble() > 0) {
			%>
			<td class="reportCellEven" align="right"><b><%=formatCurrency.format(bmoOrder.getTax().toDouble())%></b>
			</td>
			<%
				} else {
			%>
			<td class="" align="right"></td>
			<%
				}
			%>
		</tr>
		<tr>
			<td class="" align="right"><b>Total:</b></td>
			<td class="reportCellEven" align="right"><b><%=formatCurrency.format(bmoOrder.getTotal().toDouble())%></b>
			</td>
		</tr>
	</table>

	<%
		}

			//proximo inicio if2
			if (bmoOrderType.getType().equals("" + BmoOrderType.TYPE_SESSION)) {
				System.out.println("");
				BmoWFlowType bmoWFlowType = new BmoWFlowType();
				PmWFlowType pmWFlowTypeSale = new PmWFlowType(sFParams);
				bmoWFlowType = (BmoWFlowType) pmWFlowTypeSale.get(bmoOrder.getWFlowTypeId().toInteger());
				BmoSessionSale bmoSessionSale = new BmoSessionSale();
				PmPropertySale pmPropertySale = new PmPropertySale(sFParams);
				BmoUser bmoUser = new BmoUser();
				PmUser pmUser = new PmUser(sFParams);
				bmoUser = (BmoUser) pmUser.get(bmoOrder.getUserId().toInteger());
				String customer = "";
				if (bmoOrder.getBmoCustomer().getCustomertype().toChar() == BmoCustomer.TYPE_PERSON) {
					customer = bmoOrder.getBmoCustomer().getCode().toHtml() + " "
							+ bmoOrder.getBmoCustomer().getDisplayName().toHtml();
				} else {
					customer = bmoOrder.getBmoCustomer().getCode().toHtml() + " "
							+ bmoOrder.getBmoCustomer().getDisplayName().toHtml();
				}
				//con esta linea me traigo un id como una consulta sql pero de una clase

				bmoCompany = (BmoCompany) pmCompany.get(bmoOrder.getCompanyId().toInteger());
	%>
	<table border="0" cellspacing="0" width="100%" cellpadding="0"
		style="font-size: 12px">
		<tr>
			<td align="left" width="" rowspan="6" valign="top"><img
				border="0" width="<%=SFParams.LOGO_WIDTH%>"
				height="<%=SFParams.LOGO_HEIGHT%>" src="<%=logoURL%>"></td>
			<td colspan="4" class="reportTitleCell">Pedido: <%=bmoOrder.getCode().toHtml()%>
			</td>
		</tr>
		<tr>
			<th class="reportCellEven" align="left">Nombre:</th>
			<td class="reportCellEven" align="left"><%=bmoOrder.getName().toHtml()%>
			</td>
			<th class="reportCellEven" align="left">Tipo:</th>
			<td class="reportCellEven" align="left"><%=bmoWFlowType.getName().toHtml()%>
			</td>
		</tr>
		<tr>
			<th class="reportCellEven" align="left">Cliente:</th>
			<td class="reportCellEven" align="left"><%=customer%></td>
			<th class="reportCellEven" align="left">Empresa:</th>
			<td class="reportCellEven" align="left"><%=bmoCompany.getName().toHtml()%>
			</td>
		</tr>
		<tr>
			<th class="reportCellEven" align="left">Ejecutivo Comercial:</th>
			<td class="reportCellEven" align="left"><%=bmoUser.getFirstname().toHtml()%>
				<%=bmoUser.getFatherlastname().toHtml()%></td>
			<th class="reportCellEven" align="left">Fecha/Hora Pedido:</th>
			<td class="reportCellEven" align="left">
				<%
					if (!bmoOrder.getLockStart().toString().equals("")) {
				%> <%=SFServerUtil.formatDate(sFParams, sFParams.getDateTimeFormat(),
								sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString(),
								bmoOrder.getLockStart().toString())%>
				<%
					}
				%> <%
 	if (!bmoOrder.getLockEnd().toString().equals("")) {
 %> - <%=SFServerUtil.formatDate(sFParams, sFParams.getDateTimeFormat(),
								sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString(),
								bmoOrder.getLockEnd().toString())%>
				<%
					}
				%>
			</td>
		</tr>
		<tr>
			<th class="reportCellEven" align="left">Moneda:</th>
			<td class="reportCellEven" align="left"><%=bmoOrder.getBmoCurrency().getCode().toHtml()%>
				- <%=bmoOrder.getBmoCurrency().getName().toHtml()%></td>
			<th class="reportCellEven" align="left">Fecha Impresi&oacute;n:
			</th>
			<td class="reportCellEven" align="left"><%=SFServerUtil.nowToString(sFParams,
							sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString())%>
			</td>
		</tr>
		<tr>
			<th class="reportCellEven" align="left">Estatus Pedido:</th>
			<td class="reportCellEven" align="left"><%=bmoSessionSale.getStatus().getSelectedOption().getLabeltoHtml()%>
			</td>

		</tr>
	</table>
	<br>

	<br>
	<table border="0" cellspacing="0" width="100%" cellpadding="0"
		style="font-size: 12px">
		<tr>

			<td colspan="8" class="reportHeaderCellCenter">Paquete Sesiones
			</td>
		<tr>
			<th class="reportHeaderCell">Cant.</th>
			<th class="reportHeaderCellCenter">Nombre</th>
			<th class="reportHeaderCellCenter">Descripcion</th>
			<th class="reportHeaderCellRight">Precio</th>
			<th class="reportHeaderCellRight">Total</th>
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		<tr>


			<%
				PmConn pmConnSessionSales = new PmConn(sFParams);
						pmConnSessionSales.open();
						String sqlSession = "SELECT * FROM ordersessiontypepackages  WHERE (  ( orsp_orderid = "
								+ bmoOrder.getId() + "  )  )   ORDER BY orsp_ordersessiontypepackageid ASC ; ";
						pmConnSessionSales.doFetch(sqlSession);
						while (pmConnSessionSales.next()) {
			%>
		
		<tr>
			<td class="reportCellText">1</td>
			<td class="reportCellText"><%=HtmlUtil.stringToHtml(pmConnSessionSales.getString("orsp_name"))%>
			</td>
			<td class="reportCellText"><%=HtmlUtil.stringToHtml(pmConnSessionSales.getString("orsp_description"))%>
			</td>
			<td class="reportCellEven" align="right">$<%=HtmlUtil.stringToHtml(pmConnSessionSales.getString("orsp_amount"))%>
			</td>
			<td class="reportCellEven" align="right">$<%=HtmlUtil.stringToHtml(pmConnSessionSales.getString("orsp_amount"))%>
			</td>
		</tr>
	</table>
	<br>
	<br>
	<%
		}
	%>
	<!-- ----------------------------Segunda tabla---------------------------- -->
	<table border="0" cellspacing="0" width="100%" cellpadding="0"
		style="font-size: 12px">
		<tr>
			<td colspan="8" class="reportHeaderCellCenter">Grupo de Items:
				Items</td>
		<tr>
			<th class="reportHeaderCell">Cant.</th>
			<th class="reportHeaderCell">Clave</th>
			<th class="reportHeaderCellCenter">Nombre</th>
			<th class="reportHeaderCellCenter">Descripcion</th>
			<th class="reportHeaderCellCenter">Tipo</th>
			<th class="reportHeaderCellCenter">Modelo</th>
			<th class="reportHeaderCellRight">Precio</th>
			<th class="reportHeaderCellRight">Total</th>
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		<tr>
			<%
				BmoOrderItem bmoOrderItem = new BmoOrderItem();
						PmOrderItem pmOrderItem = new PmOrderItem(sFParams);

						PmConn pmConnOrderItem = new PmConn(sFParams);
						pmConnOrderItem.open();

						String sqlItem = "select ordi_name,ordi_description,ordi_price,prod_code,prod_model,prod_type,ordi_amount from products left join orderitems on (ordi_productid = prod_productid) left join ordergroups on (ordg_ordergroupid = ordi_ordergroupid)"
								+ "left join orders on (orde_orderid=ordg_orderid) where (orde_orderid=" + bmoOrder.getId()
								+ "); ";
						pmConnOrderItem.doFetch(sqlItem);
						while (pmConnOrderItem.next()) {
			%>
		
		<tr>
			<td class="reportCellText"><%=bmoOrderItem.getQuantity()%></td>
			<td class="reportCellText"><%=HtmlUtil.stringToHtml(pmConnOrderItem.getString("prod_code"))%></td>

			<td class="reportCellText" align="right"><%=HtmlUtil.stringToHtml(pmConnOrderItem.getString("ordi_name"))%></td>
			<td class="reportCellText"><%=HtmlUtil.stringToHtml(pmConnOrderItem.getString("ordi_description"))%>
			</td>
			<td class="reportCellText">
				<%
					BmoProduct bmoProduct1 = new BmoProduct();
								bmoProduct1.getType().setValue(pmConnOrderItem.getString("prod_type"));
				%> <%=HtmlUtil.stringToHtml(bmoProduct1.getType().getSelectedOption().getLabel())%>

			</td>
			<td class="reportCellText"><%=HtmlUtil.stringToHtml(pmConnOrderItem.getString("prod_model"))%>
			</td>
			<td class="reportCellEven" align="right">$<%=HtmlUtil.stringToHtml(pmConnOrderItem.getString("ordi_price"))%>
			</td>
			<td class="reportCellEven" align="right">$<%=HtmlUtil.stringToHtml(pmConnOrderItem.getString("ordi_amount"))%>
			</td>
		</tr>
		<%
			}
		%>
	</table>

	<!-- ---------------------------------TERCER TABLA------------------------------- -->


	<table border="0" cellspacing="0" width="100%" cellpadding="0"
		style="font-size: 12px">
		<br>
		<tr>
			<td colspan="8" class="reportHeaderCellCenter">Extras</td>
		</tr>
		<td>
		<tr>
			<th class="reportHeaderCell">Cant.</th>
			<th class="reportHeaderCell" colspan="2">Extra</th>
			<th class="reportHeaderCellCenter">Comentarios</th>
			<th class="reportHeaderCellRight">Precio</th>
			<th class="reportHeaderCellRight">Total</th>

		</tr>

		<tr>
			<%
				//CODIGO tercer tabla
						PmConn pmOrderSessionExtra = new PmConn(sFParams);
						pmOrderSessionExtra.open();
						String sqlExtras = "select * from ordersessionextras left join orders on (orde_orderid = orsx_orderid)"
								+ "left join sessiontypeextras on(setx_sessiontypeextraid=orsx_sessiontypeextraid)"
								+ "where (orde_orderid=" + bmoOrder.getId() + "); ";
						pmOrderSessionExtra.doFetch(sqlExtras);
						while (pmOrderSessionExtra.next()) {
			%>
		
		<tr>
			<td class="reportCellText"><%=HtmlUtil.stringToHtml(pmOrderSessionExtra.getString("orsx_quantity"))%>
			</td>
			<td class="reportCellText" colspan="2"><%=HtmlUtil.stringToHtml(pmOrderSessionExtra.getString("setx_name"))%>
			</td>
			<td class="reportCellText" align="right"><%=HtmlUtil.stringToHtml(pmOrderSessionExtra.getString("orsx_comments"))%>
			</td>
			<td class="reportCellEven" align="right">$<%=HtmlUtil.stringToHtml(pmOrderSessionExtra.getString("orsx_price"))%>
			</td>
			<td class="reportCellEven" align="right">$<%=HtmlUtil.stringToHtml(pmOrderSessionExtra.getString("orsx_amount"))%>
				<br>

			</td>
		</tr>


		<%
			}
					pmOrderSessionExtra.close();
					pmConnSessionSales.close();
					pmConnOrderItem.close();
		%><br>

		<tr>
		
			<td colspan="4" rowspan="4" valign="top" align="left"
				class="detailStart" >
				<br>
				<p class="documentComments">
					<b>Notas:</b> 
					<%=bmoOrder.getDescription().toString()%>
					<br>
					<%=bmoOrder.getDescription().toHtml()%>
				</p>
			</td>
			<th class="" align="right">Subtotal:</th>
			<td class="reportCellEven" align="right"><b><%=bmoOrder.getAmount().toHtml()%></b>
			</td>
		</tr>
		<tr>
			<th class="" align="right">
				<%
					if (bmoOrder.getDiscount().toDouble() > 0) {
				%> Descuento: <%
					}
				%>
			</th>
			<%
				if (bmoOrder.getDiscount().toDouble() > 0) {
			%>
			<td class="reportCellEven" align="right"><b><%=formatCurrency.format(bmoOrder.getDiscount().toDouble())%></b>
			</td>
			<%
				} else {
			%>
			<td class="" align="right"></td>
			<%
				}
			%>
		</tr>
		<tr>
			<th class="" align="right">
				<%
					if (bmoOrder.getTax().toDouble() > 0) {
				%> IVA: <%
					}
				%>
			</th>
			<%
				if (bmoOrder.getTax().toDouble() > 0) {
			%>
			<td class="reportCellEven" align="right"><b><%=formatCurrency.format(bmoOrder.getTax().toDouble())%></b>
			</td>
			<%
				} else {
			%>
			<td class="" align="right"></td>
			<%
				}
			%>
		</tr>
		<tr>
			<th class="" align="right">Total:</th>
			<td class="reportCellEven" align="right"><b><%=bmoOrder.getTotal().toHtml()%></b>
			</td>
		</tr>
	</table>

	<%
		}
			//fin if tabla flotis

			//inicio INADICO
			if (bmoOrderType.getType().equals("" + BmoOrderType.TYPE_LEASE)) {
				System.out.println("");
				BmoWFlowType bmoWFlowType = new BmoWFlowType();
				PmWFlowType pmWFlowTypeSale = new PmWFlowType(sFParams);
				bmoWFlowType = (BmoWFlowType) pmWFlowTypeSale.get(bmoOrder.getWFlowTypeId().toInteger());
				BmoSessionSale bmoSessionSale = new BmoSessionSale();
				PmPropertySale pmPropertySale = new PmPropertySale(sFParams);
				BmoUser bmoUser = new BmoUser();
				PmUser pmUser = new PmUser(sFParams);
				bmoUser = (BmoUser) pmUser.get(bmoOrder.getUserId().toInteger());
				String customer = "";
				if (bmoOrder.getBmoCustomer().getCustomertype().toChar() == BmoCustomer.TYPE_PERSON) {
					customer = bmoOrder.getBmoCustomer().getCode().toHtml() + " "
							+ bmoOrder.getBmoCustomer().getDisplayName().toHtml();
				} else {
					customer = bmoOrder.getBmoCustomer().getCode().toHtml() + " "
							+ bmoOrder.getBmoCustomer().getDisplayName().toHtml();
				}
				//con esta linea me traigo un id como una consulta sql pero de una clase

				bmoCompany = (BmoCompany) pmCompany.get(bmoOrder.getCompanyId().toInteger());
	%>
	<table border="0" cellspacing="0" width="100%" cellpadding="0"
		style="font-size: 12px">
		<tr>
			<td align="left" width="" rowspan="6" valign="top"><img
				border="0" width="<%=SFParams.LOGO_WIDTH%>"
				height="<%=SFParams.LOGO_HEIGHT%>" src="<%=logoURL%>"></td>
			<td colspan="4" class="reportTitleCell">Pedido: <%=bmoOrder.getCode().toHtml()%>
			</td>
		</tr>
		<tr>
			<th class="reportCellEven" align="left">Nombre:</th>
			<td class="reportCellEven" align="left"><%=bmoOrder.getName().toHtml()%>
			</td>
			<th class="reportCellEven" align="left">Tipo:</th>
			<td class="reportCellEven" align="left"><%=bmoWFlowType.getName().toHtml()%>
			</td>
		</tr>
		<tr>
			<th class="reportCellEven" align="left">Cliente:</th>
			<td class="reportCellEven" align="left"><%=customer%></td>
			<th class="reportCellEven" align="left">Empresa:</th>
			<td class="reportCellEven" align="left"><%=bmoCompany.getName().toHtml()%>
			</td>
		</tr>
		<tr>
			<th class="reportCellEven" align="left">Ejecutivo Comercial:</th>
			<td class="reportCellEven" align="left"><%=bmoUser.getFirstname().toHtml()%>
				<%=bmoUser.getFatherlastname().toHtml()%></td>
			<th class="reportCellEven" align="left">Fecha Inicio/Hora Pedido:</th>
			
			<td class="reportCellEven" align="left">
				<%
					if (!bmoOrder.getLockStart().toString().equals("")) {
				%> <%=SFServerUtil.formatDate(sFParams, sFParams.getDateTimeFormat(),
								sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString(),
								bmoOrder.getLockStart().toString())%>
				<%
					}
				%> 
				
				
			</td>
		</tr>
		<tr>
			<th class="reportCellEven" align="left">Moneda:</th>
			<td class="reportCellEven" align="left"><%=bmoOrder.getBmoCurrency().getCode().toHtml()%>
				- <%=bmoOrder.getBmoCurrency().getName().toHtml()%></td>
				<th class="reportCellEven" align="left">Fecha Fin/Hora Pedido:</th>
				<td class="reportCellEven" align="left">
				 <%=SFServerUtil.formatDate(sFParams, sFParams.getDateTimeFormat(),
								sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString(),
								bmoOrder.getLockEnd().toString())%>
				
			
		</tr>
		<tr>
			<th class="reportCellEven" align="left">Estatus Pedido:</th>
			<td class="reportCellEven" align="left"><%=bmoSessionSale.getStatus().getSelectedOption().getLabeltoHtml()%>
			</td>
			<th class="reportCellEven" align="left">Fecha Impresi&oacute;n:
			</th>
			<td class="reportCellEven" align="left"><%=SFServerUtil.nowToString(sFParams,
							sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString())%>
			</td>	
		</tr>
	</table>
	<br>
	<!-- PRIMER TABLA------- -->
	<table border="0" cellspacing="0" width="100%" cellpadding="0"
		style="font-size: 12px">
		<tr>

			<td colspan="8" class="reportHeaderCellCenter">Arrendamiento</td>
		<tr>
			<th class="reportHeaderCell">Cant.</th>
			<th class="reportHeaderCell">Descripci�n</th>
			<th class="reportHeaderCell">Calle y N�mero</th>
			<th class="reportHeaderCellRight">Precio</th>
			<th class="reportHeaderCellRight">Total</th>
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		<tr>


			<%
				PmConn pmConnProperty = new PmConn(sFParams);
						pmConnProperty.open();
						String sqlProperty = "select * from orderpropertiestax left join properties on (orpt_propertyid = prty_propertyid)"
								+ "inner join orders on(orde_orderid = orpt_orderid) where (orde_orderid="
								+ bmoOrder.getId() + ");";
						pmConnProperty.doFetch(sqlProperty);
						while (pmConnProperty.next()) {
			%>
		
		<tr>
			<td class="reportCellText"><%=HtmlUtil.stringToHtml(pmConnProperty.getString("prty_code"))%>

			</td>
			<td class="reportCellText"><%=HtmlUtil.stringToHtml(pmConnProperty.getString("prty_description"))%>
			</td>
			<td class="reportCellText"><%=HtmlUtil.stringToHtml(pmConnProperty.getString("prty_street"))%>
				#<%=HtmlUtil.stringToHtml(pmConnProperty.getString("prty_number"))%>

			</td>
			<td class="reportCellEven" align="right">$<%=HtmlUtil.stringToHtml(pmConnProperty.getString("orpt_price"))%>
			</td>
			<td class="reportCellEven" align="right">$<%=HtmlUtil.stringToHtml(pmConnProperty.getString("orpt_amount"))%>
			</td>
		</tr>
	</table>
	<%
		}
	%>


	<br>
	<br>

	<!-- Segunda tabla inadico -->


	<table border="0" cellspacing="0" width="100%" cellpadding="0"
		style="font-size: 12px">
		<tr>

			<td colspan="8" class="reportHeaderCellCenter">Extras</td>
		<tr>
			<th class="reportHeaderCell">Cant.</th>
			<th class="reportHeaderCell" colspan="2">Extra</th>
			<th class="reportHeaderCell">Coment.</th>
			<th class="reportHeaderCellRight">Precio</th>
			<th class="reportHeaderCellRight">Total</th>
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		<tr>


			<%
				PmConn pmConnPropertyExtra = new PmConn(sFParams);
						BmoPropertyModelExtra bmoPropertyModelExtra = new BmoPropertyModelExtra();
						BmObject bmObject;
						pmConnPropertyExtra.open();
						String sqlPropertyExtra = "select * from propertymodelextras left join orderpropertymodelextras "
								+ "on(orpx_propertymodelextraid= prmx_propertymodelextraid ) left join orders on(orde_orderid = orpx_orderid)"
								+ "where orde_orderid=" + bmoOrder.getId() + "";
						pmConnPropertyExtra.doFetch(sqlPropertyExtra);
						while (pmConnPropertyExtra.next()) {
			%>
		
		<tr>
			<td class="reportCellText"><%=HtmlUtil.stringToHtml(pmConnPropertyExtra.getString("orpx_quantity"))%>

			</td>
			<td class="reportCellText" colspan="2"><%=HtmlUtil.stringToHtml(pmConnPropertyExtra.getString("prmx_name"))%>

			</td>
			<td class="reportCellText"><%=HtmlUtil.stringToHtml(pmConnPropertyExtra.getString("orpx_comments"))%>
			</td>
			<td class="reportCellEven" align="right">$<%=HtmlUtil.stringToHtml(pmConnPropertyExtra.getString("orpx_price"))%>
			</td>
			<td class="reportCellEven" align="right">$<%=HtmlUtil.stringToHtml(pmConnPropertyExtra.getString("orpx_amount"))%>
			</td>
		</tr>
		<tr>
			
			
			<%} %>
			   
			<td colspan="4" rowspan="4"    align="left"
				class="detailStart">
				<p class="documentComments">			
				
					<b>Notas:</b>
					<%=bmoOrder.getDescription().toString()%>
					<br>
					<%=bmoOrder.getDescription().toHtml()%>
				</p>
			</td>
			<th class="" align="right">Subtotal:</th>
			<td class="reportCellEven" align="right"><b><%=bmoOrder.getAmount().toHtml()%></b>
			</td>
		</tr>
		<tr>
			<th class="" align="right">
				<%
					if (bmoOrder.getDiscount().toDouble() > 0) {
				%> Descuento: <%
					}
				%>
			</th>
			<%
				if (bmoOrder.getDiscount().toDouble() > 0) {
			%>
			<td class="reportCellEven" align="right"><b><%=formatCurrency.format(bmoOrder.getDiscount().toDouble())%></b>
			</td>
			<%
				} else {
			%>
			<td class="" align="right"></td>
			<%
				}
			%>
		</tr>
		<tr>
			<th class="" align="right">
				<%
					if (bmoOrder.getTax().toDouble() > 0) {
				%> IVA: <%
					}
				%>
			</th>
			<%
				if (bmoOrder.getTax().toDouble() > 0) {
			%>
			<td class="reportCellEven" align="right"><b><%=formatCurrency.format(bmoOrder.getTax().toDouble())%></b>
			</td>
			<%
				} else {
			%>
			<td class="" align="right"></td>
			<%
				}
			%>
		</tr>
		<tr>
			<th class="" align="right">Total:</th>
			<td class="reportCellEven" align="right"><b><%=bmoOrder.getTotal().toHtml()%></b>
			</td>
		</tr>
	</table>
	</table>
	<%
		pmConnPropertyExtra.close();
		pmConnProperty.close();
	%>
	<%
		}
			
		} catch (Exception e) {
			String errorLabel = "Error de Pedido";
			String errorText = "El Pedido no puede ser desplegado.";
			String errorException = e.toString();

			//response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);
	%>

	<%=errorException%>

	<%
		}
	%>
</body>
</html>
