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

<%
	String appTitle = "SYMGAE";
	String defaultCss = "login.css";
	session.setAttribute(SFParams.SFPARAMSNAME, null);
	String timeStamp = new Date().getTime() + "";
	
	// Obtiene informacion del servidor y la almacena en variable de sesion
	String url = request.getRequestURL().toString();
	String baseURL = url.substring(0, url.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
	session.setAttribute("loginurl", baseURL);
	
	BmoUser bmoUser;
	boolean isMobile = false;
	
	String email = request.getParameter("email");
	if (email == null) email = "";
	
	String msg = request.getParameter("msg");
	
	// Determina si es mobil o 
	if (SFServerUtil.isMobile(request.getHeader("user-agent"))) {
		isMobile = true;
	}
%>

<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<meta name="google-signin-client_id" content="<%= application.getInitParameter("googlesignid") %>">
<meta name="baseauthcode" content="<%= baseURL %>">
<link type="text/css" rel="stylesheet" href="./css/<%= defaultCss %>">

<% if (isMobile) { %>
	<meta name="viewport" content="width=320, initial-scale=1.0, maximum-scale=1">
<% } %>

<title>:::<%= appTitle %>:::
</title>
</head>

<body class="default">
	<table id="loginTable" width="100%" height="80%">
		<tr height="100%" valign="middle">
			<td width="100%" align="center" height="100%" valign="middle">
				<form id="loginform" action="login_post_standalone.jsp" method="POST">
					<table class="loginTable">
						<tr>
							<td colspan="2" align="center">
								<a href="https://www.flexwm.com" target="_blank"><img src="./img/main.png"></a>
							</td>
						</tr>
						<tr>
							<td colspan="2" align="center" class="loginLabel">
								<hr>
							</td>
						</tr>
						<tr>
							<td colspan="2" align="center" class="googleLabel">
								Ingresar con Cuenta FlexWM:
							</td>
						</tr>
						<tr>
							<td colspan="2" align="center" class="loginLabel">
								&nbsp;
							</td>
						</tr>
						<tr>
							<td class="loginLabel">Email:&nbsp;&nbsp;</td>
							<td class="loginText">
								<input type="text" class="loginTextBox" name="email" value="<%= email %>">
							</td>
						</tr>
						<tr>
							<td class="loginLabel">Password:&nbsp;&nbsp;</td>
							<td class="loginText">
								<input type="password" class="loginTextBox" name="password">
							</td>
						</tr>
						<% if (msg != null && msg.equals("1")) { %>
						<tr>
							<td class="loginError" colspan="2" align="center">&nbsp; 
								Email / Password inv&aacute;lido. 
							</td>
						</tr>
						<% } %>
						<tr>
							<td colspan="2" align="center" class="loginLabel">
								&nbsp;
							</td>
						</tr>
						<tr>
							<td colspan="2" align="center">
								<input type="button" class="loginButton" onClick="javascript:window.location.href = 'login.jsp';" value="REGRESAR">
								<input type="submit" class="loginButton" value="LOGIN">
							</td>
						</tr>
						<tr>
							<td colspan="2" align="center" class="loginLabel">
								&nbsp;
							</td>
						</tr>
					</table>
				</form>
			</td>
		</tr>
	</table>
	<table width="100%" height="20%">
		<tr>
			<td align="center" valign="bottom" class="copyright">
				 &copy;&nbsp;<%= new java.util.Date().getYear() + 1900 %> FlexWM, 
				 <br>FlexWM Web-Based Management y el logo FlexWM son marcas registradas de 
				 <br>Ctech Consulting S.A. de C.V. y Symetria Tecnológica S.A. de C.V. 
				 <br>Todos los Derechos Reservados.
				 <br><br>
			</td>
		</tr>
	</table>
</body>
</html>

