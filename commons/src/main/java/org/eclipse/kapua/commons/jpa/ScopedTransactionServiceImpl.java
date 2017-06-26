/*******************************************************************************
 * Copyright (c) 2011, 2017 Eurotech and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.commons.jpa;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaRuntimeException;
import org.eclipse.kapua.commons.core.ComponentProvider;
import org.eclipse.kapua.locator.inject.Service;
import org.eclipse.kapua.model.KapuaEntity;
import org.eclipse.kapua.model.id.KapuaId;

@ComponentProvider
@Service(provides = {EntityManager.class, ScopedTransactionService.class})
public class ScopedTransactionServiceImpl implements EntityManager, ScopedTransactionService {
    
    ThreadLocal<ScopedTransactionContext> txnContextThdLocal = new ThreadLocal<>();
    ThreadLocal<Integer> referenceCountThdLocal = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };
    
    public ScopedTransactionServiceImpl() {    
    }
    
    @Override
    public void begin(EntityManagerFactory factory) throws KapuaException {
        if (txnContextThdLocal.get() == null) {
            EntityManager em = factory.createEntityManager();
            txnContextThdLocal.set(new ScopedTransactionContext(factory, em));
        }
        
        ScopedTransactionContext ctx = txnContextThdLocal.get();
        if (!ctx.getEntityManagerFactory().getClass().equals(factory.getClass())) {
            throw KapuaRuntimeException.internalError(
                    String.format("Detected a tentative to open a nested transaction using %s,"
                            + " nested distributed transactions are not allowed", factory.getClass().getName()));
        }
        
        referenceCountThdLocal.set(referenceCountThdLocal.get()+1);
    }
    
    @Override
    public EntityManager get() {
        ScopedTransactionContext ctx = txnContextThdLocal.get();
        return ctx.getEntityManager();
    }

    @Override
    public void end() {
        if (referenceCountThdLocal.get() > 0) {
            referenceCountThdLocal.set(referenceCountThdLocal.get()-1);
            if (referenceCountThdLocal.get() == 0) {
                if (txnContextThdLocal.get() != null) {
                    ScopedTransactionContext ctx = txnContextThdLocal.get();
                    ctx.getEntityManager().close();
                    txnContextThdLocal.set(null);
                }
            }
        }
    }

    @Override
    public void beginTransaction() throws KapuaException {
        ScopedTransactionContext ctx = txnContextThdLocal.get();
        ctx.getEntityManager().beginTransaction();
    }

    @Override
    public void commit() throws KapuaException {
        ScopedTransactionContext ctx = txnContextThdLocal.get();
        ctx.getEntityManager().commit();
    }

    @Override
    public void rollback() {
        ScopedTransactionContext ctx = txnContextThdLocal.get();
        ctx.getEntityManager().rollback();
    }

    @Override
    public boolean isTransactionActive() {
        ScopedTransactionContext ctx = txnContextThdLocal.get();
        return ctx.getEntityManager().isTransactionActive();
    }

    @Override
    public void close() {
        ScopedTransactionContext ctx = txnContextThdLocal.get();
        ctx.getEntityManager().close();
    }

    @Override
    public <E extends KapuaEntity> void persist(E entity) {
        ScopedTransactionContext ctx = txnContextThdLocal.get();
        ctx.getEntityManager().persist(entity);
    }

    @Override
    public void flush() {
        ScopedTransactionContext ctx = txnContextThdLocal.get();
        ctx.getEntityManager().flush();
    }

    @Override
    public <E extends KapuaEntity> E find(Class<E> clazz, KapuaId id) {
        ScopedTransactionContext ctx = txnContextThdLocal.get();
        return ctx.getEntityManager().find(clazz, id);
    }

    @Override
    public <E extends KapuaEntity> void merge(E entity) {
        ScopedTransactionContext ctx = txnContextThdLocal.get();
        ctx.getEntityManager().merge(entity);
    }

    @Override
    public <E extends KapuaEntity> void refresh(E entity) {
        ScopedTransactionContext ctx = txnContextThdLocal.get();
        ctx.getEntityManager().refresh(entity);
    }

    @Override
    public <E extends KapuaEntity> void remove(E entity) {
        ScopedTransactionContext ctx = txnContextThdLocal.get();
        ctx.getEntityManager().remove(entity);
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        ScopedTransactionContext ctx = txnContextThdLocal.get();
        return ctx.getEntityManager().getCriteriaBuilder();
    }

    @Override
    public <E> TypedQuery<E> createQuery(CriteriaQuery<E> criteriaSelectQuery) {
        ScopedTransactionContext ctx = txnContextThdLocal.get();
        return ctx.getEntityManager().createQuery(criteriaSelectQuery);
    }

    @Override
    public <E> TypedQuery<E> createNamedQuery(String queryName, Class<E> clazz) {
        ScopedTransactionContext ctx = txnContextThdLocal.get();
        return ctx.getEntityManager().createNamedQuery(queryName, clazz);
    }

    @Override
    public <E> Query createNativeQuery(String querySelectUuidShort) {
        ScopedTransactionContext ctx = txnContextThdLocal.get();
        return ctx.getEntityManager().createNativeQuery(querySelectUuidShort);
    }
}