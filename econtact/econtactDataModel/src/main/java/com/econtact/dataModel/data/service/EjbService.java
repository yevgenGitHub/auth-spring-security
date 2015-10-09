package com.econtact.dataModel.data.service;

import java.util.List;

import com.econtact.dataModel.data.context.UserContext;
import com.econtact.dataModel.data.query.SearchCriteria;
import com.econtact.dataModel.data.util.UniqueConstraintException;
import com.econtact.dataModel.model.AbstractView;
import com.econtact.dataModel.model.entity.AbstractEntity;

public interface EjbService {
	<T extends AbstractView> T findById(Class<T> findClass, Object id);

	<T extends AbstractEntity> T saveOrUpdate(T entity, UserContext userContext) throws UniqueConstraintException;

	void remove(AbstractEntity entity, UserContext userContext);

	<T> T findSingleResult(SearchCriteria<T> criteria);

	<T> List<T> find(SearchCriteria<T> criteria);

	<T> List<T> find(SearchCriteria<T> criteria, Integer from, Integer count);

	<T> Long getRowCount(SearchCriteria<T> criteria);
}
