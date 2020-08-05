<!--  
/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Javier Alberto Hernandez
 * @version 2013-10
 */ -->

<%@page import="com.flexwm.server.cm.PmOpportunity"%>
<%@page import="com.flexwm.shared.cm.BmoOpportunity"%>
<%@page import="com.flexwm.server.FlexUtil"%>
<%@page import="com.flexwm.server.op.PmProduct"%>
<%@page import="com.flexwm.shared.op.BmoProduct"%>
<%@page import="com.flexwm.server.op.PmOrderType"%>
<%@page import="com.flexwm.shared.op.BmoOrderType"%>
<%@page import="com.flexwm.server.op.PmProductPrice"%>
<%@page import="com.flexwm.shared.op.BmoProductPrice"%>
<%@page import="com.flexwm.shared.BmoFlexConfig"%>
<%@page import="java.sql.Types"%>
<%@page import="com.flexwm.shared.wf.BmoWFlow"%>
<%@page import="com.flexwm.server.wf.PmWFlow"%>
<%@page import="com.flexwm.shared.wf.BmoWFlowUser"%>
<%@page import="com.flexwm.server.wf.PmWFlowUser"%>
<%@page import="com.flexwm.server.wf.PmWFlowStep"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.symgae.server.SFServerUtil"%>
<%@page import="com.symgae.shared.SFException"%>
<%@include file="../inc/login.jsp"%>

<html>

<%
	// Inicializar variables
	String title = "Proceso Calcular periodo fiscal";
%>

<head>
<title>:::<%=appTitle%>:::
</title>
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
	<FORM action="flexwm_periodFiscal.jsp" method="POST" name="listFilter">
		<input type="hidden" name="action" value="update">
		<tr class="">
			<td align="center" colspan="4" height="35" valign="middle"><input
				type="submit" value="Aceptar"></td>
		</tr>
	</FORM>

	<br>
	<table border="0" width="100%" style="font-size: 12px">
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
					if (request.getParameter("action") != null) action = request.getParameter("action");
					try {
						if (action.equals("update")) { 
							 pmConn.disableAutoCommit();
							 pmConn2.disableAutoCommit();
							 
						     int i = 1, opportunityId = 0;
						     
						     BmoOpportunity bmoOpportunity = new BmoOpportunity();
						     PmOpportunity pmOpportunity = new PmOpportunity(sFParams);
						     
						     sql = "SELECT oppo_opportunityid FROM opportunities WHERE oppo_saledate IS NOT NULL; ";
						     pmConn2.doFetch(sql);
						    	 
						     while (pmConn2.next()) {
				    		 	opportunityId = pmConn2.getInt("oppo_opportunityid");
						    	bmoOpportunity  = (BmoOpportunity)pmOpportunity.get(opportunityId);
						    	%>
						    	<%= i%> - <%= bmoOpportunity.getCode().toString() %> <br>
						    	<%
						    	// Asignar perido y fecha fiscal de acuerdo a la Fecha de Cierre de la oportunidad
								if (!bmoOpportunity.getSaleDate().toString().equals("")) {
									BmoCompany bmoCompany = new BmoCompany();
									PmCompany pmCompany = new PmCompany(sFParams);
									bmoCompany = (BmoCompany)pmCompany.get(bmoOpportunity.getCompanyId().toInteger());

									if (!bmoCompany.getFiscalPeriodType().toString().equals("") 
											&& bmoCompany.getFiscalStartMonth().toInteger() > 0) {
										bmoOpportunity.getFiscalPeriod().setValue("" + FlexUtil.getFiscalPeriod(sFParams.getDateFormat(), 
														bmoCompany.getFiscalStartMonth().toInteger(), 
														bmoCompany.getFiscalPeriodType().toString(), 
														bmoOpportunity.getSaleDate().toString()));
										
										bmoOpportunity.getFiscalYear().setValue(FlexUtil.getFiscalYear(sFParams.getDateFormat(), 
														bmoCompany.getFiscalStartMonth().toInteger(),
														bmoOpportunity.getSaleDate().toString()));
									}
								}
						    	
						    	pmOpportunity.saveSimple(pmConn, bmoOpportunity, bmUpdateResult);	
						    	i++;
						     }
						     
					 		 if (!bmUpdateResult.hasErrors()) {
						 		pmConn.commit();
							 	%>
							 	<h2><b><font color="green">&#10004; Proceso Terminado &#10004;</font></b></h2>
						 	 <%	
						 	 } %>
						<%= bmUpdateResult.errorsToString() %> 		
						<%
						}
					} catch (Exception e) {
 						throw new SFException(e.toString());
 					} finally {
 						pmConn.close();
 					}
				%>
			</td>
		</TR>
	</table>
</body>
</html>