<%@page import="com.symgae.shared.GwtUtil"%>
<%@page import="com.flexwm.server.cm.PmPayCondition"%>
<%@page import="com.flexwm.shared.cm.BmoPayCondition"%>
<%@page import="com.flexwm.server.cm.PmCustomerContact"%>
<%@page import="com.flexwm.shared.cm.BmoCustomerContact"%>
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
<%@page import="com.flexwm.server.cm.PmOpportunity"%>
<%@page import="com.flexwm.shared.cm.BmoOpportunity"%>
<%@page import="com.flexwm.server.cm.PmOpportunityDetail"%>
<%@page import="com.flexwm.shared.cm.BmoOpportunityDetail"%>
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="com.flexwm.shared.wf.BmoWFlowType"%>
<%@page import="com.flexwm.shared.op.BmoOrderType"%>
<%@page import="com.flexwm.server.wf.PmWFlowType"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.symgae.server.sf.PmCompany"%>
<%@page import="com.symgae.shared.sf.BmoCompany"%>
<%@include file="../inc/login_opt.jsp" %>


<%
try {
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(Locale.US);

	String title = "Cotizaci&oacute;n";

	BmoQuote bmoQuote = new BmoQuote();
	
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
	
	<body class="default" <%= permissionPrint %> >
	
	<%
			PmConn pmConnOppoDet= new PmConn(sFParams);
			pmConnOppoDet.open();
			boolean opde = false;
			
		    // Obtener parametros
		    int opportunityId = 0;
		    if (isExternal) opportunityId = decryptId;
		    else opportunityId = Integer.parseInt(request.getParameter("foreignId"));    
		
		    PmConn pmConn = new PmConn(sFParams);
		    
			BmoOpportunity bmoOpportunity = new BmoOpportunity();
			PmOpportunity pmOpportunity = new PmOpportunity(sFParams);
			bmoOpportunity = (BmoOpportunity)pmOpportunity.get(opportunityId);
			
			// Empresa
			BmoCompany bmoCompany = new BmoCompany();
			PmCompany pmCompany = new PmCompany(sFParams);
			bmoCompany = (BmoCompany)pmCompany.get(bmoOpportunity.getCompanyId().toInteger());

			// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
			String logoURL ="";
// 			if (!bmoCompany.getLogo().toString().equals(""))
// 				logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
// 			else 
				logoURL = sFParams.getMainImageUrl();
			
			BmoCity bmoCompanyCity = new BmoCity();
			PmCity pmCity = new PmCity(sFParams);
			if (bmoCompany.getCityId().toInteger() > 0) 
				bmoCompanyCity = (BmoCity)pmCity.get(bmoCompany.getCityId().toInteger());
			
			BmoOpportunityDetail bmoOpportunityDetail = new BmoOpportunityDetail();
			PmOpportunityDetail pmOpportunityDetail = new PmOpportunityDetail(sFParams);
			
			String sqlOpde = "SELECT opde_opportunityid FROM opportunitydetails WHERE opde_opportunityid = " + bmoOpportunity.getId(); 
			pmConnOppoDet.doFetch(sqlOpde);
			if(pmConnOppoDet.next()) opde = true;
			if(opde)
				bmoOpportunityDetail = (BmoOpportunityDetail)pmOpportunityDetail.getBy(opportunityId, bmoOpportunityDetail.getOpportunityId().getName());
					
			pmConnOppoDet.close();
			
			BmoWFlowType bmoProjectWFlowType = new BmoWFlowType();
			PmWFlowType pmWFlowType = new PmWFlowType(sFParams);
			if(bmoOpportunity.getForeignWFlowTypeId().toInteger() > 0)
				bmoProjectWFlowType = (BmoWFlowType)pmWFlowType.get(bmoOpportunity.getForeignWFlowTypeId().toInteger());
						
			PmQuote pmQuote = new PmQuote(sFParams);
			
			// Si es llamada externa, utilizar llave desencriptada, en caso contrario utilizar llave de la oportunidad
			bmoQuote = (BmoQuote)pmQuote.get(bmoOpportunity.getQuoteId().toInteger());
			
			// Obtener cliente
			String customer = "";
			if (bmoOpportunity.getBmoCustomer().getCustomertype().toChar() == BmoCustomer.TYPE_PERSON) {
				customer =  bmoOpportunity.getBmoCustomer().getCode().toHtml() + " " +
							bmoOpportunity.getBmoCustomer().getFirstname().toHtml() + " " +
							bmoOpportunity.getBmoCustomer().getFatherlastname().toHtml() + " " +
							bmoOpportunity.getBmoCustomer().getMotherlastname().toHtml();
			} else {
				customer = bmoOpportunity.getBmoCustomer().getCode().toHtml() + " " +
							bmoOpportunity.getBmoCustomer().getLegalname().toHtml();
			}
			
			// Obtener Direccion del evento
			String venue = "", venueDir =  "";
			// direccion cliente
// 			BmoCustomerAddress bmoCustomerAddress = new BmoCustomerAddress();
// 			PmCustomerAddress pmCustomerAddress = new PmCustomerAddress(sFParams);
			// direccion del salon
			BmoVenue bmoVenue = new BmoVenue();
			PmVenue pmVenue = new PmVenue(sFParams);
			
			// Si viene de un salon tomar datos, sino de la direccion del cliente
			if (bmoOpportunity.getVenueId().toInteger() > 0) {
				bmoVenue = (BmoVenue)pmVenue.get(bmoOpportunity.getVenueId().toInteger());
				if (bmoVenue.getHomeAddress().toInteger() > 0) {
					venue = bmoVenue.getName().toHtml() + ": " +bmoOpportunity.getCustomField4().toString();
				} else {
					venue = bmoVenue.getName().toHtml() + ": " +
							bmoVenue.getStreet().toHtml() + " " +
							bmoVenue.getNumber().toHtml() + " " +
							bmoVenue.getNeighborhood().toHtml() + ". ";
							
					venueDir = bmoVenue.getBmoCity().getName().toHtml() + ", " +
							bmoVenue.getBmoCity().getBmoState().getCode().toHtml() + ", " +
							bmoVenue.getBmoCity().getBmoState().getBmoCountry().getCode().toHtml() + ".";
				}
			} 
// 			else if (bmoOpportunity.getCustomerAddressId().toInteger() > 0) {
// 				bmoCustomerAddress = (BmoCustomerAddress)pmCustomerAddress.get(bmoOpportunity.getCustomerAddressId().toInteger());
				
// 				venue = bmoCustomerAddress.getStreet().toHtml() + ": " +
// 						bmoCustomerAddress.getNumber().toHtml() + " " +
// 						bmoCustomerAddress.getNeighborhood().toHtml() + ". ";
						
// 				venueDir = bmoCustomerAddress.getBmoCity().getName().toHtml() + ", " +
// 						bmoCustomerAddress.getBmoCity().getBmoState().getCode().toHtml() + ", " +
// 						bmoCustomerAddress.getBmoCity().getBmoState().getBmoCountry().getCode().toHtml() + ".";
// 			}	
			
			if (!(bmoOpportunity.getWFlowTypeId().toInteger() > 0)) throw new Exception("La oportunidad no cuenta con el Tipo de Efecto(flujo)- no se puede desplegar.");
			
			//if (bmoQuote.getStatus().toChar() != BmoQuote.STATUS_AUTHORIZED) throw new Exception("La Cotizacion no esta autorizada - no se puede desplegar.");
			
	%>
	
	
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		<tr>
		    <td align="left" width="6%" rowspan="2" valign="top">
				<img border="0" width="140" height="38" src="<%=GwtUtil.getProperUrl(sFParams, "./img/logo.jpg") %>">
		    </td> 
			<td class="contracSubTitle" align="center">
<%-- 			    <b><%= bmoCompany.getLegalName().toString().toUpperCase() %></b> --%>
				<b>VISUAL M&Eacute;XICO</b>
			</td >
			<td align="right" width="6%" valign="top" rowspan="2" >
				<img border="0" width="110px" height="25px" src="<%=GwtUtil.getProperUrl(sFParams, "./img/infor.png") %>">
			</td>	
			
		</tr>
		<tr>  
			<td class="contractTitleCaption" align="center" >
					<%= bmoCompany.getStreet().toHtml() %> <%= bmoCompany.getNumber().toHtml() %><br>
			        <%= bmoCompany.getNeighborhood().toHtml() %>, C.P. <%= bmoCompany.getZip().toHtml() %><br>
			        <%= bmoCompanyCity.getName().toHtml() %>, <%= bmoCompanyCity.getBmoState().getName().toHtml() %>, <%= bmoCompanyCity.getBmoState().getBmoCountry().getName().toHtml() %>.<br>
			       	TEL: <%= bmoCompany.getPhone().toHtml() %><br>
			        <b><%= bmoCompany.getWww().toHtml() %></b> 
			</td>	
					
		</tr>
	</table>
	<br>
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		<tr>
		    <td colspan="4" class="reportHeaderCell" style="text-align: center;" width="100%">
		        COTIZACI&Oacute;N
		    </td>
        </tr>
        <tr>
	        <th align = "left" class="reportCellEven" width="10%" >Empresa/Cliente:</th>
	        <td class="reportCellEven">
	        	<%= customer %>
	        </td>
<!-- 	        <th align = "left" class="reportCellEven" width="10%">Fecha/Hora: </th> -->
<!-- 	        <td class="reportCellEven" width="10%"> -->
<%-- 		        <% if (!bmoOpportunity.getStartDate().toString().equals("")) { %> --%>
<%-- 		  			de: <%= SFServerUtil.formatDate(sFParams, sFParams.getDateTimeFormat(), sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString(), bmoOpportunity.getStartDate().toString()).substring(0, 11) %>      				 --%>
<%-- 		        <% } %> --%>
<%-- 		        <% if (!bmoOpportunity.getEndDate().toString().equals("")) { %> --%>
<%-- 					&nbsp;a:&nbsp;<%= SFServerUtil.formatDate(sFParams, sFParams.getDateTimeFormat(), sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString(), bmoOpportunity.getEndDate().toString()).substring(0, 11) %> --%>
<%-- 				<% } %>	         --%>
<!-- 			</td> -->
			<th align = "left" class="reportCellEven"  width="10%" >Tipo:</th>
	        <td class="reportCellEven" width="20%">
            	<%=bmoOpportunity.getBmoWFlowType().getName().toHtml() %>
			</td>
        </tr>
        <tr>
	        <th align = "left" class="reportCellEven">Nombre:</th>
	        <td class="reportCellEven">
            	<%= bmoOpportunity.getCode().toHtml() + " - " + bmoOpportunity.getName().toHtml() %> 
	        </td>
<!-- 	        <th align = "left" class="reportCellEven" >Tipo:</th> -->
<!-- 	        <td class="reportCellEven"> -->
<%--             	<%= bmoProjectWFlowType.getName().toHtml() %> --%>
<!-- 			</td> -->
			<th class="reportCellEven" align = "left">
				Fecha Cotizaci&oacute;n:
			</th>
			<td class="reportCellEven">
				<%= SFServerUtil.nowToString(sFParams, sFParams.getBmoSFConfig().getPrintDateFormat().toString()) %>
			</td>
	    </tr>
		<tr>
			<th class="reportCellEven" align = "left">
				Ejecutivo Comercial: 
			</th>
			<td class="reportCellEven">
				<%= bmoOpportunity.getBmoUser().getFirstname().toHtml()%> 
				<%= bmoOpportunity.getBmoUser().getFatherlastname().toHtml()%>
				<%= bmoOpportunity.getBmoUser().getMotherlastname().toHtml()%>
			</td>
<!-- 			<th class="reportCellEven" align = "left"> -->
<!-- 				Fecha Cotizaci&oacute;n: -->
<!-- 			</th> -->
<!-- 			<td class="reportCellEven"> -->
<%-- 				<%= SFServerUtil.nowToString(sFParams, sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString()) %> --%>
<!-- 			</td> -->
			<th class="reportCellEven" align = "left">
				Estatus:
			</th>
			<td class="reportCellEven">
				<%= bmoQuote.getStatus().getSelectedOption().getLabeltoHtml() %>
				(#<%= bmoQuote.getAuthNum().toString() %>)
			</td>
		</tr>
		<tr>
			<th class="reportCellEven" align = "left">
				Condiciones de pago generales:
			</th>
			
			<td class="reportCellEven">
				<%
				PmConn pmConnPayCondition = new PmConn(sFParams);
				pmConnPayCondition.open();
				String sqlPayCondition = "SELECT qogr_quotegroupid FROM quotegroups WHERE qogr_quoteid = " + bmoQuote.getId() + " AND qogr_payconditionid > 0";
				System.out.println("CONSULTA SSQL FORMATO " + sqlPayCondition);
				pmConnPayCondition.doFetch(sqlPayCondition);
				if(pmConnPayCondition.next()){%>
					Ver condiciones de pago de cada rubro
				<%	
				}else{
					if(bmoQuote.getPayConditionId().toInteger() > 0){
						BmoPayCondition bmoPayCondition = new BmoPayCondition();
						PmPayCondition pmPayCondition = new PmPayCondition(sFParams);
						bmoPayCondition = (BmoPayCondition)pmPayCondition.get(bmoQuote.getPayConditionId().toInteger());%>
						<%if(!bmoPayCondition.getDescription().toString().equals("") ){ %>
							<%=bmoPayCondition.getDescription().toString() %>
						<%}else{ %>
							<%=bmoPayCondition.getCode().toString() %>
						<%} %>
			 		<% }else{%>
						&nbsp;
					<% } 
				}
				pmConnPayCondition.close();%>
			</td>
<!-- 			<th class="reportCellEven" align = "left"> -->
<!-- 				Estatus: -->
<!-- 			</th> -->
<!-- 			<td class="reportCellEven"> -->
<%-- 				<%= bmoQuote.getStatus().getSelectedOption().getLabeltoHtml() %> --%>
<%-- 				(#<%= bmoQuote.getAuthNum().toString() %>) --%>
<!-- 			</td> -->
			<th class="reportCellEven" align = "left">
				<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoOpportunity.getProgramCode().toString(), bmoOpportunity.getExpireDate())) + ":"%>
			</th>
			<td class="reportCellEven">
				<%=SFServerUtil.dateToString(SFServerUtil.stringToDate(sFParams.getDateFormat(), 
						bmoOpportunity.getExpireDate().toString()), sFParams.getBmoSFConfig().getPrintDateFormat().toString())%>			
			</td>
		</tr>
		<tr>
			<th class="reportCellEven" align = "left">
				At&acute;n:
			</th>			
			
			 <td class="reportCellEven">
			 	<%if(bmoOpportunity.getCustomerContactId().toInteger() > 0){ %>
			 		<%BmoCustomerContact bmoCustomerContact = new BmoCustomerContact();
			 		 PmCustomerContact pmCustomerContact = new PmCustomerContact(sFParams);			  
			  		 bmoCustomerContact = (BmoCustomerContact)pmCustomerContact.get(bmoOpportunity.getCustomerContactId().toInteger());%>
				<%=bmoCustomerContact.getFullName().toString() + " "+bmoCustomerContact.getFatherLastName()+" / " + bmoCustomerContact.getPosition().toString()  %>
				<%} %>
			</td>
			<th class="reportCellEven" align = "left">
				Moneda:
			</th>
			<td class="reportCellEven">
				<%=bmoQuote.getBmoCurrency().getCode() + " : " + bmoQuote.getBmoCurrency().getName()  %>		
			</td>
<!-- 			<th class="reportCellEven" align = "left"> -->
<%-- 				<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoOpportunity.getProgramCode().toString(), bmoOpportunity.getExpireDate())) + ":"%> --%>
<!-- 			</th> -->
<!-- 			<td class="reportCellEven"> -->
<%-- 				<%=bmoOpportunity.getExpireDate().toString() %> --%>
<!-- 			</td> -->
		</tr>
	</table>
	<br>
	<table border="0" cellspacing="0" width="100%" cellpadding="0"  style="font-size: 12px">
