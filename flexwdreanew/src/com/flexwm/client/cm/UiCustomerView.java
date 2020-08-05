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
import java.util.Iterator;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoCustomerEmail;
import com.flexwm.shared.cm.BmoCustomerPhone;
import com.flexwm.shared.cm.BmoCustomerSocial;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;


public class UiCustomerView extends Ui {
	private boolean processing = false;
	private int id = 0;
	private BmoCustomer bmoCustomer;
	private int projectId;

	public UiCustomerView(UiParams uiParams, Panel defaultPanel, int id, int projectId) {
		super(uiParams, new BmoCustomer(), defaultPanel);
		this.id = id;
		this.projectId = projectId;
	}

	public void show() {
		getCustomer(id);
	}

	public void populateDetail(){
		bmoCustomer = (BmoCustomer)getBmObject();

		if (getUiParams().getSFParams().hasRead(new BmoCustomerPhone().getProgramCode()))
			getPhoneList();
	}

	public void populatePhoneList(ArrayList<BmObject> phoneList){
		Iterator<BmObject> phoneIterator = phoneList.iterator();

		BmoCustomerPhone bmoCustomerPhoneMain = new BmoCustomerPhone();

		// Telefono principal del cliente
		if (!bmoCustomer.getPhone().toString().equals("")) {
			HorizontalPanel hp0 = new HorizontalPanel();

			String imageUrlPhoneMain = "";
			if (bmoCustomer.getCustomertype().toChar() == BmoCustomer.TYPE_PERSON)
				imageUrlPhoneMain = bmoCustomerPhoneMain.getType().getFieldOptionByCode(BmoCustomerPhone.TYPE_HOME).getImgUrl();
			else
				imageUrlPhoneMain = bmoCustomerPhoneMain.getType().getFieldOptionByCode(BmoCustomerPhone.TYPE_WORK).getImgUrl();

			Image imagePhoneMain = new Image(imageUrlPhoneMain);

			Label phoneMain = new Label(bmoCustomer.getPhone().toString());
			phoneMain.setStyleName("detailText");

			hp0.add(imagePhoneMain);
			hp0.add(new InlineHTML("&nbsp;"));
			hp0.add(phoneMain);
			addToDP(hp0);
		}
		
		// Telefono movil principal del cliente
		if (!bmoCustomer.getMobile().toString().equals("")) {
			HorizontalPanel hp1 = new HorizontalPanel();

			String imageUrlMobileMain = bmoCustomerPhoneMain.getType().getFieldOptionByCode(BmoCustomerPhone.TYPE_MOBILE).getImgUrl();
			Image imageMobileMain = new Image(imageUrlMobileMain);

			Label mobileMain = new Label(bmoCustomer.getMobile().toString());
			mobileMain.setStyleName("detailText");

			hp1.add(imageMobileMain);
			hp1.add(new InlineHTML("&nbsp;"));
			hp1.add(mobileMain);
			addToDP(hp1);
		}
		// Lista de telefonos del catalogo de Tels. del cliente
		while (phoneIterator.hasNext()) {
			BmoCustomerPhone bmoCustomerPhone = (BmoCustomerPhone)phoneIterator.next();
			HorizontalPanel hp = new HorizontalPanel();

			// Imagen
			String imageUrl = bmoCustomerPhone.getType().getFieldOptionByCode(bmoCustomerPhone.getType().toChar()).getImgUrl();
			Image phoneImage = new Image(imageUrl);

			Label phone = new Label(bmoCustomerPhone.getNumber().toString());
			phone.setStyleName("detailText");

			hp.add(phoneImage);
			hp.add(new InlineHTML("&nbsp;"));
			hp.add(phone);

			addToDP(hp);			
		}

		if (getUiParams().getSFParams().hasRead(new BmoCustomerEmail().getProgramCode()))
			getEmailList();
	}

	public void populateEmailList(ArrayList<BmObject> emailList){
		Iterator<BmObject> emailIterator = emailList.iterator();

		// Email principal del cliente
		if (!bmoCustomer.getEmail().toString().equals("")) {
			HorizontalPanel hp0 = new HorizontalPanel();
			BmoCustomerEmail bmoCustomerEmailMain = new BmoCustomerEmail();
			
			String imageUrlEmailMain = "";
			if (bmoCustomer.getCustomertype().toChar() == BmoCustomer.TYPE_PERSON)
				imageUrlEmailMain = bmoCustomerEmailMain.getType().getFieldOptionByCode(BmoCustomerEmail.TYPE_PERSONAL).getImgUrl();
			else
				imageUrlEmailMain = bmoCustomerEmailMain.getType().getFieldOptionByCode(BmoCustomerEmail.TYPE_WORK).getImgUrl();
	
			Image imageEmailMain = new Image(imageUrlEmailMain);
	
			Anchor emailMain = new Anchor("" + bmoCustomer.getEmail().toString(), 
					"mailto:" + bmoCustomer.getEmail().toString() + 
					"?subject=#FLEXP" + projectId + " ", "_blank");
			emailMain.setStyleName("detailText");
	
			hp0.add(imageEmailMain);
			hp0.add(new InlineHTML("&nbsp;"));
			hp0.add(emailMain);
			addToDP(hp0);
		}
		// Lista de emails del catalogo de Emails del cliente
		while (emailIterator.hasNext()) {
			BmoCustomerEmail bmoCustomerEmail = (BmoCustomerEmail)emailIterator.next();
			HorizontalPanel hp = new HorizontalPanel();

			// Imagen
			String imageUrl = bmoCustomerEmail.getType().getFieldOptionByCode(bmoCustomerEmail.getType().toChar()).getImgUrl();
			Image emailImage = new Image(imageUrl);

			Anchor email = new Anchor("" + bmoCustomerEmail.getEmail().toString(), 
					"mailto:" + bmoCustomerEmail.getEmail().toString() + 
					"?subject=#FLEXP" + projectId + " ", "_blank");
			email.setStyleName("detailText");

			hp.add(emailImage);
			hp.add(new InlineHTML("&nbsp;"));
			hp.add(email);

			addToDP(hp);			
		}

		if (getUiParams().getSFParams().hasRead(new BmoCustomerSocial().getProgramCode()))
			getSocialList();
	}

