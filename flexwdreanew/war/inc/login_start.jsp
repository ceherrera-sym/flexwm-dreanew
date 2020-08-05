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
	String appTitle = "FlexWM";
	String defaultCss = "symgae.css";
	String gwtCss = "gwt_standard.css";
	boolean isMobile = false;
	BmoUser bmoUser;
	String comments = "";
	
	SFParams sFParams;
	LoginInfo loginInfo = new LoginInfo();
	
	try {
		// Obtener informacion de SFParams
		sFParams = (SFParams)session.getAttribute(SFParams.SFPARAMSNAME);
		if (sFParams == null) {
			// No esta inicializado, configurar
			sFParams = new SFParams();
			SFParamsServiceImpl.fillSFParamsFromWebXML(sFParams, config.getServletContext());
			comments += "No existia cliente en sesion, se creo; ";
		} else {
			comments += "Ya existe sFParams en sesion, se reestablece info xml; ";
			// Lo siguiente se cambio para reestablecer permisos
			//SFParamsServiceImpl.fillSFParamsFromWebXML(sFParams, config.getServletContext());
			loginInfo = sFParams.getLoginInfo();
			SFParamsServiceImpl.paramsFactory(sFParams, loginInfo, config.getServletContext());
		}
		
		// Si es autentificacion de google, inicializa LoginInfo
		comments += "Comienza a determinar el tipo de autentificacion.";
		if (sFParams.isGoogleAuth()) {
			comments += "Es autentificacion google, tomando parametros; ";
			
			UserService userService = UserServiceFactory.getUserService();
			User user = userService.getCurrentUser();
			loginInfo.setLoggedIn(true);
			loginInfo.setEmailAddress(user.getEmail());
			loginInfo.setNickname(user.getNickname());
			SFParamsServiceImpl.paramsFactory(sFParams, loginInfo, config.getServletContext());
			
			comments += "Tomando parametros google finalizados; ";
		} else {
			//Si es autentificacion interna, continua
			comments += "Es autentificacion interna; ";
		}
		
		if (sFParams.getLoginInfo().isLoggedIn()) {
			comments += "Obteniendo titulo de aplicacion; ";
			
			appTitle = sFParams.getMainAppTitle();
			defaultCss = sFParams.getDefaultCss();
			
			comments += "Obteniendo configuracion personal usuario; ";
			
			// Configura estilos segun usuario loggeado
			bmoUser = sFParams.getLoginInfo().getBmoUser();
			
			if (bmoUser.getUiTemplate().toChar() == BmoUser.UITEMPLATE_MINIMALIST) {
				comments += "Es minimalista; ";
				gwtCss = "gwt_minimalist.css";
			} else {
				comments += "Es plantilla estandar; ";
			}
			
			session.setAttribute(SFParams.SFPARAMSNAME, sFParams);
			
			comments += "sFParams inicializado correctamente";
			
		} else {
			if (!sFParams.isGoogleAuth()) {
				response.sendRedirect("./login.jsp");
			}
		}
		
		// Determina si es mobil o 
		if (SFServerUtil.isMobile(request.getHeader("user-agent"))) {
			defaultCss = "symgae-mobile.css";
			isMobile = true;
			comments += "Cliente mobil; ";
		} else {
			comments += "Cliente escritorio; ";
		}
	} catch (Exception e) {
		%>
			<b>Iniciando...</b>
			
			<!-- <%= e.toString() %> -->
		<%
	}
%>
	<!-- <%= comments %> -->