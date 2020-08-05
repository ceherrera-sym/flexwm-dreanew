<!--  
/**
 * @author César Herrera Hernández
 */ -->
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.util.*"%>
<%@ page import="com.symgae.server.*"%>
<%@ page import="com.symgae.server.sf.*"%>
<%@ page import="com.symgae.shared.SFParams"%>
<%@ page import="com.symgae.shared.BmFilter"%>
<%@ page import="com.symgae.shared.BmSearchField"%>
<%@ page import="com.symgae.shared.BmOrder"%>
<%@ page import="com.symgae.shared.BmObject"%>
<%@ page import="com.symgae.shared.BmField"%>
<%@ page import="com.symgae.shared.BmFieldType"%>
<%@ page import="com.symgae.shared.sf.*"%>
<%@ page import="com.symgae.shared.LoginInfo"%>
<%@ page import="java.lang.reflect.Constructor"%>

<%
	try { 
		// Inicializa sFParams
		
		
		SFParams sFParams = new SFParams();
		
		LoginInfo loginInfo = new LoginInfo();
		loginInfo.setLoggedIn(true);
		loginInfo.setEmailAddress(getServletContext().getInitParameter("systememail"));
		SFParamsServiceImpl.fillSFParamsFromWebXML(sFParams, config.getServletContext());
		SFParamsServiceImpl.paramsFactory(sFParams, loginInfo, config.getServletContext());	
		
		String sql = "";
		String user = request.getParameter("user");
		String passw = request.getParameter("passw");	
                    
        sql = "SELECT cust_customerid,cust_customercategory FROM customers " +
        	  "WHERE cust_user = '" + user + "' " +
              "AND cust_passw = '" + new DESPassword().encryptPassword(passw) + "'";

        PmConn pmConn = new PmConn(sFParams);
        
        pmConn.open();
        
        pmConn.doFetch(sql);
    if(pmConn.next()){
    	
        	
        	 
        	  if(pmConn.getString("cust_customercategory").equals("E")){
        		  session.setAttribute("Id", pmConn.getString("cust_customerid"));
        		 
        		  response.sendRedirect("portal_start.jsp?program=racc") ;//+ "?Id=" + pmConn.getString("cust_customerid"));
        		  
        	  }else{
        		  
        		  response.sendRedirect("portal_login.jsp?user=" + user + "&msg=1");
        	  }
        	 
     }else {
        	 response.sendRedirect("portal_login.jsp?msg=2");
         }
		
		pmConn.close();
			
		
	} catch (Exception e) {
%>
			Error no esperado: <%= e.toString() %>
			<br>
			Vuelve a ingresar haciendo click <a href="portal_login.jsp">aqui</a>.
			
  <%}%>


