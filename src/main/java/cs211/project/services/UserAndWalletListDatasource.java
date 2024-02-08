package cs211.project.services;


import cs211.project.models.User;
import cs211.project.models.Wallet;
import cs211.project.models.collections.UserList;
import cs211.project.models.collections.WalletList;

import java.io.*;
import java.nio.charset.StandardCharsets;


public class UserAndWalletListDatasource implements DatasourcePair {
    private String directoryName;
    private String fileName;
    private UserList userList;
    private WalletList walletList;

    public UserAndWalletListDatasource(String directoryName, String fileName, UserList userList, WalletList walletList) {
        this.directoryName = directoryName;
        this.fileName = fileName;
        this.userList = userList;
        this.walletList = walletList;
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
    public void readData() {
        String filePath = directoryName + File.separator + fileName;
        File file = new File(filePath);

        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        InputStreamReader inputStreamReader = new InputStreamReader(
                fileInputStream,
                StandardCharsets.UTF_8
        );

        BufferedReader buffer = new BufferedReader(inputStreamReader);

        String line = "";
        try {

            while ((line = buffer.readLine()) != null) {
                if (line.equals("")) continue;

                String[] data = line.split(",");

                User user = userList.findUserById(data[0]);
                Wallet wallet = walletList.findWalletById(data[1]);
                user.setWallet(wallet);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeData() {
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
            for (User user : userList.getUserArrayList()) {
                if (user.getWallet() != null) {
                    String line = user.getUserId() + "," +
                            user.getWallet().getWalletId();
                    buffer.append(line);
                    buffer.append("\n");
                }
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
