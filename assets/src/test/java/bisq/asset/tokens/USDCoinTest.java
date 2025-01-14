/*
 * This file is part of Haveno.
 *
 * Haveno is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Haveno is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Haveno. If not, see <http://www.gnu.org/licenses/>.
 */

package bisq.asset.tokens;

import bisq.asset.AbstractAssetTest;

import org.junit.Test;

public class USDCoinTest extends AbstractAssetTest {

    public USDCoinTest() {
        super(new USDCoin());
    }

    @Test
    public void testValidAddresses() {
        assertValidAddress("0xb86bb5fc804768db34f1a37da8b719e19af9dffd");
        assertValidAddress("0xea82afd93ebfc4f6564f3e5bd823cdef710f75dd");
    }

    @Test
    public void testInvalidAddresses() {
        assertInvalidAddress("0x2a65Aca4D5fC5B5C859090a6c34d1641353982266");
        assertInvalidAddress("0x2a65Aca4D5fC5B5C859090a6c34d16413539822g");
        assertInvalidAddress("2a65Aca4D5fC5B5C859090a6c34d16413539822g");
    }
}
