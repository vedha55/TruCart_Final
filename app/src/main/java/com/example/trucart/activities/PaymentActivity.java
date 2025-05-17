package com.example.trucart.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;

import com.example.trucart.databinding.ActivityPaymentBinding;
import com.example.trucart.utils.Constants;

public class PaymentActivity extends AppCompatActivity {

    ActivityPaymentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String orderCode = getIntent().getStringExtra("orderCode");

        binding.webview.setMixedContentAllowed(true);
        binding.webview.loadUrl(Constants.PAYMENT_URL + orderCode);

        // Simple refresh logic
        binding.swipeRefresh.setOnRefreshListener(() -> binding.webview.reload());

        // Stop refresh after page load
        binding.webview.setListener(this, new im.delight.android.webview.AdvancedWebView.Listener() {
            @Override
            public void onPageStarted(String url, android.graphics.Bitmap favicon) {}

            @Override
            public void onPageFinished(String url) {
                binding.swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onPageError(int errorCode, String description, String failingUrl) {
                binding.swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {

            }

            public void onDownloadRequested(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {}

            @Override
            public void onExternalPageRequest(String url) {}
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
