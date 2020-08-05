
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
	String programTitle = "Migraci&oacuten Modulos a Programas";
	String errorLabel = request.getParameter("errorLabel");
	String errorText = request.getParameter("errorText");
	String errorException = request.getParameter("errorException");
	if(request.getParameter("errorsave") != null)
	 errorSave = request.getParameter("errorsave");
	String populateData = "", action = "";
	String errors = "",errorsSum = "";
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
	          
	          <li><b>Los campos con '*' son obligatorios. Colocar la palabra "empty" para los campos vacíos.</b></li>          
	          <li>Formato (| = tab): Clave* | Nombre | Comp. Legado | Tipo Programa* | Program Superior | Menu | Orden Menu  </li>
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
		pmConn.disableAutoCommit();
		
		
		
		try {
			String msg = "";
			String sql = "";
			String s = "";
			int i = 1;		
			StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
				
			String code = "";
			String name = "";
			String campLegacy = "";
			String programType = "";
			String parent = "";
			String menu = "";
			String ordeMenu = "";		

			 errors = "";
		%>
		<table width="80%" border="0" align="center"  class="">
		    <TR>
		        <td colspan="4">  
		            <TABLE cellpadding="0" cellspacing="0" border="1" rules=all width="90%">               
		            	<tr valign="middle" align="center" class="">
		                    <td class="reportHeaderCell">&nbsp;#</td>
		                    <td class="reportHeaderCell">Clave</td>
		                    <td class="reportHeaderCell">Nombre</td>
		                     <td class="reportHeaderCell">Comp. Legado</td>
							<td class="reportHeaderCell">Tipo Programa</td>		                   
		                    <td class="reportHeaderCell">Program Superior</td>
		                    <td class="reportHeaderCell">Menu</td>
		                    <td class="reportHeaderCell">Orden Menu</td>
		                     <td class="reportHeaderCell">Errores</td>
		                    
		                </tr>
		            <%
		            	while (inputData.hasMoreTokens()) {
		            		        			errors = "";
		            		        		    s = inputData.nextToken();
		            		        			StringTokenizer tabs = new StringTokenizer(s, "\t");        			
		            		        			
		            		        			// Recuperar valores
		            		        		
		            		        			code = tabs.nextToken();
		            		        			name = tabs.nextToken();
		            		        			campLegacy = tabs.nextToken();
		            		        			programType = tabs.nextToken();
		            		        			parent = tabs.nextToken();
		            		        			menu = tabs.nextToken();
		            		        			ordeMenu = tabs.nextToken();      		
		            		                        				
		            		        			code = code.trim();		        			
		            		        			programType = programType.trim();
		            		        			ordeMenu = ordeMenu.trim();
		            		        			
		            		    			
		            // 		        			if(!code.equalsIgnoreCase("empty")){
		            // 		        				sql = "SELECT * FROM programs WHERE prog_code LIKE '"+code+"'";
		            // 		        				pmConn.doFetch(sql);
		            // 		        				if(!pmConn.next()){
		            // 		        					errors += "No existe un registro con el codigo "+code+" en la tabla programs, " ;
		            // 		        				}
		            // 		        			}

		            // 		        			if (!parent.equalsIgnoreCase("empty")) {		       			
		            // 			        		    sql = "SELECT * FROM programs WHERE prog_code LIKE '"+parent+"'";
		            // 		        				pmConn.doFetch(sql);
		            // 		        				if(!pmConn.next()){
		            // 		        					errors += "No existe un registro con el codigo "+parent+" en la tabla programs , " ;
		            // 		 	       				}
		            // 		        			}
		            					if(!ordeMenu.equalsIgnoreCase("empty")){
		            						boolean isNumber = true;
		            		        				try{
		            		        					Integer.parseInt(ordeMenu);
		            		        					
		            		        				}catch (Exception e){
		            		        					errors +=  ordeMenu + " no es un numero, ";
		            		        				}                                      
		            		        			}
		            					if(!menu.equalsIgnoreCase("empty")){
		            						sql = "SELECT * FROM menus WHERE (menu_code LIKE '" + menu + "') OR (menu_name like '"
		            							+ menu + "')";
		            					pmConn.doFetch(sql);
		            					if (!pmConn.next()) {
		            						errors += menu + " no es un menu valido, ";
		            					}
		            				}
		            				if (programType.equalsIgnoreCase("empty")) {
		            					errors += "Falta el tipo de programa, ";
		            				}
		            %>      
                            <!--LLenar tabla -->
		    				<TR class="reportCellEven" width="100%">
		    					<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, code, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, name, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, campLegacy, BmFieldType.STRING)%>
								<%=HtmlUtil.formatReportCell(sFParams, programType, BmFieldType.STRING)%>
								<%=HtmlUtil.formatReportCell(sFParams, parent, BmFieldType.STRING)%>
								<%=HtmlUtil.formatReportCell(sFParams, menu, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, ordeMenu, BmFieldType.STRING)%>		    					
		    				
		    				    <td><font color="red"><%= errors %></font></prcp_price>
		    				</TR>
		    		<%
		    			errorsSum += errors;
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
				    <% if (!(errorsSum.length() > 0)) { %>
				        <input type="submit" class="formSaveButton" value="GUARDAR">
				    <% }else{%>    
				    	<a href="?">Reiniciar</a>
				    <% } %>
				    </td>
				</tr>			    
				</FORM>								
			</TABLE>
		
		<% 
		} catch (Exception e) {
			response.sendRedirect("flex_migrationprograms.jsp?action=complete&errorsave="+e.toString());
			pmConn.rollback();
			throw new SFException(e.toString());
			
		} finally {
			pmConn.close();
		}
		
	} else if (action.equals("populate")) {
		
		 PmConn pmConn = new PmConn(sFParams);
		 pmConn.open();
		
			
		 String msg = "";
			String sql = "";
			String s = "";
			int i = 1;		
			StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
				
			String code = "";
			String name = "";
			String campLegacy = "";
			String programType = "";
			String parent = "";
			String menu = "";
			String ordeMenu = "";		

			 errors = "";
		 try{
			 while (inputData.hasMoreTokens()) {
					errors = "";
        		    s = inputData.nextToken();
        			StringTokenizer tabs = new StringTokenizer(s, "\t");        			
        			
        			// Recuperar valores
        		
        			code = tabs.nextToken();
        			name = tabs.nextToken();
        			campLegacy = tabs.nextToken();
        			programType = tabs.nextToken();
        			parent = tabs.nextToken();
        			menu = tabs.nextToken();
        			ordeMenu = tabs.nextToken();
        			int progId = 0,parentId = 0,menuId = 0;;
        		
        			code = code.trim();		        			
        			programType = programType.trim();
        			ordeMenu = ordeMenu.trim();
        			
        			if(!code.equalsIgnoreCase("empty")){
        				sql = "SELECT prog_programid FROM programs WHERE prog_code LIKE '"+code+"'";
        				pmConn.doFetch(sql);
        				if(pmConn.next()){
        					progId = pmConn.getInt("prog_programid") ;
        				}
        			}
        			if (!parent.equalsIgnoreCase("empty")) {		       			
	        		    sql = "SELECT prog_programid FROM programs WHERE prog_code LIKE '"+parent+"'";
        				pmConn.doFetch(sql);
        				if(pmConn.next()){
        					parentId  = pmConn.getInt("prog_programid") ;
        				}
        			}
        			if(!menu.equals("empty")){
						sql = "SELECT menu_menuid FROM menus WHERE (menu_code LIKE '"+menu+"') OR (menu_name like '"+menu+"')";
						pmConn.doFetch(sql);
						if(pmConn.next()){
							menuId = pmConn.getInt("menu_menuid") ;;
						}
					}
        		
        			if(!(parentId > 0)){
        				parent = null;
        	     	}else{
        	     		parent = ""+parentId;
        	     	}
        			if(!(menuId > 0)){
        				menu = null;
        			}else{
        				menu = ""+menuId;
        			}
        			if(ordeMenu.equalsIgnoreCase("empty")){
        				ordeMenu = null;
        			}
        			if(progId > 0){
        				sql = "update programs SET prog_type = '"+programType+"', prog_parentid = "+parent
        					+",prog_menuid = "+menu+",prog_menuindex = "+ordeMenu+" where prog_programid = "+progId ;
        			}else{
        				sql = "insert programs set prog_code = '"+code+"' , prog_name = '"+name+"' , prog_type = '"+
        					programType+"' , prog_parentid = "+parent+",prog_menuid = "+menu+",prog_menuindex = "+ordeMenu;
        			}
        			pmConn.doUpdate(sql);
        			
			 }
			 response.sendRedirect("flex_migrationprograms.jsp?action=complete&errorsave=");
			
		 }catch (Exception e){
			 response.sendRedirect("flex_migrationprograms.jsp?action=complete&errorsave="+e.toString());
			 pmConn.rollback();			 
				throw new SFException(e.toString());
		 }finally{
			 pmConn.close();			
		 }

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