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

import com.flexwm.shared.co.BmoContractEstimation;
import com.flexwm.shared.co.BmoWorkContract;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;


public class UiContractEstimation extends UiList {
	BmoContractEstimation bmoContractEstimation;

	BmoWorkContract bmoWorkContract = new BmoWorkContract();

	public UiContractEstimation(UiParams uiParams) {
		super(uiParams, new BmoContractEstimation());
		bmoContractEstimation = (BmoContractEstimation)getBmObject();
	}

	public UiContractEstimation(UiParams uiParams, BmoWorkContract bmoWorkContract) {
		super(uiParams, new BmoContractEstimation());
		this.bmoWorkContract = bmoWorkContract;
	}	

	@Override
	public void postShow() {
		if (isSlave()) {
			getUiParams().getUiTemplate().hideEastPanel();			
		} 
	}

	@Override
	public void create() {
		UiContractEstimationForm uiContractEstimationForm = new UiContractEstimationForm(getUiParams(), 0);
		uiContractEstimationForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoContractEstimation = (BmoContractEstimation)bmObject;
		UiContractEstimationForm uiContractEstimationForm = new UiContractEstimationForm(getUiParams(), bmoContractEstimation.getId());
		uiContractEstimationForm.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		UiContractEstimationForm uiContractEstimationForm = new UiContractEstimationForm(getUiParams(), bmObject.getId());
		uiContractEstimationForm.show();
	}

	public class UiContractEstimationForm extends UiFormDialog {
		BmoContractEstimation bmoContractEstimation;

		BmoWorkContract bmoWorkContract = new BmoWorkContract();

		int workContractId;
		TextBox codeTextBox = new TextBox();
		TextBox consecutiveTextBox = new TextBox();
		UiDateBox startDateDateBox= new UiDateBox();
		UiDateBox endDateDateBox= new UiDateBox();
		TextBox amountTextBox = new TextBox();
		TextBox taxTextBox = new TextBox();
		TextBox downPaymentTextBox = new TextBox();
		TextBox warrantyFundTextBox = new TextBox();
		TextBox totalTextBox = new TextBox();
		UiListBox statusUiListBox = new UiListBox(getUiParams());
		TextBox othersExpensesTextBox = new TextBox();
		UiListBox paymentStatusListBox = new UiListBox(getUiParams());
		TextArea descriptionOtherExpensesTextArea = new TextArea();

		private NumberFormat numberFormat = NumberFormat.getDecimalFormat();	
		protected FlowPanel formatPanel;

		// Estimaciones
		private UiEstimationItemGrid uiEstimationItemGrid;
		private FlowPanel uiEstimationItemPanel = new FlowPanel();

		ContractEstimationUpdater contractEstimationUpdater = new ContractEstimationUpdater();

		int contractEstimationId;

		String generalSection = "Datos Generales";
		String itemsSection = "Items";
		String amountsSection = "Montos";

		public UiContractEstimationForm(UiParams uiParams, int id) {
			super(uiParams, new BmoContractEstimation(), id);		
			this.contractEstimationId = id;

			// Panel de Item Estimacion
			uiEstimationItemPanel.setWidth("100%");

			ValueChangeHandler<String> textChangeHandler = new ValueChangeHandler<String>() {
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					othersExpensesChange();
				}
			};
			othersExpensesTextBox.addValueChangeHandler(textChangeHandler);

		}

		public UiContractEstimationForm(UiParams uiParams, int id, BmoWorkContract bmoWorkContract) {
			super(uiParams, new BmoContractEstimation(), id);
			this.bmoWorkContract = bmoWorkContract;
			this.contractEstimationId = id;
		}

