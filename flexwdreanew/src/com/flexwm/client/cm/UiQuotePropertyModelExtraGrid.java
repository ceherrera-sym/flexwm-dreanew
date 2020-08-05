package com.flexwm.client.cm;


import com.flexwm.client.cm.UiQuoteForm.QuoteUpdater;
import com.flexwm.shared.co.BmoPropertyModel;
import com.flexwm.shared.co.BmoPropertyModelExtra;
import com.flexwm.shared.cm.BmoQuote;
import com.flexwm.shared.cm.BmoQuotePropertyModelExtra;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiFormFlexTable;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiListDataProvider;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;


public class UiQuotePropertyModelExtraGrid extends Ui {
	private UiFormFlexTable formFlexTable = new UiFormFlexTable(getUiParams());

	// QuotePropertyModelExtra
	private BmoQuotePropertyModelExtra bmoQuotePropertyModelExtra = new BmoQuotePropertyModelExtra();
	private FlowPanel quotePropertyModelExtraPanel = new FlowPanel();
	private CellTable<BmObject> quotePropertyModelExtraGrid = new CellTable<BmObject>();
	private UiListDataProvider<BmObject> quotePropertyModelExtraData;
	BmFilter quotePropertyModelExtraFilter;
	private TextArea quotePropertyModelExtraDescriptionTextArea = new TextArea();
	DialogBox quotePropertyModelExtraDescriptionDialogBox;
	Button changePropertyModelExtraDescriptionButton = new Button("GUARDAR");
	Button closePropertyModelExtraDescriptionButton = new Button("CERRAR");

	private Button addQuotePropertyModelExtraButton = new Button("+EXTRA");
	protected DialogBox quotePropertyModelExtraDialogBox;	

	// Cambio de PropertyModelExtra
	Button changePropertyModelExtraButton = new Button("CAMBIAR");
	Button closePropertyModelExtraButton = new Button("CERRAR");

	// Otros
	private HorizontalPanel buttonPanel = new HorizontalPanel();
	//	private Label amountLabel = new Label();
	private NumberFormat numberFormat = NumberFormat.getDecimalFormat();
	private BmoQuote bmoQuote;
	private QuoteUpdater quoteUpdater;


