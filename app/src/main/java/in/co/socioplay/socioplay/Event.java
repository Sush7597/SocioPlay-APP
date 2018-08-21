package in.co.socioplay.socioplay;

public class Event {

    private String title, desc, date;

    public Event(String title, String desc, String date) {
        this.title = title;
        this.desc = desc;
        this.date = date;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String name)
    {
        this.title = name;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getDesc()
    {
        return desc;
    }

    public void setDesc(String desc)
    {
        this.desc = desc;
    }
}
