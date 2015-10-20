package com.econtact.authWeb.app.helpers;

import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

public class LabelsHelper {
	private static final ResourceBundle bundle = FacesContext.getCurrentInstance().getApplication().getResourceBundle(FacesContext.getCurrentInstance(), "viewMsg");
	
	public static String getLocalizedMessage(String key) {
		return bundle.getString(key);
	}
	
	private LabelsHelper() {
		
	}
}
