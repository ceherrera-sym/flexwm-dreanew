package com.flexwm.client.fi;

import com.flexwm.client.fi.UiPaccount.UiPaccountForm.PaccountUpdater;
import com.flexwm.shared.fi.BmoBankMovement;
import com.flexwm.shared.fi.BmoPaccount;
import com.flexwm.shared.fi.BmoPaccountAssignment;
import com.flexwm.shared.fi.BmoPaccountType;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent.LoadingState;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiFormFlexTable;
import com.symgae.client.ui.UiListDataProvider;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.client.ui.UiSuggestBoxAction;


public class UiPaccountAssignmentGrid extends Ui {
	private UiFormFlexTable formFlexTable = new UiFormFlexTable(getUiParams());
	private NumberFormat numberFormat = NumberFormat.getDecimalFormat();

	// PaccountAssignments
	UiSuggestBox changePaccountSuggestBox;
	private BmoPaccountAssignment bmoPaccountAssignment = new BmoPaccountAssignment();
	private FlowPanel paccountAssignementPanel = new FlowPanel();
	private CellTable<BmObject> paccountAssignementGrid = new CellTable<BmObject>();
	private UiListDataProvider<BmObject> paccountAssignementData;
	BmFilter paccountAssignementFilter;

	private Button addPaccountAssignmentWithDrawButton = new Button("+ APLICAR");
	protected DialogBox paccountAssignementDialogBox;

	protected DialogBox showBankMovementDialogBox;

	// Cambio de Paccount
	DialogBox changePaccountDialogBox;
	Button changePaccountButton = new Button("CAMBIAR");
	Button closePaccountButton = new Button("CERRAR");

	// Otros
	private HorizontalPanel buttonPanel = new HorizontalPanel();
	private BmoPaccount bmoPaccount;
	private PaccountUpdater paccountUpdater;

