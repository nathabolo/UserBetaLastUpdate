package com.munye;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.MediaRouteButton;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.munye.dialog.CustomDialog;
import com.munye.dialog.CustomTitleDialog;
import com.munye.fragment.FragmeentThree;
import com.munye.fragment.FragmeentTwo;
import com.munye.fragment.FragmentOne;
import com.munye.model.Provider;
import com.munye.model.ProviderType;
import com.munye.parse.AsyncTaskCompleteListener;
import com.munye.parse.HttpRequester;
import com.munye.user.R;
import com.munye.utils.AndyUtils;
import com.munye.utils.Const;
import com.munye.utils.Formatter;
import com.stripe.android.model.Card;
import com.sun.mail.iap.Response;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import static com.stripe.android.model.Card.AMERICAN_EXPRESS;
import static com.stripe.android.model.Card.DINERS_CLUB;
import static com.stripe.android.model.Card.DISCOVER;
import static com.stripe.android.model.Card.JCB;
import static com.stripe.android.model.Card.MASTERCARD;
import static com.stripe.android.model.Card.UNKNOWN;
import static com.stripe.android.model.Card.VISA;

public class LandingActivity extends ActionBarBaseActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, AsyncTaskCompleteListener {

    private ImageView mapImageView;
    private ImageView profileImageView;
    private ImageView jobImageView;
    private ImageView contactImageView;
    private ImageView viewQuotesImageView;
    private ImageView tokensImageView;
    private ImageButton imgBtnToolbarBack;
    private Toolbar toolbar;
    private TextView tvToolbarTitle;
    private ImageButton imgBtnDrawerToggle;
    private DrawerLayout drawer;
    private View viewHeader;
    private ImageView imgNavHeaderUserImage;
    private TextView tvNavHeaderUserName;
    private TextView tvTokenBalance;
    private CustomTitleDialog exitDialog;
    private CustomTitleDialog logoutDialog;
    private Dialog dialogAddCard;
    private ImageView imgViewCloseDialog;
    private EditText edtCreditCardNo;
    private static final Pattern CODE_PATTERN = Pattern.compile("([0-9]{0,4})|([0-9]{4}-)+|([0-9]{4}-[0-9]{0,4})+");
    private EditText edtCardMonth;
    private EditText edtCardYear;
    private EditText edtCardCvv;
    private Button btnSubmitCard;
    private String cardType;
    private boolean opt;
    private boolean amnt;
    private ArrayList<Provider> listProvider;
    private ArrayList<ProviderType> listProviderType;
    private ArrayAdapter providerTypeAdapter;
    private String selectedProviderType;
    private String selectedProviderName;
    private String imageUrlProviderType;
    private String iconColor;
    private MediaRouteButton actvplaceAutocomplete;
    private AlertDialog.Builder serviceSpinner;
    private CustomDialog pendingAmountDialog;
    private MediaRouteButton recyclerViewProviderTypeList;
    private MediaRouteButton imgViewClearSearch;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       View.inflate(this, R.layout.activity_landing, null);
       setContentView(R.layout.activity_landing);
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.action_item1:
                                selectedFragment = FragmentOne.newInstance();
                                Intent fragOneIntent = new Intent(getApplicationContext(), MyJobsActivity.class);
                                startActivity(fragOneIntent);

