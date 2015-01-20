sercurity

overall

http://stackoverflow.com/questions/549/the-definitive-guide-to-form-based-website-authentication

PART I: How To Log In

3. The only (currently practical) way to protect against login interception (packet sniffing) during login is by using a certificate-based encryption scheme (for example, SSL) or a proven & tested challenge-response scheme (for example, the Diffie-Hellman-based SRP). Any other method can be easily circumvented by an eavesdropping attacker. On that note: hashing the password client-side (for example, with JavaScript) is often useless (and can be a security flaw) unless it is combined with one of the above - that is, either securing the line with strong encryption or using a tried-and-tested challenge-response mechanism (if you don't know what that is, just know that it is one of the most difficult to prove, most difficult to design, and most difficult to implement concepts in digital security). Hashing the password is effective against password disclosure, but not against replay attacks, Man-In-The-Middle attacks / hijackings, or brute-force attacks (since we are handing the attacker both username, salt and hashed password).

4. After sending the authentication tokens, the system needs a way to remember that you have been authenticated - this fact should only ever be stored serverside in the session data. A cookie can be used to reference the session data. Wherever possible, the cookie should have the secure and HTTP Only flags set when sent to the browser. The httponly flag provides some protection against the cookie being read by a XSS attack. The secure flag ensures that the cookie is only sent back via HTTPS, and therefore protects against network sniffing attacks. The value of the cookie should not be predictable. Where a cookie referencing a non-existent session is presented, its value should be replaced immediately to prevent session fixation.

PART II: How To Remain Logged In - The Infamous "Remember Me" Checkbox

If you DO decide to implement persistent login cookies, this is how you do it:
...

PART IV: Forgotten Password Functionality

2. Always hash the lost password code/token in the database. AGAIN, this code is another example of a Password Equivalent, so it MUST be hashed in case an attacker got his hands on your database. When a lost password code is requested, send the plaintext code to the user's email address, then hash it, save the hash in your database -- and throw away the original. Just like a password or a persistent login token.

PART V: Checking Password Strength

PART VI: Much More - Or: Preventing Rapid-Fire Login Attempts

It would, however, take an inordinate amount of time to crack even a 6-character password, if you were limited to one attempt per second!

介绍了如何防御Dos攻击

Best practice #3: Combining the two approaches - either a fixed, short time delay that goes into effect after N failed attempts, like: ...

PART VII: Distributed Brute Force Attacks

Say your site has had an average of 120 bad logins per day over the past 3 months. Using that (running average), your system might set the global limit to 3 times that -- ie. 360 failed attempts over a 24 hour period. Then, if the total number of failed attempts across all accounts exceeds that number within one day (or even better, monitor the rate of acceleration and trigger on a calculated treshold), it activates system-wide login throttling - meaning short delays for ALL users (still, with the exception of cookie logins and/or backup CAPTCHA logins).

I also posted a question with more details and a really good discussion of how to avoid tricky pitfals in fending off distributed brute force attacks

PART VIII: Two-Factor Authentication and Authentication Providers

手机 OAuth etc

文后必读链接 略

https://www.owasp.org/index.php/Authentication_Cheat_Sheet

只是一个设想
http://www.darkreading.com/risk/the-future-of-web-authentication/d/d-id/1139772?


Secure a Web application, Java-style
http://www.javaworld.com/article/2076292/core-java/secure-a-web-application--java-style.html

j2ee6 Securing Web Applications
http://docs.oracle.com/javaee/6/tutorial/doc/gkbaa.html
一个图片
http://docs.oracle.com/javaee/6/tutorial/doc/figures/security-formBasedLogin.gif

using session
http://www.avajava.com/tutorials/lessons/how-do-i-use-form-authentication-with-tomcat.html

http://www.mkyong.com/webservices/jax-ws/application-authentication-with-jax-ws/
http://docs.oracle.com/cd/E24329_01/web.1211/e24983/secure.htm#RESTF256







http://crypto.stackexchange.com/questions/2663/what-is-the-time-complexity-of-the-rc4-encryption-decryption-algorithms
http://zhidao.baidu.com/question/7745006.html
http://blog.csdn.net/kamaliang/article/details/6690979
http://en.wikipedia.org/wiki/Advanced_Encryption_Standard
http://www.open-open.com/lib/view/open1403354185778.html

http://en.wikipedia.org/wiki/Stream_cipher
http://en.wikipedia.org/wiki/Tiny_Encryption_Algorithm
http://en.wikipedia.org/wiki/XTEA
http://en.wikipedia.org/wiki/RC4