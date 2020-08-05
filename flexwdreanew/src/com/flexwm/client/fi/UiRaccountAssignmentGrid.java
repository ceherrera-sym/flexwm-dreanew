package com.flexwm.client.fi;


import com.flexwm.client.fi.UiRaccount.UiRaccountForm.RaccountUpdater;
import com.flexwm.shared.fi.BmoBankMovement;
import com.flexwm.shared.fi.BmoRaccount;
import com.flexwm.shared.fi.BmoRaccountAssignment;
import com.flexwm.shared.fi.BmoRaccountAssignmentParent;
import com.flexwm.shared.fi.BmoRaccountType;
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


public class UiRaccountAssignmentGrid extends Ui {
	private UiFormFlexTable formFlexTable = new UiFormFlexTable(getUiParams());
	private NumberFormat numberFormat = NumberFormat.getDecimalFormat();

	// RaccountAssignments
	UiSuggestBox changeRaccountSuggestBox;
	private BmoRaccountAssignment bmoRaccountAssignment = new BmoRaccountAssignment();
	private BmoRaccountAssignmentParent bmoRaccountAssignmentParent = new BmoRaccountAssignmentParent();

	private FlowPanel raccountAssignementPanel = new FlowPanel();
	private CellTable<BmObject> raccountAssignementGrid = new CellTable<BmObject>();
	private UiListDataProvider<BmObject> raccountAssignementData;
	BmFilter raccountAssignementFilter;

	private Button addRaccountAssignmentButton = new Button("+ APLICAR");
	protected DialogBox raccountAssignementDialogBox;

	protected DialogBox showBankMovementDialogBox;

	// Cambio de Raccount
	DialogBox changeRaccountDialogBox;
	Button changeRaccountButton = new Button("CAMBIAR");
	Button closeRaccountButton = new Button("CERRAR");

	// Otros
	private HorizontalPanel buttonPanel = new HorizontalPanel();

	private BmoRaccount bmoRaccount;
	private RaccountUpdater raccountUpdater;
	private int orderId = 0;

	// Constructor original
	public UiRaccountAssignmentGrid(UiParams uiParams, Panel defaultPanel, BmoRaccount bmoRaccount, RaccountUpdater raccountUpdater) {
		super(uiParams, defaultPanel);

		initialize(uiParams, defaultPanel, bmoRaccount, raccountUpdater);
	}

	// Constructor con parametro de Pedidos
	public UiRaccountAssignmentGrid(UiParams uiParams, Panel defaultPanel, BmoRaccount bmoRaccount, int orderId, RaccountUpdater raccountUpdater) {
		super(uiParams, defaultPanel);
		this.orderId = orderId;
		initialize(uiParams, defaultPanel, bmoRaccount, raccountUpdater);
	}

	// Inicializa el objeto
	public void initialize(UiParams uiParams, Panel defaultPanel, BmoRaccount bmoRaccount, RaccountUpdater raccountUpdater) {

		this.bmoRaccount = bmoRaccount;
		this.raccountUpdater = raccountUpdater;

		closeRaccountButton.setStyleName("formCloseButton");
		closeRaccountButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				changeRaccountDialogBox.hide();
			}
		});

		

		// Inicializar grid de Personal
		raccountAssignementGrid = new CellTable<BmObject>();
		raccountAssignementGrid.setWidth("100%");
		raccountAssignementPanel.clear();
		raccountAssignementPanel.setWidth("100%");
		defaultPanel.setStyleName("detailStart");
		setRaccountAssignmentColumns();
		raccountAssignementFilter = new BmFilter();

		if (bmoRaccount.getBmoRaccountType().getType().equals(BmoRaccountType.TYPE_DEPOSIT)) {
//			showSystemMessage("TYPE_DEPOSIT");
			raccountAssignementFilter.setValueFilter(bmoRaccountAssignment.getKind(), bmoRaccountAssignment.getRaccountId().getName(), bmoRaccount.getId());
			raccountAssignementData = new UiListDataProvider<BmObject>(new BmoRaccountAssignmentParent(), raccountAssignementFilter);

		} else {
//			showSystemMessage("TYPE_WITHDRAW");
			raccountAssignementFilter.setValueFilter(bmoRaccountAssignment.getKind(), bmoRaccountAssignment.getForeignRaccountId().getName(), bmoRaccount.getId());
			raccountAssignementData = new UiListDataProvider<BmObject>(new BmoRaccountAssignment(), raccountAssignementFilter);
		}

		raccountAssignementGrid.addLoadingStateChangeHandler(new LoadingStateChangeEvent.Handler() {           
			@Override
			public void onLoadingStateChanged(LoadingStateChangeEvent event) {
				if (event.getLoadingState() == LoadingState.LOADED) {
					changeHeight();
				}
			}
		}); 
