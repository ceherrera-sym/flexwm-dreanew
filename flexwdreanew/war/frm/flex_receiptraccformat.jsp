<%@page import="com.flexwm.shared.fi.BmoBankMovType"%>
<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@page import="com.flexwm.server.op.PmOrder"%>
<%@page import="com.flexwm.shared.op.BmoOrderType"%>
<%@page import="com.flexwm.server.op.PmOrderType"%>
<%@page import="com.flexwm.shared.co.BmoOrderProperty"%>
<%@page import="com.flexwm.server.co.PmOrderProperty"%>
<%@page import="com.flexwm.server.fi.PmRaccount"%>
<%@page import="com.flexwm.shared.fi.BmoRaccountAssignment"%>
<%@page import="com.flexwm.server.fi.PmRaccountAssignment"%>
<%@page import="com.flexwm.shared.fi.BmoRaccount"%>
<%@page import="com.flexwm.shared.fi.BmoRaccountType"%>
<%@page import="com.symgae.shared.BmoFlex"%>
<%@page import="com.flexwm.server.PmFlexConfig"%>
<%@page import="com.flexwm.shared.BmoFlexConfig"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.server.cm.*"%>
<%@page import="com.flexwm.shared.cm.*"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="java.sql.Types"%>
<%@page import="com.symgae.server.PmConn"%>
<%@include file="../inc/login_opt.jsp" %>
<%
	String title = "";
	
	BmoRaccount bmoRaccount = new BmoRaccount();
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)sFParams.getBmoProgram(bmoRaccount.getProgramCode());
%>

