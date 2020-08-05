<%@page import="com.flexwm.shared.fi.BmoPaccountItem"%>
<%@page import="com.flexwm.server.fi.PmPaccountItem"%>
<%@page import="com.flexwm.shared.fi.BmoPaccount"%>
<%@page import="com.flexwm.server.fi.PmPaccount"%>
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
	String programTitle = "Importacion de Partidas Presupuestales y Departamentos en CxP";
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
	          <li>Se recomienda elaborar preparaci&oacute;n en una hoja de c&aacute;lculo, seleccionar y pegar en el recuadro siguiente.</li>
	          <li><b>Los campos con asterisco(*) son oblitarorios. Colocar la palabra "empty" para los campos vacíos.</b></li>          
	          <li>Formato (celda = tab): Clave CxP* | Empresa CxP* | Partida Presp. | Departamento </li>
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
				
			String paccCode = "";
			String paccBudgetItem = "";
			String paccCompany = "";
			String paccArea = "";
			String errors = "";
		%>
		<table width="80%" border="0" align="center">
		    <TR>
		        <td colspan="4">  
		            <TABLE  border="1"  width="90%">               
		            	<tr valign="middle" align="center" class="">
		                    <td class="reportHeaderCell">&nbsp;#</td>
		                    <td class="reportHeaderCell">Clave CxP*</td>
		                   	<td class="reportHeaderCell">Empresa CxP*</td>
		                    <td class="reportHeaderCell">Presp : Partida Presupuestal</td>
		                    <td class="reportHeaderCell">Departamento</td>
		                    <td class="reportHeaderCell">Errores</td>
		                </TR>
		            <%           
		        		while (inputData.hasMoreTokens() && i < 2000) {
		        			errors = "";
		        			s = inputData.nextToken();
		        			StringTokenizer tabs = new StringTokenizer(s, "\t");        			
		        			
		        			// Recuperar valores
		        			paccCode = tabs.nextToken();
		        			paccCompany = tabs.nextToken();
		        			paccBudgetItem = tabs.nextToken();
		        			paccArea = tabs.nextToken();
							
							// Formatear; quitar espacios
		        			paccCode = paccCode.trim();
		        			paccCompany = paccCompany.trim();
		        			paccBudgetItem = paccBudgetItem.trim();
		        			paccArea = paccArea.trim();
		        			
		        			int companyId = 0;
	
		        			// Validar CxP
		        			if (!paccCode.equalsIgnoreCase("empty")) {
			        			sql = " SELECT pacc_code FROM paccounts " +
			          			      " WHERE pacc_code LIKE '" + paccCode + "'";
			        			pmConn2.doFetch(sql);
			        			if (!pmConn2.next()) {
			        				errors = "La CxP no Existe: " + paccCode + ".";
			        			} else {
			        				//paccCode = pmConn2.getString("pacc_code");
			        			}
		        			} else {
		        				errors = "La CxP es requerida";
		        			}
		        			
		        			// Validar Empresa de la CxP
		        			// Solo la muestro por si se van a meter items de partidas con el mismo nombre, pero diferente empresa
		        			// ej: en visual en un presupuesto que tiene un item "Servicios" con monto de 100,00
		        			//	 y en ctech en un presupuesto que tiene un item "Servicios" con monto de 150,00
		        			if (!paccCompany.equalsIgnoreCase("empty")) {
			        			sql = " SELECT comp_name, comp_companyid FROM paccounts " +
			        					" LEFT JOIN companies ON (comp_companyid = pacc_companyid) " +
			          			      	" WHERE comp_name LIKE '" + paccCompany + "'";
			        			pmConn2.doFetch(sql);
			        			if (pmConn2.next()) {
			        				companyId = pmConn2.getInt("comp_companyid");
			        			} else {
			        				errors = "La Empresa de la CxP no Existe: " + paccCompany + ".";
			        			}
		        			} else {
		        				errors = "La Empresa de la CxP es requerida";
		        			}
		        			
		        			// Validar Partida Presupuestal
		        			if (!paccBudgetItem.equalsIgnoreCase("empty")) {
			        			sql = " SELECT budg_name, bgty_name FROM budgetitems " +
		        						" LEFT JOIN budgetitemtypes ON (bgty_budgetitemtypeid = bgit_budgetitemtypeid) " +
		        						" LEFT JOIN budgets ON (bgit_budgetid = budg_budgetid) " +
		          			      		" WHERE bgty_name LIKE '" + paccBudgetItem + "' " +
		        						" AND budg_companyid = " + companyId ;
			        			pmConn2.doFetch(sql);
			        			if (pmConn2.next()) {
			        				paccBudgetItem = pmConn2.getString("budg_name") + " : " + paccBudgetItem;
			        			} else {
			        				errors = "La Partida Presupuestal no Existe: " + paccBudgetItem + ".";
			        			}
		        			}
		        			
		        			// Validar Departamento
		        			if (!paccArea.equalsIgnoreCase("empty")) {
			        			sql = " SELECT * FROM areas " +
			          			      " WHERE area_name LIKE '" + paccArea + "'";
			        			pmConn2.doFetch(sql);
			        			if (!pmConn2.next()) {
			        				errors = "El Departamento no Existe: " + paccArea + ".";
			        			}
		        			}
		    		%>
		    				<TR class="reportCellEven" width="100%">
		    					<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, paccCode, BmFieldType.STRING)%>
								<%=HtmlUtil.formatReportCell(sFParams, paccCompany, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, paccBudgetItem, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, paccArea, BmFieldType.STRING)%>		    					
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

			String paccCode = "";
			String paccCompany = "";
			String paccBudgetItem = "";
			String paccArea = "";

			// Llaves foraneas
			int paccId = 0;
			int paccCompanyId = 0;
			int budgetItemId = 0;
			int areaId = 0;
			
			PmPaccount pmPaccount = new PmPaccount(sFParams);
			BmoPaccount bmoPaccount = new BmoPaccount();
					
			StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
			int i = 1;
			while (inputData.hasMoreTokens() && i < 20000) {
				String sql = "";
				s = inputData.nextToken();
				StringTokenizer tabs = new StringTokenizer(s, "\t");

				// Recuperar valores
    			paccCode = tabs.nextToken();
    			paccCompany = tabs.nextToken();
    			paccBudgetItem = tabs.nextToken();
    			paccArea = tabs.nextToken();
				
				// Formatear; minuscula y quitar espacios
    			paccCode = paccCode.trim();
    			paccCompany = paccCompany.trim();
    			paccBudgetItem = paccBudgetItem.toLowerCase().trim();
    			paccArea = paccArea.toLowerCase().trim(); 

    			// Obtiene la cxp
	    		paccId = 0;
				if (!paccCode.equalsIgnoreCase("empty")) {
					sql = " SELECT pacc_paccountid FROM paccounts " +
		    			  " WHERE pacc_code LIKE '" + paccCode + "'";			
					pmConn2.doFetch(sql);
					if (pmConn2.next()) {
						paccId = pmConn2.getInt("pacc_paccountid");
						bmoPaccount = (BmoPaccount)pmPaccount.get(paccId);
					}
				}
				
				// Obtener la empresa de la cxp
				// Ya esta implicita en la cxp
				
				// Obtiene la partida presupuestal			
    			if (!paccBudgetItem.equalsIgnoreCase("empty")) {
        			sql = " SELECT bgit_budgetitemid FROM budgetitems " +
    						" LEFT JOIN budgetitemtypes ON (bgty_budgetitemtypeid = bgit_budgetitemtypeid) " +
    						" LEFT JOIN budgets ON (bgit_budgetid = budg_budgetid) " +
      			      		" WHERE bgty_name LIKE '" + paccBudgetItem + "' " +
    						" AND budg_companyid = " + bmoPaccount.getCompanyId().toInteger();
        			pmConn2.doFetch(sql);
        			if (pmConn2.next()) {
        				budgetItemId = pmConn2.getInt("bgit_budgetitemid");
        			}
    			}
    			
    			// Obtiene departamento
    			if (!paccArea.equalsIgnoreCase("empty")) {
        			sql = " SELECT area_areaid FROM areas " +
          			      " WHERE area_name LIKE '" + paccArea + "'";
        			pmConn2.doFetch(sql);
        			if (pmConn2.next()) {
        				areaId = pmConn2.getInt("area_areaid");
        			}
    			}
    				
    			// CxP
    			if (budgetItemId > 0)
    				bmoPaccount.getBudgetItemId().setValue(budgetItemId);
    			else 
        			bmoPaccount.getBudgetItemId().setValue("");
    			
    			if (areaId > 0)
    				bmoPaccount.getAreaId().setValue(areaId);
    			else 
        			bmoPaccount.getAreaId().setValue("");
    			
    			pmPaccount.saveSimple(pmConn, bmoPaccount, bmUpdateResult);
    			
    			// ITEMS de la CxP
    			PmPaccountItem pmPaccountItem = new PmPaccountItem(sFParams);
				BmoPaccountItem bmoPaccountItem = new BmoPaccountItem();

				sql = " SELECT pait_paccountitemid FROM paccountitems " +
          			      " WHERE pait_paccountid = " + paccId + "";
        			pmConn2.doFetch(sql);
        			while (pmConn2.next()) {
        				int paccItemId = pmConn2.getInt("pait_paccountitemid");
        				bmoPaccountItem = (BmoPaccountItem)pmPaccountItem.get(paccItemId);
        				if (budgetItemId > 0)
        					bmoPaccountItem.getBudgetItemId().setValue(budgetItemId);
        				else 
        					bmoPaccountItem.getBudgetItemId().setValue("");
        				
        				if (areaId > 0)
        					bmoPaccountItem.getAreaId().setValue(areaId);
        				else 
        					bmoPaccountItem.getAreaId().setValue("");
        				
        				
        				pmPaccountItem.saveSimple(pmConn, bmoPaccountItem, bmUpdateResult);
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