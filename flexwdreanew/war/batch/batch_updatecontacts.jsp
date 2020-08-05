
<%@page import="com.flexwm.server.FlexUtil"%>
<%@page import="com.flexwm.server.cm.PmCustomerContact"%>
<%@page import="com.flexwm.shared.cm.BmoCustomerContact"%>
<%@page import="com.flexwm.shared.cm.BmoExternalSales"%>
<%@page import="com.flexwm.server.cm.PmExternalSales"%>
<%@page import="com.flexwm.shared.op.BmoProductPrice"%>
<%@include file="../inc/imports.jsp" %>
<%@page import="com.flexwm.server.op.PmProduct"%>
<%@page import="com.flexwm.shared.op.BmoProduct"%>
<%@page import="com.flexwm.server.op.PmProductPrice"%>
<%@page import="com.flexwm.shared.op.BmoProduct"%>
<%@page import="java.util.StringTokenizer"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@page import="java.text.DecimalFormat"%>
<%@include file="../inc/login.jsp" %>
<%
	// Variables
	String errorSave = "";
	String programTitle = "Actualizaci&oacuten de contactos";
	String errorLabel = request.getParameter("errorLabel");
	String errorText = request.getParameter("errorText");
	String errorException = request.getParameter("errorException");
	if(request.getParameter("errorsave") != null)
	 errorSave = request.getParameter("errorsave");
	String populateData = "", action = "";
	String errors = "";
	if (request.getParameter("populateData") != null) 
		populateData = request.getParameter("populateData");
	if (request.getParameter("action") != null) 
		action = request.getParameter("action");
%>

<html>
<head>

<title>:::<%= programTitle %>:::</title>
<link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>">


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
	          <li><b>Los campos con '*' son obligatorios. Colocar "-" para los campos vac&iacuteos.</b></li>          
	          <li>Formato (| = tab): ID | Puesto | Alias | Nombre* | A. Paterno | A. Materno |Email | Tel&eacutefono | Ext | T. Celular | Clave Cliente* | Nombre Cliente 
	          | Nombre Legal | Tipo de Cliente  </li>
	        </td>
	    </tr>    
	    <tr>
	        <td colspan="3">  
	            <TABLE cellpadding="0" cellspacing="0" border="0"  width="80%">
				    <FORM action="?" method="post" name="listFilter">
				    <input type="hidden" name="action" value="revision">
	             	<tr class="" height="35">
					    <td colspan="10" align="center" width="100%">
							<textarea name="populateData" width="100%" cols="100" rows="30"></textarea>
				        </td>
				    </tr>
	                <tr class="">
	                    <td align="center" colspan="4" height="35" valign="middle">
	                        <input class="formSaveButton" type="submit" value="SIGUIENTE">
	                    </td>
	                </tr>			    
	                </FORM>								
	            </TABLE>
	        </TD>
	    </TR>
	</TABLE>
