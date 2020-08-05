package com.flexwm.client.cm;


import java.util.Iterator;

import com.flexwm.client.cm.UiQuoteForm.QuoteUpdater;
import com.flexwm.shared.cm.BmoQuote;
import com.flexwm.shared.cm.BmoQuoteStaff;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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
import com.symgae.shared.sf.BmoProfile;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiFormFlexTable;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiListDataProvider;
import com.symgae.client.ui.UiParams;


public class UiQuoteStaffGrid extends Ui {
	private UiFormFlexTable formFlexTable = new UiFormFlexTable(getUiParams());

	// QuoteStaff
	private BmoQuoteStaff bmoQuoteStaff = new BmoQuoteStaff();
	private FlowPanel quoteStaffPanel = new FlowPanel();
	private CellTable<BmObject> quoteStaffGrid = new CellTable<BmObject>();
	private UiListDataProvider<BmObject> quoteStaffData;
	BmFilter quoteStaffFilter;
	private TextArea quoteStaffDescriptionTextArea = new TextArea();
	DialogBox quoteStaffDescriptionDialogBox;
	Button changeStaffDescriptionButton = new Button("GUARDAR");
	Button closeStaffDescriptionButton = new Button("CERRAR");

	private Button addQuoteStaffButton = new Button("+PERSONAL");
	protected DialogBox quoteStaffDialogBox;	

	// Cambio de Staff
	UiListBox changeStaffListBox;
	DialogBox changeStaffDialogBox;
	Button changeStaffButton = new Button("CAMBIAR");
	Button closeStaffButton = new Button("CERRAR");

	// Arbol
	public final MultiSelectionModel<BmObject> selectionQuoteStaffModel = new MultiSelectionModel<BmObject>();
	private UiQuoteStaffTreeModel uiQuoteStaffTreeModel = new UiQuoteStaffTreeModel(selectionQuoteStaffModel);
	private Button treeShowButton = new Button("NAVEGADOR");
	private Button treeCloseButton = new Button("CERRAR");
	CellTree cellStaffTree;
	Label treeStaffLabel = new Label("Agregar Personal: ");

	// Otros
	private HorizontalPanel buttonPanel = new HorizontalPanel();
	private CheckBox showQuantityCheckBox = new CheckBox();
	private CheckBox showPriceCheckBox = new CheckBox();
	private NumberFormat numberFormat = NumberFormat.getDecimalFormat();
	private BmoQuote bmoQuote;
	private QuoteUpdater quoteUpdater;
	private int saveStaffChangeRpcAttempt = 0;

