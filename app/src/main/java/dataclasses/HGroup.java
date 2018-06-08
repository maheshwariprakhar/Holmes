package dataclasses;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class HGroup {
    private String Created_By;
    private ArrayList<String> Members;
    private Object TimeStamp;

    public HGroup() {

    }
    public HGroup(String created_By, ArrayList<String> members, Object timeStamp) {
        Created_By = created_By;
        Members = members;
        TimeStamp = timeStamp;
    }

    public String getCreated_By() {
        return Created_By;
    }

    public void setCreated_By(String created_By) {
        Created_By = created_By;
    }

    public ArrayList<String> getMembers() {
        return Members;
    }

    public void setMembers(ArrayList<String> members) {
        Members = members;
    }

    public Object getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(Object timeStamp) {
        TimeStamp = timeStamp;
    }
}
