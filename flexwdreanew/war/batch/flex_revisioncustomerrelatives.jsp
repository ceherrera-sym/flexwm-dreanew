
<%@include file="../inc/imports.jsp" %>
<%@page import="com.flexwm.server.cm.PmCustomerRelative"%>
<%@page import="com.flexwm.shared.cm.BmoCustomerRelative"%>
<%@page import="java.util.StringTokenizer"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/login.jsp" %>

<%
	String errorLabel = request.getParameter("errorLabel");
	String errorText = request.getParameter("errorText");
	String errorSave = request.getParameter("errorsave");
	String errorException = request.getParameter("errorException");
	String programTitle = "Importacion de Familiares de Clientes";
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
	
	String customerName = "";
	String customerFatherLastName = "";
	String customerMotherLastName = "";
	String name = "";
	String fatherLastname = "";
	String motherLastname = "";
    String type = "";    
    String email = "";
    String phone = "";
    String ext = "";
    String mobile = "";	
    String responsible = "";
	String errors = "";
	
%>  
    <TR>
        <td colspan="4">  
            <TABLE cellpadding="0" cellspacing="0" border="1" rules=all width="90%">               
            	<tr valign="middle" align="center" class="">
                    <td class="reportHeaderCell">&nbsp;#</td>
                    <td class="reportHeaderCell">Cliente</td>
                    <td class="reportHeaderCell">Paterno</td>
                    <td class="reportHeaderCell">Materno</td>
                    <td class="reportHeaderCell">Nombre</td>
                    <td class="reportHeaderCell">Paterno</td>
                    <td class="reportHeaderCell">Materno</td>
                    <td class="reportHeaderCell">Tipo</td>                    
                    <td class="reportHeaderCell">Email</td>
                    <td class="reportHeaderCell">Telefono</td>
                    <td class="reportHeaderCell">Ext</td>
                    <td class="reportHeaderCell">Celular</td>
                    <td class="reportHeaderCell">Responsable</td>
                    <td class="reportHeaderCell">Errors</td>
                </TR>
            <%           
        		while (inputData.hasMoreTokens() && i < 2000) {
        			
        			errors = "";
        			s = inputData.nextToken();
        			StringTokenizer tabs = new StringTokenizer(s, "\t");          			
        			
        			//Recuperar valores        		
        			customerName = tabs.nextToken();
        			customerFatherLastName = tabs.nextToken();
        			customerMotherLastName = tabs.nextToken();
        			name = tabs.nextToken();
        			fatherLastname = tabs.nextToken();
        			motherLastname = tabs.nextToken();        		    
        		    type = tabs.nextToken();
        		    email = tabs.nextToken();
        		    phone = tabs.nextToken();
        		    ext = tabs.nextToken();
        		    mobile = tabs.nextToken();	
        		    responsible = tabs.nextToken();
        			errors = "";
        			
        			//Validar los datos necesarios para la direccion del cliente
        		    if (name.equals("")) errors = "Falta el Nombre";
        		    else if (phone.equals("")) errors = "Falta el telefono";
        		    
        		    //Cambio al Nombre
        		    customerName = customerName.toUpperCase().substring(0,1) + customerName.toLowerCase().substring(1,customerName.length());
        		    customerFatherLastName = customerFatherLastName.toUpperCase().substring(0,1) + customerFatherLastName.toLowerCase().substring(1,customerFatherLastName.length());
        			
        			
        			if (!customerMotherLastName.equals("empty")) {
        				customerMotherLastName = customerMotherLastName.toUpperCase().substring(0,1) + customerMotherLastName.toLowerCase().substring(1,customerMotherLastName.length());
        			}
        		    
        		    //Validar el cliente
        		    sql = " SELECT * FROM customers " + 
        		          " WHERE cust_firstname like '" + customerName + "'" +
        		          " AND cust_fatherlastname like '" + customerFatherLastName + "'" +
        		          " AND cust_motherlastname like '" + customerMotherLastName + "'";        		    
        		    pmConn2.doFetch(sql);
        		    if (!pmConn2.next()) errors = "El cliente no existe";
        		  
        			        			
        			
    		%>
    			
    				<TR class="reportCellEven" width="100%">
    					<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>
    					<%=HtmlUtil.formatReportCell(sFParams, customerName, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, customerFatherLastName, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, customerMotherLastName, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, name, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, fatherLastname, BmFieldType.STRING)%>    					
    					<%=HtmlUtil.formatReportCell(sFParams, motherLastname, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, type, BmFieldType.STRING)%>    					
    					<%=HtmlUtil.formatReportCell(sFParams, email, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, phone, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, ext, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, mobile, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, responsible, BmFieldType.STRING)%>
    				    <td><font color="red"><%= errors %></font></td>
    				</TR>
    		<%
    			
    			i++;        		
        	}
            %>
</TABLE>

	<TABLE cellpadding="0" cellspacing="0" border="0"  width="100%">
		<FORM action="flex_revisioncustomerrelatives.jsp" method="POST" name="listFilter">	
		<input type="hidden" name="action" value="populate">
		<input type="hidden" name="populateData" value="<%= populateData %>">			
		<tr class="">
		    <td align="center" colspan="4" height="35" valign="middle">
		    <% if (errors.equals("")) { %>
		        <input type="submit" value="Cargar Familiares">
		    <% } %>    
		    </td>
		</tr>			    
		</FORM>								
	</TABLE>
