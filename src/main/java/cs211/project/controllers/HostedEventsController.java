package cs211.project.controllers;

import cs211.project.services.AllCollection;

public class HostedEventsController extends EventsController {
    @Override
    protected void setupEventList() {
        eventList.getEventArrayList().addAll(AllCollection.getInstance().getCurrentUser().getHostedEvent().getEventArrayList());
    }

    @Override
    protected void setupCurrentPage() {
        currentPage = "hosted-events";
    }
}