<html>
	<%
	try {
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
			
		//No cargar datos en caso de que se imprima/exporte y no tenga permisos
		if(sFParams.hasPrint(bmoProgram.getCode().toString()) || !(exportExcel.equals("1"))) {
		%>
			<head>
			    <title>:::Recibo CxC.:::</title>
			    <link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>"> 
			     <link rel="stylesheet" type="text/css" href="../css/flexwm.css"> 
			</head>
			<body class="default" <%= permissionPrint %>>
			<%
			
			NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(Locale.US);
			
			String sql = "";
		
			int raccountId = 0, projectId = 0;
			String projectName = "", projectDate = "", projectCity = "", clientName = "";
			if (isExternal) raccountId = decryptId;
			else raccountId = Integer.parseInt(request.getParameter("foreignId"));
			
		    
		    PmRaccount pmRaccount = new PmRaccount(sFParams);
		    bmoRaccount = (BmoRaccount)pmRaccount.get(raccountId);
		    
		    BmoOrder bmoOrder = new BmoOrder();
		    PmOrder pmOrder = new PmOrder(sFParams);
		    if(bmoRaccount.getOrderId().toInteger() > 0)
		    	bmoOrder = (BmoOrder)pmOrder.get(bmoRaccount.getOrderId().toInteger());
		    
		    BmoOrderType bmoOrderType = new BmoOrderType();
		    PmOrderType pmOrderType = new PmOrderType(sFParams);
		    if(bmoRaccount.getOrderId().toInteger() > 0)
		    	bmoOrderType = (BmoOrderType)pmOrderType.get(bmoOrder.getOrderTypeId().toInteger());
		    
		    
		  //Pedido-Inmueble	
		    BmoOrderProperty bmoOrderProperty = new BmoOrderProperty();
		    if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_PROPERTY)){
				PmOrderProperty pmOrderProperty = new PmOrderProperty(sFParams);
				bmoOrderProperty = (BmoOrderProperty)pmOrderProperty.getBy(bmoOrder.getId(), bmoOrderProperty.getOrderId().getName());
		    }
		       
		    clientName = bmoRaccount.getBmoCustomer().getFatherlastname().toString() + " " +
			             bmoRaccount.getBmoCustomer().getMotherlastname().toString() + " " +
			             bmoRaccount.getBmoCustomer().getFirstname().toString();
		    
		    BmoCompany bmoCompany = new BmoCompany();
			PmCompany pmCompany = new PmCompany(sFParams);
			bmoCompany = (BmoCompany)pmCompany.get(bmoRaccount.getCompanyId().toInteger());
			
			// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
			String logoURL ="";
			if (!bmoCompany.getLogo().toString().equals(""))
				logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
			else 
				logoURL = sFParams.getMainImageUrl();
		    
		 %>   
		    
			 <table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
				 <tr>
				       <td align="left" width="" rowspan="5" valign="top">
							<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
				       </td>
				       <td colspan="4" class="reportTitleCell">
				       	Recibo de CxC: <%= bmoRaccount.getCode().toString() %>
				       </td>
				 </tr>     
				 <tr>
				       <th align = "left" class="reportCellEven">Empresa:</th>
				       <td class="reportCellEven">
				            &nbsp;<%= bmoRaccount.getBmoCompany().getName().toString() %>                 
				       </td>
				       <th align = "left" class="reportCellEven">Tipo de CxC:</th>
				       <td class="reportCellEven">
				       		&nbsp;<%= bmoRaccount.getBmoRaccountType().getName().toString() %>                 
				       </td>
				 </tr>
				 <tr>
				     <th align = "left" class="reportCellEven">Cliente:</th>
				     <td class="reportCellEven">
				     	&nbsp;<%= bmoRaccount.getBmoCustomer().getCode().toString()%> <%= clientName %> 	         
				     </td>
				     <% if(bmoOrder.getId() > 0){ %>
					     <th align = "left" class="reportCellEven">Pedido:</th>
					     <td class="reportCellEven">
					     	&nbsp;
					     	<% if(bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_PROPERTY)){ %>
					     		<%= bmoOrder.getCode().toString() %>
					     	<% }else{%>
					     		<%= bmoOrder.getCode().toString() %> - <%= bmoOrder.getName().toString() %>
					     	<% }%>
					     </td>
				     <% }else{%>
					     <th align = "left" class="reportCellEven">&nbsp;</th>
					     <td class="reportCellEven">&nbsp;</td>
		     		 <% }%>
				 </tr>
				 <% if(bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_PROPERTY)){ %>
					 <tr>
					     <th align = "left" class="reportCellEven">Inmueble:</th>
					     <td class="reportCellEven">
					     	&nbsp;<%= bmoOrderProperty.getBmoProperty().getCode().toString()%>          
					     </td>
					     <th align = "left" class="reportCellEven">Calle y N&uacute;mero Oficial:</th>
					     <td class="reportCellEven">
					     	&nbsp;<%= bmoOrderProperty.getBmoProperty().getStreet().toString() %> # <%= bmoOrderProperty.getBmoProperty().getNumber().toString() %>
					     </td>
					 </tr>
				 <% } %>
			  <tr>		
			  		<%
			  			PmUser pmUser = new PmUser(sFParams);
			  			BmoUser bmoUserAutorized = new BmoUser();
			    		if (bmoRaccount.getAuthorizedUser().toInteger() > 0)	    			
			    			bmoUserAutorized = (BmoUser)pmUser.get(bmoRaccount.getAuthorizedUser().toInteger());
			    	%>
			    	<th align = "left" class="reportCellEven">Autorizada por:</th>
			        <td class="reportCellEven">
			            <%= bmoUserAutorized.getFirstname().toString() + " " + bmoUserAutorized.getFatherlastname().toString() %>                
			        </td>
			        <th align="left" class="reportCellEven">Fecha Autorizaci&oacute;n:</th>
					<td class="reportCellEven"><%=bmoRaccount.getAuthorizedDate().toString()%>
					</td>
			  </tr>
			  <tr>
				 	<th align = "left" class="reportCellEven">Estatus:</th>
					<td class="reportCellEven">
					 	&nbsp;<%= bmoRaccount.getStatus().getSelectedOption().getLabel() %>         
					</td>
				 	<th align = "left" class="reportCellEven">Pago:</th>
					 <td class="reportCellEven">
					 	&nbsp;<%= bmoRaccount.getPaymentStatus().getSelectedOption().getLabel() %>       
					 </td>
				 </tr>
			 </table>
			 <br>
			 <table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
				 <tr>
					 <td colspan="6" class="reportHeaderCell">
					 	Detalles y Fechas:
					 </td>
				 </tr>
				 <tr>
					 <th align = "left" class="reportCellEven">Factura:</th>
					 <td class="reportCellEven">
					 	&nbsp;<%= bmoRaccount.getInvoiceno().toString() %>         
					 </td>
					 <th align = "left" class="reportCellEven">Folio:</th>
					 <td class="reportCellEven">
					 	&nbsp;<%= bmoRaccount.getFolio().toString() %>   
					 	<%
					 		if (bmoRaccount.getBmoRaccountType().getType().equals(BmoRaccountType.TYPE_DEPOSIT)) {
						 		//Obtener la Cta Banco
						 		PmConn pmConn = new PmConn(sFParams);
						 		pmConn.open();
						 		String bkacName = "";
						 		sql = " SELECT * FROM bankmovconcepts " +
						 		      " LEFT JOIN bankmovements ON (bkmc_bankmovementid = bkmv_bankmovementid) " +
						 			  " LEFT JOIN bankaccounts ON (bkmv_bankaccountid = bkac_bankaccountid) " +
						 			  " LEFT JOIN bankmovtypes ON (bkmv_bankmovtypeid = bkmt_bankmovtypeid) " +	
						 		      " WHERE bkmc_foreignid = " + raccountId + 
						 		      " AND bkmt_type = '" + BmoBankMovType.TYPE_DEPOSIT + "'";
						 		pmConn.doFetch(sql);
						 		if (pmConn.next()) bkacName = pmConn.getString("bankaccounts", "bkac_name");
						 		pmConn.close();
						%>
							Cta.Banco: <%= bkacName %>
						<%  		
							}	      
					 	%>    
					 </td>
				 </tr> 
				 <tr>
					 <th align = "left" class="reportCellEven">Descripci&oacute;n:</th>
					 <td class="reportCellEven" colspan="">
					 	&nbsp;<%= bmoRaccount.getDescription().toString() %>         
					 </td>
					 <th align = "left" class="reportCellEven">Fecha de Pago:</th>
					 <td class="reportCellEven" colspan="">
					 	&nbsp;
					 	<%
					 	// Traer fecha de pago de ABONO
					 	BmoRaccountAssignment bmoRaccountAssignment = new BmoRaccountAssignment();
						PmRaccountAssignment pmRaccountAssignment = new PmRaccountAssignment(sFParams);
					 	
						ArrayList<BmFilter> filterListRass = new ArrayList<BmFilter>();
						BmFilter filterRaccountId = new BmFilter();
						filterRaccountId.setValueFilter(bmoRaccountAssignment.getKind(), bmoRaccountAssignment.getForeignRaccountId().getName(), bmoRaccount.getId());
						filterListRass.add(filterRaccountId);						
						Iterator<BmObject> rass = new PmRaccountAssignment(sFParams).list(filterListRass).iterator();
						int raccountD = 0;
						while (rass.hasNext()) {
							bmoRaccountAssignment = (BmoRaccountAssignment)rass.next();
							raccountD = bmoRaccountAssignment.getRaccountId().toInteger();
						}
						
						BmoRaccount bmoRaccountD = new BmoRaccount();
						PmRaccount pmRaccountD = new PmRaccount(sFParams);
	
						if(raccountD > 0){
								bmoRaccountD = (BmoRaccount)pmRaccountD.get(raccountD);
						%>
					 			<%= bmoRaccountD.getDueDate().toString() %>   
					 	<% }else{ %>
					 			<%= bmoRaccount.getDueDate().toString() %> 
					 	<% }%>
					 </td>
				 </tr>
				 <tr>
					 <td colspan="4" class="">
					 	&nbsp;          
					 </td>
				 </tr>
				 <tr>
					 <td colspan="4" class="reportHeaderCell">
					 	Montos:                
					 </td>
				 </tr>
				 <tr>
					 <td class="reportCellEven" colspan="2">&nbsp;</td>
					 <th align = "left" class="reportCellEven">Monto:</th>
					 <td class="reportCellEven">
					 	&nbsp;<%= formatCurrency.format(bmoRaccount.getTotal().toDouble()) %>        
					 </td>
				 </tr>
				 <tr>
					 <td class="reportCellEven" colspan="2">&nbsp;</td>
					 <th align = "left" class="reportCellEven">Pago:</th>
					 <td class="reportCellEven">
					 	&nbsp;<%= formatCurrency.format(bmoRaccount.getPayments().toDouble()) %>        
					 </td>
				 </tr>
				 <tr>
					 <th align = "left" class="reportCellEven">&nbsp;</th>
					 <td class="reportCellEven">
					 	&nbsp;
					 </td>
					 <th align = "left" class="reportCellEven">Saldo:</th>
					 <td class="reportCellEven">
					 	&nbsp;<%= formatCurrency.format(bmoRaccount.getBalance().toDouble()) %>        
					 </td>
				 </tr> 
	
			</table>    
			<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
				<tr align="center">
					<td align="center"><br><br>
						___________________
					</td>
				</tr>
				<tr>
					<td align="center" class="documentTitleCaption">
						<%
						BmoUser bmoUser = new BmoUser();						
						if (sFParams.getLoginInfo().getUserId() > 0)
							bmoUser = (BmoUser)pmUser.get(sFParams.getLoginInfo().getUserId()); 
				
						%>
						Recibida por<br>
						<%= bmoUser.getFirstname().toString() %>
						<%= bmoUser.getFatherlastname().toString() %>
						<%= bmoUser.getMotherlastname().toString() %>  
					</td>
				</tr>
				<p class="documentTitleCaption" align="left"> 
					Fecha de Impresi&oacute;n <%= SFServerUtil.nowToString(sFParams, "dd/MM/yyyy HH:mm") %> Por:
					<%=  sFParams.getLoginInfo().getBmoUser().getFirstname() + " " + sFParams.getLoginInfo().getBmoUser().getFatherlastname().toString() %>
				</p>
			</table>
			
<%			
       	}
	} catch (Exception e) {
    String errorLabel = "Error de Recibo CxC";
    String errorText = "El recibo  de cxc no pudo ser desplegado exitosamente.";
    String errorException = e.toString();
    
    response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);
    }
%>
</body>
</html>
