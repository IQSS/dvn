// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 6/24/2008 4:45:22 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   LocalizedDate.java

package edu.harvard.hmdc.vdcnet.web;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

public class LocalizedDate
{

    public LocalizedDate()
    {
    }

    public String getLocalizedDate(Timestamp timestamp, int datestyle)
    {
        Calendar calendar = Calendar.getInstance();
        Locale locale = null;
        if(locale == null)
            locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        DateFormat dateformat = DateFormat.getDateInstance(datestyle, locale);
        String localizedDateString = dateformat.format(timestamp);
        return localizedDateString;
    }
}