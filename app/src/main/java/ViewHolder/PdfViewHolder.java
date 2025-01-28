package ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yead.mybook.R;

public class PdfViewHolder extends RecyclerView.ViewHolder {
    public TextView txtPdfName;
    public Button viewButton;  // Reference the "View" button

    public PdfViewHolder(@NonNull View itemView) {
        super(itemView);
        txtPdfName = itemView.findViewById(R.id.pdf_name);
        viewButton = itemView.findViewById(R.id.view_pdf_button);  // Initialize the "View" button
    }
}
