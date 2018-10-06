package ir.hosfa.wemos_relay;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class MyTextView extends AppCompatTextView {

    //static Typeface tf;

    public MyTextView(Context context) {
        super(context);
        init();
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

      //Typeface  tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/BKARIMBD.TTF");
//      Typeface  tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/vazir/Vazir.ttf");
      Typeface  tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Yekan.ttf");
      setTypeface(tf, 1);

    }

}
