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
	String appTitle = "SYMGF";
	String defaultCss = "login.css";
	session.setAttribute(SFParams.SFPARAMSNAME, null);
	boolean isMobile = false;
	
	BmoUser bmoUser;
	
	String email = request.getParameter("email");
	if (email == null) email = "";
	
	String msg = request.getParameter("msg");
	if (msg == null)
		msg = "";
	
	// Determina si es mobil o 
	if (SFServerUtil.isMobile(request.getHeader("user-agent"))) {
		isMobile = true;
	}
%>

<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<meta name="google-signin-client_id" content="143461298130-lv0j313lfh8ddkdf4c40ufdip9a2vco8.apps.googleusercontent.com">
<meta http-equiv="refresh" content="4;url=login.jsp" />
<link type="text/css" rel="stylesheet" href="./css/<%= defaultCss %>">

<% if (isMobile) { %>
	<meta name="viewport" content="width=320, initial-scale=1.0, maximum-scale=1">
<% } %>

<title>:::<%= appTitle %>:::
</title>

	<script src="https://apis.google.com/js/platform.js" async defer></script>
	<script type="text/javascript">
		function onSignIn() {
			signOut(); 
		}
	
		function signOut() {
			gapi.auth2.init();
			var auth2 = gapi.auth2.getAuthInstance();
			auth2.signOut().then(function() {
				 console.log('Logout de FlexWM');
			});
			
			//if (confirm('Redireccionando a pagina de Login'))
				document.location.href = 'login.jsp';
		}
	</script>
</head>

<body class="default">

<div class="g-signin2" data-onsuccess="onSignIn" style="visibility: hidden"></div>

<table width="100%" height="80%">
		<tr height="100%" valign="middle">
			<td width="100%" align="center" height="100%" valign="middle">
					<table class="loginTable">
						<tr>
							<td colspan="2" align="center">
								<a href="login.jsp"><img src="./img/main.png"></a>
							</td>
						<tr>
							<td class="googleLabel">
								<%= msg %>
								<br>Completando Salida del Sistema...
								<br>
								<br>Si no se carga FlexWM en unos momentos, haz click <a href="login.jsp">Aqui</a>.
							</td>
						</tr>
						</tr>
					</table>
				</form>
			</td>
		</tr>
	</table>

</body>
</html>

