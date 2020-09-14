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

import com.symgae.shared.BmException;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFException;
import com.symgae.shared.sf.BmoCompany;
import java.sql.Types;
import java.util.Date;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoProject;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderDelivery;
import com.flexwm.shared.op.BmoOrderDeliveryItem;
import com.flexwm.shared.op.BmoOrderItem;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoWarehouse;
import com.flexwm.shared.op.BmoWhBox;
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
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent.LoadingState;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiFormFlexTable;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiListDataProvider;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.client.ui.UiSuggestBoxAction;


public class UiOrderDelivery extends UiList {
	BmoOrderDelivery bmoOrderDelivery = new BmoOrderDelivery();
	BmoProject bmoProject = new BmoProject();
	BmoOrder bmoOrder = new BmoOrder();

	public UiOrderDelivery(UiParams uiParams) {
		super(uiParams, new BmoOrderDelivery());
	}

	public UiOrderDelivery(UiParams uiParams, BmoOrder bmoOrder) {
		super(uiParams, new BmoOrderDelivery());
		this.bmoOrder = bmoOrder;
	}

	public UiOrderDelivery(UiParams uiParams, BmoProject bmoProject) {
		super(uiParams, new BmoOrderDelivery());
		this.bmoProject = bmoProject;
	}

	@Override
	public void postShow() {
		if (isMaster()) {
			addFilterSuggestBox(new UiSuggestBox(new BmoCustomer()), new BmoCustomer(), bmoOrderDelivery.getCustomerId());
			addStaticFilterListBox(new UiListBox(getUiParams(), bmoOrderDelivery.getType()), bmoOrderDelivery, bmoOrderDelivery.getType());
			if (!isMobile()) 
				addStaticFilterListBox(new UiListBox(getUiParams(), bmoOrderDelivery.getPayment()), bmoOrderDelivery, bmoOrderDelivery.getPayment());
			addStaticFilterListBox(new UiListBox(getUiParams(), bmoOrderDelivery.getStatus()), bmoOrderDelivery, bmoOrderDelivery.getStatus());

		}
	}

	@Override
	public void create() {
		UiOrderDeliveryForm uiOrderDeliveryForm = new UiOrderDeliveryForm(getUiParams(), 0);
		uiOrderDeliveryForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoOrderDelivery = (BmoOrderDelivery)bmObject;
		UiOrderDeliveryForm uiOrderDeliveryForm = new UiOrderDeliveryForm(getUiParams(), bmoOrderDelivery.getId());
		uiOrderDeliveryForm.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		UiOrderDeliveryForm uiOrderDeliveryForm = new UiOrderDeliveryForm(getUiParams(), bmObject.getId());
		uiOrderDeliveryForm.show();
	}

	public class UiOrderDeliveryForm extends UiFormDialog {
		private BmoOrderDeliveryItem bmoOrderDeliveryItem = new BmoOrderDeliveryItem();

		BmoOrderDelivery bmoOrderDelivery = new BmoOrderDelivery();

		TextBox codeTextBox = new TextBox();
		UiListBox typeListBox = new UiListBox(getUiParams());
		UiListBox statusListBox = new UiListBox(getUiParams());
		UiListBox paymentStatusListBox = new UiListBox(getUiParams());
		TextBox amountTextBox = new TextBox();	
		TextBox discountTextBox = new TextBox();
		TextBox taxTextBox = new TextBox();
		TextBox totalTextBox = new TextBox();
		UiListBox orderListBox = new UiListBox(getUiParams(), new BmoOrder());	
		UiSuggestBox customerSuggestBox = new UiSuggestBox(new BmoCustomer());
		UiSuggestBox toWhSectionSuggestBox = new UiSuggestBox(new BmoWhSection());
		UiSuggestBox projectSuggestBox = new UiSuggestBox(new BmoProject());
		
		CheckBox acceptMissingCheckBox = new CheckBox();
		UiListBox currencyListBox;
		TextBox currencyParityTextBox = new TextBox();
		UiListBox companyListBox;
		TextArea notesTextArea = new TextArea();

		String itemsSection = "Items";
		String amountSection = "Montos";

		private FlowPanel orderDeliveryItemPanel = new FlowPanel();
		private FlowPanel orderDeliveryItemButtonPanel = new FlowPanel();

		private CellTable<BmObject> orderDeliveryItemGrid = new CellTable<BmObject>();
		private UiListDataProvider<BmObject> data;
		private NumberFormat numberFormat = NumberFormat.getDecimalFormat();	

		private Button addOrderDeliveryItemButton = new Button("+ITEM");
		protected DialogBox orderDeliveryItemDialogBox;

		private Button addWhBoxButton = new Button("+CAJA");
		protected DialogBox whBoxDialogBox;

		// Ingreso por codigo de barras
		private Button barcodeDialogButton = new Button("SCANNER");
		protected DialogBox barcodeDialogBox;

		int orderDeliveryId;
		private int updateAmountRpcAttempt = 0;
		private int saveItemChangeRpcAttempt = 0;
		private int idOrderDeliveryRPC = 0;

		public UiOrderDeliveryForm(UiParams uiParams, int id) {
			super(uiParams, new BmoOrderDelivery(), id);

			orderDeliveryId = id;

			// Inicializar GRID si es registro existente
			if (orderDeliveryId > 0) { 

				// Todas las operaciones con los items de la orden de compra
				addOrderDeliveryItemButton.setStyleName("formSaveButton");
				addOrderDeliveryItemButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						addOrderDeliveryItem();
					}
				});

