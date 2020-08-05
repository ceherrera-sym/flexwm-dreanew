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
	
	String googleEmail = request.getParameter("googleemail");
	if (googleEmail == null) googleEmail = "";
	
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
<title>:::<%= appTitle %>:::</title>

<style type="text/css">
		#customBtn {
		  display: inline-block;
		  background: white;
		  color: #444;
		  width: 100px;
		  height: 30px;
		  vertical-align: middle;
		  padding-top: 5px;
		  border-radius: 5px;
		  border: thin solid #888;
		  box-shadow: 1px 1px 1px grey;
		  white-space: nowrap;
		  text-align: center;
		}
		#customBtn:hover {
		  cursor: pointer;
		}
		#customBtn:active {
			position: relative;
			top: 1px;
		}
		span.label {
		  font-family: serif;
		  font-weight: normal;
		}
		googleIcon {
		  background: url('./icons/google.png') transparent 5px 50% no-repeat;
		  display: inline-block;
		  vertical-align: middle;
		  text-align: center;
		  width: 42px;
		  height: 42px;
		}
		span.buttonText {
		  display: inline-block;
		  vertical-align: middle;
		  padding-left: 42px;
		  padding-right: 42px;
		  font-size: 14px;
		  font-weight: bold;
		  /* Use the Roboto font that is loaded in the <head> */
		  font-family: 'Roboto', sans-serif;
		  text-align: center;
		}
		#name {
		  margin-top: 2px;
		  font-size: 10px;
		  font-weight: bold;
		  color: green;
		  text-align: center;
		}
		flexwmIcon {
		  background: url('./icons/flexwmicon.png') transparent 5px 50% no-repeat;
		  display: inline-block;
		  vertical-align: middle;
		  width: 42px;
		  height: 42px;
		}
	</style>

	<script src="https://apis.google.com/js/api:client.js"></script>
	<script>
		var googleUser = {};
	  	var startApp = function() {
			gapi.load('auth2', function(){
				// Retrieve the singleton for the GoogleAuth library and set up the client.
				auth2 = gapi.auth2.init({
				client_id: '<%= application.getInitParameter("googlesignid") %>',
				cookiepolicy: 'single_host_origin',
				// Request scopes in addition to 'profile' and 'email'
				//scope: 'additional_scope'
				});
				attachSignin(document.getElementById('customBtn'));
			});
		  };
	
		function attachSignin(element) {
			console.log(element.id);
			auth2.attachClickHandler(element, {},
				function(googleUser) {
					document.getElementById('name').innerText = "Ingresando con " + googleUser.getBasicProfile().getName();
					document.getElementById('googleid').value = googleUser.getBasicProfile().getId();
					document.getElementById('googleemail').value = googleUser.getBasicProfile().getEmail();
					document.getElementById('loginform').submit();
				}, 
				function(error) {
					console.log(JSON.stringify(error, undefined, 2));
				}
			);
		}
		
		function signOut() {
		    var auth2 = gapi.auth2.getAuthInstance();
		    auth2.signOut().then(function () {
		    	console.log('Logout de Usuario.');
		    });
		    gapi.auth2.getAuthInstance().disconnect();
		  }
		
		function flexwmLogin() {
			window.location.href = './login_standalone.jsp';    
		}
	</script>
</head>

<body class="default">
	<table id="loginTable" width="100%" height="80%">
		<tr height="100%" valign="middle">
			<td width="100%" align="center" height="100%" valign="middle">
				<form id="loginform" action="login_post.jsp" method="POST">
					<input type="hidden" id="googleappchecksum" name="googleappchecksum" value="<%= session.getId() %>">
					<input type="hidden" id="googleid" name="googleid" value="">
					<input type="hidden" id="googleemail" name="googleemail" value="">
					<table class="loginTable">
							<td colspan="2" align="center">
								<a href="https://www.flexwm.com" target="_blank"><img src="./img/main.png"></a>
							</td>
						<tr>
							<td colspan="2" align="center" class="loginLabel">
								<hr>
							</td>
						</tr>
						<tr>
							<td colspan="2" align="center" class="loginLabel">
								&nbsp;
							</td>
						</tr>
						<tr>
							<td colspan="1" align="center" class="googleLabel">
								Ingresar con cuenta Google:
							</td>
							<td colspan="1" align="center">
								<div id="gSignInWrapper">
										<div id="customBtn" class="customGPlusSignIn">
											<img src="./icons/google.png">
										</div>
								</div>
								<script>startApp();</script>
							</td>
						</tr>
						<% if (msg != null && msg.equals("2")) { %>
						<tr>
							<td class="loginError" colspan="1" align="center">&nbsp;</td>
							<td class="loginError" colspan="1" align="center">&nbsp; 
								El Usuario <b><%= googleEmail %></b>
								<br>no está registrado en el sistema 
								<br><b>FlexWM</b>. 
							</td>
						</tr>
						<% } %>
						<tr>
							<td colspan="1" align="center" class="loginLabel">&nbsp;</td>
							<td colspan="1" align="center" class="loginLabel">
								<div id="name">&nbsp;</div>
							</td>
						</tr>
						<tr>
							<td colspan="2" align="center" class="loginLabel">
								<hr>
							</td>
						</tr>
						<tr>
							<td colspan="2" align="center" class="loginLabel">
								&nbsp;
							</td>
						</tr>
						<tr>
							<td colspan="1" align="center" class="googleLabel">
								Ingresar con Usuario/Pwd: 
							</td>
							<td colspan="1" align="center" >
								<!-- <a href="login_standalone.jsp" class="loginButton">FlexWM</a> -->
								<div id="gSignInWrapper" onClick="javascript:flexwmLogin();">
										<div id="customBtn" class="customGPlusSignIn">
											<img src="./icons/flexwmicon.png">
										</div>
								</div>
								<div id="name"></div>
							</td>
						</tr>
						<tr>
							<td colspan="2" align="center" class="loginLabel">
								&nbsp;
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

