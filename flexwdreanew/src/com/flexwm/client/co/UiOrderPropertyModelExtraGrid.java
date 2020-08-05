package com.flexwm.client.co;


import com.flexwm.client.op.UiOrderForm.OrderUpdater;
import com.flexwm.shared.co.BmoOrderPropertyModelExtra;
import com.flexwm.shared.co.BmoPropertyModel;
import com.flexwm.shared.co.BmoPropertyModelExtra;
import com.flexwm.shared.op.BmoOrder;
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
import com.symgae.client.ui.UiFileUploadBox;
import com.symgae.client.ui.UiFormFlexTable;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiListDataProvider;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.google.gwt.user.client.ui.CheckBox;


public class UiOrderPropertyModelExtraGrid extends Ui {
	private UiFormFlexTable formFlexTable = new UiFormFlexTable(getUiParams());

	// OrderPropertyModelExtra
	private BmoOrderPropertyModelExtra bmoOrderPropertyModelExtra = new BmoOrderPropertyModelExtra();
	private FlowPanel orderPropertyModelExtraPanel = new FlowPanel();
	private CellTable<BmObject> orderPropertyModelExtraGrid = new CellTable<BmObject>();
	private UiListDataProvider<BmObject> orderPropertyModelExtraData;
	BmFilter orderPropertyModelExtraFilter;
	private TextArea orderPropertyModelExtraDescriptionTextArea = new TextArea();
	DialogBox orderPropertyModelExtraDescriptionDialogBox;
	Button changePropertyModelExtraDescriptionButton = new Button("Guardar");
	Button closePropertyModelExtraDescriptionButton = new Button("Cerrar");

	private Button addOrderPropertyModelExtraButton = new Button("+ Extra");
	protected DialogBox orderPropertyModelExtraDialogBox;

	protected DialogBox orderPropertyModelExtraDocumentDialogBox;

	// Cambio de PropertyModelExtra
	private HorizontalPanel buttonPanel = new HorizontalPanel();
	Button changePropertyModelExtraButton = new Button("Cambiar");
	Button closePropertyModelExtraButton = new Button("Cerrar");

	// Otros
	private CheckBox showQuantityCheckBox = new CheckBox();
	private CheckBox showPriceCheckBox = new CheckBox();
	//	private Label amountLabel = new Label();
	private NumberFormat numberFormat = NumberFormat.getDecimalFormat();
	private BmoOrder bmoOrder;
	private OrderUpdater orderUpdater;