				// Todas las operaciones con los items de la orden de compra
				addWhBoxButton.setStyleName("formSaveButton");
				addWhBoxButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						addWhBox();
					}
				});

				// Pantalla de ingreso por codigo de barras
				barcodeDialogButton.setStyleName("formSaveButton");
				barcodeDialogButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						barcodeDialog();
					}
				});

				//Filtar el contenido del recibo
				BmFilter bmFilter = new BmFilter();
				bmFilter.setValueFilter(bmoOrderDeliveryItem.getKind(), bmoOrderDeliveryItem.getOrderDeliveryId().getName(), orderDeliveryId);
				data = new UiListDataProvider<BmObject>(new BmoOrderDeliveryItem(), bmFilter);

				orderDeliveryItemGrid.setWidth("100%");
				orderDeliveryItemPanel.setWidth("100%");

				data.addDataDisplay(orderDeliveryItemGrid);
				orderDeliveryItemPanel.add(orderDeliveryItemGrid);

				orderDeliveryItemGrid.addLoadingStateChangeHandler(new LoadingStateChangeEvent.Handler() {           
					@Override
					public void onLoadingStateChanged(LoadingStateChangeEvent event) {
						if (event.getLoadingState() == LoadingState.LOADED) {
							changeHeight();
						}
					}
				});				

			}
		}

		@Override
		public void populateFields() {
			bmoOrderDelivery = (BmoOrderDelivery)getBmObject();
			companyListBox = new UiListBox(getUiParams(), new BmoCompany());
			currencyListBox = new UiListBox(getUiParams(), new BmoCurrency());

			// Asignar valores por default
			if (newRecord) {

				try {
					if (isSlave()) {
						if (bmoProject.getId() > 0)
							bmoOrderDelivery.getProjectId().setValue(bmoProject.getId());
						else if (bmoOrder.getId() > 0)
							bmoOrderDelivery.getCustomerId().setValue(bmoOrder.getCustomerId().toInteger());
						bmoOrderDelivery.getOrderId().setValue(bmoOrder.getId());
					}
					bmoOrderDelivery.getDeliveryDate().setValue(GwtUtil.dateToString(new Date(), getSFParams().getDateTimeFormat()));
				} catch (BmException e) {
					showErrorMessage(this.getClass().getName() + "-populateFields(): ERROR " + e.toString());
				}
			}

			formFlexTable.addFieldReadOnly(1, 0, codeTextBox, bmoOrderDelivery.getCode());
			formFlexTable.addField(2, 0, typeListBox, bmoOrderDelivery.getType());
			formFlexTable.addField(3, 0, customerSuggestBox, bmoOrderDelivery.getCustomerId());
			formFlexTable.addField(4, 0, projectSuggestBox, bmoOrderDelivery.getProjectId());

			if (isSlave()) {
				if (bmoProject.getId() > 0)
					setOrdersByProjectListBoxFilters(bmoProject.getOrderId().toInteger());
				else if (bmoOrder.getId() > 0)
					setOrdersListBoxFilters(bmoOrder.getCustomerId().toInteger());
			} else {
				setOrdersListBoxFilters(bmoOrderDelivery.getCustomerId().toInteger());
			}
			formFlexTable.addField(5, 0, orderListBox, bmoOrderDelivery.getOrderId());
			formFlexTable.addLabelField(6, 0, bmoOrderDelivery.getDeliveryDate());
			setCompanyListBoxFilters(bmoOrderDelivery.getCompanyId().toInteger());
			formFlexTable.addField(7, 0, companyListBox, bmoOrderDelivery.getCompanyId());
			formFlexTable.addField(9, 0, notesTextArea,bmoOrderDelivery.getNotes());

			if (!newRecord) {
				if (bmoOrderDelivery.getType().equals(BmoOrderDelivery.TYPE_DELIVERY))
					formFlexTable.addField(8, 0, toWhSectionSuggestBox, bmoOrderDelivery.getToWhSectionId());
				formFlexTable.addSectionLabel(10, 0, itemsSection, 2);

				if (!bmoOrderDelivery.getBmoOrder().getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {
					formFlexTable.addPanel(11, 0, orderDeliveryItemButtonPanel);
					orderDeliveryItemButtonPanel.clear();
					orderDeliveryItemButtonPanel.add(addOrderDeliveryItemButton);
					orderDeliveryItemButtonPanel.add(addWhBoxButton);
					orderDeliveryItemButtonPanel.add(barcodeDialogButton);
					//orderDeliveryItemButtonPanel.add(preloadDialogButton);
				}	

				formFlexTable.addPanel(12, 0, orderDeliveryItemPanel);

				// Solo puedes ver el Precio con permiso
				if (getSFParams().hasSpecialAccess(BmoOrderDelivery.ACCESS_VIEWAMOUNT)) {
					formFlexTable.addSectionLabel(13, 0, amountSection, 2);
				}
				formFlexTable.addField(14, 0, currencyListBox, bmoOrderDelivery.getCurrencyId());
				formFlexTable.addField(15, 0, currencyParityTextBox, bmoOrderDelivery.getCurrencyParity());
				formFlexTable.addField(16, 0, amountTextBox, bmoOrderDelivery.getAmount());
				formFlexTable.addField(17, 0, taxTextBox, bmoOrderDelivery.getTax());
				formFlexTable.addField(18, 0, totalTextBox, bmoOrderDelivery.getTotal());
				if (bmoOrderDelivery.getType().equals(BmoOrderDelivery.TYPE_RETURN)) {
					formFlexTable.addField(19, 0, acceptMissingCheckBox,bmoOrderDelivery.getAcceptMissing());
				}
				formFlexTable.addLabelField(20, 0, bmoOrderDelivery.getPayment());							
				formFlexTable.addField(21, 0, statusListBox, bmoOrderDelivery.getStatus());			

				reset();

			} else {
				formFlexTable.addSectionLabel(10, 0, amountSection, 2);
				formFlexTable.addField(11, 0, currencyListBox, bmoOrderDelivery.getCurrencyId());
				formFlexTable.addField(12, 0, currencyParityTextBox, bmoOrderDelivery.getCurrencyParity());
				formFlexTable.addLabelField(13, 0, bmoOrderDelivery.getPayment());	
				formFlexTable.addField(14, 0, statusListBox, bmoOrderDelivery.getStatus());

			}

			statusEffect();
		}

		public void reset() {
			// Elimina columnas del grid
			while (orderDeliveryItemGrid.getColumnCount() > 0)
				orderDeliveryItemGrid.removeColumn(0);

			// Crea las columnas
			setColumns();

			updateAmount(id);
			data.list();
			orderDeliveryItemGrid.redraw();
		}

		public void changeHeight() {
			orderDeliveryItemGrid.setPageSize(data.getList().size());
			orderDeliveryItemGrid.setVisibleRange(0, data.getList().size());
		}

		public void setAmount(BmoOrderDelivery bmoOrderDelivery) {
			numberFormat = NumberFormat.getCurrencyFormat();
			amountTextBox.setText(numberFormat.format(bmoOrderDelivery.getAmount().toDouble()));
			taxTextBox.setText(numberFormat.format(bmoOrderDelivery.getTax().toDouble()));
			totalTextBox.setText(numberFormat.format(bmoOrderDelivery.getTotal().toDouble()));
		}

		public void setColumns() {
			// Cantidad
			Column<BmObject, String> quantityColumn;
			if (bmoOrderDelivery.getStatus().equals(BmoOrderDelivery.STATUS_REVISION)) {
				quantityColumn = new Column<BmObject, String>(new EditTextCell()) {
					@Override
					public String getValue(BmObject bmObject) {
						return ((BmoOrderDeliveryItem)bmObject).getQuantity().toString();
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
						return ((BmoOrderDeliveryItem)bmObject).getQuantity().toString();
					}
				};
			}
			orderDeliveryItemGrid.addColumn(quantityColumn, SafeHtmlUtils.fromSafeConstant("Cant."));
			orderDeliveryItemGrid.setColumnWidth(quantityColumn, 50, Unit.PX);
			quantityColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

			// Cantidad Pedido
			Column<BmObject, String> orderItemQuantityColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					if( ((BmoOrderDeliveryItem)bmObject).getBmoOrderItem().getId() > 0) {
						return ((BmoOrderDeliveryItem)bmObject).getBmoOrderItem().getQuantity().toString();
					}else return "0";
				}
			};
			orderDeliveryItemGrid.setColumnWidth(orderItemQuantityColumn, 50, Unit.PX);
			orderDeliveryItemGrid.addColumn(orderItemQuantityColumn, SafeHtmlUtils.fromSafeConstant("Ped."));		

			//if (!isMobile()) {
			// Balance pedido
			Column<BmObject, String> quantityBalanceColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoOrderDeliveryItem)bmObject).getQuantityBalance().toString();
				}
			};		
			orderDeliveryItemGrid.setColumnWidth(quantityBalanceColumn, 50, Unit.PX);
			orderDeliveryItemGrid.addColumn(quantityBalanceColumn, SafeHtmlUtils.fromSafeConstant("Pend."));

			// Devolucion
			Column<BmObject, String> quantityReturnColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoOrderDeliveryItem)bmObject).getQuantityReturned().toString();
				}
			};		
			orderDeliveryItemGrid.setColumnWidth(quantityReturnColumn, 50, Unit.PX);
			orderDeliveryItemGrid.addColumn(quantityReturnColumn, SafeHtmlUtils.fromSafeConstant("Dev."));	
			//}
			// Nombre
			Column<BmObject, String> prodColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoOrderDeliveryItem)bmObject).getBmoProduct().getCode().toString() + " " + 
							((BmoOrderDeliveryItem)bmObject).getName().toString(50);
				}
			};
			orderDeliveryItemGrid.setColumnWidth(prodColumn, 200, Unit.PX);
			orderDeliveryItemGrid.addColumn(prodColumn, SafeHtmlUtils.fromSafeConstant("Nombre"));		
			//tipo
			Column<BmObject, String> typeColumn;
			typeColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoOrderDeliveryItem)bmObject).getBmoProduct().getType().getSelectedOption().getLabel().toString();
				}
			};			
			orderDeliveryItemGrid.addColumn(typeColumn, SafeHtmlUtils.fromSafeConstant("Tipo Prod."));
			orderDeliveryItemGrid.setColumnWidth(typeColumn, 50, Unit.PX);
			typeColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			// Número de Serie
			Column<BmObject, String> serialColumn;
			serialColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoOrderDeliveryItem)bmObject).getSerial().toString();
				}
			};
			orderDeliveryItemGrid.addColumn(serialColumn, SafeHtmlUtils.fromSafeConstant("#Serie/Lote"));
			orderDeliveryItemGrid.setColumnWidth(serialColumn, 50, Unit.PX);
			serialColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			
			//if (!isMobile()) {
			// S. Almacén Origen
			Column<BmObject, String> fromWhSectionColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoOrderDeliveryItem)bmObject).getBmoFromWhSection().getName().toString();
				}
			};
			orderDeliveryItemGrid.addColumn(fromWhSectionColumn, SafeHtmlUtils.fromSafeConstant("Origen"));
			orderDeliveryItemGrid.setColumnWidth(fromWhSectionColumn, 50, Unit.PX);

			// Dias
			Column<BmObject, String> daysColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoOrderDeliveryItem)bmObject).getBmoOrderItem().getDays().toString();
				}
			};
			orderDeliveryItemGrid.setColumnWidth(daysColumn, 50, Unit.PX);
			orderDeliveryItemGrid.addColumn(daysColumn, SafeHtmlUtils.fromSafeConstant("Dias"));
			//}
			// Solo puedes ver el Precio con permiso
			if (getSFParams().hasSpecialAccess(BmoOrderDelivery.ACCESS_VIEWAMOUNT)) {
				Column<BmObject, String> priceColumn = new Column<BmObject, String>(new TextCell()) {
					@Override
					public String getValue(BmObject bmObject) {
						numberFormat = NumberFormat.getCurrencyFormat();
						String formatted = numberFormat.format(((BmoOrderDeliveryItem)bmObject).getPrice().toDouble());
						return (formatted);
					}
				};
				orderDeliveryItemGrid.addColumn(priceColumn, SafeHtmlUtils.fromSafeConstant("Precio"));
				orderDeliveryItemGrid.setColumnWidth(priceColumn, 100, Unit.PX);
				priceColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
	
				// Total
				Column<BmObject, String> totalColumn = new Column<BmObject, String>(new TextCell()) {
					@Override
					public String getValue(BmObject bmObject) {
						numberFormat = NumberFormat.getCurrencyFormat();
						String formatted = numberFormat.format(((BmoOrderDeliveryItem)bmObject).getAmount().toDouble());
						return (formatted);
					}
				};
				totalColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
				orderDeliveryItemGrid.setColumnWidth(totalColumn, 120, Unit.PX);
				orderDeliveryItemGrid.addColumn(totalColumn, SafeHtmlUtils.fromSafeConstant("Total"));
			}
			// Eliminar
			Column<BmObject, String> deleteColumn;
			if (bmoOrderDelivery.getStatus().equals(BmoOrderDelivery.STATUS_REVISION)) {
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
			orderDeliveryItemGrid.addColumn(deleteColumn, SafeHtmlUtils.fromSafeConstant("Eliminar"));
			deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			orderDeliveryItemGrid.setColumnWidth(deleteColumn, 50, Unit.PX);	
		}

		@Override
		public void formListChange(ChangeEvent event) {
			if (event.getSource() == statusListBox) {
				update("Desea cambiar el Status del Envío de Pedido?");
			} 		
			if (event.getSource() == orderListBox) {
				BmoOrder bmoOrder = (BmoOrder)orderListBox.getSelectedBmObject();			
				if (bmoOrder != null) {
					populateCompany(bmoOrder.getCompanyId().toInteger());
					populateCurrency(bmoOrder.getCurrencyId().toInteger(), bmoOrder.getCurrencyParity().toDouble());
				} else {
					populateCompany(-1);
					populateCurrency(-1, -1);
				}	
			}if (event.getSource() == typeListBox) {
				if (projectSuggestBox.getSelectedId() > 0)
					populateOrdersByProject(bmoProject.getOrderId().toInteger());
				else if (customerSuggestBox.getSelectedId() > 0)
					populateOrders(customerSuggestBox.getSelectedId());
			}

			statusEffect();
		}

		// Cambios en los SuggestBox
		@Override
		public void formSuggestionSelectionChange(UiSuggestBox uiSuggestBox) {
			if (uiSuggestBox == customerSuggestBox) {
				populateOrders(customerSuggestBox.getSelectedId());
			}
			if (uiSuggestBox == projectSuggestBox) {
				BmoProject bmoProject = ((BmoProject)projectSuggestBox.getSelectedBmObject());
				if (bmoProject == null)
					populateOrdersByProject(-1);
				else
					populateOrdersByProject(bmoProject.getOrderId().toInteger());
			}


			statusEffect();
		}

		private void statusEffect() {
			// Deshabilita campos
			codeTextBox.setEnabled(false);
			//orderListBox.setEnabled(false);
			amountTextBox.setEnabled(false);
			taxTextBox.setEnabled(false);
			discountTextBox.setEnabled(false);
			totalTextBox.setEnabled(false);		
			paymentStatusListBox.setEnabled(false);
			//customerSuggestBox.setEnabled(false);		
			addOrderDeliveryItemButton.setVisible(false);
			addWhBoxButton.setVisible(false);
			barcodeDialogButton.setVisible(false);
			currencyListBox.setEnabled(false);
			companyListBox.setEnabled(false);
			currencyParityTextBox.setEnabled(false);
			statusListBox.setEnabled(false);
			typeListBox.setEnabled(false);
			toWhSectionSuggestBox.setEnabled(false);
			//projectSuggestBox.setEnabled(false);
			notesTextArea.setEnabled(false);
			
			acceptMissingCheckBox.setEnabled(false);
			
			formFlexTable.hideField(bmoOrderDelivery.getCurrencyId());
			formFlexTable.hideField(bmoOrderDelivery.getCurrencyParity());
			formFlexTable.hideField(bmoOrderDelivery.getAmount());
			formFlexTable.hideField(bmoOrderDelivery.getTax());
			formFlexTable.hideField(bmoOrderDelivery.getTotal());
			formFlexTable.hideField(bmoOrderDelivery.getPayment());

			// De acuerdo al estatus habilitar campos
			if (bmoOrderDelivery.getStatus().toChar() == BmoOrderDelivery.STATUS_REVISION) {
				addWhBoxButton.setVisible(true);
				addOrderDeliveryItemButton.setVisible(true);
				barcodeDialogButton.setVisible(true);
				notesTextArea.setEnabled(true);
				acceptMissingCheckBox.setEnabled(true);
			}else {
				notesTextArea.setEnabled(false);
			}

			if (newRecord) {
				//				customerSuggestBox.setEnabled(true);

				//				if (customerSuggestBox.getSelectedId() > 0)
				//					orderListBox.setEnabled(true);

				typeListBox.setEnabled(true);
			} else {
				orderListBox.setEnabled(false);
				customerSuggestBox.setEnabled(false);	
				projectSuggestBox.setEnabled(false);

				// Si no tiene permiso para modificar status, deshabilitar combo
				if (getSFParams().hasSpecialAccess(BmoOrderDelivery.ACCESS_CHANGESTATUS)) {
					statusListBox.setEnabled(true);
				}

				// Si ya esta provisionado, no se puede modificar
				if (bmoOrderDelivery.getPayment().toChar() == BmoOrderDelivery.PAYMENT_PROVISIONED) {
					statusListBox.setEnabled(false);
				} else {
					saveButton.setVisible(true);
					deleteButton.setVisible(true);
				}			
			}

			if (customerSuggestBox.getSelectedId() > 0)
				projectSuggestBox.setEnabled(false);
			else if (projectSuggestBox.getSelectedId() > 0)
				customerSuggestBox.setEnabled(false);
			else {
				customerSuggestBox.setEnabled(true);
				projectSuggestBox.setEnabled(true);
			}


			amountTextBox.setText(numberFormat.format(bmoOrderDelivery.getAmount().toDouble()));
			taxTextBox.setText(numberFormat.format(bmoOrderDelivery.getTax().toDouble()));
			totalTextBox.setText(numberFormat.format(bmoOrderDelivery.getTotal().toDouble()));
			
			// Solo puedes ver el Precio con permiso
			if (getSFParams().hasSpecialAccess(BmoOrderDelivery.ACCESS_VIEWAMOUNT)) {
				formFlexTable.showField(bmoOrderDelivery.getCurrencyId());
				formFlexTable.showField(bmoOrderDelivery.getCurrencyParity());
				formFlexTable.showField(bmoOrderDelivery.getAmount());
				formFlexTable.showField(bmoOrderDelivery.getTax());
				formFlexTable.showField(bmoOrderDelivery.getTotal());
				formFlexTable.showField(bmoOrderDelivery.getPayment());
			}
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoOrderDelivery.setId(id);
			bmoOrderDelivery.getCode().setValue(codeTextBox.getText());
			bmoOrderDelivery.getType().setValue(typeListBox.getSelectedCode());
			bmoOrderDelivery.getStatus().setValue(statusListBox.getSelectedCode());		
			bmoOrderDelivery.getAmount().setValue(amountTextBox.getText());
			bmoOrderDelivery.getDiscount().setValue(discountTextBox.getText());
			bmoOrderDelivery.getTax().setValue(taxTextBox.getText());
			bmoOrderDelivery.getTotal().setValue(totalTextBox.getText());
			bmoOrderDelivery.getOrderId().setValue(orderListBox.getSelectedId());		
			bmoOrderDelivery.getCustomerId().setValue(customerSuggestBox.getSelectedId());
			bmoOrderDelivery.getCompanyId().setValue(companyListBox.getSelectedId());
			bmoOrderDelivery.getCurrencyId().setValue(currencyListBox.getSelectedId());
			bmoOrderDelivery.getCurrencyParity().setValue(currencyParityTextBox.getText());
			bmoOrderDelivery.getProjectId().setValue(projectSuggestBox.getSelectedId());
			bmoOrderDelivery.getNotes().setValue(notesTextArea.getText());
			bmoOrderDelivery.getAcceptMissing().setValue(acceptMissingCheckBox.getValue());
			return bmoOrderDelivery;
		}

		@Override
		public void close() {
			list();
		}

		@Override
		public void saveNext() {
			if (newRecord) { 
				UiOrderDeliveryForm uiOrderDeliveryForm = new UiOrderDeliveryForm(getUiParams(), getBmObject().getId());
				uiOrderDeliveryForm.show();
			} else {
				list();
			}		
		}

		// Primer intento
		public void updateAmount(int idOrderDelivery) {
			updateAmount(idOrderDelivery, 0);
		}
		public void updateAmount(int idOrderDelivery, int updateAmountRpcAttempt) {
			if (updateAmountRpcAttempt < 5) {
				setIdOrderDeliveryRPC(idOrderDelivery);
				setUpdateAmountRpcAttempt(updateAmountRpcAttempt + 1);

				AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
					@Override
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getUpdateAmountRpcAttempt() < 5)
							updateAmount(getIdOrderDeliveryRPC(), getUpdateAmountRpcAttempt());
						else
							showErrorMessage(this.getClass().getName() + "-updateAmount() ERROR: " + caught.toString());
					}

					@Override
					public void onSuccess(BmObject result) {
						stopLoading();
						setUpdateAmountRpcAttempt(0);
						setAmount((BmoOrderDelivery)result);
					}
				};

				try {
					if (!isLoading()) {
						startLoading();
						getUiParams().getBmObjectServiceAsync().get(bmoOrderDelivery.getPmClass(), idOrderDelivery, callback);
					}
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-updateAmount(): ERROR " + e.toString());
				}
			}
		}

		// Actualiza listado de Pedidos
		private void populateOrders(int customerId) {
			orderListBox.clear();
			orderListBox.clearFilters();
			setOrdersListBoxFilters(customerId);
			orderListBox.populate("" + bmoOrderDelivery.getOrderId(), false);	
		}

		// Asigna filtros de listado de Pedidos
		private void setOrdersListBoxFilters(int customerId) {
			BmoOrder bmoOrder = new BmoOrder();

			if (customerId > 0) {

				BmFilter bmFilterReqAutorized = new BmFilter();
				BmFilter bmFilterReqDelivery = new BmFilter();				
				BmFilter bmFilterReqByCustomer = new BmFilter();

				if (bmoOrderDelivery.getStatus().toChar() == BmoOrderDelivery.STATUS_REVISION &&
						!(bmoOrderDelivery.getOrderId().toInteger() > 0)) {
					bmFilterReqAutorized.setValueFilter(bmoOrder.getKind(), bmoOrder.getStatus(), "" + BmoOrder.STATUS_AUTHORIZED);
					//bmFilterReqDelivery.setValueOrFilter(bmoOrder.getKind(),bmoOrder.getDeliveryStatus().getName(), "" + BmoOrder.DELIVERYSTATUS_PENDING, "" + BmoOrder.DELIVERYSTATUS_PARTIAL);

					orderListBox.addBmFilter(bmFilterReqAutorized);

					char type = typeListBox.getSelectedCode().charAt(0);
					if (type == BmoOrderDelivery.TYPE_DELIVERY) {
						bmFilterReqDelivery.setValueOrFilter(bmoOrder.getKind(),bmoOrder.getDeliveryStatus().getName(), 
								"" + BmoOrder.DELIVERYSTATUS_PENDING, "" + BmoOrder.DELIVERYSTATUS_PARTIAL);
						orderListBox.addBmFilter(bmFilterReqDelivery);	
					} else if (type == BmoOrderDelivery.TYPE_RETURN) {
						bmFilterReqDelivery.setValueOrFilter(bmoOrder.getKind(),bmoOrder.getDeliveryStatus().getName(), 
								"" + BmoOrder.DELIVERYSTATUS_PARTIAL, "" + BmoOrder.DELIVERYSTATUS_TOTAL);
						orderListBox.addBmFilter(bmFilterReqDelivery);
					}

				} else {
					bmFilterReqAutorized.setValueFilter(bmoOrder.getKind(), bmoOrder.getIdField(), bmoOrderDelivery.getOrderId().toInteger());
					orderListBox.addBmFilter(bmFilterReqAutorized);
				}

				bmFilterReqByCustomer.setValueFilter(bmoOrder.getKind(), bmoOrder.getCustomerId(), customerId);
				orderListBox.addBmFilter(bmFilterReqByCustomer);
			} else {

				BmFilter bmFilterLimitRecords = new BmFilter();
				bmFilterLimitRecords.setValueFilter(bmoOrder.getKind(), bmoOrder.getCustomerId(), "-1");
				orderListBox.addBmFilter(bmFilterLimitRecords);

				// limpiar empresa y moneda
				populateCompany(-1);
				populateCurrency(-1, -1);
			}
		}

		// Actualiza listado de Pedidos
		private void populateOrdersByProject(int orderId) {
			orderListBox.clear();
			orderListBox.clearFilters();
			setOrdersByProjectListBoxFilters(orderId);
			orderListBox.populate("" + orderId, false);	
		}

		// Asigna filtros de listado de Pedidos
		private void setOrdersByProjectListBoxFilters(int orderId) {
			BmoOrder bmoOrder = new BmoOrder();

			if (orderId > 0) {

				BmFilter bmFilterReqAutorized = new BmFilter();
				BmFilter bmFilterReqDelivery = new BmFilter();				
				BmFilter bmFilterReqByCustomer = new BmFilter();

				if (bmoOrderDelivery.getStatus().toChar() == BmoOrderDelivery.STATUS_REVISION &&
						!(bmoOrderDelivery.getOrderId().toInteger() > 0)) {
					bmFilterReqAutorized.setValueFilter(bmoOrder.getKind(), bmoOrder.getStatus(), "" + BmoOrder.STATUS_AUTHORIZED);
					bmFilterReqDelivery.setValueOrFilter(bmoOrder.getKind(),bmoOrder.getDeliveryStatus().getName(), "" + BmoOrder.DELIVERYSTATUS_PENDING, "" + BmoOrder.DELIVERYSTATUS_PARTIAL);

					orderListBox.addBmFilter(bmFilterReqAutorized);

					char type = typeListBox.getSelectedCode().charAt(0);
					if (type == BmoOrderDelivery.TYPE_DELIVERY) {
						orderListBox.addBmFilter(bmFilterReqDelivery);	
					}

				} else {
					bmFilterReqAutorized.setValueFilter(bmoOrder.getKind(), bmoOrder.getIdField(), bmoOrderDelivery.getOrderId().toInteger());
					orderListBox.addBmFilter(bmFilterReqAutorized);
				}

				bmFilterReqByCustomer.setValueFilter(bmoOrder.getKind(), bmoOrder.getIdFieldName(), orderId);
				orderListBox.addBmFilter(bmFilterReqByCustomer);
			} else {

				BmFilter bmFilterLimitRecords = new BmFilter();
				bmFilterLimitRecords.setValueFilter(bmoOrder.getKind(), bmoOrder.getIdFieldName(), "-1");
				orderListBox.addBmFilter(bmFilterLimitRecords);

				// limpiar empresa y moneda
				populateCompany(-1);
				populateCurrency(-1, -1);
			}
		}

		// Actualiza listado de Empresas
		private void populateCompany(int companyId) {
			companyListBox.clear();
			companyListBox.clearFilters();
			setCompanyListBoxFilters(companyId);
			companyListBox.populate("" + companyId);
		}

		// Asigna filtros de listado de Empresas
		private void setCompanyListBoxFilters(int companyId) {
			BmoCompany bmoCompany = new BmoCompany();

			// Agregamos empresa del pedido seleccionado
			if (companyId > 0) {
				BmFilter bmFilterByCompanyId = new BmFilter();
				bmFilterByCompanyId.setValueFilter(bmoCompany.getKind(), bmoCompany.getIdField(), companyId);
				companyListBox.addBmFilter(bmFilterByCompanyId);
			} else {
				BmFilter bmFilterLimitRecords = new BmFilter();
				bmFilterLimitRecords.setValueFilter(bmoCompany.getKind(), bmoCompany.getIdField(), "-1");
				companyListBox.addBmFilter(bmFilterLimitRecords);
			}

			companyListBox.setEnabled(false);
		}

		// Actualiza listado de Moneda
		private void populateCurrency(int currencyId, double currencyParity) {
			currencyListBox.clear();
			currencyListBox.clearFilters();
			setCurrencyListBoxFilters(currencyId, currencyParity);
			currencyListBox.populate("" + currencyId);
			currencyListBox.setEnabled(false);
		}

		// Asigna filtros de listado de Empresas
		private void setCurrencyListBoxFilters(int currencyId, double currencyParity) {
			BmoCurrency bmoCurrency = new BmoCurrency();

			// Agregamos moneda del pedido seleccionado
			if (currencyId > 0) {
				BmFilter bmFilterByCompanyId = new BmFilter();
				bmFilterByCompanyId.setValueFilter(bmoCurrency.getKind(), bmoCurrency.getIdField(), currencyId);
				currencyListBox.addBmFilter(bmFilterByCompanyId);
			} else {
				BmFilter bmFilterLimitRecords = new BmFilter();
				bmFilterLimitRecords.setValueFilter(bmoCurrency.getKind(), bmoCurrency.getIdField(), "-1");
				currencyListBox.addBmFilter(bmFilterLimitRecords);
			}

			if (currencyParity > 0)
				currencyParityTextBox.setText("" + currencyParity);
			else 
				currencyParityTextBox.setText("");

		}

		public void saveAmountChange() {
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-saveDiscount(): ERROR " + caught.toString());
				}

				@Override
				public void onSuccess(BmUpdateResult result) {
					stopLoading();				
					processOrderDeliveryUpdateResult(result);
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().save(bmoOrderDelivery.getPmClass(), bmoOrderDelivery, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-saveAmountChange(): ERROR " + e.toString());
			}
		}

		public void processOrderDeliveryUpdateResult(BmUpdateResult result) {
			if (result.hasErrors()) showFormMsg("Error al actualizar montos del Envío de Pedido.", "Error al actualizar montos del Envío de Pedido.: " + result.errorsToString());
			else updateAmount(id);
		}

		public void changeQuantity(BmObject bmObject, String quantity) {
			bmoOrderDeliveryItem = (BmoOrderDeliveryItem)bmObject;

			try {
				double q = Double.parseDouble(quantity);
				// Si requiere serial, primero solicitarlo
				// Identificar si es producto
				if (bmoOrderDeliveryItem.getBmoOrderItem().getProductId().toInteger() > 0) {

					if (bmoOrderDeliveryItem.getBmoProduct().getTrack().equals(BmoProduct.TRACK_SERIAL) 
							|| bmoOrderDeliveryItem.getBmoProduct().getTrack().equals(BmoProduct.TRACK_BATCH)) {

						if (bmoOrderDeliveryItem.getSerial().toString().equals("")) {
							showErrorMessage("Debe establecer primero el # de Serie.");
							reset();

							// Tiene rastreo tipo #SERIE la cantidad debe ser cero 
						} else if (bmoOrderDeliveryItem.getBmoProduct().getTrack().toChar() == BmoProduct.TRACK_SERIAL &&
								(q > 1)) {
							showErrorMessage("La Cantidad no puede ser mayor a 1.");
							reset();
							// No tiene rastreo
						} else {
							bmoOrderDeliveryItem.getQuantity().setValue(q);

							saveItemChange();
						}
					} else {
						bmoOrderDeliveryItem.getQuantity().setValue(q);
						saveItemChange();	
					}
				} else {
					bmoOrderDeliveryItem.getQuantity().setValue(q);
					saveItemChange();
				}
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-changeQuantity(): ERROR " + e.toString());
				reset();
			}
		}

		public void changeSerial(BmObject bmObject, String serial) {
			bmoOrderDeliveryItem = (BmoOrderDeliveryItem)bmObject;
			try {
				if (!serial.equals("")) {
					if (bmoOrderDeliveryItem.getBmoProduct().getTrack().toChar() == BmoProduct.TRACK_SERIAL) {
						bmoOrderDeliveryItem.getQuantity().setValue(1);
						bmoOrderDeliveryItem.getSerial().setValue(serial);
						saveItemChange();
					} else if (bmoOrderDeliveryItem.getBmoProduct().getTrack().toChar() == BmoProduct.TRACK_BATCH) {
						bmoOrderDeliveryItem.getSerial().setValue(serial);
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

		public void changePrice(BmObject bmObject, String price) {
			bmoOrderDeliveryItem = (BmoOrderDeliveryItem)bmObject;
			try {
				double p = Double.parseDouble(price);
				if (p > 0) {
					bmoOrderDeliveryItem.getPrice().setValue(price);
					saveItemChange();
				}
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-changePrice(): ERROR " + e.toString());
			}
		}

		public void processItemChangeSave(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) 
				showErrorMessage("Error al modificar el Item: " + bmUpdateResult.errorsToString());
			this.reset();
		}

		public void processItemDelete(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) 
				showFormMsg("Error al modificar el Item.", "Error al modificar el Item: " + bmUpdateResult.errorsToString());
			else {
				this.reset();			
			}		
		}

		// Guardar item, primer intento
		public void saveItemChange() {
			saveItemChange(0);
		}
		// Guardar item
		public void saveItemChange(int saveItemChangeRpcAttempt) {
			if (saveItemChangeRpcAttempt < 5) {
				setSaveItemChangeRpcAttempt(saveItemChangeRpcAttempt + 1);

				// Establece eventos ante respuesta de servicio
				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					@Override
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getSaveItemChangeRpcAttempt() < 5)
							saveItemChange(getSaveItemChangeRpcAttempt());
						else
							showErrorMessage(this.getClass().getName() + "-saveItemChange(): ERROR " + caught.toString());
					}

					@Override
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
						getUiParams().getBmObjectServiceAsync().save(bmoOrderDeliveryItem.getPmClass(), bmoOrderDeliveryItem, callback);
					}
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-saveItemChange(): ERROR " + e.toString());
				}
			}
		}

		private void deleteItem(BmObject bmObject) {
			bmoOrderDeliveryItem = (BmoOrderDeliveryItem)bmObject;
			deleteItem();
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
					getUiParams().getBmObjectServiceAsync().delete(bmoOrderDeliveryItem.getPmClass(), bmoOrderDeliveryItem, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-deleteItem(): ERROR " + e.toString());
			}
		}

		public void addOrderDeliveryItem() {
			addOrderDeliveryItem(new BmoProduct());
		}

		public void addOrderDeliveryItem(BmoProduct bmoProduct) {
			orderDeliveryItemDialogBox = new DialogBox(true);
			orderDeliveryItemDialogBox.setGlassEnabled(true);
			orderDeliveryItemDialogBox.setText("Item de Pedido");

			VerticalPanel vp = new VerticalPanel();
			vp.setSize("400px", "100px");

			orderDeliveryItemDialogBox.setWidget(vp);

			UiOrderDeliveryItemForm orderDeliveryItemForm = new UiOrderDeliveryItemForm(getUiParams(), vp, bmoOrderDelivery, bmoProduct);

			orderDeliveryItemForm.show();

			orderDeliveryItemDialogBox.center();
			orderDeliveryItemDialogBox.show();
		}

		public void addWhBox() {
			addWhBox(new BmoWhBox());
		}

		public void addWhBox(BmoWhBox bmoWhBox) {
			whBoxDialogBox = new DialogBox(true);
			whBoxDialogBox.setGlassEnabled(true);
			whBoxDialogBox.setText("Caja de Productos");

			VerticalPanel vp = new VerticalPanel();
			vp.setSize("400px", "100px");

			whBoxDialogBox.setWidget(vp);

			UiWhBoxForm whBoxForm = new UiWhBoxForm(getUiParams(), vp, bmoOrderDelivery, bmoWhBox);

			whBoxForm.show();

			whBoxDialogBox.center();
			whBoxDialogBox.show();
		}

		public void barcodeDialog() {
			barcodeDialogBox = new DialogBox(true);
			barcodeDialogBox.setGlassEnabled(true);
			barcodeDialogBox.setText("Scanner Código de Barras");

			VerticalPanel vp = new VerticalPanel();
			vp.setSize("400px", "200px");

			barcodeDialogBox.setWidget(vp);

			UiBarcodeForm uiBarcodeForm = new UiBarcodeForm(getUiParams(), vp, bmoOrderDelivery);
			uiBarcodeForm.show();

			barcodeDialogBox.center();
			barcodeDialogBox.show();
		}

		// Agrega un item de un producto al envio de pedido
		private class UiOrderDeliveryItemForm extends Ui {
			private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());		
			private BmoOrderDeliveryItem bmoOrderDeliveryItem;
			private Button saveButton = new Button("AGREGAR");
			private HorizontalPanel buttonPanel = new HorizontalPanel();
			private UiSuggestBox orderItemSuggestBox = new UiSuggestBox(new BmoOrderItem());
			private BmoOrderItem bmoOrderItem = new BmoOrderItem();

			public UiOrderDeliveryItemForm(UiParams uiParams, Panel defaultPanel, BmoOrderDelivery bmoOrderDelivery, BmoProduct bmoProduct) {
				super(uiParams, defaultPanel);
				this.bmoOrderDeliveryItem = new BmoOrderDeliveryItem();

				try {
					bmoOrderDeliveryItem.getOrderDeliveryId().setValue(orderDeliveryId);
					bmoOrderDeliveryItem.getProductId().setValue(bmoProduct.getId());
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
				if (getSFParams().hasWrite(bmoOrderDelivery.getProgramCode())) saveButton.setEnabled(true);
				buttonPanel.add(saveButton);

				// Manejo de acciones de suggest box
				UiSuggestBoxAction uiSuggestBoxAction = new UiSuggestBoxAction() {
					@Override
					public void onSelect(UiSuggestBox uiSuggestBox) {
						formSuggestionChange(uiSuggestBox);
					}
				};
				formTable.setUiSuggestBoxAction(uiSuggestBoxAction);

				// Filtro de pedidos en pedido
				BmFilter itemsByOrderFilter = new BmFilter();
				itemsByOrderFilter.setValueFilter(bmoOrderItem.getBmoOrderGroup().getKind(), 
						bmoOrderItem.getBmoOrderGroup().getOrderId().getName(), bmoOrderDelivery.getOrderId().toInteger());
				//		itemsByOrderFilter.setInFilter(bmoOrderItem.getBmoOrderGroup().getKind(), 
				//		bmoOrderItem.getOrderGroupId().getName(), 
				//		bmoOrderItem.getBmoOrderGroup().getIdFieldName(), 
				//		bmoOrderItem.getBmoOrderGroup().getOrderId().getName(), 
				//		bmoOrderDelivery.getOrderId().toString());
				orderItemSuggestBox.addFilter(itemsByOrderFilter);

				// Filtro de solo productos que afecten inventario
				BmFilter filterByNotProductComposed = new BmFilter();
				filterByNotProductComposed.setValueFilter(bmoOrderItem.getBmoProduct().getKind(), bmoOrderItem.getBmoProduct().getInventory().getName(), 1);
				orderItemSuggestBox.addFilter(filterByNotProductComposed);
				
				defaultPanel.add(formTable);
			}

			@Override
			public void show() {
				formTable.addField(1, 0, orderItemSuggestBox, bmoOrderDeliveryItem.getProductId());

				formTable.addButtonPanel(buttonPanel);
			}

			public void formSuggestionChange(UiSuggestBox uiSuggestBox) {
				if (uiSuggestBox.getSelectedId() > 0)
					this.bmoOrderItem = ((BmoOrderItem)uiSuggestBox.getSelectedBmObject());
			}

			public void processUpdateResult(BmUpdateResult bmUpdateResult) {
				if (bmUpdateResult.hasErrors()) 
					showSystemMessage("Error al modificar Item: " + bmUpdateResult.errorsToString());
				else {
					orderDeliveryItemDialogBox.hide();
					reset();
				}
			}

			public void prepareSave() {
				try {
					bmoOrderDeliveryItem = new BmoOrderDeliveryItem();

					bmoOrderDeliveryItem.getQuantity().setValue(0);
					bmoOrderDeliveryItem.getDays().setValue(bmoOrderItem.getDays().toDouble());
					bmoOrderDeliveryItem.getName().setValue(bmoOrderItem.getBmoProduct().getName().toString());				
					bmoOrderDeliveryItem.getPrice().setValue(bmoOrderItem.getPrice().toDouble());
					bmoOrderDeliveryItem.getAmount().setValue(0);

					bmoOrderDeliveryItem.getProductId().setValue(bmoOrderItem.getProductId().toInteger());
					bmoOrderDeliveryItem.getOrderDeliveryId().setValue(orderDeliveryId);
					bmoOrderDeliveryItem.getOrderItemId().setValue(bmoOrderItem.getId());
					bmoOrderDeliveryItem.getToWhSectionId().setValue(bmoOrderDelivery.getToWhSectionId().toInteger());
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
						getUiParams().getBmObjectServiceAsync().save(bmoOrderDeliveryItem.getPmClass(), bmoOrderDeliveryItem, callback);
					}
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-save(): ERROR " + e.toString());
				}
			}
		}

		// Agrega un item de un producto a la orden de compra
		private class UiWhBoxForm extends Ui {
			private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
			private UiListBox whBoxListBox = new UiListBox(getUiParams(), new BmoWhBox());

			private Button saveButton = new Button("AGREGAR");
			private HorizontalPanel buttonPanel = new HorizontalPanel();
			private BmoWhBox bmoWhBox;
			private BmoOrderDelivery bmoOrderDelivery;

			public UiWhBoxForm(UiParams uiParams, Panel defaultPanel, BmoOrderDelivery bmoOrderDelivery, BmoWhBox bmoWhBox) {
				super(uiParams, defaultPanel);
				this.bmoOrderDelivery = bmoOrderDelivery;
				this.bmoWhBox = bmoWhBox;

				saveButton.setStyleName("formSaveButton");
				saveButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						prepareSave();
					}
				});
				saveButton.setEnabled(false);
				if (getSFParams().hasWrite(bmoOrderDelivery.getProgramCode())) saveButton.setEnabled(true);
				buttonPanel.add(saveButton);

				defaultPanel.add(formTable);
			}

			@Override
			public void show() {
				formTable.addField(1, 0, whBoxListBox, bmoWhBox.getIdField());
				formTable.addButtonPanel(buttonPanel);
			}

			public void prepareSave() {
				try {
					bmoWhBox = new BmoWhBox();
					bmoWhBox.setId(whBoxListBox.getSelectedId());
					save();
				} catch (BmException e) {
					showErrorMessage(this.getClass().getName() + "-prepareSave(): ERROR " + e.toString());
				}
			}

			//Obtener la cantidad en almacen
			public void save() {
				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					@Override
					public void onFailure(Throwable caught) {
						stopLoading();
						showErrorMessage(this.getClass().getName() + "-save() ERROR: " + caught.toString());
					}

					@Override
					public void onSuccess(BmUpdateResult result) {
						stopLoading();
						processUpdateResult(result);			
					}
				};

				try {	
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoOrderDelivery.getPmClass(), bmoOrderDelivery, BmoOrderDelivery.ACTION_WHBOX, "" +  bmoWhBox.getId(), callback);
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-save() ERROR: " + e.toString());
				}
			} 

			public void processUpdateResult(BmUpdateResult bmUpdateResult) {
				if (bmUpdateResult.hasErrors()) showErrorMessage("Error al modificar Item: " + bmUpdateResult.errorsToString());
				else {
					whBoxDialogBox.hide();
					reset();
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
			private Label orderQuantityLabel = new Label();
			private Label orderBalanceLabel = new Label();

			private UiListBox whStockListBox = new UiListBox(getUiParams(), new BmoWhStock());
			private UiSuggestBox toWhSectionSuggestBox = new UiSuggestBox(new BmoWhSection());
			private UiSuggestBox fromWhSectionSuggestBox = new UiSuggestBox(new BmoWhSection());

			private BmoOrderDelivery bmoOrderDelivery;
			private BmoOrderDeliveryItem bmoOrderDeliveryItem;
			private BmoWhStock bmoWhStock;
			private int searchBarcodeRpcAttempt = 0;
			private String barcode = "";

			BmField barcodeTextBoxField;

			public UiBarcodeForm(UiParams uiParams, Panel defaultPanel, BmoOrderDelivery bmoOrderDelivery) {
				super(uiParams, defaultPanel);
				this.bmoOrderDeliveryItem = new BmoOrderDeliveryItem();
				this.bmoOrderDelivery = bmoOrderDelivery;
				this.bmoWhStock = new BmoWhStock();

				barcodeTextBoxField = new BmField("barcodeTextBoxField", "", "Item Envío Ped.", 40, Types.VARCHAR, false, BmFieldType.STRING, false);

				saveButton.setStyleName("formSaveButton");
				saveButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						prepareSave();
					}
				});
				saveButton.setEnabled(false);
				if (getSFParams().hasWrite(bmoOrderDelivery.getProgramCode())) saveButton.setEnabled(true);
				buttonPanel.add(saveButton);

				closeButton.setStyleName("formCloseButton");
				closeButton.addClickHandler(new ClickHandler() {
					@Override
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

				// Asigna filtros a Existencias
				setWhStockListBoxFilters(-1, "");
			}

			@Override
			public void show() {
				formTable.addField(1, 0, barcodeTextBox, barcodeTextBoxField);
				formTable.addField(2, 0, whStockListBox, bmoWhStock.getIdField());
				formTable.addField(3, 0, fromWhSectionSuggestBox, bmoOrderDeliveryItem.getFromWhSectionId());
				formTable.addField(4, 0, toWhSectionSuggestBox, bmoOrderDeliveryItem.getToWhSectionId());
				formTable.addField(5, 0, serialTextBox, bmoOrderDeliveryItem.getSerial());
				formTable.addLabelField(6, 0, "Producto / Item:", productLabel);
				formTable.addLabelField(7, 0, "Cantidad Pedido:", orderQuantityLabel);
				formTable.addLabelField(8, 0, "Pendiente:", orderBalanceLabel);
				formTable.addField(9, 0, quantityTextBox, bmoOrderDeliveryItem.getQuantity());
				formTable.addButtonPanel(buttonPanel);

				serialTextBox.setEnabled(false);
				whStockListBox.setEnabled(false);
				fromWhSectionSuggestBox.setEnabled(false);
				toWhSectionSuggestBox.setEnabled(false);
				setBarcodeFocus();
			}

			// Asigna foco al campo de codigo de barras
			private void setBarcodeFocus() {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						barcodeTextBox.setFocus(true);                
					}
				});
			}

			// Cambia texto en codigo de barras
			public void changeBarcode(ValueChangeEvent<String> event) {
				try {
					bmoOrderDeliveryItem.getOrderDeliveryId().setValue(bmoOrderDelivery.getId());				
					searchBarcode(barcodeTextBox.getText());
				} catch (BmException e) {
					showSystemMessage(this.getClass().getName() + "-changeBarcode(): " + e.toString());
				}
			}

			// Obtener la cantidad en almacen
			private void searchBarcode(String barcode) {
				searchBarcode(barcode, 0);
			}

			// Obtener la cantidad en almacen
			private void searchBarcode(String barcode, int searchBarcodeRpcAttempt) {
				if (searchBarcodeRpcAttempt < 5) {
					setBarcode(barcode);
					setSearchBarcodeRpcAttempt(searchBarcodeRpcAttempt + 1);

					AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
						@Override
						public void onFailure(Throwable caught) {
							stopLoading();
							if (getSearchBarcodeRpcAttempt() < 5)
								searchBarcode(getBarcode(), getSearchBarcodeRpcAttempt());
							else
								showErrorMessage(this.getClass().getName() + "-searchBarcode() ERROR: " + caught.toString());
						}

						@Override
						public void onSuccess(BmUpdateResult result) {
							stopLoading();
							setSearchBarcodeRpcAttempt(0);
							processBarcodeSearchResult(result);
						}
					};

					try {	
						startLoading();
						getUiParams().getBmObjectServiceAsync().action(bmoOrderDeliveryItem.getPmClass(), bmoOrderDeliveryItem, BmoOrderDeliveryItem.ACTION_SEARCHBARCODE, barcode, callback);
					} catch (SFException e) {
						stopLoading();
						showErrorMessage(this.getClass().getName() + "-searchBarcode() ERROR: " + e.toString());
					}
				}
			} 

			// Acciones con el codigo de barras recuperado
			public void processBarcodeSearchResult(BmUpdateResult bmUpdateResult) {

				// Existen errores, mostrarlos
				if (bmUpdateResult.hasErrors()) {

					bmoOrderDeliveryItem = new BmoOrderDeliveryItem();
					//					productLabel.setText("");
					//					orderQuantityLabel.setText("");
					//					orderBalanceLabel.setText("");
					//					barcodeTextBox.setFocus(true);
					//					serialTextBox.setEnabled(false);
					clearForm();
					showSystemMessage("Error al buscar el Código de Barras: " + bmUpdateResult.errorsToString());

				} else {																
			
					// Revisa el tipo de objecto recibido
					if (bmUpdateResult.getBmObject() instanceof BmoOrderDeliveryItem) {				
						// Se obtiene resultado correcto
						bmoOrderDeliveryItem = (BmoOrderDeliveryItem)bmUpdateResult.getBmObject();	
						if (bmoOrderDeliveryItem.getBmoProduct().getType().equals(BmoProduct.TYPE_COMPLEMENTARY) 
								&& !(bmoOrderDeliveryItem.getBmoOrderItem().getId() > 0)) {		
														
							
							productLabel.setText(bmoOrderDeliveryItem.getBmoProduct().getName().toString());
							orderQuantityLabel.setText(bmoOrderDeliveryItem.getBmoOrderItem().getQuantity().toString());
							orderBalanceLabel.setText(bmoOrderDeliveryItem.getQuantityBalance().toString());
							quantityTextBox.setText(bmoOrderDeliveryItem.getQuantity().toString());
							quantityTextBox.setEnabled(false);						
							serialTextBox.setText(barcodeTextBox.getText().toUpperCase());
							populateWhStockListBox(-1, barcodeTextBox.getText().toUpperCase());
							
							
							if (bmoOrderDeliveryItem.getBmoProduct().getTrack().equals(BmoProduct.TRACK_SERIAL)){
								updateWhSections();
								quantityTextBox.setText("1");
								prepareSave();
							}
							if (bmoOrderDeliveryItem.getBmoProduct().getTrack().equals(BmoProduct.TRACK_BATCH)){						
								updateWhSections();
								quantityTextBox.setEnabled(true);
								if (bmoOrderDelivery.getType().equals(BmoOrderDelivery.TYPE_DELIVERY)) {
									whStockListBox.setEnabled(true);
								}else {
									formTable.hideField(whStockListBox);
								}
								
							}
							setWhStockListBoxFilters(bmoOrderDeliveryItem.getProductId().toInteger(), serialTextBox.getText());
							
							
					
							
						}else {
							productLabel.setText(bmoOrderDeliveryItem.getBmoProduct().getName().toString());
							orderQuantityLabel.setText(bmoOrderDeliveryItem.getBmoOrderItem().getQuantity().toString());
							orderBalanceLabel.setText(bmoOrderDeliveryItem.getQuantityBalance().toString());
							quantityTextBox.setText(bmoOrderDeliveryItem.getQuantity().toString());

							// Depende el tipo de rastreo
							if (bmoOrderDeliveryItem.getBmoProduct().getTrack().equals(BmoProduct.TRACK_NONE)) {
								serialTextBox.setText("");
								quantityTextBox.setEnabled(true);
								quantityTextBox.setFocus(true);

								// Si no esta asignada la cantidad, elegir fuente
								if (!(bmoOrderDeliveryItem.getQuantity().toDouble() > 0)) {

									// Tipo Envio
									if (bmoOrderDelivery.getType().equals(BmoOrderDelivery.TYPE_DELIVERY)) {

										// Asignar filtros de Existencias y Tipos de Almacen
										populateWhStockListBox(bmoOrderDeliveryItem.getBmoOrderItem().getProductId().toInteger(), "-1");

										//									// Filtro de existencias
										//									BmFilter filterByProduct = new BmFilter();
										//									filterByProduct.setValueFilter(bmoWhStock.getKind(), bmoWhStock.getProductId(), bmoOrderDeliveryItem.getBmoOrderItem().getProductId().toInteger());
										//
										//									// Filtro de tipos de almacen
										//									BmFilter filterByWarehouseType = new BmFilter();
										//									filterByWarehouseType.setValueFilter(bmoWhStock.getKind(), bmoWhStock.getBmoWhSection().getBmoWarehouse().getType(), "" + BmoWarehouse.TYPE_NORMAL);
										//
										//									whStockListBox = new UiListBox(getUiParams(), new BmoWhStock());
										//									whStockListBox.addFilter(filterByProduct);					
										//									whStockListBox.addFilter(filterByWarehouseType);
										//
										//									// Listado de existencias
										//									formTable.addField(2, 0, whStockListBox, bmoWhStock.getIdField());
										whStockListBox.setEnabled(true);
									} else {
										// Ya tiene cantidad
										updateWhSections();

										// No se puede mover el almacen destino
										toWhSectionSuggestBox.setEnabled(false);

										// Ocultar combo de Existencias
										formTable.hideField(whStockListBox);
									}
								} else {
									updateWhSections();
								}
							
							} 
							// Rastro tipo lotes
							else if (bmoOrderDeliveryItem.getBmoProduct().getTrack().equals(BmoProduct.TRACK_BATCH)) {
								serialTextBox.setText(bmoOrderDeliveryItem.getSerial().toString());
								quantityTextBox.setEnabled(true);
							
								// Si la busqueda fue de numero de serie, asignarlo y guardar
								if (!barcodeTextBox.getText().toUpperCase().equals(bmoOrderDeliveryItem.getBmoProduct().getCode().toString().toUpperCase())) {
									serialTextBox.setText(barcodeTextBox.getText().toUpperCase());
									quantityTextBox.setFocus(true);

									// Si no esta asignada la cantidad, elegir fuente
									if (!(bmoOrderDeliveryItem.getQuantity().toDouble() > 0)) {

										// Seleccionar origen si es de tipo envio
										if (bmoOrderDelivery.getType().equals(BmoOrderDelivery.TYPE_DELIVERY)) {

											// Asignar filtros de Existencias y Tipos de Almacen
											populateWhStockListBox(-1, barcodeTextBox.getText().toUpperCase());

											//										// Filtro de existencias
											//										BmFilter filterBySerial = new BmFilter();
											//										filterBySerial.setValueFilter(bmoWhStock.getKind(), bmoWhStock.getBmoWhTrack().getSerial(), barcodeTextBox.getText().toUpperCase());
											//
											//										// Filtro de tipos de almacen
											//										BmFilter filterByWarehouseType = new BmFilter();
											//										filterByWarehouseType.setValueFilter(bmoWhStock.getKind(), bmoWhStock.getBmoWhSection().getBmoWarehouse().getType(), "" + BmoWarehouse.TYPE_NORMAL);
											//
											//										whStockListBox = new UiListBox(getUiParams(), new BmoWhStock());
											//										whStockListBox.addFilter(filterBySerial);
											//										whStockListBox.addFilter(filterByWarehouseType);
											//
											//										// Listado de existencias
											//										formTable.addField(2, 0, whStockListBox, bmoWhStock.getIdField());
											whStockListBox.setEnabled(true);
										} else {
											updateWhSections();

											// Ocultar combo de Existencias
											//formTable.hideField(whStockListBox);
										}
									
										serialTextBox.setEnabled(true);
									} else {
										// Ya tiene cantidad
										updateWhSections();
									
										// No se puede mover el almacen destino
										toWhSectionSuggestBox.setEnabled(false);

										// Ocultar combo de Existencias
										if (bmoOrderDelivery.getType().equals(BmoOrderDelivery.TYPE_RETURN))
											formTable.hideField(whStockListBox);
									}

								} else { 
									showSystemMessage("No es un #Lote Válido.");

									clearForm();
								}
							
							} 
							// Rastreo tipo Serie
							else if (bmoOrderDeliveryItem.getBmoProduct().getTrack().equals(BmoProduct.TRACK_SERIAL)) {					

								// Asigna valores default
								quantityTextBox.setText("1");
								quantityTextBox.setEnabled(false);

								// Si la busqueda fue de numero de serie, asignarlo y guardar
								if (!barcodeTextBox.getText().toUpperCase().equals(bmoOrderDeliveryItem.getBmoProduct().getCode().toString().toUpperCase())) {
									serialTextBox.setText(barcodeTextBox.getText().toUpperCase());	

									// Actualiza secciones
									updateWhSections();

									// Guarda en automatico si es envio
									if (bmoOrderDelivery.getType().equals(BmoOrderDelivery.TYPE_DELIVERY))
										prepareSave();
									else {
										// Ocultar combo de Existencias
										formTable.hideField(whStockListBox);
										prepareSave();
									}
								} else { 
									showSystemMessage("No es un #Serie Válido.");
									clearForm();
								}
							}

							barcodeTextBox.setText(barcodeTextBox.getText().toUpperCase());
						}//Fin else Complementarios
					}// 
					else if (bmUpdateResult.getBmObject() instanceof BmoWhBox) {
						BmoWhBox bmoWhBox = (BmoWhBox)bmUpdateResult.getBmObject();

						clearForm();

						reset();

						// Es una caja la que se ingreso
						showSystemMessage("Se Agrego la Caja de Items: " + bmoWhBox.getCode() + " " + bmoWhBox.getName());
					}
				} 
			}

			// Si se cambia el listado de whstocks activar evento
			public void whStockformListChange(ChangeEvent event) {
				if (event.getSource() == whStockListBox) {

					// Actualiza el origen
					if (!whStockListBox.getSelectedId().equals("")) {
						BmoWhStock bmoWhStock = (BmoWhStock)whStockListBox.getSelectedBmObject();
						try {
							bmoOrderDeliveryItem.getFromWhSectionId().setValue(bmoWhStock.getWhSectionId().toInteger());
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
				formTable.addField(3, 0, fromWhSectionSuggestBox, bmoOrderDeliveryItem.getFromWhSectionId());
				fromWhSectionSuggestBox.setEnabled(false);

				// Sección Destino
				// Filtro de tipos de almacen
				
				BmFilter filterByWarehouseType = new BmFilter();
				filterByWarehouseType.setValueFilter(bmoWhStock.getKind(), bmoWhStock.getBmoWhSection().getBmoWarehouse().getType(), "" + BmoWarehouse.TYPE_NORMAL);
				
				
				toWhSectionSuggestBox = new UiSuggestBox(new BmoWhSection(), filterByWarehouseType);
				
				formTable.addField(4, 0, toWhSectionSuggestBox, bmoOrderDeliveryItem.getToWhSectionId());

				toWhSectionSuggestBox.setEnabled(false);	

				// Si es devolucion, habilitar cambiar seccion destino
//				if (bmoOrderDelivery.getType().equals(BmoOrderDelivery.TYPE_RETURN))
//					toWhSectionSuggestBox.setEnabled(true);	
			}

			public void prepareSave() {
				try {
					if (quantityTextBox.getText().equals(""))
						showErrorMessage("La Cantidad no puede estar vacía.");
					else {
						bmoOrderDeliveryItem.getSerial().setValue(serialTextBox.getText());
						bmoOrderDeliveryItem.getQuantity().setValue(quantityTextBox.getText());
						bmoOrderDeliveryItem.getFromWhSectionId().setValue(fromWhSectionSuggestBox.getSelectedId());				
						bmoOrderDeliveryItem.getToWhSectionId().setValue(toWhSectionSuggestBox.getSelectedId());

						save();
					}
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
						getUiParams().getBmObjectServiceAsync().save(bmoOrderDeliveryItem.getPmClass(), bmoOrderDeliveryItem, callback);
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
					clearForm();

					reset();
				}
			}

			// Llena combo Invetario/Existencias
			private void populateWhStockListBox(int productId, String barcode) {
				whStockListBox.clear();
				whStockListBox.clearFilters();
				setWhStockListBoxFilters(productId, barcode);
				whStockListBox.populate(bmoWhStock.getIdField().toString(), false);
			}

			// Asigna filtros a Invetarios/Existencias
			private void setWhStockListBoxFilters(int productId, String barcode) {
				BmoWhStock bmoWhStock = new BmoWhStock();
				
				// Filtro de Producto
				if (productId > 0) {

					// Filtro de Almacenes de Tipo Normal
					BmFilter filterByWarehouseType = new BmFilter();
					filterByWarehouseType.setValueFilter(bmoWhStock.getKind(), bmoWhStock.getBmoWhSection().getBmoWarehouse().getType(), "" + BmoWarehouse.TYPE_NORMAL);
					whStockListBox.addFilter(filterByWarehouseType);

					// Filtro de existencias
					BmFilter filterByProduct = new BmFilter();
					filterByProduct.setValueFilter(bmoWhStock.getKind(), bmoWhStock.getProductId(), bmoOrderDeliveryItem.getBmoOrderItem().getProductId().toInteger());
					whStockListBox.addBmFilter(filterByProduct);
				}
				// Filtro por Serie/Lote(Código de Barras)
				else if (!barcode.equals("")) {
					
					
					// Filtro de Almacenes de Tipo Normal
					BmFilter filterByWarehouseType = new BmFilter();
					filterByWarehouseType.setValueFilter(bmoWhStock.getKind(), bmoWhStock.getBmoWhSection().getBmoWarehouse().getType(), "" + BmoWarehouse.TYPE_NORMAL);
					whStockListBox.addFilter(filterByWarehouseType);

					// Filtro de existencias no #Serie
					BmFilter filterBySerial = new BmFilter();
					filterBySerial.setValueFilter(bmoWhStock.getKind(), bmoWhStock.getBmoWhTrack().getSerial(), barcodeTextBox.getText().toUpperCase());
					whStockListBox.addBmFilter(filterBySerial);
				} else {

					BmFilter bmFilterLimit = new BmFilter();
					bmFilterLimit.setValueFilter(bmoWhStock.getKind(), bmoWhStock.getIdField(), -1);
					whStockListBox.addBmFilter(bmFilterLimit);
				}
			}

			// Limpia forma
			private void clearForm() {

				bmoOrderDeliveryItem = new BmoOrderDeliveryItem();
				barcodeTextBox.setText("");
				serialTextBox.setText("");
				serialTextBox.setEnabled(false);
				quantityTextBox.setText("");
				barcodeTextBox.setFocus(true);

				productLabel.setText("");
				orderQuantityLabel.setText("");
				orderBalanceLabel.setText("");

				populateWhStockListBox(-1, "");
				whStockListBox.setEnabled(false);
				fromWhSectionSuggestBox.clear();
				toWhSectionSuggestBox.clear();
				toWhSectionSuggestBox.setEnabled(false);
			}

			private void closeBarcodeDialog() {
				barcodeDialogBox.setVisible(false);
			}

			// Variables para llamadas RPC
			public int getSearchBarcodeRpcAttempt() {
				return searchBarcodeRpcAttempt;
			}

			public void setSearchBarcodeRpcAttempt(int searchBarcodeRpcAttempt) {
				this.searchBarcodeRpcAttempt = searchBarcodeRpcAttempt;
			}

			public String getBarcode() {
				return barcode;
			}

			public void setBarcode(String barcode) {
				this.barcode = barcode;
			}
		}

		// Variables para llamadas RPC
		public int getUpdateAmountRpcAttempt() {
			return updateAmountRpcAttempt;
		}

		public void setUpdateAmountRpcAttempt(int updateAmountRpcAttempt) {
			this.updateAmountRpcAttempt = updateAmountRpcAttempt;
		}

		public int getSaveItemChangeRpcAttempt() {
			return saveItemChangeRpcAttempt;
		}

		public void setSaveItemChangeRpcAttempt(int saveItemChangeRpcAttempt) {
			this.saveItemChangeRpcAttempt = saveItemChangeRpcAttempt;
		}

		public int getIdOrderDeliveryRPC() {
			return idOrderDeliveryRPC;
		}

		public void setIdOrderDeliveryRPC(int idOrderDeliveryRPC) {
			this.idOrderDeliveryRPC = idOrderDeliveryRPC;
		}
	}
}
