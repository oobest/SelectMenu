package com.example.myapplication.menu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

/**
 * @author albert
 * @date 2022/1/21 9:28
 * @mail 416392758@@gmail.com
 * @since v1
 */
public class DataItemAdapter extends RecyclerView.Adapter<DataItemAdapter.ItemViewHolder> {

    private final List<DataItem> dataList;

    private DataItem selectItem;

    public interface Callback {
        void onClick(DataItem item);
    }

    private final Callback mCallback;

    private final String markString;

    /**
     * @param dataList   数据列表
     * @param selectItem 默认选中内容
     * @param markString 需要标记的内容
     * @param mCallback  回调接口实现
     */
    public DataItemAdapter(@NonNull List<DataItem> dataList, DataItem selectItem, String markString, @NonNull Callback mCallback) {
        this.dataList = dataList;
        this.selectItem = selectItem;
        this.markString = markString;
        this.mCallback = mCallback;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.select_item_single, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        DataItem item = dataList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    /**
     * 计算在dataList中的位置
     *
     * @param dataItem
     * @return
     */
    private int computePosition(DataItem dataItem) {
        int position = -1;
        int index = dataItem.getIndex();
        for (int i = 0; i < dataList.size(); i++) {
            if (index == dataList.get(i).getIndex()) {
                position = i;
            }
        }
        return position;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }

        void bind(DataItem item) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DataItem oldSelect = selectItem;
                    selectItem = item;
                    if (oldSelect != null) {
                        int oldSelectPosition = computePosition(oldSelect);
                        if (oldSelectPosition > -1) {
                            notifyItemChanged(oldSelectPosition);
                        }
                    }
                    itemView.setSelected(true);
                    mCallback.onClick(item);
                }
            });
            boolean selected = false;
            if (selectItem != null) {
                selected = selectItem.getIndex() == item.getIndex();
            }
            itemView.setSelected(selected);
            textView.setText(TextViewMarkUtils.format(item.getText(), markString));
        }
    }
}
