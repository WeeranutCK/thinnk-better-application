package cs211.project.services;

import cs211.project.models.Participant;
import cs211.project.models.collections.ParticipantList;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class ParticipantListDatasource implements Datasource<ParticipantList>{
    private String directoryName;
    private String fileName;


    public ParticipantListDatasource(String directoryName, String fileName) {
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
    public ParticipantList readData() {
        ParticipantList participantList = new ParticipantList();

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
                ArrayList<String> stringArrayList = StringAdjustment.splitToArrayList(line);
                String participantId = stringArrayList.get(0).trim();
                Date date = TimeConversion.getDate(stringArrayList.get(1).trim());

                participantList.addNewParticipant(date, participantId);
            }
        } catch (IOException e) {
            System.err.println("Error Read Line");
        } catch (ParseException e) {
            System.err.println("Can Not Parse To Date (ParticipantListDatasource)");
        }

        return participantList;
    }

    @Override
    public void writeData(ParticipantList data) {
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
            for (Participant participant : data.getParticipantArrayList()) {
                String line = participant.getParticipantId() + "," +
                        TimeConversion.getFormattedDate(participant.getJoinDate());
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
