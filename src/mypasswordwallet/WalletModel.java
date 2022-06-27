package mypasswordwallet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.Map;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.List;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class WalletModel {

    private final File loginFile, masterFile, hintFile, saltFile;
    private String masterHash, hint, masterHashEncrypted;
    private byte[] salt;
    private final byte[] KEY, KEY_IV;
    private final Map<Integer, UserLogin> logins;
    private final String HASH_ALGORITHM, SALT_HASH_ALGORITHM, KEY_ALGORITHM, KEY_CIPHER;
    private final int ITERATIONS, KEY_LENGTH;

    public WalletModel() {
        logins = new HashMap<>();

        loginFile = new File("logins.txt");
        loginFile.setWritable(false);

        masterFile = new File("master.txt");
        masterFile.setWritable(false);

        hintFile = new File("hint.txt");
        hintFile.setWritable(false);

        saltFile = new File("salt.txt");
        saltFile.setWritable(false);

        HASH_ALGORITHM = "PBKDF2WithHmacSHA256";
        SALT_HASH_ALGORITHM = "SHA1PRNG";
        KEY_ALGORITHM = "AES";
        KEY_CIPHER = "AES/CBC/PKCS5PADDING";
        ITERATIONS = 1000;
        KEY_LENGTH = 512;
        KEY = new byte[]{(byte) 0xe4, 0x73, (byte) 0xd9,
            0x2a, (byte) 0xea, 0x3a, 0x69, 0x10, (byte) 0xc7, (byte) 0xa2, 0x08, 0x00, 0x2b,
            0x3d, 0x30, (byte) 0x9d};
        KEY_IV = new byte[]{(byte) 0x14, 0x7d, (byte) 0xdd,
            0x3d, (byte) 0xea, 0x3a, 0x69, 0x10, (byte) 0xc7, (byte) 0xaa, 0x19, 0x01, 0x2b,
            0x3d, 0x03, (byte) 0xd9};

        addExistingLoginsToMap();
        initalizeMasterHash();
        initalizeSalt();
        initalizeHint();
    }

    private String encrypt(byte[] key, byte[] initVector, String plaintext) throws InvalidKeyException, IllegalBlockSizeException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, BadPaddingException {
        IvParameterSpec iv = new IvParameterSpec(initVector);
        SecretKeySpec skeySpec = new SecretKeySpec(key, KEY_ALGORITHM);
        Cipher cipher = Cipher.getInstance(KEY_CIPHER);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(plaintext.getBytes("UTF-8"));
        String encodedString = Base64.getEncoder().encodeToString(encrypted);

        return encodedString;
    }

    private String decrypt(byte[] key, byte[] initVector, String ciphertext) throws InvalidKeyException, IllegalBlockSizeException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, BadPaddingException {
        IvParameterSpec iv = new IvParameterSpec(initVector);
        SecretKeySpec skeySpec = new SecretKeySpec(key, KEY_ALGORITHM);
        Cipher cipher = Cipher.getInstance(KEY_CIPHER);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
        byte[] originalPlainText = cipher.doFinal(Base64.getDecoder().decode(ciphertext));

        return new String(originalPlainText);
    }

    private void addExistingLoginsToMap() {
        try {
            List<String> encryptedLogins = Files.readAllLines(loginFile.toPath());
            String[] loginList = new String[encryptedLogins.size()];

            for (int i = 0; i < encryptedLogins.size(); i++) {
                loginList[i] = encryptedLogins.get(i);
                String[] loginInfo = loginList[i].split(", ");
                String decryptedLogin = "";

                for (int j = 0; j < loginInfo.length; j++) {
                    if (j == loginInfo.length - 1) {
                        decryptedLogin += decrypt(KEY, KEY_IV, loginInfo[j]);
                    } else {
                        decryptedLogin += decrypt(KEY, KEY_IV, loginInfo[j]) + ", ";
                    }
                }
                String[] decryptedLoginInfo = decryptedLogin.split(", ");
                
                logins.put(i, new UserLogin(decryptedLoginInfo[0], decryptedLoginInfo[1],
                    decryptedLoginInfo[2], decryptedLoginInfo[3]));
            }
        } catch (IOException | IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException
                | NoSuchPaddingException | InvalidAlgorithmParameterException | BadPaddingException e) {
            System.out.println("Error adding existing logins to map: " + e.getMessage());
        }
    }

    private void initalizeMasterHash() {
        try (BufferedReader br = new BufferedReader(new FileReader(masterFile))) {
            masterHash = decrypt(KEY, KEY_IV, br.readLine());
        } catch (IOException | IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException
                | NoSuchPaddingException | InvalidAlgorithmParameterException | BadPaddingException e) {
            System.out.println("Error initializing master hash: " + e.getMessage());
        }
    }

    public void overwriteSaltInFile() {
        saltFile.setWritable(true);
        try {
            SecureRandom random = SecureRandom.getInstance(SALT_HASH_ALGORITHM);
            salt = new byte[8];
            random.nextBytes(salt);

        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error overwriting the salt in file: " + e.getMessage());
        } finally {
            try {
                Files.write(saltFile.toPath(), salt, StandardOpenOption.WRITE);
            } catch (IOException ioe) {
                System.out.println("Error overwriting the salt in file: " + ioe.getMessage());
            }
        }
        saltFile.setWritable(false);
    }

    private void initalizeSalt() {
        saltFile.setWritable(true);

        try {
            if (saltFile.length() == 0) {
                SecureRandom random = SecureRandom.getInstance(SALT_HASH_ALGORITHM);
                salt = new byte[8];
                random.nextBytes(salt);

                try {
                    Files.write(saltFile.toPath(), salt, StandardOpenOption.WRITE);
                } catch (IOException e) {
                    System.out.println("Error initializing salt: " + e.getMessage());
                }
            } else {
                try {
                    salt = Files.readAllBytes(saltFile.toPath());
                } catch (IOException e) {
                    System.out.println("Error initializing salt: " + e.getMessage());
                }
            }
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error initializing salt: " + e.getMessage());
        } finally {
            saltFile.setWritable(false);
        }

    }

    private void initalizeHint() {
        try {
            List<String> hintFileContents = Files.readAllLines(hintFile.toPath());
            if(hintFile.length() != 0){
                hint = decrypt(KEY, KEY_IV, hintFileContents.get(0));
            }
        } catch (IOException | IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException
                | NoSuchPaddingException | InvalidAlgorithmParameterException | BadPaddingException e) {
            System.out.println("Error initializing hint: " + e.getMessage());
        }
    }

    public void overwriteHintInFile(String hint) {
        this.hint = hint;
        hintFile.setWritable(true);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(hintFile))) {
            bw.write(encrypt(KEY, KEY_IV, hint));
        } catch (IOException | IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException
                | NoSuchPaddingException | InvalidAlgorithmParameterException | BadPaddingException e) {
            System.out.println("Error overwriting hint in file: " + e.getMessage());
        } finally {
            hintFile.setWritable(false);
        }
    }

    public void overwriteMasterInFile(String master) {
        masterFile.setWritable(true);
        if (master.isEmpty()) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(masterFile))) {
                bw.write(master);
            } catch (IOException ioe) {
                System.out.println("Error overwriting master in file: " + ioe.getMessage());
            }
        } else {
            masterHash = "";
            //masterHashEncrypted = "";
            try {
                PBEKeySpec keySpec = new PBEKeySpec(master.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
                SecretKeyFactory skf = SecretKeyFactory.getInstance(HASH_ALGORITHM);
                byte[] hashed = skf.generateSecret(keySpec).getEncoded();

                for (byte b : hashed) {
                    masterHash += b;
                }
                masterHash = masterHash.replaceAll("-", "");
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                System.out.println(e.getMessage());
            } finally {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(masterFile))) {
                    masterHashEncrypted = encrypt(KEY, KEY_IV, masterHash);
                    bw.write(masterHashEncrypted);
                } catch (IOException | IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException
                | NoSuchPaddingException | InvalidAlgorithmParameterException | BadPaddingException e) {
                    System.out.println("Error overwriting master in file: " + e.getMessage());
                }
            }
        }
        masterFile.setWritable(false);
    }

    //Gets logins from list and populates the file
    public void updateLoginInFile() {
        loginFile.setWritable(true);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(loginFile))) {
            for (int i = 0; i < logins.size(); i++) {
                bw.write(encrypt(KEY, KEY_IV, logins.get(i).toString()) + "\n");
            }
        } catch (IOException | IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException
                | NoSuchPaddingException | InvalidAlgorithmParameterException | BadPaddingException e) {
            System.out.println("Error updating logins in file: " + e.getMessage());
        } finally {
            loginFile.setWritable(false);
        }
    }

    public void addLoginToFile(int loginIndex) {
        loginFile.setWritable(true);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(loginFile, true))) {
            bw.write(encrypt(KEY, KEY_IV, logins.get(loginIndex).toString()) + "\n");
        } catch (IOException | IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException
                | NoSuchPaddingException | InvalidAlgorithmParameterException | BadPaddingException e) {
            System.out.println("Error writing logins to file: " + e.getMessage());
        } finally {
            loginFile.setWritable(false);
        }
    }

    public int getFileRows() {
        int numRows = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(loginFile))) {
            while ((br.readLine()) != null) {
                numRows++;
            }
        } catch (IOException ioe) {
            System.out.println("Error in retrieving file rows: " + ioe.getMessage());
        }
        return numRows;
    }

    public Map<Integer, UserLogin> getLogins() {
        return logins;
    }

    public String getMasterHash() {
        return masterHash;
    }

    public String getHashAlgorithm() {
        return HASH_ALGORITHM;
    }

    public int getKeyLength() {
        return KEY_LENGTH;
    }

    public byte[] getSalt() {
        return salt;
    }

    public int getIterations() {
        return ITERATIONS;
    }

    public String getHint() {
        return hint;
    }
    
}