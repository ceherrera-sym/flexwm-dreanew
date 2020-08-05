<!--  
/**
 * @author César Herrera Hernández
 */ -->

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
	String comments = "";
	String appTitle = "SYMGF";
	String defaultCss = "symgae.css";
	SFParams sFParams = new SFParams();
	boolean isMobile = false;
	
	boolean isExternal = false;
	String w = request.getParameter("w");
	if (w != null && w.equals("EXT")) isExternal = true;
	int decryptId = 0;
	
	try {
	
		comments += "Inicia revision si es externo; ";
		if (isExternal) {
			comments += "Es externo, inicia configuracion; ";
		
			// Preparar informacion login automatico con datos de usuario del servidor
			LoginInfo loginInfo = new LoginInfo();
			loginInfo.setLoggedIn(true);
			loginInfo.setEmailAddress(getServletContext().getInitParameter("systememail"));
			SFParamsServiceImpl.fillSFParamsFromWebXML(sFParams, config.getServletContext());
			SFParamsServiceImpl.paramsFactory(sFParams, loginInfo, config.getServletContext());	
			
			String rawId = request.getParameter("z");
			if (rawId != null) decryptId = SFServerUtil.decryptId(rawId);
			
			comments += "Finaliza configuracion externa; ";
		} else {
			// Busca los parametros en sesion
			sFParams = (SFParams)session.getAttribute(SFParams.SFPARAMSNAME);
			SFParamsServiceImpl.fillSFParamsFromWebXML(sFParams, config.getServletContext());
		}
		
		appTitle = sFParams.getMainAppTitle();
		defaultCss = sFParams.getDefaultCss();
		comments += "Titulos asignados; ";
		
		if (SFServerUtil.isMobile(request.getHeader("user-agent"))) {
			defaultCss = "symgae-mobile.css";
			isMobile = true;
		}
		
		comments += "Finaliza configuracion login_opt; ";
		
	} catch (Exception e) {
		%>
			Error de login_opt.jsp: <%= e.toString() %>
		<%
	}
%>

