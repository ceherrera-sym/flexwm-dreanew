<%@page import="com.flexwm.server.op.PmConsultancy"%>
<%@page import="com.flexwm.shared.op.BmoConsultancy"%>
<%@page import="com.symgae.shared.GwtUtil"%>
<%@page import="com.flexwm.server.op.PmOrderDetail"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.server.op.PmOrderGroup"%>
<%@page import="com.flexwm.shared.op.BmoOrderGroup"%>
<%@page import="com.flexwm.shared.op.BmoOrderItem"%>
<%@page import="com.flexwm.shared.op.BmoOrderEquipment"%>
<%@page import="com.flexwm.shared.op.BmoOrderStaff"%>
<%@page import="com.flexwm.server.op.PmOrderItem"%>
<%@page import="com.flexwm.server.op.PmOrderEquipment"%>
<%@page import="com.flexwm.server.op.PmOrderStaff"%>
<%@page import="com.flexwm.server.op.PmOrder"%>
<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@page import="com.flexwm.server.op.PmOrderType"%>
<%@page import="com.flexwm.shared.op.BmoOrderType"%>
<%@page import="com.flexwm.server.cm.PmProject"%>
<%@page import="com.flexwm.shared.cm.BmoProject"%>
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="com.flexwm.shared.cm.BmoCustomerAddress"%>
<%@page import="com.flexwm.server.cm.PmCustomerAddress"%>
<%@page import="com.flexwm.shared.cm.BmoCustomerContact"%>
<%@page import="com.flexwm.server.cm.PmCustomerContact"%>
<%@page import="com.flexwm.shared.wf.BmoWFlowLog"%>
<%@page import="com.flexwm.server.wf.PmWFlowLog"%>
<%@page import="com.flexwm.shared.wf.BmoWFlowType"%>
<%@page import="com.flexwm.server.wf.PmWFlowType"%>
<%@page import="com.flexwm.shared.BmoFlexConfig"%>
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
<%@page import="com.symgae.client.ui.UiParams"%>

<%@include file="../inc/login_opt.jsp" %>

<%
try {
	String title = "Orden de Servicio";
	
	BmoConsultancy bmoConsultancy = new BmoConsultancy();
	PmConsultancy pmConsultancy = new PmConsultancy(sFParams);
	
	BmoOrder bmoOrder = new BmoOrder();
	PmOrder pmOrder = new PmOrder(sFParams);
	
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)sFParams.getBmoProgram(bmoConsultancy.getProgramCode());	
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
		<link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>""> 
	</head>
	<body class="default" <%= permissionPrint %>>
