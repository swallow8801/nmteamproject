package com.example.teamprojectapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class facerecognition extends Activity {

    private static final int REQUEST_IMAGE_GET = 1;
    private ImageView imageView;
    private ImageView btnSelectImage;
    private ImageView btnChooseOne;

    private ImageView howtoplay;

    // 선택된 얼굴 정보를 저장할 변수
    private FirebaseVisionFace chosenFace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facerecognition);

        imageView = findViewById(R.id.imageView);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnChooseOne = findViewById(R.id.btnChooseOne);
        howtoplay=findViewById(R.id.howtoplay);

        howtoplay.setVisibility(View.VISIBLE);
        howtoplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                howtoplay.setVisibility(View.INVISIBLE);
                btnSelectImage.setVisibility(View.VISIBLE);
                btnChooseOne.setVisibility(View.VISIBLE);
            }
        });

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이미지 가져오기 Intent 호출
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE_GET);
            }
        });

        // "한 명 고르기" 버튼 클릭 시 처리
        btnChooseOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // "한 명 고르기" 버튼 클릭 시 처리
                if (chosenFace != null) {
                    

                    // 선택된 얼굴 주위에 초록색 사각형 그리기
                    drawRectangle2(chosenFace.getBoundingBox());

                    // 여기에 선택된 얼굴에 대한 추가 로직 작성
                    // 예를 들어 선택된 얼굴의 정보를 사용하여 다른 동작 수행
                    Toast.makeText(getApplicationContext(), "얼굴을 선택합니다", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "얼굴을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            try {
                // 선택된 이미지를 비트맵으로 변환하여 ImageView에 표시
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);

                // ML Kit를 사용하여 얼굴 인식 후 사각형 표시
                detectFaces(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void detectFaces(Bitmap bitmap) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

        FirebaseVisionFaceDetectorOptions options =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                        .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                        .build();

        FirebaseVisionFaceDetector detector = FirebaseVision.getInstance()
                .getVisionFaceDetector(options);

        detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionFace> faces) {
                        // 얼굴을 인식한 후 얼굴 주위에 초록색 사각형 그리기
                        for (FirebaseVisionFace face : faces) {
                            drawRectangle(face.getBoundingBox());
                        }

                        // 여기에 얼굴 정보 저장 로직 추가
                        // 예를 들어 여러 얼굴 중 하나를 선택할 수 있는 방법을 구현
                        if (!faces.isEmpty()) {
                            if (faces.size() == 1) {
                                chosenFace = faces.get(0);
                            } else {
                                // 여러 개의 얼굴 중 하나를 선택 (여기서는 첫 번째 얼굴을 선택하도록 함)
                                chosenFace = faces.get(0);
                            }
                        } else {
                            chosenFace = null; // 얼굴이 없으면 선택된 얼굴을 초기화
                            Toast.makeText(getApplicationContext(), "얼굴이 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // 얼굴 인식 실패 시 처리
                        Toast.makeText(getApplicationContext(), "실패: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }


    // 이미지뷰 위에 그려진 모든 사각형 지우기
    private void clearRectangles() {
        // 이미지뷰에서 원본 비트맵 가져오기
        Bitmap originalBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        // 원본 비트맵으로 Canvas 생성
        Canvas canvas = new Canvas(originalBitmap);

        // 비트맵을 투명으로 채우기
        canvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR);

        // ImageView 업데이트
        imageView.setImageBitmap(originalBitmap);
    }



    // 얼굴 주위에 사각형 그리기 메서드
    private void drawRectangle(android.graphics.Rect boundingBox) {
        // ImageView 위에 Canvas 생성
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);

        // Paint 설정 (여기에서는 초록색)
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        // 사각형 그리기
        canvas.drawRect(boundingBox, paint);

        // ImageView 업데이트
        imageView.setImageBitmap(bitmap);
    }

    private void drawRectangle2(android.graphics.Rect boundingBox) {
        // ImageView 위에 Canvas 생성
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);

        // Paint 설정 (여기에서는 초록색)
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(9);

        // 사각형 그리기
        canvas.drawRect(boundingBox, paint);

        // ImageView 업데이트
        imageView.setImageBitmap(bitmap);
    }


}
