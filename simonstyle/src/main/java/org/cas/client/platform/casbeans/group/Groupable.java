package org.cas.client.platform.casbeans.group;

public interface Groupable {
    /**
     * Set the button to group.
     * 
     * @param group
     *            the button group.
     * @param dialog
     *            the dialog which button to be added on.
     */
    public void setGroup(
            PIMButtonGroup group);

    /**
     * Get the button group to which the button belongs.
     * 
     * @return the button group.
     */
    public PIMButtonGroup getButtonGroup();

    /**
     * Set the button if it can be request the focus.
     * 
     * @flag the focus requestable flag.
     */
    public void setFocusFlag(
            boolean flag);

    /**
     * Get the the flag if the button request the focus when selected .
     * 
     * @return the flag of focus request.
     */
    public boolean isSelectedFocus();
}
