/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.wf;

import java.util.ArrayList;
import java.util.Date;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.symgae.client.ui.UiBmFieldClickHandler;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.sf.BmoUser;
import com.flexwm.shared.ac.BmoSessionSale;
import com.flexwm.shared.ar.BmoPropertyRental;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.cm.BmoProject;
import com.flexwm.shared.co.BmoDevelopmentPhase;
import com.flexwm.shared.co.BmoPropertySale;
import com.flexwm.shared.cr.BmoCredit;
import com.flexwm.shared.cv.BmoActivity;
import com.flexwm.shared.cv.BmoMeeting;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoRequisition;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowLog;


public class UiWFlowLog extends UiList {

	BmoWFlowLog bmoWFlowLog;
	BmoWFlow bmoWFlow = new BmoWFlow();
	UiSuggestBox wFlowUiSuggestBox = new UiSuggestBox(new BmoWFlow());
	int wFlowId;
	int customerId;

	public UiWFlowLog(UiParams uiParams, BmoWFlow bmoWFlow) {
		super(uiParams, new BmoWFlowLog());
		bmoWFlowLog = (BmoWFlowLog)getBmObject();
		this.bmoWFlow = bmoWFlow;
	}

	public UiWFlowLog(UiParams uiParams, Panel defaultPanel) {
		super(uiParams, defaultPanel, new BmoWFlowLog());
		this.bmoWFlowLog = (BmoWFlowLog)getBmObject();
	}

	public UiWFlowLog(UiParams uiParams, Panel defaultPanel, int wFlowId, int customerId) {
		super(uiParams, defaultPanel, new BmoWFlowLog());
		this.bmoWFlowLog = (BmoWFlowLog)getBmObject();
		this.customerId = customerId;
		this.wFlowId = wFlowId;
	}
	
	public UiWFlowLog(UiParams uiParams, int wFlowId) {
		super(uiParams, new BmoWFlowLog());
		this.bmoWFlowLog = (BmoWFlowLog)getBmObject();
		this.wFlowId = wFlowId;
	}

	public UiWFlowLog(UiParams uiParams) {
		super(uiParams, new BmoWFlowLog());
		bmoWFlowLog = (BmoWFlowLog)getBmObject();
	}

	@Override
	public void postShow() {

		// Muestra filtros si es modulo independiente
		if (getUiType() == UiParams.MASTER) {
			// Se resetean filtros forzados para tener solo parametros de la lista del modulo
			getUiParams().getUiProgramParams(bmoWFlowLog.getProgramCode()).clearFilters();

			// Lista de Flujos a descartar por permisos de lectura
			BmoOpportunity bmoOpportunity = new BmoOpportunity();
			BmoOrder bmoOrder = new BmoOrder();
			BmoMeeting bmoMeeting = new BmoMeeting();
			BmoProject bmoProject = new BmoProject();
			BmoPropertySale bmoPropertySale = new BmoPropertySale();
			BmoDevelopmentPhase bmoDevelopmentPhase = new BmoDevelopmentPhase();
			BmoSessionSale bmoSessionSale = new BmoSessionSale();
			BmoCredit bmoCredit = new BmoCredit();
			BmoActivity bmoActivity = new BmoActivity();
			BmoPropertyRental bmoPropertyRental = new BmoPropertyRental();
			BmoCustomer bmoCustomer = new BmoCustomer();
			BmoRequisition bmoRequisition = new BmoRequisition();

			ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();

			// Descartar si NO tiene permiso de ver Módulo Oportunidades
			if (!getSFParams().hasRead(bmoOpportunity.getProgramCode())) {
				BmFilter filterWFlowCallerCode = new BmFilter();
				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
						bmoWFlow.getCallerCode(), 
						BmFilter.NOTEQUALS, 
						bmoOpportunity.getProgramCode());
				filterList.add(filterWFlowCallerCode);
				wFlowUiSuggestBox.addFilter(filterWFlowCallerCode);
			}

			// Descartar si NO tiene permiso de ver Módulo Pedidos
			if (!getSFParams().hasRead(bmoOrder.getProgramCode())) {
				BmFilter filterWFlowCallerCode = new BmFilter();
				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
						bmoWFlow.getCallerCode(), 
						BmFilter.NOTEQUALS, 
						bmoOrder.getProgramCode());
				filterList.add(filterWFlowCallerCode);
				wFlowUiSuggestBox.addFilter(filterWFlowCallerCode);
			}

