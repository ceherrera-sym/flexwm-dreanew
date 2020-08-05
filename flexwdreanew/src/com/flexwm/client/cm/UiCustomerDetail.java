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

import com.flexwm.client.ac.UiSessionSale;
import com.flexwm.client.ar.UiPropertyRental;
import com.flexwm.client.co.UiPropertySale;
import com.flexwm.client.cr.UiCredit;
import com.flexwm.client.fi.UiRaccount;
import com.flexwm.client.op.UiConsultancy;
import com.flexwm.client.op.UiOrderList;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.ac.BmoSessionSale;
import com.flexwm.shared.ar.BmoPropertyRental;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.cm.BmoProject;
import com.flexwm.shared.co.BmoPropertySale;
import com.flexwm.shared.cr.BmoCredit;
import com.flexwm.shared.fi.BmoRaccount;
import com.flexwm.shared.op.BmoConsultancy;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoReqPayType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.symgae.client.ui.UiDetail;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiTagBox;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.flexwm.shared.wf.BmoWFlow;


public class UiCustomerDetail extends UiDetail {

	protected BmoCustomer bmoCustomer;
	protected Label codeLabel = new Label();
	protected Label nameLabel = new Label();
	protected Label descriptionLabel = new Label();
	protected Button startButton = new Button("Inicio");
	protected Button editButton = new Button("Editar Proyecto");

	BmoWFlow bmoWFlow = new BmoWFlow();
	BmoReqPayType bmoReqPayType = new BmoReqPayType();

	public UiCustomerDetail(UiParams uiParams, int id) {
		super(uiParams, new BmoCustomer(), id);
		bmoCustomer = (BmoCustomer)getBmObject();
		startButton.setStyleName("formSaveButton");
		editButton.setStyleName("formCloseButton");		
	}

	@Override
	public void populateLabels(){
		bmoCustomer = (BmoCustomer)getBmObject();
		addDetailImage(bmoCustomer.getLogo());
		addTitleLabel(bmoCustomer.getCode());
		addLabel(bmoCustomer.getDisplayName());
		if (bmoCustomer.getCustomertype().equals(BmoCustomer.TYPE_COMPANY)) {
			addLabel(bmoCustomer.getLegalname());
			addLabel(bmoCustomer.getRfc());
		}
		addLabel(bmoCustomer.getCustomertype());
		if( bmoCustomer.getReqPayTypeId().toInteger() > 0)
			getBmoReqPayType();
		else
			getInfo();
	}

