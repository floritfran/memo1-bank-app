package com.aninfo.service;

import com.aninfo.exceptions.*;
import com.aninfo.model.Account;
import com.aninfo.model.Transaction;
import com.aninfo.repository.AccountRepository;
import com.aninfo.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public Collection<Account> getAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> findById(Long cbu) {
        return accountRepository.findById(cbu);
    }

    public void save(Account account) {
        accountRepository.save(account);
    }

    public void deleteById(Long cbu) {
        accountRepository.deleteById(cbu);
    }

    @Transactional
    public Account withdraw(Long cbu, Double sum) {
        Account account = accountRepository.findAccountByCbu(cbu);

        if (account.getBalance() < sum) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        account.setBalance(account.getBalance() - sum);
        accountRepository.save(account);

        return account;
    }

    @Transactional
    public Account deposit(Long cbu, Double sum) {

        if (sum <= 0) {
            throw new DepositNegativeSumException("Cannot deposit negative sums");
        }

        if (sum >= 2000) {
            Double extra = sum * 0.1;
            if (extra <= 500) {
                sum += extra;
            } else {
                sum += 500;
            }
        }

        Account account = accountRepository.findAccountByCbu(cbu);
        account.setBalance(account.getBalance() + sum);
        accountRepository.save(account);

        return account;
    }

    public Transaction createTransaction(Transaction transaction) {
        Optional<Account> optionalAccount = accountRepository.findById(transaction.getCbu());
        if (optionalAccount.isEmpty()) {
            throw new AccountNotFoundException("Account not found");
        }

        if (transaction.getType().equals("Extraction")) {
            this.withdraw(transaction.getCbu(), transaction.getAmount());
        }
        if (transaction.getType().equals("Deposit")) {
            this.deposit(transaction.getCbu(), transaction.getAmount());
        }
        return transactionRepository.save(transaction);
    }

    public Collection<Transaction> getTransactionsByCBU(Long cbu) {
        return transactionRepository.findAllByCbu(cbu);
    }

    public Transaction getTransactionById(Long id) {
        Optional<Transaction> transactionOptional = transactionRepository.findById(id);
        if (transactionOptional.isPresent()) {
            return transactionOptional.get();
        } else {
            throw new TransactionNotFoundException("Transaction not found");
        }
    }

    public void deleteTransaction(Long id) {
        Optional<Transaction> transactionOptional = transactionRepository.findById(id);
        if (transactionOptional.isPresent()) {
            transactionRepository.delete(transactionOptional.get());
        } else {
            throw new TransactionNotFoundException("Transaction not found");
        }
    }
}
