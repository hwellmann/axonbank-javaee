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
import org.axonframework.eventhandling.EventHandler;
import org.ops4j.axon.bank.api.banktransfer.BankTransferCompletedEvent;
import org.ops4j.axon.bank.api.banktransfer.BankTransferCreatedEvent;
import org.ops4j.axon.bank.api.banktransfer.BankTransferFailedEvent;
import org.ops4j.axon.bank.api.banktransfer.CreateBankTransferCommand;
import org.ops4j.axon.bank.api.banktransfer.MarkBankTransferCompletedCommand;
import org.ops4j.axon.bank.api.banktransfer.MarkBankTransferFailedCommand;

@Aggregate
public class BankTransfer {

    public enum Status {
        STARTED,
        FAILED,
        COMPLETED
    }


    @AggregateIdentifier
    private String bankTransferId;
    private String sourceBankAccountId;
    private String destinationBankAccountId;
    private long amount;
    private Status status;

    protected BankTransfer() {
    }

    @CommandHandler
    public BankTransfer(CreateBankTransferCommand command) {
        apply(new BankTransferCreatedEvent(command.getBankTransferId(),
                                           command.getSourceBankAccountId(),



           command.getDestinationBankAccountId(),
                                           command.getAmount()));
    }

    @CommandHandler
    public void handle(MarkBankTransferCompletedCommand command) {
        apply(new BankTransferCompletedEvent(command.getBankTransferId()));
    }

    @CommandHandler
    public void handle(MarkBankTransferFailedCommand command) {
        apply(new BankTransferFailedEvent(command.getBankTransferId()));
    }

    @EventHandler
    public void on(BankTransferCreatedEvent event) throws Exception {
        this.bankTransferId = event.getBankTransferId();
        this.sourceBankAccountId = event.getSourceBankAccountId();
        this.destinationBankAccountId = event.getDestinationBankAccountId();
        this.amount = event.getAmount();
        this.status = Status.STARTED;
    }

    @EventHandler
    public void on(BankTransferCompletedEvent event) {
        this.status = Status.COMPLETED;
    }

    @EventHandler
    public void on(BankTransferFailedEvent event) {
        this.status = Status.FAILED;
    }

    public String getBankTransferId() {
        return bankTransferId;
    }


    public String getSourceBankAccountId() {
        return sourceBankAccountId;
    }


    public String getDestinationBankAccountId() {
        return destinationBankAccountId;
    }


    public long getAmount() {
        return amount;
    }


    public Status getStatus() {
        return status;
    }
}