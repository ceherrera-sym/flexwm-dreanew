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

<%@page import="com.flexwm.server.fi.PmRaccount"%>
<%@page import="com.flexwm.shared.fi.BmoRaccount"%>
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
	<FORM action="flex_copy.jsp" method="POST" name="listFilter">
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
					int i = 0;			
					PmConn pmConn = new PmConn(sFParams);
					
					
					BmUpdateResult bmUpdateResult = new BmUpdateResult();
					String sql;
					String action = "";
					if (request.getParameter("action") != null) action = request.getParameter("action");
					try {
						if (action.equals("update")) { 
						PmRaccount pmRaccount = new PmRaccount(sFParams);
						pmConn.open();
						pmConn.disableAutoCommit();
						sql = "select * from raccounts where racc_reminddate is null ";
						System.out.println("sql1: "+ sql);
						pmConn.doFetch(sql);
						while (pmConn.next()) {
							BmoRaccount bmoRaccount = new BmoRaccount();
							bmoRaccount = (BmoRaccount) pmRaccount.get(pmConn.getInt("racc_raccountid"));
							if(bmoRaccount.getRemindDate().equals("") || bmoRaccount.getRemindDate().toString() == null){
								if(!(bmoRaccount.getDueDate().toString().equals(""))){
									bmoRaccount.getRemindDate().setValue(bmoRaccount.getDueDate().toString());
									pmRaccount.saveSimple(bmoRaccount, bmUpdateResult);
									%>
									<br> -------------------------------------<br>
									CXC: <%=bmoRaccount.getCode()%> | <%=bmoRaccount.getRemindDate()%>
									<%
									if (bmUpdateResult.hasErrors())		 
										out.println("| Error");
									else
										out.println("| Ok");
									i++;
									
									}
								}
							}
						if (!bmUpdateResult.hasErrors())							
							pmConn.commit();
						else
							out.println(bmUpdateResult.errorsToString());
						%> 
				-------------------------------------<br> Registros Procesados:
				<%=i%> <%
 	
	}} catch (Exception e) {
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