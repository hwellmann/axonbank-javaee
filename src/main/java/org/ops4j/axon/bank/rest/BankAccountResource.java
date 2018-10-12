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

package org.ops4j.axon.bank.rest;

import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.ops4j.axon.bank.api.bankaccount.CreateBankAccountCommand;
import org.ops4j.axon.bank.api.bankaccount.DepositMoneyCommand;
import org.ops4j.axon.bank.api.bankaccount.WithdrawMoneyCommand;
import org.ops4j.axon.bank.query.bankaccount.BankAccountEntry;
import org.ops4j.axon.bank.query.bankaccount.BankAccountRepository;
import org.ops4j.axon.bank.rest.dto.BankAccountDto;
import org.ops4j.axon.bank.rest.dto.DepositDto;
import org.ops4j.axon.bank.rest.dto.WithdrawalDto;

@RequestScoped
@Path("/bank-accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BankAccountResource {

    @Inject
    private CommandGateway commandGateway;

    @Inject
    private BankAccountRepository bankAccountRepository;

    @GET
    public Iterable<BankAccountEntry> all() {
        return bankAccountRepository.findByOrderByIdAsc();
    }

    @Path("/{id}")
    @GET
    public BankAccountEntry get(@PathParam("id") long id) {
        return bankAccountRepository.findBy(id);
    }

    @POST
    public void create(BankAccountDto bankAccountDto) {
        String id = UUID.randomUUID().toString();
        CreateBankAccountCommand command = new CreateBankAccountCommand(id, bankAccountDto.getOverdraftLimit());
        commandGateway.send(command);
    }

    @PUT
    @Path("/withdraw")
    public void withdraw(WithdrawalDto withdrawalDto) {
        WithdrawMoneyCommand command = new WithdrawMoneyCommand(withdrawalDto.getBankAccountId(), withdrawalDto.getAmount());
        commandGateway.send(command);
    }

    @PUT
    @Path("/deposit")
    public void deposit(DepositDto depositDto) {
        DepositMoneyCommand command = new DepositMoneyCommand(depositDto.getBankAccountId(), depositDto.getAmount());
        commandGateway.send(command);
    }

}
