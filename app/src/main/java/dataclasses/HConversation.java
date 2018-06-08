package dataclasses;

import android.content.Intent;

public class HConversation {
    public String providerid;
    public String providername;
    public String receiverid;
    public String receivername;
    public Object lasttime;
    public String lasttext;
    public Integer unreadcount;

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

    public Integer getUnreadcount() {
        return unreadcount;
    }

    public void setUnreadcount(Integer unreadcount) {
        this.unreadcount = unreadcount;
    }

    public HConversation(HConversation hConversation) {
        this.providerid = hConversation.providerid;
        this.receiverid = hConversation.receiverid;
        this.lasttime = hConversation.lasttime;
        this.lasttext = hConversation.lasttext;
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

    public Object getLasttime() {
        return lasttime;
    }

    public void setLasttime(Object lasttime) {
        this.lasttime = lasttime;
    }

    public String getLasttext() {
        return lasttext;
    }

    public void setLasttext(String lasttext) {
        this.lasttext = lasttext;
    }

    public HConversation() {

    }

    public HConversation(String providerid, String providername, String receiverid, String receivername, Object lasttime, String lasttext, Integer unreadcount) {
        this.providerid = providerid;
        this.providername = providername;
        this.receiverid = receiverid;
        this.receivername = receivername;
        this.lasttime = lasttime;
        this.lasttext = lasttext;
        this.unreadcount = unreadcount;
    }

}
