package com.livio.sdl.enums;

import java.util.Arrays;
import java.util.EnumSet;

import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * Specifies the language to be used for TTS, VR, displayed messages/menus
 * <p>
 * 
 * @since AppLink 1.0
 *
 */
public enum SdlLanguage {
	//TODO - change these to actual readable language names, maybe?
    EN_US("EN-US"),
    ES_MX("ES-MX"),
    FR_CA("FR-CA"),
    DE_DE("DE-DE"),
    ES_ES("ES-ES"),
    EN_GB("EN-GB"),
    RU_RU("RU-RU"),
    TR_TR("TR-TR"),
    PL_PL("PL-PL"),
    FR_FR("FR-FR"),
    IT_IT("IT-IT"),
    SV_SE("SV-SE"),
    PT_PT("PT-PT"),
    NL_NL("NL-NL"),
    EN_AU("EN-AU"),
    ZH_CN("ZH-CN"),
    ZH_TW("ZH-TW"),
    JA_JP("JA-JP"),
    AR_SA("AR-SA"),
    KO_KR("KO-KR"),
    PT_BR("PT-BR"),
    CS_CZ("CS-CZ"),
    DA_DK("DA-DK"),
    NO_NO("NO-NO")
    
    // future languages go here
    
    ;

    private final String READABLE_NAME;
    
    private SdlLanguage(String readableName) {
        this.READABLE_NAME = readableName;
    }
	
	//public member methods
	public String getReadableName(){
		return this.READABLE_NAME;
	}
    
    /**
     * Returns a Language's name.  This method iterates through every enum in the list,
     * but it won't be used as often as SyncCommand, so we'll sacrifice the reverse look-up
     * HashMap in this case.  Without the HashMap, this method will certainly take longer
     * to run, but it isn't worth the memory hit since languages will be used
     * much less often than SyncCommand. 
     * 
     * @param value a String
     * @return Language -EN-US, ES-MX or FR-CA
     */
    public static SdlLanguage lookupByReadableName(String readableName) {       	
    	for (SdlLanguage anEnum : EnumSet.allOf(SdlLanguage.class)) {
            if (anEnum.getReadableName().equals(readableName)) {
                return anEnum;
            }
        }
        return null;
    }
    
    public static ArrayAdapter<SdlLanguage> getListAdapter(Context context){
    	ArrayAdapter<SdlLanguage> result = new ArrayAdapter<SdlLanguage>(context, android.R.layout.simple_list_item_1, values());
    	return result;
    }
    
    public static ArrayAdapter<SdlLanguage> getSpinnerAdapter(Context context){
    	ArrayAdapter<SdlLanguage> result = new ArrayAdapter<SdlLanguage>(context, android.R.layout.simple_spinner_dropdown_item, values());
    	return result;
    }
	
	/**
	 * Returns an array of the objects in this enum sorted in alphabetical order.
	 * 
	 * @return The sorted array
	 */
	public static SdlLanguage[] getSortedArray(){
		SdlLanguage[] result = values();
		Arrays.sort(result, new EnumComparator<SdlLanguage>());
		return result;
	}
	
	@Override
	public String toString(){
		return this.READABLE_NAME;
	}
}
