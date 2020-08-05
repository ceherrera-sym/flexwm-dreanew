<%@page import="com.flexwm.shared.cm.BmoQuoteItem"%>
<%@page import="com.flexwm.server.cm.PmQuoteItem"%>
<%@page import="com.flexwm.shared.op.BmoOrderItem"%>
<%@page import="com.flexwm.server.op.PmOrderItem"%>
<%@page import="com.symgae.shared.GwtUtil"%>
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
	String programTitle = "Actualizar Información de Items de Oportunidades(no actualiza sub-total del Grupo ni del Pedido)";
	String errorLabel = request.getParameter("errorLabel");
	String errorText = request.getParameter("errorText");
	String errorException = request.getParameter("errorException");
	String errorSave = request.getParameter("errorsave");
	String populateData = "", action = "";
	if (request.getParameter("populateData") != null) 
		populateData = request.getParameter("populateData");
	if (request.getParameter("action") != null) 
		action = request.getParameter("action");
	
	String css = GwtUtil.getProperUrl(sFParams, "/css/" + defaultCss);
%>

<html>
<head>
<title>:::<%= programTitle %>:::</title>
<link rel="stylesheet" type="text/css" href="<%= css%>">
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
         		<li>Formato (| = celda): Oportunidad | ID ITEM | Cantidad | Clave Producto | Nombre Producto/Item | Tipo Producto | Precio | 
         			Empresa | Presupuesto | Partida Presupuestal | Departamento  </li>
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
		
