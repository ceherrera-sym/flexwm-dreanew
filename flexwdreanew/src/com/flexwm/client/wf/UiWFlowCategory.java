/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.wf;

import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoProgram;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.wf.BmoWFlowCategory;
import com.flexwm.shared.wf.BmoWFlowCategoryCompany;
import com.flexwm.shared.wf.BmoWFlowCategoryProfile;
import com.flexwm.shared.wf.BmoWFlowPhase;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;


public class UiWFlowCategory extends UiList {
	BmoWFlowCategory bmoWFlowCategory;

	public UiWFlowCategory(UiParams uiParams) {
		super(uiParams, new BmoWFlowCategory());
		bmoWFlowCategory = (BmoWFlowCategory)getBmObject();
	}

	@Override
	public void create() {
		UiWFlowCategoryForm uiWFlowCategoryForm = new UiWFlowCategoryForm(getUiParams(), 0);
		uiWFlowCategoryForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiWFlowCategoryForm uiWFlowCategoryForm = new UiWFlowCategoryForm(getUiParams(), bmObject.getId());
		uiWFlowCategoryForm.show();	
	}
	
	@Override
	public void postShow() {
		addStaticFilterListBox(new UiListBox(getUiParams(), bmoWFlowCategory.getStatus()), bmoWFlowCategory,bmoWFlowCategory.getStatus());
	}

	public class UiWFlowCategoryForm extends UiFormDialog {
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		UiSuggestBox programSuggestBox = new UiSuggestBox(new BmoProgram());
		UiListBox effectWFlowCategoryListBox = new UiListBox(getUiParams(), new BmoWFlowCategory());
		CheckBox emailRemindersCheckBox = new CheckBox();
		CheckBox gCalendarSyncCheckBox = new CheckBox();
		TextBox gCalendarIdTextBox = new TextBox();
		CheckBox expiresCheckBox = new CheckBox();
		TextBox expireDaysTextBox = new TextBox();
		UiListBox statusListBox = new UiListBox(getUiParams());
		UiListBox companyListBox = new UiListBox(getUiParams(),new BmoCompany());
		TextBox daysRemindExpiredTextBox = new TextBox();

		BmoWFlowCategory bmoWFlowCategory;

		public UiWFlowCategoryForm(UiParams uiParams, int id) {
			super(uiParams, new BmoWFlowCategory(), id);
		}

		@Override
		public void populateFields(){
			bmoWFlowCategory = (BmoWFlowCategory)getBmObject();
			formFlexTable.addSectionLabel(0, 0, "Datos Generales", 2);
			if(newRecord && (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getMultiCompany().toBoolean()) )
				formFlexTable.addField(1, 0, companyListBox,bmoWFlowCategory.getCompanyId());
			formFlexTable.addField(2, 0, nameTextBox, bmoWFlowCategory.getName());
			if(!newRecord && ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getMultiCompany().toBoolean())
				formFlexTable.addField(3, 0, new UiWFlowCategoryCompanyLabelList(getUiParams(), id));
			formFlexTable.addField(4, 0, descriptionTextArea, bmoWFlowCategory.getDescription());
			formFlexTable.addField(5, 0, programSuggestBox, bmoWFlowCategory.getProgramId());
			formFlexTable.addField(6, 0, effectWFlowCategoryListBox, bmoWFlowCategory.getEffectWFlowCategoryId());
			formFlexTable.addField(7, 0, emailRemindersCheckBox, bmoWFlowCategory.getEmailReminders());
			formFlexTable.addField(8, 0, expiresCheckBox, bmoWFlowCategory.getExpires());
			formFlexTable.addField(9, 0, expireDaysTextBox, bmoWFlowCategory.getExpireDays());
			formFlexTable.addField(10, 0, daysRemindExpiredTextBox,bmoWFlowCategory.getDaysRemindExpired());
			formFlexTable.addField(11, 0, gCalendarSyncCheckBox, bmoWFlowCategory.getgCalendarSync());
			formFlexTable.addField(12, 0, gCalendarIdTextBox, bmoWFlowCategory.getgCalendarId());
		
			formFlexTable.addField(13, 0, statusListBox, bmoWFlowCategory.getStatus());

			
			if (!newRecord) {
				formFlexTable.addSectionLabel(14, 0, "Datos Adicionales", 2);

				// Fases de Flujos
				BmoWFlowPhase bmoWFlowPhase = new BmoWFlowPhase();
				FlowPanel wFlowPhaseFP = new FlowPanel();
				UiWFlowPhase uiWFlowPhase = new UiWFlowPhase(getUiParams(), wFlowPhaseFP, bmoWFlowCategory);
				setUiType(bmoWFlowPhase.getProgramCode(), UiParams.MINIMALIST);
				uiWFlowPhase.show();
				formFlexTable.addPanel(15, 0, wFlowPhaseFP, 2);
				
				// Grupos de Flujos
				BmoWFlowCategoryProfile bmoWFlowCategoryProfile = new BmoWFlowCategoryProfile();
				FlowPanel wFlowGroupItemFP = new FlowPanel();
				UiWFlowCategoryProfile uiWFlowGroup = new UiWFlowCategoryProfile(getUiParams(), wFlowGroupItemFP, bmoWFlowCategory);
				setUiType(bmoWFlowCategoryProfile.getProgramCode(), UiParams.MINIMALIST);
				uiWFlowGroup.show();
				formFlexTable.addPanel(16, 0, wFlowGroupItemFP, 2);
			}
			
		}

