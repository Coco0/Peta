package com.example.vincent.projetpeta;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.etsy.android.grid.util.DynamicHeightImageView;

import java.util.List;

public class UserAdaptater extends ArrayAdapter<User> {
	private final LayoutInflater mLayoutInflater;
	private Context mContext;
	private List<User> users;

	public UserAdaptater(Context c, int resource, List<User> users) {
		super(c, resource, users);
		this.mContext = c;
		this.mLayoutInflater = LayoutInflater.from(mContext);
		this.users = users;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;

		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.activity_row, parent, false);
			holder = new ViewHolder();
			holder.image = (DynamicHeightImageView) convertView.findViewById(R.id.imgView);
			holder.n = (CardView) convertView.findViewById(R.id.cardView);
			holder.name = (TextView) convertView.findViewById(R.id.txtView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final User user = users.get(position);
		holder.image.setHeightRatio(1.0);
		Glide.with(mContext).load(user.getPathPP()).into(holder.image);
		holder.name.setText(user.getPrenom());

		return convertView;
	}

	static class ViewHolder {
		DynamicHeightImageView image;
		TextView name;
		CardView n;
	}
}