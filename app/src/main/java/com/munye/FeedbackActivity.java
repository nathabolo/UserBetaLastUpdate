package com.munye;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.munye.dialog.CustomTitleDialog;
import com.munye.dialog.ImageDialog;
import com.munye.parse.MultiPartRequester;
import com.munye.user.R;
import com.munye.model.ActiveJob;
import com.munye.parse.AsyncTaskCompleteListener;
import com.munye.parse.HttpRequester;
import com.munye.utils.AndyUtils;
import com.munye.utils.Const;
import com.munye.utils.Formatter;
import com.munye.utils.RSACipher;
import com.stripe.android.model.Card;

import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Pattern;

public class FeedbackActivity extends ActionBarBaseActivity implements View.OnClickListener, AsyncTaskCompleteListener {

    private Bundle feedbackData;
    private Button btnSubmitFeedback;
    private TextView tvFeedbackJobTitle,tvAmountActiveJobActivity ,tvFeedbackJobType , tvFeedbackJobDate , tvFeedbackJobDescription ,tvFeedbackJobAmount , tvFeedbackProviderName;
    private ImageView imgFeedbackJobIcon , imgFeedbackProviderImage;
    private EditText edtFeedback;
    private RatingBar ratingFeedback;
    private float rating;
    private String comment;
    private CustomTitleDialog exitDialog;
    private Dialog dialogAddCard;
    private ImageView imgViewCloseDialog;
    private EditText edtCreditCardNo , edtCardMonth , edtCardYear , edtCardCvv;
    private Button btnSubmitCard;
    private String cardType;
    private TextView tvInvoiceJobAmount;
    private TextView tvInvoiceAdminPrice;
    private TextView tvInvoiceGrandTotal;
    private TextView tvAmountPayByCard;
    private TextView tvAmountPayByCash;
    private Button btnInvoiceOk, buyTokens, btnInvoiceCancel;
    private ImageDialog imageDialog;
    private double jobAmount , adminPrice , total ;
    private ActiveJob activeJob;
    private int position;
    private String amnt , requestState;
    private Button btnFeedbackAndCancel;
    private Dialog dialogInvoice;
    protected String filePath = null;
    private static final String AMERICAN_EXPRESS = "AMERICAN EXPRESS";
    private static final String DISCOVER = "Discover";
    private static final String JCB = "JCB";
    private static final String DINERS_CLUB = "Diners Club";
    private static final String VISA = "VISA";
    private static final String MASTERCARD = "MASTERCARD";
    private static final String UNKNOWN = "Unknown";
    private static final int ACTION_GET_CARD = 1;
    private static final int ACTION_ADD_CARD = 2;
    private static final int ACTION_DELETE_CARD = 3;
    private static final Pattern CODE_PATTERN = Pattern.compile("([0-9]{0,4})|([0-9]{4}-)+|([0-9]{4}-[0-9]{0,4})+");
    private String ctPayToken;
    private String ctCVV;
    private RSACipher rsaCipher;
    private String expiryDate;
    int TAKE_PHOTO_CODE = 0;
    public static int count = 0;
    private String file;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        initRequire();

        feedbackData = getIntent().getExtras();
        activeJob = feedbackData.getParcelable("FEEDBACK");
        position = feedbackData.getInt("position");
        amnt = feedbackData.getString("amount");

        tvFeedbackJobTitle.setText(activeJob.getTitle());
        tvFeedbackJobTitle.setTextColor(Color.parseColor(AndyUtils.getColorCode(position % 6 , this)));
        tvFeedbackJobDate.setText(Formatter.getDateInFormate(activeJob.getDate()));
        tvFeedbackJobDescription.setText(activeJob.getDescription());
        tvFeedbackJobAmount.setText(AndyUtils.getSymbolFromHex(activeJob.getCurrency())+activeJob.getAmount());
        tvFeedbackProviderName.setText(activeJob.getProviderName());

