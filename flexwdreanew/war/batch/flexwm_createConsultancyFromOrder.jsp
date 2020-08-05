<!--  
/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author smuniz
 * @version 2013-10
 */ -->

<%@page import="com.flexwm.server.PmCompanyNomenclature"%>
<%@page import="com.flexwm.shared.op.BmoOrderType"%>
<%@page import="com.flexwm.server.op.PmConsultancy"%>
<%@page import="com.flexwm.shared.op.BmoConsultancy"%>
<%@page import="com.flexwm.shared.BmoFlexConfig"%>
<%@page import="java.sql.Types"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.symgae.shared.SFException"%>
<%@include file="../inc/login.jsp"%>

<html>

<%
	// Inicializar variables
	String title = "Proceso crear Venta de Servicio";
%>

<head>
<title>:::<%=title%>:::
</title>
<link rel="stylesheet" type="text/css"
	href="<%=sFParams.getAppURL()%>css/<%=defaultCss%>">
</head>

<body class="default">

	<table style="width: 100%">
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
	<!-- 	<FORM action="flexwm_periodFiscal.jsp" method="POST" name="listFilter"> -->
	<!-- 		<input type="hidden" name="action" value="update"> -->
	<!-- 		<tr> -->
	<!-- 			<td align="center" colspan="4" height="35" valign="middle"><input -->
	<!-- 				type="submit" value="Aceptar"></td> -->
	<!-- 		</tr> -->
	<!-- 	</FORM> -->

	<br>
	<table style="width: 100%; font-size: 12px">
		<TR>
			<td>
				<%
					PmConn pmConn = new PmConn(sFParams);
									pmConn.open();

									PmConn pmConn2 = new PmConn(sFParams);
									pmConn2.open();

									BmUpdateResult bmUpdateResult = new BmUpdateResult();
									String sql;
									String action = "";
									int autoIncrement = 0;
									try {
										pmConn.disableAutoCommit();
										pmConn2.disableAutoCommit();
										PmCompanyNomenclature pmCompanyNomenclature = new PmCompanyNomenclature(sFParams);

										int i = 1, opportunityId = 0;

										PmConsultancy pmConsultancy = new PmConsultancy(sFParams);

										sql = "SELECT * FROM orders " + " LEFT JOIN ordertypes ON (ortp_ordertypeid = orde_ordertypeid) "
												+ " LEFT JOIN orderdetails ON (ordt_orderid = orde_orderid) " + " WHERE orde_orderid > 0 "
												+ " AND (ortp_type = '" + BmoOrderType.TYPE_SALE + "' " 
												+ " OR ortp_type = '" + BmoOrderType.TYPE_SERVICE + "') "
												+ " AND orde_orderid NOT IN (SELECT cons_orderid FROM consultancies) "
												+ " ORDER BY orde_orderid asc ";
										System.out.println("proces_sql: " + sql);
										pmConn2.doFetch(sql);

										while (pmConn2.next()) {
											BmoConsultancy bmoConsultancy = new BmoConsultancy();
											
											bmoConsultancy.setId(pmConn2.getString("orde_orderid"));
											
										 	// Datos generales - 25 campos
											bmoConsultancy.getCode().setValue(pmConn2.getString("orde_code"));
				%> <%=i%> - <%= bmoConsultancy.getCode()%> <br> <%
						 	
				 			bmoConsultancy.getName().setValue(pmConn2.getString("orde_name"));
							if (!pmConn2.getString("orde_description").equals(""))
				 				bmoConsultancy.getDescription().setValue(pmConn2.getString("orde_description"));
							else 
				 				bmoConsultancy.getDescription().setValue(null);

				 			if (pmConn2.getInt("orde_ordertypeid") > 0)
				 				bmoConsultancy.getOrderTypeId().setValue(pmConn2.getInt("orde_ordertypeid"));
				 			else 
				 				bmoConsultancy.getOrderTypeId().setValue(null);

				 			if (pmConn2.getInt("orde_wflowtypeid") > 0)
				 				bmoConsultancy.getWFlowTypeId().setValue(pmConn2.getInt("orde_wflowtypeid"));
				 			else 
				 				bmoConsultancy.getWFlowTypeId().setValue(null);
				 			
				 			if (pmConn2.getInt("orde_wflowid") > 0)
				 				bmoConsultancy.getWFlowId().setValue(pmConn2.getInt("orde_wflowid"));
				 			else 
				 				bmoConsultancy.getWFlowId().setValue(null);
				 			
				 			if (pmConn2.getInt("orde_customerid") > 0)
				 				bmoConsultancy.getCustomerId().setValue(pmConn2.getInt("orde_customerid"));
				 			else 
				 				bmoConsultancy.getCustomerId().setValue(null);
				 			
				 			if (pmConn2.getInt("orde_userid") > 0)
				 				bmoConsultancy.getUserId().setValue(pmConn2.getInt("orde_userid"));
				 			else 
				 				bmoConsultancy.getUserId().setValue(null);
				 			
				 			if (pmConn2.getInt("orde_companyid") > 0)
				 				bmoConsultancy.getCompanyId().setValue(pmConn2.getInt("orde_companyid"));
				 			else 
				 				bmoConsultancy.getCompanyId().setValue(null);
				 			
				 			if (!pmConn2.getString("orde_lockstart").equals(""))
				 				bmoConsultancy.getStartDate().setValue(pmConn2.getString("orde_lockstart"));
				 			else 
				 				bmoConsultancy.getStartDate().setValue(null);
				 			
				 			if (!pmConn2.getString("orde_lockend").equals(""))
				 				bmoConsultancy.getEndDate().setValue(pmConn2.getString("orde_lockend"));
				 			else 
				 				bmoConsultancy.getEndDate().setValue(null);
				 			
				 			if (!pmConn2.getString("orde_status").equals(""))
				 				bmoConsultancy.getStatus().setValue(pmConn2.getString("orde_status"));
				 			else 
				 				bmoConsultancy.getStatus().setValue(null);
				 			
				 			if (pmConn2.getInt("orde_marketid") > 0)
				 				bmoConsultancy.getMarketId().setValue(pmConn2.getInt("orde_marketid"));
				 			else 
				 				bmoConsultancy.getMarketId().setValue(null);
				 			
				 			if (pmConn2.getInt("orde_currencyid") > 0)
				 				bmoConsultancy.getCurrencyId().setValue(pmConn2.getInt("orde_currencyid"));
				 			else 
				 				bmoConsultancy.getCurrencyId().setValue(null);
				 			
				 			if (!pmConn2.getString("orde_currencyparity").equals(""))
				 				bmoConsultancy.getCurrencyParity().setValue(pmConn2.getDouble("orde_currencyparity"));
				 			else 
				 				bmoConsultancy.getCurrencyParity().setValue(null);
				 			
				 			if (pmConn2.getDouble("orde_amount") > 0)
				 				bmoConsultancy.getAmount().setValue(pmConn2.getDouble("orde_amount"));
				 			else 
				 				bmoConsultancy.getAmount().setValue(null);
				 			
				 			if (pmConn2.getDouble("orde_tax") > 0)
				 				bmoConsultancy.getTax().setValue(pmConn2.getDouble("orde_tax"));
				 			else 
				 				bmoConsultancy.getTax().setValue(null);
				 			
				 			if (pmConn2.getDouble("orde_total") > 0)
				 				bmoConsultancy.getTotal().setValue(pmConn2.getDouble("orde_total"));
				 			else 
				 				bmoConsultancy.getTotal().setValue(null);
				 			
				 			if (pmConn2.getDouble("orde_payments") > 0)
				 				bmoConsultancy.getPayments().setValue(pmConn2.getDouble("orde_payments"));
				 			else 
				 				bmoConsultancy.getPayments().setValue(null);
				 			
				 			if (pmConn2.getDouble("orde_balance") > 0)
				 				bmoConsultancy.getBalance().setValue(pmConn2.getDouble("orde_balance"));
				 			else 
				 				bmoConsultancy.getBalance().setValue(0);
				 			
				 			if (pmConn2.getInt("orde_orderid") > 0)
						 		bmoConsultancy.getOrderId().setValue(pmConn2.getInt("orde_orderid"));
				 			else 
				 				bmoConsultancy.getOrderId().setValue(null);
				 			
				 			if (pmConn2.getInt("orde_opportunityid") > 0)
				 				bmoConsultancy.getOpportunityId().setValue(pmConn2.getInt("orde_opportunityid"));
				 			else 
				 				bmoConsultancy.getOpportunityId().setValue(null);

			 				if (pmConn2.getInt("orde_defaultbudgetitemid") > 0)
				 				bmoConsultancy.getBudgetItemId().setValue(pmConn2.getInt("orde_defaultbudgetitemid"));
			 				else 
				 				bmoConsultancy.getBudgetItemId().setValue(null);
			 				
			 				if (pmConn2.getInt("orde_defaultareaid") > 0)
				 				bmoConsultancy.getAreaId().setValue(pmConn2.getInt("orde_defaultareaid"));
			 				else 
				 				bmoConsultancy.getAreaId().setValue(null);
				 			
			 				if (!pmConn2.getString("orde_tags").equals(""))
				 				bmoConsultancy.getTags().setValue(pmConn2.getString("orde_tags"));
			 				else 
				 				bmoConsultancy.getTags().setValue(null);
							
			 				
				 			// Scrum 9 campos
				 			if (pmConn2.getInt("ordt_areaid") > 0)
				 				bmoConsultancy.getAreaIdScrum().setValue(pmConn2.getInt("ordt_areaid"));
				 			else 
				 				bmoConsultancy.getAreaIdScrum().setValue(null);
				 			
				 			if (pmConn2.getInt("ordt_leaderuserid") > 0)
				 				bmoConsultancy.getLeaderUserId().setValue(pmConn2.getInt("ordt_leaderuserid"));
				 			else 
				 				bmoConsultancy.getLeaderUserId().setValue(null);
				 			
				 			if (pmConn2.getInt("ordt_assigneduserid") > 0)
				 				bmoConsultancy.getAssignedUserId().setValue(pmConn2.getInt("ordt_assigneduserid"));
				 			else 
				 				bmoConsultancy.getAssignedUserId().setValue(null);
				 			
				 			
				 			if (!pmConn2.getString("ordt_status").equals(""))
				 				bmoConsultancy.getStatusScrum().setValue(pmConn2.getString("ordt_status"));
				 			else 
				 				bmoConsultancy.getStatusScrum().setValue(null);
				 			
				 			if (!pmConn2.getString("ordt_closedate").equals(""))
				 				bmoConsultancy.getCloseDate().setValue(pmConn2.getString("ordt_closedate"));
				 			else 
				 				bmoConsultancy.getCloseDate().setValue(null);
				 			
				 			if (!pmConn2.getString("ordt_orderdate").equals(""))
				 				bmoConsultancy.getOrderDate().setValue(pmConn2.getString("ordt_orderdate"));
				 			else 
				 				bmoConsultancy.getOrderDate().setValue(null);
				 			
				 			if (!pmConn2.getString("ordt_desiredate").equals(""))
				 				bmoConsultancy.getDesireDate().setValue(pmConn2.getString("ordt_desiredate"));
				 			else 
				 				bmoConsultancy.getDesireDate().setValue(null);
				 			
				 			if (!pmConn2.getString("ordt_startdate").equals(""))
				 				bmoConsultancy.getStartDateScrum().setValue(pmConn2.getString("ordt_startdate"));
				 			else 
				 				bmoConsultancy.getStartDateScrum().setValue(null);
				 			
				 			if (!pmConn2.getString("ordt_deliverydate").equals(""))
				 				bmoConsultancy.getDeliveryDate().setValue(pmConn2.getString("ordt_deliverydate"));
				 			else 
				 				bmoConsultancy.getDeliveryDate().setValue(null);
				
				 			
				 			// FWK
				 			if (pmConn2.getInt("orde_usercreateid") > 0)
				 				bmoConsultancy.getUserCreateId().setValue(pmConn2.getInt("orde_usercreateid"));
				 			else 
				 				bmoConsultancy.getUserCreateId().setValue(null);
				 			
				 			if (pmConn2.getInt("orde_usermodifyid") > 0)
				 				bmoConsultancy.getUserModifyId().setValue(pmConn2.getInt("orde_usermodifyid"));
				 			else 
				 				bmoConsultancy.getUserModifyId().setValue(null);

				 			if (!pmConn2.getString("orde_datecreate").equals(""))
				 				bmoConsultancy.getDateCreate().setValue(pmConn2.getString("orde_datecreate"));
				 			else 
				 				bmoConsultancy.getDateCreate().setValue(null);
				 			
				 			if (!pmConn2.getString("orde_datemodify").equals(""))
				 				bmoConsultancy.getDateModify().setValue(pmConn2.getString("orde_datemodify"));
				 			else 
				 				bmoConsultancy.getDateModify().setValue(null);

				 			// Almacena pedido preliminar
				 			String sqlInsert = "INSERT INTO `consultancies`"
				 							+ " ("
				 							+"`cons_consultancyid`,"
				 							+"`cons_code`,"
				 							+"`cons_name`,"
				 							+"`cons_description`,"
				 							+"`cons_ordertypeid`,"
				 							+"`cons_wflowtypeid`,"
				 							+"`cons_wflowid`,"
				 							+"`cons_customerid`,"
				 							+"`cons_userid`,"
				 							+"`cons_companyid`,\n"
				 							+"`cons_startdate`,"
				 							+"`cons_enddate`,"
				 							+"`cons_status`,"
				 							+"`cons_marketid`,"
				 							+"`cons_currencyid`,"
				 							+"`cons_currencyparity`,"
				 							+"`cons_amount`,"
				 							+"`cons_tax`,"
				 							+"`cons_total`,"
				 							+"`cons_payments`,\n"
				 							+"`cons_balance`,"
				 							+"`cons_orderid`,"
				 							+"`cons_opportunityid`,"
				 							+"`cons_budgetitemid`,"
				 							+"`cons_areaid`,"
				 							+"`cons_tags`,"
				 							+"`cons_statusscrum`,"
				 							+"`cons_closedate`,"
				 							+"`cons_orderdate`,"
				 							+"`cons_desiredate`,\n"
				 							+"`cons_startdatescrum`,"
				 							+"`cons_deliverydate`,"
				 							+"`cons_leaderuserid`,"
				 							+"`cons_assigneduserid`,"
				 							+"`cons_areaidscrum`,"
				 							
											+"`cons_usercreateid`,"
											+"`cons_usermodifyid`,"
											+"`cons_datecreate`,"
											+"`cons_datemodify`"
				 						+" ) "
				 					
				 						+ " VALUES ( "
				 						+"'"+ bmoConsultancy.getId()+"',"
				 						+"'"+ bmoConsultancy.getCode().toString()+"',"
				 						+"'"+ bmoConsultancy.getName().toString()+"',"
						 				+ ((!bmoConsultancy.getDescription().toString().equals("")) ? "'"+ bmoConsultancy.getDescription().toString()+"'," : "null,") 
				 						+"'"+ bmoConsultancy.getOrderTypeId().toInteger()+"',"
				 						+"'"+ bmoConsultancy.getWFlowTypeId().toInteger()+"',"
				 						+"'"+ bmoConsultancy.getWFlowId().toInteger()+"',"
				 						+"'"+ bmoConsultancy.getCustomerId().toInteger()+"',"
				 						+"'"+ bmoConsultancy.getUserId().toInteger()+"',"
				 						+"'"+ bmoConsultancy.getCompanyId().toInteger()+"',\n"
				 		
				 						+ ((!bmoConsultancy.getStartDate().toString().equals("")) ? "'"+ bmoConsultancy.getStartDate().toString()+"'," : "null,") 
				 						+ ((!bmoConsultancy.getEndDate().toString().equals("")) ? "'"+ bmoConsultancy.getEndDate().toString()+"'," : "null,") 
				 						+"'"+ bmoConsultancy.getStatus().toString()+"',"
				 						+ ((bmoConsultancy.getMarketId().toInteger() > 0) ? "'"+ bmoConsultancy.getMarketId().toInteger()+"'," : "null,") 
				 						
				 						+"'"+ bmoConsultancy.getCurrencyId().toInteger()+"',"
				 						+"'"+ bmoConsultancy.getCurrencyParity().toDouble()+"',"
				 						+"'"+ bmoConsultancy.getAmount().toDouble()+"',"
				 						+"'"+ bmoConsultancy.getTax().toDouble()+"',"
				 						+"'"+ bmoConsultancy.getTotal().toDouble()+"',"
				 						+"'"+ bmoConsultancy.getPayments().toDouble()+"',\n"
				 						+"'"+ bmoConsultancy.getBalance().toDouble()+"',"
				 						+"'"+ bmoConsultancy.getOrderId().toInteger()+"',"
				 						
				 						+ ((bmoConsultancy.getOpportunityId().toInteger() > 0) ? "'"+ bmoConsultancy.getOpportunityId().toInteger()+"'," : "null,") 
				 						+ ((bmoConsultancy.getBudgetItemId().toInteger() > 0) ? "'"+ bmoConsultancy.getBudgetItemId().toInteger()+"'," : "null,") 
				 						+ ((bmoConsultancy.getAreaId().toInteger() > 0) ? "'"+ bmoConsultancy.getAreaId().toInteger()+"'," : "null,") 
				 						+ ((!bmoConsultancy.getTags().toString().equals("")) ? "'"+ bmoConsultancy.getTags().toString()+"'," : "null,") 
				 						
				 						// SCRUM
				 						+ ((!bmoConsultancy.getStatusScrum().toString().equals("")) ? "'"+ bmoConsultancy.getStatusScrum().toString()+"'," : "null,") 
				 						+ ((!bmoConsultancy.getCloseDate().toString().equals("")) ? "'"+ bmoConsultancy.getCloseDate().toString()+"'," : "null,") 
				 						+ ((!bmoConsultancy.getOrderDate().toString().equals("")) ? "'"+ bmoConsultancy.getOrderDate().toString()+"'," : "null,") 
				 						+ ((!bmoConsultancy.getDesireDate().toString().equals("")) ? "'"+ bmoConsultancy.getDesireDate().toString()+"'," : "null,\n") 
				 						+ ((!bmoConsultancy.getStartDateScrum().toString().equals("")) ? "'"+ bmoConsultancy.getStartDateScrum().toString()+"'," : "null,") 
				 						+ ((!bmoConsultancy.getDeliveryDate().toString().equals("")) ? "'"+ bmoConsultancy.getDeliveryDate().toString()+"'," : "null,") 
				 						+ ((bmoConsultancy.getLeaderUserId().toInteger() > 0) ? "'"+ bmoConsultancy.getLeaderUserId().toInteger()+"'," : "null,") 
				 						+ ((bmoConsultancy.getAssignedUserId().toInteger() > 0) ? "'"+ bmoConsultancy.getAssignedUserId().toInteger()+"'," : "null,") 
				 						+ ((bmoConsultancy.getAreaIdScrum().toInteger() > 0) ? "'"+ bmoConsultancy.getAreaIdScrum().toInteger()+"'," : "null,") 
				 						
				 						+ ((bmoConsultancy.getUserCreateId().toInteger() > 0) ? "'"+ bmoConsultancy.getUserCreateId().toInteger()+"'," : "null,") 
				 						+ ((bmoConsultancy.getUserModifyId().toInteger() > 0) ? "'"+ bmoConsultancy.getUserModifyId().toInteger()+"'," : "null,") 
				 						+ ((!bmoConsultancy.getDateCreate().toString().equals("")) ? "'"+ bmoConsultancy.getDateCreate().toString()+",'," : "null,") 
				 						+ ((!bmoConsultancy.getDateModify().toString().equals("")) ? "'"+ bmoConsultancy.getDateModify().toString()+"'" : "null") 

				 						+ " ); ";
				 						System.out.println("\n\n\n"+sqlInsert);
				 						pmConn.doUpdate(sqlInsert);
				 						
				 			i++;
				 		}

 			if (!bmUpdateResult.hasErrors()) {

 			pmConn.commit();
 			%>
				<h2>
					<b><font color="green">&#10004; Proceso Terminado
							&#10004;</font></b>
				</h2> <%
 			}
 %> <%=bmUpdateResult.errorsToString()%> <%
		 
		 
		 
		 
		 
 	} catch (Exception e) {%>
 		<h2><%=  new SFException(e.toString()) %></h2>
 		throw new SFException(e.toString());
 		<%
 	} finally {
 		pmConn.close();
 	}

 %>
			</td>
		</TR>
	</table>
</body>
</html>