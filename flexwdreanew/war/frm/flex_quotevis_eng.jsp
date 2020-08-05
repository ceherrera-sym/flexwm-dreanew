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

	String title = "QUOTATION";

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
	
	//Si se visualiza y no se tienen permisos, deshabilita: copiar, pegar, seleccionar, menÃÂº(clic-derecho).
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
				QUOTATION</td>
        </tr>
        <tr>
	        <th align = "left" class="reportCellEven" width="10%" >Company / Client:</th>
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
			<th align = "left" class="reportCellEven"  width="10%" >Type:</th>
	        <td class="reportCellEven" width="20%">
            	<%=bmoOpportunity.getBmoWFlowType().getName().toHtml() %>
			</td>
        </tr>
        <tr>
	        <th align = "left" class="reportCellEven">Name:</th>
	        <td class="reportCellEven">
            	<%= bmoOpportunity.getCode().toHtml() + " - " + bmoOpportunity.getName().toHtml() %> 
	        </td>
<!-- 	        <th align = "left" class="reportCellEven" >Tipo:</th> -->
<!-- 	        <td class="reportCellEven"> -->
<%--             	<%= bmoProjectWFlowType.getName().toHtml() %> --%>
<!-- 			</td> -->
			<th class="reportCellEven" align = "left">				
					Quote Date:
			</th>
			<td class="reportCellEven">
				<%= SFServerUtil.nowToString(sFParams, sFParams.getBmoSFConfig().getPrintDateFormat().toString()) %>
			</td>
	    </tr>
		<tr>
			<th class="reportCellEven" align = "left">
					Commercial Executive: 
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
				Status:
			</th>
			<td class="reportCellEven">
				<%= bmoQuote.getStatus().getSelectedOption().getLabeltoHtml() %>
				(#<%= bmoQuote.getAuthNum().toString() %>)
			</td>
		</tr>
		<tr>
			<th class="reportCellEven" align = "left">
				General Payment Terms:
			</th>
			
			<td class="reportCellEven">
				<%
				PmConn pmConnPayCondition = new PmConn(sFParams);
				pmConnPayCondition.open();
				String sqlPayCondition = "SELECT qogr_quotegroupid FROM quotegroups WHERE qogr_quoteid = " + bmoQuote.getId() + " AND qogr_payconditionid > 0";
				System.out.println("CONSULTA SSQL FORMATO " + sqlPayCondition);
				pmConnPayCondition.doFetch(sqlPayCondition);
				if(pmConnPayCondition.next()){%>					
					See payment conditions for each item
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
				Validity
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
				Currency:
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
	         <td class="reportHeaderCell"  width="40%">Description</td>
	        <td class="reportHeaderCellCenter"  width="10%">Quantity</td>
	        <td class="reportHeaderCellCenter" width="8%">UM</td>
	        <% if (bmoQuote.getBmoOrderType().getType().equals("" + BmoOrderType.TYPE_RENTAL)) { %>
	        	<td class="reportHeaderCellCenter" width="1%">Days</td>
	        <% } else { %>
	        	<td class="reportHeaderCell" align="left" width="1%">&nbsp;</td>	        
	        <% } %>
	        <td class="reportHeaderCellRight" width="8%">Unit Price</td>
	        <td class="reportHeaderCellRight" width="8%" colspan="2">Amount&nbsp;&nbsp;&nbsp;</td>
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
	                            <b>&nbsp;Payments Terms</b>
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
		                      	<%=bmoQuoteItem.getBmoProduct().getBmoUnit().getCode() %>
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
	                  <th class="reportCellEven" align="right">Hours</th>
	                  	<th colspan="" class="reportCellEven"><%=sumGroup %></th>
	                  	<td colspan="5"  class="reportCellEven">
	                </tr>
	                <% } // fin de orderGroups %> 
	                <tr>
	                	<th class="reportCellEven" align="right">Total Hours</th>
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
								General Conditions	&nbsp;<%= bmoQuoteEquipment.getDescription().toHtml() %>
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
	                		<p class="documentComments"><b>Notes:</b> <br>
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
								Discount:
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
		                			 VAT:
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
                				Total Amount:
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
					<b>I. Currency: </b> The prices do not include VAT
				</p>

				<p align="justify">
					<b>II. Developments:</b> The additional Developments that &quot;the
					customer&quot; will come to require due to the nature of their operation
					that are not explicitly mentioned in this proposal, are not
					considered within the present. If any additional development is
					required, a technical specification must be created that will be
					validated by both parties to have a scope and be able to cite the
					required development before being executed. This proposal does not
					include any Addendas nor EDI services, if required, the
					requirements will be reviewed separately and an independent quote
					will be delivered.

				</p>


				<p align="justify">
					<b>III. Estimation: </b> The services and development figures can
					be validated and confirmed against the definitions obtained from
					the Scope Confirmation and Project Planning sessions. For any
					consultancy and additional development not contemplated in the
					estimated time of the project, the rate per day stipulated in this
					document will be respected. The  &quot;day &quot; of services includes 9 hours
					including 1 lunch hour (8 Billable Hours).
				</p>
				<p align="justify">
					<b>IV. Information:</b> The capture of current and historical
					information in INFOR ERP VISUAL is the responsibility of the
					customer and its users.
				</p>
				<p align="justify">
					<b>V. Facilities: </b> The customer will make available to the
					consultants the facilities necessary for the best performance of
					the activities.
				</p>
				<p align="justify">
					<b>VI. Payments:</b> Payment of the VES Services and Products will
					be made directly to Visual ERP Systems, S.A. of C.V. In due course,
					Payments may be made in USD or in national currency at the exchange
					rate of the Banco de M&eacute;xico Daily.
				</p>
				<p align="justify">
					<b>VII. Travel Expenses:</b> Travel expenses outside of Mexico City
					and Guadalajara incurred during the execution process of the
					services quoted (transportation, lodging, food, tips, etc.) are not
					included in the estimated rate and will be covered by the customer
					and may be administered and billed by  &quot;VISUAL ERP SYSTEMS &quot;.
				</p>
				<p align="justify">
					<b>VIII. Hardware:</b> The hardware, the database servers, the
					additional software and the database necessary for the best
					performance of these suggested solutions are NOT included in this
					proposal. The equipment specifications are shown in the
					corresponding annex (If applicable).
				</p>
				<p align="justify">
					<b>IX. Moratorium Interests: </b> he lack of timely payment within
					the aforementioned period will cause default interest at a rate of
					5% (five percent) per month from the next day when the payment of
					the consideration 
				</p>

			</td>
			<td>&nbsp;</td>
			<td width="50%" style="vertical-align: top">
				<p align="justify">
						is not covered and during the entire time the
					payment in question is not carried out. Such default interest must
					be paid in favor of &quot;VISUAL ERP SYSTEMS&quot; simultaneously with the
					payment of the service.
				
				</p>
				<p align="justify">
					<b>X. Scope:</b>The project does not consider within its scope the
					time and effort required for the following activities:
			<ul >
    	<li style="list-style-type: decimal;">  Acquisition of hardware, software and network infrastructure of the customer. </li>
   		<li style="list-style-type: decimal;">  The estimate does not consider any factor to absorb	delays in the project derived from situations not
   												attributable to <b>Visual ERP Systems</b> such as:
  			<ul>
  				<li style="list-style-type:lower-alpha;"> Lack of availability of the personnel that defines the requirements.</li>
   				<li style="list-style-type:lower-alpha;"> Changes once the requirements have been defined.</li>
   				<li style="list-style-type:lower-alpha;"> Lack of availability of the client project team.</li>
   				<li style="list-style-type:lower-alpha;"> Changes to the Work Plan established together.</li>
   				<li style="list-style-type:lower-alpha;"> Activities or events inherent to the client and outside the project.</li>
  				<li style="list-style-type:lower-alpha;"> Lack of availability of the client's
infrastructure, such as connections to
servers and / or computer equipment, as
well as the client's own internet connection
and speed, among others.</li>
  			</ul> 
  
  </li>
	  	<li style="list-style-type: decimal;">  The additional support of Consulting will be at the
request of the Customer prior approval of the same
for its subsequent invoicing.</li>
	</ul>
				</p>
				<p align="center">
					<b>CONFIDENTIALITY AGREEMENT:</b>
				</p>
				<p align="justify">This proposal contains confidential business information and is subject to confidential business information and is subject to property rights by VISUAL ERP SYSTEMS, S.A. of C.V. This
material is protected by copyright law and must be printed or photocopied and / or distributed only for the be shared with third parties without the written consent of VISUAL ERP SYSTEMS.
evaluation process of the proposed services, it must not 
				<p align="center"><b>Customer Approval:</b>
				<br>
				<br>
				
					<table  width="100%" style="font-size: 13px" >
						<tr>
							<td style=" border: 1px solid #ddd" width="30%" height="50%">SIGN</td>
							<td style=" border: 1px solid #ddd" width="50%" height="50px">
							</td>
						</tr>
						<tr>
							<td  style=" border: 1px solid #ddd" width="30%">NAME AND DATE	</td>
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