package cs211.project.services;

import cs211.project.models.Wallet;
import cs211.project.models.collections.WalletList;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class WalletListDatasource implements Datasource<WalletList> {
    private String directoryName;
    private String fileName;

    public WalletListDatasource(String directoryName, String fileName) {
        this.directoryName = directoryName;
        this.fileName = fileName;
        checkFileIsExisted();
    }

    private void checkFileIsExisted() {
        File file = new File(directoryName);
        if (!file.exists()) {
            file.mkdirs();
        }

        String filePath = directoryName + File.separator + fileName;
        file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public WalletList readData() {
        WalletList walletList = new WalletList();
        String filePath = directoryName + File.separator + fileName;
        File file = new File(filePath);
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        InputStreamReader inputStreamReader = new InputStreamReader (
                fileInputStream,
                StandardCharsets.UTF_8
        );

            BufferedReader buffer = new BufferedReader(inputStreamReader);
            String line = "";

            try {
                while ((line = buffer.readLine()) != null) {

                    ArrayList<String> stringArrayList = StringAdjustment.splitToArrayList(line);
                    String walletId = stringArrayList.get(0).trim();
                    String holder = stringArrayList.get(1).trim();
                    double amount = Double.parseDouble(stringArrayList.get(2));
                    String history = stringArrayList.get(3);
                    ArrayList<String> walletHistory = new ArrayList<>();

                    if (!history.equals("null")) {
                        ArrayList<String> walletHistoryArrayList = StringAdjustment.splitToArrayList(history);
                        walletHistory.addAll(walletHistoryArrayList);
                    }
                    walletList.addNewWallet(walletId, amount, walletHistory, holder);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return walletList;
    }

    @Override
    public void writeData(WalletList data) {
        String filePath = directoryName + File.separator + fileName;
        File file = new File(filePath);

        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                fileOutputStream,
                StandardCharsets.UTF_8
        );
        BufferedWriter buffer = new BufferedWriter(outputStreamWriter);


        try {
            for (Wallet wallet : data.getWalletArrayList()) {
                ArrayList<String> walletHistoryArrayList = new ArrayList<>();
                if (wallet.getHistory().isEmpty()) {
                    walletHistoryArrayList.add("null");
                } else {
                    walletHistoryArrayList.addAll(wallet.getHistory());
                }
                String line = wallet.getWalletId() + "," +
                              wallet.getHolder() + "," +
                              wallet.getAmount() + "," +
                              walletHistoryArrayList;
                buffer.append(line);
                buffer.append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                buffer.flush();
                buffer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
