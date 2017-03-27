package com.ssiot.fish.question;

import android.text.SpannableStringBuilder;
import java.io.Serializable;

public class SStrBuilder extends SpannableStringBuilder
  implements Serializable
{
  public SStrBuilder(CharSequence paramCharSequence)
  {
    super(paramCharSequence);
  }
}