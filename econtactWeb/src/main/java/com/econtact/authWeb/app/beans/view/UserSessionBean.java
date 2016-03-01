package com.econtact.authWeb.app.beans.view;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.menu.MenuModel;
import org.springframework.security.core.context.SecurityContextHolder;

import com.econtact.authWeb.app.beans.helper.MenuHelper;
import com.econtact.dataModel.data.context.UserContext;
import com.econtact.dataModel.data.filter.FilterDefEquals;
import com.econtact.dataModel.data.query.GenericFilterDefQueries;
import com.econtact.dataModel.data.query.SearchCriteria;
import com.econtact.dataModel.data.service.GenericService;
import com.econtact.dataModel.data.util.EntityHelper;
import com.econtact.dataModel.model.entity.access.AccessChurchEntity;
import com.econtact.dataModel.model.entity.accout.ConfirmStatusEnum;
import com.econtact.dataModel.model.entity.accout.SessionUserEntity;

@Named
@SessionScoped
public class UserSessionBean implements Serializable {
	private static final long serialVersionUID = 5815150040159902787L;
	
	@EJB
	private GenericService genericService;
	
	@Inject
	private MenuHelper menuUtils;
	
	private SessionUserEntity principal;
	
	private UserContext userContext;
	
	private MenuModel topMenuModel;
	
	private Map<BigDecimal, AccessChurchEntity> churchAccess;
	
	@PostConstruct
	public void init() {
		principal = (SessionUserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		userContext = UserContext.create(getPrincipal(), TimeZone.getTimeZone("GMT+2"));
	}
	
	public MenuModel getTopMenuModel() {
		if (topMenuModel == null) {
			topMenuModel = menuUtils.buildTopMenu(getPrincipal().getRole());
		}
		return topMenuModel;
	}

	public SessionUserEntity getPrincipal() {
		return principal;
	}
	
	public UserContext getUserContext() {
		return userContext;
	}
	
	public AccessChurchEntity getChurchAccess(BigDecimal idChurch) {
		if (churchAccess == null) {
			churchAccess = new HashMap<BigDecimal, AccessChurchEntity>();
			SearchCriteria<AccessChurchEntity> criteria = new SearchCriteria<>(new GenericFilterDefQueries<>(AccessChurchEntity.class));
			criteria.andFilter(new FilterDefEquals(AccessChurchEntity.CONFIRM_A, true))
					.andFilter(new FilterDefEquals(AccessChurchEntity.USER_A, principal))
					.andFilter(new FilterDefEquals(EntityHelper.SIGN_A, EntityHelper.ACTUAL_SIGN));
			genericService.find(criteria).forEach(item -> churchAccess.put(item.getChurch().getId(), item));
		}
		return 	churchAccess.get(idChurch);
	}
}
