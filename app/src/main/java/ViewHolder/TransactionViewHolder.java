package ViewHolder;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.yead.mybook.R;
import Model.TransactionModel;

public class TransactionViewHolder extends RecyclerView.ViewHolder {
    private final TextView txtProductName, txtProductBkash, txtAmountPaid;

    public TransactionViewHolder(@NonNull View itemView) {
        super(itemView);
        txtProductName = itemView.findViewById(R.id.txtProductName);
        txtProductBkash = itemView.findViewById(R.id.txtProductBkash);
        txtAmountPaid = itemView.findViewById(R.id.txtAmountPaid);
    }

    public void bind(TransactionModel transaction) {
        txtProductName.setText(transaction.getProductName());
        txtProductBkash.setText(transaction.getProductBkash());
        txtAmountPaid.setText("Paid Amount: " + transaction.getAmountPaid() + " Taka");
    }
}
