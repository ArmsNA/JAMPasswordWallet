package mypasswordwallet;

public class UserLogin {
    private String loginTitle;
    private String userID;
    private String passHash;
    private String url;

    public UserLogin(String loginTitle, String userID, String passHash, String url) {
        this.loginTitle = loginTitle;
        this.userID = userID;
        this.passHash = passHash;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getLoginTitle() {
        return loginTitle;
    }

    public String getUserID() {
        return userID;
    }

    public String getPassHash() {
        return passHash;
    }
    
    public void setUrl(String url){
        this.url = url;
    }
    
    public void setPassword(String password){
        this.passHash = password;
    }
    
    public void setLoginTitle(String loginTitle){
        this.loginTitle = loginTitle;
    }
    
    public void setUserID(String userID){
        this.userID = userID;
    }
    
    @Override
    public String toString() {
        return loginTitle + ", " + userID 
                + ", " + passHash + ", " + url;
    }
}
