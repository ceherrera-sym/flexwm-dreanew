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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.symgae.client.LoginService;
import com.symgae.client.LoginServiceAsync;
import com.symgae.client.nodist.SFUiStart;
import com.symgae.client.ui.UiTemplateMaterialDesign;
import com.symgae.client.ui.UiTimeClock;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiTemplate;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFParams;
import com.symgae.shared.LoginInfo;
import com.symgae.shared.SFParamsService;
import com.symgae.shared.SFParamsServiceAsync;
import com.symgae.shared.sf.BmoProgram;
import com.symgae.shared.SFException;
import com.symgae.shared.SFLoginException;

/**
 * Entry point classes define <code>onProgramLoad()</code>.
 */
public class Flexwm implements EntryPoint {
	protected SFParamsServiceAsync sfParamsServiceAsync = GWT.create(SFParamsService.class);
	private LoginInfo loginInfo;
	private SFParams sFParams = new SFParams();
	private UiParams uiParams = new UiParams();

	// Llamada por default de entrada a GWT
	public void onModuleLoad() {
		fetchSFParams();
	}

	// Obtiene parametros iniciales
	public void fetchSFParams() {
		// Set up the callback object.
		AsyncCallback<SFParams> callback = new AsyncCallback<SFParams>() {
			public void onFailure(Throwable caught) {
				if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {}
				else Window.alert(this.getClass().getName() + "-fetchSFParams(): ERROR " + caught.toString());	
			}

			public void onSuccess(SFParams result) {
				setSFParams(result);
				channelLogin();
			}
		};

		try {
			sfParamsServiceAsync.fetchParams(callback);
		} catch (SFException e) {
			Window.alert("LIST Error: " + e.toString());
		}
	}

	// Revisa hacia donde hacer login
	public void channelLogin() {
		// Revisar sistema de autentificacion, google o interna
		// Autentificacion interna
		if (sFParams.getLoginInfo().isLoggedIn()) {
			// Si esta loggeado
			show();
		} else {
			// Enviar a login interno
			Window.Location.replace("./login.jsp");
		}	
	}

	// Realiza login via Autentificacion Google
	public void googleLogin() {
		// Check login status using login service.
		LoginServiceAsync loginService = GWT.create(LoginService.class);
		loginService.login(GWT.getHostPageBaseURL(), new AsyncCallback<LoginInfo>() {
			public void onFailure(Throwable error) {
				Window.alert(("Symgf-onProgramLoad(): Login service error: " + error.toString()));
				loadLogin();
			}

			public void onSuccess(LoginInfo result) {
				loginInfo = result;
				if (loginInfo.isLoggedIn()) {
					createParams();
				} else {
					loadLogin();
				}
			}
		});
	}

	// Obtiene y actualiza lista de permisos
	public void createParams() {
		// Llamada a servidor
		if (sfParamsServiceAsync == null) {
			sfParamsServiceAsync = GWT.create(SFParamsService.class);
		}

		// Set up the callback object.
		AsyncCallback<SFParams> callback = new AsyncCallback<SFParams>() {

			public void onFailure(Throwable caught) {
				if (caught instanceof SFLoginException) {
					Window.Location.replace("/jsp/error.jsp?errorLabel=Error de Login&errorText=El Usuario no se encuentra Registrado/Activo en el sistema.&errorException=" + caught.toString());
				} else if (caught instanceof SFException) {
					Window.Location.replace("/jsp/error.jsp?errorLabel=Error de Ingreso&errorText=No se puede Ingresar al Sistema por un error no esperado.&errorException=" + caught.toString());
				}
				else Window.alert("UNKNOWN EXCEPTION: " + caught.toString());	
			}

			public void onSuccess(SFParams result) {
				setSFParams(result);
				show();
			}
		};

		try {
			sfParamsServiceAsync.createParams(loginInfo, callback);
		} catch (SFException e) {
			Window.alert("LIST Error: " + e.toString());
		}
	}

	// Muestra los modulos
	public void show() {
		uiParams.setSFParams(sFParams);
		String forceMobile = Window.Location.getParameter("forcemobile");
		if (forceMobile == null)
			forceMobile = "0";

		// Detecta si es mobil el acceso
		if (GwtUtil.isMobile(Window.Navigator.getUserAgent())
				|| forceMobile.equals("1"))
			uiParams.setMobile(true);
		else 
			uiParams.setMobile(false);

		// Asigna el program factory a los parametros
		UiFlexwmProgramFactory uiFlexwmProgramFactory = new UiFlexwmProgramFactory(uiParams);
		uiParams.setUiProgramFactory(uiFlexwmProgramFactory);

		// Crear template a partir del seleccionado
		UiTemplate uiTemplate = new UiTemplateMaterialDesign(uiParams);

		// Revisa si el usuario tiene modulo de inicio default
		BmoProgram startProgram = uiParams.getSFParams().getStartProgram();

		// Revisa si hay parametros en navegador
		if ((Window.Location.getParameter("startprogram") != null || Window.Location.getParameter("startmodule") != null)
				&& Window.Location.getParameter("foreignid") != null) {
			int foreignId = Integer.parseInt(Window.Location.getParameter("foreignid"));
			String startProgramCode = Window.Location.getParameter("startprogram");
			// Si quieren entrar con startmodule "redireccionar"
			String startModuleCode = Window.Location.getParameter("startmodule");
			if (startProgramCode == null && !startModuleCode.equals(""))
				startProgramCode = startModuleCode;

			uiFlexwmProgramFactory.showProgram(startProgramCode, foreignId);

		} else if (startProgram != null && !startProgram.getCode().toString().equals("")) {
			// Inicia con el modulo default del usuario
			uiFlexwmProgramFactory.showProgram(startProgram.getCode().toString());
		} else {
			startProgram = uiParams.getSFParams().getBmoSFConfig().getBmoStartProgram();
			// Recuperar el modulo de inicio default
			if (startProgram != null && !startProgram.getCode().toString().equals("")) {
				uiFlexwmProgramFactory.showProgram(startProgram.getCode().toString());
			} else {
				// Modulo default
				SFUiStart sfUiStart = new SFUiStart(uiParams);
				uiTemplate.setUiStart(sfUiStart);	

				// Mostrar template
				uiTemplate.show();
			}
		}

		// Rastreo de Tiempo
		//		if (uiParams.getSFParams().getBmoSFConfig().getEnableUserTimeClock().toBoolean()) {
		//			new UiWFlowTimeTrackDialog(uiParams, uiTemplate.getTopEastPanel());
		//			uiTemplate.getTopEastPanel().add(new HTML("&nbsp"));
		//		}

		// Reloj Checador
		if (uiParams.getSFParams().getBmoSFConfig().getEnableUserTimeClock().toBoolean())
			new UiTimeClock(uiParams, uiTemplate.getTopEastPanel());
	}

	// Sale de la aplicacion
	private void loadLogin() {
		// Enviar a pantalla de login
		Window.Location.replace(loginInfo.getLoginUrl());
	}

	public void setSFParams(SFParams sFParams) {
		this.sFParams = sFParams;
	}
}