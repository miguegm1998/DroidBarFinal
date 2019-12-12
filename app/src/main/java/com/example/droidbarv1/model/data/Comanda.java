package com.example.droidbarv1.model.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Comanda implements Parcelable {
    private long id;
    private long id_employee;
    private long id_product;
    private long id_ticket;
    private int units;
    private double price;
    private int served;

    public Comanda() {
        this(0, 4, 0, 0, 0, 0.0, 0);
    }

    public Comanda(long id, long id_employee, long id_product, long id_ticket, int units, double price, int served) {
        this.id = id;
        this.id_employee = id_employee;
        this.id_product = id_product;
        this.id_ticket = id_ticket;
        this.units = units;
        this.price = price;
        this.served = served;
    }

    protected Comanda(Parcel in) {
        id = in.readLong();
        id_employee = in.readLong();
        id_product = in.readLong();
        id_ticket = in.readLong();
        units = in.readInt();
        price = in.readDouble();
        served = in.readInt();
    }

    public static final Creator<Comanda> CREATOR = new Creator<Comanda>() {
        @Override
        public Comanda createFromParcel(Parcel in) {
            return new Comanda(in);
        }

        @Override
        public Comanda[] newArray(int size) {
            return new Comanda[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId_employee() {
        return id_employee;
    }

    public void setId_employee(long id_employee) {
        this.id_employee = id_employee;
    }

    public long getId_product() {
        return id_product;
    }

    public void setId_product(long id_product) {
        this.id_product = id_product;
    }

    public long getId_ticket() {
        return id_ticket;
    }

    public void setId_ticket(long id_ticket) {
        this.id_ticket = id_ticket;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int isServed() {
        return served;
    }

    public void setServed(int served) {
        this.served = served;
    }

    @Override
    public String toString() {
        return "Comanda{" +
                "id=" + id +
                ", id_employee=" + id_employee +
                ", id_product=" + id_product +
                ", id_ticket=" + id_ticket +
                ", units=" + units +
                ", price=" + price +
                ", served=" + served +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(id_employee);
        dest.writeLong(id_product);
        dest.writeLong(id_ticket);
        dest.writeInt(units);
        dest.writeDouble(price);
        dest.writeInt(served);
    }
}
