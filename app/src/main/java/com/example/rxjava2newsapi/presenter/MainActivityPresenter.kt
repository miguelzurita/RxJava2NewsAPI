package com.example.rxjava2newsapi.presenter

import android.content.Context
import android.support.v7.widget.SearchView
import android.util.Log
import com.example.rxjava2newsapi.news.model.Article
import com.example.rxjava2newsapi.news.model.TopHeadlines
import com.example.rxjava2newsapi.services.RetrofitClient
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivityPresenter(private val context: Context) : Presenter {

    private var retrofitClient: RetrofitClient
    private lateinit var userKeyWordInput: String


    // RxJava related fields
    lateinit var topHeadlinesObservable: Observable<TopHeadlines>
    lateinit var compositeDisposable: CompositeDisposable

    lateinit var articleList: ArrayList<Article>

    init {
        //When the app is launched of course the user input is empty.
        userKeyWordInput = ""
        //CompositeDisposable is needed to avoid memory leaks
        compositeDisposable = CompositeDisposable()

        articleList = ArrayList()
        //Network request
        retrofitClient = RetrofitClient(context)
    }

    private fun subscribeObservableOfArticle() {
        articleList.clear()
        compositeDisposable.add(
            topHeadlinesObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    Observable.fromIterable(it.articles)
                }
                .subscribeWith(createArticleObserver())
        )
    }

    fun checkUserKeywordInput() {
        if (userKeyWordInput.isEmpty()) {
            queryTopHeadlines()
        } else {
            getKeyWordQuery(userKeyWordInput)
        }
    }

    fun onQueryTextListenerCallback(): SearchView.OnQueryTextListener {
        return object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(userInput: String?): Boolean {
                return checkQueryText(userInput)
            }

            override fun onQueryTextChange(userInput: String?): Boolean {
                return checkQueryText(userInput)
            }
        }
    }

    private fun checkQueryText(userInput: String?): Boolean {
        if (userInput != null && userInput.length > 1) {
            userKeyWordInput = userInput
            getKeyWordQuery(userInput)
        } else if (userInput != null && userInput == "") {
            userKeyWordInput = ""
            queryTopHeadlines()
        }
        return false
    }


    //Gets immediately triggered when user clicks on search icon and enters something
    private fun getKeyWordQuery(userKeywordInput: String) {
//        swipe_refresh.isRefreshing = true
        if (userKeywordInput != null && userKeywordInput.isNotEmpty()) {
            topHeadlinesObservable =
                retrofitClient.topHeadlinesEndpoint.getUserSearchInput(retrofitClient.newsApiConfig, userKeywordInput)
            subscribeObservableOfArticle()
        } else {
            queryTopHeadlines()
        }
    }


    private fun queryTopHeadlines() {
//        swipe_refresh.isRefreshing = true
        topHeadlinesObservable = retrofitClient.topHeadlinesEndpoint.getTopHeadlines("us", retrofitClient.newsApiConfig)
        subscribeObservableOfArticle()
    }

    private fun createArticleObserver(): DisposableObserver<Article> {
        return object : DisposableObserver<Article>() {
            override fun onNext(article: Article) {
                if (!articleList.contains(article)) {
                    articleList.add(article)
                }
            }

            override fun onComplete() {
                //showArticlesOnRecyclerView()
                //event comunicate to view
            }

            override fun onError(e: Throwable) {
                Log.e("createArticleObserver", "Article error: ${e.message}")
            }
        }
    }

    fun onDestroy() {
        compositeDisposable.clear()
    }


}