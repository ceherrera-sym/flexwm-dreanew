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

<%@page import="com.flexwm.shared.BmoFlexConfig"%>
<%@page import="com.flexwm.shared.fi.BmoRaccountType"%>
<%@page import="com.flexwm.shared.fi.BmoRaccount"%>
<%@page import="com.flexwm.server.fi.PmRaccount"%>
<%@page import="com.symgae.shared.BmFieldOption"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@page import="com.symgae.shared.SFException"%>
<%@include file="../inc/login.jsp"%>

<html>
<%
	// Imprimir
	String print = "0";
	if ((String) request.getParameter("print") != null)
		print = (String) request.getParameter("print");

	// Exportar a Excel
	String exportExcel = "0";
	if ((String) request.getParameter("exportexcel") != null)
		exportExcel = (String) request.getParameter("exportexcel");
	if (exportExcel.equals("1")) {
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "inline; username=symgf_report.xls");
	}
	
	// Obtener variables url
	String action = "", paymentStatus = "";
	if (request.getParameter("paymentStatus") != null)
		paymentStatus = request.getParameter("paymentStatus");
	if (request.getParameter("action") != null) 
		action = request.getParameter("action");
	
	//System.out.println("estatus: " +paymentStatus);
%>

<%
	// Inicializar variables
	String title = "Proceso Colocar usuario de Cobranza en las CxC";
%>

<head>
<title>:::<%=appTitle%>:::
</title>
<link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%=defaultCss%>">
</head>

