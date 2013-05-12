package net.emmanuelsotelo.projectdow.indicators;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "net.emmanuelsotelo.ProjectDow.indicators.messages"; //$NON-NLS-1$
    
    public static String WeightPreferencePage_Color;
    public static String WeightPreferencePage_Label;
    public static String WeightPreferencePage_LineType;
    public static String WeightPreferencePage_Multiplier;

	public static String InputSecurity_Default;
    
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
    }
}

