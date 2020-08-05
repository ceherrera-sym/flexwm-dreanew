<%@page import="com.flexwm.shared.fi.BmoRaccountItem"%>
<%@page import="com.flexwm.server.fi.PmRaccountItem"%>
<%@page import="com.flexwm.shared.fi.BmoRaccount"%>
<%@page import="com.flexwm.server.fi.PmRaccount"%>
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
	String programTitle = "Cambiar datos de CxC";
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
	          <li><b>Los campos con asterisco(*) son oblitarorios. Colocar la palabra "empty" para los campos vacíos.</b></li>          
	          <li>Formato (| = celda): Clave CxC* | Cliente | Fecha Registro | Fecha Prog. | F. Vencimiento | Serie | Folio </li>
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
		
		PmConn pmConn2 = new PmConn(sFParams);
		pmConn2.open();
		
		try {
			String msg = "";
			String sql = "";
			String s = "";
			int i = 1;		
			StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
				
			String raccCode = "";
			String custCode = "";
			String raccReceiveDate = "";
			String raccDueDate = "";
			String raccDueDateStart = "";
			String raccSerie = "";
			String raccFolio = "";
			String errors = "";
		%>
		<table width="80%" border="0" align="center">
		    <TR>
		        <td colspan="4">  
		            <TABLE  border="1"  width="90%">               
		            	<tr valign="middle" align="center" class="">
		                    <td class="reportHeaderCell">&nbsp;#</td>
		                    <td class="reportHeaderCell">Clave CxC*</td>
		                    <td class="reportHeaderCell">Cliente</td>
		                   	<td class="reportHeaderCell">Fecha Registro</td>
		                    <td class="reportHeaderCell">Fecha Prog.</td>
		                    <td class="reportHeaderCell">F. Vencimiento</td>
		                    <td class="reportHeaderCell">Serie</td>
							<td class="reportHeaderCell">Folio</td>
		                    <td class="reportHeaderCell">Errores</td>
		                </TR>
		            <%           
		        		while (inputData.hasMoreTokens() && i < 2000) {
		        			errors = "";
		        			s = inputData.nextToken();
		        			StringTokenizer tabs = new StringTokenizer(s, "\t");        			
		        			
		        			// Recuperar valores
		        			raccCode = tabs.nextToken();
		        			custCode = tabs.nextToken();
		        			raccReceiveDate = tabs.nextToken();
		        			raccDueDate = tabs.nextToken();
		        			raccDueDateStart = tabs.nextToken();
		        			raccSerie = tabs.nextToken();
		        			raccFolio = tabs.nextToken();
							
							// Formatear; quitar espacios
		        			raccCode = raccCode.trim();
		        			custCode = custCode.trim();
		        			raccReceiveDate = raccReceiveDate.trim();
		        			raccDueDate = raccDueDate.trim();
		        			raccDueDateStart = raccDueDateStart.trim();
		        			raccSerie = raccSerie.trim();
		        			raccFolio = raccFolio.trim();
		        			
		        			//int companyId = 0;
	
		        			// Validar CxC
		        			if (!raccCode.equalsIgnoreCase("empty")) {
			        			sql = " SELECT racc_code FROM raccounts " +
			          			      " WHERE racc_code LIKE '" + raccCode + "'";
			        			pmConn2.doFetch(sql);
			        			if (!pmConn2.next()) {
			        				errors = "La CxC no Existe: " + raccCode + ".";
			        			} else {
			        				//raccCode = pmConn2.getString("racc_code");
			        			}
		        			} else {
		        				errors = "La CxC es requerida";
		        			}
		        			
		        			// Validar CLiente de la CxC
		        			if (!custCode.equalsIgnoreCase("empty")) {
			        			sql = " SELECT cust_code, cust_customerid FROM customers " +
			          			      	" WHERE cust_code LIKE '" + custCode + "'";
			        			pmConn2.doFetch(sql);
			        			if (pmConn2.next()) {
			        				custCode = pmConn2.getString("cust_code");
			        			} else {
			        				errors = "El cliente no Existe: " + custCode + ".";
			        			}
		        			}

		    		%>
		    				<TR class="reportCellEven" width="100%">
		    					<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, raccCode, BmFieldType.STRING)%>
								<%=HtmlUtil.formatReportCell(sFParams, custCode, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, raccReceiveDate, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, raccDueDate, BmFieldType.STRING)%>
								<%=HtmlUtil.formatReportCell(sFParams, raccDueDateStart, BmFieldType.STRING)%>			
								<%=HtmlUtil.formatReportCell(sFParams, raccSerie, BmFieldType.STRING)%>
								<%=HtmlUtil.formatReportCell(sFParams, raccFolio, BmFieldType.STRING)%>
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
			throw new SFException(e.toString());
			
		} finally {
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

			String raccCode = "";
			String custCode = "";
			String raccReceiveDate = "";
			String raccDueDate = "";
			String raccDueDateStart = "";
			String raccSerie = "";
			String raccFolio = "";
			String errors = "";

			// Llaves
			int raccId = 0;
			int customerId = 0;
			
			PmRaccount pmRaccount = new PmRaccount(sFParams);
			BmoRaccount bmoRaccount = new BmoRaccount();
					
			StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
			int i = 1;
			while (inputData.hasMoreTokens() && i < 20000) {
				String sql = "";
				s = inputData.nextToken();
				StringTokenizer tabs = new StringTokenizer(s, "\t");

				// Recuperar valores
    			raccCode = tabs.nextToken();
    			custCode = tabs.nextToken();
    			raccReceiveDate = tabs.nextToken();
    			raccDueDate = tabs.nextToken();
    			raccDueDateStart = tabs.nextToken();
    			raccSerie = tabs.nextToken();
    			raccFolio = tabs.nextToken();
				
				// Formatear; quitar espacios
    			raccCode = raccCode.trim();
    			custCode = custCode.trim();
    			raccReceiveDate = raccReceiveDate.trim();
    			raccDueDate = raccDueDate.trim();
    			raccDueDateStart = raccDueDateStart.trim();
    			raccSerie = raccSerie.trim();
    			raccFolio = raccFolio.trim();

    			// Obtiene la cxc
	    		raccId = 0;
				if (!raccCode.equalsIgnoreCase("empty")) {
					sql = " SELECT racc_raccountid FROM raccounts " +
		    			  " WHERE racc_code LIKE '" + raccCode + "'";			
					pmConn2.doFetch(sql);
					if (pmConn2.next()) {
						raccId = pmConn2.getInt("racc_raccountid");
						bmoRaccount = (BmoRaccount)pmRaccount.get(raccId);
					} else 
						errors = "La CxC no Existe: " + custCode + ".";
				}
    			
    			// Obtiene departamento
       			if (!custCode.equalsIgnoreCase("empty")) {
        			sql = " SELECT cust_code, cust_customerid FROM customers " +
          			      	" WHERE cust_code LIKE '" + custCode + "'";
        			pmConn2.doFetch(sql);
        			if (pmConn2.next()) {
        				customerId = pmConn2.getInt("cust_customerid");
        				bmoRaccount.getCustomerId().setValue(customerId);
        			} else {
        				errors = "El cliente no Existe: " + custCode + ".";
        			}
       			}
    				
    			// CxC
    			if (!raccReceiveDate.equalsIgnoreCase("empty"))
    				bmoRaccount.getReceiveDate().setValue(raccReceiveDate);
    			
    			if (!raccDueDate.equalsIgnoreCase("empty"))
    				bmoRaccount.getDueDate().setValue(raccDueDate);
    			
    			if (!raccDueDateStart.equalsIgnoreCase("empty"))
    				bmoRaccount.getDueDateStart().setValue(raccDueDateStart);
    			
    			if (!raccSerie.equalsIgnoreCase("empty"))
    				bmoRaccount.getSerie().setValue(raccSerie);
    			
    			if (!raccFolio.equalsIgnoreCase("empty"))
    				bmoRaccount.getFolio().setValue(raccFolio);
    			
    			if (!raccSerie.equalsIgnoreCase("empty") || !raccFolio.equalsIgnoreCase("empty"))
    				bmoRaccount.getInvoiceno().setValue(bmoRaccount.getSerie().toString() + "" + bmoRaccount.getFolio().toString());
    			
    			pmRaccount.saveSimple(pmConn, bmoRaccount, bmUpdateResult);				
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