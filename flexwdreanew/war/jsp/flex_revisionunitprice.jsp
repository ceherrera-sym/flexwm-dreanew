
<%@include file="../inc/imports.jsp" %>
<%@page import="com.flexwm.server.co.PmUnitPrice"%>
<%@page import="com.flexwm.shared.co.BmoUnitPrice"%>
<%@page import="java.util.StringTokenizer"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/login.jsp" %>

<%
	String errorLabel = request.getParameter("errorLabel");
	String errorText = request.getParameter("errorText");
	String errorException = request.getParameter("errorException");
	String programTitle = "Importacion de Precios Unitarios";
	String programDescription = "Importacion de Precios Unitarios";
	String populateData = "", action = "";
	if (request.getParameter("populateData") != null) populateData = request.getParameter("populateData");
	if (request.getParameter("action") != null) action = request.getParameter("action");
%>

<html>
<head>
<title>:::Poblacion de Viviendas:::</title>
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

try { 

if (action.equals("revision")) { %>
	<table width="80%" border="0" align="center"  class="">

<%
	String msg = "";
	
	ArrayList list = new ArrayList();
	String s = "";
	int i = 1;		
	StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
	
	String cons = "";
	String code = "";
    String name = "";    
    String type = "";
    String category = "";
	String unit = "";
	String price = "";
	
%>
    
    <TR>
        <td colspan="4">  
            <TABLE cellpadding="0" cellspacing="0" border="1" rules=all width="90%">               
            	<tr valign="middle" align="center" class="">
                    <td class="reportHeaderCell">&nbsp;#</td>                    
                    <td class="reportHeaderCell">Clave</td>
                    <td class="reportHeaderCell">Nombre</td>                    	
                    <td class="reportHeaderCell">Tipo</td>                    
                    <td class="reportHeaderCell">Unidad</td>
                    <td class="reportHeaderCell">Categoria</td>
                    <td class="reportHeaderCell">Precio</td>
                </TR>
            <%           
        		while (inputData.hasMoreTokens() && i < 2000) {
        			s = inputData.nextToken();
        			StringTokenizer tabs = new StringTokenizer(s, "\t");        			
        			
        			//Recuperar valores        			        			
        			code = tabs.nextToken();        			    				
        			name = tabs.nextToken();
        			type = tabs.nextToken();
        			unit = tabs.nextToken();
        			category = tabs.nextToken();
        			price = tabs.nextToken();
        			
    				%>
    				<TR class="reportCellEven" width="100%">
    					<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>    					
    					<%=HtmlUtil.formatReportCell(sFParams, code, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, name, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, type, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, unit, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, category, BmFieldType.STRING)%>    					
    					<%=HtmlUtil.formatReportCell(sFParams, price, BmFieldType.STRING)%>    					    					
    				</TR>
    		<%
    			i++;	
        		}      		
            %>
</TABLE>

	<TABLE cellpadding="0" cellspacing="0" border="0"  width="100%">
		<FORM action="flex_revisionunitprice.jsp" method="POST" name="listFilter">	
		<input type="hidden" name="action" value="populate">
		<input type="hidden" name="populateData" value="<%= populateData %>">			
		<tr class="">
		    <td align="center" colspan="4" height="35" valign="middle">
		        <input type="submit" value="Cargar Precios Unitarios">
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
	    String type = "";
	    String category = "";
		String unit = "";
		String price = "";
			
		
		PmUnitPrice pmUnitPrice = new PmUnitPrice(sFParams);
		
				
		StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
		int i = 1;
		while (inputData.hasMoreTokens() && i < 20000) { 
			
			s = inputData.nextToken();
			StringTokenizer tabs = new StringTokenizer(s, "\t");
			
			code = tabs.nextToken();        			    				
			name = tabs.nextToken();
			type = tabs.nextToken();
			unit = tabs.nextToken();
			category = tabs.nextToken();
			price = tabs.nextToken();
			
			BmoUnitPrice bmoUnitPrice = new BmoUnitPrice();
			bmoUnitPrice.getCode().setValue(code);
			bmoUnitPrice.getName().setValue(name);	
			bmoUnitPrice.getType().setValue(type);
			bmoUnitPrice.getUnitId().setValue(unit);
			bmoUnitPrice.getCategory().setValue(category);			
			bmoUnitPrice.getSubTotal().setValue(price);
			bmoUnitPrice.getTotal().setValue(price);
			bmoUnitPrice.getWorkId().setValue(2);
			
			pmUnitPrice.saveSimple(pmConn, bmoUnitPrice, bmUpdateResult);
		}
		
		pmConn.commit();
	
	} catch (Exception e) {
		pmConn.rollback();
		throw new SFException(e.toString());
		
	} finally {
		pmConn.close();
	}
		
	response.sendRedirect("/jsp/flex_revisionunitprice.jsp?action=complete");
		
}  else if (action.equals("complete")) { %>
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr>		
		<td colspan="" class="reportTitle">
		    &nbsp;La Carga esta completa
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
}
%>
</body>
</html>


