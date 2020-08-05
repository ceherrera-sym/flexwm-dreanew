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
import java.util.Date;
import java.util.Iterator;
import com.flexwm.server.FlexUtil;
import com.flexwm.server.PmCompanyCollectionProfile;
import com.flexwm.server.PmCompanyNomenclature;
import com.flexwm.server.PmFlexConfig;
import com.flexwm.server.cm.PmCustomer;
import com.flexwm.server.op.PmRequisition;
import com.flexwm.server.op.PmSupplier;
import com.flexwm.shared.BmoCompanyCollectionProfile;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.fi.BmoBankAccount;
import com.flexwm.shared.fi.BmoBankMovConcept;
import com.flexwm.shared.fi.BmoBankMovType;
import com.flexwm.shared.fi.BmoBankMovement;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.fi.BmoLoanDisbursement;
import com.flexwm.shared.fi.BmoPaccount;
import com.flexwm.shared.fi.BmoPaccountType;
import com.flexwm.shared.op.BmoRequisition;
import com.flexwm.shared.op.BmoSupplier;
import com.symgae.server.HtmlUtil;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.SFSendMail;
import com.symgae.server.SFServerUtil;
import com.symgae.server.sf.PmUser;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;

import com.symgae.shared.sf.BmoUser;


public class PmBankMovement extends PmObject {
	BmoBankMovement bmoBankMovement;
	BmoBankAccount bmoBankAccount = new BmoBankAccount();

	public PmBankMovement(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoBankMovement = new BmoBankMovement();
		setBmObject(bmoBankMovement);

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoBankMovement.getBankAccountId(), bmoBankMovement.getBmoBankAccount()),
				new PmJoin(bmoBankMovement.getBmoBankAccount().getCompanyId(), bmoBankMovement.getBmoBankAccount().getBmoCompany()),
				new PmJoin(bmoBankMovement.getBmoBankAccount().getCurrencyId(),	bmoBankMovement.getBmoBankAccount().getBmoCurrency()),						
				new PmJoin(bmoBankMovement.getBankMovTypeId(), bmoBankMovement.getBmoBankMovType()),
				new PmJoin(bmoBankMovement.getBmoBankAccount().getBankAccountTypeId(),bmoBankMovement.getBmoBankAccount().getBmoBankAccountType()),
				new PmJoin(bmoBankMovement.getBmoBankMovType().getPaccountTypeId(), bmoBankMovement.getBmoBankMovType().getBmoPaccountType()),
				new PmJoin(bmoBankMovement.getBmoBankMovType().getRaccountTypeId(), bmoBankMovement.getBmoBankMovType().getBmoRaccountType()),
				new PmJoin(bmoBankMovement.getSupplierId(), bmoBankMovement.getBmoSupplier()),
				new PmJoin(bmoBankMovement.getBmoSupplier().getSupplierCategoryId(), bmoBankMovement.getBmoSupplier().getBmoSupplierCategory()),
				new PmJoin(bmoBankMovement.getCustomerId(), bmoBankMovement.getBmoCustomer()),
				new PmJoin(bmoBankMovement.getBmoCustomer().getSalesmanId(), bmoBankMovement.getBmoCustomer().getBmoUser()),
				new PmJoin(bmoBankMovement.getBmoCustomer().getBmoUser().getAreaId(), bmoBankMovement.getBmoCustomer().getBmoUser().getBmoArea()),
				new PmJoin(bmoBankMovement.getBmoCustomer().getBmoUser().getLocationId(), bmoBankMovement.getBmoCustomer().getBmoUser().getBmoLocation()),
				new PmJoin(bmoBankMovement.getBmoCustomer().getTerritoryId(), bmoBankMovement.getBmoCustomer().getBmoTerritory()),
				new PmJoin(bmoBankMovement.getBmoCustomer().getReqPayTypeId(), bmoBankMovement.getBmoCustomer().getBmoReqPayType())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoBankMovement bmoBankMovement = (BmoBankMovement) autoPopulate(pmConn, new BmoBankMovement());

		// BmoBankMovType
		BmoBankMovType bmoBankMovType = new BmoBankMovType();
		if (pmConn.getInt(bmoBankMovType.getIdFieldName()) > 0) 
			bmoBankMovement.setBmoBankMovType((BmoBankMovType) new PmBankMovType(getSFParams()).populate(pmConn));
		else
			bmoBankMovement.setBmoBankMovType(bmoBankMovType);

		// Supplier
		BmoSupplier bmoSupplier = new BmoSupplier();
		if (pmConn.getInt(bmoSupplier.getIdFieldName()) > 0)
			bmoBankMovement.setBmoSupplier((BmoSupplier) new PmSupplier(getSFParams()).populate(pmConn));
		else
			bmoBankMovement.setBmoSupplier(bmoSupplier);

		// Customer
		BmoCustomer bmoCustomer = new BmoCustomer();
		if (pmConn.getInt(bmoCustomer.getIdFieldName()) > 0)
			bmoBankMovement.setBmoCustomer((BmoCustomer) new PmCustomer(getSFParams()).populate(pmConn));
		else
			bmoBankMovement.setBmoCustomer(bmoCustomer);

		// BankAccount
		BmoBankAccount bmoBankAccount = new BmoBankAccount();
		if (pmConn.getInt(bmoBankAccount.getIdFieldName()) > 0)
			bmoBankMovement.setBmoBankAccount((BmoBankAccount) new PmBankAccount(getSFParams()).populate(pmConn));
		else
			bmoBankMovement.setBmoBankAccount(bmoBankAccount);

		// BmoPaccountType
		BmoPaccountType bmoPaccountType = new BmoPaccountType();
		if (pmConn.getInt(bmoPaccountType.getIdFieldName()) > 0)
			bmoBankMovType.setBmoPaccountType((BmoPaccountType) new PmPaccountType(getSFParams()).populate(pmConn));
		else
			bmoBankMovType.setBmoPaccountType(bmoPaccountType);

		return bmoBankMovement;
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();
		int loggedUserAreaId =  getSFParams().getLoginInfo().getBmoUser().getAreaId().toInteger();

		// Filtro por asginacion de CUENTAS DE BANCO
		if (getSFParams().restrictData(bmoBankAccount.getProgramCode())) {

			// Responsable de Cuenta de Banco
			filters = "( bkac_responsibleid IN (" +
					" SELECT user_userid FROM users " +
					" WHERE " + 
					" user_userid = " + loggedUserId +
					" OR user_userid IN ( " +
					" SELECT u2.user_userid FROM users u1 " +
					" LEFT JOIN users u2 on (u2.user_parentid = u1.user_userid) " +
					" WHERE u1.user_userid = " + loggedUserId +
					" ) " +
					" OR user_userid IN ( " +
					" SELECT u3.user_userid FROM users u1 " +
					" LEFT JOIN users u2 on (u2.user_parentid = u1.user_userid) " +
					" LEFT JOIN users u3 on (u3.user_parentid = u2.user_userid) " +
					" WHERE u1.user_userid = " + loggedUserId +
					" ) " +
					" OR user_userid IN ( " +
					" SELECT u4.user_userid FROM users u1 " +
					" LEFT JOIN users u2 on (u2.user_parentid = u1.user_userid) " +
					" LEFT JOIN users u3 on (u3.user_parentid = u2.user_userid) " +
					" LEFT JOIN users u4 on (u4.user_parentid = u3.user_userid) " +
					" WHERE u1.user_userid = " + loggedUserId +
					" ) " +
					" OR user_userid IN ( " +
					" SELECT u5.user_userid FROM users u1 " +
					" LEFT JOIN users u2 on (u2.user_parentid = u1.user_userid) " +
					" LEFT JOIN users u3 on (u3.user_parentid = u2.user_userid) " +
					" LEFT JOIN users u4 on (u4.user_parentid = u3.user_userid) " +
					" LEFT JOIN users u5 on (u5.user_parentid = u4.user_userid) " +
					" WHERE u1.user_userid = " + loggedUserId +
					" ) " + 

					// Usuario que lo creo
					" OR ( bkmv_usercreateid IN " +
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

					// De Anticipo Prov.
//					" OR  ( " +
//					" bkmv_requisitionid IN (" +
//					" SELECT reqi_requisitionid " +
//					" FROM requisitions" +
//					" WHERE reqi_usercreateid = " + loggedUserId +
//					" OR reqi_requestedby =  " + loggedUserId +
//					" OR reqi_areaid = " + loggedUserAreaId + 
//					" ) " +
//					" ) " +
					
					// De una OC(MB ant. prov.) donde se encuentre el solicitado/creado/departamento del usuario
					" OR  ( " +
					" bkmv_bankmovementid IN (" +
					" SELECT bkmc_bankmovementid FROM bankmovconcepts " +					
					" LEFT JOIN bankmovements ON (bkmv_bankmovementid = bkmc_bankmovementid) " +
					" LEFT JOIN requisitions ON (reqi_requisitionid = bkmc_requisitionid) " +
					" WHERE bkmc_paccountid IS NULL " +
					" AND (reqi_usercreateid = " + loggedUserId +
					" OR reqi_requestedby =  " + loggedUserId +
					" OR reqi_areaid = " + loggedUserAreaId + " )" +
					" ) " +
					" ) " +

					// De una CxP que tiene OC y donde se encuentre el solicitado/creado/departamento del usuario
					" OR  ( " +
					" bkmv_bankmovementid IN (" +
					" SELECT bkmc_bankmovementid FROM bankmovconcepts " +					
					" LEFT JOIN bankmovements ON (bkmv_bankmovementid = bkmc_bankmovementid) " +
					" LEFT JOIN paccounts ON (pacc_paccountid = bkmc_paccountid) " +
					" LEFT JOIN requisitions ON (reqi_requisitionid = pacc_requisitionid) " +
					" WHERE reqi_usercreateid = " + loggedUserId +
					" OR reqi_requestedby =  " + loggedUserId +
					" OR reqi_areaid = " + loggedUserAreaId + 
					" ) " +
					" ) " +

					" ) " +
					" ) ";
		}

