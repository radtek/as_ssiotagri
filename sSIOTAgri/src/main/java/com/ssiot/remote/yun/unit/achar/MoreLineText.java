package com.ssiot.remote.yun.unit.achar;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.Log;
import android.view.KeyEvent;
import java.util.Vector;

public class MoreLineText{
    private static final String tag = "MoreLineText";
  private int mCurrentLine;
  private int mFontHeight;
  private int mPageLineNum;
  private Paint mPaint;
  private int mRealLine;
  private String mStrText;
  private Vector mString;
  private int mTextHeight;
  private int mTextPosx = 0;
  private int mTextPosy = 0;
  private int mTextWidth = 0;

  public MoreLineText(String strText, int posx, int posy, int width, int height, Paint paramPaint) {
    mTextHeight = 0;
    mFontHeight = 0;
    mPageLineNum = 0;
    mRealLine = 0;//0代表一行
    mCurrentLine = 0;
    mStrText = "";
    mString = null;
    mPaint = null;
    mPaint = paramPaint;
    mString = new Vector();
    mStrText = strText;
    mTextPosx = posx;
    mTextPosy = posy;
    mTextWidth = width;
    mTextHeight = height;
  }

  public void DrawText(Canvas canvas) {//TODO
    int i = mCurrentLine;
    int j = 0;
    Log.v(tag, "-----------------x:" + mTextPosx + " y:" + mTextPosy + " "+ mString.toString() + " realLine" + mRealLine + " mpagelinenum:" + mPageLineNum);
    canvas.drawText((String)mString.elementAt(i), mTextPosx, mTextPosy + j * mFontHeight, mPaint);
//    if (){
//        
//    }
//    while (i < mRealLine){
//        while(j < mPageLineNum){
//            Log.v(tag, "-----------------x:" + mTextPosx + " y:" + mTextPosy + " "+ mString.toString() + " realLine" + mRealLine + " mpagelinenum:" + mPageLineNum);
//            canvas.drawText((String)mString.elementAt(i), mTextPosx, mTextPosy + j * mFontHeight, mPaint);
//            ++i;
//            ++j;
//        }
//    }
  }

  private void GetTextInfo(){//TODO
    int wid = 0;
    int beginIndex = 0;
    Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
    mFontHeight = (int)(-0.0D + Math.ceil(fontMetrics.descent - fontMetrics.top));
    mPageLineNum = (mTextHeight / mFontHeight);
    int lenth = mStrText.length();
    int endIndex = 0;/*
    while (endIndex < lenth -1){
        char c = mStrText.charAt(endIndex);
        float[] widths = new float[1];
        String str = String.valueOf(c);
        mPaint.getTextWidths(str, widths);
        if (c == '\n') {
          mRealLine = 1 + mRealLine;
          mString.addElement(mStrText.substring(beginIndex, endIndex));
          beginIndex = endIndex + 1;
          wid = 0;
        }
        
        wid += (int)Math.ceil(widths[0]);
        if (wid > mTextWidth){
            mRealLine = 1 + mRealLine;
            mString.addElement(mStrText.substring(beginIndex, endIndex));
            beginIndex = endIndex;
            --endIndex;
            wid = 0;
        }
        
        mRealLine = 1 + mRealLine;
        mString.addElement(mStrText.substring(beginIndex, lenth));
        endIndex ++;
    }*/
    mString.addElement(mStrText);
  }

  public void InitText(){
    mString.clear();
    GetTextInfo();
  }

//  public boolean KeyDown(int paramInt, KeyEvent keyEvent){
//    if (paramInt == 19)
//      if (mCurrentLine > 0)
//        mCurrentLine = (-1 + mCurrentLine);
//    while (true)
//    {
//      do
//        return false;
//      while ((paramInt != 20) || (mCurrentLine + mPageLineNum >= -1 + mRealLine);
//      mCurrentLine = (1 + mCurrentLine);
//    }
//  }
}