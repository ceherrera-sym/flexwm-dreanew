/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.cm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.HtmlUtil;
import com.symgae.server.PmConn;
import com.symgae.server.SFSendMail;
import com.symgae.server.SFServerUtil;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFException;
import com.symgae.shared.SFMailAddress;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.server.sf.PmFormat;
import com.symgae.server.sf.PmUser;
import com.flexwm.server.fi.PmCurrency;
import com.flexwm.server.op.PmOrderType;
import com.flexwm.server.wf.PmWFlow;
import com.flexwm.server.wf.PmWFlowLog;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoCustomerEmail;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.cm.BmoOpportunityDetail;
import com.flexwm.shared.cm.BmoQuote;
import com.flexwm.shared.cm.BmoQuoteGroup;
import com.flexwm.shared.cm.BmoQuoteItem;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.op.BmoOrderType;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoFormat;
import com.symgae.shared.sf.BmoUser;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowLog;


public class PmQuote extends PmObject {
	BmoQuote bmoQuote;

	public PmQuote(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoQuote = new BmoQuote();
		setBmObject(bmoQuote);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoQuote.getCurrencyId(), bmoQuote.getBmoCurrency()),
				new PmJoin(bmoQuote.getOrderTypeId(), bmoQuote.getBmoOrderType()),
				new PmJoin(bmoQuote.getUserId(), bmoQuote.getBmoUser()),
				new PmJoin(bmoQuote.getBmoUser().getAreaId(), bmoQuote.getBmoUser().getBmoArea()),
				new PmJoin(bmoQuote.getBmoUser().getLocationId(), bmoQuote.getBmoUser().getBmoLocation()),
				new PmJoin(bmoQuote.getCustomerId(), bmoQuote.getBmoCustomer()),
				new PmJoin(bmoQuote.getBmoCustomer().getTerritoryId(), bmoQuote.getBmoCustomer().getBmoTerritory()),
				new PmJoin(bmoQuote.getBmoCustomer().getReqPayTypeId(), bmoQuote.getBmoCustomer().getBmoReqPayType())
				)));
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();

		// Filtro por asignacion de cotizaciones 
		if (getSFParams().restrictData(bmoQuote.getProgramCode())) {

			filters = "( quot_userid in (" +
					" SELECT user_userid FROM users " +
					" WHERE " + 
					" user_userid = " + loggedUserId +
					" or user_userid in ( " +
					" 	select u2.user_userid from users u1 " +
					" left join users u2 on (u2.user_parentid = u1.user_userid) " +
					" where u1.user_userid = " + loggedUserId +
					" ) " +
					" or user_userid in ( " +
					" select u3.user_userid from users u1 " +
					" left join users u2 on (u2.user_parentid = u1.user_userid) " +
					" left join users u3 on (u3.user_parentid = u2.user_userid) " +
					" where u1.user_userid = " + loggedUserId +
					" ) " +
					" or user_userid in ( " +
					" select u4.user_userid from users u1 " +
					" left join users u2 on (u2.user_parentid = u1.user_userid) " +
					" left join users u3 on (u3.user_parentid = u2.user_userid) " +
					" left join users u4 on (u4.user_parentid = u3.user_userid) " +
					" where u1.user_userid = " + loggedUserId +
					" ) " +
					" or user_userid in ( " +
					" select u5.user_userid from users u1 " +
					" left join users u2 on (u2.user_parentid = u1.user_userid) " +
					" left join users u3 on (u3.user_parentid = u2.user_userid) " +
					" left join users u4 on (u4.user_parentid = u3.user_userid) " +
					" left join users u5 on (u5.user_parentid = u4.user_userid) " +
					" where u1.user_userid = " + loggedUserId +
					" ) " + 
					" ) " +
					" ) ";
		}

		// Filtro de oportunidades de empresas del usuario
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			if (filters.length() > 0) filters += " AND ";
			filters += " ( quot_companyid in (" +
					" select uscp_companyid from usercompanies " +
					" where " + 
					" uscp_userid = " + loggedUserId + " )"
					+ ") ";			
		}

		// Filtro de cotizaciones seleccionada
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0) filters += " AND ";
			filters += " quot_companyid = " + getSFParams().getSelectedCompanyId();
		}

		return filters;
	}	

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoQuote = (BmoQuote)autoPopulate(pmConn, new BmoQuote());

		// BmoCurrency
		BmoCurrency bmoCurrency = new BmoCurrency();
		int currencyId = (int)pmConn.getInt(bmoCurrency.getIdFieldName());
		if (currencyId > 0) bmoQuote.setBmoCurrency((BmoCurrency) new PmCurrency(getSFParams()).populate(pmConn));
		else bmoQuote.setBmoCurrency(bmoCurrency);

		// BmoOrderType
		BmoOrderType bmoOrderType = new BmoOrderType();
		int orderTypeId = (int)pmConn.getInt(bmoOrderType.getIdFieldName());
		if (orderTypeId > 0) bmoQuote.setBmoOrderType((BmoOrderType) new PmOrderType(getSFParams()).populate(pmConn));
		else bmoQuote.setBmoOrderType(bmoOrderType);

		// BmoCustomer
		BmoCustomer bmoCustomer = new BmoCustomer();
		int customerId = (int)pmConn.getInt(bmoCustomer.getIdFieldName());
		if (customerId > 0) bmoQuote.setBmoCustomer((BmoCustomer) new PmCustomer(getSFParams()).populate(pmConn));
		else bmoQuote.setBmoCustomer(bmoCustomer);

		// BmoUser
		BmoUser bmoUser = new BmoUser();
		int userId = (int)pmConn.getInt(bmoUser.getIdFieldName());
		if (userId > 0) bmoQuote.setBmoUser((BmoUser) new PmUser(getSFParams()).populate(pmConn));
		else bmoQuote.setBmoUser(bmoUser);

		return bmoQuote;
	}

	@Override
	public BmUpdateResult save(BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {	

		bmUpdateResult = super.save(bmObject, bmUpdateResult);

		// Guardar bitacora si esta autorizada la cotizacion
		if ((bmoQuote.getWFlowId().toInteger() > 0) && 
				bmoQuote.getStatus().toChar() == BmoQuote.STATUS_AUTHORIZED) {

			bmoQuote = (BmoQuote)bmUpdateResult.getBmObject();			
			addDataLog(bmoQuote, bmUpdateResult);

			// Regresar el objeto de la Cotizacion
			bmUpdateResult.setBmObject(bmoQuote);
			bmUpdateResult.setId(bmoQuote.getId());
		}

		return bmUpdateResult;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoQuote = (BmoQuote)bmObject;
		BmoQuote bmoQuotePrev = (BmoQuote)bmObject;

		// Si la cotizacion es nueva, obtener id y crear el primer grupo de la cotizacion
		if (!(bmoQuote.getId() > 0)) {
			super.save(pmConn, bmoQuote, bmUpdateResult);
			bmoQuote.setId(bmUpdateResult.getId());

			//Revisar si el tipo de Pedido maneja IVA
			if (bmoQuote.getBmoOrderType().getHasTaxes().toBoolean())
				bmoQuote.getTaxApplies().setValue(true);
			
			// Asigna clave si no tiene
			if (!(bmoQuote.getCode().toString().length() > 0))
				bmoQuote.getCode().setValue(bmoQuote.getCodeFormat());
		} else {
			PmQuote pmQuotePrev = new PmQuote(getSFParams());
			bmoQuotePrev = (BmoQuote)pmQuotePrev.get(bmoQuote.getId());
		}
		//No dejar pasar el estatus cancelado para tipo Renta(Drea), se manipula desde perder o expirar oportunidad
		if ((bmoQuote.getWFlowId().toInteger() > 0) && 	bmoQuote.getStatus().toChar() == BmoQuote.STATUS_CANCELLED 
				&& bmoQuote.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
					bmUpdateResult.addError(bmoQuote.getStatus().getName(), "Este estatus se maneja desde la Oportunidad");
		}

		// Obtener tipo de pedido
		PmOrderType pmOrderType = new PmOrderType(getSFParams());
		bmoQuote.setBmoOrderType((BmoOrderType)pmOrderType.get(pmConn, bmoQuote.getOrderTypeId().toInteger()));
		
		// Validar que si esta cambiando el mercado, verifique si hay lineas con productos
		if (bmoQuotePrev.getMarketId().toInteger() > 0) {
			if (bmoQuote.getMarketId().toInteger() != bmoQuotePrev.getMarketId().toInteger()) {
				if (existProducts(pmConn, bmoQuote.getId()))
					bmUpdateResult.addError(bmoQuote.getMarketId().getName(), "La Cotización tiene Productos con Precios asignados al(la) " + getSFParams().getFieldFormTitle(bmoQuote.getMarketId()) + " actual. "
									+ "Debe eliminarlos para poder cambiar el(la) " + getSFParams().getFieldFormTitle(bmoQuote.getMarketId()) + "." );
			}
		}
		
		// Calcular montos
		this.updateBalance(pmConn, bmoQuote, bmUpdateResult);

		// Si es de tipo renta revisar asignacion de fechas
		if (bmoQuote.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
			if (bmoQuote.getStartDate().toString().equals(""))
				bmUpdateResult.addError(bmoQuote.getStartDate().getName(), "Debe asignarse la Fecha de Inicio de la Renta.");

			if (bmoQuote.getEndDate().toString().equals(""))
				bmUpdateResult.addError(bmoQuote.getEndDate().getName(), "Debe asignarse la Fecha de Fin de la Renta.");

			// Revisa fechas
			if (!bmUpdateResult.hasErrors())
				if (SFServerUtil.isBefore(getSFParams().getDateTimeFormat(), getSFParams().getTimeZone(), 
						bmoQuote.getEndDate().toString(), bmoQuote.getStartDate().toString()))
					bmUpdateResult.addError(bmoQuote.getEndDate().getName(), 
							"No puede ser Anterior a " + bmoQuote.getStartDate().getLabel());
		} 

		// Revisar si la oportunidad esta autorizada
		try {
			PmOpportunity pmOpportunity = new PmOpportunity(getSFParams());
			BmoOpportunity bmoOpportunity = new BmoOpportunity();
			bmoOpportunity = (BmoOpportunity)pmOpportunity.getBy(pmConn, bmoQuote.getId(), bmoOpportunity.getQuoteId().getName());
			PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());

			if (!getSFParams().hasSpecialAccess(BmoOpportunity.ACCESS_CHANGEDATEINQUOTAUT)) {
				bmoQuote.getStartDate().setValue(bmoOpportunity.getStartDate().toString());
				bmoQuote.getEndDate().setValue(bmoOpportunity.getEndDate().toString());
			}
			// Si tiene permiso de cambiar la fecha inicio con cotizacion aut. colocar nueva fecha inicio
			else {
				bmoOpportunity.getStartDate().setValue(bmoQuote.getStartDate().toString());
				bmoOpportunity.getEndDate().setValue(bmoQuote.getEndDate().toString());

				// Actualizar fecha inicio y fin en el flujo
				BmoWFlow bmoWFlow = new BmoWFlow();
				PmWFlow pmWFlow = new PmWFlow(getSFParams());
				bmoWFlow = (BmoWFlow) pmWFlow.get(bmoQuote.getWFlowId().toInteger());
				
				bmoWFlow.getStartDate().setValue(bmoQuote.getStartDate().toString());
				bmoWFlow.getEndDate().setValue(bmoQuote.getEndDate().toString());
				pmWFlow.saveSimple(pmConn, bmoWFlow, bmUpdateResult);
			}
			if(!bmUpdateResult.hasErrors())
				super.save(pmConn, bmoQuote, bmUpdateResult);
			
			// Si tiene permiso de cambiar el vendedor con cotizacion aut. colocar nuevo vendedor
			if (getSFParams().hasSpecialAccess(BmoOpportunity.ACCESS_CHANGESALESMANINQUOTAUT)) {
				bmoOpportunity.getUserId().setValue(bmoQuote.getUserId().toInteger());

				// Actualizar usuario vendedor en el flujo
				BmoWFlow bmoWFlow = new BmoWFlow();
				PmWFlow pmWFlow = new PmWFlow(getSFParams());
				bmoWFlow = (BmoWFlow)pmWFlow.get(bmoQuote.getWFlowId().toInteger());
				bmoWFlow.getUserId().setValue(bmoQuote.getUserId().toInteger());
				pmWFlow.saveSimple(pmConn, bmoWFlow, bmUpdateResult);
			}
			if(!bmUpdateResult.hasErrors())
				super.save(pmConn, bmoQuote, bmUpdateResult);
		
			if (bmoOpportunity.getStatus().toChar() == BmoOpportunity.STATUS_WON)
				bmUpdateResult.addError(bmoQuote.getName().getName(), "No se puede modificar la Cotización - La Oportunidad está Ganada.");

			if (bmoOpportunity.getStatus().toChar() == BmoOpportunity.STATUS_LOST)
				bmUpdateResult.addError(bmoQuote.getName().getName(), "No se puede modificar la Cotización - La Oportunidad está Perdida.");
			
			if (bmoOpportunity.getStatus().toChar() == BmoOpportunity.STATUS_EXPIRED)
				bmUpdateResult.addError(bmoQuote.getName().getName(), "No se puede modificar la Cotización - La Oportunidad está Expirada.");
			

			// Si la cotización esta autorizada, actualizar valor estimado de la oportunidad
			if (bmoQuote.getStatus().toChar() == BmoQuote.STATUS_AUTHORIZED) {
				if (!bmoQuotePrev.getStatus().equals(BmoQuote.STATUS_AUTHORIZED)) {
					if (!(bmoOpportunity.getForeignWFlowTypeId().toInteger() > 0))
						bmUpdateResult.addError(bmoOpportunity.getForeignWFlowTypeId().getName(), "La Oportunidad no cuenta con el Tipo de Efecto(flujo)");
	
					bmoOpportunity.getAmount().setValue(bmoQuote.getAmount().toDouble());
					double quoteDownPayment = ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getQuoteDownPayment().toDouble();
					double downPayment = bmoQuote.getTotal().toDouble() * (quoteDownPayment / 100);
					bmoQuote.getDownPayment().setValue(downPayment);

					if (bmoQuote.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
						PmOpportunityDetail pmOpportunityDetail = new PmOpportunityDetail(getSFParams());
						BmoOpportunityDetail bmoOpportunityDetail = new BmoOpportunityDetail();
						bmoOpportunityDetail = (BmoOpportunityDetail)pmOpportunityDetail.getBy(pmConn, bmoOpportunity.getId(), bmoOpportunityDetail.getOpportunityId().getName());
	
						if (bmoOpportunityDetail.getDownPayment().toDouble() == 0) {
							bmoOpportunityDetail.getDownPayment().setValue(downPayment);
							pmOpportunityDetail.saveSimple(pmConn, bmoOpportunityDetail, bmUpdateResult);
						}
					}
	
					// Guardar fecha y usuario de autorizacion
					bmoQuote.getAuthorizedDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));
					bmoQuote.getAuthorizedUser().setValue(getSFParams().getLoginInfo().getUserId());
					// Limpiar fecha y usuario de cancelacion
					bmoQuote.getCancelledDate().setValue("");
					bmoQuote.getCancelledUser().setValue("");
	
					// Calcula # de autorizacion si no esta asignado
					if (!(bmoQuote.getAuthNum().toDouble() > 0))
						bmoQuote.getAuthNum().setValue(SFServerUtil.getRandomNumberFrom(5000, 9999) + "0" + (bmoQuote.getId() * SFServerUtil.getRandomNumberFrom(1, 9)) + bmoQuote.getId());
	
					// Actualizar moneda/paridad y fechas 
					//				bmoOpportunity.getStartDate().setValue(bmoQuote.getStartDate().toString());
					//				bmoOpportunity.getEndDate().setValue(bmoQuote.getEndDate().toString());
					bmoOpportunity.getCurrencyId().setValue(bmoQuote.getCurrencyId().toInteger());
					bmoOpportunity.getCurrencyParity().setValue(bmoQuote.getCurrencyParity().toDouble());
				}
				
				// Guardar datos, sino guardar solo vendedor del permiso especial
				pmOpportunity.saveSimple(pmConn, bmoOpportunity, bmUpdateResult);

			} else if (bmoQuote.getWFlowId().toInteger() > 0 && bmoQuote.getStatus().toChar() == BmoQuote.STATUS_REVISION) {
				// Limpiar fecha/usuario de cancelacion y autorizacion
				bmoQuote.getCancelledDate().setValue("");
				bmoQuote.getCancelledUser().setValue("");
				bmoQuote.getAuthorizedDate().setValue("");
				bmoQuote.getAuthorizedUser().setValue("");

				// Desactiva numero autorización
				bmoQuote.getAuthNum().setValue("");
				pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoQuote.getWFlowId().toInteger(), BmoWFlowLog.TYPE_OTHER, "La Cotización se guardó como En Revisión.");

				// Actualizar moneda/paridad y fechas 
				//				bmoOpportunity.getStartDate().setValue(bmoQuote.getStartDate().toString());
				//				bmoOpportunity.getEndDate().setValue(bmoQuote.getEndDate().toString());
				bmoOpportunity.getCurrencyId().setValue(bmoQuote.getCurrencyId().toInteger());
				bmoOpportunity.getCurrencyParity().setValue(bmoQuote.getCurrencyParity().toDouble());
				bmoOpportunity.getPayConditionId().setValue(bmoQuote.getPayConditionId().toInteger());
				bmoOpportunity.getMarketId().setValue(bmoQuote.getMarketId().toInteger());

				pmOpportunity.saveSimple(pmConn, bmoOpportunity, bmUpdateResult);

				if (bmoQuote.getDiscount().toDouble() > 0 && bmoQuote.getDiscount().toDouble() != bmoQuotePrev.getDiscount().toDouble()) {
					pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoQuote.getWFlowId().toInteger(),
								BmoWFlowLog.TYPE_OTHER, "Descuento aplicado: " + bmoQuote.getDiscount().toDouble());
				}
			} else if (bmoQuote.getWFlowId().toInteger() > 0 && bmoQuote.getStatus().toChar() == BmoQuote.STATUS_CANCELLED) {
				// Guardar fecha de cancelacion y limpia autorizacion
				bmoQuote.getCancelledDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));
				bmoQuote.getCancelledUser().setValue(getSFParams().getLoginInfo().getUserId());
				bmoQuote.getAuthorizedDate().setValue("");
				bmoQuote.getAuthorizedUser().setValue("");

				// Desactiva numero autorización
				bmoQuote.getAuthNum().setValue("");
				pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoQuote.getWFlowId().toInteger(), BmoWFlowLog.TYPE_OTHER, "La Cotización se guardó como Cancelada.");
			}
		} catch (SFException e) {
			System.out.println(this.getClass().getName() + "-save(): No existe alguna Oportunidad ligada a esta Cotización: " + e.toString());
		}

		// Se guarda al final
		super.save(pmConn, bmoQuote, bmUpdateResult);

		printDevLog("Se hizo el ultimo save");

		return bmUpdateResult;
	}

	public void updateBalance(PmConn pmConn, BmoQuote bmoQuote, BmUpdateResult bmUpdateResult) throws SFException {
		double quoteItemAmount = 0, quoteEquipmentAmount = 0, quoteStaffAmount = 0, 
				quotePropertyAmount = 0, quotePropertyModelExtraAmount = 0, amount = 0;
		//Drea
		double comission = 0.0,feeProduction = 0.0;
		String sql = "";

		// Suma los items de los grupos de la cotizacion
		if (bmoQuote.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)
				|| bmoQuote.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CONSULTANCY)) {
			pmConn.doFetch("SELECT SUM(qogr_amount) FROM quotegroups "
					+ " WHERE qogr_quoteid = " + bmoQuote.getId());
			if (pmConn.next()) quoteItemAmount = pmConn.getDouble(1);
			 System.err.println(this.getClass().getName() + "-calculateAmount() SQL: " + sql);
		}
		
		//Calculo de total para drea(campo total en lugar de subtotal)total = amount * days o subtotal
		
		if (bmoQuote.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {		
			pmConn.doFetch("SELECT qogr_amount AS sumAmount,qogr_total AS sumTotal,qogr_iskit,qogr_comission,qogr_feeproduction FROM quotegroups "
					+ " WHERE qogr_quoteid = " + bmoQuote.getId() );
			while (pmConn.next()) {
				if (pmConn.getInt("qogr_iskit") > 0)
					quoteItemAmount += pmConn.getDouble("sumTotal");
				else
					quoteItemAmount += pmConn.getDouble("sumAmount");
				
				comission += pmConn.getDouble("qogr_comission");
				feeProduction += pmConn.getDouble("qogr_feeproduction");
			}
			 System.out.println(this.getClass().getName() + "-calculateAmount() SQL: " + sql);
		}

		// Suma de recursos
//		if (bmoQuote.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
//			pmConn.doFetch("SELECT SUM(qoeq_amount) FROM quoteequipments "
//					+ " WHERE qoeq_quoteid = " + bmoQuote.getId());
//			if (pmConn.next()) quoteEquipmentAmount = pmConn.getDouble(1);
//			if (!getSFParams().isProduction()) System.out.println(this.getClass().getName() + "-calculateAmount() SQL: " + sql);
//
//			// Suma de personal
//			pmConn.doFetch("SELECT SUM(qost_amount) FROM quotestaff "
//					+ " WHERE qost_quoteid = " + bmoQuote.getId());
//			if (pmConn.next()) quoteStaffAmount = pmConn.getDouble(1);
//			if (!getSFParams().isProduction()) System.out.println(this.getClass().getName() + "-calculateAmount() SQL: " + sql);
//		}

		// Suma de inmuebles
		if (bmoQuote.getBmoOrderType().getType().equals(BmoOrderType.TYPE_PROPERTY)) {
			sql = "SELECT SUM(qupy_amount) FROM quoteproperties "
					+ " WHERE qupy_quoteid = " + bmoQuote.getId();
			pmConn.doFetch(sql);
			if (pmConn.next()) quotePropertyAmount = pmConn.getDouble(1);
			if (!getSFParams().isProduction()) System.out.println(this.getClass().getName() + "-calculateAmount() SQL: " + sql);

			sql = "SELECT SUM(qupx_amount) FROM quotepropertymodelextras "
					+ " WHERE qupx_quoteid = " + bmoQuote.getId();
			pmConn.doFetch(sql);
			if (pmConn.next()) quotePropertyModelExtraAmount = pmConn.getDouble(1);
			if (!getSFParams().isProduction()) System.out.println(this.getClass().getName() + "-calculateAmount() SQL: " + sql);
		}

		amount = quoteItemAmount + quoteEquipmentAmount + quoteStaffAmount + quotePropertyAmount + quotePropertyModelExtraAmount;
		if (bmoQuote.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
			amount = amount + feeProduction - comission;
		}
		// Calcular montos
		if (amount == 0) {
			bmoQuote.getAmount().setValue(0);
			bmoQuote.getDiscount().setValue(0);
			bmoQuote.getTax().setValue(0);
			bmoQuote.getTotal().setValue(0);
		} else {
			bmoQuote.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(amount));
			if (bmoQuote.getDiscount().toString().equals("")) bmoQuote.getDiscount().setValue(0);

			double tax = 0;
			double total = 0;
			
			double taxRate = ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getTax().toDouble() / 100;				
			if (bmoQuote.getTaxApplies().toBoolean()) tax = (bmoQuote.getAmount().toDouble() - bmoQuote.getDiscount().toDouble()) * taxRate;		
				total = bmoQuote.getAmount().toDouble() - bmoQuote.getDiscount().toDouble() + tax;
			

			bmoQuote.getTax().setValue(SFServerUtil.roundCurrencyDecimals(tax));
			bmoQuote.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(total));
		}

		super.save(pmConn, bmoQuote, bmUpdateResult);
	}

	@Override
	public BmUpdateResult action(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {
		// Actualiza datos de la cotización
		bmoQuote = (BmoQuote)this.get(pmConn, bmObject.getId());

		// Acción de envío de email
		if (action.equals(BmoQuote.ACTION_SENDQUOTEEMAIL)) {
			if (bmoQuote.getStatus().toChar() == BmoQuote.STATUS_AUTHORIZED) {
				sendQuoteMail(bmoQuote);
				addLog(bmUpdateResult, "Cotización enviada por Correo Electrónico.");
			} else {
				bmUpdateResult.addMsg("No está Autorizada.");
			}
		} else if (action.equals(BmoQuote.ACTION_COPYQUOTE)) {
			int fromQuoteId = Integer.parseInt(value);
			copyQuoteItemsFromQuote(pmConn, fromQuoteId, bmoQuote, bmUpdateResult);
		} else if (action.equals(BmoQuote.ACTION_LOCKEDQUANTITY)) {
			BmoQuoteItem bmoQuoteItem = new BmoQuoteItem();
			bmoQuoteItem.getQuantity().setValue(getLockedQuantity(bmoQuote, value));
			bmoQuoteItem.getProductId().setValue(value);
			bmUpdateResult.setBmObject(bmoQuoteItem);
		} else if (action.equals(BmoQuote.ACTION_QUOTESTAFFQUANTITY)) {
			double result = getQuoteStaffQuantity(bmoQuote.getId(), value);		
			bmUpdateResult.setMsg("" + result);
		}

		return bmUpdateResult;
	}

	// Copiar los items de otra cotización
	public void copyQuoteItemsFromQuote(PmConn pmConn, int fromQuoteId, BmoQuote toBmoQuote, BmUpdateResult bmUpdateResult) throws SFException {

		// Obtener cotización base
		PmQuote pmQuote = new PmQuote(getSFParams());
		BmoQuote fromBmoQuote = new BmoQuote();
		fromBmoQuote = (BmoQuote)pmQuote.get(pmConn, fromQuoteId);

		// Filtro de items de la cotización por grupo de cotización
		PmQuoteGroup pmQuoteGroup = new PmQuoteGroup(getSFParams());
		BmoQuoteGroup fromBmoQuoteGroup = new BmoQuoteGroup();
		BmFilter byQuoteGroupFilter = new BmFilter();
		byQuoteGroupFilter.setValueFilter(fromBmoQuoteGroup.getKind(), fromBmoQuoteGroup.getQuoteId().getName(), fromQuoteId);

		// Crear los grupos del pedido
		Iterator<BmObject> quoteGroupIterator = pmQuoteGroup.list(pmConn, byQuoteGroupFilter).iterator();
		while (quoteGroupIterator.hasNext()) {
			fromBmoQuoteGroup = (BmoQuoteGroup)quoteGroupIterator.next();

			BmoQuoteGroup toBmoQuoteGroup = new BmoQuoteGroup();
			toBmoQuoteGroup.getName().setValue(fromBmoQuoteGroup.getName().toString());
			toBmoQuoteGroup.getDescription().setValue(fromBmoQuoteGroup.getDescription().toString());
			toBmoQuoteGroup.getAmount().setValue(fromBmoQuoteGroup.getAmount().toDouble());
			toBmoQuoteGroup.getIsKit().setValue(fromBmoQuoteGroup.getIsKit().toBoolean());
			toBmoQuoteGroup.getPayConditionId().setValue(fromBmoQuoteGroup.getPayConditionId().toInteger());
			toBmoQuoteGroup.getIndex().setValue(fromBmoQuoteGroup.getIndex().toInteger());
			toBmoQuoteGroup.getQuoteId().setValue(toBmoQuote.getId());			

			pmQuoteGroup.saveSimple(pmConn, toBmoQuoteGroup, bmUpdateResult);
			// Obten el ultimo ID generado, que es el del grupo de pedido
			int toQuoteGroupId = bmUpdateResult.getId();

			// Filtro de items de la cotización por Grupo de Cotizacion
			PmQuoteItem pmQuoteItem = new PmQuoteItem(getSFParams());
			BmoQuoteItem fromBmoQuoteItem = new BmoQuoteItem();
			BmFilter byQuoteFilter = new BmFilter();
			byQuoteFilter.setValueFilter(fromBmoQuoteItem.getKind(), fromBmoQuoteItem.getQuoteGroupId().getName(), fromBmoQuoteGroup.getId());

			Iterator<BmObject> quoteItemIterator = pmQuoteItem.list(pmConn, byQuoteFilter).iterator();
			while (quoteItemIterator.hasNext()) {
				fromBmoQuoteItem = (BmoQuoteItem)quoteItemIterator.next();

				BmoQuoteItem toBmoQuoteItem = new BmoQuoteItem();
				toBmoQuoteItem.getProductId().setValue(fromBmoQuoteItem.getProductId().toInteger());
				toBmoQuoteItem.getName().setValue(fromBmoQuoteItem.getName().toString());
				toBmoQuoteItem.getDescription().setValue(fromBmoQuoteItem.getDescription().toString());
				toBmoQuoteItem.getQuantity().setValue(fromBmoQuoteItem.getQuantity().toDouble());
				toBmoQuoteItem.getDays().setValue(fromBmoQuoteItem.getDays().toDouble());
				toBmoQuoteItem.getBasePrice().setValue(fromBmoQuoteItem.getBasePrice().toDouble());
				toBmoQuoteItem.getPrice().setValue(fromBmoQuoteItem.getPrice().toDouble());
				toBmoQuoteItem.getAmount().setValue(fromBmoQuoteItem.getAmount().toDouble());
				toBmoQuoteItem.getIndex().setValue(fromBmoQuoteItem.getIndex().toInteger());

				if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
					toBmoQuoteItem.getBudgetItemId().setValue(fromBmoQuoteItem.getBudgetItemId().toInteger());
					toBmoQuoteItem.getAreaId().setValue(fromBmoQuoteItem.getAreaId().toInteger());
				}
				toBmoQuoteItem.getQuoteGroupId().setValue(toQuoteGroupId);

				pmQuoteItem.saveSimple(pmConn, toBmoQuoteItem, bmUpdateResult);
			}
		}

		// Lista de recursos
//		PmQuoteEquipment pmQuoteEquipment = new PmQuoteEquipment(getSFParams());
//		BmoQuoteEquipment fromBmoQuoteEquipment = new BmoQuoteEquipment();
//		BmFilter byQuoteFilter = new BmFilter();
//		byQuoteFilter.setValueFilter(fromBmoQuoteEquipment.getKind(), fromBmoQuoteEquipment.getQuoteId().getName(), fromQuoteId);
//		Iterator<BmObject> quoteEquipmentIterator = pmQuoteEquipment.list(pmConn, byQuoteFilter).iterator();
//		while (quoteEquipmentIterator.hasNext()) {
//			fromBmoQuoteEquipment = (BmoQuoteEquipment)quoteEquipmentIterator.next();
//
//			BmoQuoteEquipment toBmoQuoteEquipment = new BmoQuoteEquipment();
//			toBmoQuoteEquipment.getName().setValue(fromBmoQuoteEquipment.getName().toString());
//			toBmoQuoteEquipment.getQuantity().setValue(fromBmoQuoteEquipment.getQuantity().toInteger());
//			toBmoQuoteEquipment.getDays().setValue(fromBmoQuoteEquipment.getDays().toDouble());
//			toBmoQuoteEquipment.getBasePrice().setValue(fromBmoQuoteEquipment.getBasePrice().toDouble());
//			toBmoQuoteEquipment.getPrice().setValue(fromBmoQuoteEquipment.getPrice().toDouble());
//			toBmoQuoteEquipment.getAmount().setValue(fromBmoQuoteEquipment.getAmount().toDouble());
//			toBmoQuoteEquipment.getEquipmentId().setValue(fromBmoQuoteEquipment.getEquipmentId().toInteger());
//			toBmoQuoteEquipment.getQuoteId().setValue(toBmoQuote.getId());
//
//			pmQuoteEquipment.saveSimple(pmConn, toBmoQuoteEquipment, bmUpdateResult);
//		}
//
//		// Lista de personal
//		PmQuoteStaff pmQuoteStaff = new PmQuoteStaff(getSFParams());
//		BmoQuoteStaff fromBmoQuoteStaff = new BmoQuoteStaff();
//		byQuoteFilter.setValueFilter(fromBmoQuoteStaff.getKind(), fromBmoQuoteStaff.getQuoteId().getName(), fromQuoteId);
//		Iterator<BmObject> quoteStaffIterator = pmQuoteStaff.list(pmConn, byQuoteFilter).iterator();
//		while (quoteStaffIterator.hasNext()) {
//			fromBmoQuoteStaff = (BmoQuoteStaff)quoteStaffIterator.next();
//
//			BmoQuoteStaff toBmoQuoteStaff = new BmoQuoteStaff();
//			toBmoQuoteStaff.getName().setValue(fromBmoQuoteStaff.getName().toString());
//			toBmoQuoteStaff.getDescription().setValue(fromBmoQuoteStaff.getDescription().toString());
//			toBmoQuoteStaff.getQuantity().setValue(fromBmoQuoteStaff.getQuantity().toInteger());
//			toBmoQuoteStaff.getDays().setValue(fromBmoQuoteStaff.getDays().toDouble());
//			toBmoQuoteStaff.getBasePrice().setValue(fromBmoQuoteStaff.getBasePrice().toDouble());
//			toBmoQuoteStaff.getPrice().setValue(fromBmoQuoteStaff.getPrice().toDouble());
//			toBmoQuoteStaff.getAmount().setValue(fromBmoQuoteStaff.getAmount().toDouble());
//			toBmoQuoteStaff.getQuoteId().setValue(toBmoQuote.getId());
//			toBmoQuoteStaff.getProfileId().setValue(fromBmoQuoteStaff.getProfileId().toInteger());
//
//			pmQuoteStaff.saveSimple(pmConn, toBmoQuoteStaff, bmUpdateResult);
//		}

		// Traer descuento
		toBmoQuote.getDiscount().setValue(fromBmoQuote.getDiscount().toDouble());

		// Aplica IVA
		if (fromBmoQuote.getTax().toDouble() > 0) 
			toBmoQuote.getTaxApplies().setValue(true);
		else 
			toBmoQuote.getTaxApplies().setValue(false);

		// Otros valores de mostrar valores
		toBmoQuote.getShowEquipmentQuantity().setValue(fromBmoQuote.getShowEquipmentQuantity().getValue());
		toBmoQuote.getShowEquipmentPrice().setValue(fromBmoQuote.getShowEquipmentPrice().getValue());
		toBmoQuote.getShowStaffQuantity().setValue(fromBmoQuote.getShowStaffQuantity().getValue());
		toBmoQuote.getShowStaffPrice().setValue(fromBmoQuote.getShowStaffPrice().getValue());

		// Actualizar valor del pedido
		this.updateBalance(pmConn, toBmoQuote, bmUpdateResult);

		// Se guarda en bitácora que fue copiada
		this.addLog(fromBmoQuote, toBmoQuote,  bmUpdateResult);
		
	}

	public void sendQuoteMail(BmoQuote bmoQuote) throws SFException {
		ArrayList<SFMailAddress> mailList = new ArrayList<SFMailAddress>();

		// Datos de la oportunidad
		//		PmOpportunity pmOpportunity = new PmOpportunity(getSFParams());
		//		BmoOpportunity bmoOpportunity = (BmoOpportunity)pmOpportunity.get(bmoQuote.getOpportunityId().toInteger());

		// Datos del correos del cliente
		PmCustomerEmail pmCustomerEmail = new PmCustomerEmail(getSFParams());
		BmoCustomerEmail bmoCustomerEmail = new BmoCustomerEmail();
		BmFilter customerFilter = new BmFilter();
		customerFilter.setValueFilter(bmoCustomerEmail.getKind(), bmoCustomerEmail.getCustomerId(), bmoQuote.getCustomerId().toInteger());
		ArrayList<BmObject> customerEmailList = pmCustomerEmail.list(customerFilter);
		Iterator<BmObject> i = customerEmailList.iterator();
		int index = 0;
		while (i.hasNext()) {
			bmoCustomerEmail = (BmoCustomerEmail)i.next();
			SFMailAddress mailAddress = new SFMailAddress(bmoCustomerEmail.getEmail().toString(),
					bmoQuote.getBmoCustomer().getFirstname().toString() + " " +
							bmoQuote.getBmoCustomer().getFatherlastname().toString());
			mailList.add(mailAddress);
			index++;
		}
		if (index == 0) throw new SFException("El cliente no tiene cuentas de correo asignadas.");

		// Datos del usuario que está enviando el correo
		mailList.add(new SFMailAddress(getSFParams().getLoginInfo().getEmailAddress(),
				getSFParams().getLoginInfo().getEmailAddress()));

		// Datos del productor del proyecto siempre y cuando este activo el vendedor
		if (bmoQuote.getBmoUser().getStatus().toChar() == BmoUser.STATUS_ACTIVE) {
			mailList.add(new SFMailAddress(bmoQuote.getBmoUser().getEmail().toString(),
					bmoQuote.getBmoUser().getFirstname().toString() + " " +
							bmoQuote.getBmoUser().getFatherlastname().toString()
					));
		}

		String subject = "#" + getSFParams().getAppCode()  + bmoQuote.getCode().toString() + " - Cotización ";
		String quoteQuery = "<a href=\"" + getSFParams().getAppURL() + 
				"/frm/flex_basequote_ext.jsp?h=" + new Date().getTime() + "quote&w=EXT&z=" + 
				SFServerUtil.encryptId(bmoQuote.getId()) + "&r=proj" + (new Date().getTime() * 456) +"\">Aqui</a>";

		String msgBody = HtmlUtil.mailBodyFormat(getSFParams(), subject, 
				" Se ha elaborado y autorizado la Cotizacion para el Proyecto " + bmoQuote.getCode().toString() + " - " +
						bmoQuote.getName().toString() + ". " + 
						" Puede revisar la Cotizacion en el Sistema " + getSFParams().getMainAppTitle() + ". " +
						" Para visualizarla haz click " + quoteQuery + ".<br><br>" +
				" No es necesario contestar (Reply) este Correo. ");

		// Si hay destinatarios, enviar los correos
		if (mailList.size() > 0) SFSendMail.send(getSFParams(),
				mailList, 
				getSFParams().getBmoSFConfig().getEmail().toString(), 
				getSFParams().getMainAppTitle(), 
				subject, 
				msgBody);
	}

	// Obtener utilizacion en intervalo de fechas
	private double getLockedQuantity(BmoQuote bmoQuote, String productId) throws SFException{
		double lockedQuantity = 0;

		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();

		// Revisar asignaciones en las fechas establecidas de los items en cotizacion
		String lockedSql = "SELECT SUM(qoit_quantity) FROM quoteitems"
				+ " LEFT JOIN quotegroups ON (qogr_quotegroupid = qoit_quotegroupid) "
				+ " LEFT JOIN quotes ON (quot_quoteid = qogr_quoteid)"
				+ " WHERE "
				+ " qoit_productid = " + productId
				+ " AND ("
				+ "		('" + bmoQuote.getStartDate().toString() + "' BETWEEN quot_startdate AND quot_enddate)"
				+ "		OR"
				+ "		('" + bmoQuote.getEndDate().toString() + "' BETWEEN quot_startdate AND quot_enddate)"
				+ "		)"
				+ " AND quot_companyid = " + bmoQuote.getCompanyId().toInteger();
		pmConn.doFetch(lockedSql);
		pmConn.next();
		lockedQuantity = pmConn.getDouble(1);
		pmConn.close();

		return lockedQuantity;	
	}

	private void addLog(BmUpdateResult bmUpdateResult, String value) throws SFException {
		PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());
		pmWFlowLog.addLog(bmUpdateResult, bmoQuote.getWFlowId().toInteger(), BmoWFlowLog.TYPE_DATA, value);
	}

	// Genera bitacora de la copia de items de otra cotizacion
	private void addLog(BmoQuote fromBmoQuote, BmoQuote bmoQuote, BmUpdateResult bmUpdateResult) throws SFException {		
		PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());

		if (bmoQuote.getWFlowId().toInteger() > 0)
			pmWFlowLog.addLog(bmUpdateResult, bmoQuote.getWFlowId().toInteger(),  
					BmoWFlowLog.TYPE_OTHER, "Copia de Items Exitosa. Cotización '" + fromBmoQuote.getCode().toString() + " " + fromBmoQuote.getName().toString() + "' copiada ");
	}

	private void addDataLog(BmoQuote bmoQuote, BmUpdateResult bmUpdateResult) throws SFException {	
		PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());

		// Moneda con la que se esta autorizando
		BmoCurrency bmoCurrencyAuthorized = new BmoCurrency();
		PmCurrency pmCurrencyAuthorized = new PmCurrency(getSFParams());
		bmoCurrencyAuthorized = (BmoCurrency)pmCurrencyAuthorized.get(bmoQuote.getCurrencyId().toInteger());

		// Formato de Pedido por defecto
		BmoFormat bmoFormat = new BmoFormat();
		PmFormat pmFormat = new PmFormat(getSFParams());

		// Oportunidad
		PmOpportunity pmOpportunity = new PmOpportunity(getSFParams());
		BmoOpportunity bmoOpportunity = new BmoOpportunity();
		bmoOpportunity = (BmoOpportunity)pmOpportunity.getBy(bmoQuote.getId(), bmoOpportunity.getQuoteId().getName());

		String link = "";
		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getDefaultFormatOpportunity().toInteger() > 0) {
			bmoFormat = (BmoFormat)pmFormat.get(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getDefaultFormatOpportunity().toInteger());
			link = bmoFormat.getLink().toString();
		} else link = "/frm/flex_basequote.jsp";

		if (bmoQuote.getStatus().equals(BmoQuote.STATUS_AUTHORIZED)) {
			// Obtener html del formato
			// log - indica que se va a mostrar valores que se ocultan en el formato en la bitacora(html)
			// (ej. mostrar cantidad, mostrar dias, mostrar precio, mostrar subtotal) 

			
			String url = GwtUtil.getProperUrl(getSFParams(), link) + "?h=" + new Date().getTime() + "format&w=EXT&z=" + 
					GwtUtil.encryptId(bmoOpportunity.getId()) + "&resource=oppo" + (new Date().getTime() * 456) + "&log=true";

			String data = SFServerUtil.fetchUrlToString(url);

			System.out.println("URL Cotizacion: " + url);

			pmWFlowLog.addDataLog(bmUpdateResult, bmoQuote.getWFlowId().toInteger(), BmoWFlowLog.TYPE_DATA, 
					"Autorización de la Cotización (#" + bmoQuote.getAuthNum().toString() + "). Valor Total: " +
							SFServerUtil.formatCurrency(bmoQuote.getTotal().toDouble()) + " " +
							bmoCurrencyAuthorized.getCode().toString(), 
							data);
		}

		// Obtener cotizacion
		//		String url = getSFParams().getAppURL() + "frm/flex_basequote.jsp" + "?h=" + new Date().getTime() + "format&w=EXT&z=" + 
		//				GwtUtil.encryptId(bmoQuote.getId()) + "&resource=oppo" + (new Date().getTime() * 456);
		//		String data = SFServerUtil.fetchUrlToString(url);


	}

	public void addDataLog(PmConn pmConn, BmoQuote bmoQuote, int wFlowId, BmUpdateResult bmUpdateResult) throws SFException {
		PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());

		// Moneda con la que se esta autorizando
		BmoCurrency bmoCurrencyAuthorized = new BmoCurrency();
		PmCurrency pmCurrencyAuthorized = new PmCurrency(getSFParams());
		bmoCurrencyAuthorized = (BmoCurrency)pmCurrencyAuthorized.get(bmoQuote.getCurrencyId().toInteger());

		// Formato de Pedido por defecto
		BmoFormat bmoFormat = new BmoFormat();
		PmFormat pmFormat = new PmFormat(getSFParams());

		// Oportunidad
		PmOpportunity pmOpportunity = new PmOpportunity(getSFParams());
		BmoOpportunity bmoOpportunity = new BmoOpportunity();
		bmoOpportunity = (BmoOpportunity)pmOpportunity.getBy(bmoQuote.getId(), bmoOpportunity.getQuoteId().getName());

		String link = "";
		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getDefaultFormatOpportunity().toInteger() > 0) {
			bmoFormat = (BmoFormat)pmFormat.get(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getDefaultFormatOpportunity().toInteger());
			link = bmoFormat.getLink().toString();
		} else link = "/frm/flex_basequote.jsp";

		if (bmoQuote.getStatus().equals(BmoQuote.STATUS_AUTHORIZED)) {

			// Obtener html del formato
			// log - indica que se va a mostrar valores que se ocultan en el formato en la bitacora(html)
			// (ej. mostrar cantidad, mostrar dias, mostrar precio, mostrar subtotal) 

			String url = GwtUtil.getProperUrl(getSFParams(), link) + "?h=" + new Date().getTime() + "format&w=EXT&z=" + 
					GwtUtil.encryptId(bmoOpportunity.getId()) + "&resource=oppo" + (new Date().getTime() * 456) + "&log=true";

			String data = SFServerUtil.fetchUrlToString(url);

			System.out.println("URL Cotizacion: " + url);

			pmWFlowLog.addDataLog(pmConn, bmUpdateResult, wFlowId, BmoWFlowLog.TYPE_DATA, 
					"Autorización de la Cotización (#" + bmoQuote.getAuthNum().toString() + "). Valor Total: " + 
							SFServerUtil.formatCurrency(bmoQuote.getTotal().toDouble()) + " " +
							bmoCurrencyAuthorized.getCode().toString(), 
							data);
		}


		//		// Obtener cotizacion
		//		String url = getSFParams().getAppURL() + "frm/flex_basequote.jsp" + "?h=" + new Date().getTime() + "format&w=EXT&z=" + 
		//				GwtUtil.encryptId(bmoQuote.getId()) + "&resource=oppo" + (new Date().getTime() * 456);
		//		String data = SFServerUtil.fetchUrlToString(url);
		//
		//		System.out.println("URL Cotizacion: " + url);

	}

	public int getQuoteStaffQuantity(int quoteId, String profileId) throws SFException {
		PmConn pmConn = new PmConn(getSFParams());
		int quantity = 0;
		try {
			pmConn.open();

			quantity = getQuoteStaffQuantity(pmConn, quoteId, profileId);

		} catch (SFException e) {
			System.out.println(this.getClass().getName() + "-quoteStaffQuantity() ERROR: " + e.toString());
		} finally {
			pmConn.close();
		}
		return quantity;
	}

	// Obtene la cantidad de personal de un grupo de la cotización
	public int getQuoteStaffQuantity(PmConn pmConn, int quoteId, String profileId) throws SFException {	
		int quantity = 0;

		String sql = "SELECT SUM(qost_quantity) FROM quotestaff"
				+ " WHERE qost_quoteid = " + quoteId + " "
				+ " AND qost_profileid = " + profileId;
		pmConn.doFetch(sql);
		if (pmConn.next()) quantity = pmConn.getInt(1);

		return quantity;
	}
	
	// Existen lineas de PRODUCTOS en la cotizacion?
	public boolean existProducts(PmConn pmConn, int quoteId) throws SFException {
		boolean existProducts = false;
		String sql = "SELECT qoit_quoteitemid FROM quoteitems "
				+ " LEFT JOIN quotegroups ON (qogr_quotegroupid = qoit_quotegroupid) "
				+ " WHERE qogr_quoteid = " + quoteId
				+ " AND qoit_productid > 0 ";
		pmConn.doFetch(sql);
		if (pmConn.next() ) existProducts = true;
		
		return existProducts;
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoQuote = (BmoQuote)bmObject;

		if (bmoQuote.getStatus().toChar() == BmoQuote.STATUS_REVISION) {
			pmConn.doUpdate("DELETE FROM quoteitems "
					+ " WHERE qoit_quotegroupid IN ("
					+ " SELECT qogr_quotegroupid FROM quotegroups WHERE qogr_quoteid = " + bmoQuote.getId() + ")");
			pmConn.doUpdate("DELETE FROM quotegroups WHERE qogr_quoteid = " + bmoQuote.getId());
			pmConn.doUpdate("DELETE FROM quoteequipments WHERE qoeq_quoteid = " + bmoQuote.getId());
			pmConn.doUpdate("DELETE FROM quotestaff WHERE qost_quoteid = " + bmoQuote.getId());

			super.delete(pmConn, bmoQuote, bmUpdateResult);
		} else {
			bmUpdateResult.addMsg("No se puede Eliminar la Cotización -  está Autorizada.");
		}

		return bmUpdateResult;
	}
}
