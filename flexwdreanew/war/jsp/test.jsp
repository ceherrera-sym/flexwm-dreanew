<%@page import="com.symgae.shared.GwtUtil"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="com.symgae.shared.SFParams"%>

<%
	//Obtiene informacion del servidor y la almacena en variable de sesion
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
	System.out.println("URL del APP es: " + appUrl);
	
	
	SFParams sFParams = (SFParams)session.getAttribute(SFParams.SFPARAMSNAME);
	
	String sessionUrl = sFParams.getAppURL();
%>


<body>

	URL Aplicacion: <%= appUrl %>
	<br>
	URL Sesion: <%= sessionUrl %>

	<table width="100%" height="100%" border="1">
		<tr height="100%" valign="middle">
			<td align="center" width="100%" height="100%" valign="middle">
				<img src="<%= GwtUtil.getProperUrl(sFParams, "/img/main.png")%>">
				

			</td>
		</tr>
	</table>

</body>