<%
	if (sFParams.hasRead(bmoProgram.getCode().toString())) {
		NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(Locale.US);
		
	    // Obtener parametros
	    int consultancyId = 0;
	    if (isExternal) consultancyId = decryptId;	    
	    else consultancyId = Integer.parseInt(request.getParameter("foreignId"));  
	    
	    // Si es llamada externa, utilizar llave desencriptada, en caso contrario utilizar llave de la orden
	    bmoConsultancy = (BmoConsultancy)pmConsultancy.get(consultancyId);

		bmoOrder = (BmoOrder)pmOrder.get(bmoConsultancy.getOrderId().toInteger());
		
		// Se esta autorizando, Info para guardar en la bitacora, ya que el pedido hasta ahorita esta en revision
	    boolean log = false;
	    if (request.getParameter("log") != null) log = Boolean.parseBoolean(request.getParameter("log"));
	    
//	    int authorizedUser = 0; 
//	    if (request.getParameter("auser") != null) authorizedUser = Integer.parseInt(request.getParameter("auser"));
//	    
//	    String authorizedDate = "";
//	    if (request.getParameter("auser") != null) authorizedDate = request.getParameter("adate");
	    
		if (!log)
			if (bmoConsultancy.getStatus().toChar() != BmoOrder.STATUS_AUTHORIZED) 
				throw new Exception("El Pedido no esta Autorizado - no se puede desplegar.");

		
		BmoUser bmoUser = new BmoUser();
		PmUser pmUser = new PmUser(sFParams);
// 		BmoOrderDetail bmoOrderDetail = new BmoOrderDetail();
// 		PmOrderDetail pmOrderDetail = new PmOrderDetail(sFParams);
// 		bmoOrderDetail = (BmoOrderDetail)pmOrderDetail.getBy(bmoOrder.getId(),bmoOrderDetail.getOrderId().getName());
		BmoCustomer bmoCustomer = new BmoCustomer();
		
		BmoCompany bmoCompany = new BmoCompany();
		PmCompany pmCompany = new PmCompany(sFParams);
		bmoCompany = (BmoCompany)pmCompany.get(bmoConsultancy.getCompanyId().toInteger());
			
		// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
		String logoURL ="";
		if (!bmoCompany.getLogo().toString().equals(""))
			logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
		else 
			logoURL = sFParams.getMainImageUrl();
		
		PmConn pmConn = new PmConn(sFParams);
		pmConn.open();
		//if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)) {

		bmoUser = (BmoUser)pmUser.get(bmoConsultancy.getUserId().toInteger());
		
		// Usuario que autorizo
		BmoUser bmoUserAuthorized = new BmoUser();
		PmUser pmUserAuthorized = new PmUser(sFParams);
//		if (log) {
//			if (authorizedUser > 0)
//				bmoUserAuthorized = (BmoUser)pmUserAuthorized.get(authorizedUser);
//		} else {
			if (bmoOrder.getAuthorizedUser().toInteger() > 0)
				bmoUserAuthorized = (BmoUser)pmUserAuthorized.get(bmoOrder.getAuthorizedUser().toInteger());
		//}
		
		String customer = "", orderBy = "";
		if (bmoConsultancy.getBmoCustomer().getCustomertype().toChar() == BmoCustomer.TYPE_PERSON) {
			customer =  bmoConsultancy.getBmoCustomer().getCode().toHtml() + " " +  bmoConsultancy.getBmoCustomer().getDisplayName().toHtml();
			orderBy = " ASC";
		} else {
			customer = bmoConsultancy.getBmoCustomer().getCode().toHtml() + " " + 
					bmoConsultancy.getBmoCustomer().getLegalname().toHtml();
			orderBy = " DESC";
		}	
		
		String sqlAditional  = "", address = "", userAuthorization = "", dateAuthorization = "";
		
		sqlAditional = " SELECT * FROM customeraddress " +
							" LEFT JOIN cities ON (city_cityid = cuad_cityid) " +
							" LEFT JOIN states ON (stat_stateid = city_stateid) " +
		    				" WHERE cuad_customerid = " + bmoConsultancy.getCustomerId().toInteger() +
		    				" ORDER BY cuad_type " +orderBy;
		
		pmConn.doFetch(sqlAditional);
		if (pmConn.next()) { 
			address = pmConn.getString("customeraddress", "cuad_street") +
						" #" + pmConn.getString("customeraddress", "cuad_number") + 
						" " + pmConn.getString("customeraddress", "cuad_neighborhood") + 
						", C.P. " + pmConn.getString("customeraddress", "cuad_zip") +
						", " + pmConn.getString("cities", "city_name") +
						", " + pmConn.getString("states", "stat_name");
		}
		
		// Contacto clientes
		BmoCustomerContact bmoCustomerContact = new BmoCustomerContact();
		PmCustomerContact pmCustomerContact = new PmCustomerContact(sFParams);
		if (bmoConsultancy.getCustomerContactId().toInteger() > 0)
			bmoCustomerContact = (BmoCustomerContact)pmCustomerContact.get(bmoConsultancy.getCustomerContactId().toInteger());
		
	%>
			<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
				<tr>
					<td align="left" width="" rowspan="10" valign="top">
						<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
					</td>
					<td colspan="4" class="reportTitleCell" style="text-align:center">
						<%= bmoCompany.getName().toHtml()%>
						<br>
						<span style="font-size: 12px">
							<%= title %>
						</span>
					</td>
				</tr>
				<tr>       
					<th class="reportCellEven" align="left">
						<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoConsultancy.getCustomerId()))%>:
					</th>
					<td class="reportCellEven" align="left">
						<%= HtmlUtil.stringToHtml(customer) %>
					</td>
					<th class="reportCellEven" align="left">
						<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoConsultancy.getCode()))%>:
					</th>
					<td class="reportCellEven" align="left">
						<%= bmoConsultancy.getCode().toHtml() %>
					</td>
				</tr>
				<tr>
					<th class="reportCellEven" align="left">
						<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoCustomer.getProgramCode(), bmoCustomer.getRfc()))%>:
					</th>
					<td class="reportCellEven" align="left">
						<%= bmoConsultancy.getBmoCustomer().getRfc().toHtml()%>
					</td>
					<th class="reportCellEven" align="left">
						<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoConsultancy.getStartDate()))%>:
					</th>
					<td class="reportCellEven" align="left">
		  				<%= SFServerUtil.formatDate(sFParams, sFParams.getDateTimeFormat(), sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString(), bmoConsultancy.getStartDate().toString()) %>      				
					</td>
				</tr>
				<tr>
					<th class="reportCellEven" align="left">
						Domicilio:
					</th>
					<td class="reportCellEven" align="left">
						<%= HtmlUtil.stringToHtml(address)%>
					</td>
					<th class="reportCellEven" align="left">
						<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoConsultancy.getCustomerRequisition()))%>:
					</th>
					<td class="reportCellEven" align="left">
						<%= HtmlUtil.stringToHtml(bmoConsultancy.getCustomerRequisition().toString())%>
					</td>
				</tr>
				<tr>
					<th class="reportCellEven" align="left">
						<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoCustomer.getProgramCode().toString(), bmoCustomer.getPhone()))%>:
					</th>
					<td class="reportCellEven" align="left">
						<%	if (bmoConsultancy.getBmoCustomer().getMobile().toString().equals("")) { %>
								<%= bmoConsultancy.getBmoCustomer().getPhone().toHtml() %>
						<%	} else { %>
							<%= bmoConsultancy.getBmoCustomer().getMobile().toHtml() %>
						<%	}%>
					</td>
					<th class="reportCellEven" align="left">
						<%= ((bmoOrder.getCoverageParity().toBoolean()) ? 
								"" + HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoOrder.getCoverageParity())) + ":" 
								:
								"") %>
					</th>
					<td class="reportCellEven" align="left">
						<%= ((bmoOrder.getCoverageParity().toBoolean()) ? "Si" : "") %>
					</td>
				</tr>

				<tr>
					<th class="reportCellEven" align="left">
						<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoCustomer.getProgramCode().toString(), bmoCustomer.getEmail()))%>:
					</th>
					<td class="reportCellEven" align="left">
						<%= bmoConsultancy.getBmoCustomer().getEmail().toHtml()%>
					</td>
					<th class="reportCellEven" align="left">
					<%= ((bmoOrder.getCoverageParity().toBoolean()) ? 
							"" + HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoOrder.getCurrencyParity())) + ":"
							: 
							"") %>
					</th>
					<td class="reportCellEven" align="left">
					<%	if (bmoOrder.getCoverageParity().toBoolean()) { %>
							<%= bmoOrder.getCurrencyParity().toHtml() %>
					<%	} else { %>
							&nbsp;
					<%	} %>
					</td>
				</tr>
				<tr>
					<th class="reportCellEven" align="left">
						<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoConsultancy.getCustomerContactId()))%>:
					</th>
					<td class="reportCellEven" align="left">
						<%= bmoCustomerContact.getFullName().toHtml() %>
						<%= bmoCustomerContact.getFatherLastName().toHtml() %>
						<%= bmoCustomerContact.getMotherLastName().toHtml() %>
						(
						<% 	if (bmoCustomerContact.getNumber().toString().length() > 0) { %>
								<%= bmoCustomerContact.getNumber().toHtml() %>,
						<%	} %>
						<% 	if (bmoCustomerContact.getCellPhone().toString().length() > 0) { %>
								<%= bmoCustomerContact.getCellPhone().toHtml() %>,
						<%	} %>
						<% 	if (bmoCustomerContact.getEmail().toString().length() > 0) { %>
								<%= bmoCustomerContact.getEmail().toHtml() %>
						<%	} %>
						)
					</td>
					<th class="reportCellEven" align="left">
						<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoConsultancy.getUserId()))%>:
					</th>
					<td class="reportCellEven" align="left">
						<%= bmoUser.getFirstname().toHtml()%> 
						<%= bmoUser.getFatherlastname().toHtml()%>
						<%= bmoUser.getMotherlastname().toHtml()%>
					</td>
				</tr>
				<tr>
					<th class="reportCellEven" align="left">
						<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoConsultancy.getWFlowTypeId()))%>:
					</th>
					<td class="reportCellEven" align="left">
						<%= bmoConsultancy.getBmoWFlowType().getName().toHtml() %>
					</td>
					<th class="reportCellEven" align="left">
						<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoConsultancy.getStatus()))%>:
					</th>
					<td class="reportCellEven" align="left">
						<%= bmoConsultancy.getStatus().getSelectedOption().getLabeltoHtml() %>
					</td>
				</tr>				
				<tr>
					<th class="reportCellEven" align="left">
						<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoConsultancy.getProgramCode().toString(), bmoConsultancy.getDeliveryDate()))%>:
					</th>					
					<td class="reportCellEven" align="left" colspan="3">
					   <%= bmoConsultancy.getDeliveryDate().toHtml() %>
					</td>
				</tr>				 		
				<tr>
					<th class="reportCellEven" align="left">
					<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoConsultancy.getDescription()))%>:
 
					</th>
					<td class="reportCellEven" align="left" colspan="3">
						<%= bmoConsultancy.getDescription().toHtml()%>
					</td>
				</tr>
				
			</table>
				<br>
			<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
				<tr>       
					<th class="reportHeaderCell" width="50%" colspan="3">Producto</th>
					<th class="reportHeaderCell" width="15%" >Cant.</th>
					<th class="reportHeaderCell" width="15%" >Unidad</th>
					<th class="reportHeaderCell" width="50%" colspan="">Descripci&oacute;n</th>
					<th class="reportHeaderCellRight"  width="10%">Tarifa</th>
					<th class="reportHeaderCellRight" width="10%">Importe</th>
				</tr>
				<tr>
					<td colspan="6">&nbsp;</td>
				</tr>
				<tr>    
				<%
				String sql = "";
				double subTotal = 0, iva = 0;
				double subTotalGeneral = 0;
				BmoOrderGroup bmoOrderGroup = new BmoOrderGroup();
				PmOrderGroup pmOrderGroup = new PmOrderGroup(sFParams);
				BmFilter bmFilter = new BmFilter();
				bmFilter.setValueFilter(bmoOrderGroup.getKind(), bmoOrderGroup.getOrderId().getName(), bmoConsultancy.getOrderId().toInteger());
				Iterator<BmObject> quoteGroups = pmOrderGroup.list(bmFilter).iterator();
				int i = 1;
				while (quoteGroups.hasNext()) {
					bmoOrderGroup = (BmoOrderGroup)quoteGroups.next(); %>
					<tr>
						<td class="reportHeaderCell" colspan="8" style="white-space: normal">
							<%= i++ %>. <%= bmoOrderGroup.getName().toHtml() %>
							<%	if (bmoOrderGroup.getDescription().toString().length() > 0) { %>
                            		<br><%= bmoOrderGroup.getDescription().toHtml() %>
                            <%	} %>
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
					bmFilterO.setValueFilter(bmoOrderItem.getKind(), bmoOrderItem.getOrderGroupId().getName(), bmoOrderGroup.getId());
					ArrayList list = pmOrderItem.list(bmFilterO);
					Iterator<BmObject> items = list.iterator();
					int index = 1, count = list.size();
					while(items.hasNext()) {
						bmoOrderItem = (BmoOrderItem)items.next();	
						%>
						<tr>	   	                      
							<% 	if (bmoOrderItem.getProductId().toInteger() > 0) { %>
									<td class="reportCellEven" align ="left" colspan="3">  
									<% 	if(bmoOrderItem.getBmoProduct().getDisplayName().toString().equals("")) { %>
											<%= bmoOrderItem.getBmoProduct().getName().toHtml() %>
											<br><span class="documentSubText"><%= bmoOrderItem.getDescription().toHtml() %></span>
									<% 	} else { %>
											<%= bmoOrderItem.getBmoProduct().getDisplayName().toHtml() %>
									<% 	} %>
									</td>
									<td class="reportCellEven" >
										<%= bmoOrderItem.getQuantity().toHtml() %>
									</td>
									<td class="reportCellEven" >
										<%= bmoOrderItem.getBmoProduct().getBmoUnit().getCode().toHtml() %>
									</td>
									<td class="reportCellEven" align="left">
										<%= bmoOrderItem.getBmoProduct().getDescription().toHtml() %>
									</td>
							<% 	} else { %>
									<td class="reportCellEven" align ="left" colspan="3">  
										<%= bmoOrderItem.getName().toHtml() %>
									</td>
									<td class="reportCellEven" >
										<%= bmoOrderItem.getQuantity().toHtml() %>
									</td>
									<td class="reportCellEven">
									</td>
									<td class="reportCellEven" >
										<%= bmoOrderItem.getDescription().toHtml() %>
									</td>
							<% 	} %>
							
							<td class="reportCellEven" align="right">
							<% 	if (log) { %>
									<%= formatCurrency.format(bmoOrderItem.getPrice().toDouble()) %>
							<% 	} else { %>
								<%	if (bmoOrderGroup.getShowPrice().toBoolean()) { %>  
										<%= formatCurrency.format(bmoOrderItem.getPrice().toDouble()) %>
								<% 	} %>   
							<%	}%>
							</td>
		
							<td class="reportCellEven" align="right">
							<% 	if (log) { %>
									<%= formatCurrency.format(bmoOrderItem.getAmount().toDouble()) %>
							<% 	} else { %>
								<% 	if (bmoOrderGroup.getShowPrice().toBoolean()) { %>  
										<%= formatCurrency.format(bmoOrderItem.getAmount().toDouble()) %>
								<% 	} %>
							<%	} %>
							</td>
						</tr>
					<% 
							index++;
						}// fin de while
						//}//else - Si es Kit
						%>
			<% 	} //fin de groups%> 
				<tr>
					<td colspan="6" class="">&nbsp;</td>
				</tr>
				<tr>
					<td colspan="6" rowspan="4"  valign="top" align="left" class="detailStart">
						<p class="documentComments">
						<b>
							<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoOrder.getComments()))%>:
						</b> 
						<br> 
						<%= bmoOrder.getComments().toHtml() %></p>
					</td>                                  
					<td class="" align="right">
						<b>Subtotal:</b>
					</td>
					<td class="reportCellEven" align="right">
						<b><%=  formatCurrency.format(bmoOrder.getAmount().toDouble()) %></b>
					</td>
				</tr>
				<% 	if (bmoOrder.getDiscount().toDouble() > 0) { %>
						<tr>            
							<td class="" align="right">
								<b>Descuento:</b>
							</td>
		        			<td class="reportCellEven" align="right">
								<b><%=  formatCurrency.format(bmoOrder.getDiscount().toDouble()) %></b>	
							</td>
						</tr> 
				<%	} %>
				<%	if (bmoOrder.getTax().toDouble() > 0) { %>
						<tr>            
							<td class="" align="right">
								<b>IVA:</b>
							</td>
			        			<td class="reportCellEven" align="right">
									<b><%= formatCurrency.format(bmoOrder.getTax().toDouble()) %></b>
								</td>
						</tr>
				<%	} %>
				<tr>                                                                        
					<td class="" align="right">
						<b>Total(<%= bmoOrder.getBmoCurrency().getCode().toString()%>):</b>
					</td>
					<td class="reportCellEven" align="right">
						<b><%=  formatCurrency.format(bmoOrder.getTotal().toDouble()) %></b>
					</td>
				</tr>
			</table>
			<table width="100%" border="0" cellpadding="0" cellspacing="0" style="font-size: 12px">
			    <tr >
