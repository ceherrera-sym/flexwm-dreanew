package com.flexwm.client.co;

import com.flexwm.client.op.UiOrderForm.OrderUpdater;
import com.flexwm.shared.co.BmoOrderPropertyTax;
import com.flexwm.shared.op.BmoOrder;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent;
import com.google.gwt.user.cellview.client.SafeHtmlHeader;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent.LoadingState;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiFormFlexTable;
import com.symgae.client.ui.UiListDataProvider;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;


public class UiOrderPropertyTaxGrid extends Ui {
	private UiFormFlexTable formFlexTable = new UiFormFlexTable(getUiParams());

	// OrderProperty
	private BmoOrderPropertyTax bmoOrderPropertyTax = new BmoOrderPropertyTax();
	private FlowPanel orderPropertyTaxPanel = new FlowPanel();
	private CellTable<BmObject> orderPropertyTaxGrid = new CellTable<BmObject>();
	private UiListDataProvider<BmObject> orderPropertyTaxData;
	BmFilter orderPropertyTaxFilter;
	int months = 0;
	double prices = 0;

	// Otros
	private NumberFormat numberFormat = NumberFormat.getDecimalFormat();
	private BmoOrder bmoOrder;
	private OrderUpdater orderUpdater;

	public UiOrderPropertyTaxGrid(UiParams uiParams, Panel defaultPanel, BmoOrder bmoOrder, OrderUpdater orderUpdater){
		super(uiParams, defaultPanel);
		this.bmoOrder = bmoOrder;
		this.orderUpdater = orderUpdater;
		

		// Inicializar grid de Inmueble
		orderPropertyTaxGrid = new CellTable<BmObject>();
		orderPropertyTaxGrid.setWidth("100%");
		orderPropertyTaxPanel.clear();
		orderPropertyTaxPanel.setWidth("100%");
		defaultPanel.setStyleName("detailStart");
		setOrderPropertyColumns();
		orderPropertyTaxFilter = new BmFilter();
		orderPropertyTaxFilter.setValueFilter(bmoOrderPropertyTax.getKind(), bmoOrderPropertyTax.getOrderId().getName(), bmoOrder.getId());
		orderPropertyTaxGrid.addLoadingStateChangeHandler(new LoadingStateChangeEvent.Handler() {           
			@Override
			public void onLoadingStateChanged(LoadingStateChangeEvent event) {
				if (event.getLoadingState() == LoadingState.LOADED) {
					changeHeight();
				}
			}
		}); 
		orderPropertyTaxData = new UiListDataProvider<BmObject>(new BmoOrderPropertyTax(), orderPropertyTaxFilter);
		orderPropertyTaxData.addDataDisplay(orderPropertyTaxGrid);
		orderPropertyTaxPanel.add(orderPropertyTaxGrid);

		// Crear forma y campos
		formFlexTable.setWidth("100%");
//		formFlexTable.addSectionLabel(1, 0, "Arrendamiento", 2);
//				formTable.addField(2, 0, showQuantityCheckBox, bmoOrder.get());
//				formTable.addField(2, 0, showPriceCheckBox, bmoOrderProperty.getShowPrice());
//				formFlexTable.addLabelField(2, 2, bmoOrderProperty.getAmount(), amountLabel);
		formFlexTable.addPanel(3, 0, orderPropertyTaxPanel);
		defaultPanel.add(formFlexTable);
	}

	public void show(){
		orderPropertyTaxData.list();
		orderPropertyTaxGrid.redraw();
	}

	public void reset(){
		orderUpdater.changeOrderPropertyTax();
	}

	public void changeHeight() {
		orderPropertyTaxGrid.setPageSize(orderPropertyTaxData.getList().size());
		orderPropertyTaxGrid.setVisibleRange(0, orderPropertyTaxData.getList().size());
	}

	// Columnas grid de Inmueble
	public void setOrderPropertyColumns() {
		// Clave
		Column<BmObject, String> codeColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoOrderPropertyTax)bmObject).getBmoProperty().getCode().toString();
			}
		};
		orderPropertyTaxGrid.addColumn(codeColumn, SafeHtmlUtils.fromSafeConstant("Clave"));
		orderPropertyTaxGrid.setColumnWidth(codeColumn, 150, Unit.PX);


			// Descripcion
			Column<BmObject, String> blockColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoOrderPropertyTax)bmObject).getBmoProperty().getDescription().toString();
				}
			};
			orderPropertyTaxGrid.addColumn(blockColumn, SafeHtmlUtils.fromSafeConstant("Descripción"));
			orderPropertyTaxGrid.setColumnWidth(blockColumn, 150, Unit.PX);

		// Calle y numero
		Column<BmObject, String> streetColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoOrderPropertyTax)bmObject).getBmoProperty().getStreet().toString() + " "
						+ " #" + ((BmoOrderPropertyTax)bmObject).getBmoProperty().getNumber().toString();
			}
		};
		orderPropertyTaxGrid.addColumn(streetColumn, SafeHtmlUtils.fromSafeConstant("Calle y Número"));
		orderPropertyTaxGrid.setColumnWidth(streetColumn, 200, Unit.PX);
		
		//Meses
		Column<BmObject, String> monthsColumn;
		monthsColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				// con un asicrono obtener los meses de f.inicio y f.fin del pedido
				return ((BmoOrderPropertyTax)bmObject).getQuantity().toString();
//				return String.valueOf(months);
			}
		};
