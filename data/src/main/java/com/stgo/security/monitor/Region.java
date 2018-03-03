package com.stgo.security.monitor;

import java.util.Comparator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "region")
@XmlAccessorType(XmlAccessType.NONE)
public class Region {

    private static final String SECURITY = "security";
    private static final String STATISTIC = "statistic";

    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "id")
    private String id;

    @XmlElementWrapper(name = SECURITY)
    @XmlElement(name = STATISTIC)
    private List<RegionCounter> counters;

    public String getName() {
        return name;
    }

    public void setName(
            final String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(
            final String id) {
        this.id = id;
    }

    public List<RegionCounter> getCounters() {
        return counters;
    }

    public void setCounters(
            List<RegionCounter> counters) {
        this.counters = counters;
    }

    public static enum RegionComparator {
        ID("id", new RegionStringBaseFieldComparator() {

            @Override
            protected String getFieldValue(
                    final Region region) {
                return region.getId();
            }
        }), NAME("name", new RegionStringBaseFieldComparator() {

            @Override
            protected String getFieldValue(
                    final Region region) {
                return region.getName();
            }
        });

        private final Comparator<Region> comparator;

        private final String fieldName;

        private RegionComparator(final String fieldName, final Comparator<Region> comparator) {
            this.fieldName = fieldName;
            this.comparator = comparator;
        }

        public Comparator<Region> getComparator() {
            return comparator;
        }

        public RegionComparator comaparatorByFieldName(
                final String fieldName) {
            for (final RegionComparator comparator : RegionComparator.values()) {
                if (comparator.fieldName.equals(fieldName)) {
                    return comparator;
                }
            }
            return null;
        }

        private static abstract class RegionStringBaseFieldComparator implements Comparator<Region> {

            @Override
            public int compare(
                    final Region region1,
                    final Region region2) {
                final String field1 = getFieldValue(region1);
                final String field2 = getFieldValue(region2);
                if (field1 == null && field2 == null) {
                    return 0;
                }
                if (field1 == null ^ field2 == null) {
                    return field1 == null ? -1 : 1;
                }
                return field1.compareToIgnoreCase(field2);
            }

            protected abstract String getFieldValue(
                    Region region);
        }
    }
}
