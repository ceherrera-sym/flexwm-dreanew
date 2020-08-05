/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.co;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import com.flexwm.server.FlexUtil;
import com.flexwm.server.fi.PmBankAccount;
import com.flexwm.server.fi.PmBankMovConcept;
import com.flexwm.server.fi.PmBankMovement;
import com.flexwm.server.fi.PmPaccount;
import com.flexwm.server.fi.PmRaccount;
import com.flexwm.server.op.PmOrder;
import com.flexwm.server.op.PmRequisition;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.ar.BmoPropertyRental;
import com.flexwm.shared.co.BmoDevelopmentBlock;
import com.flexwm.shared.co.BmoDevelopmentPhase;
import com.flexwm.shared.co.BmoProperty;
import com.flexwm.shared.co.BmoPropertyModel;
import com.flexwm.shared.co.BmoPropertySale;
import com.flexwm.shared.fi.BmoBankAccount;
import com.flexwm.shared.fi.BmoBankMovConcept;
import com.flexwm.shared.fi.BmoBankMovement;
import com.flexwm.shared.fi.BmoPaccount;
import com.flexwm.shared.fi.BmoRaccount;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.op.BmoRequisition;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;


public class PmProperty extends PmObject{
	BmoProperty bmoProperty;


	public PmProperty(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoProperty = new BmoProperty();
		setBmObject(bmoProperty); 

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoProperty.getDevelopmentBlockId(), bmoProperty.getBmoDevelopmentBlock()),
				new PmJoin(bmoProperty.getBmoDevelopmentBlock().getDevelopmentPhaseId(), bmoProperty.getBmoDevelopmentBlock().getBmoDevelopmentPhase())
				)));
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";

		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getMultiCompany().toBoolean()) {
			if (getSFParams().restrictData(new BmoCompany().getProgramCode()))  {
				filters = " ("+
						" prty_companyid IN " +
						"					(" +
						" 						SELECT uscp_companyid FROM usercompanies where uscp_userid = " + getSFParams().getLoginInfo().getUserId() +
						" 					) " + 
						" )";

			}

			if(getSFParams().getSelectedCompanyId() > 0) {
				if (filters.length() > 0)filters += " AND ";
				filters += " prty_companyid = " + getSFParams().getSelectedCompanyId();
			}
		}

		return filters;
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoProperty = (BmoProperty)autoPopulate(pmConn, new BmoProperty());	

		// BmoDevelopmentBlock
		BmoDevelopmentBlock bmoDevelopmentBlock = new BmoDevelopmentBlock();
		if ((int)pmConn.getInt(bmoDevelopmentBlock.getIdFieldName()) > 0) 
			bmoProperty.setBmoDevelopmentBlock((BmoDevelopmentBlock) new PmDevelopmentBlock(getSFParams()).populate(pmConn));
		else 
			bmoProperty.setBmoDevelopmentBlock(bmoDevelopmentBlock);

		return bmoProperty;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoProperty bmoProperty = (BmoProperty)bmObject;		
		BmoProperty bmoPropertyPrevious = new BmoProperty();

		int propertymodelid = 0;
		String sql = "";
		boolean isLease = false;

		// Se almacena de forma preliminar para asignar ID
		if (!(bmoProperty.getId() > 0)) {
			super.save(pmConn, bmoProperty, bmUpdateResult);
			bmoProperty.setId(bmUpdateResult.getId());
			propertymodelid = bmoProperty.getPropertyModelId().toInteger();

		}
		else {
			PmProperty pmProperty = new PmProperty(getSFParams());
			bmoPropertyPrevious = (BmoProperty) pmProperty.get(bmoProperty.getId());
			propertymodelid = bmoPropertyPrevious.getPropertyModelId().toInteger();		


		}				
		BmoPropertyModel bmoPropertyModelType = new BmoPropertyModel();
		PmPropertyModel pmPropertyModelType = new PmPropertyModel(getSFParams());
		bmoPropertyModelType = (BmoPropertyModel)pmPropertyModelType.get(bmoProperty.getPropertyModelId().toInteger());
		if(bmoPropertyModelType.getBmoPropertyType().getCopyTag().toBoolean()) {
			copyTags(pmConn, bmoProperty);
		}

		//Saber si el inmueble es de arrendamiento 		
		sql = "SELECT ortp_type FROM propertymodels" + 
				" LEFT JOIN propertytypes on   ptyp_propertytypeid = ptym_propertytypeid " + 
				" LEFT JOIN ordertypes on ortp_ordertypeid = ptyp_ordertypeid " + 
				" WHERE ptym_propertymodelid = " + propertymodelid;

		pmConn.doFetch(sql);
		if(pmConn.next()) {
			if(pmConn.getString("ortp_type").equals(""+ BmoOrderType.TYPE_LEASE)) {
				isLease = true;
			}
		}




		//Si ubo un cambio de empresa 	
		if (bmoPropertyPrevious.getCompanyId().toInteger() != bmoProperty.getCompanyId().toInteger()) {
			// Se cambia la empresa en venta
			PmPropertySale pmPropertySale = new PmPropertySale(getSFParams());
			BmoPropertySale bmoPropertySale = new BmoPropertySale();

			ArrayList<BmFilter> filterListD = new ArrayList<BmFilter>();
			BmFilter filterProperty = new BmFilter();
			BmFilter filterStatus = new BmFilter();

			filterProperty.setValueFilter(bmoPropertySale.getKind(), bmoPropertySale.getPropertyId().getName(), bmoProperty.getId());
			filterListD.add(filterProperty);
			filterStatus.setValueOperatorFilter(bmoPropertySale.getKind(), bmoPropertySale.getStatus(), BmFilter.NOTEQUALS, "" + BmoPropertySale.STATUS_CANCELLED);
			filterListD.add(filterStatus);
			// buscamos su venta del inmueble diferente a cancelada
			Iterator<BmObject> listPropertySale = new PmPropertySale(getSFParams()).list(pmConn, filterListD).iterator();

			while (listPropertySale.hasNext()) {
				bmoPropertySale = (BmoPropertySale) listPropertySale.next();
				bmoPropertySale.getCompanyId().setValue(bmoProperty.getCompanyId().toInteger());
				pmPropertySale.saveSimple(pmConn, bmoPropertySale, bmUpdateResult);
			}

			if (bmoPropertySale.getOrderId().toInteger() > 0) {
				// Se cambia empresa en el pedido
				PmOrder pmOrder = new PmOrder(getSFParams());
				BmoOrder bmoOrder = new BmoOrder();
				bmoOrder = (BmoOrder) pmOrder.get(bmoPropertySale.getOrderId().toInteger());
				bmoOrder.getCompanyId().setValue(bmoProperty.getCompanyId().toInteger());

				int budgetItemId = pmOrder.getBudgetItemByOrder(pmConn, bmoOrder, bmUpdateResult);
				bmoOrder.getDefaultBudgetItemId().setValue(budgetItemId);
				pmOrder.saveSimple(pmConn, bmoOrder, bmUpdateResult);

				BmoRaccount bmoRaccount = new BmoRaccount();
				PmRaccount pmRaccount = new PmRaccount(getSFParams());

				BmFilter byBmoOrderFilter = new BmFilter();
				byBmoOrderFilter.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getOrderId(), bmoOrder.getId());
				Iterator<BmObject> raccountIterator = pmRaccount.list(byBmoOrderFilter).iterator();

				while (raccountIterator.hasNext()) {
					bmoRaccount = (BmoRaccount) raccountIterator.next();

					if (bmoRaccount.getPayments().toDouble() > 0) {
						// validar que el pago sea de la misma empresa que la vivienda, si no no cambiar
						// nada
						boolean changeCompany = false;

						BmoBankMovConcept bmoBankMovConcept = new BmoBankMovConcept();
						PmBankMovConcept pmBankMovConcept = new PmBankMovConcept(getSFParams());

						BmFilter byRaccountFilter = new BmFilter();
						byRaccountFilter.setValueFilter(bmoBankMovConcept.getKind(), bmoBankMovConcept.getRaccountId(),
								bmoRaccount.getId());
						Iterator<BmObject> bankMovConceptIterator = pmBankMovConcept.list(byRaccountFilter).iterator();

						while (bankMovConceptIterator.hasNext()) {
							bmoBankMovConcept = (BmoBankMovConcept) bankMovConceptIterator.next();

							BmoBankMovement bmoBankMovement = new BmoBankMovement();
							PmBankMovement pmBankMovement = new PmBankMovement(getSFParams());
							bmoBankMovement = (BmoBankMovement) pmBankMovement.get(bmoBankMovConcept.getBankMovementId().toInteger());

							BmoBankAccount bmoBankAccount = new BmoBankAccount();
							PmBankAccount pmBankAccount = new PmBankAccount(getSFParams());
							bmoBankAccount = (BmoBankAccount) pmBankAccount.get(bmoBankMovement.getBankAccountId().toInteger());

							if (bmoBankAccount.getCompanyId().toInteger() == bmoProperty.getCompanyId().toInteger()) {
								changeCompany = true;
							} else
								changeCompany = false;
						}

						if (changeCompany) {
							bmoRaccount.getBudgetItemId().setValue(budgetItemId);
							bmoRaccount.getCompanyId().setValue(bmoOrder.getCompanyId().toInteger());
							pmRaccount.saveSimple(pmConn, bmoRaccount, bmUpdateResult);
						}

					} else {
						bmoRaccount.getBudgetItemId().setValue(budgetItemId);
						bmoRaccount.getCompanyId().setValue(bmoOrder.getCompanyId().toInteger());
						pmRaccount.saveSimple(pmConn, bmoRaccount, bmUpdateResult);
					}
				}

				// Cambiar la empresa de la OC
				BmoRequisition bmoRequisition = new BmoRequisition();
				PmRequisition pmRequisition = new PmRequisition(getSFParams());

				BmFilter byBmoOrderRequiFilter = new BmFilter();
				byBmoOrderRequiFilter.setValueFilter(bmoRequisition.getKind(), bmoRequisition.getOrderId(),
						bmoOrder.getId());
				Iterator<BmObject> requisitionIterator = pmRequisition.list(byBmoOrderRequiFilter).iterator();

				BmoBankMovConcept bmoBankMovConcept = new BmoBankMovConcept();
				PmBankMovConcept pmBankMovConcept = new PmBankMovConcept(getSFParams());

				while (requisitionIterator.hasNext()) {
					bmoRequisition = (BmoRequisition) requisitionIterator.next();

					if (bmoRequisition.getPayments().toDouble() > 0) {
						// validar que el pago sea de la misma empresa que la vivienda, si no no cambiar la empresa
						boolean changeCompany = false;

						ArrayList<BmFilter> filters = new ArrayList<BmFilter>();

						// Validar si tiene anticipo que sea de la misma empresa
						BmFilter byRequisitionFilter = new BmFilter();
						byRequisitionFilter.setValueFilter(bmoBankMovConcept.getKind(), bmoBankMovConcept.getRequisitionId(), bmoRequisition.getId());
						filters.add(byRequisitionFilter);

						// Validar si tiene anticipo que el anticipo no tenga ya ligada una cxp
						BmFilter byRequisitionNotPaccount = new BmFilter();
						byRequisitionNotPaccount.setNullFilter(bmoBankMovConcept.getKind(), bmoBankMovConcept.getPaccountId().getName(), BmFilter.ISNULL);
						filters.add(byRequisitionNotPaccount);

						Iterator<BmObject> bmoBankMovConceptIterator = pmBankMovConcept.list(filters).iterator();

						while (bmoBankMovConceptIterator.hasNext()) {

							bmoBankMovConcept = (BmoBankMovConcept)bmoBankMovConceptIterator.next();

							BmoBankAccount bmoBankAccount = new BmoBankAccount();
							PmBankAccount pmBankAccount = new PmBankAccount(getSFParams());
							bmoBankAccount = (BmoBankAccount) pmBankAccount.get(bmoBankMovConcept.getBmoBankMovement().getBankAccountId().toInteger());

							if (bmoBankAccount.getCompanyId().toInteger() == bmoProperty.getCompanyId().toInteger()) {
								changeCompany = true;
							} else
								changeCompany = false;
						}
						if (changeCompany) {
							bmoRequisition.getBudgetItemId().setValue(budgetItemId);
							bmoRequisition.getCompanyId().setValue(bmoOrder.getCompanyId().toInteger());
							pmRequisition.saveSimple(pmConn, bmoRequisition, bmUpdateResult);
						}

						// Busca las CXP
						BmoPaccount bmoPaccount = new BmoPaccount();
						PmPaccount pmPaccount = new PmPaccount(getSFParams());

						BmFilter paccountFilter = new BmFilter();
						paccountFilter.setValueFilter(bmoPaccount.getKind(), bmoPaccount.getRequisitionId(), bmoRequisition.getId());
						Iterator<BmObject> paccountIterator = pmPaccount.list(paccountFilter).iterator();

						while (paccountIterator.hasNext()) {
							bmoPaccount = (BmoPaccount) paccountIterator.next();

							bmoBankMovConcept = new BmoBankMovConcept();
							pmBankMovConcept = new PmBankMovConcept(getSFParams());

							BmFilter byPaccountFilter = new BmFilter();
							byPaccountFilter.setValueFilter(bmoBankMovConcept.getKind(), bmoBankMovConcept.getPaccountId(), bmoPaccount.getId());
							Iterator<BmObject> bankMovConcepPaccounttIterator = pmBankMovConcept.list(byPaccountFilter).iterator();
							PmBankMovement pmBankMovement = new PmBankMovement(getSFParams());

							while (bankMovConcepPaccounttIterator.hasNext()) {
								bmoBankMovConcept = (BmoBankMovConcept) bankMovConcepPaccounttIterator.next();

								BmoBankMovement bmoBankMovement = (BmoBankMovement)pmBankMovement.get(bmoBankMovConcept.getBankMovementId().toInteger());

								BmoBankAccount bmoBankAccount = new BmoBankAccount();
								PmBankAccount pmBankAccount = new PmBankAccount(getSFParams());
								bmoBankAccount = (BmoBankAccount) pmBankAccount.get(bmoBankMovement.getBankAccountId().toInteger());

								if (bmoBankAccount.getCompanyId().toInteger() == bmoProperty.getCompanyId().toInteger()) {
									changeCompany = true;
								} else
									changeCompany = false;
							}

							if (changeCompany) {
								bmoRequisition.getBudgetItemId().setValue(budgetItemId);
								bmoRequisition.getCompanyId().setValue(bmoOrder.getCompanyId().toInteger());
								pmRequisition.saveSimple(pmConn, bmoRequisition, bmUpdateResult);
							}
						}
					} else {
						bmoRequisition.getBudgetItemId().setValue(budgetItemId);
						bmoRequisition.getCompanyId().setValue(bmoOrder.getCompanyId().toInteger());
						pmRequisition.saveSimple(pmConn, bmoRequisition, bmUpdateResult);
					}
				}
			}
		}

		saveSimple(pmConn, bmoProperty, bmUpdateResult);

		// Actualizacion de la clave del inmueble
		if(!isLease) {
			PmDevelopmentBlock pmDevelopmentBlock = new PmDevelopmentBlock(getSFParams());
			BmoDevelopmentBlock bmoDevelopmentBlock = (BmoDevelopmentBlock)pmDevelopmentBlock.get(pmConn, bmoProperty.getDevelopmentBlockId().toInteger());
			PmPropertyModel pmPropertyModel = new PmPropertyModel(getSFParams());
			BmoPropertyModel bmoPropertyModel = (BmoPropertyModel)pmPropertyModel.get(pmConn, bmoProperty.getPropertyModelId().toInteger());
			bmoProperty.getCode().setValue(bmoDevelopmentBlock.getBmoDevelopmentPhase().getCode().toString() 
					+ "." + bmoDevelopmentBlock.getCode().toString() 
					+ "." + bmoProperty.getLot().toString()
					+ "." + bmoProperty.getNumber().toString()
					+ "." + bmoPropertyModel.getCode().toString()
					);	
		}
		else {
			String code = FlexUtil.codeFormatDigits(bmUpdateResult.getId(), 4, BmoProperty.CODE_PREFIX);			
			bmoProperty.getCode().setValue(code);	
		}

		super.save(pmConn, bmoProperty, bmUpdateResult);

		if(!isLease) {
			//Actualizar el No.Viviendas
			this.updateNoProperties(pmConn, bmoProperty, bmUpdateResult);
		}
		// Actualiza estatus disponibilidad de la propiedad
		this.updateAvailability(pmConn, bmoProperty, bmUpdateResult);

		return bmUpdateResult;
	}
	//Copiar tags de inmueble a Venta de inmueble
	private void copyTags(PmConn pmConn, BmoProperty bmoProperty) throws SFException {
		int propertyId = bmoProperty.getId();

		BmoPropertySale bmoPropertySale;
		PmPropertySale pmPropertySale = new PmPropertySale(getSFParams());
		String sql = "SELECT prsa_propertysaleid FROM " +formatKind("propertysales") + " WHERE prsa_propertyid = " + propertyId;

		pmConn.doFetch(sql);
		while(pmConn.next()) {
			bmoPropertySale = (BmoPropertySale)pmPropertySale.get(pmConn.getInt("prsa_propertysaleid"));

			bmoPropertySale.getTags().setValue(""+bmoProperty.getTags());

			pmPropertySale.saveSimple(bmoPropertySale,new BmUpdateResult());
		}
	}
	// Actualiza status de disponibilidad de la propiedad
	public void updateAvailability(PmConn pmConn, BmoProperty bmoProperty, BmUpdateResult bmUpdateResult) throws SFException {
		BmoPropertyModel bmoPropertyModel= new BmoPropertyModel();
		PmPropertyModel pmPropertyModel = new PmPropertyModel(getSFParams());
		bmoPropertyModel = (BmoPropertyModel) pmPropertyModel.get(bmoProperty.getPropertyModelId().toInteger()); 
		String isAvailable = "";
		boolean locked = false;
		if(bmoPropertyModel.getBmoPropertyType().getBmoOrderType().getType().toChar() == BmoOrderType.TYPE_LEASE)
		{
			isAvailable = "SELECT * FROM propertiesrent "
					+ " WHERE prrt_propertyid = "+ bmoProperty.getId() + " "
					+ " AND prrt_status <> '" + BmoOrder.STATUS_CANCELLED + "'" 
					+ " AND prrt_status <> '" + BmoOrder.STATUS_FINISHED + "'";
			pmConn.doFetch(isAvailable);
			locked = pmConn.next();
		}
		else {
			// Recuperar items bloqueados
			isAvailable = "SELECT * FROM propertysales "
					+ " WHERE prsa_propertyid = " + bmoProperty.getId() + " "
					+ " AND prsa_status <> '" + BmoPropertySale.STATUS_CANCELLED + "'";
			pmConn.doFetch(isAvailable);
			locked = pmConn.next();
		}
		if (locked)
			bmoProperty.getAvailable().setValue(false);
		else
			bmoProperty.getAvailable().setValue(true);

		super.save(pmConn, bmoProperty, bmUpdateResult);
	}

	// Actualiza status de disponibilidad de la propiedad
	public void updateAvailability(PmConn pmConn, BmoPropertyRental bmoPropertyRental, BmUpdateResult bmUpdateResult) throws SFException {

		BmoProperty bmoProperty = new BmoProperty();
		PmProperty pmProperty = new PmProperty(getSFParams());

		bmoProperty = (BmoProperty) pmProperty.get(bmoPropertyRental.getPropertyId().toInteger());

		BmoPropertyModel bmoPropertyModel= new BmoPropertyModel();
		PmPropertyModel pmPropertyModel = new PmPropertyModel(getSFParams());
		bmoPropertyModel = (BmoPropertyModel) pmPropertyModel.get(bmoPropertyRental.getBmoProperty().getPropertyModelId().toInteger());
		String isAvailable = "";
		String sql = "";
		boolean locked = false;
		if(bmoPropertyModel.getBmoPropertyType().getBmoOrderType().getType().toChar() == BmoOrderType.TYPE_LEASE)
		{
			//obtenemos el pedido origen del contrato
			sql = "SELECT prrt_orderid FROM propertiesrent "
					+ " WHERE prrt_propertiesrentid = "+ bmoPropertyRental.getId();

			pmConn.doFetch(sql);
			if(pmConn.next()) {
				//Buscamos todos los pedidos renovados diferentes a cancelados y finalizados si se encuenta uno poner el inmueble desahabilitado
				isAvailable = "SELECT * FROM orders" + 
						" WHERE orde_originreneworderid = " + pmConn.getString("prrt_orderid")+
						" and orde_status <> '" + BmoOrder.STATUS_CANCELLED + "'" +
						" and orde_status <> '" + BmoOrder.STATUS_FINISHED + "'" ;
				pmConn.doFetch(isAvailable);
				locked = pmConn.next();
			}
		}

		if (locked)
			bmoProperty.getAvailable().setValue(false);
		else
			bmoProperty.getAvailable().setValue(true);

		super.save(pmConn, bmoProperty, bmUpdateResult);
	}

	//Actualizar el numero de viviendas de la etapa
	public void updateNoProperties(PmConn pmConn, BmoProperty bmoProperty, BmUpdateResult bmUpdateResult) throws SFException {

		PmDevelopmentPhase pmDevelopmentPhase = new PmDevelopmentPhase(getSFParams());

		PmDevelopmentBlock pmDevelopmentBlock = new PmDevelopmentBlock(getSFParams());
		BmoDevelopmentBlock bmoDevelopmentBlock = (BmoDevelopmentBlock)pmDevelopmentBlock.get(pmConn, bmoProperty.getDevelopmentBlockId().toInteger());

		BmoDevelopmentPhase bmoDevelopmentPhase = (BmoDevelopmentPhase)pmDevelopmentPhase.get(pmConn, bmoDevelopmentBlock.getDevelopmentPhaseId().toInteger());

		pmDevelopmentPhase.updateNoProperties(pmConn, bmoDevelopmentPhase, bmUpdateResult);
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		BmoProperty bmoProperty = (BmoProperty)bmObject;
		String sql="";

		super.delete(pmConn, bmoProperty, bmUpdateResult);

		// Saber si el inmueble es de arrendamiento
		sql = "SELECT ortp_type FROM propertymodels"
				+ " LEFT JOIN propertytypes on   ptyp_propertytypeid = ptym_propertytypeid "
				+ " LEFT JOIN ordertypes on ortp_ordertypeid = ptyp_ordertypeid " + " WHERE ptym_propertymodelid = "
				+ bmoProperty.getPropertyModelId().toInteger();

		pmConn.doFetch(sql);
		if (pmConn.next()) {
			if (pmConn.getString("ortp_type").charAt(0) != BmoOrderType.TYPE_LEASE) {
				updateNoProperties(pmConn, bmoProperty, bmUpdateResult);
			}
		}



		return bmUpdateResult;
	}

}
