/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.op;

import com.flexwm.client.op.UiWhMovement;
import com.flexwm.shared.op.BmoOrderItem;
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoWhMovItem;
import com.flexwm.shared.op.BmoWhMovement;
import com.flexwm.shared.op.BmoWhSection;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiDetailEastFlexTable;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiFormFlexTable;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiListDataProvider;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;


public class UiWhMovementFormOut extends UiFormDialog {
	private FlowPanel whMovItemPanel = new FlowPanel();
	private CellTable<BmObject> whMovItemGrid = new CellTable<BmObject>();
	private UiListDataProvider<BmObject> data;
	protected DialogBox whMovItemDialogBox;
	private Button addWhMovItemButton = new Button("+ ITEM");
	protected UiDetailEastFlexTable eastTable = new UiDetailEastFlexTable(getUiParams());
	private FlowPanel whMovementFormOutButtonPanel = new FlowPanel();
	private int saveItemChangeRpcAttempt = 0;
	UiListBox statusListBox = new UiListBox(getUiParams());

	String itemsSection = "Items";
	String statusSection = "Estatus";

	BmoWhSection bmoWhSection = new BmoWhSection();
	BmoWhMovItem bmoWhMovItem = new BmoWhMovItem();
	BmoWhMovement bmoWhMovement;

