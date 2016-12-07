package tv.sportssidekick.sportssidekick.model;
/**
 * Created by Djordje Krutil on 6.12.2016..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class UserInfo {

    String firstName;
    String lastName;
    String nicName;
    String phone;

    public UserInfo(String firstName, String lastName, String nicName, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.nicName = nicName;
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNicName() {
        return nicName;
    }

    public void setNicName(String nicName) {
        this.nicName = nicName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
