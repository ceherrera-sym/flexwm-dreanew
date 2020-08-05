<%@page import="com.flexwm.server.ev.PmVenue"%>
<%@page import="com.flexwm.shared.ev.BmoVenue"%>
<%@page import="com.flexwm.server.cm.PmCustomerAddress"%>
<%@page import="com.flexwm.shared.cm.BmoCustomerAddress"%>
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
	String title = "Hoja de Pedido";
	
	BmoOrderType bmoOrderType = new BmoOrderType();
	PmOrderType pmOrderType = new PmOrderType(sFParams);
	
	BmoOrder bmoOrder = new BmoOrder();
	PmOrder pmOrder = new PmOrder(sFParams);

	BmoPropertySale bmoPropertySale = new BmoPropertySale();
	BmoWFlowType bmoPropertySaleWFlowType = new BmoWFlowType();
	BmoOrderProperty bmoOrderProperty = new BmoOrderProperty();
	BmoDevelopmentBlock bmoDevelopmentBlock = new BmoDevelopmentBlock();
	BmoOrderPropertyModelExtra bmoOrderPropertyModelExtra = new BmoOrderPropertyModelExtra();
	BmoRaccount bmoRaccount= new BmoRaccount();

	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)sFParams.getBmoProgram(bmoOrder.getProgramCode());		
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
if(!(sFParams.hasPrint(bmoProgram.getCode().toString()))) { 
%>
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
	if (sFParams.hasRead(bmoProgram.getCode().toString())) {

		NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(Locale.US);
		
	    // Obtener parametros
	    int projectId = 0;
	    if (isExternal) projectId = decryptId;
	    else projectId = Integer.parseInt(request.getParameter("foreignId"));    
	
	    
	    //if (bmoOrderType.getType().equals("" + BmoOrderType.TYPE_RENTAL)) {
			//System.out.println("Renta");
		
		BmoProject bmoProject = new BmoProject();
		PmProject pmProject = new PmProject(sFParams);
		bmoProject = (BmoProject)pmProject.get(projectId);
			
		BmoWFlowType bmoProjectWFlowType = new BmoWFlowType();
		PmWFlowType pmWFlowTypeProject = new PmWFlowType(sFParams);
		if (bmoProject.getWFlowTypeId().toInteger() > 0)
			bmoProjectWFlowType = (BmoWFlowType)pmWFlowTypeProject.get(bmoProject.getWFlowTypeId().toInteger());
			
			
		// Si es llamada externa, utilizar llave desencriptada, en caso contrario utilizar llave de la oportunidad
		bmoOrder = (BmoOrder)pmOrder.get(bmoProject.getOrderId().toInteger());
		
		bmoOrderType = (BmoOrderType)pmOrderType.get(bmoOrder.getOrderTypeId().toInteger());	
		
		// Empresa
		BmoCompany bmoCompany = new BmoCompany();
		PmCompany pmCompany = new PmCompany(sFParams);
		bmoCompany = (BmoCompany)pmCompany.get(bmoOrder.getCompanyId().toInteger());
		
		// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
		String logoURL ="";
		if (!bmoCompany.getLogo().toString().equals(""))
			logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
		else 
			logoURL = sFParams.getMainImageUrl();

		if (bmoOrderType.getType().equals("" + BmoOrderType.TYPE_RENTAL)) {
			
			PmConn pmConn = new PmConn(sFParams);

			String customer = "";
			if (bmoProject.getBmoCustomer().getCustomertype().toChar() == BmoCustomer.TYPE_PERSON) {
				customer =  bmoProject.getBmoCustomer().getFirstname().toHtml() + " " +
							bmoProject.getBmoCustomer().getFatherlastname().toHtml() + " " +
							bmoProject.getBmoCustomer().getMotherlastname().toHtml();
			} else {
				customer = bmoProject.getBmoCustomer().getLegalname().toHtml();
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
			if (bmoProject.getVenueId().toInteger() > 0 ) {
				bmoVenue = (BmoVenue)pmVenue.get(bmoProject.getVenueId().toInteger());

				if (bmoVenue.getHomeAddress().toInteger() > 0) {
					venue = bmoVenue.getName().toHtml() + ": " + bmoProject.getHomeAddress().toString();
				} else {
				venue = bmoVenue.getName().toHtml() + ": " +
						bmoVenue.getStreet().toHtml() + " " +
						" #" + bmoVenue.getNumber().toHtml() + " " +
						bmoVenue.getNeighborhood().toHtml() + ".";
				
				venueDir = bmoVenue.getBmoCity().getName().toHtml() + ", " +
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
			<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
				<tr>
					<td align="left" width="" rowspan="7" valign="top">
						<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
					</td>
					<td colspan="4" class="reportTitleCell">
						Pedido
					</td>
				</tr>
				<tr>       
					<th align = "left" class="reportCellEven">
						Empresa/Cliente: 
					</th>
					<td align = "left" class="reportCellEven">
						<%= customer %>
					</td>
					<th align = "left" class="reportCellEven">
						Fecha/Hora Evento: 
					</th>
					<td align = "left" class="reportCellEven">
						<% if (!bmoProject.getStartDate().toString().equals("")) { %>
							de: <%= SFServerUtil.formatDate(sFParams, sFParams.getDateTimeFormat(), sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString(), bmoProject.getStartDate().toString()) %>      				
						<% } %>
						<% if (!bmoProject.getEndDate().toString().equals("")) { %>
							&nbsp;a:&nbsp; <%= SFServerUtil.formatDate(sFParams, sFParams.getDateTimeFormat(), sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString(), bmoProject.getEndDate().toString()) %>
						<% } %>
					</td>
				</tr>
				<tr>
					<th align = "left" class="reportCellEven">
						Proyecto: 
					</th>
					<td align = "left" class="reportCellEven">
						<%= bmoProject.getCode().toHtml() + " - " + bmoProject.getName().toHtml() %> 
					</td>
					<th align = "left" class="reportCellEven">
						Tipo de Evento:
					</th>
					<td align = "left" class="reportCellEven">
						<%= bmoProjectWFlowType.getName().toHtml() %>
					</td>
				</tr>
				<tr>
					<th align = "left" class="reportCellEven">
						Lugar: 
					</th>
					<td align = "left" class="reportCellEven">
						<%= venue%> 
					</td>
					<%
		      		if (bmoProject.getVenueId().toInteger() > 0) {
						if (bmoVenue.getHomeAddress().toInteger() > 0) {
					%>
						<td class="reportCellEven" colspan="2">&nbsp;</td>
					<% } else { %>
						<th align = "left" class="reportCellEven">
							Ciudad: 
						</th>
						<td align = "left" class="reportCellEven">
							<%= venueDir %>
						</td>
					<%	}
					} %>
				</tr>
				<tr>
					<th align = "left" class="reportCellEven">
						Ejecutivo Comercial: 
					</th>
					<td align = "left" class="reportCellEven">
						<%= bmoProject.getBmoUser().getFirstname().toHtml()%> 
						<%= bmoProject.getBmoUser().getFatherlastname().toHtml()%>
						<%= bmoProject.getBmoUser().getMotherlastname().toHtml()%>
					</td>
					<th align = "left" class="reportCellEven">
						Fecha Impresi&oacute;n:
					</th>
					<td align = "left" class="reportCellEven">
						<%= SFServerUtil.nowToString(sFParams, sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString()) %>
					</td>
				</tr>
				<tr>
					<th align = "left" class="reportCellEven">
						Moneda: 
					</th>
					<td align = "left" class="reportCellEven">
						<%= bmoOrder.getBmoCurrency().getCode().toHtml()%> - <%= bmoOrder.getBmoCurrency().getName().toHtml()%>
					</td>
					<th align = "left" class="reportCellEven">
						Estatus Pedido: 
					</th>
					<td align = "left" class="reportCellEven">
						<%= bmoOrder.getStatus().getSelectedOption().getLabeltoHtml()%>
					</td>
				</tr>
				<tr>
					<th align = "left" class="reportCellEven">
						Estatus Entrega:
					</th>
					<td align = "left" class="reportCellEven" colspan="3">
						<%= bmoOrder.getDeliveryStatus().getSelectedOption().getLabeltoHtml()%>
					</td>
				</tr>
			</table>
			<br>
			<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		    	<tr>       
		        	<td class="reportHeaderCell" width="50%" >Rubro</td>
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
			                  	<%= i++ %>. <%= bmoOrderGroup.getName().toHtml() %>
			                  	<%	if (bmoOrderGroup.getDescription().toString().length() > 0) { %>
			                  		<br><%= bmoOrderGroup.getDescription().toHtml() %>
	                            <%	} %>
			                  </td>
			                  <td  class="reportHeaderCellRight">
			                  	<% if (bmoOrderGroup.getShowAmount().toBoolean()) { %>
			                  		<%= formatCurrency.format(bmoOrderGroup.getAmount().toDouble()) %>&nbsp;
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
							
          	  			   boolean showitems = true;
	             	 
	             		  if (bmoOrderGroup.getIsKit().toBoolean() && !bmoOrderGroup.getShowItems().toBoolean())
	             	 	  showitems = false;
	             		  
	             		 if (showitems) {
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
				                  <td class="reportCellEven" align ="left">
				                  	<% if(bmoOrderItem.getBmoProduct().getDisplayName().toString().equals("")) { %>
		                      			<%= bmoOrderItem.getName().toHtml() %>
		                      			<%= bmoOrderItem.getBmoProduct().getBrand().toHtml() %>
		                      			<%= bmoOrderItem.getBmoProduct().getModel().toHtml() %>
		                     		<% } else { %>
										<%= bmoOrderItem.getBmoProduct().getDisplayName().toHtml() %>
										<%= bmoOrderItem.getBmoProduct().getBrand().toHtml() %>
		                      			<%= bmoOrderItem.getBmoProduct().getModel().toHtml() %>
									<% } %>
				                  	<br>
				                  	<span class="documentSubText">
				                  		<%= bmoOrderItem.getDescription().toHtml() %>
				                  	</span>
				                  </td>
				                  <td class="reportCellEven" align="center">
				                  	<% if (bmoOrderGroup.getShowQuantity().toBoolean()) { %> 
				                  		<%= bmoOrderItem.getQuantity().toDouble() %>
			                  		<% } %>  
				                  </td>
				                  <td class="reportCellEven" align="center">
				                  	<% if (bmoOrderGroup.getShowQuantity().toBoolean()) { %>  
				                  		<%= bmoOrderItem.getDays().toDouble() %>
			                  		<% } %>  	                        	
				                  </td>
				                  <td class="reportCellEven" align="right">
				                  	<% if (bmoOrderGroup.getShowPrice().toBoolean()) { %>  
				                  		<%= formatCurrency.format(bmoOrderItem.getPrice().toDouble()) %>
			                  		<% } %>   
				                  </td>
				                  <td class="reportCellEven" align="right">
				                  	<% if (bmoOrderGroup.getShowPrice().toBoolean()) { %>  
				                  		<%= formatCurrency.format(bmoOrderItem.getAmount().toDouble()) %>
			                  		<% } %>    
				                  </td>
				                  <%
		                         
				                  // Si es Kit
				                  if (bmoOrderGroup.getIsKit().toBoolean()) {
				                	  if (index == 1) { %>
				                  		<td class="reportCellEven" align ="center" colspan="2" rowspan="<%= count %>" style="white-space: nowrap;" valign="center">&nbsp;
					                  		<% if (bmoOrderGroup.getShowGroupImage().toBoolean() 
					                  				&& (bmoOrderGroup.getImage().toString().length() > 0)) {
					                  			
					                  			String blobKeyParse = HtmlUtil.parseImageLink(sFParams, bmoOrderGroup.getImage());
					                  			%> 
			                        	 			<img src="<%= blobKeyParse %>" width="300" height="200"> 
		                        	 		<% } %> 
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
				                         		<td class="reportCellEven" align ="center"  rowspan="<%= count %>" style="white-space: nowrap;" valign="center">
					                    			<img src="<%= blobKeyParse %>" width="300" height="200" padding="4">
				                    			</td>
			                    			<% }//Fin index 						                         				
	                         				// Muestra solo imgProd si es seleccionada
             								} else if (bmoOrderGroup.getShowProductImage().toBoolean() 
             										&& (bmoOrderItem.getBmoProduct().getImage().toString().length() > 0)
			                    						&& !bmoOrderGroup.getShowGroupImage().toBoolean() ) {
             										
	                         						String blobKeyParse = HtmlUtil.parseImageLink(sFParams, bmoOrderItem.getBmoProduct().getImage());
													%>
													<td class="reportCellEven" align ="center" style="white-space: nowrap;" valign="center">
							                       		<img src="<%= blobKeyParse %>" width="100" height="100"> 
						                       		</td>
				                       		<% } else  { %>
					                       		<td class="reportCellEven" align ="center" style="white-space: nowrap;" valign="center">
						                       		&nbsp;
					                       		</td>
				                       		<% }
		      	             		} %>  
			                  </tr>
			             <% 
			             	index++;
			             	} // fin de items
			              
	             		 }
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
			                    <td class="reportHeaderCell" colspan="5">
			                        Recursos
			                    </td>
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
			                    	%>                    			
			                    		<%= formatCurrency.format(totalEquipment) %>&nbsp;
			                    	<% }                    		
			                    	  pmConn.close(); 
			                        %>
			                    </td>    
			                </tr>
	                <%	} %>
		             		<tr>	                
				             	<td class="reportCellEven" align ="left">
					             	<%= bmoOrderEquipment.getBmoEquipment().getCode().toHtml() %>
					             	<%= bmoOrderEquipment.getName().toHtml() %>
					             	<%= bmoOrderEquipment.getDescription().toHtml() %>
				             	</td>
				             	<td class="reportCellEven" align="center">  
					             	<% if (bmoOrder.getShowEquipmentQuantity().toBoolean()) { %> 
					             		<%= bmoOrderEquipment.getQuantity().toInteger() %>
					             	<% } %> 
				             	</td>
				             	<td class="reportCellEven" align="center">  
					             	<% if (bmoOrder.getShowEquipmentQuantity().toBoolean()) { %> 
					             		<%= bmoOrderEquipment.getDays().toDouble() %>
					             	<% } %> 
				             	</td>
				             	<td class="reportCellEven" align="right">
					             	<% if (bmoOrder.getShowEquipmentPrice().toBoolean()) { %> 
					             		<%= formatCurrency.format(bmoOrderEquipment.getPrice().toDouble()) %>
					             	<% } %>
				             	</td>
				             	<td class="reportCellEven" align="right">
				             		<% if (bmoOrder.getShowEquipmentPrice().toBoolean()) { %>
				             			<%= formatCurrency.format(bmoOrderEquipment.getAmount().toDouble()) %>
				             		<% } %>	
				             	</td>
				             	<td class="reportCellEven" align ="left">
				             		&nbsp;
				             	</td>
			             	<tr>
			             	<tr>
				            	<td colspan="6" class="">&nbsp;</td>
				            </tr>
	            <% 
	            		iOEquipment++;
	              	} 
	              	
					BmoOrderStaff bmoOrderStaff = new BmoOrderStaff();
	              	PmOrderStaff pmOrderStaff = new PmOrderStaff(sFParams);
	              	BmFilter bmFilterSt = new BmFilter();
	              	bmFilterSt.setValueFilter(bmoOrderStaff.getKind(), bmoOrderStaff.getOrderId().getName(),bmoOrder.getId());
	              	Iterator<BmObject> itemsStaff = pmOrderStaff.list(bmFilterSt).iterator();
	              	int iOStaff = 1;
	              	while(itemsStaff.hasNext()) {
	              		bmoOrderStaff = (BmoOrderStaff)itemsStaff.next();
	              		if (iOStaff == 1) {
                 	%>
	                		<tr>
					            <td class="reportHeaderCell" colspan="5">
					            	Personal
					            </td>
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
					            	%>                    			
						            	<%= formatCurrency.format(totalStaff) %>&nbsp;
					            	<% }                    		
						            pmConn.close(); 
					            %>
					            </td> 
				            </tr>
		            <%	} %>
		                <tr>                   
			                <td class="reportCellEven" align ="left" >
				                <%= bmoOrderStaff.getName().toHtml() %>
				                <%= bmoOrderStaff.getDescription().toHtml() %>
			                </td>
			                <td class="reportCellEven" align="center">  
				                <% if (bmoOrder.getShowStaffQuantity().toBoolean()) { %> 
				                	<%= bmoOrderStaff.getQuantity().toInteger() %>
				                <% } %>  
			                </td>
			                <td class="reportCellEven" align="center">  
				                <% if (bmoOrder.getShowStaffQuantity().toBoolean()) { %> 
				                	<%= bmoOrderStaff.getDays().toDouble()%>
				                <% } %> 
			                </td>
			                <td class="reportCellEven" align="right">
				                <% if (bmoOrder.getShowStaffPrice().toBoolean()) { %> 
				                	<%= formatCurrency.format(bmoOrderStaff.getPrice().toDouble()) %>
				                <% } %>
			                </td>
			                <td class="reportCellEven" align="right">
				                <% if (bmoOrder.getShowStaffPrice().toBoolean()) { %>
				                	<%= formatCurrency.format(bmoOrderStaff.getAmount().toDouble()) %>
				                <% } %>   
			                </td>
			                <td class="reportCellEven" align ="left">
			             		&nbsp;
			             	</td>
		                <tr>
           	            <tr>
			            	<td colspan="6" class="">&nbsp;</td>
			            </tr>
	            <% 	
	            		iOStaff++;
              		} %>

	            <tr>
		            <td colspan="4" rowspan="4"  valign="top" align="left" class="detailStart">
		            	<p class="documentComments">
			            	<b>Notas:</b> <br> 
<%-- 			            	Personas: <%= bmoProject.getGuests().toString() %> <br> --%>
			            	<%= bmoOrder.getDescription().toHtml() %>
			            	<br>
			            	<br>
			            	<b>Comentarios:</b> <br> 
			            	<%= bmoOrder.getComments().toHtml() %>
		            	</p>
		            </td>                                  
		            <th class="" align="right">
		            	Subtotal:
	            	</th>
		            <td class="reportCellEven" align="right">
		            	<b><%=  formatCurrency.format(bmoOrder.getAmount().toDouble()) %></b>
		            </td>
	            </tr>    
	            <tr>                                                                       
		            <th class="" align="right">
			            <% if (bmoOrder.getDiscount().toDouble() > 0) { %>
			            	Descuento:
		            	<% } %>
	            	</th>
	            	<% if (bmoOrder.getDiscount().toDouble() > 0) { %>
		            	<td class="reportCellEven" align="right">
		            		<b><%=  formatCurrency.format(bmoOrder.getDiscount().toDouble()) %></b>
		            	</td>
	            	<% } else { %>
		            	<td class="" align="right"></td>
	            	<% } %>
            	</tr>  
            	<tr>                                                                        
	            	<th class="" align="right">
	            		<% if (bmoOrder.getTax().toDouble() > 0) { %>
	            			IVA:
	            		<% } %>
            		</th>
            		<% if (bmoOrder.getTax().toDouble() > 0) { %>
	            		<td class="reportCellEven" align="right">
							<b><%=  formatCurrency.format(bmoOrder.getTax().toDouble()) %></b>
	    				</td>
	        		<% } else { %>
		            	<td class="" align="right"></td>
	            	<% } %>
        		</tr>  
        		<tr>                                                                        
            		<th class="" align="right">
            			Total:
        			</th>
            		<td class="reportCellEven" align="right">
            			<b><%=  formatCurrency.format(bmoOrder.getTotal().toDouble()) %></b>
            		</td>
        		</tr>
            </table>
		<%
	    }
	} //Fin Si tiene permiso para ver
		} catch (Exception e) { 
				String errorLabel = "Error de Pedido";
				String errorText = "El Pedido no puede ser desplegado.";
				String errorException = e.toString();
				
				//response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);
		
		%>
		
			<%= errorException %>
		
		<%
			}

		
		%>

</body>
</html>