package co.acaia.acaiaupdater.view.firmwarelList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

import co.acaia.acaiaupdater.R;
import co.acaia.acaiaupdater.view.deviceList.DeviceModel;
import co.acaia.communications.scaleService.gatt.Log;

public class CustomFirmwareAdaptor  extends ArrayAdapter<FirmwareModel> {
    private ArrayList<FirmwareModel> dataSet;
    Context mContext;

    // View lookup cache
    public static class ViewHolder {
        TextView title;
        TextView caption;
    }

    public CustomFirmwareAdaptor(ArrayList<FirmwareModel> data, Context context) {
        super(context,R.layout.row_item_firmware,data);
        this.mContext=context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        FirmwareModel dataModel = getItem(position);
        //Log.v("CustomFirmwareAdaptor", "set data "+dataModel.title);
        // Check if an existing view is being reused, otherwise inflate the  view
        CustomFirmwareAdaptor.ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new CustomFirmwareAdaptor.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item_firmware, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.tv_firmware_title);
            viewHolder.title.setText(dataModel.title);
            viewHolder.caption = (TextView) convertView.findViewById(R.id.tv_firmware_caption);
            viewHolder.caption.setText(dataModel.caption);
            convertView.setTag(viewHolder);
            return convertView;
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }
        return convertView;
    }

}