package proCollab.projectManagement.capstoneProject.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import proCollab.projectManagement.capstoneProject.model.Task;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendVerificationEmail(String to, String verificationLink) {
        String emailSubject = "Verify Your Email - ProCollab";
        String emailContent = "<html><body>" +
                "<h2>Welcome to ProCollab!</h2>" +
                "<p>Dear User,</p>" +
                "<p>Thank you for signing up with ProCollab. To activate your account, please verify your email address by clicking on the link below:</p>"
                +
                "<p><a href=\"" + verificationLink + "\">Verify Email</a></p>" +
                "<p>Please note that this link will expire in 24 hours.</p>" +
                "<p>If you have any questions or need assistance, please contact us at <a href=\"mailto:support@procollab.com\">support@procollab.com</a>.</p>"
                +
                "<p>Best regards,</p>" +
                "<p>The ProCollab Team</p>" +
                "</body></html>";

        sendHtmlEmail(to, emailSubject, emailContent);
    }

    private void sendHtmlEmail(String to, String subject, String content) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true); // Set content as HTML
            javaMailSender.send(message);
        } catch (MessagingException e) {
            // Handle exception
        }
    }

    public void sendTaskMail(String to, Task task) {
        String emailSubject = "New Task Assigned - Pro-Collab: " + task.getName();
        String emailContent = "<html><body>" +
                "<h2>Hello,</h2>" +
                "<p>You have been assigned a new task:</p>" +
                "<h3>Task Details:</h3>" +
                "<ul>" +
                "<li><strong>Name:</strong> " + task.getName() + "</li>" +
                "<li><strong>Description:</strong> " + task.getDescription() + "</li>" +
                "<li><strong>Date:</strong> " + task.getDate() + "</li>" +
                "<li><strong>Assigned By:</strong> " + task.getCreatorName() + "</li>" +
                "</ul>" +
                "<p>Please log in to your ProCollab account to view the task.</p>" +
                "<p><a href=\"http://localhost:1111/login\">Login to ProCollab</a></p>" +
                "<p>If you have any questions or need assistance, please contact your project manager.</p>" +
                "<p>Best regards,</p>" +
                "<p>The ProCollab Team</p>" +
                "</body></html>";

        sendHtmlEmail(to, emailSubject, emailContent);
    }

    // Send an email
    public void sendEmail(String to, String subject, String text) {
        sendHtmlEmail(to, subject, text);
    }

    public void sendInviteMail(String email,String password){
        String emailSubject = "Welocome to ProCollab";
        String emailContent = "<html><body>" +
                "<h2>Welcome to ProCollab!</h2>" +
                "<p>Dear User,</p>" +
                "<p>You've been added to ProCollab. You can access the app with the below credentials:</p>"
                +
                "<p>Username:\""+email+"\"</p>" +
                "<p>Password:\""+password+"\"</p>" +
                "<p>If you have any questions or need assistance, please contact us at <a href=\"mailto:support@procollab.com\">support@procollab.com</a>.</p>"
                +
                "<p>Best regards,</p>" +
                "<p>The ProCollab Team</p>" +
                "</body></html>";

        sendHtmlEmail(email, emailSubject, emailContent);
    }
}