<!-- 	    <tr>        -->
<!-- 	        <td class="reportHeaderCell" align="left"  width="50%" colspan="3">Rubro</td> -->
<!-- 	        <td class="reportHeaderCellCenter"  width="10%">Cantidad</td> -->
<%-- 	        <% if (bmoQuote.getBmoOrderType().getType().equals("" + BmoOrderType.TYPE_RENTAL)) { %> --%>
<!-- 	        	<td class="reportHeaderCellCenter" width="10%">D&iacute;as</td> -->
<%-- 	        <% } else { %> --%>
<!-- 	        	<td class="reportHeaderCell" align="left" width="10%">&nbsp;</td>	         -->
<%-- 	        <% } %> --%>
<!-- 	        <td class="reportHeaderCellRight" width="10%">Precio&nbsp;&nbsp;&nbsp;</td> -->
<!-- 	        <td class="reportHeaderCellRight" width="10%">Subtotal&nbsp;&nbsp;&nbsp;</td> -->
<!-- 	        <td class="reportHeaderCellRight" width="10%"colspan="">Total&nbsp;&nbsp;</td> -->
<!-- 	    </tr> -->
		 <tr>       	     
	         <td class="reportHeaderCell"  width="40%">Descripci&oacute;n</td>
	        <td class="reportHeaderCellCenter"  width="10%">Cantidad</td>
	        <td class="reportHeaderCellCenter" width="8%"> UM</td>
	        <% if (bmoQuote.getBmoOrderType().getType().equals("" + BmoOrderType.TYPE_RENTAL)) { %>
	        	<td class="reportHeaderCellCenter" width="1%">D&iacute;as</td>
	        <% } else { %>
	        	<td class="reportHeaderCell" align="left" width="1%">&nbsp;</td>	        
	        <% } %>
	        <td class="reportHeaderCellRight" width="8%">Precio Unit&nbsp;&nbsp;&nbsp;</td>
	        <td class="reportHeaderCellRight" width="8%" colspan="2">Subtotal&nbsp;&nbsp;&nbsp;</td>
