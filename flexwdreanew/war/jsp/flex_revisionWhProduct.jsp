
<%@include file="../inc/imports.jsp"%>
<%@page import="com.flexwm.server.op.PmWhMovement"%>
<%@page import="com.flexwm.shared.op.BmoWhMovement"%>
<%@page import="com.flexwm.shared.op.BmoWhSection"%>
<%@page import="com.flexwm.server.op.PmWhSection"%>
<%@page import="com.flexwm.server.op.PmProduct"%>
<%@page import="com.flexwm.shared.op.BmoProduct"%>
<%@page import="com.flexwm.server.op.PmWhMovItem"%>
<%@page import="com.flexwm.shared.op.BmoWhMovItem"%>
<%@page import="java.util.StringTokenizer"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>

<%@include file="../inc/login.jsp"%>

<%
	String errorLabel = request.getParameter("errorLabel");
	String errorText = request.getParameter("errorText");
	String errorException = request.getParameter("errorException");
	String programTitle = "Importacion de Existencias";
	String programDescription = programTitle;
	String populateData = "", action = "";
	String errorSave = "";
	
	if (request.getParameter("populateData") != null) populateData = request.getParameter("populateData");
	if (request.getParameter("action") != null) action = request.getParameter("action");
	if (request.getParameter("errorsave") != null) errorSave = request.getParameter("errorsave");
%>

<html>
<head>
<title>:::<%= programTitle %> :::
</title>
<link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>">
<link rel="stylesheet" type="text/css" href="../css/flexwm.css">
<meta charset=utf-8>
</head>
<body class="default">
	<table border="0" cellspacing="0" width="100%" cellpadding="0"
		style="font-size: 12px">
		<tr>
			<td align="left" width="" rowspan="5" valign="top"><img
				border="0" width="<%=SFParams.LOGO_WIDTH%>"
				height="<%=SFParams.LOGO_HEIGHT%>"
				src="<%= sFParams.getMainImageUrl() %>"></td>
			<td colspan="" class="reportTitle">&nbsp;<%= programTitle %>
			</td>
		</tr>
	</table>

	<% 

PmConn pmConn = new PmConn(sFParams);
pmConn.open();

PmConn pmConn2 = new PmConn(sFParams);
pmConn2.open();

BmUpdateResult bmUpdateResult = new BmUpdateResult();


