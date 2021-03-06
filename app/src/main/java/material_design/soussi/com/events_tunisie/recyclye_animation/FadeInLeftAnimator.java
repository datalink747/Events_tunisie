package material_design.soussi.com.events_tunisie.recyclye_animation;

/**
 * Created by Soussi on 07/07/2015.
 */
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
public class FadeInLeftAnimator extends BaseItemAnimator {

    @Override
    protected void animateRemoveImpl(final RecyclerView.ViewHolder holder) {
        ViewCompat.animate(holder.itemView)
                .translationX(-holder.itemView.getRootView().getWidth() * .25f)
                .alpha(0)
                .setDuration(getRemoveDuration())
                .setListener(new DefaultRemoveVpaListener(holder))
                .start();
        mRemoveAnimations.add(holder);
    }

    @Override
    protected void preAnimateAdd(RecyclerView.ViewHolder holder) {
        ViewCompat.setTranslationX(holder.itemView,
                -holder.itemView.getRootView().getWidth() * .25f);
        ViewCompat.setAlpha(holder.itemView, 0);
    }

    @Override
    protected void animateAddImpl(final RecyclerView.ViewHolder holder) {
        ViewCompat.animate(holder.itemView)
                .translationX(0)
                .alpha(1)
                .setDuration(getAddDuration())
                .setListener(new DefaultAddVpaListener(holder)).start();
        mAddAnimations.add(holder);
    }
}
