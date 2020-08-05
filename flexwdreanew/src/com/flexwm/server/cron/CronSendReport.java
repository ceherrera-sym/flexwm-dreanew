package com.flexwm.server.cron;

import java.util.ArrayList;
import java.util.Iterator;
import com.flexwm.server.PmSendReport;
import com.flexwm.shared.BmoSendReport;
import com.symgae.server.SFSendMail;
import com.symgae.server.SFServerUtil;
import com.symgae.server.cron.ICronJob;
import com.symgae.server.sf.PmProfileUser;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFException;
import com.symgae.shared.SFMailAddress;
import com.symgae.shared.SFParams;
import com.symgae.shared.sf.BmoUser;
import com.symgae.shared.sf.BmoProfileUser;


public class CronSendReport implements ICronJob {

	@Override
	public String init(SFParams sFParams) throws SFException {
		return execute(sFParams);
	}

	private String execute(SFParams sFParams) throws SFException {
		ArrayList<SFMailAddress> mailList = new ArrayList<SFMailAddress>();

		BmoSendReport bmoSendReport = new BmoSendReport();
		PmSendReport pmSendReport = new PmSendReport(sFParams);
		Iterator<BmObject> sendReportIterator = pmSendReport.list().iterator();

		while(sendReportIterator.hasNext()) {
			bmoSendReport = (BmoSendReport)sendReportIterator.next();
			//Obtener el reporte
			String url = GwtUtil.getProperUrl(sFParams, bmoSendReport.getLink().toString()) + "?w=EXT";				
			String data = SFServerUtil.fetchUrlToString(url);

			//Lista
			PmProfileUser pmProfileUser = new PmProfileUser(sFParams);
			BmoProfileUser bmoProfileUser = new BmoProfileUser();
			BmFilter groupFilter = new BmFilter();
			groupFilter.setValueFilter(bmoProfileUser.getKind(), bmoProfileUser.getProfileId(), bmoSendReport.getProfileId().toInteger());
			ArrayList<BmObject> groupEmailList = pmProfileUser.list(groupFilter);
			Iterator<BmObject> i = groupEmailList.iterator();
			int index = 0;
			while (i.hasNext()) {
				bmoProfileUser = (BmoProfileUser)i.next();
				// Agregar si el usuario esta activo
				if (bmoProfileUser.getBmoUser().getStatus().toChar() == BmoUser.STATUS_ACTIVE) {
					SFMailAddress mailAddress = new SFMailAddress(bmoProfileUser.getBmoUser().getEmail().toString(),
							bmoProfileUser.getBmoUser().getFirstname().toString() + " " +
									bmoProfileUser.getBmoUser().getFatherlastname().toString());
					mailList.add(mailAddress);
					index++;
				}
			}

			if (index == 0) throw new SFException("El grupo no tiene cuentas de correo asignadas.");

			String subject = bmoSendReport.getName().toString();

			//String msgBody = SFServerUtil.mailBodyFormat(sFParams, subject, data);
			String imgUrl = sFParams.getMainImageUrl();
			String msgBody = "";
			msgBody += " <HTML> "
					+ "<BODY> "
					+ "<TABLE bgcolor=\"#EDEDED\" width=\"100%\" height=\"100%\"> "
					+ "	<TR> "
					+ "		<TD width=\"100%\" heigh=\"100%\" valign=\"top\"> "
					+ "			<TABLE width=\"1000px\" style=\"font-family:verdana; font-size:10pt;\" align=\"center\" border=\"0\" bordercolor=\"silver\" cellpading=\"0\" cellspacing=\"0\"> "  
					+ "				<TR> "
					+ "					<TD width=\"75\" align=\"left\" height=\"75\" colspan=\"2\" valign=\"bottom\"> "  
					+ "						<img border=\"0\" width=\"" + SFParams.LOGO_WIDTH + "\" height=\"" + SFParams.LOGO_HEIGHT + "\" src=\"" + sFParams.getAppURL() + imgUrl +  "\"> " 
					+ "					</TD> "
					+ "				</TR> "
					+ "				<TR> "
					+ "					<TD bgcolor=\"black\"  align=\"center\" colspan=\"3\" height=\"35px\" style=\"font-size:14px; color:white\"> "  
					+ " 					<b>"
					+ 						subject
					+ "						</b> "
					+ "					</TD> "
					+ "				</TR> "
					+ "				<TR> "
					+ "					<TD bgcolor=\"white\" align=\"center\" width=\"50px\"> "  
					+ "							&nbsp; "
					+ "					 </TD> " 
					+ "					<TD bgcolor=\"white\" align=\"justify\" width=\"950px\" valign=\"top\"> "
					+ "						<br>" 
					+ 						data	
					+ "					</TD> "
					+ "					<TD bgcolor=\"white\" align=\"center\" width=\"50px\"> "  
					+ "							&nbsp;  "
					+ "					 </TD> "  
					+ "				</TR> "
					+ "				<TR> "
					+ "					<TD bgcolor=\"white\" colspan=\"3\" align=\"center\" height=\"40px\"> " 
					+ "						 &nbsp;	<br><br><br><br>"
					+ "					</TD> "
					+ "				</TR> "
					+ "				<TR> "
					+ "					<TD bgcolor=\"#EDEDED\" colspan=\"3\" align=\"center\" height=\"30px\"> " 
					+ "						 <br><a href=\"http://www.symetria.com.mx\" title=\"FlexWM Web-Based Management\" style=\"font-size:11px\"><img src=\"" + GwtUtil.getProperUrl(sFParams, "/img/main.png") + " \" height=\"25\"></a>  "
					+ "					</TD> "
					+ "				</TR> "
					+ "				<TR bgcolor=\"#EDEDED\" > " 
					+ "					<TD align=\"center\" width=\"100px\" height=\"50px\"> "  
					+ "							&nbsp;  "
					+ "					 </TD> " 
					+ "					<TD align=\"center\" style=\"font-size:9px\"> " 
					+ "						 &nbsp;©" + SFServerUtil.nowToString(sFParams, "YYYY") + " FlexWM, FlexWM Web-Based Management y el logo FlexWM "
					+ "						 son marcas registradas de Ctech Consulting S.A. de C.V. y Symetria Tecnológica S.A. de C.V. "
					+ "						  <br> "
					+ "						 Todos los Derechos Reservados. "
					+ "					</TD> "
					+ "					<TD align=\"center\" > "  
					+ "							&nbsp; " 
					+ "					 </TD> " 
					+ "				</TR> " 
					+ "				<TR bgcolor=\"#EDEDED\" > "
					+ "					<TD colspan=\"3\" align=\"center\" style=\"font-size:8px\" height=\"50px\"> " 
					+ "						 &nbsp; "
					+ "					</TD> "
					+ "				</TR> "
					+ "			</TABLE> "
					+ "		</TD> "
					+ "	</TR> "
					+ "</TABLE> "
					+ "</BODY> "  
					+ "</HTML> "
					; 
			//String msgBody = SFServerUtil.mailBodyFormat(sFParams, subject, data);				

			// Si hay destinatarios, enviar los correos
			if (mailList.size() > 0) SFSendMail.send(sFParams,
					mailList, 
					sFParams.getBmoSFConfig().getEmail().toString(), 
					sFParams.getMainAppTitle(), 
					subject, 
					msgBody);
		}
		return this.getClass().getName() + "-execute(): OK";
	}
}
