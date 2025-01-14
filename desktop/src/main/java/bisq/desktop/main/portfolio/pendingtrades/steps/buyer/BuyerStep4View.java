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

package bisq.desktop.main.portfolio.pendingtrades.steps.buyer;

import bisq.desktop.components.AutoTooltipButton;
import bisq.desktop.components.AutoTooltipLabel;
import bisq.desktop.components.InputTextField;
import bisq.desktop.components.TitledGroupBg;
import bisq.desktop.main.MainView;
import bisq.desktop.main.overlays.notifications.Notification;
import bisq.desktop.main.overlays.popups.Popup;
import bisq.desktop.main.overlays.windows.TradeFeedbackWindow;
import bisq.desktop.main.portfolio.PortfolioView;
import bisq.desktop.main.portfolio.closedtrades.ClosedTradesView;
import bisq.desktop.main.portfolio.pendingtrades.PendingTradesViewModel;
import bisq.desktop.main.portfolio.pendingtrades.steps.TradeStepView;
import bisq.desktop.util.Layout;

import bisq.core.btc.model.XmrAddressEntry;
import bisq.core.locale.Res;
import bisq.core.trade.txproof.AssetTxProofResult;
import bisq.core.user.DontShowAgainLookup;

import bisq.common.UserThread;
import bisq.common.app.DevEnv;
import bisq.common.handlers.FaultHandler;
import bisq.common.handlers.ResultHandler;

import org.bitcoinj.core.Coin;

import com.jfoenix.controls.JFXBadge;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import org.bouncycastle.crypto.params.KeyParameter;

import java.util.concurrent.TimeUnit;

import static bisq.desktop.util.FormBuilder.addCompactTopLabelTextField;
import static bisq.desktop.util.FormBuilder.addInputTextField;
import static bisq.desktop.util.FormBuilder.addTitledGroupBg;

public class BuyerStep4View extends TradeStepView {
    // private final ChangeListener<Boolean> focusedPropertyListener;

    private InputTextField withdrawAddressTextField, withdrawMemoTextField;
    private Button withdrawToExternalWalletButton, useSavingsWalletButton;
    private TitledGroupBg withdrawTitledGroupBg;

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Constructor, Initialisation
    ///////////////////////////////////////////////////////////////////////////////////////////

    public BuyerStep4View(PendingTradesViewModel model) {
        super(model);
    }

    @Override
    public void activate() {
        super.activate();
        // Don't display any trade step info when trade is complete
        hideTradeStepInfo();
    }

    @Override
    public void deactivate() {
        super.deactivate();
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Content
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void addContent() {
        gridPane.getColumnConstraints().get(1).setHgrow(Priority.SOMETIMES);

        TitledGroupBg completedTradeLabel = new TitledGroupBg();
        completedTradeLabel.setText(Res.get("portfolio.pending.step5_buyer.groupTitle"));

        JFXBadge autoConfBadge = new JFXBadge(new Label(""), Pos.BASELINE_RIGHT);
        autoConfBadge.setText(Res.get("portfolio.pending.autoConf"));
        autoConfBadge.getStyleClass().add("auto-conf");

        HBox hBox2 = new HBox(1, completedTradeLabel, autoConfBadge);
        GridPane.setMargin(hBox2, new Insets(18, -10, -12, -10));
        gridPane.getChildren().add(hBox2);
        GridPane.setRowSpan(hBox2, 5);
        autoConfBadge.setVisible(AssetTxProofResult.COMPLETED == trade.getAssetTxProofResult());

        addCompactTopLabelTextField(gridPane, gridRow, getBtcTradeAmountLabel(), model.getTradeVolume(), Layout.TWICE_FIRST_ROW_DISTANCE);
        addCompactTopLabelTextField(gridPane, ++gridRow, getFiatTradeAmountLabel(), model.getFiatVolume());
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("portfolio.pending.step5_buyer.refunded"), model.getSecurityDeposit());
        addCompactTopLabelTextField(gridPane, ++gridRow, Res.get("portfolio.pending.step5_buyer.tradeFee"), model.getTradeFee());
        final String miningFee = model.dataModel.isMaker() ?
                Res.get("portfolio.pending.step5_buyer.makersMiningFee") :
                Res.get("portfolio.pending.step5_buyer.takersMiningFee");
        addCompactTopLabelTextField(gridPane, ++gridRow, miningFee, model.getTxFee());
        withdrawTitledGroupBg = addTitledGroupBg(gridPane, ++gridRow, 1, Res.get("portfolio.pending.step5_buyer.withdrawBTC"), Layout.COMPACT_GROUP_DISTANCE);
        withdrawTitledGroupBg.getStyleClass().add("last");
        addCompactTopLabelTextField(gridPane, gridRow, Res.get("portfolio.pending.step5_buyer.amount"), model.getPayoutAmount(), Layout.FIRST_ROW_AND_GROUP_DISTANCE);

        withdrawAddressTextField = addInputTextField(gridPane, ++gridRow, Res.get("portfolio.pending.step5_buyer.withdrawToAddress"));
        withdrawAddressTextField.setManaged(false);
        withdrawAddressTextField.setVisible(false);

        withdrawMemoTextField = addInputTextField(gridPane, ++gridRow,
                Res.get("funds.withdrawal.memoLabel", Res.getBaseCurrencyCode()));
        withdrawMemoTextField.setPromptText(Res.get("funds.withdrawal.memo"));
        withdrawMemoTextField.setManaged(false);
        withdrawMemoTextField.setVisible(false);

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        useSavingsWalletButton = new AutoTooltipButton(Res.get("portfolio.pending.step5_buyer.moveToHavenoWallet"));
        useSavingsWalletButton.setDefaultButton(true);
        useSavingsWalletButton.getStyleClass().add("action-button");
        Label label = new AutoTooltipLabel(Res.get("shared.OR"));
        label.setPadding(new Insets(5, 0, 0, 0));
        withdrawToExternalWalletButton = new AutoTooltipButton(Res.get("portfolio.pending.step5_buyer.withdrawExternal"));
        withdrawToExternalWalletButton.setDefaultButton(false);
        hBox.getChildren().addAll(useSavingsWalletButton, label, withdrawToExternalWalletButton);
        GridPane.setRowIndex(hBox, ++gridRow);
        GridPane.setMargin(hBox, new Insets(5, 10, 0, 0));
        gridPane.getChildren().add(hBox);

        useSavingsWalletButton.setOnAction(e -> {
            handleTradeCompleted();
            model.dataModel.tradeManager.onTradeCompleted(trade);
        });
        withdrawToExternalWalletButton.setOnAction(e -> {
            onWithdrawal();
        });

        String key = "tradeCompleted" + trade.getId();
        if (!DevEnv.isDevMode() && DontShowAgainLookup.showAgain(key)) {
            DontShowAgainLookup.dontShowAgain(key, true);
            new Notification().headLine(Res.get("notification.tradeCompleted.headline"))
                    .notification(Res.get("notification.tradeCompleted.msg"))
                    .autoClose()
                    .show();
        }
    }

