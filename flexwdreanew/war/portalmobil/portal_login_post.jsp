<!--  
/**
 * @author César Herrera Hernández
 */ -->
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
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
		String appTitle = "inadico";
		LoginInfo loginInfo = new LoginInfo();
		loginInfo.setLoggedIn(true);
		loginInfo.setEmailAddress(getServletContext().getInitParameter("systememail"));
		SFParamsServiceImpl.fillSFParamsFromWebXML(sFParams, config.getServletContext());
		SFParamsServiceImpl.paramsFactory(sFParams, loginInfo, config.getServletContext());

		String company = "";
		
		if (request.getParameter("company") != null) {
			company = request.getParameter("company");			
		}
		
		
		String dbUrl = sFParams.getProdDbUrl();
		String appUrl = sFParams.getAppURL();
		
		dbUrl = dbUrl.replace(appTitle, company.toLowerCase());
		appUrl = appUrl.replace(appTitle, company.toLowerCase());
		
		sFParams.setProdDbUrl(dbUrl);
		sFParams.setAppURL(appUrl);
		
		sFParams.setProdJndi(null);
		//Asignar parametros de sesion 
		session.setAttribute("sfparamPortal", sFParams);
		session.setAttribute("loginInf", loginInfo);
		
		String sql = "";
		String user = request.getParameter("user");
		String passw = request.getParameter("passw");
		if(!(company.length() > 0)){
			response.sendRedirect("portal_login.jsp?user=" + user + "&msg=3");
		}
		boolean isMobile = false;

		if (SFServerUtil.isMobile(request.getHeader("user-agent"))) {
			isMobile = true;
		}
		
		
		sql = "SELECT cust_customerid,cust_customercategory FROM customers " + "WHERE cust_user = '" + user
				+ "' " + "AND cust_passw = '" + new DESPassword().encryptPassword(passw) + "'";

		PmConn pmConn = new PmConn(sFParams);

		pmConn.open();

		pmConn.doFetch(sql);
		if (pmConn.next()) {

			if (pmConn.getString("cust_customercategory").equals(""+BmoCustomer.CATEGORY_LESSEE)) {
				session.setAttribute("Id", pmConn.getString("cust_customerid"));

				response.sendRedirect("portal_start_mobile.jsp?program=racc&viewHelp=1");

			} else {

				response.sendRedirect("portal_login.jsp?user=" + user + "&msg=1");
			}

		} else {
			response.sendRedirect("portal_login.jsp?msg=2");
		}

		pmConn.close();

	} catch (Exception e) {
%>
Error no esperado:
<%=e.toString()%>
<br>
Vuelve a ingresar haciendo click
<a href="portal_login.jsp">aqui</a>
.

<%
	}
%>


