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
import java.util.Iterator;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowPhase;


public class UiWFlowPhaseBar extends Ui {
	int presentSequence;
	protected ArrayList<BmObject> data;
	private HorizontalPanel hp = new HorizontalPanel();
	Panel fp;
	BmFilter bmFilterPhase = new BmFilter();
	BmoWFlowPhase bmoWFlowPhase;
	BmoWFlow bmoWFlow = new BmoWFlow();
	int wFlowId;
	boolean processing = false;
	private int wFlowIdRPC = 0; 
	private int updateWFlowRpcAttempt = 0;
	private int listRpcAttempt = 0;

	public UiWFlowPhaseBar(UiParams uiParams, Panel defaultPanel, int wFlowId) {
		super(uiParams, new BmoWFlowPhase());
		bmoWFlowPhase = (BmoWFlowPhase)getBmObject();
		this.fp = defaultPanel;
		this.wFlowId = wFlowId;
	}
	
	public void show() {
		fp.clear();
		
		hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		fp.add(hp);
		
		updateWFlow(wFlowId);
	}

	// Primer intento
	public void updateWFlow(int wFlowIdRPC) {
		updateWFlow(wFlowIdRPC, 0);
	}
	
	public void updateWFlow(int wFlowIdRPC, int updateWFlowRpcAttempt) {
		if (updateWFlowRpcAttempt < 5) {
			setwFlowIdRPC(wFlowIdRPC);
			setUpdateWFlowRpcAttempt(updateWFlowRpcAttempt + 1);

			AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
				public void onFailure(Throwable caught) {
					if (getUpdateWFlowRpcAttempt() < 5)
						updateWFlow(getwFlowIdRPC(), getUpdateWFlowRpcAttempt());
					else {
						if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {}
						else showErrorMessage(this.getClass().getName() + "-updateWFlow() ERROR: " + caught.toString());
						processing = false;
					}
				}
	
				public void onSuccess(BmObject result) {
					setUpdateWFlowRpcAttempt(0);
					setWFlow((BmoWFlow)result);
					processing = false;
				}
			};
			try {
				if (!processing) {
					processing = true;
					getUiParams().getBmObjectServiceAsync().get(bmoWFlow.getPmClass(), wFlowIdRPC, callback);
				}
			} catch (SFException e) {
				showErrorMessage(this.getClass().getName() + "-updateWFlow() ERROR: " + e.toString());
			}
		}
	}
	
	public void setWFlow(BmoWFlow bmoWFlow) {
		this.bmoWFlow = bmoWFlow;
		
		bmFilterPhase.setValueFilter(bmoWFlowPhase.getKind(), 
				bmoWFlowPhase.getWFlowCategoryId().getName(),
				bmoWFlow.getBmoWFlowType().getWFlowCategoryId().toInteger());
		
		this.presentSequence = bmoWFlow.getBmoWFlowPhase().getSequence().toInteger();
		list();
	}

	public void displayList() {
		Iterator<BmObject> iterator = data.iterator();
		int zindex = 0;
		while (iterator.hasNext()) {
		    BmoWFlowPhase cellBmObject = (BmoWFlowPhase)iterator.next(); 
		    
		    Button l = new Button(cellBmObject.getSequence().toString() + "." + cellBmObject.getName().toString());
		    
		    if (bmoWFlow.getProgress().toInteger() == 100) l.setStyleName("pastPhase");
		    else {
			    if (cellBmObject.getSequence().toInteger() < presentSequence) l.setStyleName("pastPhase");
			    if (cellBmObject.getSequence().toInteger() == presentSequence) l.setStyleName("presentPhase");
			    if (cellBmObject.getSequence().toInteger() > presentSequence) l.setStyleName("futurePhase");
		    }
		    
		    l.getElement().getStyle().setZIndex(zindex);
		    l.setEnabled(false);
		    l.setSize("100px", "30px");
		    
		    hp.add(l);
		    zindex--;
		}
	}
	
	private void setListData(ArrayList<BmObject> data){
		this.data = data;
	}
	// Primer intento
	public void list() {
		list(0);
	}
	public void list(int listRpcAttempt) {
		if (listRpcAttempt < 5) {
			setListRpcAttempt(listRpcAttempt + 1);

			// Set up the callback object.
			AsyncCallback<ArrayList<BmObject>> callback = new AsyncCallback<ArrayList<BmObject>>() {
				public void onFailure(Throwable caught) {
					if (getListRpcAttempt() < 5)
						list(getListRpcAttempt());
					else
						showErrorMessage(this.getClass().getName() + "-list() ERROR: " + caught.toString());
				}
				public void onSuccess(ArrayList<BmObject> result) {
					setListRpcAttempt(0);
					setListData(result);
					displayList();
				}
			};
			try {
				// Si es de tipo lista de detalle, no se maneja paginado
				getUiParams().getBmObjectServiceAsync().list(getBmObject().getPmClass(), bmFilterPhase, callback);
			} catch (SFException e) {
				showErrorMessage(this.getClass().getName() + "-list() ERROR: " + e.toString());
			}
		}
	}
	
	// Variables para llamadas RPC
	public int getwFlowIdRPC() {
		return wFlowIdRPC;
	}

	public void setwFlowIdRPC(int wFlowIdRPC) {
		this.wFlowIdRPC = wFlowIdRPC;
	}

	public int getUpdateWFlowRpcAttempt() {
		return updateWFlowRpcAttempt;
	}

	public void setUpdateWFlowRpcAttempt(int updateWFlowRpcAttempt) {
		this.updateWFlowRpcAttempt = updateWFlowRpcAttempt;
	}
	
	public int getListRpcAttempt() {
		return listRpcAttempt;
	}

	public void setListRpcAttempt(int listRpcAttempt) {
		this.listRpcAttempt = listRpcAttempt;
	}
}