                                break;
                            case R.id.action_item2:
                                selectedFragment = FragmeentTwo.newInstance();
                                Intent fragTwoIntent = new Intent(getApplicationContext(), ViewQuotesActivity.class);
                                startActivity(fragTwoIntent);
                                break;
                            case R.id.action_item3:
                                selectedFragment = FragmeentThree.newInstance();
                                Intent fragThreeIntent = new Intent(getApplicationContext(), EmailSupportActivity.class);
                                startActivity(fragThreeIntent);
                                break;
                        }
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, FragmentOne.newInstance());
        transaction.commit();


        initToolBar();
        imgBtnToolbarBack.setOnClickListener(this);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //initToolBar();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        imgBtnDrawerToggle.setOnClickListener(this);
        //imgBtnToolbarBack.setOnClickListener(this);
        tvToolbarTitle.setOnClickListener(this);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        viewHeader = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        //navigation Header items....
        imgNavHeaderUserImage = (ImageView)viewHeader.findViewById(R.id.imgNavHeaderUserImage);
        tvNavHeaderUserName = (TextView)viewHeader.findViewById(R.id.tvNavHeaderUserName);
        tvTokenBalance = (TextView)viewHeader.findViewById(R.id.tvTokenBalance);
        imgNavHeaderUserImage.setOnClickListener(this);



        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.id.jobImageView, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        String imageType = options.outMimeType;
        imgBtnToolbarBack.setVisibility(View.INVISIBLE);

        //Find the Id's of the Image views
        mapImageView = (ImageView)findViewById(R.id.mapImageView);
        profileImageView = (ImageView)findViewById(R.id.profileImageView);
        jobImageView = (ImageView)findViewById(R.id.jobImageView);
        contactImageView = (ImageView)findViewById(R.id.contactImageView);
        viewQuotesImageView = (ImageView)findViewById(R.id.viewQuotesImageView);
        tokensImageView = (ImageView)findViewById(R.id.tokensImageView);

        //Set the onclick listeners on the image views
        mapImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(mapIntent);
                Toast.makeText(LandingActivity.this, "You have been directed to a map activity", Toast.LENGTH_LONG).show();
            }
        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(getApplicationContext(), UpdateProfileActivity.class);
                startActivity(profileIntent);
                Toast.makeText(LandingActivity.this, "You have been directed to a profile update activity", Toast.LENGTH_LONG).show();
            }
        });

        jobImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent jobIntent = new Intent(getApplicationContext(), MyJobsActivity.class);
                startActivity(jobIntent);
                Toast.makeText(LandingActivity.this, "You have been directed to your jobs activity", Toast.LENGTH_LONG).show();
            }
        });

        contactImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contactIntent = new Intent(getApplicationContext(), EmailSupportActivity.class);
                startActivity(contactIntent);
                Toast.makeText(LandingActivity.this, "You have been directed to support activity", Toast.LENGTH_LONG).show();
            }
        });

        viewQuotesImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewQuotesIntent = new Intent(getApplicationContext(), ViewQuotesActivity.class);
                Toast.makeText(LandingActivity.this, "You have been directed to view quotes activity", Toast.LENGTH_LONG).show();
                startActivity(viewQuotesIntent);
            }
        });



        tokensImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(LandingActivity.this, tokensImageView);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.token_menu, popup.getMenu());

                //Registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        // Handle navigation view item clicks here.
                        int id = item.getItemId();

                         if(id == R.id.shareTokens){
                            //shareMunye();
                            doShareLink();
                        }

                        else if(id == R.id.buyTokens){
                            final Dialog buyTokensDialog = new Dialog(LandingActivity.this);
                            buyTokensDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            buyTokensDialog.setContentView(R.layout.buy_tokens);
                            buyTokensDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
                            Button btnSubmit = (Button) buyTokensDialog.findViewById(R.id.btnInvoiceOk);
                            Button btnCancel = (Button) buyTokensDialog.findViewById(R.id.btnInvoiceCancel);
                            final EditText key = (EditText) buyTokensDialog.findViewById(R.id.verificationKey);

                            TextView txtAdminPrice = (TextView) buyTokensDialog.findViewById(R.id.msg);
                            //txtAdminPrice.setText("Select the Amount of Tokens you want to buy");

                            btnSubmit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {


                                    exitDialog = new CustomTitleDialog(LandingActivity.this , "Buy 5 Tokens" , "Are you sure you want to buy Tokens?\n\nTo Buys Tokens you are required to enter details of your Debit/Credit Card for payment  and proceed" ,
                                            "PROCEED", "NO") {

                                        public void positiveResponse() {
                                            buyTokensDialog.dismiss();
                                            exitDialog.dismiss();
                                            openAddCardDialog(10,"buy_tokens");
                                        }


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

                                    exitDialog = new CustomTitleDialog(LandingActivity.this , "Buy 10 Tokens" , "Are you sure you want to buy Tokens?\n\nTo Buys Tokens you are required to enter details of your Debit/Credit Card for payment  and proceed" ,
                                            "PROCEED", "NO") {

                                        public void positiveResponse() {
                                            buyTokensDialog.dismiss();
                                            exitDialog.dismiss();
                                            openAddCardDialog(12,"buy_tokens");
                                        }


                                        public void negativeResponse() {
                                            exitDialog.dismiss();
                                        }
                                    };
                                    exitDialog.show();

                                }
                            });
                            buyTokensDialog.show();
                        }
                        drawer.closeDrawer(GravityCompat.START);
                        return true;

