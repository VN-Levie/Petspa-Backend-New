package vn.aptech.petspa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendOtpMail(String to, String subject, String otp, String link) {
        String message = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8" />
                  <title>OTP Verification</title>
                  <style>
                    body {
                      margin: 0;
                      padding: 0;
                      font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
                      color: #333;
                      background-color: #fff;
                    }

                    .container {
                      margin: 0 auto;
                      width: 100%%;
                      max-width: 600px;
                      padding: 20px;
                      border-radius: 5px;
                      line-height: 1.8;
                      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                    }

                    .header {
                      border-bottom: 1px solid #eee;
                      padding-bottom: 10px;
                      text-align: center;
                    }

                    .header a {
                      font-size: 1.5em;
                      color: #4CAF50;
                      text-decoration: none;
                      font-weight: bold;
                    }

                    .otp {
                      background: linear-gradient(to right, #00bc69 0, #00bc88 50%%, #00bca8 100%%);
                      color: white;
                      font-weight: bold;
                      text-align: center;
                      display: inline-block;
                      padding: 10px 20px;
                      border-radius: 4px;
                      font-size: 1.2em;
                      margin: 20px 0;
                    }

                    .verification-link {
                      display: inline-block;
                      margin: 20px 0;
                      padding: 10px 20px;
                      background-color: #2196F3;
                      color: white;
                      text-decoration: none;
                      border-radius: 4px;
                      font-weight: bold;
                      font-size: 1em;
                    }

                    .footer {
                      margin-top: 30px;
                      color: #aaa;
                      font-size: 0.8em;
                      text-align: center;
                    }

                    .footer a {
                      color: #2196F3;
                      text-decoration: none;
                    }
                  </style>
                </head>

                <body>
                  <div class="container">
                    <div class="header">
                      <a href="#">🐾 Welcome to Pet Spa! 🐾</a>
                    </div>
                    <p>We are so excited to have you and your furry friend join our community!</p>
                    <p>To keep your account safe and secure, please use the following <strong>One-Time Password (OTP)</strong> to verify your identity:</p>
                    <div class="otp">%s</div>
                    <p>
                         If you have trouble using the OTP, don’t worry! You can verify your account by clicking the button below:
                    </p>
                    <a class="verification-link" href="%s">Verify My Account</a>
                    <p style="font-size: 0.9em;">
                      Please note that this OTP is valid for only 15 minutes.
                    </p>
                    <hr>
                    <p style="margin-top: 20px;">
                        DO NOT forward this email or share <strong>YOUR OTP</strong> with anyone.
                    </p>
                    <p style="margin-top: 20px; color: #666;">
                        If you didn’t request this, just ignore this email. Your furry friends are safe with us! 🐶🐱
                    </p>
                    <hr>
                    <div class="footer">
                      <p>This email was sent to <a href="mailto:%s">%s</a>. This email can't receive replies.</p>
                      <p>
                        For more information, visit our website at
                        <a href="#">Pet Spa</a>.
                      </p>
                      <p>&copy; 2025 Pet Spa. Making tails wag, one spa day at a time!</p>
                    </div>
                  </div>
                </body>
                </html>
                """
                .formatted(otp, link, to, to);

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom("aptech.petspa@zohomail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(message, true); // Enable HTML content

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
