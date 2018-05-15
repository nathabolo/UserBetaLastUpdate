package com.munye;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.munye.dialog.CustomTitleDialog;
import com.munye.user.R;
import com.munye.dialog.CustomCountryCodeDialog;
import com.munye.model.CountryCode;
import com.munye.parse.AsyncTaskCompleteListener;
import com.munye.parse.MultiPartRequester;
import com.munye.utils.AndyUtils;
import com.munye.utils.AppLog;
import com.munye.utils.CompressImage;
import com.munye.utils.Const;
import com.munye.utils.Validation;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

public class RegisterActivity extends ActionBarBaseActivity implements View.OnClickListener, AsyncTaskCompleteListener{

    private EditText edtRegisterUserName, edtRegisterEmail, edtRegisterPassword, edtRegisterConformPassword, edtRegisterConatctNo , edtRegisterAddress;
    private Button btnRegister;
    private String userName, userEmail, userPassword, userContact , userAddress , countryCode;

    private static final int CHOOSE_PHOTO = 1121;
    private static final int TAKE_PHOTO = 1122;
    private ImageView imgUserPhoto;
    private Uri uri = null;
    private String filePath = null;
    private TextView tvCountryCodeType;
    private CustomCountryCodeDialog customCountryCodeDialog;
    private ArrayList<CountryCode> listCountryCode;
    private TextView tvGotoLogin;
    private static String defaultCase = "Default case in Register";
    private CompressImage compressImage;
    private int verification_key_a,verification_key_b;
    private int i = 0;
    private CustomTitleDialog exitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        initRequire();
        checkLocationPermission();
    }

    private void initRequire() {
        initToolBar();
        imgBtnDrawerToggle.setVisibility(View.INVISIBLE);
        setToolBarTitle(getString(R.string.title_register));

        edtRegisterUserName = (EditText) findViewById(R.id.edtRegisterUserName);
        edtRegisterEmail = (EditText) findViewById(R.id.edtRegisterEmail);
        edtRegisterPassword = (EditText) findViewById(R.id.edtRegisterPassword);
        edtRegisterConformPassword = (EditText) findViewById(R.id.edtRegisterConformPassword);
        edtRegisterConatctNo = (EditText) findViewById(R.id.edtRegisterConatctNo);
        edtRegisterAddress = (EditText)findViewById(R.id.edtRegisterAddress);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        imgUserPhoto = (ImageView)findViewById(R.id.imgUserPhoto);
        tvCountryCodeType = (TextView)findViewById(R.id.tvCountryCodeType);
        tvGotoLogin = (TextView)findViewById(R.id.tvGotoLogin);

        imgBtnToolbarBack.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        imgUserPhoto.setOnClickListener(this);
        tvCountryCodeType.setOnClickListener(this);
        tvGotoLogin.setOnClickListener(this);

        listCountryCode = new ArrayList<>();

    }

    /*Handle click events*/
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.imgBtnActionBarBack:
                onBackPressed();
                break;

            case R.id.btnRegister:
                if(filePath == null) {
                }else {
                    if (isValidData()) {

                        if(Integer.parseInt(userContact.charAt(0)+"")>0){
                            userContact = "0"+userContact;

                        }

                        Random r = new Random();
                        verification_key_a = r.nextInt(2000 - 1000) * r.nextInt(2000 - 1000);
                        i=1;
                        registerUserWithKey(verification_key_a+"","");

                    }
                    break;
                }
            case R.id.imgUserPhoto:
                showPictureChooseDialog();
                break;

            case R.id.tvCountryCodeType:
                showCountryCodeDialog();
                break;

            case R.id.tvGotoLogin:
                goToLoginScreen();
                break;


            default:
                AppLog.Log(Const.TAG, defaultCase);
                break;
        }
    }


    public void VerifyDetails(String msg){
        try{
            Button btnSubmit,btnSkip;
            final Dialog buyTokensDialog = new Dialog(RegisterActivity.this);
            buyTokensDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            buyTokensDialog.setContentView(R.layout.verification_dialog);
            buyTokensDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
            btnSubmit = (Button) buyTokensDialog.findViewById(R.id.btnInvoiceOk);
            btnSkip = (Button) buyTokensDialog.findViewById(R.id.btnSkip);
            Button btnCancel = (Button) buyTokensDialog.findViewById(R.id.btnInvoiceCancel);
            final EditText key = (EditText) buyTokensDialog.findViewById(R.id.verificationKey);

            TextView txtAdminPrice = (TextView) buyTokensDialog.findViewById(R.id.msg);
            txtAdminPrice.setText(msg);

            if(i==1){
                btnSkip.setVisibility(View.INVISIBLE);
            }
            else{
                btnSkip.setVisibility(View.VISIBLE);
            }

            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(i == 1){
                        buyTokensDialog.dismiss();
                        // i = 2;
                        AndyUtils.showCustomProgressDialog(RegisterActivity.this, false);
                        Random r = new Random();
                        verification_key_b = r.nextInt(2000 - 1000) + 765764;
                        if(key.getText().toString().equals(verification_key_a+"")) {
                            //  registerUserWithKey(verification_key_b + "", "sms");
                            //registerUserWithKey(verification_key_a+"","");
                            registerUser();
                            buyTokensDialog.dismiss();
                        }else{
                            Toast.makeText(RegisterActivity.this, "Invalid Verification Key\n Please Try Again", Toast.LENGTH_SHORT).show();
                            AndyUtils.removeCustomProgressDialog();
                        }
                    }
                    else if(i == 2){

                        registerUser();
                        buyTokensDialog.dismiss();
                    }
                    else{
                        Toast.makeText(RegisterActivity.this, "Invalid Input\n Please Verify Your Details", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            btnSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    exitDialog = new CustomTitleDialog(RegisterActivity.this, "Skip Tier 2 Verification", "By Skipping Tier 2 (SMS) Verification your account will have a lower rating when viewed",
                            "Skip", "Cancel") {
                        @Override
                        public void positiveResponse() {
                            registerUser();
                        }

                        @Override
                        public void negativeResponse() {
                            exitDialog.dismiss();
                        }
                    };
                    exitDialog.show();

                }
            });
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    buyTokensDialog.dismiss();
                }
            });
            buyTokensDialog.show();
        }catch (Exception e) {
        }
    }

    private void goToLoginScreen(){
        startActivity(new Intent(RegisterActivity.this , SignInActivity.class));
        finish();
    }


    /*It shows the country dialog*/
    private void showCountryCodeDialog() {

        listCountryCode = dataParser.parseCountryCode();
        customCountryCodeDialog = null;
        customCountryCodeDialog = new CustomCountryCodeDialog(this , listCountryCode) {
            @Override
            public void onSelectCountryCode(View view, int position) {
                TextView tvCountryCode = (TextView)view.findViewById(R.id.tvCountryCode);
                String countryCode = tvCountryCode.getText().toString().trim();
                tvCountryCodeType.setText(countryCode);
                customCountryCodeDialog.dismiss();
            }
        };

        customCountryCodeDialog.show();

    }


    /*It checks the validation of input data*/
    private boolean isValidData(){

        userName = edtRegisterUserName.getText().toString().trim();
        userEmail = edtRegisterEmail.getText().toString().trim();
        userPassword = edtRegisterPassword.getText().toString().trim();
        userContact = edtRegisterConatctNo.getText().toString().trim();
        userAddress = edtRegisterAddress.getText().toString().trim();
        countryCode = tvCountryCodeType.getText().toString().trim();

        if(Validation.isEmpty(userName)){
            AndyUtils.showToast(this,getString(R.string.toast_name_not_be_empty));
            return false;
        }

        else if(!Validation.isEmailValid(userEmail)){
            AndyUtils.showToast(this ,getString(R.string.toast_invalid_email));
            return false;
        }
        else if(Validation.isValidPasswordLength(userPassword)){
            AndyUtils.showToast(this , getString(R.string.toast_password_lenght));
            return false;
        }
        else if(!Validation.isPasswordMatch(userPassword , edtRegisterConformPassword.getText().toString().trim())){
            AndyUtils.showToast(this , getString(R.string.toast_password_not_matched));
            return false;
        }
//        else if(!Validation.isContactNoValid(userContact)){
//            AndyUtils.showToast(this , getString(R.string.toast_invalid_contact));
//            return false;
//        }

        else if(TextUtils.isEmpty(userAddress)){
            AndyUtils.showToast(this , getString(R.string.toast_enter_an_address));
            return false;
        }
        else {
            return true;
        }
    }



