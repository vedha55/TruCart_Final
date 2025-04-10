package com.example.trucart.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.trucart.R;
import com.example.trucart.adapters.CategoryAdapter;
import com.example.trucart.adapters.ProductAdapter;
import com.example.trucart.databinding.ActivityMainBinding;
import com.example.trucart.model.Category;
import com.example.trucart.model.Product;
import com.example.trucart.utils.Constants;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.mancj.materialsearchbar.MaterialSearchBar;

import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    CategoryAdapter categoryAdapter;
    ArrayList<Category> categories;
    ProductAdapter productAdapter;
    ArrayList<Product> products;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialSearchBar searchBar;
    private ImageView profileIcon;
    private SwipeRefreshLayout swipeRefreshLayout; // Added SwipeRefreshLayout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize views
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        profileIcon = findViewById(R.id.profileIcon);
        searchBar = findViewById(R.id.searchBar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout); // Reference SwipeRefreshLayout

        // Set profile icon instead of default navigation icon
        searchBar.setNavButtonEnabled(true);
        searchBar.setMenuIcon(R.drawable.ic_profile);

        // Open navigation drawer when clicking the profile icon
        profileIcon.setOnClickListener(view -> drawerLayout.open());

        // Handle navigation drawer item clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                drawerLayout.closeDrawers();
            } else if (id == R.id.nav_edit_profile) {
                startActivity(new Intent(MainActivity.this, EditProfileActivity.class));
            } else if (id == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
            return true;
        });

        // Pull-to-Refresh Functionality
        swipeRefreshLayout.setOnRefreshListener(() -> {
            refreshData(); // Method to refresh data
        });

        // Initialize lists
        initCategories();
        initProducts();
        initSlider();
    }

    // Refresh the data when user swipes down
    private void refreshData() {
        categories.clear();
        products.clear();
        getCategories();
        getRecentProducts();
        getRecentOffers();

        // Stop refresh animation after data loads
        swipeRefreshLayout.setRefreshing(false);
    }

    private void initSlider() {
        getRecentOffers();
    }

    void initCategories() {
        categories = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(this, categories);
        getCategories();
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        binding.categoriesList.setLayoutManager(layoutManager);
        binding.categoriesList.setAdapter(categoryAdapter);
    }

    void getCategories() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, Constants.GET_CATEGORIES_URL,
                response -> {
                    try {
                        Log.e("err", response);
                        JSONObject mainObj = new JSONObject(response);
                        if (mainObj.getString("status").equals("success")) {
                            JSONArray categoriesArray = mainObj.getJSONArray("categories");
                            for (int i = 0; i < categoriesArray.length(); i++) {
                                JSONObject object = categoriesArray.getJSONObject(i);
                                Category category = new Category(
                                        object.getString("name"),
                                        Constants.CATEGORIES_IMAGE_URL + object.getString("icon"),
                                        object.getString("color"),
                                        object.getString("brief"),
                                        object.getInt("id")
                                );
                                categories.add(category);
                            }
                            categoryAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {}
        );
        queue.add(request);
    }

    void getRecentProducts() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Constants.GET_PRODUCTS_URL + "?count=8";
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getString("status").equals("success")) {
                    JSONArray productsArray = object.getJSONArray("products");
                    for (int i = 0; i < productsArray.length(); i++) {
                        JSONObject childObj = productsArray.getJSONObject(i);
                        Product product = new Product(
                                childObj.getString("name"),
                                Constants.PRODUCTS_IMAGE_URL + childObj.getString("image"),
                                childObj.getString("status"),
                                childObj.getDouble("price"),
                                childObj.getDouble("price_discount"),
                                childObj.getInt("stock"),
                                childObj.getInt("id")
                        );
                        products.add(product);
                    }
                    productAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {});
        queue.add(request);
    }

    void getRecentOffers() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, Constants.GET_OFFERS_URL, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getString("status").equals("success")) {
                    JSONArray offerArray = object.getJSONArray("news_infos");
                    for (int i = 0; i < offerArray.length(); i++) {
                        JSONObject childObj = offerArray.getJSONObject(i);
                        binding.carousel.addData(
                                new CarouselItem(
                                        Constants.NEWS_IMAGE_URL + childObj.getString("image"),
                                        childObj.getString("title")
                                )
                        );
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {});
        queue.add(request);
    }

    void initProducts() {
        products = new ArrayList<>();
        productAdapter = new ProductAdapter(this, products);
        getRecentProducts();
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        binding.productList.setLayoutManager(layoutManager);
        binding.productList.setAdapter(productAdapter);
    }
}
