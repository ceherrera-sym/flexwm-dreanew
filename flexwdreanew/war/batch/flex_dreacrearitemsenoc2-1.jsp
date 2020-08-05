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

<%@page import="com.flexwm.shared.op.BmoRequisitionType"%>
<%@page import="com.flexwm.server.op.PmRequisitionType"%>
<%@page import="com.flexwm.shared.op.BmoRequisition"%>
<%@page import="com.flexwm.server.op.PmRequisition"%>

<%@page import="com.flexwm.shared.op.BmoRequisitionItem"%>
<%@page import="com.flexwm.server.op.PmRequisitionItem"%>
<%@page import="com.flexwm.shared.op.BmoRequisitionReceipt"%>
<%@page import="com.flexwm.server.op.PmRequisitionReceipt"%>
<%@page import="com.flexwm.shared.op.BmoRequisitionReceiptItem"%>
<%@page import="com.flexwm.server.op.PmRequisitionReceiptItem"%>
<%@page import="com.flexwm.shared.fi.BmoPaccount"%>
<%@page import="com.flexwm.server.fi.PmPaccount"%>
<%@page import="com.flexwm.shared.fi.BmoPaccountItem"%>
<%@page import="com.flexwm.server.fi.PmPaccountItem"%>
<%@page import="com.flexwm.shared.fi.BmoPaccountType"%>
<%@page import="com.flexwm.server.fi.PmPaccountType"%>
<%@page import="com.flexwm.shared.fi.BmoPaccountItem"%>
<%@page import="com.flexwm.server.fi.PmPaccountItem"%>
<%@page import="com.flexwm.shared.fi.BmoPaccountType"%>

<%@page import="java.util.Iterator"%>
<%@page import="com.flexwm.shared.BmoFlexConfig"%>
<%@page import="com.symgae.shared.BmFilter"%>
<%@page import="com.symgae.shared.BmObject"%>
<%@page import="com.symgae.server.PmConn"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>


<%@page import="java.util.Iterator"%>
<%@page import="com.symgae.shared.BmFilter"%>
<%@page import="com.symgae.shared.BmObject"%>

<%@include file="../inc/login.jsp" %>



<html>
<head>
	<title>:::<%= appTitle %>:::</title>
	<link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>"> 
</head>

<body class="default">

