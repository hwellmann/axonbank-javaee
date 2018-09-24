/*
 * Copyright 2018 OPS4J Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.axon.bank.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.ops4j.axon.bank.query.bankaccount.BankAccountEntry;

/**
 * @author Harald Wellmann
 *
 */
public class BankTransferTest {

    private BankClient client = new BankClient("http://localhost:8080/axon-bank");

    @Test
    public void shouldTransferAmount() throws InterruptedException {
        client.createBankAccount(100);
        client.createBankAccount(200);
        List<BankAccountEntry> accounts = client.getBankAccounts();
        assertThat(accounts).hasSize(2);

        BankAccountEntry account0 = accounts.get(0);
        BankAccountEntry account1 = accounts.get(1);

        client.deposit(account0.getAxonBankAccountId(), 1000);
        client.deposit(account1.getAxonBankAccountId(), 2000);
        Thread.sleep(1000);

        assertThat(client.getBankAccount(account0.getId()).getBalance()).isEqualTo(1000);
        assertThat(client.getBankAccount(account1.getId()).getBalance()).isEqualTo(2000);

        client.transfer(100, account1.getAxonBankAccountId(), account0.getAxonBankAccountId());
        Thread.sleep(2000);

        assertThat(client.getBankAccount(account0.getId()).getBalance()).isEqualTo(1100);
        assertThat(client.getBankAccount(account1.getId()).getBalance()).isEqualTo(1900);
    }

}
