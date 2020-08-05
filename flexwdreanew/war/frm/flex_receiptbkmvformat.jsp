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
<%@include file="../inc/login_opt.jsp"%>
<%
String title = "";

BmoBankMovement bmoBankMovement = new BmoBankMovement();
BmoProgram bmoProgram = new BmoProgram();
PmProgram pmProgram  = new PmProgram(sFParams);
bmoProgram = (BmoProgram)sFParams.getBmoProgram(bmoBankMovement.getProgramCode());

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
body {
	user-select: none;
	-moz-user-select: none;
	-o-user-select: none;
	-webkit-user-select: none;
	-ie-user-select: none;
	-khtml-user-select: none;
	-ms-user-select: none;
	-webkit-touch-callout: none
}
</style>
<style type="text/css" media="print">
* {
	display: none;
}
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
<title>:::Recibo de Movimiento Bancario:::</title>
<link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>">
<link rel="stylesheet" type="text/css" href="../css/flexwm.css">
</head>
<body class="default" <%= permissionPrint %>>

	<%
		NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(Locale.US);
		PmConn pmConnEvento = new PmConn(sFParams);
		pmConnEvento.open();
		PmConn concepts = new PmConn(sFParams);
		concepts.open();
		String nameProj="";
	    // Obtener parametros
	    int bankMovementId = 0;
	    String sql="", sqlConcepts; 
	    int movConcept=0, movRequi = 0;
	      
	    if (isExternal) bankMovementId = decryptId;
	    else if (request.getParameter("foreignId") != null) bankMovementId = Integer.parseInt(request.getParameter("foreignId"));
	    
	    
	    PmBankMovement pmBankMovement = new PmBankMovement(sFParams);
	    bmoBankMovement = (BmoBankMovement)pmBankMovement.get(bankMovementId);
	    
	    BmoBankAccount bmoBankAccount = new BmoBankAccount();
	    
	    if(bmoBankMovement.getBankAccoTransId().toInteger() > 0) {
		    PmBankAccount pmBankAccount = new PmBankAccount(sFParams);
		    bmoBankAccount = (BmoBankAccount)pmBankAccount.get(bmoBankMovement.getBankAccoTransId().toInteger());
	    }
	    
	    BmoCompany bmoCompany = new BmoCompany();
		PmCompany pmCompany = new PmCompany(sFParams);
		bmoCompany = (BmoCompany)pmCompany.get(bmoBankMovement.getBmoBankAccount().getCompanyId().toInteger());
		
		// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
		String logoURL ="";
		if (!bmoCompany.getLogo().toString().equals(""))
			logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
		else 
			logoURL = sFParams.getMainImageUrl();
	    