//                        switch (item.getItemId()) {
//                            case R.id.buyTokens:
//                                Intent buyTokensIntent = new Intent(getApplicationContext(), EmailSupportActivity.class);
//                                startActivity(buyTokensIntent);
//                                return true;
//                            case R.id.shareTokens:
//                                Intent shareTokensIntent = new Intent(getApplicationContext(), RegisterActivity.class);
//                                startActivity(shareTokensIntent);
//                                return true;
//                            default:
//                                return true;
//                        }
                    }
                });

                popup.show(); //showing popup menu
            }
        });

    }

    public void initToolBar() {
        toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        tvToolbarTitle = (TextView)findViewById(R.id.tvToolBarTitle);
        imgBtnToolbarBack = (ImageButton)findViewById(R.id.imgBtnActionBarBack);
        imgBtnDrawerToggle = (ImageButton)findViewById(R.id.imgBtnDrawerToggle);
        imgBtnToolbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    //
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.imgBtnDrawerToggle:
            case R.id.tvToolBarTitle:
                drawer.openDrawer(GravityCompat.START);
                break;

            case R.id.imgNavHeaderUserImage:
                startActivity(new Intent(this,UpdateProfileActivity.class));
                drawer.closeDrawer(GravityCompat.START);
                break;

            case R.id.imgBtnActionBarBack:
                onBackPressed();
                break;

            default:
                AndyUtils.generateLog("map onclick default");
                break;

    }
}

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile_update) {

            startActivity(new Intent(LandingActivity.this , UpdateProfileActivity.class));
        } else if (id == R.id.nav_view_quotes) {
            startActivity(new Intent(LandingActivity.this , ViewQuotesActivity.class));

        } else if (id == R.id.nav_my_jobs) {
            startActivity(new Intent(LandingActivity.this , MyJobsActivity.class));

        } else if (id == R.id.help) {
            startActivity(new Intent(LandingActivity.this , EmailSupportActivity.class));
            //help();

        } else if (id == R.id.nav_logout) {
            showLogoutDialog();
        }
        else if(id == R.id.nav_payment){
            //shareMunye();
            doShareLink();
        }

        else if(id == R.id.buyTokens){
            final Dialog buyTokensDialog = new Dialog(LandingActivity.this);
            buyTokensDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            buyTokensDialog.setContentView(R.layout.buy_tokens);
            buyTokensDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
            Button btnSubmit = (Button) buyTokensDialog.findViewById(R.id.btnInvoiceOk);
            Button btnCancel = (Button) buyTokensDialog.findViewById(R.id.btnInvoiceCancel);
            final EditText key = (EditText) buyTokensDialog.findViewById(R.id.verificationKey);

            TextView txtAdminPrice = (TextView) buyTokensDialog.findViewById(R.id.msg);
            //txtAdminPrice.setText("Select the Amount of Tokens you want to buy");

            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    exitDialog = new CustomTitleDialog(LandingActivity.this , "Buy 5 Tokens" , "Are you sure you want to buy Tokens?\n\nTo Buys Tokens you are required to enter details of your Debit/Credit Card for payment  and proceed" ,
                            "PROCEED", "NO") {

                        public void positiveResponse() {
                            buyTokensDialog.dismiss();
                            exitDialog.dismiss();
                            openAddCardDialog(10,"buy_tokens");
                        }


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

                    exitDialog = new CustomTitleDialog(LandingActivity.this , "Buy 10 Tokens" , "Are you sure you want to buy Tokens?\n\nTo Buys Tokens you are required to enter details of your Debit/Credit Card for payment  and proceed" ,
                            "PROCEED", "NO") {

                        public void positiveResponse() {
                            buyTokensDialog.dismiss();
                            exitDialog.dismiss();
                            openAddCardDialog(12,"buy_tokens");
                        }


                        public void negativeResponse() {
                            exitDialog.dismiss();
                        }
                    };
                    exitDialog.show();

                }
            });
            buyTokensDialog.show();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

