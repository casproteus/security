package com.stgo.security.monitor;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = Status.REGIONS)
@XmlAccessorType(XmlAccessType.NONE)
public class Status {

    protected static final String REGIONS = "regions";

    @XmlElementWrapper(name = REGIONS)
    @XmlElement(name = "region")
    private List<Region> regions;

    @XmlElement(name = "offset")
    private Long offset;

    public Long getOffset() {
        return offset;
    }

    public void setOffset(
            Long offset) {
        this.offset = offset;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(
            Long count) {
        this.count = count;
    }

    @XmlElement(name = "count")
    private Long count;

    public List<Region> getRegions() {
        return regions;
    }

    public void setRegions(
            List<Region> regions) {
        this.regions = regions;
    }

}
