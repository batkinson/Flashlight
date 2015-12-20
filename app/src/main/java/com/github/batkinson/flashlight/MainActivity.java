package com.github.batkinson.flashlight;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

    boolean flashSupported;

    Flashlight flashlight;

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.clickableText);

        flashSupported = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (flashSupported) {
            StateHandler handler = new StateHandler();
            flashlight = new Flashlight();
            flashlight.setListener(handler);
            textView.setOnClickListener(handler);
            flashlight.create();
        }
    }

    class StateHandler implements View.OnClickListener, Flashlight.Listener {

        @Override
        public void statusChanged(boolean on) {
            textView.setText(on ? R.string.turn_off : R.string.turn_on);
        }

        @Override
        public void onClick(View v) {
            CharSequence text = textView.getText();
            if (getText(R.string.turn_on).equals(text)) {
                textView.setText(R.string.turning_on);
                flashlight.on();
            } else if (getText(R.string.turn_off).equals(text)) {
                textView.setText(R.string.turning_off);
                flashlight.off();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        textView = null;
        flashlight.destroy();
        flashlight = null;
    }

    static class Flashlight {

        Camera c;

        Listener listener;

        interface Listener {
            void statusChanged(boolean on);
        }

        Flashlight() {
        }

        public void setListener(Listener listener) {
            this.listener = listener;
        }

        void create() {
            c = Camera.open();
            if (c != null) {
                c.release();
                fireStateChanged(false);
            }
        }

        void on() {
            c = Camera.open();
            Camera.Parameters params = c.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            c.setParameters(params);
            if (c != null) {
                c.startPreview();
                fireStateChanged(true);
            }
        }

        void off() {
            if (c != null) {
                c.stopPreview();
                c.release();
                fireStateChanged(false);
            }
        }

        void fireStateChanged(boolean on) {
            if (listener != null) {
                listener.statusChanged(on);
            }
        }

        void destroy() {
            if (c != null) {
                c.release();
            }
        }
    }
}
