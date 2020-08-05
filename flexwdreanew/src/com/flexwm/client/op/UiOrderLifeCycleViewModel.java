package com.flexwm.client.op;

import java.util.ArrayList;
import java.util.Iterator;
import com.flexwm.shared.fi.BmoBankMovConcept;
import com.flexwm.shared.fi.BmoRaccount;
import com.flexwm.shared.fi.BmoRaccountType;
import com.flexwm.shared.op.BmoOrder;
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


public class UiOrderLifeCycleViewModel implements TreeViewModel {
	private SelectionModel<BmObject> selectionModel;
	private final DefaultSelectionEventManager<BmObject> selectionManager = DefaultSelectionEventManager.createDefaultManager();
	private NumberFormat numberFormat = NumberFormat.getDecimalFormat();
	int orderId;

	Cell<BmObject> cell = new AbstractCell<BmObject>() {
		@Override
		public void render(Context context, BmObject bmObject, SafeHtmlBuilder sb) {
			if (bmObject != null) {

				// Si es de tipo Pedido
				if (bmObject instanceof BmoOrder) {
					BmoOrder bmoOrder = (BmoOrder) bmObject;
					Iterator<BmField> fieldIterator = bmoOrder.getDisplayFieldList().iterator();
					BmField bmField = fieldIterator.next();
					String toolTip = bmField.getLabel() + ": " + bmField.toString();
					while (fieldIterator.hasNext()) { 
						bmField = fieldIterator.next();
						toolTip += "\n" + bmField.getLabel() + ": " 
								+ ((bmField.getFieldType() == BmFieldType.OPTIONS) ? "" + bmField.getSelectedOption().getLabel() : "" + bmField.toString());
					} 

					sb.appendHtmlConstant("<span title=\"" + toolTip + "\">" 
							+ bmoOrder.getCode().toString()
							+ " | " + bmoOrder.getName().toString()
							+ "</span>");
				}

				// Tipo Cuenta por Cobrar
				else if (bmObject instanceof BmoRaccount) {
					BmoRaccount bmoRaccount = (BmoRaccount) bmObject;
					Iterator<BmField> fieldIterator = bmoRaccount.getDisplayFieldList().iterator();
					BmField bmField = fieldIterator.next();
					String toolTip = bmField.getLabel() + ": " + bmField.toString();
					while (fieldIterator.hasNext()) { 
						bmField = fieldIterator.next();
						toolTip += "\n" + bmField.getLabel() + ": " 
								+ ((bmField.getFieldType() == BmFieldType.OPTIONS) ? "" + bmField.getSelectedOption().getLabel() : "" + bmField.toString());
					} 

					sb.appendHtmlConstant("<span title=\"" + toolTip + "\">" 
							+ bmoRaccount.getCode().toString()
							+ " | " + bmoRaccount.getPaymentDate().toString()
							+ " | $" + numberFormat.format(bmoRaccount.getTotal().toDouble())
							+ " | " + bmoRaccount.getStatus().getSelectedOption().getLabel()
							+ "</span>");
				}

				// Tipo Concepto de Movimiento Bancario
				else if (bmObject instanceof BmoBankMovConcept) {
					BmoBankMovConcept bmoBankMovConcept = (BmoBankMovConcept) bmObject;
					Iterator<BmField> fieldIterator = bmoBankMovConcept.getDisplayFieldList().iterator();
					BmField bmField = fieldIterator.next();
					String toolTip = bmField.getLabel() + ": " + bmField.toString();
					while (fieldIterator.hasNext()) { 
						bmField = fieldIterator.next();
						toolTip += "\n" + bmField.getLabel() + ": " 
								+ ((bmField.getFieldType() == BmFieldType.OPTIONS) ? "" + bmField.getSelectedOption().getLabel() : "" + bmField.toString());
					} 

					sb.appendHtmlConstant("<span title=\"" + toolTip + "\">" 
							+ bmoBankMovConcept.getBmoBankMovement().getCode().toString()
							+ " | " + bmoBankMovConcept.getBmoBankMovement().getDueDate().toString()
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

	public UiOrderLifeCycleViewModel(SelectionModel<BmObject> selectionModel, int orderId){
		this.selectionModel = selectionModel;
		this.orderId = orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	@Override
	public <T> NodeInfo<?> getNodeInfo(T parentBmObject) {

		// Mostrar Cuentas x Pagar
		if (parentBmObject instanceof BmoOrder) {
			BmoRaccount bmoRaccount = new BmoRaccount();

			ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
			BmFilter filterByOrder = new BmFilter();
			filterByOrder.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getOrderId().getName(), ((BmObject)parentBmObject).getId());
			filterList.add(filterByOrder);

			BmFilter filterByType = new BmFilter();
			filterByType.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getBmoRaccountType().getType(), "" + BmoRaccountType.TYPE_WITHDRAW);
			filterList.add(filterByType);

			return new DefaultNodeInfo<BmObject>(new UiListDataProvider<BmObject>(new BmoRaccount(), filterList), cell, selectionModel, selectionManager, null);
		} 

		// Mostrar Conceptos de Banco
		else if (parentBmObject instanceof BmoRaccount) {
			BmoBankMovConcept bmoBankMovConcept = new BmoBankMovConcept();
			BmFilter bmFilter = new BmFilter();
			bmFilter.setValueFilter(bmoBankMovConcept.getKind(), bmoBankMovConcept.getRaccountId().getName(), ((BmObject)parentBmObject).getId());
			return new DefaultNodeInfo<BmObject>(new UiListDataProvider<BmObject>(new BmoBankMovConcept(), bmFilter), cell, selectionModel, selectionManager, null);
		} 

		// Panel raiz
		else {
			BmoOrder bmoOrder = new BmoOrder();
			BmFilter bmFilter = new BmFilter();
			bmFilter.setValueFilter(bmoOrder.getKind(), bmoOrder.getIdField(), orderId);
			return new DefaultNodeInfo<BmObject>(new UiListDataProvider<BmObject>(new BmoOrder(), bmFilter), cell, selectionModel, selectionManager, null);
		}		
	}

	@Override
	public boolean isLeaf(Object value) {
		boolean leaf = false;
		if (value instanceof BmoBankMovConcept) leaf = true;
		return leaf;
	}
}
