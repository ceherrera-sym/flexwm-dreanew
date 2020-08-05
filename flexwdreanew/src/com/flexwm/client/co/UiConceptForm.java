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

import com.flexwm.shared.co.BmoConcept;
import com.flexwm.shared.co.BmoWork;
import com.flexwm.shared.fi.BmoBudgetItemType;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiForm;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmException;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;


public class UiConceptForm extends UiForm{	
	
	BmoConcept bmoConcept;
	BmoWork bmoWork = new BmoWork();
	
	// Items de Concept
	private UiWorkItemGrid uiWorkItemGrid;
	private FlowPanel uiConceptItemPanel = new FlowPanel();
	
	ConceptUpdater conceptUpdater = new ConceptUpdater();
	
	
	UiSuggestBox workSuggestBox = new UiSuggestBox(new BmoWork());
	TextBox quantityTextBox = new TextBox();
	TextBox subTotalTextBox = new TextBox();
	TextBox totalTextBox = new TextBox();
	TextBox codeTextBox = new TextBox();
	TextBox nameTextBox = new TextBox();
	UiSuggestBox typeCostCenterSuggest = new UiSuggestBox(new BmoBudgetItemType());
	TextArea descriptionTextArea = new TextArea();	
		
	private NumberFormat numberFormat = NumberFormat.getDecimalFormat();
	

	
	
	public UiConceptForm(UiParams uiParams, int id) {
		super(uiParams, new BmoConcept(), id);
		
		// Panel de staff
		uiConceptItemPanel.setWidth("100%");
		
		ValueChangeHandler<String> textChangeHandler = new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				changePrice();
			}
		};
		quantityTextBox.addValueChangeHandler(textChangeHandler);
	}
	
	public void changePrice() {
		try {
			bmoConcept.getQuantity().setValue(quantityTextBox.getText());
			saveChangePrice();
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-changePrice() ERROR: " + e.toString());
		}
	}
	
	public void saveChangePrice() {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-saveChangePrice(): ERROR " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				processConceptUpdateResult(result);
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().save(bmoConcept.getPmClass(), bmoConcept, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-saveChangePrice(): ERROR " + e.toString());
		}
	}
	
	public void processConceptUpdateResult(BmUpdateResult result) {
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
				setAmount((BmoConcept)result);
			}
		};
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().get(bmoConcept.getPmClass(), id, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-updateAmount(): ERROR " + e.toString());
		}
	}
	
	public void setAmount(BmoConcept bmoConcept) {
		numberFormat = NumberFormat.getCurrencyFormat();
		subTotalTextBox.setText(numberFormat.format(bmoConcept.getSubTotal().toDouble()));		
		totalTextBox.setText(numberFormat.format(bmoConcept.getTotal().toDouble()));
		setAmount("" + bmoConcept.getSubTotal().toDouble(), ""  + bmoConcept.getTotal().toDouble());
	}
	
	private void setAmount(String subTotal, String total ) {		
		double a = Double.parseDouble(subTotal);
		subTotalTextBox.setText(numberFormat.format(a));
		
		a = Double.parseDouble(total);
		totalTextBox.setText(numberFormat.format(a));
	}
	
	@Override
	public void saveNext() {
		UiConceptList uiConceptList = new UiConceptList(getUiParams());
		uiConceptList.show();
	}
	
	public void reset(){
		updateAmount(id);
		uiWorkItemGrid.show();	
	}
	
	
	@Override
	public void populateFields(){
		bmoConcept = (BmoConcept)getBmObject();		
		formFlexTable.addField(1, 0, codeTextBox, bmoConcept.getCode());
		formFlexTable.addField(1, 2, nameTextBox, bmoConcept.getName());
		formFlexTable.addField(2, 0, descriptionTextArea, bmoConcept.getDescription());
		formFlexTable.addField(2, 2, typeCostCenterSuggest, bmoConcept.getTypeCostCenterId());
		
		if (!newRecord) {			
			
			formFlexTable.addSectionLabel(3, 0, "Items", 2);
			formFlexTable.addPanel(4, 0, uiConceptItemPanel);
			//uiWorkItemGrid = new UiWorkItemGrid(getUiParams(), uiConceptItemPanel, bmoConcept, conceptUpdater);
			//uiWorkItemGrid.show();

			formFlexTable.addSectionLabel(5, 0, "Totales", 2);
						
			formFlexTable.addField(6, 2, totalTextBox, bmoConcept.getTotal());
		}	
		
		if (isSlave()) {
			// Es derivado de una obra, no es necesario mostrarlo
			int workId = Integer.parseInt(getUiParams().getUiProgramParams(bmoConcept.getProgramCode()).getForceFilter().getValue());			
			getBmoWork(workId);
		}
		
		statusEffect();

	}
	
	public void getBmoWork(int workId){


		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {}
				else showErrorMessage(this.getClass().getName() + "-get() ERROR: " + caught.toString());
			}

			public void onSuccess(BmObject result) {
				stopLoading();
				setBmoWork((BmoWork)result);
			}
		};

		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().get(bmoWork.getPmClass(), workId, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-getBmoOrder() ERROR: " + e.toString());
		}
	}
	
	private void setBmoWork(BmoWork bmoWork) {
		this.bmoWork = bmoWork;
		/*try {
			
			
		} catch (BmException e) {
			showSystemMessage("No se puede asignar cliente a la Cuenta x Cobrar.");
		}*/
	}
	
	@Override
	public BmObject populateBObject() throws BmException {
		bmoConcept.setId(id);
		bmoConcept.getWorkId().setValue(bmoWork.getId());
		bmoConcept.getCode().setValue(codeTextBox.getText());
		bmoConcept.getName().setValue(nameTextBox.getText());	
		bmoConcept.getQuantity().setValue(quantityTextBox.getText());
		bmoConcept.getSubTotal().setValue(subTotalTextBox.getText());
		bmoConcept.getTotal().setValue(totalTextBox.getText());
		bmoConcept.getDescription().setValue(descriptionTextArea.getText());
		bmoConcept.getTypeCostCenterId().setValue(typeCostCenterSuggest.getSelectedId());
		
		return bmoConcept;
	}
	
	@Override
	public void close() {
		UiConceptList uiConceptList = new UiConceptList(getUiParams());
		uiConceptList.show();
	}
	
	public void statusEffect() {
		subTotalTextBox.setEnabled(false);
		totalTextBox.setEnabled(false);
		
		if (bmoConcept.getBmoWork().getStatus().equals(BmoWork.STATUS_AUTHORIZED)) {
			deleteButton.setVisible(false);
			saveButton.setVisible(false);
			quantityTextBox.setEnabled(false);
		} 
		
		setAmount(bmoConcept);
	}
	
		
	protected class ConceptUpdater {
		public void changeConcept() {
			stopLoading();
			reset();
		}		
	}
}


