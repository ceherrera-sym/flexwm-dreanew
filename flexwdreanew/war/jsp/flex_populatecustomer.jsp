	<%@include file="../inc/login_opt.jsp" %>
<%
	String errorLabel = request.getParameter("errorLabel");
	String errorText = request.getParameter("errorText");
	String errorException = request.getParameter("errorException");
	String programTitle = "Importacion de Clientes";
	String programDescription = "Importacion de Clientes";
%>

<html>
<head>
<title>:::Importacion de Clientes:::</title>
<link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>">
<link rel="stylesheet" type="text/css" href="../css/flexwm.css">  
<meta charset=utf-8>
</head>
<body class="default">
<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr>
		<td align="left" width="" rowspan="5" valign="top">
		    <img border="0" height="" src="<%= sFParams.getMainImageUrl() %>" >
		</td>
		<td colspan="" class="reportTitle">
		    &nbsp;<%= programTitle %>
		</td>
	</tr>
</table>


<table width="100%" height="50%" border="0" align="center"  class="detailStart">	
    <tr class="reportSubTitle">
        <td colspan="1" align="left" width="99%" height="35" class="reportCellEven">
          <li>Se recomienda elaborar preparaci&oacute;n en una hoja de calculo, seleccionar y pegar en el campo siguiente.</li>          
          <li>Formato (| = celda): Codigo | Tipo de Cliente* | Nombre Comercial | Razón Social | RFC | Titulo | Nombre* | Paterno* | Materno | Numero de Telefono | Extensión | Email | Móvil | Cargo | Vendedor | Moneda | Mercado | Referencia | Notas Referencia | CURP | IMSS | Fecha de nacimineto | Estado Civil | Ingresos | Sitio Web  </li>
        </td>
    </tr>    
    <TR>
        <td colspan="3">  
            <TABLE cellpadding="0" cellspacing="0" border="0"  width="80%">
			    <FORM action="flex_revisioncustomer.jsp" method="POST" name="listFilter">
			    <input type="hidden" name="action" value="revision">
             	<tr class="" height="35">
				    <td colspan="10" align="center">
						<textarea name="populateData" cols="150" rows="30"></textarea>
			        </td>
			    </tr>
                <tr class="">
                    <td align="center" colspan="4" height="35" valign="middle">
                        <input type="submit" value="Siguiente">
                    </td>
                </tr>			    
                </FORM>								
            </TABLE>
        </TD>
    </TR>
</TABLE>

</body>
</html>