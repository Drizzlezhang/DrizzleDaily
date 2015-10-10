package com.drizzle.drizzledaily.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.drizzle.drizzledaily.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GildeActivity extends AppCompatActivity {
    @Bind(R.id.start_img)
    ImageView startImg;

    private android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    //TODO

                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gilde);
        ButterKnife.bind(this);
        handler.sendEmptyMessageDelayed(1, 1500);
        playAnim();
    }

    private void playAnim() {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(startImg, "alpha", 1f, 0.75f);
        ObjectAnimator scalex = ObjectAnimator.ofFloat(startImg, "scaleX", 1f, 1.1f);
        ObjectAnimator scaley = ObjectAnimator.ofFloat(startImg, "scaleY", 1f, 1.1f);
        AnimatorSet animator = new AnimatorSet();
        animator.play(alpha).with(scalex).with(scaley);
        animator.setDuration(2000).start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                startActivity(new Intent(GildeActivity.this, MainActivity.class));
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.not_move);
            }
        });
    }
}
