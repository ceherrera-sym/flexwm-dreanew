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

<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.shared.fi.BmoBankMovType"%>
<%@page import="java.sql.Types"%>
<%@page import="com.flexwm.server.fi.PmBankMovement"%>
<%@page import="com.flexwm.shared.fi.BmoBankMovement"%>
<%@page import="com.flexwm.server.fi.PmBankMovConcept"%>
<%@page import="com.flexwm.shared.fi.BmoBankMovConcept"%>
<%@page import="com.flexwm.shared.BmoFlexConfig"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.symgae.server.SFServerUtil"%>
<%@page import="com.symgae.shared.SFException"%>
<%@include file="../inc/login.jsp"%>

<html>
<%
	// Inicializar variables
	String title = "Proceso para crear Concepto de MB solo tipo Anticipo Proveedor";
	String sql ="", action = "";
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(Locale.US);
	
	if (request.getParameter("action") != null) action = request.getParameter("action");

%>

<head>
<title>:::<%=title%>:::</title>
<link rel="stylesheet" type="text/css"
	href="<%= sFParams.getAppURL()%>css/<%=defaultCss%>">
</head>

<body class="default">

	<table border="0" width="100%">
		<tr>
			<td align="left" rowspan="2" valign="top"><img border="0"
				width="<%=SFParams.LOGO_WIDTH%>" height="<%=SFParams.LOGO_HEIGHT%>"
				src="<%=sFParams.getMainImageUrl()%>"></td>
			<td class="reportTitle" align="left"><%=title%></td>
		</tr>
		<tr>
			<td class="reportDate" align="right">Creado: <%=SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat())%>
				por: <%=sFParams.getLoginInfo().getEmailAddress()%>
			</td>
		</tr>
	</table>
	<br>
	<FORM action="flexwm_createMovConceptRequisitionAdvance.jsp" method="POST" name="listFilter">
		<%  if (action.equals("")) { %>
			<input type="hidden" name="action" value="update">
			<table border="0" width="100%">
				<tr class="">
					<td align="center" colspan="4" height="35" valign="middle">
					
					<input class="formSaveButton" type="submit" value="Aplicar"></td>
					
				</tr>
			</table>
		<% }%>
	</FORM>
	<table border="0" width="100%" style="font-size: 12px">
		<TR>
			<td>
				<%	
					PmConn pmConn = new PmConn(sFParams);
					pmConn.open();
					
					PmConn pmConn2 = new PmConn(sFParams);
					pmConn2.open();
					
					BmUpdateResult bmUpdateResult = new BmUpdateResult();
					
					if (request.getParameter("action") != null) action = request.getParameter("action");
					try {
						if (action.equals("update")) { %>
							<br>
						    -------------------------------- MB ANT.PROV.-------------------------------------
						    <br>
							<%
							pmConn.disableAutoCommit();
							pmConn2.disableAutoCommit();
							
						    int i = 1, bankMovementId = 0;
						    
						    BmoBankMovement BmoBankMovement = new BmoBankMovement();
						    PmBankMovement pmBankMovement = new PmBankMovement(sFParams);
						    
						    BmoBankMovConcept bmoBankMovConcept = new BmoBankMovConcept();
						    PmBankMovConcept pmBankMovConcept = new PmBankMovConcept(sFParams);
						    
						    // Traer todos los MB de anticipo proveedor
						    sql = "SELECT * FROM bankmovements "
						    		+ " LEFT JOIN bankmovtypes ON(bkmt_bankmovtypeid = bkmv_bankmovtypeid) "
								    + " LEFT JOIN requisitions ON(reqi_requisitionid = bkmv_requisitionid) "
									+ " LEFT JOIN bankaccounts ON(bkac_bankaccountid = bkmv_bankaccountid) "
						     		+ " WHERE bkmt_category = '" + BmoBankMovType.CATEGORY_REQUISITIONADVANCE + "' " 
							    	+ " AND bkmt_type = '" + BmoBankMovType.TYPE_WITHDRAW + "' "
							    	+ " AND bkmv_requisitionid > 0 "
							    	+ " AND bkmv_bankmovementid "
							    	+ " NOT IN (SELECT bkmc_bankmovementid FROM bankmovconcepts WHERE bkmc_bankmovconceptid > 0) "
							    	+ " ORDER BY bkmv_bankmovementid DESC; ";
						    
				    		System.out.println("sql: "+sql);
						    pmConn2.doFetch(sql);
						   	
						    while (pmConn2.next()) { %>
						    	<%= i%>
						    	| <%= pmConn2.getString("bkmv_code")%> 
						    	| <%= pmConn2.getString("reqi_code")%>
						    	|CtaBan: <%= pmConn2.getString("bkac_name")%>
						    	|Saldo CtaBan:<%= formatCurrency.format(pmConn2.getDouble("bkac_balance"))%>
						    	<br>
						    	<% 
						    	bankMovementId = pmConn2.getInt("bkmv_bankmovementid");
				    		 	
				    		 	pmBankMovConcept = new PmBankMovConcept(sFParams);
								bmoBankMovConcept = new BmoBankMovConcept();
								bmoBankMovConcept.getBankMovementId().setValue(bankMovementId);
								bmoBankMovConcept.getCode().setValue(pmConn2.getString("reqi_code"));
								bmoBankMovConcept.getRequisitionId().setValue(pmConn2.getInt("bkmv_requisitionid"));
								bmoBankMovConcept.getAmount().setValue(pmConn2.getDouble("bkmv_withdraw"));
								pmBankMovConcept.saveSimple(pmConn, bmoBankMovConcept, bmUpdateResult);
								
						    	i++;
						     }
						    %>
						    <br>
						    -------------------------------- MB ANT.PROV. ligados a CP -------------------------------------
						    <br>
						    <%
						    // -----------------------------------------------------------------------------------------------
							// Traer todos los MB de anticipo proveedor ligados a una CxP
							// Asignar la OC al concepto
						    sql = "SELECT * FROM bankmovconcepts "
						    		+ " LEFT JOIN bankmovements ON(bkmv_bankmovementid = bkmc_bankmovementid) "
						    		+ " LEFT JOIN bankmovtypes ON(bkmt_bankmovtypeid = bkmv_bankmovtypeid) "
								    + " LEFT JOIN bankaccounts ON(bkac_bankaccountid = bkmv_bankaccountid) "
									+ " LEFT JOIN requisitions ON(reqi_requisitionid = bkmv_requisitionid) "
						     		+ " WHERE bkmt_category = '" + BmoBankMovType.CATEGORY_REQUISITIONADVANCE + "' " 
							    	+ " AND bkmt_type = '" + BmoBankMovType.TYPE_WITHDRAW + "' "
							    	+ " AND bkmv_requisitionid > 0 AND bkmc_requisitionid IS NULL "
							    	+ " AND bkmc_paccountid > 0 "
							    	+ " ORDER BY bkmv_bankmovementid DESC; ";
						    
				    		System.out.println("sql_ligados-CP: "+sql);
						    pmConn2.doFetch(sql);
						    i = 1; // regresa conteo a 1
						    int bankMovConceptId =0;
						    while (pmConn2.next()) { %>
						    	<%= i%>
						    	|<%= pmConn2.getString("bkmv_code")%>
						    	|<%= pmConn2.getString("reqi_code")%>
						    	|CP-<%= pmConn2.getString("bkmc_paccountid")%>
						    	|Concepto: <%= pmConn2.getString("bkmc_code")%>
						    	|CtaBan: <%= pmConn2.getString("bkac_name")%>
						    	|Saldo CtaBan:<%= formatCurrency.format(pmConn2.getDouble("bkac_balance"))%>
						    	<br>
						    	<% 
						    	bankMovConceptId = pmConn2.getInt("bkmc_bankmovconceptid");
				    		 	
				    		 	pmBankMovConcept = new PmBankMovConcept(sFParams);
								bmoBankMovConcept = new BmoBankMovConcept();
								bmoBankMovConcept = (BmoBankMovConcept)pmBankMovConcept.get(bankMovConceptId);
								bmoBankMovConcept.getRequisitionId().setValue(pmConn2.getInt("bkmv_requisitionid"));
								pmBankMovConcept.saveSimple(pmConn, bmoBankMovConcept, bmUpdateResult);

						    	i++;
						     }
						     
					 		 if (!bmUpdateResult.hasErrors()) {
						 		pmConn.commit();
							 	%>
								<h2>
									<b><font color="green">&#10004; Proceso Terminado
											&#10004;</font></b>
								</h2> <%	
						 	 } %> <%= bmUpdateResult.errorsToString() %> <%
						}
					} catch (Exception e) {
 						throw new SFException(e.toString());
 					} finally {
 						pmConn.close();
 						pmConn2.close();
 					}
				%>
			</td>
		</TR>
	</table>
</body>
</html>
