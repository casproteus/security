package com.stgo.security.monitor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class SecurityLog extends org.springframework.core.io.AbstractResource {

    private long id; // record id.

    private String group_name; // company name or other group name.

    private String computer_name; // computer name or id.

    private String label; // contains the lables, could be used for searching.

    private String description; // detail message.

    private Date created; // generated time.

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
     * Returns the <code>description</code> value.
     *
     * @return The <code>description</code>.
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Sets the <code>description</code> value.
     *
     * @param description
     *            The <code>description</code> to set.
     */
    public void setDescription(
            String description) {
        this.description = description;
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

    /**
     * Returns the <code>label</code> value.
     *
     * @return The <code>label</code>.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the <code>label</code> value.
     *
     * @param label
     *            The <code>label</code> to set.
     */
    public void setLabel(
            String label) {
        this.label = label;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return null;
    }

    /**
     * Returns the <code>created</code> value.
     *
     * @return The <code>created</code>.
     */
    public Date getCreated() {
        return created;
    }

    /**
     * Sets the <code>created</code> value.
     *
     * @param created
     *            The <code>created</code> to set.
     */
    public void setCreated(
            Date created) {
        this.created = created;
    }

}