	public void populateSocialList(ArrayList<BmObject> emailList){
		Iterator<BmObject> emailIterator = emailList.iterator();
		while (emailIterator.hasNext()) {
			BmoCustomerSocial bmoCustomerSocial = (BmoCustomerSocial)emailIterator.next();
			HorizontalPanel hp = new HorizontalPanel();

			// Imagen
			Image socialImage = new Image(bmoCustomerSocial.getBmoSocial().getIcon().toString());

			Anchor social = new Anchor("/" + bmoCustomerSocial.getAccount().toString(), 
					"" + bmoCustomerSocial.getBmoSocial().getLink().toString() + 
					"/" + bmoCustomerSocial.getAccount().toString(), 
					"_blank");
			social.setStyleName("detailText");

			hp.add(socialImage);
			hp.add(new InlineHTML("&nbsp;"));
			hp.add(social);
			addToDP(hp);	
		}
	}

	// Obtiene objeto del servicio
	public void getCustomer(int id){

		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
			public void onFailure(Throwable caught) {
				processing = false;
				showErrorMessage(this.getClass().getName() + "-getCustomer() ERROR: " + caught.toString());
			}

			public void onSuccess(BmObject result) {
				processing = false;
				setBmObject((BmObject)result);
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
			showErrorMessage(this.getClass().getName() + "-getCustomer() ERROR: " + e.toString());
		}
	}

	public void getPhoneList(){
		// Set up the callback object.
		AsyncCallback<ArrayList<BmObject>> callback = new AsyncCallback<ArrayList<BmObject>>() {
			public void onFailure(Throwable caught) {
				processing = false;
				showErrorMessage(this.getClass().getName() + "-getPhoneList() ERROR: " + caught.toString());	
			}
			public void onSuccess(ArrayList<BmObject> result) {
				processing = false;
				populatePhoneList(result);
			}
		};
		try {
			if (!processing) {
				processing = true;
				BmoCustomerPhone bmoCustomerPhone = new BmoCustomerPhone();
				BmFilter bmFilter = new BmFilter();
				bmFilter.setValueFilter(bmoCustomerPhone.getKind(), bmoCustomerPhone.getCustomerId(), id);
				getUiParams().getBmObjectServiceAsync().list(bmoCustomerPhone.getPmClass(), bmFilter, callback);
			}
		} catch (SFException e) {
			showErrorMessage(this.getClass().getName() + "-getPhoneList() ERROR: " + e.toString());
		}
	}

	public void getEmailList(){
		// Set up the callback object.
		AsyncCallback<ArrayList<BmObject>> callback = new AsyncCallback<ArrayList<BmObject>>() {
			public void onFailure(Throwable caught) {
				processing = false;
				showErrorMessage(this.getClass().getName() + "-getEmailList() ERROR: " + caught.toString());	
			}
			public void onSuccess(ArrayList<BmObject> result) {
				processing = false;
				populateEmailList(result);
			}
		};
		try {
			if (!processing) {
				processing = true;
				BmoCustomerEmail bmoCustomerEmail = new BmoCustomerEmail();
				BmFilter bmFilter = new BmFilter();
				bmFilter.setValueFilter(bmoCustomerEmail.getKind(), bmoCustomerEmail.getCustomerId(), id);
				getUiParams().getBmObjectServiceAsync().list(bmoCustomerEmail.getPmClass(), bmFilter, callback);
			}
		} catch (SFException e) {
			showErrorMessage(this.getClass().getName() + "-getEmailList() ERROR: " + e.toString());
		}
	}

	public void getSocialList(){
		// Set up the callback object.
		AsyncCallback<ArrayList<BmObject>> callback = new AsyncCallback<ArrayList<BmObject>>() {
			public void onFailure(Throwable caught) {
				processing = false;
				showErrorMessage(this.getClass().getName() + "-getSociaList() ERROR: " + caught.toString());	
			}
			public void onSuccess(ArrayList<BmObject> result) {
				processing = false;
				populateSocialList(result);
			}
		};
		try {
			if (!processing) {
				processing = true;
				BmoCustomerSocial bmoCustomerSocial = new BmoCustomerSocial();
				BmFilter bmFilter = new BmFilter();
				bmFilter.setValueFilter(bmoCustomerSocial.getKind(), bmoCustomerSocial.getCustomerId(), id);
				getUiParams().getBmObjectServiceAsync().list(bmoCustomerSocial.getPmClass(), bmFilter, callback);
			}
		} catch (SFException e) {
			showErrorMessage(this.getClass().getName() + "-getSocialList() ERROR: " + e.toString());
		}
	}
}
