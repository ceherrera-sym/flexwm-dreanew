
<%@include file="../inc/imports.jsp" %>
<%@page import="com.flexwm.server.cm.PmCustomer"%>
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
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

System.out.println("entra");
PmConn pmConn = new PmConn(sFParams);
pmConn.open();

PmConn pmConn2 = new PmConn(sFParams);
pmConn2.open();

try { 

if (action.equals("revision")) { %>

	 

	<table width="80%" border="0" align="center"  class="">

<%
	System.out.println("revision");
	String msg = "";
	String sql = "";
	
	ArrayList list = new ArrayList();
	String s = "";
	int i = 1;		
	StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
	
	//String codeCustomers = "";
	String displayName = "";
	String legalName = "";
	String rfc = "";
	String type = "";
    String dateCreate = "";    
    String titleCode = "";
    String firstName = "";
    String fatherLastName = "";
    String motherLastName = "";
    String birthDate = "";
    String phone = "";
    String mobile = "";
    String email = "";
    String position = "";
    String salesManCode= "";
    String www = "";
    String currencyCode = "";
    String referralCode = "";
    String territoryCode = "";
    String sicCode = "";    
    String parentCode = "";
	String errors = "";
	
	
	
%>
    
    <TR>
        <td colspan="4">  
            <TABLE cellpadding="0" cellspacing="0" border="1" rules=all width="90%">               
            	<tr valign="middle" align="center" class="">
                    <td class="reportHeaderCell">&nbsp;#</td>
                    <td class="reportHeaderCell">Nombre</td>
                    <td class="reportHeaderCell">Razon Social</td>
                    <td class="reportHeaderCell">Tipo</td>
                    <td class="reportHeaderCell">RFC</td>
                    <td class="reportHeaderCell">F.Creacion</td>     
                    <td class="reportHeaderCell">Titulo</td>
                    <td class="reportHeaderCell">Nombre</td>
                    <td class="reportHeaderCell">Paterno</td>
                    <td class="reportHeaderCell">Materno</td>
                    <td class="reportHeaderCell">Nacimiento</td>
                    <td class="reportHeaderCell">Telefono</td>
                    <td class="reportHeaderCell">Movil</td>
                    <td class="reportHeaderCell">Email</td>
                    <td class="reportHeaderCell">Cargo</td>
                    <td class="reportHeaderCell">Vendedor</td>
                    <td class="reportHeaderCell">www</td>
                    <td class="reportHeaderCell">Moneda</td>
                    <td class="reportHeaderCell">Refencia</td>
                    <td class="reportHeaderCell">SIC</td>                                        
                    <td class="reportHeaderCell">Territorio</td>
                    <td class="reportHeaderCell">Servicios Por</td>
                    <td class="reportHeaderCell">Errors</td>
                </TR>
            <%           
        		while (inputData.hasMoreTokens() && i < 2000) {
        			
        			errors = "";
        			s = inputData.nextToken();
        			StringTokenizer tabs = new StringTokenizer(s, "\t");        			
        			
        			//Recuperar valores        		
        			// codeCustomers = tabs.nextToken();        			 
        			 displayName = tabs.nextToken();
        			 legalName = tabs.nextToken();
        			 type = tabs.nextToken();
        			 rfc = tabs.nextToken();        		
        			 
        		     dateCreate = tabs.nextToken();        		     
        		     titleCode = tabs.nextToken();
        		     firstName = tabs.nextToken();
        		     fatherLastName = tabs.nextToken();
        		     motherLastName = tabs.nextToken();
        		    
        		     birthDate = tabs.nextToken();
        		     phone = tabs.nextToken();
        		     mobile = tabs.nextToken();
        		     email = tabs.nextToken();
        		     position = tabs.nextToken();
        		     
        		     salesManCode = tabs.nextToken();
        		     www = tabs.nextToken();
        		     currencyCode = tabs.nextToken();
        		     referralCode = tabs.nextToken();        		     
        		     sicCode = tabs.nextToken();
        		     
        		     territoryCode = tabs.nextToken();
        		     parentCode = tabs.nextToken();
        		     System.out.println(" " + territoryCode + " " + parentCode);
        		     
        			//Validar los datos necesarios para la direccion del cliente
        			if (firstName.equals("")) errors = "Falta el Nombre";
        			
        			if (fatherLastName.equals("")) errors = "Falta el Paterno";
        			if (!titleCode.equals("empty")) {
        				sql = " SELECT * FROM titles " + 
        			          " WHERE titl_code = '" + titleCode + "'";
        				pmConn2.doFetch(sql);
        				if (!pmConn2.next()) {
        					errors = "No existe el titulo: " + titleCode;
        				}
        			}
        			
        			if (!salesManCode.equals("empty")) {
        				sql = " SELECT * FROM users " + 
        			          " WHERE user_code = '" + salesManCode + "'";
        				pmConn2.doFetch(sql);
        				if (!pmConn2.next()) {
        					errors = "No existe el Usuario: " + salesManCode;
        				}
        			} 
        			
        			if (!currencyCode.equals("empty")) {
        				sql = " SELECT * FROM currencies " + 
        			          " WHERE cure_code = '" + currencyCode + "'";
        				pmConn2.doFetch(sql);
        				if (!pmConn2.next()) {
        					errors = "No existe la moneda : " + currencyCode;
        				}
        			} 
        			if (!referralCode.equals("empty")) {
        				sql = " SELECT * FROM referrals " + 
        					  " WHERE (refe_code = trim(LEADING ' ' FROM '" + referralCode + "') OR refe_name = trim(LEADING ' ' FROM '" + referralCode + "'))";
        				System.out.println("refe_code " + sql);
        				pmConn2.doFetch(sql);
        				if (!pmConn2.next()) {
        					errors = "No existe la referencia: " + referralCode;
        				}
        			} 
        			if (!sicCode.equals("empty")) {
        				sql = " SELECT * FROM industries " + 
          			          " WHERE indu_code = '" + sicCode + "'";
          				pmConn2.doFetch(sql);
          				if (!pmConn2.next()) {
          					errors = "No existe el Sic: " + sicCode;
          				}
        			}	
          		
        			if (!territoryCode.equals("empty")) {
        				sql = " SELECT * FROM territories " + 
        			          " WHERE terr_name = '" + parentCode.toUpperCase() + "'";
        				pmConn2.doFetch(sql);
        				System.out.println("sql " + sql);
        				if (!pmConn2.next()) {
        					errors = "No existe el territorio: " + parentCode;
        				}
            		} 
        			
        			if (!parentCode.trim().equals("empty")) {
            			sql = " SELECT * FROM consultingservices " + 
          			          " WHERE cose_name = '" + parentCode.toUpperCase() + "'";
            			System.out.println("sql " + sql);
          				pmConn2.doFetch(sql);
          				if (!pmConn2.next()) {
          					errors = "No existe la empresa de consultoría: " + parentCode;
          				}
            		} 
        			
        			if (!type.equals("C") && type.equals("P") && type.equals("empty")) {
            			errors = "No es valido el tipo de cliente: " + type;
            		}	
 %>       		
    				<TR class="reportCellEven" width="100%">
    					<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>
    					<%//=HtmlUtil.formatReportCell(sFParams, codeCustomers, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, displayName, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, legalName, BmFieldType.STRING)%>    					
    					<%=HtmlUtil.formatReportCell(sFParams, type, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, rfc, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, dateCreate, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, titleCode, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, firstName, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, fatherLastName, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, motherLastName, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, birthDate, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, phone, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, mobile, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, email, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, position, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, salesManCode, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, www, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, currencyCode, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, referralCode, BmFieldType.STRING)%>    					
    					<%=HtmlUtil.formatReportCell(sFParams, sicCode, BmFieldType.STRING)%>    					    					
    					<%=HtmlUtil.formatReportCell(sFParams, territoryCode, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, parentCode, BmFieldType.STRING)%>
    					
    				    <td><font color="red"><%= errors %></font></td>
    				</TR>
    		<%
    			
    			i++;        		
        	}
            %>
</TABLE>

	<TABLE cellpadding="0" cellspacing="0" border="0"  width="100%">
		<FORM action="flex_revisioncustomers.jsp" method="POST" name="listFilter">	
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
	BmUpdateResult bmUpdateResult = new BmUpdateResult();
	
	try {		
		
		pmConn.disableAutoCommit();
	
		ArrayList list = new ArrayList();
		String s = "";
		
		//String codeCustomers = "";
		String displayName = "";
		String legalName = "";
		String rfc = "";
		String type = "";
	    String dateCreate = "";    
	    String titleCode = "";
	    String firstName = "";
	    String fatherLastName = "";
	    String motherLastName = "";
	    String birthDate = "";
	    String phone = "";
	    String mobile = "";
	    String email = "";
	    String position = "";
	    String salesManCode= "";
	    String www = "";
	    String currencyCode = "";
	    String referralCode = "";
	    String sicCode = "";
	    String territoryCode = "";
	    String parentCode = "";
		String errors = "";
			
		
		PmCustomer pmCustomer = new PmCustomer(sFParams);
		BmoCustomer bmoCustomer = new BmoCustomer();
		
				
		StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
		int i = 1;
		while (inputData.hasMoreTokens() && i < 20000) { 
			String sql = "";
			s = inputData.nextToken();
			StringTokenizer tabs = new StringTokenizer(s, "\t");
			
			bmoCustomer = new BmoCustomer();
			
			//codeCustomers = tabs.nextToken();
			displayName = tabs.nextToken();
			legalName = tabs.nextToken();			
			type = tabs.nextToken();
			rfc = tabs.nextToken();
		    dateCreate = tabs.nextToken();    
		    titleCode = tabs.nextToken();
		    firstName = tabs.nextToken();
		    fatherLastName = tabs.nextToken();
		    motherLastName = tabs.nextToken();
		    birthDate = tabs.nextToken();
		    phone = tabs.nextToken();
		    mobile = tabs.nextToken();
		    email = tabs.nextToken();
		    position = tabs.nextToken();
		    salesManCode = tabs.nextToken();
		    www = tabs.nextToken();
		    currencyCode = tabs.nextToken();
		    referralCode = tabs.nextToken();
		    sicCode = tabs.nextToken();
		    territoryCode = tabs.nextToken();
		    parentCode = tabs.nextToken();
		    
		    int titleId = 0, userId = 0;
		    
		    //Obtener el Id del Titulo
		    if (!titleCode.equals("empty")) {
			    sql = " SELECT titl_titleid FROM titles " + 
				      " WHERE titl_code = '" + titleCode + "'";
			    pmConn2.doFetch(sql);
			    if (pmConn2.next()) titleId = pmConn2.getInt("titl_titleid");
			    
			    bmoCustomer.getTitleId().setValue(titleId);
		    }
		    
		    //Obtener el Id del Vendedor
		    if (!salesManCode.equals("empty")) {
			    sql = " SELECT user_userid FROM users " + 
				      " WHERE user_code = '" + salesManCode + "'";			    
			    pmConn2.doFetch(sql);
			    if (pmConn2.next()) userId = pmConn2.getInt("user_userid");
			    
			    bmoCustomer.getSalesmanId().setValue(userId);
		    }
		    
		    int currencyId = 0;
		    //Obtener el Id de la moneda
		    if (!currencyCode.equals("empty")) {
			    sql = " SELECT cure_currencyid FROM currencies " + 
				      " WHERE cure_code = '" + currencyCode + "'";
			    pmConn2.doFetch(sql);
			    if (pmConn2.next()) currencyId = pmConn2.getInt("cure_currencyid");
			    
			    bmoCustomer.getCurrencyId().setValue(currencyId);
		    }
			
		    
		    int referralId = 0;
		    //Obtener el Id de la moneda
		    if (!referralCode.equals("empty")) {
		    	sql = " SELECT refe_referralid FROM referrals " + 
		    		  " WHERE (refe_code = trim(LEADING ' ' FROM '" + referralCode + "') OR refe_name = trim(LEADING ' ' FROM '" + referralCode + "'))";
		    	System.out.println("refe_code " + sql);
  				pmConn2.doFetch(sql);			    
			    if (pmConn2.next()) referralId = pmConn2.getInt("refe_referralid");
			    
			    bmoCustomer.getReferralId().setValue(referralId);
		    }
		   	
			int sicId = 0;
			if (!sicCode.equals("empty")) {
				sql = " SELECT indu_industryid FROM industries " + 
			          " WHERE indu_name = '" + sicCode + "'";
				pmConn2.doFetch(sql);
				if (pmConn2.next()) sicId = pmConn2.getInt("indu_industryid");
				
				bmoCustomer.getIndustryId().setValue(sicId);
			}
			
		    int terretoryId = 0;
		    //Obtener el Id de la moneda		    		    
		    if (!parentCode.equals("empty")) {
		    	sql = " SELECT terr_territoryid FROM territories " + 
  			          " WHERE terr_name = '" + parentCode.toUpperCase() + "'";
  				pmConn2.doFetch(sql);			    
			    if (pmConn2.next()) terretoryId = pmConn2.getInt("terr_territoryid");
			    
			    bmoCustomer.getTerritoryId().setValue(terretoryId);
		    }
		    
		    int consultingServiceId = 0;
		    //Obtener el Id de la moneda		    		    
		    if (!territoryCode.equals("empty")) {
		    	sql = " SELECT cose_consultingserviceid FROM consultingservices " + 
  			          " WHERE cose_name = '" + territoryCode.toUpperCase() + "'";
  				pmConn2.doFetch(sql);			    
			    if (pmConn2.next()) consultingServiceId = pmConn2.getInt("cose_consultingserviceid");
			    
			    bmoCustomer.getConsultingServiceId().setValue(consultingServiceId);
		    }
		    
			
		    //if (!codeCustomers.equals("empty")) bmoCustomer.getReferralComments().setValue(codeCustomers);			
		    if (!displayName.equals("empty")) bmoCustomer.getDisplayName().setValue(displayName);
		    
		    if (!legalName.equals("empty")) bmoCustomer.getLegalname().setValue(legalName);
		    if (!rfc.equals("empty")) bmoCustomer.getRfc().setValue(rfc);
			if (!type.equals("empty")) bmoCustomer.getCustomertype().setValue(type);
			
			
			//Cambio al Nombre
			firstName = firstName.toUpperCase().substring(0,1) + firstName.toLowerCase().substring(1,firstName.length());
			bmoCustomer.getFirstname().setValue(firstName);
			
			fatherLastName = fatherLastName.toUpperCase().substring(0,1) + fatherLastName.toLowerCase().substring(1,fatherLastName.length());
			bmoCustomer.getFatherlastname().setValue(fatherLastName);
			
			if (!motherLastName.equals("empty")) {
				motherLastName = motherLastName.toUpperCase().substring(0,1) + motherLastName.toLowerCase().substring(1,motherLastName.length());
				bmoCustomer.getMotherlastname().setValue(motherLastName);
			}
			
			if (!dateCreate.equals("empty")) bmoCustomer.getEstablishmentDate().setValue(dateCreate);			
			if (!birthDate.equals("empty")) bmoCustomer.getBirthdate().setValue(birthDate);
			if (!phone.equals("empty")) bmoCustomer.getPhone().setValue(phone);
			if (!mobile.equals("empty")) bmoCustomer.getMobile().setValue(mobile);
			if (!email.equals("empty")) bmoCustomer.getEmail().setValue(email);
			if (!position.equals("empty")) bmoCustomer.getPosition().setValue(position);
			if (!www.equals("empty")) bmoCustomer.getWww().setValue(www);			
			
			pmCustomer.save(pmConn, bmoCustomer, bmUpdateResult);
			 
		}
		
		if (!bmUpdateResult.hasErrors())
			pmConn.commit();
	
	} catch (Exception e) {
		pmConn.rollback();
		throw new SFException(e.toString());
		
	} finally {
		pmConn.close();
	}
%>	
	<%= bmUpdateResult.errorsToString() %>
<%		
response.sendRedirect("/batch/flex_revisioncustomers.jsp?action=complete&errorsave=" + bmUpdateResult.errorsToString());
	
		
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