//    private void shareMunye() {
//        try{
//            Intent intentShare = new Intent(LandingActivity.this , UpdateProfileActivity.class);
//            intentShare.putExtra("share" , "share");
//            startActivity(intentShare);
//        }catch (Exception e) {
//        }
//    }


    private void doShareLink() {

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        Intent chooserIntent = Intent.createChooser(shareIntent, getString(R.string.share_to_earn_tokens));

        // for 21+, we can use EXTRA_REPLACEMENT_EXTRAS to support the specific case of Facebook
        // (only supports a link)
        // >=21: facebook=link, other=text+link
        // <=20: all=link
        String link = "http://jimmiejob.com/admin_portal/uploads/User.apk";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String text = "Install the apk to earn tokens with jimmie jobs";
            shareIntent.putExtra(Intent.EXTRA_TEXT, text + " " + link);
            Bundle facebookBundle = new Bundle();
            facebookBundle.putString(Intent.EXTRA_TEXT, link);
            Bundle replacement = new Bundle();
            replacement.putBundle("com.facebook.katana", facebookBundle);
            chooserIntent.putExtra(Intent.EXTRA_REPLACEMENT_EXTRAS, replacement);
        } else {
            shareIntent.putExtra(Intent.EXTRA_TEXT, link);
        }

        chooserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(chooserIntent);

    }

    private void openAddCardDialog(int i, String buy_tokens) {


        try{
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
            setTextWatcher();

            btnSubmitCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        String x = year + "".replace("20", "");
                        int y = Integer.parseInt(x);
                        if (cardType.equals("Unknown")) {
                            Toast.makeText(LandingActivity.this, "Invalid Card", Toast.LENGTH_SHORT).show();
                        }
                        if (Integer.parseInt(edtCardMonth.getText().toString()) > 12) {
                            Toast.makeText(LandingActivity.this, "Invalid Expiry Month", Toast.LENGTH_SHORT).show();
                        }
                        if (Integer.parseInt(edtCardYear.getText().toString()) + 2000 < y) {

                            Toast.makeText(LandingActivity.this, "Invalid Expiry Year", Toast.LENGTH_SHORT).show();
                        } else {

                            exitDialog = new CustomTitleDialog(LandingActivity.this , "Make Payment" , "Pay for Tokens ?" ,
                                    "YES", "NO") {

                                public void positiveResponse() {
                                    exitDialog.dismiss();
                                    dialogAddCard.dismiss();
                                    Intent intentShare = new Intent(LandingActivity.this, UpdateProfileActivity.class);
                                    intentShare.putExtra("share", opt);
                                    intentShare.putExtra("amnt", amnt);
                                    intentShare.putExtra("c_no", edtCreditCardNo.getText().toString().replace("-", ""));
                                    intentShare.putExtra("cvv", edtCardCvv.getText().toString());
                                    intentShare.putExtra("exp_yr", edtCardYear.getText().toString());
                                    intentShare.putExtra("exp_m", edtCardMonth.getText().toString());
                                    intentShare.putExtra("c_typ", cardType);
                                    startActivity(intentShare);
                                }


                                public void negativeResponse() {
                                    exitDialog.dismiss();
                                }
                            };
                            exitDialog.show();


                        }
                    }catch (Exception e){
                        Toast.makeText(LandingActivity.this, "Input Error", Toast.LENGTH_SHORT).show();
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
        }catch (Exception e) {
        }

    }

    private void setTextWatcher() {
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

                try {
                    if (s.length() > 0 && !CODE_PATTERN.matcher(s).matches()) {
                        String input = s.toString();
                        String numberOnly = keepNumberOnly(input);
                        String code = formatCardNo(numberOnly);
                        edtCreditCardNo.removeTextChangedListener(this);
                        edtCreditCardNo.setText(code);
                        edtCreditCardNo.setSelection(code.length());
                        edtCreditCardNo.addTextChangedListener(this);
                    }

                } catch (Exception e) {
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

    private void setCardTypeDrawable(String cradTypes) {
        try{
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
        }catch (Exception e) {
        }
    }

    private String getCardType(String prifix) {
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
            }


            else {
                return UNKNOWN;
            }
        }
    }

    private void showLogoutDialog() {
        logoutDialog = new CustomTitleDialog(this , getString(R.string.dialog_title_logout) , getString(R.string.dialog_message_are_you_sure) ,
                getString(R.string.dialog_button_logout), getString(R.string.dialog_button_cancel)) {
            @Override
            public void positiveResponse() {
                logout();
            }

            @Override
            public void negativeResponse() {
                logoutDialog.dismiss();
            }
        };
        logoutDialog.show();
    }

    private void logout() {
        try{
            HashMap<String , String> map = new HashMap<>();

            map.put(Const.URL , Const.ServiceType.LOGOUT);
            map.put(Const.Params.ID , preferenceHelper.getId());
            map.put(Const.Params.TOKEN , preferenceHelper.getToken());

            AndyUtils.showCustomProgressDialog(this , false);
            new HttpRequester(this , map , Const.ServiceCode.LOGOUT , Const.httpRequestType.POST , this);

        }catch (Exception e) {
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTaskCompleted(final String response, int serviceCode) {

        try{

            AndyUtils.removeCustomProgressDialog();
            switch (serviceCode)
            {
                case Const.ServiceCode.GET_COMPANY_LIST:
                    if(dataParser.isSuccess(response)){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                listProvider = new ArrayList<>();
                                listProvider = dataParser.parseNearestProvider(response);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        //setProviderOnMap();
                                    }
                                });
                            }
                        }).start();
                    }
                    break;

                case Const.ServiceCode.GET_TYPES:
                    if(dataParser.isSuccess(response)){

                        listProviderType.clear();
                        listProviderType = dataParser.parseTypesOfProviders(response , listProviderType);
                        providerTypeAdapter.notifyDataSetChanged();
                        selectedProviderType = listProviderType.get(0).getId();
                        selectedProviderName = listProviderType.get(0).getName();
                        imageUrlProviderType = listProviderType.get(0).getPicture();
                        iconColor = AndyUtils.getColorCode(0 % 6 , getApplicationContext());

                        hideProviderType();

                        List<String> spinnerArray = new ArrayList<String>();
                        spinnerArray.add("SELECT CATEGORY");
                        for(int i = 0; i <listProviderType.size(); i++) {

                            spinnerArray.add(listProviderType.get(i).getName());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                this, android.R.layout.simple_spinner_item, spinnerArray);

                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                       // serviceSpinner.setAdapter(adapter);
                    }
                    break;


                case Const.ServiceCode.LOGOUT:
                    if(logoutDialog != null && logoutDialog.isShowing())
                        logoutDialog.dismiss();
                    preferenceHelper.logout();
                    startActivity(new Intent(this , SignInActivity.class));
                    finish();
                    break;


                case Const.ServiceCode.GET_PROFILE:
                    if(dataParser.isSuccess(response)){
                        dataParser.parsePendingAmontDetail(response);
                        checkPendingAmount();
                    }
                    break;


                case Const.ServiceCode.PAY_OLD_REQUEST:
                    closePendingAmountDialog();
                    if(dataParser.isSuccess(response)){
                        dataParser.parseOldRequstPay(response);
                    }
                    break;


                default:
                    AndyUtils.generateLog("OnTaskComplete default");
                    break;
            }


        }catch (Exception e) {
        }

    }

    private void closePendingAmountDialog() {
        if(pendingAmountDialog != null && pendingAmountDialog.isShowing()){
            pendingAmountDialog.dismiss();
            pendingAmountDialog = null;
        }
    }

    private void checkPendingAmount() {
        double pendingAmout = Double.parseDouble(preferenceHelper.getPendingAmount());
        if(pendingAmout > 0)
            showPendingAmountDialog(pendingAmout);
    }

    private void showPendingAmountDialog(double amount) {
        String amountToDisplay = Formatter.invoiceDigitFormater(String.valueOf(amount))+"$";
        String content = getString(R.string.dialog_message_pending_payment)+" "+amountToDisplay;
        String buttonPay = getString(R.string.dialog_button_pay)+" "+amountToDisplay;
        pendingAmountDialog = new CustomDialog(this , content, buttonPay , getString(R.string.dialog_button_cancel) , false) {
            @Override
            public void positiveButton() {
                payOldRequest();
            }

            @Override
            public void negativeButton() {
                closePendingAmountDialog();
            }
        };
        pendingAmountDialog.show();
    }

    private void payOldRequest() {
        HashMap<String , String> map = new HashMap<>();

        map.put(Const.URL , Const.ServiceType.PAY_OLD_REQUEST);
        map.put(Const.Params.ID , preferenceHelper.getId());
        map.put(Const.Params.TOKEN , preferenceHelper.getToken());

        AndyUtils.showCustomProgressDialog(this ,false);
        new HttpRequester(this , map , Const.ServiceCode.PAY_OLD_REQUEST , Const.httpRequestType.POST , this);

    }

    private void hideProviderType() {
        if(!TextUtils.isEmpty(selectedProviderName) && !TextUtils.isEmpty(iconColor)){
//            imgViewSelectedProviderType.setColorFilter(Color.parseColor(iconColor));
//            Glide.with(this)
//                    .load(imageUrlProviderType)
//                    .skipMemoryCache(true)
//                    .placeholder(R.drawable.search_icon)
//                    .into(imgViewSelectedProviderType);
        }
        //  tvHireExpertText.setVisibility(View.GONE);
        actvplaceAutocomplete.setVisibility(View.VISIBLE);
        recyclerViewProviderTypeList.setVisibility(View.GONE);
        imgViewClearSearch.setVisibility(View.VISIBLE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //Toast.makeText(MapActivity.this, convertToBase64(file), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
             //showExitDialog();
            finish();
            Intent i = new Intent(getApplicationContext(),LandingActivity.class);
            startActivity(i);
        }
    }

    private void showExitDialog() {
        exitDialog = new CustomTitleDialog(this , getString(R.string.dialog_title_exit) , getString(R.string.dialog_message_are_you_sure) ,
                getString(R.string.dialog_button_yes), getString(R.string.dialog_button_no)) {
            @Override
            public void positiveResponse() {
                exitDialog.dismiss();
                finish();
            }

            @Override
            public void negativeResponse() {
                exitDialog.dismiss();
            }
        };
        exitDialog.show();
    }

}


