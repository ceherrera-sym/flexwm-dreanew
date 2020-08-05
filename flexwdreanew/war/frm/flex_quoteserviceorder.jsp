<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.server.cm.PmOpportunity"%>
<%@page import="com.flexwm.shared.cm.BmoOpportunity"%>
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
<%@page import="com.flexwm.server.cm.PmCustomer"%>
<%@page import="com.flexwm.shared.cm.BmoCustomerAddress"%>
<%@page import="com.flexwm.server.cm.PmCustomerAddress"%>
<%@page import="com.flexwm.shared.cm.BmoCustomerContact"%>
<%@page import="com.flexwm.server.cm.PmCustomerContact"%>
<%@page import="com.flexwm.shared.wf.BmoWFlowLog"%>
<%@page import="com.flexwm.server.wf.PmWFlowLog"%>
<%@page import="com.flexwm.shared.wf.BmoWFlowType"%>
<%@page import="com.flexwm.server.wf.PmWFlowType"%>
<%@page import="com.flexwm.shared.BmoFlexConfig"%>
<%@page import="com.symgae.server.sf.PmCompany"%>
<%@page import="com.symgae.shared.sf.BmoCompany"%>
<%@page import="com.flexwm.shared.fi.BmoCurrency"%>
<%@page import="com.flexwm.server.fi.PmCurrency"%>
<%@page import="com.symgae.client.ui.UiParams"%>

<%@include file="../inc/login_opt.jsp" %>

<%
try {
	String title = "Orden de Servicio";
	
	BmoOpportunity bmoOpportunity = new BmoOpportunity();
	PmOpportunity pmOpportunity = new PmOpportunity(sFParams);
	
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)sFParams.getBmoProgram(bmoOpportunity.getProgramCode());	
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
		<link rel="stylesheet" type="text/css" href="../../css/<%= defaultCss %>"> 
		<link rel="stylesheet" type="text/css" href="../../css/flexwm.css"> 
	</head>
	<body class="default" <%= permissionPrint %>>
