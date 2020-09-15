/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */
package com.flexwm.server.fi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import com.flexwm.server.FlexUtil;
import com.flexwm.server.PmCompanyNomenclature;
import com.flexwm.server.PmFlexConfig;
import com.flexwm.server.ar.PmPropertyRental;
import com.flexwm.server.cm.PmCustomer;
import com.flexwm.server.cr.PmCredit;
import com.flexwm.server.cr.PmCreditType;
import com.flexwm.server.op.PmOrder;
import com.flexwm.server.op.PmOrderDelivery;
import com.flexwm.server.op.PmOrderGroup;
import com.flexwm.server.op.PmOrderType;
import com.flexwm.server.op.PmReqPayType;
import com.flexwm.server.wf.PmWFlowLog;
import com.flexwm.server.wf.PmWFlowUser;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.ar.BmoPropertyRental;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cr.BmoCredit;
import com.flexwm.shared.cr.BmoCreditType;
import com.flexwm.shared.fi.BmoBankAccount;
import com.flexwm.shared.fi.BmoBankMovConcept;
import com.flexwm.shared.fi.BmoBankMovType;
import com.flexwm.shared.fi.BmoBankMovement;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.fi.BmoPaymentType;
import com.flexwm.shared.fi.BmoRaccount;
import com.flexwm.shared.fi.BmoRaccountAssignment;
import com.flexwm.shared.fi.BmoRaccountItem;
import com.flexwm.shared.fi.BmoRaccountLink;
import com.flexwm.shared.fi.BmoRaccountType;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderDelivery;
import com.flexwm.shared.op.BmoOrderGroup;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.op.BmoReqPayType;
import com.flexwm.shared.wf.BmoWFlowLog;
import com.symgae.server.HtmlUtil;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.SFSendMail;
import com.symgae.server.SFServerUtil;
import com.symgae.server.sf.PmCompany;
import com.symgae.server.sf.PmProfile;
import com.symgae.server.sf.PmUser;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFMailAddress;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoProfile;

import com.symgae.shared.sf.BmoUser;


public class PmRaccount extends PmObject {
	BmoRaccount bmoRaccount;

	public PmRaccount(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoRaccount = new BmoRaccount();
		setBmObject(bmoRaccount);

		// Lista de joins
		setJoinList(
				new ArrayList<PmJoin>(Arrays.asList(new PmJoin(bmoRaccount.getCompanyId(), bmoRaccount.getBmoCompany()),
						new PmJoin(bmoRaccount.getCustomerId(), bmoRaccount.getBmoCustomer()),
						new PmJoin(bmoRaccount.getUserId(), bmoRaccount.getBmoUser()),
						new PmJoin(bmoRaccount.getBmoUser().getAreaId(), bmoRaccount.getBmoUser().getBmoArea()),
						new PmJoin(bmoRaccount.getBmoUser().getLocationId(), bmoRaccount.getBmoUser().getBmoLocation()),
						new PmJoin(bmoRaccount.getBmoCustomer().getTerritoryId(),bmoRaccount.getBmoCustomer().getBmoTerritory()),
						new PmJoin(bmoRaccount.getCurrencyId(), bmoRaccount.getBmoCurrency()),		
						new PmJoin(bmoRaccount.getRaccountTypeId(), bmoRaccount.getBmoRaccountType()),
						//Viene de los terminos de pago de la CXC
						new PmJoin(bmoRaccount.getReqPayTypeId(),bmoRaccount.getBmoReqPayType()))));
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();

		// Filtro de cxc por usuario
		if (getSFParams().restrictData(bmoRaccount.getProgramCode())) {

			filters = "( racc_userid in (" + " SELECT user_userid FROM users " + " WHERE " + " user_userid = "
					+ loggedUserId + " or user_userid in ( " + " select u2.user_userid from users u1 "
					+ " left join users u2 on (u2.user_parentid = u1.user_userid) " + " where u1.user_userid = "
					+ loggedUserId + " ) " + " or user_userid in ( " + " select u3.user_userid from users u1 "
					+ " left join users u2 on (u2.user_parentid = u1.user_userid) "
					+ " left join users u3 on (u3.user_parentid = u2.user_userid) " + " where u1.user_userid = "
					+ loggedUserId + " ) " + " or user_userid in ( " + " select u4.user_userid from users u1 "
					+ " left join users u2 on (u2.user_parentid = u1.user_userid) "
					+ " left join users u3 on (u3.user_parentid = u2.user_userid) "
					+ " left join users u4 on (u4.user_parentid = u3.user_userid) " + " where u1.user_userid = "
					+ loggedUserId + " ) " + " or user_userid in ( " + " select u5.user_userid from users u1 "
					+ " left join users u2 on (u2.user_parentid = u1.user_userid) "
					+ " left join users u3 on (u3.user_parentid = u2.user_userid) "
					+ " left join users u4 on (u4.user_parentid = u3.user_userid) "
					+ " left join users u5 on (u5.user_parentid = u4.user_userid) " + " where u1.user_userid = "
					+ loggedUserId + " ) " + " ) " + " ) ";
		}

		// Filtro de cxc de empresas del usuario
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			if (filters.length() > 0)
				filters += " AND ";
			filters += "( racc_companyid in (" + " select uscp_companyid from usercompanies " + " where "
					+ " uscp_userid = " + loggedUserId + " )" + ") ";
		}

