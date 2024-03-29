package com.company.NetSDK;

import java.io.Serializable;

/**
 * \if ENGLISH_LANG
 * Alarming Decoder Alarm
 * \else
 * 报警解码器报警
 * \endif
 */
public class ALARM_DECODER_ALARM implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

 	/**
	 * \if ENGLISH_LANG
	 * Current Alarming Decoder Numbers 
	 * \else
	 * 发生报警的报警解码器个数 
	 * \endif
	 */
	public int                 nAlarmDecoderNum;
	
 	/**
	 * \if ENGLISH_LANG
	 * Alarming Decoder Alarm 
	 * \else
	 * 警解码器报警 
	 * \endif
	 */
	public ALARM_DECODER       stuAlarmDecoder[] = new ALARM_DECODER[FinalVar.MAX_ALARM_DECODER_NUM];
	
	public ALARM_DECODER_ALARM() {
		for (int i = 0; i < FinalVar.MAX_ALARM_DECODER_NUM; i++) {
			stuAlarmDecoder[i] = new ALARM_DECODER();
		}
	}
}