<table border="0" cellspacing="0" cellpading="0" width="100%">
	<tr>
		<td align="left" width="80" rowspan="2" valign="top">	
			<img src="<%= sFParams.getMainImageUrl() %>">
		</td>
		<td class="reportTitle" align="left">
			&nbsp;
		</td>
	<td class="reportDate" align="right">
			Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
		</td>
	</tr>
	<tr>
		<td colspan="3">
			<%			
			//Insertar item en las OC que no tengan items
			PmConn pmConn = new PmConn(sFParams);
			PmConn pmConn2 = new PmConn(sFParams);
			BmUpdateResult bmUpdateResult = new BmUpdateResult();


			try {
				pmConn.open();
				pmConn.disableAutoCommit();
				pmConn2.open();
				pmConn2.disableAutoCommit();
				
				String sql = " SELECT * FROM requisitions " + 
							" LEFT JOIN requisitionitems on(rqit_requisitionid = reqi_requisitionid) " +
							" LEFT JOIN requisitiontypes on(rqtp_requisitiontypeid = reqi_requisitiontypeid) " +
							" WHERE rqit_requisitionitemid is null " +
							" AND reqi_total > 0 " +
							" AND reqi_deliverystatus = 'T'";
							// AND reqi_requisitionid = 243";

				pmConn.doFetch(sql);
				System.out.println("sql: "+sql);
				int i = 0;
				while(pmConn.next()){
					
					PmRequisition pmRequisition = new PmRequisition(sFParams);
					BmoRequisition bmoRequisition = (BmoRequisition)pmRequisition.get(pmConn.getInt("reqi_requisitionid"));
					
					%>
					<br>
					<%= i + 1%>
					&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;
					<%= pmConn.getString("reqi_code") %> / <%= pmConn.getString("rqtp_name") %>
					&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;
					
					<%
					
					PmRequisitionItem pmRequisitionItem = new PmRequisitionItem(sFParams);
					BmoRequisitionItem bmoRequisitionItemNew = new BmoRequisitionItem();
					
					
					bmoRequisitionItemNew.getRequisitionId().setValue(bmoRequisition.getId());
					bmoRequisitionItemNew.getName().setValue("Creado Autom.");
					bmoRequisitionItemNew.getQuantity().setValue(1);
					bmoRequisitionItemNew.getQuantityReceipt().setValue(1);
					bmoRequisitionItemNew.getPrice().setValue(bmoRequisition.getAmount().toDouble());
					bmoRequisitionItemNew.getAmount().setValue(bmoRequisition.getAmount().toDouble());

					pmRequisitionItem.saveSimple(pmConn2, bmoRequisitionItemNew, bmUpdateResult);
					System.out.println("Crea item oc");
					%> / OC_item_creado<%
					if(pmConn.getString("reqi_status").equals("A")){ //Esta autorizado
						if(pmConn.getString("reqi_deliverystatus").equals("R")){ //Recepcion: En revision
							if(pmConn.getString("reqi_paymentstatus").equals("T")){ //Pago: Total
								if(pmConn.getString("rqtp_type").equals(""+BmoRequisitionType.TYPE_COMMISION)){ // de tipo comision
									
									//Validar que no existe el ROC
									////Se quita validacion por que el metodo exitROC en private
									//if (!pmRequisition.existROC(pmConn2, bmoRequisition, bmUpdateResult)) {
										
										System.out.println("no existe roc");
										PmRequisitionReceipt pmRequisitionReceipt = new PmRequisitionReceipt(sFParams);
										BmoRequisitionReceipt bmoRequisitionReceipt = new BmoRequisitionReceipt();
										
										//Crear el ROC
										System.out.println("antes de crear roc");
										//bmoRequisitionReceipt = pmRequisitionReceipt.createFromRequisition(pmConn, bmoRequisition, bmUpdateResult);
											// Crear el recibo de orden de compra
											bmoRequisitionReceipt.getName().setValue(bmoRequisition.getName().toString());
											bmoRequisitionReceipt.getRequisitionId().setValue(bmoRequisition.getId());
											bmoRequisitionReceipt.getType().setValue(BmoRequisitionReceipt.TYPE_RECEIPT);
											bmoRequisitionReceipt.getPayment().setValue(BmoRequisitionReceipt.PAYMENT_NOTPROVISIONED);
											bmoRequisitionReceipt.getDiscount().setValue(0);			
											bmoRequisitionReceipt.getSupplierId().setValue(bmoRequisition.getSupplierId().toInteger());
											bmoRequisitionReceipt.getRequisitionId().setValue(bmoRequisition.getId());
											bmoRequisitionReceipt.setBmoRequisition(bmoRequisition);
											bmoRequisitionReceipt.getReceiptDate().setValue(bmoRequisition.getDeliveryDate().toString());
											//bmoRequisitionReceipt.getStatus().setValue(""+BmoRequisitionReceipt.STATUS_AUTHORIZED);
											
											pmRequisitionReceipt.saveSimple(pmConn2, bmoRequisitionReceipt, bmUpdateResult);	
											System.out.println("1:se guardo roc");
											
											bmoRequisitionReceipt.setId(bmUpdateResult.getId());
											System.out.println("rocID_UpdateResult: "+bmUpdateResult.getId() +" rocID: " +bmoRequisitionReceipt.getId());
											bmoRequisitionReceipt.getCode().setValue(bmoRequisitionReceipt.getCodeFormat());
											System.out.println("2:antes de guardar roc con clave");
											pmRequisitionReceipt.saveSimple(pmConn2, bmoRequisitionReceipt, bmUpdateResult);
											System.out.println("2:se guardo roc con clave");
											%> / ROC_creado <%= bmoRequisitionReceipt.getId()%> <%
											System.out.println("roc creado");
											
											// Crear los items del recibo
											BmoRequisitionItem bmoRequisitionItem = new BmoRequisitionItem();
											//pmRequisitionReceipt = new PmRequisitionReceipt(sFParams);
											PmRequisitionReceiptItem pmRequisitionReceiptItem = new PmRequisitionReceiptItem(sFParams);

											
											pmRequisition = new PmRequisition(sFParams);
											bmoRequisition = (BmoRequisition)pmRequisition.get(pmConn2, bmoRequisitionReceipt.getRequisitionId().toInteger());
											
											PmRequisitionType pmRequisitionType = new PmRequisitionType(sFParams);
											BmoRequisitionType bmoRequisitionType = (BmoRequisitionType)pmRequisitionType.get(pmConn2, bmoRequisition.getRequisitionTypeId().toInteger());
											
											BmFilter filterRequisitionItem = new BmFilter();
											filterRequisitionItem.setValueFilter(bmoRequisitionItem.getKind(), bmoRequisitionItem.getRequisitionId(), bmoRequisitionReceipt.getRequisitionId().toInteger());
											Iterator<BmObject> listRequisitionItem = new PmRequisitionItem(sFParams).list(pmConn2, filterRequisitionItem).iterator();			

											while (listRequisitionItem.hasNext()) {
												bmoRequisitionItem = (BmoRequisitionItem)listRequisitionItem.next();

													// Crear un nuevo item en el recibo
													BmoRequisitionReceiptItem bmoRequisitionReceiptItemNew = new BmoRequisitionReceiptItem();
													bmoRequisitionReceiptItemNew.getRequisitionReceiptId().setValue(bmoRequisitionReceipt.getId());
													bmoRequisitionReceiptItemNew.getRequisitionItemId().setValue(bmoRequisitionItem.getId());
													
													bmoRequisitionReceiptItemNew.getDays().setValue(bmoRequisitionItem.getDays().toDouble());
													bmoRequisitionReceiptItemNew.getName().setValue(bmoRequisitionItem.getName().toString());				
													bmoRequisitionReceiptItemNew.getPrice().setValue(bmoRequisitionItem.getPrice().toDouble());
													
													bmoRequisitionReceiptItemNew.getQuantity().setValue(bmoRequisitionItem.getQuantity().toDouble());					
													bmoRequisitionReceiptItemNew.getAmount().setValue(bmoRequisitionItem.getAmount().toDouble());
												
													pmRequisitionReceiptItem.saveSimple(pmConn2, bmoRequisitionReceiptItemNew, bmUpdateResult);		
													
											}
											
											
											
											
											// Autorecibir los items del recibo de orden de compra y autorizar el recibo
											//pmRequisitionReceipt.autoReceive(pmConn, bmoRequisitionReceipt, bmUpdateResult);
											
											bmoRequisition = (BmoRequisition)pmRequisition.get(pmConn2, bmoRequisitionReceipt.getRequisitionId().toInteger());
											
											// Actualiza los items
											pmRequisitionItem = new PmRequisitionItem(sFParams);
											bmoRequisitionItem = new BmoRequisitionItem();
											BmoRequisitionReceiptItem bmoRequisitionReceiptItem = new BmoRequisitionReceiptItem();
											
											double totalItems = 0;
											BmFilter filterRequisitionReceipt = new BmFilter();
											filterRequisitionReceipt.setValueFilter(bmoRequisitionReceiptItem.getKind(), bmoRequisitionReceiptItem.getRequisitionReceiptId(), bmoRequisitionReceipt.getId());
											pmRequisitionReceiptItem = new PmRequisitionReceiptItem(sFParams); 
											Iterator<BmObject> listRequisitionReceiptItem = pmRequisitionReceiptItem.list(pmConn2, filterRequisitionReceipt).iterator();			
											System.out.println("antes de while de autorecibir ROC-ITEMS");
											while (listRequisitionReceiptItem.hasNext()) {
												bmoRequisitionReceiptItem = (BmoRequisitionReceiptItem)listRequisitionReceiptItem.next();
												System.out.println("entra a while de autorecibir ROC-ITEMS");
												// Obtiene la cantidad total
												bmoRequisitionItem = (BmoRequisitionItem)pmRequisitionItem.get(pmConn2, bmoRequisitionReceiptItem.getRequisitionItemId().toInteger());
												
												// Asigna la cantidad y la almacena			
												bmoRequisitionReceiptItem.getQuantity().setValue(bmoRequisitionItem.getQuantity().toDouble());
												
												// Actualiza estatus de saldo a todos los items relacionados
												pmRequisitionReceiptItem.updateQuantityBalance(pmConn2, bmoRequisitionReceipt, bmoRequisitionReceiptItem, bmUpdateResult);

												// Actualiza estatus de devoluciones a todos los items relacionados
												pmRequisitionReceiptItem.updateQuantityReturned(pmConn2, bmoRequisitionReceipt, bmoRequisitionReceiptItem, bmUpdateResult);

												System.out.println("antes de guardar ROC-ITEMS");
												pmRequisitionReceiptItem.saveSimple(pmConn2, bmoRequisitionReceiptItem, bmUpdateResult);
												System.out.println("despues de guardar ROC-ITEMS");

											}
											
											// Autorizar recibo de orden de compra
											bmoRequisitionReceipt.getStatus().setValue(BmoRequisitionReceipt.STATUS_AUTHORIZED);
											pmRequisitionReceipt.saveSimple(pmConn2, bmoRequisitionReceipt, bmUpdateResult);
											
											//-----------------Actualiza Balance del ROC--------------------
											// Actualiza el estatus de la orden de compra
											bmoRequisition = (BmoRequisition)pmRequisition.get(bmoRequisitionReceipt.getRequisitionId().toInteger());
											//pmRequisition.updateDeliveryStatus(pmConn, bmoRequisition, bmUpdateResult);
												double rQuantity = 0;
												double dQuantity = 0;
	
												// Obten cantidad de items de la requisición, de productos tipo producto y no servicio
												sql = "SELECT SUM(rqit_quantity) AS rqitQ, rqit_requisitionitemid FROM requisitionitems "
														+ " LEFT JOIN products on (rqit_productid = prod_productid) "	
														+ " WHERE rqit_requisitionid = " + bmoRequisition.getId();
												pmConn2.doFetch(sql);
												System.out.println("sql rQuantity: "+sql);
												if (pmConn2.next()){ 
													rQuantity = pmConn2.getDouble("rqitQ");
													System.out.println("rQuantity: "+rQuantity + " -rqit_requisitionitemid: "+pmConn2.getString("rqit_requisitionitemid"));
												}
	
												// Obten cantidad de items en almacén
												sql = "SELECT SUM(reit_quantity) AS reitQ, reit_requisitionreceiptitemid FROM requisitionreceiptitems "
														+ " LEFT JOIN requisitionreceipts ON (reit_requisitionreceiptid = rerc_requisitionreceiptid) "
														+ " WHERE rerc_requisitionid = " + bmoRequisition.getId();
												pmConn2.doFetch(sql);
												System.out.println("sql dQuantity: "+sql);

												if (pmConn2.next()){
													dQuantity = pmConn2.getDouble("reitQ");
													System.out.println("dQuantity: "+dQuantity + " -reit_requisitionreceiptitemid: "+pmConn2.getString("reit_requisitionreceiptitemid"));
												}
	
												// Revisar cantidades
												if (dQuantity == 0) {
													System.out.println("dQuantity == 0");
													bmoRequisition.getDeliveryStatus().setValue(BmoRequisition.DELIVERYSTATUS_REVISION);
												} else if (dQuantity >= rQuantity) {
													bmoRequisition.getDeliveryStatus().setValue(BmoRequisition.DELIVERYSTATUS_TOTAL);
												} else {
													bmoRequisition.getDeliveryStatus().setValue(BmoRequisition.DELIVERYSTATUS_PARTIAL);
												}
	
												// Actualizar status
												pmRequisition.saveSimple(pmConn2, bmoRequisition, bmUpdateResult);
												
												
											// Actualiza montos
											double amount = 0;
											sql = " SELECT sum(reit_amount) FROM requisitionreceiptitems " +
													" WHERE reit_requisitionreceiptid = " + bmoRequisitionReceipt.getId() + 
													" AND reit_quantity > 0 ";		
											pmConn2.doFetch(sql);
											if (pmConn2.next()) amount = pmConn2.getDouble(1);
											
											System.out.println("Actualiza montos: "+amount);
											// Calcular montos
											if (amount == 0) {			
												bmoRequisitionReceipt.getAmount().setValue(0);
												bmoRequisitionReceipt.getTax().setValue(0);			
												bmoRequisitionReceipt.getTotal().setValue(0);	
												System.out.println("IF CERO Actualiza montos: ");
											} else {
												System.out.println("ELSE Actualiza montos: ");
												bmoRequisitionReceipt.getAmount().setValue(amount);
												//Obtener el Iva
												if (bmoRequisition.getTaxApplies().toBoolean()) {
													double taxRate = ((BmoFlexConfig)sFParams.getBmoAppConfig()).getTax().toDouble() / 100;
													bmoRequisitionReceipt.getTax().setValue(SFServerUtil.roundCurrencyDecimals(amount * taxRate));
													bmoRequisitionReceipt.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(amount +  bmoRequisitionReceipt.getTax().toDouble()));
												} else {
													bmoRequisitionReceipt.getTax().setValue(0);
													bmoRequisitionReceipt.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(amount));
												}	
											}

											bmoRequisitionReceipt.getStatus().setValue(BmoRequisitionReceipt.STATUS_AUTHORIZED);
											pmRequisitionReceipt.saveSimple(pmConn2, bmoRequisitionReceipt, bmUpdateResult);	
											%> / ROC-ITEMS_creados<%

										//----------------

										// **********************Crear la CXP*************************
										PmPaccount pmPaccount = new PmPaccount(sFParams);
										BmoPaccount bmoPaccount = new BmoPaccount();
										PmPaccountType pmPaccountType = new PmPaccountType(sFParams);
	
										
										bmoPaccount.getPaccountTypeId().setValue(pmPaccountType.getRequisitionPaccountType(pmConn2));
										bmoPaccount.getInvoiceno().setValue("S/F");
										bmoPaccount.getStatus().setValue("" + BmoPaccount.STATUS_AUTHORIZED);
										bmoPaccount.getReceiveDate().setValue(bmoRequisitionReceipt.getBmoRequisition().getRequestDate().toString());
										bmoPaccount.getDueDate().setValue(bmoRequisitionReceipt.getBmoRequisition().getDeliveryDate().toString());
										//bmoPaccount.getPaymentDate().setValue(bmoRequisitionReceipt.getBmoRequisition().getPaymentDate().toString());
										bmoPaccount.getDescription().setValue("Creado Autom.");
										bmoPaccount.getSupplierId().setValue(bmoRequisitionReceipt.getSupplierId().toInteger());
										bmoPaccount.getCompanyId().setValue(bmoRequisitionReceipt.getBmoRequisition().getCompanyId().toInteger());
	
										bmoPaccount.getRequisitionReceiptId().setValue(bmoRequisitionReceipt.getId());
										bmoPaccount.getRequisitionId().setValue(bmoRequisitionReceipt.getRequisitionId().toInteger());
										System.out.println("1:antes de guardar cxp");
	
										pmPaccount.saveSimple(pmConn2, bmoPaccount, bmUpdateResult);
										System.out.println("1:se guardo cxp");
										
										bmoPaccount.setId(bmUpdateResult.getId());
										System.out.println("paccID_UpdateResult: "+bmUpdateResult.getId() +" paccID: " +bmoPaccount.getId());
										bmoPaccount.getCode().setValue(bmoPaccount.getCodeFormat());
										System.out.println("2:antes de guardar cxp con clave");
										pmPaccount.saveSimple(pmConn2, bmoPaccount, bmUpdateResult);
										System.out.println("2:se guardo cxp con clave");
										%> / Pacc_creado <%= bmoPaccount.getId()%> <%
										
										
										PmPaccountItem pmPaccountItem = new PmPaccountItem(sFParams);
										//pmPaccountItem.createItemsFromRequisitionReceipt(pmConn, bmoPaccount, bmUpdateResult);
										// Validar que no existan items creados
										if (!pmPaccountItem.itemsFromRequisitionReceiptExist(pmConn2, bmoPaccount.getId(), bmUpdateResult)) {
											// Crear los items
											bmoRequisitionReceiptItem = new BmoRequisitionReceiptItem();
											BmFilter filterRequisitionReceiptItem = new BmFilter();

											pmRequisitionReceipt = new PmRequisitionReceipt(sFParams);
											bmoRequisitionReceipt = (BmoRequisitionReceipt)pmRequisitionReceipt.get(pmConn2, bmoPaccount.getRequisitionReceiptId().toInteger());

											double taxRate = ((BmoFlexConfig)sFParams.getBmoAppConfig()).getTax().toDouble() / 100;
											double tax = 0;

											filterRequisitionReceiptItem.setValueFilter(bmoRequisitionReceiptItem.getKind(), bmoRequisitionReceiptItem.getRequisitionReceiptId(), bmoPaccount.getRequisitionReceiptId().toInteger());
											Iterator<BmObject> listRequisitionReceiptItem2 = new PmRequisitionReceiptItem(sFParams).list(pmConn2, filterRequisitionReceiptItem).iterator();			

											while (listRequisitionReceiptItem2.hasNext()) {
												bmoRequisitionReceiptItem = (BmoRequisitionReceiptItem)listRequisitionReceiptItem2.next();

												//Crear un nuevo item en CXP			
												BmoPaccountItem bmoPaccountItemNew = new BmoPaccountItem();
												bmoPaccountItemNew.getPaccountId().setValue(bmoPaccount.getId());
												bmoPaccountItemNew.getRequisitionReceiptItemId().setValue(bmoRequisitionReceiptItem.getId());
												bmoPaccountItemNew.getQuantity().setValue(bmoRequisitionReceiptItem.getQuantity().toDouble());
												bmoPaccountItemNew.getName().setValue(bmoRequisitionReceiptItem.getName().toString());
												bmoPaccountItemNew.getPrice().setValue(bmoRequisitionReceiptItem.getPrice().toDouble());

												//Aplicar el iva a la cantidad del item
												if (bmoRequisitionReceipt.getTax().toDouble() > 0) {
													tax = bmoRequisitionReceiptItem.getAmount().toDouble() * taxRate;
													bmoPaccountItemNew.getAmount().setValue(bmoRequisitionReceiptItem.getAmount().toDouble() + tax);
												} else {
													bmoPaccountItemNew.getAmount().setValue(bmoRequisitionReceiptItem.getAmount().toDouble());
												}

												pmPaccountItem.saveSimple(pmConn2, bmoPaccountItemNew, bmUpdateResult);
											}
										}	
										
										
										System.out.println("se crearon los items de la cxp");
										%> / Pait_creado<%
										
										
										//***************Actualizar balance y estatus de pago*************************
										//pmPaccount.updateBalance(pmConn, bmoPaccount, bmUpdateResult);	
										pmPaccountType = new PmPaccountType(sFParams);
										BmoPaccountType bmoPaccountType = (BmoPaccountType)pmPaccountType.get(pmConn2, bmoPaccount.getPaccountTypeId().toInteger());

										// Validar tipo de CxP
										if (bmoPaccountType.getType().equals(BmoPaccountType.TYPE_WITHDRAW)) {			
											
											//Sumar los items de la cxp si es de tipo otros					 
											bmoPaccount.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(pmPaccount.sumPaccountItems(pmConn2, bmoPaccount, bmUpdateResult)));
												
											// Se actualiza estatus de pago
											//pmPaccount.updateWithdrawBalance(pmConn, bmoPaccount, bmUpdateResult);
												//Obtener los pagos ligados al cargo
												double totalPayments = 0;
	
												sql = " SELECT SUM(pass_amount) as totalpayments FROM paccountassignments " +		              
												             " WHERE pass_foreignpaccountid = " + bmoPaccount.getId();		             
												pmConn2.doFetch(sql);
												if (pmConn2.next()) {
													totalPayments = pmConn2.getDouble("totalpayments");
												}
												
												// Asigna estatus
												if (totalPayments > 0) {			
													if (bmoPaccount.getAmount().toDouble() <= totalPayments)
														bmoPaccount.getPaymentStatus().setValue(BmoPaccount.PAYMENTSTATUS_TOTAL);
													else
														bmoPaccount.getPaymentStatus().setValue(BmoPaccount.PAYMENTSTATUS_PENDING);
												} 
	
												// Calcular los saldos
												if (totalPayments > bmoPaccount.getAmount().toDouble()) {
													bmoPaccount.getBalance().setValue(0);
													bmoPaccount.getPayments().setValue(SFServerUtil.roundCurrencyDecimals(bmoPaccount.getAmount().toDouble()));
												} else {	
													bmoPaccount.getBalance().setValue(SFServerUtil.roundCurrencyDecimals(bmoPaccount.getAmount().toDouble() - totalPayments));
													bmoPaccount.getPayments().setValue(SFServerUtil.roundCurrencyDecimals(totalPayments));
												}	
	
												// Almacenar el cambio de estatus
												pmPaccount.saveSimple(pmConn2, bmoPaccount, bmUpdateResult);
											

											// Actualiza Recibos de Ordenes de Compra si existia liga
											if (bmoPaccount.getRequisitionReceiptId().toInteger() > 0) {
												pmRequisitionReceipt = new PmRequisitionReceipt(sFParams);
												bmoRequisitionReceipt = (BmoRequisitionReceipt)pmRequisitionReceipt.get(pmConn2, bmoPaccount.getRequisitionReceiptId().toInteger());
												//pmRequisitionReceipt.updatePaymentStatus(pmConn, bmoRequisitionReceipt, bmUpdateResult);
												
												sql = " SELECT pacc_paccountid FROM paccounts " +
														" WHERE pacc_requisitionreceiptid = " + bmoRequisitionReceipt.getId();		
												pmConn2.doFetch(sql);
												if (pmConn2.next()) {
													bmoRequisitionReceipt.getPayment().setValue(BmoRequisitionReceipt.PAYMENT_PROVISIONED);
												} else {
													bmoRequisitionReceipt.getPayment().setValue(BmoRequisitionReceipt.PAYMENT_NOTPROVISIONED);
												}
												pmRequisitionReceipt.saveSimple(pmConn2, bmoRequisitionReceipt, bmUpdateResult);
												
												// Actualiza el estatus de la orden de compra
												pmRequisition = new PmRequisition(sFParams);
												bmoRequisition = (BmoRequisition)pmRequisition.get(pmConn2, bmoRequisitionReceipt.getRequisitionId().toInteger());
												//pmRequisition.updatePaymentStatus(pmConn, bmoRequisition, bmUpdateResult);
											}
											
										} else {
											
											//Sumar los items de la cxp si es de tipo otros
											bmoPaccount.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(pmPaccount.sumPaccountItems(pmConn2, bmoPaccount, bmUpdateResult)));
											//pmPaccount.updateDepositBalance(pmConn, bmoPaccount, bmUpdateResult);
											
											//Obtener los pagos ligados al cargo
											double totalPayments = 0;

											//Obtener el total de la aplicaciones		
											sql = " SELECT SUM(pass_amount) as totalpayments FROM paccountassignments " +		              
											             " WHERE pass_paccountid = " + bmoPaccount.getId();
											             
											pmConn.doFetch(sql);
											if (pmConn.next()) {
												totalPayments = pmConn.getDouble("totalpayments");
											}		
													
											// Asigna estatus
											if (totalPayments > 0) {			
												if (bmoPaccount.getAmount().toDouble() <= totalPayments)
													bmoPaccount.getPaymentStatus().setValue(BmoPaccount.PAYMENTSTATUS_TOTAL);
												else
													bmoPaccount.getPaymentStatus().setValue(BmoPaccount.PAYMENTSTATUS_PENDING);
											}

											// Calcular los saldos		
											bmoPaccount.getBalance().setValue(SFServerUtil.roundCurrencyDecimals(bmoPaccount.getAmount().toDouble() - totalPayments));
											bmoPaccount.getPayments().setValue(SFServerUtil.roundCurrencyDecimals(totalPayments));

											// Almacenar el cambio de estatus
											pmPaccount.saveSimple(pmConn2, bmoPaccount, bmUpdateResult);

										}
										
										pmPaccount.saveSimple(pmConn2, bmoPaccount, bmUpdateResult);
	
										
									//} //Fin de -Validar que no existe el ROC
									
								} // Fin de -tipo comision
							} //Fin de -Pago: Total
							
						}//Fin de -Recepcion: En revision
						else if(pmConn.getString("reqi_deliverystatus").equals("T")){ // Recepcion: Total
							if(pmConn.getString("reqi_paymentstatus").equals("T")){ //Pago: Total
								if(pmConn.getString("rqtp_type").equals(""+BmoRequisitionType.TYPE_COMMISION)){ // de tipo comision
									System.out.println("RECEPCION TOTAL");

									//Si existe el ROC traerlo
									//Se quita validacion por que el metodo exitROC en private
									//if (pmRequisition.existROC(pmConn2, bmoRequisition, bmUpdateResult)) {
										PmRequisitionReceipt pmRequisitionReceipt = new PmRequisitionReceipt(sFParams);
										BmoRequisitionReceipt bmoRequisitionReceipt = new BmoRequisitionReceipt();
										//obtener el recibo
										bmoRequisitionReceipt = (BmoRequisitionReceipt)pmRequisitionReceipt.getBy(pmConn2, bmoRequisition.getId(), bmoRequisitionReceipt.getRequisitionId().getName());
										System.out.println("estatus del roc: "+ bmoRequisitionReceipt.getStatus().toString());
										
										//Si el ROC tiene items
										PmRequisitionReceiptItem pmRequisitionReceiptItem = new PmRequisitionReceiptItem(sFParams);
										BmoRequisitionReceiptItem bmoRequisitionReceiptItem = new BmoRequisitionReceiptItem();
										int countItems = 0;
										String items = "SELECT COUNT(reit_requisitionreceiptitemid) as countItems FROM requisitionreceiptitems " + 
														" LEFT JOIN requisitionreceipts ON(rerc_requisitionreceiptid = reit_requisitionreceiptid)" +
														" WHERE rerc_requisitionreceiptid = " + bmoRequisitionReceipt.getId();
										
										pmConn2.doFetch(items);
										if(pmConn2.next()) countItems = pmConn2.getInt("countItems");
										
										//Si el ROC no tiene items, crear el item con el item de la oc
										if(countItems == 0){
											System.out.println("crear items en el roc");

											BmoRequisitionItem bmoRequisitionItem = new BmoRequisitionItem();

											BmFilter filterRequisitionItem = new BmFilter();
											filterRequisitionItem.setValueFilter(bmoRequisitionItem.getKind(), bmoRequisitionItem.getRequisitionId(), bmoRequisitionReceipt.getRequisitionId().toInteger());
											Iterator<BmObject> listRequisitionItem = new PmRequisitionItem(sFParams).list(pmConn2, filterRequisitionItem).iterator();			

											while (listRequisitionItem.hasNext()) {
												System.out.println("crear items en el roc, dentro de while: rocID: "+bmoRequisitionReceipt.getId());
												bmoRequisitionItem = (BmoRequisitionItem)listRequisitionItem.next();
													
												// Crear un nuevo item en el recibo
												BmoRequisitionReceiptItem bmoRequisitionReceiptItemNew = new BmoRequisitionReceiptItem();
												bmoRequisitionReceiptItemNew.getRequisitionReceiptId().setValue(bmoRequisitionReceipt.getId());
												bmoRequisitionReceiptItemNew.getRequisitionItemId().setValue(bmoRequisitionItem.getId());
												
												bmoRequisitionReceiptItemNew.getDays().setValue(bmoRequisitionItem.getDays().toDouble());
												bmoRequisitionReceiptItemNew.getName().setValue(bmoRequisitionItem.getName().toString());				
												bmoRequisitionReceiptItemNew.getPrice().setValue(bmoRequisitionItem.getPrice().toDouble());
												bmoRequisitionReceiptItemNew.getQuantity().setValue(bmoRequisitionItem.getQuantity().toDouble());					
												bmoRequisitionReceiptItemNew.getAmount().setValue(bmoRequisitionItem.getAmount().toDouble());
												//pmRequisitionReceiptItem.createItemsFromRequisition(pmConn, bmoRequisitionReceipt, bmUpdateResult);
											
												// Actualiza estatus de saldo a todos los items relacionados
												//pmRequisitionReceiptItem.updateQuantityBalance(pmConn2, bmoRequisitionReceipt, bmoRequisitionReceiptItemNew, bmUpdateResult);
	
												// Actualiza estatus de devoluciones a todos los items relacionados
												//pmRequisitionReceiptItem.updateQuantityReturned(pmConn2, bmoRequisitionReceipt, bmoRequisitionReceiptItemNew, bmUpdateResult);
	
												pmRequisitionReceiptItem.saveSimple(pmConn2, bmoRequisitionReceiptItemNew, bmUpdateResult);
											}
											
											
											// Actualiza los items
											pmRequisitionItem = new PmRequisitionItem(sFParams);
											bmoRequisitionItem = new BmoRequisitionItem();
											bmoRequisitionReceiptItem = new BmoRequisitionReceiptItem();
											
											double totalItems = 0;
											BmFilter filterRequisitionReceipt = new BmFilter();
											filterRequisitionReceipt.setValueFilter(bmoRequisitionReceiptItem.getKind(), bmoRequisitionReceiptItem.getRequisitionReceiptId(), bmoRequisitionReceipt.getId());
											pmRequisitionReceiptItem = new PmRequisitionReceiptItem(sFParams); 
											Iterator<BmObject> listRequisitionReceiptItem = pmRequisitionReceiptItem.list(pmConn2, filterRequisitionReceipt).iterator();			
											System.out.println("RECEP_TOTAL: antes de while de autorecibir ROC-ITEMS");
											while (listRequisitionReceiptItem.hasNext()) {
												bmoRequisitionReceiptItem = (BmoRequisitionReceiptItem)listRequisitionReceiptItem.next();
												System.out.println("RECEP_TOTAL: entra a while de autorecibir ROC-ITEMS");
												// Obtiene la cantidad total
												bmoRequisitionItem = (BmoRequisitionItem)pmRequisitionItem.get(pmConn2, bmoRequisitionReceiptItem.getRequisitionItemId().toInteger());
												
												// Asigna la cantidad y la almacena			
												bmoRequisitionReceiptItem.getQuantity().setValue(bmoRequisitionItem.getQuantity().toDouble());
												
												// Actualiza estatus de saldo a todos los items relacionados
												pmRequisitionReceiptItem.updateQuantityBalance(pmConn2, bmoRequisitionReceipt, bmoRequisitionReceiptItem, bmUpdateResult);

												// Actualiza estatus de devoluciones a todos los items relacionados
												pmRequisitionReceiptItem.updateQuantityReturned(pmConn2, bmoRequisitionReceipt, bmoRequisitionReceiptItem, bmUpdateResult);

												System.out.println("RECEP_TOTAL: antes de guardar ROC-ITEMS");
												pmRequisitionReceiptItem.saveSimple(pmConn2, bmoRequisitionReceiptItem, bmUpdateResult);
												System.out.println("RECEP:TOTAL: despues de guardar ROC-ITEMS");

											}
											%> / ITEMS-ROC_creados<%
										}else{ // Actualizar items
											// Autorecibir los items del recibo de orden de compra y autorizar el recibo
											
											bmoRequisition = (BmoRequisition)pmRequisition.get(pmConn2, bmoRequisitionReceipt.getRequisitionId().toInteger());
											
											// Actualiza los items
											pmRequisitionItem = new PmRequisitionItem(sFParams);
											BmoRequisitionItem bmoRequisitionItem = new BmoRequisitionItem();
											bmoRequisitionReceiptItem = new BmoRequisitionReceiptItem();
											
											double totalItems = 0;
											BmFilter filterRequisitionReceipt = new BmFilter();
											filterRequisitionReceipt.setValueFilter(bmoRequisitionReceiptItem.getKind(), bmoRequisitionReceiptItem.getRequisitionReceiptId(), bmoRequisitionReceipt.getId());
											pmRequisitionReceiptItem = new PmRequisitionReceiptItem(sFParams); 
											Iterator<BmObject> listRequisitionReceiptItem = pmRequisitionReceiptItem.list(pmConn2, filterRequisitionReceipt).iterator();			
											System.out.println("2antes de while de autorecibir ROC-ITEMS");
											while (listRequisitionReceiptItem.hasNext()) {
												bmoRequisitionReceiptItem = (BmoRequisitionReceiptItem)listRequisitionReceiptItem.next();
												System.out.println("2entra a while de autorecibir ROC-ITEMS " + bmoRequisitionReceiptItem.getId() + " / : "+bmoRequisitionReceiptItem.getRequisitionItemId().toString());
												
												if(!(bmoRequisitionReceiptItem.getRequisitionItemId().toInteger() > 0)){
													//Asigna el id del item de OC al item del ROC
													bmoRequisitionReceiptItem.getRequisitionItemId().setValue(bmoRequisitionItemNew.getId());
													pmRequisitionReceiptItem.saveSimple(pmConn2, bmoRequisitionReceiptItem, bmUpdateResult);
												}
												
												// Obtiene la cantidad total
												bmoRequisitionItem = (BmoRequisitionItem)pmRequisitionItem.get(pmConn2, bmoRequisitionReceiptItem.getRequisitionItemId().toInteger());
												
												// Asigna la cantidad y la almacena			
												bmoRequisitionReceiptItem.getQuantity().setValue(bmoRequisitionItem.getQuantity().toDouble());
												
												// Actualiza estatus de saldo a todos los items relacionados
												pmRequisitionReceiptItem.updateQuantityBalance(pmConn2, bmoRequisitionReceipt, bmoRequisitionReceiptItem, bmUpdateResult);

												// Actualiza estatus de devoluciones a todos los items relacionados
												pmRequisitionReceiptItem.updateQuantityReturned(pmConn2, bmoRequisitionReceipt, bmoRequisitionReceiptItem, bmUpdateResult);

												System.out.println("antes de guardar ROC-ITEMS");
												pmRequisitionReceiptItem.saveSimple(pmConn2, bmoRequisitionReceiptItem, bmUpdateResult);
												System.out.println("despues de guardar ROC-ITEMS");

											}
											
											// Autorizar recibo de orden de compra
											bmoRequisitionReceipt.getStatus().setValue(BmoRequisitionReceipt.STATUS_AUTHORIZED);
											pmRequisitionReceipt.saveSimple(pmConn2, bmoRequisitionReceipt, bmUpdateResult);
											
											//-----------------Actualiza Balance del ROC--------------------
											// Actualiza el estatus de la orden de compra
											bmoRequisition = (BmoRequisition)pmRequisition.get(bmoRequisitionReceipt.getRequisitionId().toInteger());
											//pmRequisition.updateDeliveryStatus(pmConn, bmoRequisition, bmUpdateResult);
												double rQuantity = 0;
												double dQuantity = 0;
	
												// Obten cantidad de items de la requisición, de productos tipo producto y no servicio
												sql = "SELECT SUM(rqit_quantity) AS rqitQ, rqit_requisitionitemid FROM requisitionitems "
														+ " LEFT JOIN products on (rqit_productid = prod_productid) "	
														+ " WHERE rqit_requisitionid = " + bmoRequisition.getId();
												pmConn2.doFetch(sql);
												System.out.println("sql rQuantity: "+sql);
												if (pmConn2.next()){ 
													rQuantity = pmConn2.getDouble("rqitQ");
													System.out.println("rQuantity: "+rQuantity + " -rqit_requisitionitemid: "+pmConn2.getString("rqit_requisitionitemid"));
												}
	
												// Obten cantidad de items en almacén
												sql = "SELECT SUM(reit_quantity) AS reitQ, reit_requisitionreceiptitemid FROM requisitionreceiptitems "
														+ " LEFT JOIN requisitionreceipts ON (reit_requisitionreceiptid = rerc_requisitionreceiptid) "
														+ " WHERE rerc_requisitionid = " + bmoRequisition.getId();
												pmConn2.doFetch(sql);
												System.out.println("sql dQuantity: "+sql);

												if (pmConn2.next()){
													dQuantity = pmConn2.getDouble("reitQ");
													System.out.println("dQuantity: "+dQuantity + " -reit_requisitionreceiptitemid: "+pmConn2.getString("reit_requisitionreceiptitemid"));
												}
	
												// Revisar cantidades
												if (dQuantity == 0) {
													System.out.println("dQuantity == 0");
													bmoRequisition.getDeliveryStatus().setValue(BmoRequisition.DELIVERYSTATUS_REVISION);
												} else if (dQuantity >= rQuantity) {
													bmoRequisition.getDeliveryStatus().setValue(BmoRequisition.DELIVERYSTATUS_TOTAL);
												} else {
													bmoRequisition.getDeliveryStatus().setValue(BmoRequisition.DELIVERYSTATUS_PARTIAL);
												}
	
												// Actualizar status
												pmRequisition.saveSimple(pmConn2, bmoRequisition, bmUpdateResult);
												
												
											// Actualiza montos
											double amount = 0;
											sql = " SELECT sum(reit_amount) FROM requisitionreceiptitems " +
													" WHERE reit_requisitionreceiptid = " + bmoRequisitionReceipt.getId() + 
													" AND reit_quantity > 0 ";		
											pmConn2.doFetch(sql);
											if (pmConn2.next()) amount = pmConn2.getDouble(1);
											
											System.out.println("Actualiza montos: "+amount);
											// Calcular montos
											if (amount == 0) {			
												bmoRequisitionReceipt.getAmount().setValue(0);
												bmoRequisitionReceipt.getTax().setValue(0);			
												bmoRequisitionReceipt.getTotal().setValue(0);	
												System.out.println("IF CERO Actualiza montos: ");
											} else {
												System.out.println("ELSE Actualiza montos: ");
												bmoRequisitionReceipt.getAmount().setValue(amount);
												//Obtener el Iva
												if (bmoRequisition.getTaxApplies().toBoolean()) {
													double taxRate = ((BmoFlexConfig)sFParams.getBmoAppConfig()).getTax().toDouble() / 100;
													bmoRequisitionReceipt.getTax().setValue(SFServerUtil.roundCurrencyDecimals(amount * taxRate));
													bmoRequisitionReceipt.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(amount +  bmoRequisitionReceipt.getTax().toDouble()));
												} else {
													bmoRequisitionReceipt.getTax().setValue(0);
													bmoRequisitionReceipt.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(amount));
												}	
											}

											bmoRequisitionReceipt.getStatus().setValue(BmoRequisitionReceipt.STATUS_AUTHORIZED);
											pmRequisitionReceipt.saveSimple(pmConn2, bmoRequisitionReceipt, bmUpdateResult);	
											%> / ROC-ITEMS_actualizados<%
										}
										
										//Actualizar esatus del ROC si no está autorizado
										if(bmoRequisitionReceipt.getStatus().toString().equals(""+BmoRequisitionReceipt.STATUS_REVISION)){
											System.out.println("Estaba en revision");
											bmoRequisitionReceipt.getStatus().setValue(""+BmoRequisitionReceipt.STATUS_AUTHORIZED);
											pmRequisitionReceipt.saveSimple(pmConn2, bmoRequisitionReceipt, bmUpdateResult);
											%> / ROC_actualizado<%
										}
										
										PmPaccount pmPaccount = new PmPaccount(sFParams);
										BmoPaccount bmoPaccount = new BmoPaccount();
										PmPaccountType pmPaccountType = new PmPaccountType(sFParams);
										PmPaccountItem pmPaccountItem = new PmPaccountItem(sFParams);
										
										//Si no tiene cxp, crearla
										//if(!pmRequisition.existPacc(pmConn, bmoRequisition, bmUpdateResult)){
										
										sql = " SELECT COUNT(pacc_paccountid)AS paccs, pacc_paccountid FROM paccounts  " +
											    " LEFT JOIN paccounttypes ON(pact_paccounttypeid = pacc_paccounttypeid) " +  
												" WHERE pacc_requisitionreceiptid = " + bmoRequisitionReceipt.getId() +
											    " AND pact_type = '" + BmoPaccountType.TYPE_WITHDRAW + "'";
										System.out.println("sqlPACCS: "+ sql);
										pmConn2.doFetch(sql);
										boolean existePacc = false;
										if (pmConn2.next()){
												System.out.println("traer pacc: " + pmConn2.getInt("pacc_paccountid") +"//"+pmConn2.getInt("pacc_paccountid"));
												if(pmConn2.getInt("pacc_paccountid") > 0){
													existePacc = true;
													bmoPaccount = (BmoPaccount)pmPaccount.get(pmConn2.getInt("pacc_paccountid"));
												}
												System.out.println("despues de traer pacc: " + bmoPaccount.getId() + "-"+bmoPaccount.getCode().toString());
										}
										
										if(!existePacc){
											// Crear la CXP si no existe
											
											bmoPaccount.getPaccountTypeId().setValue(pmPaccountType.getRequisitionPaccountType(pmConn2));
											bmoPaccount.getInvoiceno().setValue("S/F");
											bmoPaccount.getStatus().setValue("" + BmoPaccount.STATUS_AUTHORIZED);
											bmoPaccount.getReceiveDate().setValue(bmoRequisition.getRequestDate().toString());
											bmoPaccount.getDueDate().setValue(bmoRequisition.getDeliveryDate().toString());
											//bmoPaccount.getPaymentDate().setValue(bmoRequisition.getPaymentDate().toString());
											bmoPaccount.getDescription().setValue("Creado Autom.");
											bmoPaccount.getSupplierId().setValue(bmoRequisition.getSupplierId().toInteger());
											bmoPaccount.getCompanyId().setValue(bmoRequisition.getCompanyId().toInteger());
				
											bmoPaccount.getRequisitionReceiptId().setValue(bmoRequisitionReceipt.getId());
											bmoPaccount.getRequisitionId().setValue(bmoRequisitionReceipt.getRequisitionId().toInteger());
											System.out.println("antes de guardar cxp");
				
											pmPaccount.saveSimple(pmConn2, bmoPaccount, bmUpdateResult);
											System.out.println("se guardo cxp");
											bmoPaccount.setId(bmUpdateResult.getId());
											System.out.println("paccID_UpdateResult: "+bmUpdateResult.getId() +" paccID: " +bmoPaccount.getId());
											bmoPaccount.getCode().setValue(bmoPaccount.getCodeFormat());
											System.out.println("RECEP_TOTAL:antes de guardar cxp con clave");
											pmPaccount.saveSimple(pmConn2, bmoPaccount, bmUpdateResult);
											System.out.println("RECEP_TOTAL:se guardo cxp con clave");
											%> / Pacc_creado <%= bmoPaccount.getId()%><%
				
											pmPaccountItem.createItemsFromRequisitionReceipt(pmConn2, bmoPaccount, bmUpdateResult);
											System.out.println("se crearon los items de la cxp");
											
											// Actualizar balance y estatus de pago
											pmPaccount.updateBalance(pmConn2, bmoPaccount, bmUpdateResult);	
											System.out.println("se actualizo balance de pacc");
											
										}// Fin de -existPacc
										else{ //Si existe CXP
											System.out.println("Si existe la cxp");
											//Comprobar que tenga items la cxp, si no tiene, los crea a partir del recibo
											if(bmoPaccount.getId() > 0){
												//pmPaccountItem.createItemsFromRequisitionReceipt(pmConn2, bmoPaccount, bmUpdateResult);
												// Validar que no existan items creados
												if (!pmPaccountItem.itemsFromRequisitionReceiptExist(pmConn2, bmoPaccount.getId(), bmUpdateResult)) {
													// Crear los items
													bmoRequisitionReceiptItem = new BmoRequisitionReceiptItem();
													BmFilter filterRequisitionReceiptItem = new BmFilter();

													pmRequisitionReceipt = new PmRequisitionReceipt(sFParams);
													bmoRequisitionReceipt = (BmoRequisitionReceipt)pmRequisitionReceipt.get(bmoPaccount.getRequisitionReceiptId().toInteger());

													double taxRate = ((BmoFlexConfig)sFParams.getBmoAppConfig()).getTax().toDouble() / 100;
													double tax = 0;

													filterRequisitionReceiptItem.setValueFilter(bmoRequisitionReceiptItem.getKind(), bmoRequisitionReceiptItem.getRequisitionReceiptId(), bmoPaccount.getRequisitionReceiptId().toInteger());
													Iterator<BmObject> listRequisitionReceiptItem = new PmRequisitionReceiptItem(sFParams).list(pmConn2, filterRequisitionReceiptItem).iterator();			

													while (listRequisitionReceiptItem.hasNext()) {
														bmoRequisitionReceiptItem = (BmoRequisitionReceiptItem)listRequisitionReceiptItem.next();

														//Crear un nuevo item en CXP			
														BmoPaccountItem bmoPaccountItemNew = new BmoPaccountItem();
														bmoPaccountItemNew.getPaccountId().setValue(bmoPaccount.getId());
														bmoPaccountItemNew.getRequisitionReceiptItemId().setValue(bmoRequisitionReceiptItem.getId());
														bmoPaccountItemNew.getQuantity().setValue(bmoRequisitionReceiptItem.getQuantity().toDouble());
														bmoPaccountItemNew.getName().setValue(bmoRequisitionReceiptItem.getName().toString());
														bmoPaccountItemNew.getPrice().setValue(bmoRequisitionReceiptItem.getPrice().toDouble());

														//Aplicar el iva a la cantidad del item
														if (bmoRequisitionReceipt.getTax().toDouble() > 0) {
															tax = bmoRequisitionReceiptItem.getAmount().toDouble() * taxRate;
															bmoPaccountItemNew.getAmount().setValue(bmoRequisitionReceiptItem.getAmount().toDouble() + tax);
														} else {
															bmoPaccountItemNew.getAmount().setValue(bmoRequisitionReceiptItem.getAmount().toDouble());
														}

														pmPaccountItem.saveSimple(pmConn2, bmoPaccountItemNew, bmUpdateResult);
													}
												}	
												if (!pmPaccountItem.itemsFromRequisitionReceiptExist(pmConn2, bmoPaccount.getId(), bmUpdateResult)) {
													%> / ITEMs-PACC_creado<%
												}
											}
											else System.out.println("paccID no existe!!!");
										}
										
										
										
										
									}// Fin de -Si existe roc y no existe ninguna cxp
									
								//}// Fin de -tipo comision
							}//Fin de -Pago: Total
						} //else_if Fin de -Recepcion: Total
						
						
						
					} //Fin de -OC Esta autorizado
							
				 i++;	
				} // Fin de -while
				pmConn.commit();
				pmConn2.commit();
				%>
				<br>
				<b>Proceso Terminado</b>
			</td>
		
		</tr>
	</table>
		<%
				
	
}catch (Exception e) {
	pmConn.rollback();
	pmConn2.rollback();
	throw new SFException("Proceso Item en conceptos: "+e.toString());
}finally {
	pmConn.close();
	pmConn2.close();
}
%>
		


  </body>
</html>