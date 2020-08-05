<%@page import="com.flexwm.server.co.PmDevelopmentBlock"%>
<%@page import="com.flexwm.shared.co.BmoDevelopmentBlock"%>
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
	String programTitle = "Importacion de Manzanas";
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
	          <li>Formato (| = tab): Clave Manzana | Nombre Manzana | Seccion | Mza En Proceso | Manzana Abierta| %Avance |</li>
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
				
			String dvbl_code = "";
			String dvbl_name = "";
			String dvbl_section = "";
			String dvbl_phase = "";
			String dvbl_proccess = "";
			String dvbl_open = "";
			String dvbl_advance = "";
			String errors = "";
		%>
		<table width="80%" border="0" align="center"  class="">
		    <TR>
		        <td colspan="4">  
		            <TABLE cellpadding="0" cellspacing="0" border="1" rules=all width="90%">               
		            	<tr valign="middle" align="center" class="">
		                    <td class="reportHeaderCell">&nbsp;#</td>
		                    <td class="reportHeaderCell">Clave Mza</td>
		                    <td class="reportHeaderCell">Nombre Mza</td>
		                    <td class="reportHeaderCell">Seccion</td>
		                    <td class="reportHeaderCell">Etqpa</td>
		                    <td class="reportHeaderCell">Mza Proceso</td>
							<td class="reportHeaderCell">Mza Abierta</td>
		                    <td class="reportHeaderCell">%Avance</td>
		                    <td class="reportHeaderCell">Errores</td>
		                </TR>
		            <%           
		        		while (inputData.hasMoreTokens() && i < 2000) {
		        			errors = "";
		        			s = inputData.nextToken();
		        			StringTokenizer tabs = new StringTokenizer(s, "\t");        			
		        			
		        			// Recuperar valores        		
		        			dvbl_code = tabs.nextToken();
		        			dvbl_name= tabs.nextToken();
		        			dvbl_section = tabs.nextToken();
		        			dvbl_phase = tabs.nextToken();
		        			dvbl_proccess = tabs.nextToken();
		        			dvbl_open = tabs.nextToken();
		        			dvbl_advance = tabs.nextToken();		        			
	
// 		        			if(dvbl_proccess=="SI" || dvbl_proccess=="NO" ){
								
// 							}else{
// 								errors = "PROCESO debe ser si, o no";
// 							}
		        			
// 							if(!(dvbl_open=="SI") || !(dvbl_open	=="NO") ){
// 								errors = "isOpen debe ser si, o no";
// 							}
// 		        			try {
// 		        				Double.parseDouble(dvbl_advance); 
// 		        			} catch(NumberFormatException e) {
// 		        				errors = "avance El formato no es un número.";
// 		        			} 
		        			
// 		        			try {
// 		        				Double.parseDouble(dvbl_phase); 
// 		        			} catch(NumberFormatException e) {
// 		        				errors = "la phase El formato no es un número.";
// 		        			}
// 		        			if(dvbl_phase ==null || )
		        			
		    		%>
		    				<TR class="reportCellEven" width="100%">
		    					<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, dvbl_code, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, dvbl_name, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, dvbl_section, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, dvbl_phase, BmFieldType.STRING)%>
								<%=HtmlUtil.formatReportCell(sFParams, dvbl_proccess, BmFieldType.STRING)%>
								<%=HtmlUtil.formatReportCell(sFParams, dvbl_open, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, dvbl_advance, BmFieldType.STRING)%>
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
		
	} else
			if (action.equals("populate")) {
		BmUpdateResult bmUpdateResult = new BmUpdateResult();
		
		PmConn pmConn = new PmConn(sFParams);
		pmConn.open();
		
		PmConn pmConn2 = new PmConn(sFParams);
		pmConn2.open();
		
		try {		
			pmConn.disableAutoCommit();
			String s = "";
			
			String dvbl_code = "";
			String dvbl_name = "";
			String dvbl_section = "";
			String dvbl_phase = "";
			String dvbl_proccess = "";
			String dvbl_open = "";
			String dvbl_advance = "";
			
			// Llaves foraneas
			
			PmDevelopmentBlock pmDevelopmentBlock = new PmDevelopmentBlock(sFParams);
			BmoDevelopmentBlock bmoDevelopmentBlock  = new BmoDevelopmentBlock();
					
			StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
			int i = 1;
			while (inputData.hasMoreTokens() && i < 20000) { 
				
				
				
				String sql = "";
				s = inputData.nextToken();
				StringTokenizer tabs = new StringTokenizer(s, "\t");
				
	    			//Recuperar valores        		
	    			dvbl_code = tabs.nextToken();
		        			dvbl_name= tabs.nextToken();
		        			dvbl_section = tabs.nextToken();
		        			dvbl_phase = tabs.nextToken();
		        			dvbl_proccess = tabs.nextToken();
		        			dvbl_open = tabs.nextToken();
		        			dvbl_advance = tabs.nextToken();	
	    				
		        			
		        			
		          			
		    				// Obtiene la seccion de almacen
		        			int dvbl_developmentBlocksCode = 0;
		    			if (!dvbl_phase.equals("empty")) {
		    				sql = " SELECT dvph_developmentphaseid FROM developmentphases " +
		    	    			  " WHERE dvph_code like '" + dvbl_phase + "'";			
		    				pmConn2.doFetch(sql);
		    				System.out.println("*****    "+sql);
		    				if (pmConn2.next()) {
		    					dvbl_developmentBlocksCode = pmConn2.getInt("dvph_developmentphaseid");
		    				}else{
		    					
		    				}
		    			}			
		    		System.out.println("*/////////");
		    			if(dvbl_proccess=="SI"){
		    				System.out.println("*/////////");
	        				dvbl_proccess= "1" ;
	        			}else{
	        				dvbl_proccess= "0" ;
	        			}
	        			if(dvbl_open=="SI"){
	        				System.out.println("*/////////");
	        				dvbl_open= "1" ;
	        			}else{
	        				dvbl_open= "0" ;
	        			}
				// Crear el nuevo registro
				bmoDevelopmentBlock = new BmoDevelopmentBlock();	
				bmoDevelopmentBlock.getCode().setValue(dvbl_code);
				bmoDevelopmentBlock.getName().setValue(dvbl_name);
				bmoDevelopmentBlock.getSection().setValue(dvbl_section);
				bmoDevelopmentBlock.getDevelopmentPhaseId().setValue(dvbl_developmentBlocksCode);
				bmoDevelopmentBlock.getStatusProcess().setValue(dvbl_proccess);
				bmoDevelopmentBlock.getIsOpen().setValue(dvbl_open);
				bmoDevelopmentBlock.getProcessPercentage().setValue(dvbl_advance);
				pmDevelopmentBlock.save(pmConn, bmoDevelopmentBlock, bmUpdateResult);
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
			
	}  else if (action.equals("complete")) {
		System.out.println("Entro al else de populate");
		%>
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