// 		PmConn pmConn2 = new PmConn(sFParams);
// 		pmConn2.open();
		pmConn.disableAutoCommit();
		
		try {
			String msg = "";
			String sql = "";
			String s = "";
			int i = 1, companyId = 0;
			StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
				
			String oppo_code = "";
			String qoit_orderItemId = "";
			String qoit_quantity = "";
			String prod_code = "";
			String qoit_name = "";
			String prod_type = "";
			String qoit_price = "";
			String comp_name = "";	    
			String budg_name = "";	    
			String bgty_name = "";	    
			String area_name = "";	
			String errors = "";
		%>
		<table width="80%" border="0" align="center"  class="">
		    <TR>
		        <td colspan="4">  
		            <TABLE cellpadding="0" cellspacing="0" border="1" rules=all width="90%">               
		            	<tr valign="middle" align="center" class="">
		                    <td class="reportHeaderCell">&nbsp;#</td>
		                    <td class="reportHeaderCell">Clave Oportunidad</td>
		                    <td class="reportHeaderCell">ID ITEM</td>
		                    <td class="reportHeaderCell">Cantidad</td>
		                    <td class="reportHeaderCell">Clave Producto</td>
							<td class="reportHeaderCell">Nombre Producto/Item</td>
							<td class="reportHeaderCell">Tipo Producto</td>
		                    <td class="reportHeaderCell">Precio</td>
   		                    <td class="reportHeaderCell">Empresa</td>
   		                    <td class="reportHeaderCell">Presupuesto</td>
		                    <td class="reportHeaderCell">Partida Presupuestal</td>
							<td class="reportHeaderCell">Departamento</td>
		                    <td class="reportHeaderCell">Errores</td>
		                </TR>
		            <%           
		        		while (inputData.hasMoreTokens() && i < 2000) {
		        			errors = "";
		        			s = inputData.nextToken();
		        			StringTokenizer tabs = new StringTokenizer(s, "\t");        			
		        			
		        			// Recuperar valores        		
		        			oppo_code = tabs.nextToken();
		        			qoit_orderItemId = tabs.nextToken();
		        			qoit_quantity = tabs.nextToken();
		        			prod_code = tabs.nextToken();
		        			qoit_name = tabs.nextToken();
		        			prod_type = tabs.nextToken();
		        			qoit_price = tabs.nextToken();
		        			comp_name = tabs.nextToken();
		        			budg_name = tabs.nextToken();
		        			bgty_name = tabs.nextToken();
		        			area_name = tabs.nextToken();
		        			
		        			// Formatear; quitar espacios
		        			oppo_code = oppo_code.trim();
		        			qoit_orderItemId = qoit_orderItemId.trim();
		        			qoit_quantity = qoit_quantity.trim();
		        			prod_code = prod_code.trim();
		        			qoit_name = qoit_name.trim();
		        			prod_type = prod_type.trim();
		        			qoit_price = qoit_price.trim();
		        			comp_name = comp_name.trim();
		        			budg_name = budg_name.trim();
		        			bgty_name = bgty_name.trim();
		        			area_name = area_name.trim();
	
		        			// Validar Pedido
		        			if (!oppo_code.equals("empty")) {
			        			sql = " SELECT * FROM opportunities " +
			          			      " WHERE oppo_code LIKE '" + oppo_code + "'";
			        			pmConn.doFetch(sql);
			        			if (!pmConn.next()) {
			        				errors = "La Oportunidad no Existe: " + oppo_code;
			        			}
		        			}
		        			
		        			// Validar ID item
		        			if (!qoit_orderItemId.equals("empty")) {
			        			sql = " SELECT * FROM quoteitems " +
			          			      " WHERE qoit_quoteitemid = " + qoit_orderItemId + "";
			        			pmConn.doFetch(sql);
			        			if (!pmConn.next()) {
			        				errors = "El ITEM no Existe: " + qoit_orderItemId;
			        			}
		        			}	
		        			
		        			// Validar Clave producto
		        			if (!prod_type.equals("extra")) {
			        			if (!prod_code.equals("empty")) {
				        			sql = " SELECT * FROM products " +
				          			      " WHERE prod_code LIKE '" + prod_code + "'";
				        			pmConn.doFetch(sql);
				        			if (!pmConn.next()) {
				        				errors = "El Producto no Existe: " + prod_code + "";
				        			}
			        			}
		        			}
		        			
		        			// Validar Empresa de pedido
		        			// Solo la muestro por si se van a meter items de partidas con el mismo nombre, pero diferente empresa
		        			// ej: en visual en un presupuesto que tiene un item "Servicios" con monto de 100,00
		        			//	 y en ctech en un presupuesto que tiene un item "Servicios" con monto de 150,00
		        			if (!comp_name.equalsIgnoreCase("empty")) {
			        			sql = " SELECT comp_name, comp_companyid FROM companies " +
			          			      	" WHERE comp_name LIKE '" + comp_name + "'";
			        			pmConn.doFetch(sql);
			        			if (pmConn.next()) {
			        				companyId = pmConn.getInt("comp_companyid");
			        			} else {
			        				errors = "La Empresa no Existe: " + comp_name + ".";
			        			}
		        			} else {
		        				errors = "La Empresa es requerida.";
		        			}
		        			
		        			// Validar Presupuesto
		        			if (!budg_name.equalsIgnoreCase("empty") ) {
			        			sql = " SELECT budg_name FROM budgets " +
		          			      		" WHERE budg_name LIKE '" + budg_name + "'" +
		        						" AND budg_companyid = " + companyId;
			        			pmConn.doFetch(sql);
			        			if (pmConn.next()) {
			        				budg_name = pmConn.getString("budg_name");
			        			} else {
			        				errors = "El Presupuesto no Existe: " + budg_name + ".";
			        			}
		        			} else errors = " Debe colocarla el Presupuesto.";
		        			
		        			// Validar Partida Presupuestal
		        			if (!bgty_name.equalsIgnoreCase("empty")) {
			        			sql = " SELECT budg_name, bgty_name FROM budgetitems " +
		        						" LEFT JOIN budgetitemtypes ON (bgty_budgetitemtypeid = bgit_budgetitemtypeid) " +
		        						" LEFT JOIN budgets ON (bgit_budgetid = budg_budgetid) " +
		          			      		" WHERE bgty_name LIKE '" + bgty_name + "' " +
		        						" AND budg_name LIKE '" + budg_name + "'" +
		        						" AND budg_companyid = " + companyId ;
			        			pmConn.doFetch(sql);
			        			if (pmConn.next()) {
			        				bgty_name = pmConn.getString("bgty_name");
			        			} else {
			        				errors = "La Partida Presupuestal no Existe: " + bgty_name + ".";
			        			}
		        			} else errors = " Debe colocarla Partida Presupuestal.";

		        			// Validar Departamento
		        			if (!area_name.equalsIgnoreCase("empty")) {
			        			sql = " SELECT * FROM areas " +
			          			      " WHERE area_name LIKE '" + area_name + "'";
			        			pmConn.doFetch(sql);
			        			if (!pmConn.next()) {
			        				errors = "El Departamento no Existe: " + area_name + ".";
			        			}
		        			}
		    		%>
		    				<TR class="reportCellEven" width="100%">
		    					<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, oppo_code, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, qoit_orderItemId, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, qoit_quantity, BmFieldType.STRING)%>
								<%=HtmlUtil.formatReportCell(sFParams, prod_code, BmFieldType.STRING)%>
								<%=HtmlUtil.formatReportCell(sFParams, qoit_name, BmFieldType.STRING)%>
								<%=HtmlUtil.formatReportCell(sFParams, prod_type, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, qoit_price, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, comp_name, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, budg_name, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, bgty_name, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, area_name, BmFieldType.STRING)%>
		    					
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
			
			String oppo_code = "";
			String qoit_quoteItemId = "";
			String qoit_quantity = "";
			String prod_code = "";
			String qoit_name = "";
			String prod_type = "";
			String qoit_price = "";
			String comp_name = "";	    
			String budg_name = "";	    
			String bgty_name = "";	    
			String area_name = "";	
			String errors = "";			
			
			// Llaves foraneas
			int prod_productId = 0;
			int companyId = 0;
			int bgit_budgetItemId = 0;
			int area_areaId = 0;
			
			PmQuoteItem pmQuoteItem = new PmQuoteItem(sFParams);
			BmoQuoteItem bmoQuoteItem = new BmoQuoteItem();
					
			StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
			int i = 1;
			while (inputData.hasMoreTokens() && i < 20000) {
				String sql = "";
				s = inputData.nextToken();
				StringTokenizer tabs = new StringTokenizer(s, "\t");
				
				// Recuperar valores
				oppo_code = tabs.nextToken();
				qoit_quoteItemId = tabs.nextToken();
				qoit_quantity = tabs.nextToken();
				prod_code = tabs.nextToken();
				qoit_name = tabs.nextToken();
				prod_type = tabs.nextToken();
				qoit_price = tabs.nextToken();
				comp_name = tabs.nextToken();
				budg_name = tabs.nextToken();
				bgty_name = tabs.nextToken();
				area_name = tabs.nextToken();
				
				// Formatear; quitar espacios
				oppo_code = oppo_code.trim();
				qoit_quoteItemId = qoit_quoteItemId.trim();
				qoit_quantity = qoit_quantity.trim();
				prod_code = prod_code.trim();
				qoit_name = qoit_name.trim();
				prod_type = prod_type.trim();
				qoit_price = qoit_price.trim();
				comp_name = comp_name.trim();
				budg_name = budg_name.trim();
				bgty_name = bgty_name.trim();
				area_name = area_name.trim();	
	    			
       			// Validar Item
       			if (!qoit_quoteItemId.equals("empty")) {
        			sql = " SELECT * FROM quoteitems " +
          			      " WHERE qoit_quoteitemid = " + qoit_quoteItemId + "";
        			pmConn2.doFetch(sql);
        			if (pmConn2.next()) 
        				bmoQuoteItem = (BmoQuoteItem)pmQuoteItem.get(Integer.parseInt(qoit_quoteItemId));
       			}
       			
       			// Validar Clave producto
    			if (!prod_type.equals("extra")) {
        			if (!prod_code.equals("empty")) {
	        			sql = " SELECT prod_productid FROM products " +
	          			      " WHERE prod_code LIKE '" + prod_code + "'";
	        			pmConn2.doFetch(sql);
	        			if (pmConn2.next()) 
	        				prod_productId = pmConn2.getInt("prod_productid");
	        			else
	        				errors = "El Producto no Existe: " + prod_code + "";
        			}
    			}
       			
       			
    			// Validar Empresa de pedido
    			// Solo la muestro por si se van a meter items de partidas con el mismo nombre, pero diferente empresa
    			// ej: en visual en un presupuesto que tiene un item "Servicios" con monto de 100,00
    			//	 y en ctech en un presupuesto que tiene un item "Servicios" con monto de 150,00
    			if (!comp_name.equalsIgnoreCase("empty")) {
        			sql = " SELECT comp_name, comp_companyid FROM companies " +
          			      	" WHERE comp_name LIKE '" + comp_name + "'";
        			pmConn2.doFetch(sql);
        			if (pmConn2.next()) {
        				companyId = pmConn2.getInt("comp_companyid");
        			} else {
        				errors = "La Empresa no Existe: " + comp_name + ".";
        			}
    			} else {
    				errors = "La Empresa es requerida";
    			}
    			
    			// Validar Presupuesto
    			if (!budg_name.equalsIgnoreCase("empty") ) {
        			sql = " SELECT budg_name FROM budgets " +
      			      		" WHERE budg_name LIKE '" + budg_name + "'" +
    						" AND budg_companyid = " + companyId;
        			pmConn2.doFetch(sql);
        			if (pmConn2.next()) {
        				budg_name = pmConn2.getString("budg_name");
        			} else {
        				errors = "El Presupuesto no Existe: " + budg_name + ".";
        			}
    			} else errors = "Debe colocarla el Presupuesto.";
    			
    			// Validar Partida Presupuestal
    			if (!bgty_name.equalsIgnoreCase("empty")) {
        			sql = " SELECT bgit_budgetitemid FROM budgetitems " +
    						" LEFT JOIN budgetitemtypes ON (bgty_budgetitemtypeid = bgit_budgetitemtypeid) " +
    						" LEFT JOIN budgets ON (bgit_budgetid = budg_budgetid) " +
      			      		" WHERE bgty_name LIKE '" + bgty_name + "' " +
    						" AND budg_name LIKE '" + budg_name + "'" +
    						" AND budg_companyid = " + companyId ;
        			pmConn2.doFetch(sql);
        			if (pmConn2.next()) {
        				bgit_budgetItemId = pmConn2.getInt("bgit_budgetitemid");
        			} else {
        				errors = "La Partida Presupuestal no Existe: " + bgty_name + ".";
        			}
    			} else errors = " Debe colocarla Partida Presupuestal.";

    			// Validar Departamento
    			if (!area_name.equalsIgnoreCase("empty")) {
        			sql = " SELECT area_areaid FROM areas " +
          			      " WHERE area_name LIKE '" + area_name + "'";
        			pmConn2.doFetch(sql);
        			if (pmConn2.next()) 
        				area_areaId = pmConn2.getInt("area_areaid");
        			else
        				errors = "El Departamento no Existe: " + area_name + ".";
    			}
				
       			bmoQuoteItem.getQuantity().setValue(qoit_quantity);
       			bmoQuoteItem.getProductId().setValue(prod_productId);
       			bmoQuoteItem.getName().setValue(qoit_name);
       			bmoQuoteItem.getPrice().setValue(qoit_price);
       			bmoQuoteItem.getAmount().setValue(
       					(bmoQuoteItem.getPrice().toDouble() * bmoQuoteItem.getQuantity().toDouble() * bmoQuoteItem.getDays().toDouble() ));
       			bmoQuoteItem.getBudgetItemId().setValue(bgit_budgetItemId);
       			bmoQuoteItem.getAreaId().setValue(area_areaId);

       			pmQuoteItem.saveSimple(pmConn, bmoQuoteItem, bmUpdateResult);
			}
			
			if (!bmUpdateResult.hasErrors())
				pmConn.commit();
		
		} catch (Exception e) {
			pmConn.rollback();
			pmConn2.rollback();
			throw new SFException(e.toString());
			
		} finally {
			pmConn.close();
			pmConn2.close();
		}
		
		response.sendRedirect("ordi_updateInfo.jsp?action=complete&errorsave=" + bmUpdateResult.errorsToString());
			
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