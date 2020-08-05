<%@page import="com.flexwm.shared.wf.BmoWFlowType"%>
<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@page import="com.flexwm.shared.cm.BmoProject"%>
<%@page import="com.flexwm.shared.op.BmoConsultancy"%>
<%@page import="com.flexwm.shared.op.BmoOrderType"%>
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
	String programTitle = "Importacion de Precios de Productos";
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
	          <li>Se recomienda elaborar preparaci&oacute;n en archivo de texto, seleccionar y pegar en el recuadro siguiente.</li>
	          <li><b>Los campos con '*' son oblitarorios. Colocar la palabra "empty" para los campos vacíos.</b></li>          
	          <li>Formato (| = tab): Clave Producto* | Nombre Producto | Tipo Pedido | Tipo Flujo | Mercado | Vigencia* | Moneda* | Precio* | Empresa</li>
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
				
			String prod_code = "";
			String prod_name = "";
			String prcp_orderTypeName = "";
			String prcp_wFlowTypeName = "";
			String prcp_marketName = "";
			String prcp_startDate = "";
			String prcp_currencyCode = "";
			String prcp_price = "";
			String prcp_companyName = "";

			String errors = "";
		%>
		<table width="80%" border="0" align="center"  class="">
		    <TR>
		        <td colspan="4">  
		            <TABLE cellpadding="0" cellspacing="0" border="1" rules=all width="90%">               
		            	<tr valign="middle" align="center" class="">
		                    <td class="reportHeaderCell">&nbsp;#</td>
		                    <td class="reportHeaderCell">Clave Producto</td>
		                    <td class="reportHeaderCell">Nombre Producto</td>
		                    <td class="reportHeaderCell">Tipo Pedido</td>
   		                    <td class="reportHeaderCell">Tipo Flujo</td>
		                    <td class="reportHeaderCell">Mercado</td>
							<td class="reportHeaderCell">Vigencia</td>
		                    <td class="reportHeaderCell">Moneda</td>
		                    <td class="reportHeaderCell">Precio</td>
		                    <td class="reportHeaderCell">Empresa</td>
		                    <td class="reportHeaderCell">Errores</td>
		                </TR>
		            <%  int countError = 0;
		        		while (inputData.hasMoreTokens() && i < 2000) {
		        			errors = "";
		        			s = inputData.nextToken();
		        			StringTokenizer tabs = new StringTokenizer(s, "\t");        			
		        			
		        			// Recuperar valores
		        			prod_code = tabs.nextToken();
							prod_name = tabs.nextToken();
							prcp_orderTypeName = tabs.nextToken();
							prcp_wFlowTypeName = tabs.nextToken();
							prcp_marketName = tabs.nextToken();
							prcp_startDate = tabs.nextToken();
							prcp_currencyCode = tabs.nextToken();
							prcp_price = tabs.nextToken();
							prcp_companyName = tabs.nextToken();
							
							// Formatear; quitar espacios
		        			prod_code = prod_code.trim();
							prod_name = prod_name.trim();
							prcp_orderTypeName = prcp_orderTypeName.trim();
							prcp_wFlowTypeName = prcp_wFlowTypeName.trim();
							prcp_marketName = prcp_marketName.trim();
							prcp_startDate = prcp_startDate.trim();
							prcp_currencyCode = prcp_currencyCode.trim();
							prcp_price = prcp_price.trim();
							prcp_companyName = prcp_companyName.trim();
							
							int ortpId = 0;
							String ortpType = "";
	
		        			// Validar Producto
		        			if (!prod_code.equalsIgnoreCase("empty")) {
			        			sql = " SELECT prod_name FROM products " +
			          			      " WHERE prod_code LIKE '" + prod_code + "'";
			        			pmConn2.doFetch(sql);
			        			if (!pmConn2.next()) {
			        				errors = "El Producto no Existe: " + prod_code;
			        				countError++;
			        			} else {
			        				prod_name = pmConn2.getString("prod_name");
			        			}
		        			}
		        			
		        			// Validar Tipo de Pedido
		        			if (!prcp_orderTypeName.equalsIgnoreCase("empty")) {
			        			sql = " SELECT * FROM ordertypes " +
			          			      " WHERE ortp_name LIKE '" + prcp_orderTypeName + "'";
			        			pmConn2.doFetch(sql);
			        			if (!pmConn2.next()) {
			        				errors = "El Tipo de Pedido no Existe: " + prcp_orderTypeName;
			        				countError++;
			        			} else {
			        				ortpId = pmConn2.getInt("ortp_ordertypeid"); 
			        				ortpType = pmConn2.getString("ortp_type"); 
			        			}
		        			}
		        			
		        			// Validar Tipo de FLujo
		        			if (!prcp_wFlowTypeName.equalsIgnoreCase("empty")) {
			        			sql = " SELECT * FROM wflowtypes " +
			          			      " WHERE wfty_name LIKE '" + prcp_wFlowTypeName + "'";
			        			pmConn2.doFetch(sql);
			        			if (!pmConn2.next()) {
			        				errors = "El Tipo de Flujo no Existe: " + prcp_wFlowTypeName;
			        				countError++;
			        			} else {
			        				int programId = 0;
			        				if (ortpType.equals("" + BmoOrderType.TYPE_CONSULTANCY)) {
			        					programId = sFParams.getProgramId(new BmoConsultancy().getProgramCode());
			        				} else if (ortpType.equals("" + BmoOrderType.TYPE_RENTAL)) {
			        					programId = sFParams.getProgramId(new BmoProject().getProgramCode());
			        				} else if (ortpType.equals("" + BmoOrderType.TYPE_SALE)) {
			        					programId = sFParams.getProgramId(new BmoOrder().getProgramCode());
			        				} else { 
				        				errors = "No se pudo obtener el Programa através del Tipo de Pedido, verifique que este configurado.";
				        				countError++;
			        				}
			        				
			        				// validar que el Tipo de flujo exista dentro del tipo de pedido
			        				sql = "	SELECT * FROM wflowtypes  left join wflowcategories on (wfty_wflowcategoryid = wfca_wflowcategoryid)  "
				        				+ " WHERE wfca_programid = '" + programId + "'  AND wfty_status = '" + BmoWFlowType.STATUS_ACTIVE + "' "
				        				+ " AND wfty_name = '" + prcp_wFlowTypeName + "' "
				        				+ " AND wfca_wflowcategoryid IN (SELECT ortw_wflowcategoryid FROM ordertypewflowcategories WHERE ortw_ordertypeid = " + ortpId + ") ";
			        				pmConn2.doFetch(sql);
			        				if (!pmConn2.next()) {
				        				errors = "El Tipo de Flujo no está dentro del Tipo de pedido.";
				        				countError++;
			        				}
			        			}
		        			}
		        			
		        			// Validar Mercado
		        			if (!prcp_marketName.equalsIgnoreCase("empty")) {
			        			sql = " SELECT * FROM markets " +
			          			      " WHERE mrkt_name LIKE '" + prcp_marketName + "'";
			        			pmConn2.doFetch(sql);
			        			if (!pmConn2.next()) {
			        				errors = "El Mercado no Existe: " + prcp_marketName + ", " + sql;
			        				countError++;
			        			}
		        			}
		        			
		        			if (prcp_startDate.equalsIgnoreCase("empty")) {
		        				errors = "La Fecha de Vigencia es requerida";
		        			}
		        			
		        			// Validar Moneda
		        			if (!prcp_currencyCode.equalsIgnoreCase("empty")) {
			        			sql = " SELECT * FROM currencies " +
			          			      " WHERE cure_code LIKE '" + prcp_currencyCode + "'";
			        			pmConn2.doFetch(sql);
			        			if (!pmConn2.next()) {
			        				errors = "La Moneda no Existe: " + prcp_currencyCode + ", " + sql;
			        				countError++;
			        			}
		        			} else {
		        				errors = "La Moneda es requerida";
		        				countError++;
		        			}
		        			
		        			if (Double.parseDouble(prcp_price) < 0 || prcp_price.equals("empty")) {
		        				errors = "El Precio no es válido";
		        				countError++;
		        			}
		        			
		        			// Validar Empresa
		        			if (!prcp_companyName.equalsIgnoreCase("empty")) {
			        			sql = " SELECT * FROM companies " +
			          			      " WHERE comp_name LIKE '" + prcp_companyName + "'";
			        			pmConn2.doFetch(sql);
			        			if (!pmConn2.next()) {
			        				errors = "La Empresa no Existe: " + prcp_companyName + ", " + sql;
			        				countError++;
			        			}
		        			}
		        			
		    		%>
		    				<TR class="reportCellEven" width="100%">
		    					<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, prod_code, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, prod_name, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, prcp_orderTypeName, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, prcp_wFlowTypeName, BmFieldType.STRING)%>
								<%=HtmlUtil.formatReportCell(sFParams, prcp_marketName, BmFieldType.STRING)%>
								<%=HtmlUtil.formatReportCell(sFParams, prcp_startDate, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, prcp_currencyCode, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, prcp_price, BmFieldType.CURRENCY)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, prcp_companyName, BmFieldType.STRING)%>
		    					
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
				    <% if (!(countError > 0)) { %>
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
			
			String prod_code = "";
			String prod_name = "";
			String prcp_orderTypeName = "";
			String prcp_wFlowTypeName = "";
			String prcp_marketName = "";
			String prcp_startDate = "";
			String prcp_currencyCode = "";
			String prcp_price = "";
			String prcp_companyName = "";

			// Llaves foraneas
			int prod_productid = 0;
			int prcp_ordertypeid = 0;
			int prcp_wflowtypeid = 0;
			int prcp_marketid = 0;
			int prcp_currencyid = 0;
			int prcp_companyid = 0;
			
			PmProductPrice pmProductPrice = new PmProductPrice(sFParams);
			BmoProductPrice bmoProductPrice = new BmoProductPrice();
					
			StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
			int i = 1;
			while (inputData.hasMoreTokens() && i < 20000) {
				String sql = "";
				s = inputData.nextToken();
				StringTokenizer tabs = new StringTokenizer(s, "\t");
				
				prod_code = "";
				prod_name = "";
				prcp_orderTypeName = "";
				prcp_wFlowTypeName = "";
				prcp_marketName = "";
				prcp_startDate = "";
				prcp_currencyCode = "";
				prcp_price = "";
				prcp_companyName = "";

				// Llaves foraneas
				prod_productid = 0;
				prcp_ordertypeid = 0;
				prcp_wflowtypeid = 0;
				prcp_marketid = 0;
				prcp_currencyid = 0;
				prcp_companyid = 0;

    			//Recuperar valores
    			prod_code = tabs.nextToken();
				prod_name = tabs.nextToken();
				prcp_orderTypeName = tabs.nextToken();
				prcp_wFlowTypeName = tabs.nextToken();
				prcp_marketName = tabs.nextToken();
				prcp_startDate = tabs.nextToken();
				prcp_currencyCode = tabs.nextToken();
				prcp_price = tabs.nextToken();
				prcp_companyName = tabs.nextToken();

				// Formatear; minuscula y quitar espacios
    			prod_code = prod_code.toLowerCase().trim();
				prod_name = prod_name.toLowerCase().trim();
				prcp_orderTypeName = prcp_orderTypeName.toLowerCase().trim();
				prcp_wFlowTypeName = prcp_wFlowTypeName.toLowerCase().trim();
				prcp_marketName = prcp_marketName.toLowerCase().trim();
				prcp_startDate = prcp_startDate.toLowerCase().trim();
				prcp_currencyCode = prcp_currencyCode.toLowerCase().trim();
				prcp_price = prcp_price.toLowerCase().trim();
				prcp_companyName = prcp_companyName.toLowerCase().trim();

    			// Obtiene el producto
	    		prod_productid = 0;
				if (!prod_code.equalsIgnoreCase("empty")) {
					sql = " SELECT prod_productid FROM products " +
		    			  " WHERE prod_code LIKE '" + prod_code + "'";			
					pmConn2.doFetch(sql);
					if (pmConn2.next()) {
						prod_productid = pmConn2.getInt("prod_productid");
					}
				}

				// Validar Tipo de Pedido
    			if (!prcp_orderTypeName.equalsIgnoreCase("empty")) {
        			sql = " SELECT ortp_ordertypeid FROM ordertypes " +
          			      " WHERE ortp_name LIKE '" + prcp_orderTypeName + "'";
        			pmConn2.doFetch(sql);
        			if (pmConn2.next()) {
        				prcp_ordertypeid = pmConn2.getInt("ortp_ordertypeid");
        			}
    			}
				
    			// Validar Tipo de Flujo
    			if (!prcp_wFlowTypeName.equalsIgnoreCase("empty")) {
        			sql = " SELECT wfty_wflowtypeid FROM wflowtypes " +
          			      " WHERE wfty_name LIKE '" + prcp_wFlowTypeName + "'";
        			pmConn2.doFetch(sql);
        			if (pmConn2.next()) {
        				prcp_wflowtypeid = pmConn2.getInt("wfty_wflowtypeid");
        			}
    			}
    			
    			// Validar Mercado
    			if (!prcp_marketName.equalsIgnoreCase("empty")) {
        			sql = " SELECT mrkt_marketid FROM markets " +
          			      " WHERE mrkt_name LIKE '" + prcp_marketName + "'";
        			pmConn2.doFetch(sql);
        			if (pmConn2.next()) {
        				prcp_marketid = pmConn2.getInt("mrkt_marketid");
        			}
    			}
    			
    			// Validar Moneda
    			if (!prcp_currencyCode.equalsIgnoreCase("empty")) {
        			sql = " SELECT cure_currencyid FROM currencies " +
          			      " WHERE cure_code LIKE '" + prcp_currencyCode + "'";
        			pmConn2.doFetch(sql);
        			if (pmConn2.next()) {
        				prcp_currencyid = pmConn2.getInt("cure_currencyid");
        			}
    			}
    			
    			// Validar Empresa
    			if (!prcp_companyName.equalsIgnoreCase("empty")) {
        			sql = " SELECT comp_companyid FROM companies " +
          			      " WHERE comp_name LIKE '" + prcp_companyName + "'";
        			pmConn2.doFetch(sql);
        			if (pmConn2.next()) {
        				prcp_companyid = pmConn2.getInt("comp_companyid");
        			}
    			}

				// Crear el nuevo registro
				bmoProductPrice = new BmoProductPrice();	
				bmoProductPrice.getProductId().setValue(prod_productid);
				if (prcp_ordertypeid > 0)
					bmoProductPrice.getOrderTypeId().setValue(prcp_ordertypeid);
				if (prcp_marketid > 0)
					bmoProductPrice.getMarketId().setValue(prcp_marketid);
				
				if (prcp_wflowtypeid > 0)
					bmoProductPrice.getWFlowTypeId().setValue(prcp_wflowtypeid);
				bmoProductPrice.getStartDate().setValue(prcp_startDate);
				bmoProductPrice.getCurrencyId().setValue(prcp_currencyid);
				bmoProductPrice.getPrice().setValue(prcp_price);
				if (prcp_companyid > 0)
					bmoProductPrice.getCompanyId().setValue(prcp_companyid);
				
				pmProductPrice.save(pmConn, bmoProductPrice, bmUpdateResult);
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
		
		response.sendRedirect("prpc_batch.jsp?action=complete&errorsave=" + bmUpdateResult.errorsToString());
			
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