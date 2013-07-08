package com.vosto.customer.favourites.activities;

import java.util.Arrays;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.*;
import com.vosto.customer.cart.vos.Cart;
import com.vosto.customer.cart.vos.CartItem;
import com.vosto.customer.favourites.ProductFavouriteListAdapter;
import com.vosto.customer.favourites.services.GetProductFavouriteService;
import com.vosto.customer.products.activities.ProductDetailsActivity;
import com.vosto.customer.products.services.GetProductsResult;
import com.vosto.customer.products.vos.ProductVo;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.utils.Constants;

import com.vosto.customer.R;
import com.vosto.customer.VostoBaseActivity;

import android.os.Bundle;
import android.util.Log;
import com.vosto.customer.utils.MoneyUtils;

import com.agimind.widget.SlideHolder;

public class ProductFavouritesActivity extends VostoBaseActivity implements OnRestReturn {

    private SlideHolder mSlideHolder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_favourites);

        mSlideHolder = (SlideHolder) findViewById(R.id.slideHolder);

        View toggleView = findViewById(R.id.menuButton);
        toggleView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mSlideHolder.toggle();
            }
        });

        SharedPreferences settings = getSharedPreferences("VostoPreferences", 0);
        if(!settings.getString("userToken", "").equals("") &&  settings.getString("userName", "user") != "user"){
            //User logged in:
            TextView nameOfUser = (TextView)findViewById(R.id.nameOfUser);
            nameOfUser.setText(settings.getString("userName", "user"));

        }else{
            //User not logged in:
        }

        fetchProducts();
    }

    private void fetchProducts() {
        new GetProductFavouriteService(this, this).execute();
    }

    @Override
    public void onRestReturn(RestResult result) {
        if(result == null){
            return;
        }

        if (result instanceof GetProductsResult) {
            ProductVo[] products = ((GetProductsResult) result).getProducts();
            Log.d(Constants.TAG, Arrays.asList(products).toString());

            //update products and update listview
            ListView list = (ListView)findViewById(R.id.lstProducts);
            list.setAdapter(new ProductFavouriteListAdapter(this, R.layout.product_favourtie_item_row, products));
        }

    }

    public void productSelected(View v){
        ProductVo selectedProduct = (ProductVo)v.getTag();
        Intent intent = new Intent(this, ProductDetailsActivity.class);
        intent.putExtra("product", selectedProduct);

//      TextView lblCategoryName = (TextView)findViewById(R.id.lblCategoryName);
//      intent.putExtra("categoryName", lblCategoryName.getText().toString());
//      intent.putExtra("store", this.store);
        startActivity(intent);
    }

    public void addToCartClicked(View v){
        Button button = (Button)v;
        ProductVo product = (ProductVo)button.getTag();
        Cart cart = getCart();

        // Check if a cart is open with another store, and block this item:
        if(cart.isOpen() && cart.getStore() != null && cart.getStore().getId() != product.getStore_id()){
            Log.d("STO", "cart store: " + cart.getStore().getId() + ", product store: " + product.getStore_id());
            this.showAlertDialog("Error", "You can only order from one store at a time.");
            return;
        }

        // cart.setStore(this.store);
        cart.addItem(new CartItem(product, 1));
        saveCart(cart);

        // send toast message
        CharSequence text = "Product was added to your cart.";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
        toast.show();

        updateBuyButton();
    }

    private void updateBuyButton(){
        TextView txtAmount = (TextView)findViewById(R.id.buy_button_price);
        TextView txtQuantity = (TextView)findViewById(R.id.buy_button_quantity);
        Cart cart = getCart();
        if(cart == null){
            Log.d("Cart", "Base activity returned a null cart!");
        }
        txtAmount.setText(MoneyUtils.getRandString(cart.getTotalPrice()));
        txtQuantity.setText(cart.getNumberOfItems() + " items");
    }

    public void productFavouritesClicked(View v) {
    	// We are already on the favourite products activity. Don't need to do anything.
    }

    public void storeFavouritesClicked(View v) {
        Intent intent = new Intent(this, StoreFavouritesActivity.class);
        startActivity(intent);
        finish();
    }

}
