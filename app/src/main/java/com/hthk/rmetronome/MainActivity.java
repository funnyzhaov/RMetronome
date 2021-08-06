package com.hthk.rmetronome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingMethod;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.databinding.PropertyChangeRegistry;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import com.hthk.rmetronome.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mainBinding;
    private ViewDataBinding viewDataBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //---------------ViewBinding-------------
        mainBinding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        //调用view
        mainBinding.title.setText("节拍器");


        //---------------DataBinding--------------
        viewDataBinding=DataBindingUtil.setContentView(this, R.layout.activity_main);
        //创建viewModel
        MainViewModel viewModel= new ViewModelProvider(this).get(MainViewModel.class);
        //指定生命周期所有者
        viewDataBinding.setLifecycleOwner(this);

        //---------------绑定额外的周期组件-----------

        MyLocationListener myLocationListener = new MyLocationListener(getLifecycle());
        viewDataBinding.getLifecycleOwner().getLifecycle().addObserver(myLocationListener);

        //异步操作后调用
        myLocationListener.enable();

        //创建观察者与关连
        Observer<String> nameObserver=new Observer<String>() {
            @Override
            public void onChanged(String s) {
                mainBinding.title.setText(s);
            }
        };
        viewModel.getName().observe(this,nameObserver);
        viewModel.getName().setValue("ASS");


        //快捷的写法
        viewModel.getName().observe(this,name->{
            // update UI
        });
    }


    //viewModel
    static class MainViewModel extends ViewModel{
        private MutableLiveData<String> mName;

        public MutableLiveData<String> getName() {
            if (mName==null){
                mName=new MutableLiveData<>();
            }
            return mName;
        }
    }

    //示例，位置生命周期观察者
    static class MyLocationListener implements LifecycleObserver {
        private boolean enabled = false;
        private Lifecycle lifecycle;
        public MyLocationListener(Lifecycle lifecycle) {
            this.lifecycle=lifecycle;
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        void start() {
            if (enabled) {
            }
        }

        public void enable() {
            enabled = true;
            if (lifecycle.getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                // connect if not connected
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        void stop() {
            // disconnect if connected
        }
    }


}