//
//It shows the dialog to select photo from gallery or camera....
    private void showPictureChooseDialog(){
        AndyUtils.showCustomProgressDialog(this , false);
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle(getResources().getString(
                R.string.txt_dialog_choose_picture));
        String[] pictureDialogItems = {
                getResources().getString(R.string.txt_dialog_gallery),
                getResources().getString(R.string.txt_dialog_Camera)};

        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int type) {
                        switch (type) {
                            case 0:
                                choosePhotoFromGallery();
                                break;

                            case 1:
                                takePhotoFromCamera();
                                break;

                            default:
                                AppLog.Log(Const.TAG, defaultCase);
                                break;
                        }
                    }
                });
        pictureDialog.show();
AndyUtils.removeCustomProgressDialog();
    }

//    //It shows the dialog to select photo from gallery or camera....
//    private void showPictureChooseDialog() {
//
//        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
//        pictureDialog.setTitle(getResources().getString(
//                R.string.txt_dialog_choose_picture));
//        String[] pictureDialogItems = {
//                getResources().getString(R.string.txt_dialog_gallery),
//                getResources().getString(R.string.txt_dialog_Camera)};
//
//        pictureDialog.setItems(pictureDialogItems,
//                new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int type) {
//                        switch (type) {
//                            case 0:
//                                choosePhotoFromGallery();
//                                break;
//
//                            case 1:
//                                takePhotoFromCamera();
//                                break;
//                            default:
//                                AppLog.Log(Const.TAG, defaultCase);
//                                //AndyUtils.removeCustomProgressDialog();
//                                break;
//                        }
//                    }
//                });
//        pictureDialog.show();
//
//    }

    //It choose photo from gallery...
    private void choosePhotoFromGallery(){
        AndyUtils.showCustomProgressDialog(this , false);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, Const.PERMISSION_STORAGE_REQUEST_CODE);
        } else {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, Const.CHOOSE_PHOTO);
        }
        AndyUtils.removeCustomProgressDialog();
    }


    //It take photo from the camera....
