
<%@page import="com.flexwm.shared.cm.BmoCustomerContact"%>
<%@page import="com.flexwm.server.cm.PmCustomerContact"%>
<%@page import="com.flexwm.server.op.PmOrder"%>
<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@page import="com.flexwm.shared.fi.BmoRaccount"%>
<%@page import="com.flexwm.server.fi.PmRaccount"%>
<%@page import="com.flexwm.shared.wf.BmoWFlow"%>
<%@page import="com.flexwm.server.wf.PmWFlow"%>
<%@page import="com.flexwm.server.wf.PmWFlowType"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.server.cm.PmCustomer"%>
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/imports.jsp" %>
<%@include file="../inc/login.jsp" %>

<%
	String programTitle = "Proceso Pasar Título de Cliente a Contactos del cliente ";
	String programDescription = programTitle;
	String populateData = "", action = "";
%>

<html>
	<head>
		<title>:::<%= programTitle %>:::</title>
		<link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%> %>/css/<%= defaultCss %>">
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
	
	<% 
		PmConn pmConn = new PmConn(sFParams);
		pmConn.open();
		
		PmConn pmConn2 = new PmConn(sFParams);
		pmConn2.open();
		
		BmUpdateResult bmUpdateResult = new BmUpdateResult();
		
		String status = "";
		int idStart = 0, idEnd = 0;
		
		try {
			 String sql = "";
			 pmConn.disableAutoCommit();
			 pmConn2.disableAutoCommit();
			 
		     int i = 1;
		     
		     PmCustomerContact pmCustomerContact = new PmCustomerContact(sFParams);
		     BmoCustomerContact bmoCustomerContact =  new BmoCustomerContact();
		    
// 		     BmoOrder bmoOrder = new BmoOrder();
// 		     PmOrder pmOrder = new PmOrder(sFParams);
		     sql = "SELECT cust_code, cust_firstname, cust_fatherlastname, cust_motherlastname, cust_titleid"
		    		 + " FROM customers WHERE cust_titleid > 0;" ;
		     System.out.println("sql_"+sql);
		     pmConn2.doFetch(sql);
		     		    	 
		     int n = 1, customerContactId = 0;
		     while (pmConn2.next()) {
		    	 
		    	 // Si existe un contacto igual, pasar el titulo
		    	 if (pmCustomerContact.customerContactExists(pmConn,
							pmConn2.getString("cust_firstname"), 
		    			 	pmConn2.getString("cust_fatherlastname"),  
		    			 	pmConn2.getString("cust_motherlastname")) ) {
		    		 
		    		 // Obtiene el registro
		    		 customerContactId = pmCustomerContact.customerContactByName(pmConn, pmConn2.getString("cust_firstname"), 
			    			 	pmConn2.getString("cust_fatherlastname"),  
			    			 	pmConn2.getString("cust_motherlastname"));
		    		 
		    		 // Vuelve a validar que regrese uno
		    		 if (customerContactId > 0) {
		    			 %>
		    			 <BR>
		    			 #<%= n%>| <%= pmConn2.getString("cust_code")%>
		    			 <%= pmConn2.getString("cust_firstname")%>
 		    			 <%= pmConn2.getString("cust_fatherlastname")%>
 		    			 <%= pmConn2.getString("cust_motherlastname")%>
		    			 <%
		    			 
		    			 
		    			 bmoCustomerContact = (BmoCustomerContact)pmCustomerContact.get(customerContactId);
		    			 bmoCustomerContact.getTitleId().setValue(pmConn2.getInt("cust_titleid"));
		    			 pmCustomerContact.saveSimple(pmConn, bmoCustomerContact, bmUpdateResult);
		    		 }
		    	 }
		    	 n++;
		     }
		     
		 	if (!bmUpdateResult.hasErrors()) {
		 		pmConn.commit();
			 	%>
			 		<h2><b><font color="green">&#10004; Proceso Terminado &#10004;</font></b></h2>
		 		<%	
		 	}
		%>
		<%= bmUpdateResult.errorsToString() %> 		
		<%
		} catch (Exception e) {
			pmConn.rollback();
			throw new SFException(e.toString());
		
		} finally {
			pmConn.close();
			pmConn2.close();
		}
		%>
	</body>
</html>