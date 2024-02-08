package cs211.project.services;

import cs211.project.models.Event;
import cs211.project.models.Participant;
import cs211.project.models.collections.EventList;
import cs211.project.models.collections.ParticipantList;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ParticipantAndEventDatasource implements DatasourcePair {

    String directoryName;
    String fileName;
    ParticipantList participantList;
    EventList eventList;


    public ParticipantAndEventDatasource(String directoryName, String fileName, ParticipantList participantList, EventList eventList) {
        this.directoryName = directoryName;
        this.fileName = fileName;
        this.participantList = participantList;
        this.eventList = eventList;
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

                Participant participant = participantList.findParticipantByParticipantId(data[0]);
                Event event = eventList.findEventById(data[1]);

                participant.setEvent(event);
                event.getParticipantList().getParticipantArrayList().add(participant);
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
            for (Participant participant : participantList.getParticipantArrayList()) {
                String line = participant.getParticipantId() + "," +
                        participant.getEvent().getEventId();
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
