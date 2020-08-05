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

<%@page import="org.apache.jasper.tagplugins.jstl.core.ForEach"%>
<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@page import="com.flexwm.shared.fi.BmoRaccountLink"%>
<%@page import="com.flexwm.shared.fi.BmoRaccount"%>
<%@page import="com.flexwm.server.fi.PmRaccountLink"%>
<%@page import="com.flexwm.server.fi.PmRaccount"%>
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
	String title = "Proceso Para Cambiar Codigo de Pedido por Identificador de Pedido";
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
	<br>
	<FORM action="flex_raccountLink.jsp" method="POST" name="listFilter">
			    <input type="hidden" name="action" value="update">             	
                <tr class="">
                    <td align="center" colspan="4" height="35" valign="middle">
                        <input type="submit" value="Aceptar">
                    </td>
                </tr>			    
    </FORM>	

	<br>
	<table border="0"  width="100%" style="font-size: 12px">
		<TR>
			<td>
				<%
					String action = "";

					PmConn pmConn2 = new PmConn(sFParams);
					pmConn2.open();

					PmRaccountLink pmRaccountLink = new PmRaccountLink(sFParams);
					PmRaccount pmRaccount = new PmRaccount(sFParams);
					BmUpdateResult bmUpdateResult = new BmUpdateResult();

					PmConn pmConn = new PmConn(sFParams);
					String sql = "";

					int i = 0;
					if (request.getParameter("action") != null)
						action = request.getParameter("action");
					try {
						if (action.equals("update")) {

							pmConn.open();
							pmConn.disableAutoCommit();
							pmConn2.disableAutoCommit();
							
							sql = "SELECT * FROM raccountlinks WHERE ralk_raccountid is not null" ;
							pmConn.doFetch(sql);

							while (pmConn.next()) {
								BmoRaccountLink bmoRaccountLink = new BmoRaccountLink();
								bmoRaccountLink = (BmoRaccountLink) pmRaccountLink.get(pmConn.getInt("ralk_raccountlinkid"));
														
								//buscamos la cxc de ralk_foreignid
								BmoRaccount bmoRaccountForeing = new BmoRaccount();
								bmoRaccountForeing = (BmoRaccount) pmRaccount.get(pmConn.getInt("ralk_foreignid"));
								
								//buscamos la cxc origen
								BmoRaccount bmoRaccountId = new BmoRaccount();
								bmoRaccountId = (BmoRaccount) pmRaccount.get(pmConn.getInt("ralk_raccountid"));
								
								//Buscamos el orderid de ralk_foreignid
								sql = "SELECT orde_orderid FROM orders where orde_orderid = '" + bmoRaccountForeing.getOrderId() + "'";
								pmConn2.doFetch(sql);
								if (pmConn2.next()) {
									int orderId = pmConn2.getInt("orde_orderid");
									bmoRaccountLink.getOrderId().setValue(orderId);
								}
								//Buscamos el ordercode del origen
								sql = "SELECT orde_code FROM orders where orde_orderid = '" + bmoRaccountId.getOrderId() + "'";
								pmConn2.doFetch(sql);
								if (pmConn2.next()) {
									String orderId = pmConn2.getString("orde_code");
									bmoRaccountLink.getOrderCode().setValue(orderId);
								}
								
								pmRaccountLink.saveSimple(pmConn, bmoRaccountLink, bmUpdateResult);
								
								i++;
							}
							if (!bmUpdateResult.hasErrors())
								pmConn.commit();
							else
				%> <br> <%=bmUpdateResult.errorsToString()%> <br>
				-------------------------------------<br> Registros Procesados: <%=i%> <%
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