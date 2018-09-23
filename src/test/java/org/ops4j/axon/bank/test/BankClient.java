/*
 * Copyright 2017 OPS4J Contributors
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

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.ops4j.axon.bank.query.bankaccount.BankAccountEntry;
import org.ops4j.axon.bank.rest.dto.BankAccountDto;
import org.ops4j.axon.bank.rest.dto.BankTransferDto;
import org.ops4j.axon.bank.rest.dto.DepositDto;

/**
 * @author Harald Wellmann
 *
 */
public class BankClient {

    private Client client;
    private WebTarget entryPoint;

    public BankClient(String bankUrl) {
        client = new ResteasyClientBuilder().connectionPoolSize(20).build();

        entryPoint = client.target(bankUrl).path("api");
    }

    public List<BankAccountEntry> getBankAccounts() {
        Response response = entryPoint.path("bank-accounts").request(MediaType.APPLICATION_JSON)
            .get();
        assertThat(response.getStatusInfo()).isEqualTo(Status.OK);
        List<BankAccountEntry> accounts = response
            .readEntity(new GenericType<List<BankAccountEntry>>() {
            });
        return accounts;
    }

    public BankAccountEntry getBankAccount(long id) {
        Response response = entryPoint.path("bank-accounts/{id}").resolveTemplate("id", id)
            .request(MediaType.APPLICATION_JSON).get();
        assertThat(response.getStatusInfo()).isEqualTo(Status.OK);
        BankAccountEntry account = response.readEntity(BankAccountEntry.class);
        return account;
    }

    public void createBankAccount(long overdraftLimit) {
        BankAccountDto account = new BankAccountDto(overdraftLimit);
        Response response = entryPoint.path("bank-accounts").request(MediaType.APPLICATION_JSON)
            .post(Entity.json(account));
        assertThat(response.getStatusInfo()).isEqualTo(Status.NO_CONTENT);
    }

    public void deposit(String bankAccountId, long amount) {
        DepositDto deposit = new DepositDto(bankAccountId, amount);
        Response response = entryPoint.path("bank-accounts/deposit")
            .request(MediaType.APPLICATION_JSON).put(Entity.json(deposit));
        assertThat(response.getStatusInfo()).isEqualTo(Status.NO_CONTENT);
    }

    public void transfer(long amount, String sourceAccountId, String destinationAccountId) {
        BankTransferDto transfer = new BankTransferDto(sourceAccountId, destinationAccountId,
            amount);
        Response response = entryPoint.path("bank-transfers").request(MediaType.APPLICATION_JSON)
            .post(Entity.json(transfer));
        assertThat(response.getStatusInfo()).isEqualTo(Status.NO_CONTENT);
    }
}
