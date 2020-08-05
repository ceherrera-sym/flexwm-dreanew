<%@page import="com.flexwm.shared.BmoSendReport"%>
<%@page import="com.flexwm.shared.BmoFlexConfig"%>
<%@page import="com.flexwm.shared.ac.*"%>
<%@page import="com.flexwm.shared.ar.*"%>
<%@page import="com.flexwm.shared.cm.*"%>
<%@page import="com.flexwm.shared.co.*"%>
<%@page import="com.flexwm.shared.cr.*"%>
<%@page import="com.flexwm.shared.cv.*"%>
<%@page import="com.flexwm.shared.ev.*"%>
<%@page import="com.flexwm.shared.fi.*"%>
<%@page import="com.flexwm.shared.in.*"%>
<%@page import="com.flexwm.shared.op.*"%>
<%@page import="com.flexwm.shared.wf.*"%>
<%@page import="java.io.File"%>
<%@include file="../inc/login_opt.jsp" %>
<%String title = "Informaci&oacute;n de Programas y Campos"; 
  PmConn pmConn = new PmConn(sFParams);
  pmConn.open();
  
  PmConn pmConn2 = new PmConn(sFParams);
  pmConn2.open();
  String sql = "",sql2 = "";%>


<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>">
<link rel="stylesheet" type="text/css" href="../css/flexwm.css">  
<meta charset="UTF-8">
<title><%=title %></title>
</head>
<body>
<table class="report">
	<tr class="">
		
		<td class="reportHeaderCell">Programa</td>
		<td class="reportHeaderCell">ID</td>
		<td class="reportHeaderCell">Label Codigo</td>
		<td class="reportHeaderCell">Forma Local</td>
		<td class="reportHeaderCell">Lista Local</td>
		<td class="reportHeaderCell">Lista Externa</td>
		<td class="reportHeaderCell">Habilitado?</td>
		<td class="reportHeaderCell">Requerido?</td>
		
	</tr>
<%
	// table = new BmoCustomer();	
