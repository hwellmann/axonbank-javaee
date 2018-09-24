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

package org.ops4j.axon.bank.query.bankaccount;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.axonframework.eventhandling.EventHandler;
import org.ops4j.axon.bank.api.bankaccount.BankAccountCreatedEvent;
import org.ops4j.axon.bank.api.bankaccount.MoneyAddedEvent;
import org.ops4j.axon.bank.api.bankaccount.MoneySubtractedEvent;

@Dependent
public class BankAccountEventListener {

    @Inject
    private BankAccountRepository repository;

    // private SimpMessageSendingOperations messagingTemplate;

    @EventHandler
    public void on(BankAccountCreatedEvent event) {
        repository.save(new BankAccountEntry(event.getId(), 0, event.getOverdraftLimit()));

        broadcastUpdates();
    }

    @EventHandler
    public void on(MoneyAddedEvent event) {
        BankAccountEntry bankAccountEntry = repository.findOptionalByAxonBankAccountId(event.getBankAccountId());
        bankAccountEntry.setBalance(bankAccountEntry.getBalance() + event.getAmount());

        repository.save(bankAccountEntry);

        broadcastUpdates();
    }

    @EventHandler
    public void on(MoneySubtractedEvent event) {
        BankAccountEntry bankAccountEntry = repository.findOptionalByAxonBankAccountId(event.getBankAccountId());
        bankAccountEntry.setBalance(bankAccountEntry.getBalance() - event.getAmount());

        repository.save(bankAccountEntry);

        broadcastUpdates();
    }

    private void broadcastUpdates() {
        // Iterable<BankAccountEntry> bankAccountEntries = repository.findAll();
        // messagingTemplate.convertAndSend("/topic/bank-accounts.updates", bankAccountEntries);
    }

}
