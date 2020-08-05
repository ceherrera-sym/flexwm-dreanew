
<%@include file="../inc/imports.jsp" %>
<%@page import="com.flexwm.server.cm.*"%>
<%@page import="com.flexwm.shared.cm.*"%>
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
	
	String type = "";    
    String firstName = "";
    String fatherLastName = "";
    String motherLastName = "";
    String birthDate = "";
    String title = "";    
    String salesManId= "";
    //Padre
    String father = "";
    String firstNameF = "";
    String fatherLastNameF = "";
    String motherLastNameF = "";
    //Madre
    String mother = "";
    String firstNameM = "";
    String fatherLastNameM = "";
    String motherLastNameM = "";
    String mobil = "";
   	String phone = "";
   	String email = "";
   	String principal = "";
    
	String errors = "";
	
%>
    
    <TR>
        <td colspan="4">  
            <TABLE cellpadding="0" cellspacing="0" border="1" rules=all width="90%">               
            	<tr valign="middle" align="center" class="">
                    <td class="reportHeaderCell">&nbsp;#</td>                    
                    <td class="reportHeaderCell">Tipo</td>
                    <td class="reportHeaderCell">Nombre</td>
                    <td class="reportHeaderCell">Paterno</td>
                    <td class="reportHeaderCell">Materno</td>
                    <td class="reportHeaderCell">Nacimiento</td>
                    <td class="reportHeaderCell">Genero</td>
                    <td class="reportHeaderCell">Vendedor</td>
                    <!-- Padre -->
                    <td class="reportHeaderCell">Padre</td>
                    <td class="reportHeaderCell">Nombre</td>
                    <td class="reportHeaderCell">Paterno</td>
                    <td class="reportHeaderCell">Materno</td>
                    <!-- Madre -->
                    <td class="reportHeaderCell">Madre</td>
                    <td class="reportHeaderCell">Nombre</td>
                    <td class="reportHeaderCell">Paterno</td>
                    <td class="reportHeaderCell">Materno</td>
                    <td class="reportHeaderCell">Celular</td>
                    <td class="reportHeaderCell">Telefono</td>
                    <td class="reportHeaderCell">Email</td>
                    <td class="reportHeaderCell">Principal</td>
                                        
                    <td class="reportHeaderCell">Errors</td>
                    
                </TR>
            <%           
        		while (inputData.hasMoreTokens() && i < 20000) {
        			
        			errors = "";
        			s = inputData.nextToken();
        			StringTokenizer tabs = new StringTokenizer(s, "\t");        			
        			
        			//Recuperar valores        		
        			type = tabs.nextToken();
				    firstName = tabs.nextToken();
				    fatherLastName = tabs.nextToken();
				    motherLastName = tabs.nextToken();
				    birthDate = tabs.nextToken();		    
				    title = tabs.nextToken();
				    salesManId = tabs.nextToken();
				    //Padre
				    father = tabs.nextToken();
				    firstNameF = tabs.nextToken();
				    fatherLastNameF = tabs.nextToken();
				    motherLastNameF = tabs.nextToken();
				  	//Madre
				    mother = tabs.nextToken();
				    firstNameM = tabs.nextToken();
				    fatherLastNameM = tabs.nextToken();
				    motherLastNameM = tabs.nextToken();
				    mobil = tabs.nextToken();
				   	phone = tabs.nextToken();
				   	email = tabs.nextToken();
				   	principal = tabs.nextToken();
        		     
        			//Validar los datos necesarios para la direccion del cliente
        			if (firstName.equals("")) errors = "Falta el Nombre";
        			
        			if (fatherLastName.equals("")) errors = "Falta el Paterno";
        			
        			if (!type.equals("C") && type.equals("P") && type.equals("empty")) {
            			errors = "No es valido el tipo de cliente: " + type;
            		}	
 %>       		
    				<TR class="reportCellEven" width="100%">
    					<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>    					    					
    					<%=HtmlUtil.formatReportCell(sFParams, type, BmFieldType.STRING)%>    					
    					<%=HtmlUtil.formatReportCell(sFParams, firstName, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, fatherLastName, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, motherLastName, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, birthDate, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, title, BmFieldType.STRING)%>    					
    					<%=HtmlUtil.formatReportCell(sFParams, salesManId, BmFieldType.STRING)%>
    					<!-- Padre  -->
    					<%=HtmlUtil.formatReportCell(sFParams, father, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, firstNameF, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, fatherLastNameF, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, motherLastNameF, BmFieldType.STRING)%>
    					<!-- Madre  -->
    					<%=HtmlUtil.formatReportCell(sFParams, mother, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, firstNameM, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, fatherLastNameM, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, motherLastNameM, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, mobil, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, phone, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, email, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, principal, BmFieldType.STRING)%>
    					
    				    <td><font color="red"><%= errors %></font></td>
    				</TR>
    		<%
    			
    			i++;        		
        	}
            %>
