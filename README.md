# JAMPasswordWallet

JAM Password Wallet is a fully functional locally kept java password manager that maintains integrity of user logins, master password, master password salt, and master password hint. Features include a user-friendly GUI, AES symmetric key encryption (AES256), login indexing, NIST compliant hashing techniques (PBKDF2WithHmacSHA256), and MVC design. 

The master password by default is "master" without quotes.

Certain interactions with the program, such as accessing logins, and failed/successful password attempts, generate xml entries in a wallet.log file. 

The program also features a time-based automatic prompt followed by logout.
