package cs211.project.services;

import java.util.ArrayList;

public class PageHistory {
    private static PageHistory instance;
    private int top;
    private ArrayList<String> stack;

    private PageHistory() {
        stack = new ArrayList<>();
        top = -1;
    }

    public void clearStack() {
        stack.clear();
        top = -1;
    }

    public void pushStack(String page) {
        top += 1;
        stack.add(page);
    }

    public String popStack() {
        if (top >= 0) {
            String temp = stack.get(top);
            stack.remove(top);
            top -= 1;
            return temp;
        } else {
            System.err.println("Empty Stack");
        }
        return null;
    }


    public static PageHistory getInstance() {
        if (instance == null) {
            instance = new PageHistory();
        }
        return instance;
    }
}
