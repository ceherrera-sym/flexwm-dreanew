/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.co;

import java.util.Date;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFException;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoUser;
import com.flexwm.shared.co.BmoDevelopmentPhase;
import com.flexwm.shared.co.BmoWork;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiFormFlexTable;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;


/**
 * @author smuniz
 *
 */

public class UiWork extends UiList {
	BmoWork bmoWork;

	public UiWork(UiParams uiParams) {
		super(uiParams, new BmoWork());
		bmoWork = (BmoWork)getBmObject();
	}

	@Override
	public void postShow() {
		if (isMaster()) {
			addFilterListBox(new UiListBox(getUiParams(), new BmoCompany()), bmoWork.getBmoCompany());
			addStaticFilterListBox(new UiListBox(getUiParams(), bmoWork.getStatus()), bmoWork, bmoWork.getStatus());
		}
	}

	@Override
	public void create() {
		UiWorkForm uiWorkForm = new UiWorkForm(getUiParams(), 0);
		uiWorkForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoWork = (BmoWork)bmObject;
		// Si esta asignado el tipo de proyecto, envia directo al dashboard
		if (bmoWork.getId() > 0) {
			UiWorkDetail uiWorkDetail = new UiWorkDetail(getUiParams(), bmoWork.getId());
			uiWorkDetail.show();
		} else {
			UiWorkForm uiWorkForm = new UiWorkForm(getUiParams(), bmoWork.getId());
			uiWorkForm.show();
		}		
	}

	@Override
	public void edit(BmObject bmObject) {
		UiWorkForm uiWorkForm = new UiWorkForm(getUiParams(), bmObject.getId());
		uiWorkForm.show();
	}

	public class UiWorkForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		UiDateBox dateCreateBox = new UiDateBox();
		TextArea descriptionTextArea = new TextArea();
		UiListBox developmentPhaseListBox = new UiListBox(getUiParams(), new BmoDevelopmentPhase());
		UiSuggestBox userIdSuggestBox = new UiSuggestBox(new BmoUser());	
		UiSuggestBox budgetItemSuggest = new UiSuggestBox(new BmoBudgetItem());	
		CheckBox isMasterCheckBox = new CheckBox();
		UiListBox statusListBox = new UiListBox(getUiParams());
		TextBox indirectsTextBox = new TextBox();
		TextBox indirectsAmountTextBox = new TextBox();
		TextBox subTotalTextBox = new TextBox();
		TextBox totalTextBox = new TextBox();

		private NumberFormat numberFormat = NumberFormat.getDecimalFormat();

		UiListBox companyIdListBox = new UiListBox(getUiParams(), new BmoCompany());

		BmoWork bmoWork = new BmoWork();

		WorkUpdater workUpdater = new WorkUpdater();

		// Copiar cotizacion
		private Button copyWorkButton = new Button("Copiar");
		private Button copyWorkDialogButton = new Button("Copiar Items");
		private Button copyWorkCloseDialogButton = new Button("Cerrar");
		private HorizontalPanel workButtonPanel = new HorizontalPanel();
		protected DialogBox copyWorkDialogBox;
		UiSuggestBox copyWorkSuggestBox = new UiSuggestBox(new BmoWork());

		protected FlowPanel formatPanel;