<!-- 				    <td class="documentTitleCaption" align="left" height="40" width="40%"> -->
<!-- 				    	&nbsp; -->
<!-- 			        </td> -->
			        <td class="documentTitleCaption" align="left" height="40">
						<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoOrder.getAuthorizedUser()))%>:
			        	<b>
					        <%= HtmlUtil.stringToHtml(bmoUserAuthorized.getFirstname().toString()) %>
							<%= HtmlUtil.stringToHtml(bmoUserAuthorized.getFatherlastname().toString()) %>
							<%= HtmlUtil.stringToHtml(bmoUserAuthorized.getMotherlastname().toString()) %>							
						</b>
			        </td>
			        <td class="documentTitleCaption" align="left" height="20%">
						<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoOrder.getAuthorizedDate()))%>:
		        		<b>
			        		<% 	//if (log) { %>
				        		<%	//if (!authorizedDate.equals("")) { %>
										<%//= SFServerUtil.formatDate(sFParams, sFParams.getDateFormat(), sFParams.getBmoSFConfig().getPrintDateFormat().toString(), authorizedDate) %>
								<%	//} %>
							<%	//} else { %>
								<%	if (!bmoOrder.getAuthorizedDate().toString().equals("")) { %>
											<%= SFServerUtil.formatDate(sFParams, sFParams.getDateFormat(), sFParams.getBmoSFConfig().getPrintDateFormat().toString(), bmoOrder.getAuthorizedDate().toString()) %>
								<%	} %>
							<%	//}%>
						</b>
					</td>
			    </tr>
			</table>
	<%	
		   	 //} 
		pmConn.close();
	} //Fin Si tiene permiso para ver
		} catch (Exception e) { 
				String errorLabel = "Error de Formato";
				String errorText = "El Formato no se puede desplegar";
				String errorException = e.toString();
				
			    response.sendRedirect(GwtUtil.getProperUrl(sFParams, "/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException));
		%>
			<%= errorException %>
	<%
		}
	%>

</body>
</html>
