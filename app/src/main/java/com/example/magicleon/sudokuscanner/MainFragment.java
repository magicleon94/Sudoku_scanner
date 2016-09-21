package com.example.magicleon.sudokuscanner;

import android.Manifest;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by magicleon on 11/09/16.
 */
public class MainFragment extends Fragment {
    static final String TAG = "MainFragment";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int RESULT_OK = -1;
    Button photoButton;
    Button downloadButton;
    String photoPath = null;
    Uri photoUri = null;
    boolean permissionGranted = false;
    public ProgressDialog mProgressDialog;

    public  MainFragment(){};
    View.OnClickListener buttonCallback = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(takePictureIntent.resolveActivity(getActivity().getPackageManager())!=null) {
                File photoFile = null;
                try{
                    photoFile = createImageFile();

                }catch (IOException ex) {
                    Log.e("Error","Error in creating");

                }

                if (photoFile != null) {
                    photoUri = FileProvider.getUriForFile(getActivity(), "com.example.android.fileprovider", photoFile);

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
            }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.main_fragment_layout,container,false);
        photoButton = (Button) view.findViewById(R.id.photobutton);
        downloadButton = (Button) view.findViewById(R.id.downloadButton);
        permissionGranted = isStoragePermissionGranted();

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog = new ProgressDialog(getActivity());

                mProgressDialog.setMessage("Downloading");
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mProgressDialog.setCancelable(false);

                final DownloadTask task = new DownloadTask(getActivity());
                task.execute(getActivity().getApplicationInfo().dataDir,"ita");
            }
        });
        if (permissionGranted){
            photoButton.setOnClickListener(buttonCallback);
        }
        return view;
    }

    @Override

    public void onActivityResult(int requestCode,int resultCode, Intent data){
        Log.d("AA","Result code: " + resultCode);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
                Intent transformIntent = new Intent(getActivity(),TransformActivity.class);
                transformIntent.putExtra("photoPath",photoPath);
                startActivity(transformIntent);
        }
    }

    private File createImageFile() throws IOException{
        String filename = "my_image";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(filename,".jpg",storageDir);
        photoPath = image.getAbsolutePath();
        return image;

    }
    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {
                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            photoButton.setOnClickListener(buttonCallback);
        }
    }
    public class DownloadTask extends AsyncTask<String,Integer,String> {
        final String[] URL_POSTFIX = {".cube.bigrams",
                ".cube.fold",
                ".cube.lm",
                ".cube.nn",
                ".cube.params",
                ".cube.size",
                ".cube.word-freq",
                ".tesseract_cube.nn",
                ".traineddata"};
        final String URL_PREFIX = "https://raw.githubusercontent.com/tesseract-ocr/tessdata/master/";

        private Context context;
        private PowerManager.WakeLock mWakeLock;


        public DownloadTask(Context context){
            this.context = context;
        }
        @Override
        protected String doInBackground(String... params) {
            //path for tessdata dir
            Log.d("ASYNCTASK","Got path: " + params[0]);

            File tessdataDir = new File(params[0] + "/tessdata");
            if (tessdataDir.exists() && tessdataDir.isDirectory()) {
                Log.d("ASYNCTASK","Everything good");
                return null;
            } else {
                Log.d("ASYNCTASK","Not good");
                InputStream inputStream = null;
                OutputStream outputStream = null;
                HttpURLConnection connection = null;
                for (int i = 0; i < URL_POSTFIX.length; i++) {
                    try {
                        String address = URL_PREFIX + params[1] + URL_POSTFIX[i];
                        Log.d("ASYNCTASK","computed url: " + address);
                        URL url = new URL(address);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.connect();
                        Log.d("ASYNCTASK",connection.getResponseMessage());
                        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                            return "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();
                        }
                        tessdataDir.mkdir();
                        inputStream = connection.getInputStream();
                        outputStream = new FileOutputStream(params[0] + "/tessdata/" + params[1] + URL_POSTFIX[i]);
                        byte[] data = new byte[4096];
                        long total = 0;
                        int count ;
                        int fileLength = connection.getContentLength();
                        while ((count = inputStream.read(data)) != -1) {
                            total += count;
                            if (fileLength > 0) {
                                Log.d("ASYNCTASK","i = " + i + "total = " + total);
                                publishProgress((int) total * 100 / fileLength);
                            }
                            outputStream.write(data, 0, count);
                        }
                    } catch (Exception e) {
                        return e.toString();
                    } finally {
                        try {
                            if (outputStream != null) {
                                outputStream.close();
                            }
                            if (inputStream != null) {
                                inputStream.close();
                            }
                        } catch (IOException ignored) {
                        }
                        if (connection != null) {
                            connection.disconnect();
                        }
                    }
                }
            }
            return null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power photoButton during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }
        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (s==null){
                Toast.makeText(context,"OK",Toast.LENGTH_SHORT);
            }
            else{
                Toast.makeText(context,s,Toast.LENGTH_LONG);
            }
        }
    }

}
