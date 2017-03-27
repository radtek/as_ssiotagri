
package com.ssiot.remote.yun.unit.achar;

public class Radar {
    int count;
    float maxVal;
    String name;
    float[] values;
    int[] windValues;
    
    public Radar(float[] vals, int[] windVals, int count){
        values = vals;
        windValues = windVals;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public float getMaxVal() {
        float[] arrayOfFloat;
        int size;
//        if (maxVal == 0F)//改变了val之后也要计算
//        {
            arrayOfFloat = values;
            size = arrayOfFloat.length;
            for (int j = 0; j < size; j++) {
                if (arrayOfFloat[j] > maxVal) {
                    maxVal = arrayOfFloat[j];
                }
            }
//        }

        return maxVal;
    }

    public String getName() {
        return name;
    }

    public float[] getValues() {
        return values;
    }

    public int[] getWindValues() {
        return windValues;
    }

    public void setCount(int paramInt) {
        count = paramInt;
    }

    public void setName(String paramString) {
        name = paramString;
    }

    public void setValues(float[] paramArrayOfFloat) {
        values = paramArrayOfFloat;
    }

    public void setWindValues(int[] paramArrayOfInt) {
        windValues = paramArrayOfInt;
    }
}
