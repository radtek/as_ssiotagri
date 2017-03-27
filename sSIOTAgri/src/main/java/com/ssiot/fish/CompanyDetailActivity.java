package com.ssiot.fish;

import android.os.Bundle;
import android.widget.TextView;

import com.ssiot.agri.R;
import com.ssiot.remote.data.model.IOTCompanyModel;

public class CompanyDetailActivity extends HeadActivity{
    IOTCompanyModel model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = (IOTCompanyModel) getIntent().getSerializableExtra("company");
        setContentView(R.layout.activity_company_detail);
        TextView titleView = (TextView) findViewById(R.id.company_title);
        TextView urlView = (TextView) findViewById(R.id.company_url);
        TextView detailView = (TextView) findViewById(R.id.company_detail);
        TextView remarkView = (TextView) findViewById(R.id.company_remark);
        
        titleView.setText("公司名称:"+model._name);
        urlView.setText("公司网站:"+model._url);
        detailView.setText("公司详细介绍:\n" + model._companyContent);
        urlView.setText("备注:"+model._remark);
    }
}