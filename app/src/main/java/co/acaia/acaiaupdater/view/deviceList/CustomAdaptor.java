package co.acaia.acaiaupdater.view.deviceList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import co.acaia.acaiaupdater.R;

public class CustomAdaptor extends ArrayAdapter<DeviceModel> implements View.OnClickListener{

    private ArrayList<DeviceModel> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView deviceName;
    }

    public CustomAdaptor(ArrayList<DeviceModel> data, Context context) {
        super(context, R.layout.row_item, data);
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DeviceModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.deviceName=(TextView)convertView.findViewById(R.id.tv_device_name);
            viewHolder.deviceName.setText(dataModel.modelName);
            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }


        return convertView;
    }
}