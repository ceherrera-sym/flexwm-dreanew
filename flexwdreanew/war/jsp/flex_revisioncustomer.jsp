
<%@page import="com.symgae.shared.GwtUtil"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.flexwm.shared.cm.BmoCustomerAddress"%>
<%@page import="com.flexwm.server.cm.PmCustomerAddress"%>
<%@page import="com.flexwm.server.cm.PmCustomer"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@include file="../inc/imports.jsp"%>


<%@include file="../inc/login.jsp"%>

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

BmoCustomer bmoCustomer = new BmoCustomer();			

try { 
if (action.equalsIgnoreCase("revision")) { %>
	<table width="80%" border="0" align="center"  class="">

<%
	String msg = "";
	
	ArrayList list = new ArrayList();
	String s = "";
	int i = 1;		
	StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
	
	String code = "";
	String type = "";
	String comercialName = "";
	String legalName = "";
	String RFC = "";
	String title = "";
	String name = "";
	String fatherName = "";
	String motherName = "";
	String phone = "";
	String extension = "";
	String mail = "";
	String mobile = "";
	String position = "";
	String salesMan = "";
	String currency = "";
	String market = "";
	String reference = "";
	String referenceNotes = "";
	String curp = "";
	String nss = "";
	String birthdate = "";
	String maritalStatus = "";
	String income = "";
	String web = "";
	String errors = "";
%>
    
    <TR>
        <td colspan="4">  
            <TABLE cellpadding="0" cellspacing="0" border="1" rules=all width="90%">               
            	<tr valign="middle" align="center" class="">
                    <td class="reportHeaderCell">&nbsp;#</td>
                    <td class="reportHeaderCell">Codigo</td>
                    <td class="reportHeaderCell">Tipo de Cliente</td>                    	
                    <td class="reportHeaderCell">Nombre Comercial </td>                    
                    <td class="reportHeaderCell">Razón Social </td>
                    <td class="reportHeaderCell">RFC</td>
                    <td class="reportHeaderCell">Titulo</td>
                    <td class="reportHeaderCell">Nombre</td>
                    <td class="reportHeaderCell">Paterno</td>
                    <td class="reportHeaderCell">Materno</td>
                    <td class="reportHeaderCell">Numero de Telefono</td>
                    <td class="reportHeaderCell">Extensión</td>
                    <td class="reportHeaderCell">Email</td>
                    <td class="reportHeaderCell">Móvil</td>
                    <td class="reportHeaderCell">Cargo</td>
                    <td class="reportHeaderCell">Vendedor</td>
                    <td class="reportHeaderCell">Moneda</td>
                    <td class="reportHeaderCell">Mercado</td>                    
                    <td class="reportHeaderCell">Referencia</td>
                    <td class="reportHeaderCell">Notas de Referencia</td>
                    <td class="reportHeaderCell">CURP</td>
                    <td class="reportHeaderCell">IMSS</td>
                     <td class="reportHeaderCell">Fecha de nacimineto</td>
                    <td class="reportHeaderCell">Estado Civil </td>
                    <td class="reportHeaderCell">Ingresos</td>
                    <td class="reportHeaderCell">Sitio Web</td>
                    <td class="reportHeaderCell">Errors</td>
                </TR>
            <%     
        	boolean hasErrors = false;
        		while (inputData.hasMoreTokens() && i < 20000) {        			
        			errors = "";
        			s = inputData.nextToken();
        			StringTokenizer tabs = new StringTokenizer(s, "\t");        			
        			
        			//Recuperar valores        		
        			code = (tabs.nextToken()).trim();        			    				
        			type = (tabs.nextToken()).trim();  
        			comercialName = (tabs.nextToken()).trim();  
        			legalName = (tabs.nextToken()).trim();  
        			RFC = (tabs.nextToken()).trim();  
        			title = (tabs.nextToken()).trim();  
        			name = (tabs.nextToken()).trim();  
        			fatherName = (tabs.nextToken()).trim();  
        			motherName = (tabs.nextToken()).trim();  
        			phone = (tabs.nextToken()).trim();  
        			extension = (tabs.nextToken()).trim();  
        			mail = (tabs.nextToken()).trim();  
        			mobile = (tabs.nextToken()).trim();  
        			position = (tabs.nextToken()).trim();  
        			salesMan = (tabs.nextToken()).trim();  
        			currency = (tabs.nextToken()).trim();  
        			market = (tabs.nextToken()).trim();  
        			reference = (tabs.nextToken()).trim();  
        			referenceNotes = (tabs.nextToken()).trim();  
        			curp = (tabs.nextToken()).trim();          			
        			nss = (tabs.nextToken()).trim();  
        			birthdate = (tabs.nextToken()).trim(); 
        			maritalStatus = (tabs.nextToken()).trim();  
        			income = (tabs.nextToken()).trim();  
        			web = (tabs.nextToken()).trim();  
        			
	        		//Validar que el vendedor existe        			
	        		String sql = " select user_userid from users " + 
	        					 " where user_code like '" + salesMan + "'";
	        		System.out.println("sql:"+sql);
	    			pmConn2.doFetch(sql);
	    			
	    			if (!pmConn2.next()) {
	    				hasErrors = true;
	    				errors = "El vendedor no existe en el sistema " + salesMan;
	    			} 
	    			
	    			if (name.equalsIgnoreCase("")) {
	    				hasErrors = true;
	    				errors = "El Nombre es requerido";
	    			}
	    			
	    			if (fatherName.equalsIgnoreCase("")) {
	    				hasErrors = true;
	    				errors = "El Apellido paterno es requerido";
	    			}
	    			
	    			//Validar que la referencia exista        			
		    		if (!reference.equalsIgnoreCase("empty")) {
			        		sql = " select refe_name from referrals " + 
		        					" where refe_name like '" + reference + "'";	        		
		    			pmConn2.doFetch(sql);
		    			if (!pmConn2.next()) {
		    				hasErrors = true;
		    				errors = "La Referencia no existe en el sistema";
		    			}
	    			}
			   		if (!title.equals("empty")){
			   			sql = "select titl_titleid from titles where titl_code = '" + title + "'";
			   			pmConn2.doFetch(sql);
		    			if (!pmConn2.next()) {
		    				hasErrors = true;
		    				errors = "El titulo no existe en el sistema";
		    			}
			   		}
		   			if (curp.equalsIgnoreCase("empty")) {
		   				curp = "";
		   			}
		   		
		   			if (nss.equalsIgnoreCase("0")) {
		   				nss = "";
		   			}
		   			if (!currency.equals("empty")){
						sql = "select cure_currencyid from currencies where cure_code = '" + currency + "'";
						pmConn2.doFetch(sql);
						if (!pmConn2.next()) {
							hasErrors = true;
		    				errors = "La moneda no existe en el sistema";
						} 
						
					}
		   			// Validar que el estado civil exista      			
		    		if (!maritalStatus.equalsIgnoreCase("empty")) {
			        		sql = " SELECT mast_name FROM maritalstatus " + 
		        					" WHERE mast_name LIKE '" + maritalStatus + "'";	        		
		    			pmConn2.doFetch(sql);
		    			if (!pmConn2.next()) {
		    				hasErrors = true;
		    				errors = "El Estado civil no se reconoce en el sistema.";
		    			}
	    			}
		    		if (!market.equalsIgnoreCase("empty")){
						sql = "select mrkt_marketid from markets where mrkt_name = '" + market + "'";
						if (!pmConn2.next()) {
							hasErrors = true;
		    				errors = "El Mercado no se reconoce en el sistema. " + market;
						} 
					}
		   			
		   			if (type.equalsIgnoreCase("") || type.equalsIgnoreCase("empty") ) {
		   				hasErrors = true;
		   				errors = "El Tipo de cliente es requerido";
		   			}
		   			if (type.equalsIgnoreCase(""+BmoCustomer.TYPE_COMPANY) 
		    				|| type.equalsIgnoreCase(""+BmoCustomer.TYPE_PERSON) ){
		    		} else {
		    			hasErrors = true;
	    				errors = "El Tipo de cliente  no se reconoce en el sistema";
		    		}
		   			
		   			
        			
    		%>
        			
    			
    				<TR class="reportCellEven" width="100%">
    					<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>
    					<%=HtmlUtil.formatReportCell(sFParams, code, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, type, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, comercialName, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, legalName, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, RFC, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, title, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, name, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, fatherName, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, motherName, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, phone, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, extension, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, mail, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, mobile, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, position, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, salesMan, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, currency, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, market, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, reference, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, referenceNotes, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, curp, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, nss, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, birthdate, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, maritalStatus, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, income, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, web, BmFieldType.STRING)%>
    				    <td><font color="red"><%= errors %></font></td>
    				</TR>
    		<%
    			i++;        		
        	}
            %>
</TABLE>

	<TABLE cellpadding="0" cellspacing="0" border="0"  width="100%">
		<FORM action="flex_revisioncustomer.jsp" method="POST" name="listFilter">	
		<input type="hidden" name="action" value="populate">
		<input type="hidden" name="populateData" value="<%= populateData %>">			
		<tr class="">
		    <td align="center" colspan="4" height="35" valign="middle">
		    <% if (!hasErrors) { %>
		        <input type="submit" value="Cargar Clientes">
		    <% } %>    
		    </td>
		</tr>			    
		</FORM>								
	</TABLE>
<% } else if (action.equalsIgnoreCase("populate")) {
	BmUpdateResult bmUpdateResult = new BmUpdateResult();
	
	try {		
		pmConn.disableAutoCommit();
	
		String s = "";
		String code = "";
		String type = "";
		String comercialName = "";
		String legalName = "";
		String RFC = "";
		String title = "";
		String name = "";
		String fatherName = "";
		String motherName = "";
		String phone = "";
		String extension = "";
		String mail = "";
		String mobile = "";
		String position = "";
		String salesMan = "";
		String currency = "";
		String market = "";
		String reference = "";
		String referenceNotes = "";
		String curp = "";
		String nss = "";
		String birthdate = "";
		String maritalStatus = "";
		String income = "";
		String web = "";
		String errors = "";
		
		PmCustomer pmCustomer = new PmCustomer(sFParams);
				
		StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
		int i = 1;
		
		while (inputData.hasMoreTokens() && i < 20000) {
			String sql = "";
			bmoCustomer = new BmoCustomer();

			s = inputData.nextToken();
			StringTokenizer tabs = new StringTokenizer(s, "\t");
			
			code = (tabs.nextToken()).trim();        			    				
			type = (tabs.nextToken()).trim();  
			comercialName = (tabs.nextToken()).trim();  
			legalName = (tabs.nextToken()).trim();  
			RFC = (tabs.nextToken()).trim();  
			title = (tabs.nextToken()).trim();  
			name = (tabs.nextToken()).trim();  
			fatherName = (tabs.nextToken()).trim();  
			motherName = (tabs.nextToken()).trim();  
			phone = (tabs.nextToken()).trim();  
			extension = (tabs.nextToken()).trim();  
			mail = (tabs.nextToken()).trim();  
			mobile = (tabs.nextToken()).trim();  
			position = (tabs.nextToken()).trim();  
			salesMan = (tabs.nextToken()).trim();  
			currency = (tabs.nextToken()).trim();  
			market = (tabs.nextToken()).trim();  
			reference = (tabs.nextToken()).trim();  
			referenceNotes = (tabs.nextToken()).trim();  
			curp = (tabs.nextToken()).trim();          			
			nss = (tabs.nextToken()).trim();  
			birthdate = (tabs.nextToken()).trim(); 
			maritalStatus = (tabs.nextToken()).trim();  
			income = (tabs.nextToken()).trim();  
			web = (tabs.nextToken()).trim();  
 
			
			int custId = 0;
			
			if(mail.equalsIgnoreCase("empty")){
				mail = "";
			}
			//Validar si existe el cliente
			sql = " select cust_customerid from customers " + 
			      " where cust_firstname = '" + name + "'" +
				  " and cust_fatherlastname like '" + fatherName + "'" +
			      " and cust_motherlastname like '" + motherName + "'";
			pmConn2.doFetch(sql);
			if (pmConn2.next()) {
				custId = pmConn2.getInt("cust_customerid");
			}
			
			if (!(custId > 0)) {
				
					bmoCustomer.getCode().setValue(code);
					bmoCustomer.getCustomertype().setValue(type);
					bmoCustomer.getDisplayName().setValue(comercialName);
					if (!legalName.equals("empty"))
						bmoCustomer.getLegalname().setValue(legalName);
					if (!RFC.equals("empty"))
						bmoCustomer.getRfc().setValue(RFC);
					int titleId = 0;
					if (!title.equals("empty")){
			   			sql = "select titl_titleid from titles where titl_code = '" + title + "'";
			   			pmConn2.doFetch(sql);
		    			if (pmConn2.next()) {
		    				titleId = pmConn2.getInt("titl_titleid");
		    			}
		    			bmoCustomer.getTitleId().setValue(titleId);
			   		}					
					bmoCustomer.getFirstname().setValue(name);
					bmoCustomer.getFatherlastname().setValue(fatherName);
					
					if (!motherName.equals("empty")){
						bmoCustomer.getMotherlastname().setValue(motherName);
					}
					
					if (!phone.equals("empty")){
						bmoCustomer.getPhone().setValue(phone);
					}
					if (!extension.equals("empty")){
						bmoCustomer.getExtension().setValue(extension);
					}
					if (!mail.equals("empty")){
						bmoCustomer.getEmail().setValue(mail);
					}
					if (!mobile.equals("empty")){
						bmoCustomer.getMobile().setValue(mobile);
					}
					if (!position.equals("empty")){
						bmoCustomer.getPosition().setValue(position);
					}
					//Obtener el Id del vendedor en flex
					int userId = 0;
					sql = " SELECT user_userid FROM users WHERE user_code = '" + salesMan + "'";
					pmConn2.doFetch(sql);
					if (pmConn2.next()) {
						userId = pmConn2.getInt("user_userid");
					} 
					bmoCustomer.getSalesmanId().setValue(userId);
					
					int currencyId = 0;
					if (!currency.equals("empty")){
						sql = "select cure_currencyid from currencies where cure_code = '" + currency + "'";
						pmConn2.doFetch(sql);
						if (pmConn2.next()) {
							currencyId = pmConn2.getInt("cure_currencyid");
						} 		
						bmoCustomer.getCurrencyId().setValue(currencyId);
					}
					int marketId = 0;
					if (!market.equals("empty")){
						sql = "select mrkt_marketid from markets where mrkt_name = '" + market + "'";
						if (pmConn2.next()) {
							marketId = pmConn2.getInt("mrkt_marketid");
						} 
						bmoCustomer.getMarketId().setValue(marketId);
					}
					
					
					int referralId = 0;
					if (!reference.equalsIgnoreCase("empty")) {
		        		sql = " select refe_referralid from referrals " + 
		        					" where refe_name like '" + reference + "'";	        		
		    			pmConn2.doFetch(sql);
		    			if (pmConn2.next()) {
		    				referralId = pmConn2.getInt("refe_referralid");
		    			}
						bmoCustomer.getReferralId() .setValue(referralId);			
    				}
					if (!referenceNotes.equals("empty")){
						bmoCustomer.getReferralComments().setValue(referenceNotes);
					}
					if (!curp.equalsIgnoreCase("empty")) {
						bmoCustomer.getCurp().setValue(curp);
					}
					if (!nss.equalsIgnoreCase("empty")) {
						bmoCustomer.getNss().setValue(nss);
					}
					if (!birthdate.equals("empty")){
						bmoCustomer.getBirthdate().setValue(birthdate);
					}
					
					// Validar que el estado civil exista
					int maritalStatusId = 0;
		    		if (!maritalStatus.equalsIgnoreCase("empty")) {
			        		sql = " SELECT mast_maritalstatusid FROM maritalstatus " + 
		        					" WHERE mast_name LIKE '" + maritalStatus + "'";	        		
		    			pmConn2.doFetch(sql);
		    			if (pmConn2.next()) {
		    				maritalStatusId = pmConn2.getInt("mast_maritalstatusid");
		    			}
	    			}
		    		if (maritalStatusId > 0)
						bmoCustomer.getMaritalStatusId().setValue(maritalStatusId);
		    		if (!income.equals("empty")){
		    			bmoCustomer.getIncome().setValue(income);
		    		}
		    		if (!web.equals("empty")){
		    			bmoCustomer.getWww().setValue(web);
		    		}
					
					if (type.equalsIgnoreCase(""+BmoCustomer.TYPE_COMPANY)) {
						bmoCustomer.getDisplayName().setValue(bmoCustomer.getFirstname().toString() 
								+ " " + bmoCustomer.getFatherlastname().toString()
								+ " " + bmoCustomer.getMotherlastname().toString()); 
					}
					pmCustomer.save(pmConn, bmoCustomer, bmUpdateResult);
// 					bmoCustomer.setId(bmUpdateResult.getId());

// 					int customerId = bmoCustomer.getId(); 
					//Agregar un domicilio
// 					if (!typeAddress.equalsIgnoreCase("empty")) {
// 						PmCustomerAddress pmCustomerAddress = new PmCustomerAddress(sFParams);
// 						BmoCustomerAddress bmoCustomerAddress = new BmoCustomerAddress();
						
// 						bmoCustomerAddress.getCustomerId().setValue(customerId);
// 						bmoCustomerAddress.getStreet().setValue(street);
// 						bmoCustomerAddress.getNumber().setValue(streetNumber);
// 						bmoCustomerAddress.getNeighborhood().setValue(neighborhood);
// 						bmoCustomerAddress.getZip().setValue(zip);
// 						bmoCustomerAddress.getType().setValue(typeAddress);
						
									
// 						int cityId = 0;
// 						sql = " select * from cities " +			      
// 							  " where city_name like '" + cityName + "'";			
// 						pmConn2.doFetch(sql);			
// 						if (pmConn2.next()) {
// 							cityId = pmConn2.getInt("city_cityid");							
// 							bmoCustomerAddress.getCityId().setValue(cityId);
// 							bmUpdateResult = pmCustomerAddress.save(pmConn, bmoCustomerAddress, bmUpdateResult);
// 						} 
// 					}
					
// 					PmCustomerCompany pmCustomerCompany = new PmCustomerCompany(sFParams);
// 					BmoCustomerCompany bmoCustomerCompany = new BmoCustomerCompany();
// 					bmoCustomerCompany.getCustomerId().setValue(customerId);
// 					bmoCustomerCompany.getCompanyId().setValue(2);
// 					pmCustomerCompany.save(pmConn, bmoCustomerCompany, bmUpdateResult);

// 					pmCustomer.save(pmConn, bmoCustomer, bmUpdateResult);
			} 
			i++;
		}
		
		if (!bmUpdateResult.hasErrors())
			pmConn.commit();
		else {
		}
	
	} catch (Exception e) {
		pmConn.rollback();
		throw new SFException(e.toString());
		
	} finally {
		pmConn.close();
	}
		
	response.sendRedirect(GwtUtil.getProperUrl(sFParams, "/jsp/flex_revisioncustomer.jsp") + "?action=complete&errorsave=" + bmUpdateResult.errorsToString());
		
}  else if (action.equalsIgnoreCase("complete")) { %>
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr>		
		<td colspan="4" align="center" class="reportTitle">
			<br>
		    &nbsp;La Carga esta completa
		    <%= errorSave %>
		    <br>
			   
			<br><br><a href="<%= GwtUtil.getProperUrl(sFParams, "/jsp/flex_populatecustomer.jsp") %>">Importar m&aacute;s clientes.</a>
		</td>
	</tr>
	</table>

<% } %>


<% 	} catch (Exception e) { 
	errorLabel = "Error ";
	errorText = "Error";
	errorException = e.toString();
	
	response.sendRedirect(GwtUtil.getProperUrl(sFParams, "/jsp/error.jsp")+ "?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);
%>

<%
} finally {
	pmConn.close();
	pmConn2.close();
}
%>
</body>
</html>