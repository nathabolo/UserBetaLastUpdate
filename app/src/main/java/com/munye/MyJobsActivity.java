package com.munye;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Toast;

import com.munye.user.R;
import com.munye.adapter.ViewPagerAdapter;

public class MyJobsActivity extends ActionBarBaseActivity implements View.OnClickListener {

    public boolean isLoadAgain = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_jobs);
        initToolBar();
        setToolBarTitle(getString(R.string.title_my_jobs));
        imgBtnDrawerToggle.setVisibility(View.INVISIBLE);

        imgBtnToolbarBack.setOnClickListener(this);

        ViewPager viewPager = (ViewPager) findViewById(R.id.myJobViewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabMyJobs);
        ViewPagerAdapter adapterViewPager = new ViewPagerAdapter(getSupportFragmentManager(), this);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(adapterViewPager);

    }catch (Exception e) {
            Toast.makeText(getApplicationContext(), ""+e, Toast.LENGTH_SHORT).show();
        }
        }



    @Override
    public void onClick(View v) {
        try{
        if(v.getId() == R.id.imgBtnActionBarBack)
            onBackPressed();
        }catch (Exception e) {
            Toast.makeText(getApplicationContext(), ""+e, Toast.LENGTH_SHORT).show();
        }}

    @Override
    public void onBackPressed() {
        try{
        if(this.isTaskRoot())
            backToMapActivity();
        super.onBackPressed();
        }catch (Exception e) {
            Toast.makeText(getApplicationContext(), ""+e, Toast.LENGTH_SHORT).show();
        }}
}