	public void getInfo(){
		if(!(bmoCustomer.getReqPayTypeId().toInteger() > 0))
			addLabel(bmoCustomer.getReqPayTypeId());
		addLabel(bmoCustomer.getPhone());
		addLabel(bmoCustomer.getMobile());
		addLabel(bmoCustomer.getEmail());
		addLabel(bmoCustomer.getStatus());
		// Tags
		UiTagBox uiTagBox = new UiTagBox(getUiParams());
		VerticalPanel tagPanel = new VerticalPanel();
		uiTagBox.showTagDetail(tagPanel, bmoCustomer.getTags());
		addDetailPanel(tagPanel);	
		addEmptyLabel();
		addTitleLabel("Vendedor:");
		addLabel(bmoCustomer.getBmoUser().getCode());
		addLabel(bmoCustomer.getBmoUser().getPhone());
		addLabel(bmoCustomer.getBmoUser().getEmail());
		addEmptyLabel();

		// Inicio
		addActionLabel("Inicio", "start", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				reset();
			}
		});

		// Editar forma
		addActionLabel("Editar", bmoCustomer.getProgramCode(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showCustomerForm();
			}
		});

		// Oportunidades
		addActionLabel(new BmoOpportunity(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showUiOpportunityList();
			}
		});

		// Proyectos
		addActionLabel(new BmoProject(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showUiProjectList();
			}
		});

		// Venta Inmuebles
		addActionLabel(new BmoPropertySale(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showUiPropertySaleList();
			}
		});

		// Creditos
		addActionLabel(new BmoCredit(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showUiCreditList();
			}
		});

		// Ventas de sesion
		addActionLabel(new BmoSessionSale(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showUiSessionSaleList();
			}
		});

		// Arrendamientos-Contratos
		addActionLabel(new BmoPropertyRental(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showUiPropertyRentalList();
			}
		});

		// Ventas
		if (getUiParams().getSFParams().hasRead(new BmoConsultancy().getProgramCode())) {
			addActionLabel(new BmoConsultancy(), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					showUiConsultancyList();
				}
			});
		}

		// Pedidos
		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getShowOrderInCustomer().toBoolean()) {
			if (getUiParams().getSFParams().hasRead(new BmoOrder().getProgramCode())) {
				addActionLabel(new BmoOrder(), new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						showUiOrderList();
					}
				});
			}
		}

		// Cuentas x Cobrar
		if (getUiParams().getSFParams().hasRead(new BmoRaccount().getProgramCode())) {
			addActionLabel(new BmoRaccount(), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					showUiRaccountList();
				}
			});
		}	

		// Si otro modulo hizo la llamada
		if (getUiParams().getCallerProgramCode().equalsIgnoreCase(getBmObject().getProgramCode())) {
			addActionLabel("Regresar", "close", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					close();
				}
			});
		} else {
			addActionLabel("Regresar", "start", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					getUiParams().getUiProgramFactory().showProgram(getUiParams().getCallerProgramCode());
				}
			});

			addActionLabel("Ir a Listado", "close", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					close();
				}
			});
		}

		// Si otro modulo hizo la llamada
		if (!getUiParams().getCallerProgramCode().equalsIgnoreCase(getBmObject().getProgramCode())) {
			addActionLabel("Ir a Tablero", "start", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					getUiParams().getUiProgramFactory().showProgram(getUiParams().getCallerProgramCode());
				}
			});
		}

		// Panel default inicial
		showStart();
	}

	public void getBmoReqPayType() {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {}
				else showErrorMessage(this.getClass().getName() + "-getBmoReqPayType() ERROR: " + caught.toString());
				getInfo();
			}

			public void onSuccess(BmObject result) {
				stopLoading();				
				bmoReqPayType = (BmoReqPayType )result;
				addLabel(bmoReqPayType.getName());				
				getInfo();
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().get(bmoReqPayType.getPmClass(), bmoCustomer.getReqPayTypeId().toInteger(), callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-getBmoReqPayType ERROR: " + e.toString());
		}
	}

	@Override
	public void close() {
		UiCustomer uiCustomerList = new UiCustomer(getUiParams());
		setUiType(UiParams.MASTER);
		uiCustomerList.show();
	}

	private void reset() {
		UiCustomerDetail uiCustomerDetail = new UiCustomerDetail(getUiParams(), bmoCustomer.getId());
		uiCustomerDetail.show();
	}

	private void showStart() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiCustomerStart uiCustomerStart = new UiCustomerStart(getUiParams(), bmObjectProgramId, bmoCustomer);
		uiCustomerStart.show();
	}

	private void showCustomerForm() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiCustomer uiCustomer = new UiCustomer(getUiParams());
		uiCustomer.edit(bmoCustomer);
	}

	private void showUiOpportunityList() {
		getUiParams().getUiTemplate().hideEastPanel();
		BmoOpportunity bmoOpportunity = new BmoOpportunity();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoOpportunity.getKind(), bmoOpportunity.getCustomerId(), bmoCustomer.getId()); 
		getUiParams().setForceFilter(bmoOpportunity.getProgramCode(), bmFilter);
		setUiType(bmoOpportunity.getProgramCode(), UiParams.SLAVE);
		UiOpportunity uiOpportunityList = new UiOpportunity(getUiParams());		
		uiOpportunityList.show();
	}

	private void showUiProjectList() {
		getUiParams().getUiTemplate().hideEastPanel();
		BmoProject bmoProject = new BmoProject();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoProject.getKind(), bmoProject.getCustomerId(), bmoCustomer.getId()); 
		getUiParams().setForceFilter(bmoProject.getProgramCode(), bmFilter);
		setUiType(bmoProject.getProgramCode(), UiParams.SLAVE);
		UiProject uiProjectList = new UiProject(getUiParams());		
		uiProjectList.show();
	}

	private void showUiPropertySaleList() {
		getUiParams().getUiTemplate().hideEastPanel();
		BmoPropertySale bmoPropertySale = new BmoPropertySale();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoPropertySale.getKind(), bmoPropertySale.getCustomerId(), bmoCustomer.getId()); 
		getUiParams().setForceFilter(bmoPropertySale.getProgramCode(), bmFilter);
		setUiType(bmoPropertySale.getProgramCode(), UiParams.SLAVE);
		UiPropertySale uiPropertySaleList = new UiPropertySale(getUiParams());		
		uiPropertySaleList.show();
	}

	private void showUiOrderList() {
		getUiParams().getUiTemplate().hideEastPanel();
		BmoOrder bmoOrder = new BmoOrder();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoOrder.getKind(), bmoOrder.getCustomerId(), bmoCustomer.getId()); 
		getUiParams().setForceFilter(bmoOrder.getProgramCode(), bmFilter);
		setUiType(bmoOrder.getProgramCode(), UiParams.SLAVE);
		UiOrderList uiOrderList = new UiOrderList(getUiParams());		
		uiOrderList.show();
	}

	private void showUiConsultancyList() {
		getUiParams().getUiTemplate().hideEastPanel();
		BmoConsultancy bmoConsultancy = new BmoConsultancy();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoConsultancy.getKind(), bmoConsultancy.getCustomerId(), bmoCustomer.getId()); 
		getUiParams().setForceFilter(bmoConsultancy.getProgramCode(), bmFilter);
		setUiType(bmoConsultancy.getProgramCode(), UiParams.SLAVE);
		UiConsultancy uiConsultancy = new UiConsultancy(getUiParams());		
		uiConsultancy.show();
	}

	private void showUiCreditList() {
		getUiParams().getUiTemplate().hideEastPanel();
		BmoCredit bmoCredit = new BmoCredit();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoCredit.getKind(), bmoCredit.getCustomerId(), bmoCustomer.getId()); 
		getUiParams().setForceFilter(bmoCredit.getProgramCode(), bmFilter);
		setUiType(bmoCredit.getProgramCode(), UiParams.SLAVE);
		UiCredit uiCredit = new UiCredit(getUiParams());		
		uiCredit.show();
	}

	private void showUiSessionSaleList() {
		getUiParams().getUiTemplate().hideEastPanel();
		BmoSessionSale bmoSessionSale = new BmoSessionSale();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoSessionSale.getKind(), bmoSessionSale.getCustomerId(), bmoCustomer.getId()); 
		getUiParams().setForceFilter(bmoSessionSale.getProgramCode(), bmFilter);
		setUiType(bmoSessionSale.getProgramCode(), UiParams.SLAVE);
		UiSessionSale uiSessionSale = new UiSessionSale(getUiParams());		
		uiSessionSale.show();
	}

	private void showUiPropertyRentalList() {
		getUiParams().getUiTemplate().hideEastPanel();
		BmoPropertyRental bmoPropertyRental = new BmoPropertyRental();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoPropertyRental.getKind(), bmoPropertyRental.getCustomerId(), bmoCustomer.getId()); 
		getUiParams().setForceFilter(bmoPropertyRental.getProgramCode(), bmFilter);
		setUiType(bmoPropertyRental.getProgramCode(), UiParams.SLAVE);
		UiPropertyRental uiPropertyRental = new UiPropertyRental(getUiParams());		
		uiPropertyRental.show();
	}

	private void showUiRaccountList() {
		getUiParams().getUiTemplate().hideEastPanel();
		BmoRaccount bmoRaccount = new BmoRaccount();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getCustomerId(), bmoCustomer.getId()); 
		getUiParams().setForceFilter(bmoRaccount.getProgramCode(), bmFilter);
		setUiType(bmoRaccount.getProgramCode(), UiParams.SLAVE);
		UiRaccount uiRaccountList = new UiRaccount(getUiParams());		
		uiRaccountList.show();
	}

}