//    private void takePhotoFromCamera(){
//try{
//    AndyUtils.showCustomProgressDialog(this , false);
//        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, Const.PERMISSION_CAMERA_REQUEST_CODE);
//        } else {
//            Calendar cal = Calendar.getInstance();
//            File directory = new File(Environment.getExternalStorageDirectory()+"/JimmieJobs");
//            File file = new File(Environment.getExternalStorageDirectory()+"/JimmieJobs",(cal.getTimeInMillis() + ".jpg"));
//
//            if(!directory.exists()){
//                directory.mkdir();
//            }
//
//            if(file.exists()){
//                file.delete();
//            }
//            else{
//                try {
//                    file.createNewFile();
//                } catch (IOException e) {
//                    AndyUtils.generateLog("Exception of file");
//                }
//            }
//            uri = Uri.fromFile(file);
//            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//            startActivityForResult(cameraIntent, Const.TAKE_PHOTO);
//        }
//    AndyUtils.removeCustomProgressDialog();
//    }catch (Exception e) {
//    Toast.makeText(RegisterActivity.this, "Apologies the Camera Service has failed, Please try the Gallery Option", Toast.LENGTH_SHORT).show();
//}
//    }

    public void takePhotoFromCamera() {
        AndyUtils.showCustomProgressDialog(this , false);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, Const.PERMISSION_CAMERA_REQUEST_CODE);
        } else {
            Calendar cal = Calendar.getInstance();
            File file = new File(Environment.getExternalStorageDirectory(),
                    cal.getTimeInMillis() + ".jpg");

            if(!file.exists()){
                file.mkdir();
           }

            if (file.exists()) {
                boolean isDelete = file.delete();
                AppLog.Log(Const.TAG, "Deleted " + isDelete);
            } else {
                try {
                    boolean isCreate = file.createNewFile();
                    AppLog.Log(Const.TAG, "Created" + isCreate);
                } catch (IOException e) {
                    AppLog.Log(Const.TAG, Const.EXCEPTION + e);
                }
            }

            uri = Uri.fromFile(file);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(cameraIntent, Const.TAKE_PHOTO);
        }

        try {
            AndyUtils.removeCustomProgressDialog();
        }catch (Exception e) {
    //Toast.makeText(RegisterActivity.this, "Apologies the Camera Service has failed, Please try the Gallery Option", Toast.LENGTH_SHORT).show();
}
    }

    private void beginCrop(Uri source) {
        Uri outputUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                Calendar.getInstance().getTimeInMillis() + ".jpg"));
        Crop.of(source, outputUri).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {

            filePath = AndyUtils.getRealPathFromURI(Crop.getOutput(result), this);
            imgUserPhoto.setImageURI(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_PHOTO) {
            onRequestCodeChosePhoto(data);
        } else if (requestCode == TAKE_PHOTO) {
            try {
                onRequestTakePhoto();
            } catch (IOException e) {
                //AppLog.Log(Const.TAG, Const.EXCEPTION + e);
            }
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }

    }

    public void onRequestCodeChosePhoto(Intent data) {
        Bitmap photoBitmap;
        int rotationAngle;
        Uri getUri;
        if (data != null) {
            getUri = data.getData();
            filePath = AndyUtils.getRealPathFromURI(getUri, this);

            int mobileWidth = 480;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);
            int outWidth = options.outWidth;
            int ratio = (int) ((((float) outWidth) / mobileWidth) + 0.5f);

            ratio = getRatio(ratio);
            rotationAngle = AndyUtils.setRotaionValue(filePath);


            options.inJustDecodeBounds = false;
            options.inSampleSize = ratio;
            photoBitmap = BitmapFactory.decodeFile(filePath, options);
            intoCrop(photoBitmap, null, rotationAngle, false);


        } else {
            Toast.makeText(
                    this,
                    getResources().getString(
                            R.string.error_select_image),
                    Toast.LENGTH_LONG).show();
        }

    }


