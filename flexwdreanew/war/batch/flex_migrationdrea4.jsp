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


<%@page import="com.symgae.server.PmConn"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@page import="com.symgae.shared.BmFilter"%>
<%@page import="com.symgae.shared.BmObject"%>

<%@page import="com.flexwm.shared.fi.BmoPaccountType"%>
<%@page import="com.flexwm.shared.fi.BmoPaccount"%>
<%@page import="com.flexwm.server.fi.PmPaccount"%>
<%@page import="com.flexwm.shared.fi.BmoPaccountAssignment"%>
<%@page import="com.flexwm.server.fi.PmPaccountAssignment"%>
<%@page import="com.flexwm.shared.fi.BmoPaccountItem"%>
<%@page import="com.flexwm.server.fi.PmPaccountItem"%>

<%@page import="java.util.Iterator"%>
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
			<p>--------------------------------------------------Items para CxP Tipo Cargo --------------------------------------------------<p>
			<%
					PmConn pmConn4 = new PmConn(sFParams);
					BmUpdateResult bmUpdateResult4 = new BmUpdateResult();
					PmConn pmConnPaccount = new PmConn(sFParams);
					pmConnPaccount.open();
					try {
						pmConn4.open();
						pmConn4.disableAutoCommit();
						
						
						
						int i = 1;
									
						String sql4 =  " SELECT * FROM paccounts " + 
										  " LEFT JOIN paccounttypes ON(pact_paccounttypeid = pacc_paccounttypeid)" +
										  " LEFT JOIN paccountitems ON(pait_paccountid = pacc_paccountid)" +
										  " WHERE pact_type = '" + BmoPaccountType.TYPE_WITHDRAW + "'" +
										  " AND NOT pacc_paccountid in (SELECT pait_paccountid FROM paccountitems)";
						
						System.out.println("SQL CxP tipo Cargo: " +sql4);
						pmConnPaccount.doFetch(sql4);
						
						%>
						#
						&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;
						Id: 
						&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;
						<br>
						<% 
						int paccId = 0;
						while(pmConnPaccount.next()) {
							paccId = Integer.parseInt(pmConnPaccount.getString("pacc_paccountid"));
							%>
							<%= i%>
							&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;
							<%= paccId%>
							&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;
							<br>
							<% 
							
								BmoPaccount bmoPaccount = new BmoPaccount();
								PmPaccount pmPaccount = new PmPaccount(sFParams);
								if(paccId > 0)
									bmoPaccount = (BmoPaccount)pmPaccount.get(paccId);

								BmoPaccountItem bmoPaccountItem = new BmoPaccountItem();
								PmPaccountItem pmPaccountItem = new PmPaccountItem(sFParams);
								
								bmoPaccountItem.getName().setValue("Ajuste Autom.");	
								bmoPaccountItem.getQuantity().setValue(1);
								bmoPaccountItem.getPrice().setValue(bmoPaccount.getAmount().toDouble());
								bmoPaccountItem.getAmount().setValue(bmoPaccount.getAmount().toDouble());
								bmoPaccountItem.getPaccountId().setValue(bmoPaccount.getId());
	
								pmPaccountItem.saveSimple(pmConn4, bmoPaccountItem, bmUpdateResult4);

							i++;
						}
						pmConn4.commit();
						
					
				}catch (Exception e) {
					pmConn4.rollback();
					throw new SFException("Proceso Item Faltantes para CXP de Tipo Cargo: "+e.toString());
				}finally {
					pmConnPaccount.close();
					pmConn4.close();
				}
			%>

		</td>
	</tr>
</table>

  </body>
</html>
