
<%@page import="com.flexwm.shared.cm.BmoExternalSales"%>
<%@page import="com.flexwm.server.cm.PmExternalSales"%>
<%@page import="com.flexwm.shared.op.BmoProductPrice"%>
<%@include file="../inc/imports.jsp" %>
<%@page import="com.flexwm.server.op.PmProduct"%>
<%@page import="com.flexwm.shared.op.BmoProduct"%>
<%@page import="com.flexwm.server.op.PmProductPrice"%>
<%@page import="com.flexwm.shared.op.BmoProduct"%>
<%@page import="java.util.StringTokenizer"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@page import="java.text.DecimalFormat"%>
<%@include file="../inc/login.jsp" %>
<%
	// Variables
	String errorSave = "";
	String programTitle = "Importacion Ventas Externas";
	String errorLabel = request.getParameter("errorLabel");
	String errorText = request.getParameter("errorText");
	String errorException = request.getParameter("errorException");
	if(request.getParameter("errorsave") != null)
	 errorSave = request.getParameter("errorsave");
	String populateData = "", action = "";
	String errors = "";
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
	          <li><b>Los campos con '*' son obligatorios. Colocar la palabra "empty" para los campos vacíos.</b></li>          
	          <li>Formato (| = tab): Cliente* | DOC | Mes | Fecha* | Cantidad* |	 Producto |Precio* | Total*  </li>
	        </td>
	    </tr>    
	    <TR>
	        <td colspan="3">  
	            <TABLE cellpadding="0" cellspacing="0" border="0"  width="80%">
				    <FORM action="?" method="post" name="listFilter">
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
				
			String exts_month = "";
			String exts_cust = "";
			String exts_reference = "";
			String exts_date = "";
			String exts_quantity = "";
			String exts_price = "";
			String exts_total = "";
			String exts_produc = "";

			 errors = "";
		%>
		<table width="80%" border="0" align="center"  class="">
		    <TR>
		        <td colspan="4">  
		            <TABLE cellpadding="0" cellspacing="0" border="1" rules=all width="90%">               
		            	<tr valign="middle" align="center" class="">
		                    <td class="reportHeaderCell">&nbsp;#</td>
		                    <td class="reportHeaderCell">Cliente</td>
		                    <td class="reportHeaderCell">Referencia</td>
		                     <td class="reportHeaderCell">Mes</td>
							<td class="reportHeaderCell">Fecha</td>		                   
		                    <td class="reportHeaderCell">Cantidad</td>
		                    <td class="reportHeaderCell">Producto</td>
		                    <td class="reportHeaderCell">Precio</td>
		                    <td class="reportHeaderCell">Total</td>		                 
		                    <td class="reportHeaderCell">Errores</td>
		                </tr>
		            <%           
		        		while (inputData.hasMoreTokens() && i < 2000) {
		        			errors = "";
		        		    s = inputData.nextToken();
		        			StringTokenizer tabs = new StringTokenizer(s, "\t");        			
		        			
		        			// Recuperar valores
		        		
		        			exts_cust = tabs.nextToken();
		        			exts_reference = tabs.nextToken();
		        			exts_month = tabs.nextToken();
		        			exts_date = tabs.nextToken();
		        			exts_quantity = tabs.nextToken();
		        			exts_produc = tabs.nextToken();
		        			exts_price = tabs.nextToken();
		        			exts_total = tabs.nextToken();
		        			
		        			
		        			pmConn.disableAutoCommit();
		        			pmConn2.disableAutoCommit();
                        	
							// Formatear; quitar espacios
		        			
					
		        			exts_cust = exts_cust.trim();
		        			//exts_reference = exts_reference.trim();
		        			exts_date = exts_date.trim();
		        			exts_month = exts_month.trim();
		        			exts_quantity = exts_quantity.trim();
		        			exts_produc = exts_produc.trim();
		        			exts_price = exts_price.trim();
		        			exts_total = exts_total.trim();
		        			
		        			exts_price = exts_price.replace(",", "");
		        			exts_total = exts_total.replace(",","");
		        			
		        			exts_date = SFServerUtil.formatDate(sFParams, "dd/MM/yyyy", sFParams.getDateFormat(), exts_date);
		        			
		    			
		        			if(!exts_reference.equalsIgnoreCase("empty")){
		        				sql = "SELECT * FROM externalsales "
		        					   + "left join products on (exts_productid = prod_productid) "
		        					   + "where (exts_extsreference like '"+exts_reference+"') "
		        					   + "AND (prod_description like '"+exts_produc+"') ";
		        				pmConn2.doFetch(sql);
		        				if(pmConn2.next()){
		        					errors += "El DOC "+ exts_reference + " ya tiene una Venta con el producto " + exts_produc ;
		        				}
		        			}
		        			// Validar cliente
		        			if (!exts_cust.equalsIgnoreCase("empty")) {
			        			sql = " SELECT * FROM customers " +	
			          			      " WHERE cust_displayname LIKE '" + exts_cust + "'";
			        		
			        			pmConn2.doFetch(sql);
			        			if (!pmConn2.next()) {
			        				errors += "El Cliente " + exts_cust + " no Existe: " ;
			        			}
		        			}
		        			
                            // 		Validar Producto
                            if(!exts_produc.equalsIgnoreCase("empty")){
                            	sql = " SELECT * FROM products "+
                                      " WHERE prod_description LIKE '" + exts_produc + "'";
                            	pmConn2.doFetch(sql);
                            	if(!pmConn2.next()){
                            		errors += " El Producto " + exts_produc + "no Existe ";
                            	}
                            }
                            //Valiar Referencia
                            if(exts_reference.equals("empty")){
                            	exts_reference = " ";
                            }                                               
		        			
		    		%>      
                            <!--LLenar tabla -->
		    				<TR class="reportCellEven" width="100%">
		    					<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, exts_cust, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, exts_reference, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, exts_month, BmFieldType.STRING)%>
								<%=HtmlUtil.formatReportCell(sFParams, exts_date, BmFieldType.STRING)%>
								<%=HtmlUtil.formatReportCell(sFParams, exts_quantity, BmFieldType.STRING)%>
								<%=HtmlUtil.formatReportCell(sFParams, exts_produc, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, exts_price, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, exts_total, BmFieldType.STRING)%>
		    					
		    					
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

			String exts_month = "";
			String exts_cust = "";
			String exts_reference = "";
			String exts_date = "";
			String exts_quantity = "";
			String exts_price = "";
			String exts_total = "";
			String exts_produc = "";

			// Llaves foraneas
			int prod_productid = 0;
			int cust_customerid = 0;
			
			
			PmExternalSales pmExternalSales = new PmExternalSales(sFParams);
			BmoExternalSales bmoExternalSales = new BmoExternalSales();
					
			StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
			int i = 1;
			while (inputData.hasMoreTokens() && i < 20000) {
				String sql = "";
				s = inputData.nextToken();
				StringTokenizer tabs = new StringTokenizer(s, "\t");
				errors = "";
				bmUpdateResult = new BmUpdateResult();
    			//Recuperar valores
    		
    			exts_cust = tabs.nextToken();
    			exts_reference = tabs.nextToken();
    			exts_month = tabs.nextToken();
    			exts_date = tabs.nextToken();
    			exts_quantity = tabs.nextToken();
    			exts_produc = tabs.nextToken();
    			exts_price = tabs.nextToken();
    			exts_total = tabs.nextToken();
    			
				
				// Formatear; minuscula y quitar espacios
				
    			exts_cust = exts_cust.toLowerCase().trim();
//     			exts_reference = exts_reference.toLowerCase().trim();
    			exts_month = exts_month.toLowerCase().trim();
    			exts_date = exts_date.toLowerCase().trim();
    			exts_quantity = exts_quantity.toLowerCase().trim();
    			exts_produc = exts_produc.toLowerCase().trim();
    			exts_price = exts_price.toLowerCase().trim();
    			exts_total = exts_total.toLowerCase().trim();
    			exts_produc = exts_produc.toLowerCase().trim();
 			
    			exts_price = exts_price.replace(",", "");
    			exts_total = exts_total.replace(",","");
    			exts_date = SFServerUtil.formatDate(sFParams, "dd/MM/yyyy", sFParams.getDateFormat(), exts_date);
    			// Obtiene el producto
	    		
				if (!exts_produc.equalsIgnoreCase("empty")) {
					sql = " SELECT prod_productid FROM products " +
		    			  " WHERE prod_description LIKE '" + exts_produc + "'";			
					pmConn2.doFetch(sql);
					if (pmConn2.next()) {
						prod_productid = pmConn2.getInt("prod_productid");
					}
				}

				// obtiene cliente
    			if (!exts_cust.equalsIgnoreCase("empty")) {
        			sql = " SELECT cust_customerid FROM customers " +
          			      " WHERE cust_displayname LIKE '" + exts_cust + "'";
        			pmConn2.doFetch(sql);
        			if (pmConn2.next()) {
        				cust_customerid = pmConn2.getInt("cust_customerid");
        			  System.out.println(cust_customerid);
        			}
    			}   				
	
				bmoExternalSales = new BmoExternalSales();
				
			   Double exts_quantityD = Double.parseDouble(exts_quantity);
			   Double exts_priceD = Double.parseDouble(exts_price);
			   Double exts_totalD = Double.parseDouble(exts_total);
			   System.out.println(exts_total);

			   
			if (cust_customerid > 0)
			    bmoExternalSales.getCustomerId().setValue(cust_customerid);				
				bmoExternalSales.getReference().setValue(exts_reference);
			    bmoExternalSales.getDate().setValue(exts_date);
			    bmoExternalSales.getQuantity().setValue(exts_quantityD);
			    bmoExternalSales.getPrice().setValue(exts_priceD);
			    bmoExternalSales.getTotal().setValue(exts_totalD);
			if (prod_productid > 0)    
				bmoExternalSales.getProductId().setValue(prod_productid);
			
			sql = "SELECT * FROM externalsales "
					   + "left join products on (exts_productid = prod_productid) "
					   + "where (exts_extsreference like '"+exts_reference+"') "
					   + "AND (prod_description like '"+exts_produc+"') ";
			pmConn.doFetch(sql);
			if(pmConn.next()){				
				bmUpdateResult.addMsg( "<br> El DOC "+ exts_reference + " ya tiene un registro con el Producto ");
				errorSave += "<br> "+bmUpdateResult.errorsToString();
			}
				
				pmExternalSales.save(pmConn, bmoExternalSales, bmUpdateResult);
			
			
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
		
		response.sendRedirect("?action=complete&errorsave=" + errorSave);
			
	}  else if (action.equals("complete") ) { %>
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