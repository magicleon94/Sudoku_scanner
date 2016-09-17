package com.example.magicleon.sudokuscanner;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

/**
 * Created by magicleon on 11/09/16.
 */
public class MainFragment extends Fragment {
    static final String TAG = "MainFragment";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int RESULT_OK = -1;
    Button button;
    ImageView imageView;
    String photoPath = null;
    Uri photoUri = null;
    static final int REQUEST_CODE = 1234;
    boolean permissionGranted = false;

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
        button = (Button) view.findViewById(R.id.photobutton);
//        imageView = (ImageView) view.findViewById(R.id.imview);
        permissionGranted = isStoragePermissionGranted();
        if (permissionGranted){
            button.setOnClickListener(buttonCallback);
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
            button.setOnClickListener(buttonCallback);
        }
    }
}
