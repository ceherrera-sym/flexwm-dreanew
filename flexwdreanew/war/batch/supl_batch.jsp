<%@page import="com.flexwm.shared.op.BmoSupplier"%>
<%@page import="com.flexwm.server.op.PmSupplier"%>
<%@page import="com.flexwm.shared.op.BmoProductPrice"%>
<%@include file="../inc/imports.jsp" %>
<%@page import="com.flexwm.server.op.PmProduct"%>
<%@page import="com.flexwm.shared.op.BmoProduct"%>
<%@page import="com.flexwm.server.op.PmProductPrice"%>
<%@page import="com.flexwm.shared.op.BmoProduct"%>
<%@page import="java.util.StringTokenizer"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/login.jsp" %>
<%
	// Variables
	String programTitle = "Actualizacion de información de Proveedores";
	String errorLabel = request.getParameter("errorLabel");
	String errorText = request.getParameter("errorText");
	String errorException = request.getParameter("errorException");
	String errorSave = request.getParameter("errorsave");
	String populateData = "", action = "";
	if (request.getParameter("populateData") != null) 
		populateData = request.getParameter("populateData");
	if (request.getParameter("action") != null) 
		action = request.getParameter("action");
%>

<html>
<head>
<title>:::<%= programTitle %>:::</title>
<link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>">
<meta charset=utf-8>
</head>
<body class="default">
<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr>
		<td align="left" width="" rowspan="5" valign="top">
		    <img border="0" width="<%=SFParams.LOGO_WIDTH%>" height="<%=SFParams.LOGO_HEIGHT%>" src="<%= sFParams.getMainImageUrl() %>" >
		</td>
		<td colspan="" class="reportTitle">
		    &nbsp;<%= programTitle %>
		</td>
	</tr>
</table>

