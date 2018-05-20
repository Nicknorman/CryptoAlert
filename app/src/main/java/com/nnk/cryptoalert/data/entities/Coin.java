package com.nnk.cryptoalert.data.entities;

/**
 * Created by Nick on 24-03-2018.
 */

public class Coin {
  private int id;
  private String imageUrl;
  private String name;
  private String coinName;
  private String fullName;
  private String algorithm;
  private String proofType;
  private int sortOrder;

  public Coin(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCoinName() {
    return coinName;
  }

  public void setCoinName(String coinName) {
    this.coinName = coinName;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getAlgorithm() {
    return algorithm;
  }

  public void setAlgorithm(String algorithm) {
    this.algorithm = algorithm;
  }

  public String getProofType() {
    return proofType;
  }

  public void setProofType(String proofType) {
    this.proofType = proofType;
  }

  public int getSortOrder() {
    return sortOrder;
  }

  public void setSortOrder(int sortOrder) {
    this.sortOrder = sortOrder;
  }
}