<%
	if (sFParams.hasRead(bmoProgram.getCode().toString())) {
		NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(Locale.US);
		
	    // Obtener parametros
	    int opportunityId = 0;
	    if (isExternal) opportunityId = decryptId;
	    else opportunityId = Integer.parseInt(request.getParameter("foreignId"));  
	    
	    // Si es llamada externa, utilizar llave desencriptada, en caso contrario utilizar llave de la orden
		bmoOpportunity = (BmoOpportunity)pmOpportunity.get(opportunityId);
		
		BmoQuote bmoQuote = new BmoQuote();
		PmQuote pmQuote = new PmQuote(sFParams);
		bmoQuote = (BmoQuote)pmQuote.get(bmoOpportunity.getQuoteId().toInteger());
		
		// Se esta autorizando, Info para guardar en la bitacora, ya que la cotizacion hasta ahorita esta en revision
	    boolean log = false;
	    if (request.getParameter("log") != null) log = Boolean.parseBoolean(request.getParameter("log"));
	    
//	    int authorizedUser = 0; 
//	    if (request.getParameter("auser") != null) authorizedUser = Integer.parseInt(request.getParameter("auser"));
//	    
//	    String authorizedDate = "";
//	    if (request.getParameter("auser") != null) authorizedDate = request.getParameter("adate");
	    
		if (!log)
			if (bmoQuote.getStatus().toChar() != bmoQuote.STATUS_AUTHORIZED) 
				throw new Exception("La Cotizacion no esta Autorizada - no se puede desplegar.");

		// Vendedor
		BmoUser bmoUser = new BmoUser();
		PmUser pmUser = new PmUser(sFParams);
		bmoUser = (BmoUser)pmUser.get(bmoOpportunity.getUserId().toInteger());
		
		// Empresa de la Oportunidad
		BmoCompany bmoCompany = new BmoCompany();
		PmCompany pmCompany = new PmCompany(sFParams);
		bmoCompany = (BmoCompany)pmCompany.get(bmoOpportunity.getCompanyId().toInteger());
		
		
		
		// Cliente
		BmoCustomer bmoCustomer = new BmoCustomer();
		PmCustomer pmCustomer = new PmCustomer(sFParams);
		bmoCustomer = (BmoCustomer)pmCustomer.get(bmoOpportunity.getCustomerId().toInteger());
		
		// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
		String logoURL ="";
		if (!bmoCompany.getLogo().toString().equals(""))
			logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
		else 
			logoURL = sFParams.getMainImageUrl();
		
		PmConn pmConn = new PmConn(sFParams);
		pmConn.open();
		//if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)) {

		
		// Usuario que autorizo
		BmoUser bmoUserAuthorized = new BmoUser();
		PmUser pmUserAuthorized = new PmUser(sFParams);
//		if (log) {
//			if (authorizedUser > 0)
//				bmoUserAuthorized = (BmoUser)pmUserAuthorized.get(authorizedUser);
//		} else {
			if (bmoQuote.getAuthorizedUser().toInteger() > 0)
				bmoUserAuthorized = (BmoUser)pmUserAuthorized.get(bmoQuote.getAuthorizedUser().toInteger());
		//}
		
		String customer = "", orderBy = "";
		if (bmoCustomer.getCustomertype().toChar() == BmoCustomer.TYPE_PERSON) {
			customer =  bmoCustomer.getCode().toHtml() + " " +  bmoCustomer.getDisplayName().toHtml();
			orderBy = " ASC";
		} else {
			customer = bmoCustomer.getCode().toHtml() + " " + 
						bmoCustomer.getLegalname().toHtml();
			orderBy = " DESC";
		}	
		
		String sqlAditional  = "", address = "", userAuthorization = "", dateAuthorization = "";
		
		sqlAditional = " SELECT * FROM customeraddress " +
							" LEFT JOIN cities ON (city_cityid = cuad_cityid) " +
							" LEFT JOIN states ON (stat_stateid = city_stateid) " +
		    				" WHERE cuad_customerid = " + bmoOpportunity.getCustomerId().toInteger() +
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
		
		// Contacto clientes desde Cotizacion
		BmoCustomerContact bmoCustomerContact = new BmoCustomerContact();
		PmCustomerContact pmCustomerContact = new PmCustomerContact(sFParams);
		if (bmoQuote.getCustomerContactId().toInteger() > 0)
			bmoCustomerContact = (BmoCustomerContact)pmCustomerContact.get(bmoQuote.getCustomerContactId().toInteger());
		
		BmoCurrency bmoCurrencyNow = new BmoCurrency();
		PmCurrency pmCurrency = new PmCurrency(sFParams);
		bmoCurrencyNow = (BmoCurrency)pmCurrency.get(bmoQuote.getCurrencyId().toInteger());
	%>
			<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
				<tr>
					<td align="left" width="" rowspan="9" valign="top">
						<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
					</td>
					<td colspan="4" class="reportTitleCell" style="text-align:center">
						<%= bmoCompany.getName().toHtml() %>
						<br>
						<span style="font-size: 12px">
							<%= title %>
						</span>
					</td>
				</tr>
				<tr>       
					<th class="reportCellEven" align="left">
						<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoOpportunity.getCustomerId()))%>:
					</th>
					<td class="reportCellEven" align="left">
						<%= HtmlUtil.stringToHtml(customer) %>
					</td>
					<th class="reportCellEven" align="left">
						<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoOpportunity.getCode()))%>:
					</th>
					<td class="reportCellEven" align="left">
						<%= bmoOpportunity.getCode().toHtml() %>
					</td>
				</tr>
				<tr>
					<th class="reportCellEven" align="left">
						<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoCustomer.getProgramCode(), bmoCustomer.getRfc()))%>:
					</th>
					<td class="reportCellEven" align="left">
						<%= bmoCustomer.getRfc().toHtml()%>
					</td>
					<th class="reportCellEven" align="left">
						<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoOpportunity.getStartDate()))%>:
					</th>
					<td class="reportCellEven" align="left">
		  				<%= SFServerUtil.formatDate(sFParams, sFParams.getDateTimeFormat(), sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString(), bmoOpportunity.getStartDate().toString()) %>      				
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
						<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoQuote.getCustomerRequisition()))%>:
					</th>
					<td class="reportCellEven" align="left">
						<%= HtmlUtil.stringToHtml(bmoQuote.getCustomerRequisition().toString())%>
					</td>
				</tr>
				<tr>
					<th class="reportCellEven" align="left">
						<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoCustomer.getProgramCode().toString(), bmoCustomer.getPhone()))%>:
					</th>
					<td class="reportCellEven" align="left">
						<%	if (bmoCustomer.getMobile().toString().equals("")) { %>
								<%= bmoCustomer.getPhone().toHtml() %>
						<%	} else { %>
								<%= bmoCustomer.getMobile().toHtml() %>
						<%	}%>
					</td>	
					<th class="reportCellEven" align="left">
						<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoQuote.getProgramCode(), bmoQuote.getCoverageParity()))%>:
					</th>
					<td class="reportCellEven" align="left">
						<%= ((bmoQuote.getCoverageParity().toBoolean()) ? "Si" : "No") %>
					</td>
				</tr>
				<tr>
					<th class="reportCellEven" align="left">
						<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoCustomer.getProgramCode().toString(), bmoCustomer.getEmail()))%>:
					</th>
					<td class="reportCellEven" align="left">
						<%= bmoCustomer.getEmail().toHtml()%>
					</td>
					<th class="reportCellEven" align="left">
						<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoQuote.getProgramCode().toString(), bmoQuote.getCurrencyParity()))%>:
					</th>
					<td class="reportCellEven" align="left">
					<%	if (bmoQuote.getCoverageParity().toBoolean()) { %>
							<%= bmoQuote.getCurrencyParity().toHtml() %>
					<%	} else { %>
							NA
					<%	} %>
					</td>
				</tr>
				<tr>
					<th class="reportCellEven" align="left">
						<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoQuote.getProgramCode(), bmoQuote.getCustomerContactId()))%>:
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
						<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoQuote.getProgramCode(), bmoQuote.getUserId()))%>:
					</th>
					<td class="reportCellEven" align="left">
						<%= bmoUser.getFirstname().toHtml()%> 
						<%= bmoUser.getFatherlastname().toHtml()%>
						<%= bmoUser.getMotherlastname().toHtml()%>
					</td>
				</tr>
				<tr>
					<th class="reportCellEven" align="left">
						<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoOpportunity.getWFlowTypeId()))%>:
					</th>
					<td class="reportCellEven" align="left">
						<%= bmoOpportunity.getBmoWFlowType().getName().toHtml() %>
					</td>
					<th class="reportCellEven" align="left">
						<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoQuote.getProgramCode(), bmoQuote.getStatus()))%>:
					</th>
					<td class="reportCellEven" align="left">
						<%= bmoQuote.getStatus().getSelectedOption().getLabeltoHtml() %>
						(#<%= bmoQuote.getAuthNum().toString() %>)
					</td>
				</tr>
					<tr>
					<th class="reportCellEven" align="left">
					<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoQuote.getProgramCode(), bmoQuote.getDescription()))%>:
 
					</th>
					<td class="reportCellEven" align="left" colspan="3">
						<%= bmoQuote.getDescription().toHtml()%>
					</td>
				</tr>
				
			</table>
				<br>
			<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
				<tr>       
					<th class="reportHeaderCell" width="50%" colspan="3">Producto</th>
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
				BmoQuoteGroup bmoQuoteGroup = new BmoQuoteGroup();
				PmQuoteGroup pmQuoteGroup = new PmQuoteGroup(sFParams);
				BmFilter bmFilter = new BmFilter();
				bmFilter.setValueFilter(bmoQuoteGroup.getKind(), bmoQuoteGroup.getQuoteId().getName(), bmoQuote.getId());
				Iterator<BmObject> quoteGroups = pmQuoteGroup.list(bmFilter).iterator();
				int i = 1;
				while (quoteGroups.hasNext()) {
					bmoQuoteGroup = (BmoQuoteGroup)quoteGroups.next(); %>
					<tr>
						<td class="reportHeaderCell" colspan="6" style="white-space: normal">
							<%= i++ %>. <%= bmoQuoteGroup.getName().toHtml() %>
							<%	if (bmoQuoteGroup.getDescription().toString().length() > 0) { %>
                            		<br><%= bmoQuoteGroup.getDescription().toHtml() %>
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
	
					BmoQuoteItem bmoQuoteItem = new BmoQuoteItem();
					PmQuoteItem pmQuoteItem = new PmQuoteItem(sFParams);
					BmFilter bmFilterQ = new BmFilter();
					bmFilterQ.setValueFilter(bmoQuoteItem.getKind(), bmoQuoteItem.getQuoteGroupId().getName(), bmoQuoteGroup.getId());
					ArrayList list = pmQuoteItem.list(bmFilterQ);
					Iterator<BmObject> items = list.iterator();
					int index = 1, count = list.size();
					while(items.hasNext()) {
						bmoQuoteItem = (BmoQuoteItem)items.next();	
						%>
						<tr>	   	                      
							<% 	if (bmoQuoteItem.getProductId().toInteger() > 0) { %>
									<td class="reportCellEven" align ="left" colspan="3">  
									<% 	if(bmoQuoteItem.getBmoProduct().getDisplayName().toString().equals("")) { %>
											<%= bmoQuoteItem.getBmoProduct().getName().toHtml() %>
											<br><span class="documentSubText"><%= bmoQuoteItem.getDescription().toHtml() %></span>
									<% 	} else { %>
											<%= bmoQuoteItem.getBmoProduct().getDisplayName().toHtml() %>
									<% 	} %>
									</td>
									<td class="reportCellEven" align="left">
										<%= bmoQuoteItem.getBmoProduct().getDescription().toHtml() %>
									</td>
							<% 	} else { %>
									<td class="reportCellEven" align ="left" colspan="3">  
										<%= bmoQuoteItem.getName().toHtml() %>
									</td>
									<td class="reportCellEven" align="left">
										<%= bmoQuoteItem.getDescription().toHtml() %>
									</td>
							<% 	} %>
							
							<td class="reportCellEven" align="right">
							<% 	if (log) { %>
									<%= formatCurrency.format(bmoQuoteItem.getPrice().toDouble()) %>
							<% 	} else { %>
								<%	if (bmoQuoteGroup.getShowPrice().toBoolean()) { %>  
										<%= formatCurrency.format(bmoQuoteItem.getPrice().toDouble()) %>
								<% 	} %>   
							<%	}%>
							</td>
		
							<td class="reportCellEven" align="right">
							<% 	if (log) { %>
									<%= formatCurrency.format(bmoQuoteItem.getAmount().toDouble()) %>
							<% 	} else { %>
								<% 	if (bmoQuoteGroup.getShowPrice().toBoolean()) { %>  
										<%= formatCurrency.format(bmoQuoteItem.getAmount().toDouble()) %>
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
					<td colspan="4" rowspan="4"  valign="top" align="left" class="detailStart">
						<p class="documentComments">
						<b>
							<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoQuote.getProgramCode(), bmoQuote.getComments()))%>:
						</b> 
						<br> 
						<%= bmoQuote.getComments().toHtml() %></p>
					</td>                                  
					<td class="" align="right">
						<b>Subtotal:</b>
					</td>
					<td class="reportCellEven" align="right">
						<b><%=  formatCurrency.format(bmoQuote.getAmount().toDouble()) %>
					</td>
				</tr>
				<% 	if (bmoQuote.getDiscount().toDouble() > 0) { %>
						<tr>            
							<td class="" align="right">
								<b>Descuento:</b>
							</td>
		        			<td class="reportCellEven" align="right">
								<b><%= formatCurrency.format(bmoQuote.getDiscount().toDouble()) %></b>	
							</td>
						</tr> 
				<%	} %>
				<%	if (bmoQuote.getTax().toDouble() > 0) { %>
						<tr>            
							<td class="" align="right">
								<b>IVA:</b>
							</td>
			        			<td class="reportCellEven" align="right">
									<b><%= formatCurrency.format(bmoQuote.getTax().toDouble()) %></b>
								</td>
						</tr>
				<%	} %>
				<tr>                                                                        
					<td class="" align="right">
						<b>Total(<%= bmoQuote.getBmoCurrency().getCode().toString()%>):</b>
					</td>
					<td class="reportCellEven" align="right">
						<b>
							<%= formatCurrency.format(bmoQuote.getTotal().toDouble()) %>
						</b>
					</td>
				</tr>
			</table>
			<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0" style="font-size: 12px">
			    <tr align="center">
				    <td class="documentTitleCaption" align="right" height="40" width="40%">
				    	&nbsp;
			        </td>
			        <td class="documentTitleCaption" align="right" height="40">
						<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoQuote.getProgramCode(), bmoQuote.getAuthorizedUser()))%>:
			        	<b>
					        <%= HtmlUtil.stringToHtml(bmoUserAuthorized.getFirstname().toString()) %>
							<%= HtmlUtil.stringToHtml(bmoUserAuthorized.getFatherlastname().toString()) %>
							<%= HtmlUtil.stringToHtml(bmoUserAuthorized.getMotherlastname().toString()) %>							
						</b>
			        </td>
			        <td class="documentTitleCaption" align="right" height="40">
						<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoQuote.getProgramCode(), bmoQuote.getAuthorizedDate()))%>:
		        		<b>
			        		<% 	//if (log) { %>
				        		<%	//if (!authorizedDate.equals("")) { %>
										<%//= SFServerUtil.formatDate(sFParams, sFParams.getDateFormat(), sFParams.getBmoSFConfig().getPrintDateFormat().toString(), authorizedDate) %>
								<%	//} %>
							<%	//} else { %>
								<%	if (!bmoQuote.getAuthorizedDate().toString().equals("")) { %>
											<%= SFServerUtil.formatDate(sFParams, sFParams.getDateFormat(), sFParams.getBmoSFConfig().getPrintDateFormat().toString(), bmoQuote.getAuthorizedDate().toString()) %>
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
				
				response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);
		%>
			<%= errorException %>
	<%
		}
	%>

</body>
</html>