			// Descartar si NO tiene permiso de ver Módulo Control de Minutas
			if (!getSFParams().hasRead(bmoMeeting.getProgramCode())) {
				BmFilter filterWFlowCallerCode = new BmFilter();
				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
						bmoWFlow.getCallerCode(), 
						BmFilter.NOTEQUALS, 
						bmoMeeting.getProgramCode());
				filterList.add(filterWFlowCallerCode);
				wFlowUiSuggestBox.addFilter(filterWFlowCallerCode);
			}

			// Descartar si NO tiene permiso de ver Módulo Proyectos
			if (!getSFParams().hasRead(bmoProject.getProgramCode())) {
				BmFilter filterWFlowCallerCode = new BmFilter();
				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
						bmoWFlow.getCallerCode(), 
						BmFilter.NOTEQUALS, 
						bmoProject.getProgramCode());
				filterList.add(filterWFlowCallerCode);
				wFlowUiSuggestBox.addFilter(filterWFlowCallerCode);
			}

			// Descartar si NO tiene permiso de ver Módulo Venta de Inmuebles
			if (!getSFParams().hasRead(bmoPropertySale.getProgramCode())) {
				BmFilter filterWFlowCallerCode = new BmFilter();
				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
						bmoWFlow.getCallerCode(), 
						BmFilter.NOTEQUALS, 
						bmoPropertySale.getProgramCode());
				filterList.add(filterWFlowCallerCode);
				wFlowUiSuggestBox.addFilter(filterWFlowCallerCode);
			}

			// Descartar si NO tiene permiso de ver Módulo Etapas de Desarrollo
			if (!getSFParams().hasRead(bmoDevelopmentPhase.getProgramCode())) {
				BmFilter filterWFlowCallerCode = new BmFilter();
				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
						bmoWFlow.getCallerCode(), 
						BmFilter.NOTEQUALS, 
						bmoDevelopmentPhase.getProgramCode());
				filterList.add(filterWFlowCallerCode);
				wFlowUiSuggestBox.addFilter(filterWFlowCallerCode);
			}

			// Descartar si NO tiene permiso de ver Módulo Venta de Sesiones
			if (!getSFParams().hasRead(bmoSessionSale.getProgramCode())) {
				BmFilter filterWFlowCallerCode = new BmFilter();
				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
						bmoWFlow.getCallerCode(), 
						BmFilter.NOTEQUALS, 
						bmoSessionSale.getProgramCode());
				filterList.add(filterWFlowCallerCode);
				wFlowUiSuggestBox.addFilter(filterWFlowCallerCode);
			}
			
			// Descartar si NO tiene permiso de ver Módulo Créditos
			if (!getSFParams().hasRead(bmoCredit.getProgramCode())) {
				BmFilter filterWFlowCallerCode = new BmFilter();
				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
						bmoWFlow.getCallerCode(), 
						BmFilter.NOTEQUALS, 
						bmoCredit.getProgramCode());
				filterList.add(filterWFlowCallerCode);
				wFlowUiSuggestBox.addFilter(filterWFlowCallerCode);
			}
			
			// Descartar si NO tiene permiso de ver Módulo Actividades
			if (!getSFParams().hasRead(bmoActivity.getProgramCode())) {
				BmFilter filterWFlowCallerCode = new BmFilter();
				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
						bmoWFlow.getCallerCode(), 
						BmFilter.NOTEQUALS, 
						bmoActivity.getProgramCode());
				filterList.add(filterWFlowCallerCode);
				wFlowUiSuggestBox.addFilter(filterWFlowCallerCode);
			}
			
			// Descartar si NO tiene permiso de ver Módulo Arrendamiento
			if (!getSFParams().hasRead(bmoPropertyRental.getProgramCode())) {
				BmFilter filterWFlowCallerCode = new BmFilter();
				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
						bmoWFlow.getCallerCode(), 
						BmFilter.NOTEQUALS, 
						bmoPropertyRental.getProgramCode());
				filterList.add(filterWFlowCallerCode);
				wFlowUiSuggestBox.addFilter(filterWFlowCallerCode);
			}
			
			// Descartar si NO tiene permiso de ver Módulo Clientes
			if (!getSFParams().hasRead(bmoCustomer.getProgramCode())) {
				BmFilter filterWFlowCallerCode = new BmFilter();
				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
						bmoWFlow.getCallerCode(), 
						BmFilter.NOTEQUALS, 
						bmoCustomer.getProgramCode());
				filterList.add(filterWFlowCallerCode);
				wFlowUiSuggestBox.addFilter(filterWFlowCallerCode);
			}
			
			// Descartar si NO tiene permiso de ver Módulo OC
			if (!getSFParams().hasRead(bmoCustomer.getProgramCode())) {
				BmFilter filterWFlowCallerCode = new BmFilter();
				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
						bmoWFlow.getCallerCode(), 
						BmFilter.NOTEQUALS, 
						bmoRequisition.getProgramCode());
				filterList.add(filterWFlowCallerCode);
				wFlowUiSuggestBox.addFilter(filterWFlowCallerCode);
			}

			addFilterSuggestBox(wFlowUiSuggestBox, new BmoWFlow(), bmoWFlowLog.getWFlowId());
			if (!isMobile())
				addStaticFilterListBox(new UiListBox(getUiParams(), bmoWFlowLog.getType()), bmoWFlowLog, bmoWFlowLog.getType());
			addFilterSuggestBox(new UiSuggestBox(new BmoUser()), new BmoUser(), bmoWFlowLog.getUserId());
			addFilterSuggestBox(new UiSuggestBox(new BmoCustomer()), new BmoCustomer(), bmoWFlowLog.getBmoWFlow().getCustomerId());
			if (!isMobile())
				addDateRangeFilterListBox(bmoWFlowLog.getLogdate());

			getUiParams().getUiProgramParams(bmoWFlowLog.getProgramCode()).setFilterList(filterList);
		} else {
			if (!isMobile())  {
				if (customerId > 0 || wFlowId > 0) {
					addStaticFilterListBox(new UiListBox(getUiParams(), new BmoWFlowLog().getType()), bmoWFlowLog, bmoWFlowLog.getType());
				}
			}
		}
	}

	@Override
	public void displayList() {
		int col = 0;
		// Si es movil, hacer ajuste de columnas
		if (isMobile()) {
			listFlexTable.addListTitleCell(0, col++, bmoWFlowLog.getBmoUser().getCode().getLabel());
			listFlexTable.addListTitleCell(0, col++, bmoWFlowLog.getType().getLabel());
			listFlexTable.addListTitleCell(0, col++, bmoWFlowLog.getLogdate().getLabel());
			if (getUiType() == UiParams.MASTER || customerId > 0 && !(wFlowId > 0)) 
				listFlexTable.addListTitleCell(0, col++, bmoWFlowLog.getBmoWFlow().getCode().getLabel());
		} else {
			listFlexTable.addListTitleCell(0, col++, bmoWFlowLog.getBmoUser().getCode().getLabel());

			listFlexTable.addListTitleCell(0, col++, bmoWFlowLog.getType().getLabel());
			listFlexTable.addListTitleCell(0, col++, bmoWFlowLog.getLogdate().getLabel());
			if (getUiType() == UiParams.MASTER || customerId > 0 && !(wFlowId > 0)) {
				listFlexTable.addListTitleCell(0, col++, bmoWFlowLog.getBmoWFlow().getCode().getLabel());
				listFlexTable.addListTitleCell(0, col++, bmoWFlowLog.getBmoWFlow().getName().getLabel());
			}
			listFlexTable.addListTitleCell(0, col++, bmoWFlowLog.getComments().getLabel());
			listFlexTable.addListTitleCell(0, col++, bmoWFlowLog.getData().getLabel());
			listFlexTable.addListTitleCell(0, col++, "Ventana");
		}

		int row = 1;
		while (iterator.hasNext()) {
			col = 0;
			BmoWFlowLog bmoWFlowLog = (BmoWFlowLog)iterator.next();

			if (isMobile()) {
				Label linkLabel = new Label(bmoWFlowLog.getBmoUser().getCode().toString());
				linkLabel.addClickHandler(rowClickHandler);
				linkLabel.setStyleName("listCellLink");
				listFlexTable.setWidget(row, col++, linkLabel);
				listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowLog.getType());
				listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowLog.getLogdate());
				if (getUiType() == UiParams.MASTER || customerId > 0 && !(wFlowId > 0)) 
					listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowLog.getBmoWFlow().getCode());
			} else {
				Label linkLabel = new Label(bmoWFlowLog.getBmoUser().getCode().toString());
				linkLabel.addClickHandler(rowClickHandler);
				linkLabel.setStyleName("listCellLink");
				listFlexTable.setWidget(row, col++, linkLabel);
				listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowLog.getType());
				listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowLog.getLogdate());
				// Mostrar Clave/Nombre del flujo si NO se visualiza dentro de un Flujo
				// es decir, el módulo independiente y dentro de un cliente
				if (getUiType() == UiParams.MASTER || customerId > 0 && !(wFlowId > 0)) {
					listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowLog.getBmoWFlow().getCode());
					listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowLog.getBmoWFlow().getName());
				}
				listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowLog.getComments());

				// Visor interno
				listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowLog.getData());

				if (bmoWFlowLog.getData().toString().length() > 0) {
					// Muestra visor externo de los datos
					Image icon = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/dataview.png"));
					icon.setTitle("Abrir en Ventana Externa.");
					icon.addClickHandler(new UiBmFieldClickHandler(bmoWFlowLog.getIdField()) {
						@Override
						public void onClick(ClickEvent event) {
							Window.open(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/rpt/wf/wflg_view.jsp?" + bmField.getName() + "=" + bmField.toString()), "_blank", "");
						}
					});
					listFlexTable.setWidget(row, col++, icon);
				} else if (bmoWFlowLog.getLink().toString().length() > 0) {
					// Muestra informacion de archivo ligado
					Image icon = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/dataview.png"));
					icon.setTitle("Abrir Liga en Ventana Externa.");
					icon.addClickHandler(new UiBmFieldClickHandler(bmoWFlowLog, bmoWFlowLog.getIdField()) {
						@Override
						public void onClick(ClickEvent event) {
							Window.open(((BmoWFlowLog)bmObject).getLink().toString(), "_blank", "");
						}
					});
					listFlexTable.setWidget(row, col++, icon);
				} else {
					listFlexTable.setWidget(row, col++, new HTML(""));
				}
				listFlexTable.formatRow(row);
			}
			row++;
		}
	}

	@Override
	public void create() {
		UiWFlowLogForm uiWFlowLogForm = new UiWFlowLogForm(getUiParams(), 0, wFlowId, customerId);
		uiWFlowLogForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoWFlowLog = (BmoWFlowLog)bmObject;
		UiWFlowLogForm uiWFlowLogForm = new UiWFlowLogForm(getUiParams(), bmoWFlowLog.getId(), wFlowId, customerId);
		uiWFlowLogForm.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		UiWFlowLogForm uiWFlowLogForm = new UiWFlowLogForm(getUiParams(), bmObject.getId(), wFlowId, customerId);
		uiWFlowLogForm.show();
	}

	private class UiWFlowLogForm extends UiFormDialog { 
		UiListBox typeListBox = new UiListBox(getUiParams());
		TextArea commentsTextArea = new TextArea();
		//		BmoWFlow bmoWFlow = new BmoWFlow();
		UiSuggestBox wFlowSuggestBox = new UiSuggestBox(new BmoWFlow());
		//UiListBox wFlowUiListBox = new UiListBox(getUiParams(), new BmoWFlow());

		// Lista de Flujos a descartar por permisos de lectura
		BmoOpportunity bmoOpportunity = new BmoOpportunity();
		BmoOrder bmoOrder = new BmoOrder();
		BmoMeeting bmoMeeting = new BmoMeeting();
		BmoProject bmoProject = new BmoProject();
		BmoPropertySale bmoPropertySale = new BmoPropertySale();
		BmoDevelopmentPhase bmoDevelopmentPhase = new BmoDevelopmentPhase();
		BmoSessionSale bmoSessionSale = new BmoSessionSale();
		BmoCredit bmoCredit = new BmoCredit();
		BmoActivity bmoActivity = new BmoActivity();
		BmoPropertyRental bmoPropertyRental = new BmoPropertyRental();
		BmoCustomer bmoCustomer = new BmoCustomer();
		BmoRequisition bmoRequisition = new BmoRequisition();

		int wFlowId, customerId;

		public UiWFlowLogForm(UiParams uiParams, int id, int wFlowId, int customerId) {
			super(uiParams, new BmoWFlowLog(), id);
			bmoWFlowLog = (BmoWFlowLog)getBmObject();
			this.wFlowId = wFlowId;
			this.customerId = customerId;
			initialize();
		}

		private void initialize() {

			//setWFlowListBoxFilters(customerId);

			setWFlowSuggestBoxFilters(customerId);

		}

		@Override
		public void populateFields() {
			bmoWFlowLog = (BmoWFlowLog)getBmObject();
			//if (getUiType() == UiParams.MASTER) {
			// Asignar valores por default
			if (newRecord) {
				try {
					bmoWFlowLog.getLogdate().setValue(GwtUtil.dateToString(new Date(), getSFParams().getDateTimeFormat()));
					bmoWFlowLog.getUserId().setValue(getSFParams().getLoginInfo().getUserId());
				} catch (BmException e) {
					showErrorMessage(this.getClass().getName()  + "-populateFields(): ERROR " + e.toString());
				}
			}

			formFlexTable.addLabelField(1, 0, bmoWFlowLog.getLogdate());
			if (newRecord)
				formFlexTable.addLabelField(2, 0, getSFParams().getLoginInfo().getBmoUser().getCode());
			else 
				formFlexTable.addLabelField(2, 0, bmoWFlowLog.getBmoUser().getCode());
			//}

			formFlexTable.addField(3, 0, typeListBox, bmoWFlowLog.getType());

			// Dentro de un cliente se puede dar de alta una bitacora ligada 
			// a un flujo donde se ecuentre este
//			if (customerId > 0 ) {
//				formFlexTable.addField(4, 0, wFlowSuggestBox, bmoWFlowLog.getWFlowId());
//			} else {
			//if (!(wFlowId > 0))
			if (customerId > 0 || !(wFlowId > 0)) 
				formFlexTable.addField(4, 0, wFlowSuggestBox, bmoWFlowLog.getWFlowId());
//			}

			formFlexTable.addField(5, 0, commentsTextArea, bmoWFlowLog.getComments());
			//formFlexTable.addField(6, 0, uiFormLabelList);

			statusEffect();
		}


		@Override
		public BmObject populateBObject() throws BmException {
			bmoWFlowLog = new BmoWFlowLog();
			bmoWFlowLog.setId(id);
//			if (customerId > 0 && !(wFlowId > 0)) 
//				bmoWFlowLog.getWFlowId().setValue(wFlowUiListBox.getSelectedId());
//			else {
				if (wFlowId > 0)
					bmoWFlowLog.getWFlowId().setValue(wFlowId);
				else 
					bmoWFlowLog.getWFlowId().setValue(wFlowSuggestBox.getSelectedId());

//			}
			bmoWFlowLog.getComments().setValue(commentsTextArea.getText());
			bmoWFlowLog.getType().setValue(typeListBox.getSelectedCode());
			bmoWFlowLog.getLogdate().setValue(GwtUtil.dateToString(new Date(), getSFParams().getDateTimeFormat()));
			bmoWFlowLog.getUserId().setValue(getSFParams().getLoginInfo().getUserId());
			return bmoWFlowLog;
		}

		public void postShow() {
			if (!(wFlowId > 0)) {
				// Se resetean filtros forzados para tener solo parametros de la lista del flujo
				getUiParams().getUiProgramParams(bmoWFlowLog.getProgramCode()).clearFilters();
			}


			ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
			// Traer bitacora del Flujo
			if (wFlowId > 0) {
				BmFilter forceFilter = new BmFilter();
				forceFilter.setValueLabelFilter(bmoWFlowLog.getKind(), 
						bmoWFlowLog.getWFlowId().getName(), 
						bmoWFlowLog.getWFlowId().getLabel(), 
						BmFilter.EQUALS, 
						wFlowId,
						bmoWFlowLog.getWFlowId().getName());

				filterList.add(forceFilter);
			}

			// Traer bitacora de los Flujos donde se encuentre el cliente
			if (customerId > 0) {
				BmoWFlow bmoWFlow = new BmoWFlow();
				BmFilter filterWFlowLog = new BmFilter();
				filterWFlowLog.setValueFilter(bmoWFlow.getKind(), bmoWFlow.getCustomerId().getName(), customerId);
				filterList.add(filterWFlowLog);
			}

			// Descartar si NO tiene permiso de ver Módulo Oportunidades
			if (!getSFParams().hasRead(bmoOpportunity.getProgramCode())) {
				BmFilter filterWFlowCallerCode = new BmFilter();
				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
						bmoWFlow.getCallerCode(), 
						BmFilter.NOTEQUALS, 
						bmoOpportunity.getProgramCode());
				filterList.add(filterWFlowCallerCode);
			}

			// Descartar si NO tiene permiso de ver Módulo Pedidos
			if (!getSFParams().hasRead(bmoOrder.getProgramCode())) {
				BmFilter filterWFlowCallerCode = new BmFilter();
				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
						bmoWFlow.getCallerCode(), 
						BmFilter.NOTEQUALS, 
						bmoOrder.getProgramCode());
				filterList.add(filterWFlowCallerCode);
			}

			// Descartar si NO tiene permiso de ver Módulo Control de Minutas
			if (!getSFParams().hasRead(bmoMeeting.getProgramCode())) {
				BmFilter filterWFlowCallerCode = new BmFilter();
				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
						bmoWFlow.getCallerCode(), 
						BmFilter.NOTEQUALS, 
						bmoMeeting.getProgramCode());
				filterList.add(filterWFlowCallerCode);
			}

			// Descartar si NO tiene permiso de ver Módulo Proyectos
			if (!getSFParams().hasRead(bmoProject.getProgramCode())) {
				BmFilter filterWFlowCallerCode = new BmFilter();
				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
						bmoWFlow.getCallerCode(), 
						BmFilter.NOTEQUALS, 
						bmoProject.getProgramCode());
				filterList.add(filterWFlowCallerCode);
			}

			// Descartar si NO tiene permiso de ver Módulo Venta de Inmuebles
			if (!getSFParams().hasRead(bmoPropertySale.getProgramCode())) {
				BmFilter filterWFlowCallerCode = new BmFilter();
				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
						bmoWFlow.getCallerCode(), 
						BmFilter.NOTEQUALS, 
						bmoPropertySale.getProgramCode());
				filterList.add(filterWFlowCallerCode);
			}

			// Descartar si NO tiene permiso de ver Módulo Etapas de Desarrollo
			if (!getSFParams().hasRead(bmoDevelopmentPhase.getProgramCode())) {
				BmFilter filterWFlowCallerCode = new BmFilter();
				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
						bmoWFlow.getCallerCode(), 
						BmFilter.NOTEQUALS, 
						bmoDevelopmentPhase.getProgramCode());
				filterList.add(filterWFlowCallerCode);
			}

			// Descartar si NO tiene permiso de ver Módulo Venta de Sesiones
			if (!getSFParams().hasRead(bmoSessionSale.getProgramCode())) {
				BmFilter filterWFlowCallerCode = new BmFilter();
				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
						bmoWFlow.getCallerCode(), 
						BmFilter.NOTEQUALS, 
						bmoSessionSale.getProgramCode());
				filterList.add(filterWFlowCallerCode);
			}
			
			// Descartar si NO tiene permiso de ver Módulo Créditos
			if (!getSFParams().hasRead(bmoCredit.getProgramCode())) {
				BmFilter filterWFlowCallerCode = new BmFilter();
				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
						bmoWFlow.getCallerCode(), 
						BmFilter.NOTEQUALS, 
						bmoCredit.getProgramCode());
				filterList.add(filterWFlowCallerCode);
			}
			
			// Descartar si NO tiene permiso de ver Módulo Actividades
			if (!getSFParams().hasRead(bmoActivity.getProgramCode())) {
				BmFilter filterWFlowCallerCode = new BmFilter();
				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
						bmoWFlow.getCallerCode(), 
						BmFilter.NOTEQUALS, 
						bmoActivity.getProgramCode());
				filterList.add(filterWFlowCallerCode);
				wFlowUiSuggestBox.addFilter(filterWFlowCallerCode);
			}
			
			// Descartar si NO tiene permiso de ver Módulo Arrendamiento
			if (!getSFParams().hasRead(bmoPropertyRental.getProgramCode())) {
				BmFilter filterWFlowCallerCode = new BmFilter();
				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
						bmoWFlow.getCallerCode(), 
						BmFilter.NOTEQUALS, 
						bmoPropertyRental.getProgramCode());
				filterList.add(filterWFlowCallerCode);
			}
			
			// Descartar si NO tiene permiso de ver Módulo Clientes
			if (!getSFParams().hasRead(bmoCustomer.getProgramCode())) {
				BmFilter filterWFlowCallerCode = new BmFilter();
				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
						bmoWFlow.getCallerCode(), 
						BmFilter.NOTEQUALS, 
						bmoCustomer.getProgramCode());
				filterList.add(filterWFlowCallerCode);
			}
			
			// Descartar si NO tiene permiso de ver Módulo OC
			if (!getSFParams().hasRead(bmoCustomer.getProgramCode())) {
				BmFilter filterWFlowCallerCode = new BmFilter();
				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
						bmoWFlow.getCallerCode(), 
						BmFilter.NOTEQUALS, 
						bmoRequisition.getProgramCode());
				filterList.add(filterWFlowCallerCode);
			}

			getUiParams().getUiProgramParams(bmoWFlowLog.getProgramCode()).setFilterList(filterList);

			statusEffect();
		}

		// Asigna filtros al listado de Flujos