	public UiWhMovementFormOut(UiParams uiParams, int id) {
		super(uiParams, new BmoWhMovement(), id);

		// Movimiento de altura del grid
		whMovItemGrid.addLoadingStateChangeHandler(new LoadingStateChangeEvent.Handler() {           
			@Override
			public void onLoadingStateChanged(LoadingStateChangeEvent event) {
				if (event.getLoadingState() == LoadingState.LOADED) {
					changeHeight();
				}
			}
		}); 

		// Todas las operaciones con los items de la orden de compra
		addWhMovItemButton.setStyleName("formSaveButton");
		addWhMovItemButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addProduct();
			}
		});

		// Grid de items
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoWhMovItem.getKind(), bmoWhMovItem.getWhMovementId().getName(), id);
		data = new UiListDataProvider<BmObject>(new BmoWhMovItem(), bmFilter);

		whMovItemGrid.setWidth("100%");
		whMovItemPanel.setWidth("100%");
		setColumns();

		data.addDataDisplay(whMovItemGrid);
		whMovItemPanel.add(whMovItemGrid);
	}

	@Override
	public void populateFields() {
		bmoWhMovement = (BmoWhMovement)getBmObject();

		formFlexTable.addLabelField(1, 0, bmoWhMovement.getCode());
		formFlexTable.addLabelField(2, 0, bmoWhMovement.getType());
		formFlexTable.addLabelField(3, 0, bmoWhMovement.getBmoOrderDelivery().getName());
		formFlexTable.addLabelField(4, 0, bmoWhMovement.getBmoToWhSection().getName());
		formFlexTable.addLabelField(5, 0, bmoWhMovement.getDatemov());
		formFlexTable.addLabelField(6, 0, bmoWhMovement.getDescription());				
		formFlexTable.addLabelField(7, 0, bmoWhMovement.getCompanyId().getLabel(), bmoWhMovement.getBmoCompany().getName().toString());

		formFlexTable.addSectionLabel(8, 0, itemsSection, 2);
		formFlexTable.addPanel(9, 0, whMovementFormOutButtonPanel);
		whMovementFormOutButtonPanel.clear();
		whMovementFormOutButtonPanel.add(addWhMovItemButton);
		formFlexTable.addPanel(10, 0, whMovItemPanel, 2);

		formFlexTable.addSectionLabel(11, 0, statusSection, 2);
		formFlexTable.addField(12, 0, statusListBox, bmoWhMovement.getStatus());

		//		// East Table - acciones
		//		getUiParams().getUiTemplate().showEastPanel();
		//		eastTable.addEmptyRow();	
		//		getUiParams().getUiTemplate().getEastPanel().add(eastTable);
		//
		//		// Accion del formato
		//		FlowPanel formatPanel = new FlowPanel();
		//		formatPanel.setWidth((UiTemplate.EASTSIZE - 10) + "px");
		//		UiFormatDisplayList uiFormatDisplayList = new UiFormatDisplayList(getUiParams(), formatPanel, bmoWhMovement, bmoWhMovement.getId());
		//		BmoFormat bmoFormat = new BmoFormat();
		//		UiProgramParams uiProgramParams = getUiParams().getUiProgramParams(bmoFormat.getProgramCode());
		//		BmFilter bmFilter = new BmFilter();
		//		bmFilter.setValueFilter(bmoFormat.getKind(), bmoFormat.getProgramId(), bmObjectProgramId);
		//		uiProgramParams.setForceFilter(bmFilter);
		//		setUiType(bmoFormat.getProgramCode(), UiParams.MINIMALIST);
		//		uiFormatDisplayList.show();
		//		DecoratorPanel formatDecorator = new DecoratorPanel();
		//		formatDecorator.addStyleName("detailPanel");
		//		formatDecorator.add(formatPanel);
		//		getUiParams().getUiTemplate().getEastPanel().add(formatDecorator);
		//
		//		getUiParams().getUiTemplate().getEastPanel().add(new HTML("&nbsp;"));
	}

	public void changeHeight() {
		whMovItemGrid.setVisibleRange(0, data.getList().size());
	}

	public void setColumns() {
		// Cantidad
		Column<BmObject, String> quantityColumn;
		quantityColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoWhMovItem)bmObject).getQuantity().toString();
			}
		};
		whMovItemGrid.addColumn(quantityColumn, SafeHtmlUtils.fromSafeConstant("Cant."));
		whMovItemGrid.setColumnWidth(quantityColumn, 50, Unit.PX);
		quantityColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

		if (!isMobile()) {
			// Clave
			Column<BmObject, String> codeColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoWhMovItem)bmObject).getBmoProduct().getCode().toString();
				}
			};
			whMovItemGrid.setColumnWidth(codeColumn, 70, Unit.PX);
			whMovItemGrid.addColumn(codeColumn, SafeHtmlUtils.fromSafeConstant("Clave"));	
		}
		// Nombre
		Column<BmObject, String> prodColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoWhMovItem)bmObject).getBmoProduct().getName().toString(50);
			}
		};
		whMovItemGrid.setColumnWidth(prodColumn, 150, Unit.PX);
		whMovItemGrid.addColumn(prodColumn, SafeHtmlUtils.fromSafeConstant("Nombre"));

		if (!isMobile()) {
			// Modelo
			Column<BmObject, String> modelColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoWhMovItem)bmObject).getBmoProduct().getModel().toString();
				}
			};
			whMovItemGrid.addColumn(modelColumn, SafeHtmlUtils.fromSafeConstant("Modelo"));
			whMovItemGrid.setColumnWidth(modelColumn, 100, Unit.PX);
		}
		// Tipo producto
		Column<BmObject, String> trackColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoWhMovItem)bmObject).getBmoProduct().getTrack().getSelectedOption().getLabel();
			}
		};
		whMovItemGrid.addColumn(trackColumn, SafeHtmlUtils.fromSafeConstant("Rastreo"));
		whMovItemGrid.setColumnWidth(trackColumn, 70, Unit.PX);

		// Número de Serie
		Column<BmObject, String> serialColumn;
		serialColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoWhMovItem)bmObject).getSerial().toString();
			}
		};
		whMovItemGrid.addColumn(serialColumn, SafeHtmlUtils.fromSafeConstant("#Serie/Lote"));
		whMovItemGrid.setColumnWidth(serialColumn, 100, Unit.PX);
		serialColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);	
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoWhMovement = (BmoWhMovement)getBmObject();
		bmoWhMovement.getAmount().setValue(0);	
		bmoWhMovement.getStatus().setValue(statusListBox.getSelectedCode());
		return bmoWhMovement;
	}

	@Override
	public void close() {
		UiWhMovement uiWhMovementList = new UiWhMovement(getUiParams());
		uiWhMovementList.show();
	}

	@Override
	public void saveNext() {
		if (newRecord) { 
			UiWhMovementFormOut uiWhMovementForm = new UiWhMovementFormOut(getUiParams(), getBmObject().getId());
			uiWhMovementForm.show();
		} else {
			UiWhMovement uiWhMovementList = new UiWhMovement(getUiParams());
			uiWhMovementList.show();
		}		
	}
	
	@Override
	public void postShow() {
		if (bmoWhMovement.getOrderDeliveryId().toInteger() > 0)
			deleteButton.setVisible(false);
	}

	public void addProduct() {
		addProduct(new BmoProduct());
	}

	public void addProduct(BmoProduct bmoProduct) {
		whMovItemDialogBox = new DialogBox(true);
		whMovItemDialogBox.setGlassEnabled(true);
		whMovItemDialogBox.setText("Item de Pedido");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "200px");

		whMovItemDialogBox.setWidget(vp);

		UiWhMovItemForm whMovItemForm = new UiWhMovItemForm(getUiParams(), vp, bmoWhMovement.getId(), bmoProduct);

		whMovItemForm.show();

		whMovItemDialogBox.center();
		whMovItemDialogBox.show();
	}

	public void changeQuantity(BmObject bmObject, String quantity) {
		bmoWhMovItem = (BmoWhMovItem)bmObject;
		try {
			double q = Double.parseDouble(quantity);
			// Si requiere serial, primero solicitarlo
			if (!(bmoWhMovItem.getBmoProduct().getTrack().toChar() == BmoProduct.TRACK_NONE)) {
				if (bmoWhMovItem.getSerial().toString().equals("")) {
					showErrorMessage("Debe establecer primero el # de Serie.");
					reset();

					// Tiene rastreo tipo #SERIE la cantidad debe ser cero 
				} else if (bmoWhMovItem.getBmoProduct().getTrack().toChar() == BmoProduct.TRACK_SERIAL &&
						(q > 1)) {
					showErrorMessage("La Cantidad no puede ser mayor a 1.");
					reset();
					// No tiene rastreo
				} else {
					bmoWhMovItem.getQuantity().setValue(q);
					saveItemChange();
				}
			} else {
				bmoWhMovItem.getQuantity().setValue(q);
				saveItemChange();	
			}
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-changeQuantity(): ERROR " + e.toString());
			reset();
		}
	}

	public void deleteItem(BmObject bmObject) {
		bmoWhMovItem = (BmoWhMovItem)bmObject;
		deleteItem();
	}

	public void changeSerial(BmObject bmObject, String serial) {
		bmoWhMovItem = (BmoWhMovItem)bmObject;
		try {
			if (!serial.equals("")) {
				if (bmoWhMovItem.getBmoProduct().getTrack().toChar() == BmoProduct.TRACK_SERIAL) {
					bmoWhMovItem.getQuantity().setValue(1);
					bmoWhMovItem.getSerial().setValue(serial);
					saveItemChange();
				} else if (bmoWhMovItem.getBmoProduct().getTrack().toChar() == BmoProduct.TRACK_BATCH) {
					bmoWhMovItem.getSerial().setValue(serial);
					saveItemChange();
				} else {
					showErrorMessage("No aplica cambio de #Serie para este Item.");
					reset();
				}
			}
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-changeSerial(): ERROR " + e.toString());
		}
	}

	// Guardar registro, primer intento
	public void saveItemChange() {
		saveItemChange(0);
	}

	public void saveItemChange(int saveItemChangeRpcAttempt) {
		if (saveItemChangeRpcAttempt < 5) {
			setSaveItemChangeRpcAttempt(saveItemChangeRpcAttempt + 1);

			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					if (getSaveItemChangeRpcAttempt() < 5)
						saveItemChange(getSaveItemChangeRpcAttempt());
					else
						showErrorMessage(this.getClass().getName() + "-saveItemChange(): ERROR " + caught.toString());
				}

				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					setSaveItemChangeRpcAttempt(0);
					processItemChangeSave(result);
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().save(bmoWhMovItem.getPmClass(), bmoWhMovItem, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-saveItemChange(): ERROR " + e.toString());
			}
		}
	}

	public void processItemChangeSave(BmUpdateResult bmUpdateResult) {
		if (bmUpdateResult.hasErrors()) 
			showErrorMessage("Error al modificar el Item: " + bmUpdateResult.errorsToString());
		this.reset();
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
				getUiParams().getBmObjectServiceAsync().delete(bmoWhMovItem.getPmClass(), bmoWhMovItem, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-deleteItem(): ERROR " + e.toString());
		}
	}

	public void processItemDelete(BmUpdateResult result) {
		if (result.hasErrors()) {
			showErrorMessage("Error al eliminar Item : " + result.errorsToString());
		}
		this.reset();
	}

	public void reset() {
		data.list();
		whMovItemGrid.redraw();
	}

	// Agrega un item de un producto a la orden de compra
	private class UiWhMovItemForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
		private TextBox quantityTextBox = new TextBox();
		private TextBox serialTextBox = new TextBox();
		private BmoWhMovItem bmoWhMovItem;
		private Button saveButton = new Button("AGREGAR");
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		private UiSuggestBox orderItemSuggestBox;
		private BmoProduct bmoProduct;
		private BmoOrderItem bmoOrderItem = new BmoOrderItem();
		int whMovementId;

		public UiWhMovItemForm(UiParams uiParams, Panel defaultPanel, int whMovementId, BmoProduct bmoProduct) {
			super(uiParams, defaultPanel);
			this.bmoWhMovItem = new BmoWhMovItem();
			this.whMovementId = whMovementId;
			this.bmoProduct = bmoProduct;

			try {
				bmoWhMovItem.getWhMovementId().setValue(whMovementId);
				bmoWhMovItem.getProductId().setValue(bmoProduct.getId());
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "(): ERROR " + e.toString());
			}

			saveButton.setStyleName("formSaveButton");
			saveButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					prepareSave();
				}
			});
			saveButton.setEnabled(false);
			if (getSFParams().hasWrite(bmoWhMovement.getProgramCode())) saveButton.setEnabled(true);
			buttonPanel.add(saveButton);

			// Filtro del suggest box de productos
			// Es de tipo entrada. Mostrar items de la orden de compra
			// Filtro de tipos de producto

			// Filtro del suggest box de productos
			BmFilter bmFilterOrder = new BmFilter();
			bmFilterOrder.setValueFilter(bmoOrderItem.getKind(), bmoOrderItem.getBmoOrderGroup().getOrderId(), bmoWhMovement.getBmoOrderDelivery().getOrderId().toString());

			// Filtro afecta inventario
			BmFilter bmInventoryFilter = new BmFilter();
			bmInventoryFilter.setValueFilter(bmoOrderItem.getKind(), bmoOrderItem.getBmoProduct().getInventory(), 1);

			orderItemSuggestBox = new UiSuggestBox(bmoOrderItem);
			orderItemSuggestBox.addFilter(bmFilterOrder);			
			orderItemSuggestBox.addFilter(bmInventoryFilter);

			SelectionHandler<Suggestion> selectionHandler = new SelectionHandler<Suggestion>() {
				@Override
				public void onSelection(SelectionEvent<Suggestion> event) {
					setProductFromList(event);
				}
			};
			orderItemSuggestBox.addSelectionHandler(selectionHandler);

			defaultPanel.add(formTable);
		}

		public void show() {
			formTable.addField(1, 0, orderItemSuggestBox, bmoWhMovItem.getProductId());
			formTable.addField(2, 0, serialTextBox, bmoWhMovItem.getSerial());
			formTable.addField(3, 0, quantityTextBox, bmoWhMovItem.getQuantity());
			formTable.addButtonPanel(buttonPanel);
		}

		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) showErrorMessage("Error al modificar Item: " + bmUpdateResult.errorsToString());
			else {
				whMovItemDialogBox.hide();
				reset();
			}
		}

		public void prepareSave() {
			try {
				bmoWhMovItem = new BmoWhMovItem();
				bmoWhMovItem.getWhMovementId().setValue(whMovementId);
				bmoWhMovItem.getAmount().setValue(0);
				bmoWhMovItem.getProductId().setValue(bmoProduct.getId());
				bmoWhMovItem.getSerial().setValue(serialTextBox.getText());
				bmoWhMovItem.getQuantity().setValue(quantityTextBox.getText());
				save();
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-prepareSave(): ERROR " + e.toString());
			}
		}

		public void save() {
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-save(): ERROR " + caught.toString());
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
					getUiParams().getBmObjectServiceAsync().save(bmoWhMovItem.getPmClass(), bmoWhMovItem, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-save(): ERROR " + e.toString());
			}
		}

		public void setProductFromList(SelectionEvent<Suggestion> event) {
			serialTextBox.setEnabled(false);
			quantityTextBox.setEnabled(false);

			// Obtener el producto
			if (orderItemSuggestBox.getSelectedId() > 0) {	
				bmoProduct = (BmoProduct)((BmoOrderItem)orderItemSuggestBox.getSelectedBmObject()).getBmoProduct();

				// Hacer cambios por el producto
				if (bmoProduct.getTrack().toChar() == BmoProduct.TRACK_SERIAL) {
					serialTextBox.setEnabled(true);
					quantityTextBox.setText("1");
				} else if (bmoProduct.getTrack().toChar() == BmoProduct.TRACK_BATCH) {
					serialTextBox.setEnabled(true);
					quantityTextBox.setEnabled(true);
				} else {
					quantityTextBox.setEnabled(true);
				}
			}
		}
	}

	// Variables para llamadas RPC
	public int getSaveItemChangeRpcAttempt() {
		return saveItemChangeRpcAttempt;
	}

	public void setSaveItemChangeRpcAttempt(int saveItemChangeRpcAttempt) {
		this.saveItemChangeRpcAttempt = saveItemChangeRpcAttempt;
	}
}