</TABLE>

	<TABLE cellpadding="0" cellspacing="0" border="0"  width="100%">
		<FORM action="flex_revisionflotiscust.jsp" method="POST" name="listFilter">	
		<input type="hidden" name="action" value="populate">
		<input type="hidden" name="populateData" value="<%= populateData %>">			
		<tr class="">
		    <td align="center" colspan="4" height="35" valign="middle">
		    <% if (errors.equals("")) { %>
		        <input type="submit" value="<%= programTitle %>">
		    <% } %>    
		    </td>
		</tr>			    
		</FORM>								
	</TABLE>
<% } else if (action.equals("populate")) {
	System.out.println("Entra Populate");
	BmUpdateResult bmUpdateResult = new BmUpdateResult();
	
	try {		
		
		pmConn.disableAutoCommit();
	
		ArrayList list = new ArrayList();
		String s = "";
		
		String type = "";    
	    String firstName = "";
	    String fatherLastName = "";
	    String motherLastName = "";
	    String birthDate = "";
	    String title = "";    
	    String salesManId= "";
	    //Padre
	    String father = "";
	    String firstNameF = "";
	    String fatherLastNameF = "";
	    String motherLastNameF = "";
	    //Madre
	    String mother = "";
	    String firstNameM = "";
	    String fatherLastNameM = "";
	    String motherLastNameM = "";
	    String mobil = "";
	   	String phone = "";
	   	String email = "";
	   	String principal = "";
	   	
		String errors = "";
			
		
		PmCustomer pmCustomer = new PmCustomer(sFParams);
		BmoCustomer bmoCustomer = new BmoCustomer();
		
		
		PmCustomerRelative pmCustomerRelative = new PmCustomerRelative(sFParams);
		BmoCustomerRelative bmoCustomerRelative = new BmoCustomerRelative();
				
		StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
		int i = 1, customerId = 0;
		while (inputData.hasMoreTokens() && i < 20000) { 
			String sql = "";
			s = inputData.nextToken();
			StringTokenizer tabs = new StringTokenizer(s, "\t");
			
			bmoCustomer = new BmoCustomer();
			
		    type = tabs.nextToken();
		    firstName = tabs.nextToken();
		    fatherLastName = tabs.nextToken();
		    motherLastName = tabs.nextToken();
		    birthDate = tabs.nextToken();		    
		    title = tabs.nextToken();
		    salesManId = tabs.nextToken();
		    //Padre
		    father = tabs.nextToken();
		    firstNameF = tabs.nextToken();
		    fatherLastNameF = tabs.nextToken();
		    motherLastNameF = tabs.nextToken();
		  	//Madre
		    mother = tabs.nextToken();
		    firstNameM = tabs.nextToken();
		    fatherLastNameM = tabs.nextToken();
		    motherLastNameM = tabs.nextToken();
		    mobil = tabs.nextToken();
		   	phone = tabs.nextToken();
		   	email = tabs.nextToken();
		   	principal = tabs.nextToken();
		    
		    int titleId = 0, userId = 0;
		    
		    bmoCustomer = new BmoCustomer();
		    //Tipo
		    bmoCustomer.getCustomertype().setValue(type);
		    
			//Cambio al Nombre
			firstName = firstName.toUpperCase().substring(0,1) + firstName.toLowerCase().substring(1,firstName.length());
			bmoCustomer.getFirstname().setValue(firstName);
			
			fatherLastName = fatherLastName.toUpperCase().substring(0,1) + fatherLastName.toLowerCase().substring(1,fatherLastName.length());
			bmoCustomer.getFatherlastname().setValue(fatherLastName);
			
			if (!motherLastName.equals("empty")) {
				motherLastName = motherLastName.toUpperCase().substring(0,1) + motherLastName.toLowerCase().substring(1,motherLastName.length());
				bmoCustomer.getMotherlastname().setValue(motherLastName);
			}
						
			if (!birthDate.equals("empty")) bmoCustomer.getBirthdate().setValue(birthDate);
			if (!title.equals("empty")) bmoCustomer.getTitleId().setValue(title);
			
			bmoCustomer.getSalesmanId().setValue(salesManId);
			
			bmUpdateResult = pmCustomer.save(pmConn, bmoCustomer, bmUpdateResult);
			
			//Obtener el Id del cliente
			customerId = bmUpdateResult.getId();
			
			System.out.println("customerId " + customerId);
			
			//Agregar el Padre
			bmoCustomerRelative = new BmoCustomerRelative();
			bmoCustomerRelative.getCustomerId().setValue(customerId);
			bmoCustomerRelative.getType().setValue(father);
			
			firstNameF = firstNameF.toUpperCase().substring(0,1) + firstNameF.toLowerCase().substring(1,firstNameF.length());
			bmoCustomerRelative.getFullName().setValue(firstNameF);
			
			if (!fatherLastNameF.equals("empty")) {
				fatherLastNameF = fatherLastNameF.toUpperCase().substring(0,1) + fatherLastNameF.toLowerCase().substring(1,fatherLastNameF.length());
				bmoCustomerRelative.getFatherLastName().setValue(fatherLastNameF);
			}
			
			if (!motherLastNameM.equals("empty")) {
				motherLastNameM = motherLastNameM.toUpperCase().substring(0,1) + motherLastNameM.toLowerCase().substring(1,motherLastNameM.length());
				bmoCustomerRelative.getMotherLastName().setValue(motherLastNameM);
			}
			
			if (!mobil.equals("empty")) {
				bmoCustomerRelative.getCellPhone().setValue(mobil);
			}
			
			if (!phone.equals("empty")) {
				bmoCustomerRelative.getNumber().setValue(phone);
			}
			
			if (!email.equals("empty")) {
				bmoCustomerRelative.getEmail().setValue(email);
			}
			
			bmoCustomerRelative.getResponsible().setValue(0);
			
			pmCustomerRelative.save(pmConn, bmoCustomerRelative, bmUpdateResult);
			
			//Agregar la Madre
			bmoCustomerRelative = new BmoCustomerRelative();
			bmoCustomerRelative.getCustomerId().setValue(customerId);
			bmoCustomerRelative.getType().setValue(mother);
			
			firstNameM = firstNameM.toUpperCase().substring(0,1) + firstNameM.toLowerCase().substring(1,firstNameM.length());
			bmoCustomerRelative.getFullName().setValue(firstNameM);
			
			if (!fatherLastNameM.equals("empty")) {
				fatherLastNameM = fatherLastNameM.toUpperCase().substring(0,1) + fatherLastNameM.toLowerCase().substring(1,fatherLastNameM.length());
				bmoCustomerRelative.getFatherLastName().setValue(fatherLastNameM);
			}
			
			if (!motherLastNameF.equals("empty")) {
				motherLastNameF = motherLastNameF.toUpperCase().substring(0,1) + motherLastNameF.toLowerCase().substring(1,motherLastNameF.length());
				bmoCustomerRelative.getMotherLastName().setValue(motherLastNameF);
			}
			
			if (!mobil.equals("empty")) {
				bmoCustomerRelative.getCellPhone().setValue(mobil);
			}
			
			if (!phone.equals("empty")) {
				bmoCustomerRelative.getNumber().setValue(phone);
			}
			
			if (!email.equals("empty")) {
				bmoCustomerRelative.getEmail().setValue(email);
			}
			
			bmoCustomerRelative.getResponsible().setValue(1);
			
			pmCustomerRelative.save(pmConn, bmoCustomerRelative, bmUpdateResult);
			 
		}
		
		if (!bmUpdateResult.hasErrors())
			pmConn.commit();
	
	} catch (Exception e) {
		pmConn.rollback();
		throw new SFException(e.toString() + bmUpdateResult.errorsToString());
		
	} finally {
		pmConn.close();
	}
	%>	
	<%= bmUpdateResult.errorsToString() %>
<%	
		
	response.sendRedirect("/batch/flex_revisionflotiscust.jsp?action=complete&errorsave=" + bmUpdateResult.errorsToString());
		
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


