package cs211.project.services;

import java.util.ArrayList;

public class StringAdjustment {
    public static ArrayList<String> splitToArrayList(String line) {
        String[] splitList = line.split("[\\[|\\]]");
        String[] SplitNonList = splitList[0].split(",");
        splitList[0] = null;

        ArrayList<String> stringArrayList = new ArrayList<String>();

        addArrayListItemFromArray(stringArrayList, SplitNonList);
        addArrayListItemFromArray(stringArrayList, splitList);

        return stringArrayList;
    }

    private static void addArrayListItemFromArray(ArrayList<String> stringArrayList, String[] stringArray) {
        for (String item : stringArray) {
            if (item != null && !item.equals(",") && !item.isEmpty()) {
                stringArrayList.add(item.trim());
            }
        }
    }

    public static String replaceStringToCode(String string) {
        string = string.replace("\n", "{&(thinkk-better-newline#3389)&}");
        string = string.replace(",", "{&(thinkk-better-comma#1789)&}");
        string = string.replace("[", "{&(thinkk-better-open-bracket#9512)&}");
        string = string.replace("]", "{&(thinkk-better-close-bracket#1326)&}");
        string = string.replace("|", "{&(thinkk-better-close-pipe#8954)&}");
        string = string.replace("\\", "{&(thinkk-better-back-slash#28162)&}");
        return string;
    }

    public static String replaceCodeToString(String string) {
        string = string.replace("{&(thinkk-better-newline#3389)&}", "\n");
        string = string.replace("{&(thinkk-better-comma#1789)&}", ",");
        string = string.replace("{&(thinkk-better-open-bracket#9512)&}", "[");
        string = string.replace("{&(thinkk-better-close-bracket#1326)&}", "]");
        string = string.replace("{&(thinkk-better-close-pipe#8954)&}", "|");
        string = string.replace("{&(thinkk-better-back-slash#28162)&}", "\\");
        return string;
    }
}
