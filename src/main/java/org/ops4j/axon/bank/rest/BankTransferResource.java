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
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.ops4j.axon.bank.api.banktransfer.CreateBankTransferCommand;
import org.ops4j.axon.bank.query.banktransfer.BankTransferEntry;
import org.ops4j.axon.bank.query.banktransfer.BankTransferRepository;
import org.ops4j.axon.bank.rest.dto.BankTransferDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("")
public class BankTransferResource {

    private static Logger log = LoggerFactory.getLogger(BankTransferResource.class);

    @Inject
    private CommandGateway commandGateway;

    @Inject
    private BankTransferRepository bankTransferRepository;

    @Path("/bank-accounts/{bankAccountId}/bank-transfers")
    @GET
    public Iterable<BankTransferEntry> bankTransfers(@PathParam("bankAccountId") String bankAccountId) {
        log.info("Retrieve bank transfers for bank account with id {}", bankAccountId);
        return bankTransferRepository.findBySourceBankAccountIdOrDestinationBankAccountId(bankAccountId, bankAccountId);
    }

    @Path("/bank-transfers/{id}")
    @GET
    public BankTransferEntry get(@PathParam("id") long id) {
        log.info("Retrieve bank transfer with id {}", id);
        return bankTransferRepository.findBy(id);
    }

    @Path("/bank-transfers")
    @POST
    public void create(BankTransferDto bankTransferDto) {
        log.info("Create bank transfer with payload {}", bankTransferDto);

        String bankTransferId = UUID.randomUUID().toString();
        CreateBankTransferCommand command = new CreateBankTransferCommand(bankTransferId,
                                                                          bankTransferDto.getSourceBankAccountId(),
                                                                          bankTransferDto.getDestinationBankAccountId(),
                                                                          bankTransferDto.getAmount());

        commandGateway.send(command);
    }
}
