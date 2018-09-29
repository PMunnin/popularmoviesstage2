package com.example.android.popular_movies;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;


public final class SpannableUtilities {

    /**
     * Suppressed constructor to avoid errors for example in class reflection
     */
    private SpannableUtilities() {}

    public static SpannableString makeBold(String string) {
        SpannableString boldText = new SpannableString(string);
        boldText.setSpan(new StyleSpan(Typeface.BOLD), 0, boldText.length(), 0);
        return boldText;
    }
}
