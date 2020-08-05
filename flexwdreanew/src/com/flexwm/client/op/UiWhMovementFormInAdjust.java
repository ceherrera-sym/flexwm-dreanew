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
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoProductCompany;
import com.flexwm.shared.op.BmoWhMovItem;
import com.flexwm.shared.op.BmoWhMovement;
import com.flexwm.shared.op.BmoWhSection;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiFormFlexTable;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiListDataProvider;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.client.ui.UiSuggestBoxAction;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.sf.BmoCompany;


public class UiWhMovementFormInAdjust extends UiFormDialog {
	private FlowPanel whMovItemPanel = new FlowPanel();
	private CellTable<BmObject> whMovItemGrid = new CellTable<BmObject>();
	private UiListDataProvider<BmObject> data;
	protected DialogBox whMovItemDialogBox;
	private Button addWhMovItemButton = new Button("+ ITEM");
	private UiListBox statusListBox = new UiListBox(getUiParams());
	private UiSuggestBox toWhSectionSuggestBox = new UiSuggestBox(new BmoWhSection());
	private FlowPanel whMovementFormInAjustButtonPanel = new FlowPanel();
	private int saveItemChangeRpcAttempt = 0;

	String itemsSection = "Items";
	String statusSection = "Estatus";

	BmoWhSection bmoWhSection = new BmoWhSection();
	BmoWhMovItem bmoWhMovItem = new BmoWhMovItem();
	BmoWhMovement bmoWhMovement;

	public UiWhMovementFormInAdjust(UiParams uiParams, int id) {
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

		// Todas las operaciones con los items
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
		formFlexTable.addPanel(8, 0, whMovementFormInAjustButtonPanel);
		whMovementFormInAjustButtonPanel.clear();
		whMovementFormInAjustButtonPanel.add(addWhMovItemButton);
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
	public void formListChange(ChangeEvent event) {
		if (event.getSource() == statusListBox)
			update("Desea cambiar el Status de la Entrada de Almacén?");
	}

	@Override
	public void formValueChange(String value) {
		statusEffect();
	}

	public void statusEffect() {
		addWhMovItemButton.setVisible(false);
		toWhSectionSuggestBox.setEnabled(false);

		if (newRecord)
			toWhSectionSuggestBox.setEnabled(true);

		if (bmoWhMovement.getStatus().equals(BmoWhMovement.STATUS_REVISION)) {
			addWhMovItemButton.setVisible(true);
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
			UiWhMovementFormInAdjust uiWhMovementForm = new UiWhMovementFormInAdjust(getUiParams(), getBmObject().getId());
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
		whMovItemDialogBox.setText("Item Entrada");

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

	// Agrega un item de un producto
	private class UiWhMovItemForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
		private TextBox quantityTextBox = new TextBox();
		private TextBox serialTextBox = new TextBox();
		private BmoWhMovItem bmoWhMovItem;
		private Button saveButton = new Button("AGREGAR");
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		private UiSuggestBox productSuggestBox;
		private BmoProduct bmoProduct;
		private BmoProductCompany bmoProductCompany = new BmoProductCompany();
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
			// Filtro de tipos de producto
			BmFilter bmFilterType = new BmFilter();
			bmFilterType.setValueOrOrFilter(bmoProduct.getKind(), 
					bmoProduct.getType().getName(), 
					"" + BmoProduct.TYPE_PRODUCT, 
					"" + BmoProduct.TYPE_SUBPRODUCT,
					"" + BmoProduct.TYPE_COMPLEMENTARY);

			// Filtro afecta inventario
			BmFilter bmInventoryFilter = new BmFilter();
			bmInventoryFilter.setValueFilter (bmoProduct.getKind(), bmoProduct.getInventory(), 1);

			// Filtro empresa del producto
			BmoCompany bmoCompany = new BmoCompany();
			if (getSFParams().restrictData(bmoCompany.getProgramCode())) {
				BmFilter bmProductCompanyFilter = new BmFilter();
				bmProductCompanyFilter.setInFilter(bmoProductCompany.getKind(), bmoProduct.getIdFieldName(), bmoProductCompany.getProductId().getName(), bmoProductCompany.getCompanyId().getName(), bmoWhMovement.getCompanyId().toString());
				productSuggestBox.addFilter(bmProductCompanyFilter);
			}

			productSuggestBox = new UiSuggestBox(bmoProduct);
			productSuggestBox.addFilter(bmFilterType);
			productSuggestBox.addFilter(bmInventoryFilter);

			// Manejo de acciones de suggest box
			UiSuggestBoxAction uiSuggestBoxAction = new UiSuggestBoxAction() {
				@Override
				public void onSelect(UiSuggestBox uiSuggestBox) {
					setProductFromList(uiSuggestBox);
				}
			};
			formTable.setUiSuggestBoxAction(uiSuggestBoxAction);

			defaultPanel.add(formTable);
		}

		public void show() {
			formTable.addField(1, 0, productSuggestBox, bmoWhMovItem.getProductId());
			formTable.addField(2, 0, serialTextBox, bmoWhMovItem.getSerial());
			formTable.addField(3, 0, quantityTextBox, bmoWhMovItem.getQuantity());
			formTable.addButtonPanel(buttonPanel);
		}

		public void setProductFromList(UiSuggestBox uiSuggestBox) {
			serialTextBox.setEnabled(false);
			quantityTextBox.setEnabled(false);

			// Obtener el producto
			if (productSuggestBox.getSelectedId() > 0) {	
				bmoProduct = (BmoProduct)productSuggestBox.getSelectedBmObject();

				// Hacer cambios por el producto
				if (bmoProduct.getTrack().toChar() == BmoProduct.TRACK_SERIAL) {
					serialTextBox.setEnabled(true);
					serialTextBox.setFocus(true);
					quantityTextBox.setText("1");
				} else if (bmoProduct.getTrack().toChar() == BmoProduct.TRACK_BATCH) {
					serialTextBox.setEnabled(true);
					serialTextBox.setFocus(true);
					quantityTextBox.setEnabled(true);
				} else {
					quantityTextBox.setEnabled(true);
					quantityTextBox.setFocus(true);
				}
			}
		}

		public void prepareSave() {
			try {
				bmoWhMovItem = new BmoWhMovItem();
				bmoWhMovItem.getWhMovementId().setValue(whMovementId);
				bmoWhMovItem.getAmount().setValue(0);
				bmoWhMovItem.getProductId().setValue(productSuggestBox.getSelectedId());
				bmoWhMovItem.getSerial().setValue(serialTextBox.getText());
				bmoWhMovItem.getQuantity().setValue(quantityTextBox.getText());
				bmoWhMovItem.getToWhSectionId().setValue(bmoWhMovement.getToWhSectionId().toInteger());
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
	}

	// Variables para llamadas RPC
	public int getSaveItemChangeRpcAttempt() {
		return saveItemChangeRpcAttempt;
	}

	public void setSaveItemChangeRpcAttempt(int saveItemChangeRpcAttempt) {
		this.saveItemChangeRpcAttempt = saveItemChangeRpcAttempt;
	}
}
