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

import java.sql.Types;
import com.flexwm.client.op.UiWhMovement;
import com.flexwm.shared.op.BmoWarehouse;
import com.flexwm.shared.op.BmoWhMovItem;
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoWhMovement;
import com.flexwm.shared.op.BmoWhSection;
import com.flexwm.shared.op.BmoWhStock;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiFormFlexTable;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiListDataProvider;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.client.ui.UiSuggestBoxAction;
import com.symgae.shared.BmException;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;


public class UiWhMovementFormTransfer extends UiFormDialog {	
	private FlowPanel whMovItemPanel = new FlowPanel();
	private CellTable<BmObject> whMovItemGrid = new CellTable<BmObject>();
	private UiListDataProvider<BmObject> data;
	protected DialogBox whMovItemDialogBox;
	private Button addWhMovItemButton = new Button("+ ITEM");
	private UiListBox statusListBox = new UiListBox(getUiParams());
	private UiSuggestBox toWhSectionSuggestBox = new UiSuggestBox(new BmoWhSection());
	private FlowPanel whMovementFormTransferButtonPanel = new FlowPanel();
	private String barcode = "";
	private int searchBarcodeRpcAttempt = 0;
	private int saveItemChangeRpcAttempt = 0;
	private int stockQuantityRpcAttempt = 0;
	private String companyIdWarehouseRpc = "";

	BmoWhSection bmoWhSection = new BmoWhSection();
	BmoWhMovItem bmoWhMovItem = new BmoWhMovItem();
	BmoWhMovement bmoWhMovement;

	String itemsSection = "Items";
	String statusSection = "Estatus";

	// Ingreso por codigo de barras
	private Button barcodeDialogButton = new Button("SCANNER");
	protected DialogBox barcodeDialogBox;

