<!--  
/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
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
	String appTitle = "SYMGF";
	String defaultCss = "symgae.css";
	SFParams sFParams = new SFParams();
	boolean isMobile = false;
	
	String barcodeSeparator = "_";
	int barcodeLength = 10;
	
	try {
		// Busca los parametros en sesion
		sFParams = (SFParams)session.getAttribute(SFParams.SFPARAMSNAME);
	
		// Si no se encontraba en sesion, obtener datos de login
		if (sFParams == null) {
			UserService userService = UserServiceFactory.getUserService();
			User user = userService.getCurrentUser();
			LoginInfo loginInfo = new LoginInfo();
			loginInfo.setLoggedIn(true);
			loginInfo.setEmailAddress(user.getEmail());
			loginInfo.setNickname(user.getNickname());
			SFParamsServiceImpl.paramsFactory(sFParams, loginInfo, config.getServletContext());
			
			// Almacena en sesion el objeto de parametros recien creado
			session.setAttribute(SFParams.SFPARAMSNAME, sFParams);		
		}
			
		appTitle = sFParams.getMainAppTitle();
		defaultCss = sFParams.getDefaultCss();
		
		defaultCss = "symgae-barcode.css";
		isMobile = true;
		
	} catch (Exception e) {
	%>
		<b>Iniciando...</b>
	<%
	}
%>