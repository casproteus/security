package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.cas.client.platform.casbeans.calendar.DateSeleAreaPane;
import org.cas.client.resource.international.MonthConstant;

/***/
public class WorkWeekViewAction extends AbstractAction {
    /** Creates new WorkWeekViewAction */
    public WorkWeekViewAction() {
    }

    /* action is performed */
    public void actionPerformed(
            ActionEvent evt) {
        DateSeleAreaPane.getInstance().setCalendarViewModel(MonthConstant.WORK_MODEL);
    }
}
