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

import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.sf.BmoCompany;
import com.flexwm.shared.op.BmoSupplier;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.flexwm.shared.co.BmoDevelopmentPhase;
import com.flexwm.shared.co.BmoWork;
import com.flexwm.shared.co.BmoWorkContract;
import com.flexwm.shared.fi.BmoBudgetItem;


/**
 * @author smuniz
 *
 */

public class UiWorkContract extends UiList {
	BmoWorkContract bmoWorkContract;

	public UiWorkContract(UiParams uiParams) {
		super(uiParams, new BmoWorkContract());
		bmoWorkContract = (BmoWorkContract)getBmObject();
	}

	@Override
	public void postShow() {

		if (isMaster()) {
			addFilterListBox(new UiListBox(getUiParams(), new BmoDevelopmentPhase()), bmoWorkContract.getBmoWork().getBmoDevelopmentPhase());
			addFilterSuggestBox(new UiSuggestBox(new BmoSupplier()), new BmoSupplier(), bmoWorkContract.getSupplierId());
			if (!isMobile()) {
				addFilterSuggestBox(new UiSuggestBox(new BmoWork()), new BmoWork(), bmoWorkContract.getWorkId());
				addFilterListBox(new UiListBox(getUiParams(), new BmoCompany()), bmoWorkContract.getBmoCompany());
				addStaticFilterListBox(new UiListBox(getUiParams(), bmoWorkContract.getStatus()), bmoWorkContract, bmoWorkContract.getStatus());
				addDateRangeFilterListBox(bmoWorkContract.getEndDate());
			}
		}
	}

	@Override
	public void create() {
		UiWorkContractForm uiWorkContractForm = new UiWorkContractForm(getUiParams(), 0);
		uiWorkContractForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoWorkContract = (BmoWorkContract)bmObject;
		// Si esta asignado el tipo de proyecto, envia directo al dashboard
		if (bmoWorkContract.getId() > 0) {
			UiWorkContractDetail uiWorkContractDetail = new UiWorkContractDetail(getUiParams(), bmoWorkContract.getId());
			uiWorkContractDetail.show();
		} else {
			UiWorkContractForm uiWorkContractForm = new UiWorkContractForm(getUiParams(), bmoWorkContract.getId());
			uiWorkContractForm.show();
		}		
	}

	@Override
	public void edit(BmObject bmObject) {
		UiWorkContractForm uiWorkContractForm = new UiWorkContractForm(getUiParams(), bmObject.getId());
		uiWorkContractForm.show();
	}

	public class UiWorkContractForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		UiSuggestBox workSuggestBox = new UiSuggestBox(new BmoWork());
		UiListBox companyIdUiListBox = new UiListBox(getUiParams(), new BmoCompany());
		UiSuggestBox supplierIdUiSuggestBox = new UiSuggestBox(new BmoSupplier());
		UiListBox estimationTypeUiListBox = new UiListBox(getUiParams());
		UiDateBox startDateDateBox = new UiDateBox();
		UiDateBox endDateDateBox = new UiDateBox();
		TextBox downPaymentTextBox = new TextBox();
		TextBox percentDownPaymentTextBox = new TextBox();
		CheckBox hasTaxCheckBox = new CheckBox();
		TextBox taxTextBox = new TextBox();

		TextBox dailySanctionTextBox = new TextBox();
		CheckBox hasSanctionCheckBox = new CheckBox();

		TextArea observationsTextArea = new TextArea();
		TextBox quantityTextBox = new TextBox();
		TextBox subTotalTextBox = new TextBox();
		TextBox totalTextBox = new TextBox();
		//Costo Final
		TextBox totalRealTextBox = new TextBox();

		UiDateBox paymentDateDateBox = new UiDateBox();
		UiListBox statusUiListBox = new UiListBox(getUiParams());
		UiListBox paymentStatusListBox = new UiListBox(getUiParams());
		UiListBox isCloseListBox = new UiListBox(getUiParams());

		TextBox guaranteeFundTextBox = new TextBox();
		TextBox percentGuaranteeFundTextBox = new TextBox();
		UiListBox downPaymentStatusUiListBox = new UiListBox(getUiParams());
		TextArea commentsTextArea = new TextArea();
		UiDateBox dateContractDateBox = new UiDateBox();
		UiListBox auxStatusUiListBox = new UiListBox(getUiParams());
		CheckBox isClosedTextBox = new CheckBox();
		TextArea historyTextArea = new TextArea();
		TextArea descriptionIsClosedTextArea = new TextArea();

