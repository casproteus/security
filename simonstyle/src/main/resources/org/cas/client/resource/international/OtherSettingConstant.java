package org.cas.client.resource.international;

/***/
public interface OtherSettingConstant
{
    /* The follow contents are used for other setting dialog */
    /* title of dialog */
     String OTHER_SETTING_TITLE = "Format Day/Week/Month View";

    /* option of day view */
     String DAY_TITLE = "Day";
    /* option of week view */
     String WEEK_TITLE = "Week";
    /* option of month view */
     String MONTH_TITLE = "Month";
    /* general settings */
     String GENERAL_TITLE = "General settings";

    /* font of time bar */
     String TIME_FONT = "Time Font...";
    /* time scale */
     String TIME = "Time scale:";
    /* font of day, week, month view */
     String VIEW_FONT = "Font...";
    /* show time as clock's image */
     String SHOW_TIME_CLOCK = "Show time as clocks";
    /* show end time */
     String SHOW_END_TIME = "Show end time";
    /* compress weekend */
     String COMPRESS_WEEKEND = "Compress weekend days";
    /* bolder dates in calendar represent days containing event */
     String BOLDED_DATES = "Bolded dates in Date Navigator represent days containing items";

    /* unit of time scale */
     String SCALE_MINUTES = "minutes";
    /* lunar calendar information */
     String LUNAR_TYPE = "Lunar Calendar Information Types:";
    /* lunar calendar information type */
     String[] LUNARY_TYPE_ARRAY  =
    {
        "Display 30-day Calendar",
        "Hide Lunar Calendar",
        "Display Heavenly Stems/Earthly Branches"
    };
}
