package com.example.foodapi

import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var spinner: Spinner
    private lateinit var rvMeals: RecyclerView

    private val client = AsyncHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinner = findViewById(R.id.spinnerCategories)
        rvMeals = findViewById(R.id.rvMeals)

        rvMeals.layoutManager = LinearLayoutManager(this)

        loadCategories()
    }

    private fun loadCategories() {
        val url = "https://www.themealdb.com/api/json/v1/1/categories.php"

        client.get(url, object : AsyncHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, body: ByteArray?) {

                val json = JSONObject(String(body!!))
                val arr = json.getJSONArray("categories")

                val categories = ArrayList<String>()
                for (i in 0 until arr.length()) {
                    categories.add(arr.getJSONObject(i).getString("strCategory"))
                }

                val adapter = ArrayAdapter(
                    this@MainActivity,
                    R.layout.spinner_layout,
                    categories
                )

                adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                spinner.adapter = adapter

                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: android.view.View?,
                        position: Int,
                        id: Long
                    ) {
                        val selected = categories[position]
                        loadMealsByCategory(selected)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, body: ByteArray?, error: Throwable?) {
                Toast.makeText(this@MainActivity, "Failed to load categories", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadMealsByCategory(category: String) {
        val url = "https://www.themealdb.com/api/json/v1/1/filter.php?c=$category"

        client.get(url, object : AsyncHttpResponseHandler() {

            override fun onSuccess(code: Int, headers: Array<Header>?, body: ByteArray?) {

                val mealList = mutableListOf<Meal>()
                val json = JSONObject(String(body!!))
                val arr = json.getJSONArray("meals")

                for (i in 0 until arr.length()) {
                    val mealName = arr.getJSONObject(i).getString("strMeal")
                    fetchMealDetails(mealName, mealList)
                }
            }

            override fun onFailure(code: Int, headers: Array<Header>?, body: ByteArray?, error: Throwable?) {
                Toast.makeText(this@MainActivity, "Failed to load meals", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchMealDetails(mealName: String, mealList: MutableList<Meal>) {
        val url = "https://www.themealdb.com/api/json/v1/1/search.php?s=$mealName"

        client.get(url, object : AsyncHttpResponseHandler() {

            override fun onSuccess(code: Int, headers: Array<Header>?, body: ByteArray?) {

                val json = JSONObject(String(body!!))
                val arr = json.getJSONArray("meals")
                val m = arr.getJSONObject(0)

                val meal = Meal(
                    name = m.getString("strMeal"),
                    origin = m.getString("strArea"),
                    thumbnail = m.getString("strMealThumb")
                )

                mealList.add(meal)
                rvMeals.adapter = MealAdapter(mealList)
            }

            override fun onFailure(code: Int, headers: Array<Header>?, body: ByteArray?, error: Throwable?) {
                Toast.makeText(this@MainActivity, "Failed to load meal details", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
