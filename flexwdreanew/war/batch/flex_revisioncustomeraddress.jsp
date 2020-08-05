
<%@include file="../inc/imports.jsp" %>
<%@page import="com.flexwm.server.cm.PmCustomerAddress"%>
<%@page import="com.flexwm.shared.cm.BmoCustomerAddress"%>
<%@page import="java.util.StringTokenizer"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/login.jsp" %>

<%
	String errorLabel = request.getParameter("errorLabel");
	String errorText = request.getParameter("errorText");
	String errorSave = request.getParameter("errorsave");
	String errorException = request.getParameter("errorException");
	String programTitle = "Importacion de Direcciones de Clientes";
	String programDescription = programTitle;
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
	
	String codeCustomer = "";
	String type = "";
	String street = "";
	String number = "";
    String neighborhood = "";
    String zip = "";
    String description = "";
    String city = "";
    String state = "";
    String country = "";	
	String errors = "";
	
%>  
    <TR>
        <td colspan="4">  
            <TABLE cellpadding="0" cellspacing="0" border="1" rules=all width="90%">               
            	<tr valign="middle" align="center" class="">
                    <td class="reportHeaderCell">&nbsp;#</td>
                    <td class="reportHeaderCell">Cod.Cliente</td>                    
                    <td class="reportHeaderCell">Tipo</td>
                    <td class="reportHeaderCell">Calle</td>
                    <td class="reportHeaderCell">No.</td>
                    <td class="reportHeaderCell">Colonia</td>
                    <td class="reportHeaderCell">C.P.</td>
                    <td class="reportHeaderCell">Descripcion</td>
                    <td class="reportHeaderCell">Ciudad</td>
                    <!-- 
                    <td class="reportHeaderCell">Estado</td>
                    <td class="reportHeaderCell">Pais</td>-->
                    <td class="reportHeaderCell">Errors</td>
                </TR>
            <%           
        		while (inputData.hasMoreTokens() && i < 2000) {
        			
        			errors = "";
        			s = inputData.nextToken();
        			StringTokenizer tabs = new StringTokenizer(s, "\t");        			
        			
        			//Recuperar valores        		
        			codeCustomer = tabs.nextToken();
        			type = tabs.nextToken();
        			street = tabs.nextToken();
        		    number = tabs.nextToken();
        		    neighborhood = tabs.nextToken();
        		    zip = tabs.nextToken();
        		    description = tabs.nextToken();
        		    city = tabs.nextToken();
//         		    state = tabs.nextToken();
//         		    country = tabs.nextToken();	
        			
        			//Validar los datos necesarios para la direccion del cliente
        		    if (type.equals("")) errors = "Falta el Tipo";
        		    else if (street.equals("")) errors = "Falta la calle";
        			else if (neighborhood.equals("")) errors = "Falta la Colonia ";
        			else if (number.equals("")) errors = "Falta el Numero";
        			else if (zip.equals("")) errors = "Falta el C.P.";        			
        			else if (city.equals("")) errors = "Falta la Ciudad";
//         			else if (state.equals("")) errors = "Falta el Estado";
//         			else if (country.equals("")) errors = "Falta el pais";
        			
        		    
        		    if (!type.equals("W") && !type.equals("P")) {
        		    	errors = "El tipo no es correcto";
        		    }
        		    
        		    //Validar el cliente
        		    sql = " SELECT * FROM customers " + 
        		          " WHERE cust_code = '" + codeCustomer + "'";
        		    pmConn2.doFetch(sql);
        		    if (!pmConn2.next()) errors = "El codigo de cliente no esta ligada a un cliente";
        		    
        			//Validar que existe la ciudad
        			sql = " SELECT * FROM cities " + 
        			      " LEFT JOIN states ON (city_stateid = stat_stateid) " +
        				  " LEFT JOIN countries ON (stat_countryid = cont_countryid) " +
        			      " WHERE city_name = '" + city.trim()  + "'" ;
        				  //" AND stat_code like '" + state.trim()  + "'" +
        			      //" AND cont_code like '" + country.trim() + "'";
        			System.out.println("sql " + sql);
        			pmConn2.doFetch(sql);
        			if (!pmConn2.next()) {
        				errors = "La ciudad no existe en el catalago";
        			}
        			        			
        			
    		%>
    			
    				<TR class="reportCellEven" width="100%">
    					<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>
    					<%=HtmlUtil.formatReportCell(sFParams, codeCustomer, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, type, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, street, BmFieldType.STRING)%>    					
    					<%=HtmlUtil.formatReportCell(sFParams, number, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, neighborhood, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, zip, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, description, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, city, BmFieldType.STRING)%>
    					<%//=HtmlUtil.formatReportCell(sFParams, state, BmFieldType.STRING)%>
    					<%//=HtmlUtil.formatReportCell(sFParams, country, BmFieldType.STRING)%>    				    
    				    <td><font color="red"><%= errors %></font></td>
    				</TR>
    		<%
    			
    			i++;        		
        	}
            %>