	public UiQuoteStaffGrid(UiParams uiParams, Panel defaultPanel, BmoQuote bmoQuote, QuoteUpdater quoteUpdater){
		super(uiParams, defaultPanel);
		this.bmoQuote = bmoQuote;
		this.quoteUpdater = quoteUpdater;

		// Arbol de seleccion
		selectionQuoteStaffModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				Iterator<BmObject> i = selectionQuoteStaffModel.getSelectedSet().iterator();
				BmObject bmObject;
				if (i.hasNext()) {
					bmObject = (BmObject)i.next();

					// Se esta agregando un producto directo
					if (bmObject instanceof BmoProfile) {
						addStaff((BmoProfile)bmObject);
					}
				}
			}
		});
		cellStaffTree = new CellTree(uiQuoteStaffTreeModel, selectionQuoteStaffModel);
		cellStaffTree.setAnimationEnabled(true);
		cellStaffTree.addStyleName("quoteProductTree");
		treeStaffLabel.setStyleName("detailLabelTitle");

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

		closeStaffButton.setStyleName("formCloseButton");
		closeStaffButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				changeStaffDialogBox.hide();
			}
		});

		// Boton de cerrar dialogo de personal
		changeStaffDescriptionButton.setStyleName("formCloseButton");
		changeStaffDescriptionButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				changeQuoteStaffDescription();
				quoteStaffDescriptionDialogBox.hide();
			}
		});

		closeStaffDescriptionButton.setStyleName("formCloseButton");
		closeStaffDescriptionButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				quoteStaffDescriptionDialogBox.hide();
			}
		});

		// Elementos del cambio de staff
		changeStaffListBox = new UiListBox(getUiParams(), new BmoProfile());

		// Inicializar grid de Personal
		quoteStaffGrid = new CellTable<BmObject>();
		quoteStaffGrid.setWidth("100%");
		quoteStaffPanel.clear();
		quoteStaffPanel.setWidth("100%");
		setQuoteStaffColumns();
		quoteStaffFilter = new BmFilter();
		quoteStaffFilter.setValueFilter(bmoQuoteStaff.getKind(), bmoQuoteStaff.getQuoteId().getName(), bmoQuote.getId());
		quoteStaffGrid.addLoadingStateChangeHandler(new LoadingStateChangeEvent.Handler() {           
			@Override
			public void onLoadingStateChanged(LoadingStateChangeEvent event) {
				if (event.getLoadingState() == LoadingState.LOADED) {
					changeHeight();
				}
			}
		}); 
		quoteStaffData = new UiListDataProvider<BmObject>(new BmoQuoteStaff(), quoteStaffFilter);
		quoteStaffData.addDataDisplay(quoteStaffGrid);
		quoteStaffPanel.add(quoteStaffGrid);

		// Panel de botones
		addQuoteStaffButton.setStyleName("formSaveButton");
		addQuoteStaffButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addStaff();
			}
		});
		buttonPanel.setHeight("100%");
		buttonPanel.setStyleName("formButtonPanel");
		buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		buttonPanel.add(addQuoteStaffButton);
		buttonPanel.add(treeShowButton);

		// Crear forma y campos
		formFlexTable.setWidth("100%");
		formFlexTable.addPanel(3, 0, quoteStaffPanel, 4);
		formFlexTable.addButtonPanel(buttonPanel);
		defaultPanel.add(formFlexTable);
	}

	public void show(){
		quoteStaffData.list();
		quoteStaffGrid.redraw();

		statusEffect();
	}

	public void statusEffect(){
		if (bmoQuote.getStatus().toChar() == BmoQuote.STATUS_AUTHORIZED || bmoQuote.getStatus().toChar() == BmoQuote.STATUS_CANCELLED) {
			showQuantityCheckBox.setEnabled(false);
			showPriceCheckBox.setEnabled(false);
			addQuoteStaffButton.setVisible(false);
			treeShowButton.setVisible(false);
			changeStaffDescriptionButton.setEnabled(false);
		} else {
			showQuantityCheckBox.setEnabled(true);
			showPriceCheckBox.setEnabled(true);	
			addQuoteStaffButton.setVisible(true);
			treeShowButton.setVisible(true);
		}
	}

	private void showTree() {
		getUiParams().getUiTemplate().showEastPanel();
		getUiParams().getUiTemplate().getEastPanel().add(new HTML("<pre> </pre>"));
		getUiParams().getUiTemplate().getEastPanel().add(new HTML("<pre> </pre>"));
		getUiParams().getUiTemplate().getEastPanel().add(treeStaffLabel);
		getUiParams().getUiTemplate().getEastPanel().add(cellStaffTree);
		getUiParams().getUiTemplate().getEastPanel().add(treeCloseButton);
	}

	private void hideTree() {
		getUiParams().getUiTemplate().hideEastPanel();
	}

	public void reset(){
		quoteUpdater.changeQuoteStaff();
	}

	public void changeHeight() {
		quoteStaffGrid.setVisibleRange(0, quoteStaffData.getList().size());
	}

	public void addStaff(){
		addStaff(new BmoProfile());
	}

	public void addStaff(BmoProfile bmoProfile) {
		quoteStaffDialogBox = new DialogBox(true);
		quoteStaffDialogBox.setGlassEnabled(true);
		quoteStaffDialogBox.setText("Personal de Cotización");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "200px");

		quoteStaffDialogBox.setWidget(vp);

		UiQuoteStaffForm quoteStaffForm = new UiQuoteStaffForm(getUiParams(), vp, bmoQuote, bmoProfile);

		quoteStaffForm.show();

		quoteStaffDialogBox.center();
		quoteStaffDialogBox.show();
	}

	// Columnas
	public void setQuoteStaffColumns() {
		// Cantidad
		Column<BmObject, String> quantityColumn;
		if (bmoQuote.getStatus().equals(BmoQuote.STATUS_REVISION)) {
			quantityColumn = new Column<BmObject, String>(new EditTextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoQuoteStaff)bmObject).getQuantity().toString();
				}
			};
			quantityColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					changeQuoteStaffQuantity(bmObject, value);
				}
			});
		} else {
			quantityColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoQuoteStaff)bmObject).getQuantity().toString();
				}
			};
		}
		quoteStaffGrid.addColumn(quantityColumn, SafeHtmlUtils.fromSafeConstant("Cant."));
		quoteStaffGrid.setColumnWidth(quantityColumn, 50, Unit.PX);
		quantityColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		// Nombre
		Column<BmObject, String> nameColumn;
		nameColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoQuoteStaff)bmObject).getName().toString();
			}
		};
		quoteStaffGrid.addColumn(nameColumn, SafeHtmlUtils.fromSafeConstant("Nombre"));
		quoteStaffGrid.setColumnWidth(nameColumn, 100, Unit.PX);
		nameColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

		if (!isMobile()) {
			// Columna descripcion
			Column<BmObject, String> descriptionColumn = new Column<BmObject, String>(new ButtonCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					if (((BmoQuoteStaff)bmObject).getDescription().toString().length() > 0)
						return "...";
					else return ".";
				}
			};
			descriptionColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					quoteStaffDescriptionDialog((BmoQuoteStaff)bmObject);
				}
			});
			quoteStaffGrid.addColumn(descriptionColumn, SafeHtmlUtils.fromSafeConstant("Desc."));
			quoteStaffGrid.setColumnWidth(descriptionColumn, 50, Unit.PX);
			descriptionColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

			// Grupo
			Column<BmObject, String> profileColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoQuoteStaff)bmObject).getBmoProfile().getName().toString();
				}
			};
			profileColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			quoteStaffGrid.addColumn(profileColumn, SafeHtmlUtils.fromSafeConstant("Perfil"));
			quoteStaffGrid.setColumnWidth(profileColumn, 100, Unit.PX);

			// Días
			Column<BmObject, String> daysColumn;
			if (bmoQuote.getStatus().equals(BmoQuote.STATUS_REVISION)) {
				daysColumn = new Column<BmObject, String>(new EditTextCell()) {
					@Override
					public String getValue(BmObject bmObject) {
						return ((BmoQuoteStaff)bmObject).getDays().toString();
					}
				};
				daysColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
					@Override
					public void update(int index, BmObject bmObject, String value) {
						changeQuoteStaffDays(bmObject, value);
					}
				});
			} else {
				daysColumn = new Column<BmObject, String>(new TextCell()) {
					@Override
					public String getValue(BmObject bmObject) {
						return ((BmoQuoteStaff)bmObject).getDays().toString();
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
			quoteStaffGrid.addColumn(daysColumn, daysHeader);
			quoteStaffGrid.setColumnWidth(daysColumn, 50, Unit.PX);
			quantityColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		}

		// Precio
		Column<BmObject, String> priceColumn;
		if (bmoQuote.getStatus().equals(BmoQuote.STATUS_REVISION)) {
			priceColumn = new Column<BmObject, String>(new EditTextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoQuoteStaff)bmObject).getPrice().toString();
				}
			};
			priceColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					changeQuoteStaffPrice(bmObject, value);
				}
			});
		} else {
			priceColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoQuoteStaff)bmObject).getPrice().toString();
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
		quoteStaffGrid.addColumn(priceColumn, priceHeader);
		quoteStaffGrid.setColumnWidth(priceColumn, 50, Unit.PX);
		priceColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

		// Columna de Total
		Column<BmObject, String> totalColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				numberFormat = NumberFormat.getCurrencyFormat();
				String formatted = numberFormat.format(((BmoQuoteStaff)bmObject).getAmount().toDouble());
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
		quoteStaffGrid.addColumn(totalColumn, totalHeader);
		quoteStaffGrid.setColumnWidth(totalColumn, 50, Unit.PX);
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
					deleteQuoteStaff((BmoQuoteStaff)bmObject);
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
		quoteStaffGrid.addColumn(deleteColumn, SafeHtmlUtils.fromSafeConstant("Eliminar"));
		deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		quoteStaffGrid.setColumnWidth(deleteColumn, 50, Unit.PX);	
	}

	public void changeStaffDialog(BmoQuoteStaff bmoQuoteStaff) {
		this.bmoQuoteStaff = bmoQuoteStaff;

		if (bmoQuoteStaff.getProfileId().toInteger() > 0) {
			changeStaffDialogBox = new DialogBox(true);
			changeStaffDialogBox.setGlassEnabled(true);
			changeStaffDialogBox.setText("Cambio de Personal");
			changeStaffDialogBox.setSize("400px", "100px");

			VerticalPanel vp = new VerticalPanel();
			vp.setSize("400px", "100px");
			changeStaffDialogBox.setWidget(vp);

			UiFormFlexTable formChangeStaffTable = new UiFormFlexTable(getUiParams());
			BmoQuoteStaff bmoChangeQuoteStaff = new BmoQuoteStaff();
			formChangeStaffTable.addField(1, 0, changeStaffListBox, bmoChangeQuoteStaff.getProfileId());

			HorizontalPanel changeStaffButtonPanel = new HorizontalPanel();
			changeStaffButtonPanel.add(changeStaffButton);
			changeStaffButtonPanel.add(closeStaffButton);

			vp.add(formChangeStaffTable);
			vp.add(changeStaffButtonPanel);

			changeStaffDialogBox.center();
			changeStaffDialogBox.show();
		}
	}

	public void quoteStaffDescriptionDialog(BmoQuoteStaff bmoQuoteStaff) {
		this.bmoQuoteStaff = bmoQuoteStaff;
		quoteStaffDescriptionDialogBox = new DialogBox(true);
		quoteStaffDescriptionDialogBox.setGlassEnabled(true);
		quoteStaffDescriptionDialogBox.setText("Descripción Personal");
		quoteStaffDescriptionDialogBox.setSize("400px", "200px");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "200px");
		quoteStaffDescriptionDialogBox.setWidget(vp);

		quoteStaffDescriptionTextArea = new TextArea();
		quoteStaffDescriptionTextArea.setSize("320px", "190px");
		quoteStaffDescriptionTextArea.setText(bmoQuoteStaff.getDescription().toString());
		vp.add(quoteStaffDescriptionTextArea);

		HorizontalPanel descriptionButtonPanel = new HorizontalPanel();
		descriptionButtonPanel.add(changeStaffDescriptionButton);
		descriptionButtonPanel.add(closeStaffDescriptionButton);
		vp.add(descriptionButtonPanel);

		quoteStaffDescriptionDialogBox.center();
		quoteStaffDescriptionDialogBox.show();
	}

	public void changeQuoteStaffDescription() {
		String description = quoteStaffDescriptionTextArea.getText();

		try {
			if (description.length() > 0) {
				bmoQuoteStaff.getDescription().setValue(description);
				saveStaffChange();
			}
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-changeQuoteStaffDescription() ERROR: " + e.toString());
		}
	}

	public void changeQuoteStaffQuantity(BmObject bmObject, String quantity) {
		bmoQuoteStaff = (BmoQuoteStaff)bmObject;
		if (bmoQuote.getStatus().toChar() != BmoQuote.STATUS_AUTHORIZED) {
			try {
				int q = Integer.parseInt(quantity);
				if (q > 0) {
					bmoQuoteStaff.getQuantity().setValue(quantity);
					saveStaffChange();
				} else {
					showSystemMessage("La Cantidad no puede ser menor a 0.");
					reset();
				}
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-changeQuantity(): ERROR " + e.toString());
			}
		} else {
			showSystemMessage("La cotización no se puede modificar - está Autorizado.");
		}
	}

	public void changeQuoteStaffDays(BmObject bmObject, String days) {
		bmoQuoteStaff = (BmoQuoteStaff)bmObject;
		if (bmoQuote.getStatus().toChar() != BmoQuote.STATUS_AUTHORIZED) {
			try {
				double d = Double.parseDouble(days);
				if (d > 0) {
					bmoQuoteStaff.getDays().setValue(d);
					saveStaffChange();
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

	public void changeQuoteStaffPrice(BmObject bmObject, String price) {
		if (bmoQuote.getStatus().toChar() == BmoQuote.STATUS_REVISION) {
			bmoQuoteStaff = (BmoQuoteStaff)bmObject;
			try {
				Double p = Double.parseDouble(price);

				if (bmoQuoteStaff.getProfileId().toInteger() > 0) {
					// Es de tipo Producto
					if (p == 0) {
						bmoQuoteStaff.getPrice().setValue(0);
						saveStaffChange();
					} else if (p > 0) {

						if (p >= bmoQuoteStaff.getBasePrice().toDouble()) {
							// Se esta subiendo precio, permitirlo
							bmoQuoteStaff.getPrice().setValue(p);
							saveStaffChange();
							reset();

						} else if (p < bmoQuoteStaff.getBasePrice().toDouble()) {
							// El precio es menor al precio base, validar permisos
							if (getSFParams().hasSpecialAccess(BmoQuote.ACCESS_CHANGEITEMPRICE)) {
								bmoQuoteStaff.getPrice().setValue(p);
								saveStaffChange();
								reset();
							} else {
								numberFormat = NumberFormat.getCurrencyFormat();
								String formatted = numberFormat.format(bmoQuoteStaff.getBasePrice().toDouble());
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
					bmoQuoteStaff.getPrice().setValue(p);
					saveStaffChange();
				}
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-changePrice(): ERROR " + e.toString());
			}		
		} else {
			showSystemMessage("No se puede editar la Cotización - Está Autorizada.");
			this.reset();
		}
	}
	// Primer intento
	public void saveStaffChange() {
		saveStaffChange(0);
	}
	public void saveStaffChange(int saveStaffChangeRpcAttempt) {
		if (saveStaffChangeRpcAttempt < 5) {
			setSaveStaffChangeRpcAttempt(saveStaffChangeRpcAttempt + 1);

			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					if (getSaveStaffChangeRpcAttempt() < 5)
						saveStaffChange(getSaveStaffChangeRpcAttempt());
					else 
						showErrorMessage(this.getClass().getName() + "-saveStaffChange(): ERROR " + caught.toString());
				}
	
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					setSaveStaffChangeRpcAttempt(0);
					processQuoteStaffChangeSave(result);
				}
			};
	
			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().save(bmoQuoteStaff.getPmClass(), bmoQuoteStaff, callback);	
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-saveStaffChange(): ERROR " + e.toString());
			}
		}
	}

	public void deleteQuoteStaff(BmoQuoteStaff bmoQuoteStaff) {
		if (bmoQuote.getStatus().toChar() == BmoQuote.STATUS_REVISION) {
			this.bmoQuoteStaff = bmoQuoteStaff;
			deleteQuoteStaff();
		} else {
			showSystemMessage("La cotización no se puede modificar - está Autorizado");
		}
	}

	public void deleteQuoteStaff() {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-deleteStaff(): ERROR " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				processQuoteStaffDelete(result);
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().delete(bmoQuoteStaff.getPmClass(), bmoQuoteStaff, callback);	
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-deleteStaff(): ERROR " + e.toString());
		}
	}

	public void processQuoteStaffChangeSave(BmUpdateResult bmUpdateResult) {
		if (bmUpdateResult.hasErrors()) 
			showSystemMessage("Error al modificar Personal: " + bmUpdateResult.errorsToString());
		this.reset();
	}

	public void processQuoteStaffDelete(BmUpdateResult result) {
		if (result.hasErrors()) showSystemMessage("processStaffDelete() ERROR: " + result.errorsToString());
		this.reset();
	}	

	// Agrega un item de un producto a un grupo de la cotizacion
	private class UiQuoteStaffForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
		private TextBox nameTextBox = new TextBox();
		private TextArea descriptionTextArea = new TextArea();
		private TextBox daysTextBox = new TextBox();
		private TextBox quantityTextBox = new TextBox();
		private TextBox priceTextBox = new TextBox();
		private UiListBox profileListBox = new UiListBox(getUiParams(), new BmoProfile());

		private BmoQuoteStaff bmoQuoteStaff;
		private Button saveButton = new Button("AGREGAR");
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		private BmoQuote bmoQuote;
		private BmoProfile bmoProfile = new BmoProfile();
		private Label quoteStaffQuantity = new Label();
		String profileId = "";
		private int quoteStaffQuantityRpcAttempt = 0;

		public UiQuoteStaffForm(UiParams uiParams, Panel defaultPanel, BmoQuote bmoQuote, BmoProfile bmoProfile) {
			super(uiParams, defaultPanel);
			this.bmoQuoteStaff = new BmoQuoteStaff();
			this.bmoQuote = bmoQuote;
			this.bmoProfile = bmoProfile;

			try {
				bmoQuoteStaff.getProfileId().setValue(bmoProfile.getId());
				profileId = String.valueOf(bmoProfile.getId());
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
			if (getSFParams().hasWrite(bmoQuoteStaff.getProgramCode())) saveButton.setVisible(true);
			buttonPanel.add(saveButton);

			// Mostrar grupos que estan en tipos de pedidos
//			BmoOrderTypeGroup bmoOrderTypeGroup = new BmoOrderTypeGroup();
//			BmFilter filterGroups = new BmFilter();
//			filterGroups.setInFilter(bmoOrderTypeGroup.getKind(), 
//					bmoProfile.getIdFieldName(),
//					bmoOrderTypeGroup.getProfileId().getName(),
//					bmoOrderTypeGroup.getOrderTypeId().getName(),
//					"" + bmoQuote.getOrderTypeId().toInteger()
//					);
//			profileListBox.addFilter(filterGroups);

			defaultPanel.add(formTable);
		}

		public void show(){
			// Por defaul la cotizacion maneja 1 dia
			try {
				bmoQuoteStaff.getDays().setValue(1);
				bmoQuoteStaff.getQuantity().setValue(1);
				bmoProfile.getIdField().setNullable(true);
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-show() ERROR: al asignar valor 1 a los dias del item de la cotizacion: " + e.toString());
			}

			formTable.addField(1, 0, profileListBox, bmoProfile.getIdField());
			formTable.addLabelField(2, 0, "En Cotización", quoteStaffQuantity);
			formTable.addField(4, 0, nameTextBox, bmoQuoteStaff.getName());
			formTable.addField(5, 0, descriptionTextArea, bmoQuoteStaff.getDescription());
			formTable.addField(6, 0, quantityTextBox, bmoQuoteStaff.getQuantity());
			formTable.addField(7, 0, daysTextBox, bmoQuoteStaff.getDays());
			formTable.addField(8, 0, priceTextBox, bmoQuoteStaff.getPrice());
			formTable.addButtonPanel(buttonPanel);

			statusEffect();
		}

		public void formListChange(ChangeEvent event) {
			if (event.getSource() == profileListBox) {
				profileId = profileListBox.getSelectedId();
				getQuoteStaffQuantity();
			} 
			statusEffect();
		}

		private void statusEffect() {
			if ((!profileListBox.getSelectedId().equals("") && !profileListBox.getSelectedId().equals("0"))) {
				getQuoteStaffQuantity();
				nameTextBox.setText("");
				nameTextBox.setEnabled(false);

				if (getSFParams().hasSpecialAccess(BmoQuote.ACCESS_CHANGEITEMPRICE))
					priceTextBox.setEnabled(true);
				else  
					priceTextBox.setEnabled(false);
			} else {
				nameTextBox.setText("");
				nameTextBox.setEnabled(true);
				priceTextBox.setText("");
				priceTextBox.setEnabled(true);
			}
		}

		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) 
				showSystemMessage("Error al modificar Personal: " + bmUpdateResult.errorsToString());
			else {
				quoteStaffDialogBox.hide();
				reset();
			}
		}

		public void prepareSave() {
			try {
				bmoQuoteStaff = new BmoQuoteStaff();
				bmoQuoteStaff.getProfileId().setValue(profileListBox.getSelectedId());
				bmoQuoteStaff.getName().setValue(nameTextBox.getText());
				bmoQuoteStaff.getDescription().setValue(descriptionTextArea.getText());
				bmoQuoteStaff.getDays().setValue(daysTextBox.getText());
				bmoQuoteStaff.getPrice().setValue(priceTextBox.getText());
				bmoQuoteStaff.getQuantity().setValue(quantityTextBox.getText());
				bmoQuoteStaff.getQuoteId().setValue(bmoQuote.getId());
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
					getUiParams().getBmObjectServiceAsync().save(bmoQuoteStaff.getPmClass(), bmoQuoteStaff, callback);					
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-save() ERROR: " + e.toString());
			}
		}

		//Obtener la cantidad en los grupos
		public void getQuoteStaffQuantity() {
			getQuoteStaffQuantity(0);
		}

		//Obtener la cantidad en los grupos
		public void getQuoteStaffQuantity(int quoteStaffQuantityRpcAttempt) {
			if (quoteStaffQuantityRpcAttempt < 5) {
				setQuoteStaffQuantityRpcAttempt(quoteStaffQuantityRpcAttempt + 1);

				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getQuoteStaffQuantityRpcAttempt() < 5)
							getQuoteStaffQuantity(getQuoteStaffQuantityRpcAttempt());
						else
							showErrorMessage(this.getClass().getName() + "-getQuoteStaffQuantity() ERROR: " + caught.toString());
					}
	
					public void onSuccess(BmUpdateResult result) {
						stopLoading();
						setQuoteStaffQuantityRpcAttempt(0);
						quoteStaffQuantity.setText(result.getMsg());
						setDataGroup();
					}
				};
	
				try {	
					quoteStaffQuantity.setText("");
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoQuote.getPmClass(), bmoQuote, BmoQuote.ACTION_QUOTESTAFFQUANTITY, profileId, callback);
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getQuoteStaffQuantity() ERROR: " + e.toString());
				}
			}
		} 

		// Asigna datos personal
		private void setDataGroup() {
			nameTextBox.setText("" + ((BmoProfile)profileListBox.getSelectedBmObject()).getName().toString());
			descriptionTextArea.setText("" + ((BmoProfile)profileListBox.getSelectedBmObject()).getDescription().toString());
			priceTextBox.setText("" + ((BmoProfile)profileListBox.getSelectedBmObject()).getPrice().toString());
		}

		// Variables para llamadas RPC
		public int getQuoteStaffQuantityRpcAttempt() {
			return quoteStaffQuantityRpcAttempt;
		}

		public void setQuoteStaffQuantityRpcAttempt(int quoteStaffQuantityRpcAttempt) {
			this.quoteStaffQuantityRpcAttempt = quoteStaffQuantityRpcAttempt;
		}
	}

	// Variables para llamadas RPC
	public int getSaveStaffChangeRpcAttempt() {
		return saveStaffChangeRpcAttempt;
	}

	public void setSaveStaffChangeRpcAttempt(int saveStaffChangeRpcAttempt) {
		this.saveStaffChangeRpcAttempt = saveStaffChangeRpcAttempt;
	}
}