<% if (action.equalsIgnoreCase("")) { %>
	<table width="100%" height="50%" border="0" align="center"  class="detailStart">	
	    <tr class="reportSubTitle">
	        <td colspan="1" align="left" width="99%" height="35" class="reportCellEven">
	          <li>Se recomienda elaborar preparaci&oacute;n en una hoja de calculo, seleccionar y pegar en el recuadro siguiente.</li>
	          <li><b>Los campos con '*' son oblitarorios. Colocar la palabra "empty" para los campos vacíos.</b></li>          
	          <li>Formato (| = celda): Clave Proveedor * | RFC </li>
	        </td>
	    </tr>    
	    <TR>
	        <td colspan="3">  
	            <TABLE cellpadding="0" cellspacing="0" border="0"  width="80%">
				    <FORM action="?" method="POST" name="listFilter">
					    <input type="hidden" name="action" value="revision">
		             	<tr height="35">
						    <td colspan="10" align="center" width="100%">
								<textarea name="populateData" width="100%" cols="100" rows="30"></textarea>
					        </td>
					    </tr>
		                <tr >
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
	} else if (action.equalsIgnoreCase("revision")) {
// 		PmConn pmConn = new PmConn(sFParams);
// 		pmConn.open();
		
		PmConn pmConn2 = new PmConn(sFParams);
		pmConn2.open();
		
		try {
			String msg = "";
			String sql = "";
			String s = "";
			int i = 1;		
			StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
			
			String supl_code = "";
			String supl_name = "";
			String supl_rfc = "";
			String errors = "";
		%>
		<table width="80%" border="0" align="center">
		    <TR>
		        <td colspan="4">
		            <TABLE border="1"  width="90%">               
		            	<tr valign="middle" align="center" class="">
		                    <td class="reportHeaderCell">&nbsp;#</td>
		                    <td class="reportHeaderCell">Clave Proveedor</td>
		                   	<td class="reportHeaderCell">Nombre</td>
		                    <td class="reportHeaderCell">RFC</td>
		                    <td class="reportHeaderCell">Errores</td>
		                </TR>
		            <%           
		        		while (inputData.hasMoreTokens() && i < 2000) {
		        			errors = "";
		        			s = inputData.nextToken();
		        			StringTokenizer tabs = new StringTokenizer(s, "\t");        			
		        			
		        			// Recuperar valores		
							supl_code = tabs.nextToken();
							supl_rfc = tabs.nextToken();
							
							// Formatear; quitar espacios
		        			supl_code = supl_code.trim();
		        			supl_rfc = supl_rfc.trim();			
	
		        			// Validar Producto
		        			if (!supl_code.equalsIgnoreCase("empty")) {
			        			sql = " SELECT supl_code, supl_name FROM suppliers " +
			          			      " WHERE supl_code LIKE '" + supl_code + "'";
			        			pmConn2.doFetch(sql);
			        			if (!pmConn2.next()) {
			        				errors = "El Proveedor no Existe: " + supl_code;
			        			} else {
			        				supl_name = pmConn2.getString("supl_name");
			        			}
		        			}
		        			
		    		%>
		    				<TR class="reportCellEven" width="100%">
		    					<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, supl_code, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, supl_name, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, supl_rfc, BmFieldType.STRING)%>
		    					
		    				    <td><font color="red"><%= errors %></font></prcp_price>
		    				</TR>
		    		<%
		    			i++;    		
		        	}
		            %>
			</TABLE>
		
			<TABLE cellpadding="0" cellspacing="0" border="0"  width="100%">
				<FORM action="?" method="POST" name="listFilter">	
				<input type="hidden" name="action" value="populate">
				<input type="hidden" name="populateData" value="<%= populateData %>">			
				<tr class="">
				    <td align="center" colspan="4" height="35" valign="middle">
				    <% if (errors.equals("")) { %>
				        <input type="submit" class="formSaveButton" value="GUARDAR">
				    <% } %>    
				    </td>
				</tr>			    
				</FORM>								
			</TABLE>
		
		<% 
		} catch (Exception e) {
// 			pmConn.rollback();
			throw new SFException(e.toString());
			
		} finally {
// 			pmConn.close();
			pmConn2.close();
		}
		
	} else if (action.equals("populate")) {
		BmUpdateResult bmUpdateResult = new BmUpdateResult();
		
		PmConn pmConn = new PmConn(sFParams);
		pmConn.open();
		
		PmConn pmConn2 = new PmConn(sFParams);
		pmConn2.open();
		
		try {
			pmConn.disableAutoCommit();
			String s = "";

			String supl_code = "";
			String supl_name = "";
			String supl_rfc = "";
			int supl_supplierid = 0;
			
			PmSupplier pmSupplier = new PmSupplier(sFParams);
			BmoSupplier bmoSupplier = new BmoSupplier();
					
			StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
			int i = 1;
			while (inputData.hasMoreTokens() && i < 20000) {
				String sql = "";
				s = inputData.nextToken();
				StringTokenizer tabs = new StringTokenizer(s, "\t");

    			// Recuperar valores
				supl_code = tabs.nextToken();
				supl_rfc = tabs.nextToken();
				
				// Formatear; quitar espacios
       			supl_code = supl_code.trim();
       			supl_rfc = supl_rfc.trim();	
       			
	       		// Obtener proveedor
	   			if (!supl_code.equalsIgnoreCase("empty")) {
	       			sql = " SELECT supl_supplierid, supl_code FROM suppliers " +
	         			      " WHERE supl_code LIKE '" + supl_code + "'";
	       			pmConn2.doFetch(sql);
	       			if (pmConn2.next()) {
	       				supl_supplierid = pmConn2.getInt("supl_supplierid");
	       			}
	   			}
       		
	       		// Actualizar rfc del proveedor
	       		bmoSupplier = (BmoSupplier)pmSupplier.get(pmConn, supl_supplierid);
	       		if (supl_rfc.equalsIgnoreCase("empty"))
	       			bmoSupplier.getRfc().setValue("");
	       		else
	       			bmoSupplier.getRfc().setValue(supl_rfc);
	       		
	       		// Llenar dato si esta nulo(es campo oblitario)
	       		if (!(bmoSupplier.getSendEmail().toInteger() > 0))
	       			bmoSupplier.getSendEmail().setValue(0);
	       		pmSupplier.saveSimple(pmConn, bmoSupplier, bmUpdateResult);
			}
			
			if (!bmUpdateResult.hasErrors()) {
				pmConn.commit();
			}
		
		} catch (Exception e) {
			pmConn.rollback();
			throw new SFException(e.toString());
			
		} finally {
			pmConn.close();
			pmConn2.close();
		}
		
		response.sendRedirect("supl_batch.jsp?action=complete&errorsave=" + bmUpdateResult.errorsToString());
			
	}  else if (action.equals("complete")) { %>
			<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
			<tr>		
				<td colspan="4" align="center" class="reportTitle">
				    &nbsp;
				    <% if (errorSave.equals("")) { %>
						Datos Cargados OK.
					<% } else { %>
						Existen Errores de Carga: <%= errorSave %>
					<% } %>
					<br>
					<a href="?">Reiniciar</a>
				</td>
			</tr>
			</table>
	<% } %>
</body>
</html>