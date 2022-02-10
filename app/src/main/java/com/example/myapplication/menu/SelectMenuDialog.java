package com.example.myapplication.menu;

import android.app.Dialog;
import android.graphics.Point;
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

import com.example.myapplication.R;

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
        Bundle arguments = requireArguments();
        inputData = arguments.getParcelable("inputData");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.picker_with_filter_dialog, container, false);
        mainLayout = view.findViewById(R.id.main_layout);
        searchInput = view.findViewById(R.id.searchInput);
        recyclerView = view.findViewById(R.id.recyclerView);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(),
                DividerItemDecoration.VERTICAL);
        Drawable drawable = ContextCompat.getDrawable(requireContext(), R.drawable.recycler_list_divider);
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
    }

    @Override
    public void onStart() {
        super.onStart();
        Display display = requireActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        DisplayMetrics real = new DisplayMetrics();
        display.getRealMetrics(real);

        int barHeight = real.heightPixels - outMetrics.heightPixels;

        int displayHeight = outMetrics.heightPixels;
        int yPosition = inputData.getYPosition();
        int viewHeight = inputData.getSelectViewHeight();

        int bottomOffset = displayHeight - (yPosition + viewHeight);
        int availableMaxH = Math.max(bottomOffset, yPosition);
        int margin = getResources().getDimensionPixelOffset(R.dimen.normal_margin);
//        int margin = 0;

        int measuredHeight;
        if (inputData.getDataItems().size() < 20) {
            int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            mainLayout.measure(inputData.getSelectViewWidth(), h);
            measuredHeight = mainLayout.getMeasuredHeight();
        } else {
            measuredHeight = availableMaxH;
        }

        int dx = inputData.getXPosition();
        int dy = yPosition + viewHeight;
        // 默认下面展示下拉菜单
        int dHeight = bottomOffset - margin;
        // 1、优先下面展示下拉菜单，如果没有多余的空间，
        // 2、判断上面有更多空间（h下面<h上面），如果有则上面展示，如果没有，还是下面展示
        // 3、判断内容h,与可用空间的大小
        if (measuredHeight <= dHeight) {
            // 从下面展示
            dHeight = measuredHeight;
        } else if (yPosition > bottomOffset) {
            // 上面空间大于下面空间
            dHeight = yPosition - barHeight;
            if (measuredHeight <= dHeight) {
                dHeight = measuredHeight;
            }
            dy = yPosition - dHeight;
        }

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

                window.setBackgroundDrawableResource(R.drawable.ic_white_rectangle_radius2);
                window.setGravity(Gravity.LEFT | Gravity.TOP);
            }
            dialog.setCanceledOnTouchOutside(true);
        }
    }
}
