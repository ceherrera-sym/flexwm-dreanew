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
 
<%@include file="../inc/login_opt.jsp" %>


<html>


<body>

<h1>Informacion de SFParams:</h1>

<b>DateFormat:</b> <%= sFParams.getDateFormat() %><br>
<b>DateTimeFormat:</b> <%= sFParams.getDateTimeFormat() %><br>
<b>CopyRight:</b> <%= sFParams.getCopyRight() %><br>
<b>AppUrl:</b> <%= sFParams.getAppURL() %><br>


</body>
</html>