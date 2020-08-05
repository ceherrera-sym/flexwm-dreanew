/**
 * 
 */
package com.flexwm.client.fi;

import java.util.Date;

import com.flexwm.client.op.UiRequisition;
import com.flexwm.client.op.UiRequisitionLifeCycleViewModel;
import com.flexwm.client.op.UiRequisitionReceipt;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.fi.BmoBankMovConcept;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.fi.BmoBudgetItemType;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.fi.BmoPaccount;
import com.flexwm.shared.fi.BmoPaccountType;
import com.flexwm.shared.op.BmoRequisition;
import com.flexwm.shared.op.BmoRequisitionReceipt;
import com.flexwm.shared.op.BmoRequisitionType;
import com.flexwm.shared.op.BmoSupplier;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiFileUploadBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiFormFlexTable;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.client.ui.UiTemplate;
import com.symgae.client.ui.fields.UiTextBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFException;
import com.symgae.shared.sf.BmoArea;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoUser;


public class UiPaccount extends UiList {
	BmoPaccount bmoPaccount;
	Image xmlImage = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/file_code.png"));
	protected DialogBox paccountXMLDialogBox;

	UiListBox companyFilterListBox = new UiListBox(getUiParams(), new BmoCompany());

	public UiPaccount(UiParams uiParams) {
		super(uiParams, new BmoPaccount());
		bmoPaccount = (BmoPaccount) getBmObject();

		xmlImage.setTitle("Importar Factura XML");
		xmlImage.setStyleName("listSearchImage");
		xmlImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (!isLoading()) {
					addPaccountXML();
				}
			}
		});
	}

	public void addPaccountXML() {
		paccountXMLDialogBox = new DialogBox(true);
		paccountXMLDialogBox.setGlassEnabled(true);
		paccountXMLDialogBox.setText("Cargar XML Factura");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("450px", "200px");
		paccountXMLDialogBox.setWidget(vp);

		UiPaccountXMLForm requisitionItemForm = new UiPaccountXMLForm(getUiParams(), vp);
		requisitionItemForm.show();

		paccountXMLDialogBox.center();
		paccountXMLDialogBox.show();
	}

	@Override
	public void postShow() {
		if (isMaster()) {
			// Agrega boton de subir factura XML
			if (getSFParams().hasSpecialAccess("" + BmoPaccount.ACCESS_UPXML)) 
				localButtonPanel.add(xmlImage);

			addFilterListBox(companyFilterListBox, bmoPaccount.getBmoCompany());
			addFilterSuggestBox(new UiSuggestBox(new BmoSupplier()), new BmoSupplier(), bmoPaccount.getSupplierId());
			addFilterListBox(new UiListBox(getUiParams(), new BmoPaccountType()), bmoPaccount.getBmoPaccountType());

			if (!isMobile())
				addStaticFilterListBox(new UiListBox(getUiParams(), bmoPaccount.getStatus()), bmoPaccount, bmoPaccount.getStatus());

			addStaticFilterListBox(new UiListBox(getUiParams(), bmoPaccount.getPaymentStatus()), bmoPaccount, bmoPaccount.getPaymentStatus(), "" + BmoPaccount.PAYMENTSTATUS_PENDING);

			if (!isMobile()) {
				addDateRangeFilterListBox(bmoPaccount.getDueDate());

				//Tipo de Orden de Compra
				BmoRequisitionType bmoRequisitionType = new BmoRequisitionType();		
				addStaticFilterListBox(new UiListBox(getUiParams(), bmoRequisitionType.getType()), bmoRequisitionType, bmoPaccount.getRequisitionType());
			}
		}

		if (getSFParams().hasSpecialAccess(BmoPaccount.ACCESS_CHANGESTATUS)) {
			addActionBatchListBox(new UiListBox(getUiParams(), new BmoPaccount().getStatus()), bmoPaccount);
		}
	}

	@Override
	public void create() {
		UiPaccountForm uiPaccountForm = new UiPaccountForm(getUiParams(), 0);
		uiPaccountForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoPaccount = (BmoPaccount)bmObject;
		UiPaccountForm uiPaccountForm = new UiPaccountForm(getUiParams(), bmoPaccount.getId());
		uiPaccountForm.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		UiPaccountForm uiPaccountForm = new UiPaccountForm(getUiParams(), bmObject.getId());
		uiPaccountForm.show();
	}

	public void edit(int id) {
		UiPaccountForm uiPaccountForm = new UiPaccountForm(getUiParams(), id);
		uiPaccountForm.show();
	}


	public class UiPaccountForm extends UiFormDialog {
		BmoPaccount bmoPaccount = new BmoPaccount();

		UiSuggestBox paccountSuggestBox = new UiSuggestBox(new BmoPaccount());	

		TextBox codeTextBox = new TextBox();
		TextBox invoicenoTextBox = new TextBox();
		UiDateBox receiveDateBox = new UiDateBox();
		UiDateBox dueDateBox = new UiDateBox();
		TextArea descriptionTextArea = new TextArea();
		TextBox amountTextBox = new TextBox();
		TextBox totalTextBox = new TextBox();
		TextBox taxTextBox = new TextBox();
		TextBox balanceTextBox = new TextBox();
		TextBox paymentsTextBox = new TextBox();
		UiListBox statusListBox = new UiListBox(getUiParams());
		TextArea observationsTextArea = new TextArea();
		UiDateBox paymentDateBox = new UiDateBox();
		UiListBox requisitionReceiptListBox = new UiListBox(getUiParams(), new BmoRequisitionReceipt());
		UiListBox paccountTypeListBox = new UiListBox(getUiParams(), new BmoPaccountType());
		UiSuggestBox supplierSuggestBox = new UiSuggestBox(new BmoSupplier());
		UiListBox companyListBox = new UiListBox(getUiParams(), new BmoCompany());
		UiListBox budgetItemUiListBox = new UiListBox(getUiParams(), new BmoBudgetItem());
		UiListBox currencyListBox = new UiListBox(getUiParams(), new BmoCurrency());
		TextBox currencyParityTextBox = new TextBox();
		TextBox reqiCodeTextBox = new TextBox();
		UiListBox areaUiListBox = new UiListBox(getUiParams(), new BmoArea());
		UiTextBox discountTextBox = new UiTextBox();
		UiTextBox sumRetentionTextBox = new UiTextBox();
		CheckBox isXmlCheckBox = new CheckBox();
		//Facturas y XML
		UiFileUploadBox invoiceFileUpload = new UiFileUploadBox(getUiParams());
		private int updateAmountRpcAttempt = 0;
		private int paccId;
		private int userAutorizedRpcAttempt;
		private int userId;
		private int saveAmountChangeRpcAttempt = 0;

		String generalSection = "Datos Generales";
		String detailsSection = "Detalles y Fechas";
		String amountsSection = "Montos";
		String totalsSection = "Totales";
		String itemsSection = "Items";
		String paymentsSection = "Pagos Mov. Banco";
		String assignmentsSection = "Aplicaciones Notas Crédito";
		String statusSection = "Status";

		private NumberFormat numberFormat = NumberFormat.getDecimalFormat();
		double taxRate = ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getTax().toDouble() / 100;
		private CheckBox taxAppliesCheckBox = new CheckBox("");
		protected FlowPanel formatPanel;

		// Aplicaciones de pagos
		private UiPaccountItemGrid uiPaccountItemGrid;
		private FlowPanel uiPaccountItemPanel = new FlowPanel();

		// Pagos (Conceptos de Banco)
		private UiPaccountPaymentGrid uiPaccountPaymentGrid;
		private FlowPanel uiPaccountPaymentsPanel = new FlowPanel();

		// Aplicaciones de pagos
		private UiPaccountAssignmentGrid uiPaccountAssignmentGrid;
		private FlowPanel uiPaccountAssignmentPanel = new FlowPanel();

		PaccountUpdater paccountUpdater = new PaccountUpdater();

		// Ciclo de vida documento
		public final SingleSelectionModel<BmObject> lifeCycleSelection = new SingleSelectionModel<BmObject>();
		private UiRequisitionLifeCycleViewModel uiLifeCycleViewModel;
		private CellTree lifeCycleCellTree;
		private DialogBox lifeCycleDialogBox = new DialogBox();
		private Button lifeCycleShowButton = new Button("SEGUIMIENTO");
		private Button lifeCycleCloseButton = new Button("CERRAR");

		public UiPaccountForm(UiParams uiParams, int id) {
			super(uiParams, new BmoPaccount(), id);

			// Panel de staff
			uiPaccountItemPanel.setWidth("100%");
			uiPaccountAssignmentPanel.setWidth("100%");

			if (id > 0) {
				// Ciclo de vida de ordenes de compra
				lifeCycleSelection.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					@Override
					public void onSelectionChange(SelectionChangeEvent event) {
						BmObject bmObject = lifeCycleSelection.getSelectedObject();
						// Se esta tratando de abrir un registro
						if (bmObject instanceof BmoRequisition) {
							if (Window.confirm("Desea Abrir la Órden de Compra?"))
								new UiRequisition(getUiParams()).edit(bmObject);;
						} else if (bmObject instanceof BmoRequisitionReceipt) {
							if (Window.confirm("Desea Abrir el Recibo de Órden de Compra?"))
								new UiRequisitionReceipt(getUiParams()).edit(bmObject);
						} else if (bmObject instanceof BmoPaccount) {
							if (Window.confirm("Desea Abrir la Cuenta por Pagar?"))
								new UiPaccount(getUiParams()).edit(bmObject);
						} else if (bmObject instanceof BmoBankMovConcept) {
							if (Window.confirm("Desea Abrir el Movimiento Bancario?"))
								new UiBankMovement(getUiParams()).edit(((BmoBankMovConcept)bmObject).getBmoBankMovement()); 
						} 
					}
				});

				lifeCycleShowButton.setStyleName("formCloseButton");
				lifeCycleShowButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						showLifeCycleDialog();
					}
				});
				lifeCycleCloseButton.setStyleName("formCloseButton");
				lifeCycleCloseButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						lifeCycleDialogBox.hide();
					}
				});
			}	

			//Aplicar IVA
			taxAppliesCheckBox.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					taxAppliesChange();
				}
			});
		}

		@Override
		public void populateFields() {
			bmoPaccount = (BmoPaccount) getBmObject();

			if (newRecord) {

				try {
					bmoPaccount.getReceiveDate().setValue(GwtUtil.dateToString(new Date(), getSFParams().getDateFormat()));
					bmoPaccount.getDueDate().setValue(GwtUtil.dateToString(new Date(), getSFParams().getDateFormat()));

					// Busca Empresa seleccionada por default
//					if (Integer.parseInt(companyFilterListBox.getSelectedId()) > 0)
//						bmoPaccount.getCompanyId().setValue(companyFilterListBox.getSelectedId());
//					else 
						if (getUiParams().getSFParams().getSelectedCompanyId() > 0)
						bmoPaccount.getCompanyId().setValue(getUiParams().getSFParams().getSelectedCompanyId());

				} catch (BmException e) {
					showErrorMessage(this.getClass().getName() + "-populateFields(): ERROR " + e.toString());
				}

				// Si es registro nuevo, Mostrar Tipos de CxP visibles
				BmoPaccountType bmoPaccountType = new BmoPaccountType();
				BmFilter bmFilterPaccountTypeVisible = new BmFilter();
				bmFilterPaccountTypeVisible.setValueFilter(bmoPaccountType.getKind(), bmoPaccountType.getVisible(), "" + BmoPaccountType.VISIBLE_TRUE);
				paccountTypeListBox.addFilter(bmFilterPaccountTypeVisible);
			} else {
				// Modifica titulo
				formDialogBox.setText("Editar " + getSFParams().getProgramTitle(getBmObject()) + ": " + bmoPaccount.getCode());
			}

			// Si no esta asignada la moneda, buscar por la default
			if (!(bmoPaccount.getCurrencyId().toInteger() > 0)) {
				try {
					bmoPaccount.getCurrencyId()
					.setValue(((BmoFlexConfig) getSFParams().getBmoAppConfig()).getCurrencyId().toInteger());
				} catch (BmException e) {
					showSystemMessage("No se puede asignar moneda : " + e.toString());
				}
			}

			// Campo para cambiar de CxP
			if (!newRecord)
				formFlexTable.addField(1, 0, paccountSuggestBox, bmoPaccount.getIdField());	

			formFlexTable.addSectionLabel(2, 0, generalSection, 2);
			formFlexTable.addFieldReadOnly(3, 0, codeTextBox, bmoPaccount.getCode());
			formFlexTable.addField(4, 0, paccountTypeListBox, bmoPaccount.getPaccountTypeId());
			formFlexTable.addField(5, 0, supplierSuggestBox, bmoPaccount.getSupplierId());
			// Asignar filtros a ROC
			setRequisitionReceiptsListBoxFilters(bmoPaccount.getSupplierId().toInteger());
			formFlexTable.addField(6, 0, requisitionReceiptListBox, bmoPaccount.getRequisitionReceiptId());
			//setCompanyListBoxFilters(-1, ' ');
			formFlexTable.addField(7, 0, companyListBox, bmoPaccount.getCompanyId());
			// Asignar filtros a Partidas Pres.
			setBudgetItemsListBoxFilters(Integer.parseInt(companyListBox.getSelectedId()), bmoPaccount.getBudgetItemId().toInteger());
			formFlexTable.addField(8, 0, budgetItemUiListBox, bmoPaccount.getBudgetItemId());
			formFlexTable.addField(9, 0, areaUiListBox, bmoPaccount.getAreaId());
			formFlexTable.addFieldReadOnly(10, 0, reqiCodeTextBox, bmoPaccount.getReqiCode());
			formFlexTable.addField(11, 0, isXmlCheckBox,bmoPaccount.getIsXml());

			formFlexTable.addSectionLabel(12, 0, detailsSection, 2);
			formFlexTable.addField(13, 0, invoicenoTextBox, bmoPaccount.getInvoiceno());
			formFlexTable.addField(14, 0, descriptionTextArea, bmoPaccount.getDescription());
			formFlexTable.addField(15, 0, receiveDateBox, bmoPaccount.getReceiveDate());
			formFlexTable.addField(16, 0, dueDateBox, bmoPaccount.getDueDate());

			if (newRecord) {
				formFlexTable.addSectionLabel(17, 0, amountsSection, 2);
				formFlexTable.addField(18, 0, currencyListBox, bmoPaccount.getCurrencyId());
				if (bmoPaccount.getStatus().toChar() == BmoPaccount.STATUS_REVISION)
					formFlexTable.addField(19, 0, amountTextBox, bmoPaccount.getAmount());
				else
					formFlexTable.addLabelField(20, 0, bmoPaccount.getAmount());
				formFlexTable.addLabelField(21, 0, bmoPaccount.getPayments());
				formFlexTable.addLabelField(22, 0, bmoPaccount.getBalance());
				
				formFlexTable.addSectionLabel(23, 0, statusSection, 2);
				formFlexTable.addLabelField(24, 0, bmoPaccount.getPaymentStatus());
				formFlexTable.addField(25, 0, statusListBox, bmoPaccount.getStatus());
			} else {
				showAutorizedData();
				formFlexTable.addSectionLabel(19, 0, itemsSection, 2);
				formFlexTable.addPanel(20, 0, uiPaccountItemPanel);
				uiPaccountItemGrid = new UiPaccountItemGrid(getUiParams(), uiPaccountItemPanel, bmoPaccount, paccountUpdater);
				uiPaccountItemGrid.show();

				formFlexTable.addSectionLabel(21, 0, paymentsSection, 2);
				formFlexTable.addPanel(22, 0, uiPaccountPaymentsPanel);
				uiPaccountPaymentGrid = new UiPaccountPaymentGrid(getUiParams(), uiPaccountPaymentsPanel, bmoPaccount, paccountUpdater);
				uiPaccountPaymentGrid.show();

				formFlexTable.addSectionLabel(23, 0, assignmentsSection, 2);
				formFlexTable.addPanel(24, 0, uiPaccountAssignmentPanel);
				uiPaccountAssignmentGrid = new UiPaccountAssignmentGrid(getUiParams(), uiPaccountAssignmentPanel, bmoPaccount, paccountUpdater);
				uiPaccountAssignmentGrid.show();

				formFlexTable.addSectionLabel(25, 0, totalsSection, 2);
				formFlexTable.addField(26, 0, currencyListBox, bmoPaccount.getCurrencyId());
				formFlexTable.addField(27, 0, currencyParityTextBox, bmoPaccount.getCurrencyParity());
				formFlexTable.addFieldReadOnly(28, 0, amountTextBox, bmoPaccount.getAmount());
				if (bmoPaccount.getIsXml().toBoolean()) {
					formFlexTable.addField(29, 0, discountTextBox,bmoPaccount.getDiscount());
					formFlexTable.addField(30, 0, sumRetentionTextBox,bmoPaccount.getSumRetention());
				}
				formFlexTable.addField(31, 0, taxAppliesCheckBox, bmoPaccount.getTaxApplies());
				formFlexTable.addFieldReadOnly(32, 0, taxTextBox, bmoPaccount.getTax());
				formFlexTable.addFieldReadOnly(33, 0, totalTextBox, bmoPaccount.getTotal());
				formFlexTable.addFieldReadOnly(34, 0, paymentsTextBox, bmoPaccount.getPayments());
				formFlexTable.addFieldReadOnly(35, 0, balanceTextBox, bmoPaccount.getBalance());
				
				formFlexTable.addSectionLabel(36, 0, statusSection, 2);
				formFlexTable.addLabelField(37, 0, bmoPaccount.getPaymentStatus());
				formFlexTable.addField(38, 0, statusListBox, bmoPaccount.getStatus());
			}

			if (!(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {
				formFlexTable.hideField(bmoPaccount.getBudgetItemId());
				formFlexTable.hideField(bmoPaccount.getAreaId());
			}

			if (!newRecord) {
				formFlexTable.hideSection(detailsSection);
				formFlexTable.hideSection(paymentsSection);
				formFlexTable.hideSection(assignmentsSection);
			}

			statusEffect();
		}

		@Override
		public void postShow() {
			if (!newRecord)
				buttonPanel.add(lifeCycleShowButton);
		}

		public void reset() {
			updateAmount(id);
			uiPaccountItemGrid.show();
			uiPaccountAssignmentGrid.show();
		}

		public void showAutorizedData() {
			if (bmoPaccount.getStatus().equals(BmoPaccount.STATUS_AUTHORIZED)) {
				if (bmoPaccount.getAuthorizedUser().toInteger() > 0)
					getUserAutorized(bmoPaccount.getAuthorizedUser().toInteger());
			}

		}

		public void taxAppliesChange() {
			try {
				bmoPaccount.getTaxApplies().setValue(taxAppliesCheckBox.getValue());
				saveAmountChange();
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-taxAppliesChange() ERROR: " + e.toString());
			}
		}

		@Override
		public void formListChange(ChangeEvent event) {
			if (event.getSource() == paccountTypeListBox) {
				BmoPaccountType bmoPaccountType = (BmoPaccountType) paccountTypeListBox.getSelectedBmObject();
				if (bmoPaccountType == null)
					bmoPaccountType = bmoPaccount.getBmoPaccountType();

				if (newRecord) {
					populateGeneralInfo();
				}

				if (bmoPaccountType.getCategory().equals(BmoPaccountType.CATEGORY_OTHER)) {
					if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
						if (bmoPaccountType.getType().equals(BmoPaccountType.TYPE_WITHDRAW)) {
							//populateCompany(Integer.parseInt(companyListBox.getSelectedId()), BmoPaccountType.CATEGORY_OTHER);
							populateBudgetItems(Integer.parseInt(companyListBox.getSelectedId()), -1);
							companyListBox.setEnabled(true);
							budgetItemUiListBox.setEnabled(true);
						}
					} else	{
						populateCompany(-1, ' ');
						populateRequisitionReceipts(-1);
					}
				} else if (bmoPaccountType.getCategory().equals(BmoPaccountType.CATEGORY_REQUISITION)) {
					if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
						//populateCompany(Integer.parseInt(companyListBox.getSelectedId()), BmoPaccountType.CATEGORY_REQUISITION);
						populateBudgetItems(Integer.parseInt(companyListBox.getSelectedId()), -1);
						companyListBox.setEnabled(true);
						budgetItemUiListBox.setEnabled(true);
					} else	{
						populateCompany(-1, ' ');
						populateRequisitionReceipts(supplierSuggestBox.getSelectedId());
					}
				} else if (bmoPaccountType.getCategory().equals(BmoPaccountType.CATEGORY_CREDITNOTE)) {
					if (bmoPaccountType.getType().equals(BmoPaccountType.TYPE_DEPOSIT)) {
						if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
							//populateCompany(Integer.parseInt(companyListBox.getSelectedId()), BmoPaccountType.CATEGORY_CREDITNOTE);
							populateBudgetItems(Integer.parseInt(companyListBox.getSelectedId()), -1);
							companyListBox.setEnabled(true);
							budgetItemUiListBox.setEnabled(true);
						} else	{
							populateCompany(-1, ' ');
							populateRequisitionReceipts(-1);
						} 
					}
				}

				statusEffect();
			} else if (event.getSource() == requisitionReceiptListBox) {
				// Obtener y Asignar la fecha de pago/clave/empresa de la OC
				BmoRequisitionReceipt bmoRequisitionReceipt = (BmoRequisitionReceipt)requisitionReceiptListBox.getSelectedBmObject();
				dueDateBox.getTextBox().setText(bmoRequisitionReceipt.getBmoRequisition().getPaymentDate().toString());
				reqiCodeTextBox.setText(bmoRequisitionReceipt.getBmoRequisition().getCode().toString());
				populateCompany(bmoRequisitionReceipt.getBmoRequisition().getCompanyId().toInteger(), BmoPaccountType.CATEGORY_REQUISITION);
				populateBudgetItems(bmoRequisitionReceipt.getBmoRequisition().getCompanyId().toInteger(), bmoRequisitionReceipt.getBudgetItemId().toInteger());
				budgetItemUiListBox.setSelectedId(bmoRequisitionReceipt.getBudgetItemId().toString());
				areaUiListBox.setSelectedId(bmoRequisitionReceipt.getAreaId().toString());
				currencyListBox.setSelectedId(""+bmoRequisitionReceipt.getCurrencyId().toInteger());
				companyListBox.setEnabled(false);
				currencyListBox.setEnabled(false);
			} else if (event.getSource() == currencyListBox) {
				// Cambiar la paridad
				if (!newRecord) {
					BmoCurrency bmoCurrency = (BmoCurrency) currencyListBox.getSelectedBmObject();

					try {
						bmoPaccount.getCurrencyParity().setValue(bmoCurrency.getParity().toDouble());
						currencyParityTextBox.setText("" + bmoCurrency.getParity().toDouble());
					} catch (BmException e) {
						showErrorMessage("Error al asignar el Tipo de Cambio");
					}
				}

				statusEffect();
			} else if (event.getSource() == statusListBox) {
				update("Desea cambiar el Status de la Cuenta por Pagar?");
			} else if (event.getSource() == companyListBox) {
				populateBudgetItems(Integer.parseInt(companyListBox.getSelectedId()), -1);
			}
		}

		// Cambios en los SuggestBox
		@Override
		public void formSuggestionSelectionChange(UiSuggestBox uiSuggestBox) {
			// Filtros de requisiones
			if (uiSuggestBox == supplierSuggestBox) {

				BmoPaccountType bmoPaccountType = (BmoPaccountType) paccountTypeListBox.getSelectedBmObject();
				if (bmoPaccountType == null) {
					bmoPaccountType = bmoPaccount.getBmoPaccountType();
					// Limpiar filtro de empresas
					populateCompany(-1, ' ');
				}

				if (bmoPaccountType.getCategory().equals(BmoPaccountType.CATEGORY_REQUISITION)) {
					if (supplierSuggestBox.getSelectedId() > 0)
						populateRequisitionReceipts(supplierSuggestBox.getSelectedId());
					else {
						populateRequisitionReceipts(supplierSuggestBox.getSelectedId());
						populateCompany(-1, ' ');
					}
				}
			} else if (uiSuggestBox == paccountSuggestBox) {
				if (paccountSuggestBox.getSelectedBmObject() != null)
					changePaccount((BmoPaccount)paccountSuggestBox.getSelectedBmObject());
			}

			statusEffect();
		}

		// Cambia la CxP
		private void changePaccount(BmoPaccount newBmoPaccount) {
			if (newBmoPaccount.getId() != bmoPaccount.getId()) {
				formDialogBox.hide();
				edit(newBmoPaccount);
			}
		}

		private void statusEffect() {
			// Deshabilita campos
			companyListBox.setEnabled(false);
			paccountTypeListBox.setEnabled(false);
			supplierSuggestBox.setEnabled(false);
			dueDateBox.setEnabled(false);
			receiveDateBox.setEnabled(false);
			descriptionTextArea.setEnabled(false);
			isXmlCheckBox.setEnabled(false);			
			requisitionReceiptListBox.setEnabled(false);
			discountTextBox.setEnabled(false);
			amountTextBox.setEnabled(false);
			balanceTextBox.setEnabled(false);
			paymentsTextBox.setEnabled(false);
			statusListBox.setEnabled(false);
			budgetItemUiListBox.setEnabled(false);
			currencyListBox.setEnabled(false);
			currencyParityTextBox.setEnabled(false);
			reqiCodeTextBox.setVisible(false);
			totalTextBox.setEnabled(false);
			taxAppliesCheckBox.setEnabled(false);
			taxTextBox.setEnabled(false);
			areaUiListBox.setEnabled(false);
			sumRetentionTextBox.setEnabled(false);

			// De acuerdo al estatus habilitar campos
			if (bmoPaccount.getStatus().equals(BmoPaccount.STATUS_REVISION)) {
				descriptionTextArea.setEnabled(true);

				if (newRecord) {
					companyListBox.setEnabled(true);
					paccountTypeListBox.setEnabled(true);
					supplierSuggestBox.setEnabled(true);
					dueDateBox.setEnabled(true);
					receiveDateBox.setEnabled(true);
					currencyListBox.setEnabled(true);
				} else {				
					paccountTypeListBox.setVisible(true);
					invoicenoTextBox.setEnabled(true);
					dueDateBox.setEnabled(true);
					receiveDateBox.setEnabled(true);

					//Si la CxP viene de un OC no modificar la paridad y moneda
					if (!(bmoPaccount.getRequisitionReceiptId().toInteger() > 0)) {
						currencyListBox.setEnabled(true);
						currencyParityTextBox.setEnabled(true);

						if (currencyListBox.getSelectedId() != ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getSystemCurrencyId().toString()) {
							currencyParityTextBox.setEnabled(true);				
						} else {
							currencyParityTextBox.setEnabled(false);
						}
					}	
				}

				// Si esta el tipo de cuenta
				BmoPaccountType bmoPaccountType = (BmoPaccountType) paccountTypeListBox.getSelectedBmObject();
				if (bmoPaccountType == null)
					bmoPaccountType = bmoPaccount.getBmoPaccountType();

				if (bmoPaccountType != null) {
					if (supplierSuggestBox.getSelectedId() > 0) {
						if (bmoPaccountType.getCategory().equals(BmoPaccountType.CATEGORY_REQUISITION)) {
							if (newRecord)
								requisitionReceiptListBox.setEnabled(true);
							else
								requisitionReceiptListBox.setEnabled(false);
							requisitionReceiptListBox.setVisible(true);
							taxAppliesCheckBox.setEnabled(false);
						}
					}

					if (bmoPaccountType.getCategory().equals(BmoPaccountType.CATEGORY_OTHER)) {
						if (bmoPaccountType.getType().equals(BmoPaccountType.TYPE_WITHDRAW)) {
							taxAppliesCheckBox.setEnabled(true);
							if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
								budgetItemUiListBox.setEnabled(true);
								areaUiListBox.setEnabled(true);
							}
						}
					} else {
						if (bmoPaccountType.getCategory().equals(BmoPaccountType.CATEGORY_CREDITNOTE)) {						
							if (bmoPaccountType.getType().equals(BmoPaccountType.TYPE_DEPOSIT)) {
								taxAppliesCheckBox.setEnabled(true);
								if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
									budgetItemUiListBox.setEnabled(true);
									areaUiListBox.setEnabled(true);
								}
							}
						}
					}
				}

				if (!newRecord && getSFParams().hasSpecialAccess(BmoPaccount.ACCESS_CHANGEROC)) 
					requisitionReceiptListBox.setEnabled(true);
			}

			if (bmoPaccount.getTaxApplies().toBoolean()) taxAppliesCheckBox.setValue(true);
			else taxAppliesCheckBox.setValue(false);

			invoicenoTextBox.setEnabled(true);
			dueDateBox.setEnabled(true);
			receiveDateBox.setEnabled(true);

			if (!newRecord) {
				BmoPaccountType bmoPaccountType = (BmoPaccountType) paccountTypeListBox.getSelectedBmObject();
				if (bmoPaccountType == null)
					bmoPaccountType = bmoPaccount.getBmoPaccountType();

				if (bmoPaccountType != null) {
					if (bmoPaccountType.getType().equals(BmoPaccountType.TYPE_DEPOSIT)
							&& bmoPaccountType.getCategory().equals(BmoPaccountType.CATEGORY_OTHER)) {
						invoicenoTextBox.setEnabled(false);
					}
				}
			}
			
			// Si esta cancelado, bloquear campos
