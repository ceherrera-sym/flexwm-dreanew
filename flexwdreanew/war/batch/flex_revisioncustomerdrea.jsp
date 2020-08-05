
<%@include file="../inc/imports.jsp" %>
<%@page import="com.flexwm.server.cm.PmCustomer"%>
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="com.flexwm.server.cm.PmCustomerEmail"%>
<%@page import="com.flexwm.shared.cm.BmoCustomerEmail"%>
<%@page import="com.flexwm.server.cm.PmCustomerAddress"%>
<%@page import="com.flexwm.shared.cm.BmoCustomerAddress"%>
<%@page import="com.flexwm.server.cm.PmCustomerPhone"%>
<%@page import="com.flexwm.shared.cm.BmoCustomerPhone"%>
<%@page import="java.util.StringTokenizer"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/login.jsp" %>

<%
	String errorLabel = request.getParameter("errorLabel");
	String errorText = request.getParameter("errorText");
	String errorSave = request.getParameter("errorsave");
	String errorException = request.getParameter("errorException");
	String programTitle = "Importacion de Clientes";
	String programDescription = "Importacion de Clientes";
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
	
	ArrayList list = new ArrayList();
	String s = "";
	int i = 1;		
	StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
	
	// Nombre | Paterno | Materno | Telefono | Email | Tipo | Vendedor
	
    String firstName = "";    
    String fatherLastname = "";
    String motherLastname = "";
	String phone = "";
	String email = "";
	String type = "";	
	String salesman = "";	
	String errors = "";
	
	
	
%>
    
    <TR>
        <td colspan="4">  
            <TABLE cellpadding="0" cellspacing="0" border="1" rules=all width="90%">               
            	<tr valign="middle" align="center" class="">
                    <td class="reportHeaderCell">&nbsp;#</td>
                    <td class="reportHeaderCell">Nombre</td>                    	
                    <td class="reportHeaderCell">Paterno</td>                    
                    <td class="reportHeaderCell">Materno</td>
                    <td class="reportHeaderCell">Telefono</td>
                    <td class="reportHeaderCell">Email</td>
                    <td class="reportHeaderCell">Tipo</td>                    
                    <td class="reportHeaderCell">Vendedor</td>                    
                    <td class="reportHeaderCell">Errors</td>
                </TR>
            <%           
        		while (inputData.hasMoreTokens() && i < 20000) {
        			
        			errors = "";
        			s = inputData.nextToken();
        			StringTokenizer tabs = new StringTokenizer(s, "\t");        			
        			
        			//Recuperar valores    				
        			firstName = tabs.nextToken();
        			fatherLastname = tabs.nextToken();
        			motherLastname = tabs.nextToken();
        			phone = tabs.nextToken();
        			email = tabs.nextToken();
        			type = tabs.nextToken();
        			salesman = tabs.nextToken();
        			         			
	        		//Validar que el vendedor existe        			
	        		String sql = " select user_userid from users " + 
	        					 " where user_code like '" + salesman.trim() + "'";	        		
	    			pmConn2.doFetch(sql);
	    			if (!pmConn2.next()) {
	    				errors = "El vendedor no existe en Flex";
	    			} 
	    		
        			
    		%>
    			
    				<TR class="reportCellEven" width="100%">
    					<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>    					
    					<%=HtmlUtil.formatReportCell(sFParams, firstName, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, fatherLastname, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, motherLastname, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, phone, BmFieldType.STRING)%>
    				    <%=HtmlUtil.formatReportCell(sFParams, email, BmFieldType.STRING)%>
    				    <%=HtmlUtil.formatReportCell(sFParams, type, BmFieldType.STRING)%>    				    
    				    <%=HtmlUtil.formatReportCell(sFParams, salesman, BmFieldType.STRING)%>
    				    
    				    <td><font color="red"><%= errors %></font></td>
    				</TR>
    		<%
    			
    			i++;        		
        	}
            %>
