package com.flexwm.client.ac;

import com.flexwm.client.ac.UiOrderFormSession.OrderSessionUpdater;
import com.flexwm.shared.ac.BmoOrderSessionExtra;
import com.flexwm.shared.ac.BmoSessionTypeExtra;
import com.flexwm.shared.op.BmoOrder;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
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
import com.symgae.shared.BProgramService;
import com.symgae.shared.BProgramServiceAsync;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.google.gwt.user.client.ui.CheckBox;


public class UiOrderSessionExtraGrid extends Ui {
	private UiFormFlexTable formFlexTable = new UiFormFlexTable(getUiParams());

	// OrderSessionExtra
	private BmoOrderSessionExtra bmoOrderSessionExtra = new BmoOrderSessionExtra();
	private FlowPanel OrderSessionExtraPanel = new FlowPanel();
	private CellTable<BmObject> orderSessionExtraGrid = new CellTable<BmObject>();
	private UiListDataProvider<BmObject> OrderSessionExtraData;
	BmFilter OrderSessionExtraFilter;
	private TextArea OrderSessionExtraDescriptionTextArea = new TextArea();
	DialogBox OrderSessionExtraDescriptionDialogBox;
	Button changesessionTypeExtraDescriptionButton = new Button("Guardar");
	Button closesessionTypeExtraDescriptionButton = new Button("Cerrar");

	private Button addOrderSessionExtraButton = new Button("+ EXTRA");
	protected DialogBox orderSessionExtraDialogBox;
	
	protected DialogBox orderSessionExtraDocumentDialogBox;

	// Cambio de sessionTypeExtra
	private HorizontalPanel buttonPanel = new HorizontalPanel();
	Button changesessionTypeExtraButton = new Button("CAMBIAR");
	Button closesessionTypeExtraButton = new Button("CERRAR");

	// Otros
	private CheckBox showQuantityCheckBox = new CheckBox();
	private CheckBox showPriceCheckBox = new CheckBox();
	//	private Label amountLabel = new Label();
	private NumberFormat numberFormat = NumberFormat.getDecimalFormat();
	private BmoOrder bmoOrder;
	private OrderSessionUpdater orderSessionUpdater;


