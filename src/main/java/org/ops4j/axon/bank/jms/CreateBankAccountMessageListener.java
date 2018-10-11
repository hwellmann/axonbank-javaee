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

import java.nio.charset.StandardCharsets;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.ops4j.axon.bank.rest.BankAccountResource;
import org.ops4j.axon.bank.rest.dto.BankAccountDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class CreateBankAccountMessageListener implements MessageListener {

    private static Logger log = LoggerFactory.getLogger(CreateBankAccountMessageListener.class);

    @Inject
    private BankAccountResource bankAccountResource;

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String text = textMessage.getText();
                processMessage(text);
            }
            else if (message instanceof BytesMessage) {
                BytesMessage bytesMessage = (BytesMessage) message;
                byte[] bytes = bytesMessage.getBody(byte[].class);
                String text = new String(bytes, StandardCharsets.UTF_8);
                processMessage(text);
            }
            else {
                log.warn("not a text message: {}", message.getClass().getName());
            }
        }
        catch (JMSException exc) {
            log.error("", exc);
        }
    }

    /**
     * @param text
     */
    private void processMessage(String message) {
        log.info("Received: {}", message);
        Jsonb jsonb = JsonbBuilder.create();
        BankAccountDto bankAccountDto = jsonb.fromJson(message, BankAccountDto.class);
        bankAccountResource.create(bankAccountDto);
    }

}
