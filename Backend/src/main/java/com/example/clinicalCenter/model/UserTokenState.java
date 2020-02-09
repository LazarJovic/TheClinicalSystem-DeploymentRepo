package com.example.clinicalCenter.model;

public class UserTokenState {

    private String accessToken;
    private Long expiresIn;
    private Long userId;
    private String userType;
    private boolean passwordChanged;
    private int numLogin;
    // private boolean firstLogin;

    public UserTokenState() {
        this.accessToken = null;
        this.expiresIn = null;
        this.setUserType(null);
        this.passwordChanged = false;
        this.numLogin = 0;
        // this.firstLogin = true;
    }

    public UserTokenState(String accessToken, long expiresIn, Long userId, String userType, boolean passwordChanged, int numLogins) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.userId = userId;
        this.setUserType(userType);
        this.passwordChanged = passwordChanged;
        this.numLogin = numLogins;
        //this.numLogin = 0;
        // this.firstLogin = true;
    }

    public int getNumLogin() {
        return numLogin;
    }

    public void setNumLogin(int numLogin) {
        this.numLogin = numLogin;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public boolean isPasswordChanged() {
        return passwordChanged;
    }

    public void setPasswordChanged(boolean passwordChanged) {
        this.passwordChanged = passwordChanged;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
