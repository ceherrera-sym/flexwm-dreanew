/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client;

import com.flexwm.shared.BmoCompanyCollectionProfile;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormLabelList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoProfile;


public class UiCompanyCollectionProfileLabelList extends UiFormLabelList {
	UiListBox companyListBox = new UiListBox(getUiParams(), new BmoCompany());
	// Activar envio de pedido en caso de que se autorize el MB
	UiSuggestBox collectProfileSuggestBox = new UiSuggestBox(new BmoProfile());
	CheckBox sendEmailAuthorizedMBCheckBox = new CheckBox();
	CheckBox remindRaccountCheckBox = new CheckBox();
	TextBox daysBeforeRemaindRaccount = new TextBox();
	TextBox daysBeforeRemaindRaccountTwo = new TextBox();

	BmoCompanyCollectionProfile bmoCompanyCollectionProfile;
	int flexConfigId;

	public UiCompanyCollectionProfileLabelList(UiParams uiParams, int id) {
		super(uiParams, new BmoCompanyCollectionProfile());
		flexConfigId = id;
		bmoCompanyCollectionProfile = new BmoCompanyCollectionProfile();
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoCompanyCollectionProfile.getKind(), 
				bmoCompanyCollectionProfile.getFlexConfigId().getName(), 
				bmoCompanyCollectionProfile.getFlexConfigId().getLabel(), 
				BmFilter.EQUALS, 
				id,
				bmoCompanyCollectionProfile.getFlexConfigId().getName());
	}

	@Override
	public void populateFields() {
		bmoCompanyCollectionProfile = (BmoCompanyCollectionProfile)getBmObject();
		
		// Obtener empresas que no esten asignadas
		if (!(bmoCompanyCollectionProfile.getId() > 0)) {
			BmoCompany bmoCompany = new BmoCompany();
			BmFilter bmCompanyListFilter = new BmFilter();
			bmCompanyListFilter.setNotInFilter(bmoCompanyCollectionProfile.getKind(), 
					bmoCompany.getIdFieldName(), 
					bmoCompanyCollectionProfile.getCompanyId().getName(),
					"1", "1");
			companyListBox.addFilter(bmCompanyListFilter);
		} else { 
			// Limpiar combo si ya existe
			companyListBox.clear();
			companyListBox.clearFilters();
		}
		
		formFlexTable.addField(1, 0, companyListBox, bmoCompanyCollectionProfile.getCompanyId());
		formFlexTable.addField(2, 0, collectProfileSuggestBox, bmoCompanyCollectionProfile.getCollectProfileId());
		formFlexTable.addField(3, 0, sendEmailAuthorizedMBCheckBox, bmoCompanyCollectionProfile.getSendEmailAuthorizedMB());
		formFlexTable.addField(4, 0, remindRaccountCheckBox, bmoCompanyCollectionProfile.getRemaindRaccountInCustomer());
		formFlexTable.addField(5, 0, daysBeforeRemaindRaccount, bmoCompanyCollectionProfile.getDayBeforeRemindRaccount());
		formFlexTable.addField(6, 0, daysBeforeRemaindRaccountTwo, bmoCompanyCollectionProfile.getDayBeforeRemindRaccountTwo());

		statusEffect();
	}

	private void statusEffect() {
		if (bmoCompanyCollectionProfile.getId() > 0) 
			companyListBox.setEnabled(false);
		else 
			companyListBox.setEnabled(true);
	}

	@Override
	public BmObject populateBObject() throws BmException {

		bmoCompanyCollectionProfile = new BmoCompanyCollectionProfile();
		bmoCompanyCollectionProfile.setId(id);
		bmoCompanyCollectionProfile.getFlexConfigId().setValue(flexConfigId);
		bmoCompanyCollectionProfile.getCompanyId().setValue(companyListBox.getSelectedId());
		bmoCompanyCollectionProfile.getCollectProfileId().setValue(collectProfileSuggestBox.getSelectedId());
		bmoCompanyCollectionProfile.getSendEmailAuthorizedMB().setValue(sendEmailAuthorizedMBCheckBox.getValue());
		bmoCompanyCollectionProfile.getRemaindRaccountInCustomer().setValue(remindRaccountCheckBox.getValue());
		bmoCompanyCollectionProfile.getDayBeforeRemindRaccount().setValue(daysBeforeRemaindRaccount.getText());
		bmoCompanyCollectionProfile.getDayBeforeRemindRaccountTwo().setValue(daysBeforeRemaindRaccountTwo.getText());

		return bmoCompanyCollectionProfile;
	}
}