	public UiOrderSessionExtraGrid(UiParams uiParams, Panel defaultPanel, BmoOrder bmoOrder, OrderSessionUpdater orderSessionUpdater){
		super(uiParams, defaultPanel);
		this.bmoOrder = bmoOrder;
		this.orderSessionUpdater = orderSessionUpdater;

		// Boton de cerrar dialogo de personal
		changesessionTypeExtraDescriptionButton.setStyleName("formCloseButton");
		changesessionTypeExtraDescriptionButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				changeOrderSessionExtraDescription();
				OrderSessionExtraDescriptionDialogBox.hide();
			}
		});

		closesessionTypeExtraDescriptionButton.setStyleName("formCloseButton");
		closesessionTypeExtraDescriptionButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				OrderSessionExtraDescriptionDialogBox.hide();
			}
		});

		// Inicializar grid de Extra
		orderSessionExtraGrid = new CellTable<BmObject>();
		orderSessionExtraGrid.setWidth("100%");
		OrderSessionExtraPanel.clear();
		OrderSessionExtraPanel.setWidth("100%");
		//defaultPanel.setStyleName("detailStart");
		setOrderSessionExtraColumns();
		
		OrderSessionExtraFilter = new BmFilter();
		OrderSessionExtraFilter.setValueFilter(bmoOrderSessionExtra.getKind(), bmoOrderSessionExtra.getOrderId().getName(), bmoOrder.getId());
		orderSessionExtraGrid.addLoadingStateChangeHandler(new LoadingStateChangeEvent.Handler() {           
			@Override
			public void onLoadingStateChanged(LoadingStateChangeEvent event) {
				if (event.getLoadingState() == LoadingState.LOADED) {
					changeHeight();
				}
			}
		}); 
		OrderSessionExtraData = new UiListDataProvider<BmObject>(new BmoOrderSessionExtra(), OrderSessionExtraFilter);
		OrderSessionExtraData.addDataDisplay(orderSessionExtraGrid);
		OrderSessionExtraPanel.add(orderSessionExtraGrid);

		// Panel de botones
		addOrderSessionExtraButton.setStyleName("formSaveButton");
		addOrderSessionExtraButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addSessionTypeExtra();
			}
		});
		buttonPanel.setHeight("100%");
		buttonPanel.setStyleName("formButtonPanel");
		buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		buttonPanel.add(addOrderSessionExtraButton);

		// Crear forma y campos
		formFlexTable.setWidth("100%");
		formFlexTable.addSectionLabel(1, 0, "Extras", 4);		
		formFlexTable.addPanel(3, 0, OrderSessionExtraPanel, 4);
		formFlexTable.addButtonPanel(buttonPanel);
		defaultPanel.add(formFlexTable);
	}

	public void show(){
		OrderSessionExtraData.list();
		orderSessionExtraGrid.redraw();

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
		orderSessionUpdater.changeOrderSessionExtra();
	}

	public void changeHeight() {
		orderSessionExtraGrid.setPageSize(OrderSessionExtraData.getList().size());
		orderSessionExtraGrid.setVisibleRange(0, OrderSessionExtraData.getList().size());
	}

	public void addSessionTypeExtra(){
		addSessionTypeExtra(new BmoSessionTypeExtra());
	}

	public void addSessionTypeExtra(BmoSessionTypeExtra bmosessionTypeExtra) {
		orderSessionExtraDialogBox = new DialogBox(true);
		orderSessionExtraDialogBox.setGlassEnabled(true);
		orderSessionExtraDialogBox.setText("Extra de Pedido");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "200px");

		orderSessionExtraDialogBox.setWidget(vp);

		UiOrderSessionExtraForm OrderSessionExtraForm = new UiOrderSessionExtraForm(getUiParams(), vp, bmoOrder, bmosessionTypeExtra);

		OrderSessionExtraForm.show();

		orderSessionExtraDialogBox.center();
		orderSessionExtraDialogBox.show();
	}
	
	public void showsessionTypeExtraDocument(){
		showsessionTypeExtraDocument(bmoOrderSessionExtra);
	}
	
	public void showsessionTypeExtraDocument(BmoOrderSessionExtra bmoOrderSessionExtra) {
		orderSessionExtraDocumentDialogBox = new DialogBox(true);
		orderSessionExtraDocumentDialogBox.setGlassEnabled(true);
		orderSessionExtraDocumentDialogBox.setText("Documento del Extra");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("210px", "50px");

		orderSessionExtraDocumentDialogBox.setWidget(vp);

		UiOrderSessionExtraDocumentForm uiOrderSessionExtraDocumentForm = new UiOrderSessionExtraDocumentForm(getUiParams(), vp, bmoOrder, bmoOrderSessionExtra);

		uiOrderSessionExtraDocumentForm.show();

		orderSessionExtraDocumentDialogBox.center();
		orderSessionExtraDocumentDialogBox.show();
	}

	// Columnas grid de personal
	public void setOrderSessionExtraColumns() {
		// Cantidad
		Column<BmObject, String> quantityColumn = new Column<BmObject, String>(new EditTextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoOrderSessionExtra)bmObject).getQuantity().toString();
			}
		};
		quantityColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		orderSessionExtraGrid.addColumn(quantityColumn, SafeHtmlUtils.fromSafeConstant("Cant."));
		quantityColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
			@Override
			public void update(int index, BmObject bmObject, String value) {
				// Called when the user changes the value.
				changeOrderSessionExtraQuantity(bmObject, value);
			}
		});

		// Nombre
		Column<BmObject, String> nameColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoOrderSessionExtra)bmObject).getBmoSessionTypeExtra().getName().toString();
			}
		};
		orderSessionExtraGrid.addColumn(nameColumn, SafeHtmlUtils.fromSafeConstant("Extra"));

		// Columna comentarios
		Column<BmObject, String> commentsColumn = new Column<BmObject, String>(new ButtonCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				if (((BmoOrderSessionExtra)bmObject).getComments().toString().length() > 0)
					return "...";
				else return ".";
			}
		};
		commentsColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		orderSessionExtraGrid.addColumn(commentsColumn, SafeHtmlUtils.fromSafeConstant("Comentarios."));
		commentsColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
			@Override
			public void update(int index, BmObject bmObject, String value) {
				OrderSessionExtraDescriptionDialog((BmoOrderSessionExtra)bmObject);
			}
		});

		// Precio
		Column<BmObject, String> priceColumn = new Column<BmObject, String>(new EditTextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoOrderSessionExtra)bmObject).getPrice().toString();
			}
		};
		priceColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		orderSessionExtraGrid.addColumn(priceColumn, SafeHtmlUtils.fromSafeConstant("Precio"));
		priceColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
			@Override
			public void update(int index, BmObject bmObject, String value) {
				changeOrderSessionExtraPrice(bmObject, value);
			}
		});

		// Total
		Column<BmObject, String> totalColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				numberFormat = NumberFormat.getCurrencyFormat();
				String formatted = numberFormat.format(((BmoOrderSessionExtra)bmObject).getAmount().toDouble());
				return (formatted);
			}
		};
		totalColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		orderSessionExtraGrid.addColumn(totalColumn, SafeHtmlUtils.fromSafeConstant("Total"));
		
		

		// Eliminar
		Column<BmObject, String> deleteColumn = new Column<BmObject, String>(new ButtonCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return "-";
			}
		};
		deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		orderSessionExtraGrid.addColumn(deleteColumn, SafeHtmlUtils.fromSafeConstant("Eliminar"));
		deleteColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
			@Override
			public void update(int index, BmObject bmObject, String value) {
				deletesessionTypeExtra((BmoOrderSessionExtra)bmObject);
			}
		});
	}
		
	
	public void OrderSessionExtraDescriptionDialog(BmoOrderSessionExtra bmoOrderSessionExtra) {
		this.bmoOrderSessionExtra = bmoOrderSessionExtra;
		OrderSessionExtraDescriptionDialogBox = new DialogBox(true);
		OrderSessionExtraDescriptionDialogBox.setGlassEnabled(true);
		OrderSessionExtraDescriptionDialogBox.setText("Descripción Extra");
		OrderSessionExtraDescriptionDialogBox.setSize("400px", "200px");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "200px");
		OrderSessionExtraDescriptionDialogBox.setWidget(vp);

		OrderSessionExtraDescriptionTextArea = new TextArea();
		OrderSessionExtraDescriptionTextArea.setSize("320px", "190px");
		OrderSessionExtraDescriptionTextArea.setText(bmoOrderSessionExtra.getComments().toString());
		vp.add(OrderSessionExtraDescriptionTextArea);

		HorizontalPanel descriptionButtonPanel = new HorizontalPanel();
		descriptionButtonPanel.add(changesessionTypeExtraDescriptionButton);
		descriptionButtonPanel.add(closesessionTypeExtraDescriptionButton);
		vp.add(descriptionButtonPanel);

		OrderSessionExtraDescriptionDialogBox.center();
		OrderSessionExtraDescriptionDialogBox.show();
	}

	public void changeOrderSessionExtraDescription() {
		String description = OrderSessionExtraDescriptionTextArea.getText();
		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_REVISION) {
			try {
				if (description.length() >= 0) {
					bmoOrderSessionExtra.getComments().setValue(description);
					savesessionTypeExtraChange();
				}
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-changeOrderSessionExtraDescription() ERROR: " + e.toString());
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

	public void changeOrderSessionExtraQuantity(BmObject bmObject, String quantity) {
		bmoOrderSessionExtra = (BmoOrderSessionExtra)bmObject;
		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_REVISION) {
			try {
				int q = Integer.parseInt(quantity);
				if (q > 0) {
					bmoOrderSessionExtra.getQuantity().setValue(quantity);
					savesessionTypeExtraChange();
				} else {
					// Eliminar registro
					deletesessionTypeExtra();
				}
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-changeOrderSessionExtraQuantity(): ERROR " + e.toString());
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

	public void changeOrderSessionExtraPrice(BmObject bmObject, String price) {
		bmoOrderSessionExtra = (BmoOrderSessionExtra)bmObject;
		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_REVISION) {
			try {
				Double p = Double.parseDouble(price);
				if (bmoOrderSessionExtra.getSessionTypeExtraId().toInteger() > 0) {
					if (p == 0) {
						bmoOrderSessionExtra.getPrice().setValue(0);
						savesessionTypeExtraChange();
					} else {
						if (bmoOrderSessionExtra.getBmoSessionTypeExtra().getFixedPrice().toBoolean()) {
							showSystemMessage("El Costo del Extra es Fijo. Se reestablecerá Precio del Catálogo.");	
						}
						bmoOrderSessionExtra.getPrice().setValue(p);
						savesessionTypeExtraChange();
					}
				} else {
					bmoOrderSessionExtra.getPrice().setValue(p);
					savesessionTypeExtraChange();
				}
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-changeOrderSessionExtraPrice(): ERROR " + e.toString());
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

	public void savesessionTypeExtraChange() {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-savesessionTypeExtraChange(): ERROR " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				processsessionTypeExtraChangeSave(result);
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().save(bmoOrderSessionExtra.getPmClass(), bmoOrderSessionExtra, callback);	
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-savesessionTypeExtraChange(): ERROR " + e.toString());
		}
	}

	public void deletesessionTypeExtra(BmoOrderSessionExtra bmoOrderSessionExtra) {
		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_REVISION) {
			this.bmoOrderSessionExtra = bmoOrderSessionExtra;
			deletesessionTypeExtra();
		} else {
			showSystemMessage("El Pedido no se puede modificar - está Autorizado");
		}
	}

	public void deletesessionTypeExtra() {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-deletesessionTypeExtra(): ERROR " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				processsessionTypeExtraDelete(result);
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().delete(bmoOrderSessionExtra.getPmClass(), bmoOrderSessionExtra, callback);	
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-deletesessionTypeExtra(): ERROR " + e.toString());
		}
	}

	public void processsessionTypeExtraChangeSave(BmUpdateResult bmUpdateResult) {
		if (bmUpdateResult.hasErrors()) 
			showSystemMessage("Error al modificar Extra: " + bmUpdateResult.errorsToString());
		this.reset();
	}

	public void processsessionTypeExtraDelete(BmUpdateResult result) {
		if (result.hasErrors()) showSystemMessage("processsessionTypeExtraDelete() ERROR: " + result.errorsToString());
		this.reset();
	}	
	
	

	// Agrega un item de un producto a un grupo de la cotizacion
	private class UiOrderSessionExtraForm extends Ui {
		private BProgramServiceAsync bmObjectServiceAsync = GWT.create(BProgramService.class);
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
		private TextArea commentsTextArea = new TextArea();
		private TextBox quantityTextBox = new TextBox();
		private TextBox priceTextBox = new TextBox();
		private UiListBox sessionTypeExtraListBox = new UiListBox(getUiParams(), new BmoSessionTypeExtra());

		private BmoOrderSessionExtra bmoOrderSessionExtra;
		private Button saveButton = new Button("GUARDAR");
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		private BmoOrder bmoOrder;
		private BmoSessionTypeExtra bmoSessionTypeExtra = new BmoSessionTypeExtra();
		String sessionExtraTypeId = "";


		public UiOrderSessionExtraForm(UiParams uiParams, Panel defaultPanel, BmoOrder bmoOrder, BmoSessionTypeExtra bmoSessionTypeExtra) {
			super(uiParams, defaultPanel);
			this.bmoOrderSessionExtra = new BmoOrderSessionExtra();
			this.bmoOrder = bmoOrder;
			this.bmoSessionTypeExtra = bmoSessionTypeExtra;

			try {
				bmoOrderSessionExtra.getSessionTypeExtraId().setValue(bmoSessionTypeExtra.getId());
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
			if (getSFParams().hasWrite(bmoOrderSessionExtra.getProgramCode())) saveButton.setVisible(true);
			buttonPanel.add(saveButton);

			defaultPanel.add(formTable);
		}

		public void show(){
			// Por defaul la cotizacion maneja 1 dia
			try {
				bmoOrderSessionExtra.getQuantity().setValue(1);
				bmoSessionTypeExtra.getIdField().setNullable(true);
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-show() ERROR: al asignar valor 1 a los dias del item de la cotizacion: " + e.toString());
			}
			
			formTable.addField(1, 0, sessionTypeExtraListBox, bmoOrderSessionExtra.getSessionTypeExtraId());			
			formTable.addField(2, 0, quantityTextBox, bmoOrderSessionExtra.getQuantity());
			formTable.addField(3, 0, priceTextBox, bmoOrderSessionExtra.getPrice());
			formTable.addField(4, 0, commentsTextArea, bmoOrderSessionExtra.getComments());
			formTable.addButtonPanel(buttonPanel);

			//statusEffect();
		}

		public void formListChange(ChangeEvent event) {
			if (event.getSource() == sessionTypeExtraListBox) {			
				BmoSessionTypeExtra bmoSessionTypeExtra = (BmoSessionTypeExtra)sessionTypeExtraListBox.getSelectedBmObject();
				sessionExtraTypeId = sessionTypeExtraListBox.getSelectedId();
				priceTextBox.setText(bmoSessionTypeExtra.getPrice().toString());
			} 

			statusEffect();
		}

		private void statusEffect() {
			if (!sessionTypeExtraListBox.getSelectedId().equals("") && !sessionTypeExtraListBox.getSelectedId().equals("0")) {
				bmoSessionTypeExtra = (BmoSessionTypeExtra)sessionTypeExtraListBox.getSelectedBmObject();
				quantityTextBox.setEnabled(false);
				quantityTextBox.setText("1");

				// Determina si se puede modificar el precio
				if (bmoSessionTypeExtra.getFixedPrice().toBoolean()) {
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

//		private void setSessionTypeExtraFilter(BmUpdateResult bmUpdateResult) {
//			if (!bmUpdateResult.hasErrors()) {
//				BmoSessionType bmoSessionType = (BmoSessionType)bmUpdateResult.getBmObject();
//				BmFilter filterByModel = new BmFilter();
//				BmoSessionTypeExtra bmoSessionTypeExtra = new BmoSessionTypeExtra();
//				filterByModel.setValueFilter(bmoSessionTypeExtra.getKind(), bmoSessionTypeExtra.getSessionTypeId(), bmoSessionType.getId());
//				sessionTypeExtraListBox.addFilter(filterByModel);
//
//				// Filtra por vigencia fin				
//				BmFilter filterByPromoEnd = new BmFilter();
//				filterByPromoEnd.setValueOperatorFilter(bmoSessionTypeExtra.getKind(), bmoSessionTypeExtra.getEndDate(), BmFilter.MAJOREQUAL, bmoOrder.getLockStart().toString().substring(0,10));
//				sessionTypeExtraListBox.addFilter(filterByPromoEnd);
//				
//				// Filtra por vigencia inicio
//				BmFilter filterByPromoStart = new BmFilter();								
//				filterByPromoStart.setValueOperatorFilter(bmoSessionTypeExtra.getKind(), bmoSessionTypeExtra.getStartDate(), BmFilter.MINOREQUAL, bmoOrder.getLockStart().toString().substring(0,10));
//				sessionTypeExtraListBox.addFilter(filterByPromoStart);
//				
//				formTable.addField(1, 0, sessionTypeExtraListBox, bmoSessionTypeExtra.getIdField());
//				saveButton.setVisible(true);
//			} else {
//				showSystemMessage(bmUpdateResult.errorsToString());
//				orderSessionExtraDialogBox.setVisible(false);
//			}
//		}
		
		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) 
				showSystemMessage("Error al modificar Extra: " + bmUpdateResult.errorsToString());
			else {
				orderSessionExtraDialogBox.hide();
				reset();
			}
		}

		public void prepareSave() {
			try {
				bmoOrderSessionExtra = new BmoOrderSessionExtra();
				bmoOrderSessionExtra.getSessionTypeExtraId().setValue(sessionExtraTypeId);
				bmoOrderSessionExtra.getPrice().setValue(priceTextBox.getText());
				bmoOrderSessionExtra.getQuantity().setValue(quantityTextBox.getText());
				bmoOrderSessionExtra.getComments().setValue(commentsTextArea.getText());
				bmoOrderSessionExtra.getOrderId().setValue(bmoOrder.getId());
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
					bmObjectServiceAsync.save(bmoOrderSessionExtra.getPmClass(), bmoOrderSessionExtra, callback);					
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-save() ERROR: " + e.toString());
			}
		}
	}
	
	private class UiOrderSessionExtraDocumentForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());		
		private TextBox nameExtraTextBox = new TextBox();
		private BmoOrderSessionExtra bmoOrderSessionExtra = new BmoOrderSessionExtra();
		
		public UiOrderSessionExtraDocumentForm(UiParams uiParams, Panel defaultPanel, BmoOrder bmoOrder, BmoOrderSessionExtra bmoOrderSessionExtra) {			
			super(uiParams, defaultPanel);			
			this.bmoOrderSessionExtra = bmoOrderSessionExtra;
			
			defaultPanel.add(formTable);
		}
		
		public void show() {
			formTable.addField(1, 0, nameExtraTextBox, bmoOrderSessionExtra.getBmoSessionTypeExtra().getName());
			//formTable.addFileUploadBoxField(2, 0, fileUpload, bmoOrderSessionExtra.getBmoSessionTypeExtra().getFile());
			
			statusEffect();
		}
		
		private void statusEffect() {
			nameExtraTextBox.setEnabled(false);
		}
		
	}
	
}