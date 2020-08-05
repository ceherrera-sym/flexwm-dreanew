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
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.ListIterator;

import com.flexwm.server.PmCompanyNomenclature;
import com.flexwm.server.op.PmReqPayType;
import com.flexwm.server.wf.PmWFlow;
import com.flexwm.server.wf.PmWFlowLog;
import com.flexwm.server.wf.PmWFlowType;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoAssignCoordinator;
import com.flexwm.shared.cm.BmoAssignSalesman;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoCustomerCompany;
import com.flexwm.shared.cm.BmoCustomerContact;
import com.flexwm.shared.cm.BmoCustomerDate;
import com.flexwm.shared.cm.BmoCustomerStatus;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.cm.BmoReferral;
import com.flexwm.shared.cm.BmoTerritory;
import com.flexwm.shared.cr.BmoCredit;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoReqPayType;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowLog;
import com.symgae.server.HtmlUtil;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.RFCUtil;
import com.symgae.server.SFSendMail;
import com.symgae.server.SFServerUtil;
import com.symgae.server.sf.PmUser;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFMailAddress;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoUser;



public class PmCustomer extends PmObject {
	BmoCustomer bmoCustomer;

	public PmCustomer(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoCustomer = new BmoCustomer();
		setBmObject(bmoCustomer);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoCustomer.getSalesmanId(), bmoCustomer.getBmoUser()),
				new PmJoin(bmoCustomer.getBmoUser().getAreaId(), bmoCustomer.getBmoUser().getBmoArea()),
				new PmJoin(bmoCustomer.getBmoUser().getLocationId(), bmoCustomer.getBmoUser().getBmoLocation()),
				new PmJoin(bmoCustomer.getReqPayTypeId(),bmoCustomer.getBmoReqPayType()),
				new PmJoin(bmoCustomer.getMaritalStatusId(), bmoCustomer.getBmoMaritalStatus()),
				new PmJoin(bmoCustomer.getTerritoryId(), bmoCustomer.getBmoTerritory())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoCustomer bmoCustomer = (BmoCustomer) autoPopulate(pmConn, new BmoCustomer());

		// BmoUser
		BmoUser bmoUser = new BmoUser();
		if (pmConn.getInt(bmoUser.getIdFieldName()) > 0) 
			bmoCustomer.setBmoUser((BmoUser) new PmUser(getSFParams()).populate(pmConn));
		else 
			bmoCustomer.setBmoUser(bmoUser);

		// BmoTerritory
		BmoTerritory bmoTerritory = new BmoTerritory();
		if (pmConn.getInt(bmoTerritory.getIdFieldName()) > 0) 
			bmoCustomer.setBmoTerritory((BmoTerritory) new PmTerritory(getSFParams()).populate(pmConn));
		else 
			bmoCustomer.setBmoTerritory(bmoTerritory);

		// BmoTerritory
		BmoReqPayType bmoReqPayType = new BmoReqPayType();
		if (pmConn.getInt(bmoReqPayType.getIdFieldName()) > 0) 
			bmoCustomer.setBmoReqPayType((BmoReqPayType) new PmReqPayType(getSFParams()).populate(pmConn));
		else 
			bmoCustomer.setBmoReqPayType(bmoReqPayType);

		return bmoCustomer;
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";	


		if (getSFParams().restrictData(bmoCustomer.getProgramCode())) {
			int loggedUserId = getSFParams().getLoginInfo().getUserId();
			filters = "( cust_salesmanid in (" +
					" select user_userid from users " +
					" where " + 
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
					" )) ) " ;
			if (!((BmoFlexConfig)getSFParams().getBmoAppConfig()).getShowOwnCustomer().toBoolean()) {
				filters += " OR cust_status = '" + BmoCustomer.STATUS_INACTIVE + "'";
			}

		}

