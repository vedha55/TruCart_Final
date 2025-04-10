package com.example.trucart.adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.trucart.R;
import com.example.trucart.databinding.ItemCartBinding;
import com.example.trucart.databinding.QuantityDialogBinding;
import com.example.trucart.model.Product;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    Context context;
    ArrayList<Product> products;
    CartListener cartListener;
    Cart cart;

    public interface CartListener {
        void onQuantityChanged();
    }

    public CartAdapter(Context context, ArrayList<Product> products, CartListener cartListener) {
        this.context = context;
        this.products = products;
        this.cartListener = cartListener;
        cart = TinyCartHelper.getCart();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = products.get(position);
        Glide.with(context)
                .load(product.getImage())
                .into(holder.binding.image);

        holder.binding.name.setText(product.getName());
        holder.binding.price.setText("INR " + product.getPrice());
        holder.binding.quantity.setText(product.getQuantity() + " item(s)");

        // Quantity update logic
        holder.itemView.setOnClickListener(view -> {
            QuantityDialogBinding quantityDialogBinding = QuantityDialogBinding.inflate(LayoutInflater.from(context));
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setView(quantityDialogBinding.getRoot())
                    .create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));

            quantityDialogBinding.productName.setText(product.getName());
            quantityDialogBinding.productStock.setText("Stock: " + product.getStock());
            quantityDialogBinding.quantity.setText(String.valueOf(product.getQuantity()));

            quantityDialogBinding.plusBtn.setOnClickListener(v -> {
                int quantity = product.getQuantity();
                if (quantity < product.getStock()) {
                    product.setQuantity(++quantity);
                    quantityDialogBinding.quantity.setText(String.valueOf(quantity));
                    notifyDataSetChanged();
                    cart.updateItem(product, product.getQuantity());
                    cartListener.onQuantityChanged();
                } else {
                    Toast.makeText(context, "Max stock available: " + product.getStock(), Toast.LENGTH_SHORT).show();
                }
            });

            quantityDialogBinding.minusBtn.setOnClickListener(v -> {
                int quantity = product.getQuantity();
                if (quantity > 1) {
                    product.setQuantity(--quantity);
                    quantityDialogBinding.quantity.setText(String.valueOf(quantity));
                    notifyDataSetChanged();
                    cart.updateItem(product, product.getQuantity());
                    cartListener.onQuantityChanged();
                }
            });

            quantityDialogBinding.saveBtn.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        });

        // Remove item from cart
        holder.binding.removeBtn.setOnClickListener(v -> {
            cart.removeItem(product);
            products.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, products.size());
            cartListener.onQuantityChanged();
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        ItemCartBinding binding;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemCartBinding.bind(itemView);
        }
    }
}