        switch (activeJob.getRequestType()){
            case Const.JobRequestType.TYPE_REPAIR_MAINTENANCE:
                tvFeedbackJobType.setText(getString(R.string.txt_repair_maintenance));
                break;

            case Const.JobRequestType.TYPE_INSTALLATION:
                tvFeedbackJobType.setText(getString(R.string.txt_installation));
                break;

            default:
                AndyUtils.generateLog("No srecice Type");
                break;
        }


        Glide.with(this).load(activeJob.getJobTypeIcon()).placeholder(R.mipmap.ic_launcher).skipMemoryCache(true).into(imgFeedbackJobIcon);
        imgFeedbackJobIcon.setColorFilter(Color.parseColor(AndyUtils.getColorCode(position % 6 , this)));
        Glide.with(this)
                .load(activeJob.getProviderPicture())
                .asBitmap()
                .placeholder(getResources().getDrawable(R.drawable.default_icon))
                .into(new BitmapImageViewTarget(imgFeedbackProviderImage){
                    @Override
                    protected void setResource(Bitmap resource) {
                        super.setResource(resource);
                        RoundedBitmapDrawable cirimage = RoundedBitmapDrawableFactory.create(getResources(),resource);
                        cirimage.setCircular(true);
                        imgFeedbackProviderImage.setImageDrawable(cirimage);
                    }
                });