		// Filtro de MB de las cuentas de banco de empresas del usuario
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			if (filters.length() > 0) filters += " AND ";
			filters += " ( bkac_companyid IN ( " +
					" SELECT uscp_companyid FROM usercompanies " +
					" WHERE " + 
					" uscp_userid = " + loggedUserId + " )"
					+ " ) ";			
		}

		// Filtro de empresa seleccionada
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0) filters += " AND ";
			filters += " bkac_companyid = " + getSFParams().getSelectedCompanyId();
		}

		return filters;
	}	

	public void validateBankMovement(BmoBankMovement bmoBankMovement, BmoBankMovType bmoBankMovType, BmoBankAccount bmoBankAccount, BmUpdateResult bmUpdateResult) throws SFException {
		
		bmoBankMovement.setBmoBankMovType(bmoBankMovType);

		if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_CXP)) {
			if (!(bmoBankMovement.getSupplierId().toInteger() > 0)) {
				bmUpdateResult.addError(bmoBankMovement.getSupplierId().getName(), "Debe seleccionar un Proveedor o un Vendedor.");
			}
		} else if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)
				&& bmoBankMovement.getBmoBankMovType().getType().equals(BmoBankMovType.TYPE_WITHDRAW)) {
//			if (!(bmoBankMovement.getRequisitionId().toInteger() > 0))
//				bmUpdateResult.addError(bmoBankMovement.getRequisitionId().getName(), "Debe seleccionar una Órden de Compra.");
		} else if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_CXC)) {
			if (!(bmoBankMovement.getCustomerId().toInteger() > 0)) {
				bmUpdateResult.addMsg("Debe seleccionar un Cliente.");
			}
		} else if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_TRANSFER)) {
			if (bmoBankMovement.getBmoBankMovType().getType().equals(BmoBankMovType.TYPE_WITHDRAW)) {
				if (!(bmoBankMovement.getBankAccoTransId().toInteger() > 0)) {
					bmUpdateResult.addError(bmoBankMovement.getBankAccoTransId().getName(), "Debe seleccionar la Cuenta Destino.");
				} else if (!(bmoBankMovement.getBankAccountId().toInteger() > 0)) {
					bmUpdateResult.addError(bmoBankMovement.getBankAccountId().getName(), "Debe seleccionar la cuenta origen.");
				}
				if (bmoBankMovement.getBankAccountId().toInteger() == bmoBankMovement.getBankAccoTransId().toInteger()) {
					bmUpdateResult.addError(bmoBankMovement.getBankAccoTransId().getName(), "La cuenta destino debe ser distinta al origen.");
				}
				if (!(bmoBankMovement.getWithdraw().toDouble() > 0)
						&& !(bmoBankMovement.getId() > 0)) {
					bmUpdateResult.addError(bmoBankMovement.getWithdraw().getName(), "La cantidad no puede ser 0.");
				}
			}
		} else if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_DEPOSITFREE)) {
			if (!(bmoBankMovement.getDeposit().toDouble() > 0)
					&& !(bmoBankMovement.getId() > 0)) 
				bmUpdateResult.addError(bmoBankMovement.getDeposit().getName(), "La cantidad no puede ser 0.");
		} else if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_DISPOSALFREE)) {
			if (!getSFParams().hasSpecialAccess(BmoBankMovement.ACCESS_DISPOSAL)) {
				bmUpdateResult.addError(bmoBankMovement.getCode().getName(),"No cuenta con permisos para realizar este movimiento.");
			}
			
			if (!(bmoBankMovement.getWithdraw().toDouble() >= 0)) 
				bmUpdateResult.addError(bmoBankMovement.getWithdraw().getName(), "El monto no puede ser menor a 0.");
			
			//Los Tipo cargo libre, cuando se generan desde una cancelacion no se debe solicitar el presupuesto
			if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
				if (!(bmoBankMovement.getBkmvCancelId().toInteger() > 0)) {
					if (!(bmoBankMovement.getBudgetItemId().toInteger() > 0)) {
						bmUpdateResult.addError(bmoBankMovement.getBudgetItemId().getName(), "Debe capturar el presupuesto.");
					}
				}	
			}
		} else if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_LOANDISPOSAL)) {
			if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {

				if (!(bmoBankMovement.getDeposit().toDouble() > 0)) 
					bmUpdateResult.addError(bmoBankMovement.getDeposit().getName(), "Debe capturar el monto");

				if (!(bmoBankMovement.getLoanId().toInteger() > 0)) 
					bmUpdateResult.addError(bmoBankMovement.getLoanId().getName(), "Debe capturar un crédito");

				if (!(bmoBankMovement.getBudgetItemId().toInteger() > 0)) 
					bmUpdateResult.addError(bmoBankMovement.getBudgetItemId().getName(), "Debe capturar una partida");
			}
		}
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		this.bmoBankMovement = (BmoBankMovement) bmObject;

		PmCompanyNomenclature pmCompanyNomenclature = new PmCompanyNomenclature(getSFParams());
		String code = "";
		boolean newRecord = false;
		// Obtener el tipo bankmovtype
		PmBankMovType pmBankMovType = new PmBankMovType(getSFParams());
		BmoBankMovType bmoBankMovType = new BmoBankMovType();
		
		// Cuenta de banco
		BmoBankAccount bmoBankAccount = new BmoBankAccount();
		PmBankAccount pmBankAccount = new PmBankAccount(getSFParams());

		if (bmoBankMovement.getBankMovTypeId().toInteger() > 0)
			bmoBankMovType = (BmoBankMovType) pmBankMovType.get(pmConn,	bmoBankMovement.getBankMovTypeId().toInteger());
		else bmUpdateResult.addError(bmoBankMovement.getBankAccountId().getName(), "Debe seleccionar el Tipo de Mov. Bancario.");
		
				
		if (bmoBankMovement.getBankAccountId().toInteger() > 0)
			bmoBankAccount = (BmoBankAccount)pmBankAccount.get(pmConn, bmoBankMovement.getBankAccountId().toInteger());
		else bmUpdateResult.addError(bmoBankMovement.getBankAccountId().getName(), "Debe seleccionar una Cuenta de Banco.");
		
		// Validar informacion de los diferentes escenarios
		this.validateBankMovement(bmoBankMovement,bmoBankMovType, bmoBankAccount, bmUpdateResult);
		
		if (!(bmObject.getId() > 0)) {
			newRecord = true;
			// Es un registro nuevo, guardar una vez para recuperar ID, luegovalidate2
			super.save(pmConn, bmoBankMovement, bmUpdateResult);

			int bankMovementId = bmUpdateResult.getId();
			bmoBankMovement.setId(bankMovementId);
			// Generar clave personalizada si la hay, si no retorna la de por defecto
			code = pmCompanyNomenclature.getCodeCustom(pmConn,
					bmoBankAccount.getCompanyId().toInteger(),
					bmoBankMovement.getProgramCode().toString(),
					bmUpdateResult.getId(),
					BmoBankMovement.CODE_PREFIX
					);
			bmoBankMovement.getCode().setValue(code);
//			bmoBankMovement.getCode().setValue(bmoBankMovement.getCodeFormat());
				
			// Validar que no sobre pase el saldo de la OC
			if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)
					&& bmoBankMovType.getType().equals(BmoBankMovType.TYPE_WITHDRAW)) {

				// Movimiento de tipo Transferencia
			} else if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_CXP)) {
				if (bmoBankMovType.getType().equals(BmoBankMovType.TYPE_WITHDRAW)) {

					if (!bmoBankMovement.getNoCheck().equals("")) {
						//Asignar el numero de cheque
						bmoBankAccount.getCheckNo().setValue(bmoBankMovement.getNoCheck().toInteger());					
						pmBankAccount.save(pmConn, bmoBankAccount, bmUpdateResult);
					}	
				}
			} else if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_DEVOLUTIONCXC)) {				
				if (!bmoBankMovement.getNoCheck().equals("")) {
					//Asignar el numero de cheque
					bmoBankAccount.getCheckNo().setValue(bmoBankMovement.getNoCheck().toInteger());					
					pmBankAccount.save(pmConn, bmoBankAccount, bmUpdateResult);
				}	
			// Transferencia
			} else if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_TRANSFER)) {
				if (bmoBankMovType.getType().equals(BmoBankMovType.TYPE_WITHDRAW)) {

					if (!(bmoBankMovement.getWithdraw().toDouble() > 0))
						bmUpdateResult.addError(bmoBankMovement.getWithdraw().getName(), "Debe capturar el monto");

					if (bmoBankMovement.getBankAccountId().toInteger() == bmoBankMovement.getBankAccoTransId().toInteger()) {
						bmUpdateResult.addError(bmoBankMovement.getBankAccoTransId().getName(),	"La cuenta destino debe ser distinta al origen.");
					}

					// Validar Saldo en la Cta Banco
					if (!((BmoFlexConfig) getSFParams().getBmoAppConfig())
							.getNegativeBankBalance().toBoolean()) {
						if (!validBkacBalance(pmConn, bmObject, bmUpdateResult))
							bmUpdateResult.addError(bmoBankMovement.getBankAccountId().getName(),"La Cuenta de Banco no tiene Saldo Suficiente.");
					}

					PmBankAccount pmBankAccountForeign = new PmBankAccount(getSFParams());
					BmoBankAccount bmoBankAccountForeign = (BmoBankAccount) pmBankAccountForeign.get(pmConn, bmoBankMovement.getBankAccoTransId().toInteger());

					// Crear el Concepto de Banco de envio de traspaso
					PmBankMovConcept pmBankMovConcept = new PmBankMovConcept(getSFParams());
					BmoBankMovConcept bmoBankMovConcept = new BmoBankMovConcept();
					bmoBankMovConcept.getBankMovementId().setValue(bmoBankMovement.getId());
					bmoBankMovConcept.getAmount().setValue(bmoBankMovement.getWithdraw().toDouble());
					bmoBankMovConcept.getCode().setValue("Trasp.Para:"+ bmoBankAccountForeign.getName().toString());
					pmBankMovConcept.saveSimple(pmConn, bmoBankMovConcept,bmUpdateResult);

					// Crear el movimiento bancario que recibe el traspaso
					PmBankMovement pmBkmvNew = new PmBankMovement(getSFParams());
					BmoBankMovement bmoBkmvNew = new BmoBankMovement();

					bmoBkmvNew.getBankAccountId().setValue(bmoBankAccountForeign.getId());
					bmoBkmvNew.getParentId().setValue(bmoBankMovement.getId());
					bmoBkmvNew.getBankReference().setValue("bkmv -> " + bmoBankMovement.getId());
					bmoBkmvNew.getDescription().setValue("Traspaso Recibido");
					bmoBkmvNew.getBankMovTypeId().setValue(bmoBankMovType.getBankMovTypeChildId().toInteger());
					bmoBkmvNew.getStatus().setValue("" + BmoBankMovement.STATUS_REVISION);
					bmoBkmvNew.getInputDate().setValue(bmoBankMovement.getInputDate().toString());
					bmoBkmvNew.getDueDate().setValue(bmoBankMovement.getDueDate().toString());
					bmoBkmvNew.getCurrencyParity().setValue(bmoBankMovement.getCurrencyParity().toDouble());
					bmoBkmvNew.getPaymentTypeId().setValue(bmoBankMovement.getPaymentTypeId().toInteger());
					pmBkmvNew.save(pmConn, bmoBkmvNew, bmUpdateResult);
					
					// Crear el Concepto de Banco de recepcion de traspaso
					PmBankMovConcept pmBankMovConceptNew = new PmBankMovConcept(getSFParams());
					BmoBankMovConcept bmoBankMovConceptNew = new BmoBankMovConcept();
					bmoBankMovConceptNew.getBankMovementId().setValue(bmoBkmvNew.getId());

					double amount = bmoBankMovement.getWithdraw().toDouble();

					if (bmoBankAccount.getCurrencyId().toInteger() != bmoBankAccountForeign.getCurrencyId().toInteger()) {
						amount = bmoBankMovement.getAmountConverted().toDouble();
						// La paridad debe ser mayor a cero
						if (!(bmoBankMovement.getCurrencyParity().toDouble() > 0))
							bmUpdateResult.addError(bmoBankMovement.getCurrencyParity().getName(),"El Tipo de Cambio debe ser mayor a 0.");
					}

					bmoBankMovConceptNew.getAmount().setValue(amount);
					bmoBankMovConceptNew.getCode().setValue("Trasp.de:" + bmoBankAccount.getName().toString());
					pmBankMovConceptNew.saveSimple(pmConn, bmoBankMovConceptNew, bmUpdateResult);

					pmBkmvNew.updateBalance(pmConn, bmoBkmvNew, bmUpdateResult);
				}

			// Deposito Libre
			} else if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_DEPOSITFREE)) {
//				if (!(bmoBankMovement.getDeposit().toDouble() > 0))
//					bmUpdateResult.addError(bmoBankMovement.getDeposit().getName(), "Debe capturar el monto");

				// Crear el Concepto de Banco
				PmBankMovConcept pmBankMovConceptNew = new PmBankMovConcept(getSFParams());
				BmoBankMovConcept bmoBankMovConceptNew = new BmoBankMovConcept();

				bmoBankMovConceptNew.getBankMovementId().setValue(bmoBankMovement.getId());
				bmoBankMovConceptNew.getAmount().setValue(bmoBankMovement.getDeposit().toDouble());
				bmoBankMovConceptNew.getCode().setValue("Dep Cta: "	+ bmoBankMovement.getBmoBankAccount().getName().toString());				
				pmBankMovConceptNew.saveSimple(pmConn, bmoBankMovConceptNew,bmUpdateResult);

				// Disposicion Libre
			} else if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_DISPOSALFREE)) {
//				if (!(bmoBankMovement.getWithdraw().toDouble() > 0)) {
//					bmUpdateResult.addError(bmoBankMovement.getWithdraw().getName(), "Debe capturar el monto");
//				} 

				if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
					if (!(bmoBankMovement.getBudgetItemId().toInteger() > 0)) {
						bmUpdateResult.addError(bmoBankMovement.getBudgetItemId().getName(), "Debe capturar el presupuesto");
					}				

					PmBudgetItem pmBudgetItem = new PmBudgetItem(getSFParams());
					BmoBudgetItem bmoBudgetItem = (BmoBudgetItem)pmBudgetItem.get(pmConn, bmoBankMovement.getBudgetItemId().toInteger());

					pmBudgetItem.updateBalance(pmConn, bmoBudgetItem, bmUpdateResult);
				}	

				// Crear el Concepto de Banco
				PmBankMovConcept pmBankMovConceptNew = new PmBankMovConcept(getSFParams());
				BmoBankMovConcept bmoBankMovConceptNew = new BmoBankMovConcept();

				bmoBankMovConceptNew.getBankMovementId().setValue(bmoBankMovement.getId());
				bmoBankMovConceptNew.getAmount().setValue(bmoBankMovement.getWithdraw().toDouble());
				bmoBankMovConceptNew.getCode().setValue("Disp Cta: " + bmoBankMovement.getBmoBankAccount().getName().toString());
				if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
					bmoBankMovConceptNew.getBudgetItemId().setValue(bmoBankMovement.getBudgetItemId().toInteger());
				}

				pmBankMovConceptNew.saveSimple(pmConn, bmoBankMovConceptNew,bmUpdateResult);

			} else if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_LOANDISPOSAL)) {
				if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {

					if (!(bmoBankMovement.getDeposit().toDouble() > 0)) {
						bmUpdateResult.addError(bmoBankMovement.getDeposit().getName(), "Debe capturar el monto");
					} 

					if (!(bmoBankMovement.getLoanId().toInteger() > 0)) {
						bmUpdateResult.addError(bmoBankMovement.getLoanId().getName(), "Debe capturar un crédito");
					}

					if (!(bmoBankMovement.getBudgetItemId().toInteger() > 0)) {
						bmUpdateResult.addError(bmoBankMovement.getBudgetItemId().getName(), "Debe capturar una partida");
					}

					// Crear el Concepto de Banco
					PmBankMovConcept pmBankMovConceptNew = new PmBankMovConcept(getSFParams());
					BmoBankMovConcept bmoBankMovConceptNew = new BmoBankMovConcept();

					bmoBankMovConceptNew.getBankMovementId().setValue(bmoBankMovement.getId());
					bmoBankMovConceptNew.getAmount().setValue(bmoBankMovement.getDeposit().toDouble());
					bmoBankMovConceptNew.getCode().setValue("Disp Crédito: " + bmoBankMovement.getBmoBankAccount().getName().toString());

					pmBankMovConceptNew.saveSimple(pmConn, bmoBankMovConceptNew,bmUpdateResult);

					PmLoanDisbursement pmLoanDisbursement = new PmLoanDisbursement(getSFParams());
					BmoLoanDisbursement bmoLoanDisbursement = new BmoLoanDisbursement();

					bmoLoanDisbursement.getLoanId().setValue(bmoBankMovement.getLoanId().toInteger());
					bmoLoanDisbursement.getDate().setValue(bmoBankMovement.getDueDate().toString());
					bmoLoanDisbursement.getBankMovConceptId().setValue(bmoBankMovConceptNew.getId());
					bmoLoanDisbursement.getAmount().setValue(bmoBankMovConceptNew.getAmount().toDouble());

					pmLoanDisbursement.save(pmConn, bmoLoanDisbursement, bmUpdateResult);

					pmLoanDisbursement.updateAmount(pmConn, bmoLoanDisbursement, bmUpdateResult);

					PmBudgetItem pmBudgetItem = new PmBudgetItem(getSFParams());
					BmoBudgetItem bmoBudgetItem = (BmoBudgetItem)pmBudgetItem.get(pmConn, bmoBankMovement.getBudgetItemId().toInteger());

					pmBudgetItem.updateBalance(pmConn, bmoBudgetItem, bmUpdateResult);
				}
			}
			if (!bmUpdateResult.hasErrors()) {
				// Se almacena
				super.save(pmConn, bmoBankMovement, bmUpdateResult);
	
				// Se actualiza saldos y balances
				this.updateBalance(pmConn, bmoBankMovement, bmUpdateResult);
			}
		} 
		// Modificar registro existente
		else {
			// Es registro existente
			PmBankMovement pmBankMovementPrev = new PmBankMovement(getSFParams());
			BmoBankMovement bmoBankMovementPrev = (BmoBankMovement) pmBankMovementPrev.get(pmConn, bmoBankMovement.getId());

			// Verificar si tiene permiso para Cambiar el Estatus
			if (bmoBankMovementPrev.getStatus().toChar() != bmoBankMovement.getStatus().toChar()) {
				if (!getSFParams().hasSpecialAccess(BmoBankMovement.ACCESS_CHANGESTATUS)) {
					bmUpdateResult.addError(bmoBankMovement.getStatus().getName(), "No tiene permisos para cambiar el Estatus (" +  BmoBankMovement.ACCESS_CHANGESTATUS + ").");
				}
			}

			// Movimiento En Revision
			if (bmoBankMovement.getStatus().equals(BmoBankMovement.STATUS_REVISION)) {
				saveInRevision(pmConn, bmoBankMovement, bmoBankMovementPrev, bmUpdateResult);
			// Autoriza movimiento
			} else if (bmoBankMovement.getStatus().equals(BmoBankMovement.STATUS_AUTHORIZED)) {
				saveAuthorized(pmConn, bmoBankMovement, bmoBankMovementPrev, bmUpdateResult);
				sendEmailAuthorizedMB(pmConn,  bmoBankMovement,  bmUpdateResult);
				
			// Cancelacion del Movimiento
			} else if (bmoBankMovement.getStatus().equals(BmoBankMovement.STATUS_CANCELLED)) {
				saveCancelled(pmConn, pmBankMovType, bmoBankMovType, bmoBankMovement, bmoBankMovementPrev, bmUpdateResult);
			// Se cambia estatus a Conciliado 
			} else 	if (bmoBankMovement.getStatus().equals(BmoBankMovement.STATUS_RECONCILED)) {
				saveReconcilled(pmConn, bmoBankMovement, bmoBankMovementPrev, bmUpdateResult);
			} else {
				// Un Mov cancelado no se puede cambiar de estatus
				if (bmoBankMovementPrev.getStatus().equals(BmoBankMovement.STATUS_CANCELLED)) {
					bmUpdateResult.addError(bmoBankMovement.getStatus().getName(), "El estatus no se puede cambiar el MB " + bmoBankMovement.getId() + " está Cancelado.");
				}
				this.updateAmount(pmConn, bmoBankMovement, bmUpdateResult);
			}

			// Asignar el budgetItemId si cambio o no tiene el concepto de banco			
			if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_DISPOSALFREE)) {
				if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {										
					//Obtener el concepto de banco
					BmoBankMovConcept bmoBankMovConceptNew = new BmoBankMovConcept();
					PmBankMovConcept pmBankMovConceptNew = new PmBankMovConcept(getSFParams());
					bmoBankMovConceptNew = (BmoBankMovConcept)pmBankMovConceptNew.getBy(pmConn, bmoBankMovement.getId(), bmoBankMovConceptNew.getBankMovementId().getName());

					bmoBankMovConceptNew.getBudgetItemId().setValue(bmoBankMovement.getBudgetItemId().toInteger());
					pmBankMovConceptNew.saveSimple(pmConn, bmoBankMovConceptNew, bmUpdateResult);
				}
			}	
			// Agregar bitacora interna de la tarea, si hay comentario
			if (!bmoBankMovement.getComments().toString().equals("")) {
				String commentLog = SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat())
						+ " (" + getSFParams().getLoginInfo().getBmoUser().getCode() + ") "									
						+ "\n" + getSFParams().getFieldFormTitle(bmoBankMovement.getComments()) + ": "+ bmoBankMovement.getComments().toString()
						+ "\n\n" + bmoBankMovement.getCommentLog().toString();
				bmoBankMovement.getCommentLog().setValue(commentLog);
				bmoBankMovement.getComments().setValue("");
			}
		}
		
		// Validar perido operativo de acuerdo a configuracion.
		if ( ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getRequiredPeriodFiscal().toBoolean() ) {
			printDevLog("MB: INICIO Periodo Operativo(requerido)");
			PmFiscalPeriod pmFiscalPeriod = new PmFiscalPeriod(getSFParams());
			boolean fiscalPeriodIsOpen = pmFiscalPeriod.isOpen(pmConn, bmoBankMovement.getDueDate().toString(), bmoBankAccount.getCompanyId().toInteger());
			printDevLog("MB: FIN Periodo Operativo: "+fiscalPeriodIsOpen);

			if (!fiscalPeriodIsOpen)
				bmUpdateResult.addError(bmoBankMovement.getDueDate().getName(), 
						"El Periodo Operativo está Cerrado en la fecha del Documento (" + bmoBankMovement.getDueDate().toString() + ").");
		} else {
			printDevLog("MB: INICIO Periodo Operativo(NO requerido)");
			PmFiscalPeriod pmFiscalPeriod = new PmFiscalPeriod(getSFParams());
			boolean fiscalPeriodIsClosed = pmFiscalPeriod.isClosed(pmConn, bmoBankMovement.getDueDate().toString(), bmoBankAccount.getCompanyId().toInteger());
			printDevLog("MB: FIN Periodo Operativo: "+fiscalPeriodIsClosed);

			if (fiscalPeriodIsClosed)
				bmUpdateResult.addError(bmoBankMovement.getDueDate().getName(), 
						"El Periodo Operativo está Cerrado en la fecha del Documento (" + bmoBankMovement.getDueDate().toString() + ").");
		}
		
		// Asignar que tiene archivos el MB
		if (getFiles(getSFParams().getProgramId(bmoBankMovement.getProgramCode()), bmoBankMovement.getId())) {
			bmoBankMovement.getFile().setValue("1");
		} else {
			bmoBankMovement.getFile().setValue("0");
		}
		
		// Se almacena si no existen errores
		if (!bmUpdateResult.hasErrors()) {
			// Actualizar id de claves del programa por empresa
			if (newRecord) {
				pmCompanyNomenclature.updateConsecutiveByCompany(pmConn, bmoBankAccount.getCompanyId().toInteger(), 
						bmoBankMovement.getProgramCode().toString());
			}
		
			super.save(pmConn, bmoBankMovement, bmUpdateResult);
		}
		return bmUpdateResult;
	}

	// Almacena el registro En Revision
	private void saveInRevision(PmConn pmConn, BmoBankMovement bmoBankMovement, BmoBankMovement bmoBankMovementPrev, BmUpdateResult bmUpdateResult) throws SFException {
		//Cambios Nuevo Framework
		if (bmoBankMovement.getBmoBankMovType().getType().equals(BmoBankMovType.TYPE_WITHDRAW)) {
			if (bmoBankMovementPrev.getStatus().equals(BmoBankMovement.STATUS_RECONCILED)) {

				if (!getSFParams().hasSpecialAccess(BmoBankMovement.ACCESS_CHANGERECONCILED)) {
					bmUpdateResult.addError(bmoBankMovement.getStatus().getName(), "No tiene permisos para cambiar a el estatus Conciliado (" + BmoBankMovement.ACCESS_CHANGERECONCILED + ").");
				}

			} else if (bmoBankMovementPrev.getStatus().equals(BmoBankMovement.STATUS_CANCELLED)) {
				if (!getSFParams().hasSpecialAccess(BmoBankMovement.ACCESS_CHANGECANCELLED)) 
					bmUpdateResult.addError(bmoBankMovement.getStatus().getName(), "No tiene permisos para cambiar a el estatus de Cancelado.");
			}
		}
		bmoBankMovement.getAuthorizeUserId().setValue(0);
		bmoBankMovement.getAuthorizeDate().setValue("");
		bmoBankMovement.getReconciledUserId().setValue(0);
		bmoBankMovement.getReconciledDate().setValue("");
		bmoBankMovement.getCancelledUserId().setValue(0);
		bmoBankMovement.getCancelledDate().setValue("");

		this.updateAmount(pmConn, bmoBankMovement, bmUpdateResult);
	}

	// Almacena registro autorizado
	private void saveAuthorized(PmConn pmConn, BmoBankMovement bmoBankMovement, BmoBankMovement bmoBankMovementPrev, BmUpdateResult bmUpdateResult) throws SFException {
		// Si es de tipo cargo
		if (bmoBankMovement.getBmoBankMovType().getType().equals(BmoBankMovType.TYPE_WITHDRAW)) {
			// Si no es de tipo transferencia y no es de tipo abono lbro
			if (!bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_TRANSFER) &&
					!bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_DISPOSALFREE)) {

				// Validar cambios en el estatus de banco
				if (bmoBankMovementPrev.getStatus().equals(BmoBankMovement.STATUS_REVISION)) {
					//Tiene Permisos para autorizar
					if (!getSFParams().hasSpecialAccess(BmoBankMovement.ACCESS_CHANGEAUTHORIZED)) {
						if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getAuthorizedBankMov().toBoolean()) 
							bmUpdateResult.addError(bmoBankMovement.getStatus().getName(), "No tiene permisos para Autorizar.");
					} 
				} else if (bmoBankMovementPrev.getStatus().equals(BmoBankMovement.STATUS_RECONCILED)) {
					if (!getSFParams().hasSpecialAccess(BmoBankMovement.ACCESS_CHANGERECONCILED))
						bmUpdateResult.addError(bmoBankMovement.getStatus().getName(), "No tiene permisos para cambiar estatus de Conciliado.");

					if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getAuthorizedBankMov().toBoolean()) 
						if (!getSFParams().hasSpecialAccess(BmoBankMovement.ACCESS_CHANGEAUTHORIZED))
							bmUpdateResult.addError(bmoBankMovement.getStatus().getName(), "No tiene permisos para Autorizar.");

					bmoBankMovement.getReconciledDate().setValue("");
					bmoBankMovement.getReconciledUserId().setValue("");
				} else if (bmoBankMovementPrev.getStatus().equals(BmoBankMovement.STATUS_CANCELLED)) {
					bmUpdateResult.addError(bmoBankMovement.getStatus().getName(), "Para Autorizar debe primero estar en Revisión.");
				}
			}
		}

		// Asigna el usuario que autoriza, si se esta autorizando en este momento
		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getAuthorizedBankMov().toBoolean()) {
			if (!bmoBankMovementPrev.getStatus().equals(BmoBankMovement.STATUS_AUTHORIZED)) {
				bmoBankMovement.getAuthorizeUserId().setValue(getSFParams().getLoginInfo().getUserId());
				bmoBankMovement.getAuthorizeDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));
			}
		}

		this.updateAmount(pmConn, bmoBankMovement, bmUpdateResult);
	}

	// Almacena el registro cancelado
	private void saveCancelled(PmConn pmConn, PmBankMovType pmBankMovType, BmoBankMovType bmoBankMovType, BmoBankMovement bmoBankMovement, BmoBankMovement bmoBankMovementPrev, BmUpdateResult bmUpdateResult) throws SFException {
		// Validar que el MB no se haya creado de una cancelación
		if (bmoBankMovement.getBkmvCancelId().toInteger() > 0)
			bmUpdateResult.addError(bmoBankMovement.getStatus()	.getName(), "No se puede cancelar este MB.");

		if (bmoBankMovementPrev.getStatus().equals(BmoBankMovement.STATUS_REVISION)) {

			if (!getSFParams().hasSpecialAccess(BmoBankMovement.ACCESS_CHANGECANCELLED)) 
				bmUpdateResult.addError(bmoBankMovement.getStatus().getName(), "No tiene permisos para Cancelar (" + BmoBankMovement.ACCESS_CHANGECANCELLED + ").");

			// Si se cancela en movimiento, se elimina el concepto y los
			// saldos de la cta de banco se altera,
			// Pero el monto del cargo o abono del mov de banco no se altera

			// Obtener el total del Mov de Banco
			double amount = 0;
			if (bmoBankMovement.getWithdraw().toDouble() > 0)  {
				amount = bmoBankMovement.getWithdraw().toDouble();
			}else {
				amount = bmoBankMovement.getDeposit().toDouble();
			}
			super.save(pmConn, bmoBankMovement, bmUpdateResult);

			BmoBankMovConcept bmoBankMovConcept = new BmoBankMovConcept();
			PmBankMovConcept pmBankMovConcept = new PmBankMovConcept(getSFParams());

			PmBankMovConcept pmBankMovConceptNew = new PmBankMovConcept(getSFParams());
			PmBankMovConcept pmBankMovConceptNew2 = new PmBankMovConcept(getSFParams());
			
			PmBankMovement pmBkmvNew = new PmBankMovement(getSFParams());
			BmoBankMovement bmoBkmvNew = new BmoBankMovement();

			// Crear el Tipo de Mov de Banco
			BmoBankMovType bmoBankMovTypeNew = new BmoBankMovType();

			if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_CXP)) {
				// Obtener el mov de deposito libre
				bmoBankMovTypeNew = (BmoBankMovType) pmBankMovType.getBy(pmConn, "" + BmoBankMovType.CATEGORY_DEPOSITFREE, bmoBankMovType.getCategory().getName());

			} else if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_CXC)
					|| bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_MULTIPLECXC)) {
				// Obtener el mov de disposicion
				bmoBankMovTypeNew = (BmoBankMovType) pmBankMovType.getBy(pmConn, "" + BmoBankMovType.CATEGORY_DISPOSALFREE, bmoBankMovType.getCategory().getName());

			} else if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_DEVOLUTIONCXC)) {
				bmoBankMovTypeNew = (BmoBankMovType) pmBankMovType.getBy(pmConn, "" + BmoBankMovType.CATEGORY_DEPOSITFREE, bmoBankMovType.getCategory().getName());

			} else if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_DEPOSITFREE)) {
				bmoBankMovTypeNew = (BmoBankMovType) pmBankMovType.getBy(pmConn, "" + BmoBankMovType.CATEGORY_DISPOSALFREE,
						bmoBankMovType.getCategory().getName());
				// Lo contrario de abono libre(abono) es disposiscion(cargo) se le pasa al valor contrario
				bmoBkmvNew.getWithdraw().setValue(amount);
			} else if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_DISPOSALFREE)) {
				bmoBankMovTypeNew = (BmoBankMovType) pmBankMovType.getBy(pmConn, "" + BmoBankMovType.CATEGORY_DEPOSITFREE,
						bmoBankMovType.getCategory().getName());
			} else if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) {
				if (bmoBankMovement.getBmoBankMovType().getType().equals(BmoBankMovType.TYPE_WITHDRAW)) {
					bmoBankMovTypeNew = (BmoBankMovType) pmBankMovType.getBy(pmConn, "" + BmoBankMovType.CATEGORY_DEPOSITFREE,bmoBankMovType.getCategory().getName());
				} else {
					bmoBankMovTypeNew = (BmoBankMovType) pmBankMovType.getBy(pmConn, ""	+ BmoBankMovType.CATEGORY_DISPOSALFREE,	bmoBankMovType.getCategory().getName());
				}
			} else if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_LOANDISPOSAL)) {
				bmoBankMovTypeNew = (BmoBankMovType) pmBankMovType.getBy(pmConn, ""	+ BmoBankMovType.CATEGORY_DISPOSALFREE,	bmoBankMovType.getCategory().getName());
			}

			if (bmoBankMovTypeNew.getId() > 0) {

				bmoBkmvNew.getBankAccountId().setValue(bmoBankMovement.getBankAccountId().toInteger());
				bmoBkmvNew.getBankReference().setValue(bmoBankMovement.getId());
				String noCheck = "";
				if (bmoBankMovement.getNoCheck().toString().length() > 0)
					noCheck = "Ch." + bmoBankMovement.getNoCheck().toString();

				bmoBkmvNew.getDescription().setValue("Cancelación MB-" + bmoBankMovement.getId() + " " + noCheck);
				bmoBkmvNew.getBankMovTypeId().setValue(bmoBankMovTypeNew.getId());
				bmoBkmvNew.getStatus().setValue("" + BmoBankMovement.STATUS_REVISION);
				bmoBkmvNew.getInputDate().setValue(SFServerUtil.nowToString(getSFParams(),getSFParams().getDateFormat()));
				bmoBkmvNew.getDueDate().setValue(SFServerUtil.nowToString(getSFParams(),getSFParams().getDateFormat()));
				bmoBkmvNew.getNoCheck().setValue(bmoBankMovement.getNoCheck().toString());
				bmoBkmvNew.getBkmvCancelId().setValue(bmoBankMovement.getId());
				bmoBkmvNew.getPaymentTypeId().setValue(bmoBankMovement.getPaymentTypeId().toInteger());
				
//				if (bmoBankMovement.getWithdraw().toDouble() > 0)
//					bmoBankMovement.getWithdraw().setValue(amount);
//				else
//					bmoBankMovement.getDeposit().setValue(amount);

				pmBkmvNew.saveSimple(pmConn, bmoBkmvNew, bmUpdateResult);

				bmoBkmvNew.getCode().setValue("MB-" + bmoBkmvNew.getId());

				bmoBkmvNew.setId(bmUpdateResult.getId());

				super.save(pmConn, bmoBkmvNew, bmUpdateResult);

				// Obtener los mov del concepto
				BmFilter filterBankMovConcepts = new BmFilter();
				filterBankMovConcepts.setValueFilter(bmoBankMovConcept.getKind(),bmoBankMovConcept.getBankMovementId(),	bmoBankMovement.getId());
				Iterator<BmObject> listBankMovConcepts = new PmBankMovConcept(getSFParams()).list(pmConn, filterBankMovConcepts).iterator();						
				double sumConcepts = 0;
				while (listBankMovConcepts.hasNext()) {
					bmoBankMovConcept = (BmoBankMovConcept) listBankMovConcepts.next();							
					sumConcepts += bmoBankMovConcept.getAmount().toDouble();

					// Crear un concepto de banco para sustituir los que se
					// eliminaron
					BmoBankMovConcept bmoBankMovConceptNew = new BmoBankMovConcept();
					bmoBankMovConceptNew.getBankMovementId().setValue(bmoBankMovement.getId());
					bmoBankMovConceptNew.getCode().setValue(bmoBankMovConcept.getCode().toString());
					bmoBankMovConceptNew.getAmount().setValue(bmoBankMovConcept.getAmount().toDouble());
					pmBankMovConceptNew.saveSimple(pmConn,	bmoBankMovConceptNew, bmUpdateResult);

					// Concepto del nvo MB
					BmoBankMovConcept bmoBankMovConceptNew2 = new BmoBankMovConcept();
					bmoBankMovConceptNew2.getBankMovementId().setValue(bmoBkmvNew.getId());
					bmoBankMovConceptNew2.getCode().setValue(bmoBankMovConcept.getCode().toString());
					bmoBankMovConceptNew2.getAmount().setValue(	bmoBankMovConcept.getAmount().toDouble());
					pmBankMovConceptNew2.saveSimple(pmConn,	bmoBankMovConceptNew2, bmUpdateResult);

					pmBankMovConcept.delete(pmConn, bmoBankMovConcept,	bmUpdateResult);
				}

				//Si es de tipo Anticipo actualizar el saldo en la OC
//				if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) {
//					if (bmoBankMovement.getBmoBankMovType().getType().equals(BmoBankMovType.TYPE_WITHDRAW)) {
//						PmRequisition pmRequisition = new PmRequisition(getSFParams());
//						BmoRequisition bmoRequisition = (BmoRequisition)pmRequisition.get(pmConn, bmoBankMovement.getRequisitionId().toInteger());
//						pmRequisition.updatePayments(pmConn, bmoRequisition, bmUpdateResult);
//					}
//				}

				if (!(sumConcepts > 0)) {
					BmoBankMovConcept bmoBankMovConceptNew = new BmoBankMovConcept();
					bmoBankMovConceptNew.getBankMovementId().setValue(bmoBankMovement.getId());
					bmoBankMovConceptNew.getCode().setValue(bmoBankMovType.getName().toString());
					bmoBankMovConceptNew.getAmount().setValue(amount);	
					pmBankMovConceptNew.saveSimple(pmConn, bmoBankMovConceptNew, bmUpdateResult);

					bmoBankMovConceptNew = new BmoBankMovConcept();
					bmoBankMovConceptNew.getBankMovementId().setValue(bmoBkmvNew.getId());
					bmoBankMovConceptNew.getCode().setValue(bmoBankMovType.getName().toString());
					bmoBankMovConceptNew.getAmount().setValue(amount);	
					pmBankMovConceptNew.saveSimple(pmConn, bmoBankMovConceptNew, bmUpdateResult);
				}

				//Actualizar el MB Nuevo
				pmBkmvNew.updateAmount(pmConn, bmoBkmvNew, bmUpdateResult);
			}

			bmoBankMovement.getCancelledUserId().setValue(getSFParams().getLoginInfo().getUserId());
			bmoBankMovement.getCancelledDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));

			//Actulizar el MB cancelado
			this.updateAmount(pmConn, bmoBankMovement, bmUpdateResult);
		} else  {
			if (!getSFParams().hasSpecialAccess(BmoBankMovement.ACCESS_CHANGECANCELLED)) {
				bmUpdateResult.addError(bmoBankMovement.getStatus().getName(), "No tiene permisos para Cancelar.");
			} else {
				bmUpdateResult.addError(bmoBankMovement.getStatus()	.getName(), "No se puede Cancelar el MB, ya está " + bmoBankMovementPrev.getStatus().getSelectedOption().getLabel() + ".");
			}
		}
	}

	// Almacena movimiento conciliado
	private void saveReconcilled(PmConn pmConn, BmoBankMovement bmoBankMovement, BmoBankMovement bmoBankMovementPrev, BmUpdateResult bmUpdateResult) throws SFException {
		// Cambios Nuevo Framework
		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getAuthorizedBankMov().toBoolean()) {

			if (bmoBankMovement.getBmoBankMovType().getType().equals(BmoBankMovType.TYPE_WITHDRAW)) {
				if (!bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_TRANSFER) &&
						!bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_DISPOSALFREE)) {

					//Validar cambios en el estatus de banco
					if (!bmoBankMovementPrev.getStatus().equals(BmoBankMovement.STATUS_RECONCILED)) {

						if (!getSFParams().hasSpecialAccess(BmoBankMovement.ACCESS_CHANGERECONCILED)) {
							bmUpdateResult.addError(bmoBankMovement.getStatus().getName(), "No tiene permisos para Conciliar (" + BmoBankMovement.ACCESS_CHANGERECONCILED + ").");
						} else {
							if (!bmoBankMovementPrev.getStatus().equals(BmoBankMovement.STATUS_AUTHORIZED)) {					
								bmUpdateResult.addError(bmoBankMovement.getStatus().getName(), "Para Conciliar debe estar Autorizado.");
							} else {

								// Usuario que realizo la conciliación y fecha que se realizo
								bmoBankMovement.getReconciledUserId().setValue(getSFParams().getLoginInfo().getUserId());
								bmoBankMovement.getReconciledDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));

								// Envio de Correo al conciliar
								if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getSendEmailReconciled().toBoolean()) {
									if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_CXP) ||
											bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) {							
										fixSendMailReconciled(pmConn, bmoBankMovement, bmUpdateResult);							
									}
								}

							}
						}
						this.updateAmount(pmConn, bmoBankMovement, bmUpdateResult);
					}
				} else {
					// Usuario que realizo la conciliación y fecha que se realizo
					bmoBankMovement.getReconciledUserId().setValue(getSFParams().getLoginInfo().getUserId());
					bmoBankMovement.getReconciledDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));
				}
			} else {						
				//Usuario que realizo la conciliación y fecha que se realizo
				bmoBankMovement.getReconciledUserId().setValue(getSFParams().getLoginInfo().getUserId());
				bmoBankMovement.getReconciledDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));

				if (!bmoBankMovementPrev.getStatus().equals(BmoBankMovement.STATUS_RECONCILED)) {
					if (!getSFParams().hasSpecialAccess(BmoBankMovement.ACCESS_CHANGERECONCILED)) {
						bmUpdateResult.addError(bmoBankMovement.getStatus().getName(), "No tiene permisos para Conciliar (" + BmoBankMovement.ACCESS_CHANGERECONCILED + ").");
					} else {
						//Envio de Correo al conciliar
						if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getSendEmailReconciled().toBoolean()) {
							if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_CXC)) {							
								fixSendMailReconciled(pmConn, bmoBankMovement, bmUpdateResult);							
							}
						}
					}
				}
			}
		} else {
			if (!bmoBankMovementPrev.getStatus().equals(BmoBankMovement.STATUS_RECONCILED)) {
				if (!getSFParams().hasSpecialAccess(BmoBankMovement.ACCESS_CHANGERECONCILED)) {
					bmUpdateResult.addError(bmoBankMovement.getStatus().getName(), "No tiene permisos para Conciliar (" + BmoBankMovement.ACCESS_CHANGERECONCILED + ").");
				}
			}

			//Usuario que realizo la conciliación y fecha que se realizo
			bmoBankMovement.getReconciledUserId().setValue(getSFParams().getLoginInfo().getUserId());
			bmoBankMovement.getReconciledDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));
		}

		this.updateAmount(pmConn, bmoBankMovement, bmUpdateResult);
	}

	//Enviar correo al autorizar un MB G100
	private void sendEmailAuthorizedMB(PmConn pmConn, BmoBankMovement bmoBankMovement, BmUpdateResult bmUpdateResult) throws SFException {
		System.err.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		PmFlexConfig pmFlexConfig = new PmFlexConfig(getSFParams());
		// MUltiEmpresa: g100
		if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getMultiCompany().toBoolean() ) {
			System.err.println("111111111");
			int companyCollectionProfileId = 0;
			companyCollectionProfileId = pmFlexConfig.getCompanyCollectionProfileId(pmConn, bmoBankMovement.getBmoBankAccount().getCompanyId().toInteger());
			if (companyCollectionProfileId > 0) {
				PmCompanyCollectionProfile pmCompanyCollectionProfile = new PmCompanyCollectionProfile(getSFParams());
				BmoCompanyCollectionProfile bmoCompanyCollectionProfile = new BmoCompanyCollectionProfile();
				bmoCompanyCollectionProfile = (BmoCompanyCollectionProfile)pmCompanyCollectionProfile.get(pmConn, companyCollectionProfileId);
				
				if (bmoCompanyCollectionProfile.getSendEmailAuthorizedMB().toInteger() > 0) {
					if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_CXC) &&
							bmoBankMovement.getBmoBankMovType().getType().equals(BmoBankMovType.TYPE_DEPOSIT)) {
							sendMailAuthorizedMB(pmConn, bmoBankMovement, bmUpdateResult);	
					}
				}
			}
		} else {
			System.err.println("22");
			// Envio de Correo al conciliar
			if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getSendEmailAuthorizedMB().toInteger() > 0) {
				if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_CXC) &&
						bmoBankMovement.getBmoBankMovType().getType().equals(BmoBankMovType.TYPE_DEPOSIT)) {
						sendMailAuthorizedMB(pmConn, bmoBankMovement, bmUpdateResult);	
				}
			}
		}
	}

	private void sendMailAuthorizedMB(PmConn pmConn, BmoBankMovement bmoBankMovement, BmUpdateResult bmUpdateResult) throws SFException {
		String  subject = "", msgBody = "";
		String sql = " SELECT (deve_name) as Desarrollo, (orde_total) as Total, (orde_payments) as Pagos, (orde_balance) as Saldo,prty_code, (comp_logo) AS logo FROM bankmovements  "
				+ " LEFT JOIN bankmovconcepts on (bkmc_bankmovementid = bkmv_bankmovementid) "
				+ " LEFT JOIN raccounts on (racc_raccountid = bkmc_raccountid) "
				+ " LEFT JOIN  orders on (orde_orderid = racc_orderid) "
				+ " LEFT JOIN orderproperties on (orpy_orderid = orde_orderid) " 
				+ " LEFT JOIN properties on (prty_propertyid = orpy_propertyid )"
				+ " LEFT JOIN developmentblocks on (dvbl_developmentblockid = prty_developmentblockid)" 
				+ " LEFT JOIN developmentphases on(dvph_developmentphaseid = dvbl_developmentphaseid)" 
				+ " LEFT JOIN developments on (deve_developmentid = dvph_developmentid) "
				+ " LEFT JOIN companies on (comp_companyid = deve_companyid) "
				+ " WHERE  bkmv_bankmovementid = "+bmoBankMovement.getId()+" ;";
		
		pmConn.doFetch(sql);
		System.out.println("------------------------------------------3");
		String desarrollo="",total = "", pagos = "", saldo = "",propertyCode="",logo ="";
		while (pmConn.next()){
			if(!(pmConn.getString("Desarrollo").equals(""))){
				desarrollo = pmConn.getString("Desarrollo");
			}
			if(!(pmConn.getString("Total").equals(""))){
				total = pmConn.getString("Total");
			}
			if(!(pmConn.getString("Pagos").equals(""))){
				pagos = pmConn.getString("Pagos");
			}
			if(!(pmConn.getString("Saldo").equals(""))){
				saldo = pmConn.getString("Saldo");
			}
			propertyCode = pmConn.getString("prty_code");
			if(!(pmConn.getString("logo").equals(""))){
				logo = pmConn.getString("logo");
			}
		}
		
		//Tiene una Cuenta de correo
		if (!bmoBankMovement.getBmoCustomer().getEmail().toString().equals("")) {		
//			
			String msg = "Estimado "+bmoBankMovement.getBmoCustomer().getFirstname()+", esperando te encuentres muy " + 
					"bien, queremos agradecer tu pago. Este ha sido recibido y aplicado con &eacute;xito por la " + 
					"cantidad de: "+SFServerUtil.formatCurrency(bmoBankMovement.getDeposit().toDouble())+" . Nos ponemos a tus &oacute;rdenes para cualquier duda que tengas en " + 
					"el siguiente tel&eacute;fono: (477) 7247116 y/o al siguiente correo: " + 
					"pagoscliente@valenciana.mx " + "<br><br>" +
					"Aprovechamos tambi&eacute;n, para invitarte a que sigas visitando nuestra p&aacute;gina web: " + 
					"www.valenciana.mx y redes sociales: facebook: La Valenciana e Instragam: " + 
					"@valencialeon, donde tendr&aacute;s actualizaciones sobre todas las buenas noticias y avances " + 
					"de nuestro Desarrollo, La Valenciana Arquitectura Residencial, la comunidad mejor " + 
					"planeada del Bajío." + "<br>" +
					"#inversióninteligente #terrenospremium #túpatrimonioseguro " + 
					"¡Estamos a tus &oacute;rdenes siempre!" + "<br><br>" +
					"<b>Resumen</b>" + "<br><br>" +
					"<b><i> Desarrollo: </i></b> "+desarrollo+"<br>" +
					"<b><i> Inmueble: </i></b> "+propertyCode+"<br>" +
					" <b><i> Total </i></b>: "+SFServerUtil.formatCurrency(Double.parseDouble(total))+"<br>" +
					"<b><i> Pagos </i></b>: "+SFServerUtil.formatCurrency(Double.parseDouble(pagos))+"<br>" +
					"<b><i> Saldo </i></b>: "+SFServerUtil.formatCurrency(Double.parseDouble(saldo))+"";
					
			//Enviar Correo al solicitante de la OC
			subject = desarrollo + ": #FLEX - Notificación de Pago Aplicado: ";
			msgBody = FlexUtil.mailBodyFormat(getSFParams(), 
					" #FLEX Notificación de Pago Aplicado"
					+ "", 
						msg
						,logo
					);
			if(bmoBankMovement.getBmoCustomer().getCustomertype().equals(BmoCustomer.TYPE_COMPANY)){
				sendMailAuthorized(bmoBankMovement.getBmoCustomer().getEmail().toString(), bmoBankMovement.getBmoCustomer().getLegalname().toString(), 
						subject, msgBody,desarrollo);
			}else{
			sendMailAuthorized(bmoBankMovement.getBmoCustomer().getEmail().toString(), bmoBankMovement.getBmoCustomer().getFirstname().toString()+" "+bmoBankMovement.getBmoCustomer().getFatherlastname(), 
					subject, msgBody,desarrollo);
			}
		} else {
			bmUpdateResult.addError(bmoBankMovement.getStatus().getName(), "El Cliente no cuenta con una dirección de correo");
		}
	}
	
	// enviar correo al autorizar mb
	private void sendMailAuthorized(String email, String fullName, String subject, String msgBody,String subjet) throws SFException {

		SFSendMail.send(getSFParams(),
				email, fullName,  
				getSFParams().getBmoSFConfig().getEmail().toString(), 
				subjet, 
				subject, 
				msgBody);
	}
			
	// Actualiza el saldo
	private void updateBalance(PmConn pmConn, BmoBankMovement bmoBankMovement,
			BmUpdateResult bmUpdateResult) throws SFException {

		// Actualizar el saldo de la cuenta de banco
		if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) {
			if (bmoBankMovement.getBmoBankMovType().getType().equals(BmoBankMovType.TYPE_DEPOSIT)) {
				updateBkacBalance(pmConn, bmoBankMovement, bmUpdateResult);
			} else {
				// Si existen items no se debe de actualizar el saldo
				if (!(sumConcepts(pmConn, bmoBankMovement) > 0))
					updateBkacBalanceDelete(pmConn, bmoBankMovement, bmUpdateResult);
			}

		} else {
			updateBkacBalance(pmConn, bmoBankMovement, bmUpdateResult);
		}
	}

	// Actualiza y calcula el monto a aplicar en los conceptos de banco
	public void updateAmount(PmConn pmConn, BmoBankMovement bmoBankMovement, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		double bkmcTotal = 0;

		// Obtener el tipo bankmovtype
		PmBankMovType pmBankMovType = new PmBankMovType(getSFParams());
		BmoBankMovType bmoBankMovType = (BmoBankMovType) pmBankMovType.get(pmConn, bmoBankMovement.getBankMovTypeId().toInteger());

		// Sumar los conceptos ligados al movimiento bancario
		sql = " SELECT SUM(bkmc_amount) AS totalbkmc FROM bankmovconcepts "
				+ " WHERE bkmc_bankmovementid = " + bmoBankMovement.getId();
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			bkmcTotal = pmConn.getDouble("totalbkmc");
		}

		// Actualizar la suma de los conceptos al campo amount de movimientos
		// bancarios
		//if (!bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) {
			if (bmoBankMovType.getType().equals(BmoBankMovType.TYPE_WITHDRAW)) {
				bmoBankMovement.getWithdraw().setValue(SFServerUtil.roundCurrencyDecimals(bkmcTotal));
			} else {
				bmoBankMovement.getDeposit().setValue(SFServerUtil.roundCurrencyDecimals(bkmcTotal));
			}