		UiListBox budgetItemListBox = new UiListBox(getUiParams(), new BmoBudgetItem());

		TextArea lotDescriptionTextArea = new TextArea();
		CheckBox finishedCheckBox = new CheckBox();
		TextBox totalWarrantyTextBox = new TextBox();
		TextBox totalConceptsTextBox = new TextBox();

		BmoWorkContract bmoWorkContract;
		WorkContractUpdater workContractUpdater = new WorkContractUpdater();

		private NumberFormat numberFormat = NumberFormat.getDecimalFormat();

		// Items de conceptos del contrato
		private UiWorkContractConceptItemGrid uiWorkContractConceptItemGrid;
		private FlowPanel uiWorkContractConceptItemPanel = new FlowPanel();

		public UiWorkContractForm(UiParams uiParams, int id) {
			super(uiParams, new BmoWorkContract(), id);		

			// Panel de items del contracto de obra
			uiWorkContractConceptItemPanel.setWidth("100%");

			hasTaxCheckBox.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					taxAppliesChange();
				}
			});

			hasSanctionCheckBox.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					sanctionAppliesChange();
				}
			});

			// Número de Paquetes
			ValueChangeHandler<String> quantityTextChangeHandler = new ValueChangeHandler<String>() {
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					quantityChange();
				}
			};			
			quantityTextBox.addValueChangeHandler(quantityTextChangeHandler);

			//% de Fondo de garantia
			ValueChangeHandler<String> textChangeHandler = new ValueChangeHandler<String>() {
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					guaranteeFundChange();
				}
			};
			percentGuaranteeFundTextBox.addValueChangeHandler(textChangeHandler);

			// Monto del Anticipo
			ValueChangeHandler<String> percentDownPaymentTextChangeHandler = new ValueChangeHandler<String>() {
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					downPaymentChange();;
				}
			};
			percentDownPaymentTextBox.addValueChangeHandler(percentDownPaymentTextChangeHandler);


		}

		@Override
		public void populateFields() {
			bmoWorkContract = (BmoWorkContract)getBmObject();
			formFlexTable.addField(1, 0, codeTextBox, bmoWorkContract.getCode());
			populateWorks();
			formFlexTable.addField(2, 0, workSuggestBox, bmoWorkContract.getWorkId());

			formFlexTable.addField(3, 0, nameTextBox, bmoWorkContract.getName());
			formFlexTable.addField(4, 0, descriptionTextArea, bmoWorkContract.getDescription());
			formFlexTable.addField(5, 0, supplierIdUiSuggestBox, bmoWorkContract.getSupplierId());
			formFlexTable.addField(6, 0, startDateDateBox, bmoWorkContract.getStartDate());
			formFlexTable.addField(7, 0, endDateDateBox, bmoWorkContract.getEndDate());		

			if (!newRecord) {
				formFlexTable.addField(8, 0, companyIdUiListBox, bmoWorkContract.getCompanyId());
				formFlexTable.addField(9, 0, paymentDateDateBox, bmoWorkContract.getPaymentDate());
				formFlexTable.addField(10, 0, dateContractDateBox, bmoWorkContract.getDateContract());
				formFlexTable.addField(11, 0, commentsTextArea, bmoWorkContract.getComments());

				setPopulateBudgetItemFilters();
				formFlexTable.addField(12, 0, budgetItemListBox, bmoWorkContract.getBudgetItemId());

				formFlexTable.addSectionLabel(13, 0, "Items", 4);
				formFlexTable.addPanel(14, 0, uiWorkContractConceptItemPanel, 4);
				uiWorkContractConceptItemGrid = new UiWorkContractConceptItemGrid(getUiParams(), uiWorkContractConceptItemPanel, bmoWorkContract, workContractUpdater);
				uiWorkContractConceptItemGrid.show();

				//formFlexTable.addField(9, 2, totalConceptsTextBox, bmoWorkContract.getTotalConcepts());

				formFlexTable.addSectionLabel(15, 0, "Totales",4);			
				formFlexTable.addField(16, 0, quantityTextBox, bmoWorkContract.getQuantity());
				formFlexTable.addField(17, 0, subTotalTextBox, bmoWorkContract.getSubTotal());
				formFlexTable.addField(18, 0, hasTaxCheckBox, bmoWorkContract.getHasTax());			
				formFlexTable.addFieldReadOnly(19, 0, taxTextBox, bmoWorkContract.getTax());
				formFlexTable.addField(20, 0, hasSanctionCheckBox, bmoWorkContract.getHasSanction());
				formFlexTable.addFieldReadOnly(21, 0, totalTextBox, bmoWorkContract.getTotal());
				if (bmoWorkContract.getStatus().equals(BmoWorkContract.STATUS_CLOSED)) 
					populateTotalReal();
				formFlexTable.addField(23, 0, percentDownPaymentTextBox, bmoWorkContract.getPercentDownPayment());
				formFlexTable.addFieldReadOnly(24, 0, downPaymentTextBox, bmoWorkContract.getDownPayment());
				formFlexTable.addField(25, 0, percentGuaranteeFundTextBox, bmoWorkContract.getPercentGuaranteeFund());
				formFlexTable.addFieldReadOnly(26, 0, guaranteeFundTextBox, bmoWorkContract.getGuaranteeFund());						
				formFlexTable.addFieldReadOnly(27, 0, dailySanctionTextBox, bmoWorkContract.getDailySanction());
				formFlexTable.addField(28, 0, paymentStatusListBox, bmoWorkContract.getPaymentStatus());
				formFlexTable.addField(29, 0, statusUiListBox, bmoWorkContract.getStatus());

			} else {
				formFlexTable.addField(8, 0, budgetItemListBox, bmoWorkContract.getBudgetItemId());
				formFlexTable.addField(9, 0, paymentStatusListBox, bmoWorkContract.getPaymentStatus());
				formFlexTable.addField(10, 0, statusUiListBox, bmoWorkContract.getStatus());			
			}

			hasTaxCheckBox.setValue(bmoWorkContract.getHasTax().toBoolean());
			hasSanctionCheckBox.setValue(bmoWorkContract.getHasSanction().toBoolean());

			statusEffect();
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoWorkContract.setId(id);
			bmoWorkContract.getCode().setValue(codeTextBox.getText());
			bmoWorkContract.getName().setValue(nameTextBox.getText());
			bmoWorkContract.getDescription().setValue(descriptionTextArea.getText());
			bmoWorkContract.getWorkId().setValue(workSuggestBox.getSelectedId());
			bmoWorkContract.getCompanyId().setValue(companyIdUiListBox.getSelectedId());
			bmoWorkContract.getSupplierId().setValue(supplierIdUiSuggestBox.getSelectedId());
			bmoWorkContract.getDownPayment().setValue(downPaymentTextBox.getText());
			bmoWorkContract.getPercentDownPayment().setValue(percentDownPaymentTextBox.getText());
			bmoWorkContract.getHasTax().setValue(hasTaxCheckBox.getValue());		
			bmoWorkContract.getTax().setValue(taxTextBox.getText());
			bmoWorkContract.getSubTotal().setValue(subTotalTextBox.getText());
			bmoWorkContract.getTotal().setValue(totalTextBox.getText());
			bmoWorkContract.getTotalReal().setValue(totalRealTextBox.getText());
			bmoWorkContract.getGuaranteeFund().setValue(guaranteeFundTextBox.getText());
			bmoWorkContract.getPercentGuaranteeFund().setValue(percentGuaranteeFundTextBox.getText());
			bmoWorkContract.getQuantity().setValue(quantityTextBox.getText());
			bmoWorkContract.getStatus().setValue(statusUiListBox.getSelectedCode());
			bmoWorkContract.getPaymentStatus().setValue(paymentStatusListBox.getSelectedCode());		
			bmoWorkContract.getStartDate().setValue(startDateDateBox.getTextBox().getText());
			bmoWorkContract.getEndDate().setValue(endDateDateBox.getTextBox().getText());
			bmoWorkContract.getDailySanction().setValue(dailySanctionTextBox.getText());		
			bmoWorkContract.getHasSanction().setValue(hasSanctionCheckBox.getValue());
			bmoWorkContract.getDateContract().setValue(dateContractDateBox.getTextBox().getText());
			bmoWorkContract.getPaymentDate().setValue(paymentDateDateBox.getTextBox().getText());
			bmoWorkContract.getBudgetItemId().setValue(budgetItemListBox.getSelectedId());
			bmoWorkContract.getComments().setValue(commentsTextArea.getText());

			return bmoWorkContract;
		}

		public void populateTotalReal() {
			formFlexTable.addFieldReadOnly(22, 0, totalRealTextBox, bmoWorkContract.getTotalReal());		
		}

		public void guaranteeFundChange() {
			try {
				bmoWorkContract.getPercentGuaranteeFund().setValue(percentGuaranteeFundTextBox.getText());
				saveGuaranteeFundChange();
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-guaranteeFundChange() ERROR: " + e.toString());
			}
		}	

		public void saveGuaranteeFundChange() {
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-guaranteeFundChange(): ERROR " + caught.toString());
				}

				@Override
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					processWorkContractUpdateResult(result);
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().save(bmoWorkContract.getPmClass(), bmoWorkContract, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-guaranteeFundChange(): ERROR " + e.toString());
			}
		}

		public void downPaymentChange() {
			try {
				bmoWorkContract.getPercentDownPayment().setValue(percentDownPaymentTextBox.getText());
				saveDownPaymentChange();
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-downPaymentChange() ERROR: " + e.toString());
			}
		}	

		public void saveDownPaymentChange() {
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-saveDownPaymentChange(): ERROR " + caught.toString());
				}

				@Override
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					processWorkContractUpdateResult(result);
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().save(bmoWorkContract.getPmClass(), bmoWorkContract, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-saveDownPaymentChange(): ERROR " + e.toString());
			}
		}


		public void quantityChange() {
			try {
				bmoWorkContract.getQuantity().setValue(quantityTextBox.getText());
				saveQuantityChange();
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-quantityChange() ERROR: " + e.toString());
			}
		}	

		public void saveQuantityChange() {
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-saveQuantityChange(): ERROR " + caught.toString());
				}

				@Override
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					processWorkContractUpdateResult(result);
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().save(bmoWorkContract.getPmClass(), bmoWorkContract, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-saveQuantityChange(): ERROR " + e.toString());
			}
		}

		public void reset() {
			updateAmount(id);
			uiWorkContractConceptItemGrid.show();	
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
					setAmount((BmoWorkContract)result);
				}
			};
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().get(bmoWorkContract.getPmClass(), id, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-updateAmount(): ERROR " + e.toString());
			}
		}

		public void setAmount(BmoWorkContract bmoWorkContract) {

			totalConceptsTextBox.setText(numberFormat.format(bmoWorkContract.getTotalConcepts().toDouble()));

			downPaymentTextBox.setText(numberFormat.format(bmoWorkContract.getDownPayment().toDouble()));
			guaranteeFundTextBox.setText(numberFormat.format(bmoWorkContract.getGuaranteeFund().toDouble()));

			subTotalTextBox.setText(numberFormat.format(bmoWorkContract.getSubTotal().toDouble()));
			taxTextBox.setText(numberFormat.format(bmoWorkContract.getTax().toDouble()));		
			totalTextBox.setText(numberFormat.format(bmoWorkContract.getTotal().toDouble()));
			totalRealTextBox.setText(numberFormat.format(bmoWorkContract.getTotalReal().toDouble()));

			dailySanctionTextBox.setText(numberFormat.format(bmoWorkContract.getDailySanction().toDouble()));

		}

		@Override
		public void saveNext() {
			
				UiWorkContractDetail uiWorkContractDetail = new UiWorkContractDetail(getUiParams(), getBmObject().getId());
				uiWorkContractDetail.show();
			
		}

		public void taxAppliesChange() {
			try {
				bmoWorkContract.getHasTax().setValue(hasTaxCheckBox.getValue());
				saveAmountChange();
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-taxAppliesChange() ERROR: " + e.toString());
			}
		}

		public void sanctionAppliesChange() {
			try {
				bmoWorkContract.getHasSanction().setValue(hasSanctionCheckBox.getValue());
				saveAmountChange();
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-taxAppliesChange() ERROR: " + e.toString());
			}
		}

		public void saveAmountChange() {
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-saveAmountChange(): ERROR " + caught.toString());
				}

				@Override
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					processWorkContractUpdateResult(result);
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().save(bmoWorkContract.getPmClass(), bmoWorkContract, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-saveAmountChange(): ERROR " + e.toString());
			}
		}

		public void processWorkContractUpdateResult(BmUpdateResult result) {
			if (result.hasErrors()) showFormMsg("Errores el % de Garantia.", "Errores el % de Garantia: " + result.errorsToString());
			else updateAmount(id);
		}

		private void populateNameDescription(BmObject bmObject) {

			BmoWork bmoWork = (BmoWork)bmObject;

			try {
				bmoWorkContract.getName().setValue(bmoWork.getCode().toString());
				bmoWorkContract.getDescription().setValue(bmoWork.getDescription().toString());

				nameTextBox.setText(bmoWork.getCode().toString());
				descriptionTextArea.setText(bmoWork.getName().toString());

			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-populateNameDescription(): ERROR al obtener Nombre y Descripción: " + e.toString());
			}

			formFlexTable.addField(3, 0, nameTextBox, bmoWorkContract.getName());
			formFlexTable.addField(4, 0, descriptionTextArea, bmoWorkContract.getDescription());

		}

		/*private void populateBudgetItem(BmObject bmObject) {

			BmoWork bmoWork = (BmoWork)bmObject;

			// Filtros de ordenes de compra
			budgetItemListBox.clear();
			budgetItemListBox.clearFilters();

			BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();


			if (!(bmoWorkContract.getBudgetItemId().toInteger() > 0)) {
				// Filtrar presupuestos autorizados
				BmFilter filterAuthorized = new BmFilter();		
				filterAuthorized.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBmoBudget().getStatus(), "" + BmoBudget.STATUS_AUTHORIZED);
				budgetItemListBox.addFilter(filterAuthorized);

				// Se rehacen filtros
				BmFilter bmFilterByCompany = new BmFilter();
				bmFilterByCompany.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBmoBudget().getCompanyId(), bmoWork.getCompanyId().toInteger());
				budgetItemListBox.addBmFilter(bmFilterByCompany);	

			} else {
				//Si tiene permiso puede seleccionar un item de otra Empresa
				if (!newRecord && !getSFParams().hasSpecialAccess(BmoWorkContract.ACCESS_OTHERBUDGETITEM)) {			
					BmFilter bmFilterByCompany = new BmFilter();
					bmFilterByCompany.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBmoBudget().getCompanyId(), bmoWorkContract.getBmoWork().getCompanyId().toInteger());
					budgetItemListBox.addBmFilter(bmFilterByCompany);
				}	
			}

			if (!newRecord)
				formFlexTable.addField(6, 2, budgetItemListBox, bmoWorkContract.getBudgetItemId());
			else
				formFlexTable.addField(5, 2, budgetItemListBox, bmoWorkContract.getBudgetItemId());
		}*/

		
		private void populateWorks() {
			workSuggestBox.clear();
			setPopulateWorksFilters();
		}

		private void setPopulateWorksFilters() {

			// Filtros de Obra
			BmoWork bmoWork = new BmoWork();
			BmFilter bmFilterWorkAutorized = new BmFilter();
			BmFilter bmFilterWorkNotMaster = new BmFilter();

			bmFilterWorkAutorized.setValueFilter(bmoWork.getKind(), bmoWork.getStatus(), "" + BmoWork.STATUS_AUTHORIZED);
			workSuggestBox.addFilter(bmFilterWorkAutorized);

			bmFilterWorkNotMaster.setValueFilter(bmoWork.getKind(), bmoWork.getIsMaster(),"0");

			workSuggestBox.addFilter(bmFilterWorkAutorized);
			workSuggestBox.addFilter(bmFilterWorkNotMaster);
		}	

		private void statusEffect() {		
			statusUiListBox.setEnabled(false);
			paymentStatusListBox.setEnabled(false);
			codeTextBox.setEnabled(false);
			nameTextBox.setEnabled(false);
			workSuggestBox.setEnabled(false);
			supplierIdUiSuggestBox.setEnabled(false);
			companyIdUiListBox.setEnabled(false);
			subTotalTextBox.setEnabled(false);
			totalConceptsTextBox.setEnabled(false);
			quantityTextBox.setEnabled(false);
			percentGuaranteeFundTextBox.setEnabled(false);
			percentDownPaymentTextBox.setEnabled(false);
			hasTaxCheckBox.setEnabled(false);
			hasSanctionCheckBox.setEnabled(false);
			descriptionTextArea.setEnabled(false);
			startDateDateBox.setEnabled(false);
			endDateDateBox.setEnabled(false);
			budgetItemListBox.setEnabled(true);
			isCloseListBox.setEnabled(false);
			dateContractDateBox.setEnabled(false);
			commentsTextArea.setEnabled(false);
			companyIdUiListBox.setEnabled(false);
			totalRealTextBox.setEnabled(false);



			if (newRecord) {

				nameTextBox.setEnabled(true);
				descriptionTextArea.setEnabled(true);			
				workSuggestBox.setEnabled(true);
				companyIdUiListBox.setEnabled(true);
				supplierIdUiSuggestBox.setEnabled(true);
				quantityTextBox.setEnabled(true);			
				percentGuaranteeFundTextBox.setEnabled(true);
				percentDownPaymentTextBox.setEnabled(true);
				hasTaxCheckBox.setEnabled(true);
				hasSanctionCheckBox.setEnabled(true);	

				commentsTextArea.setEnabled(true);

			} else if (bmoWorkContract.getStatus().equals(BmoWorkContract.STATUS_REVISION)) {
				nameTextBox.setEnabled(true);
				descriptionTextArea.setEnabled(true);
				commentsTextArea.setEnabled(true);
				quantityTextBox.setEnabled(true);
				percentGuaranteeFundTextBox.setEnabled(true);
				percentDownPaymentTextBox.setEnabled(true);
				hasTaxCheckBox.setEnabled(true);
				hasSanctionCheckBox.setEnabled(true);
				startDateDateBox.setEnabled(true);
				endDateDateBox.setEnabled(true);
				dateContractDateBox.setEnabled(true);

				if (!(bmoWorkContract.getCompanyId().toInteger() > 0))
					companyIdUiListBox.setEnabled(true);



			} else if (bmoWorkContract.getStatus().equals(BmoWorkContract.STATUS_CLOSED)) {
				totalRealTextBox.setEnabled(false);
				populateTotalReal();
			}

			if (!newRecord && getSFParams().hasSpecialAccess(BmoWorkContract.ACCESS_CHANGESTATUS)) {
				statusUiListBox.setEnabled(true);
				isCloseListBox.setEnabled(true);
			}
		}

		@Override
		public void close() {
			
//			if (isSlave()) {
//				//
//			} else {
//				if (getBmObject().getId() > 0) {
//					UiWorkContractDetail uiWorkContractDetail = new UiWorkContractDetail(getUiParams(), getBmObject().getId());
//					uiWorkContractDetail.show();
//				} else {
//					list();
//				}
//			}
			
		}

		@Override
		public void formSuggestionSelectionChange(UiSuggestBox uiSuggestBox) {
			// Filtros de requisiones
			if (uiSuggestBox == workSuggestBox) {
				populateNameDescription(workSuggestBox.getSelectedBmObject());
			} 
			statusEffect();
		}
		
		public void populateBudgetItem() {
			budgetItemListBox.clear();
			budgetItemListBox.clearFilters();
			setPopulateBudgetItemFilters();
			budgetItemListBox.populate(bmoWorkContract.getBudgetItemId());
		}

		public void setPopulateBudgetItemFilters() {

			// Filtros por etapa

			if (bmoWorkContract.getBudgetItemId().toInteger() > 0 ) {

				BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();

				//Obtener la Obra
				BmoWork bmoWork = bmoWorkContract.getBmoWork(); 

				BmFilter bmFilterByBudget = new BmFilter();

				// Se rehacen filtros			
				bmFilterByBudget.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBudgetId(), bmoWork.getBmoDevelopmentPhase().getBudgetId().toInteger());
				budgetItemListBox.addBmFilter(bmFilterByBudget);
			} else {
				BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();

				//Obtener la Obra
				BmFilter bmFilterByBudget = new BmFilter();
				// Se rehacen filtros			
				bmFilterByBudget.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBudgetId(), "-1");
				budgetItemListBox.addBmFilter(bmFilterByBudget);
			}
		}

		@Override
		public void formListChange(ChangeEvent event) {		
			if (event.getSource() == statusUiListBox) {
				update("Desea cambiar el Status del Contrato?");
			}
			statusEffect();
		}

		protected class WorkContractUpdater {
			public void changeWorkContract() {
				stopLoading();
				reset();
			}		
		}
	}
}
