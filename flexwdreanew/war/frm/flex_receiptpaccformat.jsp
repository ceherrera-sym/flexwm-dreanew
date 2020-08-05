<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.shared.op.*"%>
<%@page import="com.flexwm.shared.cm.*"%>
<%@page import="com.flexwm.shared.fi.*"%>
<%@page import="com.flexwm.shared.wf.*"%>

<%@page import="com.flexwm.server.cm.*"%>
<%@page import="com.flexwm.server.op.*"%>
<%@page import="com.flexwm.server.fi.*"%>
<%@page import="com.flexwm.server.wf.*"%>
<%@page import="com.symgae.client.ui.UiParams"%>

<%@include file="../inc/login_opt.jsp" %>
<%
	String title = "";

	BmoPaccount bmoPaccount = new BmoPaccount();	
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)sFParams.getBmoProgram(bmoPaccount.getProgramCode());

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
		
	//No cargar datos en caso de que se imprima/exporte y no tenga permisos
	if(sFParams.hasPrint(bmoProgram.getCode().toString()) || !(exportExcel.equals("1"))) { 
%>
		<head>
		    <title>:::Recibo CxP.:::</title>
		    <link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>"> 
		     <link rel="stylesheet" type="text/css" href="../css/flexwm.css"> 
		</head>
		<body class="default" <%= permissionPrint %>>
		<%
		
		NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(Locale.US);
		
		
		String sql = "";
		try {
			int paccountId = 0, projectId = 0;
			String projectName = "", projectDate = "", projectCity = "", clientName = "";
			if (isExternal) paccountId = decryptId;
			else paccountId = Integer.parseInt(request.getParameter("foreignId"));
			
			PmPaccount pmPaccount = new PmPaccount(sFParams);
		    bmoPaccount = (BmoPaccount)pmPaccount.get(paccountId);
		        
			BmoRequisitionReceipt bmoRequisitionReceipt = new BmoRequisitionReceipt();
		
		    if (bmoPaccount.getRequisitionReceiptId().toInteger() > 0) {
		    	PmRequisitionReceipt pmRequisitionReceipt = new PmRequisitionReceipt(sFParams);
		    	bmoRequisitionReceipt = (BmoRequisitionReceipt)pmRequisitionReceipt.get(bmoPaccount.getRequisitionReceiptId().toInteger());
		    }
		    
		    BmoCompany bmoCompany = new BmoCompany();
			PmCompany pmCompany = new PmCompany(sFParams);
			bmoCompany = (BmoCompany)pmCompany.get(bmoPaccount.getCompanyId().toInteger());
			
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
			       	Recibo de CxP: <%= bmoPaccount.getCode().toString() %>
			       </td>
			 </tr>     
			 <tr>
			       <th align = "left" class="reportCellEven">Empresa:</th>
			       <td class="reportCellEven">
			            <%= bmoPaccount.getBmoCompany().getName().toString() %>                 
			       </td>
			       <th align = "left" class="reportCellEven">Tipo de CxP:</th>
			       <td class="reportCellEven">
			       		<%= bmoPaccount.getBmoPaccountType().getName().toString() %>                 
			       </td>
			 </tr>
			 <tr>
			     <th align = "left" class="reportCellEven">Proveedor:</th>
			     <td class="reportCellEven">
			     <%= bmoPaccount.getBmoSupplier().getCode().toString() %> - <%= bmoPaccount.getBmoSupplier().getName().toString() %>	         
			     </td>
			     <th align = "left" class="reportCellEven">Recibo O. C.:</th>
			     <td class="reportCellEven">
			     	<% if (bmoPaccount.getRequisitionReceiptId().toInteger() > 0) { %>
			     		<%= bmoRequisitionReceipt.getCode().toString() %>  <%= bmoRequisitionReceipt.getName().toString() %>
			     	<% }else { %>
			     		-
			     	<% }%>
			     	
			     </td>
			 </tr>
			 <tr>		
			  		<%
			  			PmUser pmUser = new PmUser(sFParams);
			  			BmoUser bmoUserAutorized = new BmoUser();
			    		if (bmoPaccount.getAuthorizedUser().toInteger() > 0)	    			
			    			bmoUserAutorized = (BmoUser)pmUser.get(bmoPaccount.getAuthorizedUser().toInteger());
			    	%>
			    	<th align = "left" class="reportCellEven">Autorizada por:</th>
			        <td class="reportCellEven">
			            <%= bmoUserAutorized.getFirstname().toString() + " " + bmoUserAutorized.getFatherlastname().toString() %>                
			        </td>
			        <th align="left" class="reportCellEven">Fecha Autorizaci&oacute;n:</th>
					<td class="reportCellEven"><%=bmoPaccount.getAuthorizedDate().toString()%>
					</td>
			  </tr>
			  <tr>
				 	<th align = "left" class="reportCellEven">Estatus:</th>
					<td class="reportCellEven">
					 	&nbsp;<%= bmoPaccount.getStatus().getSelectedOption().getLabel() %>         
					</td>
				 	<th align = "left" class="reportCellEven">Pago:</th>
					 <td class="reportCellEven">
					 	&nbsp;<%= bmoPaccount.getPaymentStatus().getSelectedOption().getLabel() %>       
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
					<%= bmoPaccount.getInvoiceno().toString() %>         
				</td>
				<th align = "left" class="reportCellEven">Descripci&oacute;n:</th>
				<td class="reportCellEven" colspan="4">
					<%= bmoPaccount.getDescription().toString() %>         
				</td>
			</tr> 
			<tr>
				<th align = "left" class="reportCellEven">Fecha:</th>
				<td class="reportCellEven">
					<%= bmoPaccount.getReceiveDate().toString() %>         
				</td>
				<th align = "left" class="reportCellEven">Programaci&oacute;n:</th>
				<td class="reportCellEven">
					<%= bmoPaccount.getDueDate().toString() %>       
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
			<th align = "left" class="reportCellEven">Total:</th>
			<td class="reportCellEven">
				<%= formatCurrency.format(bmoPaccount.getTotal().toDouble()) %>        
			</td>
			<td class="reportCellEven" colspan="2"></td>
		</tr>
		<tr>
			<th align = "left" class="reportCellEven">Pago:</th>
			<td class="reportCellEven">
				<%= formatCurrency.format(bmoPaccount.getPayments().toDouble()) %>        
			</td>
			<td class="reportCellEven" colspan="2"></td>
		</tr>
		<tr>
			<th align = "left" class="reportCellEven">Saldo:</th>
			<td class="reportCellEven">
				<%= formatCurrency.format(bmoPaccount.getBalance().toDouble()) %>        
			</td>
			<td class="reportCellEven" colspan="2"></td>
		</tr>
		</table>	
		<p class="documentTitleCaption" align="left"> 
			Fecha de Impresi&oacute;n <%= SFServerUtil.nowToString(sFParams, "dd/MM/yyyy HH:mm") %> Por:
			<%=  sFParams.getLoginInfo().getBmoUser().getFirstname() + " " + sFParams.getLoginInfo().getBmoUser().getFatherlastname().toString() %>
		</p>
		    
		    
			
		<%  } catch (Exception e) { 
		    String errorLabel = "Error de Recibo NF";
		    String errorText = "El recibo no pudo ser desplegado exitosamente.";
		    String errorException = e.toString();
		    
		    response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);
		    }
		}
		%>
</body>
</html>
