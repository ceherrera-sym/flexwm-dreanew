<%@include file="../inc/imports.jsp" %>
<%@page import="com.flexwm.server.op.PmWhBox"%>
<%@page import="com.flexwm.shared.op.BmoWhBox"%>
<%@page import="com.flexwm.server.op.PmWhBoxTrack"%>
<%@page import="com.flexwm.shared.op.BmoWhBoxTrack"%>
<%@page import="java.util.StringTokenizer"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/login.jsp" %>
<%
	// Variables
	String programTitle = "Importacion de Cajas de Productos..";
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
<link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>">
<link rel="stylesheet" type="text/css" href="../css/flexwm.css">  
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
	          <li>Se recomienda elaborar preparaci&oacute;n en archivo de texto, seleccionar y pegar en el recuadro siguiente.</li>          
	          <li>Formato (| = tab): Clave Caja | Nombre Caja | Cantidad | Clave Producto | # Serie/Lote | Seccion Almacen | Validador (1)</li>
	        </td>
	    </tr>    
	    <TR>
	        <td colspan="3">  
	            <TABLE cellpadding="0" cellspacing="0" border="0"  width="80%">
				    <FORM action="?" method="POST" name="listFilter">
				    <input type="hidden" name="action" value="revision">
	             	<tr class="" height="35">
					    <td colspan="10" align="center" width="100%">
							<textarea name="populateData" width="100%" cols="100" rows="30"></textarea>
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
	} else if (action.equalsIgnoreCase("revision")) {
		PmConn pmConn = new PmConn(sFParams);
		pmConn.open();
		
		PmConn pmConn2 = new PmConn(sFParams);
		pmConn2.open();
		
		try {
			String msg = "";
			String sql = "";
			String s = "";
			int i = 1;		
			StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
				
			String whbx_code = "";
			String whbx_name = "";
			String whbt_quantity = "";
			String prod_code = "";
			String prod_name = "";
			String whtr_serial = "";
			String whse_name = "";	    

			String errors = "";
		%>
		<table width="80%" border="0" align="center"  class="">
		    <TR>
		        <td colspan="4">  
		            <TABLE cellpadding="0" cellspacing="0" border="1" rules=all width="90%">               
		            	<tr valign="middle" align="center" class="">
		                    <td class="reportHeaderCell">&nbsp;#</td>
		                    <td class="reportHeaderCell">Clave Caja</td>
		                    <td class="reportHeaderCell">Nombre Caja</td>
		                    <td class="reportHeaderCell">Cantidad</td>
		                    <td class="reportHeaderCell">Clave Producto</td>
							<td class="reportHeaderCell">Nombre Producto</td>
		                    <td class="reportHeaderCell"># Serie/Lote</td>
		                    <td class="reportHeaderCell">Seccion Almacen</td>
		                    <td class="reportHeaderCell">Errores</td>
		                </TR>
		            <%           
		        		while (inputData.hasMoreTokens() && i < 2000) {
		        			errors = "";
		        			s = inputData.nextToken();
		        			StringTokenizer tabs = new StringTokenizer(s, "\t");        			
		        			
		        			// Recuperar valores        		
		        			whbx_code = tabs.nextToken();
		        			whbx_name = tabs.nextToken();
		        			whbt_quantity = tabs.nextToken();
		        			prod_code = tabs.nextToken();
		        			whtr_serial = tabs.nextToken();
		        			whse_name = tabs.nextToken();		        			
	
		        			// Validar producto
		        			if (!whtr_serial.equals("empty")) {
			        			sql = " SELECT * FROM products " +
			          			      " WHERE prod_code like '" + prod_code + "'";
			        			pmConn2.doFetch(sql);
			        			if (!pmConn2.next()) {
			        				errors = "El Producto no Existe: " + prod_code;
			        			} else{
			        				prod_name = pmConn2.getString("prod_name");
			        			}
		        			}	
		        			
		        			// Validar serial
		        			if (!whtr_serial.equals("empty")) {
			        			sql = " SELECT * FROM whtracks " +
			          			      " WHERE whtr_serial like '" + whtr_serial + "'";
			        			pmConn2.doFetch(sql);
			        			if (!pmConn2.next()) {
			        				errors = "El # Serie/Lote no Existe: " + whtr_serial;
			        			}
		        			}	
		        			
		        			// Validar serial
		        			if (!whse_name.equals("empty")) {
			        			sql = " SELECT * FROM whsections " +
			          			      " WHERE whse_name like '" + whse_name + "'";
			        			pmConn2.doFetch(sql);
			        			if (!pmConn2.next()) {
			        				errors = "La Seccion de Almacen no Existe: " + whse_name + ", " + sql;
			        			}
		        			}	
		        			
		    		%>
		    				<TR class="reportCellEven" width="100%">
		    					<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, whbx_code, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, whbx_name, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, whbt_quantity, BmFieldType.STRING)%>
							<%=HtmlUtil.formatReportCell(sFParams, prod_code, BmFieldType.STRING)%>
							<%=HtmlUtil.formatReportCell(sFParams, prod_name, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, whtr_serial, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, whse_name, BmFieldType.STRING)%>
		    				    <td><font color="red"><%= errors %></font></td>
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
				pmConn.rollback();
				throw new SFException(e.toString());
				
			} finally {
				pmConn.close();
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
			
			String whbx_code = "";
			String whbx_name = "";
			String whbt_quantity = "";
			String prod_code = "";
			String whtr_serial = "";
			String whse_name = "";	  
			
			// Llaves foraneas
			int whbx_whboxid = 0;
			int prod_productid = 0;
			int whtr_whtrackid = 0;
			int whse_whsectionid = 0;
			
			PmWhBox pmWhBox = new PmWhBox(sFParams);
			BmoWhBox bmoWhBox = new BmoWhBox();
			
			PmWhBoxTrack pmWhBoxTrack = new PmWhBoxTrack(sFParams);
			BmoWhBoxTrack bmoWhBoxTrack = new BmoWhBoxTrack();
					
			StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
			int i = 1;
			while (inputData.hasMoreTokens() && i < 20000) { 
				String sql = "";
				s = inputData.nextToken();
				StringTokenizer tabs = new StringTokenizer(s, "\t");
				
	    			//Recuperar valores        		
	    			whbx_code = tabs.nextToken();
	    			whbx_name = tabs.nextToken();
	    			whbt_quantity = tabs.nextToken();
	    			prod_code = tabs.nextToken();
	    			whtr_serial = tabs.nextToken();
	    			whse_name = tabs.nextToken();	
	    			
        			// Validar Caja Productos
        			whbx_whboxid = 0;
        			if (!whbx_code.equals("empty")) {
	        			sql = " SELECT * FROM whboxes " +
	          			      " WHERE whbx_code like '" + whbx_code + "'";
	        			pmConn2.doFetch(sql);
	        			if (!pmConn2.next()) {
						bmoWhBox = new BmoWhBox();
						bmoWhBox.getCode().setValue(whbx_code);
						bmoWhBox.getName().setValue(whbx_name);
	        				pmWhBox.save(pmConn2, bmoWhBox, bmUpdateResult);
	        				whbx_whboxid = bmUpdateResult.getId();
	        			} else{
	        				whbx_whboxid = pmConn2.getInt("whbx_whboxid");
	        			}
        			}	
				
				// Obtiene el producto
		    		prod_productid = 0;
				if (!prod_code.equals("empty")) {
					sql = " SELECT * FROM products " +
		    			  " WHERE prod_code like '" + prod_code + "'";			
					pmConn2.doFetch(sql);
					if (pmConn2.next()) {
						prod_productid = pmConn2.getInt("prod_productid");
					}
				}	
				
				// Obtiene el rastreo
		    		whtr_whtrackid = 0;
				if (!whtr_serial.equals("empty")) {
					sql = " SELECT * FROM whtracks " +
		    			  " WHERE whtr_serial like '" + whtr_serial + "'";			
					pmConn2.doFetch(sql);
					if (pmConn2.next()) {
						whtr_whtrackid = pmConn2.getInt("whtr_whtrackid");
					}
				}	
				
				// Obtiene la seccion de almacen
	    			whse_whsectionid = 0;
				if (!whtr_serial.equals("empty")) {
					sql = " SELECT * FROM whsections " +
		    			  " WHERE whse_name like '" + whse_name + "'";			
					pmConn2.doFetch(sql);
					if (pmConn2.next()) {
						whse_whsectionid = pmConn2.getInt("whse_whsectionid");
					}
				}	
				
				// Crear el nuevo registro
				bmoWhBoxTrack = new BmoWhBoxTrack();	
				bmoWhBoxTrack.getWhBoxId().setValue(whbx_whboxid);
				bmoWhBoxTrack.getWhTrackId().setValue(whtr_whtrackid);
				bmoWhBoxTrack.getQuantity().setValue(whbt_quantity);
				
				pmWhBoxTrack.save(pmConn, bmoWhBoxTrack, bmUpdateResult);
			}
			
			if (!bmUpdateResult.hasErrors())
				pmConn.commit();
		
		} catch (Exception e) {
			pmConn.rollback();
			throw new SFException(e.toString());
			
		} finally {
			pmConn.close();
			pmConn2.close();
		}
		
		response.sendRedirect("?action=complete&errorsave=" + bmUpdateResult.errorsToString());
			
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