    private void onWithdrawal() {
        withdrawAddressTextField.setManaged(true);
        withdrawAddressTextField.setVisible(true);
        withdrawMemoTextField.setManaged(true);
        withdrawMemoTextField.setVisible(true);
        GridPane.setRowSpan(withdrawTitledGroupBg, 3);
        withdrawToExternalWalletButton.setDefaultButton(true);
        useSavingsWalletButton.setDefaultButton(false);
        withdrawToExternalWalletButton.getStyleClass().add("action-button");
        useSavingsWalletButton.getStyleClass().remove("action-button");

        withdrawToExternalWalletButton.setOnAction(e -> {
            if (model.dataModel.isReadyForTxBroadcast()) {
                reviewWithdrawal();
            }
        });

    }

    private void reviewWithdrawal() {
      throw new RuntimeException("BuyerStep4View.reviewWithdrawal() not yet updated for XMR");
//        Coin amount = trade.getPayoutAmount();
//        BtcWalletService walletService = model.dataModel.btcWalletService;
//
//        AddressEntry fromAddressesEntry = walletService.getOrCreateAddressEntry(trade.getId(), AddressEntry.Context.TRADE_PAYOUT);
//        String fromAddresses = fromAddressesEntry.getAddressString();
//        String toAddresses = withdrawAddressTextField.getText();
//        if (new BtcAddressValidator().validate(toAddresses).isValid) {
//            Coin balance = walletService.getBalanceForAddress(fromAddressesEntry.getAddress());
//            try {
//                Transaction feeEstimationTransaction = walletService.getFeeEstimationTransaction(fromAddresses, toAddresses, amount, AddressEntry.Context.TRADE_PAYOUT);
//                Coin fee = feeEstimationTransaction.getFee();
//                Coin receiverAmount = amount.subtract(fee);
//                if (balance.isZero()) {
//                    new Popup().warning(Res.get("portfolio.pending.step5_buyer.alreadyWithdrawn")).show();
//                    model.dataModel.tradeManager.onTradeCompleted(trade);
//                } else {
//                    if (toAddresses.isEmpty()) {
//                        validateWithdrawAddress();
//                    } else if (Restrictions.isAboveDust(receiverAmount)) {
//                        CoinFormatter formatter = model.btcFormatter;
//                        int txVsize = feeEstimationTransaction.getVsize();
//                        double feePerVbyte = CoinUtil.getFeePerVbyte(fee, txVsize);
//                        double vkb = txVsize / 1000d;
//                        String recAmount = formatter.formatCoinWithCode(receiverAmount);
//                        new Popup().headLine(Res.get("portfolio.pending.step5_buyer.confirmWithdrawal"))
//                                .confirmation(Res.get("shared.sendFundsDetailsWithFee",
//                                        formatter.formatCoinWithCode(amount),
//                                        fromAddresses,
//                                        toAddresses,
//                                        formatter.formatCoinWithCode(fee),
//                                        feePerVbyte,
//                                        vkb,
//                                        recAmount))
//                                .actionButtonText(Res.get("shared.yes"))
//                                .onAction(() -> doWithdrawal(amount, fee))
//                                .closeButtonText(Res.get("shared.cancel"))
//                                .onClose(() -> {
//                                    useSavingsWalletButton.setDisable(false);
//                                    withdrawToExternalWalletButton.setDisable(false);
//                                })
//                                .show();
//                    } else {
//                        new Popup().warning(Res.get("portfolio.pending.step5_buyer.amountTooLow")).show();
//                    }
//                }
//            } catch (AddressFormatException e) {
//                validateWithdrawAddress();
//            } catch (AddressEntryException e) {
//                log.error(e.getMessage());
//            } catch (InsufficientFundsException e) {
//                log.error(e.getMessage());
//                e.printStackTrace();
//                new Popup().warning(e.getMessage()).show();
//            }
//        } else {
//            new Popup().warning(Res.get("validation.btc.invalidAddress")).show();
//        }
    }

