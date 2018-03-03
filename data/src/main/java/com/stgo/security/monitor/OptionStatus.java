package com.stgo.security.monitor;

import java.io.IOException;
import java.io.InputStream;

public class OptionStatus extends org.springframework.core.io.AbstractResource {

    private long id; // record id.

    private String group_name; // company name or other group name.

    private String computer_name; // computer name or id.

    private int function_id; // contains the lables, could be used for searching.

    private String stat_value; // detail message.

    /**
     * Returns the <code>id</code> value.
     *
     * @return The <code>id</code>.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the <code>id</code> value.
     *
     * @param id
     *            The <code>id</code> to set.
     */
    public void setId(
            long id) {
        this.id = id;
    }

    /**
     * Returns the <code>group_name</code> value.
     *
     * @return The <code>group_name</code>.
     */
    public String getGroup_name() {
        return group_name;
    }

    /**
     * Sets the <code>group_name</code> value.
     *
     * @param group_name
     *            The <code>group_name</code> to set.
     */
    public void setGroup_name(
            String group_name) {
        this.group_name = group_name;
    }

    /**
     * Returns the <code>computer_name</code> value.
     *
     * @return The <code>computer_name</code>.
     */
    public String getComputer_name() {
        return computer_name;
    }

    /**
     * Sets the <code>computer_name</code> value.
     *
     * @param computer_name
     *            The <code>computer_name</code> to set.
     */
    public void setComputer_name(
            String computer_name) {
        this.computer_name = computer_name;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return null;
    }

    /**
     * Returns the <code>function_id</code> value.
     *
     * @return The <code>function_id</code>.
     */
    public int getFunction_id() {
        return function_id;
    }

    /**
     * Sets the <code>function_id</code> value.
     *
     * @param function_id
     *            The <code>function_id</code> to set.
     */
    public void setFunction_id(
            int function_id) {
        this.function_id = function_id;
    }

    /**
     * Returns the <code>stat_value</code> value.
     *
     * @return The <code>stat_value</code>.
     */
    public String getStat_value() {
        return stat_value;
    }

    /**
     * Sets the <code>stat_value</code> value.
     *
     * @param stat_value
     *            The <code>stat_value</code> to set.
     */
    public void setStat_value(
            String stat_value) {
        this.stat_value = stat_value;
    }

    @Override
    public String getDescription() {
        return function_id + " = " + stat_value;
    }

}