//		private void setWFlowListBoxFilters(int customerId) {
//
//			BmoWFlow bmoWFlow = new BmoWFlow();
//			if (customerId > 0) {
//				// Agregar Flujos donde se encuentre el cliente
//				BmFilter filterWFlowCustomer = new BmFilter();
//				
//				filterWFlowCustomer.setInFilter(bmoWFlowLog.getKind(), 
//						bmoWFlow.getIdFieldName(), 
//						bmoWFlowLog.getWFlowId().getName(), bmoWFlowLog.getCustomerId().getName(), "" + customerId);
//				//filterWFlowCustomer.setValueFilter(bmoWFlowLog.getKind(), bmoWFlowLog.getCustomerId().getName(), customerId);
//				wFlowUiListBox.addBmFilter(filterWFlowCustomer);
//			}
//
//			// Descartar si NO tiene permiso de ver Módulo Componentes
//			if (!getSFParams().hasRead(bmoSFComponent.getProgramCode())) {
//				BmFilter filterWFlowCallerCode = new BmFilter();
//				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
//						bmoWFlow.getCallerCode(), 
//						BmFilter.NOTEQUALS, 
//						bmoSFComponent.getProgramCode());
//				wFlowUiListBox.addBmFilter(filterWFlowCallerCode);
//			}                                         
//			// Descartar si NO tiene permiso de ver Módulo Oportunidades
//			if (!getSFParams().hasRead(bmoOpportunity.getProgramCode())) {
//				BmFilter filterWFlowCallerCode = new BmFilter();
//				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
//						bmoWFlow.getCallerCode(), 
//						BmFilter.NOTEQUALS, 
//						bmoOpportunity.getProgramCode());
//				wFlowUiListBox.addBmFilter(filterWFlowCallerCode);
//			}
//
//			// Descartar si NO tiene permiso de ver Módulo Pedidos
//			if (!getSFParams().hasRead(bmoOrder.getProgramCode())) {
//				BmFilter filterWFlowCallerCode = new BmFilter();
//				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
//						bmoWFlow.getCallerCode(), 
//						BmFilter.NOTEQUALS, 
//						bmoOrder.getProgramCode());
//				wFlowUiListBox.addBmFilter(filterWFlowCallerCode);
//			}
//
//			// Descartar si NO tiene permiso de ver Módulo Control de Minutas
//			if (!getSFParams().hasRead(bmoMeeting.getProgramCode())) {
//				BmFilter filterWFlowCallerCode = new BmFilter();
//				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
//						bmoWFlow.getCallerCode(), 
//						BmFilter.NOTEQUALS, 
//						bmoMeeting.getProgramCode());
//				wFlowUiListBox.addBmFilter(filterWFlowCallerCode);
//			}
//
//			// Descartar si NO tiene permiso de ver Módulo Proyectos
//			if (!getSFParams().hasRead(bmoProject.getProgramCode())) {
//				BmFilter filterWFlowCallerCode = new BmFilter();
//				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
//						bmoWFlow.getCallerCode(), 
//						BmFilter.NOTEQUALS, 
//						bmoProject.getProgramCode());
//				wFlowUiListBox.addBmFilter(filterWFlowCallerCode);
//			}
//
//			// Descartar si NO tiene permiso de ver Módulo Venta de Inmuebles
//			if (!getSFParams().hasRead(bmoPropertySale.getProgramCode())) {
//				BmFilter filterWFlowCallerCode = new BmFilter();
//				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
//						bmoWFlow.getCallerCode(), 
//						BmFilter.NOTEQUALS, 
//						bmoPropertySale.getProgramCode());
//				wFlowUiListBox.addBmFilter(filterWFlowCallerCode);
//			}
//
//			// Descartar si NO tiene permiso de ver Módulo Etapas de Desarrollo
//			if (!getSFParams().hasRead(bmoDevelopmentPhase.getProgramCode())) {
//				BmFilter filterWFlowCallerCode = new BmFilter();
//				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
//						bmoWFlow.getCallerCode(), 
//						BmFilter.NOTEQUALS, 
//						bmoDevelopmentPhase.getProgramCode());
//				wFlowUiListBox.addBmFilter(filterWFlowCallerCode);
//			}
//
//			// Descartar si NO tiene permiso de ver Módulo Venta de Sesiones
//			if (!getSFParams().hasRead(bmoSessionSale.getProgramCode())) {
//				BmFilter filterWFlowCallerCode = new BmFilter();
//				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
//						bmoWFlow.getCallerCode(), 
//						BmFilter.NOTEQUALS, 
//						bmoSessionSale.getProgramCode());
//				wFlowUiListBox.addBmFilter(filterWFlowCallerCode);
//			}
//			
//			// Descartar si NO tiene permiso de ver Módulo Créditos
//			if (!getSFParams().hasRead(bmoCredit.getProgramCode())) {
//				BmFilter filterWFlowCallerCode = new BmFilter();
//				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
//						bmoWFlow.getCallerCode(), 
//						BmFilter.NOTEQUALS, 
//						bmoCredit.getProgramCode());
//				wFlowUiListBox.addBmFilter(filterWFlowCallerCode);
//			}
//			
//			// Descartar si NO tiene permiso de ver Módulo Actividades
//			if (!getSFParams().hasRead(bmoActivity.getProgramCode())) {
//				BmFilter filterWFlowCallerCode = new BmFilter();
//				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
//						bmoWFlow.getCallerCode(), 
//						BmFilter.NOTEQUALS, 
//						bmoActivity.getProgramCode());
//				wFlowUiListBox.addBmFilter(filterWFlowCallerCode);
//			}
//			
//			// Descartar si NO tiene permiso de ver Módulo Arrendamiento
//			if (!getSFParams().hasRead(bmoPropertyRental.getProgramCode())) {
//				BmFilter filterWFlowCallerCode = new BmFilter();
//				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
//						bmoWFlow.getCallerCode(), 
//						BmFilter.NOTEQUALS, 
//						bmoPropertyRental.getProgramCode());
//				wFlowUiListBox.addBmFilter(filterWFlowCallerCode);
//			}
//			
//			// Descartar si NO tiene permiso de ver Módulo Clientes
//			if (!getSFParams().hasRead(bmoCustomer.getProgramCode())) {
//				BmFilter filterWFlowCallerCode = new BmFilter();
//				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
//						bmoWFlow.getCallerCode(), 
//						BmFilter.NOTEQUALS, 
//						bmoCustomer.getProgramCode());
//				wFlowUiListBox.addBmFilter(filterWFlowCallerCode);
//			}
//		}

		// Asigna filtros al suggest de Flujos
		private void setWFlowSuggestBoxFilters(int customerId) {
			
			// Filtro Flujo
			BmoWFlow bmoWFlow = new BmoWFlow();
			if (customerId > 0) {
				// Agregar Flujos donde se encuentre el cliente
				BmFilter filterWFlowCustomer = new BmFilter();
				filterWFlowCustomer.setValueFilter(bmoWFlow.getKind(), bmoWFlow.getCustomerId().getName(), customerId);
				wFlowSuggestBox.addFilter(filterWFlowCustomer);
			}

			// Descartar si NO tiene permiso de ver Módulo Oportunidades
			if (!getSFParams().hasRead(bmoOpportunity.getProgramCode())) {
				BmFilter filterWFlowCallerCode = new BmFilter();
				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
						bmoWFlow.getCallerCode(), 
						BmFilter.NOTEQUALS, 
						bmoOpportunity.getProgramCode());
				wFlowSuggestBox.addFilter(filterWFlowCallerCode);
			}

			// Descartar si NO tiene permiso de ver Módulo Pedidos
			if (!getSFParams().hasRead(bmoOrder.getProgramCode())) {
				BmFilter filterWFlowCallerCode = new BmFilter();
				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
						bmoWFlow.getCallerCode(), 
						BmFilter.NOTEQUALS, 
						bmoOrder.getProgramCode());
				wFlowSuggestBox.addFilter(filterWFlowCallerCode);
			}

			// Descartar si NO tiene permiso de ver Módulo Control de Minutas
			if (!getSFParams().hasRead(bmoMeeting.getProgramCode())) {
				BmFilter filterWFlowCallerCode = new BmFilter();
				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
						bmoWFlow.getCallerCode(), 
						BmFilter.NOTEQUALS, 
						bmoMeeting.getProgramCode());
				wFlowSuggestBox.addFilter(filterWFlowCallerCode);
			}

			// Descartar si NO tiene permiso de ver Módulo Proyectos
			if (!getSFParams().hasRead(bmoProject.getProgramCode())) {
				BmFilter filterWFlowCallerCode = new BmFilter();
				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
						bmoWFlow.getCallerCode(), 
						BmFilter.NOTEQUALS, 
						bmoProject.getProgramCode());
				wFlowSuggestBox.addFilter(filterWFlowCallerCode);
			}

			// Descartar si NO tiene permiso de ver Módulo Venta de Inmuebles
			if (!getSFParams().hasRead(bmoPropertySale.getProgramCode())) {
				BmFilter filterWFlowCallerCode = new BmFilter();
				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
						bmoWFlow.getCallerCode(), 
						BmFilter.NOTEQUALS, 
						bmoPropertySale.getProgramCode());
				wFlowSuggestBox.addFilter(filterWFlowCallerCode);
			}

			// Descartar si NO tiene permiso de ver Módulo Etapas de Desarrollo
			if (!getSFParams().hasRead(bmoDevelopmentPhase.getProgramCode())) {
				BmFilter filterWFlowCallerCode = new BmFilter();
				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
						bmoWFlow.getCallerCode(), 
						BmFilter.NOTEQUALS, 
						bmoDevelopmentPhase.getProgramCode());
				wFlowSuggestBox.addFilter(filterWFlowCallerCode);
			}

			// Descartar si NO tiene permiso de ver Módulo Venta de Sesiones
			if (!getSFParams().hasRead(bmoSessionSale.getProgramCode())) {
				BmFilter filterWFlowCallerCode = new BmFilter();
				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
						bmoWFlow.getCallerCode(), 
						BmFilter.NOTEQUALS, 
						bmoSessionSale.getProgramCode());
				wFlowSuggestBox.addFilter(filterWFlowCallerCode);
			}
			
			// Descartar si NO tiene permiso de ver Módulo Créditos
			if (!getSFParams().hasRead(bmoCredit.getProgramCode())) {
				BmFilter filterWFlowCallerCode = new BmFilter();
				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
						bmoWFlow.getCallerCode(), 
						BmFilter.NOTEQUALS, 
						bmoCredit.getProgramCode());
				wFlowSuggestBox.addFilter(filterWFlowCallerCode);
			}
			
			// Descartar si NO tiene permiso de ver Módulo Actividades
			if (!getSFParams().hasRead(bmoActivity.getProgramCode())) {
				BmFilter filterWFlowCallerCode = new BmFilter();
				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
						bmoWFlow.getCallerCode(), 
						BmFilter.NOTEQUALS, 
						bmoActivity.getProgramCode());
				wFlowSuggestBox.addFilter(filterWFlowCallerCode);
			}
			
			// Descartar si NO tiene permiso de ver Módulo Arrendamiento
			if (!getSFParams().hasRead(bmoPropertyRental.getProgramCode())) {
				BmFilter filterWFlowCallerCode = new BmFilter();
				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
						bmoWFlow.getCallerCode(), 
						BmFilter.NOTEQUALS, 
						bmoPropertyRental.getProgramCode());
				wFlowSuggestBox.addFilter(filterWFlowCallerCode);
			}
			
			// Descartar si NO tiene permiso de ver Módulo Clientes
			if (!getSFParams().hasRead(bmoCustomer.getProgramCode())) {
				BmFilter filterWFlowCallerCode = new BmFilter();
				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
						bmoWFlow.getCallerCode(), 
						BmFilter.NOTEQUALS, 
						bmoCustomer.getProgramCode());
				wFlowSuggestBox.addFilter(filterWFlowCallerCode);
			}
			
			// Descartar si NO tiene permiso de ver Módulo Clientes
			if (!getSFParams().hasRead(bmoCustomer.getProgramCode())) {
				BmFilter filterWFlowCallerCode = new BmFilter();
				filterWFlowCallerCode.setValueOperatorFilter(bmoWFlow.getKind(), 
						bmoWFlow.getCallerCode(), 
						BmFilter.NOTEQUALS, 
						bmoRequisition.getProgramCode());
				wFlowSuggestBox.addFilter(filterWFlowCallerCode);
			}
		}

		private void statusEffect() {
			if (!newRecord) {
				typeListBox.setEnabled(false);
				wFlowSuggestBox.setEnabled(false);
				//wFlowUiListBox.setEnabled(false);
				commentsTextArea.setEnabled(false);
				saveButton.setVisible(false);
				deleteButton.setVisible(false);
			}
		}

		@Override
		public void close() {
			list();
		}
	}
}
