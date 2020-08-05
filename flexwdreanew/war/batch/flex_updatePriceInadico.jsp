<!--  
/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Paulina Padilla Guerra
 * @version 2018-11
 */ -->

<%@page import="com.flexwm.server.co.PmOrderPropertyTax"%>
<%@page import="com.flexwm.shared.co.BmoOrderPropertyTax"%>
<%@page import="com.flexwm.server.ar.PmPropertyRental"%>
<%@page import="com.flexwm.shared.ar.BmoPropertyRental"%>
<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@page import="com.flexwm.server.op.PmOrder"%>
<%@page import="com.flexwm.shared.fi.BmoRaccount"%>
<%@page import="com.flexwm.server.fi.PmRaccount"%>
<%@page import="com.flexwm.server.cm.PmCustomerStatus"%>
<%@page import="com.flexwm.shared.cm.BmoCustomerStatus"%>
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
	String title = "Proceso Para Pasar Precios";
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
	<FORM action="flex_updatePriceInadico.jsp" method="POST" name="listFilter">
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
				String action = "";
				if (request.getParameter("action") != null) action = request.getParameter("action");
				try {
					if (action.equals("update")) { 
						PmPropertyRental pmPropertyRental = new PmPropertyRental(sFParams);
						BmoPropertyRental bmoPropertyRental = new BmoPropertyRental();	
						
						BmoOrder bmoOrder =new BmoOrder();
						PmOrder pmOrder = new PmOrder(sFParams);
						
						BmoOrderPropertyTax bmoOrderPropertyTax = new BmoOrderPropertyTax();
						PmOrderPropertyTax pmOrderPropertyTax = new PmOrderPropertyTax(sFParams);
					
						BmUpdateResult bmUpdateResult = new BmUpdateResult();						
						PmConn pmConn = new PmConn(sFParams);
						PmConn pmConn2 = new PmConn(sFParams);
						
						double amount = 0;
						int i = 0;
						double price = 0;
						String sql;
						pmConn.open();
						pmConn2.open();
						pmConn.disableAutoCommit();
						pmConn2.disableAutoCommit();
						sql = "SELECT prrt_propertiesrentid FROM propertiesrent order by prrt_propertiesrentid;";
						pmConn2.doFetch(sql);
						
						while (pmConn2.next()) {	
							bmoPropertyRental = (BmoPropertyRental)pmPropertyRental.get(pmConn2.getInt("prrt_propertiesrentid"));
							System.out.println("Contrato:"+bmoPropertyRental.getCode().toString());
							bmoOrder = (BmoOrder) pmOrder.get(bmoPropertyRental.getOrderId().toInteger());
							bmoOrder.getLockStart().setValue(bmoPropertyRental.getRentalScheduleDate().toString());
							bmoOrder.getLockEnd().setValue(bmoPropertyRental.getRentIncrease().toString());
							
							bmoOrderPropertyTax  = (BmoOrderPropertyTax) pmOrderPropertyTax.getBy(bmoOrder.getId(), bmoOrderPropertyTax.getOrderId().getName());
							price = bmoOrderPropertyTax.getAmount().toDouble();
							
							
							bmoOrderPropertyTax.getPrice().setValue(bmoPropertyRental.getCurrentIncome().toDouble());
							bmoOrderPropertyTax.getQuantity().setValue(pmPropertyRental.getMoths(bmoPropertyRental, bmUpdateResult));
							amount = bmoOrderPropertyTax.getQuantity().toInteger() * bmoOrderPropertyTax.getPrice().toDouble();
							bmoOrderPropertyTax.getAmount().setValue(amount);
							
							if(price == bmoOrderPropertyTax.getAmount().toDouble()){ %>
								<br> -------------------------------------<br>
								Contrato: <%=bmoPropertyRental.getCode().toString() %>|ok
								<%
								pmOrder.saveSimple(pmConn,bmoOrder, bmUpdateResult);
								pmOrderPropertyTax.saveSimple(pmConn, bmoOrderPropertyTax, bmUpdateResult);
								
								i++;
							}
							else{%>
							<br> -------------------------------------<br>
							<% pmOrder.saveSimple(pmConn,bmoOrder, bmUpdateResult);%>
								Contrato: <%=bmoPropertyRental.getCode().toString() %>|Error <%
							}
							
						}
						if (!bmUpdateResult.hasErrors())							
							pmConn.commit();
						else
							out.println(bmUpdateResult.errorsToString());
						%>
						<br> ------------------- <br> 
						Registros procesados: <%=i%>
						<%
					}
				}
				catch (Exception e) {
			 		throw new SFException(e.toString());
				}					
 %>

			</td>
		</TR>

	</table>



</body>
</html>