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

import javax.inject.Inject;
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.json.bind.Jsonb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Harald Wellmann
 *
 */
public abstract class JsonbMessageListener<T> implements MessageListener {

    private static Logger log = LoggerFactory.getLogger(JsonbMessageListener.class);

    @Inject
    private Jsonb jsonb;

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

    private void processMessage(String message) {
        log.info("Received: {}", message);
        T payload = jsonb.fromJson(message, payloadClass());
        processPayload(payload);
    }

    protected abstract void processPayload(T payload);

    protected abstract Class<T> payloadClass();
}
