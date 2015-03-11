package fr.epf.pca.selfiepeufien;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import java.util.List;

public class SelfieActivity extends Activity implements SurfaceHolder.Callback {

    private Camera mCamera;
    private SurfaceView surface;
    private SurfaceHolder holder;
    private TextView texte;
    private boolean isProcessing = false;
    private Face[] visages;

    private FaceDetectionListener faceDetectionListener = new FaceDetectionListener() {
        @Override
        public void onFaceDetection(Face[] faces, Camera camera) {
            if (isProcessing)
                return;

            visages = faces;

            switch (faces.length)
            {
                case 0:
                    texte.setText("Aucun visage détecté");
                    break;
                default:
                    texte.setText("Lancer la reconnaissance");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selfie);

        surface = (SurfaceView)findViewById(R.id.surface_view);
        holder = surface.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        texte = (TextView)findViewById(R.id.textView);
        texte.setOnClickListener(lancerReconnaissance);

        mCamera = Camera.open(1);
    }

    private OnClickListener lancerReconnaissance = new View.OnClickListener() {
        @Override
        public void onClick(View bouton) {
            if (visages.length == 0)
                return;

            isProcessing = true;
            mCamera.stopPreview();
            mCamera.stopFaceDetection();
            texte.setText("Reconnaissance en cours...");

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    texte.setText("Vous êtes Lancelot !");
                }
            }, 2000);
        }
    };

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Camera.Parameters params = mCamera.getParameters();
        List<Camera.Size> sizes = params.getSupportedPreviewSizes();
        Camera.Size selected = sizes.get(0);
        params.setPreviewFrameRate(20);
        params.setPreviewSize(selected.width,selected.height);
        mCamera.setParameters(params);

        mCamera.setDisplayOrientation(90);
        mCamera.startPreview();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(surface.getHolder());
            mCamera.setFaceDetectionListener(faceDetectionListener);
            mCamera.startFaceDetection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {



        
    }

    @Override
    public void onPause() {
        super.onPause();
        mCamera.stopPreview();
    }

    @Override
    public void onResume() {
        super.onResume();
        mCamera.startPreview();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCamera.release();
    }
}