		@Override
		public void populateFields() {
			bmoContractEstimation = (BmoContractEstimation)getBmObject();

			if (isSlave()) {
				// Es derivado de una obra, no es necesario mostrarlo
				int workContractId = Integer.parseInt(getUiParams().getUiProgramParams(bmoContractEstimation.getProgramCode()).getForceFilter().getValue());			
				getBmoWorkContract(workContractId);
			}

			formFlexTable.addSectionLabel(1, 0, generalSection, 2);
			formFlexTable.addField(2, 0, codeTextBox, bmoContractEstimation.getCode());
			formFlexTable.addField(3, 0, consecutiveTextBox, bmoContractEstimation.getConsecutive());
			formFlexTable.addField(4, 0, startDateDateBox, bmoContractEstimation.getStartDate());
			formFlexTable.addField(5, 0, endDateDateBox, bmoContractEstimation.getEndDate());		
			formFlexTable.addField(6, 0, descriptionOtherExpensesTextArea, bmoContractEstimation.getDescriptionOthersExpenses());		
			if (!newRecord) {
				formFlexTable.addSectionLabel(7, 0, itemsSection, 2);
				formFlexTable.addPanel(8, 0, uiEstimationItemPanel);
				uiEstimationItemGrid = new UiEstimationItemGrid(getUiParams(), uiEstimationItemPanel, bmoContractEstimation, contractEstimationUpdater);
				uiEstimationItemGrid.show();

				formFlexTable.addSectionLabel(9, 0, amountsSection, 2);
				formFlexTable.addField(10, 0, amountTextBox, bmoContractEstimation.getAmount());			
				formFlexTable.addField(11, 0, othersExpensesTextBox, bmoContractEstimation.getOthersExpenses());
				formFlexTable.addField(12, 0, totalTextBox, bmoContractEstimation.getTotal());
				formFlexTable.addLabelField(13, 0, bmoContractEstimation.getPaymentStatus());
				formFlexTable.addField(14, 0, statusUiListBox, bmoContractEstimation.getStatus());
			} else {
				formFlexTable.addLabelField(7, 0, bmoContractEstimation.getPaymentStatus());			
				formFlexTable.addField(8, 0, statusUiListBox, bmoContractEstimation.getStatus());
			}

			statusEffect();
		}

		@Override
		public void formListChange(ChangeEvent event) {
			if (event.getSource() == statusUiListBox) {
				update("Desea cambiar el estatus de la estimación?");
			}
			statusEffect();
		}

		@Override
		public void close() {
			UiContractEstimation uiContractEstimation;
			if (bmoWorkContract.getId() > 0 )
				uiContractEstimation = new UiContractEstimation(getUiParams(), bmoWorkContract);
			else 
				uiContractEstimation = new UiContractEstimation(getUiParams());
			uiContractEstimation.show();	
		}

		@Override
		public void saveNext() {
			if (newRecord) {
				UiContractEstimationForm uiContractEstimationForm;
				if (bmoWorkContract.getId() > 0)
					uiContractEstimationForm = new UiContractEstimationForm(getUiParams(), getBmObject().getId(), bmoWorkContract);
				else
					uiContractEstimationForm = new UiContractEstimationForm(getUiParams(), getBmObject().getId());

				uiContractEstimationForm.show();
			} else {
				UiContractEstimation uiContractEstimation;
				if (bmoWorkContract.getId() > 0) 
					uiContractEstimation = new UiContractEstimation(getUiParams(), bmoWorkContract);
				else 
					uiContractEstimation = new UiContractEstimation(getUiParams());

				uiContractEstimation.show();
			}	
		}

		public void getBmoWorkContract(int workContractId) {

			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {}
					else showErrorMessage(this.getClass().getName() + "-get() ERROR: " + caught.toString());
				}

