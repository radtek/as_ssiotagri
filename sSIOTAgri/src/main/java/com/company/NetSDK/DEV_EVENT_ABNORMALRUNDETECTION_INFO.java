package com.company.NetSDK;

import java.io.Serializable;

/**
 * \if ENGLISH_LANG
 * the describe of EVENT_IVS_ABNORMALRUNDETECTION's data
 * \else
 * 事件类型EVENT_IVS_ABNORMALRUNDETECTION(异常奔跑事件)对应的数据块描述信息
 * \endif
 */
public class DEV_EVENT_ABNORMALRUNDETECTION_INFO implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * \if ENGLISH_LANG
	 * ChannelId
	 * \else
	 * 通道号
	 * \endif
	 */
	public int                 nChannelID;
	
	/**
	 * \if ENGLISH_LANG
	 * event name
	 * \else
	 * 事件名称
	 * \endif
	 */
	public byte                szName[] = new byte[128];
	
	/**
	 * \if ENGLISH_LANG
	 * PTS(ms)
	 * \else
	 * 时间戳(单位是毫秒)
	 * \endif
	 */
	public double              PTS;
	
	/**
	 * \if ENGLISH_LANG
	 * the event happen time
	 * \else
	 * 事件发生的时间
	 * \endif
	 */
	public NET_TIME_EX         UTC = new NET_TIME_EX();
	
	/**
	 * \if ENGLISH_LANG
	 * event ID
	 * \else
	 * 事件ID
	 * \endif
	 */
	public int                 nEventID;
	
	/**
	 * \if ENGLISH_LANG
	 * have being detected object
	 * \else
	 * 检测到的物体
	 * \endif
	 */
	public SDK_MSG_OBJECT       stuObject = new SDK_MSG_OBJECT(); 
	
	/**
	 * \if ENGLISH_LANG
	 * speed ,km/h
	 * \else
	 * 物体运动速度,km/h
	 * \endif
	 */
	public double              dbSpeed; 
	
	/**
	 * \if ENGLISH_LANG
	 * triggerSpeed,km/h
	 * \else
	 * 触发速度,km/h
	 * \endif
	 */
	public double              dbTriggerSpeed; 
	
	/**
	 * \if ENGLISH_LANG
	 * detect region's point number
	 * \else
	 * 规则检测区域顶点数
	 * \endif
	 */
	public int                 nDetectRegionNum; 
	
	/**
	 * \if ENGLISH_LANG
	 * detect region info
	 * \else
	 * 规则检测区域
	 * \endif
	 */
	public SDK_POINT            DetectRegion[] = new SDK_POINT[FinalVar.SDK_MAX_DETECT_REGION_NUM]; 
	
	/**
	 * \if ENGLISH_LANG
	 * track line point number
	 * \else
	 * 物体运动轨迹顶点数
	 * \endif
	 */
	public int                 nTrackLineNum; 
	
	/**
	 * \if ENGLISH_LANG
	 * track line info
	 * \else
	 * 物体运动轨迹
	 * \endif
	 */
	public SDK_POINT            TrackLine[] = new SDK_POINT[FinalVar.SDK_MAX_TRACK_LINE_NUM]; 
	
	/**
	 * \if ENGLISH_LANG
	 * event file info
	 * \else
	 * 事件对应文件信息
	 * \endif
	 */
	public SDK_EVENT_FILE_INFO  stuFileInfo = new SDK_EVENT_FILE_INFO(); 
	
	/**
	 * \if ENGLISH_LANG
	 * Event action,0 means pulse event,1 means continuous event's begin,2means continuous event's end;
	 * \else
	 * 事件动作，0表示脉冲事件,1表示持续性事件开始,2表示持续性事件结束;
	 * \endif
	 */
	public byte                bEventAction; 
	
	/**
	 * \if ENGLISH_LANG
	 * type, 0-run fast, 1-sudden speedup, 2-sudden speed-down
	 * \else
	 * 异常奔跑类型, 0-快速奔跑, 1-突然加速, 2-突然减速
	 * \endif
	 */
	public byte                bRunType; 
	
	/**
	 * \if ENGLISH_LANG
	 * Serial number of the picture, in the same time (accurate to seconds) may have multiple images, starting from 0
	 * \else
	 * 图片的序号, 同一时间内(精确到秒)可能有多张图片, 从0开始
	 * \endif
	 */
	public byte                byImageIndex; 
	
	/**
	 * \if ENGLISH_LANG
	 * flag(by bit),see NET_RESERVED_COMMON
	 * \else
	 * 抓图标志(按位)，具体见NET_RESERVED_COMMON
	 * \endif
	 */
	public int               dwSnapFlagMask; 
	
	/**
	 * \if ENGLISH_LANG
	 * the source device's index,-1 means data in invalid
	 * \else
	 * 事件源设备上的index,-1表示数据无效
	 * \endif
	 */
	public int                 nSourceIndex; 
	
	/**
	 * \if ENGLISH_LANG
	 * the source device's sign(exclusive),field said local device does not exist or is empty
	 * \else
	 * 事件源设备唯一标识,字段不存在或者为空表示本地设备
	 * \endif
	 */
	public byte                szSourceDevice[] = new byte[FinalVar.MAX_PATH]; 
	
	/**
	 * \if ENGLISH_LANG
	 * event trigger accumilated times 
	 * \else
	 * 事件触发累计次数
	 * \endif
	 */
	public int        nOccurrenceCount; 
	
	public DEV_EVENT_ABNORMALRUNDETECTION_INFO() {
		for (int i = 0; i < FinalVar.SDK_MAX_DETECT_REGION_NUM; i++) {
			DetectRegion[i] = new SDK_POINT();
		}
		
		for (int i = 0; i < FinalVar.SDK_MAX_TRACK_LINE_NUM; i++) {
			TrackLine[i] = new SDK_POINT();
		}
	}
}
