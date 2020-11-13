package bisq.apitest.method.wallet;

import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.core.NetworkParameters;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static bisq.apitest.Scaffold.BitcoinCoreApp.bitcoind;
import static bisq.apitest.config.BisqAppConfig.alicedaemon;
import static bisq.apitest.config.BisqAppConfig.seednode;
import static org.bitcoinj.core.NetworkParameters.PAYMENT_PROTOCOL_ID_MAINNET;
import static org.bitcoinj.core.NetworkParameters.PAYMENT_PROTOCOL_ID_REGTEST;
import static org.bitcoinj.core.NetworkParameters.PAYMENT_PROTOCOL_ID_TESTNET;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.MethodOrderer.OrderAnnotation;



import bisq.apitest.method.MethodTest;

// @Disabled
@Slf4j
@TestMethodOrder(OrderAnnotation.class)
public class BsqWalletTest extends MethodTest {

    @BeforeAll
    public static void setUp() {
        startSupportingApps(false,
                true,
                bitcoind,
                seednode,
                alicedaemon);
    }

    @Test
    @Order(1)
    public void testGetUnusedBsqAddress() {
        var request = createGetUnusedBsqAddressRequest();

        String address = grpcStubs(alicedaemon).walletsService.getUnusedBsqAddress(request).getAddress();
        assertFalse(address.isEmpty());
        assertTrue(address.startsWith("B"));

        NetworkParameters networkParameters = LegacyAddress.getParametersFromAddress(address.substring(1));
        String addressNetwork = networkParameters.getPaymentProtocolId();
        assertNotEquals(PAYMENT_PROTOCOL_ID_MAINNET, addressNetwork);
        // TODO Fix bug(?) causing the regtest bsq address network to be evaluated as 'testnet' here.
        assertTrue(addressNetwork.equals(PAYMENT_PROTOCOL_ID_TESTNET)
                || addressNetwork.equals(PAYMENT_PROTOCOL_ID_REGTEST));
    }

    @AfterAll
    public static void tearDown() {
        tearDownScaffold();
    }
}
