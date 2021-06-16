package com.example.codeexp2021app.ui.activity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.codeexp2021app.BR;
import com.example.codeexp2021app.R;
import com.example.codeexp2021app.adapter.BluetoothAdapter;
import com.example.codeexp2021app.base.BaseActivity;
import com.example.codeexp2021app.bluetooth.BluetoothHelper;
import com.example.codeexp2021app.databinding.ActivityBluetoothBinding;
import com.example.codeexp2021app.listener.OnItemClickListener;
import com.example.codeexp2021app.listener.OnMultiClickListener;
import com.example.codeexp2021app.ui.viewmodel.BluetoothViewModel;
import com.example.codeexp2021app.ui.widget.ToolBar;
import com.example.codeexp2021app.utils.ContextUtils;
import com.example.codeexp2021app.utils.GPSUtils;
import com.example.codeexp2021app.utils.ListenerUtils;
import com.example.codeexp2021app.utils.StatusBarUtils;
import com.example.codeexp2021app.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class BluetoothActivity extends BaseActivity<ActivityBluetoothBinding, BluetoothViewModel> {

    private static final String TAG = BluetoothActivity.class.getSimpleName();
    private static final int START_LOCATION_ACTIVITY = 99;

    private BluetoothAdapter mBluetoothAdapter;
    private List<BluetoothDevice> bluetoothDevices = new ArrayList<>();

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_bluetooth;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public Class<BluetoothViewModel> getViewModelClazz() {
        return BluetoothViewModel.class;
    }

    @Override
    public void initData() {
        super.initData();
        refresh();
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        initView();
        setClickListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 打开蓝牙
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (BluetoothHelper.getInstance().bluetoothState()) {
                    if (GPSUtils.isOpenGPS(BluetoothActivity.this)) {
                        refresh();
                    } else {
                        startLocation();
                    }
                }
            }
        }, 1000);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == START_LOCATION_ACTIVITY) {
            if (!GPSUtils.isOpenGPS(BluetoothActivity.this)) {
                startLocation();
            }
        }
    }

    private void initView() {
        StatusBarUtils.updateStatusBarColor(this, ContextCompat.getColor(ContextUtils.getContext(), R.color.light_green));
        mBinding.toolbar.setTitle(ContextUtils.getContext().getString(R.string.devices_list)).setLeftImage(R.drawable.ic_back)
                .setToolBarClickListener(new ToolBar.ToolBarClickListener() {
                    @Override
                    public void leftIconClick() {
                        finish();
                    }
                    @Override
                    public void rightIconClick() {

                    }
                });
        mBluetoothAdapter = new BluetoothAdapter(this, bluetoothDevices);
        LinearLayoutManager bluetoothLinearLayoutManager = new LinearLayoutManager(this);
        DividerItemDecoration bluetoothDividerItemDecoration = new DividerItemDecoration(mBinding.rvBluetooth.getContext(), bluetoothLinearLayoutManager.getOrientation());
        mBinding.rvBluetooth.setLayoutManager(bluetoothLinearLayoutManager);
        mBinding.rvBluetooth.addItemDecoration(bluetoothDividerItemDecoration);
        mBinding.rvBluetooth.setAdapter(mBluetoothAdapter);
    }

    private void setClickListener() {
        mBluetoothAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ToastUtils.showShortSafe("Connecting " + bluetoothDevices.get(position).getName());
                BluetoothHelper.getInstance().connect(bluetoothDevices.get(position));
            }
        });
        mBinding.srlRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mBinding.srlRefresh.setRefreshing(false);
                refresh();
            }
        });
        ListenerUtils.setOnClickListener(mBinding.tvDisconnect, new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                BluetoothHelper.getInstance().disconnect();
            }
        });
    }

    //刷新的具体实现
    private void refresh() {
        bluetoothDevices.clear();
        bluetoothDevices.addAll(BluetoothHelper.getInstance().scan());
    }

    // 开启位置权限
    private void startLocation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tips")
                .setMessage("Please turn on your GPS")
                .setCancelable(false)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, START_LOCATION_ACTIVITY);
                    }
                }).show();
    }
}