</TABLE>

	<TABLE cellpadding="0" cellspacing="0" border="0"  width="100%">
		<FORM action="flex_revisioncustomerdrea.jsp" method="POST" name="listFilter">	
		<input type="hidden" name="action" value="populate">
		<input type="hidden" name="populateData" value="<%= populateData %>">			
		<tr class="">
		    <td align="center" colspan="4" height="35" valign="middle">
		    <% if (errors.equals("")) { %>
		        <input type="submit" value="Cargar Clientes">
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
		
		String firstName = "";    
	    String fatherLastname = "";
	    String motherLastname = "";
		String phone = "";
		String email = "";
		String type = "";	
		String salesman = "";
			
		
		PmCustomer pmCustomer = new PmCustomer(sFParams);
		
				
		StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
		int i = 1;
		while (inputData.hasMoreTokens() && i < 2000) { 
			String sql = "";
			s = inputData.nextToken();
			StringTokenizer tabs = new StringTokenizer(s, "\t");
			
			firstName = tabs.nextToken();
			fatherLastname = tabs.nextToken();
			motherLastname = tabs.nextToken();
			phone = tabs.nextToken();
			email = tabs.nextToken();
			type = tabs.nextToken();
			salesman = tabs.nextToken();
			
			System.out.println("firstName: "+firstName);
			System.out.println("fatherLastname: "+fatherLastname);
			System.out.println("motherLastname: "+motherLastname);
			System.out.println("phone: "+phone);
			System.out.println("email: "+email);
			System.out.println("type: "+type);
			System.out.println("salesman: "+salesman);

			int custId = 0;
			
			//Validar si existe el cliente
			sql = " select cust_customerid from customers " + 
			      " where cust_firstname = '" + firstName + "'" +
				  " and cust_fatherlastname like '" + fatherLastname + "'" +
			      " and cust_motherlastname like '" + motherLastname + "'";
			pmConn2.doFetch(sql);
			if (pmConn2.next()) {
				custId = pmConn2.getInt("cust_customerid");
			}
			
			if (!(custId > 0)) {
					BmoCustomer bmoCustomer = new BmoCustomer();			
					bmoCustomer.getCustomertype().setValue(type);
					
					if (type.equals(""+BmoCustomer.TYPE_PERSON)) {					
						//Cambio al Nombre
						//firstName = firstName.toUpperCase().substring(0,1) + firstName.toLowerCase().substring(1,firstName.length());
						bmoCustomer.getFirstname().setValue(firstName);
						
						//fatherLastname = fatherLastname.toUpperCase().substring(0,1) + fatherLastname.toLowerCase().substring(1,fatherLastname.length());
						bmoCustomer.getFatherlastname().setValue(fatherLastname);
						
						if (!motherLastname.equals("empty") || !motherLastname.equals("EMPTY")) {
							//motherLastname = motherLastname.toUpperCase().substring(0,1) + motherLastname.toLowerCase().substring(1,motherLastname.length());
							bmoCustomer.getMotherlastname().setValue(motherLastname);
						}
 					} else {
 						
						bmoCustomer.getFirstname().setValue(firstName);
						bmoCustomer.getFatherlastname().setValue(fatherLastname);
						
						if (!motherLastname.equals("empty") || !motherLastname.equals("EMPTY")) {
							bmoCustomer.getMotherlastname().setValue(motherLastname);
						}
						
 						bmoCustomer.getDisplayName().setValue(firstName + " " + fatherLastname+ " " +motherLastname);

 					}
					
					if (!phone.equals("empty"))
						bmoCustomer.getPhone().setValue(phone);
					
					if (!email.equals("empty"))
						bmoCustomer.getEmail().setValue(email);			
					
					//Tipo Clientes
					
					bmoCustomer.getStatus().setValue(BmoCustomer.STATUS_PROSPECT);
					
					
					//Obtener el Id del vendedor en flex
					//Validar que el vendedor existe
					int salesmanId = 0;
	        		sql = " select user_userid from users " + 
	        			  " where user_code like '" + salesman.trim() + "'";	        		
	    			pmConn2.doFetch(sql);
	    			if (pmConn2.next()) {
	    				salesmanId = pmConn2.getInt("user_userid");
	    			} 
					
					bmoCustomer.getSalesmanId().setValue(salesmanId);					
					pmCustomer.save(pmConn, bmoCustomer, bmUpdateResult);
					 
					
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
		
	response.sendRedirect("/batch/flex_revisioncustomerdrea.jsp?action=complete&errorsave=" + bmUpdateResult.errorsToString());
		
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


