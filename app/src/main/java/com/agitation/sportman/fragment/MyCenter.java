package com.agitation.sportman.fragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.agitation.sportman.R;
import com.agitation.sportman.activity.UserInfoEdit;
import com.agitation.sportman.activity.Course_Appointment;
import com.agitation.sportman.activity.Login;
import com.agitation.sportman.utils.DataHolder;
import com.agitation.sportman.utils.FastBlur;
import com.agitation.sportman.widget.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by fanwl on 2015/10/25.
 */
public class MyCenter extends Fragment implements View.OnClickListener {

    private View rootView;
    private CircleImageView mycenter_head;
    private ImageView second_bg;
    private TextView userName;
    private DataHolder dataHolder;
    public static final int EDIT_PHOTO = 125;
    private ImageLoader imageLoader;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) parent.removeView(rootView);
        } else {
            rootView = inflater.inflate(R.layout.my_center, container, false);
            initVarible();
            initView();
        }
        return rootView;
    }

    private void initVarible() {
        dataHolder=DataHolder.getInstance();
        imageLoader = ImageLoader.getInstance();
    }

    private void initView() {
        userName = (TextView)rootView.findViewById(R.id.center_userName);
        mycenter_head = (CircleImageView) rootView.findViewById(R.id.mycenter_head);
        second_bg = (ImageView)rootView.findViewById(R.id.second_bg);
        mycenter_head.setOnClickListener(this);

        if (dataHolder.isLogin()){
            String headImg = dataHolder.getImageProfix() + dataHolder.getUserData().get("head")+"";
            setCenterHead(headImg);
            userName.setText(dataHolder.getUserData().get("name") + "");
        }else {
            Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.default_head);
            setHeadImage(bitmap);
            userName.setText("未登录");
        }


        rootView.findViewById(R.id.mycenter_bt_course).setOnClickListener(this);
        rootView.findViewById(R.id.mycenter_bt_match).setOnClickListener(this);
        second_bg.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                second_bg.getViewTreeObserver().removeOnPreDrawListener(this);
                second_bg.buildDrawingCache();

                Bitmap bmp = second_bg.getDrawingCache();
                blur(bmp, second_bg);
                return true;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==EDIT_PHOTO){
            setHeadImage(dataHolder.getCenterHeadBit());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.mycenter_head:
                if (dataHolder.getUserData()!=null){
                    startActivityForResult(new Intent(getActivity(), UserInfoEdit.class), 100);
                }else {
                    startActivity(new Intent(getActivity(), Login.class));
                }
                break;
            case R.id.mycenter_bt_course:
                startActivity(new Intent(getActivity(), Course_Appointment.class));
                break;
            case R.id.mycenter_bt_match:
                startActivity(new Intent(getActivity(), Course_Appointment.class));
                break;

        }
    }



    public void setCenterHead(String url){

        imageLoader.loadImage(url, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                setHeadImage(loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }

    private void setHeadImage(Bitmap loadedImage){
        mycenter_head.setImageBitmap(loadedImage);
        second_bg.setImageBitmap(loadedImage);
    }

    /*
    给我的中心背景图像进行模糊处理
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void blur(Bitmap bkg, View view) {
        long startMs = System.currentTimeMillis();
        float scaleFactor = 8;
        float radius = 2;

        Bitmap overlay = Bitmap.createBitmap(
                (int) (view.getMeasuredWidth() / scaleFactor),
                (int) (view.getMeasuredHeight() / scaleFactor),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft() / scaleFactor, -view.getTop()
                / scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);

        overlay = FastBlur.doBlur(overlay, (int) radius, true);
        if (view instanceof ImageView){
            ((ImageView)view).setImageBitmap(overlay);
        }else {
            view.setBackground(new BitmapDrawable(getResources(), overlay));
        }
        System.out.println(System.currentTimeMillis() - startMs + "ms");
    }
}
