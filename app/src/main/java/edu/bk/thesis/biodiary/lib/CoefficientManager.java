package edu.bk.thesis.biodiary.lib;

import android.content.SharedPreferences;
import android.content.Context;
import android.app.Activity;
import android.provider.Settings;

/**
 * Created by L on 2018/04/03.
 */

public class CoefficientManager {

    public void create (Context mcontext){
        SharedPreferences sharedPreferences = mcontext.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("face",0.5f);
        editor.putFloat("voice",0.5f);
    }

    public int checkExist(Context mcontext){
        SharedPreferences sharedPreferences = mcontext.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
        double face = (double) sharedPreferences.getFloat("face",-1.0f);
        double voice= (double) sharedPreferences.getFloat("voice",-1.0f);
        System.out.println(face+" "+voice);
        int result=0;
        if (face==-1) {
            result+=0;
        }
        else{
            result+=10;
        }
        if (voice==-1) {
            result+=0;
        }
        else{
            result+=1;
        }
        return result;
    }

    public void save(Context mcontext,double face, double voice) throws Exception{
        SharedPreferences sharedPreferences = mcontext.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("face",(float) face);
        editor.apply();
        editor.putFloat("voice",(float) voice);
        editor.apply();
    }

    public double loadFace(Context mcontext) throws  Exception{
        int check=checkExist(mcontext);
        SharedPreferences sharedPreferences = mcontext.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
        if (check/10==1){
            double face = sharedPreferences.getFloat("face",0.5f);
            return face;
        }
        else{
            System.out.println("kill me please");
            create(mcontext);
            double face = sharedPreferences.getFloat("face",0.5f);
            return face;
        }
    }

    public double loadVoice(Context mcontext) throws  Exception{
        int check=checkExist(mcontext);
        SharedPreferences sharedPreferences = mcontext.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
        if (check%10==1){
            double voice = sharedPreferences.getFloat("voice",0.5f);
            return voice;
        }
        else{
            create(mcontext);
            double voice = sharedPreferences.getFloat("voice",0.5f);
            return voice;
        }
    }

}
