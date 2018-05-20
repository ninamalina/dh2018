package com.jjoe64.motiondetection;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.SurfaceView;
import android.widget.TextView;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;
import com.jjoe64.motiondetection.motiondetection.MotionDetector;
import com.jjoe64.motiondetection.motiondetection.MotionDetectorCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.System.out;

public class MainActivity extends AppCompatActivity {
    private TextView txtStatus;
    private MotionDetector motionDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtStatus = (TextView) findViewById(R.id.txtStatus);

        motionDetector = new MotionDetector(this, (SurfaceView) findViewById(R.id.surfaceView));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            motionDetector.setMotionDetectorCallback(new MotionDetectorCallback() {
                @TargetApi(Build.VERSION_CODES.KITKAT)
                @Override
                public void onMotionDetected(int[] imgs, int width, int height, byte[] bytes) throws IOException {


                    txtStatus.setText("Motion detected");
                    List<AnnotateImageRequest> requests = new ArrayList<>();

                    ByteString imgBytes = ByteString.copyFrom(bytes);

                    Image img = Image.newBuilder().setContent(imgBytes).build();
                    Feature feat = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
                    AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
                    requests.add(request);

                    try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
                        BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
                        List<AnnotateImageResponse> responses = response.getResponsesList();

                        for (AnnotateImageResponse res : responses) {
                            if (res.hasError()) {
                                out.printf("Error: %s\n", res.getError().getMessage());
                                return;
                            }

                            // For full list of available annotations, see http://g.co/cloud/vision/docs
                            for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
                                //annotation.getAllFields().forEach((k, v) -> out.printf("%s : %s\n", k, v.toString()));

                            }
                        }
                    }

                    FileOutputStream outStream = null;
                    File sdCard = Environment.getExternalStorageDirectory();
                        File dir = new File (sdCard.getAbsolutePath() + "/camtest");
                        dir.mkdirs();

                        String fileName = String.format("%d.jpg", System.currentTimeMillis());
                        File outFile = new File(dir, fileName);

                        outStream = new FileOutputStream(outFile);
                        outStream.write(bytes);
                        outStream.flush();
                        outStream.close();





    //                Bitmap photo = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
    //                // vector is your int[] of ARGB
    //                photo.copyPixelsFromBuffer(IntBuffer.wrap(img));
    //
    //                ByteArrayOutputStream baos = new ByteArrayOutputStream();
    //                photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
    //                byte[] imageBytes = baos.toByteArray();
                    String encodedImage = Base64.encodeToString(bytes, Base64.DEFAULT);
                    // Log.d(TAG, encodedImage);
                    sendRequest(encodedImage, width, height);
                }

                @Override
                public void onTooDark() {
                    txtStatus.setText("Too dark here");
                }
            });
        }

        ////// Config Options
        //motionDetector.setCheckInterval(500);
        //motionDetector.setLeniency(20);
        //motionDetector.setMinLuma(1000);
    }

    private void sendRequest(String encodedImage, int width, int height) {
        CallAPI ca = new CallAPI();
        ca.execute("http://ec2-34-240-68-66.eu-west-1.compute.amazonaws.com/check/organic", String.valueOf(width), String.valueOf(height), encodedImage);
    }

    @Override
    protected void onResume() {
        super.onResume();
        motionDetector.onResume();

        if (motionDetector.checkCameraHardware()) {
            txtStatus.setText("Camera found");
        } else {
            txtStatus.setText("No camera available");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        motionDetector.onPause();
    }



}
