
<%@include file="../inc/imports.jsp" %>
<%@page import="com.flexwm.server.cm.PmIndustry"%>
<%@page import="com.flexwm.shared.cm.BmoIndustry"%>
<%@page import="java.util.StringTokenizer"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/login.jsp" %>

<%
	String errorLabel = request.getParameter("errorLabel");
	String errorText = request.getParameter("errorText");
	String errorSave = request.getParameter("errorsave");
	String errorException = request.getParameter("errorException");
	String programTitle = "Importacion de Servicios por ?";
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

if (action.equals("revision")) { 
%>
	<table width="80%" border="0" align="center"  class="">
<%

	String msg = "";
	String sql = "";
	
	ArrayList list = new ArrayList();
	String s = "";
	int i = 1;		
	StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
	
	String codeCust = "";
	String cose = "";		
	String errors = "";
	
	
	
%>
    
    <TR>
        <td colspan="4">  
            <TABLE cellpadding="0" cellspacing="0" border="1" rules=all width="90%">               
            	<tr valign="middle" align="center" class="">
                    <td class="reportHeaderCell">&nbsp;#</td>
                    <td class="reportHeaderCell">No.Cliente</td>
                    <td class="reportHeaderCell">Servicios Por ?</td>
                    <td class="reportHeaderCell">Errors</td>
                </TR>
            <%          
	            
        		while (inputData.hasMoreTokens() && i < 2000) {
        			
        			errors = "";
        			s = inputData.nextToken();
        			StringTokenizer tabs = new StringTokenizer(s, "\t");        			
        			
        			//Recuperar valores        		
        			codeCust = tabs.nextToken();
        			cose = tabs.nextToken();
        			
        			if (!cose.trim().equals("empty")) {
	        			sql = "SELECT * FROM customers WHERE cust_referralcomments = '" + codeCust + "'";
	        			pmConn.doFetch(sql);
	        			if (!pmConn.next()) {
	        				errors = "No existe el Cod de Cliente: " + codeCust;
	        			}	        				        			
	        			
	        			sql = "SELECT * FROM consultingservices WHERE cose_code = '" + cose.trim() + "'";
	        			pmConn.doFetch(sql);
	        			if (!pmConn.next()) {
	        				errors = "No existe el Consultor: " + cose;
	        			}
        			}	
        			
    		%>
    			
    				<TR class="reportCellEven" width="100%">
    					<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>
    					<%=HtmlUtil.formatReportCell(sFParams, codeCust, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, cose, BmFieldType.STRING)%>    					    				    
    				    <td><font color="red"><%= errors %></font></td>
    				</TR>
    		<%
    			
    			i++;        		
        	}
            %>
</TABLE>

	<TABLE cellpadding="0" cellspacing="0" border="0"  width="100%">
		<FORM action="flex_revisionConsultinginCust.jsp" method="POST" name="listFilter">	
		<input type="hidden" name="action" value="populate">
		<input type="hidden" name="populateData" value="<%= populateData %>">			
		<tr class="">
		    <td align="center" colspan="4" height="35" valign="middle">
		    <% if (errors.equals("")) { %>
		        <input type="submit" value="Cargar SIC en Clientes">
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
		
		String codeCust = "";
		String cose = "";		
		String errors = "";
			
				
		StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
		int i = 1;
		while (inputData.hasMoreTokens() && i < 20000) { 
			String sql = "";
			s = inputData.nextToken();
			StringTokenizer tabs = new StringTokenizer(s, "\t");
			
			codeCust = tabs.nextToken();
			cose = tabs.nextToken();        			    				
			
			int consultingId = 0;
			if (!cose.equals("empty")) {
				//Obtener el SIC
				sql = "SELECT * FROM consultingservices WHERE cose_code = '" + cose.trim() + "'";
    			pmConn.doFetch(sql);
    			if (pmConn.next()) {
    				consultingId = pmConn.getInt("cose_consultingserviceid");
    			}
    			
				if (consultingId > 0) {
					sql = "UPDATE customers SET cust_consultingserviceid = " + consultingId + 
						  " WHERE cust_referralcomments = " + codeCust + 
						  " AND cust_consultingserviceid is null";
					pmConn.doUpdate(sql);
				}
			}
		}
		
		if (!bmUpdateResult.hasErrors())
			pmConn.commit();
	
	} catch (Exception e) {
		pmConn.rollback();
		throw new SFException(e.toString());
		
	} finally {
		pmConn.close();
	}
		
	response.sendRedirect("/batch/flex_revisionConsultinginCust.jsp?action=complete&errorsave=" + bmUpdateResult.errorsToString());
		
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