//		monthsColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
//			@Override
//			public void update(int index, BmObject bmObject, String value) {
//				changeOrderEquipmentDays(bmObject, value);
//			}
//		});
		
		
		SafeHtmlHeader daysHeader = new SafeHtmlHeader(new SafeHtml() { 
			private static final long serialVersionUID = 1L;
			@Override 
			public String asString() { 
				return "<p style=\"text-center;\">Meses</p>"; 
			} 
		}); 
		orderPropertyTaxGrid.addColumn(monthsColumn, daysHeader);
		orderPropertyTaxGrid.setColumnWidth(monthsColumn, 50, Unit.PX);
		monthsColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
//	}
		
	// Precio
			Column<BmObject, String> priceColumn;
				if (bmoOrder.getStatus().equals(BmoOrder.STATUS_REVISION)) {
					priceColumn = new Column<BmObject, String>(new EditTextCell()) {
						@Override
						public String getValue(BmObject bmObject) {
							return ((BmoOrderPropertyTax)bmObject).getPrice().toString();
						}
					};
					priceColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
						@Override
						public void update(int index, BmObject bmObject, String value) {
							changeOrderPropertyPrice(bmObject, value);
						}
					});
				}
				else {
					priceColumn = new Column<BmObject, String>(new TextCell()) {
						@Override
						public String getValue(BmObject bmObject) {
							return ((BmoOrderPropertyTax)bmObject).getPrice().toString();
						}
					};
				}
				SafeHtmlHeader priceHeader = new SafeHtmlHeader(new SafeHtml() { 
					private static final long serialVersionUID = 1L;
					@Override 
					public String asString() { 
						return "<p style=\"text-align:right;\">Precio</p>"; 
					} 
				}); 
				orderPropertyTaxGrid.addColumn(priceColumn, priceHeader);
				orderPropertyTaxGrid.setColumnWidth(priceColumn, 50, Unit.PX);
				priceColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

//				@Override
//				public String getValue(BmObject bmObject) {
//					numberFormat = NumberFormat.getCurrencyFormat()
//					String formatted = numberFormat.format(((BmoOrderPropertyTax)bmObject).getPrice().toDouble());
//					return (formatted);
//				}
//			};
//			priceColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
//			orderPropertyTaxGrid.addColumn(priceColumn, SafeHtmlUtils.fromSafeConstant("Precio"));


		// Total
		Column<BmObject, String> totalColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				numberFormat = NumberFormat.getCurrencyFormat();
				String formatted = numberFormat.format(((BmoOrderPropertyTax)bmObject).getAmount().toDouble());
				return (formatted);
			}
		};
		totalColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		orderPropertyTaxGrid.addColumn(totalColumn, SafeHtmlUtils.fromSafeConstant("Total"));
	}
	
	public void changeOrderPropertyPrice(BmObject bmObject, String price) {
		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_REVISION) {
			bmoOrderPropertyTax = (BmoOrderPropertyTax)bmObject;
			try {
				Double p = Double.parseDouble(price);
				bmoOrderPropertyTax.getPrice().setValue(p);
				bmoOrderPropertyTax.getAmount().setValue(p*bmoOrderPropertyTax.getQuantity().toInteger());
				savePropertyChange();				

//				if (bmoOrderPropertyTax.getPropertyId().toInteger() > 0) {
//					// Es de tipo Producto
//					if (p == 0) {
//						bmoOrderEquipment.getPrice().setValue(0);
//						saveEquipmentChange();
//					} else if (p > 0) {
//
//						if (p >= bmoOrderEquipment.getBasePrice().toDouble()) {
//							// Se esta subiendo precio, permitirlo
//							bmoOrderEquipment.getPrice().setValue(p);
//							saveEquipmentChange();
//							reset();
//
//						} else if (p < bmoOrderEquipment.getBasePrice().toDouble()) {
//							// El precio es menor al precio base, validar permisos
//							if (getSFParams().hasSpecialAccess(BmoOrder.ACCESS_CHANGEITEMPRICE)) {
//								bmoOrderEquipment.getPrice().setValue(p);
//								saveEquipmentChange();
//								reset();
//							} else {
//								numberFormat = NumberFormat.getCurrencyFormat();
//								String formatted = numberFormat.format(bmoOrderEquipment.getBasePrice().toDouble());
//								showSystemMessage("No cuenta con Permisos para establecer Precio menor a: " + formatted);
//								reset();
//							}
//						}
//
//					} else {
//						showSystemMessage("El Precio no puede ser menor a $0.00");
//						reset();
//					}
//				} else {
//					// No es de tipo producto, permite todo
//					bmoOrderEquipment.getPrice().setValue(p);
//					saveEquipmentChange();
//				}
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-changePrice(): ERROR " + e.toString());
			}		
		} else {
			showSystemMessage("No se puede editar el Precio - Está Autorizada.");
			this.reset();
		}
	}
	
	public void savePropertyChange() {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-savePropertyChange(): ERROR " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				processPropertyChangeSave(result);
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().save(bmoOrderPropertyTax.getPmClass(),bmoOrderPropertyTax,callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-savePropertyChange(): ERROR " + e.toString());
		}
	}
	
		
	public void processPropertyChangeSave(BmUpdateResult bmUpdateResult) {
		if (bmUpdateResult.hasErrors()) 
			showSystemMessage("Error al modificar Inmueble: " + bmUpdateResult.errorsToString());
		this.reset();
	}
}
