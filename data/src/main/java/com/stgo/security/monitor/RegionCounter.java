package com.stgo.security.monitor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "statistic")
@XmlAccessorType(XmlAccessType.NONE)
public class RegionCounter {

    private static final String DEFAULT_TYPE = "counter";

    @XmlElement(name = "id", required = false)
    private String id;

    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "value")
    @XmlJavaTypeAdapter(value = NumberAdapter.class)
    private Number value;

    @XmlElement(name = "type")
    private String type = DEFAULT_TYPE;

    public String getId() {
        return id;
    }

    public void setId(
            final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(
            final String name) {
        this.name = name;
    }

    public Number getValue() {
        return value;
    }

    public void setValue(
            final Number value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(
            String type) {
        this.type = type;
    }

}
