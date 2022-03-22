package com.albert.selectmenu;

import android.app.Dialog;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 选择菜单
 *
 * @author albert
 * @date 2022/1/20 14:42
 * @mail 416392758@@gmail.com
 * @since v1
 */
public class SelectMenuDialog extends DialogFragment {

    public static void show(FragmentManager fragmentManager,
                            SelectMenuDialogInput inputData,
                            SelectMenuDialogCallback menuCallback) {
        String s = "SelectMenuDialog";
        Fragment fragment = fragmentManager.findFragmentByTag(s);
        if (fragment instanceof SelectMenuDialog) {
            ((SelectMenuDialog) fragment).dismiss();
        }
        newInstance(inputData, menuCallback)
                .show(fragmentManager, s);
    }

    public static SelectMenuDialog newInstance(SelectMenuDialogInput inputData, SelectMenuDialogCallback menuCallback) {
        Bundle arguments = new Bundle();
        arguments.putParcelable("inputData", inputData);
        SelectMenuDialog selectMenuDialog = new SelectMenuDialog();
        selectMenuDialog.setArguments(arguments);
        selectMenuDialog.menuCallback = menuCallback;
        return selectMenuDialog;
    }

    private SelectMenuDialogCallback menuCallback;

    private SelectMenuDialogInput inputData;

    private LinearLayout mainLayout;

    private EditText searchInput;

    private RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        Objects.requireNonNull(arguments, "arguments==null");
        inputData = arguments.getParcelable("inputData");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.picker_with_filter_dialog, container, false);
        mainLayout = view.findViewById(R.id.main_layout);
        searchInput = view.findViewById(R.id.searchInput);
        recyclerView = view.findViewById(R.id.recyclerView);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(),
                DividerItemDecoration.VERTICAL);
        Drawable drawable = ContextCompat.getDrawable(requireContext(), R.drawable.select_menu_list_divider);
        Objects.requireNonNull(drawable, "drawable=null");
        dividerItemDecoration.setDrawable(drawable);
        recyclerView.addItemDecoration(dividerItemDecoration);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateRecyclerView();
            }
        });
        updateRecyclerView();
        if (inputData.isSearchable()) {
            searchInput.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(inputData.getSearchInputHint())) {
                searchInput.setHint(inputData.getSearchInputHint());
            }
        } else {
            searchInput.setVisibility(View.GONE);
        }
    }


    private void updateRecyclerView() {
        String query = searchInput.getText().toString();
        List<DataItem> dataItemList = new ArrayList<>();
        if (TextUtils.isEmpty(query)) {
            dataItemList.addAll(inputData.getDataItems());
        } else {
            for (DataItem item : inputData.getDataItems()) {
                if (item.getText().contains(query)) {
                    dataItemList.add(item);
                }
            }
        }
        DataItemAdapter dataItemAdapter = new DataItemAdapter(dataItemList,
                inputData.getSelectedItem(),
                searchInput.getText().toString(),
                item -> {
                    if (menuCallback != null) {
                        menuCallback.onCallback(item.getIndex(), item.getText());
                        dismiss();
                    }
                });
        recyclerView.setAdapter(dataItemAdapter);
        if (inputData.getSelectedItem() != null) {
            int position = dataItemAdapter.computePosition(inputData.getSelectedItem());
            if (position > -1) {
                recyclerView.scrollToPosition(position);
            }
        }
    }

    private void computeWindSize() {
        Display display = requireActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        int barHeight = getStatusBarHeight();

        int yPosition = inputData.getYPosition();
        int viewHeight = inputData.getSelectViewHeight();

        int downAvailableHeight = outMetrics.heightPixels - (yPosition + viewHeight);
        int upAvailableHeight = yPosition - barHeight;

        int margin = getResources().getDimensionPixelOffset(R.dimen.select_menu_normal_margin);

        int measuredHeight;
        if (inputData.getDataItems().size() < 20) {
            int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            mainLayout.measure(inputData.getSelectViewWidth(), h);
            measuredHeight = mainLayout.getMeasuredHeight();
        } else {
            measuredHeight = Math.max(upAvailableHeight, downAvailableHeight);
        }

        int dx = inputData.getXPosition();
        int dy;
        // 默认下面展示下拉菜单
        int dHeight;

        if (measuredHeight <= downAvailableHeight || (downAvailableHeight > upAvailableHeight)) {
            // 从下面展示
            dHeight = Math.min(measuredHeight, downAvailableHeight) - margin;
            dy = yPosition + viewHeight;
        } else {
            // 上面空间大于下面空间
            dHeight = upAvailableHeight - margin;
            dy = yPosition - dHeight;
        }
        dy = dy - barHeight;

        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.x = dx;
                layoutParams.y = dy;
                layoutParams.height = dHeight;
                layoutParams.width = inputData.getSelectViewWidth();
                window.setAttributes(layoutParams);
                window.setBackgroundDrawableResource(R.drawable.select_menu_bg);
                View decorView = window.getDecorView();
                if (decorView != null) {
                    decorView.setPadding(0, 0, 0, 0);
                }
                window.setGravity(Gravity.LEFT | Gravity.TOP);
            }
            dialog.setCanceledOnTouchOutside(true);
        }
    }

    public int getStatusBarHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        //应用区域
        Rect outRect1 = new Rect();
        requireActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect1);
        return dm.heightPixels - outRect1.height();  //状态栏高度=屏幕高度-应用区域高度
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        computeWindSize();
    }

    @Override
    public void onStart() {
        super.onStart();
        computeWindSize();
    }
}
