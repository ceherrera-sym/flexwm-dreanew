<%@page import="com.flexwm.server.cm.PmQuoteItem"%>
<%@page import="com.flexwm.shared.cm.BmoQuoteItem"%>
<%@page import="com.flexwm.server.cm.PmQuoteGroup"%>
<%@page import="com.flexwm.shared.cm.BmoQuoteGroup"%>
<%@page import="com.flexwm.server.cm.PmQuote"%>
<%@page import="com.flexwm.server.cm.PmQuoteMainGroup"%>
<%@page import="com.flexwm.shared.cm.BmoQuoteMainGroup"%>
<%@page import="com.flexwm.server.cm.PmOpportunity"%>
<%@page import="com.flexwm.shared.cm.BmoOpportunity"%>
<%@page import="com.flexwm.shared.cm.BmoQuote"%>
<%@page import="java.text.NumberFormat"%>
<%@include file="../inc/login_opt.jsp" %>

<%
try {
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(Locale.US);
	String title = "Cotizaci&oacute;n";
	BmoQuote bmoQuote = new BmoQuote();
	PmQuote pmQuote = new PmQuote(sFParams);
	
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)sFParams.getBmoProgram(bmoQuote.getProgramCode());
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
		    <link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>">
		    <meta charset=utf-8>	
		</head>
		<body class="default" <%= permissionPrint %>>
			<%
			 	// Obtener parametros
			    int opportunityId = 0;
			    if (isExternal) opportunityId = decryptId;
		    	else opportunityId = Integer.parseInt(request.getParameter("foreignId")); 
			    
			    PmConn pmConn = new PmConn(sFParams);
			    
			    pmConn.open();
			    
			    BmoOpportunity bmoOpportunity = new BmoOpportunity();
				PmOpportunity pmOpportunity = new PmOpportunity(sFParams);
				bmoOpportunity = (BmoOpportunity)pmOpportunity.get(opportunityId);
				 // Empresa
				BmoCompany bmoCompany = new BmoCompany();
				PmCompany pmCompany = new PmCompany(sFParams);
				bmoCompany = (BmoCompany)pmCompany.get(bmoOpportunity.getCompanyId().toInteger());
				
				String logoURL ="";
				if (!bmoCompany.getLogo().toString().equals(""))
					logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
				else 
					logoURL = sFParams.getMainImageUrl();
				
				BmoCity bmoCompanyCity = new BmoCity();
				PmCity pmCity = new PmCity(sFParams);
				if (bmoCompany.getCityId().toInteger() > 0) 
					bmoCompanyCity = (BmoCity)pmCity.get(bmoCompany.getCityId().toInteger());
				
				bmoQuote = (BmoQuote)pmQuote.get(bmoOpportunity.getQuoteId().toInteger());
			%>
			<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px" >
				<tr >
				    <td align="left" width="6%" rowspan="6" valign="top">
						<img border="0" height="120" src="<%= logoURL %>" >
				    </td> 		
				</tr>							
				<tr>  
					<td  width="80%" align="right">					
				    	<b>Proyecto:&nbsp;</b>
				    </td>
				    <td  width="80%" align="left" style="border-bottom:none;font-weight:lighter;">
				    	<%= bmoOpportunity.getName().toHtml() %>
					</td>
				</tr>
				<tr>  
					<td  width="80%" align="right">					
				    	<b>Tipo:&nbsp;</b>
				    </td>
				    <td  width="80%" align="left" style="border-bottom:none;font-weight:lighter">
				    	<%= bmoOpportunity.getBmoWFlowType().getBmoWFlowCategory().getName().toString() %>
					</td>
				</tr>
				<tr>  
					<td  width="80%" align="right">					
				    	<b>Fecha evento:&nbsp;</b>
				    </td>
				    <td  width="80%" align="left" style="border-bottom:none;font-weight:lighter">
				    	<%= bmoOpportunity.getStartDate().toString().substring(0, 10) %>
					</td>
				</tr>
				<tr>  
					<td  width="80%" align="right">					
				    	<b>Ciudad:&nbsp;</b>
				    </td>
				     <td  width="80%" align="left" style="border-bottom:none;font-weight:lighter">
				     	<% if (bmoOpportunity.getBmoVenue().getHomeAddress().toBoolean()) { %>
				     		Domicilio Particular
				     	<% } else {%>
				    		<%= bmoOpportunity.getBmoVenue().getBmoCity().getName().toHtml() %>
				    	<% } %>
					</td>
				</tr>
				<tr>  
					<td  width="80%" align="right">					
				    	<b>Locaci&oacute;n:&nbsp;</b>
				    </td>
				    <td  width="80%" align="left" style="border-bottom:none;font-weight:lighter">
				    	<% if (bmoOpportunity.getBmoVenue().getHomeAddress().toBoolean()) { %>
				    		<%= bmoOpportunity.getCustomField4().toHtml() %>
				    	<% } else { %>
				    		<%= bmoOpportunity.getBmoVenue().getName()%>
				    	<% } %>
					</td>
				</tr>
			</table>
			<br>
			<table border="0" cellspacing="0" width="100%" cellpadding="0"  style="font-size: 12px">
				<%
					BmoQuoteMainGroup bmoQuoteMainGroup = new BmoQuoteMainGroup();
					PmQuoteMainGroup pmQuoteMainGroup = new PmQuoteMainGroup(sFParams);
					BmFilter quotebmFilter = new BmFilter();
					
					quotebmFilter.setValueFilter(bmoQuoteMainGroup.getKind(), bmoQuoteMainGroup.getQuoteId(), bmoQuote.getId());
					
					Iterator<BmObject> mainGroupsIterator = pmQuoteMainGroup.list(quotebmFilter).iterator();
					
					ArrayList<BmObject> mainGroupsList = new ArrayList<BmObject>();
					while (mainGroupsIterator.hasNext()){
						BmoQuoteMainGroup nextBmoQuoteMainGroup = (BmoQuoteMainGroup)mainGroupsIterator.next();
						mainGroupsList.add(nextBmoQuoteMainGroup);
						
						String sql = "SELECT qogr_quotegroupid FROM quotegroups WHERE qogr_maingroupid = " + nextBmoQuoteMainGroup.getId();
						
						pmConn.doFetch(sql);
						
						if (pmConn.next()) {
				%>
							<tr>
								<td class="reportHeaderCell" style="background-color:#ccc;font-weight:lighter">
									<%= nextBmoQuoteMainGroup.getName().toHtml() %>
								</td>
								<td class="reportHeaderCellRight"  style="background-color:#ccc;">
									
									<%= SFServerUtil.formatCurrency( nextBmoQuoteMainGroup.getTotal().toDouble())%>
								</td>
							</tr>
							<tr>
								<td height="10"></td>
							</tr>
							
							<tr>
								<td align="right" colspan="2" >
									<table border="0" cellspacing="0" width="98%" cellpadding="0"  style="font-size: 12px">
										<tr>
											<td class="reportHeaderCell" width="15%" style="font-weight:lighter">Nombre</td>
											<td class="reportHeaderCell" width="25%" style="font-weight:lighter;max-width: 30%;min-width: 30%">Descripci&oacute;n</td>
											<td class="reportHeaderCell" width="15%" style="font-weight:lighter">Cantidad</td>
											<td class="reportHeaderCell" width="15%" style="font-weight:lighter">Dias</td>
											<td class="reportHeaderCellRight" width="15%" style="font-weight:lighter">Precio Unitario</td>
											<td class="reportHeaderCellRight" width="15%" style="font-weight:lighter">Descuento</td>
											<td class="reportHeaderCellRight" width="15%" style="font-weight:lighter">Total</td>
										</tr>
										<%
											BmoQuoteGroup bmoQuoteGroup = new BmoQuoteGroup();
											PmQuoteGroup pmQuoteGroup = new PmQuoteGroup(sFParams);
											BmFilter groupsFilter = new BmFilter();
											
											groupsFilter.setValueFilter(bmoQuoteGroup.getKind(), bmoQuoteGroup.getMainGroupId(), nextBmoQuoteMainGroup.getId());
											
											Iterator<BmObject> quoteGroupsIterator = pmQuoteGroup.list(groupsFilter).iterator();
											
											while (quoteGroupsIterator.hasNext()){
												BmoQuoteGroup nextBmoQuoteGroup = (BmoQuoteGroup)quoteGroupsIterator.next();
										%>&nbsp;
												<tr>
													<td height="5"></td>
												</tr>
												<tr>
													 <td class="reportCellEven" style=" border-bottom: 1px solid #000000;">
													 	<b><%=nextBmoQuoteGroup.getName().toHtml() %>
														 	<% if (nextBmoQuoteGroup.getIsKit().toBoolean()) { %>
														 		(<%= nextBmoQuoteGroup.getDays().toDouble()  %>
														 			<% if (nextBmoQuoteGroup.getDays().toDouble() > 1) { %>
														 				D&iacute;as
														 			<% } else {%>
														 				D&iacute;a
														 			<% } %>
														 		)
														 	
														 	<% } %>
													 	</b>
													 </td>
													 <td class="reportCellEven" colspan="5" style=" border-bottom: 1px solid #000000;"></td>
													 <% if (nextBmoQuoteGroup.getIsKit().toBoolean()) { %>
													 	<td class="reportCellEven" align="right" style=" border-bottom: 1px solid #000000;" ><b><%=SFServerUtil.formatCurrency(nextBmoQuoteGroup.getTotal().toDouble()) %></b></td>
													 <% } else { %>
													 	<td class="reportCellEven" align="right" style=" border-bottom: 1px solid #000000;" ><b><%=SFServerUtil.formatCurrency(nextBmoQuoteGroup.getAmount().toDouble()) %></b></td>
													 <% } %>
												</tr>
												<% if (nextBmoQuoteGroup.getIsKit().toBoolean()) {%>
													<tr>
														<td class="reportCellEven" style=" border-bottom: 1px solid #000000;" colspan="7">
															<%= nextBmoQuoteGroup.getDescription().toHtml() %>
														</td>
													</tr>
												<% } %>
											<%		
												BmoQuoteItem bmoQuoteItem = new BmoQuoteItem();
												PmQuoteItem pmQuoteItem = new PmQuoteItem(sFParams);
												BmFilter itemsBmFilter = new BmFilter();
												itemsBmFilter.setValueFilter(bmoQuoteItem.getKind(), bmoQuoteItem.getQuoteGroupId(), nextBmoQuoteGroup.getId());
												
												Iterator<BmObject> itemIterator = pmQuoteItem.list(itemsBmFilter).iterator();
												
												 if (!nextBmoQuoteGroup.getIsKit().toBoolean()) { 											
													while (itemIterator.hasNext()){
														BmoQuoteItem nextBmoQuoteItem = (BmoQuoteItem)itemIterator.next();
											%>
														<tr>
															<td class="reportCellEven"><%=nextBmoQuoteItem.getName().toHtml() %></td>
															<td class="reportCellEven"><%=nextBmoQuoteItem.getDescription().toHtml() %></td>
															<td class="reportCellEven"><%=nextBmoQuoteItem.getQuantity().toDouble() %></td>
															<td class="reportCellEven"><%=nextBmoQuoteItem.getDays().toDouble() %></td>
															<td class="reportCellEven" align="right"><%= SFServerUtil.formatCurrency(nextBmoQuoteItem.getPrice().toDouble()) %></td>
															<td class="reportCellEven" align="right"><%= SFServerUtil.formatCurrency(nextBmoQuoteItem.getDiscount().toDouble()) %></td>
															<td class="reportCellEven" align="right"><%= SFServerUtil.formatCurrency(nextBmoQuoteItem.getAmount().toDouble()) %></td>
														</tr>
											<%	
													}
												 } else {
													 while (itemIterator.hasNext()){
														BmoQuoteItem nextBmoQuoteItem = (BmoQuoteItem)itemIterator.next();
															
														if (nextBmoQuoteGroup.getShowItems().toBoolean()) {
											%>
															<tr>
																<td class="reportCellEven"><%=nextBmoQuoteItem.getName().toHtml() %></td>
																<td class="reportCellEven"><%=nextBmoQuoteItem.getDescription().toHtml() %></td>
																<td class="reportCellEven"><%=nextBmoQuoteItem.getQuantity().toDouble() %></td>
																<td class="reportCellEven"><%=nextBmoQuoteItem.getDays().toDouble() %></td>
																<td class="reportCellEven" align="right"><%= SFServerUtil.formatCurrency(nextBmoQuoteItem.getPrice().toDouble()) %></td>
																<td class="reportCellEven" align="right"><%= SFServerUtil.formatCurrency(nextBmoQuoteItem.getDiscount().toDouble()) %></td>
																<td class="reportCellEven" align="right"><%= SFServerUtil.formatCurrency(nextBmoQuoteItem.getAmount().toDouble()) %></td>
															</tr>
											<%	
														}
													 }
													 
												 }
											}
										%>
									</table>
								</td>
							</tr>		
							<tr>
								<td height="15">		
								</td>
							</tr>
				<%
						}
					}
				%>				
			</table>
			<br>
			<br>
			<table border="0" cellspacing="0" width="100%" cellpadding="0"  style="font-size: 12px;" >
				<tr>
					<td class="reportHeaderCellCenter" style="background-color:#ccc;" width="80%" ">
						Resumen
					</td>
					<td class="reportHeaderCellCenter" style="background-color:#ccc;" align="right">
						Sub total grupos 
					</td>
				</tr>
				<%
					Iterator<BmObject> mainGroupsIterator2 = mainGroupsList.iterator();
						while(mainGroupsIterator2.hasNext()){
							BmoQuoteMainGroup nextBmoQuoteMainGroup = (BmoQuoteMainGroup)mainGroupsIterator2.next();
							String sql = "SELECT qogr_quotegroupid FROM quotegroups WHERE qogr_maingroupid = " + nextBmoQuoteMainGroup.getId();
							
							pmConn.doFetch(sql);
							
							if (pmConn.next()){
									boolean discount = false, commision = false,feeProduction = false;
									PmConn pmConn2 = new PmConn(sFParams);
									pmConn2.open();
									
				%>
								<tr>
									<td>
										<table border="0" cellspacing="0" width="100%" cellpadding="0"  style="font-size: 12px;" >
											<tr>
												<th class="reportCellEven" align="right"  style="padding: 0px 0px;border-bottom: 0px;"><%= nextBmoQuoteMainGroup.getName().toHtml()  %>&nbsp;
												</th>								
											</tr>
											<%
												sql = "SELECT qogr_quotegroupid FROM quotegroups "
													 	 + " WHERE qogr_maingroupid = " + nextBmoQuoteMainGroup.getId() + " AND qogr_discountapplies = 1 ";
												pmConn2.doFetch(sql);
												
												if (pmConn2.next()){
													discount = true;
											%>
													<tr>
														<th class="reportCellEven" align="right"   style="padding: 0px 0px;border-bottom: 0px;">	Descuento&nbsp;
														</th>								
													</tr>
											<%
												}
											%>
											<%
												sql = "SELECT qogr_quotegroupid FROM quotegroups "
													 	 + " WHERE qogr_maingroupid = " + nextBmoQuoteMainGroup.getId() + " AND qogr_feeproductionrate = 1 ";
												pmConn2.doFetch(sql);
											
												if (pmConn2.next()){
													feeProduction = true;
											%>
													<tr>
														<th class="reportCellEven" align="right"  style="padding: 0px 0px;border-bottom: 0px;">	Fee de producci&oacute;n&nbsp;
														</th>								
													</tr>
											<%
												}
											%>
											<%
												
												sql = "SELECT qogr_quotegroupid FROM quotegroups "
												 	 + " WHERE qogr_maingroupid = " + nextBmoQuoteMainGroup.getId() + " AND qogr_comissionapply = 1 ";
												pmConn2.doFetch(sql);
												
												if (pmConn2.next()){
													commision = true;
													if(nextBmoQuoteMainGroup.getShowCommission().toBoolean()){
											%>
														<tr>
															<th class="reportCellEven" align="right"  style="padding: 0px 0px;border-bottom: 0px;" >	Comisi&oacute;n&nbsp;
															</th>								
														</tr>
												
											<%
													}
												}
											%>
											<tr>
												<th class="reportCellEven" align="right"   style="padding: 0px 0px;">SubTotal de <%= nextBmoQuoteMainGroup.getName().toHtml() %>
												</th>								
											</tr>
										</table>
									</td>
									<td>
										<table border="0" cellspacing="0" width="100%" cellpadding="0"  style="font-size: 12px;" >
											<tr>
												<td class="reportCellEven" align="right"  style="padding: 0px 0px;"><%= SFServerUtil.formatCurrency(nextBmoQuoteMainGroup.getAmount().toDouble()) %>
												</td>								
											</tr>
											<% if (discount) { %>
												<tr>
													<td class="reportCellEven" align="right"   style="padding: 0px 0px;"><%=  SFServerUtil.formatCurrency(nextBmoQuoteMainGroup.getDiscount().toDouble()) %> 
													</td>								
												</tr>
											<% } %>
											<% if (feeProduction) {%>
												<tr>
													<td class="reportCellEven" align="right"   style="padding: 0px 0px;"><%=  SFServerUtil.formatCurrency(nextBmoQuoteMainGroup.getProductionFee().toDouble()) %> 
												</tr>
											<% } %>
											<% if (commision) { %>
												<% if(nextBmoQuoteMainGroup.getShowCommission().toBoolean()){ %>
													<tr>
														<td class="reportCellEven" align="right"   style="padding: 0px 0px;"><%=  SFServerUtil.formatCurrency(nextBmoQuoteMainGroup.getCommission().toDouble()) %> 
														</td>								
													</tr>
												<% } %>
											<% } %>
											<tr>
												<td class="reportCellEven" align="right"   style="padding: 0px 0px;">
													<%=  SFServerUtil.formatCurrency(nextBmoQuoteMainGroup.getAmount().toDouble() - nextBmoQuoteMainGroup.getDiscount().toDouble() + nextBmoQuoteMainGroup.getProductionFee().toDouble() -nextBmoQuoteMainGroup.getCommission().toDouble() ) %>
												</td>								
											</tr>
										</table>
									</td>
								</tr>				
				<%
								pmConn2.close();
							}
						}
				%>
				<tr>
					<td class="reportHeaderCellRight" style="background-color:#ccc;" width="90%" ">
						Total Cotizado&nbsp;
					</td>
					<td class="reportHeaderCellRight" style="background-color:#ccc;" >
						<%=SFServerUtil.formatCurrency(bmoQuote.getTotal().toDouble()) %>
					</td>
				</tr>
				
			</table>
			
			

			
			<%
				pmConn.close();
			%>
		</body>
	</html>
<% } catch (Exception e) { 
			String errorLabel = "Error de Cotizacion";
			String errorText = "La Cotizacion no puede ser desplegada.";
			String errorException = e.toString();
%>
	
	<%= errorException %>
	
<% } %>