<!-- 	        <td class="reportHeaderCellRight" width="10%"colspan="">Total&nbsp;&nbsp;</td> -->
	    </tr>
	    <tr>
	        <td colspan="7">&nbsp;</td>
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
	              double sumQuantity = 0;
	              while (quoteGroups.hasNext()) { 
	            	  double sumGroup = 0;
	                   bmoQuoteGroup = (BmoQuoteGroup)quoteGroups.next();%>
	                    <tr>
	                        <td class="reportHeaderCell" colspan="7" style="white-space: normal">
	                            <%= i++ %>. <%= bmoQuoteGroup.getName().toHtml() %>
	                           <%	if (bmoQuoteGroup.getDescription().toString().length() > 0) { %>
	                            		<br><%= bmoQuoteGroup.getDescription().toHtml() %>
	                            <%	} %>
	                            <b>&nbsp;<%=bmoQuoteGroup.getPayConditionId().getLabel() + ":" %></b>
	                            <%if(bmoQuoteGroup.getPayConditionId().toInteger() > 0){ 
	                            	BmoPayCondition bmoPayCondition = new BmoPayCondition();
	                            	PmPayCondition pmPayCondition = new PmPayCondition(sFParams);
	                            	bmoPayCondition = (BmoPayCondition)pmPayCondition.get(bmoQuoteGroup.getPayConditionId().toInteger());%>
	                            	 &nbsp;
	                            	 <%if(!bmoPayCondition.getDescription().equals("")){ %>
	                            	 	<%=bmoPayCondition.getDescription().toString() %>
	                            	 <%}else{ %>
	                            	 	<%=bmoPayCondition.getCode().toString()%>
	                            	 <%} %>
	                            <%}%>    
	                        </td>	                       
<!-- 	                        <td class="reportHeaderCellRight" colspan=""> -->
<%-- 	                        	<%if (bmoQuoteGroup.getShowAmount().toBoolean()) { %>                  	 --%>
<%-- 		                            <%= formatCurrency.format(bmoQuoteGroup.getAmount().toDouble()) %>&nbsp; --%>
<%-- 	                            <%} %> --%>
<!-- 	                        </td> -->
	                    </tr> 
	             <%	  
	                // Si es Kit solo pone descripcion
	             	//if (bmoQuoteGroup.getIsKit().toBoolean()) {
	             	
	             	%><!--
	             		<tr>
	             			<td class="reportCellEven" colspan="7">
	                            &nbsp;<%//= bmoQuoteGroup.getDescription().toHtml() %>
	                        </td>
		                  <td align ="right">
		                       &nbsp;
							<%// if (bmoQuoteGroup.getShowGroupImage().toBoolean() && (bmoQuoteGroup.getImage().toString().length() > 0)) { 
								//String blobKeyParse = bmoQuoteGroup.getImage().toString();
								//if (bmoQuoteGroup.getImage().toString().indexOf(".") > 0)
									//blobKeyParse = bmoQuoteGroup.getImage().toString().substring(0, bmoQuoteGroup.getImage().toString().indexOf("."));
							%> 
		                    	<img src="/fileserve?blob-key=<%//= blobKeyParse %>" width="300" height="200" padding="4"> 
		                  	<%// } %> 
		                  </td> 
						</tr>-->
	             	<%
	             	//} else {
	             
	            	  BmoQuoteItem bmoQuoteItem = new BmoQuoteItem();
		              PmQuoteItem pmQuoteItem = new PmQuoteItem(sFParams);
		              BmFilter bmFilterQ = new BmFilter();
		              bmFilterQ.setValueFilter(bmoQuoteItem.getKind(), bmoQuoteItem.getQuoteGroupId().getName(), bmoQuoteGroup.getId());
		              ArrayList<BmObject> list = pmQuoteItem.list(bmFilterQ);
		              Iterator<BmObject> items = list.iterator();
		              int index = 1, count = list.size();
		              
		              while(items.hasNext()) {
		                  bmoQuoteItem = (BmoQuoteItem)items.next();	
		                 
		                 %>
		                 <!--
		                 <tr>
			                 <% //if (index == 1) { %>
								<td align ="center" colspan="8" rowspan="<%//= count %>" style="white-space: nowrap;">&nbsp;
									<%//if (bmoQuoteGroup.getShowGroupImage().toBoolean() && 
										//(bmoQuoteGroup.getImage().toString().length() > 0)) { 
										//String blobKeyParse = bmoQuoteGroup.getImage().toString();
										//if (bmoQuoteGroup.getImage().toString().indexOf(".") > 0)
											//blobKeyParse = bmoQuoteGroup.getImage().toString().substring(0, bmoQuoteGroup.getImage().toString().indexOf("."));
									%> 
		                    			<img src="/fileserve?blob-key=<%//= blobKeyParse %>" width="300" height="200" padding="4"> 
		                  			<%// } %> 
								</td>
							<% //} %>   
							</tr>-->
		                  <tr>   	
		                  	  	

		                      <td class="reportCellEven" align ="left">  
		                      <table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		                      <tr>
		                      <td width="30%">
		                      	<%=bmoQuoteItem.getBmoProduct().getCode() %>&nbsp
		                      	<% if (bmoQuote.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)) { %>
		                      			<%	if (bmoQuoteItem.getProductId().toInteger() > 0 ) { %>
			                      		<% 		if(bmoQuoteItem.getBmoProduct().getDisplayName().toString().equals("")){ %>
				                      				<%= bmoQuoteItem.getBmoProduct().getName().toHtml() %>			                      			
				                      		<% 	} else { %>
			                      					<%= bmoQuoteItem.getBmoProduct().getDisplayName().toHtml() %>
		                      				<% 
	                      					} 
	                      				} else { %>
											<%= bmoQuoteItem.getName().toHtml() %>
	                      				<%
	                      				}
	                      				%>
		                      	<% } else { %>
	                      				<% if(bmoQuoteItem.getBmoProduct().getDisplayName().toString().equals("")){ %>
			                      			<%= bmoQuoteItem.getName().toHtml() %>
			                      			<%= bmoQuoteItem.getBmoProduct().getBrand().toHtml() %>
			                      			<%= bmoQuoteItem.getBmoProduct().getModel().toHtml() %>
		                      			<% }else{ %>
											<%= bmoQuoteItem.getBmoProduct().getDisplayName().toHtml() %>
											<%= bmoQuoteItem.getBmoProduct().getBrand().toHtml() %>
			                      			<%= bmoQuoteItem.getBmoProduct().getModel().toHtml() %>
										<% } %>
								<% } %>
								</td>
								<td align="left" style="vertical-align: bottom;">
								<span class="documentSubText" style="font-size: 8pt;"><%= bmoQuoteItem.getDescription().toHtml() %></span>
								</td>
								</tr>
								</table>
		                      </td>
		                      <td class="reportCellEven" align="center" >
		                        <%if (bmoQuoteGroup.getShowQuantity().toBoolean()) { %> 
		                        	<%= bmoQuoteItem.getQuantity().toDouble() %>
	                            <% } %>  
		                      </td>
		                      <td class="reportCellEven" align="center">
		                      	<%=bmoQuoteItem.getBmoProduct().getBmoUnit().getName() %>
		                      </td>
		                      <td class="reportCellEven" align="center" >
	       						 <% if (bmoQuote.getBmoOrderType().getType().equals("" + BmoOrderType.TYPE_RENTAL)) { %>
		                        	<%if (bmoQuoteGroup.getShowQuantity().toBoolean()) { %>  
		                        		<%= bmoQuoteItem.getDays().toDouble() %>
	                           		 <% } %>  	     
						        <% } %>                        	
		                      </td>
		                      <td class="reportCellEven" align="right">
		                        <%if (bmoQuoteGroup.getShowPrice().toBoolean()) { %>  
	                                <%= formatCurrency.format(bmoQuoteItem.getPrice().toDouble()) %>
	                            <% } %>   
	                          </td>
		                      <td class="reportCellEven" align="right" colspan="2">
		                        <%if (bmoQuoteGroup.getShowPrice().toBoolean()) { %>  
	                                <%= formatCurrency.format(bmoQuoteItem.getAmount().toDouble()) %>
	                            <% } %>    
	                          </td>
	                          
	                          <%
//		                         if (!bmoQuoteGroup.getIsKit().toBoolean()) {
//			                         if (bmoQuoteGroup.getShowProductImage().toBoolean() && !(bmoQuoteItem.getBmoProduct().getImage().toString().length() > 0)
//	                 						&& !bmoQuoteGroup.getShowGroupImage().toBoolean()
//	                 				){ 
	                          %>
		                      			<!--<td class="reportCellEven" align="center">&nbsp;</td>-->
		                      		
			                         <% 
//		                         } }
		                         
		                         	// Si es Kit
		      	             		if (bmoQuoteGroup.getIsKit().toBoolean()) { %>
		      	             			<% if (index == 1) { %>
										<td class="reportCellEven" align ="center" colspan="8" rowspan="<%= count %>" style="white-space: nowrap;" valign="center">&nbsp;
											<% if (bmoQuoteGroup.getShowGroupImage().toBoolean() 
													&& (bmoQuoteGroup.getImage().toString().length() > 0)) {
												
                         						String blobKeyParse = HtmlUtil.parseImageLink(sFParams, bmoQuoteGroup.getImage());
%> 
				                    			<img src="<%= blobKeyParse %>" width="300" height="200"> 
				                  			<% } %> 
										</td>
									<% }	
		      	             		}else{ 			                         
	      	             				//Si estan seleccionados Mostrar-imgGrupo e imgProd, le da prioridad a la imgGrupo por mostrar y si selecciona solo imgGrupo solamente
		                         			
             							if ((bmoQuoteGroup.getShowGroupImage().toBoolean() 
             									&& (bmoQuoteGroup.getImage().toString().length() > 0) 
             									&& bmoQuoteGroup.getShowProductImage().toBoolean() 
             									//&& (bmoQuoteItem.getBmoProduct().getImage().toString().length() > 0)
	                         			) ||
	                         			(bmoQuoteGroup.getShowGroupImage().toBoolean() 
	                         					&& (bmoQuoteGroup.getImage().toString().length() > 0)
	                         					&& !bmoQuoteGroup.getShowProductImage().toBoolean() ) ){
	                         				if (index == 1) {
	                         					
                         						String blobKeyParse = HtmlUtil.parseImageLink(sFParams, bmoQuoteGroup.getImage());
%> 
				                         		<td class="reportCellEven" align ="center" rowspan="<%= count %>" style="white-space: nowrap;" valign="center">
					                    			<img src="<%= blobKeyParse %>" width="300" height="200" padding="4">
				                    			</td>
			                    			<% }//Fin index 						                         				
	                         				// Muestra solo imgProd si es seleccionada
             								} 
			                     } %>  
			                  </tr>
		             <% 
		             	index++;
						if(bmoQuoteItem.getBmoProduct().getBmoUnit().getCode().toString().equals("HRS")){		             
		             		sumQuantity = sumQuantity + bmoQuoteItem.getQuantity().toDouble();
		             		sumGroup = sumGroup + bmoQuoteItem.getQuantity().toDouble();
						}
		             	} // fin de items 
		             //}//else - Si es Kit
		             %>	            
					<tr>
	                  <th class="reportCellEven" align="right">Horas</th>
	                  	<th colspan="" class="reportCellEven"><%=sumGroup %></th>
	                  	<td colspan="5"  class="reportCellEven">
	                </tr>
	                <% } // fin de orderGroups %> 
	                <tr>
	                	<th class="reportCellEven" align="right">Total de Horas</th>
	                  	<th colspan="" class="reportCellEven"><%=sumQuantity %></th>
	                  	<td colspan="5"  class="reportCellEven">
	                </tr>
	                <% 	if (bmoQuote.getBmoOrderType().getType().equals("" + BmoOrderType.TYPE_RENTAL)) { %>
						<%	  
			        	  BmoQuoteEquipment bmoQuoteEquipment = new BmoQuoteEquipment();
			              PmQuoteEquipment pmQuoteEquipment = new PmQuoteEquipment(sFParams);
			              BmFilter bmFilterEq = new BmFilter();
			              bmFilterEq.setValueFilter(bmoQuoteEquipment.getKind(), bmoQuoteEquipment.getQuoteId().getName(), bmoQuote.getId());
			              Iterator<BmObject> items = pmQuoteEquipment.list(bmFilterEq).iterator();
			              int iQEquipment = 1;
			              while(items.hasNext()) {
		                  	bmoQuoteEquipment = (BmoQuoteEquipment)items.next();
		                  	if (iQEquipment == 1) {
		                 		%>
	                 			<tr>
			                    <td class="reportHeaderCell" colspan="7">
			                        Recursos
			                    </td>
		                        <td class="reportHeaderCellRight">
			                    	<%                    	
			                    		//Obtener el total del staff
			                    		pmConn.open();
			                    		
			                    		double totalEquipment = 0;
			                    		
			                    		sql = " SELECT SUM(qoeq_amount) AS totalequipments FROM quoteequipments " +
			                    		      " WHERE qoeq_quoteid = " +  bmoQuote.getId();
			                    		
			                    		pmConn.doFetch(sql);
			                    		if (pmConn.next()) { 
			                    			totalEquipment = pmConn.getDouble("totalequipments");
			                    		}
			                    	%>
			                    		<%= formatCurrency.format(totalEquipment) %>&nbsp;
			                    	<% 
			                    	  pmConn.close(); 
			                        %>
			                    </td>
			                	</tr>
			                <%	} %>
			                  <tr>
			                      <td class="reportCellEven" align ="left" colspan="3">  
			                      	<%= bmoQuoteEquipment.getName().toHtml() %>
									&nbsp;<%= bmoQuoteEquipment.getDescription().toHtml() %>
			                      </td>
			                      <td class="reportCellEven" align="center">  
			                        <%if (bmoQuote.getShowEquipmentQuantity().toBoolean()) { %> 
			                        	<%= bmoQuoteEquipment.getQuantity().toInteger() %>
			                        <% } %>  
			                      </td>
			                      <td class="reportCellEven" align="center">  
			                        <%if (bmoQuote.getShowEquipmentQuantity().toBoolean()) { %>  
			                        	<%= bmoQuoteEquipment.getDays().toDouble() %>
			                        <% } %>  	                        	
			                      </td>
			                      <td class="reportCellEven" align="right">
			                        <%if (bmoQuote.getShowEquipmentPrice().toBoolean()) { %>  
			                            <%= formatCurrency.format(bmoQuoteEquipment.getPrice().toDouble()) %>
			                        <% } %>    
			                      </td>
			                      <td class="reportCellEven" align="right">
			                         <% if (bmoQuote.getShowEquipmentPrice().toBoolean()) { %>
			                            <%= formatCurrency.format(bmoQuoteEquipment.getAmount().toDouble()) %>
			                         <% } %>   
			                      </td>
								  <td class="reportCellEven">
			                           &nbsp;
			                      </td> 
			                  <tr>
			             <% 
			             	iQEquipment++;
			              } 
	                	
			        	  BmoQuoteStaff bmoQuoteStaff = new BmoQuoteStaff();
			              PmQuoteStaff pmQuoteStaff = new PmQuoteStaff(sFParams);
			              BmFilter bmFilterSt = new BmFilter();
			              bmFilterSt.setValueFilter(bmoQuoteStaff.getKind(), bmoQuoteStaff.getQuoteId().getName(), bmoQuote.getId());
			              Iterator<BmObject> itemsStaff = pmQuoteStaff.list(bmFilterSt).iterator();
			              int iQStaff = 1;
			              while(itemsStaff.hasNext()) {
			                  bmoQuoteStaff = (BmoQuoteStaff)itemsStaff.next();
			                  if (iQStaff == 1) {
			                 %>
			                 	<tr>
			             			<td colspan="8" class="">&nbsp;</td>
			             		</tr>
				   				<tr>
				                    <td class="reportHeaderCell" colspan="7">
				                        Personal
				                    </td>
				                    <td class="reportHeaderCellRight">
				                    	&nbsp;
				                    	<%                    	
				                    		//Obtener el total del staff                    		
				                    		pmConn.open();
				                    		
				                    		double totalStaff = 0;
				                    		
				                    		sql = " SELECT SUM(qost_amount) AS totalstaff FROM quotestaff " +
				                    		      " WHERE qost_quoteid = " +  bmoQuote.getId();
				                    		
				                    		pmConn.doFetch(sql);
				                    		if (pmConn.next()) { 
				                    			totalStaff = pmConn.getDouble("totalstaff");
				                    		}
					                    	pmConn.close();
				                    	%>
				                    		<%= formatCurrency.format(totalStaff) %>&nbsp;
				                    </td>
				                </tr>
			                <%	} %>
			                  <tr>	                    
			                      <td class="reportCellEven" align ="left" colspan="3">  
									<%= bmoQuoteStaff.getName().toHtml() %>
									&nbsp;<%= bmoQuoteStaff.getDescription().toHtml() %>
			                      </td>
			                      <td class="reportCellEven" align="center">  
			                        <%if (bmoQuote.getShowStaffQuantity().toBoolean()) { %> 
			                        	<%= bmoQuoteStaff.getQuantity().toInteger() %>
			                        <% } %>  
			                      </td>
			                      <td class="reportCellEven" align="center">  
			                        <%if (bmoQuote.getShowStaffQuantity().toBoolean()) { %>  
			                        	<%= bmoQuoteStaff.getDays().toDouble() %>
			                        <% } %>  	                        	
			                      </td>
			                      <td class="reportCellEven" align="right">
			                        <%if (bmoQuote.getShowStaffPrice().toBoolean()) { %>  
			                        	<%= formatCurrency.format(bmoQuoteStaff.getPrice().toDouble()) %>
			                        <% } %>    
			                      </td>
			                      <td class="reportCellEven" align="right">
			                         <%if (bmoQuote.getShowStaffPrice().toBoolean()) { %>
			                            <%= formatCurrency.format(bmoQuoteStaff.getAmount().toDouble()) %>
			                         <% } %>   
			                      </td>
								  <td class="reportCellEven">
			                           &nbsp;
			                      </td> 
			                  <tr>
			             <% 
			             	iQStaff++;
			              } %>
                <% 	} %> 
	                <tr>
	                	<td colspan="8" class="">&nbsp;</td>
                	</tr>
                	<tr>
	                	<td colspan="5" rowspan="4" valign="top" align="left" class="detailStart">
	                		<p class="documentComments"><b>Notas:</b> <br>
