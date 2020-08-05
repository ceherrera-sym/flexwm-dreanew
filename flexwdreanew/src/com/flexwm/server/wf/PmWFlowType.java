/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.wf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.flexwm.server.wf.PmWFlowCategory;
import com.flexwm.server.wf.PmWFlowDocumentType;
import com.flexwm.server.wf.PmWFlowStepType;
import com.flexwm.server.wf.PmWFlowStepTypeDep;
import com.symgae.server.PmConn;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFParams;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.wf.BmoWFlowCategory;
import com.flexwm.shared.wf.BmoWFlowDocumentType;
import com.flexwm.shared.wf.BmoWFlowStepType;
import com.flexwm.shared.wf.BmoWFlowStepTypeDep;
import com.flexwm.shared.wf.BmoWFlowType;
import com.flexwm.shared.wf.BmoWFlowTypeCompany;


public class PmWFlowType extends PmObject {
	BmoWFlowType bmoWFlowType;

	public PmWFlowType(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoWFlowType = new BmoWFlowType();
		setBmObject(bmoWFlowType);

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoWFlowType.getWFlowCategoryId(), bmoWFlowType.getBmoWFlowCategory())
				)));
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";
		//Solo para G100
		if(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getMultiCompany().toBoolean()) {
			if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
				filters ="(" + 
						"		(" + 
						"			wfty_wflowtypeid IN  " + 
						"            ( " + 
						"				SELECT wftc_wflowtypeid FROM wflowtypecompanies WHERE wftc_companyid IN " +
						"					(" +
						" 						SELECT uscp_companyid FROM usercompanies where uscp_userid = " + getSFParams().getLoginInfo().getUserId() +
						" 					) " + 
						"			) " + 
						"        ) " + 				
						"	)";
			}


			if (getSFParams().getSelectedCompanyId() > 0) {
				if (filters.length() > 0) filters += " AND ";

				filters += " ( "
						+ "		( wfty_wflowtypeid IN "
						+ "			("
						+ " 		SELECT wftc_wflowtypeid FROM wflowtypecompanies "
						+ " 		WHERE wftc_companyid = " + getSFParams().getSelectedCompanyId()
						+ "			)"
						+ "		)"
						+ ")";
			}
		}else {

			if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
				filters ="( "
						+ " ( wfty_companyid IN "
						+"				(" 
						+ "				SELECT uscp_companyid FROM usercompanies "  
						+"				WHERE uscp_userid = " + getSFParams().getLoginInfo().getUserId()
						+"			    )"
						+"	)"
						+ " OR"
						+ 	"(wfty_companyid  is null)"
						+")";

			}

			// Filtro de empresa seleccionada
			if (getSFParams().getSelectedCompanyId() > 0) {
				if (filters.length() > 0) { 
					filters += " OR ";
					filters += " wfty_companyid = " + getSFParams().getSelectedCompanyId();
				}
				else
					filters = "wfty_companyid  is null OR wfty_companyid = " + getSFParams().getSelectedCompanyId();


			}
		}

		return filters;

	}


	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoWFlowType bmoWFlowType = (BmoWFlowType) autoPopulate(pmConn, new BmoWFlowType());		

		// BmoWFlowCategory
		BmoWFlowCategory bmoWFlowCategory = new BmoWFlowCategory();
		int wflowCategoryId = (int)pmConn.getInt(bmoWFlowCategory.getIdFieldName());
		if (wflowCategoryId > 0) bmoWFlowType.setBmoWFlowCategory((BmoWFlowCategory) new PmWFlowCategory(getSFParams()).populate(pmConn));
		else bmoWFlowType.setBmoWFlowCategory(bmoWFlowCategory);

		return bmoWFlowType;
	}


	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException{

		if (action.equalsIgnoreCase(BmoWFlowType.ACTION_COPY)) 
			copyWFlowType((BmoWFlowType)bmObject, bmUpdateResult, value);

		return bmUpdateResult;
	}

	// Crear un nuevo producto de seguro a partir de otro
	public void copyWFlowType(BmoWFlowType bmoFromWFlowType, BmUpdateResult bmUpdateResult, String value) throws SFException{
		BmoWFlowType bmoToWFlowType = new BmoWFlowType();
		int toWFlowTypeId;
		PmConn pmConn = new PmConn(getSFParams());

		try {
			pmConn.open();
			pmConn.disableAutoCommit();

			// Copiar datos directos
			bmoToWFlowType.getName().setValue(value);
			bmoToWFlowType.getDescription().setValue(bmoFromWFlowType.getDescription().toString() + "_COPY");
			bmoToWFlowType.getComments().setValue(bmoFromWFlowType.getComments().toString() + "_COPY");
			bmoToWFlowType.getWFlowCategoryId().setValue(bmoFromWFlowType.getWFlowCategoryId().toString());
			bmoToWFlowType.getCompanyId().setValue(bmoFromWFlowType.getCompanyId().toInteger());
			// Se almacena el nuevo producto y se obtiene el ID
			bmUpdateResult = this.save(pmConn, bmoToWFlowType, bmUpdateResult);
			toWFlowTypeId = bmUpdateResult.getId();

			// Si no hay errores continuar
			if (!bmUpdateResult.hasErrors()){
				// Crear coberturas
				copyWFlowStepTypes(pmConn, bmUpdateResult, toWFlowTypeId, bmoFromWFlowType);
			}

			// Reasignar el id creado del nuevo producto de seguro
			bmUpdateResult.setId(toWFlowTypeId);

			if (!bmUpdateResult.hasErrors()) 
				pmConn.commit();

		} catch (BmException e) {
			throw new SFException(e.toString());
		} finally {
			pmConn.close();
		}
	}



	// Copiar coberturas del producto de seguro
	private void copyWFlowStepTypes(PmConn pmConn, BmUpdateResult bmUpdateResult, int toWFlowTypeId, BmoWFlowType bmoFromWFlowType) throws SFException {

		// Copiar cada tipo de documento
		BmoWFlowStepType bmoWFlowStepType = new BmoWFlowStepType();
		PmWFlowStepType pmWFlowStepType = new PmWFlowStepType(getSFParams());
		BmFilter filterWFlowType = new BmFilter();
		filterWFlowType.setValueFilter(bmoWFlowStepType.getKind(), bmoWFlowStepType.getWFlowTypeId(), bmoFromWFlowType.getId());
		Iterator<BmObject> wFlowStepTypeListIterator = pmWFlowStepType.list(pmConn, filterWFlowType).iterator();
		HashMap<String, String> idMap = new HashMap<String, String>();
		while (wFlowStepTypeListIterator.hasNext()) {
			BmoWFlowStepType bmoToWFlowStepType = new BmoWFlowStepType();
			BmoWFlowStepType bmoFromWFlowStepType = (BmoWFlowStepType)wFlowStepTypeListIterator.next();

			bmoToWFlowStepType.getName().setValue(bmoFromWFlowStepType.getName().toString());
			bmoToWFlowStepType.getDescription().setValue(bmoFromWFlowStepType.getDescription().toString());
			bmoToWFlowStepType.getSequence().setValue(bmoFromWFlowStepType.getSequence().toString());
			bmoToWFlowStepType.getWFlowValidationId().setValue(bmoFromWFlowStepType.getWFlowValidationId().toString());
			bmoToWFlowStepType.getWFlowActionId().setValue(bmoFromWFlowStepType.getWFlowActionId().toString());
			bmoToWFlowStepType.getDaysRemind().setValue(bmoFromWFlowStepType.getDaysRemind().toString());
			bmoToWFlowStepType.getEmailReminders().setValue(bmoFromWFlowStepType.getEmailReminders().toString());
			bmoToWFlowStepType.getWFlowPhaseId().setValue(bmoFromWFlowStepType.getWFlowPhaseId().toString());
			bmoToWFlowStepType.getProfileId().setValue(bmoFromWFlowStepType.getProfileId().toString());
			bmoToWFlowStepType.getHours().setValue(bmoFromWFlowStepType.getHours().toDouble());
			bmoToWFlowStepType.getBillable().setValue(bmoFromWFlowStepType.getBillable().toBoolean());
			bmoToWFlowStepType.getRate().setValue(bmoFromWFlowStepType.getRate().toDouble());
			bmoToWFlowStepType.getWFlowFunnelId().setValue(bmoFromWFlowStepType.getWFlowFunnelId().toInteger());
			// Asigna el ID del nuevo tipo de flujo
			bmoToWFlowStepType.getWFlowTypeId().setValue(toWFlowTypeId);

			pmWFlowStepType.save(pmConn, bmoToWFlowStepType, bmUpdateResult);

			int newId = bmUpdateResult.getId();
			// Asigna al mapa de ids
			idMap.put("" + bmoFromWFlowStepType.getId(), "" + newId);
		}

		// Copiar dependencias
		PmWFlowStepTypeDep pmWFlowStepTypeDep = new PmWFlowStepTypeDep(getSFParams());
		wFlowStepTypeListIterator = pmWFlowStepType.list(pmConn, filterWFlowType).iterator();
		while (wFlowStepTypeListIterator.hasNext()) {
			BmoWFlowStepType bmoFromWFlowStepType = (BmoWFlowStepType)wFlowStepTypeListIterator.next();

			// Recorrer nuevamente la lista de pasos anteriores
			BmoWFlowStepTypeDep bmoBaseWFlowStepTypeDep = new BmoWFlowStepTypeDep();
			BmFilter filterByStepType = new BmFilter();
			filterByStepType.setValueFilter(bmoBaseWFlowStepTypeDep.getKind(), bmoBaseWFlowStepTypeDep.getWFlowStepTypeId().getName(), bmoFromWFlowStepType.getId());
			Iterator<BmObject> stepTypeDepListIterator = pmWFlowStepTypeDep.list(pmConn, filterByStepType).listIterator();

			while (stepTypeDepListIterator.hasNext()) {
				BmoWFlowStepTypeDep bmoFromWFlowStepTypeDep = (BmoWFlowStepTypeDep)stepTypeDepListIterator.next();

				String parentWFlowStepType = idMap.get("" + bmoFromWFlowStepTypeDep.getWFlowStepTypeId().toString());
				String childWFlowStepType = idMap.get("" + bmoFromWFlowStepTypeDep.getChildStepTypeId().toString());

				BmoWFlowStepTypeDep bmoToWFlowStepTypeDep = new BmoWFlowStepTypeDep();
				bmoToWFlowStepTypeDep.getWFlowStepTypeId().setValue(parentWFlowStepType);
				bmoToWFlowStepTypeDep.getChildStepTypeId().setValue(childWFlowStepType);

				pmWFlowStepTypeDep.save(pmConn, bmoToWFlowStepTypeDep, bmUpdateResult);
			}
		}

		// Copiar documentos
		BmoWFlowDocumentType bmoWFlowDocumentType = new BmoWFlowDocumentType();
		PmWFlowDocumentType pmWFlowDocumentType = new PmWFlowDocumentType(getSFParams());
		BmFilter filterWFlowDocumentType = new BmFilter();
		filterWFlowDocumentType.setValueFilter(bmoWFlowDocumentType.getKind(), bmoWFlowDocumentType.getWFlowTypeId(), bmoFromWFlowType.getId());
		Iterator<BmObject> wFlowDocumentTypeListIterator = pmWFlowDocumentType.list(pmConn, filterWFlowDocumentType).iterator();
		while (wFlowDocumentTypeListIterator.hasNext()) {
			BmoWFlowDocumentType bmoFromWFlowDocumentType = (BmoWFlowDocumentType)wFlowDocumentTypeListIterator.next();
			BmoWFlowDocumentType bmoToWFlowDocumentType = new BmoWFlowDocumentType();

			bmoToWFlowDocumentType.getCode().setValue(bmoFromWFlowDocumentType.getCode().toString());
			bmoToWFlowDocumentType.getName().setValue(bmoFromWFlowDocumentType.getName().toString());
			bmoToWFlowDocumentType.getRequired().setValue(bmoFromWFlowDocumentType.getRequired().toBoolean());
			bmoToWFlowDocumentType.getFileTypeId().setValue(bmoFromWFlowDocumentType.getFileTypeId().toInteger());

			// Asigna el ID del nuevo tipo de flujo
			bmoToWFlowDocumentType.getWFlowTypeId().setValue(toWFlowTypeId);

			pmWFlowDocumentType.save(pmConn, bmoToWFlowDocumentType, bmUpdateResult);
		}

	}

	// Obtiene el primer tipo de acuerdo al modulo
	public int getFirstWFlowTypeId(PmConn pmConn, String programCode) throws SFPmException {
		int wFlowTypeId = 0;

		String sql = "SELECT wfty_wflowtypeid FROM wflowtypes "
				+ " LEFT JOIN wflowcategories ON (wfty_wflowcategoryid = wfca_wflowcategoryid) "
				+ " LEFT JOIN programs ON (wfca_programid = prog_programid)"
				+ " WHERE "
				+ " prog_code LIKE '" + programCode + "'"
				+ " ORDER BY wfty_wflowtypeid ASC ";

		pmConn.doFetch(sql);

		// Si es rama de otro paso, revisar el avance de los anteriores
		if (pmConn.next()) {
			wFlowTypeId = pmConn.getInt(1);
		}
		return wFlowTypeId;
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoWFlowType = (BmoWFlowType)bmObject;

		// Eliminar pasos	
		PmWFlowStepType pmWFlowStepType = new PmWFlowStepType(getSFParams());
		BmoWFlowStepType bmoWFlowStepType = new BmoWFlowStepType();
		BmFilter filterByWFlowType = new BmFilter();
		filterByWFlowType.setValueFilter(bmoWFlowStepType.getKind(), bmoWFlowStepType.getWFlowTypeId(), bmoWFlowType.getId());
		ListIterator<BmObject> wFlowStepTypeList = pmWFlowStepType.list(pmConn, filterByWFlowType).listIterator();
		while (wFlowStepTypeList.hasNext()) {
			BmoWFlowStepType bmoCurrentWFlowTypeDep = (BmoWFlowStepType)wFlowStepTypeList.next();
			pmWFlowStepType.delete(pmConn, bmoCurrentWFlowTypeDep, bmUpdateResult);
		}

		// Eliminar tipos de documentos ligados
		PmWFlowDocumentType pmWFlowDocumentType = new PmWFlowDocumentType(getSFParams());
		BmoWFlowDocumentType bmoWFlowDocumentType = new BmoWFlowDocumentType();
		filterByWFlowType = new BmFilter();
		filterByWFlowType.setValueFilter(bmoWFlowDocumentType.getKind(), bmoWFlowDocumentType.getWFlowTypeId(), bmoWFlowType.getId());
		ListIterator<BmObject> wFlowDocumentList = pmWFlowDocumentType.list(pmConn, filterByWFlowType).listIterator();
		while (wFlowDocumentList.hasNext()) {
			BmoWFlowDocumentType bmoCurrentWFlowDocumentType = (BmoWFlowDocumentType)wFlowDocumentList.next();
			pmWFlowDocumentType.delete(pmConn, bmoCurrentWFlowDocumentType, bmUpdateResult);
		}

		// Eliminar usuario
		super.delete(pmConn, bmoWFlowType, bmUpdateResult);

		return bmUpdateResult;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		BmoWFlowType bmoWFlowType = (BmoWFlowType)bmObject;
		boolean isNewRecord = false;
		int companyId = 0;




		if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getMultiCompany().toBoolean())) {

			if (bmoWFlowType.getCompanyId().toInteger() > 0) {
				companyId = bmoWFlowType.getCompanyId().toInteger();
			} else {
				bmUpdateResult.addError(bmoWFlowType.getCompanyId().getName(), "Seleccione una empresa");
			}

			if(!(bmoWFlowType.getId() > 0))
				isNewRecord = true;
		}

		super.save(pmConn, bmoWFlowType, bmUpdateResult);

		if (!bmUpdateResult.hasErrors() && isNewRecord && (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getMultiCompany().toBoolean())) {
			BmoWFlowTypeCompany bmoWFlowTypeCompany = new BmoWFlowTypeCompany();

			bmoWFlowTypeCompany.getWflowTypeId().setValue(bmUpdateResult.getId());
			bmoWFlowTypeCompany.getCompanyId().setValue(companyId);

			new PmWFlowTypeCompany(getSFParams()).save(bmoWFlowTypeCompany, new BmUpdateResult());
		}

		return bmUpdateResult;
	}

}