        btnSubmitFeedback.setOnClickListener(this);

    }

    private void initRequire() {
        initToolBar();
        setToolBarTitle(getString(R.string.title_feedback));
        imgBtnDrawerToggle.setVisibility(View.INVISIBLE);

        btnSubmitFeedback = (Button)findViewById(R.id.btnSubmitFeedback);
        tvFeedbackJobTitle = (TextView)findViewById(R.id.tvFeedbackJobTitle);
        tvFeedbackJobType = (TextView)findViewById(R.id.tvFeedbackJobType);
        tvFeedbackJobDate = (TextView)findViewById(R.id.tvFeedbackJobDate);
        tvFeedbackJobDescription = (TextView)findViewById(R.id.tvFeedbackJobDescription);
        tvFeedbackJobAmount = (TextView)findViewById(R.id.tvFeedbackJobAmount);
        tvFeedbackProviderName = (TextView)findViewById(R.id.tvFeedbackProviderName);

        imgFeedbackJobIcon = (ImageView)findViewById(R.id.imgFeedbackJobIcon);
        imgFeedbackProviderImage = (ImageView)findViewById(R.id.imgFeedbackProviderImage);

        edtFeedback = (EditText)findViewById(R.id.edtFeedback);

        ratingFeedback = (RatingBar)findViewById(R.id.ratingFeedback);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnSubmitFeedback){
            submitFeedback();
        }
    }

    private void checkValidData() {

        comment = edtFeedback.getText().toString().trim();
        rating = ratingFeedback.getRating();
        if(ratingFeedback.getRating() <=0){
            AndyUtils.showToast(this , "Please Provide Rating");
        }
        else if(TextUtils.isEmpty(comment)){
            AndyUtils.showToast(this, "Please Provide Feedback");
        }
        else {
            submitFeedback();

        }
    }

    private void submitFeedback() {

        HashMap<String , String> map = new HashMap<>();

        map.put(Const.URL , Const.ServiceType.FEEDBACK);
        map.put(Const.Params.ID , preferenceHelper.getId());
        map.put(Const.Params.TOKEN , preferenceHelper.getToken());
        map.put(Const.Params.REQUEST_ID , activeJob.getActiveJobId());
        map.put(Const.Params.RATING , String.valueOf(rating));
        map.put(Const.Params.COMMENT , comment);

        AndyUtils.showCustomProgressDialog(this , false);
        new HttpRequester(this , map , Const.ServiceCode.FEEDBACK , Const.httpRequestType.POST , this);




    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {

        AndyUtils.removeCustomProgressDialog();
        if(dataParser.isSuccess(response)){
            Intent intentFeedbackBack = new Intent();
            intentFeedbackBack.putExtra("POSITION" , position);
            setResult(Const.ACTION_FEEDBACK , intentFeedbackBack);
            //onBackPressed();
            exitDialog = new CustomTitleDialog(FeedbackActivity.this , "JimmieJobs Feedback" ,  "Thank you for the Feedback, Now you will proceed to your jobs and complete the payment by entering your payment details" ,
                    "OK", "No") {

                public void positiveResponse() {
                    exitDialog.dismiss();
                    openPaymentDialog();
                }

                public void negativeResponse() {
                    exitDialog.dismiss();
                }
            };
            exitDialog.show();
        }

    }
    private void openPaymentDialog(){
        if(dialogAddCard != null && dialogAddCard.isShowing()){
            return;
        }
        dialogAddCard = new Dialog(this);
        dialogAddCard.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAddCard.setContentView(R.layout.dialog_add_card);
        WindowManager.LayoutParams params = dialogAddCard.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialogAddCard.getWindow().setAttributes(params);
        dialogAddCard.setCancelable(false);

        imgViewCloseDialog = (ImageView)dialogAddCard.findViewById(R.id.imgViewCloseDialog);
        edtCreditCardNo = (EditText) dialogAddCard.findViewById(R.id.edtCreditCardNo);
        edtCardMonth = (EditText) dialogAddCard.findViewById(R.id.edtCardMonth);
        edtCardYear = (EditText) dialogAddCard.findViewById(R.id.edtCardYear);
        edtCardCvv = (EditText) dialogAddCard.findViewById(R.id.edtCardCvv);
        btnSubmitCard = (Button) dialogAddCard.findViewById(R.id.btnSubmitCard);
        TextView t = (TextView) dialogAddCard.findViewById(R.id.txtDisclaimer);
        t.setText("You are about to pay "+amnt+" for the job done\n\nNOTE this amount will go the JimmeJobs Account and you will be able to claim it anytime, we also do not charge Admin Fees");
        setTextWatcher();

        btnSubmitCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                String x = year+"".replace("20","");
                int y = Integer.parseInt(x);
                if(cardType.equals("Unknown")){
                    Toast.makeText(FeedbackActivity.this, "Invalid Card", Toast.LENGTH_SHORT).show();
                }
                if(Integer.parseInt(edtCardMonth.getText().toString())>12){
                    Toast.makeText(FeedbackActivity.this, "Invalid Expiry Month", Toast.LENGTH_SHORT).show();
                }
                if(Integer.parseInt(edtCardYear.getText().toString())+2000<y){

                    Toast.makeText(FeedbackActivity.this, "Invalid Expiry Year", Toast.LENGTH_SHORT).show();
                }
                else {
                    dialogAddCard.dismiss();
                    Intent intentShare = new Intent(FeedbackActivity.this, UpdateProfileActivity.class);
                    intentShare.putExtra("share", "pay");
                    intentShare.putExtra("amnt", amnt);
                    intentShare.putExtra("id", activeJob.getActiveJobId());
                    intentShare.putExtra("c_no", edtCreditCardNo.getText().toString().replace("-", ""));
                    intentShare.putExtra("cvv", edtCardCvv.getText().toString());
                    intentShare.putExtra("exp_yr", edtCardYear.getText().toString());
                    intentShare.putExtra("exp_m", edtCardMonth.getText().toString());
                    intentShare.putExtra("c_typ", cardType);
                    startActivity(intentShare);
                }
            }
        });
        imgViewCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAddCard.dismiss();
            }
        });
        dialogAddCard.show();
    }
    private void setTextWatcher(){
        edtCreditCardNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do with text..
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(TextUtils.isEmpty(s.toString())){
                    edtCreditCardNo.setCompoundDrawablesWithIntrinsicBounds(null , null , null , null);
                }

                cardType = getCardType(s.toString());
                setCardTypeDrawable(cardType);

                if(edtCreditCardNo.getText().toString().length() == 19){
                    edtCardMonth.requestFocus();
                }

            }


            @Override
            public void afterTextChanged(Editable s) {

                if(s.length() > 0 && !CODE_PATTERN.matcher(s).matches()){
                    String input = s.toString();
                    String numberOnly = keepNumberOnly(input);
                    String code = formatCardNo(numberOnly);
                    edtCreditCardNo.removeTextChangedListener(this);
                    edtCreditCardNo.setText(code);
                    edtCreditCardNo.setSelection(code.length());
                    edtCreditCardNo.addTextChangedListener(this);
                }

            }


            private String keepNumberOnly(CharSequence s){
                return s.toString().replaceAll("[^0-9]", "");
            }

            private String formatCardNo(CharSequence s){
                int groupDigits = 0;
                String cardNo = "";
                int sSize = s.length();
                for (int i = 0; i < sSize; ++i) {
                    cardNo += s.charAt(i);
                    ++groupDigits;
                    if (groupDigits == 4) {
                        cardNo += "-";
                        groupDigits = 0;
                    }
                }
                return cardNo;
            }
        });


        edtCardYear.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do with text
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edtCardYear.getText().toString().length() == 2){
                    edtCardCvv.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Do with text
            }
        });


        edtCardMonth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do with text..
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(edtCardMonth.getText().toString().length() == 2){
                    edtCardYear.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                //Do with text..
            }
        });
    }


    private void setCardTypeDrawable(String cradTypes){

        if(cradTypes.equals(VISA)){
            edtCreditCardNo.setCompoundDrawablesWithIntrinsicBounds(
                    ResourcesCompat.getDrawable(getResources(), R.drawable
                            .creditcard_visa, null)
                    , null,
                    null, null);
        }
        else if(cradTypes.equals(MASTERCARD)){
            edtCreditCardNo.setCompoundDrawablesWithIntrinsicBounds(
                    ResourcesCompat.getDrawable(getResources(), R.drawable
                            .creditcard_mastercard, null)
                    , null,
                    null, null);
        }
        else if(cradTypes.equals(DISCOVER)){
            edtCreditCardNo.setCompoundDrawablesWithIntrinsicBounds(
                    ResourcesCompat.getDrawable(getResources(), R.drawable
                            .creditcard_discover, null)
                    , null,
                    null, null);
        }
        else if (cradTypes.equals(AMERICAN_EXPRESS)){
            edtCreditCardNo.setCompoundDrawablesWithIntrinsicBounds(
                    ResourcesCompat.getDrawable(getResources(), R.drawable
                            .creditcard_amex, null)
                    , null,
                    null, null);
        }
        else {
            edtCreditCardNo.setCompoundDrawablesWithIntrinsicBounds(
                    null, null, null, null);
        }
    }


    private String getCardType(String prifix){
        if(TextUtils.isEmpty(prifix)){
            return UNKNOWN;
        }
        else {
            if (AndyUtils.hasAnyPrifix(prifix, Card.PREFIXES_VISA)) {
                return VISA;
            } else if (AndyUtils.hasAnyPrifix(prifix, Card.PREFIXES_AMERICAN_EXPRESS)) {
                return AMERICAN_EXPRESS;
            } else if (AndyUtils.hasAnyPrifix(prifix, Card.PREFIXES_DISCOVER)) {
                return DISCOVER;
            } else if (AndyUtils.hasAnyPrifix(prifix, Card.PREFIXES_DINERS_CLUB)) {
                return DINERS_CLUB;
            } else if (AndyUtils.hasAnyPrifix(prifix, Card.PREFIXES_MASTERCARD)) {
                return MASTERCARD;
            } else if (AndyUtils.hasAnyPrifix(prifix, Card.PREFIXES_JCB)){
                return JCB;
            } else {
                return UNKNOWN;
            }
        }
    }


    public void updateProfile() {

        HashMap<String , String> map = new HashMap<>();
        map.put(Const.URL , Const.ServiceType.UPDATE_PROFILE);
        map.put(Const.Params.ID , preferenceHelper.getId());
        map.put(Const.Params.TOKEN , preferenceHelper.getToken());
        map.put(Const.Params.NAME , preferenceHelper.getUserName());
        map.put(Const.Params.CONTACT_NO , preferenceHelper.getContactNo());
        map.put(Const.Params.COUNTRY_CODE , preferenceHelper.getCountryCode());
        map.put(Const.Params.EMAIL , preferenceHelper.getEmail());
        map.put(Const.Params.ADDRESS , preferenceHelper.getAddress());
        if(!TextUtils.isEmpty(filePath)){
            map.put(Const.Params.PICTURE , filePath);
        }
        // if(!TextUtils.isEmpty(userUpdatePassword)){
        map.put(Const.Params.NEW_PASS, "");
        map.put(Const.Params.OLD_PASS, "");
        // }

        AndyUtils.showCustomProgressDialog(this , true);
        new MultiPartRequester(this , map , Const.ServiceCode.UPDATE_PROFILE , this);

    }

    private void showInvoiceDialog(){
        if(dialogInvoice != null && dialogInvoice.isShowing()){
            return;
        }

        dialogInvoice = new Dialog(this);
        dialogInvoice.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogInvoice.setContentView(R.layout.dialog_invoice);
        WindowManager.LayoutParams params = dialogInvoice.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialogInvoice.getWindow().setAttributes(params);
        dialogInvoice.setCancelable(false);
        TextView tvAccBalance = (TextView)dialogInvoice.findViewById(R.id.tvAccBalance);
        buyTokens = (Button)dialogInvoice.findViewById(R.id.btnInvoiceBuyTokens) ;
        btnInvoiceCancel = (Button)dialogInvoice.findViewById(R.id.btnInvoiceCancel) ;
        tvInvoiceJobAmount = (TextView)dialogInvoice.findViewById(R.id.tvInvoiceJobAmount);
        tvInvoiceAdminPrice = (TextView)dialogInvoice.findViewById(R.id.tvInvoiceAdminPrice);
        tvInvoiceGrandTotal = (TextView)dialogInvoice.findViewById(R.id.tvInvoiceGrandTotal);
        tvAmountPayByCard = (TextView)dialogInvoice.findViewById(R.id.tvAmountPayByCard);
        tvAmountPayByCash = (TextView)dialogInvoice.findViewById(R.id.tvAmountPayByCash);
        btnInvoiceOk = (Button)dialogInvoice.findViewById(R.id.btnInvoiceOk);
        buyTokens.setVisibility(View.INVISIBLE);
        btnInvoiceOk.setOnClickListener(this);
        total = Double.parseDouble(activeJob.getAmount());
        adminPrice = Double.parseDouble(activeJob.getAdminCharge());
        jobAmount = total - adminPrice;
        if(Integer.parseInt(preferenceHelper.getUserToken())<1){
            buyTokens.setVisibility(View.VISIBLE);

            buyTokens.setText("Your Account Has Run out of Tokens. Please Buy Tokens, You can also Earn Tokens By Sharing This App with your Facebook Friends");
        }
        buyTokens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FeedbackActivity.this, "Integrate Payment System", Toast.LENGTH_SHORT).show();
                closeInvoiceDialog();
            }
        });
        btnInvoiceCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeInvoiceDialog();
            }
        });
        tvAccBalance.setText(Integer.parseInt(preferenceHelper.getUserToken())+" Tokens");
        tvInvoiceJobAmount.setText("R "+Formatter.invoiceDigitFormater(String.valueOf(jobAmount)));
        tvInvoiceAdminPrice.setText("R "+Formatter.invoiceDigitFormater(String.valueOf(adminPrice)));
        tvInvoiceGrandTotal.setText("R "+Formatter.invoiceDigitFormater(String.valueOf(total)));
        tvAmountPayByCard.setText("R "+Formatter.invoiceDigitFormater(String.valueOf(adminPrice)));
        tvAmountPayByCash.setText("R "+Formatter.invoiceDigitFormater(String.valueOf(jobAmount)));

        dialogInvoice.show();
    }



    private void closeInvoiceDialog(){
        if(dialogInvoice != null && dialogInvoice.isShowing()){
            dialogInvoice.dismiss();
            dialogInvoice = null;
        }
    }



    private void showImage(String imgUrl){
        if(!TextUtils.isEmpty(imgUrl))
        {
            imageDialog = new ImageDialog(this ,imgUrl);
            imageDialog.show();
        }
    }


}
