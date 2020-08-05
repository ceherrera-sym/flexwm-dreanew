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
	String debug = "Inicial <br>";
	try { 

		
		// Inicializa sFParams
		SFParams sFParams = new SFParams();
		LoginInfo loginInfo = new LoginInfo();

		debug += " Por inicializar sfparams<br>";
		
		SFParamsServiceImpl.fillSFParamsFromWebXML(sFParams, config.getServletContext());
		
		debug += " sFParams iicialidado<br>";
		
		// Revisa que venga de login correcto
		String url = request.getRequestURL().toString();
		String baseURL = url.substring(0, url.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
		String loginURL = (String)session.getAttribute("loginurl");
		
		debug += " viene de login<br>";
		
		// Revisa que la sesion sea similar de la forma y el post
		if (baseURL.equalsIgnoreCase(loginURL)) {
			String email = request.getParameter("email");
			String password = request.getParameter("password");  
			
			debug += " parametros obtenidos<br>";
			
			// Revisar email y password dependiendo si viene de google o de usuario password
			PmUser pmUser = new PmUser(sFParams);
			loginInfo = pmUser.login(email, password);	
			
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
				response.sendRedirect("login_standalone.jsp?email=" + email + "&msg=1");		
			}
		} else {
			throw new Exception("Error de Validacion de Seguridad. baseUrl: " + baseURL + ", loginUrl: " + loginURL);
		}
	} catch (Exception e) {
%>
			Error no esperado: <%= e.toString() %>
			<br>
			Cadena debug: <%= debug %>
			<br>
			Vuelve a ingresar haciendo click <a href="login_standalone.jsp">aqui</a>.
<%
	}
%>


