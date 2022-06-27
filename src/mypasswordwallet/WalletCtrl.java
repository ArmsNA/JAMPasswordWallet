package mypasswordwallet;

public class WalletCtrl {
    private final WalletModel walletModel;
    private final WalletView walletView;
    
    public WalletCtrl(){
        walletModel = new WalletModel();
        walletView = new WalletView(this);
        walletView.setVisible(true);
    }
    
    public void addNewLogin(int index){
        UserLogin newLogin = new UserLogin( 
                null, null, null, null);
        walletModel.getLogins().put(getMapSize(), newLogin);
        walletModel.addLoginToFile(index);
    }
    
    public UserLogin getLogin(int index){
        if(index >= 0 && index < getMapSize()){
            return walletModel.getLogins().get(index);
        }else{
            index = 0;
            return getLogin(index);
        }
    }
    
    public void updateLogin(){
        walletModel.getLogins().put(walletView.getLoginIndex(), 
            new UserLogin(walletView.getLoginTitleFieldContents(),
                walletView.getUserIDFieldContents(), 
                walletView.getPasswordFieldContents(),
                walletView.getURLFieldContents()));
         walletModel.updateLoginInFile();
    }
    
    public void overwriteMaster(){
        walletModel.overwriteMasterInFile(walletView.getNewMasterFieldContents());
    }
    
    public void overwriteHint(){
        walletModel.overwriteHintInFile(walletView.getNewHintFieldContents());
    }
    
    public void overwriteSalt(){
        walletModel.overwriteSaltInFile();
    }
    
    public void deleteLogin(){
        if(walletView.getLoginIndex() == walletModel.getFileRows()-1){
            walletModel.getLogins().remove(walletView.getLoginIndex());
            walletModel.updateLoginInFile();
        }else{
            walletModel.getLogins().remove(walletView.getLoginIndex());
            for(int i = 0; i < getFileSize()-1; i++){
                if(!walletModel.getLogins().containsKey(i)){
                    walletModel.getLogins().putIfAbsent(i, walletModel.getLogins().get(i+1));
                    walletModel.getLogins().remove(i+1);
                }
            }
            walletModel.updateLoginInFile();
        }  
    }
    
    public int getFileSize(){
        return walletModel.getFileRows();
    }
    
    public int getMapSize(){
        return walletModel.getLogins().size();
    }
    
    public String getMasterHash(){
        return walletModel.getMasterHash();
    }
    
    public int getMasterKeyLength(){
        return walletModel.getKeyLength();
    }
    
    public String getMasterHashAlgorithm(){
        return walletModel.getHashAlgorithm();
    }
    
    public byte[] getMasterSalt(){
        return walletModel.getSalt();
    }
    
    public int getMasterIterations(){
        return walletModel.getIterations();
    }
    
    public String getHint(){
        return walletModel.getHint();
    }
    
    public boolean isLoginNullOrEmpty() {
        return ((getLogin(walletView.getLoginIndex())).getLoginTitle().equals("null") || getLogin(walletView.getLoginIndex()).getLoginTitle().isEmpty())
                || (getLogin(walletView.getLoginIndex()).getUserID().equals("null") || getLogin(walletView.getLoginIndex()).getUserID().isEmpty())
                || (getLogin(walletView.getLoginIndex()).getPassHash().equals("null") || getLogin(walletView.getLoginIndex()).getPassHash().isEmpty()
                || (getLogin(walletView.getLoginIndex()).getUrl().equals("null") || getLogin(walletView.getLoginIndex()).getUrl().isEmpty()));
    }
}
