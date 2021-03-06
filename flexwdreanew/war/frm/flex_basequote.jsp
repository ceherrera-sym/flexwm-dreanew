<%@page import="com.flexwm.server.ev.PmVenue"%>
<%@page import="com.flexwm.shared.ev.BmoVenue"%>
<%@page import="com.flexwm.server.cm.PmCustomerAddress"%>
<%@page import="com.flexwm.shared.cm.BmoCustomerAddress"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.server.cm.PmQuoteGroup"%>
<%@page import="com.flexwm.shared.cm.BmoQuoteGroup"%>
<%@page import="com.flexwm.shared.cm.BmoQuoteItem"%>
<%@page import="com.flexwm.shared.cm.BmoQuoteEquipment"%>
<%@page import="com.flexwm.shared.cm.BmoQuoteStaff"%>
<%@page import="com.flexwm.server.cm.PmQuoteItem"%>
<%@page import="com.flexwm.server.cm.PmQuoteEquipment"%>
<%@page import="com.flexwm.server.cm.PmQuoteStaff"%>
<%@page import="com.flexwm.server.cm.PmQuote"%>
<%@page import="com.flexwm.shared.cm.BmoQuote"%>
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="com.flexwm.shared.cm.BmoOpportunity"%>
<%@page import="com.flexwm.server.cm.PmOpportunity"%>
<%@page import="com.flexwm.shared.cm.BmoOpportunityDetail"%>
<%@page import="com.flexwm.server.cm.PmOpportunityDetail"%>
<%@page import="com.flexwm.shared.BmoFlexConfig"%>
<%@page import="com.flexwm.server.op.PmOrderType"%>
<%@page import="com.flexwm.shared.op.BmoOrderType"%>
<%@page import="com.flexwm.shared.wf.BmoWFlowType"%>
<%@page import="com.flexwm.server.wf.PmWFlowType"%>
<%@page import="com.flexwm.server.cm.PmQuoteProperty"%>
<%@page import="com.flexwm.shared.cm.BmoQuoteProperty"%>
<%@page import="com.flexwm.shared.co.BmoDevelopmentBlock"%>
<%@page import="com.flexwm.server.cm.PmQuotePropertyModelExtra"%>
<%@page import="com.flexwm.shared.cm.BmoQuotePropertyModelExtra"%>
<%@page import="com.symgae.server.sf.PmCompany"%>
<%@page import="com.symgae.shared.sf.BmoCompany"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@include file="../inc/login_opt.jsp"%>

