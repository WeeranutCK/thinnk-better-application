package cs211.project.models.collections;

import cs211.project.models.Wallet;

import java.util.ArrayList;


public class WalletList {
    private ArrayList<Wallet> walletArrayList;


    public WalletList() {
        walletArrayList = new ArrayList<>();
    }

    public void addNewWallet(String walletId, Double money, ArrayList<String> history, String holder) {
        walletId = walletId.trim();
        if (!walletId.equals("")) {
            Wallet exist = findWalletById(walletId);
            if (exist == null) {
                walletArrayList.add(new Wallet(walletId, money, history, holder));
            }
        }
    }

    public Wallet findWalletById(String walletId) {
        for (Wallet wallet: walletArrayList) {
            if (wallet.isWalletId(walletId)) {
                return wallet;
            }
        }
        return null;
    }

    public ArrayList<Wallet> getWalletArrayList() {
        return walletArrayList;
    }

}
