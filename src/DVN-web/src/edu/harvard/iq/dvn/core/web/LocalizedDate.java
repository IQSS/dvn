// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 6/24/2008 4:45:22 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   LocalizedDate.java

package edu.harvard.iq.dvn.core.web;

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
        DateFormat dateformat = DateFormat.getDateInstance(datestyle);
        String localizedDateString = dateformat.format(timestamp);
        return localizedDateString;
    }
}