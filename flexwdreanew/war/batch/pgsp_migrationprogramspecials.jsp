<%@include file="../inc/imports.jsp" %>
<%@include file="../inc/login.jsp" %>
<%@page import="com.symgae.shared.SFException"%>
<%
	// Variables
	String errorSave = "";
	String programTitle = "Migraci&oacuten de Accesos Especiales";
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
	          <li>Formato (| = tab): Clave Programa* | Clave Acceso Especial | Nombre Acceso Especial | Descripci&oacuten Acceso Especial </li>
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
				
			String codeProgram = "";
			String codeAcces = "";
			String nameAcces = "";
			String descriptionAcces = "";
			

			 errors = "";
		%>
		<table width="80%" border="0" align="center"  class="">
		    <TR>
		        <td colspan="4">  
		            <TABLE cellpadding="0" cellspacing="0" border="1" rules=all width="90%">               
		            	<tr valign="middle" align="center" class="">
		                    <td class="reportHeaderCell">&nbsp;#</td>
		                    <td class="reportHeaderCell">Clave Programa</td>
		                    <td class="reportHeaderCell">Clave Acceso Especial</td>
		                    <td class="reportHeaderCell">Nombre Acceso Especial</td>
							<td class="reportHeaderCell">Descripción Acceso Especia</td>                 
		                    <td class="reportHeaderCell">Errores</td>		                    
		                </tr>
		            <%           
		        		while (inputData.hasMoreTokens()) {
		        			errors = "";
		        		    s = inputData.nextToken();
		        			StringTokenizer tabs = new StringTokenizer(s, "\t");        			
		        			
		        			// Recuperar valores
		        		
		        			codeProgram = tabs.nextToken();
		        			codeAcces = tabs.nextToken();
		        			nameAcces = tabs.nextToken();
		        			descriptionAcces = tabs.nextToken();
		        		     		
		                        				
		        			codeProgram = codeProgram.trim();		        			
		        			codeAcces = codeAcces.trim();
		        		
// 		        			sql = "SELECT prog_programid FROM programs WHERE prog_code = '"+codeProgram+"'";
// 		        			pmConn.doFetch(sql);
// 		        			if(!pmConn.next()){
// 		        				errors += "No se encontro el programa "+codeProgram+" en la tabla programs,"; 	        				
// 		        			}
		        			if(codeAcces.equals("empty")){
		        				errors += " Falta el codigo del Acceso,";
		        			}
		        			if(nameAcces.equals("empty")){
		        				errors += " Falta el Nombre del Acceso";
		        			}
				
		        			
		    		%>      
                            <!--LLenar tabla -->
		    				<TR class="reportCellEven" width="100%">
		    					<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, codeProgram, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, codeAcces, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, nameAcces, BmFieldType.STRING)%>
								<%=HtmlUtil.formatReportCell(sFParams, descriptionAcces, BmFieldType.STRING)%>
								    					
		    				
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
			<table>
		
		<% 
		} catch (Exception e) {
			 response.sendRedirect("pgsp_migrationprogramspecials.jsp?action=complete&errorsave="+e.toString());
			pmConn.rollback();
			throw new SFException(e.toString());
			
		} finally {
			pmConn.close();
		}
		
	} else if (action.equals("populate")) {
		%><table cellpadding="0" cellspacing="0" border="0"  width="100%"><%
		 PmConn pmConn = new PmConn(sFParams);
		 pmConn.open();
		
			
		 String msg = "";
			String sql = "";
			String s = "";
			int i = 1;		
			StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
				
			String codeProgram = "";
			String codeAcces = "";
			String nameAcces = "";
			String descriptionAcces = "";
			int programId = 0;								
					
			 errors = "";
		 try{
			 while (inputData.hasMoreTokens()) {
					errors = "";
        		    s = inputData.nextToken();
        			StringTokenizer tabs = new StringTokenizer(s, "\t"); 
        			
        			codeProgram = tabs.nextToken();
        			codeAcces = tabs.nextToken();
        			nameAcces = tabs.nextToken();
        			descriptionAcces = tabs.nextToken();
        			
        			codeProgram = codeProgram.trim();		        			
        			codeAcces = codeAcces.trim();
        			
        			sql = "SELECT prog_programid FROM programs WHERE prog_code = '"+codeProgram+"'";
        			pmConn.doFetch(sql);
        			if(pmConn.next()){
        				programId = pmConn.getInt("prog_programid"); 	        				
        			}
        			sql= "SELECT * FROM programspecials where pgsp_code = '"+codeAcces+"'";
        			pmConn.doFetch(sql);
        			//
        			if(!pmConn.next() && programId > 0){       				
        			      			
        				sql = "INSERT INTO programspecials (pgsp_code,pgsp_name,pgsp_description,pgsp_programid) "
        					+"VALUES ('"+codeAcces+"','"+nameAcces+"','"+descriptionAcces+"',"+programId+")";        				
        				pmConn.doUpdate(sql);  
        				
        				%>
        				<tr>
        					<td class="reportTitle">Se creo el permiso ::: <%=codeAcces + " " +  nameAcces %></td>
        				</tr>
        				<%
        			}else{%>
        				<tr>
    					<td class="reportTitle">Ya existe el permiso ::: <%=codeAcces  %></td>
    					</tr>
        				<%
        			}
        		        							 
			 }
			 sql = "UPDATE programspecials SET pgsp_description = null WHERE pgsp_description LIKE 'empty%'";
			 
			 pmConn.doUpdate(sql);  
			
			
		 }catch (Exception e){
			 response.sendRedirect("pgsp_migrationprogramspecials.jsp?action=complete&errorsave="+e.toString());
			 pmConn.rollback();			 
				throw new SFException(e.toString());
		 }finally{
			 pmConn.close();			
		 }
		%></table><%
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