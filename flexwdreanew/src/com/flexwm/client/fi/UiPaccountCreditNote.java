/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.fi;

import com.symgae.shared.BmObject;
import com.flexwm.shared.fi.BmoPaccount;
import com.symgae.client.ui.UiCalendar;
import com.symgae.client.ui.UiParams;


public class UiPaccountCreditNote extends UiCalendar {
	BmoPaccount bmoPaccount;

	public UiPaccountCreditNote(UiParams uiParams) {
		super(uiParams, new BmoPaccount());
		this.bmoPaccount = (BmoPaccount)getBmObject();		
	}

	@Override
	public void postShow() {
		/*// Filtrar tipos de Flujos por Categoria Oportunidad
		BmoWFlowType bmoWFlowType = new BmoWFlowType();
		BmFilter filterWFlowType = new BmFilter();
		filterWFlowType.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getProgramId(), bmObjectProgramId);		
		addFilterListBox(new UiListBox(getUiParams(), new BmoWFlowType(), filterWFlowType), BmoPaccount.getBmoWFlowType());

		// Filtrar fases por Categoría Oportunidad
		BmoWFlowPhase bmoWFlowPhase = new BmoWFlowPhase();
		BmoWFlowCategory bmoWFlowCategory = new BmoWFlowCategory();
		BmFilter filterWFlowPhase = new BmFilter();
		filterWFlowPhase.setInFilter(bmoWFlowCategory.getKind(), 
				bmoWFlowPhase.getWFlowCategoryId().getName(), 
				bmoWFlowCategory.getIdFieldName(),
				bmoWFlowCategory.getProgramId().getName(),
				"" + bmObjectProgramId);
		addFilterListBox(new UiListBox(getUiParams(), new BmoWFlowPhase(), filterWFlowPhase), BmoPaccount.getBmoWFlow().getBmoWFlowPhase());

		// Filtrar por vendedores
		BmoUser bmoUser = new BmoUser();
		BmoProfileUser bmoProfileUser = new BmoProfileUser();
		BmFilter filterSalesmen = new BmFilter();
		int salesGroupId = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getSalesProfileId().toInteger();
		filterSalesmen.setInFilter(bmoProfileUser.getKind(), 
								bmoUser.getIdFieldName(),
								bmoProfileUser.getUserId().getName(),
								bmoProfileUser.getProfileId().getName(),
								"" + salesGroupId);		
		addFilterListBox(new UiListBox(getUiParams(), new BmoUser(), filterSalesmen), BmoPaccount.getBmoUser());

		addStaticFilterListBox(new UiListBox(BmoPaccount.getStatus()), BmoPaccount, BmoPaccount.getStatus());*/	
	}

	@Override
	public void create() {
		UiPaccount uiPaccountForm = new UiPaccount(getUiParams());
		uiPaccountForm.create();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoPaccount = (BmoPaccount)bmObject;
		UiPaccount uiPaccountForm = new UiPaccount(getUiParams());
		uiPaccountForm.open(bmoPaccount);
	}	
}