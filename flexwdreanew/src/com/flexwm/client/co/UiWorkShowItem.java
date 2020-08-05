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
import com.flexwm.shared.co.BmoDevelopmentPhase;
import com.flexwm.shared.co.BmoWork;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmException;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFException;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoUser;


/**
 * @author jhernandez
 *
 */

public class UiWorkShowItem extends UiFormDialog {
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

	private UiWorkItemGrid uiWorkItemGrid;
	private FlowPanel uiWorkItemPanel = new FlowPanel();

	protected FlowPanel formatPanel;

	public UiWorkShowItem(UiParams uiParams, int id) {
		super(uiParams, new BmoWork(), id);

		ValueChangeHandler<String> textChangeHandler = new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				indirectChangePrice();
			}
		};
		indirectsTextBox.addValueChangeHandler(textChangeHandler);
	}

	@Override
	public void populateFields(){
		bmoWork = (BmoWork)getBmObject();

		if (newRecord) {
			try {
				bmoWork.getDateCreate().setValue(GwtUtil.dateToString(new Date(), getSFParams().getDateFormat()));				
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-populateFields(): ERROR " + e.toString());
			}			

		}

		formFlexTable.addField(1, 0, codeTextBox, bmoWork.getCode());
		formFlexTable.addField(2, 0, nameTextBox, bmoWork.getName());
		formFlexTable.addField(3, 0, dateCreateBox, bmoWork.getDateCreate());
		formFlexTable.addField(4, 0, descriptionTextArea, bmoWork.getDescription());
		/*formFlexTable.addField(3, 0, developmentPhaseListBox, bmoWork.getDevelopmentPhaseId());
		formFlexTable.addField(3, 2, userIdSuggestBox, bmoWork.getUserId());
		formFlexTable.addField(4, 0, typeCostCenter, bmoWork.getTypeCostCenterId());
		formFlexTable.addField(4, 2, isMasterCheckBox, bmoWork.getIsMaster());		
		formFlexTable.addField(5, 0, companyIdListBox, bmoWork.getCompanyId());
		formFlexTable.addField(5, 2, statusListBox, bmoWork.getStatus());*/

		formFlexTable.addPanel(5, 0, uiWorkItemPanel, 2);
		uiWorkItemGrid = new UiWorkItemGrid(getUiParams(), uiWorkItemPanel, bmoWork, workUpdater);

		if (!newRecord) {
			formFlexTable.addField(6, 0, totalTextBox, bmoWork.getTotal());
		}

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
		bmoWork.getDateCreate().setValue(dateCreateBox.getTextBox().getText());

		return bmoWork;
	}

	@Override
	public void close() {
		UiWorkDetail uiWorkDetail = new UiWorkDetail(getUiParams(), getBmObject().getId());
		uiWorkDetail.show();
	}

	@Override
	public void formListChange(ChangeEvent event) {
		if (event.getSource() == statusListBox) {
			update("Desea cambiar el Status de la Obrar?");
			reset();
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

		setAmount(bmoWork);
	}

	@Override
	public void postShow() {
		saveButton.setVisible(false);
		deleteButton.setVisible(false);
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
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-saveDiscount(): ERROR " + caught.toString());
			}

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

	public void updateAmount(int id){
		AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-updateAmount() ERROR: " + caught.toString());
			}

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

	public void reset(){
		updateAmount(id);
		uiWorkItemGrid.show();	
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


