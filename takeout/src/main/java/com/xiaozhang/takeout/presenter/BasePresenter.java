package com.xiaozhang.takeout.presenter;

import com.xiaozhang.takeout.model.bean.ResponseInfo;
import com.xiaozhang.takeout.presenter.net.ResponseInfoApi;
import com.xiaozhang.takeout.util.Constant;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by zhangxiao on 2018/3/5.
 */

public abstract class BasePresenter {

    public ResponseInfoApi responseInfoApi;
    private HashMap<String,String> errorMap = new HashMap<>();

    public BasePresenter(){
        //1,初始化retrofit2
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //2.指定此对象需要发送的网络请求
        //完整的url地址,get post请求方式,请求参数,接收请求结果(xml,json)
        responseInfoApi = retrofit.create(ResponseInfoApi.class);

        errorMap.put("1","没有最新数据");
        errorMap.put("2","服务器忙");
        errorMap.put("3","服务器异常");
    }
    //同步    okhttp
    //异步    框架开启了子线程,回调方法(成功,失败)
    class CallBackAdapter implements Callback<ResponseInfo>{
        @Override
        public void onResponse(Call<ResponseInfo> call, Response<ResponseInfo> response) {
            //请求成功
            //body就是服务器返回数据的具体内容,json串
            ResponseInfo body = response.body();
            if (body==null){
                onFailure(call,new RuntimeException(errorMap.get(3)));
                return;
            }
            String code = body.getCode();
            if (code.equals("0")){
                //请求成功,不同的模块,json的结构不一样,所以无法再父类中统一处理,一定要在子类中解析
                String json = body.getData();
                parseJson(json);
            }else{
                String errorMessage = errorMap.get(code);
                //所有的异常,都在onFailure中处理
                onFailure(call,new RuntimeException(errorMessage));
            }
        }

        @Override
        public void onFailure(Call<ResponseInfo> call, Throwable t) {
            if (t instanceof RuntimeException){
                //请求失败
                String message = t.getMessage();
                showErrorMesssage(message);
            }
            showErrorMesssage("服务器忙,请稍后重试");
        }
    }

    /**
     * @param json  需要解析的json,父类不知道到子类具体的json结构,所以留给子类解析
     */
    protected abstract void parseJson(String json);

    /**
     * @param message   错误的描述
     *                  此方法留给子类具体实现(不同的开发者处理异常的方式不一样(谈对话框,打吐司.......))
     */
    protected abstract void showErrorMesssage(String message) ;
}
