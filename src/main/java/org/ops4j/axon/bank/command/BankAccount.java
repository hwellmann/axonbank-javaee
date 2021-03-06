/*
 * Copyright (c) 2016. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ops4j.axon.bank.command;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

import org.axonframework.cdi.stereotype.Aggregate;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.ops4j.axon.bank.api.bankaccount.BankAccountCreatedEvent;
import org.ops4j.axon.bank.api.bankaccount.CreateBankAccountCommand;
import org.ops4j.axon.bank.api.bankaccount.DepositMoneyCommand;
import org.ops4j.axon.bank.api.bankaccount.DestinationBankAccountCreditedEvent;
import org.ops4j.axon.bank.api.bankaccount.MoneyAddedEvent;
import org.ops4j.axon.bank.api.bankaccount.MoneyDepositedEvent;
import org.ops4j.axon.bank.api.bankaccount.MoneyOfFailedBankTransferReturnedEvent;
import org.ops4j.axon.bank.api.bankaccount.MoneySubtractedEvent;
import org.ops4j.axon.bank.api.bankaccount.MoneyWithdrawnEvent;
import org.ops4j.axon.bank.api.bankaccount.ReturnMoneyOfFailedBankTransferCommand;
import org.ops4j.axon.bank.api.bankaccount.SourceBankAccountDebitRejectedEvent;
import org.ops4j.axon.bank.api.bankaccount.SourceBankAccountDebitedEvent;
import org.ops4j.axon.bank.api.bankaccount.WithdrawMoneyCommand;

@Aggregate
public class BankAccount {

    @AggregateIdentifier
    private String id;
    private long overdraftLimit;
    private long balanceInCents;

    @SuppressWarnings("unused")
    private BankAccount() {
    }

    @CommandHandler
    public BankAccount(CreateBankAccountCommand command) {
        apply(new BankAccountCreatedEvent(command.getBankAccountId(), command.getOverdraftLimit()));
    }

    @CommandHandler
    public void deposit(DepositMoneyCommand command) {
        apply(new MoneyDepositedEvent(id, command.getAmountOfMoney()));
    }

    @CommandHandler
    public void withdraw(WithdrawMoneyCommand command) {
        if (command.getAmountOfMoney() <= balanceInCents + overdraftLimit) {
            apply(new MoneyWithdrawnEvent(id, command.getAmountOfMoney()));
        }
    }

    public void debit(long amount, String bankTransferId) {
        if (amount <= balanceInCents + overdraftLimit) {
            apply(new SourceBankAccountDebitedEvent(id, amount, bankTransferId));
        }
        else {
            apply(new SourceBankAccountDebitRejectedEvent(bankTransferId));
        }
    }

    public void credit(long amount, String bankTransferId) {
        apply(new DestinationBankAccountCreditedEvent(id, amount, bankTransferId));
    }

    @CommandHandler
    public void returnMoney(ReturnMoneyOfFailedBankTransferCommand command) {
        apply(new MoneyOfFailedBankTransferReturnedEvent(id, command.getAmount()));
    }

    @EventSourcingHandler
    public void on(BankAccountCreatedEvent event) {
        this.id = event.getId();
        this.overdraftLimit = event.getOverdraftLimit();
        this.balanceInCents = 0;
    }

    @EventSourcingHandler
    public void on(MoneyAddedEvent event) {
        balanceInCents += event.getAmount();
    }

    @EventSourcingHandler
    public void on(MoneySubtractedEvent event) {
        balanceInCents -= event.getAmount();
    }
}
