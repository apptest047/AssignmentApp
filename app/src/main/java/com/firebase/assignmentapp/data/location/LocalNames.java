
package com.firebase.assignmentapp.data.location;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LocalNames {

    @SerializedName("he")
    @Expose
    private String he;
    @SerializedName("ja")
    @Expose
    private String ja;
    @SerializedName("kn")
    @Expose
    private String kn;

    public String getHe() {
        return he;
    }

    public void setHe(String he) {
        this.he = he;
    }

    public String getJa() {
        return ja;
    }

    public void setJa(String ja) {
        this.ja = ja;
    }

    public String getKn() {
        return kn;
    }

    public void setKn(String kn) {
        this.kn = kn;
    }

}
