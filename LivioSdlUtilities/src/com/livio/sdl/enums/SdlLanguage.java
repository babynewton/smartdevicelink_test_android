package com.livio.sdl.enums;

import java.util.EnumSet;

/**
 * Specifies the language to be used for TTS, VR, displayed messages/menus
 * <p>
 * 
 * @since SmartDeviceLink 1.0
 *
 */
public enum SdlLanguage {
	/**
	 * American English
	 */
    EN_US("EN-US"),
    /**
     * Australian English
     */
    EN_AU("EN-AU"),
    /**
     * British English
     */
    EN_GB("EN-GB"),
    
    /**
     * Mexico Spanish
     */
    ES_MX("ES-MX"),
    /**
     * Spain Spanish
     */
    ES_ES("ES-ES"),

    /**
     * France French
     */
    FR_FR("FR-FR"),
    /**
     * Canadian French
     */
    FR_CA("FR-CA"),
    
    /**
     * German
     */
    DE_DE("DE-DE"),
    
    /**
     * Russian
     */
    RU_RU("RU-RU"),
    
    /**
     * Turkish
     */
    TR_TR("TR-TR"),
    
    /**
     * Polish
     */
    PL_PL("PL-PL"),
    
    /**
     * Italian
     */
    IT_IT("IT-IT"),
    
    /**
     * Swedish
     */
    SV_SE("SV-SE"),
    
    /**
     * Portuguese
     */
    PT_PT("PT-PT"),
    /**
     * Brazilian Portuguese
     */
    PT_BR("PT-BR"),
    
    /**
     * Dutch
     */
    NL_NL("NL-NL"),
    
    /**
     * Traditional Chinese
     */
    ZH_CN("ZH-CN"),
    /**
     * Thai Chinese
     */
    ZH_TW("ZH-TW"),
    
    /**
     * Japanese
     */
    JA_JP("JA-JP"),
    
    /**
     * Arabic
     */
    AR_SA("AR-SA"),
    
    /**
     * Korean
     */
    KO_KR("KO-KR"),
    
    /**
     * Czech
     */
    CS_CZ("CS-CZ"),
    
    /**
     * Danish
     */
    DA_DK("DA-DK"),
    
    /**
     * Norwegian
     */
    NO_NO("NO-NO"),
    
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
	
	@Override
	public String toString(){
		return this.READABLE_NAME;
	}
}
