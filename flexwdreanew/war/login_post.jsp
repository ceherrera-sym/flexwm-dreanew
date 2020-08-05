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
		SFParamsServiceImpl.fillSFParamsFromWebXML(sFParams, config.getServletContext());
		
		// Revisa que venga de login correcto
		String url = request.getRequestURL().toString();
		String baseURL = url.substring(0, url.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
		String loginURL = (String)session.getAttribute("loginurl");
		
		// Revisa que la sesion sea similar de la forma y el post
		if (baseURL.equalsIgnoreCase(loginURL)) {
			//String callerURL = request.getAttribute("javax.servlet.forward.request_uri");
			String checkSessionId = request.getParameter("googleappchecksum");
			String email = request.getParameter("email");
			String password = request.getParameter("password");  
			String googleId = request.getParameter("googleid");
			String googleEmail = request.getParameter("googleemail");
			
			// Validacion de sesiones
			if (!checkSessionId.equalsIgnoreCase(session.getId())) {
				throw new Exception("Error de login.");
			}
			
			// Revisar email y password dependiendo si viene de google o de usuario password
			PmUser pmUser = new PmUser(sFParams);
			if (googleId.length() > 0) {
				loginInfo = pmUser.googleLogin(googleId, googleEmail);
			} else {
				loginInfo = pmUser.login(email, password);	
			}
			
			if (loginInfo.isLoggedIn()) {
				// Establecer toda la informacion del login 
				SFParamsServiceImpl.paramsFactory(sFParams, loginInfo, config.getServletContext());
				
				// Obtiene informacion del servidor y la almacena en variable de sesion
				String appUrl = "";
				if (request.isSecure())
					appUrl += "https://";
				else 
					appUrl += "http://";
				
				appUrl += request.getServerName();
				if (request.getServerPort() > 0 && request.getServerPort() != 80) {
					appUrl += ":" + request.getServerPort();
				}
				if (request.getContextPath().length() > 0) {
					appUrl += request.getContextPath();	
				}
				appUrl += "/";
				session.setAttribute(SFParams.BASEURLVARNAME, appUrl);
				sFParams.setAppURL(appUrl);
				
				// Almacenar Login Info en sesion
				session.setAttribute(SFParams.SFPARAMSNAME, sFParams);
			
				response.sendRedirect("start.jsp" + "?v=" + new Date().getTime() * 27822534.44332344);
			} else {
				if (googleId.length() > 0) {
					response.sendRedirect("login.jsp?googleemail=" + googleEmail + "&msg=2");	
				} else {
					response.sendRedirect("login.jsp?email=" + email + "&msg=1");		
				}
			}
		} else {
			throw new Exception("Error Interno de Login.");
		}
	} catch (Exception e) {
%>
			Error no esperado: <%= e.toString() %>
			<br>
			Vuelve a ingresar haciendo click <a href="login.jsp">aqui</a>.
<%
	}
%>


