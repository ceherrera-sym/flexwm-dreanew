<%@page import="java.util.Iterator"%>

<%@page import="com.symgae.shared.BmFilter"%>
<%@page import="com.symgae.shared.BmObject"%>

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

<%@page import="com.flexwm.shared.fi.BmoBankMovConcept"%>
<%@page import="com.flexwm.server.fi.PmBankMovConcept"%>

<%@page import="com.symgae.server.PmConn"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>

<%@page import="com.flexwm.shared.fi.BmoRaccountType"%>
<%@page import="com.flexwm.shared.fi.BmoRaccount"%>
<%@page import="com.flexwm.server.fi.PmRaccount"%>
<%@page import="com.flexwm.shared.fi.BmoRaccountAssignment"%>
<%@page import="com.flexwm.server.fi.PmRaccountAssignment"%>
<%@page import="com.flexwm.shared.fi.BmoRaccountItem"%>
<%@page import="com.flexwm.server.fi.PmRaccountItem"%>

<%@page import="com.flexwm.shared.fi.BmoPaccountType"%>
<%@page import="com.flexwm.shared.fi.BmoPaccount"%>
<%@page import="com.flexwm.server.fi.PmPaccount"%>
<%@page import="com.flexwm.shared.fi.BmoPaccountAssignment"%>
<%@page import="com.flexwm.server.fi.PmPaccountAssignment"%>
<%@page import="com.flexwm.shared.fi.BmoPaccountItem"%>
<%@page import="com.flexwm.server.fi.PmPaccountItem"%>

<%@page import="com.flexwm.shared.fi.BmoBankMovType"%>
<%@page import="com.flexwm.server.fi.PmBankMovType"%>

<%@page import="com.flexwm.shared.fi.BmoBankMovement"%>
<%@page import="com.flexwm.server.fi.PmBankMovement"%>

