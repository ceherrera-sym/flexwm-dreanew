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

<%@page import="com.flexwm.shared.op.BmoOrderDelivery"%>
<%@page import="com.flexwm.server.op.PmOrderDelivery"%>
<%@page import="org.apache.jasper.tagplugins.jstl.core.Catch"%>
<%@page import="com.flexwm.shared.fi.BmoRaccount"%>
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
	String title = "Actualizar Creador de Envio de Pedido";
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
	</table>
	<br>
	<FORM action="flex_ordedeliveryusercreate.jsp" method="post" name="listFilter">
		<input type="hidden" name="action" value="update">
		<tr class="">
			<td align="center" colspan="4" height="35" valign="middle"><input class="formSaveButton"
				type="submit" value="Aceptar"></td>
		</tr>
	</FORM>

	<br>
	<table border="0" width="100%" style="font-size: 12px">
		<TR>
			<td>
				<%	
				;
					int i = 1;		
					PmConn pmConn = new PmConn(sFParams);					
					PmOrderDelivery pmOrderDelivery = new PmOrderDelivery(sFParams);
				    BmoOrderDelivery bmoOrderDelivery = new BmoOrderDelivery();
				    BmUpdateResult bmUpdateResult = new BmUpdateResult();
				   
					
					String sql,sqlFile;
					String action = "";
					if (request.getParameter("action") != null) action = request.getParameter("action");
						
						if ( action.equalsIgnoreCase("update")) {
							
							sql = "SELECT odly_orderdeliveryid FROM orderdeliveries ORDER BY odly_orderdeliveryid";
                            try{
                            	pmConn.open();
                            	
                            	pmConn.doFetch(sql);
                              		
                            	while(pmConn.next()) {
                            			String displayName = "";
                            			bmoOrderDelivery = (BmoOrderDelivery)pmOrderDelivery.get(pmConn.getInt("odly_orderdeliveryid"));
                            		
                            			
                            			if (bmoOrderDelivery.getUserCreateId().toInteger() > 0){
                            				BmoUser boBmoUser = new BmoUser();
                            			    PmUser pmUser = new PmUser(sFParams);
                            				bmoUser = (BmoUser)pmUser.get(bmoOrderDelivery.getUserCreateId().toInteger());
                            				displayName = bmoUser.getFirstname()+" "+bmoUser.getFatherlastname()+" "+bmoUser.getMotherlastname();
                            			
                             				bmoOrderDelivery.getUserCreate().setValue(displayName);
                             				pmOrderDelivery.saveSimple(bmoOrderDelivery, bmUpdateResult);
                             				if (bmUpdateResult.hasErrors())	{
                             					out.println("<br>");
            									out.println("| Error"+ bmUpdateResult.errorsToString());
                             				}
                             				else {
                             					out.println("<br>");
                             					out.println("| Ok Registro No." + i);
                             				}
                             					
                            			}else{
                            				out.println("<br>");
                            				out.println("| Error Registro No." + i +" "+ bmoOrderDelivery.getCode() + " El registro no tiene Creador");
                            				
                            			}
                            			i++;
                            		}
                            	pmConn.close();
                            	
                            		
                            }catch(Exception e){
                            		
                            }
						}
 %>

			</td>
		</TR>

	</table>



</body>
</html>