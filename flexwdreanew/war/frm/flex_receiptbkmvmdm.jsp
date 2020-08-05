<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.shared.op.*"%>
<%@page import="com.flexwm.shared.cm.*"%>
<%@page import="com.flexwm.shared.fi.*"%>
<%@page import="com.flexwm.shared.wf.*"%>
<%@page import="com.symgae.client.ui.UiParams"%>

<%@page import="com.flexwm.server.cm.*"%>
<%@page import="com.flexwm.server.op.*"%>
<%@page import="com.flexwm.server.fi.*"%>
<%@page import="com.flexwm.server.wf.*"%>
<%@include file="../inc/login_opt.jsp" %>

<%
String title = "";

BmoBankMovement bmoBankMovement = new BmoBankMovement();
BmoProgram bmoProgram = new BmoProgram();
PmProgram pmProgram  = new PmProgram(sFParams);
bmoProgram = (BmoProgram)sFParams.getBmoProgram(bmoBankMovement.getProgramCode());
%>

<html>
<% 
//Imprimir
String print = "0", permissionPrint = "";
if ((String)request.getParameter("print") != null) print = (String)request.getParameter("print");

//Exportar a Excel
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
    <title>:::Movimiento Bancario:::</title>
    <link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>"> 
	<link rel="stylesheet" type="text/css" href="../css/flexwm.css"> 
</head>
<body class="default" <%= permissionPrint %>>

<%

		NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(Locale.US);
		PmConn pmConnEvento = new PmConn(sFParams);
		pmConnEvento.open();
		PmConn concepts = new PmConn(sFParams);
		concepts.open();
		String nameProj="";
	    // Obtener parametros
	    int bankMovementId = 0;
	    String sql="", sqlConcepts; 
	    int movConcept=0, movRequi = 0;
	      
	    if (isExternal) bankMovementId = decryptId;
	    else if (request.getParameter("foreignId") != null) bankMovementId = Integer.parseInt(request.getParameter("foreignId"));
	    
	    
	    PmBankMovement pmBankMovement = new PmBankMovement(sFParams);
	    bmoBankMovement = (BmoBankMovement)pmBankMovement.get(bankMovementId);
	    
	    BmoBankAccount bmoBankAccount = new BmoBankAccount();
	    
	    if(bmoBankMovement.getBankAccoTransId().toInteger() > 0) {
		    PmBankAccount pmBankAccount = new PmBankAccount(sFParams);
		    bmoBankAccount = (BmoBankAccount)pmBankAccount.get(bmoBankMovement.getBankAccoTransId().toInteger());
	    }
	    
	    //Obtener la empresa
	    PmCompany pmCompany = new PmCompany(sFParams);
	    BmoCompany bmoCompany = (BmoCompany)pmCompany.get(bmoBankMovement.getBmoBankAccount().getCompanyId().toInteger());
	    
	 // Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
	 		String logoURL ="";
	 		if (!bmoCompany.getLogo().toString().equals(""))
	 			logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
	 		else 
	 			logoURL = sFParams.getMainImageUrl();
	 		
	    PmUser pmUser = new PmUser(sFParams);
	    BmoUser bmoUser = new BmoUser();
%>