//			if (bmoPaccount.getStatus().equals(BmoPaccount.STATUS_CANCELLED)) {
//				invoicenoTextBox.setEnabled(false);
//				dueDateBox.setEnabled(false);
//				receiveDateBox.setEnabled(false);
//			} else {
				if (bmoPaccount.getTaxApplies().toBoolean()) taxAppliesCheckBox.setValue(true);
				else taxAppliesCheckBox.setValue(false);
	
				invoicenoTextBox.setEnabled(true);
				dueDateBox.setEnabled(true);
				receiveDateBox.setEnabled(true);
	
				if (!newRecord) {
					BmoPaccountType bmoPaccountType = (BmoPaccountType) paccountTypeListBox.getSelectedBmObject();
					if (bmoPaccountType == null)
						bmoPaccountType = bmoPaccount.getBmoPaccountType();
	
					if (bmoPaccountType != null) {
						if (bmoPaccountType.getType().equals(BmoPaccountType.TYPE_DEPOSIT)
								&& bmoPaccountType.getCategory().equals(BmoPaccountType.CATEGORY_OTHER)) {
							invoicenoTextBox.setEnabled(false);
						}
					}
				}
//			}

			// Si no hay pagos, se puede cambiar estatus
			if (!newRecord && getSFParams().hasSpecialAccess(BmoPaccount.ACCESS_CHANGESTATUS)
					&& (bmoPaccount.getBalance().toDouble() == bmoPaccount.getTotal().toDouble())) {
				statusListBox.setEnabled(true);
			} else if (!newRecord
					&& bmoPaccount.getBmoPaccountType().getCategory().equals(BmoPaccountType.CATEGORY_CREDITNOTE)) {
				if (getSFParams().hasSpecialAccess(BmoPaccount.ACCESS_CHANGESTATUS)) {
					statusListBox.setEnabled(true);
				}
			}

			setAmount(bmoPaccount);

			// Si hay seleccion default de empresa, deshabilitar combo Y forzar a ponerlo
			if (getUiParams().getSFParams().getSelectedCompanyId() > 0) {
				companyListBox.setEnabled(false);
				companyListBox.setSelectedId(""+getUiParams().getSFParams().getSelectedCompanyId());
			}
			
			if(bmoPaccount.getIsXml().toBoolean()) {
				taxAppliesCheckBox.setEnabled(false);
			}
				
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoPaccount.setId(id);
			bmoPaccount.getCode().setValue(codeTextBox.getText());
			bmoPaccount.getInvoiceno().setValue(invoicenoTextBox.getText());
			bmoPaccount.getReceiveDate().setValue(receiveDateBox.getTextBox().getText());
			bmoPaccount.getDueDate().setValue(dueDateBox.getTextBox().getText());
			bmoPaccount.getPaymentDate().setValue(dueDateBox.getTextBox().getText());
			bmoPaccount.getDescription().setValue(descriptionTextArea.getText());
			bmoPaccount.getAmount().setValue(amountTextBox.getText());
			bmoPaccount.getPaccountTypeId().setValue(paccountTypeListBox.getSelectedId());
			bmoPaccount.getBudgetItemId().setValue(budgetItemUiListBox.getSelectedId());
			bmoPaccount.getSupplierId().setValue(supplierSuggestBox.getSelectedId());
			bmoPaccount.getRequisitionReceiptId().setValue(requisitionReceiptListBox.getSelectedId());
			bmoPaccount.getCompanyId().setValue(companyListBox.getSelectedId());
			bmoPaccount.getCurrencyId().setValue(currencyListBox.getSelectedId());
			bmoPaccount.getCurrencyParity().setValue(currencyParityTextBox.getText());
			bmoPaccount.getDiscount().setValue(discountTextBox.getText());
			if (!(bmoPaccount.getCurrencyParity().toDouble() > 0)) {
				BmoCurrency bmoCurrency = (BmoCurrency) currencyListBox.getSelectedBmObject();
				try {
					bmoPaccount.getCurrencyParity().setValue(bmoCurrency.getParity().toDouble());
				} catch (BmException e) {
					showErrorMessage(this.getClass().getName() + "-populateCurrencyParity(): ERROR " + e.toString());
				}
			}
			bmoPaccount.getStatus().setValue(statusListBox.getSelectedCode());
			bmoPaccount.getTotal().setValue(totalTextBox.getText());
			bmoPaccount.getTax().setValue(taxTextBox.getText());
			bmoPaccount.getTaxApplies().setValue(taxAppliesCheckBox.getValue());
			bmoPaccount.getAreaId().setValue(areaUiListBox.getSelectedId());
			return bmoPaccount;
		}

		@Override
		public void saveNext() {
			if (newRecord) {
				UiPaccountForm uiPaccountForm = new UiPaccountForm(getUiParams(), getBmObject().getId());
				uiPaccountForm.show();
			} else {

				UiPaccount uiPaccountList = new UiPaccount(getUiParams());
				uiPaccountList.show();
			}
		}

		@Override
		public void close() {
			if (deleted) list();
			//list();
		}

		public void showLifeCycleDialog() {
			// Es de tipo forma de dialogo
			lifeCycleDialogBox = new DialogBox(true);
			lifeCycleDialogBox.setGlassEnabled(true);
			lifeCycleDialogBox.setText("Seguimiento del Documento");

			uiLifeCycleViewModel = new UiRequisitionLifeCycleViewModel(lifeCycleSelection, bmoPaccount.getRequisitionId().toInteger());
			lifeCycleCellTree = new CellTree(uiLifeCycleViewModel, lifeCycleSelection);
			lifeCycleCellTree.setSize("100%", "100%");
			lifeCycleCellTree.setAnimationEnabled(true);

			// Vertical Panel
			VerticalPanel vp = new VerticalPanel();
			vp.setSize("100%", "100%");
			vp.add(lifeCycleCellTree);
			vp.add(new HTML("&nbsp;"));
			vp.add(lifeCycleCloseButton);

			// Scroll Panel
			ScrollPanel scrollPanel = new ScrollPanel();
			if (getUiParams().isMobile())
				scrollPanel.setSize(Window.getClientWidth() + "px", Window.getClientHeight() + "px");
			else
				scrollPanel.setSize(Window.getClientWidth() * .4 + "px", Window.getClientHeight() * .3 + "px");
			scrollPanel.setWidget(vp);
			lifeCycleDialogBox.setWidget(scrollPanel);

			Double d = Window.getClientWidth() * .3;
			if (!getUiParams().isMobile()) 
				lifeCycleDialogBox.setPopupPosition(d.intValue(), UiTemplate.NORTHSIZE * 3);

			lifeCycleDialogBox.show();
		}
		
		private void updateAmount(int id) {
			updateAmount(id, 0);
		}

		private void updateAmount(int id, int updateAmountRpcAttempt) {
			if (updateAmountRpcAttempt < 5) {
				setPaccId(id);
				setUpdateAmountRpcAttempt(updateAmountRpcAttempt + 1);
				
				AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
					@Override
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getUpdateAmountRpcAttempt() < 5) {
							updateAmount(getPaccId(), getUpdateAmountRpcAttempt());
						} else {
							showErrorMessage(this.getClass().getName() + "-updateAmount() ERROR: " + caught.toString());
						}
					}
	
					@Override
					public void onSuccess(BmObject result) {
						stopLoading();
						setUpdateAmountRpcAttempt(0);
						setAmount((BmoPaccount) result);
					}
				};
				try {
					if (!isLoading()) {
						startLoading();
						getUiParams().getBmObjectServiceAsync().get(bmoPaccount.getPmClass(), id, callback);
					}
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-updateAmount(): ERROR " + e.toString());
				}
			}
		}

		private void setAmount(BmoPaccount bmoPaccount) {
			numberFormat = NumberFormat.getCurrencyFormat();
			taxTextBox.setText(numberFormat.format(bmoPaccount.getTax().toDouble()));
			paymentsTextBox.setText(numberFormat.format(bmoPaccount.getPayments().toDouble()));
			balanceTextBox.setText(numberFormat.format(bmoPaccount.getBalance().toDouble()));
			amountTextBox.setText("" + numberFormat.format(bmoPaccount.getAmount().toDouble()));
			totalTextBox.setText("" + numberFormat.format(bmoPaccount.getTotal().toDouble()));
			discountTextBox.setText("" + numberFormat.format(bmoPaccount.getDiscount().toDouble()));
		}

		// Rellenar descripcion con el tipo de de CXP
		private void populateGeneralInfo() {
			// Filtros de ordenes de compra
			if (newRecord) {
				BmoPaccountType bmoPaccountType = (BmoPaccountType) paccountTypeListBox.getSelectedBmObject();
				if (bmoPaccountType == null)
					bmoPaccountType = bmoPaccount.getBmoPaccountType();

				descriptionTextArea.setText(bmoPaccountType.getName().toString());
			}
		}

		public void getUserAutorized(int id) {
			getUserAutorized(id, 0);
		}
		
		public void getUserAutorized(int id, int userAutorizedRpcAttempt) {
			if (userAutorizedRpcAttempt < 5) {
				setUserId(id);
				setUpdateAmountRpcAttempt(userAutorizedRpcAttempt + 1);
				
				BmoUser bmoUser = new BmoUser();
				AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
					@Override
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getUserAutorizedRpcAttempt()  < 5) {
							getUserAutorized(getUserId(), getUserAutorizedRpcAttempt());
						} else {
							showErrorMessage(this.getClass().getName() + "-getUserAutorized() ERROR: " + caught.toString());
						}
					}
	
					@Override
					public void onSuccess(BmObject result) {
						stopLoading();
						setUserAutorizedRpcAttempt(0);
						setBmObject((BmObject)result);
						BmoUser bmoUser = (BmoUser)getBmObject();
						formFlexTable.addLabelField(17, 0, "Autorizo:", bmoUser.getCode().toString());
						formFlexTable.addLabelField(18, 0, bmoPaccount.getAuthorizedDate());
					}
				};
				try {
					if (!isLoading()) {
						startLoading();
						getUiParams().getBmObjectServiceAsync().get(bmoUser.getPmClass(), id, callback);
					}
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getUserAutorized(): ERROR " + e.toString());
				}
			}
		}

		// Actualiza combo de ROC
		private void populateRequisitionReceipts(int supplierId) {
			requisitionReceiptListBox.clear();
			requisitionReceiptListBox.clearFilters();
			setRequisitionReceiptsListBoxFilters(supplierId);
			requisitionReceiptListBox.populate(bmoPaccount.getRequisitionReceiptId());
		}

		// Asigna filtros al listado de ROC
		private void setRequisitionReceiptsListBoxFilters(int supplierId) {
			BmoRequisitionReceipt bmoRequisitionReceipt = new BmoRequisitionReceipt();

			if (supplierId > 0) {
				if (newRecord) {
					BmFilter bmFilterReqByStatus = new BmFilter();
					BmFilter bmFilterReqByProvisioned = new BmFilter();
					BmFilter bmFilterReqBySupplier = new BmFilter();

					// Se rehacen filtros
					if (bmoPaccount.getStatus().equals(BmoPaccount.STATUS_REVISION)
							&& !(bmoPaccount.getRequisitionReceiptId().toInteger() > 0)) {
						bmFilterReqByProvisioned.setValueFilter(bmoRequisitionReceipt.getKind(), bmoRequisitionReceipt.getPayment(),
								"" + BmoRequisitionReceipt.PAYMENT_NOTPROVISIONED);
					} else {
						bmFilterReqByProvisioned.setValueFilter(bmoRequisitionReceipt.getKind(), bmoRequisitionReceipt.getIdField(),
								bmoPaccount.getRequisitionReceiptId().toInteger());
					}

					bmFilterReqBySupplier.setValueFilter(bmoRequisitionReceipt.getKind(), bmoRequisitionReceipt.getSupplierId(), supplierId);
					
					bmFilterReqByStatus.setValueFilter(bmoRequisitionReceipt.getKind(), bmoRequisitionReceipt.getStatus(), "" + BmoRequisitionReceipt.STATUS_AUTHORIZED);

					requisitionReceiptListBox.addBmFilter(bmFilterReqBySupplier);
					requisitionReceiptListBox.addBmFilter(bmFilterReqByProvisioned);
					requisitionReceiptListBox.addBmFilter(bmFilterReqByStatus);

				} else {
					// Si es de Categoria Otros no cargar ROC
					if (bmoPaccount.getBmoPaccountType().getCategory().toString().equals("" + BmoPaccountType.CATEGORY_OTHER)) {

						BmFilter bmFilterRoc = new BmFilter();
						bmFilterRoc.setValueFilter(bmoRequisitionReceipt.getKind(), bmoRequisitionReceipt.getIdField(), bmoPaccount.getRequisitionReceiptId().toInteger());
						requisitionReceiptListBox.addBmFilter(bmFilterRoc);

					} else {
						BmFilter bmFilterReqByStatus = new BmFilter();
						BmFilter bmFilterReqByProvisioned = new BmFilter();
						BmFilter bmFilterReqBySupplier = new BmFilter();

						// Se rehacen filtros
						if (bmoPaccount.getStatus().equals(BmoPaccount.STATUS_REVISION)
								&& !(bmoPaccount.getRequisitionReceiptId().toInteger() > 0)) {
							bmFilterReqByProvisioned.setValueFilter(bmoRequisitionReceipt.getKind(), bmoRequisitionReceipt.getPayment(),
									"" + BmoRequisitionReceipt.PAYMENT_NOTPROVISIONED);
						} else {
							bmFilterReqByProvisioned.setValueFilter(bmoRequisitionReceipt.getKind(), bmoRequisitionReceipt.getIdField(),
									bmoPaccount.getRequisitionReceiptId().toInteger());
						}

						bmFilterReqBySupplier.setValueFilter(bmoRequisitionReceipt.getKind(), bmoRequisitionReceipt.getSupplierId(), supplierId);
						
						bmFilterReqByStatus.setValueFilter(bmoRequisitionReceipt.getKind(), bmoRequisitionReceipt.getStatus(), "" + BmoRequisitionReceipt.STATUS_AUTHORIZED);


						requisitionReceiptListBox.addBmFilter(bmFilterReqBySupplier);
						requisitionReceiptListBox.addBmFilter(bmFilterReqByProvisioned);
						requisitionReceiptListBox.addBmFilter(bmFilterReqByStatus);
					}
				}
			} else {
				BmFilter bmFilterReqBySupplier = new BmFilter();
				bmFilterReqBySupplier.setValueFilter(bmoRequisitionReceipt.getKind(), bmoRequisitionReceipt.getIdField(), bmoPaccount.getRequisitionReceiptId().toInteger());
				requisitionReceiptListBox.addBmFilter(bmFilterReqBySupplier);

				// limpiamos filtros de empresas y presupuestos
				//populateCompany(-1, ' ');
				populateBudgetItems(-1, -1);
			}
		}

		// Actualiza combo de empresas
		private void populateCompany(int companyId, char typeCxP) {
			companyListBox.clear();
			companyListBox.clearFilters();
			setCompanyListBoxFilters(companyId, typeCxP);
			companyListBox.populate("" + companyId);
		}

		// Asigna filtros al listado de empresas
		private void setCompanyListBoxFilters(int companyId, char typeCxP) {
			BmoCompany bmoCompany = new BmoCompany();
			BmFilter filterCompany = new BmFilter();

			if (companyId > 0) {
				filterCompany.setValueFilter(bmoCompany.getKind(), bmoCompany.getIdField(), companyId);
				companyListBox.addFilter(filterCompany);			
			} else {
				// Si es de tipo OC, aplica filtros de empresas, 
				// sino no lo reinicia, esto para la seleccion manual de la empresa y llenar partidas presp.
				if (typeCxP == BmoPaccountType.CATEGORY_REQUISITION) {
					filterCompany.setValueFilter(bmoCompany.getKind(), bmoCompany.getIdField(), "-1");
					companyListBox.addFilter(filterCompany);
				}
			}
		}

		// Actualiza combo de partidas presp.
		private void populateBudgetItems(int companyId, int budgetItemId) {
			budgetItemUiListBox.clear();
			budgetItemUiListBox.clearFilters();
			if (companyId > 0) {
				setBudgetItemsListBoxFilters(companyId, budgetItemId);
				budgetItemUiListBox.populate(bmoPaccount.getBudgetItemId());
			} else if (budgetItemId > 0) { 
				setBudgetItemsListBoxFilters(-1, budgetItemId);
				budgetItemUiListBox.populate("" + budgetItemId);
			} else {
				setBudgetItemsListBoxFilters(-1, budgetItemId);
				
				budgetItemUiListBox.populate(bmoPaccount.getBudgetItemId());
			}
		}

		// Asigna filtros al listado de partidas presp.
		private void setBudgetItemsListBoxFilters(int companyId, int budgetItemId) {
			BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();

			// darle prioridad a la partida
			if (budgetItemId > 0 || companyId > 0) {
				if (budgetItemId > 0) {
					if (companyId > 0) {
						BmFilter bmFilterByCompany = new BmFilter();
						bmFilterByCompany.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBmoBudget().getCompanyId(), companyId);
						budgetItemUiListBox.addBmFilter(bmFilterByCompany);		
					}
					
					// Filtro de egresos(cargo)
					BmFilter filterByWithdraw = new BmFilter();
					filterByWithdraw.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBmoBudgetItemType().getType().getName(), "" + BmoBudgetItemType.TYPE_WITHDRAW);
					budgetItemUiListBox.addFilter(filterByWithdraw);
					
				} else if (companyId > 0) {
					BmFilter bmFilterByCompany = new BmFilter();
					bmFilterByCompany.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBmoBudget().getCompanyId(), companyId);
					budgetItemUiListBox.addBmFilter(bmFilterByCompany);
					
					// Filtro de egresos(cargo)
					BmFilter filterByWithdraw = new BmFilter();
					filterByWithdraw.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBmoBudgetItemType().getType().getName(), "" + BmoBudgetItemType.TYPE_WITHDRAW);
					budgetItemUiListBox.addFilter(filterByWithdraw);
				} 
			} else {
				BmFilter bmFilter = new BmFilter();
				bmFilter.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getIdField(), bmoPaccount.getBudgetItemId().toInteger());
				budgetItemUiListBox.addBmFilter(bmFilter);
			}
		}

		protected class PaccountUpdater {
			public void changePaccount() {
				stopLoading();
				reset();
			}
		}

		public void saveAmountChange() {
			saveAmountChange(0);
		}
		
		public void saveAmountChange(int saveAmountChangeRpcAttempt) {
			
			if (saveAmountChangeRpcAttempt < 5) {
				setSaveAmountChangeRpcAttempt(saveAmountChangeRpcAttempt + 1);
				
				// Establece eventos ante respuesta de servicio
				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					@Override
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getSaveAmountChangeRpcAttempt() < 5) saveAmountChange(getSaveAmountChangeRpcAttempt());
						else showErrorMessage(this.getClass().getName() + "-saveAmountChange() ERROR: " + caught.toString());
					}
	
					@Override
					public void onSuccess(BmUpdateResult result) {
						stopLoading();
						setSaveAmountChangeRpcAttempt(0);
						processPaccountUpdateResult(result);
					}
				};
	
				// Llamada al servicio RPC
				try {
					if (!isLoading()) {
						startLoading();
						getUiParams().getBmObjectServiceAsync().save(bmoPaccount.getPmClass(), bmoPaccount, callback);
					}
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-saveAmountChange() ERROR: " + e.toString());
				}
			}
		}

		public void processPaccountUpdateResult(BmUpdateResult result) {
			if (result.hasErrors()) showFormMsg("Error al guardar el monto", "Error al guardar el monto: " + result.errorsToString());
			else updateAmount(id);
		}
		
		// Variables para llamadas RPC
		public int getUpdateAmountRpcAttempt() {
			return updateAmountRpcAttempt;
		}

		public void setUpdateAmountRpcAttempt(int updateAmountRpcAttempt) {
			this.updateAmountRpcAttempt = updateAmountRpcAttempt;
		}

		public int getPaccId() {
			return paccId;
		}

		public void setPaccId(int paccId) {
			this.paccId = paccId;
		}
		
		public int getUserAutorizedRpcAttempt() {
			return userAutorizedRpcAttempt;
		}

		public void setUserAutorizedRpcAttempt(int userAutorizedRpcAttempt) {
			this.userAutorizedRpcAttempt = userAutorizedRpcAttempt;
		}

		public int getUserId() {
			return userId;
		}

		public void setUserId(int userId) {
			this.userId = userId;
		}
		
		public int getSaveAmountChangeRpcAttempt() {
			return saveAmountChangeRpcAttempt;
		}

		public void setSaveAmountChangeRpcAttempt(int saveAmountChangeRpcAttempt) {
			this.saveAmountChangeRpcAttempt = saveAmountChangeRpcAttempt;
		}
	}

	private class UiPaccountXMLForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
		private BmoPaccount bmoPaccount = new BmoPaccount();
		private Button saveButton = new Button("AGREGAR");
		private Button lifeCycleCloseButton = new Button("CERRAR");
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		UiFileUploadBox xmlFileUpload = new UiFileUploadBox(getUiParams());
		UiListBox requisitionReceiptListBox = new UiListBox(getUiParams(), new BmoRequisitionReceipt());

		public UiPaccountXMLForm(UiParams uiParams, Panel defaultPanel) {
			super(uiParams, defaultPanel);
			this.bmoPaccount = new BmoPaccount();

			saveButton.setStyleName("formSaveButton");
			saveButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					prepareSave();
				}
			});
			saveButton.setEnabled(false);

			lifeCycleCloseButton.setStyleName("formCloseButton");
			lifeCycleCloseButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					paccountXMLDialogBox.hide();
				}
			});

			if (getSFParams().hasWrite(bmoPaccount.getProgramCode()))
				saveButton.setEnabled(true);
			buttonPanel.add(saveButton);
			buttonPanel.add(lifeCycleCloseButton);
			defaultPanel.add(formTable);
		}

		public void show() {
			formTable.addField(1, 0, xmlFileUpload, bmoPaccount.getXmlFileUpload());
			setRequisitionReceiptsListBoxFilters(bmoPaccount.getSupplierId().toInteger(),
					bmoPaccount.getTotal().toDouble(),bmoPaccount.getTaxApplies().toInteger());
			formTable.addField(2, 0, requisitionReceiptListBox, bmoPaccount.getRequisitionReceiptId());
			requisitionReceiptListBox.setEnabled(false);
			formTable.addButtonPanel(buttonPanel);

		}

		public void prepareSave() {
			try {
				createPaccount();
			} catch (BmException e) {
				showSystemMessage(this.getClass().getName() + "-prepareSave(): ERROR " + e.toString());
			}
		}

		public void createPaccount() throws BmException {
			String values = "";
			String requisitionReceip = "0";
			if (requisitionReceiptListBox.getSelectedIndex() > 0) {
				requisitionReceip = requisitionReceiptListBox.getSelectedId();
			}
			values = requisitionReceip + "|" + xmlFileUpload.getBlobKey();

			bmoPaccount.getXmlFileUpload().setValue(xmlFileUpload.getBlobKey());
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-createPaccount() ERROR: " + caught.toString());
				}

				public void onSuccess(BmUpdateResult result) {
					stopLoading();

					if (!result.hasErrors()) {
						showSystemMessage("Cuenta por Pagar Registrada!");
						paccountXMLDialogBox.hide();
						list();
					} else {
						BmoPaccount bmoPaccountResult = (BmoPaccount) result.getBmObject();
						if (result.getMsg().trim().equals(BmoPaccount.MORE_REQUISITIONRECEIPTS)) {
							showSystemMessage("Selecciona un Recibo");
							requisitionReceiptListBox.setEnabled(true);
							formTable.addField(2, 0, requisitionReceiptListBox, bmoPaccount.getRequisitionReceiptId());
							populateRequisitionReceipts(bmoPaccountResult.getSupplierId().toInteger(),
									bmoPaccountResult.getTotal().toDouble(),bmoPaccountResult.getTaxApplies().toInteger());

						} else {
							showErrorMessage(this.getClass().getName() + "-createPaccount ERROR: "
									+ result.getBmErrorList().get(0).msg + result.getMsg());
						}
					}
				}
			};

			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoPaccount.getPmClass(), bmoPaccount,
							BmoPaccount.ACTION_NEWPACCOUNT, values, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-createSessionSale() ERROR: " + e.toString());
			}
		}

		private void populateRequisitionReceipts(int supplierId,double total,int taxApplies) {
			requisitionReceiptListBox.clear();
			requisitionReceiptListBox.clearFilters();
			setRequisitionReceiptsListBoxFilters(supplierId,total,taxApplies);

			requisitionReceiptListBox.populate(bmoPaccount.getRequisitionReceiptId());
		}

		// Asigna filtros al listado de ROC
		private void setRequisitionReceiptsListBoxFilters(int supplierId, double total,int taxApplies) {
			BmoRequisitionReceipt bmoRequisitionReceipt = new BmoRequisitionReceipt();
			BmFilter bmFilterReqBySupplier = new BmFilter();
			BmFilter bmFilterReqStatus = new BmFilter();
			BmFilter bmFilterReqTotal = new BmFilter();
			BmFilter bmFilterReqTax = new BmFilter();
			int tax = 0;
			if(taxApplies != 0) {
				bmFilterReqTax.setValueOperatorFilter(bmoRequisitionReceipt.getKind(), bmoRequisitionReceipt.getTax(), BmFilter.MAYOR, tax);
			}
			else {
				bmFilterReqTax.setValueOperatorFilter(bmoRequisitionReceipt.getKind(), bmoRequisitionReceipt.getTax(), BmFilter.EQUALS, tax);
			}

			bmFilterReqStatus.setValueFilter(bmoRequisitionReceipt.getKind(), bmoRequisitionReceipt.getPayment(),
					"" + BmoRequisitionReceipt.PAYMENT_NOTPROVISIONED);

			bmFilterReqBySupplier.setValueFilter(bmoRequisitionReceipt.getKind(), 
					bmoRequisitionReceipt.getSupplierId(),supplierId);

			bmFilterReqTotal.setValueFilter(bmoRequisitionReceipt.getKind(), 
					bmoRequisitionReceipt.getTotal(),String.valueOf(total));

			requisitionReceiptListBox.addBmFilter(bmFilterReqStatus);
			requisitionReceiptListBox.addBmFilter(bmFilterReqBySupplier);
			requisitionReceiptListBox.addBmFilter(bmFilterReqTotal);
			requisitionReceiptListBox.addBmFilter(bmFilterReqTax);
		}
	}
}
