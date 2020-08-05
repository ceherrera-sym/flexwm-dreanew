

<%@include file="../inc/imports.jsp" %>
<%@page import="com.flexwm.server.op.PmSupplier"%>
<%@page import="com.flexwm.shared.op.BmoSupplier"%>
<%@page import="java.util.StringTokenizer"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/login.jsp" %>

<%
	String errorLabel = request.getParameter("errorLabel");
	String errorText = request.getParameter("errorText");
	String errorSave = request.getParameter("errorsave");
	System.out.println("errorSave::: "+errorSave);
	String errorException = request.getParameter("errorException");
	String programTitle = "Importacion de Proveedores";
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
if (action.equals("revision")) {
%>
	<table width="80%" border="0" align="center"  class="">
<%

	PmConn pmConn = new PmConn(sFParams);
	pmConn.open();
	
	PmConn pmConn2 = new PmConn(sFParams);
	pmConn2.open();

	try {		
		String msg = "";
		String sql = "";
		
		String s = "";
		int i = 1;		
		StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
		
		String code = ""; 
		String name = "";
		String legalName = "";
		String description = "";
		String type = "";	
		String category = "";	
		String rfc = "";	
		String legalRep = "";
		String nss = "";
		String currency = "";
		String fiscalType = "";
		String phone = "";
		String email = "";
		String sendMail = "";
		String lawyernumber = "";
		String lawyername = "";
		String lawyerdeed = "";
		String lawyerdeeddate = "";
		String citydeed = "";		
		String errors = "";		
%>    
    <TR>
        <td colspan="4">  
            <TABLE cellpadding="0" cellspacing="0" border="1" rules=all width="90%">               
            	<tr valign="middle" align="center" class="">
                    <td class="reportHeaderCell">&nbsp;#</td>   
                    <td class="reportHeaderCell">Codigo</td>                  
                    <td class="reportHeaderCell">Nombre</td> 
                    <td class="reportHeaderCell">Razón Social</td>
                    <td class="reportHeaderCell">Descripción</td>
                    <td class="reportHeaderCell">Tipo</td>                    
                    <td class="reportHeaderCell">Categoria</td>  
                    <td class="reportHeaderCell">RFC</td>                   
                    <td class="reportHeaderCell">Representante Legal</td>
                    <td class="reportHeaderCell">IMSS</td> 
                    <td class="reportHeaderCell">Moneda</td>
                    <td class="reportHeaderCell">Regimen</td>
                    <td class="reportHeaderCell">Telefono</td>
                    <td class="reportHeaderCell">Email</td>
                    <td class="reportHeaderCell">Email Conciliar?</td>
                    <td class="reportHeaderCell">Notaria No.</td>
                    <td class="reportHeaderCell">Nom. Notario</td>
                    <td class="reportHeaderCell">No.Escritura</td>
                    <td class="reportHeaderCell">Fecha Escritura</td>
                    <td class="reportHeaderCell">Ciudad Escritura</td>
					<td class="reportHeaderCell">Errores</td>     
                </TR>
            <%           
        		while (inputData.hasMoreTokens() && i < 20000) {
        			
        			errors = "";
        			s = inputData.nextToken();
        			StringTokenizer tabs = new StringTokenizer(s, "\t");   
        			
        			//Recuperar valores
        			code = tabs.nextToken();
        			name = tabs.nextToken();
					legalName = tabs.nextToken();
					description = tabs.nextToken();
					type = tabs.nextToken();			
					category = tabs.nextToken();	
					rfc = tabs.nextToken();	
					legalRep = tabs.nextToken();
					nss = tabs.nextToken();	
					currency = tabs.nextToken();
					fiscalType = tabs.nextToken();
					phone = tabs.nextToken();
					email = tabs.nextToken();
					sendMail = tabs.nextToken();
					lawyernumber = tabs.nextToken();
					lawyername = tabs.nextToken();
					lawyerdeed = tabs.nextToken();
					lawyerdeeddate = tabs.nextToken();
					citydeed = tabs.nextToken();
        			
					if (name.equalsIgnoreCase("empty"))
						errors = "El Nombre no debe estar vacío";
					
					if (legalName.equalsIgnoreCase("empty"))
						errors = "La Razón Social no debe estar vacía";
					
					// Validar Categoria
        			if (!category.equalsIgnoreCase("empty")) {
	        			sql = " SELECT * FROM suppliercategories " +
	          			      " WHERE spca_name LIKE '" + category.trim() + "'";
	        			pmConn2.doFetch(sql);
	        			if (!pmConn2.next()) {
	        				errors = "La Categoría del Proveedor no Existe: " + category + ", " ;
	        			}
        			}
					
        			// Validar Moneda
        			if (!currency.equalsIgnoreCase("empty")) {
	        			sql = " SELECT * FROM currencies " +
	          			      " WHERE cure_code LIKE '" + currency + "'";
	        			pmConn2.doFetch(sql);
	        			if (!pmConn2.next()) {
	        				errors = "La Moneda no Existe: " + currency + ", " ;
	        			}
        			}
        			
//         			if (!citydeed.equalsIgnoreCase("empty")){
//         				sql = "select city_cityid from cities where city_name = '" + citydeed + "'";
//         				pmConn2.doFetch(sql);
// 	        			if (!pmConn2.next()) {
// 	        				errors = "La Ciudad no Existe: " + citydeed + ", "  ;
// 	        			}
//         			}
    		%>
    			
    				<TR class="reportCellEven" width="100%">
    					<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>
    					<%=HtmlUtil.formatReportCell(sFParams, code, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, name, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, legalName, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, description, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, type, BmFieldType.STRING)%>    					
    					<%=HtmlUtil.formatReportCell(sFParams, category, BmFieldType.STRING)%> 
    					<%=HtmlUtil.formatReportCell(sFParams, rfc, BmFieldType.STRING)%>   					
    					<%=HtmlUtil.formatReportCell(sFParams, legalRep, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, nss, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, currency, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, fiscalType, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, phone, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, email, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, sendMail, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, lawyernumber, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, lawyername, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, lawyerdeed, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, lawyerdeeddate, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, citydeed, BmFieldType.STRING)%>
    				    <td><font color="red"><%= errors %></font></td>
    				</TR>
    		<%
    			i++;        		
        	}
            %>
</TABLE>

	<TABLE cellpadding="0" cellspacing="0" border="0"  width="100%">
		<FORM action="flex_revisionSupplier.jsp" method="POST" name="listFilter">	
		<input type="hidden" name="action" value="populate">
		<input type="hidden" name="populateData" value="<%= populateData %>">			
		<tr class="">
		    <td align="center" colspan="4" height="35" valign="middle">
		    <% if (errors.equals("")) { %>
		        <input type="submit" class="formSaveButton" value="Cargar Proveedores">
		    <% } %>    
		    </td>
		</tr>			    
		</FORM>								
	</TABLE>
	<%
	} catch (Exception e) {
			pmConn.rollback();
			throw new SFException(e.toString());
			
		} finally {
			pmConn.close();
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
		String code = ""; 
		String name = "";
		String legalName = "";
		String description = "";
		String type = "";	
		String category = "";	
		String rfc = "";	
		String legalRep = "";
		String nss = "";
		String currency = "";
		String fiscalType = "";
		String phone = "";
		String email = "";
		String sendMail = "";
		String lawyernumber = "";
		String lawyername = "";
		String lawyerdeed = "";
		String lawyerdeeddate = "";
		String citydeed = "";		
		int categoryId = 0;
		int currencyId = 0;
		
		PmSupplier pmSupplier = new PmSupplier(sFParams);
		BmoSupplier bmoSupplier = new BmoSupplier();
		
		StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
		int i = 1;
		while (inputData.hasMoreTokens() && i < 20000) { 
			String sql = "";
			s = inputData.nextToken();
			StringTokenizer tabs = new StringTokenizer(s, "\t");
			
			//Recuperar valores
			code = tabs.nextToken();
        			name = tabs.nextToken();
					legalName = tabs.nextToken();
					description = tabs.nextToken();
					type = tabs.nextToken();			
					category = tabs.nextToken();	
					rfc = tabs.nextToken().trim();	
					legalRep = tabs.nextToken();
					nss = tabs.nextToken();	
					currency = tabs.nextToken();
					fiscalType = tabs.nextToken();
					phone = tabs.nextToken();
					email = tabs.nextToken();
					sendMail = tabs.nextToken();
					lawyernumber = tabs.nextToken();
					lawyername = tabs.nextToken();
					lawyerdeed = tabs.nextToken();
					lawyerdeeddate = tabs.nextToken();
					citydeed = tabs.nextToken();		    				
			System.err.println("CODE: " +code);
			bmoSupplier = new BmoSupplier();
			bmoSupplier.getName().setValue(name);
			bmoSupplier.getType().setValue(type.trim());
		
			// Validar Categoria
			if (!category.equalsIgnoreCase("empty")) {
    			sql = " SELECT spca_suppliercategoryid FROM suppliercategories " +
      			      " WHERE spca_name LIKE '" + category.trim() + "'";
    			pmConn2.doFetch(sql);
    			if (pmConn2.next()) {
    				categoryId = pmConn2.getInt("spca_suppliercategoryid");
    			}
			}
			// Validar Moneda
			if (!currency.equalsIgnoreCase("empty")) {
    			sql = " SELECT cure_currencyid FROM currencies " +
      			      " WHERE cure_code LIKE '" + currency.trim() + "'";
    			pmConn2.doFetch(sql);
    			if (pmConn2.next()) {
    				currencyId = pmConn2.getInt("cure_currencyid");
    			}
			}
			bmoSupplier.getCode().setValue(code);
			bmoSupplier.getName().setValue(name);
			bmoSupplier.getLegalName().setValue(legalName);
			if (!description.trim().equals("empty"))
				bmoSupplier.getDescription().setValue(description);
			if (!type.equals("empty"))
				bmoSupplier.getType().setValue(type);
			if (categoryId > 0)				
			bmoSupplier.getSupplierCategoryId().setValue(categoryId);
			if (!rfc.equals("empty"))
				bmoSupplier.getRfc().setValue(rfc);		
			if (!legalRep.trim().equals("empty"))
				bmoSupplier.getLegalRep().setValue(legalRep);
			if (!nss.equals("empty"))
				bmoSupplier.getImss().setValue(nss);				
			if (currencyId > 0)
				bmoSupplier.getCurrencyId().setValue(currencyId);
			if (!fiscalType.equals("empty"))
				bmoSupplier.getFiscalType().setValue(fiscalType);
			
			if (!phone.trim().equals("empty"))
				bmoSupplier.getOfficePhone1().setValue(phone);
			
			if (!email.trim().equals("empty"))
				bmoSupplier.getEmail().setValue(email);
			if (sendMail.equals("empty"))
				sendMail = "0";
			
			bmoSupplier.getSendEmail().setValue(sendMail);
			
			bmoSupplier.getLawyerNumber().setValue(lawyernumber);
			bmoSupplier.getLawyerName().setValue(lawyername);
			bmoSupplier.getLawyerDeed().setValue(lawyerdeed);
			bmoSupplier.getLawyerDeedDate().setValue(lawyerdeeddate);
			pmSupplier.save(pmConn, bmoSupplier, bmUpdateResult); 
		}
		
		if (!bmUpdateResult.hasErrors())
			pmConn.commit();
		else {
			
			%>
			Erroresss: <%= bmUpdateResult.errorsToString()%>
			<%
		}
	
	} catch (Exception e) {
		pmConn.rollback();
		throw new SFException(e.toString());
		
	} finally {
		pmConn.close();
		pmConn2.close();
	}
		
	response.sendRedirect("flex_revisionSupplier.jsp?action=complete&errorsave=" + bmUpdateResult.errorsToString());
		
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
		</td>
	</tr>
	</table>

<% } %>

</body>
</html>


