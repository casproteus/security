package org.cas.client.platform.casbeans.calendar;

import org.cas.client.platform.casutil.EDaySet;

public interface DateSelectionListener {
    void setSelectedDayModel(
            int viewModel,
            EDaySet daySet);
}
