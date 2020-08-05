package com.flexwm.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import com.flexwm.server.ac.PmSession;
import com.flexwm.server.cm.PmCustomer;
import com.flexwm.server.cm.PmCustomerDate;
import com.flexwm.server.cm.PmProject;
import com.flexwm.server.cm.PmQuote;
import com.flexwm.server.cm.PmQuoteGroup;
import com.flexwm.server.cm.PmQuoteItem;
import com.flexwm.server.wf.PmWFlowStepType;
import com.flexwm.shared.ac.BmoSession;
import com.flexwm.shared.ac.BmoSessionSale;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoCustomerDate;
import com.flexwm.shared.cm.BmoProject;
import com.flexwm.shared.cm.BmoQuote;
import com.flexwm.shared.cm.BmoQuoteGroup;
import com.flexwm.shared.cm.BmoQuoteItem;
import com.flexwm.shared.wf.BmoWFlowStepType;
import com.symgae.server.HtmlUtil;
import com.symgae.server.PmConn;
import com.symgae.server.PmFlex;
import com.symgae.server.SFSendMail;
import com.symgae.server.SFServerUtil;
import com.symgae.server.sf.PmArea;
import com.symgae.server.sf.PmProfile;
import com.symgae.server.sf.PmLocation;
import com.symgae.server.sf.PmUser;
import com.symgae.shared.BmError;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.BmoFlex;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoArea;
import com.symgae.shared.sf.BmoProfile;
import com.symgae.shared.sf.BmoLocation;
import com.symgae.shared.sf.BmoUser;
import com.symgae.shared.sf.BmoProfileUser;


public class FlexTest {

	public static void main(String[] args) {
		PmConn pmConn;

		try {
			SFParams sFParams = SFServerUtil.createSFParamsFromXML("/home/mlopez/git/symgae-gwt/symgae/war/WEB-INF/web.xml");
			
			BmUpdateResult bmUpdateResult = new BmUpdateResult();
			PmUser pmUser = new PmUser(sFParams);
			BmoUser bmoUser = (BmoUser)pmUser.get(1);
			sFParams.getLoginInfo().setBmoUser(bmoUser);
			
			bmoUser.getBirthdate().setValue("2019-04-09");
			pmUser.save(bmoUser, bmUpdateResult);
			
			bmoUser = (BmoUser)pmUser.get(1);
			
			pmConn = new PmConn(sFParams);
			pmConn.open();
			pmConn.doFetch("SELECT * FROM users WHERE user_userid = 1");
			pmConn.next();
			System.out.println("bd fecha nac: " + pmConn.getString("user_birthdate"));
			
			System.out.println(bmUpdateResult.errorsToString());
			
			pmConn.close();
		
		} catch (Exception e) {
			System.out.println("FlexTest() Error: " + e.toString());
		} finally {
			
		}
	}
	
	public static void testSessions(SFParams sFParams, PmConn pmConn, BmUpdateResult bmUpdateResult) throws SFException {
		PmSession pmSession = new PmSession(sFParams);
		
		System.out.println("iniciando prueba sesiones");
		
		// Datos Venta de Sesion
		BmoSessionSale bmoSessionSale = new BmoSessionSale();
		bmoSessionSale.getWFlowTypeId().setValue(1);
		bmoSessionSale.getWFlowId().setValue(367);
		bmoSessionSale.getCustomerId().setValue(1);
		bmoSessionSale.getSessionTypePackageId().setValue(1);
		bmoSessionSale.getStartDate().setValue("2018-06-01 00:00:00");
		bmoSessionSale.getOrderId().setValue(259);
		
		// Datos Sesion a crear,
		BmoSession bmoSession = new BmoSession();
		bmoSession.getUserId().setValue(41);
		bmoSession.getStartDate().setValue("2018-06-01 18:30:00");
		bmoSession.getSessionTypeId().setValue(1);
		bmoSession.getSeriesMonday().setValue(1);
		bmoSession.getSeriesTuesday().setValue(0);		
		bmoSession.getSeriesWednesday().setValue(1);
		bmoSession.getSeriesThursday().setValue(0);
		bmoSession.getSeriesFriday().setValue(1);
		bmoSession.getSeriesSaturday().setValue(0);
		bmoSession.getSeriesSunday().setValue(0);
		pmSession.createAllOrderSessions(pmConn, bmoSessionSale, bmoSession, bmUpdateResult);
	}
	
	
	public static SOAPMessage createSOAPRequest() throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        String serverURI = "http://www.banxico.org.mx:80/DgieWSWeb/DgieWS?WSDL";

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // SOAP Body
        SOAPBody soapBody = envelope.getBody();

