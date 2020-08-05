/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.ev;

import com.flexwm.shared.ev.BmoVenue;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;


public class UiVenueView extends Ui {
	private boolean processing = false;
	private int id = 0;
	private BmoVenue bmoVenue;

	final SimplePanel mapPanel = new SimplePanel();
	final DialogBox dialogBox = new DialogBox(true);

	public UiVenueView(UiParams uiParams, Panel defaultPanel, int id) {
		super(uiParams, new BmoVenue(), defaultPanel);
		this.id = id;

		dialogBox.hide();

		dialogBox.add(mapPanel);

		dialogBox.setGlassEnabled(true);
		//		dialogBox.setAnimationEnabled(true);
		dialogBox.setPixelSize(620, 420);
		dialogBox.setText("SYMGF-Maps");

		mapPanel.setSize("600px", "400px");
	}

	@Override
	public void show() {
		getVenue(id);
	}


	public void populateDetail(){
		bmoVenue = (BmoVenue)getBmObject();

		Label titleLabel = new Label(bmoVenue.getName().toString());
		titleLabel.setStyleName("detailText");
		addToDP(titleLabel);

		Label streetLabel = new Label(bmoVenue.getStreet().toString() + " " + bmoVenue.getNumber().toString());
		streetLabel.setStyleName("detailText");
		addToDP(streetLabel);

		Label otherLabel = new Label(bmoVenue.getNeighborhood().toString() + " " + bmoVenue.getZip().toString());
		otherLabel.setStyleName("detailText");
		addToDP(otherLabel);		

		Label cityLabel = new Label(bmoVenue.getBmoCity().getName().toString() + ", " + bmoVenue.getBmoCity().getBmoState().getCode().toString());
		cityLabel.setStyleName("detailText");
		addToDP(cityLabel);

		/*Image mapImage = new Image(GwtUtil.getProperUrl(uiParams.getSFParams(), "/icons/map.png"));
		mapImage.setStyleName("listCellLink");
		mapImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				prepareMap();
			}
		});*/

		//addToDP(mapImage);
	}

	// Obtiene objeto del servicio
	public void getVenue(int id){

		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
			@Override
			public void onFailure(Throwable caught) {
				processing = false;
				showErrorMessage(this.getClass().getName() + "-getVenue() ERROR: " + caught.toString());
			}

			@Override
			public void onSuccess(BmObject result) {
				processing = false;
				setBmObject(result);
				populateDetail();
			}
		};

		// Llamada al servicio RPC
		try {
			if (!processing) {
				processing = true;
				getUiParams().getBmObjectServiceAsync().get(getBmObject().getPmClass(), id, callback);
			}
		} catch (SFException e) {
			showErrorMessage(this.getClass().getName() + "-getVenue() ERROR: " + e.toString());
		}
	}

}
