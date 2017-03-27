package com.company.NetSDK;

import java.io.Serializable;

/**
 * \if ENGLISH_LANG
 * RTSP configuration
 * \else
 * RTSP 配置
 * \endif
 */
public class SDKDEV_RTSP_CFG implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * \if ENGLISH_LANG
	 * port number(can't be the same as tcp or udp's port number)
	 * \else
	 * 端口号
	 * \endif
	 */
    public short               wPort; 
}
