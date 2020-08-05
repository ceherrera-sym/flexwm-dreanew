
<%@include file="../inc/imports.jsp" %>
<%@page import="com.flexwm.server.co.PmPropertySale"%>
<%@page import="com.flexwm.shared.co.BmoPropertySale"%>
<%@page import="com.flexwm.server.fi.PmRaccount"%>
<%@page import="com.flexwm.shared.fi.BmoRaccount"%>
<%@page import="com.flexwm.server.fi.PmRaccountItem"%>
<%@page import="com.flexwm.shared.fi.BmoRaccountItem"%>
<%@page import="com.flexwm.server.fi.PmRaccountAssignment"%>
<%@page import="com.flexwm.shared.fi.BmoRaccountAssignment"%>
<%@page import="com.flexwm.server.op.PmOrder"%>
<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@page import="java.util.StringTokenizer"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/login.jsp" %>

<%
	String errorLabel = request.getParameter("errorLabel");
	String errorText = request.getParameter("errorText");
	String errorSave = request.getParameter("errorSave");
	String errorException = request.getParameter("errorException");
	String programTitle = "Importacion de CxC en Ventas";
	String programDescription = "Importacion de CxC en Ventas";
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
	
	String allocationId = "";
	String raccountId = "";
	String parentId = "";
	String type = "";
	String invoiceno = "";
	String description = "";
	String receiveDate = "";
	String dueDate = "";	
	String total = "";	
	String errors = "";
	
	
	
%>
    
    <TR>
        <td colspan="4">  
            <TABLE cellpadding="0" cellspacing="0" border="1" rules=all width="90%">               
            	<tr valign="middle" align="center" class="">
                    <td class="reportHeaderCell">&nbsp;#</td>
                    <td class="reportHeaderCell">Venta</td>
                    <td class="reportHeaderCell">CxC</td>
                    <td class="reportHeaderCell">Parent</td>
                    <td class="reportHeaderCell">Tipo</td>                    
                    <td class="reportHeaderCell">Factura</td>
                    <td class="reportHeaderCell">Descripcion</td>
                    <td class="reportHeaderCell">Ingreso</td>
                    <td class="reportHeaderCell">Pago</td>                                        
                    <td class="reportHeaderCell">Total</td>
                    <td class="reportHeaderCell">Errors</td>
                </TR>
            <%           
        		while (inputData.hasMoreTokens() && i < 2000) {
        			
        			errors = "";
        			s = inputData.nextToken();
        			StringTokenizer tabs = new StringTokenizer(s, "\t");        			
        			
        			//Recuperar valores        		
        			allocationId = tabs.nextToken();        			
        			raccountId = tabs.nextToken();
        			parentId = tabs.nextToken();
        			type = tabs.nextToken();
        			invoiceno = tabs.nextToken();
        			description = tabs.nextToken();
        			receiveDate = tabs.nextToken();
        			dueDate = tabs.nextToken();
        			total = tabs.nextToken();
        			
    		%>
    				<TR class="reportCellEven" width="100%">
    					<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>
    					<%=HtmlUtil.formatReportCell(sFParams, allocationId, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, raccountId, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, parentId, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, type, BmFieldType.STRING)%>    					
    					<%=HtmlUtil.formatReportCell(sFParams, invoiceno, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, description, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, receiveDate, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, dueDate, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, total, BmFieldType.STRING)%>
    				    <td><font color="red"><%= errors %></font></td>
    				</TR>
    		<%
    			i++;
        		}
            %>
</TABLE>

	<TABLE cellpadding="0" cellspacing="0" border="0"  width="100%">
		<FORM action="flex_revisionraccountsale.jsp" method="POST" name="listFilter">	
		<input type="hidden" name="action" value="populate">
		<input type="hidden" name="populateData" value="<%= populateData %>">			
		<tr class="">
		    <td align="center" colspan="4" height="35" valign="middle">
		    <% if (errors.equals("")) { %>
		        <input type="submit" value="Cargar CxC">
		    <% } %>    
		    </td>
		</tr>			    
		</FORM>								
	</TABLE>
