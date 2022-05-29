package com.example.task61d;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class pay extends AppCompatActivity {
    //Server port for local testing
    private static final String BACKEND_URL = "http://10.0.2.2:8080/";
    TextView amountText;
    CardInputWidget cardInputWidget;
    Button payButton;
    private String paymentIntentClientSecret;
    private Stripe stripe;
    private OkHttpClient httpClient;
    static ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        amountText = findViewById(R.id.amount_id);
        amountText.setText("The amount you paid is "+String.valueOf(Global.money)+" Dollar");
        cardInputWidget = findViewById(R.id.cardInputWidget);
        payButton = findViewById(R.id.payButton);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Transaction in progress");
        progressDialog.setCancelable(false);
        httpClient = new OkHttpClient();
        //Initialize
        stripe = new Stripe(
                getApplicationContext(),
                Objects.requireNonNull("pk_test_51L0xb3GOfffUM9R3k0M1qXC4nLH7fPDPMfuZWnQArU2pv9UGfNq9NgoP1kInuYeP57xSw4NknOjvnIQVwvhXZzLX00s2MNSWGN")
        );
        //Click the Pay button to enter the payment process，And when
        // finished, jump to the feedback screen
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                startCheckout();
            }
        });
    }
    //Passing of payment information to the server
    private void startCheckout() {
        {
            MediaType mediaType = MediaType.get("application/json; charset=utf-8");
            double amount= Global.money*1000;
            Map<String,Object> payMap=new HashMap<>();
            Map<String,Object> itemMap=new HashMap<>();
            List<Map<String,Object>> itemList =new ArrayList<>();
            payMap.put("currency","INR");
            itemMap.put("id","photo_subscription");
            itemMap.put("amount",amount);
            itemList.add(itemMap);
            payMap.put("items",itemList);
            String json = new Gson().toJson(payMap);
            RequestBody body = RequestBody.create(json, mediaType);
            Request request = new Request.Builder()
                    .url(BACKEND_URL+ "create-payment-intent")
                    .post(body)
                    .build();
            httpClient.newCall(request)
                    .enqueue(new PayCallback(this));
        }
    }
    //Get the results returned by the server
    private static final class PayCallback implements Callback {
        @NonNull
        private final WeakReference<pay> activityRef;

        PayCallback(@NonNull pay activity) {
            activityRef = new WeakReference<>(activity);
        }
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            progressDialog.dismiss();
            final pay activity = activityRef.get();
            if (activity == null) {
                return;
            }
            activity.runOnUiThread(() ->
                    Toast.makeText(
                            activity, "Error:1 " + e.toString(), Toast.LENGTH_LONG
                    ).show()
            );
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull final Response response)
                throws IOException {
            final pay activity = activityRef.get();
            if (activity == null) {
                return;
            }
            if (!response.isSuccessful()) {
                activity.runOnUiThread(() ->
                        Toast.makeText(
                                activity, "Error:2 " + response.toString(), Toast.LENGTH_LONG
                        ).show()
                );
            } else {
                activity.onPaymentSuccess(response);
            }
        }
    }
            private void onPaymentSuccess ( @NonNull final Response response) throws
                IOException {
                    Gson gson = new Gson();
                    Type type = new TypeToken<Map<String, String>>() {
                    }.getType();
                    Map<String, String> responseMap = gson.fromJson(
                            Objects.requireNonNull(response.body()).string(),
                            type
                    );
                    paymentIntentClientSecret = responseMap.get("clientSecret");
                    PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
                    if (params != null) {
                        ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                                .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
                        stripe.confirmPayment(pay.this, confirmParams);
                    }
                    Log.i("TAG", "onPaymentSuccess: " + paymentIntentClientSecret);
                }


                protected void onActivityResult ( int requestCode, int resultCode,
                @Nullable Intent data){
                    super.onActivityResult(requestCode, resultCode, data);
                    // Handle the result of stripe.confirmPayment
                    stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
                }

                private final class PaymentResultCallback
                        implements ApiResultCallback<PaymentIntentResult> {
                    @NonNull
                    private final WeakReference<pay> activityRef;

                    PaymentResultCallback(@NonNull pay activity) {
                        activityRef = new WeakReference<>(activity);
                    }
                    @Override
                    public void onSuccess(@NonNull PaymentIntentResult result) {
                        progressDialog.dismiss();
                        final pay activity = activityRef.get();
                        if (activity == null) {
                            return;
                        }
                        PaymentIntent paymentIntent = result.getIntent();
                        PaymentIntent.Status status = paymentIntent.getStatus();
                        if (status == PaymentIntent.Status.Succeeded) {
                            Gson gson = new GsonBuilder().setPrettyPrinting().create();
                            Toast toast = Toast.makeText(activity, "Ordered Successful", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            Intent intent = new Intent(pay.this, Feedback.class);
                            startActivity(intent);
                            finish();
                        } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                            activity.displayAlert(
                                    "Payment failed",
                                    Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage()
                            );
                        }
                    }
                    @Override
                    public void onError(@NonNull Exception e) {
                        progressDialog.dismiss();
                        final pay activity = activityRef.get();
                        if (activity == null) {
                            return;
                        }
                        activity.displayAlert("Error3", e.toString());
                    }
                }
    private void displayAlert(@NonNull String title,
                              @Nullable String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message);
        builder.setPositiveButton("Ok", null);
        builder.create().show();
    }
}