package com.stgo.security.monitor;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class NumberAdapter extends XmlAdapter<String, Number> {

    @Override
    public String marshal(
            final Number v) throws Exception {
        return String.valueOf(v);
    }

    @Override
    public Number unmarshal(
            final String v) throws Exception {

        final float f = Float.valueOf(v);
        if (f == 0) {
            return 0;
        } else {
            final int i = (int) f;
            if (f - i != 0) {
                return f;
            }
            return i;
        }
    }
}
