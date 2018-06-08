package dataclasses;


public class HEvents {
    public String host;
    public String title;
    public String location;
    public String start_date;
    public String end_date;
    public String start_time;
    public String end_time;
    public String description;
    public String pr_or_pu;

    public String getHost() {
        return host;
    }

    public void setHost(String userid) {
        this.host = host;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() { return location; }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date_ip) {
        this.start_date = start_date_ip;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date_ip) {
        this.end_date = end_date_ip;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time_ip) {
        this.start_time = start_time_ip;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time_ip) {
        this.end_time = end_time_ip;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String descr) {
        this.description = descr;
    }

    public String getPr_or_pu() { return pr_or_pu;}

    public void setPr_or_pu(String pr) {
        this.pr_or_pu = pr;
    }

    public HEvents(String host, String title, String location, String start_date_ip, String end_date_ip, String start_time_ip, String end_time_ip, String description, String pr) {
        this.host = host;
        this.title = title;
        this.location = location;
        this.start_date = start_date_ip;
        this.end_date = end_date_ip;
        this.start_time = start_time_ip;
        this.end_time = end_time_ip;
        this.description = description;
        this.pr_or_pu = pr;
    }

    public HEvents() {

    }

}