%>

	<table border="0" cellspacing="0" width="100%" cellpadding="0"
		style="font-size: 12px">
		<tr>
			<td align="left" width="" rowspan="10" valign="top"><img
				border="0" width="<%= SFParams.LOGO_WIDTH %>"
				height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>"></td>
			<td colspan="4" class="reportTitleCell">Movimiento Bancario: <%= bmoBankMovement.getCode().toString() %>
			</td>
		</tr>
		<tr>
			<th align="left" class="reportCellEven">Tipo Movimiento:</th>
			<td class="reportCellEven"><%= bmoBankMovement.getBmoBankMovType().getType().getSelectedOption().getLabel() %>
				- <%= bmoBankMovement.getBmoBankMovType().getName().toString() %></td>
			<% 	if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)
            		|| bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_CXP)) { %>
			<th align="left" class="reportCellEven">Proveedor:</th>
			<td class="reportCellEven"><%= bmoBankMovement.getBmoSupplier().getCode().toString() %>
				- <%= bmoBankMovement.getBmoSupplier().getName().toString() %></td>
			<% 	} else if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_CXC)
           			|| bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_DEVOLUTIONCXC)) { %>
			<th align="left" class="reportCellEven">Cliente:</th>
			<td class="reportCellEven"><%= bmoBankMovement.getBmoCustomer().getCode().toString() %>
				- <%= bmoBankMovement.getBmoCustomer().getDisplayName().toString() %>
			</td>
			<%  } %>
		</tr>
		<tr>
			<th align="left" class="reportCellEven">Cuenta de Banco:</th>
			<td class="reportCellEven"><%=bmoBankMovement.getBmoBankAccount().getName().toString() %>
			</td>
			<th align="left" class="reportCellEven">No.Cuenta:</th>
			<td class="reportCellEven"><%=bmoBankMovement.getBmoBankAccount().getAccountNo().toString() %>
			</td>
		</tr>
		<tr>
			<th align="left" class="reportCellEven">Descripci&oacute;n:</th>
			<td class="reportCellEven">&nbsp;<%= bmoBankMovement.getDescription().toString() %>
			</td>
			<th align="left" class="reportCellEven">Evento:</th>
			<% 	      
		      sqlConcepts =  "SELECT * FROM bankmovconcepts " + 
		    		  		" LEFT JOIN bankmovements on(bkmv_bankmovementid=bkmc_bankmovementid) " +
		      				" WHERE bkmc_bankmovementid = " + bmoBankMovement.getId();
		      concepts.doFetch(sqlConcepts);
			  if (concepts.next()) {
					movConcept = concepts.getInt("bkmc_bankmovconceptid");
					movRequi = concepts.getInt("bkmv_requisitionid");
			  }
	      
	      	BmoBankMovConcept eventoBmoBankMovConcept = new BmoBankMovConcept(); 
			PmBankMovConcept eventoPmBankMovConcept= new PmBankMovConcept(sFParams);
			if(movConcept > 0){
				eventoBmoBankMovConcept = (BmoBankMovConcept)eventoPmBankMovConcept.getBy(bmoBankMovement.getId(), eventoBmoBankMovConcept.getBankMovementId().getName());
				if(eventoBmoBankMovConcept.getRaccountId().toInteger() > 0){
					sql = " SELECT * FROM bankmovconcepts " +
							" LEFT JOIN bankmovements on(bkmv_bankmovementid=bkmc_bankmovementid)" +
							" LEFT JOIN raccounts on(racc_raccountid=bkmc_raccountid)" +
							" LEFT JOIN orders on(racc_orderid=orde_orderid)" +
							" LEFT JOIN projects on(proj_orderid = orde_orderid) " +
							" WHERE bkmv_bankmovementid = " + eventoBmoBankMovConcept.getBankMovementId().toInteger();
				}
				
				if(eventoBmoBankMovConcept.getPaccountId().toInteger() > 0){
					sql = " SELECT * FROM bankmovconcepts " +
							" LEFT JOIN bankmovements on(bkmv_bankmovementid=bkmc_bankmovementid)" +
							" LEFT JOIN paccounts on(pacc_paccountid=bkmc_paccountid)" +					
							" LEFT JOIN requisitionreceipts on(rerc_requisitionreceiptid=pacc_requisitionreceiptid) " +
							" LEFT JOIN requisitions on(reqi_requisitionid=rerc_requisitionid) " +
							" LEFT JOIN orders on(orde_orderid = reqi_orderid) " +
							" LEFT JOIN projects on(proj_orderid = orde_orderid) " +
							" WHERE bkmv_bankmovementid = " + eventoBmoBankMovConcept.getBankMovementId().toInteger();
					
				}
				
				if(movRequi > 0){
					sql = " SELECT * FROM requisitions " +
							" LEFT JOIN orders on(orde_orderid = reqi_orderid) " +
							" LEFT JOIN projects on(proj_orderid = orde_orderid) " +
							" WHERE reqi_requisitionid = " + movRequi;
				}
				
				if((eventoBmoBankMovConcept.getRaccountId().toInteger() > 0) || (eventoBmoBankMovConcept.getPaccountId().toInteger() > 0) || movRequi > 0){
					System.out.println("entras pmConnEvento----: "+ sql);
					pmConnEvento.doFetch(sql);
					if (pmConnEvento.next()) {
						nameProj = pmConnEvento.getString("proj_code")+" "+pmConnEvento.getString("proj_name");
				    }
				}
			}
	      %>
			<td class="reportCellEven">&nbsp; <%= nameProj %>
			</td>
		</tr>
		<tr>
			<th align="left" class="reportCellEven">Fecha:</th>
			<td class="reportCellEven">&nbsp;<%= bmoBankMovement.getInputDate().toString() %>
			</td>
			<th align="left" class="reportCellEven">Pago:</th>
			<td class="reportCellEven">&nbsp;<%= bmoBankMovement.getDueDate().toString() %>
			</td>
		</tr>
		<% 
	  if (bmoBankMovement.getBmoBankMovType().getCategory().toChar() != BmoBankMovType.CATEGORY_TRANSFER) {
	  }else {%>
		<tr>
			<th align="left"  class="reportCellEven">Cta.Banco
				Destino:</th>
			<td class="reportCellEven">&nbsp;<%= bmoBankAccount.getName().toString() %>
			</td>
		</tr>
		<% }%>

		<tr>
			<th align="left" class="reportCellEven">Monto:</th>
			<td class="reportCellEven">&nbsp;<%= formatCurrency.format(bmoBankMovement.getWithdraw().toDouble()) %>
			</td>
			<th align="left" class="reportCellEven">Abono:</th>
			<td class="reportCellEven">&nbsp;<%= formatCurrency.format(bmoBankMovement.getDeposit().toDouble()) %>
			</td>
		</tr>
		<tr>
			<th align="left" class="reportCellEven">Estatus:</th>
			<td class="reportCellEven" colspan="3"><%= bmoBankMovement.getStatus().getSelectedOption().getLabel()%>
			</td>
		</tr>
	</table>
	<br>

	<br>
	<% 
