<!--  
/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */ -->
<%@page import="com.symgae.shared.GwtUtil"%>
<%@page import="com.flexwm.shared.BmoFlexConfig"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.shared.op.*"%>
<%@page import="com.flexwm.shared.cm.*"%>
<%@page import="com.flexwm.shared.fi.*"%>
<%@page import="com.flexwm.shared.wf.*"%>
<%@page import="com.symgae.client.ui.UiParams"%>

<%@page import="com.flexwm.server.cm.*"%>
<%@page import="com.flexwm.server.op.*"%>
<%@page import="com.flexwm.server.fi.*"%>
<%@page import="com.flexwm.server.wf.*"%>
<%@include file="../inc/login_opt.jsp" %>

<%
	try {
		
	String title = "";

	BmoRequisition bmoRequisition = new BmoRequisition();
	BmoRequisitionType bmoRequisitionType = new BmoRequisitionType();
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)sFParams.getBmoProgram(bmoRequisition.getProgramCode());
	
	String css = "/css/" +defaultCss;
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
	    <title>:::Orden de Compra:::</title>
	    <link rel="stylesheet" type="text/css" href="<%= GwtUtil.getProperUrl(sFParams, css) %> "> 
	</head>
	<body class="default" <%= permissionPrint %>>
	
	<%
		
			NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(Locale.US);
		    
		    // Obtener parametros
		    
		    int requisitionId = 0;
		    
		    if (isExternal) requisitionId = decryptId;
		    else if (request.getParameter("foreignId") != null) requisitionId = Integer.parseInt(request.getParameter("foreignId"));
		    
		    // Obtener la OC
		    PmRequisition pmRequisition = new PmRequisition(sFParams);
		    bmoRequisition = (BmoRequisition)pmRequisition.get(requisitionId);

		    //Validad que si el check esta activado
		    PmRequisitionType pmRequisitionType = new PmRequisitionType(sFParams);
		    bmoRequisitionType = (BmoRequisitionType)pmRequisitionType.get(bmoRequisition.getRequisitionTypeId().toInteger());

		    // NO validar cuando venga de una bitacora(pedido)
		    boolean log = false;
		    if (request.getParameter("log") != null) log = Boolean.parseBoolean(request.getParameter("log"));
			 if (!log)
				 if(bmoRequisition.getBmoRequisitionType().getViewFormat().toBoolean()){
					 
				 }else{
			    if(bmoRequisition.getStatus().toChar() != BmoRequisition.STATUS_AUTHORIZED){
			    	throw new Exception("La O.C. no está Autorizada - no se puede desplegar.");	
			    }
				 }
		    BmoOrder bmoOrder = new BmoOrder();
		    PmOrder pmOrder = new PmOrder(sFParams);
		    if (bmoRequisition.getOrderId().toInteger() > 0)
		    	bmoOrder = (BmoOrder)pmOrder.get(bmoRequisition.getOrderId().toInteger());
			    
			BmoArea bmoArea = new BmoArea();
		    PmArea pmArea = new PmArea(sFParams);
		    if (bmoRequisition.getAreaId().toInteger() > 0)
			    bmoArea = (BmoArea)pmArea.get(bmoRequisition.getAreaId().toInteger());    
		    
		    BmoUser bmoUserResponsibleArea = new BmoUser();
		    PmUser pmUserResponsibleArea = new PmUser(sFParams);
		    if (bmoArea.getUserId().toInteger() > 0)
		    	bmoUserResponsibleArea = (BmoUser)pmUserResponsibleArea.get(bmoArea.getUserId().toInteger()); 
	
			BmoWarehouse bmoWarehouse = new BmoWarehouse();
		    PmWarehouse pmWarehouse = new PmWarehouse(sFParams);
		    if (bmoRequisition.getWarehouseId().toInteger() > 0)
			    bmoWarehouse = (BmoWarehouse)pmWarehouse.get(bmoRequisition.getWarehouseId().toInteger());   
			    
			BmoUser bmoUser = new BmoUser();
		    PmUser pmUser = new PmUser(sFParams);
		    if (bmoRequisition.getRequestedBy().toInteger() > 0)
			    bmoUser = (BmoUser)pmUser.get(bmoRequisition.getRequestedBy().toInteger()); 
			
		  	PmCompany pmCompany = new PmCompany(sFParams);
      	  	BmoCompany bmoCompany = (BmoCompany)pmCompany.get(bmoRequisition.getCompanyId().toInteger());
  	  	
      	// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
    		String logoURL ="";
    		if (!bmoCompany.getLogo().toString().equals(""))
    			logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
    		else 
    			logoURL = sFParams.getMainImageUrl();
	
	%>
	
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	      <tr>
	            <td align="left" rowspan="10" valign="top">
					<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
	            </td>
	            <td colspan="4" class="reportTitleCell">
	                Orden de Compra: <%= bmoRequisition.getCode().toString() %>
	            </td>
	      </tr>     
	      <tr>
	            <th align = "left" class="reportCellEven">Nombre:</th>
	            <td class="reportCellEven">
	                 <%= bmoRequisition.getName().toString() %>                 
	            </td>
	            <th align = "left" class="reportCellEven">Tipo:</th>
	            <td class="reportCellEven">
	                 <%= bmoRequisition.getBmoRequisitionType().getName().toString() %>                 
	            </td>
	      </tr>
	      <tr>
	            <th align = "left" class="reportCellEven">Solicitado Por:</th>
	            <td class="reportCellEven">
	                <%= bmoUser.getFirstname().toString() %>
	                <%= bmoUser.getFatherlastname().toString() %>
	                <%= bmoUser.getMotherlastname().toString() %>  
	            </td>
	            <th align = "left" class="reportCellEven">Departamento:</th>
	            <td class="reportCellEven">
	                <%= bmoArea.getName().toString() %>                 
	            </td>
	      </tr>
	      <tr>
	            <th align = "left" class="reportCellEven">Proveedor:</th>
	            <td class="reportCellEven">
	                <b><%= bmoRequisition.getBmoSupplier().getCode().toString() %></b> - <%= bmoRequisition.getBmoSupplier().getName().toString() %>                 
	            </td>
	            <th align = "left" class="reportCellEven">Almac&eacute;n</th>
	            <td class="reportCellEven">
	                 <%= bmoWarehouse.getName().toString() %>                 
	            </td>
	      </tr>
	      <tr>
	            <th align = "left" class="reportCellEven">Fecha Pago:</th>
	            <td class="reportCellEven">
	                <%= bmoRequisition.getPaymentDate().toString() %>                
	            </td>
	            <th align = "left" class="reportCellEven">Fecha Entrega:</th>
	            <td class="reportCellEven">
	                 <%= bmoRequisition.getDeliveryDate().toString() %>                 
	            </td>
	      </tr>
	     
     		<%
     			boolean showOrder = false;
     			//Control presupuestal
     			if (((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
     				
     		%>
     			<tr>
	      			<th align = "left" class="reportCellEven">Presupuesto:</th>
		      		<td class="reportCellEven">
			          	<%		            
			          		PmBudgetItem pmBudgetItem = new PmBudgetItem(sFParams);	          		          	
			          		BmoBudgetItem bmoBudgetItem = (BmoBudgetItem)pmBudgetItem.get(bmoRequisition.getBudgetItemId().toInteger());
			          	%>
			          	<b><%= bmoBudgetItem.getBmoBudget().getName().toString() %></b> - <%= bmoBudgetItem.getBmoBudget().getName().toString() %>  - <%= bmoBudgetItem.getBmoBudgetItemType().getName().toString() %>
		     	 	</td>	     	 	
	     	 	<% if (bmoRequisition.getLoanId().toInteger() > 0) { %>	     	 	
	     	 			<th align = "left" class="reportCellEven">Financiamiento:</th>
			      		<td class="reportCellEven">
				          	<%		            
				          		PmLoan pmLoan = new PmLoan(sFParams);	          		          	
				          		BmoLoan bmoLoan = (BmoLoan)pmLoan.get(bmoRequisition.getLoanId().toInteger());
				          	%>
				          	<b><%= bmoLoan.getCode().toString() %></b> - <%= bmoLoan.getName().toString() %>
			     	 	</td>
			    <% } else if (bmoRequisition.getOrderId().toInteger() > 0) {
			    	showOrder = true;
			    %>
			    	<th align = "left" class="reportCellEven">Pedido:</th>
	            	<td class="reportCellEven" colspan="">
	                	<%= bmoOrder.getCode().toString() %> - <%= bmoOrder.getName().toString() %>                 
	            	</td>
			    <% } else { %>
			    	<td class="reportCellEven" colspan="3">
		            </td>
			    <% } %>
			    </tr>	     	 	
     		<% } else { %>
     			<% if (bmoRequisition.getOrderId().toInteger() > 0) {
     				showOrder = true;
     			%>
	     			<tr>
		     			<th align = "left" class="reportCellEven">Pedido:</th>
		            	<td class="reportCellEven" colspan="">
		                	<%= bmoOrder.getCode().toString() %> - <%= bmoOrder.getName().toString() %>                 
		            	</td>
		            	<td class="reportCellEven" colspan="3">
		            	</td>
	            	</tr>
	            <% } %>	
     		<% }%>
	     <% if (!showOrder) { 
	     		if (bmoRequisition.getOrderId().toInteger() > 0) { 
	     %>
	     	<tr>
		     			<th align = "left" class="reportCellEven">Pedido:</th>
		            	<td class="reportCellEven" colspan="">
		                	<%= bmoOrder.getCode().toString() %> - <%= bmoOrder.getName().toString() %>                 
		            	</td>
		            	<td class="reportCellEven" colspan="3">
		            	</td>
	            	</tr>
	     <%   }
	     	}	
	     %>
	      <tr>
	      		<%
	      			//Elaboro
	      			pmUser = new PmUser(sFParams);
	      			
	      			BmoUser bmoUserFix = new BmoUser();
	      			if (bmoRequisition.getUserCreateId().toInteger() > 0)
	      				bmoUserFix = (BmoUser)pmUser.get(bmoRequisition.getUserCreateId().toInteger());
	      			
	      		%>
	            <th align = "left" class="reportCellEven">Elaboro:</th>
	            <td class="reportCellEven">
	                <%= bmoUserFix.getFirstname().toString() + " " + bmoUserFix.getFatherlastname().toString() %>                
	            </td>
	            <th align = "left" class="reportCellEven">Fecha Elaboraci&oacute;n:</th>
		      	<td class="reportCellEven">	      	  		
	          		<%= bmoRequisition.getDateCreate().toString() %>                
		      	</td>
	      </tr>
	      <% if (bmoRequisition.getAuthorizedUser().toInteger() > 0) { %>	
		      <tr>
		      		<%
		      			//Elaboro
		      			pmUser = new PmUser(sFParams);
		      			
		      			bmoUserFix = new BmoUser();
		      			if (bmoRequisition.getUserCreateId().toInteger() > 0)
		      				bmoUserFix = (BmoUser)pmUser.get(bmoRequisition.getAuthorizedUser().toInteger());
		      			
		      		%>
		            <th align = "left" class="reportCellEven">Autorizo:</th>
		            <td class="reportCellEven">
		                <%= bmoUserFix.getFirstname().toString() + " " + bmoUserFix.getFatherlastname().toString() %>                
		            </td>
		            <th align = "left" class="reportCellEven">Fecha Autorizaci&oacute;n:</th>
			      	<td class="reportCellEven">	      	  		
		          		<%= bmoRequisition.getAuthorizedDate().toString() %>                
			      	</td>
		      </tr>
		  <% } %>    	
	      <tr>
	            <th align = "left" class="reportCellEven">Estatus:</th>
	            <td class="reportCellEven">
	                <%= bmoRequisition.getStatus().getSelectedOption().getLabel() %>                
	            </td>
	            <th align = "left" class="reportCellEven">Empresa:</th>
		      	<td class="reportCellEven">
	      	  		
			      	
	          		<%= bmoCompany.getName().toString() %>                
		      	</td>
	      </tr>                   
	</table>
	<br>
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	        <tr>
	            <td colspan="9" class="reportHeaderCell">
	                Detalle de Items                
	            </td>
	        </tr>
	        <TR class="">                    
	            <th class="reportCellEven" align="left">Cantidad</th>
	            <th class="reportCellEven" align="left">Nombre</th>
	            <th class="reportCellEven" align="left">Descripci&oacute;n</th>
	            <th class="reportCellEven" align="left">Modelo</th>   
	            <th class="reportCellEven" align="left">D&iacute;as</th>                               
	            <th class="reportCellEven" align="left">Tipo</th>
	            <th class="reportCellEven" align="right">Precio</th>         
	            <th class="reportCellEven" align="right">Total</th>
	        </TR>
				<%
					BmoRequisitionItem bmoRequisitionItem = new BmoRequisitionItem();
					BmFilter filterRequisitionItem = new BmFilter();
					filterRequisitionItem.setValueFilter(bmoRequisitionItem.getKind(), bmoRequisitionItem.getRequisitionId(), bmoRequisition.getId());
					Iterator<BmObject> listRequisitionItem = new PmRequisitionItem(sFParams).list(filterRequisitionItem).iterator();
					while (listRequisitionItem.hasNext()) {
						bmoRequisitionItem = (BmoRequisitionItem)listRequisitionItem.next();
				%>
	           <TR class=""> 
	               <td class="reportCellEven" align="left">
	                    <%= bmoRequisitionItem.getQuantity().toString() %>                  
	               </td>
	               <td class="reportCellEven" align="left">
	                    <%= bmoRequisitionItem.getName().toString() %>                 
	               </td>
	               <td class="reportCellEven" align="left">
	                    <%= bmoRequisitionItem.getBmoProduct().getDescription().toString() %>                 
	               </td>
	               <td class="reportCellEven" align="left">
	                    <%= bmoRequisitionItem.getBmoProduct().getModel().toString() %>                  
	               </td>      
	               <td class="reportCellEven" align="left">
	                    <%= bmoRequisitionItem.getDays().toDouble() %>                 
	               </td>            
	               <td class="reportCellEven" align="left">
	                    <%= bmoRequisitionItem.getBmoProduct().getType().getSelectedOption().getLabel() %>
	               </td>
	               <td class="reportCellEven" align="right">
	                    <%= formatCurrency.format(bmoRequisitionItem.getPrice().toDouble()) %>                 
	               </td> 
	               <td class="reportCellEven" align="right">
	                    <%= formatCurrency.format(bmoRequisitionItem.getAmount().toDouble()) %>                 
	               </td> 
	          </TR>
	          <%
	            }
	           %>
	       <tr>
	            <td>&nbsp;</td>
	        </tr>
	        <tr>
			   <td colspan="6" rowspan="4"  valign="top" align="left" class="detailStart">
			   		<p class="documentComments"><b>Descripci&oacute;n:</b><br> 
					<%= bmoRequisition.getDescription().toHtml() %></p>
			   </td> 
	            <th class="" align="right">Subtotal:</th>
	            <td class="reportCellEven" align="right"> <%= formatCurrency.format(bmoRequisition.getAmount().toDouble()) %> </td>
	        </tr>
	        <tr>
	            <th class="" align="right">Descuento:</th>
	            <td class="reportCellEven" align="right"> <%= formatCurrency.format(bmoRequisition.getDiscount().toDouble()) %> </td>
	        </tr>
	        <tr>
	            <th class="" align="right">IVA:</th>
	            <td class="reportCellEven" align="right"> <%= formatCurrency.format(bmoRequisition.getTax().toDouble()) %> </td>
	        </tr>
	        <tr>
	            <th class="" align="right">Total:</th>
	            <td class="reportCellEven" align="right"> 
	            	<b><%= formatCurrency.format(bmoRequisition.getTotal().toDouble()) %> </b>
	            </td>
	        </tr>
	</table>
	<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
	    <tr align="center">
	        <td align="center"><br><br>
	            ___________________
	        </td>
	        <td align="center"><br><br>
	        	___________________
	        </td>
	        <td align="center"><br><br>
	        	___________________
	        </td>
	    </tr>
	    <tr>
		    <td align="center" class="documentTitleCaption">
				<br>
			    <%
			    	BmoUser bmoRequestBy = new BmoUser();
			    	if (bmoRequisition.getRequestedBy().toInteger() > 0)
			    		bmoRequestBy = (BmoUser)pmUser.get(bmoRequisition.getRequestedBy().toInteger());
			    %>
			    Solicito<br>
			    <%= bmoRequestBy.getFirstname().toString() %>
			    <%= bmoRequestBy.getFatherlastname().toString() %>
			    <%= bmoRequestBy.getMotherlastname().toString() %>  
			</td>
			<td align="center" class="documentTitleCaption">
				<br>
				<%
					/*
					BmoUser bmoParent = new BmoUser();
					if (bmoRequestBy.getParentId().toInteger() > 0)
						bmoParent = (BmoUser)pmUser.get(bmoRequestBy.getParentId().toInteger());
					*/
			    %>
			    Autorizo<br>
			   	<%= bmoUserResponsibleArea.getFirstname().toString() %>&nbsp;
			    <%= bmoUserResponsibleArea.getFatherlastname().toString() %>  
			    <%= bmoUserResponsibleArea.getMotherlastname().toString() %>  

			    <%//= bmoParent.getFirstname().toString() %>&nbsp;
			    <%//= bmoParent.getFatherlastname().toString() %>     
		    </td>	     
	        <td align="center" class="documentTitleCaption">               
	            <br>
	            Recibido Por:
	            <br>
	            <%= bmoRequisition.getBmoSupplier().getLegalName().toString() %>      
	        </td>
	        <td align="center" class="documentTitleCaption"><br>
	            <br>            
	        </td>            
	    </tr>
	    
	</table>
	<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
		<p class="documentTitleCaption" align="left"><br><br> 
			Fecha de Impresi&oacute;n <%= SFServerUtil.nowToString(sFParams, "dd/MM/yyyy HH:mm") %> Por:
			<%=  sFParams.getLoginInfo().getBmoUser().getFirstname() + " " + sFParams.getLoginInfo().getBmoUser().getFatherlastname().toString() %>
		</p>
	</table>
	   
	
	<%  
	
		} catch (Exception e) { 
		    String errorLabel = "Error del la OC";
		    String errorText = "El formato de la OC no pudo ser desplegado exitosamente.";
		    String errorException = e.toString();
		    
		    response.sendRedirect(GwtUtil.getProperUrl(sFParams, "/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException));
	    }	
	
	%>
