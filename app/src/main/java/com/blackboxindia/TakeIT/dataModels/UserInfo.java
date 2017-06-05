package com.blackboxindia.TakeIT.dataModels;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.blackboxindia.TakeIT.Network.networkMethods;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;

public class UserInfo {

    String uID;
    //String authKey;

    String name;

    String email;
    String address;

    String phone;


    public UserInfo(){

    }

    public UserInfo(EditText nm, EditText em, EditText add, EditText ph)
    {
        try {
            name = nm.getText().toString().trim();
            email = em.getText().toString().trim();
            address = add.getText().toString().trim();
            phone = ph.getText().toString().trim();
        }
        catch (NullPointerException e)
        {
            Log.i("YOYO", e.toString());
        }
    }

    public boolean newUser(String password, Context context){
        /**
         * Todo:
         * add new user
         *
         *  set authKey
         *  set uID
         */
        networkMethods.newInterface anInterface = new networkMethods.newInterface() {
            @Override
            public void onResult(FirebaseAuth Auth) {
                Log.i("YOYO", "onResult: " + Auth.getCurrentUser().getDisplayName());
            }
        };

        networkMethods net = new networkMethods(context, anInterface);
        net.Create_Account(this,password);
        return true;
    }

    public Bundle validateEntry() {
        Bundle result = new Bundle();
        result.putAll(isIDValid(email));

        if(name != null && address!=null && phone!=null && result.getBoolean("ID"))
            result.putBoolean("allGood",true);
        else
            result.putBoolean("allGood",false);

        return result;
    }

    private Bundle isIDValid(String id) {
        Bundle bundle = new Bundle();
        if (id.length() < 4)
        {
            bundle.putString("errorID","Minimum 4 characters required.");
            bundle.putBoolean("ID", false);
            return bundle;
        }
        else if (id.contains("\"") || id.contains("\\") || id.contains("\'") || id.contains(";"))
        {
            bundle.putString("errorID","ID can\'t contain \", \\, \', or ;");
            bundle.putBoolean("ID", false);
            return bundle;
        }
        else if(!id.contains("@"))
        {
            bundle.putString("errorID","Not a valid email format.");
            bundle.putBoolean("ID", false);
            return bundle;
        }
        else
        {
            bundle.putBoolean("ID", true);
            return bundle;
        }
    }


    public void setuID(String uID) {
        this.uID = uID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getuID() {
        return uID;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

//    public String getAuthKey() {
//        return authKey;
//    }

}
