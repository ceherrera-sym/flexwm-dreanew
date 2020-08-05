package com.flexwm.client.op;

import java.util.ArrayList;
import java.util.Iterator;
import com.flexwm.shared.fi.BmoBankMovConcept;
import com.flexwm.shared.fi.BmoPaccount;
import com.flexwm.shared.fi.BmoPaccountType;
import com.flexwm.shared.op.BmoRequisition;
import com.flexwm.shared.op.BmoRequisitionReceipt;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.TreeViewModel;
import com.symgae.client.ui.UiListDataProvider;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;


public class UiRequisitionLifeCycleViewModel implements TreeViewModel {
	private SelectionModel<BmObject> selectionModel;
	private final DefaultSelectionEventManager<BmObject> selectionManager = DefaultSelectionEventManager.createDefaultManager();
	private NumberFormat numberFormat = NumberFormat.getDecimalFormat();
	int requisitionId;


	Cell<BmObject> cell = new AbstractCell<BmObject>() {

		@Override
		public void render(Context context, BmObject bmObject, SafeHtmlBuilder sb) {
			if (bmObject != null) {

				// Si es de tipo Orden de Compra
				if (bmObject instanceof BmoRequisition) {
					BmoRequisition bmoRequisition = (BmoRequisition) bmObject;
					Iterator<BmField> fieldIterator = bmoRequisition.getDisplayFieldList().iterator();
					BmField bmField = (BmField)fieldIterator.next();
					String toolTip = bmField.getLabel() + ": " + bmField.toString();
					while (fieldIterator.hasNext()) { 
						bmField = (BmField)fieldIterator.next();

						toolTip += "\n" + bmField.getLabel() + ": " 
								+ ((bmField.getFieldType() == BmFieldType.OPTIONS) ? "" + bmField.getSelectedOption().getLabel() : "" + bmField.toString());
					} 

					sb.appendHtmlConstant("<span title=\"" + toolTip + "\">" 
							+ bmoRequisition.getCode().toString()
							+ " | " + bmoRequisition.getName().toString()
							+ "</span>");					
				}

				// Si es de tipo Recepcion de Orden de Compra
				else if (bmObject instanceof BmoRequisitionReceipt) {
					BmoRequisitionReceipt bmoRequisitionReceipt = (BmoRequisitionReceipt) bmObject;
					Iterator<BmField> fieldIterator = bmoRequisitionReceipt.getDisplayFieldList().iterator();
					BmField bmField = (BmField)fieldIterator.next();
					String toolTip = bmField.getLabel() + ": " + bmField.toString();
					while (fieldIterator.hasNext()) { 
						bmField = (BmField)fieldIterator.next();
						toolTip += "\n" + bmField.getLabel() + ": " 
								+ ((bmField.getFieldType() == BmFieldType.OPTIONS) ? "" + bmField.getSelectedOption().getLabel() : "" + bmField.toString());
					} 

					sb.appendHtmlConstant("<span title=\"" + toolTip + "\">" 
							+ bmoRequisitionReceipt.getCode().toString()
							+ " | " + bmoRequisitionReceipt.getReceiptDate().toString()
							+ " | $" + numberFormat.format(bmoRequisitionReceipt.getTotal().toDouble())
							+ "</span>");
				}

				// Tipo Cuenta por Pagar
				else if (bmObject instanceof BmoPaccount) {
					BmoPaccount bmoPaccount = (BmoPaccount) bmObject;
					Iterator<BmField> fieldIterator = bmoPaccount.getDisplayFieldList().iterator();
					BmField bmField = (BmField)fieldIterator.next();
					String toolTip = bmField.getLabel() + ": " + bmField.toString();
					while (fieldIterator.hasNext()) { 
						bmField = (BmField)fieldIterator.next();
						toolTip += "\n" + bmField.getLabel() + ": " 
								+ ((bmField.getFieldType() == BmFieldType.OPTIONS) ? "" + bmField.getSelectedOption().getLabel() : "" + bmField.toString());
					} 

					sb.appendHtmlConstant("<span title=\"" + toolTip + "\">" 
							+ bmoPaccount.getCode().toString()
							+ " | " + bmoPaccount.getPaymentDate().toString()
							+ " | $" + numberFormat.format(bmoPaccount.getTotal().toDouble())
							+ " | " + bmoPaccount.getStatus().getSelectedOption().getLabel()
							+ "</span>");
				}

				// Tipo Concepto de Movimiento Bancario
				else if (bmObject instanceof BmoBankMovConcept) {
					BmoBankMovConcept bmoBankMovConcept = (BmoBankMovConcept) bmObject;
					Iterator<BmField> fieldIterator = bmoBankMovConcept.getDisplayFieldList().iterator();
					BmField bmField = (BmField)fieldIterator.next();
					String toolTip = bmField.getLabel() + ": " + bmField.toString();
					while (fieldIterator.hasNext()) { 
						bmField = (BmField)fieldIterator.next();
						toolTip += "\n" + bmField.getLabel() + ": " 
								+ ((bmField.getFieldType() == BmFieldType.OPTIONS) ? "" + bmField.getSelectedOption().getLabel() : "" + bmField.toString());
					} 

					sb.appendHtmlConstant("<span title=\"" + toolTip + "\">" 
							+ bmoBankMovConcept.getBmoBankMovement().getCode().toString()
							+ " | " + bmoBankMovConcept.getBmoBankMovement().getDueDate().toString()
							+ " | " + bmoBankMovConcept.getBmoBankMovement().getNoCheck().toString()
							+ " | $" + numberFormat.format(bmoBankMovConcept.getAmount().toDouble())
							+ " | " + bmoBankMovConcept.getBmoBankMovement().getStatus().getSelectedOption().getLabel()
							+ "</span>");
				}

				// Otro tipo desconocido
				else {
					sb.appendEscaped(bmObject.getDisplayFieldList().get(1).toString());
				}
			}
		}
	};

