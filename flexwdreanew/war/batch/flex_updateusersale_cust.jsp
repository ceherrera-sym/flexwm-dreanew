<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="com.flexwm.server.cm.PmCustomer"%>
<%@page import="com.symgae.shared.SFException"%>
<%@include file="../inc/login.jsp" %>
<%
	String programTitle = "Actualizar vendedor en cliente";
	String errorSave = "";
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
</body>
<% if (action.equalsIgnoreCase("")) { %>
	<table width="100%" height="50%" border="0" align="center"  class="detailStart">	
	    <tr class="reportSubTitle">
	        <td colspan="1" align="left" width="99%" height="35" class="reportCellEven">
	          <li>Se recomienda elaborar preparaci&oacute;n en archivo de texto, seleccionar y pegar en el recuadro siguiente.</li>
	          <li>Formato (| = tab):Clave Cliente* | Clave Vendedor  </li>
	        </td>
	    </tr>    
	    <TR>
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

<% } else if (action.equalsIgnoreCase("revision")) { %>
	<%  PmConn pmConn = new PmConn(sFParams);
		pmConn.open();
		
		try {
			StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
			
			String custCode = "";
			String userCode = "";
			String s = "";
			String msg = "";
			String sql = "";
			
			int i = 1;
		%>
			<table width="80%" border="0" align="center"  class="">
				<tr>
					<td colspan="4">  
						 <TABLE cellpadding="0" cellspacing="0" border="1" rules=all width="90%">  
						 	<tr valign="middle" align="center" class="">
						 		 <td class="reportHeaderCell">&nbsp;#</td>
						 		 <td class="reportHeaderCell">Cliente</td>
						 		 <td class="reportHeaderCell">Vendedor</td>
						 		 <td class="reportHeaderCell">Errores</td>
						 	</tr>
			
		<%
			while (inputData.hasMoreTokens() && i < 2000){
				errors = "";
				s = inputData.nextToken();
	  			StringTokenizer tabs = new StringTokenizer(s, "\t"); 
	  			
	  			custCode = tabs.nextToken().trim();
	  			userCode = tabs.nextToken().trim();
	  			
	  			PmCustomer pmCustomer = new PmCustomer(sFParams);
	  			BmoCustomer bmoCustomer = new BmoCustomer();
	  			
	  			PmUser pmUser = new PmUser(sFParams);
	  			BmoUser bmoUser2 = new BmoUser();
	  			
	  			if (custCode.equalsIgnoreCase("empty")){
	  				sql = "SELECT cust_customerid FROM customers WHERE cust_code = '" + custCode + "'";
	  				pmConn.doFetch(sql);
	  				
	  				if (pmConn.next())
						bmoCustomer = (BmoCustomer)pmCustomer.get(pmConn.getInt("cust_customerid"));  					
	  				else
	  					errors += "No se encuentra el cliente con la clave " + custCode;
	  			}
	  			if (userCode.equalsIgnoreCase("empty")){
	  				sql = "SELECT user_userid FROM users WHERE user_code = '" + userCode + "'";
	  				pmConn.doFetch(sql);
	  				
	  				if (pmConn.next())
	  					bmoUser2 = (BmoUser)pmUser.get(pmConn.getInt("user_userid"));  					
	  				else
	  					errors += "No se encuentra el Vendedor con la clave " + userCode;
	  			}
	  			%>
	  				<TR class="reportCellEven" width="100%">
	  					<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>
	  					<%=HtmlUtil.formatReportCell(sFParams,custCode, BmFieldType.STRING)%>
			    		<%=HtmlUtil.formatReportCell(sFParams, userCode, BmFieldType.STRING)%>
			    		<td><font color="red"><%= errors %></font></prcp_price>
	  				</TR>
	  		<%
				i++;
			}
		%>
				 </TABLE>
				</td>			
			</tr>
		</table>
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
		
		} catch (Exception e){
			pmConn.rollback();
			throw new SFException(e.toString());
		} finally {
			pmConn.close();
		}
	%>
<% }  else if (action.equals("populate")) { %>
	<%
		BmUpdateResult bmUpdateResult = new BmUpdateResult();
		PmConn pmConn = new PmConn(sFParams);
		pmConn.open();
		
		try {
			pmConn.disableAutoCommit();
			String s = "";
			String custCode = "";
			String userCode = "";
			
			int customerId = 0;
			int userId = 0;
			
			BmoCustomer bmoCustomer = new BmoCustomer();
			PmCustomer pmCustomer = new PmCustomer(sFParams);
			
			StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
			int i = 1;
			while (inputData.hasMoreTokens() && i < 20000) {
				String sql = "";
				s = inputData.nextToken();
				StringTokenizer tabs = new StringTokenizer(s, "\t");
				errors = "";
				bmUpdateResult = new BmUpdateResult();
				
				custCode = tabs.nextToken().trim();
	  			userCode = tabs.nextToken().trim();
	  			

	  			sql = "SELECT cust_customerid FROM customers WHERE cust_code = '" + custCode + "'";
	  			pmConn.doFetch(sql);
	  				
	  			if (pmConn.next())
					customerId = pmConn.getInt("cust_customerid") ;	
	  			
	  			sql = "SELECT user_userid FROM users WHERE user_code = '" + userCode + "'";
	  			pmConn.doFetch(sql);
	  				
	  			if (pmConn.next())
	  				userId = pmConn.getInt("user_userid") ;
	  			
	  			bmoCustomer = (BmoCustomer)pmCustomer.get(customerId);
	  			
	  			bmoCustomer.getSalesmanId().setValue(userId);
	  			
	  			pmCustomer.saveSimple(pmConn, bmoCustomer, bmUpdateResult);
	  			
				i++;
			}
			
			if (!bmUpdateResult.hasErrors()){
				pmConn.commit();
			}
		}catch (Exception e){
			pmConn.rollback();
			throw new SFException(e.toString());
		}finally {
			pmConn.close();
		}
		response.sendRedirect(sFParams.getAppURL() + "batch/flex_updateusersale_cust.jsp?action=complete&errorsave=" + errorSave);
	%>
<% } else if (action.equals("complete") ) { %>
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
 




