package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {

    private final TransactionHandler transactionHandler;

    public TransactionListener(TransactionHandler transactionHandler) {
        this.transactionHandler = transactionHandler;
    }

    @KafkaListener(topics = "${general.kafka-topic}")
    public void listen(Transaction transaction) {
        transactionHandler.handleTransaction(transaction);
    }
}