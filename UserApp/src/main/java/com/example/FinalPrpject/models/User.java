package com.example.FinalPrpject.models;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "users")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean isBanned;

    @Column(nullable = false)
    private boolean isPushEnabled = true;

    @Column(nullable = false)
    private boolean isEmailEnabled = true;

    @ElementCollection
    private List<String> warnings = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "user_followers",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "follower_id"))
    private Set<User> followers = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "user_following",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "following_id"))
    private Set<User> following = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "user_blocks",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "blocked_id"))
    private Set<User> blockedUsers = new HashSet<>();


    public User() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<User> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<User> followers) {
        this.followers = followers;
    }

    public Set<User> getFollowing() {
        return following;
    }

    public void setFollowing(Set<User> following) {
        this.following = following;
    }

    public Set<User> getBlockedUsers() {
        return blockedUsers;
    }

    public void setBlockedUsers(Set<User> blockedUsers) {
        this.blockedUsers = blockedUsers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }

    public boolean isPushEnabled() {
        return isPushEnabled;
    }

    public void setPushEnabled(boolean isPushEnabled) {
        this.isPushEnabled = isPushEnabled;
    }

    public boolean isEmailEnabled() {
        return isEmailEnabled;
    }

    public void setEmailEnabled(boolean isEmailEnabled) {
        this.isEmailEnabled = isEmailEnabled;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    private User(Builder builder) {
        this.username = builder.username;
        this.email = builder.email;
        this.password = builder.password;
        this.isBanned = builder.isBanned;
        this.isPushEnabled = builder.isPushEnabled;
        this.isEmailEnabled = builder.isEmailEnabled;
        this.followers = builder.followers;
        this.following = builder.following;
        this.blockedUsers = builder.blockedUsers;
        this.warnings = builder.warnings;
    }

    public static class Builder {
        private String username;
        private String email;
        private String password;
        private boolean isBanned;
        private boolean isPushEnabled = true;
        private boolean isEmailEnabled = true;
        private List<String> warnings = new ArrayList<>();
        private Set<User> followers = new HashSet<>();
        private Set<User> following = new HashSet<>();
        private Set<User> blockedUsers = new HashSet<>();


        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder isBanned(boolean isBanned) {
            this.isBanned = isBanned;
            return this;
        }

        public Builder isPushEnabled(boolean isPushEnabled) {
            this.isPushEnabled = isPushEnabled;
            return this;
        }

        public Builder isEmailEnabled(boolean isEmailEnabled) {
            this.isEmailEnabled = isEmailEnabled;
            return this;
        }

        public Builder followers(Set<User> followers) {
            this.followers = followers;
            return this;
        }

        public Builder following(Set<User> following) {
            this.following = following;
            return this;
        }

        public Builder blockedUsers(Set<User> blockedUsers) {
            this.blockedUsers = blockedUsers;
            return this;
        }

        public Builder warnings(List<String> warnings) {
            this.warnings = warnings;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

}