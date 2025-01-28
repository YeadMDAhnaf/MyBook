package ViewHolder;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.yead.mybook.R;

import Model.NotificationModel;

public class NotificationViewHolder extends RecyclerView.ViewHolder {

    private TextView buyerName;

    public NotificationViewHolder(View itemView) {
        super(itemView);
        buyerName = itemView.findViewById(R.id.txtbuni);
    }

    public void bind(NotificationModel model) {
        buyerName.setText("Got a message from " + model.getBunbuni());
    }
}
