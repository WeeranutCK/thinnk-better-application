package cs211.project.models.collections;

import cs211.project.models.Participant;

import java.util.ArrayList;
import java.util.Date;

public class ParticipantList {
    ArrayList<Participant> participantArrayList;


    public ParticipantList() {
        participantArrayList = new ArrayList<>();
    }

    public void addNewParticipant(Date joinDate, String participantId) {
        if (!participantId.trim().isEmpty() && findParticipantByParticipantId(participantId) == null) {
            participantArrayList.add(new Participant(joinDate, participantId));
        }
    }


    public Participant findParticipantByParticipantId(String participantId) {
        for (Participant participant : participantArrayList) {
            if (participant.isParticipantId(participantId)) {
                return participant;
            }
        }
        return null;
    }

    public Participant findParticipantByUserId(String userId) {
        for (Participant participant : participantArrayList) {
            if (participant.getUser().isUserId(userId)) {
                return participant;
            }
        }
        return null;
    }

    public ArrayList<Participant> getParticipantArrayList() {
        return participantArrayList;
    }

    public int getSize(){
        return participantArrayList.size();
    }
}
