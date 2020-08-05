package com.flexwm.client.cm;


import java.util.ArrayList;
import java.util.Iterator;
import com.flexwm.client.cm.UiQuoteForm.QuoteUpdater;
import com.flexwm.shared.op.BmoEquipment;
import com.flexwm.shared.cm.BmoQuote;
import com.flexwm.shared.cm.BmoQuoteEquipment;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent;
import com.google.gwt.user.cellview.client.SafeHtmlHeader;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent.LoadingState;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiFormFlexTable;
import com.symgae.client.ui.UiListDataProvider;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.client.ui.UiSuggestBoxAction;


public class UiQuoteEquipmentGrid extends Ui {
	private UiFormFlexTable formFlexTable = new UiFormFlexTable(getUiParams());

	// QuoteEquipments
	// Cambio de Recursos
	UiSuggestBox changeEquipmentSuggestBox;
	private BmoQuoteEquipment bmoQuoteEquipment = new BmoQuoteEquipment();
	private FlowPanel quoteEquipmentPanel = new FlowPanel();
	private CellTable<BmObject> quoteEquipmentGrid = new CellTable<BmObject>();
	private UiListDataProvider<BmObject> quoteEquipmentData;
	BmFilter quoteEquipmentFilter;
	private TextArea quoteEquipmentDescriptionTextArea = new TextArea();
	DialogBox quoteEquipmentDescriptionDialogBox;
	Button changeEquipmentDescriptionButton = new Button("GUARDAR");
	Button closeEquipmentDescriptionButton = new Button("CERRAR");

	private Button addQuoteEquipmentButton = new Button("+RECURSO");
	protected DialogBox quoteEquipmentDialogBox;	

	// Cambio de Equipment
	DialogBox changeEquipmentDialogBox;
	Button changeEquipmentButton = new Button("CAMBIAR");
	Button closeEquipmentButton = new Button("CERRAR");

	// Arbol
	public final MultiSelectionModel<BmObject> selectionQuoteEquipmentModel = new MultiSelectionModel<BmObject>();
	private UiQuoteEquipmentTreeModel uiQuoteEquipmentTreeModel = new UiQuoteEquipmentTreeModel(selectionQuoteEquipmentModel);
	private Button treeShowButton = new Button("NAVEGADOR");
	private Button treeCloseButton = new Button("CERRAR");
	CellTree cellEquipmentTree;
	Label treeEquipmentLabel = new Label("Agregar Recurso: ");

	// Otros
	private HorizontalPanel buttonPanel = new HorizontalPanel();
	private CheckBox showQuantityCheckBox = new CheckBox();
	private CheckBox showPriceCheckBox = new CheckBox();
	//	private Label amountLabel = new Label();
	private NumberFormat numberFormat = NumberFormat.getDecimalFormat();
	private BmoQuote bmoQuote;
	private QuoteUpdater quoteUpdater;
	private int saveEquipmentChangeRpcAttempt = 0;

