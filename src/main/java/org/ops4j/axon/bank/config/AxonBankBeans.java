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
package org.ops4j.axon.bank.config;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.common.jpa.EntityManagerProvider;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.config.Configuration;
import org.axonframework.config.DefaultConfigurer;
import org.axonframework.config.EventHandlingConfiguration;
import org.axonframework.config.SagaConfiguration;
import org.ops4j.axon.bank.command.BankAccount;
import org.ops4j.axon.bank.command.BankAccountCommandHandler;
import org.ops4j.axon.bank.command.BankTransfer;
import org.ops4j.axon.bank.command.BankTransferManagementSaga;
import org.ops4j.axon.bank.query.bankaccount.BankAccountEventListener;

/**
 * @author Harald Wellmann
 *
 */
@ApplicationScoped
public class AxonBankBeans {

    @Inject
    private BankAccountEventListener bankAccountEventListener;

    @Produces
    @PersistenceContext
    private EntityManager em;

    @Produces
    EntityManagerProvider entityManagerProvider() {
        return () -> em;
    }

    @Produces
    @ApplicationScoped
    Configuration configuration(EntityManagerProvider entityManagerProvider, TransactionManager transactionManager) {
        EventHandlingConfiguration ehc = new EventHandlingConfiguration().registerEventHandler(c -> bankAccountEventListener);

        return DefaultConfigurer.jpaConfiguration(entityManagerProvider)
            .configureAggregate(BankAccount.class)
            .configureAggregate(BankTransfer.class)
            .configureTransactionManager(c -> transactionManager)
            .registerCommandHandler(c -> new BankAccountCommandHandler(c.repository(BankAccount.class), c.eventBus()))
            .registerModule(SagaConfiguration.trackingSagaManager(BankTransferManagementSaga.class))
            .registerModule(ehc)
            .buildConfiguration();
    }

    @Produces
    @ApplicationScoped
    @Typed(CommandGateway.class)
    public CommandGateway commandGateway(Configuration configuration) {
        return configuration.commandGateway();
    }

    @Produces
    @Typed(CommandBus.class)
    @ApplicationScoped
    public CommandBus commandBus(Configuration configuration) {
        return configuration.commandBus();
    }

}
