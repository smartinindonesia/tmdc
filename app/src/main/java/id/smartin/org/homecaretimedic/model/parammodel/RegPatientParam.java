package id.smartin.org.homecaretimedic.model.parammodel;

import com.google.gson.annotations.SerializedName;

import id.smartin.org.homecaretimedic.model.User;

/**
 * Created by Hafid on 1/10/2018.
 */

public class RegPatientParam {
    @SerializedName("id")
    private Long id;
    @SerializedName("name")
    private String name;
    @SerializedName("religion")
    private String religion;
    @SerializedName("gender")
    private String gender;
    @SerializedName("height")
    private double height;
    @SerializedName("weight")
    private double weight;
    @SerializedName("dateOfBirth")
    private String dateOfBirth;
    @SerializedName("placeOfBirth")
    private String placeOfBirth;
    @SerializedName("idAppUser")
    private Long idAppUser;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public Long getIdAppUser() {
        return idAppUser;
    }

    public void setIdAppUser(Long idAppUser) {
        this.idAppUser = idAppUser;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", religion='" + religion + '\'' +
                ", gender='" + gender + '\'' +
                ", height=" + height +
                ", weight=" + weight +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", placeOfBirth='" + placeOfBirth + '\'' +
                ", idAppUser=" + idAppUser +
                '}';
    }
}
