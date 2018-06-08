package dataclasses;

public class HMessage {
    public String providerid;
    public String providername;
    public String receiverid;
    public String receivername;
    public Object time;
    public String text;
    public boolean isread;

    public String getProvidername() {
        return providername;
    }

    public void setProvidername(String providername) {
        this.providername = providername;
    }

    public String getReceivername() {
        return receivername;
    }

    public void setReceivername(String receivername) {
        this.receivername = receivername;
    }

    public boolean isIsread() {
        return isread;
    }

    public void setIsread(boolean isread) {
        this.isread = isread;
    }

    public String getProviderid() {
        return providerid;
    }

    public void setProviderid(String providerid) {
        this.providerid = providerid;
    }

    public String getReceiverid() {
        return receiverid;
    }

    public void setReceiverid(String receiverid) {
        this.receiverid = receiverid;
    }

    public Object getTime() {
        return time;
    }

    public void setTime(Object time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public HMessage() {

    }

    public HMessage(String providerid, String providername, String receiverid, String receivername, Object time, String text, boolean isread) {
        this.providerid = providerid;
        this.providername = providername;
        this.receiverid = receiverid;
        this.receivername = receivername;
        this.time = time;
        this.text = text;
        this.isread = isread;
    }
}
