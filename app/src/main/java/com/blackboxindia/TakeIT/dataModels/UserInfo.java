package com.blackboxindia.TakeIT.dataModels;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import java.util.Date;

public class UserInfo {

    String uID;
    String authKey;

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


    public boolean newUser(String password){
        /**
         * Todo:
         * add new user
         *
         *  set authKey
         *  set uID
         */
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
}
