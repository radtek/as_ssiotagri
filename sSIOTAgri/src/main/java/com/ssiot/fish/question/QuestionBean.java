
package com.ssiot.fish.question;

import android.text.SpannableStringBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

//copy from package com.satan.peacantdoctor.question.b.i.class;
public class QuestionBean implements Serializable {
    public final ArrayList<String> aPics = new ArrayList<String>();
    public final ArrayList<String> bPicUrls = new ArrayList<String>();
    public final ArrayList<String> cPicThumbs = new ArrayList<String>();
    public int id;
    public String extra;
    public long fCreateT;
    public long gUpdateT;
    public int hModiCount;
    public int iReplyCount;
    public boolean j_is_fav;
    public boolean kHasCn;
    public boolean lGood;
    public boolean mHastJ;
    public double nLat;
    public double oLon;
    public boolean pModifiable;
    public AskerModel qAskerModel;// f q
    public String rdistrict;
    public String sCrop;
    private final ArrayList tLinks = new ArrayList();
    public CharSequence uContent;
    private SStrBuilder vStrBui;
    private int wState;
    private int x;

    public QuestionBean() {
        extra = "";
        qAskerModel = new AskerModel();
        rdistrict = "";
        sCrop = "";
        uContent = "";
    }

    public QuestionBean(JSONObject paramJSONObject) {
        extra = "";
        qAskerModel = new AskerModel();
        rdistrict = "";
        sCrop = "";
        uContent = "";
        parseJson(paramJSONObject);
    }

    public void parseJson(JSONObject paramJSONObject) {
        try {
            id = paramJSONObject.optInt("id");
            uContent = paramJSONObject.optString("content", "");
            extra = paramJSONObject.optString("extra", "");
            rdistrict = paramJSONObject.optString("district", "");//地区
            sCrop = paramJSONObject.optString("crop", "");
            fCreateT = paramJSONObject.optLong("createtime");
            gUpdateT = paramJSONObject.optLong("updatetime");
            hModiCount = paramJSONObject.optInt("modifyCount");
            iReplyCount = paramJSONObject.optInt("replycount");
            wState = paramJSONObject.optInt("state");
            j_is_fav = paramJSONObject.optBoolean("is_fav");
            kHasCn = paramJSONObject.optBoolean("hascn");
            lGood = paramJSONObject.optBoolean("good");
            mHastJ = paramJSONObject.optBoolean("hastj");
            nLat = paramJSONObject.optDouble("lat");
            oLon = paramJSONObject.optDouble("lon");
            pModifiable = paramJSONObject.optBoolean("modifiable");
            JSONArray localJSONArray1 = paramJSONObject.optJSONArray("pics");
            if (localJSONArray1 != null)
                for (int i4 = 0; i4 < localJSONArray1.length(); ++i4)
                    aPics.add(localJSONArray1.getString(i4));
            JSONArray localJSONArray2 = paramJSONObject.optJSONArray("picUrls");
            if (localJSONArray2 != null)
                for (int i3 = 0; i3 < localJSONArray2.length(); ++i3)
                    bPicUrls.add(localJSONArray2.getString(i3));
            JSONArray localJSONArray3 = paramJSONObject.optJSONArray("picThumbs");
            if (localJSONArray3 != null)
                for (int i2 = 0; i2 < localJSONArray3.length(); ++i2)
                    cPicThumbs.add(localJSONArray3.getString(i2));
            JSONObject askerObject = paramJSONObject.optJSONObject("asker");
            if (askerObject != null)
                qAskerModel = new AskerModel(askerObject);
            JSONArray localJSONArray4 = paramJSONObject.optJSONArray("links");// 对话文字中的点击自动链接功能
            if (localJSONArray4 != null) {
                tLinks.clear();
                int i1 = 0;
                while (i1 < localJSONArray4.length()) {
                    hModel localh = new hModel(localJSONArray4.optJSONObject(i1));
                    tLinks.add(localh);
                    ++i1;
                }
            }
            // i_func();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void i_func() {
        vStrBui = new SStrBuilder(uContent);
        if (tLinks.size() > 0) {
            for (int i = 0; i < tLinks.size(); i++) {
                int start;
                hModel localh = (hModel) tLinks.get(i);
                int end = 0;
                if (uContent.toString().indexOf(localh.aWord, end) >= 0) {
                    start = uContent.toString().indexOf(localh.aWord, end);
                    end = start + localh.aWord.length();
//                    vStrBui.setSpan(new ClickableSpan(localh.bUrl), start, end, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);// jingbo
                    break;
                }
            }
        }
        
    }
}
