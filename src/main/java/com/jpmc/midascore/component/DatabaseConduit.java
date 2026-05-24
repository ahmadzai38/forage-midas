package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConduit {

    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;

    public DatabaseConduit(UserRepository userRepository,
                           TransactionRecordRepository transactionRecordRepository) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
    }

    public void save(UserRecord userRecord) {
        userRepository.save(userRecord);
    }

    public UserRecord queryUser(long userId) {
        return userRepository.findById(userId);
    }

    public boolean isValid(Transaction transaction) {
        UserRecord sender = queryUser(transaction.getSenderId());
        UserRecord recipient = queryUser(transaction.getRecipientId());

        if (sender == null) {
            return false;
        }

        if (recipient == null) {
            return false;
        }

        return sender.getBalance() >= transaction.getAmount();
    }

    @Transactional
    public void save(Transaction transaction) {
        UserRecord sender = queryUser(transaction.getSenderId());
        UserRecord recipient = queryUser(transaction.getRecipientId());

        TransactionRecord transactionRecord =
                new TransactionRecord(
                        sender,
                        recipient,
                        transaction.getAmount(),
                        transaction.getIncentive()
                );

        transactionRecordRepository.save(transactionRecord);

        sender.setBalance(sender.getBalance() - transaction.getAmount());

        recipient.setBalance(
                recipient.getBalance()
                        + transaction.getAmount()
                        + transaction.getIncentive()
        );

        userRepository.save(sender);
        userRepository.save(recipient);
    }
}