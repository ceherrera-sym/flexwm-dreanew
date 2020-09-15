/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client;


import com.flexwm.client.ac.UiSessionCalendar;
import com.flexwm.client.ac.UiSessionDiscipline;
import com.flexwm.client.ac.UiSessionAttendance;
import com.flexwm.client.ac.UiProgramSession;
import com.flexwm.client.ac.UiProgramSessionLevel;
import com.flexwm.client.ac.UiSession;
import com.flexwm.client.ac.UiSessionSale;
import com.flexwm.client.ac.UiSessionTypeExtra;
import com.flexwm.client.ar.UiPropertyRental;
import com.flexwm.client.ar.UiPropertyRentalDetail;
import com.flexwm.client.ac.UiSessionType;
import com.flexwm.client.cm.UiAssignCoordinator;
import com.flexwm.client.cm.UiCategoryForecast;
import com.flexwm.client.cm.UiCompetition;
import com.flexwm.client.cm.UiConsultingService;
import com.flexwm.client.cm.UiCustomer;
import com.flexwm.client.cm.UiCustomerContactList;
import com.flexwm.client.cm.UiDelegation;
import com.flexwm.client.cm.UiExternalSales;
import com.flexwm.client.cm.UiIndustry;
import com.flexwm.client.cm.UiLoseMotive;
import com.flexwm.client.cm.UiMaritalStatus;
import com.flexwm.client.cm.UiMarket;
import com.flexwm.client.cm.UiNationality;
import com.flexwm.client.cm.UiOpportunityCalendar;
import com.flexwm.client.cm.UiOpportunityDetail;
import com.flexwm.client.cm.UiPayCondition;
import com.flexwm.client.cm.UiProject;
import com.flexwm.client.cm.UiOpportunity;
import com.flexwm.client.cm.UiProjectCalendar;
import com.flexwm.client.cm.UiProjectDetail;
//import com.flexwm.client.cm.UiProjectStep;
import com.flexwm.client.cm.UiQuoteList;
//import com.flexwm.client.cm.UiRFQU;
//import com.flexwm.client.cm.UiRFQU;
import com.flexwm.client.cm.UiRateType;
import com.flexwm.client.cm.UiReferral;
import com.flexwm.client.cm.UiRegion;
import com.flexwm.client.cm.UiTerritory;
import com.flexwm.client.co.UiConceptGroup;
import com.flexwm.client.co.UiConceptHeading;
import com.flexwm.client.co.UiContractTerm;
import com.flexwm.client.co.UiDevelopmentBlock;
import com.flexwm.client.co.UiDevelopment;
import com.flexwm.client.co.UiDevelopmentPhase;
import com.flexwm.client.co.UiDevelopmentPhaseDetail;
import com.flexwm.client.co.UiDevelopmentType;
import com.flexwm.client.co.UiEstimationItem;
import com.flexwm.client.co.UiFSRSList;
import com.flexwm.client.co.UiProperty;
import com.flexwm.client.co.UiPropertyModelExtra;
import com.flexwm.client.co.UiPropertyModel;
import com.flexwm.client.co.UiPropertySale;
import com.flexwm.client.co.UiPropertyTypeList;
import com.flexwm.client.co.UiUnitPrice;
import com.flexwm.client.co.UiWorkContract;
import com.flexwm.client.co.UiWork;
import com.flexwm.client.co.UiWorkType;
import com.flexwm.client.cr.UiCredit;
import com.flexwm.client.cr.UiCreditTypeList;
import com.flexwm.client.cr.UiUserCreditLimitList;
import com.flexwm.client.cv.UiActivity;
import com.flexwm.client.cv.UiMeeting;
import com.flexwm.client.cv.UiPosition;
import com.flexwm.client.cv.UiCourseProgram;
import com.flexwm.client.cv.UiSkill;
import com.flexwm.client.cv.UiTrainingSession;
import com.flexwm.client.dash.UiComercialProjectsDash;
import com.flexwm.client.dash.UiConstructionDash;
import com.flexwm.client.dash.UiConsultancyDash;
import com.flexwm.client.dash.UiConsultancyOperationsDash;
import com.flexwm.client.dash.UiConsultancySaleDash;
import com.flexwm.client.dash.UiFinanceConsultancyDash;
import com.flexwm.client.dash.UiFinanceDash;
import com.flexwm.client.dash.UiLoanDash;
import com.flexwm.client.dash.UiOperationsDash;
import com.flexwm.client.dash.UiOrderDetailDash;
import com.flexwm.client.dash.UiOrderSaleDash;
import com.flexwm.client.dash.UiPermitDash;
import com.flexwm.client.dash.UiProjectsOperationsDash;
import com.flexwm.client.dash.UiPropertySaleDash;
import com.flexwm.client.ev.UiVenue;
import com.flexwm.client.fi.UiBankAccount;
import com.flexwm.client.fi.UiBankAccountType;
import com.flexwm.client.fi.UiBankMovType;
import com.flexwm.client.fi.UiBankMovement;
import com.flexwm.client.fi.UiBudgetItemType;
import com.flexwm.client.fi.UiCFDI;
import com.flexwm.client.fi.UiBudget;
import com.flexwm.client.fi.UiCommission;
import com.flexwm.client.fi.UiCurrency;
import com.flexwm.client.fi.UiFactorType;
import com.flexwm.client.fi.UiFiscalPeriod;
import com.flexwm.client.fi.UiInvoiceList;
import com.flexwm.client.fi.UiLoan;
import com.flexwm.client.fi.UiPaccount;
import com.flexwm.client.fi.UiPaccountType;
import com.flexwm.client.fi.UiPayMethod;
import com.flexwm.client.fi.UiPaymentType;
import com.flexwm.client.fi.UiRaccount;
import com.flexwm.client.fi.UiRaccountType;
import com.flexwm.client.fi.UiRateOrFee;
import com.flexwm.client.fi.UiTax;
import com.flexwm.client.in.UiCoverageList;
import com.flexwm.client.in.UiDiscountList;
import com.flexwm.client.in.UiFlexInsuranceStart;
import com.flexwm.client.in.UiFundList;
import com.flexwm.client.in.UiGoalList;
import com.flexwm.client.in.UiInsuranceCategoryList;
import com.flexwm.client.in.UiInsuranceList;
import com.flexwm.client.in.UiPolicyList;
import com.flexwm.client.in.UiValuableList;
import com.flexwm.client.op.UiEquipment;
import com.flexwm.client.op.UiEquipmentType;
import com.flexwm.client.op.UiOrderBlockDateList;
import com.flexwm.client.op.UiOrderCommission;
import com.flexwm.client.op.UiConsultancy;
import com.flexwm.client.op.UiConsultancyDetail;
import com.flexwm.client.op.UiCustomerService;
import com.flexwm.client.op.UiCustomerServiceType;
import com.flexwm.client.op.UiOrderDelivery;
import com.flexwm.client.op.UiOrderDetail;
import com.flexwm.client.op.UiOrderList;
import com.flexwm.client.op.UiOrderType;
import com.flexwm.client.op.UiProductFamily;
import com.flexwm.client.op.UiProductGroup;
import com.flexwm.client.op.UiProductKit;
import com.flexwm.client.op.UiProduct;
import com.flexwm.client.op.UiReqPayType;
import com.flexwm.client.op.UiRequisition;
import com.flexwm.client.op.UiRequisitionReceipt;
import com.flexwm.client.op.UiRequisitionType;
//import com.flexwm.client.op.UiServiceOrder;
import com.flexwm.client.op.UiSupplierCategory;
import com.flexwm.client.op.UiSupplier;
import com.flexwm.client.op.UiUnit;
import com.flexwm.client.op.UiWarehouse;
import com.flexwm.client.op.UiWhBox;
import com.flexwm.client.op.UiWhMovement;
import com.flexwm.client.op.UiWhSection;
import com.flexwm.client.op.UiWhStock;
import com.flexwm.client.op.UiWhTrack;
import com.flexwm.client.rpt.UiADRPReports;
import com.flexwm.client.rpt.UiBankMovementReport;
import com.flexwm.client.rpt.UiCORPReports;
import com.flexwm.client.rpt.UiCRMReports;
import com.flexwm.client.rpt.UiCreditDaCreditoReport;
import com.flexwm.client.rpt.UiCreditReport;
import com.flexwm.client.rpt.UiCustomerDaCreditoReport;
import com.flexwm.client.rpt.UiGERPReports;
import com.flexwm.client.rpt.UiOPRPReports;
import com.flexwm.client.rpt.UiSYSTReports;
import com.flexwm.client.rpt.UiSessionReport;
import com.flexwm.client.rpt.UiVIRPReports;
import com.flexwm.client.rpt.UiWARPReports;
import com.flexwm.shared.BmoCompanyNomenclature;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.BmoSendReport;
import com.flexwm.shared.ac.BmoProgramSession;
import com.flexwm.shared.ac.BmoProgramSessionLevel;
import com.flexwm.shared.ac.BmoSession;
import com.flexwm.shared.ac.BmoSessionDiscipline;
import com.flexwm.shared.ac.BmoSessionSale;
import com.flexwm.shared.ac.BmoSessionType;
import com.flexwm.shared.ac.BmoSessionTypeExtra;
import com.flexwm.shared.ar.BmoPropertyRental;
import com.flexwm.shared.cm.BmoAssignCoordinator;
import com.flexwm.shared.cm.BmoCategoryForecast;
import com.flexwm.shared.cm.BmoConsultingService;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoDelegation;
import com.flexwm.shared.cm.BmoExternalSales;
import com.flexwm.shared.cm.BmoIndustry;
import com.flexwm.shared.cm.BmoLoseMotive;
import com.flexwm.shared.cm.BmoMaritalStatus;
import com.flexwm.shared.cm.BmoMarket;
import com.flexwm.shared.cm.BmoNationality;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.cm.BmoPayCondition;
import com.flexwm.shared.cm.BmoProject;
//import com.flexwm.shared.cm.BmoProjectStep;
import com.flexwm.shared.cm.BmoQuote;
import com.flexwm.shared.cm.BmoRateType;
import com.flexwm.shared.cm.BmoReferral;
import com.flexwm.shared.cm.BmoRegion;
import com.flexwm.shared.cm.BmoTerritory;
import com.flexwm.shared.co.BmoConceptGroup;
import com.flexwm.shared.co.BmoConceptHeading;
import com.flexwm.shared.co.BmoContractTerm;
import com.flexwm.shared.co.BmoDevelopment;
import com.flexwm.shared.co.BmoDevelopmentBlock;
import com.flexwm.shared.co.BmoDevelopmentPhase;
import com.flexwm.shared.co.BmoDevelopmentType;
import com.flexwm.shared.co.BmoEstimationItem;
import com.flexwm.shared.co.BmoFSRS;
import com.flexwm.shared.co.BmoProperty;
import com.flexwm.shared.co.BmoPropertyModel;
import com.flexwm.shared.co.BmoPropertyModelExtra;
import com.flexwm.shared.co.BmoPropertySale;
import com.flexwm.shared.co.BmoPropertyType;
import com.flexwm.shared.co.BmoUnitPrice;
import com.flexwm.shared.co.BmoWork;
import com.flexwm.shared.co.BmoWorkContract;
import com.flexwm.shared.co.BmoWorkType;
import com.flexwm.shared.cr.BmoCredit;
import com.flexwm.shared.cr.BmoCreditType;
import com.flexwm.shared.cr.BmoUserCreditLimit;
import com.flexwm.shared.cv.BmoActivity;
import com.flexwm.shared.cv.BmoCourseProgram;
import com.flexwm.shared.cv.BmoMeeting;
import com.flexwm.shared.cv.BmoPosition;
import com.flexwm.shared.cv.BmoSkill;
import com.flexwm.shared.cv.BmoTrainingSession;
import com.flexwm.shared.ev.BmoVenue;
import com.flexwm.shared.fi.BmoBankAccount;
import com.flexwm.shared.fi.BmoBankAccountType;
import com.flexwm.shared.fi.BmoBankMovType;
import com.flexwm.shared.fi.BmoBankMovement;
import com.flexwm.shared.fi.BmoBudget;
import com.flexwm.shared.fi.BmoBudgetItemType;
import com.flexwm.shared.fi.BmoCFDI;
import com.flexwm.shared.fi.BmoCommission;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.fi.BmoFactorType;
import com.flexwm.shared.fi.BmoFiscalPeriod;
import com.flexwm.shared.fi.BmoInvoice;
import com.flexwm.shared.fi.BmoLoan;
import com.flexwm.shared.fi.BmoPaccount;
import com.flexwm.shared.fi.BmoPaccountType;
import com.flexwm.shared.fi.BmoPayMethod;
import com.flexwm.shared.fi.BmoPaymentType;
import com.flexwm.shared.fi.BmoRaccount;
import com.flexwm.shared.fi.BmoRaccountType;
import com.flexwm.shared.fi.BmoRateOrFee;
import com.flexwm.shared.fi.BmoTax;
import com.flexwm.shared.in.BmoCoverage;
import com.flexwm.shared.in.BmoDiscount;
import com.flexwm.shared.in.BmoFund;
import com.flexwm.shared.in.BmoGoal;
import com.flexwm.shared.in.BmoInsurance;
import com.flexwm.shared.in.BmoInsuranceCategory;
import com.flexwm.shared.in.BmoPolicy;
import com.flexwm.shared.in.BmoValuable;
import com.flexwm.shared.op.BmoEquipment;
import com.flexwm.shared.op.BmoEquipmentType;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderBlockDate;
import com.flexwm.shared.op.BmoOrderCommission;
import com.flexwm.shared.op.BmoConsultancy;
import com.flexwm.shared.op.BmoCustomerService;
import com.flexwm.shared.op.BmoCustomerServiceType;
import com.flexwm.shared.op.BmoOrderDelivery;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoProductFamily;
import com.flexwm.shared.op.BmoProductGroup;
import com.flexwm.shared.op.BmoProductKit;
import com.flexwm.shared.op.BmoReqPayType;
import com.flexwm.shared.op.BmoRequisition;
import com.flexwm.shared.op.BmoRequisitionReceipt;
import com.flexwm.shared.op.BmoRequisitionType;
//import com.flexwm.shared.op.BmoServiceOrder;
import com.flexwm.shared.op.BmoSupplier;
import com.flexwm.shared.op.BmoSupplierCategory;
import com.flexwm.shared.op.BmoUnit;
import com.flexwm.shared.op.BmoWarehouse;
import com.flexwm.shared.op.BmoWhBox;
import com.flexwm.shared.op.BmoWhMovement;
import com.flexwm.shared.op.BmoWhSection;
import com.flexwm.shared.op.BmoWhStock;
import com.flexwm.shared.op.BmoWhTrack;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;
import com.symgae.client.rpt.UiSqlFlexList;
import com.symgae.client.sf.UiArea;
import com.symgae.client.sf.UiCity;
import com.symgae.client.sf.UiColor;
import com.symgae.client.sf.UiCompany;
import com.symgae.client.sf.UiCountry;
import com.symgae.client.sf.UiCronJob;
import com.symgae.client.sf.UiCronLog;
import com.symgae.client.sf.UiEmail;
import com.symgae.client.sf.UiFileType;
import com.symgae.client.sf.UiProfile;
import com.symgae.client.sf.UiProgram;
import com.symgae.client.sf.UiProgramProfile;
import com.symgae.client.sf.UiLocation;
import com.symgae.client.sf.UiMenu;
import com.symgae.client.sf.UiOccupation;
import com.symgae.client.sf.UiSFConfigForm;
import com.symgae.client.sf.UiSFLog;
import com.symgae.client.sf.UiSocial;
import com.symgae.client.sf.UiState;
import com.symgae.client.sf.UiTag;
import com.symgae.client.sf.UiTitle;
import com.symgae.client.sf.UiUser;
import com.symgae.client.sf.UiUserTimeClock;
import com.symgae.client.ui.UiProgramFactory;
import com.symgae.client.ui.UiParams;
import com.flexwm.client.wf.UiWFlowAction;
import com.flexwm.client.wf.UiWFlowCategory;
import com.flexwm.client.wf.UiWFlowDocument;
import com.flexwm.client.wf.UiWFlowFunnel;
import com.flexwm.client.wf.UiWFlowLog;
import com.flexwm.client.wf.UiWFlow;
import com.flexwm.client.wf.UiWFlowStep;
import com.flexwm.client.wf.UiWFlowTimeTrack;
import com.flexwm.client.wf.UiWFlowType;
import com.flexwm.client.wf.UiWFlowUserBlockDate;
import com.flexwm.client.wf.UiWFlowUserCalendar;
import com.flexwm.client.wf.UiWFlowUserSelect;
import com.flexwm.client.wf.UiWFlowValidation;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.sf.BmoArea;
import com.symgae.shared.sf.BmoCity;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoCountry;
import com.symgae.shared.sf.BmoCronJob;
import com.symgae.shared.sf.BmoCronLog;
import com.symgae.shared.sf.BmoEmail;
import com.symgae.shared.sf.BmoFileType;
import com.symgae.shared.sf.BmoProfile;
import com.symgae.shared.sf.BmoLocation;
import com.symgae.shared.sf.BmoMenu;
import com.symgae.shared.sf.BmoProgram;
import com.symgae.shared.sf.BmoProgramProfile;
import com.symgae.shared.sf.BmoOccupation;
import com.symgae.shared.sf.BmoSFConfig;
import com.symgae.shared.sf.BmoSFLog;
import com.symgae.shared.sf.BmoSocial;
import com.symgae.shared.sf.BmoState;
import com.symgae.shared.sf.BmoTag;
import com.symgae.shared.sf.BmoTitle;
import com.symgae.shared.sf.BmoUiColor;
import com.symgae.shared.sf.BmoUser;
import com.symgae.shared.sf.BmoUserTimeClock;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowAction;
import com.flexwm.shared.wf.BmoWFlowCategory;
import com.flexwm.shared.wf.BmoWFlowDocument;
import com.flexwm.shared.wf.BmoWFlowFunnel;
import com.flexwm.shared.wf.BmoWFlowLog;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.flexwm.shared.wf.BmoWFlowTimeTrack;
import com.flexwm.shared.wf.BmoWFlowType;
import com.flexwm.shared.wf.BmoWFlowUser;
import com.flexwm.shared.wf.BmoWFlowUserBlockDate;
import com.flexwm.shared.wf.BmoWFlowUserSelect;
import com.flexwm.shared.wf.BmoWFlowValidation;