		public UiWorkForm(UiParams uiParams, int id) {
			super(uiParams, new BmoWork(), id);


			// Copiar Obra
			copyWorkDialogButton.setStyleName("formCloseButton");
			copyWorkDialogButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					copyWorkDialog();
				}
			});

			copyWorkCloseDialogButton.setStyleName("formCloseButton");
			copyWorkCloseDialogButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					copyWorkDialogBox.hide();
				}
			});

			copyWorkButton.setStyleName("formCloseButton");
			copyWorkButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					copyWorkAction();
					copyWorkDialogBox.hide();
				}
			});

			ValueChangeHandler<String> textChangeHandler = new ValueChangeHandler<String>() {
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					indirectChangePrice();
				}
			};
			indirectsTextBox.addValueChangeHandler(textChangeHandler);
		}

		@Override
		public void populateFields() {
			bmoWork = (BmoWork)getBmObject();

			if (newRecord) {
				try {
					bmoWork.getStartDate().setValue(GwtUtil.dateToString(new Date(), getSFParams().getDateFormat()));				
				} catch (BmException e) {
					showErrorMessage(this.getClass().getName() + "-populateFields(): ERROR " + e.toString());
				}	
			}

			// Filtrar por usuarios activos
			BmoUser bmoUser = new BmoUser();
			BmFilter filterUserActive = new BmFilter();
			filterUserActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
			userIdSuggestBox.addFilter(filterUserActive);

			formFlexTable.addField(1, 0, codeTextBox, bmoWork.getCode());
			formFlexTable.addField(2, 0, nameTextBox, bmoWork.getName());
			formFlexTable.addField(3, 0, dateCreateBox, bmoWork.getStartDate());
			formFlexTable.addField(4, 0, descriptionTextArea, bmoWork.getDescription());
			formFlexTable.addField(5, 0, developmentPhaseListBox, bmoWork.getDevelopmentPhaseId());
			formFlexTable.addField(6, 0, userIdSuggestBox, bmoWork.getUserId());
			//formFlexTable.addField(4, 0, budgetItemSuggest, bmoWork.getBudgetItemId());
			formFlexTable.addField(7, 0, companyIdListBox, bmoWork.getCompanyId());
			formFlexTable.addField(8, 0, isMasterCheckBox, bmoWork.getIsMaster());		
			if (!newRecord && bmoWork.getStatus().equals(BmoWork.STATUS_REVISION)) {
				if (!(bmoWork.getTotal().toDouble() > 0))
					formFlexTable.addPanel(9, 0, workButtonPanel, 2);
			}
			workButtonPanel.clear();
			workButtonPanel.add(copyWorkDialogButton);		

			if (!newRecord) {			
				formFlexTable.addField(10, 0, indirectsTextBox, bmoWork.getIndirects());					
				formFlexTable.addField(11, 0, totalTextBox, bmoWork.getTotal());
			}
			formFlexTable.addField(12, 0, statusListBox, bmoWork.getStatus());

			statusEffect();
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoWork.setId(id);
			bmoWork.getCode().setValue(codeTextBox.getText());
			bmoWork.getName().setValue(nameTextBox.getText());
			bmoWork.getDescription().setValue(descriptionTextArea.getText());
			bmoWork.getDevelopmentPhaseId().setValue(developmentPhaseListBox.getSelectedId());
			bmoWork.getUserId().setValue(userIdSuggestBox.getSelectedId());
			bmoWork.getBudgetItemId().setValue(budgetItemSuggest.getSelectedId());
			bmoWork.getIsMaster().setValue(isMasterCheckBox.getValue());
			bmoWork.getStatus().setValue(statusListBox.getSelectedCode());
			bmoWork.getIndirects().setValue(indirectsTextBox.getText());
			bmoWork.getIndirects().setValue(indirectsTextBox.getText());
			bmoWork.getIndirectsAmount().setValue(indirectsTextBox.getText());
			bmoWork.getIndirects().setValue(indirectsTextBox.getText());		
			bmoWork.getCompanyId().setValue(companyIdListBox.getSelectedId());
			bmoWork.getStartDate().setValue(dateCreateBox.getTextBox().getText());

			return bmoWork;
		}

		@Override
		public void close() {
//			
//			if (isSlave()) {
//				//
//			} else {
//				if (getBmObject().getId() > 0) {
//					UiWorkDetail uiWorkDetail = new UiWorkDetail(getUiParams(), getBmObject().getId());
//					uiWorkDetail.show();
//				} else {
//					list();
//				}
//			}
		}

		@Override
		public void formListChange(ChangeEvent event) {
			if (event.getSource() == statusListBox) {
				update("Desea cambiar el Status de la Obrar?");
				reset();
			} else if (event.getSource() == developmentPhaseListBox) {
				BmoDevelopmentPhase bmoDevelopmentPhase = (BmoDevelopmentPhase)developmentPhaseListBox.getSelectedBmObject();
				if (bmoDevelopmentPhase != null) {
					populateCompany(bmoDevelopmentPhase.getCompanyId().toInteger());
				}
			}
		}

		// Actualiza combo de Empresas
		private void populateCompany(int companyId) {
			companyIdListBox.clear();
			companyIdListBox.clearFilters();
			setCompanyListBoxFilters(companyId);
			companyIdListBox.populate("" + companyId);
		}

		// Asigna filtros al listado de Empresas
		private void setCompanyListBoxFilters(int companyId) {
			BmoCompany bmoCompany = new BmoCompany();
			if (companyId > 0) {
				BmFilter bmFilterByCompany = new BmFilter();
				bmFilterByCompany.setValueFilter(bmoCompany.getKind(), bmoCompany.getIdField(), companyId);
				companyIdListBox.addBmFilter(bmFilterByCompany);
			} else {
				BmFilter bmFilterLimitRecords = new BmFilter();
				bmFilterLimitRecords.setValueFilter(bmoCompany.getKind(), bmoCompany.getIdField(), "-1");
				companyIdListBox.addBmFilter(bmFilterLimitRecords);
			}		
		}

		public void statusEffect() {
			codeTextBox.setEnabled(false);
			nameTextBox.setEnabled(false);
			dateCreateBox.setEnabled(false);
			statusListBox.setEnabled(false);
			developmentPhaseListBox.setEnabled(false);
			isMasterCheckBox.setEnabled(false);
			budgetItemSuggest.setEnabled(false);
			companyIdListBox.setEnabled(false);
			indirectsTextBox.setEnabled(false);
			indirectsAmountTextBox.setEnabled(false);
			subTotalTextBox.setEnabled(false);
			totalTextBox.setEnabled(false);
			descriptionTextArea.setEnabled(false);
			userIdSuggestBox.setEnabled(false);		

			if (newRecord) {
				codeTextBox.setEnabled(true);
				nameTextBox.setEnabled(true);
				dateCreateBox.setEnabled(true);
				developmentPhaseListBox.setEnabled(true);
				isMasterCheckBox.setEnabled(true);
				budgetItemSuggest.setEnabled(true);			
				indirectsTextBox.setEnabled(true);
				descriptionTextArea.setEnabled(true);
				userIdSuggestBox.setEnabled(true);			
			} else {

				if (bmoWork.getStatus().equals(BmoWork.STATUS_AUTHORIZED)) {
					codeTextBox.setEnabled(false);
					nameTextBox.setEnabled(false);
					dateCreateBox.setEnabled(false);
					statusListBox.setEnabled(false);				
					isMasterCheckBox.setEnabled(false);
					budgetItemSuggest.setEnabled(false);
					companyIdListBox.setEnabled(false);
					indirectsTextBox.setEnabled(false);
					descriptionTextArea.setEnabled(false);
					userIdSuggestBox.setEnabled(false);				
				} else {
					codeTextBox.setEnabled(true);
					nameTextBox.setEnabled(true);
					dateCreateBox.setEnabled(true);				
					isMasterCheckBox.setEnabled(true);
					budgetItemSuggest.setEnabled(true);
					indirectsTextBox.setEnabled(true);
					descriptionTextArea.setEnabled(true);
					userIdSuggestBox.setEnabled(true);				
					companyIdListBox.setEnabled(true);
					developmentPhaseListBox.setEnabled(true);

				}

				if (getSFParams().hasSpecialAccess(BmoWork.ACCESS_CHANGESTATUS)) {					
					statusListBox.setEnabled(true);
				}
			}		

			if (bmoWork.getStatus().equals(BmoWork.STATUS_AUTHORIZED)) {
				deleteButton.setVisible(false);
			} else {
				deleteButton.setVisible(true);
			}

			setAmount(bmoWork);
		}

		public void copyWorkDialog() {
			copyWorkDialogBox = new DialogBox(true);
			copyWorkDialogBox.setGlassEnabled(true);
			copyWorkDialogBox.setText("Copiar Cotización");
			copyWorkDialogBox.setSize("400px", "100px");

			VerticalPanel vp = new VerticalPanel();
			vp.setSize("400px", "100px");
			copyWorkDialogBox.setWidget(vp);

			UiFormFlexTable formCopyWorkTable = new UiFormFlexTable(getUiParams());
			BmoWork fromBmoWork = new BmoWork();
			formCopyWorkTable.addField(6, 0, copyWorkSuggestBox, fromBmoWork.getIdField());

			HorizontalPanel changeStaffButtonPanel = new HorizontalPanel();
			changeStaffButtonPanel.add(copyWorkButton);
			changeStaffButtonPanel.add(copyWorkCloseDialogButton);

			vp.add(formCopyWorkTable);
			vp.add(changeStaffButtonPanel);

			copyWorkDialogBox.center();
			copyWorkDialogBox.show();
		}

		//Obtener la cantidad en almacen
		public void copyWorkAction() {
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-copyWorkAction() ERROR: " + caught.toString());
				}

				@Override
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					showSystemMessage("Copia de Items Exitosa.");
					show();
				}
			};

			try {	
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoWork.getPmClass(), bmoWork, BmoWork.ACTION_COPYWORK, "" + copyWorkSuggestBox.getSelectedId(), callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-copyWorkAction() ERROR: " + e.toString());
			}
		} 

		public void indirectChangePrice() {
			try {
				bmoWork.getIndirects().setValue(indirectsTextBox.getText());
				saveIndirectChangePrice();
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-othersExpensesChange() ERROR: " + e.toString());
			}
		}

		public void saveIndirectChangePrice() {
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-saveDiscount(): ERROR " + caught.toString());
				}

				@Override
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					processUnitPriceUpdateResult(result);
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().save(bmoWork.getPmClass(), bmoWork, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-saveOtherExpensesChange(): ERROR " + e.toString());
			}
		}

		public void processUnitPriceUpdateResult(BmUpdateResult result) {
			if (result.hasErrors()) showFormMsg("Errores al actualizar los indirectos.", "Errores al actualizar los indirectos: " + result.errorsToString());
			else updateAmount(id);
		}

		public void updateAmount(int id) {
			AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-updateAmount() ERROR: " + caught.toString());
				}

				@Override
				public void onSuccess(BmObject result) {
					stopLoading();				
					setAmount((BmoWork)result);
				}
			};
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().get(bmoWork.getPmClass(), id, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-updateAmount(): ERROR " + e.toString());
			}
		}

		public void setAmount(BmoWork bmoWork) {
			numberFormat = NumberFormat.getCurrencyFormat();
			subTotalTextBox.setText(numberFormat.format(bmoWork.getSubTotal().toDouble()));
			indirectsAmountTextBox.setText(numberFormat.format(bmoWork.getIndirectsAmount().toDouble()));
			totalTextBox.setText(numberFormat.format(bmoWork.getTotal().toDouble()));
			setAmount("" + bmoWork.getSubTotal().toDouble(), "" + bmoWork.getIndirectsAmount().toDouble() ,"" + bmoWork.getTotal().toDouble());
		}

		private void setAmount(String subTotal, String totalIndirects, String total ) {		
			double a = Double.parseDouble(subTotal);
			subTotalTextBox.setText(numberFormat.format(a));

			a = Double.parseDouble(totalIndirects);
			indirectsAmountTextBox.setText(numberFormat.format(a));

			a = Double.parseDouble(total);
			totalTextBox.setText(numberFormat.format(a));
		}

		public void reset() {
			updateAmount(id);
			//uiWorkItemGrid.show();	
		}

		protected class WorkUpdater {
			public void changeWork() {
				stopLoading();
				reset();
			}		
		}

		@Override
		public void saveNext() {
			UiWorkDetail UiWorkDetail = new UiWorkDetail(getUiParams(), getBmObject().getId());
			UiWorkDetail.show();
		}
	}
}
