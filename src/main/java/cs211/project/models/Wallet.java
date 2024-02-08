package cs211.project.models;

import cs211.project.services.AllCollection;
import cs211.project.services.IdGenerator;
import cs211.project.services.TimeConversion;

import java.util.ArrayList;


public class Wallet {
    private double amount;
    private ArrayList<String> history = new ArrayList<>();
    private String holder;
    private String walletId;

    public Wallet(String userId) {
        walletId = IdGenerator.generateId("wallet", AllCollection.getInstance().getWalletList().getWalletArrayList().size());
        holder = userId;
    }

    public Wallet(String walletId, Double amount, ArrayList<String> history, String holder) {
        this.walletId = walletId;
        this.amount = amount;
        this.history = history;
        this.holder = holder;
    }

    public void addPurchaseHistory(String eventId, double money) {
        Event event = AllCollection.getInstance().getEventList().findEventById(eventId);
        String eventName = event.getEventName();
        history.add("Purchase ; " + "Event : " + eventName + " ; Timestamp : " + TimeConversion.getFormattedDate(TimeConversion.getNowDate()) + " ; Total : " + amount + " (" + money + ")");
    }

    public void addTopUpHistory(double money) {
        history.add("Top up ; " + "Timestamp : " + TimeConversion.getFormattedDate(TimeConversion.getNowDate()) + " ; Total : " + amount + " (+" + money + ")");
    }

    public void addWithdrawHistory(double money) {
        history.add("Withdraw ; " + "Timestamp : " + TimeConversion.getFormattedDate(TimeConversion.getNowDate()) + " (-" + money + ")");
    }

    public boolean isEnoughMoney(double money) {
        return amount - money >= 0;
    }

    public void purchase(String eventId, double money) {
        if (isEnoughMoney(money)) {
            amount -= money;
            addPurchaseHistory(eventId, -money);
        }
    }

    public void topUp(double money) {
        amount += money;
        addTopUpHistory(money);
    }

    public void withdraw() {
        addWithdrawHistory(amount);
        setAmount(0.00);
    }


    public boolean isWalletId(String walletId) {
        return this.walletId.equals(walletId);
    }

    public double getAmount() {
        return amount;
    }

    public ArrayList<String> getHistory() {
        return history;
    }

    public String getWalletId() {
        return walletId;
    }

    public String getHolder() {
        return holder;
    }

    private void setAmount(double amount) {
        this.amount = amount;
    }
}