if (bmoBankMovement.getBmoBankMovType().getCategory().toChar() != BmoBankMovType.CATEGORY_TRANSFER ) {
%>


	<table border="0" cellspacing="0" width="100%" cellpadding="0"
		style="font-size: 12px">
		<tr>
			<td colspan="10" class="reportHeaderCell">Detalle del Movimiento
				de Banco</td>
		</tr>

		<%
				int celda = 1;
			
				BmoBankMovConcept bmoBankMovConcept = new BmoBankMovConcept();
				BmFilter filterBankMovConcept = new BmFilter();
				filterBankMovConcept.setValueFilter(bmoBankMovConcept.getKind(), bmoBankMovConcept.getBankMovementId(), bmoBankMovement.getId());
				Iterator<BmObject> listBankMovConcept = new PmBankMovConcept(sFParams).list(filterBankMovConcept).iterator();
				while (listBankMovConcept.hasNext()) {
					bmoBankMovConcept = (BmoBankMovConcept)listBankMovConcept.next();
					
					BmoRaccount bmoRaccount = new BmoRaccount();
				    PmRaccount pmRaccount = new PmRaccount(sFParams);
					BmoUser bmoUser = new BmoUser();
					PmUser pmUser = new PmUser(sFParams);
				    BmoPaccount bmoPaccount = new BmoPaccount();
					PmPaccount pmPaccount = new PmPaccount(sFParams);
				    
				    BmoRequisition bmoRequisition = new BmoRequisition();
					PmRequisition pmRequisition = new PmRequisition(sFParams);
					if (bmoBankMovConcept.getRequisitionId().toInteger() > 0) {
				    	bmoRequisition = (BmoRequisition)pmRequisition.get(bmoBankMovConcept.getRequisitionId().toInteger());
				    	bmoUser = (BmoUser) pmUser.get(bmoRequisition.getUserCreateId().toInteger());
				    	if(bmoBankMovConcept.getPaccountId().toInteger()>0)
				    	bmoPaccount = (BmoPaccount) pmPaccount.getBy(bmoRequisition.getId(), bmoPaccount.getRequisitionId().getName());
				    	if(celda==1){
						%>
						
						<TR class="">
						<th class="reportCellEven" align="left">O.C</th>
						<th class="reportCellEven" align="left">CXP</th>

			<th class="reportCellEven" align="left">Nombre O.C</th>
			<th class="reportCellEven" align="left">F. Solicitud</th>
			<th class="reportCellEven" align="left">F. Entrega</th>
			<th class="reportCellEven" align="left">Autorizacion</th>
			<th class="reportCellEven" align="right">Total</th>
		</TR>
		<%
				    celda = 0;
					
				    	}
				    	}else{
						if(bmoBankMovConcept.getRaccountId().toInteger() > 0) {
							 bmoRaccount = (BmoRaccount)pmRaccount.get(bmoBankMovConcept.getRaccountId().toInteger());
							    if(celda==1){
								    %>
		<TR class="">
			<th class="reportCellEven" align="left">CxC</th>
			<th class="reportCellEven" align="left">Factura</th>
			<th class="reportCellEven" align="left">Folio</th>
			<th class="reportCellEven" align="left">Cliente</th>
			<th class="reportCellEven" align="left">Programaci&oacute;n</th>
			<th class="reportCellEven" align="left">Cobro en Bancos</th>
			<th class="reportCellEven" align="left">Descripci&oacute;n</th>
			<th class="reportCellEven" align="left">Estatus</th>
			<th class="reportCellEven" align="left">Estatus Pago</th>
			<th class="reportCellEven" align="right">Monto</th>
		</TR>
		<% 		celda=0; 
							    }
						}else{
							if(bmoBankMovConcept.getPaccountId().toInteger() > 0) {
							    bmoPaccount = (BmoPaccount)pmPaccount.get(bmoBankMovConcept.getPaccountId().toInteger());
							    if(celda==1){ %>
		<TR class="">
			<th class="reportCellEven" align="left">CxP</th>
			<th class="reportCellEven" align="left">Factura</th>
			<th class="reportCellEven" align="left">Clave Prov.</th>
			<th class="reportCellEven" align="left">Nombre Prov.</th>
			<th class="reportCellEven" align="left">Descripci&oacute;n</th>
			<th class="reportCellEven" align="left">Fecha de Prog.</th>
			<th class="reportCellEven" align="left">Fecha de Pago</th>
			<th class="reportCellEven" align="left">Estatus</th>
			<th class="reportCellEven" align="left">Estatus de Pago</th>
			<th class="reportCellEven" align="right">Monto</th>
		</TR>
		<%		celda=0; 
							    }
					    	}
						}
				    }
				    %>

		<TR class="">

			<%  if(bmoBankMovConcept.getRequisitionId().toInteger() > 0) { %>
				
		<td class="reportCellEven" align="left">&nbsp;<%= bmoRequisition.getCode().toString() %>
			</td>
			<%
			if(bmoPaccount.getId()>0){
			%>
						<td class="reportCellEven" align="left">&nbsp;<%= bmoPaccount.getCode().toString() %>
			</td>
			<%
			}else{
				%>
							<td class="reportCellEven" align="left">-</td>
				<%
			}
			%>
			<td class="reportCellEven" align="left">&nbsp;<%= bmoRequisition.getName().toString() %>
			</td>
			<td class="reportCellEven" align="left">&nbsp;<%= bmoRequisition.getRequestDate().toString() %>
			</td>
			<td class="reportCellEven" align="left">&nbsp;<%= bmoRequisition.getDeliveryDate().toString() %>
			</td>
			<td class="reportCellEven" align="left">&nbsp;<%= bmoUser.getFirstname().toString() %>
			</td>
			<td class="reportCellEven" align="right">&nbsp;<%= formatCurrency.format(bmoRequisition.getTotal().toDouble()) %>
			</td>
			<%
			} else {
		           	   		
			           		if(bmoBankMovConcept.getRaccountId().toInteger() > 0) { 
						    	bmoUser = (BmoUser) pmUser.get(bmoRaccount.getUserCreateId().toInteger());
			           		%>
			<td class="reportCellEven" align="left">&nbsp;<%= bmoRaccount.getCode().toString() %>
			</td>
			<td class="reportCellEven" align="left">&nbsp;<%= bmoRaccount.getInvoiceno().toString() %>
			</td>
			<td class="reportCellEven" align="left">&nbsp;<%= bmoRaccount.getFolio().toString() %>
			</td>
			<td class="reportCellEven" align="left">&nbsp;<%= bmoRaccount.getBmoCustomer().getDisplayName().toString() %>
			</td>
			<td class="reportCellEven" align="left">&nbsp;<%= bmoRaccount.getDueDate().toString() %>
			</td>
			<td class="reportCellEven" align="left">&nbsp;<%= bmoRaccount.getPaymentDate().toString() %>
			</td>
			<td class="reportCellEven" align="left">&nbsp;<%= bmoRaccount.getDescription().toString() %>
			</td>
			<td class="reportCellEven" align="left">&nbsp;<%= bmoRaccount.getStatus().getSelectedOption().getLabel() %>
			</td>
			<td class="reportCellEven" align="left">&nbsp;<%= bmoRaccount.getPaymentStatus().getSelectedOption().getLabel() %>
			</td>
			<td class="reportCellEven" align="right">&nbsp;<%= formatCurrency.format(bmoRaccount.getTotal().toDouble()) %>
			</td>
			<% }else{				
		    	
		    	%>		
			<td class="reportCellEven" align="left">&nbsp;<%= bmoPaccount.getCode().toString() %>
			</td>
			<td class="reportCellEven" align="left">&nbsp;<%= bmoPaccount.getInvoiceno().toString() %>
			</td>
			<td class="reportCellEven" align="left">&nbsp;<%= bmoPaccount.getBmoSupplier().getCode().toString() %>
			</td>
			<td class="reportCellEven" align="left">&nbsp;<%= bmoPaccount.getBmoSupplier().getName().toString() %>
			</td>
			<td class="reportCellEven" align="left">&nbsp;<%= bmoPaccount.getDescription().toString() %>
			</td>
			<td class="reportCellEven" align="left">&nbsp;<%= bmoPaccount.getDueDate().toString() %>
			</td>
			<td class="reportCellEven" align="left">&nbsp;<%= bmoPaccount.getPaymentDate().toString() %>
			</td>
			<td class="reportCellEven" align="left">&nbsp;<%= bmoPaccount.getStatus().getSelectedOption().getLabel() %>
			</td>
			<td class="reportCellEven" align="left">&nbsp;<%= bmoPaccount.getPaymentStatus().getSelectedOption().getLabel()%>
			</td>
			<td class="reportCellEven" align="right">&nbsp;<%= formatCurrency.format(bmoPaccount.getTotal().toDouble()) %>
			</td>
			<% }%>
			<% }%>
		</TR>
		<%
				} //listBankMovConcept
           %>
		<tr>
			<td colspan="10">&nbsp;</td>
		</tr>


	</table>


	<% } //if%>
	<table width="100%" align="center" border="0" cellpadding="0"
		cellspacing="0" class="documentTitleCaption">
		<tr align="center">
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>

		<tr align="center">
			<td align="center"><br>
			<p>
					<br> _______________________________________</td>
			<td align="center"><br>
			<p>
					<br> _______________________________________</td>
		</tr>
		<tr>
			<td align="center" class=""><br> Nombre (Qui&eacute;n
				Recibe)</td>
			<td align="center" class=""><br> Firma de Recibido</td>
		</tr>

	</table>


	<p class="documentTitleCaption" align="left">
		Fecha de Impresi&oacute;n
		<%= SFServerUtil.nowToString(sFParams, "dd/MM/yyyy HH:mm") %>
	</p>


	<%  
pmConnEvento.close();
concepts.close();

%>