package com.microlending.model;

import java.sql.Date;

public class Member {
    private int id;
    private int circleId;
    private String name;
    private String phone;
    private Date joinDate;
    private int payoutOrder;
    private boolean hasReceivedPayout;

    public Member() {}

    public Member(int id, int circleId, String name, String phone, Date joinDate,
                   int payoutOrder, boolean hasReceivedPayout) {
        this.id = id;
        this.circleId = circleId;
        this.name = name;
        this.phone = phone;
        this.joinDate = joinDate;
        this.payoutOrder = payoutOrder;
        this.hasReceivedPayout = hasReceivedPayout;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCircleId() { return circleId; }
    public void setCircleId(int circleId) { this.circleId = circleId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Date getJoinDate() { return joinDate; }
    public void setJoinDate(Date joinDate) { this.joinDate = joinDate; }

    public int getPayoutOrder() { return payoutOrder; }
    public void setPayoutOrder(int payoutOrder) { this.payoutOrder = payoutOrder; }

    public boolean isHasReceivedPayout() { return hasReceivedPayout; }
    public void setHasReceivedPayout(boolean hasReceivedPayout) { this.hasReceivedPayout = hasReceivedPayout; }

    @Override
    public String toString() {
        return name;
    }
}
