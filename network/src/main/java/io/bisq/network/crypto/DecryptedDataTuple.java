/*
 * This file is part of bisq.
 *
 * bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package io.bisq.network.crypto;


import io.bisq.common.proto.network.NetworkEnvelope;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.security.PublicKey;

@EqualsAndHashCode
@Value
public final class DecryptedDataTuple {
    private final NetworkEnvelope networkEnvelope;
    private final PublicKey sigPublicKey;

    public DecryptedDataTuple(NetworkEnvelope networkEnvelope, PublicKey sigPublicKey) {
        this.networkEnvelope = networkEnvelope;
        this.sigPublicKey = sigPublicKey;
    }
}
