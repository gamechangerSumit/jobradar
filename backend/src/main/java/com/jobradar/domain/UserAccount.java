package com.jobradar.domain;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
@Entity @Table(name="user_accounts")
public class UserAccount {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true,length=254) private String email;
 @Column(name="full_name",nullable=false,length=120) private String fullName;
 @Column(name="password_hash",nullable=false,length=100) private String passwordHash;
 @Column(name="created_at",nullable=false) private OffsetDateTime createdAt;
 protected UserAccount(){}
 public UserAccount(String email,String fullName,String passwordHash){this.email=email;this.fullName=fullName;this.passwordHash=passwordHash;}
 @PrePersist void create(){createdAt=OffsetDateTime.now();}
 public Long getId(){return id;} public String getEmail(){return email;} public String getFullName(){return fullName;} public String getPasswordHash(){return passwordHash;}
}