public class UiFlexwmProgramFactory extends UiProgramFactory {
	private int foreignId;

	public UiFlexwmProgramFactory(UiParams uiParams) {
		super(uiParams);
	}

	@Override
	public void showProgram(String programCode) {
		// Cualquier modulo llamado desde el menu se establece como MASTER
		getUiParams().setUiType(programCode, UiParams.MASTER);

		// Establece el program code de llamada para Dashboards
		getUiParams().setCallerProgramCode(programCode);

		// Modulos CRM
		if (programCode.equals(new BmoReferral().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiReferral(getUiParams()).show();        
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}  
		//		else if (programCode.equals(new BmoProjectStep().getProgramCode())) {
		//			GWT.runAsync(new RunAsyncCallback() {
		//				@Override 
		//				public void onSuccess() {
		//					new UiProjectStep(getUiParams()).show();      
		//				}
		//
		//				@Override
		//				public void onFailure(Throwable reason) {
		//					errorMessage(reason);
		//				}
		//			});
		//		}
		else if (programCode.equals(new BmoCompanyNomenclature().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiCompanyNomenclature(getUiParams()).show();      
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if(programCode.equals(new BmoExternalSales().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {

				@Override
				public void onSuccess() {
					new UiExternalSales(getUiParams()).show();
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);					
				}
			});
		}
		else if (programCode.equals(new BmoTerritory().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiTerritory(getUiParams()).show();        
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}

		else if (programCode.equals(new BmoMarket().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiMarket(getUiParams()).show();        
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals(new BmoConsultingService().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiConsultingService(getUiParams()).show();        
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}  
		else if (programCode.equals(new BmoRegion().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiRegion(getUiParams()).show();        
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals(new BmoNationality().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiNationality(getUiParams()).show();        
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals(new BmoCustomer().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiCustomer(getUiParams()).show();           
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoPayCondition().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiPayCondition(getUiParams()).show();        
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals(new BmoProject().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiProject(getUiParams()).show();             
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoOrderDelivery().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiOrderDelivery(getUiParams()).show();             
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoVenue().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiVenue(getUiParams()).show();        
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoSocial().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiSocial(getUiParams()).show();   
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoQuote().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiQuoteList(getUiParams()).show(); 
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoOpportunity().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiOpportunity(getUiParams()).show();
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoLoseMotive().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiLoseMotive(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoMaritalStatus().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiMaritalStatus(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 

		else if (programCode.equals(new BmoIndustry().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiIndustry(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoRateType().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiRateType(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals(new BmoDelegation().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiDelegation(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}

		else if (programCode.equals(new BmoCategoryForecast().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiCategoryForecast(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals(new BmoAssignCoordinator().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiAssignCoordinator(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}

		// Modulos Construccion
		else if (programCode.equals(new BmoDevelopmentPhase().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiDevelopmentPhase(getUiParams()).show();      
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals(new BmoWork().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiWork(getUiParams()).show();      
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals(new BmoWorkType().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiWorkType(getUiParams()).show();      
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals(new BmoFSRS().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiFSRSList(getUiParams()).show();      
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals(new BmoWorkContract().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiWorkContract(getUiParams()).show();      
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals(new BmoBudget().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiBudget(getUiParams()).show();      
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals(new BmoUnitPrice().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiUnitPrice(getUiParams()).show();      
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals(new BmoEstimationItem().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiEstimationItem(getUiParams()).show();      
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals(new BmoConceptGroup().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiConceptGroup(getUiParams()).show();      
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals(new BmoConceptHeading().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiConceptHeading(getUiParams()).show();      
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals(new BmoPropertyModel().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiPropertyModel(getUiParams()).show();      
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals(new BmoPropertyModelExtra().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiPropertyModelExtra(getUiParams()).show();      
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals(new BmoDevelopment().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiDevelopment(getUiParams()).show();      
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals(new BmoDevelopmentType().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiDevelopmentType(getUiParams()).show();      
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		//		else if (programCode.equals(new BmoRFQU().getProgramCode())) {
		//			GWT.runAsync(new RunAsyncCallback() {
		//				@Override 
		//				public void onSuccess() {
		//					new UiRFQU(getUiParams()).show();
		//				}
		//
		//				@Override
		//				public void onFailure(Throwable reason) {
		//					errorMessage(reason);
		//				}
		//			});
		//		} 
		else if (programCode.equals(new BmoDevelopmentBlock().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiDevelopmentBlock(getUiParams()).show();      
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals(new BmoPropertyType().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiPropertyTypeList(getUiParams()).show();      
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}

		else if (programCode.equals(new BmoContractTerm().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiContractTerm(getUiParams()).show();   
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}

		// Modulos Operaciones
		else if (programCode.equals(new BmoProductFamily().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiProductFamily(getUiParams()).show();      
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals(new BmoProduct().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiProduct(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoProductGroup().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiProductGroup(getUiParams()).show();   
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoProductKit().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiProductKit(getUiParams()).show();   
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoCompany().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiCompany(getUiParams()).show();    
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoRequisition().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiRequisition(getUiParams()).show();      
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoRequisitionReceipt().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiRequisitionReceipt(getUiParams()).show();      
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoWarehouse().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiWarehouse(getUiParams()).show();      
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoSupplier().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiSupplier(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoSupplierCategory().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiSupplierCategory(getUiParams()).show();    
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoEquipment().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiEquipment(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoEquipmentType().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiEquipmentType(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoWhMovement().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiWhMovement(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoWhStock().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiWhStock(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoWhBox().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiWhBox(getUiParams()).show(); 
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoWhTrack().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiWhTrack(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals(new BmoOrder().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiOrderList(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoConsultancy().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiConsultancy(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals(new BmoWhSection().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiWhSection(getUiParams()).show();      
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoOrderType().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiOrderType(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoOrderBlockDate().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiOrderBlockDateList(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoOrderCommission().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiOrderCommission(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 

		else if (programCode.equals(new BmoCustomerServiceType().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiCustomerServiceType(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 

		else if (programCode.equals(new BmoCustomerService().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiCustomerService(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}

		// Modulos Finanzas
		else if (programCode.equals(new BmoPaccountType().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiPaccountType(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}  
		else if (programCode.equals(new BmoBankAccount().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiBankAccount(getUiParams()).show();    
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoBankMovement().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiBankMovement(getUiParams()).show();      
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 		
		else if (programCode.equals(new BmoInvoice().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiInvoiceList(getUiParams()).show();      
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals(new BmoBankMovType().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiBankMovType(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoPaccount().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiPaccount(getUiParams()).show();    
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}	 
		else if (programCode.equals(new BmoReqPayType().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiReqPayType(getUiParams()).show();   
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoPayMethod().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiPayMethod(getUiParams()).show();   
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoCFDI().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiCFDI(getUiParams()).show();   
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoBankAccountType().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiBankAccountType(getUiParams()).show();   
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoRequisitionType().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiRequisitionType(getUiParams()).show();   
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoRaccount().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiRaccount(getUiParams()).show();    
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}		
		else if (programCode.equals(new BmoRaccountType().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiRaccountType(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoLoan().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiLoan(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}  
		else if (programCode.equals(new BmoInvoice().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiInvoiceList(getUiParams()).show();    
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoCurrency().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiCurrency(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoBudgetItemType().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiBudgetItemType(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals(new BmoPaymentType().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiPaymentType(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals(new BmoCommission().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiCommission(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals(new BmoFiscalPeriod().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiFiscalPeriod(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}

		// Modulos de CV
		else if (programCode.equals(new BmoPropertySale().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiPropertySale(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		// Modulos de CV
		else if (programCode.equals(new BmoProperty().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiProperty(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 

		// Modulos de Insurance
		else if (programCode.equals(new BmoGoal().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiGoalList(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoDiscount().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiDiscountList(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoFund().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiFundList(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoValuable().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiValuableList(getUiParams()).show();  
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoCoverage().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiCoverageList(getUiParams()).show();
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoPolicy().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiPolicyList(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoInsurance().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiInsuranceList(getUiParams()).show();    
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoInsuranceCategory().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiInsuranceCategoryList(getUiParams()).show();    
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 

		// Modulos HR
		else if (programCode.equals(new BmoCourseProgram().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiCourseProgram(getUiParams()).show();    
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 

		else if (programCode.equals(new BmoMeeting().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiMeeting(getUiParams()).show();    
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 

		else if (programCode.equals(new BmoActivity().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiActivity(getUiParams()).show();  
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 

		else if (programCode.equals(new BmoWFlowStep().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiWFlowStep(getUiParams()).show();    
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 

		else if (programCode.equals(new BmoWFlowTimeTrack().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiWFlowTimeTrack(getUiParams()).show();    
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 

		else if (programCode.equals(new BmoWFlowDocument().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiWFlowDocument(getUiParams()).show();    
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 

		else if (programCode.equals(new BmoWFlowLog().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiWFlowLog(getUiParams()).show();    
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 

		else if (programCode.equals(new BmoPosition().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiPosition(getUiParams()).show();    
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 

		else if (programCode.equals(new BmoTrainingSession().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiTrainingSession(getUiParams()).show();    
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 

		else if (programCode.equals(new BmoSkill().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiSkill(getUiParams()).show();    
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals("GERP")) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiGERPReports(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}

		// Modulos SYMGF
		else if (programCode.equals(new BmoArea().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiArea(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoLocation().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiLocation(getUiParams()).show();      
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoCourseProgram().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiCourseProgram(getUiParams()).show();  
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoMenu().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiMenu(getUiParams()).show();  
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoProfile().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiProfile(getUiParams()).show();  
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoProgram().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiProgram(getUiParams()).show();  
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoProgramProfile().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiProgramProfile(getUiParams()).show();  
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoUser().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiUser(getUiParams()).show();      
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoUserTimeClock().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiUserTimeClock(getUiParams()).show();      
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoCity().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiCity(getUiParams()).show();       
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoUnit().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiUnit(getUiParams()).show();    
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoState().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiState(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoCountry().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiCountry(getUiParams()).show();    
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoFlexConfig().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiFlexConfigForm(getUiParams()).show();      
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoCronJob().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiCronJob(getUiParams()).show();      
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoCronLog().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiCronLog(getUiParams()).show();      
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoSFConfig().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiSFConfigForm(getUiParams(), 0).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoSendReport().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiSendReport(getUiParams()).show();      
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoTitle().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiTitle(getUiParams()).show();   
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoOccupation().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiOccupation(getUiParams()).show();      
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoEmail().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiEmail(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoFileType().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiFileType(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoTag().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiTag(getUiParams()).show();              
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoUiColor().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiColor(getUiParams()).show();              
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoSFLog().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiSFLog(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 

		else if (programCode.equals("SYST")) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiSYSTReports(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}


		// Modulos WFLOW
		else if (programCode.equals(new BmoWFlowType().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiWFlowType(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoWFlowCategory().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiWFlowCategory(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoWFlowValidation().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiWFlowValidation(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoWFlowAction().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiWFlowAction(getUiParams()).show();              
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoWFlowUserSelect().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiWFlowUserSelect(getUiParams()).show();              
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoWFlowUserBlockDate().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiWFlowUserBlockDate(getUiParams()).show();              
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(UiWFlowUserCalendar.UIMODULECODE)) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					getUiParams().setUiType(new BmoWFlowUser().getProgramCode(), UiParams.MASTER);
					new UiWFlowUserCalendar(getUiParams()).show();      
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}  
		else if (programCode.equals(new BmoWFlow().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiWFlow(getUiParams()).show();              
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoWFlowFunnel().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiWFlowFunnel(getUiParams()).show();              
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 


		// Modulos ACADEMIAS
		else if (programCode.equals(new BmoSessionDiscipline().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiSessionDiscipline(getUiParams()).show();              
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoSessionType().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiSessionType(getUiParams()).show();              
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoSessionTypeExtra().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiSessionTypeExtra(getUiParams()).show();
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoSession().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiSession(getUiParams()).show();              
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoSessionSale().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiSessionSale(getUiParams()).show();              
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals(new BmoProgramSession().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiProgramSession(getUiParams()).show();              
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}

		else if (programCode.equals(new BmoProgramSessionLevel().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiProgramSessionLevel(getUiParams()).show();              
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals(new BmoUserCreditLimit().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiUserCreditLimitList(getUiParams()).show();              
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals(new BmoCreditType().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiCreditTypeList(getUiParams()).show();              
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals(new BmoCredit().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiCredit(getUiParams()).show();              
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals("SECA")) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiSessionCalendar(getUiParams()).show();                
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		// SRO - Order de Servicio
		//		else if (programCode.equals(new BmoServiceOrder().getProgramCode())) {
		//			GWT.runAsync(new RunAsyncCallback() {
		//				@Override 
		//				public void onSuccess() {
		//					new UiServiceOrder(getUiParams()).show();                
		//				}
		//
		//				@Override
		//				public void onFailure(Throwable reason) {
		//					errorMessage(reason);
		//				}
		//			});
		//		} 

		// Modulos varios
		else if (programCode.equals("CRRP")) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiCRMReports(getUiParams()).show();                
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 


		else if (programCode.equals("RSQL")) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiSqlFlexList(getUiParams());             
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals("BMRP")) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiBankMovementReport(getUiParams()); 
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals("PRCA")) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					getUiParams().setUiType(new BmoProject().getProgramCode(), UiParams.MASTER);
					new UiProjectCalendar(getUiParams()).show();      
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}  
		else if (programCode.equals("OPCA")) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					getUiParams().setUiType(new BmoOpportunity().getProgramCode(), UiParams.MASTER);
					new UiOpportunityCalendar(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals("CRMS")) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					UiComercialProjectsDash uiEosCRMStart = new UiComercialProjectsDash(getUiParams());
					uiEosCRMStart.show();	     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals("CRMO")) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					UiOrderSaleDash uiOrderSaleDash = new UiOrderSaleDash(getUiParams());
					uiOrderSaleDash.show();	     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		// Para consultoria
		else if (programCode.equals("CRMC")) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					UiConsultancySaleDash uiConsultancySaleDash = new UiConsultancySaleDash(getUiParams());
					uiConsultancySaleDash.show();	     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}


		else if (programCode.equals("CMPT")) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					UiCompetition uiCompetition = new UiCompetition(getUiParams());
					uiCompetition.show();	     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals("SCRM")) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					UiOrderDetailDash uiOrderDetailDash = new UiOrderDetailDash(getUiParams());
					uiOrderDetailDash.show();	     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals("SCR2")) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					UiConsultancyDash uiServiceDash = new UiConsultancyDash(getUiParams());
					uiServiceDash.show();	     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals("COPS")) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					UiPropertySaleDash uiPropertySaleDashboard = new UiPropertySaleDash(getUiParams());
					uiPropertySaleDashboard.show();	     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals("DVDS")) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					UiConstructionDash uiConstructionDash = new UiConstructionDash(getUiParams());
					uiConstructionDash.show();	     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals("COTR")) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					UiPermitDash uiPermitDash = new UiPermitDash(getUiParams());
					uiPermitDash.show();	     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals("PJDA")) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					UiProjectsOperationsDash uiProjectsDash = new UiProjectsOperationsDash(getUiParams());
					uiProjectsDash.show();	     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals("OPDA")) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					UiOperationsDash uiOperationsDash = new UiOperationsDash(getUiParams());
					uiOperationsDash.show();	     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals("OPDC")) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					UiConsultancyOperationsDash uiConsultancyOperationsDash = new UiConsultancyOperationsDash(getUiParams());
					uiConsultancyOperationsDash.show();	     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals("OPSS")) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					UiProjectsOperationsDash uiProjectsOperationsDash = new UiProjectsOperationsDash(getUiParams());
					uiProjectsOperationsDash.show();	     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		// Para Consultoria
		else if (programCode.equals("FIMC")) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					UiFinanceConsultancyDash uiFinanceConsultancyDash = new UiFinanceConsultancyDash(getUiParams());
					uiFinanceConsultancyDash.show();	     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals("FIMS")) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					UiFinanceDash uiFinanceDash = new UiFinanceDash(getUiParams());
					uiFinanceDash.show();	     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals("FIFO")) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					UiLoanDash uiLoanDash = new UiLoanDash(getUiParams());
					uiLoanDash.show();	     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals("INSS")) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					UiFlexInsuranceStart uiEosInsuranceStart = new UiFlexInsuranceStart(getUiParams());
					uiEosInsuranceStart.show();	    
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals("ADRP")) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiADRPReports(getUiParams()).show();     
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}

		else if (programCode.equals("OPRP")) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiOPRPReports(getUiParams()).show();      
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}

		else if (programCode.equals("WARP")) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiWARPReports(getUiParams()).show();      
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}

		else if (programCode.equals("VIRP")) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiVIRPReports(getUiParams()).show();                
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 

		else if (programCode.equals("CORP")) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiCORPReports(getUiParams()).show();                
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}	
		else if (programCode.equals("RPSE")) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiSessionReport(getUiParams()).show();                
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		// Reportes de creditos(cobi)
		else if (programCode.equals("RPCR")) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiCreditReport(getUiParams()); 
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});	    
		}
		// Reportes daCredito de clientes
		else if (programCode.equals("RPCC")) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiCustomerDaCreditoReport(getUiParams()); 
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});	    
		}
		// Reportes daCredito de creditos
		else if (programCode.equals("RPCD")) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiCreditDaCreditoReport(getUiParams()); 
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});	    
		}


		else if (programCode.equals("CUAT")) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiSessionAttendance(getUiParams()).show(); 
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});	    
		}

		//Modulo Arrendamiento
		else if (programCode.equals(new BmoPropertyRental().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiPropertyRental(getUiParams()).show();
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});	    
		}

		//Modulo Arrendamiento
		else if (programCode.equals("PORT")) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					Window.open(GwtUtil.getProperUrl(getUiParams().getSFParams(), "portal/portal_start.jsp"), "", "");
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});	    
		}
		else if (programCode.equals("CPPR")) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {

					new UiCopyProfile(getUiParams()).show();	
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});	    
		}
		//Reportes drea
		else if (programCode.equals("ODDR")) {
			GWT.runAsync(new RunAsyncCallback() {				
				@Override
				public void onSuccess() {					

				}				
				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);					
				}
			});
		}
		//tipo de factor
		else if (programCode.equals(new BmoFactorType().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiFactorType(getUiParams()).show();  
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoTax().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiTax(getUiParams()).show();  
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoRateOrFee().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiRateOrFee(getUiParams()).show();
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals("CUCL")) {
			GWT.runAsync(new RunAsyncCallback() {				
				@Override
				public void onSuccess() {					
					new UiCustomerContactList(getUiParams()).show();
				}				
				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);					
				}
			});
		}
		else {
			Window.alert("El Modulo " + programCode + " no esta habilidado en " + this.getClass().getName() + "-openProgram()");
		}
	}

	public void showProgram(String programCode, int id) {
		this.foreignId = id;

		if (programCode.equalsIgnoreCase(new BmoProject().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					UiProjectDetail uiProjectDetail = new UiProjectDetail(getUiParams(), foreignId);
					uiProjectDetail.show();	    
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoOpportunity().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					UiOpportunityDetail uiOpportunityDetail = new UiOpportunityDetail(getUiParams(), foreignId);
					uiOpportunityDetail.show();	    
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoRequisition().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					UiRequisition uiRequisition = new UiRequisition(getUiParams());
					uiRequisition.show();
					uiRequisition.edit(foreignId);	    
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else if (programCode.equals(new BmoActivity().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					new UiActivity(getUiParams()).show();  
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 		
		////////////////////////////////
		//Modulo Arrendamiento
		else if (programCode.equals(new BmoPropertyRental().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					UiPropertyRentalDetail uiPropertyRentalDetail = new UiPropertyRentalDetail(getUiParams(), foreignId);
					uiPropertyRentalDetail.show();         
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		/////////////////////////////////////
		else if (programCode.equals(new BmoOrder().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					UiOrderDetail uiOrderDetail = new UiOrderDetail(getUiParams(), foreignId);
					uiOrderDetail.show();
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals(new BmoConsultancy().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					UiConsultancyDetail uiConsultancyDetail = new UiConsultancyDetail(getUiParams(), foreignId);
					uiConsultancyDetail.show();
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals(new BmoRaccount().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					UiRaccount uiRaccount = new UiRaccount(getUiParams());
					uiRaccount.show();
					uiRaccount.edit(foreignId);
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		}
		else if (programCode.equals(new BmoDevelopmentPhase().getProgramCode())) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override 
				public void onSuccess() {
					UiDevelopmentPhaseDetail uiDevelopmentPhaseDetail = new UiDevelopmentPhaseDetail(getUiParams(), foreignId);
					uiDevelopmentPhaseDetail.show();
				}

				@Override
				public void onFailure(Throwable reason) {
					errorMessage(reason);
				}
			});
		} 
		else {
			Window.alert("El Programa " + programCode + " no esta habilidado en apertura directa desde liga: " + this.getClass().getName() + "-openProgram()");
		}
	}

	// Muestra mensaje en caso de existir error al recuperar codigo de un modulo
	public void errorMessage(Throwable reason) {
		if (Window.confirm("Al parecer el Sistema fue Actualizado, desea recargar la Aplicación?"))
			Window.Location.reload();
		else
			Window.alert(this.getClass().getName() + "-openProgram(): " + reason.toString());
	}
}
