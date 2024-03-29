package com.company.NetSDK;

import java.io.Serializable;

/**
 * \if ENGLISH_LANG
 * ACC power off configuration
 * \else
 * ACC断线报警事件配置
 * \endif
 */
public class SDKDEV_ACC_POWEROFF_CFG implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * \if ENGLISH_LANG
	 * ACC power off configuration enable
	 * \else
	 * ACC断线报警使能
	 * \endif
	 */
	public boolean                bEnable;

	/**
	 * \if ENGLISH_LANG
	 * ACC power off delay time(m)
	 * \else
	 * ACC断线报警延时时间,单位分钟
	 * \endif
	 */
	public int                 nDelayTime;

	/**
	 * \if ENGLISH_LANG
	 * handle method
	 * \else
	 * 处理方式
	 * \endif
	 */
	public SDK_MSG_HANDLE_EX    struHandle = new SDK_MSG_HANDLE_EX();
}
