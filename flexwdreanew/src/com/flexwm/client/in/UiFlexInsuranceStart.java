package com.flexwm.client.in;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.symgae.client.chart.UiChartBar;
import com.symgae.client.ui.UiBmFieldClickHandler;
import com.symgae.client.ui.UiDashboard;
import com.symgae.client.ui.UiImageGallery;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiTemplate;
import com.symgae.shared.GwtUtil;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.flexwm.client.UiTwitterFeed;
import com.flexwm.client.cm.UiBirthdayView;
import com.flexwm.client.cm.UiCustomer;
import com.flexwm.client.cm.UiOpportunity;
import com.flexwm.client.cm.UiOpportunityActiveList;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.in.BmoPolicy;
import com.flexwm.shared.in.BmoPolicyPayment;


public class UiFlexInsuranceStart extends UiDashboard {
	private FlexTable flexTable = new FlexTable();
	protected DecoratorPanel labelDecoratorPanel = new DecoratorPanel();
	protected FlowPanel labelPanel = new FlowPanel();
	protected FlowPanel actionPanel = new FlowPanel();

	public UiFlexInsuranceStart(UiParams uiParams){
		super(uiParams, "Tablero Principal", GwtUtil.getProperUrl(uiParams.getSFParams(), "crm.png"));
		getUiParams().getUiTemplate().hideProgramButtonPanel();

		flexTable.setSize("97%", "100%");
	}

	@Override
	public void populate() {
		clearDP();

		// Galeria de imagenes
		HorizontalPanel phrasePanel = new HorizontalPanel();
		phrasePanel.setSize("95%", "35px");
		phrasePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		Image image = new Image();
		image.setHeight("35px");
		UiImageGallery uiPhraseGallery = new UiImageGallery(getUiParams(), "FRAS", image);
		uiPhraseGallery.show();
		phrasePanel.add(image);
		getUiParams().getUiTemplate().showProgramExtrasPanel();
		getUiParams().getUiTemplate().getProgramExtrasPanel().add(phrasePanel);
		
		// Oportunidades activos
		FlowPanel fp1 = new FlowPanel();
		fp1.setSize("100%", "100%");
		ScrollPanel sp1 = new ScrollPanel();
		sp1.setSize("95%", "250px");
		sp1.addStyleName("detailStart");
		sp1.add(fp1);
		UiOpportunityActiveList uiOpportunityActiveList = new UiOpportunityActiveList(getUiParams(), fp1);
		getUiParams().setUiType(new BmoOpportunity().getProgramCode(), UiParams.MINIMALIST);
		uiOpportunityActiveList.show();
		flexTable.setWidget(1, 0, sp1);
		
		// Cumpleaños clienes		
		FlowPanel fp2 = new FlowPanel();
		fp2.setSize("100%", "100%");
		ScrollPanel sp2 = new ScrollPanel();
		sp2.setSize("95%", "250px");
		sp2.addStyleName("detailStart");
		sp2.add(fp2);
		
		UiBirthdayView uiBirthdayView = new UiBirthdayView(getUiParams(), fp2);
		uiBirthdayView.show();
		flexTable.setWidget(1, 1, sp2);
		
		// Tareas pendientes
		FlowPanel fp3 = new FlowPanel();
		fp3.setSize("100%", "100%");
		ScrollPanel sp3 = new ScrollPanel();
		sp3.setSize("98%", "400px");
		sp3.addStyleName("detailStart");
		sp3.add(fp3);
		UiInsuranceStepPendingList uiInsuranceStepPendingList = new UiInsuranceStepPendingList(getUiParams(), fp3);
		getUiParams().setUiType(new BmoWFlowStep().getProgramCode(), UiParams.MINIMALIST);
		uiInsuranceStepPendingList.show();
		flexTable.setWidget(3, 0, sp3);
		flexTable.getFlexCellFormatter().setColSpan(3, 0, 2);
		
		// Pagos de polizas		
		FlowPanel paymentFlowPanel = new FlowPanel();
		paymentFlowPanel.setSize("100%", "100%");
		ScrollPanel paymentScrollPanel = new ScrollPanel();
		paymentScrollPanel.setSize("95%", "250px");
		paymentScrollPanel.addStyleName("detailStart");
		paymentScrollPanel.add(paymentFlowPanel);
		UiPolicyPaymentList uiPolicyPaymentList = new UiPolicyPaymentList(getUiParams(), paymentFlowPanel);
		setUiType(new BmoPolicyPayment().getProgramCode(), UiParams.MINIMALIST);
		uiPolicyPaymentList.show();
		flexTable.setWidget(4, 0, paymentScrollPanel);
		flexTable.getFlexCellFormatter().setColSpan(4, 0, 2);
		
		// Videos
		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getYtVideoId().toString().length() > 0) {
			Label videoLabel = new Label("Canal YouTube");
			videoLabel.setStyleName("programSubtitle");
			Frame frame = new Frame("http://www.youtube.com/embed/" + ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getYtVideoId().toString());
			frame.setStyleName("detailStart");
			frame.setSize("95%", "250px");
			flexTable.setWidget(5, 0, frame);
			flexTable.getCellFormatter().setStyleName(3, 0, "listCellChar");
		}
		
