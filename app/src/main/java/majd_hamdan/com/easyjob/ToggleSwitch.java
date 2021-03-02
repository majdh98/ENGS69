package majd_hamdan.com.easyjob;

// based on implementation from stackoverflow
// https://stackoverflow.com/questions/22526348/android-text-should-appear-both-side-in-the-switch

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

public class ToggleSwitch extends Drawable {

    private final Context theContext;
    private final String leftText;
    private final String rightText;
    private final Paint textPaint;

    public ToggleSwitch(
            @NonNull Context context,
            String leftTextIn,
            String rightTextIn){
        theContext = context;

        // Left Text
        leftText = leftTextIn;

        // painter
        textPaint = createTextPaint();

        // Right Text
        rightText = rightTextIn;
    }

    private Paint createTextPaint(){
        Paint textPaint = new Paint();
        textPaint.setColor(theContext.getColor(R.color.primaryDarkColor));
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextAlign(Paint.Align.CENTER);
        return textPaint;
    }

    @Override
    public void draw(Canvas canvas){
        final Rect textBounds = new Rect();
        textPaint.getTextBounds(rightText, 0, rightText.length(), textBounds);

        final int heightBaseline = canvas.getClipBounds().height() / 2 + textBounds.height() / 2;

        final int widthQuarter = canvas.getClipBounds().width() / 4;

        canvas.drawText(leftText, 0, leftText.length(), widthQuarter, heightBaseline, textPaint);
        canvas.drawText(rightText, 0, rightText.length(), widthQuarter * 3, heightBaseline, textPaint);

    }

    @Override
    public void setAlpha(int alpha){

    }

    @Override
    public void setColorFilter(ColorFilter filter){

    }

    @Override
    public int getOpacity(){
        return PixelFormat.TRANSLUCENT;
    }

}
