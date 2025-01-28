package com.yead.mybook;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Properties;
import java.util.Random;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class VerifyTransaction extends AppCompatActivity {

    private EditText emailEditText, codeEditText;
    private Button sendCodeButton, verifyButton;
    private String generatedCode;
    private String name, imageUrl, productBkash, productID;
    private double amountPaid;
    private static final String ADMIN_EMAIL = "mybook9231@gmail.com";
    private static final String ADMIN_PASSWORD = "mlxz lgyb cqao oyqu";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_transaction);

        // Retrieve product details passed from TransactionActivity
        Intent intent = getIntent();
        productID = intent.getStringExtra("PID");
        name = intent.getStringExtra("PRODUCT_NAME");
        imageUrl = intent.getStringExtra("PRODUCT_IMAGE");
        productBkash = intent.getStringExtra("PRODUCT_BKASH");
        amountPaid = intent.getDoubleExtra("AMOUNT_PAID", 0.0);

        emailEditText = findViewById(R.id.email_edit_text);
        codeEditText = findViewById(R.id.code_edit_text);
        sendCodeButton = findViewById(R.id.send_code_button);
        verifyButton = findViewById(R.id.verify_button);

        sendCodeButton.setOnClickListener(v -> sendVerificationCode());
        verifyButton.setOnClickListener(v -> verifyCode());
    }

    private void sendVerificationCode() {
        String userEmail = emailEditText.getText().toString().trim();
        if (userEmail.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a random 6-digit code
        generatedCode = String.format("%06d", new Random().nextInt(999999));

        new Thread(() -> {
            try {
                sendEmail(userEmail, generatedCode);
                runOnUiThread(() -> Toast.makeText(this, "Verification code sent to your email", Toast.LENGTH_LONG).show());
            } catch (Exception e) {
                Log.e("EmailError", "Error sending email", e);
                runOnUiThread(() -> Toast.makeText(this, "Failed to send email", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void sendEmail(String recipientEmail, String code) throws Exception {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(ADMIN_EMAIL, ADMIN_PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(ADMIN_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject("Your Verification Code");
        message.setText("Your verification code is: " + code);

        Transport.send(message);
    }

    private void verifyCode() {
        String enteredCode = codeEditText.getText().toString().trim();
        if (enteredCode.equals(generatedCode)) {
            Toast.makeText(this, "Payment Verified", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(VerifyTransaction.this, finalTransaction.class);
            intent.putExtra("PID", productID);
            intent.putExtra("PRODUCT_NAME", name);
            intent.putExtra("PRODUCT_IMAGE", imageUrl);
            intent.putExtra("PRODUCT_BKASH", productBkash);
            intent.putExtra("AMOUNT_PAID", amountPaid);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Invalid code, please try again", Toast.LENGTH_SHORT).show();
        }
    }
}
