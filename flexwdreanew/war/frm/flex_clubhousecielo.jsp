<%@page import="com.flexwm.server.cm.*"%>
<%@page import="com.flexwm.shared.cm.*"%>
<%@page import="com.flexwm.server.ev.*"%>
<%@page import="com.flexwm.shared.ev.*"%>
<%@page import="com.flexwm.server.op.*"%>
<%@page import="com.flexwm.shared.op.*"%>
<%@page import="com.flexwm.server.co.*"%>
<%@page import="com.flexwm.shared.co.*"%>
<%@page import="com.flexwm.server.fi.*"%>
<%@page import="com.flexwm.shared.fi.*"%>
<%@page import="com.symgae.shared.sf.BmoCompany"%>
<%@page import="com.symgae.server.sf.PmCompany"%>
<%@page import="com.symgae.shared.BmFilter"%>
<%@page import="com.flexwm.shared.wf.BmoWFlowType"%>
<%@page import="com.flexwm.server.wf.PmWFlowType"%>
<%@page import="com.symgae.server.PmConn"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="java.sql.Types"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.DecimalFormat "%>
<%@page import="com.flexwm.server.AmountByWord"%>
<%@page import="com.flexwm.server.AmountByWorden"%>
<%@page import="com.flexwm.server.AmountByWordes"%>
<%@page import="com.flexwm.server.IAmountByWord"%>
<%@page import="com.flexwm.server.NumberByWord"%>

<%@page import="com.symgae.client.ui.UiParams"%>
<%@include file="../inc/login_opt.jsp" %>
<%
	String title = "Reglamento Casa Club ECR";
	
		BmoPropertySale bmoPropertySale = new BmoPropertySale(); 	
		BmoProgram bmoProgram = new BmoProgram();
		PmProgram pmProgram  = new PmProgram(sFParams);
		bmoProgram = (BmoProgram)sFParams.getBmoProgram(bmoPropertySale.getProgramCode());
%>

<html>
<% 
// Imprimir
String print = "0", permissionPrint = "";
if ((String)request.getParameter("print") != null) print = (String)request.getParameter("print");

// Exportar a Excel
String exportExcel = "0";
if ((String)request.getParameter("exportexcel") != null) exportExcel = (String)request.getParameter("exportexcel");
if (exportExcel.equals("1") && sFParams.hasPrint(bmoProgram.getCode().toString())) {
	response.setContentType("application/vnd.ms-excel");
	response.setHeader("Content-Disposition", "inline; filename=\""+title+".xls\"");
}

//Si se visualiza y no se tienen permisos, deshabilita: copiar, pegar, seleccionar, menú(clic-derecho).
//En caso de que mande a imprimir, deshabilita contenido
if(!(sFParams.hasPrint(bmoProgram.getCode().toString()))) { %>
<style> 
	body{
		user-select: none;
		-moz-user-select: none; 
		-o-user-select: none; 
		-webkit-user-select: none; 
		-ie-user-select: none; 
		-khtml-user-select:none; 
		-ms-user-select:none; 
		-webkit-touch-callout:none
	}
</style>
<style type="text/css" media="print">
    * { display: none; }
</style>
<%
permissionPrint = "oncopy='return false' oncut='return false' onpaste='return false' oncontextmenu='return false' onkeydown='return false' onselectstart='return false' ondragstart='return false'";
	//Mensaje 
	if(print.equals("1") || exportExcel.equals("1")) { %>
		<script>
			alert('No tiene permisos para imprimir/exportar el documento, el documento saldr\u00E1 en blanco');
		</script>
	<% }
}
 
%>
<head>
	<title>:::<%= title %>:::</title>
    <link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>"> 
     <link rel="stylesheet" type="text/css" href="../css/flexwm.css"> 
</head>
<body class="default" <%= permissionPrint %> style="font-size: 12px">

