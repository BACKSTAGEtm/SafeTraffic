package by.backstagedevteam.safetraffic;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

public class SearchExemple extends AppCompatActivity
            implements SearchView.OnQueryTextListener {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu_search, menu);

            MenuItem searchItem = menu.findItem(R.id.search);
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setOnQueryTextListener(this);

            return true;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            // User pressed the search button
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            // User changed the text
            return false;
        }
    }