	public UiWhMovementFormTransfer(UiParams uiParams, int id) {
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

		// Pantalla de ingreso por codigo de barras
		barcodeDialogButton.setStyleName("formSaveButton");
		barcodeDialogButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				barcodeDialog();
			}
		});

		// Grid de items
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoWhMovItem.getKind(), bmoWhMovItem.getWhMovementId().getName(), id);
		data = new UiListDataProvider<BmObject>(new BmoWhMovItem(), bmFilter);

		whMovItemGrid.setWidth("100%");
		whMovItemPanel.setWidth("100%");

		data.addDataDisplay(whMovItemGrid);
		whMovItemPanel.add(whMovItemGrid);
	}

	@Override
	public void populateFields() {
		bmoWhMovement = (BmoWhMovement)getBmObject();

		formFlexTable.addLabelField(1, 0, bmoWhMovement.getCode());
		formFlexTable.addLabelField(2, 0, bmoWhMovement.getType());
		formFlexTable.addLabelField(3, 0, bmoWhMovement.getDatemov());
		formFlexTable.addLabelField(4, 0, bmoWhMovement.getDescription());				
		formFlexTable.addLabelField(5, 0, bmoWhMovement.getCompanyId().getLabel(), bmoWhMovement.getBmoCompany().getName().toString());
		formFlexTable.addField(6, 0, toWhSectionSuggestBox, bmoWhMovement.getToWhSectionId());

		formFlexTable.addSectionLabel(7, 0, itemsSection, 2);
		formFlexTable.addPanel(8, 0, whMovementFormTransferButtonPanel);
		whMovementFormTransferButtonPanel.clear();
		whMovementFormTransferButtonPanel.add(addWhMovItemButton);
		whMovementFormTransferButtonPanel.add(barcodeDialogButton);
		formFlexTable.addPanel(9, 0, whMovItemPanel, 2);

		formFlexTable.addSectionLabel(10, 0, statusSection, 2);
		formFlexTable.addField(11, 0, statusListBox, bmoWhMovement.getStatus());

		reset();
		statusEffect();
	}

	public void reset() {
		// Elimina columnas del grid
		while (whMovItemGrid.getColumnCount() > 0)
			whMovItemGrid.removeColumn(0);

		// Crea las columnas
		setColumns();

		data.list();
		whMovItemGrid.redraw();
	}

	public void changeHeight() {
		whMovItemGrid.setVisibleRange(0, data.getList().size());
	}

	public void setColumns() {
		// Cantidad
		Column<BmObject, String> quantityColumn;
		if (bmoWhMovement.getStatus().equals(BmoWhMovement.STATUS_REVISION)) {
			quantityColumn = new Column<BmObject, String>(new EditTextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoWhMovItem)bmObject).getQuantity().toString();
				}
			};
			quantityColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					changeQuantity(bmObject, value);
				}
			});
		} else {
			quantityColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoWhMovItem)bmObject).getQuantity().toString();
				}
			};
		}
		whMovItemGrid.addColumn(quantityColumn, SafeHtmlUtils.fromSafeConstant("Cant."));
		whMovItemGrid.setColumnWidth(quantityColumn, 50, Unit.PX);
		quantityColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

		// Nombre
		Column<BmObject, String> prodColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoWhMovItem)bmObject).getBmoProduct().getCode().toString() + " " + 
						((BmoWhMovItem)bmObject).getBmoProduct().getName().toString(50);
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

		if (!isMobile()) {
			// S. Almacén Origen
			Column<BmObject, String> fromWhSectionColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoWhMovItem)bmObject).getBmoFromWhSection().getName().toString();
				}
			};
			whMovItemGrid.addColumn(fromWhSectionColumn, SafeHtmlUtils.fromSafeConstant("Origen"));
			whMovItemGrid.setColumnWidth(fromWhSectionColumn, 50, Unit.PX);
		}

		// Eliminar
		Column<BmObject, String> deleteColumn;
		if (bmoWhMovement.getStatus().equals(BmoWhMovement.STATUS_REVISION)) {
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
		whMovItemGrid.addColumn(deleteColumn, SafeHtmlUtils.fromSafeConstant("Eliminar"));
		deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		whMovItemGrid.setColumnWidth(deleteColumn, 50, Unit.PX);	
	}

	@Override
	public void formValueChange(String value) {
		statusEffect();
	}

	@Override
	public void formListChange(ChangeEvent event) {
		if (event.getSource() == statusListBox)
			update("Desea cambiar el Status de la Transferencia?");
	}

	public void statusEffect() {
		addWhMovItemButton.setVisible(false);
		barcodeDialogButton.setVisible(false);
		toWhSectionSuggestBox.setEnabled(false);

		if (newRecord) {
			toWhSectionSuggestBox.setEnabled(true);
		}

		if (bmoWhMovement.getStatus().equals(BmoWhMovement.STATUS_REVISION)) {
			addWhMovItemButton.setVisible(true);			
			barcodeDialogButton.setVisible(true);
		}
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
			UiWhMovementFormTransfer uiWhMovementForm = new UiWhMovementFormTransfer(getUiParams(), getBmObject().getId());
			uiWhMovementForm.show();
		} else {
			UiWhMovement uiWhMovementList = new UiWhMovement(getUiParams());
			uiWhMovementList.show();
		}		
	}

	public void addProduct() {
		addProduct(new BmoProduct());
	}

	public void addProduct(BmoProduct bmoProduct) {
		whMovItemDialogBox = new DialogBox(true);
		whMovItemDialogBox.setGlassEnabled(true);
		whMovItemDialogBox.setText("Item de Iventario");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "200px");

		whMovItemDialogBox.setWidget(vp);

		UiWhMovItemForm whMovItemForm = new UiWhMovItemForm(getUiParams(), vp, bmoWhMovement.getId(), bmoProduct);

		whMovItemForm.show();

		whMovItemDialogBox.center();
		whMovItemDialogBox.show();
	}

	public void barcodeDialog() {
		barcodeDialogBox = new DialogBox(true);
		barcodeDialogBox.setGlassEnabled(true);
		barcodeDialogBox.setText("Scanner Código de Barras");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "200px");

		barcodeDialogBox.setWidget(vp);

		UiBarcodeForm uiBarcodeForm = new UiBarcodeForm(getUiParams(), vp, bmoWhMovement);
		uiBarcodeForm.show();

		barcodeDialogBox.center();
		barcodeDialogBox.show();
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

	// Agrega un item de un producto a la orden de compra
	private class UiWhMovItemForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
		private TextBox quantityTextBox = new TextBox();
		private TextBox serialTextBox = new TextBox();
		private Label stockLabel = new Label();
		private BmoWhMovItem bmoWhMovItem;
		private Button saveButton = new Button("AGREGAR");
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		private UiSuggestBox whStockSuggestBox;
		private UiSuggestBox fromWhSectionSuggestBox = new UiSuggestBox(new BmoWhSection());
		private BmoProduct bmoProduct;
		private BmoWhStock bmoWhStock = new BmoWhStock();
		private BmoWhSection bmoWhSection = new BmoWhSection();
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

			// Filtro de cantidades de producto en inventario
			BmFilter bmFilterStock = new BmFilter();
			bmFilterStock.setValueOperatorFilter(bmoWhStock.getKind(), bmoWhStock.getQuantity(), "" + BmFilter.MAYOR, 0);

			// Filtro afecta inventario
			BmFilter bmInventoryFilter = new BmFilter();
			bmInventoryFilter.setValueFilter(bmoWhStock.getKind(), bmoWhStock.getBmoProduct().getInventory(), 1);

			// Filtro de almacen origen distinto al destino
			BmFilter bmFromWhSectionFilter = new BmFilter();
			bmFromWhSectionFilter.setValueOperatorFilter(bmoWhStock.getKind(), bmoWhStock.getWhSectionId(), BmFilter.NOTEQUALS, bmoWhMovement.getToWhSectionId().toInteger());

			// Filtro empresa de almacen
			BmFilter bmFilterCompanyWarehouse = new BmFilter();
			bmFilterCompanyWarehouse.setValueFilter(bmoWhStock.getKind(), bmoWhStock.getBmoWhSection().getBmoWarehouse().getCompanyId(), bmoWhMovement.getCompanyId().toInteger());

			whStockSuggestBox = new UiSuggestBox(bmoWhStock);
			whStockSuggestBox.addFilter(bmFilterStock);
			whStockSuggestBox.addFilter(bmInventoryFilter);
			whStockSuggestBox.addFilter(bmFromWhSectionFilter);
			whStockSuggestBox.addFilter(bmFilterCompanyWarehouse);

			// Filtrar secciones de almacen normales
			BmFilter bmFilterWhSections = new BmFilter();
			bmFilterWhSections.setValueFilter(bmoWhSection.getKind(), bmoWhSection.getBmoWarehouse().getType(), 
					"" + BmoWarehouse.TYPE_NORMAL);

			// Filtro de almacen origen distinto al destino
			BmFilter bmFromWhSectionFilter2 = new BmFilter();
			bmFromWhSectionFilter2.setValueOperatorFilter(bmoWhSection.getKind(), bmoWhSection.getIdField(), BmFilter.NOTEQUALS, bmoWhMovement.getToWhSectionId().toInteger());

			fromWhSectionSuggestBox.addFilter(bmFilterWhSections);
			fromWhSectionSuggestBox.addFilter(bmFromWhSectionFilter2);

			// Manejo de acciones de suggest box
			UiSuggestBoxAction uiSuggestBoxAction = new UiSuggestBoxAction() {
				@Override
				public void onSelect(UiSuggestBox uiSuggestBox) {
					formSuggestionChange(uiSuggestBox);
				}
			};
			formTable.setUiSuggestBoxAction(uiSuggestBoxAction);

			ChangeHandler listChangeHandler = new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					formListChange(event);
				}
			};
			formTable.setListChangeHandler(listChangeHandler);

			defaultPanel.add(formTable);
		}

		public void show() {
			formTable.addField(1, 0, whStockSuggestBox, bmoWhMovItem.getProductId());
			formTable.addField(2, 0, fromWhSectionSuggestBox, bmoWhMovItem.getFromWhSectionId());
			fromWhSectionSuggestBox.setEnabled(false);
			formTable.addLabelField(3, 0, "Existencias", stockLabel);
			formTable.addField(4, 0, serialTextBox, bmoWhMovItem.getSerial());
			formTable.addField(5, 0, quantityTextBox, bmoWhMovItem.getQuantity());
			formTable.addButtonPanel(buttonPanel);

			// Asigna foco al campo
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					whStockSuggestBox.setFocus(true);                
				}
			});
		}

		public void formSuggestionChange(UiSuggestBox uiSuggestBox) {
			if (uiSuggestBox == whStockSuggestBox) {
				if (whStockSuggestBox.getSelectedId() > 0) {
					bmoWhStock = (BmoWhStock)whStockSuggestBox.getSelectedBmObject();
					serialTextBox.setText(bmoWhStock.getBmoWhTrack().getSerial().toString());
					serialTextBox.setEnabled(false);
					quantityTextBox.setEnabled(false);

					// Obtener el producto
					bmoProduct = (BmoProduct)((BmoWhStock)
							whStockSuggestBox.getSelectedBmObject()).getBmoProduct();

					// Hacer cambios por el producto
					if (bmoProduct.getTrack().toChar() == BmoProduct.TRACK_SERIAL) {
						quantityTextBox.setEnabled(false);
						quantityTextBox.setText("1");
					} else {
						quantityTextBox.setEnabled(true);
					} 

					// Asigna la seccion desde
					try {
						bmoWhMovItem.getFromWhSectionId().setValue(bmoWhStock.getWhSectionId().toInteger());
					} catch (BmException e) {
						showFormMsg("Error al asignar valor al campo " + bmoWhMovement.getToWhSectionId().getLabel(), "Error al asignar valor al campo " + bmoWhMovement.getToWhSectionId().getLabel());
					}

					formTable.addField(2, 0, fromWhSectionSuggestBox, bmoWhMovItem.getFromWhSectionId());
					//fromWhSectionSuggestBox.setEnabled(true);

					getStockQuantity(bmoWhStock.getBmoWhSection().getBmoWarehouse().getCompanyId().toString());

				} else {
					formTable.addFieldEmpty(2, 0);
					stockLabel.setText("");
					fromWhSectionSuggestBox.setEnabled(false);
				}
			}
			else if (uiSuggestBox == fromWhSectionSuggestBox) {
				if (fromWhSectionSuggestBox.getSelectedId() > 0) {
					bmoWhStock = (BmoWhStock)whStockSuggestBox.getSelectedBmObject();
					try {
						bmoWhStock.getWhSectionId().setValue(fromWhSectionSuggestBox.getSelectedId());
					} catch (BmException e) {
						showFormMsg("Error al asignar valor al campo " + bmoWhMovement.getToWhSectionId().getLabel(), "Error al asignar valor al campo " + bmoWhMovement.getToWhSectionId().getLabel());
					}
					getStockQuantity(bmoWhStock.getBmoWhSection().getBmoWarehouse().getCompanyId().toString());
				}
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
				bmoWhMovItem.getFromWhSectionId().setValue(fromWhSectionSuggestBox.getSelectedId());
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

		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) showErrorMessage("Error al modificar Item: " + bmUpdateResult.errorsToString());
			else {
				whMovItemDialogBox.hide();
				reset();
			}
		}

		//Obtener la cantidad en almacen, primer intento
		public void getStockQuantity(String companyIdWarehouseRpc) {
			getStockQuantity(companyIdWarehouseRpc, 0);
		}

		//Obtener la cantidad en almacen
		public void getStockQuantity(String companyIdWarehouseRpc, int stockQuantityRpcAttempt) {
			if (stockQuantityRpcAttempt < 5) {
				setStockQuantityRpcAttempt(stockQuantityRpcAttempt + 1);
				setCompanyIdWarehouseRpc(companyIdWarehouseRpc);

				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getStockQuantityRpcAttempt() < 5)
							getStockQuantity(getCompanyIdWarehouseRpc(), getStockQuantityRpcAttempt());
						else
							showErrorMessage(this.getClass().getName() + "-getStockQuantity() ERROR: " + caught.toString());
					}

					public void onSuccess(BmUpdateResult result) {
						stopLoading();
						setStockQuantityRpcAttempt(0);
						BmoWhStock bmoWhStock = (BmoWhStock)result.getBmObject();
						stockLabel.setText(bmoWhStock.getQuantity().toString());
					}
				};

				try {
					if (!isLoading()) {
						stockLabel.setText("");
						startLoading();
						getUiParams().getBmObjectServiceAsync().action(bmoWhStock.getPmClass(), bmoWhStock, BmoWhStock.ACTION_STOCKQUANTITY, bmoWhStock.getProductId().toString() + "|" +  companyIdWarehouseRpc, callback);	
					}
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getStockQuantity() ERROR: " + e.toString());
				}
			}
		} 
	}

	// Escanea codigo de barras y asigna cantidades a los items de la recepción
	private class UiBarcodeForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());		
		private Button saveButton = new Button("GUARDAR");
		private Button closeButton = new Button("CERRAR");
		private HorizontalPanel buttonPanel = new HorizontalPanel();

		protected ValueChangeHandler<String> textChangeHandler;
		private TextBox barcodeTextBox = new TextBox();
		private TextBox serialTextBox = new TextBox();
		private TextBox quantityTextBox = new TextBox();
		private Label productLabel = new Label();
		private Label stockLabel = new Label();

		private UiListBox whStockListBox;

		private BmoWhMovement bmoWhMovement;
		private BmoWhMovItem bmoWhMovItem = new BmoWhMovItem();
		private BmoWhStock bmoWhStock = new BmoWhStock();

		private UiSuggestBox toWhSectionSuggestBox;
		private UiSuggestBox fromWhSectionSuggestBox;

		BmField barcodeTextBoxField;

		public UiBarcodeForm(UiParams uiParams, Panel defaultPanel, BmoWhMovement bmoWhMovement) {
			super(uiParams, defaultPanel);
			this.bmoWhMovement = bmoWhMovement;

			barcodeTextBoxField = new BmField("barcodeTextBoxField", "", "Item Transfer", 40, Types.VARCHAR, false, BmFieldType.STRING, false);

			saveButton.setStyleName("formSaveButton");
			saveButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					prepareSave();
				}
			});
			saveButton.setEnabled(false);
			if (getSFParams().hasWrite(bmoWhMovement.getProgramCode())) saveButton.setEnabled(true);
			buttonPanel.add(saveButton);

			closeButton.setStyleName("formCloseButton");
			closeButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					closeBarcodeDialog();
				}
			});
			buttonPanel.add(closeButton);

			// Dispara accion del codigo de barras
			textChangeHandler = new ValueChangeHandler<String>() {
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					changeBarcode(event);
				}
			};
			barcodeTextBox.addValueChangeHandler(textChangeHandler);

			// Dispara accion de listado
			listChangeHandler = new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					whStockformListChange(event);
				}
			};
			formTable.setListChangeHandler(listChangeHandler);

			defaultPanel.add(formTable);
		}

		public void show() {
			formTable.addField(1, 0, barcodeTextBox, barcodeTextBoxField);
			formTable.addFieldEmpty(2, 0);
			formTable.addFieldEmpty(3, 0);
			formTable.addFieldEmpty(4, 0);
			formTable.addField(5, 0, serialTextBox, bmoWhMovItem.getSerial());
			formTable.addLabelField(6, 0, "Producto / Item:", productLabel);
			formTable.addField(7, 0, quantityTextBox, bmoWhMovItem.getQuantity());
			formTable.addButtonPanel(buttonPanel);

			serialTextBox.setEnabled(false);
			setBarcodeFocus();
		}

		public void setBarcodeFocus() {
			// Asigna foco al campo
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					barcodeTextBox.setFocus(true);                
				}
			});
		}

		public void changeBarcode(ValueChangeEvent<String> event) {
			searchBarcode(barcodeTextBox.getText());
		}

		//Obtener la cantidad en almacen
		private void searchBarcode(String barcode) {
			searchBarcode(barcode, 0);
		}

		//Obtener la cantidad en almacen
		private void searchBarcode(String barcode, int searchBarcodeRpcAttempt) {
			if (searchBarcodeRpcAttempt < 5) {
				setBarcode(barcode);
				setSearchBarcodeRpcAttempt(searchBarcodeRpcAttempt + 1);

				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getSearchBarcodeRpcAttempt() < 5)
							searchBarcode(getBarcode(), getSearchBarcodeRpcAttempt());
						else
							showErrorMessage(this.getClass().getName() + "-searchBarcode() ERROR: " + caught.toString());
					}

					public void onSuccess(BmUpdateResult result) {
						stopLoading();
						setSearchBarcodeRpcAttempt(0);
						processBarcodeSearchResult((BmUpdateResult)result);
					}
				};

				try {	
					if (!isLoading()) {
						startLoading();
						getUiParams().getBmObjectServiceAsync().action(bmoWhStock.getPmClass(), bmoWhStock, BmoWhStock.ACTION_SEARCHBARCODE, barcode, callback);
					}
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getStockQuantity() ERROR: " + e.toString());
				}
			}
		} 

		public void processBarcodeSearchResult(BmUpdateResult bmUpdateResult) {

			if (bmUpdateResult.hasErrors()) {
				bmoWhMovItem = new BmoWhMovItem();
				bmoWhStock = new BmoWhStock();
				barcodeTextBox.setText("");
				productLabel.setText("");
				stockLabel.setText("");
				formTable.displayError(bmoWhMovItem.getIdField(), "El Código de Barras no fue Encontrado.");
				setBarcodeFocus();

			} else {
				bmoWhStock = (BmoWhStock)bmUpdateResult.getBmObject();
				productLabel.setText(bmoWhStock.getBmoProduct().getCode().toString() + " " + bmoWhStock.getBmoProduct().getName().toString());
				stockLabel.setText(bmoWhStock.getQuantity().toString());
				quantityTextBox.setText("");

				// Asigna los valores al item del movimiento
				try {
					bmoWhMovItem.getProductId().setValue(bmoWhStock.getProductId().toInteger());
					bmoWhMovItem.getFromWhSectionId().setValue(bmoWhStock.getWhSectionId().toInteger());
				} catch (BmException e) {
					showSystemMessage(this.getClass().getName() + "-processBarcodeSearchResult(): " + e.toString());
				}

				// Depende el tipo de rastreo
				if (bmoWhStock.getBmoProduct().getTrack().equals(BmoProduct.TRACK_NONE)) {
					serialTextBox.setText("");
					quantityTextBox.setEnabled(true);
					quantityTextBox.setFocus(true);

					// Si no esta asignada la cantidad, elegir fuente
					// Filtro de existencias
					BmFilter filterByProduct = new BmFilter();
					filterByProduct.setValueFilter(bmoWhStock.getKind(), bmoWhStock.getProductId(), bmoWhStock.getProductId().toInteger());

					// Filtro de tipos de almacen
					BmFilter filterByWarehouseType = new BmFilter();
					filterByWarehouseType.setValueFilter(bmoWhStock.getKind(), bmoWhStock.getBmoWhSection().getBmoWarehouse().getType(), "" + BmoWarehouse.TYPE_NORMAL);

					// Filtro almacen origen distinto
					BmFilter filterByWhSection = new BmFilter();
					filterByWhSection.setValueOperatorFilter(bmoWhStock.getKind(), bmoWhStock.getWhSectionId(), BmFilter.NOTEQUALS, bmoWhMovement.getToWhSectionId().toInteger());

					whStockListBox = new UiListBox(getUiParams(), new BmoWhStock());
					whStockListBox.addFilter(filterByProduct);					
					whStockListBox.addFilter(filterByWarehouseType);
					whStockListBox.addFilter(filterByWhSection);

					// Listado de existencias
					formTable.addField(2, 0, whStockListBox, bmoWhStock.getIdField());
					whStockListBox.setEnabled(true);

					updateWhSections();

				} 
				// Rastro tipo lotes
				else if (bmoWhStock.getBmoProduct().getTrack().equals(BmoProduct.TRACK_BATCH)) {
					serialTextBox.setText(bmoWhStock.getBmoWhTrack().getSerial().toString());
					quantityTextBox.setEnabled(true);

					// Si la busqueda fue de numero de serie, asignarlo y guardar
					if (!barcodeTextBox.getText().toUpperCase().equals(bmoWhStock.getBmoProduct().getCode().toString().toUpperCase())) {
						serialTextBox.setText(barcodeTextBox.getText().toUpperCase());
						quantityTextBox.setFocus(true);

						// Filtro de existencias
						BmFilter filterBySerial = new BmFilter();
						filterBySerial.setValueFilter(bmoWhStock.getKind(), bmoWhStock.getBmoWhTrack().getSerial(), barcodeTextBox.getText().toUpperCase());

						// Filtro de tipos de almacen
						BmFilter filterByWarehouseType = new BmFilter();
						filterByWarehouseType.setValueFilter(bmoWhStock.getKind(), bmoWhStock.getBmoWhSection().getBmoWarehouse().getType(), "" + BmoWarehouse.TYPE_NORMAL);

						// Filtro almacen origen distinto
						BmFilter filterByWhSection = new BmFilter();
						filterByWhSection.setValueOperatorFilter(bmoWhStock.getKind(), bmoWhStock.getWhSectionId(), BmFilter.NOTEQUALS, bmoWhMovement.getToWhSectionId().toInteger());

						whStockListBox = new UiListBox(getUiParams(), new BmoWhStock());
						whStockListBox.addFilter(filterBySerial);
						whStockListBox.addFilter(filterByWarehouseType);
						whStockListBox.addFilter(filterByWhSection);

						// Listado de existencias
						formTable.addField(2, 0, whStockListBox, bmoWhStock.getIdField());
						whStockListBox.setEnabled(true);

						serialTextBox.setEnabled(true);

						updateWhSections();

					} else { 
						serialTextBox.setText("");
						serialTextBox.setEnabled(true);
						serialTextBox.setFocus(true);
					}

				} 
				// Rastreo tipo Serie
				else if (bmoWhStock.getBmoProduct().getTrack().equals(BmoProduct.TRACK_SERIAL)) {					

					// Asigna valores default
					quantityTextBox.setText("1");
					quantityTextBox.setEnabled(false);

					// Si la busqueda fue de numero de serie, asignarlo y guardar
					if (!barcodeTextBox.getText().toUpperCase().equals(bmoWhStock.getBmoProduct().getCode().toString().toUpperCase())) {
						serialTextBox.setText(barcodeTextBox.getText().toUpperCase());	

						// Actualiza secciones
						updateWhSections();

						prepareSave();

					} else { 
						serialTextBox.setText("");	
						serialTextBox.setEnabled(true);
						serialTextBox.setFocus(true);
					}
				}

				barcodeTextBox.setText(barcodeTextBox.getText().toUpperCase());

			}
		}

		// Si se cambia el listado de whstocks activar evento
		public void whStockformListChange(ChangeEvent event) {
			if (event.getSource() == whStockListBox) {

				// Actualiza el origen
				if (!whStockListBox.getSelectedId().equals("")) {
					BmoWhStock bmoWhStock = (BmoWhStock)whStockListBox.getSelectedBmObject();
					try {
						bmoWhMovItem.getFromWhSectionId().setValue(bmoWhStock.getWhSectionId().toInteger());
					} catch (BmException e) {
						showSystemMessage(this.getClass().getName() + "-whStockformListChange(): " + e.toString());
					}	
				}

				updateWhSections();
			}
		}

		// Actualiza secciones
		private void updateWhSections() {
			// Sección Origen
			fromWhSectionSuggestBox = new UiSuggestBox(new BmoWhSection());
			formTable.addField(3, 0, fromWhSectionSuggestBox, bmoWhMovItem.getFromWhSectionId());
			fromWhSectionSuggestBox.setEnabled(false);

			// Sección Destino
			toWhSectionSuggestBox = new UiSuggestBox(new BmoWhSection());
			try {
				bmoWhMovItem.getToWhSectionId().setValue(bmoWhMovement.getToWhSectionId().toInteger());
			} catch (BmException e) {
				showSystemMessage(this.getClass().getName() + "-updateWhSections(): " + e.toString());
			}	
			formTable.addField(4, 0, toWhSectionSuggestBox, bmoWhMovItem.getToWhSectionId());
			toWhSectionSuggestBox.setEnabled(false);

		}

		public void prepareSave() {
			try {
				bmoWhMovItem.getSerial().setValue(serialTextBox.getText());
				bmoWhMovItem.getQuantity().setValue(quantityTextBox.getText());
				bmoWhMovItem.getWhMovementId().setValue(bmoWhMovement.getId());

				bmoWhMovItem.getFromWhSectionId().setValue(fromWhSectionSuggestBox.getSelectedId());				
				bmoWhMovItem.getToWhSectionId().setValue(toWhSectionSuggestBox.getSelectedId());

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

		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) 
				showSystemMessage("Error al modificar Item: " + bmUpdateResult.errorsToString());
			else {
				barcodeTextBox.setText("");
				serialTextBox.setText("");
				serialTextBox.setEnabled(false);
				quantityTextBox.setText("");
				barcodeTextBox.setFocus(true);

				productLabel.setText("");
				stockLabel.setText("");

				formTable.addFieldEmpty(2, 0);
				formTable.addFieldEmpty(3, 0);
				formTable.addFieldEmpty(4, 0);

				reset();
			}
		}

		private void closeBarcodeDialog() {
			barcodeDialogBox.setVisible(false);
		}
	}

	// Variables para llamadas RPC
	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public int getSearchBarcodeRpcAttempt() {
		return searchBarcodeRpcAttempt;
	}

	public void setSearchBarcodeRpcAttempt(int searchBarcodeRpcAttempt) {
		this.searchBarcodeRpcAttempt = searchBarcodeRpcAttempt;
	}

	public int getSaveItemChangeRpcAttempt() {
		return saveItemChangeRpcAttempt;
	}

	public void setSaveItemChangeRpcAttempt(int saveItemChangeRpcAttempt) {
		this.saveItemChangeRpcAttempt = saveItemChangeRpcAttempt;
	}

	public int getStockQuantityRpcAttempt() {
		return stockQuantityRpcAttempt;
	}

	public void setStockQuantityRpcAttempt(int stockQuantityRpcAttempt) {
		this.stockQuantityRpcAttempt = stockQuantityRpcAttempt;
	}
	
	public String getCompanyIdWarehouseRpc() {
		return companyIdWarehouseRpc;
	}

	public void setCompanyIdWarehouseRpc(String companyIdWarehouseRpc) {
		this.companyIdWarehouseRpc = companyIdWarehouseRpc;
	}

}
