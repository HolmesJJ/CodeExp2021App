package com.example.codeexp2021app.ui.widget.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.codeexp2021app.R;
import com.example.codeexp2021app.listener.OnMultiClickListener;

/**
 * @author chenzhaojie
 * @date 2019/1/23
 */
public class SeekBarDialog extends BaseDialog {

    private TextView mTvTitle;
    private TextView mTvRemark;
    private TextView mTvTips;
    /**
     * mTvNumber 当前值
     */
    private TextView mTvNumber;
    /**
     * mTvStartNumber 最小值
     */
    private TextView mTvStartNumber;
    /**
     * mTvEndNumber 最大值
     */
    private TextView mTvEndNumber;
    private TextView mTvCancel;
    private TextView mTvConfirm;
    private SeekBar mSbSlider;
    private DialogEventListener mDialogEventListener;

    private String mTitle, mRemark, mUnit, mTips;
    private int mStart, mEnd, mCurrent;

    public SeekBarDialog(@NonNull Context context) {

        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.createDialogView(savedInstanceState, R.layout.dialog_seekbar);
    }

    @Override
    protected void initView() {

        mTvTitle = findViewById(R.id.tv_title);
        mTvRemark = findViewById(R.id.tv_remark);
        mTvNumber = findViewById(R.id.tv_number);
        mTvStartNumber = findViewById(R.id.tv_start);
        mTvEndNumber = findViewById(R.id.tv_end);
        mTvCancel = findViewById(R.id.tv_cancel);
        mTvConfirm = findViewById(R.id.tv_confirm);
        mSbSlider = findViewById(R.id.sb_slider);
        mTvTips = findViewById(R.id.tv_dialog_tips);
        mTvTitle.setText(mTitle);
        mTvRemark.setText(mRemark);
        mTvNumber.setText(mCurrent + mUnit);
        mTvStartNumber.setText(mStart + "");
        mTvEndNumber.setText(mEnd + "");
        mSbSlider.setMax(mEnd - mStart);
        mSbSlider.setProgress(mCurrent - mStart);
        mSbSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                mCurrent = progress + mStart;
                mTvNumber.setText(mCurrent + mUnit);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if (!TextUtils.isEmpty(mTips)) {
            mTvTips.setVisibility(View.VISIBLE);
            mTvTips.setText(mTips);
        }

        mTvConfirm.setOnClickListener(new OnMultiClickListener() {

            @Override
            public void onMultiClick(View v) {

                if (mDialogEventListener != null) {
                    mDialogEventListener.confirm(mCurrent);
                }
                dismiss();
            }
        });
        mTvCancel.setOnClickListener(new OnMultiClickListener() {

            @Override
            public void onMultiClick(View v) {

                if (mDialogEventListener != null) {
                    mDialogEventListener.cancel();
                }
                dismiss();
            }
        });
    }

    public SeekBarDialog setDialogTitle(String title) {

        this.mTitle = title;
        return this;
    }

    public SeekBarDialog setDialogTitle(int resId) {

        this.mTitle = mContext.getString(resId);
        return this;
    }

    public SeekBarDialog setRemark(String remark) {

        this.mRemark = remark;
        return this;
    }

    public SeekBarDialog setRemark(int resId) {

        this.mRemark = mContext.getString(resId);
        return this;
    }

    public SeekBarDialog setTips(String tips) {

        this.mTips = tips;
        return this;
    }

    public SeekBarDialog setTips(int resId) {

        this.mTips = mContext.getString(resId);
        return this;
    }

    /**
     * 设置值的单位
     *
     * @param unit String，例如s,min,h
     *
     * @return 当前对象
     */
    public SeekBarDialog setUnit(String unit) {

        this.mUnit = unit;
        return this;
    }

    public SeekBarDialog setUnit(int resId) {

        this.mUnit = mContext.getString(resId);
        return this;
    }

    /**
     * 设置可供选择的值的范围
     *
     * @param start 开始值
     * @param end   结束值
     *
     * @return 当前对象
     */
    public SeekBarDialog setRange(int start, int end) {

        this.mStart = start;
        this.mEnd = end;
        return this;
    }

    /**
     * 设置当前值
     *
     * @param current int
     *
     * @return 当前对象
     */
    public SeekBarDialog setCurrent(int current) {

        this.mCurrent = current;
        return this;
    }

    public SeekBarDialog setDialogEventListener(DialogEventListener dialogEventListener) {

        this.mDialogEventListener = dialogEventListener;
        return this;
    }

    public interface DialogEventListener {

        /**
         * 点击确定按钮后的回调
         *
         * @param progress 选择值
         */
        void confirm(int progress);

        /**
         * 点击取消按钮后的回调
         */
        void cancel();
    }
}
