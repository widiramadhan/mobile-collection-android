package sfi.mobile.collection.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import sfi.mobile.collection.R;
import sfi.mobile.collection.app.AppController;
import sfi.mobile.collection.fragment.DashboardTabPriority;
import sfi.mobile.collection.model.TaskList;

public class TaskListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<TaskList> itemList;

    public TaskListAdapter(Activity activity, List<TaskList> itemList) {
        this.activity = activity;
        this.itemList = itemList;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_tasklist_layout, null);

        TextView contract_id = (TextView) convertView.findViewById(R.id.contract_id);
        TextView customer_name = (TextView) convertView.findViewById(R.id.customer_name);

        TaskList j = itemList.get(position);

        contract_id.setText(j.getContractID());
        customer_name.setText(j.getCustomerName());

        return convertView;
    }
}
