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

<%@page import="com.flexwm.shared.fi.BmoPaccount"%>
<%@page import="com.flexwm.server.fi.PmPaccount"%>
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
	String title = "Actualizar Validación de Documentos CXP";
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
	<FORM action="flex_paccountsfiles.jsp" method="POST" name="listFilter">
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
				int i = 1;		
				PmConn pmConn = new PmConn(sFParams);
				PmConn pmConnFile = new PmConn(sFParams);
				PmPaccount pmPaccount = new PmPaccount(sFParams);
			    BmoPaccount bmoPaccount = new BmoPaccount();
			    BmUpdateResult bmUpdateResult = new BmUpdateResult();
			    int programId = sFParams.getProgramId(bmoPaccount.getProgramCode());
				
				String sql,sqlFile;
				String action = "";
				if (request.getParameter("action") != null) action = request.getParameter("action");
					
					if ( action.equals("update")) {
						
						sql = "SELECT pacc_paccountid FROM paccounts ORDER BY pacc_paccountid";
						
						
                        try{
                        	pmConn.open();
                        	pmConnFile.open();
                        	pmConn.doFetch(sql);
                          		
                        	while(pmConn.next()) {
                        	    	bmoPaccount = (BmoPaccount)pmPaccount.get(pmConn.getInt("pacc_paccountid"));
                        			sqlFile = "SELECT file_fileid FROM sffiles WHERE file_programid = " + programId +" AND file_foreignid = " + bmoPaccount.getId();
                        			
                         			pmConnFile.doFetch(sqlFile);
                         			if (pmConnFile.next()){
                         				bmoPaccount.getFile().setValue("1");
                         			} else {
                         				bmoPaccount.getFile().setValue("0");
                         			}
                         			pmPaccount.saveSimple(bmoPaccount, bmUpdateResult);
                         			if (bmUpdateResult.hasErrors())	{
                         				out.println("<br>");
        								out.println("| Error");
                         			}
                         			else {
                         				out.println("<br>");
                         				out.println("| Ok Registro No." + i);
                         			}
                         		i++;	
                        	}
                        	pmConn.close();
                        	pmConnFile.close();
                        		
                        }catch(Exception e){
                        		
                        }
					}

 %>

			</td>
		</TR>

	</table>



</body>
</html>