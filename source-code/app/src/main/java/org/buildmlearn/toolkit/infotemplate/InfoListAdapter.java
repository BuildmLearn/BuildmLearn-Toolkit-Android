package org.buildmlearn.toolkit.infotemplate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;

import java.util.ArrayList;

public class InfoListAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<InfoModel> mList;

	public InfoListAdapter(Context context) {
		mList = new ArrayList<InfoModel>();
		mContext = context;
	}

	public void setList(ArrayList<InfoModel> list) {
		mList = list;
		this.notifyDataSetChanged();

	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {

			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(R.layout.info_template_app_list_item, parent,
					false);

			holder = new ViewHolder();
			holder.mTvInfoObject = (TextView) convertView
					.findViewById(R.id.tv_info_object);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.mTvInfoObject.setTag(R.id.tv_info_object);
		holder.mTvInfoObject.setText(mList.get(position).getInfo_object());

		return convertView;
	}

	public class ViewHolder {
		private TextView mTvInfoObject;
	}

}
