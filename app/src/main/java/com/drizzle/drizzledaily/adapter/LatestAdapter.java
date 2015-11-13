package com.drizzle.drizzledaily.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.drizzle.drizzledaily.R;
import com.drizzle.drizzledaily.bean.BaseListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页适配器
 */
public class LatestAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private List<BaseListItem> baseListItemList = new ArrayList<>();

    public LatestAdapter(Context mContext, List<BaseListItem> baseListItemList) {
        this.mContext = mContext;
        this.baseListItemList = baseListItemList;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return baseListItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return baseListItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewtype = getItemViewType(position);
        BaseListItem listItem = baseListItemList.get(position);
        ViewHolder1 viewHolder1;
        ViewHolder2 viewHolder2;
        if (convertView == null) {
            switch (viewtype) {
                case 0://type为日期
                    convertView = inflater.inflate(R.layout.base_date_item, null);
                    viewHolder1 = new ViewHolder1();
                    viewHolder1.itemDate = (TextView) convertView.findViewById(R.id.base_date_item_text);
                    convertView.setTag(viewHolder1);
                    break;
                case 1://type为内容
                    convertView = inflater.inflate(R.layout.base_list_item, null);
                    viewHolder2 = new ViewHolder2();
                    viewHolder2.itemTitle = (TextView) convertView.findViewById(R.id.base_item_title);
                    viewHolder2.itemImg = (ImageView) convertView.findViewById(R.id.base_item_img);
                    convertView.setTag(viewHolder2);
                    break;
                default:
                    break;
            }
        } else {
            switch (viewtype) {
                case 0://type为日期
                    viewHolder1 = (ViewHolder1) convertView.getTag();
                    viewHolder1.itemDate.setText(listItem.getDate());
                    break;
                case 1:
                    viewHolder2 = (ViewHolder2) convertView.getTag();
                    viewHolder2.itemTitle.setText(listItem.getTitle());
                    Glide.with(mContext).load(listItem.getImgUrl())
                            .centerCrop().error(R.mipmap.place_img)
                            .crossFade().into(viewHolder2.itemImg);
                    break;
                default:
                    break;
            }
        }
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return baseListItemList.get(position).getViewType();
    }

    class ViewHolder2 {
        public TextView itemTitle;
        public ImageView itemImg;
    }

    class ViewHolder1 {
        public TextView itemDate;
    }
}
