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
	String appTitle = "SYMGF";
	String defaultCss = "symgae.css";
	String gwtCss = "gwt_standard.css";
	SFParams sFParams = new SFParams();
	boolean isMobile = false;
	BmoUser bmoUser;
	String messageTooLargeList = "";
	
	// Es llamada externa?
	boolean isExternal = false;
	String w = request.getParameter("w");
	if (w != null && w.equals("EXT")) isExternal = true;
	int decryptId = 0;
	
	try {
		// Busca los parametros en sesion
		sFParams = (SFParams)session.getAttribute(SFParams.SFPARAMSNAME);
		
		// Si no se encontraba en sesion, obtener datos de login
		if (sFParams == null) {
			LoginInfo loginInfo = new LoginInfo();
			loginInfo.setLoggedIn(true);
			SFParamsServiceImpl.paramsFactory(sFParams, loginInfo, config.getServletContext());
			
			// Almacena en sesion el objeto de parametros recien creado
			session.setAttribute(SFParams.SFPARAMSNAME, sFParams);		
		}
		
		bmoUser = sFParams.getLoginInfo().getBmoUser();
		
		appTitle = sFParams.getMainAppTitle();
		defaultCss = sFParams.getDefaultCss();
		
		messageTooLargeList = "No se puede desplegar una lista con mas de "+ 
				(sFParams.getBmoSFConfig().getMaxRecords().toInteger())  +" registros";
		
		if (bmoUser.getUiTemplate().toChar() == BmoUser.UITEMPLATE_MINIMALIST)
			gwtCss = "gwt_minimalist.css";
		
		if (SFServerUtil.isMobile(request.getHeader("user-agent")))
			isMobile = true;
		
		// Desencripta llave externa
		String rawId = request.getParameter("z");
		if (rawId != null) decryptId = SFServerUtil.decryptId(rawId);
		
	} catch (Exception e) {
	%>
		<%= e.toString() %>
	<%
	}
%>