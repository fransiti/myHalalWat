package com.example.yasina.myhalalwat.View;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import com.example.yasina.myhalalwat.R;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

/**
 * Created by Ilya Eremin on 09.03.2015.
 */
public class PrayerTimelineView extends View {

    Paint greenPaint    = new Paint();
    Paint whiteDotPaint = new Paint();
    Paint greenDotPaint = new Paint();
    Paint fontPaint     = new Paint();
    Paint grayPaint     = new Paint();
    Paint grayFill      = new Paint();
    int pixelsIn8dp;
    int pixelsIn6dp;
    int pixelsIn2dp;
    int currentHour;
    int currentMinute;
    int timeFontSize;
    private int minutesFromDayStart;

    String currentTime;
    int    firstNamazTime;
    int    lastNamazTime;
    private int[] namazesOffsetInMinutesFromDayStart;

    public PrayerTimelineView(Context context) {
        super(context);
        init(context);
    }

    public PrayerTimelineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        pixelsIn2dp = context.getResources().getDimensionPixelSize(R.dimen.dim_2dp);
        pixelsIn8dp = context.getResources().getDimensionPixelSize(R.dimen.dim_8dp);
        pixelsIn6dp = context.getResources().getDimensionPixelSize(R.dimen.dim_6dp);
        greenPaint.setAntiAlias(true);
        greenPaint.setStrokeWidth(pixelsIn2dp);
        greenPaint.setColor(context.getResources().getColor(R.color.halal_green));
        greenPaint.setStyle(Paint.Style.STROKE);
        greenPaint.setStrokeJoin(Paint.Join.ROUND);

        whiteDotPaint.setAntiAlias(true);
        whiteDotPaint.setStyle(Paint.Style.FILL);
        whiteDotPaint.setColor(Color.WHITE);

        greenDotPaint.setAntiAlias(true);
        greenDotPaint.setStyle(Paint.Style.FILL);
        greenDotPaint.setColor(context.getResources().getColor(R.color.halal_green));

        timeFontSize = context.getResources().getDimensionPixelSize(R.dimen.dim_12sp);
        fontPaint.setColor(Color.WHITE);
        fontPaint.setTextSize(timeFontSize);
        fontPaint.setTextAlign(Paint.Align.CENTER);

        grayPaint.setAntiAlias(true);
        grayPaint.setStrokeWidth(pixelsIn2dp);
        grayPaint.setStyle(Paint.Style.STROKE);
        grayPaint.setStrokeJoin(Paint.Join.ROUND);
        grayPaint.setColor(context.getResources().getColor(R.color.namaz_timeline_gray));

        grayFill.setAntiAlias(true);
        grayFill.setStyle(Paint.Style.FILL);
        grayFill.setColor(context.getResources().getColor(R.color.namaz_timeline_gray));

    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        if (isInEditMode()) {
            setUp(new int[]{300, 500, 700, 900, 1100, 1300});
            setTime(Calendar.getInstance());
        }
    }

    public void setTime(Calendar calendar) {
        currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        currentMinute = calendar.get(Calendar.MINUTE);
        minutesFromDayStart = currentHour * 60 + currentMinute;
        currentTime = String.format("%02d:%02d", currentHour, currentMinute);
    }

    public void setUp(int[] namazesTimeInMinutesFromDayStart) {
        this.namazesOffsetInMinutesFromDayStart = namazesTimeInMinutesFromDayStart;
        firstNamazTime = namazesTimeInMinutesFromDayStart[0];
        lastNamazTime = namazesTimeInMinutesFromDayStart[namazesTimeInMinutesFromDayStart.length - 1];
        invalidate();
    }

    @Override public void draw(@NotNull Canvas canvas) {
        super.draw(canvas);
        Paint.FontMetrics fm = new Paint.FontMetrics();

        int top = getPaddingTop();
        int bottom = getHeight() - getPaddingBottom();
        int heightWithoutPadding = bottom - top;

        int currentTimeHeight = 0;
        int halfOfWidth = getWidth() / 2;
        if (minutesFromDayStart > lastNamazTime) {
            canvas.drawLine(halfOfWidth, top, halfOfWidth, bottom, greenPaint);
        } else if (minutesFromDayStart < firstNamazTime) {
            canvas.drawLine(halfOfWidth, top, halfOfWidth, bottom, grayPaint);
        } else {
            currentTimeHeight = calculateWatchPosition(top, heightWithoutPadding);
            canvas.drawLine(halfOfWidth, top, halfOfWidth, currentTimeHeight, greenPaint);
            canvas.drawLine(halfOfWidth, currentTimeHeight, halfOfWidth, bottom, grayPaint);
        }

        for (int i = 0; i < 6; i++) {
            int dotHeight = top + heightWithoutPadding / 5 * i;
            if (currentTimeHeight < dotHeight - heightWithoutPadding / 5) {
                canvas.drawCircle(halfOfWidth, dotHeight, pixelsIn6dp, whiteDotPaint);
                canvas.drawCircle(halfOfWidth, dotHeight, pixelsIn8dp / 2, grayFill);
            } else {
                canvas.drawCircle(halfOfWidth, dotHeight, pixelsIn8dp, whiteDotPaint);
                canvas.drawCircle(halfOfWidth, dotHeight, pixelsIn6dp, greenDotPaint);
                canvas.drawCircle(halfOfWidth, dotHeight, pixelsIn8dp / 2, whiteDotPaint);
            }

        }

        if (currentTimeHeight != 0) {
            fontPaint.getFontMetrics(fm);
            canvas.drawRect(halfOfWidth - fontPaint.measureText(currentTime) / 2 - pixelsIn2dp * 2,
                    currentTimeHeight,
                    halfOfWidth + fontPaint.measureText(currentTime) / 2 + pixelsIn2dp * 2,
                    currentTimeHeight + fontPaint.getTextSize() + pixelsIn2dp * 2,
                    greenDotPaint);
            canvas.drawText(currentTime,
                    halfOfWidth,
                    currentTimeHeight + fontPaint.getTextSize(),
                    fontPaint);
        }
    }

    private int calculateWatchPosition(int top, int heightWithoutPadding) {
        int currentTimeHeight = 0;
        if(namazesOffsetInMinutesFromDayStart == null) return 0;

        for (int i = 0; i < namazesOffsetInMinutesFromDayStart.length - 1; i++) {
            if (minutesFromDayStart >= namazesOffsetInMinutesFromDayStart[i]
                    && minutesFromDayStart < namazesOffsetInMinutesFromDayStart[i + 1]) {
                int sectionHeight = heightWithoutPadding / 5;
                int topOffsetInSector = sectionHeight * (minutesFromDayStart - namazesOffsetInMinutesFromDayStart[i]) /
                        (namazesOffsetInMinutesFromDayStart[i + 1] - namazesOffsetInMinutesFromDayStart[i]);
                if (topOffsetInSector < pixelsIn8dp + timeFontSize / 2) {
                    topOffsetInSector = pixelsIn8dp;
                } else if (topOffsetInSector > sectionHeight - pixelsIn8dp - timeFontSize / 2) {
                    topOffsetInSector = sectionHeight - pixelsIn8dp - timeFontSize - pixelsIn2dp;
                }
                currentTimeHeight = top + heightWithoutPadding * i / 5 + topOffsetInSector;
                break;
            }
        }
        return currentTimeHeight;
    }
}