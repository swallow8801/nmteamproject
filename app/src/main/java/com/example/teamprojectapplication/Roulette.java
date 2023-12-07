package com.example.teamprojectapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.bluehomestudio.luckywheel.LuckyWheel;
import com.bluehomestudio.luckywheel.OnLuckyWheelReachTheTarget;
import com.bluehomestudio.luckywheel.WheelItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Roulette extends AppCompatActivity {

    Animation translateLeft, translateRight;//애니메이션 효과

    private ConstraintLayout slideview, mainview, howtoplay9;
    boolean isPageOpen = false;

    private ImageView btnAdd, btnMinus, changebtn1, backbtn, mainbackbutton;

    private TextView tvCount, resultView;

    private ScrollView rootView;
    private int count = 0;

    List<WheelItem> wheelItems;
    LuckyWheel luckyWheel;
    String point;

    EditText ets[] = new EditText[6];
    int etss[] = {R.id.et1, R.id.et2, R.id.et3, R.id.et4, R.id.et5, R.id.et6};

    private class SlidinAnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (isPageOpen) {
                slideview.setVisibility(View.INVISIBLE);
                mainview.setVisibility(View.VISIBLE);

                isPageOpen = false;
            } else {
                isPageOpen = true;
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roulette);

        mainview = findViewById(R.id.mainview);
        slideview = findViewById(R.id.slide_view);
        tvCount = findViewById(R.id.tv_count);
        tvCount.setText(count + "");
        btnAdd = findViewById(R.id.btn_add);
        btnMinus = findViewById(R.id.btn_minus);
        changebtn1 = findViewById(R.id.btn_change1);
        backbtn = findViewById(R.id.backbtn);
        luckyWheel = findViewById(R.id.luck_wheel);

        translateLeft = AnimationUtils.loadAnimation(this, R.anim.translate_left);
        translateRight = AnimationUtils.loadAnimation(this, R.anim.translate_right);

        SlidinAnimationListener listener = new SlidinAnimationListener();
        translateRight.setAnimationListener(listener);
        translateLeft.setAnimationListener(listener);

        count = 2;
        tvCount.setText(String.valueOf(count));

        final View activityRootView = findViewById(R.id.rootview);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            int previousHeightDiff = -1;

            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                activityRootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = activityRootView.getRootView().getHeight();
                int heightDiff = screenHeight - r.bottom;

                if (previousHeightDiff != heightDiff) {
                    previousHeightDiff = heightDiff;

                    if (heightDiff < screenHeight * 0.15) {
                        activityRootView.scrollTo(0, 0);
                    } else {
                        // Keyboard is visible
                    }
                }
            }
        });

        for (int i = 0; i < 6; i++) {
            final int idx = i;
            ets[idx] = findViewById(etss[idx]);
        }

        howtoplay9 = findViewById(R.id.howtoplay9);
        mainbackbutton = findViewById(R.id.mainbackbtn);

        mainbackbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        howtoplay9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                howtoplay9.setVisibility(View.INVISIBLE);
            }
        });

        generateWheelItems();

        luckyWheel.setLuckyWheelReachTheTarget(new OnLuckyWheelReachTheTarget() {
            @Override
            public void onReachTarget() {
                int wheelIndex = Integer.parseInt(point) - 1;

                if (wheelIndex >= 0 && wheelIndex < 6) {
                    String textToShow = ets[wheelIndex].getText().toString();
                    resultView = findViewById(R.id.resultview);
                    resultView.setText(textToShow);
                }
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count < 6) {
                    count++;
                    tvCount.setText(count + "");
                    updateLinearLayoutVisibility();
                }
            }
        });

        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count > 2) {
                    count--;
                    tvCount.setText(count + "");
                    updateLinearLayoutVisibility();
                }
            }
        });

        backbtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                slideview.startAnimation(translateRight);
                return false;
            }
        });

        ImageView start = findViewById(R.id.spin_btn);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                wheelItems = new ArrayList<>();

                Drawable d = getResources().getDrawable(R.drawable.ic_money, null);

                String color[] = {"#F44336", "#E91E63", "#9C27B0", "#3F51B5", "#1E88E5", "#009688"};

                Bitmap bitmap = drawableToBitmap(d);
                int repeatCount = Integer.parseInt(tvCount.getText().toString());

                for (int i = 0; i < repeatCount; i++) {
                    String editTextValue = String.valueOf(i + 1);
                    wheelItems.add(new WheelItem(Color.parseColor(color[i]), bitmap, editTextValue));
                }

                luckyWheel.addWheelItems(wheelItems);

                Random random = new Random();
                int maxRange = count;
                point = String.valueOf(random.nextInt(maxRange) + 1);
                luckyWheel.rotateWheelTo(Integer.parseInt(point));
            }
        });

        changebtn1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                slideview.setVisibility(View.VISIBLE);
                slideview.startAnimation(translateLeft);
                return false;
            }
        });

        updateLinearLayoutVisibility(); // 초기화
    }

    private void generateWheelItems() {
        wheelItems = new ArrayList<>();
        Drawable d = getResources().getDrawable(R.drawable.ic_money, null);
        String color[] = {"#F44336", "#E91E63", "#9C27B0", "#3F51B5", "#1E88E5", "#009688"};
        Bitmap bitmap = drawableToBitmap(d);

        wheelItems.add(new WheelItem(Color.parseColor("#F44336"), bitmap, getResources().getString(R.string.peace1)));
        wheelItems.add(new WheelItem(Color.parseColor("#E91E63"), bitmap, getResources().getString(R.string.peace2)));
        wheelItems.add(new WheelItem(Color.parseColor("#9C27B0"), bitmap, getResources().getString(R.string.peace3)));
        wheelItems.add(new WheelItem(Color.parseColor("#3F51B5"), bitmap, getResources().getString(R.string.peace4)));
        wheelItems.add(new WheelItem(Color.parseColor("#1E88E5"), bitmap, getResources().getString(R.string.peace5)));
        wheelItems.add(new WheelItem(Color.parseColor("#009688"), bitmap, getResources().getString(R.string.peace6)));

        luckyWheel.addWheelItems(wheelItems);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private void updateLinearLayoutVisibility() {
        LinearLayout[] linearLayouts = new LinearLayout[]{
                findViewById(R.id.number1),
                findViewById(R.id.number2),
                findViewById(R.id.number3),
                findViewById(R.id.number4),
                findViewById(R.id.number5),
                findViewById(R.id.number6)
        };

        for (int i = 0; i < 6; i++) {
            if (i < count ) {
                linearLayouts[i].setVisibility(View.VISIBLE);
            } else {
                linearLayouts[i].setVisibility(View.INVISIBLE);
            }
        }
    }
}