	public UiRequisitionLifeCycleViewModel(SelectionModel<BmObject> selectionModel, int requisitionId){
		this.selectionModel = selectionModel;
		this.requisitionId = requisitionId;
	}

	public <T> NodeInfo<?> getNodeInfo(T parentBmObject) {

		// Mostrar recibos de Ordenes de Compra
		if (parentBmObject instanceof BmoRequisition) {
			BmoRequisitionReceipt bmoRequisitionReceipt = new BmoRequisitionReceipt();
			BmFilter bmFilter = new BmFilter();
			bmFilter.setValueFilter(bmoRequisitionReceipt.getKind(), bmoRequisitionReceipt.getRequisitionId().getName(), requisitionId);
			return new DefaultNodeInfo<BmObject>(new UiListDataProvider<BmObject>(new BmoRequisitionReceipt(), bmFilter), cell, selectionModel, selectionManager, null);
		} 

		// Mostrar Cuentas x Pagar
		if (parentBmObject instanceof BmoRequisitionReceipt) {
			BmoPaccount bmoPaccount = new BmoPaccount();

			ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
			BmFilter filterByRequisitionReceipt = new BmFilter();
			filterByRequisitionReceipt.setValueFilter(bmoPaccount.getKind(), bmoPaccount.getRequisitionReceiptId().getName(), ((BmObject)parentBmObject).getId());
			filterList.add(filterByRequisitionReceipt);

			BmFilter filterByType = new BmFilter();
			filterByType.setValueFilter(bmoPaccount.getKind(), bmoPaccount.getBmoPaccountType().getType(), "" + BmoPaccountType.TYPE_WITHDRAW);
			filterList.add(filterByType);

			return new DefaultNodeInfo<BmObject>(new UiListDataProvider<BmObject>(new BmoPaccount(), filterList), cell, selectionModel, selectionManager, null);
		} 

		// Mostrar Conceptos de Banco
		else if (parentBmObject instanceof BmoPaccount) {
			BmoBankMovConcept bmoBankMovConcept = new BmoBankMovConcept();
			BmFilter bmFilter = new BmFilter();
			bmFilter.setValueFilter(bmoBankMovConcept.getKind(), bmoBankMovConcept.getPaccountId().getName(), ((BmObject)parentBmObject).getId());
			return new DefaultNodeInfo<BmObject>(new UiListDataProvider<BmObject>(new BmoBankMovConcept(), bmFilter), cell, selectionModel, selectionManager, null);
		} 

		// Panel raiz
		else {
			BmoRequisition bmoRequisition = new BmoRequisition();
			BmFilter bmFilter = new BmFilter();
			bmFilter.setValueFilter(bmoRequisition.getKind(), bmoRequisition.getIdField(), requisitionId);
			return new DefaultNodeInfo<BmObject>(new UiListDataProvider<BmObject>(new BmoRequisition(), bmFilter), cell, selectionModel, selectionManager, null);
		}		
	}

	public boolean isLeaf(Object value) {
		boolean leaf = false;
		if (value instanceof BmoBankMovConcept) leaf = true;
		return leaf;
	}
}
