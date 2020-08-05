<%@page import="com.flexwm.shared.op.BmoOrderType"%>
<%@page import="com.flexwm.shared.op.BmoProduct"%>
<%@page import="com.flexwm.shared.op.BmoOrderItem"%>
<%@page import="com.flexwm.server.op.PmOrder"%>
<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@page import="com.flexwm.server.op.PmWarehouse"%>
<%@page import="com.flexwm.shared.op.BmoWarehouse"%>
<%@page import="com.flexwm.server.cm.PmProject"%>
<%@page import="com.flexwm.shared.cm.BmoProject"%>
<%@page import="com.flexwm.server.op.PmOrderDelivery"%>
<%@page import="com.flexwm.shared.op.BmoOrderDelivery"%>
<%@include file="../inc/login_opt.jsp" %>

<html>
<%try{ 
	String title = ""; 
	

	
	BmoOrderDelivery bmoOrderDelivery = new BmoOrderDelivery();
	PmOrderDelivery pmOderDelivery = new PmOrderDelivery(sFParams);
	
	int orderDeliveryId = 0;
	double odyi_quantity = 0;
	String logoURL ="";
	
	orderDeliveryId = Integer.parseInt(request.getParameter("foreignId"));
	
	bmoOrderDelivery = (BmoOrderDelivery)pmOderDelivery.get(orderDeliveryId);
	
	if (bmoOrderDelivery.getStatus().toChar() != BmoOrder.STATUS_AUTHORIZED) throw new Exception("El envio no esta Autorizado - no se puede desplegar.");
		BmoProgram bmoProgram = new BmoProgram();
		PmProgram pmProgram  = new PmProgram(sFParams);
		bmoProgram = (BmoProgram)sFParams.getBmoProgram(bmoOrderDelivery.getProgramCode());	
	
		BmoProject bmoProject = new BmoProject();
		PmProject pmProject = new PmProject(sFParams);
	
		BmoWarehouse bmoWarehouse = new BmoWarehouse();
		PmWarehouse pmWarehouse = new PmWarehouse(sFParams);
	
		BmoCompany bmoCompany = new BmoCompany();
		PmCompany pmCompany = new PmCompany(sFParams);
	
		BmoUser bmoUserTL = new BmoUser();
		PmUser pmUser = new PmUser(sFParams);
	
		BmoOrder bmoOrder = new BmoOrder();
    	PmOrder pmOrder = new PmOrder(sFParams);
    
    	BmoOrderItem bmoOrderItem = new BmoOrderItem();
    	
    	bmoOrder = (BmoOrder)pmOrder.get(bmoOrderDelivery.getOrderId().toInteger());
    	
    	if(bmoOrder.getBmoOrderType().getType().toChar() == BmoOrderType.TYPE_SALE){
    		if(bmoOrderDelivery.getType().toChar() == BmoOrderDelivery.TYPE_DELIVERY){
    			title = "Formato de Salida de préstamo";
    		}else if(bmoOrderDelivery.getType().toChar() == BmoOrderDelivery.TYPE_RETURN){
    			title = "Formato de Entrada de préstamo";
    		}
    			
    	}else{
    
   			if(bmoOrderDelivery.getType().toChar() == BmoOrderDelivery.TYPE_DELIVERY){
   				title = "Formato de Salida de pedido";
   			}else if(bmoOrderDelivery.getType().toChar() == BmoOrderDelivery.TYPE_RETURN){
   				title = "Formato de Entrada de pedido";
   			}
    	}
    	PmConn pmConn = new PmConn(sFParams);
%>


	
	
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
<meta charset="UTF-8">
<title>:::<%= title %>:::</title>
	 <link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>">
</head>
<body class="default" <%= permissionPrint %>>
<%
	
	

	
if(!(bmoOrder.getBmoOrderType().getType().toChar() == BmoOrderType.TYPE_SALE)){
	
	if(bmoOrder.getOriginRenewOrderId().toInteger() > 0){
		bmoProject = (BmoProject)pmProject.getBy(bmoOrder.getOriginRenewOrderId().toInteger(), bmoProject.getOrderId().getName());
	}else{
		 bmoProject = (BmoProject)pmProject.getBy(bmoOrder.getId(), bmoProject.getOrderId().getName());
	}
	bmoCompany = (BmoCompany)pmCompany.get(bmoOrderDelivery.getCompanyId().toInteger());
	if((bmoProject.getWarehouseManagerId().toInteger()) > 0)	{
		bmoUserTL = (BmoUser)pmUser.get(bmoProject.getWarehouseManagerId().toInteger());
	}
}
	
	
	
	if (!bmoCompany.getLogo().toString().equals(""))
		logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
	else 
		logoURL = sFParams.getMainImageUrl();

	String sql = "SELECT * FROM orderdeliveryitems " +
	             "LEFT JOIN orderdeliveries ON (odyi_orderdeliveryid = odly_orderdeliveryid) " +
			     "LEFT JOIN orders ON (orde_orderid = odly_orderid) " +
			     "LEFT JOIN products ON (odyi_productid = prod_productid) " +
			     "LEFT JOIN orderitems ON (ordi_orderitemid = odyi_orderitemid) " +
			     "LEFT JOIN whsections ON (odyi_fromwhsectionid = whse_whsectionid) WHERE " ;
	if(bmoOrderDelivery.getType().toChar() == BmoOrderDelivery.TYPE_DELIVERY){
				sql += "odyi_quantity > 0 AND ";
	}
// 			     
	if(!(bmoOrder.getOriginRenewOrderId().toInteger() > 0)){
	      		 sql+= "orde_originreneworderid IS NULL AND " ;
	}
	if(bmoOrderDelivery.getType().toChar() == BmoOrderDelivery.TYPE_RETURN){
				 sql += " odly_type =  '"+BmoOrderDelivery.TYPE_RETURN+"' AND " ;
	}else if (bmoOrderDelivery.getType().toChar() == BmoOrderDelivery.TYPE_DELIVERY){
		         sql += " odly_type =  '"+BmoOrderDelivery.TYPE_DELIVERY+"' AND " ;
	}
	
		   sql+= " orde_orderid = "+bmoOrder.getId()+" ORDER BY odyi_orderdeliveryitemid ";

		   
// 			String sql = "SELECT * FROM orderdeliveryitems " +
// 		             "LEFT JOIN orderdeliveries ON (odyi_orderdeliveryid = odly_orderdeliveryid) " +
// 				     "LEFT JOIN orders ON (orde_orderid = odly_orderid) " +
// 				     "LEFT JOIN products ON (odyi_productid = prod_productid) " +
// 				     "LEFT JOIN warehouses ON (odyi_fromwhsectionid = ware_warehouseid) WHERE " +
// 				     "odyi_quantity > 0 ";
// 		if(!(bmoOrder.getOriginRenewOrderId().toInteger() > 0)){
// 		      		 sql+= " AND orde_originreneworderid IS NULL AND" ;
// 		}
// 			   sql+= " orde_orderid = "+bmoOrder.getId()+" ORDER BY odyi_orderdeliveryitemid ";

	
	%>
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		<tr>
			<td align="left" width="6%" rowspan="6" valign="top">
				<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
			</td>
			<%if(bmoOrder.getBmoOrderType().getType().toChar() == BmoOrderType.TYPE_SALE){%>
			<td colspan="2" class="reportTitleCell">
			<%}else{ %>
			<td colspan="4" class="reportTitleCell">
			<%}	 %>
					<%=title %> 
			</td>
		</tr>
		<tr>       
		
			<th align = "left" class="reportCellEven">
			<%if(bmoOrder.getBmoOrderType().getType().toChar() == BmoOrderType.TYPE_SALE){%>
			Nombre de cliente:	
			<%} else{%>
				Nombre y PR del Evento: 
			<%} %>
			</th>
			<td align = "left" class="reportCellEven">
			<%if(bmoOrder.getBmoOrderType().getType().toChar() == BmoOrderType.TYPE_SALE){%>
				<%=bmoOrder.getBmoCustomer().getDisplayName() %>	 	
			<%} else{%>
						<%= bmoProject.getCode() + " " + bmoProject.getName()%>
			<%} %>
			</td>
			<%if(!(bmoOrder.getBmoOrderType().getType().toChar() == BmoOrderType.TYPE_SALE)){%>
			<th align = "left" class="reportCellEven">
				<%=bmoProject.getVenueId().getLabel()+":" %>
			</th>
			<td align = "left" class="reportCellEven">
				<%=bmoProject.getBmoVenue().getName()%>				
			</td>
			<%} %>
		</tr>
		<tr>
		<%if(!(bmoOrder.getBmoOrderType().getType().toChar() == BmoOrderType.TYPE_SALE)){%>
			<th align = "left" class="reportCellEven">
				Dirección del Evento: 
			</th>
			<td align = "left" class="reportCellEven">
				<%if(bmoProject.getVenueId().toInteger() > 0){ %>
						<%= bmoProject.getBmoVenue().getBmoCity().getName()+", "+bmoProject.getBmoVenue().getNeighborhood()
						+", " +bmoProject.getBmoVenue().getStreet() + "No. " + bmoProject.getBmoVenue().getNumber()%>
				<%} %>
			</td>
			<th align = "left" class="reportCellEven">
				<%=bmoProject.getWarehouseManagerId().getLabel() %>:
			</th>
			<td align = "left" class="reportCellEven">
				<%=bmoUserTL.getFirstname()+" "+bmoUserTL.getFatherlastname()+" "+bmoUserTL.getMotherlastname()%>				
			</td>	
		<%} else{%>	
			<th align = "left" class="reportCellEven">
				Nombre de quien Recibe: 
			</th>	
			<td align = "left" class="reportCellEven">
				<%=bmoUserTL.getFirstname()+" "+bmoUserTL.getFatherlastname()+" "+bmoUserTL.getMotherlastname()%>				
			</td>
		<%} %>
		</tr>
		<tr>
		<%if(!(bmoOrder.getBmoOrderType().getType().toChar() == BmoOrderType.TYPE_SALE)){%>
			<th align = "left" class="reportCellEven">
				<%=bmoProject.getUserId().getLabel()+":"  %>
			</th>
			<td align = "left" class="reportCellEven">
				<%=bmoProject.getBmoUser().getFirstname()+" "+bmoProject.getBmoUser().getFatherlastname()+" "+bmoProject.getBmoUser().getMotherlastname() %>
			</td>
			<td align = "left" class="reportCellEven" colspan= "2">
			</td>
		<%}else{ %>
			<th align = "left" class="reportCellEven">
				Aprobado por:
			</th>
			<td align = "left" class="reportCellEven">
				<%=bmoProject.getBmoUser().getFirstname()+" "+bmoProject.getBmoUser().getFatherlastname()+" "+bmoProject.getBmoUser().getMotherlastname() %>
			</td>
		<%} %>
		</tr>
		<tr>
			<td >
				    &nbsp;
			</td>
		</tr>
		
	</table>
	<br>
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		
		<tr>
		    <td colspan="7"   valign="top"  class="detailStart" >
		          <p class="documentComments">
			           <b>Notas:</b> <br> <br>
			           <%=bmoOrderDelivery.getNotes().toHtml() %>
			            	
		           </p>
		    </td>                               
		</tr>  
		<tr>
			<td >
				    &nbsp;
			</td>
		</tr>
		<tr>
			<td colspan="7" class="reportHeaderCellCenter">
				<%=bmoOrderDelivery.getOrderId().getLabel()%>
			</td>
		</tr>		 	
		<tr>
			<%if(bmoOrderDelivery.getType().toChar() == BmoOrderDelivery.TYPE_RETURN){%>
			<td class="reportHeaderCell"  >
				Cantidad que salió
			</td>
			<%} %>
			<%if(bmoOrderDelivery.getType().toChar() == BmoOrderDelivery.TYPE_RETURN){%>
			<td class="reportHeaderCell"  >
				Cantidad que entro
			</td>
			<%}else{ %>
			<td  class="reportHeaderCell"  >
				<%=bmoOrderItem.getQuantity().getLabel()%>
			</td>
			<%} %>
			<td class="reportHeaderCell"  >
				#Lote/Serie
			</td>			
			<td class="reportHeaderCell"  >
				Origen
			</td>
			<td class="reportHeaderCell"  >
				Días
			</td>
			<td class="reportHeaderCell"  >
				Clave Producto
			</td>
			<td class="reportHeaderCell"  >
				Nom. Producto
			</td>
			<td class="reportHeaderCell"  >
				Tipo Prod.
			</td>
			
		</tr>
		<%
		
		
		pmConn.open();
		pmConn.doFetch(sql);
		while(pmConn.next()){
			//Cantodad que salio
		double quantity = pmConn.getDouble("ordi_quantity");
		quantity = quantity - (pmConn.getDouble("odyi_quantitybalance"));
		
		if(bmoOrderDelivery.getType().toChar() == BmoOrderDelivery.TYPE_DELIVERY){
			odyi_quantity = 1;
		}else{
			odyi_quantity = pmConn.getDouble("odyi_quantity");
		}
			if(odyi_quantity > 0){
		%>
			<tr>
				<%if(bmoOrderDelivery.getType().toChar() == BmoOrderDelivery.TYPE_RETURN){%>
				<td class="reportCellEven">
					<%=quantity %>
				</td>
				<%} %>
				<td class="reportCellEven">
					<%=pmConn.getString("odyi_quantity") %>
				</td>
				<td class="reportCellEven" >
					<%=pmConn.getString("odyi_serial") %>
				</td>
				<td class="reportCellEven" >
					<%=pmConn.getString("whse_name") %>
				</td>
				<td class="reportCellEven" >
					<%=pmConn.getString("odyi_days") %>
				</td>
				<td class="reportCellEven" >
					<%=pmConn.getString("prod_code") %>
				</td>			
				<td class="reportCellEven" >
					<%=pmConn.getString("prod_name")%>
				</td>			
				<td class="reportCellEven" >
				<%String type = "";
				if (pmConn.getString("prod_type").equals(""+BmoProduct.TYPE_PRODUCT)){
					type = "Producto";
				}
				if (pmConn.getString("prod_type").equals(""+BmoProduct.TYPE_COMPOSED)){
					type = "Compuesto";
				}
				if (pmConn.getString("prod_type").equals(""+BmoProduct.TYPE_SUBPRODUCT)){
					type = "Sub-Producto";
				}
				if (pmConn.getString("prod_type").equals(""+BmoProduct.TYPE_SERVICE)){
					type = "Servicio Interno";
				}
				if (pmConn.getString("prod_type").equals(""+BmoProduct.TYPE_EXTERNAL)){
					type = "Servicio Externo";
				}
				if (pmConn.getString("prod_type").equals(""+BmoProduct.TYPE_CLASS)){
					type = "Clase";
				}
				if (pmConn.getString("prod_type").equals(""+BmoProduct.TYPE_COMPLEMENTARY)){
					type = "Complementario/Auxiliar";
				}
				%>
					<%=type%>
				</td>			
				
			</tr>
		<%}
		} 
		
		
		%>
		
	<%if(bmoOrderDelivery.getType().toChar() == BmoOrderDelivery.TYPE_RETURN){
		pmConn.doFetch(sql);
		%>	 		
	</table>
	<br>
	<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0" style="font-size: 12px">
		<tr>
			<td colspan="4" class="reportHeaderCellCenter">
					Perdidas 
			</td>
		</tr>
		<tr>
			<td class="reportHeaderCell"  >
				Cantidad
			</td>	
			<td class="reportHeaderCell"  >				
				Nom. Producto			
			</td>		
			<td class="reportHeaderCell"  >
				#Lote/Serie
			</td>	
		</tr>
		<%while(pmConn.next()){ 
			//Cantodad que salio
			double quantity = pmConn.getDouble("ordi_quantity");
			quantity = quantity -(pmConn.getDouble("odyi_quantitybalance"));
			double returnedNo =  pmConn.getDouble("ordi_quantity") - pmConn.getDouble("odyi_quantitybalance") - pmConn.getDouble("odyi_quantityreturned");
			if((returnedNo > 0)){
			%>
			<tr>
				<td class="reportCellEven" >
					<%=returnedNo%>
				</td>
				<td class="reportCellEven" >
					<%=pmConn.getString("prod_code")+" "+ pmConn.getString("prod_name") %>
				</td>
				<td class="reportCellEven" >
					<%=pmConn.getString("odyi_serial") %>
				</td>
			</tr>
			
		<%}
		} %>
	</table>
	<%} %>
	<%if(bmoOrder.getBmoOrderType().getType().toChar() == BmoOrderType.TYPE_SALE){%>
	
	<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0" style="font-size: 12px">
		<tr>
			<td  class="documentComments" align="center">
				&nbsp;
			</td>			
		</tr>
		<tr>
			
			<td  class="documentComments" align="center">
			 Por medio de la presente consto que recibo el equipo en perfectas condiciones,<br> en caso de pérdida, daño parcial o total del material me haré cargo de la reposición o pago de este.
			</td>
		</tr>
	</table>
	<%} %>
	<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0" style="font-size: 12px">
		
		<tr>
			<td colspan="5" height="40px">
					&nbsp;
			</td>
		</tr> 		
		<tr>
			<td align="center" colspan="2">
					 ________________________________
			</td>
			<td align="center" colspan="3">
					 ________________________________
			</td>
		</tr>
		<tr>
			<th align="center" colspan="2" class="documentTitleCaption">
				 Entrega<br>
				 <%if(bmoOrderDelivery.getType().toChar() == BmoOrderDelivery.TYPE_RETURN){%>
				 		<%=bmoUserTL.getFirstname()+" "+bmoUserTL.getFatherlastname()+" "+bmoUserTL.getMotherlastname()%>
				 <%} else{%>
				 		<%=bmoOrderDelivery.getUserCreate().toString() %>
				 
				<% }%>
				
			</th>
			<th align="center" colspan="3" class="documentTitleCaption">
			     Recibe<br>
			      <%if(bmoOrderDelivery.getType().toChar() == BmoOrderDelivery.TYPE_DELIVERY){%>
						<%=bmoUserTL.getFirstname()+" "+bmoUserTL.getFatherlastname()+" "+bmoUserTL.getMotherlastname()%>	
				  <%}else{%>
			 			<%=bmoOrderDelivery.getUserCreate().toString() %>					 
			      <%}%>
			
			</th>
		</tr>  		
	</table>
	
	
<%pmConn.close();
	
}catch(Exception e){
	String errorLabel = "Error de Formato";
	String errorText = "El Formato no pudo ser desplegado exitosamente.";
	String errorException = e.toString();
	response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);
} %>
</body>
</html>