//
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case Const.PERMISSION_STORAGE_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        choosePhotoFromGallery();
                }
                break;

            case Const.PERMISSION_CAMERA_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        takePhotoFromCamera();
                }
                break;

            default:
                AndyUtils.generateLog("No permission granted");
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public int getRatio(int ratio) {
        if (ratio == 0) {
            return 1;
        }
        return 0;
    }

    public void intoCrop(Bitmap bitmap, OutputStream outStream, int rotationAngle, boolean isCompressed) {

        if (isCompressed && bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    outStream);

        }
        if (bitmap != null) {
            beginCrop(Uri.parse(AndyUtils.getPathForBitmap(bitmap, this, rotationAngle)));
        }

    }


    public void onRequestTakePhoto() throws IOException {

        int rotationAngle;
        if (uri != null) {
            String imageFilePath = uri.getPath();
            if (imageFilePath != null && imageFilePath.length() > 0) {

                int mobileWidth = 480;
                BitmapFactory.Options options = new BitmapFactory.Options();
                int outWidth = options.outWidth;
                int ratio = (int) ((((float) outWidth) / mobileWidth) + 0.5f);
                ratio = getRatio(ratio);
                rotationAngle = AndyUtils.setRotaionValue(imageFilePath);

                options.inJustDecodeBounds = false;
                options.inSampleSize = ratio;

                Bitmap bmp = BitmapFactory.decodeFile(imageFilePath,
                        options);
                File myFile = new File(imageFilePath);
                FileOutputStream outStream = new FileOutputStream(
                        myFile);
                intoCrop(bmp, outStream, rotationAngle, true);

                outStream.close();
            }

        } else {
            Toast.makeText(
                    this,
                    getResources().getString(
                            R.string.error_select_image),
                    Toast.LENGTH_LONG).show();
        }

    }

    private void setProfileImage(String picturePath){
        try {
            filePath = picturePath;
            imgUserPhoto.setImageURI(Uri.parse(filePath));
        }catch (Exception e){
            Toast.makeText(RegisterActivity.this, "Photo could not be loaded at the moment you can do it later", Toast.LENGTH_SHORT).show();
        }
    }


    private void registerUserWithKey(String key, String sms){

        HashMap<String,String> map = new HashMap<>();

        map.put(Const.URL , Const.ServiceType.REGISTER);
        map.put(Const.Params.PICTURE, filePath);
        map.put(Const.Params.NAME , userName);
        map.put(Const.Params.EMAIL , userEmail);
        map.put(Const.Params.PASS, userPassword);
        map.put(Const.Params.CONTACT_NO , userContact);
        map.put(Const.Params.COUNTRY_CODE , countryCode);
        map.put(Const.Params.ADDRESS , userAddress);
        map.put(Const.Params.DEVICE_TYPE , Const.DEVICE_TYPE_ANDROID );
        map.put(Const.Params.DEVICE_TOKEN , preferenceHelper.getDeviceToken());
        map.put("verification_key", key);
        map.put("sms", sms);

        AndyUtils.showCustomProgressDialog(this , false);
        new MultiPartRequester(this , map , Const.ServiceCode.REGISTER , this);


    }

    private void registerUser(){
        AndyUtils.showCustomProgressDialog(this , false);
        HashMap<String,String> map = new HashMap<>();

        map.put(Const.URL , Const.ServiceType.REGISTER);
        map.put(Const.Params.PICTURE, filePath);
        map.put(Const.Params.NAME , userName);
        map.put(Const.Params.EMAIL , userEmail);
        map.put(Const.Params.PASS, userPassword);
        map.put(Const.Params.CONTACT_NO , userContact);
        map.put(Const.Params.COUNTRY_CODE , countryCode);
        map.put(Const.Params.ADDRESS , userAddress);
        map.put(Const.Params.DEVICE_TYPE , Const.DEVICE_TYPE_ANDROID );
        map.put(Const.Params.DEVICE_TOKEN , preferenceHelper.getDeviceToken());
        map.put("verification_key", "");

        AndyUtils.showCustomProgressDialog(this , false);
        new MultiPartRequester(this , map , Const.ServiceCode.REGISTER , this);


    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {

        AndyUtils.removeCustomProgressDialog();
        switch (serviceCode){
            case Const.ServiceCode.REGISTER:
                if(dataParser.isSuccess(response)){
                    goToLoginScreen();

                }
                else {
                    if( i == 1){
                        VerifyDetails("Thank you for creating a JimmieJobs User Account, to complete the process please enter the verification " +
                                "key that has been sent to "+edtRegisterEmail.getText().toString());
                    }else if(i == 2){
                        VerifyDetails("Please enter the final verification key that has been sent to "+edtRegisterConatctNo.getText().toString()+"\n\n" +
                                "You may also Skip this Tier 2 Verification by Clicking the Skip Button");
                    }
                }
                break;

            default:
                AndyUtils.generateLog("No service");
                break;
        }
    }
}
