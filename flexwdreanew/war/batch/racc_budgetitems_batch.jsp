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
	String programTitle = "Importacion de Partidas Presupuestales y Departamentos en CxC";
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
	          <li>Formato (celda = tab): Clave CxC* | Empresa CxC* | Partida Presp. | Departamento </li>
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
				
			String raccCode = "";
			String raccBudgetItem = "";
			String raccCompany = "";
			String raccArea = "";
			String errors = "";
		%>
		<table width="80%" border="0" align="center">
		    <TR>
		        <td colspan="4">  
		            <TABLE  border="1"  width="90%">               
		            	<tr valign="middle" align="center" class="">
		                    <td class="reportHeaderCell">&nbsp;#</td>
		                    <td class="reportHeaderCell">Clave CxC*</td>
		                   	<td class="reportHeaderCell">Empresa CxC*</td>
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
		        			raccCode = tabs.nextToken();
		        			raccCompany = tabs.nextToken();
		        			raccBudgetItem = tabs.nextToken();
		        			raccArea = tabs.nextToken();
							
							// Formatear; quitar espacios
		        			raccCode = raccCode.trim();
		        			raccCompany = raccCompany.trim();
		        			raccBudgetItem = raccBudgetItem.trim();
		        			raccArea = raccArea.trim();
		        			
		        			int companyId = 0;
	
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
		        			
		        			// Validar Empresa de la CxC
		        			// Solo la muestro por si se van a meter items de partidas con el mismo nombre, pero diferente empresa
		        			// ej: en visual en un presupuesto que tiene un item "Servicios" con monto de 100,00
		        			//	 y en ctech en un presupuesto que tiene un item "Servicios" con monto de 150,00
		        			if (!raccCompany.equalsIgnoreCase("empty")) {
			        			sql = " SELECT comp_name, comp_companyid FROM raccounts " +
			        					" LEFT JOIN companies ON (comp_companyid = racc_companyid) " +
			          			      	" WHERE comp_name LIKE '" + raccCompany + "'";
			        			pmConn2.doFetch(sql);
			        			if (pmConn2.next()) {
			        				companyId = pmConn2.getInt("comp_companyid");
			        			} else {
			        				errors = "La Empresa de la CxC no Existe: " + raccCompany + ".";
			        			}
		        			} else {
		        				errors = "La Empresa de la CxC es requerida";
		        			}
		        			
		        			// Validar Partida Presupuestal
		        			if (!raccBudgetItem.equalsIgnoreCase("empty")) {
			        			sql = " SELECT budg_name, bgty_name FROM budgetitems " +
		        						" LEFT JOIN budgetitemtypes ON (bgty_budgetitemtypeid = bgit_budgetitemtypeid) " +
		        						" LEFT JOIN budgets ON (bgit_budgetid = budg_budgetid) " +
		          			      		" WHERE bgty_name LIKE '" + raccBudgetItem + "' " +
		        						" AND budg_companyid = " + companyId ;
			        			pmConn2.doFetch(sql);
			        			if (pmConn2.next()) {
			        				raccBudgetItem = pmConn2.getString("budg_name") + " : " + raccBudgetItem;
			        			} else {
			        				errors = "La Partida Presupuestal no Existe: " + raccBudgetItem + ".";
			        			}
		        			}
		        			
		        			// Validar Departamento
		        			if (!raccArea.equalsIgnoreCase("empty")) {
			        			sql = " SELECT * FROM areas " +
			          			      " WHERE area_name LIKE '" + raccArea + "'";
			        			pmConn2.doFetch(sql);
			        			if (!pmConn2.next()) {
			        				errors = "El Departamento no Existe: " + raccArea + ".";
			        			}
		        			}
		    		%>
		    				<TR class="reportCellEven" width="100%">
		    					<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, raccCode, BmFieldType.STRING)%>
								<%=HtmlUtil.formatReportCell(sFParams, raccCompany, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, raccBudgetItem, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, raccArea, BmFieldType.STRING)%>		    					
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
			String raccCompany = "";
			String raccBudgetItem = "";
			String raccArea = "";

			// Llaves foraneas
			int raccId = 0;
			int raccCompanyId = 0;
			int budgetItemId = 0;
			int areaId = 0;
			
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
    			raccCompany = tabs.nextToken();
    			raccBudgetItem = tabs.nextToken();
    			raccArea = tabs.nextToken();
				
				// Formatear; minuscula y quitar espacios
    			raccCode = raccCode.trim();
    			raccCompany = raccCompany.trim();
    			raccBudgetItem = raccBudgetItem.toLowerCase().trim();
    			raccArea = raccArea.toLowerCase().trim(); 

    			// Obtiene la cxc
	    		raccId = 0;
				if (!raccCode.equalsIgnoreCase("empty")) {
					sql = " SELECT racc_raccountid FROM raccounts " +
		    			  " WHERE racc_code LIKE '" + raccCode + "'";			
					pmConn2.doFetch(sql);
					if (pmConn2.next()) {
						raccId = pmConn2.getInt("racc_raccountid");
						bmoRaccount = (BmoRaccount)pmRaccount.get(raccId);
					}
				}
				
				// Obtener la empresa de la cxc
				// Ya esta implicita en la cxc
				
				// Obtiene la partida presupuestal			
    			if (!raccBudgetItem.equalsIgnoreCase("empty")) {
        			sql = " SELECT bgit_budgetitemid FROM budgetitems " +
    						" LEFT JOIN budgetitemtypes ON (bgty_budgetitemtypeid = bgit_budgetitemtypeid) " +
    						" LEFT JOIN budgets ON (bgit_budgetid = budg_budgetid) " +
      			      		" WHERE bgty_name LIKE '" + raccBudgetItem + "' " +
    						" AND budg_companyid = " + bmoRaccount.getCompanyId().toInteger();
        			pmConn2.doFetch(sql);
        			if (pmConn2.next()) {
        				budgetItemId = pmConn2.getInt("bgit_budgetitemid");
        			}
    			}
    			
    			// Obtiene departamento
    			if (!raccArea.equalsIgnoreCase("empty")) {
        			sql = " SELECT area_areaid FROM areas " +
          			      " WHERE area_name LIKE '" + raccArea + "'";
        			pmConn2.doFetch(sql);
        			if (pmConn2.next()) {
        				areaId = pmConn2.getInt("area_areaid");
        			}
    			}
    				
    			// CxC
    			if (budgetItemId > 0)
    				bmoRaccount.getBudgetItemId().setValue(budgetItemId);
    			else 
        			bmoRaccount.getBudgetItemId().setValue("");
    			
    			if (areaId > 0)
    				bmoRaccount.getAreaId().setValue(areaId);
    			else 
        			bmoRaccount.getAreaId().setValue("");
    			
    			pmRaccount.saveSimple(pmConn, bmoRaccount, bmUpdateResult);
    			
    			// ITEMS de la CxC
    			PmRaccountItem pmRaccountItem = new PmRaccountItem(sFParams);
				BmoRaccountItem bmoRaccountItem = new BmoRaccountItem();

				sql = " SELECT rait_raccountitemid FROM raccountitems " +
          			      " WHERE rait_raccountid = " + raccId + "";
        			pmConn2.doFetch(sql);
        			while (pmConn2.next()) {
        				int raccItemId = pmConn2.getInt("rait_raccountitemid");
        				bmoRaccountItem = (BmoRaccountItem)pmRaccountItem.get(raccItemId);
        				if (budgetItemId > 0)
        					bmoRaccountItem.getBudgetItemId().setValue(budgetItemId);
        				else 
        					bmoRaccountItem.getBudgetItemId().setValue("");
        				
        				if (areaId > 0)
        					bmoRaccountItem.getAreaId().setValue(areaId);
        				else 
        					bmoRaccountItem.getAreaId().setValue("");
        				
        				
        				pmRaccountItem.saveSimple(pmConn, bmoRaccountItem, bmUpdateResult);
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