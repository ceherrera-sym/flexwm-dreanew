<%@page import="com.flexwm.server.wf.PmWFlowUser"%>
<%@page import="com.flexwm.shared.wf.BmoWFlowUser"%>
<%@page import="com.symgae.shared.GwtUtil"%>
<%@page import="com.symgae.shared.SQLUtil"%>
<%@include file="../inc/imports.jsp"%>
<%@page import="com.flexwm.server.op.*"%>
<%@page import="com.flexwm.shared.op.*"%>
<%@page import="com.flexwm.server.fi.*"%>
<%@page import="com.flexwm.shared.fi.*"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.server.cr.*"%>
<%@page import="com.flexwm.shared.cr.*"%>
<%@page import="com.flexwm.server.*"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/login.jsp"%>

<%
	String programTitle = "Borrar Usuarios de Flujo duplicados";
	//Obtener variables url
// 	int idStart =0, idEnd = 0;
// 	if (request.getParameter("idStart") != null) idStart = Integer.parseInt(request.getParameter("idStart"));
// 	if (request.getParameter("idEnd") != null) idEnd = Integer.parseInt(request.getParameter("idEnd"));
%>

<html>
<head>
<title>:::<%= programTitle %>:::
</title>
<link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>">
<meta charset=utf-8>
</head>
<body class="default">
	<table border="0" cellspacing="0" width="100%" cellpadding="0"
		style="font-size: 12px">
		<tr>
			<td align="left" width="5%" rowspan="5" valign="top">
				<img border="0" width="<%=SFParams.LOGO_WIDTH%>" height="<%=SFParams.LOGO_HEIGHT%>" src="<%= sFParams.getMainImageUrl() %>" >
			</td>
			<td colspan="" class="reportTitle">&nbsp;<%= programTitle %>
			</td>
			<td class="reportDate" align="right">
	            Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
	        </td>
		</tr>
	</table>
	<table border="0" cellspacing="0" width="100%" cellpadding="0">
	
	<% 
		PmConn pmConn = new PmConn(sFParams);
		pmConn.open();
		pmConn.disableAutoCommit();

		BmUpdateResult bmUpdateResult = new BmUpdateResult();
		PmConn pmConnSQL = new PmConn(sFParams);
		pmConnSQL.open();

		PmConn pmConnSQL2 = new PmConn(sFParams);
		pmConnSQL2.open();
		
		BmoOrder bmoOrder = new BmoOrder();
		BmoWFlowUser bmoWFlowUser = new BmoWFlowUser();
		PmWFlowUser pmWFlowUser = new PmWFlowUser(sFParams);
		
		PmObject pmObject = new PmObject(sFParams);
		
		// Sacar usuarios de flujo repetidos de los flujos
		String sql = " SELECT COUNT(wflu_userid) AS cu, wflu_userid, user_code, wflw_wflowid, wflu_profileid, prof_name, wflw_code "
					+ " FROM wflowusers "
					+ " LEFT JOIN wflows ON (wflw_wflowid = wflu_wflowid) "
					+ " LEFT JOIN `profiles` ON (prof_profileid = wflu_profileid) "
					+ " LEFT JOIN users ON (user_userid = wflu_userid) "
					+ " WHERE wflu_wflowuserid > 0 "
					//+ " AND wflw_code = 'PED-0339' "
					//+ " wflw_callercode = '" + bmoOrder.getProgramCode() + "'  "
					//+ " AND orde_status = '" + BmoOrder.STATUS_REVISION + "' "
					+ " GROUP BY wflw_wflowid, wflu_profileid, wflu_userid "
					+ " HAVING cu > 1 "
					+ " ORDER BY wflw_wflowid ";
		
		pmConnSQL.doFetch(sql);
		int a = 1;
		while (pmConnSQL.next()) {
			%><br>
			<%= a%>|<%= pmConnSQL.getString("wflw_code")%> : <%= pmConnSQL.getString("prof_name")%> : <%= pmConnSQL.getString("user_code")%>
			<%
			// Obtener a detalle los usuarios duplicados
			sql =  "SELECT wflu_wflowuserid,  wflu_required, wflu_wflowuserid FROM wflowusers "
					+ " WHERE wflu_wflowid = " + pmConnSQL.getInt("wflw_wflowid")
					+ " AND wflu_profileid = " + pmConnSQL.getInt("wflu_profileid")
					+ " AND wflu_userid = " + pmConnSQL.getInt("wflu_userid")
					+ " ORDER BY wflu_required";
			
			pmConnSQL2.doFetch(sql);
			boolean required = true; 
			while (pmConnSQL2.next()) {
				bmoWFlowUser = (BmoWFlowUser)pmWFlowUser.get(pmConnSQL2.getInt("wflu_wflowuserid"));
				System.out.println("id: " + bmoWFlowUser.getId());
				// Si hay un usuario duplicado que no es requerido, lo borra y sale del ciclo
				if (!(pmConnSQL2.getInt("wflu_required") > 0)) {
					required = false;
					pmObject.delete(pmConn, bmoWFlowUser, bmUpdateResult);
					break;
				}
			}
			// Borrar el ultimo registro Si no hay ningun requerido
			if (required && bmoWFlowUser.getId() > 0) {
				pmObject.delete(pmConn, bmoWFlowUser, bmUpdateResult);
			}
			a++;
		}
			
		
		if (!bmUpdateResult.hasErrors())  pmConn.commit();
		
		pmConn.close();
		pmConnSQL.close();
		pmConnSQL2.close();
	%>
	</table>
</body>
</html>