    private void doWithdrawal(Coin amount, Coin fee) {
        String toAddress = withdrawAddressTextField.getText();
        ResultHandler resultHandler = this::handleTradeCompleted;
        FaultHandler faultHandler = (errorMessage, throwable) -> {
            useSavingsWalletButton.setDisable(false);
            withdrawToExternalWalletButton.setDisable(false);
            if (throwable != null && throwable.getMessage() != null)
                new Popup().error(errorMessage + "\n\n" + throwable.getMessage()).show();
            else
                new Popup().error(errorMessage).show();
        };
        if (true) throw new RuntimeException("BuyerStep4View.doWithdrawal() not yet updated for XMR");
//        if (model.dataModel.btcWalletService.isEncrypted()) {
//            UserThread.runAfter(() -> model.dataModel.walletPasswordWindow.onAesKey(aesKey ->
//                    doWithdrawRequest(toAddress, amount, fee, aesKey, resultHandler, faultHandler))
//                    .show(), 300, TimeUnit.MILLISECONDS);
//        } else
//            doWithdrawRequest(toAddress, amount, fee, null, resultHandler, faultHandler);
    }

    private void doWithdrawRequest(String toAddress,
                                   Coin amount,
                                   Coin fee,
                                   KeyParameter aesKey,
                                   ResultHandler resultHandler,
                                   FaultHandler faultHandler) {
        useSavingsWalletButton.setDisable(true);
        withdrawToExternalWalletButton.setDisable(true);
        String memo = withdrawMemoTextField.getText();
        if (memo.isEmpty()) {
            memo = null;
        }
        model.dataModel.onWithdrawRequest(toAddress,
                amount,
                fee,
                aesKey,
                memo,
                resultHandler,
                faultHandler);
    }

    private void handleTradeCompleted() {
        useSavingsWalletButton.setDisable(true);
        withdrawToExternalWalletButton.setDisable(true);
        model.dataModel.xmrWalletService.swapTradeEntryToAvailableEntry(trade.getId(), XmrAddressEntry.Context.TRADE_PAYOUT);

        openTradeFeedbackWindow();
    }

    private void openTradeFeedbackWindow() {
        String key = "feedbackPopupAfterTrade";
        if (!DevEnv.isDevMode() && preferences.showAgain(key)) {
            UserThread.runAfter(() -> {
                new TradeFeedbackWindow()
                        .dontShowAgainId(key)
                        .onAction(this::showNavigateToClosedTradesViewPopup)
                        .show();
            }, 500, TimeUnit.MILLISECONDS);
        } else {
            showNavigateToClosedTradesViewPopup();
        }
    }

    private void showNavigateToClosedTradesViewPopup() {
        if (!DevEnv.isDevMode()) {
            UserThread.runAfter(() -> {
                new Popup().headLine(Res.get("portfolio.pending.step5_buyer.withdrawalCompleted.headline"))
                        .feedback(Res.get("portfolio.pending.step5_buyer.withdrawalCompleted.msg"))
                        .actionButtonTextWithGoTo("navigation.portfolio.closedTrades")
                        .onAction(() -> model.dataModel.navigation.navigateTo(MainView.class, PortfolioView.class, ClosedTradesView.class))
                        .dontShowAgainId("tradeCompleteWithdrawCompletedInfo")
                        .show();
            }, 500, TimeUnit.MILLISECONDS);
        }
    }

    private void validateWithdrawAddress() {
        withdrawAddressTextField.setValidator(model.btcAddressValidator);
        withdrawAddressTextField.requestFocus();
        useSavingsWalletButton.requestFocus();
    }

    protected String getBtcTradeAmountLabel() {
        return Res.get("portfolio.pending.step5_buyer.bought");
    }

    protected String getFiatTradeAmountLabel() {
        return Res.get("portfolio.pending.step5_buyer.paid");
    }
}
