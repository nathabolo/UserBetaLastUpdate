package com.munye;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.munye.user.R;
import com.munye.dialog.CustomDialogWithEdittext;
import com.munye.parse.AsyncTaskCompleteListener;
import com.munye.parse.HttpRequester;
import com.munye.utils.AndyUtils;
import com.munye.utils.Const;
import com.munye.utils.Validation;

import java.util.HashMap;

public class SignInActivity extends ActionBarBaseActivity implements View.OnClickListener, AsyncTaskCompleteListener {

    private Button  btnSignIn;
    private EditText edtEmail , edtPassword ;
    private TextView tvForgotPassword;
    private CustomDialogWithEdittext dialogForgotPassword;
    private TextView tvGotoRegister;
    public Login alertDialoge = new Login();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        alertDialoge.showDialog(SignInActivity.this, "JimmieJob");

        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {


            }

            public void onFinish() {
                alertDialoge.dialog.dismiss();
            }
        }.start();

        initToolBar();
        imgBtnDrawerToggle.setVisibility(View.INVISIBLE);
        setToolBarTitle(getString(R.string.title_sign_in));

        edtEmail = (EditText)findViewById(R.id.edtEmail);
        edtPassword = (EditText)findViewById(R.id.edtPassword);
        btnSignIn = (Button)findViewById(R.id.btnSignIn);
        tvForgotPassword = (TextView)findViewById(R.id.tvForgotPassword);
        tvGotoRegister = (TextView)findViewById(R.id.tvGotoRegister);

        imgBtnToolbarBack.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
        tvForgotPassword.setOnClickListener(this);
        tvGotoRegister.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.imgBtnActionBarBack:
                onBackPressed();
                break;

            case R.id.btnSignIn:
                validateInputData();
                break;

            case R.id.tvForgotPassword:
                showForgotPasswordDialog();
                break;

            case R.id.tvGotoRegister:
                goToRegisterPage();
                break;

            default:
                AndyUtils.generateLog("default action");
                break;
        }
    }


    private void goToRegisterPage(){
        startActivity(new Intent(SignInActivity.this , RegisterActivity.class));
        finish();
    }

    private void validateInputData(){
        if(!Validation.isEmailValid(edtEmail.getText().toString().trim())){
            AndyUtils.showToast(this , getString(R.string.toast_invalid_email));
        }
        else if(TextUtils.isEmpty(edtPassword.getText().toString().trim())){
            AndyUtils.showToast(this , getString(R.string.toast_blank_password));
        }
        else{
            sendLoginRequest();
        }
    }


    private void showForgotPasswordDialog(){

        dialogForgotPassword = new CustomDialogWithEdittext(this , getString(R.string.dilaog_title_forgot_password),
                getString(R.string.dilaog_hint_email) , getString(R.string.dialog_button_send) , getString(R.string.dialog_button_cancel)) {
            @Override
            public void yesUpdate(String email) {

                if(TextUtils.isEmpty(email) || !Validation.isEmailValid(email)){
                    AndyUtils.showToast(SignInActivity.this , getString(R.string.toast_invalid_email));
                }
                else{
                    forgotPasswordRequest(email);
                    dialogForgotPassword.dismiss();
                }

            }

            @Override
            public void NoUpdate() {
                dialogForgotPassword.dismiss();
            }
        };

        dialogForgotPassword.show();
    }

    private void forgotPasswordRequest(String email){

        HashMap<String , String> map = new HashMap<>();

        map.put(Const.URL , Const.ServiceType.FORGOT_PASSWORD);
        map.put(Const.Params.USER_TYPE , Const.USER_TYPE);
        map.put(Const.Params.EMAIL , email);

        AndyUtils.showCustomProgressDialog(this , false);
        new HttpRequester(this , map , Const.ServiceCode.FORGOT_PASSWORD , Const.httpRequestType.POST , this);
    }

    private void sendLoginRequest(){
        HashMap<String,String> map = new HashMap<>();

        map.put(Const.URL , Const.ServiceType.LOGIN);
        map.put(Const.Params.LOGIN_BY , Const.MANUAL);
        map.put(Const.Params.APP_VERSION , Const.VERSION);
        map.put(Const.Params.EMAIL , edtEmail.getText().toString().trim());
        map.put(Const.Params.PASS, edtPassword.getText().toString().trim());
        map.put(Const.Params.DEVICE_TYPE , Const.DEVICE_TYPE_ANDROID );
        map.put(Const.Params.DEVICE_TOKEN , preferenceHelper.getDeviceToken());

        AndyUtils.showCustomProgressDialog(this , false);
        new HttpRequester(this , map , Const.ServiceCode.LOGIN ,Const.httpRequestType.POST ,this);
    }


    @Override
    public void onTaskCompleted(String response, int serviceCode) {

        AndyUtils.removeCustomProgressDialog();
        switch (serviceCode){
            case Const.ServiceCode.LOGIN :
                if(dataParser.isSuccess(response)){
                    dataParser.parseUserData(response);
                    Intent intentGotoMapFromLogin = new Intent(SignInActivity.this , LandingActivity.class);
                    intentGotoMapFromLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intentGotoMapFromLogin);
                }
                break;

            case Const.ServiceCode.FORGOT_PASSWORD:
                if(dataParser.isSuccess(response)){
                    AndyUtils.showToast(this , "Check your email for temporary password");
                }
                break;

            default:
                //Default action..
                break;
        }


    }
}
