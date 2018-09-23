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

package org.ops4j.axon.bank.api.banktransfer;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

public class CreateBankTransferCommand {

    @TargetAggregateIdentifier
    final private String bankTransferId;
    final private String sourceBankAccountId;
    final private String destinationBankAccountId;
    final private long amount;



    public CreateBankTransferCommand(String bankTransferId, String sourceBankAccountId,
        String destinationBankAccountId, long amount) {
        this.bankTransferId = bankTransferId;
        this.sourceBankAccountId = sourceBankAccountId;
        this.destinationBankAccountId = destinationBankAccountId;
        this.amount = amount;
    }

    public String getBankTransferId() {
        return bankTransferId;
    }

    public String getSourceBankAccountId() {
        return sourceBankAccountId;
    }
    public String getDestinationBankAccountId() {
        return destinationBankAccountId;
    }

    public long getAmount() {
        return amount;
    }
}