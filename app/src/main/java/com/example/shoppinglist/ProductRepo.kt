package com.example.shoppinglist

import android.content.Context

//creates repository that handles queries from ProductDao and connects them to the database
class ProductRepo(context: Context) {
    private val productDAO: ProductDao

    init {
        val database = AppDatabase.getDatabase(context)
        productDAO = database!!.productDao()
    }

    suspend fun getAllProducts(): List<Product> {
        return productDAO.getAllProducts()
    }

    suspend fun insertProduct(product: Product) {
        productDAO.insertProduct(product)
    }

    suspend fun deleteProduct(product: Product) {
        productDAO.deleteProduct(product)
    }

    suspend fun deleteAllProducts() {
        productDAO.deleteAllProducts()
    }

}