<% } else if (action.equals("populate")) {
	
	System.out.println("Entra populate");
	BmUpdateResult bmUpdateResult = new BmUpdateResult();
	
	try {		
		
		pmConn.disableAutoCommit();
	
		ArrayList list = new ArrayList();
		String s = "";
		
		String customerName = "";
		String customerFatherLastName = "";
		String customerMotherLastName = "";
		String name = "";
		String fatherLastname = "";
		String motherLastname = "";
	    String type = "";    
	    String email = "";
	    String phone = "";
	    String ext = "";
	    String mobile = "";	
	    String responsible = "";
			
	    System.out.println("Entra variables");
	    
		PmCustomerRelative pmCustomerRelative = new PmCustomerRelative(sFParams);
		BmoCustomerRelative bmoCustomerRelative = new BmoCustomerRelative();
		
		System.out.println("Entra CustomerRelatives");
				
		StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
		int i = 1;
		while (inputData.hasMoreTokens() && i < 20000) { 
			String sql = "";
			s = inputData.nextToken();
			StringTokenizer tabs = new StringTokenizer(s, "\t");
			
			System.out.println("Entra StringTokenizer");
			
			customerName = tabs.nextToken();
			customerFatherLastName = tabs.nextToken();
			customerMotherLastName = tabs.nextToken();
			name = tabs.nextToken();
			fatherLastname = tabs.nextToken();
			motherLastname = tabs.nextToken();		    
		    type = tabs.nextToken();
		    email = tabs.nextToken();
		    phone = tabs.nextToken();
		    ext = tabs.nextToken();
		    mobile = tabs.nextToken();	
		    responsible = tabs.nextToken();
		    
			
		    int customerId = 0;
		    //Validar el cliente
		    //Cambio al Nombre
		    customerName = customerName.toUpperCase().substring(0,1) + customerName.toLowerCase().substring(1,customerName.length());
		    customerFatherLastName = customerFatherLastName.toUpperCase().substring(0,1) + customerFatherLastName.toLowerCase().substring(1,customerFatherLastName.length());
		    
			if (!customerMotherLastName.equals("empty")) {
				customerMotherLastName = customerMotherLastName.toUpperCase().substring(0,1) + customerMotherLastName.toLowerCase().substring(1,customerMotherLastName.length());
			}
		    
		    
		    sql = " SELECT cust_customerid FROM customers " + 
		    	  " WHERE cust_firstname = '" + customerName + "'" +
  		          " AND cust_fatherlastname = '" + customerFatherLastName + "'" +
  		          " AND cust_motherlastname = '" + customerMotherLastName + "'";
		    pmConn2.doFetch(sql);
		    if (pmConn2.next()) customerId = pmConn2.getInt("cust_customerid");
		    
		    System.out.println("Entra customerId " + customerId);
			
			
			bmoCustomerRelative = new BmoCustomerRelative();
			bmoCustomerRelative.getCustomerId().setValue(customerId);
			
			bmoCustomerRelative.getNumber().setValue(phone);
			
			//Cambio al Nombre
			name = name.toUpperCase().substring(0,1) + name.toLowerCase().substring(1,name.length());
			bmoCustomerRelative.getFullName().setValue(name);
			
			if (!motherLastname.equals("empty")) {
				motherLastname = motherLastname.toUpperCase().substring(0,1) + motherLastname.toLowerCase().substring(1,motherLastname.length());
				bmoCustomerRelative.getMotherLastName().setValue(motherLastname);
			}
			
			if (!fatherLastname.equals("empty")) {
				fatherLastname = fatherLastname.toUpperCase().substring(0,1) + fatherLastname.toLowerCase().substring(1,fatherLastname.length());
				bmoCustomerRelative.getFatherLastName().setValue(fatherLastname);
			}
			
			 
			if (!type.equals("empty")) bmoCustomerRelative.getType().setValue(type);						
			if (!email.equals("empty")) bmoCustomerRelative.getEmail().setValue(email);
			if (!ext.equals("empty")) bmoCustomerRelative.getExtension().setValue(ext);
			if (!phone.equals("empty")) bmoCustomerRelative.getNumber().setValue(phone);
			if (!mobile.equals("empty")) bmoCustomerRelative.getCellPhone().setValue(mobile);
			if (!responsible.equals("empty")) bmoCustomerRelative.getResponsible().setValue(responsible.trim());
			
			pmCustomerRelative.save(pmConn, bmoCustomerRelative, bmUpdateResult);
			 
		}
		
		System.out.println("sale while");
		
		if (!bmUpdateResult.hasErrors())
			pmConn.commit();
		
		System.out.println("sale commit " + bmUpdateResult.errorsToString());
	
	} catch (Exception e) {
		pmConn.rollback();
		throw new SFException(e.toString());
		
	} finally {
		pmConn.close();
	}
		
	response.sendRedirect("/batch/flex_revisioncustomerrelatives.jsp?action=complete&errorsave=" + bmUpdateResult.errorsToString());
		
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


