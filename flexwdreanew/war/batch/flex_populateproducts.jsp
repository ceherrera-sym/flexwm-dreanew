<%@page import="com.flexwm.shared.op.BmoProduct"%>
<%@include file="../inc/login_opt.jsp" %>
<%
	String errorLabel = request.getParameter("errorLabel");
	String errorText = request.getParameter("errorText");
	String errorException = request.getParameter("errorException");
	String programTitle = "Importacion de Productos";
	String programDescription = programTitle;
%>

<html>
<head>
<title>:::<%= programTitle %>:::</title>
<link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>">
<link rel="stylesheet" type="text/css" href="../css/flexwm.css">  
<meta charset=utf-8>
</head>
<body class="default">
<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr>
		<td align="left" width="" rowspan="5" valign="top">
		    <img border="0" height="" width="<%=SFParams.LOGO_WIDTH%>" height="<%=SFParams.LOGO_HEIGHT%>" src="<%= sFParams.getMainImageUrl() %>" >
		</td>
		<td colspan="" class="reportTitle">
		    &nbsp;<%= programTitle %>
		</td>
	</tr>
</table>


<table width="100%" height="50%" border="0" align="center"  class="detailStart">	
    <tr class="reportSubTitle">
        <td colspan="1" align="left" width="99%" height="35" class="reportCellEven">
          <li>Se recomienda elaborar preparaci&oacute;n en archivo de texto, seleccionar y pegar en el campo siguiente.</li>          
          <li>Formato (| = tab): Clave | Nombre | Tipo | Descripcion | Marca | Modelo | N.Comercial | Familia Prod. | Grupos de Productos | Activo | Moneda | Precio Venta | Costo Compra | Precio Renta | Costo Renta | Proveedor | Rastreo | Unidad | Peso(Kg) | Largo(Mts) | Alto(Mts) | Ancho(Mts) | Amperaje 110v | Amperaje 220v   
           <%if (sFParams.isFieldEnabled(new BmoProduct().getUseCase())){%>| Case? | Cantidad por Case | Peso | Largo(Mts) | Alto(Mts) | Ancho(Mts)<%}%> </li>
        </td>
    </tr>    
    <TR>
        <td colspan="3">  
            <TABLE cellpadding="0" cellspacing="0" border="0"  width="80%">
			    <FORM action="flex_revisionproducts.jsp" method="POST" name="listFilter">
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