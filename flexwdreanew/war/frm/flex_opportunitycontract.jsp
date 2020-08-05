<%@page import="com.flexwm.server.cm.*"%>
<%@page import="com.flexwm.shared.cm.*"%>
<%@page import="com.flexwm.server.ev.*"%>
<%@page import="com.flexwm.shared.ev.*"%>
<%@page import="com.flexwm.shared.op.BmoOrderType"%>
<%@page import="com.symgae.shared.BmFilter"%>
<%@page import="java.sql.Types"%>
<%@page import="com.symgae.server.PmConn"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.shared.wf.BmoWFlowType"%>
<%@page import="com.flexwm.server.wf.PmWFlowType"%>
<%@include file="../inc/login_opt.jsp" %>

<%
	String title = "Contrato Proyecto";

	BmoOpportunity bmoOpportunity = new BmoOpportunity();
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
	    <link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>"> 
	</head>
	<body class="default" <%= permissionPrint %>>
	<%
		try {
			NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(Locale.US);
				
			PmConn pmConnCustomer= new PmConn(sFParams);
			pmConnCustomer.open();
			boolean opde = false, cuad = false, cuem = false;
			
		    // Si es llamada externa, utilizar llave desencriptada
		    int opportunityId = 0;
		    if (isExternal) opportunityId = decryptId;
		    else opportunityId = Integer.parseInt(request.getParameter("foreignId"));	    
		    
			
			PmOpportunity pmOpportunity = new PmOpportunity(sFParams);
			bmoOpportunity = (BmoOpportunity)pmOpportunity.get(opportunityId);
			
			BmoCompany bmoCompany = new BmoCompany();
			PmCompany pmCompany = new PmCompany(sFParams);
								
			if (bmoOpportunity.getCompanyId().toInteger() > 0)
				bmoCompany = (BmoCompany)pmCompany.get(bmoOpportunity.getCompanyId().toInteger());
			
			
			// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
			String logoURL ="";
			if (!bmoCompany.getLogo().toString().equals(""))
				logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
			else 
				logoURL = sFParams.getMainImageUrl();
			
			
			BmoCity bmoCompanyCity = new BmoCity();
			PmCity pmCity = new PmCity(sFParams);
			if (bmoCompany.getCityId().toInteger() > 0) 
				bmoCompanyCity = (BmoCity)pmCity.get(bmoCompany.getCityId().toInteger());
			
			// Detalle de oportunidad
			BmoOpportunityDetail bmoOpportunityDetail = new BmoOpportunityDetail();
			PmOpportunityDetail pmOpportunityDetail = new PmOpportunityDetail(sFParams);
			bmoOpportunityDetail = (BmoOpportunityDetail)pmOpportunityDetail.getBy(bmoOpportunity.getId(), bmoOpportunityDetail.getOpportunityId().getName());
	
			// Obtener Direccion particular del Cliente
			BmoCustomerAddress bmoCustomerAddressSpecial = new BmoCustomerAddress();
			PmCustomerAddress pmCustomerAddressSpecial = new PmCustomerAddress(sFParams);
			String sqlCust = " SELECT cuad_customeraddressid FROM customeraddress WHERE cuad_customerid = " + bmoOpportunity.getBmoCustomer().getId();
			pmConnCustomer.doFetch(sqlCust);
			if(pmConnCustomer.next()) { 
				cuad = true;
				bmoCustomerAddressSpecial = (BmoCustomerAddress)pmCustomerAddressSpecial.get(pmConnCustomer.getInt("cuad_customeraddressid"));
			}
			
			// Obtener Direccion del evento
			String venue = "";
			// direccion cliente
			BmoCustomerAddress bmoCustomerAddress = new BmoCustomerAddress();
			PmCustomerAddress pmCustomerAddress = new PmCustomerAddress(sFParams);
			// direccion del salon
			BmoVenue bmoVenue = new BmoVenue();
			PmVenue pmVenue = new PmVenue(sFParams);
			
			// Si viene de un salon tomar datos, sino de la direccion del cliente
			if (bmoOpportunity.getVenueId().toInteger() > 0 ) {
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
// 			else if (bmoOpportunity.getCustomerAddressId().toInteger() > 0) {
// 				bmoCustomerAddress = (BmoCustomerAddress)pmCustomerAddress.get(bmoOpportunity.getCustomerAddressId().toInteger());
				
// 				venue = bmoCustomerAddress.getStreet().toHtml() + ": " +
// 						bmoCustomerAddress.getNumber().toHtml() + " " +
// 						bmoCustomerAddress.getNeighborhood().toHtml() + ", " +
// 						bmoCustomerAddress.getBmoCity().getName().toHtml() + ", " +
// 						bmoCustomerAddress.getBmoCity().getBmoState().getCode().toHtml() + ", " +
// 						bmoCustomerAddress.getBmoCity().getBmoState().getBmoCountry().getCode().toHtml() + ".";
// 			}
			
			// Correo del cliente, si no del catalogo de correos del cliente
			String emailCust = "";
			BmoCustomerEmail bmoCustomerEmail = new BmoCustomerEmail();
			PmCustomerEmail pmCustomerEmail = new PmCustomerEmail(sFParams);
			
			int countEmailCust = 1;
			if (!bmoOpportunity.getBmoCustomer().getEmail().toString().equals("")) {
				emailCust = bmoOpportunity.getBmoCustomer().getEmail().toString();
				countEmailCust++;
			}
			
			sqlCust = " SELECT cuem_email FROM customeremails WHERE cuem_customerid = " + bmoOpportunity.getBmoCustomer().getId();
			pmConnCustomer.doFetch(sqlCust);
			
			while (pmConnCustomer.next()) {
				if (countEmailCust > 1) emailCust += ", ";
				emailCust += pmConnCustomer.getString("cuem_email");
				countEmailCust++;
			}
			
			pmConnCustomer.close();
	%>
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		<tr valign="top">
    		<td align="left" width="" align="center" rowspan="2">
				<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
		    </td>
		    <td class="contracSubTitle" align="center">
		       <b><%= bmoCompany.getLegalName().toHtml().toUpperCase() %></b>
		    </td>
		    <td width="20%" align="right" rowspan="2">
		        <span style="background-color : #F0F0F0;"><b>FOLIO</b></span>
		        <br>
		        CTO/<%= bmoOpportunity.getCode().toHtml() %>
		    </td>
	    </tr>
	    <tr>
	  		<td class="contractTitleCaption" align="center">
		  		<%= bmoCompany.getStreet().toHtml() %> <%= bmoCompany.getNumber().toHtml() %><br>
		  		<%= bmoCompany.getNeighborhood().toHtml() %>, C.P. <%= bmoCompany.getZip().toHtml() %><br>
		  		<%= bmoCompanyCity.getName().toHtml() %>, <%= bmoCompanyCity.getBmoState().getName().toHtml() %>,
		  		<%= bmoCompanyCity.getBmoState().getBmoCountry().getName().toHtml() %>.<br>
		  		TEL: <%= bmoCompany.getPhone().toHtml() %><br>
		  		<b><%= bmoCompany.getWww().toHtml() %></b>
	  		</td>
  		</tr>
	</table>
	<br>
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		<tr>
	  		<td colspan="5" class="reportHeaderCell">
  				1. Datos del Contratante:
			</td>
  		</tr>
  		<tr>
  			<th align="left" class="reportCellEven">
  				Nombre:
			</th>
			<td class="reportCellEven">
	            <%
	               String fullName = bmoOpportunity.getBmoCustomer().getFatherlastname().toHtml() + " " +
	                       bmoOpportunity.getBmoCustomer().getMotherlastname().toHtml() + " " +
	                       bmoOpportunity.getBmoCustomer().getFirstname().toHtml(); 
	            %>
	            <%= fullName  %> 
            </td>
            <th align="left" class="reportCellEven">
            	Domicilio part.:
        	</th>
            <td align="left" class="reportCellEven" colspan="2">
	            <%
	            String street = "", number = "", neighborhood  = "", city = "",state = "", country = "", zip = "";
	            if(cuad) {
	            	street = bmoCustomerAddressSpecial.getStreet().toHtml();
		            number = bmoCustomerAddressSpecial.getNumber().toHtml();
		            neighborhood = bmoCustomerAddressSpecial.getNeighborhood().toHtml();            	  
		            zip = bmoCustomerAddressSpecial.getZip().toHtml();
	            }
	            %>  
	            <%= street %>
	            <%= number %>      	 
            </td>
    	</tr>
    	<tr>   
	    	<th align="left" class="reportCellEven">
	    		Colonia:
    		</th>
	    	<td align="left" class="reportCellEven">
	    		<%= neighborhood %>
	    	</td>
	    	<th align="left" class="reportCellEven">
	    		Ciudad:
		    	<%
		    	BmoCity bmoCity = new BmoCity();
		    	pmCity = new PmCity(sFParams);
		    	if(cuad) {
		    		bmoCity = (BmoCity)pmCity.get(bmoCustomerAddressSpecial.getCityId().toInteger());
		    		city = bmoCity.getName().toHtml();
		    		state = bmoCity.getBmoState().getName().toHtml();
		    		country = bmoCity.getBmoState().getBmoCountry().getName().toHtml();
		    	}
		    	%> 
	    		</th>
	    	<td align="left" class="reportCellEven" colspan="2"> 
	    		<%= city %> 
	    	</td> 
    	</tr>
    	<tr>   
	    	<th align="left" class="reportCellEven">
	    		Estado:
    		</th>
	    	<td align="left" class="reportCellEven">
	    		<%= state %> 
	    	</td> 
	    	<th align="left" class="reportCellEven">
	    		Pa&iacute;s:
    		</th>
	    	<td align="left" class="reportCellEven" colspan="2">
	    		<%= country %>
	    	</td>
    	</tr>
    	<tr>
	    	<th align="left" class="reportCellEven">
	    		C.P.:
    		</th>
	    	<td align="left" class="reportCellEven">
	    		<%= zip %> 
	    	</td>
	    	<th align="left" class="reportCellEven">
	    		Tel&eacute;fonos:
    		</th>
	    	<td align="left" class="reportCellEven" colspan="2">
		    	<%
		    	if (!bmoOpportunity.getBmoCustomer().getMobile().toString().equals("") 
		    			|| !bmoOpportunity.getBmoCustomer().getPhone().toString().equals("") ) {

		    		if (!bmoOpportunity.getBmoCustomer().getPhone().toString().equals("")) {%>
		    			Tel.: <%= bmoOpportunity.getBmoCustomer().getPhone().toString() %><%
		    			if (!bmoOpportunity.getBmoCustomer().getMobile().toString().equals("")) { %>,<% }
	    			} 
		    		if (!bmoOpportunity.getBmoCustomer().getMobile().toString().equals("")) { %>
		    			M&oacute;vil: <%= bmoOpportunity.getBmoCustomer().getMobile().toString() %>
	    		<%	}
    			} else {
    				String homePhone = "", officePhone = "", movilPhone = "";
			    	BmoCustomerPhone bmoCustomerPhone = new BmoCustomerPhone();
			    	PmCustomerPhone pmCustomerPhone = new PmCustomerPhone(sFParams);
			    	BmFilter bmFilter = new BmFilter();
			    	bmFilter.setValueFilter(bmoCustomerPhone.getKind(), bmoCustomerPhone.getCustomerId().getName(), 
			    			bmoOpportunity.getBmoCustomer().getId());	      
			    	Iterator<BmObject> fields = pmCustomerPhone.list(bmFilter).iterator();
			    	while (fields.hasNext()) { 
			    		bmoCustomerPhone = (BmoCustomerPhone)fields.next();
			    		%> 	    	   
			    		<%= bmoCustomerPhone.getType().getSelectedOption().getLabel() %>: <%= bmoCustomerPhone.getNumber().toHtml() %>, 
		    		<%	
		    		}
    			}
    		%>
    		</td>
		</tr>
		<tr>
			<th align="left" class="reportCellEven">          
				Correos electr&oacute;nicos:
			</th>
			<td align="left" class="reportCellEven">
				<%= emailCust %>
			</td>     
			<th align="left" class="reportCellEven" >
				R.F.C.:
			</th>
			<td align="left" class="reportCellEven" colspan="2">
				<%=  bmoOpportunity.getBmoCustomer().getRfc().toHtml() %>	
			</td>
		</tr>
		<tr>
			<td colspan="5">
				&nbsp;
			</td>
		</tr>
		<tr>
			<td colspan="5" align="left" class="reportHeaderCell">
				2. Fecha y hora del servicio:
			</td>
		</tr>
		<tr> 
			<th align="left" class="reportCellEven">
				Fecha y hora del Evento:
			</th>       	
			<td align="left" class="reportCellEven" colspan="4">
				Inicio: <%= SFServerUtil.formatDate(sFParams, sFParams.getDateTimeFormat(), sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString(), bmoOpportunity.getStartDate().toString()) %>
				&nbsp;&nbsp;Fin: <%= SFServerUtil.formatDate(sFParams, sFParams.getDateTimeFormat(), sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString(), bmoOpportunity.getEndDate().toString()) %> 
			</td>
		</tr>
		<tr>
			<th align="left" class="reportCellEven">
				Lugar del evento: 
			</th>  		
			<td align="left" class="reportCellEven" colspan="4">           
				<%= venue %>
			</td>
		</tr>
		<tr>
			<th align="left" class="reportCellEven">
				Notas:
			</th>
			<td align="left" class="reportCellEven" colspan="4">
				Personas: <%= bmoOpportunityDetail.getGuests().toHtml() %>; <%= HtmlUtil.newLineToBr(bmoOpportunity.getDescription().toHtml()) %>
			</td>
		</tr>
		<tr>
			<td>
				&nbsp;
			</td>
		</tr>
			<%
				BmoQuote bmoQuote = new BmoQuote();
				PmQuote pmQuote = new PmQuote(sFParams);
				bmoQuote = (BmoQuote)pmQuote.get(bmoOpportunity.getQuoteId().toInteger());
			%>
		<tr>
	  		<th colspan="5" class="reportHeaderCell">
	  			3. Servicios contratados:
			</th>
		</tr>
		<% 
		
		if (bmoOpportunityDetail.getShowInContract().toInteger() > 0) { // El valor lo toma del Bmo, hasta cuando se guarda el Detalle, que es cuando se crea el id de opportunityDetail
			String sql = "";
			double subTotal = 0, iva = 0;
			double subTotalGeneral = 0;
	        BmoQuoteGroup bmoQuoteGroup = new BmoQuoteGroup();
	        PmQuoteGroup pmQuoteGroup = new PmQuoteGroup(sFParams);
	        BmFilter bmFilterQG = new BmFilter();
	        bmFilterQG.setValueFilter(bmoQuoteGroup.getKind(), bmoQuoteGroup.getQuoteId().getName(), bmoQuote.getId());
	        Iterator<BmObject> quoteGroups = pmQuoteGroup.list(bmFilterQG).iterator();
	        int i = 1, y = 3;
        	while (quoteGroups.hasNext()) {
        		bmoQuoteGroup = (BmoQuoteGroup)quoteGroups.next(); 
        		boolean showitems = true;
        		if (bmoQuoteGroup.getIsKit().toBoolean() && !bmoQuoteGroup.getShowItems().toBoolean())
	             	  showitems = false;
        		
		%>		
				<tr>
					<td class="reportHeaderCell" colspan="5" style="padding-left: 9px;">
						<%= y %>.<%= i++ %>. <%= bmoQuoteGroup.getName().toHtml() %>
						<%	if (bmoQuoteGroup.getDescription().toString().length() > 0) { %>
	                            <br><%= bmoQuoteGroup.getDescription().toHtml() %>
	                    <%	} %>
					</td>
				</tr> 
				<%if (showitems) { %>
					<tr>
						<th align="left" class="reportCellEven">
							Nombre
						</th>
						<th align="center" class="reportCellEven">
							Cantidad
						</th>
						<th align="center" class="reportCellEven">
							D&iacute;as
						</th>
						<th align="right" class="reportCellEven">
							Precio
						</th>
						<th align="right" class="reportCellEven">
							Subtotal
						<th>
					</tr> 
					<%	  
	   				BmoQuoteItem bmoQuoteItem = new BmoQuoteItem();
		            PmQuoteItem pmQuoteItem = new PmQuoteItem(sFParams);
		            BmFilter bmFilterQ = new BmFilter();
		            bmFilterQ.setValueFilter(bmoQuoteItem.getKind(), bmoQuoteItem.getQuoteGroupId().getName(), bmoQuoteGroup.getId());
		            Iterator<BmObject> items = pmQuoteItem.list(bmFilterQ).iterator();
		            while(items.hasNext()) {
		                bmoQuoteItem = (BmoQuoteItem)items.next();	                  
	                %>      
	                	<tr>             
	                		<td class="reportCellEven" align ="left" colspan="">  
	                    		<% if (bmoQuote.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)) { %>	
		                    		<%= bmoQuoteItem.getBmoProduct().getName().toHtml() %>
		    					<% } else { %>
			    					<% if(bmoQuoteItem.getBmoProduct().getDisplayName().toString().equals("")){ %>
		                      			<%= bmoQuoteItem.getName().toHtml() %>
		                      			<%= bmoQuoteItem.getBmoProduct().getBrand().toHtml() %>
		                      			<%= bmoQuoteItem.getBmoProduct().getModel().toHtml() %>
		                     		<% } else { %>
										<%= bmoQuoteItem.getBmoProduct().getDisplayName().toHtml() %>
										<%= bmoQuoteItem.getBmoProduct().getBrand().toHtml() %>
		                      			<%= bmoQuoteItem.getBmoProduct().getModel().toHtml() %>
									<% } %>
			                    <% } %>
		                    	<br><span class="documentSubText">
		                    	<%= bmoQuoteItem.getDescription().toHtml() %></span>
		                    </td>
		                    <td class="reportCellEven" align="center">  
		                        <% if (bmoQuoteGroup.getShowQuantity().toBoolean()) { %> 
		                        	<%= bmoQuoteItem.getQuantity().toDouble() %>
	                            <% } %>  
		                    </td>
		                    <td class="reportCellEven" align="center">  
		                        <% if (bmoQuoteGroup.getShowQuantity().toBoolean()) { %>  
		                        	<%= bmoQuoteItem.getDays().toDouble() %>
	                            <% } %>  	
		                    </td>
		                      <td class="reportCellEven" align="right">
		                        <% if (bmoQuoteGroup.getShowPrice().toBoolean()) { %>  
	                                <%= formatCurrency.format(bmoQuoteItem.getPrice().toDouble()) %>
	                            <% } %>
	                          </td>
		                      <td class="reportCellEven" align="right">
		                        <% if (bmoQuoteGroup.getShowPrice().toBoolean()) { %>  
	                                <%= formatCurrency.format(bmoQuoteItem.getAmount().toDouble()) %>
	                            <% } %>
	                          </td>
		                <tr>
	                <% } %>	
                <% } %>
                <tr>
	                <td class="reportCellEven" align="left" colspan="3">
	                	&nbsp;
	                </td>
	                <td class="reportCellEven" align="right" >
	                	<%// if(bmoQuoteGroup.getShowAmount().toBoolean()) { %>
	                	<b>TOTAL:</b>
	                	<%// }%>
	                </td>
	                <td class="reportCellEven" align="right" >
	                	<%// if(bmoQuoteGroup.getShowAmount().toBoolean()) { %>
	                	<b><%= formatCurrency.format(bmoQuoteGroup.getAmount().toDouble())%></b>
	                	<%// }%>
                	</td>
                </tr>
                <tr>
                	<td colspan="5" class="">&nbsp;</td>
                </tr>
            <% } %> 
        
			<%	  
			BmoQuoteEquipment bmoQuoteEquipment = new BmoQuoteEquipment();
		    PmQuoteEquipment pmQuoteEquipment = new PmQuoteEquipment(sFParams);
		    BmFilter bmFilterEq = new BmFilter();
		    bmFilterEq.setValueFilter(bmoQuoteEquipment.getKind(), bmoQuoteEquipment.getQuoteId().getName(), bmoQuote.getId());
		    Iterator<BmObject> items = pmQuoteEquipment.list(bmFilterEq).iterator();
		    int iEquipment = 1;
		    while(items.hasNext()) {
		        bmoQuoteEquipment = (BmoQuoteEquipment)items.next();
				if (iEquipment == 1) {        
			       	%>
			       	<tr>
			      		<td class="reportHeaderCell" colspan="5">
			  				<%= y %>.<%= i++ %>. Recursos
						</td>
			     	</tr> 
			     	<tr>
				     	<td class="reportCellEven" align ="left"">
				     		<b>Nombre</b>
				     	</td>
				     	<td class="reportCellEven" align ="center">
				     		<b>Cantidad</b> 
				     	</td>
				     	<td class="reportCellEven" align="center">
				     		<b>D&iacute;as</b>
				     	</td> 	
				     	<td class="reportCellEven" align="right">
				     		<b>Precio</b>
				     	</td>
				     	<td class="reportCellEven" align="right">
				     		<b>Subtotal</b>
				     	</td>
			     	</tr> 
		     	<% 	}%>
		        <tr>	                  
			        <td class="reportCellEven" align ="left" >  
			        	<%= bmoQuoteEquipment.getName().toHtml() %>
			        	<%= bmoQuoteEquipment.getDescription().toHtml() %>
			        </td>
			        <td class="reportCellEven" align="center">  
			        	<% if (bmoQuote.getShowEquipmentQuantity().toBoolean()) { %> 
			        		<%= bmoQuoteEquipment.getQuantity().toInteger() %>
			        	<% } %>  
			        </td>
			        <td class="reportCellEven" align="center">  
			        	<% if (bmoQuote.getShowEquipmentQuantity().toBoolean()) { %>  
			        		<%= bmoQuoteEquipment.getDays().toDouble() %>
			        	<% } %>  	                        	
			        </td>
			        <td class="reportCellEven" align="right">
			        	<% if (bmoQuote.getShowEquipmentPrice().toBoolean()) { %>  
			        		<%= formatCurrency.format(bmoQuoteEquipment.getPrice().toDouble()) %>
			        	<% } %>
			        </td>
			        <td class="reportCellEven" align="right">
			        	<%= formatCurrency.format(bmoQuoteEquipment.getAmount().toDouble()) %>
			        </td>
		        <tr>
		   	<% 
		   		iEquipment++;
		    }
		    if (iEquipment > 1) {
			    %>
		   		<tr>
				   	<td class="reportCellEven" align="left" colspan="3">
				   		&nbsp;
				   	</td>
				   	<td class="reportCellEven" align="right">
				   		<b>TOTAL:</b>
				   	</td>
				   		<%                    	
					   	//Obtener el total del Recurso
					   	PmConn pmConnQuoteEquipments = new PmConn(sFParams);
					   	pmConnQuoteEquipments.open();
				
					   	double totalEquipment = 0;
				
					   	sql = " SELECT SUM(qoeq_amount) AS totalEquipment FROM quoteequipments " +
					   			" WHERE qoeq_quoteid = " +  bmoQuote.getId();
				
					   	pmConnQuoteEquipments.doFetch(sql);
					   	if (pmConnQuoteEquipments.next()) {
					   		totalEquipment = pmConnQuoteEquipments.getDouble("totalEquipment");
					   	}
					   	pmConnQuoteEquipments.close(); 
					   	%>
					   	<td class="reportCellEven" align="right" >
					   		<b><%= formatCurrency.format(totalEquipment)%></b>
					   	</td>
	        	</tr>
		        <tr>
		        	<td colspan="5" class="">&nbsp;</td>
		        </tr>
				<%
		    }
		    
			BmoQuoteStaff bmoQuoteStaff = new BmoQuoteStaff();
		    PmQuoteStaff pmQuoteStaff = new PmQuoteStaff(sFParams);
		    BmFilter bmFilterSt = new BmFilter();
		    bmFilterSt.setValueFilter(bmoQuoteStaff.getKind(), bmoQuoteStaff.getQuoteId().getName(), bmoQuote.getId());
		    Iterator<BmObject> itemsStaff = pmQuoteStaff.list(bmFilterSt).iterator();
		    int iStaff = 1;
		    while(itemsStaff.hasNext()) {
		        bmoQuoteStaff = (BmoQuoteStaff)itemsStaff.next();
		        if (iStaff == 1) {
			       	%>
			       	<tr>
		        		<td class="reportHeaderCell" colspan="5">
			        		<%= y %>.<%= i++ %>. Personal
			        	</td>
			        </tr> 
			        <tr>
				        <td class="reportCellEven" align ="left"">
				        	<b>Nombre</b>
				        </td>
				        <td class="reportCellEven" align ="center">
				        	<b>Cantidad</b>
				        </td>
				        <td class="reportCellEven" align="center">
				        	<b>D&iacute;as</b>
				        </td>
				        <td class="reportCellEven" align="right">
				        	<b>Precio</b>
				        </td>
				        <td class="reportCellEven" align="right">
				        	<b>Subtotal</b>
				        </td>
			        </tr>
      <%		} %>    
		        <tr>	                      
		        	<td class="reportCellEven" align ="left" colspan="">  
						<%= bmoQuoteStaff.getName().toHtml() %>
						<%= bmoQuoteStaff.getDescription().toHtml() %>
					</td>
					<td class="reportCellEven" align="center">  
						<% if (bmoQuote.getShowStaffQuantity().toBoolean()) { %> 
							<%= bmoQuoteStaff.getQuantity().toInteger() %>
						<% } %>  
					</td>
					<td class="reportCellEven" align="center">  
						<% if (bmoQuote.getShowStaffQuantity().toBoolean()) { %>  
							<%= bmoQuoteStaff.getDays().toDouble() %>
						<% } %>  	                        	
					</td>
					<td class="reportCellEven" align="right">
						<% if (bmoQuote.getShowStaffPrice().toBoolean()) { %>  
							<%= formatCurrency.format(bmoQuoteStaff.getPrice().toDouble()) %>
						<% } %>
					</td>
					<td class="reportCellEven" align="right">
						<%= formatCurrency.format(bmoQuoteStaff.getAmount().toDouble()) %>
					</td>
		        <tr>
	        <% 
	        	iStaff++;
		    } 
		    if (iStaff > 1) {
			    %>
		        <tr>
			        <td class="reportCellEven" align="left" colspan="3">
			        	&nbsp;
			        </td>
			        <td class="reportCellEven" align="right">
			        	<b>TOTAL:</b>
			        </td>
			        <%                    	
			        //Obtener el total del staff
			        PmConn pmConnQuoteStaff = new PmConn(sFParams);
			        pmConnQuoteStaff.open();
			        double totalStaff = 0;
		
			        sql = " SELECT SUM(qost_amount) AS totalstaff FROM quotestaff " +
			        		" WHERE qost_quoteid = " +  bmoQuote.getId();
		
			        pmConnQuoteStaff.doFetch(sql);
			        if (pmConnQuoteStaff.next()) { 
			        	totalStaff = pmConnQuoteStaff.getDouble("totalstaff");
			        }
			        pmConnQuoteStaff.close(); 
			        %>
			        <td class="reportCellEven" align="right" >
			        	<b><%= formatCurrency.format(totalStaff)%></b>
			        </td>
				 </tr>
				 <tr>
				 	<td colspan="5" class="">&nbsp;</td>
				 </tr>
		 <%		} %>
				 <tr>
					 <td colspan="5"  class="detailStart" align="left" valign="top">
					 	<p class="documentComments"><b>Notas:</b> 
					 	<%= bmoQuote.getDescription().toHtml() %></p>
					 </td>
				 </tr>
			
	 <% } else { %>
	 	<tr>
	 		<td class="reportCellEven" colspan="5">
  				<%= HtmlUtil.newLineToBr(bmoOpportunityDetail.getDescription().toHtml()) %>
			</td>
		</tr>
	  <% } %>
	  	<tr>
	  		<td colspan="5" class="">&nbsp;</td>
	  	</tr>
	  	<tr>
	  		<td colspan="5" class="reportHeaderCell">
	  			4. Costo y forma de pago:
			</td>
	 	</tr>
	 	<tr>
		 	<td class="reportCellEven">
		 		<b> Costo:</b>
	 		</td>
		 	<td class="reportCellEven" align="left">
				<%= formatCurrency.format(bmoQuote.getTotal().toDouble()) %>
			</td>
		 	<% if (bmoOpportunityDetail.getDownPayment().toDouble() > 0 ) { %>
		 		<td class="reportCellEven">
		 			<b>Anticipo no reembolsable:</b>
		 		</td>
	 			<td class="reportCellEven" align="left" colspan="2">
					<%= formatCurrency.format(bmoOpportunityDetail.getDownPayment().toDouble()) %>
				</td>	
		 	<% } else { %>
		 		<td class="reportCellEven" colspan="3">
		 			&nbsp;
		 		</td>
		 	<% } %>
	 	</tr>
	 	<tr>
		 	<td class="reportCellEven">
		 		<b>Fecha contrato:</b> 
		 	</td>
		 	<td class="reportCellEven">
		 		<%= SFServerUtil.nowToString(sFParams, sFParams.getBmoSFConfig().getPrintDateTimeFormat().toHtml()) %>
		 	</td>
		 	<td class="reportCellEven">
		 		<b>Fecha l&iacute;mite de pago:</b> 
		 	</td>
		 	<td class="reportCellEven" colspan="2">
		 		<% if(!(bmoOpportunityDetail.getPaymentDate().toHtml().equals(""))) { %>
		 			<%= SFServerUtil.formatDate(sFParams, sFParams.getDateFormat(), sFParams.getBmoSFConfig().getPrintDateFormat().toString(), bmoOpportunityDetail.getPaymentDate().toString()) %> 
		 		<% } %>
		 	</td>
	 	</tr>
	 	<tr>
	  		<td colspan="5">&nbsp;</td>
	  	</tr>
	  	<tr>
	  		<td colspan="2" class="reportHeaderCell">
	  			5. Costo de hora extra:
	  		</td>
	  		<td colspan="3" class="reportHeaderCell">
	  			6. Dep&oacute;sito en garant&iacute;a:
	  		</td>
	  	</tr>
	  	<tr>
		  	<td class="reportCellEven">
		  		<b>Costo:</b> 
	  		</td>
	  		<td class="reportCellEven" align="left">
				<%= formatCurrency.format(bmoOpportunityDetail.getExtraHour().toDouble()) %>
			</td>
		  	<td class="reportCellEven">
		  		<b>Dep&oacute;sito:</b>
	  		</td>
	  		<td class="reportCellEven" align="left">
				<%= formatCurrency.format(bmoOpportunityDetail.getDeposit().toDouble()) %>
			</td>
		  	<td class="reportCellEven">
		  		&nbsp;
		  	</td>
	  	</tr>
  	</table>
	<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0" style="font-size: 12px">
	  	<tr>
	  		<td colspan="5">&nbsp;</td>
	  	</tr>
	  	<tr>
	  		<td colspan="2" class="reportHeaderCellCenter">
	  			DREA:
			</td>
  			<td colspan="3" class="reportHeaderCellCenter">
	  			EL CONTRATANTE:
			</td>
		</tr> 
		<tr>
			<td colspan="5" height="40px">
				&nbsp;
			</td>
		</tr> 
	    <tr>
	        <th align="center" colspan="2">
	           ________________________________
	        </th>
	        <th align="center" colspan="3">
	           ________________________________
	        </th>
	    </tr>
	    <tr>
	        <th align="center" colspan="2" class="documentTitleCaption">
	            <%= bmoCompany.getLegalName().toHtml().toUpperCase() %>,<br>
	            A TRAV&Eacute;S DE SU REPRESENTANTE EL SE&Ntilde;OR<br>
	            <%= bmoCompany.getLegalRep().toHtml().toUpperCase() %>
	        </th>
	        <th align="center" colspan="3" class="documentTitleCaption">
	            <%= fullName %>
	        </th>
	    </tr>  
	</table>
	
	<p style="page-break-before: always">&nbsp; </p>
	<% if (bmoOpportunity.getBmoOrderType().getType().toString().equals("" + BmoOrderType.TYPE_RENTAL)) { %>
		<p  class="documentComments" align="justify"><b>CONTRATO DE PRESTACI&Oacute;N DE SERVICIOS</b> (EL "CONTRATO") QUE CELEBRAN POR UNA PARTE, <b>DREA
		PRODUCCIONES S. DE R.L. DE C.V.</b>, A LA QUE EN LO SUCESIVO SE LE DENOMINAR&Aacute; "<b>DREA</b>", REPRESENTADA POR
		<b>DIEGO IGNACIO RIPOLL BERUBEN</b> Y POR OTRA PARTE LA PERSONA CUYO NOMBRE APARECE EN EL ANVERSO DEL PRESENTE INSTRUMENTO,
		A QUIEN EN LOS SUCESIVO SE LE DENOMINAR&Aacute; EL "<b>CONTRATANTE</b>",
		AL TENOR DE LAS SIGUIENTES DECLARACIONES Y CL&Aacute;USULAS:
		</p> 
		<p class="documentComments" align="center" style="background-color : #F0F0F0;">
		    <span>DECLARACIONES:</span>
		</p>
		<p class="documentComments" align="justify">
		<b>I. Declara DREA, a trav&eacute;s de su Representante Legal:</b><br>
		&nbsp;(a) Ser una sociedad mercantil debidamente constituida de conformidad con las leyes de los Estados Unidos Mexicanos.<br>
		&nbsp;(b) Que su Representante Legal, cuenta con las facultades suficientes para celebrar el presente Contrato y obligarse en los t&eacute;rminos del mismo.<br>
		&nbsp;(c) Que los Servicios que presta forman parte de su Objeto Social.<br>
		&nbsp;(d) Que es su voluntad celebrar el presente Contrato con el Contratante.<br><br>
		
		<b>II.  Declara el Contratante, a trav&eacute;s de su Representante Legal:</b><br>
		
		&nbsp;(a) Que cuenta con capacidad jur&iacute;dica y econ&oacute;mica para celebrar el presente Contrato.<br>
		&nbsp;(b) Que es su voluntad y tiene inter&eacute;s de celebrar el presente Contrato con DREA para que &eacute;sta
		le preste los servicios establecidos en el anverso del presente documento, por lo que desea obligarse conforme a los t&eacute;rminos y condiciones aqu&iacute; establecidos.<br><br>
		
		Conformes con las anteriores Declaraciones, DREA y el Contratante (conjuntamente las "Partes") se reconoce
		mutuamente el car&aacute;cter con que comparecen a celebrar el presente Contrato, por lo que convienen en obligarse de conformidad con las siguientes:
		</p>
		<p class="documentComments" align="center" style="background-color : #F0F0F0;">
		    <span>CL&Aacute;USULAS:</span>
		</p>
		<p class="documentComments" align="justify">
		<b>PRIMERA. Definiciones. </b>Acuerdan las Partes que para efectos del presente Contrato, a excepci&oacute;n de que se&ntilde;ale
		lo contrario o a excepci&oacute;n de que el contexto requiera lo contrario, los t&eacute;rminos en may&uacute;sculas
		tendr&aacute;n el siguiente significado,  sin perjuicio de que sean utilizados en singular o plural:<br>
		&nbsp;1.1.&nbsp; Car&aacute;tula: significa el anverso del presente documento.<br>
		&nbsp;1.2.&nbsp; Contratante: significa la persona f&iacute;sica cuyas generales se describen en el punto 1 de la Car&aacute;tula.<br>
		&nbsp;1.3.&nbsp; Evento: significa el evento social en el que se prestar&aacute;n los Servicios, y cuya fecha, duraci&oacute;n,
		direcci&oacute;n se especifican en el punto 2 de la Car&aacute;tula.<br>
		&nbsp;1.4.&nbsp; Servicios: significa los servicios profesionales de iluminaci&oacute;n, audio, instalaci&oacute;n, provisi&oacute;n
		de materiales y mobiliario que se describen en el punto 3 de la Car&aacute;tula.<br><br>
		
		<b>SEGUNDA. Objeto.</b> En virtud del presente Contrato, DREA prestar&aacute; en favor del Contratante los Servicios dentro del Evento, con los medios humanos, t&eacute;cnicos, materiales y mobiliario necesario seg&uacute;n han quedado descritos en la Car&aacute;tula.<br><br>
		
		<b>TERCERA. Duraci&oacute;n.</b> El presente Contrato estar&aacute; vigente a partir de la fecha de firma del mismo y concluir&aacute; una vez concluido el Evento en la fecha y hora establecidos en el punto 2 de la Car&aacute;tula.<br><br>
		
		
		<b>CUARTA. Contraprestaci&oacute;n y forma de pago.</b> El Contratante pagar&aacute; a DREA como contraprestaci&oacute;n por los Servicios, la cantidad establecida en el punto 4 de la Car&aacute;tula m&aacute;s el impuesto al valor agregado (IVA) correspondiente (el "Precio"). Los pagos del Precio deber&aacute;n realizarse en las fechas y montos especificados en el punto 4 de la Car&aacute;tula.
		<br><br>
		Acuerdan las Partes que el Contratante deber&aacute; cubrir el 100% del Precio por lo menos con 15 d&iacute;as naturales de anticipaci&oacute;n a la fecha del Evento. En caso de que el Contratante no cubra el 100% del Precio con la anticipaci&oacute;n debida, DREA estar&aacute; facultado para rescindir el presente Contrato, sin necesidad de declaraci&oacute;n judicial o arbitral previa, mediando simple aviso al Contratante. En su caso, el Contratante se har&aacute; acreedor de una pena convencional del 100% del Precio, teniendo DREA derecho a cobrar dicha pena de los montos efectivamente pagados por el Contratante, debiendo el Contratante cubrir el resto dentro de los 30 (treinta) d&iacute;as siguientes al aviso de rescisi&oacute;n que al efecto env&iacute;e DREA.<br><br>
		
		<b>QUINTA. Prestaci&oacute;n de los Servicios.</b> DREA llevar&aacute; a cabo la prestaci&oacute;n de Servicios &uacute;nicamente en el d&iacute;a y en las horas de Evento. En caso de que el lugar en donde se llevar&aacute; a cabo el Evento no re&uacute;na las caracter&iacute;sticas necesarias para la colocaci&oacute;n e instalaci&oacute;n del equipo, ser&aacute; decisi&oacute;n discrecional de DREA la forma en la que desempe&ntilde;ar&aacute; esta tarea, en cuyo caso, DREA tendr&aacute; derecho a requerir un pago adicional por las instalaciones extras que debieran llevarse a cabo.
		<br><br>
		Acuerdan las Partes que en caso de que posterior a la fecha de firma del presente Contrato, el Contratante desee aumentar los Servicios, las Partes deber&aacute;n acordar dicho aumento mediante la firma de un Addendum al presente Contrato, en donde se especificar&aacute;n los servicios extras contratados y la contraprestaci&oacute;n de los mismos. En todo caso, el precio final pactado con los servicios extras no podr&aacute; ser menor al establecido en el presente Contrato y deber&aacute; ser igualmente liquidado al 100% con por los menos 15 d&iacute;as naturales de anticipaci&oacute;n a la fecha del Evento, conforme a la cl&aacute;usula CUARTA del presente Contrato.<br><br>
		
		<b>SEXTA. Vi&aacute;ticos.</b> En los casos en los que el Evento se lleve a cabo fuera del &aacute;rea Metropolitana de Guadalajara, el Contratante tendr&aacute; la obligaci&oacute;n de cubrir con todos los gastos originados por el transporte del mobiliario, personal y equipo necesario para la prestaci&oacute;n de los Servicios al lugar del Evento, incluyendo sin limitar gastos de flete, combustible, personal, comida, y hospedaje en su caso.<br><br>
		
		<b>S&Eacute;PTIMA. Horas Extraordinarias.</b> DREA se obliga a prestar sus Servicios &uacute;nicamente durante la duraci&oacute;n del Evento en el horario especificado en el punto 2 de la Car&aacute;tula. En caso de que durante el desarrollo del evento se requiera prestar los Servicios durante horas extras, el Contratante solicitar&aacute; la prestaci&oacute;n de los Servicios al personal designado de DREA, quien discrecionalmente decidir&aacute; la prestaci&oacute;n o no de los Servicios extras. En su caso, el Contratante se obliga a pagar la cantidad establecida en el punto 5 de la Car&aacute;tula por cada hora extra de Servicios, sin necesidad de reiterarlo entendi&eacute;ndose que al firmar el presente Contrato, est&aacute; completamente de acuerdo con el precio establecido por cada hora extra de Servicio.<br><br>
		
		<b>OCTAVA. Dep&oacute;sito.</b> El Contratante se obliga a otorgar la cantidad especificada en el punto 6 de la Car&aacute;tula por concepto de dep&oacute;sito (el "Dep&oacute;sito")  para resarcir cualquier tipo de da&ntilde;o, alteraci&oacute;n o en determinado momento, destrucci&oacute;n del equipo o mobiliario utilizado para la prestaci&oacute;n de los Servicios. En caso de que el costo de reparaci&oacute;n o reposici&oacute;n exceda el monto del Dep&oacute;sito, el Contratante deber&aacute; cubrir con el resto de monto total resultante de reparar y/o reponer el mobiliario dentro de los 10 (diez) d&iacute;as naturales siguientes a la conclusi&oacute;n del Evento. De igual manera, el inclumplimiento del Contratante en cualquiera de sus obligaciones establecidas en el presente Contrato, facultar&aacute; a DREA para hacer uso de dicho dep&oacute;sito para cubrir con los gastos generados por la negligencia o descuido del Contratante.<br><br>
		<b>NOVENA. Cancelaci&oacute;n.</b> En caso de que el Contratante desee terminar el presente Contrato, &eacute;ste se har&aacute; acreedor a una pena convencional del 50% del Precio en los casos en los que la terminaci&oacute;n se lleve a cabo con por lo menos 90 d&iacute;as naturales previos a la fecha del Evento. En caso de que la terminaci&oacute;n se lleve a cabo con una anticipaci&oacute;n menor a 90 d&iacute;as naturales, el Contratante se har&aacute; acreedor a una pena convencional por el 100% del Precio.<br><br>
		
		<b>D&Eacute;CIMA. Da&ntilde;os, p&eacute;rdidas o robos.</b> El Contratante deber&aacute; responder por cualquier da&ntilde;o parcial o total al equipo y/o mobiliario y/o bienes propiedad de DREA, as&iacute; como cualquier da&ntilde;o y/o perjuicio que sufra el personal de DREA durante el tiempo en el que se encuentra prestando los Servicios.<br><br>
		
		<b>D&Eacute;CIMA PRIMERA. Instalaciones.</b> El Contratante ser&aacute; responsable de suministrar la energ&iacute;a el&eacute;ctrica as&iacute; como las tomas de agua indispensables para llevar a cabo la prestaci&oacute;n de los Servicios, cualquier falta en estos suministros, ser&aacute; imputable al Contratante. En caso de que el Evento se lleve  cabo en un lugar que se encuentre a la intemperie, el Contratante ser&aacute; responsable de proporcionar los medios adecuados e instrumentos necesarios para la protecci&oacute;n del equipo y bienes de DREA, haci&eacute;ndose responsable por los da&ntilde;os y perjuicios que pudieran surgir por el incumplimiento de esta obligaci&oacute;n. Lo dispuesto en la presente cl&aacute;usula no ser&aacute; aplicable al Contratante cuando, dentro de los Servicios a prestar por DREA, se encuentre precisamente el suministro de energ&iacute;a el&eacute;ctrica, en cuyo caso DREA ser&aacute; responsable del correcto funcionamiento de la planta de luz.<br><br>
		
		<b>D&Eacute;CIMA SEGUNDA. Gastos por Personal.</b> El Contratante ser&aacute; responsable de otorgar al personal de DREA comida y bebidas durante todo el tiempo que dure la prestaci&oacute;n de Servicios, adem&aacute;s de cubrir con los Vi&aacute;ticos seg&uacute;n se establece en la Cl&aacute;usula Sexta del presente Contrato.<br><br>
		
		<b>D&Eacute;CIMA TERCERA. Permisos y Licencias.</b> Es responsabilidad del Contratante el adquirir o renovar cualquier Permiso, Licencia o Autorizaci&oacute;n que se requiera para llevar a cabo el Evento.<br><br>
		
		<b>D&Eacute;CIMA CUARTA. Relaciones Laborales.</b> Las Partes reconocen que no existe subordinaci&oacute;n ni relaci&oacute;n de trabajo alguna entre ellas derivadas del cumplimiento de las obligaciones a que se refiere este Contrato. Por ello, las Partes se liberan mutua y expresamente de cualquier responsabilidad laboral o reclamaci&oacute;n alguna que pudiera surgir con motivo de conflictos individuales o colectivos de car&aacute;cter laboral. Por lo que las Partes convienen en indemnizar y sacar en paz  a salvo a la otra parte por cualquier reclamaci&oacute;n o procedimiento de car&aacute;cter laboral o de seguridad social, derivados del presente Contrato, que se iniciara por cualquiera de ellas o por cualquier tercero en contra de cualquiera de las partes.<br><br>
		
		<b>D&Eacute;CIMA QUINTA. Confidencialidad.</b> El Contratante reconoce y acepta que en virtud del presente Contrato podr&iacute;a tener acceso a "Informaci&oacute;n Confidencial" propiedad de DREA, por lo que reconoce  y se obliga a que toda la Informaci&oacute;n Confidencial que sea proporcionada o revelada, de manera oral, escrita, gr&aacute;fica, electr&oacute;nica o cualesquiera otra, en relaci&oacute;n con las actividades, productos y servicios, desarrollos, planes, proyectos, c&aacute;lculos, as&iacute; como los t&eacute;rminos y condiciones del presente Contrato o cualquier otra informaci&oacute;n que no sea p&uacute;blica y que conozca o le sea revelada por DREA en virtud de este Contrato, es y ser&aacute; considerada como Informaci&oacute;n Confidencial (La "Informaci&oacute;n Confidencial"), Por lo que el Contratante se obliga a no revelar ning&uacute;n tipo de Informaci&oacute;n Confidencial y/o secreto industrial que sean propiedad de DREA.<br><br>
		
		<b>D&Eacute;CIMA SEXTA. Subcontrataci&oacute;n.</b> Las Partes est&aacute;n de acuerdo en que DREA pueda subcontratar con alg&uacute;n tercero parte o el total de los Servicios que se encomiendan mediante el presente Contrato.<br><br>
		
		<b>D&Eacute;CIMA S&Eacute;PTIMA. Miscel&aacute;neos.</b><br>
		&nbsp;<b>17.1.&nbsp; Totalidad del Contrato.</b> Este Contrato, constituye el acuerdo completo de voluntades entre las Partes, y reemplaza cualquier entendimiento anterior, verbal o escrito, entre las Partes. Por tal motivo, ni DREA ni el Contratante estar&aacute;n vinculados por cualesquiera t&eacute;rminos o condiciones contenidos en cualquier presupuesto, orden, liberaci&oacute;n, factura, confirmaci&oacute;n u otro documento entregado o propuesto por alguno de ellos y que imponga t&eacute;rminos o condiciones diferentes a los t&eacute;rminos y condiciones contenidos en este Contrato.<br>
		
		&nbsp;<b>17.2.&nbsp; Cesi&oacute;n.</b> Acuerdan las Partes que el Contratante no podr&aacute; ceder los derechos del presente Contrato.<br>
		
		&nbsp;<b>17.3.&nbsp; Ley Aplicable y Jurisdicci&oacute;n.</b> Todo litigio, controversia o reclamaci&oacute;n resultante de este contrato o relativo a este contrato, su incumplimiento, resoluci&oacute;n o nulidad, se resolver&aacute; mediante arbitraje de conformidad con el Reglamento de Arbitraje de la C&aacute;mara Nacional de Comercio de la Ciudad de M&eacute;xico. El lugar del arbitraje ser&aacute; Guadalajara, Jalisco M&eacute;xico. El idioma que se utilizar&aacute; en el procedimiento arbitral ser&aacute; Espa&ntilde;ol. El derecho aplicable a la controversia ser&aacute; las Leyes de los Estados Unidos Mexicanos.<br>
		
		&nbsp;<b>17.4.&nbsp; Avisos y Notificaciones.</b> Todas las notificaciones y comunicaciones requeridas o permitidas por medio del presente Contrato, ser&aacute;n hechas por escrito y deber&aacute;n de ser env&iacute;adas ya sea por mensajer&iacute;a privada o por correo certificado o registrado, o por entrega personal u otro medio indubitable a las direcciones se&ntilde;aladas en el anverso del presente Contrato.<br>
		
		<%
			String nowMonth = SFServerUtil.nowToString(sFParams, "MM");
			
			switch (Integer.parseInt(nowMonth)) {
				        case 1:
				            nowMonth = "Enero";
				            break;
				        case 2:
				            nowMonth = "Febrero";
				            break;
				        case 3:
				            nowMonth = "Marzo";
				            break;
				        case 4:
				            nowMonth = "Abril";
				            break;
				        case 5:
				            nowMonth = "Mayo";
				            break;
				        case 6:
				            nowMonth = "Junio";
				            break;
				        case 7:
				            nowMonth = "Julio";
				            break;
				        case 8:
				            nowMonth = "Agosto";
				            break;
				        case 9:
				            nowMonth = "Septiembre";
				            break;
				        case 10:
				            nowMonth = "Octubre";
				            break;
				        case 11:
				            nowMonth = "Noviembre";
				            break;
				        case 12:
				            nowMonth = "Diciembre";
				            break;
				        default:
				            nowMonth = "n/d";
				  	 }
		
		%>
		
		&nbsp;<b>17.5.&nbsp; T&iacute;tulos.
		</b> Los encabezados de cada una de las Cl&aacute;usulas que integran este documento, 
		se han incorporado &uacute;nicamente por referencia de las Partes y no deber&aacute;n considerarse parte del Contrato 
		para efectos de interpretaci&oacute;n. Le&iacute;do que fue el presente instrumento por las partes y comprendido su contenido, 
		alcance y fuerza legal, lo firman por duplicado, de manera libre, espont&aacute;nea y voluntaria, 
		el d&iacute;a <%= SFServerUtil.nowToString(sFParams, "dd") %> del mes de <%= nowMonth %> 
		del a&ntilde;o <%= SFServerUtil.nowToString(sFParams, "YYYY") %> en Guadalajara, Jalisco.<br>
		</p>
		<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0" class="documentComments">
		    <tr align="center">
		        <td>
		            <b>"DREA"</b>
		        </td>
		        <td>
		            <b>"EL CONTRATANTE"</b>
		        </td>
		    </tr>
		
		    <tr align="center">
		        <td align="center"><br><p><br>
		            _______________________________________
		        </td>
		        <td align="center"><br><p><br>
		            _______________________________________
		        </td>
		    </tr>
		    <tr>
		        <td align="center" class="documentComments">
		        	<%= bmoCompany.getLegalName().toString().toUpperCase() %><br>
		            A TRAV&Eacute;S DE SU REPRESENTANTE EL SE&Ntilde;OR<br>
		            <%= bmoCompany.getLegalRep().toString().toUpperCase() %>
		        </td>
		        <td align="center" class="documentComments">
		            <%= fullName %>
		        </td>
		    </tr> 
		
		</table>
	<% } %>
	<% 	} catch (Exception e) { 
		String errorLabel = "Error de Contrato";
		String errorText = "El Contrato no pudo ser desplegado exitosamente. Es necesario completar todos los datos faltantes: Detalle de Oportunidad.";
		String errorException = e.toString();
		
		response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);
		}
	
	%>
</body>
</html>