package com.example.codeexp2021app;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;

import com.example.codeexp2021app.base.BaseActivity;
import com.example.codeexp2021app.config.Config;
import com.example.codeexp2021app.constants.Constants;
import com.example.codeexp2021app.constants.SpUtilValueConstants;
import com.example.codeexp2021app.databinding.ActivityMainBinding;
import com.example.codeexp2021app.listener.OnMultiClickListener;
import com.example.codeexp2021app.service.AudioCaptureService;
import com.example.codeexp2021app.ui.widget.dialog.RadioSelectDialog;
import com.example.codeexp2021app.ui.widget.dialog.SeekBarDialog;
import com.example.codeexp2021app.utils.ContextUtils;
import com.example.codeexp2021app.utils.ListenerUtils;
import com.example.codeexp2021app.utils.StatusBarUtils;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private final static int MIN_FRONT_SIZE = 1;
    private final static int MAX_FRONT_SIZE = 50;
    private final static int MIN_VOLUME = 1;
    private final static int MAX_VOLUME = 10;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public Class<MainViewModel> getViewModelClazz() {
        return MainViewModel.class;
    }

    @Override
    public void initData() {
        super.initData();
        StatusBarUtils.updateStatusBarColor(this, ContextCompat.getColor(ContextUtils.getContext(), R.color.light_green));
        mBinding.toolbar.setTitle(ContextUtils.getContext().getString(R.string.main_menu));
        mBinding.fontSize.setLeftText(R.string.front_size).setRightText(Config.sFrontSize + "sp").setBottomLineVisible(false)
                .setItemClickListener(
                        this::showUpdateFontSizeDialog
                );
        mBinding.fontColor.setLeftText(R.string.front_color).setRightText(Config.sFrontColor).setBottomLineVisible(false)
                .setItemClickListener(
                        this::showUpdateFontColorDialog
                );
        mBinding.volume.setLeftText(R.string.volume).setRightText(String.valueOf(Config.sVolume)).setBottomLineVisible(false)
                .setItemClickListener(
                        this::showUpdateVolumeDialog
                );
        mBinding.voiceType.setLeftText(R.string.voice_type).setRightText(getVoiceType()).setBottomLineVisible(false)
                .setItemClickListener(
                        this::showUpdateVoiceTypeDialog
                );
        mBinding.isShowEmoji.setLeftText(R.string.show_emoji).setRightText(Boolean.toString(Config.sIsShowEmoji)).setBottomLineVisible(true)
                .setItemClickListener(
                        this::showUpdateShowEmojiDialog
                );
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        setObserveListener();
        setClickListener();
        mViewModel.initCapturingBtnState();
        if (mViewModel != null) {
            mViewModel.createToken();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setObserveListener() {
        mViewModel.mEnableStartCapturing.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                mBinding.tvStartCapturing.setEnabled(aBoolean);
                if (aBoolean) {
                    mBinding.tvStartCapturing.setTextColor(ContextCompat.getColor(ContextUtils.getContext(), R.color.itemEnable));
                } else {
                    mBinding.tvStartCapturing.setTextColor(ContextCompat.getColor(ContextUtils.getContext(), R.color.itemDisable));
                }
            }
        });
        mViewModel.mEnableStopCapturing.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                mBinding.tvStopCapturing.setEnabled(aBoolean);
                if (aBoolean) {
                    mBinding.tvStopCapturing.setTextColor(ContextCompat.getColor(ContextUtils.getContext(), R.color.itemEnable));
                } else {
                    mBinding.tvStopCapturing.setTextColor(ContextCompat.getColor(ContextUtils.getContext(), R.color.itemDisable));
                }
            }
        });
        mViewModel.mErrorMsg.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String value) {
                if (mBinding != null) {
                    mBinding.tvErrorMsg.setText(value);
                }
            }
        });
    }

    private void setClickListener() {
        ListenerUtils.setOnClickListener(mBinding.tvStartCapturing, new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                startAudioCaptureService();
                mViewModel.startCapturingBtnState();
            }
        });
        ListenerUtils.setOnClickListener(mBinding.tvStopCapturing, new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                stopAudioCaptureService();
                mViewModel.stopCapturingBtnState();
            }
        });
    }

    public void startAudioCaptureService() {
        Intent serviceIntent = new Intent(ContextUtils.getContext(), AudioCaptureService.class);
        serviceIntent.setAction(Constants.AUDIO_CAPTURE_SERVICE_START);
        startForegroundService(serviceIntent);
    }

    public void stopAudioCaptureService() {
        Intent serviceIntent = new Intent(ContextUtils.getContext(), AudioCaptureService.class);
        serviceIntent.setAction(Constants.AUDIO_CAPTURE_SERVICE_STOP);
        stopService(serviceIntent);
    }

    private void showUpdateFontSizeDialog() {
        SeekBarDialog seekBarDialog =
                new SeekBarDialog(mContext).setDialogTitle(R.string.front_size)
                        .setRemark(R.string.front_size_unit).setUnit("sp").setRange(MIN_FRONT_SIZE, MAX_FRONT_SIZE)
                        .setCurrent(Config.sFrontSize)
                        .setDialogEventListener(new SeekBarDialog.DialogEventListener() {
                            @Override
                            public void confirm(int progress) {
                                mBinding.fontSize.setRightText(progress + "sp");
                                Config.setFrontSize(progress);
                            }
                            @Override
                            public void cancel() {

                            }
                        });
        seekBarDialog.show();
    }

    private void showUpdateFontColorDialog() {
        ColorPickerDialogBuilder
                .with(MainActivity.this)
                .setTitle(R.string.front_color)
                .initialColor(Color.parseColor(Config.sFrontColor))
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {

                    }
                })
                .setPositiveButton(R.string.confirm, new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        mBinding.fontColor.setRightText(String.format("#%06X", (0xFFFFFF & selectedColor)));
                        Config.setFrontColor(String.format("#%06X", (0xFFFFFF & selectedColor)));
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .build()
                .show();
    }

    private void showUpdateVolumeDialog() {
        SeekBarDialog seekBarDialog =
                new SeekBarDialog(mContext).setDialogTitle(R.string.volume).setRange(MIN_VOLUME, MAX_VOLUME)
                        .setCurrent(Config.sVolume)
                        .setDialogEventListener(new SeekBarDialog.DialogEventListener() {
                            @Override
                            public void confirm(int progress) {
                                mBinding.volume.setRightText(String.valueOf(progress));
                                Config.setVolume(progress);
                            }
                            @Override
                            public void cancel() {

                            }
                        });
        seekBarDialog.show();
    }

    private void showUpdateVoiceTypeDialog() {
        List<RadioSelectDialog.Item> items = new ArrayList<>();
        items.add(new RadioSelectDialog.Item(0, Config.sVoiceType == SpUtilValueConstants.NORMAL, getString(R.string.normal)));
        items.add(new RadioSelectDialog.Item(1, Config.sVoiceType == SpUtilValueConstants.TREBLE, getString(R.string.treble)));
        items.add(new RadioSelectDialog.Item(2, Config.sVoiceType == SpUtilValueConstants.BASS, getString(R.string.bass)));

        RadioSelectDialog radioSelectDialog =
                new RadioSelectDialog(mContext).setDialogTitle(R.string.voice_type).setItemList(items)
                        .setSelectCallback(new RadioSelectDialog.SelectCallback() {
                            @Override
                            public void cancel() {

                            }
                            @Override
                            public void select(RadioSelectDialog.Item item) {
                                mBinding.voiceType.setRightText(item.description);
                                Config.setVoiceType(item.id);
                            }
                        });
        radioSelectDialog.show();
    }

    private void showUpdateShowEmojiDialog() {
        List<RadioSelectDialog.Item> items = new ArrayList<>();
        items.add(new RadioSelectDialog.Item(0, Config.sIsShowEmoji, Boolean.toString(true)));
        items.add(new RadioSelectDialog.Item(1, !Config.sIsShowEmoji, Boolean.toString(false)));

        RadioSelectDialog radioSelectDialog =
                new RadioSelectDialog(mContext).setDialogTitle(R.string.show_emoji).setItemList(items)
                        .setSelectCallback(new RadioSelectDialog.SelectCallback() {
                            @Override
                            public void cancel() {

                            }
                            @Override
                            public void select(RadioSelectDialog.Item item) {
                                mBinding.isShowEmoji.setRightText(item.description);
                                Config.setIsShowEmoji(item.id == 0);
                            }
                        });
        radioSelectDialog.show();
    }

    private String getVoiceType() {
        if (Config.sVoiceType == SpUtilValueConstants.NORMAL) {
            return getString(R.string.normal);
        } else if (Config.sVoiceType == SpUtilValueConstants.TREBLE) {
            return getString(R.string.treble);
        } else if (Config.sVoiceType == SpUtilValueConstants.BASS) {
            return getString(R.string.bass);
        } else {
            return getString(R.string.normal);
        }
    }
}