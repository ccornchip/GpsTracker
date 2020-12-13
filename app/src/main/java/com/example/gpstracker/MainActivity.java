package com.example.gpstracker;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {

    private Button mButton;
    private Intent gpsForegroundServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gpsForegroundServiceIntent = new Intent(this, GpsForegroundService.class);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.textView);
        textView.setText(getIpAddress());

        mButton = findViewById(R.id.button);
        mButton.setOnClickListener(v -> {
            textView.setText(getIpAddress());

            if (mButton.getText().equals(getResources().getString(R.string.start_text))) {
                askPermissionsAndStartService();
            } else {
                stopService(gpsForegroundServiceIntent);
            }
        });

        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        findViewById(R.id.button2).setOnClickListener(v -> {
            requestQueue.add(new StringRequest(Request.Method.POST,
                    "http://xbacams.cluster028.hosting.ovh.net/api/track-api.php",
                    (String response) -> {
                        System.out.println(response);
                    }, (VolleyError error) -> {
                         System.out.println(error);
                    }){
                @Override
                public byte[] getBody() throws AuthFailureError {
                    return "data=kiki".getBytes();
                }
            });

//            requestQueue.add(new Request<String>(Request.Method.POST,
//                    "http://xbacams.cluster028.hosting.ovh.net/api/track-api.php",
//                    error -> {
//                        System.out.println(error);
//                    }) {
//
//                @Override
//                public byte[] getBody() throws AuthFailureError {
//                    return "data=fromappli".getBytes();
//                }
//
//                @Override
//                protected Response<String> parseNetworkResponse(NetworkResponse response) {
//                    return new Response<String>();
//                }
//
//                @Override
//                protected void deliverResponse(String response) {
//                }
//            });
        });
        bindService(gpsForegroundServiceIntent, serviceConnection, BIND_ABOVE_CLIENT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        askPermissionsAndStartService();
    }

    private void askPermissionsAndStartService() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            startForegroundService(gpsForegroundServiceIntent);
            bindService(gpsForegroundServiceIntent, serviceConnection, BIND_ABOVE_CLIENT);
        }
    }

    public static String getIpAddress() {
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();
                    if (inetAddress.isSiteLocalAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            ((GpsForegroundService.GpsBinder) binder).setIsRunningListener(isRunning ->
                    mButton.setText(isRunning ? R.string.stop_text : R.string.start_text)
            );
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
}