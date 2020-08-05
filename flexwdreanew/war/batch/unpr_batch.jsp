
<%@page import="com.flexwm.server.co.PmWorkItem"%>
<%@page import="com.flexwm.shared.co.BmoWorkItem"%>
<%@page import="com.flexwm.server.co.PmUnitPriceItem"%>
<%@page import="com.flexwm.shared.co.BmoUnitPriceItem"%>
<%@page import="com.flexwm.server.co.PmUnitPrice"%>
<%@page import="com.flexwm.shared.co.BmoUnitPrice"%>
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
	String programTitle = "Importacion Precios Unitarios";
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
	          <li>Formato (| = tab): Clave Obra* | Clave Precio U.| Nombre | Descripción | Tipo* | Categoria* | Clave Unidad* | Cantidad | Total* </li>
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
				
			String unpr_workidCode = "";
			String unpr_code = "";
			String unpr_name = "";
			String unpr_description = "";
			String unpr_type = "";
			String unpr_category = "";
			String unpr_codeUnity = "";
			String unpr_quantity = "";
			String unpr_total = "";
			
			 errors = "";
		%>
		<table width="80%" border="0" align="center"  class="">
		    <TR>
		        <td colspan="4">  
		            <TABLE cellpadding="0" cellspacing="0" border="1" rules=all width="90%">               
		            	<tr valign="middle" align="center" class="">
		                    <td class="reportHeaderCell">&nbsp;#</td>
		                    <td class="reportHeaderCell">Clave Obra</td>
		                    <td class="reportHeaderCell">Clave Precio U.</td>
		                     <td class="reportHeaderCell">Nombre</td>
							<td class="reportHeaderCell">Descripción</td>		                   
		                    <td class="reportHeaderCell">Tipo</td>
		                    <td class="reportHeaderCell">Categoria</td>
		                    <td class="reportHeaderCell">Clave Unidad</td>
		                    <td class="reportHeaderCell">Cantidad</td>		                 
		                    <td class="reportHeaderCell">Total</td>
		                </tr>
		            <%           
		        		while (inputData.hasMoreTokens() && i < 2000) {
		        			errors = "";
		        		    s = inputData.nextToken();
		        			StringTokenizer tabs = new StringTokenizer(s, "\t");        			
		        			
		        			// Recuperar valores
		        		
		        			unpr_workidCode = tabs.nextToken();
		        			unpr_code = tabs.nextToken();
		        			unpr_name = tabs.nextToken();
		        			unpr_description = tabs.nextToken();
		        			unpr_type = tabs.nextToken();
		        			unpr_category = tabs.nextToken();
		        			unpr_codeUnity = tabs.nextToken();
		        			unpr_quantity = tabs.nextToken();
		        			unpr_total = tabs.nextToken();
		        			
		        			pmConn.disableAutoCommit();
		        			pmConn2.disableAutoCommit();
                        	
							// Formatear; quitar espacios
		        			
					
// 		        			unpr_workidCode = unpr_workidCode.trim();
		        			//exts_reference = exts_reference.trim();
// 		        			unpr_code = unpr_code.trim();
// 		        			unpr_name = unpr_name.trim();
// 		        			unpr_description = unpr_description.trim();
		        			unpr_type = unpr_type.trim();
		        			unpr_category = unpr_category.trim();
		        			unpr_codeUnity = unpr_codeUnity.trim();
		        			unpr_quantity = unpr_quantity.trim();
		        			unpr_total = unpr_total.trim();
		        			
		        			
		        			
		    			
		        			if(!unpr_workidCode.equalsIgnoreCase("empty")){
		        				sql = "SELECT work_workid FROM	works WHERE work_code like '"+unpr_workidCode+"'";
		        				pmConn2.doFetch(sql);
		        				if(!pmConn2.next()){
		        					errors += "No se en contro la Obra " +unpr_workidCode;
		        				}
		        			}
		        			if(!unpr_type.equalsIgnoreCase("")){
		        				if(unpr_type.equalsIgnoreCase("Material")){
		        					unpr_type = ""+BmoUnitPrice.TYPE_MATERIAL;
		        				}else if(unpr_type.equalsIgnoreCase("Mano de Obra")){
		        					unpr_type = ""+BmoUnitPrice.TYPE_WORK;
		        				}else if(unpr_type.equalsIgnoreCase("Herramienta")){
		        					unpr_type = ""+BmoUnitPrice.TYPE_TOOL;
		        				}else if(unpr_type.equalsIgnoreCase("Equipo")){
		        					unpr_type = ""+BmoUnitPrice.TYPE_EQUIPMENT;
		        				}else if(unpr_type.equalsIgnoreCase("Auxiliares")){
		        					unpr_type = ""+BmoUnitPrice.TYPE_AUXILIARY;
		        				}else if(unpr_type.equalsIgnoreCase("Concepto")){
		        					unpr_type = ""+BmoUnitPrice.TYPE_CONCEPT;
		        				}else if(unpr_type.equalsIgnoreCase("Mando Intermedio")){
		        					unpr_type = ""+BmoUnitPrice.TYPE_INTERMEDIATE;
		        				}
		        			}
		        			if(!unpr_category.equalsIgnoreCase("")){
		        				if(unpr_category.equalsIgnoreCase("Insumo")){
		        					unpr_category = ""+BmoUnitPrice.CATEGORY_SUPPLIES;
		        				}else if(unpr_category.equalsIgnoreCase("Básico")){
		        					unpr_category = ""+BmoUnitPrice.CATEGORY_BASIC;
		        				}else if(unpr_category.equalsIgnoreCase("Compuesto")){
		        					unpr_category = ""+BmoUnitPrice.CATEGORY_COMPLEX;
		        				}else if(unpr_category.equalsIgnoreCase("Horario")){
		        					unpr_category = ""+BmoUnitPrice.CATEGORY_HOURLY;		        					
		        				}
		        				        	
		        			}
		        			if (!unpr_codeUnity.equalsIgnoreCase("")){
		        				sql = "SELECT unit_unitid FROM units where unit_code like '"+unpr_codeUnity+"'";
		        				pmConn.doFetch(sql);
		        				if(!pmConn.next()){
		        					errors += "No se encontro la Unidad " + unpr_codeUnity;
		        				}
		        				
		        			}
		        			if(!unpr_quantity.equalsIgnoreCase("")){
		        				try{
		        					Double.parseDouble(unpr_quantity);
		        				}catch (Exception e){
		        					errors +=  unpr_quantity +" no es un numero valido";
		        				}
		        			}
		        			if(!unpr_total.equalsIgnoreCase("")){
		        				try{
		        					Double.parseDouble(unpr_total);
		        				}catch(Exception e){
		        					errors +=  unpr_total +" no es un numero valido";
		        				}
		        			}
		        			// Validar cliente
		        			if (unpr_code.equalsIgnoreCase("empty")) {	        			
			        					        			
			        				errors += "Se debe asignar una clave ";
		        			}
		        			
                                                        
		        			
		    		%>      
                            <!--LLenar tabla -->
		    				<TR class="reportCellEven" width="100%">
		    					<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, unpr_workidCode, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, unpr_code, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, unpr_name, BmFieldType.STRING)%>
								<%=HtmlUtil.formatReportCell(sFParams, unpr_description, BmFieldType.STRING)%>
								<%=HtmlUtil.formatReportCell(sFParams, unpr_type, BmFieldType.STRING)%>
								<%=HtmlUtil.formatReportCell(sFParams, unpr_category, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, unpr_codeUnity, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, unpr_quantity, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, unpr_total, BmFieldType.STRING)%>
		    					
		    					<%if (!errors.equals("")){ %>
		    				    <td><font color="red"><%= errors %></font></prcp_price>
		    				    <%} %>
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
			pmConn2.disableAutoCommit();
			String s = "";

			String unpr_workidCode = "";
			String unpr_code = "";
			String unpr_name = "";
			String unpr_description = "";
			String unpr_type = "";
			String unpr_category = "";
			String unpr_codeUnity = "";
			String unpr_quantity = "";
			String unpr_total = "";

			// Llaves foraneas
			int unpr_workId = 0;
			int unpr_unityId = 0;
			
			
			PmUnitPrice pmUnitPrice = new PmUnitPrice(sFParams);
			BmoUnitPrice bmoUnitPrice = new BmoUnitPrice();
			
			StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
			int i = 1;
			while (inputData.hasMoreTokens() && i < 20000) {
				
				String sql = "";
				
				s = inputData.nextToken();
				StringTokenizer tabs = new StringTokenizer(s, "\t");
				errors = "";
				bmUpdateResult = new BmUpdateResult();
    			//Recuperar valores
    		
    			unpr_workidCode = tabs.nextToken();
    			unpr_code = tabs.nextToken();
    			unpr_name = tabs.nextToken();
    			unpr_description = tabs.nextToken();
    			unpr_type = tabs.nextToken();    		
    			unpr_category = tabs.nextToken();
    			unpr_codeUnity = tabs.nextToken();
    			unpr_quantity = tabs.nextToken();
    			unpr_total = tabs.nextToken();
    		
				// Formatear; minuscula y quitar espacios
				
    			//unpr_workidCode = unpr_workidCode.toLowerCase().trim();
//     			exts_reference = exts_reference.toLowerCase().trim();
//     			unpr_code = unpr_code.toLowerCase().trim();
//     			unpr_name = unpr_name.toLowerCase().trim();
//     			unpr_description = unpr_description.toLowerCase().trim();
    			unpr_type = unpr_type.toLowerCase().trim();
    			unpr_category = unpr_category.toLowerCase().trim();
    			unpr_codeUnity = unpr_codeUnity.toLowerCase().trim();
    			unpr_quantity = unpr_quantity.toLowerCase().trim();
 			
    			
    			String unpr_date = SFServerUtil.nowToString(sFParams, sFParams.getDateFormat());
    			
	    		
    			if(!unpr_workidCode.equalsIgnoreCase("")){
    				
    				sql = "SELECT work_workid FROM	works WHERE work_code like '"+unpr_workidCode+"'";
				
    				pmConn2.doFetch(sql);
    				if(pmConn2.next()){    					
    				
    					unpr_workidCode = pmConn2.getString("work_workid");
    				}
    			}
    			if(!unpr_type.equalsIgnoreCase("")){
    				if(unpr_type.equalsIgnoreCase("Material")){
    					unpr_type = ""+BmoUnitPrice.TYPE_MATERIAL;
    				}else if(unpr_type.equalsIgnoreCase("Mano de Obra")){
    					unpr_type = ""+BmoUnitPrice.TYPE_WORK;
    				}else if(unpr_type.equalsIgnoreCase("Herramienta")){
    					unpr_type = ""+BmoUnitPrice.TYPE_TOOL;
    				}else if(unpr_type.equalsIgnoreCase("Equipo")){
    					unpr_type = ""+BmoUnitPrice.TYPE_EQUIPMENT;
    				}else if(unpr_type.equalsIgnoreCase("Auxiliares")){
    					unpr_type = ""+BmoUnitPrice.TYPE_AUXILIARY;
    				}else if(unpr_type.equalsIgnoreCase("Concepto")){
    					unpr_type = ""+BmoUnitPrice.TYPE_CONCEPT;
    				}else if(unpr_type.equalsIgnoreCase("Mando Intermedio")){
    					unpr_type = ""+BmoUnitPrice.TYPE_INTERMEDIATE;
    				}
    			}
    			if(!unpr_category.equalsIgnoreCase("")){
    				if(unpr_category.equalsIgnoreCase("Insumo")){
    					unpr_category = ""+BmoUnitPrice.CATEGORY_SUPPLIES;
    				}else if(unpr_category.equalsIgnoreCase("Básico")){
    					unpr_category = ""+BmoUnitPrice.CATEGORY_BASIC;
    				}else if(unpr_category.equalsIgnoreCase("Compuesto")){
    					unpr_category = ""+BmoUnitPrice.CATEGORY_COMPLEX;
    				}else if(unpr_category.equalsIgnoreCase("Horario")){
    					unpr_category = ""+BmoUnitPrice.CATEGORY_HOURLY;		        					
    				}
    				        	
    			}
    		
    			if (!unpr_codeUnity.equalsIgnoreCase("")){
    				sql = "SELECT unit_unitid FROM units where unit_code like '"+unpr_codeUnity+"'";
    				
    				pmConn.doFetch(sql);
    				if(pmConn.next()){
    					
    					unpr_codeUnity = pmConn.getString("unit_unitid");
    				}
    				
    			}

    			
	
				bmoUnitPrice = new BmoUnitPrice();
				
				double total = Double.parseDouble(unpr_total);
				
				bmoUnitPrice.getCode().setValue(unpr_code);
				bmoUnitPrice.getName().setValue(unpr_name);
				bmoUnitPrice.getDescription().setValue(unpr_description);
				bmoUnitPrice.getType().setValue(unpr_type);
				bmoUnitPrice.getCategory().setValue(unpr_category);
				bmoUnitPrice.getUnitId().setValue(unpr_codeUnity);
				bmoUnitPrice.getWorkId().setValue(unpr_workidCode);
				bmoUnitPrice.getTotal().setValue(unpr_total);
				bmoUnitPrice.getDate().setValue(unpr_date);
				bmoUnitPrice.getCurrencyId().setValue(1);
				pmUnitPrice.save(pmConn, bmoUnitPrice, bmUpdateResult);
				if (!bmUpdateResult.hasErrors()){
					pmConn.commit();
					System.out.println("DDDDDDDDDDDDDDDDDD  "+bmUpdateResult.getBmObject().getId());
				
					//bmoUnitPrice = (BmoUnitPrice)bmUpdateResult.getBmObject();
					System.out.println("ID OBTENIDO "+ bmoUnitPrice.getId());
					BmoWorkItem bmoWorkItem = new BmoWorkItem();
					PmWorkItem pmWorkItem = new PmWorkItem(sFParams);
				
					bmoWorkItem.getCode().setValue(unpr_code);
					bmoWorkItem.getQuantity().setValue(unpr_quantity);
					bmoWorkItem.getPrice().setValue(unpr_total);
					bmoWorkItem.getAmount().setValue((Double.parseDouble(unpr_quantity)*(Double.parseDouble(unpr_total))));
					bmoWorkItem.getUnitPriceId().setValue(bmUpdateResult.getBmObject().getId());
					bmoWorkItem.getWorkId().setValue(unpr_workidCode);
					pmWorkItem.saveSimple(pmConn2,bmoWorkItem, bmUpdateResult);
					if (!bmUpdateResult.hasErrors())
						pmConn2.commit();
					else
						System.out.println("ERRORES "+bmUpdateResult.getMsg());
			}
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
		
		response.sendRedirect("unpr_batch.jsp?action=complete&errorsave=" + errorSave);
			
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