	public UiQuoteEquipmentGrid(UiParams uiParams, Panel defaultPanel, BmoQuote bmoQuote, QuoteUpdater quoteUpdater){
		super(uiParams, defaultPanel);
		this.bmoQuote = bmoQuote;
		this.quoteUpdater = quoteUpdater;

		// Arbol de seleccion
		selectionQuoteEquipmentModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				Iterator<BmObject> i = selectionQuoteEquipmentModel.getSelectedSet().iterator();
				BmObject bmObject;
				if (i.hasNext()) {
					bmObject = (BmObject)i.next();

					// Se esta agregando un producto directo
					if (bmObject instanceof BmoEquipment) {
						addEquipment((BmoEquipment)bmObject);
					}
				}
			}
		});
		cellEquipmentTree = new CellTree(uiQuoteEquipmentTreeModel, selectionQuoteEquipmentModel);
		cellEquipmentTree.setAnimationEnabled(true);
		cellEquipmentTree.addStyleName("quoteProductTree");
		treeEquipmentLabel.setStyleName("detailLabelTitle");

		treeShowButton.setStyleName("formCloseButton");
		treeShowButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				showTree();
			}
		});
		treeCloseButton.setStyleName("formCloseButton");
		treeCloseButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				hideTree();
			}
		});

		closeEquipmentButton.setStyleName("formCloseButton");
		closeEquipmentButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				changeEquipmentDialogBox.hide();
			}
		});

		// Boton de cerrar dialogo de personal
		changeEquipmentDescriptionButton.setStyleName("formCloseButton");
		changeEquipmentDescriptionButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				changeQuoteEquipmentDescription();
				quoteEquipmentDescriptionDialogBox.hide();
			}
		});

		closeEquipmentDescriptionButton.setStyleName("formCloseButton");
		closeEquipmentDescriptionButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				quoteEquipmentDescriptionDialogBox.hide();
			}
		});

		

		// Inicializar grid de Personal
		quoteEquipmentGrid = new CellTable<BmObject>();
		quoteEquipmentGrid.setWidth("100%");
		quoteEquipmentPanel.clear();
		quoteEquipmentPanel.setWidth("100%");
		setQuoteEquipmentColumns();
		quoteEquipmentFilter = new BmFilter();
		quoteEquipmentFilter.setValueFilter(bmoQuoteEquipment.getKind(), bmoQuoteEquipment.getQuoteId().getName(), bmoQuote.getId());
		quoteEquipmentGrid.addLoadingStateChangeHandler(new LoadingStateChangeEvent.Handler() {           
			@Override
			public void onLoadingStateChanged(LoadingStateChangeEvent event) {
				if (event.getLoadingState() == LoadingState.LOADED) {
					changeHeight();
				}
			}
		}); 
		quoteEquipmentData = new UiListDataProvider<BmObject>(new BmoQuoteEquipment(), quoteEquipmentFilter);
		quoteEquipmentData.addDataDisplay(quoteEquipmentGrid);
		quoteEquipmentPanel.add(quoteEquipmentGrid);

		// Panel de botones
		addQuoteEquipmentButton.setStyleName("formSaveButton");
		addQuoteEquipmentButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addEquipment();
			}
		});
		buttonPanel.setHeight("100%");
		buttonPanel.setStyleName("formButtonPanel");
		buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		buttonPanel.add(addQuoteEquipmentButton);
		buttonPanel.add(treeShowButton);

		// Crear forma y campos
		formFlexTable.setWidth("100%");
		formFlexTable.addPanel(3, 0, quoteEquipmentPanel, 4);
		formFlexTable.addButtonPanel(buttonPanel);
		defaultPanel.add(formFlexTable);
	}

	public void show(){
		quoteEquipmentData.list();
		quoteEquipmentGrid.redraw();

		statusEffect();
	}

	public void statusEffect(){
		if (bmoQuote.getStatus().toChar() == BmoQuote.STATUS_AUTHORIZED || bmoQuote.getStatus().toChar() == BmoQuote.STATUS_CANCELLED) {
			showQuantityCheckBox.setEnabled(false);
			showPriceCheckBox.setEnabled(false);
			addQuoteEquipmentButton.setVisible(false);
			treeShowButton.setVisible(false);
		} else {
			showQuantityCheckBox.setEnabled(true);
			showPriceCheckBox.setEnabled(true);	
			addQuoteEquipmentButton.setVisible(true);
			treeShowButton.setVisible(true);
		}
	}

	private void showTree() {
		getUiParams().getUiTemplate().showEastPanel();
		getUiParams().getUiTemplate().getEastPanel().add(new HTML("<pre> </pre>"));
		getUiParams().getUiTemplate().getEastPanel().add(new HTML("<pre> </pre>"));
		getUiParams().getUiTemplate().getEastPanel().add(treeEquipmentLabel);
		getUiParams().getUiTemplate().getEastPanel().add(cellEquipmentTree);
		getUiParams().getUiTemplate().getEastPanel().add(treeCloseButton);
	}

	private void hideTree() {
		getUiParams().getUiTemplate().hideEastPanel();
	}

	public void reset(){
		quoteUpdater.changeQuoteEquipment();
	}

	public void changeHeight() {
		quoteEquipmentGrid.setVisibleRange(0, quoteEquipmentData.getList().size());
	}

	public void addEquipment(){
		addEquipment(new BmoEquipment());
	}

	// Columnas
	public void setQuoteEquipmentColumns() {
		// Cantidad
		Column<BmObject, String> quantityColumn;
		if (bmoQuote.getStatus().equals(BmoQuote.STATUS_REVISION)) {
			quantityColumn = new Column<BmObject, String>(new EditTextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoQuoteEquipment)bmObject).getQuantity().toString();
				}
			};
			quantityColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					changeQuoteEquipmentQuantity(bmObject, value);
				}
			});
		} else {
			quantityColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoQuoteEquipment)bmObject).getQuantity().toString();
				}
			};
		}
		quoteEquipmentGrid.addColumn(quantityColumn, SafeHtmlUtils.fromSafeConstant("Cant."));
		quoteEquipmentGrid.setColumnWidth(quantityColumn, 50, Unit.PX);
		quantityColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		// Nombre
		Column<BmObject, String> nameColumn;
		nameColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoQuoteEquipment)bmObject).getName().toString();
			}
		};
		quoteEquipmentGrid.addColumn(nameColumn, SafeHtmlUtils.fromSafeConstant("Nombre"));
		quoteEquipmentGrid.setColumnWidth(nameColumn, 100, Unit.PX);
		nameColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

		if (!isMobile()) {
			// Columna descripcion
			Column<BmObject, String> descriptionColumn = new Column<BmObject, String>(new ButtonCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					if (((BmoQuoteEquipment)bmObject).getDescription().toString().length() > 0)
						return "...";
					else return ".";
				}
			};
			descriptionColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					quoteEquipmentDescriptionDialog((BmoQuoteEquipment)bmObject);
				}
			});
			quoteEquipmentGrid.addColumn(descriptionColumn, SafeHtmlUtils.fromSafeConstant("Desc."));
			quoteEquipmentGrid.setColumnWidth(descriptionColumn, 50, Unit.PX);
			descriptionColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

			// Recurso
			Column<BmObject, String> equipmentColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoQuoteEquipment)bmObject).getBmoEquipment().getName().toString();
				}
			};
			equipmentColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			quoteEquipmentGrid.addColumn(equipmentColumn, SafeHtmlUtils.fromSafeConstant("Recurso"));
			quoteEquipmentGrid.setColumnWidth(equipmentColumn, 100, Unit.PX);

			// Días
			Column<BmObject, String> daysColumn;
			if (bmoQuote.getStatus().equals(BmoQuote.STATUS_REVISION)) {
				daysColumn = new Column<BmObject, String>(new EditTextCell()) {
					@Override
					public String getValue(BmObject bmObject) {
						return ((BmoQuoteEquipment)bmObject).getDays().toString();
					}
				};
				daysColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
					@Override
					public void update(int index, BmObject bmObject, String value) {
						changeQuoteEquipmentDays(bmObject, value);
					}
				});
			} else {
				daysColumn = new Column<BmObject, String>(new TextCell()) {
					@Override
					public String getValue(BmObject bmObject) {
						return ((BmoQuoteEquipment)bmObject).getDays().toString();
					}
				};
			}
			SafeHtmlHeader daysHeader = new SafeHtmlHeader(new SafeHtml() { 
				private static final long serialVersionUID = 1L;
				@Override 
				public String asString() { 
					return "<p style=\"text-center;\">D&iacute;as</p>"; 
				} 
			}); 
			quoteEquipmentGrid.addColumn(daysColumn, daysHeader);
			quoteEquipmentGrid.setColumnWidth(daysColumn, 50, Unit.PX);
			quantityColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		}
		// Precio
		Column<BmObject, String> priceColumn;
		if (bmoQuote.getStatus().equals(BmoQuote.STATUS_REVISION)) {
			priceColumn = new Column<BmObject, String>(new EditTextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoQuoteEquipment)bmObject).getPrice().toString();
				}
			};
			priceColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					changeQuoteEquipmentPrice(bmObject, value);
				}
			});
		} else {
			priceColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoQuoteEquipment)bmObject).getPrice().toString();
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
		quoteEquipmentGrid.addColumn(priceColumn, priceHeader);
		quoteEquipmentGrid.setColumnWidth(priceColumn, 50, Unit.PX);
		priceColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

		// Columna de Total
		Column<BmObject, String> totalColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				numberFormat = NumberFormat.getCurrencyFormat();
				String formatted = numberFormat.format(((BmoQuoteEquipment)bmObject).getAmount().toDouble());
				return (formatted);
			}
		};
		SafeHtmlHeader totalHeader = new SafeHtmlHeader(new SafeHtml() { 
			private static final long serialVersionUID = 1L;

			@Override 
			public String asString() { 
				return "<p style=\"text-align:right;\">Total</p>"; 
			} 
		}); 
		quoteEquipmentGrid.addColumn(totalColumn, totalHeader);
		quoteEquipmentGrid.setColumnWidth(totalColumn, 50, Unit.PX);
		totalColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

		// Eliminar
		Column<BmObject, String> deleteColumn;
		if (bmoQuote.getStatus().equals(BmoQuote.STATUS_REVISION)) {
			deleteColumn = new Column<BmObject, String>(new ButtonCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return "-";
				}
			};
			deleteColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					deleteQuoteEquipment((BmoQuoteEquipment)bmObject);
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
		quoteEquipmentGrid.addColumn(deleteColumn, SafeHtmlUtils.fromSafeConstant("Eliminar"));
		deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		quoteEquipmentGrid.setColumnWidth(deleteColumn, 50, Unit.PX);	
	}

	public void addEquipment(BmoEquipment bmoEquipment) {
		quoteEquipmentDialogBox = new DialogBox(true);
		quoteEquipmentDialogBox.setGlassEnabled(true);
		quoteEquipmentDialogBox.setText("Recurso de Cotización");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "250px");

		quoteEquipmentDialogBox.setWidget(vp);

		UiQuoteEquipmentForm quoteEquipmentForm = new UiQuoteEquipmentForm(getUiParams(), vp, bmoQuote, bmoEquipment);

		quoteEquipmentForm.show();

		quoteEquipmentDialogBox.center();
		quoteEquipmentDialogBox.show();
	}

	public void quoteEquipmentDescriptionDialog(BmoQuoteEquipment bmoQuoteEquipment) {
		this.bmoQuoteEquipment = bmoQuoteEquipment;
		quoteEquipmentDescriptionDialogBox = new DialogBox(true);
		quoteEquipmentDescriptionDialogBox.setGlassEnabled(true);
		quoteEquipmentDescriptionDialogBox.setText("Descripción Recurso");
		quoteEquipmentDescriptionDialogBox.setSize("400px", "200px");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "200px");
		quoteEquipmentDescriptionDialogBox.setWidget(vp);

		quoteEquipmentDescriptionTextArea = new TextArea();
		quoteEquipmentDescriptionTextArea.setSize("320px", "190px");
		quoteEquipmentDescriptionTextArea.setText(bmoQuoteEquipment.getDescription().toString());
		vp.add(quoteEquipmentDescriptionTextArea);

		HorizontalPanel descriptionButtonPanel = new HorizontalPanel();
		descriptionButtonPanel.add(changeEquipmentDescriptionButton);
		descriptionButtonPanel.add(closeEquipmentDescriptionButton);
		vp.add(descriptionButtonPanel);

		quoteEquipmentDescriptionDialogBox.center();
		quoteEquipmentDescriptionDialogBox.show();
	}

	public void changeQuoteEquipmentDescription() {
		String description = quoteEquipmentDescriptionTextArea.getText();

		try {
			if (description.length() > 0) {
				bmoQuoteEquipment.getDescription().setValue(description);
				saveEquipmentChange();
			}
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-changeQuoteEquipmentDescription() ERROR: " + e.toString());
		}
	}

	public void changeQuoteEquipmentDays(BmObject bmObject, String days) {
		bmoQuoteEquipment = (BmoQuoteEquipment)bmObject;
		if (bmoQuote.getStatus().toChar() != BmoQuote.STATUS_AUTHORIZED) {
			try {
				double d = Double.parseDouble(days);
				if (d > 0) {
					bmoQuoteEquipment.getDays().setValue(d);
					saveEquipmentChange();
				} else {
					showSystemMessage("Los Días no pueden ser menores a 0.");
					reset();
				}
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-changeDays(): ERROR " + e.toString());
			}
		} else {
			showSystemMessage("La cotización no se puede modificar - está Autorizado.");
		}
	}

	public void changeQuoteEquipmentPrice(BmObject bmObject, String price) {
		if (bmoQuote.getStatus().toChar() == BmoQuote.STATUS_REVISION) {
			bmoQuoteEquipment = (BmoQuoteEquipment)bmObject;
			try {
				Double p = Double.parseDouble(price);

				if (bmoQuoteEquipment.getEquipmentId().toInteger() > 0) {
					// Es de tipo Producto
					if (p == 0) {
						bmoQuoteEquipment.getPrice().setValue(0);
						saveEquipmentChange();
					} else if (p > 0) {

						if (p >= bmoQuoteEquipment.getBasePrice().toDouble()) {
							// Se esta subiendo precio, permitirlo
							bmoQuoteEquipment.getPrice().setValue(p);
							saveEquipmentChange();
							reset();

						} else if (p < bmoQuoteEquipment.getBasePrice().toDouble()) {
							// El precio es menor al precio base, validar permisos
							if (getSFParams().hasSpecialAccess(BmoQuote.ACCESS_CHANGEITEMPRICE)) {
								bmoQuoteEquipment.getPrice().setValue(p);
								saveEquipmentChange();
								reset();
							} else {
								numberFormat = NumberFormat.getCurrencyFormat();
								String formatted = numberFormat.format(bmoQuoteEquipment.getBasePrice().toDouble());
								showSystemMessage("No cuenta con Permisos para establecer Precio menor a: " + formatted);
								reset();
							}
						}

					} else {
						showSystemMessage("El Precio no puede ser menor a $0.00");
						reset();
					}
				} else {
					// No es de tipo producto, permite todo
					bmoQuoteEquipment.getPrice().setValue(p);
					saveEquipmentChange();
				}
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-changePrice(): ERROR " + e.toString());
			}		
		} else {
			showSystemMessage("No se puede editar la Cotización - Está Autorizada.");
			this.reset();
		}
	}

	public void changeQuoteEquipmentQuantity(BmObject bmObject, String quantity) {
		bmoQuoteEquipment = (BmoQuoteEquipment)bmObject;
		if (bmoQuote.getStatus().toChar() != BmoQuote.STATUS_AUTHORIZED) {
			try {
				int q = Integer.parseInt(quantity);
				if (q > 0) {
					if (bmoQuoteEquipment.getEquipmentId().toInteger() > 0) 
						showSystemMessage("No se puede modificar la cantidad de un Recurso Asignado.");
					else bmoQuoteEquipment.getQuantity().setValue(quantity);
					saveEquipmentChange();
				} else {
					showSystemMessage("La Cantidad no puede ser menor a 0.");
					reset();
				}
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-changeQuantity(): ERROR " + e.toString());
			}
		} else {
			showSystemMessage("La cotización no se puede modificar - está Autorizada.");
		}
	}

	// Primer intento
	public void saveEquipmentChange() {
		saveEquipmentChange(0);
	}
	public void saveEquipmentChange(int saveEquipmentChangeRpcAttempt) {
		if (saveEquipmentChangeRpcAttempt < 5) {
			setSaveEquipmentChangeRpcAttempt(saveEquipmentChangeRpcAttempt + 1);

			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					if (getSaveEquipmentChangeRpcAttempt() < 5)
						saveEquipmentChange(getSaveEquipmentChangeRpcAttempt());
					else
						showErrorMessage(this.getClass().getName() + "-saveEquipmentChange(): ERROR " + caught.toString());
				}
	
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					setSaveEquipmentChangeRpcAttempt(0);
					processQuoteEquipmentChangeSave(result);
				}
			};
	
			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().save(bmoQuoteEquipment.getPmClass(), bmoQuoteEquipment, callback);	
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-saveEquipmentChange(): ERROR " + e.toString());
			}
		}
	}

	public void deleteQuoteEquipment(BmoQuoteEquipment bmoQuoteEquipment) {
		if (bmoQuote.getStatus().toChar() == BmoQuote.STATUS_REVISION) {
			this.bmoQuoteEquipment = bmoQuoteEquipment;
			deleteQuoteEquipment();
		} else {
			showSystemMessage("La cotización no se puede modificar - está Autorizada.");
		}
	}

	public void deleteQuoteEquipment() {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-deleteEquipment(): ERROR " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				processQuoteEquipmentDelete(result);
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().delete(bmoQuoteEquipment.getPmClass(), bmoQuoteEquipment, callback);	
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-deleteEquipment(): ERROR " + e.toString());
		}
	}

	public void processQuoteEquipmentChangeSave(BmUpdateResult bmUpdateResult) {
		if (bmUpdateResult.hasErrors()) 
			showSystemMessage("Error al modificar Recurso: " + bmUpdateResult.errorsToString());
		this.reset();
	}

	public void processQuoteEquipmentDelete(BmUpdateResult result) {
		if (result.hasErrors()) showSystemMessage("processStaffDelete() ERROR: " + result.errorsToString());
		this.reset();
	}

	// Agrega un item de un producto a un grupo de la cotizacion
	private class UiQuoteEquipmentForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
		private TextBox daysTextBox = new TextBox();
		private TextBox nameTextBox = new TextBox();
		private TextArea descriptionTextArea = new TextArea();
		private TextBox quantityTextBox = new TextBox();
		private TextBox priceTextBox = new TextBox();
		private BmoQuoteEquipment bmoQuoteEquipment;
		private Button saveButton = new Button("AGREGAR");
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		private UiSuggestBox equipmentSuggestBox = new UiSuggestBox(new BmoEquipment());
		private BmoQuote bmoQuote;
		private BmoEquipment bmoEquipment = new BmoEquipment();
		private int bmoEquipmentRpcAttempt;

		public UiQuoteEquipmentForm(UiParams uiParams, Panel defaultPanel, BmoQuote bmoQuote, BmoEquipment bmoEquipment) {
			super(uiParams, defaultPanel);
			this.bmoQuoteEquipment = new BmoQuoteEquipment();
			this.bmoQuote = bmoQuote;

			try {
				bmoQuoteEquipment.getEquipmentId().setValue(bmoEquipment.getId());
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

			saveButton.setStyleName("formSaveButton");
			saveButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					prepareSave();
				}
			});
			saveButton.setVisible(false);
			if (getSFParams().hasWrite(bmoQuoteEquipment.getProgramCode())) saveButton.setVisible(true);
			buttonPanel.add(saveButton);

			//filtro para mostrar los equipos que Activos			
			ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
			BmFilter filterByEnabled = new BmFilter();
			filterByEnabled.setValueFilter(bmoQuoteEquipment.getBmoEquipment().getKind(), bmoQuoteEquipment.getBmoEquipment().getStatus(), "" + BmoEquipment.STATUS_ACTIVE);
			filterList.add(filterByEnabled);

			equipmentSuggestBox = new UiSuggestBox(new BmoEquipment());
			equipmentSuggestBox.addFilter(filterByEnabled);

			defaultPanel.add(formTable);
		}

		public void show(){

			// Por defaul la cotizacion maneja 1 dia
			try {
				bmoQuoteEquipment.getDays().setValue(1);
				bmoQuoteEquipment.getQuantity().setValue(1);
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-show() ERROR: al asignar valor 1 a los dias del item de la cotizacion: " + e.toString());
			}

			formTable.addField(1, 0, equipmentSuggestBox, bmoQuoteEquipment.getEquipmentId());
			formTable.addField(2, 0, nameTextBox, bmoQuoteEquipment.getName());
			formTable.addField(3, 0, descriptionTextArea, bmoQuoteEquipment.getDescription());
			formTable.addField(4, 0, quantityTextBox, bmoQuoteEquipment.getQuantity());
			formTable.addField(5, 0, daysTextBox, bmoQuoteEquipment.getDays());
			formTable.addField(6, 0, priceTextBox, bmoQuoteEquipment.getPrice());
			formTable.addButtonPanel(buttonPanel);

			statusEffect();
		}

		// Actualizar cantidades
		public void formSuggestionChange(UiSuggestBox uiSuggestBox) {
			if (uiSuggestBox == equipmentSuggestBox) 
				statusEffect();
		}

		private void statusEffect() {
			if (equipmentSuggestBox.getSelectedId() > 0) {
				nameTextBox.setText("");
				nameTextBox.setEnabled(false);
				if (getSFParams().hasSpecialAccess(BmoQuote.ACCESS_CHANGEITEMPRICE))
					priceTextBox.setEnabled(true);
				else  
					priceTextBox.setEnabled(false);
				quantityTextBox.setText("1");
				quantityTextBox.setEnabled(false);
				// Colocar datos del recurso, 
				// NOTA: lo hice con asincrono porque suggestBox no te manda todos los datos de la tabla, en cambio el listBox SI?
				getBmoEquipment();
			} else {
				nameTextBox.setText("");
				nameTextBox.setEnabled(true);
				priceTextBox.setText("");
				priceTextBox.setEnabled(true);
				quantityTextBox.setText("1");
				quantityTextBox.setEnabled(true);
			}
		}
		
		// Asigna datos personal
		private void setDataEquipment(BmoEquipment bmoEquipment) {
			nameTextBox.setText("" + bmoEquipment.getName().toString());
			descriptionTextArea.setText("" + bmoEquipment.getDescription().toString());
			priceTextBox.setText("" + bmoEquipment.getPrice().toString());
		}
		
		//Obtener la cantidad en los grupos
		public void getBmoEquipment() {
			getBmoEquipment(0);
		}

		//Obtener la cantidad en los grupos
		public void getBmoEquipment(int bmoEquipmentRpcAttempt) {
			if (bmoEquipmentRpcAttempt < 5) {
				setBmoEquipmentRpcAttempt(bmoEquipmentRpcAttempt + 1);

				AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getBmoEquipmentRpcAttempt() < 5)
							getBmoEquipment(getBmoEquipmentRpcAttempt());
						else
							showErrorMessage(this.getClass().getName() + "-getBmoEquipment() ERROR: " + caught.toString());
					}
	
					public void onSuccess(BmObject result) {
						stopLoading();
						setBmoEquipmentRpcAttempt(0);
						BmoEquipment bmoEquipment = ((BmoEquipment)result);
						setDataEquipment(bmoEquipment);
					}
				};
	
				try {	
					startLoading();
					getUiParams().getBmObjectServiceAsync().get(bmoEquipment .getPmClass(), equipmentSuggestBox.getSelectedId(), callback);
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getBmoEquipment() ERROR: " + e.toString());
				}
			}
		}

		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) 
				showSystemMessage("Error al modificar Recurso: " + bmUpdateResult.errorsToString());
			else {
				quoteEquipmentDialogBox.hide();
				reset();
			}
		}

		public void prepareSave() {
			try {
				bmoQuoteEquipment = new BmoQuoteEquipment();
				bmoQuoteEquipment.getEquipmentId().setValue(equipmentSuggestBox.getSelectedId());
				bmoQuoteEquipment.getName().setValue(nameTextBox.getText());
				bmoQuoteEquipment.getDescription().setValue(descriptionTextArea.getText());
				bmoQuoteEquipment.getQuantity().setValue(quantityTextBox.getText());
				bmoQuoteEquipment.getDays().setValue(daysTextBox.getText());
				bmoQuoteEquipment.getPrice().setValue(priceTextBox.getText());
				bmoQuoteEquipment.getQuoteId().setValue(bmoQuote.getId());
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
					getUiParams().getBmObjectServiceAsync().save(bmoQuoteEquipment.getPmClass(), bmoQuoteEquipment, callback);					
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-save() ERROR: " + e.toString());
			}
		}
		
		// Variables para llamadas RPC
		public int getBmoEquipmentRpcAttempt() {
			return bmoEquipmentRpcAttempt;
		}

		public void setBmoEquipmentRpcAttempt(int bmoEquipmentRpcAttempt) {
			this.bmoEquipmentRpcAttempt = bmoEquipmentRpcAttempt;
		}
	}

	// Variables para llamadas RPC
	public int getSaveEquipmentChangeRpcAttempt() {
		return saveEquipmentChangeRpcAttempt;
	}

	public void setSaveEquipmentChangeRpcAttempt(int saveEquipmentChangeRpcAttempt) {
		this.saveEquipmentChangeRpcAttempt = saveEquipmentChangeRpcAttempt;
	}
}