<% } else if (action.equals("populate")) { %>

<TABLE cellpadding="0" cellspacing="0" border="0"  width="100%">
<%	
	BmUpdateResult bmUpdateResult = new BmUpdateResult();
	
	try {		
		
		pmConn.disableAutoCommit();
	
		ArrayList list = new ArrayList();
		String s = "";
		String sql = "";
		
		String allocationId = "";
		String raccountId = "";
		String parentId = "";
		String type = "";
		String invoiceno = "";
		String description = "";
		String receiveDate = "";
		String dueDate = "";
		String total = "";
		String errors = "";
	    
		PmRaccount pmRaccNew = new PmRaccount(sFParams);
		BmoRaccount bmoRaccNew = null;
		
				
		StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
		int i = 1;
		while (inputData.hasMoreTokens() && i < 20000) { 
			sql = "";
			s = inputData.nextToken();
			StringTokenizer tabs = new StringTokenizer(s, "\t");
			
			//Recuperar valores
			allocationId = tabs.nextToken();        			
			raccountId = tabs.nextToken();
			parentId = tabs.nextToken();
			type = tabs.nextToken();
			invoiceno = tabs.nextToken();
			description = tabs.nextToken();
			receiveDate = tabs.nextToken();
			dueDate = tabs.nextToken();
			total = tabs.nextToken();
			
			
			//Obtener el pedido
			int orderId = 0;
			sql = "SELECT prsa_orderid  FROM propertysales " +
			      "WHERE prsa_description = '" + allocationId + "'";
			pmConn.doFetch(sql);
			if (pmConn.next()) orderId = pmConn.getInt("prsa_orderid");
			
			//Obtener la empresa del pedido
			int companyId = 0, customerId = 0;
			sql = "SELECT orde_companyid, orde_customerid  FROM orders " +
				  "WHERE orde_orderid = '" + orderId + "'";
			pmConn.doFetch(sql);
			if (pmConn.next()) {
				companyId = pmConn.getInt("orde_companyid");
				customerId = pmConn.getInt("orde_customerid");				
			}
			
			bmoRaccNew = new BmoRaccount();
			
			String descItem = "";
			if (type.equals("W") && parentId.equals("0")) {
				bmoRaccNew.getRaccountTypeId().setValue(1);
				bmoRaccNew.getOrderId().setValue(orderId);
				descItem = "Migracion Covent";
			} else if (type.equals("W") && !parentId.equals("0")) {
				bmoRaccNew.getRaccountTypeId().setValue(3);
				descItem = "Migracion Covent";
			} else {
				bmoRaccNew.getRaccountTypeId().setValue(5);
				descItem = "Migracion Covent";
			}	
			
			
			bmoRaccNew.getCustomerId().setValue(customerId);
			bmoRaccNew.getInvoiceno().setValue(invoiceno);			
			bmoRaccNew.getStatus().setValue("" + BmoRaccount.STATUS_AUTHORIZED);
			bmoRaccNew.getPaymentStatus().setValue("" + BmoRaccount.PAYMENTSTATUS_PENDING);			
			bmoRaccNew.getReceiveDate().setValue(receiveDate);
			bmoRaccNew.getDueDate().setValue(dueDate);
			bmoRaccNew.getDescription().setValue(description);
			bmoRaccNew.getFolio().setValue(raccountId);
			bmoRaccNew.getCompanyId().setValue(companyId);
			bmoRaccNew.getOrderId().setValue(orderId);
			bmoRaccNew.getAutoCreate().setValue("0");
			bmoRaccNew.getAmount().setValue(total);
			bmoRaccNew.getBalance().setValue(total);
			
			pmRaccNew.save(pmConn, bmoRaccNew, bmUpdateResult);
			
			//Obtener el Id de la venta
			bmoRaccNew.setId(bmUpdateResult.getId());
			
			//Crear los items 
			PmRaccountItem pmRaccItemNew = new PmRaccountItem(sFParams);
			BmoRaccountItem bmoRaccItemNew = new BmoRaccountItem();
			bmoRaccItemNew.getRaccountId().setValue(bmoRaccNew.getId());
			bmoRaccItemNew.getName().setValue(descItem);
			bmoRaccItemNew.getQuantity().setValue(1);
			bmoRaccItemNew.getAmount().setValue(total);
			bmoRaccItemNew.getPrice().setValue(total);
			
			pmRaccItemNew.save(pmConn, bmoRaccItemNew, bmUpdateResult);					
			
			//Si es de tipo abono crear la apliacion
			if (type.equals("D") && !parentId.equals("0")) {
				
				//Obtener el cargo
				int newParentId = 0;
				sql = " SELECT racc_raccountid FROM raccounts " +
				      " WHERE racc_folio = '" + parentId + "'";
				pmConn.doFetch(sql);
				if (pmConn.next()) {
					newParentId = pmConn.getInt("racc_raccountid");
					PmRaccount pmRaccount = new PmRaccount(sFParams);
					BmoRaccount bmoRaccount = (BmoRaccount)pmRaccount.get(pmConn, newParentId);
					
					//Crear la asignacion de la CXP
					PmRaccountAssignment pmRaccountAssigmentNew = new PmRaccountAssignment(sFParams);
					BmoRaccountAssignment bmoRaccountAssigmentNew = new BmoRaccountAssignment();			
					bmoRaccountAssigmentNew.getRaccountId().setValue(bmoRaccNew.getId());
					bmoRaccountAssigmentNew.getForeignRaccountId().setValue(bmoRaccount.getId());		
					bmoRaccountAssigmentNew.getAmount().setValue(total);
					bmoRaccountAssigmentNew.getInvoiceno().setValue(bmoRaccount.getInvoiceno().toString());
					bmoRaccountAssigmentNew.getCode().setValue(bmoRaccNew.getCode().toString());
					
					pmRaccountAssigmentNew.save(pmConn, bmoRaccountAssigmentNew, bmUpdateResult);
										
					pmRaccount.updateBalance(pmConn, bmoRaccount, bmUpdateResult);
				}	
				
			} else if (type.equals("W") && !parentId.equals("0")) {
				//Obtener el cargo
				int newParentId = 0;
				sql = " SELECT racc_raccountid FROM raccounts " +
				      " WHERE racc_folio = '" + parentId + "'";
				pmConn.doFetch(sql);
				if (pmConn.next()) {
					newParentId = pmConn.getInt("racc_raccountid");
					PmRaccount pmRaccount = new PmRaccount(sFParams);
					BmoRaccount bmoRaccount = (BmoRaccount)pmRaccount.get(pmConn, newParentId);
					
					//Crear la asignacion de la CXP
					PmRaccountAssignment pmRaccountAssigmentNew = new PmRaccountAssignment(sFParams);
					BmoRaccountAssignment bmoRaccountAssigmentNew = new BmoRaccountAssignment();			
					bmoRaccountAssigmentNew.getRaccountId().setValue(bmoRaccount.getId());
					bmoRaccountAssigmentNew.getForeignRaccountId().setValue(bmoRaccNew.getId());		
					bmoRaccountAssigmentNew.getAmount().setValue(total);
					bmoRaccountAssigmentNew.getInvoiceno().setValue(bmoRaccNew.getInvoiceno().toString());
					bmoRaccountAssigmentNew.getCode().setValue(bmoRaccount.getCode().toString());
					
					pmRaccountAssigmentNew.saveSimple(pmConn, bmoRaccountAssigmentNew, bmUpdateResult);
					
					bmoRaccountAssigmentNew = (BmoRaccountAssignment)pmRaccountAssigmentNew.getBy(pmConn, bmoRaccount.getId(), bmoRaccountAssigmentNew.getRaccountId().getName());
					
					bmoRaccountAssigmentNew.getForeignRaccountId().setValue(bmoRaccNew.getId());
					pmRaccountAssigmentNew.saveSimple(pmConn, bmoRaccountAssigmentNew, bmUpdateResult);
					
					pmRaccount.updateBalance(pmConn, bmoRaccNew, bmUpdateResult);
					
					pmRaccount.updateBalance(pmConn, bmoRaccount, bmUpdateResult);
					
					
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
		
	response.sendRedirect("/batch/flex_revisionraccountsale.jsp?action=complete&errorsave=" + bmUpdateResult.errorsToString());
		
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
</table>
</body>
</html>