//		} else {
//			if (bmoBankMovType.getType().equals(BmoBankMovType.TYPE_DEPOSIT))
//				bmoBankMovement.getDeposit().setValue(SFServerUtil.roundCurrencyDecimals(bkmcTotal));
//		}

		super.save(pmConn, bmoBankMovement, bmUpdateResult);
	}

	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String data, String value) throws SFException {
		bmoBankMovement = (BmoBankMovement)bmObject;

		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();

		try {
			if(data.equals(BmoBankMovement.ACTION_CHECKNO)) {				
				PmBankAccount pmBankAccount = new PmBankAccount(getSFParams());
				BmoBankAccount bmoBankAccount = (BmoBankAccount)pmBankAccount.get(Integer.parseInt(value));
				if (bmoBankAccount.getCheckNo().toInteger() > 0)
					bmUpdateResult.setMsg("" + (bmoBankAccount.getCheckNo().toInteger() + 1));
				else	
					bmUpdateResult.setMsg("");
			} 
			else if(data.equals(BmoBankMovement.ACTION_BALACEBANKACCOUNT)) {
				PmBankAccount pmBankAccount = new PmBankAccount(getSFParams());
				bmUpdateResult.setMsg("" + SFServerUtil.roundCurrencyDecimals(pmBankAccount.getBalanceBankAccount(Integer.parseInt(value))));
			}
			else if (data.equals("" + BmoBankMovement.ACTION_GETCUREBYDESTYNY)) {
				bmUpdateResult.setMsg("" + getCurrencyFromDestiny(Integer.parseInt(value), bmUpdateResult));
			} else if (data.equals("" + BmoBankMovement.ACTION_BALANCEREQI)) {
				bmUpdateResult.setMsg("" + getRequisitionBalance(pmConn, Integer.parseInt(value), bmUpdateResult));
			} else if (data.equals("" + BmoBankMovement.ACTION_BALANCEREQIADVANCE)) {
				bmUpdateResult.setMsg("" + getBalanceRequistionAdvace(pmConn, Integer.parseInt(value), bmUpdateResult));
			} else if (data.equals("" + BmoBankMovement.ACTION_SHOWBANKMOVEMENTCP)) {
				bmUpdateResult = showBankMovementCP(pmConn, Integer.parseInt(value), bmUpdateResult);
			} else if (data.equals("" + BmoBankMovement.ACTION_SHOWBANKMOVEMENTCC)) {
				bmUpdateResult = showBankMovementCC(pmConn, Integer.parseInt(value), bmUpdateResult);
			} else if (data.equals("" + BmoBankMovement.ACTION_SHOWBANKMOVEMENT)) {
				bmUpdateResult = showBankMovement(pmConn, Integer.parseInt(value), bmUpdateResult);
			}


		} catch (SFException e) {
			throw new SFException(this.getClass().getName() + "-action() " + e.toString());
		} finally {
			pmConn.close();			
		}

		return bmUpdateResult;
	}

	private double getBalanceRequistionAdvace(PmConn pmConn, int bankMovementId, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";

		sql = " SELECT SUM(bkmc_amount) AS amount FROM bankmovconcepts " +
				" WHERE bkmc_bankmovementid = " + bankMovementId;
		pmConn.doFetch(sql);
		if (pmConn.next()) return FlexUtil.roundDouble(pmConn.getDouble("amount"), 2); 

		return 0;
	}

	//Obtener el Id de Bancos utilizando el abono de la CxP
	private BmUpdateResult showBankMovementCP(PmConn pmConn, int paccountId, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		int bankMovementId = 0;

		sql = " SELECT * FROM bankmovconcepts " +
				" WHERE bkmc_foreignid = " + paccountId +
				" AND NOT bkmc_paccountid IS NULL";
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			bankMovementId = pmConn.getInt("bkmc_bankmovementid");
		}

		bmoBankMovement = (BmoBankMovement)this.get(bankMovementId);

		bmUpdateResult.setBmObject(bmoBankMovement);

		return bmUpdateResult;
	}

	// Obtener el Id de Bancos utilizando el abono de la CxC
	private BmUpdateResult showBankMovementCC(PmConn pmConn, int raccountId, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		int bankMovementId = 0;

		sql = " SELECT * FROM bankmovconcepts " +
				" WHERE bkmc_foreignid = " + raccountId +
				" AND NOT bkmc_raccountid IS NULL";
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			bankMovementId = pmConn.getInt("bkmc_bankmovementid");
		}

		bmoBankMovement = (BmoBankMovement)this.get(bankMovementId);

		bmUpdateResult.setBmObject(bmoBankMovement);

		return bmUpdateResult;
	}

	// Mostrar movimiento de banco
	private BmUpdateResult showBankMovement(PmConn pmConn, int bankMovementId, BmUpdateResult bmUpdateResult) throws SFException {
		bmoBankMovement = (BmoBankMovement)this.get(bankMovementId);
		bmUpdateResult.setBmObject(bmoBankMovement);
		return bmUpdateResult;
	}

	// Obtener balance de la OC
	private double getRequisitionBalance(PmConn pmConn, int requisitionId, BmUpdateResult bmUpdateResult) throws SFException {
		PmRequisition pmRequisition = new PmRequisition(getSFParams());
		BmoRequisition bmoRequistion = (BmoRequisition)pmRequisition.get(pmConn, requisitionId);
		double balance = FlexUtil.roundDouble(bmoRequistion.getTotal().toDouble() - bmoRequistion.getPayments().toDouble(), 2);
		return balance;
	}

	// Obtener moneda del destino
	private int getCurrencyFromDestiny(int bankMovementId, BmUpdateResult bmUpdateResult) throws SFException {
		PmBankMovement pmBankMovement = new PmBankMovement(getSFParams());
		BmoBankMovement bmoBankMovement = (BmoBankMovement)pmBankMovement.get(bankMovementId);
		PmBankAccount pmBankAccount = new PmBankAccount(getSFParams());
		BmoBankAccount bmoBankAccount = (BmoBankAccount)pmBankAccount.get(bmoBankMovement.getBankAccoTransId().toInteger());
		return bmoBankAccount.getCurrencyId().toInteger();
	} 

	// Actualizar el balance de la cuenta bancaria
	private void updateBkacBalance(PmConn pmConn, BmObject bmObject,BmUpdateResult bmUpdateResult) throws SFException {
		bmoBankMovement = (BmoBankMovement) bmObject;

		PmBankMovType pmBankMovType = new PmBankMovType(getSFParams());
		BmoBankMovType bmoBankMovType = (BmoBankMovType) pmBankMovType.get(pmConn, bmoBankMovement.getBankMovTypeId().toInteger());

		double balanceNow = 0;
		double totalBkmv = 0;

		// Obtener la suma de los conceptos ligados al banco
		if (bmoBankMovType.getType().equals(BmoBankMovType.TYPE_WITHDRAW)) {
			totalBkmv = bmoBankMovement.getWithdraw().toDouble();
		} else {
			totalBkmv = bmoBankMovement.getDeposit().toDouble();
		}

		// Obtener el total la suma de los items
		if (!(totalBkmv > 0))
			totalBkmv = sumConcepts(pmConn, bmoBankMovement);

		//Obtener la Cuenta de Banco
		PmBankAccount pmBankAccount = new PmBankAccount(getSFParams());
		BmoBankAccount bmoBankAccount = (BmoBankAccount)pmBankAccount.get(pmConn, bmoBankMovement.getBankAccountId().toInteger());
		balanceNow = bmoBankAccount.getBalance().toDouble();

		// Actualizar el saldo en la cuenta de banco
		if (bmoBankMovType.getType().equals(BmoBankMovType.TYPE_DEPOSIT)) {
			balanceNow += totalBkmv;
		} else {
			if (!((BmoFlexConfig) getSFParams().getBmoAppConfig()).getNegativeBankBalance().toBoolean()) {
				if (totalBkmv > balanceNow && totalBkmv > 0) {
					bmUpdateResult.addError(bmoBankMovement.getBankAccountId().getName(), "No cuenta con saldo suficiente");
				}
			}

			balanceNow -= totalBkmv;
		}
		bmoBankAccount.getBalance().setValue(SFServerUtil.roundCurrencyDecimals(balanceNow));

		pmBankAccount.saveSimple(pmConn, bmoBankAccount, bmUpdateResult);
		this.updateAmount(pmConn, bmoBankMovement, bmUpdateResult);
	}

	// Obtener el Saldo de la Orden de Compra
	private double sumConcepts(PmConn pmConn, BmoBankMovement bmoBankMovement)
			throws SFPmException {
		String sql = "";
		double sumConcepts = 0;
		sql = "	SELECT SUM(bkmc_amount) AS totalconcepts FROM bankmovconcepts "
				+ " WHERE bkmc_bankmovementid = " + bmoBankMovement.getId();
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			sumConcepts = pmConn.getDouble("totalconcepts");
		}

		return sumConcepts;
	}

	// Actualizar el balance de la cuenta bancaria
	private boolean validBkacBalance(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoBankMovement = (BmoBankMovement) bmObject;
		String sql = "";

		double balanceNow = 0;
		double amountBkmv = 0;
		boolean result = true;

		// Obtener la cantidad del concepto bancario a transferir
		amountBkmv = bmoBankMovement.getWithdraw().toDouble();
		// Obtener los saldos de la cuenta de banco
		sql = " SELECT bkac_balance FROM bankaccounts "
				+ " WHERE bkac_bankaccountid = " + bmoBankMovement.getBankAccountId().toInteger();
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			balanceNow = pmConn.getDouble("bkac_balance");
		}

		// Validar si la cantidad a pagar no sobrepasa el saldo
		if (balanceNow >= amountBkmv) {
			result = true;
		} else {
			result = false;
		}

		return result;
	}

	// Ligar la CxP a un Anticipo de OC