//		raccountAssignementData = new UiListDataProvider<BmObject>(new BmoRaccountAssignment(), raccountAssignementFilter);
		raccountAssignementData.addDataDisplay(raccountAssignementGrid);
		raccountAssignementPanel.add(raccountAssignementGrid);

		// Panel de botones
		addRaccountAssignmentButton.setStyleName("formSaveButton");
		addRaccountAssignmentButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addRaccount();
			}
		});
		buttonPanel.setHeight("100%");
		buttonPanel.setStyleName("formButtonPanel");
		buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		if (bmoRaccount.getBmoRaccountType().getType().equals(BmoRaccountType.TYPE_DEPOSIT)) {
			buttonPanel.add(addRaccountAssignmentButton);
		}

		// Crear forma y campos
		formFlexTable.setWidth("100%");		
		formFlexTable.addPanel(3, 0, raccountAssignementPanel, 4);
		formFlexTable.addButtonPanel(buttonPanel);
		defaultPanel.add(formFlexTable);
	}

	public void show(){
		raccountAssignementData.list();
		raccountAssignementGrid.redraw();

		statusEffect();
	}

	public void statusEffect(){
		if (bmoRaccount.getStatus().toChar() == BmoRaccount.STATUS_AUTHORIZED) {
			addRaccountAssignmentButton.setVisible(false);
		} else {
			addRaccountAssignmentButton.setVisible(true);
		}
	}

	public void reset(){
		raccountUpdater.changeRaccount();
	}

	public void changeHeight() {
		raccountAssignementGrid.setVisibleRange(0, raccountAssignementData.getList().size());
	}

	public void setRaccountAssignmentColumns() {
		// La CxC SI es una Nota de credito
		if (bmoRaccount.getBmoRaccountType().getType().equals(BmoRaccountType.TYPE_DEPOSIT)) {
			// Origen CxC
			Column<BmObject, String> originColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {				
					return "" + bmoRaccount.getCode().toString();
				}
			};
			raccountAssignementGrid.addColumn(originColumn, SafeHtmlUtils.fromSafeConstant("CxC Origen"));
			originColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			raccountAssignementGrid.setColumnWidth(originColumn, 50, Unit.PX);
	
			// Destino CxC
			Column<BmObject, String> foreignColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {				
					return "" + ((BmoRaccountAssignmentParent)bmObject).getBmoForeignRaccount().getCode().toString();
				}
			};
			raccountAssignementGrid.addColumn(foreignColumn, SafeHtmlUtils.fromSafeConstant("CxC Destino"));
			foreignColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			raccountAssignementGrid.setColumnWidth(foreignColumn, 50, Unit.PX);
		
			// Factura
			Column<BmObject, String> invoiceColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoRaccountAssignmentParent)bmObject).getInvoiceno().toString();
				}
			};
			raccountAssignementGrid.addColumn(invoiceColumn, SafeHtmlUtils.fromSafeConstant("Fact."));
			invoiceColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			raccountAssignementGrid.setColumnWidth(invoiceColumn, 150, Unit.PX);

			//Mostrar el boton para ir a la CxC o CxP
			if (bmoRaccount.getBmoRaccountType().getCategory().equals(BmoRaccountType.CATEGORY_OTHER) &&
					bmoRaccount.getBmoRaccountType().getType().equals(BmoRaccountType.TYPE_WITHDRAW)) {				
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
						int raccountId = ((BmoRaccountAssignmentParent)bmObject).getRaccountId().toInteger();
						showBankMovement(raccountId);
					}
				});

				raccountAssignementGrid.addColumn(showBankMovement, SafeHtmlUtils.fromSafeConstant("Ver"));
				showBankMovement.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
				raccountAssignementGrid.setColumnWidth(showBankMovement, 50, Unit.PX);
			}	

			// Monto
			Column<BmObject, String> amountColumn;
			if (bmoRaccount.getStatus().equals(BmoRaccount.STATUS_REVISION)) {
				amountColumn = new Column<BmObject, String>(new EditTextCell()) {
					@Override
					public String getValue(BmObject bmObject) {
						return ((BmoRaccountAssignmentParent)bmObject).getAmount().toString();
					}
				};
				amountColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
					@Override
					public void update(int index, BmObject bmObject, String value) {
						changeAmount(bmObject, value);
					}
				});
			} else {
				amountColumn = new Column<BmObject, String>(new TextCell()) {
					@Override
					public String getValue(BmObject bmObject) {
						numberFormat = NumberFormat.getCurrencyFormat();
						double amount = 0;
						if (((BmoRaccountAssignmentParent)bmObject).getAmountConverted().toDouble() > 0 )
							amount = ((BmoRaccountAssignmentParent)bmObject).getAmountConverted().toDouble();
						else
							amount = ((BmoRaccountAssignmentParent)bmObject).getAmount().toDouble();

						String formatted = numberFormat.format(amount);
						return (formatted);
					}
				};
			}

			amountColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
			raccountAssignementGrid.addColumn(amountColumn, SafeHtmlUtils.fromSafeConstant("Monto"));
			amountColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					changeAmount(bmObject, value);
				}
			});

			// Eliminar
			Column<BmObject, String> deleteColumn;
			if (bmoRaccount.getStatus().equals(BmoRaccount.STATUS_REVISION)) {
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
			raccountAssignementGrid.addColumn(deleteColumn, SafeHtmlUtils.fromSafeConstant("Eliminar"));
			deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			raccountAssignementGrid.setColumnWidth(deleteColumn, 50, Unit.PX);
		
		// ------------------------------------------------------------------------------------------
		// La CxC NO es una Nota de credito
		} else {
			// Origen CxC
			Column<BmObject, String> originColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {				
					return "" +  ((BmoRaccountAssignment)bmObject).getBmoForeignRaccount().getCode().toString();
				}
			};
			raccountAssignementGrid.addColumn(originColumn, SafeHtmlUtils.fromSafeConstant("CxC Origen"));
			originColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			raccountAssignementGrid.setColumnWidth(originColumn, 50, Unit.PX);

			// Destino CxC
			Column<BmObject, String> foreignColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {				
					return "" + bmoRaccount.getCode().toString();
				}
			};
			raccountAssignementGrid.addColumn(foreignColumn, SafeHtmlUtils.fromSafeConstant("CxC Destino"));
			foreignColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			raccountAssignementGrid.setColumnWidth(foreignColumn, 50, Unit.PX);
			
			// Factura
			Column<BmObject, String> invoiceColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoRaccountAssignment)bmObject).getInvoiceno().toString();
				}
			};
			raccountAssignementGrid.addColumn(invoiceColumn, SafeHtmlUtils.fromSafeConstant("Fact."));
			invoiceColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			raccountAssignementGrid.setColumnWidth(invoiceColumn, 150, Unit.PX);

			//Mostrar el boton para ir a la CxC o CxP
			if (bmoRaccount.getBmoRaccountType().getCategory().equals(BmoRaccountType.CATEGORY_OTHER) &&
					bmoRaccount.getBmoRaccountType().getType().equals(BmoRaccountType.TYPE_WITHDRAW)) {				
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
						int raccountId = ((BmoRaccountAssignment)bmObject).getRaccountId().toInteger();
						showBankMovement(raccountId);
					}
				});

				raccountAssignementGrid.addColumn(showBankMovement, SafeHtmlUtils.fromSafeConstant("Ver"));
				showBankMovement.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
				raccountAssignementGrid.setColumnWidth(showBankMovement, 50, Unit.PX);
			}	

			// Monto
			Column<BmObject, String> amountColumn;
			if (bmoRaccount.getStatus().equals(BmoRaccount.STATUS_REVISION)) {
				amountColumn = new Column<BmObject, String>(new EditTextCell()) {
					@Override
					public String getValue(BmObject bmObject) {
						return ((BmoRaccountAssignment)bmObject).getAmount().toString();
					}
				};
				amountColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
					@Override
					public void update(int index, BmObject bmObject, String value) {
						changeAmount(bmObject, value);
					}
				});
			} else {
				amountColumn = new Column<BmObject, String>(new TextCell()) {
					@Override
					public String getValue(BmObject bmObject) {
						numberFormat = NumberFormat.getCurrencyFormat();
						double amount = 0;
						if (((BmoRaccountAssignment)bmObject).getAmountConverted().toDouble() > 0 )
							amount = ((BmoRaccountAssignment)bmObject).getAmountConverted().toDouble();
						else
							amount = ((BmoRaccountAssignment)bmObject).getAmount().toDouble();

						String formatted = numberFormat.format(amount);
						return (formatted);
					}
				};
			}

			amountColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
			raccountAssignementGrid.addColumn(amountColumn, SafeHtmlUtils.fromSafeConstant("Monto"));
			amountColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					changeAmount(bmObject, value);
				}
			});

			// Eliminar
			Column<BmObject, String> deleteColumn;
			if (bmoRaccount.getStatus().equals(BmoRaccount.STATUS_REVISION)) {
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
			raccountAssignementGrid.addColumn(deleteColumn, SafeHtmlUtils.fromSafeConstant("Eliminar"));
			deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			raccountAssignementGrid.setColumnWidth(deleteColumn, 50, Unit.PX);
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
				getUiParams().getBmObjectServiceAsync().save(bmoRaccountAssignment.getPmClass(), bmoRaccountAssignment, callback);
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

	public void showBankMovement(int raccountId) {		
		showBankMovementDialogBox = new DialogBox(true);
		showBankMovementDialogBox.setGlassEnabled(true);

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "130px");

		showBankMovementDialogBox.setWidget(vp);
		// Verifica si es Nota de credito o MB
		getRaccount(raccountId);

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

	private void getRaccount(int id) {

		AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
			@Override
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getRaccount() ERROR: " + caught.toString());
			}

			@Override
			public void onSuccess(BmObject result) {
				stopLoading();				
				BmoRaccount bmoRacc = (BmoRaccount)result;

				if (bmoRacc.getBmoRaccountType().getCategory().equals(BmoRaccountType.CATEGORY_CREDITNOTE)) {
					UiRaccount uiRaccount = new UiRaccount(getUiParams());
					uiRaccount.edit(bmoRacc);
				} else {
					//Obtener el MB 
					getBankMovement(bmoRacc.getId());	
				}
			}
		};
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().get(bmoRaccount.getPmClass(), id, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-getRaccount(): ERROR " + e.toString());
		}
	}

	public void addRaccount(){
		addRaccount(new BmoRaccount());
	}

	public void addRaccount(BmoRaccount bmoForeignRaccount) {
		raccountAssignementDialogBox = new DialogBox(true);
		raccountAssignementDialogBox.setGlassEnabled(true);
		raccountAssignementDialogBox.setText("Aplicación de Pago");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "100px");

		raccountAssignementDialogBox.setWidget(vp);

		UiRaccountAssignmentForm RaccountAssignementForm = new UiRaccountAssignmentForm(getUiParams(), vp, bmoRaccount, bmoForeignRaccount);

		RaccountAssignementForm.show();

		raccountAssignementDialogBox.center();
		raccountAssignementDialogBox.show();
	}

	public void changeAmount(BmObject bmObject, String amount) {
		bmoRaccountAssignment = (BmoRaccountAssignment)bmObject;
		try {
			double p = Double.parseDouble(amount);
			if (bmoRaccount.getStatus().equals(BmoRaccount.STATUS_AUTHORIZED)) {
				showErrorMessage("No se puede modificar el monto, la cxc esta autorizada");
				reset();
			} else {
				if (p > 0) {
					bmoRaccountAssignment.getAmount().setValue(amount);
					saveItemChange();
				} else {
					//Si que el monto sea mayor a cero				}
					showErrorMessage("El monto debe ser mayor a $ 0.00.");
					reset();
				}
			}
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-changeAmount(): ERROR " + e.toString());
		}
	}

	private void deleteItem(BmObject bmObject) {
		if (bmoRaccount.getBmoRaccountType().getType().equals(BmoRaccountType.TYPE_DEPOSIT)) {
			bmoRaccountAssignmentParent = (BmoRaccountAssignmentParent)bmObject;
			deleteItem();
		} else {
			bmoRaccountAssignment = (BmoRaccountAssignment)bmObject;
			deleteItem();
		}
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
				if (bmoRaccount.getBmoRaccountType().getType().equals(BmoRaccountType.TYPE_DEPOSIT)) {
					getUiParams().getBmObjectServiceAsync().delete(bmoRaccountAssignmentParent.getPmClass(), bmoRaccountAssignmentParent, callback);
				} else {
					getUiParams().getBmObjectServiceAsync().delete(bmoRaccountAssignment.getPmClass(), bmoRaccountAssignment, callback);

				}
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
	private class UiRaccountAssignmentForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
		private TextBox amountTextBox = new TextBox();
		private TextBox amountConvertedTextBox = new TextBox();
		private TextBox parityTextBox = new TextBox();
		private BmoRaccountAssignment bmoRaccountAssignment;
		private Button saveButton = new Button("AGREGAR");
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		private UiSuggestBox foreignRaccountSuggestBox = new UiSuggestBox(new BmoRaccount());
		private BmoRaccount bmoRaccount;
		private BmoRaccount bmoRaccountForeign;


		public UiRaccountAssignmentForm(UiParams uiParams, Panel defaultPanel, BmoRaccount bmoRaccount, BmoRaccount bmoForeignRaccount) {
			super(uiParams, defaultPanel);
			this.bmoRaccountAssignment = new BmoRaccountAssignment();
			this.bmoRaccount = bmoRaccount;

			try {
				bmoRaccountAssignment.getRaccountId().setValue(bmoRaccount.getId());
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

			// Manejo de cambios de textbox
			ValueChangeHandler<String> textChangeHandler = new ValueChangeHandler<String>() {
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					formTextChange(event);
				}
			};
			formTable.setTextChangeHandler(textChangeHandler);

			saveButton.setStyleName("formSaveButton");
			saveButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					prepareSave();
				}
			});
			saveButton.setVisible(false);
			if (getSFParams().hasWrite(bmoRaccountAssignment.getProgramCode())) saveButton.setVisible(true);
			buttonPanel.add(saveButton);

			// CxC destino
			foreignRaccountSuggestBox = new UiSuggestBox(new BmoRaccount());

			// Filtros CxP destino
			BmFilter filterByCustomer = new BmFilter();
			filterByCustomer.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getCustomerId(), bmoRaccount.getCustomerId().toInteger());
			foreignRaccountSuggestBox.addFilter(filterByCustomer);

			// Filtra por estatus de pago
			BmFilter filterByPaymentStatus = new BmFilter();
			filterByPaymentStatus.setValueOperatorFilter(bmoRaccount.getKind(), bmoRaccount.getPaymentStatus(), BmFilter.NOTEQUALS, "" + BmoRaccount.PAYMENTSTATUS_TOTAL);
			foreignRaccountSuggestBox.addFilter(filterByPaymentStatus);

			// Filtra por CxC autorizadas
			BmFilter filterByStatus = new BmFilter();
			filterByStatus.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getStatus(), "" + BmoRaccount.STATUS_AUTHORIZED);
			foreignRaccountSuggestBox.addFilter(filterByStatus);

			// Filtra por CxC retiro
			BmFilter filterByType = new BmFilter();
			filterByType.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getBmoRaccountType().getType(), "" + BmoRaccountType.TYPE_WITHDRAW);
			foreignRaccountSuggestBox.addFilter(filterByType);

			// Si esta asignado el Pedido, filtrar CxC del pedido
			if (orderId > 0) {
				BmFilter filterByOrder = new BmFilter();
				filterByOrder.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getOrderId(), orderId);
				foreignRaccountSuggestBox.addFilter(filterByOrder);
			}

			defaultPanel.add(formTable);
		}

		public void show(){
			formTable.addField(1, 0, foreignRaccountSuggestBox, bmoRaccountAssignment.getForeignRaccountId());
			formTable.addField(2, 0, amountTextBox, bmoRaccountAssignment.getAmount());

			statusEffect();
		}

		public void formSuggestionChange(UiSuggestBox uiSuggestBox) {
			NumberFormat fmt = NumberFormat.getFormat("#####.##");
			bmoRaccountForeign = (BmoRaccount)foreignRaccountSuggestBox.getSelectedBmObject();			
			if (bmoRaccountForeign.getCurrencyId().toInteger() != bmoRaccount.getCurrencyId().toInteger()) {

				double factor = bmoRaccount.getCurrencyParity().toDouble() / bmoRaccountForeign.getCurrencyParity().toDouble();
				amountTextBox.setText("" + fmt.format(bmoRaccount.getBalance().toDouble()));

				//Aplicar el total del saldo de CxP Cargo				
				double balanceForeign = (bmoRaccount.getBalance().toDouble() * factor);
				if (balanceForeign > bmoRaccountForeign.getBalance().toDouble())
					balanceForeign = balanceForeign - bmoRaccountForeign.getBalance().toDouble();
				else	
					balanceForeign  = bmoRaccountForeign.getBalance().toDouble() - balanceForeign;

				amountConvertedTextBox.setText("" + fmt.format(balanceForeign));

				factor = bmoRaccount.getCurrencyParity().toDouble() * bmoRaccountForeign.getCurrencyParity().toDouble() ;

				try {
					bmoRaccountAssignment.getAmount().setValue(amountTextBox.getText());
					bmoRaccountAssignment.getCurrencyParity().setValue(fmt.format(factor));
					bmoRaccountAssignment.getAmountConverted().setValue(amountConvertedTextBox.getText());
				} catch (BmException e) {
					showSystemMessage(this.getClass().getName() + " ERROR: " + e.toString());
				}
				formTable.addField(2, 0, amountTextBox, bmoRaccountAssignment.getAmount());
				formTable.addField(3, 0, parityTextBox, bmoRaccountAssignment.getCurrencyParity());
				formTable.addField(4, 0, amountConvertedTextBox, bmoRaccountAssignment.getAmountConverted());
			} else {			
				amountTextBox.setText(bmoRaccountForeign.getBalance().toString());
			}	

			formTable.addButtonPanel(buttonPanel);

			statusEffect();
		}

		public void formTextChange(ValueChangeEvent<String> event) {			
			NumberFormat fmt = NumberFormat.getFormat("#####.##");
			double parityOrigen = bmoRaccount.getCurrencyParity().toDouble();
			double parityDestiny = bmoRaccountAssignment.getCurrencyParity().toDouble();
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
				raccountAssignementDialogBox.hide();
				reset();
			}
		}

		public void prepareSave() {
			try {
				bmoRaccountAssignment = new BmoRaccountAssignment();
				bmoRaccountAssignment.getForeignRaccountId().setValue(foreignRaccountSuggestBox.getSelectedId());
				bmoRaccountAssignment.getAmount().setValue(amountTextBox.getText());
				bmoRaccountAssignment.getCurrencyParity().setValue(parityTextBox.getText());
				bmoRaccountAssignment.getAmountConverted().setValue(amountConvertedTextBox.getText());
				bmoRaccountAssignment.getRaccountId().setValue(bmoRaccount.getId());
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
					getUiParams().getBmObjectServiceAsync().save(bmoRaccountAssignment.getPmClass(), bmoRaccountAssignment, callback);					
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-save() ERROR: " + e.toString());
			}
		}
	}
}