<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
      <tr>
            <td align="left" width="" rowspan="11" valign="top">
				<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
            </td>
            <td colspan="4" class="reportTitleCell">
            	Movimiento Bancario: <%= bmoBankMovement.getCode().toString() %>
            </td>
      </tr>
      <tr>
	      <th align = "left" class="reportCellEven">Nombre:</th>
		  <td class="reportCellEven" colspan="">
		     	<%= bmoBankMovement.getBmoBankAccount().getName().toString() %>           
		  </td>	
	      <th align = "left" class="reportCellEven">No.Cuenta:</th>
	      <td class="reportCellEven">
	           <%= bmoBankMovement.getBmoBankAccount().getAccountNo().toString() %>                 
	      </td>
	      
      </tr>    
      <% if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_CXP) ||
    		 bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE) || 
    		 bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_CXC) ||    		 
    		 bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_DEVOLUTIONCXC)
    		 ) { %> 
	      <tr>
	      	<% if (bmoBankMovement.getSupplierId().toInteger() > 0) { %>
	            <th align = "left" class="reportCellEven">Proveedor:</th>            
	            <td class="reportCellEven" colspan="">
	                 <%= bmoBankMovement.getBmoSupplier().getCode().toString() %> - <%= bmoBankMovement.getBmoSupplier().getLegalName().toString() %>                 
	            </td>
		    <% } else if (bmoBankMovement.getCustomerId().toInteger() > 0) { %>        
	            <th align = "left" class="reportCellEven">Cliente:</th>
		  	    <td class="reportCellEven" colspan="">
		  	      	<%= bmoBankMovement.getBmoCustomer().getCode().toString() %> - <%= bmoBankMovement.getBmoCustomer().getDisplayName().toString() %>           
		  	    </td>
		  	<% } %>   
		 	 <th align = "left" class="reportCellEven">Empresa:</th>
		  	 <td class="reportCellEven" colspan="">
		  	   	<%= bmoCompany.getName().toString() %>           
		  	 </td>
		  </tr>      		
      <% } %>
      <tr>
	      <th align = "left" class="reportCellEven">Descripci&oacute;n:</th>
	      <td class="reportCellEven">
	      		&nbsp;<%= bmoBankMovement.getDescription().toString() %>         
	      </td>
	      <th align = "left" class="reportCellEven">Banco:</th>
	      <td class="reportCellEven">
	      &nbsp;<%= bmoBankMovement.getBmoBankAccount().getBankName().toString() %>
	      </td>
      </tr>  
      <tr>
      	  <th align = "left" class="reportCellEven">F.Pago:</th>
	      <td class="reportCellEven">
	      		&nbsp;<%= bmoBankMovement.getDueDate().toString() %>       
	      </td>
	      <th align="left" class="reportCellEven">No.Cheque:</th>
		  <td class="reportCellEven"><%= bmoBankMovement.getNoCheck().toString() %>
		  </td>
	  </tr>
	  <% 
	  //if (bmoBankMovement.getBmoBankMovType().getCategory().toChar() != BmoBankMovType.CATEGORY_TRANSFER) {
		  
	  if (bmoBankMovement.getBmoBankMovType().getCategory().toChar() == BmoBankMovType.CATEGORY_TRANSFER) {
		  String bkacOrign = "";
		  String bkacDestiny  = "";

		  BmoBankMovement bmoBankOrignDestiny = new BmoBankMovement();		  
		  //Origen = quienEnvia Destino = Quien Recibe
		  if (bmoBankMovement.getBmoBankMovType().getType().toChar() == BmoBankMovType.TYPE_DEPOSIT) {
			  //Obtener el MB Origen
			  bmoBankOrignDestiny = (BmoBankMovement)pmBankMovement.get(bmoBankMovement.getParentId().toInteger());
			  
			  bkacOrign = bmoBankOrignDestiny.getBmoBankAccount().getName().toString();
			  bkacDestiny = bmoBankMovement.getBmoBankAccount().getName().toString();
		  } else {
		  
			  bmoBankOrignDestiny = (BmoBankMovement)pmBankMovement.getBy(bmoBankMovement.getId(), bmoBankMovement.getParentId().getName());
			  
			  bkacOrign = bmoBankMovement.getBmoBankAccount().getName().toString();
			  bkacDestiny = bmoBankOrignDestiny.getBmoBankAccount().getName().toString();
		  }
	  %>	
			<tr>
			      <th align = "left" colspan="4" class="reportCellEven">Cta.Banco Origen:</th>
			      <td class="reportCellEven">
			      		&nbsp;<%= bkacOrign %>
			      </td>
			      <th align = "left" colspan="4" class="reportCellEven">Cta.Banco Destino:</th>
			      <td class="reportCellEven">
			      		&nbsp;<%= bkacDestiny %>
			      </td>
			</tr>
	  <% }%>
	  
	  <tr>
	      <th align = "left" class="reportCellEven">Cargo:</th>
	      <td class="reportCellEven">
	      		&nbsp;<%= formatCurrency.format(bmoBankMovement.getWithdraw().toDouble()) %>      
	      </td>
	      <th align = "left" class="reportCellEven">Abono:</th>
	      <td class="reportCellEven">
	      		&nbsp;<%= formatCurrency.format(bmoBankMovement.getDeposit().toDouble()) %>     
	      </td>
	  </tr>
	  <tr>
	  		<%
		  		BmoUser bmoUserFix = new BmoUser();
	  			if (bmoBankMovement.getUserCreateId().toInteger() > 0)
	  				bmoUserFix = (BmoUser)pmUser.get(bmoBankMovement.getUserCreateId().toInteger());
	  			
	  		%>
	        <th align = "left" class="reportCellEven">Elaborador por:</th>
	        <td class="reportCellEven">
	            <%= bmoUserFix.getFirstname().toString() + " " + bmoUserFix.getFatherlastname().toString() %>                
	        </td>
	  		<th align="left" class="reportCellEven">Fecha Elaboraci&oacute;n:</th>
			<td class="reportCellEven"><%=bmoBankMovement.getDateCreate().toString()%>
			</td>
	  </tr>
	  <% if (bmoBankMovement.getAuthorizeUserId().toInteger() > 0) { %>
		  <tr>		
		  		<%
		    		bmoUserFix = new BmoUser();	
		    		if (bmoBankMovement.getAuthorizeUserId().toInteger() > 0)	    			
		    			bmoUserFix = (BmoUser)pmUser.get(bmoBankMovement.getAuthorizeUserId().toInteger());
		    	%>
		    	<th align = "left" class="reportCellEven">Autorizado por:</th>
		        <td class="reportCellEven">
		            <%= bmoUserFix.getFirstname().toString() + " " + bmoUserFix.getFatherlastname().toString() %>                
		        </td>
		        <th align="left" class="reportCellEven">Fecha Autorizado:</th>
				<td class="reportCellEven"><%=bmoBankMovement.getAuthorizeDate().toString()%>
				</td>
		  </tr>
	  <% } %>
	  <% if (bmoBankMovement.getReconciledUserId().toInteger() > 0) { %>
		  <tr>		
		  		<%
		    		bmoUserFix = new BmoUser();	
		    		if (bmoBankMovement.getReconciledUserId().toInteger() > 0)	    			
		    			bmoUserFix = (BmoUser)pmUser.get(bmoBankMovement.getReconciledUserId().toInteger());
		    	%>
		    	<th align = "left" class="reportCellEven">Conciliado por:</th>
		        <td class="reportCellEven">
		            <%= bmoUserFix.getFirstname().toString() + " " + bmoUserFix.getFatherlastname().toString() %>                
		        </td>
		        <th align="left" class="reportCellEven">Fecha Conciliaci&oacute;n:</th>
				<td class="reportCellEven"><%=bmoBankMovement.getReconciledDate().toString()%>
				</td>
		  </tr>
	  <% } %>	    			
	  <tr>
			<th align = "left" class="reportCellEven">Estatus:</th>
	        <td class="reportCellEven">
	             <%= bmoBankMovement.getStatus().getSelectedOption().getLabel() %>                
	        </td>
	       <% if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_TRANSFER) ||
	    		  bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_DEPOSITFREE) ||
	    		  bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_CREDITDISPOSAL) ||
	    		  bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_MULTIPLECXC) ||
	    		  bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_LOANDISPOSAL) ||
	    		  bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_DISPOSALFREE)) { %>
		        <th align = "left" class="reportCellEven">Empresa:</th>
			  	<td class="reportCellEven" colspan="">
			  	   	<%= bmoCompany.getName().toString() %>           
			  	</td>
		   <% } else { %>
			   	<td class="reportCellEven" colspan="3">
					&nbsp;
		        </td>
		   <% } %>	 
	 </tr>
	  	  