<%
	try {
		String title = "";

		BmoQuote bmoQuote = new BmoQuote();

		BmoProgram bmoProgram = new BmoProgram();
		PmProgram pmProgram = new PmProgram(sFParams);
		bmoProgram = (BmoProgram) sFParams.getBmoProgram(bmoQuote.getProgramCode());

		BmoQuoteProperty bmoQuoteProperty = new BmoQuoteProperty();
		BmoDevelopmentBlock bmoDevelopmentBlock = new BmoDevelopmentBlock();
		BmoQuotePropertyModelExtra bmoQuotePropertyModelExtra = new BmoQuotePropertyModelExtra();
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
<title>:::Cotizaci&oacute;n:::</title>
	    <link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>">
<meta charset=utf-8>
</head>
<body class="default" <%=permissionPrint%>>
	<%
		NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(Locale.US);

			// Obtener parametros
			int opportunityId = 0;
			if (isExternal)
				opportunityId = decryptId;
			else
				opportunityId = Integer.parseInt(request.getParameter("foreignId"));

			BmoOpportunity bmoOpportunity = new BmoOpportunity();
			PmOpportunity pmOpportunity = new PmOpportunity(sFParams);
			bmoOpportunity = (BmoOpportunity) pmOpportunity.get(opportunityId);

			PmQuote pmQuote = new PmQuote(sFParams);
			bmoQuote = (BmoQuote) pmQuote.get(bmoOpportunity.getQuoteId().toInteger());

			BmoOrderType bmoOrderType = new BmoOrderType();
			PmOrderType pmOrderType = new PmOrderType(sFParams);
			bmoOrderType = (BmoOrderType) pmOrderType.get(bmoQuote.getOrderTypeId().toInteger());

			BmoWFlowType bmoOpportunityWFlowType = new BmoWFlowType();
			PmWFlowType pmWFlowTypeOppo = new PmWFlowType(sFParams);
			if (bmoOpportunity.getForeignWFlowTypeId().toInteger() > 0)
				bmoOpportunityWFlowType = (BmoWFlowType) pmWFlowTypeOppo
						.get(bmoOpportunity.getForeignWFlowTypeId().toInteger());

			PmConn pmConn = new PmConn(sFParams);

			// Empresa
			BmoCompany bmoCompany = new BmoCompany();
			PmCompany pmCompany = new PmCompany(sFParams);
			bmoCompany = (BmoCompany) pmCompany.get(bmoQuote.getCompanyId().toInteger());

			// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
			String logoURL = "";
			if (!bmoCompany.getLogo().toString().equals(""))
				logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
			else
				logoURL = sFParams.getMainImageUrl();

			BmoCity bmoCompanyCity = new BmoCity();
			PmCity pmCity = new PmCity(sFParams);
			if (bmoCompany.getCityId().toInteger() > 0)
				bmoCompanyCity = (BmoCity) pmCity.get(bmoCompany.getCityId().toInteger());

			if (bmoQuote.getStatus().toChar() != BmoQuote.STATUS_AUTHORIZED)
				throw new Exception("La Cotizacion no esta autorizada - no se puede desplegar.");

			if (bmoOrderType.getType().equals("" + BmoOrderType.TYPE_RENTAL)
					|| bmoOrderType.getType().equals("" + BmoOrderType.TYPE_SALE)
					|| bmoOrderType.getType().equals("" + BmoOrderType.TYPE_SERVICE)
					|| bmoOrderType.getType().equals("" + BmoOrderType.TYPE_CONSULTANCY)) {

				BmoOpportunityDetail bmoOpportunityDetail = new BmoOpportunityDetail();
				PmOpportunityDetail pmOpportunityDetail = new PmOpportunityDetail(sFParams);
				bmoOpportunityDetail = (BmoOpportunityDetail) pmOpportunityDetail.getBy(bmoOpportunity.getId(), bmoOpportunityDetail.getOpportunityId().getName());

				String customer = "";
				if (bmoOpportunity.getBmoCustomer().getCustomertype().toChar() == BmoCustomer.TYPE_PERSON) {

					customer = bmoOpportunity.getBmoCustomer().getCode().toHtml() + " "
							+ bmoOpportunity.getBmoCustomer().getFirstname().toHtml() + " "
							+ bmoOpportunity.getBmoCustomer().getFatherlastname().toHtml() + " "
							+ bmoOpportunity.getBmoCustomer().getMotherlastname().toHtml();
				} else {
					customer = bmoOpportunity.getBmoCustomer().getCode().toHtml() + " "
							+ bmoOpportunity.getBmoCustomer().getLegalname().toHtml();
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
				if (bmoOpportunity.getVenueId().toInteger() > 0) {
					bmoVenue = (BmoVenue)pmVenue.get(bmoOpportunity.getVenueId().toInteger());
	
					if (bmoVenue.getHomeAddress().toInteger() > 0) {
						venue = bmoVenue.getName().toHtml() + ": " + bmoOpportunity.getCustomField4().toString();
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
// 				else if (bmoOpportunity.getCustomerAddressId().toInteger() > 0) {
// 					bmoCustomerAddress = (BmoCustomerAddress)pmCustomerAddress.get(bmoOpportunity.getCustomerAddressId().toInteger());
					
// 					venue = bmoCustomerAddress.getStreet().toHtml() + ": " +
// 							bmoCustomerAddress.getNumber().toHtml() + " " +
// 							bmoCustomerAddress.getNeighborhood().toHtml() + ". ";
							
// 					venueDir = bmoCustomerAddress.getBmoCity().getName().toHtml() + ", " +
// 							bmoCustomerAddress.getBmoCity().getBmoState().getCode().toHtml() + ", " +
// 							bmoCustomerAddress.getBmoCity().getBmoState().getBmoCountry().getCode().toHtml() + ".";
// 				}	
				
	%>
	<table border="0" cellspacing="0" width="100%" cellpadding="0"
		style="font-size: 12px">
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
	<table border="0" cellspacing="0" width="100%" cellpadding="0"
		style="font-size: 12px">
		<tr>
			<td class="reportHeaderCell" colspan="4">Cotizaci&oacute;n</td>
		</tr>
		<tr>
			<th align="left" class="reportCellEven">Empresa/Cliente:</th>
			<td class="reportCellEven"><%=customer.toString()%></td>
			<th align="left" class="reportCellEven">Fecha/Hora:</th>
			<td class="reportCellEven">
				<%
					if (!bmoOpportunity.getStartDate().toString().equals("")) {
					%> de: <%=SFServerUtil.formatDate(sFParams, sFParams.getDateTimeFormat(), sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString(),bmoOpportunity.getStartDate().toString())%> <%
 					}
 %> <%
				 	if (!bmoOpportunity.getEndDate().toString().equals("")) {
				 %> &nbsp;a:&nbsp;<%=SFServerUtil.formatDate(sFParams, sFParams.getDateTimeFormat(), sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString(), bmoOpportunity.getEndDate().toString())%> <%
				 	}
 %>
			</td>
		</tr>
		<tr>
			<th align="left" class="reportCellEven">Nombre:</th>
			<td class="reportCellEven"><%=bmoOpportunity.getCode().toHtml() + " - " + bmoOpportunity.getName().toHtml()%>
			</td>
			<th align="left" class="reportCellEven">Tipo:</th>
			<td class="reportCellEven"><%=bmoOpportunityWFlowType.getName().toHtml()%>
			</td>
		</tr>

		<%
			// Si es de renta, mostrar di�as
					if (bmoQuote.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
		%>
		<tr>
			<th class="reportCellEven" align="left">Lugar:</th>
			<td class="reportCellEven"><%= venue%></td>
			<th class="reportCellEven" align="left">Ciudad:</th>
			<td class="reportCellEven"><%= venueDir%></td>
		</tr>
		<%
			}
		%>
		<tr>
			<th class="reportCellEven" align="left">Ejecutivo Comercial:</th>
			<td class="reportCellEven"><%=bmoOpportunity.getBmoUser().getFirstname().toHtml()%>
				<%=bmoOpportunity.getBmoUser().getFatherlastname().toHtml()%> <%=bmoOpportunity.getBmoUser().getMotherlastname().toHtml()%>
			</td>
			<th class="reportCellEven" align="left">Fecha Cotizaci&oacute;n:
			</th>
			<td class="reportCellEven"><%=SFServerUtil.nowToString(sFParams,
							sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString())%>
			</td>
		</tr>
		<tr>
			<th class="reportCellEven">&nbsp;</th>
			<td class="reportCellEven">&nbsp;</td>
			<th class="reportCellEven" align="left">Estatus:</th>
			<td class="reportCellEven"><%=bmoQuote.getStatus().getSelectedOption().getLabeltoHtml()%>
				(#<%=bmoQuote.getAuthNum().toString()%>)</td>
		</tr>
	</table>
	<br>
	<table border="0" cellspacing="0" width="100%" cellpadding="0"
		style="font-size: 12px">
		<tr>
			<td class="reportHeaderCell" align="left" width="50%" colspan="3">Rubro</td>
			<td class="reportHeaderCellCenter" width="10%">Cantidad</td>
			<%
				if (bmoQuote.getBmoOrderType().getType().equals("" + BmoOrderType.TYPE_RENTAL)) {
			%>
			<td class="reportHeaderCellCenter" width="10%">D&iacute;as</td>
			<%
				} else {
			%>
			<td class="reportHeaderCell" align="left" width="10%">&nbsp;</td>
			<%
				}
			%>
			<td class="reportHeaderCellRight" width="10%">Precio&nbsp;&nbsp;&nbsp;</td>
			<td class="reportHeaderCellRight" width="10%">Subtotal&nbsp;&nbsp;&nbsp;</td>
			<td class="reportHeaderCellRight" width="10%" colspan="">Total&nbsp;&nbsp;</td>
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		<tr>
			<%
				String sql = "";
						double subTotal = 0, iva = 0;
						double subTotalGeneral = 0;
						BmoQuoteGroup bmoQuoteGroup = new BmoQuoteGroup();
						PmQuoteGroup pmQuoteGroup = new PmQuoteGroup(sFParams);
						BmFilter bmFilter = new BmFilter();
						bmFilter.setValueFilter(bmoQuoteGroup.getKind(), bmoQuoteGroup.getQuoteId().getName(), bmoQuote.getId());
						Iterator<BmObject> quoteGroups = pmQuoteGroup.list(bmFilter).iterator();
						int i = 1;
						while (quoteGroups.hasNext()) {
							bmoQuoteGroup = (BmoQuoteGroup) quoteGroups.next();
			%>
		
		<tr>
			<td class="reportHeaderCell" colspan="7" style="white-space: normal">
				<%=i++%>. <%=bmoQuoteGroup.getName().toHtml()%> <%
 	if (bmoQuoteGroup.getDescription().toString().length() > 0) {
 %> <br><%=bmoQuoteGroup.getDescription().toHtml()%> <%
 	}
 %>
			</td>
			<td class="reportHeaderCellRight" colspan="">
				<%
					if (bmoQuoteGroup.getShowAmount().toBoolean()) {
				%> <%=formatCurrency.format(bmoQuoteGroup.getAmount().toDouble())%>&nbsp;
				<%
					}
				%>
			</td>
		</tr>
		<%
			// Si es Kit solo pone descripcion
						//if (bmoQuoteGroup.getIsKit().toBoolean()) {
		%><!--
	             		<tr>
	             			<td class="reportCellEven" colspan="7">
	                            &nbsp;<%//= bmoQuoteGroup.getDescription().toHtml()%>
	                        </td>
		                  <td align ="right">
		                       &nbsp;
							<%// if (bmoQuoteGroup.getShowGroupImage().toBoolean() && (bmoQuoteGroup.getImage().toString().length() > 0)) { 
						//String blobKeyParse = bmoQuoteGroup.getImage().toString();
						//if (bmoQuoteGroup.getImage().toString().indexOf(".") > 0)
						//blobKeyParse = bmoQuoteGroup.getImage().toString().substring(0, bmoQuoteGroup.getImage().toString().indexOf("."));%> 
		                    	<img src="/fileserve?blob-key=<%//= blobKeyParse%>" width="300" height="200" padding="4"> 
		                  	<%// }%> 
		                  </td> 
						</tr>-->
		<%
			//} else {

						BmoQuoteItem bmoQuoteItem = new BmoQuoteItem();
						PmQuoteItem pmQuoteItem = new PmQuoteItem(sFParams);
						BmFilter bmFilterQ = new BmFilter();
						bmFilterQ.setValueFilter(bmoQuoteItem.getKind(), bmoQuoteItem.getQuoteGroupId().getName(),
								bmoQuoteGroup.getId());
						ArrayList<BmObject> list = pmQuoteItem.list(bmFilterQ);
						Iterator<BmObject> items = list.iterator();
						int index = 1, count = list.size();
						while (items.hasNext()) {
							bmoQuoteItem = (BmoQuoteItem) items.next();
		%>
		<!--
		                 <tr>
			                 <%//if (index == 1) {%>
								<td align ="center" colspan="8" rowspan="<%//= count%>" style="white-space: nowrap;">&nbsp;
									<%//if (bmoQuoteGroup.getShowGroupImage().toBoolean() && 
							//(bmoQuoteGroup.getImage().toString().length() > 0)) { 
							//String blobKeyParse = bmoQuoteGroup.getImage().toString();
							//if (bmoQuoteGroup.getImage().toString().indexOf(".") > 0)
							//blobKeyParse = bmoQuoteGroup.getImage().toString().substring(0, bmoQuoteGroup.getImage().toString().indexOf("."));%> 
		                    			<img src="/fileserve?blob-key=<%//= blobKeyParse%>" width="300" height="200" padding="4"> 
		                  			<%// }%> 
								</td>
							<%//}%>   
							</tr>-->
		<tr>
			<td class="reportCellEven" align="left" colspan="3">
				<%
					if (bmoQuote.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)) {
						if (bmoQuoteItem.getProductId().toInteger() > 0 ) { 
						 	if (bmoQuoteItem.getBmoProduct().getDisplayName().toString().equals("")) {
						 %> <%=bmoQuoteItem.getBmoProduct().getName().toHtml()%> <%
						 	} else {
						 %> <%=bmoQuoteItem.getBmoProduct().getDisplayName().toHtml()%> <%
						 	}
					 	} else {
					 %> <%=bmoQuoteItem.getName().toHtml()%> <%
					 	}
					 %> <%
				 	} else {
						 	if(bmoQuoteItem.getBmoProduct().getDisplayName().toString().equals("")){ %>
	                  			<%= bmoQuoteItem.getName().toHtml() %>
	                  			<%= bmoQuoteItem.getBmoProduct().getBrand().toHtml() %>
	                  			<%= bmoQuoteItem.getBmoProduct().getModel().toHtml() %>
            			<% 	} else{ %>
								<%= bmoQuoteItem.getBmoProduct().getDisplayName().toHtml() %>
								<%= bmoQuoteItem.getBmoProduct().getBrand().toHtml() %>
	                    		<%= bmoQuoteItem.getBmoProduct().getModel().toHtml() %>
						<% 	} 
				 	} %>
 	<br> <span class="documentSubText"><%=bmoQuoteItem.getDescription().toHtml()%></>


			
			</td>
			<td class="reportCellEven" align="center">
				<%
					if (bmoQuoteGroup.getShowQuantity().toBoolean()) {
				%> <%=bmoQuoteItem.getQuantity().toDouble()%> <%
 					}
 %>
			</td>
			<td class="reportCellEven" align="center">
				<%
					if (bmoQuote.getBmoOrderType().getType().equals("" + BmoOrderType.TYPE_RENTAL)) {
				%> <%
					 	if (bmoQuoteGroup.getShowQuantity().toBoolean()) {
					 %> <%=bmoQuoteItem.getDays().toDouble()%> <%
					 	}
					 %> <%
 	}
 %>
			</td>
			<td class="reportCellEven" align="right">
				<%
					if (bmoQuoteGroup.getShowPrice().toBoolean()) {
				%> <%=formatCurrency.format(bmoQuoteItem.getPrice().toDouble())%> <%
 					}
 %>
			</td>
			<td class="reportCellEven" align="right">
				<%
					if (bmoQuoteGroup.getShowPrice().toBoolean()) {
				%> <%=formatCurrency.format(bmoQuoteItem.getAmount().toDouble())%> <%
 					}
 %>
			</td>

			<%
				//	                         if (!bmoQuoteGroup.getIsKit().toBoolean()) {
								//		                         if (bmoQuoteGroup.getShowProductImage().toBoolean() && !(bmoQuoteItem.getBmoProduct().getImage().toString().length() > 0)
								//                 						&& !bmoQuoteGroup.getShowGroupImage().toBoolean()
								//                 				){
			%>
			<!--<td class="reportCellEven" align="center">&nbsp;</td>-->

			<%
				//	                         } }

								// Si es Kit
								if (bmoQuoteGroup.getIsKit().toBoolean()) {
			%>
			<%
				if (index == 1) {
			%>
			<td class="reportCellEven" align="center" colspan="8"
				rowspan="<%=count%>" style="white-space: nowrap;" valign="center">&nbsp;
				<%
					if (bmoQuoteGroup.getShowGroupImage().toBoolean()
													&& (bmoQuoteGroup.getImage().toString().length() > 0)) {
						String blobKeyParse = HtmlUtil.parseImageLink(sFParams,  bmoQuoteGroup.getImage());
												
				%> <img src="<%=blobKeyParse%>" width="300"
				height="200"> <%
 	}
 %>
			</td>
			<%
			}
				} else {
					//Si estan seleccionados Mostrar-imgGrupo e imgProd, le da prioridad a la imgGrupo por mostrar y si selecciona solo imgGrupo solamente

					if ((bmoQuoteGroup.getShowGroupImage().toBoolean()
							&& (bmoQuoteGroup.getImage().toString().length() > 0)
							&& bmoQuoteGroup.getShowProductImage().toBoolean()
					//&& (bmoQuoteItem.getBmoProduct().getImage().toString().length() > 0)
					) || (bmoQuoteGroup.getShowGroupImage().toBoolean()
							&& (bmoQuoteGroup.getImage().toString().length() > 0)
							&& !bmoQuoteGroup.getShowProductImage().toBoolean())) {
						if (index == 1) {
							String blobKeyParse = HtmlUtil.parseImageLink(sFParams, bmoQuoteGroup.getImage());
			%>
						<td class="reportCellEven" align="center" rowspan="<%=count%>"
							style="white-space: nowrap;" valign="center"><img
							src="<%=blobKeyParse%>" width="300" height="200"
							padding="4"></td>
			<%
				} //Fin index 						                         				
											// Muestra solo imgProd si es seleccionada
					} else if (bmoQuoteGroup.getShowProductImage().toBoolean()
						&& (bmoQuoteItem.getBmoProduct().getImage().toString().length() > 0)
						&& !bmoQuoteGroup.getShowGroupImage().toBoolean()) {
								String blobKeyParse = HtmlUtil.parseImageLink(sFParams, bmoQuoteItem.getBmoProduct().getImage());
										
			%>
			<td class="reportCellEven" align="center"
				style="white-space: nowrap;" valign="center"><img
				src="<%=blobKeyParse%>" width="100" height="100"></td>
			<%
				} else {
			%>
			<td class="reportCellEven" align="center"
				style="white-space: nowrap;" valign="center">&nbsp;</td>
			<%
				}
			}
			%>
		</tr>
		<%
			index++;
		} // fin de items 
							//}//else - Si es Kit
		%>
		<tr>
			<td colspan="8" class="">&nbsp;</td>
		</tr>
		<%
			} // fin de orderGroups
		%>
		<%
			if (bmoQuote.getBmoOrderType().getType().equals("" + BmoOrderType.TYPE_RENTAL)) {

						BmoQuoteEquipment bmoQuoteEquipment = new BmoQuoteEquipment();
						PmQuoteEquipment pmQuoteEquipment = new PmQuoteEquipment(sFParams);
						BmFilter bmFilterEq = new BmFilter();
						bmFilterEq.setValueFilter(bmoQuoteEquipment.getKind(), bmoQuoteEquipment.getQuoteId().getName(), bmoQuote.getId());
						Iterator<BmObject> items = pmQuoteEquipment.list(bmFilterEq).iterator();
						int iQEquipment = 1;
						while (items.hasNext()) {
							bmoQuoteEquipment = (BmoQuoteEquipment) items.next();
							if (iQEquipment == 1) {
							%>
							<tr>
								<td class="reportHeaderCell" colspan="7">Recursos</td>
								<td class="reportHeaderCellRight">
									<%
										//Obtener el total del staff
		
										pmConn.open();
		
										double totalEquipment = 0;
		
										sql = " SELECT SUM(qoeq_amount) AS totalequipments FROM quoteequipments "
												+ " WHERE qoeq_quoteid = " + bmoQuote.getId();
		
										pmConn.doFetch(sql);
										if (pmConn.next()) {
											totalEquipment = pmConn.getDouble("totalequipments");
										%> 					
										<%=formatCurrency.format(totalEquipment)%>&nbsp; <%
		 								}
					
					 				pmConn.close();
							 %>
								</td>
							</tr>
							<%	} %>
							<tr>
								<td class="reportCellEven" align="left" colspan="3"><%=bmoQuoteEquipment.getName().toHtml()%>
									&nbsp;<%=bmoQuoteEquipment.getDescription().toHtml()%></td>
								<td class="reportCellEven" align="center">
									<%
										if (bmoQuote.getShowEquipmentQuantity().toBoolean()) {
									%> <%=bmoQuoteEquipment.getQuantity().toInteger()%> <%
					 					}
					 		%>
								</td>
								<td class="reportCellEven" align="center">
									<%
									if (bmoQuote.getShowEquipmentQuantity().toBoolean()) {
									%> <%=bmoQuoteEquipment.getDays().toDouble()%> <%
				 					}
							 %>
								</td>
								<td class="reportCellEven" align="right">
									<%
										if (bmoQuote.getShowEquipmentPrice().toBoolean()) {
									%> <%=formatCurrency.format(bmoQuoteEquipment.getPrice().toDouble())%>
									<%
										}
									%>
								</td>
								<td class="reportCellEven" align="right">
									<%
										if (bmoQuote.getShowEquipmentPrice().toBoolean()) {
									%> <%=formatCurrency.format(bmoQuoteEquipment.getAmount().toDouble())%>
									<%
										}
									%>
								</td>
								<td class="reportCellEven">&nbsp;</td>
							<tr>
							<tr>
								<td colspan="8" class="">&nbsp;</td>
							</tr>
							<%
							iQEquipment++;
						}

						BmoQuoteStaff bmoQuoteStaff = new BmoQuoteStaff();
						PmQuoteStaff pmQuoteStaff = new PmQuoteStaff(sFParams);
						BmFilter bmFilterSt = new BmFilter();
						bmFilterSt.setValueFilter(bmoQuoteStaff.getKind(), bmoQuoteStaff.getQuoteId().getName(),
								bmoQuote.getId());
						Iterator<BmObject> itemsStaff = pmQuoteStaff.list(bmFilterSt).iterator();
						int iQStaff = 1;
						while (itemsStaff.hasNext()) {
							bmoQuoteStaff = (BmoQuoteStaff) itemsStaff.next();
							if (iQStaff == 1) {
								%>			
								<tr>
									<td class="reportHeaderCell" colspan="7">Personal</td>
									<td class="reportHeaderCellRight">&nbsp; <%
						 			//Obtener el total del staff                    		
						 				pmConn.open();
						
						 				double totalStaff = 0;
						
						 				sql = " SELECT SUM(qost_amount) AS totalstaff FROM quotestaff " + " WHERE qost_quoteid = " + bmoQuote.getId();
						
						 				pmConn.doFetch(sql);
						 				if (pmConn.next()) {
						 					totalStaff = pmConn.getDouble("totalstaff");
						 				}
						 				pmConn.close();
						 				%>
						 				<%=formatCurrency.format(totalStaff)%>&nbsp;
						
									</td>
								</tr>
							<%	} %>
							<tr>
								<td class="reportCellEven" align="left" colspan="3"><%=bmoQuoteStaff.getName().toHtml()%>
									&nbsp;<%=bmoQuoteStaff.getDescription().toHtml()%></td>
								<td class="reportCellEven" align="center">
									<%
										if (bmoQuote.getShowStaffQuantity().toBoolean()) {
									%> <%=bmoQuoteStaff.getQuantity().toInteger()%> <%
					 					}
							 	%>
								</td>
								<td class="reportCellEven" align="center">
									<%
										if (bmoQuote.getShowStaffQuantity().toBoolean()) {
									%> <%=bmoQuoteStaff.getDays().toDouble()%> <%
					 					}
					 			%>
								</td>
								<td class="reportCellEven" align="right">
									<%
										if (bmoQuote.getShowStaffPrice().toBoolean()) {
									%> <%=formatCurrency.format(bmoQuoteStaff.getPrice().toDouble())%> <%
					 					}
					 			%>
								</td>
								<td class="reportCellEven" align="right">
									<%
										if (bmoQuote.getShowStaffPrice().toBoolean()) {
									%> <%=formatCurrency.format(bmoQuoteStaff.getAmount().toDouble())%>
									<%
										}
									%>
								</td>
								<td class="reportCellEven">&nbsp;</td>
							<tr>
					<%
						iQStaff++;
					}
				}
			%>
		
		<tr>
			<td colspan="8" class="">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="6" rowspan="4" valign="top" align="left"
				class="detailStart">
				<p class="documentComments">
					<b>Notas:</b> <br>
					<%
						if (bmoOpportunityDetail.getGuests().toInteger() > 0) {
					%>
					Asistentes:
					<%=bmoOpportunityDetail.getGuests().toInteger()%>
					<br>
					<%
						}
					%>
					<%=bmoQuote.getDescription().toHtml()%></p>
			</td>
			<th class="" align="right">Subtotal:</th>
			<td class="reportCellEven" align="right"><b><%=formatCurrency.format(bmoQuote.getAmount().toDouble())%></b>
			</td>
		</tr>
		<tr>
			<th class="" align="right">
				<%
					if (bmoQuote.getDiscount().toDouble() > 0) {
				%> Descuento: <%
					}
				%>
			</th>
			<td class="reportCellEven" align="right">
				<%
					if (bmoQuote.getDiscount().toDouble() > 0) {
				%> <b><%=formatCurrency.format(bmoQuote.getDiscount().toDouble())%></b>
				<%
					}
				%>
			</td>
		</tr>
		<tr>
			<th class="" align="right">
				<%
					if (bmoQuote.getTax().toDouble() > 0) {
				%> IVA: <%
					}
				%>
			</th>
			<td class="reportCellEven" align="right">
				<%
					if (bmoQuote.getTax().toDouble() > 0) {
				%> <b><%=formatCurrency.format(bmoQuote.getTax().toDouble())%></b> <%
 	}
 %>
			</td>
		</tr>
		<tr>
			<th class="" align="right">Total:</th>
			<td class="reportCellEven" align="right"><b><%=formatCurrency.format(bmoQuote.getTotal().toDouble())%></b>
			</td>
		</tr>
		<tr>
			<td class="" colspan="8">&nbsp;</td>
		</tr>
	</table>

	<table width="100%" align="center" border="0" cellpadding="0"
		cellspacing="0" style="font-size: 12px">
		<tr align="center">
			<td align="center"><br> <br>
				_______________________________________</td>
			<td align="center"><br> <br>
				_______________________________________</td>
		</tr>
		<tr>
			<td align="center" class="documentTitleCaption">Ejecutivo
				Comercial: <br> <%=bmoOpportunity.getBmoUser().getFirstname().toHtml()%>
				<%=bmoOpportunity.getBmoUser().getFatherlastname().toHtml()%> <%=bmoOpportunity.getBmoUser().getMotherlastname().toHtml()%>
			</td>
			<td align="center" class="documentTitleCaption">Acepto: <br>
				<%=bmoOpportunity.getBmoCustomer().getDisplayName().toHtml()%>
			</td>
		</tr>
	</table>

	<%
		} else if (bmoOrderType.getType().equals("" + BmoOrderType.TYPE_PROPERTY)) {
				PmQuoteProperty pmQuoteProperty = new PmQuoteProperty(sFParams);
				bmoQuoteProperty = (BmoQuoteProperty) pmQuoteProperty.getBy(bmoQuote.getId(),
						bmoQuoteProperty.getQuoteId().getName());
	%>
	<table border="0" cellspacing="0" width="100%" cellpadding="0"
		style="font-size: 12px">
		<tr>
			<td align="left" rowspan="5" valign="top"><img border="0"
				width="<%=SFParams.LOGO_WIDTH%>" height="<%=SFParams.LOGO_HEIGHT%>"
				src="<%=logoURL%>"></td>
			<td colspan="4" class="reportTitleCell">&nbsp;Cotizaci&oacute;n
			</td>
		</tr>
		<tr>
			<th class="reportCellEven" align="left">Empresa/Cliente:</th>
			<td class="reportCellEven"><%=bmoQuote.getBmoCustomer().getCode().toHtml()%>
				<%=bmoQuote.getBmoCustomer().getDisplayName().toHtml()%></td>
			<th class="reportCellEven" align="left">Clave/Nombre:</th>
			<td class="reportCellEven"><%=bmoQuote.getCode().toHtml() + " - " + bmoQuote.getName().toHtml()%>
			</td>
		</tr>
		<tr>
			<th class="reportCellEven" align="left">Tipo: </t>
			<td class="reportCellEven"><%=bmoOpportunityWFlowType.getName().toHtml()%>
			</td>
			<th class="reportCellEven" align="left">Fecha Cotizaci&oacute;n:
			</th>
			<td class="reportCellEven"><%=SFServerUtil.nowToString(sFParams,
							sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString())%>
			</td>
		</tr>
		<tr>
			<th class="reportCellEven" align="left">Ejecutivo Comercial: </t>
			<td class="reportCellEven"><%=bmoQuote.getBmoUser().getFirstname().toHtml()%>
				<%=bmoQuote.getBmoUser().getFatherlastname().toHtml()%> <%=bmoQuote.getBmoUser().getMotherlastname().toHtml()%>
			</td>
			<th class="reportCellEven" align="left">Estatus:</th>
			<td class="reportCellEven"><%=bmoQuote.getStatus().getSelectedOption().getLabeltoHtml()%>
				(#<%=bmoQuote.getAuthNum().toString()%>)</td>
		</tr>
	</table>
	<br>
	<table border="0" cellspacing="0" width="100%" cellpadding="0"
		style="font-size: 12px">
		<tr>
			<td class="reportTitleCell" colspan="9">Inmueble</td>
		</tr>
		<tr>
			<th class="reportCellEven" style="text-align: left" width="15%">Clave</th>
			<th class="reportCellEven" style="text-align: center" width="10%">Mza</th>
			<th class="reportCellEven" style="text-align: center" width="5%">Lote</th>
			<th class="reportCellEven" style="text-align: center" width="20%">Calle
				y N&uacute;mero</th>
			<th class="reportCellEven" style="text-align: right" width="10%">Precio</th>
			<th class="reportCellEven" style="text-align: right" width="10%">$
				T. Ex.</th>
			<th class="reportCellEven" style="text-align: right" width="10%">$
				C. Ex.</th>
			<th class="reportCellEven" style="text-align: right" width="10%">$
				Otros</th>
			<th class="reportCellEven" style="text-align: right" width="10%">Total</th>
		</tr>
		<tr>
			<td class="reportCellEven" align="left">&nbsp;<%=bmoQuoteProperty.getBmoProperty().getCode().toHtml()%>
			</td>
			<td class="reportCellEven" align="center"><%=bmoQuoteProperty.getBmoProperty().getBmoDevelopmentBlock().getCode().toHtml()%>
			</td>
			<td class="reportCellEven" align="center"><%=bmoQuoteProperty.getBmoProperty().getLot().toHtml()%>
			</td>
			<td class="reportCellEven" align="center"><%=bmoQuoteProperty.getBmoProperty().getStreet().toHtml()%>
				#<%=bmoQuoteProperty.getBmoProperty().getNumber().toHtml()%></td>
			<td class="reportCellEven" align="right"><%=formatCurrency.format(bmoQuoteProperty.getPrice().toDouble())%>
			</td>
			<td class="reportCellEven" align="right"><%=formatCurrency.format(bmoQuoteProperty.getExtraLand().toDouble())%>
			</td>
			<td class="reportCellEven" align="right"><%=formatCurrency.format(bmoQuoteProperty.getExtraConstruction().toDouble())%>
			</td>
			<td class="reportCellEven" align="right"><%=formatCurrency.format(bmoQuoteProperty.getExtraOther().toDouble())%>
			</td>
			<td class="reportCellEven" align="right"><b><%=formatCurrency.format(bmoQuoteProperty.getAmount().toDouble())%></b>
			</td>
		</tr>
	</table>
	<br>
	<table border="0" cellspacing="0" width="100%" cellpadding="0"
		style="font-size: 12px">
		<tr>
			<td class="reportTitleCell" colspan="9">Extras</td>
		</tr>
		<tr>
			<th class="reportCellEven" align="left" width="15%">Cantidad</th>
			<th class="reportCellEven" align="left" width="30%">Extra</th>
			<th class="reportCellEven" align="left" width="33%">Comentarios</th>
			<th class="reportCellEven" style="text-align: right" width="15%">Precio</th>
			<th class="reportCellEven" style="text-align: right" width="15%">Total</th>
		</tr>
		<%
			double totalExtras = 0;
					PmQuotePropertyModelExtra pmQuotePropertyModelExtra = new PmQuotePropertyModelExtra(sFParams);
					BmFilter bmFilterQupx = new BmFilter();
					bmFilterQupx.setValueFilter(bmoQuotePropertyModelExtra.getKind(),
							bmoQuotePropertyModelExtra.getQuoteId().getName(), bmoQuote.getId());
					Iterator<BmObject> extras = pmQuotePropertyModelExtra.list(bmFilterQupx).iterator();
					while (extras.hasNext()) {
						bmoQuotePropertyModelExtra = (BmoQuotePropertyModelExtra) extras.next();
						totalExtras += bmoQuotePropertyModelExtra.getAmount().toDouble();
		%>
		<tr>
			<td class="reportCellEven" align="left"><%=bmoQuotePropertyModelExtra.getQuantity().toHtml()%>
			</td>
			<td class="reportCellEven" align="left"><%=bmoQuotePropertyModelExtra.getBmoPropertyModelExtra().getName().toHtml()%>
			</td>
			<td class="reportCellEven" align="left"><%=bmoQuotePropertyModelExtra.getComments().toHtml()%>
			</td>
			<td class="reportCellEven" align="right"><%=formatCurrency.format(bmoQuotePropertyModelExtra.getPrice().toDouble())%>
			</td>
			<td class="reportCellEven" align="right"><%=formatCurrency.format(bmoQuotePropertyModelExtra.getAmount().toDouble())%>
			</td>
		<tr>
			<%
				}
			%>
		
		<tr>
			<th class="reportCellEven" align="right" colspan="4">Total
				Extras:</th>
			<td class="reportCellEven" align="right"><b><%=formatCurrency.format(totalExtras)%></b>
			</td>
		</tr>
		<tr>
			<td colspan="5">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="3" rowspan="4" valign="top" align="left"
				class="detailStart">
				<p class="documentComments">
					<b>Notas:</b> <br>
					<%=bmoQuote.getDescription().toHtml()%></p>
			</td>
			<th class="documentLabel" align="right">Subtotal:</th>
			<td class="reportCellEven" align="right"><b><%=formatCurrency.format(bmoQuote.getAmount().toDouble())%></b>
			</td>
		</tr>
		<%
			if (bmoQuote.getDiscount().toDouble() > 0) {
		%>
		<tr>
			<th class="" align="right">Descuento:</th>
			<td class="reportCellEven" align="right"><b><%=formatCurrency.format(bmoQuote.getDiscount().toDouble())%></b>
			</td>
		</tr>
		<%
			} else {
		%>
		<td class="" align="right"></td>
		<%
			}
		%>
		<%
			if (bmoQuote.getTax().toDouble() > 0) {
		%>
		<tr>
			<th class="" align="right">IVA:</th>
			<td class="reportCellEven" align="right"><b><%=formatCurrency.format(bmoQuote.getTax().toDouble())%></b>
			</td>
		</tr>
		<%
			} else {
		%>
		<td class="" align="right"></td>
		<%
			}
		%>
		<tr>
			<th class="" align="right">Total:</th>
			<td class="reportCellEven" align="right"><b><%=formatCurrency.format(bmoQuote.getTotal().toDouble())%></b>
			</td>
		</tr>
	</table>

	<%
		}
	%>
	<%
		} catch (Exception e) {
			String errorLabel = "Error de Cotizacion";
			String errorText = "La Cotizacion no puede ser desplegada.";
			String errorException = e.toString();

			//response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);
	%>

	<%=errorException%>

	<%
		}
	%>

</body>
</html>