		// Filtro de empresa seleccionada
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0)
				filters += " AND ";
			filters += " racc_companyid = " + getSFParams().getSelectedCompanyId();
		}

		return filters;
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoRaccount bmoRaccount = (BmoRaccount) autoPopulate(pmConn, new BmoRaccount());

		// BmoRaccountType
		BmoRaccountType bmoRaccountType = new BmoRaccountType();
		int raccountTypeId = pmConn.getInt(bmoRaccountType.getIdFieldName());
		if (raccountTypeId > 0)
			bmoRaccount.setBmoRaccountType((BmoRaccountType) new PmRaccountType(getSFParams()).populate(pmConn));
		else
			bmoRaccount.setBmoRaccountType(bmoRaccountType);

		// BmoCompany
		BmoCompany bmoCompany = new BmoCompany();
		int companyId = pmConn.getInt(bmoCompany.getIdFieldName());
		if (companyId > 0)
			bmoRaccount.setBmoCompany((BmoCompany) new PmCompany(getSFParams()).populate(pmConn));
		else
			bmoRaccount.setBmoCompany(bmoCompany);

		// BmoCustomer
		BmoCustomer bmoCustomer = new BmoCustomer();
		int customerId = pmConn.getInt(bmoCustomer.getIdFieldName());
		if (customerId > 0)
			bmoRaccount.setBmoCustomer((BmoCustomer) new PmCustomer(getSFParams()).populate(pmConn));
		else
			bmoRaccount.setBmoCustomer(bmoCustomer);

		// BmoCurrency
		BmoCurrency bmoCurrency = new BmoCurrency();
		int currencyId = pmConn.getInt(bmoCurrency.getIdFieldName());
		if (currencyId > 0)
			bmoRaccount.setBmoCurrency((BmoCurrency) new PmCurrency(getSFParams()).populate(pmConn));
		else
			bmoRaccount.setBmoCurrency(bmoCurrency);
		// BmoCurrency
		BmoReqPayType bmoReqPayType = new BmoReqPayType();
		int reqPayTypeId = pmConn.getInt(bmoReqPayType.getIdFieldName());
		if (reqPayTypeId > 0)
			bmoRaccount.setBmoCurrency((BmoCurrency) new PmCurrency(getSFParams()).populate(pmConn));
		else
			bmoRaccount.setBmoReqPayType(bmoReqPayType);

		return bmoRaccount;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		boolean newRecord = false;
		bmoRaccount = (BmoRaccount) bmObject;
		BmoRaccount bmoRaccountPrevious = new BmoRaccount();
		int programId = getSFParams().getProgramId(bmoRaccount.getProgramCode());
		PmCompanyNomenclature pmCompanyNomenclature = new PmCompanyNomenclature(getSFParams());
		String code = "";

		PmRaccountType pmRaccountType = new PmRaccountType(getSFParams());
		BmoRaccountType bmoRaccountType = (BmoRaccountType) pmRaccountType.get(pmConn,bmoRaccount.getRaccountTypeId().toInteger());

		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder bmoOrder = new BmoOrder();

		if (bmoRaccount.getOrderId().toInteger() > 0)
			bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoRaccount.getOrderId().toInteger());

		if (!(bmoRaccount.getCompanyId().toInteger() > 0))
			bmUpdateResult.addError(bmoRaccount.getCompanyId().getName(), "Debe seleccionar una empresa.");

		// Validar que folio sea solo números
		String regex = "[0-9]+";
		if (!bmoRaccount.getFolio().toString().equals("")) {
			if (!bmoRaccount.getFolio().toString().matches(regex)) {
				bmUpdateResult.addError(bmoRaccount.getFolio().getName(), "El " + bmoRaccount.getFolio().getLabel() + " debe contener solo números.");
			}
		}

		// Se almacena de forma preliminar para asignar ID y la Clave
		if (!(bmoRaccount.getId() > 0)) {
			newRecord = true;
			super.save(pmConn, bmoRaccount, bmUpdateResult);
			bmoRaccount.setId(bmUpdateResult.getId());
			// Generar clave personalizada si la hay, si no retorna la de por defecto
			code = pmCompanyNomenclature.getCodeCustom(pmConn,
					bmoRaccount.getCompanyId().toInteger(),
					bmoRaccount.getProgramCode().toString(),
					bmUpdateResult.getId(),
					BmoRaccount.CODE_PREFIX
					);
			bmoRaccount.getCode().setValue(code);
			//			bmoRaccount.getCode().setValue(bmoRaccount.getCodeFormat());

			// Si es de tipo pedidos
			if (bmoRaccountType.getCategory().equals(BmoRaccountType.CATEGORY_ORDER)) {
				if (!(bmoRaccount.getOrderId().toInteger() > 0))
					bmUpdateResult.addError(bmoRaccount.getOrderId().getName(), "Debe seleccionar un Pedido.");

				int paymentTypeId = getCustomerPaymentTypeByOrder(pmConn, bmoOrder, bmUpdateResult);
				if (paymentTypeId > 0)
					bmoRaccount.getPaymentTypeId().setValue(paymentTypeId);

				// Asignar el usuario del perfil cobranza desde conf. flex.
				if (!(bmoRaccount.getCollectorUserId().toInteger() > 0))
					asignCollectUser(pmConn, bmoRaccount, bmoOrder, bmUpdateResult);

				// Asigna si aplica iva o no
				bmoRaccount.getTaxApplies().setValue(bmoOrder.getTaxApplies().toBoolean());

			} else if (bmoRaccountType.getCategory().equals(BmoRaccountType.CATEGORY_OTHER)) {
				if (bmoRaccount.getOrderId().toInteger() > 0) {
					int paymentTypeId = getCustomerPaymentTypeByOrder(pmConn, bmoOrder, bmUpdateResult);					
					if (paymentTypeId > 0)
						bmoRaccount.getPaymentTypeId().setValue(paymentTypeId);
				} else {
					int paymentTypeId = getCustomerPaymentTypeByCust(pmConn, bmoRaccount.getCustomerId().toInteger(), bmoRaccount, bmUpdateResult);					
					if (paymentTypeId > 0)
						bmoRaccount.getPaymentTypeId().setValue(paymentTypeId);
				}
			}

			if (!(bmoRaccount.getCurrencyId().toInteger() > 0)) {
				if (bmoRaccount.getOrderId().toInteger() > 0)
					bmoRaccount.getCurrencyId().setValue(bmoOrder.getCurrencyId().toInteger());
				else	
					bmoRaccount.getCurrencyId().setValue(((BmoFlexConfig) getSFParams().getBmoAppConfig()).getCurrencyId().toInteger());
			}

			bmoRaccount.getAutoCreate().setValue("0");
			bmoRaccount.getFailure().setValue("0");

			// Guardar fecha de programacion de pago, original
			bmoRaccount.getDueDateStart().setValue(bmoRaccount.getDueDate().toString());

		} else {
			PmRaccount pmRaccountPrevious = new PmRaccount(getSFParams());
			bmoRaccountPrevious = (BmoRaccount)pmRaccountPrevious.get(pmConn, bmoRaccount.getId());

			// Validar que no se pueda cambiar de estatus a en revision si ya tiene pagos
			if(!bmoRaccount.getBmoRaccountType().getCategory().equals(""+BmoRaccountType.CATEGORY_CREDITNOTE)) {
				if (bmoRaccount.getStatus().toChar() ==  BmoRaccount.STATUS_REVISION) {
					if (bmoRaccount.getPayments().toDouble() > 0) 
						bmUpdateResult.addError(bmoRaccount.getStatus().getName(), "No se puede cambiar el Estatus, " 
								+ " la " + getSFParams().getProgramTitle(getBmObject()) + " tiene Pagos.");
					
					// Validar si tiene pagos, que no pueda cambiar el estatus a en revison. El escenario es cuando ya tiene un mb cobro, y una de devolucion
					if (hasPayments(pmConn, bmoRaccount.getId()))
						bmUpdateResult.addError(bmoRaccount.getStatus().getName(), "No se puede cambiar el estatus " + bmoRaccount.getStatus().getSelectedOption().getLabel() 
								+ " la " + getSFParams().getProgramTitle(getBmObject()) + " tiene Pagos ligados.");
				}
			}
			
			// Validar que si esta cancelando y tiene pagos, regresa error
			if (bmoRaccount.getStatus().toChar() ==  BmoRaccount.STATUS_CANCELLED) {
				if (bmoRaccount.getPayments().toDouble() > 0) {
					bmUpdateResult.addError(bmoRaccount.getStatus().getName(), "No se puede Cancelar, " 
							+ " la " + getSFParams().getProgramTitle(getBmObject()) + " tiene Pagos.");
				}
			}
		}

		// Obtener la partida presupuestal
		if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {					
			//			if (!(bmoRaccount.getBudgetItemId().toInteger() > 0) 
			//					&& bmoRaccount.getOrderId().toInteger() > 0) {
			//				int budgetItemId = pmOrder.getBudgetItemByOrder(pmConn, bmoOrder, bmUpdateResult);
			//				bmoRaccount.getBudgetItemId().setValue(budgetItemId);
			//			}

			if (!(bmoRaccount.getBudgetItemId().toInteger() > 0)) {
				bmUpdateResult.addError(bmoRaccount.getBudgetItemId().getName(), "Seleccione una Partida.");
			}	

			//	if (!(bmoRaccount.getAreaId().toInteger() > 0)) {
			//		bmUpdateResult.addError(bmoRaccount.getAreaId().getName(), "Seleccione un Departamento");
			//	}
		}

		if (bmoRaccount.getStatus().equals(BmoRaccount.STATUS_AUTHORIZED)) {
			bmoRaccount.getAutoCreate().setValue("0");
			bmoRaccount.getAuthorizedDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));
			bmoRaccount.getAuthorizedUser().setValue(getSFParams().getLoginInfo().getUserId());
		}

		// Agregar la paridad de la moneda
		if (bmoRaccount.getCurrencyId().toInteger() > 0) {
			if (bmoRaccount.getCurrencyParity().equals("")) {
				PmCurrency pmCurrency = new PmCurrency(getSFParams());
				BmoCurrency bmoCurrency = (BmoCurrency) pmCurrency.get(pmConn, bmoRaccount.getCurrencyId().toInteger());
				bmoRaccount.getCurrencyParity().setValue(bmoCurrency.getParity().toString());
			}
		}

		// Se concatena serie y folio al campo Factura si la CxC viene de En Revision
		if (bmoRaccount.getStatus().equals(BmoRaccount.STATUS_REVISION) 
				|| bmoRaccountPrevious.getStatus().equals(BmoRaccount.STATUS_REVISION)) {

			// Validar que sea solo números para agregar ceros al folio
			if (!bmoRaccount.getFolio().toString().equals("")) {
				if (bmoRaccount.getFolio().toString().matches(regex)) {
					if (bmoRaccount.getFolio().toString().length() < bmoRaccountType.getFolioZeros().toInteger())
						bmoRaccount.getFolio().setValue(FlexUtil.codeFormatDigits(bmoRaccount.getFolio().toInteger(), bmoRaccountType.getFolioZeros().toInteger(), ""));
					bmoRaccount.getInvoiceno().setValue(bmoRaccount.getSerie().toString() + bmoRaccount.getFolio().toString());
				} else 
					bmUpdateResult.addError(bmoRaccount.getFolio().getName(), "El " + bmoRaccount.getFolio().getLabel() + " debe contener solo números.");
			} else {
				// Como esta vacio, deja solo la serie
				bmoRaccount.getInvoiceno().setValue(bmoRaccount.getSerie().toString());
			}
		}

		// Actualizar el presupuesto
		if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
			if (bmoRaccount.getBudgetItemId().toInteger() > 0) {
				if (bmoRaccount.getStatus().equals(BmoRaccount.STATUS_AUTHORIZED)) {
					if (bmoRaccount.getBudgetItemId().toInteger() > 0 && bmoRaccount.getTotal().toDouble() > 0) {
						PmBudgetItem pmBudgetItem = new PmBudgetItem(getSFParams());
						BmoBudgetItem bmoBudgetItem = (BmoBudgetItem) pmBudgetItem.get(pmConn,
								bmoRaccount.getBudgetItemId().toInteger());
						pmBudgetItem.updateBalance(pmConn, bmoBudgetItem, bmUpdateResult);
					}
				} else if (bmoRaccount.getStatus().equals(BmoRaccount.STATUS_REVISION)) {
					PmBudgetItem pmBudgetItem = new PmBudgetItem(getSFParams());
					BmoBudgetItem bmoBudgetItem = (BmoBudgetItem) pmBudgetItem.get(pmConn,
							bmoRaccount.getBudgetItemId().toInteger());
					pmBudgetItem.updateBalance(pmConn, bmoBudgetItem, bmUpdateResult);
				}
			}
		}

		// Validar perido operativo de acuerdo a configuracion.
		if ( ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getRequiredPeriodFiscal().toBoolean() ) {
			printDevLog("racc: INICIO Periodo Operativo(requerido)");
			PmFiscalPeriod pmFiscalPeriod = new PmFiscalPeriod(getSFParams());
			boolean fiscalPeriodIsOpen = pmFiscalPeriod.isOpen(pmConn, bmoRaccount.getReceiveDate().toString(), bmoRaccount.getCompanyId().toInteger());
			printDevLog("racc: FIN Periodo Operativo: "+fiscalPeriodIsOpen);

			if (!fiscalPeriodIsOpen)
				bmUpdateResult.addError(bmoRaccount.getReceiveDate().getName(), 
						"El Periodo Operativo está Cerrado en la fecha del Documento (" + bmoRaccount.getReceiveDate().toString() + ").");
		} else {
			printDevLog("racc: INICIO Periodo Operativo(NO requerido)");
			PmFiscalPeriod pmFiscalPeriod = new PmFiscalPeriod(getSFParams());
			boolean fiscalPeriodIsClosed = pmFiscalPeriod.isClosed(pmConn, bmoRaccount.getReceiveDate().toString(), bmoRaccount.getCompanyId().toInteger());
			printDevLog("racc: FIN Periodo Operativo: "+fiscalPeriodIsClosed);

			if (fiscalPeriodIsClosed)
				bmUpdateResult.addError(bmoRaccount.getReceiveDate().getName(), 
						"El Periodo Operativo está Cerrado en la fecha del Documento (" + bmoRaccount.getReceiveDate().toString() + ").");
		}

		if (!bmUpdateResult.hasErrors()) {
			super.save(pmConn, bmoRaccount, bmUpdateResult);
			// Actualizar id de claves del programa por empresa(antes de que entre a updateBalance y cree la automatica)
			if (newRecord) {
				pmCompanyNomenclature.updateConsecutiveByCompany(pmConn, bmoRaccount.getCompanyId().toInteger(), 
						bmoRaccount.getProgramCode().toString());
			}
		}

		// Actualiza los montos y estatus
		this.updateBalance(pmConn, bmoRaccount, bmUpdateResult);

		// Forzar estatus a pendiente cuando se esta creando, ya que lo ponia como total
		if (newRecord)
			bmoRaccount.getPaymentStatus().setValue(BmoRaccount.PAYMENTSTATUS_PENDING);

		// Agregar bitacora interna de la tarea, si hay comentario
		if (!bmoRaccount.getComments().toString().equals("")) {
			String commentLog = SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat())
					+ " (" + getSFParams().getLoginInfo().getBmoUser().getCode() + "), "
					+ getSFParams().getFieldFormTitle(bmoRaccount.getDueDate()) + ": " + bmoRaccount.getDueDate().toString() + ", "
					+ getSFParams().getFieldFormTitle(bmoRaccount.getBalance()) + ": " + SFServerUtil.formatCurrency(bmoRaccount.getBalance().toDouble()) + ", "
					+ "\n" + getSFParams().getFieldFormTitle(bmoRaccount.getComments()) + ": "+ bmoRaccount.getComments().toString()
					+ "\n\n" + bmoRaccount.getCommentLog().toString();
			bmoRaccount.getCommentLog().setValue(commentLog);
			bmoRaccount.getComments().setValue("");
		}

		// Genera bitácora si viene de Pedido
		if (bmoOrder.getId() > 0) {
			String action = "Modificó";
			if (newRecord) action = "Creó";
			addWFlowLog(pmConn, bmoOrder, bmoRaccount, action, bmUpdateResult);
		}
		if (getFiles(programId, bmoRaccount.getId())) {
			bmoRaccount.getFile().setValue("1");
		}else {
			bmoRaccount.getFile().setValue("0");
		}

		if (!bmUpdateResult.hasErrors()) {
			super.save(pmConn, bmoRaccount, bmUpdateResult);
		}

		return bmUpdateResult;
	}

	// Actualiza saldos y pagos de la cuenta x cobrar
	public void updateBalance(PmConn pmConn, BmoRaccount bmoRaccount, BmUpdateResult bmUpdateResult)
			throws SFException {
		PmRaccountType pmRaccountType = new PmRaccountType(getSFParams());
		BmoRaccountType bmoRaccountType = (BmoRaccountType) pmRaccountType.get(pmConn,
				bmoRaccount.getRaccountTypeId().toInteger());

		// Validar tipo de CxC
		if (bmoRaccountType.getType().equals(BmoRaccountType.TYPE_WITHDRAW)) {
			// Validar si tiene items, sumar los item y asignar el valor a la
			// cantidad de la cuenta por cobrar
			bmoRaccount.getAmount().setValue(
					SFServerUtil.roundCurrencyDecimals(this.sumRaccountItems(pmConn, bmoRaccount, bmUpdateResult)));

			super.save(pmConn, bmoRaccount, bmUpdateResult);

			// Actualiza balance del pedido
			if (bmoRaccount.getOrderId().toInteger() > 0) {
				PmOrder pmOrder = new PmOrder(getSFParams());
				BmoOrder bmoOrder = (BmoOrder) pmOrder.get(pmConn, bmoRaccount.getOrderId().toInteger());

				// Validar si tiene items, sumar los item y asignar el valor a
				// la cantidad de la cuenta por cobrar
				bmoRaccount.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(this.sumRaccountItems(pmConn, bmoRaccount, bmUpdateResult)));

				// Revisar si aplica IVA
				if (bmoRaccount.getTaxApplies().toBoolean()) {
					double taxRate = (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getTax().toDouble() / 100);
					double tax = (bmoRaccount.getAmount().toDouble() * taxRate);
					bmoRaccount.getTax().setValue(SFServerUtil.roundCurrencyDecimals(tax));
					bmoRaccount.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(bmoRaccount.getAmount().toDouble() + tax));
				} else {
					bmoRaccount.getTax().setValue(0);
					bmoRaccount.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(bmoRaccount.getAmount().toDouble()));
				}

				// Se actualiza estatus de pago
				this.updateWithdrawBalance(pmConn, bmoRaccount, bmUpdateResult);

				// Asegura saldo de las cxc
				if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnsureProcessCxC().toBoolean()
						&& !bmoOrder.getStatus().equals(BmoOrder.STATUS_CANCELLED)) {
					if (!bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT))
						if (!bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)) 
							ensureOrderBalance(pmConn, bmoOrder, bmUpdateResult);
				}

				// Actualizar el status de pago del pedido
				pmOrder.updatePayments(pmConn, bmoOrder, bmUpdateResult);

				// Actualizar estatus de pago del contrato
				if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)) {
					updatePaymentStatusPropertyRental(pmConn, bmoOrder, bmUpdateResult);
				}
			} 
			// No es de pedido
			else {
				if (bmoRaccount.getTaxApplies().toBoolean()) {
					// Calcular el IVA
					double taxRate = (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getTax().toDouble() / 100);
					double tax = (bmoRaccount.getAmount().toDouble() * taxRate);
					bmoRaccount.getTax().setValue(SFServerUtil.roundCurrencyDecimals(tax));
					bmoRaccount.getTotal()
					.setValue(SFServerUtil.roundCurrencyDecimals(bmoRaccount.getAmount().toDouble() + tax));
				} else {
					bmoRaccount.getAmount().setValue(SFServerUtil
							.roundCurrencyDecimals(this.sumRaccountItems(pmConn, bmoRaccount, bmUpdateResult)));
					bmoRaccount.getTax().setValue(0);
					bmoRaccount.getTotal()
					.setValue(SFServerUtil.roundCurrencyDecimals(bmoRaccount.getAmount().toDouble()));
				}

				// Se actualiza estatus de pago
				this.updateWithdrawBalance(pmConn, bmoRaccount, bmUpdateResult);
			}

		} 
		// Es de tipo abono / nota de credito
		else {
			// Sumar los items de la cxc si es de tipo abono
			bmoRaccount.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(this.sumRaccountItems(pmConn, bmoRaccount, bmUpdateResult)));

			// Revisar si aplica IVA
			if (bmoRaccount.getTaxApplies().toBoolean()) {
				double taxRate = (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getTax().toDouble() / 100);
				double tax = (bmoRaccount.getAmount().toDouble() * taxRate);
				bmoRaccount.getTax().setValue(SFServerUtil.roundCurrencyDecimals(tax));
				bmoRaccount.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(bmoRaccount.getAmount().toDouble() + tax));
			} else {
				bmoRaccount.getTax().setValue(0);
				bmoRaccount.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(bmoRaccount.getAmount().toDouble()));
			}

			// Actualizar el status de pago del pedido
			if (bmoRaccount.getOrderId().toInteger() > 0) {
				PmOrder pmOrder = new PmOrder(getSFParams());
				BmoOrder bmoOrder = (BmoOrder) pmOrder.get(pmConn, bmoRaccount.getOrderId().toInteger());
				pmOrder.updatePayments(pmConn, bmoOrder, bmUpdateResult);
			}

			this.updateDepositBalance(pmConn, bmoRaccount, bmUpdateResult);

			super.save(pmConn, bmoRaccount, bmUpdateResult);
		}
	}

	// Actualizar saldo de los cargos
	private void updateWithdrawBalance(PmConn pmConn, BmoRaccount bmoRaccount, BmUpdateResult bmUpdateResult)
			throws SFException {
		// Obtener los pagos ligados al cargo
		double totalPayments = 0;
		String sql = "";

		//Sumar los conceptos de banco
		sql = " SELECT * FROM bankmovconcepts " +
				" LEFT JOIN bankmovements ON (bkmc_bankmovementid = bkmv_bankmovementid) " +
				" LEFT JOIN bankaccounts ON (bkmv_bankaccountid = bkac_bankaccountid ) " +
				" LEFT JOIN bankmovtypes ON (bkmv_bankmovtypeid = bkmt_bankmovtypeid) " +
				" LEFT JOIN raccounts ON (bkmc_raccountid = racc_raccountid) " +
				" LEFT JOIN currencies ON (bkac_currencyid = cure_currencyid) " +
				" WHERE bkmc_raccountid = " + bmoRaccount.getId() + 
				" AND bkmt_category <> '" + BmoBankMovType.CATEGORY_DEVOLUTIONCXC + "'"; 
		pmConn.doFetch(sql);
		while(pmConn.next()) {			
			if (pmConn.getDouble("bkmc_amountconverted") > 0)
				totalPayments += pmConn.getDouble("bkmc_amountconverted");
			else 
				totalPayments += pmConn.getDouble("bkmc_amount");
		}

		//Sumar las devoluciones de banco
		sql = " SELECT * FROM bankmovconcepts " +
				" LEFT JOIN bankmovements ON (bkmc_bankmovementid = bkmv_bankmovementid) " +
				" LEFT JOIN bankaccounts ON (bkmv_bankaccountid = bkac_bankaccountid ) " +
				" LEFT JOIN bankmovtypes ON (bkmv_bankmovtypeid = bkmt_bankmovtypeid) " +
				" LEFT JOIN raccounts ON (bkmc_raccountid = racc_raccountid) " +
				" LEFT JOIN currencies ON (bkac_currencyid = cure_currencyid) " +
				" WHERE bkmc_raccountid = " + bmoRaccount.getId() + 
				" AND bkmt_category = '" + BmoBankMovType.CATEGORY_DEVOLUTIONCXC + "'"; 
		pmConn.doFetch(sql);
		while(pmConn.next()) {
			if (pmConn.getDouble("bkmc_amountconverted") > 0)
				totalPayments -= pmConn.getDouble("bkmc_amountconverted");
			else 
				totalPayments -= pmConn.getDouble("bkmc_amount");
		}

		//Notas de Crédito
		sql = " SELECT * FROM raccountassignments " +
				" LEFT JOIN raccounts ON (rass_raccountid = racc_raccountid) " +
				" LEFT JOIN currencies ON (racc_currencyid = cure_currencyid) " +	
				" LEFT JOIN raccounttypes ON (ract_raccounttypeid = racc_raccounttypeid) " +
				" WHERE ract_category  = '" + BmoRaccountType.CATEGORY_CREDITNOTE + "'" +  
				" AND ract_type = '" + BmoRaccountType.TYPE_DEPOSIT + "'" +				
				" AND rass_foreignraccountid IN ( " +
				" SELECT racc_raccountid FROM raccounts " +
				"	LEFT JOIN raccounttypes ON (ract_raccounttypeid = racc_raccounttypeid) " +  
				"	WHERE ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" +
				"	AND racc_raccountid = " + bmoRaccount.getId() +				
				"  ) ";
		pmConn.doFetch(sql);		
		while (pmConn.next()) {
			if (pmConn.getDouble("rass_amountconverted") > 0) {
				totalPayments += pmConn.getDouble("rass_amountconverted");
			} else {
				totalPayments += pmConn.getDouble("rass_amount");
			}
		}

		// Asigna estatus
		if (bmoRaccount.getTotal().toDouble() <= totalPayments && bmoRaccount.getTotal().toDouble() > 0)
			bmoRaccount.getPaymentStatus().setValue(BmoRaccount.PAYMENTSTATUS_TOTAL);
		else
			bmoRaccount.getPaymentStatus().setValue(BmoRaccount.PAYMENTSTATUS_PENDING);


		bmoRaccount.getBalance().setValue(SFServerUtil.roundCurrencyDecimals(bmoRaccount.getTotal().toDouble() - totalPayments));
		bmoRaccount.getPayments().setValue(SFServerUtil.roundCurrencyDecimals(totalPayments));

		// Almacenar el cambio de estatus
		super.save(pmConn, bmoRaccount, bmUpdateResult);
	}

	// Actualiza los balances de la CxC
	private void updateDepositBalance(PmConn pmConn, BmoRaccount bmoRaccount, BmUpdateResult bmUpdateResult)
			throws SFException {
		// Obtener los pagos ligados al cargo
		double totalPayments = 0;
		String sql = "";

		// Notas de Crédito
		sql = " SELECT * FROM raccountassignments " +
				" LEFT JOIN raccounts ON (rass_raccountid = racc_raccountid) " +
				" LEFT JOIN currencies ON (racc_currencyid = cure_currencyid) " +	
				" LEFT JOIN raccounttypes ON (ract_raccounttypeid = racc_raccounttypeid) " +
				" WHERE ract_category  = '" + BmoRaccountType.CATEGORY_CREDITNOTE + "'" +  
				" AND ract_type = '" + BmoRaccountType.TYPE_DEPOSIT + "'" +
				" AND racc_raccountid = " + bmoRaccount.getId();
		pmConn.doFetch(sql);		
		while (pmConn.next()) {
			//if (pmConn.getDouble("rass_currencyparity") != 1) 
			if (pmConn.getDouble("rass_amountconverted") > 0) 
				totalPayments += pmConn.getDouble("rass_amountconverted");
			else 
				totalPayments += pmConn.getDouble("rass_amount");
		}

		// Sumar las devoluciones de banco
		sql = " SELECT * FROM bankmovconcepts " +
				" LEFT JOIN bankmovements ON (bkmc_bankmovementid = bkmv_bankmovementid) " +
				" LEFT JOIN bankaccounts ON (bkmv_bankaccountid = bkac_bankaccountid ) " +
				" LEFT JOIN bankmovtypes ON (bkmv_bankmovtypeid = bkmt_bankmovtypeid) " +
				" LEFT JOIN raccounts ON (bkmc_raccountid = racc_raccountid) " +
				" LEFT JOIN currencies ON (bkac_currencyid = cure_currencyid) " +
				" WHERE bkmc_raccountid = " + bmoRaccount.getId() + 
				" AND bkmt_category = '" + BmoBankMovType.CATEGORY_DEVOLUTIONCXC + "'"; 
		pmConn.doFetch(sql);
		while(pmConn.next()) {
			if (pmConn.getDouble("bkmc_amountconverted") > 0)	
				totalPayments += pmConn.getDouble("bkmc_amountconverted");
			else 
				totalPayments += pmConn.getDouble("bkmc_amount");
		}

		// Asigna estatus
		if (bmoRaccount.getTotal().toDouble() <= totalPayments)
			bmoRaccount.getPaymentStatus().setValue(BmoRaccount.PAYMENTSTATUS_TOTAL);
		else
			bmoRaccount.getPaymentStatus().setValue(BmoRaccount.PAYMENTSTATUS_PENDING);

		// Calcular los saldos
		bmoRaccount.getBalance().setValue(SFServerUtil.roundCurrencyDecimals(bmoRaccount.getTotal().toDouble() - totalPayments));
		bmoRaccount.getPayments().setValue(SFServerUtil.roundCurrencyDecimals(totalPayments));

		// Almacenar el cambio de estatus
		super.save(pmConn, bmoRaccount, bmUpdateResult);

	}
	
	// Revisa si tiene pagos aplicados
	public boolean hasPayments(PmConn pmConn, int raccountId) throws SFPmException {
		boolean hasPayment = false;
		if (payments(pmConn, raccountId) > 0) hasPayment = true;
		return hasPayment;
	}
	
	// Obtener SOLO el monto de lo pagos
	public double payments(PmConn pmConn, int raccountId) throws SFPmException {
		double payments = 0;
		// Sumar los conceptos de banco
		String sql = " SELECT * FROM bankmovconcepts " +
				" LEFT JOIN bankmovements ON (bkmc_bankmovementid = bkmv_bankmovementid) " +
				" LEFT JOIN bankaccounts ON (bkmv_bankaccountid = bkac_bankaccountid ) " +
				" LEFT JOIN bankmovtypes ON (bkmv_bankmovtypeid = bkmt_bankmovtypeid) " +
				" LEFT JOIN raccounts ON (bkmc_raccountid = racc_raccountid) " +
				" LEFT JOIN currencies ON (bkac_currencyid = cure_currencyid) " +
				" WHERE bkmc_raccountid = " + raccountId + 
				" AND bkmt_category <> '" + BmoBankMovType.CATEGORY_DEVOLUTIONCXC + "'"; 
		pmConn.doFetch(sql);
		while(pmConn.next()) {			
			if (pmConn.getDouble("bkmc_amountconverted") > 0)
				payments += pmConn.getDouble("bkmc_amountconverted");
			else 
				payments += pmConn.getDouble("bkmc_amount");
		}

		// Notas de Crédito
		sql = " SELECT * FROM raccountassignments " +
				" LEFT JOIN raccounts ON (rass_raccountid = racc_raccountid) " +
				" LEFT JOIN currencies ON (racc_currencyid = cure_currencyid) " +	
				" LEFT JOIN raccounttypes ON (ract_raccounttypeid = racc_raccounttypeid) " +
				" WHERE ract_category  = '" + BmoRaccountType.CATEGORY_CREDITNOTE + "'" +  
				" AND ract_type = '" + BmoRaccountType.TYPE_DEPOSIT + "'" +				
				" AND rass_foreignraccountid IN ( " +
				" SELECT racc_raccountid FROM raccounts " +
				"	LEFT JOIN raccounttypes ON (ract_raccounttypeid = racc_raccounttypeid) " +  
				"	WHERE ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" +
				"	AND racc_raccountid = " + raccountId +				
				"  ) ";
		pmConn.doFetch(sql);		
		while (pmConn.next()) {
			if (pmConn.getDouble("rass_amountconverted") > 0) {
				payments += pmConn.getDouble("rass_amountconverted");
			} else {
				payments += pmConn.getDouble("rass_amount");
			}
		}
		
		// Redondear decimales
		payments = Double.parseDouble(SFServerUtil.roundCurrencyDecimals(payments));
		
		return payments;
	}

	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value)	throws SFException {
		double result = 0;

		// Obtener el saldo de la orden de compra
		if (action.equals(BmoRaccount.ACTION_ORDERBALANCE)) {
			result = getOrderBalance(Integer.parseInt(value), bmUpdateResult);
			bmUpdateResult.setMsg("" + SFServerUtil.formatCurrency(result));
		} else if (action.equals(BmoRaccount.ACTION_ORDERPROVISION)) {
			result = getOrderProvision(Integer.parseInt(value), bmUpdateResult);
			bmUpdateResult.setMsg("" + SFServerUtil.formatCurrency(result));
		} else if (action.equals(BmoRaccount.ACTION_ORDERPENDINGPROVISION)) {
			result = getOrderPendingProvision(Integer.parseInt(value), bmUpdateResult) - getOrderProvision(Integer.parseInt(value), bmUpdateResult);
			bmUpdateResult.setMsg("" + SFServerUtil.formatCurrency(result));
		} else if (action.equals(BmoRaccount.ACTION_ORDERPAYMENTS)) {
			result = getOrderPayments(Integer.parseInt(value), bmUpdateResult);
			bmUpdateResult.setMsg("" + result);
		} else if (action.equals(BmoRaccount.ACTION_ORDERFAILURE)) {
			result = getOrderFailure(Integer.parseInt(value), bmUpdateResult);
			bmUpdateResult.setMsg("" + result);
		} else if (action.equals(BmoRaccount.ACTION_AMOUNTAUTOCREATE)) {
			bmUpdateResult.setMsg("" + SFServerUtil.roundCurrencyDecimals(getAmountAutoCreate(Integer.parseInt(value), bmUpdateResult)));
		} else if (action.equals(BmoRaccount.ACTION_MULTIPLERACCOUNT)) {
			bmUpdateResult = multipleRaccount(value, bmUpdateResult);		
		} else if (action.equals(BmoRaccount.ACTION_GETCUSTOMERBYRACC)) {
			bmUpdateResult.setMsg("" + getCustByRacc(value, bmUpdateResult));
		} else if (action.equals(BmoRaccount.ACTION_DUEDATEAPP)) {
			bmUpdateResult.setMsg(getDueDateCobi(bmUpdateResult));
		} else if (action.equals(BmoRaccount.ACTION_GETBUDGETITEM)) {
			bmUpdateResult.setMsg("" + getBudgetItem(value, bmUpdateResult));
		} else if (action.equals(BmoRaccount.ACTION_GETBANKACCOUNT)) {
			bmUpdateResult.setMsg(getBankAccount(value, bmUpdateResult));
		} else if (action.equals(BmoRaccount.ACTION_GETPAYMENTTYPE)) {
			bmUpdateResult.setMsg(getPaymentType(value, bmUpdateResult));	
		} else if (action.equals(BmoRaccount.ACTION_DELETEPAYORDER)) {
			bmUpdateResult = deletePayOrder(value, bmUpdateResult);
		} else if (action.equals(BmoRaccount.ACTION_MULTIPLEPAYMENT)) {
			bmUpdateResult = multiplePaymentApp(value, bmUpdateResult);
		} else if (action.equals(BmoRaccount.ACTION_PAYMENTAPP)) {
			bmUpdateResult = paymentApp(value, bmUpdateResult);
		} else if (action.equals(BmoRaccount.ACTION_PAYDATE)) {
			bmUpdateResult.setMsg(changePaymentDate(value, bmUpdateResult));
		} else if (action.equals(BmoRaccount.ACTION_SUMCCPENDINGNOLINKED)) {
			StringTokenizer tabs = new StringTokenizer(value, "|");
			String status = "", customerId = "", leaderUserId = "", assignedUserId = "", areaId = "", 
					currencyId = "", startDateOrdt = "", endDateOrdt = "";
			if (tabs.hasMoreTokens()) {
				status = tabs.nextToken();
				customerId = tabs.nextToken();
				leaderUserId = tabs.nextToken();
				assignedUserId = tabs.nextToken();
				areaId = tabs.nextToken();
				currencyId = tabs.nextToken();
				startDateOrdt = tabs.nextToken();
				endDateOrdt = tabs.nextToken();
			}

			result = getSumCCPendingWithoutLinked(status, customerId, leaderUserId, assignedUserId, areaId, currencyId, startDateOrdt, endDateOrdt, bmUpdateResult);
			bmUpdateResult.setMsg("" + result);
		}

		return bmUpdateResult;
	}

	private String changePaymentDate(String value, BmUpdateResult bmUpdateResult) throws SFException {
		String result = "";
		try {			
			int customerId = 0;
			String dateReceive = "";
			String dateDueDate = "";
			StringTokenizer tabs = new StringTokenizer(value, "|");
			while (tabs.hasMoreTokens()) {
				customerId = Integer.parseInt(tabs.nextToken());
				dateReceive = tabs.nextToken();	
				dateDueDate = tabs.nextToken();
			}

			result = dateDueDate;

			//Calcular fecha de registro (el metodo de pago del cliente) 
			PmCustomer pmCustomer = new PmCustomer(getSFParams());
			BmoCustomer bmoCustomer = (BmoCustomer)pmCustomer.get(customerId);					
			if (bmoCustomer.getReqPayTypeId().toInteger() > 0) {
				PmReqPayType pmReqPayType = new PmReqPayType(getSFParams());
				BmoReqPayType bmoReqPayType = (BmoReqPayType)pmReqPayType.get(bmoCustomer.getReqPayTypeId().toInteger());

				if (bmoCustomer.getReqPayTypeId().toInteger() > 0) {	
					// Sumar a la fecha de registros los dias de pago
					Calendar calNow = SFServerUtil.stringToCalendar(getSFParams().getDateFormat(), dateReceive);				
					calNow.add(Calendar.DAY_OF_YEAR, bmoReqPayType.getDays().toInteger());

					//Asignar la fecha de registro				
					result = FlexUtil.calendarToString(getSFParams(), calNow);
				}	
			}

		} catch (SFException e) {
			bmUpdateResult.addError(bmoRaccount.getReceiveDate().getName(), "Error al calcular la fecha de pago");
		} 

		return result;
	}

	//Obtener el tipo de pago
	private String getPaymentType(String value, BmUpdateResult bmUpdateResult) throws SFException {
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();
		String paymentTypeName = ""; 
		try {
			//Obtener el Concepto y el MB
			PmPaymentType pmPaymentType = new PmPaymentType(getSFParams());
			BmoPaymentType bmoPaymentType = (BmoPaymentType)pmPaymentType.get(Integer.parseInt(value));

			paymentTypeName = bmoPaymentType.getName().toString();

		} catch (SFException e) {
			bmUpdateResult.addError(bmoRaccount.getInvoiceno().getName(), "Error al obtener el tipo de pago");
		} finally {
			pmConn.close();
		}

		return paymentTypeName;
	}

	//Obtener el usuario de flujo para cobranza
	private void asignCollectUser(PmConn pmConn, BmoRaccount bmoRaccount, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult) throws SFException {
		int collectProfileId = 0;
		// MultiEmpresa: g100
		if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getMultiCompany().toBoolean() ) {
			// Buscar perfil de cobranza por multiempresa
			PmFlexConfig pmFlexConfig = new PmFlexConfig(getSFParams());
			collectProfileId = pmFlexConfig.getCollectProfileId(pmConn, bmoRaccount.getCompanyId().toInteger());
		} else {
			// Buscar perfil de cobranza asignado en conf. flex
			collectProfileId = ((BmoFlexConfig) getSFParams().getBmoAppConfig()).getCollectProfileId().toInteger();
		}
		// Si existe algun perfil, Asignar el usuario de flujo del perfil de cobranza del flujo
		if (collectProfileId > 0) {
			String sql = "";
			sql = " SELECT wflu_userid FROM wflowusers " +
					" WHERE wflu_wflowid = " + bmoOrder.getWFlowId().toInteger() +
					" AND wflu_profileid = " + collectProfileId;
			printDevLog("sql_collectorProfile: " + sql);
			pmConn.doFetch(sql);	    
			if (pmConn.next()) {
				bmoRaccount.getCollectorUserId().setValue(pmConn.getInt("wflu_userid"));
			}
		}
	}

	//Eliminar Pago del Pedido
	private String getBankAccount(String value, BmUpdateResult bmUpdateResult) throws SFException {
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();
		String bkacName = ""; 
		String sql = "";
		try {
			//Obtener el Concepto y el MB
			sql = " SELECT  FROM bankmovconcepts " +
					" WHERE bkmc_raccountid > 0 " +
					" AND bkmc_foreignid = " + value;
			pmConn.doFetch(sql);
			if (pmConn.next()) {
				bkacName = pmConn.getString("bkac_name");
			}

		} catch (SFException e) {
			bmUpdateResult.addError(bmoRaccount.getInvoiceno().getName(), "Error al obtener la cuenta de banco");
		} finally {
			pmConn.close();
		}

		return bkacName;
	}

	//Obtener la cuenta de banco
	private BmUpdateResult deletePayOrder(String value, BmUpdateResult bmUpdateResult) throws SFException {
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();
		int bankMovConceptId = 0;
		try {
			pmConn.disableAutoCommit();

			bankMovConceptId = Integer.parseInt(value);

			//Eliminar el concepto
			PmBankMovConcept pmBankMovConcept = new PmBankMovConcept(getSFParams());
			BmoBankMovConcept bmoBankMovConcept = (BmoBankMovConcept)pmBankMovConcept.get(pmConn, bankMovConceptId);

			if (bmoBankMovConcept != null) {

				pmBankMovConcept.delete(pmConn, bmoBankMovConcept, bmUpdateResult);

				//Eliminar el Mov.banco
				PmBankMovement pmBankMovement = new PmBankMovement(getSFParams());
				BmoBankMovement bmoBankMovement = (BmoBankMovement)pmBankMovement.get(pmConn, bmoBankMovConcept.getBankMovementId().toInteger());

				pmBankMovement.delete(pmConn, bmoBankMovement, bmUpdateResult);
			} else {
				//Eliminar Nota de credito
				BmoRaccount bmoRaccount = new BmoRaccount();
				bmoRaccount = (BmoRaccount)this.get(Integer.parseInt(value));
				if (bmoRaccount.getBmoRaccountType().getCategory().equals(BmoRaccountType.CATEGORY_CREDITNOTE)) {
					this.delete(pmConn, bmoRaccount, bmUpdateResult);
				}
			}

			if (!bmUpdateResult.hasErrors())
				pmConn.commit();

		} catch (SFException e) {
			pmConn.rollback();
			bmUpdateResult.addError(bmoRaccount.getInvoiceno().getName(), "Error al obtener la cuenta de banco");
		} finally {
			pmConn.close();
		}

		return bmUpdateResult;
	}

	// Obtener la presupuesto en las CxC
	private int getBudgetItem(String value, BmUpdateResult bmUpdateResult) throws SFException {
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();
		int budgetItemId = 0;
		try {
			PmOrder pmOrder = new PmOrder(getSFParams());
			BmoOrder bmoOrder = (BmoOrder) pmOrder.get(Integer.parseInt(value));

			budgetItemId = pmOrder.getBudgetItemByOrder(pmConn, bmoOrder, bmUpdateResult);

		} catch (SFException e) {
			bmUpdateResult.addError(bmoRaccount.getBudgetItemId().getName(), "Error al obtener el presupuesto");
		} finally {
			pmConn.close();
		}

		return budgetItemId;
	}

	// Obtener la fecha de pago (Lunes) Cobi
	private String getDueDateCobi(BmUpdateResult bmUpdateResult) throws SFException {
		// Obtener el Lunes de esta semana
		Calendar payWeek = SFServerUtil.stringToCalendar(getSFParams().getDateFormat(),
				SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
		// Obtener el día
		int nowDay = payWeek.get(Calendar.DAY_OF_WEEK);

		// System.out.println("nowDay " + nowDay);
		// Sunday
		if (nowDay == 1)
			nowDay = 6;
		// Monday
		else if (nowDay == 2)
			nowDay = 0;
		// Thusday
		else if (nowDay == 3)
			nowDay = 1;
		// Weendays
		else if (nowDay == 4)
			nowDay = 2;
		// Thurday
		else if (nowDay == 5)
			nowDay = 3;
		// Friday
		else if (nowDay == 6)
			nowDay = 4;
		// Saturday
		else if (nowDay == 7)
			nowDay = 5;

		payWeek.add(Calendar.DAY_OF_WEEK, -nowDay);

		String datePayout = FlexUtil.calendarToString(getSFParams(), payWeek);

		return datePayout;
	}

	private int getCustByRacc(String raccountId, BmUpdateResult bmUpdateResult) throws SFException {
		BmoRaccount bmoRaccount = (BmoRaccount) this.get(Integer.parseInt(raccountId));
		return bmoRaccount.getCustomerId().toInteger();
	}

	// Obtener cantidad de cxc del pedido
	private double getOrderProvision(int orderId, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		double amount = 0;

		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder bmoOrder = (BmoOrder) pmOrder.get(orderId);

		PmConn pmConn = null;

		try {

			pmConn = new PmConn(getSFParams());
			pmConn.open();

			// Obtener el total de las cxc manuales
			double raccountAmount = 0;

			sql = " SELECT SUM(racc_total) as amount FROM raccounts "
					+ " LEFT JOIN raccounttypes on (racc_raccounttypeid = ract_raccounttypeid) "
					+ " WHERE ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" + " AND racc_orderid = "
					+ bmoOrder.getId() + " AND racc_currencyid = " + bmoOrder.getCurrencyId().toInteger();
			pmConn.doFetch(sql);
			if (pmConn.next()) {
				raccountAmount = pmConn.getDouble("amount");
			}

			double amountDiffCure = 0;

			double orderParity = 0;

			if (bmoOrder.getCurrencyParity().toDouble() > 0)
				orderParity = bmoOrder.getCurrencyParity().toDouble();
			else
				orderParity = bmoOrder.getBmoCurrency().getParity().toDouble();

			sql = " SELECT * FROM raccounts "
					+ " LEFT JOIN raccounttypes on (racc_raccounttypeid = ract_raccounttypeid) "
					+ " WHERE ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" + " AND racc_autocreate = 0 "
					+ " AND racc_orderid = " + bmoOrder.getId() + " AND racc_currencyid <> "
					+ bmoOrder.getCurrencyId().toInteger();
			pmConn.doFetch(sql);
			while (pmConn.next()) {
				amountDiffCure += (pmConn.getDouble("racc_total") * pmConn.getDouble("racc_currencyparity"))
						/ orderParity;
			}

			amount = raccountAmount + amountDiffCure;

		} catch (SFPmException e) {
			throw new SFException(this.getClass().getName() + "getOrderProvision(): " + e.toString());
		} finally {
			pmConn.close();
		}

		return amount;
	}


	// Revisa si tiene penalizacion
	public boolean hasPenalty(PmConn pmConn, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		int items = 0;
		boolean result = false;

		// Obtener el Credito
		PmCredit pmCredit = new PmCredit(getSFParams());
		BmoCredit bmoCredit = new BmoCredit();
		bmoCredit = (BmoCredit) pmCredit.getBy(pmConn, bmoOrder.getId(), bmoCredit.getOrderId().getName());

		sql = " SELECT COUNT(*) AS items FROM raccounts "
				+ " LEFT JOIN raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) " + " WHERE racc_orderid = "
				+ bmoOrder.getId() + " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'"
				+ " AND racc_failure = 1 ";
		pmConn.doFetch(sql);
		if (pmConn.next())
			items = pmConn.getInt("items");

		if (items > 0)
			result = true;

		return result;
	}

	// Obtener el Saldo del pedido
	private double getOrderPendingProvision(int orderId, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		double withDraw = 0;
		PmConn pmConn = null;
		try {
			pmConn = new PmConn(getSFParams());
			pmConn.open();

			sql = " select orde_total from orders " + " where orde_orderid = " + orderId;
			pmConn.doFetch(sql);
			if (pmConn.next()) {
				withDraw = pmConn.getDouble("orde_total");
			}

		} catch (SFPmException e) {
			throw new SFException(this.getClass().getName() + "getOrderPendingProvision(): " + e.toString());
		} finally {
			pmConn.close();
		}

		return withDraw;
	}

	// Obtener el Saldo del pedido
	private double getOrderBalance(int orderId, BmUpdateResult bmUpdateResult) throws SFException {
		double balance = 0;

		PmOrder pmOrder = new PmOrder(getSFParams());
		balance = pmOrder.getOrderBalance(orderId, bmUpdateResult);

		return balance;
	}

	private double getAmountAutoCreate(int orderId, BmUpdateResult bmUpdateResult) throws SFPmException {
		double amount = 0;
		double provision = 0;
		try {
			amount = getOrderBalance(orderId, bmUpdateResult);
			provision = getOrderProvision(orderId, bmUpdateResult);

			PmOrder pmOrder = new PmOrder(getSFParams());
			BmoOrder bmoOrder = (BmoOrder) pmOrder.get(orderId);

			if (bmoOrder.getTax().toDouble() > 0) {
				// Al monto quitar el iva
				double taxRate = (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getTax().toDouble() / 100);
				amount = (amount / (taxRate + 1));
				provision = (provision / (taxRate + 1));
			}
			amount = amount - provision;
		} catch (SFException e) {
			System.out.println(this.getClass().getName() + "-getAmountAutoCreate(): " + e.toString());
		}
		return amount;
	}

	// Obtener el Saldo del pedido
	private double getOrderPayments(int orderId, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		double totalPayments = 0;

		PmConn pmConn = null;

		try {
			pmConn = new PmConn(getSFParams());
			pmConn.open();

			// Obtener el total del credito
			BmoCredit bmoCredit = new BmoCredit();
			PmCredit pmCredit = new PmCredit(getSFParams());
			try {
				bmoCredit = (BmoCredit) pmCredit.getBy(pmConn, orderId, bmoCredit.getOrderId().getName());
			} catch (SFException e) {
				System.out.println(this.getClass().getName() + "-getOrderPayments(): " + e.toString());
			}

			//Sumar los conceptos de banco, no tomar en cuenta la entra del dinero
			sql = " SELECT * FROM bankmovconcepts " +
					" LEFT JOIN bankmovements ON (bkmc_bankmovementid = bkmv_bankmovementid) " +
					" LEFT JOIN bankaccounts ON (bkmv_bankaccountid = bkac_bankaccountid ) " +
					" LEFT JOIN bankmovtypes ON (bkmv_bankmovtypeid = bkmt_bankmovtypeid) " +
					" LEFT JOIN raccounts ON (bkmc_raccountid = racc_raccountid) " +
					" LEFT JOIN currencies ON (bkac_currencyid = cure_currencyid) " +
					" WHERE racc_orderid = " + orderId + 
					" AND bkmt_category = '" + BmoBankMovType.CATEGORY_CXC + "'";
			pmConn.doFetch(sql);
			while(pmConn.next()) {			
				if (pmConn.getDouble("bkmc_amountconverted") > 0)
					totalPayments += pmConn.getDouble("bkmc_amountconverted");
				else 
					totalPayments += pmConn.getDouble("bkmc_amount");
			}

			//Sumar las notas de crédito
			sql = " SELECT SUM(rass_amount) as totalpayments FROM raccountassignments " +
					" LEFT JOIN raccounts ON (rass_foreignraccountid = racc_raccountid) " +					
					" WHERE racc_orderid = " + orderId +
					" AND rass_amountconverted is null";		             
			pmConn.doFetch(sql);
			if (pmConn.next()) {
				totalPayments += pmConn.getDouble("totalpayments");
			}

			//Sumar las notas de crédito de otra moneda
			sql = " SELECT SUM(rass_amountconverted) as totalpayments FROM raccountassignments " +		              
					" LEFT JOIN raccounts ON (rass_foreignraccountid = racc_raccountid) " +					
					" WHERE racc_orderid = " + orderId +
					" AND not rass_amountconverted is null";		             
			pmConn.doFetch(sql);
			if (pmConn.next()) {
				totalPayments += pmConn.getDouble("totalpayments");
			}

		} catch (SFPmException e) {
			throw new SFException(this.getClass().getName() + "getOrderPayments(): " + e.toString());
		} finally {
			pmConn.close();
		}

		return totalPayments;
	}

	private double getOrderFailure(int orderId, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		double failure = 0;

		PmConn pmConn = null;

		try {
			pmConn = new PmConn(getSFParams());
			pmConn.open();

			// Obtener el total de Abonos ligados a la orden de compra
			sql = " select SUM(racc_total) as failure from raccounts ";
			sql += " left join raccounttypes on (racc_raccounttypeid = ract_raccounttypeid)";
			sql += " where racc_orderid = " + orderId;
			sql += " and ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'";
			sql += " and racc_failure = 1 ";
			pmConn.doFetch(sql);
			if (pmConn.next()) {
				failure = pmConn.getDouble("failure");
			}

		} catch (SFPmException e) {
			throw new SFException(this.getClass().getName() + "getOrderFailure(): " + e.toString());
		} finally {
			pmConn.close();
		}

		return failure;
	}

	// Crear penalizacion
	public void createPenalty(PmConn pmConn, BmoOrder bmoOrder, int raccountOrignId, BmUpdateResult bmUpdateResult) throws SFException {
		PmConn pmConn2 = new PmConn(getSFParams());
		pmConn2.open();

		try {
			String sql = "";
			double amount = 0;
			String lastDate = "";

			// Obtener el total de credito
			BmoCredit bmoCredit = new BmoCredit();
			PmCredit pmCredit = new PmCredit(getSFParams());
			bmoCredit = (BmoCredit) pmCredit.getBy(bmoOrder.getId(), bmoCredit.getOrderId().getName());

			// Actualizar el estatus pago
			bmoCredit.getPaymentStatus().setValue(BmoCredit.PAYMENTSTATUS_PENALTY);
			pmCredit.saveSimple(pmConn, bmoCredit, bmUpdateResult);

			// Obtener la Ultima CxC
			sql = " SELECT * FROM raccounts "
					+ " LEFT JOIN raccounttypes on (racc_raccounttypeid = ract_raccounttypeid) "
					+ " WHERE ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" 
					+ " AND racc_orderid = " + bmoOrder.getId() 
					+ " AND racc_total <> " + bmoCredit.getAmount().toDouble()
					+ " ORDER BY racc_raccountid DESC";
			pmConn2.doFetch(sql);
			if (pmConn2.next()) {
				amount = pmConn2.getDouble("racc_total");
				lastDate = pmConn2.getString("raccounts", "racc_duedate");
			}

			// Obtener el Plazo
			PmCreditType pmCreditType = new PmCreditType(getSFParams());
			BmoCreditType bmoCreditType = (BmoCreditType) pmCreditType.get(pmConn, bmoCredit.getCreditTypeId().toInteger());

			// Obtener los dias de pago
			// Asignar monto de la falla dependiendo del tipo de credito
			int daysPayout = 0;
			if (bmoCreditType.getType().equals(BmoCreditType.TYPE_DAILY)) {
				daysPayout = 1;
				amount = bmoCreditType.getAmountFailure().toDouble();
			} else if (bmoCreditType.getType().equals(BmoCreditType.TYPE_WEEKLY)) {
				daysPayout = 7;
				// Si hay un monto, tomar ese valor. Esto es para separar cobi de dacredito, cobi utiliza el total de las cxc
				if (bmoCreditType.getAmountFailure().toDouble() > 0)
					amount = bmoCreditType.getAmountFailure().toDouble();
			} else if (bmoCreditType.getType().equals(BmoCreditType.TYPE_TWOWEEKS)) {
				daysPayout = 15;
			} else if (bmoCreditType.getType().equals(BmoCreditType.TYPE_MONTHLY)) {
				daysPayout = 30;
			} else {
				bmUpdateResult.addMsg("El tipo de crédito no cuenta con un tipo definido");
			}

			String datePayout = SFServerUtil.addDays(getSFParams().getDateFormat(), lastDate, daysPayout);

			PmRaccount pmNewRaccount = new PmRaccount(getSFParams());
			BmoRaccount bmoNewRaccount = new BmoRaccount();

			bmoNewRaccount.getInvoiceno().setValue("Penalización");
			bmoNewRaccount.getReceiveDate().setValue(datePayout);
			bmoNewRaccount.getDueDate().setValue(datePayout);
			bmoNewRaccount.getDescription().setValue("Penalización.");
			bmoNewRaccount.getReference().setValue("Penalización.");
			bmoNewRaccount.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(amount));
			bmoNewRaccount.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(amount));
			bmoNewRaccount.getBalance().setValue(SFServerUtil.roundCurrencyDecimals(amount));
			bmoNewRaccount.getRaccountTypeId().setValue(((BmoFlexConfig) getSFParams().getBmoAppConfig()).getOrderRaccountTypeId().toInteger());
			bmoNewRaccount.getOrderId().setValue(bmoOrder.getId());
			bmoNewRaccount.getUserId().setValue(bmoOrder.getUserId().toInteger());
			bmoNewRaccount.getCompanyId().setValue(bmoOrder.getCompanyId().toInteger());
			bmoNewRaccount.getCustomerId().setValue(bmoOrder.getCustomerId().toInteger());
			bmoNewRaccount.getStatus().setValue(BmoRaccount.STATUS_AUTHORIZED);
			bmoNewRaccount.getAutoCreate().setValue(0);
			bmoNewRaccount.getFailure().setValue(1);
			bmoNewRaccount.getCurrencyId().setValue(bmoOrder.getCurrencyId().toInteger());
			bmoNewRaccount.getCurrencyParity().setValue(bmoOrder.getCurrencyParity().toDouble());
			bmoNewRaccount.getLinked().setValue(0);
			//bmoNewRaccount.getRelatedRaccountId().setValue(raccountOrignId);

			// super.save(pmConn, bmoNewRaccount, bmUpdateResult);
			pmNewRaccount.saveSimple(bmoNewRaccount, bmUpdateResult);

			bmoNewRaccount.setId(bmUpdateResult.getId());

			PmCompanyNomenclature pmCompanyNomenclature = new PmCompanyNomenclature(getSFParams());
			String code = "";
			// Generar clave personalizada si la hay, si no retorna la de por defecto
			code = pmCompanyNomenclature.getCodeCustom(pmConn,
					bmoNewRaccount.getCompanyId().toInteger(),
					bmoNewRaccount.getProgramCode().toString(),
					bmUpdateResult.getId(),
					BmoRaccount.CODE_PREFIX
					);
			bmoNewRaccount.getCode().setValue(code);
			//			bmoNewRaccount.getCode().setValue(bmoNewRaccount.getCodeFormat());

			// Crear el item con el monto de la liquidacion
			PmRaccountItem pmRaccItemNew = new PmRaccountItem(getSFParams());
			BmoRaccountItem bmoRaccItemNew = new BmoRaccountItem();
			bmoRaccItemNew.getName().setValue(bmoNewRaccount.getInvoiceno().toString());
			bmoRaccItemNew.getQuantity().setValue("1");
			bmoRaccItemNew.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(bmoNewRaccount.getAmount().toDouble()));
			bmoRaccItemNew.getPrice().setValue(SFServerUtil.roundCurrencyDecimals(bmoNewRaccount.getAmount().toDouble()));
			bmoRaccItemNew.getRaccountId().setValue(bmoNewRaccount.getId());

			pmRaccItemNew.saveSimple(pmConn, bmoRaccItemNew, bmUpdateResult);

			updatePenaltyFromNewFail(pmConn, bmoOrder, raccountOrignId, bmUpdateResult);

			if (!bmUpdateResult.hasErrors()) {
				// Actualizar id de claves del programa por empresa
				pmCompanyNomenclature.updateConsecutiveByCompany(pmConn, bmoNewRaccount.getCompanyId().toInteger(), 
						bmoNewRaccount.getProgramCode().toString());

				super.save(pmConn2, bmoNewRaccount, bmUpdateResult);
			}

			//			super.save(pmConn2, bmoNewRaccount, bmUpdateResult);

		} catch (SFException e) {
			bmUpdateResult.addMsg("Error al crear la penalidad " + e.toString());
		} finally {
			pmConn2.close();
		}
	}

	// Revisar que el pedido no tenga CxC Creadas
	public int orderHasRaccount(PmConn pmConn, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult) throws SFException {
		int items = 0;
		String sql = "";
		sql = " SELECT count(*) AS items FROM raccounts " +
				" LEFT JOIN raccounttypes ON (ract_raccounttypeid = racc_raccounttypeid) " +
				" WHERE racc_orderid = " + bmoOrder.getId();
		// No contar las notas de credito para de tipo arrendamiento
		if (bmoOrder.getBmoOrderType().getType().toChar() == BmoOrderType.TYPE_LEASE) {
			sql += " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "' ";
		}
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			items = pmConn.getInt("items");
		}

		return items;
	}

	// Valida los creditos del pedido
	public BmUpdateResult ensureOrderCredit(PmConn pmConn, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult) throws SFException {
		// Validar que no existan CxC Creadas
		//		if (!(orderHasRaccount(pmConn, bmoOrder, bmUpdateResult) > 0)) {
		// Credito
		PmCredit pmCredit = new PmCredit(getSFParams());
		BmoCredit bmoCredit = new BmoCredit();

		// Plazo(credito)
		PmCreditType pmCreditType = new PmCreditType(getSFParams());
		BmoCreditType bmoCreditType = new BmoCreditType();

		// Cliente
		PmCustomer pmCustomer = new PmCustomer(getSFParams());
		BmoCustomer bmoCustomer = new BmoCustomer();
		bmoCustomer = (BmoCustomer)pmCustomer.get(bmoOrder.getCustomerId().toInteger());

		// Arrendamiento
		PmPropertyRental pmPropertyRental = new PmPropertyRental(getSFParams());
		BmoPropertyRental bmoPropertyRental = new BmoPropertyRental();
		int countRaccount = 0;

		// variables credito
		String datePayout = "", rentalIncrease = "";
		int daysPayout = 0, deadLine = 0;
		double payout = 0;

		if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {
			// Obtener el credito
			bmoCredit = (BmoCredit) pmCredit.getBy(pmConn, bmoOrder.getId(), bmoCredit.getOrderId().getName());

			// Obtener el Plazo
			bmoCreditType = (BmoCreditType) pmCreditType.get(pmConn, bmoCredit.getCreditTypeId().toInteger());
			deadLine = bmoCreditType.getDeadLine().toInteger();

			if (bmoCreditType.getType().equals(BmoCreditType.TYPE_DAILY)) {
				daysPayout = 1;
				
				// CALCULAR siguiente dia de pago, debe ser el dia proximo
				Calendar nowWeek = SFServerUtil.stringToCalendar(getSFParams().getDateFormat(), SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
				datePayout = FlexUtil.calendarToString(getSFParams(), nowWeek);
				nowWeek.add(Calendar.DAY_OF_WEEK, + 1);
				int nowDayOfWeek = nowWeek.get(Calendar.DAY_OF_WEEK);

				datePayout = FlexUtil.calendarToString(getSFParams(), nowWeek);

				// Revisar si el dia siguiente es dia cobrable, sino traer el mas proximo
				datePayout = pmCreditType.getLastPaymentDay(datePayout, nowDayOfWeek, bmoCreditType, false);
			} else if (bmoCreditType.getType().equals(BmoCreditType.TYPE_WEEKLY)) {
				daysPayout = 7;
				
				// CALCULAR(*) EL primer dia de pago debe ser el lunes proximo
				Calendar nowWeek = SFServerUtil.stringToCalendar(getSFParams().getDateFormat(), SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
				int nowDay = nowWeek.get(Calendar.DAY_OF_WEEK);

				datePayout = FlexUtil.calendarToString(getSFParams(), nowWeek);
				// Sunday
				if (nowDay == 1) nowDay = 1;
				// Monday
				else if (nowDay == 2) nowDay = 7;
				// Thusday
				else if (nowDay == 3) nowDay = 6;
				// Weendays
				else if (nowDay == 4) nowDay = 5;
				// Thurday
				else if (nowDay == 5) nowDay = 4;
				// Friday
				else if (nowDay == 6) nowDay = 3;
				// Saturday
				else if (nowDay == 7) nowDay = 2;

				nowWeek.add(Calendar.DAY_OF_WEEK, +nowDay);

				// Calcular la primera fecha de pago
				datePayout = FlexUtil.calendarToString(getSFParams(), nowWeek);
				
			} else if (bmoCreditType.getType().equals(BmoCreditType.TYPE_TWOWEEKS)) {
				daysPayout = 15;
			} else if (bmoCreditType.getType().equals(BmoCreditType.TYPE_MONTHLY)) {
				daysPayout = 30;
			} else {
				bmUpdateResult.addMsg("El tipo de crédito no cuenta con un tipo definido");
			}

			printDevLog("date " + datePayout);

			// Dividir el total en CxC
			payout = bmoOrder.getTotal().toDouble() / deadLine;

			printDevLog(this.getClass().getName() + " - ensureOrderCredit(): Se van a generar " + deadLine + " CxC del Credito.");
		} if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)) {
			// Obtener el contrato de arrendamiento
			bmoPropertyRental = (BmoPropertyRental)pmPropertyRental.getBy(bmoOrder.getOriginRenewOrderId().toInteger(), bmoPropertyRental.getOrderId().getName());

			// plazo
			// Calcular meses entre fechas
			//				String rentalScheduleDate = bmoPropertyRental.getRentalScheduleDate().toString();
			//				rentalIncrease = bmoPropertyRental.getRentIncrease() .toString();

			String rentalScheduleDate = bmoOrder.getLockStart().toString().substring(0, 10);
			rentalIncrease = bmoOrder.getLockEnd().toString().substring(0, 10);

			Calendar dateStart = Calendar.getInstance();
			Calendar dateEnd= Calendar.getInstance();

			dateStart.setTime(SFServerUtil.stringToDate(getSFParams().getDateFormat(), rentalScheduleDate));
			dateEnd.setTime(SFServerUtil.stringToDate(getSFParams().getDateFormat(), rentalIncrease));

			int diffYear = dateEnd.get(Calendar.YEAR) - dateStart.get(Calendar.YEAR);
			int diffMonth = diffYear * 12 + dateEnd.get(Calendar.MONTH) - dateStart.get(Calendar.MONTH);
			// Descontar las cxc creadas manualmente
			countRaccount = orderHasRaccount(pmConn, bmoOrder, bmUpdateResult);
			deadLine = diffMonth - countRaccount;

			// Monto para cada cxc a crear
			// ej: (Total de pedido - sum de cxc) / meses
			//payout = bmoPropertyRental.getCurrentIncome().toDouble();
			payout = (bmoOrder.getTotal().toDouble() - bmoOrder.getTotalRaccounts().toDouble()) / deadLine;

			// Fecha programacion renta
			datePayout = rentalScheduleDate;

			// Si existen cxc manualmente, recorrer las fecha de programacion de acuerdo al conteo de estas
			if (countRaccount > 0)
				datePayout = SFServerUtil.addMonths(getSFParams().getDateFormat(), datePayout, countRaccount);
		}

		// Buscar si hay usuario en Usuarios de flujo del grupo que esta en conf(Cobranza), y asignarlo a las cxc.
		BmoUser bmoUser = new BmoUser();
		BmoProfile bmoProfile = new BmoProfile();
		PmProfile pmProfile = new PmProfile(getSFParams());
		// Traer grupo  de cobranza desde conf.
		int collectProfileId = 0;

		// MultiEmpresa: g100
		if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getMultiCompany().toBoolean() ) {
			// Buscar perfil de cobranza por multiempresa
			PmFlexConfig pmFlexConfig = new PmFlexConfig(getSFParams());
			collectProfileId = pmFlexConfig.getCollectProfileId(pmConn, bmoOrder.getCompanyId().toInteger());
		} else {
			// Buscar perfil de cobranza asignado en conf. flex
			collectProfileId = ((BmoFlexConfig) getSFParams().getBmoAppConfig()).getCollectProfileId().toInteger();
		}
		//			collectGroupId = ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCollectProfileId().toInteger();

		// Regresa usuario que esta en grupo de cobranza
		if (collectProfileId > 0) {
			bmoProfile = (BmoProfile)pmProfile.get(pmConn, collectProfileId);
			PmWFlowUser pmWFlowUser = new PmWFlowUser(getSFParams());
			bmoUser = pmWFlowUser.getUserByGroup(pmConn, bmoOrder.getWFlowId().toInteger(), bmoProfile, bmUpdateResult);
			printDevLog("usuario_cobranza: "+bmoUser.getCode());
		}

		int limitRacc = 12;
		// Genera una CxC por cada pago del plazo
		int x = 0;
		while ( x < deadLine ) {	//	for (int x = 0; x < deadLine; x++) {
//			boolean saveIsPaymentDay = false;
			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {
				if (bmoCreditType.getType().equals(BmoCreditType.TYPE_DAILY)) {
					if (x > 0) {
						// Agregar dias a la ultima fecha de la cxc creada, NO ENTRA LA PRIMERA VEZ, 
						// ya que la fecha ya esta calculada(*) mas arriba, esto solo repite los DIAS a agregar y valida dia cobrable
						datePayout = SFServerUtil.addDays(getSFParams().getDateFormat(), datePayout, daysPayout);
						// Revisar si el dia siguiente es un dia cobrable
						Calendar nextDate = SFServerUtil.stringToCalendar(getSFParams().getDateFormat(), datePayout);
						int nowDayOfWeek = nextDate.get(Calendar.DAY_OF_WEEK);
						
						// Revisar si el dia siguiente es dia cobrable, sino traer el mas proximo
						datePayout = pmCreditType.getLastPaymentDay(datePayout, nowDayOfWeek, bmoCreditType, false);
					}
				} else if (bmoCreditType.getType().equals(BmoCreditType.TYPE_WEEKLY)) {
					// Agregar dias a la ultima fecha de la cxc creada, NO ENTRA LA PRIMERA VEZ, 
					// ya que la fecha ya esta calculada(*) mas arriba, esto solo repite los lunes a agregar
					if (x > 0) {
						datePayout = SFServerUtil.addDays(getSFParams().getDateFormat(), datePayout, daysPayout);
					}
				}
				
			} else if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)) {
				// Solo se pueden crear 12 cxc
				if (x == limitRacc) break;
			}

			BmoRaccount bmoNewRaccount = new BmoRaccount();
			bmoNewRaccount.getSerie().setValue("Pagare");
			bmoNewRaccount.getFolio().setValue((x + 1));
			bmoNewRaccount.getInvoiceno().setValue("Pagare " + (x + 1));
			bmoNewRaccount.getReceiveDate().setValue(datePayout);
			bmoNewRaccount.getDueDate().setValue(datePayout);
			bmoNewRaccount.getDueDateStart().setValue(datePayout);
			bmoNewRaccount.getDescription().setValue("Creado Automaticamente.");
			bmoNewRaccount.getReference().setValue("Creado Autom.");
			bmoNewRaccount.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(payout));
			bmoNewRaccount.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(payout));
			bmoNewRaccount.getBalance().setValue(SFServerUtil.roundCurrencyDecimals(payout));
			bmoNewRaccount.getRaccountTypeId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getOrderRaccountTypeId().toInteger());
			bmoNewRaccount.getOrderId().setValue(bmoOrder.getId());
			bmoNewRaccount.getUserId().setValue(bmoOrder.getUserId().toInteger());
			bmoNewRaccount.getCompanyId().setValue(bmoOrder.getCompanyId().toInteger());
			bmoNewRaccount.getCustomerId().setValue(bmoOrder.getCustomerId().toInteger());
			bmoNewRaccount.getStatus().setValue(BmoRaccount.STATUS_AUTHORIZED);
			bmoNewRaccount.getAutoCreate().setValue(0);
			bmoNewRaccount.getFailure().setValue(0);
			bmoNewRaccount.getLinked().setValue(0);
			bmoNewRaccount.getCurrencyId().setValue(bmoOrder.getCurrencyId().toInteger());
			bmoNewRaccount.getCurrencyParity().setValue(bmoOrder.getCurrencyParity().toDouble());
			if (bmoUser.getId() > 0)
				bmoNewRaccount.getCollectorUserId().setValue(bmoUser.getId());

			super.saveSimple(pmConn, bmoNewRaccount, bmUpdateResult);
			
			PmCompanyNomenclature pmCompanyNomenclature = new PmCompanyNomenclature(getSFParams());
			String code = "";
			// Generar clave personalizada si la hay, si no retorna la de por defecto
			code = pmCompanyNomenclature.getCodeCustom(pmConn,
					bmoNewRaccount.getCompanyId().toInteger(),
					bmoNewRaccount.getProgramCode().toString(),
					bmUpdateResult.getId(),
					BmoRaccount.CODE_PREFIX
					);
			bmoNewRaccount.getCode().setValue(code);

			printDevLog(this.getClass().getName() + " - ensureOrderCredit(): Se genero la CxC " + bmoNewRaccount.getCode().toString());

			// Crear el item con el monto de la liquidacion
			PmRaccountItem pmRaccItemNew = new PmRaccountItem(getSFParams());
			BmoRaccountItem bmoRaccItemNew = new BmoRaccountItem();
			bmoRaccItemNew.getName().setValue(bmoNewRaccount.getInvoiceno().toString());
			bmoRaccItemNew.getQuantity().setValue("1");
			bmoRaccItemNew.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(bmoNewRaccount.getAmount().toDouble()));
			bmoRaccItemNew.getPrice().setValue(SFServerUtil.roundCurrencyDecimals(bmoNewRaccount.getAmount().toDouble()));
			bmoRaccItemNew.getRaccountId().setValue(bmUpdateResult.getId());

			pmRaccItemNew.saveSimple(pmConn, bmoRaccItemNew, bmUpdateResult);

			// De tipo Arrendamiento
			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)) {
				// Agregar 1 mes a la fecha de programacion de pago para las cxc
				datePayout = SFServerUtil.addMonths(getSFParams().getDateFormat(), datePayout, 1);

				// Marcar como externa las cxc si es cliente es Arrendatario
				if (bmoCustomer.getCustomercategory().equals(BmoCustomer.CATEGORY_LESSOR))
					bmoNewRaccount.getLinked().setValue(1);
				else
					bmoNewRaccount.getLinked().setValue(0);
			}

			//Calcular fecha de registro (el termino de pago del cliente) y asignar		
			if (bmoCustomer.getReqPayTypeId().toInteger() > 0) {
				PmReqPayType pmReqPayType = new PmReqPayType(getSFParams());
				BmoReqPayType bmoReqPayType = (BmoReqPayType)pmReqPayType.get(pmConn, bmoCustomer.getReqPayTypeId().toInteger());
				bmoNewRaccount.getReqPayTypeId().setValue(bmoCustomer.getReqPayTypeId().toInteger());

				// Sumar a la fecha de registros los dias de pago
				Calendar calNow = SFServerUtil.stringToCalendar(getSFParams().getDateFormat(), bmoNewRaccount.getReceiveDate().toString());				
				calNow.add(Calendar.DAY_OF_MONTH, bmoReqPayType.getDays().toInteger());

				// Asignar
				if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {
					// No hace nada
				} else if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)) {
					// Asignar dias a la fecha de recordatorio
					bmoNewRaccount.getRemindDate().setValue(SFServerUtil.dateToString(calNow.getTime() , getSFParams().getDateFormat()));

					// Quitar un dia a la fecha de programacion
					Calendar calDueDate = Calendar.getInstance();
					calDueDate.setTime(SFServerUtil.stringToDate(getSFParams().getDateFormat(), bmoNewRaccount.getReceiveDate().toString()));
					calDueDate.add(Calendar.DAY_OF_MONTH, (bmoReqPayType.getDays().toInteger()) );
					bmoNewRaccount.getDueDate().setValue(SFServerUtil.dateToString(calDueDate.getTime(), getSFParams().getDateFormat()));

				} else {
					//Asignar la fecha de registro				
					bmoNewRaccount.getDueDate().setValue(FlexUtil.calendarToString(getSFParams(), calNow));
				}
			}

			// Asignar metodo de pago que esta asginado en el cliente
			int paymentTypeId = getCustomerPaymentTypeByOrder(pmConn, bmoOrder, bmUpdateResult);
			if (paymentTypeId > 0)
				bmoNewRaccount.getPaymentTypeId().setValue(paymentTypeId);

			printDevLog(this.getClass().getName() + "ensureOrderCredit(): Se asigno el Item a la CxC " + bmoNewRaccount.getCode().toString());

			if (!bmUpdateResult.hasErrors()) {
				// Actualizar id de claves del programa por empresa
				pmCompanyNomenclature.updateConsecutiveByCompany(pmConn, bmoNewRaccount.getCompanyId().toInteger(), 
						bmoNewRaccount.getProgramCode().toString());
				
				super.save(pmConn, bmoNewRaccount, bmUpdateResult);
			}
			x++;
		}

		// Generar bitacora de cxc
		if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)) {
			//BmoWFlowLog bmoWFlowLog = new BmoWFlowLog();
			PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());
			pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoOrder.getWFlowId().toInteger(), BmoWFlowLog.TYPE_SYS, "Creación de " + deadLine + " CxC.");
		}
		//		} else {
		//			bmUpdateResult.addError(bmoOrder.getStatus().getName(), "<b>Existen CxC ligadas.</b>");
		//		}

		return bmUpdateResult;
	}

	// CxC multiples
	private BmUpdateResult multipleRaccount(String value, BmUpdateResult bmUpdateResult) throws SFException {
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();

		try {
			pmConn.disableAutoCommit();

			// Inicializa variables
			PmRaccount pmRaccount = new PmRaccount(getSFParams());
			BmoRaccount bmoRaccount = new BmoRaccount();
			int orderId = 0;
			String startDate = "", datePayout;
			double originAmount = 0, originTotal = 0, partialAmount = 0, partialTotal = 0, remainingAmount = 0, remainingTotal = 0;
			int paymentNumber = 0;
			int raccountId = 0;

			// Realiza parse de variables
			StringTokenizer tabs = new StringTokenizer(value, "|");
			while (tabs.hasMoreTokens()) {
				orderId = Integer.parseInt(tabs.nextToken());
				startDate = tabs.nextToken();
				originAmount = Double.parseDouble(tabs.nextToken());
				paymentNumber = Integer.parseInt(tabs.nextToken());
				raccountId = Integer.parseInt(tabs.nextToken());
			}

			// Obtener el Pedido
			PmOrder pmOrder = new PmOrder(getSFParams());
			BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn, orderId);

			// Calcular si aplica iva
			boolean taxApplies = bmoOrder.getTaxApplies().toBoolean();
			if (raccountId > 0) {
				bmoRaccount = (BmoRaccount) pmRaccount.get(pmConn, raccountId);
				taxApplies = bmoRaccount.getTaxApplies().toBoolean();
				if (!getSFParams().isProduction()) System.out.println(this.getClass().getName() + "-multipleRaccount() MSG: Se estan creando CxC a partir de CxC: " + bmoRaccount.getCode().toString());
			} 

			// Validaciones
			if (!(originAmount > 0)) bmUpdateResult.addMsg("El monto a dividir debe ser mayor a $0.00");
			if (!(paymentNumber > 1)) bmUpdateResult.addMsg("La cantidad de pagos debe ser mayor a 1");

			// Obtener el Subtotal
			originTotal = originAmount;
			if (taxApplies) {
				originAmount = originTotal / ((((BmoFlexConfig) getSFParams().getBmoAppConfig()).getTax().toDouble() / 100) + 1);
			}

			// Dividir el monto entre el plazo
			partialAmount = SFServerUtil.stringToDouble(SFServerUtil.roundCurrencyDecimals(originAmount / paymentNumber));
			partialTotal = SFServerUtil.stringToDouble(SFServerUtil.roundCurrencyDecimals(originTotal / paymentNumber));
			remainingAmount = originAmount;
			remainingTotal = originTotal;
			datePayout = startDate;

			for (int x = 1; x <= paymentNumber; x++) {
				BmoRaccount bmoNewRaccount = new BmoRaccount();

				if (bmoRaccount.getOrderGroupId().toInteger() > 0)
					bmoNewRaccount.getInvoiceno().setValue(bmoRaccount.getInvoiceno().toString() + " " + x);
				else
					bmoNewRaccount.getInvoiceno().setValue("Documento " + x);

				bmoNewRaccount.getReceiveDate().setValue(datePayout);
				bmoNewRaccount.getDueDate().setValue(datePayout);
				bmoNewRaccount.getDescription().setValue("Creado Automaticamente.");
				bmoNewRaccount.getReference().setValue("Creado Autom.");
				bmoNewRaccount.getRaccountTypeId().setValue(bmoRaccount.getRaccountTypeId().toInteger());
				bmoNewRaccount.getOrderId().setValue(bmoOrder.getId());
				bmoNewRaccount.getCompanyId().setValue(bmoOrder.getCompanyId().toInteger());
				bmoNewRaccount.getCustomerId().setValue(bmoOrder.getCustomerId().toInteger());
				bmoNewRaccount.getPaymentTypeId().setValue(bmoRaccount.getPaymentTypeId().toInteger());
				bmoNewRaccount.getCollectorUserId().setValue(bmoRaccount.getCollectorUserId().toInteger());
				// Si esta habilitado el control presupuestal pasar valores
				if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
					bmoNewRaccount.getBudgetItemId().setValue(bmoRaccount.getBudgetItemId().toInteger());
					bmoNewRaccount.getAreaId().setValue(bmoRaccount.getAreaId().toInteger());
				}

				// Calcular termino de pago del cliente si la CC a dividir tiene termino de pago
				if (bmoRaccount.getReqPayTypeId().toInteger() > 0) {
					PmReqPayType pmReqPayType = new PmReqPayType(getSFParams());
					BmoReqPayType bmoReqPayType = (BmoReqPayType)pmReqPayType.get(pmConn, bmoRaccount.getReqPayTypeId().toInteger());

					// Sumar a la fecha de registro los dias de pago
					Calendar calNow = SFServerUtil.stringToCalendar(getSFParams().getDateFormat(), bmoNewRaccount.getReceiveDate().toString());				
					calNow.add(Calendar.DAY_OF_YEAR, bmoReqPayType.getDays().toInteger());

					// Asignar la fecha de programacion, vencimiento y termino de pago del cliente
					bmoNewRaccount.getDueDate().setValue(FlexUtil.calendarToString(getSFParams(), calNow));
					bmoNewRaccount.getDueDateStart().setValue(bmoNewRaccount.getDueDate().toString());
					bmoNewRaccount.getReqPayTypeId().setValue(bmoRaccount.getReqPayTypeId().toInteger());
				}	

				// Toma valores de la CxC origen si esta seleccionada
				if (raccountId > 0) {
					bmoNewRaccount.getCurrencyId().setValue(bmoRaccount.getCurrencyId().toInteger());
					bmoNewRaccount.getCurrencyParity().setValue(bmoRaccount.getCurrencyParity().toDouble());
					if (!getSFParams().isProduction()) System.out.println(this.getClass().getName() + "-multipleRaccount() MSG: Esta asignando moneda de la CxC origen " + bmoRaccount.getCode().toString());
				} 
				// Toma valores del pedido
				else {
					bmoNewRaccount.getCurrencyId().setValue(bmoOrder.getCurrencyId().toInteger());
					bmoNewRaccount.getCurrencyParity().setValue(bmoOrder.getCurrencyParity().toDouble());
					if (!getSFParams().isProduction()) System.out.println(this.getClass().getName() + "-multipleRaccount() MSG: Esta asignando moneda del Pedido origen " + bmoOrder.getCode().toString());
				}

				bmoNewRaccount.getStatus().setValue(BmoRaccount.STATUS_REVISION);
				bmoNewRaccount.getAutoCreate().setValue(0);
				bmoNewRaccount.getFailure().setValue(0);

				// Establece montos
				if (!getSFParams().isProduction()) System.out.println(this.getClass().getName() + "-multipleRaccount() MSG: X = " + x + ", Monto Pendiente: " + remainingAmount + ", Monto Parcial: " + partialAmount);

				if (x == paymentNumber) {
					partialAmount = remainingAmount;
					partialTotal = remainingTotal;
				}

				if (taxApplies) {
					// Como aplica IVA, para ajustar el monto total, se parte del total
					bmoNewRaccount.getTotal().setValue(partialTotal);					
					bmoNewRaccount.getTaxApplies().setValue(true);	
					double tempAmount = SFServerUtil.stringToDouble(SFServerUtil.roundCurrencyDecimals(partialTotal / (1 + (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getTax().toDouble() / 100))));
					bmoNewRaccount.getTax().setValue(SFServerUtil.roundCurrencyDecimals(
							tempAmount * ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getTax().toDouble() / 100))		
							));
					bmoNewRaccount.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(tempAmount));
					bmoNewRaccount.getBalance().setValue(partialTotal);
				} else {
					bmoNewRaccount.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(partialAmount));
					bmoNewRaccount.getTax().setValue(0);
					bmoNewRaccount.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(partialAmount));
					bmoNewRaccount.getBalance().setValue(SFServerUtil.roundCurrencyDecimals(partialAmount));
				}
				remainingAmount -= partialAmount;
				remainingTotal -= partialTotal;


				// Si viene de un ordergroup
				if (bmoRaccount.getOrderGroupId().toInteger() > 0)
					bmoNewRaccount.getOrderGroupId().setValue(bmoRaccount.getOrderGroupId().toInteger());

				super.saveSimple(pmConn, bmoNewRaccount, bmUpdateResult);

				PmCompanyNomenclature pmCompanyNomenclature = new PmCompanyNomenclature(getSFParams());
				String code = "";
				// Generar clave personalizada si la hay, si no retorna la de por defecto
				code = pmCompanyNomenclature.getCodeCustom(pmConn,
						bmoNewRaccount.getCompanyId().toInteger(),
						bmoNewRaccount.getProgramCode().toString(),
						bmUpdateResult.getId(),
						BmoRaccount.CODE_PREFIX
						);
				bmoNewRaccount.getCode().setValue(code);

				super.save(pmConn, bmoNewRaccount, bmUpdateResult);

				// Crear el item con el monto de la liquidacion
				PmRaccountItem pmRaccItemNew = new PmRaccountItem(getSFParams());
				BmoRaccountItem bmoRaccItemNew = new BmoRaccountItem();
				bmoRaccItemNew.getName().setValue(bmoNewRaccount.getInvoiceno().toString());
				bmoRaccItemNew.getQuantity().setValue("1");
				bmoRaccItemNew.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(partialAmount));
				bmoRaccItemNew.getPrice().setValue(SFServerUtil.roundCurrencyDecimals(partialAmount));
				bmoRaccItemNew.getRaccountId().setValue(bmoNewRaccount.getId());
				if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
					bmoRaccItemNew.getBudgetItemId().setValue(bmoRaccount.getBudgetItemId().toInteger());
					bmoRaccItemNew.getAreaId().setValue(bmoRaccount.getAreaId().toInteger());
				}

				pmRaccItemNew.saveSimple(pmConn, bmoRaccItemNew, bmUpdateResult);

				datePayout = SFServerUtil.addMonths(getSFParams().getDateFormat(), datePayout, 1);

				if (!bmUpdateResult.hasErrors()) {
					// Actualizar id de claves del programa por empresa
					pmCompanyNomenclature.updateConsecutiveByCompany(pmConn, bmoNewRaccount.getCompanyId().toInteger(), 
							bmoNewRaccount.getProgramCode().toString());

					//					super.save(pmConn, bmoNewRaccount, bmUpdateResult);
				}
			}

			// Eliminar la CxC ligada al grupo
			if (raccountId > 0) {
				// Eliminar los conceptos de banco
				pmRaccount.deleteItems(pmConn, raccountId, bmUpdateResult);
				super.delete(pmConn, bmoRaccount, bmUpdateResult);
			} else {
				pmRaccount.ensureOrderBalance(pmConn, bmoOrder, bmUpdateResult);
			}

			// Actualiza montos de pago del pedido
			pmOrder.updatePayments(pmConn, bmoOrder, bmUpdateResult);

			if (!bmUpdateResult.hasErrors())
				pmConn.commit();

		} catch (SFException e) {
			bmUpdateResult.addMsg("Error Multiple CxC " + e.toString());
		} finally {
			pmConn.close();
		}

		return bmUpdateResult;
	}

	// Realizar multiples pagos (creditos)
	private BmUpdateResult multiplePaymentApp(String values, BmUpdateResult bmUpdateResult) throws SFException {
		// CxC
		PmRaccount pmRaccountApp = new PmRaccount(getSFParams());
		BmoRaccount bmoRaccountApp = new BmoRaccount();

		// Pedido
		//		PmOrder pmOrder = new PmOrder(getSFParams());
		//		BmoOrder bmoOrder = new BmoOrder();

		int raccountId = 0;
		String actionValues = "";

		StringTokenizer tabs = new StringTokenizer(values, "|");
		while (tabs.hasMoreTokens()) {
			raccountId = Integer.parseInt(tabs.nextToken());
			bmoRaccountApp = (BmoRaccount) pmRaccountApp.get(raccountId);
			// Obtener pedido
			//			if (bmoRaccountApp.getOrderId().toInteger() > 0)
			//					bmoOrder = (BmoOrder) pmOrder.get(bmoRaccountApp.getOrderId().toInteger());

			// Si es te tipo arrendamiento pasar el total
			//			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE))
			//				actionValues = bmoRaccountApp.getId() + "|" + bmoRaccountApp.getDueDate().toString() + "|" + bmoRaccountApp.getTotal().toDouble(); 
			//			else 
			actionValues = bmoRaccountApp.getId() + "|" + bmoRaccountApp.getDueDate().toString() + "|" + bmoRaccountApp.getBalance().toDouble(); 

			paymentApp(actionValues, bmUpdateResult);
		}

		return bmUpdateResult;
	}


	// Realizar pagos desde la aplicación
	private BmUpdateResult paymentApp(String value, BmUpdateResult bmUpdateResult) throws SFException {
		// Valores
		int raccountId = 0;
		double amount = 0;
		String dueDate = "";

		StringTokenizer tabs = new StringTokenizer(value, "|");
		while (tabs.hasMoreTokens()) {
			raccountId = Integer.parseInt(tabs.nextToken());
			dueDate = tabs.nextToken();
			amount = Double.parseDouble(tabs.nextToken());
		}

		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();

		PmConn pmConnRaccNC = new PmConn(getSFParams());
		pmConnRaccNC.open();

		try {
			pmConn.disableAutoCommit();

			// Obtener la CxC
			PmRaccount pmRaccountApp = new PmRaccount(getSFParams());
			BmoRaccount bmoRaccountApp = (BmoRaccount) pmRaccountApp.get(pmConn, raccountId);

			if (bmoRaccountApp.getOrderId().toInteger() > 0) {
				PmOrder pmOrder = new PmOrder(getSFParams());
				BmoOrder bmoOrder = (BmoOrder) pmOrder.get(pmConn, bmoRaccountApp.getOrderId().toInteger());

				if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {

					if (!bmoRaccountApp.getStatus().equals(BmoRaccount.STATUS_AUTHORIZED)) {
						bmUpdateResult.addMsg("La CxC(" + bmoRaccountApp.getCode() + ") debe estar autorizada.");
					} else if (!(amount > 0)) {
						bmUpdateResult.addMsg("El monto debe ser mayor a $ 0.00");
					} else if (amount > bmoRaccountApp.getBalance().toDouble()) {
						bmUpdateResult.addMsg("El monto debe no debe ser mayor a " + SFServerUtil.formatCurrency(bmoRaccountApp.getBalance().toDouble()));
					}

					if (!bmUpdateResult.hasErrors()) {
						// Obtener el credito
						PmCredit pmCredit = new PmCredit(getSFParams());
						BmoCredit bmoCredit = new BmoCredit();
						bmoCredit = (BmoCredit) pmCredit.getBy(pmConn, bmoRaccountApp.getOrderId().toInteger(),
								bmoCredit.getOrderId().getName());

						// Obtener el ejecutivo del vendedor						
						int userId = pmOrder.getUserByGroup(pmConn, bmoOrder, bmUpdateResult);
						// Obtener la cuenta de banco del ejecutivo
						BmoBankAccount bmoBankAccount = new BmoBankAccount();
						PmBankAccount pmBankAccount = new PmBankAccount(getSFParams());
						bmoBankAccount = (BmoBankAccount) pmBankAccount.getBy(pmConn, userId, bmoBankAccount.getResponsibleId().getName());

						if (!bmUpdateResult.hasErrors()) {

							// Obtener el tipo pago en bancos
							PmBankMovType pmBankMovType = new PmBankMovType(getSFParams());
							BmoBankMovType bmoBankMovType = new BmoBankMovType();
							bmoBankMovType = (BmoBankMovType) pmBankMovType.getBy(pmConn, "" + BmoBankMovType.CATEGORY_CXC,	bmoBankMovType.getCategory().getName());

							// Crear el Mov de Banco de Tipo Pago a Proveedor
							PmBankMovement pmBkmvNew = new PmBankMovement(getSFParams());
							BmoBankMovement bmoBkmvNew = new BmoBankMovement();

							bmoBkmvNew.getBankAccountId().setValue(bmoBankAccount.getId());
							bmoBkmvNew.getCustomerId().setValue(bmoRaccountApp.getCustomerId().toInteger());
							bmoBkmvNew.getBankReference().setValue("Pago Recibido ");
							bmoBkmvNew.getDescription().setValue("Pago Recibido " + getSFParams().getLoginInfo().getBmoUser().getCode() + 
									" " + SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));
							bmoBkmvNew.getBankMovTypeId().setValue(bmoBankMovType.getId());
							bmoBkmvNew.getStatus().setValue("" + BmoBankMovement.STATUS_AUTHORIZED);
							bmoBkmvNew.getInputDate().setValue(dueDate);
							bmoBkmvNew.getDueDate().setValue(dueDate);

							pmBkmvNew.save(pmConn, bmoBkmvNew, bmUpdateResult);

							// Crear el concepto de Banco
							PmBankMovConcept pmBankMovConcept = new PmBankMovConcept(getSFParams());
							BmoBankMovConcept bmoBankMovConcept = new BmoBankMovConcept();
							bmoBankMovConcept.getBankMovementId().setValue(bmoBkmvNew.getId());
							bmoBankMovConcept.getRaccountId().setValue(raccountId);
							bmoBankMovConcept.getAmount().setValue(amount);
							pmBankMovConcept.save(pmConn, bmoBankMovConcept, bmUpdateResult);
						}

						if(!bmUpdateResult.hasErrors())
							pmConn.commit();
					}

				} else if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)) {
					// Validaciones
					if (!getSFParams().hasSpecialAccess(BmoRaccount.ACCESS_PAYMENTRACC))  
						bmUpdateResult.addMsg("<b>No tiene Permiso de aplicar pagos a la(s) CxC.</b>");
					if (!bmoRaccountApp.getStatus().equals(BmoRaccount.STATUS_AUTHORIZED)) 
						bmUpdateResult.addMsg("<b>La CxC(" + bmoRaccountApp.getCode() + ") debe estar autorizada.</b>");
					else if (bmoRaccountApp.getPaymentStatus().equals(BmoRaccount.PAYMENTSTATUS_TOTAL))
						bmUpdateResult.addMsg("<b>La CxC(" + bmoRaccountApp.getCode() + ") ya está Pagada totalmente.</b>");
					else if (!(amount > 0)) 
						bmUpdateResult.addMsg("<b>El monto debe ser mayor a $ 0.00.</b>");
					else if (amount > bmoRaccountApp.getBalance().toDouble()) 
						bmUpdateResult.addMsg("<b>El monto no debe ser mayor a " + SFServerUtil.formatCurrency(bmoRaccountApp.getBalance().toDouble()) + "</b>");

					// Crear Nota de credito de CxC 
					if (!bmUpdateResult.hasErrors()) {	
						// Obtener el tipo de Nota de credito
						int raccountTypeIdNC = 0;
						PmRaccountType pmRaccountType = new PmRaccountType(getSFParams());
						BmoRaccountType bmoRaccountType = new BmoRaccountType();
						String sql = " SELECT ract_raccounttypeid FROM " + formatKind("raccounttypes") +
								" WHERE ract_type = '" + BmoRaccountType.TYPE_DEPOSIT + "' " +
								" AND ract_category = '" + BmoRaccountType.CATEGORY_CREDITNOTE + "' ";

						pmConnRaccNC.doFetch(sql);
						if (pmConnRaccNC.next()) raccountTypeIdNC = pmConnRaccNC.getInt("ract_raccounttypeid");
						if (!(raccountTypeIdNC > 0))
							bmUpdateResult.addMsg("<b>No existe un Tipo de CxC de Nota de Crédito en configuración de CxC.</b>");
						else 
							bmoRaccountType = (BmoRaccountType)pmRaccountType.get(raccountTypeIdNC);

						BmoRaccount bmoNewRaccount = new BmoRaccount();
						// Datos generales
						bmoNewRaccount.getRaccountTypeId().setValue(bmoRaccountType.getId());
						bmoNewRaccount.getCustomerId().setValue(bmoOrder.getCustomerId().toInteger());
						bmoNewRaccount.getCompanyId().setValue(bmoOrder.getCompanyId().toInteger());
						bmoNewRaccount.getOrderId().setValue(bmoOrder.getId());
						bmoNewRaccount.getUserId().setValue(bmoOrder.getUserId().toInteger());
						// detalle y fechas
						bmoNewRaccount.getReference().setValue("Pago -> " + bmoRaccountApp.getCode().toString() + " " + bmoRaccountApp.getInvoiceno().toString());
						bmoNewRaccount.getSerie().setValue("Pago");
						bmoNewRaccount.getFolio().setValue("");
						bmoNewRaccount.getInvoiceno().setValue("Pago");
						bmoNewRaccount.getDescription().setValue("Pago con Nota crédito");
						bmoNewRaccount.getReceiveDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
						bmoNewRaccount.getDueDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
						bmoNewRaccount.getDueDateStart().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));		
						bmoNewRaccount.getFailure().setValue(0);
						bmoNewRaccount.getLinked().setValue(0);
						bmoNewRaccount.getAutoCreate().setValue(0);
						if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
							bmoNewRaccount.getBudgetItemId().setValue(bmoRaccountApp.getBudgetItemId().toInteger());
							bmoNewRaccount.getAreaId().setValue(bmoRaccountApp.getAreaId().toInteger());
						}
						// Montos
						bmoNewRaccount.getCurrencyParity().setValue(bmoRaccountApp.getCurrencyParity().toDouble());
						bmoNewRaccount.getCurrencyId().setValue(bmoRaccountApp.getCurrencyId().toInteger());
						bmoNewRaccount.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(bmoRaccountApp.getTotal().toDouble()));
						bmoNewRaccount.getTaxApplies().setValue(false);
						bmoNewRaccount.getTax().setValue(0);
						bmoNewRaccount.getTotal().setValue(amount);

						// Si el monto es mayor a 0, crearla
						if (bmoNewRaccount.getTotal().toDouble() > 0) {
							// Asigar la partida de ingresos
							if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {

								// Validar que exista partida en el pedido
								if (!(bmoOrder.getDefaultBudgetItemId().toInteger() > 0))
									bmUpdateResult.addError(bmoOrder.getDefaultBudgetItemId().getName(), "El Pedido No tiene una Partida Presp.");

								// Colocar partida y departamento del pedido
								bmoNewRaccount.getBudgetItemId().setValue(bmoOrder.getDefaultBudgetItemId().toInteger());
								bmoNewRaccount.getAreaId().setValue(bmoOrder.getDefaultAreaId().toInteger());

								PmBudgetItem pmBudgetItem = new PmBudgetItem(getSFParams());
								BmoBudgetItem bmoBudgetItem = (BmoBudgetItem) pmBudgetItem.get(pmConn, bmoNewRaccount.getBudgetItemId().toInteger());

								pmBudgetItem.updateBalance(pmConn, bmoBudgetItem, bmUpdateResult);
							}

							super.save(pmConn, bmoNewRaccount, bmUpdateResult);
							// Obtener metodo/termino del cliente
							if (bmoNewRaccount.getCustomerId().toInteger() > 0) {
								PmCustomer pmCustomer = new PmCustomer(getSFParams());
								BmoCustomer bmoCustomer = (BmoCustomer)pmCustomer.get(pmConn, bmoNewRaccount.getCustomerId().toInteger());
								// Calcular termino de pago del cliente
								if (bmoCustomer.getReqPayTypeId().toInteger() > 0) {
									PmReqPayType pmReqPayType = new PmReqPayType(getSFParams());
									BmoReqPayType bmoReqPayType = (BmoReqPayType)pmReqPayType.get(pmConn, bmoCustomer.getReqPayTypeId().toInteger());

									// Sumar a la fecha de registro los dias de pago
									Calendar calNow = SFServerUtil.stringToCalendar(getSFParams().getDateFormat(), bmoNewRaccount.getReceiveDate().toString());				
									calNow.add(Calendar.DAY_OF_YEAR, bmoReqPayType.getDays().toInteger());

									// Asignar la fecha de programacion, vencimiento y termino de pago del cliente
									bmoNewRaccount.getDueDate().setValue(FlexUtil.calendarToString(getSFParams(), calNow));
									bmoNewRaccount.getDueDateStart().setValue(bmoNewRaccount.getDueDate().toString());
									bmoNewRaccount.getReqPayTypeId().setValue(bmoCustomer.getReqPayTypeId().toInteger());
								}	

								// Traer metodo de pago del cliente de acuerdo al cliente y moneda del pedido
								int paymentTypeId = getCustomerPaymentTypeByOrder(pmConn, bmoOrder, bmUpdateResult);

								if (paymentTypeId > 0)
									bmoNewRaccount.getPaymentTypeId().setValue(paymentTypeId);
							}
							super.save(pmConn, bmoNewRaccount, bmUpdateResult);

							PmCompanyNomenclature pmCompanyNomenclature = new PmCompanyNomenclature(getSFParams());
							String code = "";
							// Generar clave personalizada si la hay, si no retorna la de por defecto
							code = pmCompanyNomenclature.getCodeCustom(pmConn,
									bmoNewRaccount.getCompanyId().toInteger(),
									bmoNewRaccount.getProgramCode().toString(),
									bmUpdateResult.getId(),
									BmoRaccount.CODE_PREFIX
									);
							bmoNewRaccount.getCode().setValue(code);
							bmoNewRaccount.getStatus().setValue(BmoRaccount.STATUS_AUTHORIZED);

							// Crear el item con el monto de la liquidacion
							PmRaccountItem pmRaccItemNew = new PmRaccountItem(getSFParams());
							BmoRaccountItem bmoRaccItemNew = new BmoRaccountItem();
							bmoRaccItemNew.getName().setValue("Pago");
							bmoRaccItemNew.getQuantity().setValue("1");
							bmoRaccItemNew.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(amount));
							bmoRaccItemNew.getPrice().setValue(SFServerUtil.roundCurrencyDecimals(amount));
							bmoRaccItemNew.getRaccountId().setValue(bmoNewRaccount.getId());
							// Asigar la partida de ingresos
							if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
								// Colocar partida y departamento de la cxc origen
								bmoRaccItemNew.getBudgetItemId().setValue(bmoRaccountApp.getBudgetItemId().toInteger());
								bmoRaccItemNew.getAreaId().setValue(bmoRaccountApp.getAreaId().toInteger());
							}

							pmRaccItemNew.saveSimple(pmConn, bmoRaccItemNew, bmUpdateResult);				

							// Asignar el usuario del perfil cobranza desde conf. flex.
							asignCollectUser(pmConn, bmoNewRaccount, bmoOrder, bmUpdateResult);

							if (!bmUpdateResult.hasErrors()) {
								// Actualizar id de claves del programa por empresa
								pmCompanyNomenclature.updateConsecutiveByCompany(pmConn, bmoNewRaccount.getCompanyId().toInteger(), 
										bmoNewRaccount.getProgramCode().toString());

								super.save(pmConn, bmoNewRaccount, bmUpdateResult);
							}

							// Aplicar nota de credito
							PmRaccountAssignment pmRaccountAssignment = new PmRaccountAssignment(getSFParams());
							BmoRaccountAssignment bmoRaccountAssignment = new BmoRaccountAssignment();
							bmoRaccountAssignment.getRaccountId().setValue(bmoNewRaccount.getId());
							bmoRaccountAssignment.getForeignRaccountId().setValue(bmoRaccountApp.getId());
							bmoRaccountAssignment.getAmount().setValue(amount);
							pmRaccountAssignment.save(pmConn, bmoRaccountAssignment, bmUpdateResult);

							// Genera bitácora si viene de Pedido con datos actualizados
							bmoRaccountApp = (BmoRaccount)pmRaccountApp.get(pmConn, bmoRaccountApp.getId());
							String action = "Modificó";
							addWFlowLog(pmConn, bmoOrder, bmoRaccountApp, action, bmUpdateResult);
						}
					}

					updatePaymentStatusPropertyRental(pmConn, bmoOrder, bmUpdateResult);

					if(!bmUpdateResult.hasErrors())
						pmConn.commit();
				} else {
					bmUpdateResult.addMsg("<b>Los pagos NO aplican para este Tipo de Pedido.</b>");
				}
			} else {
				bmUpdateResult.addMsg("<b>La CxC(" + bmoRaccountApp.getCode() + ") debe estar ligada a un Pedido.</b>");
			}

		} catch (SFException e) {
			pmConn.rollback();
			bmUpdateResult.addMsg(this.getClass().getName() + " - paymentApp(): Error " + bmUpdateResult.errorsToString() + " " + e.toString());
		} finally {
			pmConn.close();
			pmConnRaccNC.close();
		}

		return bmUpdateResult;
	}

	// Crear las CxC de los grupos del pedido
	private BmUpdateResult createRaccOrderGroup(PmConn pmConn, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult)
			throws SFException {
		System.out.println("Ingresando a createRaccOrderGroup");

		PmConn pmConn2 = new PmConn(getSFParams());
		pmConn2.open();

		PmConn pmConn3 = new PmConn(getSFParams());
		pmConn3.open();

		String sql = "";

		try {

			//			PmOrderGroup pmOrderGroup = new PmOrderGroup(getSFParams());
			BmoOrderGroup bmoOrderGroup = new BmoOrderGroup();

			ArrayList<BmFilter> filterListD = new ArrayList<BmFilter>();
			BmFilter filterOrder = new BmFilter();
			BmFilter filterCxC = new BmFilter();

			filterOrder.setValueFilter(bmoOrderGroup.getKind(), bmoOrderGroup.getOrderId().getName(), bmoOrder.getId());
			filterListD.add(filterOrder);
			filterCxC.setValueFilter(bmoOrderGroup.getKind(), bmoOrderGroup.getCreateRaccount().getName(), "1");
			filterListD.add(filterCxC);

			Iterator<BmObject> listOrderGroup = new PmOrderGroup(getSFParams()).list(pmConn, filterListD).iterator();
			while (listOrderGroup.hasNext()) {
				bmoOrderGroup = (BmoOrderGroup) listOrderGroup.next();

				System.out.println("Ingresando a listOrderGroups");


				// Contar las CxC ligadas al grupo
				int items = 0;
				sql = " SELECT COUNT(*) AS items FROM raccounts "
						+ " LEFT JOIN raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) "
						+ " WHERE racc_ordergroupid = " + bmoOrderGroup.getId() + " AND ract_type = '"
						+ BmoRaccountType.TYPE_WITHDRAW + "'" + " AND racc_status = '" + BmoRaccount.STATUS_REVISION
						+ "'";
				pmConn.doFetch(sql);
				if (pmConn.next()) {
					items = pmConn.getInt("items");
				}

				double amountOdgp = bmoOrderGroup.getAmount().toDouble();
				//				double amountRaccs = 0;
				sql = " SELECT SUM(racc_amount) AS amount FROM raccounts "
						+ " LEFT JOIN raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) "
						+ " WHERE racc_ordergroupid = " + bmoOrderGroup.getId() + " AND ract_type = '"
						+ BmoRaccountType.TYPE_WITHDRAW + "'" + " AND racc_status = '" + BmoRaccount.STATUS_REVISION
						+ "'";
				pmConn.doFetch(sql);
				if (pmConn.next()) {
					//					amountRaccs = pmConn.getDouble("amount");
				}

				System.out.println("Items " + items);

				if (items != 0) {

					double balanceAuth = 0;
					sql = " SELECT SUM(racc_amount) AS totalRaccGroup FROM raccounts "
							+ " LEFT JOIN raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) "
							+ " WHERE racc_orderid = " + bmoOrder.getId() + " AND racc_ordergroupid = "
							+ bmoOrderGroup.getId() + " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'"
							+ " AND racc_status = '" + BmoRaccount.STATUS_AUTHORIZED + "'";
					pmConn.doFetch(sql);
					if (pmConn.next()) {
						balanceAuth = pmConn.getDouble("totalRaccGroup");
					}

					double amount = amountOdgp - balanceAuth;
					if (items > 1) {
						amount = amount / items;
					}

					BmoRaccount bmoRaccount = new BmoRaccount();
					ArrayList<BmFilter> filterRacc = new ArrayList<BmFilter>();
					BmFilter filterOrderGroup = new BmFilter();
					filterOrderGroup.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getOrderGroupId(),
							bmoOrderGroup.getId());
					filterRacc.add(filterOrderGroup);
					BmFilter filterStatus = new BmFilter();
					filterStatus.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getStatus(),
							"" + BmoRaccount.STATUS_REVISION);
					filterRacc.add(filterStatus);

					PmRaccount pmRaccount = new PmRaccount(getSFParams());
					Iterator<BmObject> raccountIterator = new PmRaccount(getSFParams()).list(pmConn, filterRacc)
							.iterator();

					while (raccountIterator.hasNext()) {

						// modifiRaccOrderGroup(pmConn, bmoOrder,
						// bmUpdateResult);

						bmoRaccount = (BmoRaccount) raccountIterator.next();

						// Actualizar la CxC si el monto cambio
						//						double amountOrdg = amount;
						//						double amountRacc = bmoRaccount.getAmount().toDouble();
						// if (amountOrdg != amountRacc) {

						bmoRaccount.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(amount));

						if (bmoOrder.getTaxApplies().toBoolean()) {
							// Calcular el IVA
							bmoRaccount.getTaxApplies().setValue(1);
							double taxRate = (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getTax().toDouble()
									/ 100);
							double tax = (bmoRaccount.getAmount().toDouble() * taxRate);
							bmoRaccount.getTax().setValue(SFServerUtil.roundCurrencyDecimals(tax));
							bmoRaccount.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(amount + tax));
							bmoRaccount.getBalance().setValue(bmoRaccount.getTotal().toDouble());
						} else {
							bmoRaccount.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(amount));
							bmoRaccount.getBalance().setValue(amount);
						}

						// Modificar el monto del item
						PmRaccountItem pmRaccountItem = new PmRaccountItem(getSFParams());
						BmoRaccountItem bmoRaccountItem = new BmoRaccountItem();
						bmoRaccountItem = (BmoRaccountItem) pmRaccountItem.getBy(bmoRaccount.getId(),
								bmoRaccountItem.getRaccountId().getName());
						bmoRaccountItem.getQuantity().setValue("1");
						bmoRaccountItem.getPrice().setValue(SFServerUtil.roundCurrencyDecimals(amount));
						bmoRaccountItem.getAmount().setValue(
								SFServerUtil.roundCurrencyDecimals(amount * bmoRaccountItem.getQuantity().toDouble()));

						pmRaccountItem.saveSimple(pmConn, bmoRaccountItem, bmUpdateResult);

						pmRaccount.saveSimple(pmConn, bmoRaccount, bmUpdateResult);
						// }
					}

				} else {

					double balance = 0;
					double balanceRev = 0;

					// Obtener el balance de las CxC ligadas al grupo
					sql = " SELECT SUM(racc_amount) AS totalRaccGroup FROM raccounts "
							+ " LEFT JOIN raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) "
							+ " WHERE racc_orderid = " + bmoOrder.getId() + " AND racc_ordergroupid = "
							+ bmoOrderGroup.getId() + " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'"
							+ " AND racc_status = '" + BmoRaccount.STATUS_REVISION + "'";
					pmConn.doFetch(sql);
					if (pmConn.next()) {
						balanceRev = pmConn.getDouble("totalRaccGroup");
					}


					double balanceAuth = 0;
					sql = " SELECT SUM(racc_amount) AS totalRaccGroup FROM raccounts "
							+ " LEFT JOIN raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) "
							+ " WHERE racc_orderid = " + bmoOrder.getId() + " AND racc_ordergroupid = "
							+ bmoOrderGroup.getId() + " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'"
							+ " AND racc_status = '" + BmoRaccount.STATUS_AUTHORIZED + "'";
					pmConn.doFetch(sql);
					if (pmConn.next()) {
						balanceAuth = pmConn.getDouble("totalRaccGroup");
					}

					balance = bmoOrderGroup.getAmount().toDouble() - (balanceRev + balanceAuth);

					if (balance > 0) {

						// Crear la CXC sobre el monto del pedido
						BmoRaccount bmoNewRaccount = new BmoRaccount();
						bmoNewRaccount.getInvoiceno().setValue(bmoOrderGroup.getName().toString());
						bmoNewRaccount.getReceiveDate()
						.setValue(SFServerUtil.formatDate(getSFParams(), getSFParams().getDateTimeFormat(),
								getSFParams().getDateFormat(), bmoOrder.getLockStart().toString()));
						bmoNewRaccount.getDueDate()
						.setValue(SFServerUtil.formatDate(getSFParams(), getSFParams().getDateTimeFormat(),
								getSFParams().getDateFormat(), bmoOrder.getLockStart().toString()));
						bmoNewRaccount.getDescription().setValue("Creado Automaticamente.");
						bmoNewRaccount.getReference().setValue("Creado Autom.");
						bmoNewRaccount.getOrderGroupId().setValue(bmoOrderGroup.getId());

						bmoNewRaccount.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(balance));

						// Obtener la paridad del pedido
						if (bmoOrder.getCurrencyParity().toDouble() > 0) {
							bmoNewRaccount.getCurrencyParity().setValue(bmoOrder.getCurrencyParity().toDouble());
						}

						if (bmoOrder.getTaxApplies().toBoolean()) {
							// Calcular el IVA
							bmoNewRaccount.getTaxApplies().setValue(1);
							double taxRate = (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getTax().toDouble()
									/ 100);
							double tax = (bmoNewRaccount.getAmount().toDouble() * taxRate);
							bmoNewRaccount.getTax().setValue(SFServerUtil.roundCurrencyDecimals(tax));
							bmoNewRaccount.getTotal().setValue(
									SFServerUtil.roundCurrencyDecimals(bmoNewRaccount.getAmount().toDouble() + tax));
						} else {
							bmoNewRaccount.getTotal().setValue(
									SFServerUtil.roundCurrencyDecimals(bmoNewRaccount.getAmount().toDouble()));
						}

						bmoNewRaccount.getBalance()
						.setValue(SFServerUtil.roundCurrencyDecimals(bmoNewRaccount.getTotal().toDouble()));

						bmoNewRaccount.getRaccountTypeId().setValue(
								((BmoFlexConfig) getSFParams().getBmoAppConfig()).getOrderRaccountTypeId().toInteger());
						bmoNewRaccount.getOrderId().setValue(bmoOrder.getId());
						bmoNewRaccount.getUserId().setValue(bmoOrder.getUserId().toInteger());
						bmoNewRaccount.getCompanyId().setValue(bmoOrder.getCompanyId().toInteger());
						bmoNewRaccount.getCustomerId().setValue(bmoOrder.getCustomerId().toInteger());
						// Obtener la moneda
						if (bmoOrder.getCurrencyId().toInteger() > 0) {
							bmoNewRaccount.getCurrencyId().setValue(bmoOrder.getCurrencyId().toInteger());
						} else {
							bmoNewRaccount.getCurrencyId().setValue(
									((BmoFlexConfig) getSFParams().getBmoAppConfig()).getCurrencyId().toInteger());
						}
						bmoNewRaccount.getStatus().setValue(BmoRaccount.STATUS_REVISION);
						bmoNewRaccount.getFailure().setValue(0);
						bmoNewRaccount.getAutoCreate().setValue(0);
						bmoNewRaccount.getLinked().setValue(0);

						// Asigar la partida de ingresos
						if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
							PmOrder pmOrder = new PmOrder(getSFParams());
							int budgetItemId = 0;
							budgetItemId = pmOrder.getBudgetItemByOrder(pmConn, bmoOrder, bmUpdateResult);

							PmBudgetItem pmBudgetItem = new PmBudgetItem(getSFParams());
							BmoBudgetItem bmoBudgetItem = (BmoBudgetItem) pmBudgetItem.get(pmConn, budgetItemId);

							pmBudgetItem.updateBalance(pmConn, bmoBudgetItem, bmUpdateResult);

							bmoNewRaccount.getBudgetItemId().setValue(budgetItemId);
						}

						super.save(pmConn, bmoNewRaccount, bmUpdateResult);

						PmCompanyNomenclature pmCompanyNomenclature = new PmCompanyNomenclature(getSFParams());
						String code = "";
						// Generar clave personalizada si la hay, si no retorna la de por defecto
						code = pmCompanyNomenclature.getCodeCustom(pmConn,
								bmoNewRaccount.getCompanyId().toInteger(),
								bmoNewRaccount.getProgramCode().toString(),
								bmUpdateResult.getId(),
								BmoRaccount.CODE_PREFIX
								);
						bmoNewRaccount.getCode().setValue(code);

						// Crear el item con el monto de la liquidacion
						PmRaccountItem pmRaccItemNew = new PmRaccountItem(getSFParams());
						BmoRaccountItem bmoRaccItemNew = new BmoRaccountItem();
						bmoRaccItemNew.getName().setValue(bmoNewRaccount.getInvoiceno().toString());
						bmoRaccItemNew.getQuantity().setValue("1");
						bmoRaccItemNew.getAmount()
						.setValue(SFServerUtil.roundCurrencyDecimals(bmoNewRaccount.getAmount().toDouble()));
						bmoRaccItemNew.getPrice()
						.setValue(SFServerUtil.roundCurrencyDecimals(bmoNewRaccount.getAmount().toDouble()));
						bmoRaccItemNew.getRaccountId().setValue(bmoNewRaccount.getId());

						pmRaccItemNew.saveSimple(pmConn, bmoRaccItemNew, bmUpdateResult);

						if (!bmUpdateResult.hasErrors()) {
							// Actualizar id de claves del programa por empresa
							pmCompanyNomenclature.updateConsecutiveByCompany(pmConn, bmoNewRaccount.getCompanyId().toInteger(), 
									bmoNewRaccount.getProgramCode().toString());

							super.save(pmConn, bmoNewRaccount, bmUpdateResult);
						}

						//						super.save(pmConn, bmoNewRaccount, bmUpdateResult);
					}
				}
			}

		} catch (SFException e) {
			throw new SFException(this.getClass().getName() + "createRaccOrderGroup(): " + e.toString());
		} finally {
			pmConn2.close();
			pmConn3.close();
		}

		return bmUpdateResult;
	}

	// Asegura balance del Pedido
	public BmUpdateResult ensureOrderBalance(PmConn pmConn, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult) throws SFException {
		//Obtener el tipo de pedido default
		PmOrderType pmOrderType = new PmOrderType(getSFParams());		
		int orderTypeDefault = ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getDefaultOrderTypeId().toInteger();
		BmoOrderType bmoOrderTypeDefault = (BmoOrderType)pmOrderType.get(pmConn, orderTypeDefault);

		// Crear las CxC de lo orderGroups
		createRaccOrderGroup(pmConn, bmoOrder, bmUpdateResult);

		// Obtener el total de las cxc manuales
		double raccountAmount = 0, balance = 0;
		String sql = "";

		sql = " SELECT SUM(racc_amount) as amount FROM raccounts "
				+ " LEFT JOIN raccounttypes on (racc_raccounttypeid = ract_raccounttypeid) " + " WHERE ract_type = '"
				+ BmoRaccountType.TYPE_WITHDRAW + "'" + " AND racc_autocreate = 0 " + " AND racc_orderid = "
				+ bmoOrder.getId() + " AND racc_currencyid = " + bmoOrder.getCurrencyId().toInteger();
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			raccountAmount = pmConn.getDouble("amount");
		}

		double amountDiffCure = 0;
		double orderParity = 0;

		if (bmoOrder.getCurrencyParity().toDouble() > 0)
			orderParity = bmoOrder.getCurrencyParity().toDouble();
		else
			orderParity = bmoOrder.getBmoCurrency().getParity().toDouble();

		DecimalFormat df = new DecimalFormat("####.####");

		sql = " SELECT * FROM raccounts " + " LEFT JOIN raccounttypes on (racc_raccounttypeid = ract_raccounttypeid) "
				+ " WHERE ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" + " AND racc_autocreate = 0 "
				+ " AND racc_orderid = " + bmoOrder.getId() + " AND racc_currencyid <> "
				+ bmoOrder.getCurrencyId().toInteger();
		pmConn.doFetch(sql);
		while (pmConn.next()) {
			// Covertir a la moneda
			amountDiffCure += Double.parseDouble(df.format((pmConn.getDouble("racc_amount") * pmConn.getDouble("racc_currencyparity")) / orderParity));
		}

		raccountAmount = raccountAmount + amountDiffCure;
		balance = FlexUtil.roundDouble((bmoOrder.getAmount().toDouble() - raccountAmount) - bmoOrder.getDiscount().toDouble(), 4);

		// Id de cxc automatica
		int autoRaccId = 0;
		pmConn.doFetch("SELECT racc_raccountid FROM raccounts "
				+ " LEFT JOIN raccounttypes on (racc_raccounttypeid = ract_raccounttypeid) " + " WHERE ract_type = '"
				+ BmoRaccountType.TYPE_WITHDRAW + "'" + " AND racc_autocreate = 1 " + " AND racc_orderid = "
				+ bmoOrder.getId());
		if (pmConn.next())
			autoRaccId = pmConn.getInt("racc_raccountid");

		// Si el saldo es mayor a 0
		if (balance > 0) {
			// Si ya existe la CxC automatica
			if (autoRaccId > 0) {
				PmRaccount pmRaccAuto = new PmRaccount(getSFParams());
				BmoRaccount bmoRaccAuto = (BmoRaccount) pmRaccAuto.get(pmConn, autoRaccId);
				// Solo modifica el creado automatico si esta en revision
				if (bmoRaccAuto.getStatus().equals(BmoRaccount.STATUS_REVISION)) {

					BmoRaccountItem bmoRaccAutoItem = new BmoRaccountItem();
					PmRaccountItem pmRaccAutoItem = new PmRaccountItem(getSFParams());
					bmoRaccAutoItem = (BmoRaccountItem) pmRaccAutoItem.getBy(pmConn, autoRaccId,
							bmoRaccAutoItem.getRaccountId().getName());

					bmoRaccAutoItem.getPrice().setValue(SFServerUtil.roundCurrencyDecimals(balance));
					bmoRaccAutoItem.getQuantity().setValue(1);
					bmoRaccAutoItem.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(balance));
					// Colocar partida y departamento del pedido
					if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {
						// Validar que exista partida en el pedido
						if (!(bmoOrder.getDefaultBudgetItemId().toInteger() > 0))
							bmUpdateResult.addError(bmoOrder.getDefaultBudgetItemId().getName(), "El Pedido No tiene una Partida Presp.");

						bmoRaccAutoItem.getBudgetItemId().setValue(bmoOrder.getDefaultBudgetItemId().toInteger());
						bmoRaccAutoItem.getAreaId().setValue(bmoOrder.getDefaultAreaId().toInteger());
					}

					pmRaccAutoItem.saveSimple(pmConn, bmoRaccAutoItem, bmUpdateResult);

					bmoRaccAuto.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(balance));

					// Obtener la moneda
					if (bmoOrder.getCurrencyId().toInteger() > 0)
						bmoRaccAuto.getCurrencyId().setValue(bmoOrder.getCurrencyId().toInteger());

					// Obtener la paridad del pedido
					if (bmoOrder.getCurrencyParity().toDouble() > 0) {
						bmoRaccAuto.getCurrencyParity().setValue(bmoOrder.getCurrencyParity().toDouble());
					}

					if (bmoOrder.getTaxApplies().toBoolean()) {
						// Calcular el IVA
						bmoRaccAuto.getTaxApplies().setValue(1);
						double taxRate = (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getTax().toDouble() / 100);
						double tax = (bmoRaccAuto.getAmount().toDouble() * taxRate);
						bmoRaccAuto.getTax().setValue(SFServerUtil.roundCurrencyDecimals(tax));
						bmoRaccAuto.getTotal()
						.setValue(SFServerUtil.roundCurrencyDecimals(bmoRaccAuto.getAmount().toDouble() + tax));
					} else {
						bmoRaccAuto.getTaxApplies().setValue(0);
						bmoRaccAuto.getTax().setValue(0);
						bmoRaccAuto.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(bmoRaccAuto.getAmount().toDouble()));
					}

					bmoRaccAuto.getBalance()
					.setValue(SFServerUtil.roundCurrencyDecimals(bmoRaccAuto.getTotal().toDouble()));
					bmoRaccAuto.getCompanyId().setValue(bmoOrder.getCompanyId().toInteger());

					// Asigar la partida de ingresos
					// Colocar partida y departamento del pedido
					if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {	
						bmoRaccAuto.getBudgetItemId().setValue(bmoOrder.getDefaultBudgetItemId().toInteger());
						bmoRaccAuto.getAreaId().setValue(bmoOrder.getDefaultAreaId().toInteger());

						PmBudgetItem pmBudgetItem = new PmBudgetItem(getSFParams());
						BmoBudgetItem bmoBudgetItem = (BmoBudgetItem) pmBudgetItem.get(pmConn, bmoRaccAuto.getBudgetItemId().toInteger());

						pmBudgetItem.updateBalance(pmConn, bmoBudgetItem, bmUpdateResult);
					}

					//					if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_PROPERTY)) {
					//						if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
					//							PmOrder pmOrder = new PmOrder(getSFParams());
					//							int budgetItemId = 0;
					//							budgetItemId = pmOrder.getBudgetItemByOrder(pmConn, bmoOrder, bmUpdateResult);
					//
					//							PmBudgetItem pmBudgetItem = new PmBudgetItem(getSFParams());
					//							BmoBudgetItem bmoBudgetItem = (BmoBudgetItem) pmBudgetItem.get(pmConn, budgetItemId);
					//
					//							pmBudgetItem.updateBalance(pmConn, bmoBudgetItem, bmUpdateResult);
					//
					//							bmoRaccAuto.getBudgetItemId().setValue(budgetItemId);
					//						}
					//					}
					super.save(pmConn, bmoRaccAuto, bmUpdateResult);

					//Calcular fecha de registro (el metodo de pago del cliente) 
					PmCustomer pmCustomer = new PmCustomer(getSFParams());
					BmoCustomer bmoCustomer = (BmoCustomer)pmCustomer.get(pmConn, bmoRaccAuto.getCustomerId().toInteger());					
					if (bmoCustomer.getReqPayTypeId().toInteger() > 0) {
						PmReqPayType pmReqPayType = new PmReqPayType(getSFParams());
						BmoReqPayType bmoReqPayType = (BmoReqPayType)pmReqPayType.get(pmConn, bmoCustomer.getReqPayTypeId().toInteger());

						if (bmoCustomer.getReqPayTypeId().toInteger() > 0) {	
							// Sumar a la fecha de registros los dias de pago
							Calendar calNow = SFServerUtil.stringToCalendar(getSFParams().getDateFormat(), bmoRaccAuto.getReceiveDate().toString());				
							calNow.add(Calendar.DAY_OF_YEAR, bmoReqPayType.getDays().toInteger());

							//Asignar la fecha de registro				
							bmoRaccAuto.getDueDate().setValue(FlexUtil.calendarToString(getSFParams(), calNow));
						}
						int paymentTypeId = getCustomerPaymentTypeByOrder(pmConn, bmoOrder, bmUpdateResult);
						if (paymentTypeId > 0)
							bmoRaccAuto.getPaymentTypeId().setValue(paymentTypeId);
					}
					// Asignar el usuario del perfil cobranza desde conf. flex.
					asignCollectUser(pmConn, bmoRaccAuto, bmoOrder, bmUpdateResult);

					super.save(pmConn, bmoRaccAuto, bmUpdateResult);

					pmRaccAuto.updateWithdrawBalance(pmConn, bmoRaccAuto, bmUpdateResult);
				}
			} 
			// No existe CxC automatica, crearla
			else {
				BmoRaccount bmoNewRaccount = new BmoRaccount();
				bmoNewRaccount.getInvoiceno().setValue("Creado Autom.");
				bmoNewRaccount.getReceiveDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
				bmoNewRaccount.getDueDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
				bmoNewRaccount.getDueDateStart().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
				//				bmoNewRaccount.getReceiveDate().setValue(SFServerUtil.formatDate(getSFParams(), getSFParams().getDateTimeFormat(), getSFParams().getDateFormat(), bmoOrder.getLockStart().toString()));
				//				bmoNewRaccount.getDueDate().setValue(SFServerUtil.formatDate(getSFParams(), getSFParams().getDateTimeFormat(), getSFParams().getDateFormat(), bmoOrder.getLockStart().toString()));
				bmoNewRaccount.getDescription().setValue("Creado Automaticamente.");
				bmoNewRaccount.getReference().setValue("Creado Autom.");

				bmoNewRaccount.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(balance));

				// Obtener la paridad del pedido
				if (bmoOrder.getCurrencyParity().toDouble() > 0) {
					bmoNewRaccount.getCurrencyParity().setValue(bmoOrder.getCurrencyParity().toDouble());
				}

				if (bmoOrder.getTaxApplies().toBoolean()) {
					// Calcular el IVA
					bmoNewRaccount.getTaxApplies().setValue(1);
					double taxRate = (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getTax().toDouble() / 100);
					double tax = (bmoNewRaccount.getAmount().toDouble() * taxRate);
					bmoNewRaccount.getTax().setValue(SFServerUtil.roundCurrencyDecimals(tax));
					bmoNewRaccount.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(bmoNewRaccount.getAmount().toDouble() + tax));
				} else {
					bmoNewRaccount.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(bmoNewRaccount.getAmount().toDouble()));
				}

				bmoNewRaccount.getBalance().setValue(SFServerUtil.roundCurrencyDecimals(bmoNewRaccount.getTotal().toDouble()));

				bmoNewRaccount.getRaccountTypeId().setValue(((BmoFlexConfig) getSFParams().getBmoAppConfig()).getOrderRaccountTypeId().toInteger());
				bmoNewRaccount.getOrderId().setValue(bmoOrder.getId());
				bmoNewRaccount.getUserId().setValue(bmoOrder.getUserId().toInteger());
				bmoNewRaccount.getCompanyId().setValue(bmoOrder.getCompanyId().toInteger());
				bmoNewRaccount.getCustomerId().setValue(bmoOrder.getCustomerId().toInteger());

				// Obtener la moneda
				if (bmoOrder.getCurrencyId().toInteger() > 0) {
					bmoNewRaccount.getCurrencyId().setValue(bmoOrder.getCurrencyId().toInteger());
				} else {
					bmoNewRaccount.getCurrencyId().setValue(((BmoFlexConfig) getSFParams().getBmoAppConfig()).getCurrencyId().toInteger());
				}

				bmoNewRaccount.getFailure().setValue(0);
				bmoNewRaccount.getLinked().setValue(0);
				bmoNewRaccount.getAutoCreate().setValue(1);

				// Si el monto es mayor a 0, crearla
				if (bmoNewRaccount.getTotal().toDouble() > 0) {
					// Asigar la partida de ingresos
					if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {

						// Validar que exista partida en el pedido
						if (!(bmoOrder.getDefaultBudgetItemId().toInteger() > 0))
							bmUpdateResult.addError(bmoOrder.getDefaultBudgetItemId().getName(), "El Pedido No tiene una Partida Presp.");

						// Colocar partida y departamento del pedido
						bmoNewRaccount.getBudgetItemId().setValue(bmoOrder.getDefaultBudgetItemId().toInteger());
						bmoNewRaccount.getAreaId().setValue(bmoOrder.getDefaultAreaId().toInteger());

						PmBudgetItem pmBudgetItem = new PmBudgetItem(getSFParams());
						BmoBudgetItem bmoBudgetItem = (BmoBudgetItem) pmBudgetItem.get(pmConn, bmoNewRaccount.getBudgetItemId().toInteger());

						pmBudgetItem.updateBalance(pmConn, bmoBudgetItem, bmUpdateResult);
					}

					PmCompanyNomenclature pmCompanyNomenclature = new PmCompanyNomenclature(getSFParams());
					String code = "";

					super.save(pmConn, bmoNewRaccount, bmUpdateResult);

					// Generar clave personalizada si la hay, si no retorna la de por defecto
					code = pmCompanyNomenclature.getCodeCustom(pmConn,
							bmoNewRaccount.getCompanyId().toInteger(),
							bmoNewRaccount.getProgramCode().toString(),
							bmUpdateResult.getId(),
							BmoRaccount.CODE_PREFIX
							);
					bmoNewRaccount.getCode().setValue(code);

					// Obtener metodo/termino del cliente
					if (bmoNewRaccount.getCustomerId().toInteger() > 0) {
						PmCustomer pmCustomer = new PmCustomer(getSFParams());
						BmoCustomer bmoCustomer = (BmoCustomer)pmCustomer.get(pmConn, bmoNewRaccount.getCustomerId().toInteger());
						// Calcular termino de pago del cliente
						if (bmoCustomer.getReqPayTypeId().toInteger() > 0) {
							PmReqPayType pmReqPayType = new PmReqPayType(getSFParams());
							BmoReqPayType bmoReqPayType = (BmoReqPayType)pmReqPayType.get(pmConn, bmoCustomer.getReqPayTypeId().toInteger());

							// Sumar a la fecha de registro los dias de pago
							Calendar calNow = SFServerUtil.stringToCalendar(getSFParams().getDateFormat(), bmoNewRaccount.getReceiveDate().toString());				
							calNow.add(Calendar.DAY_OF_YEAR, bmoReqPayType.getDays().toInteger());

							// Asignar la fecha de programacion, vencimiento y termino de pago del cliente
							bmoNewRaccount.getDueDate().setValue(FlexUtil.calendarToString(getSFParams(), calNow));
							bmoNewRaccount.getDueDateStart().setValue(bmoNewRaccount.getDueDate().toString());
							bmoNewRaccount.getReqPayTypeId().setValue(bmoCustomer.getReqPayTypeId().toInteger());
						}	

						// Traer metodo de pago del cliente de acuerdo al cliente y moneda del pedido
						int paymentTypeId = getCustomerPaymentTypeByOrder(pmConn, bmoOrder, bmUpdateResult);

						if (paymentTypeId > 0)
							bmoNewRaccount.getPaymentTypeId().setValue(paymentTypeId);
					}

					super.save(pmConn, bmoNewRaccount, bmUpdateResult);

					//Autorizar las CxC solo cuando el pedido por default sea de tipo sesion				
					if (bmoOrderTypeDefault.getType().equals(BmoOrderType.TYPE_SESSION))
						bmoNewRaccount.getStatus().setValue(BmoRaccount.STATUS_AUTHORIZED);
					else
						bmoNewRaccount.getStatus().setValue(BmoRaccount.STATUS_REVISION);

					// Crear el item con el monto de la liquidacion
					PmRaccountItem pmRaccItemNew = new PmRaccountItem(getSFParams());
					BmoRaccountItem bmoRaccItemNew = new BmoRaccountItem();
					bmoRaccItemNew.getName().setValue("Ajuste Autom.");
					bmoRaccItemNew.getQuantity().setValue("1");
					bmoRaccItemNew.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(bmoNewRaccount.getAmount().toDouble()));
					bmoRaccItemNew.getPrice().setValue(SFServerUtil.roundCurrencyDecimals(bmoNewRaccount.getAmount().toDouble()));
					bmoRaccItemNew.getRaccountId().setValue(bmoNewRaccount.getId());
					// Asigar la partida de ingresos
					if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
						// Colocar partida y departamento del pedido
						bmoRaccItemNew.getBudgetItemId().setValue(bmoOrder.getDefaultBudgetItemId().toInteger());
						bmoRaccItemNew.getAreaId().setValue(bmoOrder.getDefaultAreaId().toInteger());
					}

					pmRaccItemNew.saveSimple(pmConn, bmoRaccItemNew, bmUpdateResult);				

					// Asignar el usuario del perfil cobranza desde conf. flex.
					asignCollectUser(pmConn, bmoNewRaccount, bmoOrder, bmUpdateResult);

					if (!bmUpdateResult.hasErrors()) {
						// Actualizar id de claves del programa por empresa
						pmCompanyNomenclature.updateConsecutiveByCompany(pmConn, bmoNewRaccount.getCompanyId().toInteger(), 
								bmoNewRaccount.getProgramCode().toString());

						super.save(pmConn, bmoNewRaccount, bmUpdateResult);
					}
					//					super.save(pmConn, bmoNewRaccount, bmUpdateResult);
				}
			}
		} else {
			// Eliminar la cxc de creación automatica
			if (autoRaccId > 0) {
				PmRaccount pmRaccAuto = new PmRaccount(getSFParams());
				BmoRaccount bmoRaccAuto = (BmoRaccount) pmRaccAuto.get(pmConn, autoRaccId);
				pmRaccAuto.delete(pmConn, bmoRaccAuto, bmUpdateResult);
			}
		}

		// Update Payments	
		PmOrder pmOrder = new PmOrder(getSFParams());
		pmOrder.updatePayments(pmConn, bmoOrder, bmUpdateResult);

		return bmUpdateResult;
	}

	// Obtener tipo de cliente por pedido
	private int getCustomerPaymentTypeByOrder(PmConn pmConn, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		int customerPaymentTypeId = -1;
		sql = " SELECT * FROM customerpaymenttypes " +
				" WHERE cupt_customerid = " + bmoOrder.getCustomerId().toInteger() +
				" AND cupt_currencyid = " + bmoOrder.getCurrencyId().toInteger();
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			customerPaymentTypeId = pmConn.getInt("cupt_paymenttypeid");
		}

		return customerPaymentTypeId;
	}

	// Obtener tipo de pago por cliente
	private int getCustomerPaymentTypeByCust(PmConn pmConn, int customerId, BmoRaccount bmoRaccount, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		int customerPaymentTypeId = 0;
		sql = " SELECT * FROM customerpaymenttypes " +
				" WHERE cupt_customerid = " + customerId +
				" AND cupt_currencyid = " + bmoRaccount.getCurrencyId().toInteger();
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			customerPaymentTypeId = pmConn.getInt("cupt_paymenttypeid");
		}

		return customerPaymentTypeId;
	}

	// Revisa si existen cuentas x cobrar autorizadas de un pedido
	public boolean orderHasAuthorizedRaccounts(PmConn pmConn, int orderId) throws SFException {
		pmConn.doFetch("SELECT * FROM raccounts WHERE " + "racc_status = '" + BmoRaccount.STATUS_AUTHORIZED + "'"
				+ " AND racc_orderid = " + orderId);
		return pmConn.next();
	}

	// Revisa si hay defaults en pagos(COBI)
	public void checkFailure(String nowDate) throws SFException {				
		PmConn pmConn = new PmConn(getSFParams());
		PmConn pmConn2 = new PmConn(getSFParams());
		PmConn pmConn3 = new PmConn(getSFParams());

		BmUpdateResult bmUpdateResult = new BmUpdateResult();
		pmConn.open();
		pmConn2.open();
		pmConn3.open();

		try {
			String sql = "";
			BmoRaccount bmoRaccWeek = new BmoRaccount();

			PmOrder pmOrder = new PmOrder(getSFParams());
			BmoOrder bmoOrder = new BmoOrder();

			// Obtener el Lunes de esta semana
			Calendar payWeek = SFServerUtil.stringToCalendar(getSFParams().getDateFormat(),
					SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
			// Obtener el día
			int nowDay = payWeek.get(Calendar.DAY_OF_WEEK);

			if (nowDay == 1)
				nowDay = 6;
			// Monday
			else if (nowDay == 2)
				nowDay = 0;
			// Thusday
			else if (nowDay == 3)
				nowDay = 1;
			// Weendays
			else if (nowDay == 4)
				nowDay = 2;
			// Thurday
			else if (nowDay == 5)
				nowDay = 3;
			// Friday
			else if (nowDay == 6)
				nowDay = 4;
			// Saturday
			else if (nowDay == 7)
				nowDay = 5;

			payWeek.add(Calendar.DAY_OF_WEEK, -nowDay);

			String datePayout = "";
			if (nowDate.equals(""))
				datePayout = FlexUtil.calendarToString(getSFParams(), payWeek);
			else
				datePayout = nowDate;

			// Listas las cuentas por cobrar
			sql = " SELECT DISTINCT(racc_orderid), racc_orderid , racc_raccountid, racc_total FROM raccounts "
					+ " LEFT JOIN raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) "
					+ " LEFT JOIN orders ON (racc_orderid = orde_orderid) " 
					+ " WHERE racc_duedate = '" + datePayout + "' " 
					+ " AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "'" 
					+ " AND racc_status = '" + BmoRaccount.STATUS_AUTHORIZED + "'" 
					+ " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'"
					+ " AND racc_failure = 0 ";
			//+ " GROUP BY racc_orderid ";
			pmConn2.doFetch(sql);
			while (pmConn2.next()) {
				bmoOrder = (BmoOrder) pmOrder.get(pmConn, pmConn2.getInt("racc_orderid"));

				bmoRaccWeek = (BmoRaccount) this.get(pmConn, pmConn2.getInt("racc_raccountid"));

				if (!bmoRaccWeek.getPaymentStatus().equals(BmoRaccount.PAYMENTSTATUS_TOTAL)) {
					// Generar Penalidad
					if (!hasPenalty(pmConn, bmoOrder, bmUpdateResult)) {
						createPenalty(pmConn, bmoOrder, pmConn2.getInt("racc_raccountid"), bmUpdateResult);
					} else {
						BmoCredit bmoCredit = new BmoCredit();
						PmCredit pmCredit = new PmCredit(getSFParams());
						bmoCredit = (BmoCredit) pmCredit.getBy(pmConn, bmoOrder.getId(),
								bmoCredit.getOrderId().getName());

						bmoCredit.getPaymentStatus().setValue(BmoCredit.PAYMENTSTATUS_INPROBLEM);

						pmCredit.saveSimple(pmConn, bmoCredit, bmUpdateResult);

						// Ligar la Penalizacion ya creada a la CxC que fallo
						// esta semana
						updatePenaltyFromNewFail(pmConn, bmoOrder, pmConn2.getInt("racc_raccountid"), bmUpdateResult);
					}
				} else {
					BmoCredit bmoCredit = new BmoCredit();
					PmCredit pmCredit = new PmCredit(getSFParams());
					bmoCredit = (BmoCredit) pmCredit.getBy(pmConn, bmoOrder.getId(), bmoCredit.getOrderId().getName());

					if (bmoCredit.getPaymentStatus().equals(BmoCredit.PAYMENTSTATUS_REVISION)) {
						bmoCredit.getPaymentStatus().setValue(BmoCredit.PAYMENTSTATUS_NORMAL);
						pmCredit.saveSimple(pmConn, bmoCredit, bmUpdateResult);
					}
					// Manejo de la Semana 10

					// Obtener la Primera fecha de pago del crédito
					sql = " SELECT COUNT(*) AS items FROM raccounts "
							+ " LEFT JOIN raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) "
							+ " WHERE racc_orderid = " + bmoOrder.getId() + " AND ract_type = '"
							+ BmoRaccountType.TYPE_WITHDRAW + "'" + " AND racc_duedate <= '" + datePayout + "'"
							+ " ORDER BY racc_raccountid ";
					pmConn3.doFetch(sql);
					if (pmConn3.next()) {

						int items = pmConn3.getInt("items");

						if (items >= 9) {

							// Obtener el pago individual
							double payment = pmConn2.getDouble("racc_total");

							// Calcular el monto que debe estar pagado al día de
							// hoy
							payment = payment * items;

							sql = " SELECT SUM(racc_total) AS payment FROM raccounts  " + " WHERE racc_orderid = "
									+ bmoOrder.getId() + " AND racc_duedate <= '" + datePayout + "'";
							pmConn3.doFetch(sql);
							if (pmConn3.next()) {
								if (pmConn3.getDouble("payment") >= payment) {
									sql = " UPDATE raccounts SET racc_receivedate = '" + datePayout + "'"
											+ " WHERE racc_orderid = " + bmoOrder.getId() + " AND racc_duedate >= '"
											+ datePayout + "'";
									pmConn3.doUpdate(sql);
								}
							}
						}
					}
				}
			}
		} catch (SFException e) {
			throw new SFException(this.getClass().getName() + "checkFailure(): " + e.toString());
		} finally {
			pmConn.close();
			pmConn2.close();
			pmConn3.close();
		}
	}

	
	// Revisa si hay fallas DIARIAMENTE en los pagos (DaCredito)
	public void checkFailureDailyDaCredito(String nowDate) throws SFException {
		printDevLog("checkFailureDailyDaCredito");
		PmConn pmConn = new PmConn(getSFParams());
		PmConn pmConn2 = new PmConn(getSFParams());
		PmConn pmConn3 = new PmConn(getSFParams());

		BmUpdateResult bmUpdateResult = new BmUpdateResult();
		pmConn.open();
		pmConn2.open();
		pmConn3.open();

		try {
			String sql = "";
			String datePayout = "";

			BmoRaccount bmoRaccWeek = new BmoRaccount();

			PmOrder pmOrder = new PmOrder(getSFParams());
			BmoOrder bmoOrder = new BmoOrder();
			PmCreditType pmCreditType = new PmCreditType(getSFParams());
			BmoCreditType bmoCreditType = new BmoCreditType();
			bmoCreditType = (BmoCreditType)pmCreditType.getBy(pmConn, "" + BmoCreditType.TYPE_DAILY, bmoCreditType.getType().getName());
			
			// Obtener dia de ayer
			Calendar payWeek = SFServerUtil.stringToCalendar(getSFParams().getDateFormat(), SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
			payWeek.add(Calendar.DAY_OF_WEEK, -1);
			int dayOfWeek = payWeek.get(Calendar.DAY_OF_WEEK);
			datePayout = FlexUtil.calendarToString(getSFParams(), payWeek);

			// Revisar si ayer es dia de pago, sino traer el que le sigue
			datePayout = pmCreditType.getLastPaymentDay(datePayout, dayOfWeek, bmoCreditType, true);

			if (!nowDate.equals(""))
				datePayout = nowDate;

			// Lista de las cuentas por cobrar del dia anterior
			sql = " SELECT DISTINCT(racc_orderid), racc_orderid , racc_raccountid, racc_total, cred_creditid, crty_type FROM raccounts "
					+ " LEFT JOIN raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) "
					+ " LEFT JOIN orders ON (racc_orderid = orde_orderid) " 
					+ " LEFT JOIN credits ON (cred_orderid= orde_orderid) " 
					+ " LEFT JOIN credittypes ON (crty_credittypeid = cred_credittypeid) " 
					+ " WHERE racc_duedate = '" + datePayout + "' " 
					+ " AND crty_type = '" + BmoCreditType.TYPE_DAILY + "'"
					+ " AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "'" 
					+ " AND racc_status = '" + BmoRaccount.STATUS_AUTHORIZED + "'" 
					+ " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'"
					+ " AND racc_failure = 0 ";
			//+ " GROUP BY racc_orderid ";
			
			printDevLog("checkFailureDaily_sql:"+sql);
			pmConn2.doFetch(sql);
			while (pmConn2.next()) {
				bmoRaccWeek = (BmoRaccount) this.get(pmConn, pmConn2.getInt("racc_raccountid"));
				bmoOrder = (BmoOrder) pmOrder.get(pmConn, pmConn2.getInt("racc_orderid"));

				if (!bmoRaccWeek.getPaymentStatus().equals(BmoRaccount.PAYMENTSTATUS_TOTAL)) {
					// Generar Penalidad si no existe
					if (!hasPenalty(pmConn, bmoOrder, bmUpdateResult)) {
						createPenalty(pmConn, bmoOrder, pmConn2.getInt("racc_raccountid"), bmUpdateResult);
					} else {
						BmoCredit bmoCredit = new BmoCredit();
						PmCredit pmCredit = new PmCredit(getSFParams());
						bmoCredit = (BmoCredit) pmCredit.get(pmConn, pmConn2.getInt("cred_creditid"));
							
						int failureId = 0;
						// Obtener la CxC de penalización
						sql = " SELECT racc_raccountid FROM raccounts " + " WHERE racc_failure = 1 " + " AND racc_orderid = " + bmoOrder.getId();
						pmConn3.doFetch(sql);
						if (pmConn3.next()) {
							failureId = pmConn3.getInt("racc_raccountid");
						}
						
						// Contar las fallas del credito
						int items = -1;
						sql = " SELECT COUNT(*) AS items FROM raccountlinks  WHERE ralk_raccountid = " + failureId;
						pmConn3.doFetch(sql);
						if (pmConn3.next()) {
							items = pmConn3.getInt("items");
						}
						
						printDevLog("CXC ligadas con fallas:"+items);
						// Se inicia desde 0 porque apenas se estan generando las fallas
						if (items == 0 || items == 1) {
							printDevLog("1era o 2da falla, genera la liga y cambia estatus a penalidad");
							bmoCredit.getPaymentStatus().setValue(BmoCredit.PAYMENTSTATUS_PENALTY);
							pmCredit.saveSimple(pmConn, bmoCredit, bmUpdateResult);
							
							// Ligar la Penalizacion ya creada a la CxC que fallo 
							updatePenaltyFromNewFail(pmConn, bmoOrder, pmConn2.getInt("racc_raccountid"), bmUpdateResult);
							
							// Obtener la cxc automatica para su modificacion, SOLO MONTOS
							if (!bmUpdateResult.hasErrors()) {
								PmRaccount pmRaccountFailure = new PmRaccount(getSFParams());
								BmoRaccount bmoRaccountFailure = new BmoRaccount();
								bmoRaccountFailure = (BmoRaccount)pmRaccountFailure.get(pmConn, failureId);
		
								BmoRaccountItem bmoRaccFailureItem = new BmoRaccountItem();
								PmRaccountItem pmRaccFailureItem = new PmRaccountItem(getSFParams());
								bmoRaccFailureItem = (BmoRaccountItem) pmRaccFailureItem.getBy(pmConn, bmoRaccountFailure.getId(), bmoRaccFailureItem.getRaccountId().getName());
								bmoRaccFailureItem.getPrice().setValue (bmoRaccountFailure.getAmount().toDouble() + bmoCredit.getBmoCreditType().getAmountFailure().toDouble());
								bmoRaccFailureItem.getAmount().setValue (bmoRaccountFailure.getAmount().toDouble() + bmoCredit.getBmoCreditType().getAmountFailure().toDouble());
								pmRaccFailureItem.saveSimple(pmConn, bmoRaccFailureItem, bmUpdateResult);
		
								bmoRaccountFailure.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(bmoRaccFailureItem.getAmount().toDouble()));
								bmoRaccountFailure.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(bmoRaccFailureItem.getAmount().toDouble()));
		
								pmRaccountFailure.updateWithdrawBalance(pmConn, bmoRaccountFailure, bmUpdateResult);
							}
						} else if (items == 2) {
							printDevLog("por generar la 3era, genera la liga y cambia estatus a penalidad");
							bmoCredit.getPaymentStatus().setValue(BmoCredit.PAYMENTSTATUS_INPROBLEM);
							pmCredit.saveSimple(pmConn, bmoCredit, bmUpdateResult);
							
							// Ligar la Penalizacion ya creada a la CxC que fallo 
							updatePenaltyFromNewFail(pmConn, bmoOrder, pmConn2.getInt("racc_raccountid"), bmUpdateResult);
							
							// Obtener la cxc automatica para su modificacion, SOLO MONTOS
							if (!bmUpdateResult.hasErrors()) {
								PmRaccount pmRaccountFailure = new PmRaccount(getSFParams());
								BmoRaccount bmoRaccountFailure = new BmoRaccount();
								bmoRaccountFailure = (BmoRaccount)pmRaccountFailure.get(pmConn, failureId);
		
								BmoRaccountItem bmoRaccFailureItem = new BmoRaccountItem();
								PmRaccountItem pmRaccFailureItem = new PmRaccountItem(getSFParams());
								bmoRaccFailureItem = (BmoRaccountItem) pmRaccFailureItem.getBy(pmConn, bmoRaccountFailure.getId(), bmoRaccFailureItem.getRaccountId().getName());
								bmoRaccFailureItem.getPrice().setValue (bmoRaccountFailure.getAmount().toDouble() + bmoCredit.getBmoCreditType().getAmountFailure().toDouble());
								bmoRaccFailureItem.getAmount().setValue (bmoRaccountFailure.getAmount().toDouble() + bmoCredit.getBmoCreditType().getAmountFailure().toDouble());
								pmRaccFailureItem.saveSimple(pmConn, bmoRaccFailureItem, bmUpdateResult);
		
								bmoRaccountFailure.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(bmoRaccFailureItem.getAmount().toDouble()));
								bmoRaccountFailure.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(bmoRaccFailureItem.getAmount().toDouble()));
		
								pmRaccountFailure.updateWithdrawBalance(pmConn, bmoRaccountFailure, bmUpdateResult);
							}
						} else if (items >= 3) { 
							printDevLog("sobrepasa las 3 fallas, solo forza el estatus de en problemas al credito");
							// Si pasa de 3, solo colocar estatus de en problemas
							bmoCredit.getPaymentStatus().setValue(BmoCredit.PAYMENTSTATUS_INPROBLEM);
							pmCredit.saveSimple(pmConn, bmoCredit, bmUpdateResult);
						}
					}
				} else {
					BmoCredit bmoCredit = new BmoCredit();
					PmCredit pmCredit = new PmCredit(getSFParams());
					bmoCredit = (BmoCredit) pmCredit.getBy(pmConn, bmoOrder.getId(), bmoCredit.getOrderId().getName());

					if (bmoCredit.getPaymentStatus().equals(BmoCredit.PAYMENTSTATUS_REVISION)) {
						bmoCredit.getPaymentStatus().setValue(BmoCredit.PAYMENTSTATUS_NORMAL);
						pmCredit.saveSimple(pmConn, bmoCredit, bmUpdateResult);
					}
					
				}
			}
		} catch (SFException e) {
			throw new SFException(this.getClass().getName() + "checkFailure(): " + e.toString());
		} finally {
			pmConn.close();
			pmConn2.close();
			pmConn3.close();
		}
	}
	
	// Revisa si hay fallas SEMANALMENTE en los pagos(DaCredito)
	public void checkFailureWeeklyDaCredito(String nowDate) throws SFException {
		
		printDevLog("checkFailureWeeklyDaCredito");
		PmConn pmConn = new PmConn(getSFParams());
		PmConn pmConn2 = new PmConn(getSFParams());
		PmConn pmConn3 = new PmConn(getSFParams());

		BmUpdateResult bmUpdateResult = new BmUpdateResult();
		pmConn.open();
		pmConn2.open();
		pmConn3.open();

		try {
			String sql = "";
			String datePayout = "";

			BmoRaccount bmoRaccWeek = new BmoRaccount();

			PmOrder pmOrder = new PmOrder(getSFParams());
			BmoOrder bmoOrder = new BmoOrder();
			
			// Obtener el Lunes de esta semana
			Calendar payWeek = SFServerUtil.stringToCalendar(getSFParams().getDateFormat(),	SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
			// Obtener el día de la semana
			int nowDay = payWeek.get(Calendar.DAY_OF_WEEK);
			// restarle dias de acuerdo al dia de la semana para que siempre te de el lunes de la semana pasada
			if (nowDay == 1)
				nowDay = 6;
			// Monday
			else if (nowDay == 2)
				nowDay = 0;
			// Thusday
			else if (nowDay == 3)
				nowDay = 1;
			// Weendays
			else if (nowDay == 4)
				nowDay = 2;
			// Thurday
			else if (nowDay == 5)
				nowDay = 3;
			// Friday
			else if (nowDay == 6)
				nowDay = 4;
			// Saturday
			else if (nowDay == 7)
				nowDay = 5;

			payWeek.add(Calendar.DAY_OF_WEEK, -nowDay);

			if (nowDate.equals(""))
				datePayout = FlexUtil.calendarToString(getSFParams(), payWeek);
			else
				datePayout = nowDate;

			// Lista de las cuentas por cobrar del dia anterior
			sql = " SELECT DISTINCT(racc_orderid), racc_orderid , racc_raccountid, racc_total, cred_creditid, crty_type FROM raccounts "
					+ " LEFT JOIN raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) "
					+ " LEFT JOIN orders ON (racc_orderid = orde_orderid) " 
					+ " LEFT JOIN credits ON (cred_orderid= orde_orderid) " 
					+ " LEFT JOIN credittypes ON (crty_credittypeid = cred_credittypeid) " 
					+ " WHERE racc_duedate = '" + datePayout + "' " 
					+ " AND crty_type = '" + BmoCreditType.TYPE_WEEKLY + "'"
					+ " AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "'" 
					+ " AND racc_status = '" + BmoRaccount.STATUS_AUTHORIZED + "'" 
					+ " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'"
					+ " AND racc_failure = 0 ";
			//+ " GROUP BY racc_orderid ";
			
			printDevLog("checkFailureWeekly_sql:"+sql);
			pmConn2.doFetch(sql);
			while (pmConn2.next()) {
				bmoRaccWeek = (BmoRaccount) this.get(pmConn, pmConn2.getInt("racc_raccountid"));
				bmoOrder = (BmoOrder) pmOrder.get(pmConn, pmConn2.getInt("racc_orderid"));

				if (!bmoRaccWeek.getPaymentStatus().equals(BmoRaccount.PAYMENTSTATUS_TOTAL)) {
					// Generar Penalidad si no existe
					if (!hasPenalty(pmConn, bmoOrder, bmUpdateResult)) {
						createPenalty(pmConn, bmoOrder, pmConn2.getInt("racc_raccountid"), bmUpdateResult);
					} else {
						BmoCredit bmoCredit = new BmoCredit();
						PmCredit pmCredit = new PmCredit(getSFParams());
						bmoCredit = (BmoCredit) pmCredit.get(pmConn, pmConn2.getInt("cred_creditid"));
						
						int failureId = 0;
						// Obtener la CxC de penalización
						sql = " SELECT racc_raccountid FROM raccounts " + " WHERE racc_failure = 1 " + " AND racc_orderid = " + bmoOrder.getId();
						pmConn3.doFetch(sql);
						if (pmConn3.next()) {
							failureId = pmConn3.getInt("racc_raccountid");
						}
						
						// Contar las fallas del credito
						int items = -1;
						sql = " SELECT COUNT(*) AS items FROM raccountlinks  WHERE ralk_raccountid = " + failureId;
						pmConn3.doFetch(sql);
						if (pmConn3.next()) {
							items = pmConn3.getInt("items");
						}
						
						printDevLog("CXC ligadas con fallas:"+items);
						// Se inicia desde 0 porque apenas se estan generando las fallas
						if (items == 0 || items == 1) {
							printDevLog("1era o 2da falla, genera la liga y cambia estatus a penalidad");
							bmoCredit.getPaymentStatus().setValue(BmoCredit.PAYMENTSTATUS_PENALTY);
							pmCredit.saveSimple(pmConn, bmoCredit, bmUpdateResult);
							
							// Ligar la Penalizacion ya creada a la CxC que fallo 
							updatePenaltyFromNewFail(pmConn, bmoOrder, pmConn2.getInt("racc_raccountid"), bmUpdateResult);
							
							// Obtener la cxc automatica para su modificacion, SOLO MONTOS
							if (!bmUpdateResult.hasErrors()) {
								PmRaccount pmRaccountFailure = new PmRaccount(getSFParams());
								BmoRaccount bmoRaccountFailure = new BmoRaccount();
								bmoRaccountFailure = (BmoRaccount)pmRaccountFailure.get(pmConn, failureId);
		
								BmoRaccountItem bmoRaccFailureItem = new BmoRaccountItem();
								PmRaccountItem pmRaccFailureItem = new PmRaccountItem(getSFParams());
								bmoRaccFailureItem = (BmoRaccountItem) pmRaccFailureItem.getBy(pmConn, bmoRaccountFailure.getId(), bmoRaccFailureItem.getRaccountId().getName());
								bmoRaccFailureItem.getPrice().setValue (bmoRaccountFailure.getAmount().toDouble() + bmoCredit.getBmoCreditType().getAmountFailure().toDouble());
								bmoRaccFailureItem.getAmount().setValue (bmoRaccountFailure.getAmount().toDouble() + bmoCredit.getBmoCreditType().getAmountFailure().toDouble());
								pmRaccFailureItem.saveSimple(pmConn, bmoRaccFailureItem, bmUpdateResult);
		
								bmoRaccountFailure.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(bmoRaccFailureItem.getAmount().toDouble()));
								bmoRaccountFailure.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(bmoRaccFailureItem.getAmount().toDouble()));
		
								pmRaccountFailure.updateWithdrawBalance(pmConn, bmoRaccountFailure, bmUpdateResult);
							}
						} else if (items == 2) {
							printDevLog("por generar la 3era, genera la liga y cambia estatus a penalidad");
							bmoCredit.getPaymentStatus().setValue(BmoCredit.PAYMENTSTATUS_INPROBLEM);
							pmCredit.saveSimple(pmConn, bmoCredit, bmUpdateResult);
							
							// Ligar la Penalizacion ya creada a la CxC que fallo 
							updatePenaltyFromNewFail(pmConn, bmoOrder, pmConn2.getInt("racc_raccountid"), bmUpdateResult);
							
							// Obtener la cxc automatica para su modificacion, SOLO MONTOS
							if (!bmUpdateResult.hasErrors()) {
								PmRaccount pmRaccountFailure = new PmRaccount(getSFParams());
								BmoRaccount bmoRaccountFailure = new BmoRaccount();
								bmoRaccountFailure = (BmoRaccount)pmRaccountFailure.get(pmConn, failureId);
		
								BmoRaccountItem bmoRaccFailureItem = new BmoRaccountItem();
								PmRaccountItem pmRaccFailureItem = new PmRaccountItem(getSFParams());
								bmoRaccFailureItem = (BmoRaccountItem) pmRaccFailureItem.getBy(pmConn, bmoRaccountFailure.getId(), bmoRaccFailureItem.getRaccountId().getName());
								bmoRaccFailureItem.getPrice().setValue (bmoRaccountFailure.getAmount().toDouble() + bmoCredit.getBmoCreditType().getAmountFailure().toDouble());
								bmoRaccFailureItem.getAmount().setValue (bmoRaccountFailure.getAmount().toDouble() + bmoCredit.getBmoCreditType().getAmountFailure().toDouble());
								pmRaccFailureItem.saveSimple(pmConn, bmoRaccFailureItem, bmUpdateResult);
		
								bmoRaccountFailure.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(bmoRaccFailureItem.getAmount().toDouble()));
								bmoRaccountFailure.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(bmoRaccFailureItem.getAmount().toDouble()));
		
								pmRaccountFailure.updateWithdrawBalance(pmConn, bmoRaccountFailure, bmUpdateResult);
							}
						} else if (items >= 3) { 
							printDevLog("sobrepasa las 3 fallas, solo forza el estatus de en problemas al credito");
							// Si pasa de 3, solo colocar estatus de en problemas
							bmoCredit.getPaymentStatus().setValue(BmoCredit.PAYMENTSTATUS_INPROBLEM);
							pmCredit.saveSimple(pmConn, bmoCredit, bmUpdateResult);
						}
					}
				} else {
					BmoCredit bmoCredit = new BmoCredit();
					PmCredit pmCredit = new PmCredit(getSFParams());
					bmoCredit = (BmoCredit) pmCredit.getBy(pmConn, bmoOrder.getId(), bmoCredit.getOrderId().getName());

					if (bmoCredit.getPaymentStatus().equals(BmoCredit.PAYMENTSTATUS_REVISION)) {
						bmoCredit.getPaymentStatus().setValue(BmoCredit.PAYMENTSTATUS_NORMAL);
						pmCredit.saveSimple(pmConn, bmoCredit, bmUpdateResult);
					}
					
				}
			}
		} catch (SFException e) {
			throw new SFException(this.getClass().getName() + "checkFailure(): " + e.toString());
		} finally {
			pmConn.close();
			pmConn2.close();
			pmConn3.close();
		}
	}

	// Crear la falla dentro de la penalización
	public void updatePenaltyFromNewFail(PmConn pmConn, BmoOrder bmoOrder, int raccountOrignId,
			BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		int failureId = 0;
		// Obtener la CxC de penalización
		sql = " SELECT racc_raccountid FROM raccounts " + " WHERE racc_failure = 1 " + " AND racc_orderid = "
				+ bmoOrder.getId();
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			failureId = pmConn.getInt("racc_raccountid");
		}

		// Revisar que el no exista la relación creada anteriormente
		int items = 0;
		sql = " SELECT COUNT(*) AS items FROM raccountlinks " + " WHERE ralk_raccountid = " + failureId
				+ " AND ralk_foreignid = " + raccountOrignId;
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			items = pmConn.getInt("items");
		}

		if (items == 0) {
			// Crear la relación
			PmRaccountLink pmRaccountLink = new PmRaccountLink(getSFParams());
			BmoRaccountLink bmoNewRaccountLink = new BmoRaccountLink();

			bmoNewRaccountLink.getRaccountId().setValue(failureId);
			bmoNewRaccountLink.getForeignId().setValue(raccountOrignId);
			pmRaccountLink.save(pmConn, bmoNewRaccountLink, bmUpdateResult);
		}

		// Marcar como Externa la CxC origen
		sql = " UPDATE raccounts SET racc_linked = 1 WHERE racc_raccountid = " + raccountOrignId;
		pmConn.doUpdate(sql);

		//Crear la bitacora
		PmRaccount pmRaccount = new PmRaccount(getSFParams());
		BmoRaccount bmoOrign = (BmoRaccount)pmRaccount.get(pmConn, raccountOrignId);
		String log = "Pago no registrado de la " + bmoOrign.getCode().toString() + 
				"/" + bmoOrign.getInvoiceno().toString();

		PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());
		pmWFlowLog.addDataLog(pmConn, bmUpdateResult, bmoOrder.getWFlowId().toInteger(), BmoWFlowLog.TYPE_OTHER, log, "");
	}

	// Suma items CxC
	private double sumRaccountItems(PmConn pmConn, BmoRaccount bmoRaccount, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		PmRaccountType pmRaccountType = new PmRaccountType(getSFParams());
		BmoRaccountType bmoRaccountType = (BmoRaccountType) pmRaccountType.get(pmConn,
				bmoRaccount.getRaccountTypeId().toInteger());

		double sumItems = 0;
		if (bmoRaccountType.getType().equals(BmoRaccountType.TYPE_WITHDRAW)) {
			sql = " SELECT ROUND(SUM(rait_amount), 2) AS sumItems FROM raccountitems " + " WHERE rait_raccountid = "
					+ bmoRaccount.getId();
			pmConn.doFetch(sql);
			if (pmConn.next())
				sumItems = pmConn.getDouble("sumItems");
		} else {
			sql = " SELECT SUM(rait_amount) AS sumItems FROM raccountitems " + " WHERE rait_raccountid = "
					+ bmoRaccount.getId();
			pmConn.doFetch(sql);
			if (pmConn.next())
				sumItems = pmConn.getDouble("sumItems");
		}
		return sumItems;
	}

	// Cambiar partida presupuestal de cada cxc del pedido
	public BmUpdateResult changeBudgetItemByOrder(PmConn pmConn, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult)
			throws SFException {
		BmoRaccount bmoRaccount = new BmoRaccount();
		PmRaccount pmRaccount = new PmRaccount(getSFParams());
		//		PmOrder pmOrder = new PmOrder(getSFParams());

		ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();

		BmFilter filterRaccountList = new BmFilter();
		filterRaccountList.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getOrderId(), bmoOrder.getId());
		filterList.add(filterRaccountList);

		Iterator<BmObject> listRaccounts = new PmRaccount(getSFParams()).list(pmConn, filterList).iterator();

		while (listRaccounts.hasNext()) {
			bmoRaccount = (BmoRaccount) listRaccounts.next();

			// Cambiar partida presupuestal de cada cxc al reubicar inmueble
			//			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_PROPERTY)) {
			//				int budgetItemId = pmOrder.getBudgetItemByOrder(pmConn, bmoOrder, bmUpdateResult);
			//				bmoRaccount.getCompanyId().setValue(bmoOrder.getCompanyId().toInteger());
			bmoRaccount.getBudgetItemId().setValue(bmoOrder.getDefaultBudgetItemId().toInteger());
			bmoRaccount.getAreaId().setValue(bmoOrder.getDefaultAreaId().toInteger());
			//			}
			pmRaccount.save(pmConn, bmoRaccount, bmUpdateResult);
		}

		return bmUpdateResult;
	}

	// Recalcular las Fechas de las CxC si se cambia la fecha del crédito
	public void recalculatedRaccDate(BmoCredit bmoCredit, BmUpdateResult bmUpdateResult) throws SFException {
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();

		PmConn pmConn2 = new PmConn(getSFParams());
		pmConn2.open();

		PmConn pmConn3 = new PmConn(getSFParams());
		pmConn3.open();

		String sql = "";

		PmRaccount pmRaccount = new PmRaccount(getSFParams());
		BmoRaccount bmoRaccount = new BmoRaccount();
		try {

			pmConn.disableAutoCommit();
			
			// Obtener el Plazo
			PmCreditType pmCreditType = new PmCreditType(getSFParams());
			BmoCreditType bmoCreditType = (BmoCreditType) pmCreditType.get(bmoCredit.getCreditTypeId().toInteger());

			// Obtener los dias de pago
			int daysPayout = 0;
			if (bmoCreditType.getType().equals(BmoCreditType.TYPE_DAILY)) {
				daysPayout = 1;
			} else if (bmoCreditType.getType().equals(BmoCreditType.TYPE_WEEKLY)) {
				daysPayout = 7;
			} else if (bmoCreditType.getType().equals(BmoCreditType.TYPE_TWOWEEKS)) {
				daysPayout = 15;
			} else if (bmoCreditType.getType().equals(BmoCreditType.TYPE_MONTHLY)) {
				daysPayout = 30;
			} else {
				bmUpdateResult.addMsg("El Tipo de crédito no cuenta con un tipo definido.");
			}

			String datePayout = "";
			if (bmoCreditType.getType().equals(BmoCreditType.TYPE_DAILY)) {
				datePayout = bmoCredit.getStartDate().toString();
			} else if (bmoCreditType.getType().equals(BmoCreditType.TYPE_WEEKLY)) {
				// EL primer dia de pago debe ser el lunes proximo
				Calendar nowWeek = SFServerUtil.stringToCalendar(getSFParams().getDateFormat(),
						bmoCredit.getStartDate().toString());
				nowWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
				nowWeek.add(Calendar.WEEK_OF_YEAR, 0);
				// Calcular la primera fecha de pago
				datePayout = FlexUtil.calendarToString(getSFParams(), nowWeek);
			}

			sql = " SELECT * FROM raccounts "
					+ " LEFT JOIN raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) "
					+ " WHERE ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" + " AND racc_orderid = "
					+ bmoCredit.getOrderId().toInteger() + " ORDER BY racc_raccountid ";
			pmConn2.doFetch(sql);
			while (pmConn2.next()) {
				bmoRaccount = (BmoRaccount) pmRaccount.get(pmConn2.getInt("racc_raccountid"));

				if (bmoRaccount.getTotal().toDouble() != bmoCredit.getAmount().toDouble()) {

					if (bmoCreditType.getType().equals(BmoCreditType.TYPE_DAILY)) {
						// Agregar dias a la ultima fecha de la cxc creada, NO ENTRA LA PRIMERA VEZ, 
						// ya que la fecha ya esta calculada(*) mas arriba, esto solo repite los DIAS a agregar y valida dia cobrable
						datePayout = SFServerUtil.addDays(getSFParams().getDateFormat(), datePayout, daysPayout);
						// Revisar si el dia siguiente es un dia cobrable
						Calendar nextDate = SFServerUtil.stringToCalendar(getSFParams().getDateFormat(), datePayout);
						int nowDayOfWeek = nextDate.get(Calendar.DAY_OF_WEEK);
						
						// Revisar si el dia siguiente es dia cobrable, sino traer el mas proximo
						datePayout = pmCreditType.getLastPaymentDay(datePayout, nowDayOfWeek, bmoCreditType, false);
					} else if (bmoCreditType.getType().equals(BmoCreditType.TYPE_WEEKLY)) {
						datePayout = SFServerUtil.addDays(getSFParams().getDateFormat(), datePayout, daysPayout);
					} 

					bmoRaccount.getReceiveDate().setValue(datePayout);
					bmoRaccount.getDueDate().setValue(datePayout);

					pmRaccount.saveSimple(pmConn, bmoRaccount, bmUpdateResult);
				} else {
					int bankmovementId = 0, raccWithdrawId = 0;
					// Modificar la fecha del Bancos a la fecha del crédito y
					// las CxC Dispersión
					sql = " SELECT * FROM bankmovconcepts " + " WHERE bkmc_foreignid = " + bmoRaccount.getId();
					pmConn3.doFetch(sql);
					if (pmConn3.next()) {
						bankmovementId = pmConn3.getInt("bkmc_bankmovementid");
						raccWithdrawId = pmConn3.getInt("bkmc_foreignid");
					}

					bmoRaccount.getReceiveDate().setValue(datePayout);
					bmoRaccount.getDueDate().setValue(datePayout);

					pmRaccount.saveSimple(pmConn, bmoRaccount, bmUpdateResult);

					// Actualizar el movimiento de banco
					if (bankmovementId > 0) {
						sql = " UPDATE bankmovements SET bkmv_duedate = '" + bmoCredit.getStartDate().toString() + "'"
								+ " WHERE bkmv_bankmovementid = " + bankmovementId;
						pmConn.doUpdate(sql);
					}
					
					// Actualizar la CxC de Abono
					if (raccWithdrawId > 0 ) {
						sql = " UPDATE raccounts SET racc_receivedate = '" + bmoCredit.getStartDate().toString() + "'"
								+ ", racc_duedate = '" + bmoCredit.getStartDate().toString() + "'"
								+ " WHERE racc_raccountid = " + raccWithdrawId;
						pmConn.doUpdate(sql);
					}
					
					// Actualizar el Envio de pedido
					sql = " UPDATE orderdeliveries SET odly_deliverydate = '" + bmoCredit.getStartDate().toString()
							+ "'" + " WHERE odly_orderid = " + bmoCredit.getOrderId().toInteger();
					pmConn.doUpdate(sql);

				}
			}

			if (!bmUpdateResult.hasErrors())
				pmConn.commit();

		} catch (SFException e) {
			pmConn.rollback();
			bmUpdateResult.addMsg("Error al recalcular la fechas en el crédito " + e.toString());
		} finally {
			pmConn.close();
			pmConn2.close();
			pmConn3.close();
		}
	}

	// Genera bitacora de la CxC
	private void addWFlowLog(PmConn pmConn, BmoOrder bmoOrder, BmoRaccount bmoRaccount, String action, BmUpdateResult bmUpdateResult) throws SFException {
		String logComments = "Se " + action + " Cuenta x Cobrar: " + bmoRaccount.getCode().toString() + ", ";
		logComments += "Factura: " + bmoRaccount.getInvoiceno().toString()  + ", ";
		logComments += "Monto: " + SFServerUtil.formatCurrency(bmoRaccount.getAmount().toDouble())  + ", ";
		logComments += "Pagos: " + SFServerUtil.formatCurrency(bmoRaccount.getPayments().toDouble())  + ", ";
		logComments += "Saldo: " + SFServerUtil.formatCurrency(bmoRaccount.getBalance().toDouble())  + ", ";
		logComments += "Estatus: " + bmoRaccount.getStatus().getSelectedOption().getLabel()  + "";

		PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());
		pmWFlowLog.addDataLog(pmConn, bmUpdateResult, bmoOrder.getWFlowId().toInteger(), BmoWFlowLog.TYPE_OTHER, logComments, "");
	}

	// Envia recordatorios de Cuentas x Cobrar
	public void prepareReminders() throws SFException {
		ArrayList<BmFilter> filters = new ArrayList<BmFilter>();

		BmFilter filterByAuthorized = new BmFilter();
		filterByAuthorized.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getStatus(), "" + BmoRaccount.STATUS_AUTHORIZED);
		filters.add(filterByAuthorized);

		BmFilter filterByNotPaid = new BmFilter();
		filterByNotPaid.setValueOperatorFilter(bmoRaccount.getKind(), bmoRaccount.getPaymentStatus(), BmFilter.NOTEQUALS, "" + BmoRaccount.PAYMENTSTATUS_TOTAL);
		filters.add(filterByNotPaid);

		BmFilter filterByDate = new BmFilter();
		filterByDate.setValueOperatorFilter(bmoRaccount.getKind(), bmoRaccount.getRemindDate(), BmFilter.MINOREQUAL, "" + SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
		filters.add(filterByDate);

		BmFilter filterByUser = new BmFilter();
		filterByUser.setValueOperatorFilter(bmoRaccount.getKind(), bmoRaccount.getCollectorUserId(), BmFilter.MAYOR, 0);
		filters.add(filterByUser);

		Iterator<BmObject> iterator = this.list(filters).iterator();

		// Por cada CxC a recordar envía mensaje
		while (iterator.hasNext()) {
			BmoRaccount nextBmoRaccount = (BmoRaccount)iterator.next();

			if (!getSFParams().isProduction()) 
				System.out.println("Por Enviar recordatorio CxC: " + nextBmoRaccount.getCode().toString());

			// Genera pedidos de renovacion
			this.sendReminder(nextBmoRaccount);
		}
	}

	// Envia recordatorio de la cuenta x cobrar
	private void sendReminder(BmoRaccount bmoRaccount) throws SFException {
		// Obtiene el usuario a enviar correo
		PmUser pmUser = new PmUser(getSFParams());
		BmoUser bmoUser = new BmoUser();
		bmoUser = (BmoUser)pmUser.get(bmoRaccount.getCollectorUserId().toInteger());

		// Obtener el pedido
		BmoOrder bmoOrder = new BmoOrder();
		PmOrder pmOrder = new PmOrder(getSFParams());
		if (bmoRaccount.getOrderId().toInteger() > 0)
			bmoOrder = (BmoOrder)pmOrder.get(bmoRaccount.getOrderId().toInteger());

		BmoOrder bmoOrderRenew = new BmoOrder();
		PmOrder pmOrderRenew = new PmOrder(getSFParams());

		//INADICO: Obtener Arrendador y contrato
		PmPropertyRental pmPropertyRentalCXC = new PmPropertyRental(getSFParams());
		BmoPropertyRental bmoPropertyRentalCXC = new BmoPropertyRental();
		BmoCustomer bmoCustomerCXC = new BmoCustomer();

		if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)) {
			//si es in pedido renovado se busca el pedido original para obtener el contrato
			if(bmoOrder.getRenewOrderId().toInteger() > 0) {
				bmoOrderRenew = (BmoOrder)pmOrderRenew.get(bmoOrder.getRenewOrderId().toInteger());				

				bmoPropertyRentalCXC = (BmoPropertyRental)pmPropertyRentalCXC.getBy(bmoOrderRenew.getOriginRenewOrderId().toInteger(), bmoPropertyRentalCXC.getOrderId().getName());
				PmCustomer pmCustomerCXC = new PmCustomer(getSFParams());	
				bmoCustomerCXC = (BmoCustomer)pmCustomerCXC.get(bmoPropertyRentalCXC.getBmoProperty().getCustomerId().toInteger());

			}else {//si no es renovado se asigna el contrato
				bmoPropertyRentalCXC = (BmoPropertyRental)pmPropertyRentalCXC.getBy(bmoRaccount.getOrderId().toInteger(), bmoPropertyRentalCXC.getOrderId().getName());
				PmCustomer pmCustomerCXC = new PmCustomer(getSFParams());			
				bmoCustomerCXC = (BmoCustomer)pmCustomerCXC.get(bmoPropertyRentalCXC.getBmoProperty().getCustomerId().toInteger());
			}

		}
		// Lista de correos del grupo del Tipo de Pedido, que se le va a enviar notificacion
		ArrayList<SFMailAddress> mailList = new ArrayList<SFMailAddress>();
		// Si esta activo el usuario prepara info para enviar por correo
		if (bmoUser.getStatus().toChar() ==  BmoUser.STATUS_ACTIVE) {
			mailList.add(new SFMailAddress(bmoUser.getEmail().toString(), 
					bmoUser.getFirstname().toString() + " " + bmoUser.getFatherlastname().toString()));

			String subject = getSFParams().getAppCode() 
					+ " Recordatorio de " + getSFParams().getProgramTitle(bmoRaccount.getProgramCode()) + ": " 
					+ bmoRaccount.getCode().toString() + ", " 
					+ bmoRaccount.getBmoCustomer().getCode().toString() + " "
					+ bmoRaccount.getBmoCustomer().getDisplayName().toString();

			String msgBody = "" ,msgC ="", msg="";
			//Campos Inadico		
			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)) {
				msgC = 	" <b>Arrendador:</b> " + bmoCustomerCXC.getDisplayName() + "<br>"		
						+ " <b>Contrato:</b> " + bmoPropertyRentalCXC.getCode() + " " + bmoPropertyRentalCXC.getName()
						+ " <br> ";
				if(bmoOrderRenew.getId() > 0) {
					msgC += " <b>Contrato renovado:</b> " + bmoOrder.getCode();
				}
			}		
			msg = " 	<p style=\"font-size:12px\"> "
					+ " <b>CxC:</b> " + bmoRaccount.getCode().toString() + " " + bmoRaccount.getDescription().toString()
					+ " <br> "
					+ msgC
					+ "	<b>Cliente:</b> " + bmoRaccount.getBmoCustomer().getCode().toString() 
					+ " " + bmoRaccount.getBmoCustomer().getDisplayName().toString()
					+ "	<br> "
					+ " <b>Monto:</b> " + SFServerUtil.formatCurrency(bmoRaccount.getAmount().toDouble())
					+ " <br> "
					+ " <b>Pagos:</b> " + SFServerUtil.formatCurrency(bmoRaccount.getPayments().toDouble())
					+ " <br> "
					+ " <b>Saldo:</b> " + SFServerUtil.formatCurrency(bmoRaccount.getBalance().toDouble())
					+ " <br> "
					+ "	<p align=\"center\" style=\"font-size:12px\"> "
					+ "		Favor de dar Seguimiento a la CxC <a target=\"_blank\" href=\""
					+ getSFParams().getAppURL() + "start.jsp?startprogram=" + bmoRaccount.getProgramCode() +"&foreignid=" + bmoRaccount.getId() + "\">Aqu&iacute;</a>. "
					+ "	</p> ";

			msgBody = HtmlUtil.mailBodyFormat(
					getSFParams(), 
					"Recordatorio de " + getSFParams().getProgramTitle(bmoRaccount.getProgramCode()), msg);


			SFSendMail.send(getSFParams(),
					mailList,  
					getSFParams().getBmoSFConfig().getEmail().toString(), 
					getSFParams().getBmoSFConfig().getAppTitle().toString(), 
					subject, 
					msgBody);
		}
	}

	//Inicio de Cron G100 Enviar CxC Autotrizadas y que no esten en pago total 3/14 days
	// Envia recordatorios de Cuentas x Cobrar
	public void prepareReminderOfRaccount() throws SFException {

		ArrayList<BmFilter> filters = new ArrayList<BmFilter>();
		// Filtrar la CxC ppr las autorizadas
		BmFilter filterByAuthorized = new BmFilter();
		filterByAuthorized.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getStatus(), "" + BmoRaccount.STATUS_AUTHORIZED);
		filters.add(filterByAuthorized);

		// Filtrar la CxC que no están em estatus pago total
		BmFilter filterByNotPaid = new BmFilter();
		filterByNotPaid.setValueOperatorFilter(bmoRaccount.getKind(), bmoRaccount.getPaymentStatus(), BmFilter.NOTEQUALS, "" + BmoRaccount.PAYMENTSTATUS_TOTAL);
		filters.add(filterByNotPaid);


		//Filtrar los usuarios que son de cobranza ¿que va a pasar si no existe un ussarios de cobranza?
		BmFilter filterByUser = new BmFilter();
		filterByUser.setValueOperatorFilter(bmoRaccount.getKind(), bmoRaccount.getCollectorUserId(), BmFilter.MAYOR, 0);
		filters.add(filterByUser);


		//		String todayDate = year+"-"+month+"-"+day;
		String todayDate = SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat());
		// Le quito los dias

		Iterator<BmObject> iterator = this.list(filters).iterator();

		// Por cada CxC a recordar envía mensaje
		while (iterator.hasNext()) {

			BmoRaccount nextBmoRaccount = (BmoRaccount)iterator.next();

			String  previousDate = SFServerUtil.addDays(getSFParams().getDateFormat(), 
					nextBmoRaccount.getDueDate().toString(), 
					-((BmoFlexConfig) getSFParams().getBmoAppConfig()).getDayBeforeRemindRaccount().toInteger());
			String  previousDateTwo = SFServerUtil.addDays(getSFParams().getDateFormat(), 
					nextBmoRaccount.getDueDate().toString(), 
					-((BmoFlexConfig) getSFParams().getBmoAppConfig()).getDayBeforeRemindRaccountTwo().toInteger());
			if (previousDate.equals(todayDate)){
				this.sendReminderOfRaccount(nextBmoRaccount,  ((BmoFlexConfig) getSFParams().getBmoAppConfig()).getDayBeforeRemindRaccount().toInteger());
			}else{
				if(previousDateTwo.equals(todayDate)){
					this.sendReminderOfRaccount(nextBmoRaccount, ((BmoFlexConfig) getSFParams().getBmoAppConfig()).getDayBeforeRemindRaccountTwo().toInteger());
				}

			}
		}
	}
	// Envia recordatorio de la cuenta x cobrar
	private void sendReminderOfRaccount(BmoRaccount bmoRaccount, int remaindDays) throws SFException {
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();
		// Obtiene el usuario a enviar correo
		PmUser pmUser = new PmUser(getSFParams());
		BmoUser bmoUser = new BmoUser();
		bmoUser = (BmoUser)pmUser.get(bmoRaccount.getCollectorUserId().toInteger());
		printDevLog(" 0 " +  ((BmoFlexConfig) getSFParams().getBmoAppConfig()).getDayBeforeRemindRaccount().toInteger());
		// obtener cliente
		PmCustomer pmCustomer = new PmCustomer(getSFParams());
		BmoCustomer bmoCustomer = new BmoCustomer();
		bmoCustomer = (BmoCustomer)pmCustomer.get(bmoRaccount.getCustomerId().toInteger());

		String sql = " SELECT (orde_status) as Status, (deve_name) as Desarrollo, (orde_total) as Total, (orde_payments) as Pagos, (orde_balance) as Saldo, (comp_logo) AS logo FROM raccounts "
				+ "  LEFT JOIN  orders on (orde_orderid = racc_orderid) "
				+ " LEFT JOIN orderproperties on (orpy_orderid = orde_orderid) " 
				+ " LEFT JOIN properties on (prty_propertyid = orpy_propertyid )"
				+ " LEFT JOIN developmentblocks on (dvbl_developmentblockid = prty_developmentblockid)" 
				+ " LEFT JOIN developmentphases on(dvph_developmentphaseid = dvbl_developmentphaseid)" 
				+ " LEFT JOIN developments on (deve_developmentid = dvph_developmentid) "
				+ " LEFT JOIN companies on (comp_companyid = deve_companyid) "
				+ " WHERE  racc_raccountid = "+bmoRaccount.getId()+" ;";
		pmConn.doFetch(sql);
		//		String desarrollo="",total = "", pagos = "", saldo = "",
		String status = "",desarrollo="",logo = "";
		while (pmConn.next()){
						if(!(pmConn.getString("Desarrollo").equals(""))){
							desarrollo = pmConn.getString("Desarrollo");
						}
						if(!(pmConn.getString("logo").equals(""))){
							logo = pmConn.getString("logo");
						}
			//			if(!(pmConn.getString("Pagos").equals(""))){
			//				pagos = pmConn.getString("Pagos");
			//			}
			//			if(!(pmConn.getString("Saldo").equals(""))){
			//				saldo = pmConn.getString("Saldo");
			//			}
			if(!(pmConn.getString("Status").equals(""))){
				status = pmConn.getString("Status");
			}
		}
		pmConn.close();
		// Lista de correos del grupo del Tipo de Pedido, que se le va a enviar notificacion
		ArrayList<SFMailAddress> mailList = new ArrayList<SFMailAddress>();
		// Si esta activo el usuario prepara info para enviar por correo
		if (bmoUser.getStatus().toChar() ==  BmoUser.STATUS_ACTIVE) {
			//mandar email a el usuario (cobranza)
			mailList.add(new SFMailAddress(bmoUser.getEmail().toString(), 
					bmoUser.getFirstname().toString() + " " + bmoUser.getFatherlastname().toString()));
			//mandar email a el cliente de la CxC 

			mailList.add(new SFMailAddress(bmoCustomer.getEmail().toString(), 
					bmoCustomer.getFirstname().toString() + " " + bmoCustomer.getFatherlastname().toString()));

			String subject = desarrollo + " - "
					+ " Recordatorio de " + getSFParams().getProgramTitle(bmoRaccount.getProgramCode()) + ": " 
					+ bmoRaccount.getCode().toString() + ", " 
					+ bmoRaccount.getBmoCustomer().getCode().toString() + " "
					+ bmoRaccount.getBmoCustomer().getDisplayName().toString();
			String customer = "";
			if(bmoRaccount.getBmoCustomer().getCustomertype().equals(BmoCustomer.TYPE_COMPANY)){
				customer = bmoRaccount.getBmoCustomer().getLegalname().toString();
			}else{
				customer = bmoRaccount.getBmoCustomer().getFirstname().toString()+" "+bmoRaccount.getBmoCustomer().getFatherlastname().toString();
			}

			String msgBody = "" , msg="";

			msg = "Estimado " + customer + ", esperando te " + 
					"encuentres muy bien, nos ponemos en contacto contigo para recordarte tu próximo pago " + 
					"a vencer dentro de " + remaindDays + " d&iacute;as. Nos ponemos a tus &oacute;rdenes para cualquier duda que tengas " + 
					"en el siguiente tel&eacute;fono: (477) 7247116." + "<br>" +
					"Te agradeceremos mucho nos compartas tu comprobante de pago al siguiente correo: " + 
					"pagoscliente@valenciana.mx" + "<br><br>" +
					"Aprovechamos tambi&eacute;n, para invitarte a que sigas visitando nuestra p&aacute;gina web: " + 
					"www.valenciana.mx y redes sociales: facebook: La Valenciana e Instragam: " + 
					"@valencialeon, donde tendr&aacute;s actualizaciones sobre todas las buenas noticias y avances " + 
					"de nuestro Desarrollo, La Valenciana Arquitectura Residencial, la comunidad mejor " + 
					"planeada del Bajío. " + "<br>" +
					"#inversióninteligente #terrenospremium #túpatrimonioseguro " + "<br><br>" +
					"¡Estamos a tus &oacute;rdenes siempre! ";


			msgBody = FlexUtil.mailBodyFormat(
					getSFParams(), 
					"Recordatorio de " + getSFParams().getProgramTitle(bmoRaccount.getProgramCode()), msg,logo);
			
			if((((BmoFlexConfig) getSFParams().getBmoAppConfig()).getRemaindRaccountInCustomer().toInteger()>0)){
			
				if((status.equals("A") )|| ((status.equals("R")))){
					//				// Quitar emails repetidos
					//				ArrayList<SFMailAddress> mailListNoRepeat = new ArrayList<SFMailAddress>();
					//				for (SFMailAddress event : mailList) {
					//				    boolean isFound = false;
					//				    // Revisar si el email existe en noRepeat
					//				    for (SFMailAddress e : mailListNoRepeat) {
					//				        if (e.getEmail().equals(event.getEmail()) || (e.equals(event))) {
					//				            isFound = true;        
					//				            break;
					//				        }
					//				    }
					//				    // Si no encontro ninguno añadirlo a la nueva lista
					//				    if (!isFound) mailListNoRepeat.add(event);
					//				}

									SFSendMail.send(getSFParams(),
											mailList,  
											getSFParams().getBmoSFConfig().getEmail().toString(), 
											desarrollo,
											subject, 
											msgBody);


				}
			}

		}
	}


	// enviar email en caso de que el pago este atrazado G100
	// CXC Expirada
	// Envia recordatorios de Cuentas x Cobrar
	public void prepareReminderRaccountExpired() throws SFException {

		ArrayList<BmFilter> filters = new ArrayList<BmFilter>();
		// Filtrar la CxC ppr las autorizadas
		BmFilter filterByAuthorized = new BmFilter();
		filterByAuthorized.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getStatus(), "" + BmoRaccount.STATUS_AUTHORIZED);
		filters.add(filterByAuthorized);

		// Filtrar la CxC que no están em estatus pago total
		BmFilter filterByNotPaid = new BmFilter();
		filterByNotPaid.setValueOperatorFilter(bmoRaccount.getKind(), bmoRaccount.getPaymentStatus(), BmFilter.NOTEQUALS, "" + BmoRaccount.PAYMENTSTATUS_TOTAL);
		filters.add(filterByNotPaid);

		//Filtrar las CxC que su fecha de recordar sea menor a la fecha de hoy
		//			BmFilter filterByDate = new BmFilter();
		//			filterByDate.setValueOperatorFilter(bmoRaccount.getKind(), bmoRaccount.getDueDate(), BmFilter.MINOREQUAL, "" + SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
		//			filters.add(filterByDate);
		//			
		//Filtrar los usuarios que son de cobranza ¿que va a pasar si no existe un ussarios de cobranza?
		BmFilter filterByUser = new BmFilter();
		filterByUser.setValueOperatorFilter(bmoRaccount.getKind(), bmoRaccount.getCollectorUserId(), BmFilter.MAYOR, 0);
		filters.add(filterByUser);


		Iterator<BmObject> iterator = this.list(filters).iterator();

		// Por cada CxC a recordar envía mensaje
		while (iterator.hasNext()) {

			BmoRaccount nextBmoRaccount = (BmoRaccount)iterator.next();
			Date nowdat = SFServerUtil.stringToDate(getSFParams().getDateFormat(),
					SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
			Date dueDate = SFServerUtil.stringToDate(getSFParams().getDateFormat(), nextBmoRaccount.getDueDate().toString());	

			if (!nowdat.before(dueDate)) {

				this.sendReminderRaccountExpired(nextBmoRaccount);
			}
		}
	}

	// Envia recordatorio de la cuenta x cobrar
	private void sendReminderRaccountExpired(BmoRaccount bmoRaccount) throws SFException {

		// Obtiene el usuario a enviar correo
		PmUser pmUser = new PmUser(getSFParams());
		BmoUser bmoUser = new BmoUser();
		bmoUser = (BmoUser)pmUser.get(bmoRaccount.getCollectorUserId().toInteger());
		// obtener cliente
		PmCustomer pmCustomer = new PmCustomer(getSFParams());
		BmoCustomer bmoCustomer = new BmoCustomer();
		bmoCustomer = (BmoCustomer)pmCustomer.get(bmoRaccount.getCustomerId().toInteger());

		// Lista de correos del grupo del Tipo de Pedido, que se le va a enviar notificacion
		ArrayList<SFMailAddress> mailList = new ArrayList<SFMailAddress>();
		// Si esta activo el usuario prepara info para enviar por correo
		if (bmoUser.getStatus().toChar() ==  BmoUser.STATUS_ACTIVE) {
			//mandar email a el usuario (cobranza)
			mailList.add(new SFMailAddress(bmoUser.getEmail().toString(), 
					bmoUser.getFirstname().toString() + " " + bmoUser.getFatherlastname().toString()));
			//mandar email a el cliente de la CxC 

			mailList.add(new SFMailAddress(bmoCustomer.getEmail().toString(), 
					bmoCustomer.getFirstname().toString() + " " + bmoCustomer.getFatherlastname().toString()));

			

			String customer = "";
			if(bmoRaccount.getBmoCustomer().getCustomertype().equals(BmoCustomer.TYPE_COMPANY)){
				customer = bmoRaccount.getBmoCustomer().getLegalname().toString();
			}else{
				customer = bmoRaccount.getBmoCustomer().getFirstname().toString()+" "+bmoRaccount.getBmoCustomer().getFatherlastname().toString();
			}
			PmConn pmConn = new PmConn(getSFParams());
			pmConn.open();
			String sql = " SELECT (orde_status) as Status, (deve_name) as Desarrollo, (orde_total) as Total, (orde_payments) as Pagos, (orde_balance) as Saldo, (comp_logo) AS logo FROM raccounts "
					+ "  LEFT JOIN  orders on (orde_orderid = racc_orderid) "
					+ " LEFT JOIN orderproperties on (orpy_orderid = orde_orderid) " 
					+ " LEFT JOIN properties on (prty_propertyid = orpy_propertyid )"
					+ " LEFT JOIN developmentblocks on (dvbl_developmentblockid = prty_developmentblockid)" 
					+ " LEFT JOIN developmentphases on(dvph_developmentphaseid = dvbl_developmentphaseid)" 
					+ " LEFT JOIN developments on (deve_developmentid = dvph_developmentid) "
					+ " LEFT JOIN companies on (comp_companyid = deve_companyid) "
					+ " WHERE  racc_raccountid = "+bmoRaccount.getId()+" ;";

			pmConn.doFetch(sql);
			String  status = "",desarrollo= "",logo = "";
			while (pmConn.next()){				
				if(!(pmConn.getString("Desarrollo").equals(""))){
					desarrollo = pmConn.getString("Desarrollo");
				}
				if(!(pmConn.getString("Status").equals(""))){
					status = pmConn.getString("Status");
				}
				if(!(pmConn.getString("logo").equals(""))){
					logo = pmConn.getString("logo");
				}
			}
			
			String subject = desarrollo + " - "
					+ " Recordatorio de " + getSFParams().getProgramTitle(bmoRaccount.getProgramCode()) + ": " 
					+ bmoRaccount.getCode().toString() + ", " 
					+ bmoRaccount.getBmoCustomer().getCode().toString() + " "
					+ bmoRaccount.getBmoCustomer().getDisplayName().toString();
			pmConn.close();
			String msgBody = "" , msg="";


			msg = "Estimado "+customer+", esperando te encuentres muy bien, " + 
					"te compartimos que en nuestros registros a&uacute;n no se ve reflejado tu pago por la cantidad " + 
					"de "+SFServerUtil.formatCurrency(bmoRaccount.getBalance().toDouble())+" que venció el pasado " + bmoRaccount.getDueDate() + ". Te agradeceremos nos puedas enviar " + 
					"tu comprobante al siguiente correo: pagoscliente@valenciana.mx " + "<br><br>"+
					"Si tienes alguna duda y/o comentario, te pedimos de favor nos contactes al teléfono " + 
					"(477) 7247116 o al siguiente correo: pagoscliente@valenciana.mx " + "<br><br>" +
					"¡Estamos a tus órdenes siempre!";


			msgBody = FlexUtil.mailBodyFormat(
					getSFParams(), 
					"Recordatorio de " + getSFParams().getProgramTitle(bmoRaccount.getProgramCode()), msg,logo);
			if((((BmoFlexConfig) getSFParams().getBmoAppConfig()).getRemaindRaccountInCustomer().toInteger()>0)){
				if((status.equals(""+BmoRaccount.STATUS_AUTHORIZED) )|| ((status.equals(""+BmoRaccount.STATUS_REVISION)))){
					//					// Quitar emails repetidos
					//					ArrayList<SFMailAddress> mailListNoRepeat = new ArrayList<SFMailAddress>();
					//					for (SFMailAddress event : mailList) {
					//					    boolean isFound = false;
					//					    // Revisar si el email existe en noRepeat
					//					    for (SFMailAddress e : mailListNoRepeat) {
					//					        if (e.getEmail().equals(event.getEmail()) || (e.equals(event))) {
					//					            isFound = true;        
					//					            break;
					//					        }
					//					    }
					//					    // Si no encontro ninguno añadirlo a la nueva lista
					//					    if (!isFound) mailListNoRepeat.add(event);
					//					}

					SFSendMail.send(getSFParams(),
							mailList,  
							getSFParams().getBmoSFConfig().getEmail().toString(), 
							desarrollo,
							subject, 
							msgBody);
				}
			}

		}
	}





	// Revisa si hay pagos
	public boolean failureIsPaid(PmConn pmConn, int orderId, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		// Revisar que la penalizacion este cubierta
		boolean failure = true;
		// Revisar que la penalización este pagada
		sql = " SELECT * FROM raccounts " + " WHERE racc_orderid = " + orderId + " AND racc_failure = 1 "
				+ " AND racc_paymentstatus <> '" + BmoRaccount.PAYMENTSTATUS_TOTAL + "'";
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			failure = false;
		}

		return failure;
	}

	// Obtener saldo de cxc pendientes de cobro y sin check de externas por estatus de detalle del pedido
	public double getSumCCPendingWithoutLinked(String status, String customerId, String leaderUserId, String assignedUserId, String areaId, String currencyId, String startDateOrdt, String endDateOrdt, BmUpdateResult bmUpdateResult) throws SFException {
		PmConn pmConn = null;
		double amountPending = 0;

		try {
			pmConn = new PmConn(getSFParams());
			pmConn.open();

			BmoCurrency bmoCurrencyDestiny = new BmoCurrency();
			PmCurrency pmCurrency = new PmCurrency(getSFParams());
			bmoCurrencyDestiny = (BmoCurrency)pmCurrency.get(Integer.parseInt(currencyId));
			String sql = " SELECT racc_amount, racc_currencyid, racc_currencyparity FROM " + formatKind("raccounts") + 
					" LEFT JOIN " + formatKind("orders") + " ON (orde_orderid = racc_orderid) " +
					" LEFT JOIN " + formatKind("ordertypes") + " ON (ortp_ordertypeid = orde_ordertypeid) " +
					//					" LEFT JOIN " + formatKind("orderdetails") + " ON (ordt_orderid = orde_orderid) " +
					" LEFT JOIN " + formatKind("consultancies") + " ON (cons_orderid = orde_orderid) " +

					" WHERE cons_statusscrum = '" + status + "' ";
			if (Integer.parseInt(customerId) > 0) 
				sql += " AND racc_customerid = " + customerId;
			if (Integer.parseInt(leaderUserId) > 0) 
				sql += " AND cons_leaderuserid = " + leaderUserId;
			if (Integer.parseInt(assignedUserId) > 0) 
				sql += " AND cons_assigneduserid = " + assignedUserId;
			if (Integer.parseInt(areaId) > 0) 
				sql += " AND cons_areaidscrum = " + areaId;
			if (!startDateOrdt.equals("") && !startDateOrdt.equals("0")) 
				sql += " AND cons_startdatescrum >= '" + startDateOrdt + " 00:00:00' ";
			if (!endDateOrdt.equals("") && !endDateOrdt.equals("0")) 
				sql += " AND cons_startdatescrum <= '" + endDateOrdt + " 23:59:59' ";
			if (getSFParams().getSelectedCompanyId() > 0) 
				sql += " AND racc_companyid = " + getSFParams().getSelectedCompanyId() ;

			sql +=  " AND ortp_filteronscrum = 1 " +
					" AND racc_paymentstatus = '" + BmoRaccount.PAYMENTSTATUS_PENDING + "' " +
					" AND (racc_linked = 0 OR racc_linked IS NULL ) ";

			printDevLog("sql_getSumCCPendingWithoutLinked: "+sql);
			pmConn.doFetch(sql);
			while (pmConn.next()) {
				// Hacer conversion a la moneda del filtro
				double amount = pmConn.getDouble("racc_amount");
				amount = pmCurrency.currencyExchange(amount, pmConn.getInt("racc_currencyid"), pmConn.getDouble("racc_currencyparity"), 
						bmoCurrencyDestiny.getId(), bmoCurrencyDestiny.getParity().toDouble());
				amountPending += amount;
			}

		} catch (SFPmException e) {
			throw new SFException(this.getClass().getName() + "getSumCCPendingWithoutLinked(): " + e.toString());
		} finally {
			pmConn.close();
		}
		return amountPending;
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoRaccount = (BmoRaccount) bmObject;

		// Validaciones
		if (bmoRaccount.getStatus().toChar() == BmoRaccount.STATUS_AUTHORIZED) {
			bmUpdateResult.addMsg("La CxC no se puede Eliminar: está Autorizada.");
		} else {

			// Eliminar las relaciones
			deleteLinked(pmConn, bmoRaccount, bmUpdateResult);

			// Eliminar las aplicaciones
			deleteAssignment(pmConn, bmoRaccount, bmUpdateResult);

			// Eliminar los items
			deleteItems(pmConn, bmoRaccount.getId(), bmUpdateResult);

			// Elimina la CxP
			super.delete(pmConn, bmObject, bmUpdateResult);

			// Actualiza Recibos de Ordenes de Compra si existia liga
			if (bmoRaccount.getOrderId().toInteger() > 0) {
				PmOrder pmOrder = new PmOrder(getSFParams());
				BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoRaccount.getOrderId().toInteger());

				// Asegura saldo de las cxc
				if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnsureProcessCxC().toBoolean()) {
					if (!bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {
						if (!bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)) {
							// Si el pedido esta autorizado generar cxc en automatico O si esta activo en el tipo de pedido y no esta cancelado
							if (bmoOrder.getStatus().equals(BmoOrder.STATUS_AUTHORIZED)
									|| (bmoOrder.getBmoOrderType().getAtmCCRevision().toInteger() > 0
											&& !bmoOrder.getStatus().equals(BmoOrder.STATUS_CANCELLED))) {
								ensureOrderBalance(pmConn, bmoOrder, bmUpdateResult);
							}
						}
					} else if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {

						PmCredit pmCredit = new PmCredit(getSFParams());
						BmoCredit bmoCredit = new BmoCredit();
						bmoCredit = (BmoCredit) pmCredit.getBy(pmConn, bmoOrder.getId(),
								bmoCredit.getOrderId().getName());

						// Si el monto de la Cxc es igual al del credito,
						// eliminar la entrega de dinero
						if (bmoRaccount.getTotal().toDouble() == bmoCredit.getAmount().toDouble()) {
							// Deshautorizar el Envio de Pedido
							BmoOrderDelivery bmoOrderDelivery = new BmoOrderDelivery();
							PmOrderDelivery pmOrderDelivery = new PmOrderDelivery(getSFParams());
							bmoOrderDelivery = (BmoOrderDelivery) pmOrderDelivery.getBy(pmConn, bmoOrder.getId(),
									bmoOrderDelivery.getOrderId().getName());
							bmoOrderDelivery.getStatus().setValue(BmoOrderDelivery.STATUS_REVISION);

							pmOrderDelivery.save(pmConn, bmoOrderDelivery, bmUpdateResult);

							pmOrderDelivery.delete(pmConn, bmoOrderDelivery, bmUpdateResult);
						}

						// Cambiar el estatus del credito a normal si se elimina
						// la penalización
						if (bmoRaccount.getFailure().toBoolean()) {
							bmoCredit.getPaymentStatus().setValue(BmoCredit.PAYMENTSTATUS_NORMAL);
							super.save(pmConn, bmoCredit, bmUpdateResult);
						}
					} 
				}

				// Actualiza estatus de pago en contratos de arrendamiento dependiendo de la cxc vigente
				if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)) {
					updatePaymentStatusPropertyRental(pmConn, bmoOrder, bmUpdateResult);
				}

				// Actualiza saldo de provision
				pmOrder.updatePayments(pmConn, bmoOrder, bmUpdateResult);
				pmOrder.updateDeliveryStatus(pmConn, bmoOrder, bmUpdateResult);

				// Genera bitacora
				addWFlowLog(pmConn, bmoOrder, bmoRaccount, "Eliminó", bmUpdateResult);
			}
		}
		return bmUpdateResult;
	}

	// Borra sin cambiar estatus
	private BmUpdateResult deleteSimple(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult)
			throws SFException {
		bmoRaccount = (BmoRaccount) bmObject;

		PmRaccountType pmRaccountType = new PmRaccountType(getSFParams());
		BmoRaccountType bmoRaccountType = (BmoRaccountType) pmRaccountType.get(pmConn,
				bmoRaccount.getRaccountTypeId().toInteger());

		// Validaciones
		if (bmoRaccount.getStatus().toChar() == BmoRaccount.STATUS_AUTHORIZED) {
			bmUpdateResult.addMsg("La CxC no se puede Eliminar: está Autorizada.");
		} else {

			// Eliminar las relaciones
			deleteLinked(pmConn, bmoRaccount, bmUpdateResult);

			if (bmoRaccountType.getCategory().equals(BmoRaccountType.CATEGORY_CREDITNOTE)) {
				// Eliminar las aplicaciones
				deleteAssignment(pmConn, bmoRaccount, bmUpdateResult);

			} else {
				deleteAssignment(pmConn, bmoRaccount.getId(), bmUpdateResult);
			}

			// Eliminar los items
			deleteItems(pmConn, bmoRaccount.getId(), bmUpdateResult);

			// Elimina la CxP
			super.delete(pmConn, bmObject, bmUpdateResult);
		}
		return bmUpdateResult;
	}

	// Eliminar CXC del Pedido Cancelado
	public void deleteRaccount(PmConn pmConn, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult) throws SFException {
		PmRaccount pmRaccount = new PmRaccount(getSFParams());
		BmoRaccount bmoRaccount = new BmoRaccount();		
		String sql = "";

		if (!bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SESSION)) { 
			sql = " SELECT * FROM raccounts " + " WHERE racc_status = '" + BmoRaccount.STATUS_REVISION + "'"
					+ " AND racc_paymentstatus = '" + BmoRaccount.PAYMENTSTATUS_PENDING + "'" + " AND racc_orderid = "
					+ bmoOrder.getId();
		} else {
			//Eliminar las CxC que no esten pagadas
			sql = " SELECT * FROM raccounts " + 
					" WHERE racc_status = '" + BmoRaccount.STATUS_REVISION+ "'" + 
					" AND racc_orderid = " + bmoOrder.getId();
		}

		pmConn.doFetch(sql);
		while (pmConn.next()) {
			bmoRaccount = (BmoRaccount) pmRaccount.get(pmConn, pmConn.getInt("racc_raccountid"));
			//			bmoRaccount.getStatus().setValue(BmoRaccount.STATUS_REVISION);
			//			super.save(pmConn, bmoRaccount, bmUpdateResult);

			pmRaccount.deleteSimple(pmConn, bmoRaccount, bmUpdateResult);
		}
	}

	// Delete assignments
	private void deleteLinked(PmConn pmConn, BmoRaccount bmoRaccount, BmUpdateResult bmUpdateResult) throws SFException {
		BmoRaccount bmoForeign = new BmoRaccount();
		BmoRaccountLink bmoRaccountLink = new BmoRaccountLink();
		PmRaccountLink pmRaccountLink = new PmRaccountLink(getSFParams());
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoRaccountLink.getKind(), bmoRaccountLink.getRaccountId().getName(),
				bmoRaccount.getId());
		Iterator<BmObject> listRaccLinks = pmRaccountLink.list(pmConn, bmFilter).iterator();
		while (listRaccLinks.hasNext()) {
			bmoRaccountLink = (BmoRaccountLink) listRaccLinks.next();
			//Quitar el marcado de la CxC
			bmoForeign = (BmoRaccount)this.get(pmConn, bmoRaccountLink.getForeignId().toInteger());
			bmoForeign.getLinked().setValue(0);			
			super.save(pmConn, bmoForeign, bmUpdateResult);

			pmRaccountLink.delete(pmConn, bmoRaccountLink, bmUpdateResult);
		}
	}

	// Delete assignments
	private void deleteAssignment(PmConn pmConn, int raccountId, BmUpdateResult bmUpdateResult) throws SFException {
		pmConn.doUpdate("DELETE FROM raccountassignments WHERE rass_raccountid = " + raccountId);
	}

	// Delete assignments
	private void deleteAssignment(PmConn pmConn, BmoRaccount bmoRaccount, BmUpdateResult bmUpdateResult)
			throws SFException {
		BmoRaccountAssignment bmoRaccountAssignment = new BmoRaccountAssignment();
		PmRaccountAssignment pmRaccountAssignment = new PmRaccountAssignment(getSFParams());
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoRaccountAssignment.getKind(), bmoRaccountAssignment.getRaccountId().getName(),
				bmoRaccount.getId());
		Iterator<BmObject> listRaccAssig = pmRaccountAssignment.list(pmConn, bmFilter).iterator();
		while (listRaccAssig.hasNext()) {
			bmoRaccountAssignment = (BmoRaccountAssignment) listRaccAssig.next();
			pmRaccountAssignment.delete(pmConn, bmoRaccountAssignment, bmUpdateResult);
		}
	}
	private boolean getFiles(int programId, int foreignid) throws SFPmException {
		boolean result;
		String sql = "";
		PmConn pmConn = new PmConn(getSFParams());

		sql = "SELECT * FROM sffiles where file_programid = " + programId + " AND file_foreignid = " + foreignid;

		pmConn.open();
		pmConn.doFetch(sql);

		if (pmConn.next()) {
			result = true;
			pmConn.close();
		}else {
			result = false;
			pmConn.close();
		}

		return result;

	}

	// delete items
	private void deleteItems(PmConn pmConn, int raccountId, BmUpdateResult bmUpdateResult) throws SFException {
		pmConn.doUpdate("DELETE FROM raccountitems WHERE rait_raccountid = " + raccountId);
	}

	// Actualizar estatus de pago del contrato de acuerdo al la CC vigente
	public void updatePaymentStatusPropertyRental(PmConn pmConn, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult) throws SFException {
		boolean beatens = false;
		BmoPropertyRental bmoPropertyRental = new BmoPropertyRental();
		PmPropertyRental pmPropertyRental = new PmPropertyRental(getSFParams());
		bmoPropertyRental = (BmoPropertyRental)pmPropertyRental.getBy(bmoOrder.getOriginRenewOrderId().toInteger(), bmoPropertyRental.getOrderId().getName());


		//revizar si hay cxc pendientes
		pmConn.doFetch("SELECT racc_raccountid FROM raccounts WHERE " +
				" (racc_orderid = " + bmoOrder.getId() + " AND racc_duedate <= NOW() AND racc_paymentstatus <> '"
				+ BmoRaccount.PAYMENTSTATUS_TOTAL + "')");

		if(pmConn.next()) {
			beatens = true;
		}
		//si hay CXC pendientes agrega semaforo rojo
		if(beatens) {
			bmoPropertyRental.getPaymentStatus().setValue(BmoPropertyRental.PAYMENTSTATUS_BEATEN);
		}else {
			pmConn.doFetch("SELECT racc_paymentstatus,racc_duedate FROM raccounts "
					+ " WHERE racc_orderid = " + bmoOrder.getId() 
					+ " AND MONTH(racc_duedate) >= MONTH(NOW()) "
					+ " AND MONTH(racc_duedate) <= MONTH(NOW()); ");
			// Por defecto
			bmoPropertyRental.getPaymentStatus().setValue(BmoPropertyRental.PAYMENTSTATUS_TOTAL);


			while (pmConn.next()) {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getSFParams().getDateFormat());
				Date startDate = new Date();
				try {
					startDate = simpleDateFormat.parse(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
				} catch (ParseException e1) {

				}
				Date enddate = new Date();
				try {
					enddate = simpleDateFormat.parse(pmConn.getString("racc_duedate"));
				} catch (ParseException e) {

				}
				int days =(int)((enddate.getTime()-startDate.getTime())/86400000);		


				if (pmConn.getString("racc_paymentstatus").equals("" + BmoRaccount.PAYMENTSTATUS_PENDING) ) {
					if(days > 0) {
						bmoPropertyRental.getPaymentStatus().setValue(BmoPropertyRental.PAYMENTSTATUS_PENDING);
					}else if(days <= 0) {
						bmoPropertyRental.getPaymentStatus().setValue(BmoPropertyRental.PAYMENTSTATUS_BEATEN);
					}

					break;
				} else {
					bmoPropertyRental.getPaymentStatus().setValue(BmoPropertyRental.PAYMENTSTATUS_TOTAL);
				}
			}
		}
		pmPropertyRental.saveSimple(pmConn, bmoPropertyRental, bmUpdateResult);
	}

	public void updatePayment() throws SFException {

		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();
		PmConn pmConn2 = new PmConn(getSFParams());
		pmConn2.open();

		BmoOrder bmoOrder = new BmoOrder();
		PmOrder pmOrder = new PmOrder(getSFParams());
		String sql = "SELECT " + bmoOrder.getIdFieldName() + " FROM " + bmoOrder.getKind() + " WHERE "
				+  " orde_status "
				+ " = '" + BmoOrder.STATUS_AUTHORIZED + "'";


		pmConn.doFetch(sql);

		while(pmConn.next()) {
			bmoOrder = (BmoOrder)pmOrder.get(pmConn.getInt( bmoOrder.getIdFieldName()));
			this.updatePaymentStatusPropertyRental(pmConn2, bmoOrder, new BmUpdateResult());
		}
		pmConn.close();
		pmConn2.close();
	}
}