</table>
<br>

<br> 
<%
 	if (bmoBankMovement.getBmoBankMovType().getCategory().toChar() != BmoBankMovType.CATEGORY_TRANSFER) {
 %>
	

<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
        <tr>
            <td colspan="10" class="reportHeaderCell">
                Detalle del Movimiento de Banco                
            </td>
        </tr>
       
			<%
			
				//PmPaccount pmPaccount = new PmPaccount(sFParams);
				BmoPaccount bmoParent = null;
				int celda = 1;
			
				BmoBankMovConcept bmoBankMovConcept = new BmoBankMovConcept();
				BmFilter filterBankMovConcept = new BmFilter();
				filterBankMovConcept.setValueFilter(bmoBankMovConcept.getKind(), bmoBankMovConcept.getBankMovementId(), bmoBankMovement.getId());
				Iterator<BmObject> listBankMovConcept = new PmBankMovConcept(sFParams).list(filterBankMovConcept).iterator();
				while (listBankMovConcept.hasNext()) {
					bmoBankMovConcept = (BmoBankMovConcept)listBankMovConcept.next();
					
				    BmoPaccount bmoPaccount = new BmoPaccount();
					PmPaccount pmPaccount = new PmPaccount(sFParams);
					BmoBankMovConcept bankMovConcept = new BmoBankMovConcept();
				    PmBankMovConcept pmBankMovConcept = new PmBankMovConcept(sFParams);
				    BmoRequisition bmoRequisition = new BmoRequisition();
					PmRequisition pmRequisition = new PmRequisition(sFParams);
					if(bmoBankMovConcept.getRequisitionId().toInteger() > 0) {
				    	bmoRequisition = (BmoRequisition)pmRequisition.get(bmoBankMovConcept.getRequisitionId().toInteger());
				    	bmoUser = (BmoUser) pmUser.get(bmoRequisition.getUserCreateId().toInteger());
				    	if(bmoBankMovConcept.getPaccountId().toInteger()>0)
				    	bmoPaccount = (BmoPaccount) pmPaccount.get(bmoBankMovConcept.getPaccountId().toInteger());			    
				    	if(celda==1){
						%>
						
						<TR class="">
						<th class="reportCellEven" align="left">O.C</th>
						<th class="reportCellEven" align="left">CXP</th>

			<th class="reportCellEven" align="left">Nombre O.C</th>
			<th class="reportCellEven" align="left">F. Solicitud</th>
			<th class="reportCellEven" align="left">F. Entrega</th>
			<th class="reportCellEven" align="left">Autorizacion</th>
			<th class="reportCellEven" align="right">Total</th>
		</TR>
		<%
				    celda = 0;
					
				    	}
				    	} else {
						if (bmoBankMovConcept.getRaccountId().toInteger() > 0) {
						    if (celda==1) {
							    %>
							     <tr>
								    <th class="reportCellEven" align="left">Clave</th>
								    <th class="reportCellEven" align="left">Descripc&iacute;on</th>						    	
							    	<th class="reportCellEven" align="left">Cobro en Bancos</th>
							    	<th class="reportCellEven" align="left">Estatus</th>
							        <th class="reportCellEven" align="right">Monto</th>
						        </tr>
					    <% 		celda=0; 
						    }
						} else {
							if(bmoBankMovConcept.getPaccountId().toInteger() > 0) {
							    bmoPaccount = (BmoPaccount)pmPaccount.get(bmoBankMovConcept.getPaccountId().toInteger());
							    if(celda==1){ %>
							   		 <TR>
								    	<th class="reportCellEven" align="left">CxP</th>
								    	<th class="reportCellEven" align="left">Factura</th>
								    	<th class="reportCellEven" align="left">Proveedor</th>
								    	<th class="reportCellEven" align="left">Descripci&oacute;n</th>
								    	<th class="reportCellEven" align="left">Fecha de Prog.</th>
								    	<th class="reportCellEven" align="left">Autorizaci&oacute;n</th>									    	
								        <th class="reportCellEven" align="right">Monto</th>
								        </TR>
								 <%		celda=0; 
							    }
					    	}
						}
				    }
				    %>

           <TR class=""> 
		      
		      		<%	if(bmoBankMovConcept.getRaccountId().toInteger() > 0) { %>
		      			<td class="reportCellEven" align="left">
		      				&nbsp;<%= bmoBankMovConcept.getBmoBankMovement().getCode().toString() %>
		      			</td>
		      			<td class="reportCellEven" align="left">		      				
		      				&nbsp;<%=  bmoBankMovConcept.getBmoBankMovement().getDescription().toString() %>                 
		                </td>		                
	                    <td class="reportCellEven" align="left">
	                    	&nbsp;<%= bmoBankMovConcept.getBmoBankMovement().getDueDate().toString() %>                  
	                    </td>	                    
	                    <td class="reportCellEven" align="left">
	                    	&nbsp;<%= bmoBankMovConcept.getBmoBankMovement().getStatus().getSelectedOption().getLabel() %>                  
	                    </td>	                    
		                <td class="reportCellEven" align="right">
		                	&nbsp;<%= formatCurrency.format(bmoBankMovConcept.getAmount().toDouble()) %>                 
		                </td> 
		           <% }%>
		           
		           <%  

		           		if(bmoBankMovConcept.getRequisitionId().toInteger() > 0) { %>
						
		        		<td class="reportCellEven" align="left">&nbsp;<%= bmoRequisition.getCode().toString() %>
		        			</td>
		        			<%
		        			if(bmoBankMovConcept.getPaccountId().toInteger() > 0){
		        			
		        			%>
		        				<td class="reportCellEven" align="left">&nbsp;<%= bmoPaccount.getCode().toString() %>
		        				</td>
		        			<%
		        			}else{
		        				%>
		        				<td class="reportCellEven" align="left">-</td>
		        				<%
		        			}
		        			%>
		        			<td class="reportCellEven" align="left">&nbsp;<%= bmoRequisition.getName().toString() %>
		        			</td>
		        			<td class="reportCellEven" align="left">&nbsp;<%= bmoRequisition.getRequestDate().toString() %>
		        			</td>
		        			<td class="reportCellEven" align="left">&nbsp;<%= bmoRequisition.getDeliveryDate().toString() %>
		        			</td>
		        			<td class="reportCellEven" align="left">&nbsp;<%= bmoUser.getFirstname().toString() %>
		        			</td>
		        			<td class="reportCellEven" align="right">&nbsp;<%= formatCurrency.format(bmoRequisition.getTotal().toDouble()) %>
		        			</td>
		        			<%
		        			} else {
			           		if(bmoBankMovConcept.getPaccountId().toInteger() > 0) {
								
									bmoParent = (BmoPaccount)pmPaccount.get(bmoBankMovConcept.getPaccountId().toInteger());
								%>
								<td class="reportCellEven" align="left">
									&nbsp;<%= bmoParent.getCode().toString() %> 
								</td>
								<td class="reportCellEven" align="left">									
									&nbsp;<%= bmoParent.getInvoiceno().toString() %> 
								</td>
								<td class="reportCellEven" align="left">
									&nbsp;<%= bmoParent.getBmoSupplier().getCode().toString() %>
									<%= bmoParent.getBmoSupplier().getName().toString() %>                
								</td>
								<td class="reportCellEven" align="left">
									&nbsp;<%= bmoParent.getDescription().toString() %>                  
								</td>
								
								<td class="reportCellEven" align="left">
									&nbsp;<%= bmoParent.getDueDate().toString() %> 
								</td>
								<td class="reportCellEven" align="left">
									<%
									 bmoUser = new BmoUser();
									 if(bmoPaccount.getAuthorizedUser().toInteger() > 0) 
									 	bmoUser = (BmoUser)pmUser.get(bmoPaccount.getAuthorizedUser().toInteger());
									%>
									&nbsp;<%= bmoUser.getCode().toString() + " " + bmoPaccount.getAuthorizedDate().toString() %> 
								</td>								
								<td class="reportCellEven" align="right">
									&nbsp;<%= formatCurrency.format(bmoBankMovConcept.getAmount().toDouble()) %>                 
								</td> 
		           			<% }%>
		           	 <% }%>
          </TR>
          <%
				} //listBankMovConcept
           %>
            <tr>
	            <td colspan="10">&nbsp;</td>
	        </tr> 
</table> 


<% } //if%>	
<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0" class="documentTitleCaption">
	<tr align="center">
	    <td>
	    	&nbsp;
	    </td>
	    <td>
	    	&nbsp;
	    </td>
	</tr>
	
	<tr align="center">
	    <td align="center"><br><p><br>
	        _______________________________________<br>
	        Aplicado por
	    </td>
	</tr>
	<tr>
	    <td align="center" class="reportCellEven">
	    	<%
	    		//Obterner el Responsable de la Cta de Banco
	    		
	    		bmoUser = new BmoUser();
	    		if (bmoBankMovement.getBmoBankAccount().getResponsibleId().toInteger() > 0)
	    			bmoUser = (BmoUser)pmUser.get(bmoBankMovement.getBmoBankAccount().getResponsibleId().toInteger());	
	    	%>
	        <%= bmoUser.getFirstname().toString() + " " + bmoUser.getFatherlastname().toString() %> 
	    </td>
	</tr> 
</table>


<p class="documentTitleCaption" align="left"> 
Fecha de Impresi&oacute;n <%= SFServerUtil.nowToString(sFParams, "dd/MM/yyyy HH:mm") %> Por:
<%=  sFParams.getLoginInfo().getBmoUser().getFirstname() + " " + sFParams.getLoginInfo().getBmoUser().getFatherlastname().toString() %>
</p>


<%  
pmConnEvento.close();
concepts.close();
	
%>
