package com.example.teamprojectapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class liargame extends AppCompatActivity {
    int num1=1;


    String[][] twoDArray = {
            {"음식", "라면"},
            {"음식", "돈까스"},
            {"음식", "피자"},
            {"음식", "떡볶이"},
            {"장소", "대학교"},
            {"장소", "화장실"},
            {"장소", "피시방"},
            {"장소", "백화점"},
            {"직업", "교수"},
            {"직업", "경찰"},
            {"직업", "의사"},
            {"직업", "연예인"},
            {"간식", "탕후루"},
            {"간식", "케이크"},
            {"간식", "와플"},
            {"간식", "초콜릿"},
            {"과일", "사과"},
            {"과일", "귤"},
            {"과일", "복숭아"},
            {"과일", "레몬"},
            {"과자", "빼빼로"},
            {"과자", "포카칩"},
            {"과자", "홈런볼"},
            {"과자", "오레오"},
    };

    private ArrayList<Integer> getRandomNumbers(int start, int end, int count) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        Random random = new Random();
        while (result.size() < count) {
            int randomNumber = random.nextInt(end - start + 1) + start;
            if (!result.contains(randomNumber)) {
                result.add(randomNumber);
            }
        }
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liargame);




        Intent intent = getIntent();
        String string = intent.getStringExtra("주제");
        String topic = intent.getStringExtra("주제어");
        int num2 = intent.getIntExtra("인원수", 0); // 기본값은 0으로 설정
        int num3 = intent.getIntExtra("라이어", 0);



        if (string.isEmpty() || topic.isEmpty()) {
            Random random = new Random();

            // twoDArray에서 랜덤으로 행을 선택
            int randomRow = random.nextInt(twoDArray.length);

            // 선택된 행에서 주제와 주제어를 가져와서 설정
            string = twoDArray[randomRow][0];
            topic = twoDArray[randomRow][1];
        }

        String finalTopic = topic;  // Declare topic as final variable

        ImageView button1 = findViewById(R.id.backbutton_1);
        TextView textViewCenter = findViewById(R.id.textViewCenter);
        TextView textViewTopic = findViewById(R.id.textViewTopic);
        ImageView imageView = findViewById(R.id.imageView);
        TextView touchTextView = findViewById(R.id.touchTextView);
        ImageView bottomButton = findViewById(R.id.bottomButton);


        textViewCenter.setText(num1+" / "+num2);
        textViewTopic.setText("주제 : " + string);

        ArrayList<Integer> randomNumbers = getRandomNumbers(1, num2, num3);

        if(!randomNumbers.contains(num1)){
            touchTextView.setTextColor(Color.parseColor("#000000"));
            touchTextView.setText(topic);
        }
        else{
            touchTextView.setTextColor(Color.parseColor("#FF0000"));
            touchTextView.setText("라이어");
        }


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 터치 시 이미지뷰를 invisible로 변경, 텍스트뷰를 visible로 변경
                        imageView.setVisibility(View.INVISIBLE);
                        touchTextView.setVisibility(View.VISIBLE);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // 터치 시 이미지뷰를 invisible로 변경, 텍스트뷰를 visible로 변경
                        imageView.setVisibility(View.VISIBLE);
                        touchTextView.setVisibility(View.INVISIBLE);
                        return true;
                }
                return false;
            }
        });

        bottomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num1++;
                textViewCenter.setText(num1+" / "+num2);
                if(!randomNumbers.contains(num1)){
                    touchTextView.setTextColor(Color.parseColor("#000000"));
                    touchTextView.setText(finalTopic);
                }
                else{
                    touchTextView.setTextColor(Color.parseColor("#FF0000"));
                    touchTextView.setText("라이어");
                }
                if(num1==num2){
                    bottomButton.setVisibility(View.INVISIBLE);
                }
            }
        });



    }
}