		@Override
		public void formListChange(ChangeEvent event) {
			if(event.getSource() ==  companyListBox) {
				if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getMultiCompany().toBoolean())) {
					if (companyListBox.getSelectedBmObject() != null) {
						filterByCompany(companyListBox.getSelectedBmObject().getId());
					}else {
						filterByCompany(0);
					}
				}
			}
		}	
		public void filterByCompany(int companyId) {

			if(companyId > 0) {

				BmoWFlowCategoryCompany bmoWFlowCategoryCompany = new BmoWFlowCategoryCompany();
				BmoWFlowCategory bmoWFlowCategory = new BmoWFlowCategory();
				BmFilter bmFilter = new BmFilter();
				bmFilter.setInFilter(bmoWFlowCategoryCompany.getKind(), bmoWFlowCategory.getIdFieldName(), 
						bmoWFlowCategoryCompany.getWflowCategoryId().getName(), 
						bmoWFlowCategoryCompany.getCompanyId().getName(),
						""+companyId);
				effectWFlowCategoryListBox.clearFilters();
				effectWFlowCategoryListBox.addFilter(bmFilter);			

			} else {

				effectWFlowCategoryListBox.clearFilters();
			}
			effectWFlowCategoryListBox.populate(bmoWFlowCategory.getEffectWFlowCategoryId());
			
		}
		@Override
		public BmObject populateBObject() throws BmException {
			bmoWFlowCategory.setId(id);
			bmoWFlowCategory.getName().setValue(nameTextBox.getText());
			bmoWFlowCategory.getDescription().setValue(descriptionTextArea.getText());
			bmoWFlowCategory.getProgramId().setValue(programSuggestBox.getSelectedId());
			bmoWFlowCategory.getEmailReminders().setValue(emailRemindersCheckBox.getValue());
			bmoWFlowCategory.getExpires().setValue(expiresCheckBox.getValue());
			bmoWFlowCategory.getExpireDays().setValue(expireDaysTextBox.getText());
			bmoWFlowCategory.getEffectWFlowCategoryId().setValue(effectWFlowCategoryListBox.getSelectedId());
			bmoWFlowCategory.getgCalendarSync().setValue(gCalendarSyncCheckBox.getValue());
			bmoWFlowCategory.getgCalendarId().setValue(gCalendarIdTextBox.getText());
			bmoWFlowCategory.getStatus().setValue(statusListBox.getSelectedCode());
			if(newRecord || (!((BmoFlexConfig)getSFParams().getBmoAppConfig()).getMultiCompany().toBoolean()))
				bmoWFlowCategory.getCompanyId().setValue(companyListBox.getSelectedId());
			bmoWFlowCategory.getDaysRemindExpired().setValue(daysRemindExpiredTextBox.getText());
			return bmoWFlowCategory;
		}

		@Override
		public void close() {
			list();
		}

		@Override
		public void saveNext() {
			if (newRecord) { 
				UiWFlowCategoryForm uiWFlowCategoryForm = new UiWFlowCategoryForm(getUiParams(), getBmObject().getId());
				uiWFlowCategoryForm.show();
			} else {
				UiWFlowCategory uiWFlowCategoryList = new UiWFlowCategory(getUiParams());
				uiWFlowCategoryList.show();
			}		
		}
	}
}