        SOAPElement soapBodyElem = soapBody.addChildElement("TITULO");

        SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("divisa");
        soapBodyElem1.addTextNode("USD");

        SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("tipo");
        soapBodyElem2.addTextNode("VENTA");

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", serverURI );

        soapMessage.saveChanges();

        /* Print the request message */
        System.out.print("Request SOAP Message = ");
        soapMessage.writeTo(System.out);
        System.out.println();

        return soapMessage;
    }

	// Enviar recordatorios de cumpleaños y fechas importantes del cliente
	public static void sendReminders(SFParams sFParams, int month, int day) throws SFException{
		BmoCustomer bmoCustomer = new BmoCustomer();
		PmUser pmUser = new PmUser(sFParams);
		PmCustomer pmCustomer = new PmCustomer(sFParams);

		// Obtener los cumpleaños del día
		BmFilter filterByDay = new BmFilter();
		filterByDay.setDayMonthFilter(bmoCustomer.getKind(), bmoCustomer.getBirthdate().getName(), bmoCustomer.getBirthdate().getName(), "" + month, "" + day);

		Iterator<BmObject> customerIterator = pmCustomer.list(filterByDay).iterator();

		while (customerIterator.hasNext()) {
			bmoCustomer = (BmoCustomer)customerIterator.next();
			// Si es cliente personal, enviar correo al vendedor
			if (bmoCustomer.getCustomertype().toChar() == BmoCustomer.TYPE_PERSON) {
				BmoUser bmoUser = (BmoUser)pmUser.get(bmoCustomer.getSalesmanId().toInteger());

				sendMailReminder(sFParams, bmoCustomer, bmoUser, "Cumpleaños");
			}
		}

		// Obtener las fechas especiales del día
		BmoCustomerDate bmoCustomerDate = new BmoCustomerDate();
		BmFilter filterSpecialDateByDay = new BmFilter();
		filterSpecialDateByDay.setDayMonthFilter(bmoCustomerDate.getKind(), bmoCustomerDate.getRelevantDate().getName(), bmoCustomerDate.getRelevantDate().getName(), "" + month, "" + day);
		PmCustomerDate pmCustomerDate = new PmCustomerDate(sFParams);
		Iterator<BmObject> customerDateIterator = pmCustomerDate.list(filterSpecialDateByDay).iterator();
		while (customerDateIterator.hasNext()) {
			bmoCustomerDate = (BmoCustomerDate)customerDateIterator.next();

			String motive = "Otros";
			if (bmoCustomerDate.getType().toChar() == BmoCustomerDate.TYPE_ANNIVERSARY) motive = "Aniversario";
			else if (bmoCustomerDate.getType().toChar() == BmoCustomerDate.TYPE_BIRTHDAY) motive = "Cumpleaños";

			// Si es cliente personal, enviar correo al vendedor
			bmoCustomer = (BmoCustomer)pmCustomer.get(bmoCustomerDate.getCustomerId().toInteger());
			BmoUser bmoUser = (BmoUser)pmUser.get(bmoCustomer.getSalesmanId().toInteger());
			sendMailReminder(sFParams, bmoCustomer, bmoUser, motive);

		}
	}

	public static void sendMailReminder(SFParams sFParams, BmoCustomer bmoCustomer, BmoUser bmoUser, String motive) throws SFException {
		String subject = sFParams.getAppCode() + " Recordatorio de " + motive + " del Cliente " + bmoCustomer.getCode() + " " + bmoCustomer.getDisplayName();

		String msgBody = HtmlUtil.mailBodyFormat(sFParams, subject, 
				" Recordatorio de  " + motive + 
				" del Cliente " + bmoCustomer.getCode() + " " + bmoCustomer.getDisplayName() +
				" para el día de hoy: " + SFServerUtil.nowToString(sFParams, sFParams.getDateFormat()));

		// Enviar correo si el usuario esta activo
		if (bmoUser.getStatus().toChar() == BmoUser.STATUS_ACTIVE) {
			SFSendMail.send(sFParams,
					bmoUser.getEmail().toString(), bmoUser.getFirstname() + " " + bmoUser.getFatherlastname(),  
					sFParams.getBmoSFConfig().getEmail().toString(), 
					sFParams.getBmoSFConfig().getAppTitle().toString(), 
					subject, 
					msgBody);
		}
	}


	public static void testProject(SFParams sFParams) throws Exception {
		PmProject pmProject = new PmProject(sFParams);
		BmoProject bmoProject = new BmoProject();

		bmoProject.getName().setValue("Boda X");
		bmoProject.getWFlowTypeId().setValue(1);
		bmoProject.getCustomerId().setValue(1);
		bmoProject.getStartDate().setValue("2014-02-20 19:30");
		bmoProject.getEndDate().setValue("2014-02-20 19:30");

		BmUpdateResult bmUpdateResult = new BmUpdateResult();

		pmProject.save(bmoProject, bmUpdateResult);

		System.out.println(bmUpdateResult.errorsToString());
	}

	public static void testQuoteGroup(SFParams sFParams) throws Exception {
		BmoQuoteGroup bmoQuoteGroup = new BmoQuoteGroup();
		bmoQuoteGroup.getCode().setValue("Code");
		bmoQuoteGroup.getName().setValue("Name");
		bmoQuoteGroup.getShowQuantity().setValue(1);
		bmoQuoteGroup.getQuoteId().setValue(1);
		bmoQuoteGroup.getAmount().setValue(0);

		PmQuoteGroup pmQuoteGroup = new PmQuoteGroup(sFParams);
		BmUpdateResult bmUpdateResult = new BmUpdateResult();
		pmQuoteGroup.save(bmoQuoteGroup, bmUpdateResult);
		System.out.println(bmUpdateResult.errorsToString());

	}

	public static Date convertFromGmt(Date source) {
		TimeZone timeZoneServer = TimeZone.getTimeZone("GMT -0600");
		long rawOffset = timeZoneServer.getRawOffset();
		Date dest = new Date(source.getTime() + rawOffset);
		return dest;
	}

	public static Date convertToGmt(Date source) {
		int rawOffset = TimeZone.getDefault().getRawOffset();
		Date dest = new Date(source.getTime() - rawOffset);
		return dest;
	}

	public static void testKitAction(SFParams sFParams) throws Exception {
		BmoQuoteGroup bmoQuoteGroup = new BmoQuoteGroup();
		PmQuoteGroup pmQuoteGroup = new PmQuoteGroup(sFParams);
		BmUpdateResult bmUpdateResult = new BmUpdateResult();

		bmoQuoteGroup = (BmoQuoteGroup)pmQuoteGroup.get(1);

		pmQuoteGroup.action(bmoQuoteGroup, bmUpdateResult, BmoQuoteGroup.ACTION_PRODUCTKIT, "1");

		System.out.println(bmUpdateResult.errorsToString());
	}

	public static void testQuoteItem(SFParams sFParams) throws Exception {
		BmoQuoteItem bmoQuoteItem = new BmoQuoteItem();
		PmQuoteItem pmQuoteItem = new PmQuoteItem(sFParams);

		//		bmoQuoteItem = (BmoQuoteItem)pmQuoteItem.get(19);

		double q = 13;

		bmoQuoteItem.getQuantity().setValue(q);
		bmoQuoteItem.getQuoteGroupId().setValue(1);
		bmoQuoteItem.getProductId().setValue(1);
		bmoQuoteItem.getPrice().setValue(450);

		BmUpdateResult bmUpdateResult = new BmUpdateResult();

		pmQuoteItem.save(bmoQuoteItem, bmUpdateResult);

		System.out.println(bmUpdateResult.errorsToString());
	}

	public static void testQuoteGetBy(SFParams sFParams) throws Exception {
		BmoQuote bmoQuote = new BmoQuote();
		PmQuote pmQuote = new PmQuote(sFParams);

		bmoQuote = (BmoQuote)pmQuote.getBy(6, "proj_projectid");
		System.out.println("Quote: " + bmoQuote.getId());
	}

	public static void testCreateProject(SFParams sFParams) throws Exception {
		BmoProject bmoProject = new BmoProject();
		PmProject pmProject = new PmProject(sFParams);
		BmUpdateResult bmUpdateResult = new BmUpdateResult();

		bmoProject.getCode().setValue("Boda A");
		bmoProject.getName().setValue("Boda A");
		bmoProject.getStartDate().setValue("2013-12-01 14:30:00");
		bmoProject.getEndDate().setValue("2013-12-01 18:30:00");
		bmoProject.getStatus().setValue(BmoProject.STATUS_REVISION);
		bmoProject.getCustomerId().setValue(1);
		bmoProject.getWFlowTypeId().setValue(1);

		pmProject.save(bmoProject, bmUpdateResult);

		System.out.println(bmUpdateResult.errorsToString());
	}

	public static void testIdNull(SFParams sFParams) throws Exception {
		BmoCustomer bmoCustomer = new BmoCustomer();
		BmUpdateResult bmUpdateResult = new BmUpdateResult();
		PmCustomer pmCustomer = new PmCustomer(sFParams);

		bmoCustomer.getFirstname().setValue("Carlos");
		bmoCustomer.getFatherlastname().setValue("Gutierrez");
		bmoCustomer.getRfc().setValue("DFG44554");
		bmoCustomer.getCustomertype().setValue("C");

		pmCustomer.save(bmoCustomer, bmUpdateResult);
		System.out.println("Lista de errores: " + bmUpdateResult.errorsToString());
	}



	public static void testCreateWFlowStep(SFParams sFParams) throws Exception {
		BmoWFlowStepType bmoWFlowStepType = new BmoWFlowStepType();
		bmoWFlowStepType.setId(0);
		bmoWFlowStepType.getName().setValue("Verificacion datos");
		bmoWFlowStepType.getDescription().setValue("Verificacion de datos");
		bmoWFlowStepType.getSequence().setValue(2);
		bmoWFlowStepType.getProfileId().setValue(1);
		bmoWFlowStepType.getWFlowTypeId().setValue(3);
		bmoWFlowStepType.getWFlowPhaseId().setValue(3);

		PmWFlowStepType pm = new PmWFlowStepType(sFParams);

		BmUpdateResult sr = new BmUpdateResult();

		pm.save(bmoWFlowStepType, sr);

		System.out.println(sr.errorsToString());
	}

	public static void testNoTransaction(SFParams sFParams) throws Exception {
		PmLocation pm = new PmLocation(sFParams);

		PmConn pmConn = new PmConn(sFParams);
		pmConn.open();

		BmoLocation bmoLocation = new BmoLocation();
		bmoLocation.getName().setValue("1xxxx");

		pm.save(pmConn, bmoLocation, new BmUpdateResult());

		pmConn.close();

		//		System.out.println("Mensaje: " + bmUpdateResult.hasErrors());
		//		
		//		pmConn.open();
		//		BmoLocation bmoLocation2 = new BmoLocation();
		//		bmoLocation2.getCode().setValue("2xxx");
		//		bmoLocation2.getName().setValue("2xxxx");
		//		
		//		pm.save(pmConn, bmoLocation2, new BmUpdateResult());
		//		pmConn.close();

	}

	public static void testTransaction(SFParams sFParams) throws Exception {

		//PmLocation = new PmLocation(sFParams);
		PmLocation pm = new PmLocation(sFParams);

		// Con transacciones
		PmConn pmConn = new PmConn(sFParams);		
		try {
			pmConn.open();
			pmConn.disableAutoCommit();

			BmoLocation bmoLocation = new BmoLocation();
			bmoLocation.getName().setValue("1xxxx");

			BmUpdateResult bmUpdateResult = pm.save(pmConn, bmoLocation, new BmUpdateResult());

			System.out.println("Mensaje: " + bmUpdateResult.hasErrors() +  ", id almacenado: " + bmUpdateResult.getId());


			BmoLocation bmoLocation2 = new BmoLocation();
			bmoLocation2.getName().setValue("2xxxx");

			pm.save(pmConn, bmoLocation2, new BmUpdateResult());

			pmConn.commit();

		} catch (SFPmException e) {
			System.out.println("Error: " + e.toString());
			pmConn.rollback();
		} finally {
			pmConn.close();
		}

	}

	public static void testArea(SFParams sFParams) throws Exception {
		BmoArea bmoArea = new BmoArea();

		bmoArea.getName().setValue("add test dd");

		PmArea pmArea = new PmArea(sFParams);

		BmUpdateResult bmUpdateResult = pmArea.save(bmoArea, new BmUpdateResult());

		if (!bmUpdateResult.hasErrors() && bmUpdateResult.getBmErrorList() == null && bmUpdateResult.getBmErrorList().size() < 1) {
			System.out.println("Exito, id : " + bmUpdateResult.getId());

		} else {
			if (bmUpdateResult.getBmErrorList().size() > 0) {
				Iterator<BmError> iterator = bmUpdateResult.getBmErrorList().iterator();
				while (iterator.hasNext()) {
					BmError bmError = (BmError)iterator.next();
					System.out.println("Error: " + bmError.getCode() + ", " + bmError.getMsg());
				}
			} else {
				// El error es del objeto completo

				System.out.println("UiForm-processUpdateResult(): Error al guardar: " + bmUpdateResult.getMsg());
			}
		}

	}

	//	public static void testBmoProject(SFParams sFParams) throws Exception {
	//		BmoProject bmoProject = new BmoProject();
	//		
	//		PmProject pmProject = new PmProject(sFParams);
	//		
	//		bmoProject = (BmoProject)pmProject.get(1);
	//		
	//		System.out.println("Project: " + bmoProject.getCode().toString() +
	//				" status = " + bmoProject.getStatus().toChar()
	//				);
	//		
	//		bmoProject.getStatus().setValue(BmoProject.ACTIVE);
	//		
	//		pmProject.save(bmoProject, new BmUpdateResult());
	//		
	//	}

	public static void testFlexList(SFParams sFParams) throws Exception {
		PmFlex pmFlex = new PmFlex(sFParams);
		BmoFlex bmoFlex = new BmoFlex();

		ArrayList<BmObject> list = pmFlex.flexList(
				"select name as Area, count(userid) as Usuarios from users left join areas on (users.areaid = areas.areaid) group by name"
				);

		System.out.println("El flex es: " + list.size());

		ArrayList<BmField> fields = bmoFlex.getFieldList();
		Iterator<BmObject> iterator = list.iterator();


		if (iterator.hasNext()) {
			BmObject cellBmObject = (BmObject)iterator.next(); 
			fields = cellBmObject.getDisplayFieldList();

			Iterator<BmField> itf = fields.iterator();

			String row = "";
			while (itf.hasNext()) {
				BmField cellBmField = (BmField)itf.next();
				row += "     " + cellBmField.getLabel() + "(" + cellBmField.getSqlType() + ")";
			}
			System.out.println(row);
		}

		iterator = list.iterator();
		while (iterator.hasNext()) {
			BmObject cellBmObject = (BmObject)iterator.next(); 
			fields = cellBmObject.getDisplayFieldList();

			Iterator<BmField> itf = fields.iterator();

			String row = "";
			while (itf.hasNext()) {
				BmField cellBmField = (BmField)itf.next();
				row += "     " + cellBmField.toString();
			}
			System.out.println(row);
		}

	}

	public static void testGroups(SFParams sFParams) throws Exception {
		PmProfile pmProfile = new PmProfile(sFParams);
		BmoProfileUser bmoProfileUser = new BmoProfileUser();
		BmoProfile bmoProfile = new BmoProfile();

		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueLabelFilter(bmoProfileUser.getKind(), 
				bmoProfileUser.getBmoProfile().getIdFieldName(), 
				bmoProfileUser.getBmoProfile().getProgramLabel(), 
				BmFilter.EQUALS, 
				bmoProfile.getId(), 
				bmoProfile.getName().toString());
		ArrayList<BmObject> s = pmProfile.list(bmFilter);

		System.out.println("El grupo es: " + s.size());
	}

	//	public static void testUsers(SFParams sFParams) throws Exception {
	//		PmUser pmUser = new PmUser(sFParams);
	//		BmoUser newBmoUser = new BmoUser();
	//		newBmoUser.getFirstname().setValue("Carlos");
	//		newBmoUser.getFatherlastname().setValue("Perez");
	//		newBmoUser.getMotherlastname().setValue("Cota");
	//		newBmoUser.getBirthdate().setValue("1977-10-10");
	//		newBmoUser.getBmoArea().setId(1);
	//		newBmoUser.getBmoLocation().setId(1);
	//		
	//		int id = pmUser.save(newBmoUser);
	//
	//		BmoUser bmoUser = (BmoUser)pmUser.get(id);
	//
	//		System.out.println("El id es: " + id + ", El usuario es: " + bmoUser.getEmail().toString() + "\n su cumplea���os es: " + bmoUser.getBirthdate().toString() + "\n su fecha de creacion es: " + bmoUser.getSFLog().toString());
	//
	//	}



}