		// Galeria de imagenes
		Image imageG = new Image();
		imageG.setSize("100%", "250px");
		UiImageGallery uiImageGallery = new UiImageGallery(getUiParams(), "IMGB", imageG);
		uiImageGallery.show();
		flexTable.setWidget(5, 1, imageG);
		flexTable.getCellFormatter().setStyleName(3, 1, "listCellChar");
		
		
		// Iconos
		HorizontalPanel iconPanel = new HorizontalPanel();
		iconPanel.setSize("95%", "100px");
		iconPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		Image img1 = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/res/planem/mdrt.png"));
		img1.addStyleName("bottomImage");
		img1.addClickHandler(new UiBmFieldClickHandler("") {
			@Override
			public void onClick(ClickEvent event) {
				Window.open("http://www.mdrt.org", "_blank", "");
			}
		});
		iconPanel.add(img1);
		
		Image img2 = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/res/planem/lutc.png"));
		img2.addStyleName("bottomImage");
		img2.addClickHandler(new UiBmFieldClickHandler("") {
			@Override
			public void onClick(ClickEvent event) {
				Window.open("http://www.imesfac.com.mx/", "_blank", "");
			}
		});
		iconPanel.add(img2);
		
		Image img3 = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/res/planem/american.png"));
		img3.addStyleName("bottomImage");
		img3.addClickHandler(new UiBmFieldClickHandler("") {
			@Override
			public void onClick(ClickEvent event) {
				Window.open("http://www.theamericancollege.edu/insurance-education/lutcf-insurance-skills", "_blank", "");
			}
		});
		iconPanel.add(img3);
		
		Image img4 = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/res/planem/smty.png"));
		img4.addStyleName("bottomImage");
		img4.addClickHandler(new UiBmFieldClickHandler("") {
			@Override
			public void onClick(ClickEvent event) {
				Window.open("http://www.lineamonterrey.com.mx", "_blank", "");
			}
		});
		iconPanel.add(img4);

		flexTable.setWidget(7, 0, iconPanel);
		flexTable.getFlexCellFormatter().setColSpan(7, 0, 2);

		addToDP(flexTable);
		