try { 

if (action.equals("revision")) { %>
	<table width="80%" border="0" align="center" class="">

		<%
	String msg = "";
	
	String s = "";
	int i = 1;		
	StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
	
	String cons = "";
	String wareCode = "";	
    String prodCode = "";    
    String prodName = "";
    String quantity = "";
    String serial = "";   
	String check = "";
	String sql = "";
	String errors = "";
	
	
	
%>

		<TR>
			<td colspan="4">
				<TABLE cellpadding="0" cellspacing="0" border="1" rules=all
					width="90%">
					<tr valign="middle" align="center" class="">
						<td class="reportHeaderCell">&nbsp;#</td>
						<td class="reportHeaderCell">Secc. Almacen</td>
						<td class="reportHeaderCell">Clave Producto</td>
						<td class="reportHeaderCell">Nombre Producto</td>
						<td class="reportHeaderCell">Cantidad</td>
						<td class="reportHeaderCell">No.Serie</td>
						<td class="reportHeaderCell">Error</td>
					</TR>
					<%           
        		while (inputData.hasMoreTokens() && i < 2000) { 
        			errors = "";
        			s = inputData.nextToken();
        			StringTokenizer tabs = new StringTokenizer(s, "\t");        			
        			
        			//Recuperar valores        			        			
        			wareCode = (tabs.nextToken()).trim();        			    				
        			prodCode = (tabs.nextToken()).trim();
        			prodName = (tabs.nextToken()).trim();
        			quantity = (tabs.nextToken()).trim();    				
        			serial = (tabs.nextToken()).trim();
        			
        			int wareCodeS = 0;
        			//Revisar la seccion del Almacen
        			sql = "SELECT count(whse_name) as wareName FROM whsections WHERE whse_name like '" + wareCode + "'";
        			pmConn.doFetch(sql);
        			if (pmConn.next()) wareCodeS = pmConn.getInt("wareName");
        			
        			if (!(wareCodeS > 0)) errors += "No Existe la Seccion del Almacen";
        			
        			int prodCodeS = 0;
        			//Revisar la seccion del Almacen
        			sql = "SELECT count(prod_code) as prodcode FROM products WHERE prod_code like '" + prodCode + "'";
        			pmConn.doFetch(sql);
        			if (pmConn.next()) prodCodeS = pmConn.getInt("prodcode");
        			
        			if (!(prodCodeS > 0)) errors += "No Existe el producto";
        			
        			
        			sql = "SELECT whtr_serial FROM whtracks " +
        			      "WHERE whtr_serial = '" + serial + "'";
        			pmConn.doFetch(sql);
        			if (pmConn.next()) errors += " ya existe el serial " + serial;
        			
        			
        			
    				%>
					<TR class="reportCellEven" width="100%">
						<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>
						<%=HtmlUtil.formatReportCell(sFParams, wareCode, BmFieldType.STRING)%>
						<%=HtmlUtil.formatReportCell(sFParams, prodCode, BmFieldType.STRING)%>
						<%=HtmlUtil.formatReportCell(sFParams, prodName, BmFieldType.STRING)%>
						<%=HtmlUtil.formatReportCell(sFParams, quantity, BmFieldType.STRING)%>
						<%=HtmlUtil.formatReportCell(sFParams, serial, BmFieldType.STRING)%>
						<td><font color="red"><%= errors %></font></td>
					</TR>
					<%
    			i++;	
        		}      		
            %>
				</TABLE>

				<TABLE cellpadding="0" cellspacing="0" border="0" width="100%">
					<FORM action="flex_revisionWhProduct.jsp" method="POST"
						name="listFilter">
						<input type="hidden" name="action" value="populate"> <input
							type="hidden" name="populateData" value="<%= populateData %>">
						<tr class="">
							<td align="center" colspan="4" height="35" valign="middle">
								<input type="submit" value="Cargar Productos">
							</td>
						</tr>
					</FORM>
				</TABLE> <%
	} else if (action.equals("populate")) {
		//PmWhMovement pmWhMovement = new PmWhMovement(sFParams);
		//pmWhMovement.populateProducts(populateData);
	
	
	try {		

		pmConn2.disableAutoCommit();
		
		PmWhMovement pmWhMovement = new PmWhMovement(sFParams);
		PmWhMovItem pmWhMovItem = new PmWhMovItem(sFParams);
	
		//Almacen
		PmWhSection pmWhSection = new PmWhSection(sFParams);
		BmoWhSection bmoWhSection = new BmoWhSection();
	
		//Productos
		PmProduct pmProduct = new PmProduct(sFParams);
		BmoProduct bmoProduct = new BmoProduct();
	
		String s = "";
		String wareSectionName = "";	
		String prodCode = "";    
		String prodName = "";
		String quantity = "";
		String serial = "";  		
	
		int i = 1;		
		StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
	
		while (inputData.hasMoreTokens() && i < 2000) {
	
			s = inputData.nextToken();
			StringTokenizer tabs = new StringTokenizer(s, "\t");        			
	
			//Recuperar valores
			wareSectionName = (tabs.nextToken()).trim();
			prodCode = (tabs.nextToken()).trim();
			prodName = (tabs.nextToken()).trim();
			quantity = (tabs.nextToken()).trim();
			serial = (tabs.nextToken()).trim();   				    				
	
			//Obtener el Almacen
			bmoWhSection = (BmoWhSection)pmWhSection.getBy(pmConn2, wareSectionName, bmoWhSection.getName().getName());
			if (!(bmoWhSection.getId() > 0)) bmUpdateResult.addMsg("No Existe la Seccion del Almacen: " + wareSectionName);
	
			//Crear el Movimiento de Entrada por Ajuste
			BmoWhMovement bmoWhMovement = new BmoWhMovement();
			bmoWhMovement.getType().setValue(BmoWhMovement.TYPE_IN_ADJUST);
			bmoWhMovement.getDescription().setValue("Entrada por Ajuste Automatico");
			bmoWhMovement.getDatemov().setValue(SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()));
			bmoWhMovement.getStatus().setValue(BmoWhMovement.STATUS_REVISION);
	
			bmoWhMovement.getToWhSectionId().setValue(bmoWhSection.getId());
			bmoWhMovement.getCompanyId().setValue(bmoWhSection.getBmoWarehouse().getCompanyId().toInteger());
			pmWhMovement.save(pmConn2, bmoWhMovement, bmUpdateResult);	
	
			//Crear el Item del Mov Por Ajuste
			BmoWhMovItem bmoWhMovItemNew = new BmoWhMovItem();
	
			//Obtener el Producto mediante la clave
			bmoProduct = (BmoProduct) pmProduct.getBy(pmConn2, prodCode, bmoProduct.getCode().getName());
			if (!(bmoProduct.getId() > 0)) {
				bmUpdateResult.addMsg("No Existe el Producto: " + prodCode);
			} else {
				bmoProduct.getEnabled().setValue(1);
				pmProduct.saveSimple(pmConn2, bmoProduct, bmUpdateResult);
	
				if (bmoProduct.getTrack().equals(BmoProduct.TRACK_SERIAL)) {
					quantity = "1";
				}
			}
	
			bmoWhMovItemNew.getWhMovementId().setValue(bmoWhMovement.getId());
			bmoWhMovItemNew.getToWhSectionId().setValue(bmoWhSection.getId());
			bmoWhMovItemNew.getProductId().setValue(bmoProduct.getId());
			bmoWhMovItemNew.getQuantity().setValue(quantity);
			bmoWhMovItemNew.getSerial().setValue(serial);
			//System.out.println("cc: " +bmoWhMovement.getId());
			pmWhMovItem.save(pmConn2, bmoWhMovItemNew, bmUpdateResult);
	
			//Actualizar el estatus whmovement
			bmoWhMovement.getStatus().setValue(BmoWhMovement.STATUS_AUTHORIZED);
	
			pmWhMovement.save(pmConn2, bmoWhMovement, bmUpdateResult);
	
			//System.out.println("Producto " + prodName);
	
			i++;
		}
	
		if (!bmUpdateResult.hasErrors())
			pmConn2.commit();

	} catch (Exception e) {
	pmConn2.rollback();
	throw new SFException(e.toString());

} finally {
	pmConn.close();
	pmConn2.close();
}

	response.sendRedirect("/jsp/flex_revisionWhProduct.jsp?action=complete&errorsave="
			+ bmUpdateResult.errorsToString());

		} else if (action.equals("complete")) {
%>
				<table border="0" cellspacing="0" width="100%" cellpadding="0"
					style="font-size: 12px">
					<tr>
						<td colspan="" class="reportTitle">&nbsp;La Carga esta
							completa <%= errorSave %>
						</td>
					</tr>
				</table> 
				<% } %> 
				<% 	
} catch (Exception e) { 
	errorLabel = "Error ";
	errorText = "Error";
	errorException = e.toString();
	
	response.sendRedirect(sFParams.getAppURL() + "/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);

%> <%
} finally {
	pmConn.close();
	pmConn2.close();
}
%>
			
</body>
</html>


