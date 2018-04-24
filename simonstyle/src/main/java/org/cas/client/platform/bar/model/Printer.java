package org.cas.client.platform.bar.model;

/**
 * Entity mapped to table PRINTER.
 */
public class Printer {

    private int id;
    private String ip;
    private String pname;
    private String note;
    private Integer firstPrint;
    private Integer type;
    private Integer state;
    private Long version;

    public Printer() {
    }

    public Printer(int id) {
        this.id = id;
    }

    public Printer(int id, String ip, String pname, String note, Integer firstPrint, Integer type, Integer state, Long version) {
        this.id = id;
        this.ip = ip;
        this.pname = pname;
        this.note = note;
        this.firstPrint = firstPrint;		//all print/ single print flag
        this.type = type;					//language flag
        this.state = state;
        this.version = version;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getFirstPrint() {
        return firstPrint;
    }

    public void setFirstPrint(Integer firstPrint) {
        this.firstPrint = firstPrint;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Printer) {
            return ((Printer) o).getId() == getId();
        } else return false;
    }

}

