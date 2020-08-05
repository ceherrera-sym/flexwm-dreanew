<%@include file="../inc/imports.jsp" %>

<%
	String errorLabel = request.getParameter("errorLabel");
	String errorText = request.getParameter("errorText");
	String errorException = request.getParameter("errorException");
%>

<html>
<head>
<title></title>
<META HTTP-EQUIV="expires" CONTENT="0">
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=ISO-8859-1">
<link rel="stylesheet" href="../css/symgae.css">
</head>

<body class="default">

<table width="50%" height="50%" border="0" align="center"  class="detailStart">
	<tr valign="middle" align="center" class="DashTitleGroup"> 
	  <td> 
	  	<img  src="../icons/error.png" >
	  </td>
	</tr>
	<tr valign="middle" align="center" class=""> 
	  <td class="programTitle" bgcolor="white">
	  	<br>
	  	<%= errorLabel %>
	  </td>
	 </tr>
	<tr valign="middle" align="center" class=""> 
	  <td class="programSubtitle" >
	  	<%= errorText %>
	  </td>
	 </tr>
	<tr align="center" class=""> 
	  <td >&nbsp;</td>
	 </tr>
	<tr align="center" class=""> 
	  <td height="60%" align="center" class="bottomText" valign="bottom">
	  	&nbsp;Datos t&eacute;cnicos:<br>
	  	<textarea style="width: 300px; height: 100px;"><%= errorException %></textarea>
	  </td>
	 </tr>
</table>
</body>
</html>