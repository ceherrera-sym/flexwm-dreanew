/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.cm;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import com.flexwm.shared.cm.BmoCustomer;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiClickHandler;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFException;


public class UiBirthdayView extends Ui {
	private boolean processing = false;

	public UiBirthdayView(UiParams uiParams, Panel defaultPanel) {
		super(uiParams, defaultPanel);
	}

	public void show() {
		listBirthdays();
	}

	public void listBirthdays(){
		// Set up the callback object.
		AsyncCallback<ArrayList<BmObject>> callback = new AsyncCallback<ArrayList<BmObject>>() {
			public void onFailure(Throwable caught) {
				processing = false;
				showErrorMessage(this.getClass().getName() + "-listBirthdays() ERROR: " + caught.toString());	
			}
			public void onSuccess(ArrayList<BmObject> result) {
				processing = false;
				populateBirthdayList(result);
			}
		};
		try {
			if (!processing) {
				processing = true;
				BmoCustomer bmoCustomer = new BmoCustomer();
				Date today = new Date();
				BmFilter bmFilter = new BmFilter();
				bmFilter.setDayMonthFilter(bmoCustomer.getKind(), bmoCustomer.getBirthdate().getName(), 
						bmoCustomer.getBirthdate().getName(),
						DateTimeFormat.getFormat("M").format(today),
						DateTimeFormat.getFormat("d").format(today));
				getUiParams().getBmObjectServiceAsync().list(bmoCustomer.getPmClass(), bmFilter, callback);
			}
		} catch (SFException e) {
			showErrorMessage(this.getClass().getName() + "-listBirthdays() ERROR: " + e.toString());
		}
	}
	
	public void populateBirthdayList(ArrayList<BmObject> customerList){
		Iterator<BmObject> customerIterator = customerList.iterator();
		while (customerIterator.hasNext()) {
			BmoCustomer bmoCustomer = (BmoCustomer)customerIterator.next();
			HorizontalPanel hp = new HorizontalPanel();

			Image img = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/16/wi0028-16.png"));
			
			Label customerLabel = new Label(bmoCustomer.getDisplayName().toString());
			customerLabel.setStyleName("listCellLink");
			customerLabel.addClickHandler( new UiClickHandler(bmoCustomer) {
				@Override
				public void onClick(ClickEvent event) {
					customerSelected(this.bmObject);
				}
			});
			
			hp.add(img);
			hp.add(customerLabel);

			addToDP(hp);			
		}
	}
	
	public void customerSelected(BmObject bmObject) {
		UiCustomer uiCustomer = new UiCustomer(getUiParams());
		uiCustomer.edit(bmObject);
	}
}
