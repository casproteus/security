package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;

import org.cas.client.platform.casbeans.calendar.DateSeleAreaPane;
import org.cas.client.resource.international.MonthConstant;

/***/
public class WeekViewAction extends SAction {
    /* action is performed */
    public void actionPerformed(
            ActionEvent evt) {
        DateSeleAreaPane.getInstance().setCalendarViewModel(MonthConstant.WEEK_MODEL);
    }
}
