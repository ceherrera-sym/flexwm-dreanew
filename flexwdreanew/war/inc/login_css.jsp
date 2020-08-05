<%@ page contentType="text/css" %>
<%@ page import="java.util.*" %>
<%@ page import="com.symgae.server.*" %>
<%@ page import="com.symgae.server.sf.*" %>
<%@ page import="com.symgae.shared.SFParams" %>
<%@ page import="com.symgae.shared.BmFilter" %>
<%@ page import="com.symgae.shared.BmSearchField" %>
<%@ page import="com.symgae.shared.BmOrder" %>
<%@ page import="com.symgae.shared.BmObject" %>
<%@ page import="com.symgae.shared.BmField" %>
<%@ page import="com.symgae.shared.BmFieldType" %>
<%@ page import="com.symgae.shared.sf.*" %>

<%@ page import="com.symgae.shared.LoginInfo" %>
<%@ page import="java.lang.reflect.Constructor" %>


<%
	SFParams sFParams = new SFParams();
	
	
	try {
		// Preparar informacion login automatico con datos de usuario del servidor
		LoginInfo loginInfo = new LoginInfo();
		loginInfo.setLoggedIn(true);
		loginInfo.setEmailAddress(getServletContext().getInitParameter("systememail"));
		SFParamsServiceImpl.fillSFParamsFromWebXML(sFParams, config.getServletContext());
		SFParamsServiceImpl.paramsFactory(sFParams, loginInfo, config.getServletContext());	
			
		
	} catch (Exception e) {
		%>
			Error de login_opt.jsp: <%= e.toString() %>
		<%
	}
%>

