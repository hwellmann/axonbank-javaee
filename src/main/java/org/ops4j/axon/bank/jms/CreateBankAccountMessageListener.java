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
package org.ops4j.axon.bank.jms;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSDestinationDefinition;

import org.ops4j.axon.bank.rest.BankAccountResource;
import org.ops4j.axon.bank.rest.dto.BankAccountDto;

/**
 * @author Harald Wellmann
 *
 */
@MessageDriven(
    activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup",
            propertyValue = "jms/queue/bank-accounts.create"),
        @ActivationConfigProperty(propertyName = "destinationType",
            propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode",
            propertyValue = "Auto-acknowledge") })
@JMSDestinationDefinition(
    name = "java:/jms/queue/bank-accounts.create",
    interfaceName = "javax.jms.Queue",
    destinationName = "bank-accounts.create")
public class CreateBankAccountMessageListener extends JsonbMessageListener<BankAccountDto> {

    @Inject
    private BankAccountResource bankAccountResource;

    @Override
    protected void processPayload(BankAccountDto payload) {
        bankAccountResource.create(payload);
    }

    @Override
    protected Class<BankAccountDto> payloadClass() {
        return BankAccountDto.class;
    }
}
