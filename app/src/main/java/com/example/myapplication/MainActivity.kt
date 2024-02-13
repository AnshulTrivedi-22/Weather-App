package com.example.myapplication

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.SearchView.OnQueryTextListener
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.example.myapplication.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var items:MutableList<ItemsOfGrid>
    private var city: String = "jaipur"
    private lateinit  var gridAdapter: AdapterForTheGrid

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)


        setContentView(binding.root)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        items = mutableListOf<ItemsOfGrid>(
            ItemsOfGrid("random1", "random2", R.drawable.humidity),
            ItemsOfGrid("random1", "random2", R.drawable.wind)
        )
        items.add(ItemsOfGrid("random1", "random2", R.drawable.white_cloud))
        items.add(ItemsOfGrid("random1", "random2", R.drawable.sunrise))
        items.add(ItemsOfGrid("random1", "random2", R.drawable.sunset))
        gridAdapter = AdapterForTheGrid(items)
        binding.rcView.apply {
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)

            adapter = gridAdapter

        }
        val snaph: SnapHelper = LinearSnapHelper()

        snaph.attachToRecyclerView(binding.rcView)
        snaph.getSnapPosition(binding.rcView)

        fatchWeatherData()
        searchCity()
    }
    private fun searchCity()
    {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?):Boolean {
                if (query != null) {
                    city = query
                    fatchWeatherData()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }
    private fun SnapHelper.getSnapPosition(recyclerView: RecyclerView): Int {

        val layoutManager = recyclerView.layoutManager ?: return RecyclerView.NO_POSITION
        val snapView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
        return layoutManager.getPosition(snapView)
    }
    private fun fatchWeatherData()
    {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response = retrofit.getWeatherDetails(city, "cb52a2f93eb5f202c2ae6a03660ac048", "metric")
        response.enqueue(object: Callback<WeatherApp>{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                //Log.d("onResponse","position = $pos")
                val responseStr:WeatherApp? = response.body()
                if(response.isSuccessful && responseStr != null)
                {
                    val temp = responseStr?.main?.temp
                    val maxT = responseStr?.main?.temp_max
                    val minT = responseStr?. main?.temp_min

                    binding.temperature.text = "$temp °C"
                    binding.MaxTemp.text = "Max: $maxT °C"
                    binding.MinTemp.text = "Min: $minT °C"
                    var condition = "${responseStr?.weather?.firstOrNull()?.main?:"Unknown"}"
                    binding.textView5.text = condition
                    val currentDate = LocalDate.now()
                    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
                    val formattedDate = currentDate.format(formatter)
                    binding.Today.text = "$formattedDate"
                    binding.Whichday.text = getCurrentDayAsString()
                    var lat = responseStr.coord.lat
                    var lon = responseStr.coord.lon

                    binding.textView2.text = city
                    setValuesOfTheRecycleView(items, responseStr)
                    changeWeatherConditions(condition)
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {

            }

        })

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentDayAsString(): String {
        val currentDay = LocalDate.now().dayOfWeek
        return currentDay.toString()
    }

    private fun setValuesOfTheRecycleView(items:MutableList<ItemsOfGrid>, responses:WeatherApp?): Unit
    {
        responses?.let {
            items[0] = items[0].copy(
                update = "${responses?.main?.humidity}%",
                des = "Humidity"
            )
            items[1] = items[1].copy(
                update = "${responses?.wind?.speed} m/s",
                des = "Wind Speed"
            )
            items[2] = items[2].copy(
                update = "${responses?.weather?.firstOrNull()?.main ?: "Unknown"}",
                des = "Condition"
            )
            items[3] = items[3].copy(
                update = "${changeTime(responses.sys.sunrise.toLong())}",
                des = "SunRise"
            )
            items[4] = items[4].copy(
                update = "${changeTime(responses.sys.sunset.toLong())}",
                des = "SunSet"
            )
        }
        gridAdapter.notifyItemChanged(0)
    }
    private fun changeTime(timeStamp: Long): String
    {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timeStamp * 1000)))
    }
    private fun changeWeatherConditions(conditions:String)
    {
        when(conditions){
            "Haze", "Mist","Fog","Partly Clouds", "Overcast","Clouds","Smoke" ->
            {
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloudy)
            }
            "Clear", "Sky", "Sunny", "Clear Sky" ->
            {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Light Rain", "Drizzle","Moderate Rain", "Showers", "Heavy Rain", "Rain" ->
            {
                binding.root.setBackgroundResource(R.drawable.rainyweather)
                binding.lottieAnimationView.setAnimation(R.raw.rainy)
            }
            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard","Snow" ->
            {
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snowfall)
            }
            else ->
            {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()

    }
}