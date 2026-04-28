package com.vertex.backendcore.service;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@SuppressWarnings("unused")
public class TrxManager {

    TransactionTemplate transactionManager;

    public TrxManager(PlatformTransactionManager ptm) {
        this.transactionManager = new TransactionTemplate(ptm);
    }

    public void doInTransaction(Trx trx) {
        transactionManager.execute(status -> {
            trx.doInTransaction(status);
            return "ok";
        });
    }

    public <T> T execute(TransactionCallback<T> callback) {
        return transactionManager.execute(callback);
    }
}
