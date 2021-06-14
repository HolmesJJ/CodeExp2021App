package com.example.codeexp2021app.init;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import com.example.codeexp2021app.BR;
import com.example.codeexp2021app.R;
import com.example.codeexp2021app.base.BaseActivity;
import com.example.codeexp2021app.constants.Constants;
import com.example.codeexp2021app.databinding.ActivityInitBinding;
import com.example.codeexp2021app.utils.ContextUtils;
import com.example.codeexp2021app.utils.PermissionsUtils;

import pub.devrel.easypermissions.EasyPermissions;

public class InitActivity extends BaseActivity<ActivityInitBinding, InitViewModel> {

    private static final String TAG = InitActivity.class.getSimpleName();

    private static final String[] PERMISSIONS = new String[]{
            android.Manifest.permission.FOREGROUND_SERVICE,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.INTERNET,
    };

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_init;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public Class<InitViewModel> getViewModelClazz() {
        return InitViewModel.class;
    }

    @Override
    public void initData() {
        super.initData();
        PermissionsUtils.doSomeThingWithPermission(this, () -> {
            if (mViewModel != null) {
                mViewModel.initData();
            }
        }, PERMISSIONS, Constants.PERMISSION_REQUEST_CODE, R.string.rationale_init);
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        showLoading(false);
        mViewModel.mActivityAction.observe(this, activityAction -> {
            Intent intent = new Intent(ContextUtils.getContext(), activityAction);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLoading();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected boolean onHasPermissions() {
        return EasyPermissions.hasPermissions(this, PERMISSIONS);
    }

    @Override
    protected void onPermissionSuccessCallbackFromSetting() {
        super.onPermissionSuccessCallbackFromSetting();
        if (mViewModel != null) {
            mViewModel.initData();
        }
    }
}
