package sfi.mobile.collection.model;

public class Question {

    String contractID, customerName, mobilePhone, DaysOD, angsuran, Alamat, duedate;

    public String getContractID() {
        return contractID;
    }

    public void setContractID(String contractID) {
        this.contractID = contractID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getDaysOD() {
        return DaysOD;
    }

    public void setDaysOD(String daysOD) {
        DaysOD = daysOD;
    }

    public String getAngsuran() {
        return angsuran;
    }

    public void setAngsuran(String angsuran) {
        this.angsuran = angsuran;
    }

    public String getAlamat() {
        return Alamat;
    }

    public void setAlamat(String alamat) {
        Alamat = alamat;
    }

    public String getDuedate() {
        return duedate;
    }

    public void setDuedate(String duedate) {
        this.duedate = duedate;
    }
}