<body class="default">

	<table border="0"  width="100%">
		<tr>
			<td align="left" rowspan="2" valign="top"><img border="0"
				width="<%=SFParams.LOGO_WIDTH%>"
				height="<%=SFParams.LOGO_HEIGHT%>"
				src="<%=sFParams.getMainImageUrl()%>"></td>
			<td class="reportTitle" align="left"><%=title%></td>
		</tr>
		<tr>
			<td class="reportDate" align="right">Creado: <%=SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat())%>
				por: <%=sFParams.getLoginInfo().getEmailAddress()%>
			</td>
		</tr>
	</table>

	<% if (action.equalsIgnoreCase("")) { %>
	<table width="100%" height="50%" border="0" align="center"  class="detailStart">	
	       
	    <TR>
	        <td colspan="3">  
	            <TABLE cellpadding="0" cellspacing="0" border="0"  width="80%">
				    <FORM action="?" method="POST" name="listFilter">
				    <input type="hidden" name="action" value="update">
				    <input type="hidden" name="paymentStatus" value="<%= paymentStatus %>">	
				    <tr class="reportSubTitle">
				        <td colspan="1" align="left" width="99%" height="35" class="reportCellEven">
				        <%
			        	if (paymentStatus.equals("")) {
				        %>
				          <li>Si se quiere afectar solo CxC con Estatus de Pago 'Pago Pendiente' ó 'Pago Total' 
				          colocar esto después de la URL "...flexwm.com"<br>
				          	<b>/batch/flexwm_racc_usercollector.jsp?paymentStatus=?</b><br>
				          	dónde el último parámetro(?) es: P = Pago Pendiente y T = Pago Total, colocar la letra correspondiente.
				          	<br> 
				          		Ejemplo 1:<br>
				          		...flexwm.com/batch/flexwm_racc_usercollector.jsp?paymentStatus=P
				          	<br>
				          		Ejemplo 2:<br>
				          		...flexwm.com/batch/flexwm_racc_usercollector.jsp?paymentStatus=T
				          </li>          
		          <%	} else if (paymentStatus.equals("" + BmoRaccount.PAYMENTSTATUS_PENDING)) { %>
		          			<li>El Estatus de Pago es Pendiente</li>
		          <%	} else if (paymentStatus.equals("" + BmoRaccount.PAYMENTSTATUS_TOTAL)) { %>
		         			 <li>El Estatus de Pago es Total</li>
		          <%	} %>
				        </td>
				    </tr> 
	                <tr class="">
	                    <td align="center" colspan="4" height="35" valign="middle">
	                    	<input class="formSaveButton" type="submit" value="SIGUIENTE">
	                    </td>
	                </tr>			    
	                </FORM>								
	            </TABLE>
	        </TD>
	    </TR>
	</TABLE>
<% 
	} else if (action.equals("update")) {
	%>
	<table border="0"  width="100%" style="font-size: 12px">
		<TR>
			<td>
				<%

					BmUpdateResult bmUpdateResult = new BmUpdateResult();
					// Conexiones
					PmConn pmConn = new PmConn(sFParams);
					PmConn pmConnSqlRacc = new PmConn(sFParams);
					PmConn pmConnSqlUser = new PmConn(sFParams);

					PmRaccount pmRaccount = new PmRaccount(sFParams);
					BmoRaccount bmoRaccount = new BmoRaccount();
					BmoRaccountType bmoRaccountType = new BmoRaccountType();

					try {
						pmConn.open();
						pmConnSqlRacc.open();
						pmConnSqlUser.open();
						pmConn.disableAutoCommit();

						// Traer grupo  de cobranza desde conf.
						int collectGroupid = 0; 
						collectGroupid = ((BmoFlexConfig)sFParams.getBmoAppConfig()).getCollectProfileId().toInteger();
						
						String cxc = "CxC ";
						if (paymentStatus.equals("")) cxc += "Todas";
						if (paymentStatus.equals("" + BmoRaccount.PAYMENTSTATUS_PENDING)) cxc += " Pago Pendiete";
						if (paymentStatus.equals(""+ BmoRaccount.PAYMENTSTATUS_TOTAL)) cxc += " Pago Total";
						%>
						<%= cxc %><br>
						<%
						// Aplicar proceso si existe grupo de cobranza en configuracion flex
						if (collectGroupid > 0) {

							// CXC que sean de categoria pedido, esten en un pedido, y no tengan usuario de cobranza
							String sql = " SELECT racc_raccountid, racc_code, racc_invoiceno, racc_collectuserid, orde_wflowid, orde_code " 
							+ " FROM raccounts "
							+ " LEFT JOIN raccounttypes ON (ract_raccounttypeid = racc_raccounttypeid) "
							+ " LEFT JOIN orders ON (orde_orderid = racc_orderid) "
							+ " WHERE ract_category = '" + BmoRaccountType.CATEGORY_ORDER + "' "
							+ " AND racc_orderid  > 0 ";
							if (!paymentStatus.equals(""))
								sql += " AND racc_paymentstatus = '" + paymentStatus + "' ";
							
							sql += " AND racc_collectuserid IS NULL " 
							+ " ORDER BY racc_orderid DESC, racc_raccountid ASC LIMIT 1000";
						
							System.out.println("sql1: " +sql);
							pmConnSqlRacc.doFetch(sql);
							
							int i = 0, collectorUserId = 0;
							while (pmConnSqlRacc.next()) {
								// Obtener la CxC
								bmoRaccount = (BmoRaccount)pmRaccount.get(pmConnSqlRacc.getInt("racc_raccountid"));

								// Usuario de cobranza del pedido, obtenerlos de los usuarios de flujo del flujo
								sql = " SELECT wflu_userid, user_code FROM wflowusers "
										+ " LEFT JOIN users ON (user_userid = wflu_userid) "
										+ " WHERE wflu_profileid = " + collectGroupid  
										+ " AND wflu_wflowid = " + pmConnSqlRacc.getInt("orde_wflowid") 
										+ " ORDER BY wflu_wflowuserid ASC";

										System.out.println("sql2: " +sql);
										pmConnSqlUser.doFetch(sql);

										if (pmConnSqlUser.next())  {
											collectorUserId = pmConnSqlUser.getInt("wflu_userid");

											i++;
											%>
											<br>
											<br>
											<!-- 
												Mostrar registros: clave pedido, clave cxc, factura cxc, estatus de pago, usuario que aplicado
											 -->
											<%= i%> : <%= pmConnSqlRacc.getString("orde_code") %> 
											: <%= bmoRaccount.getCode().toString()%> : <%= bmoRaccount.getInvoiceno().toString()%> : <%= bmoRaccount.getPaymentStatus().getSelectedOption().getLabel()%>
											<br>
											<%
											// Aplicar usuario cobranza sacado de los usuarios de flujo del pedido
											bmoRaccount.getCollectorUserId().setValue(collectorUserId);
											%>
											Usuario asignado Cobranza: 
											(ID:<%= bmoRaccount.getCollectorUserId().toString() %>) <%= pmConnSqlUser.getString("user_code")%>
											<%
												pmRaccount.saveSimple(pmConn, bmoRaccount, bmUpdateResult);
										}
								}
								
								if (!bmUpdateResult.hasErrors())
									pmConn.commit();
								else {
									%><%= bmUpdateResult.errorsToString()%> <%
								}
							}

				 	} catch (Exception e) {
				 		throw new SFException(e.toString());
				 	} finally {
				 		pmConn.close();
				 		pmConnSqlRacc.close();
				 		pmConnSqlUser.close();
				 	}
 %>
			</td>
		</TR>

	</table>
	<%
	}
		if (print.equals("1")) {
	%>
	<script>
		//window.print();
	</script>
	<%
		}
	%>
</body>
</html>