<%@page import="com.flexwm.shared.fi.BmoBankAccount"%>
<%@page import="com.flexwm.server.fi.PmBankAccount"%>
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
		<td>
		<p>--------------------------------------------------CxP--------------------------------------------------<p>
		<% 
			
				PmConn pmConn = new PmConn(sFParams);
				BmUpdateResult bmUpdateResult = new BmUpdateResult();
				PmConn pmConnPaccount = new PmConn(sFParams);
				pmConnPaccount.open();

				try {
					pmConn.open();
					pmConn.disableAutoCommit();
							
					BmoPaccountType bmoPaccountType = new BmoPaccountType();
					
					BmoPaccount bmoPaccount = new BmoPaccount();
					
					
					int parentId = 0, i = 1;
					
					String sql = "SELECT * FROM paccounts " +
								 " LEFT JOIN paccounttypes ON(pact_paccounttypeid = pacc_paccounttypeid) " +
								 " WHERE pacc_parentid > 0 " +
								 " AND pact_type = '" + bmoPaccountType.TYPE_DEPOSIT + "'";
					
					System.out.println("sql: " +sql);
					pmConnPaccount.doFetch(sql);
					
					%>
					#
					&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;
					Id: 
					&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;
					ParentId: <br>
					<% 
					while(pmConnPaccount.next()) {
						%>
						<%= i%>
						&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;
						<%= pmConnPaccount.getInt("pacc_paccountid")%>
						&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;
						<%= pmConnPaccount.getInt("pacc_parentid")%><br>
						<% 
						parentId = pmConnPaccount.getInt("pacc_parentid");
						if (parentId > 0){
							
							PmPaccount pmPaccount = new PmPaccount(sFParams);
							bmoPaccount = (BmoPaccount)pmPaccount.get(pmConnPaccount.getInt("pacc_paccountid"));
							
							BmoPaccountItem bmoPaccountItem = new BmoPaccountItem();
							PmPaccountItem pmPaccountItem = new PmPaccountItem(sFParams);
							
							bmoPaccountItem.getName().setValue(" -> " + bmoPaccount.getCode().toString());	
							bmoPaccountItem.getQuantity().setValue(1);
							bmoPaccountItem.getPrice().setValue(bmoPaccount.getAmount().toDouble());
							bmoPaccountItem.getAmount().setValue(bmoPaccount.getAmount().toDouble());
							bmoPaccountItem.getPaccountId().setValue(bmoPaccount.getId());
							
							pmPaccountItem.saveSimple(pmConn, bmoPaccountItem, bmUpdateResult);
							
							BmoPaccountAssignment bmoPaccountAssignment = new BmoPaccountAssignment();
							PmPaccountAssignment pmPaccountAssignment = new PmPaccountAssignment(sFParams);
							bmoPaccountAssignment.getCode().setValue(bmoPaccount.getCode().toString());
							bmoPaccountAssignment.getInvoiceno().setValue(bmoPaccount.getInvoiceno().toString());
							bmoPaccountAssignment.getPaccountId().setValue(bmoPaccount.getId());
							bmoPaccountAssignment.getForeignPaccountId().setValue(parentId);
							bmoPaccountAssignment.getAmount().setValue(bmoPaccount.getAmount().toDouble());

							pmPaccountAssignment.saveSimple(pmConn, bmoPaccountAssignment, bmUpdateResult);
							
							pmPaccount.updateBalance(pmConn, bmoPaccount, bmUpdateResult);
							
						}
						i++;
					}
					pmConn.commit();
					
					%>
						<b>Proceso CxP Terminado</b>
					<%
				}catch (Exception e) {
					throw new SFException("Proceso CxP: "+e.toString());
				}finally {
					pmConn.close();
					pmConnPaccount.close();
				}
			
			%>
			<p>--------------------------------------------------CxC--------------------------------------------------<p>
			<%
				PmConn pmConnRacc = new PmConn(sFParams);
				BmUpdateResult bmUpdateResultRacc = new BmUpdateResult();
				PmConn pmConnRaccount = new PmConn(sFParams);
				pmConnRaccount.open();
				
				try {
					pmConnRacc.open();
					pmConnRacc.disableAutoCommit();
							
					BmoRaccountType bmoRaccountType = new BmoRaccountType();
					
					BmoRaccount bmoRaccount = new BmoRaccount();
					
					
					int parentId = 0, i = 1;
					
					String sqlRacc = "SELECT * FROM raccounts " +
									" LEFT JOIN raccounttypes ON(ract_raccounttypeid = racc_raccounttypeid) " +
									" WHERE racc_parentid > 0 " +
									" AND ract_type = '" + bmoRaccountType.TYPE_DEPOSIT + "'";
					
					System.out.println("sqlRacc: " +sqlRacc);
					pmConnRaccount.doFetch(sqlRacc);
					
					%>
					#
					&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;
					Id: 
					&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;
					ParentId: <br>
					<% 
					
					while(pmConnRaccount.next()) {
						%>
						<%= i%>&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;
						<%= pmConnRaccount.getInt("racc_raccountid")%>
						&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;
						<%= pmConnRaccount.getInt("racc_parentid")%> <br>
						<% 
						parentId = pmConnRaccount.getInt("racc_parentid");
						if (parentId > 0){
							
							PmRaccount pmRaccount = new PmRaccount(sFParams);
							bmoRaccount = (BmoRaccount)pmRaccount.get(pmConnRaccount.getInt("racc_raccountid"));
							
							BmoRaccountItem bmoRaccountItem = new BmoRaccountItem();
							PmRaccountItem pmRaccountItem = new PmRaccountItem(sFParams);
							
							bmoRaccountItem.getName().setValue(" -> " + bmoRaccount.getCode().toString());	
							bmoRaccountItem.getQuantity().setValue(1);
							bmoRaccountItem.getPrice().setValue(bmoRaccount.getAmount().toDouble());
							bmoRaccountItem.getAmount().setValue(bmoRaccount.getAmount().toDouble());
							bmoRaccountItem.getRaccountId().setValue(bmoRaccount.getId());
							
							pmRaccountItem.saveSimple(pmConnRacc, bmoRaccountItem, bmUpdateResultRacc);
							
							
							BmoRaccountAssignment bmoRaccountAssignment = new BmoRaccountAssignment();
							PmRaccountAssignment pmRaccountAssignment = new PmRaccountAssignment(sFParams);
							
							bmoRaccountAssignment.getCode().setValue(bmoRaccount.getCode().toString());
							bmoRaccountAssignment.getInvoiceno().setValue(bmoRaccount.getInvoiceno().toString());
							bmoRaccountAssignment.getRaccountId().setValue(bmoRaccount.getId());
							bmoRaccountAssignment.getForeignRaccountId().setValue(parentId);
							bmoRaccountAssignment.getAmount().setValue(bmoRaccount.getAmount().toDouble());

							pmRaccountAssignment.saveSimple(pmConnRacc, bmoRaccountAssignment, bmUpdateResultRacc);
							
							pmRaccount.updateBalance(pmConnRacc, bmoRaccount, bmUpdateResultRacc);
							
						}
						i++;
						%>
				 		
					<%
					}
					pmConnRacc.commit();
									
					%>
						<b>Proceso CxC Terminado</b>
					<%
				}catch (Exception e) {
					throw new SFException("Proceso CxC: "+e.toString());
				}finally {
					pmConnRacc.close();
					pmConnRaccount.close();
				}
		%>
		<p>--------------------------------------------------BANCOS--------------------------------------------------<p>
		<%
		PmConn pmConnBkmv = new PmConn(sFParams);
		BmUpdateResult bmUpdateResultBkmv = new BmUpdateResult();
		PmConn pmConnBankMovement = new PmConn(sFParams);
		pmConnBankMovement.open();
		
		try {
			pmConnBkmv.open();
			pmConnBkmv.disableAutoCommit();
					
			BmoBankMovType bmoBankMovType = new BmoBankMovType();
			
			BmoBankMovement bmoBankMovement = new BmoBankMovement();
			
			
			int parentId = 0, i = 1;
			/*
			String sqlBkmv = "SELECT * FROM bankmovements " +
								" LEFT JOIN bankmovtypes ON(bkmt_bankmovtypeid = bkmv_bankmovtypeid) " +
								" WHERE bkmv_parentid > 0 " +
								" AND bkmt_type = '" + bmoBankMovType.TYPE_DEPOSIT + "'";
			*/
			String sqlBkmv = "SELECT * FROM bankmovements " +
								" LEFT JOIN bankmovconcepts  ON(bkmc_bankmovementid = bkmv_bankmovementid) " +
								" LEFT JOIN bankmovtypes ON(bkmt_bankmovtypeid = bkmv_bankmovtypeid) " +
								" WHERE bkmc_bankmovconceptid is null" +
								" ORDER BY bkmv_bankmovementid asc ";
			
			
			System.out.println("sqlBkmv: " +sqlBkmv);
			pmConnBankMovement.doFetch(sqlBkmv);
			
			%>
			#
			&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;
			Id: 
			&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;
			<br>
			<% 
			
			while(pmConnBankMovement.next()) {
				
				%>
				
				<%= i%>&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;
				<%= pmConnBankMovement.getInt("bkmv_bankmovementid")%>
				&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;
				<br>
				<% 
				
						PmBankMovement pmBankMovement = new PmBankMovement(sFParams);
						bmoBankMovement = (BmoBankMovement)pmBankMovement.get(pmConnBankMovement.getInt("bkmv_bankmovementid"));
						
						BmoBankMovConcept bmoBankMovConcept = new BmoBankMovConcept();
						PmBankMovConcept pmBankMovConcept = new PmBankMovConcept(sFParams);
												
						PmBankMovType pmBankMovType = new PmBankMovType(sFParams);
						bmoBankMovType = (BmoBankMovType)pmBankMovType.get(bmoBankMovement.getBankMovTypeId().toInteger());
						
						//BankAccount Origen
						PmBankAccount pmBankAccount = new PmBankAccount(sFParams);
						BmoBankAccount bmoBankAccount = new BmoBankAccount();
						if(bmoBankMovement.getBankAccountId().toInteger() > 0)
							bmoBankAccount = (BmoBankAccount)pmBankAccount.get(bmoBankMovement.getBankAccountId().toInteger());

						//BankAccount Destino
						PmBankAccount pmBankAccountForeign = new PmBankAccount(sFParams);
						BmoBankAccount bmoBankAccountForeign = new BmoBankAccount();
						if(bmoBankMovement.getBankAccoTransId().toInteger() > 0)
							bmoBankAccountForeign = (BmoBankAccount)pmBankAccountForeign.get(bmoBankMovement.getBankAccoTransId().toInteger());
						
						bmoBankMovConcept.getBankMovementId().setValue(bmoBankMovement.getId());
						
						//Tipo de MovBank
						if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_TRANSFER)) {
							if(bmoBankAccountForeign.getId() > 0){
								bmoBankMovConcept.getCode().setValue("Trasp. para: " + bmoBankAccountForeign.getName().toString());
							}else{
								BmoBankMovement bmoBankMovementTras = new BmoBankMovement();
								if(bmoBankMovement.getParentId().toInteger() > 0)
									bmoBankMovementTras = (BmoBankMovement)pmBankMovement.get(bmoBankMovement.getParentId().toInteger());
								
								bmoBankMovConcept.getCode().setValue("Trasp. de: " + bmoBankMovementTras.getBmoBankAccount().getName().toString());
								}
						}else if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_DEPOSITFREE)) {
							bmoBankMovConcept.getCode().setValue("Dep Cta: " + bmoBankAccount.getName().toString());
						}else if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_DISPOSALFREE)) {
							bmoBankMovConcept.getCode().setValue("Disp Cta: " + bmoBankAccount.getName().toString());	
						}else {
							bmoBankMovConcept.getCode().setValue(" -> " + bmoBankMovement.getCode().toString());
						}
						
						if(bmoBankMovement.getWithdraw().toDouble() > 0)
							bmoBankMovConcept.getAmount().setValue(bmoBankMovement.getWithdraw().toDouble());
						else
							bmoBankMovConcept.getAmount().setValue(bmoBankMovement.getDeposit().toDouble());
						
						//bmoBankMovConcept.getForeignId().setValue(parentId);

							
						pmBankMovConcept.saveSimple(pmConnBkmv, bmoBankMovConcept, bmUpdateResultBkmv);
						//pmBankMovement.updateBalance(pmConnBkmv, bmoBankMovement, bmUpdateResultBkmv);
	
						i++;
						System.out.println("-------------------------------------------------------------------- \n");
			}
			pmConnBkmv.commit();
		}catch (Exception e) {			
			pmConnBkmv.rollback();
			throw new SFException("Proceso Bancos: "+e.toString());
		}finally {
			pmConnBkmv.close();
			pmConnBankMovement.close();
		}	
			%>
			
			<p>--------------------------------------------------Items Paccount de los conceptos--------------------------------------------------<p>
				
	<%
			
			//Actualizar el depositItem en conceptos de banco
			PmConn pmConn2 = new PmConn(sFParams);
			BmUpdateResult bmUpdateResult2 = new BmUpdateResult();
			
			try {
				pmConn2.open();
				pmConn2.disableAutoCommit();
				
				BmoBankMovConcept bmoBankMovConcept = new BmoBankMovConcept();
				BmoPaccountItem bmoPaccountItem = new BmoPaccountItem();
				PmPaccountItem pmPaccountItem = new PmPaccountItem(sFParams);
				
				BmFilter filterBankMovConcept = new BmFilter();				
				filterBankMovConcept.setValueOperatorFilter(bmoBankMovConcept.getKind(), bmoBankMovConcept.getPaccountId(), BmFilter.NOTEQUALS, "0");
				Iterator<BmObject> listBankMovConcept = new PmBankMovConcept(sFParams).list(pmConn2, filterBankMovConcept).iterator();			
				int i = 0;
				while (listBankMovConcept.hasNext()) {
					bmoBankMovConcept = (BmoBankMovConcept)listBankMovConcept.next(); %>
					<%= i + 1%>&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;
					<%= bmoBankMovConcept.getId() %>
					&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;
					<br>
					<%
					// Si no tiene liga al item del deposito actualizar el campo
					if (!(bmoBankMovConcept.getDepositPaccountItemId().toInteger() > 0)) {
						//Obtener el item del deposito (foreignId)
						bmoPaccountItem = (BmoPaccountItem)pmPaccountItem.getBy(pmConn2, bmoBankMovConcept.getForeignId().toInteger(), bmoPaccountItem.getPaccountId().getName());
						
						//Actualizar el item del concepto de banco con el item
						bmoBankMovConcept.getDepositPaccountItemId().setValue(bmoPaccountItem.getId());						
						pmPaccountItem.saveSimple(pmConn2, bmoBankMovConcept, bmUpdateResult2);						
						
					}
				 i++;	
				}	
						
				
				pmConn2.commit();
				
			
		}catch (Exception e) {			
			pmConn2.rollback();
			throw new SFException("Proceso Item en conceptos: "+e.toString());
		}finally {
			pmConn2.close();
		}
