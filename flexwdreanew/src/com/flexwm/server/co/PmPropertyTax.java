package com.flexwm.server.co;

import com.symgae.server.PmObject;
import com.symgae.server.SFSendMail;
import com.symgae.server.HtmlUtil;
import com.symgae.server.PmConn;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFMailAddress;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoUser;

import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.GregorianCalendar;
import java.util.Iterator;

import com.flexwm.shared.co.BmoProperty;
import com.flexwm.shared.co.BmoPropertyTax;


public class PmPropertyTax extends PmObject {
	BmoPropertyTax bmoPropertyTax;

	public PmPropertyTax(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoPropertyTax = new BmoPropertyTax();
		setBmObject(bmoPropertyTax);
		
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoPropertyTax());
	}
	// recordatorio de pago predial solo se mandara cuando existan prediales con pago pendiente
	public void statusReminders() throws SFException {
		
		BmFilter pendingFilter = new BmFilter();
		PmProperty pmProperty = new PmProperty(getSFParams());
	
		pendingFilter.setValueFilter(bmoPropertyTax.getKind(), bmoPropertyTax.getStatus(), ""+BmoPropertyTax.PAYMENTSTATUS_PENDING);
		
//		Calendar calendar = new GregorianCalendar();
		
//		int day = calendar.get(Calendar.DATE);
//		int month = calendar.get(Calendar.MONTH);
		
		ArrayList<SFMailAddress> mailList = new ArrayList<SFMailAddress>();
		
//		if((day == 14) &&( month == 1 ) ){
			PmConn pmConn = new PmConn(getSFParams());
			pmConn.open();
			
			String sql = "SELECT user_email,user_firstname,user_fatherlastname,user_motherlastname FROM users " + 
						"LEFT JOIN profileusers ON (user_userid = pfus_userid) " + 
						"LEFT JOIN profiles ON (pfus_profileid = prof_profileid) " + 
						"WHERE prof_name LIKE  'Ejecutivo comercial' AND pfus_defaultuser = 1 AND user_status = '" + BmoUser.STATUS_ACTIVE + "'";
			
			pmConn.doFetch(sql);
			
			if(pmConn.next()) {
				
				mailList.add(new SFMailAddress(pmConn.getString("user_email"),
						pmConn.getString("user_firstname") + " " + pmConn.getString("user_fatherlastname") + " " + pmConn.getString("user_motherlastname")));
			}
			
			pmConn.close();
			
			String propertyTaxs = "";
			Iterator<BmObject> listIterator = this.list(pendingFilter).iterator();
			boolean existPropertyTax = false;
			while(listIterator.hasNext()) {
				BmoPropertyTax nextBmoPropertyTax = (BmoPropertyTax)listIterator.next();	
				BmoProperty bmoProperty = new BmoProperty();
				existPropertyTax = true;
				bmoProperty = (BmoProperty)pmProperty.get(nextBmoPropertyTax.getPropertyId().toInteger());
				
				propertyTaxs += "<b>" + getSFParams().getFieldFormTitle(nextBmoPropertyTax.getAccountNo())+ " :</b> "
						     + nextBmoPropertyTax.getAccountNo().toHtml() 
						     + "<br>"
						     + "<b>Inmueble: </b> " + bmoProperty.getCode().toString() + " " + bmoProperty.getDescription().toHtml() 
						     + "<br>"
							 + "<br>" ;
				
			}
			String subject = getSFParams().getAppCode() + " Pagos prediales pendientes ";
			
			String msgBody = HtmlUtil.mailBodyFormat(getSFParams(), "Pagos prediales pendientes:",
					" 	<p style=\"font-size:12px\"> " 
							+ propertyTaxs
							+ "	<br> "
							+ "	<p align=\"left\" style=\"font-size:12px\"> "
							+ " Este mensaje podría contener información confidencial, si tú no eres el destinatario por favor reporta esta situación a los datos de contacto "
							+ " y bórralo sin retener copia alguna." + "	</p> "
							
					);
					
			
			if(getSFParams().isProduction() && existPropertyTax) {
				SFSendMail.send(getSFParams(), mailList, getSFParams().getBmoSFConfig().getEmail().toString(),
						getSFParams().getBmoSFConfig().getAppTitle().toString(), subject, msgBody);
			}else if (!getSFParams().isProduction()  && existPropertyTax){
				printDevLog("statusReminders() - Mandando notificación : " + msgBody);
			}else {
				printDevLog(this.getClass() + " statusReminders() - No se mandara ningun correo");
			}
			
//		}
		
	
		
		
	}
}