				@Override
				public void onSuccess(BmObject result) {
					stopLoading();
					setBmoWorkContract((BmoWorkContract)result);
				}
			};

			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().get(bmoWorkContract.getPmClass(), workContractId, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getBmoOrder() ERROR: " + e.toString());
			}
		}

		private void setBmoWorkContract(BmoWorkContract bmoWorkContract) {
			this.bmoWorkContract = bmoWorkContract;
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoContractEstimation.setId(id);

			bmoContractEstimation.getWorkContractId().setValue(bmoWorkContract.getId());		
			bmoContractEstimation.getCode().setValue(codeTextBox.getText());
			bmoContractEstimation.getConsecutive().setValue(consecutiveTextBox.getText());
			bmoContractEstimation.getStartDate().setValue(startDateDateBox.getTextBox().getText());
			bmoContractEstimation.getEndDate().setValue(endDateDateBox.getTextBox().getText());
			bmoContractEstimation.getOthersExpenses().setValue(othersExpensesTextBox.getText());
			bmoContractEstimation.getDescriptionOthersExpenses().setValue(descriptionOtherExpensesTextArea.getText());
			bmoContractEstimation.getStatus().setValue(statusUiListBox.getSelectedCode());		

			return bmoContractEstimation;
		}


		public void statusEffect() {		
			statusUiListBox.setEnabled(false);
			codeTextBox.setEnabled(false);			

			if (bmoContractEstimation.getStatus().equals(BmoContractEstimation.STATUS_AUTHORIZED)) {				
				othersExpensesTextBox.setEnabled(false);
				amountTextBox.setEnabled(false);
				totalTextBox.setEnabled(false);
				consecutiveTextBox.setEnabled(false);
			}

			if (!newRecord && getSFParams().hasSpecialAccess(BmoContractEstimation.ACCESS_CHANGESTATUS)) {
				if (bmoContractEstimation.getPaymentStatus().equals(BmoContractEstimation.PAYMENTSTATUS_PENDING))
					statusUiListBox.setEnabled(true);
			}

			setAmount(bmoContractEstimation);
		}

		public void reset() {
			updateAmount(id);
			uiEstimationItemGrid.show();		
		}

		public void othersExpensesChange() {
			try {
				bmoContractEstimation.getOthersExpenses().setValue(othersExpensesTextBox.getText());
				saveOtherExpensesChange();
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-othersExpensesChange() ERROR: " + e.toString());
			}
		}

		public void saveOtherExpensesChange() {
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
					processContractEstimationUpdateResult(result);
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().save(bmoContractEstimation.getPmClass(), bmoContractEstimation, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-saveOtherExpensesChange(): ERROR " + e.toString());
			}
		}

		public void processContractEstimationUpdateResult(BmUpdateResult result) {
			if (result.hasErrors()) showFormMsg("Errores al actualizar otros cargos.", "Errores al actualizar otros cargos: " + result.errorsToString());
			else updateAmount(id);
		}

		public void setAmount(BmoContractEstimation bmoContractEstimation) {
			amountTextBox.setText(numberFormat.format(bmoContractEstimation.getAmount().toDouble()));
			othersExpensesTextBox.setText(numberFormat.format(bmoContractEstimation.getOthersExpenses().toDouble()));
			totalTextBox.setText(numberFormat.format(bmoContractEstimation.getTotal().toDouble()));
			setAmount("" + bmoContractEstimation.getAmount().toDouble(), "" + bmoContractEstimation.getOthersExpenses().toDouble(), "" + bmoContractEstimation.getTotal().toDouble());
		}

		private void setAmount(String amount, String otherExp, String total) {		
			double a = Double.parseDouble(amount);
			amountTextBox.setText(numberFormat.format(a));

			a = Double.parseDouble(otherExp);
			othersExpensesTextBox.setText(numberFormat.format(a));

			a = Double.parseDouble(total);
			totalTextBox.setText(numberFormat.format(a));
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
					setAmount((BmoContractEstimation)result);
				}
			};
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().get(bmoContractEstimation.getPmClass(), id, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-updateAmount(): ERROR " + e.toString());
			}
		}

		protected class ContractEstimationUpdater {
			public void changeContractEstimation() {
				stopLoading();
				reset();
			}		
		}
	}
}