	public UiQuotePropertyModelExtraGrid(UiParams uiParams, Panel defaultPanel, BmoQuote bmoQuote, QuoteUpdater quoteUpdater){
		super(uiParams, defaultPanel);
		this.bmoQuote = bmoQuote;
		this.quoteUpdater = quoteUpdater;

		// Boton de cerrar dialogo de personal
		changePropertyModelExtraDescriptionButton.setStyleName("formCloseButton");
		changePropertyModelExtraDescriptionButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				changeQuotePropertyModelExtraDescription();
				quotePropertyModelExtraDescriptionDialogBox.hide();
			}
		});

		closePropertyModelExtraDescriptionButton.setStyleName("formCloseButton");
		closePropertyModelExtraDescriptionButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				quotePropertyModelExtraDescriptionDialogBox.hide();
			}
		});

		// Inicializar grid de Extra
		quotePropertyModelExtraGrid = new CellTable<BmObject>();
		quotePropertyModelExtraGrid.setWidth("100%");
		quotePropertyModelExtraPanel.clear();
		quotePropertyModelExtraPanel.setWidth("100%");
		setQuotePropertyModelExtraColumns();
		quotePropertyModelExtraFilter = new BmFilter();
		quotePropertyModelExtraFilter.setValueFilter(bmoQuotePropertyModelExtra.getKind(), bmoQuotePropertyModelExtra.getQuoteId().getName(), bmoQuote.getId());
		quotePropertyModelExtraGrid.addLoadingStateChangeHandler(new LoadingStateChangeEvent.Handler() {           
			@Override
			public void onLoadingStateChanged(LoadingStateChangeEvent event) {
				if (event.getLoadingState() == LoadingState.LOADED) {
					changeHeight();
				}
			}
		}); 
		quotePropertyModelExtraData = new UiListDataProvider<BmObject>(new BmoQuotePropertyModelExtra(), quotePropertyModelExtraFilter);
		quotePropertyModelExtraData.addDataDisplay(quotePropertyModelExtraGrid);
		quotePropertyModelExtraPanel.add(quotePropertyModelExtraGrid);

		// Panel de botones
		addQuotePropertyModelExtraButton.setStyleName("formSaveButton");
		addQuotePropertyModelExtraButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addPropertyModelExtra();
			}
		});
		buttonPanel.setHeight("100%");
		buttonPanel.setStyleName("formButtonPanel");
		buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		buttonPanel.add(addQuotePropertyModelExtraButton);

		// Crear forma y campos
		formFlexTable.setWidth("100%");
		formFlexTable.addPanel(1, 0, quotePropertyModelExtraPanel);
		formFlexTable.addButtonPanel(buttonPanel);
		defaultPanel.add(formFlexTable);
	}

	public void show(){
		quotePropertyModelExtraData.list();
		quotePropertyModelExtraGrid.redraw();

		statusEffect();
	}

	public void statusEffect(){
		if (bmoQuote.getStatus().toChar() == BmoQuote.STATUS_AUTHORIZED || bmoQuote.getStatus().toChar() == BmoQuote.STATUS_CANCELLED) {
			addQuotePropertyModelExtraButton.setVisible(false);
			changePropertyModelExtraDescriptionButton.setEnabled(false);
		} else {
			addQuotePropertyModelExtraButton.setVisible(true);
		}
	}

	public void reset(){
		quoteUpdater.changeQuotePropertyModelExtra();
	}

	public void changeHeight() {
		quotePropertyModelExtraGrid.setPageSize(quotePropertyModelExtraData.getList().size());
		quotePropertyModelExtraGrid.setVisibleRange(0, quotePropertyModelExtraData.getList().size());
	}

	public void addPropertyModelExtra(){
		addPropertyModelExtra(new BmoPropertyModelExtra());
	}

	public void addPropertyModelExtra(BmoPropertyModelExtra bmoPropertyModelExtra) {
		quotePropertyModelExtraDialogBox = new DialogBox(true);
		quotePropertyModelExtraDialogBox.setGlassEnabled(true);
		quotePropertyModelExtraDialogBox.setText("Extra de Cotización");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "200px");

		quotePropertyModelExtraDialogBox.setWidget(vp);

		UiQuotePropertyModelExtraForm quotePropertyModelExtraForm = new UiQuotePropertyModelExtraForm(getUiParams(), vp, bmoQuote, bmoPropertyModelExtra);

		quotePropertyModelExtraForm.show();

		quotePropertyModelExtraDialogBox.center();
		quotePropertyModelExtraDialogBox.show();
	}

	// Columnas grid de personal
	public void setQuotePropertyModelExtraColumns() {

		if (!isMobile()) {
			// Cantidad
			Column<BmObject, String> quantityColumn = new Column<BmObject, String>(new EditTextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoQuotePropertyModelExtra)bmObject).getQuantity().toString();
				}
			};
			quantityColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			quotePropertyModelExtraGrid.addColumn(quantityColumn, SafeHtmlUtils.fromSafeConstant("Cant."));
			quantityColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					// Called when the user changes the value.
					changeQuotePropertyModelExtraQuantity(bmObject, value);
				}
			});
		}
		// Nombre
		Column<BmObject, String> nameColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoQuotePropertyModelExtra)bmObject).getBmoPropertyModelExtra().getName().toString();
			}
		};
		quotePropertyModelExtraGrid.addColumn(nameColumn, SafeHtmlUtils.fromSafeConstant("Extra"));

		// Columna comentarios
		Column<BmObject, String> commentsColumn = new Column<BmObject, String>(new ButtonCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				if (((BmoQuotePropertyModelExtra)bmObject).getComments().toString().length() > 0)
					return "...";
				else return ".";
			}
		};
		commentsColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		quotePropertyModelExtraGrid.addColumn(commentsColumn, SafeHtmlUtils.fromSafeConstant("Coment."));
		commentsColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
			@Override
			public void update(int index, BmObject bmObject, String value) {
				quotePropertyModelExtraDescriptionDialog((BmoQuotePropertyModelExtra)bmObject);
			}
		});

		if (!isMobile()) {
			// Precio
			Column<BmObject, String> priceColumn = new Column<BmObject, String>(new EditTextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoQuotePropertyModelExtra)bmObject).getPrice().toString();
				}
			};
			priceColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
			quotePropertyModelExtraGrid.addColumn(priceColumn, SafeHtmlUtils.fromSafeConstant("Precio"));
			priceColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					changeQuotePropertyModelExtraPrice(bmObject, value);
				}
			});
		}
		// Total
		Column<BmObject, String> totalColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				numberFormat = NumberFormat.getCurrencyFormat();
				String formatted = numberFormat.format(((BmoQuotePropertyModelExtra)bmObject).getAmount().toDouble());
				return (formatted);
			}
		};
		totalColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		quotePropertyModelExtraGrid.addColumn(totalColumn, SafeHtmlUtils.fromSafeConstant("Total"));

		// Eliminar
		Column<BmObject, String> deleteColumn = new Column<BmObject, String>(new ButtonCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return "-";
			}
		};
		deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		quotePropertyModelExtraGrid.addColumn(deleteColumn, SafeHtmlUtils.fromSafeConstant("Eliminar"));
		deleteColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
			@Override
			public void update(int index, BmObject bmObject, String value) {
				deletePropertyModelExtra((BmoQuotePropertyModelExtra)bmObject);
			}
		});
	}

	public void quotePropertyModelExtraDescriptionDialog(BmoQuotePropertyModelExtra bmoQuotePropertyModelExtra) {
		this.bmoQuotePropertyModelExtra = bmoQuotePropertyModelExtra;
		quotePropertyModelExtraDescriptionDialogBox = new DialogBox(true);
		quotePropertyModelExtraDescriptionDialogBox.setGlassEnabled(true);
		quotePropertyModelExtraDescriptionDialogBox.setText("Descripción Extra");
		quotePropertyModelExtraDescriptionDialogBox.setSize("400px", "200px");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "200px");
		quotePropertyModelExtraDescriptionDialogBox.setWidget(vp);

		quotePropertyModelExtraDescriptionTextArea = new TextArea();
		quotePropertyModelExtraDescriptionTextArea.setSize("320px", "190px");
		quotePropertyModelExtraDescriptionTextArea.setText(bmoQuotePropertyModelExtra.getComments().toString());
		vp.add(quotePropertyModelExtraDescriptionTextArea);

		HorizontalPanel descriptionButtonPanel = new HorizontalPanel();
		descriptionButtonPanel.add(changePropertyModelExtraDescriptionButton);
		descriptionButtonPanel.add(closePropertyModelExtraDescriptionButton);
		vp.add(descriptionButtonPanel);

		quotePropertyModelExtraDescriptionDialogBox.center();
		quotePropertyModelExtraDescriptionDialogBox.show();
	}

	public void changeQuotePropertyModelExtraDescription() {
		String description = quotePropertyModelExtraDescriptionTextArea.getText();

		try {
			if (description.length() >= 0) {
				bmoQuotePropertyModelExtra.getComments().setValue(description);
				savePropertyModelExtraChange();
			}
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-changeQuotePropertyModelExtraDescription() ERROR: " + e.toString());
		}
	}

	public void changeQuotePropertyModelExtraQuantity(BmObject bmObject, String quantity) {
		bmoQuotePropertyModelExtra = (BmoQuotePropertyModelExtra)bmObject;
		if (bmoQuote.getStatus().toChar() != BmoQuote.STATUS_AUTHORIZED) {
			try {
				int q = Integer.parseInt(quantity);
				if (q > 0) {
					bmoQuotePropertyModelExtra.getQuantity().setValue(quantity);
					savePropertyModelExtraChange();
				} else {
					// Eliminar registro
					deletePropertyModelExtra();
				}
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-changeQuantity(): ERROR " + e.toString());
			}
		} else {
			String status= "";
			if(bmoQuote.getStatus().toChar()== BmoQuote.STATUS_AUTHORIZED)
				status= "Autorizada";
			else if(bmoQuote.getStatus().toChar()== BmoQuote.STATUS_CANCELLED)
				status= "Cancelada";

			showSystemMessage("La Cotización no se puede modificar - está " + status);
		}
	}

	public void changeQuotePropertyModelExtraPrice(BmObject bmObject, String price) {
		bmoQuotePropertyModelExtra = (BmoQuotePropertyModelExtra)bmObject;
		try {
			Double p = Double.parseDouble(price);
			if (bmoQuotePropertyModelExtra.getPropertyModelExtraId().toInteger() > 0) {
				if (p == 0) {
					bmoQuotePropertyModelExtra.getPrice().setValue(0);
					savePropertyModelExtraChange();
				} else {
					if (bmoQuotePropertyModelExtra.getBmoPropertyModelExtra().getFixedPrice().toBoolean()) {
						showSystemMessage("El Costo del Extra es Fijo. Se reestablecerá Precio del Catálogo.");	
					}
					bmoQuotePropertyModelExtra.getPrice().setValue(p);
					savePropertyModelExtraChange();
				}
			} else {
				bmoQuotePropertyModelExtra.getPrice().setValue(p);
				savePropertyModelExtraChange();
			}
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-changePrice(): ERROR " + e.toString());
		}
	}

	public void savePropertyModelExtraChange() {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-savePropertyModelExtraChange(): ERROR " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				processPropertyModelExtraChangeSave(result);
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().save(bmoQuotePropertyModelExtra.getPmClass(), bmoQuotePropertyModelExtra, callback);	
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-savePropertyModelExtraChange(): ERROR " + e.toString());
		}
	}

	public void deletePropertyModelExtra(BmoQuotePropertyModelExtra bmoQuotePropertyModelExtra) {
		if (bmoQuote.getStatus().toChar() == BmoQuote.STATUS_REVISION) {
			this.bmoQuotePropertyModelExtra = bmoQuotePropertyModelExtra;
			deletePropertyModelExtra();
		} else {
			String status= "";
			if(bmoQuote.getStatus().toChar()== BmoQuote.STATUS_AUTHORIZED)
				status= "Autorizada";
			else if(bmoQuote.getStatus().toChar()== BmoQuote.STATUS_CANCELLED)
				status= "Cancelada";
			showSystemMessage("La Cotización no se puede modificar -  está  " + status);
		}
	}

	public void deletePropertyModelExtra() {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-deletePropertyModelExtra(): ERROR " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				processPropertyModelExtraDelete(result);
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().delete(bmoQuotePropertyModelExtra.getPmClass(), bmoQuotePropertyModelExtra, callback);	
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-deletePropertyModelExtra(): ERROR " + e.toString());
		}
	}

	public void processPropertyModelExtraChangeSave(BmUpdateResult bmUpdateResult) {
		if (bmUpdateResult.hasErrors()) 
			showSystemMessage("Error al modificar Extra: " + bmUpdateResult.errorsToString());
		this.reset();
	}

	public void processPropertyModelExtraDelete(BmUpdateResult result) {
		if (result.hasErrors()) showSystemMessage("processPropertyModelExtraDelete() ERROR: " + result.errorsToString());
		this.reset();
	}	

	// Agrega un item de un producto a un grupo de la cotizacion
	private class UiQuotePropertyModelExtraForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
		private TextArea commentsTextArea = new TextArea();
		private TextBox quantityTextBox = new TextBox();
		private TextBox priceTextBox = new TextBox();
		private UiListBox propertyModelExtraListBox = new UiListBox(getUiParams(), new BmoPropertyModelExtra());

		private BmoQuotePropertyModelExtra bmoQuotePropertyModelExtra;
		private Button saveButton = new Button("AGREGAR");
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		private BmoQuote bmoQuote;
		private BmoPropertyModelExtra bmoPropertyModelExtra = new BmoPropertyModelExtra();
		String propertyModelExtraId = "";


		public UiQuotePropertyModelExtraForm(UiParams uiParams, Panel defaultPanel, BmoQuote bmoQuote, BmoPropertyModelExtra bmoPropertyModelExtra) {
			super(uiParams, defaultPanel);
			this.bmoQuotePropertyModelExtra = new BmoQuotePropertyModelExtra();
			this.bmoQuote = bmoQuote;
			this.bmoPropertyModelExtra = bmoPropertyModelExtra;

			try {
				bmoQuotePropertyModelExtra.getPropertyModelExtraId().setValue(bmoPropertyModelExtra.getId());
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "() ERROR: " + e.toString());
			}

			ChangeHandler listChangeHandler = new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					formListChange(event);
				}
			};
			formTable.setListChangeHandler(listChangeHandler);

			saveButton.setStyleName("formSaveButton");
			saveButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					prepareSave();
				}
			});
			saveButton.setVisible(false);
			if (getSFParams().hasWrite(bmoQuotePropertyModelExtra.getProgramCode())) saveButton.setVisible(true);
			buttonPanel.add(saveButton);

			defaultPanel.add(formTable);
		}

		public void show(){
			// Por defaul la cotizacion maneja 1 dia
			try {
				bmoQuotePropertyModelExtra.getQuantity().setValue(1);
				bmoPropertyModelExtra.getIdField().setNullable(true);
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-show() ERROR: al asignar valor 1 a los dias del item de la cotizacion: " + e.toString());
			}

			getPropertyModel();

			formTable.addField(2, 0, quantityTextBox, bmoQuotePropertyModelExtra.getQuantity());
			formTable.addField(3, 0, priceTextBox, bmoQuotePropertyModelExtra.getPrice());
			formTable.addField(4, 0, commentsTextArea, bmoQuotePropertyModelExtra.getComments());

			formTable.addButtonPanel(buttonPanel);

			statusEffect();
		}

		public void formListChange(ChangeEvent event) {
			if (event.getSource() == propertyModelExtraListBox) {
				BmoPropertyModelExtra bmoPropertyModelExtra = (BmoPropertyModelExtra)propertyModelExtraListBox.getSelectedBmObject();
				propertyModelExtraId = propertyModelExtraListBox.getSelectedId();
				priceTextBox.setText(bmoPropertyModelExtra.getPrice().toString());
			} 

			statusEffect();
		}

		private void statusEffect() {
			if (!propertyModelExtraListBox.getSelectedId().equals("") && !propertyModelExtraListBox.getSelectedId().equals("0")) {
				bmoPropertyModelExtra = (BmoPropertyModelExtra)propertyModelExtraListBox.getSelectedBmObject();
				quantityTextBox.setEnabled(false);
				quantityTextBox.setText("1");

				// Determina si se puede modificar el precio
				if (bmoPropertyModelExtra.getFixedPrice().toBoolean()) {
					priceTextBox.setEnabled(false);					
				} else {
					priceTextBox.setEnabled(true);	
				}

			} else {
				priceTextBox.setText("");
				priceTextBox.setEnabled(true);
				quantityTextBox.setText("");
				commentsTextArea.setText("");
			}
		}

		private void setPropertyModelExtraFilter(BmUpdateResult bmUpdateResult) {
			if (!bmUpdateResult.hasErrors()) {
				BmoPropertyModel bmoPropertyModel = (BmoPropertyModel)bmUpdateResult.getBmObject();
				BmFilter filterByModel = new BmFilter();
				BmoPropertyModelExtra bmoPropertyModelExtra = new BmoPropertyModelExtra();
				filterByModel.setValueFilter(bmoPropertyModelExtra.getKind(), bmoPropertyModelExtra.getPropertyModelId(), bmoPropertyModel.getId());
				propertyModelExtraListBox.addFilter(filterByModel);

				// Filtra por vigencia fin				
				BmFilter filterByPromoEnd = new BmFilter();
				filterByPromoEnd.setValueOperatorFilter(bmoPropertyModelExtra.getKind(), bmoPropertyModelExtra.getEndDate(), ""+BmFilter.MAJOREQUAL, bmoQuote.getStartDate().toString().substring(0,10));
				propertyModelExtraListBox.addFilter(filterByPromoEnd);

				// Filtra por vigencia inicio
				BmFilter filterByPromoStart = new BmFilter();								
				filterByPromoStart.setValueOperatorFilter(bmoPropertyModelExtra.getKind(), bmoPropertyModelExtra.getStartDate(), BmFilter.MINOREQUAL, bmoQuote.getEndDate().toString().substring(0,10));
				propertyModelExtraListBox.addFilter(filterByPromoStart);

				formTable.addField(1, 0, propertyModelExtraListBox, bmoPropertyModelExtra.getIdField());
				saveButton.setVisible(true);
			} else {
				showSystemMessage(bmUpdateResult.errorsToString());
				quotePropertyModelExtraDialogBox.setVisible(false);
			}
		}

		//Obtener la cantidad en almacen
		public void getPropertyModel(){
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getLockedQuantity() ERROR: " + caught.toString());
				}

				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					setPropertyModelExtraFilter(result);
				}
			};

			try {	
				startLoading();
				getUiParams().getBmObjectServiceAsync().action(bmoQuotePropertyModelExtra.getPmClass(), bmoQuotePropertyModelExtra, BmoQuotePropertyModelExtra.ACTION_GETPROPERTYMODEL, "" + bmoQuote.getId(), callback);
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getLockedQuantity() ERROR: " + e.toString());
			}
		} 

		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) 
				showSystemMessage("Error al modificar Extra: " + bmUpdateResult.errorsToString());
			else {
				quotePropertyModelExtraDialogBox.hide();
				reset();
			}
		}

		public void prepareSave() {
			try {
				bmoQuotePropertyModelExtra = new BmoQuotePropertyModelExtra();
				bmoQuotePropertyModelExtra.getPropertyModelExtraId().setValue(propertyModelExtraId);
				bmoQuotePropertyModelExtra.getQuantity().setValue(quantityTextBox.getText());
				bmoQuotePropertyModelExtra.getPrice().setValue(priceTextBox.getText());
				bmoQuotePropertyModelExtra.getComments().setValue(commentsTextArea.getText());
				bmoQuotePropertyModelExtra.getQuoteId().setValue(bmoQuote.getId());
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
					getUiParams().getBmObjectServiceAsync().save(bmoQuotePropertyModelExtra.getPmClass(), bmoQuotePropertyModelExtra, callback);					
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-save() ERROR: " + e.toString());
			}
		}
	}
}
