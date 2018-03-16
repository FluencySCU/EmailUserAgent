package cn.fluencycat.emailuseragent;

import android.content.Context;
import android.widget.Toast;

public class Tools {
    /**
     * 1.5s Toast
     * @param string
     * @param context
     */
    public static void showShortToast(String string,Context context){
        Toast.makeText(context, string,Toast.LENGTH_SHORT).show();
    }

}
