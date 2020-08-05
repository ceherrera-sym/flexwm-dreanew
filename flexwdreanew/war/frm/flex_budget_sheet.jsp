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
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.shared.op.*"%>
<%@page import="com.flexwm.shared.cm.*"%>
<%@page import="com.flexwm.shared.fi.*"%>
<%@page import="com.flexwm.shared.wf.*"%>
<%@page import="com.flexwm.server.cm.*"%>
<%@page import="com.flexwm.server.op.*"%>
<%@page import="com.flexwm.server.fi.*"%>
<%@page import="com.flexwm.server.wf.*"%>
<%@page import="com.flexwm.shared.BmoFlexConfig"%>		
<%@page import="java.text.DecimalFormat"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@include file="../inc/login_opt.jsp" %>

<%
	try {
	String title = ":::Hoja de Presupuesto:::";

	BmoBudget bmoBudget = new BmoBudget();
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)sFParams.getBmoProgram(bmoBudget.getProgramCode());

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
	    <title><%= title%></title>
	    <link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>"> 
		<link rel="stylesheet" type="text/css" href="../css/flexwm.css"> 
	</head>
	<body class="default" <%= permissionPrint %>>
	
	<%
		NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(Locale.US);	
		DecimalFormat decimal = new DecimalFormat("0.00");
	    
	    // Obtener parametros
	    int budgetId = 0;
	    if (isExternal) budgetId = decryptId;
	    else if (request.getParameter("foreignId") != null) budgetId = Integer.parseInt(request.getParameter("foreignId"));
	    
	    double incomeBudgetItemAmount = 0;
	   
	    
	    PmBudget pmBudget = new PmBudget(sFParams);
	    bmoBudget = (BmoBudget)pmBudget.get(budgetId);
	    BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();

	    BmoUser bmoUser = new BmoUser();
	    PmUser pmUser = new PmUser(sFParams);
	    if (bmoBudget.getUserId().toInteger() > 0)
		    bmoUser = (BmoUser)pmUser.get(bmoBudget.getUserId().toInteger());
	    
	    BmoUser bmoUserParent = new BmoUser();
	    PmUser pmUserParent = new PmUser(sFParams);
	    if (bmoUser.getParentId().toInteger() > 0)
	    	bmoUserParent = (BmoUser)pmUserParent.get(bmoUser.getParentId().toInteger());
	    
	    BmoCompany bmoCompany = new BmoCompany();
		PmCompany pmCompany = new PmCompany(sFParams);
		if (bmoBudget.getCompanyId().toInteger() > 0)
			bmoCompany = (BmoCompany)pmCompany.get(bmoBudget.getCompanyId().toInteger());
		
		// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
		String logoURL ="";
		if (!bmoCompany.getLogo().toString().equals(""))
			logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
		else 
			logoURL = sFParams.getMainImageUrl();
	    
	   
	    
	%>
	
		<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
			<tr>
				<td align="left" rowspan="10" valign="top">
					<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
				</td>
				<td colspan="4" width="95%" class="reportTitleCell">
				Presupuesto: <%= bmoBudget.getName().toString() %>
				</td>
			</tr>     
			<tr>
				<th align = "left" class="reportCellEven">Nombre:</th>
				<td class="reportCellEven">
					<%= bmoBudget.getName().toString() %>                 
				</td>
				<th align = "left" class="reportCellEven">Responsable:</th>
				<td class="reportCellEven">
					<%= bmoUser.getFirstname().toString() %>
					<%= bmoUser.getFatherlastname().toString() %>
					<%= bmoUser.getMotherlastname().toString() %>  
				</td>
			</tr>
			<tr>
				<th align = "left" class="reportCellEven">Estatus:</th>
				<td class="reportCellEven">
					<b><%= bmoBudget.getStatus().getSelectedOption().getLabel() %></b>       
				</td>
				<th align = "left" class="reportCellEven">Descripci&oacute;n:</th>
				<td class="reportCellEven">
					<%= bmoBudget.getDescription().toString() %>               
				</td>
			</tr>
			<tr>
				<th align = "left" class="reportCellEven">Inicio:</th>
				<td class="reportCellEven">
					<%= bmoBudget.getStartDate().toString() %>             
				</td>
				<th align = "left" class="reportCellEven">Fin:</th>
				<td class="reportCellEven">
					<%= bmoBudget.getEndDate().toString() %>                 
				</td>
			</tr>    
		</table>
		<br>
	
		<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	        <tr>
	            <td colspan="8" class="reportHeaderCell">
	                Ingresos               
	            </td>
	        </tr>
	        <tr>                    
	            <th class="reportCellEven" align="left">Clave</th>
	            <th class="reportCellEven" align="left">Nombre</th>
	            <th class="reportCellEven" align="left">Descripci&oacute;n</th>
	            <th class="reportCellEven" align="right">Monto</th>
	            <th class="reportCellEven" align="right">&nbsp;</th>
	            <th class="reportCellEven" align="right">Provisionado</th>   
	            <th class="reportCellEven" align="right">Pagos</th>
	            <th class="reportCellEven" align="right">Saldo</th>
	        </tr>
				<%
				double sumAmountItems = 0, sumProvisionedItems = 0, sumPaymentsItems = 0, sumBalanceItems = 0;
				
				ArrayList<BmFilter> filterListD = new ArrayList<BmFilter>();
				BmFilter filterBudgetD = new BmFilter();
				BmFilter filterBudgetTypeD = new BmFilter();

				filterBudgetD.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBudgetId().getName(), bmoBudget.getId());
				filterListD.add(filterBudgetD);
				filterBudgetTypeD.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBmoBudgetItemType().getType(), ""+BmoBudgetItemType.TYPE_DEPOSIT);
				filterListD.add(filterBudgetTypeD);
				
				Iterator<BmObject> listBudgetItemD = new PmBudgetItem(sFParams).list(filterListD).iterator();
				while (listBudgetItemD.hasNext()) {
					bmoBudgetItem = (BmoBudgetItem)listBudgetItemD.next();
					
					if (bmoBudgetItem.getBudgetItemTypeId().toInteger() == ((BmoFlexConfig)sFParams.getBmoAppConfig()).getDepositBudgetItemTypeId().toInteger()) {
						incomeBudgetItemAmount = bmoBudgetItem.getAmount().toDouble();
					}
					
					sumAmountItems += bmoBudgetItem.getAmount().toDouble();
					sumProvisionedItems += bmoBudgetItem.getProvisioned().toDouble();
					sumPaymentsItems += bmoBudgetItem.getPayments().toDouble();
					sumBalanceItems += bmoBudgetItem.getBalance().toDouble();

				%>
		           <tr> 
		               <td class="reportCellEven" align="left">
		                    <b><%= bmoBudgetItem.getBmoBudgetItemType().getName().toString() %></b>                  
		               </td>
		               <td class="reportCellEven" align="left">
		                    <%= bmoBudgetItem.getBmoBudgetItemType().getName().toString() %>                 
		               </td>
		               <td class="reportCellEven" align="left">
		                   <%= bmoBudgetItem.getComments().toString() %>                 
		              </td>
		               <td class="reportCellEven" align="right">
		                    <%= formatCurrency.format(bmoBudgetItem.getAmount().toDouble()) %>                 
		               </td>
		               <td class="reportCellEven" align="right">
	                    	&nbsp;                 
	                   </td>
		               <td class="reportCellEven" align="right">
		                    <%= formatCurrency.format(bmoBudgetItem.getProvisioned().toDouble()) %>                 
		               </td>
		               <td class="reportCellEven" align="right">
		                    <%= formatCurrency.format(bmoBudgetItem.getPayments().toDouble()) %>                  
		               </td>      
		               <td class="reportCellEven" align="right">
		                    <%= formatCurrency.format(bmoBudgetItem.getBalance().toDouble()) %>                 
		               </td>            
		          </tr>
	          <%
	            }
	           %>

	        <tr>
			   <td colspan="3" class="reportCellEven" align="left">
					&nbsp;
			   </td> 
	            <td class="reportCellEven" align="right"> 
	            	<b>
	            		<%= formatCurrency.format(sumAmountItems) %> 
	            	</b>
	            </td>
	            <td class="reportCellEven" align="right">
	            	&nbsp;
	            </td>
	            <td class="reportCellEven" align="right">
	            	<b>
	            		<%= formatCurrency.format(sumProvisionedItems) %> 
	        		</b>
	            </td>
	            <td class="reportCellEven" align="right">
	            	<b>
	            		<%= formatCurrency.format(sumPaymentsItems) %> 
            		</b>
	            </td>
	            <td class="reportCellEven" align="right">
	            	<b>
	            		<%= formatCurrency.format(sumBalanceItems) %>
	            	</b>
        		</td>
	        </tr>
	        <tr>
	    		<td class="reportCellEven" colspan="8">
	    			&nbsp;
	    		</td>
			</tr>			
	        <tr>
	            <td colspan="8" class="reportHeaderCell">
	                Egresos               
	            </td>
	        </tr>
	        <tr>                    
	            <th class="reportCellEven" align="left">Clave</th>
	            <th class="reportCellEven" align="left">Nombre</th>
	            <th class="reportCellEven" align="left">Descripci&oacute;n</th>
	            <th class="reportCellEven" align="right">Monto</th>
	            <th class="reportCellEven" align="right">&nbsp;</th>
	            <th class="reportCellEven" align="right">Provisionado</th>   
	            <th class="reportCellEven" align="right">Pagos</th>
	            <th class="reportCellEven" align="right">Saldo</th>
	        </tr>
				<%
				double sumAmountItemsW = 0, sumProvisionedItemsW = 0, sumPaymentsItemsW = 0, sumBalanceItemsW = 0;
				
				
				ArrayList<BmFilter> filterListW = new ArrayList<BmFilter>();
				BmFilter filterBudgetW = new BmFilter();
				BmFilter filterBudgetTypeW = new BmFilter();
	
				filterBudgetW.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBudgetId().getName(), bmoBudget.getId());
				filterListW.add(filterBudgetW);
				filterBudgetTypeW.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBmoBudgetItemType().getType(), ""+BmoBudgetItemType.TYPE_WITHDRAW);
				filterListW.add(filterBudgetTypeW);
				
				Iterator<BmObject> listBudgetItemW = new PmBudgetItem(sFParams).list(filterListW).iterator();
	
				
				while (listBudgetItemW.hasNext()) {
					bmoBudgetItem = (BmoBudgetItem)listBudgetItemW.next();
					sumAmountItemsW += bmoBudgetItem.getAmount().toDouble();
					sumProvisionedItemsW += bmoBudgetItem.getProvisioned().toDouble();
					sumPaymentsItemsW += bmoBudgetItem.getPayments().toDouble();
					sumBalanceItemsW += bmoBudgetItem.getBalance().toDouble();
	
				%>
		           <tr> 
		               <td class="reportCellEven" align="left">
		                    <b><%= bmoBudgetItem.getBmoBudgetItemType().getName().toString() %></b>                  
		               </td>
		               <td class="reportCellEven" align="left">
		                    <%= bmoBudgetItem.getBmoBudgetItemType().getName().toString() %>                 
		               </td>
		               <td class="reportCellEven" align="left">
		                   <%= bmoBudgetItem.getComments().toString() %>                 
		              </td>
		               <td class="reportCellEven" align="right">
		                    <%= formatCurrency.format(bmoBudgetItem.getAmount().toDouble()) %>&nbsp;
		               </td>     
		               <td class="reportCellEven" align="center" width="10">
		                    <%= decimal.format((bmoBudgetItem.getAmount().toDouble() / incomeBudgetItemAmount) * 100) + " %" %>
		               </td>
		               <td class="reportCellEven" align="right">
		                    <%= formatCurrency.format(bmoBudgetItem.getProvisioned().toDouble()) %>                 
		               </td>
		               <td class="reportCellEven" align="right">
		                    <%= formatCurrency.format(bmoBudgetItem.getPayments().toDouble()) %>                  
		               </td>      
		               <td class="reportCellEven" align="right">
		                    <%= formatCurrency.format(bmoBudgetItem.getBalance().toDouble()) %>                 
		               </td>            
		          </tr>
	          <%
	            }
	           %>
	
	        <tr>
			   <td colspan="3" class="reportCellEven" align="left">
					&nbsp;
			   </td> 
	            <td class="reportCellEven" align="right"> 
	            	<b>
	            		<%= formatCurrency.format(sumAmountItemsW) %>	            		
	            	</b>
	            </td>
	            <td class="reportCellEven" align="center"> 
	            	&nbsp;            		
            	</td>	
	            <td class="reportCellEven" align="right">
	            	<b>
	            		<%= formatCurrency.format(sumProvisionedItemsW) %> 
	        		</b>
	            </td>
	            <td class="reportCellEven" align="right">
	            	<b>
	            		<%= formatCurrency.format(sumPaymentsItemsW) %> 
	        		</b>
	            </td>
	            <td class="reportCellEven" align="right">
	            	<b>
	            		<%= formatCurrency.format(sumBalanceItemsW) %>
	            	</b>
	    		</td>
	        </tr>	        
	        <tr>
	        	<td colspan="7">&nbsp;</td>
	        </tr>
	        <tr>
	        	<td colspan="7">&nbsp;</td>
	        </tr>
	        <tr>
	        	<td colspan="7">&nbsp;</td>
	        </tr>	       
	        <tr>
	        	<th align="left" colspan="3" class="reportCellEven">Balances:</th>
	        	<td class="reportCellEven" align="right">
	        		<b><%= formatCurrency.format(sumAmountItems - sumAmountItemsW) %></b>
	        	</td>
	        	<td class="reportCellEven" align="center">	        	
	            	<b><%= decimal.format(((sumAmountItems - sumAmountItemsW) / incomeBudgetItemAmount) * 100) + " %"  %></b>
	            </td>
	        	<td class="reportCellEven" align="right">
	        		<b><%= formatCurrency.format(sumProvisionedItems - sumProvisionedItemsW) %></b>
	        	</td>
	        	<td class="reportCellEven" align="right">
	        		<b><%= formatCurrency.format(sumPaymentsItems - sumPaymentsItemsW) %></b>
	        	</td>	        	
	        	<td class="reportCellEven" align="right">
	        		<b><%= formatCurrency.format(sumBalanceItems - sumBalanceItemsW) %></b>
	        	</td>
	        </tr>
	    </table>
	
        <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
	        <tr>
		    	<td colspan="">&nbsp;</td>
		    </tr>
		    <tr align="center">
		        <td align="center"><br><br>
		            ___________________
		        </td>
		        <td align="center"><br><br>
		        	___________________
		        </td>
	        	<td width="15%" align="center"><br><br>
		        	&nbsp;
		        </td>
		    </tr>
		    <tr>
			    <td align="center" class="documentTitleCaption">
				    Reponsable<br>
				    <%= bmoUser.getFirstname().toString() %>
				    <%= bmoUser.getFatherlastname().toString() %>
				    <%= bmoUser.getMotherlastname().toString() %>  
				</td>
				<td align="center" class="documentTitleCaption">
				    Jefe Inmediato<br>
				   	<%= bmoUserParent.getFirstname().toString() %>
				    <%= bmoUserParent.getFatherlastname().toString() %>  
				    <%= bmoUserParent.getMotherlastname().toString() %>      
			    </td>
			    <td align="center" class="documentTitleCaption">
			    	&nbsp;
				</td>
		    </tr>
	    </table>
	    <p class="documentTitleCaption" align="left"> 
			Fecha de Impresi&oacute;n <%= SFServerUtil.nowToString(sFParams, "dd/MM/yyyy HH:mm") %>
		</p>
	
	<%  
		} catch (Exception e) { 
		    String errorLabel = "Error en Hoja de Presupuesto";
		    String errorText = "La Hoja de Presupuesto no pudo ser desplegado exitosamente.";
		    String errorException = e.toString();
		    
		    response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);
	    }	
	%>
