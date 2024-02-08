package cs211.project.services;

public class IdGenerator {
    public static String generateId(String modelName, int arrayListSize) {
        return String.format("%s-%d#%05d", modelName, TimeConversion.getNowDate().getTime(), arrayListSize);
    }
}