		setEastPanel();
		setWestPanel();
	}

	private void setEastPanel() {
		getUiParams().getUiTemplate().getEastPanel().add(new HTML("<pre> </pre>"));
		
		// Gráfica de oportunnidades por tipo
		FlowPanel oppoChartFP = new FlowPanel();
		oppoChartFP.setWidth((UiTemplate.EASTSIZE - 10) + "px");
		UiChartBar uiOppoChart = new UiChartBar(getUiParams(), oppoChartFP, "Oportunidades por Categoría", 
				" select wfca_code as Tipo, count(wflw_wflowid) as Oportunidades from opportunities " +
						" left join wflows on (oppo_wflowid = wflw_wflowid) " +
						" left join wflowtypes on (wflw_wflowtypeid = wfty_wflowtypeid) " +
						" left join wflowcategories on (wfty_wflowcategoryid = wfca_wflowcategoryid) "
						+ " GROUP BY wfca_name ");
		uiOppoChart.setSize(UiTemplate.EASTSIZE - 10, 160);
		uiOppoChart.show();
		DecoratorPanel oppoChartDP = new DecoratorPanel();
		oppoChartDP.addStyleName("detailPanel");
		oppoChartDP.setWidth((UiTemplate.EASTSIZE - 10) + "px");
		oppoChartDP.setWidget(oppoChartFP);
		getUiParams().getUiTemplate().getEastPanel().add(oppoChartDP);
		
		//Mostrar datos en la barra lateral derecha
		if (!((BmoFlexConfig)getSFParams().getBmoAppConfig()).getSysMessage().toString().equals("")) {
			labelPanel.setWidth((UiTemplate.EASTSIZE - 10) + "px");
			labelDecoratorPanel.setWidget(labelPanel);
			labelDecoratorPanel.addStyleName("detailPanel");

			getUiParams().getUiTemplate().getEastPanel().add(new HTML("<pre> </pre>"));
			getUiParams().getUiTemplate().getEastPanel().add(labelDecoratorPanel);
			labelPanel.add(detailLabel("Mensaje sistema:"));
			labelPanel.add(detailText(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getSysMessage().toString()));
		}
		getUiParams().getUiTemplate().getEastPanel().add(new HTML("&nbsp;"));
		
		// Mostrar tweets
		FlowPanel twitterPanel = new FlowPanel();
		twitterPanel.setWidth((UiTemplate.EASTSIZE - 10) + "px");
//		twitterPanel.setStyleName("detailStart");
		UiTwitterFeed uiTwitterFeed = new UiTwitterFeed();
		twitterPanel.add(uiTwitterFeed.getTwitterPanel());
		getUiParams().getUiTemplate().getEastPanel().add(twitterPanel);
		
		getUiParams().getUiTemplate().getEastPanel().add(new HTML("&nbsp;"));
		
		// Iconos sociales
		HorizontalPanel iconPanel = new HorizontalPanel();
		iconPanel.setWidth("95%");
		iconPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		Image fbImage = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/fb29.png"));
		fbImage.addStyleName("bottomImage");
		fbImage.addClickHandler(new UiBmFieldClickHandler("") {
			@Override
			public void onClick(ClickEvent event) {
				Window.open("http://www.facebook.com/planem", "_blank", "");
			}
		});
		
		Image twitterImage = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/twitter29.png"));
		twitterImage.addStyleName("bottomImage");
		twitterImage.addClickHandler(new UiBmFieldClickHandler("") {
			@Override
			public void onClick(ClickEvent event) {
				Window.open("http://www.twitter.com/planem4u", "_blank", "");
			}
		});
		
		Image linkedInImage = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/linkedin29.png"));
		linkedInImage.addStyleName("bottomImage");
		linkedInImage.addClickHandler(new UiBmFieldClickHandler("") {
			@Override
			public void onClick(ClickEvent event) {
				Window.open("http://www.linkedin.com/planem", "_blank", "");
			}
		});
		
		Image googlePlusImage = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/googleplus29.png"));
		googlePlusImage.addStyleName("bottomImage");
		googlePlusImage.addClickHandler(new UiBmFieldClickHandler("") {
			@Override
			public void onClick(ClickEvent event) {
				Window.open("http://www.googleplus.com/planem", "_blank", "");
			}
		});
		
		Image mnylImage = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/googleplus29.png"));
		mnylImage.addStyleName("bottomImage");
		mnylImage.addClickHandler(new UiBmFieldClickHandler("") {
			@Override
			public void onClick(ClickEvent event) {
				Window.open("http://www.googleplus.com/planem", "_blank", "");
			}
		});
		
		
		iconPanel.add(fbImage);
		iconPanel.add(twitterImage);
		iconPanel.add(linkedInImage);
		iconPanel.add(googlePlusImage);
		getUiParams().getUiTemplate().getEastPanel().add(iconPanel);
		getUiParams().getUiTemplate().getEastPanel().add(new HTML("&nbsp;"));
		
		// Iconos otros
		HorizontalPanel otherPanel = new HorizontalPanel();
		otherPanel.setWidth("95%");
		otherPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		Image solucionImage = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/res/planem/solucionline.png"));
		solucionImage.addStyleName("bottomImage");
		solucionImage.addClickHandler(new UiBmFieldClickHandler("") {
			@Override
			public void onClick(ClickEvent event) {
				Window.open("https://www.solucionlinemonterrey.mx/CotizadorWebApp/Forms/Firma.aspx", "_blank", "");
			}
		});
		
		otherPanel.add(solucionImage);
		getUiParams().getUiTemplate().getEastPanel().add(otherPanel);
		getUiParams().getUiTemplate().getEastPanel().add(new HTML("&nbsp;"));
	}

	private void setWestPanel() {
		actionPanel.setWidth(UiTemplate.WESTSIZE + "px");
		getUiParams().getUiTemplate().getWestPanel().add(new HTML("<pre> </pre>"));
		getUiParams().getUiTemplate().getWestPanel().add(actionPanel);
		
		// Creación de prospecto		
		westTable.addActionLabel("+ Cliente", new BmoCustomer().getProgramCode(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				UiCustomer uiCustomer = new UiCustomer(getUiParams());
				uiCustomer.create();
			}
		});
		
		// Crear oportunidad
		westTable.addActionLabel("+ Oportunidad", "oppo", new ClickHandler() {
			public void onClick(ClickEvent event) {
				UiOpportunity uiOpportunity = new UiOpportunity(getUiParams());
				uiOpportunity.create();
			}
		});
		
		// Creación de Póliza
		westTable.addActionLabel("+ Póliza", new BmoPolicy().getProgramCode(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				UiPolicyForm uiPolicyForm = new UiPolicyForm(getUiParams(), 0);
				setUiType(new BmoPolicy().getProgramCode(), UiParams.MASTER);
				uiPolicyForm.show();
			}
		});
	}

	public HTML detailLabel(String label){
		HTML labelHtml = new HTML(label);
		labelHtml.setStyleName("detailLabel");
		return labelHtml;
	}

	public HTML detailText(String text){
		HTML labelHtml = new HTML(text);
		labelHtml.setStyleName("detailText");
		return labelHtml;
	}
}