</TABLE>

	<TABLE cellpadding="0" cellspacing="0" border="0"  width="100%">
		<FORM action="flex_revisioncustomeraddress.jsp" method="POST" name="listFilter">	
		<input type="hidden" name="action" value="populate">
		<input type="hidden" name="populateData" value="<%= populateData %>">			
		<tr class="">
		    <td align="center" colspan="4" height="35" valign="middle">
		    <% if (errors.equals("")) { %>
		        <input type="submit" value="Cargar Direcciones">
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
		
		String codeCustomer = "";
		String type = "";
		String street = "";
		String number = "";
	    String neighborhood = "";
	    String zip = "";
	    String description = "";
	    String city = "";
// 	    String state = "";
// 	    String country = "";	
		String errors = "";
			
		
		PmCustomerAddress pmCustomerAddress = new PmCustomerAddress(sFParams);
		BmoCustomerAddress bmoCustomerAddress = new BmoCustomerAddress();
		
				
		StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
		int i = 1;
		while (inputData.hasMoreTokens() && i < 20000) { 
			String sql = "";
			s = inputData.nextToken();
			StringTokenizer tabs = new StringTokenizer(s, "\t");
			
			codeCustomer = tabs.nextToken();
			type = tabs.nextToken();
			street = tabs.nextToken();
		    number = tabs.nextToken();
		    neighborhood = tabs.nextToken();
		    zip = tabs.nextToken();
		    description = tabs.nextToken();
		    city = tabs.nextToken();
// 		    state = tabs.nextToken();
// 		    country = tabs.nextToken();	
			
		    int customerId = 0;
		    //Validar el cliente
		    sql = " SELECT cust_customerid FROM customers " + 
		          " WHERE cust_code = '" + codeCustomer + "'";
		    pmConn2.doFetch(sql);
		    if (pmConn2.next()) customerId = pmConn2.getInt("cust_customerid");
		    
			int cityId = 0;			
			
			//Obtener el Id
			sql = " SELECT city_cityid FROM cities " +
				  " LEFT JOIN states on (city_stateid = stat_stateid) " +
  				  " LEFT JOIN countries on (stat_countryid = cont_countryid) " +          			      
  				  " WHERE city_name = '" + city.trim() + "'" ;
				  // " AND stat_code like '" + state + "'" +
			      //" AND cont_code like '" + country.trim() + "'";
			pmConn2.doFetch(sql);
			if (pmConn2.next()) {
				cityId = pmConn2.getInt("city_cityid");
			}
			
			
			bmoCustomerAddress = new BmoCustomerAddress();
			bmoCustomerAddress.getCustomerId().setValue(customerId);			
			bmoCustomerAddress.getType().setValue(type);
			bmoCustomerAddress.getStreet().setValue(street);
			bmoCustomerAddress.getNeighborhood().setValue(neighborhood);
			bmoCustomerAddress.getNumber().setValue(number);
			bmoCustomerAddress.getZip().setValue(zip);
			bmoCustomerAddress.getDescription().setValue(description);
			bmoCustomerAddress.getCityId().setValue(cityId);
			
			pmCustomerAddress.save(pmConn, bmoCustomerAddress, bmUpdateResult);
			 
		}
		
		if (!bmUpdateResult.hasErrors())
			pmConn.commit();
	
	} catch (Exception e) {
		pmConn.rollback();
		throw new SFException(e.toString());
		
	} finally {
		pmConn.close();
	}
		
	response.sendRedirect("/batch/flex_revisioncustomeraddress.jsp?action=complete&errorsave=" + bmUpdateResult.errorsToString());
		
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


