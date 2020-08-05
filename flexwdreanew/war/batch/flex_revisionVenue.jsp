
<%@include file="../inc/imports.jsp" %>
<%@page import="com.flexwm.server.ev.PmVenue"%>
<%@page import="com.flexwm.shared.ev.BmoVenue"%>
<%@page import="java.util.StringTokenizer"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/login.jsp" %>

<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>

<%
	String errorLabel = request.getParameter("errorLabel");
	String errorText = request.getParameter("errorText");
	String errorSave = request.getParameter("errorsave");
	String errorException = request.getParameter("errorException");
	String programTitle = "Importacion de Salones";
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

int i = 1;	
String msg = "";
String sql = "";
String s = "";
String code = "";
String name = "";
String description = "";
String street = "";
String number = "";
String suburb = "";
String zip = "";
String city = "";
String www = "";
String contact = "";
String email = "";
String phone = "";
String extension = "";
String phone2 = "";
String extension2 = "";
String homeAddress = "";
String delegation = "";
String planes = "";
String errors = "";

try { 

if (action.equals("revision")) { 
	StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
	// Nombre | Descripcion | Calle | Numero | Colonio | C.P. | Ciudad | www | Contacto | Email | Telefono
	
%>
   <table width="80%" border="0" align="center"  class="">
    
    <TR>
        <td colspan="4">  
            <TABLE cellpadding="0" cellspacing="0" border="1" rules=all width="90%">               
            	<tr valign="middle" align="center" class="">
                    <td class="reportHeaderCell">&nbsp;#</td> 
 					<td class="reportHeaderCell">Codigo</td>
                    <td class="reportHeaderCell">Nombre</td>
                    <td class="reportHeaderCell">Descripción</td> 
                    <td class="reportHeaderCell">Calle</td>                    
                    <td class="reportHeaderCell">Numero</td>                    
                    <td class="reportHeaderCell">Colonia</td>                    
                    <td class="reportHeaderCell">C.P.</td>                    
                    <td class="reportHeaderCell">Ciudad</td>
                    <td class="reportHeaderCell">Web</td>
                    <td class="reportHeaderCell">Contacto</td>
                    <td class="reportHeaderCell">Email</td>
                    <td class="reportHeaderCell">Telefono</td>   
                    <td class="reportHeaderCell">Ext.</td>   
                    <td class="reportHeaderCell">Telefono2</td>   
                    <td class="reportHeaderCell">Ext2.</td>
                    <td class="reportHeaderCell">Domicilio Particular?</td>       
                    <td class="reportHeaderCell">Delegación</td>     
                    <td class="reportHeaderCell">Liga para planos</td>
                    <td class="reportHeaderCell">Errores</td>
                                          
                </TR>
            <%           
        		while (inputData.hasMoreTokens()) {
        			errors = "";
        			s = inputData.nextToken();
        			StringTokenizer tabs = new StringTokenizer(s, "\t");   
        			//Recuperar valores
        			code = tabs.nextToken();
        			name = tabs.nextToken();	
					description = tabs.nextToken();
					street = tabs.nextToken();	
					number = tabs.nextToken();	
					suburb = tabs.nextToken();
					zip = tabs.nextToken();
					city = tabs.nextToken();
					www = tabs.nextToken();
					contact = tabs.nextToken();
					email = tabs.nextToken();
					phone = tabs.nextToken();
        			extension = tabs.nextToken();
        			phone2 = tabs.nextToken();
        			extension2 = tabs.nextToken();
					homeAddress = tabs.nextToken();
					delegation = tabs.nextToken();
					planes = tabs.nextToken();
					//Buscar la Ciudad
					if (!city.equalsIgnoreCase("empty")){
						sql = " SELECT * FROM cities WHERE city_name = '" + city + "'";
						pmConn.doFetch(sql);
						if (!pmConn.next()) {
							errors = "La ciudad " + city + " No existe en el catalogo ";
						}
        			}
					
					if (!delegation.equalsIgnoreCase("empty")){
						sql = "select dele_delegationid from delegations where dele_name = '" + delegation + "'";
						pmConn.doFetch(sql);
						if (!pmConn.next()) {
							errors = "La Delegacion " + delegation + " No existe en el catalogo ";
						}
					}
					
					
        			
    		%>
    				<TR class="reportCellEven" width="100%">
    					<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>
    					<%=HtmlUtil.formatReportCell(sFParams, code, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, name, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, description, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, street, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, number, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, suburb, BmFieldType.STRING)%>    					
    					<%=HtmlUtil.formatReportCell(sFParams, zip, BmFieldType.STRING)%>    					
    					<%=HtmlUtil.formatReportCell(sFParams, city, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, www, BmFieldType.STRING)%>    					
    					<%=HtmlUtil.formatReportCell(sFParams, contact, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, email, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, phone, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, extension, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, phone2, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, extension2, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, homeAddress, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, delegation, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, planes, BmFieldType.STRING)%>
    				    <td><font color="red"><%= errors %></font></td>
    				</TR>
    		<%
    			i++;        		
        	}
            %>
</TABLE>

	<TABLE cellpadding="0" cellspacing="0" border="0"  width="100%">
		<FORM action="flex_revisionVenue.jsp" method="POST" name="listFilter">	
		<input type="hidden" name="action" value="populate">
		<input type="hidden" name="populateData" value="<%= populateData %>">			
		<tr class="">
		    <td align="center" colspan="4" height="35" valign="middle">
		    <% if (errors.equals("")) { %>
		        <input type="submit" value="Cargar Lugares">
		    <% } %>    
		    </td>
		</tr>			    
		</FORM>								
	</TABLE>
<% } else if (action.equals("populate")) {
	BmUpdateResult bmUpdateResult = new BmUpdateResult();
	
	try {		
		
		pmConn.disableAutoCommit();
		
		PmVenue pmVenue = new PmVenue(sFParams);
		BmoVenue bmoVenue = new BmoVenue();
		
		i = 1;
		StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
		while (inputData.hasMoreTokens() && i < 20000) {
			s = inputData.nextToken();
			StringTokenizer tabs = new StringTokenizer(s, "\t");
			
			//Recuperar valores		
			code = tabs.nextToken();
        	name = tabs.nextToken();	
			description = tabs.nextToken();
			street = tabs.nextToken();	
			number = tabs.nextToken();	
			suburb = tabs.nextToken();
			zip = tabs.nextToken();
			city = tabs.nextToken();
			www = tabs.nextToken();
			contact = tabs.nextToken();
			email = tabs.nextToken();
			phone = tabs.nextToken();
        	extension = tabs.nextToken();
        	phone2 = tabs.nextToken();
        	extension2 = tabs.nextToken();
			homeAddress = tabs.nextToken();
			delegation = tabs.nextToken();
			planes = tabs.nextToken();		    				
			
			bmoVenue = new BmoVenue();
			bmoVenue.getCode().setValue(code);
			bmoVenue.getName().setValue(name);
			
			if (!description.trim().equals("empty"))
				bmoVenue.getDescription().setValue(description);
			
			if (!street.trim().equals("empty"))
				bmoVenue.getStreet().setValue(street);
			
			if (!number.trim().equals("empty"))
				bmoVenue.getNumber().setValue(number);
			
			if (!suburb.trim().equals("empty"))
				bmoVenue.getNeighborhood().setValue(suburb);
						
			if (!zip.trim().equals("empty"))
				bmoVenue.getZip().setValue(zip);
			
			if (!city.trim().equals("empty")) {
				//Buscar la Ciudad
				sql = " SELECT * FROM cities WHERE city_name = '" + city + "'";
				pmConn2.doFetch(sql);
				if (pmConn2.next()) {
					bmoVenue.getCityId().setValue(pmConn2.getInt("city_cityid"));	
				}
				
			}			
		
			
			if (!www.trim().equals("empty")) {				
				bmoVenue.getWww().setValue(www);
			}
			
			if (!contact.trim().equals("empty")) {				
				bmoVenue.getContactName().setValue(contact);
			}
			
			if (!email.trim().equals("empty")) {				
				bmoVenue.getContactEmail().setValue(email);
			}
			
			if (!phone.trim().equals("empty"))
				bmoVenue.getContactPhone().setValue(phone);
			
			if (!extension.trim().equals("empty"))
				bmoVenue.getContactPhoneExt().setValue(extension);
			
			if (!phone2.trim().equals("empty"))
				bmoVenue.getContactPhone2().setValue(phone2);
			
			if (!extension2.trim().equals("empty"))
				bmoVenue.getContactPhoneExt2().setValue(extension2);
			
			if (homeAddress.trim().equals("empty"))homeAddress = "0";
			
			bmoVenue.getHomeAddress().setValue(homeAddress);
			
			
			if (!delegation.equalsIgnoreCase("empty")){
				sql = "select dele_delegationid from delegations where dele_name = '" + delegation + "'";
				pmConn.doFetch(sql);
				if (pmConn2.next()) {
					bmoVenue.getDelegationId().setValue(pmConn2.getInt("dele_delegationid"));	
				}
			}
			
			if (!planes.trim().equals("empty"))
				bmoVenue.getBluePrint().setValue(planes);
			
			pmVenue.saveSimple(pmConn, bmoVenue, bmUpdateResult); 
			
			if (bmUpdateResult.hasErrors())
				System.err.println("Code " + code + " : " + bmUpdateResult.errorsToString());
		
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
		
	response.sendRedirect(sFParams.getAppURL() + "/batch/flex_revisionVenue.jsp?action=complete&errorsave=" + bmUpdateResult.errorsToString());
		
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
	
	response.sendRedirect(sFParams.getAppURL() + "/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);

%>

<%
} finally {
	pmConn.close();
	pmConn2.close();
	
}
%>
</body>
</html>


