package com.t4app.t4everandroid.Login.models;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

@Entity(tableName = "user")
public class User implements Serializable {

    @SerializedName("id")
    @PrimaryKey
    public int id;

    @SerializedName("name")
    public String name;

    @SerializedName("email")
    public String email;

    @SerializedName("email_verified_at")
    public String emailVerifiedAt;

    @SerializedName("rol")
    public String rol;

    @SerializedName("alias")
    public String alias;

    @SerializedName("age")
    public int age;

    @SerializedName("country")
    public String country;

    @SerializedName("language")
    public String language;

    @SerializedName(value ="avatar_url", alternate = {"avatar_path"})
    public String avatarUrl;

    @SerializedName("date_register")
    public String dateRegister;

    @SerializedName("last_login")
    public String lastLogin;


    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof User)) return false;

        User user = (User) obj;
        return age == user.age &&
                id == user.id &&
                Objects.equals(name, user.name) &&
                Objects.equals(email, user.email) &&
                Objects.equals(rol, user.rol) &&
                Objects.equals(alias, user.alias) &&
                Objects.equals(country, user.country) &&
                Objects.equals(language, user.language) &&
                Objects.equals(emailVerifiedAt, user.emailVerifiedAt) &&
                Objects.equals(avatarUrl, user.avatarUrl);
    }


    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getDateRegister() {
        return dateRegister;
    }

    public void setDateRegister(String dateRegister) {
        this.dateRegister = dateRegister;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getEmailVerifiedAt() {
        return emailVerifiedAt;
    }

    public void setEmailVerifiedAt(String emailVerifiedAt) {
        this.emailVerifiedAt = emailVerifiedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