<%-- 		                	<% if(bmoOpportunityDetail.getGuests().toInteger() > 0){ %> --%>
<%-- 		                		Asistentes: <%= bmoOpportunityDetail.getGuests().toInteger() %> <br> --%>
<%-- 		                	<%	} %> --%>
<%-- 	                		<%= bmoQuote.getDescription().toHtml() %> --%>
								<%=bmoQuote.getDescription().toString() %>	                		
	                		</p>
	                	</td>                                  
	                	<th class="" align="right">
	                		Subtotal:
                		</th>
	                	<td class="reportCellEven" align="right">
	                		<b><%=  formatCurrency.format(bmoQuote.getAmount().toDouble()) %>
	                	</td>
                	</tr>    
                	<tr>                                                                       
	                	<th class="" align="right">
	                		<% if (bmoQuote.getDiscount().toDouble() > 0) { %>
	                			Descuento:
	            			<% } %>
	            		</th>
                		<td class="reportCellEven" align="right">
	                		<% if (bmoQuote.getDiscount().toDouble() > 0) { %>
	                			<b><%=  formatCurrency.format(bmoQuote.getDiscount().toDouble()) %></b>
	                		<% } %>
                		</td>
                		</tr>  
                		<tr>                                                                        
	                		<th class="" align="right">
		                		<% if (bmoQuote.getTax().toDouble() > 0) { %>
		                			IVA:
	                			<% } %>
                			</th>
	                			<td class="reportCellEven"align="right">
	                			<% if (bmoQuote.getTax().toDouble() > 0) { %>
	                				<b><%=  formatCurrency.format(bmoQuote.getTax().toDouble()) %></b>
	                			<% } %>
                			</td>
            			</tr>  
            			<tr>                                                                        
                			<th class="" align="right">
                				Total:
            				</th>
                			<td class="reportCellEven" align="right">
                				<b><%= formatCurrency.format(bmoQuote.getTotal().toDouble()) %></b>
                			</td>
            			</tr> 
            			<tr>
	            			<td class="" colspan="8">
	            				&nbsp;
	            			</td>
            			</tr>
	</table>	

	<table style="page-break-before:always;font-size: 14px">
		<tr>
			<td width="50%" style="vertical-align: top">
				<p align="justify">
					<b>I. Moneda: </b> Los precios no incluyen IVA.
				</p>

				<p align="justify">
					<b>II. Desarrollos:</b> Los Desarrollos adicionales que 
					&quot;<b>el cliente</b>&quot; llegase a requerir por la naturaleza de su operaci&oacute;n 
					que no est&eacute;n expl&iacute;citamente mencionados en esta propuesta, no 
					se consideran dentro de la presente. En caso de requerir 
					cualquier tipo de desarrollo adicional, se deber&aacute; crear 
					una especificaci&oacute;n t&eacute;cnica que ser&aacute; validada por ambas 
					partes para tener un alcance y poder citar el desarrollo 
					requerido antes de ser ejecutado.

				</p>


				<p align="justify">
					<b>III. Estimaci&oacute;n: </b> Las cifras de servicio y desarrollo podr&aacute;n 
					ser validadas y confirmadas contra las definiciones que se obtengan de las 
					sesiones de Confirmaci&oacute;n de Alcance y Planeaci&oacute;n del Proyecto. Para toda consultor&iacute;a 
					y desarrollo adicional no contemplados en el tiempo estimado del proyecto, se respetar&aacute;
					la tarifa por d&iacute;a estipulada en el presente documento. El &quot;d&iacute;a&quot; de servicios comprende 
					9 horas incluyendo 1 hora de almuerzo (8 Horas Facturables).
				</p>
				<p align="justify">
					<b>IV. Informaci&oacute;n:</b> La captura de informaci&oacute;n actual e
					hist&oacute;rica es responsabilidad del cliente y sus
					usuarios.
				</p>
				<p align="justify">
					<b>V. Instalaciones: </b> El cliente pondr&aacute; a disposici&oacute;n de los 
					consultores las instalaciones y facilidades necesarias para el 
					mejor desempe&ntilde;o de las actividades.
				</p>
				<p align="justify">
					<b>VI. Pagos:</b> El pago de los Servicios y Productos VES, se realizar&aacute; directamente
					 a Visual ERP Systems, S.A. de C.V. En su momento los Pagos podr&aacute;n ser 
					 realizados en USD o en moneda nacional al tipo de cambio del 
					 Diario del Banco de M&eacute;xico.
				</p>
				<p align="justify">
					<b>VII. Vi&aacute;ticos:</b> Los gastos de viaje fuera de la 
					Ciudad de M&eacute;xico y Guadalajara incurridos durante el 
					proceso de ejecuci&oacute;n de los servicios cotizados (transporte, alojamiento, comida, propinas, etc.)
					 no est&aacute;n incluidos en la tarifa estimada, ser&aacute;n cubiertos por el 
					 cliente y podr&aacute;n ser administrados y facturados por &quot;VISUAL ERP SYSTEMS&quot;.
				</p>
				<p align="justify">
					<b>VIII. Hardware:</b> El hardware, los servidores de bases de datos, 
					el software adicional y la propia base de datos necesarios para 
					el mejor rendimiento de estas soluciones sugeridas, NO se 
					incluyen en esta propuesta. Las especificaciones del equipo 
					se muestran en el anexo correspondiente (Si aplica).
				</p>
				<p align="justify">
				<b>IX. Intereses Moratorios: </b> La falta del pago oportuno dentro 
					del plazo previamente mencionado causar&aacute; intereses moratorios a raz&oacute;n del 
					<b>5% </b>(cinco por ciento) mensual a partir del d&iacute;a siguiente en que no 
					</p>
					
			</td>
			<td>&nbsp;</td>
			<td width="50%" style="vertical-align: top">
				<p align="justify">
					
					se cubra el pago de la contraprestaci&oacute;n y durante todo el tiempo en 
					el que el pago de que se trate no sea realizado. Dichos intereses 
					moratorios deber&aacute;n pagarse a favor de &quot;VISUAL ERP SYSTEMS&quot; simult&aacute;neamente
					con el pago del servicio.
				</p>
				<p align="justify">
					<b>X. Alcance:</b>El proyecto no considera dentro de su alcance el tiempo y esfuerzo requerido para las siguientes actividades:
    <ul >
    	<li style="list-style-type: decimal;">  Adquisici&oacute;n de Hardware, Software e Infraestructura de redes propia del cliente. </li>
   		<li style="list-style-type: decimal;">  La estimaci&oacute;n no considera ning&uacute;n factor para absorber atrasos 
   		en el proyecto, derivados de situaciones no imputables a <b>Visual ERP Systems</b> como son:
  			<ul>
  				<li style="list-style-type:lower-alpha;">  Falta de disponibilidad del personal que define los requerimientos.</li>
   				<li style="list-style-type:lower-alpha;"> Cambios una vez definidos los requerimientos.</li>
   				<li style="list-style-type:lower-alpha;">  Falta de disponibilidad del equipo del proyecto del cliente.</li>
   				<li style="list-style-type:lower-alpha;">  Cambios al Plan de Trabajo establecido en conjunto.</li>
   				<li style="list-style-type:lower-alpha;">  Actividades o eventos inherentes al cliente y ajenos al proyecto.</li>
  				<li style="list-style-type:lower-alpha;">  Falta de disponibilidad de la infraestructura del cliente, como conexiones a servidores 
  				 y/o equipos de c&oacute;mputo, as&iacute; como la conexi&oacute;n y velocidad de internet propio del cliente, entre otros.</li>
  			</ul> 
  
  </li>
	  	<li style="list-style-type: decimal;">  El apoyo adicional de Consultor&iacute;a ser&aacute; por solicitud del Cliente previa aprobaci&oacute;n del mismo para su posterior facturaci&oacute;n.</li>
	</ul>
				</p>
				<p align="center">
					<b>ACUERDO DE CONFIDENCIALIDAD</b>
				</p>
				<p align="justify">La presente propuesta contiene informaci&oacute;n comercial confidencial y 
				est&eacute; sujeta a derecho de propiedad por VISUAL ERP SYSTEMS, S.A. de C.V. Dicho material 
				est&eacute; protegido por la ley de derechos de autor y debe ser impreso o fotocopiado y/o distribuido 
				solo para el proceso de evaluaci&oacute;n de los servicios propuestos, no deber&aacute; ser compartido con terceras partes sin el consentimiento escrito de VISUAL ERP SYSTEMS.
					
				<p align="center"><b>Aprobaci&oacute;n Cliente:</b>
				<br>
				<br>
				
					<table  width="100%" style="font-size: 13px" >
						<tr>
							<td style=" border: 1px solid #ddd" width="30%" height="50%">
								FIRMA
							</td>
							<td style=" border: 1px solid #ddd" width="50%" height="50px">
							</td>
						</tr>
						<tr>
							<td  style=" border: 1px solid #ddd" width="30%">
								NOMBRE Y FECHA
							</td>
							<td style=" border: 1px solid #ddd" width="50%"  height="50px">
							</td>
						</tr>
						<tr>
							<td class= "reportDate" align="right" colspan="2">
							&nbsp;
							</td> 
						</tr>
						<tr>
							<td class= "reportDate" align="right" colspan="2">
							<%=bmoOpportunity.getCode() + " " + bmoOpportunity.getName()%>
							</td> 
						</tr>
						
					</table>
				</p>
			</td>
		</tr>
	</table>
<!-- 	<footer style=" position: absolute;bottom: 0;width: 95%; height: 35px; color: white;text-align: right;"> -->
<%-- 		<%= bmoQuote.getStatus().getSelectedOption().getLabeltoHtml() %> --%>
<%-- 		(#<%= bmoQuote.getAuthNum().toString() %>)		 --%>
<!-- 	</footer> -->
	<% 	} catch (Exception e) { 
			String errorLabel = "Error de Cotizacion";
			String errorText = "La Cotizacion no puede ser desplegada.";
			String errorException = e.toString();
			
			//response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);
	
	%>
	
		<%= errorException %>
	
	<%
		}
	
	%>
</body>

</html>