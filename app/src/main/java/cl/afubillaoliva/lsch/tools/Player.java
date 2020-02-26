package cl.afubillaoliva.lsch.tools;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class Player extends VideoView {

    private int mWidth, mHeight;
    private final int defaultWidth = 544, defaultHeight = 360;
    private DisplayMode displayMode = DisplayMode.ORIGINAL;

    public enum DisplayMode {
        ORIGINAL,
        FULL_SCREEN,
        ZOOM
    }

    public Player(Context context){
        super(context);
    }

    public Player(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public Player(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        mWidth = 0;
        mHeight = 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(mHeight, heightMeasureSpec);

        if(displayMode == DisplayMode.ORIGINAL)
            if(mWidth > 0 && mHeight > 0)
                height = width * mHeight / mWidth;
            else if (mWidth * height < width * mHeight)
                width = height * mWidth / mHeight;
        else if (displayMode == DisplayMode.FULL_SCREEN){}
            //USE THE DEFAULT SCREEN WIDTH AND HEIGHT
        else if (displayMode == DisplayMode.ZOOM)
            if(mWidth > 0 && mHeight > 0 && mWidth < width)
                height = mHeight * width / mWidth;
        setMeasuredDimension(width, height);
    }

    public void changeVideoSize(int width, int height){
        mWidth = width;
        mHeight = height;
        getHolder().setFixedSize(width, height);
        requestLayout();
        invalidate();
    }

    public void setDisplayMode(DisplayMode mode){
        displayMode = mode;
    }

}
