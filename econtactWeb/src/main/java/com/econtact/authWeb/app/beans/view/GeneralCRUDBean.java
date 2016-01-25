package com.econtact.authWeb.app.beans.view;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;

import org.apache.commons.lang.StringUtils;

import com.econtact.authWeb.app.beans.helper.LabelsHelper;
import com.econtact.authWeb.app.beans.helper.NavigationHelper;
import com.econtact.authWeb.app.constraint.ContraintViewRelation;
import com.econtact.authWeb.app.utils.UniqueConstraintHandleUtils;
import com.econtact.dataModel.data.service.GenericService;
import com.econtact.dataModel.data.util.UniqueConstraintException;
import com.econtact.dataModel.model.entity.AbstractEntity;

public abstract class GeneralCRUDBean<T extends AbstractEntity> implements Serializable {
	private static final long serialVersionUID = 1839876621381844278L;

	@Inject
	protected NavigationHelper navigationHelper;
	
	@Inject
	LabelsHelper labelsHelper;
	
	@Inject
	protected UserSessionBean userSession;
	
	@EJB
	GenericService genericService;
	
	protected T entity;
	private Class<T> entityClass;
	
	@PostConstruct
	public void init() throws IOException {
		entityClass = (Class<T>) getParameterClass( 0, getClass());
		String idParam = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(NavigationHelper.ID_PARAM);
		if (StringUtils.isNotBlank(idParam)) {
			T entity = genericService.findById(entityClass, BigDecimal.valueOf(Long.parseLong(idParam)), getDefaultEntityGraph()); 
			if (canModifyEntity(entity)) {
				setEntity(entity);
			} else {
				navigationHelper.navigate(NavigationHelper.MODIFY_NOT_ALLOWED_PAGE);
			}
		} else {
			setEntity(createDefaultEntity());
		}
	}
	
	public T getEntity() {
		return entity;
	}

	public void setEntity(T entity) {
		this.entity = entity;
	}
	
	
	public void save() throws IOException {
		preSave();
		try {
			genericService.saveOrUpdate(entity, userSession.getUserContext());
			afterSaveNavigate();
		} catch (EJBException e) {
			if (e.getCause() instanceof OptimisticLockException) {
				//TODO handle optimistick lock exception
				
			} else {
				throw e;
			}
		} catch (UniqueConstraintException e) {
			ContraintViewRelation relation = UniqueConstraintHandleUtils.getInstance().handleException(e);
			FacesMessage errorMessage = new FacesMessage(labelsHelper.getLocalizedMessage(relation.getErrorMessageKey()));
			errorMessage.setSeverity(FacesMessage.SEVERITY_WARN);
			FacesContext.getCurrentInstance().addMessage(null, errorMessage);
		}
	}
	
	public void cancel() throws IOException {
		cancelNavigate();
	}
	
	protected void afterSaveNavigate() throws IOException {
		navigationHelper.navigate(navigationHelper.getIndexPage());
	}
	
	protected void cancelNavigate() throws IOException {
		navigationHelper.navigate(navigationHelper.getIndexPage());
	}
	
	protected void preSave() {
	}
	
	protected String getDefaultEntityGraph() {
		return null;
	}
	
	abstract protected boolean canModifyEntity(T entity);
	
	abstract protected T createDefaultEntity();	
	
	private Class<?> getParameterClass(int pos, Class<?> target) {
		return (Class<?>) ((ParameterizedType) target.getGenericSuperclass())
				.getActualTypeArguments()[pos];
	}
}
