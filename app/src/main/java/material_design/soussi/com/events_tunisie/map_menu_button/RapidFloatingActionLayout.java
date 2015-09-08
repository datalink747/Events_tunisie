package material_design.soussi.com.events_tunisie.map_menu_button;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;


import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;



import material_design.soussi.com.events_tunisie.R;
import material_design.soussi.com.events_tunisie.map_menu_button.listener.OnRapidFloatingActionListener;

/**
 * Created by Soussi on 10/05/2015.
 */
public class RapidFloatingActionLayout extends RelativeLayout implements OnClickListener {
    private static final String TAG = RapidFloatingActionLayout.class.getSimpleName();

    public RapidFloatingActionLayout(Context context) {
        super(context);
        initAfterConstructor();
    }

    public RapidFloatingActionLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        parserAttrs(context, attrs, 0, 0);
        initAfterConstructor();
    }

    public RapidFloatingActionLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parserAttrs(context, attrs, defStyleAttr, 0);
        initAfterConstructor();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RapidFloatingActionLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        parserAttrs(context, attrs, defStyleAttr, defStyleRes);
        initAfterConstructor();
    }

    private void parserAttrs(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.RapidFloatingActionLayout, defStyleAttr, defStyleRes);
        frameColor = a.getColor(R.styleable.RapidFloatingActionLayout_rfal_frame_color, getContext().getResources().getColor(R.color.rfab__color_frame));
        frameAlpha = a.getFloat(R.styleable.RapidFloatingActionLayout_rfal_frame_alpha,
                Float.valueOf(getResources().getString(R.string.rfab_rfal__float_convert_color_alpha))
        );

        frameAlpha = frameAlpha > 1f ? 1f : (frameAlpha < 0f ? 0f : frameAlpha);

        a.recycle();

    }

    public static final long ANIMATION_DURATION = 150/*ms*/;

    private void initAfterConstructor() {

    }

    private OnRapidFloatingActionListener onRapidFloatingActionListener;

    public void setOnRapidFloatingActionListener(OnRapidFloatingActionListener onRapidFloatingActionListener) {
        this.onRapidFloatingActionListener = onRapidFloatingActionListener;
    }

    private View fillFrameView;
    private RapidFloatingActionContent contentView;

    private int frameColor;
    private float frameAlpha;

    public RapidFloatingActionLayout setContentView(RapidFloatingActionContent contentView) {
        if (null == contentView) {
            throw new RuntimeException("contentView can not be null");
        }
        if (null != this.contentView) {
            this.removeView(this.contentView);
//            throw new RuntimeException("contentView: [" + this.contentView + "] is already initialed");
            Logger.w(TAG, "contentView: [" + this.contentView + "] is already initialed");
        }
        this.contentView = contentView;
        //
        fillFrameView = new View(getContext());
        fillFrameView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        fillFrameView.setBackgroundColor(frameColor);
        fillFrameView.setVisibility(GONE);
        fillFrameView.setOnClickListener(this);
        this.addView(fillFrameView, Math.max(this.getChildCount() - 1, 0));

        // ????
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ABOVE, onRapidFloatingActionListener.obtainRFAButton().getId());
        lp.addRule(RelativeLayout.ALIGN_RIGHT, onRapidFloatingActionListener.obtainRFAButton().getId());
        this.contentView.setLayoutParams(lp);
        this.contentView.setVisibility(GONE);
        this.addView(this.contentView);
        return this;
    }

    @Override
    public void onClick(View v) {
        if (fillFrameView == v) {
            collapseContent();
        }
    }

    public void setFrameColor(int frameColor) {
        this.frameColor = frameColor;
        if(null != fillFrameView){
            fillFrameView.setBackgroundColor(frameColor);
        }
    }

    public void setFrameAlpha(float frameAlpha) {
        this.frameAlpha = frameAlpha;
    }

    private boolean isExpanded = false;

    public boolean isExpanded() {
        return isExpanded;
    }

    public void toggleContent() {
        if (isExpanded) {
            collapseContent();
        } else {
            expandContent();
        }
    }

    private AnimatorSet animatorSet;

    public void expandContent() {
        if (isExpanded) {
            return;
        }
        endAnimatorSet();
        isExpanded = true;
        contentAnimator.setTarget(this.contentView);
        contentAnimator.setFloatValues(0.0f, 1.0f);
        contentAnimator.setPropertyName("alpha");

        fillFrameAnimator.setTarget(this.fillFrameView);
        fillFrameAnimator.setFloatValues(0.0f, frameAlpha);
        fillFrameAnimator.setPropertyName("alpha");

        animatorSet = new AnimatorSet();
        animatorSet.playTogether(contentAnimator, fillFrameAnimator);
        animatorSet.setDuration(ANIMATION_DURATION);
        animatorSet.setInterpolator(mAccelerateInterpolator);
        onRapidFloatingActionListener.onExpandAnimator(animatorSet);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                contentView.setVisibility(VISIBLE);
                fillFrameView.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isExpanded = true;
            }
        });

        animatorSet.start();

    }

    public void collapseContent() {
        if (!isExpanded) {
            return;
        }
        endAnimatorSet();
        isExpanded = false;
        contentAnimator.setTarget(this.contentView);
        contentAnimator.setFloatValues(1.0f, 0.0f);
        contentAnimator.setPropertyName("alpha");

        fillFrameAnimator.setTarget(this.fillFrameView);
        fillFrameAnimator.setFloatValues(frameAlpha, 0.0f);
        fillFrameAnimator.setPropertyName("alpha");

        animatorSet = new AnimatorSet();
        animatorSet.playTogether(contentAnimator, fillFrameAnimator);
        animatorSet.setDuration(ANIMATION_DURATION);
        animatorSet.setInterpolator(mAccelerateInterpolator);
        onRapidFloatingActionListener.onCollapseAnimator(animatorSet);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                fillFrameView.setVisibility(VISIBLE);
                contentView.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                fillFrameView.setVisibility(GONE);
                contentView.setVisibility(GONE);
                isExpanded = false;
            }
        });
        animatorSet.start();

    }

    private void endAnimatorSet() {
        if (null != animatorSet) {
            animatorSet.end();
        }
    }

    private ObjectAnimator contentAnimator = new ObjectAnimator();
    private ObjectAnimator fillFrameAnimator = new ObjectAnimator();
    private AccelerateInterpolator mAccelerateInterpolator = new AccelerateInterpolator();


}