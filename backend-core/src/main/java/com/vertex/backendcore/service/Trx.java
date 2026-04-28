package com.vertex.backendcore.service;

import org.springframework.transaction.TransactionStatus;

public interface Trx {

    void doInTransaction(TransactionStatus status);

}