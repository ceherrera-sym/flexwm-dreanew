package com.flexwm.client.co;

import com.flexwm.client.co.UiWorkContract.UiWorkContractForm.WorkContractUpdater;
import com.flexwm.shared.co.BmoContractConceptItem;
import com.flexwm.shared.co.BmoWorkContract;
import com.flexwm.shared.co.BmoWorkItem;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiFormFlexTable;
import com.symgae.client.ui.UiListDataProvider;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;


public class UiWorkContractConceptItemGrid extends Ui {
	private UiFormFlexTable formFlexTable = new UiFormFlexTable(getUiParams());

	// ContractConceptItems	
	private BmoContractConceptItem bmoContractConceptItem = new BmoContractConceptItem();
	private FlowPanel contractConceptItemPanel = new FlowPanel();
	private CellTable<BmObject> contractConceptItemGrid = new CellTable<BmObject>();
	private UiListDataProvider<BmObject> contractConceptItemData;
	BmFilter workItemContractItemFilter;

	private Button addContractConceptItemButton = new Button("+ item");
	protected DialogBox contractConceptItemDialogBox;	

	// Cambio de Concept
	DialogBox changeConceptDialogBox;
	Button changeConceptButton = new Button("Cambiar");
	Button closeConceptButton = new Button("Cerrar");

	// Otros
	private HorizontalPanel buttonPanel = new HorizontalPanel();
	//	private Label amountLabel = new Label();
	private NumberFormat numberFormat = NumberFormat.getDecimalFormat();
	private BmoWorkContract bmoWorkContract;	
	private WorkContractUpdater workContractUpdater;