sql = "SELECT prog_code,prog_name FROM programs";
pmConn.doFetch(sql);
int c = 1;
while(pmConn.next()){
	BmObject table = new BmObject();
	ArrayList<BmField> fields;
	

		if (pmConn.getString("prog_code").equals(new BmoOrderSession().getProgramCode())) {
			table = new BmoOrderSession();
		} else if (pmConn.getString("prog_code").equals(new BmoOrderSessionExtra().getProgramCode())) {
			table = new BmoOrderSessionExtra();
		} else if (pmConn.getString("prog_code").equals(new BmoOrderSessionTypePackage().getProgramCode())) {
			table = new BmoOrderSessionTypePackage();
		} else if (pmConn.getString("prog_code").equals(new BmoProgramSession().getProgramCode())) {
			table = new BmoProgramSession();
		} else if (pmConn.getString("prog_code").equals(new BmoProgramSessionLevel().getProgramCode())) {
			table = new BmoProgramSessionLevel();
		} else if (pmConn.getString("prog_code").equals(new BmoProgramSessionSubLevel().getProgramCode())) {
			table = new BmoProgramSessionSubLevel();
		} else if (pmConn.getString("prog_code").equals(new BmoProgramSessionSubLevelType().getProgramCode())) {
			table = new BmoProgramSessionSubLevelType();
		} else if (pmConn.getString("prog_code").equals(new BmoSession().getProgramCode())) {
			table = new BmoSession();
		} else if (pmConn.getString("prog_code").equals(new BmoSessionDiscipline().getProgramCode())) {
			table = new BmoSessionDiscipline();
		} else if (pmConn.getString("prog_code").equals(new BmoSessionReview().getProgramCode())) {
			table = new BmoSessionReview();
		} else if (pmConn.getString("prog_code").equals(new BmoSessionSale().getProgramCode())) {
			table = new BmoSessionSale();
		} else if (pmConn.getString("prog_code").equals(new BmoSessionType().getProgramCode())) {
			table = new BmoSessionType();
		} else if (pmConn.getString("prog_code").equals(new BmoSessionTypeExtra().getProgramCode())) {
			table = new BmoSessionTypeExtra();
		} else if (pmConn.getString("prog_code").equals(new BmoSessionTypePackage().getProgramCode())) {
			table = new BmoSessionTypePackage();
		} else if (pmConn.getString("prog_code").equals(new BmoPropertyRental().getProgramCode())) {
			table = new BmoPropertyRental();
		} else if (pmConn.getString("prog_code").equals(new BmoCompetition().getProgramCode())) {
			table = new BmoCompetition();
		} else if (pmConn.getString("prog_code").equals(new BmoConsultingService().getProgramCode())) {
			table = new BmoConsultingService();
		} else if (pmConn.getString("prog_code").equals(new BmoCustomer().getProgramCode())) {
			table = new BmoCustomer();
		} else if (pmConn.getString("prog_code").equals(new BmoCustomerAddress().getProgramCode())) {
			table = new BmoCustomerAddress();
		} else if (pmConn.getString("prog_code").equals(new BmoCustomerBankAccount().getProgramCode())) {
			table = new BmoCustomerBankAccount();
		} else if (pmConn.getString("prog_code").equals(new BmoCustomerCompany().getProgramCode())) {
			table = new BmoCustomerCompany();
		} else if (pmConn.getString("prog_code").equals(new BmoCustomerContact().getProgramCode())) {
			table = new BmoCustomerContact();
		} else if (pmConn.getString("prog_code").equals(new BmoCustomerDate().getProgramCode())) {
			table = new BmoCustomerDate();
		} else if (pmConn.getString("prog_code").equals(new BmoCustomerEmail().getProgramCode())) {
			table = new BmoCustomerEmail();
		} else if (pmConn.getString("prog_code").equals(new BmoCustomerNote().getProgramCode())) {
			table = new BmoCustomerNote();
		} else if (pmConn.getString("prog_code").equals(new BmoCustomerPaymentType().getProgramCode())) {
			table = new BmoCustomerPaymentType();
		} else if (pmConn.getString("prog_code").equals(new BmoCustomerPhone().getProgramCode())) {
			table = new BmoCustomerPhone();
		} else if (pmConn.getString("prog_code").equals(new BmoCustomerPoll().getProgramCode())) {
			table = new BmoCustomerPoll();
		} else if (pmConn.getString("prog_code").equals(new BmoCustomerRelative().getProgramCode())) {
			table = new BmoCustomerRelative();
		} else if (pmConn.getString("prog_code").equals(new BmoCustomerSocial().getProgramCode())) {
			table = new BmoCustomerSocial();
		} else if (pmConn.getString("prog_code").equals(new BmoCustomerStatus().getProgramCode())) {
			table = new BmoCustomerStatus();
		} else if (pmConn.getString("prog_code").equals(new BmoCustomerWeb().getProgramCode())) {
			table = new BmoCustomerWeb();
		} else if (pmConn.getString("prog_code").equals(new BmoEstimation().getProgramCode())) {
			table = new BmoEstimation();
		} else if (pmConn.getString("prog_code").equals(new BmoEstimationGroup().getProgramCode())) {
			table = new BmoEstimationGroup();
		} else if (pmConn.getString("prog_code").equals(new BmoEstimationRFQItem().getProgramCode())) {
			table = new BmoEstimationRFQItem();
		} else if (pmConn.getString("prog_code").equals(new BmoExternalSales().getProgramCode())) {
			table = new BmoExternalSales();
		} else if (pmConn.getString("prog_code").equals(new BmoIndustry().getProgramCode())) {
			table = new BmoIndustry();
		} else if (pmConn.getString("prog_code").equals(new BmoLoseMotive().getProgramCode())) {
			table = new BmoLoseMotive();
		} else if (pmConn.getString("prog_code").equals(new BmoMarket().getProgramCode())) {
			table = new BmoMarket();
		} else if (pmConn.getString("prog_code").equals(new BmoOpportunity().getProgramCode())) {
			table = new BmoOpportunity();
		} else if (pmConn.getString("prog_code").equals(new BmoOpportunityCompetition().getProgramCode())) {
			table = new BmoOpportunityCompetition();
		} else if (pmConn.getString("prog_code").equals(new BmoOpportunityDetail().getProgramCode())) {
			table = new BmoOpportunityDetail();
		} else if (pmConn.getString("prog_code").equals(new BmoPayCondition().getProgramCode())) {
			table = new BmoPayCondition();
		} else if (pmConn.getString("prog_code").equals(new BmoProject().getProgramCode())) {
			table = new BmoProject();
		} else if (pmConn.getString("prog_code").equals(new BmoProjectActivities().getProgramCode())) {
			table = new BmoProjectActivities();
		} else if (pmConn.getString("prog_code").equals(new BmoProjectActivitiesDep().getProgramCode())) {
			table = new BmoProjectActivitiesDep();
		} else if (pmConn.getString("prog_code").equals(new BmoProjectDetail().getProgramCode())) {
			table = new BmoProjectDetail();
		} else if (pmConn.getString("prog_code").equals(new BmoProjectGuideline().getProgramCode())) {
			table = new BmoProjectGuideline();
		} else if (pmConn.getString("prog_code").equals(new BmoProjectStep().getProgramCode())) {
			table = new BmoProjectStep();
		} else if (pmConn.getString("prog_code").equals(new BmoQuote().getProgramCode())) {
			table = new BmoQuote();
		} else if (pmConn.getString("prog_code").equals(new BmoQuoteEquipment().getProgramCode())) {
			table = new BmoQuoteEquipment();
		} else if (pmConn.getString("prog_code").equals(new BmoQuoteGroup().getProgramCode())) {
			table = new BmoQuoteGroup();
		} else if (pmConn.getString("prog_code").equals(new BmoQuoteItem().getProgramCode())) {
			table = new BmoQuoteItem();
		} else if (pmConn.getString("prog_code").equals(new BmoQuoteProperty().getProgramCode())) {
			table = new BmoQuoteProperty();
		} else if (pmConn.getString("prog_code").equals(new BmoQuotePropertyModelExtra().getProgramCode())) {
			table = new BmoQuotePropertyModelExtra();
		} else if (pmConn.getString("prog_code").equals(new BmoQuoteStaff().getProgramCode())) {
			table = new BmoQuoteStaff();
		} else if (pmConn.getString("prog_code").equals(new BmoRateType().getProgramCode())) {
			table = new BmoRateType();
		} else if (pmConn.getString("prog_code").equals(new BmoReferral().getProgramCode())) {
			table = new BmoReferral();
		} else if (pmConn.getString("prog_code").equals(new BmoRegion().getProgramCode())) {
			table = new BmoRegion();
		} else if (pmConn.getString("prog_code").equals(new BmoRFQU().getProgramCode())) {
			table = new BmoRFQU();
		} else if (pmConn.getString("prog_code").equals(new BmoTerritory().getProgramCode())) {
			table = new BmoTerritory();
		} else if (pmConn.getString("prog_code").equals(new BmoComplexUnitPrice().getProgramCode())) {
			table = new BmoComplexUnitPrice();
		} else if (pmConn.getString("prog_code").equals(new BmoConcept().getProgramCode())) {
			table = new BmoConcept();
		} else if (pmConn.getString("prog_code").equals(new BmoConceptGroup().getProgramCode())) {
			table = new BmoConceptGroup();
		} else if (pmConn.getString("prog_code").equals(new BmoConceptHeading().getProgramCode())) {
			table = new BmoConceptHeading();
		} else if (pmConn.getString("prog_code").equals(new BmoContractConcept().getProgramCode())) {
			table = new BmoContractConcept();
		} else if (pmConn.getString("prog_code").equals(new BmoContractConceptGroup().getProgramCode())) {
			table = new BmoContractConceptGroup();
		} else if (pmConn.getString("prog_code").equals(new BmoContractConceptItem().getProgramCode())) {
			table = new BmoContractConceptItem();
		} else if (pmConn.getString("prog_code").equals(new BmoContractEstimation().getProgramCode())) {
			table = new BmoContractEstimation();
		} else if (pmConn.getString("prog_code").equals(new BmoContractProperty().getProgramCode())) {
			table = new BmoContractProperty();
		} else if (pmConn.getString("prog_code").equals(new BmoContractTerm().getProgramCode())) {
			table = new BmoContractTerm();
		} else if (pmConn.getString("prog_code").equals(new BmoDevelopment().getProgramCode())) {
			table = new BmoDevelopment();
		} else if (pmConn.getString("prog_code").equals(new BmoDevelopmentBlock().getProgramCode())) {
			table = new BmoDevelopmentBlock();
		} else if (pmConn.getString("prog_code").equals(new BmoDevelopmentPhase().getProgramCode())) {
			table = new BmoDevelopmentPhase();
		} else if (pmConn.getString("prog_code").equals(new BmoDevelopmentRegistry().getProgramCode())) {
			table = new BmoDevelopmentRegistry();
		} else if (pmConn.getString("prog_code").equals(new BmoDevelopmentType().getProgramCode())) {
			table = new BmoDevelopmentType();
		} else if (pmConn.getString("prog_code").equals(new BmoEstimationItem().getProgramCode())) {
			table = new BmoEstimationItem();
		} else if (pmConn.getString("prog_code").equals(new BmoFSRS().getProgramCode())) {
			table = new BmoFSRS();
		} else if (pmConn.getString("prog_code").equals(new BmoMaterialType().getProgramCode())) {
			table = new BmoMaterialType();
		} else if (pmConn.getString("prog_code").equals(new BmoOrderProperty().getProgramCode())) {
			table = new BmoOrderProperty();
		} else if (pmConn.getString("prog_code").equals(new BmoOrderPropertyModelExtra().getProgramCode())) {
			table = new BmoOrderPropertyModelExtra();
		} else if (pmConn.getString("prog_code").equals(new BmoOrderPropertyTax().getProgramCode())) {
			table = new BmoOrderPropertyTax();
		} else if (pmConn.getString("prog_code").equals(new BmoProperty().getProgramCode())) {
			table = new BmoProperty();
		} else if (pmConn.getString("prog_code").equals(new BmoPropertyModel().getProgramCode())) {
			table = new BmoPropertyModel();
		} else if (pmConn.getString("prog_code").equals(new BmoPropertyModelExtra().getProgramCode())) {
			table = new BmoPropertyModelExtra();
		} else if (pmConn.getString("prog_code").equals(new BmoPropertyModelPrice().getProgramCode())) {
			table = new BmoPropertyModelPrice();
		} else if (pmConn.getString("prog_code").equals(new BmoPropertySale().getProgramCode())) {
			table = new BmoPropertySale();
		} else if (pmConn.getString("prog_code").equals(new BmoPropertySaleDetail().getProgramCode())) {
			table = new BmoPropertySaleDetail();
		} else if (pmConn.getString("prog_code").equals(new BmoPropertyTax().getProgramCode())) {
			table = new BmoPropertyTax();
		} else if (pmConn.getString("prog_code").equals(new BmoPropertyType().getProgramCode())) {
			table = new BmoPropertyType();
		} else if (pmConn.getString("prog_code").equals(new BmoUnitPrice().getProgramCode())) {
			table = new BmoUnitPrice();
		} else if (pmConn.getString("prog_code").equals(new BmoUnitPriceEquipment().getProgramCode())) {
			table = new BmoUnitPriceEquipment();
		} else if (pmConn.getString("prog_code").equals(new BmoUnitPriceHistory().getProgramCode())) {
			table = new BmoUnitPriceHistory();
		} else if (pmConn.getString("prog_code").equals(new BmoUnitPriceItem().getProgramCode())) {
			table = new BmoUnitPriceItem();
		} else if (pmConn.getString("prog_code").equals(new BmoWork().getProgramCode())) {
			table = new BmoWork();
		} else if (pmConn.getString("prog_code").equals(new BmoWorkContract().getProgramCode())) {
			table = new BmoWorkContract();
		} else if (pmConn.getString("prog_code").equals(new BmoWorkContractProperty().getProgramCode())) {
			table = new BmoWorkContractProperty();
		} else if (pmConn.getString("prog_code").equals(new BmoWorkItem().getProgramCode())) {
			table = new BmoWorkItem();
		} else if (pmConn.getString("prog_code").equals(new BmoWorkType().getProgramCode())) {
			table = new BmoWorkType();
			if (pmConn.getString("prog_code").equals(new BmoOrderSession().getProgramCode())) {
				table = new BmoOrderSession();
			} else if (pmConn.getString("prog_code").equals(new BmoOrderSessionExtra().getProgramCode())) {
				table = new BmoOrderSessionExtra();
			} else if (pmConn.getString("prog_code")
					.equals(new BmoOrderSessionTypePackage().getProgramCode())) {
				table = new BmoOrderSessionTypePackage();
			} else if (pmConn.getString("prog_code").equals(new BmoProgramSession().getProgramCode())) {
				table = new BmoProgramSession();
			} else if (pmConn.getString("prog_code").equals(new BmoProgramSessionLevel().getProgramCode())) {
				table = new BmoProgramSessionLevel();
			} else if (pmConn.getString("prog_code").equals(new BmoProgramSessionSubLevel().getProgramCode())) {
				table = new BmoProgramSessionSubLevel();
			} else if (pmConn.getString("prog_code")
					.equals(new BmoProgramSessionSubLevelType().getProgramCode())) {
				table = new BmoProgramSessionSubLevelType();
			} else if (pmConn.getString("prog_code").equals(new BmoSession().getProgramCode())) {
				table = new BmoSession();
			} else if (pmConn.getString("prog_code").equals(new BmoSessionDiscipline().getProgramCode())) {
				table = new BmoSessionDiscipline();
			} else if (pmConn.getString("prog_code").equals(new BmoSessionReview().getProgramCode())) {
				table = new BmoSessionReview();
			} else if (pmConn.getString("prog_code").equals(new BmoSessionSale().getProgramCode())) {
				table = new BmoSessionSale();
			} else if (pmConn.getString("prog_code").equals(new BmoSessionType().getProgramCode())) {
				table = new BmoSessionType();
			} else if (pmConn.getString("prog_code").equals(new BmoSessionTypeExtra().getProgramCode())) {
				table = new BmoSessionTypeExtra();
			} else if (pmConn.getString("prog_code").equals(new BmoSessionTypePackage().getProgramCode())) {
				table = new BmoSessionTypePackage();
			} else if (pmConn.getString("prog_code").equals(new BmoPropertyRental().getProgramCode())) {
				table = new BmoPropertyRental();
			} else if (pmConn.getString("prog_code").equals(new BmoCompetition().getProgramCode())) {
				table = new BmoCompetition();
			} else if (pmConn.getString("prog_code").equals(new BmoConsultingService().getProgramCode())) {
				table = new BmoConsultingService();
			} else if (pmConn.getString("prog_code").equals(new BmoCustomer().getProgramCode())) {
				table = new BmoCustomer();
			} else if (pmConn.getString("prog_code").equals(new BmoCustomerAddress().getProgramCode())) {
				table = new BmoCustomerAddress();
			} else if (pmConn.getString("prog_code").equals(new BmoCustomerBankAccount().getProgramCode())) {
				table = new BmoCustomerBankAccount();
			} else if (pmConn.getString("prog_code").equals(new BmoCustomerCompany().getProgramCode())) {
				table = new BmoCustomerCompany();
			} else if (pmConn.getString("prog_code").equals(new BmoCustomerContact().getProgramCode())) {
				table = new BmoCustomerContact();
			} else if (pmConn.getString("prog_code").equals(new BmoCustomerDate().getProgramCode())) {
				table = new BmoCustomerDate();
			} else if (pmConn.getString("prog_code").equals(new BmoCustomerEmail().getProgramCode())) {
				table = new BmoCustomerEmail();
			} else if (pmConn.getString("prog_code").equals(new BmoCustomerNote().getProgramCode())) {
				table = new BmoCustomerNote();
			} else if (pmConn.getString("prog_code").equals(new BmoCustomerPaymentType().getProgramCode())) {
				table = new BmoCustomerPaymentType();
			} else if (pmConn.getString("prog_code").equals(new BmoCustomerPhone().getProgramCode())) {
				table = new BmoCustomerPhone();
			} else if (pmConn.getString("prog_code").equals(new BmoCustomerPoll().getProgramCode())) {
				table = new BmoCustomerPoll();
			} else if (pmConn.getString("prog_code").equals(new BmoCustomerRelative().getProgramCode())) {
				table = new BmoCustomerRelative();
			} else if (pmConn.getString("prog_code").equals(new BmoCustomerSocial().getProgramCode())) {
				table = new BmoCustomerSocial();
			} else if (pmConn.getString("prog_code").equals(new BmoCustomerStatus().getProgramCode())) {
				table = new BmoCustomerStatus();
			} else if (pmConn.getString("prog_code").equals(new BmoCustomerWeb().getProgramCode())) {
				table = new BmoCustomerWeb();
			} else if (pmConn.getString("prog_code").equals(new BmoEstimation().getProgramCode())) {
				table = new BmoEstimation();
			} else if (pmConn.getString("prog_code").equals(new BmoEstimationGroup().getProgramCode())) {
				table = new BmoEstimationGroup();
			} else if (pmConn.getString("prog_code").equals(new BmoEstimationRFQItem().getProgramCode())) {
				table = new BmoEstimationRFQItem();
			} else if (pmConn.getString("prog_code").equals(new BmoExternalSales().getProgramCode())) {
				table = new BmoExternalSales();
			} else if (pmConn.getString("prog_code").equals(new BmoIndustry().getProgramCode())) {
				table = new BmoIndustry();
			} else if (pmConn.getString("prog_code").equals(new BmoLoseMotive().getProgramCode())) {
				table = new BmoLoseMotive();
			} else if (pmConn.getString("prog_code").equals(new BmoMarket().getProgramCode())) {
				table = new BmoMarket();
			} else if (pmConn.getString("prog_code").equals(new BmoOpportunity().getProgramCode())) {
				table = new BmoOpportunity();
			} else if (pmConn.getString("prog_code").equals(new BmoOpportunityCompetition().getProgramCode())) {
				table = new BmoOpportunityCompetition();
			} else if (pmConn.getString("prog_code").equals(new BmoOpportunityDetail().getProgramCode())) {
				table = new BmoOpportunityDetail();
			} else if (pmConn.getString("prog_code").equals(new BmoPayCondition().getProgramCode())) {
				table = new BmoPayCondition();
			} else if (pmConn.getString("prog_code").equals(new BmoProject().getProgramCode())) {
				table = new BmoProject();
			} else if (pmConn.getString("prog_code").equals(new BmoProjectActivities().getProgramCode())) {
				table = new BmoProjectActivities();
			} else if (pmConn.getString("prog_code").equals(new BmoProjectActivitiesDep().getProgramCode())) {
				table = new BmoProjectActivitiesDep();
			} else if (pmConn.getString("prog_code").equals(new BmoProjectDetail().getProgramCode())) {
				table = new BmoProjectDetail();
			} else if (pmConn.getString("prog_code").equals(new BmoProjectGuideline().getProgramCode())) {
				table = new BmoProjectGuideline();
			} else if (pmConn.getString("prog_code").equals(new BmoProjectStep().getProgramCode())) {
				table = new BmoProjectStep();
			} else if (pmConn.getString("prog_code").equals(new BmoQuote().getProgramCode())) {
				table = new BmoQuote();
			} else if (pmConn.getString("prog_code").equals(new BmoQuoteEquipment().getProgramCode())) {
				table = new BmoQuoteEquipment();
			} else if (pmConn.getString("prog_code").equals(new BmoQuoteGroup().getProgramCode())) {
				table = new BmoQuoteGroup();
			} else if (pmConn.getString("prog_code").equals(new BmoQuoteItem().getProgramCode())) {
				table = new BmoQuoteItem();
			} else if (pmConn.getString("prog_code").equals(new BmoQuoteProperty().getProgramCode())) {
				table = new BmoQuoteProperty();
			} else if (pmConn.getString("prog_code")
					.equals(new BmoQuotePropertyModelExtra().getProgramCode())) {
				table = new BmoQuotePropertyModelExtra();
			} else if (pmConn.getString("prog_code").equals(new BmoQuoteStaff().getProgramCode())) {
				table = new BmoQuoteStaff();
			} else if (pmConn.getString("prog_code").equals(new BmoRateType().getProgramCode())) {
				table = new BmoRateType();
			} else if (pmConn.getString("prog_code").equals(new BmoReferral().getProgramCode())) {
				table = new BmoReferral();
			} else if (pmConn.getString("prog_code").equals(new BmoRegion().getProgramCode())) {
				table = new BmoRegion();
			} else if (pmConn.getString("prog_code").equals(new BmoRFQU().getProgramCode())) {
				table = new BmoRFQU();
			} else if (pmConn.getString("prog_code").equals(new BmoTerritory().getProgramCode())) {
				table = new BmoTerritory();
			} else if (pmConn.getString("prog_code").equals(new BmoComplexUnitPrice().getProgramCode())) {
				table = new BmoComplexUnitPrice();
			} else if (pmConn.getString("prog_code").equals(new BmoConcept().getProgramCode())) {
				table = new BmoConcept();
			} else if (pmConn.getString("prog_code").equals(new BmoConceptGroup().getProgramCode())) {
				table = new BmoConceptGroup();
			} else if (pmConn.getString("prog_code").equals(new BmoConceptHeading().getProgramCode())) {
				table = new BmoConceptHeading();
			} else if (pmConn.getString("prog_code").equals(new BmoContractConcept().getProgramCode())) {
				table = new BmoContractConcept();
			} else if (pmConn.getString("prog_code").equals(new BmoContractConceptGroup().getProgramCode())) {
				table = new BmoContractConceptGroup();
			} else if (pmConn.getString("prog_code").equals(new BmoContractConceptItem().getProgramCode())) {
				table = new BmoContractConceptItem();
			} else if (pmConn.getString("prog_code").equals(new BmoContractEstimation().getProgramCode())) {
				table = new BmoContractEstimation();
			} else if (pmConn.getString("prog_code").equals(new BmoContractProperty().getProgramCode())) {
				table = new BmoContractProperty();
			} else if (pmConn.getString("prog_code").equals(new BmoContractTerm().getProgramCode())) {
				table = new BmoContractTerm();
			} else if (pmConn.getString("prog_code").equals(new BmoDevelopment().getProgramCode())) {
				table = new BmoDevelopment();
			} else if (pmConn.getString("prog_code").equals(new BmoDevelopmentBlock().getProgramCode())) {
				table = new BmoDevelopmentBlock();
			} else if (pmConn.getString("prog_code").equals(new BmoDevelopmentPhase().getProgramCode())) {
				table = new BmoDevelopmentPhase();
			} else if (pmConn.getString("prog_code").equals(new BmoDevelopmentRegistry().getProgramCode())) {
				table = new BmoDevelopmentRegistry();
			} else if (pmConn.getString("prog_code").equals(new BmoDevelopmentType().getProgramCode())) {
				table = new BmoDevelopmentType();
			} else if (pmConn.getString("prog_code").equals(new BmoEstimationItem().getProgramCode())) {
				table = new BmoEstimationItem();
			} else if (pmConn.getString("prog_code").equals(new BmoFSRS().getProgramCode())) {
				table = new BmoFSRS();
			} else if (pmConn.getString("prog_code").equals(new BmoMaterialType().getProgramCode())) {
				table = new BmoMaterialType();
			} else if (pmConn.getString("prog_code").equals(new BmoOrderProperty().getProgramCode())) {
				table = new BmoOrderProperty();
			} else if (pmConn.getString("prog_code")
					.equals(new BmoOrderPropertyModelExtra().getProgramCode())) {
				table = new BmoOrderPropertyModelExtra();
			} else if (pmConn.getString("prog_code").equals(new BmoOrderPropertyTax().getProgramCode())) {
				table = new BmoOrderPropertyTax();
			} else if (pmConn.getString("prog_code").equals(new BmoProperty().getProgramCode())) {
				table = new BmoProperty();
			} else if (pmConn.getString("prog_code").equals(new BmoPropertyModel().getProgramCode())) {
				table = new BmoPropertyModel();
			} else if (pmConn.getString("prog_code").equals(new BmoPropertyModelExtra().getProgramCode())) {
				table = new BmoPropertyModelExtra();
			} else if (pmConn.getString("prog_code").equals(new BmoPropertyModelPrice().getProgramCode())) {
				table = new BmoPropertyModelPrice();
			} else if (pmConn.getString("prog_code").equals(new BmoPropertySale().getProgramCode())) {
				table = new BmoPropertySale();
			} else if (pmConn.getString("prog_code").equals(new BmoPropertySaleDetail().getProgramCode())) {
				table = new BmoPropertySaleDetail();
			} else if (pmConn.getString("prog_code").equals(new BmoPropertyTax().getProgramCode())) {
				table = new BmoPropertyTax();
			} else if (pmConn.getString("prog_code").equals(new BmoPropertyType().getProgramCode())) {
				table = new BmoPropertyType();
			} else if (pmConn.getString("prog_code").equals(new BmoUnitPrice().getProgramCode())) {
				table = new BmoUnitPrice();
			} else if (pmConn.getString("prog_code").equals(new BmoUnitPriceEquipment().getProgramCode())) {
				table = new BmoUnitPriceEquipment();
			} else if (pmConn.getString("prog_code").equals(new BmoUnitPriceHistory().getProgramCode())) {
				table = new BmoUnitPriceHistory();
			} else if (pmConn.getString("prog_code").equals(new BmoUnitPriceItem().getProgramCode())) {
				table = new BmoUnitPriceItem();
			} else if (pmConn.getString("prog_code").equals(new BmoWork().getProgramCode())) {
				table = new BmoWork();
			} else if (pmConn.getString("prog_code").equals(new BmoWorkContract().getProgramCode())) {
				table = new BmoWorkContract();
			} else if (pmConn.getString("prog_code").equals(new BmoWorkContractProperty().getProgramCode())) {
				table = new BmoWorkContractProperty();
			} else if (pmConn.getString("prog_code").equals(new BmoWorkItem().getProgramCode())) {
				table = new BmoWorkItem();
			} else if (pmConn.getString("prog_code").equals(new BmoWorkType().getProgramCode())) {
				table = new BmoWorkType();
			} else if (pmConn.getString("prog_code").equals(new BmoCredit().getProgramCode())) {
				table = new BmoCredit();
			} else if (pmConn.getString("prog_code").equals(new BmoCreditGuarantee().getProgramCode())) {
				table = new BmoCreditGuarantee();
			} else if (pmConn.getString("prog_code").equals(new BmoCreditType().getProgramCode())) {
				table = new BmoCreditType();
			} else if (pmConn.getString("prog_code").equals(new BmoOrderCredit().getProgramCode())) {
				table = new BmoOrderCredit();
			} else if (pmConn.getString("prog_code").equals(new BmoUserCreditLimit().getProgramCode())) {
				table = new BmoUserCreditLimit();
			} else if (pmConn.getString("prog_code").equals(new BmoActivity().getProgramCode())) {
				table = new BmoActivity();
			} else if (pmConn.getString("prog_code").equals(new BmoCourse().getProgramCode())) {
				table = new BmoCourse();
			} else if (pmConn.getString("prog_code").equals(new BmoCourseProgram().getProgramCode())) {
				table = new BmoCourseProgram();
			} else if (pmConn.getString("prog_code").equals(new BmoMeeting().getProgramCode())) {
				table = new BmoMeeting();
			} else if (pmConn.getString("prog_code").equals(new BmoPosition().getProgramCode())) {
				table = new BmoPosition();
			} else if (pmConn.getString("prog_code").equals(new BmoPositionSkill().getProgramCode())) {
				table = new BmoPositionSkill();
			} else if (pmConn.getString("prog_code").equals(new BmoSkill().getProgramCode())) {
				table = new BmoSkill();
			} else if (pmConn.getString("prog_code").equals(new BmoTrainingSession().getProgramCode())) {
				table = new BmoTrainingSession();
			} else if (pmConn.getString("prog_code").equals(new BmoUserSession().getProgramCode())) {
				table = new BmoUserSession();
			} else if (pmConn.getString("prog_code").equals(new BmoVenue().getProgramCode())) {
				table = new BmoVenue();
			} else if (pmConn.getString("prog_code").equals(new BmoBankAccount().getProgramCode())) {
				table = new BmoBankAccount();
			} else if (pmConn.getString("prog_code").equals(new BmoBankAccountType().getProgramCode())) {
				table = new BmoBankAccountType();
			} else if (pmConn.getString("prog_code").equals(new BmoBankMovConcept().getProgramCode())) {
				table = new BmoBankMovConcept();
			} else if (pmConn.getString("prog_code").equals(new BmoBankMovement().getProgramCode())) {
				table = new BmoBankMovement();
			} else if (pmConn.getString("prog_code").equals(new BmoBankMovType().getProgramCode())) {
				table = new BmoBankMovType();
			} else if (pmConn.getString("prog_code").equals(new BmoBudget().getProgramCode())) {
				table = new BmoBudget();
			} else if (pmConn.getString("prog_code").equals(new BmoBudgetItem().getProgramCode())) {
				table = new BmoBudgetItem();
			} else if (pmConn.getString("prog_code").equals(new BmoBudgetItemType().getProgramCode())) {
				table = new BmoBudgetItemType();
			} else if (pmConn.getString("prog_code").equals(new BmoCFDI().getProgramCode())) {
				table = new BmoCFDI();
			} else if (pmConn.getString("prog_code").equals(new BmoCommission().getProgramCode())) {
				table = new BmoCommission();
			} else if (pmConn.getString("prog_code").equals(new BmoCurrency().getProgramCode())) {
				table = new BmoCurrency();
			} else if (pmConn.getString("prog_code").equals(new BmoCurrencyRate().getProgramCode())) {
				table = new BmoCurrencyRate();
			} else if (pmConn.getString("prog_code").equals(new BmoFiscalPeriod().getProgramCode())) {
				table = new BmoFiscalPeriod();
			} else if (pmConn.getString("prog_code").equals(new BmoInvoice().getProgramCode())) {
				table = new BmoInvoice();
			} else if (pmConn.getString("prog_code").equals(new BmoInvoiceOrderDelivery().getProgramCode())) {
				table = new BmoInvoiceOrderDelivery();
			} else if (pmConn.getString("prog_code").equals(new BmoLoan().getProgramCode())) {
				table = new BmoLoan();
			} else if (pmConn.getString("prog_code").equals(new BmoLoanDisbursement().getProgramCode())) {
				table = new BmoLoanDisbursement();
			} else if (pmConn.getString("prog_code").equals(new BmoLoanPayment().getProgramCode())) {
				table = new BmoLoanPayment();
			} else if (pmConn.getString("prog_code").equals(new BmoPaccount().getProgramCode())) {
				table = new BmoPaccount();
			} else if (pmConn.getString("prog_code").equals(new BmoPaccountAssignment().getProgramCode())) {
				table = new BmoPaccountAssignment();
			} else if (pmConn.getString("prog_code").equals(new BmoPaccountItem().getProgramCode())) {
				table = new BmoPaccountItem();
			} else if (pmConn.getString("prog_code").equals(new BmoPaccountType().getProgramCode())) {
				table = new BmoPaccountType();
			} else if (pmConn.getString("prog_code").equals(new BmoPaymentType().getProgramCode())) {
				table = new BmoPaymentType();
			} else if (pmConn.getString("prog_code").equals(new BmoPayMethod().getProgramCode())) {
				table = new BmoPayMethod();
			} else if (pmConn.getString("prog_code").equals(new BmoRaccount().getProgramCode())) {
				table = new BmoRaccount();
			} else if (pmConn.getString("prog_code").equals(new BmoRaccountAssignment().getProgramCode())) {
				table = new BmoRaccountAssignment();
			} else if (pmConn.getString("prog_code").equals(new BmoRaccountItem().getProgramCode())) {
				table = new BmoRaccountItem();
			} else if (pmConn.getString("prog_code").equals(new BmoRaccountLink().getProgramCode())) {
				table = new BmoRaccountLink();
			} else if (pmConn.getString("prog_code").equals(new BmoRaccountLink2().getProgramCode())) {
				table = new BmoRaccountLink2();
			} else if (pmConn.getString("prog_code").equals(new BmoRaccountType().getProgramCode())) {
				table = new BmoRaccountType();
			} else if (pmConn.getString("prog_code").equals(new BmoCondition().getProgramCode())) {
				table = new BmoCondition();
			} else if (pmConn.getString("prog_code").equals(new BmoCoverage().getProgramCode())) {
				table = new BmoCoverage();
			} else if (pmConn.getString("prog_code").equals(new BmoDiscount().getProgramCode())) {
				table = new BmoDiscount();
			} else if (pmConn.getString("prog_code").equals(new BmoDocument().getProgramCode())) {
				table = new BmoDocument();
			} else if (pmConn.getString("prog_code").equals(new BmoFund().getProgramCode())) {
				table = new BmoFund();
			} else if (pmConn.getString("prog_code").equals(new BmoGoal().getProgramCode())) {
				table = new BmoGoal();
			} else if (pmConn.getString("prog_code").equals(new BmoInsurance().getProgramCode())) {
				table = new BmoInsurance();
			} else if (pmConn.getString("prog_code").equals(new BmoInsuranceCategory().getProgramCode())) {
				table = new BmoInsuranceCategory();
			} else if (pmConn.getString("prog_code").equals(new BmoInsuranceCoverage().getProgramCode())) {
				table = new BmoInsuranceCoverage();
			} else if (pmConn.getString("prog_code").equals(new BmoInsuranceDiscount().getProgramCode())) {
				table = new BmoInsuranceDiscount();
			} else if (pmConn.getString("prog_code").equals(new BmoInsuranceFund().getProgramCode())) {
				table = new BmoInsuranceFund();
			} else if (pmConn.getString("prog_code").equals(new BmoInsuranceValuable().getProgramCode())) {
				table = new BmoInsuranceValuable();
			} else if (pmConn.getString("prog_code").equals(new BmoPolicy().getProgramCode())) {
				table = new BmoPolicy();
			} else if (pmConn.getString("prog_code").equals(new BmoPolicyCoverage().getProgramCode())) {
				table = new BmoPolicyCoverage();
			} else if (pmConn.getString("prog_code").equals(new BmoPolicyPayment().getProgramCode())) {
				table = new BmoPolicyPayment();
			} else if (pmConn.getString("prog_code").equals(new BmoPolicyRecipient().getProgramCode())) {
				table = new BmoPolicyRecipient();
			} else if (pmConn.getString("prog_code").equals(new BmoPolicyStatus().getProgramCode())) {
				table = new BmoPolicyStatus();
			} else if (pmConn.getString("prog_code").equals(new BmoValuable().getProgramCode())) {
				table = new BmoValuable();
			} else if (pmConn.getString("prog_code").equals(new BmoCustomerService().getProgramCode())) {
				table = new BmoCustomerService();
			} else if (pmConn.getString("prog_code")
					.equals(new BmoCustomerServiceFollowup().getProgramCode())) {
				table = new BmoCustomerServiceFollowup();
			} else if (pmConn.getString("prog_code").equals(new BmoCustomerServiceType().getProgramCode())) {
				table = new BmoCustomerServiceType();
			} else if (pmConn.getString("prog_code").equals(new BmoEquipment().getProgramCode())) {
				table = new BmoEquipment();
			} else if (pmConn.getString("prog_code").equals(new BmoEquipmentCompany().getProgramCode())) {
				table = new BmoEquipmentCompany();
			} else if (pmConn.getString("prog_code").equals(new BmoEquipmentService().getProgramCode())) {
				table = new BmoEquipmentService();
			} else if (pmConn.getString("prog_code").equals(new BmoEquipmentType().getProgramCode())) {
				table = new BmoEquipmentType();
			} else if (pmConn.getString("prog_code").equals(new BmoEquipmentUse().getProgramCode())) {
				table = new BmoEquipmentUse();
			} else if (pmConn.getString("prog_code").equals(new BmoHelpDesk().getProgramCode())) {
				table = new BmoHelpDesk();
			} else if (pmConn.getString("prog_code").equals(new BmoHelpDeskType().getProgramCode())) {
				table = new BmoHelpDeskType();
			} else if (pmConn.getString("prog_code").equals(new BmoOrder().getProgramCode())) {
				table = new BmoOrder();
			} else if (pmConn.getString("prog_code").equals(new BmoOrderBlockDate().getProgramCode())) {
				table = new BmoOrderBlockDate();
			} else if (pmConn.getString("prog_code").equals(new BmoOrderCommission().getProgramCode())) {
				table = new BmoOrderCommission();
			} else if (pmConn.getString("prog_code").equals(new BmoOrderCommissionAmount().getProgramCode())) {
				table = new BmoOrderCommissionAmount();
			} else if (pmConn.getString("prog_code").equals(new BmoOrderDelivery().getProgramCode())) {
				table = new BmoOrderDelivery();
			} else if (pmConn.getString("prog_code").equals(new BmoOrderDeliveryItem().getProgramCode())) {
				table = new BmoOrderDeliveryItem();
			} else if (pmConn.getString("prog_code").equals(new BmoOrderDetail().getProgramCode())) {
				table = new BmoOrderDetail();
			} else if (pmConn.getString("prog_code").equals(new BmoOrderEquipment().getProgramCode())) {
				table = new BmoOrderEquipment();
			} else if (pmConn.getString("prog_code").equals(new BmoOrderFlowUserGrup().getProgramCode())) {
				table = new BmoOrderFlowUserGrup();
			} else if (pmConn.getString("prog_code").equals(new BmoOrderGroup().getProgramCode())) {
				table = new BmoOrderGroup();
			} else if (pmConn.getString("prog_code").equals(new BmoOrderItem().getProgramCode())) {
				table = new BmoOrderItem();
			} else if (pmConn.getString("prog_code").equals(new BmoOrderLock().getProgramCode())) {
				table = new BmoOrderLock();
			} else if (pmConn.getString("prog_code").equals(new BmoOrderStaff().getProgramCode())) {
				table = new BmoOrderStaff();
			} else if (pmConn.getString("prog_code").equals(new BmoOrderType().getProgramCode())) {
				table = new BmoOrderType();
			} else if (pmConn.getString("prog_code").equals(new BmoOrderTypeWFlowCategory().getProgramCode())) {
				table = new BmoOrderTypeWFlowCategory();
			} else if (pmConn.getString("prog_code").equals(new BmoProduct().getProgramCode())) {
				table = new BmoProduct();
			} else if (pmConn.getString("prog_code").equals(new BmoProductCompany().getProgramCode())) {
				table = new BmoProductCompany();
			} else if (pmConn.getString("prog_code").equals(new BmoProductFamily().getProgramCode())) {
				table = new BmoProductFamily();
			} else if (pmConn.getString("prog_code").equals(new BmoProductGroup().getProgramCode())) {
				table = new BmoProductGroup();
			} else if (pmConn.getString("prog_code").equals(new BmoProductKit().getProgramCode())) {
				table = new BmoProductKit();
			} else if (pmConn.getString("prog_code").equals(new BmoProductKitItem().getProgramCode())) {
				table = new BmoProductKitItem();
			} else if (pmConn.getString("prog_code").equals(new BmoProductLink().getProgramCode())) {
				table = new BmoProductLink();
			} else if (pmConn.getString("prog_code").equals(new BmoProductPrice().getProgramCode())) {
				table = new BmoProductPrice();
			} else if (pmConn.getString("prog_code").equals(new BmoProjectEquipment().getProgramCode())) {
				table = new BmoProjectEquipment();
			} else if (pmConn.getString("prog_code").equals(new BmoReqPayType().getProgramCode())) {
				table = new BmoReqPayType();
			} else if (pmConn.getString("prog_code").equals(new BmoRequisition().getProgramCode())) {
				table = new BmoRequisition();
			} else if (pmConn.getString("prog_code").equals(new BmoRequisitionItem().getProgramCode())) {
				table = new BmoRequisitionItem();
			} else if (pmConn.getString("prog_code").equals(new BmoRequisitionReceipt().getProgramCode())) {
				table = new BmoRequisitionReceipt();
			} else if (pmConn.getString("prog_code").equals(new BmoRequisitionReceiptItem().getProgramCode())) {
				table = new BmoRequisitionReceiptItem();
			} else if (pmConn.getString("prog_code").equals(new BmoRequisitionType().getProgramCode())) {
				table = new BmoRequisitionType();
			} else if (pmConn.getString("prog_code").equals(new BmoServiceOrder().getProgramCode())) {
				table = new BmoServiceOrder();
			} else if (pmConn.getString("prog_code").equals(new BmoServiceOrderReportTime().getProgramCode())) {
				table = new BmoServiceOrderReportTime();
			} else if (pmConn.getString("prog_code").equals(new BmoSupplier().getProgramCode())) {
				table = new BmoSupplier();
			} else if (pmConn.getString("prog_code").equals(new BmoSupplierAddress().getProgramCode())) {
				table = new BmoSupplierAddress();
			} else if (pmConn.getString("prog_code").equals(new BmoSupplierBankAccount().getProgramCode())) {
				table = new BmoSupplierBankAccount();
			} else if (pmConn.getString("prog_code").equals(new BmoSupplierCategory().getProgramCode())) {
				table = new BmoSupplierCategory();
			} else if (pmConn.getString("prog_code").equals(new BmoSupplierCompany().getProgramCode())) {
				table = new BmoSupplierCompany();
			} else if (pmConn.getString("prog_code").equals(new BmoSupplierContact().getProgramCode())) {
				table = new BmoSupplierContact();
			} else if (pmConn.getString("prog_code").equals(new BmoSupplierEmail().getProgramCode())) {
				table = new BmoSupplierEmail();
			} else if (pmConn.getString("prog_code").equals(new BmoSupplierPaymentType().getProgramCode())) {
				table = new BmoSupplierPaymentType();
			} else if (pmConn.getString("prog_code").equals(new BmoSupplierPhone().getProgramCode())) {
				table = new BmoSupplierPhone();
			} else if (pmConn.getString("prog_code").equals(new BmoSupplierUser().getProgramCode())) {
				table = new BmoSupplierUser();
			} else if (pmConn.getString("prog_code").equals(new BmoUnit().getProgramCode())) {
				table = new BmoUnit();
			} else if (pmConn.getString("prog_code").equals(new BmoWarehouse().getProgramCode())) {
				table = new BmoWarehouse();
			} else if (pmConn.getString("prog_code").equals(new BmoWhBox().getProgramCode())) {
				table = new BmoWhBox();
			} else if (pmConn.getString("prog_code").equals(new BmoWhBoxTrack().getProgramCode())) {
				table = new BmoWhBoxTrack();
			} else if (pmConn.getString("prog_code").equals(new BmoWhMovement().getProgramCode())) {
				table = new BmoWhMovement();
			} else if (pmConn.getString("prog_code").equals(new BmoWhMovItem().getProgramCode())) {
				table = new BmoWhMovItem();
			} else if (pmConn.getString("prog_code").equals(new BmoWhMovItemTrack().getProgramCode())) {
				table = new BmoWhMovItemTrack();
			} else if (pmConn.getString("prog_code").equals(new BmoWhSection().getProgramCode())) {
				table = new BmoWhSection();
			} else if (pmConn.getString("prog_code").equals(new BmoWhStock().getProgramCode())) {
				table = new BmoWhStock();
			} else if (pmConn.getString("prog_code").equals(new BmoWhTrack().getProgramCode())) {
				table = new BmoWhTrack();
			} else if (pmConn.getString("prog_code").equals(new BmoWFEmail().getProgramCode())) {
				table = new BmoWFEmail();
			} else if (pmConn.getString("prog_code").equals(new BmoWFlow().getProgramCode())) {
				table = new BmoWFlow();
			} else if (pmConn.getString("prog_code").equals(new BmoWFlowAction().getProgramCode())) {
				table = new BmoWFlowAction();
			} else if (pmConn.getString("prog_code").equals(new BmoWFlowCategory().getProgramCode())) {
				table = new BmoWFlowCategory();
			} else if (pmConn.getString("prog_code").equals(new BmoWFlowCategoryProfile().getProgramCode())) {
				table = new BmoWFlowCategoryProfile();
			} else if (pmConn.getString("prog_code").equals(new BmoWFlowDocument().getProgramCode())) {
				table = new BmoWFlowDocument();
			} else if (pmConn.getString("prog_code").equals(new BmoWFlowDocumentType().getProgramCode())) {
				table = new BmoWFlowDocumentType();
			} else if (pmConn.getString("prog_code").equals(new BmoWFlowFunnel().getProgramCode())) {
				table = new BmoWFlowFunnel();
			} else if (pmConn.getString("prog_code").equals(new BmoWFlowLog().getProgramCode())) {
				table = new BmoWFlowLog();
			} else if (pmConn.getString("prog_code").equals(new BmoWFlowPhase().getProgramCode())) {
				table = new BmoWFlowPhase();
			} else if (pmConn.getString("prog_code").equals(new BmoWFlowStep().getProgramCode())) {
				table = new BmoWFlowStep();
			} else if (pmConn.getString("prog_code").equals(new BmoWFlowStepDep().getProgramCode())) {
				table = new BmoWFlowStepDep();
			} else if (pmConn.getString("prog_code").equals(new BmoWFlowStepType().getProgramCode())) {
				table = new BmoWFlowStepType();
			} else if (pmConn.getString("prog_code").equals(new BmoWFlowStepTypeDep().getProgramCode())) {
				table = new BmoWFlowStepTypeDep();
			} else if (pmConn.getString("prog_code").equals(new BmoWFlowTimeTrack().getProgramCode())) {
				table = new BmoWFlowTimeTrack();
			} else if (pmConn.getString("prog_code").equals(new BmoWFlowType().getProgramCode())) {
				table = new BmoWFlowType();
			} else if (pmConn.getString("prog_code").equals(new BmoWFlowUser().getProgramCode())) {
				table = new BmoWFlowUser();
			} else if (pmConn.getString("prog_code").equals(new BmoWFlowUserBlockDate().getProgramCode())) {
				table = new BmoWFlowUserBlockDate();
			} else if (pmConn.getString("prog_code").equals(new BmoWFlowUserSelect().getProgramCode())) {
				table = new BmoWFlowUserSelect();
			} else if (pmConn.getString("prog_code").equals(new BmoWFlowValidation().getProgramCode())) {
				table = new BmoWFlowValidation();
			} else if (pmConn.getString("prog_code").equals(new BmoFlexConfig().getProgramCode())) {
				table = new BmoFlexConfig();
			} else if (pmConn.getString("prog_code").equals(new BmoSendReport().getProgramCode())) {
				table = new BmoSendReport();
			} else if (pmConn.getString("prog_code").equals(new BmoArea().getProgramCode())) {
				table = new BmoArea();
			} else if (pmConn.getString("prog_code").equals(new BmoAreaRate().getProgramCode())) {
				table = new BmoAreaRate();
			} else if (pmConn.getString("prog_code").equals(new BmoCity().getProgramCode())) {
				table = new BmoCity();
			} else if (pmConn.getString("prog_code").equals(new BmoCompany().getProgramCode())) {
				table = new BmoCompany();
			} else if (pmConn.getString("prog_code").equals(new BmoCountry().getProgramCode())) {
				table = new BmoCountry();
			} else if (pmConn.getString("prog_code").equals(new BmoCronJob().getProgramCode())) {
				table = new BmoCronJob();
			} else if (pmConn.getString("prog_code").equals(new BmoCronLog().getProgramCode())) {
				table = new BmoCronLog();
			} else if (pmConn.getString("prog_code").equals(new BmoEmail().getProgramCode())) {
				table = new BmoEmail();
			} else if (pmConn.getString("prog_code").equals(new BmoFile().getProgramCode())) {
				table = new BmoFile();
			} else if (pmConn.getString("prog_code").equals(new BmoFileType().getProgramCode())) {
				table = new BmoFileType();
			} else if (pmConn.getString("prog_code").equals(new BmoFormat().getProgramCode())) {
				table = new BmoFormat();
			} else if (pmConn.getString("prog_code").equals(new BmoLocation().getProgramCode())) {
				table = new BmoLocation();
			} else if (pmConn.getString("prog_code").equals(new BmoMenu().getProgramCode())) {
				table = new BmoMenu();
			} else if (pmConn.getString("prog_code").equals(new BmoOccupation().getProgramCode())) {
				table = new BmoOccupation();
			} else if (pmConn.getString("prog_code").equals(new BmoProfile().getProgramCode())) {
				table = new BmoProfile();
			} else if (pmConn.getString("prog_code").equals(new BmoProfileUser().getProgramCode())) {
				table = new BmoProfileUser();
			} else if (pmConn.getString("prog_code").equals(new BmoProgram().getProgramCode())) {
				table = new BmoProgram();
			} else if (pmConn.getString("prog_code").equals(new BmoProgramProfile().getProgramCode())) {
				table = new BmoProgramProfile();
			} else if (pmConn.getString("prog_code").equals(new BmoProgramProfileSpecial().getProgramCode())) {
				table = new BmoProgramProfileSpecial();
			} else if (pmConn.getString("prog_code").equals(new BmoProgramSpecial().getProgramCode())) {
				table = new BmoProgramSpecial();
			} else if (pmConn.getString("prog_code").equals(new BmoSFCaption().getProgramCode())) {
				table = new BmoSFCaption();
			} else if (pmConn.getString("prog_code").equals(new BmoSFConfig().getProgramCode())) {
				table = new BmoSFConfig();
			} else if (pmConn.getString("prog_code").equals(new BmoSFInstance().getProgramCode())) {
				table = new BmoSFInstance();
			} else if (pmConn.getString("prog_code").equals(new BmoSFLog().getProgramCode())) {
				table = new BmoSFLog();
			} else if (pmConn.getString("prog_code").equals(new BmoSocial().getProgramCode())) {
				table = new BmoSocial();
			} else if (pmConn.getString("prog_code").equals(new BmoState().getProgramCode())) {
				table = new BmoState();
			} else if (pmConn.getString("prog_code").equals(new BmoTag().getProgramCode())) {
				table = new BmoTag();
			} else if (pmConn.getString("prog_code").equals(new BmoTitle().getProgramCode())) {
				table = new BmoTitle();
			} else if (pmConn.getString("prog_code").equals(new BmoUiColor().getProgramCode())) {
				table = new BmoUiColor();
			} else if (pmConn.getString("prog_code").equals(new BmoUser().getProgramCode())) {
				table = new BmoUser();
			} else if (pmConn.getString("prog_code").equals(new BmoUserAddress().getProgramCode())) {
				table = new BmoUserAddress();
			} else if (pmConn.getString("prog_code").equals(new BmoUserCompany().getProgramCode())) {
				table = new BmoUserCompany();
			} else if (pmConn.getString("prog_code").equals(new BmoUserContact().getProgramCode())) {
				table = new BmoUserContact();
			} else if (pmConn.getString("prog_code").equals(new BmoUserDate().getProgramCode())) {
				table = new BmoUserDate();
			} else if (pmConn.getString("prog_code").equals(new BmoUserEmail().getProgramCode())) {
				table = new BmoUserEmail();
			} else if (pmConn.getString("prog_code").equals(new BmoUserPhone().getProgramCode())) {
				table = new BmoUserPhone();
			} else if (pmConn.getString("prog_code").equals(new BmoUserRelative().getProgramCode())) {
				table = new BmoUserRelative();
			} else if (pmConn.getString("prog_code").equals(new BmoUserSocial().getProgramCode())) {
				table = new BmoUserSocial();
			} else if (pmConn.getString("prog_code").equals(new BmoUserTimeClock().getProgramCode())) {
				table = new BmoUserTimeClock();
			}
		} else if (pmConn.getString("prog_code").equals(new BmoCredit().getProgramCode())) {
			table = new BmoCredit();
		} else if (pmConn.getString("prog_code").equals(new BmoCreditGuarantee().getProgramCode())) {
			table = new BmoCreditGuarantee();
		} else if (pmConn.getString("prog_code").equals(new BmoCreditType().getProgramCode())) {
			table = new BmoCreditType();
		} else if (pmConn.getString("prog_code").equals(new BmoOrderCredit().getProgramCode())) {
			table = new BmoOrderCredit();
		} else if (pmConn.getString("prog_code").equals(new BmoUserCreditLimit().getProgramCode())) {
			table = new BmoUserCreditLimit();
		} else if (pmConn.getString("prog_code").equals(new BmoActivity().getProgramCode())) {
			table = new BmoActivity();
		} else if (pmConn.getString("prog_code").equals(new BmoCourse().getProgramCode())) {
			table = new BmoCourse();
		} else if (pmConn.getString("prog_code").equals(new BmoCourseProgram().getProgramCode())) {
			table = new BmoCourseProgram();
		} else if (pmConn.getString("prog_code").equals(new BmoMeeting().getProgramCode())) {
			table = new BmoMeeting();
		} else if (pmConn.getString("prog_code").equals(new BmoPosition().getProgramCode())) {
			table = new BmoPosition();
		} else if (pmConn.getString("prog_code").equals(new BmoPositionSkill().getProgramCode())) {
			table = new BmoPositionSkill();
		} else if (pmConn.getString("prog_code").equals(new BmoSkill().getProgramCode())) {
			table = new BmoSkill();
		} else if (pmConn.getString("prog_code").equals(new BmoTrainingSession().getProgramCode())) {
			table = new BmoTrainingSession();
		} else if (pmConn.getString("prog_code").equals(new BmoUserSession().getProgramCode())) {
			table = new BmoUserSession();
		} else if (pmConn.getString("prog_code").equals(new BmoVenue().getProgramCode())) {
			table = new BmoVenue();
		} else if (pmConn.getString("prog_code").equals(new BmoBankAccount().getProgramCode())) {
			table = new BmoBankAccount();
		} else if (pmConn.getString("prog_code").equals(new BmoBankAccountType().getProgramCode())) {
			table = new BmoBankAccountType();
		} else if (pmConn.getString("prog_code").equals(new BmoBankMovConcept().getProgramCode())) {
			table = new BmoBankMovConcept();
		} else if (pmConn.getString("prog_code").equals(new BmoBankMovement().getProgramCode())) {
			table = new BmoBankMovement();
		} else if (pmConn.getString("prog_code").equals(new BmoBankMovType().getProgramCode())) {
			table = new BmoBankMovType();
		} else if (pmConn.getString("prog_code").equals(new BmoBudget().getProgramCode())) {
			table = new BmoBudget();
		} else if (pmConn.getString("prog_code").equals(new BmoBudgetItem().getProgramCode())) {
			table = new BmoBudgetItem();
		} else if (pmConn.getString("prog_code").equals(new BmoBudgetItemType().getProgramCode())) {
			table = new BmoBudgetItemType();
		} else if (pmConn.getString("prog_code").equals(new BmoCFDI().getProgramCode())) {
			table = new BmoCFDI();
		} else if (pmConn.getString("prog_code").equals(new BmoCommission().getProgramCode())) {
			table = new BmoCommission();
		} else if (pmConn.getString("prog_code").equals(new BmoCurrency().getProgramCode())) {
			table = new BmoCurrency();
		} else if (pmConn.getString("prog_code").equals(new BmoCurrencyRate().getProgramCode())) {
			table = new BmoCurrencyRate();
		} else if (pmConn.getString("prog_code").equals(new BmoFiscalPeriod().getProgramCode())) {
			table = new BmoFiscalPeriod();
		} else if (pmConn.getString("prog_code").equals(new BmoInvoice().getProgramCode())) {
			table = new BmoInvoice();
		} else if (pmConn.getString("prog_code").equals(new BmoInvoiceOrderDelivery().getProgramCode())) {
			table = new BmoInvoiceOrderDelivery();
		} else if (pmConn.getString("prog_code").equals(new BmoLoan().getProgramCode())) {
			table = new BmoLoan();
		} else if (pmConn.getString("prog_code").equals(new BmoLoanDisbursement().getProgramCode())) {
			table = new BmoLoanDisbursement();
		} else if (pmConn.getString("prog_code").equals(new BmoLoanPayment().getProgramCode())) {
			table = new BmoLoanPayment();
		} else if (pmConn.getString("prog_code").equals(new BmoPaccount().getProgramCode())) {
			table = new BmoPaccount();
		} else if (pmConn.getString("prog_code").equals(new BmoPaccountAssignment().getProgramCode())) {
			table = new BmoPaccountAssignment();
		} else if (pmConn.getString("prog_code").equals(new BmoPaccountItem().getProgramCode())) {
			table = new BmoPaccountItem();
		} else if (pmConn.getString("prog_code").equals(new BmoPaccountType().getProgramCode())) {
			table = new BmoPaccountType();
		} else if (pmConn.getString("prog_code").equals(new BmoPaymentType().getProgramCode())) {
			table = new BmoPaymentType();
		} else if (pmConn.getString("prog_code").equals(new BmoPayMethod().getProgramCode())) {
			table = new BmoPayMethod();
		} else if (pmConn.getString("prog_code").equals(new BmoRaccount().getProgramCode())) {
			table = new BmoRaccount();
		} else if (pmConn.getString("prog_code").equals(new BmoRaccountAssignment().getProgramCode())) {
			table = new BmoRaccountAssignment();
		} else if (pmConn.getString("prog_code").equals(new BmoRaccountItem().getProgramCode())) {
			table = new BmoRaccountItem();
		} else if (pmConn.getString("prog_code").equals(new BmoRaccountLink().getProgramCode())) {
			table = new BmoRaccountLink();
		} else if (pmConn.getString("prog_code").equals(new BmoRaccountLink2().getProgramCode())) {
			table = new BmoRaccountLink2();
		} else if (pmConn.getString("prog_code").equals(new BmoRaccountType().getProgramCode())) {
			table = new BmoRaccountType();
		} else if (pmConn.getString("prog_code").equals(new BmoCondition().getProgramCode())) {
			table = new BmoCondition();
		} else if (pmConn.getString("prog_code").equals(new BmoCoverage().getProgramCode())) {
			table = new BmoCoverage();
		} else if (pmConn.getString("prog_code").equals(new BmoDiscount().getProgramCode())) {
			table = new BmoDiscount();
		} else if (pmConn.getString("prog_code").equals(new BmoDocument().getProgramCode())) {
			table = new BmoDocument();
		} else if (pmConn.getString("prog_code").equals(new BmoFund().getProgramCode())) {
			table = new BmoFund();
		} else if (pmConn.getString("prog_code").equals(new BmoGoal().getProgramCode())) {
			table = new BmoGoal();
		} else if (pmConn.getString("prog_code").equals(new BmoInsurance().getProgramCode())) {
			table = new BmoInsurance();
		} else if (pmConn.getString("prog_code").equals(new BmoInsuranceCategory().getProgramCode())) {
			table = new BmoInsuranceCategory();
		} else if (pmConn.getString("prog_code").equals(new BmoInsuranceCoverage().getProgramCode())) {
			table = new BmoInsuranceCoverage();
		} else if (pmConn.getString("prog_code").equals(new BmoInsuranceDiscount().getProgramCode())) {
			table = new BmoInsuranceDiscount();
		} else if (pmConn.getString("prog_code").equals(new BmoInsuranceFund().getProgramCode())) {
			table = new BmoInsuranceFund();
		} else if (pmConn.getString("prog_code").equals(new BmoInsuranceValuable().getProgramCode())) {
			table = new BmoInsuranceValuable();
		} else if (pmConn.getString("prog_code").equals(new BmoPolicy().getProgramCode())) {
			table = new BmoPolicy();
		} else if (pmConn.getString("prog_code").equals(new BmoPolicyCoverage().getProgramCode())) {
			table = new BmoPolicyCoverage();
		} else if (pmConn.getString("prog_code").equals(new BmoPolicyPayment().getProgramCode())) {
			table = new BmoPolicyPayment();
		} else if (pmConn.getString("prog_code").equals(new BmoPolicyRecipient().getProgramCode())) {
			table = new BmoPolicyRecipient();
		} else if (pmConn.getString("prog_code").equals(new BmoPolicyStatus().getProgramCode())) {
			table = new BmoPolicyStatus();
		} else if (pmConn.getString("prog_code").equals(new BmoValuable().getProgramCode())) {
			table = new BmoValuable();
		} else if (pmConn.getString("prog_code").equals(new BmoCustomerService().getProgramCode())) {
			table = new BmoCustomerService();
		} else if (pmConn.getString("prog_code").equals(new BmoCustomerServiceFollowup().getProgramCode())) {
			table = new BmoCustomerServiceFollowup();
		} else if (pmConn.getString("prog_code").equals(new BmoCustomerServiceType().getProgramCode())) {
			table = new BmoCustomerServiceType();
		} else if (pmConn.getString("prog_code").equals(new BmoEquipment().getProgramCode())) {
			table = new BmoEquipment();
		} else if (pmConn.getString("prog_code").equals(new BmoEquipmentCompany().getProgramCode())) {
			table = new BmoEquipmentCompany();
		} else if (pmConn.getString("prog_code").equals(new BmoEquipmentService().getProgramCode())) {
			table = new BmoEquipmentService();
		} else if (pmConn.getString("prog_code").equals(new BmoEquipmentType().getProgramCode())) {
			table = new BmoEquipmentType();
		} else if (pmConn.getString("prog_code").equals(new BmoEquipmentUse().getProgramCode())) {
			table = new BmoEquipmentUse();
		} else if (pmConn.getString("prog_code").equals(new BmoHelpDesk().getProgramCode())) {
			table = new BmoHelpDesk();
		} else if (pmConn.getString("prog_code").equals(new BmoHelpDeskType().getProgramCode())) {
			table = new BmoHelpDeskType();
		} else if (pmConn.getString("prog_code").equals(new BmoOrder().getProgramCode())) {
			table = new BmoOrder();
		} else if (pmConn.getString("prog_code").equals(new BmoOrderBlockDate().getProgramCode())) {
			table = new BmoOrderBlockDate();
		} else if (pmConn.getString("prog_code").equals(new BmoOrderCommission().getProgramCode())) {
			table = new BmoOrderCommission();
		} else if (pmConn.getString("prog_code").equals(new BmoOrderCommissionAmount().getProgramCode())) {
			table = new BmoOrderCommissionAmount();
		} else if (pmConn.getString("prog_code").equals(new BmoOrderDelivery().getProgramCode())) {
			table = new BmoOrderDelivery();
		} else if (pmConn.getString("prog_code").equals(new BmoOrderDeliveryItem().getProgramCode())) {
			table = new BmoOrderDeliveryItem();
		} else if (pmConn.getString("prog_code").equals(new BmoOrderDetail().getProgramCode())) {
			table = new BmoOrderDetail();
		} else if (pmConn.getString("prog_code").equals(new BmoOrderEquipment().getProgramCode())) {
			table = new BmoOrderEquipment();
		} else if (pmConn.getString("prog_code").equals(new BmoOrderFlowUserGrup().getProgramCode())) {
			table = new BmoOrderFlowUserGrup();
		} else if (pmConn.getString("prog_code").equals(new BmoOrderGroup().getProgramCode())) {
			table = new BmoOrderGroup();
		} else if (pmConn.getString("prog_code").equals(new BmoOrderItem().getProgramCode())) {
			table = new BmoOrderItem();
		} else if (pmConn.getString("prog_code").equals(new BmoOrderLock().getProgramCode())) {
			table = new BmoOrderLock();
		} else if (pmConn.getString("prog_code").equals(new BmoOrderStaff().getProgramCode())) {
			table = new BmoOrderStaff();
		} else if (pmConn.getString("prog_code").equals(new BmoOrderType().getProgramCode())) {
			table = new BmoOrderType();
		} else if (pmConn.getString("prog_code").equals(new BmoOrderTypeWFlowCategory().getProgramCode())) {
			table = new BmoOrderTypeWFlowCategory();
		} else if (pmConn.getString("prog_code").equals(new BmoProduct().getProgramCode())) {
			table = new BmoProduct();
		} else if (pmConn.getString("prog_code").equals(new BmoProductCompany().getProgramCode())) {
			table = new BmoProductCompany();
		} else if (pmConn.getString("prog_code").equals(new BmoProductFamily().getProgramCode())) {
			table = new BmoProductFamily();
		} else if (pmConn.getString("prog_code").equals(new BmoProductGroup().getProgramCode())) {
			table = new BmoProductGroup();
		} else if (pmConn.getString("prog_code").equals(new BmoProductKit().getProgramCode())) {
			table = new BmoProductKit();
		} else if (pmConn.getString("prog_code").equals(new BmoProductKitItem().getProgramCode())) {
			table = new BmoProductKitItem();
		} else if (pmConn.getString("prog_code").equals(new BmoProductLink().getProgramCode())) {
			table = new BmoProductLink();
		} else if (pmConn.getString("prog_code").equals(new BmoProductPrice().getProgramCode())) {
			table = new BmoProductPrice();
		} else if (pmConn.getString("prog_code").equals(new BmoProjectEquipment().getProgramCode())) {
			table = new BmoProjectEquipment();
		} else if (pmConn.getString("prog_code").equals(new BmoReqPayType().getProgramCode())) {
			table = new BmoReqPayType();
		} else if (pmConn.getString("prog_code").equals(new BmoRequisition().getProgramCode())) {
			table = new BmoRequisition();
		} else if (pmConn.getString("prog_code").equals(new BmoRequisitionItem().getProgramCode())) {
			table = new BmoRequisitionItem();
		} else if (pmConn.getString("prog_code").equals(new BmoRequisitionReceipt().getProgramCode())) {
			table = new BmoRequisitionReceipt();
		} else if (pmConn.getString("prog_code").equals(new BmoRequisitionReceiptItem().getProgramCode())) {
			table = new BmoRequisitionReceiptItem();
		} else if (pmConn.getString("prog_code").equals(new BmoRequisitionType().getProgramCode())) {
			table = new BmoRequisitionType();
		} else if (pmConn.getString("prog_code").equals(new BmoServiceOrder().getProgramCode())) {
			table = new BmoServiceOrder();
		} else if (pmConn.getString("prog_code").equals(new BmoServiceOrderReportTime().getProgramCode())) {
			table = new BmoServiceOrderReportTime();
		} else if (pmConn.getString("prog_code").equals(new BmoSupplier().getProgramCode())) {
			table = new BmoSupplier();
		} else if (pmConn.getString("prog_code").equals(new BmoSupplierAddress().getProgramCode())) {
			table = new BmoSupplierAddress();
		} else if (pmConn.getString("prog_code").equals(new BmoSupplierBankAccount().getProgramCode())) {
			table = new BmoSupplierBankAccount();
		} else if (pmConn.getString("prog_code").equals(new BmoSupplierCategory().getProgramCode())) {
			table = new BmoSupplierCategory();
		} else if (pmConn.getString("prog_code").equals(new BmoSupplierCompany().getProgramCode())) {
			table = new BmoSupplierCompany();
		} else if (pmConn.getString("prog_code").equals(new BmoSupplierContact().getProgramCode())) {
			table = new BmoSupplierContact();
		} else if (pmConn.getString("prog_code").equals(new BmoSupplierEmail().getProgramCode())) {
			table = new BmoSupplierEmail();
		} else if (pmConn.getString("prog_code").equals(new BmoSupplierPaymentType().getProgramCode())) {
			table = new BmoSupplierPaymentType();
		} else if (pmConn.getString("prog_code").equals(new BmoSupplierPhone().getProgramCode())) {
			table = new BmoSupplierPhone();
		} else if (pmConn.getString("prog_code").equals(new BmoSupplierUser().getProgramCode())) {
			table = new BmoSupplierUser();
		} else if (pmConn.getString("prog_code").equals(new BmoUnit().getProgramCode())) {
			table = new BmoUnit();
		} else if (pmConn.getString("prog_code").equals(new BmoWarehouse().getProgramCode())) {
			table = new BmoWarehouse();
		} else if (pmConn.getString("prog_code").equals(new BmoWhBox().getProgramCode())) {
			table = new BmoWhBox();
		} else if (pmConn.getString("prog_code").equals(new BmoWhBoxTrack().getProgramCode())) {
			table = new BmoWhBoxTrack();
		} else if (pmConn.getString("prog_code").equals(new BmoWhMovement().getProgramCode())) {
			table = new BmoWhMovement();
		} else if (pmConn.getString("prog_code").equals(new BmoWhMovItem().getProgramCode())) {
			table = new BmoWhMovItem();
		} else if (pmConn.getString("prog_code").equals(new BmoWhMovItemTrack().getProgramCode())) {
			table = new BmoWhMovItemTrack();
		} else if (pmConn.getString("prog_code").equals(new BmoWhSection().getProgramCode())) {
			table = new BmoWhSection();
		} else if (pmConn.getString("prog_code").equals(new BmoWhStock().getProgramCode())) {
			table = new BmoWhStock();
		} else if (pmConn.getString("prog_code").equals(new BmoWhTrack().getProgramCode())) {
			table = new BmoWhTrack();
		} else if (pmConn.getString("prog_code").equals(new BmoWFEmail().getProgramCode())) {
			table = new BmoWFEmail();
		} else if (pmConn.getString("prog_code").equals(new BmoWFlow().getProgramCode())) {
			table = new BmoWFlow();
		} else if (pmConn.getString("prog_code").equals(new BmoWFlowAction().getProgramCode())) {
			table = new BmoWFlowAction();
		} else if (pmConn.getString("prog_code").equals(new BmoWFlowCategory().getProgramCode())) {
			table = new BmoWFlowCategory();
		} else if (pmConn.getString("prog_code").equals(new BmoWFlowCategoryProfile().getProgramCode())) {
			table = new BmoWFlowCategoryProfile();
		} else if (pmConn.getString("prog_code").equals(new BmoWFlowDocument().getProgramCode())) {
			table = new BmoWFlowDocument();
		} else if (pmConn.getString("prog_code").equals(new BmoWFlowDocumentType().getProgramCode())) {
			table = new BmoWFlowDocumentType();
		} else if (pmConn.getString("prog_code").equals(new BmoWFlowFunnel().getProgramCode())) {
			table = new BmoWFlowFunnel();
		} else if (pmConn.getString("prog_code").equals(new BmoWFlowLog().getProgramCode())) {
			table = new BmoWFlowLog();
		} else if (pmConn.getString("prog_code").equals(new BmoWFlowPhase().getProgramCode())) {
			table = new BmoWFlowPhase();
		} else if (pmConn.getString("prog_code").equals(new BmoWFlowStep().getProgramCode())) {
			table = new BmoWFlowStep();
		} else if (pmConn.getString("prog_code").equals(new BmoWFlowStepDep().getProgramCode())) {
			table = new BmoWFlowStepDep();
		} else if (pmConn.getString("prog_code").equals(new BmoWFlowStepType().getProgramCode())) {
			table = new BmoWFlowStepType();
		} else if (pmConn.getString("prog_code").equals(new BmoWFlowStepTypeDep().getProgramCode())) {
			table = new BmoWFlowStepTypeDep();
		} else if (pmConn.getString("prog_code").equals(new BmoWFlowTimeTrack().getProgramCode())) {
			table = new BmoWFlowTimeTrack();
		} else if (pmConn.getString("prog_code").equals(new BmoWFlowType().getProgramCode())) {
			table = new BmoWFlowType();
		} else if (pmConn.getString("prog_code").equals(new BmoWFlowUser().getProgramCode())) {
			table = new BmoWFlowUser();
		} else if (pmConn.getString("prog_code").equals(new BmoWFlowUserBlockDate().getProgramCode())) {
			table = new BmoWFlowUserBlockDate();
		} else if (pmConn.getString("prog_code").equals(new BmoWFlowUserSelect().getProgramCode())) {
			table = new BmoWFlowUserSelect();
		} else if (pmConn.getString("prog_code").equals(new BmoWFlowValidation().getProgramCode())) {
			table = new BmoWFlowValidation();
		} else if (pmConn.getString("prog_code").equals(new BmoFlexConfig().getProgramCode())) {
			table = new BmoFlexConfig();
		} else if (pmConn.getString("prog_code").equals(new BmoSendReport().getProgramCode())) {
			table = new BmoSendReport();
		} else if (pmConn.getString("prog_code").equals(new BmoArea().getProgramCode())) {
			table = new BmoArea();
		} else if (pmConn.getString("prog_code").equals(new BmoAreaRate().getProgramCode())) {
			table = new BmoAreaRate();
		} else if (pmConn.getString("prog_code").equals(new BmoCity().getProgramCode())) {
			table = new BmoCity();
		} else if (pmConn.getString("prog_code").equals(new BmoCompany().getProgramCode())) {
			table = new BmoCompany();
		} else if (pmConn.getString("prog_code").equals(new BmoCountry().getProgramCode())) {
			table = new BmoCountry();
		} else if (pmConn.getString("prog_code").equals(new BmoCronJob().getProgramCode())) {
			table = new BmoCronJob();
		} else if (pmConn.getString("prog_code").equals(new BmoCronLog().getProgramCode())) {
			table = new BmoCronLog();
		} else if (pmConn.getString("prog_code").equals(new BmoEmail().getProgramCode())) {
			table = new BmoEmail();
		} else if (pmConn.getString("prog_code").equals(new BmoFile().getProgramCode())) {
			table = new BmoFile();
		} else if (pmConn.getString("prog_code").equals(new BmoFileType().getProgramCode())) {
			table = new BmoFileType();
		} else if (pmConn.getString("prog_code").equals(new BmoFormat().getProgramCode())) {
			table = new BmoFormat();
		} else if (pmConn.getString("prog_code").equals(new BmoLocation().getProgramCode())) {
			table = new BmoLocation();
		} else if (pmConn.getString("prog_code").equals(new BmoMenu().getProgramCode())) {
			table = new BmoMenu();
		} else if (pmConn.getString("prog_code").equals(new BmoOccupation().getProgramCode())) {
			table = new BmoOccupation();
		} else if (pmConn.getString("prog_code").equals(new BmoProfile().getProgramCode())) {
			table = new BmoProfile();
		} else if (pmConn.getString("prog_code").equals(new BmoProfileUser().getProgramCode())) {
			table = new BmoProfileUser();
		} else if (pmConn.getString("prog_code").equals(new BmoProgram().getProgramCode())) {
			table = new BmoProgram();
		} else if (pmConn.getString("prog_code").equals(new BmoProgramProfile().getProgramCode())) {
			table = new BmoProgramProfile();
		} else if (pmConn.getString("prog_code").equals(new BmoProgramProfileSpecial().getProgramCode())) {
			table = new BmoProgramProfileSpecial();
		} else if (pmConn.getString("prog_code").equals(new BmoProgramSpecial().getProgramCode())) {
			table = new BmoProgramSpecial();
		} else if (pmConn.getString("prog_code").equals(new BmoSFCaption().getProgramCode())) {
			table = new BmoSFCaption();
		} else if (pmConn.getString("prog_code").equals(new BmoSFConfig().getProgramCode())) {
			table = new BmoSFConfig();
		} else if (pmConn.getString("prog_code").equals(new BmoSFInstance().getProgramCode())) {
			table = new BmoSFInstance();
		} else if (pmConn.getString("prog_code").equals(new BmoSFLog().getProgramCode())) {
			table = new BmoSFLog();
		} else if (pmConn.getString("prog_code").equals(new BmoSocial().getProgramCode())) {
			table = new BmoSocial();
		} else if (pmConn.getString("prog_code").equals(new BmoState().getProgramCode())) {
			table = new BmoState();
		} else if (pmConn.getString("prog_code").equals(new BmoTag().getProgramCode())) {
			table = new BmoTag();
		} else if (pmConn.getString("prog_code").equals(new BmoTitle().getProgramCode())) {
			table = new BmoTitle();
		} else if (pmConn.getString("prog_code").equals(new BmoUiColor().getProgramCode())) {
			table = new BmoUiColor();
		} else if (pmConn.getString("prog_code").equals(new BmoUser().getProgramCode())) {
			table = new BmoUser();
		} else if (pmConn.getString("prog_code").equals(new BmoUserAddress().getProgramCode())) {
			table = new BmoUserAddress();
		} else if (pmConn.getString("prog_code").equals(new BmoUserCompany().getProgramCode())) {
			table = new BmoUserCompany();
		} else if (pmConn.getString("prog_code").equals(new BmoUserContact().getProgramCode())) {
			table = new BmoUserContact();
		} else if (pmConn.getString("prog_code").equals(new BmoUserDate().getProgramCode())) {
			table = new BmoUserDate();
		} else if (pmConn.getString("prog_code").equals(new BmoUserEmail().getProgramCode())) {
			table = new BmoUserEmail();
		} else if (pmConn.getString("prog_code").equals(new BmoUserPhone().getProgramCode())) {
			table = new BmoUserPhone();
		} else if (pmConn.getString("prog_code").equals(new BmoUserRelative().getProgramCode())) {
			table = new BmoUserRelative();
		} else if (pmConn.getString("prog_code").equals(new BmoUserSocial().getProgramCode())) {
			table = new BmoUserSocial();
		} else if (pmConn.getString("prog_code").equals(new BmoUserTimeClock().getProgramCode())) {
			table = new BmoUserTimeClock();
		}
// 		Mauricio habia pedido, nombre del campo, id del campo, programa o componente donde esté, si esta habilitado, si es requerido y la esta consulta hacerla por instancia
// 		Si gustas en un excel de Drive por pestaña pones la instancia
		fields = table.getFieldList();
		Iterator<BmField> it = fields.iterator();
		while (it.hasNext()) {
			BmField bmField = (BmField) it.next();
			sql2 = "SELECT sfcp_foreignlisttitle FROM sfcaptions WHERE sfcp_fieldcode = '" + bmField.getName() + "'";
			String foreignListTitle = "-";
			boolean req = false;
			pmConn2.doFetch(sql2);
			
			if(pmConn2.next()){
				foreignListTitle = pmConn2.getString("sfcp_foreignlisttitle");				
			}
%>		
		<tr class="reportCellEven">
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn.getString("prog_name"), BmFieldType.CODE)) %>
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmField.getName(), BmFieldType.STRING)) %> 
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, sFParams.getFieldFormTitle(pmConn.getString("prog_code"),bmField), BmFieldType.STRING)) %> 
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, sFParams.getFieldListTitle(pmConn.getString("prog_code"),bmField), BmFieldType.STRING)) %> 			
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmField.getLabel(), BmFieldType.STRING)) %>
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, foreignListTitle, BmFieldType.STRING)) %>
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, sFParams.isFieldEnabled(bmField) ? "Si":"No", BmFieldType.BOOLEAN)) %>
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, sFParams.isFieldRequired(bmField) ? "Si":"No", BmFieldType.BOOLEAN)) %>
			
		</tr>

	<%}
	c++;
}
System.out.println("Numero de veces en el While " + c);
	
pmConn.close(); 
pmConn2.close();%>
</table>
</body>
</html>
