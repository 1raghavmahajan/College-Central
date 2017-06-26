package com.blackboxindia.TakeIT.dataModels;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import com.blackboxindia.TakeIT.Network.Interfaces.onLoginListener;
import com.blackboxindia.TakeIT.Network.NetworkMethods;
import com.blackboxindia.TakeIT.activities.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("WeakerAccess")
public class UserInfo implements Parcelable{

    //region Variables

    private String profileIMG;
    private String uID;
    private String name;
    private String email;
    private String address;
    private String phone;

    private ArrayList<String> userAdKeys;
    private String collegeName;

    //endregion

    //region Constructors

    public UserInfo(){
        uID = null;
        userAdKeys = new ArrayList<>();
    }

    protected UserInfo(Parcel in) {
        profileIMG = in.readString();
        uID = in.readString();
        name = in.readString();
        email = in.readString();
        address = in.readString();
        phone = in.readString();
        userAdKeys = in.createStringArrayList();
        collegeName = in.readString();
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    public UserInfo createCopy() {
        UserInfo userInfo = new UserInfo();
        userInfo.setData(name,email,address,phone);
        userInfo.setuID(uID);
        userInfo.setProfileIMG(profileIMG);
        for(String s:userAdKeys){
            userInfo.addUserAd(s);
        }
        if(collegeName!=null)
            userInfo.setCollegeName(collegeName);
        return userInfo;
    }

    //endregion

    public void setData(String name, String email, String address, String phone) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.phone = phone;
        collegeName = "IIT Indore";
    }

    public void newUser(String password, final Context context) {
        NetworkMethods net = new NetworkMethods(context);
        net.Create_Account(this,password, new onLoginListener() {
            @Override
            public void onSuccess(UserInfo userInfo) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    ((MainActivity) context).UpdateUI(userInfo,true,true);
                    ((MainActivity) context).createSnackbar("Account Created Successfully",Snackbar.LENGTH_LONG);
                }
            }

            @Override
            public void onFailure(Exception e) {
                if (e != null) {
                    if (e.getMessage().contains("network"))
                        Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void login(final String email, final String password, final Context context) {

        final ProgressDialog dialog = ProgressDialog.show(context, "Logging in..", "", true, false);
        NetworkMethods net = new NetworkMethods(context);
        net.Login(email, password, new onLoginListener() {
            @Override
            public void onSuccess(UserInfo userInfo) {
                dialog.cancel();
                UserCred userCred = new UserCred(email, password);
                userCred.save_cred(context);

                ((MainActivity) context).UpdateUI(userInfo, true, true);
            }

            @Override
            public void onFailure(Exception e) {
                if (e != null) {
                    dialog.cancel();
                    if (e.getMessage().contains("network")) {
                        Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show();
                    } else if (e.getMessage().contains("password") && e.getMessage().contains("invalid")) {
                        Toast.makeText(context, "Invalid Password.", Toast.LENGTH_SHORT).show();
                    } else if (e.getMessage().contains("no user record")) {
                        Toast.makeText(context, "Invalid Email ID.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void addUserAd(String userAdKey) {
        if(userAdKeys==null)
            userAdKeys = new ArrayList<>();
        userAdKeys.add(userAdKey);
    }

    public void removeUserAd(String userAdKey) {
        if(userAdKeys.contains(userAdKey))
            userAdKeys.remove(userAdKey);
    }

    public static void cacheUserDetails(UserInfo userInfo, Context context) {
        final String FILENAME = "profile_Img";

        SharedPreferences cache = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = cache.edit();

        String profileIMG = userInfo.getProfileIMG();
        if(profileIMG !=null) {
            Log.i("YOYO","write: "+profileIMG);

            FileOutputStream fos;
            try {
                fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
                byte[] bytes = profileIMG.getBytes("UTF-8");
                Log.i("YOYO",new String(bytes,"UTF-8"));
                fos.write(bytes);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //edit.putString("profileIMG", userInfo.getProfileIMG());
        edit.putString("uID", userInfo.getuID());
        edit.putString("name", userInfo.getName());
        edit.putString("email", userInfo.getEmail());
        edit.putString("address", userInfo.getAddress());
        edit.putString("phone", userInfo.getPhone());

        Set<String> UserAdKeys = new HashSet<>(userInfo.getUserAdKeys());

        edit.putStringSet("userAdKeys", UserAdKeys );

        if(userInfo.getCollegeName()!=null)
            edit.putString("collegeName", userInfo.getCollegeName());

        edit.apply();
    }

    public static UserInfo getCachedUserDetails(String uID, Context context) {
        final String FILENAME = "profile_Img";

        UserInfo userInfo = new UserInfo();

        SharedPreferences cache = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        String id = cache.getString("uID", null);
        if(id != null) {

            if(id.equals(uID)) {

                try {
                    FileInputStream fileInputStream = context.openFileInput(FILENAME);
                    byte[] bytes = new byte[1024*200];
                    //noinspection ResultOfMethodCallIgnored
                    fileInputStream.read(bytes);
                    String s = new String(bytes,"UTF-8");
                    Log.i("YOYO","read: "+s);
                    userInfo.setProfileIMG(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                userInfo.setuID(id);
                userInfo.setName(cache.getString("name", null));
                userInfo.setEmail(cache.getString("email", null));
                userInfo.setAddress(cache.getString("address", null));
                userInfo.setPhone(cache.getString("phone", null));

                Set<String> userAdKeys = cache.getStringSet("userAdKeys", null);
                ArrayList<String> keys;
                if (userAdKeys != null)
                    keys = new ArrayList<>(userAdKeys);
                else
                    keys = new ArrayList<>();

                userInfo.setUserAdKeys(keys);

                userInfo.setProfileIMG(cache.getString("collegeName", "IIT Indore"));

                return userInfo;
            }
        }
        return null;
    }

    //region Getters and Setters

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ArrayList<String> getUserAdKeys() {
        return userAdKeys;
    }

    public void setUserAdKeys(ArrayList<String> userAdKeys) {
        this.userAdKeys = userAdKeys;
    }


    public String getProfileIMG() {
        return profileIMG;
    }

    public void setProfileIMG(String profileIMG) {
        this.profileIMG = profileIMG;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(profileIMG);
        dest.writeString(uID);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(address);
        dest.writeString(phone);
        dest.writeStringList(userAdKeys);
        dest.writeString(collegeName);
    }

    //endregion

}