	public UiWorkContractConceptItemGrid(UiParams uiParams, Panel defaultPanel, BmoWorkContract bmoWorkContract, WorkContractUpdater workContractUpdater){
		super(uiParams, defaultPanel);
		this.bmoWorkContract = bmoWorkContract;
		this.workContractUpdater = workContractUpdater;

		closeConceptButton.setStyleName("formCloseButton");
		closeConceptButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				changeConceptDialogBox.hide();
			}
		});

		

		// Inicializar grid de Personal
		contractConceptItemGrid = new CellTable<BmObject>();
		contractConceptItemGrid.setWidth("100%");
		contractConceptItemPanel.clear();
		contractConceptItemPanel.setWidth("100%");
		defaultPanel.setStyleName("detailStart");
		setContractConceptItemColumns();
		workItemContractItemFilter = new BmFilter();		
		workItemContractItemFilter.setValueFilter(bmoContractConceptItem.getKind(), bmoContractConceptItem.getWorkContractId().getName(), bmoWorkContract.getId());
		contractConceptItemGrid.addLoadingStateChangeHandler(new LoadingStateChangeEvent.Handler() {           
			@Override
			public void onLoadingStateChanged(LoadingStateChangeEvent event) {
				if (event.getLoadingState() == LoadingState.LOADED) {
					changeHeight();
				}
			}
		}); 
		contractConceptItemData = new UiListDataProvider<BmObject>(new BmoContractConceptItem(), workItemContractItemFilter);
		contractConceptItemData.addDataDisplay(contractConceptItemGrid);
		contractConceptItemPanel.add(contractConceptItemGrid);

		// Panel de botones
		addContractConceptItemButton.setStyleName("formSaveButton");
		addContractConceptItemButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addContractConceptItem();
			}
		});
		buttonPanel.setHeight("100%");
		buttonPanel.setStyleName("formButtonPanel");
		buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);				
		buttonPanel.add(addContractConceptItemButton);

		// Crear forma y campos
		formFlexTable.setWidth("100%");
		formFlexTable.addPanel(1, 0, contractConceptItemPanel, 4);
		//Si es de tipo orden de compra no mostrar el boton para agregar items		
		//formFlexTable.addButtonPanel(buttonPanel);
		defaultPanel.add(formFlexTable);
	}

	@Override
	public void show(){
		contractConceptItemData.list();
		contractConceptItemGrid.redraw();
		statusEffect();
	}

	public void changeHeight() {
		contractConceptItemGrid.setPageSize(contractConceptItemData.getList().size());
		contractConceptItemGrid.setVisibleRange(0, contractConceptItemData.getList().size());
	}

	public void setContractConceptItemColumns() {
		// Clave
		Column<BmObject, String> codeColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoContractConceptItem)bmObject).getBmoWorkItem().getBmoUnitPrice().getCode().toString();
			}
		};
		contractConceptItemGrid.addColumn(codeColumn, SafeHtmlUtils.fromSafeConstant("Clave"));
		codeColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		contractConceptItemGrid.setColumnWidth(codeColumn, 100, Unit.PX);

		//Descripcion
		Column<BmObject, String> descriptionColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoContractConceptItem)bmObject).getBmoWorkItem().getBmoUnitPrice().getName().toString();
			}
		};
		contractConceptItemGrid.addColumn(descriptionColumn, SafeHtmlUtils.fromSafeConstant("Nombre"));
		descriptionColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		contractConceptItemGrid.setColumnWidth(descriptionColumn, 150, Unit.PX);

		// Unidad
		Column<BmObject, String> unitColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoContractConceptItem)bmObject).getBmoWorkItem().getBmoUnitPrice().getBmoUnit().getName().toString();
			}
		};
		contractConceptItemGrid.addColumn(unitColumn, SafeHtmlUtils.fromSafeConstant("Unidad"));
		unitColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		contractConceptItemGrid.setColumnWidth(unitColumn, 100, Unit.PX);

		// Cantidad
		Column<BmObject, String> cantColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoContractConceptItem)bmObject).getBmoWorkItem().getQuantity().toString();
			}
		};
		contractConceptItemGrid.addColumn(cantColumn, SafeHtmlUtils.fromSafeConstant("Cantidad"));
		cantColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		contractConceptItemGrid.setColumnWidth(cantColumn, 100, Unit.PX);

		// Precio
		Column<BmObject, String>  priceColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				numberFormat = NumberFormat.getCurrencyFormat();
				String formatted = numberFormat.format(((BmoContractConceptItem)bmObject).getPrice().toDouble());
				return (formatted);
			}
		};
		/*priceColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
			@Override
			public void update(int index, BmObject bmObject, String value) {
				changePrice(bmObject, value);
			}
		});*/

		contractConceptItemGrid.addColumn(priceColumn, SafeHtmlUtils.fromSafeConstant("Precio"));
		priceColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		contractConceptItemGrid.setColumnWidth(priceColumn, 100, Unit.PX);

		// Total
		Column<BmObject, String> totalColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				numberFormat = NumberFormat.getCurrencyFormat();
				String formatted = numberFormat.format(((BmoContractConceptItem)bmObject).getAmount().toDouble());
				return (formatted);
			}
		};
		contractConceptItemGrid.addColumn(totalColumn, SafeHtmlUtils.fromSafeConstant("Total"));	
		totalColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		contractConceptItemGrid.setColumnWidth(totalColumn, 100, Unit.PX);


	}

	public void statusEffect(){
		if(bmoWorkContract.getStatus().equals(BmoWorkContract.STATUS_REVISION)) {
			addContractConceptItemButton.setVisible(true);
		} else {
			addContractConceptItemButton.setVisible(false);
		}
	}

	public void reset(){
		workContractUpdater.changeWorkContract();
	}

	public void changeQuantity(BmObject bmObject, String quantity) {
		bmoContractConceptItem = (BmoContractConceptItem)bmObject;
		try {
			if(!bmoWorkContract.getStatus().equals(BmoWorkContract.STATUS_REVISION)) {
				showErrorMessage("No se puede modificar la cantidad, el contrato no esta en revision");
				reset();
			} else {
				int q = Integer.parseInt(quantity);
				if (q > 0) {
					bmoContractConceptItem.getQuantity().setValue(quantity);
					saveItemChange();
				} else {
					// Eliminar registro				
					deleteItem();
				}
			}	
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-changeQuantity(): ERROR " + e.toString());
		}
	}

	public void changePrice(BmObject bmObject, String price) {
		bmoContractConceptItem = (BmoContractConceptItem)bmObject;
		try {
			double p = Double.parseDouble(price);
			/*if(!bmoWorkContract.getStatus().equals(BmoWorkContract.STATUS_REVISION)) {
				showErrorMessage("No se puede modificar el precio, el contrato no esta en revision");
				reset();

			} else {*/
			if (p > 0) {
				bmoContractConceptItem.getPrice().setValue(price);
				saveItemChange();
			}								
			//}
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-changePrice(): ERROR " + e.toString());
		}
	}

	public void processItemChangeSave(BmUpdateResult bmUpdateResult) {
		if (bmUpdateResult.hasErrors()) 
			showSystemMessage("Error al modificar el Item: " + bmUpdateResult.errorsToString());
		else {
			this.reset();
		}
	}

	public void processItemDelete(BmUpdateResult bmUpdateResult) {
		if (bmUpdateResult.hasErrors()) 
			showSystemMessage("Error al modificar el Item: " + bmUpdateResult.errorsToString());
		else {
			this.reset();			
		}		
	}

	public void saveItemChange() {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			@Override
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-saveItemChange(): ERROR " + caught.toString());
			}

			@Override
			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				processItemChangeSave(result);
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()){
				startLoading();
				getUiParams().getBmObjectServiceAsync().save(bmoContractConceptItem.getPmClass(), bmoContractConceptItem, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-saveItemChange(): ERROR " + e.toString());
		}
	}

	public void deleteItem() {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			@Override
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-deleteItem(): ERROR " + caught.toString());
			}

			@Override
			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				processItemDelete(result);
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().delete(bmoContractConceptItem.getPmClass(), bmoContractConceptItem, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-deleteItem(): ERROR " + e.toString());
		}
	}

	public void addContractConceptItem() {
		contractConceptItemDialogBox = new DialogBox(true);
		contractConceptItemDialogBox.setGlassEnabled(true);
		contractConceptItemDialogBox.setText("Items del Precio Unitario");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "130px");

		contractConceptItemDialogBox.setWidget(vp);

		UiContractConceptItemForm contractConceptItemForm = new UiContractConceptItemForm(getUiParams(), vp, bmoWorkContract);
		contractConceptItemForm.show();

		contractConceptItemDialogBox.center();
		contractConceptItemDialogBox.show();
	}

	// Agrega un item de un producto a la CXP
	private class UiContractConceptItemForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
		private TextBox codeTextBox = new TextBox();
		private TextBox nameTextBox = new TextBox();
		private UiSuggestBox workItemSuggestBox = new UiSuggestBox(new BmoWorkItem());		
		private TextBox quantityTextBox = new TextBox();		
		private BmoContractConceptItem bmoContractConceptItem;
		private BmoWorkContract bmoWorkContract = new BmoWorkContract();
		private Button saveButton = new Button("Agregar");
		private HorizontalPanel buttonPanel = new HorizontalPanel();


		public UiContractConceptItemForm(UiParams uiParams, Panel defaultPanel, BmoWorkContract bmoWorkContract) {
			super(uiParams, defaultPanel);
			this.bmoWorkContract = bmoWorkContract;
			this.bmoContractConceptItem = new BmoContractConceptItem();

			try {
				bmoContractConceptItem.getWorkContractId().setValue(bmoWorkContract.getId());

			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "(): ERROR " + e.toString());
			}

			saveButton.setStyleName("formSaveButton");
			saveButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					prepareSave();
				}
			});
			saveButton.setEnabled(false);			
			buttonPanel.add(saveButton);

			defaultPanel.add(formTable);
		}

		@Override
		public void show(){			
			formTable.addField(1, 0, codeTextBox, bmoContractConceptItem.getCode());
			formTable.addField(2, 0, nameTextBox, bmoContractConceptItem.getName());
			formTable.addField(3, 0, workItemSuggestBox, bmoContractConceptItem.getWorkItemId());			
			formTable.addField(4, 0, quantityTextBox, bmoContractConceptItem.getQuantity());			
			formTable.addButtonPanel(buttonPanel);			
		}

		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) 
				showSystemMessage("Error al modificar Item: " + bmUpdateResult.errorsToString());
			else {
				contractConceptItemDialogBox.hide();
				reset();
			}
		}

		public void prepareSave() {
			try {
				bmoContractConceptItem.getWorkContractId().setValue(bmoWorkContract.getId());
				bmoContractConceptItem = new BmoContractConceptItem();
				bmoContractConceptItem.getWorkItemId().setValue(workItemSuggestBox.getSelectedId());
				bmoContractConceptItem.getCode().setValue(codeTextBox.getText());
				bmoContractConceptItem.getName().setValue(nameTextBox.getText());				
				bmoContractConceptItem.getQuantity().setValue(quantityTextBox.getText());
				bmoContractConceptItem.getAmount().setValue(bmoContractConceptItem.getAmount().toDouble());
				bmoContractConceptItem.getPrice().setValue(bmoContractConceptItem.getPrice().toDouble());
				save();
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-prepareSave(): ERROR " + e.toString());
			}
		}

		public void save() {
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-save(): ERROR " + caught.toString());
				}

				@Override
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					processUpdateResult(result);
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().save(bmoContractConceptItem.getPmClass(), bmoContractConceptItem, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-save(): ERROR " + e.toString());
			}
		}
	}

}
