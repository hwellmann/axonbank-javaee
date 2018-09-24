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

import static javax.interceptor.Interceptor.Priority.APPLICATION;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.common.jpa.EntityManagerProvider;
import org.axonframework.config.Configuration;
import org.axonframework.config.Configurer;
import org.axonframework.config.DefaultConfigurer;
import org.axonframework.messaging.interceptors.BeanValidationInterceptor;

/**
 * @author Harald Wellmann
 *
 */
@ApplicationScoped
@Priority(APPLICATION)
public class AxonBankBeans {

    @Produces
    @PersistenceContext
    private EntityManager em;

    @Produces
    EntityManagerProvider entityManagerProvider() {
        return () -> em;
    }

    @Produces
    @Alternative
    @ApplicationScoped
    Configurer defaultConfigurer(EntityManagerProvider entityManagerProvider) {
        return DefaultConfigurer.jpaConfiguration(entityManagerProvider);
    }

    @Produces
    @Typed(CommandBus.class)
    @Alternative
    @ApplicationScoped
    public CommandBus commandBus(Configuration configuration) {
        CommandBus commandBus = configuration.commandBus();
        commandBus.registerDispatchInterceptor(new BeanValidationInterceptor<>());
        return commandBus;
    }
}