<% 
	} else if (action.equalsIgnoreCase("revision")) {
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
				
			String cuco_customercontactid = "";
			String cuco_position = "";
			String cuco_alias = "";
			String cuco_fullname = "";
			String cuco_fatherlastname = "";
			String cuco_motherlastname = "";
			String cuco_email = "";
			String cuco_number = "";
			String cuco_extension = "";
			String cuco_cellphone = "";
			String cust_code = "";
			String cust_displayname = "";
			String cust_legalname = "";
			String cust_customertype = "";

			 errors = "";
		%>
		<table width="80%" border="0" align="center"  class="">
		    <TR>
		        <td colspan="4">  
		            <TABLE cellpadding="0" cellspacing="0" border="1" rules=all width="90%">               
		            	<tr valign="middle" align="center" class="">
		                    <td class="reportHeaderCell">&nbsp;#</td>
		                    <td class="reportHeaderCell">ID</td>
		                    <td class="reportHeaderCell">Puesto</td>
		                     <td class="reportHeaderCell">Alias</td>
							<td class="reportHeaderCell">Nombre</td>		                   
		                    <td class="reportHeaderCell">A. Paterno</td>
		                    <td class="reportHeaderCell">A. Materno</td>
		                    <td class="reportHeaderCell">Email</td>
		                    <td class="reportHeaderCell">Tel&eacutefono:</td>		                 
		                    <td class="reportHeaderCell">Ext</td>
		                    <td class="reportHeaderCell">T. Celular</td>
		                    <td class="reportHeaderCell">Clave Cliente</td>
		                    <td class="reportHeaderCell">Nombre Cliente</td>
		                    <td class="reportHeaderCell">Nombre Legal</td>		                 
		                    <td class="reportHeaderCell">Tipo de Cliente</td>
		                     <td class="reportHeaderCell">Errores</td>
		                </tr>
		            <%           
		        		while (inputData.hasMoreTokens()) {
		        			errors = "";
		        		    s = inputData.nextToken();
		        			StringTokenizer tabs = new StringTokenizer(s, "\t");        			
		        			
		        			// Recuperar valores
		        		
		        			cuco_customercontactid = FlexUtil.stripAccents( tabs.nextToken());
		        			cuco_position =  FlexUtil.stripAccents( tabs.nextToken());
		        			cuco_alias = FlexUtil.stripAccents( tabs.nextToken());
		        			cuco_fullname = FlexUtil.stripAccents( tabs.nextToken());
		        			cuco_fatherlastname = FlexUtil.stripAccents( tabs.nextToken());
		        			cuco_motherlastname = FlexUtil.stripAccents( tabs.nextToken());
		        			cuco_email = FlexUtil.stripAccents( tabs.nextToken());
		        			cuco_number = FlexUtil.stripAccents( tabs.nextToken());
		        			cuco_extension = FlexUtil.stripAccents( tabs.nextToken());
		        			cuco_cellphone = FlexUtil.stripAccents( tabs.nextToken());
		        			cust_code = FlexUtil.stripAccents( tabs.nextToken());
		        			cust_displayname = FlexUtil.stripAccents( tabs.nextToken());
		        			cust_legalname = FlexUtil.stripAccents( tabs.nextToken());
		        			cust_customertype = FlexUtil.stripAccents( tabs.nextToken());
		        			
		        			pmConn.disableAutoCommit();
		        			pmConn2.disableAutoCommit();
                        	
							// Formatear; quitar espacios
		        			
					
		        			cuco_customercontactid = cuco_customercontactid.trim();		        			
		        			cust_code = cust_code.trim();
		        			cust_customertype = cust_customertype.trim();
		        			
		        			cuco_position = FlexUtil.stripAccents(cuco_position);
		        			cuco_fullname = FlexUtil.stripAccents(cuco_fullname);
		        			cuco_fatherlastname = FlexUtil.stripAccents(cuco_fatherlastname);
		        			cuco_motherlastname = FlexUtil.stripAccents(cuco_motherlastname);
		        			cuco_email = FlexUtil.stripAccents(cuco_email);
		        			cust_displayname = FlexUtil.stripAccents(cust_displayname);
		        			cust_legalname = FlexUtil.stripAccents(cust_legalname);
		        			
		        			if(!cuco_customercontactid.equalsIgnoreCase("-")){
		        				sql = "SELECT cuco_customercontactid from customercontacts where cuco_customercontactid = "+cuco_customercontactid;
		        			
		        				pmConn2.doFetch(sql);
		        				if(!pmConn2.next()){
		        					errors += "El contacto no existe, " ;
		        				}
		        			}
		        			// Validar cliente
		        			if (!cust_code.equalsIgnoreCase("-")) {
			        			sql = " select cust_customerid from customers where cust_code like '"+cust_code+"'";
			        			
			        			pmConn2.doFetch(sql);
			        			if (!pmConn2.next()) {
			        				errors += "El Cliente " + cust_code + " no Existe: " ;
			        			}
		        			}
		        			
                                                               
		        			
		    		%>      
                            <!--LLenar tabla -->
		    				<TR class="reportCellEven" width="100%">
		    					<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, cuco_customercontactid, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, cuco_position, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, cuco_alias, BmFieldType.STRING)%>
								<%=HtmlUtil.formatReportCell(sFParams, cuco_fullname, BmFieldType.STRING)%>
								<%=HtmlUtil.formatReportCell(sFParams, cuco_fatherlastname, BmFieldType.STRING)%>
								<%=HtmlUtil.formatReportCell(sFParams, cuco_motherlastname, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, cuco_email, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, cuco_number, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, cuco_extension, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, cuco_cellphone, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, cust_code, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, cust_displayname, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, cust_legalname, BmFieldType.STRING)%>
		    					<%=HtmlUtil.formatReportCell(sFParams, cust_customertype, BmFieldType.STRING)%>
		    					
		    				    <td><font color="red"><%= errors %></font></prcp_price>
		    				</TR>
		    		<%
		    			
		    			i++;        		
		        	}
		            %>
			</TABLE>
		
			<TABLE cellpadding="0" cellspacing="0" border="0"  width="100%">
				<FORM action="?" method="POST" name="listFilter">	
				<input type="hidden" name="action" value="populate">
				<input type="hidden" name="populateData" value="<%= populateData %>">			
				<tr class="">
				    <td align="center" colspan="4" height="35" valign="middle">
				    <% if (errors.equals("")) { %>
				        <input type="submit" class="formSaveButton" value="GUARDAR">
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
		
		BmoCustomerContact bmoCustomerContact = new BmoCustomerContact();
		PmCustomerContact pmCustomerContact = new PmCustomerContact(sFParams);
		
		PmConn pmConn = new PmConn(sFParams);
		pmConn.open();
		
		PmConn pmConn2 = new PmConn(sFParams);
		pmConn2.open();
		int customercontactid = 0;
		int cust_customerid = 0;
		
		
		
		try {
			String cuco_customercontactid = "";
			String cuco_position = "";
			String cuco_alias = "";
			String cuco_fullname = "";
			String cuco_fatherlastname = "";
			String cuco_motherlastname = "";
			String cuco_email = "";
			String cuco_number = "";
			String cuco_extension = "";
			String cuco_cellphone = "";
			String cust_code = "";
			String cust_displayname = "";
			String cust_legalname = "";
			String cust_customertype = "";

			errors = "";
			pmConn.disableAutoCommit();
			StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
			String s = "";
			int i = 1;
			while (inputData.hasMoreTokens()) {
				String sql = "";
				s = inputData.nextToken();
				StringTokenizer tabs = new StringTokenizer(s, "\t");
				errors = "";
				bmUpdateResult = new BmUpdateResult();
				
				cuco_customercontactid = FlexUtil.stripAccents( tabs.nextToken());
    			cuco_position =  FlexUtil.stripAccents( tabs.nextToken());
    			cuco_alias = FlexUtil.stripAccents( tabs.nextToken());
    			cuco_fullname = FlexUtil.stripAccents( tabs.nextToken());
    			cuco_fatherlastname = FlexUtil.stripAccents( tabs.nextToken());
    			cuco_motherlastname = FlexUtil.stripAccents( tabs.nextToken());
    			cuco_email = FlexUtil.stripAccents( tabs.nextToken());
    			cuco_number = FlexUtil.stripAccents( tabs.nextToken());
    			cuco_extension = FlexUtil.stripAccents( tabs.nextToken());
    			cuco_cellphone = FlexUtil.stripAccents( tabs.nextToken());
    			cust_code = FlexUtil.stripAccents( tabs.nextToken());
    			cust_displayname = FlexUtil.stripAccents( tabs.nextToken());
    			cust_legalname = FlexUtil.stripAccents( tabs.nextToken());
    			cust_customertype = FlexUtil.stripAccents( tabs.nextToken());
    			
    			cuco_customercontactid = cuco_customercontactid.trim();		        			
    			cust_code = cust_code.trim();
    			cust_customertype = cust_customertype.trim();
    			
    			cuco_position = FlexUtil.stripAccents(cuco_position);
    			cuco_fullname = FlexUtil.stripAccents(cuco_fullname);
    			cuco_fatherlastname = FlexUtil.stripAccents(cuco_fatherlastname);
    			cuco_motherlastname = FlexUtil.stripAccents(cuco_motherlastname);
    			cuco_email = FlexUtil.stripAccents(cuco_email);
    			cust_displayname = FlexUtil.stripAccents(cust_displayname);
    			cust_legalname = FlexUtil.stripAccents(cust_legalname);
    			
    			if(!cuco_customercontactid.equalsIgnoreCase("-")){
    				sql = "SELECT cuco_customercontactid from customercontacts where cuco_customercontactid = "+cuco_customercontactid;
    			
    				pmConn2.doFetch(sql);
    				if(pmConn2.next()){
    					customercontactid = pmConn2.getInt("cuco_customercontactid");
    				}
    			}
    			if (!cust_code.equalsIgnoreCase("-")) {
        			sql = " select cust_customerid from customers where cust_code like '"+cust_code+"'";
        		
        			pmConn2.doFetch(sql);
        			if (pmConn2.next()) {
        				cust_customerid = pmConn2.getInt("cust_customerid");
        			}
    			}
    			
    			bmoCustomerContact =(BmoCustomerContact)pmCustomerContact.get(customercontactid);
    			if(!cuco_position.equalsIgnoreCase("-")){
    		 	 bmoCustomerContact.getPosition().setValue(cuco_position);
    			}
    			if(!cuco_alias.equalsIgnoreCase("-")){
    				bmoCustomerContact.getAlias().setValue(cuco_alias);
    			}
    			if(!cuco_fullname.equalsIgnoreCase("-")){
    				bmoCustomerContact.getFullName().setValue(cuco_fullname);
    			}
    			if(!cuco_fatherlastname.equalsIgnoreCase("-")){
    				bmoCustomerContact.getFatherLastName().setValue(cuco_fatherlastname);
    			}
    			if(!cuco_motherlastname.equalsIgnoreCase("-")){
    				bmoCustomerContact.getMotherLastName().setValue(cuco_motherlastname);
    			}
    			if(!cuco_email.equalsIgnoreCase("-")){
    				bmoCustomerContact.getEmail().setValue(cuco_email);
    			}
    			if(!cuco_number.equalsIgnoreCase("-")){
    				bmoCustomerContact.getNumber().setValue(cuco_number);
    			}
    			if(!cuco_extension.equalsIgnoreCase("-")){
    				bmoCustomerContact.getExtension().setValue(cuco_extension);
    			}
    			if(!cuco_cellphone.equalsIgnoreCase("-")){
    				bmoCustomerContact.getCellPhone().setValue(cuco_cellphone);
    			}
    			if(cust_customerid > 0){
    				bmoCustomerContact.getCustomerId().setValue(cust_customerid);
    			}
    			
    			pmCustomerContact.saveSimple(pmConn, bmoCustomerContact, bmUpdateResult);
    			
    			if (!bmUpdateResult.hasErrors()){
    				pmConn.commit();
    			}
    			
			}
			
		}catch (Exception e){
			pmConn.rollback();
			throw new SFException(e.toString());
		}finally{
			pmConn.close();
			pmConn2.close();
		}
		
		response.sendRedirect("batch_updatecontacts.jsp?action=complete&errorsave=" + errorSave);
			
	}  else if (action.equals("complete") ) { %>
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
					<a href="?">Reiniciar</a>
				</td>
			</tr>
			</table>
	<% } %>
</body>
</html>