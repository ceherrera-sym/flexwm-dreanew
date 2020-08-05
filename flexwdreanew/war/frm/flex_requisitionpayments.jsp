<!--  
/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author jhernandez
 * @version 2013-10
 */ -->
<%@page import="com.symgae.shared.GwtUtil"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.flexwm.shared.op.*"%>
<%@page import="com.flexwm.shared.cm.*"%>
<%@page import="com.flexwm.shared.fi.*"%>
<%@page import="com.flexwm.shared.wf.*"%>
<%@page import="com.flexwm.server.cm.*"%>
<%@page import="com.flexwm.server.op.*"%>
<%@page import="com.flexwm.server.fi.*"%>
<%@page import="com.flexwm.server.wf.*"%>
<%@include file="../inc/login_opt.jsp" %>

<%

	PmConn pmConn = new PmConn(sFParams);
	PmConn pmConn2 = new PmConn(sFParams);
	
	try {
		
	String title = "";

	BmoRequisition bmoRequisition = new BmoRequisition();
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
	    <title>:::Orden de Compra:::</title>
	   	<link rel="stylesheet" type="text/css" href="<%= GwtUtil.getProperUrl(sFParams, css) %> ">
   	</head>
	<body class="default" <%= permissionPrint %>>
	
	<%
			String sql = "";
			pmConn.open();
			pmConn2.open();
	
			NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(Locale.US);
		    
		    // Obtener parametros
		    int requisitionId = 0;
		    if (isExternal) requisitionId = decryptId;
		    else if (request.getParameter("foreignId") != null) requisitionId = Integer.parseInt(request.getParameter("foreignId"));
		    
		    
		    PmRequisition pmRequisition = new PmRequisition(sFParams);
		    bmoRequisition = (BmoRequisition)pmRequisition.get(requisitionId);	    
		    
		    BmoOrder bmoOrder = new BmoOrder();
		    PmOrder pmOrder = new PmOrder(sFParams);
		    if (bmoRequisition.getOrderId().toInteger() > 0)
		    	bmoOrder = (BmoOrder)pmOrder.get(bmoRequisition.getOrderId().toInteger());
			    
			BmoArea bmoArea = new BmoArea();
		    PmArea pmArea = new PmArea(sFParams);
		    if (bmoRequisition.getAreaId().toInteger() > 0)
			    bmoArea = (BmoArea)pmArea.get(bmoRequisition.getAreaId().toInteger());    
	
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
      	  	
      	  	BmoBankMovement bmoBankMovement = new BmoBankMovement();
      	  	PmBankMovement pmBankMovement = new PmBankMovement(sFParams);
      	  	
      	  	BmoPaccount bmoPaccount = new BmoPaccount();
    	  	PmPaccount pmPaccount = new PmPaccount(sFParams);
      	  
      		// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
    		String logoURL ="";
    		if (!bmoCompany.getLogo().toString().equals(""))
    			logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
    		else 
    			logoURL = sFParams.getMainImageUrl();
	%>
	
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	      <tr>
	            <td align="left" width="5%" rowspan="10" valign="top">
					<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%=logoURL %>" >
	            </td>
	            <td colspan="4" class="reportTitleCell">
	                Estado de Cuenta de la Orden de Compra: <%= bmoRequisition.getCode().toString() %>
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
	      <tr>
	            <th align = "left" class="reportCellEven">Status:</th>
	            <td class="reportCellEven">
	                <%= bmoRequisition.getStatus().getSelectedOption().getLabel() %>                
	            </td>
	            <th align = "left" class="reportCellEven">Empresa:</th>
	            <td class="reportCellEven">
	      	  		
		      	  	<%= bmoCompany.getName().toString() %>                
	      	  	</td>
	      </tr>
	      <tr>
		      <% 	
		      	int colspan = 1;
		      	if (bmoOrder.getId() > 0) {   	  
			    	  if(!(bmoRequisition.getLoanId().toInteger() > 0) && !(bmoRequisition.getBudgetItemId().toInteger() > 0)){
			    		  colspan = 3;
		    	  }
		    	  %>      
		            <th align = "left" class="reportCellEven">Pedido:</th>
		            <td class="reportCellEven" colspan="<%= colspan%>">
		                <%= bmoOrder.getCode().toString() %> - <%= bmoOrder.getName().toString() %>                 
		            </td>
	            <% }%>
	            <% if (bmoRequisition.getLoanId().toInteger() > 0) { %>
		      		<th align = "left" class="reportCellEven">Financiamiento:</th>
		      		<td class="reportCellEven">
			          	<%		            
			          		PmLoan pmLoan = new PmLoan(sFParams);	          		          	
			          		BmoLoan bmoLoan = (BmoLoan)pmLoan.get(bmoRequisition.getLoanId().toInteger());
			          	%>
			          	<b><%= bmoLoan.getCode().toString() %></b> - <%= bmoLoan.getName().toString() %>
		     	 	</td>
		      	<% } %>
	            <% if (bmoRequisition.getBudgetItemId().toInteger() > 0) {
	        		colspan = 1;
	        		
	        		if(bmoOrder.getId() > 0 && bmoRequisition.getLoanId().toInteger() > 0){
				%>
	        			</tr><tr>
	        	<%	}
	        			
	        		if(!bmoRequisition.getBmoRequisitionType().getType().equals(BmoRequisitionType.TYPE_COMMISION)
	        			|| !bmoRequisition.getBmoRequisitionType().getType().equals(BmoRequisitionType.TYPE_CREDIT)){
	        				colspan = 3;
	        		}
	            %>
		      		<th align = "left" class="reportCellEven">Presupuesto:</th>
		      		<td class="reportCellEven" colspan="<%= colspan%>">
			          	<%		            
			          		PmBudgetItem pmBudgetItem = new PmBudgetItem(sFParams);	          		          	
			          		BmoBudgetItem bmoBudgetItem = (BmoBudgetItem)pmBudgetItem.get(bmoRequisition.getBudgetItemId().toInteger());
			          	%>
			          	<b><%= bmoBudgetItem.getBmoBudget().getName().toString() %></b> - <%= bmoBudgetItem.getBmoBudget().getName().toString() %>  - <%= bmoBudgetItem.getBmoBudgetItemType().getName().toString() %>
		     	 	</td>
		      	<% }%>
	      </tr>
	      <tr>
		      <th align = "left" class="reportCellEven">Recepci&oacute;n:</th>
		      <td class="reportCellEven">		      	  
		          <%= bmoRequisition.getDeliveryStatus().getSelectedOption().getLabel() %>                
		      </td> 
		      <th align = "left" class="reportCellEven">Pago:</th>
		      <td class="reportCellEven">		      	  
		          <%= bmoRequisition.getPaymentStatus().getSelectedOption().getLabel() %>                
		      </td>
		 </tr>     
	</table>
	<br>
	
	<br> 
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		<tr>
			<td align="left" width="" rowspan="10" valign="top">
			    &nbsp;
			</td>
		    <td colspan="8" class="reportHeaderCell">
		        Anticipos Realizados
		    </td>
		</tr>
		<!-- .1 -->
		<tr class="">
	        <th class="reportCellEven" align="left" width="5%">#</td>
	        <th class="reportCellEven" align="left" width="10%">MB</td>
	        <th class="reportCellEven" align="left" width="25%">Descripci&oacute;n</td>
	        <th class="reportCellEven" align="left" width="15%">Referencia</td>
	       	<th class="reportCellEven" align="left" width="10%">Estatus</td>
	        <th class="reportCellEven" align="left" width="10%">Fecha Pago</td>
	        <th class="reportCellEven" align="left" width="10%">CxP</td>
	        <th class="reportCellEven" align="right" width="15%">Monto</td>
	    </tr>    
			      
		<%
	 	  sql = " SELECT * FROM bankmovconcepts " +		
		 	  	" LEFT JOIN bankmovements ON (bkmc_bankmovementid = bkmv_bankmovementid) " +  
				" LEFT JOIN bankmovtypes ON (bkmv_bankmovtypeid = bkmt_bankmovtypeid) " +
		 	  	" LEFT JOIN paccounts ON (pacc_paccountid = bkmc_paccountid) " + 
		        " WHERE bkmc_requisitionid = " + bmoRequisition.getId() +
		        " AND bkmc_requisitionid > 0 " +
		        //" AND bkmc_paccountid IS NULL " +
			    " AND bkmt_type = '" + BmoBankMovType.TYPE_WITHDRAW + "'" +
		        " AND bkmt_category = '" + BmoBankMovType.CATEGORY_REQUISITIONADVANCE + "'" +
		        " AND bkmv_status <> '" + BmoBankMovement.STATUS_CANCELLED + "'" +
		        " ORDER BY bkmv_bankmovementid";

		  pmConn.doFetch(sql);
		  int i = 0;
			String paccCode = "";
		  double payments = 0;
		  double payments1 = 0;
		  while(pmConn.next()) {
			  
			  if (pmConn.getInt("bankmovements", "bkmv_bankmovementid") > 0)
			  	bmoBankMovement = (BmoBankMovement)pmBankMovement.get(pmConn.getInt("bankmovements", "bkmv_bankmovementid"));
			  %>
		  <tr class="reportCellEven">
			  <td class="reportCellEven" align="left">
			  	<%= i + 1 %>
			  </td>
			  <td class="reportCellEven" align="left">
			  	<b><%= bmoBankMovement.getCode().toHtml() %></b>
			  </td>
			  <td class="reportCellEven" align="left">
			  	<%= bmoBankMovement.getDescription().toHtml() %>
			  </td>
			  <td class="reportCellEven" align="left">
			  	<%= bmoBankMovement.getBankReference().toHtml()%>
			  </td>
			  <td class="reportCellEven" align="left">
			  	<%= bmoBankMovement.getStatus().getSelectedOption().getLabel()%>
			  </td>
			  <td class="reportCellEven" align="left">
			  	<%= bmoBankMovement.getDueDate().toHtml() %>
			  </td>
			  	<%
			  		if (pmConn.getInt("bkmc_paccountid") > 0)
						paccCode = pmConn.getString("paccounts", "pacc_code");
				  %>
				  <td class="reportCellEven" align="left">
		              <b><%= paccCode%></b>
		          </td>
		          <td class="reportCellEven" align="right">
		          		<% 
		          		if (pmConn.getDouble("bkmc_amountconverted") > 0)
							payments += pmConn.getDouble("bkmc_amountconverted");
						else 
							payments += pmConn.getDouble("bkmc_amount");
		          		
		          		if (pmConn.getDouble("bkmc_amountconverted") > 0) { %>
		          				<%= formatCurrency.format(pmConn.getDouble("bkmc_amountconverted")) %>
		          		<% 	} else { %>
		          				<%= formatCurrency.format(pmConn.getDouble("bkmc_amount")) %>
		          		<% 	} 
		          	i++;
       		}%>
          </tr> 
		<%
		   
		   
		%>  
		<tr>
			<th colspan="7" align="right">Total:</th>
	        <td class="reportCellEven" align="right"> 
	        	<b><%= formatCurrency.format(payments) %></b>
	        </td>
		</tr>
	</table>
	<!-- PLox inicio de la nueva tabla -->
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		<tr>
			<td align="left" width="" rowspan="10" valign="top">
			    &nbsp;
			</td>
		    <td colspan="8" class="reportHeaderCell">
		       Devoluciones	
		    </td>
		</tr>
			<!-- TABLA 2  -->
		<tr class="">
	        <th class="reportCellEven" align="left" width="5%">#</td>
	        <th class="reportCellEven" align="left" width="10%">MB</td>
	        <th class="reportCellEven" align="left" width="25%">Descripci&oacute;n</td>
	        <th class="reportCellEven" align="left" width="15%">Referencia</td>
	       	<th class="reportCellEven" align="left" width="10%">Estatus</td>
	        <th class="reportCellEven" align="left" width="10%">Fecha Pago</td>
	        <th class="reportCellEven" align="left" width="10%">CxP</td>
	        <th class="reportCellEven" align="right" width="15%">Monto</td>
	    </tr>    

		<%
		sql = " SELECT * FROM bankmovconcepts " +
		 	  	  " LEFT JOIN  bankmovements ON (bkmc_bankmovementid = bkmv_bankmovementid) " +  
				  " LEFT JOIN bankmovtypes ON (bkmv_bankmovtypeid = bkmt_bankmovtypeid) " +
		 		  " LEFT JOIN paccounts ON (bkmc_paccountid = pacc_paccountid) " +
		 		  " LEFT JOIN requisitionreceipts ON (pacc_requisitionreceiptid = rerc_requisitionreceiptid) " +
			      " WHERE pacc_reqicode = '" + bmoRequisition.getCode().toString()  +"'"+
			      " AND bkmt_category = '" + BmoBankMovType.CATEGORY_REQUISITIONADVANCE + "'" +
			     " AND bkmt_type = '" + BmoBankMovType.TYPE_DEPOSIT + "'" +
			      " ORDER BY bkmv_bankmovementid";
		System.out.println("------------------------------------------   "+sql);
		  pmConn.doFetch(sql);
		   i = 0;
		  double payments1n = 0;
		  while(pmConn.next()) {			  
			  //if (pmConn.getInt("bankmovements", "bkmv_bankmovementid") > 0)
			  //bmoBankMovement = (BmoBankMovement)pmBankMovement.get(pmConn.getInt("bankmovements", "bkmv_bankmovementid"));
			  bmoBankMovement.getStatus().setValue(pmConn.getString("bkmv_status"));
			  %>
		  <tr class="reportCellEven">
			  <td class="reportCellEven" align="left">
			  	<%= i +1 %>
			  </td>
			  <td class="reportCellEven" align="left">
			  	<b><%= pmConn.getString("bkmv_code") %></b>
			  </td>
			  <td class="reportCellEven" align="left">
			  	<%= pmConn.getString("bkmv_description") %>
			  </td>
			  <td class="reportCellEven" align="left">
			  	<%= pmConn.getString("bkmv_bankreference")%>
			  </td>
			  <td class="reportCellEven" align="left">
			  	<%= bmoBankMovement.getStatus().getSelectedOption().getLabel()%>
			  </td>
			  <td class="reportCellEven" align="left">
			  	<%= pmConn.getString("bkmv_inputDate")%>
			  </td>
			  <%
			  	//Obtener la CxP
			  	boolean existLinkPacc =  false; 
				existLinkPacc =  true;
				paccCode = pmConn.getString("pacc_code");
				%>
				<td class="reportCellEven" align="left">
		        	<b><%= paccCode%></b>
		        </td>
		        <td class="reportCellEven" align="right">
			        <%
			        double paymentsBkmc = 0;
			     	if (pmConn.getDouble("bkmc_amountconverted") > 0)
			     		paymentsBkmc = pmConn.getDouble("bkmc_amountconverted");
			     	else
			     		paymentsBkmc = pmConn.getDouble("bkmc_amount");
			     	
			     	payments1n += paymentsBkmc;
	
					%>
		          	<%= formatCurrency.format(paymentsBkmc) %>
		          </td>
          </tr> 
		<%
		   i++;
		   } 
		%>  
		<tr>
			<th colspan="7" align="right">Total:</th>
	        <td class="reportCellEven" align="right"> 
	        	<b><%= formatCurrency.format(payments1n) %></b>
	        </td>
		</tr>
	</table>
	<!-- PLox fin de la nueva tabla -->
	<br>
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		<tr>
		    <td colspan="8" class="reportHeaderCell">
		        Pagos Realizados
		    </td>
		</tr>
		<tr class="">
	        <th class="reportCellEven" align="left" width="5%">#</td>
	        <th class="reportCellEven" align="left" width="10%">MB</td>
	        <th class="reportCellEven" align="left" width="25%">Descripci&oacute;n</td>
	        <th class="reportCellEven" align="left" width="15%">Referencia</td>
	        <th class="reportCellEven" align="left" width="10%">Estatus</td>
	        <th class="reportCellEven" align="left" width="10%">Fecha Pago</td>
	        <th class="reportCellEven" align="left" width="10%">CxP</td>
	        <th class="reportCellEven" align="right" width="15%">Monto</td>
	    </tr>
		<%
		  sql = " SELECT * FROM bankmovconcepts " +
				" LEFT JOIN bankmovements ON (bkmc_bankmovementid = bkmv_bankmovementid) " +  
				" LEFT JOIN bankmovtypes ON (bkmv_bankmovtypeid = bkmt_bankmovtypeid) " +
				" LEFT JOIN paccounts ON (bkmc_paccountid = pacc_paccountid) " +
				" LEFT JOIN requisitionreceipts ON (pacc_requisitionreceiptid = rerc_requisitionreceiptid) " +
		        " WHERE rerc_requisitionid = " + bmoRequisition.getId() +
		        " AND bkmt_category <> '" + BmoBankMovType.CATEGORY_REQUISITIONADVANCE + "'" +
		        " ORDER BY bkmv_bankmovementid";
		  pmConn.doFetch(sql);
		  i = 0;
		  double payments2 = 0;
		  while(pmConn.next()) {			  
			  	if (pmConn.getDouble("bkmc_amountconverted") > 0) {
				 	payments2 += pmConn.getDouble("bkmc_amountconverted");
				} else {
					payments2 += pmConn.getDouble("bkmc_amount");
				}			  
			  
			  if (pmConn.getInt("bankmovements", "bkmv_bankmovementid") > 0)
				  	bmoBankMovement = (BmoBankMovement)pmBankMovement.get(pmConn.getInt("bankmovements", "bkmv_bankmovementid"));
			  %>
			  <tr class="reportCell">
			  	<td class="reportCellEven">
			  		<%= i +1 %>
		  		</td>
		  		<td class="reportCellEven">
		  			<b><%= bmoBankMovement.getCode().toHtml() %></b>
		  		</td>
		  		<td class="reportCellEven">
		  			<%= bmoBankMovement.getDescription().toHtml() %>
		  		</td>
		  		<td class="reportCellEven">
		  			<%= bmoBankMovement.getBankReference().toHtml() %>
		  		</td>
		  		<td class="reportCellEven" align="left">
				  	<%= bmoBankMovement.getStatus().getSelectedOption().getLabel()%>
			  	</td>
		  		<td class="reportCellEven">
		  			<%= bmoBankMovement.getDueDate().toHtml() %>
		  		</td>
		  		<td class="reportCellEven">
		  			<b><%= pmConn.getString("paccounts", "pacc_code")%></b>
		  		</td>
		  		<td class="reportCellEven" align="right">
			  		<% 	if (pmConn.getDouble("bkmc_amountconverted") > 0) { %>
	          				<%= formatCurrency.format(pmConn.getDouble("bkmc_amountconverted")) %>
	          		<% 	} else { %>
	          				<%= formatCurrency.format(pmConn.getDouble("bkmc_amount")) %>
	          		<% 	} %>
				</td>
		      </tr>
		<%
		   i++;
		   } 
		%>  
		<tr>
			<th colspan="7" class="" align="right">Total:</th>
			<td class="reportCellEven" align="right"> 
	      		<b><%= formatCurrency.format(payments2) %> </b>
      		</td>
		</tr>
	</table>
	<br>
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		<tr>
		    <td colspan="8" class="reportHeaderCell">
		        Pagos Notas Cr&eacute;dito
		    </td>
		</tr>
		<tr>
		    <th class="reportCellEven" align="left" width="5%">#</td>
		    <th class="reportCellEven" align="left" width="10%">Clave</td>
		    <th class="reportCellEven" align="left" width="25%">Descripci&oacute;n</td>
		    <th class="reportCellEven" align="left" width="15%">Factura</td>
		    <th class="reportCellEven" align="left" width="10%">Estatus</td>
		    <th class="reportCellEven" align="left" width="10%">Fecha Pago</td>
		    <th class="reportCellEven" align="left" width="10%">&nbsp;</td>	    
		    <th class="reportCellEven" align="right" width="15%">Monto</td>
		</tr>  
		<%
		sql = " SELECT * FROM paccountassignments " +
			  " LEFT JOIN paccounts ON (pass_paccountid = pacc_paccountid) " +
			  " LEFT JOIN paccounttypes ON (pact_paccounttypeid = pacc_paccounttypeid) " +				  			
			  " WHERE pacc_payments > 0 " +
			  " AND pact_category  = '" + BmoPaccountType.CATEGORY_CREDITNOTE + "'" +  
			  " AND pact_type = '" + BmoPaccountType.TYPE_DEPOSIT + "'" +
			  " AND pass_foreignpaccountid IN ( " +
				  " SELECT pacc_paccountid FROM paccounts " +
				  "	LEFT JOIN paccounttypes ON (pact_paccounttypeid = pacc_paccounttypeid) " +  
				  "	WHERE pact_type = '" + BmoPaccountType.TYPE_WITHDRAW + "'" +
				  "	AND pacc_requisitionid = " + bmoRequisition.getId() +
				  "  ) ";		
		pmConn.doFetch(sql);		
		i = 0;
		double payments3 = 0;
		while(pmConn.next()) {
			if (pmConn.getDouble("pass_amountconverted") > 0) {
			 	payments3 += pmConn.getDouble("pass_amountconverted");
			} else {
				payments3 += pmConn.getDouble("pacc_payments");
			}
						
			if (pmConn.getInt("paccounts", "pacc_paccountid") > 0)
			  	bmoPaccount = (BmoPaccount)pmPaccount.get(pmConn.getInt("paccounts", "pacc_paccountid"));
	  %>
		  	<tr class="reportCell">
			  	<td class="reportCellEven">
			  		<%= i +1 %>
		        </td>
		        <td class="reportCellEven">
		        	<b><%= bmoPaccount.getCode().toHtml()%></b>
			    </td>
		        <td class="reportCellEven">
		        	<%= bmoPaccount.getDescription().toHtml()%>
			    </td>
		        <td class="reportCellEven">
		        	<%= bmoPaccount.getInvoiceno().toHtml()%>
			    </td>
			    <td class="reportCellEven" align="left">
				  	<%= bmoPaccount.getStatus().getSelectedOption().getLabel()%>
			 	</td>
		        <td class="reportCellEven">
		        	<%= bmoPaccount.getDueDate().toHtml()%>
			    </td>
		        <td class="reportCellEven">
			  		&nbsp;
			    </td>
		        <td class="reportCellEven" align="right">
			  		<% if (pmConn.getDouble("pass_amountconverted") > 0) { %>
			  			<%= formatCurrency.format(pmConn.getDouble("pass_amountconverted"))%>
					<% } else { %>
						<%= formatCurrency.format(bmoPaccount.getPayments().toDouble())%>
					<% }%>
			    </td>
		    </tr>
	<%
		   	i++;
	   }
	%>  
		<tr>
			<th colspan="7" class="" align="right">Total:</th>
			<td class="reportCellEven" align="right"> 
				<b><%= formatCurrency.format(payments3) %> </b>
			</td>
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="6" rowspan="7" valign="top" align="left" class="detailStart">
				<p class="documentComments"><b>Descripci&oacute;n:</b><br> 
				<%= bmoRequisition.getDescription().toHtml() %></p>
			</td> 
			<th align="right">Monto:</th>
			<td class="reportCellEven" align="right">
				<%= formatCurrency.format(bmoRequisition.getAmount().toDouble()) %>
			</td>
		</tr>
		<tr>
			<th align="right">Descuento:</th>
			<td class="reportCellEven" align="right">
				<%= formatCurrency.format(bmoRequisition.getDiscount().toDouble()) %>
			</td>
		</tr>
		<tr>
			<th align="right">IVA:</th>
			<td class="reportCellEven" align="right">
				<%= formatCurrency.format(bmoRequisition.getTax().toDouble()) %>
			</td>
		</tr>
		<tr>
			<th align="right">Retenciones:</th>
			<td class="reportCellEven" align="right">
				<%= formatCurrency.format(bmoRequisition.getHoldBack().toDouble()) %>
			</td>
		</tr>
		<tr>
			<th align="right">Total:</th>
			<td class="reportCellEven" align="right"> 
				<b><%= formatCurrency.format(bmoRequisition.getTotal().toDouble()) %></b>
			</td>
		</tr>
		<tr>
			<th align="right">Pagos:</th>
			<td class="reportCellEven" align="right"> 
				<b><%= formatCurrency.format(bmoRequisition.getPayments().toDouble()) %></b>
			</td>
		</tr>
		<tr>
			<th align="right">Saldo:</th>
			<td class="reportCellEven" align="right"> 
				<b><%= formatCurrency.format(bmoRequisition.getBalance().toDouble()) %></b>
			</td>
		</tr>
	</table>
	<p class="documentTitleCaption" align="left"> 
		Fecha de Impresi&oacute;n <%= SFServerUtil.nowToString(sFParams, "dd/MM/yyyy HH:mm") %>
	</p>
	
	<%  
		} catch (Exception e) { 
		    String errorLabel = "Error del Estado Cuenta OC";
		    String errorText = "El Estado Cuenta OC no pudo ser desplegado exitosamente.";
		    String errorException = e.toString();
		    
		    response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);
	    } finally {
	    	pmConn2.close();
			pmConn.close();
	    }
	%>