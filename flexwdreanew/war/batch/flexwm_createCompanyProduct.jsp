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

<%@page import="com.flexwm.shared.op.BmoProductCompany"%>
<%@page import="com.flexwm.server.op.PmProductCompany"%>
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
	String title = "Proceso Crear Empresas en el Producto";
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
	<FORM action="flexwm_createCompanyProduct.jsp" method="POST" name="listFilter">
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
							 
						     int i = 1, productId = 0;
						     
						     BmoProduct bmoProduct = new BmoProduct();
						     PmProduct pmProduct = new PmProduct(sFParams);
						     PmProductCompany pmProductCompany = new PmProductCompany(sFParams);

						     sql = "SELECT prod_productid FROM products WHERE prod_productid > 0 "
						    		 + " AND prod_productid NOT IN (SELECT prcp_productid FROM productcompanies) ";
						     pmConn2.doFetch(sql);
						     System.out.println("sql: "+sql);
						     while (pmConn2.next()) {
							
				    		 	productId = pmConn2.getInt("prod_productid");
				    		 	bmoProduct  = (BmoProduct)pmProduct.get(productId);
						    	%>
						    	<%= i%> - <%= bmoProduct.getCode().toString() %> <br>
						    	<%
							    BmoProductCompany bmoProductCompany = new BmoProductCompany();
						    	bmoProductCompany.getProductId().setValue(productId);
						    	bmoProductCompany.getCompanyId().setValue(54);
						    	pmProductCompany.saveSimple(pmConn, bmoProductCompany, bmUpdateResult);	
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