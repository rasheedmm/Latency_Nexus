package com.live.latency_nexus.Models;

public class MyPosts {
    private String postid, prdname,prddesc,prdqnt,publisher,img1,img2,stock,mrp,offer;
    private String payment;

    public MyPosts(String postid, String prdname, String prddesc, String prdqnt, String publisher, String img1, String img2, String stock, String mrp, String offer, String payment) {
        this.postid = postid;
        this.prdname = prdname;
        this.prddesc = prddesc;
        this.prdqnt = prdqnt;
        this.publisher = publisher;
        this.img1 = img1;
        this.img2 = img2;
        this.offer = offer;
        this.mrp = mrp;
        this.payment=payment;
        this.stock = stock;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPrdname() {
        return prdname;
    }

    public void setPrdname(String prdname) {
        this.prdname = prdname;
    }

    public String getPrddesc() {
        return prddesc;
    }

    public void setPrddesc(String prddesc) {
        this.prddesc = prddesc;
    }

    public String getPrdqnt() {
        return prdqnt;
    }

    public void setPrdqnt(String prdqnt) {
        this.prdqnt = prdqnt;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getImg1() {
        return img1;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }

    public String getImg2() {
        return img2;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }



    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }


    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }
    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public MyPosts() {
    }

}

