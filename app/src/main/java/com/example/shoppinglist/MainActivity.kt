package com.example.shoppinglist

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var productRepository: ProductRepo
    private val mainScope = CoroutineScope(Dispatchers.Main)
    private val products = arrayListOf<Product>()
    private val productAdapter = ProductAdapter(products)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        productRepository = ProductRepo(this)
        initViews()
    }

    private fun initViews(){
        rvProducts.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvProducts.adapter = productAdapter
        rvProducts.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        createItemTouchHelper().attachToRecyclerView(rvProducts)
        getShoppingListFromDatabase()

        fab.setOnClickListener {
            addProduct()
        }
    }


    //creates field validation before inserting into the database
    private fun validator(): Boolean {
        val name = etName.text.toString()
        val amount = etAmount.text.toString()
        return if (name.isNotBlank() && amount.isNotBlank()) {
            true
        } else {
            Snackbar.make(rvProducts, "You must fill in all fields!", Snackbar.LENGTH_SHORT).show()
            false
        }
    }


    //add item to database
    private fun addProduct() {
        if (validator()) {
            mainScope.launch {
                //create object from text values
                val product = Product(
                    name = etName.text.toString(),
                    amount  = etAmount.text.toString()
                )

                withContext(Dispatchers.IO) {
                    productRepository.insertProduct(product)
                }
                //update shoppinglist
                getShoppingListFromDatabase()
            }
        }
    }

    //creates option on main menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    //remove shoppinglist from database
    private fun deleteShoppingList() {
        mainScope.launch {
            withContext(Dispatchers.IO) {
                productRepository.deleteAllProducts()
            }
            getShoppingListFromDatabase()
        }
    }

    //creates option for icon to delete current ArrayList
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete_shopping_list -> {
                deleteShoppingList()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getShoppingListFromDatabase() {
        CoroutineScope(Dispatchers.Main).launch {
            //get content from repository api call
            val shoppingList = withContext(Dispatchers.IO) {
                productRepository.getAllProducts()
            }
            //remove all current items
            this@MainActivity.products.clear()
            //add all new items
            this@MainActivity.products.addAll(shoppingList)
            //notify that a change has been made
            this@MainActivity.productAdapter.notifyDataSetChanged()
        }
    }



    private fun createItemTouchHelper(): ItemTouchHelper {

        val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            // Enables or Disables the ability to move items
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            // Callback triggered when an item is swiped
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val productToDelete = products[position]
                mainScope.launch {
                    withContext(Dispatchers.IO) {
                        //deletes item from ArrayList
                        productRepository.deleteProduct(productToDelete)
                    }
                    //updates shoppinglist
                    getShoppingListFromDatabase()
                }
            }
        }
        return ItemTouchHelper(callback)
    }

}
