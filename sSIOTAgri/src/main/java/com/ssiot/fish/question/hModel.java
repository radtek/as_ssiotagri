
package com.ssiot.fish.question;

import java.io.Serializable;
import org.json.JSONObject;

class hModel implements Serializable {
    public String aWord = "";
    public String bUrl;

    public hModel(JSONObject paramJSONObject) {
        try {
            this.aWord = paramJSONObject.optString("word", "");
            this.bUrl = paramJSONObject.optString("url", "");
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
