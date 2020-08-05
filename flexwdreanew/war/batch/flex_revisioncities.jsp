
<%@include file="../inc/imports.jsp" %>
<%@page import="com.symgae.server.sf.PmCity"%>
<%@page import="com.symgae.shared.sf.BmoCity"%>
<%@page import="java.util.StringTokenizer"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/login.jsp" %>

<%
	String errorLabel = request.getParameter("errorLabel");
	String errorText = request.getParameter("errorText");
	String errorSave = request.getParameter("errorsave");
	String errorException = request.getParameter("errorException");
	String programTitle = "Importacion de Ciudades";
	String programDescription = "Importacion de Ciudades";
	String populateData = "", action = "";
	if (request.getParameter("populateData") != null) populateData = request.getParameter("populateData");
	if (request.getParameter("action") != null) action = request.getParameter("action");
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
		    <img border="0" height="" src="<%= sFParams.getMainImageUrl() %>" >
		</td>
		<td colspan="" class="reportTitle">
		    &nbsp;<%= programTitle %>
		</td>
	</tr>
</table>

<% 

PmConn pmConn = new PmConn(sFParams);
pmConn.open();

PmConn pmConn2 = new PmConn(sFParams);
pmConn2.open();

try { 

if (action.equals("revision")) { %>
	<table width="80%" border="0" align="center"  class="">

<%
	String msg = "";
	String sql = "";
	
	ArrayList list = new ArrayList();
	String s = "";
	int i = 1;		
	StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
	
	String code = "";
	String name = "";
	String description = "";
	String population = "";
    String codePhone = "";    
    String state = "";
    String country = "";	
	String errors = "";
	
	
	
%>
    
    <TR>
        <td colspan="4">  
            <TABLE cellpadding="0" cellspacing="0" border="1" rules=all width="90%">               
            	<tr valign="middle" align="center" class="">
                    <td class="reportHeaderCell">&nbsp;#</td>
                    <td class="reportHeaderCell">Clave</td>
                    <td class="reportHeaderCell">Ciudad</td>
                    <td class="reportHeaderCell">Descripcion</td>
                    <td class="reportHeaderCell">Poblacion</td>
                    <td class="reportHeaderCell">Lada</td>                    
                    <td class="reportHeaderCell">Estado</td>
                    <td class="reportHeaderCell">Pais</td>
                    <td class="reportHeaderCell">Errors</td>
                </TR>
            <%           
        		while (inputData.hasMoreTokens() && i < 2000) {
        			
        			errors = "";
        			s = inputData.nextToken();
        			StringTokenizer tabs = new StringTokenizer(s, "\t");        			
        			
        			//Recuperar valores        		
        			code = tabs.nextToken();
        			name = tabs.nextToken();        			    				
        			description = tabs.nextToken();
        			population = tabs.nextToken();
        			codePhone = tabs.nextToken();        			
        			state = tabs.nextToken();        			
        			country = tabs.nextToken();
        			
        			//Validar los datos necesarios para la direccion del cliente
        			if (code.equals("")) errors = "Falta la clave";        			
        			else if (name.equals("")) errors = "Falta la ciudad";
        			else if (codePhone.equals("")) errors = "Falta la lada ";
        			else if (state.equals("")) errors = "Falta el Estado";
        			else if (country.equals("")) errors = "Falta el pais";
        			
        			
        			//Validar el ciudad
        			sql = " SELECT * FROM countries " +
          			      " WHERE cont_code = '" + country.trim() + "'";
        			pmConn2.doFetch(sql);
        			if (!pmConn2.next()) {
        				errors = "El Pais no existe en el catalago";
        			}
        			
        			
        			//Validar el estado
        			sql = " SELECT * FROM states " +
          				  " LEFT JOIN countries ON (stat_countryid = cont_countryid) " +          			      
          				  " WHERE stat_code = '" + state.trim() + "'" +
          			      " AND cont_code = '" + country.trim() + "'";        			
        			pmConn2.doFetch(sql);
        			if (!pmConn2.next()) {
        				errors = "El estado no existe en el catalago";
        			}
        			
        			
        			//Validar que existe la ciudad
        			sql = " SELECT * FROM cities " + 
        			      " LEFT JOIN states ON (city_stateid = stat_stateid) " +
        				  " LEFT JOIN countries ON (stat_countryid = cont_countryid) " +
        			      " WHERE city_name = '" + name + "'" +
        				  " AND stat_code = '" + state.trim() + "'" +
        			      " AND cont_code = '" + country.trim() + "'";
        			pmConn2.doFetch(sql);
        			if (pmConn2.next()) {
        				errors = "La ciudad ya existe en el catalago";
        			}
        			        			
        			
    		%>
    			
    				<TR class="reportCellEven" width="100%">
    					<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>
    					<%=HtmlUtil.formatReportCell(sFParams, code, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, name, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, description, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, population, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, codePhone, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, state, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, country, BmFieldType.STRING)%>    				    
    				    <td><font color="red"><%= errors %></font></td>
    				</TR>
    		<%
    			
    			i++;        		
        	}
            %>
</TABLE>

	<TABLE cellpadding="0" cellspacing="0" border="0"  width="100%">
		<FORM action="flex_revisioncities.jsp" method="POST" name="listFilter">	
		<input type="hidden" name="action" value="populate">
		<input type="hidden" name="populateData" value="<%= populateData %>">			
		<tr class="">
		    <td align="center" colspan="4" height="35" valign="middle">
		    <% if (errors.equals("")) { %>
		        <input type="submit" value="Cargar Ciudades">
		    <% } %>    
		    </td>
		</tr>			    
		</FORM>								
	</TABLE>
<% } else if (action.equals("populate")) {
	BmUpdateResult bmUpdateResult = new BmUpdateResult();
	
	try {		
		
		pmConn.disableAutoCommit();
	
		ArrayList list = new ArrayList();
		String s = "";
		
		String code = "";
		String name = "";
		String description = "";
		String population = "";
	    String codePhone = "";    
	    String state = "";
	    String country = "";	
		String errors = "";
			
		
		PmCity pmCity = new PmCity(sFParams);
		BmoCity bmoCity = new BmoCity();
		
				
		StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
		int i = 1;
		while (inputData.hasMoreTokens() && i < 20000) { 
			String sql = "";
			s = inputData.nextToken();
			StringTokenizer tabs = new StringTokenizer(s, "\t");
			
			code = tabs.nextToken();
			name = tabs.nextToken();        			    				
			description = tabs.nextToken();
			population = tabs.nextToken();
			codePhone = tabs.nextToken();        			
			state = tabs.nextToken();        			
			country = tabs.nextToken();
			
			int stateId = 0;
			
			
			//Validar el estado
			sql = " SELECT * FROM states " +
  				  " LEFT JOIN countries on (stat_countryid = cont_countryid) " +          			      
  				  " WHERE stat_code = '" + state.trim() + "'" +
  			      " AND cont_code = '" + country.trim() + "'";
			System.out.println("sql" + sql);
			pmConn2.doFetch(sql);
			if (pmConn2.next()) {
				stateId = pmConn2.getInt("stat_stateid");
			}
			
			
			bmoCity = new BmoCity();
			bmoCity.getCode().setValue(code);
			bmoCity.getName().setValue(name);
			bmoCity.getDescription().setValue(description);
			bmoCity.getPopulation().setValue(population);			
			bmoCity.getLada().setValue(codePhone);
			bmoCity.getStateId().setValue(stateId);
			
			pmCity.save(pmConn, bmoCity, bmUpdateResult);
			 
		}
		
		if (!bmUpdateResult.hasErrors())
			pmConn.commit();
	
	} catch (Exception e) {
		pmConn.rollback();
		throw new SFException(e.toString());
		
	} finally {
		pmConn.close();
	}
		
	response.sendRedirect("/batch/flex_revisioncities.jsp?action=complete&errorsave=" + bmUpdateResult.errorsToString());
		
}  else if (action.equals("complete")) { %>
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr>		
		<td colspan="4" align="center" class="reportTitle">
		    &nbsp;La Carga esta completa
		    <%= errorSave %>
		</td>
	</tr>
	</table>

<% } %>


<% 	} catch (Exception e) { 
	errorLabel = "Error ";
	errorText = "Error";
	errorException = e.toString();
	
	response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);

%>

<%
} finally {
	pmConn.close();
	pmConn2.close();
	
}
%>
</body>
</html>


