/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.op;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import com.flexwm.server.fi.PmCurrency;
import com.flexwm.server.fi.PmFiscalPeriod;
import com.flexwm.server.fi.PmPaccount;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.op.BmoRequisition;
import com.flexwm.shared.op.BmoRequisitionItem;
import com.flexwm.shared.op.BmoRequisitionReceipt;
import com.flexwm.shared.op.BmoRequisitionReceiptItem;
import com.flexwm.shared.op.BmoRequisitionType;
import com.flexwm.shared.op.BmoWhMovement;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.SFServerUtil;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;



public class PmRequisitionReceipt extends PmObject {
	BmoRequisitionReceipt bmoRequisitionReceipt;

	public PmRequisitionReceipt(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoRequisitionReceipt = new BmoRequisitionReceipt();
		setBmObject(bmoRequisitionReceipt);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoRequisitionReceipt.getRequisitionId(), bmoRequisitionReceipt.getBmoRequisition()),
				new PmJoin(bmoRequisitionReceipt.getBmoRequisition().getCompanyId(), bmoRequisitionReceipt.getBmoRequisition().getBmoCompany()),
				new PmJoin(bmoRequisitionReceipt.getBmoRequisition().getCurrencyId(), bmoRequisitionReceipt.getBmoRequisition().getBmoCurrency()),
				new PmJoin(bmoRequisitionReceipt.getBmoRequisition().getSupplierId(), bmoRequisitionReceipt.getBmoRequisition().getBmoSupplier()),
				new PmJoin(bmoRequisitionReceipt.getBmoRequisition().getBmoSupplier().getSupplierCategoryId(), bmoRequisitionReceipt.getBmoRequisition().getBmoSupplier().getBmoSupplierCategory()),
				new PmJoin(bmoRequisitionReceipt.getBmoRequisition().getReqPayTypeId(), bmoRequisitionReceipt.getBmoRequisition().getBmoReqPayType()),
				new PmJoin(bmoRequisitionReceipt.getBmoRequisition().getRequisitionTypeId(), bmoRequisitionReceipt.getBmoRequisition().getBmoRequisitionType())
				)));
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();
		int loggedUserAreaId = getSFParams().getLoginInfo().getBmoUser().getAreaId().toInteger();

		// Filtro muestra las OC que solicito el usuario y sus subordinados
		if (getSFParams().restrictData(bmoRequisitionReceipt.getProgramCode())) {

			// Solicitado Por de la OC
			filters = " ( reqi_requestedby IN " +
					" ( " +
					" SELECT user_userid FROM users " +
					" WHERE " + 
					" user_userid = " + loggedUserId +
					" OR user_userid IN ( " +
					" SELECT u2.user_userid FROM users u1 " +
					" LEFT JOIN users u2 ON (u2.user_parentid = u1.user_userid) " +
					" WHERE u1.user_userid = " + loggedUserId +
					" ) " +
					" OR user_userid IN ( " +
					" SELECT u3.user_userid FROM users u1 " +
					" LEFT JOIN users u2 ON (u2.user_parentid = u1.user_userid) " +
					" LEFT JOIN users u3 ON (u3.user_parentid = u2.user_userid) " +
					" WHERE u1.user_userid = " + loggedUserId +
					" ) " +
					" OR user_userid IN ( " +
					" SELECT u4.user_userid FROM users u1 " +
					" LEFT JOIN users u2 ON (u2.user_parentid = u1.user_userid) " +
					" LEFT JOIN users u3 ON (u3.user_parentid = u2.user_userid) " +
					" LEFT JOIN users u4 ON (u4.user_parentid = u3.user_userid) " +
					" WHERE u1.user_userid = " + loggedUserId +
					" ) " +
					" OR user_userid IN ( " +
					" SELECT u5.user_userid FROM users u1 " +
					" LEFT JOIN users u2 ON (u2.user_parentid = u1.user_userid) " +
					" LEFT JOIN users u3 ON (u3.user_parentid = u2.user_userid) " +
					" LEFT JOIN users u4 ON (u4.user_parentid = u3.user_userid) " +
					" LEFT JOIN users u5 ON (u5.user_parentid = u4.user_userid) " +
					" WHERE u1.user_userid = " + loggedUserId +
					" ) " + 

					// Usuario que lo creo de OC
					" OR ( reqi_usercreateid IN " +
					" ( " +
					" SELECT user_userid FROM users " +
					" WHERE " + 
					" user_userid = " + loggedUserId +
					" OR user_userid IN ( " +
					" SELECT u2.user_userid FROM users u1 " +
					" LEFT JOIN users u2 ON (u2.user_parentid = u1.user_userid) " +
					" WHERE u1.user_userid = " + loggedUserId +
					" ) " +
					" OR user_userid IN ( " +
					" SELECT u3.user_userid FROM users u1 " +
					" LEFT JOIN users u2 ON (u2.user_parentid = u1.user_userid) " +
					" LEFT JOIN users u3 ON (u3.user_parentid = u2.user_userid) " +
					" WHERE u1.user_userid = " + loggedUserId +
					" ) " +
					" OR user_userid IN ( " +
					" SELECT u4.user_userid FROM users u1 " +
					" LEFT JOIN users u2 ON (u2.user_parentid = u1.user_userid) " +
					" LEFT JOIN users u3 ON (u3.user_parentid = u2.user_userid) " +
					" LEFT JOIN users u4 ON (u4.user_parentid = u3.user_userid) " +
					" WHERE u1.user_userid = " + loggedUserId +
					" ) " +
					" OR user_userid IN ( " +
					" SELECT u5.user_userid FROM users u1 " +
					" LEFT JOIN users u2 ON (u2.user_parentid = u1.user_userid) " +
					" LEFT JOIN users u3 ON (u3.user_parentid = u2.user_userid) " +
					" LEFT JOIN users u4 ON (u4.user_parentid = u3.user_userid) " +
					" LEFT JOIN users u5 ON (u5.user_parentid = u4.user_userid) " +
					" WHERE u1.user_userid = " + loggedUserId +
					" ) " + 
					" ) " +
					" ) " +

					// Por departamento de OC
					" OR  ( " +
					" reqi_areaid = " + loggedUserAreaId + 
					" ) " +

					" ) " +
					" ) ";
		}

		// Filtro de OC de empresas del usuario
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			if (filters.length() > 0) filters += " AND ";
			filters += " ( rerc_companyid IN ( " +
					" SELECT uscp_companyid FROM usercompanies " +
					" WHERE " + 
					" uscp_userid = " + loggedUserId + " ) "
					+ " ) ";
		}

		// Filtro de empresa seleccionada
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0) filters += " AND ";
			filters += " rerc_companyid = " + getSFParams().getSelectedCompanyId();
		}

		return filters;
	}	

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoRequisitionReceipt = (BmoRequisitionReceipt)autoPopulate(pmConn, new BmoRequisitionReceipt());

		// Tipo de Equipo
		BmoRequisition bmoRequisition = new BmoRequisition();
		int requisitionId = pmConn.getInt(bmoRequisition.getIdFieldName());
		if (requisitionId > 0) bmoRequisitionReceipt.setBmoRequisition((BmoRequisition) new PmRequisition(getSFParams()).populate(pmConn));
		else bmoRequisitionReceipt.setBmoRequisition(bmoRequisition);

		return bmoRequisitionReceipt;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		bmoRequisitionReceipt = (BmoRequisitionReceipt)bmObject;

		PmRequisition pmRequisition = new PmRequisition(getSFParams());

		// Operaciones si es nuevo recibo
		if (!(bmoRequisitionReceipt.getId() > 0)) {
			// Establecer clave
			super.save(pmConn, bmoRequisitionReceipt, bmUpdateResult);
			bmoRequisitionReceipt.getCode().setValue(bmoRequisitionReceipt.getCodeFormat());
			bmoRequisitionReceipt.getName().setValue("OC -> " + bmoRequisitionReceipt.getRequisitionId().toInteger());

			BmoRequisition bmoRequisition = (BmoRequisition)pmRequisition.get(pmConn, bmoRequisitionReceipt.getRequisitionId().toInteger());

			// Asignar el budgetItemId
			if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
				if (!(bmoRequisition.getBudgetItemId().toInteger() > 0))
					bmUpdateResult.addError(bmoRequisition.getBudgetItemId().getName(), "La Orden de Compra no tiene asignada una Partida Presupuestal.");

				bmoRequisitionReceipt.getBudgetItemId().setValue(bmoRequisition.getBudgetItemId().toInteger());
				bmoRequisitionReceipt.getAreaId().setValue(bmoRequisition.getAreaId().toInteger());
			}	

			bmoRequisitionReceipt.setId(bmUpdateResult.getId());

			// Si la OC tiene items, crear los items del recibo
			if (hasRequisitionItems(pmConn, bmoRequisitionReceipt.getRequisitionId().toInteger())) {
				PmRequisitionReceiptItem pmRequisitionReceiptItem = new PmRequisitionReceiptItem(getSFParams());
				pmRequisitionReceiptItem.createItemsFromRequisition(pmConn, bmoRequisitionReceipt, bmUpdateResult);
			}			
		}
		
		// Validar perido operativo de acuerdo a configuracion.
		if ( ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getRequiredPeriodFiscal().toBoolean() ) {
			printDevLog("ROC: INICIO Periodo Operativo(requerido)");
			PmFiscalPeriod pmFiscalPeriod = new PmFiscalPeriod(getSFParams());
			boolean fiscalPeriodIsOpen = pmFiscalPeriod.isOpen(pmConn, bmoRequisitionReceipt.getReceiptDate().toString().substring(0, 10), bmoRequisitionReceipt.getCompanyId().toInteger());
			printDevLog("ROC: FIN Periodo Operativo: "+fiscalPeriodIsOpen);

			if (!fiscalPeriodIsOpen)
				bmUpdateResult.addError(bmoRequisitionReceipt.getReceiptDate().getName(), 
						"El Periodo Operativo está Cerrado en la fecha del Documento (" + bmoRequisitionReceipt.getReceiptDate().toString().substring(0, 10) + ").");
		} else {
			printDevLog("ROC: INICIO Periodo Operativo(NO requerido)");
			PmFiscalPeriod pmFiscalPeriod = new PmFiscalPeriod(getSFParams());
			boolean fiscalPeriodIsClosed = pmFiscalPeriod.isClosed(pmConn, bmoRequisitionReceipt.getReceiptDate().toString().substring(0, 10), bmoRequisitionReceipt.getCompanyId().toInteger());
			printDevLog("ROC: FIN Periodo Operativo: "+fiscalPeriodIsClosed);

			if (fiscalPeriodIsClosed)
				bmUpdateResult.addError(bmoRequisitionReceipt.getReceiptDate().getName(), 
						"El Periodo Operativo está Cerrado en la fecha del Documento (" + bmoRequisitionReceipt.getReceiptDate().toString().substring(0, 10) + ").");
		}

		//Si el Recibo tiene una CXP ligada no se puede cambiar de estatus
		if (bmoRequisitionReceipt.getPayment().equals(BmoRequisitionReceipt.PAYMENT_PROVISIONED)) {
			if (bmoRequisitionReceipt.getStatus().equals(BmoRequisitionReceipt.STATUS_REVISION)) {
				bmUpdateResult.addError(bmoRequisitionReceipt.getStatus().getName(), "El Recibo tiene una CxP ligada.");
			}			
		}

		//Agregar la paridad de la moneda
		if (bmoRequisitionReceipt.getCurrencyId().toInteger() > 0) {
			if (bmoRequisitionReceipt.getCurrencyParity().equals("")) {
				PmCurrency pmCurrency = new PmCurrency(getSFParams());
				BmoCurrency bmoCurrency = (BmoCurrency)pmCurrency.get(pmConn, bmoRequisitionReceipt.getCurrencyId().toInteger());

				bmoRequisitionReceipt.getCurrencyParity().setValue(bmoCurrency.getParity().toString());
			}
		}

		// Si tiene productos que afecten inventario, validar que se haya seleccionado la sección de almacén
		if (pmRequisition.requisitionAffectsInventory(pmConn, bmoRequisitionReceipt.getRequisitionId().toInteger())
				&& !(bmoRequisitionReceipt.getWhSectionId().toInteger() > 0))
			bmUpdateResult.addError(bmoRequisitionReceipt.getWhSectionId().getName(), "<b>La Orden de Compra tiene productos que afectan Almácen. Debe seleccionar la Sección de Almacén destino.</b>");

		// Actualiza registros actuales
		super.save(pmConn, bmoRequisitionReceipt, bmUpdateResult);

		// Actualizar totales del recibo
		updateBalance(pmConn, bmoRequisitionReceipt, bmUpdateResult);

		// Operaciones con movimientos de almacen
		PmWhMovement pmWhMovement = new PmWhMovement(getSFParams());
		pmWhMovement.updateWhMovementFromRequisitionReceipt(pmConn, bmoRequisitionReceipt, bmUpdateResult);

		// Se almacena para establecer id correcto
		super.save(pmConn, bmoRequisitionReceipt, bmUpdateResult);
		// Crear la cxp si tiene habilitado desde conf.
		if (bmoRequisitionReceipt.getBmoRequisition().getBmoRequisitionType().getCreatePaccount().toBoolean() 
				&& bmoRequisitionReceipt.getStatus().equals(BmoRequisitionReceipt.STATUS_AUTHORIZED)) {

			// Validar que si la oc tiene MB anticipo prov. el monto de este sea <= al del roc
			//			if (bmoRequisitionReceipt.getStatus().equals(BmoRequisitionReceipt.STATUS_AUTHORIZED)) {
			//				PmBankMovConcept pmBankMovConcept = new PmBankMovConcept(getSFParams());
			//				if (pmBankMovConcept.hasMovConceptRequisitionAdvance(pmConn, bmoRequisitionReceipt.getRequisitionId().toInteger(), bmUpdateResult)) {
			//					double sumAmount = pmBankMovConcept.getSumAmountRequisitionAdvance(pmConn, bmoRequisitionReceipt.getRequisitionId().toInteger(), bmUpdateResult);
			//					if (sumAmount > bmoRequisitionReceipt.getTotal().toDouble()) {
			//						bmUpdateResult.addError(bmoRequisitionReceipt.getTotal().getName(), "La O.C. tiene un pago 'Anticipo Proveedor' con un monto mayor al del Recibo O.C.");
			//					}
			//				}
			//			}

			
			PmPaccount pmPaccNew = new PmPaccount(getSFParams());
			pmPaccNew.createFromRequisitionReceipt(pmConn, bmoRequisitionReceipt, bmUpdateResult);
			bmoRequisitionReceipt.getPayment().setValue(BmoRequisitionReceipt.PAYMENT_PROVISIONED);

			super.save(pmConn, bmoRequisitionReceipt, bmUpdateResult);
		}


		return bmUpdateResult;
	}	

	public void updateBalance(PmConn pmConn, BmoRequisitionReceipt bmoRequisitionReceipt, BmUpdateResult bmUpdateResult) throws SFException {
		// Actualiza el estatus de la orden de compra
		PmRequisition pmRequisition = new PmRequisition(getSFParams());
		BmoRequisition bmoRequisition = (BmoRequisition)pmRequisition.get(pmConn, bmoRequisitionReceipt.getRequisitionId().toInteger());
		pmRequisition.updateDeliveryStatus(pmConn, bmoRequisition, bmUpdateResult);

		// Actuliza montos
		double amount = 0, quantityRerc = 0 , quantityReqi = 0;
		String sql = " SELECT sum(reit_amount) as amount, sum(reit_quantity) as quantity FROM requisitionreceiptitems " +
				" WHERE reit_requisitionreceiptid = " + bmoRequisitionReceipt.getId() + 
				" AND reit_quantity > 0 ";		
		pmConn.doFetch(sql);
		if (pmConn.next())  { 
			amount = pmConn.getDouble("amount");
			quantityRerc = pmConn.getDouble("quantity");
		}

		//Obtener la cantidad de items en la OC
		sql = " SELECT sum(rqit_quantity) as quantity FROM requisitionitems " +
				" WHERE rqit_requisitionid = " + bmoRequisition.getId() + 
				" AND rqit_quantity > 0 ";		
		pmConn.doFetch(sql);
		if (pmConn.next())  {
			quantityReqi = pmConn.getDouble("quantity");
		}

		//Descuentos en OC
		double discount = 0;

		if (quantityRerc > 0) {
			discount = (bmoRequisition.getDiscount().toDouble() / quantityReqi) * quantityRerc;
		}

		// Calcular montos
		if (amount == 0) {			
			bmoRequisitionReceipt.getAmount().setValue(0);
			bmoRequisitionReceipt.getTax().setValue(0);			
			bmoRequisitionReceipt.getTotal().setValue(0);			
		} else {			
			bmoRequisitionReceipt.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(amount - discount));
			//Obtener el Iva
			//if (getRequisitionHasTax(pmConn, bmoRequisitionReceipt.getRequisitionId().toInteger())) {
			if (bmoRequisition.getTaxApplies().toBoolean()) {
				double taxRate = ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getTax().toDouble() / 100;				
				bmoRequisitionReceipt.getTax().setValue(SFServerUtil.roundCurrencyDecimals((amount - discount) * taxRate));				
				bmoRequisitionReceipt.getTotal().setValue(SFServerUtil.roundCurrencyDecimals((amount - discount) +  bmoRequisitionReceipt.getTax().toDouble()));

			} else {
				bmoRequisitionReceipt.getTax().setValue(0);
				bmoRequisitionReceipt.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(amount - discount));
			}

			if (bmoRequisition.getHoldBack().toDouble() > 0) {
				bmoRequisitionReceipt.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(bmoRequisitionReceipt.getTotal().toDouble() - bmoRequisition.getHoldBack().toDouble()));
			}
		}

		super.save(pmConn, bmoRequisitionReceipt, bmUpdateResult);	
	}

	//Actualizar la partidad del presupuesto
	public void updateBudgetItem(PmConn pmConn, BmoRequisition bmoRequisition, BmUpdateResult bmUpdateResult) throws SFException {
		BmoRequisitionReceipt bmoRequisitionReceipt = new BmoRequisitionReceipt();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoRequisitionReceipt.getKind(), bmoRequisitionReceipt.getRequisitionId(), bmoRequisition.getId());
		PmRequisitionReceipt pmRequisitionReceipt = new PmRequisitionReceipt(getSFParams());
		Iterator<BmObject> requisitionReceiptIterator = pmRequisitionReceipt.list(pmConn, bmFilter).iterator();

		PmPaccount pmPaccount = new PmPaccount(getSFParams());

		while (requisitionReceiptIterator.hasNext()) {
			bmoRequisitionReceipt = (BmoRequisitionReceipt)requisitionReceiptIterator.next();

			bmoRequisitionReceipt.getBudgetItemId().setValue(bmoRequisition.getBudgetItemId().toInteger());			
			pmRequisitionReceipt.saveSimple(pmConn, bmoRequisitionReceipt, bmUpdateResult);

			//Actualizar la CxP
			pmPaccount.updateBudgetItem(pmConn, bmoRequisitionReceipt, bmUpdateResult);

		}
	}

	// Actualiza estatus de provision
	public void updatePaymentStatus(PmConn pmConn, BmoRequisitionReceipt bmoRequisitionReceipt, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = " SELECT pacc_paccountid FROM paccounts "
				+ " WHERE pacc_requisitionreceiptid = " + bmoRequisitionReceipt.getId();
				//+ " AND pacc_status <> '" + BmoPaccount.STATUS_CANCELLED + "'";
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			bmoRequisitionReceipt.getPayment().setValue(BmoRequisitionReceipt.PAYMENT_PROVISIONED);
		} else {
			bmoRequisitionReceipt.getPayment().setValue(BmoRequisitionReceipt.PAYMENT_NOTPROVISIONED);
		}
		super.save(pmConn, bmoRequisitionReceipt, bmUpdateResult);

		// Actualiza el estatus de la orden de compra
		PmRequisition pmRequisition = new PmRequisition(getSFParams());
		BmoRequisition bmoRequisition = (BmoRequisition)pmRequisition.get(pmConn, bmoRequisitionReceipt.getRequisitionId().toInteger());
		
		// Calcular montos
		pmRequisition.updateBalance(pmConn, bmoRequisition, bmUpdateResult);
		
		// Actualizar pagos y saldo
		pmRequisition.updatePayments(pmConn, bmoRequisition, bmUpdateResult);
		
		// Crear bitacora en OC
		pmRequisition.createWFloLogRequisition(pmConn, bmoRequisition, "Modificó", bmUpdateResult);

	}

	// Crea el recibo a partir de una orden de compra
	public BmoRequisitionReceipt createFromRequisition(PmConn pmConn, BmoRequisition bmoRequisition, BmUpdateResult bmUpdateResult) throws SFException {
		bmoRequisitionReceipt = new BmoRequisitionReceipt();

		try {
			bmoRequisitionReceipt = (BmoRequisitionReceipt)this.getBy(pmConn, bmoRequisition.getId(), bmoRequisitionReceipt.getRequisitionId().getName());
			printDevLog("Existe roc: "+bmoRequisitionReceipt.getId());
		} catch(SFException e) {
			printDevLog("NO existe roc");
			// Crear el recibo de orden de compra
			bmoRequisitionReceipt.getName().setValue(bmoRequisition.getName().toString());
			bmoRequisitionReceipt.getReceiptDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));
			bmoRequisitionReceipt.getRequisitionId().setValue(bmoRequisition.getId());
			bmoRequisitionReceipt.getType().setValue(BmoRequisitionReceipt.TYPE_RECEIPT);			
			bmoRequisitionReceipt.getPayment().setValue(BmoRequisitionReceipt.PAYMENT_NOTPROVISIONED);
			bmoRequisitionReceipt.getDiscount().setValue(0);			
			bmoRequisitionReceipt.getSupplierId().setValue(bmoRequisition.getSupplierId().toInteger());			
			if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
				if (!(bmoRequisition.getBudgetItemId().toInteger() > 0))
					bmUpdateResult.addError(bmoRequisition.getBudgetItemId().getName(), "La Orden de Compra no tiene asignada una Partida Presupuestal");
				bmoRequisitionReceipt.getBudgetItemId().setValue(bmoRequisition.getBudgetItemId().toInteger());
				bmoRequisitionReceipt.getAreaId().setValue(bmoRequisition.getAreaId().toInteger());
			}

			bmoRequisitionReceipt.getCurrencyId().setValue(bmoRequisition.getCurrencyId().toInteger());
			bmoRequisitionReceipt.getCurrencyParity().setValue(bmoRequisition.getCurrencyParity().toDouble());
			bmoRequisitionReceipt.getCompanyId().setValue(bmoRequisition.getCompanyId().toInteger());
			//			bmoRequisitionReceipt.getWhSectionId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getDefaultVirtualWhSectionId().toInteger());
			bmoRequisitionReceipt.setBmoRequisition(bmoRequisition);

			this.save(pmConn, bmoRequisitionReceipt, bmUpdateResult);	
			// Autorecibir los items del recibo de orden de compra y autorizar el recibo
			this.autoReceive(pmConn, bmoRequisitionReceipt, bmUpdateResult);
			if (bmoRequisition.getTaxApplies().toBoolean()) {
				//sumar los items del recibo
				this.updateBalance(pmConn, bmoRequisitionReceipt, bmUpdateResult);
			}

			bmoRequisitionReceipt.getStatus().setValue(BmoRequisitionReceipt.STATUS_AUTHORIZED);
			super.save(pmConn, bmoRequisitionReceipt, bmUpdateResult);
			PmRequisitionType pmRequisitionType = new PmRequisitionType(getSFParams());
			BmoRequisitionType bmoRequisitionType = (BmoRequisitionType)pmRequisitionType.get(pmConn, bmoRequisitionReceipt.getBmoRequisition().getRequisitionTypeId().toInteger());
			//Crear la CxP si se crea el recibo	
			if (bmoRequisitionType.getCreateReceipt().toBoolean()) {
				PmPaccount pmPaccNew = new PmPaccount(getSFParams());
				pmPaccNew.createFromRequisitionReceipt(pmConn, bmoRequisitionReceipt, bmUpdateResult); 
				bmoRequisitionReceipt.getPayment().setValue(BmoRequisitionReceipt.PAYMENT_PROVISIONED);

				super.save(pmConn, bmoRequisitionReceipt, bmUpdateResult);
			}
		}

		return bmoRequisitionReceipt;
	}

	private void autoReceive(PmConn pmConn, BmoRequisitionReceipt bmoRequisitionReceipt, BmUpdateResult bmUpdateResult) throws SFException, SFException {
		this.bmoRequisitionReceipt = bmoRequisitionReceipt;

		// Actualiza los items
		PmRequisitionItem pmRequisitionItem = new PmRequisitionItem(getSFParams());
		BmoRequisitionItem bmoRequisitionItem = new BmoRequisitionItem();
		BmoRequisitionReceiptItem bmoRequisitionReceiptItem = new BmoRequisitionReceiptItem();

		BmFilter filterRequisitionReceipt = new BmFilter();
		filterRequisitionReceipt.setValueFilter(bmoRequisitionReceiptItem.getKind(), bmoRequisitionReceiptItem.getRequisitionReceiptId(), bmoRequisitionReceipt.getId());
		PmRequisitionReceiptItem pmRequisitionReceiptItem = new PmRequisitionReceiptItem(getSFParams()); 
		Iterator<BmObject> listRequisitionReceiptItem = pmRequisitionReceiptItem.list(pmConn, filterRequisitionReceipt).iterator();			
		while (listRequisitionReceiptItem.hasNext()) {
			bmoRequisitionReceiptItem = (BmoRequisitionReceiptItem)listRequisitionReceiptItem.next();

			// Obtiene la cantidad total
			bmoRequisitionItem = (BmoRequisitionItem)pmRequisitionItem.get(pmConn, bmoRequisitionReceiptItem.getRequisitionItemId().toInteger());

			// Asigna la cantidad y la almacena			
			bmoRequisitionReceiptItem.getQuantity().setValue(bmoRequisitionItem.getQuantity().toDouble());

			pmRequisitionReceiptItem.save(pmConn, bmoRequisitionReceiptItem, bmUpdateResult);
		}

		// Autorizar recibo de orden de compra
		bmoRequisitionReceipt.getStatus().setValue(BmoRequisitionReceipt.STATUS_AUTHORIZED);

		this.save(pmConn, bmoRequisitionReceipt, bmUpdateResult);

	}

	private boolean hasRequisitionItems(PmConn pmConn, int requisitionId) throws SFException {
		boolean result = false;
		int items = 0;

		String sql = "";
		//Sumar los items de la OC		
		sql  = " SELECT COUNT(*) AS elements FROM requisitionitems " +
				" WHERE rqit_requisitionid = " + requisitionId;
		pmConn.doFetch(sql);
		if (pmConn.next()) items = pmConn.getInt("elements");			

		if (items > 0) result = true;

		return result;
	}

	// Crea una devolución de un recibo de orden de compra
	public BmoRequisitionReceipt createReturnFromRequisition(PmConn pmConn, BmoRequisition bmoRequisition, BmUpdateResult bmUpdateResult) throws SFException {
		bmoRequisitionReceipt = new BmoRequisitionReceipt();

		// Crear el recibo de orden de compra
		bmoRequisitionReceipt.getName().setValue(bmoRequisition.getName().toString());
		bmoRequisitionReceipt.getReceiptDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
		bmoRequisitionReceipt.getRequisitionId().setValue(bmoRequisition.getId());
		bmoRequisitionReceipt.getType().setValue(BmoRequisitionReceipt.TYPE_RETURN);
		bmoRequisitionReceipt.getStatus().setValue(BmoRequisitionReceipt.STATUS_REVISION);
		bmoRequisitionReceipt.getPayment().setValue(BmoRequisitionReceipt.PAYMENT_NOTPROVISIONED);
		bmoRequisitionReceipt.getDiscount().setValue(0);
		bmoRequisitionReceipt.getTax().setValue(0);
		bmoRequisitionReceipt.getSupplierId().setValue(bmoRequisition.getSupplierId().toInteger());
		// bmoRequisitionReceipt.getWhSectionId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getDefaultVirtualWhSectionId().toInteger());
		bmoRequisitionReceipt.setBmoRequisition(bmoRequisition);

		this.save(pmConn, bmoRequisitionReceipt, bmUpdateResult);	

		// Autorecibir los items del recibo de orden de compra y autorizar el recibo
		this.autoReceive(pmConn, bmoRequisitionReceipt, bmUpdateResult);

		return bmoRequisitionReceipt;
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoRequisitionReceipt = (BmoRequisitionReceipt)bmObject;

		// Si el recibo ya está autorizado, no se puede hacer movimientos			
		if (bmoRequisitionReceipt.getStatus().toChar() == BmoRequisitionReceipt.STATUS_AUTHORIZED) {				
			bmUpdateResult.addMsg("No se puede Eliminar el Recibo de Órden de Compra - está Autorizado.");
		} else {
			// Obtener orden de compra
			PmRequisition pmRequisition = new PmRequisition(getSFParams());
			BmoRequisition bmoRequisition = (BmoRequisition)pmRequisition.get(pmConn, bmoRequisitionReceipt.getRequisitionId().toInteger());

			// Eliminar Movimiento de Almacen
			if (pmRequisition.requisitionAffectsInventory(pmConn, bmoRequisitionReceipt.getRequisitionId().toInteger())) {
				PmWhMovement pmWhMovement = new PmWhMovement(getSFParams());
				BmoWhMovement bmoWhMovement = new BmoWhMovement();
				bmoWhMovement = (BmoWhMovement)pmWhMovement.getBy(pmConn, bmoRequisitionReceipt.getId(), bmoWhMovement.getRequisitionReceiptId().getName());
				pmWhMovement.delete(pmConn, bmoWhMovement, bmUpdateResult);
			}

			// Eliminar los items	
			PmRequisitionReceiptItem pmRequisitionReceiptItem = new PmRequisitionReceiptItem(getSFParams());
			pmRequisitionReceiptItem.deleteItems(pmConn, bmoRequisitionReceipt, bmUpdateResult);

			// Eliminar Registro
			super.delete(pmConn, bmObject, bmUpdateResult);

			// Actualiza el estatus de la orden de compra
			pmRequisition.updateDeliveryStatus(pmConn, bmoRequisition, bmUpdateResult);
		}

		return bmUpdateResult;
	}

	public void deleteByRequisition(PmConn pmConn, BmoRequisition bmoRequisition, BmUpdateResult bmUpdateResult) throws SFException {
		// Filtrar cuentas por pagar por orden de compra
		BmFilter filterByRequisition = new BmFilter();
		filterByRequisition.setValueFilter(bmoRequisitionReceipt.getKind(), bmoRequisitionReceipt.getRequisitionId().getName(), bmoRequisition.getId());
		Iterator<BmObject> listRequisitionReceipts = this.list(pmConn, filterByRequisition).iterator();

		while (listRequisitionReceipts.hasNext()) {
			BmoRequisitionReceipt bmoRequisitionReceipt = (BmoRequisitionReceipt)listRequisitionReceipts.next();

			// Elimina las Cuentas x Pagar 
			PmPaccount pmPaccount = new PmPaccount(getSFParams());
			pmPaccount.deleteByRequisitionReceipt(pmConn, bmoRequisitionReceipt, bmUpdateResult);		

			// Cambia Estatus a En Revision para poder eliminar
			bmoRequisitionReceipt.getStatus().setValue(BmoRequisitionReceipt.STATUS_REVISION);
			super.save(pmConn, bmoRequisitionReceipt, bmUpdateResult);

			// Elimina el Recibo de O.C.
			this.delete(pmConn, bmoRequisitionReceipt, bmUpdateResult);
		}
	}
}