	public UiOrderPropertyModelExtraGrid(UiParams uiParams, Panel defaultPanel, BmoOrder bmoOrder, OrderUpdater orderUpdater){
		super(uiParams, defaultPanel);
		this.bmoOrder = bmoOrder;
		this.orderUpdater = orderUpdater;

		// Boton de cerrar dialogo de personal
		changePropertyModelExtraDescriptionButton.setStyleName("formCloseButton");
		changePropertyModelExtraDescriptionButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				changeOrderPropertyModelExtraDescription();
				orderPropertyModelExtraDescriptionDialogBox.hide();
			}
		});

		closePropertyModelExtraDescriptionButton.setStyleName("formCloseButton");
		closePropertyModelExtraDescriptionButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				orderPropertyModelExtraDescriptionDialogBox.hide();
			}
		});

		// Inicializar grid de Extra
		orderPropertyModelExtraGrid = new CellTable<BmObject>();
		orderPropertyModelExtraGrid.setWidth("100%");
		orderPropertyModelExtraPanel.clear();
		orderPropertyModelExtraPanel.setWidth("100%");
		defaultPanel.setStyleName("detailStart");
		setOrderPropertyModelExtraColumns();
		orderPropertyModelExtraFilter = new BmFilter();
		orderPropertyModelExtraFilter.setValueFilter(bmoOrderPropertyModelExtra.getKind(), bmoOrderPropertyModelExtra.getOrderId().getName(), bmoOrder.getId());
		orderPropertyModelExtraGrid.addLoadingStateChangeHandler(new LoadingStateChangeEvent.Handler() {           
			@Override
			public void onLoadingStateChanged(LoadingStateChangeEvent event) {
				if (event.getLoadingState() == LoadingState.LOADED) {
					changeHeight();
				}
			}
		}); 
		orderPropertyModelExtraData = new UiListDataProvider<BmObject>(new BmoOrderPropertyModelExtra(), orderPropertyModelExtraFilter);
		orderPropertyModelExtraData.addDataDisplay(orderPropertyModelExtraGrid);
		orderPropertyModelExtraPanel.add(orderPropertyModelExtraGrid);

		// Panel de botones
		addOrderPropertyModelExtraButton.setStyleName("formSaveButton");
		addOrderPropertyModelExtraButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addPropertyModelExtra();
			}
		});
		buttonPanel.setHeight("100%");
		buttonPanel.setStyleName("formButtonPanel");
		buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		buttonPanel.add(addOrderPropertyModelExtraButton);

		// Crear forma y campos
		formFlexTable.setWidth("100%");
		//formFlexTable.addSectionLabel(1, 0, "Extras", 2);
		//		formTable.addField(2, 0, showQuantityCheckBox, bmoOrder.get());
		//		formTable.addField(2, 0, showPriceCheckBox, bmoOrderPropertyModelExtra.getShowPrice());
		//		formFlexTable.addLabelField(2, 2, bmoOrderPropertyModelExtra.getAmount(), amountLabel);
		formFlexTable.addPanel(3, 0, orderPropertyModelExtraPanel);
		formFlexTable.addButtonPanel(buttonPanel);
		defaultPanel.add(formFlexTable);
	}

	public void show(){
		orderPropertyModelExtraData.list();
		orderPropertyModelExtraGrid.redraw();

		statusEffect();
	}

	public void statusEffect(){
		if (bmoOrder.getStatus().toChar() != BmoOrder.STATUS_REVISION) {
			showQuantityCheckBox.setEnabled(false);
			showPriceCheckBox.setEnabled(false);
			buttonPanel.setVisible(false);
		} else {
			showQuantityCheckBox.setEnabled(true);
			showPriceCheckBox.setEnabled(true);			
		}
	}

	public void reset(){
		orderUpdater.changeOrderPropertyModelExtra();
	}

	public void changeHeight() {
		orderPropertyModelExtraGrid.setPageSize(orderPropertyModelExtraData.getList().size());
		orderPropertyModelExtraGrid.setVisibleRange(0, orderPropertyModelExtraData.getList().size());
	}

	public void addPropertyModelExtra(){
		addPropertyModelExtra(new BmoPropertyModelExtra());
	}

	public void addPropertyModelExtra(BmoPropertyModelExtra bmoPropertyModelExtra) {
		orderPropertyModelExtraDialogBox = new DialogBox(true);
		orderPropertyModelExtraDialogBox.setGlassEnabled(true);
		orderPropertyModelExtraDialogBox.setText("Extra de Pedido");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "200px");

		orderPropertyModelExtraDialogBox.setWidget(vp);

		UiOrderPropertyModelExtraForm orderPropertyModelExtraForm = new UiOrderPropertyModelExtraForm(getUiParams(), vp, bmoOrder, bmoPropertyModelExtra);

		orderPropertyModelExtraForm.show();

		orderPropertyModelExtraDialogBox.center();
		orderPropertyModelExtraDialogBox.show();
	}

	public void showPropertyModelExtraDocument(){
		showPropertyModelExtraDocument(bmoOrderPropertyModelExtra);
	}

	public void showPropertyModelExtraDocument(BmoOrderPropertyModelExtra bmoOrderPropertyModelExtra) {
		orderPropertyModelExtraDocumentDialogBox = new DialogBox(true);
		orderPropertyModelExtraDocumentDialogBox.setGlassEnabled(true);
		orderPropertyModelExtraDocumentDialogBox.setText("Documento del Extra");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("210px", "50px");

		orderPropertyModelExtraDocumentDialogBox.setWidget(vp);

		UiOrderPropertyModelExtraDocumentForm uiOrderPropertyModelExtraDocumentForm = new UiOrderPropertyModelExtraDocumentForm(getUiParams(), vp, bmoOrder, bmoOrderPropertyModelExtra);

		uiOrderPropertyModelExtraDocumentForm.show();

		orderPropertyModelExtraDocumentDialogBox.center();
		orderPropertyModelExtraDocumentDialogBox.show();
	}

	// Columnas grid de personal
	public void setOrderPropertyModelExtraColumns() {

		if (!isMobile()) {
			// Cantidad
			Column<BmObject, String> quantityColumn = new Column<BmObject, String>(new EditTextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoOrderPropertyModelExtra)bmObject).getQuantity().toString();
				}
			};
			quantityColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			orderPropertyModelExtraGrid.addColumn(quantityColumn, SafeHtmlUtils.fromSafeConstant("Cant."));
			quantityColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					// Called when the user changes the value.
					changeOrderPropertyModelExtraQuantity(bmObject, value);
				}
			});
		}
		// Nombre
		Column<BmObject, String> nameColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoOrderPropertyModelExtra)bmObject).getBmoPropertyModelExtra().getName().toString();
			}
		};
		orderPropertyModelExtraGrid.addColumn(nameColumn, SafeHtmlUtils.fromSafeConstant("Extra"));

		if (!isMobile()) {
			// Columna comentarios
			Column<BmObject, String> commentsColumn = new Column<BmObject, String>(new ButtonCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					if (((BmoOrderPropertyModelExtra)bmObject).getComments().toString().length() > 0)
						return "...";
					else return ".";
				}
			};
			commentsColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			orderPropertyModelExtraGrid.addColumn(commentsColumn, SafeHtmlUtils.fromSafeConstant("Coment."));
			commentsColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					orderPropertyModelExtraDescriptionDialog((BmoOrderPropertyModelExtra)bmObject);
				}
			});

			// Precio
			Column<BmObject, String> priceColumn = new Column<BmObject, String>(new EditTextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoOrderPropertyModelExtra)bmObject).getPrice().toString();
				}
			};
			priceColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
			orderPropertyModelExtraGrid.addColumn(priceColumn, SafeHtmlUtils.fromSafeConstant("Precio"));
			priceColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					changeOrderPropertyModelExtraPrice(bmObject, value);
				}
			});
		}

		// Total
		Column<BmObject, String> totalColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				numberFormat = NumberFormat.getCurrencyFormat();
				String formatted = numberFormat.format(((BmoOrderPropertyModelExtra)bmObject).getAmount().toDouble());
				return (formatted);
			}
		};
		totalColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		orderPropertyModelExtraGrid.addColumn(totalColumn, SafeHtmlUtils.fromSafeConstant("Total"));

		// Adjunto Extras de Modelo 
		Column<BmObject, String> fileColumn = new Column<BmObject, String>(new ButtonCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				if (((BmoOrderPropertyModelExtra)bmObject).getBmoPropertyModelExtra().getFile().toString().length() > 0)
					return "Ver";
				else
					return " ";
			}
		};

		fileColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		orderPropertyModelExtraGrid.addColumn(fileColumn, SafeHtmlUtils.fromSafeConstant("Adjunto"));
		fileColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
			@Override
			public void update(int index, BmObject bmObject, String value) {
				if (((BmoOrderPropertyModelExtra)bmObject).getBmoPropertyModelExtra().getFile().toString().length() > 0) {
					bmoOrderPropertyModelExtra = (BmoOrderPropertyModelExtra)bmObject;
					showPropertyModelExtraDocument(bmoOrderPropertyModelExtra);
				}	
			}	
		});

		// Eliminar
		Column<BmObject, String> deleteColumn = new Column<BmObject, String>(new ButtonCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return "-";
			}
		};
		deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		orderPropertyModelExtraGrid.addColumn(deleteColumn, SafeHtmlUtils.fromSafeConstant("Eliminar"));
		deleteColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
			@Override
			public void update(int index, BmObject bmObject, String value) {
				deletePropertyModelExtra((BmoOrderPropertyModelExtra)bmObject);
			}
		});
	}

	public void orderPropertyModelExtraDescriptionDialog(BmoOrderPropertyModelExtra bmoOrderPropertyModelExtra) {
		this.bmoOrderPropertyModelExtra = bmoOrderPropertyModelExtra;
		orderPropertyModelExtraDescriptionDialogBox = new DialogBox(true);
		orderPropertyModelExtraDescriptionDialogBox.setGlassEnabled(true);
		orderPropertyModelExtraDescriptionDialogBox.setText("Descripción Extra");
		orderPropertyModelExtraDescriptionDialogBox.setSize("400px", "200px");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "200px");
		orderPropertyModelExtraDescriptionDialogBox.setWidget(vp);

		orderPropertyModelExtraDescriptionTextArea = new TextArea();
		orderPropertyModelExtraDescriptionTextArea.setSize("320px", "190px");
		orderPropertyModelExtraDescriptionTextArea.setText(bmoOrderPropertyModelExtra.getComments().toString());
		vp.add(orderPropertyModelExtraDescriptionTextArea);

		HorizontalPanel descriptionButtonPanel = new HorizontalPanel();
		descriptionButtonPanel.add(changePropertyModelExtraDescriptionButton);
		descriptionButtonPanel.add(closePropertyModelExtraDescriptionButton);
		vp.add(descriptionButtonPanel);

		orderPropertyModelExtraDescriptionDialogBox.center();
		orderPropertyModelExtraDescriptionDialogBox.show();
	}

	public void changeOrderPropertyModelExtraDescription() {
		String description = orderPropertyModelExtraDescriptionTextArea.getText();
		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_REVISION) {
			try {
				if (description.length() >= 0) {
					bmoOrderPropertyModelExtra.getComments().setValue(description);
					savePropertyModelExtraChange();
				}
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-changeOrderPropertyModelExtraDescription() ERROR: " + e.toString());
			}
		} else {
			String status = "";
			if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED)
				status = "Autorizado.";
			else if(bmoOrder.getStatus().toChar() == BmoOrder.STATUS_CANCELLED)
				status = "Cancelado.";
			else if(bmoOrder.getStatus().toChar() == BmoOrder.STATUS_FINISHED)
				status = "Finalizado.";

			showSystemMessage("El Pedido no se puede modificar - está " + status);
		}
	}

	public void changeOrderPropertyModelExtraQuantity(BmObject bmObject, String quantity) {
		bmoOrderPropertyModelExtra = (BmoOrderPropertyModelExtra)bmObject;
		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_REVISION) {
			try {
				int q = Integer.parseInt(quantity);
				if (q > 0) {
					bmoOrderPropertyModelExtra.getQuantity().setValue(quantity);
					savePropertyModelExtraChange();
				} else {
					// Eliminar registro
					deletePropertyModelExtra();
				}
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-changeOrderPropertyModelExtraQuantity(): ERROR " + e.toString());
			}
		} else {
			String status = "";
			if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED)
				status = "Autorizado.";
			else if(bmoOrder.getStatus().toChar() == BmoOrder.STATUS_CANCELLED)
				status = "Cancelado.";
			else if(bmoOrder.getStatus().toChar() == BmoOrder.STATUS_FINISHED)
				status = "Finalizado.";

			showSystemMessage("El Pedido no se puede modificar - está " + status);
		}
	}

	public void changeOrderPropertyModelExtraPrice(BmObject bmObject, String price) {
		bmoOrderPropertyModelExtra = (BmoOrderPropertyModelExtra)bmObject;
		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_REVISION) {
			try {
				Double p = Double.parseDouble(price);
				if (bmoOrderPropertyModelExtra.getPropertyModelExtraId().toInteger() > 0) {
					if (p == 0) {
						bmoOrderPropertyModelExtra.getPrice().setValue(0);
						savePropertyModelExtraChange();
					} else {
						if (bmoOrderPropertyModelExtra.getBmoPropertyModelExtra().getFixedPrice().toBoolean()) {
							showSystemMessage("El Costo del Extra es Fijo. Se reestablecerá Precio del Catálogo.");	
						}
						bmoOrderPropertyModelExtra.getPrice().setValue(p);
						savePropertyModelExtraChange();
					}
				} else {
					bmoOrderPropertyModelExtra.getPrice().setValue(p);
					savePropertyModelExtraChange();
				}
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-changeOrderPropertyModelExtraPrice(): ERROR " + e.toString());
			}
		} else {
			String status = "";
			if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED)
				status = "Autorizado.";
			else if(bmoOrder.getStatus().toChar() == BmoOrder.STATUS_CANCELLED)
				status = "Cancelado.";
			else if(bmoOrder.getStatus().toChar() == BmoOrder.STATUS_FINISHED)
				status = "Finalizado.";

			showSystemMessage("El Pedido no se puede modificar - está " + status);
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
				getUiParams().getBmObjectServiceAsync().save(bmoOrderPropertyModelExtra.getPmClass(), bmoOrderPropertyModelExtra, callback);	
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-savePropertyModelExtraChange(): ERROR " + e.toString());
		}
	}

	public void deletePropertyModelExtra(BmoOrderPropertyModelExtra bmoOrderPropertyModelExtra) {
		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_REVISION) {
			this.bmoOrderPropertyModelExtra = bmoOrderPropertyModelExtra;
			deletePropertyModelExtra();
		} else {
			showSystemMessage("El Pedido no se puede modificar - está Autorizado");
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
				getUiParams().getBmObjectServiceAsync().delete(bmoOrderPropertyModelExtra.getPmClass(), bmoOrderPropertyModelExtra, callback);	
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
	private class UiOrderPropertyModelExtraForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
		private TextArea commentsTextArea = new TextArea();
		private TextBox quantityTextBox = new TextBox();
		private TextBox priceTextBox = new TextBox();
		private UiListBox propertyModelExtraListBox = new UiListBox(getUiParams(), new BmoPropertyModelExtra());

		private BmoOrderPropertyModelExtra bmoOrderPropertyModelExtra;
		private Button saveButton = new Button("Guardar");
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		private BmoOrder bmoOrder;
		private BmoPropertyModelExtra bmoPropertyModelExtra = new BmoPropertyModelExtra();
		String propertyModelExtraId = "";


		public UiOrderPropertyModelExtraForm(UiParams uiParams, Panel defaultPanel, BmoOrder bmoOrder, BmoPropertyModelExtra bmoPropertyModelExtra) {
			super(uiParams, defaultPanel);
			this.bmoOrderPropertyModelExtra = new BmoOrderPropertyModelExtra();
			this.bmoOrder = bmoOrder;
			this.bmoPropertyModelExtra = bmoPropertyModelExtra;

			try {
				bmoOrderPropertyModelExtra.getPropertyModelExtraId().setValue(bmoPropertyModelExtra.getId());
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
			if (getSFParams().hasWrite(bmoOrderPropertyModelExtra.getProgramCode())) saveButton.setVisible(true);
			buttonPanel.add(saveButton);

			defaultPanel.add(formTable);
		}

		public void show(){
			// Por defaul la cotizacion maneja 1 dia
			try {
				bmoOrderPropertyModelExtra.getQuantity().setValue(1);
				bmoPropertyModelExtra.getIdField().setNullable(true);
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-show() ERROR: al asignar valor 1 a los dias del item de la cotizacion: " + e.toString());
			}

			getPropertyModel();

			formTable.addField(2, 0, quantityTextBox, bmoOrderPropertyModelExtra.getQuantity());
			formTable.addField(3, 0, priceTextBox, bmoOrderPropertyModelExtra.getPrice());
			formTable.addField(4, 0, commentsTextArea, bmoOrderPropertyModelExtra.getComments());
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
				filterByPromoEnd.setValueOperatorFilter(bmoPropertyModelExtra.getKind(), bmoPropertyModelExtra.getEndDate(), BmFilter.MAJOREQUAL, bmoOrder.getLockStart().toString().substring(0,10));
				propertyModelExtraListBox.addFilter(filterByPromoEnd);

				// Filtra por vigencia inicio
				BmFilter filterByPromoStart = new BmFilter();								
				filterByPromoStart.setValueOperatorFilter(bmoPropertyModelExtra.getKind(), bmoPropertyModelExtra.getStartDate(), BmFilter.MINOREQUAL, bmoOrder.getLockStart().toString().substring(0,10));
				propertyModelExtraListBox.addFilter(filterByPromoStart);

				formTable.addField(1, 0, propertyModelExtraListBox, bmoPropertyModelExtra.getIdField());
				saveButton.setVisible(true);
			} else {
				showSystemMessage(bmUpdateResult.errorsToString());
				orderPropertyModelExtraDialogBox.setVisible(false);
			}
		}

		//Obtener la cantidad en almacen
		public void getPropertyModel(){
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getPropertyModel() ERROR: " + caught.toString());
				}

				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					setPropertyModelExtraFilter(result);
				}
			};

			try {	
				startLoading();
				getUiParams().getBmObjectServiceAsync().action(bmoOrderPropertyModelExtra.getPmClass(), bmoOrderPropertyModelExtra, BmoOrderPropertyModelExtra.ACTION_GETPROPERTYMODEL, "" + bmoOrder.getId(), callback);
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getPropertyModel() ERROR: " + e.toString());
			}
		} 

		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) 
				showSystemMessage("Error al modificar Extra: " + bmUpdateResult.errorsToString());
			else {
				orderPropertyModelExtraDialogBox.hide();
				reset();
			}
		}

		public void prepareSave() {
			try {
				bmoOrderPropertyModelExtra = new BmoOrderPropertyModelExtra();
				bmoOrderPropertyModelExtra.getPropertyModelExtraId().setValue(propertyModelExtraId);
				bmoOrderPropertyModelExtra.getPrice().setValue(priceTextBox.getText());
				bmoOrderPropertyModelExtra.getQuantity().setValue(quantityTextBox.getText());
				bmoOrderPropertyModelExtra.getComments().setValue(commentsTextArea.getText());
				bmoOrderPropertyModelExtra.getOrderId().setValue(bmoOrder.getId());
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
					getUiParams().getBmObjectServiceAsync().save(bmoOrderPropertyModelExtra.getPmClass(), bmoOrderPropertyModelExtra, callback);					
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-save() ERROR: " + e.toString());
			}
		}
	}

	private class UiOrderPropertyModelExtraDocumentForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());		
		private TextBox nameExtraTextBox = new TextBox();
		UiFileUploadBox fileUpload = new UiFileUploadBox(getUiParams());

		//		private BmoOrder bmoOrder;
		private BmoOrderPropertyModelExtra bmoOrderPropertyModelExtra = new BmoOrderPropertyModelExtra();

		public UiOrderPropertyModelExtraDocumentForm(UiParams uiParams, Panel defaultPanel, BmoOrder bmoOrder, BmoOrderPropertyModelExtra bmoOrderPropertyModelExtra) {			
			super(uiParams, defaultPanel);			
			//			this.bmoOrder = bmoOrder;
			this.bmoOrderPropertyModelExtra = bmoOrderPropertyModelExtra;

			defaultPanel.add(formTable);
		}

		public void show() {
			formTable.addField(1, 0, nameExtraTextBox, bmoOrderPropertyModelExtra.getBmoPropertyModelExtra().getName());
			formTable.addField(2, 0, fileUpload, bmoOrderPropertyModelExtra.getBmoPropertyModelExtra().getFile());

			statusEffect();
		}

		private void statusEffect() {
			nameExtraTextBox.setEnabled(false);
		}
	}
}