		// Filtro de clientes de empresas del usuario
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			if (filters.length() > 0) filters += " AND ";
			filters += " ( "
					+ "		( cust_customerid IN "
					+ "			(" 
					+ " 			SELECT cucp_customerid FROM customercompanies " 
					+ " 			WHERE cucp_companyid IN "
					+ "					("
					+ "					SELECT uscp_companyid FROM usercompanies"
					+ "					WHERE uscp_userid = " + getSFParams().getLoginInfo().getUserId()
					+ "					)"
					+ "			)"
					+ "		)"
					+ " OR"
					+ "		("
					+ "			NOT EXISTS (SELECT cucp_customercompanyid FROM customercompanies "
					+ "			WHERE cucp_customerid = cust_customerid)"
					+ "		)"
					+ ")";			
		}

		// Filtro de empresa seleccionada
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0) filters += " AND ";
			filters += " ( "
					+ "		( cust_customerid IN "
					+ "			("
					+ " 		SELECT cucp_customerid FROM customercompanies "
					+ " 		WHERE cucp_companyid = " + getSFParams().getSelectedCompanyId()
					+ "			)"
					+ "		)"
					+ " OR "
					+ "		( "
					+ "			NOT EXISTS (SELECT cucp_customercompanyid FROM customercompanies "
					+ "			WHERE cucp_customerid = cust_customerid)"
					+ "		)"
					+ ")";
		}

		return filters;
	}	

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		BmoCustomer bmoCustomer = (BmoCustomer)bmObject;
		BmoCustomer bmoCustomerPrev = new BmoCustomer();
		boolean newRecord = false;
		String passwordErrors = "";
		PmCompanyNomenclature pmCompanyNomenclature = new PmCompanyNomenclature(getSFParams());
		String code = "";
		int companyId = 0;

		if (bmoCustomer.getCustomercategory().toString().equals(""+BmoCustomer.CATEGORY_LESSEE)) {
			passwordErrors = SFServerUtil.validatePassword(bmoCustomer.getPassw().toString(), bmoCustomer.getPasswconf().toString());
		}

		bmoCustomer.getEmail().setValue(bmoCustomer.getEmail().toString().trim());
		bmoCustomer.getMobile().setValue(bmoCustomer.getMobile().toString().trim());

		// Validaciones de email y movil G100
		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getMobilOrMailCust().toBoolean()) {
			if (bmoCustomer.getEmail().toString().equals("") && bmoCustomer.getMobile().toString().equals("") ) {
				bmUpdateResult.addError(bmoCustomer.getEmail().getName(), "Complete este campo o el Móvil");
				bmUpdateResult.addError(bmoCustomer.getMobile().getName(), "Complete este campo o el Email");
			}
		}

		// Se almacena de forma preliminar para asignar ID
		if (!(bmoCustomer.getId() > 0)) {
			newRecord = true;

			// Validaciones de email y movil G100
			if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getMobilOrMailCust().toBoolean()) {
				if(validateMobile(bmoCustomer)) {
					bmUpdateResult.addError(bmoCustomer.getMobile().getName(), "El " + getSFParams().getFieldFormTitle(bmoCustomer.getMobile()) +
							" ya existe en otro cliente.");
				}
				if (validateEmail(bmoCustomer)) {
					bmUpdateResult.addError(bmoCustomer.getEmail().getName(), "El " + getSFParams().getFieldFormTitle(bmoCustomer.getEmail()) +
							" ya existe en otro cliente.");
				}
			}

			super.save(pmConn, bmoCustomer, bmUpdateResult);
			bmoCustomer.setId(bmUpdateResult.getId());

			//			bmoCustomer.getCode().setValue(bmoCustomer.getCodeFormat());

			// Genera la empresa default del cliente
			if (!((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCompanyInCustomer().toBoolean()) {
				companyId = getSFParams().getLoginInfo().getBmoUser().getCompanyId().toInteger();
				if (!(companyId > 0)) 
					companyId = getSFParams().getBmoSFConfig().getDefaultCompanyId().toInteger();
				if (getSFParams().getSelectedCompanyId() > 0)
					companyId = getSFParams().getSelectedCompanyId();				

				// Si esta asignada empresa, crear el primer registro
				if (companyId > 0) {
					BmoCustomerCompany bmoCustomerCompany = new BmoCustomerCompany();
					bmoCustomerCompany.getCompanyId().setValue(companyId);
					bmoCustomerCompany.getCustomerId().setValue(bmoCustomer.getId());
					PmCustomerCompany pmCustomerCompany = new PmCustomerCompany(getSFParams());
					pmCustomerCompany.save(pmConn, bmoCustomerCompany, bmUpdateResult);
				}
			}

			// Generar clave personalizada si la hay, si no retorna la de por defecto
			code = pmCompanyNomenclature.getCodeCustom(pmConn,
					companyId,
					bmoCustomer.getProgramCode().toString(),
					bmoCustomer.getId(),
					BmoCustomer.CODE_PREFIX
					);
			bmoCustomer.getCode().setValue(code);
		} else {
			bmoCustomerPrev = (BmoCustomer)this.get(bmoCustomer.getId());

			// Valida que no esten duplicados el movil y correo en OTRO cliente
			if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getMobilOrMailCust().toBoolean()) {
				if (!bmoCustomerPrev.getMobile().toString().equals(bmoCustomer.getMobile().toString())) {
					if(validateMobile(bmoCustomer)) {
						bmUpdateResult.addError(bmoCustomer.getMobile().getName(), "El " + getSFParams().getFieldFormTitle(bmoCustomer.getMobile()) + " ya existe en otro cliente.");
					}
				}
				if (!bmoCustomerPrev.getEmail().toString().equals(bmoCustomer.getEmail().toString())) {
					if (validateEmail(bmoCustomer)) {
						bmUpdateResult.addError(bmoCustomer.getEmail().getName(), "El " + getSFParams().getFieldFormTitle(bmoCustomer.getEmail()) + " ya existe en otro cliente.");
					}
				}
			}
		}

		//al cambiar vendedor solo cobi
		BmoCredit bmoCredit = new BmoCredit();
		if(getSFParams().hasRead(bmoCredit.getProgramCode())) {
			if(!newRecord) {
				//				bmoCustomerPrev = (BmoCustomer)this.get(bmoCustomer.getId());					
				if(bmoCustomer.getSalesmanId().toInteger() != bmoCustomerPrev.getSalesmanId().toInteger()){	
					printDevLog("Validar creditos" );
					if(customerOnCredit(pmConn,bmoCustomer.getId()))
						bmUpdateResult.addError(bmoCustomer.getSalesmanId().getName(), "No se puede cambiar el " 
								+ getSFParams().getFieldFormTitle(bmoCustomer.getProgramCode().toString(), bmoCustomer.getSalesmanId()) + ", el cliente "
								+ " tiene un crédito activo ");
					else if(customerCreditGuarinties(pmConn, bmoCustomer.getId()))
						bmUpdateResult.addError(bmoCustomer.getSalesmanId().getName(), "No se puede cambiar el " 
								+ getSFParams().getFieldFormTitle(bmoCustomer.getProgramCode().toString(), bmoCustomer.getSalesmanId()) + ", el cliente "
								+ " es aval en un crédito activo ");
				}		
			}	
		}

		// Establecer nombre a desplegar
		if (bmoCustomer.getCustomertype().toChar() == BmoCustomer.TYPE_PERSON){
			bmoCustomer.getDisplayName().setValue(bmoCustomer.getFirstname().toString() 
					+ " " + bmoCustomer.getFatherlastname().toString()
					+ " " + bmoCustomer.getMotherlastname().toString()); 

			// Generar RFC con fecha valida
			if (!bmoCustomer.getBirthdate().toString().equals("")) {
				int lengthDate = getSFParams().getDateFormat().length();
				if (bmoCustomer.getBirthdate().toString().length() == lengthDate) {
					if(bmoCustomer.getRfc().toString().equals("")) {
						RFCUtil rfcUtil = new RFCUtil();
						String rfc = rfcUtil.getGeneraRFC(bmoCustomer.getFirstname().toString(), bmoCustomer.getFatherlastname().toString(), bmoCustomer.getMotherlastname().toString(), bmoCustomer.getBirthdate().toString());
						bmoCustomer.getRfc().setValue(rfc);
					}
				} else {
					bmUpdateResult.addError(bmoCustomer.getBirthdate().getName(), "Formato de Fecha Invalido. Debe ser: " + getSFParams().getDateFormat());
				}
			}
		} else {
			// Si es de tipo Empresa, y no esta asignado el nombre del cliente, utilizar el nombre legal
			if (!(bmoCustomer.getDisplayName().toString().length() > 0))
				bmoCustomer.getDisplayName().setValue(bmoCustomer.getLegalname().toString()); 

			// Crear/Actualizar contacto principal
			BmoCustomerContact bmoCustomerContact = new BmoCustomerContact();
			PmCustomerContact pmCustomerContact = new PmCustomerContact(getSFParams());
			int contactMain = pmCustomerContact.getCustomerContactMain(bmoCustomer.getId());

			// Si existe un contacto principal obtenerlo
			if (contactMain > 0) {
				bmoCustomerContact = (BmoCustomerContact)pmCustomerContact.get(contactMain);
			} else {
				// Es nuevo
				if (newRecord)
					bmoCustomerContact.getContactMain().setValue(1);
				else {
					// Obtener el contacto si el mismo que el contacto del formulario del cliente(antes de la modificaion)
					int contactId = pmCustomerContact.customerContactByName(pmConn, 
							bmoCustomerPrev.getFirstname().toString(), 
							bmoCustomerPrev.getFatherlastname().toString(), 
							bmoCustomerPrev.getMotherlastname().toString(), bmoCustomer.getId());
					if (contactId > 0)
						bmoCustomerContact = (BmoCustomerContact)pmCustomerContact.get(contactId);
					else
						bmoCustomerContact.getContactMain().setValue(1); // Si no existe como contacto, lo crea como principal
					//						bmUpdateResult.addError(bmoCustomer.getFirstname().getName(), "Error: No se encontro el nombre en el catálogo de contactos.");
				}
			}

			bmoCustomerContact.getCustomerId().setValue(bmoCustomer.getId());
			bmoCustomerContact.getTitleId().setValue(bmoCustomer.getTitleId().toInteger());
			bmoCustomerContact.getFullName().setValue(bmoCustomer.getFirstname().toString());
			bmoCustomerContact.getFatherLastName().setValue(bmoCustomer.getFatherlastname().toString());
			bmoCustomerContact.getMotherLastName().setValue(bmoCustomer.getMotherlastname().toString());
			bmoCustomerContact.getNumber().setValue(bmoCustomer.getPhone().toString());
			bmoCustomerContact.getExtension().setValue(bmoCustomer.getExtension().toString());
			bmoCustomerContact.getCellPhone().setValue(bmoCustomer.getMobile().toString());
			bmoCustomerContact.getEmail().setValue(bmoCustomer.getEmail().toString());
			bmoCustomerContact.getPosition().setValue(bmoCustomer.getPosition().toString());
			pmCustomerContact.saveSimple(pmConn, bmoCustomerContact, bmUpdateResult);
		}

		// Buscar el tipo de wflow, toma el primero
		int wFlowTypeId = new PmWFlowType(getSFParams()).getFirstWFlowTypeId(pmConn, bmoCustomer.getProgramCode());
		if (!(wFlowTypeId > 0)) {
			bmUpdateResult.addError(bmoCustomer.getCode().getName(), "Debe crearse Tipo de WFlow para Clientes.");
		}

		// Crea el WFlow y asigna el ID recien creado, siempre va estar activo este flujo
		//BmoWFlow bmoWFlow = new BmoWFlow();
		PmWFlow pmWFlow = new PmWFlow(getSFParams());
		int wFlowId = 0;
		// Obtener flujo y validar que el cliente tenga flujo
		if (!newRecord) {
			pmConn.doFetch("SELECT wflw_wflowid FROM " + formatKind("wflows") 
			+ " WHERE wflw_callerid = " + bmoCustomer.getId()
			+ " AND wflw_callercode = '" + bmoCustomer.getProgramCode() +"';");

			if (pmConn.next()) wFlowId = pmConn.getInt("wflw_wflowid");
			if (!(wFlowId > 0)) bmUpdateResult.addError(bmoCustomer.getCode().getName(), "Debe crearse WFlow para el Cliente.");
		}

		wFlowId = pmWFlow.updateWFlow(pmConn, wFlowTypeId, wFlowId, 
				bmoCustomer.getProgramCode(), bmoCustomer.getId(), bmoCustomer.getUserCreateId().toInteger(), -1, bmoCustomer.getId(),
				bmoCustomer.getCode().toString(), bmoCustomer.getDisplayName().toString(), "", 
				SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()), 
				SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()), BmoWFlow.STATUS_ACTIVE, bmUpdateResult).getId();

		// Registrar bitacora si es nuevo
		if (newRecord && wFlowId > 0) {
			PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());
			pmWFlowLog.addLog(pmConn, bmUpdateResult, wFlowId, BmoWFlowLog.TYPE_WFLOW, "Creación de Cliente.");
		}

		if (passwordErrors.length() > 0) {
			bmUpdateResult.addError(bmoCustomer.getPassw().getName(), passwordErrors);
		}

		// Mandar correo al vendedor si es Lead, y si esta cambiando de vendedor
		if (newRecord && bmoCustomer.getLead().toInteger() > 0 && !bmUpdateResult.hasErrors()) {
			PmUser pmUser = new PmUser(getSFParams());
			BmoUser bmoUser = (BmoUser)pmUser.get(bmoCustomer.getSalesmanId().toInteger());
			// Ejecutar proceso de asignación del vendedor
			printDevLog("Ejecutar proceso de asignación del vendedor");
			assignSalesman(pmConn, bmoUser, bmUpdateResult);

			if (bmoCustomer.getSalesmanId().toInteger() != bmoCustomerPrev.getSalesmanId().toInteger()
					&& getSFParams().isProduction() && !bmUpdateResult.hasErrors()) {
				printDevLog("Enviar correo al vendedor y su coordinador");
				sendMailSalesman(bmoCustomer, bmoUser, wFlowId, pmConn, bmUpdateResult);
			}
		}

		// Actualizar id de claves del programa por empresa
		if (newRecord && !bmUpdateResult.hasErrors()) {
			pmCompanyNomenclature.updateConsecutiveByCompany(pmConn, companyId, 
					bmoCustomer.getProgramCode().toString());
		}

		super.save(pmConn, bmoCustomer, bmUpdateResult);

		return bmUpdateResult;
	}
	private boolean validateEmail(BmoCustomer bmoCustomer) throws SFPmException {
		String sql = "";
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();
		boolean result = false;
		if (!bmoCustomer.getEmail().toString().equals("")) {
			sql = "SELECT " + bmoCustomer.getIdFieldName() + " FROM " +
					bmoCustomer.getKind() + " WHERE " + bmoCustomer.getEmail().getName() + 
					" = '" + bmoCustomer.getEmail().toString() + "'";

			pmConn.doFetch(sql);
			if(pmConn.next())result = true;
		}
		pmConn.close();
		return result;

	}
	private boolean validateMobile(BmoCustomer bmoCustomer) throws SFPmException {
		String sql = "";
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();
		boolean result = false;
		if (!bmoCustomer.getMobile().toString().equals("")) {
			sql = "SELECT " + bmoCustomer.getIdFieldName() + " FROM " +
					bmoCustomer.getKind() + " WHERE " + bmoCustomer.getMobile().getName() + 
					" = '" + bmoCustomer.getMobile().toString() + "'";

			pmConn.doFetch(sql);
			if(pmConn.next())result = true;
		}
		pmConn.close();
		return result;
	}
	// Actualiza el estatus de todos los clientes
	public void batchUpdateStatus() throws SFException {
		PmConn pmConn = new PmConn(getSFParams());
		BmUpdateResult bmUpdateResult = new BmUpdateResult();
		pmConn.open();

		try {	
			BmoCustomer bmoCustomer = new BmoCustomer();
			Iterator<BmObject> customerIterator = this.list().iterator();

			while (customerIterator.hasNext()) {
				bmoCustomer = (BmoCustomer)customerIterator.next();
				this.updateStatus(pmConn, bmoCustomer, bmUpdateResult);
			}
		} catch (SFException e) {
			System.out.println(this.getClass().getName() + "batchUpdateStatus(): " + e.toString());
		} finally {
			pmConn.close();
		}
	}

	// Actualiza estatus del cliente por empresa segun los pedidos u oportunidades ligadas
	public void updateStatusByCompany(PmConn pmConn, BmoCustomer bmoCustomer, BmUpdateResult bmUpdateResult) throws SFException {
		this.bmoCustomer = bmoCustomer;
		String sql = "";
		int companyid = 0;

		boolean active = false; //Variable para saber si ya esta asignado el status 

		PmCustomerStatus pmCustomerStatus = new PmCustomerStatus(getSFParams());	
		PmConn pmConnCompany = new PmConn(getSFParams());
		try {
			pmConnCompany.open();

			sql = "SELECT distinct(orde_companyid) FROM orders WHERE "
					+ " orde_customerid = "+bmoCustomer.getId()
					+ " order by orde_companyid asc";
			//				System.out.println("Query: "+sql);		
			pmConnCompany.doFetch(sql);
			if(pmConnCompany.next()) {
				pmConnCompany.beforeFirst();
				while(pmConnCompany.next()) {
					BmoCustomerStatus bmoCustomerStatus = new BmoCustomerStatus();
					active = false;
					companyid = pmConnCompany.getInt("orde_companyid");
					bmoCustomerStatus.getCompanyId().setValue(companyid);
					//Busca si ya existe el registro del cliente con su empresa y carga bmoCustomerStatus else llena el objeto
					BmFilter filterByCustomer = new BmFilter();
					filterByCustomer.setValueFilter(bmoCustomerStatus.getKind(),bmoCustomerStatus.getCustomerId(),bmoCustomer.getId());
					BmFilter filterByCompany = new BmFilter();
					filterByCompany.setValueFilter(bmoCustomerStatus.getKind(),bmoCustomerStatus.getCompanyId(),companyid);

					ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
					filterList.add(filterByCustomer);
					filterList.add(filterByCompany);
					ListIterator<BmObject> customerStatusList = pmCustomerStatus.list(filterList).listIterator();
					if (customerStatusList.hasNext()) {						
						bmoCustomerStatus = (BmoCustomerStatus)customerStatusList.next();
					}
					else {
						bmoCustomerStatus.getCustomerId().setValue(bmoCustomer.getId());
						bmoCustomerStatus.getCompanyId().setValue(companyid);
						bmoCustomerStatus.getStatus().setValue(BmoCustomerStatus.STATUS_INACTIVE);
					}

					if(!active) {
						//Cliente Activo: Cuando tiene pedido con estatus diferente a Terminado
						sql = " SELECT COUNT(*) FROM orders  WHERE orde_customerid ="+  bmoCustomer.getId() +
								" AND orde_companyid = " + companyid +
								" AND orde_status <> '" + BmoOrder.STATUS_FINISHED+"'" +
								" AND orde_status <> '" + BmoOrder.STATUS_CANCELLED+ "' ";
						//						System.out.println("ACTIVO: "+sql);	
						pmConn.doFetch(sql);
						if (pmConn.next()) {
							if (pmConn.getInt(1) > 0) {
								bmoCustomerStatus.getStatus().setValue(BmoCustomerStatus.STATUS_ACTIVE);
								active = true;
							}
						}
					}
					if (!active) {
						// Cliente Inactivo: Cuando tiene pedido(s) en estatus Terminado, sin tener otro
						// pedido abierto
						sql = " SELECT COUNT(*) FROM orders"
								+ " WHERE orde_customerid =" + bmoCustomer.getId() 
								+ " AND orde_companyid = " + companyid
								+ " AND (orde_status = '"+ BmoOrder.STATUS_FINISHED + "' OR orde_status = '" + BmoOrder.STATUS_CANCELLED + "') "
								+ " AND  orde_status <>'"+ BmoOrder.STATUS_REVISION + "'"
								+ " AND  orde_status <>'"+ BmoOrder.STATUS_AUTHORIZED + "'"
								+ "";
						// System.out.println("INACTIVO: " +sql );
						pmConn.doFetch(sql);			
						if (pmConn.next()) {
							if (pmConn.getInt(1) > 0) {
								bmoCustomerStatus.getStatus().setValue(BmoCustomerStatus.STATUS_INACTIVE);
								active = true;
							}
						}
					}
					pmCustomerStatus.saveSimple(pmConn, bmoCustomerStatus, bmUpdateResult);
				}
			}
			else {
				sql = "SELECT distinct(oppo_companyid) FROM opportunities WHERE oppo_customerid = "+bmoCustomer.getId();
				//					System.out.println("Query: "+sql);		
				pmConnCompany.doFetch(sql);
				while(pmConnCompany.next()) {
					active = false;
					BmoCustomerStatus bmoCustomerStatus = new BmoCustomerStatus();
					companyid = pmConnCompany.getInt("oppo_companyid");
					bmoCustomerStatus.getCompanyId().setValue(companyid);
					//Busca si ya existe el registro del cliente con su empresa y carga bmoCustomerStatus else llena el objeto
					BmFilter filterByCustomer = new BmFilter();
					filterByCustomer.setValueFilter(bmoCustomerStatus.getKind(),bmoCustomerStatus.getCustomerId(),bmoCustomer.getId());
					BmFilter filterByCompany = new BmFilter();
					filterByCompany.setValueFilter(bmoCustomerStatus.getKind(),bmoCustomerStatus.getCompanyId(),companyid);

					ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
					filterList.add(filterByCustomer);
					filterList.add(filterByCompany);
					ListIterator<BmObject> customerStatusList = pmCustomerStatus.list(filterList).listIterator();
					if (customerStatusList.hasNext()) {						
						bmoCustomerStatus = (BmoCustomerStatus)customerStatusList.next();
					}
					else {
						bmoCustomerStatus.getCustomerId().setValue(bmoCustomer.getId());
						bmoCustomerStatus.getCompanyId().setValue(companyid);
						bmoCustomerStatus.getStatus().setValue(BmoCustomerStatus.STATUS_INACTIVE);
					}
					if (!active) {
						// Prospecto: tiene oportunidades 
						sql = " SELECT COUNT(*) FROM opportunities WHERE oppo_customerid = " + bmoCustomer.getId()
						+ " AND oppo_companyid = " + companyid
						+" AND oppo_status <> '"+BmoOpportunity.STATUS_WON +"'";
						pmConn.doFetch(sql);
						if (pmConn.next()) {
							if (pmConn.getInt(1) > 0) {
								bmoCustomerStatus.getStatus().setValue(BmoCustomerStatus.STATUS_PROSPECT);
								active = true;
							}
							else {
								sql = " SELECT COUNT(*) FROM opportunities WHERE oppo_customerid = " + bmoCustomer.getId();
								pmConn.doFetch(sql);
								if (pmConn.next()) {
									if (pmConn.getInt(1) == 0) {
										sql = " SELECT COUNT(*) FROM orders WHERE orde_customerid = " + bmoCustomer.getId();
										pmConn.doFetch(sql);
										if (pmConn.next()) {
											if (pmConn.getInt(1) == 0) {
												bmoCustomerStatus.getStatus().setValue(BmoCustomerStatus.STATUS_PROSPECT);
												active = true;
											}
										}
									}
								}
							}
						}
					}
					pmCustomerStatus.saveSimple(pmConn, bmoCustomerStatus, bmUpdateResult);

				}
			}

		}

		catch (SFException e) {
			bmUpdateResult.addError("", "Error al cambiar status del cliente: " + e);
		}
		finally {
			pmConnCompany.close();
		}
	}

	// Actualiza estatus del cliente segun los pedidos u oportunidades ligadas
	public void updateStatus(PmConn pmConn, BmoCustomer bmoCustomer, BmUpdateResult bmUpdateResult) throws SFException {
		this.bmoCustomer = bmoCustomer;
		String sql = "";
		boolean active = false;

		if(!active) {
			//Cliente Activo: Cuando tiene pedido con estatus diferente a Terminado
			sql = " SELECT COUNT(*) FROM orders  WHERE orde_customerid ="+  bmoCustomer.getId() +
					" AND orde_status <> '" + BmoOrder.STATUS_FINISHED+"'" +
					" AND orde_status <> '" + BmoOrder.STATUS_CANCELLED+ "' ";
			pmConn.doFetch(sql);
			if (pmConn.next()) {
				if (pmConn.getInt(1) > 0) {
					bmoCustomer.getStatus().setValue(BmoCustomer.STATUS_ACTIVE);
					active = true;
				}
			}
		}
		if (!active) {
			// Cliente Inactivo: Cuando tiene pedido(s) en estatus Terminado, sin tener otro
			// pedido abierto
			sql = " SELECT COUNT(*) FROM orders"
					+ " WHERE orde_customerid =" + bmoCustomer.getId() 
					+ " AND (orde_status = '"+ BmoOrder.STATUS_FINISHED + "' OR orde_status = '" + BmoOrder.STATUS_CANCELLED + "') "
					+ " AND  orde_status <>'"+ BmoOrder.STATUS_REVISION + "'"
					+ " AND  orde_status <>'"+ BmoOrder.STATUS_AUTHORIZED + "'"
					+ "";
			//			System.out.println("INACTIVO: " +sql );
			pmConn.doFetch(sql);			
			if (pmConn.next()) {
				if (pmConn.getInt(1) > 0) {
					bmoCustomer.getStatus().setValue(BmoCustomer.STATUS_INACTIVE);
					active = true;
				}
			}
		}
		if (!active) {
			// Prospecto: tiene oportunidades 
			sql = " SELECT COUNT(*) FROM opportunities WHERE oppo_customerid = " + bmoCustomer.getId()
			+" AND oppo_status <> '"+BmoOpportunity.STATUS_WON +"'";
			pmConn.doFetch(sql);
			if (pmConn.next()) {
				if (pmConn.getInt(1) > 0) {
					bmoCustomer.getStatus().setValue(BmoCustomer.STATUS_PROSPECT);
					active = true;
				}
				else {
					sql = " SELECT COUNT(*) FROM opportunities WHERE oppo_customerid = " + bmoCustomer.getId();
					pmConn.doFetch(sql);
					if (pmConn.next()) {
						if (pmConn.getInt(1) == 0) {
							sql = " SELECT COUNT(*) FROM orders WHERE orde_customerid = " + bmoCustomer.getId();
							pmConn.doFetch(sql);
							if (pmConn.next()) {
								if (pmConn.getInt(1) == 0) {
									bmoCustomer.getStatus().setValue(BmoCustomer.STATUS_PROSPECT);
									active = true;
								}
							}
						}
					}
				}
			}
		}
		super.save(pmConn, bmoCustomer, bmUpdateResult);

		if (!((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCompanyInCustomer().toBoolean()) {
			updateStatusByCompany(pmConn, bmoCustomer, bmUpdateResult);
		}
	}


	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {		
		int result = 0;

		//Obtener el saldo de la orden de compra
		if (action.equals(BmoCustomer.ACTION_SHOWOPPO)) {
			result = showOpportunities(Integer.parseInt(value), bmUpdateResult);
		} else if (action.equals(BmoCustomer.ACTION_SHOWPROJ)) {
			result = showProjects(Integer.parseInt(value), bmUpdateResult);
		}

		if(action.equals(BmoCustomer.ACTION_GETCUSTOMERCONTACT)) {
			PmConn pmConn = new PmConn(getSFParams());
			pmConn.open();

			String sql = " SELECT  cuco_customercontactid FROM customercontacts LEFT JOIN customers ON"
					+ " (cust_customerid = cuco_customerid) WHERE cust_customerid = "+value+"   AND cuco_contactmain = 1 ";
			pmConn.doFetch(sql);
			if(pmConn.next()) {
				result = pmConn.getInt("cuco_customercontactid");
			}


		}

		bmUpdateResult.setMsg("" + result);

		return bmUpdateResult;
	}


	private int showOpportunities(int customerId, BmUpdateResult bmUpdateResult) throws SFPmException {
		String sql="";
		int result = 0;
		PmConn pmConn = null;

		try {
			pmConn = new PmConn(getSFParams());
			pmConn.open();	

			sql = " SELECT COUNT(*) FROM opportunities " +
					" WHERE oppo_customerid = " + customerId;			
			pmConn.doFetch(sql);
			System.out.println("sql " + sql);
			if (pmConn.next()) result = pmConn.getInt(1);

		} catch (SFPmException e) {
			pmConn.close();
		} finally {
			pmConn.close();
		}

		return result;
	}

	private int showProjects(int customerId, BmUpdateResult bmUpdateResult) throws SFPmException {
		String sql="";
		int result = 0;
		PmConn pmConn = null;

		try {
			pmConn = new PmConn(getSFParams());
			pmConn.open();	

			sql = " SELECT COUNT(*) FROM projects " +
					" WHERE proj_customerid = " + customerId;			
			pmConn.doFetch(sql);
			if (pmConn.next()) result = pmConn.getInt(1);

		} catch (SFPmException e) {
			pmConn.close();
		} finally {
			pmConn.close();
		}

		return result;
	}

	// Enviar recordatorios de cumpleaños y fechas importantes del cliente
	public void prepareDayReminders() throws SFException {
		BmoCustomer bmoCustomer = new BmoCustomer();
		PmUser pmUser = new PmUser(getSFParams());

		// Si esta activado en la configuracion, enviar correo de clientes
		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEmailCustomerBirthday().toBoolean()) {

			// Establecer la fecha
			Date date = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int month = cal.get(Calendar.MONTH) + 1;
			int day = cal.get(Calendar.DAY_OF_MONTH);

			// Obtener los cumpleaños del día
			BmFilter filterByDay = new BmFilter();
			filterByDay.setDayMonthFilter(bmoCustomer.getKind(), bmoCustomer.getBirthdate().getName(), bmoCustomer.getBirthdate().getName(), "" + month, "" + day);

			Iterator<BmObject> customerIterator = this.list(filterByDay).iterator();

			while (customerIterator.hasNext()) {
				bmoCustomer = (BmoCustomer)customerIterator.next();
				// Si es cliente personal, enviar correo al vendedor (si este esta activo)
				if (bmoCustomer.getCustomertype().toChar() == BmoCustomer.TYPE_PERSON) {
					BmoUser bmoUser = (BmoUser)pmUser.get(bmoCustomer.getSalesmanId().toInteger());
					if (bmoUser.getStatus().toChar() == BmoUser.STATUS_ACTIVE)
						sendMailReminder(bmoCustomer, bmoUser, "Cumpleaños", "", "");
				}
			}

			// Obtener las fechas especiales del día que tengan activada la notificación
			BmoCustomerDate bmoCustomerDate = new BmoCustomerDate();
			BmFilter filterEmailReminder = new BmFilter();
			//filterSpecialDateByDay.setDayMonthFilter(bmoCustomer.getKind(), bmoCustomerDate.getRelevantDate().getName(), bmoCustomerDate.getRelevantDate().getName(), "" + month, "" + day);
			filterEmailReminder.setValueFilter(bmoCustomerDate.getKind(), bmoCustomerDate.getEmailReminder().getName(), 1);

			PmCustomerDate pmCustomerDate = new PmCustomerDate(getSFParams());
			Iterator<BmObject> customerDateIterator = pmCustomerDate.list(filterEmailReminder).iterator();
			while (customerDateIterator.hasNext()) {
				bmoCustomerDate = (BmoCustomerDate)customerDateIterator.next();

				// Le quito los dias
				String  previousDate = SFServerUtil.addDays(getSFParams().getDateFormat(), 
						bmoCustomerDate.getRelevantDate().toString(), 
						-bmoCustomerDate.getRemindDate().toInteger());

				Calendar calDatePrev = SFServerUtil.stringToCalendar(getSFParams().getDateFormat(), previousDate);
				int monthDatePrev = calDatePrev.get(Calendar.MONTH) + 1;
				int dayDatePrev = calDatePrev.get(Calendar.DAY_OF_MONTH);

				Calendar calEspecialDate = SFServerUtil.stringToCalendar(getSFParams().getDateFormat(), bmoCustomerDate.getRelevantDate().toString());
				int monthEspecialDate = calEspecialDate.get(Calendar.MONTH) + 1;
				int dayEspecialDate = calEspecialDate.get(Calendar.DAY_OF_MONTH);

				Calendar calNow = SFServerUtil.stringToCalendar(getSFParams().getDateFormat(), SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
				int monthNow = calNow.get(Calendar.MONTH) + 1;
				int dayNow = calNow.get(Calendar.DAY_OF_MONTH);

				// Valida que el mes y el dia, sea igual a la fecha previa
				// Valida que el mes y el dia, sea igual a hoy
				if ((monthDatePrev == monthNow && dayDatePrev == dayNow)
						|| (monthEspecialDate == monthNow && dayEspecialDate == dayNow)) {

					String motive = "Otros", comments = "", relevantDate = "";
					if (bmoCustomerDate.getType().toChar() == BmoCustomerDate.TYPE_ANNIVERSARY) motive = "Aniversario";
					else if (bmoCustomerDate.getType().toChar() == BmoCustomerDate.TYPE_BIRTHDAY) motive = "Cumpleaños";

					comments = bmoCustomerDate.getDescription().toString();
					relevantDate = bmoCustomerDate.getRelevantDate().toString();

					// Si es cliente personal, enviar correo al vendedor(si este esta activo)
					bmoCustomer = (BmoCustomer)this.get(bmoCustomerDate.getCustomerId().toInteger());
					BmoUser bmoUser = (BmoUser)pmUser.get(bmoCustomer.getSalesmanId().toInteger());
					if (bmoUser.getStatus().toChar() == BmoUser.STATUS_ACTIVE)
						sendMailReminder(bmoCustomer, bmoUser, motive, comments, relevantDate);

				}
			}
		}
	}

	public void sendMailReminder(BmoCustomer bmoCustomer, BmoUser bmoUser, String motive, String comments, String dateSpecial) throws SFException {
		String subject = getSFParams().getAppCode() + " Recordatorio de " + motive + " del Cliente " + bmoCustomer.getCode() + " " + bmoCustomer.getDisplayName();

		String msgBody = HtmlUtil.mailBodyFormat(getSFParams(), subject, 
				" Recordatorio de"  + ((!dateSpecial.equals("")) ? " Fecha Importante: " : ": ")  + motive +
				" del Cliente " + bmoCustomer.getCode() + " " + bmoCustomer.getDisplayName() +	
				((!dateSpecial.equals("")) ? 
						" para el día de: " + dateSpecial + "." +
						" Comentario: " + comments + "."
						: 
							" para el día de hoy: " + SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()) + "."
						));

		// Volver a validar si el usuario esta activo
		if (bmoUser.getStatus().toChar() == BmoUser.STATUS_ACTIVE) {
			SFSendMail.send(getSFParams(),
					bmoUser.getEmail().toString(), bmoUser.getFirstname() + " " + bmoUser.getFatherlastname(),  
					getSFParams().getBmoSFConfig().getEmail().toString(), 
					getSFParams().getBmoSFConfig().getAppTitle().toString(), 
					subject, 
					msgBody);
		} else 
			printDevLog("Usuario inactivo: "+bmoUser.getCode());
	}

	// Validar el NSS(INE)
	public void checkNSS(PmConn pmConn, BmoCustomer bmoCustomer, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		sql = " SELECT * FROM customers " +
				" WHERE cust_nss = " + bmoCustomer.getNss().toString() +
				" AND cust_customerid <> " + bmoCustomer.getId();
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			bmUpdateResult.addError("cred_customerid", "El " + getSFParams().getFieldFormTitle(bmoCustomer.getProgramCode(), bmoCustomer.getNss()) +" esta registrado en el cliente " + pmConn.getString("cust_code"));
		}
	}

	public int getCustomerByNameContact(PmConn pmConn, String firstName, String fatherLastName, String motherLastName, int customerId) throws SFPmException {
		int contact = -1;
		pmConn.doFetch("SELECT cust_customerid FROM customers " +
				" WHERE cust_firstname = '" + firstName + "' " + 
				" AND cust_fatherlastname = '" + fatherLastName + "' " +
				((motherLastName.equals("")) ? "" : 
					" AND cust_motherlastname =  '" + motherLastName + "' ") +
				((customerId > 0) ? " AND cust_customerid = " + customerId : "")
				);

		if (pmConn.next())
			contact = pmConn.getInt("cust_customerid");

		return contact;
	}

	// Enviar correo al vendedor de  Asignacion del cliente
	public void sendMailSalesman(BmoCustomer bmoCustomer, BmoUser bmoUser, int wFlowId, PmConn pmConn, BmUpdateResult bmUpdateResult) throws SFException {
		String subject = getSFParams().getAppCode() + " Recordatorio de asignación del Cliente " + bmoCustomer.getCode() + " " + bmoCustomer.getDisplayName();

		// Obtener referencia si existe
		BmoReferral bmoReferral = new BmoReferral();
		if (bmoCustomer.getReferralId().toInteger() > 0) {
			PmReferral pmReferral = new PmReferral(getSFParams());
			bmoReferral = (BmoReferral)pmReferral.get(bmoCustomer.getReferralId().toInteger());
		}

		ArrayList<SFMailAddress> mailList = new ArrayList<SFMailAddress>();
		ArrayList<SFMailAddress> mailListNoRepeat = new ArrayList<SFMailAddress>();
		String emails = "", emailsFail = "";

		SFMailAddress mail = new SFMailAddress(bmoUser.getEmail().toString(), bmoUser.getFirstname() + " " + bmoUser.getFatherlastname());
		mailList.add(mail);
		emails +=  bmoUser.getEmail().toString();

		if (bmoUser.getParentId().toInteger() > 0) {
			BmoUser bmoUserParent = new BmoUser();
			PmUser pmUser = new PmUser(getSFParams());
			bmoUserParent = (BmoUser)pmUser.get(pmConn, bmoUser.getParentId().toInteger());

			if (bmoUserParent.getStatus().equals("" + BmoUser.STATUS_ACTIVE)) {
				SFMailAddress mailParent = new SFMailAddress(bmoUserParent.getEmail().toString(), bmoUserParent.getFirstname() + " " + bmoUserParent.getFatherlastname());
				mailList.add(mailParent);
				emails += " ;" + bmoUserParent.getEmail().toString();
			}

			// Quitar emails repetidos
			for (SFMailAddress event : mailList) {
				boolean isFound = false;
				// Revisar si el email existe en noRepeat
				for (SFMailAddress e : mailListNoRepeat) {
					if (e.getEmail().equals(event.getEmail()) || (e.equals(event))) {
						isFound = true;        
						break;
					}
				}
				// Si no encontro ninguno añadirlo a la nueva lista
				if (!isFound) mailListNoRepeat.add(event);
			}
		}

		String msgBody = HtmlUtil.mailBodyFormat(getSFParams(), "Asignaci&oacute;n del Cliente", 
				"	<b>Cliente:</b> " + bmoCustomer.getCode().toString() + " "
						+ bmoCustomer.getDisplayName().toString() + ""
						+ ((bmoCustomer.getCustomertype().equals(BmoCustomer.TYPE_COMPANY))
								? " (" + bmoCustomer.getLegalname().toString() + ")"
										: "")
						+ "	<br> " + "	<b>Correo del cliente:</b> " + bmoCustomer.getEmail().toString() 
						+ "	<br> " + "	<b>M&oacute;vil del cliente:</b> " + bmoCustomer.getMobile().toString() 				
						+ "	<br> " + "	<b>Tel&eacute;fono:</b> " + bmoCustomer.getPhone().toString() 
						+ "	<br> " + "	<b>Vendedor:</b> " + bmoUser.getFirstname().toString() + " " + bmoUser.getFatherlastname().toString() + " " + bmoUser.getMotherlastname().toString() +
						((bmoCustomer.getReferralId().toInteger() > 0) ? 
								"	<br> " + "	<b>Referencia:</b> " + bmoReferral.getName().toString() : "")  );

		// Volver a validar si el usuario esta activo
		if (bmoUser.getStatus().toChar() == BmoUser.STATUS_ACTIVE 
				&& getSFParams().isProduction() 
				&& !bmUpdateResult.hasErrors()) {
			boolean error = false;
			try {
				printDevLog("Por enviar correo al vendedor y al coordinador");
				SFSendMail.send(getSFParams(),
						mailListNoRepeat,  
						getSFParams().getBmoSFConfig().getEmail().toString(), 
						getSFParams().getBmoSFConfig().getAppTitle().toString(), 
						subject, 
						msgBody);
			} catch (Exception e) {
				error = true;
				bmUpdateResult.addError(bmoCustomer.getLead().getName(), "Error al enviar correo al Vendedor.");

				// Obtener correos a los cuales no se enviaron, y si llego a quitar alguno repetido
				for (SFMailAddress event : mailList) {
					emailsFail += event.getEmail() + ";";
				}
				emailsFail += "|ListadoSinRepetir:";
				for (SFMailAddress eventNoRepeat : mailListNoRepeat) {
					emailsFail += eventNoRepeat.getEmail() + ";";
				}

				// Mandar correo para revision
				subject = getSFParams().getAppCode() + " ERROR en Recordatorio de asignación del Cliente " + bmoCustomer.getCode() + " " + bmoCustomer.getDisplayName();
				msgBody += 	" <br> " + ""
						+"	<br> Detalle Error: " + e.toString()
						+ " <br> Emails fallidos: " + emailsFail;

				if (getSFParams().isProduction()) {
					SFSendMail.send(getSFParams(),
							"smuniz@visualmexico.com.mx", "Saul Muñiz",
							getSFParams().getBmoSFConfig().getEmail().toString(), 
							getSFParams().getBmoSFConfig().getAppTitle().toString(), 
							subject, 
							msgBody);
				}
			}

			if (!error) {
				// Crear bitacora wflow
				if (wFlowId > 0) {
					PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());
					String comments = " Para: " + emails + "\n" + 
							" Asunto: Recordatorio de asignación del Cliente";
					pmWFlowLog.addDataLog(pmConn, bmUpdateResult, wFlowId, BmoWFlowLog.TYPE_EMAIL, comments, "");
				}
			}

		} else 
			printDevLog("Usuario inactivo/No es produccion: "+bmoUser.getCode());
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoCustomer = (BmoCustomer)bmObject;

		// Eliminar datos adicionales
		// Empresas
		PmCustomerCompany pmCustomerCompany = new PmCustomerCompany(getSFParams());
		BmoCustomerCompany bmoCustomerCompany = new BmoCustomerCompany();
		BmFilter filterByCustomer = new BmFilter();
		filterByCustomer.setValueFilter(bmoCustomerCompany.getKind(), bmoCustomerCompany.getCustomerId(), bmoCustomer.getId());
		ListIterator<BmObject> customerCompanyList = pmCustomerCompany.list(pmConn, filterByCustomer).listIterator();
		while (customerCompanyList.hasNext()) {
			bmoCustomerCompany = (BmoCustomerCompany)customerCompanyList.next();
			pmCustomerCompany.delete(pmConn, bmoCustomerCompany, bmUpdateResult);
		}

		// Contactos
		PmCustomerContact pmCustomerContact = new PmCustomerContact(getSFParams());
		BmoCustomerContact bmoCustomerContact = new BmoCustomerContact();
		filterByCustomer = new BmFilter();
		filterByCustomer.setValueFilter(bmoCustomerContact.getKind(), bmoCustomerContact.getCustomerId(), bmoCustomer.getId());
		ListIterator<BmObject> customerContactList = pmCustomerContact.list(pmConn, filterByCustomer).listIterator();
		while (customerContactList.hasNext()) {
			bmoCustomerContact = (BmoCustomerContact)customerContactList.next();
			super.delete(pmConn, bmoCustomerContact, bmUpdateResult);
			//			pmCustomerContact.delete(pmConn, bmoCustomerContact, bmUpdateResult);
		}

		// Eliminar flujos
		PmWFlow pmWFlow = new PmWFlow(getSFParams());
		BmoWFlow bmoWFlow = new BmoWFlow();
		BmFilter filterByCust = new BmFilter();
		filterByCust.setValueFilter(bmoWFlow.getKind(), bmoWFlow.getCallerId(), bmoCustomer.getId());			
		BmFilter filterByProgram = new BmFilter();
		filterByProgram.setValueFilter(bmoWFlow.getKind(), bmoWFlow.getCallerCode(), bmoCustomer.getProgramCode());
		ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
		filterList.add(filterByCust);
		filterList.add(filterByProgram);
		ListIterator<BmObject> wFlowList = pmWFlow.list(filterList).listIterator();
		while (wFlowList.hasNext()) {
			bmoWFlow = (BmoWFlow)wFlowList.next();
			pmWFlow.delete(pmConn, bmoWFlow, bmUpdateResult);
		}

		// Eliminar cliente
		super.delete(pmConn, bmoCustomer, bmUpdateResult);

		return bmUpdateResult;
	}
	public boolean customerOnCredit(PmConn pmConn,int customerId) throws SFPmException {

		String sql = "SELECT cred_creditid FROM credits WHERE cred_customerid = " + customerId +" AND "
				+ " (cred_status = '" + BmoCredit.STATUS_AUTHORIZED + "' OR cred_status = '" + BmoCredit.STATUS_REVISION + "')";

		pmConn.doFetch(sql);
		if(pmConn.next())
			return true;
		else
			return false;
	}
	public boolean customerCreditGuarinties(PmConn pmConn,int customerId) throws SFPmException {
		String sql = "SELECT crgu_creditguaranteesid FROM creditguarantees "
				+ " LEFT JOIN credits ON (crgu_creditid  = cred_creditid) WHERE crgu_customerid = " + customerId  
				+ " AND (cred_status = '" + BmoCredit.STATUS_REVISION + "' OR cred_status = '" + BmoCredit.STATUS_AUTHORIZED + "')";
		pmConn.doFetch(sql);
		if(pmConn.next())
			return true;
		else
			return false;

	}

	// Valida datos en el cliente
	public boolean validateDataCustomer(PmConn pmConn,int customerid, BmField bmField, BmUpdateResult bmUpdateResult) throws SFException {

		BmoCustomer bmoCustomer = new BmoCustomer();
		PmCustomer pmCustomer = new PmCustomer(getSFParams());

		boolean result = true;
		if(customerid > 0) {
			bmoCustomer = (BmoCustomer)pmCustomer.get(customerid);
			if(getSFParams().isFieldRequired(bmoCustomer.getReferralId()) && !(bmoCustomer.getReferralId().toInteger() > 0 )) {
				result = false;
				bmUpdateResult.addError(bmoCustomer.getProgramCode().toLowerCase() + bmField.getValue(), "<b>El campo " + getSFParams().getFieldFormTitle(bmoCustomer.getProgramCode(), bmoCustomer.getReferralId()) + " está vacío en el cliente.</b>");
			}
		}
		return result;
	}

	// Firmar asignacion en vendedor y coordinador
	public void assignSalesman(PmConn pmConn, BmoUser bmoUser, BmUpdateResult bmUpdateResult) throws SFException {
		BmoAssignSalesman bmoAssignSalesman = new BmoAssignSalesman();
		PmAssignSalesman pmAssignSalesman = new PmAssignSalesman(getSFParams());
		BmoAssignCoordinator bmoAssignCoordinator = new BmoAssignCoordinator();
		PmAssignCoordinator pmAssignCoordinator = new PmAssignCoordinator(getSFParams());
		int companyId = getSFParams().getSelectedCompanyId();
		// El usuario existe en la tabla de asignaciones de vendedores
		boolean existAssignSalesman = pmAssignSalesman.existSalesmanEnable(pmConn, bmoUser.getId());		

		if (bmoUser.getId() > 0 && existAssignSalesman)  {
			bmoAssignSalesman = (BmoAssignSalesman)pmAssignSalesman.getBy(pmConn, bmoUser.getId(), bmoAssignSalesman.getUserId().getName());

			printDevLog("ID-Registro asignacionVendeor:"+bmoAssignSalesman.getId());
			printDevLog("ID-Vendedor:"+bmoAssignSalesman.getUserId() + " - "+bmoAssignSalesman.getBmoUser().getCode());

			// Firmar que ya paso sobre este vendedor y coordinador
			if (!bmUpdateResult.hasErrors()) {
				bmoAssignCoordinator = (BmoAssignCoordinator)pmAssignCoordinator.get(pmConn, bmoAssignSalesman.getAssingCoordinatorId().toInteger());
				// Validar que concuerden las empresas
				if (bmoAssignCoordinator.getCompanyId().toInteger() != companyId)
					bmUpdateResult.addError(bmoCustomer.getLead().getName(), "La Empresa del coordinador(usuario) no coincide con la Empresa maestra.");

				if (!bmUpdateResult.hasErrors()) {
					printDevLog("Guarda 1ero el coordinador \n" +
							"Coordinador:"+bmoAssignCoordinator.getUserId() + " - "+bmoAssignCoordinator.getBmoUser().getCode());

					bmoAssignCoordinator.getAssigned().setValue(1);
					bmoAssignCoordinator.getCountAssigned().setValue(bmoAssignCoordinator.getCountAssigned().toInteger() + 1);
					pmAssignCoordinator.save(pmConn, bmoAssignCoordinator, bmUpdateResult);

					if (!bmUpdateResult.hasErrors()) {
						printDevLog("En 2do Guarda el vendedor");
						bmoAssignSalesman.getAssigned().setValue(1);
						bmoAssignSalesman.getCountAssigned().setValue(bmoAssignSalesman.getCountAssigned().toInteger() + 1);
						// Si se asigna le toca su turno, entonces se quita que esta en espera
						bmoAssignSalesman.getQueuedUser().setValue(0);
						pmAssignSalesman.save(pmConn, bmoAssignSalesman, bmUpdateResult);

					} else bmUpdateResult.addError(bmoCustomer.getLead().getName(), "Error al asingar Lead.");
				}
			} else bmUpdateResult.addError(bmoCustomer.getLead().getName(), "Error al asingar Lead.");
		}

		// Si no existen errores actualizar datos una vez guardado el cliente
		if (!bmUpdateResult.hasErrors() && existAssignSalesman) {
			pmAssignCoordinator.assignmentUpdate(pmConn, companyId, bmUpdateResult);
		}
	}
}