<%
	try {		
		
		AmountByWord amountByWord = new AmountByWord();
		amountByWord.setLanguage("es");
		amountByWord.setCurrency("es");
		
	    // Si es llamada externa, utilizar llave desencriptada
	    int propertySaleId = 0;
	    if (isExternal) propertySaleId = decryptId;
	    else propertySaleId = Integer.parseInt(request.getParameter("foreignId"));	    
	    
	    //Venta
		
		PmPropertySale pmPropertySale = new PmPropertySale(sFParams);
		bmoPropertySale = (BmoPropertySale)pmPropertySale.get(propertySaleId);
	/*
		//Pedido 
		BmoOrder bmoOrder = new BmoOrder();
		PmOrder pmOrder = new PmOrder(sFParams);
		bmoOrder = (BmoOrder)pmOrder.get(bmoPropertySale.getOrderId().toInteger());
		
		//Pedido-Inmueble		
		BmoOrderProperty bmoOrderProperty = new BmoOrderProperty();
		PmOrderProperty pmOrderProperty = new PmOrderProperty(sFParams);
		bmoOrderProperty = (BmoOrderProperty)pmOrderProperty.getBy(bmoOrder.getId(), bmoOrderProperty.getOrderId().getName());
		*/
		//Desarrollo
		BmoDevelopment bmoDevelopment = new BmoDevelopment();
		PmDevelopment pmDevelopment = new PmDevelopment(sFParams);
		bmoDevelopment= (BmoDevelopment)pmDevelopment.get(bmoPropertySale.getBmoProperty().getBmoDevelopmentBlock().getBmoDevelopmentPhase().getDevelopmentId().toInteger());
		/*
		//Cliente
		BmoCustomer bmoCustomer = new BmoCustomer();
		PmCustomer pmCustomer = new PmCustomer(sFParams);
		bmoCustomer = (BmoCustomer)pmCustomer.get(bmoPropertySale.getCustomerId().toInteger());
		*/
		
		//Empresa
		BmoCompany bmoCompany = new BmoCompany();
		PmCompany  pmCompany  = new PmCompany (sFParams);
		bmoCompany = (BmoCompany)pmCompany.get(bmoDevelopment.getCompanyId().toInteger());
		
		// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
		String logoURL = "";
		if (!bmoCompany.getLogo().toString().equals(""))
			logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
		else
			logoURL = sFParams.getMainImageUrl();

		/*
		//Ciudad de la Empresa
		BmoCity bmoCityCompany = new BmoCity();
		PmCity pmCity = new PmCity(sFParams);
		bmoCityCompany = (BmoCity)pmCity.get(bmoCompany.getCityId().toInteger());
		
		//Vivienda
		BmoProperty bmoProperty = new BmoProperty();
		PmProperty pmProperty = new PmProperty(sFParams);
		bmoProperty = (BmoProperty)pmProperty.get(bmoPropertySale.getPropertyId().toInteger());
		
		//Modelo
		BmoPropertyModel bmoPropertyModel = new BmoPropertyModel();
		PmPropertyModel pmPropertyModel = new PmPropertyModel(sFParams);
		bmoPropertyModel = (BmoPropertyModel)pmPropertyModel.get(bmoPropertySale.getBmoProperty().getPropertyModelId().toInteger());
		
		//Ciudad del Desarrollo
		BmoCity bmoCityDevelopment = new BmoCity();
		PmCity pmCityDevelopment = new PmCity(sFParams);
		bmoCityDevelopment = (BmoCity)pmCityDevelopment.get(bmoPropertyModel.getBmoDevelopment().getCityId().toInteger());
		
		//Direcciones del Cliente
		PmConn pmConnCustomer= new PmConn(sFParams);
		pmConnCustomer.open();
		boolean cuad = false;
		BmoCustomerAddress bmoCustomerAddress = new BmoCustomerAddress();
		PmCustomerAddress pmCustomerAddress = new PmCustomerAddress(sFParams);
		String sqlCustAddress = " SELECT * FROM customeraddress WHERE cuad_customerid = " + bmoPropertySale.getBmoCustomer().getId() + " ORDER BY cuad_type ASC";
		pmConnCustomer.doFetch(sqlCustAddress);
		if(pmConnCustomer.next()) cuad = true;
		if(cuad)
			bmoCustomerAddress = (BmoCustomerAddress)pmCustomerAddress.getBy(bmoPropertySale.getBmoCustomer().getId(), bmoCustomerAddress.getCustomerId().getName());
		pmConnCustomer.close();
		
		//Vendedor
		BmoUser bmoSalesUser = new BmoUser();
		PmUser pmSalesUser = new PmUser(sFParams);
		bmoSalesUser = (BmoUser)pmSalesUser.get(bmoPropertySale.getSalesUserId().toInteger());
		*/
		DecimalFormat df = new DecimalFormat("###.##");
		String nowMonth = SFServerUtil.nowToString(sFParams, "MM");

		switch (Integer.parseInt(nowMonth)) {
		case 1:
			nowMonth = "Enero";
			break;
		case 2:
			nowMonth = "Febrero";
			break;
		case 3:
			nowMonth = "Marzo";
			break;
		case 4:
			nowMonth = "Abril";
			break;
		case 5:
			nowMonth = "Mayo";
			break;
		case 6:
			nowMonth = "Junio";
			break;
		case 7:
			nowMonth = "Julio";
			break;
		case 8:
			nowMonth = "Agosto";
			break;
		case 9:
			nowMonth = "Septiembre";
			break;
		case 10:
			nowMonth = "Octubre";
			break;
		case 11:
			nowMonth = "Noviembre";
			break;
		case 12:
			nowMonth = "Diciembre";
			break;
		default:
			nowMonth = "n/d";
		}
%>
<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr>
		<td width="5%" align="left">
			<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
		</td>
		<td width="80%" class="reportTitleCell" align="justify" style="font-size: 12px">
			REGLAMENTO DE USO, CONVIVENCIA Y DISFRUTE  DE LAS &Aacute;REAS COMUNES  E  INSTALACIONES DE LA CASA CLUB DEL 
			FRACCIONAMIENTO "EL CIELO RESIDENCIAL".
			<span class="formExample"><%--= consecutive --%></span></td>
	</tr>
</table>
<!--
<p align="justify" >
<b>CONTENIDO:</b>

<ol align="justify" type="I" >
	<b>
		<li>Antecedentes</li>
		<li>Cap&iacute;tulo primero: Disposiciones generales.</li>
		<li>Cap&iacute;tulo segundo: Reglamento Cancha pasto sint&eacute;tico.</li>
		<li>Cap&iacute;tulo tercero: Reglamento de Gimnasio</li>
		<li>Cap&iacute;tulo cuarto: Reglamento Alberca, Juegos Infantiles, camastros y regaderas.</li>
		<li>Cap&iacute;tulo quinto: Reglamento sal&oacute;n de usos m&uacute;ltiples.</li>
		<li>Cap&iacute;tulo sexto: Reglamento Estacionamiento Casa Club.</li>
		<li>Cap&iacute;tulo s&eacute;ptimo: Reglamento Restricciones.</li>
	</b>
</ol>
-->
</p>

<p align="center" >
	<b>I. ANTECEDENTES</b>
</p>
<p align="justify" >
	<b>PRIMERO.-</b><br>
	<b>El FRACCIONAMIENTO EL CIELO RESIDENCIAL</b>, Es un n&uacute;cleo residencial formado por unidades privativas, para casas unifamiliares, 
	que cuenta con instalaciones funcionales, teniendo como finalidad el dotar a sus moradores de un entorno privado que ofrece tranquilidad y seguridad, 
	producto de una armoniosa convivencia familiar y social, el cual tiene su domicilio en el Ciudad de Le&oacute;n, Guanajuato.
</p>

<p align="justify" >
	<b>SEGUNDO.-</b><br>
	El desarrollo habitacional se integra de viviendas o lotes de terreno, vialidades de uso com&uacute;n, caseta de ingreso, &aacute;reas verdes, Casa club.
</p>

<p align="justify" >
	<b>TERCERO.- </b><br>
	Todos los colonos  tienen los derechos y obligaciones que en el propio instrumento se se&ntilde;alan, estableci&eacute;ndose asimismo que la Casa Club 
	ser&aacute; operada y administrada por un "Consejo de Administraci&oacute;n", concedi&eacute;ndole determinadas facultades y obligaciones, 
	y en ejercicio de las mismas expide el presente reglamento de uso y disfrute de las &aacute;reas comunes e instalaciones de la Casa Club.
</p>

<p align="justify" >
	<b> CUARTO.-</b><br>
	El presente reglamento tiene por objeto establecer las normas para el buen uso, funcionamiento y mantenimiento de las &aacute;reas comunes e 
	instalaciones de la Casa Club que se encuentra  dentro del fraccionamiento denominado "EL CIELO RESIDENCIAL", la forma de administrar y operar 
	la misma y las restricciones de los colonos,  en la forma y t&eacute;rminos que se establecen en los cap&iacute;tulos y art&iacute;culos siguientes.
</p>
<br>
<p align="center" >
	<b>
		CAP&Iacute;TULO PRIMERO<br>
		II.  Disposiciones Generales 
	</b>
</p>	

<p align="justify" >
	<b>ART&Iacute;CULO PRIMERO. -.USO Y RESPONSABILIDAD EN CASO DE ACCIDENTES.</b><br>
	Los colonos al corriente de sus cuotas, y sus familiares directos, tendr&aacute;n derecho a usar y disfrutar de las instalaciones de la CASA CLUB, 
	sujet&aacute;ndose a los horarios y normas establecidas en el presente reglamento. El uso de la CASA CLUB, es por cuenta y riesgo de los colonos y 
	los usuarios, por lo que la administraci&oacute;n no se hace responsable de los accidentes, robos o cualquier otro acontecimiento que les ocasione 
	da&ntilde;os o perjuicios. La Casa Club no se hace responsable por accidentes ocurridos dentro de cualquiera de sus &aacute;reas, ya que toda 
	actividad ser&aacute; bajo cuenta y riesgo de los Colonos  y los usuarios.
</p>


<p align="justify" >
	<b>ART&Iacute;CULO SEGUNDO.-IDENTIFICACI&Oacute;N.</b><br>
	Para poder hacer uso de la CASA CLUB, los colonos deber&aacute;n mostrar la identificaci&oacute;n que les ser&aacute; proporcionada por la 
	administraci&oacute;n, la cual especificar&aacute; el n&uacute;mero de la unidad privativa o lote del cual es propietario, la cual ser&aacute; 
	personal e intransferible, en el caso de p&eacute;rdida o robo, deber&aacute; ser reportada a la administraci&oacute;n y la reposici&oacute;n 
	tendr&aacute; un costo econ&oacute;mico, de acuerdo a las actualizaciones que en ese momento existan.
	<br><br>
	El personal de vigilancia, est&aacute; facultado para solicitar la exhibici&oacute;n de la identificaci&oacute;n a cualquier persona que use las 
	instalaciones de la CASA CLUB. Si por cualquier motivo el colono no tenga en su poder su identificaci&oacute;n, deber&aacute; dar su n&uacute;mero 
	de la unidad privativa o lote de terreno y el personal de vigilancia a su vez lo cotejara en la administraci&oacute;n.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO TERCERO.- FAMILIARES DIRECTOS.</b><br>
	Se entiende por familiares directos del colono, los miembros de la familia que vivan permanentemente en las unidades privativas de su propiedad entre 
	otros esposo(a), hijos, padres, hermanos, etc.; en el caso de que el colono no resida en su unidad privativa  y la tenga vac&iacute;a &uacute;nicamente 
	tendr&aacute;n derecho a designar cuatro familiares  mayores de edad directos.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO CUARTO.- INVITADOS.</b><br>
	La modalidad de invitados para el uso de las instalaciones de la Casa Club en El Cielo Residencial no est&aacute; permitido.
	<br>
	Esto es en beneficio de los colonos del fraccionamiento y con acceso a la Casa Club, para disfrute de los equipamientos y 
	amenidades que para su beneficio han sido provistos.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO QUINTO.- PROHIBICIONES.</b><br>
	Queda estrictamente prohibido a los colonos:
</p>

<ol type="a"  style="text-align:justify;">
	<li>
		Introducir dentro de las instalaciones de la CASA CLUB, cualquier clase de animales o mascotas. Fuera de la CASA CLUB y dentro del &aacute;rea 
		com&uacute;n &uacute;nicamente los animales dom&eacute;sticos podr&aacute;n ser conducidos por su propietario, con la correa correspondiente 
		y adem&aacute;s ser responsables de recoger las suciedades y/o excrementos de sus respectivas mascotas. En caso de incurrir en este punto 
		ser&aacute; sancionado de no entrar a la Casa Club por un mes y si existe la reincidencia sobre esta misma acci&oacute;n, ser&aacute; 
		sancionado con la cancelaci&oacute;n permanente a la entrada de la Casa Club.
	</li>
	<li>
		Entrar al Sal&oacute;n de Usos M&uacute;ltiples y permanecer en traje de ba&ntilde;o y caminar descalzo fuera del &aacute;rea de albercas, 
		ba&ntilde;os o vestidores.
	</li>
	<li>
		Jugar o practicar alg&uacute;n deporte, sin la ropa adecuada para el ejercicio de dicho deporte.
	</li>
	<li>
		Introducir a las instalaciones de la CASA CLUB, cualquier tipo de bebida o comida por su cuenta.
	</li>
	<li>
		Estacionar veh&iacute;culos sin respetar los espacios se&ntilde;alados en el &aacute;rea de estacionamiento, y/ o dejarlos en el estacionamiento 
		de la CASA CLUB.
	</li>
	<li>
		Introducir a la CASA CLUB, m&uacute;sicos o artistas de cualquier clase.
	</li>
	<li>
		Tirar basura o desperdicios fuera de los lugares se&ntilde;alados para dicho objeto.
	</li>
	<li>
		Introducir a la CASA CLUB, cualquier tipo de explosivos o armas de cualquier tipo (rifles, pistolas de aire, di&aacute;bolos o municiones), 
		sustancias inflamables, t&oacute;xicas o peligrosas.
	</li>
	<li>
		Introducir a la CASA CLUB, cualquier tipo de drogas, enervantes o estupefacientes y cigarros (Casa Club es una &aacute;rea libre de humo).
	</li>
	<li>
		Inducir Y/O PROPORCIONAR  bebidas embriagantes a los empleados de la Casa Club el Cielo Residencial.
	</li>
	<li>
		Realizar cualquier acto contrario a la moral y las buenas costumbres, o a este reglamento.
	</li>
	<li>
		Organizar ventas o vendimias, rifas, fijar anuncios o propaganda dentro de la CASA CLUB, o utilizar sus instalaciones para exhibir productos u 
		objetos de comercio, para su venta.	
	</li>
	<li>
		Introducir y conducir a la CASA CLUB bicicletas, motocicletas, triciclos, patines, patinetas avalanchas o similares.
	</li>
	<li>
		Introducir y operar en la CASA CLUB, equipos de sonido de cualquier tipo.
	</li>
	<li>
		Concertar cualquier tipo de apuestas o juegos de azar. 
	</li>
	<li>
		Proferir palabras altisonantes y conducta ruidosa o escandalosa.
	</li>
	<li>
		Dar instrucci&oacute;n de cualquier disciplina con fines lucrativos.
	</li>
	<li>
		Tomar dentro de la Casa Club cualquier tipo de l&iacute;quidos en envases, vasos o recipientes de cristal.
	</li>
	<li>
		Toda la Casa Club es libre de humo. (No cigarrillos, Cerillos o cualquier otro art&iacute;culo que produzca humo)
	</li>
</ol>

<br>
<p align="center" >
	<b>
		CAP&Iacute;TULO SEGUNDO <br>
		II. Reglamento Cancha Pasto Sint&eacute;tico.
	</b>
</p>	
	
<p align="justify" >
	<b>ART&Iacute;CULO SEXTO.- </b><br>
	<b>Para el uso de las instalaciones deportivas,</b>
	los colonos deber&aacute;n hacerlo con zapatos y ropa apropiada para este deporte. No hay reservaciones, por lo que una vez completos los jugadores, 
	podr&aacute;n utilizar la cancha con el procedimiento que se indica a continuaci&oacute;n, y podr&aacute;n continuar jugando siempre que no hubiere 
	colonos en espera.
	<br><br>
	El procedimiento que se observar&aacute; ser&aacute; el siguiente: 
</p>
	 
<ol type="1"  style="text-align:justify;">
	<li>
		En los fines de semana y d&iacute;as festivos o en momentos de mayor afluencia, estar&aacute; sujeta a disponibilidad.
	</li>
	<li>
		Los interesados deber&aacute;n presentarse con su credencial en el &aacute;rea de control para pedir el uso y horario de la cancha.
	</li>
	<li>
		No se podr&aacute; asignar cancha por anticipado, s&oacute;lo ser&aacute;n asignadas canchas y horarios a las personas que se presenten al 
		&aacute;rea de control. (No se separar&aacute;n previamente tiempos para uso de las canchas).
	</li>
	<li>
		Los juegos tendr&aacute;n una duraci&oacute;n m&aacute;xima de una hora, si alguien est&aacute; esperando cancha.
	</li>
	<li>
		Solo se permitir&aacute; de martes a viernes y no exceder&aacute; de una hora, concluido ese tiempo se podr&aacute; anotar de nueva cuenta 
		para solicitar asignaci&oacute;n de cancha de acuerdo al horario que determine la administraci&oacute;n.
	</li>
	<li>
		En los fines de semana y d&iacute;as festivos o en momentos de mayor afluencia, estar&aacute; sujeta a disponibilidad.
	</li>
	<li>
		La administraci&oacute;n se reserva el derecho de poner fuera de servicio la cancha para su mantenimiento.
	</li>
</ol>

<p align="justify" >
	<b>ART&Iacute;CULO S&Eacute;PTIMO.- Para el uso de la cancha</b> se cumplir&aacute;n las siguientes normas:
</p>

<ol type="a"  style="text-align:justify;">
	<li>
		El encargado del &aacute;rea de control se negar&aacute; a dar cancha al usuario que no se encuentre al corriente en su pago de cuota de 
		mantenimiento, debiendo reportar &eacute;sta anomal&iacute;a a la administraci&oacute;n.
	</li>
	<li>
		Podr&aacute; ser utilizada por cualquier usuario mayor de quince a&ntilde;os, o bien, menores de edad acompa&ntilde;ados por una persona mayor, 
		solicit&aacute;ndolas en el &aacute;rea de control, donde personal autorizado conceder&aacute; el permiso para su uso, en el cual indicar&aacute; 
		el tiempo de utilizaci&oacute;n.
	</li>
	<li>
		No se permitir&aacute; la presencia en la cancha a aquellas personas que no sean participantes en el juego. Los espectadores deber&aacute;n estar 
		fuera del &aacute;rea de la cancha donde no se obstaculice el libre tr&aacute;nsito de peatones. Se deber&aacute; cuidar el buen comportamiento 
		tanto de los participantes como de los espectadores y guardar orden y disciplina.
	</li>
	<li>
		No se permite masticar chicle, ni comer,  ni fumar en la zona de cancha y pasto.
	</li>
	<li>
		S&oacute;lo se permitir&aacute; introducir a las cancha agua en recipientes desechables o pl&aacute;sticos.
	</li>
	<li>
		En ning&uacute;n caso se permitir&aacute; el uso de la cancha a personas que no tengan el equipo reglamentario:
		<ol type="a"  style="text-align:justify;">
			<li>
				Tenis antiderrapantes  o zapatos de f&uacute;tbol. No se permite el uso de suela negra o de color.
			</li>
			<li>
				Ropa adecuada para dicha actividad. (prohibido jugar sin camiseta)
			</li>
		</ol>
	</li>
	<li>
		Demostrar mala conducta deportiva.
	</li>
	<li>
		No se pueden realizar apuestas por los colonos que utilicen la cancha.
	</li>
	<li>
		Queda prohibido efectuar acciones que deterioren la cancha e instalaciones y pongan en riesgo la seguridad f&iacute;sica de los usuarios.
	</li>
	<li>
		Queda prohibido pelotear contra las alambradas.
	</li>
	<li>
		En el caso que utilicen el servicio de iluminaci&oacute;n, ser&aacute; cargado a raz&oacute;n de la cuota establecida por la administraci&oacute;n. 
		El uso de la cancha en horarios nocturnos deber&aacute; ser cubierto por parte de los usuarios, con una cuota por uso de alumbrado. 
		El costo de la iluminaci&oacute;n de la cancha ser&aacute; a cargo de los usuarios  siempre.
	</li>
	<li>
		No se permite la entrada a personas en estado inconveniente; que hayan ingerido bebidas embriagantes o utilizado sustancias t&oacute;xicas; 
		que se encuentren bajo la influencia de alg&uacute;n medicamento, en cuyo caso, el club no se hace responsable de alg&uacute;n accidente ocurrido dentro de la cancha. 
	</li>
	<li>
		A los colonos y los usuarios les queda terminantemente prohibido reprender o abusar del personal del &aacute;rea de control y vigilancia. 
		Toda queja por irregularidades que &eacute;stos cometan deber&aacute; formularse a la administraci&oacute;n.
	</li>
	<li>
		Todos los usuarios deber&aacute;n observar el presente Reglamento, as&iacute; como el C&oacute;digo de Conducta establecido 
		por la administraci&oacute;n, en caso contrario, ser&aacute;n reportados al Consejo de administraci&oacute;n para la sanci&oacute;n correspondiente. 
		La persona que no cumpla con este C&oacute;digo, deber&aacute; abandonar la cancha al notific&aacute;rsele la infracci&oacute;n o 
		suspensi&oacute;n definitiva, nos reservamos este derecho.
	</li>
</ol>

<p align="justify" >
	<b>ART&Iacute;CULO OCTAVO.- Queda estrictamente prohibido el uso de:</b><br>
</p>
<ol type="a"  style="text-align:justify;">
	<li>
		Ropa que no sea apropiada para el uso de la cancha.
	</li>
	<li>
		Calz&oacute;n o traje de ba&ntilde;o.
	</li>
	<li>
		Zapatos de tac&oacute;n o de suela de cuero, negra o de color.
	</li>
	<li>
		Jugar sin camiseta deportiva. 
	</li>
</ol>
<br>
<p align="center" >
	<b>
		CAP&Iacute;TULO TERCERO <br>
		IV. Reglamento Gimnasio.
	</b>
</p>

<p align="justify" >
	<b>ART&Iacute;CULO NOVENO.- Para el uso del Gimnasio</b>
	los colonos, deber&aacute;n hacerlo con equipo, zapatos y ropa apropiada para este deporte. No hay reservaciones. Los da&ntilde;os causados 
	por el mal uso, fuera de uso normal, ser&aacute;n pagados por el responsable del da&ntilde;o. Los colonos mayores de 17 a&ntilde;os, 
	podr&aacute;n utilizar los equipos y aparatos del gimnasio, dentro del horario establecido por la administraci&oacute;n y con un l&iacute;mite 
	de 20 minutos, y podr&aacute;n continuar haci&eacute;ndolo, siempre que no hubiere colonos en espera. No se permite fumar dentro del gimnasio, 
	introducir maletas, raquetas, alimentos y bebidas, excepto agua y/o bebidas hidratantes en recipientes de pl&aacute;stico solamente. 
	No se deber&aacute; permanecer dentro del gimnasio con ropa de vestir.
</p>
	
<ol type="1"  style="text-align:justify;">
	<li>
		Para poder acceder al uso del gimnasio los colonos, deber&aacute;n registrar su entrada y acudir con toalla de mano.
	</li>
	<li>
		La permanencia en el gimnasio debe estar supervisada por el responsable de la casa Club o vigilante en turno.
	</li>
	<li>
		Todos los usuarios del gimnasio tienen la obligaci&oacute;n de ubicar discos, mancuernas y pines en el lugar donde les corresponda y al 
		terminar de usar los aparatos, limpiarlos con el producto y franelas a disposici&oacute;n de los mismos dentro del gimnasio.
	</li>
	<li>
		El cupo m&aacute;ximo para usar el gimnasio ser&aacute; de 6 personas, y se alternar&aacute;n en los equipos.
	</li>
	<li>
		Queda prohibido adicionar peso extra al equipo utilizado; solo se permite utilizar el que tiene indicado como capacidad m&aacute;xima.
	</li>
	<li>
		Los colonos, menores de 16 a&ntilde;os m&iacute;nimo pueden utilizar el gimnasio &uacute;nicamente en compa&ntilde;&iacute;a de una persona adulta, 
		que supervise el correcto uso de los aparatos.
	</li>
	<li>
		Cualquier da&ntilde;o, ruptura o abuso en los equipos e instalaciones, ser&aacute; pagado por la persona que caus&oacute; el da&ntilde;o, en caso 
		de ser ni&ntilde;o(a) o invitado, se le notificar&aacute; al colono que tendr&aacute; que cubrir el da&ntilde;o al costo determinado por la 
		administraci&oacute;n dentro de las 24 horas siguientes a su notificaci&oacute;n, neg&aacute;ndole el acceso al &aacute;rea en tanto el pago 
		requerido no sea cubierto, en caso de retraso en el pago, causar&aacute; un intereses moratorios del 10% mensual acumulable hasta su liquidaci&oacute;n.
	</li>
	<li>
		Est&aacute; prohibido realizar actividad f&iacute;sica con el torso desnudo o sin calzado deportivo.
	</li>
	<li>
		Est&aacute; prohibido subirse a las cintas con el calzado sucio, y por seguridad revisar los cordones de su calzado se encuentren atados.
	</li>
	<li>
		Los turnos para el uso de las cintas ser&aacute;n de 15 minutos cuando &eacute;stas sean requeridas.
	</li>
	<li>
		Los turnos para el uso de las bicicletas ser&aacute;n de 20 minutos cuando &eacute;stas sean requeridas.
	</li>
	<li>
		Est&aacute; prohibido ingerir alimentos y fumar dentro del gimnasio.
	</li>
	<li>
		Es obligatorio secar la transpiraci&oacute;n que hubiera quedado en los tapizados de cada aparato que usted haya usado.
	</li>
	<li>
		Siempre tendr&aacute; que traer y usar su toalla para su  aseo personal.
	</li>
</ol>
<br>
<p align="center" >
	<b>
		CAP&Iacute;TULO CUARTO <br>
		V. Reglamento Alberca, Juegos Infantiles, Sombrillas, Camastros, Ba&ntilde;os y Regaderas.
	</b>
</p>

<p align="justify" >
	<b>ART&Iacute;CULO D&Eacute;CIMO.- Para el uso de la Alberca</b>
	los colonos, familiares e invitados deber&aacute;n hacerlo con equipo, zapatos y ropa apropiada para este deporte, dentro del horario que se 
	determine por la administraci&oacute;n. No hay reservaciones. Los da&ntilde;os causados por el mal uso, fuera de uso normal, ser&aacute;n pagados 
	por el responsable del da&ntilde;o.
</p>
	
<p align="justify" >
	<b>ART&Iacute;CULO  D&Eacute;CIMO PRIMERO.- </b><br>
	Los menores deber&aacute;n estar siempre al cuidado de un adulto. Solo se autoriza el uso de juegos infantiles a ni&ntilde;os mayores 2 a&ntilde;os. 
	No se permite realizar juegos que molesten o pongan en peligro a los dem&aacute;s, ni la utilizaci&oacute;n de lanchas inflables u otro tipo de 
	juguetes que estorben el uso libre de la alberca.
</p>
	
<p align="justify" >
	<b>ART&Iacute;CULO D&Eacute;CIMO SEGUNDO.- </b><br>
	Para su uso se deber&aacute;n cumplir las siguientes reglas:
</p>
	
<ol type="a"  style="text-align:justify;">
	<li>
		La alberca es de uso exclusivo de los colonos, esposa, hijos, padres que habitan en el fraccionamiento, previamente identificados en la 
		Administraci&oacute;n con las credenciales que se expedir&aacute;n  a los colonos del fraccionamiento y que est&eacute;n al corriente
		en el pago de las cuotas del mantenimiento que determine el Consejo Directivo.
	</li>
	<li>
		La alberca se utilizar&aacute; en el horario y los d&iacute;as que determine el Consejo Directivo, en la inteligencia que el propio 
		Consejo Directivo o la Gerencia podr&aacute;n determinar los d&iacute;as en que la alberca no deber&aacute; ser usada por reparaciones y/o mantenimiento.
	</li>
	<li>
		El personal de servicio dom&eacute;stico, por parte de los colonos, no podr&aacute; hacer uso de las instalaciones deportivas del club, de la 
		alberca y gimnasio.
	</li>
</ol>

<p align="justify" >
	<b>ART&Iacute;CULO D&Eacute;CIMO TERCERO.-</b><br>
	Para utilizar la alberca los usuarios deber&aacute;n cumplir los siguientes requisitos:
</p>

<ol type="1"  style="text-align:justify;">
	<li>
		Es indispensable que las personas mayores a 15 a&ntilde;os,  tomen un ba&ntilde;o de regadera antes de hacer uso de la alberca.
	</li>
	<li>
		Los usuarios de la alberca tendr&aacute;n especial empe&ntilde;o en cuidar la moral y las buenas costumbres.
	</li>
	<li>
		Los usuarios al transitar de los vestidores a la alberca en traje de ba&ntilde;o, utilizar&aacute;n bata o alguna otra prenda sobre &eacute;ste.
	</li>
	<li>
		No se permite realizar juegos que molesten o pongan en peligro a los dem&aacute;s, ni el uso de lanchas u otro tipo de juguetes que estorben 
		el uso libre de la alberca.
	</li>
	<li>
		Los menores de 10 a&ntilde;os de edad, deber&aacute;n de estar siempre acompa&ntilde;ados por una persona mayor al hacer uso de la alberca.
	</li>
	<li>
		No est&aacute; permitido usar salvavidas inflables ya que estos pueden provocar un accidente y poner en riesgo la vida del ni&ntilde;o, 
		s&oacute;lo se permitir&aacute;n los brazaletes.
	</li>
	<li>
		Est&aacute; prohibido comer y/o tomar dentro de la alberca.
	</li>
	<li>
		No est&aacute; permitido llevar al &aacute;rea de la alberca  y a ninguna otra &aacute;rea de la Casa Club botellas, vasos y cualquier otro 
		objeto de vidrio, que al romperse pueda causar un accidente.
	</li>
	<li>
		Est&aacute; prohibido tirar basura en el &aacute;rea de la alberca y tender toallas o cualquier otra prenda de vestir.
	</li>
	<li>
		No est&aacute; permitido nadar cuando haya tormentas el&eacute;ctricas.
	</li>
	<li>
		Est&aacute; prohibido permanecer en traje de ba&ntilde;o y caminar descalzo fuera del &aacute;rea de albercas, ba&ntilde;os y vestidores.
	</li>
	<li>
		Est&aacute; prohibido arrojarse con ropa a la alberca.
	</li>
	<li>
		Queda prohibido entrar a la alberca con el cuerpo untado de crema, aceites o cualquier sustancia que contamine el agua, en caso de hacerlo 
		ser&aacute; sujeto a la sanci&oacute;n que determine la Administraci&oacute;n.
	</li>
	<li>
		De conformidad con las reglas que establezca el Consejo Directivo, los usuarios de la alberca deber&aacute;n practicarse los ex&aacute;menes 
		m&eacute;dicos que el propio colono determine.
	</li>
	<li>
		Los usuarios de la alberca deber&aacute;n acatar las indicaciones que les hagan el personal de la Casa Club, en relaci&oacute;n con su 
		comportamiento, as&iacute; como en aquellas situaciones que puedan afectar su seguridad o la de los dem&aacute;s usuarios.
	</li>
	<li>
		No podr&aacute;n hacer uso de la alberca las personas que sufran de infecciones en la piel, ojos, catarro, o&iacute;dos o cualquier otra 
		enfermedad, as&iacute; como los que tengan heridas abiertas o vendajes en el cuerpo y que ponga en riesgo la salud de otros usuarios.
		</li>
	<li>
		El ingreso a la alberca ser&aacute; por cuenta y riesgo de los usuarios y sus familias.
	</li>
	<li>
		El club no tiene obligaci&oacute;n de tener salvavidas.
	</li>
	<li>
		El horario establecido para el uso de la alberca es de 8:00 AM. A 8:00 P.M.
	</li>
	<li>
		Solo se permite el uso de la alberca con traje de ba&ntilde;o, playeras y zapatos especiales para alberca.  No se permite entrar a la alberca 
		con otro tipo de ropa o zapatos.
	</li>
	<li>
		Queda prohibido Fumar, Beber, o Comer cerca y dentro de la alberca.
	</li>
	<li>
		Los menores de 2 a&ntilde;os deber&aacute;n usar pa&ntilde;al especial, en caso de NO hacerlo ser&aacute; sujeto a la sanci&oacute;n que 
		determine la Administraci&oacute;n.
	</li>
	<li>
		El uso de la alberca y la integridad f&iacute;sica es responsabilidad de quien disponga de la misma, por lo tanto la administraci&oacute;n 
		se deslindan de cualquier responsabilidad.
	</li>
	<li>
		La profundidad m&aacute;xima de la alberca es de 1.40 Metros; por lo que no se podr&aacute;n realizar clavados en la alberca, para evitar 
		accidentes, siendo responsabilidad de quien lo haga.
	</li>
	<li>
		No se permite el acceso a personas que hayan ingerido bebidas embriagantes, utilizando sustancias t&oacute;xicas o que se encuentren bajo 
		la influencia de alg&uacute;n medicamento.
	</li>
	<li>
		En el caso de llevar mobiliario o juguetes al &aacute;rea de alberca, los mencionados deber&aacute;n ser especiales para alberca y ser 
		retirados cuando abandonen las instalaciones.  En caso de dejarlos, ser&aacute;n considerados desperdicio y depositados en la basura sin ninguna 
		responsabilidad para la administraci&oacute;n de la casa Club.
	</li>
	<li>
		Queda estrictamente prohibido correr alrededor de la alberca para evitar accidentes o tener comportamiento indebido que perturbe la 
		tranquilidad de los usuarios.
	</li>
	<li>
		Queda estrictamente prohibido ingresar con mascotas al &aacute;rea de la alberca.
	</li>
	<li>
		Queda estrictamente prohibido colocar aparatos el&eacute;ctricos cerca de la alberca. 
	</li>
	<li>
		Queda prohibido portar artefactos punzo cortantes en el &aacute;rea de la alberca.
	</li>
	<li>
		La administraci&oacute;n no se hace responsable de cualquier objeto personal o de valor que sea olvidado dentro  del &aacute;rea de la alberca.	</li>
	<li>
		Est&aacute; prohibido bloquear las salidas de agua de la alberca con cualquier tipo de objeto.
	</li>
	<li>
		Al mobiliario de la alberca deber&aacute; darse buen uso para que se conserve en buen estado y deber&aacute;n de permanecer en dicha 
		&aacute;rea, en caso de haber sido movido deber&aacute; ser regresado al lugar donde fue encontrado.
	</li>
	<li>
		No se permite deambular descalzo o correr y sin playera fuera de la zona de la alberca.	</li>
	<li>
		Cualquier da&ntilde;o, ruptura o abuso en los equipos e instalaciones, ser&aacute; pagado por la persona que caus&oacute; el da&ntilde;o, 
		en caso de ser ni&ntilde;o(a), se le notificar&aacute; al colono que tendr&aacute; que cubrir el da&ntilde;o, al costo determinado por la 
		administraci&oacute;n dentro de las 24 horas siguientes a su notificaci&oacute;n y se le negar&aacute; el acceso al &aacute;rea en tanto el 
		pago requerido no sea cubierto, en caso de retraso en el pago, causar&aacute; un inter&eacute;s moratorio del 10% mensual acumulable hasta su 
		liquidaci&oacute;n.
	</li>
	<li>
		Al t&eacute;rmino del horario establecido, el personal de seguridad avisar&aacute; con anticipaci&oacute;n al colono que estuviere haciendo 
		uso de las instalaciones, para que se retire, se cierren las &aacute;reas y se apaguen las luces de acuerdo al horario de uso de la alberca.
	</li>
	<li>
		Solo el personal autorizado puede hacer uso del equipo de operaci&oacute;n y mantenimiento de la alberca y entrar al &aacute;rea de 
		m&aacute;quinas, queda restringida la entrada a los colonos a dicha &aacute;rea.
	</li>
	<li>
		La alberca deber&aacute; usarse conforme a la moral y las buenas costumbres de manera ordenada y tranquila, no podr&aacute; efectuarse acto 
		alguno que perturbe la tranquilidad de los dem&aacute;s colonos ni utilizar lenguaje altisonante.
	</li>
	<li>
		Finalmente se solicita respeto al personal que labora en Casa Club ya que est&aacute;n realizando su trabajo.
	</li>
</ol>

<p align="justify" >
	<b>ART&Iacute;CULO D&Eacute;CIMO CUARTO.- BA&Ntilde;OS, VESTIDORES, REGADERAS.-</b><br>
	El uso de estas &aacute;reas es exclusivamente para personas mayores de 16 a&ntilde;os, y los menores deber&aacute;n invariablemente ser 
	acompa&ntilde;ados por un adulto. Queda prohibido: introducir y consumir bebidas y alimentos en estas &aacute;reas  fumar en el interior de 
	ba&ntilde;os y vestidores. Usar jabones, aceites, mascarillas, tintes para el cabello. Jabonarse fuera del &aacute;rea de las regaderas. 
	Queda prohibido utilizar los ba&ntilde;os, vestidores y albercas sufriendo alguna enfermedad contagiosa que ponga en peligro la salud del 
	resto de los colonos.
</p>
<br>
<p align="center" >
	<b>
		CAP&Iacute;TULO QUINTO <br>
		VI. Reglamento Sal&oacute;n Usos M&uacute;ltiples.
	</b>
</p>

<p align="justify" >
	<b>ART&Iacute;CULO D&Eacute;CIMO QUINTO.- Para el uso del Sal&oacute;n de usos M&uacute;ltiples,</b>
	los colonos, esposa, hijos, padres que vivan en la casa que adquirieron en el fraccionamiento, deber&aacute;n hacerlo apropiadamente, 
	dentro del horario que se determine por la administraci&oacute;n. No hay reservaciones. Los da&ntilde;os causados por el mal uso, 
	ser&aacute;n pagados por el responsable del da&ntilde;o.
</p>
	
<p align="justify" >
	<b>ART&Iacute;CULO D&Eacute;CIMO SEXTO.-</b><br>
	No hay reservaciones, y se podr&aacute; hacer uso de las instalaciones de manera responsable.
</p>
	
<ol type="1"  style="text-align:justify;">
	<li>
		En t&eacute;rminos generales, no se permiten envases de vidrio dentro de todas las instalaciones de la Casa Club, y los alimentos que 
		se ingieran dentro del sal&oacute;n de eventos y &aacute;reas permitidas, tendr&aacute;n que estar previamente preparados y almacenados, 
		y despu&eacute;s de haber sido ingeridos, toda la basura y / o desperdicios tendr&aacute;n que depositarse en los contenedores correspondientes.
	</li>
	<li>
		Su uso es exclusivo para los colonos, por lo que queda prohibido prestarse para eventos particulares  de los mismos colonos.
	</li>
	<li>
		El aforo m&aacute;ximo para utilizar en la sal&oacute;n de usos m&uacute;ltiples es de 60 personas incluyendo ni&ntilde;os.
	</li>
	<li>
		No se permite el uso de aparatos de sonido dentro del &aacute;rea de la casa club, que moleste a los dem&aacute;s usuarios; en caso de 
		utilizarlos, se deber&aacute; contar con los correspondientes aud&iacute;fonos para su uso personal.
	</li>
</ol>

<p align="justify" >
	<b>ART&Iacute;CULO D&Eacute;CIMO S&Eacute;PTIMO.-ALIMENTOS Y BEBIDAS</b><br>
	No se servir&aacute;n alimentos en las &aacute;reas de  cancha, ba&ntilde;os o alberca ni a personas en traje de ba&ntilde;o. Debiendo vestir 
	adecuadamente en cada &aacute;rea.
	 <br><br>
	 Los colonos que tienen adeudos en sus cuotas de mantenimiento u otros conceptos, no tienen derecho al uso de estas &aacute;reas comunes.  
	 Esto incluye a los colonos o los inquilinos de las viviendas, por lo que se les pedir&aacute; regularizarse o en su defecto retirarse de 
	 estas &aacute;reas.
	 <br><br>
	 Los inquilinos de las viviendas o colonos que no acaten el reglamento dependiendo de la gravedad de la falta ser&aacute;n exhortados a 
	 retirarse y se les prohibir&aacute; el acceso a las instalaciones.
	 <br><br>
	 Este reglamento tiene por objeto lograr la sana convivencia mediante el uso adecuado de las instalaciones; que son de los colonos y 
	 fueron creadas para el esparcimiento, goce y disfrute de los mismos. Y para  cualquier situaci&oacute;n no prevista en este reglamento 
	 se regular&aacute; a criterio de la administraci&oacute;n, quien tomar&aacute; las medidas necesarias para que se logre lo aqu&iacute; mencionado. 
</p>
<br>
<p align="center" >
	<b>
	 	CAP&Iacute;TULO  SEXTO <br>
		VII. Reglamento Estacionamiento Casa Club. 
	</b>
</p>

<p align="justify" >
	<b>ART&Iacute;CULO D&Eacute;CIMO OCTAVO.- Para el uso del Estacionamiento</b>,
	los colonos  podr&aacute;n hacer uso del mismo, siempre y cuando est&eacute;n haciendo uso de las Instalaciones de la Casa Club y al 
	corriente en sus pagos de la Cuota de Mantenimiento.
</p>
	
<p align="justify" >
	<b>ART&Iacute;CULO D&Eacute;CIMO NOVENO.- </b><br>
	Para su uso se deber&aacute; cumplir las siguientes reglas: 
</p>
	
<ol type="a"  style="text-align:justify;">
	<li>
		Los colonos deber&aacute;n utilizar como estacionamiento los lugares expresamente delimitados en bater&iacute;a y sin invadir las 
		v&iacute;as de acceso.
	</li>
	<li>
		El club no se hace responsable de los objetos que est&eacute;n en el interior de los veh&iacute;culos, ni del robo del mismo o de los 
		da&ntilde;os que le puedan ser causados por terceros.
	</li>
	<li>
		La velocidad m&aacute;xima de circulaci&oacute;n no ser&aacute; mayor de 20 Km. y se dar&aacute; en todo momento la preferencia a los peatones.
	</li>
	<li>
		Los usuarios que lleguen al club en motocicleta las estacionar&aacute;n en los mismos lugares destinados a los veh&iacute;culos.
	</li>
	<li>
		Queda prohibido dejar los veh&iacute;culos en el estacionamiento del Club sino est&aacute; haciendo uso de las instalaciones de la Casa Club.
	</li>
</ol>
<br>
<p align="center" >
<b>
 	CAP&Iacute;TULO  S&Eacute;PTIMO <br>
 	VIII. Reglamento Restricciones.
</b>
</p>

<p align="justify" >
	<b>ART&Iacute;CULO VIG&Eacute;SIMO.-</b><br>
	Los colonos  est&aacute;n obligados a pagar puntualmente las cuotas, consumos y cualquier otro cargo a su cuenta en las fechas, montos y 
	t&eacute;rminos que se establezcan para el uso y goce de la Casa Club. La falta de pago oportuno originar&aacute; intereses moratorias a 
	las tasas que hubieren sido acordadas y ser&aacute;n acreedores de las sanciones que establece este Reglamento.
</p>
	
<p align="justify" >
	<b>ART&Iacute;CULO VIG&Eacute;SIMO PRIMERO.-</b><br>
	Los colonos est&aacute;n obligados a guardar una conducta intachable y a cuidar que sus acompa&ntilde;antes que vivan en su hogar se conduzcan 
	en las instalaciones del club con la correcci&oacute;n y cordialidad debidas. 
</p>
	
<p align="justify" >
	<b>ART&Iacute;CULO VIG&Eacute;SIMO SEGUNDO.- </b><br>
	Los colonos est&aacute;n obligados a utilizar el mobiliario, instalaciones, y equipo del club, de acuerdo con el destino de los mismos, 
	evitando se da&ntilde;en o destruyan, har&aacute;n uso de los servicios del club en forma prudente y moderada. 
</p>
	
<p align="justify" >
	<b>ART&Iacute;CULO  VIG&Eacute;SIMO TERCERO.-</b><br>
	Los colonos est&aacute;n obligados a dispensar un trato cort&eacute;s y respetuoso al personal que labore en la Casa Club.	
</p>
<p align="justify" >
	<b>ART&Iacute;CULO VIG&Eacute;SIMO CUARTO.-</b><br>
	Los colonos est&aacute;n obligados a exhibir cada vez que les sea requerida por el personal de vigilancia o por cualquier funcionario o 
	empleado de la Casa Club, la credencial que los acredite como tales y a reportar el robo o extrav&iacute;o de las mismas y a obtener la 
	reposici&oacute;n correspondiente previo el pago de ellas.
</p>
	
<p align="justify" >
	<b>ART&Iacute;CULO VIG&Eacute;SIMO QUINTO.-</b><br>
	Los colonos est&aacute;n obligados a presentar certificado m&eacute;dico anual expedido por quienes legalmente tengan esa facultad en donde 
	conste que no sufren enfermedades infecciosas o contagiosas que pongan en peligro la salud de los dem&aacute;s colonos  o usuarios de la Casa Club.
</p>
	
<p align="justify" >
	<b>ART&Iacute;CULO VIG&Eacute;SIMO SEXTO.-</b><br>
	El uso de TODAS las &aacute;reas de la CASA CLUB, alberca, etc. ser&aacute;n por cuenta y riesgo de los usuarios y es requisito indispensable 
	que entreguen los certificados m&eacute;dicos a que se refiere el p&aacute;rrafo anterior. El uso y abuso de los mismos ser&aacute; bajo su 
	absoluta responsabilidad.
</p>
	
<p align="justify" >
	<b>ART&Iacute;CULO VIG&Eacute;SIMO S&Eacute;PTIMO.-</b><br>
	Los colonos tendr&aacute;n las siguientes obligaciones respecto de sus hijos menores de edad:
</p>

<ol type="a"  style="text-align:justify;">
	<li>
		Ser&aacute;n responsables de la conducta de sus hijos menores y pagar&aacute;n los adeudos que adquieran y el importe de los da&ntilde;os 
		que ocasionen a la Casa Club.
	</li>
	<li>
		Los hijos de los colonos menores de 12 a&ntilde;os no podr&aacute;n concurrir a las &aacute;reas, salones o dependencias del club donde su 
		entrada est&eacute; restringida.
	</li>
	<li>
		Los menores de 8 (ocho a&ntilde;os), deber&aacute;n estar acompa&ntilde;ados en todo momento por cualquiera de sus padres o una persona mayor, 
		quien ser&aacute; responsable del comportamiento y seguridad de &eacute;stos,  en la inteligencia de que esmerar&aacute; el cuidado, 
		respecto de aquellos menores de 5 a&ntilde;os. El club no tendr&aacute; ninguna responsabilidad por los accidentes que llegase a sufrir 
		cualquier menor.
	</li>
</ol>

<p align="justify" >
	<b>ART&Iacute;CULO VIG&Eacute;SIMO OCTAVO.-</b><br>
	La Casa Club El Cielo Residencial dar&aacute; servicio de martes a domingo. Los lunes estar&aacute; cerrado para proporcionar el mantenimiento 
	necesario a las instalaciones.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO VIG&Eacute;SIMO NOVENO.-</b><br>
	Adicionalmente a los lunes la Casa Club no abrir&aacute; sus puertas los d&iacute;as que la administraci&oacute;n determine y aquellos otros 
	que por disposiciones legales o de autoridad competente deber&aacute; de permanecer cerrado parcial o totalmente.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO TRIG&Eacute;SIMO.-</b><br>
	El horario de prestaci&oacute;n de servicios ser&aacute; determinado por la administraci&oacute;n, pudiendo variar seg&uacute;n el servicio o 
	evento de que se trate, en la inteligencia de que dichos horarios se har&aacute;n del conocimiento de los colonos, por lo que ninguna persona 
	ser&aacute; admitida en el recinto del club fuera de ese horario.
</p>

<p align="justify" >
	<b>ART&Iacute;CULO TRIG&Eacute;SIMO PRIMERO.-</b><br>
	La administraci&oacute;n podr&aacute; determinar que no sean usadas diversas &aacute;reas, edificaciones y dem&aacute;s instalaciones que integran 
	el club, o que se suspendan algunos de sus servicios, debido a compromisos que hubiese adquirido con los mismos colonos  la Casa Club, como torneos, 
	encuentros, competencias, exhibiciones, cursos, entrenamientos, eventos sociales o por cualquier otra causa que determine. 
</p>

<p align="justify" >
	<b>ART&Iacute;CULO TRIG&Eacute;SIMO SEGUNDO.-</b><br>
	Est&aacute; absolutamente prohibido el acceso a menores de edad a los lugares donde se haya indicado &eacute;sto en los art&iacute;culos previos. 
	Queda estrictamente prohibido:
</p>

<ol type="a"  style="text-align:justify;">
	<li>
		Fumar en el interior de ba&ntilde;os, vestidores y &aacute;reas cerradas y en todas las &aacute;reas del Club ya que es un &aacute;rea libre de humo.
	</li>
	<li>
		Trasladar equipos del gimnasio a otra &aacute;rea.
	</li>
	<li>
		Usar jabones, aceites, mascarillas o cualquier sustancia en la alberca.
	</li>
	<li>
		Todo colono o usuario deber&aacute; ducharse antes de usar la alberca y no deber&aacute; ingresar a &eacute;stas instalaciones con heridas, 
		vendajes, aceites o materias grasosas.
	</li>
	<li>
		Es obligatorio el uso de sandalias antiderrapantes en las zonas h&uacute;medas, incluyendo el &aacute;rea de la piscina y su traslado hacia 
		&eacute;sta. Toda persona que haga uso del gimnasio deber&aacute; calzar tenis.
	</li>
</ol>

<p align="justify" >
	<b>ART&Iacute;CULO TRIG&Eacute;SIMO TERCERO.-</b><br>
	&Uacute;nicamente tendr&aacute; acceso a las bodegas y dem&aacute;s &aacute;reas restringidas el personal debidamente autorizado, estando prohibido 
	para cualquier otra persona el acceso a dichos lugares. 
</p>
	
<p align="justify" >
	<b>ART&Iacute;CULO TRIG&Eacute;SIMO CUARTO.-</b><br>
	Queda terminantemente prohibido introducir al club cualquier clase de armas, materiales inflamables, explosivos u objetos peligrosos o alg&uacute;n 
	otro que pudiera perjudicar o da&ntilde;ar a los asociados, usuarios o a las instalaciones y en general a cualquier tercero.
</p>
	
<p align="justify" >
	<b>ART&Iacute;CULO TRIG&Eacute;SIMO QUINTO.-</b><br>
	Est&aacute; prohibida la introducci&oacute;n a la Casa Club de mascotas y animales.
</p>
	
<p align="justify" >
	<b>ART&Iacute;CULO TRIG&Eacute;SIMO SEXTO.-</b><br>
	Queda prohibido introducir a la Casa Club, alimentos, bebidas, exhibir anuncios comerciales, repartir propaganda, publicidad, solicitar suscripciones y 
	realizar ventas, efectuar propaganda pol&iacute;tica o realizar cualquier otra actividad ajena a la estrictamente relacionada 
	con el destino de la Casa Club.
</p>
	
<p align="justify" >
	<b>ART&Iacute;CULO  TRIG&Eacute;SIMO S&Eacute;PTIMO.-Sanciones.</b><br>
	El consejo de administraci&oacute;n se encuentra facultado para fijar las sanciones que correspondan de acuerdo a la falta cometida por el 
	colono el cual decretar&aacute; la suspensi&oacute;n provisional en el uso de las instalaciones de la Casa Club a los colonos que:
</p>
	
<ol type="a"  style="text-align:justify;">
	<li>
		Observen mala conducta, deportiva o social que desprestigio a la Casa Club dentro o fuera de &eacute;ste; la sanci&oacute;n se aplicar&aacute; 
		por un periodo de 30 a 180 d&iacute;as naturales o definitiva.
	</li>
	<li>
		A quien no pague sus cuotas de mantenimiento, servicios deportivos y consumo de alimentos y bebidas; se les suspender&aacute; por un 
		periodo de 30 a 60 d&iacute;as naturales, quedando condicionado su reingreso a la Casa Club hasta el d&iacute;a que haya realizado el 
		pago de sus adeudos totales.
	</li>
	<li>
		A quien no respete las disposiciones para el uso del estacionamiento; se le suspender&aacute; por un periodo de 30 a 60 d&iacute;as 
		naturales.
	</li>
	<li>
		A quien no cumpla con las disposiciones de este Reglamento o de los Reglamentos internos para el uso de cada &aacute;rea deportiva, 
		que no tengan sanci&oacute;n establecida; se aplicar&aacute; como sanci&oacute;n la suspensi&oacute;n de sus derechos por un periodo 
		de 30 d&iacute;as naturales. El Consejo de administraci&oacute;n excluir&aacute; de manera definitiva a los colonos que se coloquen en 
		cualquiera de los siguientes supuestos:
		<br><br>
		<ol type="a"  style="text-align:justify;">
			<li>
				Al que ejecute actos fraudulentos, dolosos o inmorales en perjuicio de los usuarios de la Casa Club.
			</li>
			<li>
				Al que haya sido sujeto de suspensi&oacute;n Provisional por 3 o m&aacute;s ocasiones.
			</li>
			<li>
				A quien deje de pagar las cuotas de mantenimiento o adeudos con el club por m&aacute;s de 90 d&iacute;as.
			</li>
		</ol>
	</li>
</ol>

<p align="justify" >
	Todo lo no previsto en este Reglamento de uso de la casa club, quedar&aacute; a resoluci&oacute;n del Consejo de Administraci&oacute;n 
	del colono En Cielo Residencial.
</p>
<br>
<p align="center" >
	<b>
		Atentamente
		<br><br><br><br>
		El Consejo  de Administraci&oacute;n
	</b>
</p>

<% 	} catch (Exception e) { 
	String errorLabel = "Error de Formato";
	String errorText = "El Formato no pudo ser desplegado exitosamente.";
	String errorException = e.toString();
	
	response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);
	}

%>
</body>
</html>



