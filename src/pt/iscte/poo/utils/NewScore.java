package pt.iscte.poo.utils;

public class NewScore implements Comparable<NewScore> {
    public static final int NAME_WIDTH = 20;

    String name;
    int numTurns;
    long time;

    public NewScore(String name, long time, int numTurns) {
        this.name = name;
        this.time = time;
        this.numTurns = numTurns;
    }

    //Nome no máximo com 20 caracteres
    public String getNameTruncated() {
        return name.length() > NAME_WIDTH ? name.substring(0, NAME_WIDTH) : name;
    }

    public int getNumTurns() {
        return numTurns;
    }

    public long getTime() {
        return time;
    }

    @Override
    public int compareTo(NewScore other) {
        if (this.time != other.time) {
            return Long.compare(this.time, other.time);
        }
        return Integer.compare(this.numTurns, other.numTurns);
    }
}
