
<%@include file="../inc/imports.jsp" %>
<%@page import="com.flexwm.server.cm.PmCustomerContact"%>
<%@page import="com.flexwm.shared.cm.BmoCustomerContact"%>
<%@page import="java.util.StringTokenizer"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/login.jsp" %>

<%
	String errorLabel = request.getParameter("errorLabel");
	String errorText = request.getParameter("errorText");
	String errorSave = request.getParameter("errorsave");
	String errorException = request.getParameter("errorException");
	String programTitle = "Importacion de Contactos de Clientes";
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
	String name = "";
	String fatherLastname = "";
	String motherLastname = "";
    String alias = "";
    String position = "";
    String email = "";
    String phone = "";
    String ext = "";
    String mobile = "";	
    String comments = "";
	String errors = "";
	
%>  
    <TR>
        <td colspan="4">  
            <TABLE cellpadding="0" cellspacing="0" border="1" rules=all width="90%">               
            	<tr valign="middle" align="center" class="">
                    <td class="reportHeaderCell">&nbsp;#</td>
                    <td class="reportHeaderCell">Cod.Cliente</td>                    
                    <td class="reportHeaderCell">Nombre</td>
                    <td class="reportHeaderCell">Paterno</td>
                    <td class="reportHeaderCell">Materno</td>
                    <td class="reportHeaderCell">Alias</td>
                    <td class="reportHeaderCell">Puesto</td>
                    <td class="reportHeaderCell">Email</td>
                    <td class="reportHeaderCell">Telefono</td>
                    <td class="reportHeaderCell">Ext</td>
                    <td class="reportHeaderCell">Celular</td>
                    <td class="reportHeaderCell">Comentarios</td>
                    <td class="reportHeaderCell">Errors</td>
                </TR>
            <%           
        		while (inputData.hasMoreTokens() && i < 2000) {
        			
        			errors = "";
        			s = inputData.nextToken();
        			StringTokenizer tabs = new StringTokenizer(s, "\t");          			
        			
        			//Recuperar valores        		
        			codeCustomer = tabs.nextToken();
        			name = tabs.nextToken();
        			fatherLastname = tabs.nextToken();
        			motherLastname = tabs.nextToken();
        		    alias = tabs.nextToken();
        		    position = tabs.nextToken();
        		    email = tabs.nextToken();
        		    phone = tabs.nextToken();
        		    ext = tabs.nextToken();
        		    mobile = tabs.nextToken();	
        		    comments = tabs.nextToken();
        			errors = "";
        			
        			//Validar los datos necesarios para la direccion del cliente
        		    if (name.equals("")) errors = "Falta el Nombre";
        		    else if (phone.equals("")) errors = "Falta el telefono";
        		    
        		    //Validar el cliente
        		    sql = " SELECT * FROM customers " + 
        		          " WHERE cust_referralcomments = '" + codeCustomer + "'";
        		    pmConn2.doFetch(sql);
        		    if (!pmConn2.next()) errors = "El codigo de cliente no esta ligada a un cliente";
        		  
        			        			
        			
    		%>
    			
    				<TR class="reportCellEven" width="100%">
    					<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>
    					<%=HtmlUtil.formatReportCell(sFParams, codeCustomer, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, name, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, fatherLastname, BmFieldType.STRING)%>    					
    					<%=HtmlUtil.formatReportCell(sFParams, motherLastname, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, alias, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, position, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, email, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, phone, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, ext, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, mobile, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, comments, BmFieldType.STRING)%>
    				    <td><font color="red"><%= errors %></font></td>
    				</TR>
    		<%
    			
    			i++;        		
        	}
            %>
</TABLE>

	<TABLE cellpadding="0" cellspacing="0" border="0"  width="100%">
		<FORM action="flex_revisioncustomercontacts.jsp" method="POST" name="listFilter">	
		<input type="hidden" name="action" value="populate">
		<input type="hidden" name="populateData" value="<%= populateData %>">			
		<tr class="">
		    <td align="center" colspan="4" height="35" valign="middle">
		    <% if (errors.equals("")) { %>
		        <input type="submit" value="Cargar Contactos">
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
		String name = "";
		String fatherLastname = "";
		String motherLastname = "";
	    String alias = "";
	    String position = "";
	    String email = "";
	    String phone = "";
	    String ext = "";
	    String mobile = "";	
	    String comments = "";
		String errors = "";
			
		
		PmCustomerContact pmCustomerContact = new PmCustomerContact(sFParams);
		BmoCustomerContact bmoCustomerContact = new BmoCustomerContact();
		
				
		StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
		int i = 1;
		while (inputData.hasMoreTokens() && i < 20000) { 
			String sql = "";
			s = inputData.nextToken();
			StringTokenizer tabs = new StringTokenizer(s, "\t");
			
			codeCustomer = tabs.nextToken();
			name = tabs.nextToken();
			fatherLastname = tabs.nextToken();
			motherLastname = tabs.nextToken();
		    alias = tabs.nextToken();
		    position = tabs.nextToken();
		    email = tabs.nextToken();
		    phone = tabs.nextToken();
		    ext = tabs.nextToken();
		    mobile = tabs.nextToken();	
		    comments = tabs.nextToken();
			errors = "";
			
		    int customerId = 0;
		    //Validar el cliente
		    sql = " SELECT cust_customerid FROM customers " + 
		          " WHERE cust_referralcomments = '" + codeCustomer + "'";
		    pmConn2.doFetch(sql);
		    if (pmConn2.next()) customerId = pmConn2.getInt("cust_customerid");
		    
			
			
			
			bmoCustomerContact = new BmoCustomerContact();
			bmoCustomerContact.getCustomerId().setValue(customerId);
			bmoCustomerContact.getFullName().setValue(name);
			bmoCustomerContact.getNumber().setValue(phone);
			
			if (!fatherLastname.equals("empty")) bmoCustomerContact.getFatherLastName().setValue(fatherLastname);
			if (!motherLastname.equals("empty")) bmoCustomerContact.getMotherLastName().setValue(motherLastname);
			if (!alias.equals("empty")) bmoCustomerContact.getAlias().setValue(alias);
			if (!position.equals("empty")) bmoCustomerContact.getPosition().setValue(position);			
			if (!email.equals("empty")) bmoCustomerContact.getEmail().setValue(email);
			if (!ext.equals("empty")) bmoCustomerContact.getExtension().setValue(ext);
			if (!mobile.equals("empty")) bmoCustomerContact.getCellPhone().setValue(mobile);
			if (!comments.equals("empty")) bmoCustomerContact.getCommentAlias().setValue(comments);
			
			
			pmCustomerContact.save(pmConn, bmoCustomerContact, bmUpdateResult);
			 
		}
		
		if (!bmUpdateResult.hasErrors())
			pmConn.commit();
	
	} catch (Exception e) {
		pmConn.rollback();
		throw new SFException(e.toString());
		
	} finally {
		pmConn.close();
	}
		
	response.sendRedirect("/batch/flex_revisioncustomercontacts.jsp?action=complete&errorsave=" + bmUpdateResult.errorsToString());
		
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