	public UiPaccountAssignmentGrid(UiParams uiParams, Panel defaultPanel, BmoPaccount bmoPaccount, PaccountUpdater paccountUpdater){
		super(uiParams, defaultPanel);
		this.bmoPaccount = bmoPaccount;
		this.paccountUpdater = paccountUpdater;		 

		closePaccountButton.setStyleName("formCloseButton");
		closePaccountButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				changePaccountDialogBox.hide();
			}
		});

		
		// Inicializar grid de Personal
		paccountAssignementGrid = new CellTable<BmObject>();
		paccountAssignementGrid.setWidth("100%");
		paccountAssignementPanel.clear();
		paccountAssignementPanel.setWidth("100%");
		defaultPanel.setStyleName("detailStart");
		setPaccountAssignmentColumns();
		paccountAssignementFilter = new BmFilter();

		if (bmoPaccount.getBmoPaccountType().getType().equals(BmoPaccountType.TYPE_DEPOSIT)) {
			paccountAssignementFilter.setValueFilter(bmoPaccountAssignment.getKind(), bmoPaccountAssignment.getPaccountId().getName(), bmoPaccount.getId());
		} else {
			paccountAssignementFilter.setValueFilter(bmoPaccountAssignment.getKind(), bmoPaccountAssignment.getForeignPaccountId().getName(), bmoPaccount.getId());
		}

		paccountAssignementGrid.addLoadingStateChangeHandler(new LoadingStateChangeEvent.Handler() {           
			@Override
			public void onLoadingStateChanged(LoadingStateChangeEvent event) {
				if (event.getLoadingState() == LoadingState.LOADED) {
					changeHeight();
				}
			}
		}); 
		paccountAssignementData = new UiListDataProvider<BmObject>(new BmoPaccountAssignment(), paccountAssignementFilter);
		paccountAssignementData.addDataDisplay(paccountAssignementGrid);
		paccountAssignementPanel.add(paccountAssignementGrid);

		// Panel de botones
		addPaccountAssignmentWithDrawButton.setStyleName("formSaveButton");
		addPaccountAssignmentWithDrawButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addPaccountWithDraw();
			}
		});
		buttonPanel.setHeight("100%");
		buttonPanel.setStyleName("formButtonPanel");
		buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		if (bmoPaccount.getBmoPaccountType().getType().equals(BmoPaccountType.TYPE_DEPOSIT) &&
				!(bmoPaccountAssignment.getForeignPaccountId().toInteger() > 0)) {
			buttonPanel.add(addPaccountAssignmentWithDrawButton);
		}

		// Crear forma y campos
		formFlexTable.setWidth("100%");
		formFlexTable.addPanel(1, 0, paccountAssignementPanel);
		formFlexTable.addButtonPanel(buttonPanel);
		defaultPanel.add(formFlexTable);
	}

	public void show(){
		paccountAssignementData.list();
		paccountAssignementGrid.redraw();

		statusEffect();
	}

	public void statusEffect(){

	}

	public void reset(){
		paccountUpdater.changePaccount();
	}

	public void changeHeight() {
		paccountAssignementGrid.setVisibleRange(0, paccountAssignementData.getList().size());
	}

	public void setPaccountAssignmentColumns() {

		// CxP Origen
		Column<BmObject, String> originColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {				
				return BmoPaccount.CODE_PREFIX + ((BmoPaccountAssignment)bmObject).getPaccountId().toString();
			}
		};
		paccountAssignementGrid.addColumn(originColumn, SafeHtmlUtils.fromSafeConstant("CxP Origen"));
		originColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		paccountAssignementGrid.setColumnWidth(originColumn, 50, Unit.PX);

		// CxP Destino
		Column<BmObject, String> foreignColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {				
				return BmoPaccount.CODE_PREFIX + ((BmoPaccountAssignment)bmObject).getForeignPaccountId().toString();
			}
		};
		paccountAssignementGrid.addColumn(foreignColumn, SafeHtmlUtils.fromSafeConstant("CxP Destino"));
		foreignColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		paccountAssignementGrid.setColumnWidth(foreignColumn, 50, Unit.PX);

		// Factura
		Column<BmObject, String> invoiceColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoPaccountAssignment)bmObject).getInvoiceno().toString();
			}
		};
		paccountAssignementGrid.addColumn(invoiceColumn, SafeHtmlUtils.fromSafeConstant("Fact."));
		invoiceColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		paccountAssignementGrid.setColumnWidth(invoiceColumn, 150, Unit.PX);

		// Mostrar el boton para ir a la CxC o CxP
		if (bmoPaccount.getBmoPaccountType().getCategory().equals(BmoPaccountType.CATEGORY_OTHER) &&
				bmoPaccount.getBmoPaccountType().getType().equals(BmoPaccountType.TYPE_WITHDRAW)) {				
			// Seguimiento
			Column<BmObject, String> showBankMovement;
			showBankMovement = new Column<BmObject, String>(new ButtonCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return "Doc";
				}
			};
			showBankMovement.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					int raccountId = ((BmoPaccountAssignment)bmObject).getPaccountId().toInteger();
					showBankMovement(raccountId);
				}
			});

			paccountAssignementGrid.addColumn(showBankMovement, SafeHtmlUtils.fromSafeConstant("Ver"));
			showBankMovement.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			paccountAssignementGrid.setColumnWidth(showBankMovement, 50, Unit.PX);
		}	

		// Monto
		Column<BmObject, String> amountColumn;
		if (bmoPaccount.getStatus().equals(BmoPaccount.STATUS_REVISION)) {
			amountColumn = new Column<BmObject, String>(new EditTextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					numberFormat = NumberFormat.getCurrencyFormat();
					String formatted = numberFormat.format((((BmoPaccountAssignment)bmObject).getAmount().toDouble()));
					return formatted;
				}
			};
			amountColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					changePrice(bmObject, value);
				}
			});
		} else {
			amountColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					numberFormat = NumberFormat.getCurrencyFormat();
					double amount = 0;
					if (((BmoPaccountAssignment)bmObject).getAmountConverted().toDouble() > 0 )
						amount = ((BmoPaccountAssignment)bmObject).getAmountConverted().toDouble();
					else
						amount = ((BmoPaccountAssignment)bmObject).getAmount().toDouble();

					String formatted = numberFormat.format(amount);
					return (formatted);
				}
			};
		}

		amountColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		paccountAssignementGrid.addColumn(amountColumn, SafeHtmlUtils.fromSafeConstant("Monto"));
		paccountAssignementGrid.setColumnWidth(amountColumn, 50, Unit.PX);

		// Eliminar
		Column<BmObject, String> deleteColumn;
		if (bmoPaccount.getStatus().equals(BmoPaccount.STATUS_REVISION)) {
			deleteColumn = new Column<BmObject, String>(new ButtonCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return "-";
				}
			};
			deleteColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					deleteItem(bmObject);
				}
			});
		} else {
			deleteColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return " ";
				}
			};
		}
		paccountAssignementGrid.addColumn(deleteColumn, SafeHtmlUtils.fromSafeConstant("Eliminar"));
		deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		paccountAssignementGrid.setColumnWidth(deleteColumn, 50, Unit.PX);		
	}

	public void changePrice(BmObject bmObject, String amount) {
		bmoPaccountAssignment = (BmoPaccountAssignment)bmObject;
		try {
			double p = Double.parseDouble(amount);	
			if (bmoPaccount.getStatus().equals(BmoPaccount.STATUS_AUTHORIZED)) {
				showErrorMessage("No se puede modificar el monto, esta ligado a un recibo");
				reset();
			} else {
				if (p > 0) {
					bmoPaccountAssignment.getAmount().setValue(amount);
					saveItemChange();
				} else {
					//Si que el monto sea mayor a cero
					showErrorMessage("El monto debe ser mayor a $ 0.00.");
					reset();
				}
			}
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-changePrice(): ERROR " + e.toString());
		}
	}

	public void saveItemChange() {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-saveItemChange(): ERROR " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				processItemChangeSave(result);
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()){
				startLoading();
				getUiParams().getBmObjectServiceAsync().save(bmoPaccountAssignment.getPmClass(), bmoPaccountAssignment, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-saveItemChange(): ERROR " + e.toString());
		}
	}

	public void processItemChangeSave(BmUpdateResult bmUpdateResult) {
		if (bmUpdateResult.hasErrors()) 
			showSystemMessage("Error al modificar el Item: " + bmUpdateResult.errorsToString());
		else {
			this.reset();
		}
	}

	public void addPaccountWithDraw(){
		addPaccountWithDraw(new BmoPaccount());
	}

	public void showBankMovement(int raccountId) {		
		showBankMovementDialogBox = new DialogBox(true);
		showBankMovementDialogBox.setGlassEnabled(true);

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "130px");

		showBankMovementDialogBox.setWidget(vp);
		// Verifica si es Nota de credito o MB
		getPaccount(raccountId);

	}

	//Obtener el concepto de Banco ligado a la CxC
	private void getBankMovement(int id) {
		BmoBankMovement bmoBankMovement = new BmoBankMovement();

		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			@Override
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getBankMovement() ERROR: " + caught.toString());
			}

			@Override
			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				if (!result.hasErrors()) {
					if (result.getBmObject() != null) {
						BmoBankMovement bmoBankMovement = (BmoBankMovement)result.getBmObject();					
						UiBankMovement uiBankMovement = new UiBankMovement(getUiParams());
						uiBankMovement.open(bmoBankMovement);
					} 
				}	
			}
		};
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().action(bmoBankMovement.getPmClass(), bmoBankMovement, BmoBankMovement.ACTION_SHOWBANKMOVEMENTCC, "" + id, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-getBankMovement(): ERROR " + e.toString());
		}
	}

	private void getPaccount(int id) {

		AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
			@Override
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getPaccount() ERROR: " + caught.toString());
			}

			@Override
			public void onSuccess(BmObject result) {
				stopLoading();				
				BmoPaccount bmoPacc = (BmoPaccount)result;

				if (bmoPacc.getBmoPaccountType().getCategory().equals(BmoPaccountType.CATEGORY_CREDITNOTE)) {
					UiPaccount uiPaccount = new UiPaccount(getUiParams());
					uiPaccount.edit(bmoPacc);
				} else {
					//Obtener el MB 
					getBankMovement(bmoPacc.getId());	
				}
			}
		};
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().get(bmoPaccount.getPmClass(), id, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-getPaccount(): ERROR " + e.toString());
		}
	}

	public void addPaccountWithDraw(BmoPaccount bmoForeignPaccount) {
		paccountAssignementDialogBox = new DialogBox(true);
		paccountAssignementDialogBox.setGlassEnabled(true);
		paccountAssignementDialogBox.setText("Aplicación de Pago");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "120px");

		paccountAssignementDialogBox.setWidget(vp);

		UiPaccountAssignmentWithDrawForm paccountAssignmentWithDrawForm = new UiPaccountAssignmentWithDrawForm(getUiParams(), vp, bmoPaccount, bmoForeignPaccount);
		paccountAssignmentWithDrawForm.show();

		paccountAssignementDialogBox.center();
		paccountAssignementDialogBox.show();
	}

	private void deleteItem(BmObject bmObject) {
		bmoPaccountAssignment = (BmoPaccountAssignment)bmObject;
		deleteItem();
	}

	public void deleteItem() {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-deleteItem(): ERROR " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				processItemDelete(result);
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().delete(bmoPaccountAssignment.getPmClass(), bmoPaccountAssignment, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-deleteItem(): ERROR " + e.toString());
		}
	}

	public void processItemDelete(BmUpdateResult bmUpdateResult) {
		if (bmUpdateResult.hasErrors()) 
			showSystemMessage("Error al modificar el Item: " + bmUpdateResult.errorsToString());
		else {
			this.reset();			
		}		
	}

	// Agrega un item de un producto a un grupo de la cotizacion
	private class UiPaccountAssignmentWithDrawForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
		private TextBox amountTextBox = new TextBox();
		private TextBox amountConvertedTextBox = new TextBox();
		private TextBox parityTextBox = new TextBox();
		private BmoPaccountAssignment bmoPaccountAssignment;
		private Button saveButton = new Button("AGREGAR");
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		private UiSuggestBox foreignPaccountSuggestBox = new UiSuggestBox(new BmoPaccount());
		private BmoPaccount bmoPaccount;
		private BmoPaccount bmoPaccountForeign;

		public UiPaccountAssignmentWithDrawForm(UiParams uiParams, Panel defaultPanel, BmoPaccount bmoPaccount, BmoPaccount bmoForeignPaccount) {
			super(uiParams, defaultPanel);
			this.bmoPaccountAssignment = new BmoPaccountAssignment();
			this.bmoPaccount = bmoPaccount;

			try {
				bmoPaccountAssignment.getPaccountId().setValue(bmoPaccount.getId());
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "() ERROR: " + e.toString());
			}

			// Manejo de acciones de suggest box
			UiSuggestBoxAction uiSuggestBoxAction = new UiSuggestBoxAction() {
				@Override
				public void onSelect(UiSuggestBox uiSuggestBox) {
					formSuggestionChange(uiSuggestBox);
				}
			};
			formTable.setUiSuggestBoxAction(uiSuggestBoxAction);

			saveButton.setStyleName("formSaveButton");
			saveButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					prepareSave();
				}
			});
			saveButton.setVisible(false);

			// Manejo de cambios de textbox
			ValueChangeHandler<String> textChangeHandler = new ValueChangeHandler<String>() {
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					formTextChange(event);
				}
			};
			formTable.setTextChangeHandler(textChangeHandler);

			if (getSFParams().hasWrite(bmoPaccountAssignment.getProgramCode())) saveButton.setVisible(true);
			buttonPanel.add(saveButton);

			// Filtros CxP destino
			//ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
			BmFilter filterBySupplier = new BmFilter();
			filterBySupplier.setValueFilter(bmoPaccount.getKind(), bmoPaccount.getSupplierId(), bmoPaccount.getSupplierId().toInteger());
			//filterList.add(filterBySupplier);

			BmFilter filterByPaymentStatus = new BmFilter();
			filterByPaymentStatus.setValueOperatorFilter(bmoPaccount.getKind(), bmoPaccount.getPaymentStatus(), BmFilter.NOTEQUALS, "" + BmoPaccount.PAYMENTSTATUS_TOTAL);
			//filterList.add(filterByPaymentStatus);

			BmFilter filterByStatus = new BmFilter();
			filterByStatus.setValueFilter(bmoPaccount.getKind(), bmoPaccount.getStatus(), "" + BmoPaccount.STATUS_AUTHORIZED);
			//filterList.add(filterByStatus);

			BmFilter filterByType = new BmFilter();
			filterByType.setValueFilter(bmoPaccount.getKind(), bmoPaccount.getBmoPaccountType().getType(), "" + BmoPaccountType.TYPE_WITHDRAW);
			//filterList.add(filterByType);
			
			// al pagar con una nota de credito la cxp destino debe tener la misma noneda
			BmFilter filterByCurrency = new BmFilter();
			filterByCurrency.setValueFilter(bmoPaccount.getKind(), bmoPaccount.getCurrencyId(), bmoPaccount.getCurrencyId().toInteger());
			//filterList.add(filterByCurrency);

			foreignPaccountSuggestBox = new UiSuggestBox(new BmoPaccount());
			foreignPaccountSuggestBox.addFilter(filterBySupplier);
			foreignPaccountSuggestBox.addFilter(filterByPaymentStatus);
			foreignPaccountSuggestBox.addFilter(filterByStatus);
			foreignPaccountSuggestBox.addFilter(filterByType);
			foreignPaccountSuggestBox.addFilter(filterByCurrency);

			defaultPanel.add(formTable);
		}

		public void show(){
			formTable.addField(1, 0, foreignPaccountSuggestBox, bmoPaccountAssignment.getForeignPaccountId());
			formTable.addField(2, 0, amountTextBox, bmoPaccountAssignment.getAmount());			
			statusEffect();
		}

		// Actualizar cantidades
		public void formSuggestionChange(UiSuggestBox uiSuggestBox) {
			NumberFormat fmt = NumberFormat.getFormat("#####.##");
			bmoPaccountForeign = (BmoPaccount)foreignPaccountSuggestBox.getSelectedBmObject();			
			if (bmoPaccountForeign.getCurrencyId().toInteger() != bmoPaccount.getCurrencyId().toInteger()) {


				double factor = bmoPaccount.getCurrencyParity().toDouble() / bmoPaccountForeign.getCurrencyParity().toDouble();
				amountTextBox.setText("" + fmt.format(bmoPaccount.getBalance().toDouble()));

				//Aplicar el total del saldo de CxP Cargo				
				double balanceForeign = (bmoPaccount.getBalance().toDouble() * factor);
				if (balanceForeign > bmoPaccountForeign.getBalance().toDouble())
					balanceForeign = balanceForeign - bmoPaccountForeign.getBalance().toDouble();
				else	
					balanceForeign  = bmoPaccountForeign.getBalance().toDouble() - balanceForeign;

				amountConvertedTextBox.setText("" + fmt.format(balanceForeign));

				factor = bmoPaccount.getCurrencyParity().toDouble() * bmoPaccountForeign.getCurrencyParity().toDouble() ;

				try {
					bmoPaccountAssignment.getAmount().setValue(amountTextBox.getText());
					bmoPaccountAssignment.getCurrencyParity().setValue(fmt.format(factor));
					bmoPaccountAssignment.getAmountConverted().setValue(amountConvertedTextBox.getText());
				} catch (BmException e) {
					showSystemMessage(this.getClass().getName() + " ERROR: " + e.toString());
				}
				formTable.addField(2, 0, amountTextBox, bmoPaccountAssignment.getAmount());
				formTable.addField(3, 0, parityTextBox, bmoPaccountAssignment.getCurrencyParity());
				formTable.addField(4, 0, amountConvertedTextBox, bmoPaccountAssignment.getAmountConverted());
			} else {			
				amountTextBox.setText(bmoPaccountForeign.getBalance().toString());
			}	

			formTable.addButtonPanel(buttonPanel);

			statusEffect();
		}

		public void formTextChange(ValueChangeEvent<String> event) {			
			NumberFormat fmt = NumberFormat.getFormat("#####.##");
			double parityOrigen = bmoPaccount.getCurrencyParity().toDouble();
			double parityDestiny = bmoPaccountAssignment.getCurrencyParity().toDouble();
			double parity = Double.parseDouble(parityTextBox.getText());


			if (parityOrigen > parityDestiny) { 

				if (event.getSource() == amountTextBox) {
					amountConvertedTextBox.setText("" + fmt.format(Double.parseDouble(amountTextBox.getText()) * parity));
				} 
				// Se cambia la paridad
				else if (event.getSource() == parityTextBox) {
					amountTextBox.setText("" + fmt.format(Double.parseDouble(amountConvertedTextBox.getText()) / parity ));
				}
				// Se cambia el monto a aplicar en cxp o cxc
				else if (event.getSource() == amountConvertedTextBox) {
					amountTextBox.setText("" + fmt.format(Double.parseDouble(amountConvertedTextBox.getText()) / parity ));					
				}
			} else {
				if (event.getSource() == amountTextBox) {
					amountConvertedTextBox.setText("" + fmt.format(Double.parseDouble(amountTextBox.getText()) / parity));
				} 
				// Se cambia la paridad
				else if (event.getSource() == parityTextBox) {
					amountTextBox.setText("" + fmt.format(Double.parseDouble(amountConvertedTextBox.getText()) * parity ));
				}
				// Se cambia el monto a aplicar en cxp o cxc
				else if (event.getSource() == amountConvertedTextBox) {
					amountTextBox.setText("" + fmt.format(Double.parseDouble(amountConvertedTextBox.getText()) * parity ));					
				}
			}
		}

		private void statusEffect(){

		}

		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) 
				showSystemMessage("Error al modificar Aplicación: " + bmUpdateResult.errorsToString());
			else {
				paccountAssignementDialogBox.hide();
				reset();
			}
		}

		public void prepareSave() {
			try {
				bmoPaccountAssignment = new BmoPaccountAssignment();
				bmoPaccountAssignment.getForeignPaccountId().setValue(foreignPaccountSuggestBox.getSelectedId());
				bmoPaccountAssignment.getAmount().setValue(amountTextBox.getText());
				bmoPaccountAssignment.getCurrencyParity().setValue(parityTextBox.getText());
				bmoPaccountAssignment.getAmountConverted().setValue(amountConvertedTextBox.getText());
				bmoPaccountAssignment.getPaccountId().setValue(bmoPaccount.getId());
				save();
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-prepareSave() ERROR: " + e.toString());
			}
		}

		public void save() {
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-save() ERROR: " + caught.toString());
				}

				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					processUpdateResult(result);
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().save(bmoPaccountAssignment.getPmClass(), bmoPaccountAssignment, callback);					
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-save() ERROR: " + e.toString());
			}
		}
	}
}