%>

<p>--------------------------------------------------Items Raccount de los conceptos--------------------------------------------------<p>

<%
		
		//Actualizar el depositItem en conceptos de banco
		PmConn pmConn3 = new PmConn(sFParams);
		BmUpdateResult bmUpdateResult3 = new BmUpdateResult();
		
		try {
			pmConn3.open();
			pmConn3.disableAutoCommit();
			
			BmoBankMovConcept bmoBankMovConcept = new BmoBankMovConcept();
			BmoRaccountItem bmoRaccountItem = new BmoRaccountItem();
			PmRaccountItem pmRaccountItem = new PmRaccountItem(sFParams);
			
			BmFilter filterBankMovConcept = new BmFilter();				
			filterBankMovConcept.setValueOperatorFilter(bmoBankMovConcept.getKind(), bmoBankMovConcept.getRaccountId(), BmFilter.NOTEQUALS, "0");
			Iterator<BmObject> listBankMovConcept = new PmBankMovConcept(sFParams).list(pmConn3, filterBankMovConcept).iterator();			
			int i = 0;
			while (listBankMovConcept.hasNext()) {
				bmoBankMovConcept = (BmoBankMovConcept)listBankMovConcept.next(); %>
				<%= i + 1%>&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;
				<%= bmoBankMovConcept.getId() %>
				&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;
				<br>
				<%
				// Si no tiene liga al item del deposito actualizar el campo
				//if (!(bmoBankMovConcept.getDepositRaccountItemId().toInteger() > 0)) {
					//Obtener el item del deposito (foreignId)
					bmoRaccountItem = (BmoRaccountItem)pmRaccountItem.getBy(pmConn3, bmoBankMovConcept.getForeignId().toInteger(), bmoRaccountItem.getRaccountId().getName());
					
					//Actualizar el item del concepto de banco con el item
					//bmoBankMovConcept.getDepositRaccountItemId().setValue(bmoRaccountItem.getId());						
					pmRaccountItem.saveSimple(pmConn3, bmoBankMovConcept, bmUpdateResult3);						
					
				//}
			 i++;	
			}	
					
			
			pmConn3.commit();
			
		
	}catch (Exception e) {			
		pmConn3.rollback();
		throw new SFException("Proceso Item en conceptos: "+e.toString());
	}finally {
		pmConn3.close();
	}
%>
<b>Proceso Bancos Terminado</b>
		</td>
	</tr>
</table>

  </body>
</html>