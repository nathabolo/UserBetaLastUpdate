package com.munye;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.munye.user.R;
import com.munye.utils.AndyUtils;

import java.util.Locale;

public class EmailSupportActivity extends ActionBarBaseActivity implements View.OnClickListener {

    //Declaring EditText
    public EditText editSubject;
    public EditText editMessage;
    private TextToSpeech textToSpeech;
    private TextView speakText;
    private int status;


    //Send button
    private Button emailSupport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_support);

        initToolBar();
        imgBtnToolbarBack.setOnClickListener(this);

        //Initializing the views
        editSubject = (EditText) findViewById(R.id.editSubject);
        editMessage = (EditText) findViewById(R.id.editMessage);

        emailSupport = (Button) findViewById(R.id.emailSupport);

//        //Get the id of the text view
        speakText = (TextView) findViewById(R.id.speakText);

        speakText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editMessage.getText().toString();

                //Speak the text here for people with disabilities
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });


//        //Adding click listener
        emailSupport.setOnClickListener(this);

//        //Action bar for the back/home buttom
//        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void sendEmail() {
        //Getting content for email
        // String email = editTextEmail.getText().toString().trim();

        String subject = editSubject.getText().toString().trim();
        String message = editMessage.getText().toString().trim();

        //Creating SendMail object
        SendMail sm = new SendMail(this, subject, message);

        if (subject.isEmpty()) {
            Toast.makeText(EmailSupportActivity.this, "Please enter your subject", Toast.LENGTH_LONG).show();

        } else if (message.isEmpty()) {
            Toast.makeText(EmailSupportActivity.this, "Please enter your message", Toast.LENGTH_LONG).show();
        } else {
            //Executing sendmail to send email
            sm.execute();
        }

    }

    @Override
    public void onClick(View v) {

        sendEmail();

        switch (v.getId()){
            case R.id.imgBtnActionBarBack:
                onBackPressed();
                break;

            default:
                AndyUtils.generateLog("default action");
                break;
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onPause() {

        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onPause();
    }
}