//	public void setPaccountToBankmovement(PmConn pmConn, BmoPaccount bmoPaccount, BmUpdateResult bmUpdateResult) throws SFException {
//		String sql = "";		
//		PmConn pmConn2 = new PmConn(getSFParams());
//
//		try {
//			pmConn2.open();
//			
//			PmRequisitionReceipt pmRequisitionReceipt = new PmRequisitionReceipt(getSFParams());
//			BmoRequisitionReceipt bmoRequisitionReceipt  = (BmoRequisitionReceipt)pmRequisitionReceipt.get(pmConn, bmoPaccount.getRequisitionReceiptId().toInteger());
//			
//			BmoBankMovement bmoBankMovement = new BmoBankMovement();
//			ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
//			
//			// Filtro por OC
////			BmFilter filterByRequisition = new BmFilter();
////			filterByRequisition.setValueFilter(bmoBankMovement.getKind(), bmoBankMovement.getRequisitionId(), bmoRequisitionReceipt.getRequisitionId().toInteger());			
////			filterList.add(filterByRequisition);
//			
//			// Filtra por monto exacto
//			BmFilter filterByAmount = new BmFilter();
//			filterByAmount.setValueFilter(bmoBankMovement.getKind(), bmoBankMovement.getWithdraw(), "" + bmoPaccount.getAmount().toDouble());
//			filterList.add(filterByAmount);
//
//			Iterator<BmObject> bkmvIterator = this.list(pmConn, filterList).iterator();
//			printDevLog("antes ifff");
//			// Existe al menos un Mov de Banco con el mismo monto, toma solo el primer registro de Mov Banco de anticipo a la OC
//			if (bkmvIterator.hasNext()) {
//				bmoBankMovement = (BmoBankMovement)bkmvIterator.next();
//
//				printDevLog("1 clave: "+bmoBankMovement.getCode().toString());
//				// Validar que los conceptos no sobre pasen el monto del MB
//				double sumConcepts = 0;
//				sql = " SELECT SUM(bkmc_amount) AS concepts FROM bankmovconcepts " +
//						" WHERE bkmc_bankmovementid = " + bmoBankMovement.getId();
//				pmConn2.doFetch(sql);
//				if (pmConn2.next()) sumConcepts = pmConn2.getDouble("concepts");
//
//				if (sumConcepts < bmoBankMovement.getWithdraw().toDouble()) {
//					//Revisar si tiene un concepto de banco
////					sql = " SELECT COUNT(*) FROM bankmovconcepts " +
////							" WHERE bkmc_bankmovementid = " + bmoBankMovement.getId();
////					pmConn2.doFetch(sql);
//
//					sumConcepts = bmoBankMovement.getWithdraw().toDouble() - sumConcepts;
//
//					if (sumConcepts > bmoPaccount.getBalance().toDouble()) {
//						sumConcepts = bmoPaccount.getBalance().toDouble();
//					}
//
//					PmBankMovConcept pmBankMovConcept = new PmBankMovConcept(getSFParams());
//					BmoBankMovConcept bmoBankMovConcept = new BmoBankMovConcept();
//					bmoBankMovConcept.getBankMovementId().setValue(bmoBankMovement.getId());
//					bmoBankMovConcept.getPaccountId().setValue(bmoPaccount.getId());
//					bmoBankMovConcept.getAmount().setValue(sumConcepts);
//					pmBankMovConcept.save(pmConn, bmoBankMovConcept, bmUpdateResult);
//					
//				}	
//			} 
//			// No encontro Mov. Banco del monto exacto de la CxP, busca cualquier ligado a la OC
//			else {
////				bkmvIterator = this.list(pmConn, filterByRequisition).iterator();
////				printDevLog("elseee");
////				while (bkmvIterator.hasNext()) {	
////					bmoBankMovement = (BmoBankMovement)bkmvIterator.next();
////					printDevLog("2 clave: "+bmoBankMovement.getCode().toString());
////					
////					// Validar que los conceptos no sobre pasen el monto del MB
////					double sumConcepts = 0;				
////					sql = " SELECT SUM(bkmc_amount) AS concepts FROM bankmovconcepts " +
////							" WHERE bkmc_bankmovementid = " + bmoBankMovement.getId();
////					pmConn2.doFetch(sql);
////					if (pmConn2.next()) sumConcepts = pmConn2.getDouble("concepts");
////					
////					printDevLog("SUMA CONCEPTOS: "+sumConcepts + " CARGO-MB : "+bmoBankMovement.getWithdraw().toDouble());
////					if (sumConcepts < bmoBankMovement.getWithdraw().toDouble()) {
////						printDevLog("1");
////						// Revisar si tiene un concepto de banco
//////						sql = " SELECT COUNT(*) FROM bankmovconcepts " +
//////								" WHERE bkmc_bankmovementid = " + bmoBankMovement.getId();
//////						pmConn2.doFetch(sql);
////
////						sumConcepts = bmoBankMovement.getWithdraw().toDouble() - sumConcepts;
////						printDevLog("SUMA CONCEPTOS2: "+sumConcepts);
////
////						if (sumConcepts > bmoPaccount.getBalance().toDouble()) {
////							printDevLog("REDUCE MONTO");
////							// Si es Anticipo Proveedor no igualar el monto
////							if (!(bmoBankMovement.getBmoBankMovType().getType().equals(BmoBankMovType.TYPE_WITHDRAW) 
////									&& bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)))
////							sumConcepts = bmoPaccount.getBalance().toDouble();
////						}	
////						printDevLog("SUMA CONCEPTOS3: "+sumConcepts);
////
////						PmBankMovConcept pmBankMovConcept = new PmBankMovConcept(getSFParams());
////						BmoBankMovConcept bmoBankMovConcept = new BmoBankMovConcept();
////						bmoBankMovConcept.getBankMovementId().setValue(bmoBankMovement.getId());
////						bmoBankMovConcept.getPaccountId().setValue(bmoPaccount.getId());
////						bmoBankMovConcept.getAmount().setValue(sumConcepts);
////						pmBankMovConcept.save(pmConn, bmoBankMovConcept, bmUpdateResult);
////					}	
////				}
//			}	
//
//		} catch (SFException e) {
//			bmUpdateResult.addMsg("Error setPaccountToBankmovement " + e.toString());
//		} finally {
//			pmConn2.close();
//		}	
//	}	

	//Cambios Nuevo Framework
	//Envio de Correo al conciliar
	private void fixSendMailReconciled(PmConn pmConn, BmoBankMovement bmoBankMovement, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "", subject = "", msgBody = "";
		subject = getSFParams().getBmoSFConfig().getAppTitle().toString();
		PmUser pmUser = new PmUser(getSFParams());
		BmoUser bmoUser = new BmoUser();
		BmoUser bmoParent = new BmoUser();

		//Obtener la Cta de Banco		
		PmBankAccount pmBankAccount = new PmBankAccount(getSFParams());
		BmoBankAccount bmoBankAccount = (BmoBankAccount)pmBankAccount.get(pmConn, bmoBankMovement.getBankAccountId().toInteger());

		//Datos del Proveedor
		if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_CXP) ||
				bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) {
			PmRequisition pmRequisition = new PmRequisition(getSFParams());
			BmoRequisition bmoRequisition = new BmoRequisition();
			
			PmPaccount pmPaccount = new PmPaccount(getSFParams());
			BmoPaccount bmoPaccount = new BmoPaccount();

			String listRequisitions = "";
			ArrayList<Integer> arrayUserId = new ArrayList<Integer>();

			double sumReqiAmount = 0;					

			//Pago a Proveedor
			//if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_CXP)) {

				int requisitionId = 0;				

				//Enviar Correo de la CxP ligadas a un correo (Pago Proveedor y Anticipo Prov.)
				sql = " SELECT bkmc_amount, bkmc_requisitionid, bkmc_paccountid " +
//						" reqi_requisitionid,reqi_code, reqi_name, reqi_requestedby, reqi_authorizeduser," +
//						" reqi_usercreateid, pacc_code, pacc_invoiceno  " +
						" FROM bankmovconcepts " +
						" LEFT JOIN bankmovements ON (bkmv_bankmovementid = bkmc_bankmovementid) " +
						" LEFT JOIN bankmovtypes ON (bkmt_bankmovtypeid = bkmv_bankmovtypeid) " +
						
//						" LEFT JOIN paccounts ON (bkmc_paccountid = pacc_paccountid) " +
//						" LEFT JOIN paccounttypes ON (pacc_paccounttypeid = pact_paccounttypeid) " +
//						" LEFT JOIN requisitionreceipts ON (pacc_requisitionreceiptid = rerc_requisitionreceiptid) " +
//						" LEFT JOIN requisitions ON (rerc_requisitionid = reqi_requisitionid) " +
						
						" WHERE bkmc_bankmovementid = " + bmoBankMovement.getId() +
						" AND bkmt_type = '" + BmoBankMovType.TYPE_WITHDRAW + "' " +
						" AND (bkmt_category = '" + BmoBankMovType.CATEGORY_CXP + "' "+
						" OR bkmt_category = '" + BmoBankMovType.CATEGORY_REQUISITIONADVANCE + "') " + 
						//" AND pact_category = '" + BmoPaccountType.CATEGORY_REQUISITION + "'" +
						" ORDER BY bkmc_requisitionid, bkmc_paccountid";			
				
				printDevLog("sql_fixSendMailReconciled: "+sql);
				pmConn.doFetch(sql);
				while (pmConn.next()) {
					double amount = pmConn.getDouble("bkmc_amount");
					int bkmcReqiId = pmConn.getInt("bkmc_requisitionid");
					int bkmcPaccId = pmConn.getInt("bkmc_paccountid");

					// Anticipo
					if (bkmcReqiId > 0 && !(bkmcPaccId > 0)) {
						bmoRequisition = (BmoRequisition)pmRequisition.get(bkmcReqiId);
						bmoPaccount = new BmoPaccount();
					}
					
					// Pago proveedor
					if (bkmcPaccId > 0) {
						bmoPaccount = (BmoPaccount)pmPaccount.get(bkmcPaccId);
					}
					// Solo enviar de tipo OC
					if (bmoPaccount.getBmoPaccountType().getCategory().equals(BmoPaccountType.CATEGORY_REQUISITION) || bmoRequisition.getId() > 0) {
						// Pago proveedor
						if (bkmcPaccId > 0) {
							bmoRequisition = (BmoRequisition)pmRequisition.get(bmoPaccount.getRequisitionId().toInteger());
						}
						//Obtener la OC
						if (requisitionId != bmoRequisition.getId()) {
							requisitionId = bmoRequisition.getId();					
							listRequisitions += "<b> " + bmoRequisition.getCode().toString() + " " + bmoRequisition.getName().toString() + "</b> ";
							listRequisitions += " <a href=\"" + GwtUtil.getProperUrl(getSFParams(), "frm/flex_requisition.jsp") + "?h=" + new Date().getTime() + "format&w=EXT&z=" + 
									GwtUtil.encryptId(bmoRequisition.getId()) + "&resource=reqi" + (new Date().getTime() * 456) +"\"> (Ver O.C.)</a>";
							listRequisitions += "<br>";
	
							//Autorizado
							if (bmoRequisition.getAuthorizedUser().toInteger() > 0) {
								arrayUserId.add(bmoRequisition.getAuthorizedUser().toInteger());
							}
	
							//Creado
							if (bmoRequisition.getUserCreateId().toInteger() > 0) {
								arrayUserId.add(bmoRequisition.getUserCreateId().toInteger());
							}
	
							//Solicitado
							if (bmoRequisition.getRequestedBy().toInteger() > 0) {
								arrayUserId.add(bmoRequisition.getRequestedBy().toInteger());
	
								bmoUser = (BmoUser)pmUser.get(bmoRequisition.getRequestedBy().toInteger());
	
								//Jefe Directo
								if (bmoUser.getParentId().toInteger() > 0) {
									bmoParent = (BmoUser)pmUser.get(bmoUser.getParentId().toInteger());
									if (!bmoParent.getEmail().toString().equals(""))
										arrayUserId.add(bmoParent.getId());
								}	
							}
						}
	
						//Monto de las OC
						sumReqiAmount += amount;
						listRequisitions += " Factura:" + bmoPaccount.getInvoiceno().toString()	+ " " + SFServerUtil.formatCurrency(amount) + "<br> ";
					} 
				}

			//} else {
				//Anticipo a proveedor

//				bmoRequisition = (BmoRequisition)pmRequisition.get(pmConn, bmoBankMovement.getRequisitionId().toInteger());
//
//				listRequisitions += "<b> " + bmoRequisition.getCode().toString() + " " + bmoRequisition.getName().toString() + "</b>";
//				listRequisitions += "<br>";
//				listRequisitions += "<br>";
//				listRequisitions += "Se ha realizado un anticipo a la orden de compra";
//
//				sumReqiAmount = bmoBankMovement.getWithdraw().toDouble();
//
//				//Autorizado
//				if (bmoRequisition.getAuthorizedUser().toInteger() > 0) {
//					arrayUserId.add(bmoRequisition.getAuthorizedUser().toInteger());
//				}
//
//				//Creado
//				if (bmoRequisition.getUserCreateId().toInteger() > 0) {
//					arrayUserId.add(bmoRequisition.getUserCreateId().toInteger());
//				}
//
//				//Solicitado
//				if (bmoRequisition.getRequestedBy().toInteger() > 0) {
//					arrayUserId.add(bmoRequisition.getRequestedBy().toInteger());
//
//					bmoUser = (BmoUser)pmUser.get(pmConn,bmoRequisition.getRequestedBy().toInteger());
//
//					//Jefe Directo
//					if (bmoUser.getParentId().toInteger() > 0) {
//						bmoParent = (BmoUser)pmUser.get(pmConn, bmoUser.getParentId().toInteger());
//						if (!bmoParent.getEmail().toString().equals(""))
//							arrayUserId.add(bmoParent.getId());
//					}		
//				}
			//}	

			//Enviar Correo al Proveedor
			if (bmoBankMovement.getBmoSupplier().getSendEmail().toBoolean()) {
				//Tiene una Cuenta de correo
				if (!bmoBankMovement.getBmoSupplier().getEmail().toString().equals("")) {					
					//Enviar Correo al solicitante de la OC
					subject = getSFParams().getBmoSFConfig().getAppTitle().toString() + ": #FLEX - Notificación de Pago: ";
					msgBody = HtmlUtil.mailBodyFormat(getSFParams(), 
							" #FLEX Notificación de Pago", 
							" 	<p style=\"font-size:12px\"> "
									+ " <b>Empresa:</b> " + bmoBankAccount.getBmoCompany().getLegalName().toString()
									+ " <br>"
									+ " <b>Proveedor:</b> " + bmoBankMovement.getBmoSupplier().getName().toString()
									+ " <br> "
									+ " <b>Referencia:</b> " + bmoBankMovement.getBankReference().toString()
									+ " <br> "
									+ " <b>Nombre del Banco:</b> " + bmoBankAccount.getBankName().toString()
									+ " <br> "
									+ " <b>RFC del Banco:</b> " +bmoBankAccount.getBankRfc().toString()
									+ " <br> "
									+ " <b>Número de Cuenta Emisora:</b> " + bmoBankAccount.getAccountNo().toString()
									+ " <br> "
									+ " <b>Clave Interbancaria Emisora:</b> " + bmoBankAccount.getClabe().toString()
									+ " <br> "	
									+ " <b>Tipo de Moneda:</b> " + bmoBankAccount.getBmoCurrency().getCode()
									+ " <br> "
									+ " <b>Fecha Pago:</b> " + bmoBankMovement.getDueDate().toString()
									+ "	<b>No.cheque:</b> " + bmoBankMovement.getNoCheck().toString()
									+ " <br> "
									+ " <br> "
									+ " <b>Ordenes de Compra:</b> " 
									+ " <br> "
									+ listRequisitions
									+ " <br> "						
									+ "	<b>Monto:</b> " + SFServerUtil.formatCurrency(sumReqiAmount)
									+ "	<br> "
									+ "	</p> "
									+ "	<p align=\"left\" style=\"font-size:12px\"> "											
									+ " Este mensaje podría contener información confidencial, si tú no eres el destinatario por favor reporta esta situación a los datos de contacto "
									+ " y bórralo sin retener copia alguna."
									+ "	</p> "
							);
					sendMailReconciled(bmoBankMovement.getBmoSupplier().getEmail().toString(), bmoBankMovement.getBmoSupplier().getName().toString(), 
							subject, msgBody);

				} else {
					bmUpdateResult.addError(bmoBankMovement.getStatus().getName(), "El proveedor no cuenta con una dirección de correo");
				}
			}

			ArrayList<Integer> sendUserId = new ArrayList<Integer>();			
			//Listar el Array con los usuarios del la OC
			for(int x= 0; x < arrayUserId.size(); x++) {
				int count = 0;
				int userId = arrayUserId.get(x);
				printDevLog("userID: "+userId);
				for(int y = 0; y < arrayUserId.size(); y++) {					
					if (userId == arrayUserId.get(y)) {
						if (count == 0) {
							//Validar que no exista ya asignado
							boolean isAdd = false;
							for (int z = 0; z < sendUserId.size(); z++) {
								if (userId == sendUserId.get(z)) {
									isAdd = true;
								}
							}
							if (!isAdd)
								sendUserId.add(userId);
						} 
						arrayUserId.remove(y);							
						count = count + 1;
					}
				}
			}


			for (int x = 0; x < sendUserId.size(); x++) {				
				bmoUser = (BmoUser)pmUser.get(pmConn, sendUserId.get(x));		
				System.out.println("Usuario correo: " + bmoUser.getCode().toString());

				if (!bmoUser.getEmail().toString().equals("") && bmoUser.getStatus().equals(BmoUser.STATUS_ACTIVE)) {
					//Enviar Correo al solicitante de la OC
					subject = getSFParams().getBmoSFConfig().getAppTitle().toString() + ": #FLEX - Notificación de Pago: ";
					msgBody = HtmlUtil.mailBodyFormat(getSFParams(), 
							" #FLEX Notificación de Pago", 
							" 	<p style=\"font-size:12px\"> "
									+ " <b>Empresa:</b> " + bmoBankAccount.getBmoCompany().getLegalName().toString()
									+ " <br>"
									+ " <b>Proveedor:</b> " + bmoBankMovement.getBmoSupplier().getName().toString()
									+ " <br> "
								    + " <b>Referencia:</b> " + bmoBankMovement.getBankReference().toString()
									+ " <br> "
									+ " <b>Nombre del Banco:</b> " + bmoBankAccount.getBankName().toString()
									+ " <br> "
									+ " <b>RFC del Banco:</b> " +bmoBankAccount.getBankRfc().toString()
									+ " <br> "
									+ " <b>Número de Cuenta Emisora:</b> " + bmoBankAccount.getAccountNo().toString()
									+ " <br> "
									+ " <b>Clave Interbancaria Emisora:</b> " + bmoBankAccount.getClabe().toString()
									+ " <br> "	
									+ " <b>Tipo de Moneda:</b> " + bmoBankAccount.getBmoCurrency().getCode()
									+ " <br> "
									+ " <b>Fecha Pago:</b> " + bmoBankMovement.getDueDate().toString()
									+ "	<b>No.cheque:</b> " + bmoBankMovement.getNoCheck().toString()
									+ " <br> "
									+ " <br> "
									+ " <b>Ordenes de Compra:</b> " 
									+ " <br> "
									+ listRequisitions
									+ " <br> "						
									+ "	<b>Monto:</b> " + SFServerUtil.formatCurrency(sumReqiAmount)
									+ "	<br> "
									+ "	</p> "
									+ "	<p align=\"left\" style=\"font-size:12px\"> "											
									+ " Este mensaje podría contener información confidencial, si tú no eres el destinatario por favor reporta esta situación a los datos de contacto "
									+ " y bórralo sin retener copia alguna."
									+ "	</p> "
							);
					sendMailReconciled(bmoUser.getEmail().toString(), bmoBankMovement.getBmoSupplier().getName().toString(), 
							subject, msgBody);
				}	
			}

			pmPaccount = new PmPaccount(getSFParams());
			bmoPaccount = new BmoPaccount();

			//Enviar Correo de la CxP No ligadas a un correo(OC)
			sql = " SELECT SUM(bkmc_amount) AS amount, pacc_paccountid  FROM bankmovconcepts " +
					" LEFT JOIN paccounts ON (bkmc_paccountid = pacc_paccountid) " +
					" LEFT JOIN paccounttypes ON (pacc_paccounttypeid = pact_paccounttypeid) " +					
					" WHERE bkmc_bankmovementid = " + bmoBankMovement.getId() +
					" AND pact_category = '" + BmoPaccountType.CATEGORY_OTHER + "'" +
					" GROUP BY pacc_supplierid";
			pmConn.doFetch(sql);
			while (pmConn.next()) {
				//Enviar correo al usuario que lo creo
				bmoPaccount = (BmoPaccount)pmPaccount.get(pmConn.getInt("pacc_paccountid"));

				//Usuario que solicita
				bmoUser = new BmoUser();
				if (bmoPaccount.getUserCreateId().toInteger() > 0) {
					bmoUser = (BmoUser)pmUser.get(bmoPaccount.getUserCreateId().toInteger());
					if (!bmoUser.getEmail().toString().equals("") && bmoUser.getStatus().equals(BmoUser.STATUS_ACTIVE)) {
						//Enviar Correo al creador del CxP
						subject = getSFParams().getBmoSFConfig().getAppTitle().toString() + ": #FLEX - Notificación de Pago " + bmoPaccount.getCode().toString();
						msgBody = HtmlUtil.mailBodyFormat(getSFParams(), 
								" #FLEX -Notificación de Pago", 
								" 	<p style=\"font-size:12px\"> "
										+ " <b>Empresa:</b> " + bmoBankAccount.getBmoCompany().getLegalName().toString()
										+ " <br>"
										+ " <b>Proveedor:</b> " + bmoBankMovement.getBmoSupplier().getName().toString()
										+ " <br> "
										+ " <b>Referencia:</b> " + bmoBankMovement.getBankReference().toString()
								     	+ " <br> "
								     	+ " <b>Nombre del Banco:</b> " + bmoBankAccount.getBankName().toString()
								     	+ " <br> "
								     	+ " <b>RFC del Banco:</b> " +bmoBankAccount.getBankRfc().toString()
								     	+ " <br> "
								     	+ " <b>Número de Cuenta Emisora:</b> " + bmoBankAccount.getAccountNo().toString()
								     	+ " <br> "
								     	+ " <b>Clave Interbancaria Emisora:</b> " + bmoBankAccount.getClabe().toString()
								     	+ " <br> "	
								     	+ " <b>Tipo de Moneda:</b> " + bmoBankAccount.getBmoCurrency().getCode()
								     	+ " <br> "
										+ " <b>Factura:</b> " + bmoPaccount.getCode().toString() + " " + bmoPaccount.getInvoiceno().toString()								
										+ "	<b>Descripción:</b> " + bmoPaccount.getDescription().toString()								
										+ "	<b>No.Cheque:</b> " + bmoBankMovement.getNoCheck().toString()
										+ "	<br> "
										+ "	</p> "
										+ "	<p align=\"left\" style=\"font-size:12px\"> "										
										+ " Este mensaje podría contener información confidencial, si tú no eres el destinatario por favor reporta esta situación a los datos de contacto "
										+ " y bórralo sin retener copia alguna."
										+ "	</p> "
								);
						sendMailReconciled(bmoUser.getEmail().toString(), bmoUser.getFirstname().toString() + " " + bmoUser.getFatherlastname().toString(), 
								subject, msgBody);
					}	
				}	

				//Enviar Correo al Proveedor
				if (bmoBankMovement.getBmoSupplier().getSendEmail().toBoolean()) {
					//Tiene una Cuenta de correo
					if (!bmoBankMovement.getBmoSupplier().getEmail().toString().equals("")) {
						//Enviar Correo al solicitante de la OC
						subject = getSFParams().getBmoSFConfig().getAppTitle().toString() + ": #FLEX - Notificación de Pago " + bmoPaccount.getCode().toString();
						msgBody = HtmlUtil.mailBodyFormat(getSFParams(), 
								"Notificación de Pago", 
								" 	<p style=\"font-size:12px\"> "
										+ " <b>Empresa:</b> " + bmoBankAccount.getBmoCompany().getLegalName().toString()
										+ " <br>"
										+ " <b>Proveedor:</b> " + bmoBankMovement.getBmoSupplier().getName().toString()
										+ " <br> "
										+ " <b>Referencia:</b> " + bmoBankMovement.getBankReference().toString()
										+ " <br> "
										+ " <b>Nombre del Banco:</b> " + bmoBankAccount.getBankName().toString()
										+ " <br> "
										+ " <b>RFC del Banco:</b> " +bmoBankAccount.getBankRfc().toString()
										+ " <br> "
										+ " <b>Número de Cuenta Emisora:</b> " + bmoBankAccount.getAccountNo().toString()
										+ " <br> "
										+ " <b>Clave Interbancaria Emisora:</b> " + bmoBankAccount.getClabe().toString()
										+ " <br> "	
										+ " <b>Tipo de Moneda:</b> " + bmoBankAccount.getBmoCurrency().getCode()
										+ " <br> "
										+ " <br> "
										+ " <b>Factura:</b> " + bmoPaccount.getCode().toString() + " " + bmoPaccount.getInvoiceno().toString()								
										+ "	<b>Descripción:</b> " + bmoPaccount.getDescription().toString()								
										+ "	<b>No.Cheque:</b> " + bmoBankMovement.getNoCheck().toString()
										+ "	<br> "								
										+ "	</p> "
										+ "	<p align=\"left\" style=\"font-size:12px\"> "										
										+ " Este mensaje podría contener información confidencial, si tú no eres el destinatario por favor reporta esta situación a los datos de contacto "
										+ " y bórralo sin retener copia alguna."
										+ "	</p> "
								);
						sendMailReconciled(bmoBankMovement.getBmoSupplier().getEmail().toString(), bmoBankMovement.getBmoSupplier().getName().toString(), 
								subject, msgBody);
					} 
				}
			}

		} else if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_CXC)) {	
			String propertyAddress = getPropertyAddress(pmConn, bmoBankMovement, bmUpdateResult);
			String deveName = getDevelopmentName(pmConn, bmoBankMovement, bmUpdateResult);
			int salesmanId = getSalesmanId(pmConn, bmoBankMovement, bmUpdateResult);

			subject = getSFParams().getBmoSFConfig().getAppTitle().toString() + ": #FLEX - Notificación al cliente " + bmoBankMovement.getBmoCustomer().getCode().toString();
			msgBody = HtmlUtil.mailBodyFormat(getSFParams(),
					"Notificación al cliente", 
					" 	<p style=\"font-size:12px\"> "
							+ " <b>Inmobiliaria:</b> " + bmoBankAccount.getBmoCompany().getLegalName().toString()
							+ " <br>"
							+ " <b>Cliente:</b> " + bmoBankMovement.getBmoCustomer().getDisplayName().toString()
							+ " <br> "
							+ "	<p align=\"left\" style=\"font-size:12px\"> "
							+ " Estimado cliente:"
							+ " <br><br>"
							+ " Recibimos su pago por: " + SFServerUtil.formatCurrency(bmoBankMovement.getDeposit().toDouble())
							+ " el día " + bmoBankMovement.getDueDate().toString()
							+ " ,a cuenta de su vivienda ubicada en: " + propertyAddress + " fraccionamiento " + deveName
							+ " el cual fue aplicado a la cuenta por pagar que tiene con la inmobiliaria."
							+ "	</p> "									
							+ "	<br> "																		
							+ "	<p align=\"left\" style=\"font-size:12px\"> "											
							+ " Este mensaje podría contener información confidencial, si tú no eres el destinatario "
							+ " por favor reporta esta situación a los datos de contacto "
							+ " y bórralo sin retener copia alguna."
							+ "	</p> "
					);

			//El cliente puede recibir correo
			//			if (bmoBankMovement.getBmoCustomer().getSendEmailReconciled().toBoolean()) {				
			//				
			//				if (!bmoBankMovement.getBmoCustomer().getEmail().toString().equals("")) {
			//					sendMailReconciled(bmoBankMovement.getBmoCustomer().getEmail().toString(), bmoBankMovement.getBmoCustomer().getDisplayName().toString(), 
			//					           		   subject, msgBody);
			//				}
			//			}

			if (salesmanId > 0) {
				PmUser pmSalesman = new PmUser(getSFParams());
				BmoUser bmoSalesmanId = (BmoUser)pmSalesman.get(salesmanId);
				if (!bmoSalesmanId.getEmail().toString().equals("")) {
					//					String fullName = bmoSalesmanId.getFirstname().toString() + " " + bmoSalesmanId.getFatherlastname().toString();

					//sendMailReconciled(bmoSalesmanId.getEmail().toString(), fullName, subject, msgBody);
				}	
			}
		}
	}

	private void sendMailReconciled(String email, String fullName, String subject, String msgBody) throws SFException {		
		SFSendMail.send(getSFParams(),
				email, fullName,  
				getSFParams().getBmoSFConfig().getEmail().toString(), 
				getSFParams().getBmoSFConfig().getAppTitle().toString(), 
				subject, 
				msgBody);
	}	

	//Obtener Clave Vivienda
	private String getPropertyAddress(PmConn pmConn, BmoBankMovement bmoBankMovement, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "", propertyAddress = "";
		//Obtener la vivienda
		sql = " SELECT * FROM bankmovconcepts " +
				" LEFT JOIN raccounts ON (bkmc_raccountid = racc_raccountid) " +			  
				" LEFT JOIN orderproperties ON (racc_orderid = orpy_orderid) " +
				" LEFT JOIN properties ON (orpy_propertyid = prty_propertyid) " +
				" WHERE bkmc_bankmovementid = " + bmoBankMovement.getId();
		pmConn.doFetch(sql);
		if(pmConn.next()) {
			propertyAddress = pmConn.getString("properties", "prty_street");
			propertyAddress += " # " + pmConn.getString("properties", "prty_number");
		}

		return propertyAddress;
	}

	//Obtener Desarrollo
	private String  getDevelopmentName(PmConn pmConn, BmoBankMovement bmoBankMovement, BmUpdateResult bmUpdateResult) throws SFException {		
		String sql = "", deveName = "";
		//Obtener el desarrollo 
		sql = " SELECT * FROM bankmovconcepts " +
				" LEFT JOIN raccounts ON (bkmc_raccountid = racc_raccountid) " +			  
				" LEFT JOIN orderproperties ON (racc_orderid = orpy_orderid) " +
				" LEFT JOIN properties ON (orpy_propertyid = prty_propertyid) " +
				" LEFT JOIN developmentblocks ON (prty_developmentblockid = dvbl_developmentblockid) " +
				" LEFT JOIN developmentphases ON (dvbl_developmentphaseid = dvph_developmentphaseid) " +
				" LEFT JOIN developments ON (dvph_developmentid = deve_developmentid) " +
				" WHERE bkmc_bankmovementid = " + bmoBankMovement.getId();
		pmConn.doFetch(sql);
		if(pmConn.next()) {
			deveName = pmConn.getString("developments", "deve_name");
		}

		return deveName;
	}

	private int getSalesmanId(PmConn pmConn, BmoBankMovement bmoBankMovement, BmUpdateResult bmUpdateResult) throws SFException {		
		String sql = "";
		int salesmanId = 0;
		//Obtener el correo del vendedor
		sql = " SELECT * FROM bankmovconcepts " +
				" LEFT JOIN raccounts ON (bkmc_raccountid = racc_raccountid) " +			  
				" LEFT JOIN orders ON (racc_orderid = orde_orderid) " +		      
				" WHERE bkmc_bankmovementid = " + bmoBankMovement.getId();
		pmConn.doFetch(sql);
		if(pmConn.next()) {
			salesmanId = pmConn.getInt("orde_userid");
		}

		return salesmanId;
	}

	// Obtener el Saldo de la Orden de Compra
	private void deleteConcepts(PmConn pmConn, BmoBankMovement bmoBankMovement)
			throws SFException {
		pmConn.doUpdate("DELETE FROM bankmovconcepts WHERE bkmc_bankmovementid = "
				+ bmoBankMovement.getId());
	}

	// Eliminar la transferencia
	private void deleteTransferReceipt(PmConn pmConn,
			BmoBankMovement bmoBankMovement) throws SFException {
		// Elimina los conceptos
		deleteConcepts(pmConn, bmoBankMovement);

		// Elimina el mov de banco
		pmConn.doUpdate("DELETE FROM bankmovements WHERE bkmv_bankmovementid = "
				+ bmoBankMovement.getId());
	}
	
	//Valida si tiene documentos cargados
	private boolean getFiles(int programId, int foreignid) throws SFPmException {
		boolean result;
		String sql = "";
		PmConn pmConn = new PmConn(getSFParams());			
		sql = "SELECT * FROM sffiles WHERE file_programid = " + programId + " AND file_foreignid = " + foreignid;

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

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject,
			BmUpdateResult bmUpdateResult) throws SFException {
		bmoBankMovement = (BmoBankMovement) bmObject;

		PmBankMovType pmBankMovType = new PmBankMovType(getSFParams());
		BmoBankMovType bmoBankMovType = (BmoBankMovType) pmBankMovType.get(
				pmConn, bmoBankMovement.getBankMovTypeId().toInteger());

		// Validar que el MB no se haya creado de una cancelación
		if (bmoBankMovement.getBkmvCancelId().toInteger() > 0)
			bmUpdateResult.addError(bmoBankMovement.getStatus().getName(),
					"No se puede eliminar el " + bmoBankMovement.getCode() + " fue cancelado de otro MB, revise la Referencia/Descripción para obtener detalles.");

		// Validar que el MB esta cancelado no se pueda cambiar de estatus
		//Un Mov cancelado no se puede cambiar de estatus
		PmBankMovement pmBankMovementPrev = new PmBankMovement(getSFParams());
		BmoBankMovement bmoBankMovementPrev = (BmoBankMovement)pmBankMovementPrev.get(pmConn, bmoBankMovement.getId());

		if (bmoBankMovementPrev.getStatus().equals(BmoBankMovement.STATUS_CANCELLED)) {
			bmUpdateResult.addError(bmoBankMovement.getStatus().getName(), "El MB " + bmoBankMovement.getId() + " no se puede eliminar esta cancelado.");
		}

		else if (bmoBankMovement.getStatus().equals(BmoBankMovement.STATUS_RECONCILED)) {
			bmUpdateResult.addMsg("No se puede Eliminar: Está Conciliado.");
		} else {

			if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_TRANSFER)) {
				if (bmoBankMovType.getType().equals(BmoBankMovType.TYPE_WITHDRAW)) {
					// Eliminar los concepts de la cuenta que recibio el
					// traspaso
					PmBankMovement pmBankMovementForeign = new PmBankMovement(
							getSFParams());
					BmoBankMovement bmoBanMovementForeign = (BmoBankMovement) pmBankMovementForeign
							.getBy(pmConn, bmoBankMovement.getId(),	bmoBankMovement.getParentId().getName());

					if (bmoBanMovementForeign.getStatus().equals(BmoBankMovement.STATUS_REVISION)) {
						// Actualizar los saldos
						updateBkacBalanceDelete(pmConn, bmoBanMovementForeign,bmUpdateResult);
						// Borrar en mov de traspaso
						deleteTransferReceipt(pmConn, bmoBanMovementForeign);

					} else {
						bmUpdateResult.addError(bmoBankMovement.getBankAccoTransId().getName(),	"No Se Puede Eliminar La Transferencia Destino Su Estatus Esta Conciliado");
					}

					// pmBankMovementForeign.delete(bmoBanMovementForeign,
					// bmUpdateResult);

				} else {
					bmUpdateResult.addMsg("No se puede Eliminar: Debe Eliminar la Transferencia de Origen.");
				} 
			} else {
				// Validar que no existan conceptos

				if (!bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_LOANDISPOSAL) &&
						!(bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_DISPOSALFREE)) &&
						!(bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_DEPOSITFREE))) {
					if (sumConcepts(pmConn, bmoBankMovement) > 0)	
						bmUpdateResult.addMsg("No se puede Eliminar: Existen conceptos.");
				}	
			}

			// Solo si es transferencia actualizar el balance en la cta de banco
			if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_TRANSFER)
					|| (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE) 
							&& bmoBankMovType.getType().equals(BmoBankMovType.TYPE_WITHDRAW))) {
				updateBkacBalanceDelete(pmConn, bmObject, bmUpdateResult);
			}

			//ELiminar la disposicion del crédito 
			if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_LOANDISPOSAL)) {
				if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
					BmoBankMovConcept bmoBankMovConcept = new BmoBankMovConcept();
					PmBankMovConcept pmBankMovConcept = new PmBankMovConcept(getSFParams());
					bmoBankMovConcept = (BmoBankMovConcept)pmBankMovConcept.getBy(pmConn, bmoBankMovement.getId(), bmoBankMovConcept.getBankMovementId().getName());

					if  (bmoBankMovConcept != null) {

						BmoLoanDisbursement bmoLoanDisbursement = new BmoLoanDisbursement();
						PmLoanDisbursement pmLoanDisbursement = new PmLoanDisbursement(getSFParams());
						bmoLoanDisbursement = (BmoLoanDisbursement)pmLoanDisbursement.getBy(pmConn, bmoBankMovConcept.getId(), bmoLoanDisbursement.getBankMovConceptId().getName());
						//Antes de borrar la disposición se actualiza el registro quitandole la refencia del MB ya que no se puede eliminar si tiene un MB
						bmoLoanDisbursement.getBankMovConceptId().setValue("NULL");
						pmLoanDisbursement.saveSimple(pmConn, bmoLoanDisbursement,bmUpdateResult);
						
						pmLoanDisbursement.delete(pmConn, bmoLoanDisbursement, bmUpdateResult);

					}

					updateBkacBalanceDelete(pmConn, bmObject, bmUpdateResult);


				}
			} else if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_DISPOSALFREE)) {
				BmoBankMovConcept bmoBankMovConcept = new BmoBankMovConcept();
				PmBankMovConcept pmBankMovConcept = new PmBankMovConcept(getSFParams());
				bmoBankMovConcept = (BmoBankMovConcept)pmBankMovConcept.getBy(pmConn, bmoBankMovement.getId(), bmoBankMovConcept.getBankMovementId().getName());

				pmBankMovConcept.delete(pmConn, bmoBankMovConcept, bmUpdateResult);

				//Recualcular el presupuesto
				if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean() 
						&&  bmoBankMovement.getBudgetItemId().toInteger() > 0) {
					PmBudgetItem pmBudgetItem = new PmBudgetItem(getSFParams());
					BmoBudgetItem bmoBudgetItem = (BmoBudgetItem)pmBudgetItem.get(pmConn, bmoBankMovement.getBudgetItemId().toInteger());

					pmBudgetItem.updateBalance(pmConn, bmoBudgetItem, bmUpdateResult);

				}


			} else if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_DEPOSITFREE)) {
				BmoBankMovConcept bmoBankMovConcept = new BmoBankMovConcept();
				PmBankMovConcept pmBankMovConcept = new PmBankMovConcept(getSFParams());
				bmoBankMovConcept = (BmoBankMovConcept)pmBankMovConcept.getBy(pmConn, bmoBankMovement.getId(), bmoBankMovConcept.getBankMovementId().getName());

				pmBankMovConcept.delete(pmConn, bmoBankMovConcept, bmUpdateResult);
			}



			// Elimina los conceptos
			deleteConcepts(pmConn, bmoBankMovement);

			// elimina el moviento de banco
			super.delete(pmConn, bmObject, bmUpdateResult);

			// Actualizar el estatus de la OC
			if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)
					&& bmoBankMovType.getType().equals(	BmoBankMovType.TYPE_WITHDRAW)) {

//				PmRequisition pmRequisition = new PmRequisition(getSFParams());
//				BmoRequisition bmoRequisition = (BmoRequisition) pmRequisition.get(pmConn, bmoBankMovement.getRequisitionId().toInteger());
//				pmRequisition.updatePayments(pmConn, bmoRequisition,bmUpdateResult);
			} else  {

				if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_LOANDISPOSAL)) {
					PmBudgetItem pmBudgetItem = new PmBudgetItem(getSFParams());
					BmoBudgetItem bmoBudgetItem = (BmoBudgetItem)pmBudgetItem.get(pmConn, bmoBankMovement.getBudgetItemId().toInteger());

					pmBudgetItem.updateBalance(pmConn, bmoBudgetItem, bmUpdateResult);
				}
			}
		}

		return bmUpdateResult;
	}

	// Actualizar el balance de la cuenta bancaria
	private void updateBkacBalanceDelete(PmConn pmConn, BmObject bmObject, 	BmUpdateResult bmUpdateResult) throws SFException {
		bmoBankMovement = (BmoBankMovement) bmObject;

		PmBankMovType pmBankMovType = new PmBankMovType(getSFParams());
		BmoBankMovType bmoBankMovType = (BmoBankMovType) pmBankMovType.get(pmConn, bmoBankMovement.getBankMovTypeId().toInteger());

		double balanceNow = 0;
		double amountBkmv = 0;

		// Obtener la cantidad del concepto bancario
		if (bmoBankMovType.getType().equals(BmoBankMovType.TYPE_DEPOSIT)) {
			amountBkmv = bmoBankMovement.getDeposit().toDouble();
		} else {
			amountBkmv = (bmoBankMovement.getWithdraw().toDouble());
		}

		// Obtener el total la suma de los items
		if (!(amountBkmv > 0))
			amountBkmv = sumConcepts(pmConn, bmoBankMovement);

		//Obtener la Cuenta de Banco

		PmBankAccount pmBankAccount = new PmBankAccount(getSFParams());
		BmoBankAccount bmoBankAccount = (BmoBankAccount)pmBankAccount.get(pmConn, bmoBankMovement.getBankAccountId().toInteger());

		balanceNow = bmoBankAccount.getBalance().toDouble();


		// Actualizar el saldo en la cuenta de banco
		if (bmoBankMovType.getType().equals(BmoBankMovType.TYPE_DEPOSIT)) {
			balanceNow -= amountBkmv;
		} else {
			balanceNow += amountBkmv;
		}

		bmoBankAccount.getBalance().setValue(SFServerUtil.roundCurrencyDecimals(balanceNow));

		pmBankAccount.saveSimple(pmConn, bmoBankAccount, bmUpdateResult);

		this.updateAmount(pmConn, bmoBankMovement, bmUpdateResult);
	}
}
