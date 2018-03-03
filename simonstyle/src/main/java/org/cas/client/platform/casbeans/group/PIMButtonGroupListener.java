package org.cas.client.platform.casbeans.group;

import java.util.EventListener;

public interface PIMButtonGroupListener extends EventListener {
    /**
     * Invoked when selection changed.
     * 
     * @param group
     *            the button group whose selection changed.
     * @param select
     *            the group selected index.
     */
    public void selected(
            PIMButtonGroup group,
            int select);
}
