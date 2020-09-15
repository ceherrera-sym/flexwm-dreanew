<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@page import="com.flexwm.server.op.PmSupplierCompany"%>
<%@page import="com.flexwm.shared.op.BmoSupplierCompany"%>
<%@include file="../inc/login.jsp" %>
<%
	//Variables
	String errorSave = "";
	String programTitle = "Creaci&oacute;n de Empresas de Proveedor";
	String errorLabel = request.getParameter("errorLabel");
	String errorText = request.getParameter("errorText");
	String errorException = request.getParameter("errorException");
	if(request.getParameter("errorsave") != null)
	 errorSave = request.getParameter("errorsave");
	String populateData = "", action = "";
	String errors = "";
	
	if (request.getParameter("action") != null) 
		action = request.getParameter("action");
%>
<html>
<head>
	<title>:::<%= programTitle %>:::</title>
	<link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>">
	<meta charset=utf-8>
</head>
<body class="default">
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		<tr>
			<td align="left" width="" rowspan="5" valign="top">
			    <img border="0" width="<%=SFParams.LOGO_WIDTH%>" height="<%=SFParams.LOGO_HEIGHT%>" src="<%= sFParams.getMainImageUrl() %>" >
			</td>
			<td colspan="" class="reportTitle">
			    &nbsp;<%= programTitle %>
			</td>
		</tr>
	</table>
	<% if (action.equalsIgnoreCase("")) { %>
		<table width="100%" height="50%" border="0" align="center"  class="detailStart">	
	    <tr class="reportSubTitle">
	        <td colspan="1" align="left" width="99%" height="35" class="reportCellEven">
	          <li>Crear las Empresas de Proveedor</li>	         
	        </td>
	    </tr>    
	    <TR>
	        <td colspan="3">  
	            <TABLE cellpadding="0" cellspacing="0" border="0"  width="80%">
				    <FORM action="?" method="post" name="listFilter">
				    <input type="hidden" name="action" value="execute">	             	
	                <tr class="">
	                    <td align="center" colspan="4" height="35" valign="middle">
	                        <input class="formSaveButton" type="submit" value="CREAR">
	                    </td>
	                </tr>			    
	                </FORM>								
	            </TABLE>
	        </TD>
	    </TR>
	</TABLE>
	
	
	<% } else if (action.equalsIgnoreCase("execute")) {%>	
			<%
				PmConn pmConn = new PmConn(sFParams);
				pmConn.open();	
				
				PmConn pmConn2 = new PmConn(sFParams);
				pmConn2.open();
				
				pmConn2.disableAutoCommit();
				
				BmUpdateResult bmUpdateResult = new BmUpdateResult();
				
				int companyId = 0;
				
				String sql = "SELECT comp_companyid FROM companies WHERE comp_name = 'DREA GDL'";
				
				pmConn.doFetch(sql);
				
				if (pmConn.next())companyId = pmConn.getInt("comp_companyid");
				
				sql = "SELECT supl_supplierid FROM suppliers";
				
				pmConn.doFetch(sql);
				
				while (pmConn.next()) {
					int supplierId = pmConn.getInt("supl_supplierid");
					BmoSupplierCompany bmoSupplierCompany = new BmoSupplierCompany();
					PmSupplierCompany pmSupplierCompany = new PmSupplierCompany(sFParams);		
					
					bmoSupplierCompany.getCompanyId().setValue(companyId);
					bmoSupplierCompany.getSupplierId().setValue(supplierId);
					
					pmSupplierCompany.save(pmConn2, bmoSupplierCompany, bmUpdateResult);					
					
				}
				
				if (!bmUpdateResult.hasErrors())
					pmConn2.commit();
				
				pmConn.close();
				pmConn2.close();
				
			%>
	<% } %>
</body>